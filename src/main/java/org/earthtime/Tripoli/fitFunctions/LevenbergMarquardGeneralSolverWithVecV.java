/*
 * LevenbergMarquardGeneralSolverWithVecV.java
 *
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

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import java.io.Serializable;
import org.earthtime.dataDictionaries.FitFunctionTypeEnum;

/**
 *
 * @author James F. Bowring see document at:
 * https://docs.google.com/document/d/1x5tZpTb9uVOS1gqFKDET9VGjZitYDUeXogh-SwRnKlU/edit#
 */
public class LevenbergMarquardGeneralSolverWithVecV implements FitFunctionInterface, Serializable {

    // Class variables
    //private static final long serialVersionUID = -7497190229192384278L;
    private static LevenbergMarquardGeneralSolverWithVecV instance = null;
    private AbstractOverDispersionLMVecAlgorithm overDispersionLMAlgorithm;

    private LevenbergMarquardGeneralSolverWithVecV() {
        super();
    }

    /**
     *
     * @return
     */
    public static LevenbergMarquardGeneralSolverWithVecV getInstance() {
        if (instance == null) {
            instance = new LevenbergMarquardGeneralSolverWithVecV();
        }
        return instance;
    }

    @Override
    public AbstractFunctionOfX getFunctionOfX(boolean[] dataActiveMap, double[] xValues, double[] yValues, Matrix MeasuredCovMatrixS, boolean calculateHatMatrix) {

        exerciseAlgorithm();

        return overDispersionLMAlgorithm.getInitialFofX();
    }

    /**
     *
     * @param fitFunctionType
     * @param dataActiveMap
     * @param xValues
     * @param yValues
     * @param MeasuredCovMatrixS
     * @param calculateHatMatrix
     * @return
     */
    public AbstractOverDispersionLMVecAlgorithm getSelectedLMAlgorithm(//
            FitFunctionTypeEnum fitFunctionType,//
            boolean[] dataActiveMap, //
            double[] xValues, //
            double[] yValues, //
            Matrix MeasuredCovMatrixS,//
            boolean calculateHatMatrix) {

        overDispersionLMAlgorithm = odFunctionFactory(fitFunctionType);

        overDispersionLMAlgorithm.coldInitializeFunctionAlgorithm(//
                dataActiveMap, xValues, yValues, MeasuredCovMatrixS);

        if ((fitFunctionType.compareTo(FitFunctionTypeEnum.EXPFAST) != 0)//
                && //
                (overDispersionLMAlgorithm.getInitialFofX().getMSWD() >= 1.0)) {
            exerciseAlgorithm();
        } else {
            overDispersionLMAlgorithm.assignMatrixJypToFitFunctions();
        }

        return overDispersionLMAlgorithm;
    }

    /**
     *
     * @param fitFunctionType
     * @param dataActiveMap
     * @param xValues
     * @param yValues
     * @param MeasuredCovMatrixS
     * @param calculateHatMatrix
     * @param initialFofX
     * @return
     */
    public AbstractOverDispersionLMVecAlgorithm getSelectedLMAlgorithmUsingIntialFofX(//
            FitFunctionTypeEnum fitFunctionType,//
            boolean[] dataActiveMap, //
            double[] xValues, //
            double[] yValues, //
            Matrix MeasuredCovMatrixS,//
            boolean calculateHatMatrix,
            AbstractFunctionOfX initialFofX) {

        overDispersionLMAlgorithm = odFunctionFactory(fitFunctionType);

        overDispersionLMAlgorithm.hotInitializeFunctionAlgorithm(//
                dataActiveMap, xValues, yValues, MeasuredCovMatrixS, initialFofX);

        exerciseAlgorithm();

        return overDispersionLMAlgorithm;
    }

