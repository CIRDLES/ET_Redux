/*
 * UncertaintyPropagator.java
 *
 * Created on January 2, 2009
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
package org.earthtime.UPb_Redux.fractions.UPbReduxFractions.fractionReduction;

import Jama.Matrix;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.fractions.FractionI;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFraction;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFractionI;
import org.earthtime.UPb_Redux.utilities.BrowserControl;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.dataDictionaries.DataDictionary;
import org.earthtime.dataDictionaries.Lambdas;
import org.earthtime.dataDictionaries.MatrixSpecifications;
import org.earthtime.dataDictionaries.TracerUPbRatiosAndConcentrations;
import org.earthtime.matrices.matrixModels.AbstractMatrixModel;
import org.earthtime.matrices.matrixModels.CovarianceMatrixModel;
import org.earthtime.matrices.matrixModels.CovarianceMatrixWithSubMatricesModel;
import org.earthtime.matrices.matrixModels.JacobianMatrixModel;

/**
 * ReductionHandler is the workhorse of the matrix-based ratio, date and
 * uncertainty calculations. An instance of this class is an attribute of a
 * UPbFracton.
 *
 * @author James F. Bowring
 */
public class ReductionHandler {

    private FractionI fraction;
    /**
     * matrixSpecs contain the string rowForSpecificDate names for each of the
     * matrices for a given tracer-based reduction. These specs are defined in
     * dataDictionary.java. It is assumed that matrices will be numbered 0 ... n
     * and that the covariance matrix will be defined as matrix 0. These names
     * correspond exactly to the input and output names used to store fraction
     * data.
     */
    private String matrixSpecsName;
    private String[][] matrixSpecs;
    private String[][][] clumpSpecs;
    private String[][][] covaryingTerms;
    private AbstractMatrixModel[] matrixModels;
    /**
     * Contains data reduction variances and covariances for all ratios.
     * Calculated using chaining Mat-N-1 *** Mat-0 *** Mat-N-1-transpose.
     */
    private AbstractMatrixModel ratioCovMatModel;
    private AbstractMatrixModel ratioSensitivityVectors;
    private AbstractMatrixModel analyticalRatioSensitivityVectors;
    private AbstractMatrixModel tracerRatioSensitivityVectors;
    private AbstractMatrixModel lambdaRatioSensitivityVectors;
    private AbstractMatrixModel analyticalMiniRatioCovMatModel;
    private AbstractMatrixModel tracerMiniRatioCovMatModel;
    private AbstractMatrixModel lambdaMiniRatioCovMatModel;
    /**
     * Contains data reduction variances and covariances for all inputs.
     * Equivalent to Mat-N * ratioCovMatModel * Mat-N-transpose.
     */
    private AbstractMatrixModel dateCovMatModel;
    private AbstractMatrixModel dateSensitivityVectors;
    private AbstractMatrixModel analyticalDateSensitivityVectors;
    private AbstractMatrixModel tracerDateSensitivityVectors;
    private AbstractMatrixModel lambdaDateSensitivityVectors;
    private AbstractMatrixModel analyticalMiniDateCovMatModel;
    private AbstractMatrixModel tracerMiniDateCovMatModel;
    private AbstractMatrixModel lambdaMiniDateCovMatModel;
    private String measuredRatioUncertaintiesValidity;

    /**
     *
     * @param fraction
     * @param matrixSpecsName
     */
    public ReductionHandler (
            FractionI fraction,
            String matrixSpecsName ) {

        this.fraction = fraction;

        //this.matrixSpecs = matrixSpecs;
        // June 2010 switch to reflection so name of matrixspecs is preserved here for axis selection
        this.matrixSpecsName = matrixSpecsName;
        this.matrixSpecs = MatrixSpecifications.getMatrixSpecsByName( matrixSpecsName );

        matrixModels = new AbstractMatrixModel[matrixSpecs.length];

        // setup covariance matrixModels
        matrixModels[0] = new CovarianceMatrixWithSubMatricesModel();
        matrixModels[0].setRows( matrixSpecs[0] );
        matrixModels[0].setCols( matrixModels[0].getRows() );

        // extract column names in order from specs for mini covariances
        Map<Integer, String> lambdaNamesList = new HashMap<>();
        Map<Integer, String> tracerNamesList = new HashMap<>();
        Map<Integer, String> analyticalNamesList = new HashMap<>();

        for (int i = 0; i < matrixSpecs[0].length; i ++) {
            if ( Lambdas.contains( matrixSpecs[0][i] ) ) {//     lambdaSourceNames.contains( matrixSpecs[0][i] ) ) {
                lambdaNamesList.put( lambdaNamesList.size(), matrixSpecs[0][i] );
            } else if ( TracerUPbRatiosAndConcentrations.contains( matrixSpecs[0][i] ) ) {//         tracerSourceNames.contains( matrixSpecs[0][i] ) ) {
                tracerNamesList.put( tracerNamesList.size(), matrixSpecs[0][i] );
            } else { // if not in lambdas or tracerSourceNames goes here
                analyticalNamesList.put( analyticalNamesList.size(), matrixSpecs[0][i] );
            }
        }

        // build mini-matrices for covariance matrix
        ((CovarianceMatrixWithSubMatricesModel) matrixModels[0]).setLambdasCovarianceMatrix( new CovarianceMatrixModel() );
        ((CovarianceMatrixWithSubMatricesModel) matrixModels[0]).getLambdasCovarianceMatrix().setRows( lambdaNamesList );
        ((CovarianceMatrixWithSubMatricesModel) matrixModels[0]).getLambdasCovarianceMatrix().setCols( lambdaNamesList );

        ((CovarianceMatrixWithSubMatricesModel) matrixModels[0]).setTracerCovarianceMatrix( new CovarianceMatrixModel() );
        ((CovarianceMatrixWithSubMatricesModel) matrixModels[0]).getTracerCovarianceMatrix().setRows( tracerNamesList );
        ((CovarianceMatrixWithSubMatricesModel) matrixModels[0]).getTracerCovarianceMatrix().setCols( tracerNamesList );

        ((CovarianceMatrixWithSubMatricesModel) matrixModels[0]).setAnalyticalCovarianceMatrix( new CovarianceMatrixModel() );
        ((CovarianceMatrixWithSubMatricesModel) matrixModels[0]).getAnalyticalCovarianceMatrix().setRows( analyticalNamesList );
        ((CovarianceMatrixWithSubMatricesModel) matrixModels[0]).getAnalyticalCovarianceMatrix().setCols( analyticalNamesList );

        // build remaining Jacobian matrices
        for (int i = 1; i < matrixSpecs.length; i ++) {
            matrixModels[i] = new JacobianMatrixModel( Integer.toString( i ) );
            matrixModels[i].setRows( matrixSpecs[i] );
            matrixModels[i].setCols( matrixModels[i - 1].getRows() );
        }

        buildClumpSpecsForKwiki();
    }

