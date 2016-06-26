/*
 * AbstractSessionForStandardDataModel.java
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
package org.earthtime.Tripoli.dataModels.sessionModels;

import Jama.Matrix;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import org.earthtime.Tripoli.dataModels.DataModelFitFunctionInterface;
import org.earthtime.Tripoli.dataModels.RawRatioDataModel;
import org.earthtime.Tripoli.dataModels.collectorModels.IonCounterCollectorModel;
import org.earthtime.Tripoli.dataViews.fitFunctionViews.SessionSplineRangeOD;
import org.earthtime.Tripoli.fitFunctions.AbstractFunctionOfX;
import org.earthtime.Tripoli.fitFunctions.AbstractOverDispersionLMAlgorithmInterface;
import org.earthtime.Tripoli.fitFunctions.LevenbergMarquardGeneralSolverWithCovS;
import org.earthtime.Tripoli.fitFunctions.LevenbergMarquardGeneralSolverWithCovS.AbstractOverDispersionLMAlgorithm;
import org.earthtime.Tripoli.fitFunctions.LevenbergMarquardGeneralSolverWithVecV;
import org.earthtime.Tripoli.fitFunctions.LevenbergMarquardGeneralSolverWithVecV.AbstractOverDispersionLMVecAlgorithm;
import org.earthtime.Tripoli.fitFunctions.MeanFitFunction;
import org.earthtime.Tripoli.fitFunctions.SmoothingSplineWithCov;
import org.earthtime.Tripoli.fractions.TripoliFraction;
import org.earthtime.Tripoli.sessions.TripoliSessionInterface;
import org.earthtime.dataDictionaries.FitFunctionTypeEnum;
import org.earthtime.dataDictionaries.RawRatioNames;

/**
 *
 * @author James F. Bowring
 */
public abstract class AbstractSessionForStandardDataModel implements Serializable, DataModelFitFunctionInterface {

    // Class variables
    private static final long serialVersionUID = -2266381475632623313L;

    /**
     *
     */
    protected final static boolean USING_FULL_PROPAGATION = true;// ALWAYS TRUE 
    /**
     *
     */
    protected RawRatioNames rawRatioName;
    /**
     *
     */
    private SortedSet<TripoliFraction> standardFractions;
    /**
     *
     */
    protected Map<String, AbstractFunctionOfX> sessionOfStandardsFitFunctionsNoOD;

    /**
     *
     */
    protected Map<String, AbstractFunctionOfX> sessionOfStandardsFitFunctionsWithOD;

    /**
     *
     */
    protected boolean overDispersionSelected;
    /**
     *
     */
    protected FitFunctionTypeEnum selectedFitFunctionType;
    /**
     *
     */
    protected double[] fittedStandardMeanLogRatios;
    /**
     *
     */
    protected boolean[] includedStandardsDataActiveMap;
    /**
     *
     */
    protected double[] includedStandardsAquireTimes;
    /**
     *
     */
    private double[] allStandardsMeanLogRatios;
    private double[] allStandardsMeanLogRatioStdErrs;

    /**
     *
     */
    protected double[] includedStandardsMeanLogRatios;

    /**
     *
     */
    protected double[] includedStandardsMeanLogRatioStdErrs;

    /**
     *
     */
    protected String sessionTechnique;
    private double standardValue;
    private Matrix sessionIncludedStandardsCovariance;
    //   private Matrix sessionIncludedStandardsVarianceVector;
    private boolean calculatedInitialFitFunctions;

    /**
     *
     */
    protected TripoliSessionInterface tripoliSession;

    // oct 2014
    private SessionSplineRangeOD sessionSplineRangeOD;

    /**
     *
     *
     * @param tripoliSession the value of tripoliSession
     * @param rawRatioName
     * @param standardValue the value of standardValue
     * @param standardFractions
     */
    public AbstractSessionForStandardDataModel(//
            TripoliSessionInterface tripoliSession, RawRatioNames rawRatioName, double standardValue, SortedSet<TripoliFraction> standardFractions) {

        this.tripoliSession = tripoliSession;
        this.rawRatioName = rawRatioName;
        this.standardValue = standardValue;
        this.standardFractions = standardFractions;
        this.sessionOfStandardsFitFunctionsNoOD = new TreeMap<>();
        this.sessionOfStandardsFitFunctionsWithOD = new TreeMap<>();
        this.overDispersionSelected = true;
        this.selectedFitFunctionType = FitFunctionTypeEnum.LINE;//.SMOOTHING_SPLINE;
        this.fittedStandardMeanLogRatios = null;
        this.allStandardsMeanLogRatios = null;
        this.allStandardsMeanLogRatioStdErrs = null;
        this.includedStandardsDataActiveMap = null;
        this.includedStandardsAquireTimes = null;
        this.includedStandardsMeanLogRatios = null;
        this.includedStandardsMeanLogRatioStdErrs = null;
        this.sessionIncludedStandardsCovariance = null;
        //      this.sessionIncludedStandardsVarianceVector = null;
        this.calculatedInitialFitFunctions = false;
        this.sessionSplineRangeOD = new SessionSplineRangeOD();
    }

