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
 * From Ken Ludwig's Squid VBA code for use with Shrimp prawn files data
 * reduction. Note code extracted by Simon Bodorkos in emails to bowring
 * Feb.2016
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public final class TukeyBiweight {

    public static ValueModel calculateTukeyBiweightMean(String name, double tuningConstant, double[] values) {
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

        do {
            iterationCounter++;
            previousMean = mean;
            previousSigma = sigma;

            // init to zeroes
            double[] deltas = new double[n];
            double[] u = new double[n];
            double sa = 0.0;
            double sb = 0.0;
            double sc = 0.0;

            double tee = tuningConstant * sigma;

            for (int i = 0; i < n; i++) {
                deltas[i] = values[i] - mean;
                if (Math.abs(deltas[i]) < tee) {
                    deltas[i] = values[i] - mean;
                    u[i] = deltas[i] / tee;
                    double uSquared = u[i] * u[i];
                    sa += Math.pow(deltas[i] * Math.pow((1.0 - uSquared), 2), 2);
                    sb += (1.0 - uSquared) * (1.0 - 5.0 * uSquared);
                    sc += u[i] * Math.pow(1.0 - uSquared, 2);
                }
            }
            sigma = Math.sqrt(n * sa) / Math.abs(sb);
            mean = previousMean + tee * sc / sb;

        } // both tests against epsilon must pass OR iterations top out
        while (((Math.abs(sigma - previousSigma) / sigma > epsilon)//
                || (Math.abs(mean - previousMean) / mean > epsilon))//
                && (iterationCounter < iterationMax));

        return new ValueModel(name, new BigDecimal(mean), "ABS", new BigDecimal(sigma), BigDecimal.ZERO);
    }

    /**
     * Calculates arithmetic median of array of doubles.
     *
     * @pre values has one element
     * @param values
     * @return
     */
    public static double calculateMedian(double[] values) {
        double median;

        // enforce precondition
        if (values.length == 0) {
            median = 0.0;
        } else {
            double[] myValues = values.clone();

            Arrays.sort(myValues);
            int pos1 = (int) Math.floor((myValues.length - 1.0) / 2.0);
            int pos2 = (int) Math.ceil((myValues.length - 1.0) / 2.0);
            if (pos1 == pos2) {
                median = myValues[pos1];
            } else {
                median = (myValues[pos1] + myValues[pos2]) / 2.0;
            }
        }
        return median;
    }
}
