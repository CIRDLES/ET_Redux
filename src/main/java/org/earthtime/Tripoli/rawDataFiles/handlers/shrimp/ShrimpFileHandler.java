/*
 * ShrimpFileHandler
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
package org.earthtime.Tripoli.rawDataFiles.handlers.shrimp;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.xml.bind.JAXBException;
import org.earthtime.Tripoli.dataModels.DataModelInterface;
import org.earthtime.Tripoli.fractions.TripoliFraction;
import org.earthtime.Tripoli.massSpecSetups.singleCollector.shrimp.ShrimpSetupUPb;
import org.earthtime.Tripoli.rawDataFiles.handlers.AbstractRawDataFileHandler;

/**
 *
 * @author James F. Bowring
 */
public class ShrimpFileHandler extends AbstractRawDataFileHandler {

    // Class variables
    // private static final long serialVersionUID = -2860923405769819758L;
    private static ShrimpFileHandler instance = new ShrimpFileHandler();

    /**
     *
     * @param massSpec
     * @param rawDataFileTemplate
     */
    private ShrimpFileHandler() {

        super();
        NAME = "Shrimp XML Prawn File '.xml'";
        aboutInfo = "Details: This is the Prawn xml file from the SHRIMP. ";
    }

    /**
     *
     * @return
     */
    public static ShrimpFileHandler getInstance() {
        return instance;
    }

