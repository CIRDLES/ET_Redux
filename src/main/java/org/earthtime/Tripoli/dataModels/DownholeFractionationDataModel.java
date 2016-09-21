/*
 * DownholeFractionationDataModel.java
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
package org.earthtime.Tripoli.dataModels;

import Jama.Matrix;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import org.earthtime.Tripoli.fitFunctions.AbstractFunctionOfX;
import org.earthtime.Tripoli.fitFunctions.LevenbergMarquardGeneralSolverWithCovS;
import org.earthtime.Tripoli.fitFunctions.LevenbergMarquardGeneralSolverWithCovS.AbstractOverDispersionLMAlgorithm;
import org.earthtime.Tripoli.fitFunctions.MeanFitFunction;
import org.earthtime.Tripoli.fitFunctions.SmoothingSplineWithCov;
import org.earthtime.Tripoli.fractions.TripoliFraction;
import org.earthtime.UPb_Redux.fractions.FractionsFilterInterface;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.dataDictionaries.FitFunctionTypeEnum;
import org.earthtime.dataDictionaries.FractionSelectionTypeEnum;
import org.earthtime.dataDictionaries.IncludedTypeEnum;
import org.earthtime.dataDictionaries.RawRatioNames;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;

/**
 *
 * @author James F. Bowring
 */
public class DownholeFractionationDataModel implements Serializable, DataModelFitFunctionInterface {

    // Class variables
    private static final long serialVersionUID = 1386388565877245817L;
    /**
     *
     */
    //
    private SortedSet<TripoliFraction> tripoliFractions;
    private final RawRatioNames rawRatioName;
    private double[] weightedMeanIntegrations;
    private double[] onPeakAcquireTimesBySecond;
    private final double[] normalizedAquireTimes;
    private double[] fittedStandards;
    private double[] fittedStandardsResiduals;
    private double[] fittedStandardsBeta;
    private MaskingSingleton maskingSingleton;
    private ValueModel standardValueModel;

    /**
     *
     */
    protected Map<String, AbstractFunctionOfX> downholeStandardsFitFunctionsNoOD;

    /**
     *
     */
    protected Map<String, AbstractFunctionOfX> downholeStandardsFitFunctionsWithOD;

    /**
     *
     */
    protected boolean overDispersionSelected;
    private FitFunctionTypeEnum selectedFitFunctionType;
    private boolean calculatedInitialFitFunctions;
    // mar 2013
    private boolean[] activeDataMap;
    private double[] activeXvalues;
    private Matrix SwtdMeanStdIntegrations;
    //feb 2013 ... refactor by promoting common attributes
    private Matrix Jgammag;
    ArrayList<Double> tList;
    double[] h;
    Matrix Q;
    Matrix R;
    private Map<FitFunctionTypeEnum, Matrix> matrixJfMapAquisitionsAquisitions;

    /**
     *
     * @param tripoliFractions the value of tripoliFractions
     * @param rawRatioName
     * @param primaryMineralStandard the value of primaryMineralStandard
     * @param weightedMeanIntegrations
     * @param onPeakAcquireTimesBySecond
     * @param normalizedAquireTimes
     * @param maskingSingleton
     */
    public DownholeFractionationDataModel(//
            SortedSet<TripoliFraction> tripoliFractions, //
            RawRatioNames rawRatioName, AbstractRatiosDataModel primaryMineralStandard, //
            double[] weightedMeanIntegrations, double[] onPeakAcquireTimesBySecond, double[] normalizedAquireTimes, MaskingSingleton maskingSingleton) {

        this.tripoliFractions = tripoliFractions;
        this.rawRatioName = rawRatioName;
        this.weightedMeanIntegrations = weightedMeanIntegrations;
        this.onPeakAcquireTimesBySecond = onPeakAcquireTimesBySecond.clone();
        this.normalizedAquireTimes = normalizedAquireTimes.clone();

        this.fittedStandards = new double[weightedMeanIntegrations.length];
        this.fittedStandardsResiduals = new double[weightedMeanIntegrations.length];
        this.fittedStandardsBeta = new double[weightedMeanIntegrations.length];
        this.maskingSingleton = maskingSingleton;

        this.downholeStandardsFitFunctionsNoOD = new TreeMap<>();
        this.downholeStandardsFitFunctionsWithOD = new TreeMap<>();
        this.overDispersionSelected = true;

        this.selectedFitFunctionType = FitFunctionTypeEnum.EXPONENTIAL;

        this.calculatedInitialFitFunctions = false;
        this.overDispersionSelected = true;

        this.standardValueModel = primaryMineralStandard.getDatumByName(rawRatioName.getName().replace("w", "r"));

        this.SwtdMeanStdIntegrations = new Matrix(0, 0);

    }

