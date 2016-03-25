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

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.ArrayList;
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
import org.earthtime.utilities.FileHelper;

/**
 *
 * @author James F. Bowring
 */
public class WashStateElementIISingleCollFileHandler extends AbstractRawDataFileHandler {

    // Class variables
    // private static final long serialVersionUID = 3111511502335804607L;
    private static WashStateElementIISingleCollFileHandler instance = new WashStateElementIISingleCollFileHandler();
    private File[] analysisFiles;

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
     */
    @Override
    public void getAndLoadRawIntensityDataFile(SwingWorker loadDataTask, boolean usingFullPropagation, int leftShadeCount, int ignoreFirstFractions) {

        // get .txt files from the folder and check the first one
        analysisFiles = rawDataFile.listFiles((File dir, String name) //
                -> (name.toLowerCase().endsWith(".txt"))//
                && //
                (!name.toLowerCase().endsWith("_b.txt")));

        if (analysisFiles.length > 0) {
            String onPeakFileContents = URIHelper.getTextFromURI(analysisFiles[0].getAbsolutePath());
            if (isValidRawDataFileType(analysisFiles[0]) //
                    && //
                    areKeyWordsPresent(onPeakFileContents)) {
                // create fractions from raw data and perform corrections and calculate ratios
                loadRawDataFile(loadDataTask, usingFullPropagation, leftShadeCount, 0);
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
    protected SortedSet<TripoliFraction> loadRawDataFile(SwingWorker loadDataTask, boolean usingFullPropagation, int leftShadeCount, int ignoreFirstFractions) {
        tripoliFractions = new TreeSet<>();

        // assume we are golden        
        for (int f = 0; f < analysisFiles.length; f++) {

            if (loadDataTask.isCancelled()) {
                break;
            }
            loadDataTask.firePropertyChange("progress", 0, ((100 * f) / analysisFiles.length));

            String fractionID = analysisFiles[f].getName().toUpperCase().replace(".TXT", "");
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

                if (f == 137) {
                    System.out.println();
                }
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
                boolean isStandard = isStandardFractionID(fractionID);
                if (isStandard){
                    fractionID = "PLE-" + fractionID;
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

        if (tripoliFractions.isEmpty()) {
            tripoliFractions = null;
        }

        return tripoliFractions;
    }

    private void readObject(
            ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        ObjectStreamClass myObject = ObjectStreamClass.lookup(
                Class.forName(WashStateElementIISingleCollFileHandler.class.getCanonicalName()));
        long theSUID = myObject.getSerialVersionUID();
        System.out.println("Customized De-serialization of WashStateElementIISingleCollFileHandler " + theSUID);
    }
}
