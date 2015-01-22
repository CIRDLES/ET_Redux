/*
 * RawIntensityDataModel.java
 *
 * Created Jul 5, 2011
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
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import org.earthtime.Tripoli.dataModels.collectorModels.AbstractCollectorModel;
import org.earthtime.Tripoli.fitFunctions.AbstractFunctionOfX;
import org.earthtime.Tripoli.fitFunctions.ConstantFitFunctionWithCovS;
import org.earthtime.UPb_Redux.utilities.comparators.IntuitiveStringComparator;
import org.earthtime.dataDictionaries.FitFunctionTypeEnum;
import org.earthtime.dataDictionaries.IsotopeNames;
import org.earthtime.dataDictionaries.RawRatioNames;

/**
 *
 * @author James F. Bowring
 */
public class RawIntensityDataModel //
        implements Serializable, //
        Comparable<RawIntensityDataModel>, DataModelInterface, DataModelFitFunctionInterface {

    // Class variables
    private static final long serialVersionUID = -4497425710276767467L;

    /**
     *
     */
    private boolean USING_FULL_PROPAGATION;
    // instance variables
    private final IsotopeNames rawIsotopeModelName;
    private final VirtualCollectorModel backgroundVirtualCollector;
    private final VirtualCollectorModel onPeakVirtualCollector;
    private long COLLECTOR_DATA_FREQUENCY_MILLISECS;
    // oct 2012
    private final AbstractCollectorModel collectorModel;
    // used for individual background fitting
//    private Map<String, AbstractFunctionOfX> backgroundFitFunctions;
    private FitFunctionTypeEnum selectedFitFunctionType;
    private transient double[] normalizedBackgroundAquireTimes;
    // only need for calculations
    private transient Matrix matrixSiCovarianceIntensities;
    private transient Matrix matrixSibCovarianceBackgroundIntensities;
    // June 2013 used for fast processing (will temporarily be inside square matrices)
    private transient Matrix vectorSviVarianceIntensities;
    private transient Matrix vectorSviVarianceBackgroundIntensities;
    // used for individual background fitting
    private final Map<String, AbstractFunctionOfX> backgroundFitFunctionsNoOD;
    private final Map<String, AbstractFunctionOfX> backgroundFitFunctionsWithOD;

    /**
     *
     */
    protected boolean overDispersionSelected;
    private boolean belowDetection;
    private transient Matrix J11;
    private transient Matrix J21;
    private transient Matrix J22;
    private transient Matrix JOnPeak;
    private transient Matrix Sopbc;
    private transient Matrix Jlogr;
    private transient Matrix Jmat;
    private transient Matrix Sopbclr;
    private boolean calculatedInitialFitFunctions;
    // june 2013 - only not null for Pb204
    private Matrix correctedHg202Si;
    private double[] diagonalOfMatrixSIntensities;
    private double[] allItensities;
    // nov 2014 - see validateOnPeakBaselineCorrectedIsotope inn abstractMassSpecSetup
    private boolean forceMeanForCommonLeadRatios;
    protected double forcedMeanForCommonLeadRatios;

    /**
     *
     * @param rawIsotopeModelName
     * @param backgroundCollector
     * @param onPeakCollector
     * @param collectorModel
     * @param collectorDataFrequencyMillisecs
     */
    public RawIntensityDataModel(//
            IsotopeNames rawIsotopeModelName,//
            VirtualCollectorModel backgroundCollector, //
            VirtualCollectorModel onPeakCollector,//
            long collectorDataFrequencyMillisecs,
            AbstractCollectorModel collectorModel) {
        this.rawIsotopeModelName = rawIsotopeModelName;
        this.backgroundVirtualCollector = backgroundCollector;
        this.onPeakVirtualCollector = onPeakCollector;
        this.COLLECTOR_DATA_FREQUENCY_MILLISECS = collectorDataFrequencyMillisecs;

        this.collectorModel = collectorModel;

        this.USING_FULL_PROPAGATION = true;

        //this.backgroundFitFunctions = new TreeMap<String, AbstractFunctionOfX>();
        this.selectedFitFunctionType = FitFunctionTypeEnum.MEAN;
        this.overDispersionSelected = true;

        this.normalizedBackgroundAquireTimes = new double[0];

        this.matrixSiCovarianceIntensities = null;
        this.matrixSibCovarianceBackgroundIntensities = null;
        this.vectorSviVarianceIntensities = null;
        this.vectorSviVarianceBackgroundIntensities = null;

        this.belowDetection = false;

        this.Sopbclr = null;

        this.calculatedInitialFitFunctions = false;

        this.backgroundFitFunctionsNoOD = new TreeMap<String, AbstractFunctionOfX>();
        this.backgroundFitFunctionsWithOD = new TreeMap<String, AbstractFunctionOfX>();

        this.correctedHg202Si = null;
        this.forceMeanForCommonLeadRatios = false;
    }

    /**
     *
     * @param im
     * @return
     */
    @Override
    public int compareTo(RawIntensityDataModel im) {
        String imName = im.getIsotope().getName();
        String myName = this.getIsotope().getName();

        Comparator<String> intuitiveString = new IntuitiveStringComparator<>();
        return intuitiveString.compare(myName, imName);
    }

    /**
     *
     */
    public void correctIntensitiesForResistor() {
        // added may 2014 to handle a correction noah left out for raw intensities
        double[] backgroundIntensitiesCorrectedForResistor = //
                collectorModel.correctRawIntensitiesForResistor(backgroundVirtualCollector.getIntensities());
        backgroundVirtualCollector.setIntensities(backgroundIntensitiesCorrectedForResistor);

        double[] onPeakIntensitiesCorrectedForResistor = //
                collectorModel.correctRawIntensitiesForResistor(onPeakVirtualCollector.getIntensities());
        onPeakVirtualCollector.setIntensities(onPeakIntensitiesCorrectedForResistor);

    }

    // TODO: equals and hashcode
    /**
     *
     */
    public void convertRawIntensitiesToCountsPerSecond() {
        // dec 2012
        double[] backgroundIntensitiesCountsPerSecond = //
                collectorModel.convertRawIntensitiesToCountsPerSecond(backgroundVirtualCollector.getIntensities());
        backgroundVirtualCollector.setIntensities(backgroundIntensitiesCountsPerSecond);

        double[] onPeakIntensitiesCountsPerSecond = //
                collectorModel.convertRawIntensitiesToCountsPerSecond(onPeakVirtualCollector.getIntensities());
        onPeakVirtualCollector.setIntensities(onPeakIntensitiesCountsPerSecond);
    }

    /**
     *
     * @return
     */
    public String outputIntensities() {
        StringBuilder retval = new StringBuilder(//
                rawIsotopeModelName.getName() + " [" + collectorModel.getCollectorType() + "]\n");
        retval.append("\tBack:\t");
        for (int i = 0; i < backgroundVirtualCollector.getIntensities().length; i++) {
            retval.append(backgroundVirtualCollector.getIntensities()[i] + ", ");
        }
        retval.append("\n");
        retval.append("\tPeak:\t");
        for (int i = 0; i < onPeakVirtualCollector.getIntensities().length; i++) {
            retval.append(onPeakVirtualCollector.getIntensities()[i] + ", ");
        }

        return retval.toString();
    }

    /**
     *
     * @return
     */
    public String outputCorrectedIntensities() {
        StringBuilder retval = new StringBuilder(//
                rawIsotopeModelName.getName() + " [" + collectorModel.getCollectorType() + "]\n");

        retval.append("\tBack:\t");
        for (int i = 0; i < onPeakVirtualCollector.getCorrectedIntensities().length; i++) {
            retval.append(onPeakVirtualCollector.getCorrectedIntensities()[i] + ", ");
        }

        return retval.toString();
    }

    /**
     *
     * @return
     */
    public String outputCorrectedIntensitiesAsLogs() {
        StringBuilder retval = new StringBuilder(//
                rawIsotopeModelName.getName() + " [" + collectorModel.getCollectorType() + "]\n");

        retval.append("\tBack:\t");
        for (int i = 0; i < onPeakVirtualCollector.getLogCorrectedIntensities().length; i++) {
            retval.append(onPeakVirtualCollector.getLogCorrectedIntensities()[i] + ", ");
        }

        return retval.toString();
    }

    /**
     *
     * @return
     */
    public String outputBaseLineFitFunctionParameters() {
        String retval = rawIsotopeModelName.getName() + "\n";
        AbstractFunctionOfX fOfXcurrent = getBackgroundFitFunctions().get(selectedFitFunctionType.getName());
        retval += fOfXcurrent.showParameters() + "\n";

        return retval;
    }

    /**
     *
     * @return
     */
    public double[] getBackgroundCountsPerSecondAsRawIntensities() {
        return collectorModel.convertCountsPerSecondToRawIntensities(backgroundVirtualCollector.getIntensities());
    }

    /**
     *
     * @return
     */
    public double[] getBackgroundCountsPerSecondCorrectionsAsRawIntensities() {
        return collectorModel.convertCountsPerSecondToRawIntensities(backgroundVirtualCollector.getIntensityCorrections());
    }

    /**
     *
     * @return
     */
    public double[] getBackgroundFitCountsPerSecondAsRawIntensities() {
        return collectorModel.convertCountsPerSecondToRawIntensities(backgroundVirtualCollector.getFitBackgroundIntensities());
    }

    /**
     *
     * @return
     */
    public double[] getOnPeakCountsPerSecondAsRawIntensities() {
        return collectorModel.convertCountsPerSecondToRawIntensities(onPeakVirtualCollector.getIntensities());
    }

    /**
     *
     * @return
     */
    public double[] getOnPeakCountsPerSecondCorrectionsAsRawIntensities() {
        return collectorModel.convertCountsPerSecondToRawIntensities(onPeakVirtualCollector.getIntensityCorrections());
    }

    /**
     *
     * @return
     */
    public double[] getOnPeakFitCountsPerSecondAsRawIntensities() {
        return collectorModel.convertCountsPerSecondToRawIntensities(onPeakVirtualCollector.getFitBackgroundIntensities());
    }

    /**
     *
     * @return
     */
    public double[] getOnPeakCorrectedCountsPerSecondAsRawIntensities() {
        return collectorModel.convertCountsPerSecondToRawIntensities(onPeakVirtualCollector.getCorrectedIntensities());
    }

    /**
     *
     * @param integrationTime
     */
    public void calculateIntensityMatrixSDiagonal(double integrationTime) {
        // task is to build diagonal from baseline and ablation raw intensities and modify
        // it with collector values for each isotope
        // the modification is done in collector classes by type

        double[] baselineIntensities = backgroundVirtualCollector.getIntensities();
        double[] ablationIntensities = onPeakVirtualCollector.getIntensities();

        allItensities = new double[baselineIntensities.length + ablationIntensities.length];
        System.arraycopy(baselineIntensities, 0, allItensities, 0, baselineIntensities.length);
        System.arraycopy(ablationIntensities, 0, allItensities, baselineIntensities.length, ablationIntensities.length);

        diagonalOfMatrixSIntensities = collectorModel.calculateMeasuredCountsAndMatrixSIntensityDiagonal( //
                baselineIntensities.length, allItensities, integrationTime).clone();

//        if (collectorModel instanceof IonCounterCollectorModel){
//            measuredIntensityIonCounter = ((IonCounterCollectorModel)collectorModel).getMeasuredIntensityIonCounter().clone();
//        } else {
//            measuredIntensityIonCounter = new double[0];
//        }
    }

    /**
     *
     * @return
     */
    @Override
    public double[] getNormalizedOnPeakAquireTimes() {
        double[] normalizedAquire =//
                onPeakVirtualCollector.getOnPeakAquireTimes().clone();

        for (int i = 0; i < normalizedAquire.length; i++) {
            normalizedAquire[i] /= COLLECTOR_DATA_FREQUENCY_MILLISECS;
        }

        return normalizedAquire;
    }

    /**
     *
     * @return
     */
    @Override
    public double[] getOnPeakAquireTimesInSeconds() {
        double[] onPeakAquireTimesInSeconds = //
                onPeakVirtualCollector.getOnPeakAquireTimes().clone();

        for (int i = 0; i < onPeakAquireTimesInSeconds.length; i++) {
            onPeakAquireTimesInSeconds[i] = onPeakAquireTimesInSeconds[i] /= 1000.0;
        }

        return onPeakAquireTimesInSeconds;
    }

    /**
     *
     * @return
     */
    public double[] getNormalizedBackgroundAquireTimes() {
        double[] normalizedAquire =//
                backgroundVirtualCollector.getBackgroundAquireTimes().clone();

        double shiftFactor = normalizedAquire[normalizedAquire.length - 1] / COLLECTOR_DATA_FREQUENCY_MILLISECS + 1;

        for (int i = 0; i < normalizedAquire.length; i++) {
            normalizedAquire[i] = (normalizedAquire[i] / COLLECTOR_DATA_FREQUENCY_MILLISECS) - shiftFactor;
        }

        return normalizedAquire;
    }

    /**
     *
     * @param index
     * @param included
     */
    @Override
    public void toggleOneDataAquisition(int index, boolean included) {
        onPeakVirtualCollector.toggleOneDataAquisition(index, included);
    }

    /**
     *
     */
    @Override
    public void applyMaskingArray() {
        onPeakVirtualCollector.setDataActiveMap(MaskingSingleton.getInstance().applyMask(onPeakVirtualCollector.getDataActiveMap()));//.getMaskingArray().clone());
    }

    /**
     * @return the rawIsotopeModelName
     */
    public IsotopeNames getIsotope() {
        return rawIsotopeModelName;
    }

    /**
     * @return the backgroundVirtualCollector
     */
    public VirtualCollectorModel getBackgroundVirtualCollector() {
        return backgroundVirtualCollector;
    }

    /**
     * @return the onPeakVirtualCollector
     */
    public VirtualCollectorModel getOnPeakVirtualCollector() {
        return onPeakVirtualCollector;
    }

    /**
     *
     * @param CollectorDataFrequencyMillisecs
     */
    @Override
    public void setCollectorDataFrequencyMillisecs(long CollectorDataFrequencyMillisecs) {
        COLLECTOR_DATA_FREQUENCY_MILLISECS = CollectorDataFrequencyMillisecs;
    }

    /**
     *
     * @return
     */
    @Override
    public long getCollectorDataFrequencyMillisecs() {
        return COLLECTOR_DATA_FREQUENCY_MILLISECS;
    }

    /**
     *
     * @return
     */
    @Override
    public String getDataModelName() {
        return rawIsotopeModelName.getName();
    }

    /**
     *
     * @return
     */
    @Override
    public RawRatioNames getRawRatioModelName() {
        // one-off case 
        //TODO: make RawRAtioName and IsotpeName enums have common ancestor???
        return null;
        //throw new UnsupportedOperationException( "Not supported yet." );
    }

    /**
     *
     */
    @Override
    public void calculateCorrectedRatioStatistics() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private void generateCONSTANTfitFunction() {
        // CONSTANT *********************************************************

        AbstractFunctionOfX fOfX_CONSTANT;

        if (USING_FULL_PROPAGATION) {
            fOfX_CONSTANT = ConstantFitFunctionWithCovS.getInstance().getFunctionOfX(//
                    backgroundVirtualCollector.getDataActiveMap(), //
                    normalizedBackgroundAquireTimes,//
                    backgroundVirtualCollector.getIntensities(), //
                    matrixSibCovarianceBackgroundIntensities, false);
        } else {
            fOfX_CONSTANT = ConstantFitFunctionWithCovS.getInstance().getFunctionOfX(//
                    backgroundVirtualCollector.getDataActiveMap(), //
                    normalizedBackgroundAquireTimes,//
                    backgroundVirtualCollector.getIntensities(), //
                    vectorSviVarianceBackgroundIntensities, false);
        }

        backgroundFitFunctionsWithOD.put("CONSTANT", fOfX_CONSTANT);
        backgroundFitFunctionsNoOD.put("CONSTANT", fOfX_CONSTANT);

    }

//    private void generateMEANfitFunction() {
//        // MEAN *********************************************************
//        AbstractFunctionOfX fOfX_MEAN = MeanFitFunctionWithCovS.getInstance().getFunctionOfX(//
//                backgroundVirtualCollector.getDataActiveMap(), //
//                normalizedBackgroundAquireTimes,//
//                backgroundVirtualCollector.getIntensities(), //
//                matrixSibCovarianceBackgroundIntensities, false);
//
//        backgroundFitFunctionsWithOD.put("MEAN", fOfX_MEAN);
//        backgroundFitFunctionsNoOD.put("MEAN", fOfX_MEAN);
//
//        selectedFitFunctionType = FitFunctionTypeEnum.MEAN;
//    }

//    private boolean generateMEANfitFunctionUsingLM() {
//
//        boolean retVal;
//
//        // algorithmForMEAN contains both the non OD and OD versions
//        AbstractFunctionOfX fOfX_MEAN;
//        AbstractFunctionOfX fOfX_MEAN_OD;
//
//        if (USING_FULL_PROPAGATION) {
//            LevenbergMarquardGeneralSolverWithCovS.AbstractOverDispersionLMAlgorithm algorithmForMEAN//
//                    = LevenbergMarquardGeneralSolverWithCovS.getInstance()//
//                    .getSelectedLMAlgorithm( //
//                            FitFunctionTypeEnum.MEAN,//
//                            backgroundVirtualCollector.getDataActiveMap(), //
//                            normalizedBackgroundAquireTimes,//
//                            backgroundVirtualCollector.getIntensities(), //
//                            matrixSibCovarianceBackgroundIntensities, false);
//
//            fOfX_MEAN = algorithmForMEAN.getInitialFofX();
//            fOfX_MEAN_OD = algorithmForMEAN.getFinalFofX();
//
//        } else {
//
//            LevenbergMarquardGeneralSolverWithVecV.AbstractOverDispersionLMVecAlgorithm algorithmForMEAN//
//                    = LevenbergMarquardGeneralSolverWithVecV.getInstance()//
//                    .getSelectedLMAlgorithm( //
//                            FitFunctionTypeEnum.MEAN,//
//                            backgroundVirtualCollector.getDataActiveMap(), //
//                            normalizedBackgroundAquireTimes,//
//                            backgroundVirtualCollector.getIntensities(), //
//                            vectorSviVarianceBackgroundIntensities, false);
//
//            fOfX_MEAN = algorithmForMEAN.getInitialFofX();
//            fOfX_MEAN_OD = algorithmForMEAN.getFinalFofX();
//
//        }
//
//        if ((fOfX_MEAN != null) && fOfX_MEAN.verifyPositiveVariances()) {
//            if (backgroundFitFunctionsNoOD.containsKey(fOfX_MEAN.getShortNameString())) {
//                AbstractFunctionOfX fOfXexist = backgroundFitFunctionsNoOD.get(fOfX_MEAN.getShortNameString());
//                fOfXexist.copyValuesFrom(fOfX_MEAN);
//            } else {
//                backgroundFitFunctionsNoOD.put(fOfX_MEAN.getShortNameString(), fOfX_MEAN);
//            }
//
//            if ((fOfX_MEAN_OD != null) && fOfX_MEAN_OD.verifyPositiveVariances()) {
//                if (backgroundFitFunctionsWithOD.containsKey(fOfX_MEAN_OD.getShortNameString())) {
//                    AbstractFunctionOfX fOfXexist = backgroundFitFunctionsWithOD.get(fOfX_MEAN_OD.getShortNameString());
//                    fOfXexist.copyValuesFrom(fOfX_MEAN_OD);
//                } else {
//                    backgroundFitFunctionsWithOD.put(fOfX_MEAN_OD.getShortNameString(), fOfX_MEAN_OD);
//                }
//            } else {
//                backgroundFitFunctionsWithOD.put(fOfX_MEAN.getShortNameString(), fOfX_MEAN);
//            }
//
//            retVal = true;
//
//        } else {
//            // to handle really bad data sets, for which LM wont work, do good old fashioned mean
//            System.out.println("LM would not fit mean , so using arithmetic mean fit");
//            fOfX_MEAN = MeanFitFunction.getInstance()//
//                    .getFunctionOfX(//
//                            backgroundVirtualCollector.getDataActiveMap(), //
//                            normalizedBackgroundAquireTimes,//
//                            backgroundVirtualCollector.getIntensities(), //
//                            null, //
//                            false);
//
//            backgroundFitFunctionsNoOD.put(FitFunctionTypeEnum.MEAN.getName(), fOfX_MEAN);
//            backgroundFitFunctionsWithOD.put(FitFunctionTypeEnum.MEAN.getName(), fOfX_MEAN);
//
//            selectedFitFunctionType = FitFunctionTypeEnum.MEAN;
//
//            retVal = false;
//        }
//
//        return retVal;
//    }
//
//    private void generateLINEfitFunctionUsingLM() {
//
//        // algorithmForLINE contains both the non OD and OD versions
//        AbstractFunctionOfX fOfX_LINE;
//        AbstractFunctionOfX fOfX_LINE_OD;
//
//        if (USING_FULL_PROPAGATION) {
//            LevenbergMarquardGeneralSolverWithCovS.AbstractOverDispersionLMAlgorithm algorithmForLINE = LevenbergMarquardGeneralSolverWithCovS.getInstance()//
//                    .getSelectedLMAlgorithm( //
//                            FitFunctionTypeEnum.LINE,//
//                            backgroundVirtualCollector.getDataActiveMap(), //
//                            normalizedBackgroundAquireTimes,//
//                            backgroundVirtualCollector.getIntensities(), //
//                            matrixSibCovarianceBackgroundIntensities, false);
//
//            fOfX_LINE = algorithmForLINE.getInitialFofX();
//            fOfX_LINE_OD = algorithmForLINE.getFinalFofX();
//
//        } else {
//            LevenbergMarquardGeneralSolverWithVecV.AbstractOverDispersionLMVecAlgorithm algorithmForLINE = LevenbergMarquardGeneralSolverWithVecV.getInstance()//
//                    .getSelectedLMAlgorithm( //
//                            FitFunctionTypeEnum.LINE,//
//                            backgroundVirtualCollector.getDataActiveMap(), //
//                            normalizedBackgroundAquireTimes,//
//                            backgroundVirtualCollector.getIntensities(), //
//                            vectorSviVarianceBackgroundIntensities, false);
//
//            fOfX_LINE = algorithmForLINE.getInitialFofX();
//            fOfX_LINE_OD = algorithmForLINE.getFinalFofX();
//        }
//
//        if ((fOfX_LINE != null) && fOfX_LINE.verifyPositiveVariances()) {
//            if (backgroundFitFunctionsNoOD.containsKey(fOfX_LINE.getShortNameString())) {
//                AbstractFunctionOfX fOfXexist = backgroundFitFunctionsNoOD.get(fOfX_LINE.getShortNameString());
//                fOfXexist.copyValuesFrom(fOfX_LINE);
//            } else {
//                backgroundFitFunctionsNoOD.put(fOfX_LINE.getShortNameString(), fOfX_LINE);
//            }
//
//            if ((fOfX_LINE_OD != null) && fOfX_LINE_OD.verifyPositiveVariances()) {
//                if (backgroundFitFunctionsWithOD.containsKey(fOfX_LINE_OD.getShortNameString())) {
//                    AbstractFunctionOfX fOfXexist = backgroundFitFunctionsWithOD.get(fOfX_LINE_OD.getShortNameString());
//                    fOfXexist.copyValuesFrom(fOfX_LINE_OD);
//                } else {
//                    backgroundFitFunctionsWithOD.put(fOfX_LINE_OD.getShortNameString(), fOfX_LINE_OD);
//                }
//            } else {
//                backgroundFitFunctionsWithOD.put(fOfX_LINE.getShortNameString(), fOfX_LINE);
//            }
//        } else {
//            backgroundFitFunctionsNoOD.remove(FitFunctionTypeEnum.LINE.getName());
//            backgroundFitFunctionsWithOD.remove(FitFunctionTypeEnum.LINE.getName());
//            selectedFitFunctionType = FitFunctionTypeEnum.MEAN;
//        }
//    }
//
//    private void generateEXPONENTIALfitFunctionUsingLM() {
//
//        System.out.println("trying expfast");
//
//        AbstractFunctionOfX fOfX_ExpFast = null;
//
//        if (USING_FULL_PROPAGATION) {
//            LevenbergMarquardGeneralSolverWithCovS.AbstractOverDispersionLMAlgorithm algorithmForEXPFAST//
//                    = LevenbergMarquardGeneralSolverWithCovS.getInstance().getSelectedLMAlgorithm(//
//                            FitFunctionTypeEnum.EXPFAST,//
//                            backgroundVirtualCollector.getDataActiveMap(), //
//                            normalizedBackgroundAquireTimes,//
//                            backgroundVirtualCollector.getIntensities(), //
//                            matrixSibCovarianceBackgroundIntensities, false);
//
//            fOfX_ExpFast = algorithmForEXPFAST.getInitialFofX();
//
//        } else {
//
//            LevenbergMarquardGeneralSolverWithVecV.AbstractOverDispersionLMVecAlgorithm algorithmForEXPFAST//
//                    = LevenbergMarquardGeneralSolverWithVecV.getInstance().getSelectedLMAlgorithm(//
//                            FitFunctionTypeEnum.EXPFAST,//
//                            backgroundVirtualCollector.getDataActiveMap(), //
//                            normalizedBackgroundAquireTimes,//
//                            backgroundVirtualCollector.getIntensities(), //
//                            vectorSviVarianceBackgroundIntensities, false);
//
//            fOfX_ExpFast = algorithmForEXPFAST.getInitialFofX();
//        }
//
//        AbstractOverDispersionLMAlgorithmInterface algorithmForEXPMAT;
//
//        if (fOfX_ExpFast != null) //
//        {
//            System.out.println("now trying expmat with expfast input");
//
//            if (USING_FULL_PROPAGATION) {
//                algorithmForEXPMAT = LevenbergMarquardGeneralSolverWithCovS.getInstance().getSelectedLMAlgorithmUsingIntialFofX(//
//                        FitFunctionTypeEnum.EXPMAT,//
//                        backgroundVirtualCollector.getDataActiveMap(), //
//                        normalizedBackgroundAquireTimes,//
//                        backgroundVirtualCollector.getIntensities(), //
//                        matrixSibCovarianceBackgroundIntensities, false, //
//                        fOfX_ExpFast);
//            } else {
//
//                algorithmForEXPMAT = LevenbergMarquardGeneralSolverWithVecV.getInstance().getSelectedLMAlgorithmUsingIntialFofX(//
//                        FitFunctionTypeEnum.EXPMAT,//
//                        backgroundVirtualCollector.getDataActiveMap(), //
//                        normalizedBackgroundAquireTimes,//
//                        backgroundVirtualCollector.getIntensities(), //
//                        vectorSviVarianceBackgroundIntensities, false, //
//                        fOfX_ExpFast);
//            }
//
//            AbstractFunctionOfX fOfX_EXPMAT = algorithmForEXPMAT.getFinalFofX();
//
//            if ((fOfX_EXPMAT != null) && (fOfX_EXPMAT.verifyPositiveVariances())) {
//                if (backgroundFitFunctionsNoOD.containsKey(fOfX_EXPMAT.getShortNameString())) {
//                    AbstractFunctionOfX fOfXexist = backgroundFitFunctionsNoOD.get(fOfX_EXPMAT.getShortNameString());
//                    fOfXexist.copyValuesFrom(fOfX_EXPMAT);
//                } else {
//                    backgroundFitFunctionsNoOD.put(fOfX_EXPMAT.getShortNameString(), fOfX_EXPMAT);
//                }
//
//                if (fOfX_EXPMAT.getMSWD() >= 1.0) {
//                    System.out.println("now trying expOD with expfast input");
//
//                    AbstractFunctionOfX fOfX_EXPOD = null;
//
//                    if (USING_FULL_PROPAGATION) {
//                        LevenbergMarquardGeneralSolverWithCovS.AbstractOverDispersionLMAlgorithm algorithmForEXPOD = LevenbergMarquardGeneralSolverWithCovS.getInstance().getSelectedLMAlgorithmUsingIntialFofX(//
//                                FitFunctionTypeEnum.EXPONENTIAL,//
//                                backgroundVirtualCollector.getDataActiveMap(), //
//                                normalizedBackgroundAquireTimes,//
//                                backgroundVirtualCollector.getIntensities(), //
//                                matrixSibCovarianceBackgroundIntensities, false, //
//                                fOfX_ExpFast);
//
//                        fOfX_EXPOD = algorithmForEXPOD.getFinalFofX();
//
//                    } else {
//                        LevenbergMarquardGeneralSolverWithVecV.AbstractOverDispersionLMVecAlgorithm algorithmForEXPOD = LevenbergMarquardGeneralSolverWithVecV.getInstance().getSelectedLMAlgorithmUsingIntialFofX(//
//                                FitFunctionTypeEnum.EXPONENTIAL,//
//                                backgroundVirtualCollector.getDataActiveMap(), //
//                                normalizedBackgroundAquireTimes,//
//                                backgroundVirtualCollector.getIntensities(), //
//                                vectorSviVarianceBackgroundIntensities, false, //
//                                fOfX_ExpFast);//algorithmForEXPFAST.getInitialFofX() );
//
//                        fOfX_EXPOD = algorithmForEXPOD.getFinalFofX();
//                    }
//
//                    if ((fOfX_EXPOD != null) && (fOfX_EXPOD.verifyPositiveVariances())) {
//
//                        if (backgroundFitFunctionsWithOD.containsKey(fOfX_EXPOD.getShortNameString())) {
//                            AbstractFunctionOfX fOfXexist = backgroundFitFunctionsWithOD.get(fOfX_EXPOD.getShortNameString());
//                            fOfXexist.copyValuesFrom(fOfX_EXPOD);
//                        } else {
//                            backgroundFitFunctionsWithOD.put(fOfX_EXPOD.getShortNameString(), fOfX_EXPOD);
//                        }
//                    }
//
//                } else {
//                    backgroundFitFunctionsWithOD.put(fOfX_EXPMAT.getShortNameString(), fOfX_EXPMAT);
//                }
//            } else {
//                backgroundFitFunctionsNoOD.remove(FitFunctionTypeEnum.EXPONENTIAL.getName());//was expmat??
//                backgroundFitFunctionsWithOD.remove(FitFunctionTypeEnum.EXPONENTIAL.getName());
//                selectedFitFunctionType = FitFunctionTypeEnum.LINE;
//            }
//        } else {
//            backgroundFitFunctionsNoOD.remove(FitFunctionTypeEnum.EXPONENTIAL.getName());//was expmat ??
//            backgroundFitFunctionsWithOD.remove(FitFunctionTypeEnum.EXPONENTIAL.getName());
//            selectedFitFunctionType = FitFunctionTypeEnum.LINE;
//        }
//    }

    /**
     *
     * @return
     */
    public Matrix specialBuildMatrixSiForHg202() {
        return collectorModel.buildMatrixSi(diagonalOfMatrixSIntensities, allItensities);
    }

    /**
     *
     */
    public void prepareDataForFitFunctions() {

        // nov 2012 calculate the SigmaI matrix for the background fit
        // first calculate the full matrix, then take upper left quadrant (assuming same number of background and foreground readings)
        if (USING_FULL_PROPAGATION) {

            matrixSiCovarianceIntensities = collectorModel.buildMatrixSi(diagonalOfMatrixSIntensities, allItensities);
            if ((correctedHg202Si != null) && rawIsotopeModelName.compareTo(IsotopeNames.Pb204) == 0) {
                matrixSiCovarianceIntensities.plusEquals(correctedHg202Si);
                //get rid of it
                correctedHg202Si = null;
            }

            // TODO: trim matrixSibCovarianceBackgroundIntensities to reflect any de-selected data
            int backgroundIntensityCount = backgroundVirtualCollector.getIntensities().length;
            matrixSibCovarianceBackgroundIntensities = matrixSiCovarianceIntensities//
                    .getMatrix(0, backgroundIntensityCount - 1, 0, backgroundIntensityCount - 1);
        } else {
            vectorSviVarianceIntensities = collectorModel.buildVectorSvi(diagonalOfMatrixSIntensities, allItensities);
            if ((correctedHg202Si != null) && rawIsotopeModelName.compareTo(IsotopeNames.Pb204) == 0) {
                vectorSviVarianceIntensities.plusEquals(correctedHg202Si);
                //get rid of it
                correctedHg202Si = null;
            }

            // TODO: trim matrixSibCovarianceBackgroundIntensities to reflect any de-selected data
            int backgroundIntensityCount = backgroundVirtualCollector.getIntensities().length;
            vectorSviVarianceBackgroundIntensities = vectorSviVarianceIntensities//
                    .getMatrix(0, backgroundIntensityCount - 1, 0, backgroundIntensityCount - 1);
        }

        normalizedBackgroundAquireTimes = getNormalizedBackgroundAquireTimes();
    }

    @Override
    public void generateSetOfFitFunctions(boolean propagateUncertainties, boolean doApplyMaskingArray) {

        // June 2013 copied from RawRatioDataModel feb 2013 new strategy to do only once
        // also MEAN returns false if it had to use an arithmentic mean and stops further processing
        prepareDataForFitFunctions();

        System.out.println("\nCalculate Fit Functions for Intensity  " + rawIsotopeModelName.getName() //
                + "  USING " + (USING_FULL_PROPAGATION ? "FULL PROPAGATION" : "FAST PROPAGATION"));

        if (selectedFitFunctionType.equals(FitFunctionTypeEnum.NONE)) {
            // do nothing
        } else if (selectedFitFunctionType.equals(FitFunctionTypeEnum.CONSTANT)) {
            generateCONSTANTfitFunction();
        }

        // one last time to restore current choice
        calculateFittedFunctions(selectedFitFunctionType.getName());
        calculatedInitialFitFunctions = true;

        matrixSibCovarianceBackgroundIntensities = null;
        vectorSviVarianceBackgroundIntensities = null;

        System.gc();
    }

    @Override
    public void calculateFittedFunctions(String fitFunctionTypeName) {

        AbstractFunctionOfX backgroundFitFunction = getBackgroundFitFunctions().get(fitFunctionTypeName);

        double[] fitBackgroundIntensitiesBackground = new double[backgroundVirtualCollector.getAquireTimes().length];
        double[] normalizedOnPeakAquireTimes = getNormalizedOnPeakAquireTimes();
        double[] fitBackgroundIntensitiesOnPeak = new double[onPeakVirtualCollector.getAquireTimes().length];

        if (backgroundFitFunction != null) {
            System.out.println("Fitting Background ....");
            // apply fitfunction to background  (NOT TRUE precondition = both same size of course)
            for (int i = 0; i < fitBackgroundIntensitiesBackground.length; i++) {
                fitBackgroundIntensitiesBackground[i] = backgroundFitFunction.f(normalizedBackgroundAquireTimes[i]);
            }

//            // apply fitfunction to onPeak 
//            for (int i = 0; i < fitBackgroundIntensitiesBackground.length; i++) {
//                fitBackgroundIntensitiesOnPeak[i] = backgroundFitFunction.f(normalizedOnPeakAquireTimes[i]);
//            }
            // apply fitfunction to onPeak - corrected dec 2014
            for (int i = 0; i < fitBackgroundIntensitiesOnPeak.length; i++) {
                fitBackgroundIntensitiesOnPeak[i] = backgroundFitFunction.f(normalizedOnPeakAquireTimes[i]);
            }
        }

        backgroundVirtualCollector.setFitBackgroundIntensities(fitBackgroundIntensitiesBackground);
        onPeakVirtualCollector.setFitBackgroundIntensities(fitBackgroundIntensitiesOnPeak);

    }

    /**
     *
     * @return
     */
    @Override
    public AbstractFunctionOfX getSelectedFitFunction() {

        AbstractFunctionOfX fitFunc = null;
        if (overDispersionSelected) {
            fitFunc = backgroundFitFunctionsWithOD.get(selectedFitFunctionType.getName());
        } else {
            fitFunc = backgroundFitFunctionsNoOD.get(selectedFitFunctionType.getName());
        }

        return fitFunc;
    }

    /**
     *
     */
    public void propagateUnctInBaselineCorrOnPeakIntensities() {

        AbstractFunctionOfX backgroundFitFunction = getBackgroundFitFunctions().get(selectedFitFunctionType.getName());

        if (backgroundFitFunction != null) { // not NONE
            // June 2013
            Matrix matrixOrVector;
            if (USING_FULL_PROPAGATION) {
                matrixOrVector = matrixSiCovarianceIntensities;
            } else {
                matrixOrVector = vectorSviVarianceIntensities;
            }

            double[] normalizedOnPeakAquireTimes = getNormalizedOnPeakAquireTimes();
            // propagate uncertainties in baseline-corrected onpeak
            // decision for J21 ****************************************************
            double[] neededValues = normalizedOnPeakAquireTimes;
            // mean just needs dataActiveMap
            // line needs dataActiveMap and mesuredIntensities
            if (backgroundFitFunction.getShortName().equals(FitFunctionTypeEnum.LINE)) {
                neededValues = onPeakVirtualCollector.getIntensities();
            }
            // L-M needs dataActiveMap and normalizedOnPeakAquireTimes

            // end decision for J21 ************************************************
            int countOfActiveOnPeakData = 0;
            for (int i = 0; i < onPeakVirtualCollector.getDataActiveMap().length; i++) {
                if (onPeakVirtualCollector.getDataActiveMap()[i]) {
                    countOfActiveOnPeakData++;
                }
            }

            J11 = backgroundFitFunction.getMatrixJ11();

            // mar 2014
            if (J11 != null) {

                J21 = backgroundFitFunction.makeMatrixJ21(countOfActiveOnPeakData, onPeakVirtualCollector.getDataActiveMap(), neededValues);

                J22 = backgroundFitFunction.makeMatrixJ22(countOfActiveOnPeakData, onPeakVirtualCollector.getDataActiveMap(), normalizedOnPeakAquireTimes);

                Matrix JOnPeak1 = null;
                try {
                    JOnPeak1 = J21.times(J11);
//                } catch (Exception e) {
//                    System.out.println(" JOnPeak1 in RawIntensityDataModel matrix error " + e.getMessage());
//                }

                    // append J22 after JOnPeak1
                    JOnPeak = new Matrix(countOfActiveOnPeakData, JOnPeak1.getColumnDimension() + countOfActiveOnPeakData);
                    JOnPeak.setMatrix(0, countOfActiveOnPeakData - 1, 0, JOnPeak1.getColumnDimension() - 1, JOnPeak1);
                    JOnPeak.setMatrix(0, countOfActiveOnPeakData - 1, JOnPeak1.getColumnDimension(), JOnPeak1.getColumnDimension() + countOfActiveOnPeakData - 1, J22);

                    // remove matrix entries for inactive data
                    // onpeak data is on the right of matrixSI
                    int countOfBackgroundData = backgroundVirtualCollector.getDataActiveMap().length;

                    ArrayList<Integer> deselectedIndexes = new ArrayList<>();
                    for (int i = 0; i < onPeakVirtualCollector.getDataActiveMap().length; i++) {
                        if (!onPeakVirtualCollector.getDataActiveMap()[i]) {
                            deselectedIndexes.add(i + countOfBackgroundData);
                        }
                    }

                    int[] rowColToCopy = new int[matrixOrVector.getRowDimension() - deselectedIndexes.size()];

                    // this collects indices of columns/rows of kept (selected) data for extraction
                    int count = 0;
                    for (int j = 0; j < matrixOrVector.getRowDimension(); j++) {
                        if (!deselectedIndexes.contains(j)) {
                            rowColToCopy[count] = j;
                            count++;
                        }
                    }

                    Matrix copymatrixSiCovarianceIntensities = matrixOrVector.getMatrix(rowColToCopy, rowColToCopy);

                    Sopbc = JOnPeak.times(copymatrixSiCovarianceIntensities).times(JOnPeak.transpose());

                    // calculate the covariance matrix for the log-ratios, Sopbclr.
                    double[][] invertedOnPeakCorrectedIntensities = new double[countOfActiveOnPeakData][1];
                    count = 0;
                    for (int i = 0; i < onPeakVirtualCollector.getDataActiveMap().length; i++) {
                        if (onPeakVirtualCollector.getDataActiveMap()[i]) {
                            invertedOnPeakCorrectedIntensities[count][0] = 1.0 / onPeakVirtualCollector.getCorrectedIntensities()[i];
                            count++;
                        }
                    }

                    Jlogr = new Matrix(invertedOnPeakCorrectedIntensities);
                    Jmat = Jlogr.times(Jlogr.transpose());

                    Sopbclr = Jmat.arrayTimes(Sopbc);

//                System.out.println("CheckPoint Sopbclr uncertainty propagation for " + this.getDataModelName());
                } catch (Exception e) {
                    System.out.println(" JOnPeak1 in RawIntensityDataModel matrix error " + e.getMessage());
                }
            } else {
                System.out.println("NO J11 for " + this.getDataModelName());
            }
        }
//        else {
////            System.out.println("no background fit function for " + this.getDataModelName());
//        }
    }

    /**
     *
     * @return
     */
    public Matrix getColumnVectorOfCorrectedOnPeakIntensities() {

        // account for missing data
        ArrayList<Double> correctedIntensities = new ArrayList<Double>();
        for (int i = 0; i < onPeakVirtualCollector.getCorrectedIntensities().length; i++) {
            if (onPeakVirtualCollector.getDataActiveMap()[i]) {
                correctedIntensities.add(onPeakVirtualCollector.getCorrectedIntensities()[i]);
            }
        }

        double[] correctedPeakIntensities = new double[correctedIntensities.size()];
        for (int i = 0; i < correctedPeakIntensities.length; i++) {
            correctedPeakIntensities[i] = correctedIntensities.get(i);
        }

//        double[][] correctedPeakIntensities = new double[onPeakVirtualCollector.getCorrectedIntensities().length][1];
//
//        for (int i = 0; i < correctedPeakIntensities.length; i ++) {
//            correctedPeakIntensities[i][0] = onPeakVirtualCollector.getCorrectedIntensities()[i];
//        }
//
//        Matrix colVector = new Matrix( correctedPeakIntensities );
        return new Matrix(correctedPeakIntensities, correctedPeakIntensities.length);
    }

    @Override
    public Map<String, AbstractFunctionOfX> getBackgroundFitFunctions() {
        //return logRatioFitFunctionsNoOD;
        Map<String, AbstractFunctionOfX> fitFunctions = null;

        if (overDispersionSelected) {
            fitFunctions = backgroundFitFunctionsWithOD;
        } else {
            fitFunctions = backgroundFitFunctionsNoOD;
        }

        return fitFunctions;
    }

    @Override
    public void setSelectedFitFunctionType(FitFunctionTypeEnum selectedFitFunctionType) {
        this.selectedFitFunctionType = selectedFitFunctionType;
    }

    @Override
    public FitFunctionTypeEnum getSelectedFitFunctionType() {
        return selectedFitFunctionType;
    }

    /**
     * @return the belowDetection
     */
    public boolean isBelowDetection() {
        return belowDetection;
    }

    /**
     * @param belowDetection the belowDetection to set
     */
    public void setBelowDetection(boolean belowDetection) {
        this.belowDetection = belowDetection;
    }

    /**
     * @return the Sopbclr
     */
    public Matrix getSopbclr() {
        return Sopbclr;
    }

    /**
     * @param Sopbclr the Sopbclr to set
     */
    public void setSopbclr(Matrix Sopbclr) {
        this.Sopbclr = Sopbclr;
    }

    /**
     * @return the collectorModel
     */
    public AbstractCollectorModel getCollectorModel() {
        return collectorModel;
    }

    /**
     * @return the matrixSiCovarianceIntensities
     */
    public Matrix getMatrixSiCovarianceIntensities() {
        return matrixSiCovarianceIntensities;
    }

    /**
     * @param J11 the J11 to set
     */
    public void setJ11(Matrix J11) {
        this.J11 = J11;
    }

    /**
     * @param matrixJ21 the J21 to set
     */
    public void setMatrixJ21(Matrix matrixJ21) {
        this.J21 = matrixJ21;
    }

    /**
     * @param J22 the J22 to set
     */
    public void setJ22(Matrix J22) {
        this.J22 = J22;
    }

    /**
     * @param JOnPeak the JOnPeak to set
     */
    public void setJOnPeak(Matrix JOnPeak) {
        this.JOnPeak = JOnPeak;
    }

    /**
     * @param Sopbc the Sopbc to set
     */
    public void setSopbc(Matrix Sopbc) {
        this.Sopbc = Sopbc;
    }

    /**
     * @param matrixJlogr the Jlogr to set
     */
    public void setMatrixJlogr(Matrix matrixJlogr) {
        this.Jlogr = matrixJlogr;
    }

    /**
     * @param Jmat the Jmat to set
     */
    public void setJmat(Matrix Jmat) {
        this.Jmat = Jmat;
    }

    /**
     * @return the J11
     */
    public Matrix getJ11() {
        return J11;
    }

    /**
     * @return the J21
     */
    public Matrix getMatrixJ21() {
        return J21;
    }

    /**
     * @return the J22
     */
    public Matrix getJ22() {
        return J22;
    }

    /**
     * @return the JOnPeak
     */
    public Matrix getJOnPeak() {
        return JOnPeak;
    }

    /**
     * @return the Sopbc
     */
    public Matrix getSopbc() {
        return Sopbc;
    }

    /**
     * @return the Jlogr
     */
    public Matrix getMatrixJlogr() {
        return Jlogr;
    }

    /**
     * @return the Jmat
     */
    public Matrix getJmat() {
        return Jmat;
    }

    /**
     *
     * @return
     */
    @Override
    public double getStandardValue() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     *
     */
    @Override
    public void cleanupUnctCalcs() {
        matrixSiCovarianceIntensities = null;
        matrixSibCovarianceBackgroundIntensities = null;
        vectorSviVarianceIntensities = null;
        vectorSviVarianceBackgroundIntensities = null;
        setJ11(null);
        setMatrixJ21(null);
        setJ22(null);
        setJOnPeak(null);
        setSopbc(null);
        setMatrixJlogr(null);
        setJmat(null);
        setSopbclr(null);
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
//        return logRatioFitFunctionsNoOD.get( fitFunctionType.getName() ) != null;

        boolean contains = false;

        if (overDispersionSelected) {
            contains = backgroundFitFunctionsWithOD.get(fitFunctionType.getName()) != null;
        } else {
            contains = backgroundFitFunctionsNoOD.get(fitFunctionType.getName()) != null;
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
        AbstractFunctionOfX fitFunc = backgroundFitFunctionsWithOD.get(fitFunctionType.getName());
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
            return Math.sqrt(backgroundFitFunctionsWithOD.get(fitFunctionType.getName()).getOverDispersion());
        }

        return retVal;

    }

    /**
     * @return the correctedHg202Si
     */
    public Matrix getCorrectedHg202Si() {
        return correctedHg202Si;
    }

    /**
     * @param correctedHg202Si the correctedHg202Si to set
     */
    public void setCorrectedHg202Si(Matrix correctedHg202Si) {
        this.correctedHg202Si = correctedHg202Si;
    }

    /**
     * @return the vectorSviVarianceIntensities
     */
    public Matrix getVectorSviVarianceIntensities() {
        return vectorSviVarianceIntensities;
    }

    /**
     * @return the vectorSviVarianceBackgroundIntensities
     */
    public Matrix getVectorSviVarianceBackgroundIntensities() {
        return vectorSviVarianceBackgroundIntensities;
    }

    /**
     * @return the rawIsotopeModelName
     */
    public IsotopeNames getRawIsotopeModelName() {
        return rawIsotopeModelName;
    }

    /**
     * @return the forceMeanForCommonLeadRatios
     */
    @Override
    public boolean isForceMeanForCommonLeadRatios() {
        return forceMeanForCommonLeadRatios;
    }

    /**
     * @param forceMeanForCommonLeadRatios the forceMeanForCommonLeadRatios to
     * set
     */
    public void setForceMeanForCommonLeadRatios(boolean forceMeanForCommonLeadRatios) {
        this.forceMeanForCommonLeadRatios = forceMeanForCommonLeadRatios;
    }

    @Override
    public boolean isUsedForCommonLeadCorrections() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean[] getDataActiveMap() {
        // nov 2014
        return onPeakVirtualCollector.getDataActiveMap();
    }

    /**
     * @return the forcedMeanForCommonLeadRatios
     */
    public double getForcedMeanForCommonLeadRatios() {
        return forcedMeanForCommonLeadRatios;
    }

    /**
     * @param forcedMeanForCommonLeadRatios the forcedMeanForCommonLeadRatios to
     * set
     */
    public void setForcedMeanForCommonLeadRatios(double forcedMeanForCommonLeadRatios) {
        this.forcedMeanForCommonLeadRatios = forcedMeanForCommonLeadRatios;
    }

    /**
     * @return the USING_FULL_PROPAGATION
     */
    public boolean isUSING_FULL_PROPAGATION() {
        return USING_FULL_PROPAGATION;
    }

    /**
     * @param USING_FULL_PROPAGATION the USING_FULL_PROPAGATION to set
     */
    public void setUSING_FULL_PROPAGATION(boolean USING_FULL_PROPAGATION) {
        this.USING_FULL_PROPAGATION = USING_FULL_PROPAGATION;
    }
}
