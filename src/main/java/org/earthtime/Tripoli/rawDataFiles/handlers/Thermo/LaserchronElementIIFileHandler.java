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
    private static int referenceMaterialIncrementer;
    private static Map<String, Integer> referenceMaterialIncrementerMap = null;
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
        referenceMaterialIncrementer = 1;

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

        if (referenceMaterialIncrementerMap == null) {
            referenceMaterialIncrementerMap = new ConcurrentHashMap<>();
            for (int i = 0; i < rawDataFileTemplate.getStandardIDs().length; i++) {
                referenceMaterialIncrementerMap.put(rawDataFileTemplate.getStandardIDs()[i], 1);
            }
        }

        // Laserchron ElementII has folder of .dat files 
        analysisFiles = rawDataFile.listFiles((File dir, String name) -> {
            return name.toLowerCase().endsWith(".dat");
        });

        // Laserchron produces file with numerical ordering tags
        Arrays.sort(analysisFiles, new FractionFileNameComparator());

        if (analysisFiles.length > 0) {
            Arrays.sort(analysisFiles, new FractionFileModifiedComparator());
            
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

//        return rawDataFile;
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
     * @return the java.util.SortedSet<org.earthtime.Tripoli.fractions.TripoliFraction>
     */
    @Override
    protected SortedSet<TripoliFraction> loadRawDataFile(//
            SwingWorker loadDataTask, boolean usingFullPropagation, int leftShadeCount, int ignoreFirstFractions, boolean inLiveMode) {

        SortedSet myTripoliFractions = new TreeSet<>();

        // assume we are golden   
        // take first entry in fractionNames that came from scancsv file and confirm it is referenceMaterial (standard)
        String referenceMaterialfractionID = fractionNames[0];

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
            boolean isReferenceMaterial = (fractionID.substring(0, 2).compareToIgnoreCase(referenceMaterialfractionID.substring(0, 2)) == 0);
            // number the reference materials
//            if (isReferenceMaterial) {
//                fractionID = fractionID + "-" + String.valueOf(referenceMaterialIncrementer);
//                referenceMaterialIncrementer++;
//            }
            if (referenceMaterialIncrementerMap.containsKey(fractionID)) {
                int refMatIndex = referenceMaterialIncrementerMap.get(fractionID);
                referenceMaterialIncrementerMap.put(fractionID, refMatIndex + 1);
                fractionID = fractionID + "-" + String.valueOf(refMatIndex);
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
                    if (rawDataFileTemplate instanceof LaserchronElementII_RawDataTemplate_A) {
                        processIntensities_A(i, hardwiredEndOfBackground, backgroundAcquisitions, peakAcquisitions, extractedData[i]);
                    } else if (rawDataFileTemplate instanceof LaserchronElementII_RawDataTemplate_B) {
                        processIntensities_B(i, hardwiredEndOfBackground, backgroundAcquisitions, peakAcquisitions, extractedData[i]);
                    } else if (rawDataFileTemplate instanceof LaserchronElementII_RawDataTemplate_C) {
                        processIntensities_C(i, hardwiredEndOfBackground, backgroundAcquisitions, peakAcquisitions, extractedData[i]);
                    }
                }  // i loop

                TripoliFraction tripoliFraction
                        = new TripoliFraction( //
                                fractionID, //
                                massSpec.getCommonLeadCorrectionHighestLevel(), //
                                isReferenceMaterial,
                                fractionBackgroundTimeStamp, //
                                fractionPeakTimeStamp,
                                peakAcquisitions.size());

                SortedSet<DataModelInterface> rawRatios = massSpec.rawRatiosFactoryRevised();

                tripoliFraction.setRawRatios(rawRatios);

                massSpec.setCountOfAcquisitions(peakAcquisitions.size());

                massSpec.processFractionRawRatiosII(//
                        backgroundAcquisitions, peakAcquisitions, usingFullPropagation, tripoliFraction, inLiveMode);

                tripoliFraction.shadeDataActiveMapLeft(leftShadeCount);
                System.out.println("\n**** Element II FractionID  " + fractionID + " refMat? " + isReferenceMaterial + "  livemode = " + inLiveMode + " <<<<<<<<<<<<<<<<<<\n");

                myTripoliFractions.add(tripoliFraction);
                
                if (isReferenceMaterial){
                    loadDataTask.firePropertyChange("refMaterialLoaded", 0, 1);
                }

            } catch (PyException pyException) {
                System.out.println("bad read of fraction " + analysisFiles[f].getName() + " message = " + pyException.getMessage());
            }
        } // end of files loop

