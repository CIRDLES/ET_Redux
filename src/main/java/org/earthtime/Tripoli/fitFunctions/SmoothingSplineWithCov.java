/*
 * SmoothingSplineWithCov.java
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
import java.util.ArrayList;
import org.earthtime.dataDictionaries.FitFunctionTypeEnum;

/**
 *
 * @author James F. Bowring
 */
public class SmoothingSplineWithCov implements FitFunctionInterface, Serializable {

    // Class variables
    private static final long serialVersionUID = -1409687298849270059L;
    private static SmoothingSplineWithCov instance = null;

    private SmoothingSplineWithCov() {
        super();
    }

    /**
     *
     * @return
     */
    public static SmoothingSplineWithCov getInstance() {
        if (instance == null) {
            instance = new SmoothingSplineWithCov();
        }
        return instance;
    }

    /**
     *
     * @param dataActiveMap
     * @param t
     * @param yValues
     * @param Ss
     * @param stdErrOfYValues
     * @return
     */
    @Override
    public AbstractFunctionOfX getFunctionOfX(boolean[] dataActiveMap, double[] t, double[] yValues, Matrix Ss, boolean calculateHatMatrix) {

        // jan 2013 new assumption = data arrives already prepared
        // determine count of datapoints to be used
        int countOfActiveData = dataActiveMap.length;

        Matrix W = Ss.inverse();

        double[][] a = new double[countOfActiveData][2];
        // y values
        double[] y = new double[countOfActiveData];
        double[][] b = new double[countOfActiveData][1];

        // assemble active data points
        for (int i = 0; i < dataActiveMap.length; i++) {
            a[i][0] = 1.0;
            a[i][1] = t[i];

            y[i] = yValues[i];
            b[i][0] = y[i];
        }

        // QR make bands per Noah
        // time deltas 
        double[] h = new double[countOfActiveData - 1];

        for (int i = 0; i < (countOfActiveData - 1); i++) {
            h[i] = t[i + 1] - t[i];
        }

        Matrix Q = new Matrix(countOfActiveData, countOfActiveData - 1);
        Matrix R = new Matrix(countOfActiveData - 1, countOfActiveData - 1);

        for (int j = 2; j < countOfActiveData - 1; j++) {
            int i = j - 1;

            Q.set(i - 1, i, 1.0 / h[i - 1]);
            Q.set(i, i, -(1.0 / h[i - 1] + 1.0 / h[i]));
            Q.set(i + 1, i, 1.0 / h[i]);

            R.set(i, i, (h[i - 1] + h[i]) / 3.0);
            R.set(i, i + 1, h[i] / 6.0);
            R.set(i + 1, i, h[i] / 6.0);
        }

        int i = countOfActiveData - 2;
        Q.set(i - 1, i, 1.0 / h[i - 1]);
        Q.set(i, i, -(1.0 / h[i - 1] + 1.0 / h[i]));
        Q.set(i + 1, i, 1.0 / h[i]);

        R.set(i, i, (h[i - 1] + h[i]) / 3.0);

        Q = Q.getMatrix(0, Q.getRowDimension() - 1, 1, Q.getColumnDimension() - 1);
        R = R.getMatrix(1, R.getRowDimension() - 1, 1, R.getColumnDimension() - 1);

        Matrix K = Q.times(R.solve(Q.transpose()));

        int iExp = 5;  //%this gets incremented by 1 at the start of the loop
        int iMax = 5;  //%loop through this many values at a given Exp

        // rootbracket v3 per Noah
        long starttime = System.currentTimeMillis();
        Matrix g = null;
        double alpha = 0.0;
        boolean signChange = false;
        double redChiSqOld = 0.0;
        double redChiSqNew = 0.0;
        Matrix ImAwAlpha;
        double effectiveDegreesOfFreedom = 1.0;
        i = 1;

        int loopCounter = 0;
        int smoothingSplineLoopLimit = 100;

        Matrix Y = new Matrix(y.length, 1);
        for (int k = 0; k < y.length; k++) {
            Y.set(k, 0, y[k]);
        }

        while ((!signChange) && (loopCounter < smoothingSplineLoopLimit)) {
            loopCounter++;
            iExp++;
            i = 1;
            alpha = 1.0;
            g = (W.plus(K.times(alpha))).solve(W.times(Y));

            Matrix aQtSsQinv = Q.transpose().times(Ss).times(Q).times(alpha);
            ImAwAlpha = //
                    Ss.times(Q.times(//
                                    (R.plus(aQtSsQinv)).inverse().times(Q.transpose()))//
                            .times(alpha));
            effectiveDegreesOfFreedom = ImAwAlpha.trace();

            redChiSqNew = Y.minus(g).transpose().times(W).times(Y.minus(g)).get(0, 0);
            redChiSqNew = redChiSqNew / effectiveDegreesOfFreedom - 1.0;

            if (Math.signum(redChiSqNew) < 1) {
                while ((!signChange) && (i <= iMax)) {
                    redChiSqOld = redChiSqNew;
                    i = i + 1;
                    alpha = Math.pow(i, iExp);

                    g = (W.plus(K.times(alpha))).solve(W.times(Y));

                    aQtSsQinv = Q.transpose().times(Ss).times(Q).times(alpha);
                    ImAwAlpha = //
                            Ss.times(Q.times(//
                                            (R.plus(aQtSsQinv)).inverse().times(Q.transpose()))//
                                    .times(alpha));
                    effectiveDegreesOfFreedom = ImAwAlpha.trace();

                    redChiSqNew = Y.minus(g).transpose().times(W).times(Y.minus(g)).get(0, 0);
                    redChiSqNew = redChiSqNew / effectiveDegreesOfFreedom - 1.0;

                    signChange = (Math.signum(redChiSqNew) > -1);
                }
            } else {
                iExp = iExp - 2;
            }
        }

        AbstractFunctionOfX myFofX = null;

        if (loopCounter >= smoothingSplineLoopLimit) {
            System.out.println("SMOOTHING SPLINE ROOTBRACKET BLOWUP at " + smoothingSplineLoopLimit + "iterations with iExp = " + iExp);

            myFofX = LevenbergMarquardGeneralSolverWithCovS.getInstance()//         LevenbergMarquardAlgorithmWithCovS.getInstance()//
                    .getFunctionOfX(dataActiveMap, t, yValues, Ss, calculateHatMatrix);
        } else {

            double alphaLow = Math.pow(i - 1, iExp);
            double redChiSqLow = redChiSqOld;
            double alphaHigh = Math.pow(i, iExp);
            double redChiSqHigh = redChiSqNew;

            // end rootbracket
            //B. use Ridders's method to converge on the bracketed root in < linear time
            iMax = 500;
            double adiffLimit = 0.01;

            double alphaBest = //
                    riddersSolveV2( //
                            alphaLow, //
                            alphaHigh, //
                            redChiSqLow, //
                            redChiSqHigh, //
                            iMax, //
                            adiffLimit, //
                            W, Y, K, Ss, Q, R);

            long endtime = System.currentTimeMillis();
            System.out.println("ROOT BRACKET plus RIDDERS TIME = " + (endtime - starttime));

            // initialize function of x for exponential
            myFofX = new FofX_SmoothingSpline();
            ArrayList<Double> tList = new ArrayList<Double>();
            for (i = 0; i < t.length; i++) {
                tList.add(t[i]);
            }

            ((FofX_SmoothingSpline) myFofX).setT(tList);
            ((FofX_SmoothingSpline) myFofX).setH(h);
            ((FofX_SmoothingSpline) myFofX).setSs(Ss);
            ((FofX_SmoothingSpline) myFofX).setW(W);
            ((FofX_SmoothingSpline) myFofX).setY(Y);
            ((FofX_SmoothingSpline) myFofX).setK(K);
            ((FofX_SmoothingSpline) myFofX).setQ(Q);
            ((FofX_SmoothingSpline) myFofX).setR(R);

            // call last so that g and gamma can be computed
            ((FofX_SmoothingSpline) myFofX).setA(alphaBest);

            // q r ss
            Matrix aQtSsQinv = Q.transpose().times(Ss).times(Q).times(alphaBest);
            Matrix bracket = (R.plus(aQtSsQinv)).solve(Q.transpose());
            ImAwAlpha = //
                    (Ss.times(Q).times(//
                            bracket))//
                    .times(alphaBest);
            effectiveDegreesOfFreedom = ImAwAlpha.trace();

            g = (W.plus(K.times(alphaBest))).solve(W.times(Y));
            double redChiSqBest = Y.minus(g).transpose().times(W).times(Y.minus(g)).get(0, 0);

            if (calculateHatMatrix) {
                Matrix hatMatrix = //
                        Matrix.identity(countOfActiveData, countOfActiveData).minus(ImAwAlpha);
                myFofX.setHatMatrix(hatMatrix);
            }

            myFofX.setChiSquared(redChiSqBest);
            ((FofX_SmoothingSpline) myFofX).setEffectiveDegreesOfFreedom(effectiveDegreesOfFreedom);
            myFofX.setMSWD(redChiSqBest / effectiveDegreesOfFreedom);
            myFofX.setBIC(redChiSqBest + ((double) countOfActiveData - effectiveDegreesOfFreedom) * Math.log(countOfActiveData));

            //FAKE FAKE  square the entry so that it serves as the variance
            myFofX.setFitParameterCovarianceMatrix(new Matrix( //
                    new double[][]{{Math.pow(myFofX.getA() / 4, 2)}}));

            myFofX.setLogRoughness(Math.log(g.transpose().times(K.times(g)).get(0, 0) / countOfActiveData));

            System.out.println("Spline Solve: a = " + myFofX.getA() + " chiSqr = " + redChiSqBest);
        }

        myFofX.setCountOfActiveData(countOfActiveData);

        return myFofX;
    }