    /**
     *
     * @param fractionIterator
     */
    public void calculateWeightedMeanOfStandards() {

        // march 2013
        // the problem is that we don't yet have math to deal with
        // un-alighed standard on peak data sets because of the matrix math used
        // so we proceed by un-rejecteting non-mask aquisitions
        boolean[] dataCommonActiveMap = MaskingSingleton.getInstance().getMaskingArray();
        int countOfActiveData = 0;
        for (int i = 0; i < dataCommonActiveMap.length; i++) {
            if (dataCommonActiveMap[i]) {
                countOfActiveData++;
            }
        }

        activeDataMap = new boolean[countOfActiveData];
        weightedMeanIntegrations = new double[countOfActiveData];
        fittedStandards = new double[countOfActiveData];
        setFittedStandardsBeta(new double[countOfActiveData]);
        fittedStandardsResiduals = new double[countOfActiveData];
        activeXvalues = new double[countOfActiveData];

        int index = 0;
        for (int i = 0; i < dataCommonActiveMap.length; i++) {
            if (dataCommonActiveMap[i]) {
                activeDataMap[index] = true;
                weightedMeanIntegrations[index] = 0.0;
                fittedStandards[index] = 0.0;
                getFittedStandardsBeta()[index] = 0.0;
                fittedStandardsResiduals[index] = 0.0;
                activeXvalues[index] = normalizedAquireTimes[i];

                index++;
            }
        }

        // section 7b Calculate offsets and uncertainties for downhole per iolite
        Matrix sumInvSlogRatioX_Y = new Matrix(countOfActiveData, countOfActiveData);
        Matrix sumInvSlogRatioX_YTimeslr = new Matrix(countOfActiveData, 1);

        // assume fraction iterator is correct length as it was set on included fractions
        Iterator<TripoliFraction> fractionIterator
                =//
                FractionsFilterInterface.getTripoliFractionsFiltered(//
                        tripoliFractions, FractionSelectionTypeEnum.STANDARD, IncludedTypeEnum.INCLUDED).iterator();

        while (fractionIterator.hasNext()) {
            TripoliFraction tf = fractionIterator.next();

            RawRatioDataModel rawRatio
                    = ((RawRatioDataModel) tf.getRawRatioDataModelByName(rawRatioName));

            rawRatio.calculateSlogRatioX_Y(dataCommonActiveMap);

            Matrix SlogRatioX_Y_copy = rawRatio.getSlogRatioX_Y(false).copy();

            // get active logratios from standard with any zero entries replaced with interposlated values
            Matrix logRatiosVector = new Matrix(rawRatio.getActiveLogRatios(countOfActiveData, dataCommonActiveMap), countOfActiveData);

            // Sept 2016 to support matrix math for downhole
            // infill zeroes in logRatiosVector due to rejected points with interpolations 
            // per Noah and multiply corresponding SlogRatioXY diagonal elements by 100
            int lastGoodIndex = -1;
            int nextGoodIndex = -1;
            for (int i = 0; i < logRatiosVector.getRowDimension(); i++) {
                if ((lastGoodIndex < 0) && (logRatiosVector.get(i, 0) == 0)) {
                    lastGoodIndex = i - 1;
                }
                if ((lastGoodIndex >= 0) && (logRatiosVector.get(i, 0) > 0)) {
                    nextGoodIndex = i;
                    double average = (logRatiosVector.get(lastGoodIndex, 0) + logRatiosVector.get(nextGoodIndex, 0)) / 2.0;
                    for (int j = lastGoodIndex + 1; j < nextGoodIndex; j++) {
                        logRatiosVector.set(j, 0, average);
                        SlogRatioX_Y_copy.set(j, j, SlogRatioX_Y_copy.get(j, j) * 100.0);
                    }
                    lastGoodIndex = -1;
                    nextGoodIndex = -1;
                }
            }

            // sum of the inverses of all of the Slr_X_Y covariance matrices
            sumInvSlogRatioX_Y.plusEquals(SlogRatioX_Y_copy.inverse());

            // column vector length count of aquisitions
            sumInvSlogRatioX_YTimeslr.plusEquals(SlogRatioX_Y_copy.solve(logRatiosVector));

        }

        // column vector for THICK BLACK LINE
        Matrix wtdMeanStdIntegrations = sumInvSlogRatioX_Y.solve(sumInvSlogRatioX_YTimeslr);

        // square matrix
        SwtdMeanStdIntegrations = sumInvSlogRatioX_Y.inverse();

        // populate arrays for plotting and fitting of thick RED LINE from weightedMeanIntegrations
        for (int i = 0; i < weightedMeanIntegrations.length; i++) {
            weightedMeanIntegrations[i] = wtdMeanStdIntegrations.get(i, 0);
        }
    }

