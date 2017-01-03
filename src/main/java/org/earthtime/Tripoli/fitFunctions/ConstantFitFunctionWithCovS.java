/*
 * ConstantFitFunctionWithCovS.java
 *
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
import java.io.Serializable;
import org.earthtime.dataDictionaries.FitFunctionTypeEnum;

/**
 *
 * @author James F. Bowring
 */
public class ConstantFitFunctionWithCovS implements FitFunctionInterface, Serializable {

    // Class variables
    //private static final long serialVersionUID = -4168711014938748918L;
    private static ConstantFitFunctionWithCovS instance = null;

    private ConstantFitFunctionWithCovS() {
        super();
    }

    /**
     *
     * @return
     */
    public static ConstantFitFunctionWithCovS getInstance() {
        if (instance == null) {
            instance = new ConstantFitFunctionWithCovS();
        }
        return instance;
    }

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
    @Override
    public AbstractFunctionOfX getFunctionOfX(boolean[] dataActiveMap, double[] xValues, double[] yValues, Matrix MeasuredCovMatrixS, boolean calculateHatMatrix) {

        // Dec 2012 Modified to handle constant vs mean which is George's old data setup
        // determine count of datapoints to be used
        // NOTE: MeasuredCovMatrixS should have been properly sized before arriving here (inactive data removed)
        int countOfActiveData = 0;
        for (int i = 0; i < dataActiveMap.length; i++) {
            if (dataActiveMap[i]) {
                countOfActiveData++;
            }
        }

        // usng the first value only
        double meanIntensity = yValues[0];

        // create function of x for mean
        AbstractFunctionOfX myFofX = new FofX(meanIntensity);
        
        // create column vector filled with ones
        Matrix onesVector = new Matrix(countOfActiveData, 1, 1.0);

        Matrix MeasuredCovMatrixS_solveOnesVector = MeasuredCovMatrixS.solve(onesVector);
        double meanIntensityVariance = 1.0 / onesVector.transpose().times(MeasuredCovMatrixS_solveOnesVector).get(0, 0);
        
        // nov 2014
        myFofX.setMatrixJacobianYInterceptLogRatioXY(MeasuredCovMatrixS_solveOnesVector.transpose().times(meanIntensityVariance));      
        
        // set the variance
        myFofX.setFitParameterCovarianceMatrix(new Matrix( //
                new double[][]{{meanIntensityVariance}}));

        double chiSquared = Double.NaN;

        System.out.println("Constant Solve: a = " + myFofX.getA() + " chiSqr = NaN by design");

        myFofX.setChiSquared(chiSquared);
        myFofX.setMSWD(chiSquared / (countOfActiveData - myFofX.getCountOfParameters()));

        Matrix matrixJ11 = new Matrix(1, countOfActiveData, 1.0 / countOfActiveData);
        myFofX.setMatrixJ11(matrixJ11);

        myFofX.setCountOfActiveData(countOfActiveData);

        return myFofX;
    }

    private class FofX extends AbstractFunctionOfX implements Serializable {

        // Class variables
        private static final long serialVersionUID = -5936401723415772528L;

        public FofX() {
            super();
        }

        public FofX(double a) {
            super(a, 0.0, 0.0);

            this.description = "y = a";
            this.shortName = FitFunctionTypeEnum.CONSTANT;
            this.countOfParameters = 1;
        }

        @Override
        public double f(double x) {
            return a;
        }

        @Override
        public double[] calculateInterpolatedVariances(Matrix Jf, double[] timesForMatrix) {
            throw new UnsupportedOperationException("Not supported for constant.");
        }

        @Override
        public double getYIntercept() {
            return a;
        }

        @Override
        public double getYInterceptStdErr() {
            return getStdErrOfA();
        }

        @Override
        public double getStdErrOfA() {
            return Math.sqrt(fitParameterCovarianceMatrix.get(0, 0));
        }

        @Override
        public double getStdErrOfB() {
            throw new UnsupportedOperationException("Parameter B is not defined.");
        }

        @Override
        public double getStdErrOfC() {
            throw new UnsupportedOperationException("Parameter C is not defined.");
        }

        @Override
        public double getVarOfA() {
            return fitParameterCovarianceMatrix.get(0, 0);
        }

        @Override
        public double getVarOfB() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public double getVarOfC() {
            throw new UnsupportedOperationException("Not supported.");
        }

        @Override
        public Matrix makeMatrixJ21(int countOfActiveData, boolean[] dataActiveMap, double[] neededValues) {
            return new Matrix(countOfActiveData, 1, -1.0);
        }

        @Override
        public Matrix makeMatrixJ22(int countOfActiveData, boolean[] dataActiveMap, double[] neededValues) {
            return Matrix.identity(countOfActiveData, countOfActiveData);
        }

        @Override
        public boolean fitFound() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public double getYInterceptVariance() {
            return fitParameterCovarianceMatrix.get(0, 0);
        }

        @Override
        protected Matrix extractMatrixJIntpFromJpy(Matrix Jpy) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
