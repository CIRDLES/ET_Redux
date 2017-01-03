/*
 * FitFunctionInterface.java
 *
 * Created Jul 1, 2011
 * 
 * Copyright 2006-2017 James F. Bowring and www.Earth-Time.org
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
package org.earthtime.Tripoli.fitFunctions;

import Jama.Matrix;

/**
 *
 * @author James F. Bowring
 */
public interface FitFunctionInterface {

    /**
     *
     *
     * @param dataActiveMap
     * @param xValues
     * @param yValues
     * @param MeasuredCovMatrixS
     * @param calculateHatMatrix
     * @return
     */
    AbstractFunctionOfX getFunctionOfX( //
            boolean[] dataActiveMap, double[] xValues, double[] yValues, Matrix MeasuredCovMatrixS, boolean calculateHatMatrix);

    public static double calculateMeanOfCovarianceMatrixDiagonal(Matrix matrix) {
        // precondition: square matrix
        double retVal = 0.0;

        for (int i = 0; i < matrix.getColumnDimension(); i++) {
            retVal += matrix.get(i, i);
        }

        return retVal / matrix.getColumnDimension();
    }

    public static double[] initializeExpFastParameters(double[] xValues, double[] yValues) {
        // June 2015 new code from Noah to better prime this puppy
        double [] pod = new double[3];
        
        // using first, third, and fifth 20% ranges to determine properties of exponential
        double first20 = 0.0;
        double mid20 = 0.0;
        double last20 = 0.0;
        int oneFifth = (int) ((yValues.length * 0.2) + 0.5);
        int twoFifth = (int) ((yValues.length * 0.4) + 0.5);
        int fourFifth = (int) ((yValues.length * 0.8) + 0.5);

        for (int i = 0; i < oneFifth; i++) {
            first20 += yValues[i];
        }
        for (int i = twoFifth; i < (oneFifth + twoFifth); i++) {
            mid20 += yValues[i];
        }
        for (int i = fourFifth; i < yValues.length; i++) {
            last20 += yValues[i];
        }

        first20 /= oneFifth;
        mid20 /= oneFifth;
        last20 /= (yValues.length - fourFifth);

        double fudgec = 0.01; // 1 percent
        double tstart = xValues[0];
        double tend = xValues[xValues.length - 1];

        if (first20 <= last20) {// positive average slope
            if ((mid20 - first20) > (last20 - mid20)) { // if concave down, positive slope
                pod[2] = last20 + fudgec;
                pod[1] = (Math.log(last20 - first20 + fudgec) - Math.log(fudgec)) / (tstart - tend);
                pod[0] = -Math.exp((tstart * Math.log(fudgec) - tend * Math.log(last20 - first20 + fudgec)) / (tstart - tend));

            } else {// if concave up, positive slope
                pod[2] = first20 - fudgec;
                pod[1] = (Math.log(fudgec) - Math.log(last20 - first20 + fudgec)) / (tstart - tend);
                pod[0] = Math.exp((tstart * Math.log(last20 - first20 + fudgec) - tend * Math.log(fudgec)) / (tstart - tend));

            } // if concave up or down
        } else {// negative average slope
            if ((first20 - mid20) < (mid20 - last20)) { // if concave down
                pod[2] = first20 + fudgec;
                pod[1] = (Math.log(fudgec) - Math.log(first20 - last20 + fudgec)) / (tstart - tend);
                pod[0] = -Math.exp((tstart * Math.log(first20 - last20 + fudgec) - tend * Math.log(fudgec)) / (tstart - tend));

            } else {// if concave up
                pod[2] = last20 - fudgec;
                pod[1] = (Math.log(first20 - last20 + fudgec) - Math.log(fudgec)) / (tstart - tend);
                pod[0] = Math.exp((tstart * Math.log(fudgec) - tend * Math.log(first20 - last20 + fudgec)) / (tstart - tend));
            } //if concave up or down
        } // if positive or negative slope
        
        return pod;
    }

}
