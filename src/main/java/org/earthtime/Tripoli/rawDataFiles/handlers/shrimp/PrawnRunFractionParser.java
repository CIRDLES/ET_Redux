/*
 * Copyright 2006-2016 CIRDLES.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.earthtime.Tripoli.rawDataFiles.handlers.shrimp;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.cirdles.shrimp.PrawnFile;
import org.cirdles.shrimp.PrawnFile.Run.Set;
import org.cirdles.shrimp.PrawnFile.Run.Set.Scan;
import org.cirdles.shrimp.PrawnFile.Run.Set.Scan.Measurement;
import org.earthtime.Tripoli.fitFunctions.algorithms.TukeyBiweight;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.dataDictionaries.PoissonLimitsCountLessThanEqual100;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public class PrawnRunFractionParser {

    private static final int HARD_WIRED_INDEX_OF_BACKGROUND = 2;

    private static double[][] extractedRunData;
    private static int nSpecies;
    private static int nScans;
    private static double deadTimeNanoseconds;
    private static double sbmZeroCps;
    private static List<PrawnFile.Run.RunTable.Entry> runTableEntries;
    private static List<PrawnFile.Run.Set.Scan> scans;
    private static double[] countTimeSec;
    private static double[][] timeStampSec;
    private static double[][] netPkCps;
    private static double[][] sbmCps;
    private static double[][] pkFCps;

    public static void processRunFraction(PrawnFile.Run runFraction) {

        prepareRunFractionMetaData(runFraction);
        parseRunFractionData();
    }

    private static void prepareRunFractionMetaData(PrawnFile.Run runFraction) {
        nSpecies = Integer.parseInt(runFraction.getPar().get(2).getValue());
        nScans = Integer.parseInt(runFraction.getPar().get(3).getValue());
        deadTimeNanoseconds = Integer.parseInt(runFraction.getPar().get(4).getValue());
        sbmZeroCps = Double.parseDouble(runFraction.getPar().get(5).getValue());
        runTableEntries = runFraction.getRunTable().getEntry();
        scans = runFraction.getSet().getScan();

        countTimeSec = new double[nSpecies];
        for (int i = 0; i < runTableEntries.size(); i++) {
            countTimeSec[i] = Double.parseDouble(runTableEntries.get(i).getPar().get(4).getValue());
        }

        timeStampSec = new double[nScans][nSpecies];
        netPkCps = new double[nScans][nSpecies];
        sbmCps = new double[nScans][nSpecies];
        pkFCps = new double[nScans][nSpecies];

    }

    /**
     * Returns 2D Double array of converted raw data Based on Simon Bodorkos
     * email 4.Feb.2016 interpretation of Squid code Corrected per Phil Main
     * 9.Feb.2016 email
     *
     * @param runFraction
     * @return
     */
    private static void parseRunFractionData() {
        // insert column 0 for scanNum number, then 3 columns per mass = total counts, 1 sig, total counts SBM
        extractedRunData = new double[nScans][nSpecies * 3 + 1];

        for (int scanNum = 0; scanNum < nScans; scanNum++) {
            extractedRunData[scanNum][0] = scanNum + 1; // 1-based in xml
            // there is one measurement per mass per scanNum
            List<PrawnFile.Run.Set.Scan.Measurement> measurements = scans.get(scanNum).getMeasurement();
            for (int speciesMeasurementIndex = 0; speciesMeasurementIndex < nSpecies; speciesMeasurementIndex++) {
                // record the time_stamp_sec
                timeStampSec[scanNum][speciesMeasurementIndex]
                        = Double.parseDouble(measurements.get(speciesMeasurementIndex).getPar().get(2).getValue());
                // handle peakMeasurements measurements
                String[] peakMeasurementsRaw = measurements.get(speciesMeasurementIndex).getData().get(0).getValue().split(",");
                int peakMeasurementsCount = peakMeasurementsRaw.length;
                double[] peakMeasurements = new double[peakMeasurementsCount];
                for (int i = 0; i < peakMeasurementsCount; i++) {
                    peakMeasurements[i] = Double.parseDouble(peakMeasurementsRaw[i]);
                }

                double median = TukeyBiweight.calculateMedian(peakMeasurements);
                double totalCounts;
                double totalCountsSigma;

                if (median > 100.0) {
                    ValueModel peakTukeyMean = TukeyBiweight.calculateTukeyBiweightMean("PEAK", 9.0, peakMeasurements);
                    // BV is variable used by Ludwig for Tukey Mean fo peak measurements
                    double bV = peakTukeyMean.getValue().doubleValue();
                    double bVcps = bV * peakMeasurementsCount / countTimeSec[speciesMeasurementIndex];
                    double bVcpsDeadTime = bVcps / (1.0 - bVcps * deadTimeNanoseconds / 1E9);

                    totalCounts = bVcpsDeadTime * countTimeSec[speciesMeasurementIndex];
                    double countsSigmaCandidate = Math.max(peakTukeyMean.getOneSigmaAbs().doubleValue(), Math.sqrt(bV));
                    totalCountsSigma = countsSigmaCandidate / Math.sqrt(peakMeasurementsCount) * bVcps * countTimeSec[speciesMeasurementIndex] / bV;

                } else if (median >= 0.0) {

                    // remove the one element with first occurrence of largest residual if any.
                    int maxResidualIndex = PoissonLimitsCountLessThanEqual100.determineIndexOfValueWithLargestResidual(median, peakMeasurements);
                    double sumX = 0.0;
                    double sumXsquared = 0.0;
                    for (int i = 0; i < peakMeasurementsCount; i++) {
                        if (i != maxResidualIndex) {
                            sumX += peakMeasurements[i];
                            sumXsquared += peakMeasurements[i] * peakMeasurements[i];
                        }
                    }

                    int countIncludedIntegrations = (maxResidualIndex == -1) ? peakMeasurementsCount : peakMeasurementsCount - 1;
                    double peakMeanCounts = sumX / countIncludedIntegrations;
                    double poissonSigma = Math.sqrt(peakMeanCounts);
                    double sigmaPeakCounts = Math.sqrt((sumXsquared - (sumX * sumX / countIncludedIntegrations)) / (countIncludedIntegrations - 1));

                    double peakCountsPerSecond = peakMeanCounts * peakMeasurementsCount / countTimeSec[speciesMeasurementIndex];
                    double peakCountsPerSecondDeadTime = peakCountsPerSecond / (1.0 - peakCountsPerSecond * deadTimeNanoseconds / 1E9);

                    totalCounts = peakCountsPerSecondDeadTime * countTimeSec[speciesMeasurementIndex];

                    totalCountsSigma = 0.0;
                    if (peakMeanCounts > 0.0) {
                        totalCountsSigma
                                = Math.max(sigmaPeakCounts, poissonSigma) / Math.sqrt(countIncludedIntegrations) * peakCountsPerSecond * countTimeSec[speciesMeasurementIndex] / peakMeanCounts;
                    }
                } else {
                    // set flag as this should be impossible for count data
                    totalCounts = -1.0;
                    totalCountsSigma = -1.0;
                }

                extractedRunData[scanNum][speciesMeasurementIndex * 3 + 1] = totalCounts;
                extractedRunData[scanNum][speciesMeasurementIndex * 3 + 2] = totalCountsSigma;

                // handle SBM measurements
                String[] sbmMeasurementsRaw = measurements.get(speciesMeasurementIndex).getData().get(1).getValue().split(",");
                int sbmMeasurementsCount = sbmMeasurementsRaw.length;
                double[] sbm = new double[sbmMeasurementsCount];
                for (int i = 0; i < sbmMeasurementsCount; i++) {
                    sbm[i] = Double.parseDouble(sbmMeasurementsRaw[i]);
                }
                ValueModel sbmTukeyMean = TukeyBiweight.calculateTukeyBiweightMean("SBM", 6.0, sbm);
                double totalCountsSBM = sbmMeasurementsCount * sbmTukeyMean.getValue().doubleValue();
                extractedRunData[scanNum][speciesMeasurementIndex * 3 + 3] = totalCountsSBM;
            }
        }
    }

    public static double[] calculateTotalPerSpeciesCPS() {
        // Calculate Total CPS per Species = Step 2 of Development for SHRIMP 
        // (see wiki: https://github.com/CIRDLES/ET_Redux/wiki/Development-for-SHRIMP:-Step-2)

        double[][] pkCps = new double[nScans][nSpecies];
        double[] backgroundCpsArray = new double[nScans];

        double sumBackgroundCps = 0.0;
        for (int scanNum = 0; scanNum < nScans; scanNum++) {
            for (int speciesMeasurementIndex = 0; speciesMeasurementIndex < nSpecies; speciesMeasurementIndex++) {
                // calculate PeakCps 
                pkCps[scanNum][speciesMeasurementIndex] = extractedRunData[scanNum][speciesMeasurementIndex * 3 + 1] / countTimeSec[speciesMeasurementIndex];
                // calculate corrected (by sbmZeroCps) SBMCps 
                sbmCps[scanNum][speciesMeasurementIndex] = (extractedRunData[scanNum][speciesMeasurementIndex * 3 + 3] / countTimeSec[speciesMeasurementIndex]) - sbmZeroCps;

                if (speciesMeasurementIndex == HARD_WIRED_INDEX_OF_BACKGROUND) {
                    backgroundCpsArray[scanNum] = pkCps[scanNum][speciesMeasurementIndex];
                    sumBackgroundCps += pkCps[scanNum][speciesMeasurementIndex];
                }
            }
        }

        // determine backgroundCps if background species exists
        double backgroundCps = 0.0;
        if (HARD_WIRED_INDEX_OF_BACKGROUND >= 0) {
            backgroundCps = sumBackgroundCps / nScans;

            if (backgroundCps >= 10.0) {
                // recalculate
                backgroundCps = TukeyBiweight.calculateTukeyBiweightMean("BACK", 9.0, backgroundCpsArray).getValue().doubleValue();
            }
        }

        // background correct the peaks with fractional error and calculate total cps for peaks
        double[] sumOfCorrectedPeaks = new double[nSpecies];
        for (int scanNum = 0; scanNum < nScans; scanNum++) {
            for (int speciesMeasurementIndex = 0; speciesMeasurementIndex < nSpecies; speciesMeasurementIndex++) {
                if (speciesMeasurementIndex != HARD_WIRED_INDEX_OF_BACKGROUND) {
                    // correct PeakCps to NetPkCps inside correctedData (note translation of matrix)
                    netPkCps[scanNum][speciesMeasurementIndex] =  pkCps[scanNum][speciesMeasurementIndex]- backgroundCps;
                    sumOfCorrectedPeaks[speciesMeasurementIndex] += netPkCps[scanNum][speciesMeasurementIndex];
                    // calculate fractional error
                    double absNetPeakCps = netPkCps[scanNum][speciesMeasurementIndex];
                    if (absNetPeakCps > 1.0e-6) {
                        double calcVariance
                                = absNetPeakCps + (Math.abs(backgroundCps) * Math.pow(countTimeSec[speciesMeasurementIndex] / countTimeSec[HARD_WIRED_INDEX_OF_BACKGROUND], 2));
                        pkFCps[scanNum][speciesMeasurementIndex]
                                = Math.sqrt(calcVariance) / absNetPeakCps / countTimeSec[speciesMeasurementIndex];
                    } else {
                        pkFCps[scanNum][speciesMeasurementIndex] = 1.0;
                    }
                }
            }
        }

        double[] totalCps = new double[nSpecies];
        for (int speciesMeasurementIndex = 0; speciesMeasurementIndex < nSpecies; speciesMeasurementIndex++) {
            // calculate total cps
            // this has the effect of setting totalCps[backgroundIndex] to backgroundCps
            totalCps[speciesMeasurementIndex] = (sumOfCorrectedPeaks[speciesMeasurementIndex] / nScans) + backgroundCps;
        }

        return totalCps;
    }

    /**
     * Driver to test results
     *
     * @param args
     */
    public static void main(String[] args) {

//        reportForSimon();
        // local copy of file - use prawnFileXML in place of prawnFileURL below
//        File prawnFileXML = new File("/Users/sbowring/Documents/Development_XSD/100142_G6147_10111109.43 10.33.37 AM.xml");
        // remote copy of example file
        java.net.URL prawnFileURL = null;
        try {
            prawnFileURL = new URL("https://raw.githubusercontent.com/bowring/XSD/master/SHRIMP/EXAMPLE_100142_G6147_10111109.43_10.33.37%20AM.xml");
        } catch (MalformedURLException malformedURLException) {
            System.out.println(malformedURLException.getMessage());
        }

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(PrawnFile.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            PrawnFile prawnFile = (PrawnFile) jaxbUnmarshaller.unmarshal(prawnFileURL);

            for (int f = 0; f < prawnFile.getRuns(); f++) {
                PrawnFile.Run runFraction = prawnFile.getRun().get(f);

                if (runFraction.getPar().get(0).getValue().startsWith("097.Z.1.1.1")) {
//                System.out.println("\n" + runFraction.getPar().get(0).getValue() + "  ***********************\n");
                    processRunFraction(runFraction);
                    double[][] scannedData = extractedRunData;
                    double[] totalCps = calculateTotalPerSpeciesCPS();

                    for (double[] scannedData1 : scannedData) {
//                        System.out.print(scannedData1[16] + ",  " + scannedData1[17]);
                        for (int j = 0; j < scannedData1.length; j++) {
                            System.out.print(scannedData1[j]);
                            if (j < (scannedData1.length - 1)) {
                                System.out.print(",");
                            }
                        }
                        System.out.print("\n");
                    }
//                    if (runFraction.getPar().get(0).getValue().startsWith("097.Z")) {
                    System.out.print(runFraction.getPar().get(0).getValue() + ", ");
//                    System.out.print(totalCps[5]);
                    for (int j = 0; j < totalCps.length; j++) {
                        System.out.print(totalCps[j]);
                        if (j < (totalCps.length - 1)) {
                            System.out.print(",");
                        }
                    }
                    System.out.print("\n");
//                    }
                }
            } // end of fractions loop

        } catch (JAXBException jAXBException) {
            System.out.println(jAXBException.getMessage());
        }
    }

    public static void reportForSimon() {
        java.net.URL prawnFileURL = null;
        try {
            prawnFileURL = new URL("https://raw.githubusercontent.com/bowring/XSD/master/SHRIMP/EXAMPLE_100142_G6147_10111109.43_10.33.37%20AM.xml");
        } catch (MalformedURLException malformedURLException) {
            System.out.println(malformedURLException.getMessage());
        }

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(PrawnFile.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            PrawnFile prawnFile = (PrawnFile) jaxbUnmarshaller.unmarshal(prawnFileURL);

            // print headers
            System.out.print("Spot, Scan#, Time, ");//196,  204, BKGRND, 206, 207 208 238 248 254 270");
            PrawnFile.Run firstFraction = prawnFile.getRun().get(0);
            for (int i = 0; i < 10; i++) {
                String speciesName = firstFraction.getRunTable().getEntry().get(i).getPar().get(0).getValue();
                for (int j = 0; j < 10; j++) {
                    System.out.print(speciesName + "." + (j + 1) + ", ");
                }
            }
            System.out.println();

            for (int f = 0; f < prawnFile.getRuns(); f++) {
                PrawnFile.Run runFraction = prawnFile.getRun().get(f);
                Set mySet = runFraction.getSet();
                for (int scan = 0; scan < 6; scan++) {
                    Scan myScan = mySet.getScan().get(scan);
                    System.out.print(runFraction.getPar().get(0).getValue() //
                            + ", " + (scan + 1) + ", " //
                            + mySet.getPar().get(0).getValue() + " " + mySet.getPar().get(1).getValue() + ", ");
                    for (int species = 0; species < 10; species++) {
                        Measurement mySpecies = myScan.getMeasurement().get(species);
                        System.out.print(mySpecies.getData().get(0).getValue() + ", ");
                    }

                    System.out.println();
                }

            } // end of fractions loop

        } catch (JAXBException jAXBException) {
            System.out.println(jAXBException.getMessage());
        }
    }

    /**
     * @return the extractedRunData
     */
    public static double[][] getExtractedRunData() {
        return extractedRunData;
    }
}