    /**
     *
     *
     * @param propagateUncertainties the value of propagateUncertainties
     * @param doApplyMaskingArray the value of doApplyMaskingArray
     * @param inLiveMode
     */
    @Override
    public void generateSetOfFitFunctions(boolean propagateUncertainties, boolean doApplyMaskingArray, boolean inLiveMode) {

        // dec 2014 ... needs refactoring to be property
        // ?always true for sessions USING_FULL_PROPAGATION =  tripoliSession.getRawDataFileHandler().getAcquisitionModel().isUsingFullPropagation();//  RawRatioDataModel.USING_FULL_PROPAGATION;       
        // for plotting - want to show all even those excluded
        int countOfAllStandards = standardFractions.size();
        allStandardsMeanLogRatios = new double[countOfAllStandards];
        allStandardsMeanLogRatioStdErrs = new double[countOfAllStandards];

        Iterator<TripoliFraction> fractionIterator = standardFractions.iterator();
        int index = 0;

        while (fractionIterator.hasNext()) {
            TripoliFraction tf = fractionIterator.next();

            RawRatioDataModel rawRatio
                    = ((RawRatioDataModel) tf.getRawRatioDataModelByName(rawRatioName));

            allStandardsMeanLogRatios[index]
                    = rawRatio.getSessionValueBySessionTechnique(sessionTechnique);
            allStandardsMeanLogRatioStdErrs[index]
                    = rawRatio.getSessionErrorBySessionTechnique(sessionTechnique);

            index++;

        }

        //begin section needed for fitting
        // jan 2013 re-engineer so that dataactivemap is only used to prepare for fit
        // functions, not sent to them
        // transition phase = will send in fake dataactive map until re-engineer completed       
        // set up only included fractions for line-fitting
        int countOfIncludedStandards = getActiveStandardFractions().size();

        includedStandardsAquireTimes = new double[countOfIncludedStandards];
        includedStandardsMeanLogRatios = new double[countOfIncludedStandards];
        includedStandardsMeanLogRatioStdErrs = new double[countOfIncludedStandards];
        includedStandardsDataActiveMap = new boolean[countOfIncludedStandards];

        sessionIncludedStandardsCovariance = new Matrix(countOfIncludedStandards, countOfIncludedStandards);

        fractionIterator = getActiveStandardFractions().iterator();
        index = 0;

        while (fractionIterator.hasNext()) {
            TripoliFraction tf = fractionIterator.next();

            includedStandardsDataActiveMap[index] = true;
            includedStandardsAquireTimes[index] = tf.getZeroBasedNormalizedTimeStamp();

            RawRatioDataModel rawRatio
                    = ((RawRatioDataModel) tf.getRawRatioDataModelByName(rawRatioName));
            includedStandardsMeanLogRatios[index]
                    = rawRatio.getSessionValueBySessionTechnique(sessionTechnique);
            includedStandardsMeanLogRatioStdErrs[index]
                    = rawRatio.getSessionErrorBySessionTechnique(sessionTechnique);
            sessionIncludedStandardsCovariance.set( //
                    index, index, rawRatio.getSessionVarianceBySessionTechnique(sessionTechnique));

            index++;

        }

        if (countOfIncludedStandards > 0) {
            // begin section 8 of uncertainty propagation paper

            RawRatioDataModel rawRatioFirst
                    = ((RawRatioDataModel) getActiveStandardFractions().first().getRawRatioDataModelByName(rawRatioName));
            if (rawRatioFirst.hasTwoIdenticalIonCounters()) {
                // calculate the covariance between each pair of standards
                Matrix dLrInt_dDt = new Matrix(countOfIncludedStandards, 1);
                int index2 = 0;

                fractionIterator = getActiveStandardFractions().iterator();
                while (fractionIterator.hasNext()) {
                    TripoliFraction tf = fractionIterator.next();

                    RawRatioDataModel rawRatio
                            = ((RawRatioDataModel) tf.getRawRatioDataModelByName(rawRatioName));

                    dLrInt_dDt.set(index2, 0, rawRatio.getSelectedFitFunction().getdLrInt_dDt());

                    index2++;
                }

                double deadtimeUnctSqr
                        = ((IonCounterCollectorModel) rawRatioFirst.getBotIsotope().getCollectorModel())//
                        .getDeadTime().getOneSigmaAbs().movePointLeft(0).pow(2).doubleValue();

                Matrix Ssod = dLrInt_dDt.times(dLrInt_dDt.transpose()).times(deadtimeUnctSqr);

                // zero diagonal
                for (int i = 0; i < countOfIncludedStandards; i++) {
                    Ssod.set(i, i, 0.0);
                }

                sessionIncludedStandardsCovariance.plusEquals(Ssod);
            }

            calculateFitFunctions(inLiveMode);
            calculatedInitialFitFunctions = true;
        }
    }

