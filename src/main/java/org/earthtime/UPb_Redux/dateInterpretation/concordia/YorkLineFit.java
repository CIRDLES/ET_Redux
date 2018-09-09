
/*
 * YorkLineFit.java
 *
 * Created on November 28, 2008
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
package org.earthtime.UPb_Redux.dateInterpretation.concordia;

/**
 *
 * @author James F. Bowring
 */
public class YorkLineFit {
    // fields

    private double slope;
    private double yIntercept;
    private double slopeVariance;
    private double yInterceptVariance;
    private double oneSigmaYIntercept;
    private double oneSigmaSlope;
    private double covYIntercept__slope;
    private double rhoYIntercept__slope;
    private double mswd;
    private double[] X;
    private double[] Y;
    private double[] sigmaX;
    private double[] sigmaY;
    private double[] rho;

    /**
     *
     */
    public YorkLineFit() {
    }

    /**
     *
     * @param X
     * @param Y
     * @param sigmaX
     * @param sigmaY
     * @param rho
     */
    public YorkLineFit(
            double[] X,
            double[] Y,
            double[] sigmaX,
            double[] sigmaY,
            double[] rho) {

        this.X = X;
        this.Y = Y;
        this.sigmaX = sigmaX;
        this.sigmaY = sigmaY;
        this.rho = rho;

        performYorkFit();
    }

    private void performYorkFit() {
        setSlope(0.0);
        setSlopeVariance(0.0);
        setYIntercept(0.0);
        setYInterceptVariance(0.0);

        setOneSigmaSlope(0.0);
        setOneSigmaYIntercept(0.0);

        setCovYIntercept__slope(0.0);
        setRhoYIntercept__slope(0.0);

        setMSWD(0.0);

        // York et al - Am. J Phys 72 (3) March 2004
        // setup working variables
        double slopeSaved = 0.0;

        double maximumX = 0.0;
        double maximumY = 0.0;
        // the isotopic ratios are usually < 1E2
        double minimumX = 1E10;
        double minimumY = 1E10;

        int pointCount = X.length;

        // weighted data
        double[] wX = new double[pointCount];
        double[] wY = new double[pointCount];
        double[] W = new double[pointCount];

        double[] alpha = new double[pointCount];
        double[] beta = new double[pointCount];

        double Xbar = 0.0;
        double Ybar = 0.0;
        double sumW = 0.0;

        double[] x = new double[pointCount];
        double[] y = new double[pointCount];
        double xBar = 0.0;
        double yBar = 0.0;
        double[] u = new double[pointCount];
        double[] v = new double[pointCount];
        double sumWx = 0.0;
        double sumWy = 0.0;
        double sumWuu = 0.0;

        double sumMSWDnumerator = 0.0;

        if (pointCount > 1) {
            // initialize arrays
            for (int i = 0; i < pointCount; i++) {

                maximumX = Math.max(maximumX, X[i]);
                maximumY = Math.max(maximumY, Y[i]);

                minimumX = Math.min(minimumX, X[i]);
                minimumY = Math.min(minimumY, Y[i]);

                wX[i] = 1.0 / sigmaX[i] / sigmaX[i];
                wY[i] = 1.0 / sigmaY[i] / sigmaY[i];

                alpha[i] = Math.sqrt(wX[i] * wY[i]);
            }

            // estimate a starting slope
            try {
                slope = (maximumY - minimumY) / (maximumX - minimumX);
            } catch (Exception e) {
            }

            // iteratively solve for slope
            while (Math.abs(slope - slopeSaved) > 1E-8) {
                slopeSaved = slope;

                double[] U = new double[pointCount];
                double[] V = new double[pointCount];
                double sumWX = 0.0;
                double sumWY = 0.0;
                double sumWbetaV = 0.0;
                double sumWbetaU = 0.0;
                sumW = 0.0;

                for (int i = 0; i < pointCount; i++) {
                    W[i] = (wX[i] * wY[i]) //
                            / (wX[i] + (slopeSaved * slopeSaved * wY[i]) - (2.0 * slopeSaved * rho[i] * alpha[i]));
                    sumWX += W[i] * X[i];
                    sumWY += W[i] * Y[i];
                    sumW += W[i];
                }

                try {
                    Xbar = sumWX / sumW;
                    Ybar = sumWY / sumW;
                } catch (Exception e) {
                }

                for (int i = 0; i < pointCount; i++) {
                    U[i] = X[i] - Xbar;
                    V[i] = Y[i] - Ybar;
                    try {
                        beta[i] = //
                                W[i] //
                                * (U[i] / wY[i] //
                                + slopeSaved * V[i] / wX[i] //
                                - (slopeSaved * U[i] + V[i]) * rho[i] / alpha[i]);
                    } catch (Exception e) {
                    }

                    sumWbetaV += W[i] * beta[i] * V[i];
                    sumWbetaU += W[i] * beta[i] * U[i];
                }

                try {
                    slope = sumWbetaV / sumWbetaU;
                } catch (Exception e) {
                }
            }// end while iteration

            for (int i = 0; i < pointCount; i++) {
                x[i] = Xbar + beta[i];
                y[i] = Ybar + slope * beta[i];

                sumWx += (W[i] * x[i]);
                sumWy += (W[i] * y[i]);
            }

            xBar = sumWx / sumW;
            yBar = sumWy / sumW;

            setYIntercept(Ybar - slope * Xbar);

            for (int i = 0; i < pointCount; i++) {
                u[i] += x[i] - xBar;
                v[i] += y[i] - yBar;

                sumWuu += W[i] * u[i] * u[i];

                sumMSWDnumerator += W[i] * (Y[i] - slope * X[i] - yIntercept) * (Y[i] - slope * X[i] - yIntercept);
            }

            setSlopeVariance(1.0 / sumWuu);
            setYInterceptVariance(1.0 / sumW + xBar * xBar * slopeVariance);
            setOneSigmaYIntercept(Math.sqrt(yInterceptVariance));
            setOneSigmaSlope(Math.sqrt(slopeVariance));
            setCovYIntercept__slope(-1.0 * xBar * oneSigmaSlope * oneSigmaSlope);
            setRhoYIntercept__slope(-1.0 * xBar * oneSigmaSlope / oneSigmaYIntercept);

            if (pointCount > 2) {
                setMSWD(sumMSWDnumerator / (double) (pointCount - 2));
            }

        }

    }