    /**
     *
     * @param inputVariances
     * @param coVariances
     */
    public void populateCovarianceMatrices (
            Map<String, BigDecimal> inputVariances,
            Map<String, BigDecimal> coVariances ) {

        ((CovarianceMatrixModel) matrixModels[0]).initializeMatrixModelWithVariances( inputVariances );
        ((CovarianceMatrixModel) matrixModels[0]).initializeCoVariances( coVariances );

        // March 21 2009: check quality of input
        setMeasuredRatioUncertaintiesValidity( ((CovarianceMatrixModel) matrixModels[0]).//
                checkValidityOfMeasuredRatioUncertainties( fraction.getFractionID() ) );

        populateKwikiClumpCovariances();
    }

    /**
     *
     * @param parDerivTerms
     */
    protected void populateJacobianMatrices (
            Map<String, BigDecimal> parDerivTerms ) {

        for (int i = 1; i < getMatrixSpecs().length; i ++) {
            ((JacobianMatrixModel) matrixModels[i]).initializeMatrixModelWithDerivedTerms( parDerivTerms );
        }
    }

    /**
     *
     * @param calculateSensitivityMat
     */
    public void calculateDateCovMatModel ( boolean calculateSensitivityMat ) {

        // new sept 2009 produce covariance matrix for n-1 to store ratio covariances
        Matrix ratioCovMatrix = calcRatioCovMatrix( calculateSensitivityMat );

        // convert to model form
        ratioCovMatModel = new CovarianceMatrixModel();
        ratioCovMatModel.setLevelName( "-1" );
        ratioCovMatModel.setRows( getMatrixSpecs()[matrixModels.length - 2] );
        ratioCovMatModel.setCols( ratioCovMatModel.getRows() );
        ratioCovMatModel.setMatrix( ratioCovMatrix );

        calculateMiniRatioCovMatModels();

        // new sept 2009, use matrix math from n-1 to calculate n covariance matrix
        Matrix dateCovMatrix = //
                matrixModels[matrixModels.length - 1].getMatrix().//
                times( ratioCovMatrix ).//
                times( matrixModels[matrixModels.length - 1].getMatrix().transpose() );

        // convert to model form
        dateCovMatModel = new CovarianceMatrixModel();
        dateCovMatModel.setLevelName( "-1" );
        dateCovMatModel.setRows( getMatrixSpecs()[matrixModels.length - 1] );
        dateCovMatModel.setCols( dateCovMatModel.getRows() );
        dateCovMatModel.setMatrix( dateCovMatrix );

        calculateMiniDateCovMatModels();

    }

    private Matrix calcRatioCovMatrix ( boolean calculateSensitivityMat ) {

        if ( calculateSensitivityMat ) {
            calculateSensitivityVectors();
        }

        Matrix ratioSensitivityMatrix = ratioSensitivityVectors.getMatrix();

        Matrix ratioCovMatrix = ratioSensitivityMatrix.//
                times( matrixModels[0].getMatrix() ).//
                times( ratioSensitivityMatrix.transpose() );

        return ratioCovMatrix;
    }