    private boolean generateMEANfitFunctionUsingLM() {

        boolean retVal;

        AbstractOverDispersionLMAlgorithm algorithmForMEAN//
                = LevenbergMarquardGeneralSolverWithCovS.getInstance()//
                .getSelectedLMAlgorithm(//
                        FitFunctionTypeEnum.MEAN,//
                        activeDataMap, //
                        activeXvalues, //
                        weightedMeanIntegrations,//
                        SwtdMeanStdIntegrations, //
                        false);

        // algorithmForMEAN contains both the non OD and OD versions
        AbstractFunctionOfX fOfX_MEAN = algorithmForMEAN.getInitialFofX();

        if ((fOfX_MEAN != null) && fOfX_MEAN.verifyPositiveVariances()) {
            if (downholeStandardsFitFunctionsNoOD.containsKey(fOfX_MEAN.getShortNameString())) {
                AbstractFunctionOfX fOfXexist = downholeStandardsFitFunctionsNoOD.get(fOfX_MEAN.getShortNameString());
                fOfXexist.copyValuesFrom(fOfX_MEAN);
            } else {
                downholeStandardsFitFunctionsNoOD.put(fOfX_MEAN.getShortNameString(), fOfX_MEAN);
            }

            AbstractFunctionOfX fOfX_MEAN_OD = algorithmForMEAN.getFinalFofX();

            if ((fOfX_MEAN_OD != null) && fOfX_MEAN_OD.verifyPositiveVariances()) {
                if (downholeStandardsFitFunctionsWithOD.containsKey(fOfX_MEAN_OD.getShortNameString())) {
                    AbstractFunctionOfX fOfXexist = downholeStandardsFitFunctionsWithOD.get(fOfX_MEAN_OD.getShortNameString());
                    fOfXexist.copyValuesFrom(fOfX_MEAN_OD);
                } else {
                    downholeStandardsFitFunctionsWithOD.put(fOfX_MEAN_OD.getShortNameString(), fOfX_MEAN_OD);
                }
            } else {
                downholeStandardsFitFunctionsWithOD.put(fOfX_MEAN.getShortNameString(), fOfX_MEAN);
            }
            retVal = true;

        } else {
            // to handle really bad data sets, for which LM wont work, do good old fashioned mean
            fOfX_MEAN = MeanFitFunction.getInstance()//
                    .getFunctionOfX(//
                            activeDataMap, //
                            activeXvalues, //
                            weightedMeanIntegrations,//
                            SwtdMeanStdIntegrations, //
                            false);

            downholeStandardsFitFunctionsNoOD.put(FitFunctionTypeEnum.MEAN.getName(), fOfX_MEAN);
            downholeStandardsFitFunctionsWithOD.put(FitFunctionTypeEnum.MEAN.getName(), fOfX_MEAN);

            //selectedFitFunctionType = FitFunctionTypeEnum.MEAN;
            retVal = false;
        }
        selectedFitFunctionType = FitFunctionTypeEnum.MEAN;

        return retVal;
    }

    private void generateLINEfitFunctionUsingLM() {

        AbstractOverDispersionLMAlgorithm algorithmForLINE = LevenbergMarquardGeneralSolverWithCovS.getInstance().getSelectedLMAlgorithm(//
                FitFunctionTypeEnum.LINE,//
                activeDataMap, //
                activeXvalues, //
                weightedMeanIntegrations,//
                SwtdMeanStdIntegrations, //
                false);

        // algorithmForMEAN contains both the non OD and OD versions
        AbstractFunctionOfX fOfX_LINE = algorithmForLINE.getInitialFofX();

        if ((fOfX_LINE != null) && fOfX_LINE.verifyPositiveVariances()) {
            if (downholeStandardsFitFunctionsNoOD.containsKey(fOfX_LINE.getShortNameString())) {
                AbstractFunctionOfX fOfXexist = downholeStandardsFitFunctionsNoOD.get(fOfX_LINE.getShortNameString());
                fOfXexist.copyValuesFrom(fOfX_LINE);
            } else {
                downholeStandardsFitFunctionsNoOD.put(fOfX_LINE.getShortNameString(), fOfX_LINE);
            }

            AbstractFunctionOfX fOfX_LINE_OD = algorithmForLINE.getFinalFofX();

            if (fOfX_LINE_OD != null) {
                if (downholeStandardsFitFunctionsWithOD.containsKey(fOfX_LINE_OD.getShortNameString())) {
                    AbstractFunctionOfX fOfXexist = downholeStandardsFitFunctionsWithOD.get(fOfX_LINE_OD.getShortNameString());
                    fOfXexist.copyValuesFrom(fOfX_LINE_OD);
                } else {
                    downholeStandardsFitFunctionsWithOD.put(fOfX_LINE_OD.getShortNameString(), fOfX_LINE_OD);
                }
            } else {
                downholeStandardsFitFunctionsWithOD.put(fOfX_LINE.getShortNameString(), fOfX_LINE);
            }

            selectedFitFunctionType = FitFunctionTypeEnum.LINE;
        } else {
            downholeStandardsFitFunctionsNoOD.remove(FitFunctionTypeEnum.LINE.getName());
            downholeStandardsFitFunctionsWithOD.remove(FitFunctionTypeEnum.LINE.getName());
            selectedFitFunctionType = FitFunctionTypeEnum.MEAN;
        }
    }

