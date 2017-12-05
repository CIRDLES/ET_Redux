/*
 * DataModelInterface.java
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
package org.earthtime.Tripoli.dataModels;

import org.earthtime.Tripoli.dataModels.collectorModels.AbstractCollectorModel;
import org.earthtime.dataDictionaries.RawRatioNames;

/**
 *
 * @author James F. Bowring
 */
public interface DataModelInterface {

    /**
     *
     * @param CollectorDataFrequencyMillisecs
     */
    public void setCollectorDataFrequencyMillisecs(long CollectorDataFrequencyMillisecs);

    /**
     *
     * @return
     */
    public long getCollectorDataFrequencyMillisecs();

    /**
     *
     * @param index
     * @param included
     */
    public void toggleOneDataAquisition(int index, boolean included);

    /**
     *
     * @return
     */
    public String getDataModelName();

    /**
     *
     * @return
     */
    public RawRatioNames getRawRatioModelName();

    /**
     *
     */
    public void calculateCorrectedRatioStatistics();

    /**
     *
     * @param propagateUncertainties
     * @param doApplyMaskingArray the value of doApplyMaskingArray
     * @param inLiveMode the value of inLiveMode
     */
    public void generateSetOfFitFunctions(boolean propagateUncertainties, boolean doApplyMaskingArray, boolean inLiveMode);

    /**
     *
     * @return
     */
    public double[] getOnPeakAquireTimesInSeconds();

    /**
     *
     * @return
     */
    public double[] getNormalizedOnPeakAquireTimes();

    /**
     *
     * @return
     */
    public double getStandardValue();

    /**
     *
     */
    public void applyMaskingArray();

    /**
     *
     * @return
     */
    public AbstractCollectorModel getCollectorModel();

    public boolean isUsedForCommonLeadCorrections();

    public boolean isBelowDetection();

    public boolean isForceMeanForCommonLeadRatios();

    public boolean[] getDataActiveMap();

    public boolean isUSING_FULL_PROPAGATION();

    public void setUSING_FULL_PROPAGATION(boolean usingFullPropagation);

}
