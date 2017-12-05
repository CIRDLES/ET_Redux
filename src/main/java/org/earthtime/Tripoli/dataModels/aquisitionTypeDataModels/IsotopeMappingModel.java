/*
 * IsotopeMappingModel.java
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
package org.earthtime.Tripoli.dataModels.aquisitionTypeDataModels;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import org.earthtime.Tripoli.dataModels.DataModelInterface;
import org.earthtime.Tripoli.dataModels.RawIntensityDataModel;
import org.earthtime.Tripoli.dataModels.collectorModels.AbstractCollectorModel;
import org.earthtime.isotopes.IsotopesEnum;

/**
 *
 * @author James F. Bowring
 */
public class IsotopeMappingModel implements Serializable {

    // Class variables
    private static final long serialVersionUID = -5162135115838746654L;
    /**
     *
     */
    protected Map<IsotopesEnum, AbstractCollectorModel> isotopeToCollectorMap;
    /**
     * IntegrationTime is the same for all Isotopes in StaticAquisition, but
     * varies for SingleCollector Thus for StaticAquisition integrationTime =
     * cycleDuration
     */
    protected Map<IsotopesEnum, Double> isotopeToIntegrationTimeMap;

    /**
     *
     */
    protected Map<IsotopesEnum, DataModelInterface> isotopeToRawIntensitiesMap;

    /**
     *
     */
    public IsotopeMappingModel() {

        isotopeToCollectorMap = new EnumMap<>(IsotopesEnum.class);
        isotopeToIntegrationTimeMap = new EnumMap<>(IsotopesEnum.class);
        isotopeToRawIntensitiesMap = new EnumMap<>(IsotopesEnum.class);

    }

    /**
     *
     */
    public void calculateAllIntensityMatrixSDiagonals() {

        Iterator<IsotopesEnum> isotopeToRawIntensitiesMapIterator = isotopeToRawIntensitiesMap.keySet().iterator();
        while (isotopeToRawIntensitiesMapIterator.hasNext()) {
            IsotopesEnum isotopeKey = isotopeToRawIntensitiesMapIterator.next();

            DataModelInterface rawIntensityDataModel = isotopeToRawIntensitiesMap.get(isotopeKey);
            double integrationTime = isotopeToIntegrationTimeMap.get(isotopeKey);

            ((RawIntensityDataModel) rawIntensityDataModel).calculateIntensityMatrixSDiagonal(integrationTime);

        }

        System.out.println("***  calculateAllIntensityMatrixSDiagonals");

    }

    /**
     * @return the isotopeToCollectorMap
     */
    public Map<IsotopesEnum, AbstractCollectorModel> getIsotopeToCollectorMap() {
        return isotopeToCollectorMap;
    }

    /**
     * @param isotopeToCollectorMap the isotopeToCollectorMap to set
     */
    public void setIsotopeToCollectorMap(Map<IsotopesEnum, AbstractCollectorModel> isotopeToCollectorMap) {
        this.isotopeToCollectorMap = isotopeToCollectorMap;
    }

    /**
     * @return the isotopeToIntegrationTimeMap
     */
    public Map<IsotopesEnum, Double> getIsotopeToIntegrationTimeMap() {
        return isotopeToIntegrationTimeMap;
    }

    /**
     * @param isotopeToIntegrationTimeMap the isotopeToIntegrationTimeMap to set
     */
    public void setIsotopeToIntegrationTimeMap(Map<IsotopesEnum, Double> isotopeToIntegrationTimeMap) {
        this.isotopeToIntegrationTimeMap = isotopeToIntegrationTimeMap;
    }

    /**
     * @return the isotopeToRawIntensitiesMap
     */
    public Map<IsotopesEnum, DataModelInterface> getIsotopeToRawIntensitiesMap() {
        return isotopeToRawIntensitiesMap;
    }

    /**
     * @param isotopeToRawIntensitiesMap the isotopeToRawIntensitiesMap to set
     */
    public void setIsotopeToRawIntensitiesMap(Map<IsotopesEnum, DataModelInterface> isotopeToRawIntensitiesMap) {
        this.isotopeToRawIntensitiesMap = isotopeToRawIntensitiesMap;
    }
}
