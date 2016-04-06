/*
 * UnivKansasElementIIFileHandler
 *
 * Copyright 2006-2016 James F. Bowring and www.Earth-Time.org
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.earthtime.Tripoli.rawDataFiles.handlers.Thermo;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.earthtime.Tripoli.dataModels.DataModelInterface;
import org.earthtime.Tripoli.fractions.TripoliFraction;
import org.earthtime.Tripoli.rawDataFiles.handlers.AbstractRawDataFileHandler;
import org.earthtime.archivingTools.URIHelper;
import org.earthtime.exceptions.ETWarningDialog;
import org.earthtime.utilities.FileHelper;

/**
 *
 * @author James F. Bowring
 */
public class UnivKansasElementIIFileHandler extends AbstractRawDataFileHandler {

    // Class variables
    private static final long serialVersionUID = 1676980296251048119L;
    private static UnivKansasElementIIFileHandler instance = new UnivKansasElementIIFileHandler();
    // instance variables
    private File[] analysisFiles;
    private String[] fractionFileNames;

    /**
     *
     * @param massSpec
     * @param rawDataFileTemplate
     */
    private UnivKansasElementIIFileHandler() {

        super();
        NAME = "Univ Kansas Element II Folder";
        aboutInfo = "Details: This is the Feb 2016 protocol for an ElementII.";
    }

    /**
     *
     * @return
     */
    public static UnivKansasElementIIFileHandler getInstance() {
        return instance;
    }

    /**
     *
     * @param tripoliRawDataFolder
     * @return
     */
    @Override
    public File validateAndGetHeaderDataFromRawIntensityFile(File tripoliRawDataFolder) {
        String dialogTitle = "Select a Univ Kansas Element II Raw Data Folder:";

        rawDataFile = FileHelper.AllPlatformGetFolder(dialogTitle, tripoliRawDataFolder);
        return rawDataFile;
    }

    /**
     *
     * @param loadDataTask the value of loadRawDataTaskListener
     * @param usingFullPropagation the value of usingFullPropagation
     * @param leftShadeCount the value of leftShadeCount
     * @param ignoreFirstFractions the value of ignoreFirstFract
     */
    @Override
    public void getAndLoadRawIntensityDataFile(SwingWorker loadDataTask, boolean usingFullPropagation, int leftShadeCount, int ignoreFirstFractions) {

        // ElementII has folder of .FIN2 files plus INF DAT and TXT files for each acquisition
        // also there is a FIN file containing naming and ordering of samples
        analysisFiles = rawDataFile.listFiles((File dir, String name) -> {
            return name.toLowerCase().endsWith(".fin2");
        });

        if (analysisFiles.length > 0) {
            String onPeakFileContents = URIHelper.getTextFromURI(analysisFiles[0].getAbsolutePath());
            if (isValidRawDataFileType(analysisFiles[0]) //
                    && //
                    areKeyWordsPresent(onPeakFileContents)) {

                // open and process ".FIN" file that has a fraction name for each file
                File[] fileWithFractionFileNames = rawDataFile.listFiles((File dir, String name) -> {
                    return name.toLowerCase().endsWith(".fin");
                });

                if (fileWithFractionFileNames.length == 0) {
                    new ETWarningDialog("Missing '.FIN' file listing the files, so quitting load process.").setVisible(true);
                    loadDataTask.cancel(true);
                } else {
                    // read the first (and assumedly only) scancsv file in the folder
                    int ignoredLineCount = 11;
                    List<String> fractionData = null;
                    try {
                        fractionData = Files.readLines(fileWithFractionFileNames[0], Charsets.ISO_8859_1);
                        // skip data in rows 0 - 10
                        fractionFileNames = new String[fractionData.size() - ignoredLineCount];
                        for (int i = ignoredLineCount; i < fractionData.size(); i++) {
                            String lineContents = fractionData.get(i);
                            fractionFileNames[i - ignoredLineCount] = lineContents.trim();
                        }
                    } catch (IOException iOException) {
                    }
                }

                // create fractions from raw data and perform corrections and calculate ratios
                tripoliFractions = loadRawDataFile(loadDataTask, usingFullPropagation, leftShadeCount, 0);
            }
        } else {
            JOptionPane.showMessageDialog(
                    null,
                    new String[]{"Selected raw data folder does not contain valid files."},
                    "ET Redux Warning",
                    JOptionPane.WARNING_MESSAGE);

            rawDataFile = null;
        }

//        return rawDataFile;
    }

