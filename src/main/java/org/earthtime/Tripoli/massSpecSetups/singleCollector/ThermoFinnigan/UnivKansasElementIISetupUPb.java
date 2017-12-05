/*
 * UnivKansasElementIISetupUPb
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
package org.earthtime.Tripoli.massSpecSetups.singleCollector.ThermoFinnigan;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import org.earthtime.Tripoli.dataModels.DataModelInterface;
import org.earthtime.Tripoli.dataModels.RawIntensityDataModel;
import org.earthtime.Tripoli.dataModels.RawRatioDataModel;
import org.earthtime.Tripoli.dataModels.VirtualCollectorModel;
import org.earthtime.Tripoli.dataModels.aquisitionTypeDataModels.IsotopeMappingModel;
import org.earthtime.Tripoli.dataModels.collectorModels.AbstractCollectorModel;
import org.earthtime.Tripoli.dataModels.collectorModels.IonCounterCollectorModel;
import org.earthtime.Tripoli.fractions.TripoliFraction;
import org.earthtime.Tripoli.massSpecSetups.AbstractMassSpecSetup;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.dataDictionaries.IsotopeNames;
import org.earthtime.dataDictionaries.MassSpecTypeEnum;
import org.earthtime.dataDictionaries.RawRatioNames;
import org.earthtime.isotopes.IsotopesEnum;

/**
 *
 * @author James F. Bowring
 */
public final class UnivKansasElementIISetupUPb extends AbstractMassSpecSetup{

    private static UnivKansasElementIISetupUPb instance = new UnivKansasElementIISetupUPb();

    private UnivKansasElementIISetupUPb() {
        super();
        NAME = "University of Kansas Element II Setup";
        massSpecType = MassSpecTypeEnum.SINGLE;
        VIRTUAL_COLLECTOR_COUNT = 10;
        COLLECTOR_DATA_FREQUENCY_MILLISECS = 214; //0.214410714 sec
        countOfAcquisitions = 0;

        isotopeMappingModel = new IsotopeMappingModel();

        collectorNameToModelMap = new TreeMap<>();

        useConstantBackgroundFitFunction = false;

        this.commonLeadCorrectionHighestLevel = "B2";

        AbstractCollectorModel singleCollector = //
                new IonCounterCollectorModel(//
                        "Single", //
                        new ValueModel("DeadTime", //
                                new BigDecimal(12.0e-9, //
                                        ReduxConstants.mathContext10), //
                                "ABS", //
                                new BigDecimal(1.0e-9, ReduxConstants.mathContext10), //
                                BigDecimal.ZERO), //
                        IonCounterCollectorModel.CollectedDataStyle.COUNTS);

        // from Noah June 2015
        //isotope	int. time (s)
        //206	0.032
        //207	0.080
        //208	0.016
        //232	0.020
        //238	0.020
        
        isotopeMappingModel.getIsotopeToCollectorMap().put(//
                IsotopesEnum.U238, singleCollector);
        isotopeMappingModel.getIsotopeToIntegrationTimeMap().put( //
                IsotopesEnum.U238, 0.020);

        isotopeMappingModel.getIsotopeToCollectorMap().put(//
                IsotopesEnum.Th232, singleCollector);
        isotopeMappingModel.getIsotopeToIntegrationTimeMap().put( //
                IsotopesEnum.Th232, 0.020);

        isotopeMappingModel.getIsotopeToCollectorMap().put(//
                IsotopesEnum.Pb208, singleCollector);
        isotopeMappingModel.getIsotopeToIntegrationTimeMap().put( //
                IsotopesEnum.Pb208, 0.016);

        isotopeMappingModel.getIsotopeToCollectorMap().put(//
                IsotopesEnum.Pb207, singleCollector);
        isotopeMappingModel.getIsotopeToIntegrationTimeMap().put( //
                IsotopesEnum.Pb207, 0.080);

        isotopeMappingModel.getIsotopeToCollectorMap().put(//
                IsotopesEnum.Pb206, singleCollector);
        isotopeMappingModel.getIsotopeToIntegrationTimeMap().put( //
                IsotopesEnum.Pb206, 0.032);

        collectorNameToModelMap.put("Single", singleCollector);

    }

    /**
     *
     * @return
     */
    public static UnivKansasElementIISetupUPb getInstance() {
        return instance;
    }