    private void calculateMiniRatioCovMatModels () {

        Matrix analyticalMini = //
                analyticalRatioSensitivityVectors.getMatrix().//
                times( ((CovarianceMatrixWithSubMatricesModel) matrixModels[0]).getAnalyticalCovarianceMatrix().getMatrix() ).//
                times( analyticalRatioSensitivityVectors.getMatrix().transpose() );

        analyticalMiniRatioCovMatModel = new CovarianceMatrixModel();
        analyticalMiniRatioCovMatModel.setLevelName( "-2" );
        analyticalMiniRatioCovMatModel.setRows( getMatrixSpecs()[matrixModels.length - 2] );
        analyticalMiniRatioCovMatModel.setCols( ratioCovMatModel.getRows() );
        analyticalMiniRatioCovMatModel.setMatrix( analyticalMini );

        Matrix tracerMini = //
                tracerRatioSensitivityVectors.getMatrix().//
                times( ((CovarianceMatrixWithSubMatricesModel) matrixModels[0]).getTracerCovarianceMatrix().getMatrix() ).//
                times( tracerRatioSensitivityVectors.getMatrix().transpose() );

        tracerMiniRatioCovMatModel = new CovarianceMatrixModel();
        tracerMiniRatioCovMatModel.setLevelName( "-2" );
        tracerMiniRatioCovMatModel.setRows( getMatrixSpecs()[matrixModels.length - 2] );
        tracerMiniRatioCovMatModel.setCols( ratioCovMatModel.getRows() );
        tracerMiniRatioCovMatModel.setMatrix( tracerMini );

        Matrix lambdaMini = //
                lambdaRatioSensitivityVectors.getMatrix().//
                times( ((CovarianceMatrixWithSubMatricesModel) matrixModels[0]).getLambdasCovarianceMatrix().getMatrix() ).//
                times( lambdaRatioSensitivityVectors.getMatrix().transpose() );
        lambdaMiniRatioCovMatModel = new CovarianceMatrixModel();
        lambdaMiniRatioCovMatModel.setLevelName( "-2" );
        lambdaMiniRatioCovMatModel.setRows( getMatrixSpecs()[matrixModels.length - 2] );
        lambdaMiniRatioCovMatModel.setCols( ratioCovMatModel.getRows() );
        lambdaMiniRatioCovMatModel.setMatrix( lambdaMini );

        // set the one-sigmas for each ratio in fraction
        // walk the rows of each mini-matrix
        Iterator<Integer> rowKeys = analyticalMiniRatioCovMatModel.getRows().keySet().iterator();
        while (rowKeys.hasNext()) {
            Integer rowKey = (Integer) rowKeys.next();
            int row = rowKey;
            String rowName = analyticalMiniRatioCovMatModel.getRows().get( rowKey );

            double analyticalUnct = analyticalMiniRatioCovMatModel.getMatrix().get( row, row );
            double tracerUnct = tracerMiniRatioCovMatModel.getMatrix().get( row, row );
            double lambdaUnct = lambdaMiniRatioCovMatModel.getMatrix().get( row, row );

            try {
                // only concerned with setting uncertainties for ratios, which begin with letter "r"
                if ( rowName.startsWith( "r" ) ) {
                    fraction.getRadiogenicIsotopeRatioByName( rowName ).//
                            setOneSigma( new BigDecimal( Double.toString( Math.sqrt( analyticalUnct ) ) ) );

                    ((UPbFraction) fraction).getRadiogenicIsotopeRatioWithTracerUnctByName( rowName ).//
                            setValue( fraction.getRadiogenicIsotopeRatioByName( rowName ).getValue() );
                    ((UPbFraction) fraction).getRadiogenicIsotopeRatioWithTracerUnctByName( rowName ).//
                            setOneSigma( new BigDecimal( Double.toString( Math.sqrt( analyticalUnct + tracerUnct ) ) ) );


                    ((UPbFraction) fraction).getRadiogenicIsotopeRatioWithAllUncertaintyByName( rowName ).//
                            setValue( fraction.getRadiogenicIsotopeRatioByName( rowName ).getValue() );
                    ((UPbFraction) fraction).getRadiogenicIsotopeRatioWithAllUncertaintyByName( rowName ).//
                            setOneSigma( new BigDecimal( Double.toString( Math.sqrt( analyticalUnct + tracerUnct + lambdaUnct ) ) ) );
                }
            } catch (Exception e) {
            }

        }

        calculateCorrelationCoefficients();
    }

    private void calculateMiniDateCovMatModels () {

        Matrix analyticalMini = //
                analyticalDateSensitivityVectors.getMatrix().//
                times( ((CovarianceMatrixWithSubMatricesModel) matrixModels[0]).getAnalyticalCovarianceMatrix().getMatrix() ).//
                times( analyticalDateSensitivityVectors.getMatrix().transpose() );

        analyticalMiniDateCovMatModel = new CovarianceMatrixModel();
        analyticalMiniDateCovMatModel.setLevelName( "-2" );
        analyticalMiniDateCovMatModel.setRows( getMatrixSpecs()[matrixModels.length - 1] );
        analyticalMiniDateCovMatModel.setCols( dateCovMatModel.getRows() );
        analyticalMiniDateCovMatModel.setMatrix( analyticalMini );

        Matrix tracerMini = //
                tracerDateSensitivityVectors.getMatrix().//
                times( ((CovarianceMatrixWithSubMatricesModel) matrixModels[0]).getTracerCovarianceMatrix().getMatrix() ).//
                times( tracerDateSensitivityVectors.getMatrix().transpose() );

        tracerMiniDateCovMatModel = new CovarianceMatrixModel();
        tracerMiniDateCovMatModel.setLevelName( "-2" );
        tracerMiniDateCovMatModel.setRows( getMatrixSpecs()[matrixModels.length - 1] );
        tracerMiniDateCovMatModel.setCols( dateCovMatModel.getRows() );
        tracerMiniDateCovMatModel.setMatrix( tracerMini );

        Matrix lambdaMini = //
                lambdaDateSensitivityVectors.getMatrix().//
                times( ((CovarianceMatrixWithSubMatricesModel) matrixModels[0]).getLambdasCovarianceMatrix().getMatrix() ).//
                times( lambdaDateSensitivityVectors.getMatrix().transpose() );
        lambdaMiniDateCovMatModel = new CovarianceMatrixModel();
        lambdaMiniDateCovMatModel.setLevelName( "-2" );
        lambdaMiniDateCovMatModel.setRows( getMatrixSpecs()[matrixModels.length - 1] );
        lambdaMiniDateCovMatModel.setCols( dateCovMatModel.getRows() );
        lambdaMiniDateCovMatModel.setMatrix( lambdaMini );

        // set the date one-sigmas for each mode in fraction
        // walk the rows of the mini-matrix
        Iterator<Integer> rowKeys = analyticalMiniDateCovMatModel.getRows().keySet().iterator();
        while (rowKeys.hasNext()) {
            Integer rowKey = rowKeys.next();
            int row = rowKey;
            String rowName = analyticalMiniDateCovMatModel.getRows().get( rowKey );

            double analyticalUnct = analyticalMiniDateCovMatModel.getMatrix().get( row, row );
            double tracerUnct = tracerMiniDateCovMatModel.getMatrix().get( row, row );
            double lambdaUnct = lambdaMiniDateCovMatModel.getMatrix().get( row, row );

            try {
                // only concerned with dates here
                if ( rowName.startsWith( "age" ) ) {
                    fraction.getRadiogenicIsotopeDateByName( rowName ).//
                            setOneSigma( new BigDecimal( Double.toString( Math.sqrt( analyticalUnct ) ) ) );

                    ((UPbFractionI) fraction).getRadiogenicIsotopeDateWithTracerUnctByName( rowName ).//
                            setValue( fraction.getRadiogenicIsotopeDateByName( rowName ).getValue() );
                    ((UPbFractionI) fraction).getRadiogenicIsotopeDateWithTracerUnctByName( rowName ).//
                            setOneSigma( new BigDecimal( Double.toString( Math.sqrt( analyticalUnct + tracerUnct ) ) ) );

                    ((UPbFractionI) fraction).getRadiogenicIsotopeDateWithAllUnctByName( rowName ).//
                            setValue( fraction.getRadiogenicIsotopeDateByName( rowName ).getValue() );
                    ((UPbFractionI) fraction).getRadiogenicIsotopeDateWithAllUnctByName( rowName ).//
                            setOneSigma( new BigDecimal( Double.toString( Math.sqrt( analyticalUnct + tracerUnct + lambdaUnct ) ) ) );
                }
            } catch (Exception e) {
            }
        }

    }