    private double riddersSolveV2(
            double myAlphaLow,
            double myAlphaHigh,
            double myRedChiSqLow,
            double myRredChiSqHigh,
            int iMax,
            double adiffLimit,
            Matrix W,
            Matrix Y,
            Matrix K,
            Matrix Ss,
            Matrix Q,
            Matrix R) {

        double alphaLow = myAlphaLow;
        double alphaHigh = myAlphaHigh;
        double redChiSqLow = myRedChiSqLow;
        double redChiSqHigh = myRredChiSqHigh;

        double alphaBest = 0.0;
        Matrix g = null;

        for (int i = 1; i <= iMax; i++) {

            double alphaMid = (alphaLow + alphaHigh) / 2.0;
            g = (W.plus(K.times(alphaMid))).solve(W.times(Y));

            Matrix aQtSsQinv = Q.transpose().times(Ss).times(Q).times(alphaMid);
            Matrix ImAwAlpha = //
                    Ss.times(Q.times(//
                                    (R.plus(aQtSsQinv)).inverse().times(Q.transpose()))//
                            .times(alphaMid));
            double effectiveDegreesOfFreedom = ImAwAlpha.trace();

            double redChiSqMid = Y.minus(g).transpose().times(W).times(Y.minus(g)).get(0, 0);
            redChiSqMid = redChiSqMid / effectiveDegreesOfFreedom - 1.0;

            double s = Math.sqrt(redChiSqMid * redChiSqMid - redChiSqLow * redChiSqHigh);
            if (s == 0) {
                alphaBest = alphaMid;
            } else {
                // calculate new best guess
                alphaBest = alphaMid + (alphaMid - alphaLow) * Math.signum(redChiSqLow - redChiSqHigh) * redChiSqMid / s;
                g = (W.plus(K.times(alphaBest))).solve(W.times(Y));

                aQtSsQinv = Q.transpose().times(Ss).times(Q).times(alphaBest);
                ImAwAlpha = //
                        Ss.times(Q.times(//
                                        (R.plus(aQtSsQinv)).inverse().times(Q.transpose()))//
                                .times(alphaBest));
                effectiveDegreesOfFreedom = ImAwAlpha.trace();

                double redChiSqBest = Y.minus(g).transpose().times(W).times(Y.minus(g)).get(0, 0);
                redChiSqBest = redChiSqBest / effectiveDegreesOfFreedom - 1.0;

                double adiffPCT = Math.abs(alphaBest - alphaMid) / alphaMid * 100;
                if (adiffPCT >= adiffLimit) {

                    if (Math.signum(redChiSqMid) != Math.signum(redChiSqBest)) {
                        alphaLow = alphaMid;
                        redChiSqLow = redChiSqMid;
                        alphaHigh = alphaBest;
                        redChiSqHigh = redChiSqBest;
                    } else if (Math.signum(redChiSqLow) != Math.signum(redChiSqBest)) {
                        alphaHigh = alphaBest;
                        redChiSqHigh = redChiSqBest;
                    } else if (Math.signum(redChiSqHigh) != Math.signum(redChiSqBest)) {
                        alphaLow = alphaBest;
                        redChiSqLow = redChiSqBest;
                    } else {
                        //TODO: better hope we don't get here
                    }
                }
                // System.out.println( "OUT");
            }
        }

        return alphaBest;
    }

