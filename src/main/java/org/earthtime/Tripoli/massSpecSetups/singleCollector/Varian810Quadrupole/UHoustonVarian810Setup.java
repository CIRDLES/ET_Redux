/*
 * LaserchronElementIISetupUPb_C
 *
 * Copyright 2006-2016 James F. Bowring and www.Earth-Time.org
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
package org.earthtime.Tripoli.massSpecSetups.singleCollector.Varian810Quadrupole;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
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
 * Specified by Kurt Sundell and Thomas Lage via email October 2016
 */
public final class UHoustonVarian810Setup extends AbstractMassSpecSetup {

    private static UHoustonVarian810Setup instance = new UHoustonVarian810Setup();

    private UHoustonVarian810Setup() {
        super();
        NAME = "U Houston Varian 810 Setup";
        massSpecType = MassSpecTypeEnum.SINGLE;
        VIRTUAL_COLLECTOR_COUNT = 18;
        COLLECTOR_DATA_FREQUENCY_MILLISECS = 143;
        countOfAcquisitions = 0;

        isotopeMappingModel = new IsotopeMappingModel();

        collectorNameToModelMap = new TreeMap<>();

        useConstantBackgroundFitFunction = false;

        this.commonLeadCorrectionHighestLevel = "B2";

        AbstractCollectorModel singleCollector
                = new IonCounterCollectorModel(//
                        "Single", //
                        new ValueModel("DeadTime", //
                                new BigDecimal(20.0e-9, //
                                        ReduxConstants.mathContext10), //
                                "ABS", //
                                new BigDecimal(1.0e-9, ReduxConstants.mathContext10), //
                                BigDecimal.ZERO), //
                        IonCounterCollectorModel.CollectedDataStyle.SEM);

//Deadtime published in associated paper is 20 ns
/* Dwell time (ms)
202Hg n/a 10 = 0.01 seconds
204Pb n/a 30
206Pb n/a 10
207Pb n/a 20
208Pb n/a 10
232Th n/a 10
235U 20 n/a
238U 10 10
         */
/// per Thomas Lapen
// In the list of nuclides, below, "Hg204","Po208","U232","Pu238" machine outputs should be ignored.
// Hg202","Hg201","Pb204","Pb206","Pb207","Pb208","Th232","U238","xxxHg204",xxx"Po208","xxxU232",xxx"Pu238"

        isotopeMappingModel.getIsotopeToCollectorMap().put(//
                IsotopesEnum.U238, singleCollector);
        isotopeMappingModel.getIsotopeToIntegrationTimeMap().put( //
                IsotopesEnum.U238, 0.01);

        // U235 is calculated from U238
        isotopeMappingModel.getIsotopeToCollectorMap().put(//
                IsotopesEnum.U235, singleCollector);
        isotopeMappingModel.getIsotopeToIntegrationTimeMap().put( //
                IsotopesEnum.U235, 0.01);
        
        isotopeMappingModel.getIsotopeToCollectorMap().put(//
                IsotopesEnum.Th232, singleCollector);
        isotopeMappingModel.getIsotopeToIntegrationTimeMap().put( //
                IsotopesEnum.Th232, 0.01);

        isotopeMappingModel.getIsotopeToCollectorMap().put(//
                IsotopesEnum.Pb208, singleCollector);
        isotopeMappingModel.getIsotopeToIntegrationTimeMap().put( //
                IsotopesEnum.Pb208, 0.01);

        isotopeMappingModel.getIsotopeToCollectorMap().put(//
                IsotopesEnum.Pb207, singleCollector);
        isotopeMappingModel.getIsotopeToIntegrationTimeMap().put( //
                IsotopesEnum.Pb207, 0.02);

        isotopeMappingModel.getIsotopeToCollectorMap().put(//
                IsotopesEnum.Pb206, singleCollector);
        isotopeMappingModel.getIsotopeToIntegrationTimeMap().put( //
                IsotopesEnum.Pb206, 0.01);

        isotopeMappingModel.getIsotopeToCollectorMap().put(//
                IsotopesEnum.Pb204, singleCollector);
        isotopeMappingModel.getIsotopeToIntegrationTimeMap().put( //
                IsotopesEnum.Pb204, 0.03);

        isotopeMappingModel.getIsotopeToCollectorMap().put(//
                IsotopesEnum.Hg202, singleCollector);
        isotopeMappingModel.getIsotopeToIntegrationTimeMap().put( //
                IsotopesEnum.Hg202, 0.01);

        isotopeMappingModel.getIsotopeToCollectorMap().put(//
                IsotopesEnum.Hg201, singleCollector);
        isotopeMappingModel.getIsotopeToIntegrationTimeMap().put( //
                IsotopesEnum.Hg201, 0.01);

        collectorNameToModelMap.put("Single", singleCollector);

    }

    /**
     *
     * @return
     */
    public static UHoustonVarian810Setup getInstance() {
        return instance;
    }

