/*
 * LevenbergMarquardAlgorithmWithCovS.java
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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.math.BigDecimal;
import org.earthtime.Tripoli.dataModels.collectorModels.AbstractCollectorModel;
import org.earthtime.Tripoli.dataModels.collectorModels.IonCounterCollectorModel;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.dataDictionaries.FitFunctionTypeEnum;
import org.earthtime.dataDictionaries.GoodnessOfFitFunctionTypeEnum;

/**
 *
 * @author James F. Bowring see document at:
 * https://docs.google.com/document/d/1x5tZpTb9uVOS1gqFKDET9VGjZitYDUeXogh-SwRnKlU/edit#
 */
public class LevenbergMarquardAlgorithmWithCovS implements FitFunctionInterface, Serializable {

    // Class variables
    private static final long serialVersionUID = -2199064337889324454L;
    private static LevenbergMarquardAlgorithmWithCovS instance = null;

    private LevenbergMarquardAlgorithmWithCovS () {
        super();
    }

    /**
     *
     * @return
     */
    public static LevenbergMarquardAlgorithmWithCovS getInstance () {
        if ( instance == null ) {
            instance = new LevenbergMarquardAlgorithmWithCovS();
        }
        return instance;
    }

    /**
     * Note: stdErrOfValues not used but part of signature
     *
     * @param dataActiveMap
     * @param xValues
     * @param yValues
     * @param MeasuredCovMatrixS
     * @param calculateHatMatrix
     * @return
     */
    @Override
    public AbstractFunctionOfX getFunctionOfX (//
            boolean[] dataActiveMap, double[] xValues, double[] yValues, Matrix MeasuredCovMatrixS, boolean calculateHatMatrix ) {

        // first call exponential to estimate a,b,c for speed
        FitFunctionInterface exponentialFit = ExponentialFitFunction.getInstance();

        AbstractFunctionOfX expFofX = exponentialFit.getFunctionOfX( dataActiveMap, xValues, yValues, MeasuredCovMatrixS, false );

        // count of paramenters
        int m = 3;
        // determine count of datapoints to be used
        int countOfActiveData = 0;
        for (int i = 0; i < dataActiveMap.length; i ++) {
            if ( dataActiveMap[i] ) {
                countOfActiveData ++;
            }
        }

        // create function of x for exponential
        double lambda = 1000;
        double lambdaFactor = 10.0;
        double chiTolerance = 0.000001;
        boolean deltaChiIsInTolerance = false;

        int maxIterations = 1000;
        double a = expFofX.getA();//0.1;
        double b = expFofX.getB();//0.1;
        double c = expFofX.getC();//0.0;

        AbstractFunctionOfX myFofX = new FofX( a, b, c );

        // step 7 and 8
        Matrix residuals = calculateResiduals( countOfActiveData, dataActiveMap, xValues, yValues, myFofX );

        // step 9 - chiSquared is scalar
        double chiSquared = calculateChiSquared( residuals, MeasuredCovMatrixS );

        int countOfIterations = 0;
        while (countOfIterations < maxIterations && ( ! deltaChiIsInTolerance)) {

            // step 7 and 8 needs to occur each loop
            residuals = calculateResiduals( countOfActiveData, dataActiveMap, xValues, yValues, myFofX );
            // step 9 - chiSquared is scalar
            chiSquared = calculateChiSquared( residuals, MeasuredCovMatrixS );

            double aNew;
            double bNew;
            double cNew;
            Matrix Jacobian;
            //   try {
            // step 10 and 11: populate Jacobian matrix Jacobian
            Jacobian = calculateJacobian( m, countOfActiveData, dataActiveMap, xValues, a, b );

            // step 12: Matrix JTWJ is m x m
            Matrix JTWJ = Jacobian.transpose().times( MeasuredCovMatrixS.solve( Jacobian ) );

            // step 13
            Matrix ldJTWJ = new Matrix( m, m );
            for (int i = 0; i < m; i ++) {
                ldJTWJ.set( i, i, JTWJ.get( i, i ) * lambda );
            }

            // step 14 : hPerturbation is a m x 1 vector
            Matrix A = JTWJ.plus( ldJTWJ );
            Matrix B = MeasuredCovMatrixS.solve( residuals );
            Matrix C = Jacobian.transpose().times( B );

            Matrix hPerturbation;
            try {
                hPerturbation = A.solve( C );
            } catch (Exception e) {
                System.out.println( "Catching NANs  " );
                hPerturbation = null;
            }

            if ( hPerturbation != null ) {
                // step 15
                aNew = a + hPerturbation.get( 0, 0 );
                bNew = b + hPerturbation.get( 1, 0 );
                cNew = c + hPerturbation.get( 2, 0 );

                // step 16 and 17
                AbstractFunctionOfX myFofXNew = new FofX( aNew, bNew, cNew );
                residuals = calculateResiduals( countOfActiveData, dataActiveMap, xValues, yValues, myFofXNew );

                // step 18 - chiSquared is scalar
                double chiSquaredNew = calculateChiSquared( residuals, MeasuredCovMatrixS );

                if ( chiSquaredNew > chiSquared ) {
                    // step 19
                    lambda = lambda * lambdaFactor;

                } else {
                    // step 21
                    a = aNew;
                    b = bNew;
                    c = cNew;

                    myFofX = new FofX( a, b, c );

                    // step 22
                    if ( (1.0 - chiSquaredNew / chiSquared) < chiTolerance ) {
                        deltaChiIsInTolerance = true;
                        // step 23
                        Jacobian = calculateJacobian( m, countOfActiveData, dataActiveMap, xValues, a, b );

                        // step 24
                        try {
                            myFofX.setFitParameterCovarianceMatrix( //
                                    Jacobian.transpose().times( MeasuredCovMatrixS.solve( Jacobian ) ).inverse() );

                            if (  ! ((FofX) myFofX).fitFound() ) {
                                System.out.println( "1 Fit Not Found since Some zero variances:  sigA = " + myFofX.getStdErrOfA() + "  sigB = " + myFofX.getStdErrOfB() + "   sigC = " + myFofX.getStdErrOfC() + "  Iterations = " + countOfIterations );

                                FitFunctionInterface lineFit = LineFitFunctionWithCovS.getInstance();
                                myFofX = lineFit.getFunctionOfX( dataActiveMap, xValues, yValues, MeasuredCovMatrixS, calculateHatMatrix );

                            } else {
                                System.out.println( "L-M Solve: a = " + myFofX.getA() + "  b = " + myFofX.getB() + "   c = " + myFofX.getC() + "  Iterations = " + countOfIterations + " chiSqr = " + chiSquared );

                                myFofX.setChiSquared( chiSquared );
                                myFofX.setMSWD( chiSquared / (countOfActiveData - myFofX.getCountOfParameters()));


                                myFofX.setMatrixJ11( Jacobian.transpose() );

                                if ( calculateHatMatrix ) {
                                    Matrix E = (MeasuredCovMatrixS.solveTranspose( Jacobian.transpose() )).transpose();
//                                    myFofX.setHatMatrix( Jacobian.times( Jacobian.transpose().times( MeasuredCovMatrixS.solve( Jacobian) ) ).times(E) );

                                    myFofX.setHatMatrix( Jacobian.times( //
                                            (Jacobian.transpose().times( MeasuredCovMatrixS.inverse().times( Jacobian ) )).inverse() )//
                                            .times( Jacobian.transpose() ).times( MeasuredCovMatrixS.inverse() ) );
                                }
                            }

                        } catch (Exception e) {
                            // make it a line
                            deltaChiIsInTolerance = true; // kill loop
                            myFofX = LineFitFunctionWithCovS.getInstance().getFunctionOfX( dataActiveMap, xValues, yValues, MeasuredCovMatrixS, calculateHatMatrix );
                        }


                    } else {
                        // step 26
                        lambda = lambda / lambdaFactor;
                    }
                }

                countOfIterations ++;


            } else {
                deltaChiIsInTolerance = true; //countOfIterations = maxIterations;

                FitFunctionInterface lineFit = LineFitFunctionWithCovS.getInstance();
                myFofX = lineFit.getFunctionOfX( dataActiveMap, xValues, yValues, MeasuredCovMatrixS, calculateHatMatrix );

//                myFofX = new FofX(0.0, 0.0, 0.0);
                //break;
            }

        }

        if ( (myFofX.getShortName().equals( FitFunctionTypeEnum.EXPONENTIAL ))//
                &&//
                ( ! ((FofX) myFofX).fitFound()) ) {
            System.out.println( "Fit Not Found a = " + myFofX.getA() + "  b = " + myFofX.getB() + "   c = " + myFofX.getC() + "  Iterations = " + countOfIterations + " chiSqr = " + chiSquared + "  lambda = " + lambda );

            myFofX = LineFitFunctionWithCovS.getInstance().getFunctionOfX( dataActiveMap, xValues, yValues, MeasuredCovMatrixS, calculateHatMatrix );
        }

        myFofX.setCountOfActiveData( countOfActiveData );

        return myFofX;
    }

