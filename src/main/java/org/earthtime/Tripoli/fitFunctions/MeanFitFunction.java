/*
 * MeanFitFunction.java
 *
 * Created Jul 7, 2011
 *
 * Copyright 2006-2015 James F. Bowring and www.Earth-Time.org
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
import org.earthtime.statistics.NonParametricStats;

/**
 *
 * @author James F. Bowring
 */
public class MeanFitFunction implements FitFunctionInterface, Serializable {

    // Class variables
    private static final long serialVersionUID = 5760948815970052808L;
    private static MeanFitFunction instance = null;

    private MeanFitFunction() {
        super();
    }

    /**
     *
     * @return
     */
    public static MeanFitFunction getInstance() {
        if (instance == null) {
            instance = new MeanFitFunction();
        }
        return instance;
    }

    /**
     *
     * @param dataActiveMap
     * @param xValues
     * @param yValues
     * @param Sib
     * @param stdErrOfYValues
     * @param calculateHatMatrix
     * @return
     */
    @Override
    public AbstractFunctionOfX getFunctionOfX(boolean[] dataActiveMap, double[] xValues, double[] yValues, Matrix Sib, boolean calculateHatMatrix) {

        NonParametricStats nonParametricStats = NonParametricStats.getInstance();
        nonParametricStats.calculateStats(dataActiveMap, yValues);

        // create function of x for mean
        AbstractFunctionOfX myFofX = new FofX(nonParametricStats.getSampleMean());

        // calculate sumSquaredErrors
        double sse = 0.0;

        int countOfActiveData = 0;

        for (int i = 0; i < dataActiveMap.length; i++) {
            if (dataActiveMap[i]) {
                sse += Math.pow(yValues[i] - myFofX.f(xValues[i]), 2);
                countOfActiveData++;
            }
        }

        myFofX.setChiSquared(sse);
        myFofX.setMSWD(sse / (countOfActiveData - myFofX.getCountOfParameters()));

        // square the entry so that it serves as the variance
        myFofX.setFitParameterCovarianceMatrix(new Matrix( //
                new double[][]{{Math.pow(nonParametricStats.getStdErrSampleMean(), 2)}}));

        myFofX.setCountOfActiveData(countOfActiveData);

        // nov 2014
        // create row vector filled with ones
        Matrix onesRowVector = new Matrix(1, countOfActiveData, 1.0);
        myFofX.setMatrixJacobianYInterceptLogRatioXY(onesRowVector.times(-Sib.get(0, 0) / (Sib.get(0, 1) * Sib.get(0, 1))));

        return myFofX;
    }

    private class FofX extends AbstractFunctionOfX implements Serializable {

        // Class variables
        private static final long serialVersionUID = -792428549251696289L;

        public FofX() {
            super();
        }

        public FofX(double a) {
            super(a, 0, 0);

            this.description = "y = a";
            this.shortName = FitFunctionTypeEnum.MEAN;
            this.countOfParameters = 1;
        }

        @Override
        public double f(double x) {
            return a;
        }

        @Override
        public double[] calculateInterpolatedVariances(Matrix Jf, double[] timesForMatrix) {
            double[] iv = new double[timesForMatrix.length];
            for (int i = 0; i < timesForMatrix.length; i++) {
                iv[i] = fitParameterCovarianceMatrix.get(0, 0);
            }

            return iv;
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
        public double getYInterceptVariance() {
            return fitParameterCovarianceMatrix.get(0, 0);
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
            throw new UnsupportedOperationException("Parameter B is not defined.");
        }

        @Override
        public double getVarOfC() {
            throw new UnsupportedOperationException("Parameter C is not defined.");
        }

        @Override
        public Matrix makeMatrixJ21(int countOfActiveData, boolean[] dataActiveMap, double[] neededValues) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public Matrix makeMatrixJ22(int countOfActiveData, boolean[] dataActiveMap, double[] neededValues) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public boolean fitFound() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        protected Matrix extractMatrixJIntpFromJpy(Matrix Jpy) {
            return Jpy;
        }
    }
}