    private AbstractFunctionOfX exerciseAlgorithm() {

        // determine count of datapoints to be used
        System.out.println("LM is attempting to fit using " + overDispersionLMAlgorithm.getClass().getSimpleName());
        int countOfActiveData = overDispersionLMAlgorithm.getCountOfActiveData();

        Matrix yHat = overDispersionLMAlgorithm.calcYHat();

        Matrix Vod = overDispersionLMAlgorithm.calcVod();

        Matrix yValuesMatrix = overDispersionLMAlgorithm.makeYValuesMatrix();
        Matrix r = yValuesMatrix.minus(yHat);

        double L = overDispersionLMAlgorithm.calcL(r, Vod);

        overDispersionLMAlgorithm.calcGH(r, Vod);
        Matrix G = overDispersionLMAlgorithm.getG();
        Matrix H = overDispersionLMAlgorithm.getH();

        int iterations = 0;
        int maxIterations = 100;
        double lambda = 1000.0;
        double chiTolerance = 1e-5;  // per noah april 2015 1e-10;

        AbstractFunctionOfX FofX = null;
        boolean doContinue = true;

        while (iterations < maxIterations) {

            Matrix h = null;
            try {
                h = (H.arrayTimes(Matrix.identity(H.getRowDimension(), H.getRowDimension())).times(lambda).plus(H)).solve(G).times(-1.0);
            } catch (Exception e) {
                // for now dirty
                System.out.println("FIT FAILED WITH SINGULAR MATRIX DURING h CALCULATIONS " + "\n");
                iterations = maxIterations;
                doContinue = false;
            }

            if (!Double.isFinite(h.get(0, 0))) {
                System.out.println("FIT FAILED WITH NANs in h " + "\n");
                iterations = maxIterations;
                doContinue = false;
            }

            if (doContinue) {
                double[] savePod = overDispersionLMAlgorithm.getPod().clone();

                double[] newPod = savePod.clone();
                for (int i = 0; i < newPod.length; i++) {
                    newPod[i] += h.get(i, 0);
                }

                overDispersionLMAlgorithm.setPod(newPod);

                yHat = overDispersionLMAlgorithm.calcYHat();

                Matrix VodNew = overDispersionLMAlgorithm.calcVod();

                Matrix rNew = yValuesMatrix.minus(yHat);

                double LNew = overDispersionLMAlgorithm.calcL(rNew, VodNew);

                if ((LNew > L) && Math.abs(1.0 - LNew / L) >= chiTolerance) {
                    // things got worse, so try again
                    lambda *= 10.0;
                    overDispersionLMAlgorithm.setPod(savePod);
                } else {
                    // things got better
                    if (Math.abs(1.0 - LNew / L) < chiTolerance) { // Solved
                        // put test for exp fast here instead
                        if (overDispersionLMAlgorithm instanceof ExponentialFastNoOD) {
                            // we are here because expfast is using this algorithm to get an initial fit for expmat and expod
                            System.out.println("LM found a fit with EXPFAST after " + iterations + "\n");
                            FofX = overDispersionLMAlgorithm.produceFinalFitFunction();
                            FofX.setMatrixJ11(overDispersionLMAlgorithm.getInitialFofX().getMatrixJ11());

                        } else if (overDispersionLMAlgorithm instanceof ExponentialMatNoOD) {
                            // we are here because expfast is using this algorithm to get an initial fit for expmat and expod

                            FofX = overDispersionLMAlgorithm.produceFinalFitFunction();
                            FofX.setMatrixJ11(overDispersionLMAlgorithm.getInitialFofX().getMatrixJ11());

                            FofX.setFitParameterCovarianceMatrix(H.inverse());
                            double sumRnewSqDivVodNew = 0.0;
                            for (int i = 0; i < rNew.getRowDimension(); i++) {
                                sumRnewSqDivVodNew += rNew.get(i, 0) / rNew.get(i, 0) / VodNew.get(i, 0);
                            }
                            FofX.setMSWD(sumRnewSqDivVodNew//
                                    / (countOfActiveData - overDispersionLMAlgorithm.getM()));
                            // per Noah chat 30 may 2013
                            double sumRsqDivVod = 0.0;
                            double prodVod = 0.0;

                            for (int i = 0; i < r.getRowDimension(); i++) {
                                sumRsqDivVod += r.get(0, 0) * r.get(0, 0) / Vod.get(0, 0);
                                prodVod *= Vod.get(0, 0);
                            }

                            L = 0.5 * (sumRsqDivVod + Math.log(prodVod));

                            FofX.setBIC(-2.0 * L + overDispersionLMAlgorithm.getM() * Math.log(countOfActiveData));
                            FofX.setNegativeLogLikelihood(L);
                            FofX.setOverDispersionSelected(false);
                            // test for bad parameter covariances
                            if (!FofX.verifyPositiveVariances()) {
                                FofX = null;
                            } else {
                                System.out.println("LM found a fit with EXPMAT after " + iterations + "\n");
                            }

                        } else if ((overDispersionLMAlgorithm.getOverDispersion() / overDispersionLMAlgorithm.getMeasuredCovMatrixV().get(0, 0)) <= 0.00010) {
                        // no overdispersion
                            // revert to original fit function without overdispersion
                            FofX = overDispersionLMAlgorithm.getInitialFofX();
                            System.out.println("LM with NO OD for " + FofX.getShortNameString() + "\n");

                        } else {
                            //  there is overdispersion = this is the normal case a new fit function with overdispersion
                            FofX = overDispersionLMAlgorithm.produceFinalFitFunction();
                            FofX.setMatrixJ11(overDispersionLMAlgorithm.getInitialFofX().getMatrixJ11());

                            FofX.setFitParameterCovarianceMatrix(H.inverse());
                            double sumRnewSqDivVodNew = 0.0;
                            for (int i = 0; i < rNew.getRowDimension(); i++) {
                                sumRnewSqDivVodNew += rNew.get(i, 0) / rNew.get(i, 0) / VodNew.get(i, 0);
                            }
                            FofX.setMSWD(sumRnewSqDivVodNew//
                                    / (double) (countOfActiveData - overDispersionLMAlgorithm.getM()));
                            FofX.setBIC(-2.0 * L + ((double) overDispersionLMAlgorithm.getM()) * Math.log(countOfActiveData));
                            FofX.setNegativeLogLikelihood(L);
                            FofX.setOverDispersionSelected(true);
                            FofX.setOverDispersion(overDispersionLMAlgorithm.getOverDispersion());

                            // test for bad parameter covariances
                            if (!FofX.verifyPositiveVariances()) {
                                FofX = null;
                            } else {
                                System.out.println("LM with YES OD for " + FofX.getShortNameString() + " after " + iterations + "\n");
                            }
                        }
                        // let's get out of here
                        iterations = maxIterations - 1;
                    } else {
                        // improved but not solved
                        lambda /= 10.0;
                        L = LNew;
                        r = rNew;
                        Vod = VodNew;
                        overDispersionLMAlgorithm.calcGH(r, Vod);
                        G = overDispersionLMAlgorithm.getG();
                        H = overDispersionLMAlgorithm.getH();
                    }
                } // end of things got better

                iterations++;
            } // end of doContinue
        } // end of while

        if (FofX == null) {
            if (iterations == maxIterations) {
                System.out.println("LM did not find a fit" + "\n");
            }
            FofX = overDispersionLMAlgorithm.getInitialFofX();
        }

        // part of section 7a
        if (!(overDispersionLMAlgorithm instanceof LevenbergMarquardGeneralSolverWithVecV.ExponentialFastNoOD)) {
            overDispersionLMAlgorithm.assignMatrixJypToFitFunctions();
        }
        // nov 2014
        if (FofX != null) {
            if (FofX.getMatrixJacobianYInterceptLogRatioXY() == null) {
                FofX.setMatrixJacobianYInterceptLogRatioXY(overDispersionLMAlgorithm.getInitialFofX().getMatrixJacobianYInterceptLogRatioXY());
            }
        }

        return FofX;
    }

