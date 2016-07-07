/*
 * CottlesNUPlasmaSetupUPbFarTRA.java
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
package org.earthtime.Tripoli.massSpecSetups.multiCollector.NUPlasma;

import java.io.Serializable;
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
import org.earthtime.Tripoli.dataModels.collectorModels.FaradayCollectorModel;
import org.earthtime.Tripoli.dataModels.collectorModels.FaradayCollectorModel.ResistorEnum;
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
public final class CottlesNUPlasmaSetupUPbFarTRA extends AbstractMassSpecSetup implements //
        Comparable<AbstractMassSpecSetup>,
        Serializable {

    // Class variables
    private static final long serialVersionUID = 3425085745353656230L;
    private static CottlesNUPlasmaSetupUPbFarTRA instance = new CottlesNUPlasmaSetupUPbFarTRA();

    private CottlesNUPlasmaSetupUPbFarTRA() {
        super();
        NAME = "Cottle's Santa Barbara NUPlasma Faraday TRA Setup";
        massSpecType = MassSpecTypeEnum.MULTI;
        VIRTUAL_COLLECTOR_COUNT = 12;
        COLLECTOR_DATA_FREQUENCY_MILLISECS = 200;
        countOfAcquisitions = 0;

        isotopeMappingModel = new IsotopeMappingModel();

        useConstantBackgroundFitFunction = true;//false;

        this.commonLeadCorrectionHighestLevel = "B2";

        // initialize machine
        // Based on email from Cottle June 1 2015
        /*
         >Line 6: ".2" refers to the integration time in seconds i.e. each line of
            >data is integrated over 200ms. We change this anywhere from 0.2
            >(minimum) to 1s in the software.
            >
            >Line 61: this text is added by the user in the NP software for a sample
            >name.
            >
            >Line 62: time stamp. Stamp is the time at the end of the run (i.e. when
            >the run finished), so need to count back from this point
            >
            >Line 64: beginning of the data. All data are in volts.
            >Column1: U238 (F)
            >Column2: Th232 (F)
            >Column3: Pb208 (IC)
            >Column4: Pb207 (IC)
            >Column5: Pb206 (IC)
            >Column6: Pb+Hg204 (IC)
            >Column7: time slice in increments of 1
            >Column8: 0 or 1, used by Nu Plasma software, can be ignored
         */
        // this section using AbstractCollectorModel is part of a mjor refactoring from Setp 2012 to handle machine setups
        // and uncertainty propagation more robustly
        collectorNameToModelMap = new TreeMap<>();
        AbstractCollectorModel ExHi
                = new FaradayCollectorModel(NUPlasmaCollectorsEnum.ExHi.name(), ResistorEnum.OHMS3e11, ResistorEnum.OHMS3e11.getDefaultAmplifierNoiseVariance());
        isotopeMappingModel.getIsotopeToCollectorMap().put(//
                IsotopesEnum.U238, ExHi);
        isotopeMappingModel.getIsotopeToIntegrationTimeMap().put( //
                IsotopesEnum.U238, COLLECTOR_DATA_FREQUENCY_MILLISECS / 1000.0);
        collectorNameToModelMap.put(NUPlasmaCollectorsEnum.ExHi.name(), ExHi);

        AbstractCollectorModel H2
                = new FaradayCollectorModel(NUPlasmaCollectorsEnum.H2.name(), ResistorEnum.OHMS3e11, ResistorEnum.OHMS3e11.getDefaultAmplifierNoiseVariance());
        isotopeMappingModel.getIsotopeToCollectorMap().put(//
                IsotopesEnum.Th232, H2);
        isotopeMappingModel.getIsotopeToIntegrationTimeMap().put( //
                IsotopesEnum.Th232, COLLECTOR_DATA_FREQUENCY_MILLISECS / 1000.0);
        collectorNameToModelMap.put(NUPlasmaCollectorsEnum.H2.name(), H2);

        AbstractCollectorModel IC0
                = new IonCounterCollectorModel(//
                        NUPlasmaCollectorsEnum.IC0.name(), new ValueModel("DeadTime", new BigDecimal(12.0e-9, ReduxConstants.mathContext10), //
                                "ABS", new BigDecimal(1.0e-9, ReduxConstants.mathContext10), BigDecimal.ZERO), //
                        IonCounterCollectorModel.CollectedDataStyle.VOLTS);
        isotopeMappingModel.getIsotopeToCollectorMap().put(//
                IsotopesEnum.Pb208, IC0);
        isotopeMappingModel.getIsotopeToIntegrationTimeMap().put( //
                IsotopesEnum.Pb208, COLLECTOR_DATA_FREQUENCY_MILLISECS / 1000.0);
        collectorNameToModelMap.put(NUPlasmaCollectorsEnum.IC0.name(), IC0);

        AbstractCollectorModel IC1
                = new IonCounterCollectorModel(//
                        NUPlasmaCollectorsEnum.IC1.name(), new ValueModel("DeadTime", new BigDecimal(12.0e-9, ReduxConstants.mathContext10), //
                                "ABS", new BigDecimal(1.0e-9, ReduxConstants.mathContext10), BigDecimal.ZERO), //
                        IonCounterCollectorModel.CollectedDataStyle.VOLTS);
        isotopeMappingModel.getIsotopeToCollectorMap().put(//
                IsotopesEnum.Pb207, IC1);
        isotopeMappingModel.getIsotopeToIntegrationTimeMap().put( //
                IsotopesEnum.Pb207, COLLECTOR_DATA_FREQUENCY_MILLISECS / 1000.0);
        collectorNameToModelMap.put(NUPlasmaCollectorsEnum.IC2.name(), IC1);

        AbstractCollectorModel IC2
                = new IonCounterCollectorModel(//
                        NUPlasmaCollectorsEnum.IC2.name(), new ValueModel("DeadTime", new BigDecimal(12.0e-9, ReduxConstants.mathContext10), //
                                "ABS", new BigDecimal(1.0e-9, ReduxConstants.mathContext10), BigDecimal.ZERO), //
                        IonCounterCollectorModel.CollectedDataStyle.VOLTS);
        isotopeMappingModel.getIsotopeToCollectorMap().put(//
                IsotopesEnum.Pb206, IC2);
        isotopeMappingModel.getIsotopeToIntegrationTimeMap().put( //
                IsotopesEnum.Pb206, COLLECTOR_DATA_FREQUENCY_MILLISECS / 1000.0);
        collectorNameToModelMap.put(NUPlasmaCollectorsEnum.IC2.name(), IC2);

        AbstractCollectorModel IC3
                = new IonCounterCollectorModel(//
                        NUPlasmaCollectorsEnum.IC3.name(), new ValueModel("DeadTime", new BigDecimal(12.0e-9, ReduxConstants.mathContext10), //
                                "ABS", new BigDecimal(1.0e-9, ReduxConstants.mathContext10), BigDecimal.ZERO), //
                        IonCounterCollectorModel.CollectedDataStyle.VOLTS);
        isotopeMappingModel.getIsotopeToCollectorMap().put(//
                IsotopesEnum.Pb204, IC3);
        isotopeMappingModel.getIsotopeToIntegrationTimeMap().put( //
                IsotopesEnum.Pb204, COLLECTOR_DATA_FREQUENCY_MILLISECS / 1000.0);
        collectorNameToModelMap.put(NUPlasmaCollectorsEnum.IC2.name(), IC3);

    }

    /**
     *
     * @param integrationTime
     */
    @Override
    public void assignIntegrationTime(double integrationTime) {
        COLLECTOR_DATA_FREQUENCY_MILLISECS = (long) (integrationTime * 1000.0);
    }

    /**
     *
     * @return
     */
    public static CottlesNUPlasmaSetupUPbFarTRA getInstance() {
        return instance;
    }

    @Override
    public SortedSet<DataModelInterface> rawRatiosFactory( //
            String[][] intensitiesScan, String fractionID, boolean usingFullPropagation, TripoliFraction tripoliFraction) {

        countOfAcquisitions = intensitiesScan.length;

        return rawRatiosFactoryRevised();
    }
    //

    /**
     *
     *
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
        virtualCollectors.get(6 - 1).updateCollector(true);
        virtualCollectors.get(5 - 1).updateCollector(true);
        virtualCollectors.get(4 - 1).updateCollector(true);
        virtualCollectors.get(3 - 1).updateCollector(true);
        virtualCollectors.get(2 - 1).updateCollector(true);
        virtualCollectors.get(1 - 1).updateCollector(true);
        // on peak
        virtualCollectors.get(12 - 1).updateCollector(false);
        virtualCollectors.get(11 - 1).updateCollector(false);
        virtualCollectors.get(10 - 1).updateCollector(false);
        virtualCollectors.get(9 - 1).updateCollector(false);
        virtualCollectors.get(8 - 1).updateCollector(false);
        virtualCollectors.get(7 - 1).updateCollector(false);

        // new set of isotope models for this fraction
        genericIsotopeModels = new TreeSet<>();
        U238 = new RawIntensityDataModel( //
                IsotopeNames.U238, virtualCollectors.get(6 - 1), virtualCollectors.get(12 - 1), COLLECTOR_DATA_FREQUENCY_MILLISECS,//
                isotopeMappingModel.getIsotopeToCollectorMap().get(IsotopesEnum.U238));
        genericIsotopeModels.add(U238);
        isotopeToRawIntensitiesMap.put(IsotopesEnum.U238, U238);

        Th232 = new RawIntensityDataModel( //
                IsotopeNames.Th232, virtualCollectors.get(5 - 1), virtualCollectors.get(11 - 1), COLLECTOR_DATA_FREQUENCY_MILLISECS,//
                isotopeMappingModel.getIsotopeToCollectorMap().get(IsotopesEnum.Th232));
        genericIsotopeModels.add(Th232);
        isotopeToRawIntensitiesMap.put(IsotopesEnum.Th232, Th232);

        Pb208 = new RawIntensityDataModel( //
                IsotopeNames.Pb208, virtualCollectors.get(4 - 1), virtualCollectors.get(10 - 1), COLLECTOR_DATA_FREQUENCY_MILLISECS,//
                isotopeMappingModel.getIsotopeToCollectorMap().get(IsotopesEnum.Pb208));
        genericIsotopeModels.add(Pb208);
        isotopeToRawIntensitiesMap.put(IsotopesEnum.Pb208, Pb208);

        Pb207 = new RawIntensityDataModel( //
                IsotopeNames.Pb207, virtualCollectors.get(3 - 1), virtualCollectors.get(9 - 1), COLLECTOR_DATA_FREQUENCY_MILLISECS,//
                isotopeMappingModel.getIsotopeToCollectorMap().get(IsotopesEnum.Pb207));
        genericIsotopeModels.add(Pb207);
        isotopeToRawIntensitiesMap.put(IsotopesEnum.Pb207, Pb207);

        Pb206 = new RawIntensityDataModel( //
                IsotopeNames.Pb206, virtualCollectors.get(2 - 1), virtualCollectors.get(8 - 1), COLLECTOR_DATA_FREQUENCY_MILLISECS,//
                isotopeMappingModel.getIsotopeToCollectorMap().get(IsotopesEnum.Pb206));
        genericIsotopeModels.add(Pb206);
        isotopeToRawIntensitiesMap.put(IsotopesEnum.Pb206, Pb206);

        Pb204 = new RawIntensityDataModel( //
                IsotopeNames.Pb204, virtualCollectors.get(1 - 1), virtualCollectors.get(7 - 1), COLLECTOR_DATA_FREQUENCY_MILLISECS,//
                isotopeMappingModel.getIsotopeToCollectorMap().get(IsotopesEnum.Pb204));
        genericIsotopeModels.add(Pb204);
        isotopeToRawIntensitiesMap.put(IsotopesEnum.Pb204, Pb204);

        isotopeMappingModel.setIsotopeToRawIntensitiesMap(isotopeToRawIntensitiesMap);

        virtualCollectorModelMapToFieldIndexes = new HashMap<>();
        getVirtualCollectorModelMapToFieldIndexes().put(U238, 0);
        getVirtualCollectorModelMapToFieldIndexes().put(Th232, 1);
        getVirtualCollectorModelMapToFieldIndexes().put(Pb208, 2);
        getVirtualCollectorModelMapToFieldIndexes().put(Pb207, 3);
        getVirtualCollectorModelMapToFieldIndexes().put(Pb206, 4);
        getVirtualCollectorModelMapToFieldIndexes().put(Pb204, 5);

        // raw ratios
        // oct 2012 defaults for fractionation corrections are set true but later missing ratio at standard will override 
        // in TripoliFraction.updateRawRatioDataModelsWithPrimaryStandardValue if necessary
        rawRatios = new TreeSet<>();
        DataModelInterface r206_238w = new RawRatioDataModel(RawRatioNames.r206_238w, Pb206, U238, true, false, COLLECTOR_DATA_FREQUENCY_MILLISECS);
        rawRatios.add(r206_238w);
        DataModelInterface r206_207w = new RawRatioDataModel(RawRatioNames.r206_207w, Pb206, Pb207, true, false, COLLECTOR_DATA_FREQUENCY_MILLISECS);
        rawRatios.add(r206_207w);
        DataModelInterface r208_232w = new RawRatioDataModel(RawRatioNames.r208_232w, Pb208, Th232, true, false, COLLECTOR_DATA_FREQUENCY_MILLISECS);
        rawRatios.add(r208_232w);

        // oct 2014 to handle B schemas for common lead correction
        DataModelInterface r206_204w = new RawRatioDataModel(RawRatioNames.r206_204w, Pb206, Pb204, false, true, COLLECTOR_DATA_FREQUENCY_MILLISECS);
        rawRatios.add(r206_204w);
        DataModelInterface r207_204w = new RawRatioDataModel(RawRatioNames.r207_204w, Pb207, Pb204, false, true, COLLECTOR_DATA_FREQUENCY_MILLISECS);
        rawRatios.add(r207_204w);
        DataModelInterface r208_204w = new RawRatioDataModel(RawRatioNames.r208_204w, Pb208, Pb204, false, true, COLLECTOR_DATA_FREQUENCY_MILLISECS);
        rawRatios.add(r208_204w);

        return rawRatios;
    }

 
}