    private boolean generateMEANfitFunctionUsingLM() {

        boolean retVal;

        // algorithmForMEAN contains both the non OD and OD versions
        AbstractFunctionOfX fOfX_MEAN = null;
        AbstractFunctionOfX fOfX_MEAN_OD = null;

        if (USING_FULL_PROPAGATION) {
            AbstractOverDispersionLMAlgorithm algorithmForMEAN = LevenbergMarquardGeneralSolverWithCovS.getInstance()//
                    .getSelectedLMAlgorithm( //
                            FitFunctionTypeEnum.MEAN,//
                            includedStandardsDataActiveMap, //
                            includedStandardsAquireTimes, //
                            includedStandardsMeanLogRatios,
                            sessionIncludedStandardsCovariance, false);

            fOfX_MEAN = algorithmForMEAN.getInitialFofX();
            fOfX_MEAN_OD = algorithmForMEAN.getFinalFofX();
        } else {
            AbstractOverDispersionLMVecAlgorithm algorithmForMEAN = LevenbergMarquardGeneralSolverWithVecV.getInstance()//
                    .getSelectedLMAlgorithm( //
                            FitFunctionTypeEnum.MEAN,//
                            includedStandardsDataActiveMap, //
                            includedStandardsAquireTimes, //
                            includedStandardsMeanLogRatios,
                            sessionIncludedStandardsCovariance, false);

            fOfX_MEAN = algorithmForMEAN.getInitialFofX();
            fOfX_MEAN_OD = algorithmForMEAN.getFinalFofX();
        }

        if ((fOfX_MEAN != null) && fOfX_MEAN.verifyPositiveVariances()) {
            if (sessionOfStandardsFitFunctionsNoOD.containsKey(fOfX_MEAN.getShortNameString())) {
                AbstractFunctionOfX fOfXexist = sessionOfStandardsFitFunctionsNoOD.get(fOfX_MEAN.getShortNameString());
                fOfXexist.copyValuesFrom(fOfX_MEAN);
            } else {
                sessionOfStandardsFitFunctionsNoOD.put(fOfX_MEAN.getShortNameString(), fOfX_MEAN);
            }

            if ((fOfX_MEAN_OD != null) && fOfX_MEAN_OD.verifyPositiveVariances()) {
                if (sessionOfStandardsFitFunctionsWithOD.containsKey(fOfX_MEAN_OD.getShortNameString())) {
                    AbstractFunctionOfX fOfXexist = sessionOfStandardsFitFunctionsWithOD.get(fOfX_MEAN_OD.getShortNameString());
                    fOfXexist.copyValuesFrom(fOfX_MEAN_OD);
                } else {
                    sessionOfStandardsFitFunctionsWithOD.put(fOfX_MEAN_OD.getShortNameString(), fOfX_MEAN_OD);
                }
            } else {
                sessionOfStandardsFitFunctionsWithOD.put(fOfX_MEAN.getShortNameString(), fOfX_MEAN);
            }
            retVal = true;

        } else {
            // to handle really bad data sets, for which LM wont work, do good old fashioned mean
            fOfX_MEAN = MeanFitFunction.getInstance()//
                    .getFunctionOfX(//
                            includedStandardsDataActiveMap, //
                            includedStandardsAquireTimes, //
                            includedStandardsMeanLogRatios,
                            sessionIncludedStandardsCovariance,//null, //
                            false);

            sessionOfStandardsFitFunctionsNoOD.put(FitFunctionTypeEnum.MEAN.getName(), fOfX_MEAN);
            sessionOfStandardsFitFunctionsWithOD.put(FitFunctionTypeEnum.MEAN.getName(), fOfX_MEAN);

            selectedFitFunctionType = FitFunctionTypeEnum.MEAN;

            retVal = false;
        }

        return retVal;
    }