    /**
     *
     * @param tripoliRawDataFolder
     * @return
     */
    @Override
    public File validateAndGetHeaderDataFromRawIntensityFile(File tripoliRawDataFolder) {
        String dialogTitle = "Select a Shrimp Prawn '.xml' file:";

        // temporary during development
        JOptionPane.showMessageDialog(null,
                new String[]{"We are using a pre-selected Prawn file during intial development, which can be found here:\n\n"
                    + "https://raw.githubusercontent.com/bowring/XSD/master/SHRIMP/EXAMPLE_100142_G6147_10111109.43_10.33.37%20AM.xml"});

        rawDataFile = new File("temp.xml");//FileHelper.AllPlatformGetFolder(dialogTitle, tripoliRawDataFolder);
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

        // temp       
        rawDataFile = new File("/Users/sbowring/Documents/Development_XSD/100142_G6147_10111109.43 10.33.37 AM.xml");

        if (rawDataFile != null) {

            // create fractions from raw data and perform corrections and calculate ratios
            tripoliFractions = loadRawDataFile(loadDataTask, usingFullPropagation, leftShadeCount, ignoreFirstFractions, inLiveMode);
        } else {
            JOptionPane.showMessageDialog(
                    null,
                    new String[]{"Selected raw data file is empty."},
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
        loadDataTask.firePropertyChange("progress", 10, 33);

        try {
            List<org.cirdles.calamari.shrimp.ShrimpFraction> myShrimpFractions = null;
//            myShrimpFractions = org.cirdles.calamari.core.RawDataFileHandler.extractShrimpFractionsFromPrawnFile("/Users/sbowring/Google Drive/_ETRedux_ProjectData/SHRIMP/100142_G6147_10111109.43.xml");
            myShrimpFractions = org.cirdles.calamari.core.RawDataFileHandler.extractShrimpFractionsFromPrawnFile(//
                    "https://raw.githubusercontent.com/bowring/XSD/master/SHRIMP/EXAMPLE_100142_G6147_10111109.43_10.33.37%20AM.xml", true, false);

            // send name to project
            loadDataTask.firePropertyChange("projectName", "", myShrimpFractions.get(0).getNameOfMount());

            // assume we are golden   
            for (int f = ignoreFirstFractions; f < myShrimpFractions.size(); f++) {

                if (loadDataTask.isCancelled()) {
                    break;
                }

                loadDataTask.firePropertyChange("progress", 33, 33 + ((67 * f) / myShrimpFractions.size()));

                TripoliFraction tripoliFraction = processRunFraction(myShrimpFractions.get(f));

                // determine if standard reference material
                myTripoliFractions.add(tripoliFraction);

            } // end of files loop

            if (myTripoliFractions.isEmpty()) {
                myTripoliFractions = null;
            }

        } catch (JAXBException | MalformedURLException jAXBException) {
            JOptionPane.showMessageDialog(
                    null,
                    new String[]{"Selected Prawn file does not conform to schema."},
                    "ET Redux Warning",
                    JOptionPane.WARNING_MESSAGE);
        }
        return myTripoliFractions;
    }

    private TripoliFraction processRunFraction(org.cirdles.calamari.shrimp.ShrimpFraction shrimpFraction) {

        String fractionID = shrimpFraction.getFractionID();
        // temp hack
        boolean isReferenceMaterial = fractionID.startsWith("T.");
        long fractionPeakTimeStamp = shrimpFraction.getDateTimeMilliseconds();

        double[][] totalCounts = shrimpFraction.getTotalCounts();
        double[][] totalCountsOneSigmaAbs = shrimpFraction.getTotalCountsOneSigmaAbs();

        // within each row
        // index 0 = scannumber; followed by order of groups = 196  204 Backgrnd 206 207 208 238 248 254 270
        // each acquisition file contains 6 scans (for now)
        // each group contains totalCounts, 1-sigma, and totalCountsSBM
        // later we will give user interactive tools to pick them out
        ArrayList<double[]> backgroundAcquisitions = new ArrayList<>();
        ArrayList<double[]> peakAcquisitions = new ArrayList<>();
        ArrayList<double[]> peakAcquisitionsVariances = new ArrayList<>();

        for (int scan = 0; scan < totalCounts.length; scan++) {
            // use these for now: 196  204  206 Pb207	Pb208	238 248 254 270
            double[] peakCounts = new double[9];
            double[] peakVariances = new double[9];
            peakAcquisitions.add(peakCounts);
            peakAcquisitionsVariances.add(peakVariances);

            peakCounts[0] = totalCounts[scan][0];
            peakCounts[1] = totalCounts[scan][1];
            peakCounts[2] = totalCounts[scan][3];
            peakCounts[3] = totalCounts[scan][4];
            peakCounts[4] = totalCounts[scan][5];
            peakCounts[5] = totalCounts[scan][6];
            peakCounts[6] = totalCounts[scan][7];
            peakCounts[7] = totalCounts[scan][8];
            peakCounts[8] = totalCounts[scan][9];

            peakVariances[0] = Math.pow(totalCountsOneSigmaAbs[scan][0], 2);
            peakVariances[1] = Math.pow(totalCountsOneSigmaAbs[scan][1], 2);
            peakVariances[2] = Math.pow(totalCountsOneSigmaAbs[scan][3], 2);
            peakVariances[3] = Math.pow(totalCountsOneSigmaAbs[scan][4], 2);
            peakVariances[4] = Math.pow(totalCountsOneSigmaAbs[scan][5], 2);
            peakVariances[5] = Math.pow(totalCountsOneSigmaAbs[scan][6], 2);
            peakVariances[6] = Math.pow(totalCountsOneSigmaAbs[scan][7], 2);
            peakVariances[7] = Math.pow(totalCountsOneSigmaAbs[scan][8], 2);
            peakVariances[8] = Math.pow(totalCountsOneSigmaAbs[scan][9], 2);
        }

        TripoliFraction tripoliFraction
                = new TripoliFraction( //
                        fractionID, //
                        massSpec.getCommonLeadCorrectionHighestLevel(), //
                        isReferenceMaterial,
                        fractionPeakTimeStamp, //
                        fractionPeakTimeStamp,
                        peakAcquisitions.size());

        SortedSet<DataModelInterface> rawRatios = massSpec.rawRatiosFactoryRevised();

        tripoliFraction.setRawRatios(rawRatios);

        massSpec.setCountOfAcquisitions(peakAcquisitions.size());

        // supply calculated variances
        ((ShrimpSetupUPb) massSpec).initializeVariances(peakAcquisitionsVariances);

        // supply corrected counts
        ((ShrimpSetupUPb) massSpec).correctOnPeakIntensities(stripOutBackgroundHack(2, shrimpFraction.getNetPkCps()));

        // supply calculated variances of CORRECTED counts
        ((ShrimpSetupUPb) massSpec).initializeCorrectedVariances(stripOutBackgroundHack(2, shrimpFraction.getPkFerr()));

        // supply calculated ratios and uncertainties
        ((ShrimpSetupUPb) massSpec).populateRawAndLogRatios(shrimpFraction.getIsotopicRatios());

        massSpec.processFractionRawRatiosII(//
                backgroundAcquisitions, peakAcquisitions, true, tripoliFraction, false);

        tripoliFraction.shadeDataActiveMapLeft(0);
        System.out.println("\n**** SHRIMP FractionID  " + fractionID + " refMat? " + tripoliFraction.isStandard() + " <<<<<<<<<<<<<<<<<<\n");

        return tripoliFraction;
    }

    private double[][] stripOutBackgroundHack(int backgroundIndex, double[][] original) {

        double[][] stripped = new double[original.length][original[0].length - 1];
        int strippedCol = 0;
        for (int col = 0; col < original[0].length; col++) {
            if (col != backgroundIndex) {
                for (int row = 0; row < original.length; row++) {
                    stripped[row][strippedCol] = original[row][col];
                }
                strippedCol++;
            }
        }

        return stripped;
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
