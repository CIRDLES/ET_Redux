/*
 * VirtualCollectorModel.java
 *
 * Created Jul 1, 2011
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
package org.earthtime.Tripoli.dataModels;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author James F. Bowring
 */
public class VirtualCollectorModel implements Serializable {

    // Class variables
    private static final long serialVersionUID = 9205757228422225198L;
    private final int collectorNumber;
    private boolean background;
    // starts as measured intensities then are converted to counts
    private double[] intensities;
    private double[] analogCorrectionFactors;
    private double[] aquireTimes;
    private double[] fitBackgroundIntensities;
    private double[] intensityCorrections;
    private double[] correctedIntensities;
    private double sumOfCorrectedOnPeakIntensities;
    private double[] logCorrectedIntensities;
    private boolean[] dataActiveMap;

    /**
     *
     * @param collectorNumber
     */
    public VirtualCollectorModel ( int collectorNumber ) {
        this.collectorNumber = collectorNumber;
        this.background = false;
        this.intensities = new double[0];
        this.analogCorrectionFactors = new double[0];
        this.aquireTimes = new double[0];
        this.fitBackgroundIntensities = new double[0];
        this.intensityCorrections = new double[0];
        this.correctedIntensities = new double[0];
        this.sumOfCorrectedOnPeakIntensities = 0;
        this.logCorrectedIntensities = new double[0];
        this.dataActiveMap = new boolean[0];
    }

    /**
     *
     */
    public void correctOnPeakIntensities () {
        correctedIntensities = new double[intensities.length];
        sumOfCorrectedOnPeakIntensities = 0.0;
        if (  ! background ) {
            for (int i = 0; i < intensities.length; i ++) {
                correctedIntensities[i] = //
                        intensities[i] //
                        - fitBackgroundIntensities[i]; //

                sumOfCorrectedOnPeakIntensities += correctedIntensities[i];
            }
        }
    }

    /**
     *
     */
    public void adjustNonPositiveCorrectedIntensities () {
        if (  ! background ) {
            for (int i = 0; i < correctedIntensities.length; i ++) {
                if ( correctedIntensities[i] == 0.0 ) {// changed from <= to == nov 2014 since 0 is only number undefined with log
                    correctedIntensities[i] = Double.MIN_VALUE;
                }
            }
        }
    }
        
    /**
     *
     */
    public void calculateLogOnPeakCorrectedIntensities () {
        logCorrectedIntensities = new double[intensities.length];
        if (  ! background ) {
            for (int i = 0; i < logCorrectedIntensities.length; i ++) {
                logCorrectedIntensities[i] = Math.log( correctedIntensities[i] );
            }
        }

    }

    /**
     *
     * @param background
     */
    public void updateCollector ( boolean background ) {
        this.background = background;
    }

    /**
     *
     * @param index
     * @param included
     */
    public void toggleOneDataAquisition ( int index, boolean included ) {
        dataActiveMap[index] = included;
    }

    /**
     * @return the collectorNumber
     */
    public int getCollectorNumber () {
        return collectorNumber;
    }

    /**
     * @return the intensities
     */
    public double[] getIntensities () {
        return intensities;
    }

    /**
     * @param intensities the intensities to set
     */
    public void setIntensities ( double[] intensities ) {
        this.intensities = intensities;
    }

    /**
     * @return the dataActiveMap
     */
    public boolean[] getDataActiveMap () {
        return dataActiveMap;
    }

    /**
     * @param dataActiveMap the dataActiveMap to set
     */
    public void setDataActiveMap ( boolean[] dataActiveMap ) {
        this.dataActiveMap = dataActiveMap;
    }

    /**
     * @return the aquireTimes
     */
    public double[] getAquireTimes () {
        return aquireTimes;
    }

    /**
     *
     * @return
     */
    public double[] getOnPeakAquireTimes () {
        // times shifted left to ignore background
        double[] onPeakAquireTimes = new double[aquireTimes.length];
        for (int i = 0; i < onPeakAquireTimes.length; i ++) {
            onPeakAquireTimes[i] = aquireTimes[i] - aquireTimes[0];
        }

        return onPeakAquireTimes;
    }

    /**
     *
     * @return
     */
    public double[] getBackgroundAquireTimes () {
        return aquireTimes;
    }

    /**
     * @param aquireTimes the aquireTimes to set
     */
    public void setAquireTimes ( double[] aquireTimes ) {
        this.aquireTimes = aquireTimes;
    }

    /**
     * @return the intensityCorrections
     */
    public double[] getIntensityCorrections () {
        return intensityCorrections;
    }

    /**
     * @param intensityCorrections the intensityCorrections to set
     */
    public void setIntensityCorrections ( double[] intensityCorrections ) {
        this.intensityCorrections = intensityCorrections;
    }

    /**
     * @return the fitBackgroundIntensities
     */
    public double[] getFitBackgroundIntensities () {
        return fitBackgroundIntensities;
    }

    /**
     * @param fitBackgroundIntensities the fitBackgroundIntensities to set
     */
    public void setFitBackgroundIntensities ( double[] fitBackgroundIntensities ) {
        this.fitBackgroundIntensities = fitBackgroundIntensities;
    }

    /**
     * @return the background
     */
    public boolean isBackground () {
        return background;
    }

    /**
     * @param background the background to set
     */
    public void setBackground ( boolean background ) {
        this.background = background;
    }

    /**
     * @return the correctedIntensities
     */
    public double[] getCorrectedIntensities () {
        return correctedIntensities;
    }
    
    /**
     *
     * @return
     */
    public double[] getActiveCorrectedIntensities () {

        ArrayList<Double> activeCorrectedintensitiesList = new ArrayList<Double>();
        for (int i = 0; i < dataActiveMap.length; i ++) {
            if ( dataActiveMap[i] ) {
                activeCorrectedintensitiesList.add(correctedIntensities[i]);
            }
        }
        
        double [] activeCorrectedIntensities = new double[activeCorrectedintensitiesList.size()];
        for (int i = 0; i < activeCorrectedintensitiesList.size(); i ++){
            activeCorrectedIntensities[i] = activeCorrectedintensitiesList.get( i);
        }
        
        return activeCorrectedIntensities;        
    }

    /**
     * @param correctedIntensities the correctedIntensities to set
     */
    public void setCorrectedIntensities ( double[] correctedIntensities ) {
        this.correctedIntensities = correctedIntensities;
    }

    /**
     * @return the logCorrectedIntensities
     */
    public double[] getLogCorrectedIntensities () {
        return logCorrectedIntensities;
    }

    /**
     * @return the sumOfCorrectedOnPeakIntensities
     */
    public double getSumOfCorrectedOnPeakIntensities () {
        return sumOfCorrectedOnPeakIntensities;
    }

    /**
     * @return the analogCorrectionFactors
     */
    public double[] getAnalogCorrectionFactors() {
        return analogCorrectionFactors;
    }

    /**
     * @param analogCorrectionFactors the analogCorrectionFactors to set
     */
    public void setAnalogCorrectionFactors(double[] analogCorrectionFactors) {
        this.analogCorrectionFactors = analogCorrectionFactors;
    }
}
