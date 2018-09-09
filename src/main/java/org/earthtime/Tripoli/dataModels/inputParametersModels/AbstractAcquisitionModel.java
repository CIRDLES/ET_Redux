/*
 * AbstractAcquisitionModel.java
 *
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
package org.earthtime.Tripoli.dataModels.inputParametersModels;

import java.io.File;
import java.io.Serializable;
import java.util.Map;
import org.earthtime.Tripoli.dataModels.collectorModels.FaradayCollectorModel;
import org.earthtime.Tripoli.massSpecSetups.AbstractMassSpecSetup;
import org.earthtime.dataDictionaries.AcquisitionTypesEnum;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;

/**
 *
 * @author James F. Bowring
 */
public abstract class AbstractAcquisitionModel implements Serializable {

    // Class variables
    private static final long serialVersionUID = -1164757885416000963L;
    //instance variables

    /**
     *
     */
    protected AcquisitionTypesEnum acquisitionType;

    /**
     *
     */
    protected Map<String, Double> collectorNameToRelativeGainsMap;

    /**
     *
     */
    protected Map<String, Double> collectorNameToRelativeGainsUnctMap;

    /**
     *
     */
    protected Map<String, Double> collectorNameToDeadTimesMap;

    /**
     *
     */
    protected Map<String, Double> collectorNameToDeadTimesUnctMap;

    /**
     *
     */
    protected Map<String, FaradayCollectorModel.ResistorEnum> collectorNameToResistorMap;

    /**
     *
     */
    protected Map<String, Double> collectorNameToAmpNoiseMap;

    /**
     *
     */
    protected File rawDataFile;

    /**
     *
     */
    protected boolean rawDataProcessed;

    /**
     *
     */
    protected boolean usingFullPropagation;

    /**
     *
     */
    protected int leftShadeCount;
    protected int baselineStartIndex;
    protected int baselineEndIndex;
    protected int peakStartIndex;
    protected int peakEndIndex;

    protected AbstractRatiosDataModel primaryMineralStandardModel;

    /**
     *
     */
    public AbstractAcquisitionModel() {
        this.acquisitionType = null;
        this.collectorNameToRelativeGainsMap = null;
        this.collectorNameToRelativeGainsUnctMap = null;
        this.collectorNameToDeadTimesMap = null;
        this.collectorNameToDeadTimesUnctMap = null;
        this.rawDataFile = null;
        this.rawDataProcessed = false;

        this.usingFullPropagation = true;
        this.leftShadeCount = 0;

        this.primaryMineralStandardModel = null;

    }

    /**
     *
     * @param massSpec
     */
    public void updateMassSpec(AbstractMassSpecSetup massSpec) {

        try {
            massSpec.assignGainsToCollectors(collectorNameToRelativeGainsMap);
        } catch (NullPointerException e) {
        }

        try {
            massSpec.assignGainsUnctToCollectors(collectorNameToRelativeGainsUnctMap);
        } catch (NullPointerException e) {
        }

        try {
            massSpec.assignDeadTimesToCollectors(collectorNameToDeadTimesMap);
        } catch (NullPointerException e) {
        }

        try {
            massSpec.assignDeadTimesUnctToCollectors(collectorNameToDeadTimesUnctMap);
        } catch (NullPointerException e) {
        }

        try {
            massSpec.assignResistorsCollectors(collectorNameToResistorMap);
        } catch (NullPointerException e) {
        }

        try {
            massSpec.assignAmpNoiseToCollectors(collectorNameToAmpNoiseMap);
        } catch (NullPointerException e) {
        }

        updateMassSpecIntegrationTimes(massSpec);

    }

    /**
     *
     * @param massSpec
     */
    public abstract void updateMassSpecIntegrationTimes(AbstractMassSpecSetup massSpec);

    /**
     * @return the acquisitionType
     */
    public AcquisitionTypesEnum getAcquisitionType() {
        return acquisitionType;
    }

    /**
     * @return the rawDataFile
     */
    public File getRawDataFile() {
        return rawDataFile;
    }

    /**
     * @param rawDataFile the rawDataFile to set
     */
    public void setRawDataFile(File rawDataFile) {
        this.rawDataFile = rawDataFile;
    }

    /**
     * @return the rawDataProcessed
     */
    public boolean isRawDataProcessed() {
        return rawDataProcessed;
    }

    /**
     * @param rawDataProcessed the rawDataProcessed to set
     */
    public void setRawDataFileProcessed(boolean rawDataProcessed) {
        this.rawDataProcessed = rawDataProcessed;
    }

    /**
     * @return the collectorNameToRelativeGainsMap
     */
    public Map<String, Double> getCollectorNameToRelativeGainsMap() {
        return collectorNameToRelativeGainsMap;
    }

    /**
     * @param collectorNameToRelativeGainsMap the
     * collectorNameToRelativeGainsMap to set
     */
    public void setCollectorNameToRelativeGainsMap(Map<String, Double> collectorNameToRelativeGainsMap) {
        this.collectorNameToRelativeGainsMap = collectorNameToRelativeGainsMap;
    }