    /**
     *
     */
    public void calculateSensitivityVectors () {
        // sept 2009
        // for the new P = ratioCovMatr, we first calculate
        // n-1 sensitivity matrix note here it is n sensitivity matrix now
        // thereby splitting math so that final sensitivity matrix is say n times n-1 sens matrix
        // then do this math to get "P" n-1 times 0 times n-1 transpose
        // P will have the ratios we need for Th Pa corrections

        // make copies of matrix n and matrix n-1 to use in calculating sensitivity matrixModels
        Matrix sensMatN = matrixModels[matrixModels.length - 1].getMatrix().copy();
        Matrix sensMatNminus1 = matrixModels[matrixModels.length - 2].getMatrix().copy();

        for (int i = matrixModels.length - 3; i > 0; i --) {
            sensMatNminus1 = sensMatNminus1.times( matrixModels[i].getMatrix() );
        }

        sensMatN = sensMatN.times( sensMatNminus1 );

        // RATIOS:  convert to model form
        ratioSensitivityVectors = new JacobianMatrixModel( "-2" );

        // use setrows to prime setcols then redo setrows for vectors
        ratioSensitivityVectors.setRows( getMatrixSpecs()[0] );
        ratioSensitivityVectors.setCols( ratioSensitivityVectors.getRows() );
        ratioSensitivityVectors.setRows( getMatrixSpecs()[matrixModels.length - 2] );
        ratioSensitivityVectors.setMatrix( sensMatNminus1 );


        // cut out and initialize the three sub matrixModels for ratios
        analyticalRatioSensitivityVectors = new JacobianMatrixModel( "-2" );
        analyticalRatioSensitivityVectors.setRows( ratioSensitivityVectors.getRows() );
        analyticalRatioSensitivityVectors.setCols( ((CovarianceMatrixWithSubMatricesModel) matrixModels[0]).getAnalyticalCovarianceMatrix().getRows() );
        analyticalRatioSensitivityVectors.initializeMatrixModelFromMatrixModel( ratioSensitivityVectors );

        tracerRatioSensitivityVectors = new JacobianMatrixModel( "-2" );
        tracerRatioSensitivityVectors.setRows( ratioSensitivityVectors.getRows() );
        tracerRatioSensitivityVectors.setCols( ((CovarianceMatrixWithSubMatricesModel) matrixModels[0]).getTracerCovarianceMatrix().getRows() );
        tracerRatioSensitivityVectors.initializeMatrixModelFromMatrixModel( ratioSensitivityVectors );

        lambdaRatioSensitivityVectors = new JacobianMatrixModel( "-2" );
        lambdaRatioSensitivityVectors.setRows( ratioSensitivityVectors.getRows() );
        lambdaRatioSensitivityVectors.setCols( ((CovarianceMatrixWithSubMatricesModel) matrixModels[0]).getLambdasCovarianceMatrix().getRows() );
        lambdaRatioSensitivityVectors.initializeMatrixModelFromMatrixModel( ratioSensitivityVectors );


        // DATES:  convert to model form
        dateSensitivityVectors = new JacobianMatrixModel( "-2" );

        // use setrows to prime setcols then redo setrows for vectors
        dateSensitivityVectors.setRows( getMatrixSpecs()[0] );
        dateSensitivityVectors.setCols( dateSensitivityVectors.getRows() );
        dateSensitivityVectors.setRows( getMatrixSpecs()[matrixModels.length - 1] );
        dateSensitivityVectors.setMatrix( sensMatN );

        // cut out and initialize the three sub matrixModels for dates
        analyticalDateSensitivityVectors = new JacobianMatrixModel( "-2" );
        analyticalDateSensitivityVectors.setRows( dateSensitivityVectors.getRows() );
        analyticalDateSensitivityVectors.setCols( ((CovarianceMatrixWithSubMatricesModel) matrixModels[0]).getAnalyticalCovarianceMatrix().getRows() );
        analyticalDateSensitivityVectors.initializeMatrixModelFromMatrixModel( dateSensitivityVectors );

        tracerDateSensitivityVectors = new JacobianMatrixModel( "-2" );
        tracerDateSensitivityVectors.setRows( dateSensitivityVectors.getRows() );
        tracerDateSensitivityVectors.setCols( ((CovarianceMatrixWithSubMatricesModel) matrixModels[0]).getTracerCovarianceMatrix().getRows() );
        tracerDateSensitivityVectors.initializeMatrixModelFromMatrixModel( dateSensitivityVectors );

        lambdaDateSensitivityVectors = new JacobianMatrixModel( "-2" );
        lambdaDateSensitivityVectors.setRows( dateSensitivityVectors.getRows() );
        lambdaDateSensitivityVectors.setCols( ((CovarianceMatrixWithSubMatricesModel) matrixModels[0]).getLambdasCovarianceMatrix().getRows() );
        lambdaDateSensitivityVectors.initializeMatrixModelFromMatrixModel( dateSensitivityVectors );

    }