    private void generateLINEfitFunctionUsingLM() {

        // algorithmForLINE contains both the non OD and OD versions
        AbstractFunctionOfX fOfX_LINE = null;
        AbstractFunctionOfX fOfX_LINE_OD = null;

        if (USING_FULL_PROPAGATION) {
            AbstractOverDispersionLMAlgorithm algorithmForLINE = LevenbergMarquardGeneralSolverWithCovS.getInstance().getSelectedLMAlgorithm(//
                    FitFunctionTypeEnum.LINE,//
                    includedStandardsDataActiveMap, //
                    includedStandardsAquireTimes, //
                    includedStandardsMeanLogRatios,
                    sessionIncludedStandardsCovariance, false);

            fOfX_LINE = algorithmForLINE.getInitialFofX();
            fOfX_LINE_OD = algorithmForLINE.getFinalFofX();
        } else {

            AbstractOverDispersionLMVecAlgorithm algorithmForLINE = LevenbergMarquardGeneralSolverWithVecV.getInstance()//
                    .getSelectedLMAlgorithm( //
                            FitFunctionTypeEnum.LINE,//
                            includedStandardsDataActiveMap, //
                            includedStandardsAquireTimes, //
                            includedStandardsMeanLogRatios,
                            sessionIncludedStandardsCovariance, false);

            fOfX_LINE = algorithmForLINE.getInitialFofX();
            fOfX_LINE_OD = algorithmForLINE.getFinalFofX();
        }

        if ((fOfX_LINE != null) && fOfX_LINE.verifyPositiveVariances()) {
            if (sessionOfStandardsFitFunctionsNoOD.containsKey(fOfX_LINE.getShortNameString())) {
                AbstractFunctionOfX fOfXexist = sessionOfStandardsFitFunctionsNoOD.get(fOfX_LINE.getShortNameString());
                fOfXexist.copyValuesFrom(fOfX_LINE);
            } else {
                sessionOfStandardsFitFunctionsNoOD.put(fOfX_LINE.getShortNameString(), fOfX_LINE);
            }

            if (fOfX_LINE_OD != null) {
                if (sessionOfStandardsFitFunctionsWithOD.containsKey(fOfX_LINE_OD.getShortNameString())) {
                    AbstractFunctionOfX fOfXexist = sessionOfStandardsFitFunctionsWithOD.get(fOfX_LINE_OD.getShortNameString());
                    fOfXexist.copyValuesFrom(fOfX_LINE_OD);
                } else {
                    sessionOfStandardsFitFunctionsWithOD.put(fOfX_LINE_OD.getShortNameString(), fOfX_LINE_OD);
                }
            } else {
                sessionOfStandardsFitFunctionsWithOD.put(fOfX_LINE.getShortNameString(), fOfX_LINE);
            }
        } else {
            sessionOfStandardsFitFunctionsNoOD.remove(FitFunctionTypeEnum.LINE.getName());
            sessionOfStandardsFitFunctionsWithOD.remove(FitFunctionTypeEnum.LINE.getName());
            selectedFitFunctionType = FitFunctionTypeEnum.MEAN;
        }
    }

