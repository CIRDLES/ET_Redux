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
        int measurementsPerScan = Integer.parseInt(runFraction.getPar().get(2).getValue());
        int scanCount = Integer.parseInt(runFraction.getPar().get(3).getValue());
        double deadTimeNanoseconds = Integer.parseInt(runFraction.getPar().get(4).getValue());
        // insert column 0 for scan number, then 3 columns per mass = total counts, 1 sig, total counts SBM
        double[][] scannedData = new double[scanCount][measurementsPerScan * 3 + 1];

        for (int s = 0; s < scanCount; s++) {
            scannedData[s][0] = s + 1; // 1-based in xml
            // there is one measurement per mass per scan
            List<PrawnFile.Run.Set.Scan.Measurement> measurements = runFraction.getSet().getScan().get(s).getMeasurement();
            for (int m = 0; m < measurementsPerScan; m++) {
                // handle peakMeasurements measurements
                String[] peakMeasurementsRaw = measurements.get(m).getData().get(0).getValue().split(",");
                int peakMeasurementsCount = peakMeasurementsRaw.length;
                double[] peakMeasurements = new double[peakMeasurementsCount];
                for (int i = 0; i < peakMeasurementsCount; i++) {
                    peakMeasurements[i] = Double.parseDouble(peakMeasurementsRaw[i]);
                }
                
                double median = TukeyBiweight.calculateMedian(peakMeasurements);
                // convert value to counts per second
                double countTimeSec = Double.parseDouble(runFraction.getRunTable().getEntry().get(m).getPar().get(4).getValue());
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
                        totalCountsSigma = Math.max(sigmaPeakCounts, poissonSigma) / Math.sqrt(countIncludedIntegrations) * peakCountsPerSecond * countTimeSec / peakMeanCounts;
                    }
                } else {
                    // set flag as this should be impossible for count data
                    totalCounts = -1.0;
                    totalCountsSigma = -1.0;
                }

                scannedData[s][m * 3 + 1] = totalCounts;
                scannedData[s][m * 3 + 2] = totalCountsSigma;

                // handle SBM measurements
                String[] sbmMeasurementsRaw = measurements.get(m).getData().get(1).getValue().split(",");
                int sbmMeasurementsCount = sbmMeasurementsRaw.length;
                double[] sbm = new double[sbmMeasurementsCount];
                for (int i = 0; i < sbmMeasurementsCount; i++) {
                    sbm[i] = Double.parseDouble(sbmMeasurementsRaw[i]);
                }
                ValueModel sbmTukeyMean = TukeyBiweight.calculateTukeyBiweightMean("SBM", 6.0, sbm);
                double totalCountsSBM = sbmMeasurementsCount * sbmTukeyMean.getValue().doubleValue();
                scannedData[s][m * 3 + 3] = totalCountsSBM;
            }
        }

        return scannedData;
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

                System.out.println("\n" + runFraction.getPar().get(0).getValue() + "  ***********************\n");
                double[][] scannedData = parsedRunFractionData(runFraction);

                for (double[] scannedData1 : scannedData) {
                    for (int j = 0; j < scannedData1.length; j++) {
                        System.out.print(scannedData1[j]);
                        if (j < (scannedData1.length - 1)) {
                            System.out.print(",");
                        }
                    }
                    System.out.print("\n");
                }
            } // end of fractions loop

        } catch (JAXBException jAXBException) {
            System.out.println(jAXBException.getMessage());
        }
    }
}