    /**
     * Serves data to UncertaintyZoomLayer
     *
     * @param dateName
     * @param uncertaintyMode
     * @return
     */
    public AbstractMatrixModel extractSensitivityVectorForDate (//
            String dateName,
            String uncertaintyMode ) {

        int rowForSpecificDate;
        AbstractMatrixModel sensitivityMatrix;
        AbstractMatrixModel retVector = new JacobianMatrixModel( "-2" );

        if ( uncertaintyMode.equalsIgnoreCase( "Analytical" ) ) {
            rowForSpecificDate = analyticalMiniDateCovMatModel.getCols().get( dateName );
            sensitivityMatrix = analyticalDateSensitivityVectors;
            retVector.setCols(((CovarianceMatrixWithSubMatricesModel)matrixModels[0]).getAnalyticalCovarianceMatrix().getRows());
            
        } else if ( uncertaintyMode.equalsIgnoreCase( "Tracer" ) ) {
            // here need to marry analytical and tracer or subtract lambda from all
            rowForSpecificDate = dateCovMatModel.getCols().get( dateName );
            sensitivityMatrix = dateSensitivityVectors.copy();
            retVector.setCols( matrixModels[0].getRows() );
            // zero out the lambda elements for the rowForSpecificDate we are looking at
            for (Lambdas lambda : Lambdas.values()) {
                try {
                    int col = sensitivityMatrix.getCols().get( lambda.getName() );
                    sensitivityMatrix.setValueAt( rowForSpecificDate, col, 0.0 );
                } catch (Exception e) {
                }
            }

        } else {
            // all uncertainty, so send the whole thing
            rowForSpecificDate = dateCovMatModel.getCols().get( dateName );
            sensitivityMatrix = dateSensitivityVectors;
            retVector.setCols( matrixModels[0].getRows() );
        }

        Matrix rowSelected = //
                sensitivityMatrix.getMatrix().//
                getMatrix( rowForSpecificDate, rowForSpecificDate, 0, sensitivityMatrix.getMatrix().getColumnDimension() - 1 );

        retVector.setRows( new String[]{dateName} );
        retVector.setMatrix( rowSelected );

        return retVector;
    }

    private void calculateCorrelationCoefficients () {

        calculateConcordiaRho( "r206_238r", "r207_235r" );
        calculateConcordiaRho( "r206_238r", "r207_235r_Pa" );
        calculateConcordiaRho( "r206_238r_Th", "r207_235r" );
        calculateConcordiaRho( "r206_238r_Th", "r207_235r_Pa" );

        calculateTeraWasserburgRho( "r206_238r", "r207_206r" );
        // calculateTeraWasserburgRho("r206_238r", "r207_206r_Th");
        calculateTeraWasserburgRho( "r206_238r", "r207_206r_Pa" );
        // calculateTeraWasserburgRho("r206_238r", "r207_206r_ThPa");
        calculateTeraWasserburgRho( "r206_238r_Th", "r207_206r_Th" );
        // calculateTeraWasserburgRho("r206_238r_Th", "r207_206r_Pa");
        calculateTeraWasserburgRho( "r206_238r_Th", "r207_206r_ThPa" );

    }

    private void calculateConcordiaRho ( String r206_238Name, String r207_235Name ) {

        double cov = ((CovarianceMatrixModel) analyticalMiniRatioCovMatModel).//
                getCovarianceCell( r206_238Name, r207_235Name );
        double var1 = ((CovarianceMatrixModel) analyticalMiniRatioCovMatModel).//
                getCovarianceCell( r206_238Name, r206_238Name );
        double var2 = ((CovarianceMatrixModel) analyticalMiniRatioCovMatModel).//
                getCovarianceCell( r207_235Name, r207_235Name );

        double rho = 0.0;
        if ( (var1 * var2) > 0.0 ) {
            rho = cov / Math.sqrt( var1 * var2 );
        }

        String rhoName = "rhoR" + r206_238Name.substring( 1 ) + "__" + r207_235Name;

        fraction.setRadiogenicIsotopeRatioByName(//
                rhoName,
                new ValueModel(//
                //
                rhoName,
                new BigDecimal( Double.toString( rho ) ),
                "ABS",
                BigDecimal.ZERO, BigDecimal.ZERO ) );

    }