    /**
     *
     */
    public abstract class AbstractOverDispersionLMVecAlgorithm implements Serializable, AbstractOverDispersionLMAlgorithmInterface {

        //   private static final long serialVersionUID = -4647454840872488163L;
        // count of parameters
        /**
         *
         */
        protected int m;

        /**
         *
         */
        protected double[] pod;

        /**
         *
         */
        protected double[] xValues;

        /**
         *
         */
        protected double[] yValues;

        /**
         *
         */
        protected Matrix MeasuredCovMatrixS;

        /**
         *
         */
        protected Matrix MeasuredCovVectorV;

        /**
         *
         */
        protected Matrix onesV;

        /**
         *
         */
        protected Matrix onesVt;

        /**
         *
         */
        protected Matrix G;

        /**
         *
         */
        protected Matrix H;

        /**
         *
         */
        protected Matrix VodInv;

        /**
         *
         */
        protected Matrix VodInvR;
        // the isnatnce that will cerate the fit function

        /**
         *
         */
        protected FitFunctionInterface initialFuncFit;
        // the fit function used to estimate the parameters for the fit function

        /**
         *
         */
        protected AbstractFunctionOfX initialFofX;
        // the asked-for fit function (generally OD-flavor but could be the exponential without OD as well)

        /**
         *
         */
        protected AbstractFunctionOfX finalFofX;

        /**
         *
         * @param m
         */
        public AbstractOverDispersionLMVecAlgorithm(int m) {
            this.m = m;
            this.pod = new double[m];
            this.G = new Matrix(m, 1, 0.0);
            this.H = new Matrix(m, m, 0.0);

            this.initialFofX = null;
            this.finalFofX = null;

        }

        /**
         *
         * @param dataActiveMap
         * @param xValues
         * @param yValues
         * @param MeasuredCovMatrixS
         * @param myInitialFofX
         */
        protected void hotInitializeFunctionAlgorithm(boolean[] dataActiveMap, double[] xValues, double[] yValues, Matrix MeasuredCovMatrixS, AbstractFunctionOfX myInitialFofX) {

            initializeFunctionAlgorithm(dataActiveMap, xValues, yValues, MeasuredCovMatrixS);

            initialFofX = myInitialFofX;

            if (initialFofX != null) {
                initializeFunctionParameters(MeasuredCovMatrixS.get(0, 0) * 0.1);
            }
        }

        /**
         *
         * @param dataActiveMap
         * @param xValues
         * @param yValues
         * @param MeasuredCovVectorV
         */
        protected void coldInitializeFunctionAlgorithm(boolean[] dataActiveMap, double[] xValues, double[] yValues, Matrix MeasuredCovVectorV) {

            initializeFunctionAlgorithm(dataActiveMap, xValues, yValues, MeasuredCovVectorV);

            if (this instanceof ExponentialFastNoOD) {
                // this first call is to prime the pump for internal exp-fast and mat
//                this.xValues = xValues; // needed for data count
                initializeFunctionParameters(0.0); // OD not used here
                initialFofX = initialFuncFit.getFunctionOfX(dataActiveMap, xValues, yValues, MeasuredCovVectorV, false);
            } else {

                initialFofX = initialFuncFit.getFunctionOfX(dataActiveMap, xValues, yValues, MeasuredCovVectorV, false);
                if (initialFofX != null) {
                    initializeFunctionParameters(MeasuredCovVectorV.get(0, 0) * 0.1);
                }
            }

        }

        private void initializeFunctionAlgorithm(boolean[] dataActiveMap, double[] xValues, double[] yValues, Matrix MeasuredCovMatrixS) {

            this.xValues = xValues;
            this.yValues = yValues;
            this.MeasuredCovMatrixS = MeasuredCovMatrixS;
            // strip out diagonal
            this.MeasuredCovVectorV = makeMeasuredCovVectorV(MeasuredCovMatrixS);

            onesV = new Matrix(yValues.length, 1, 1.0);

        }

        /**
         *
         * @return
         */
        protected Matrix makeYValuesMatrix() {
            return new Matrix(yValues, yValues.length);
        }

        /**
         *
         * @return
         */
        protected int getCountOfActiveData() {
            return yValues.length;
        }

        /**
         *
         * @return
         */
        protected abstract AbstractFunctionOfX produceFinalFitFunction();

        /**
         *
         */
        protected abstract void assignMatrixJypToFitFunctions();

        /**
         *
         * @return
         */
        public AbstractFunctionOfX getInitialFofX() {
            return initialFofX;
        }

        /**
         *
         * @return
         */
        public AbstractFunctionOfX getFinalFofX() {
            return finalFofX;
        }

        /**
         *
         * @param overDispersionEstimate
         */
        protected abstract void initializeFunctionParameters(double overDispersionEstimate);

        /**
         *
         * @return
         */
        protected abstract Matrix calcYHat();

        /**
         *
         * @param r
         * @param Vod
         */
        protected void calcGH(Matrix r, Matrix Vod) {
            VodInv = Vod.copy();
            VodInvR = Vod.copy();

            for (int i = 0; i < Vod.getRowDimension(); i++) {
                VodInv.set(i, 0, 1.0 / Vod.get(i, 0));
                VodInvR.set(i, 0, r.get(i, 0) / Vod.get(i, 0));
            }
        }

        /**
         *
         * @return
         */
        protected abstract Matrix calcVod();

        /**
         *
         * @param r
         * @param Vod
         * @return
         */
        protected abstract double calcL(Matrix r, Matrix Vod);