    private Matrix calculateJacobian ( int m, int countOfActiveData, boolean[] dataActiveMap, double[] xValues, double a, double b ) {
        double[][] JValues = new double[countOfActiveData][m];

        int index = 0;
        for (int i = 0; i < dataActiveMap.length; i ++) {
            if ( dataActiveMap[i] ) {
                JValues[index][0] = 1.0 + Math.expm1(  - b * xValues[i] );
                JValues[index][1] =  - a * xValues[i] * JValues[index][0];
                JValues[index][2] = 1.0;

                index ++;
            }
        }

        return new Matrix( JValues );

    }

    private double calculateChiSquared ( Matrix residuals, Matrix MeasuredCovMatrixS ) {

        double chiSquared = 0.0;
        try {
            chiSquared = residuals.transpose().times( MeasuredCovMatrixS.solve( residuals ) ).get( 0, 0 );
        } catch (Exception e) {
        }

        return chiSquared;
    }

    private Matrix calculateResiduals ( int countOfActiveData, boolean[] dataActiveMap, double[] xValues, double[] yValues, AbstractFunctionOfX myFofX ) {

        double[][] yMinusYHat = new double[countOfActiveData][1];

        // step 8: generate Matrix r
        int index = 0;
        for (int i = 0; i < dataActiveMap.length; i ++) {
            if ( dataActiveMap[i] ) {
                yMinusYHat[index][0] = yValues[i] - myFofX.f( xValues[i] );
                index ++;
            }
        }

        return new Matrix( yMinusYHat );


    }

