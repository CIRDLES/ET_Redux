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
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.cirdles.shrimp.PrawnFile;
import org.cirdles.shrimp.PrawnFile.Run.Set.Scan.Measurement;
import org.earthtime.Tripoli.fitFunctions.algorithms.TukeyBiweight;
import org.earthtime.Tripoli.fractions.TripoliFraction;
import org.earthtime.Tripoli.rawDataFiles.handlers.AbstractRawDataFileHandler;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.utilities.FileHelper;

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
        NAME = "Shrimp Prawn File '.xml'";
        aboutInfo = "Details: This is the Prawn xml file form the Shrimp. ";
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

        rawDataFile = FileHelper.AllPlatformGetFolder(dialogTitle, tripoliRawDataFolder);
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
            prawnFile = (PrawnFile) jaxbUnmarshaller.unmarshal(rawDataFile);//  url);

            // assume we are golden   
            // a 'run' is an analysis or fraction
            for (int f = ignoreFirstFractions; f < prawnFile.getRuns(); f++) {

                if (loadDataTask.isCancelled()) {
                    break;
                }
                loadDataTask.firePropertyChange("progress", 0, ((100 * f) / prawnFile.getRuns()));

                PrawnFile.Run runFraction = prawnFile.getRun().get(f);

                processRunFraction(runFraction);

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

    private void processRunFraction(PrawnFile.Run runFraction) {
        String fractionID = runFraction.getPar().get(0).getValue();

        // needs to be more robust
        boolean isStandard = (fractionID.compareToIgnoreCase(rawDataFileTemplate.getStandardIDs()[0]) == 0);

        double[][] extractedData;

//        try {
//            extractedData = scannedData(runFraction);
//
//            // within each row
//            // index 0 = scannumber; 1 = time stamp; 2 = ACF; followed by order of groups = 202  204  206	Pb207	Pb208	Th232	U238
//            // each acquisition file contains background followed by peak followed by background
//            // initial solution is to hard wire the first background and peak per Gehrels
//            // later we will give user interactive tools to pick them out
//            ArrayList<double[]> backgroundAcquisitions = new ArrayList<>();
//            ArrayList<double[]> peakAcquisitions = new ArrayList<>();
//
//            int hardwiredEndOfBackground = 24;
//            // process time stamp from first scan as time stamp of file and background
//            long fractionBackgroundTimeStamp = calculateTimeStamp(extractedData[0][1]);
//            // process time stamp of first peak reading
//            long fractionPeakTimeStamp = calculateTimeStamp(extractedData[hardwiredEndOfBackground + 1][1]);
//
//            for (int i = rawDataFileTemplate.getBlockStartOffset(); i < rawDataFileTemplate.getBlockSize(); i++) {
//                if (rawDataFileTemplate instanceof LaserchronElementII_RawDataTemplate_A) {
//                    processIntensities_A(i, hardwiredEndOfBackground, backgroundAcquisitions, peakAcquisitions, extractedData[i]);
//                } else if (rawDataFileTemplate instanceof LaserchronElementII_RawDataTemplate_B) {
//                    processIntensities_B(i, hardwiredEndOfBackground, backgroundAcquisitions, peakAcquisitions, extractedData[i]);
//                } else if (rawDataFileTemplate instanceof LaserchronElementII_RawDataTemplate_C) {
//                    processIntensities_C(i, hardwiredEndOfBackground, backgroundAcquisitions, peakAcquisitions, extractedData[i]);
//                }
//            }  // i loop
//
//            TripoliFraction tripoliFraction
//                    = new TripoliFraction( //
//                            fractionID, //
//                            massSpec.getCommonLeadCorrectionHighestLevel(), //
//                            isStandard,
//                            fractionBackgroundTimeStamp, //
//                            fractionPeakTimeStamp,
//                            peakAcquisitions.size());
//
//            SortedSet<DataModelInterface> rawRatios = massSpec.rawRatiosFactoryRevised();
//
//            tripoliFraction.setRawRatios(rawRatios);
//
//            massSpec.setCountOfAcquisitions(peakAcquisitions.size());
//
//            massSpec.processFractionRawRatiosII(//
//                    backgroundAcquisitions, peakAcquisitions, isStandard, usingFullPropagation, tripoliFraction);
//
//            tripoliFraction.shadeDataActiveMapLeft(leftShadeCount);
//            System.out.println("\n**** Element II FractionID  " + fractionID);
//
//            myTripoliFractions.add(tripoliFraction);
//
//        } catch (PyException pyException) {
//            System.out.println("bad read of fraction " + analysisFiles[f].getName() + " message = " + pyException.getMessage());
//        }
    }

    /**
     * Per Simon Bodorkos email 4.Feb.2016
     *
     * @param runFraction
     * @return
     */
    private static double[][] scannedData(PrawnFile.Run runFraction) {
        int measurementsPerScan = Integer.parseInt(runFraction.getPar().get(2).getValue());
        int scanCount = Integer.parseInt(runFraction.getPar().get(3).getValue());
        double deadTimeNanoseconds = Integer.parseInt(runFraction.getPar().get(4).getValue());
        // insert column 0 for scan number, then 3 columns per mass = total counts, 1 sig, total counts SBM
        double[][] scannedData = new double[scanCount][measurementsPerScan * 3 + 1];

        for (int s = 0; s < scanCount; s++) {
            scannedData[s][0] = s + 1; // 1-based in xml
            // there is one measurement per mass per scan
            List<Measurement> measurements = runFraction.getSet().getScan().get(s).getMeasurement();
            for (int m = 0; m < measurementsPerScan; m++) {
                // handle peak measurements
                String[] tenPeakMeasurements = measurements.get(m).getData().get(0).getValue().split(",");
                int peakCount = tenPeakMeasurements.length;
                double[] peak = new double[peakCount];
                for (int i = 0; i < peakCount; i++) {
                    peak[i] = Double.parseDouble(tenPeakMeasurements[i]);
                }
                double median = TukeyBiweight.calculateMedian(peak);
                double totalCounts;
                double totalCountsSigma;
                if (median > 100.0) {
                    ValueModel peakTukeyMean = TukeyBiweight.calculateTukeyBiweightMean("PEAK", 9, peak);
                    // convert value to counts per second
                    double countTimeSec = Double.parseDouble(runFraction.getRunTable().getEntry().get(m).getPar().get(4).getValue());
                    double bvCps = peakTukeyMean.getValue().doubleValue() * peakCount / countTimeSec;
                    double bvCpsDeadTime = bvCps / (1.0 - bvCps * deadTimeNanoseconds / 1E9);

                    totalCounts = bvCpsDeadTime * countTimeSec;
                    double countsSigmaCandidate = Math.max(peakTukeyMean.getOneSigmaAbs().doubleValue(), Math.sqrt(peakTukeyMean.getValue().doubleValue()));
                    totalCountsSigma = countsSigmaCandidate / Math.sqrt(peakCount) * totalCounts / peakTukeyMean.getValue().doubleValue();
                } else {
                    totalCounts = 0.0;
                    totalCountsSigma = 0.0;
                }
                scannedData[s][m * 3 + 1] = totalCounts;
                scannedData[s][m * 3 + 2] = totalCountsSigma;

                // handle SBM measurements
                String[] tenSBMMeasurements = measurements.get(m).getData().get(1).getValue().split(",");
                int sbmCount = tenSBMMeasurements.length;
                double[] sbm = new double[sbmCount];
                for (int i = 0; i < sbmCount; i++) {
                    sbm[i] = Double.parseDouble(tenSBMMeasurements[i]);
                }
                ValueModel sbmTukeyMean = TukeyBiweight.calculateTukeyBiweightMean("SBM", 6, sbm);
                double totalCountsSBM = sbmCount * sbmTukeyMean.getValue().doubleValue();
                scannedData[s][m * 3 + 3] = totalCountsSBM;
            }
        }

        return scannedData;
    }

    public static void main(String[] args) {
        File prawnFileXML = new File("/Users/sbowring/Documents/Development_XSD/100142_G6147_10111109.43 10.33.37 AM.xml");

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(PrawnFile.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            PrawnFile prawnFile = (PrawnFile) jaxbUnmarshaller.unmarshal(prawnFileXML);

            for (int f = 0; f < prawnFile.getRuns(); f++) {
                PrawnFile.Run runFraction = prawnFile.getRun().get(f);

                System.out.println("\n" + runFraction.getPar().get(0).getValue() + "  ***********************\n");
                double[][] scannedData = scannedData(runFraction);

                for (int i = 0; i < scannedData.length; i++) {
                    for (int j = 0; j < scannedData[i].length; j++) {
                        System.out.print(scannedData[i][j]);
                        if (j < (scannedData[i].length - 1)){
                            System.out.print(",");
                        }
                    }
                    System.out.print("\n");
                }


            } // end of fractions loop

        } catch (JAXBException jAXBException) {
        }
    }
}