        /**
         *
         * @param S
         * @return
         */
        protected double reduxMatrixLogDeterminant(Matrix S) {
            double det = 0.0;

            EigenvalueDecomposition ed = S.eig();
            double[] realEigenvalues = ed.getRealEigenvalues();

            double sumOfLogEigenvalues = 0.0;
            for (int i = 0; i < realEigenvalues.length; i++) {
                sumOfLogEigenvalues += Math.log(realEigenvalues[i]);
            }

            return det;
        }

        /**
         *
         * @return
         */
        public int getM() {
            return m;
        }

        /**
         *
         * @return
         */
        public double[] getPod() {
            return pod;
        }

        /**
         *
         * @param pod
         */
        public void setPod(double[] pod) {
            this.pod = pod;
        }

        /**
         *
         * @return
         */
        public double getOverDispersion() {
            return pod[m - 1];
        }

        /**
         *
         * @return
         */
        public Matrix getG() {
            return G;
        }

        /**
         *
         * @return
         */
        public Matrix getH() {
            return H;
        }

        /**
         * @return the MeasuredCovMatrixS
         */
        public Matrix getMeasuredCovMatrixV() {
            return MeasuredCovVectorV;
        }

        /**
         *
         * @return
         */
        public Matrix getMeasuredCovMatrixS() {
            return MeasuredCovMatrixS;
        }
    }

    private class MeanOD extends AbstractOverDispersionLMVecAlgorithm {

        public MeanOD() {
            super(2);

            initialFuncFit = MeanFitFunctionWithCovS.getInstance();
        }

        @Override
        protected void initializeFunctionParameters(double overDispersionEstimate) {
            pod[0] = initialFofX.getA();
            pod[1] = overDispersionEstimate;
        }

        @Override
        protected AbstractFunctionOfX produceFinalFitFunction() {
            finalFofX = new OdMeanFofX(pod[0]);

            return finalFofX;
        }

        @Override
        protected Matrix calcYHat() {
            Matrix yHat = onesV.times(pod[0]);

            return yHat;
        }

        /**
         *
         */
        @Override
        protected Matrix calcVod() {
            Matrix retVal = MeasuredCovVectorV.copy();
            for (int i = 0; i < MeasuredCovVectorV.getRowDimension(); i++) {
                retVal.set(i, 0, MeasuredCovVectorV.get(i, 0) + getOverDispersion());
            }
            return retVal;
        }

        @Override
        protected double calcL(Matrix r, Matrix Vod) {
            return 1.0;
            //return 0.5 * (r.transpose().times( Sod.solve( r ) ).get( 0, 0 ) + reduxMatrixLogDeterminant( Sod ));//   Math.log(Sod.det()));
        }

        @Override
        protected void calcGH(Matrix r, Matrix Vod) {
            super.calcGH(r, Vod);

            double sumVodInv = 0.0;
            double sumVodInvSq = 0.0;
            double sumVodInvRSqDivVod = 0.0;
            for (int i = 0; i < VodInv.getRowDimension(); i++) {
                sumVodInv += VodInv.get(i, 0);
                sumVodInvSq += VodInv.get(i, 0) * VodInv.get(i, 0);
                sumVodInvRSqDivVod += VodInvR.get(i, 0) * VodInvR.get(i, 0) / Vod.get(i, 0);
            }

            G.set(0, 0, -1.0 * onesV.transpose().times(VodInvR).get(0, 0));
            G.set(1, 0, -0.5 * ((VodInvR.times(VodInvR.transpose())).get(0, 0) - sumVodInv));

            Matrix VodInv1 = Vod.copy();
            for (int i = 0; i < Vod.getRowDimension(); i++) {
                VodInv1.set(i, 0, 1.0 / Vod.get(i, 0));
            }

            H.set(0, 0, onesV.transpose().times(VodInv1).get(0, 0));
            H.set(0, 1, VodInv1.transpose().times(VodInvR).get(0, 0));
            H.set(1, 0, H.get(0, 1));
            H.set(1, 1, sumVodInvRSqDivVod - 0.5 * sumVodInvSq);

        }

        @Override
        protected void assignMatrixJypToFitFunctions() {
            if (initialFofX != null) {
                initialFofX.setMatrixJyp(onesV);
            }

            if (finalFofX != null) {
                finalFofX.setMatrixJyp(onesV);
            }
        }
    }

    private class LineOD extends AbstractOverDispersionLMVecAlgorithm {

        public LineOD() {
            super(3);

            initialFuncFit = LineFitFunctionWithCovS.getInstance();
        }

        @Override
        protected void initializeFunctionParameters(double overDispersionEstimate) {
            pod[1] = initialFofX.getA();
            pod[0] = initialFofX.getB();
            pod[2] = overDispersionEstimate;
        }

        @Override
        protected AbstractFunctionOfX produceFinalFitFunction() {
            finalFofX = new OdLineFofX(pod[1], pod[0]);

            return finalFofX;
        }

        @Override
        protected Matrix calcYHat() {
            double[] t = xValues.clone();
            for (int i = 0; i < t.length; i++) {
                t[i] = pod[0] + t[i] * pod[1];
            }
            Matrix yHat = new Matrix(t, t.length);

            return yHat;
        }

        /**
         *
         */
        @Override
        protected Matrix calcVod() {
            Matrix retVal = MeasuredCovVectorV.copy();
            for (int i = 0; i < MeasuredCovVectorV.getRowDimension(); i++) {
                retVal.set(i, 0, MeasuredCovVectorV.get(i, 0) + getOverDispersion());
            }
            return retVal;

        }

        @Override
        protected double calcL(Matrix r, Matrix Vod) {
            return 1.0;
            //return 0.5 * (r.transpose().times( Sod.solve( r ) ).get( 0, 0 ) + reduxMatrixLogDeterminant( Sod )); //Math.log(Sod.det()));
        }

