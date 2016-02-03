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
public final class LaserchronElementIISetupUPb_C extends AbstractMassSpecSetup {

    private static LaserchronElementIISetupUPb_C instance = new LaserchronElementIISetupUPb_C();

    private LaserchronElementIISetupUPb_C() {
        super();
        NAME = "Laserchron Element II Setup";
        massSpecType = MassSpecTypeEnum.SINGLE;
        VIRTUAL_COLLECTOR_COUNT = 18;
        COLLECTOR_DATA_FREQUENCY_MILLISECS = 325;
        countOfAcquisitions = 0;

        isotopeMappingModel = new IsotopeMappingModel();

        collectorNameToModelMap = new TreeMap<>();

        useConstantBackgroundFitFunction = false;

        this.commonLeadCorrectionHighestLevel = "NONE";

        AbstractCollectorModel singleCollector
                = //
                new IonCounterCollectorModel(//
                        "Single", //
                        new ValueModel("DeadTime", //
                                new BigDecimal(12.0e-9, //
                                        ReduxConstants.mathContext10), //
                                "ABS", //
                                new BigDecimal(1.0e-9, ReduxConstants.mathContext10), //
                                BigDecimal.ZERO), //
                        IonCounterCollectorModel.CollectedDataStyle.COUNTS);

        // from Noah June 2015 for Kansas
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
                IsotopesEnum.U235, singleCollector);
        isotopeMappingModel.getIsotopeToIntegrationTimeMap().put( //
                IsotopesEnum.U235, 0.020);

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

        isotopeMappingModel.getIsotopeToCollectorMap().put(//
                IsotopesEnum.Pb204, singleCollector);
        isotopeMappingModel.getIsotopeToIntegrationTimeMap().put( //
                IsotopesEnum.Pb204, 0.020);

        isotopeMappingModel.getIsotopeToCollectorMap().put(//
                IsotopesEnum.Hg202, singleCollector);
        isotopeMappingModel.getIsotopeToIntegrationTimeMap().put( //
                IsotopesEnum.Hg202, 0.020);

        isotopeMappingModel.getIsotopeToCollectorMap().put(//
                IsotopesEnum.Hf176, singleCollector);
        isotopeMappingModel.getIsotopeToIntegrationTimeMap().put( //
                IsotopesEnum.Hf176, 0.020);

        collectorNameToModelMap.put("Single", singleCollector);

    }

    /**
     *
     * @return
     */
    public static LaserchronElementIISetupUPb_C getInstance() {
        return instance;
    }

    /**
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
    public SortedSet<DataModelInterface> rawRatiosFactory(String[][] intensitiesScan, boolean isStandard, String fractionID, boolean usingFullPropagation, TripoliFraction tripoliFraction) {

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

        Hf176 = new RawIntensityDataModel( //
                IsotopeNames.Hf176, virtualCollectors.get(1 - 1), virtualCollectors.get(10 - 1), COLLECTOR_DATA_FREQUENCY_MILLISECS,//
                isotopeMappingModel.getIsotopeToCollectorMap().get(IsotopesEnum.Hg202));
        genericIsotopeModels.add(Hf176);
        isotopeToRawIntensitiesMap.put(IsotopesEnum.Hf176, Hf176);

        isotopeMappingModel.setIsotopeToRawIntensitiesMap(isotopeToRawIntensitiesMap);

        virtualCollectorModelMapToFieldIndexes.put(Hf176, 0);
        virtualCollectorModelMapToFieldIndexes.put(Hg202, 1);
        virtualCollectorModelMapToFieldIndexes.put(Pb204, 2);
        virtualCollectorModelMapToFieldIndexes.put(Pb206, 3);
        virtualCollectorModelMapToFieldIndexes.put(Pb207, 4);
        virtualCollectorModelMapToFieldIndexes.put(Pb208, 5);
        virtualCollectorModelMapToFieldIndexes.put(Th232, 6);
        virtualCollectorModelMapToFieldIndexes.put(U235, 7);
        virtualCollectorModelMapToFieldIndexes.put(U238, 8);

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