    /**
     *
     * @return
     */
    public AbstractFunctionOfX getInstanceOfFitFunction() {
        return new FofX_SmoothingSpline();
    }

    /**
     *
     */
    public class FofX_SmoothingSpline extends AbstractFunctionOfX implements Serializable {

        // Class variables
        private static final long serialVersionUID = 3372829203553142590L;
        private ArrayList<Double> t;
        private double[] g;
        private double[] gamma;
        private double[] h;
        private Matrix Ss;
        private Matrix W;
        private Matrix K;
        private Matrix Y;
        private Matrix Q;
        private Matrix R;
        private double effectiveDegreesOfFreedom;

        /**
         *
         */
        public FofX_SmoothingSpline() {
            super();

            this.description = "smoothing spline";
            this.shortName = FitFunctionTypeEnum.SMOOTHING_SPLINE;
            this.countOfParameters = 0;
        }

        @Override
        public double f(double tInt) {
            // bracket x in time
            int index = calculateTimeLeftBracketIndex(tInt, t);

            return calculateInterpolatedSplineValue(tInt, index, g, gamma);
        }

        private double calculateInterpolatedSplineValue(double tInt, int index, double[] g, double[] gamma) {
            double splineFitYValue = //
                    ((tInt - t.get(index)) * g[index + 1]//
                    + (t.get(index + 1) - tInt) * g[index]) / h[index]//
                    - (tInt - t.get(index)) * (t.get(index + 1) - tInt) / 6.0//
                    * ((1.0 + (tInt - t.get(index)) / h[index]) * gamma[index + 1] //
                    + (1.0 + (t.get(index + 1) - tInt) / h[index]) * gamma[index]);

            return splineFitYValue;
        }

