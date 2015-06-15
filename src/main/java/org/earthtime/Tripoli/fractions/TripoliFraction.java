/*
 * TripoliFraction.java
 *
 * Created Jul 3, 2011
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
package org.earthtime.Tripoli.fractions;

import Jama.Matrix;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import org.earthtime.Tripoli.dataModels.DataModelFitFunctionInterface;
import org.earthtime.Tripoli.dataModels.DataModelInterface;
import org.earthtime.Tripoli.dataModels.MaskingSingleton;
import org.earthtime.Tripoli.dataModels.RawRatioDataModel;
import org.earthtime.Tripoli.fitFunctions.AbstractFunctionOfX;
import org.earthtime.UPb_Redux.fractions.FractionI;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.dataDictionaries.RadDatesForPbCorrSynchEnum;
import org.earthtime.dataDictionaries.RawRatioNames;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataModels.initialPbModelsET.PlaceholderInitialPb76Model;
import org.earthtime.ratioDataModels.initialPbModelsET.StaceyKramersInitialPbModelET;
import org.earthtime.ratioDataModels.initialPbModelsET.commonLeadLossCorrectionSchemes.AbstractCommonLeadLossCorrectionScheme;
import org.earthtime.ratioDataModels.initialPbModelsET.commonLeadLossCorrectionSchemes.CommonLeadLossCorrectionSchemeNONE;
import org.earthtime.utilities.DragAndDropListItemInterface;
import org.earthtime.utilities.TimeToString;
import org.earthtime.utilities.jamaHelpers.MatrixRemover;

/**
 *
 * @author James F. Bowring
 */
