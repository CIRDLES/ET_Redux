/*
 * SessionCorrectedUnknownsSummary.java
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
import java.util.Map;
import org.earthtime.dataDictionaries.RadRatios;

/**
 *
 * @author James F. Bowring
 */
public class SessionCorrectedUnknownsSummary implements Serializable{
    
    // Class variables
    private static final long serialVersionUID = -5775331053687026298L;
    // instance variables
    private Matrix unknownsAnalyticalCovarianceSu;
    private Map<String, Integer> unknownFractionIDs;
    private Matrix unknownsLogRatioMeans;
    private RadRatios radiogenicRatioName;
    private double varianceOfStandardLogRatio;

    /**
     *
     */
    public SessionCorrectedUnknownsSummary () {
    }

    /**
     *
     * @param unknownsAnalyticalCovarianceSu
     * @param unknownFractionIDs
     * @param unknownsLogRatioMeans
     * @param radiogenicRatioName
     * @param standardRatio
     */
    public SessionCorrectedUnknownsSummary (//
            Matrix unknownsAnalyticalCovarianceSu,//
            Map<String, Integer> unknownFractionIDs,//
            Matrix unknownsLogRatioMeans, //
            RadRatios radiogenicRatioName,
            double varianceOfStandardLogRatio) {
        this.unknownsAnalyticalCovarianceSu = unknownsAnalyticalCovarianceSu;
        this.unknownFractionIDs = unknownFractionIDs;
        this.unknownsLogRatioMeans = unknownsLogRatioMeans;
        this.radiogenicRatioName = radiogenicRatioName;
        this.varianceOfStandardLogRatio = varianceOfStandardLogRatio;
    }

    /**
     * @return the unknownsAnalyticalCovarianceSu
     */
    public Matrix getUnknownsAnalyticalCovarianceSu () {
        return unknownsAnalyticalCovarianceSu;
    }

    /**
     * @param unknownsAnalyticalCovarianceSu the unknownsAnalyticalCovarianceSu to set
     */
    public void setUnknownsAnalyticalCovarianceSu ( Matrix unknownsAnalyticalCovarianceSu ) {
        this.unknownsAnalyticalCovarianceSu = unknownsAnalyticalCovarianceSu;
    }

    /**
     * @return the unknownFractionIDs
     */
    public Map<String, Integer> getUnknownFractionIDs () {
        return unknownFractionIDs;
    }

    /**
     * @param unknownFractionIDs the unknownFractionIDs to set
     */
    public void setUnknownFractionIDs ( Map<String, Integer> unknownFractionIDs ) {
        this.unknownFractionIDs = unknownFractionIDs;
    }

    /**
     * @return the unknownsLogRatioMeans
     */
    public Matrix getUnknownsLogRatioMeans () {
        return unknownsLogRatioMeans;
    }

    /**
     * @param unknownsLogRatioMeans the unknownsLogRatioMeans to set
     */
    public void setUnknownsLogRatioMeans ( Matrix unknownsLogRatioMeans ) {
        this.unknownsLogRatioMeans = unknownsLogRatioMeans;
    }

    /**
     * @return the radiogenicRatioName
     */
    public RadRatios getRadiogenicRatioName () {
        return radiogenicRatioName;
    }

    /**
     * @param rawRatioName
     */
    public void setRadiogenicRatioName ( RadRatios rawRatioName ) {
        this.radiogenicRatioName = rawRatioName;
    }

    /**
     * @return the varianceOfStandardLogRatio
     */
    public double getVarianceOfStandardLogRatio() {
        return varianceOfStandardLogRatio;
    }

    /**
     * @param varianceOfStandardLogRatio the varianceOfStandardLogRatio to set
     */
    public void setVarianceOfStandardLogRatio(double varianceOfStandardLogRatio) {
        this.varianceOfStandardLogRatio = varianceOfStandardLogRatio;
    }
    
}
