/*
 * LaserchronElementIIFileHandler
 *
 * Copyright 2006-2018 James F. Bowring, CIRDLES.org, and Earth-Time.org
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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.earthtime.Tripoli.dataModels.DataModelInterface;
import org.earthtime.Tripoli.fractions.TripoliFraction;
import org.earthtime.Tripoli.rawDataFiles.handlers.AbstractRawDataFileHandler;
import org.earthtime.Tripoli.rawDataFiles.templates.Thermo.LaserchronElementII_RawDataTemplate_A;
import org.earthtime.Tripoli.rawDataFiles.templates.Thermo.LaserchronElementII_RawDataTemplate_B;
import org.earthtime.Tripoli.rawDataFiles.templates.Thermo.LaserchronElementII_RawDataTemplate_C;
import org.earthtime.archivingTools.URIHelper;
import org.earthtime.pythonUtilities.ElementII_DatFileConverter;
import org.earthtime.reduxLabData.ReduxLabData;
import org.earthtime.utilities.FileHelper;
import org.python.core.PyException;

/**
 *
 * @author James F. Bowring
 */
public class LaserchronElementIIFileHandler extends AbstractRawDataFileHandler {

    // Class variables
    private static final long serialVersionUID = -2860923405769819758L;
    private static final LaserchronElementIIFileHandler instance = new LaserchronElementIIFileHandler();
    private static Map<String, Integer> referenceMaterialIncrementerMap = null;
    // Instance variables
    private File[] analysisFiles;
    private String[] fractionNames;
    private static double r238_235s = ReduxLabData.getInstance().getDefaultR238_235s().getValue().doubleValue();

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
        // temp for july 2016 from Noah McLean eamil July 29 2016
        /*
        Thanks.  For the purposes of this comparison for George's C40 dataset from Dan,
        can you please change the Arizona Laserchron Element2 parsing code?  
        There should be 73 integrations in each analysis.  If the first integration 
        is indexed 1, use integrations 3-15 for the baseline and 20-54 for the on-peak. 
        Everything else can get rejected 
         */
        baselineStartIndex = 3;
        baselineEndIndex = 15;
        peakStartIndex = 20;
        peakEndIndex = 54;
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
     * @param inLiveMode the value of inLiveMode
     */
    @Override
    public void getAndLoadRawIntensityDataFile(SwingWorker loadDataTask, boolean usingFullPropagation, int leftShadeCount, int ignoreFirstFractions, boolean inLiveMode) {

        if ((referenceMaterialIncrementerMap == null) || !inLiveMode) {
            referenceMaterialIncrementerMap = new ConcurrentHashMap<>();
            for (String standardID : rawDataFileTemplate.getStandardIDs()) {
                referenceMaterialIncrementerMap.put(standardID, 1);
            }
        }

        // Laserchron ElementII has folder of .dat files 
        analysisFiles = rawDataFile.listFiles((File dir, String name) -> {
            return name.toLowerCase().endsWith(".dat");
        });

        // Laserchron produces file with numerical ordering tags
        Arrays.sort(analysisFiles, new FractionFileNameComparator());

        //load current values
//        baselineStartIndex = acqu
        if (analysisFiles.length > 0) {
//            this can be broken => depend on naming convention Arrays.sort(analysisFiles, new FractionFileModifiedComparator());

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
                tripoliFractions = loadRawDataFile(loadDataTask, usingFullPropagation, leftShadeCount, ignoreFirstFractions, inLiveMode);
            }
        } else {
            JOptionPane.showMessageDialog(
                    null,
                    new String[]{"Selected raw data folder does not contain valid files."},
                    "ET Redux Warning",
                    JOptionPane.WARNING_MESSAGE);
        }
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
     * @param inLiveMode the value of inLiveMode
     * @return the
     * java.util.SortedSet<org.earthtime.Tripoli.fractions.TripoliFraction>
     */
    @Override
    protected SortedSet<TripoliFraction> loadRawDataFile(//
            SwingWorker loadDataTask, boolean usingFullPropagation, int leftShadeCount, int ignoreFirstFractions, boolean inLiveMode) {

        SortedSet myTripoliFractions = new TreeSet<>();

        // assume we are golden   
        // take first entry in fractionNames that came from scancsv file and confirm it is referenceMaterial (standard)
        String primaryReferenceMaterialfractionID = fractionNames[0];

        // todo: need to confirm it is the standard - maybe do this at parameter manager
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
            boolean isPrimaryReferenceMaterial = (fractionID.substring(0, 2).compareToIgnoreCase(primaryReferenceMaterialfractionID.substring(0, 2)) == 0);
            boolean isSecondaryReferenceMaterial = false;

            // number the reference material
            if (referenceMaterialIncrementerMap.containsKey(fractionID)) {
                int refMatIndex = referenceMaterialIncrementerMap.get(fractionID);
                referenceMaterialIncrementerMap.put(fractionID, refMatIndex + 1);
                fractionID = fractionID + "-" + String.valueOf(refMatIndex);

                isSecondaryReferenceMaterial = !isPrimaryReferenceMaterial;
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
                List<double[]> backgroundAcquisitions = new ArrayList<>();
                List<double[]> peakAcquisitions = new ArrayList<>();
                // Sept 2016
                List<double[]> backgroundAnalogCorrectionFactors = new ArrayList<>();
                List<double[]> peakAnalogCorrectionFactors = new ArrayList<>();

                // process time stamp from first scan as time stamp of file and background
                long fractionBackgroundTimeStamp = calculateTimeStampFromThermoDatFile(extractedData[0][1]);
                // process time stamp of first peak reading
                long fractionPeakTimeStamp = calculateTimeStampFromThermoDatFile(extractedData[baselineEndIndex + 1][1]);

                for (int i = rawDataFileTemplate.getBlockStartOffset(); i < rawDataFileTemplate.getBlockSize(); i++) {
                    if (rawDataFileTemplate instanceof LaserchronElementII_RawDataTemplate_A) {
                        processIntensities_A(i, backgroundAnalogCorrectionFactors, peakAnalogCorrectionFactors, backgroundAcquisitions, peakAcquisitions, extractedData[i]);
                    } else if (rawDataFileTemplate instanceof LaserchronElementII_RawDataTemplate_B) {
                        processIntensities_B(i, backgroundAnalogCorrectionFactors, peakAnalogCorrectionFactors, backgroundAcquisitions, peakAcquisitions, extractedData[i]);
                    } else if (rawDataFileTemplate instanceof LaserchronElementII_RawDataTemplate_C) {
                        processIntensities_C(i, backgroundAnalogCorrectionFactors, peakAnalogCorrectionFactors, backgroundAcquisitions, peakAcquisitions, extractedData[i]);
                    }
                }  // i loop

                TripoliFraction tripoliFraction
                        = new TripoliFraction(
                                fractionID, //
                                massSpec.getCommonLeadCorrectionHighestLevel(), //
                                isPrimaryReferenceMaterial,
                                isSecondaryReferenceMaterial,
                                fractionBackgroundTimeStamp, //
                                fractionPeakTimeStamp,
                                peakAcquisitions.size());

                SortedSet<DataModelInterface> rawRatios = massSpec.rawRatiosFactoryRevised();

                tripoliFraction.setRawRatios(rawRatios);

                massSpec.setCountOfAcquisitions(peakAcquisitions.size());

                massSpec.processFractionRawRatiosII(backgroundAnalogCorrectionFactors, peakAnalogCorrectionFactors, //
                        backgroundAcquisitions, peakAcquisitions, usingFullPropagation, tripoliFraction, inLiveMode);

                tripoliFraction.shadeDataActiveMapLeft(leftShadeCount);
                System.out.println("\n**** Element II FractionID  " + fractionID + " refMat? " + isPrimaryReferenceMaterial + "  livemode = " + inLiveMode + " <<<<<<<<<<<<<<<<<<\n");

                myTripoliFractions.add(tripoliFraction);

                if (isPrimaryReferenceMaterial) {
                    loadDataTask.firePropertyChange("refMaterialLoaded", 0, 1);
                }

            } catch (PyException pyException) {
                System.out.println("bad read of fraction " + analysisFiles[f].getName() + " message = " + pyException.getMessage());
            }
        } // end of files loop