public class TripoliFraction implements //
        Serializable, //
        Comparable<TripoliFraction>, //
        TripoliFractionViewInterface, //
        DragAndDropListItemInterface {
    // Class variables

    private static final long serialVersionUID = 2040812902406395230L;

    private String fractionID;
    private boolean standard;
    private long peakTimeStamp;
    private long backgroundTimeStamp;
    private long zeroBasedTimeStamp;
    private long zeroBasedNormalizedTimeStamp;
    /**
     *
     */
    protected SortedSet<DataModelInterface> rawRatios;
    private boolean[] dataActiveMap;
    private boolean included;
    private boolean colorMeExcluded;
    private int showVerticalLineAtThisIndex;
    private int showSecondVerticalLineAtThisIndex;
    // jan 2011 for selectionbox
    private transient double selBoxFirstY;
    private transient double selBoxSecondY;
    private transient ArrayList<Integer> selectedForToggleIndexes;
    private boolean showLocalYAxis;
    private boolean showLocalInterceptFitPanel;
    private FractionI uPbFraction;
    // jan 2014
    private String commonLeadCorrectionHighestLevel;
    private AbstractCommonLeadLossCorrectionScheme commonLeadLossCorrectionScheme;
    private AbstractRatiosDataModel initialPbModelET;

    // sep 2014 re-work to accommodate new uncertainties of var and sys
    private ValueModel initialPbSchemeB_R206_204c;
    private ValueModel initialPbSchemeB_R207_204c;
    private ValueModel initialPbSchemeB_R208_204c;
    private ValueModel initialPbPlaceHolderModelR206_204c;
    private ValueModel initialPbPlaceHolderModelR207_204c;
    private ValueModel initialPbPlaceHolderModelR208_204c;
    private Map<String, BigDecimal> initialPbPlaceHolderVarRhos;
    private Map<String, BigDecimal> initialPbPlaceHolderSysRhos;
    private ValueModel initialPbSchemeA_r207_206c;
    private ValueModel initialPbPlaceHolderModelR207_206c;

    // stacey kramer parameters
    private BigDecimal skEstimatedDate;
    private BigDecimal skOneSigmaVarUnctPct;
    private BigDecimal skOneSigmaSysUnctPct;
    private BigDecimal skRhoVarUnct;
    private BigDecimal skRhoSysUnct;

    private transient double upperPhi_r206_207;
    private ValueModel sampleR238_235s;
    private RadDatesForPbCorrSynchEnum radDateForSKSynch;

    private boolean currentlyFitted;

    public TripoliFraction( //
            String fractionID, //
            String commonLeadCorrectionHighestLevel, //
            boolean standard, //
            long backgroundTimeStamp, //
            long peakTimeStamp,//
            int blockSize) {

        this.fractionID = fractionID;
        this.standard = standard;
        this.backgroundTimeStamp = backgroundTimeStamp;
        this.peakTimeStamp = peakTimeStamp;
        this.zeroBasedTimeStamp = 0;
        this.zeroBasedNormalizedTimeStamp = 0;

        this.included = true;
        this.colorMeExcluded = false;
        this.showVerticalLineAtThisIndex = -1;
        this.showSecondVerticalLineAtThisIndex = -1;
        this.selBoxFirstY = 0;
        this.selBoxSecondY = 0;
        this.selectedForToggleIndexes = new ArrayList<>();

        this.showLocalYAxis = false;
        this.showLocalInterceptFitPanel = false;

        this.initialPbModelET = StaceyKramersInitialPbModelET.getStaceyKramersInstance();

        this.initialPbSchemeA_r207_206c = new ValueModel("r207_206c", BigDecimal.ZERO, "PCT", BigDecimal.ZERO, BigDecimal.ZERO);
        this.initialPbPlaceHolderModelR207_206c = new ValueModel("r207_206c", BigDecimal.ZERO, "PCT", BigDecimal.ZERO, BigDecimal.ZERO);

        this.initialPbSchemeB_R206_204c = new ValueModel("r206_204c", BigDecimal.ZERO, "ABS", BigDecimal.ZERO, BigDecimal.ZERO);
        this.initialPbSchemeB_R207_204c = new ValueModel("r207_204c", BigDecimal.ZERO, "ABS", BigDecimal.ZERO, BigDecimal.ZERO);
        this.initialPbSchemeB_R208_204c = new ValueModel("r208_204c", BigDecimal.ZERO, "ABS", BigDecimal.ZERO, BigDecimal.ZERO);

        this.initialPbPlaceHolderModelR206_204c = new ValueModel("r206_204c", BigDecimal.ZERO, "ABS", BigDecimal.ZERO, BigDecimal.ZERO);
        this.initialPbPlaceHolderModelR207_204c = new ValueModel("r207_204c", BigDecimal.ZERO, "ABS", BigDecimal.ZERO, BigDecimal.ZERO);
        this.initialPbPlaceHolderModelR208_204c = new ValueModel("r208_204c", BigDecimal.ZERO, "ABS", BigDecimal.ZERO, BigDecimal.ZERO);
        this.initialPbPlaceHolderVarRhos = new HashMap<>();
        initialPbPlaceHolderVarRhos.put("rhoR206_204c__r207_204c", new BigDecimal("0.5"));
        initialPbPlaceHolderVarRhos.put("rhoR206_204c__r208_204c", new BigDecimal("0.5"));
        initialPbPlaceHolderVarRhos.put("rhoR207_204c__r208_204c", new BigDecimal("0.5"));
        this.initialPbPlaceHolderSysRhos = new HashMap<>();
        initialPbPlaceHolderSysRhos.put("rhoR206_204c__r207_204c", new BigDecimal("0.5"));
        initialPbPlaceHolderSysRhos.put("rhoR206_204c__r208_204c", new BigDecimal("0.5"));
        initialPbPlaceHolderSysRhos.put("rhoR207_204c__r208_204c", new BigDecimal("0.5"));

        this.skEstimatedDate = BigDecimal.ZERO;
        this.skOneSigmaVarUnctPct = BigDecimal.ZERO;
        this.skOneSigmaSysUnctPct = BigDecimal.ZERO;
        this.skRhoVarUnct = BigDecimal.ZERO;
        this.skRhoSysUnct = BigDecimal.ZERO;

        this.commonLeadCorrectionHighestLevel = commonLeadCorrectionHighestLevel;
        // default behavior
        if (isStandard()) {
            this.commonLeadLossCorrectionScheme = CommonLeadLossCorrectionSchemeNONE.getInstance();
        } else {
            try {
                Class highestScheme = Class.forName(AbstractCommonLeadLossCorrectionScheme.class.getPackage().getName() + ".CommonLeadLossCorrectionScheme" + commonLeadCorrectionHighestLevel);
                Method m = highestScheme.getDeclaredMethod("getInstance", new Class[]{});
                this.commonLeadLossCorrectionScheme = (AbstractCommonLeadLossCorrectionScheme) m.invoke(new Object(), new Class[]{});
            } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException classNotFoundException) {
                this.commonLeadLossCorrectionScheme = CommonLeadLossCorrectionSchemeNONE.getInstance();
            }
        }
        this.upperPhi_r206_207 = 0.0;

        // init activeDataMap
        this.dataActiveMap = new boolean[blockSize];
        for (int i = 0; i < dataActiveMap.length; i++) {
            dataActiveMap[i] = true;
        }

        this.sampleR238_235s = new ValueModel("r238_235s");

        this.radDateForSKSynch = RadDatesForPbCorrSynchEnum.date206_238r;

        this.currentlyFitted = false;

    }

    /**
     *
     *
     * @param fractionID
     * @param commonLeadCorrectionHighestLevel the value of
     * commonLeadCorrectionHighestLevel
     * @param standard
     * @param backgroundTimeStamp the value of backgroundTimeStamp
     * @param peakTimeStamp
     * @param rawRatios
     */
    public TripoliFraction( //
            String fractionID, //
            String commonLeadCorrectionHighestLevel, //
            boolean standard, long backgroundTimeStamp, //
            long peakTimeStamp, //
            SortedSet<DataModelInterface> rawRatios) {

        this(fractionID, commonLeadCorrectionHighestLevel, standard, backgroundTimeStamp, peakTimeStamp, ((RawRatioDataModel) rawRatios.first()).getRatios().length);

        this.rawRatios = rawRatios;

    }

    /**
     *
     * @param tf
     * @return
     */
    @Override
    public int compareTo(TripoliFraction tf) {
        long tfTimeStamp = tf.getPeakTimeStamp();
        long myTfTimeStamp = this.getPeakTimeStamp();

        return ((Long) myTfTimeStamp).compareTo((Long) tfTimeStamp);
    }

    /**
     *
     * @param standardValuesMap
     */
    public void updateRawRatioDataModelsWithPrimaryStandardValue(Double[] standardValuesMap) {
        // since ratios are sorted, we send in ordered array of corresponding standard values
        //TODO: make more robust
        SortedSet<DataModelInterface> ratiosSortedSet = getRatiosForFractionFitting();//getValidRawRatios();

        int count = 0;

        Iterator<DataModelInterface> ratiosSortedSetIterator = ratiosSortedSet.iterator();
        while (ratiosSortedSetIterator.hasNext()) {
            DataModelInterface ratio = ratiosSortedSetIterator.next();
            ((RawRatioDataModel) ratio).setStandardValue(standardValuesMap[count]);

            // oct 2012 check for zero or missing standard ratio and remove ratio from fractination consideration
            //if ( standardValuesMap[count] == 0.0 ) {
            ((RawRatioDataModel) ratio).setUsedForFractionationCorrections(standardValuesMap[count] != 0.0);
            //}
            count++;
        }

    }

    /**
     *
     * @param rrName
     * @return
     */
    public DataModelInterface getRawRatioDataModelByName(RawRatioNames rrName) {
        DataModelInterface foundRRDataModel = null;
        for (DataModelInterface rr : rawRatios) {
            if (rr.getRawRatioModelName().equals(rrName)) {
                foundRRDataModel = rr;
                break;
            }
        }

        return foundRRDataModel;
    }

    /**
     *
     */
    public void updateCorrectedRatioStatistics() {
        for (DataModelInterface rr : rawRatios) {
            rr.calculateCorrectedRatioStatistics();
        }
    }

    /**
     *
     */
    public void updateInterceptFitFunctionsIncludingCommonLead() {

        // may 2015 needed if user deselects illegal (negative ratios) data
        reProcessToRejectNegativeRatios();

        Iterator ratiosIterator = getRatiosForFractionFitting().iterator();
        System.out.println("Update Intercept Fit Functions Including Pbc for " + fractionID);
        while (ratiosIterator.hasNext()) {
            RawRatioDataModel rr = (RawRatioDataModel) ratiosIterator.next();
            rr.generateSetOfFitFunctions(true, false);
        }

        // nov 2014 this also sets currentlyFitted = true
        postProcessCommonLeadCorrectionRatios();
    }

    /**
     *
     * @return
     */
    public SortedMap<String, ValueModel> assembleCommonLeadCorrectionParameters() {
        SortedMap<String, ValueModel> parameters = new TreeMap<>();

        // initial pb model parameters
        parameters.put("r207_206c", initialPbSchemeA_r207_206c);
        parameters.put("r206_204c", initialPbSchemeB_R206_204c);
        parameters.put("r207_204c", initialPbSchemeB_R207_204c);
        parameters.put("r208_204c", initialPbSchemeB_R208_204c);

        return parameters;
    }

    /**
     *
     * @param tripoliFraction
     */
    public void copyPlaceHolderParameters(TripoliFraction tripoliFraction) {
        initialPbPlaceHolderModelR206_204c = tripoliFraction.getInitialPbPlaceHolderModelR206_204c().copy();
        initialPbPlaceHolderModelR207_204c = tripoliFraction.getInitialPbPlaceHolderModelR207_204c().copy();
        initialPbPlaceHolderModelR208_204c = tripoliFraction.getInitialPbPlaceHolderModelR208_204c().copy();
        initialPbPlaceHolderModelR207_206c = tripoliFraction.getInitialPbPlaceHolderModelR207_206c().copy();

        initialPbPlaceHolderVarRhos = tripoliFraction.copyInitialPbPlaceHolderVarRhos();
        initialPbPlaceHolderSysRhos = tripoliFraction.copyInitialPbPlaceHolderSysRhos();

    }

    /**
     *
     * @return
     */
    public SortedMap<String, BigDecimal> assembleStaceyKramerCorrectionParameters() {
        SortedMap<String, BigDecimal> parameters = new TreeMap<>();

        parameters.put("staceyKramerEstimatedDate", skEstimatedDate);
        parameters.put("staceyKramersOnePctUnct", skOneSigmaVarUnctPct);
        parameters.put("staceyKramersCorrelationCoeffs", skRhoVarUnct);

        // sept 2014 part of a refactoring to handle both var and sys uncertainties
        // parameters according  to new scheme are recorded below ... both will exist for backward compatibility
        parameters.put("skEstimatedDate", skEstimatedDate);
        parameters.put("skOneSigmaVarUnctPct", skOneSigmaVarUnctPct);
        parameters.put("skOneSigmaSysUnctPct", skOneSigmaSysUnctPct);
        parameters.put("skRhoVarUnct", skRhoVarUnct);
        parameters.put("skRhoSysUnct", skRhoSysUnct);

        return parameters;
    }

    /**
     *
     * @param parameters
     */
    public void updateSKParametersFromDataModel(SortedMap<String, BigDecimal> parameters) {
        skEstimatedDate = parameters.get("skEstimatedDate");
        skOneSigmaVarUnctPct = parameters.get("skOneSigmaVarUnctPct");
        skOneSigmaSysUnctPct = parameters.get("skOneSigmaSysUnctPct");
        skRhoVarUnct = parameters.get("skRhoVarUnct");
        skRhoSysUnct = parameters.get("skRhoSysUnct");
    }

    public Matrix calculateUncertaintyPbcCorrections() {
        // nov 2014 additional work for pbc corrections section 6 of paper
        // build a super matrix of all 6 Slogratioxy matrices
        // first need to sum dimensions

        int totalColumns = 0;
        SortedSet<DataModelInterface> ratiosSortedSet = getRatiosForFractionFitting();
        for (DataModelInterface rr : ratiosSortedSet) {
            totalColumns += ((RawRatioDataModel) rr).getSlogRatioX_Y().getColumnDimension();
        }

        Matrix SlogRatioAll = new Matrix(totalColumns, totalColumns, 0.0);
        Matrix JacobianYInterceptLogRatioAll = new Matrix(6, totalColumns, 0.0);
        // these are in the correct order 6/7, 6/38, 8/32, 6/4, 7/4, 8/4
        int totalColumnsUsed = 0;
        int totalRatiosUsed = 0;
        boolean[] savedCommonLeadDataActiveMap = null;
        for (DataModelInterface rr : ratiosSortedSet) {
            Matrix SlrXY = ((RawRatioDataModel) rr).getSlogRatioX_Y();
            int columnsCount = SlrXY.getColumnDimension();
            SlogRatioAll.setMatrix(totalColumnsUsed, totalColumnsUsed + columnsCount - 1, totalColumnsUsed, totalColumnsUsed + columnsCount - 1, SlrXY);

            if (rr.getRawRatioModelName().compareTo(RawRatioNames.r206_207w) == 0) {
                Matrix Sopbclr_206 = ((RawRatioDataModel) rr).getTopSopbclr();
                SlogRatioAll.setMatrix(totalColumnsUsed + columnsCount, totalColumnsUsed + 2 * columnsCount - 1, totalColumnsUsed, totalColumnsUsed + columnsCount - 1, Sopbclr_206);
                SlogRatioAll.setMatrix(totalColumnsUsed, totalColumnsUsed + columnsCount - 1, totalColumnsUsed + columnsCount, totalColumnsUsed + 2 * columnsCount - 1, Sopbclr_206);
            }

            if (rr.getRawRatioModelName().compareTo(RawRatioNames.r206_204w) == 0) {
                savedCommonLeadDataActiveMap = rr.getDataActiveMap();
                Matrix Sopbclr_204 = ((RawRatioDataModel) rr).getBotSopbclr();
                SlogRatioAll.setMatrix(totalColumnsUsed + columnsCount, totalColumnsUsed + 2 * columnsCount - 1, totalColumnsUsed, totalColumnsUsed + columnsCount - 1, Sopbclr_204);
                SlogRatioAll.setMatrix(totalColumnsUsed + 2 * columnsCount, totalColumnsUsed + 3 * columnsCount - 1, totalColumnsUsed, totalColumnsUsed + columnsCount - 1, Sopbclr_204);

                SlogRatioAll.setMatrix(totalColumnsUsed, totalColumnsUsed + columnsCount - 1, totalColumnsUsed + columnsCount, totalColumnsUsed + 2 * columnsCount - 1, Sopbclr_204);
                SlogRatioAll.setMatrix(totalColumnsUsed, totalColumnsUsed + columnsCount - 1, totalColumnsUsed + 2 * columnsCount, totalColumnsUsed + 3 * columnsCount - 1, Sopbclr_204);

                SlogRatioAll.setMatrix(totalColumnsUsed + 2 * columnsCount, totalColumnsUsed + 3 * columnsCount - 1, totalColumnsUsed + columnsCount, totalColumnsUsed + 2 * columnsCount - 1, Sopbclr_204);
                SlogRatioAll.setMatrix(totalColumnsUsed + columnsCount, totalColumnsUsed + 2 * columnsCount - 1, totalColumnsUsed + 2 * columnsCount, totalColumnsUsed + 3 * columnsCount - 1, Sopbclr_204);
            }

            AbstractFunctionOfX FofX = null;

            FofX = ((DataModelFitFunctionInterface) rr).getSelectedFitFunction();
            if (FofX != null) {
                Matrix JacobianYInterceptLogRatioXY = FofX.getMatrixJacobianYInterceptLogRatioXY();
                JacobianYInterceptLogRatioAll.setMatrix(totalRatiosUsed, totalRatiosUsed, totalColumnsUsed, totalColumnsUsed + columnsCount - 1, JacobianYInterceptLogRatioXY);
            }
            totalColumnsUsed += columnsCount;
            totalRatiosUsed += 1;
        }

        //  second pass to cover the rectangular (possibly) off-diagonal elements
        for (DataModelInterface rr : ratiosSortedSet) {
            Matrix SlrXY = ((RawRatioDataModel) rr).getSlogRatioX_Y();
            int columnsCount = SlrXY.getColumnDimension();

            // first test if common lead corrections present
            if (savedCommonLeadDataActiveMap != null) {
                // since the */204 ratios may have fewer points, need to remove the rows and cols associated with those missing points
                if (rr.getRawRatioModelName().compareTo(RawRatioNames.r206_207w) == 0) {
                // walk the 206_207 dataActiveMap and increment matrixIndex for each true entry
                    // if the lead map is false at the same entry, save the matrix index for removal from matrix
                    ArrayList<Integer> matrixIndicesToRemove = new ArrayList<>();
                    int matrixIndex = -1;
                    for (int i = 0; i < rr.getDataActiveMap().length; i++) {
                        if (rr.getDataActiveMap()[i]) {
                            matrixIndex++;
                            if (!savedCommonLeadDataActiveMap[i]) {
                                matrixIndicesToRemove.add(matrixIndex);
                            }
                        }
                    }

                    Matrix Sopbclr_206CorrectedRows = ((RawRatioDataModel) rr).getTopSopbclr().copy();
                    Matrix Sopbclr_206CorrectedCols = ((RawRatioDataModel) rr).getTopSopbclr().copy();
                    Matrix Sopbclr_207CorrectedRows = ((RawRatioDataModel) rr).getBotSopbclr().copy();
                    Matrix Sopbclr_207CorrectedCols = ((RawRatioDataModel) rr).getBotSopbclr().copy();

                    if (matrixIndicesToRemove.size() > 0) {

                        System.out.println("Matrix row col delete for fraction " + fractionID);
                        // reverse list of indices to remove to avoid counting errors
                        Collections.sort(matrixIndicesToRemove, new Comparator<Integer>() {

                            @Override
                            public int compare(Integer i1, Integer i2) {
                                return Integer.compare(i2, i1);
                            }
                        });

                        // walk the list of indices to remove and remove rows and cols before insertion
                        for (Integer indexToRemove : matrixIndicesToRemove) {
                            Sopbclr_206CorrectedRows = MatrixRemover.removeRow(Sopbclr_206CorrectedRows, indexToRemove);
                            Sopbclr_206CorrectedCols = MatrixRemover.removeCol(Sopbclr_206CorrectedCols, indexToRemove);

                            Sopbclr_207CorrectedRows = MatrixRemover.removeRow(Sopbclr_207CorrectedRows, indexToRemove);
                            Sopbclr_207CorrectedCols = MatrixRemover.removeCol(Sopbclr_207CorrectedCols, indexToRemove);
                        }
                    }
                    SlogRatioAll.setMatrix(0 + 3 * columnsCount, matrixIndex + 3 * columnsCount - 1, 0, columnsCount - 1, Sopbclr_206CorrectedRows);
                    SlogRatioAll.setMatrix(0 + 3 * columnsCount, matrixIndex + 3 * columnsCount - 1, columnsCount, 2 * columnsCount - 1, Sopbclr_206CorrectedRows);

                    SlogRatioAll.setMatrix(matrixIndex + 3 * columnsCount, 2 * matrixIndex + 3 * columnsCount - 1, 0, columnsCount - 1, Sopbclr_207CorrectedRows);

                    SlogRatioAll.setMatrix(0, 0, 0 + 3 * columnsCount, +3 * columnsCount + matrixIndex - 1, Sopbclr_206CorrectedCols);
                    SlogRatioAll.setMatrix(columnsCount, columnsCount, 0 + 3 * columnsCount, +3 * columnsCount + matrixIndex - 1, Sopbclr_206CorrectedCols);

                    SlogRatioAll.setMatrix(0, 0, matrixIndex + 3 * columnsCount, 2 * matrixIndex + 3 * columnsCount - 1, Sopbclr_207CorrectedCols);
                }

                if (rr.getRawRatioModelName().compareTo(RawRatioNames.r208_232w) == 0) {
                // walk the r208_232w dataActiveMap and increment matrixIndex for each true entry
                    // if the lead map is false at the same entry, save the matrix index for removal from matrix
                    ArrayList<Integer> matrixIndicesToRemove = new ArrayList<>();
                    int matrixIndex = -1;
                    for (int i = 0; i < rr.getDataActiveMap().length; i++) {
                        if (rr.getDataActiveMap()[i]) {
                            matrixIndex++;
                            if (!savedCommonLeadDataActiveMap[i]) {
                                matrixIndicesToRemove.add(matrixIndex);
                            }
                        }
                    }

                    Matrix Sopbclr_208CorrectedRows = ((RawRatioDataModel) rr).getTopSopbclr().copy();
                    Matrix Sopbclr_208CorrectedCols = ((RawRatioDataModel) rr).getTopSopbclr().copy();

                    if (matrixIndicesToRemove.size() > 0) {
                        // reverse list of indices to remove to avoid counting errors
                        Collections.sort(matrixIndicesToRemove, new Comparator<Integer>() {

                            @Override
                            public int compare(Integer i1, Integer i2) {
                                return Integer.compare(i2, i1);
                            }
                        });

                        // walk the list of indices to remove and remove rows and cols before insertion
                        for (Integer indexToRemove : matrixIndicesToRemove) {
                            Sopbclr_208CorrectedRows = MatrixRemover.removeRow(Sopbclr_208CorrectedRows, indexToRemove);
                            Sopbclr_208CorrectedCols = MatrixRemover.removeCol(Sopbclr_208CorrectedCols, indexToRemove);
                        }
                    }
                    SlogRatioAll.setMatrix(3 * columnsCount + 2 * matrixIndex, 3 * columnsCount + 3 * matrixIndex - 1, 2 * columnsCount, 3 * columnsCount - 1, Sopbclr_208CorrectedRows);

                    SlogRatioAll.setMatrix(2 * columnsCount, 3 * columnsCount - 1, 3 * columnsCount + 2 * matrixIndex, 3 * columnsCount + 3 * matrixIndex - 1, Sopbclr_208CorrectedCols);

                }
            }
        }

        Matrix Sfci = JacobianYInterceptLogRatioAll.times(SlogRatioAll).times(JacobianYInterceptLogRatioAll.transpose());

        System.gc();

        return Sfci;

    }

    /**
     *
     * @param included
     */
    public void toggleAllDataExceptShaded(boolean included) {
        this.included = included;
        for (int i = 0; i < dataActiveMap.length; i++) {
            toggleOneDataAquisition(i, included);
        }

        // jan 2015
        // force masking array
        if (included) {
            applyMaskingArray();
        }
        currentlyFitted = false;
    }

    /**
     *
     */
    public void updateIncludedStatus() {
        boolean haveSomeLiveData = false;
        for (int i = 0; i < dataActiveMap.length; i++) {
            haveSomeLiveData = haveSomeLiveData || dataActiveMap[i];
        }
        included = haveSomeLiveData;
    }

    /**
     *
     * @param index
     * @param datumIncluded
     */
    public void toggleOneDataAquisition(int index, boolean datumIncluded) {
        dataActiveMap[index] = datumIncluded;
        for (DataModelInterface rr : rawRatios) {
            rr.toggleOneDataAquisition(index, datumIncluded);
        }

        if (datumIncluded) {
            this.included = true;
        }
    }

    public void toggleOneDataAquisitionForPbcOnly(int index, boolean datumIncluded) {
        if (dataActiveMap[index]) {
            for (DataModelInterface rr : rawRatios) {
                if (rr.isUsedForCommonLeadCorrections()) {
                    rr.toggleOneDataAquisition(index, datumIncluded);
                }
            }
        }
    }

    /**
     *
     */
    public void applyMaskingArray() {
        dataActiveMap = MaskingSingleton.getInstance().applyMask(dataActiveMap);//.getMaskingArray().clone();
        for (DataModelInterface rr : rawRatios) {
            rr.applyMaskingArray();
        }
    }

    /**
     *
     * @param isOD
     */
    public void setODforAllRatios(boolean isOD) {
        for (DataModelInterface rr : rawRatios) {
            ((DataModelFitFunctionInterface) rr).setOverDispersionSelected(isOD);
        }
    }

    /**
     *
     * @param index
     */
    public void flipIncludeExcludeOneDataAquisition(int index) {
        toggleOneDataAquisition(index, !dataActiveMap[index]);
    }

    /**
     *
     * @return
     */
    public SortedSet<DataModelInterface> getIncludedIsotopes() {
        SortedSet<DataModelInterface> isotopes = new TreeSet<>();
        for (DataModelInterface rr : rawRatios) {
            isotopes.add(((RawRatioDataModel) rr).getTopIsotope());
            isotopes.add(((RawRatioDataModel) rr).getBotIsotope());
        }

        return isotopes;
    }

    /**
     * @return the rawRatios
     */
    public SortedSet<DataModelInterface> getValidRawRatios() {
        SortedSet<DataModelInterface> validRawRatios = new TreeSet<>();

        for (DataModelInterface rr : rawRatios) {
            if (!((RawRatioDataModel) rr).getTopIsotope().equals(((RawRatioDataModel) rr).getBotIsotope()) //
                    && //
                    !rr.isUsedForCommonLeadCorrections()) {
                validRawRatios.add(rr);
            }
        }

        return validRawRatios;
    }