    private void generateEXPONENTIALfitFunctionUsingLM() {

        System.out.println("trying expfast");

        AbstractFunctionOfX expFastFunc = null;

        if (USING_FULL_PROPAGATION) {
            AbstractOverDispersionLMAlgorithm algorithmForEXPFAST = LevenbergMarquardGeneralSolverWithCovS.getInstance().getSelectedLMAlgorithm(//
                    FitFunctionTypeEnum.EXPFAST,//
                    includedStandardsDataActiveMap, //
                    includedStandardsAquireTimes, //
                    includedStandardsMeanLogRatios,
                    sessionIncludedStandardsCovariance, false);

            expFastFunc = algorithmForEXPFAST.getInitialFofX();
        } else {

            AbstractOverDispersionLMVecAlgorithm algorithmForEXPFAST = LevenbergMarquardGeneralSolverWithVecV.getInstance()//
                    .getSelectedLMAlgorithm( //
                            FitFunctionTypeEnum.EXPFAST,//
                            includedStandardsDataActiveMap, //
                            includedStandardsAquireTimes, //
                            includedStandardsMeanLogRatios,
                            sessionIncludedStandardsCovariance, false);

            expFastFunc = algorithmForEXPFAST.getInitialFofX();
        }

        // AbstractOverDispersionLMAlgorithm algorithmForEXPMAT;
        AbstractOverDispersionLMAlgorithmInterface algorithmForEXPMAT;

        if (expFastFunc != null) {
            System.out.println("now trying expmat with expfast input");

            if (USING_FULL_PROPAGATION) {
                algorithmForEXPMAT = LevenbergMarquardGeneralSolverWithCovS.getInstance().getSelectedLMAlgorithmUsingIntialFofX(//
                        FitFunctionTypeEnum.EXPMAT,//
                        includedStandardsDataActiveMap, //
                        includedStandardsAquireTimes, //
                        includedStandardsMeanLogRatios,
                        sessionIncludedStandardsCovariance, false,//
                        expFastFunc);
            } else {
                algorithmForEXPMAT = LevenbergMarquardGeneralSolverWithVecV.getInstance().getSelectedLMAlgorithmUsingIntialFofX(//
                        FitFunctionTypeEnum.EXPMAT,//
                        includedStandardsDataActiveMap, //
                        includedStandardsAquireTimes, //
                        includedStandardsMeanLogRatios,
                        sessionIncludedStandardsCovariance, false,//
                        expFastFunc);
            }

            AbstractFunctionOfX fOfX_EXPMAT = algorithmForEXPMAT.getFinalFofX();

            if ((fOfX_EXPMAT != null) && (fOfX_EXPMAT.verifyPositiveVariances())) {

                if (sessionOfStandardsFitFunctionsNoOD.containsKey(fOfX_EXPMAT.getShortNameString())) {
                    AbstractFunctionOfX fOfXexist = sessionOfStandardsFitFunctionsNoOD.get(fOfX_EXPMAT.getShortNameString());
                    fOfXexist.copyValuesFrom(fOfX_EXPMAT);
                } else {
                    sessionOfStandardsFitFunctionsNoOD.put(fOfX_EXPMAT.getShortNameString(), fOfX_EXPMAT);
                }

                if (fOfX_EXPMAT.getMSWD() >= 1.0) {
                    System.out.println("now trying expOD with expfast input");

                    AbstractFunctionOfX fOfX_EXPOD = null;

                    if (USING_FULL_PROPAGATION) {
                        AbstractOverDispersionLMAlgorithm algorithmForEXPOD = LevenbergMarquardGeneralSolverWithCovS.getInstance().getSelectedLMAlgorithmUsingIntialFofX(//
                                FitFunctionTypeEnum.EXPONENTIAL,//
                                includedStandardsDataActiveMap, //
                                includedStandardsAquireTimes, //
                                includedStandardsMeanLogRatios,
                                sessionIncludedStandardsCovariance, false,//
                                expFastFunc);

                        fOfX_EXPOD = algorithmForEXPOD.getFinalFofX();
                    } else {

                        AbstractOverDispersionLMVecAlgorithm algorithmForEXPOD = LevenbergMarquardGeneralSolverWithVecV.getInstance().getSelectedLMAlgorithmUsingIntialFofX(//
                                FitFunctionTypeEnum.EXPONENTIAL,//
                                includedStandardsDataActiveMap, //
                                includedStandardsAquireTimes, //
                                includedStandardsMeanLogRatios,
                                sessionIncludedStandardsCovariance, false,//
                                expFastFunc);

                        fOfX_EXPOD = algorithmForEXPOD.getFinalFofX();
                    }

                    if ((fOfX_EXPOD != null) && (fOfX_EXPOD.verifyPositiveVariances())) {

                        if (sessionOfStandardsFitFunctionsWithOD.containsKey(fOfX_EXPOD.getShortNameString())) {
                            AbstractFunctionOfX fOfXexist = sessionOfStandardsFitFunctionsWithOD.get(fOfX_EXPOD.getShortNameString());
                            fOfXexist.copyValuesFrom(fOfX_EXPOD);
                        } else {
                            sessionOfStandardsFitFunctionsWithOD.put(fOfX_EXPOD.getShortNameString(), fOfX_EXPOD);
                        }
                    }

                } else {
                    sessionOfStandardsFitFunctionsWithOD.put(fOfX_EXPMAT.getShortNameString(), fOfX_EXPMAT);
                }
            } else {
                sessionOfStandardsFitFunctionsNoOD.remove(FitFunctionTypeEnum.EXPONENTIAL.getName());//was expmat??
                sessionOfStandardsFitFunctionsWithOD.remove(FitFunctionTypeEnum.EXPONENTIAL.getName());
                selectedFitFunctionType = FitFunctionTypeEnum.LINE;
            }
        } else {
            sessionOfStandardsFitFunctionsNoOD.remove(FitFunctionTypeEnum.EXPONENTIAL.getName());//was expmat ??
            sessionOfStandardsFitFunctionsWithOD.remove(FitFunctionTypeEnum.EXPONENTIAL.getName());
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
                includedStandardsDataActiveMap, //
                includedStandardsAquireTimes, //
                includedStandardsMeanLogRatios,//
                mySessionIncludedStandardsCovariance, true);

//        if ((fOfX_SMOOTHING_SPLINE != null) && fOfX_SMOOTHING_SPLINE.verifyPositiveVariances()) {
        if (fOfX_SMOOTHING_SPLINE.verifyPositiveVariances()) {
            String nameOfFitFunctionReturned = fOfX_SMOOTHING_SPLINE.getShortNameString();

            if (nameOfFitFunctionReturned.compareToIgnoreCase(FitFunctionTypeEnum.SMOOTHING_SPLINE.getName()) != 0) {
                sessionOfStandardsFitFunctionsNoOD.remove(FitFunctionTypeEnum.SMOOTHING_SPLINE.getName());
                sessionOfStandardsFitFunctionsWithOD.remove(FitFunctionTypeEnum.SMOOTHING_SPLINE.getName());
            } else {
                if (sessionOfStandardsFitFunctionsNoOD.containsKey(nameOfFitFunctionReturned)) {
                    AbstractFunctionOfX fOfXexist = sessionOfStandardsFitFunctionsNoOD.get(nameOfFitFunctionReturned);
                    fOfXexist.copyValuesFrom(fOfX_SMOOTHING_SPLINE);
                } else {
                    sessionOfStandardsFitFunctionsNoOD.put(nameOfFitFunctionReturned, fOfX_SMOOTHING_SPLINE);
                }

                if (sessionOfStandardsFitFunctionsWithOD.containsKey(nameOfFitFunctionReturned)) {
//                    AbstractFunctionOfX fOfXexist = sessionOfStandardsFitFunctionsWithOD.get(nameOfFitFunctionReturned);
                    // oct 2014 use overdispersion from LINE function
                    double myOverDispersion = 0.0;//fOfXexist.getOverDispersion();
                    //if (myOverDispersion == 0.0) {
                    if (sessionOfStandardsFitFunctionsWithOD.containsKey(FitFunctionTypeEnum.LINE.getName())) {
                        myOverDispersion = sessionOfStandardsFitFunctionsWithOD.get(FitFunctionTypeEnum.LINE.getName()).getOverDispersion();
                    }
//                        try {
//                            myOverDispersion = sessionOfStandardsFitFunctionsWithOD.get(FitFunctionTypeEnum.LINE.getName()).getOverDispersion();
//                        } catch (Exception e) {
//                        }
                    //}
                    AbstractFunctionOfX fOfXexistReplace = generateSplineWithSpecificOD(Math.floor(myOverDispersion * 0.95 * 1000000.0) / 1000000.0);            //fOfXexist.getOverDispersion() );
                    sessionOfStandardsFitFunctionsWithOD.put(nameOfFitFunctionReturned, fOfXexistReplace);
                } else {
                    // copy in the non od version 
                    SmoothingSplineWithCov splineInstance = SmoothingSplineWithCov.getInstance();
                    AbstractFunctionOfX splinePlaceHolderWithOD = splineInstance.getInstanceOfFitFunction();
                    splinePlaceHolderWithOD.copyValuesFrom(fOfX_SMOOTHING_SPLINE);
                    splinePlaceHolderWithOD.setOverDispersionSelected(true);
                    sessionOfStandardsFitFunctionsWithOD.put(nameOfFitFunctionReturned, splinePlaceHolderWithOD);
                }
            }
        } else {
            sessionOfStandardsFitFunctionsNoOD.remove(FitFunctionTypeEnum.SMOOTHING_SPLINE.getName());
            sessionOfStandardsFitFunctionsWithOD.remove(FitFunctionTypeEnum.SMOOTHING_SPLINE.getName());
            selectedFitFunctionType = FitFunctionTypeEnum.LINE; // exp prob doesnt fit here
        }
    }