        /**
         *
         * @param Jf
         * @param timesForMatrix
         * @return
         */
        @Override
        public double[] calculateInterpolatedVariances(Matrix Jf, double[] timesForMatrix) {
            // timesForMatrix not used in this fitfunction
            matrixSf = Jf.times(hatMatrix).times(Ss).times(hatMatrix.transpose()).times(Jf.transpose());

            double[] stdVariances = new double[matrixSf.getRowDimension()];
            for (int i = 0; i < stdVariances.length; i++) {
                stdVariances[i] = matrixSf.get(i, i);
            }

            return stdVariances;
        }

        // used to change alpha via slider in fitfunction view
        @Override
        public void setA(double alpha) {
            a = alpha;
            Matrix G = (W.plus(K.times(alpha))).solve(W.times(Y));
            g = G.getColumnPackedCopy();

            Matrix Gamma = R.solve(Q.transpose().times(G));
            gamma = new double[Gamma.getRowDimension() + 2];
            // this pads gamma front and back with a zero each
            for (int i = 0; i < Gamma.getRowDimension(); i++) {
                gamma[i + 1] = Gamma.get(i, 0);
            }
        }

        @Override
        public void copyValuesFrom(AbstractFunctionOfX copyFrom) {
            super.copyValuesFrom(copyFrom);

            if (copyFrom instanceof FofX_SmoothingSpline) {
                setT(((FofX_SmoothingSpline) copyFrom).getT());
                setG(((FofX_SmoothingSpline) copyFrom).getG());
                setGamma(((FofX_SmoothingSpline) copyFrom).getGamma());
                setH(((FofX_SmoothingSpline) copyFrom).getH());
                setEffectiveDegreesOfFreedom(((FofX_SmoothingSpline) copyFrom).getEffectiveDegreesOfFreedom());

                setSs(((FofX_SmoothingSpline) copyFrom).getSs());
                setW(((FofX_SmoothingSpline) copyFrom).getW());
                setK(((FofX_SmoothingSpline) copyFrom).getK());
                setY(((FofX_SmoothingSpline) copyFrom).getY());
                setQ(((FofX_SmoothingSpline) copyFrom).getQ());
                setR(((FofX_SmoothingSpline) copyFrom).getR());
            } else {
                // line
                setA(copyFrom.getA());
                setB(copyFrom.getB());
            }

        }

