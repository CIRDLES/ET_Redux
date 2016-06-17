/*
 * WashStateElementIISingleCollFileHandler
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
import java.util.ArrayList;
import java.util.Arrays;
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
public class WashStateElementIISingleCollFileHandler extends AbstractRawDataFileHandler {

    // Class variables
    private static final long serialVersionUID = 1472900943557830775L;
    private static WashStateElementIISingleCollFileHandler instance = new WashStateElementIISingleCollFileHandler();
    private File[] analysisFiles;
    private String[] fractionFileNames;

    /**
     *
     * @param massSpec
     * @param rawDataFileTemplate
     */
    private WashStateElementIISingleCollFileHandler() {

        super();

        NAME = "Washington State Element II SC Folder";

        aboutInfo = "Details: This is the default protocol for Washington State University's Thermo Finnigan Element II.";
    }

    /**
     *
     * @return
     */
    public static WashStateElementIISingleCollFileHandler getInstance() {
        return instance;
    }

    /**
     *
     * @param tripoliRawDataFolder
     * @return
     */
    @Override
    public File validateAndGetHeaderDataFromRawIntensityFile(File tripoliRawDataFolder) {
        String dialogTitle = "Select an Element II Raw Data Folder:";

        rawDataFile = FileHelper.AllPlatformGetFolder(dialogTitle, tripoliRawDataFolder);

        return rawDataFile;
    }

    /**
     *
     * @param loadDataTask the value of loadRawDataPropertyChangeListener
     * @param usingFullPropagation the value of usingFullPropagation
     * @param leftShadeCount the value of leftShadeCount
     * @param ignoreFirstFractions the value of ignoreFirstFractions
     * @param inLiveMode the value of inLiveMode
     */
    @Override
    public void getAndLoadRawIntensityDataFile(SwingWorker loadDataTask, boolean usingFullPropagation, int leftShadeCount, int ignoreFirstFractions, boolean inLiveMode) {

        // get .txt files from the folder and check the first one        
        analysisFiles = rawDataFile.listFiles((File f)
                -> ((f.getName().toLowerCase().endsWith(".txt"))
                && (!f.getName().toLowerCase().endsWith("_b.txt"))));

        if (analysisFiles.length > 0) {

            Arrays.sort(analysisFiles, new FractionFileModifiedComparator());

            String onPeakFileContents = URIHelper.getTextFromURI(analysisFiles[0].getAbsolutePath());
            if (isValidRawDataFileType(analysisFiles[0]) //
                    && //
                    areKeyWordsPresent(onPeakFileContents)) {

                // open and process "samplelist.txt" file that has a fraction name for each file in order
                File[] fileWithFractionFileNames = rawDataFile.listFiles((File f) -> {
                    return f.getName().toLowerCase().equalsIgnoreCase("samplelist.txt");
                });

                if (fileWithFractionFileNames.length == 0) {
                    new ETWarningDialog("Missing 'samplelist.txt' file listing the files, so quitting load process.").setVisible(true);
                    loadDataTask.cancel(true);
                } else {
                    // read the first (and assumedly only) samplelist file in the folder
                    int ignoredLineCount = 0;
                    List<String> fractionData = null;
                    try {
                        fractionData = Files.readLines(fileWithFractionFileNames[0], Charsets.ISO_8859_1);
                        // skip data in rows 0 to ignoredLineCount - 1
                        fractionFileNames = new String[fractionData.size() - ignoredLineCount];
                        for (int i = ignoredLineCount; i < fractionData.size(); i++) {
                            String lineContents = fractionData.get(i);
                            fractionFileNames[i - ignoredLineCount] = lineContents.trim();
                        }
                    } catch (IOException iOException) {
                    }
                }
                // create fractions from raw data and perform corrections and calculate ratios
                loadRawDataFile(loadDataTask, usingFullPropagation, leftShadeCount, 0, inLiveMode);
            }
        } else {
            JOptionPane.showMessageDialog(
                    null,
                    new String[]{"Selected raw data folder does not contain valid files."},
                    "ET Redux Warning",
                    JOptionPane.WARNING_MESSAGE);

            rawDataFile = null;
        }
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
     * @param inLiveMode the value of inLiveMode
     * @return the java.util.SortedSet<org.earthtime.Tripoli.fractions.TripoliFraction>
     */
    @Override
    protected SortedSet<TripoliFraction> loadRawDataFile(SwingWorker loadDataTask, boolean usingFullPropagation, int leftShadeCount, int ignoreFirstFractions, boolean inLiveMode) {
        tripoliFractions = new TreeSet<>();

        // assume we are golden        
        for (int f = 0; f < analysisFiles.length; f++) {

            if (loadDataTask.isCancelled()) {
                break;
            }
            loadDataTask.firePropertyChange("progress", 0, ((100 * f) / analysisFiles.length));

            // assume files are written in numerical name order
            String fractionID = fractionFileNames[f];
            long fractionPeakTimeStamp = analysisFiles[f].lastModified();

            String onPeakFileContents = URIHelper.getTextFromURI(analysisFiles[f].getAbsolutePath());
            String[] onPeakFileRows = onPeakFileContents.split("\n");

            // check for background file
            File backgroundFile = new File(analysisFiles[f].getAbsolutePath().replace(".TXT", "_b.TXT"));
            System.out.println("Background exists = " + backgroundFile.exists());
            if (backgroundFile.exists()) {
                String backgroundFileContents = URIHelper.getTextFromURI(backgroundFile.getAbsolutePath());
                String[] backgroundFileRows = backgroundFileContents.split("\n");
                long fractionBackgroundTimeStamp = backgroundFile.lastModified();

                // note each row has relative time stamp which we are hiding for now by using frequency
                int expectedRowsOfData = rawDataFileTemplate.getBlockSize();
                String[][] scanData
                        = new String[expectedRowsOfData][massSpec.getVIRTUAL_COLLECTOR_COUNT()];

                System.out.println("Fract # " + f + "   named  " + analysisFiles[f].getName() + "  row count = " + onPeakFileRows.length);
                //TODO possible missing condition here if file lengths vary from template spec and onPeakFileRows is too big

                ArrayList<double[]> backgroundAcquisitions = new ArrayList<>();
                ArrayList<double[]> peakAcquisitions = new ArrayList<>();

                for (int i = 0; i < rawDataFileTemplate.getBlockSize(); i++) {

                    String[] onPeakCollectorsColumns = new String[]{"0", "0", "0", "0", "0", "0", "0", "0", "0", "0",};
                    String[] backgroundCollectorsColumns = new String[]{"0", "0", "0", "0", "0", "0", "0", "0", "0", "0",};
                    // handle case where there is not as many lines of data as expected
                    if (onPeakFileRows.length > (i + rawDataFileTemplate.getBlockStartOffset())) {
                        onPeakCollectorsColumns
                                = onPeakFileRows[i + rawDataFileTemplate.getBlockStartOffset()].split("\t");
                    }

                    // handle case where there is not as many lines of data as expected
                    if (backgroundFileRows.length > (i + rawDataFileTemplate.getBlockStartOffset())) {
                        backgroundCollectorsColumns
                                = backgroundFileRows[i + rawDataFileTemplate.getBlockStartOffset()].split("\t");
                    }

                    // background
                    double[] backgroundIntensities = new double[8];
                    backgroundAcquisitions.add(backgroundIntensities);
                    for (int j = 1; j < 9; j++) {
                        scanData[i][j - 1] = backgroundCollectorsColumns[j].trim(); // ignore timestamp
                        backgroundIntensities[j - 1] = Double.parseDouble(backgroundCollectorsColumns[j].trim());
                    }
                    // onpeak
                    double[] peakIntensities = new double[8];
                    peakAcquisitions.add(peakIntensities);
                    for (int j = 1; j < 9; j++) {
                        scanData[i][8 + j - 1] = onPeakCollectorsColumns[j].trim(); // ignore timestamp
                        peakIntensities[j - 1] = Double.parseDouble(onPeakCollectorsColumns[j].trim());
                    }
                }

                // extract isStandard
                boolean isStandard = fractionID.equalsIgnoreCase(fractionFileNames[0]);

                // massage file name
                if (fractionID.toLowerCase().startsWith("unknown")) {
                    fractionID = fractionID.toLowerCase().replace("unknown", "unknown-");
                } else {
                    fractionID = fractionID + "-" + analysisFiles[f].getName().replace(".TXT", "").replace(".txt", "");
                }

                TripoliFraction tripoliFraction
                        = new TripoliFraction( //
                                fractionID, //
                                massSpec.getCommonLeadCorrectionHighestLevel(), //
                                isStandard,
                                fractionBackgroundTimeStamp, //
                                fractionPeakTimeStamp,
                                peakAcquisitions.size());

                SortedSet<DataModelInterface> rawRatios = massSpec.rawRatiosFactoryRevised();
                tripoliFraction.setRawRatios(rawRatios);

                massSpec.setCountOfAcquisitions(peakAcquisitions.size());

                // establish map of virtual collectors to field indexes
                Map<DataModelInterface, Integer> virtualCollectorModelMapToFieldIndexes = new HashMap<>();
                virtualCollectorModelMapToFieldIndexes.put(massSpec.getHg202(), 0);
                virtualCollectorModelMapToFieldIndexes.put(massSpec.getPb204(), 1);
                virtualCollectorModelMapToFieldIndexes.put(massSpec.getPb206(), 2);
                virtualCollectorModelMapToFieldIndexes.put(massSpec.getPb207(), 3);
                virtualCollectorModelMapToFieldIndexes.put(massSpec.getPb208(), 4);
                virtualCollectorModelMapToFieldIndexes.put(massSpec.getTh232(), 5);
                virtualCollectorModelMapToFieldIndexes.put(massSpec.getU235(), 6);
                virtualCollectorModelMapToFieldIndexes.put(massSpec.getU238(), 7);

                massSpec.processFractionRawRatiosII(//
                        backgroundAcquisitions, peakAcquisitions, usingFullPropagation, tripoliFraction, virtualCollectorModelMapToFieldIndexes);

                tripoliFraction.shadeDataActiveMapLeft(leftShadeCount);
                tripoliFractions.add(tripoliFraction);
            }
        }

        return tripoliFractions;
    }

//    private void readObject(
//            ObjectInputStream stream)
//            throws IOException, ClassNotFoundException {
//        stream.defaultReadObject();
//        ObjectStreamClass myObject = ObjectStreamClass.lookup(
//                Class.forName(WashStateElementIISingleCollFileHandler.class.getCanonicalName()));
//        long theSUID = myObject.getSerialVersionUID();
//        System.out.println("Customized De-serialization of WashStateElementIISingleCollFileHandler " + theSUID);
//    }
}