    /**
     *
     * @param inLiveMode the value of inLiveMode
     */
    private void calculateFitFunctions(boolean inLiveMode) {
        System.out.println("\nCalculate Fit Functions for Session  " + getRawRatioModelName().getDisplayName() //
                + "  USING " + (USING_FULL_PROPAGATION ? "FULL PROPAGATION" : "FAST PROPAGATION"));

        if (generateMEANfitFunctionUsingLM()) {
            try {
                generateLINEfitFunctionUsingLM();
            } catch (Exception e) {
            }

            // June 2016 in support of live mode
            if (inLiveMode && rawRatioName.compareTo(RawRatioNames.r206_207w) == 0) {
                selectedFitFunctionType = FitFunctionTypeEnum.MEAN;
            }

            // June 2016 - speed up for live mode
            if (!inLiveMode) {
                try {
                    generateEXPONENTIALfitFunctionUsingLM();
                } catch (Exception e) {
                }
                try {
                    generateSPLINEfitFunction(sessionIncludedStandardsCovariance);
                } catch (Exception e) {
                }
            }
        }

////        if (saveSelectedFitFunctionType != null) {
////            selectedFitFunctionType = saveSelectedFitFunctionType;
////        }
        System.out.println("Session complete\n");

    }

    /**
     *
     * @param startValue
     * @param increment
     * @param stopValue
     * @return
     */
    public SortedMap<Double, AbstractFunctionOfX> generateSetOfSplineWithODfitFunctions(double startValue, double increment, double stopValue) {

        //SortedMap<Double, AbstractFunctionOfX> sessionOfStandardsSplinesWithOD = new TreeMap<>();
        sessionSplineRangeOD = new SessionSplineRangeOD();

        // safety valve
        if (increment == 0) {
            increment = (stopValue - startValue) / 10.0;
        }
        for (double i = startValue; i < stopValue; i += increment) {
            AbstractFunctionOfX fOfX_SMOOTHING_SPLINE = generateSplineWithSpecificOD(Math.pow(i, 2));

            System.out.println("OD TEST xi = " + i + "  logRoughness = " + fOfX_SMOOTHING_SPLINE.getLogRoughness() + "\n");

//            // stop processing if roughness bad
//            if (Double.isNaN(fOfX_SMOOTHING_SPLINE.getLogRoughness())) {//|| (fOfX_SMOOTHING_SPLINE.getLogRoughness() < -41.0)) {
//
//                try {
//                    fOfX_SMOOTHING_SPLINE = sessionOfStandardsSplinesWithOD.get(sessionOfStandardsSplinesWithOD.lastKey());
//                    AbstractFunctionOfX fOfX_SMOOTHING_SPLINE_EXIST = sessionOfStandardsFitFunctionsWithOD.get(fOfX_SMOOTHING_SPLINE.getShortNameString());
//                    if (fOfX_SMOOTHING_SPLINE_EXIST != null) {
//                        fOfX_SMOOTHING_SPLINE_EXIST.copyValuesFrom(fOfX_SMOOTHING_SPLINE);
//                    } else {
//                        sessionOfStandardsFitFunctionsWithOD.put(fOfX_SMOOTHING_SPLINE.getShortNameString(), fOfX_SMOOTHING_SPLINE);
//                    }
//                } catch (Exception e) {
//                }
//
//                break;
//            } else {
//                sessionOfStandardsSplinesWithOD.put(Math.sqrt(fOfX_SMOOTHING_SPLINE.getOverDispersion()), fOfX_SMOOTHING_SPLINE);
//            }
            // oct 2014 new take
            // stop processing if roughness bad
            if (!Double.isFinite(fOfX_SMOOTHING_SPLINE.getLogRoughness())) {//|| (fOfX_SMOOTHING_SPLINE.getLogRoughness() < -41.0)) {

//                try {
//                    // get last good one if exists
//                    fOfX_SMOOTHING_SPLINE = sessionSplineRangeOD.getLastSessionSplineFofX();//             sessionOfStandardsSplinesWithOD.get(sessionOfStandardsSplinesWithOD.lastKey());
//                    AbstractFunctionOfX fOfX_SMOOTHING_SPLINE_EXIST = sessionOfStandardsFitFunctionsWithOD.get(fOfX_SMOOTHING_SPLINE.getShortNameString());
//                    if (fOfX_SMOOTHING_SPLINE_EXIST != null) {
//                        fOfX_SMOOTHING_SPLINE_EXIST.copyValuesFrom(fOfX_SMOOTHING_SPLINE);
//                    } else {
//                        sessionOfStandardsFitFunctionsWithOD.put(fOfX_SMOOTHING_SPLINE.getShortNameString(), fOfX_SMOOTHING_SPLINE);
//                    }
//                } catch (Exception e) {
//                }
                break;
            } else {
                //sessionOfStandardsSplinesWithOD.put(Math.sqrt(fOfX_SMOOTHING_SPLINE.getOverDispersion()), fOfX_SMOOTHING_SPLINE);
                sessionSplineRangeOD.putSplineInSessionSplines(Math.sqrt(fOfX_SMOOTHING_SPLINE.getOverDispersion()), fOfX_SMOOTHING_SPLINE);
            }
        }

        //return sessionOfStandardsSplinesWithOD;
        return sessionSplineRangeOD.getSessionOfStandardsSplinesWithOD();

    }