    private class FofX extends AbstractFunctionOfX implements Serializable {

        // Class variables
        private static final long serialVersionUID = 2465829521407415675L;

        public FofX () {
            super();
        }

        public FofX ( double a, double b, double c ) {
            super( a, b, c );

            this.description = "y = a * e^(-b * x) + c";
            this.shortName = FitFunctionTypeEnum.EXPONENTIAL;
            this.countOfParameters = 3;
        }

        @Override
        public double f ( double x ) {
            return a * (1.0 + Math.expm1(  - b * x )) + c;
        }

        @Override
        public double[] calculateInterpolatedVariances ( Matrix Jf, double[] timesForMatrix ) {

            // since derivatives depend on function, we need to populate Jf
            for (int i = 0; i < Jf.getRowDimension(); i ++) {
                Jf.set( i, 0, Math.exp(  - b * timesForMatrix[i] ) );
                Jf.set( i, 1,  - a * timesForMatrix[i] * Math.exp(  - b * timesForMatrix[i] ) );
                Jf.set( i, 2, 1.0 );
            }

            matrixSf = Jf.times( fitParameterCovarianceMatrix.getMatrix( 0, 2, 0, 2) ).times( Jf.transpose() );

            double[] stdVariances = new double[matrixSf.getRowDimension()];
            for (int i = 0; i < stdVariances.length; i ++) {
                stdVariances[i] = matrixSf.get( i, i );
            }
            
            return stdVariances;
        }

        @Override
        public double getYIntercept () {
            return a + c;
        }

        @Override
        public double getYInterceptStdErr () {
            return Math.sqrt( fitParameterCovarianceMatrix.get( 0, 0 )//
                    + fitParameterCovarianceMatrix.get( 2, 2 )//
                    + 2.0 * fitParameterCovarianceMatrix.get( 0, 2 ) );
        }

        @Override
        public double getYInterceptVariance () {
            return fitParameterCovarianceMatrix.get( 0, 0 )//
                    + fitParameterCovarianceMatrix.get( 2, 2 )//
                    + 2.0 * fitParameterCovarianceMatrix.get( 0, 2 );
        }

        @Override
        public double getStdErrOfA () {
            return Math.sqrt( fitParameterCovarianceMatrix.get( 0, 0 ) );
        }

        @Override
        public double getStdErrOfB () {
            return Math.sqrt( fitParameterCovarianceMatrix.get( 1, 1 ) );
        }

        @Override
        public double getStdErrOfC () {
            return Math.sqrt( fitParameterCovarianceMatrix.get( 2, 2 ) );
        }

        @Override
        public boolean fitFound () {
            boolean retVal = false;
            try {
                retVal = (getStdErrOfA() * getStdErrOfB() * getStdErrOfC() != 0) //
                        &&//
                        ((a + b + c) != 0.0);
            } catch (Exception e) {
            }

            return retVal;
        }

        @Override
        public double getVarOfA () {
            return fitParameterCovarianceMatrix.get( 0, 0 );
        }

        @Override
        public double getVarOfB () {
            return fitParameterCovarianceMatrix.get( 1, 1 );
        }

        @Override
        public double getVarOfC () {
            return fitParameterCovarianceMatrix.get( 2, 2 );
        }

        @Override
        public Matrix makeMatrixJ21 ( int countOfActiveData, boolean[] dataActiveMap, double[] neededValues ) {
            return calculateJacobian( 3, countOfActiveData, dataActiveMap, neededValues, a, b );
        }

        @Override
        public Matrix makeMatrixJ22 ( int countOfActiveData, boolean[] dataActiveMap, double[] neededValues ) {

            Matrix J22 = new Matrix( countOfActiveData, countOfActiveData );
            int index = 0;
            for (int i = 0; i < dataActiveMap.length; i ++) {
                if ( dataActiveMap[i] ) {
                    J22.set( index, index,  - a * b * (1.0 + Math.expm1(  - b * neededValues[index] )) );

                    index ++;
                }
            }

            return J22;
        }

        @Override
        protected Matrix extractMatrixJIntpFromJpy ( Matrix Jpy ) {
            throw new UnsupportedOperationException( "Not supported yet." );
        }
    }
}