//    /**
//     * Returns those RawRatioDataModels that have been specified for alpha
//     * display and fractionation correction.
//     *
//     * @return
//     */
//    public SortedSet<DataModelInterface> getValidRawRatioAlphas() {
//        SortedSet<DataModelInterface> validRawRatioAlphas = new TreeSet<>();
//
//        for (DataModelInterface rr : rawRatios) {
//            if (((RawRatioDataModel) rr).isUsedForFractionationCorrections()) {
//                validRawRatioAlphas.add(rr);
//            }
//        }
//
//        return validRawRatioAlphas;
//    }

    /**
     * RawRatioNames enum are in correct display order
     *
     * @return
     */
    public SortedSet<DataModelInterface> getRatiosForFractionFitting() {
        SortedSet<DataModelInterface> ratiosForFractionFitting = new TreeSet<>((DataModelInterface rrdm1, DataModelInterface rrdm2) -> {
            RawRatioNames rmName = rrdm1.getRawRatioModelName();
            RawRatioNames myName = rrdm2.getRawRatioModelName();

            return rmName.compareTo(myName);
        });

        for (DataModelInterface rr : rawRatios) {
            if (((RawRatioDataModel) rr).isUsedForFractionationCorrections() || ((RawRatioDataModel) rr).isUsedForCommonLeadCorrections()) {
                ratiosForFractionFitting.add(rr);
            }
        }

        return ratiosForFractionFitting;
    }

    /**
     *
     * @return
     */
    public boolean confirmHealthyFraction() {

        boolean retVal = true;
        if (!included) {
            retVal = false;
        }

        if (retVal) {
            SortedSet<DataModelInterface> validFractionationRatios = getRatiosForFractionFitting();//getValidRawRatioAlphas();
            Iterator<DataModelInterface> validFractionIterator = validFractionationRatios.iterator();
            while (validFractionIterator.hasNext()) {
                DataModelInterface rr = validFractionIterator.next();
                if (((RawRatioDataModel) rr).getSelectedFitFunction() == null) {
                    retVal = false;
                    break;
                }
                if (rr.isBelowDetection()) {
                    retVal = false;
                    break;
                }
            }
        }

        return retVal;
    }

    private double calculateMeanOfHighestIntensities(double[] intensities) {

        for (int i = 0; i < intensities.length; i++) {
            //set inactive to zero so not used in high end average below
            if (!dataActiveMap[i]) {
                intensities[i] = 0.0;
            }
        }

        Arrays.sort(intensities);

        int countOfHighValues = (int) Math.max(2.0, Math.floor(0.1 * intensities.length));
        double sum = 0.0;
        for (int i = 0; i < countOfHighValues; i++) {
            sum += intensities[intensities.length - 1 - i];
        }

        return sum / countOfHighValues;
    }

    public double calculateMeanOfHighestIntensityU() {

        double[] intensities = ((RawRatioDataModel) getRawRatioDataModelByName(RawRatioNames.r206_238w)).getBotIsotope().getOnPeakCorrectedCountsPerSecondAsRawIntensities();

        return calculateMeanOfHighestIntensities(intensities);
    }

    public double calculateMeanOfHighestIntensityTh() {

        double[] intensities = ((RawRatioDataModel) getRawRatioDataModelByName(RawRatioNames.r208_232w)).getBotIsotope().getOnPeakCorrectedCountsPerSecondAsRawIntensities();

        return calculateMeanOfHighestIntensities(intensities);
    }

    /**
     * @return the standard
     */
    public boolean isStandard() {
        return standard;
    }

    /**
     * @param standard the standard to set
     */
    public void setStandard(boolean standard) {
        this.standard = standard;
    }

    /**
     *
     */
    public void printRawRatios() {
        for (DataModelInterface rr : rawRatios) {
            ((RawRatioDataModel) rr).printData();
        }
    }

    /**
     * @return the fractionID
     */
    public String getFractionID() {
        return fractionID;
    }

    /**
     * @param fractionID the fractionID to set
     */
    public void setFractionID(String fractionID) {
        this.fractionID = fractionID;
    }

    /**
     * @return the peakTimeStamp
     */
    public long getPeakTimeStamp() {
        return peakTimeStamp;
    }

    /**
     * @param timeStamp
     */
    public void setPeakTimeStamp(long timeStamp) {
        this.peakTimeStamp = timeStamp;
    }

    /**
     * @return the dataActiveMap
     */
    public boolean[] getDataActiveMap() {
        return dataActiveMap;
    }

    /**
     * @param dataActiveMap the dataActiveMap to set
     */
    public void setDataActiveMap(boolean[] dataActiveMap) {
        this.dataActiveMap = dataActiveMap;
    }

    /**
     *
     * @param leftShadeCount
     */
    public void shadeDataActiveMapLeft(int leftShadeCount) {
        for (int i = 0; i < leftShadeCount; i++) {
            dataActiveMap[i] = false;
        }
    }

    /**
     * @return the colorMeExcluded
     */
    @Override
    public boolean isColorMeExcluded() {
        return colorMeExcluded;
    }

    /**
     * @param colorMeExcluded the colorMeExcluded to set
     */
    @Override
    public void setColorMeExcluded(boolean colorMeExcluded) {
        this.colorMeExcluded = colorMeExcluded;
    }

    /**
     * @return the showVerticalLineAtThisIndex
     */
    @Override
    public int getShowVerticalLineAtThisIndex() {
        return showVerticalLineAtThisIndex;
    }

    /**
     * @param showVerticalLineAtThisIndex the showVerticalLineAtThisIndex to set
     */
    @Override
    public void setShowVerticalLineAtThisIndex(int showVerticalLineAtThisIndex) {
        this.showVerticalLineAtThisIndex = showVerticalLineAtThisIndex;
    }

    /**
     * @return the included
     */
    public boolean isIncluded() {
        return included;
    }

    /**
     * @param included the included to set
     */
    public void setIncluded(boolean included) {
        this.included = included;
    }

    /**
     * @return the zeroBasedNormalizedTimeStamp
     */
    public long getZeroBasedNormalizedTimeStamp() {
        return zeroBasedNormalizedTimeStamp;
    }

    /**
     * @param zeroBasedTimeStamp
     */
    public void setZeroBasedNormalizedTimeStamp(long zeroBasedTimeStamp) {
        this.zeroBasedNormalizedTimeStamp = zeroBasedTimeStamp;
    }

    /**
     * @return the showLocalYAxis
     */
    public boolean isShowLocalYAxis() {
        return showLocalYAxis;
    }

    /**
     * @param showLocalYAxis the showLocalYAxis to set
     */
    public void setShowLocalYAxis(boolean showLocalYAxis) {
        this.showLocalYAxis = showLocalYAxis;
    }

    /**
     *
     */
    public void toggleShowLocalYAxis() {
        this.showLocalYAxis = !this.showLocalYAxis;
    }

    /**
     *
     */
    public void toggleShowLocalInterceptFitFunctionPanel() {
        this.showLocalInterceptFitPanel = !this.showLocalInterceptFitPanel;
    }

    /**
     * @return the zeroBasedTimeStamp
     */
    public long getZeroBasedTimeStamp() {
        return zeroBasedTimeStamp;
    }

    /**
     * @param zeroBasedTimeStamp the zeroBasedTimeStamp to set
     */
    public void setZeroBasedTimeStamp(long zeroBasedTimeStamp) {
        this.zeroBasedTimeStamp = zeroBasedTimeStamp;
    }

    /**
     * @return the showLocalInterceptFitPanel
     */
    public boolean isShowLocalInterceptFitPanel() {
        return showLocalInterceptFitPanel;
    }

    /**
     * @param showLocalInterceptFitPanel the showLocalInterceptFitPanel to set
     */
    public void setShowLocalInterceptFitPanel(boolean showLocalInterceptFitPanel) {
        this.showLocalInterceptFitPanel = showLocalInterceptFitPanel;
    }

    /**
     *
     * @return
     */
    @Override
    public String toString() {
        return fractionID + " [" + TimeToString.timeStampString(peakTimeStamp) + "]";
    }

    /**
     * @return the showSecondVerticalLineAtThisIndex
     */
    @Override
    public int getShowSecondVerticalLineAtThisIndex() {
        return showSecondVerticalLineAtThisIndex;
    }

    /**
     * @param showSecondVerticalLineAtThisIndex the
     * showSecondVerticalLineAtThisIndex to set
     */
    @Override
    public void setShowSecondVerticalLineAtThisIndex(int showSecondVerticalLineAtThisIndex) {
        this.showSecondVerticalLineAtThisIndex = showSecondVerticalLineAtThisIndex;
    }

    /**
     * @return the selBoxFirstY
     */
    @Override
    public double getSelBoxFirstY() {
        return selBoxFirstY;
    }

    /**
     * @param selBoxFirstY the selBoxFirstY to set
     */
    @Override
    public void setSelBoxFirstY(double selBoxFirstY) {
        this.selBoxFirstY = selBoxFirstY;
    }

    /**
     * @return the selBoxSecondY
     */
    @Override
    public double getSelBoxSecondY() {
        return selBoxSecondY;
    }

    /**
     * @param selBoxSecondY the selBoxSecondY to set
     */
    @Override
    public void setSelBoxSecondY(double selBoxSecondY) {
        this.selBoxSecondY = selBoxSecondY;
    }

    /**
     * @return the selectedForToggleIndexes
     */
    @Override
    public ArrayList<Integer> getSelectedForToggleIndexes() {
        return selectedForToggleIndexes;
    }

    /**
     * @param selectedForToggleIndexes the selectedForToggleIndexes to set
     */
    @Override
    public void setSelectedForToggleIndexes(ArrayList<Integer> selectedForToggleIndexes) {
        this.selectedForToggleIndexes = selectedForToggleIndexes;
    }

    /**
     * @return the rawRatios
     */
    public SortedSet<DataModelInterface> getRawRatios() {
        return rawRatios;
    }

    /**
     * @return the backgroundTimeStamp
     */
    public long getBackgroundTimeStamp() {
        return backgroundTimeStamp;
    }

    /**
     * @param backgroundTimeStamp the backgroundTimeStamp to set
     */
    public void setBackgroundTimeStamp(long backgroundTimeStamp) {
        this.backgroundTimeStamp = backgroundTimeStamp;
    }

    /**
     * @return the uPbFraction
     */
    public FractionI getuPbFraction() {
        return uPbFraction;
    }

    /**
     * @param uPbFraction the uPbFraction to set
     */
    public void setuPbFraction(FractionI uPbFraction) {
        this.uPbFraction = uPbFraction;
    }

    /**
     * @return the commonLeadLossCorrectionScheme
     */
    public AbstractCommonLeadLossCorrectionScheme getCommonLeadLossCorrectionScheme() {
        return commonLeadLossCorrectionScheme;
    }

    /**
     *
     * @return
     */
    public boolean hasCommonLeadLossCorrectionScheme() {
        return !(commonLeadLossCorrectionScheme instanceof CommonLeadLossCorrectionSchemeNONE);
    }

    /**
     * @param commonLeadLossCorrectionScheme the commonLeadLossCorrectionScheme
     * to set
     */
    public void setCommonLeadLossCorrectionScheme(AbstractCommonLeadLossCorrectionScheme commonLeadLossCorrectionScheme) {
        this.commonLeadLossCorrectionScheme = commonLeadLossCorrectionScheme;
    }

    /**
     * @return the initialPbModel
     */
    public AbstractRatiosDataModel getInitialPbModelET() {
        return initialPbModelET;
    }

    /**
     * @param initialPbModelET
     * @param initialPbModel the initialPbModel to set
     */
    public void setInitialPbModelET(AbstractRatiosDataModel initialPbModelET) {
        this.initialPbModelET = initialPbModelET;
    }

    /**
     *
     * @return
     */
    public boolean hasStaceyKramersInitialPbModelET() {
        return this.initialPbModelET instanceof StaceyKramersInitialPbModelET;
    }

    /**
     *
     * @return
     */
    public boolean hasSingleRatioInitialPbModelET() {
        return (this.initialPbModelET instanceof PlaceholderInitialPb76Model);
    }

    /**
     * @return the skEstimatedDate
     */
    public BigDecimal getSkEstimatedDate() {
        if (skEstimatedDate == null) {
            skEstimatedDate = BigDecimal.ZERO;
        }
        return skEstimatedDate;
    }

    /**
     * @param skEstimatedDate the skEstimatedDate to set
     */
    public void setSkEstimatedDate(BigDecimal skEstimatedDate) {
        this.skEstimatedDate = skEstimatedDate;
    }

    /**
     * @return the skOneSigmaVarUnctPct
     */
    public BigDecimal getSkOneSigmaVarUnctPct() {
        if (skOneSigmaVarUnctPct == null) {
            skOneSigmaVarUnctPct = BigDecimal.ZERO;
        }
        return skOneSigmaVarUnctPct;
    }

    /**
     * @param skOneSigmaVarUnctPct the skOneSigmaVarUnctPct to set
     */
    public void setSkOneSigmaVarUnctPct(BigDecimal skOneSigmaVarUnctPct) {
        this.skOneSigmaVarUnctPct = skOneSigmaVarUnctPct;
    }

    /**
     * @return the skRhoVarUnct
     */
    public BigDecimal getSkRhoVarUnct() {
        if (skRhoVarUnct == null) {
            skRhoVarUnct = BigDecimal.ZERO;
        }
        return skRhoVarUnct;
    }

    /**
     * @param skRhoVarUnct the skRhoVarUnct to set
     */
    public void setSkRhoVarUnct(BigDecimal skRhoVarUnct) {
        this.skRhoVarUnct = skRhoVarUnct;
    }