    /**
     *
     *
     * @param intensitiesScan
     * @param fractionID the value of fractionID
     * @param usingFullPropagation the value of usingFullPropagation
     * @param tripoliFraction the value of tripoliFraction
     * @return the
     * java.util.SortedSet<org.earthtime.Tripoli.dataModels.DataModelInterface>
     */
    @Override
    public SortedSet<DataModelInterface> rawRatiosFactory(String[][] intensitiesScan, String fractionID, boolean usingFullPropagation, TripoliFraction tripoliFraction) {

        countOfAcquisitions = intensitiesScan.length;

        return rawRatiosFactoryRevised();
    }

    /**
     * yRevised(); }
     *
     * /**
     *
     *
     * @param intensitiesScan
     * @param isStandard the value of isStandard
     * @param fractionID the value of fractionID
     * @param usingFullPropagation the value of usingFullPropagation
     * @param tripoliFraction the value of tripoliFraction
     * @return the
     * java.util.SortedSet<org.earthtime.Tripoli.dataModels.DataModelInterface>
     */
    @Override
    public SortedSet<DataModelInterface> rawRatiosFactoryRevised() {

        virtualCollectors = new ArrayList<>(VIRTUAL_COLLECTOR_COUNT);
        for (int i = 0; i < VIRTUAL_COLLECTOR_COUNT; i++) {
            virtualCollectors.add(new VirtualCollectorModel(i + 1));
        }

        // background
        virtualCollectors.get(9 - 1).updateCollector(true);
        virtualCollectors.get(8 - 1).updateCollector(true);
        virtualCollectors.get(7 - 1).updateCollector(true);
        virtualCollectors.get(6 - 1).updateCollector(true);
        virtualCollectors.get(5 - 1).updateCollector(true);
        virtualCollectors.get(4 - 1).updateCollector(true);
        virtualCollectors.get(3 - 1).updateCollector(true);
        virtualCollectors.get(2 - 1).updateCollector(true);
        virtualCollectors.get(1 - 1).updateCollector(true);
        // on peak
        virtualCollectors.get(18 - 1).updateCollector(false);
        virtualCollectors.get(17 - 1).updateCollector(false);
        virtualCollectors.get(16 - 1).updateCollector(false);
        virtualCollectors.get(15 - 1).updateCollector(false);
        virtualCollectors.get(14 - 1).updateCollector(false);
        virtualCollectors.get(13 - 1).updateCollector(false);
        virtualCollectors.get(12 - 1).updateCollector(false);
        virtualCollectors.get(11 - 1).updateCollector(false);
        virtualCollectors.get(10 - 1).updateCollector(false);

        // isotope models
        genericIsotopeModels = new TreeSet<>();
        U238 = new RawIntensityDataModel( //
                IsotopeNames.U238, virtualCollectors.get(9 - 1), virtualCollectors.get(18 - 1), COLLECTOR_DATA_FREQUENCY_MILLISECS,//
                isotopeMappingModel.getIsotopeToCollectorMap().get(IsotopesEnum.U238));
        genericIsotopeModels.add(U238);
        isotopeToRawIntensitiesMap.put(IsotopesEnum.U238, U238);

        U235 = new RawIntensityDataModel( //
                IsotopeNames.U235, virtualCollectors.get(8 - 1), virtualCollectors.get(17 - 1), COLLECTOR_DATA_FREQUENCY_MILLISECS,//
                isotopeMappingModel.getIsotopeToCollectorMap().get(IsotopesEnum.U235));
        genericIsotopeModels.add(U235);
        isotopeToRawIntensitiesMap.put(IsotopesEnum.U235, U235);

        Th232 = new RawIntensityDataModel( //
                IsotopeNames.Th232, virtualCollectors.get(7 - 1), virtualCollectors.get(16 - 1), COLLECTOR_DATA_FREQUENCY_MILLISECS,//
                isotopeMappingModel.getIsotopeToCollectorMap().get(IsotopesEnum.Th232));
        genericIsotopeModels.add(Th232);
        isotopeToRawIntensitiesMap.put(IsotopesEnum.Th232, Th232);

        Pb208 = new RawIntensityDataModel( //
                IsotopeNames.Pb208, virtualCollectors.get(6 - 1), virtualCollectors.get(15 - 1), COLLECTOR_DATA_FREQUENCY_MILLISECS,//
                isotopeMappingModel.getIsotopeToCollectorMap().get(IsotopesEnum.Pb208));
        genericIsotopeModels.add(Pb208);
        isotopeToRawIntensitiesMap.put(IsotopesEnum.Pb208, Pb208);

        Pb207 = new RawIntensityDataModel( //
                IsotopeNames.Pb207, virtualCollectors.get(5 - 1), virtualCollectors.get(14 - 1), COLLECTOR_DATA_FREQUENCY_MILLISECS,//
                isotopeMappingModel.getIsotopeToCollectorMap().get(IsotopesEnum.Pb207));
        genericIsotopeModels.add(Pb207);
        isotopeToRawIntensitiesMap.put(IsotopesEnum.Pb207, Pb207);

        Pb206 = new RawIntensityDataModel( //
                IsotopeNames.Pb206, virtualCollectors.get(4 - 1), virtualCollectors.get(13 - 1), COLLECTOR_DATA_FREQUENCY_MILLISECS,//
                isotopeMappingModel.getIsotopeToCollectorMap().get(IsotopesEnum.Pb206));
        genericIsotopeModels.add(Pb206);
        isotopeToRawIntensitiesMap.put(IsotopesEnum.Pb206, Pb206);

        Pb204 = new RawIntensityDataModel( //
                IsotopeNames.Pb204, virtualCollectors.get(3 - 1), virtualCollectors.get(12 - 1), COLLECTOR_DATA_FREQUENCY_MILLISECS,//
                isotopeMappingModel.getIsotopeToCollectorMap().get(IsotopesEnum.Pb204));
        genericIsotopeModels.add(Pb204);
        isotopeToRawIntensitiesMap.put(IsotopesEnum.Pb204, Pb204);

        Hg202 = new RawIntensityDataModel( //
                IsotopeNames.Hg202, virtualCollectors.get(2 - 1), virtualCollectors.get(11 - 1), COLLECTOR_DATA_FREQUENCY_MILLISECS,//
                isotopeMappingModel.getIsotopeToCollectorMap().get(IsotopesEnum.Hg202));
        genericIsotopeModels.add(Hg202);
        isotopeToRawIntensitiesMap.put(IsotopesEnum.Hg202, Hg202);

        Hg201 = new RawIntensityDataModel( //
                IsotopeNames.Hg201, virtualCollectors.get(1 - 1), virtualCollectors.get(10 - 1), COLLECTOR_DATA_FREQUENCY_MILLISECS,//
                isotopeMappingModel.getIsotopeToCollectorMap().get(IsotopesEnum.Hg201));
        genericIsotopeModels.add(Hg201);
        isotopeToRawIntensitiesMap.put(IsotopesEnum.Hg201, Hg201);

        isotopeMappingModel.setIsotopeToRawIntensitiesMap(isotopeToRawIntensitiesMap);

        virtualCollectorModelMapToFieldIndexes = new HashMap<>();
        getVirtualCollectorModelMapToFieldIndexes().put(Hg201, 0);
        getVirtualCollectorModelMapToFieldIndexes().put(Hg202, 1);
        getVirtualCollectorModelMapToFieldIndexes().put(Pb204, 2);
        getVirtualCollectorModelMapToFieldIndexes().put(Pb206, 3);
        getVirtualCollectorModelMapToFieldIndexes().put(Pb207, 4);
        getVirtualCollectorModelMapToFieldIndexes().put(Pb208, 5);
        getVirtualCollectorModelMapToFieldIndexes().put(Th232, 6);
        getVirtualCollectorModelMapToFieldIndexes().put(U235, 7);
        getVirtualCollectorModelMapToFieldIndexes().put(U238, 8);

        // raw ratios
        rawRatios = new TreeSet<>();
        DataModelInterface r206_238w = new RawRatioDataModel(RawRatioNames.r206_238w, Pb206, U238, true, false, COLLECTOR_DATA_FREQUENCY_MILLISECS);
        rawRatios.add(r206_238w);
        DataModelInterface r206_207w = new RawRatioDataModel(RawRatioNames.r206_207w, Pb206, Pb207, true, false, COLLECTOR_DATA_FREQUENCY_MILLISECS);
        rawRatios.add(r206_207w);
        DataModelInterface r208_232w = new RawRatioDataModel(RawRatioNames.r208_232w, Pb208, Th232, true, false, COLLECTOR_DATA_FREQUENCY_MILLISECS);
        rawRatios.add(r208_232w);

        // special case to handle mercury isotope
        rawRatios.add(new RawRatioDataModel(RawRatioNames.r202_202w, Hg202, Hg202, false, false, COLLECTOR_DATA_FREQUENCY_MILLISECS));

        // oct 2014 to handle B schemas for common lead correction
        DataModelInterface r206_204w = new RawRatioDataModel(RawRatioNames.r206_204w, Pb206, Pb204, false, true, COLLECTOR_DATA_FREQUENCY_MILLISECS);
        rawRatios.add(r206_204w);
        DataModelInterface r207_204w = new RawRatioDataModel(RawRatioNames.r207_204w, Pb207, Pb204, false, true, COLLECTOR_DATA_FREQUENCY_MILLISECS);
        rawRatios.add(r207_204w);
        DataModelInterface r208_204w = new RawRatioDataModel(RawRatioNames.r208_204w, Pb208, Pb204, false, true, COLLECTOR_DATA_FREQUENCY_MILLISECS);
        rawRatios.add(r208_204w);

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