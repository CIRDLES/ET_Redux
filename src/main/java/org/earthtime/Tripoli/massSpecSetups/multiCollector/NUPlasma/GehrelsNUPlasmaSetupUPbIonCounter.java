/*
 * GehrelsNUPlasmaSetupUPbIonCounter.java
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
package org.earthtime.Tripoli.massSpecSetups.multiCollector.NUPlasma;

import java.io.Serializable;
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
import org.earthtime.Tripoli.dataModels.collectorModels.FaradayCollectorModel;
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
public final class GehrelsNUPlasmaSetupUPbIonCounter extends AbstractMassSpecSetup implements //
        Comparable<AbstractMassSpecSetup>,
        Serializable {

    // Class variables
    //private static final long serialVersionUID = -5116574729913272427L;
    private static GehrelsNUPlasmaSetupUPbIonCounter instance = new GehrelsNUPlasmaSetupUPbIonCounter();

    private GehrelsNUPlasmaSetupUPbIonCounter() {
        super();
        NAME = "Arizona Laserchron NUPlasma IonCounter Setup";
        massSpecType = MassSpecTypeEnum.MULTI;
        VIRTUAL_COLLECTOR_COUNT = 32;
        COLLECTOR_DATA_FREQUENCY_MILLISECS = 1000; //1 sec
        countOfAcquisitions = 0;

        isotopeMappingModel = new IsotopeMappingModel();

        useConstantBackgroundFitFunction = true;

        this.commonLeadCorrectionHighestLevel = "B2";

        // initialize machine
            /*
         * Mark Pecha of US LaserChron July 122 2011 email The 202 measurement
         * is in column 15 (for backgrounds) and column 31 (while on peak).
         * During the U-Th-Pb acquisition routine the collector designation is
         * as follows:
         *
         * 1, 17) ExHi=> 238U
         *
         * 2, 18) H2=> 232Th
         *
         * 3, 19) Hi=> open 4, 20) AX=> open 5, 21) L1=> open 6, 22) L2=> open
         * 7, 23) L3=> open 8, 24) L4=> open 9, 25) L5=> open
         *
         * 10, 26) L6=> 208Pb
         *
         * 11, 27) L7=> 207Pb
         *
         * 12, 28) L8=> 206Pb
         *
         * 13, 29) IC0=> 204
         *
         * 14, 30) IC1=> open
         *
         * 15, 31) IC2=> 202
         *
         * 16, 32) IC3=> open or 200
         *
         */
        // above is from faraday;
        // here is light-weight ion counter layout from george
        // 1, 17 >> 238
        // 2, 18 >> 232
        // 13, 29 >> 208
        // 14, 30 >> 207
        // 15, 31 >> 206
        // 16, 32 >> 204
        // this section using AbstractCollectorModel is part of a mjor refactoring from Setp 2012 to handle machine setups
        // and uncertainty propagation more robustly
        collectorNameToModelMap = new TreeMap<>();
        AbstractCollectorModel ExHi = //
                new FaradayCollectorModel(NUPlasmaCollectorsEnum.ExHi.name(), FaradayCollectorModel.ResistorEnum.OHMS3e11, FaradayCollectorModel.ResistorEnum.OHMS3e11.getDefaultAmplifierNoiseVariance());
        isotopeMappingModel.getIsotopeToCollectorMap().put(//
                IsotopesEnum.U238, ExHi);
        isotopeMappingModel.getIsotopeToIntegrationTimeMap().put( //
                IsotopesEnum.U238, COLLECTOR_DATA_FREQUENCY_MILLISECS / 1000.0);
        collectorNameToModelMap.put(NUPlasmaCollectorsEnum.ExHi.name(), ExHi);

        AbstractCollectorModel H2 = //
                new FaradayCollectorModel(NUPlasmaCollectorsEnum.H2.name(), FaradayCollectorModel.ResistorEnum.OHMS3e11, FaradayCollectorModel.ResistorEnum.OHMS3e11.getDefaultAmplifierNoiseVariance());
        isotopeMappingModel.getIsotopeToCollectorMap().put(//
                IsotopesEnum.Th232, H2);
        isotopeMappingModel.getIsotopeToIntegrationTimeMap().put( //
                IsotopesEnum.Th232, COLLECTOR_DATA_FREQUENCY_MILLISECS / 1000.0);
        collectorNameToModelMap.put(NUPlasmaCollectorsEnum.H2.name(), H2);

        AbstractCollectorModel IC0 = //
                new IonCounterCollectorModel(//
                        NUPlasmaICCollectors.IC0.name(), new ValueModel("DeadTime", new BigDecimal(12.0e-9, ReduxConstants.mathContext10), //
                                "ABS", new BigDecimal(1.0e-9, ReduxConstants.mathContext10), BigDecimal.ZERO), //
                        IonCounterCollectorModel.CollectedDataStyle.VOLTS);
        isotopeMappingModel.getIsotopeToCollectorMap().put(//
                IsotopesEnum.Pb208, IC0);
        isotopeMappingModel.getIsotopeToIntegrationTimeMap().put( //
                IsotopesEnum.Pb208, COLLECTOR_DATA_FREQUENCY_MILLISECS / 1000.0);
        collectorNameToModelMap.put(NUPlasmaICCollectors.IC0.name(), IC0);

        AbstractCollectorModel IC1 = //
                new IonCounterCollectorModel(//
                        NUPlasmaICCollectors.IC1.name(), new ValueModel("DeadTime", new BigDecimal(12.0e-9, ReduxConstants.mathContext10), //
                                "ABS", new BigDecimal(1.0e-9, ReduxConstants.mathContext10), BigDecimal.ZERO), //
                        IonCounterCollectorModel.CollectedDataStyle.VOLTS);
        isotopeMappingModel.getIsotopeToCollectorMap().put(//
                IsotopesEnum.Pb207, IC1);
        isotopeMappingModel.getIsotopeToIntegrationTimeMap().put( //
                IsotopesEnum.Pb207, COLLECTOR_DATA_FREQUENCY_MILLISECS / 1000.0);
        collectorNameToModelMap.put(NUPlasmaICCollectors.IC1.name(), IC1);

        AbstractCollectorModel IC2 = //
                new IonCounterCollectorModel(//
                        NUPlasmaICCollectors.IC2.name(), new ValueModel("DeadTime", new BigDecimal(12.0e-9, ReduxConstants.mathContext10), //
                                "ABS", new BigDecimal(1.0e-9, ReduxConstants.mathContext10), BigDecimal.ZERO), //
                        IonCounterCollectorModel.CollectedDataStyle.VOLTS);
        isotopeMappingModel.getIsotopeToCollectorMap().put(//
                IsotopesEnum.Pb206, IC2);
        isotopeMappingModel.getIsotopeToIntegrationTimeMap().put( //
                IsotopesEnum.Pb206, COLLECTOR_DATA_FREQUENCY_MILLISECS / 1000.0);
        collectorNameToModelMap.put(NUPlasmaICCollectors.IC2.name(), IC2);

        AbstractCollectorModel IC3 = //
                new IonCounterCollectorModel(//
                        NUPlasmaICCollectors.IC2.name(), new ValueModel("DeadTime", new BigDecimal(12.0e-9, ReduxConstants.mathContext10), //
                                "ABS", new BigDecimal(1.0e-9, ReduxConstants.mathContext10), BigDecimal.ZERO), //
                        IonCounterCollectorModel.CollectedDataStyle.VOLTS);
        isotopeMappingModel.getIsotopeToCollectorMap().put(//
                IsotopesEnum.Pb204, IC3);
        isotopeMappingModel.getIsotopeToIntegrationTimeMap().put( //
                IsotopesEnum.Pb204, COLLECTOR_DATA_FREQUENCY_MILLISECS / 1000.0);
        collectorNameToModelMap.put(NUPlasmaICCollectors.IC3.name(), IC3);

    }

    /**
     *
     * @param integrationTime
     */
    @Override
    public void assignIntegrationTime(double integrationTime) {
        COLLECTOR_DATA_FREQUENCY_MILLISECS = (long) (integrationTime * 1000.0);
    }

    @Override
    public SortedSet<DataModelInterface> rawRatiosFactoryRevised() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     */
    public enum NUPlasmaICCollectors {
        // here is light-weight ion counter layout from george
        // 1, 17 >> 238
        // 2, 18 >> 232
        // 13, 29 >> 208
        // 14, 30 >> 207
        // 15, 31 >> 206
        // 16, 32 >> 204

        /**
         *
         */
        ExHi("F", IsotopesEnum.U238),
        /**
         *
         */
        H2("F", IsotopesEnum.Th232),
        /**
         *
         */
        Hi("F", null),
        /**
         *
         */
        AX("F", null),
        /**
         *
         */
        L1("F", null),
        /**
         *
         */
        L2("F", null),
        /**
         *
         */
        L3("F", null),
        /**
         *
         */
        L4("F", null),
        /**
         *
         */
        L5("F", null),
        /**
         *
         */
        L6("F", null),
        /**
         *
         */
        L7("F", null),
        /**
         *
         */
        L8("F", null),
        /**
         *
         */
        IC0("IC", IsotopesEnum.Pb208),
        /**
         *
         */
        IC1("IC", IsotopesEnum.Pb207),
        /**
         *
         */
        IC2("IC", IsotopesEnum.Pb206),
        /**
         *
         */
        IC3("IC", IsotopesEnum.Pb204),;
        private final String collectorType;
        private final IsotopesEnum isotope;

        private NUPlasmaICCollectors(String collectorType, IsotopesEnum isotope) {
            this.collectorType = collectorType;
            this.isotope = isotope;
        }

        /**
         *
         * @return
         */
        public static String[] getIonCounterCollectorNames() {
            return new String[]{IC0.name(), IC1.name(), IC2.name(), IC3.name()};
        }

        /**
         * @return the collectorType
         */
        public String getCollectorType() {
            return collectorType;
        }

        /**
         * @return the isotope
         */
        public IsotopesEnum getIsotope() {
            return isotope;
        }
    }

    /**
     *
     * @return
     */
    public static GehrelsNUPlasmaSetupUPbIonCounter getInstance() {
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
    public SortedSet<DataModelInterface> rawRatiosFactory( //
            String[][] intensitiesScan, String fractionID, boolean usingFullPropagation, TripoliFraction tripoliFraction) {

        countOfAcquisitions = intensitiesScan.length;

        virtualCollectors = new ArrayList<>(VIRTUAL_COLLECTOR_COUNT);
        for (int i = 0; i < VIRTUAL_COLLECTOR_COUNT; i++) {
            virtualCollectors.add(new VirtualCollectorModel(i + 1));
        }

        // background
        virtualCollectors.get(1 - 1).updateCollector(true);
        virtualCollectors.get(2 - 1).updateCollector(true);
        virtualCollectors.get(13 - 1).updateCollector(true);
        virtualCollectors.get(14 - 1).updateCollector(true);
        virtualCollectors.get(15 - 1).updateCollector(true);
        virtualCollectors.get(16 - 1).updateCollector(true);
        //foreground
        virtualCollectors.get(17 - 1).updateCollector(false);
        virtualCollectors.get(18 - 1).updateCollector(false);
        virtualCollectors.get(29 - 1).updateCollector(false);
        virtualCollectors.get(30 - 1).updateCollector(false);
        virtualCollectors.get(31 - 1).updateCollector(false);
        virtualCollectors.get(32 - 1).updateCollector(false);

        // new set of isotope models for this fraction
        genericIsotopeModels = new TreeSet<>();
        U238 = new RawIntensityDataModel( //
                IsotopeNames.U238, virtualCollectors.get(1 - 1), virtualCollectors.get(17 - 1), COLLECTOR_DATA_FREQUENCY_MILLISECS,//
                isotopeMappingModel.getIsotopeToCollectorMap().get(IsotopesEnum.U238));
        genericIsotopeModels.add(U238);
        isotopeToRawIntensitiesMap.put(IsotopesEnum.U238, U238);

        Th232 = new RawIntensityDataModel( //
                IsotopeNames.Th232, virtualCollectors.get(2 - 1), virtualCollectors.get(18 - 1), COLLECTOR_DATA_FREQUENCY_MILLISECS,//
                isotopeMappingModel.getIsotopeToCollectorMap().get(IsotopesEnum.Th232));
        genericIsotopeModels.add(Th232);
        isotopeToRawIntensitiesMap.put(IsotopesEnum.Th232, Th232);

        Pb208 = new RawIntensityDataModel( //
                IsotopeNames.Pb208, virtualCollectors.get(13 - 1), virtualCollectors.get(29 - 1), COLLECTOR_DATA_FREQUENCY_MILLISECS,//
                isotopeMappingModel.getIsotopeToCollectorMap().get(IsotopesEnum.Pb208));
        genericIsotopeModels.add(Pb208);
        isotopeToRawIntensitiesMap.put(IsotopesEnum.Pb208, Pb208);

        Pb207 = new RawIntensityDataModel( //
                IsotopeNames.Pb207, virtualCollectors.get(14 - 1), virtualCollectors.get(30 - 1), COLLECTOR_DATA_FREQUENCY_MILLISECS,//
                isotopeMappingModel.getIsotopeToCollectorMap().get(IsotopesEnum.Pb207));
        genericIsotopeModels.add(Pb207);
        isotopeToRawIntensitiesMap.put(IsotopesEnum.Pb207, Pb207);

        Pb206 = new RawIntensityDataModel( //
                IsotopeNames.Pb206, virtualCollectors.get(15 - 1), virtualCollectors.get(31 - 1), COLLECTOR_DATA_FREQUENCY_MILLISECS,//
                isotopeMappingModel.getIsotopeToCollectorMap().get(IsotopesEnum.Pb206));
        genericIsotopeModels.add(Pb206);
        isotopeToRawIntensitiesMap.put(IsotopesEnum.Pb206, Pb206);

        Pb204 = new RawIntensityDataModel( //
                IsotopeNames.Pb204, virtualCollectors.get(16 - 1), virtualCollectors.get(32 - 1), COLLECTOR_DATA_FREQUENCY_MILLISECS,//
                isotopeMappingModel.getIsotopeToCollectorMap().get(IsotopesEnum.Pb204));
        genericIsotopeModels.add(Pb204);
        isotopeToRawIntensitiesMap.put(IsotopesEnum.Pb204, Pb204);

        // sept 2012
        isotopeMappingModel.setIsotopeToRawIntensitiesMap(isotopeToRawIntensitiesMap);

        // raw ratios
        // oct 2012 defaults for corrections are set true but later missing ratio at standard will override 
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
// moved to file handler
//        processFractionRawRatios(intensitiesScan, isStandard, fractionID, usingFullPropagation, null);

        return rawRatios;
    }
}
