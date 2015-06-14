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
import java.util.TreeMap;
import org.earthtime.Tripoli.fitFunctions.AbstractFunctionOfX;
import org.earthtime.Tripoli.fitFunctions.LevenbergMarquardGeneralSolverWithCovS;
import org.earthtime.Tripoli.fitFunctions.LevenbergMarquardGeneralSolverWithCovS.AbstractOverDispersionLMAlgorithm;
import org.earthtime.Tripoli.fitFunctions.MeanFitFunction;
import org.earthtime.Tripoli.fitFunctions.SmoothingSplineWithCov;
import org.earthtime.Tripoli.fractions.TripoliFraction;
import org.earthtime.dataDictionaries.FitFunctionTypeEnum;
import org.earthtime.dataDictionaries.RawRatioNames;

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
    public static int MAX_AQUISITIONS_SHADABLE = 3;
    //
    private final RawRatioNames rawRatioName;
    private double[] averageAlphas;
    private double[] onPeakAcquireTimesBySecond;
    private final double[] normalizedAquireTimes;
    private double[] fittedAlphas;
    private double[] fittedAlphasResiduals;
    private MaskingSingleton maskingSingleton;
    private final Map<String, AbstractFunctionOfX> alphasFitFunctions;

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
     * @param rawRatioName
     * @param averageAlphas
     * @param onPeakAcquireTimesBySecond
     * @param normalizedAquireTimes
     * @param maskingSingleton
     */
    public DownholeFractionationDataModel(//
            RawRatioNames rawRatioName,//
            double[] averageAlphas,//
            double[] onPeakAcquireTimesBySecond, //
            double[] normalizedAquireTimes, //
            MaskingSingleton maskingSingleton) {

        this.rawRatioName = rawRatioName;
        this.averageAlphas = averageAlphas;
        this.onPeakAcquireTimesBySecond = onPeakAcquireTimesBySecond;
        this.normalizedAquireTimes = normalizedAquireTimes;

        this.fittedAlphas = new double[averageAlphas.length];
        this.fittedAlphasResiduals = new double[averageAlphas.length];
        this.maskingSingleton = maskingSingleton;
        this.alphasFitFunctions = new TreeMap<>();
        this.downholeStandardsFitFunctionsNoOD = new TreeMap<>();
        this.downholeStandardsFitFunctionsWithOD = new TreeMap<>();
        this.overDispersionSelected = true;

        this.selectedFitFunctionType = FitFunctionTypeEnum.EXPONENTIAL;

        this.calculatedInitialFitFunctions = false;
        this.overDispersionSelected = true;

    }

