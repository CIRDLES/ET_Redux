/*
 * Aggregation of IUnctContrib-s per depth
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
package org.earthtime.UPb_Redux.fractions.UPbReduxFractions.fractionReduction;

import Jama.Matrix;
import java.util.*;
import org.earthtime.matrices.matrixModels.AbstractMatrixModel;
import org.earthtime.matrices.matrixModels.CovarianceMatrixModel;
import org.earthtime.matrices.matrixModels.JacobianMatrixModel;

/**
 *
 * @author jackom
 */
public class UncertaintyZoomLayer {

    private String dateName;
    private Vector<InputUnctContrib> zoomLayerContents;
    private double maxVariance;
    private double minNegCov;
    private double yRangePos;
    private double yRangeTotal; // includes negative covariance
    private double centralRatio;
    private double totalUncertaintyContribs;
    private AbstractMatrixModel covarianceMatrixModel;
    private AbstractMatrixModel sensitivityVectorModel;
    private transient AbstractMatrixModel zoomCovarianceModel;

    /**
     *
     * @param dateName
     * @param covarianceMatrixModel
     * @param sensitivityVectorModel
     */
    public UncertaintyZoomLayer (
            String dateName,
            AbstractMatrixModel covarianceMatrixModel,
            AbstractMatrixModel sensitivityVectorModel ) {

        this.dateName = dateName;
        this.covarianceMatrixModel = covarianceMatrixModel;
        this.sensitivityVectorModel = sensitivityVectorModel;

        this.zoomLayerContents = new Vector<InputUnctContrib>();
        this.maxVariance = 0.0;
        this.minNegCov = 0.0;
        this.yRangePos = 0.0;
        this.yRangeTotal = 0.0;
        this.centralRatio = 0.0;
        this.totalUncertaintyContribs = 0.0;

        PrepareZoomLayerII();
    }

    private void PrepareZoomLayerII () {

        // new approach July 2012
        // given a set of variable names, extract the Jacobian and covariance models from the given models

        // first, find the nonzero elements of the sensitivity vector
        ArrayList<String> zoomInputs = new ArrayList<String>();
        Map<Integer, String> zoomRows = new HashMap<Integer, String>();
        Iterator<Integer> rowKeyIterator = covarianceMatrixModel.getRows().keySet().iterator();
        while (rowKeyIterator.hasNext()) {
            Integer rowKey = rowKeyIterator.next();
            String colKey = covarianceMatrixModel.getRows().get( rowKey );
            if ( sensitivityVectorModel.getMatrix().get( 0, sensitivityVectorModel.getCols().get( colKey ) ) != 0.0 ) {
                zoomInputs.add( colKey );
            }
        }

        InputUnctContrib totalDateUncertaintyContribs = new InputUnctContrib( dateName );
        // this call also sets up the trimmed zoomCovarianceModel for subsequent gatherCovaryingInputs
        extractZoomModels( zoomInputs, totalDateUncertaintyContribs );
        totalUncertaintyContribs = totalDateUncertaintyContribs.getTrueUnct();

        // clumping into contributors
        ArrayList<String> workList = new ArrayList<String>();

        ArrayList<ArrayList<String>> clumps = new ArrayList<ArrayList<String>>();

        for (int i = 0; i < zoomInputs.size(); i ++) {

            ArrayList<String> clumpList = new ArrayList<String>();
            clumps.add( clumpList );

            gatherCovaryingInputs( workList, clumpList, zoomInputs.get( i ), zoomCovarianceModel );

            // remove empty clump
            if ( clumpList.size() == 0 ) {
                clumps.remove( clumpList );
            }
        }

        for (int i = 0; i < clumps.size(); i ++) {
            // add InputUnctContrib  named for first element in clump
            InputUnctContrib clumpLead = new InputUnctContrib( clumps.get( i ).get( 0 ) );
            
            int colLookup = sensitivityVectorModel.getCols().get( clumps.get( i ).get( 0 ) );
            clumpLead.setPartialDerivative( sensitivityVectorModel.getMatrix().get( 0, colLookup ) );

            for (int c = 1; c < clumps.get( i ).size(); c ++) {
                InputUnctContrib covariant = new InputUnctContrib( clumps.get( i ).get( c ) );               
                clumpLead.addCovariant( covariant);
                
                colLookup = sensitivityVectorModel.getCols().get( clumps.get( i ).get( c ) );
                covariant.setPartialDerivative( sensitivityVectorModel.getMatrix().get( 0, colLookup ) );
            }
            zoomLayerContents.add( clumpLead );
            extractZoomModels( clumps.get( i ), clumpLead );
        }

        Collections.sort( zoomLayerContents );
        DetermineExtremes();

    }

