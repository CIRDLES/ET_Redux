/*
 * AbstractCollectorModel.java
 *
 * Created Sep 29, 2012
 *
 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.earthtime.Tripoli.dataModels.collectorModels;

import Jama.Matrix;
import java.io.Serializable;
import java.math.BigDecimal;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public abstract class AbstractCollectorModel implements Serializable {

    // Class variables
    private static final long serialVersionUID = -8204646307865299761L;
    private String collectorType;

    /**
     *
     */
    protected String collectorName;

    /**
     *
     */
    protected ValueModel relativeGain;

    /**
     *
     */
    protected AbstractCollectorModel() {
        this(null, ReduxConstants.DEFAULT_OBJECT_NAME);
    }

    /**
     *
     * @param collectorType
     * @param collectorName
     */
    public AbstractCollectorModel(String collectorType, String collectorName) {
        this.collectorType = collectorType;
        this.collectorName = collectorName;
        this.relativeGain = null;
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
    public abstract double[] calculateMeasuredCountsAndMatrixSIntensityDiagonal(int countOfBaselineIntensities, double[] allAnalogCorrectionFactors, double[] allItensities, double integrationTime);

    /**
     *
     * @param rawIntensities
     * @return
     */
    public abstract double[] convertRawIntensitiesToCountsPerSecond(double[] rawIntensities);

    /**
     *
     * @param rawIntensities
     * @return
     */
    public abstract double[] correctRawIntensitiesForResistor(double[] rawIntensities);

    /**
     *
     * @param countsPerSecond
     * @return
     */
    public abstract double[] convertCountsPerSecondToRawIntensities(double[] countsPerSecond);

    /**
     *
     * @param allItensities
     * @param multiplierForIntensities
     * @return
     */
    protected double[] calculateMeasuredCounts(double[] allItensities, double multiplierForIntensities) {
        double[] measuredCounts = new double[allItensities.length];

        for (int i = 0; i < allItensities.length; i++) {
            measuredCounts[i] = allItensities[i] * multiplierForIntensities;
        }

        return measuredCounts;
    }

    /**
     *
     * @param countOfBaselineIntensities the value of countOfBaselineIntensities
     * @param allValues the value of allValues
     * @return
     */
    protected double[] buildDiagonalOfMatrixSi(int countOfBaselineIntensities, double[] allValues) {

        // sum over all baseline only and calc average to replace <=0 values of baseline only
        // repeat for ablation side
        double totalPositiveValues = 0;
        int countPositiveValues = 0;
        for (int i = 0; i < countOfBaselineIntensities; i++) {
            if (allValues[i] > 0.0) {
                totalPositiveValues += allValues[i];
                countPositiveValues++;
            }
        }
        double avgAllPositiveValues = 0.0;
        // June 2015 catch possible 0 / 0 = NaN etc       
        if (totalPositiveValues > 0.0) {
            avgAllPositiveValues = totalPositiveValues / countPositiveValues;
        }

        // replace any negative element with avg of positive values
        for (int i = 0; i < countOfBaselineIntensities; i++) {
            if (allValues[i] <= 0.0) {
                allValues[i] = avgAllPositiveValues;
            }
        }

        totalPositiveValues = 0;
        countPositiveValues = 0;
        for (int i = countOfBaselineIntensities; i < allValues.length; i++) {
            if (allValues[i] > 0.0) {
                totalPositiveValues += allValues[i];
                countPositiveValues++;
            }
        }
        avgAllPositiveValues = 0.0;
        // June 2015 catch possible 0 / 0 = NaN etc       
        if (totalPositiveValues > 0.0) {
            avgAllPositiveValues = totalPositiveValues / countPositiveValues;
        }

        // replace any negative element with avg of positive values
        for (int i = countOfBaselineIntensities; i < allValues.length; i++) {
            if (allValues[i] <= 0.0) {
                allValues[i] = avgAllPositiveValues;
            }
        }

        return allValues;

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
    public abstract Matrix buildMatrixSi(double[] diagonalOfMatrixSIntensities, double[] measuredIntensityIonCounter);

    /**
     *
     *
     * @param diagonalOfMatrixSIntensities the value of
     * diagonalOfMatrixSIntensities
     * @param measuredIntensityIonCounter the value of
     * measuredIntensityIonCounter
     */

    /**
     *
     * @param diagonalOfMatrixSIntensities the value of
     * diagonalOfMatrixSIntensities
     * @param measuredIntensityIonCounter the value of
     * measuredIntensityIonCounter
     * @return
     */
    public abstract Matrix buildVectorSvi(double[] diagonalOfMatrixSIntensities, double[] measuredIntensityIonCounter);

    /**
     * @return the relativeGain
     */
    public ValueModel getRelativeGain() {
        return relativeGain;
    }

    /**
     * @param relativeGain the relativeGain to set
     */
    public void setRelativeGain(ValueModel relativeGain) {
        this.relativeGain = relativeGain;
    }

    /**
     *
     * @param relativeGainDouble
     */
    public void setRelativeGain(double relativeGainDouble) {
        this.relativeGain.setValue(new BigDecimal(relativeGainDouble, ReduxConstants.mathContext10));
    }

    /**
     *
     * @param relativeGainUnctDouble
     */
    public abstract void setRelativeGainUnct(double relativeGainUnctDouble);

    /**
     * @return the collectorName
     */
    public String getCollectorName() {
        return collectorName;
    }

    /**
     * @param collectorName the collectorName to set
     */
    public void setCollectorName(String collectorName) {
        this.collectorName = collectorName;
    }

    /**
     * @return the collectorType
     */
    public String getCollectorType() {
        return collectorType;
    }
}