    /**
     *
     * @return
     */
    public double getSlope() {
        return slope;
    }

    /**
     *
     * @param slope
     */
    public void setSlope(double slope) {
        this.slope = slope;
    }

    /**
     *
     * @return
     */
    public double getYIntercept() {
        return yIntercept;
    }

    /**
     *
     * @param yIntercept
     */
    public void setYIntercept(double yIntercept) {
        this.yIntercept = yIntercept;
    }

    /**
     *
     * @return
     */
    public double getSlopeVariance() {
        return slopeVariance;
    }

    /**
     *
     * @param slopeVariance
     */
    public void setSlopeVariance(double slopeVariance) {
        this.slopeVariance = slopeVariance;
    }

    /**
     *
     * @return
     */
    public double getYInterceptVariance() {
        return yInterceptVariance;
    }

    /**
     *
     * @param yInterceptVariance
     */
    public void setYInterceptVariance(double yInterceptVariance) {
        this.yInterceptVariance = yInterceptVariance;
    }

    /**
     *
     * @return
     */
    public double getOneSigmaYIntercept() {
        return oneSigmaYIntercept;
    }

    /**
     *
     * @param oneSigmaYIntercept
     */
    public void setOneSigmaYIntercept(double oneSigmaYIntercept) {
        this.oneSigmaYIntercept = oneSigmaYIntercept;
    }

    /**
     *
     * @return
     */
    public double getOneSigmaSlope() {
        return oneSigmaSlope;
    }

    /**
     *
     * @param oneSigmaSlope
     */
    public void setOneSigmaSlope(double oneSigmaSlope) {
        this.oneSigmaSlope = oneSigmaSlope;
    }

    /**
     *
     * @return
     */
    public double getCovYIntercept__slope() {
        return covYIntercept__slope;
    }

    /**
     *
     * @param covYIntercept__slope
     */
    public void setCovYIntercept__slope(double covYIntercept__slope) {
        this.covYIntercept__slope = covYIntercept__slope;
    }

    /**
     *
     * @return
     */
    public double getRhoYIntercept__slope() {
        return rhoYIntercept__slope;
    }

    /**
     *
     * @param rhoYIntercept__slope
     */
    public void setRhoYIntercept__slope(double rhoYIntercept__slope) {
        this.rhoYIntercept__slope = rhoYIntercept__slope;
    }

    /**
     *
     * @return
     */
    public double getMSWD() {
        return mswd;
    }

    /**
     *
     * @param mswd
     */
    public void setMSWD(double mswd) {
        this.mswd = mswd;
    }

    /**
     *
     * @return
     */
    public double[] getX() {
        return X;
    }

    /**
     *
     * @param X
     */
    public void setX(double[] X) {
        this.X = X;
    }

    /**
     *
     * @return
     */
    public double[] getY() {
        return Y;
    }

    /**
     *
     * @param Y
     */
    public void setY(double[] Y) {
        this.Y = Y;
    }

    /**
     *
     * @return
     */
    public double[] getSigmaX() {
        return sigmaX;
    }

    /**
     *
     * @param sigmaX
     */
    public void setSigmaX(double[] sigmaX) {
        this.sigmaX = sigmaX;
    }

    /**
     *
     * @return
     */
    public double[] getSigmaY() {
        return sigmaY;
    }

    /**
     *
     * @param sigmaY
     */
    public void setSigmaY(double[] sigmaY) {
        this.sigmaY = sigmaY;
    }

    /**
     *
     * @return
     */
    public double[] getRho() {
        return rho;
    }

    /**
     *
     * @param rho
     */
    public void setRho(double[] rho) {
        this.rho = rho;
    }
}