    private void extractZoomModels (//
            ArrayList<String> zoomInputs,
            InputUnctContrib inputUnctContrib ) {

        // instantiate new Jacobian and covariance and variance models
        AbstractMatrixModel zoomSensitivityModel = new JacobianMatrixModel( "zoom" );
        AbstractMatrixModel zoomVarianceModel = new CovarianceMatrixModel();
        zoomCovarianceModel = new CovarianceMatrixModel();

        // build row map for covariance
        Map<Integer, String> rowMap = new HashMap<Integer, String>();
        for (int i = 0; i < zoomInputs.size(); i ++) {
            rowMap.put( i, zoomInputs.get( i ) );
        }

        // initialize the models
        zoomSensitivityModel.setRows( new String[]{dateName} );
        zoomSensitivityModel.setCols( rowMap );
        zoomSensitivityModel.initializeMatrix();

        zoomVarianceModel.setRows( rowMap );
        zoomVarianceModel.setCols( rowMap );
        zoomVarianceModel.initializeMatrix();

        zoomCovarianceModel.setRows( rowMap );
        zoomCovarianceModel.setCols( rowMap );
        zoomCovarianceModel.initializeMatrix();

        // populate the matrices
        Iterator<Integer> rowMapIterator = rowMap.keySet().iterator();
        while (rowMapIterator.hasNext()) {
            int row = rowMapIterator.next();
            String rowName = rowMap.get( row );
            int rowLookup = sensitivityVectorModel.getCols().get( rowName );

            Iterator<Integer> rowMapIteratorForCols = rowMap.keySet().iterator();
            while (rowMapIteratorForCols.hasNext()) {
                int col = rowMapIteratorForCols.next();
                String colName = rowMap.get( col );
                int colLookup = sensitivityVectorModel.getCols().get( colName );

                if ( row == 0 ) {
                    zoomSensitivityModel.setValueAt( 0, col, sensitivityVectorModel.getMatrix().get( 0, colLookup ) );
                }

                zoomCovarianceModel.setValueAt( row, col, covarianceMatrixModel.getMatrix().get( rowLookup, colLookup ) );

                if ( row == col ) {
                    zoomVarianceModel.setValueAt( row, col, covarianceMatrixModel.getMatrix().get( colLookup, colLookup ) );
                }

            }
        }

        double trueUnct = 0;
        double unct = 0;
        try {
            trueUnct = zoomSensitivityModel.getMatrix().times( zoomCovarianceModel.getMatrix() ).times( zoomSensitivityModel.getMatrix().transpose() ).get( 0, 0 );
            unct = zoomSensitivityModel.getMatrix().times( zoomVarianceModel.getMatrix() ).times( zoomSensitivityModel.getMatrix().transpose() ).get( 0, 0 );
        } catch (Exception e) {
        }
        inputUnctContrib.setTrueUnct( trueUnct );
        inputUnctContrib.setCovariance( trueUnct - unct );
        inputUnctContrib.setUnct( unct );
    }

    private void gatherCovaryingInputs (//
            ArrayList<String> workList, //
            ArrayList<String> clumpList,//
            String inputName, //
            AbstractMatrixModel zoomCovarianceModel ) {

        if (  ! workList.contains( inputName ) ) {
            workList.add( inputName );
            clumpList.add( inputName );

            // walk the upper half of matrix recursively following covariance trail
            int row = zoomCovarianceModel.getCols().get( inputName );
            for (int i = row + 1; i < zoomCovarianceModel.getCols().size(); i ++) {
                if ( zoomCovarianceModel.getMatrix().get( row, i ) != 0.0 ) {
                    gatherCovaryingInputs( workList, clumpList, zoomCovarianceModel.getRows().get( i ), zoomCovarianceModel );
                }
            }

        }
    }

    /**
     *
     */
    public void DetermineExtremes () {

        maxVariance = 0.0;
        minNegCov = 0.0;
        totalUncertaintyContribs = 0.0;

        for (InputUnctContrib IUC : zoomLayerContents) {

            totalUncertaintyContribs += IUC.getTrueUnct();
            //determine maximum total uncertainty (uncertainy summed with any covarianceIndexes' uncertainties)

            if ( IUC.getTrueUnct() > maxVariance ) {
                maxVariance = IUC.getTrueUnct();
            }

            if ( IUC.getCovariance() < minNegCov ) {
                minNegCov = IUC.getCovariance();
            }

        }

        setYRangeExcl( maxVariance );
        setYRangeIncl( maxVariance + Math.abs( minNegCov ) );
        this.centralRatio = 1 - (Math.abs( minNegCov ) / getYRangeIncl());
    }

    /**
     *
     * @return
     */
    @Override
    public String toString () {
        String retVal = "\n     ";
        for (InputUnctContrib IUC : getZoomLayerContents()) {
            retVal += IUC.toString() + "\n\n     ";
        }
        return retVal;
    }

    /**
     *
     * @return
     */
    public double getMaxTrueCov () {
        return maxVariance;
    }

    /**
     *
     * @param d
     */
    public void setMaxTrueCov ( double d ) {
        this.maxVariance = d;
    }

    /**
     *
     * @return
     */
    public double getMinNegCov () {
        return minNegCov;
    }

    /**
     *
     * @param cov
     */
    public void setMinCov ( double cov ) {
        minNegCov = cov;
    }

    /**
     *
     * @return
     */
    public int getSize () {
        return getZoomLayerContents().size();
    }

    /**
     *
     * @return
     */
    public double getCentralRatio () {
        return centralRatio;
    }

    /**
     *
     * @param centralRatio
     */
    public void setCentralRatio ( double centralRatio ) {
        this.centralRatio = centralRatio;
    }

    /**
     *
     * @return
     */
    public Vector<InputUnctContrib> getZoomLayerContents () {
        return zoomLayerContents;
    }

    /**
     *
     * @return
     */
    public double getYRangeExcl () {
        return yRangePos;
    }

    /**
     *
     * @param yRangeExcl
     */
    public void setYRangeExcl ( double yRangeExcl ) {
        this.yRangePos = yRangeExcl;
    }

    /**
     *
     * @return
     */
    public double getYRangeIncl () {
        return yRangeTotal;
    }

    /**
     *
     * @param yRangeIncl
     */
    public void setYRangeIncl ( double yRangeIncl ) {
        this.yRangeTotal = yRangeIncl;
    }

    /**
     * @return the totalUncertaintyContribs
     */
    public double getTotalUncertaintyContribs () {
        return totalUncertaintyContribs;
    }

    /**
     * @param totalUncertaintyContribs the totalUncertaintyContribs to set
     */
    public void setTotalUncertaintyContribs ( double totalUncertaintyContribs ) {
        this.totalUncertaintyContribs = totalUncertaintyContribs;
    }
}
