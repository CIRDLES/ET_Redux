/*
 * LineFitFunctionWithCovS.java
 *
 * Copyright 2006-2018 James F. Bowring, CIRDLES.org, and Earth-Time.org
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
public class LineFitFunctionWithCovS implements FitFunctionInterface, Serializable {

    // Class variables
    private static final long serialVersionUID = 2381345978559394607L;
    private static LineFitFunctionWithCovS instance = null;

    private LineFitFunctionWithCovS() {
        super();
    }

    /**
     *
     * @return
     */
    public static LineFitFunctionWithCovS getInstance() {
        if (instance == null) {
            instance = new LineFitFunctionWithCovS();
        }
        return instance;
    }

    /**
     *
     * @param dataActiveMap
     * @param xValues
     * @param yValues
     * @param calculateHatMatrix
     * @return
     */
    @Override
    public AbstractFunctionOfX getFunctionOfX(//
            boolean[] dataActiveMap, double[] xValues, double[] yValues, Matrix MeasuredCovMatrixS, boolean calculateHatMatrix) {

        // modified Nov 2012 for MeasuredCovMatrixS
        // determine count of datapoints to be used
        int countOfActiveData = 0;
        for (int i = 0; i < dataActiveMap.length; i++) {
            if (dataActiveMap[i]) {
                countOfActiveData++;
            }
        }

        // create a n x 1 vector of yValues and n x 2 matrix of xValues
        double[][] a = new double[countOfActiveData][2];
        double[][] b = new double[countOfActiveData][1];

        int index = 0;
        for (int i = 0; i < dataActiveMap.length; i++) {
            if (dataActiveMap[i]) {
                a[index][0] = 1.0;
                a[index][1] = xValues[i];
                b[index][0] = yValues[i];

                index++;
            }
        }

        Matrix aM = new Matrix(a);
        Matrix bM = new Matrix(b);

        Matrix C = aM.transpose().times(MeasuredCovMatrixS.solve(aM));
        Matrix D = aM.transpose().times(MeasuredCovMatrixS.solve(bM));

        // 2-by-1 vector containing the y-intercept and the slope, in that order
        Matrix fitM = C.solve(D);

        // create function of x for LINE
        AbstractFunctionOfX myFofX = new FofX( //
                fitM.get(1, 0), //
                fitM.get(0, 0));

        // nov 2014
        Matrix JYintLR_twoRow = C.solve(aM.transpose()).times(MeasuredCovMatrixS.inverse());
        myFofX.setMatrixJacobianYInterceptLogRatioXY(JYintLR_twoRow.getMatrix(0, 0, 0, countOfActiveData - 1));
        
        // The covariance matrix for the y-intercept and slope, analogous to Vp from Levenberg-Marquardt
        // where the variance of the y-intercept is Vp(0,0) and the variance of the slope is Vp(1,1)
        Matrix Vp1 = C.inverse();

        myFofX.setFitParameterCovarianceMatrix(Vp1);
        if (Vp1.getRowDimension() > 2) {
            System.out.println("OOPS");
        }

        Matrix DforJ11 = MeasuredCovMatrixS.solve(aM).transpose();
        myFofX.setMatrixJ11(C.times(DforJ11));

        // calculate uncertainty-weighted sumSquaredErrors
        double[][] residualIntensities = new double[countOfActiveData][1];
        index = 0;
        for (int i = 0; i < countOfActiveData; i++) {
            if (dataActiveMap[i]) {
                residualIntensities[index][0] = yValues[i] - myFofX.f(xValues[i]);
                index++;
            }
        }
        Matrix residuals = new Matrix(residualIntensities);


        double chiSquared = 0.0;
        try {
            chiSquared = residuals.transpose().times(MeasuredCovMatrixS.solve(residuals)).get(0, 0);
        } catch (Exception e) {
        }

        System.out.println("Line Solve: a = " + myFofX.getA() + "  b = " + myFofX.getB() + " chiSqr = " + chiSquared);

        myFofX.setChiSquared(chiSquared);
        myFofX.setMSWD(chiSquared / (countOfActiveData - myFofX.getCountOfParameters()));


        if (calculateHatMatrix) {
            Matrix E = MeasuredCovMatrixS.solveTranspose(aM.transpose()).transpose();
            myFofX.setHatMatrix(aM.times(C.solve(E)));
        }

        myFofX.setCountOfActiveData(countOfActiveData);
        //For the mean, without overdispersion 
        //BIC = -(r'*(Sib\r) - 2 * log(det(Sib)) + m*log(n)
        //BIC = -transpose(residuals).times( (Sib.solve(r) ) - 2 * log(det(Sib)) + m*log(n)
        myFofX.setBIC(residuals.transpose().times(MeasuredCovMatrixS.solve(residuals)).get(0, 0) + Math.log(MeasuredCovMatrixS.det()) + 2.0 * Math.log(countOfActiveData));


        return myFofX;
    }

    private class FofX extends AbstractFunctionOfX implements Serializable {

        // Class variables
        private static final long serialVersionUID = 2848718589081244467L;
        //

        public FofX() {
            super();
        }

        public FofX(double a, double b) {
            super(a, b, 0);

            this.description = "y = a*x + b";
            this.shortName = FitFunctionTypeEnum.LINE;
            this.countOfParameters = 2;
        }

        @Override
        public double f(double x) {
            return a * x + b;
        }

        @Override
        public double[] calculateInterpolatedVariances(Matrix Jf, double[] timesForMatrix) {

            matrixSf = Jf.times(fitParameterCovarianceMatrix.getMatrix(0, 1, 0, 1)).times(Jf.transpose());

            double[] stdVariances = new double[matrixSf.getRowDimension()];
            for (int i = 0; i < stdVariances.length; i++) {
                stdVariances[i] = matrixSf.get(i, i);
            }

            return stdVariances;
        }

        @Override
        public double getYIntercept() {
            return b;
        }

        @Override
        public double getYInterceptStdErr() {
            return getStdErrOfB();
        }

        @Override
        public double getYInterceptVariance() {
            return fitParameterCovarianceMatrix.get(0, 0);
        }

        @Override
        public double getStdErrOfA() {
            return Math.sqrt(fitParameterCovarianceMatrix.get(1, 1));
        }

        @Override
        public double getStdErrOfB() {
            return Math.sqrt(fitParameterCovarianceMatrix.get(0, 0));
        }

        @Override
        public double getStdErrOfC() {
            throw new UnsupportedOperationException("Parameter C is not defined.");
        }

        @Override
        public double getVarOfA() {
            return fitParameterCovarianceMatrix.get(1, 1);
        }

        @Override
        public double getVarOfB() {
            return fitParameterCovarianceMatrix.get(0, 0);
        }

        @Override
        public double getVarOfC() {
            throw new UnsupportedOperationException("Parameter C is not defined.");
        }

        @Override
        public Matrix makeMatrixJ21(int countOfActiveData, boolean[] dataActiveMap, double[] neededValues) {

            Matrix J21 = new Matrix(countOfActiveData, 2, 1.0);
            int index = 0;
            for (int i = 0; i < dataActiveMap.length; i++) {
                if (dataActiveMap[i]) {
                    J21.set(index, 0, neededValues[index]);

                    index++;
                }
            }

            return J21;
        }

        @Override
        public Matrix makeMatrixJ22(int countOfActiveData, boolean[] dataActiveMap, double[] neededValues) {
            return Matrix.identity(countOfActiveData, countOfActiveData).times(a);
        }

        @Override
        public boolean fitFound() {
            return (getStdErrOfA() * getStdErrOfB() != 0) //
                    ||//
                    ((a + b) == 0.0);
        }

        @Override
        protected Matrix extractMatrixJIntpFromJpy ( Matrix Jpy ) {
//            return Jpy.getMatrix( 0, Jpy.getRowDimension() - 1, 0, 0);
            return Jpy.getMatrix( 0, 0, 0, Jpy.getColumnDimension() - 1);
        }
    }
}
