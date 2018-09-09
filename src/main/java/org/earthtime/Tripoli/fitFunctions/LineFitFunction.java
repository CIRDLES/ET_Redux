/*
 * LineFitFunction.java
 *
 * Created Jul 26, 2011
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
import org.earthtime.statistics.NonParametricStats;

/**
 *
 * @author James F. Bowring
 */
public class LineFitFunction implements FitFunctionInterface, Serializable {

    // Class variables
    private static final long serialVersionUID = 8389436388529760979L;
    private static LineFitFunction instance = null;

    private LineFitFunction () {
        super();
    }

    /**
     *
     * @return
     */
    public static LineFitFunction getInstance () {
        if ( instance == null ) {
            instance = new LineFitFunction();
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
     * @return
     */
    @Override
    public AbstractFunctionOfX getFunctionOfX ( boolean[] dataActiveMap, double[] xValues, double[] yValues, Matrix Sib, boolean calculateHatMatrix ) {

        // determine count of datapoints to be used
        int countOfActiveData = 0;
        for (int i = 0; i < dataActiveMap.length; i ++) {
            if ( dataActiveMap[i] ) {
                countOfActiveData ++;
            }
        }

        // create a n x 1 vector of yValues and n x 2 matrix of xValues
        double[][] a = new double[countOfActiveData][2];
        double[][] b = new double[countOfActiveData][1];

        int index = 0;
        for (int i = 0; i < dataActiveMap.length; i ++) {
            if ( dataActiveMap[i] ) {
                a[index][0] = 1.0;
                a[index][1] = xValues[i];
                b[index][0] = yValues[i];

                index ++;
            }
        }

        Matrix aM = new Matrix( a );
        Matrix bM = new Matrix( b );

        Matrix C = aM.transpose().times( aM );
        Matrix D = aM.transpose().times( bM );

        // 2-by-1 vector containing the y-intercept and the slope, in that order
        Matrix fitM = C.solve( D );

        // create function of x for LINE
        AbstractFunctionOfX myFofX = new FofX( //
                fitM.get( 1, 0 ), //
                fitM.get( 0, 0 ) );

        // calculate sumSquaredErrors
        double sse = 0.0;
        double[] residuals = new double[dataActiveMap.length];

        for (int i = 0; i < dataActiveMap.length; i ++) {
            if ( dataActiveMap[i] ) {
                residuals[i] = yValues[i] - myFofX.f( xValues[i] );
                sse += Math.pow( residuals[i], 2 );
            }
        }

        myFofX.setChiSquared( sse );
        myFofX.setMSWD( sse / (countOfActiveData - myFofX.getCountOfParameters()) );



        /* per Noah 7 Aug 2011 email
         *  Calculate the variance of the residuals and divide by n.  Call this quantity 's'.  It is a scalar.
         *  Take the inverse of matrix C (from below).  C should be n-by-2, so Cinverse should be 2-by-2.
         *  Multiply 's' by 'Cinverse' to get the covariance matrix.  
         *
         *  The square root of the upper left term in this matrix is the 1-sigma absolute uncertainty in the y-intercept. 
         *  The square root of the lower right term in this matrix is the 1-sigma absolute uncertainty in the slope.
         */

        NonParametricStats nonParametricStats = NonParametricStats.getInstance();
        nonParametricStats.calculateStats( dataActiveMap, residuals );

        double s = nonParametricStats.getVariance() / index;

        myFofX.setFitParameterCovarianceMatrix( C.inverse().times( s ) );


        myFofX.setCountOfActiveData( countOfActiveData );

        return myFofX;
    }

    private class FofX extends AbstractFunctionOfX implements Serializable {

        // Class variables
        private static final long serialVersionUID = 2210407667780469260L;
        //

        public FofX () {
            super();
        }

        public FofX ( double a, double b ) {
            super( a, b, 0 );

            this.description = "y = a*x + b";
            this.shortName = FitFunctionTypeEnum.LINE;
            this.countOfParameters = 2;
        }

        @Override
        public double f ( double x ) {
            return a * x + b;
        }

        @Override
        public double[] calculateInterpolatedVariances ( Matrix Jf, double[] timesForMatrix ) {
            throw new UnsupportedOperationException( "Not supported for simple line." );
        }

        @Override
        public double getYIntercept () {
            return b;
        }

        @Override
        public double getYInterceptStdErr () {
            return getStdErrOfB();
        }

        @Override
        public double getYInterceptVariance () {
            double retVal = fitParameterCovarianceMatrix.get( 0, 0 );
            if ( !Double.isFinite( retVal ) ) {
                retVal = 0.0;
            }
            return retVal;
        }

        @Override
        public double getStdErrOfA () {
            return Math.sqrt( fitParameterCovarianceMatrix.get( 1, 1 ) );
        }

        @Override
        public double getStdErrOfB () {
            double retVal = Math.sqrt( fitParameterCovarianceMatrix.get( 0, 0 ) );
            if ( !Double.isFinite( retVal ) ) {
                retVal = 0.0;
            }
            return retVal;
        }

        @Override
        public double getStdErrOfC () {
            throw new UnsupportedOperationException( "Parameter C is not defined." );
        }

        @Override
        public double getVarOfA () {
            throw new UnsupportedOperationException( "Not supported yet." );
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
            throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public Matrix makeMatrixJ22 ( int countOfActiveData, boolean[] dataActiveMap, double[] neededValues ) {
            throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        public boolean fitFound () {
            throw new UnsupportedOperationException( "Not supported yet." );
        }

        @Override
        protected Matrix extractMatrixJIntpFromJpy ( Matrix Jpy ) {
            throw new UnsupportedOperationException( "Not supported yet." );
        }
    }
}