    /**
     *
     *
     * @param intensitiesScan
     * @param fractionID the value of fractionID
     * @param usingFullPropagation the value of usingFullPropagation
     * @param tripoliFraction the value of tripoliFraction
     * @return the java.util.SortedSet<org.earthtime.Tripoli.dataModels.DataModelInterface>
     */
    @Override
    public SortedSet<DataModelInterface> rawRatiosFactory(String[][] intensitiesScan, String fractionID, boolean usingFullPropagation, TripoliFraction tripoliFraction) {

        countOfAcquisitions = intensitiesScan.length;

        return rawRatiosFactoryRevised();
    }

/**
 * 
 * @return 
 */
    @Override
    public SortedSet<DataModelInterface> rawRatiosFactoryRevised() {

        virtualCollectors = new ArrayList<>(VIRTUAL_COLLECTOR_COUNT);
        for (int i = 0; i < VIRTUAL_COLLECTOR_COUNT; i++) {
            virtualCollectors.add(new VirtualCollectorModel(i + 1));
        }

        // background
        virtualCollectors.get(5 - 1).updateCollector(true);
        virtualCollectors.get(4 - 1).updateCollector(true);
        virtualCollectors.get(3 - 1).updateCollector(true);
        virtualCollectors.get(2 - 1).updateCollector(true);
        virtualCollectors.get(1 - 1).updateCollector(true);
        // on peak
        virtualCollectors.get(10 - 1).updateCollector(false);
        virtualCollectors.get(9 - 1).updateCollector(false);
        virtualCollectors.get(8 - 1).updateCollector(false);
        virtualCollectors.get(7 - 1).updateCollector(false);
        virtualCollectors.get(6 - 1).updateCollector(false);

        // isotope models
        genericIsotopeModels = new TreeSet<>();
        U238 = new RawIntensityDataModel( //
                IsotopeNames.U238, virtualCollectors.get(5 - 1), virtualCollectors.get(10 - 1), COLLECTOR_DATA_FREQUENCY_MILLISECS,//
                isotopeMappingModel.getIsotopeToCollectorMap().get(IsotopesEnum.U238));
        genericIsotopeModels.add(U238);
        isotopeToRawIntensitiesMap.put(IsotopesEnum.U238, U238);

        Th232 = new RawIntensityDataModel( //
                IsotopeNames.Th232, virtualCollectors.get(4 - 1), virtualCollectors.get(9 - 1), COLLECTOR_DATA_FREQUENCY_MILLISECS,//
                isotopeMappingModel.getIsotopeToCollectorMap().get(IsotopesEnum.Th232));
        genericIsotopeModels.add(Th232);
        isotopeToRawIntensitiesMap.put(IsotopesEnum.Th232, Th232);

        Pb208 = new RawIntensityDataModel( //
                IsotopeNames.Pb208, virtualCollectors.get(3 - 1), virtualCollectors.get(8 - 1), COLLECTOR_DATA_FREQUENCY_MILLISECS,//
                isotopeMappingModel.getIsotopeToCollectorMap().get(IsotopesEnum.Pb208));
        genericIsotopeModels.add(Pb208);
        isotopeToRawIntensitiesMap.put(IsotopesEnum.Pb208, Pb208);

        Pb207 = new RawIntensityDataModel( //
                IsotopeNames.Pb207, virtualCollectors.get(2 - 1), virtualCollectors.get(7 - 1), COLLECTOR_DATA_FREQUENCY_MILLISECS,//
                isotopeMappingModel.getIsotopeToCollectorMap().get(IsotopesEnum.Pb207));
        genericIsotopeModels.add(Pb207);
        isotopeToRawIntensitiesMap.put(IsotopesEnum.Pb207, Pb207);

        Pb206 = new RawIntensityDataModel( //
                IsotopeNames.Pb206, virtualCollectors.get(1 - 1), virtualCollectors.get(6 - 1), COLLECTOR_DATA_FREQUENCY_MILLISECS,//
                isotopeMappingModel.getIsotopeToCollectorMap().get(IsotopesEnum.Pb206));
        genericIsotopeModels.add(Pb206);
        isotopeToRawIntensitiesMap.put(IsotopesEnum.Pb206, Pb206);

        isotopeMappingModel.setIsotopeToRawIntensitiesMap(isotopeToRawIntensitiesMap);

        // raw ratios
        rawRatios = new TreeSet<>();
        DataModelInterface r206_238w = new RawRatioDataModel(RawRatioNames.r206_238w, Pb206, U238, true, false, COLLECTOR_DATA_FREQUENCY_MILLISECS);
        rawRatios.add(r206_238w);
        DataModelInterface r206_207w = new RawRatioDataModel(RawRatioNames.r206_207w, Pb206, Pb207, true, false, COLLECTOR_DATA_FREQUENCY_MILLISECS);
        rawRatios.add(r206_207w);
        DataModelInterface r208_232w = new RawRatioDataModel(RawRatioNames.r208_232w, Pb208, Th232, true, false, COLLECTOR_DATA_FREQUENCY_MILLISECS);
        rawRatios.add(r208_232w);

        return rawRatios;
    }

    /**
     *
     * @param integrationTime
     */
    @Override
    public void assignIntegrationTime(double integrationTime) {
        throw new UnsupportedOperationException("Not legal.");
    }

}