    private void generateEXPONENTIALfitFunctionUsingLM() {
        AbstractOverDispersionLMAlgorithm algorithmForEXPFAST = LevenbergMarquardGeneralSolverWithCovS.getInstance().getSelectedLMAlgorithm(//
                FitFunctionTypeEnum.EXPFAST,//
                activeDataMap, //
                activeXvalues, //
                weightedMeanIntegrations,//
                SwtdMeanStdIntegrations, //
                false);
        AbstractOverDispersionLMAlgorithm algorithmForEXPMAT;

        AbstractFunctionOfX expFastFunc = algorithmForEXPFAST.getInitialFofX();
        if (expFastFunc != null) {
            algorithmForEXPMAT = LevenbergMarquardGeneralSolverWithCovS.getInstance().getSelectedLMAlgorithmUsingIntialFofX(//
                    FitFunctionTypeEnum.EXPMAT,//
                    activeDataMap, //
                    activeXvalues, //
                    weightedMeanIntegrations,//
                    SwtdMeanStdIntegrations, //
                    false,//
                    expFastFunc);

            AbstractFunctionOfX fOfX_EXPMAT = algorithmForEXPMAT.getFinalFofX();

            if ((fOfX_EXPMAT != null) && (fOfX_EXPMAT.verifyPositiveVariances())) {

                if (downholeStandardsFitFunctionsNoOD.containsKey(fOfX_EXPMAT.getShortNameString())) {
                    AbstractFunctionOfX fOfXexist = downholeStandardsFitFunctionsNoOD.get(fOfX_EXPMAT.getShortNameString());
                    fOfXexist.copyValuesFrom(fOfX_EXPMAT);
                } else {
                    downholeStandardsFitFunctionsNoOD.put(fOfX_EXPMAT.getShortNameString(), fOfX_EXPMAT);
                }

                if (fOfX_EXPMAT.getMSWD() >= 1.0) {
                    AbstractOverDispersionLMAlgorithm algorithmForEXPOD = LevenbergMarquardGeneralSolverWithCovS.getInstance().getSelectedLMAlgorithmUsingIntialFofX(//
                            FitFunctionTypeEnum.EXPONENTIAL,//
                            activeDataMap, //
                            activeXvalues, //
                            weightedMeanIntegrations,//
                            SwtdMeanStdIntegrations, //
                            false,//
                            expFastFunc);

                    AbstractFunctionOfX fOfX_EXPOD = algorithmForEXPOD.getFinalFofX();
                    if ((fOfX_EXPOD != null) && (fOfX_EXPOD.verifyPositiveVariances())) {

                        if (downholeStandardsFitFunctionsWithOD.containsKey(fOfX_EXPOD.getShortNameString())) {
                            AbstractFunctionOfX fOfXexist = downholeStandardsFitFunctionsWithOD.get(fOfX_EXPOD.getShortNameString());
                            fOfXexist.copyValuesFrom(fOfX_EXPOD);
                        } else {
                            downholeStandardsFitFunctionsWithOD.put(fOfX_EXPOD.getShortNameString(), fOfX_EXPOD);
                        }
                    }

                } else {
                    downholeStandardsFitFunctionsWithOD.put(fOfX_EXPMAT.getShortNameString(), fOfX_EXPMAT);
                }
                selectedFitFunctionType = FitFunctionTypeEnum.LINE;// default = line EXPONENTIAL;
            } else {
                downholeStandardsFitFunctionsNoOD.remove(FitFunctionTypeEnum.EXPONENTIAL.getName());//was expmat??
                downholeStandardsFitFunctionsWithOD.remove(FitFunctionTypeEnum.EXPONENTIAL.getName());
                selectedFitFunctionType = FitFunctionTypeEnum.LINE;
            }
        } else {
            downholeStandardsFitFunctionsNoOD.remove(FitFunctionTypeEnum.EXPONENTIAL.getName());//was expmat ??
            downholeStandardsFitFunctionsWithOD.remove(FitFunctionTypeEnum.EXPONENTIAL.getName());
            selectedFitFunctionType = FitFunctionTypeEnum.LINE;
        }
    }

    /**
     *
     * @param mySessionIncludedStandardsCovariance the value of
     * mySessionIncludedStandardsCovariance
     */
    private void generateSPLINEfitFunction(Matrix mySessionIncludedStandardsCovariance) {

        AbstractFunctionOfX fOfX_SMOOTHING_SPLINE = SmoothingSplineWithCov.getInstance().getFunctionOfX(//
                activeDataMap, //
                activeXvalues, //
                weightedMeanIntegrations,//
                SwtdMeanStdIntegrations, //
                true);

        if ((fOfX_SMOOTHING_SPLINE != null) && fOfX_SMOOTHING_SPLINE.verifyPositiveVariances()) {
            String nameOfFitFunctionReturned = fOfX_SMOOTHING_SPLINE.getShortNameString();
            if (nameOfFitFunctionReturned.compareToIgnoreCase(FitFunctionTypeEnum.SMOOTHING_SPLINE.getName()) != 0) {
                downholeStandardsFitFunctionsNoOD.remove(FitFunctionTypeEnum.SMOOTHING_SPLINE.getName());
                downholeStandardsFitFunctionsWithOD.remove(FitFunctionTypeEnum.SMOOTHING_SPLINE.getName());
            } else {
                if (downholeStandardsFitFunctionsNoOD.containsKey(nameOfFitFunctionReturned)) {
                    AbstractFunctionOfX fOfXexist = downholeStandardsFitFunctionsNoOD.get(nameOfFitFunctionReturned);
                    fOfXexist.copyValuesFrom(fOfX_SMOOTHING_SPLINE);
                } else {
                    downholeStandardsFitFunctionsNoOD.put(nameOfFitFunctionReturned, fOfX_SMOOTHING_SPLINE);
                }

                if (downholeStandardsFitFunctionsWithOD.containsKey(nameOfFitFunctionReturned)) {
                    AbstractFunctionOfX fOfXexist = downholeStandardsFitFunctionsWithOD.get(nameOfFitFunctionReturned);
                    AbstractFunctionOfX fOfXexistReplace = generateSplineWithSpecificOD(fOfXexist.getOverDispersion());
                    downholeStandardsFitFunctionsWithOD.put(nameOfFitFunctionReturned, fOfXexistReplace);
                } else {
                    // copy in the non od version 
                    SmoothingSplineWithCov splineInstance = SmoothingSplineWithCov.getInstance();
                    AbstractFunctionOfX splinePlaceHolderWithOD = splineInstance.getInstanceOfFitFunction();
                    splinePlaceHolderWithOD.copyValuesFrom(fOfX_SMOOTHING_SPLINE);
                    splinePlaceHolderWithOD.setOverDispersionSelected(true);
                    downholeStandardsFitFunctionsWithOD.put(nameOfFitFunctionReturned, splinePlaceHolderWithOD);
                }
            }
        } else {
            downholeStandardsFitFunctionsNoOD.remove(FitFunctionTypeEnum.SMOOTHING_SPLINE.getName());
            downholeStandardsFitFunctionsWithOD.remove(FitFunctionTypeEnum.SMOOTHING_SPLINE.getName());
            selectedFitFunctionType = FitFunctionTypeEnum.LINE; // exp prob doesnt fit here
        }
    }

