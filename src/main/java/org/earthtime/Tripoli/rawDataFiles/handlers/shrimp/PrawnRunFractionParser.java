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

import com.google.common.collect.HashBiMap;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
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
import org.earthtime.dataDictionaries.IsotopeNames;
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
    private static double[][] pkFerr;
    private static double[] totalCps;
    private static com.google.common.collect.BiMap<Integer, IsotopeNames> speciesToIndexBiMap;
    private static List<IsotopeRatioModelSHRIMP> isotopicRatios;

    public static void processRunFraction(PrawnFile.Run runFraction) {

        prepareRunFractionMetaData(runFraction);
        parseRunFractionData();
        calculateTotalPerSpeciesCPS();
        calculateIsotopicRatios(true);
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
        pkFerr = new double[nScans][nSpecies];

        // april 2016 hard-wired for prototype **********************************
        speciesToIndexBiMap = HashBiMap.create();
        speciesToIndexBiMap.put(0, IsotopeNames.Zr2O196);
        speciesToIndexBiMap.put(1, IsotopeNames.Pb204);
        speciesToIndexBiMap.put(2, IsotopeNames.BKGND);
        speciesToIndexBiMap.put(3, IsotopeNames.Pb206);
        speciesToIndexBiMap.put(4, IsotopeNames.Pb207);
        speciesToIndexBiMap.put(5, IsotopeNames.Pb208);
        speciesToIndexBiMap.put(6, IsotopeNames.U238);
        speciesToIndexBiMap.put(7, IsotopeNames.ThO248);
        speciesToIndexBiMap.put(8, IsotopeNames.UO254);
        speciesToIndexBiMap.put(9, IsotopeNames.UO270);

        isotopicRatios = new ArrayList<>();
        isotopicRatios.add(new IsotopeRatioModelSHRIMP(IsotopeNames.Pb204, IsotopeNames.Pb206));
        isotopicRatios.add(new IsotopeRatioModelSHRIMP(IsotopeNames.Pb207, IsotopeNames.Pb206));
        isotopicRatios.add(new IsotopeRatioModelSHRIMP(IsotopeNames.Pb208, IsotopeNames.Pb206));
        isotopicRatios.add(new IsotopeRatioModelSHRIMP(IsotopeNames.U238, IsotopeNames.Zr2O196));
        isotopicRatios.add(new IsotopeRatioModelSHRIMP(IsotopeNames.Pb206, IsotopeNames.U238));
        isotopicRatios.add(new IsotopeRatioModelSHRIMP(IsotopeNames.UO254, IsotopeNames.U238));
        isotopicRatios.add(new IsotopeRatioModelSHRIMP(IsotopeNames.ThO248, IsotopeNames.UO254));
        isotopicRatios.add(new IsotopeRatioModelSHRIMP(IsotopeNames.Pb206, IsotopeNames.UO270));
        isotopicRatios.add(new IsotopeRatioModelSHRIMP(IsotopeNames.UO270, IsotopeNames.UO254));
        isotopicRatios.add(new IsotopeRatioModelSHRIMP(IsotopeNames.Pb206, IsotopeNames.UO254));

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

    private static void calculateTotalPerSpeciesCPS() {
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
                    netPkCps[scanNum][speciesMeasurementIndex] = pkCps[scanNum][speciesMeasurementIndex] - backgroundCps;
                    sumOfCorrectedPeaks[speciesMeasurementIndex] += netPkCps[scanNum][speciesMeasurementIndex];
                    // calculate fractional error
                    double absNetPeakCps = netPkCps[scanNum][speciesMeasurementIndex];
                    if (absNetPeakCps > 1.0e-6) {
                        double calcVariance
                                = absNetPeakCps + (Math.abs(backgroundCps) * Math.pow(countTimeSec[speciesMeasurementIndex] / countTimeSec[HARD_WIRED_INDEX_OF_BACKGROUND], 2));
                        pkFerr[scanNum][speciesMeasurementIndex]
                                = Math.sqrt(calcVariance) / absNetPeakCps / countTimeSec[speciesMeasurementIndex];
                    } else {
                        pkFerr[scanNum][speciesMeasurementIndex] = 1.0;
                    }
                }
            }
        }

        totalCps = new double[nSpecies];
        for (int speciesMeasurementIndex = 0; speciesMeasurementIndex < nSpecies; speciesMeasurementIndex++) {
            // calculate total cps
            // this has the effect of setting totalCps[backgroundIndex] to backgroundCps
            totalCps[speciesMeasurementIndex] = (sumOfCorrectedPeaks[speciesMeasurementIndex] / nScans) + backgroundCps;
        }
    }

    private static void calculateIsotopicRatios(boolean useSBM) {
        // Step 3 of Development for SHRIMP 
        // (see wiki: https://github.com/CIRDLES/ET_Redux/wiki/Development-for-SHRIMP:-Step-3)
        // walk the ratios
        for (IsotopeRatioModelSHRIMP isotopicRatio : isotopicRatios) {
            int nDod = nScans - 1;
            int NUM = speciesToIndexBiMap.inverse().get(isotopicRatio.getNumerator());
            int DEN = speciesToIndexBiMap.inverse().get(isotopicRatio.getDenominator());

            int aOrd = (DEN > NUM) ? NUM : DEN;
            int bOrd = (DEN > NUM) ? DEN : NUM;

            double totCtsNUM = 0.0;
            double totCtsDEN = 0.0;

            for (int j = 0; j < nScans; j++) {
                totCtsNUM += netPkCps[j][NUM] * countTimeSec[NUM];
                totCtsDEN += netPkCps[j][DEN] * countTimeSec[DEN];
            }

            double ratioVal;
            double ratioFractErr;
            double[] ratioInterpTime;
            double[] interpRatVal;
            double[] ratValFerr;
            double[] ratValSig;
            double[][] sigRho;
            boolean[] zerPkCt;

            List<Double> ratEqTime = new ArrayList<>();
            List<Double> ratEqVal = new ArrayList<>();
            List<Double> ratEqErr = new ArrayList<>();

            if ((totCtsNUM < 32) || (totCtsDEN < 32) || (nDod == 0)) {
                ratioVal = 0.0;
                ratioFractErr = 1.0;
                if (totCtsNUM == 0.0) {
                    ratioVal = 1e-32;
                } else if (totCtsDEN == 0.0) {
                    ratioVal = 1e16;
                } else {
                    ratioVal = (totCtsNUM / countTimeSec[NUM]) / (totCtsDEN / countTimeSec[DEN]);
                    ratioFractErr = Math.sqrt((1.0 / Math.abs(totCtsNUM)) + (1.0 / Math.abs(totCtsDEN)));
                }

                ratioInterpTime = new double[]{//
                    0.5 * (Math.min(timeStampSec[0][NUM], timeStampSec[0][DEN]) + Math.max(timeStampSec[nScans - 1][NUM], timeStampSec[nScans - 1][DEN]))
                };
                interpRatVal = new double[]{ratioVal};
                ratValFerr = new double[]{ratioFractErr};

                ratEqTime.add(ratioInterpTime[0]);
                ratEqVal.add(interpRatVal[0]);
                ratEqErr.add(Math.abs(ratValFerr[0] * interpRatVal[0]));

            } else {
                // main treatment using double interpolation following Dodson (1978): http://dx.doi.org/10.1088/0022-3735/11/4/004)
                double errorValue = 0.0;
                double[] pkF = new double[nDod];
                double sumPkF = 0.0;
                for (int j = 0; j < nDod; j++) {
                    pkF[j] = (timeStampSec[j][bOrd] - timeStampSec[j][aOrd]) / (timeStampSec[j + 1][aOrd] - timeStampSec[j][aOrd]);
                    sumPkF += pkF[j];
                }

                double avPkF = sumPkF / nDod;
                double f1 = (1.0 - avPkF) / 2.0;
                double f2 = (1.0 + avPkF) / 2.0;
                double rhoIJ = (1.0 - avPkF * avPkF) / (1.0 + avPkF * avPkF) / 2.0;

                ratioInterpTime = new double[nDod];
                interpRatVal = new double[nDod];
                ratValFerr = new double[nDod];
                ratValSig = new double[nDod];
                sigRho = new double[nDod][nDod];
                zerPkCt = new boolean[nScans];

                int rct = -1;

                for (int sNum = 0; sNum < nDod; sNum++) {
                    boolean continueWithScanProcessing = true;
                    int sn1 = sNum + 1;
                    double totT = timeStampSec[sNum][aOrd] + timeStampSec[sNum][bOrd]
                            + timeStampSec[sn1][aOrd] + timeStampSec[sn1][bOrd];
                    double meanT = totT / 4.0;
                    ratioInterpTime[sNum] = meanT;

                    zerPkCt[sNum] = false;
                    zerPkCt[sn1] = false;
                    boolean hasZerPk = false;

                    double[] aPkCts = new double[2];
                    double[] bPkCts = new double[2];
                    for (int numDenom = 0; numDenom < 2; numDenom++) {
                        if (continueWithScanProcessing) {
                            int k = sNum + numDenom;
                            double aNetCPS = netPkCps[k][aOrd];
                            double bNetCPS = netPkCps[k][bOrd];

                            if ((aNetCPS == errorValue) || (bNetCPS == errorValue)) {
                                hasZerPk = true;
                                zerPkCt[k] = true;
                                continueWithScanProcessing = false;
                            }

                            if (continueWithScanProcessing) {
                                aPkCts[numDenom] = aNetCPS * countTimeSec[aOrd];
                                bPkCts[numDenom] = bNetCPS * countTimeSec[bOrd];

                                if (useSBM) {
                                    if ((sbmCps[k][aOrd] <= 0.0) || (sbmCps[k][aOrd] == errorValue)
                                            || (sbmCps[k][bOrd] <= 0.0) || (sbmCps[k][aOrd] == errorValue)) {
                                        zerPkCt[k] = true;
                                        continueWithScanProcessing = false;
                                    }
                                }
                            }
                        } // test continueWithScanProcessing
                    } // iteration through numDenom

                    if (continueWithScanProcessing) {
                        for (int k = 0; k < 2; k++) {
                            int numDenom = (k == 0) ? 1 : 0;

                            double a = aPkCts[k];
                            double b = aPkCts[numDenom];
                            if ((a <= 0) && (b > 16)) {
                                zerPkCt[sNum + k - 1] = true;
                            }

                            a = bPkCts[k];
                            b = bPkCts[numDenom];
                            if ((a <= 0) && (b > 16)) {
                                zerPkCt[sNum + k - 1] = true;
                            }
                        } // k iteration 

                        // test whether to continue
                        if (!zerPkCt[sNum] && !zerPkCt[sn1]) {
                            double aPk1 = netPkCps[sNum][aOrd];
                            double bPk1 = netPkCps[sNum][bOrd];
                            double aPk2 = netPkCps[sn1][aOrd];
                            double bPk2 = netPkCps[sn1][bOrd];

                            if (useSBM) {
                                aPk1 = aPk1 / sbmCps[sNum][aOrd];
                                bPk1 = bPk1 / sbmCps[sNum][bOrd];
                                aPk2 = aPk2 / sbmCps[sn1][aOrd];
                                bPk2 = bPk2 / sbmCps[sn1][bOrd];
                            }

                            double scanDeltaT = timeStampSec[sn1][aOrd] - timeStampSec[sNum][aOrd];
                            double bTfract = timeStampSec[sNum][bOrd] - timeStampSec[sNum][aOrd];
                            pkF[sNum] = bTfract / scanDeltaT;
                            double ff1 = (1.0 - pkF[sNum]) / 2.0;
                            double ff2 = (1.0 + pkF[sNum]) / 2.0;
                            double aInterp = (ff1 * aPk1) + (ff2 * aPk2);
                            double bInterp = (ff2 * bPk1) + (ff1 * bPk2);

                            double rNum = (NUM < DEN) ? aInterp : bInterp;
                            double rDen = (NUM < DEN) ? bInterp : aInterp;

                            double[] a1Pk = new double[nDod];
                            double[] b1Pk = new double[nDod];
                            double[] a2Pk = new double[nDod];
                            double[] b2Pk = new double[nDod];

                            if (rDen != 0.0) {
                                rct++;
                                interpRatVal[rct] = rNum / rDen;
                                a1Pk[sNum] = aPk1;
                                b1Pk[sNum] = bPk1;
                                a2Pk[sNum] = aPk2;
                                b2Pk[sNum] = bPk2;
                                double a1PkSig = pkFerr[sNum][aOrd] * aPk1;
                                double a2PkSig = pkFerr[sn1][aOrd] * aPk2;
                                double b1PkSig = pkFerr[sNum][bOrd] * bPk1;
                                double b2PkSig = pkFerr[sn1][bOrd] * bPk2;

                                if (useSBM) {
                                    a1PkSig = Math.sqrt(a1PkSig * a1PkSig
                                            + (aPk1 * aPk1 / sbmCps[sNum][aOrd] / countTimeSec[aOrd]));
                                    a2PkSig = Math.sqrt(a2PkSig * a2PkSig
                                            + (aPk2 * aPk2 / sbmCps[sn1][aOrd] / countTimeSec[aOrd]));
                                    b1PkSig = Math.sqrt(b1PkSig * b1PkSig
                                            + (bPk1 * bPk1 / sbmCps[sNum][bOrd] / countTimeSec[bOrd]));
                                    b2PkSig = Math.sqrt(b2PkSig * b2PkSig
                                            + (bPk2 * bPk2 / sbmCps[sn1][bOrd] / countTimeSec[bOrd]));
                                }

                                if ((aInterp == 0.0) || (bInterp == 0.0)) {
                                    ratValFerr[rct] = 1.0;
                                    ratValSig[rct] = 1E-32;
                                    sigRho[rct][rct] = 1E-32;
                                } else {
                                    double term1 = ((f1 * a1PkSig) * (f1 * a1PkSig) + (f2 * a2PkSig) * (f2 * a2PkSig));
                                    double term2 = ((f2 * b1PkSig) * (f2 * b1PkSig) + (f1 * b2PkSig) * (f1 * b2PkSig));
                                    double ratValFvar = (term1 / (aInterp * aInterp)) + (term2 / (bInterp * bInterp));
                                    double ratValVar = ratValFvar * (interpRatVal[rct] * interpRatVal[rct]);
                                    ratValFerr[rct] = Math.sqrt(ratValFvar);
                                    ratValSig[rct] = Math.max(1E-10, Math.sqrt(ratValVar));
                                    sigRho[rct][rct] = ratValSig[rct];

                                    if (rct > 0) {
                                        rhoIJ = (zerPkCt[sNum - 1]) ? 0.0 : (1 - pkF[sNum] * pkF[sNum]) / (1 + pkF[sNum] * pkF[sNum]) / 2.0;

                                        sigRho[rct][rct - 1] = rhoIJ;
                                        sigRho[rct - 1][rct] = rhoIJ;
                                    }
                                } // test aInterp andbInterp  
                            } // test rDen

                        } // test !zerPkCt[sNum] && !zerPkCt[sn1]

                    } // continueWithScanProcessing is true

                } // iteration through nDod using sNum (see "NextScanNum" in pseudocode)
      
                if (rct == -1) {
                    ratioVal = errorValue;
                    ratioFractErr = errorValue;
                } else if (rct == 0) {
                    ratioVal = interpRatVal[0];

                    if (ratioVal == 0.0) {
                        ratioVal = 1E-32;
                        ratioFractErr = 1.0;
                    } else {
                        ratioFractErr = ratValFerr[0];
                    }
                } else {
                    for (int j = 0; j < (rct + 1); j++) {
                        ratEqTime.add(ratioInterpTime[j]);
                        ratEqVal.add(interpRatVal[j]);
                        ratEqErr.add(Math.abs(ratValFerr[j] * interpRatVal[j]));
                    }
                }

            } // end decision on which ratio procedure to use

            // sanity check for this ratio
            System.out.println("STOP);");
        } // end iteration through isotopicRatios

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
            JAXBContext jaxbContext = JAXBContext.newInstance(PrawnFile.class
            );
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            PrawnFile prawnFile = (PrawnFile) jaxbUnmarshaller.unmarshal(prawnFileURL);

            for (int f = 0; f < prawnFile.getRuns(); f++) {
                PrawnFile.Run runFraction = prawnFile.getRun().get(f);

                if (runFraction.getPar().get(0).getValue().startsWith("T.1.1.1")) {
//                System.out.println("\n" + runFraction.getPar().get(0).getValue() + "  ***********************\n");
                    processRunFraction(runFraction);

                    for (double[] scannedData : extractedRunData) {
//                        System.out.print(scannedData1[16] + ",  " + scannedData1[17]);
                        for (int j = 0; j < scannedData.length; j++) {
                            System.out.print(scannedData[j]);
                            if (j < (scannedData.length - 1)) {
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
            JAXBContext jaxbContext = JAXBContext.newInstance(PrawnFile.class
            );
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