    /**
     *
     * @return
     */
    @Override
    protected boolean areKeyWordsPresent(String fileContents) {
        boolean retVal = true;

        retVal = retVal && fileContents.contains(getRawDataFileTemplate().getStartOfFirstLine().trim());

        return retVal;
    }

    /**
     *
     * @param fractionID
     * @return
     */
    @Override
    public boolean isStandardFractionID(String fractionID) {
        boolean retVal = false;
        for (String standardID : getRawDataFileTemplate().getStandardIDs()) {
            retVal = retVal || fractionID.toUpperCase().contains(standardID.toUpperCase());
        }

        return retVal;
    }

    /**
     *
     *
     * @param loadDataTask the value of loadDataTask
     * @param usingFullPropagation the value of usingFullPropagation
     * @param leftShadeCount the value of leftShadeCount
     * @param ignoreFirstFractions the value of ignoreFirstFractions
     * @return
     */
    @Override
    protected SortedSet<TripoliFraction> loadRawDataFile(//
            SwingWorker loadDataTask, boolean usingFullPropagation, int leftShadeCount, int ignoreFirstFractions) {

        SortedSet myTripoliFractions = new TreeSet<>();
 
        // assume we are golden   
        // take first entry in fractionFileNames that came from .FIN file and ?? confirm it is referenceMaterial (standard)
        String referenceMaterialfractionIDPrefix = fractionFileNames[0].toUpperCase().substring(0, 2);

        for (int f = 0; f < analysisFiles.length; f++) {

            if (loadDataTask.isCancelled()) {
                break;
            }
            loadDataTask.firePropertyChange("progress", 0, ((100 * f) / analysisFiles.length));
            String fractionID = analysisFiles[f].getName().toUpperCase().replace(".FIN2", "");

            // needs to be more robust
            boolean isReferenceMaterial = (fractionID.substring(0, 2).compareToIgnoreCase(referenceMaterialfractionIDPrefix) == 0);

            // get file contents
            String fractionFileContents = URIHelper.getTextFromURI(analysisFiles[f].getAbsolutePath());
            String[] fractionFileRows = fractionFileContents.split("\n");

            // first get time stamp for file in row 2
            // form = Friday, February 06,2015 16:57:54
            String timeStampFromRow2[] = fractionFileRows[1].split(",");

            String fractionDate
                    = timeStampFromRow2[1].trim() + " " // month day,
                    + timeStampFromRow2[2].trim() // year HH:mm:ss
                    ;

            // Get the default MEDIUM/SHORT DateFormat
            SimpleDateFormat fractionTimeFormat = new SimpleDateFormat();
            fractionTimeFormat.applyPattern("MMMMM dd yyyy HH:mm:ss");

            // Parse the fractionDateValue
            Date fractionDateValue;
            try {
                fractionDateValue = fractionTimeFormat.parse(fractionDate);

                // each acquisition file contains background followed by peak follwed by background
                // initial soultion is to hard wire the first background and peak
                // later we will give user interactive tools to pick them out
                ArrayList<double[]> backgroundAcquisitions = new ArrayList<>();
                ArrayList<double[]> peakAcquisitions = new ArrayList<>();

                int hardwiredEndOfBackground = 95;//75;//105;
                int assumedBackgrounRowCount = hardwiredEndOfBackground - rawDataFileTemplate.getBlockStartOffset();
                long fractionBackgroundTimeStamp = fractionDateValue.getTime();
                long fractionPeakTimeStamp = fractionDateValue.getTime() + assumedBackgrounRowCount * massSpec.getCOLLECTOR_DATA_FREQUENCY_MILLISECS();

                for (int i = rawDataFileTemplate.getBlockStartOffset(); i < rawDataFileTemplate.getBlockSize(); i++) {
                    String[] fractionCollectorsColumns = fractionFileRows[i].split(",");

                    // Time [Sec]b206	Pb207	Pb208	Th232	U238
                    // hard coded for now 2015
                    if (i <= hardwiredEndOfBackground) {
                        double[] backgroundIntensities = new double[5];
                        backgroundAcquisitions.add(backgroundIntensities);
                        backgroundIntensities[0] = Double.parseDouble(fractionCollectorsColumns[1]);
                        backgroundIntensities[1] = Double.parseDouble(fractionCollectorsColumns[2]);
                        backgroundIntensities[2] = Double.parseDouble(fractionCollectorsColumns[3]);
                        backgroundIntensities[3] = Double.parseDouble(fractionCollectorsColumns[4]);
                        backgroundIntensities[4] = Double.parseDouble(fractionCollectorsColumns[5]);
                    } else if (i > (hardwiredEndOfBackground + 5)) {
                        double[] peakIntensities = new double[5];
                        peakAcquisitions.add(peakIntensities);
                        peakIntensities[0] = Double.parseDouble(fractionCollectorsColumns[1]);
                        peakIntensities[1] = Double.parseDouble(fractionCollectorsColumns[2]);
                        peakIntensities[2] = Double.parseDouble(fractionCollectorsColumns[3]);
                        peakIntensities[3] = Double.parseDouble(fractionCollectorsColumns[4]);
                        peakIntensities[4] = Double.parseDouble(fractionCollectorsColumns[5]);
                    }
                }  // i loop

                TripoliFraction tripoliFraction
                        = //                           
                        new TripoliFraction( //
                                fractionID, //
                                massSpec.getCommonLeadCorrectionHighestLevel(), //
                                isReferenceMaterial,
                                fractionBackgroundTimeStamp, //
                                fractionPeakTimeStamp,
                                peakAcquisitions.size());

                SortedSet<DataModelInterface> rawRatios = massSpec.rawRatiosFactoryRevised();
                tripoliFraction.setRawRatios(rawRatios);

                massSpec.setCountOfAcquisitions(peakAcquisitions.size());

                // establish map of virtual collectors to field indexes
                Map<DataModelInterface, Integer> virtualCollectorModelMapToFieldIndexes = new HashMap<>();
                virtualCollectorModelMapToFieldIndexes.put(massSpec.getPb206(), 0);
                virtualCollectorModelMapToFieldIndexes.put(massSpec.getPb207(), 1);
                virtualCollectorModelMapToFieldIndexes.put(massSpec.getPb208(), 2);
                virtualCollectorModelMapToFieldIndexes.put(massSpec.getTh232(), 3);
                virtualCollectorModelMapToFieldIndexes.put(massSpec.getU238(), 4);

                massSpec.processFractionRawRatiosII(//
                        backgroundAcquisitions, peakAcquisitions, usingFullPropagation, tripoliFraction, virtualCollectorModelMapToFieldIndexes);

                tripoliFraction.shadeDataActiveMapLeft(leftShadeCount);
                System.out.println("\n**** Element II FractionID  " + fractionID + "  " + fractionDateValue.toString() + "  is std: " + tripoliFraction.isStandard() + " >>>>>>>>>>>>>>>>><<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");

                myTripoliFractions.add(tripoliFraction);

            } catch (ParseException parseException) {
                // TODO: drop out here
            }
        }

        if (myTripoliFractions.isEmpty()) {
            myTripoliFractions = null;
        }

        return myTripoliFractions;
    }
}