    private AbstractFunctionOfX generateSplineWithSpecificOD(double overDispersion) {
        int rowColCount = SwtdMeanStdIntegrations.getRowDimension();

        Matrix sessionIncludedStandardsCovarianceCopyWithOD = SwtdMeanStdIntegrations.plus(Matrix.identity(rowColCount, rowColCount).times(overDispersion));

        AbstractFunctionOfX fOfX_SMOOTHING_SPLINE_WITH_OD = SmoothingSplineWithCov.getInstance().getFunctionOfX(//
                activeDataMap, //
                activeXvalues, //
                weightedMeanIntegrations,//
                sessionIncludedStandardsCovarianceCopyWithOD, true);

        fOfX_SMOOTHING_SPLINE_WITH_OD.setOverDispersion(overDispersion);
        fOfX_SMOOTHING_SPLINE_WITH_OD.setOverDispersionSelected(true);

        return fOfX_SMOOTHING_SPLINE_WITH_OD;
    }

    /**
     *
     * @param startValue
     * @param increment
     * @param stopValue
     * @return
     */
    public SortedMap<Double, AbstractFunctionOfX> generateSetOfSplineWithODfitFunctions(double startValue, double increment, double stopValue) {

        SortedMap<Double, AbstractFunctionOfX> sessionOfStandardsSplinesWithOD = new TreeMap<>();

        for (double i = startValue; i < stopValue; i += increment) {
            AbstractFunctionOfX fOfX_SMOOTHING_SPLINE = generateSplineWithSpecificOD(Math.pow(i, 2));

            if (!Double.isFinite(fOfX_SMOOTHING_SPLINE.getLogRoughness())) {
                try {
                    fOfX_SMOOTHING_SPLINE = sessionOfStandardsSplinesWithOD.get(sessionOfStandardsSplinesWithOD.lastKey());
                    AbstractFunctionOfX fOfX_SMOOTHING_SPLINE_EXIST = downholeStandardsFitFunctionsWithOD.get(fOfX_SMOOTHING_SPLINE.getShortNameString());
                    if (fOfX_SMOOTHING_SPLINE_EXIST != null) {
                        fOfX_SMOOTHING_SPLINE_EXIST.copyValuesFrom(fOfX_SMOOTHING_SPLINE);
                    } else {
                        downholeStandardsFitFunctionsWithOD.put(fOfX_SMOOTHING_SPLINE.getShortNameString(), fOfX_SMOOTHING_SPLINE);
                    }
                } catch (RuntimeException e) {
                    throw e;
                } catch (Exception e) {
                }

                break;
            } else {
                sessionOfStandardsSplinesWithOD.put(Math.sqrt(fOfX_SMOOTHING_SPLINE.getOverDispersion()), fOfX_SMOOTHING_SPLINE);
            }
        }

        return sessionOfStandardsSplinesWithOD;
    }

    /**
     *
     *
     * @param propagateUncertainties the value of propagateUncertainties
     * @param doApplyMaskingArray the value of doApplyMaskingArray
     */
    @Override
    public void generateSetOfFitFunctions(boolean propagateUncertainties, boolean doApplyMaskingArray, boolean inLiveMode) {

        if (generateMEANfitFunctionUsingLM()) {
            try {
                generateLINEfitFunctionUsingLM();
                generateEXPONENTIALfitFunctionUsingLM();
            } catch (Exception e) {
            }

            // turned off for now
//            try {
//                generateEXPONENTIALfitFunctionUsingLM();
//            } catch (Exception e) {
//            }
////            try {
////                generateSPLINEfitFunction(SwtdMeanStdIntegrations);
////            } catch (Exception e) {
////            }
        }
    }

