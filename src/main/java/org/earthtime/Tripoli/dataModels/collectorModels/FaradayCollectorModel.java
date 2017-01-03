/*
 * FaradayCollectorModel.java
 *
 * Created Sep 29, 2012
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
package org.earthtime.Tripoli.dataModels.collectorModels;

import Jama.Matrix;
import java.math.BigDecimal;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public class FaradayCollectorModel extends AbstractCollectorModel {

    // Class variables
    private static final long serialVersionUID = 4360405141120954495L;
    private ResistorEnum resistor;
    private double amplifierNoiseVariance;

    /**
     *
     */
    public FaradayCollectorModel() {
        this(ReduxConstants.DEFAULT_OBJECT_NAME, ResistorEnum.OHMS10e10, ResistorEnum.OHMS10e10.getDefaultAmplifierNoiseVariance());
    }

    /**
     *
     * @param collectorName
     * @param resistor
     * @param amplifierNoiseVariance
     */
    public FaradayCollectorModel(String collectorName, ResistorEnum resistor, double amplifierNoiseVariance) {
        super("Faraday", collectorName);
        // FARADAY: if relative uncert = 3 ppm, them relativeGainOneSigmaAbs = relativeGain * 3 * 10-6 for Faraday
        this.relativeGain = new ValueModel("relativeGain", new BigDecimal(1.0, ReduxConstants.mathContext10), //
                "PCT", new BigDecimal(3e-4, ReduxConstants.mathContext10), BigDecimal.ZERO);
        this.resistor = resistor;
        this.amplifierNoiseVariance = amplifierNoiseVariance;
    }

    /**
     * @return the resistor
     */
    public ResistorEnum getResistor() {
        return resistor;
    }

    /**
     * @param resistor the resistor to set
     */
    public void setResistor(ResistorEnum resistor) {
        this.resistor = resistor;
    }

    /**
     * @return the amplifierNoiseVariance
     */
    public double getAmplifierNoiseVariance() {
        return amplifierNoiseVariance;
    }

    /**
     * @param amplifierNoiseVariance the amplifierNoiseVariance to set
     */
    public void setAmplifierNoiseVariance(double amplifierNoiseVariance) {
        this.amplifierNoiseVariance = amplifierNoiseVariance;
    }

    /**
     *
     * @param relativeGainUnctDouble
     */
    @Override
    public void setRelativeGainUnct(double relativeGainUnctDouble) {
        // ppm stored as a percent so 3ppm stored as 3e-4
        this.relativeGain.setOneSigma(new BigDecimal(relativeGainUnctDouble * 1e-4, ReduxConstants.mathContext10));
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
    public double[] calculateMeasuredCountsAndMatrixSIntensityDiagonal(int countOfBaselineIntensities, double[] analogCorrectionFactors, double[] allIntensities, double integrationTime) {

        double[] convertedIntensities = new double[allIntensities.length];
        for (int i = 0; i < allIntensities.length; i++) {
            convertedIntensities[i] = //                    
                    (allIntensities[i] + amplifierNoiseVariance * resistor.getCountsPerVolt() * resistor.getCountsPerVolt())//
                    / integrationTime;
        }
//        diagonalOfMatrixSIntensities = buildDiagonalOfMatrixSi( countOfBaselineIntensities, convertedIntensities );
        return buildDiagonalOfMatrixSi(countOfBaselineIntensities, convertedIntensities);
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
        // populate diagonal
        for (int i = 0; i < diagonalOfMatrixSIntensities.length; i++) {
            Si.set(i, i, diagonalOfMatrixSIntensities[i]);
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
        // TODO: to become vector 
        Matrix Svi = new Matrix(diagonalOfMatrixSIntensities.length, diagonalOfMatrixSIntensities.length);
        // populate diagonal
        for (int i = 0; i < diagonalOfMatrixSIntensities.length; i++) {
            Svi.set(i, i, diagonalOfMatrixSIntensities[i]);
        }

        return Svi;
    }

//    @Override
//    public void setRelativeGain ( double relativeGainDouble ) {
////        this.relativeGain = new ValueModel( //
////                "relativeGain", new BigDecimal( relativeGainDouble, ReduxConstants.mathContext10  ), //
////                "ABS", new BigDecimal( 0.000003, ReduxConstants.mathContext10 ) );
////        
//        this.relativeGain.setValue( new BigDecimal( relativeGainDouble, ReduxConstants.mathContext10  ));
//    }

    /**
     *
     * @param rawIntensities
     * @return
     */
        @Override
    public double[] correctRawIntensitiesForResistor(double[] rawIntensities) {
        double[] rawIntensitiesCorrected = new double[rawIntensities.length];

        for (int i = 0; i < rawIntensities.length; i++) {
            rawIntensitiesCorrected[i] = rawIntensities[i] * (resistor.getOhms() / 1.0e11);
        }

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

        for (int i = 0; i < rawIntensities.length; i++) {
            rawIntensitiesCountsPerSecond[i] = rawIntensities[i] * resistor.getCountsPerVolt();
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

        for (int i = 0; i < rawIntensities.length; i++) {
            rawIntensities[i] = countsPerSecond[i] / resistor.getCountsPerVolt();
        }

        return rawIntensities;
    }

    /**
     *
     */
    public enum ResistorEnum {

        /**
         *
         */
        OHMS10e10(10e10, 1.6568e-10, 624150965.0, "10 E10"),
        /**
         *
         */
        OHMS3e11(3e11, 4.9704e-9, 20805032.17, "3 E11"),
        /**
         *
         */
        OHMS10e11(10e11, 1.6568e-9, 62415096.5, "10 E11"),
        /**
         *
         */
        OHMS10e12(10e12, 1.6568e-8, 6241509.65, "10 E12");
        private final double ohms;
        private final double defaultAmplifierNoiseVariance;

        /**
         *
         */
        protected final double countsPerVolt;
        private final String prettyName;

        private ResistorEnum( //
                final double ohms, //
                final double defaultAmplifierNoiseVariance, //
                final double countsPerVolt, //
                String prettyName) {
            this.ohms = ohms;
            this.defaultAmplifierNoiseVariance = defaultAmplifierNoiseVariance;
            this.countsPerVolt = countsPerVolt;
            this.prettyName = prettyName;
        }

        /**
         * @return the ohms
         */
        public double getOhms() {
            return ohms;
        }

        /**
         * @return the defaultAmplifierNoiseVariance
         */
        public double getDefaultAmplifierNoiseVariance() {
            return defaultAmplifierNoiseVariance;
        }

        /**
         * @return the countsPerVolt
         */
        public double getCountsPerVolt() {
            return countsPerVolt;
        }

        /**
         *
         * @return
         */
        public static ResistorEnum[] getArrayOfResistors() {
            return new ResistorEnum[]{OHMS10e10, OHMS3e11, OHMS10e11, OHMS10e12};
        }
    }
}
