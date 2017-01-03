/*
 * ReinschWeightedSmoothingSpline.java
 *
 * Created Aug 21, 2011
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
package org.earthtime.Tripoli.fitFunctions.algorithms;

/**
 *
 * @author James F. Bowring
 */
public class ReinschWeightedSmoothingSpline {

    private static double[][] Qb = null;
    private static double Q11 = 0.0;
    private static double[][] Rb = null;
    private static double[] wInvTimesAlpha = null;
    private static double[] wInv = null;
    private static double[] y = null;
    private static double[] QTy = null;
    private static double[] gamma = null;
    private static double[] g = null;
    private static double redChiSq = 0.0;

    /**
     * 
     * @param alpha
     * @return
     */
    public static double[] reinschWeightedV4 (
            double alpha ) {

        wInvTimesAlpha = new double[wInv.length];
        for (int i = 0; i < wInv.length; i ++) {
            wInvTimesAlpha[i] = wInv[i] * alpha;
        }

        // 2a. Find the non-zero diagonals of R + alpha*transpose(Q)*inverse(W)*Q
        double[][] Rp = new double[QTy.length][3];
        int i = 0;
        //               Q11^2        * winv(i) +            Qb(i,1)^2              *winv(i+1) +                       Qb(i,2)^2             *winv(i+2) + Rb(i,1);
        Rp[i][0] = Math.pow( Q11, 2 ) * wInvTimesAlpha[i] + Math.pow( Qb[i][0], 2 ) * wInvTimesAlpha[i + 1] + Math.pow( Qb[i][1], 2 ) * wInvTimesAlpha[i + 2] + Rb[i][0];
        //         Qb(i,1)    *Qb(i,2)     *winv(i+1) +           Qb(i,2)    *Qb(i+1,1)     *winv(i+2)               + Rb(i,2);
        Rp[i][1] = Qb[i][0] * Qb[i][1] * wInvTimesAlpha[i + 1] + Qb[i][1] * Qb[i + 1][0] * wInvTimesAlpha[i + 2] + Rb[i][1];
        //         Qb(i,2)  *  Qb(i+1,2)     *winv(i+2);
        Rp[i][2] = Qb[i][1] * Qb[i + 1][1] * wInvTimesAlpha[i + 2];

        for (i = 1; i < (Rp.length - 2); i ++) {
            //                Qb(i-1,2)^2            *winv(i) +                   Qb(i,1)^2          *winv(i+1)              + Qb(i,2)^2             *winv(i+2) + Rb(i,1);
            Rp[i][0] = Math.pow( Qb[i - 1][1], 2 ) * wInvTimesAlpha[i] + Math.pow( Qb[i][0], 2 ) * wInvTimesAlpha[i + 1] + Math.pow( Qb[i][1], 2 ) * wInvTimesAlpha[i + 2] + Rb[i][0];
            //         Qb(i,1)    *Qb(i,2)   *winv(i+1) +             Qb(i,2)   *Qb(i+1,1)      *winv(i+2) +             Rb(i,2);
            Rp[i][1] = Qb[i][0] * Qb[i][1] * wInvTimesAlpha[i + 1] + Qb[i][1] * Qb[i + 1][0] * wInvTimesAlpha[i + 2] + Rb[i][1];
            //         Qb(i,2)   *Qb(i+1,2)      *winv(i+2);
            Rp[i][2] = Qb[i][1] * Qb[i + 1][1] * wInvTimesAlpha[i + 2];

        }

        i = Rp.length - 2;
        //                Qb(i-1,2)^2          *winv(i)       +               Qb(i,1)^2          *winv(i+1)                  + Qb(i,2)^2               *winv(i+2) + Rb(i,1);
        Rp[i][0] = Math.pow( Qb[i - 1][1], 2 ) * wInvTimesAlpha[i] + Math.pow( Qb[i][0], 2 ) * wInvTimesAlpha[i + 1] + Math.pow( Qb[i][1], 2 ) * wInvTimesAlpha[i + 2] + Rb[i][0];
        //         Qb(i,1)  *Qb(i,2)          *winv(i+1) +           Qb(i,2)    *Qb(i+1,1)    *winv(i+2)           + Rb(i,2);
        Rp[i][1] = Qb[i][0] * Qb[i][1] * wInvTimesAlpha[i + 1] + Qb[i][1] * Qb[i + 1][0] * wInvTimesAlpha[i + 2] + Rb[i][1];

        i = Rp.length - 1;
        //                    Qb(i-1,2)^2           *winv(i) +              Qb(i,1)^2             *winv(i+1) +                     Qb(i,2)^2         *winv(i+2) + Rb(i,1);
        Rp[i][0] = Math.pow( Qb[i - 1][1], 2 ) * wInvTimesAlpha[i] + Math.pow( Qb[i][0], 2 ) * wInvTimesAlpha[i + 1] + Math.pow( Qb[i][1], 2 ) * wInvTimesAlpha[i + 2] + Rb[i][0];


        // 2b. Find Cholesky decomposition of band matrix Rp = L*D*transpose(L)

        // 2.6.1 from Green and Silverman: Gaussian Elimination A

        double[] D = new double[Rp.length];
        double[][] L = new double[Rp.length - 1][2];

        i = 0;
        D[i] = Rp[i][i];
        L[i][0] = Rp[0][1] / D[0];

        i = 1;
        D[i] = Rp[1][0] - Math.pow( L[0][0], 2 ) * D[0];

        for (i = 2; i < Rp.length; i ++) {
            //L(i-2,2) =     Rp(i-2,3) / D(i-2);
            L[i - 2][1] = Rp[i - 2][2] / D[i - 2];
            //L(i-1,1) =  (Rp(i-1,2)    - L(i-2,1)   *L(i-2,2)       *D(i-2))    / D(i-1);
            L[i - 1][0] = (Rp[i - 1][1] - L[i - 2][0] * L[i - 2][1] * D[i - 2]) / D[i - 1];
            // D(i) = Rp(i,1) - (      L(i-1,1)  )^2         *D(i-1) -  (L(i-2,2)^2)             *D(i-2);
            D[i] = Rp[i][0] - Math.pow( L[i - 1][0], 2 ) * D[i - 1] - Math.pow( L[i - 2][1], 2 ) * D[i - 2];
        }


        //2.6.2 from Green and Silveman: Gaussian Elimination B
        double[] u = new double[Rp.length];
        u[0] = QTy[0];
        u[1] = QTy[1] - L[0][0] * u[0];

        for (i = 2; i < Rp.length; i ++) {
            // u(i) = QTy(i) - L(i-1,1)*u(i-1) -     L(i-2,2)      *u(i-2);
            u[i] = QTy[i] - L[i - 1][0] * u[i - 1] - L[i - 2][1] * u[i - 2];
        }

        double[] v = new double[Rp.length];
        for (i = 0; i < v.length; i ++) {
            v[i] = u[i] / D[i];
        }

        int m = Rp.length - 1;

        double[] gmma = new double[Rp.length];
        gmma[m] = v[m];
        gmma[m - 1] = v[m - 1] - L[m - 1][0] * gmma[m];

        for (i = (m - 2); i >= 0; i --) {
            //gmma(i) = v(i) - L(i,1)*gmma(i+1) -     L(i,2)   *gmma(i+2);
            gmma[i] = v[i] - L[i][0] * gmma[i + 1] - L[i][1] * gmma[i + 2];
        }

        /*2c. Back-substitution to solve system
        
        
        
         */
        g = new double[y.length];
        // g(1) = y(1) - winv(1)*gmma(1)*Q11;
        g[0] = y[0] - wInvTimesAlpha[0] * gmma[0] * Q11;
        // g(2) = y(2) - winv(2)*         (gmma(1)    *Qb(1,1)   + gmma(2)  *Qb(1,2));
        g[1] = y[1] - wInvTimesAlpha[1] * (gmma[0] * Qb[0][0] + gmma[1] * Qb[0][1]);

        for (i = 2; i < Rp.length; i ++) {
            //     y(i) - winv(i)           *(gmma(i-2)    *Qb(i-2,2)    + gmma(i-1)    *Qb(i-1,1) +    gmma(i)* Qb(i-1,2));
            g[i] = y[i] - wInvTimesAlpha[i] * (gmma[i - 2] * Qb[i - 2][1] + gmma[i - 1] * Qb[i - 1][0] + gmma[i] * Qb[i - 1][1]);
        }

        // g(n-1) =             y(n-1)    - winv(n-1)                    *  (gmma(m-1)    *Qb(m-1,2)       + gmma(m)*  Qb(m,1));
        g[y.length - 2] = y[y.length - 2] - wInvTimesAlpha[y.length - 2] * (gmma[m - 1] * Qb[m - 1][1] + gmma[m] * Qb[m][0]);
        // g(n) =         y(n)            - winv(n)            *gmma(m)    *Qb(m,2);
        g[y.length - 1] = y[y.length - 1] - wInvTimesAlpha[y.length - 1] * gmma[m] * Qb[m][1];


        // buffer gamma with zeroes for plotting
        gamma = new double[gmma.length + 2];
        gamma[0] = 0.0;
        for (i = 0; i < gmma.length; i ++) {
            gamma[i + 1] = gmma[i];
        }
        gamma[gamma.length - 1] = 0.0;


        return g;
    }