    /**
     *
     * @return
     */
    public double[] getFitFunctionLogValues() {
        AbstractFunctionOfX fitFunc = getSelectedFitFunction();

        for (int i = 0; i < fittedStandards.length; i++) {
            try {
                fittedStandards[i] = fitFunc.f(activeXvalues[i]);
            } catch (Exception e) {
                fittedStandards[i] = 0.0;
            }

            fittedStandardsResiduals[i] = weightedMeanIntegrations[i] - fittedStandards[i];
        }
        return fittedStandards;
    }

    public double[] getFitFunctionBetaValues() {
        AbstractFunctionOfX fitFunc = getSelectedFitFunction();

        for (int i = 0; i < fittedStandardsBeta.length; i++) {
            try {
                fittedStandardsBeta[i]
                        = //
                        Math.log(standardValueModel.getValue().doubleValue()) //
                        - fitFunc.f(activeXvalues[i]);
//            } catch (RuntimeException e) {
//                throw e;
            } catch (Exception e) {
                // no fit function ... need better treatment
                fittedStandardsBeta[i] = Math.log(standardValueModel.getValue().doubleValue());
            }
        }
        return fittedStandardsBeta;
    }

    /**
     *
     * @param fractionIterator
     * @param rawRatioName
     * @param downHoleFitFunction
     */
    public void calculateWeightedMeanForEachStandard(RawRatioNames rawRatioName, AbstractFunctionOfX downHoleFitFunction) {

        prepareMatrixJfMapAquisitionsAquisitions();

        Matrix matrixJfStandardsStandards = matrixJfMapAquisitionsAquisitions.get(downHoleFitFunction.getShortName());
        // this call to interpolated variances is needed to calculate matrixSf = need to refactor
        // stdVariances is diagonal of matrixSf
        double[] stdVariances = downHoleFitFunction.calculateInterpolatedVariances(matrixJfStandardsStandards, activeXvalues);

        Iterator<TripoliFraction> fractionIterator
                =//
                FractionsFilterInterface.getTripoliFractionsFiltered(tripoliFractions, FractionSelectionTypeEnum.STANDARD, IncludedTypeEnum.INCLUDED).iterator();

        while (fractionIterator.hasNext()) {
            TripoliFraction tf = fractionIterator.next();

            DataModelInterface rawRatio = tf.getRawRatioDataModelByName(rawRatioName);

            // For the fit, calculate the value of the fit at each of the standards and the matrix Sf, 
            // for each standard, this also calculates 
            //      logDifferencesFromWeightedMean
            //      meanOfResidualsFromFittedFractionation = fOfX_MEAN_OD.getA();
            //      stdErrOfmeanOfResidualsFromFittedFractionation = fOfX_MEAN_OD.getStdErrOfA();
////            double [] lr = ((RawRatioDataModel) rawRatio).getLogRatios();
////            for (int i = 0; i < lr.length; i ++){
////                System.out.println(lr[i] + ", ");
////            }
            ((RawRatioDataModel) rawRatio).setDownHoleFitFunction(downHoleFitFunction);
            ((RawRatioDataModel) rawRatio).generateFitFunctionsForDownhole();
        }
    }

    /**
     *
     */
    public void prepareSessionWithAquisitionTimes() {
        // mar 2013

        //**********  ALL OF THIS IS CURRENTLY DONE INSIDE SPLINE FIT BUT COULD BE DONE OUTSIDE ******
        // build and calculate t and h
        tList = new ArrayList<>();

        for (int i = 0; i < activeXvalues.length; i++) {
            tList.add(activeXvalues[i]);
        }

        int countOfActiveAquisitions = activeXvalues.length;
        // time deltas 
        h = new double[countOfActiveAquisitions - 1];

        for (int i = 0; i < (countOfActiveAquisitions - 1); i++) {
            h[i] = activeXvalues[i + 1] - activeXvalues[i];
        }
        Q = new Matrix(countOfActiveAquisitions, countOfActiveAquisitions - 1);
        R = new Matrix(countOfActiveAquisitions - 1, countOfActiveAquisitions - 1);

        for (int j = 2; j < countOfActiveAquisitions - 1; j++) {
            int i = j - 1;

            Q.set(i - 1, i, 1.0 / h[i - 1]);
            Q.set(i, i, -(1.0 / h[i - 1] + 1.0 / h[i]));
            Q.set(i + 1, i, 1.0 / h[i]);

            R.set(i, i, (h[i - 1] + h[i]) / 3.0);
            R.set(i, i + 1, h[i] / 6.0);
            R.set(i + 1, i, h[i] / 6.0);
        }

        int i = countOfActiveAquisitions - 2;
        Q.set(i - 1, i, 1.0 / h[i - 1]);
        Q.set(i, i, -(1.0 / h[i - 1] + 1.0 / h[i]));
        Q.set(i + 1, i, 1.0 / h[i]);

        R.set(i, i, (h[i - 1] + h[i]) / 3.0);

        Q = Q.getMatrix(0, Q.getRowDimension() - 1, 1, Q.getColumnDimension() - 1);
        R = R.getMatrix(1, R.getRowDimension() - 1, 1, R.getColumnDimension() - 1);

        Matrix JgammagSmall = R.solve(Q.transpose());

        // need to add row of zeros to top and bottom
        Jgammag = new Matrix(JgammagSmall.getRowDimension() + 2, JgammagSmall.getColumnDimension());
        Jgammag.setMatrix(1, JgammagSmall.getRowDimension() - 1, 0, JgammagSmall.getColumnDimension() - 1, JgammagSmall);

    }

