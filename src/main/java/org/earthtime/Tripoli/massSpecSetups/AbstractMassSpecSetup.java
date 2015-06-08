/*
 * AbstractMassSpecSetup.java
 *
 * Created Jul 3, 2011
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
package org.earthtime.Tripoli.massSpecSetups;

import Jama.Matrix;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import org.earthtime.Tripoli.dataModels.DataModelFitFunctionInterface;
import org.earthtime.Tripoli.dataModels.DataModelInterface;
import org.earthtime.Tripoli.dataModels.DownholeFractionationDataModel;
import org.earthtime.Tripoli.dataModels.MaskingSingleton;
import org.earthtime.Tripoli.dataModels.RawIntensityDataModel;
import org.earthtime.Tripoli.dataModels.RawRatioDataModel;
import org.earthtime.Tripoli.dataModels.VirtualCollectorModel;
import org.earthtime.Tripoli.dataModels.aquisitionTypeDataModels.IsotopeMappingModel;
import org.earthtime.Tripoli.dataModels.collectorModels.AbstractCollectorModel;
import org.earthtime.Tripoli.dataModels.collectorModels.FaradayCollectorModel;
import org.earthtime.Tripoli.dataModels.collectorModels.IonCounterCollectorModel;
import org.earthtime.Tripoli.fractions.TripoliFraction;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.dataDictionaries.FitFunctionTypeEnum;
import org.earthtime.dataDictionaries.IsotopeNames;
import org.earthtime.dataDictionaries.MassSpecTypeEnum;
import org.earthtime.dataDictionaries.RawRatioNames;
import org.earthtime.isotopes.IsotopesEnum;
import org.earthtime.statistics.NonParametricStats;

/**
 *
 * @author James F. Bowring
 */