//    /**
//     * @return the initialPbModelR207_206c
//     */
//    public BigDecimal getInitialPbModelR207_206c() {
//        if (initialPbModelR207_206c == null) {
//            initialPbModelR207_206c = BigDecimal.ZERO;
//        }
//        return initialPbModelR207_206c;
//    }
//
//    /**
//     * @param initialPbModelR207_206c the initialPbModelR207_206c to set
//     */
//    public void setInitialPbModelR207_206c(BigDecimal initialPbModelR207_206c) {
//        this.initialPbModelR207_206c = initialPbModelR207_206c;
//    }
//
//    /**
//     * @return the initialPbModelR207_206c_1PctUnct
//     */
//    public BigDecimal getInitialPbModelR207_206c_1PctUnct() {
//        if (initialPbModelR207_206c_1PctUnct == null) {
//            initialPbModelR207_206c_1PctUnct = BigDecimal.ZERO;
//        }
//        return initialPbModelR207_206c_1PctUnct;
//    }
//
//    /**
//     * @param initialPbModelR207_206c_1PctUnct the
//     * initialPbModelR207_206c_1PctUnct to set
//     */
//    public void setInitialPbModelR207_206c_1PctUnct(BigDecimal initialPbModelR207_206c_1PctUnct) {
//        this.initialPbModelR207_206c_1PctUnct = initialPbModelR207_206c_1PctUnct;
//    }
    /**
     * @return the commonLeadCorrectionHighestLevel
     */
    public String getCommonLeadCorrectionHighestLevel() {
        return commonLeadCorrectionHighestLevel;
    }

    /**
     * @param commonLeadCorrectionHighestLevel the
     * commonLeadCorrectionHighestLevel to set
     */
    public void setCommonLeadCorrectionHighestLevel(String commonLeadCorrectionHighestLevel) {
        this.commonLeadCorrectionHighestLevel = commonLeadCorrectionHighestLevel;
    }

