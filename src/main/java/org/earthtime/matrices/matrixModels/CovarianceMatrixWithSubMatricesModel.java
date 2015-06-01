/*
 * CovarianceMatrixWithSubMatricesModel.java
 *
 * Created on December 11, 2008
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
package org.earthtime.matrices.matrixModels;

import Jama.Matrix;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author James F. Bowring
 */
public class CovarianceMatrixWithSubMatricesModel extends CovarianceMatrixModel {

    private AbstractMatrixModel lambdasCovarianceMatrix;
    private AbstractMatrixModel tracerCovarianceMatrix;
    private AbstractMatrixModel analyticalCovarianceMatrix;
    private boolean subMatricesExist;

    /**
     *
     */
    public CovarianceMatrixWithSubMatricesModel () {
        super();
        lambdasCovarianceMatrix = null;
        tracerCovarianceMatrix = null;
        analyticalCovarianceMatrix = null;
        subMatricesExist = false;
    }

    /**
     *
     * @return
     */
    @Override
    public AbstractMatrixModel copy () {
        AbstractMatrixModel retval = new CovarianceMatrixWithSubMatricesModel();

        retval.setRows( rows );
        retval.copyCols( cols );
        retval.setMatrix( matrix.copy() );
        ((CovarianceMatrixWithSubMatricesModel) retval).setLambdasCovarianceMatrix( lambdasCovarianceMatrix );
        ((CovarianceMatrixWithSubMatricesModel) retval).setTracerCovarianceMatrix( tracerCovarianceMatrix );
        ((CovarianceMatrixWithSubMatricesModel) retval).setAnalyticalCovarianceMatrix( analyticalCovarianceMatrix );
        ((CovarianceMatrixWithSubMatricesModel) retval).setSubMatricesExist( subMatricesExist );

        return retval;
    }

    /**
     *
     * @param variances
     * @return
     */
    @Override
    public boolean initializeMatrixModelWithVariances (
            Map<String, BigDecimal> variances ) {

        setSubMatricesExist( (lambdasCovarianceMatrix != null) && (tracerCovarianceMatrix != null) && (analyticalCovarianceMatrix != null) );

        boolean retVal =  ! (getRows().isEmpty());
        if ( retVal ) {
            initializeMatrix();
            // initialize submatrices if they exist

            if ( isSubMatricesExist() ) {
                lambdasCovarianceMatrix.initializeMatrix();
                tracerCovarianceMatrix.initializeMatrix();
                analyticalCovarianceMatrix.initializeMatrix();
            }

            // walk the rows of covariance matrix and for each row get the name
            // if that name is in the variances (for example, inputVariances)
            // then populate the diagonal at that row with the value
            Iterator<Integer> rowKeys = getRows().keySet().iterator();
            while (rowKeys.hasNext()) {
                Integer rowKey = rowKeys.next();
                int row = (int) rowKey;
                String rowName = getRows().get( rowKey );

                BigDecimal varianceModel = variances.get( rowName );
                if ( varianceModel != null ) {
                    getMatrix().set( row, row, varianceModel.doubleValue() );

                    // initialize submatrices if they exist
                    if ( isSubMatricesExist() ) {
                        if ( lambdasCovarianceMatrix.getCols().containsKey( rowName ) ) {
                            int rowL = (int) lambdasCovarianceMatrix.getCols().get( rowName );
                            lambdasCovarianceMatrix.getMatrix().set( rowL, rowL, varianceModel.doubleValue() );
                        } else if ( tracerCovarianceMatrix.getCols().containsKey( rowName ) ) {
                            int rowT = (int) tracerCovarianceMatrix.getCols().get( rowName );
                            tracerCovarianceMatrix.getMatrix().set( rowT, rowT, varianceModel.doubleValue() );
                        } else {
                            int rowA = (int) analyticalCovarianceMatrix.getCols().get( rowName );
                            analyticalCovarianceMatrix.getMatrix().set( rowA, rowA, varianceModel.doubleValue() );
                        }

                    }
                }
            }
        }

        return retVal;
    }