    private AbstractFunctionOfX generateSplineWithSpecificOD(double overDispersion) {
        int rowColCount = sessionIncludedStandardsCovariance.getRowDimension();

        Matrix sessionIncludedStandardsCovarianceCopyWithOD
                = //
                sessionIncludedStandardsCovariance.plus(Matrix.identity(rowColCount, rowColCount).times(overDispersion));

        AbstractFunctionOfX fOfX_SMOOTHING_SPLINE_WITH_OD = SmoothingSplineWithCov.getInstance().getFunctionOfX(//
                includedStandardsDataActiveMap, //
                includedStandardsAquireTimes, //
                includedStandardsMeanLogRatios,//
                sessionIncludedStandardsCovarianceCopyWithOD, true);

        fOfX_SMOOTHING_SPLINE_WITH_OD.setOverDispersion(overDispersion);
        fOfX_SMOOTHING_SPLINE_WITH_OD.setOverDispersionSelected(true);

        return fOfX_SMOOTHING_SPLINE_WITH_OD;
    }

    /**
     *
     * @return
     */
    @Override
    public Map<String, AbstractFunctionOfX> getFitFunctions() {

        Map<String, AbstractFunctionOfX> fitFunctions;

        if (overDispersionSelected) {
            fitFunctions = sessionOfStandardsFitFunctionsWithOD;
        } else {
            fitFunctions = sessionOfStandardsFitFunctionsNoOD;
        }

        return fitFunctions;
    }

//    /**
//     *
//     * @param fitFunctionTypeName
//     */
//    @Override
//    public void calculateFittedFunctions(String fitFunctionTypeName) {
//    }
    /**
     *
     * @param selectedFitFunctionType
     */
    @Override
    public void setSelectedFitFunctionType(FitFunctionTypeEnum selectedFitFunctionType) {
        this.selectedFitFunctionType = selectedFitFunctionType;
    }