    /**
     * 
     * @return
     */
    public static double calculateRedChiSq () {
        redChiSq = 0.0;
        for (int i = 0; i < y.length; i ++) {
            redChiSq += (Math.pow( (y[i] - g[i]), 2 ) / wInv[i]);
        }

        redChiSq = (redChiSq / (double) (y.length - 1.0)) - 1.0;

        return redChiSq;
    }

    /**
     * @param aQb the Qb to set
     */
    public static void setQb ( double[][] aQb ) {
        Qb = aQb;
    }

    /**
     * @param aQ11 the Q11 to set
     */
    public static void setQ11 ( double aQ11 ) {
        Q11 = aQ11;
    }

    /**
     * @param aRb the Rb to set
     */
    public static void setRb ( double[][] aRb ) {
        Rb = aRb;
    }

    /**
     * @param awInv the wInv to set
     */
    public static void setwInv ( double[] awInv ) {
        wInv = awInv;
    }

    /**
     * @param aY the y to set
     */
    public static void setY ( double[] aY ) {
        y = aY;
    }

    /**
     * @param aQTy the QTy to set
     */
    public static void setQTy ( double[] aQTy ) {
        QTy = aQTy;
    }

    /**
     * @return the gamma
     */
    public static double[] getGamma () {
        return gamma;
    }

    /**
     * @return the g
     */
    public static double[] getG () {
        return g;
    }
}
