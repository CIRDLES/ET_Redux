/*
 * MeanFitFunctionWithCovS.java
 *
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

/**
 *
 * @author James F. Bowring
 */
public class MeanFitFunctionWithCovS implements FitFunctionInterface, Serializable {

    // Class variables
    private static final long serialVersionUID = -4168711014938748918L;
    private static MeanFitFunctionWithCovS instance = null;

    private MeanFitFunctionWithCovS () {
        super();
    }

    /**
     *
     * @return
     */
    public static MeanFitFunctionWithCovS getInstance () {
        if ( instance == null ) {
            instance = new MeanFitFunctionWithCovS();
        }
        return instance;
    }

    /**
     *
     * @param dataActiveMap
     * @param xValues
     * @param yValues
     * @param MeasuredCovMatrixS
     * @param calculateHatMatrix
     * @return
     */
    @Override
    public AbstractFunctionOfX getFunctionOfX ( boolean[] dataActiveMap, double[] xValues, double[] yValues, Matrix MeasuredCovMatrixS, boolean calculateHatMatrix ) {

        // Nov 2012
        // determine count of datapoints to be used
        // NOTE: MeasuredCovMatrixS should have been properly sized before arriving here (inactive data removed)
        int countOfActiveData = 0;
        for (int i = 0; i < dataActiveMap.length; i ++) {
            if ( dataActiveMap[i] ) {
                countOfActiveData ++;
            }
        }
        // create column vector filled with ones
        Matrix onesVector = new Matrix( countOfActiveData, 1, 1.0 );

        Matrix MeasuredCovMatrixS_solveOnesVector = MeasuredCovMatrixS.solve( onesVector );
        double meanIntensityVariance = 1.0 / onesVector.transpose().times( MeasuredCovMatrixS_solveOnesVector ).get( 0, 0 );

        // setup measured intensities as column vector
        double[][] measuredIntensities = new double[countOfActiveData][1];
        int index = 0;
        for (int i = 0; i < yValues.length; i ++) {
            if ( dataActiveMap[i] ) {
                measuredIntensities[index][0] = yValues[i];
                index ++;
            }
        }
        Matrix measuredIntensity = new Matrix( measuredIntensities );

        double meanIntensity = onesVector.transpose().times( MeasuredCovMatrixS.solve( measuredIntensity ) ).get( 0, 0 ) * meanIntensityVariance;
        // create function of x for mean
        AbstractFunctionOfX myFofX = new FofX( meanIntensity );
        
        // nov 2014
        myFofX.setMatrixJacobianYInterceptLogRatioXY(MeasuredCovMatrixS_solveOnesVector.transpose().times(meanIntensityVariance));

        // set the variance
        myFofX.setFitParameterCovarianceMatrix( new Matrix( //
                new double[][]{{Math.abs( meanIntensityVariance )}} ) );

        // calculate uncertainty-weighted sumSquaredErrors
        // setup residual intensities as column vector
        index = 0;
        double[][] residualIntensities = new double[countOfActiveData][1];
        for (int i = 0; i < yValues.length; i ++) {
            if ( dataActiveMap[i] ) {
                residualIntensities[index][0] = yValues[i] - meanIntensity;
                index ++;
            }
        }
        Matrix residuals = new Matrix( residualIntensities );


        double chiSquared = 0.0;
        try {
            chiSquared = residuals.transpose().times( MeasuredCovMatrixS.solve( residuals ) ).get( 0, 0 );
        } catch (Exception e) {
        }

        System.out.println( "Mean Solve: a = " + myFofX.getA() + " chiSqr = " + chiSquared );

        myFofX.setChiSquared( chiSquared );
        myFofX.setMSWD( chiSquared / (countOfActiveData - myFofX.getCountOfParameters()) );


        Matrix matrixJ11 = new Matrix( 1, countOfActiveData, 1.0 / countOfActiveData );
        myFofX.setMatrixJ11( matrixJ11 );

        if ( calculateHatMatrix ) {
            Matrix Arow = MeasuredCovMatrixS.solveTranspose( onesVector.transpose() ).times( meanIntensityVariance );
            myFofX.setHatMatrix( onesVector.times( Arow.transpose() ) );
        }

        myFofX.setCountOfActiveData( countOfActiveData );

        //For the mean, without overdispersion 
        //BIC = -(r'*(Sib\r) - 2 * log(det(Sib)) + m*log(n)
        //BIC = -transpose(residuals).times( (Sib.solve(r) ) - 2 * log(det(Sib)) + m*log(n)
        myFofX.setBIC( residuals.transpose().times( MeasuredCovMatrixS.solve( residuals ) ).get( 0, 0 ) + Math.log( MeasuredCovMatrixS.det() ) + Math.log( countOfActiveData ) );

        return myFofX;
    }

    private class FofX extends AbstractFunctionOfX implements Serializable {

        // Class variables
        private static final long serialVersionUID = 3177348804526625007L;

        public FofX () {
            super();
        }

        public FofX ( double a ) {
            super( a, 0, 0 );

            this.description = "y = a";
            this.shortName = FitFunctionTypeEnum.MEAN;
            this.countOfParameters = 1;
        }

        @Override
        public double f ( double x ) {
            return a;
        }

        @Override
        public double[] calculateInterpolatedVariances ( Matrix Jf, double[] timesForMatrix ) {
            // Matrix Jf not used in this fitfunction

            matrixSf = new Matrix( timesForMatrix.length, timesForMatrix.length, fitParameterCovarianceMatrix.get( 0, 0 ) );

            double[] stdVariances = new double[matrixSf.getRowDimension()];
            for (int i = 0; i < stdVariances.length; i ++) {
                stdVariances[i] = matrixSf.get( i, i );
            }
            return stdVariances;
        }

        @Override
        public double getYIntercept () {
            return a;
        }

        @Override
        public double getYInterceptStdErr () {
            return getStdErrOfA();
        }

        @Override
        public double getYInterceptVariance () {
            return fitParameterCovarianceMatrix.get( 0, 0 );
        }

        @Override
        public double getStdErrOfA () {
            return Math.sqrt( fitParameterCovarianceMatrix.get( 0, 0 ) );
        }

        @Override
        public double getStdErrOfB () {
            throw new UnsupportedOperationException( "Parameter B is not defined." );
        }

        @Override
        public double getStdErrOfC () {
            throw new UnsupportedOperationException( "Parameter C is not defined." );
        }

        @Override
        public double getVarOfA () {
            return fitParameterCovarianceMatrix.get( 0, 0 );
        }

        @Override
        public double getVarOfB () {
            throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public double getVarOfC () {
            throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public Matrix makeMatrixJ21 ( int countOfActiveData, boolean[] dataActiveMap, double[] neededValues ) {
            return new Matrix( countOfActiveData, 1,  - 1.0 );
        }

        @Override
        public Matrix makeMatrixJ22 ( int countOfActiveData, boolean[] dataActiveMap, double[] neededValues ) {
            return Matrix.identity( countOfActiveData, countOfActiveData );
        }

        @Override
        public boolean fitFound () {
            throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        protected Matrix extractMatrixJIntpFromJpy ( Matrix Jpy ) {
            return Jpy;
        }
    }
}