    /**
     *
     */
    public void prepareMatrixJfMapAquisitionsAquisitions() {
        // build session-wide marixJf for each type of fit function
        matrixJfMapAquisitionsAquisitions = new EnumMap<>(FitFunctionTypeEnum.class);

        // be sure times are prepared for standards AND all the work needed to support smoothing splines
        //if ( Jgammag == null ) {
        // we are now recalculating Jf each time to accomodate changes in included standards ... could be streamlined to only happen on count
        prepareSessionWithAquisitionTimes();
        int countOfAquisitions = activeXvalues.length;

        // prepare spline Jf
        Matrix Jfg = new Matrix(countOfAquisitions, countOfAquisitions);
        Matrix Jfgamma = new Matrix(countOfAquisitions, countOfAquisitions);

        for (int k = 0; k < activeXvalues.length; k++) {
            populateJacobianGAndGamma(k, activeXvalues[k], Jfg, Jfgamma);
        }

        Matrix Jf = Jfg.plus(Jfgamma.times(Jgammag));
        matrixJfMapAquisitionsAquisitions.put(FitFunctionTypeEnum.SMOOTHING_SPLINE, Jf);

        // prepare LM exponential Jf
        Jf = new Matrix(countOfAquisitions, 3);
        // the LM fit function will have to populate Jf on demand as it depends on the fit parameters
        matrixJfMapAquisitionsAquisitions.put(FitFunctionTypeEnum.EXPONENTIAL, Jf);

        // prepare line Jf
        Jf = new Matrix(countOfAquisitions, 2, 1.0);
        for (int i = 0; i < countOfAquisitions; i++) {
            Jf.set(i, 1, activeXvalues[i]);
        }
        matrixJfMapAquisitionsAquisitions.put(FitFunctionTypeEnum.LINE, Jf);

        // mean has no Jf
        matrixJfMapAquisitionsAquisitions.put(FitFunctionTypeEnum.MEAN, null);
    }

    private void populateJacobianGAndGamma(int k, double tInt, Matrix Jfg, Matrix Jfgamma) {
        int stIndex = AbstractFunctionOfX.calculateTimeLeftBracketIndex(tInt, tList);

        Jfg.set(k, stIndex, (tList.get(stIndex + 1) - tInt) / h[stIndex]);
        Jfg.set(k, stIndex + 1, (tInt - tList.get(stIndex)) / h[stIndex]);
        Jfgamma.set(k, stIndex, (tInt - tList.get(stIndex)) * (tInt - tList.get(stIndex + 1)) * (h[stIndex] - tInt + tList.get(stIndex + 1)) / (6 * h[stIndex]));
        Jfgamma.set(k, stIndex + 1, (tInt - tList.get(stIndex)) * (tInt - tList.get(stIndex + 1)) * (h[stIndex] + tInt - tList.get(stIndex)) / (6 * h[stIndex]));

    }

    /**
     * @return the rawRatioName
     */
    public RawRatioNames getRawRatioName() {
        return rawRatioName;
    }

    /**
     * @return the weightedMeanIntegrations
     */
    public double[] getWeightedMeanIntegrations() {
        return weightedMeanIntegrations;
    }

    /**
     * @return the fittedStandards
     */
    public double[] getFittedStandards() {
        return fittedStandards;
    }

    /**
     * @param fittedStandards the fittedStandards to set
     */
    public void setFittedStandards(double[] fittedStandards) {
        this.fittedStandards = fittedStandards;
    }

    /**
     * @return the normalizedAquireTimes
     */
    public double[] getNormalizedAquireTimes() {
        return normalizedAquireTimes;
    }

    /**
     * @return the FitFunction
     */
    @Override
    public Map<String, AbstractFunctionOfX> getFitFunctions() {
        Map<String, AbstractFunctionOfX> fitFunctions;

        if (overDispersionSelected) {
            fitFunctions = downholeStandardsFitFunctionsWithOD;
        } else {
            fitFunctions = downholeStandardsFitFunctionsNoOD;
        }

        return fitFunctions;
    }

    /**
     * @return the fittedStandardsResiduals
     */
    public double[] getFittedStandardsResiduals() {
        return fittedStandardsResiduals;
    }

    /**
     * @return the maskingSingleton
     */
    public MaskingSingleton getMaskingSingleton() {
        return maskingSingleton;
    }

    /**
     * @param maskingSingleton the maskingSingleton to set
     */
    public void setMaskingSingleton(MaskingSingleton maskingSingleton) {
        this.maskingSingleton = maskingSingleton;
    }