//        if (myTripoliFractions.isEmpty()) {
//            myTripoliFractions = null;
//        }

        return myTripoliFractions;
    }

    private void processIntensities_A(int i, int hardwiredEndOfBackground, ArrayList<double[]> backgroundAcquisitions, ArrayList<double[]> peakAcquisitions, String[] extractedData) {
        // 202  204  206 Pb207	Pb208	Th232 U238
        double[] backgroundIntensities = new double[7];
        double[] peakIntensities = new double[7];
        if (i < hardwiredEndOfBackground) {
            backgroundAcquisitions.add(backgroundIntensities);
            backgroundIntensities[0] = calcAvgPulseOrAnalog(3, 6, extractedData);
            backgroundIntensities[1] = calcAvgPulseOrAnalog(8, 11, extractedData);
            backgroundIntensities[2] = calcAvgPulseThenAnalog(13, 16, extractedData);
            backgroundIntensities[3] = calcAvgPulseThenAnalog(22, 25, extractedData);
            backgroundIntensities[4] = calcAvgPulseThenAnalog(31, 34, extractedData);
            backgroundIntensities[5] = calcAvgPulseThenAnalog(40, 43, extractedData);
            backgroundIntensities[6] = calcAvgPulseThenAnalog(49, 52, extractedData);
        } else if (i >= (hardwiredEndOfBackground)) {
            peakAcquisitions.add(peakIntensities);
            peakIntensities[0] = calcAvgPulseOrAnalog(3, 6, extractedData);
            peakIntensities[1] = calcAvgPulseOrAnalog(8, 11, extractedData);
            peakIntensities[2] = calcAvgPulseThenAnalog(13, 16, extractedData);
            peakIntensities[3] = calcAvgPulseThenAnalog(22, 25, extractedData);
            peakIntensities[4] = calcAvgPulseThenAnalog(31, 34, extractedData);
            peakIntensities[5] = calcAvgPulseThenAnalog(40, 43, extractedData);
            peakIntensities[6] = calcAvgPulseThenAnalog(49, 52, extractedData);
        }
        // detect analog and remove negative flag
        for (int j = 0; j < 7; j++) {
            backgroundIntensities[j] = Math.abs(backgroundIntensities[j]);
            peakIntensities[j] = Math.abs(peakIntensities[j]);
        }
    }

    private void processIntensities_B(int i, int hardwiredEndOfBackground, ArrayList<double[]> backgroundAcquisitions, ArrayList<double[]> peakAcquisitions, String[] extractedData) {
        // 202  204  206 Pb207	Pb208 Th232 U235 U238
        double[] backgroundIntensities = new double[8];
        double[] peakIntensities = new double[8];
        if (i < hardwiredEndOfBackground) {
            backgroundAcquisitions.add(backgroundIntensities);
            backgroundIntensities[0] = calcAvgPulseOrAnalog(3, 6, extractedData);
            backgroundIntensities[1] = calcAvgPulseOrAnalog(12, 15, extractedData);
            backgroundIntensities[2] = calcAvgPulseThenAnalog(21, 24, extractedData);
            backgroundIntensities[3] = calcAvgPulseThenAnalog(30, 33, extractedData);
            backgroundIntensities[4] = calcAvgPulseThenAnalog(39, 42, extractedData);
            backgroundIntensities[5] = calcAvgPulseThenAnalog(48, 51, extractedData);
            backgroundIntensities[6] = calcAvgPulseThenAnalog(57, 60, extractedData);
            backgroundIntensities[7] = calcAvgPulseThenAnalog(66, 69, extractedData);
        } else if (i >= (hardwiredEndOfBackground)) {
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
        // detect analog and remove negative flag
        for (int j = 0; j < 8; j++) {
            // test for GG's special case per email31 Jan 2016
            //TODO: use phys constants model
            if (j == 7) {
                if (backgroundIntensities[j] < 0.0) {
                    // U238 is analog so use 235 * 137.82
                    backgroundIntensities[7] = backgroundIntensities[6] * 137.82;
                }
                if (peakIntensities[j] < 0.0) {
                    // U238 is analog so use 235 * 137.82
                    peakIntensities[7] = peakIntensities[6] * 137.82;
                }
            }
            backgroundIntensities[j] = Math.abs(backgroundIntensities[j]);
            peakIntensities[j] = Math.abs(peakIntensities[j]);
        }
    }

    private void processIntensities_C(int i, int hardwiredEndOfBackground, ArrayList<double[]> backgroundAcquisitions, ArrayList<double[]> peakAcquisitions, String[] extractedData) {
        // 176 202  204  206 Pb207 Pb208 Th232 U235 U238
        double[] backgroundIntensities = new double[9];
        double[] peakIntensities = new double[9];
        if (i < hardwiredEndOfBackground) {
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
        } else if (i >= (hardwiredEndOfBackground)) {
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

        // detect analog and remove negative flag
        for (int j = 0; j < 9; j++) {
            // test for GG's special case per email31 Jan 2016
            //TODO: use phys constants model
            if (j == 8) {
                if (backgroundIntensities[j] < 0.0) {
                    // U238 is analog so use 235 * 137.82
                    backgroundIntensities[8] = backgroundIntensities[7] * 137.82;
                }
                if (peakIntensities[j] < 0.0) {
                    // U238 is analog so use 235 * 137.82
                    peakIntensities[8] = peakIntensities[7] * 137.82;
                }
            }
            backgroundIntensities[j] = Math.abs(backgroundIntensities[j]);
            peakIntensities[j] = Math.abs(peakIntensities[j]);
        }
    }
}
