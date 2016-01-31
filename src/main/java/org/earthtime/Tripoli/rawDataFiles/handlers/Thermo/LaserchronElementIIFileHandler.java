/*
 * LaserchronElementIIFileHandler
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
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.earthtime.Tripoli.dataModels.DataModelInterface;
import org.earthtime.Tripoli.fractions.TripoliFraction;
import org.earthtime.Tripoli.massSpecSetups.singleCollector.ThermoFinnigan.LaserchronElementIISetupUPb;
import org.earthtime.Tripoli.rawDataFiles.handlers.AbstractRawDataFileHandler;
import org.earthtime.archivingTools.URIHelper;
import org.earthtime.pythonUtilities.ElementII_DatFileConverter;
import org.earthtime.utilities.FileHelper;
import org.python.core.PyException;

/**
 *
 * @author James F. Bowring
 */
public class LaserchronElementIIFileHandler extends AbstractRawDataFileHandler {

    // Class variables
    private static final long serialVersionUID = -2860923405769819758L;
    private static LaserchronElementIIFileHandler instance = new LaserchronElementIIFileHandler();
    // Instance variables
    private File[] analysisFiles;
    private String[] fractionNames;

    /**
     *
     * @param massSpec
     * @param rawDataFileTemplate
     */
    private LaserchronElementIIFileHandler() {

        super();
        NAME = "Laserchron Element II Folder of '.dat' files";
        aboutInfo = "Details: This is the Laserchron multi-file protocol for an ElementII. "
                + " Please include the '.scancsv' file in the folder containing the '.dat' files.";

        analysisFiles = new File[0];
        fractionNames = new String[0];
    }

    /**
     *
     * @return
     */
    public static LaserchronElementIIFileHandler getInstance() {
        return instance;
    }

    /**
     *
     * @param tripoliRawDataFolder
     * @return
     */
    @Override
    public File validateAndGetHeaderDataFromRawIntensityFile(File tripoliRawDataFolder) {
        String dialogTitle = "Select a Laserchron Element II Raw Data Folder:";

        rawDataFile = FileHelper.AllPlatformGetFolder(dialogTitle, tripoliRawDataFolder);
        return rawDataFile;
    }

    /**
     *
     * @param loadDataTask the value of loadRawDataTaskListener
     * @param usingFullPropagation the value of usingFullPropagation
     * @param leftShadeCount the value of leftShadeCount
     * @param ignoreFirstFractions the value of ignoreFirstFracts
     * @return ions
     */
    @Override
    public File getAndLoadRawIntensityDataFile(SwingWorker loadDataTask, boolean usingFullPropagation, int leftShadeCount, int ignoreFirstFractions) {

        // ElementII has folder of .dat files 
        analysisFiles = rawDataFile.listFiles((File dir, String name) -> {
            return name.toLowerCase().endsWith(".dat");
        });

        Arrays.sort(analysisFiles, new FractionFileNameNameComparator());

        if (analysisFiles.length > 0) {
            String onPeakFileContents = URIHelper.getTextFromURI(analysisFiles[0].getAbsolutePath()).substring(0, 32);
            if (isValidRawDataFileType(analysisFiles[0]) //
                    && //
                    areKeyWordsPresent(onPeakFileContents)) {

                // open and process ".scancsv" file that has a fraction name for each file
                File[] scancsvFiles = rawDataFile.listFiles((File dir, String name) -> {
                    return name.toLowerCase().endsWith(".scancsv");
                });

                if (scancsvFiles.length == 0) {
                    fractionNames = new String[0];
                } else {
                    // read the first (and assumedly only) scancsv file in the folder
                    List<String> fractionData = null;
                    try {
                        fractionData = Files.readLines(scancsvFiles[0], Charsets.ISO_8859_1);
                        // skip column names in row 0
                        fractionNames = new String[fractionData.size() - 1];
                        for (int i = 1; i < fractionData.size(); i++) {
                            String[] lineContents = fractionData.get(i).replace("\"", "").split(",");
                            fractionNames[i - 1] = lineContents[1];
                        }
                    } catch (IOException iOException) {
                    }
                }

                // create fractions from raw data and perform corrections and calculate ratios
                tripoliFractions = loadRawDataFile(loadDataTask, usingFullPropagation, leftShadeCount, ignoreFirstFractions);
            }
        } else {
            JOptionPane.showMessageDialog(
                    null,
                    new String[]{"Selected raw data folder does not contain valid files."},
                    "ET Redux Warning",
                    JOptionPane.WARNING_MESSAGE);
        }

        return rawDataFile;
    }