    /**
     *
     * @param covarianceName
     * @param coVariance
     */
    protected void setCovarianceCells ( String covarianceName, double coVariance ) {

        // name is of form covXXX__YYY
        String both = covarianceName.substring( 3 );
        String[] each = both.split( "__" );
        // left side needs lowercase
        String leftSide = each[0].substring( 0, 1 ).toLowerCase() + each[0].substring( 1 );

        setCovarianceCell( leftSide, each[1], coVariance );

//        // TODO: FIX THIS JUNE 2012 populate the submatrices NOTE lambda submatrix has no off diagonals
//        if ( isSubMatricesExist() ) {
//            if (  ! ((CovarianceMatrixModel) tracerCovarianceMatrix).setCovarianceCell( leftSide, each[1], coVariance ) ) {
//                ((CovarianceMatrixModel) analyticalCovarianceMatrix).setCovarianceCell( leftSide, each[1], coVariance );
//            }
//        }

        // July 2012 refactor to include lambdas too (waiting on Noah for correlations)
        if ( tracerCovarianceMatrix.getCols().keySet().contains( leftSide ) ) {
            ((CovarianceMatrixModel) tracerCovarianceMatrix).setCovarianceCell( leftSide, each[1], coVariance );
        } else if ( lambdasCovarianceMatrix.getCols().keySet().contains( leftSide ) ) {
            ((CovarianceMatrixModel) lambdasCovarianceMatrix).setCovarianceCell( leftSide, each[1], coVariance );
        } else {
            ((CovarianceMatrixModel) analyticalCovarianceMatrix).setCovarianceCell( leftSide, each[1], coVariance );
        }

//        System.out.println(tracerCovarianceMatrix.ToStringWithLabels());
    }

    /**
     * Recalculates covariances due to moving a slider Returns Matrix with
     * diagonal = oneSigmaAbs, for use when lockingVariances
     *
     * @param covaryingTerms
     * @param oneSigma
     * @param lockVariancesTogether
     * @return
     */
    @Override
    public Matrix recalculateSubCovariances (
            String[] covaryingTerms, BigDecimal oneSigma, boolean lockVariancesTogether ) {

        // array is at least length 1 for the uncertainty changed by slider
        // walk the array and extract mini matrices from the covariance matrix
        Matrix dOldInvert = new Matrix( covaryingTerms.length, covaryingTerms.length );
        Matrix dNew = new Matrix( covaryingTerms.length, covaryingTerms.length );
        Matrix sOldCov = new Matrix( covaryingTerms.length, covaryingTerms.length );

        double sigmaConverter = 1.0;
        // depends on covaryingTerms having the subject as first element
        for (int i = 0; i < covaryingTerms.length; i ++) {
            int colA = getCols().get( covaryingTerms[i] );

            // set dOldInvert diagonal to inverse of one-sigmas
            if ( getMatrix().get( colA, colA ) == 0.0 ) {
                dOldInvert.set( i, i, 0.0 );
            } else {
                dOldInvert.set( i, i, 1.0 / Math.sqrt( getMatrix().get( colA, colA ) ) );
            }

            // set dNew diagonal to one-sigmas
            if ( i == 0 ) {
                sigmaConverter = oneSigma.doubleValue() / Math.sqrt( getMatrix().get( colA, colA ) );
            }
            if ( lockVariancesTogether ) {
                dNew.set( i, i, //
                        sigmaConverter * Math.sqrt( getMatrix().get( colA, colA ) ) );
            } else {
                if ( i == 0 ) {
                    dNew.set( 0, 0, oneSigma.doubleValue() );
                } else {
                    dNew.set( i, i, Math.sqrt( getMatrix().get( colA, colA ) ) );
                }
            }

            // extract a mini-covariance matrix of affected terms
            sOldCov.set( i, i, getMatrix().get( colA, colA ) );
            for (int j = i + 1; j < covaryingTerms.length; j ++) {
                int colB = getCols().get( covaryingTerms[j] );
                sOldCov.set( i, j, getMatrix().get( colA, colB ) );
                sOldCov.set( j, i, getMatrix().get( colB, colA ) );
            }
        }

        // calculate new mini covariance matrix
        Matrix R =//
                dOldInvert.times(//
                sOldCov ).times(//
                dOldInvert );

        Matrix sNewCov =//
                dNew.times( R ).times( dNew );

        // push new values back into all covariance matrices
        for (int i = 0; i < covaryingTerms.length; i ++) {
            int colA = getCols().get( covaryingTerms[i] );
            for (int j = 0; j < covaryingTerms.length; j ++) {
                int colB = getCols().get( covaryingTerms[j] );
                getMatrix().set( colA, colB, sNewCov.get( i, j ) );
            }
        }

        // additonal work here
        recalculateSubCovariancesSubMatrices( covaryingTerms, sNewCov );

        return dNew;

    }

