/*
 * ExponentialFitFunction.java
 *
 * Created Jul 26, 2011
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
public class ExponentialFitFunction implements FitFunctionInterface, Serializable {

    // Class variables
    private static final long serialVersionUID = 6722241296005085870L;
    private static ExponentialFitFunction instance = null;

    private ExponentialFitFunction () {
        super();
    }

    /**
     *
     * @return
     */
    public static ExponentialFitFunction getInstance () {
        if ( instance == null ) {
            instance = new ExponentialFitFunction();
        }
        return instance;
    }

    /**
     *
     * @param dataActiveMap
     * @param xValues
     * @param yValues
     * @param stdErrOfYValues
     * @param calculateHatMatrix
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

        // create function of x for exponential
        double a = 0.1;
        double b = 0.1;
        double c = 0.0;
        double lambda = 0.0001; // per Noah McLean email 28 July 2011
        AbstractFunctionOfX myFofX;
        double chiTolerance = 0.001;
        boolean deltaChiIsInTolerance = false;

        myFofX = new FofX( a, b, c );

        int repeat = 0;
        while (repeat < 1000 && ( ! deltaChiIsInTolerance)) {
            repeat ++;

            // calculate beta
            double[] yDiffs = new double[countOfActiveData];
            double chi2a = 0.0;
            double[] dyxida = new double[countOfActiveData];
            double[] dyxidb = new double[countOfActiveData];
            double[] dyxidc = new double[countOfActiveData];
            double[][] beta = new double[3][1];

            int index = 0;
            for (int i = 0; i < dataActiveMap.length; i ++) {
                if ( dataActiveMap[i] ) {
                    yDiffs[index] = yValues[i] - myFofX.f( xValues[i] );
                    chi2a += yDiffs[index] * yDiffs[index];
                    dyxida[index] = ((FofX) myFofX).dyxida( xValues[i] );
                    dyxidb[index] = ((FofX) myFofX).dyxidb( xValues[i] );
                    dyxidc[index] = ((FofX) myFofX).dyxidc( xValues[i] );
                    beta[0][0] += yDiffs[index] * dyxida[index];
                    beta[1][0] += yDiffs[index] * dyxidb[index];
                    beta[2][0] += yDiffs[index] * dyxidc[index];

                    index ++;
                }
            }
            // calcualte alpha
            double[] d2yxi_daa = new double[countOfActiveData];
            double[] d2yxi_dab = new double[countOfActiveData];
            double[] d2yxi_dac = new double[countOfActiveData];
            double[] d2yxi_dba = new double[countOfActiveData];
            double[] d2yxi_dbb = new double[countOfActiveData];
            double[] d2yxi_dbc = new double[countOfActiveData];
            double[] d2yxi_dca = new double[countOfActiveData];
            double[] d2yxi_dcb = new double[countOfActiveData];
            double[] d2yxi_dcc = new double[countOfActiveData];

            index = 0;
            for (int i = 0; i < dataActiveMap.length; i ++) {
                if ( dataActiveMap[i] ) {
                    d2yxi_dab[index] =  - xValues[i] * dyxida[index];
                    d2yxi_dba[index] = d2yxi_dab[index];
                    d2yxi_dbb[index] = ((FofX) myFofX).d2yxi_dbb( xValues[i] );
                    d2yxi_dca[index] = d2yxi_dac[index];
                    d2yxi_dcb[index] = d2yxi_dbc[index];

                    index ++;
                }
            }
            double[][] alpha = new double[3][3];

            index = 0;
            for (int i = 0; i < dataActiveMap.length; i ++) {
                if ( dataActiveMap[i] ) {
                    alpha[0][0] += dyxida[index] * dyxida[index] - yDiffs[index] * d2yxi_daa[index];
                    alpha[0][1] += dyxida[index] * dyxidb[index] - yDiffs[index] * d2yxi_dab[index];
                    alpha[0][2] += dyxida[index] * dyxidc[index] - yDiffs[index] * d2yxi_dac[index];
                    alpha[1][1] += dyxidb[index] * dyxidb[index] - yDiffs[index] * d2yxi_dbb[index];
                    alpha[1][2] += dyxidb[index] * dyxidc[index] - yDiffs[index] * d2yxi_dbc[index];
                    alpha[2][2] += dyxidc[index] * dyxidc[index] - yDiffs[index] * d2yxi_dcc[index];

                    index ++;
                }
            }

            alpha[1][0] = alpha[0][1];
            alpha[2][0] = alpha[0][2];
            alpha[2][1] = alpha[1][2];

            double[][] alphap = alpha.clone();
            alphap[0][0] *= (1.0 + lambda);
            alphap[1][1] *= (1.0 + lambda);
            alphap[2][2] *= (1.0 + lambda);

            // calculate delta a
            Matrix alphapM = new Matrix( alphap );
            Matrix betaM = new Matrix( beta );
            Matrix delta_aM;

            try {
                delta_aM = alphapM.inverse().times( betaM );
            } catch (Exception e) {
                break;
            }

            AbstractFunctionOfX myFofXNew = new FofX(//
                    a + delta_aM.get( 0, 0 ), b + delta_aM.get( 1, 0 ), c + delta_aM.get( 2, 0 ) );

            double[] yDiffsNew = new double[countOfActiveData];
            double chi2aNew = 0.0;

            index = 0;
            for (int i = 0; i < dataActiveMap.length; i ++) {
                if ( dataActiveMap[i] ) {
                    yDiffsNew[index] = yValues[i] - myFofXNew.f( xValues[i] );
                    chi2aNew += yDiffsNew[index] * yDiffsNew[index];

                    index ++;
                }
            }

            if ( chi2aNew > chi2a ) {
                lambda *= 10.0;
            } else {
                lambda /= 10.0;
                a += delta_aM.get( 0, 0 );
                b += delta_aM.get( 1, 0 );
                c += delta_aM.get( 2, 0 );

                myFofX = new FofX( a, b, c );

                // test for delta Chi less than some tolerance such as 0.001 for now
                deltaChiIsInTolerance = (1.0 - (chi2aNew / chi2a) < chiTolerance);
                if ( deltaChiIsInTolerance ) {
                    Matrix alphaM = new Matrix( alpha );
                    alphaM = alphaM.times( 1.0 / chi2aNew );
                    myFofX.setFitParameterCovarianceMatrix( alphaM.inverse() );
                    myFofX.setChiSquared( chi2aNew );
                    myFofX.setMSWD( chi2aNew / (countOfActiveData - myFofX.getCountOfParameters()) );
                    myFofX.setIterations( repeat );
                }

            }
        }
        myFofX.setCountOfActiveData( countOfActiveData );

        return myFofX;
    }

    private class FofX extends AbstractFunctionOfX implements Serializable {

        // Class variables
        private static final long serialVersionUID = 8984845797218408915L;

        public FofX () {
            super();
        }

        public FofX ( double a, double b, double c ) {
            super( a, b, c );

            this.description = "y = a * e^(-b * x) + c";
            this.shortName = FitFunctionTypeEnum.EXPONENTIAL;
            this.countOfParameters = 3;
            this.fitParameterCovarianceMatrix = new Matrix( new double[2][2] );
        }

        @Override
        public double f ( double x ) {
            return a * (1.0 + Math.expm1(  - b * x )) + c;
        }

        @Override
        public double[] calculateInterpolatedVariances ( Matrix Jf, double[] timesForMatrix ) {
            throw new UnsupportedOperationException( "Not supported for exponential." );
        }

        private double dyxida ( double x ) {
            return (f( x ) - c) / a;
        }

        private double dyxidb ( double x ) {
            return (f( x ) - c) *  - x;
        }

        private double dyxidc ( double x ) {
            return 1.0;
        }

        private double d2yxi_dbb ( double x ) {
            return a * x * x * dyxida( x );
        }

        @Override
        public double getYIntercept () {
            return a + c;
        }

        @Override
        public double getYInterceptStdErr () {
            double retVal = Math.sqrt( fitParameterCovarianceMatrix.get( 0, 0 ) + fitParameterCovarianceMatrix.get( 2, 2 ) );
            if ( !Double.isFinite( retVal ) ) {
                retVal = 0.0;
            }
            return retVal;

        }

        @Override
        public double getYInterceptVariance () {
            double retVal = fitParameterCovarianceMatrix.get( 0, 0 ) + fitParameterCovarianceMatrix.get( 2, 2 );
            if ( !Double.isFinite( retVal ) ) {
                retVal = 0.0;
            }
            return retVal;
        }

        @Override
        public double getStdErrOfA () {
            return Math.sqrt( fitParameterCovarianceMatrix.get( 0, 0 ) );
        }

        @Override
        public double getStdErrOfB () {
            double retVal = Math.sqrt( fitParameterCovarianceMatrix.get( 1, 1 ) );
            if ( !Double.isFinite( retVal ) ) {
                retVal = 0.0;
            }
            return Math.sqrt( retVal );
        }

        @Override
        public double getStdErrOfC () {
            return Math.sqrt( fitParameterCovarianceMatrix.get( 2, 2 ) );
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