    /**
     *
     * @return
     */
    @Override
    protected boolean areKeyWordsPresent(String fileContents
    ) {
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
    public boolean isStandardFractionID(String fractionID
    ) {
        boolean retVal = false;
        for (String standardID : getRawDataFileTemplate().getStandardIDs()) {
            retVal = retVal || fractionID.toUpperCase(Locale.US).contains(standardID.toUpperCase(Locale.US));
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
        int standardIncrementer = 1;

        // assume we are golden   
        for (int f = ignoreFirstFractions; f < analysisFiles.length; f++) {

            if (loadDataTask.isCancelled()) {
                break;
            }
            loadDataTask.firePropertyChange("progress", 0, ((100 * f) / analysisFiles.length));

            // TODO: need to test for empty fractionnames or not enough fraction names (= too many dat files)
            // default value
            String fractionID = analysisFiles[f].getName().replace(".dat", "");
            if ((fractionNames.length > 0) && (fractionNames.length >= analysisFiles.length)) {
                fractionID = fractionNames[f];
            }

            // needs to be more robust
            boolean isStandard = (fractionID.compareToIgnoreCase(rawDataFileTemplate.getStandardIDs()[0]) == 0);
            // number the standards
            if (rawDataFileTemplate.standardIsKnown(fractionID)) {
                fractionID = fractionID + "-" + String.valueOf(standardIncrementer);
                standardIncrementer++;
            }

            // ************************************************************************************************
            // Laserchron uses Philip Wenig's Python routine to extract data from
            // ElementII .dat files and then pre-processes counts before passing to
            // fraction intake below
            String[][] extractedData;

            try {
                extractedData = ElementII_DatFileConverter.readDatFile5(analysisFiles[f], rawDataFileTemplate.getStringListOfElementsByIsotopicMass());

                // within each row
                // index 0 = scannumber; 1 = time stamp; 2 = ACF; followed by order of groups = 202  204  206	Pb207	Pb208	Th232	U238
                // each acquisition file contains background followed by peak followed by background
                // initial solution is to hard wire the first background and peak per Gehrels
                // later we will give user interactive tools to pick them out
                ArrayList<double[]> backgroundAcquisitions = new ArrayList<>();
                ArrayList<double[]> peakAcquisitions = new ArrayList<>();

                int hardwiredEndOfBackground = 24;
                // process time stamp from first scan as time stamp of file and background
                long fractionBackgroundTimeStamp = calculateTimeStamp(extractedData[0][1]);
                // process time stamp of first peak reading
                long fractionPeakTimeStamp = calculateTimeStamp(extractedData[hardwiredEndOfBackground + 1][1]);

                for (int i = rawDataFileTemplate.getBlockStartOffset(); i < rawDataFileTemplate.getBlockSize(); i++) {
                    // 202  204  206	Pb207	Pb208	Th232	U238
                    if (i < hardwiredEndOfBackground) {
                        double[] backgroundIntensities = new double[7];
                        backgroundAcquisitions.add(backgroundIntensities);
                        backgroundIntensities[0] = calcAvgPulseOrAnalog(3, 6, extractedData[i]);
                        backgroundIntensities[1] = calcAvgPulseOrAnalog(8, 11, extractedData[i]);
                        backgroundIntensities[2] = calcAvgPulseThenAnalog(13, 16, extractedData[i]);
                        backgroundIntensities[3] = calcAvgPulseThenAnalog(22, 25, extractedData[i]);
                        backgroundIntensities[4] = calcAvgPulseThenAnalog(31, 34, extractedData[i]);
                        backgroundIntensities[5] = calcAvgPulseThenAnalog(40, 43, extractedData[i]);
                        backgroundIntensities[6] = calcAvgPulseThenAnalog(49, 52, extractedData[i]);
                    } else if (i >= (hardwiredEndOfBackground)) {
                        double[] peakIntensities = new double[7];
                        peakAcquisitions.add(peakIntensities);
                        peakIntensities[0] = calcAvgPulseOrAnalog(3, 6, extractedData[i]);
                        peakIntensities[1] = calcAvgPulseOrAnalog(8, 11, extractedData[i]);
                        peakIntensities[2] = calcAvgPulseThenAnalog(13, 16, extractedData[i]);
                        peakIntensities[3] = calcAvgPulseThenAnalog(22, 25, extractedData[i]);
                        peakIntensities[4] = calcAvgPulseThenAnalog(31, 34, extractedData[i]);
                        peakIntensities[5] = calcAvgPulseThenAnalog(40, 43, extractedData[i]);
                        peakIntensities[6] = calcAvgPulseThenAnalog(49, 52, extractedData[i]);
                    }
                }  // i loop

                TripoliFraction tripoliFraction
                        = new TripoliFraction( //
                                fractionID, //
                                massSpec.getCommonLeadCorrectionHighestLevel(), //
                                isStandard,
                                fractionBackgroundTimeStamp, //
                                fractionPeakTimeStamp,
                                peakAcquisitions.size());

                SortedSet<DataModelInterface> rawRatios = ((LaserchronElementIISetupUPb) massSpec).rawRatiosFactoryRevised();
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
                virtualCollectorModelMapToFieldIndexes.put(massSpec.getU238(), 6);

                massSpec.processFractionRawRatiosII(//
                        backgroundAcquisitions, peakAcquisitions, isStandard, usingFullPropagation, tripoliFraction, virtualCollectorModelMapToFieldIndexes);

                tripoliFraction.shadeDataActiveMapLeft(leftShadeCount);
                System.out.println("\n**** Element II FractionID  " + fractionID);

                myTripoliFractions.add(tripoliFraction);
                
            } catch (PyException pyException) {
                System.out.println("bad read of fraction " + analysisFiles[f].getName() + " message = " + pyException.getMessage());
            }
        } // end of files loop

        if (myTripoliFractions.isEmpty()) {
            myTripoliFractions = null;
        }

        return myTripoliFractions;
    }

    private double calcAvgPulseThenAnalog(int startIndex, int endIndex, String[] data) {
        double retVal = 0.0;

        int countOfValues = 0;
        double sumOfValues = 0.0;
        for (int i = startIndex; i <= endIndex; i++) {
            if (data[i].contains("*")) {
                retVal = calcAvgPulseOrAnalog(startIndex + 4, endIndex + 4, data);
            } else {
                double val = Double.parseDouble(data[i]);
                sumOfValues += val;
                countOfValues++;
            }
        }

        // retVal > 0 means analogs were used already
        if ((retVal == 0) && (countOfValues > 0)) {
            retVal = sumOfValues / countOfValues;
        }

        return retVal;
    }
}