    private void recalculateSubCovariancesSubMatrices ( String[] covaryingTerms, Matrix sNewCov ) {
        if ( isSubMatricesExist() ) {
            // only analytical and tracer as lambda has no sliders
            for (int i = 0; i < covaryingTerms.length; i ++) {
                if ( analyticalCovarianceMatrix.getCols().containsKey( covaryingTerms[i] ) ) {
                    int colA = analyticalCovarianceMatrix.getCols().get( covaryingTerms[i] );
                    for (int j = 0; j < covaryingTerms.length; j ++) {
                        int colB = analyticalCovarianceMatrix.getCols().get( covaryingTerms[j] );
                        analyticalCovarianceMatrix.getMatrix().set( colA, colB, sNewCov.get( i, j ) );
                    }
                }
            }
            for (int i = 0; i < covaryingTerms.length; i ++) {
                if ( tracerCovarianceMatrix.getCols().containsKey( covaryingTerms[i] ) ) {
                    int colA = tracerCovarianceMatrix.getCols().get( covaryingTerms[i] );
                    for (int j = 0; j < covaryingTerms.length; j ++) {
                        int colB = tracerCovarianceMatrix.getCols().get( covaryingTerms[j] );
                        tracerCovarianceMatrix.getMatrix().set( colA, colB, sNewCov.get( i, j ) );
                    }
                }
            }
        }


    }


    /**
     * @return the lambdasCovarianceMatrix
     */
    public AbstractMatrixModel getLambdasCovarianceMatrix () {
        return lambdasCovarianceMatrix;
    }

    /**
     * @param lambdasCovarianceMatrix the lambdasCovarianceMatrix to set
     */
    public void setLambdasCovarianceMatrix ( AbstractMatrixModel lambdasCovarianceMatrix ) {
        this.lambdasCovarianceMatrix = lambdasCovarianceMatrix;
    }

    /**
     * @return the tracerCovarianceMatrix
     */
    public AbstractMatrixModel getTracerCovarianceMatrix () {
        return tracerCovarianceMatrix;
    }

    /**
     * @param tracerCovarianceMatrix the tracerCovarianceMatrix to set
     */
    public void setTracerCovarianceMatrix ( AbstractMatrixModel tracerCovarianceMatrix ) {
        this.tracerCovarianceMatrix = tracerCovarianceMatrix;
    }

    /**
     * @return the analyticalCovarianceMatrix
     */
    public AbstractMatrixModel getAnalyticalCovarianceMatrix () {
        return analyticalCovarianceMatrix;
    }

    /**
     * @param analyticalCovarianceMatrix the analyticalCovarianceMatrix to set
     */
    public void setAnalyticalCovarianceMatrix ( AbstractMatrixModel analyticalCovarianceMatrix ) {
        this.analyticalCovarianceMatrix = analyticalCovarianceMatrix;
    }

    /**
     * @return the subMatricesExist
     */
    public boolean isSubMatricesExist () {
        return subMatricesExist;
    }

    /**
     * @param subMatricesExist the subMatricesExist to set
     */
    public void setSubMatricesExist ( boolean subMatricesExist ) {
        this.subMatricesExist = subMatricesExist;
    }
}