        return myTripoliFractions;
    }

    /**
     *
     * @param i the value of i
     * @param backgroundAcquisitions the value of backgroundAcquisitions
     * @param peakAcquisitions the value of peakAcquisitions
     * @param extractedData the value of extractedData
     */
    private void processIntensities_A(int i, List<double[]> backgroundAnalogCorrectionFactors, List<double[]> peakAnalogCorrectionFactors, List<double[]> backgroundAcquisitions, List<double[]> peakAcquisitions, String[] extractedData) {
        // 202  204  206 Pb207	Pb208	Th232 U238
        double[] backgroundIntensities = new double[7];
        double[] peakIntensities = new double[7];
        double[] backgroundACFs = new double[7];
        double[] peakACFs = new double[7];
        boolean isLegal = false;
        if (legalBaselineIndex(i)) {
            isLegal = true;
            backgroundAnalogCorrectionFactors.add(backgroundACFs);
            double acf = Double.parseDouble(extractedData[2]);
            for (int j = 0; j < 7; j++) {
                backgroundACFs[j] = acf;
            }

            backgroundAcquisitions.add(backgroundIntensities);
            backgroundIntensities[0] = calcAvgPulseOrAnalog(3, 6, extractedData);
            backgroundIntensities[1] = calcAvgPulseOrAnalog(8, 11, extractedData);
            backgroundIntensities[2] = calcAvgPulseThenAnalog(13, 16, extractedData);
            backgroundIntensities[3] = calcAvgPulseThenAnalog(22, 25, extractedData);
            backgroundIntensities[4] = calcAvgPulseThenAnalog(31, 34, extractedData);
            backgroundIntensities[5] = calcAvgPulseThenAnalog(40, 43, extractedData);
            backgroundIntensities[6] = calcAvgPulseThenAnalog(49, 52, extractedData);
        } else if (legalPeakIndex(i)) {
            isLegal = true;
            peakAnalogCorrectionFactors.add(peakACFs);
            double acf = Double.parseDouble(extractedData[2]);
            for (int j = 0; j < 7; j++) {
                peakACFs[j] = acf;
            }

            peakAcquisitions.add(peakIntensities);
            peakIntensities[0] = calcAvgPulseOrAnalog(3, 6, extractedData);
            peakIntensities[1] = calcAvgPulseOrAnalog(8, 11, extractedData);
            peakIntensities[2] = calcAvgPulseThenAnalog(13, 16, extractedData);
            peakIntensities[3] = calcAvgPulseThenAnalog(22, 25, extractedData);
            peakIntensities[4] = calcAvgPulseThenAnalog(31, 34, extractedData);
            peakIntensities[5] = calcAvgPulseThenAnalog(40, 43, extractedData);
            peakIntensities[6] = calcAvgPulseThenAnalog(49, 52, extractedData);
        }

        if (isLegal) {
            // detect analog and remove negative flag and divide by acf
            for (int j = 0; j < 7; j++) {
                if (backgroundIntensities[j] < 0.0) {
                    backgroundIntensities[j] = Math.abs(backgroundIntensities[j]);// oct 2016 off for now see IonCollectorModel / backgroundACFs[j];
                } else {
                    // not analog mode so acf is 1.0
                    backgroundACFs[j] = 1.0;
                }

                if (peakIntensities[j] < 0.0) {
                    peakIntensities[j] = Math.abs(peakIntensities[j]);// oct 2016 off for now see IonCollectorModel / peakACFs[j];
                } else {
                    // not analog mode so acf is 1.0
                    peakACFs[j] = 1.0;
                }
            }
        }
    }

    /**
     *
     * @param i the value of i
     * @param backgroundAcquisitions the value of backgroundAcquisitions
     * @param peakAcquisitions the value of peakAcquisitions
     * @param extractedData the value of extractedData
     */
    private void processIntensities_B(int i, List<double[]> backgroundAnalogCorrectionFactors, List<double[]> peakAnalogCorrectionFactors, List<double[]> backgroundAcquisitions, List<double[]> peakAcquisitions, String[] extractedData) {
        // 202  204  206 Pb207	Pb208 Th232 U235 U238
        double[] backgroundIntensities = new double[8];
        double[] peakIntensities = new double[8];
        double[] backgroundACFs = new double[8];
        double[] peakACFs = new double[8];
        boolean isLegal = false;
        if (legalBaselineIndex(i)) {
            isLegal = true;
            backgroundAnalogCorrectionFactors.add(backgroundACFs);
            double acf = Double.parseDouble(extractedData[2]);
            for (int j = 0; j < 8; j++) {
                backgroundACFs[j] = acf;
            }

            backgroundAcquisitions.add(backgroundIntensities);
            backgroundIntensities[0] = calcAvgPulseOrAnalog(3, 6, extractedData);
            backgroundIntensities[1] = calcAvgPulseOrAnalog(12, 15, extractedData);
            backgroundIntensities[2] = calcAvgPulseThenAnalog(21, 24, extractedData);
            backgroundIntensities[3] = calcAvgPulseThenAnalog(30, 33, extractedData);
            backgroundIntensities[4] = calcAvgPulseThenAnalog(39, 42, extractedData);
            backgroundIntensities[5] = calcAvgPulseThenAnalog(48, 51, extractedData);
            backgroundIntensities[6] = calcAvgPulseThenAnalog(57, 60, extractedData);
            backgroundIntensities[7] = calcAvgPulseThenAnalog(66, 69, extractedData);
        } else if (legalPeakIndex(i)) {
            isLegal = true;
            peakAnalogCorrectionFactors.add(peakACFs);
            double acf = Double.parseDouble(extractedData[2]);
            for (int j = 0; j < 8; j++) {
                peakACFs[j] = acf;
            }

            peakAcquisitions.add(peakIntensities);
            peakIntensities[0] = calcAvgPulseOrAnalog(3, 6, extractedData);
            peakIntensities[1] = calcAvgPulseOrAnalog(12, 15, extractedData);
            peakIntensities[2] = calcAvgPulseThenAnalog(21, 24, extractedData);
            peakIntensities[3] = calcAvgPulseThenAnalog(30, 33, extractedData);
            peakIntensities[4] = calcAvgPulseThenAnalog(39, 42, extractedData);
            peakIntensities[5] = calcAvgPulseThenAnalog(48, 51, extractedData);
            peakIntensities[6] = calcAvgPulseThenAnalog(57, 60, extractedData);
            peakIntensities[7] = calcAvgPulseThenAnalog(66, 69, extractedData);
        }
        if (isLegal) {
            // detect analog and remove negative flag
            boolean backPb206IsAnalog = false;
            boolean peakPb206IsAnalog = false;
            for (int j = 0; j < 8; j++) {
                // test for GG's special case per email 31 Jan 2016

                if (j == 7) {
                    if (backgroundIntensities[j] < 0.0) {
                        if (backPb206IsAnalog) {
                            backgroundIntensities[7] = Math.abs(backgroundIntensities[7]);
                        } else {
                            // U238 is analog and Pb206 is not analog so use 235 * 137.82 now 137.81846 +/- 0.0223
                            backgroundIntensities[7] = backgroundIntensities[6] * r238_235s;//137.82;
                        }
                    }
                    if (peakIntensities[j] < 0.0) {
                        if (peakPb206IsAnalog) {
                            peakIntensities[7] = Math.abs(peakIntensities[7]);
                        } else {
                            // U238 is analog so use 235 * 137.82
                            peakIntensities[7] = peakIntensities[6] * r238_235s;
                        }
                    }
                }

                if (backgroundIntensities[j] < 0.0) {
                    backgroundIntensities[j] = Math.abs(backgroundIntensities[j]);// oct 2016 off for now see IonCollectorModel / backgroundACFs[j];
                    // oct 2016 revised by Noah
                    if (j == 2) {
                        backPb206IsAnalog = true;
                    }
                } else {
                    // not analog mode so acf is 1.0
                    backgroundACFs[j] = 1.0;
                }

                if (peakIntensities[j] < 0.0) {
                    peakIntensities[j] = Math.abs(peakIntensities[j]);// oct 2016 off for now see IonCollectorModel / peakACFs[j];
                    // oct 2016 revised by Noah
                    if (j == 2) {
                        peakPb206IsAnalog = true;
                    }
                } else {
                    // not analog mode so acf is 1.0
                    peakACFs[j] = 1.0;
                }
            }
        }
    }

    /**
     *
     * @param i the value of i
     * @param backgroundAcquisitions the value of backgroundAcquisitions
     * @param peakAcquisitions the value of peakAcquisitions
     * @param extractedData the value of extractedData
     */
    private void processIntensities_C(int i, List<double[]> backgroundAnalogCorrectionFactors, List<double[]> peakAnalogCorrectionFactors, List<double[]> backgroundAcquisitions, List<double[]> peakAcquisitions, String[] extractedData) {
        // 176 202  204  206 Pb207 Pb208 Th232 U235 U238
        double[] backgroundIntensities = new double[9];
        double[] peakIntensities = new double[9];
        double[] backgroundACFs = new double[9];
        double[] peakACFs = new double[9];
        boolean isLegal = false;
        if (legalBaselineIndex(i)) {
            isLegal = true;
            backgroundAnalogCorrectionFactors.add(backgroundACFs);
            double acf = Double.parseDouble(extractedData[2]);
            for (int j = 0; j < 9; j++) {
                backgroundACFs[j] = acf;
            }

            backgroundAcquisitions.add(backgroundIntensities);
            backgroundIntensities[0] = calcAvgPulseOrAnalog(3, 6, extractedData);
            backgroundIntensities[1] = calcAvgPulseOrAnalog(12, 15, extractedData);
            backgroundIntensities[2] = calcAvgPulseOrAnalog(21, 24, extractedData);
            backgroundIntensities[3] = calcAvgPulseThenAnalog(30, 33, extractedData);
            backgroundIntensities[4] = calcAvgPulseThenAnalog(39, 42, extractedData);
            backgroundIntensities[5] = calcAvgPulseThenAnalog(48, 51, extractedData);
            backgroundIntensities[6] = calcAvgPulseThenAnalog(57, 60, extractedData);
            backgroundIntensities[7] = calcAvgPulseThenAnalog(66, 69, extractedData);
            backgroundIntensities[8] = calcAvgPulseThenAnalog(75, 78, extractedData);
        } else if (legalPeakIndex(i)) {
            isLegal = true;
            peakAnalogCorrectionFactors.add(peakACFs);
            double acf = Double.parseDouble(extractedData[2]);
            for (int j = 0; j < 9; j++) {
                peakACFs[j] = acf;
            }

            peakAcquisitions.add(peakIntensities);
            peakIntensities[0] = calcAvgPulseOrAnalog(3, 6, extractedData);
            peakIntensities[1] = calcAvgPulseOrAnalog(12, 15, extractedData);
            peakIntensities[2] = calcAvgPulseOrAnalog(21, 24, extractedData);
            peakIntensities[3] = calcAvgPulseThenAnalog(30, 33, extractedData);
            peakIntensities[4] = calcAvgPulseThenAnalog(39, 42, extractedData);
            peakIntensities[5] = calcAvgPulseThenAnalog(48, 51, extractedData);
            peakIntensities[6] = calcAvgPulseThenAnalog(57, 60, extractedData);
            peakIntensities[7] = calcAvgPulseThenAnalog(66, 69, extractedData);
            peakIntensities[8] = calcAvgPulseThenAnalog(75, 78, extractedData);
        }
        if (isLegal) {
            // detect analog and remove negative flag
            boolean backPb206IsAnalog = false;
            boolean peakPb206IsAnalog = false;

            for (int j = 0; j < 9; j++) {
                // test for GG's special case per email 31 Jan 2016
                if (j == 8) {
                    if (backgroundIntensities[j] < 0.0) {
                        if (backPb206IsAnalog) {
                            backgroundIntensities[8] = Math.abs(backgroundIntensities[8]);
                        } else {
                            // U238 is analog so use 235 * 137.82
                            backgroundIntensities[8] = backgroundIntensities[7] * r238_235s;
                        }
                    }
                    if (peakIntensities[j] < 0.0) {
                        if (peakPb206IsAnalog) {
                            peakIntensities[8] = Math.abs(peakIntensities[8]);
                        } else {
                            // U238 is analog so use 235 * 137.82
                            peakIntensities[8] = peakIntensities[7] * r238_235s;
                        }
                    }
                }

                if (backgroundIntensities[j] < 0.0) {
                    backgroundIntensities[j] = Math.abs(backgroundIntensities[j]);// oct 2016 off for now see IonCollectorModel / backgroundACFs[j];
                    // oct 2016 revised by Noah
                    if (j == 2) {
                        backPb206IsAnalog = true;
                    }
                } else {
                    // not analog mode so acf is 1.0
                    backgroundACFs[j] = 1.0;
                }

                if (peakIntensities[j] < 0.0) {
                    peakIntensities[j] = Math.abs(peakIntensities[j]);// oct 2016 off for now see IonCollectorModel / peakACFs[j];
                    // oct 2016 revised by Noah
                    if (j == 2) {
                        peakPb206IsAnalog = true;
                    }
                } else {
                    // not analog mode so acf is 1.0
                    peakACFs[j] = 1.0;
                }
            }
        }
    }

    /**
     *
     * @return the boolean
     */
    @Override
    public boolean getAndLoadRawIntensityDataForReview() {
        return false;
    }
}