    /**
     *
     * @return
     */
    @Override
    public FitFunctionTypeEnum getSelectedFitFunctionType() {
        return selectedFitFunctionType;
    }

    /**
     *
     * @return
     */
    @Override
    public AbstractFunctionOfX getSelectedFitFunction() {
        AbstractFunctionOfX fitFunc;
        if (overDispersionSelected) {
            fitFunc = sessionOfStandardsFitFunctionsWithOD.get(selectedFitFunctionType.getName());
        } else {
            fitFunc = sessionOfStandardsFitFunctionsNoOD.get(selectedFitFunctionType.getName());
        }

        return fitFunc;
    }

    @Override
    public AbstractFunctionOfX getSelectedDownHoleFitFunction() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param fitFunctionType
     * @return
     */
    @Override
    public boolean doesFitFunctionTypeHaveOD(FitFunctionTypeEnum fitFunctionType) {

        boolean retVal = false;
        AbstractFunctionOfX fitFunc = sessionOfStandardsFitFunctionsWithOD.get(fitFunctionType.getName());
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
            return Math.sqrt(sessionOfStandardsFitFunctionsWithOD.get(fitFunctionType.getName()).getOverDispersion());
        }

        return retVal;

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
            contains = sessionOfStandardsFitFunctionsWithOD.get(fitFunctionType.getName()) != null;
        } else {
            contains = sessionOfStandardsFitFunctionsNoOD.get(fitFunctionType.getName()) != null;
        }

        return contains;
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
     * @return the includedStandardsAquireTimes
     */
    public double[] getIncludedStandardsAquireTimes() {
        return includedStandardsAquireTimes.clone();
    }

    /**
     * @return the includedStandardsMeanLogRatios
     */
    public double[] getIncludedStandardMeanLogRatios() {
        return includedStandardsMeanLogRatios.clone();
    }

    /**
     * @return the includedStandardsMeanLogRatioStdErrs
     */
    public double[] getIncludedStandardsMeanLogRatioStdErrs() {
        return includedStandardsMeanLogRatioStdErrs.clone();
    }

    /**
     * @return the standardFractions
     */
    public SortedSet<TripoliFraction> getStandardFractions() {
        return standardFractions;
    }

    /**
     *
     * @return
     */
    public SortedSet<TripoliFraction> getActiveStandardFractions() {
        SortedSet<TripoliFraction> activeStandards = new TreeSet<>();
        Iterator<TripoliFraction> fractionIterator = standardFractions.iterator();
        while (fractionIterator.hasNext()) {
            TripoliFraction tf = fractionIterator.next();
            if (tf.isIncluded()) {
                activeStandards.add(tf);
            }
        }

        return activeStandards;
    }

////    /**
////     * @return the includedStandardsDataActiveMap
////     */
////    public boolean[] getDataActiveMap() {
////        return includedStandardsDataActiveMap;
////    }
    /**
     * @return the sessionTechnique
     */
    public String getSessionTechnique() {
        return sessionTechnique;
    }

    /**
     * @return the standardValue
     */
    public double getStandardValue() {
        return standardValue;
    }

    /**
     * @return the sessionIncludedStandardsCovariance
     */
    public Matrix getSessionIncludedStandardsCovariance() {
        return sessionIncludedStandardsCovariance;
    }

    /**
     * @return the calculatedInitialFitFunctions
     */
    @Override
    public boolean isCalculatedInitialFitFunctions() {
        return calculatedInitialFitFunctions;
    }

    /**
     * @return the tripoliSession
     */
    public TripoliSessionInterface getTripoliSession() {
        return tripoliSession;
    }

    /**
     * @return the overDispersionSelected
     */
    @Override
    public boolean isOverDispersionSelected() {
        return overDispersionSelected;
    }

    /**
     * @param overDispersionSelected the overDispersionSelected to set
     */
    @Override
    public void setOverDispersionSelected(boolean overDispersionSelected) {
        this.overDispersionSelected = overDispersionSelected;
    }

    /**
     * @return the allStandardsMeanLogRatios
     */
    public double[] getAllStandardsMeanLogRatios() {
        return allStandardsMeanLogRatios.clone();
    }

    /**
     * @return the allStandardsMeanLogRatioStdErrs
     */
    public double[] getAllStandardsMeanLogRatioStdErrs() {
        return allStandardsMeanLogRatioStdErrs.clone();
    }

    /**
     * @param standardFractions the standardFractions to set
     */
    public void setStandardFractions(SortedSet<TripoliFraction> standardFractions) {
        this.standardFractions = standardFractions;
    }

    /**
     * @return the sessionOfStandardsFitFunctionsWithOD
     */
    public Map<String, AbstractFunctionOfX> getSessionOfStandardsFitFunctionsWithOD() {
        return sessionOfStandardsFitFunctionsWithOD;
    }
}