//    private void initializeAverageAlphas () {
//        for (int i = 0; i < averageAlphas.length; i ++) {
//            averageAlphas[i] = 0.0;
//            fittedAlphas[i] = 0.0;
//            fittedAlphasResiduals[i] = 0.0;
//        }
//    }
    /**
     *
     * @param fractionIterator
     */
    public void calculateWeightedMeanOfStandards(Iterator<TripoliFraction> fractionIterator) {

        // march 2013
        boolean[] dataCommonActiveMap = MaskingSingleton.getInstance().getMaskingArray();
        int countOfActiveData = 0;
        for (int i = 0; i < dataCommonActiveMap.length; i++) {
            if (dataCommonActiveMap[i]) {
                countOfActiveData++;
            }
        }

        activeDataMap = new boolean[countOfActiveData];
        averageAlphas = new double[countOfActiveData];
        fittedAlphas = new double[countOfActiveData];
        fittedAlphasResiduals = new double[countOfActiveData];
        activeXvalues = new double[countOfActiveData];

        int index = 0;
        for (int i = 0; i < dataCommonActiveMap.length; i++) {
            if (dataCommonActiveMap[i]) {
                activeDataMap[index] = true;
                averageAlphas[index] = 0.0;
                fittedAlphas[index] = 0.0;
                fittedAlphasResiduals[index] = 0.0;
                activeXvalues[index] = normalizedAquireTimes[i];

                index++;
            }
        }

        Matrix sumInvSlogRatioX_Y = new Matrix(countOfActiveData, countOfActiveData);
        Matrix sumInvSlogRatioX_YTimeslr = new Matrix(countOfActiveData, 1);

        // assume fraction iterator is correct length as it was set on included fractions
        while (fractionIterator.hasNext()) {
            TripoliFraction tf = fractionIterator.next();

//            System.out.println("Fraction id at downhole = " + tf.getFractionID());
            RawRatioDataModel rawRatio = //
                    ((RawRatioDataModel) tf.getRawRatioDataModelByName(rawRatioName));

            rawRatio.propagateUnctInRatios();

            Matrix SlogRatioX_Y = rawRatio.getSlogRatioX_Y();

            sumInvSlogRatioX_Y.plusEquals(SlogRatioX_Y.inverse());

            // get active logratios from standard
            Matrix logRatiosVector = new Matrix(rawRatio.getActiveLogRatios(countOfActiveData), countOfActiveData);

            sumInvSlogRatioX_YTimeslr.plusEquals(SlogRatioX_Y.solve(logRatiosVector));
        }

        Matrix wtdMeanStdIntegrations = sumInvSlogRatioX_Y.solve(sumInvSlogRatioX_YTimeslr);

        SwtdMeanStdIntegrations = sumInvSlogRatioX_Y.inverse();

        // populate arrays for plotting and fitting
        for (int i = 0; i < averageAlphas.length; i++) {
            averageAlphas[i] = wtdMeanStdIntegrations.get(i, 0);
        }

//        if (  ! ((RawRatioDataModel) tf.getRawRatioDataModelByName( rawRatioName )).isBelowDetection() ) {
//            double[] alphas = ((RawRatioDataModel) tf.getRawRatioDataModelByName( rawRatioName )).calculateAlphas();
//            boolean[] dataActiveMap = ((RawRatioDataModel) tf.getRawRatioDataModelByName( rawRatioName )).getDataActiveMap();
//
//            for (int i = 0; i < alphas.length; i ++) {
//                // calculate sum of alphas
//                if ( dataActiveMap[i] ) {
//                    countOfIncludedPoints[i] ++;
//                    averageAlphas[i] += alphas[i];
//                }
//            }
//        }
////        }
//
//        // calculate averages; averageAlphas is incoming parameter that gets populated here
//        for (int i = 0; i < averageAlphas.length; i ++) {
////            // dec 2012 to handle missing ratios due to belowdetection flag so turn off fraction in masking
////            if ( countOfIncludedPoints[i] == 0.0 ) {
////                maskingSingleton.getMaskingArray()[i] = false;
////            } else {
//            // dec 2012 if we get here with no data, divide by zero = NaN
//            averageAlphas[i] = averageAlphas[i] / countOfIncludedPoints[i];
////            }
//        }

    }

