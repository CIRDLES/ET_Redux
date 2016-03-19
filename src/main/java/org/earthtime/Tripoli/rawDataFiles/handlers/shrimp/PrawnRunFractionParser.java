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
import org.earthtime.Tripoli.fitFunctions.algorithms.TukeyBiweight;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.dataDictionaries.PoissonLimitsCountLessThanEqual100;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public class PrawnRunFractionParser {

    /**
     * Returns 2D Double array of converted raw data Based on Simon Bodorkos
     * email 4.Feb.2016 interpretation of Squid code Corrected per Phil Main
     * 9.Feb.2016 email
     *
     * @param runFraction
     * @return
     */
    public static double[][] parsedRunFractionData(PrawnFile.Run runFraction) {
        int speciesMeasurementsPerScan = Integer.parseInt(runFraction.getPar().get(2).getValue());
        int scanCount = Integer.parseInt(runFraction.getPar().get(3).getValue());
        double deadTimeNanoseconds = Integer.parseInt(runFraction.getPar().get(4).getValue());
        // insert column 0 for scanNum number, then 3 columns per mass = total counts, 1 sig, total counts SBM
        double[][] scannedData = new double[scanCount][speciesMeasurementsPerScan * 3 + 1];

        for (int scanNum = 0; scanNum < scanCount; scanNum++) {
            scannedData[scanNum][0] = scanNum + 1; // 1-based in xml
            // there is one measurement per mass per scanNum
            List<PrawnFile.Run.Set.Scan.Measurement> measurements = runFraction.getSet().getScan().get(scanNum).getMeasurement();
            for (int speciesMeasurementIndex = 0; speciesMeasurementIndex < speciesMeasurementsPerScan; speciesMeasurementIndex++) {
                // handle peakMeasurements measurements
                String[] peakMeasurementsRaw = measurements.get(speciesMeasurementIndex).getData().get(0).getValue().split(",");
                int peakMeasurementsCount = peakMeasurementsRaw.length;
                double[] peakMeasurements = new double[peakMeasurementsCount];
                for (int i = 0; i < peakMeasurementsCount; i++) {
                    peakMeasurements[i] = Double.parseDouble(peakMeasurementsRaw[i]);
                }

                double median = TukeyBiweight.calculateMedian(peakMeasurements);
                // convert value to counts per second
                double countTimeSec = Double.parseDouble(runFraction.getRunTable().getEntry().get(speciesMeasurementIndex).getPar().get(4).getValue());
                double totalCounts;
                double totalCountsSigma;

                if (median > 100.0) {
                    ValueModel peakTukeyMean = TukeyBiweight.calculateTukeyBiweightMean("PEAK", 9.0, peakMeasurements);
                    // BV is variable used by Ludwig for Tukey Mean fo peak measurements
                    double bV = peakTukeyMean.getValue().doubleValue();
                    double bVcps = bV * peakMeasurementsCount / countTimeSec;
                    double bVcpsDeadTime = bVcps / (1.0 - bVcps * deadTimeNanoseconds / 1E9);

                    totalCounts = bVcpsDeadTime * countTimeSec;
                    double countsSigmaCandidate = Math.max(peakTukeyMean.getOneSigmaAbs().doubleValue(), Math.sqrt(bV));
                    totalCountsSigma = countsSigmaCandidate / Math.sqrt(peakMeasurementsCount) * bVcps * countTimeSec / bV;

                } else if (median >= 0.0) {

                    // remove the one element with largest residual if any.
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

                    double peakCountsPerSecond = peakMeanCounts * peakMeasurementsCount / countTimeSec;
                    double peakCountsPerSecondDeadTime = peakCountsPerSecond / (1.0 - peakCountsPerSecond * deadTimeNanoseconds / 1E9);

                    totalCounts = peakCountsPerSecondDeadTime * countTimeSec;

                    totalCountsSigma = 0.0;
                    if (peakMeanCounts > 0.0) {
                        totalCountsSigma
                                = Math.max(sigmaPeakCounts, poissonSigma) / Math.sqrt(countIncludedIntegrations) * peakCountsPerSecond * countTimeSec / peakMeanCounts;
                    }
                } else {
                    // set flag as this should be impossible for count data
                    totalCounts = -1.0;
                    totalCountsSigma = -1.0;
                }

                scannedData[scanNum][speciesMeasurementIndex * 3 + 1] = totalCounts;
                scannedData[scanNum][speciesMeasurementIndex * 3 + 2] = totalCountsSigma;

                // handle SBM measurements
                String[] sbmMeasurementsRaw = measurements.get(speciesMeasurementIndex).getData().get(1).getValue().split(",");
                int sbmMeasurementsCount = sbmMeasurementsRaw.length;
                double[] sbm = new double[sbmMeasurementsCount];
                for (int i = 0; i < sbmMeasurementsCount; i++) {
                    sbm[i] = Double.parseDouble(sbmMeasurementsRaw[i]);
                }
                ValueModel sbmTukeyMean = TukeyBiweight.calculateTukeyBiweightMean("SBM", 6.0, sbm);
                double totalCountsSBM = sbmMeasurementsCount * sbmTukeyMean.getValue().doubleValue();
                scannedData[scanNum][speciesMeasurementIndex * 3 + 3] = totalCountsSBM;
            }
        }

        return scannedData;
    }

    public static double[] calculateTotalPerSpeciesCPS(PrawnFile.Run runFraction, double[][] extractedData, int backgroundIndex) {
        // Calculate Total CPS per Species = Step 2 of Development for SHRIMP 
        // (see wiki: https://github.com/CIRDLES/ET_Redux/wiki/Development-for-SHRIMP:-Step-2)
        // March 2016 - making some serious hard-coded assumptions for the time-being
        // thus note: 
        // within each row
        // index 0 = scannumber; followed by order of groups = 196  204 Backgrnd 206 207 208 238 248 254 270
        // each acquisition file contains 6 scans (for now)
        // each group contains totalCounts, 1-sigma, and totalCountsSBM

        double sbmZeroCps = Double.parseDouble(runFraction.getPar().get(5).getValue());

        int speciesMeasurementsPerScan = Integer.parseInt(runFraction.getPar().get(2).getValue());
        List<PrawnFile.Run.RunTable.Entry> runTableEntries = runFraction.getRunTable().getEntry();
        double[] countTimeSec = new double[speciesMeasurementsPerScan];
        for (int i = 0; i < runTableEntries.size(); i++) {
            countTimeSec[i] = Double.parseDouble(runTableEntries.get(i).getPar().get(4).getValue());
        }

        // copy data
        double[][] correctedData = new double[extractedData.length][extractedData[0].length];
        for (int row = 0; row < correctedData.length; row++) {
            for (int col = 0; col < extractedData[row].length; col++) {
                correctedData[row][col] = extractedData[row][col];
            }
        }

        double[] backgroundCpsArray = new double[correctedData.length];

        double sumBackgroundCps = 0.0;
        for (int scanNum = 0; scanNum < correctedData.length; scanNum++) {
            for (int speciesMeasurementIndex = 0; speciesMeasurementIndex < speciesMeasurementsPerScan; speciesMeasurementIndex++) {
                // calculate PeakCps 
                correctedData[scanNum][speciesMeasurementIndex * 3 + 1] /= countTimeSec[speciesMeasurementIndex];
                // calculate corrected SBMCps 
                correctedData[scanNum][speciesMeasurementIndex * 3 + 3] /= countTimeSec[speciesMeasurementIndex];
                correctedData[scanNum][speciesMeasurementIndex * 3 + 3] -= sbmZeroCps;

                if (speciesMeasurementIndex == backgroundIndex) {
                    backgroundCpsArray[scanNum] = correctedData[scanNum][speciesMeasurementIndex * 3 + 1];
                    sumBackgroundCps += correctedData[scanNum][speciesMeasurementIndex * 3 + 1];
                }
            }
        }

        // determine backgroundCps if background species exists
        double backgroundCps = 0.0;
        if (backgroundIndex >= 0) {
            backgroundCps = sumBackgroundCps / correctedData.length;

            if (backgroundCps >= 10.0) {
                // recalculate
                backgroundCps = TukeyBiweight.calculateTukeyBiweightMean("BACK", 9.0, backgroundCpsArray).getValue().doubleValue();
            }
        }

        // background correct the peaks with fractional error and calculate total cps for peaks
        double[] sumOfCorrectedPeaks = new double[speciesMeasurementsPerScan];
        for (int scanNum = 0; scanNum < correctedData.length; scanNum++) {
            for (int speciesMeasurementIndex = 0; speciesMeasurementIndex < speciesMeasurementsPerScan; speciesMeasurementIndex++) {
                if (speciesMeasurementIndex != backgroundIndex) {
                    // correct PeakCps 
                    correctedData[scanNum][speciesMeasurementIndex * 3 + 1] -= backgroundCps;
                    sumOfCorrectedPeaks[speciesMeasurementIndex] += correctedData[scanNum][speciesMeasurementIndex * 3 + 1];
                    // calculate fractional error
                    double absNetPeakCps = Math.abs(correctedData[scanNum][speciesMeasurementIndex * 3 + 1]);
                    if (absNetPeakCps > 1.0e-6) {
                        double calcVariance
                                = absNetPeakCps + (Math.abs(backgroundCps) * Math.pow(countTimeSec[speciesMeasurementIndex] / countTimeSec[backgroundIndex], 2));
                        correctedData[scanNum][speciesMeasurementIndex * 3 + 2]
                                = Math.sqrt(calcVariance) / absNetPeakCps / countTimeSec[speciesMeasurementIndex];

                    } else {
                        correctedData[scanNum][speciesMeasurementIndex * 3 + 2] = 1.0;
                    }
                } 
            }
        }
        double[] totalCps = new double[speciesMeasurementsPerScan];
        for (int speciesMeasurementIndex = 0; speciesMeasurementIndex < speciesMeasurementsPerScan; speciesMeasurementIndex++) {
            // calculate total cps
            // this has the effect of setting totalCps[backgroundIndex] to backgroundCps
            totalCps[speciesMeasurementIndex] = (sumOfCorrectedPeaks[speciesMeasurementIndex] / correctedData.length) + backgroundCps;
        }

        return totalCps;

    }

    /**
     * Driver to test results
     *
     * @param args
     */
    public static void main(String[] args) {

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

//                System.out.println("\n" + runFraction.getPar().get(0).getValue() + "  ***********************\n");
                double[][] scannedData = parsedRunFractionData(runFraction);
                double[] totalCps = calculateTotalPerSpeciesCPS(runFraction, scannedData, 2);

//                for (double[] scannedData1 : scannedData) {
//                    for (int j = 0; j < scannedData1.length; j++) {
//                        System.out.print(scannedData1[j]);
//                        if (j < (scannedData1.length - 1)) {
//                            System.out.print(",");
//                        }
//                    }
//                    System.out.print("\n");
//                }
                System.out.print(runFraction.getPar().get(0).getValue() + ",");

                for (int j = 0; j < totalCps.length; j++) {
                    System.out.print(totalCps[j]);
                    if (j < (totalCps.length - 1)) {
                        System.out.print(",");
                    }
                }
                System.out.print("\n");
            } // end of fractions loop

        } catch (JAXBException jAXBException) {
            System.out.println(jAXBException.getMessage());
        }
    }
}