    private void calculateTeraWasserburgRho ( String r206_238Name, String r207_206Name ) {

        double cov = -1 * ((CovarianceMatrixModel) analyticalMiniRatioCovMatModel).//
                getCovarianceCell( r206_238Name, r207_206Name )//
                / fraction.getRadiogenicIsotopeRatioByName( r206_238Name ).getValue().//
                pow( 2 ).doubleValue();

        double var1 = ((CovarianceMatrixModel) analyticalMiniRatioCovMatModel).//
                getCovarianceCell( r207_206Name, r207_206Name );

        double oneSigma2 =//
                Math.sqrt( ((CovarianceMatrixModel) analyticalMiniRatioCovMatModel).//
                getCovarianceCell( r206_238Name, r206_238Name ) )//
                / fraction.getRadiogenicIsotopeRatioByName( r206_238Name ).getValue().//
                pow( 2 ).doubleValue();

        double rho = ReduxConstants.NO_RHO_FLAG;// June 2010 to force out of range of legal cov [-1,,,1]0.0;
        if (  ! Double.isInfinite( oneSigma2 ) &&  Double.isFinite( oneSigma2 ) && (var1 * oneSigma2) >= 0.0 ) {
            //if ( (var1 * oneSigma2) != 0.0 ) {
            rho = cov / Math.sqrt( var1 ) / oneSigma2;
        }

        String r238_206Name = "r238_206r" + r206_238Name.substring( 9 );

        String rhoName = "rhoR" + r207_206Name.substring( 1 ) + "__" + r238_206Name;

        fraction.setRadiogenicIsotopeRatioByName(//
                rhoName,
                new ValueModel(//
                //
                rhoName,
                new BigDecimal( Double.toString( rho ) ),
                "ABS",
                BigDecimal.ZERO, BigDecimal.ZERO ) );

    }

    /**
     * Build. array of clumpSpecs based on covariance matrix. ClumpSpecs specify
     * which inputs and how they are organized for display on kwiki tab. They
     * depend on covariance matrix input names.
     */
    private void buildClumpSpecsForKwiki () {

        // get universal clump specs
        String[][][] universalClumpSpecs = DataDictionary.universalClumpedInputVariableSpecs;
        // walk each list and collect those that occur in covariance matrix
        clumpSpecs = new String[universalClumpSpecs.length][][];

        for (int i = 0; i < universalClumpSpecs.length; i ++) {
            ArrayList<String[]> temp = new ArrayList<String[]>();
            for (int j = 0; j < universalClumpSpecs[i].length; j ++) {
                if ( matrixModels[0].getCols().containsKey( universalClumpSpecs[i][j][0] ) ) {
                    temp.add( universalClumpSpecs[i][j] );
                }
            }
            clumpSpecs[i] = new String[temp.size()][];
            for (int j = 0; j < temp.size(); j ++) {
                clumpSpecs[i][j] = temp.get( j );
            }
        }

    }

    /**
     * Creates covariance lists for uncertainties in clumps that will be used to
     * tie sliders together on kwiki sensitivity window.
     */
    private void populateKwikiClumpCovariances () {

        // first extract a list of covarying terms for each term
        covaryingTerms = new String[clumpSpecs.length][][];
        // produces an array of covarying terms for each term in clump
        for (int c = 0; c < clumpSpecs.length; c ++) {
            covaryingTerms[c] = new String[clumpSpecs[c].length][];
            ArrayList<Set<String>> covaryingWorkList = new ArrayList<Set<String>>();
            for (int i = 0; i < clumpSpecs[c].length; i ++) {
                covaryingWorkList.add( new TreeSet<String>() );
                // puts term we are interested in first
                covaryingWorkList.get( i ).add( clumpSpecs[c][i][0] );
                int row = matrixModels[0].getCols().get( clumpSpecs[c][i][0] );
                for (int j = 0; j < clumpSpecs[c].length; j ++) {
                    int col = matrixModels[0].getCols().get( clumpSpecs[c][j][0] );
                    if ( (i != j) && (matrixModels[0].getMatrix().get( row, col ) != 0.0) ) {
                        covaryingWorkList.get( i ).add( clumpSpecs[c][j][0] );
                    }
                }
            }

            // process each clump to make sure all covarying terms have same list = the most inclusive
            for (int i = 0; i < covaryingWorkList.size() - 1; i ++) {
                for (int j = i + 1; j < covaryingWorkList.size(); j ++) {
                    if ( covaryingWorkList.get( i ).containsAll( covaryingWorkList.get( j ) ) ) {
                        covaryingWorkList.get( j ).addAll( covaryingWorkList.get( i ) );
                    } else if ( covaryingWorkList.get( j ).containsAll( covaryingWorkList.get( i ) ) ) {
                        covaryingWorkList.get( i ).addAll( covaryingWorkList.get( j ) );
                    }
                }
            }

            for (int i = 0; i < covaryingWorkList.size(); i ++) {
                // force subject to first place - covarince coordination depends on it
                covaryingTerms[c][i] = new String[covaryingWorkList.get( i ).size()];
                covaryingTerms[c][i][0] = clumpSpecs[c][i][0];
                covaryingWorkList.get( i ).remove( clumpSpecs[c][i][0] );
                int j = 1;
                for (String s : covaryingWorkList.get( i )) {
                    covaryingTerms[c][i][j ++] = s;
                }
            }
        }

    }

    /**
     * Uses reflection to get method associated with a specific input name,
     * which is a rowForSpecificDate or column name in a matrix.
     *
     * @param inputName
     * @return
     */
    public Method retrieveMethodNameForInput ( String inputName ) {
        Method meth = null;
        String methodName = DataDictionary.MapOfInputsToMethodNames.get( inputName );
        if ( methodName != null ) {
            try {
                Class<?> fractionClass =//
                        Class.forName( UPbFraction.class.getCanonicalName() );

                meth = fractionClass.getMethod(//
                        methodName,
                        new Class[]{String.class} );
            } catch (ClassNotFoundException classNotFoundException) {
            } catch (NoSuchMethodException noSuchMethodException) {
            } catch (SecurityException securityException) {
            }
        }
        return meth;
    }

    /**
     * Serves data to UncertaintyZoomLayer
     *
     * @param unctMode
     * @return
     */
    public AbstractMatrixModel chooseCovarianceMatrixModelByMode ( String unctMode ) {
        AbstractMatrixModel retval = null;

        if ( unctMode.equalsIgnoreCase( "Analytical" ) ) {
            retval = ((CovarianceMatrixWithSubMatricesModel) matrixModels[0]).getAnalyticalCovarianceMatrix();

        } else {
            // lambda = everything; tracer = everything, since the sensitivity vector lambdas are zeroed
            retval = matrixModels[0];
        }
        return retval;
    }

