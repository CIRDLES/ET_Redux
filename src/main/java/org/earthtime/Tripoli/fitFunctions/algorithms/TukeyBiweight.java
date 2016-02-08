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
package org.earthtime.Tripoli.fitFunctions.algorithms;

import java.math.BigDecimal;
import java.util.Arrays;
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 * From Ken Ludwig's Squid 2.50 User's Manual p. 47 for use with Shrimp prawn
 * files data reduction. Note Ludwig's PDF has two errors per Simon Bodorkos
 * in email to bowring 8.Feb.2016
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public final class TukeyBiweight {

    public static ValueModel calculateTukeyBiweightMean(String name, int tuningConstant, double[] values) {
        // guarantee termination
        double epsilon = 1e-10;
        int iterationMax = 100;
        int iterationCounter = 0;

        int n = values.length;
        // initial mean is median
        double mean = calculateMedian(values);
        // initial sigma is median absolute deviation from mean = median (MAD)
        double deviations[] = new double[n];
        for (int i = 0; i < values.length; i++) {
            deviations[i] = Math.abs(values[i] - mean);
        }

        double sigma = calculateMedian(deviations);
        double previousMean;
        double previousSigma;

        // init to zeroes
        double[] deltas = new double[n];
        double[] u = new double[n];

        do {
            iterationCounter++;
            previousMean = mean;
            previousSigma = sigma;

            // Ludwig's User Manual code initilaizes tee only once at the beginning of method = error
            double tee = tuningConstant * sigma;
            for (int i = 0; i < n; i++) {
                if (Math.abs(deltas[i]) < tee) {
                    deltas[i] = values[i] - mean;
                }
                u[i] = deltas[i] / tee;
            }

            // Calculate SA, SB, SC
            double sa = 0.0;
            double sb = 0.0;
            double sc = 0.0;
            for (int i = 0; i < n; i++) {
                double uSquared = u[i] * u[i];
                // next line is from Ludwig's User Manual = error
                // sa += Math.pow(deltas[i] * (1.0 - uSquared), 2);
                sa += Math.pow(deltas[i] * Math.pow((1.0 - uSquared), 2), 2);
                sb += (1.0 - uSquared) * (1.0 - 5.0 * uSquared);
                sc += u[i] * Math.pow(1.0 - uSquared, 2);
            }

            sigma = Math.sqrt(n * sa) / Math.abs(sb);
            mean = previousMean + tee * sc / sb;

        } while ((Math.abs(sigma - previousSigma) > epsilon) && (Math.abs(mean - previousMean) > epsilon) && (iterationCounter < iterationMax));

        return new ValueModel(name, new BigDecimal(mean), "ABS", new BigDecimal(sigma), BigDecimal.ZERO);
    }

    public static double calculateMedian(double[] values) {
        double median;

        Arrays.sort(values);
        double pos1 = Math.floor((values.length - 1.0) / 2.0);
        double pos2 = Math.ceil((values.length - 1.0) / 2.0);
        if (pos1 == pos2) {
            median = values[(int) pos1];
        } else {
            median = (values[(int) pos1] + values[(int) pos2]) / 2.0;
        }

        return median;
    }

    public static void main(String[] args) {
        double[] valuesM1 = {5, 4, 3, 2, 1};
        double[] valuesM2 = {6, 5, 4, 3, 2, 1};
        System.out.println(calculateMedian(valuesM1) + "  " + calculateMedian(valuesM2));

        double[] values = {48866, 48859, 48864, 48908, 48878, 48893, 48873, 48891, 48883, 48873};
        ValueModel mean = calculateTukeyBiweightMean("SBM", 6, values);
        System.out.println(mean.formatValueAndOneSigmaABSForTesting());
    }
}