//    /**
//     * @return the initialPbModelR206_204c
//     */
//    public BigDecimal getInitialPbModelR206_204c() {
//        return initialPbModelR206_204c;
//    }
//
//    /**
//     * @param initialPbModelR206_204c the initialPbModelR206_204c to set
//     */
//    public void setInitialPbModelR206_204c(BigDecimal initialPbModelR206_204c) {
//        this.initialPbModelR206_204c = initialPbModelR206_204c;
//    }
//
//    /**
//     * @return the initialPbModelR206_204c_1PctUnct
//     */
//    public BigDecimal getInitialPbModelR206_204c_1PctUnct() {
//        return initialPbModelR206_204c_1PctUnct;
//    }
//
//    /**
//     * @param initialPbModelR206_204c_1PctUnct the
//     * initialPbModelR206_204c_1PctUnct to set
//     */
//    public void setInitialPbModelR206_204c_1PctUnct(BigDecimal initialPbModelR206_204c_1PctUnct) {
//        this.initialPbModelR206_204c_1PctUnct = initialPbModelR206_204c_1PctUnct;
//    }
//
//    /**
//     * @return the initialPbModelR207_204c
//     */
//    public BigDecimal getInitialPbModelR207_204c() {
//        return initialPbModelR207_204c;
//    }
//
//    /**
//     * @param initialPbModelR207_204c the initialPbModelR207_204c to set
//     */
//    public void setInitialPbModelR207_204c(BigDecimal initialPbModelR207_204c) {
//        this.initialPbModelR207_204c = initialPbModelR207_204c;
//    }
//
//    /**
//     * @return the initialPbModelR207_204c_1PctUnct
//     */
//    public BigDecimal getInitialPbModelR207_204c_1PctUnct() {
//        return initialPbModelR207_204c_1PctUnct;
//    }
//
//    /**
//     * @param initialPbModelR207_204c_1PctUnct the
//     * initialPbModelR207_204c_1PctUnct to set
//     */
//    public void setInitialPbModelR207_204c_1PctUnct(BigDecimal initialPbModelR207_204c_1PctUnct) {
//        this.initialPbModelR207_204c_1PctUnct = initialPbModelR207_204c_1PctUnct;
//    }
    /**
     * @return the skOneSigmaSysUnctPct
     */
    public BigDecimal getSkOneSigmaSysUnctPct() {
        return skOneSigmaSysUnctPct;
    }

    /**
     * @param skOneSigmaSysUnctPct the skOneSigmaSysUnctPct to set
     */
    public void setSkOneSigmaSysUnctPct(BigDecimal skOneSigmaSysUnctPct) {
        this.skOneSigmaSysUnctPct = skOneSigmaSysUnctPct;
    }

    /**
     * @return the skRhoSysUnct
     */
    public BigDecimal getSkRhoSysUnct() {
        return skRhoSysUnct;
    }

    /**
     * @param skRhoSysUnct the skRhoSysUnct to set
     */
    public void setSkRhoSysUnct(BigDecimal skRhoSysUnct) {
        this.skRhoSysUnct = skRhoSysUnct;
    }

    /**
     * @return the initialPbPlaceHolderModelR206_204c
     */
    public ValueModel getInitialPbPlaceHolderModelR206_204c() {
        return initialPbPlaceHolderModelR206_204c;
    }

    /**
     * @param initialPbPlaceHolderModelR206_204c the
     * initialPbPlaceHolderModelR206_204c to set
     */
    public void setInitialPbPlaceHolderModelR206_204c(ValueModel initialPbPlaceHolderModelR206_204c) {
        this.initialPbPlaceHolderModelR206_204c = initialPbPlaceHolderModelR206_204c;
    }

    /**
     * @return the initialPbPlaceHolderModelR207_204c
     */
    public ValueModel getInitialPbPlaceHolderModelR207_204c() {
        return initialPbPlaceHolderModelR207_204c;
    }

    /**
     * @param initialPbPlaceHolderModelR207_204c the
     * initialPbPlaceHolderModelR207_204c to set
     */
    public void setInitialPbPlaceHolderModelR207_204c(ValueModel initialPbPlaceHolderModelR207_204c) {
        this.initialPbPlaceHolderModelR207_204c = initialPbPlaceHolderModelR207_204c;
    }

    /**
     * @return the initialPbPlaceHolderModelR208_204c
     */
    public ValueModel getInitialPbPlaceHolderModelR208_204c() {
        return initialPbPlaceHolderModelR208_204c;
    }

    /**
     * @param initialPbPlaceHolderModelR208_204c the
     * initialPbPlaceHolderModelR208_204c to set
     */
    public void setInitialPbPlaceHolderModelR208_204c(ValueModel initialPbPlaceHolderModelR208_204c) {
        this.initialPbPlaceHolderModelR208_204c = initialPbPlaceHolderModelR208_204c;
    }

    /**
     * @return the initialPbPlaceHolderVarRhos
     */
    public Map<String, BigDecimal> getInitialPbPlaceHolderVarRhos() {
        return initialPbPlaceHolderVarRhos;
    }

    /**
     *
     * @return
     */
    public Map<String, BigDecimal> copyInitialPbPlaceHolderVarRhos() {
        Map<String, BigDecimal> initialPbPlaceHolderVarRhosCOPY = new HashMap<>();
        Iterator<String> rhosIterator = initialPbPlaceHolderVarRhos.keySet().iterator();
        while (rhosIterator.hasNext()) {
            String key = rhosIterator.next();
            initialPbPlaceHolderVarRhosCOPY.put(key, initialPbPlaceHolderVarRhos.get(key));
        }

        return initialPbPlaceHolderVarRhosCOPY;
    }

    /**
     * @param initialPbPlaceHolderVarRhos the initialPbPlaceHolderVarRhos to set
     */
    public void setInitialPbPlaceHolderVarRhos(Map<String, BigDecimal> initialPbPlaceHolderVarRhos) {
        this.initialPbPlaceHolderVarRhos = initialPbPlaceHolderVarRhos;
    }

    /**
     * @return the initialPbPlaceHolderSysRhos
     */
    public Map<String, BigDecimal> getInitialPbPlaceHolderSysRhos() {
        return initialPbPlaceHolderSysRhos;
    }

    /**
     *
     * @return
     */
    public Map<String, BigDecimal> copyInitialPbPlaceHolderSysRhos() {
        Map<String, BigDecimal> initialPbPlaceHolderSysRhosCOPY = new HashMap<>();
        Iterator<String> rhosIterator = initialPbPlaceHolderSysRhos.keySet().iterator();
        while (rhosIterator.hasNext()) {
            String key = rhosIterator.next();
            initialPbPlaceHolderSysRhos.put(key, initialPbPlaceHolderSysRhos.get(key));
        }

        return initialPbPlaceHolderSysRhos;
    }

    /**
     * @param initialPbPlaceHolderSysRhos the initialPbPlaceHolderSysRhos to set
     */
    public void setInitialPbPlaceHolderSysRhos(Map<String, BigDecimal> initialPbPlaceHolderSysRhos) {
        this.initialPbPlaceHolderSysRhos = initialPbPlaceHolderSysRhos;
    }

    /**
     * @return the initialPbSchemeA_r207_206c
     */
    public ValueModel getInitialPbSchemeA_r207_206c() {
        return initialPbSchemeA_r207_206c;
    }

    /**
     * @param initialPbSchemeA_r207_206c the initialPbSchemeA_r207_206c to set
     */
    public void setInitialPbSchemeA_r207_206c(ValueModel initialPbSchemeA_r207_206c) {
        this.initialPbSchemeA_r207_206c = initialPbSchemeA_r207_206c;
    }

    /**
     * @return the initialPbPlaceHolderModelR207_206c
     */
    public ValueModel getInitialPbPlaceHolderModelR207_206c() {
        return initialPbPlaceHolderModelR207_206c;
    }

    /**
     * @param initialPbPlaceHolderModelR207_206c the
     * initialPbPlaceHolderModelR207_206c to set
     */
    public void setInitialPbPlaceHolderModelR207_206c(ValueModel initialPbPlaceHolderModelR207_206c) {
        this.initialPbPlaceHolderModelR207_206c = initialPbPlaceHolderModelR207_206c;
    }

    /**
     * @return the upperPhi_r206_207
     */
    public double getUpperPhi_r206_207() {
        return upperPhi_r206_207;
    }

    /**
     * @param upperPhi_r206_207 the upperPhi_r206_207 to set
     */
    public void setUpperPhi_r206_207(double upperPhi_r206_207) {
        this.upperPhi_r206_207 = upperPhi_r206_207;
    }

    /**
     * @return the initialPbSchemeB_R206_204c
     */
    public ValueModel getInitialPbSchemeB_R206_204c() {
        return initialPbSchemeB_R206_204c;
    }

    /**
     * @param initialPbSchemeB_R206_204c the initialPbSchemeB_R206_204c to set
     */
    public void setInitialPbSchemeB_R206_204c(ValueModel initialPbSchemeB_R206_204c) {
        this.initialPbSchemeB_R206_204c = initialPbSchemeB_R206_204c;
    }

    /**
     * @return the initialPbSchemeB_R207_204c
     */
    public ValueModel getInitialPbSchemeB_R207_204c() {
        return initialPbSchemeB_R207_204c;
    }

    /**
     * @param initialPbSchemeB_R207_204c the initialPbSchemeB_R207_204c to set
     */
    public void setInitialPbSchemeB_R207_204c(ValueModel initialPbSchemeB_R207_204c) {
        this.initialPbSchemeB_R207_204c = initialPbSchemeB_R207_204c;
    }

    /**
     * @return the initialPbSchemeB_R208_204c
     */
    public ValueModel getInitialPbSchemeB_R208_204c() {
        return initialPbSchemeB_R208_204c;
    }

    /**
     * @param initialPbSchemeB_R208_204c the initialPbSchemeB_R208_204c to set
     */
    public void setInitialPbSchemeB_R208_204c(ValueModel initialPbSchemeB_R208_204c) {
        this.initialPbSchemeB_R208_204c = initialPbSchemeB_R208_204c;
    }

    /**
     * @param rawRatios the rawRatios to set
     */
    public void setRawRatios(SortedSet<DataModelInterface> rawRatios) {
        this.rawRatios = rawRatios;
    }

    public void reProcessToRejectNegativeRatios() {
        // may 2015 per Noah, turn off all negative values - neg in a ratio turns off all in that position for fraction
        for (DataModelInterface rr : rawRatios) {
            double[] ratios = ((RawRatioDataModel) rr).getRatios();
            for (int i = 0; i < ratios.length; i++) {
                if (ratios[i] < 0.0) {
                    toggleOneDataAquisition(i, false);
                }
            }
        }
    }

    public void postProcessCommonLeadCorrectionRatios() {
//        System.out.println("Post process for Pbc on fraction  " + fractionID + "***************************************************\n");
        for (DataModelInterface rr : rawRatios) {
            boolean rejectedAPoint = false;
            // select only those with pb204 in denom
            if (rr.isUsedForCommonLeadCorrections() && !rr.isBelowDetection() && !((RawRatioDataModel) rr).getBotIsotope().isForceMeanForCommonLeadRatios()) {
                double[] ratios = ((RawRatioDataModel) rr).getRatios();

                for (int i = 0; i < ratios.length; i++) {
                    if (ratios[i] <= 0.0) {
                        ((RawRatioDataModel) rr).getDataActiveMap()[i] = false;
                        rejectedAPoint = true;
                    }
                }
                if (rejectedAPoint) {
                    rr.generateSetOfFitFunctions(true, false);
                }
            } else {
//                System.out.println("NONE");
            }
        }
        currentlyFitted = true;
    }

    /**
     * @return the sampleR238_235s
     */
    public ValueModel getSampleR238_235s() {
        return sampleR238_235s;
    }

    /**
     * @param sampleR238_235s the sampleR238_235s to set
     */
    public void setSampleR238_235s(ValueModel sampleR238_235s) {
        this.sampleR238_235s = sampleR238_235s;
    }

    /**
     * @return the radDateForSKSynch
     */
    public RadDatesForPbCorrSynchEnum getRadDateForSKSynch() {
        return radDateForSKSynch;
    }

    /**
     * @param radDateForSKSynch the radDateForSKSynch to set
     */
    public void setRadDateForSKSynch(RadDatesForPbCorrSynchEnum radDateForSKSynch) {
        this.radDateForSKSynch = radDateForSKSynch;
    }

    /**
     * @return the currentlyFitted
     */
    public boolean isCurrentlyFitted() {
        return currentlyFitted;
    }

    /**
     * @param currentlyFitted the currentlyFitted to set
     */
    public void setCurrentlyFitted(boolean currentlyFitted) {
        this.currentlyFitted = currentlyFitted;
    }
}