    /**
     *
     * @return
     */
    public AbstractMatrixModel getTracerMiniCovarianceMatrix () {
        return ((CovarianceMatrixWithSubMatricesModel) matrixModels[0]).getTracerCovarianceMatrix();
    }

    /**
     *
     * @return
     */
    public AbstractMatrixModel getLambdasMiniCovarianceMatrix () {
        return ((CovarianceMatrixWithSubMatricesModel) matrixModels[0]).getLambdasCovarianceMatrix();
    }

    /**
     * @return the matrixModels
     */
    public AbstractMatrixModel[] getMatrices () {
        return matrixModels;
    }

    /**
     * @param matrices
     */
    public void setMatrices ( AbstractMatrixModel[] matrices ) {
        this.matrixModels = matrices;
    }

    /**
     * @return the clumpSpecs
     */
    public String[][][] getClumpSpecs () {
        return clumpSpecs;
    }

    /**
     * @param clumpSpecs the clumpSpecs to set
     */
    public void setClumpSpecs ( String[][][] clumpSpecs ) {
        this.clumpSpecs = clumpSpecs;
    }

    /**
     * @return the matrixSpecs
     */
    public String[][] getMatrixSpecs () {
        return matrixSpecs;
    }

    /**
     * @return the covaryingTerms
     */
    public String[][][] getCovaryingTerms () {
        return covaryingTerms;
    }

    /**
     * @param covaryingTerms the covaryingTerms to set
     */
    public void setCovaryingTerms ( String[][][] covaryingTerms ) {
        this.covaryingTerms = covaryingTerms;
    }

    /**
     * @return the analyticalMiniDateCovMatModel
     */
    public AbstractMatrixModel getAnalyticalMiniDateCovMatModel () {
        return analyticalMiniDateCovMatModel;
    }

    /**
     * @return the measuredRatioUncertaintiesValidity
     */
    public String getMeasuredRatioUncertaintiesValidity () {
        return measuredRatioUncertaintiesValidity;
    }

    /**
     * @param measuredRatioUncertaintiesValidity the
     * measuredRatioUncertaintiesValidity to set
     */
    public void setMeasuredRatioUncertaintiesValidity ( String measuredRatioUncertaintiesValidity ) {
        this.measuredRatioUncertaintiesValidity = measuredRatioUncertaintiesValidity;
    }

    /**
     * @return the tracerMiniDateCovMatModel
     */
    public AbstractMatrixModel getTracerMiniDateCovMatModel () {
        return tracerMiniDateCovMatModel;
    }

    /**
     * @return the lambdaMiniDateCovMatModel
     */
    public AbstractMatrixModel getLambdaMiniDateCovMatModel () {
        return lambdaMiniDateCovMatModel;
    }

    /**
     * @return the tracerDateSensitivityVectors
     */
    public AbstractMatrixModel getTracerDateSensitivityVectors () {
        return tracerDateSensitivityVectors;
    }

    /**
     * @return the lambdaDateSensitivityVectors
     */
    public AbstractMatrixModel getLambdaDateSensitivityVectors () {
        return lambdaDateSensitivityVectors;
    }

    /**
     * @return the dateCovMatModel
     */
    public AbstractMatrixModel getDateCovMatModel () {
        return dateCovMatModel;
    }