public abstract class AbstractMassSpecSetup implements //
        Comparable<AbstractMassSpecSetup>,
        Serializable {

    // Class variables
    private static final long serialVersionUID = -7767996777129217194L;

    /**
     *
     */
    protected String NAME;

    /**
     *
     */
    protected MassSpecTypeEnum massSpecType;
    /**
     *
     */
    protected int VIRTUAL_COLLECTOR_COUNT;
    /**
     *
     */
    protected long COLLECTOR_DATA_FREQUENCY_MILLISECS;
    /**
     *
     */
    protected SortedSet<DataModelInterface> rawRatios;
    /**
     *
     */
    protected int countOfAcquisitions;

    /**
     *
     */
    protected SortedSet<DataModelInterface> genericIsotopeModels;

    /**
     *
     */
    protected Map<IsotopesEnum, DataModelInterface> isotopeToRawIntensitiesMap;

    /**
     *
     */
    protected ArrayList<VirtualCollectorModel> virtualCollectors;

    /**
     *
     */
    protected IsotopeMappingModel isotopeMappingModel;

    /**
     *
     */
    protected Map<String, AbstractCollectorModel> collectorNameToModelMap;
    // isobaric correction

    /**
     *
     */
    protected ValueModel r202Hg_204Hg;

    /**
     *
     */
    protected DataModelInterface Hg202;

    /**
     *
     */
    protected DataModelInterface Pb204;

    /**
     *
     */
    protected DataModelInterface Pb207;
    protected DataModelInterface Pb208;
    protected DataModelInterface Pb206;
    protected DataModelInterface U238;
    protected DataModelInterface Th232;
    protected DataModelInterface U235;
    // special handling for constant background measure

    /**
     *
     */
    protected boolean useConstantBackgroundFitFunction;
    private transient boolean isFirstReport = true;
    // jan 2014

    /**
     *
     */
    protected String commonLeadCorrectionHighestLevel;

    /**
     *
     */
    public AbstractMassSpecSetup() {
        this.NAME = "None";
        this.massSpecType = null;
        this.VIRTUAL_COLLECTOR_COUNT = 0;
        this.COLLECTOR_DATA_FREQUENCY_MILLISECS = 0;
        this.rawRatios = new TreeSet<>();
        this.countOfAcquisitions = 0;
        this.rawRatios = null;

        this.genericIsotopeModels = new TreeSet<>();
        this.isotopeToRawIntensitiesMap = new EnumMap<>(IsotopesEnum.class);
        this.virtualCollectors = new ArrayList<>();
        this.isotopeMappingModel = null;
        this.collectorNameToModelMap = null;

        // TODO: make a user-set parameter
        this.r202Hg_204Hg = new ValueModel( //
                "r202Hg_204Hg", new BigDecimal(4.346), "ABS", BigDecimal.ZERO, BigDecimal.ZERO);
        this.Hg202 = null;
        this.Pb204 = null;
        this.Pb207 = null;

        this.useConstantBackgroundFitFunction = false;
        this.commonLeadCorrectionHighestLevel = "NONE";
    }

    /**
     *
     * @param abstractMassSpecSetup
     * @return
     */
    @Override
    public int compareTo(AbstractMassSpecSetup abstractMassSpecSetup) {
        String abstractRawDataFileHandlerName =//
                abstractMassSpecSetup.NAME.trim();
        return (this.NAME.trim().compareToIgnoreCase(abstractRawDataFileHandlerName));
    }

    /**
     *
     * @param abstractMassSpecSetup
     * @return
     */
    @Override
    public boolean equals(Object abstractMassSpecSetup) {
        //check for self-comparison
        if (this == abstractMassSpecSetup) {
            return true;
        }
        if (!(abstractMassSpecSetup instanceof AbstractMassSpecSetup)) {
            return false;
        }

        AbstractMassSpecSetup myAbstractMassSpecSetup = (AbstractMassSpecSetup) abstractMassSpecSetup;
        return (this.NAME.trim().compareToIgnoreCase(myAbstractMassSpecSetup.NAME.trim()) == 0);
    }

    /**
     *
     * @return
     */
    @Override
    public int hashCode() {
        return super.hashCode();
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
    public abstract SortedSet<DataModelInterface> rawRatiosFactory(//
            String[][] intensitiesScan, boolean isStandard, String fractionID, boolean usingFullPropagation, TripoliFraction tripoliFraction);

    /**
     *
     * @param usingFullPropagation
     * @param fractionID the value of fractionID
     * @param ripoliFraction the value of ripoliFraction
     */
    public void reProcessFractionRawRatios(boolean usingFullPropagation, String fractionID, TripoliFraction tripoliFraction) {
        performBackgroundFitting(usingFullPropagation);

        correctOnPeakIntensitiesWithBackFitFunction();
        if (Pb204 != null) {
            validateOnPeakBaselineCorrectedIsotope(Pb204, fractionID);
        }

        // modification march 2014 to handle agilent see Noah email 3 mar 2014
        // We should disregard this test if the instrument is a 'single collector ion counter', like the Agilent 7700 and Jeff's Thermo Element2.
        if (Pb207 != null) {
            AbstractCollectorModel collector = collectorNameToModelMap.get(Pb207.getCollectorModel().getCollectorName());//              getDataModelName());
            if (!(massSpecType.compareTo(MassSpecTypeEnum.SINGLE) == 0) || !(collector instanceof IonCounterCollectorModel)) {
                validateOnPeakBaselineCorrectedIsotope(Pb207, tripoliFraction.getFractionID());
            }
        }

        adjustNonPositiveCorrectedIntensities();
        calculateLogOnPeakCorrectedIntensities();

        calculateRawAndLogRatios();

        tripoliFraction.reProcessToRejectNegativeRatios();

        propagateUnctInBaselineCorrOnPeakIntensities();

        propagateUnctInRatios(usingFullPropagation);

        performInterceptFittingToRatios();

        cleanupUnctCalcs();
    }

    public void processFractionRawRatios(//
            String[][] intensitiesScan, boolean isStandard, String fractionID, boolean usingFullPropagation, TripoliFraction tripoliFraction) {

        initializeVirtualCollectorsWithData(intensitiesScan);

        processFractionRawRatiosStageII(isStandard, usingFullPropagation, tripoliFraction);
    }

    /**
     * Updated version to handle case of background counts diff from peak counts
     *
     * @param backgroundAcquisitions
     * @param peakAcquisitions
     * @param isStandard
     * @param usingFullPropagation
     * @param virtualCollectorModelMapToFieldIndexes
     * @param tripoliFraction
     */
    public void processFractionRawRatiosII(//
            ArrayList<double[]> backgroundAcquisitions, //
            ArrayList<double[]> peakAcquisitions, //
            boolean isStandard, //
            boolean usingFullPropagation, //
            TripoliFraction tripoliFraction, //
            Map<DataModelInterface, Integer> virtualCollectorModelMapToFieldIndexes) {

        initializeVirtualCollectorsWithData(backgroundAcquisitions, peakAcquisitions, virtualCollectorModelMapToFieldIndexes);

        processFractionRawRatiosStageII(isStandard, usingFullPropagation, tripoliFraction);
    }

    public void processFractionRawRatiosTRA(//
            ArrayList<double[]> backgroundAcquisitions, ArrayList<double[]> peakAcquisitions, boolean isStandard, String fractionID, boolean usingFullPropagation, TripoliFraction tripoliFraction) {

        initializeVirtualCollectorsWithDataTRA(backgroundAcquisitions, peakAcquisitions);

        processFractionRawRatiosStageII(isStandard, usingFullPropagation, tripoliFraction);
    }

    /**
     *
     * @param isStandard the value of isStandard
     * @param fractionID the value of fractionID
     * @param usingFullPropagation the value of usingFullPropagation
     * @param tripoliFraction the value of tripoliFraction
     */
    public void processFractionRawRatiosStageII(//
            boolean isStandard, boolean usingFullPropagation, TripoliFraction tripoliFraction) {
        // make fresh set of rawratios with map of collector instances

        String fractionID = tripoliFraction.getFractionID();

        boolean isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean().
                getInputArguments().toString().indexOf("-Xdebug") > 0;

        boolean writeReport = isStandard && isFirstReport & (!isDebug);
        PrintWriter outputWriter = null;

        if (writeReport) {
            File dataValuesFile = new File("STANDARDS_DATA_CHECK_FILES" + File.separator + fractionID + ".txt");
            try {
                outputWriter = new PrintWriter(new FileWriter(dataValuesFile));
                outputWriter.println("******   STANDARD " + fractionID + "  DATA CHECK ********************");
            } catch (IOException iOException) {
            }
            isFirstReport = false;

        }

        if (writeReport) {
            if (outputWriter != null) {
                outputWriter.println("\n\n1: Raw intensities, baseline and on-peak (as volts or cps) ********************");
                for (DataModelInterface dm : genericIsotopeModels) {
                    outputWriter.println(((RawIntensityDataModel) dm).outputIntensities());
                }
            }
        }

        convertRawIntensitiesToCountsPerSecond();

        if (writeReport) {
            outputWriter.println("\n\n2: Intensities converted to cps, baseline and on-peak ********************");
            for (DataModelInterface dm : genericIsotopeModels) {
                outputWriter.println(((RawIntensityDataModel) dm).outputIntensities());
            }
        }

       isotopeMappingModel.calculateAllIntensityMatrixSDiagonals();

        if ((Hg202 != null) && (Pb204 != null)) {
            calculateCorrectionForIsobaricInterference(Hg202, Pb204);
        }

        // begin fast vs slow **************************************************
        performBackgroundFitting(usingFullPropagation);//usingFullPropagation );

        correctOnPeakIntensitiesWithBackFitFunction();

        if (writeReport) {
            outputWriter.println("\n\n3: Si ********************");
            for (DataModelInterface dm : genericIsotopeModels) {
                if (!dm.equals(Hg202)) {
                    outputWriter.println(dm.getDataModelName());
                    Matrix Si = ((RawIntensityDataModel) dm).getMatrixSiCovarianceIntensities();
                    if (Si != null) {
                        Si.print(outputWriter, new DecimalFormat("0.000000E00"), 10);
                    } else {
                        //fast uncertainty prop
                        Si = ((RawIntensityDataModel) dm).getVectorSviVarianceIntensities();
                        // march 2014
                        // print diagonal
                        for (int i = 0; i < Si.getColumnDimension(); i++) {
                            outputWriter.print(Si.get(i, i) + ", ");
                        }
                        outputWriter.println();
                        outputWriter.println();
                    }
                }
            }
        }

        if (writeReport) {
            outputWriter.println("\n\n4: Fit parameters for baseline (a [b, c], fitParameterCovarianceMatrix, X2) ********************");
            for (DataModelInterface dm : genericIsotopeModels) {
                if (!dm.equals(Hg202)) {
                    outputWriter.println(((RawIntensityDataModel) dm).outputBaseLineFitFunctionParameters());
                }
            }
        }

        if (writeReport) {
            outputWriter.println("\n\n5: peakIntensityBLcorr (baseline-corrected on-peak intensities) ********************");
            for (DataModelInterface dm : genericIsotopeModels) {
                if (!dm.equals(Hg202)) {
                    outputWriter.println(((RawIntensityDataModel) dm).outputCorrectedIntensities());
                }
            }
        }

        if (Pb204 != null) {
            validateOnPeakBaselineCorrectedIsotope(Pb204, fractionID);
        }

        // modification march 2014 to handle agilent see Noah email 3 mar 2014
        // We should disregard this test if the instrument is a 'single collector ion counter', like the Agilent 7700 and Jeff's Thermo Element2.
        if (Pb207 != null) {
            AbstractCollectorModel collector = collectorNameToModelMap.get(Pb207.getCollectorModel().getCollectorName());//              getDataModelName());
            if (!(massSpecType.compareTo(MassSpecTypeEnum.SINGLE) == 0) || !(collector instanceof IonCounterCollectorModel)) {
                validateOnPeakBaselineCorrectedIsotope(Pb207, fractionID);
            }
        }

        adjustNonPositiveCorrectedIntensities();

        calculateLogOnPeakCorrectedIntensities();

        calculateRawAndLogRatios();

        tripoliFraction.reProcessToRejectNegativeRatios();

        propagateUnctInBaselineCorrOnPeakIntensities();

        if (writeReport) {
            outputWriter.println("\n\n6: J11, J21, J22, JOnPeak, Sopbc ********************");
            for (DataModelInterface dm : genericIsotopeModels) {
                if (!dm.equals(Hg202)) {
                    try {
                        outputWriter.println(dm.getDataModelName());

                        outputWriter.println("J11:");
                        ((RawIntensityDataModel) dm).getJ11().print(outputWriter, new DecimalFormat("0.000000E00"), 10);

                        outputWriter.println("J21:");
                        ((RawIntensityDataModel) dm).getMatrixJ21().print(outputWriter, new DecimalFormat("0.000000E00"), 10);

                        outputWriter.println("J22:");
                        ((RawIntensityDataModel) dm).getJ22().print(outputWriter, new DecimalFormat("0.000000E00"), 10);

                        outputWriter.println("JOnPeak:");
                        ((RawIntensityDataModel) dm).getJOnPeak().print(outputWriter, new DecimalFormat("0.000000E00"), 10);

                        outputWriter.println("Sopbc:");
                        ((RawIntensityDataModel) dm).getSopbc().print(outputWriter, new DecimalFormat("0.000000E00"), 10);
                    } catch (Exception e) {
                    }
                }
            }
        }

        if (writeReport) {
            outputWriter.println("\n\n7: below the detection limit (boolean) for 204Pb and 207Pb ********************");
            outputWriter.println("Currently Disabled ...");
        }

        if (writeReport) {
            outputWriter.println("\n\n8: log-intensities (on-peak baseline-corrected) ********************");
            for (DataModelInterface dm : genericIsotopeModels) {
                if (!dm.equals(Hg202)) {
                    try {
                        outputWriter.println(((RawIntensityDataModel) dm).outputCorrectedIntensitiesAsLogs());
                    } catch (Exception e) {
                    }
                }
            }
        }

        if (writeReport) {
            outputWriter.println("\n\n9: Jlogr, Jmat, Sopbclr   ********************");
            for (DataModelInterface dm : genericIsotopeModels) {
                if (!dm.equals(Hg202)) {
                    try {
                        outputWriter.println(dm.getDataModelName());

                        outputWriter.println("Jlogr:");
                        ((RawIntensityDataModel) dm).getMatrixJlogr().print(outputWriter, new DecimalFormat("0.000000E00"), 10);

                        outputWriter.println("Jmat:");
                        ((RawIntensityDataModel) dm).getJmat().print(outputWriter, new DecimalFormat("0.000000E00"), 10);

                        outputWriter.println("Sopbclr:");
                        ((RawIntensityDataModel) dm).getSopbclr().print(outputWriter, new DecimalFormat("0.000000E00"), 10);
                    } catch (Exception e) {
                    }
                }
            }
        }

        if (writeReport) {
            outputWriter.println("\n\n10: lr206Pb_207Pb, lr206Pb_238U, lr208Pb_232Th   ********************");
            for (DataModelInterface rr : rawRatios) {
                if (rr.getRawRatioModelName().compareTo(RawRatioNames.r202_202w) != 0) {
                    try {
                        outputWriter.println(((RawRatioDataModel) rr).outputLogRatios());
                    } catch (Exception e) {
                    }
                }
            }
        }

        propagateUnctInRatios(usingFullPropagation);//usingFullPropagation);// needed for first pass

        performInterceptFittingToRatios();

        if (writeReport) {
            outputWriter.println("\n\n11: Sxyod, Slr_X_Y for the three log-ratios   ********************");
            for (DataModelInterface rr : rawRatios) {
                if (((RawRatioDataModel) rr).isUsedForFractionationCorrections()) {
                    try {
                        outputWriter.println(rr.getDataModelName());
                    } catch (Exception e) {
                    }

                    outputWriter.println("Sxyod:");

                    try {
                        ((RawRatioDataModel) rr).getMatrixSxyod().print(outputWriter, new DecimalFormat("0.000000E00"), 10);
                    } catch (Exception e) {
                        outputWriter.println("\tNot Calculated because not two ioncounters present");
                    }

                    outputWriter.println("Slr_X_Y:");
                    try {
                        ((RawRatioDataModel) rr).getSlogRatioX_Y().print(outputWriter, new DecimalFormat("0.000000E00"), 10);
                    } catch (Exception e) {
                    }
                }
            }
        }

        if (writeReport) {
            outputWriter.println("\n\n12: Fit parameters for three log-ratios (a [b, c], fitParameterCovarianceMatrix, X2) ********************");
            for (DataModelInterface rr : rawRatios) {
                if (((RawRatioDataModel) rr).isUsedForFractionationCorrections()) {
                    try {
                        outputWriter.println(((RawRatioDataModel) rr).outputLogRatioFitFunctionParameters());
                    } catch (Exception e) {
                    }
                }
            }
        }

        if (writeReport) {
            outputWriter.println("\n\n13: Y-intercept values and uncertainties for three log-ratio fits ********************");
            for (DataModelInterface rr : rawRatios) {
                if (((RawRatioDataModel) rr).isUsedForFractionationCorrections()) {
                    try {
                        outputWriter.println(((RawRatioDataModel) rr).outputLogRatioFitFunctionYInterceptData());
                    } catch (Exception e) {
                    }
                }
            }
        }

        cleanupUnctCalcs();

        if (writeReport) {
            outputWriter.println();

            outputWriter.flush();
            outputWriter.close();
        }

    }

    private void cleanupUnctCalcs() {
        for (DataModelInterface dm : genericIsotopeModels) {
            ((DataModelFitFunctionInterface) dm).cleanupUnctCalcs();
        }

        System.gc();
    }

    private void convertRawIntensitiesToCountsPerSecond() {
        genericIsotopeModels.stream().forEach((dm) -> {
            ((RawIntensityDataModel) dm).convertRawIntensitiesToCountsPerSecond();
        });
    }

    private void performInterceptFittingToRatios() {

        // generate fit function so can be done with big matrices
        for (DataModelInterface rr : rawRatios) {
            rr.generateSetOfFitFunctions(false, true);
        }
    }

    /**
     *
     * @param usingFullPropagation the value of usingFullPropagation
     */
    private void propagateUnctInRatios(boolean usingFullPropagation) {

        for (DataModelInterface rr : rawRatios) {
            rr.setUSING_FULL_PROPAGATION(usingFullPropagation);
            ((RawRatioDataModel) rr).propagateUnctInRatios();
        }

    }

    private void propagateUnctInBaselineCorrOnPeakIntensities() {
        genericIsotopeModels.stream().forEach((dm) -> {
            ((RawIntensityDataModel) dm).propagateUnctInBaselineCorrOnPeakIntensities();
        });
    }

    private void calculateLogOnPeakCorrectedIntensities() {
        virtualCollectors.stream().forEach((c) -> {
            c.calculateLogOnPeakCorrectedIntensities();
        });
    }

    private void adjustNonPositiveCorrectedIntensities() {
        for (VirtualCollectorModel c : virtualCollectors) {
            c.adjustNonPositiveCorrectedIntensities();
        }
    }

    /**
     *
     * @param isotope the value of isotope
     * @param fractionID the value of fractionID
     */
    private void validateOnPeakBaselineCorrectedIsotope(DataModelInterface isotope, String fractionID) {
        // per Noah's LA-ICP-MS Uncert Prop paper, we want to determine in particular
        // if 204Pb and 207Pb should be kept or rejected after corrections

        boolean[] dataActiveMap = //
                ((RawIntensityDataModel) isotope).getOnPeakVirtualCollector().getDataActiveMap();
        double[] isotopeOPBC = //
                ((RawIntensityDataModel) isotope).getOnPeakVirtualCollector().getCorrectedIntensities();

        NonParametricStats nonParametricStats = NonParametricStats.getInstance();

        boolean belowDetection = nonParametricStats.determineIfBelowDetectionLimitUsingTwoSigma(dataActiveMap, isotopeOPBC);
        boolean tenPercentOrMoreAreNegative = nonParametricStats.determineIfTenPercentOrMoreAreNegative(dataActiveMap, isotopeOPBC);

        // new logic Nov 2014
        // */204 ratios will be post-processed
        if (!belowDetection && (((RawIntensityDataModel) isotope).getRawIsotopeModelName().compareTo(IsotopeNames.Pb204) == 0) && tenPercentOrMoreAreNegative) {
            // prepare for ratios of means
            System.out.println(isotope.getDataModelName() + "above detection with MORE than 10% neg for " + fractionID);
            nonParametricStats.calculateStats(dataActiveMap, isotopeOPBC);
            ((RawIntensityDataModel) Pb204).setForceMeanForCommonLeadRatios(true);
            ((RawIntensityDataModel) Pb204).setForcedMeanForCommonLeadRatios(nonParametricStats.getSampleMean());

            isotopeOPBC = ((RawIntensityDataModel) Pb206).getOnPeakVirtualCollector().getCorrectedIntensities();
            nonParametricStats.calculateStats(dataActiveMap, isotopeOPBC);
            //((RawIntensityDataModel) Pb206).setForceMeanForCommonLeadRatios(true);
            ((RawIntensityDataModel) Pb206).setForcedMeanForCommonLeadRatios(nonParametricStats.getSampleMean());

            isotopeOPBC = ((RawIntensityDataModel) Pb207).getOnPeakVirtualCollector().getCorrectedIntensities();
            nonParametricStats.calculateStats(dataActiveMap, isotopeOPBC);
            //((RawIntensityDataModel) Pb207).setForceMeanForCommonLeadRatios(true);
            ((RawIntensityDataModel) Pb207).setForcedMeanForCommonLeadRatios(nonParametricStats.getSampleMean());

            isotopeOPBC = ((RawIntensityDataModel) Pb208).getOnPeakVirtualCollector().getCorrectedIntensities();
            nonParametricStats.calculateStats(dataActiveMap, isotopeOPBC);
            //((RawIntensityDataModel) Pb208).setForceMeanForCommonLeadRatios(true);
            ((RawIntensityDataModel) Pb208).setForcedMeanForCommonLeadRatios(nonParametricStats.getSampleMean());
        }
        // turn off isotope
        ((RawIntensityDataModel) isotope).setBelowDetection(belowDetection);

        System.out.println("Isotope " + isotope.getDataModelName() + "  " + (belowDetection ? "IS " : "IS NOT ") + " below detection");

    }

    /**
     *
     */
    private void calculateRawAndLogRatios() {
        // calculate ratios ****************************************************
        for (DataModelInterface rr : rawRatios) {
            ((RawRatioDataModel) rr).calculateRawAndLogRatios();
            ((RawRatioDataModel) rr).setDataActiveMap(AbstractMassSpecSetup.defaultDataActiveMap(countOfAcquisitions));
            // april 2014
            rr.applyMaskingArray();
        }
    }

    private void initializeVirtualCollectorsWithData(String[][] intensitiesScan) {
        VirtualCollectorModel[] myCollectors = new VirtualCollectorModel[VIRTUAL_COLLECTOR_COUNT];

        for (VirtualCollectorModel collector : virtualCollectors) {
            myCollectors[collector.getCollectorNumber() - 1] = collector;
        }

        // walk virtualCollectors for active virtualCollectors and create intensitySet for each
        for (int c = 0; c < myCollectors.length; c++) {
            if (myCollectors[c] != null) {

                // extract collector's column of data from scanData
                double[] intensities = new double[countOfAcquisitions];
                double[] aquireTimes = new double[countOfAcquisitions];

                for (int row = 0; row < countOfAcquisitions; row++) {

                    intensities[row] = Double.parseDouble(//
                            intensitiesScan[row][myCollectors[c].getCollectorNumber() - 1].trim());

                    double aquireTime;
                    if (myCollectors[c].isBackground()) {
                        aquireTime = (row * COLLECTOR_DATA_FREQUENCY_MILLISECS);
                    } else {
                        aquireTime = ((row + countOfAcquisitions) * COLLECTOR_DATA_FREQUENCY_MILLISECS);
                    }
                    aquireTimes[row] = aquireTime;
                }

                // isotopeMappingModel
                myCollectors[c].setIntensities(intensities);
                myCollectors[c].setAquireTimes(aquireTimes);
                myCollectors[c].setDataActiveMap(AbstractMassSpecSetup.defaultDataActiveMap(countOfAcquisitions));

                // initialize corrections to zeroes
                myCollectors[c].setIntensityCorrections(new double[countOfAcquisitions]);
                myCollectors[c].setFitBackgroundIntensities(new double[countOfAcquisitions]);
            }
        }

        // may 2014
        for (DataModelInterface dm : genericIsotopeModels) {
            ((RawIntensityDataModel) dm).correctIntensitiesForResistor();
        }

    }

    /**
     * Designed to handle background and peak acquisitions in the general case
     *
     * @param acquisitions
     */
    private void initializeVirtualCollectorsWithData(//
            ArrayList<double[]> backgroundAcquisitions,//
            ArrayList<double[]> peakAcquisitions,//
            Map<DataModelInterface, Integer> virtualCollectorModelMapToFieldIndexes) {

        int countOfBackgroundAcquisitions = backgroundAcquisitions.size();
        int countOfPeakAcquisitions = peakAcquisitions.size();

        // calculate acquire times
        double[] backgroundAquireTimes = new double[countOfBackgroundAcquisitions];
        double[] peakAquireTimes = new double[countOfPeakAcquisitions];

        for (int i = 0; i < countOfBackgroundAcquisitions; i++) {
            backgroundAquireTimes[i] = i * COLLECTOR_DATA_FREQUENCY_MILLISECS;
        }
        for (int i = 0; i < countOfPeakAcquisitions; i++) {
            peakAquireTimes[i] = (i + countOfBackgroundAcquisitions) * COLLECTOR_DATA_FREQUENCY_MILLISECS;
        }

        // convert arraylists to transposed arrays
        double[][] backgroundAcquisitionsArray = backgroundAcquisitions.toArray(new double[countOfBackgroundAcquisitions][]);
        double[][] peakAcquisitionsArray = peakAcquisitions.toArray(new double[countOfPeakAcquisitions][]);

        Matrix backgroundAcquisitionsMatrix = new Matrix(backgroundAcquisitionsArray);
        Matrix peakAcquisitionsMatrix = new Matrix(peakAcquisitionsArray);

        for (Map.Entry<DataModelInterface, Integer> vcmToIndex : virtualCollectorModelMapToFieldIndexes.entrySet()) {
            VirtualCollectorModel backgroundVCM = prepareVirtualCollector(((RawIntensityDataModel) vcmToIndex.getKey()).getBackgroundVirtualCollector(), countOfBackgroundAcquisitions);
            VirtualCollectorModel peakVCM = prepareVirtualCollector(((RawIntensityDataModel) vcmToIndex.getKey()).getOnPeakVirtualCollector(), countOfPeakAcquisitions);

            int col = vcmToIndex.getValue();

            backgroundVCM.setIntensities(backgroundAcquisitionsMatrix.getMatrix(0, countOfBackgroundAcquisitions - 1, col, col).getColumnPackedCopy());
            peakVCM.setIntensities(peakAcquisitionsMatrix.getMatrix(0, countOfPeakAcquisitions - 1, col, col).getColumnPackedCopy());

            backgroundVCM.setAquireTimes(backgroundAquireTimes);
            peakVCM.setAquireTimes(peakAquireTimes);
        }

        for (DataModelInterface dm : genericIsotopeModels) {
            ((RawIntensityDataModel) dm).correctIntensitiesForResistor();
        }
    }

    /**
     * this will be deprecated as soon as nu faraday file handler is updated
     *
     * @param backgroundAcquisitions
     * @param peakAcquisitions
     */
    private void initializeVirtualCollectorsWithDataTRA(ArrayList<double[]> backgroundAcquisitions, ArrayList<double[]> peakAcquisitions) {
        // dec 2014 TRA version where scan data contains background then peak in columns
        // background collectors
        int countOfBackgroundAcquisitions = backgroundAcquisitions.size();
        VirtualCollectorModel c238_Background = prepareVirtualCollector(((RawIntensityDataModel) U238).getBackgroundVirtualCollector(), countOfBackgroundAcquisitions);
        VirtualCollectorModel c232_Background = prepareVirtualCollector(((RawIntensityDataModel) Th232).getBackgroundVirtualCollector(), countOfBackgroundAcquisitions);
        VirtualCollectorModel c208_Background = prepareVirtualCollector(((RawIntensityDataModel) Pb208).getBackgroundVirtualCollector(), countOfBackgroundAcquisitions);
        VirtualCollectorModel c207_Background = prepareVirtualCollector(((RawIntensityDataModel) Pb207).getBackgroundVirtualCollector(), countOfBackgroundAcquisitions);
        VirtualCollectorModel c206_Background = prepareVirtualCollector(((RawIntensityDataModel) Pb206).getBackgroundVirtualCollector(), countOfBackgroundAcquisitions);
        VirtualCollectorModel c204_Background = prepareVirtualCollector(((RawIntensityDataModel) Pb204).getBackgroundVirtualCollector(), countOfBackgroundAcquisitions);
        VirtualCollectorModel c202_Background = prepareVirtualCollector(((RawIntensityDataModel) Hg202).getBackgroundVirtualCollector(), countOfBackgroundAcquisitions);

        for (int i = 0; i < countOfBackgroundAcquisitions; i++) {
            double[] acquisition = backgroundAcquisitions.get(i);
            c238_Background.getIntensities()[i] = acquisition[0];
            c232_Background.getIntensities()[i] = acquisition[1];
            c208_Background.getIntensities()[i] = acquisition[2];
            c207_Background.getIntensities()[i] = acquisition[3];
            c206_Background.getIntensities()[i] = acquisition[4];
            c204_Background.getIntensities()[i] = acquisition[5];
            c202_Background.getIntensities()[i] = acquisition[6];

            c238_Background.getAquireTimes()[i] = i * COLLECTOR_DATA_FREQUENCY_MILLISECS;
            c232_Background.getAquireTimes()[i] = i * COLLECTOR_DATA_FREQUENCY_MILLISECS;
            c208_Background.getAquireTimes()[i] = i * COLLECTOR_DATA_FREQUENCY_MILLISECS;
            c207_Background.getAquireTimes()[i] = i * COLLECTOR_DATA_FREQUENCY_MILLISECS;
            c206_Background.getAquireTimes()[i] = i * COLLECTOR_DATA_FREQUENCY_MILLISECS;
            c204_Background.getAquireTimes()[i] = i * COLLECTOR_DATA_FREQUENCY_MILLISECS;
            c202_Background.getAquireTimes()[i] = i * COLLECTOR_DATA_FREQUENCY_MILLISECS;
        }

        // peak collectors
        int countOfPeakAcquisitions = peakAcquisitions.size();
        VirtualCollectorModel c238_Peak = prepareVirtualCollector(((RawIntensityDataModel) U238).getOnPeakVirtualCollector(), countOfPeakAcquisitions);
        VirtualCollectorModel c232_Peak = prepareVirtualCollector(((RawIntensityDataModel) Th232).getOnPeakVirtualCollector(), countOfPeakAcquisitions);
        VirtualCollectorModel c208_Peak = prepareVirtualCollector(((RawIntensityDataModel) Pb208).getOnPeakVirtualCollector(), countOfPeakAcquisitions);
        VirtualCollectorModel c207_Peak = prepareVirtualCollector(((RawIntensityDataModel) Pb207).getOnPeakVirtualCollector(), countOfPeakAcquisitions);
        VirtualCollectorModel c206_Peak = prepareVirtualCollector(((RawIntensityDataModel) Pb206).getOnPeakVirtualCollector(), countOfPeakAcquisitions);
        VirtualCollectorModel c204_Peak = prepareVirtualCollector(((RawIntensityDataModel) Pb204).getOnPeakVirtualCollector(), countOfPeakAcquisitions);
        VirtualCollectorModel c202_Peak = prepareVirtualCollector(((RawIntensityDataModel) Hg202).getOnPeakVirtualCollector(), countOfPeakAcquisitions);

        for (int i = 0; i < countOfPeakAcquisitions; i++) {
            double[] acquisition = peakAcquisitions.get(i);
            c238_Peak.getIntensities()[i] = acquisition[0];
            c232_Peak.getIntensities()[i] = acquisition[1];
            c208_Peak.getIntensities()[i] = acquisition[2];
            c207_Peak.getIntensities()[i] = acquisition[3];
            c206_Peak.getIntensities()[i] = acquisition[4];
            c204_Peak.getIntensities()[i] = acquisition[5];
            c202_Peak.getIntensities()[i] = acquisition[6];

            c238_Peak.getAquireTimes()[i] = (i + countOfBackgroundAcquisitions) * COLLECTOR_DATA_FREQUENCY_MILLISECS;
            c232_Peak.getAquireTimes()[i] = (i + countOfBackgroundAcquisitions) * COLLECTOR_DATA_FREQUENCY_MILLISECS;
            c208_Peak.getAquireTimes()[i] = (i + countOfBackgroundAcquisitions) * COLLECTOR_DATA_FREQUENCY_MILLISECS;
            c207_Peak.getAquireTimes()[i] = (i + countOfBackgroundAcquisitions) * COLLECTOR_DATA_FREQUENCY_MILLISECS;
            c206_Peak.getAquireTimes()[i] = (i + countOfBackgroundAcquisitions) * COLLECTOR_DATA_FREQUENCY_MILLISECS;
            c204_Peak.getAquireTimes()[i] = (i + countOfBackgroundAcquisitions) * COLLECTOR_DATA_FREQUENCY_MILLISECS;
            c202_Peak.getAquireTimes()[i] = (i + countOfBackgroundAcquisitions) * COLLECTOR_DATA_FREQUENCY_MILLISECS;
        }

        // may 2014
        for (DataModelInterface dm : genericIsotopeModels) {
            ((RawIntensityDataModel) dm).correctIntensitiesForResistor();
        }

    }

    private VirtualCollectorModel prepareVirtualCollector(VirtualCollectorModel vcm, int countOfAcquisitions) {

        vcm.setIntensities(new double[countOfAcquisitions]);
        vcm.setAquireTimes(new double[countOfAcquisitions]);
        vcm.setDataActiveMap(AbstractMassSpecSetup.defaultDataActiveMap(countOfAcquisitions));

        // initialize corrections to zeroes
        vcm.setIntensityCorrections(new double[countOfAcquisitions]);
        vcm.setFitBackgroundIntensities(new double[countOfAcquisitions]);

        return vcm;
    }

    /**
     *
     * @param usingFullPropagation the value of usingFullPropagation
     */
    private void performBackgroundFitting(boolean usingFullPropagation) {

//        RawIntensityDataModel.setUSING_FULL_PROPAGATION(usingFullPropagation);
        // generates the fit function for the background
        Iterator<DataModelInterface> genericIsotopeModelsIterator = genericIsotopeModels.iterator();
        Matrix Hg202Si = null;
        while (genericIsotopeModelsIterator.hasNext()) {
            DataModelInterface im = genericIsotopeModelsIterator.next();
            im.setUSING_FULL_PROPAGATION(usingFullPropagation);

            if (useConstantBackgroundFitFunction) {
                ((DataModelFitFunctionInterface) im).setSelectedFitFunctionType(FitFunctionTypeEnum.CONSTANT);
            }

            if (im.getDataModelName().equalsIgnoreCase(IsotopesEnum.Hg202.getName())) {
                Hg202Si = ((RawIntensityDataModel) im).specialBuildMatrixSiForHg202();
                Hg202Si.timesEquals(1.0 / r202Hg_204Hg.getValue().pow(2).doubleValue());

                ((DataModelFitFunctionInterface) im).setSelectedFitFunctionType(FitFunctionTypeEnum.NONE);
            }

            // june 2013
            // HG202 is first on list
            if (im.getDataModelName().equalsIgnoreCase(IsotopesEnum.Pb204.getName())) {
                ((RawIntensityDataModel) im).setCorrectedHg202Si(Hg202Si);
            } else {
                ((RawIntensityDataModel) im).setCorrectedHg202Si(null);
            }

            im.generateSetOfFitFunctions(true, true);
        }
    }

    private void calculateCorrectionForIsobaricInterference(DataModelInterface Hg202, DataModelInterface Pb204) {

        // update dec 2012
        // isobaric correction is across both background and onpeak
        // the graph of intensities for Pb204 will show the original data in green and the isobaric corrected in black
        // it is the corrected data that will be background fitted
        double[] Hg202BackgroundIntensities = ((RawIntensityDataModel) Hg202).getBackgroundVirtualCollector().getIntensities();
        double[] Hg202OnPeakIntensities = ((RawIntensityDataModel) Hg202).getOnPeakVirtualCollector().getIntensities();

        double[] Hg202BackgroundCorrections = new double[Hg202BackgroundIntensities.length];
        double[] Hg202OnPeakCorrections = new double[Hg202OnPeakIntensities.length];

        double[] Pb204BackgroundIntensities = ((RawIntensityDataModel) Pb204).getBackgroundVirtualCollector().getIntensities();
        double[] Pb204OnPeakIntensities = ((RawIntensityDataModel) Pb204).getOnPeakVirtualCollector().getIntensities();

        // the correction value used is natural 204/202 ratio = 4.346, 
        // TODO: which will eventually be a user-parameter
        for (int i = 0; i < Hg202BackgroundCorrections.length; i++) {
            Hg202BackgroundCorrections[i] = Hg202BackgroundIntensities[i] / r202Hg_204Hg.getValue().doubleValue();
            // now change the intensities by the correction to prepare for function fitting
            // the green "old intensities" will be caluted by adding these back together
            Pb204BackgroundIntensities[i] -= Hg202BackgroundCorrections[i];
        }

        for (int i = 0; i < Hg202OnPeakCorrections.length; i++) {
            Hg202OnPeakCorrections[i] = Hg202OnPeakIntensities[i] / r202Hg_204Hg.getValue().doubleValue();
            // now change the intensities by the correction to prepare for function fitting
            // the green "old intensities" will be caluted by adding these back together
            Pb204OnPeakIntensities[i] -= Hg202OnPeakCorrections[i];
        }

        // Pb204 background
        ((RawIntensityDataModel) Pb204).getBackgroundVirtualCollector().setIntensityCorrections(Arrays.copyOf(Hg202BackgroundCorrections, Hg202BackgroundCorrections.length));
        ((RawIntensityDataModel) Pb204).getBackgroundVirtualCollector().setIntensities(Arrays.copyOf(Pb204BackgroundIntensities, Pb204BackgroundIntensities.length));
        // Pb204 onPeak
        ((RawIntensityDataModel) Pb204).getOnPeakVirtualCollector().setIntensityCorrections(Arrays.copyOf(Hg202OnPeakCorrections, Hg202OnPeakCorrections.length));
        ((RawIntensityDataModel) Pb204).getOnPeakVirtualCollector().setIntensities(Arrays.copyOf(Pb204OnPeakIntensities, Pb204OnPeakIntensities.length));

    }

    /**
     *
     */
    public void correctOnPeakIntensitiesWithBackFitFunction() {
        for (VirtualCollectorModel c : virtualCollectors) {
            c.correctOnPeakIntensities();
        }
    }

//    public abstract void assignGainsToCollectors ( Map<String, Double> collectorNameToRelativeGainsMap );
//
//    public abstract void assignGainsUnctToCollectors ( Map<String, Double> collectorNameToRelativeGainsUnctMap );
    /**
     * Assume gains are supplied in NUPlasmaCollectors enum order
     *
     * @param collectorNameToRelativeGainsMap
     */
    public void assignGainsToCollectors(Map<String, Double> collectorNameToRelativeGainsMap) {

        Iterator<String> collectorNameIterator = //
                collectorNameToRelativeGainsMap.keySet().iterator();

        while (collectorNameIterator.hasNext()) {
            String key = collectorNameIterator.next();

            AbstractCollectorModel collector = collectorNameToModelMap.get(key);
            if (collector != null) {
                collector.setRelativeGain(collectorNameToRelativeGainsMap.get(key));
            }
        }

    }

    /**
     *
     * @param collectorNameToRelativeGainsUnctMap
     */
    public void assignGainsUnctToCollectors(Map<String, Double> collectorNameToRelativeGainsUnctMap) {
        Iterator<String> collectorNameIterator = //
                collectorNameToRelativeGainsUnctMap.keySet().iterator();

        while (collectorNameIterator.hasNext()) {
            String key = collectorNameIterator.next();

            AbstractCollectorModel collector = collectorNameToModelMap.get(key);
            if (collector != null) {
                collector.setRelativeGainUnct(collectorNameToRelativeGainsUnctMap.get(key));
            }
        }
    }

    /**
     * Assume deadTimes are supplied in NUPlasmaCollectors enum order
     *
     * @param collectorNameToDeadTimesMap
     */
    public void assignDeadTimesToCollectors(Map<String, Double> collectorNameToDeadTimesMap) {
        Iterator<String> collectorNameIterator = //
                collectorNameToDeadTimesMap.keySet().iterator();

        while (collectorNameIterator.hasNext()) {
            String key = collectorNameIterator.next();

            AbstractCollectorModel collector = collectorNameToModelMap.get(key);
            if (collector != null) {
                ((IonCounterCollectorModel) collector).setDeadTime(collectorNameToDeadTimesMap.get(key));
            }
        }

    }

    /**
     *
     * @param collectorNameToDeadTimesUnctMap
     */
    public void assignDeadTimesUnctToCollectors(Map<String, Double> collectorNameToDeadTimesUnctMap) {
        Iterator<String> collectorNameIterator = //
                collectorNameToDeadTimesUnctMap.keySet().iterator();

        while (collectorNameIterator.hasNext()) {
            String key = collectorNameIterator.next();

            AbstractCollectorModel collector = collectorNameToModelMap.get(key);
            if (collector != null) {
                ((IonCounterCollectorModel) collector).setDeadTimeUnct(collectorNameToDeadTimesUnctMap.get(key));
            }
        }
    }

    /**
     *
     * @param isotopeNameToIntegrationTimesMap
     */
    public void assignIntegrationTimesToCollectors(Map<IsotopesEnum, Double> isotopeNameToIntegrationTimesMap) {
        Map<IsotopesEnum, Double> isotopeToIntegrationTimeMap = isotopeMappingModel.getIsotopeToIntegrationTimeMap();

        Iterator<IsotopesEnum> isotopeNameIterator = //
                isotopeNameToIntegrationTimesMap.keySet().iterator();

        while (isotopeNameIterator.hasNext()) {
            IsotopesEnum key = isotopeNameIterator.next();
            isotopeToIntegrationTimeMap.put(key, isotopeNameToIntegrationTimesMap.get(key));
        }
    }

    /**
     *
     * @param collectorNameToResistorMap
     */
    public void assignResistorsCollectors(Map<String, FaradayCollectorModel.ResistorEnum> collectorNameToResistorMap) {
        Iterator<String> collectorNameIterator = //
                collectorNameToResistorMap.keySet().iterator();

        while (collectorNameIterator.hasNext()) {
            String key = collectorNameIterator.next();

            AbstractCollectorModel collector = collectorNameToModelMap.get(key);
            if (collector != null) {
                ((FaradayCollectorModel) collector).setResistor(//
                        collectorNameToResistorMap.get(key));
            }
        }
    }

    /**
     *
     * @param collectorNameToAmpNoiseMap
     */
    public void assignAmpNoiseToCollectors(Map<String, Double> collectorNameToAmpNoiseMap) {
        Iterator<String> collectorNameIterator = //
                collectorNameToAmpNoiseMap.keySet().iterator();

        while (collectorNameIterator.hasNext()) {
            String key = collectorNameIterator.next();

            AbstractCollectorModel collector = collectorNameToModelMap.get(key);
            if (collector != null) {
                ((FaradayCollectorModel) collector).setAmplifierNoiseVariance(collectorNameToAmpNoiseMap.get(key));
            }
        }
    }

    /**
     *
     * @param integrationTime
     */
    public abstract void assignIntegrationTime(double integrationTime);

    /**
     *
     * @param tripoliFractions
     * @return
     */
    public SortedMap<RawRatioNames, DownholeFractionationDataModel>//
            downholeFractionationAlphaDataModelsFactory(//
                    SortedSet<TripoliFraction> tripoliFractions) {

                @SuppressWarnings("MapReplaceableByEnumMap")
                SortedMap<RawRatioNames, DownholeFractionationDataModel> fractionationAlphaDataModels = new TreeMap<>();

                double[] acquireTimes = rawRatios.first().getOnPeakAquireTimesInSeconds();
                double[] normalizedOnPeakAquireTimes = rawRatios.first().getNormalizedOnPeakAquireTimes();

//        // same masking array keeps masking coordinated
                MaskingSingleton maskingArray = MaskingSingleton.getInstance();
                // next line now set at TripoliSession
//        maskingArray.setMaskingArray( AbstractMassSpecSetup.defaultDataActiveMap( countOfAcquisitions ) );

                DownholeFractionationDataModel r206_207w =//
                        new DownholeFractionationDataModel( //
                                //
                                RawRatioNames.r206_207w, new double[countOfAcquisitions], acquireTimes.clone(), normalizedOnPeakAquireTimes.clone(), maskingArray);
                DownholeFractionationDataModel r206_238w =//
                        new DownholeFractionationDataModel(//
                                //
                                RawRatioNames.r206_238w, new double[countOfAcquisitions], acquireTimes.clone(), normalizedOnPeakAquireTimes.clone(), maskingArray);
                DownholeFractionationDataModel r208_232w = //
                        new DownholeFractionationDataModel( //
                                //
                                RawRatioNames.r208_232w, new double[countOfAcquisitions], acquireTimes.clone(), normalizedOnPeakAquireTimes.clone(), maskingArray);

                // oct 2012 update fractionation models based on valid alpha ratios
                // first load assumed models
                fractionationAlphaDataModels.put(RawRatioNames.r206_207w, r206_207w);
                fractionationAlphaDataModels.put(RawRatioNames.r206_238w, r206_238w);
                fractionationAlphaDataModels.put(RawRatioNames.r208_232w, r208_232w);

                // now remove unusable
                SortedSet<DataModelInterface> ratiosSortedSet = tripoliFractions.first().getRatiosForFractionFitting();//.getValidRawRatios();

                Iterator<DataModelInterface> ratiosSortedSetIterator = ratiosSortedSet.iterator();
                while (ratiosSortedSetIterator.hasNext()) {
                    DataModelInterface ratio = ratiosSortedSetIterator.next();
                    if (!((RawRatioDataModel) ratio).isUsedForFractionationCorrections()) {
                        fractionationAlphaDataModels.remove(ratio.getRawRatioModelName());
                    }
                }

                return fractionationAlphaDataModels;
            }

            /**
             * @return the VIRTUAL_COLLECTOR_COUNT
             */
            public int getVIRTUAL_COLLECTOR_COUNT() {
                return VIRTUAL_COLLECTOR_COUNT;
            }

            /**
             *
             * @param size
             * @return
             */
            public static boolean[] defaultDataActiveMap(int size) {
                boolean[] dataActiveMap = new boolean[size];

                for (int i = 0; i < dataActiveMap.length; i++) {
                    dataActiveMap[i] = true;
                }

                return dataActiveMap;
            }

            /**
             * @return the COLLECTOR_DATA_FREQUENCY_MILLISECS
             */
            public long getCollectorDataFrequencyMillisecs() {
                return COLLECTOR_DATA_FREQUENCY_MILLISECS;
            }

            /**
             * @return the isotopeMappingModel
             */
            public IsotopeMappingModel getIsotopeMappingModel() {
                return isotopeMappingModel;
            }

            /**
             * @return the NAME
             */
            public String getNAME() {
                return NAME;
            }

            /**
             * @return the massSpecType
             */
            public MassSpecTypeEnum getMassSpecType() {
                return massSpecType;
            }

            /**
             * @return the COLLECTOR_DATA_FREQUENCY_MILLISECS
             */
            public long getCOLLECTOR_DATA_FREQUENCY_MILLISECS() {
                return COLLECTOR_DATA_FREQUENCY_MILLISECS;
            }

            /**
             * @return the commonLeadCorrectionHighestLevel
             */
            public String getCommonLeadCorrectionHighestLevel() {
                return commonLeadCorrectionHighestLevel;
            }

            /**
             * @param rawRatios the rawRatios to set
             */
            public void setRawRatios(SortedSet<DataModelInterface> rawRatios) {
                this.rawRatios = rawRatios;
            }

            /**
             * @param countOfAcquisitions the countOfAcquisitions to set
             */
            public void setCountOfAcquisitions(int countOfAcquisitions) {
                this.countOfAcquisitions = countOfAcquisitions;
            }

            /**
             * @return the Pb207
             */
            public DataModelInterface getPb207() {
                return Pb207;
            }

            /**
             * @return the Pb208
             */
            public DataModelInterface getPb208() {
                return Pb208;
            }

            /**
             * @return the Pb206
             */
            public DataModelInterface getPb206() {
                return Pb206;
            }

            /**
             * @return the U238
             */
            public DataModelInterface getU238() {
                return U238;
            }

            /**
             * @return the Th232
             */
            public DataModelInterface getTh232() {
                return Th232;
            }

            /**
             * @return the U235
             */
            public DataModelInterface getU235() {
                return U235;
            }

            /**
             * @return the Hg202
             */
            public DataModelInterface getHg202() {
                return Hg202;
            }

            /**
             * @return the Pb204
             */
            public DataModelInterface getPb204() {
                return Pb204;
            }
}