        @Override
        protected void calcGH(Matrix r, Matrix Vod) {
            super.calcGH(r, Vod);

            double sumVodInv = 0.0;
            double sumVodInvRSqDivVod = 0.0;
            for (int i = 0; i < VodInv.getRowDimension(); i++) {
                sumVodInv += VodInv.get(i, 0);
                sumVodInvRSqDivVod += VodInvR.get(i, 0) * VodInvR.get(i, 0) / Vod.get(i, 0);
            }

            Matrix t = new Matrix(xValues.clone(), xValues.length);
            onesVt = new Matrix(onesV.getRowDimension(), 2, 1.0);
            onesVt.setMatrix(0, onesV.getRowDimension() - 1, 1, 1, t);

            G.setMatrix(0, 1, 0, 0, onesVt.transpose().times(VodInvR).times(-1.0));
            G.set(2, 0, -0.5 * ((VodInvR.times(VodInvR.transpose())).get(0, 0) - sumVodInv));

            Matrix VodInv1 = Vod.copy();
            double sumVodInv1 = 0.0;
            for (int i = 0; i < Vod.getRowDimension(); i++) {
                VodInv1.set(i, 0, 1.0 / Vod.get(i, 0));
                sumVodInv1 += VodInv1.get(i, 0);
            }

            Matrix onesVtDivVod = onesVt.copy();
            Matrix tDivVod = t.copy();
            for (int i = 0; i < onesVtDivVod.getRowDimension(); i++) {
                onesVtDivVod.set(i, 0, onesVtDivVod.get(i, 0) / Vod.get(i, 0));
                onesVtDivVod.set(i, 1, onesVtDivVod.get(i, 1) / Vod.get(i, 0));
                tDivVod.set(i, 0, t.get(i, 0) / Vod.get(i, 0));
            }

            H.setMatrix(0, 1, 0, 1, onesVtDivVod.transpose().times(onesVt));
            H.set(0, 2, VodInv1.transpose().times(VodInvR).get(0, 0));
            H.set(2, 0, H.get(0, 2));
            H.set(1, 2, tDivVod.transpose().times(VodInvR).get(0, 0));
            H.set(2, 1, H.get(1, 2));
            H.set(2, 2, sumVodInvRSqDivVod - 0.5 * sumVodInv);

        }

        @Override
        public void setPod(double[] pod) {
            pod[2] = Math.abs(pod[2]);

            this.pod = pod;
        }

        @Override
        protected void assignMatrixJypToFitFunctions() {
            Matrix t = new Matrix(xValues.clone(), xValues.length);
            onesVt = new Matrix(onesV.getRowDimension(), 2, 1.0);
            onesVt.setMatrix(0, onesV.getRowDimension() - 1, 1, 1, t);

            if (initialFofX != null) {
                initialFofX.setMatrixJyp(onesVt);
            }

            if (finalFofX != null) {
                finalFofX.setMatrixJyp(onesVt);
            }
        }
    }

    private class ExponentialOD extends AbstractOverDispersionLMVecAlgorithm {

        private static final long serialVersionUID = 8296680592386714994L;

        public ExponentialOD() {
            super(4);

            initialFuncFit = ExponentialFitFunction.getInstance();
        }

        @Override
        protected void initializeFunctionParameters(double overDispersionEstimate) {
            pod[0] = initialFofX.getA();
            pod[1] = initialFofX.getB();
            pod[2] = initialFofX.getC();
            pod[3] = overDispersionEstimate;
        }

        @Override
        protected AbstractFunctionOfX produceFinalFitFunction() {
            finalFofX = new ExponentialFofX(pod[0], pod[1], pod[2]);

            return finalFofX;
        }

        @Override
        protected Matrix calcYHat() {
            double[] t = xValues.clone();
            for (int i = 0; i < t.length; i++) {
                t[i] = pod[0] * Math.exp(t[i] * pod[1]) + pod[2];
            }

            Matrix yHat = new Matrix(t, t.length);

            return yHat;
        }

        /**
         *
         */
        @Override
        protected Matrix calcVod() {
            Matrix retVal = MeasuredCovVectorV.copy();
            for (int i = 0; i < MeasuredCovVectorV.getRowDimension(); i++) {
                retVal.set(i, 0, MeasuredCovVectorV.get(i, 0) + getOverDispersion());
            }
            return retVal;
        }

        @Override
        protected double calcL(Matrix r, Matrix Vod) {
            double sumRsqDivVod = 0.0;
            double prodVod = 0.0;

            for (int i = 0; i < r.getRowDimension(); i++) {
                sumRsqDivVod += r.get(0, 0) * r.get(0, 0) / Vod.get(0, 0);
                prodVod = prodVod * Vod.get(0, 0);
            }

            return 0.5 * (sumRsqDivVod + Math.log(prodVod));
        }