    /**
     * @return the selectedFitFunctionType
     */
    public FitFunctionTypeEnum getSelectedFitFunctionType() {
        return selectedFitFunctionType;
    }

    /**
     *
     * @return
     */
    public AbstractFunctionOfX getSelectedDownHoleFrationationFitFunction() {
        AbstractFunctionOfX fitFunc;
        if (overDispersionSelected) {
            fitFunc = downholeStandardsFitFunctionsWithOD.get(selectedFitFunctionType.getName());
        } else {
            fitFunc = downholeStandardsFitFunctionsNoOD.get(selectedFitFunctionType.getName());
        }

        return fitFunc;
    }

    /**
     * @param selectedFitFunctionType the selectedFitFunctionType to set
     */
    @Override
    public void setSelectedFitFunctionType(FitFunctionTypeEnum selectedFitFunctionType) {
        this.selectedFitFunctionType = selectedFitFunctionType;
    }

    /**
     * @return the onPeakAcquireTimesBySecond
     */
    public double[] getOnPeakAcquireTimesInSeconds() {
        return onPeakAcquireTimesBySecond;
    }

    public double[] getActiveOnPeakAcquireTimesInSeconds() {
        boolean[] activeData = MaskingSingleton.getInstance().getMaskingArray();
        double[] activeOnPeak = new double[MaskingSingleton.getInstance().getCountOfActiveData()];
        int index = 0;
        for (int i = 0; i < activeData.length; i++) {
            if (activeData[i]) {
                activeOnPeak[index] = onPeakAcquireTimesBySecond[i];
                index++;
            }
        }

        return activeOnPeak;
    }

    /**
     * @param onPeakAcquireTimesBySecond the onPeakAcquireTimesBySecond to set
     */
    public void setOnPeakAcquireTimesBySecond(double[] onPeakAcquireTimesBySecond) {
        this.onPeakAcquireTimesBySecond = onPeakAcquireTimesBySecond;
    }

    /**
     *
     * @return
     */
    @Override
    public RawRatioNames getRawRatioModelName() {
        return rawRatioName;
    }

    /**
     *
     */
    @Override
    public void cleanupUnctCalcs() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     *
     * @return
     */
    @Override
    public AbstractFunctionOfX getSelectedFitFunction() {
        AbstractFunctionOfX fitFunc;
        if (overDispersionSelected) {
            fitFunc = downholeStandardsFitFunctionsWithOD.get(selectedFitFunctionType.getName());
        } else {
            fitFunc = downholeStandardsFitFunctionsNoOD.get(selectedFitFunctionType.getName());
        }

        return fitFunc;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isCalculatedInitialFitFunctions() {
        return calculatedInitialFitFunctions;
    }

    /**
     *
     * @param fitFunctionType
     * @return
     */
    @Override
    public boolean containsFitFunction(FitFunctionTypeEnum fitFunctionType) {
        boolean contains = false;

        if (overDispersionSelected) {
            contains = downholeStandardsFitFunctionsWithOD.get(fitFunctionType.getName()) != null;
        } else {
            contains = downholeStandardsFitFunctionsNoOD.get(fitFunctionType.getName()) != null;
        }

        return contains;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isOverDispersionSelected() {
        return overDispersionSelected;
    }

    @Override
    public void setOverDispersionSelected(boolean overDispersionSelected) {
        this.overDispersionSelected = overDispersionSelected;
    }

    /**
     *
     * @param fitFunctionType
     * @return
     */
    @Override
    public boolean doesFitFunctionTypeHaveOD(FitFunctionTypeEnum fitFunctionType) {

        boolean retVal = false;
        AbstractFunctionOfX fitFunc = downholeStandardsFitFunctionsWithOD.get(fitFunctionType.getName());
        if (fitFunc != null) {
            retVal = fitFunc.isOverDispersionSelected();
        }
        return retVal;
    }

    /**
     *
     * @param fitFunctionType
     * @return
     */
    @Override
    public double getXIforFitFunction(FitFunctionTypeEnum fitFunctionType) {
        double retVal = 0.0;
        if (doesFitFunctionTypeHaveOD(fitFunctionType)) {
            return Math.sqrt(downholeStandardsFitFunctionsWithOD.get(fitFunctionType.getName()).getOverDispersion());
        }

        return retVal;
    }

    /**
     * @return the activeXvalues
     */
    public double[] getActiveXvalues() {
        return activeXvalues;
    }

    /**
     * @return the standardValueModel
     */
    public ValueModel getStandardValueModel() {
        return standardValueModel;
    }

    /**
     * @param standardValueModel the standardValueModel to set
     */
    public void setStandardValueModel(ValueModel standardValueModel) {
        this.standardValueModel = standardValueModel;
    }

    /**
     * @return the fittedStandardsBeta
     */
    public double[] getFittedStandardsBeta() {
        return fittedStandardsBeta;
    }

    /**
     * @param fittedStandardsBeta the fittedStandardsBeta to set
     */
    public void setFittedStandardsBeta(double[] fittedStandardsBeta) {
        this.fittedStandardsBeta = fittedStandardsBeta;
    }

    @Override
    public AbstractFunctionOfX getSelectedDownHoleFitFunction() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
