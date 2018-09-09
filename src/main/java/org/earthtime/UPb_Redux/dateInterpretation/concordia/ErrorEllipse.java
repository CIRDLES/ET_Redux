/*
 * ErrorEllipse.java
 *
 * Created on March 21, 2008, 3:32 PM
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

import Jama.CholeskyDecomposition;
import Jama.Matrix;
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public class ErrorEllipse {

    private static final Matrix cntrlPointsMatrix;

    static {

        double k = 4.0 / 3.0 * (Math.sqrt( 2.0 ) - 1.0);
        double[][] cntrlPointsArray = new double[][]{
            {1.0, 0.0},
            {1.0, k},
            {k, 1.0},
            {0.0, 1.0},
            { - k, 1.0},
            { - 1.0, k},
            { - 1.0, 0.0},
            { - 1.0,  - k},
            { - k,  - 1.0},
            {0.0,  - 1.0},
            {k,  - 1.0},
            {1.0,  - k},
            {1.0, 0.0}
        };

        cntrlPointsMatrix = new Matrix( cntrlPointsArray );

    }
    // instance variables
    private ValueModel x_Value;
    private ValueModel y_Value;
    private ValueModel rho;
    private double aspectRatio;
    private double ellipseSize;
    private Matrix ellipseControlPoints;

    /**
     * 
     * @param X_value
     * @param Y_value
     * @param rho
     * @param aspectRatio
     * @param ellipseSize
     */
    public ErrorEllipse (
            ValueModel X_value,
            ValueModel Y_value,
            ValueModel rho,
            double aspectRatio,
            double ellipseSize ) {

        x_Value = X_value;
        y_Value = Y_value ;
        setRho( rho );
        setAspectRatio( aspectRatio );
        setEllipseSize( ellipseSize );

        CalculateErrorEllipseIII();
    }

    private void CalculateErrorEllipseIII () {
        double xOneSigmaAbs = getX_Value().getOneSigmaAbs().doubleValue();
        double yOneSigmaAbs = getY_Value().getOneSigmaAbs().doubleValue();
        double covarianceX_Y = //
                rho.getValue().doubleValue()//
                * xOneSigmaAbs//
                * yOneSigmaAbs;

        // ref http://math.nist.gov/javanumerics/jama/
        double[][] covMatRaw = new double[2][2];
        covMatRaw[0][0] = Math.pow( xOneSigmaAbs, 2 );
        covMatRaw[0][1] = covarianceX_Y;
        covMatRaw[1][0] = covarianceX_Y;
        covMatRaw[1][1] = Math.pow( yOneSigmaAbs, 2 );

        Matrix covMat = new Matrix( covMatRaw );

        CholeskyDecomposition cd = covMat.chol();//   new CholeskyDecomposition(covMat);
        Matrix R = cd.getL().transpose();

        Matrix scaledControlPointsMatrix = cntrlPointsMatrix.times( ellipseSize );
        ellipseControlPoints = scaledControlPointsMatrix.times( R );

        double[][] xy = new double[13][2];
        double x = x_Value.getValue().doubleValue();
        double y = y_Value.getValue().doubleValue();

        for (int i = 0; i < 13; i ++) {
            xy[i][0] = x;
            xy[i][1] = y;
        }

        ellipseControlPoints.plusEquals( new Matrix( xy ) );

        // ellipseControlPoints.print( 10, 10 );

    }

    /**
     * 
     * @return
     */
    public double getbezierMinX () {
        //return ellipseControlPoints.get(6, 0);
        return getExtreme( -1, 0 );
    }

    /**
     * 
     * @return
     */
    public double getbezierMaxX () {
        //return ellipseControlPoints.get(1, 0);
        return getExtreme( 1, 0 );
    }

    /**
     * 
     * @return
     */
    public double getbezierMinY () {
        //return ellipseControlPoints.get(6, 1);
        return getExtreme( -1, 1 );
    }

    /**
     * 
     * @return
     */
    public double getbezierMaxY () {
        //return ellipseControlPoints.get(1, 1);
        return getExtreme( 1, 1 );
    }

    private double getExtreme ( int maxORmin, int xORy ) {
        // maxORmin = -1 for min, 1 for max
        // xORy = 0 for x, 1 for y
        double retval;

        if ( maxORmin == -1 ) {
            retval = 1e10;
            for (int i = 0; i < ellipseControlPoints.getRowDimension(); i ++) {
                if ( ellipseControlPoints.get( i, xORy ) < retval ) {
                    retval = ellipseControlPoints.get( i, xORy );
                }
            }
        } else {
            retval = 0.0;
            for (int i = 0; i < ellipseControlPoints.getRowDimension(); i ++) {
                if ( ellipseControlPoints.get( i, xORy ) > retval ) {
                    retval = ellipseControlPoints.get( i, xORy );
                }
            }
        }
        return retval;
    }

    /**
     * 
     * @return
     */
    public ValueModel getX_Value () {
        return x_Value;
    }

    /**
     * 
     * @param x_Value
     */
    public void setX_Value ( ValueModel x_Value ) {
        this.x_Value = x_Value;
    }

    /**
     * 
     * @return
     */
    public ValueModel getY_Value () {
        return y_Value;
    }

    /**
     * 
     * @param y_Value
     */
    public void setY_Value ( ValueModel y_Value ) {
        this.y_Value = y_Value;
    }

    /**
     * 
     * @return
     */
    public ValueModel getRho () {
        return rho;
    }

    /**
     * 
     * @param rho
     */
    public void setRho ( ValueModel rho ) {
        this.rho = rho;
    }

    /**
     * 
     * @return
     */
    public double getAspectRatio () {
        return aspectRatio;
    }

    /**
     * 
     * @param aspectRatio
     */
    public void setAspectRatio ( double aspectRatio ) {
        this.aspectRatio = aspectRatio;
    }

    /**
     * 
     * @return
     */
    public double getEllipseSize () {
        return ellipseSize;
    }

    /**
     * 
     * @param ellipseSize
     */
    public void setEllipseSize ( double ellipseSize ) {
        this.ellipseSize = ellipseSize;
    }

    /**
     * @return the ellipseControlPoints
     */
    public Matrix getEllipseControlPoints () {
        return ellipseControlPoints;
    }

    /**
     * @param ellipseControlPoints the ellipseControlPoints to set
     */
    public void setEllipseControlPoints ( Matrix ellipseControlPoints ) {
        this.ellipseControlPoints = ellipseControlPoints;
    }
}