        @Override
        protected void calcGH(Matrix r, Matrix Vod) {
            super.calcGH(r, Vod);

            double[] JaValues = xValues.clone();
            for (int i = 0; i < JaValues.length; i++) {
                JaValues[i] = Math.exp(JaValues[i] * pod[1]);
            }

            Matrix Ja = new Matrix(JaValues, JaValues.length);

            Matrix t = new Matrix(xValues.clone(), xValues.length);
            Matrix Jba = t.arrayTimes(Ja);

            Matrix Jb = Jba.times(pod[0]);
            Matrix Jbb = t.arrayTimes(Jb);

            int n = yValues.length;
            Matrix Jabc = new Matrix(n, 3);
            Jabc.setMatrix(0, n - 1, 0, 0, Ja);
            Jabc.setMatrix(0, n - 1, 1, 1, Jb);
            Jabc.setMatrix(0, n - 1, 2, 2, onesV);

            Matrix VodInvJabc = Jabc.copy();
            for (int i = 0; i < Jabc.getRowDimension(); i++) {
                VodInvJabc.set(i, 0, VodInvJabc.get(i, 0) / Vod.get(i, 0));
                VodInvJabc.set(i, 1, VodInvJabc.get(i, 1) / Vod.get(i, 0));
                VodInvJabc.set(i, 2, VodInvJabc.get(i, 2) / Vod.get(i, 0));
            }

            double sumVodInv = 0.0;
            double sumVodInvRSqDivVod = 0.0;
            for (int i = 0; i < VodInv.getRowDimension(); i++) {
                sumVodInv += VodInv.get(i, 0);
                sumVodInvRSqDivVod += VodInvR.get(i, 0) * VodInvR.get(i, 0) / Vod.get(i, 0);
            }

            G.setMatrix(0, 2, 0, 0, Jabc.transpose().times(VodInvR).times(-1.0));
            G.set(3, 0, (-0.5) * (VodInvR.transpose().times(VodInvR).get(0, 0) - sumVodInv));

            H.setMatrix(0, 2, 0, 2, Jabc.transpose().times(VodInvJabc));
            H.set(1, 1, H.get(1, 1) - VodInvR.transpose().times(Jbb).get(0, 0));
            H.set(0, 1, H.get(0, 1) - VodInvR.transpose().times(Jba).get(0, 0));
            H.set(1, 0, H.get(0, 1));
            H.setMatrix(0, 2, 3, 3, VodInvJabc.transpose().times(VodInvR));
            H.setMatrix(3, 3, 0, 2, H.getMatrix(0, 2, 3, 3).transpose());
            H.set(3, 3, sumVodInvRSqDivVod - 0.5 * VodInv.transpose().times(VodInv).get(0, 0));

        }

        @Override
        protected void assignMatrixJypToFitFunctions() {

            double[] JaValues = xValues.clone();
            for (int i = 0; i < JaValues.length; i++) {
                JaValues[i] = Math.exp(JaValues[i] * pod[1]);
            }

            Matrix Ja = new Matrix(JaValues, JaValues.length);

            Matrix t = new Matrix(xValues.clone(), xValues.length);
            Matrix Jba = t.arrayTimes(Ja);

            Matrix Jb = Jba.times(pod[0]);
            Matrix Jbb = t.arrayTimes(Jb);

            int n = yValues.length;
            Matrix Jabc = new Matrix(n, 3);
            Jabc.setMatrix(0, n - 1, 0, 0, Ja);
            Jabc.setMatrix(0, n - 1, 1, 1, Jb);
            Jabc.setMatrix(0, n - 1, 2, 2, onesV);

            if (initialFofX != null) {
                initialFofX.setMatrixJyp(Jabc);
            }

            if (finalFofX != null) {
                finalFofX.setMatrixJyp(Jabc);
            }
        }
    }

    private class ExponentialFastNoOD extends AbstractOverDispersionLMVecAlgorithm {

        public ExponentialFastNoOD() {
            super(3);

            initialFuncFit = LevenbergMarquardGeneralSolverWithVecV.getInstance();
        }

        @Override
        protected AbstractFunctionOfX produceFinalFitFunction() {
            // the same because this EXPFAST is used to prime EXPMAT AND EXPOD
            finalFofX = new ExponentialFofX(pod[0], pod[1], pod[2]);
            initialFofX = new ExponentialFofX(pod[0], pod[1], pod[2]);

            return finalFofX;
        }

        @Override
        protected void initializeFunctionParameters(double overDispersionEstimate) {
            pod = FitFunctionInterface.initializeExpFastParameters(xValues, yValues);
        }

        @Override
        protected Matrix calcYHat() {
            double[] t = xValues.clone();
            for (int i = 0; i < t.length; i++) {
                t[i] = pod[0] * Math.exp(t[i] * pod[1]) + pod[2];
            }

            Matrix yHat = new Matrix(t, t.length);

            return yHat;
        }

        /**
         *
         */
        @Override
        protected Matrix calcVod() {
            return null; // not used, so this should not hit
        }

        @Override
        protected double calcL(Matrix r, Matrix Vod) {
            return r.transpose().times(r).get(0, 0);
        }

        @Override
        protected void calcGH(Matrix r, Matrix Vod) {
            double[] JaValues = xValues.clone();
            for (int i = 0; i < JaValues.length; i++) {
                JaValues[i] = Math.exp(JaValues[i] * pod[1]);
            }

            Matrix Ja = new Matrix(JaValues, JaValues.length);

            Matrix t = new Matrix(xValues.clone(), xValues.length);
            Matrix Jba = t.arrayTimes(Ja);

            Matrix Jb = Jba.times(pod[0]);
            Matrix Jbb = t.arrayTimes(Jb);

            int n = yValues.length;
            Matrix Jabc = new Matrix(n, 3);
            Jabc.setMatrix(0, n - 1, 0, 0, Ja);
            Jabc.setMatrix(0, n - 1, 1, 1, Jb);
            Jabc.setMatrix(0, n - 1, 2, 2, onesV);

            G.setMatrix(0, 2, 0, 0, Jabc.transpose().times(r).times(-1.0));

            H.setMatrix(0, 2, 0, 2, Jabc.transpose().times(Jabc));
            H.set(1, 1, H.get(1, 1) - r.transpose().times(Jbb).get(0, 0));
            H.set(0, 1, H.get(0, 1) - r.transpose().times(Jba).get(0, 0));
            H.set(1, 0, H.get(0, 1));
        }

        @Override
        protected void assignMatrixJypToFitFunctions() {
            //System.out.println("nothing to do at 754 expfastnood");// do nothing
        }
    }