        // used to set a only without re-calculations
        /**
         *
         * @param alpha
         */
        public void setAlpha(double alpha) {
            a = alpha;
        }

        /**
         * @return the t
         */
        public ArrayList<Double> getT() {
            return t;
        }

        /**
         * @param t the t to set
         */
        public void setT(ArrayList<Double> t) {
            this.t = t;
        }

        /**
         * @return the g
         */
        public double[] getG() {
            return g;
        }

        /**
         * @param g the g to set
         */
        public void setG(double[] g) {
            this.g = g;
        }

        /**
         * @return the gamma
         */
        public double[] getGamma() {
            return gamma;
        }

        /**
         * @param gamma the gamma to set
         */
        public void setGamma(double[] gamma) {
            this.gamma = gamma;
        }

        /**
         * @return the h
         */
        public double[] getH() {
            return h;
        }

        /**
         * @param h the h to set
         */
        public void setH(double[] h) {
            this.h = h;
        }

        /**
         *
         * @param W
         */
        public void setW(Matrix W) {
            this.W = W;
        }

        /**
         *
         * @param K
         */
        public void setK(Matrix K) {
            this.K = K;
        }

        /**
         *
         * @param Y
         */
        public void setY(Matrix Y) {
            this.Y = Y;
        }

        /**
         *
         * @param Q
         */
        public void setQ(Matrix Q) {
            this.Q = Q;
        }

        /**
         *
         * @param R
         */
        public void setR(Matrix R) {
            this.R = R;
        }

        @Override
        public double getYIntercept() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        /**
         *
         * @return
         */
        @Override
        public double getYInterceptStdErr() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public double getYInterceptVariance() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        /**
         *
         * @return
         */
        @Override
        public double getStdErrOfA() {
            return Math.sqrt(fitParameterCovarianceMatrix.get(0, 0));
        }

        /**
         *
         * @return
         */
        @Override
        public double getStdErrOfB() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        /**
         *
         * @return
         */
        @Override
        public double getStdErrOfC() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        /**
         *
         * @return
         */
        @Override
        public double getVarOfA() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        /**
         *
         * @return
         */
        @Override
        public double getVarOfB() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        /**
         *
         * @return
         */
        @Override
        public double getVarOfC() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        /**
         *
         * @param countOfActiveData
         * @param dataActiveMap
         * @param neededValues
         * @return
         */
        @Override
        public Matrix makeMatrixJ21(int countOfActiveData, boolean[] dataActiveMap, double[] neededValues) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        /**
         *
         * @param countOfActiveData
         * @param dataActiveMap
         * @param neededValues
         * @return
         */
        @Override
        public Matrix makeMatrixJ22(int countOfActiveData, boolean[] dataActiveMap, double[] neededValues) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        /**
         *
         * @return
         */
        @Override
        public boolean fitFound() {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        /**
         *
         * @param effectiveDegreesOfFreedom
         */
        public void setEffectiveDegreesOfFreedom(double effectiveDegreesOfFreedom) {
            this.effectiveDegreesOfFreedom = effectiveDegreesOfFreedom;
        }

        /**
         * @return the effectiveDegreesOfFreedom
         */
        public double getEffectiveDegreesOfFreedom() {
            return effectiveDegreesOfFreedom;
        }

        /**
         * @return the W
         */
        public Matrix getW() {
            return W;
        }

        /**
         * @return the K
         */
        public Matrix getK() {
            return K;
        }

        /**
         * @return the Y
         */
        public Matrix getY() {
            return Y;
        }

        /**
         * @return the Q
         */
        public Matrix getQ() {
            return Q;
        }

        /**
         * @return the R
         */
        public Matrix getR() {
            return R;
        }

        /**
         * @return the Ss
         */
        public Matrix getSs() {
            return Ss;
        }

        /**
         * @param Ss the Ss to set
         */
        public void setSs(Matrix Ss) {
            this.Ss = Ss;
        }

        /**
         *
         * @param Jpy
         * @return
         */
        @Override
        protected Matrix extractMatrixJIntpFromJpy(Matrix Jpy) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