    /**
     * @return the collectorNameToDeadTimesMap
     */
    public Map<String, Double> getCollectorNameToDeadTimesMap() {
        return collectorNameToDeadTimesMap;
    }

    /**
     * @param collectorNameToDeadTimesMap the collectorNameToDeadTimesMap to set
     */
    public void setCollectorNameToDeadTimesMap(Map<String, Double> collectorNameToDeadTimesMap) {
        this.collectorNameToDeadTimesMap = collectorNameToDeadTimesMap;
    }

    /**
     * @return the collectorNameToRelativeGainsUnctMap
     */
    public Map<String, Double> getCollectorNameToRelativeGainsUnctMap() {
        return collectorNameToRelativeGainsUnctMap;
    }

    /**
     * @param collectorNameToRelativeGainsUnctMap the
     * collectorNameToRelativeGainsUnctMap to set
     */
    public void setCollectorNameToRelativeGainsUnctMap(Map<String, Double> collectorNameToRelativeGainsUnctMap) {
        this.collectorNameToRelativeGainsUnctMap = collectorNameToRelativeGainsUnctMap;
    }

    /**
     * @return the collectorNameToDeadTimesUnctMap
     */
    public Map<String, Double> getCollectorNameToDeadTimesUnctMap() {
        return collectorNameToDeadTimesUnctMap;
    }

    /**
     * @param collectorNameToDeadTimesUnctMap the
     * collectorNameToDeadTimesUnctMap to set
     */
    public void setCollectorNameToDeadTimesUnctMap(Map<String, Double> collectorNameToDeadTimesUnctMap) {
        this.collectorNameToDeadTimesUnctMap = collectorNameToDeadTimesUnctMap;
    }

    /**
     * @return the collectorNameToResistorMap
     */
    public Map<String, FaradayCollectorModel.ResistorEnum> getCollectorNameToResistorMap() {
        return collectorNameToResistorMap;
    }

    /**
     * @param collectorNameToResistorMap the collectorNameToResistorMap to set
     */
    public void setCollectorNameToResistorMap(Map<String, FaradayCollectorModel.ResistorEnum> collectorNameToResistorMap) {
        this.collectorNameToResistorMap = collectorNameToResistorMap;
    }

    /**
     * @return the collectorNameToAmpNoiseMap
     */
    public Map<String, Double> getCollectorNameToAmpNoiseMap() {
        return collectorNameToAmpNoiseMap;
    }

    /**
     * @param collectorNameToAmpNoiseMap the collectorNameToAmpNoiseMap to set
     */
    public void setCollectorNameToAmpNoiseMap(Map<String, Double> collectorNameToAmpNoiseMap) {
        this.collectorNameToAmpNoiseMap = collectorNameToAmpNoiseMap;
    }

    /**
     * @return the usingFullPropagation
     */
    public boolean isUsingFullPropagation() {
        return usingFullPropagation;
    }

    /**
     * @param usingFullPropagation the usingFullPropagation to set
     */
    public void setUsingFullPropagation(boolean usingFullPropagation) {
        this.usingFullPropagation = usingFullPropagation;
    }

    /**
     * @return the leftShadeCount
     */
    public int getLeftShadeCount() {
        return leftShadeCount;
    }

    /**
     * @param leftShadeCount the leftShadeCount to set
     */
    public void setLeftShadeCount(int leftShadeCount) {
        this.leftShadeCount = leftShadeCount;
    }

    /**
     * @return the primaryMineralStandardModel
     */
    public AbstractRatiosDataModel getPrimaryMineralStandardModel() {
        return primaryMineralStandardModel;
    }

    /**
     * @param primaryMineralStandardModel the primaryMineralStandardModel to set
     */
    public void setPrimaryMineralStandardModel(AbstractRatiosDataModel primaryMineralStandardModel) {
        this.primaryMineralStandardModel = primaryMineralStandardModel;
    }

    /**
     * @return the baselineStartIndex
     */
    public int getBaselineStartIndex() {
        return baselineStartIndex;
    }

    /**
     * @param baselineStartIndex the baselineStartIndex to set
     */
    public void setBaselineStartIndex(int baselineStartIndex) {
        this.baselineStartIndex = baselineStartIndex;
    }

    /**
     * @return the baselineEndIndex
     */
    public int getBaselineEndIndex() {
        return baselineEndIndex;
    }

    /**
     * @param baselineEndIndex the baselineEndIndex to set
     */
    public void setBaselineEndIndex(int baselineEndIndex) {
        this.baselineEndIndex = baselineEndIndex;
    }

    /**
     * @return the peakStartIndex
     */
    public int getPeakStartIndex() {
        return peakStartIndex;
    }

    /**
     * @param peakStartIndex the peakStartIndex to set
     */
    public void setPeakStartIndex(int peakStartIndex) {
        this.peakStartIndex = peakStartIndex;
    }

    /**
     * @return the peakEndIndex
     */
    public int getPeakEndIndex() {
        return peakEndIndex;
    }

    /**
     * @param peakEndIndex the peakEndIndex to set
     */
    public void setPeakEndIndex(int peakEndIndex) {
        this.peakEndIndex = peakEndIndex;
    }
}