    private class ExponentialMatNoOD extends AbstractOverDispersionLMVecAlgorithm {

        public ExponentialMatNoOD() {
            super(3);

            initialFuncFit = LevenbergMarquardGeneralSolverWithVecV.getInstance();
        }

        @Override
        protected AbstractFunctionOfX produceFinalFitFunction() {
            finalFofX = new ExponentialFofX(pod[0], pod[1], pod[2]);

            return finalFofX;
        }

        @Override
        protected void initializeFunctionParameters(double overDispersionEstimate) {
            pod[0] = initialFofX.getA();
            pod[1] = initialFofX.getB();
            pod[2] = initialFofX.getC();
        }

        @Override
        protected Matrix calcYHat() {
            double[] t = xValues.clone();
            for (int i = 0; i < t.length; i++) {
                t[i] = pod[0] * Math.exp(t[i] * pod[1]) + pod[2];
            }

            Matrix yHat = new Matrix(t, t.length);

            return yHat;
        }

        /**
         *
         */
        @Override
        protected Matrix calcVod() {
            return MeasuredCovVectorV;
        }

        @Override
        protected double calcL(Matrix r, Matrix Vod) {

            double sumRsqDivVod = 0.0;

            for (int i = 0; i < r.getRowDimension(); i++) {
                sumRsqDivVod += r.get(0, 0) * r.get(0, 0) / Vod.get(0, 0);
            }

            return sumRsqDivVod;
        }

        @Override
        protected void calcGH(Matrix r, Matrix Vod) {
            double[] JaValues = xValues.clone();
            for (int i = 0; i < JaValues.length; i++) {
                JaValues[i] = Math.exp(JaValues[i] * pod[1]);
            }

            Matrix Ja = new Matrix(JaValues, JaValues.length);

            Matrix t = new Matrix(xValues.clone(), xValues.length);
            Matrix Jba = t.arrayTimes(Ja);

            Matrix Jb = Jba.times(pod[0]);
            Matrix Jbb = t.arrayTimes(Jb);

            int n = yValues.length;
            Matrix Jabc = new Matrix(n, 3);
            Jabc.setMatrix(0, n - 1, 0, 0, Ja);
            Jabc.setMatrix(0, n - 1, 1, 1, Jb);
            Jabc.setMatrix(0, n - 1, 2, 2, onesV);

            VodInvR = r.copy();
            Matrix VodInvJabc = Jabc.copy();
            for (int i = 0; i < r.getRowDimension(); i++) {
                VodInvR.set(i, 0, r.get(i, 0) / Vod.get(i, 0));
                VodInvJabc.set(i, 0, Jabc.get(i, 0) / Vod.get(i, 0));
                VodInvJabc.set(i, 1, Jabc.get(i, 1) / Vod.get(i, 0));
                VodInvJabc.set(i, 2, Jabc.get(i, 2) / Vod.get(i, 0));
            }

            G.setMatrix(0, 2, 0, 0, Jabc.transpose().times(VodInvR).times(-1.0));

            H.setMatrix(0, 2, 0, 2, Jabc.transpose().times(VodInvJabc));
            H.set(1, 1, H.get(1, 1) - VodInvR.transpose().times(Jbb).get(0, 0));
            H.set(0, 1, H.get(0, 1) - VodInvR.transpose().times(Jba).get(0, 0));
            H.set(1, 0, H.get(0, 1));

        }

        @Override
        protected void assignMatrixJypToFitFunctions() {
            double[] JaValues = xValues.clone();
            for (int i = 0; i < JaValues.length; i++) {
                JaValues[i] = Math.exp(JaValues[i] * pod[1]);
            }

            Matrix Ja = new Matrix(JaValues, JaValues.length);

            Matrix t = new Matrix(xValues.clone(), xValues.length);
            Matrix Jba = t.arrayTimes(Ja);

            Matrix Jb = Jba.times(pod[0]);
            Matrix Jbb = t.arrayTimes(Jb);

            int n = yValues.length;
            Matrix Jabc = new Matrix(n, 3);
            Jabc.setMatrix(0, n - 1, 0, 0, Ja);
            Jabc.setMatrix(0, n - 1, 1, 1, Jb);
            Jabc.setMatrix(0, n - 1, 2, 2, onesV);

            if (initialFofX != null) {
                initialFofX.setMatrixJyp(Jabc);
            }

            if (finalFofX != null) {
                finalFofX.setMatrixJyp(Jabc);
            }
        }
    }

    private AbstractOverDispersionLMVecAlgorithm odFunctionFactory(FitFunctionTypeEnum fitFunctionType) {

        AbstractOverDispersionLMVecAlgorithm odFunction = null;

        if (fitFunctionType.compareTo(FitFunctionTypeEnum.MEAN) == 0) {
            odFunction = new MeanOD();

        } else if (fitFunctionType.compareTo(FitFunctionTypeEnum.LINE) == 0) {
            odFunction = new LineOD();

        } else if (fitFunctionType.compareTo(FitFunctionTypeEnum.EXPONENTIAL) == 0) {
            odFunction = new ExponentialOD();

        } else if (fitFunctionType.compareTo(FitFunctionTypeEnum.EXPFAST) == 0) {
            odFunction = new ExponentialFastNoOD();

        } else if (fitFunctionType.compareTo(FitFunctionTypeEnum.EXPMAT) == 0) {
            odFunction = new ExponentialMatNoOD();
        }
        return odFunction;

    }

    private class OdMeanFofX extends AbstractFunctionOfX implements Serializable {

        // Class variables
        private static final long serialVersionUID = -3696787604088419904L;

        public OdMeanFofX() {
            super();
        }