    /**
     *
     */
    public void toFileAllMatrices () {
        File matrixFile = new File( "MATRIX_FILE_" + fraction.getFractionID() + ".txt" );
        PrintWriter matrixWriter = null;

        try {
            matrixWriter = new PrintWriter( new FileWriter( matrixFile ) );
            matrixWriter.println( "\n\n******   FRACTION " + fraction.getFractionID() + "   ********************\n\n" );

            for (int i = 0; i < getMatrices().length; i ++) {
                matrixWriter.println( getMatrices()[i].ToStringWithLabels() );
                matrixWriter.println();
            }

            matrixWriter.println( "\n\n ANALYTICAL MINI COVARIANCE MATRIX \n" );
            matrixWriter.println( ((CovarianceMatrixWithSubMatricesModel) matrixModels[0]).getAnalyticalCovarianceMatrix().ToStringWithLabels() );

            matrixWriter.println( "\n\n TRACERS MINI COVARIANCE MATRIX \n" );
            matrixWriter.println( ((CovarianceMatrixWithSubMatricesModel) matrixModels[0]).getTracerCovarianceMatrix().ToStringWithLabels() );

            matrixWriter.println( "\n\n LAMBDAS MINI COVARIANCE MATRIX \n" );
            matrixWriter.println( ((CovarianceMatrixWithSubMatricesModel) matrixModels[0]).getLambdasCovarianceMatrix().ToStringWithLabels() );


            matrixWriter.println( "\n\n RATIO SENSITIVITY MATRIX \n" );
            matrixWriter.println( ratioSensitivityVectors.ToStringWithLabels() );

            matrixWriter.println( "\n\n ANALYTICAL MINI RATIO SENSITIVITY MATRIX \n" );
            matrixWriter.println( analyticalRatioSensitivityVectors.ToStringWithLabels() );

            matrixWriter.println( "\n\n TRACER MINI RATIO SENSITIVITY MATRIX \n" );
            matrixWriter.println( tracerRatioSensitivityVectors.ToStringWithLabels() );

            matrixWriter.println( "\n\n LAMBDA MINI RATIO SENSITIVITY MATRIX \n" );
            matrixWriter.println( lambdaRatioSensitivityVectors.ToStringWithLabels() );



            matrixWriter.println( "\n\n DATE SENSITIVITY MATRIX \n" );
            matrixWriter.println( dateSensitivityVectors.ToStringWithLabels() );

            matrixWriter.println( "\n\n ANALYTICAL MINI DATE SENSITIVITY MATRIX \n" );
            matrixWriter.println( analyticalDateSensitivityVectors.ToStringWithLabels() );

            matrixWriter.println( "\n\n TRACER MINI DATE SENSITIVITY MATRIX \n" );
            matrixWriter.println( tracerDateSensitivityVectors.ToStringWithLabels() );

            matrixWriter.println( "\n\n LAMBDA MINI DATE SENSITIVITY MATRIX \n" );
            matrixWriter.println( lambdaDateSensitivityVectors.ToStringWithLabels() );


            matrixWriter.println( "\n\nRATIO COVARIANCE MATRIX\n" );
            matrixWriter.println( ratioCovMatModel.ToStringWithLabels() );

            matrixWriter.println( "\n\n ANALYTICAL MINI RATIO COVARIANCE MATRIX\n" );
            matrixWriter.println( analyticalMiniRatioCovMatModel.ToStringWithLabels() );

            matrixWriter.println( "\n\n TRACER MINI RATIO COVARIANCE MATRIX\n" );
            matrixWriter.println( tracerMiniRatioCovMatModel.ToStringWithLabels() );

            matrixWriter.println( "\n\n LAMBDA MINI RATIO COVARIANCE MATRIX\n" );
            matrixWriter.println( lambdaMiniRatioCovMatModel.ToStringWithLabels() );



            matrixWriter.println( "\n\nDATE COVARIANCE MATRIX\n" );
            matrixWriter.println( dateCovMatModel.ToStringWithLabels() );

            matrixWriter.println( "\n\n ANALYTICAL MINI DATE COVARIANCE MATRIX\n" );
            matrixWriter.println( analyticalMiniDateCovMatModel.ToStringWithLabels() );

            matrixWriter.println( "\n\n TRACER MINI DATE COVARIANCE MATRIX\n" );
            matrixWriter.println( tracerMiniDateCovMatModel.ToStringWithLabels() );

            matrixWriter.println( "\n\n LAMBDA MINI DATE COVARIANCE MATRIX\n" );
            matrixWriter.println( lambdaMiniDateCovMatModel.ToStringWithLabels() );


            NumberFormat formatter = new DecimalFormat( "0.0000000000E0" );
            matrixWriter.println( "\n\n TEST DATES AND VARIANCES\n" );

            matrixWriter.println( String.format( "   %1$-23s", "NAME" ) + " = " //
                    + String.format( "%1$-20s", "ANALYTICAL" ) //
                    + String.format( "%1$-20s", "TRACER" )//
                    + String.format( "%1$-20s", "LAMBDA" )//
                    + String.format( "%1$-20s", "FULL_DATE" )//
                    + String.format( "%1$-20s", "CHECK_SUM" )//
                    + String.format( "%1$-20s", "NEW_ANAL_1-sig" )//
                    + String.format( "%1$-20s", "OLD_ANAL_1-sig" )//
                    + String.format( "%1$-20s", "DELTA_1-sig" ) );

            for (int i = 0; i < dateCovMatModel.getRows().size(); i ++) {
                double test = analyticalMiniDateCovMatModel.getMatrix().get( i, i ) + tracerMiniDateCovMatModel.getMatrix().get( i, i ) + lambdaMiniDateCovMatModel.getMatrix().get( i, i );
                double choiceVal = //
                        (double) ((i < 8) ? fraction.getRadiogenicIsotopeDateByName( dateCovMatModel.getRows().get( i ) ).getOneSigmaAbs().doubleValue()//
                        : fraction.getRadiogenicIsotopeRatioByName( dateCovMatModel.getRows().get( i ) ).getOneSigmaAbs().doubleValue());

                matrixWriter.println( String.format( "   %1$-23s", dateCovMatModel.getRows().get( i ) ) + " = " //
                        + String.format( "%1$-20s", formatter.format( analyticalMiniDateCovMatModel.getMatrix().get( i, i ) ) ) //
                        + String.format( "%1$-20s", formatter.format( tracerMiniDateCovMatModel.getMatrix().get( i, i ) ) ) //
                        + String.format( "%1$-20s", formatter.format( lambdaMiniDateCovMatModel.getMatrix().get( i, i ) ) ) //
                        + String.format( "%1$-20s", formatter.format( dateCovMatModel.getMatrix().get( i, i ) ) ) //))
                        + String.format( "%1$-20s", formatter.format( test ) ) //
                        + String.format( "%1$-20s", formatter.format( Math.sqrt( analyticalMiniDateCovMatModel.getMatrix().get( i, i ) ) ) )//
                        + String.format( "%1$-20s", formatter.format( choiceVal ) )//
                        + String.format( "%1$-20s", formatter.format( Math.abs( choiceVal - Math.sqrt( analyticalMiniDateCovMatModel.getMatrix().get( i, i ) ) ) ) )//
                        );

                matrixWriter.println();
            }

            matrixWriter.flush();
            matrixWriter.close();

        } catch (IOException iOException) {
        }

        try {
            BrowserControl.displayURL( matrixFile.getCanonicalPath() );
        } catch (IOException ex) {
        }

    }

    /**
     * @return the matrixSpecsName
     */
    public String getMatrixSpecsName () {
        return matrixSpecsName;
    }

    /**
     * @param matrixSpecsName the matrixSpecsName to set
     */
    public void setMatrixSpecsName ( String matrixSpecsName ) {
        this.matrixSpecsName = matrixSpecsName;
    }
}
