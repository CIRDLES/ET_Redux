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
package org.earthtime.dataDictionaries;

/**
 * From Ken Ludwig's Squid VBA code. Poisson distribution limits for count 0
 * through 100
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public final class PoissonLimitsCountLessThanEqual100 {

    /**
     * Contains added 0 at location 0 for case count = 0
     */
    private static double[] lowerLimits
            = {0, 0, 0, 0, 1, 1, 2, 2, 3, 4, 4, 5, 6, 6, 7, 8, 9, 9, 10,
                11, 12, 13, 13, 14, 15, 16, 17, 17, 18, 19, 20, 21, 21, 22, 23,
                24, 25, 26, 26, 27, 28, 29, 30, 31, 31, 32, 33, 34, 35, 36, 37,
                38, 38, 39, 40, 41, 42, 43, 44, 44, 45, 46, 47, 48, 49, 50, 51,
                51, 52, 53, 54, 55, 56, 57, 58, 59, 59, 60, 61, 62, 63, 64, 65,
                66, 67, 67, 68, 69, 70, 71, 72, 73, 74, 75, 75, 76, 77, 78, 79, 80, 81};
    /**
     * Contains added 6 at location 0 for case count = 0
     */
    private static double[] upperLimits
            = {6, 2, 4, 6, 7, 9, 10, 12, 13, 14, 16, 17, 18, 20, 21, 22, 23,
                25, 26, 27, 28, 29, 31, 32, 33, 34, 35, 37, 38, 39, 40, 41, 43, 44,
                45, 46, 47, 48, 50, 51, 52, 53, 54, 55, 56, 58, 59, 60, 61, 62, 63,
                64, 66, 67, 68, 69, 70, 71, 72, 74, 75, 76, 77, 78, 79, 80, 81, 82,
                84, 85, 86, 87, 88, 89, 90, 91, 93, 94, 95, 96, 97, 98, 99, 100, 101,
                103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 114, 115, 116, 117,
                118, 119};

    /**
     * Returns index of value most outside upper and lower limits determined by
     * median.
     *
     * @pre Median in [0,...,100] and measurements has one member
     * @param median
     * @param measurements
     * @return maxResidualIndex with -1 denoting no outlier
     */
    public static int determineIndexOfValueWithLargestResidual(double median, double[] measurements) {
        int maxResidualIndex = -1;

        // enforce precondition
        if ((median >= 0) && (median <= 100) && (measurements.length > 0)) {
            double lowerLimit = lowerLimits[(int) median];
            double upperLimit = upperLimits[(int) median];

            // find the single element most outside the limits
            double maxResidual = 0;

            for (int i = 0; i < measurements.length; i++) {
                double residual = 0.0;
                if (measurements[i] < lowerLimit) {
                    residual = lowerLimit - measurements[i];
                } else if (measurements[i] > upperLimit) {
                    residual = measurements[i] - upperLimit;
                }
                if (residual > maxResidual) {
                    maxResidual = residual;
                    maxResidualIndex = i;
                }
            }
        }
        
        return maxResidualIndex;
    }

}