        public OdMeanFofX(double a) {
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
            // Matrix Jf not used in this fitfunction

            matrixSf = new Matrix(timesForMatrix.length, timesForMatrix.length, fitParameterCovarianceMatrix.get(0, 0));

            double[] stdVariances = new double[matrixSf.getRowDimension()];
            for (int i = 0; i < stdVariances.length; i++) {
                stdVariances[i] = matrixSf.get(i, i);
            }
            return stdVariances;
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
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public double getVarOfC() {
            throw new UnsupportedOperationException("Not supported yet.");
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
        protected Matrix extractMatrixJIntpFromJpy(Matrix Jpy) {
            return Jpy;
        }
    }

    private class OdLineFofX extends AbstractFunctionOfX implements Serializable {

        // Class variables
        private static final long serialVersionUID = 4545830378710089175L;
        //

        public OdLineFofX() {
            super();
        }

        public OdLineFofX(double a, double b) {
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
        protected Matrix extractMatrixJIntpFromJpy(Matrix Jpy) {
//            return Jpy.getMatrix( 0, Jpy.getRowDimension() - 1, 0, 0 );
            return Jpy.getMatrix(0, 0, 0, Jpy.getColumnDimension() - 1);
        }
    }

    private class ExponentialFofX extends AbstractFunctionOfX implements Serializable {

        // Class variables
        private static final long serialVersionUID = -4263868733369283360L;

        public ExponentialFofX() {
            super();
        }

        public ExponentialFofX(double a, double b, double c) {
            super(a, b, c);

            this.description = "y = a * e^(b * x) + c";
            this.shortName = FitFunctionTypeEnum.EXPONENTIAL;
            this.countOfParameters = 3;
        }

        @Override
        public double f(double x) {
            return a * (Math.exp(b * x)) + c;
        }

        @Override
        public double[] calculateInterpolatedVariances(Matrix Jf, double[] timesForMatrix) {

            // since derivatives depend on function, we need to populate Jf
            for (int i = 0; i < Jf.getRowDimension(); i++) {
                Jf.set(i, 0, Math.exp(b * timesForMatrix[i]));
                Jf.set(i, 1, a * timesForMatrix[i] * Math.exp(b * timesForMatrix[i]));
                Jf.set(i, 2, 1.0);
            }

            matrixSf = Jf.times(fitParameterCovarianceMatrix.getMatrix(0, 2, 0, 2)).times(Jf.transpose());

            double[] stdVariances = new double[matrixSf.getRowDimension()];
            for (int i = 0; i < stdVariances.length; i++) {
                stdVariances[i] = matrixSf.get(i, i);
            }

            return stdVariances;
        }

        @Override
        public double getYIntercept() {
            return a + c;
        }

        @Override
        public double getYInterceptStdErr() {
            return Math.sqrt(fitParameterCovarianceMatrix.get(0, 0)//
                    + fitParameterCovarianceMatrix.get(2, 2)//
                    + 2.0 * fitParameterCovarianceMatrix.get(0, 2));
        }

        @Override
        public double getYInterceptVariance() {
            return fitParameterCovarianceMatrix.get(0, 0)//
                    + fitParameterCovarianceMatrix.get(2, 2)//
                    + 2.0 * fitParameterCovarianceMatrix.get(0, 2);
        }

        @Override
        public double getStdErrOfA() {
            return Math.sqrt(fitParameterCovarianceMatrix.get(0, 0));
        }

        @Override
        public double getStdErrOfB() {
            return Math.sqrt(fitParameterCovarianceMatrix.get(1, 1));
        }

        @Override
        public double getStdErrOfC() {
            return Math.sqrt(fitParameterCovarianceMatrix.get(2, 2));
        }

        @Override
        public boolean fitFound() {
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
        public double getVarOfA() {
            return fitParameterCovarianceMatrix.get(0, 0);
        }

        @Override
        public double getVarOfB() {
            return fitParameterCovarianceMatrix.get(1, 1);
        }

        @Override
        public double getVarOfC() {
            return fitParameterCovarianceMatrix.get(2, 2);
        }

        @Override
        public Matrix makeMatrixJ21(int countOfActiveData, boolean[] dataActiveMap, double[] neededValues) {
            return null;//calculateJacobian( 3, countOfActiveData, dataActiveMap, neededValues, a, b );
        }

        @Override
        public Matrix makeMatrixJ22(int countOfActiveData, boolean[] dataActiveMap, double[] neededValues) {

//            Matrix J22 = new Matrix( countOfActiveData, countOfActiveData );
//            int index = 0;
//            for (int i = 0; i < dataActiveMap.length; i ++) {
//                if ( dataActiveMap[i] ) {
//                    J22.set( index, index,  - a * b * (1.0 + Math.expm1(  - b * neededValues[index] )) );
//
//                    index ++;
//                }
//            }
            return null;//J22;
        }

        @Override
        protected Matrix extractMatrixJIntpFromJpy(Matrix Jpy) {
//            return Jpy.getMatrix( 0, Jpy.getRowDimension() - 1, 0, 0 ).plus( Jpy.getMatrix( 0, Jpy.getRowDimension() - 1, 2, 2 ) );
            return Jpy.getMatrix(0, 0, 0, Jpy.getColumnDimension() - 1).plus(Jpy.getMatrix(2, 2, 0, Jpy.getColumnDimension() - 1));
        }
    }

    private Matrix makeMeasuredCovVectorV(Matrix MeasuredCovMatrixS) {
        Matrix MeasuredCovVectorV = new Matrix(MeasuredCovMatrixS.getRowDimension(), 1);
        for (int i = 0; i < MeasuredCovVectorV.getRowDimension(); i++) {
            MeasuredCovVectorV.set(i, 0, MeasuredCovMatrixS.get(i, i));
        }

        return MeasuredCovVectorV;
    }
}
