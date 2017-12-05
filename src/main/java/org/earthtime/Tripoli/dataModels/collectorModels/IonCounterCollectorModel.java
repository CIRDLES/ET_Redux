/*
 * IonCounterCollectorModel.java
 *
 * Created Sep 29, 2012
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
package org.earthtime.Tripoli.dataModels.collectorModels;

import Jama.Matrix;
import java.math.BigDecimal;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public class IonCounterCollectorModel extends AbstractCollectorModel {

    // Class variables
    private static final long serialVersionUID = 3058997452003236257L;
    /**
     * The unit for the dead time and its uncertainty is nanoseconds (ns). New
     * data dictionary terms: deadTime, deadTimeOneSigmaAbs.
     */
    private ValueModel deadTime;
    private CollectedDataStyle collectedDataStyle;
    private double[] allAnalogCorrectionFactors;

    /**
     *
     */
    public IonCounterCollectorModel() {
        this(ReduxConstants.DEFAULT_OBJECT_NAME, new ValueModel("deadTime", BigDecimal.ZERO, "ABS", BigDecimal.ZERO, BigDecimal.ZERO), CollectedDataStyle.COUNTS);
    }

    /**
     *
     * @param collectorName
     * @param deadTime
     * @param collectedDataStyle
     */
    public IonCounterCollectorModel(String collectorName, ValueModel deadTime, CollectedDataStyle collectedDataStyle) {
        super("IonCounter", collectorName);
        // IonCounter uses percent uncertainty
        relativeGain = new ValueModel("relativeGain", new BigDecimal(1.0, ReduxConstants.mathContext10),//
                "PCT", new BigDecimal(1.0, ReduxConstants.mathContext10), BigDecimal.ZERO);
        this.deadTime = deadTime;
        this.collectedDataStyle = collectedDataStyle;
        this.allAnalogCorrectionFactors = new double[0];
    }

    /**
     * @return the deadTime
     */
    public ValueModel getDeadTime() {
        return deadTime;
    }

    /**
     * @param deadTime the deadTime to set
     */
    public void setDeadTime(ValueModel deadTime) {
        this.deadTime = deadTime;
    }

    /**
     *
     * @param relativeGainUnctDouble
     */
    @Override
    public void setRelativeGainUnct(double relativeGainUnctDouble) {
        this.relativeGain.setOneSigma(new BigDecimal(relativeGainUnctDouble, ReduxConstants.mathContext10));
    }

    /**
     * @return the collectedDataStyle
     */
    public CollectedDataStyle getCollectedDataStyle() {
        return collectedDataStyle;
    }

    /**
     * @param collectedDataStyle the collectedDataStyle to set
     */
    public void setCollectedDataStyle(CollectedDataStyle collectedDataStyle) {
        this.collectedDataStyle = collectedDataStyle;
    }

    /**
     *
     *
     * @param countOfBaselineIntensities the value of countOfBaselineIntensities
     * @param allAnalogCorrectionFactors the value of analogCorrectionFactors
     * @param allItensities the value of allItensities
     * @param integrationTime the value of integrationTime
     * @return the double[]
     */
    @Override
    public double[] calculateMeasuredCountsAndMatrixSIntensityDiagonal(int countOfBaselineIntensities, double[] allAnalogCorrectionFactors, double[] allItensities, double integrationTime) {

        this.allAnalogCorrectionFactors = allAnalogCorrectionFactors;
        double[] measuredVarianceFromIonCounts = new double[allItensities.length];

        for (int i = 0; i < allItensities.length; i++) {

            // page 6 of LAICPMS Uncertainty https://docs.google.com/document/d/1zZaVt5EIxjnTjAeBCdsPIB3eNLLNiB0ymOvcXTkaajM/edit#heading=h.p0w9k6lx30r2
            if ((collectedDataStyle.compareTo(collectedDataStyle.SEM) == 0) && (allAnalogCorrectionFactors[i] != 1.0)) {
                // octy 2016 temp hack until Noah finishes Math = both meanings of ACF are the same for now ... later will be ratio of the two
                measuredVarianceFromIonCounts[i] = Math.pow(allItensities[i] * 0.05, 2);
//                measuredVarianceFromIonCounts[i] = Math.pow(allItensities[i] * 0.05 * allAnalogCorrectionFactors[i], 2);
            } else {
                measuredVarianceFromIonCounts[i] = allItensities[i] / integrationTime; // march 2013 Noah's famous EFFING comment / integrationTime;
            }
        }

        double[] diagonalSI = buildDiagonalOfMatrixSi(countOfBaselineIntensities, measuredVarianceFromIonCounts);

        return diagonalSI;
    }

    /**
     *
     *
     * @param diagonalOfMatrixSIntensities the value of
     * diagonalOfMatrixSIntensities
     * @param measuredIntensityIonCounter the value of
     * measuredIntensityIonCounter
     * @return
     */
    @Override
    public Matrix buildMatrixSi(double[] diagonalOfMatrixSIntensities, double[] measuredIntensityIonCounter) {
        Matrix Si = new Matrix(diagonalOfMatrixSIntensities.length, diagonalOfMatrixSIntensities.length);

        Matrix columnVectorOfMeasuredCountsIntensityCountsSquared = new Matrix(measuredIntensityIonCounter.length, 1);

        // populate diagonal
        for (int i = 0; i < diagonalOfMatrixSIntensities.length; i++) {
            Si.set(i, i, diagonalOfMatrixSIntensities[i]);

            columnVectorOfMeasuredCountsIntensityCountsSquared.set(i, 0, measuredIntensityIonCounter[i] * measuredIntensityIonCounter[i]);
        }

        double deadUnctSquared = deadTime.getOneSigmaAbs().movePointLeft(0).pow(2).doubleValue();

        Matrix measuredCountsSquaredTimesDeadTime
                = columnVectorOfMeasuredCountsIntensityCountsSquared.//
                times(columnVectorOfMeasuredCountsIntensityCountsSquared.transpose());

        measuredCountsSquaredTimesDeadTime.timesEquals(deadUnctSquared);

        Si.plusEquals(measuredCountsSquaredTimesDeadTime);

        if (collectedDataStyle.compareTo(collectedDataStyle.SEM) == 0) {
            // zero out rows and columns with acf <> 1
            // note pre-condition that diagonal is built and allAnalogCorrectionFactors exists
            for (int i = 0; i < allAnalogCorrectionFactors.length; i++) {
                if (allAnalogCorrectionFactors[i] != 1.0) {
                    for (int j = 0; j < Si.getRowDimension(); j++) {
                        if (j != i) {
                            Si.set(j, i, 0.0);
                            Si.set(i, j, 0.0);
                        }
                    }
                }
            }
        }

        return Si;
    }

    /**
     *
     *
     * @param diagonalOfMatrixSIntensities the value of
     * diagonalOfMatrixSIntensities
     * @param measuredIntensityIonCounter the value of
     * measuredIntensityIonCounter
     * @return
     */
    @Override
    public Matrix buildVectorSvi(double[] diagonalOfMatrixSIntensities, double[] measuredIntensityIonCounter) {
        // TODO: make true vector
        Matrix Svi = new Matrix(diagonalOfMatrixSIntensities.length, diagonalOfMatrixSIntensities.length);

        double deadUnctSquared = deadTime.getOneSigmaAbs().movePointLeft(0).pow(2).doubleValue();

        // populate diagonal
        for (int i = 0; i < diagonalOfMatrixSIntensities.length; i++) {
            Svi.set(i, i, //
                    diagonalOfMatrixSIntensities[i] //
                    + deadUnctSquared * Math.pow(measuredIntensityIonCounter[i], 4.0));
        }

        return Svi;
    }

    /**
     *
     * @param deadTimeDouble
     */
    public void setDeadTime(double deadTimeDouble) {

        this.deadTime.setValue(new BigDecimal(deadTimeDouble, ReduxConstants.mathContext10)//
                .movePointLeft(ReduxConstants.getUnitConversionMoveCount("ns")));
    }

    /**
     *
     * @param deadTimeUnctDouble
     */
    public void setDeadTimeUnct(double deadTimeUnctDouble) {

        this.deadTime.setOneSigma(new BigDecimal(deadTimeUnctDouble, ReduxConstants.mathContext10)//
                .movePointLeft(ReduxConstants.getUnitConversionMoveCount("ns")));
    }

    /**
     *
     * @param rawIntensities
     * @return
     */
    @Override
    public double[] correctRawIntensitiesForResistor(double[] rawIntensities) {
        // do nothing for ion counter
        double[] rawIntensitiesCorrected = new double[rawIntensities.length];

        System.arraycopy(rawIntensities, 0, rawIntensitiesCorrected, 0, rawIntensities.length);

        return rawIntensitiesCorrected;
    }

    /**
     *
     * @param rawIntensities
     * @return
     */
    @Override
    public double[] convertRawIntensitiesToCountsPerSecond(double[] rawIntensities) {

        double[] rawIntensitiesCountsPerSecond = new double[rawIntensities.length];
        double multiplierForConversion = 1.0;

        if (collectedDataStyle.equals(CollectedDataStyle.VOLTS)) {
            multiplierForConversion = FaradayCollectorModel.ResistorEnum.OHMS10e11.getCountsPerVolt();
        }

        for (int i = 0; i < rawIntensities.length; i++) {
            rawIntensitiesCountsPerSecond[i] = rawIntensities[i] * multiplierForConversion;
        }

        return rawIntensitiesCountsPerSecond;
    }

    /**
     *
     * @param countsPerSecond
     * @return
     */
    @Override
    public double[] convertCountsPerSecondToRawIntensities(double[] countsPerSecond) {
        double[] rawIntensities = new double[countsPerSecond.length];
        double divisorForConversion = 1.0;

        if (collectedDataStyle.equals(CollectedDataStyle.VOLTS)) {
            divisorForConversion = FaradayCollectorModel.ResistorEnum.OHMS10e11.getCountsPerVolt();
        }
        for (int i = 0; i < rawIntensities.length; i++) {
            rawIntensities[i] = countsPerSecond[i] / divisorForConversion;
        }

        return rawIntensities;
    }

    /**
     *
     */
    public enum CollectedDataStyle {

        /**
         *
         */
        VOLTS("VOLTS"),
        /**
         *
         */
        COUNTS("COUNTS"),
        // added for use of analog corrections
        SEM("SEM");
        private final String name;

        private CollectedDataStyle(String name) {
            this.name = name;
        }
    }
}
