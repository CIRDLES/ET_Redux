/*
 * ShrimpSetupUPb
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
package org.earthtime.Tripoli.massSpecSetups.singleCollector.shrimp;

import Jama.Matrix;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
public final class ShrimpSetupUPb extends AbstractMassSpecSetup {

    private static ShrimpSetupUPb instance = new ShrimpSetupUPb();

    private ShrimpSetupUPb() {
        super();
        NAME = "SHRIMP Setup";
        massSpecType = MassSpecTypeEnum.SINGLE;
        VIRTUAL_COLLECTOR_COUNT = 18;
        COLLECTOR_DATA_FREQUENCY_MILLISECS = 182000;
        countOfAcquisitions = 0;

        isotopeMappingModel = new IsotopeMappingModel();

        collectorNameToModelMap = new TreeMap<>();

        useConstantBackgroundFitFunction = false;

        this.commonLeadCorrectionHighestLevel = "NONE";

        AbstractCollectorModel singleCollector
                = new IonCounterCollectorModel(//
                        "Single", //
                        new ValueModel("DeadTime", //
                                new BigDecimal(12.0e-9, //
                                        ReduxConstants.mathContext10), //
                                "ABS", //
                                new BigDecimal(1.0e-9, ReduxConstants.mathContext10), //
                                BigDecimal.ZERO), //
                        IonCounterCollectorModel.CollectedDataStyle.COUNTS);

        isotopeMappingModel.getIsotopeToCollectorMap().put(//
                IsotopesEnum.UO270, singleCollector);
        isotopeMappingModel.getIsotopeToIntegrationTimeMap().put( //
                IsotopesEnum.UO270, 0.020);

        isotopeMappingModel.getIsotopeToCollectorMap().put(//
                IsotopesEnum.UO254, singleCollector);
        isotopeMappingModel.getIsotopeToIntegrationTimeMap().put( //
                IsotopesEnum.UO254, 0.020);

        isotopeMappingModel.getIsotopeToCollectorMap().put(//
                IsotopesEnum.ThO248, singleCollector);
        isotopeMappingModel.getIsotopeToIntegrationTimeMap().put( //
                IsotopesEnum.ThO248, 0.020);

        isotopeMappingModel.getIsotopeToCollectorMap().put(//
                IsotopesEnum.U238, singleCollector);
        isotopeMappingModel.getIsotopeToIntegrationTimeMap().put( //
                IsotopesEnum.U238, 0.020);

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
                IsotopesEnum.Zr2O196, singleCollector);
        isotopeMappingModel.getIsotopeToIntegrationTimeMap().put( //
                IsotopesEnum.Zr2O196, 0.020);

        collectorNameToModelMap.put("Single", singleCollector);

    }

    /**
     *
     * @return
     */
    public static ShrimpSetupUPb getInstance() {
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

        UO270 = new RawIntensityDataModel( //
                IsotopeNames.UO270, virtualCollectors.get(9 - 1), virtualCollectors.get(18 - 1), COLLECTOR_DATA_FREQUENCY_MILLISECS,//
                isotopeMappingModel.getIsotopeToCollectorMap().get(IsotopesEnum.UO270));
        genericIsotopeModels.add(UO270);
        isotopeToRawIntensitiesMap.put(IsotopesEnum.UO270, UO270);

        UO254 = new RawIntensityDataModel( //
                IsotopeNames.UO254, virtualCollectors.get(8 - 1), virtualCollectors.get(17 - 1), COLLECTOR_DATA_FREQUENCY_MILLISECS,//
                isotopeMappingModel.getIsotopeToCollectorMap().get(IsotopesEnum.UO254));
        genericIsotopeModels.add(UO254);
        isotopeToRawIntensitiesMap.put(IsotopesEnum.UO254, UO254);

        ThO248 = new RawIntensityDataModel( //
                IsotopeNames.ThO248, virtualCollectors.get(7 - 1), virtualCollectors.get(16 - 1), COLLECTOR_DATA_FREQUENCY_MILLISECS,//
                isotopeMappingModel.getIsotopeToCollectorMap().get(IsotopesEnum.UO254));
        genericIsotopeModels.add(ThO248);
        isotopeToRawIntensitiesMap.put(IsotopesEnum.ThO248, ThO248);

        U238 = new RawIntensityDataModel( //
                IsotopeNames.U238, virtualCollectors.get(6 - 1), virtualCollectors.get(15 - 1), COLLECTOR_DATA_FREQUENCY_MILLISECS,//
                isotopeMappingModel.getIsotopeToCollectorMap().get(IsotopesEnum.U238));
        genericIsotopeModels.add(U238);
        isotopeToRawIntensitiesMap.put(IsotopesEnum.U238, U238);

        Pb208 = new RawIntensityDataModel( //
                IsotopeNames.Pb208, virtualCollectors.get(5 - 1), virtualCollectors.get(14 - 1), COLLECTOR_DATA_FREQUENCY_MILLISECS,//
                isotopeMappingModel.getIsotopeToCollectorMap().get(IsotopesEnum.Pb208));
        genericIsotopeModels.add(Pb208);
        isotopeToRawIntensitiesMap.put(IsotopesEnum.Pb208, Pb208);

        Pb207 = new RawIntensityDataModel( //
                IsotopeNames.Pb207, virtualCollectors.get(4 - 1), virtualCollectors.get(13 - 1), COLLECTOR_DATA_FREQUENCY_MILLISECS,//
                isotopeMappingModel.getIsotopeToCollectorMap().get(IsotopesEnum.Pb207));
        genericIsotopeModels.add(Pb207);
        isotopeToRawIntensitiesMap.put(IsotopesEnum.Pb207, Pb207);

        Pb206 = new RawIntensityDataModel( //
                IsotopeNames.Pb206, virtualCollectors.get(3 - 1), virtualCollectors.get(12 - 1), COLLECTOR_DATA_FREQUENCY_MILLISECS,//
                isotopeMappingModel.getIsotopeToCollectorMap().get(IsotopesEnum.Pb206));
        genericIsotopeModels.add(Pb206);
        isotopeToRawIntensitiesMap.put(IsotopesEnum.Pb206, Pb206);

        Pb204 = new RawIntensityDataModel( //
                IsotopeNames.Pb204, virtualCollectors.get(2 - 1), virtualCollectors.get(11 - 1), COLLECTOR_DATA_FREQUENCY_MILLISECS,//
                isotopeMappingModel.getIsotopeToCollectorMap().get(IsotopesEnum.Pb204));
        genericIsotopeModels.add(Pb204);
        isotopeToRawIntensitiesMap.put(IsotopesEnum.Pb204, Pb204);

        Zr2O196 = new RawIntensityDataModel( //
                IsotopeNames.Zr2O196, virtualCollectors.get(1 - 1), virtualCollectors.get(10 - 1), COLLECTOR_DATA_FREQUENCY_MILLISECS,//
                isotopeMappingModel.getIsotopeToCollectorMap().get(IsotopesEnum.Zr2O196));
        genericIsotopeModels.add(Zr2O196);
        isotopeToRawIntensitiesMap.put(IsotopesEnum.Zr2O196, Zr2O196);

        isotopeMappingModel.setIsotopeToRawIntensitiesMap(isotopeToRawIntensitiesMap);

        virtualCollectorModelMapToFieldIndexes = new HashMap<>();
        virtualCollectorModelMapToFieldIndexes.put(Zr2O196, 0);
        virtualCollectorModelMapToFieldIndexes.put(Pb204, 1);
        virtualCollectorModelMapToFieldIndexes.put(Pb206, 2);
        virtualCollectorModelMapToFieldIndexes.put(Pb207, 3);
        virtualCollectorModelMapToFieldIndexes.put(Pb208, 4);
        virtualCollectorModelMapToFieldIndexes.put(U238, 5);
        virtualCollectorModelMapToFieldIndexes.put(ThO248, 6);
        virtualCollectorModelMapToFieldIndexes.put(UO254, 7);
        virtualCollectorModelMapToFieldIndexes.put(UO270, 8);

        // raw ratios
        rawRatios = new TreeSet<>();
//        DataModelInterface r206_238w = new RawRatioDataModel(RawRatioNames.r206_238w, Pb206, U238, true, false, COLLECTOR_DATA_FREQUENCY_MILLISECS);
//        rawRatios.add(r206_238w);
//        DataModelInterface r206_207w = new RawRatioDataModel(RawRatioNames.r206_207w, Pb206, Pb207, true, false, COLLECTOR_DATA_FREQUENCY_MILLISECS);
//        rawRatios.add(r206_207w);
//
//        // oct 2014 to handle B schemas for common lead correction
//        DataModelInterface r206_204w = new RawRatioDataModel(RawRatioNames.r206_204w, Pb206, Pb204, false, true, COLLECTOR_DATA_FREQUENCY_MILLISECS);
//        rawRatios.add(r206_204w);
//        DataModelInterface r207_204w = new RawRatioDataModel(RawRatioNames.r207_204w, Pb207, Pb204, false, true, COLLECTOR_DATA_FREQUENCY_MILLISECS);
//        rawRatios.add(r207_204w);
//        DataModelInterface r208_204w = new RawRatioDataModel(RawRatioNames.r208_204w, Pb208, Pb204, false, true, COLLECTOR_DATA_FREQUENCY_MILLISECS);
//        rawRatios.add(r208_204w);
//        
//        // special case to handle other Shrimp isotopes
//        rawRatios.add(new RawRatioDataModel(RawRatioNames.r196_196w, Zr2O196, Zr2O196, false, false, COLLECTOR_DATA_FREQUENCY_MILLISECS));
//        rawRatios.add(new RawRatioDataModel(RawRatioNames.r248_248w, ThO248, ThO248, false, false, COLLECTOR_DATA_FREQUENCY_MILLISECS));
//        rawRatios.add(new RawRatioDataModel(RawRatioNames.r254_254w, UO254, UO254, false, false, COLLECTOR_DATA_FREQUENCY_MILLISECS));
//        rawRatios.add(new RawRatioDataModel(RawRatioNames.r270_270w, UO270, UO270, false, false, COLLECTOR_DATA_FREQUENCY_MILLISECS));

        // april 2016 prototype
        rawRatios.add(new RawRatioDataModel(RawRatioNames.r204_206w, Pb204, Pb206, true, false, COLLECTOR_DATA_FREQUENCY_MILLISECS));
        rawRatios.add(new RawRatioDataModel(RawRatioNames.r207_206w, Pb207, Pb206, true, false, COLLECTOR_DATA_FREQUENCY_MILLISECS));
        rawRatios.add(new RawRatioDataModel(RawRatioNames.r208_206w, Pb208, Pb206, true, false, COLLECTOR_DATA_FREQUENCY_MILLISECS));
        rawRatios.add(new RawRatioDataModel(RawRatioNames.r238_196w, U238, Zr2O196, true, false, COLLECTOR_DATA_FREQUENCY_MILLISECS));
        rawRatios.add(new RawRatioDataModel(RawRatioNames.r206_238w, Pb206, U238, true, false, COLLECTOR_DATA_FREQUENCY_MILLISECS));
        rawRatios.add(new RawRatioDataModel(RawRatioNames.r254_238w, UO254, U238, true, false, COLLECTOR_DATA_FREQUENCY_MILLISECS));
        rawRatios.add(new RawRatioDataModel(RawRatioNames.r248_254w, ThO248, UO254, true, false, COLLECTOR_DATA_FREQUENCY_MILLISECS));
        rawRatios.add(new RawRatioDataModel(RawRatioNames.r206_270w, Pb206, UO270, true, false, COLLECTOR_DATA_FREQUENCY_MILLISECS));
        rawRatios.add(new RawRatioDataModel(RawRatioNames.r270_254w, UO270, UO254, true, false, COLLECTOR_DATA_FREQUENCY_MILLISECS));
        rawRatios.add(new RawRatioDataModel(RawRatioNames.r206_254w, Pb206, UO254, true, false, COLLECTOR_DATA_FREQUENCY_MILLISECS));

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

    @Override
    public void processFractionRawRatiosStageII(//
            boolean usingFullPropagation, TripoliFraction tripoliFraction) {

//        calculateRawAndLogRatios();
    }

    public void correctOnPeakIntensities(double[][] correctedIntensities) {
        Matrix peakCorrectedIntensities = new Matrix(correctedIntensities);

        for (Map.Entry<DataModelInterface, Integer> vcmToIndex : virtualCollectorModelMapToFieldIndexes.entrySet()) {
            RawIntensityDataModel rawIntensityModel = ((RawIntensityDataModel) vcmToIndex.getKey());
            int col = vcmToIndex.getValue();

            // extract column for species and convert to double array
            rawIntensityModel.getOnPeakVirtualCollector().setCorrectedIntensities(peakCorrectedIntensities.getMatrix(0, countOfAcquisitions - 1, col, col).getColumnPackedCopy());
        }
        
        calculateLogOnPeakCorrectedIntensities();
    }

    public void initializeVariances(ArrayList<double[]> peakVariances) {
        // convert arraylists to arrays since our data is in columns
        double[][] peakAcquisitionVariancesArray = peakVariances.toArray(new double[countOfAcquisitions][]);

        Matrix peakAcquisitionVariancesMatrix = new Matrix(peakAcquisitionVariancesArray);

        for (Map.Entry<DataModelInterface, Integer> vcmToIndex : virtualCollectorModelMapToFieldIndexes.entrySet()) {
            RawIntensityDataModel rawIntensityModel = ((RawIntensityDataModel) vcmToIndex.getKey());
            int col = vcmToIndex.getValue();

            // extract column for species and convert to double array
            rawIntensityModel.setDiagonalOfMatrixSIntensities(peakAcquisitionVariancesMatrix.getMatrix(0, countOfAcquisitions - 1, col, col).getColumnPackedCopy());
        }
    }

    public void initializeCorrectedVariances(double[][] correctedPeakSigmas) {

        // square them to variances
        double[][] correctedPeakVariances = new double[correctedPeakSigmas.length][correctedPeakSigmas[0].length];
        for (int i = 0; i < correctedPeakSigmas.length; i++) {
            for (int j = 0; j < correctedPeakSigmas[i].length; j++) {
                correctedPeakVariances[i][j] = correctedPeakSigmas[i][j] * correctedPeakSigmas[i][j];
            }
        }
        
        Matrix peakAcquisitionCorrectedVariancesMatrix = new Matrix(correctedPeakVariances);

        for (Map.Entry<DataModelInterface, Integer> vcmToIndex : virtualCollectorModelMapToFieldIndexes.entrySet()) {
            RawIntensityDataModel rawIntensityModel = ((RawIntensityDataModel) vcmToIndex.getKey());
            int col = vcmToIndex.getValue();

            // extract column for species and convert to double array
            rawIntensityModel.setDiagonalOfMatrixSCorrectedIntensities(peakAcquisitionCorrectedVariancesMatrix.getMatrix(0, countOfAcquisitions - 1, col, col).getColumnPackedCopy());
        }
    }

}
