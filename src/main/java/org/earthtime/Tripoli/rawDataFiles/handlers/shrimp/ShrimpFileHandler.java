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
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import org.cirdles.shrimp.PrawnFile;
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
     */
    @Override
    public void getAndLoadRawIntensityDataFile(SwingWorker loadDataTask, boolean usingFullPropagation, int leftShadeCount, int ignoreFirstFractions) {

        // temp       
        rawDataFile = new File("/Users/sbowring/Documents/Development_XSD/100142_G6147_10111109.43 10.33.37 AM.xml");

        if (rawDataFile != null) {

            // create fractions from raw data and perform corrections and calculate ratios
            tripoliFractions = loadRawDataFile(loadDataTask, usingFullPropagation, leftShadeCount, ignoreFirstFractions);
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
     * @return
     */
    @Override
    protected SortedSet<TripoliFraction> loadRawDataFile(//
            SwingWorker loadDataTask, boolean usingFullPropagation, int leftShadeCount, int ignoreFirstFractions) {

        SortedSet myTripoliFractions = new TreeSet<>();
        PrawnFile prawnFile = null;

        try {
            // remote copy of example file
            java.net.URL url = null;
            url = new URL("https://raw.githubusercontent.com/bowring/XSD/master/SHRIMP/EXAMPLE_100142_G6147_10111109.43_10.33.37%20AM.xml");

            JAXBContext jaxbContext = JAXBContext.newInstance(PrawnFile.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            // show some progress
            loadDataTask.firePropertyChange("progress", 0, 10);
            prawnFile = (PrawnFile) jaxbUnmarshaller.unmarshal(url);

            // send name to project
            loadDataTask.firePropertyChange("projectName", "", prawnFile.getMount());
            // assume we are golden   
            // a 'run' is an analysis or fraction
            for (int f = ignoreFirstFractions; f < prawnFile.getRuns(); f++) {

                if (loadDataTask.isCancelled()) {
                    break;
                }
                loadDataTask.firePropertyChange("progress", 0, 10 + ((95 * f) / prawnFile.getRuns()));
                // assume we are golden   
                // a 'run' is an analysis or fraction
                for (int f = ignoreFirstFractions; f < prawnFile.getRuns(); f++) {

                    if (loadDataTask.isCancelled()) {
                        break;
                    }
                    loadDataTask.firePropertyChange("progress", 0, ((100 * f) / prawnFile.getRuns()));

                    PrawnFile.Run runFraction = prawnFile.getRun().get(f);

                    TripoliFraction tripoliFraction = processRunFraction(runFraction);

                    // determine if standard reference material
                    myTripoliFractions.add(tripoliFraction);

                } // end of files loop

                if (myTripoliFractions.isEmpty()) {
                    myTripoliFractions = null;
                }
            }catch (JAXBException | MalformedURLException jAXBException) {
            JOptionPane.showMessageDialog(
                    null,
                    new String[]{"Selected Prawn file does not conform to schema."},
                    "ET Redux Warning",
                    JOptionPane.WARNING_MESSAGE);
        }
            return myTripoliFractions;
        }

    

    

    private TripoliFraction processRunFraction(PrawnFile.Run runFraction) {
        String fractionID = runFraction.getPar().get(0).getValue();
        // temp hack
        boolean isReferenceMaterial = fractionID.startsWith("T.");

        // format "2010-11-11"
        String setDate = runFraction.getSet().getPar().get(0).getValue();
        // format 10:17:34
        String setTime = runFraction.getSet().getPar().get(1).getValue();
        // convert to long
        java.sql.Timestamp peakTimeStamp = java.sql.Timestamp.valueOf(setDate + " " + setTime);
        long fractionPeakTimeStamp = peakTimeStamp.getTime();

        double[][] extractedData = PrawnRunFractionParser.parsedRunFractionData(runFraction);

        // within each row
        // index 0 = scannumber; followed by order of groups = 196  204 Backgrnd 206 207 208 238 248 254 270
        // each acquisition file contains 6 scans (for now)
        // each group contains totalCounts, 1-sigma, and totalCountsSBM
        // later we will give user interactive tools to pick them out
        ArrayList<double[]> backgroundAcquisitions = new ArrayList<>();
        ArrayList<double[]> peakAcquisitions = new ArrayList<>();
        ArrayList<double[]> peakAcquisitionsVariances = new ArrayList<>();

        for (int scan = 0; scan < extractedData.length; scan++) {
            // use these for now: 196  204  206 Pb207	Pb208	238 248 254 270
            double[] peakCounts = new double[9];
            double[] peakVariances = new double[9];
            peakAcquisitions.add(peakCounts);
            peakAcquisitionsVariances.add(peakVariances);

            peakCounts[0] = extractedData[scan][1];
            peakCounts[1] = extractedData[scan][4];
            peakCounts[2] = extractedData[scan][10];
            peakCounts[3] = extractedData[scan][13];
            peakCounts[4] = extractedData[scan][16];
            peakCounts[5] = extractedData[scan][19];
            peakCounts[6] = extractedData[scan][22];
            peakCounts[7] = extractedData[scan][25];
            peakCounts[8] = extractedData[scan][28];

            peakVariances[0] = Math.pow(extractedData[scan][2], 2);
            peakVariances[1] = Math.pow(extractedData[scan][5], 2);
            peakVariances[2] = Math.pow(extractedData[scan][11], 2);
            peakVariances[3] = Math.pow(extractedData[scan][14], 2);
            peakVariances[4] = Math.pow(extractedData[scan][17], 2);
            peakVariances[5] = Math.pow(extractedData[scan][20], 2);
            peakVariances[6] = Math.pow(extractedData[scan][23], 2);
            peakVariances[7] = Math.pow(extractedData[scan][26], 2);
            peakVariances[8] = Math.pow(extractedData[scan][29], 2);
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

        massSpec.processFractionRawRatiosII(//
                backgroundAcquisitions, peakAcquisitions, true, tripoliFraction);

        // supply calculated variances
        ((ShrimpSetupUPb) massSpec).initializeVariances(peakAcquisitionsVariances);

        tripoliFraction.shadeDataActiveMapLeft(0);
        System.out.println("\n**** SHRIMP FractionID  " + fractionID + " refMat? " + tripoliFraction.isStandard() + " <<<<<<<<<<<<<<<<<<\n");

        return tripoliFraction;
    }

}
