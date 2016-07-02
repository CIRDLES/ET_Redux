/*
 * TexasAMElementIISingleCollFileHandler
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

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.earthtime.Tripoli.dataModels.DataModelInterface;
import org.earthtime.Tripoli.fractions.TripoliFraction;
import org.earthtime.Tripoli.rawDataFiles.handlers.AbstractRawDataFileHandler;
import org.earthtime.archivingTools.URIHelper;
import org.earthtime.pythonUtilities.ElementII_DatFileConverter;
import org.earthtime.utilities.FileHelper;
import org.python.core.PyException;

/**
 *
 * @author James F. Bowring
 */
public class TexasAMElementIISingleCollFileHandler extends AbstractRawDataFileHandler {

    // Class variables
    private static final long serialVersionUID = -4617928716331068063L;
    private static TexasAMElementIISingleCollFileHandler instance = new TexasAMElementIISingleCollFileHandler();
    private File[] analysisFiles;

    /**
     *
     * @param massSpec
     * @param rawDataFileTemplate
     */
    private TexasAMElementIISingleCollFileHandler() {

        super();

        NAME = "Texas A and M Element II SC Folder";

        aboutInfo = "Details: This is the default protocol for Texas A and M University's Thermo Finnigan Element II.";
    }

    /**
     *
     * @return
     */
    public static TexasAMElementIISingleCollFileHandler getInstance() {
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

        // ElementII has folder of .dat files some of which end with _b_b.dat unfortunately        
        analysisFiles = rawDataFile.listFiles((File f)
                -> ((f.getName().toLowerCase().endsWith(".dat"))
                && (!f.getName().toLowerCase().matches(".+_.+_b\\.dat"))));

        Arrays.sort(analysisFiles, new FractionFileModifiedComparator());

        if (analysisFiles.length > 0) {
            String onPeakFileContents = URIHelper.getTextFromURI(new File(analysisFiles[0].getAbsolutePath().toUpperCase().replace(".DAT", ".TXT")).getAbsolutePath());
            if (isValidRawDataFileType(analysisFiles[0]) //
                    && //
                    areKeyWordsPresent(onPeakFileContents)) {
                // create fractions from raw data and perform corrections and calculate ratios
                tripoliFractions = loadRawDataFile(loadDataTask, usingFullPropagation, leftShadeCount, 0, inLiveMode);
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
     * @return the
     * java.util.SortedSet<org.earthtime.Tripoli.fractions.TripoliFraction>
     */
    @Override
    protected SortedSet<TripoliFraction> loadRawDataFile(SwingWorker loadDataTask, boolean usingFullPropagation, int leftShadeCount, int ignoreFirstFractions, boolean inLiveMode) {
        tripoliFractions = new TreeSet<>();

        SortedSet myTripoliFractions = new TreeSet<>();

        // assume we are golden         
        // take first entry in fractionFileNames that came from .FIN file and ?? confirm it is referenceMaterial (standard)
        String referenceMaterialfractionIDPrefix = analysisFiles[0].getName().substring(0, 2);

        for (int f = 0; f < analysisFiles.length; f++) {

            if (loadDataTask.isCancelled()) {
                break;
            }
            loadDataTask.firePropertyChange("progress", 0, ((100 * f) / analysisFiles.length));

            // check for background file
            File backgroundFile = new File(analysisFiles[f].getAbsolutePath().replace(".dat", "_b.dat"));
            System.out.println("Background exists = " + backgroundFile.exists() + " = " + backgroundFile.getName());
            if (backgroundFile.exists()) {
                try {
                    String fractionID = analysisFiles[f].getName().toUpperCase().replace(".DAT", "");

                    // needs to be more robust
                    boolean isReferenceMaterial = (fractionID.substring(0, 2).compareToIgnoreCase(referenceMaterialfractionIDPrefix) == 0);

                    String[][] backgroundFileContents = ElementII_DatFileConverter.readDatFile5(backgroundFile, rawDataFileTemplate.getStringListOfElementsByIsotopicMass());
                    String[][] onPeakFileContents = ElementII_DatFileConverter.readDatFile5(analysisFiles[f], rawDataFileTemplate.getStringListOfElementsByIsotopicMass());

                    // test file size
                    if ((backgroundFileContents.length >= rawDataFileTemplate.getBlockSize())//
                            && (onPeakFileContents.length >= rawDataFileTemplate.getBlockSize())) {
                        ArrayList<double[]> backgroundAcquisitions = new ArrayList<>();
                        ArrayList<double[]> peakAcquisitions = new ArrayList<>();

                        // process time stamp from first scan as time stamp of file and background
                        long fractionBackgroundTimeStamp = calculateTimeStamp(backgroundFileContents[0][1]);
                        // process time stamp of first peak reading
                        long fractionPeakTimeStamp = calculateTimeStamp(onPeakFileContents[0][1]);

                        for (int i = 0; i < rawDataFileTemplate.getBlockSize(); i++) {
                            // 202  204  206	Pb207	Pb208	Th232	U235 U238
                            double[] backgroundIntensities = new double[8];
                            backgroundAcquisitions.add(backgroundIntensities);
                            backgroundIntensities[0] = calcAvgPulseOrAnalog(3, 5, backgroundFileContents[i]);
                            backgroundIntensities[1] = calcAvgPulseOrAnalog(7, 9, backgroundFileContents[i]);
                            backgroundIntensities[2] = calcAvgPulseOrAnalog(11, 13, backgroundFileContents[i]);
                            backgroundIntensities[3] = calcAvgPulseOrAnalog(15, 17, backgroundFileContents[i]);
                            backgroundIntensities[4] = calcAvgPulseOrAnalog(19, 21, backgroundFileContents[i]);
                            backgroundIntensities[5] = calcAvgPulseOrAnalog(23, 25, backgroundFileContents[i]);
                            backgroundIntensities[6] = calcAvgPulseOrAnalog(27, 29, backgroundFileContents[i]);
                            backgroundIntensities[7] = calcAvgPulseOrAnalog(31, 33, backgroundFileContents[i]);

                            double[] peakIntensities = new double[8];
                            peakAcquisitions.add(peakIntensities);
                            peakIntensities[0] = calcAvgPulseOrAnalog(3, 5, onPeakFileContents[i]);
                            peakIntensities[1] = calcAvgPulseOrAnalog(7, 9, onPeakFileContents[i]);
                            peakIntensities[2] = calcAvgPulseOrAnalog(11, 13, onPeakFileContents[i]);
                            peakIntensities[3] = calcAvgPulseOrAnalog(15, 17, onPeakFileContents[i]);
                            peakIntensities[4] = calcAvgPulseOrAnalog(19, 21, onPeakFileContents[i]);
                            peakIntensities[5] = calcAvgPulseOrAnalog(23, 25, onPeakFileContents[i]);
                            peakIntensities[6] = calcAvgPulseOrAnalog(27, 29, onPeakFileContents[i]);
                            peakIntensities[7] = calcAvgPulseOrAnalog(31, 33, onPeakFileContents[i]);

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
                        System.out.println("\n**** Element II FractionID  " + fractionID + " completed ***************************\n\n");

                        myTripoliFractions.add(tripoliFraction);
                    }
                } catch (PyException pyException) {
                    System.out.println("bad read of fraction " + analysisFiles[f].getName() + " message = " + pyException.getMessage());
                }
            } // end of files loop
        }
        if (myTripoliFractions.isEmpty()) {
            myTripoliFractions = null;
        }

        return myTripoliFractions;
    }

    /**
     *
     * @return the boolean
     */
    @Override
    public boolean getAndLoadRawIntensityDataFilePhaseI() {
        return false;
    }
}