//    @Override
//    public void generateSelectedFitFunction() {
//        throw new UnsupportedOperationException("Not supported yet.");
//    }

    private boolean generateMEANfitFunctionUsingLM() {

        boolean retVal;

        AbstractOverDispersionLMAlgorithm algorithmForMEAN//
                = LevenbergMarquardGeneralSolverWithCovS.getInstance()//
                .getSelectedLMAlgorithm(//
                FitFunctionTypeEnum.MEAN,//
                activeDataMap, //
                activeXvalues, //
                averageAlphas,//
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
                    averageAlphas,//
                    null, //
                    false);

            downholeStandardsFitFunctionsNoOD.put(FitFunctionTypeEnum.MEAN.getName(), fOfX_MEAN);
            downholeStandardsFitFunctionsWithOD.put(FitFunctionTypeEnum.MEAN.getName(), fOfX_MEAN);

            selectedFitFunctionType = FitFunctionTypeEnum.MEAN;

            retVal = false;
        }

        return retVal;
    }

    private void generateLINEfitFunctionUsingLM() {

        AbstractOverDispersionLMAlgorithm algorithmForLINE = LevenbergMarquardGeneralSolverWithCovS.getInstance().getSelectedLMAlgorithm(//
                FitFunctionTypeEnum.LINE,//
                activeDataMap, //
                activeXvalues, //
                averageAlphas,//
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
        } else {
            downholeStandardsFitFunctionsNoOD.remove(FitFunctionTypeEnum.LINE.getName());
            downholeStandardsFitFunctionsWithOD.remove(FitFunctionTypeEnum.LINE.getName());
            selectedFitFunctionType = FitFunctionTypeEnum.MEAN;
        }
    }

    private void generateEXPONENTIALfitFunctionUsingLM() {

//        System.out.println("trying expfast");
        AbstractOverDispersionLMAlgorithm algorithmForEXPFAST = LevenbergMarquardGeneralSolverWithCovS.getInstance().getSelectedLMAlgorithm(//
                FitFunctionTypeEnum.EXPFAST,//
                activeDataMap, //
                activeXvalues, //
                averageAlphas,//
                SwtdMeanStdIntegrations, //
                false);
        AbstractOverDispersionLMAlgorithm algorithmForEXPMAT;

        AbstractFunctionOfX expFastFunc = algorithmForEXPFAST.getInitialFofX();
        if (expFastFunc != null) {
//            System.out.println("now trying expmat with expfast input");
            algorithmForEXPMAT = LevenbergMarquardGeneralSolverWithCovS.getInstance().getSelectedLMAlgorithmUsingIntialFofX(//
                    FitFunctionTypeEnum.EXPMAT,//
                    activeDataMap, //
                    activeXvalues, //
                    averageAlphas,//
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
//                    System.out.println("now trying expOD with expfast input");
                    AbstractOverDispersionLMAlgorithm algorithmForEXPOD = LevenbergMarquardGeneralSolverWithCovS.getInstance().getSelectedLMAlgorithmUsingIntialFofX(//
                            FitFunctionTypeEnum.EXPONENTIAL,//
                            activeDataMap, //
                            activeXvalues, //
                            averageAlphas,//
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
                averageAlphas,//
                SwtdMeanStdIntegrations, //
                true);

        if ((fOfX_SMOOTHING_SPLINE != null) & fOfX_SMOOTHING_SPLINE.verifyPositiveVariances()) {
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
                averageAlphas,//
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

        SortedMap<Double, AbstractFunctionOfX> sessionOfStandardsSplinesWithOD = new TreeMap<Double, AbstractFunctionOfX>();

        for (double i = startValue; i < stopValue; i += increment) {
            AbstractFunctionOfX fOfX_SMOOTHING_SPLINE = generateSplineWithSpecificOD(Math.pow(i, 2));

//            System.out.println("OD TEST xi = " + i + "  logRoughness = " + fOfX_SMOOTHING_SPLINE.getLogRoughness() + "\n");

            if (Double.isNaN(fOfX_SMOOTHING_SPLINE.getLogRoughness())) {//|| (fOfX_SMOOTHING_SPLINE.getLogRoughness() < -41.0)) {

                //fOfX_SMOOTHING_SPLINE = generateSplineWithSpecificOD(Math.pow(i - increment, 2));

                try {
                    fOfX_SMOOTHING_SPLINE = sessionOfStandardsSplinesWithOD.get(sessionOfStandardsSplinesWithOD.lastKey());
                    AbstractFunctionOfX fOfX_SMOOTHING_SPLINE_EXIST = downholeStandardsFitFunctionsWithOD.get(fOfX_SMOOTHING_SPLINE.getShortNameString());
                    if (fOfX_SMOOTHING_SPLINE_EXIST != null) {
                        fOfX_SMOOTHING_SPLINE_EXIST.copyValuesFrom(fOfX_SMOOTHING_SPLINE);
                    } else {
                        downholeStandardsFitFunctionsWithOD.put(fOfX_SMOOTHING_SPLINE.getShortNameString(), fOfX_SMOOTHING_SPLINE);
                    }
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
    public void generateSetOfFitFunctions(boolean propagateUncertainties, boolean doApplyMaskingArray) {

//        System.out.println("\nCalculating Fit Functions for Downhole  " + getRawRatioModelName().getDisplayName());

        if (generateMEANfitFunctionUsingLM()) {
            generateLINEfitFunctionUsingLM();
            generateEXPONENTIALfitFunctionUsingLM();
            generateSPLINEfitFunction(SwtdMeanStdIntegrations);
        }

//        System.out.println("Downhole fit complete\n");
    }

    /**
     *
     * @return
     */
    public double[] getFitFunctionLogValues() {
        AbstractFunctionOfX fitFunc = getSelectedFitFunction();

        for (int i = 0; i < fittedAlphas.length; i++) {
            try {
                fittedAlphas[i] = fitFunc.f(activeXvalues[i]);
            } catch (Exception e) {
                fittedAlphas[i] = 0.0;
            }
        }
        return fittedAlphas;
    }

    /**
     *
     * @param fitFunctionTypeName
     */
    @Override
    public void calculateFittedFunctions(String fitFunctionTypeName) {

        double sumSquaredErrors = 0.0;
        for (int i = 0; i < fittedAlphas.length; i++) {
            fittedAlphas[i] = alphasFitFunctions.get(fitFunctionTypeName).f(normalizedAquireTimes[i]);

            fittedAlphasResiduals[i] = 0.0;
            if (getMaskingSingleton().getMaskingArray()[i]) {
                fittedAlphasResiduals[i] = averageAlphas[i] - fittedAlphas[i];
                sumSquaredErrors += fittedAlphasResiduals[i] * fittedAlphasResiduals[i];
            }
        }

        alphasFitFunctions.get(fitFunctionTypeName).setChiSquared(sumSquaredErrors);
//        alphasFitFunctions.get( fitFunctionTypeName ).setGoodnessOfFitType( GoodnessOfFitFunctionTypeEnum.SSE );

        selectedFitFunctionType = FitFunctionTypeEnum.valueOf(fitFunctionTypeName);
    }

    /**
     *
     * @param fractionIterator
     * @param rawRatioName
     * @param downHoleFitFunction
     */
    public void calculateWeightedMeanForEachStandard(Iterator<TripoliFraction> fractionIterator, RawRatioNames rawRatioName, AbstractFunctionOfX downHoleFitFunction) {

        prepareMatrixJfMapAquisitionsAquisitions();

        Matrix matrixJfStandardsStandards = matrixJfMapAquisitionsAquisitions.get(downHoleFitFunction.getShortName());
        // this call to interpolated variances is needed ro grt Sf = need to refactor
        downHoleFitFunction.calculateInterpolatedVariances(matrixJfStandardsStandards, activeXvalues);

        Matrix matrixSf = downHoleFitFunction.getMatrixSf();

        while (fractionIterator.hasNext()) {
            TripoliFraction tf = fractionIterator.next();

            DataModelInterface rawRatio = tf.getRawRatioDataModelByName(rawRatioName);

            ((RawRatioDataModel) rawRatio).calculateDownholeFractionWeightedMeanAndUnct(matrixSf, downHoleFitFunction);
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
     * @return the averageAlphas
     */
    public double[] getAverageAlphas() {
        return averageAlphas;
    }

    /**
     * @return the fittedAlphas
     */
    public double[] getFittedAlphas() {
        return fittedAlphas;
    }

    /**
     * @param fittedAlphas the fittedAlphas to set
     */
    public void setFittedAlphas(double[] fittedAlphas) {
        this.fittedAlphas = fittedAlphas;
    }

    /**
     * @return the normalizedAquireTimes
     */
    public double[] getNormalizedAquireTimes() {
        return normalizedAquireTimes;
    }

    /**
     * @return the alphasFitFunction
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
     * @return the fittedAlphasResiduals
     */
    public double[] getFittedAlphasResiduals() {
        return fittedAlphasResiduals;
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
        return alphasFitFunctions.get(selectedFitFunctionType.toString());
    }

    /**
     * @param selectedFitFunctionType the selectedFitFunctionType to set
     */
    public void setSelectedFitFunctionType(FitFunctionTypeEnum selectedFitFunctionType) {
        this.selectedFitFunctionType = selectedFitFunctionType;
    }

    /**
     * @return the onPeakAcquireTimesBySecond
     */
    public double[] getOnPeakAcquireTimesInSeconds() {
        return onPeakAcquireTimesBySecond;
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
}
