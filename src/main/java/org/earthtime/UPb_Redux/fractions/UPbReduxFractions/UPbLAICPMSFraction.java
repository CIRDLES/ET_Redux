/*
 * UPbLAICPMSFraction.java
 *
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
package org.earthtime.UPb_Redux.fractions.UPbReduxFractions;

import Jama.Matrix;
import java.awt.geom.Path2D;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.TreeMap;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.UPb_Redux.fractions.Fraction;
import org.earthtime.UPb_Redux.fractions.FractionI;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.fractionReduction.PbcCorrectionDetails;
import org.earthtime.UPb_Redux.reduxLabData.ReduxLabData;
import org.earthtime.reportViews.ReportRowGUIInterface;
import org.earthtime.UPb_Redux.utilities.BrowserControl;
import org.earthtime.UPb_Redux.utilities.comparators.IntuitiveStringComparator;
import org.earthtime.UPb_Redux.valueModels.MeasuredRatioModel;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.dataDictionaries.Lambdas;
import org.earthtime.dataDictionaries.MeasuredRatios;
import org.earthtime.dataDictionaries.RadDates;
import org.earthtime.dataDictionaries.RadDatesForPbCorrSynchEnum;
import org.earthtime.dataDictionaries.RadRatios;
import org.earthtime.dataDictionaries.RadRatiosPbcCorrected;
import org.earthtime.fractions.ETFractionInterface;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataModels.initialPbModelsET.StaceyKramersInitialPbModelET;
import org.earthtime.ratioDataModels.initialPbModelsET.commonLeadLossCorrectionSchemes.AbstractCommonLeadLossCorrectionScheme;
import org.earthtime.ratioDataModels.initialPbModelsET.commonLeadLossCorrectionSchemes.CommonLeadLossCorrectionSchemeA1;
import org.earthtime.ratioDataModels.initialPbModelsET.commonLeadLossCorrectionSchemes.CommonLeadLossCorrectionSchemeA2;
import org.earthtime.ratioDataModels.initialPbModelsET.commonLeadLossCorrectionSchemes.CommonLeadLossCorrectionSchemeNONE;
import org.earthtime.ratioDataModels.physicalConstantsModels.PhysicalConstantsModel;

/**
 *
 * @author James F. Bowring
 */
public class UPbLAICPMSFraction extends Fraction implements
        FractionI,
        UPbFractionI,
        ETFractionInterface,
        ReportRowGUIInterface,
        Serializable {
//TODO: refactor this class = quick copy and simplification of UPbFraction, but has many common features. a

    // also has extraneous stuff like top panel 
    // Class variables
    private static final long serialVersionUID = -4018882827448106218L;
    // Instance variables
    private transient Path2D errorEllipsePath;
    private transient double ellipseRho;
    // march 2014 for isoplot 
//    private transient org.cirdles.isoplot.chart.concordia.ErrorEllipse errorEllipseNode;
    private transient boolean hasMeasuredLead;
    private transient boolean hasMeasuredUranium;
    private transient boolean selectedInDataTable;
    // the CURRENT ReduxLabData are made available to any active fraction
    private transient ReduxLabData myLabData;
    private String ratioType;
    private AbstractRatiosDataModel physicalConstantsModel; // fraction class has physicalConstantsModelID
    private int aliquotNumber;
    private boolean changed;
    private boolean deleted;
    private boolean rejected;
    private String fractionNotes;
    // detrital filtering
    private boolean filtered;
    // feb 2014
    private SortedMap<String, ValueModel> commonLeadCorrectionParameters;
    private SortedMap<String, BigDecimal> staceyKramerCorrectionParameters;
    private boolean useStaceyKramer;
    private AbstractCommonLeadLossCorrectionScheme commonLeadLossCorrectionScheme;
    private RadDatesForPbCorrSynchEnum radDateForSKSynch;
    private boolean standard;
    private SortedMap<String, Double> upperPhiMap;
    //nov 2014 section 7a for common lead correction iuncertainties
    private Matrix SfciTotal;
    private Matrix SrAll;
    private Matrix SFc204;
    private Matrix JPbccs;
    private boolean correctedForPbc;

    /**
     *
     */
    public UPbLAICPMSFraction() {
        super(ReduxConstants.DEFAULT_OBJECT_NAME, ReduxConstants.DEFAULT_OBJECT_NAME);
        setIsLegacy(false);
        this.ratioType = "UPb";

        this.physicalConstantsModel = PhysicalConstantsModel.getEARTHTIMEPhysicalConstantsModel();

        this.aliquotNumber = 1;

        this.changed = true;
        this.deleted = false;

        this.fractionNotes = "";

        this.rejected = false;

        // trimming our size
        setAnalysisMeasures(new ValueModel[0]);
        setRadiogenicIsotopeRatios(new ValueModel[0]);
        setRadiogenicIsotopeDates(new ValueModel[0]);
        setCompositionalMeasures(new ValueModel[0]);
        setSampleIsochronRatios(new ValueModel[0]);

        setInitialPbModel(null);

        hasMeasuredLead = false;

        hasMeasuredUranium = false;

        commonLeadCorrectionParameters = null;
        staceyKramerCorrectionParameters = null;
        useStaceyKramer = false;
        commonLeadLossCorrectionScheme = CommonLeadLossCorrectionSchemeNONE.getInstance();
        this.radDateForSKSynch = RadDatesForPbCorrSynchEnum.date206_238r;
        this.standard = false;

        // filled in during fractionation / common lead correction
        upperPhiMap = new TreeMap<>();
        initializeUpperPhiMap();

        this.correctedForPbc = false;

    }

    /**
     * Creates a new instance of UPbFraction with default attributes.
     * UPbLegacyFraction is used to receive incoming fraction data and to save
     * fraction data via xml serialization. UPbFraction is also used as the
     * analysis framework, but is not propagated directly to the Aliquot.
     *
     * @param fractionID
     */
    public UPbLAICPMSFraction(String fractionID) {
        this();
        this.setFractionID(fractionID);
    }

    /**
     * Jan 2015 specialty handler for serialized fractions
     *
     * @param aliquotNum
     * @param fraction
     * @param labData
     * @param pbBlank
     * @throws BadLabDataException
     */
    public UPbLAICPMSFraction(
            int aliquotNum,
            FractionI fraction,
            ReduxLabData labData) throws BadLabDataException {

        this();

        // custom settings
        this.aliquotNumber = aliquotNum;
        this.myLabData = labData;
        this.changed = false;

        // Fraction fields
        this.setSampleName(fraction.getSampleName());
        this.setFractionID(fraction.getFractionID());
        this.setGrainID(fraction.getFractionID());

        this.getValuesFrom(fraction, true);

        this.setMeasuredRatios(fraction.copyMeasuredRatios());
    }

    /**
     * Creates a new UPbLegacyFraction from an imported AnalysisFraction. The
     * imported fraction cannot be changed, only manipulated.
     *
     * @param aliquotNum
     * @param fraction
     */
    public UPbLAICPMSFraction(
            int aliquotNum,
            FractionI fraction) {

        this();

        // custom settings
        this.aliquotNumber = aliquotNum;
        this.changed = false;

        // Fraction fields
        this.setSampleName(fraction.getSampleName());
        this.setFractionID(fraction.getFractionID());
        this.setGrainID(fraction.getFractionID());

        this.getValuesFrom(fraction, true);

        this.setMeasuredRatios(fraction.copyMeasuredRatios());

    }

    /**
     * Provides for ordering fractions in aliquots case insensitive
     *
     * @param fraction
     * @return
     * @throws java.lang.ClassCastException
     */
    @Override
    public int compareTo(Fraction fraction) throws ClassCastException {
        // TODO May 2010 Eventaully consider grainID
        String uPbFractionID = fraction.getFractionID();
        String uPbFractionAliquotNum = String.valueOf(fraction.getAliquotNumber());
        String myID = (uPbFractionAliquotNum + "." + uPbFractionID).toUpperCase();

        Comparator<String> forNoah = new IntuitiveStringComparator<>();

        return forNoah.compare((String.valueOf(this.getAliquotNumber()) + "." + this.getFractionID()).toUpperCase(), myID);
    }

    /**
     *
     * @return
     */
    @Override
    public Object[] getFractionTableRowData() {
        String tracerName = "N/A";

        Object[] retval = {
            String.valueOf(getAliquotNumber()), !isRejected(), // oct 2009 for fraction selector where SELECTED = NOT rejected
            getFractionNotes().length() > 0,// notes column added nov 2009 >0 ==> bold
            getFractionID(), // for fraction edit button
            tableEntryForMeasuredRatio(MeasuredRatios.r206_204m.getName()),
            tableEntryForMeasuredRatio(MeasuredRatios.r206_207m.getName()),
            tableEntryForMeasuredRatio(MeasuredRatios.r206_208m.getName()),
            tableEntryForMeasuredRatio(MeasuredRatios.r206_205m.getName()),
            tableEntryForMeasuredRatio(MeasuredRatios.r207_205m.getName()),
            tableEntryForMeasuredRatio(MeasuredRatios.r208_205m.getName()),
            tableEntryForMeasuredRatio(MeasuredRatios.r202_205m.getName()),
            tableEntryForMeasuredRatio(MeasuredRatios.r238_235m.getName()),
            tableEntryForMeasuredRatio(MeasuredRatios.r233_235m.getName()),
            tableEntryForMeasuredRatio(MeasuredRatios.r233_236m.getName()),
            tracerName
        };

        return retval;
    }

    private String tableEntryForMeasuredRatio(String measuredRatio) {
        String retVal = " ";

        if (getMeasuredRatioByName(measuredRatio).getValue().compareTo(BigDecimal.ZERO) != 0) {
            retVal = ((MeasuredRatioModel) getMeasuredRatioByName(measuredRatio)).toTableFormat();
        }

        return retVal;
    }

    /**
     *
     */
    public void performCommonLeadLossCorrectionsToRatios() {

//        if (commonLeadLossCorrectionScheme != null) {
        if (!(commonLeadLossCorrectionScheme instanceof CommonLeadLossCorrectionSchemeNONE)) {
            calculateSchemeA1A2CommonLeadCorrectedDate();
            calculateSchemeB1B2CommonLeadCorrectedDate();
        }

    }

    /**
     *
     */
    public void calculateSchemeB1B2CommonLeadCorrectedDate() {
        /*  SEE SchemeB1   calculatePbCorrectedAge
         You should now have the fractionation-corrected 206/204, 207/204, and 208/204 log-ratios.  
         Use the exponential function to transform these into isotope ratios, and you already also additionally 
         have the fractionation-corrected unknown 206/207, 206/238, and 208/232 ratios from Section 10.

         Now perform the following calculations for Pbc-corrected ratios (rX_X_PbcCorr)

         r207_206_PbcCorr = (r207_204fc - r207_204c)/(r206_204fc - r206_204c)
         r206_238_PbcCorr = r206_238fc * (1 - r206_204c / r206_204fc)
         r238_206_PbcCorr = 1/r206_238_PbCorr (note: no date for this ratio, used for T-W plot)
         r208_232_PbcCorr = r208_232fc * (1 - r208_204c / r208_204fc)
         r207_235_PbcCorr = r206_238fc * r238_235s / r206_207fc * (1 - r207_204c / r207_204fc)
         */

        // first zero out ratios, then proceed if type B
        ValueModel r207_206_PbcCorr = new ValueModel(RadRatiosPbcCorrected.r207_206_PbcCorr.getName());
        ValueModel r206_238_PbcCorr = new ValueModel(RadRatiosPbcCorrected.r206_238_PbcCorr.getName());
        ValueModel r238_206_PbcCorr = new ValueModel(RadRatiosPbcCorrected.r238_206_PbcCorr.getName());
        ValueModel r208_232_PbcCorr = new ValueModel(RadRatiosPbcCorrected.r208_232_PbcCorr.getName());
        ValueModel r207_235_PbcCorr = new ValueModel(RadRatiosPbcCorrected.r207_235_PbcCorr.getName());

        setRadiogenicIsotopeRatioByName(RadRatiosPbcCorrected.r207_206_PbcCorr.getName(), r207_206_PbcCorr);
        setRadiogenicIsotopeRatioByName(RadRatiosPbcCorrected.r206_238_PbcCorr.getName(), r206_238_PbcCorr);
        setRadiogenicIsotopeRatioByName(RadRatiosPbcCorrected.r238_206_PbcCorr.getName(), r238_206_PbcCorr);
        setRadiogenicIsotopeRatioByName(RadRatiosPbcCorrected.r208_232_PbcCorr.getName(), r208_232_PbcCorr);
        setRadiogenicIsotopeRatioByName(RadRatiosPbcCorrected.r207_235_PbcCorr.getName(), r207_235_PbcCorr);

        if (commonLeadLossCorrectionScheme.getName().contains("B")) {
            // prepare ratios for calculations
            // make valuemodel from measuredvaluemodel
            ValueModel r206_204fc = new ValueModel("r206_204tfc");
            r206_204fc.copyValuesFrom(getMeasuredRatioByName("r206_204m"));
            setSampleIsochronRatiosByName("r206_204tfc", r206_204fc);
//            ValueModel r206_204fc = getMeasuredRatioByName("r206_204m").copy();
//            r206_204fc.setName("r206_204tfc");
//            setSampleIsochronRatiosByName("r206_204tfc", r206_204fc);

            ValueModel r206_204c = new ValueModel("r206_204c");

            ValueModel r207_204fc = getMeasuredRatioByName("r207_204m").copy();
            r207_204fc.setName("r207_204fc");

            ValueModel r207_204c = new ValueModel("r207_204c");

            ValueModel r208_204fc = getMeasuredRatioByName("r208_204m").copy();
            r208_204fc.setName("r208_204fc");

            ValueModel r208_204c = new ValueModel("r208_204c");

            ValueModel r208_232fc = getMeasuredRatioByName("r208_232m").copy();

            ValueModel r206_238fc = getMeasuredRatioByName("r206_238m");

            ValueModel r207_235fc = getRadiogenicIsotopeRatioByName(RadRatios.r207_235r.getName());

            // per Noah email 6 Nov 2014
            try {
                r206_204c = commonLeadCorrectionParameters.get("r206_204c");
            } catch (Exception e) {
            }

            try {
                r207_204c = commonLeadCorrectionParameters.get("r207_204c");
            } catch (Exception e) {
            }

            try {
                r208_204c = commonLeadCorrectionParameters.get("r208_204c");
            } catch (Exception e) {
            }

            if (commonLeadLossCorrectionScheme.getName().contains("B2")) {
                double c719 = 7.19;
                double c9307 = 9.307;
                double c13788 = 137.88;
                double c10294 = 10.294;
                double c3321 = 33.21;
                double c29487 = 29.487;
                double c974 = 9.74;
                double c11152 = 11.152;
                double c12998 = 12.998;
                double c3684 = 36.84;
                double c3123 = 31.23;
                double c37 = 3.7E9;
                double c457 = 4.57E9;

                double targetDate = 0.0;
                double estimatedDatePbcCorr = 0.0;
                BigDecimal lambda238 = BigDecimal.ZERO;
                try {
                    lambda238 = ReduxLabData.getInstance().getDefaultPhysicalConstantsModel().getDatumByName(Lambdas.lambda238.getName()).getValue();
                } catch (BadLabDataException badLabDataException) {
                }

                BigDecimal lambda235 = BigDecimal.ZERO;
                try {
                    lambda235 = ReduxLabData.getInstance().getDefaultPhysicalConstantsModel().getDatumByName(Lambdas.lambda235.getName()).getValue();
                } catch (BadLabDataException badLabDataException) {
                }

                BigDecimal lambda232 = BigDecimal.ZERO;
                try {
                    lambda232 = ReduxLabData.getInstance().getDefaultPhysicalConstantsModel().getDatumByName(Lambdas.lambda232.getName()).getValue();
                } catch (BadLabDataException badLabDataException) {
                }

                switch (radDateForSKSynch) {
                    case date206_238r:
                        targetDate = getRadiogenicIsotopeDateByName(RadDates.age206_238r).getValue().doubleValue();
                        if (targetDate < 3.7e9) {
                            //r206_238_PbcCorr = (r206_238fc*((c11152 + c974*exp(c37*lambda238))/r206_204fc - 1) - 1)/((c974*r206_238fc)/r206_204fc - 1) - 1
                            double value = 0;
                            try {
                                value = (r206_238fc.getValue().doubleValue() * ((c11152 + c974 * Math.exp(c37 * lambda238.doubleValue())) / r206_204fc.getValue().doubleValue() - 1.0) - 1.0)//
                                        / ((c974 * r206_238fc.getValue().doubleValue()) / r206_204fc.getValue().doubleValue() - 1.0) - 1.0;
                            } catch (Exception e) {
                            }
                            r207_206_PbcCorr.setValue(value);
                        } else {
                            // r206_238_PbcCorr = (r206_238fc*((c9307 + c719*exp(c457*lambda238))/r206_204fc - 1) - 1)/((c719*r206_238fc)/r206_204fc - 1) - 1
                            double value = 0;
                            try {
                                value = (r206_238fc.getValue().doubleValue() * ((c9307 + c719 * Math.exp(c457 * lambda238.doubleValue())) / r206_204fc.getValue().doubleValue() - 1.0) - 1.0)//
                                        / ((c719 * r206_238fc.getValue().doubleValue()) / r206_204fc.getValue().doubleValue() - 1.0) - 1.0;
                            } catch (Exception e) {
                            }
                            r207_206_PbcCorr.setValue(value);
                        }
                        estimatedDatePbcCorr = 1.0 / lambda238.doubleValue() * Math.log(r206_238_PbcCorr.getValue().doubleValue() + 1.0);

                        break;
                    case date207_235r:
                        targetDate = getRadiogenicIsotopeDateByName(RadDates.age207_235r).getValue().doubleValue();
                        if (targetDate < 3.7e9) {
                            // r207_235_PbcCorr = (r207_235fc*((c12998 + (c974*exp(c37*lambda235))/c13788)/r207_204fc - 1) - 1) / ((c974*r207_235fc)/(c13788*r207_204fc) - 1) - 1
                            double value = 0;
                            try {
                                value = (r207_235fc.getValue().doubleValue() * ((c12998 + (c974 * Math.exp(c37 * lambda235.doubleValue())) / c13788) / r207_204fc.getValue().doubleValue() - 1.0) - 1.0)//
                                        / ((c974 * r207_235fc.getValue().doubleValue()) / (c13788 * r207_204fc.getValue().doubleValue()) - 1.0) - 1.0;
                            } catch (Exception e) {
                            }
                            r207_235_PbcCorr.setValue(value);
                        } else {
                            // r207_235_PbcCorr = (r207_235fc*((c10294 + (c719*exp(c457*lambda235))/c13788)/r207_204fc - 1) - 1) / ((c719*r207_235fc)/(c13788*r207_204fc) - 1) - 1
                            double value = 0;
                            try {
                                value = (r207_235fc.getValue().doubleValue() * ((c10294 + (c719 * Math.exp(c457 * lambda235.doubleValue())) / c13788) / r207_204fc.getValue().doubleValue() - 1.0) - 1.0)//
                                        / ((c719 * r207_235fc.getValue().doubleValue()) / (c13788 * r207_204fc.getValue().doubleValue()) - 1.0) - 1.0;
                            } catch (Exception e) {
                            }
                            r207_235_PbcCorr.setValue(value);
                        }
                        estimatedDatePbcCorr = 1.0 / lambda235.doubleValue() * Math.log(r207_235_PbcCorr.getValue().doubleValue() + 1.0);

                        break;
                    case date208_232r:
                        targetDate = getRadiogenicIsotopeDateByName(RadDates.age208_232r).getValue().doubleValue();
                        if (targetDate < 3.7e9) {
                            //r208_232_PbcCorr = (r208_232fc*((c3123 + c3684*exp(c37*lambda232))/r208_204fc - 1) - 1) /((c3684*r208_232fc)/r208_204fc - 1) - 1
                            double value = (r208_232fc.getValue().doubleValue() * ((c3123 + c3684 * Math.exp(c37 * lambda232.doubleValue())) / r208_204fc.getValue().doubleValue() - 1.0) - 1.0) //
                                    / ((c3684 * r208_232fc.getValue().doubleValue()) / r208_204fc.getValue().doubleValue() - 1.0) - 1.0;
                            r208_232_PbcCorr.setValue(value);

                        } else {
                            // r208_232_PbcCorr = (r208_232fc*((c29487 + c3321*exp(c457*lambda232))/r208_204fc - 1) - 1)/((c3321*r208_232fc)/r208_204fc - 1) - 1
                            double value = (r208_232fc.getValue().doubleValue() * ((c29487 + c3321 * Math.exp(c457 * lambda232.doubleValue())) / r208_204fc.getValue().doubleValue() - 1.0) - 1.0)//
                                    / ((c3321 * r208_232fc.getValue().doubleValue()) / r208_204fc.getValue().doubleValue() - 1.0) - 1.0;
                            r208_232_PbcCorr.setValue(value);
                        }
                        estimatedDatePbcCorr = 1.0 / lambda232.doubleValue() * Math.log(r208_232_PbcCorr.getValue().doubleValue() + 1.0);

                        break;
                }

                // now use StaceyKramers with this estimated date
                AbstractRatiosDataModel skInitialPbModel = new StaceyKramersInitialPbModelET();
                try {
                    ((StaceyKramersInitialPbModelET) skInitialPbModel).calculateRatios(//
                            new BigDecimal(estimatedDatePbcCorr / 1e6), lambda238, lambda235, lambda232);

                    r206_204c.setValue(skInitialPbModel.getDatumByName("r206_204c").getValue());
                    r207_204c.setValue(skInitialPbModel.getDatumByName("r207_204c").getValue());
                    r208_204c.setValue(skInitialPbModel.getDatumByName("r208_204c").getValue());
                } catch (Exception e) {
                }
            }

            // calculate Pbc-corrected ratios or use non-corrected
            if (r207_204fc.hasPositiveValue()) {
                correctedForPbc = true;
                r207_206_PbcCorr.setValue(
                        (r207_204fc.getValue().subtract(r207_204c.getValue())//
                        .divide(r206_204fc.getValue().subtract(r206_204c.getValue()), ReduxConstants.mathContext15)));
            } else {
                correctedForPbc = false;
                r207_206_PbcCorr.copyValuesFrom(getRadiogenicIsotopeRatioByName(RadRatios.r207_206r.getName()));
            }

            if (r206_204fc.hasPositiveValue()) {
                r206_238_PbcCorr.setValue(//
                        getMeasuredRatioByName("r206_238m").getValue()//
                        .multiply(BigDecimal.ONE//
                                .subtract(r206_204c.getValue().divide(r206_204fc.getValue(), ReduxConstants.mathContext15))));
            } else {
                r206_238_PbcCorr.copyValuesFrom(getRadiogenicIsotopeRatioByName(RadRatios.r206_238r.getName()));
            }

            if (r206_238_PbcCorr.hasPositiveValue()) {
                r238_206_PbcCorr.setValue(BigDecimal.ONE.divide(r206_238_PbcCorr.getValue(), ReduxConstants.mathContext15));
            }

            if (r208_204fc.hasPositiveValue()) {
                r208_232_PbcCorr.setValue(//
                        getMeasuredRatioByName("r208_232m").getValue()//
                        .multiply(BigDecimal.ONE//
                                .subtract(r208_204c.getValue().divide(r208_204fc.getValue(), ReduxConstants.mathContext15))));
            } else {
                r208_232_PbcCorr.copyValuesFrom(getRadiogenicIsotopeRatioByName(RadRatios.r208_232r.getName()));
            }

            if (r207_204fc.hasPositiveValue()) {
                r207_235_PbcCorr.setValue(//
                        getMeasuredRatioByName("r206_238m").getValue()//
                        .multiply(ReduxLabData.getInstance().getDefaultR238_235s().getValue())//
                        .divide(getMeasuredRatioByName("r206_207m").getValue(), ReduxConstants.mathContext15)//
                        .multiply(BigDecimal.ONE//
                                .subtract(r207_204c.getValue()//
                                        .divide(r207_204fc.getValue(), ReduxConstants.mathContext15))));
            } else {
                r207_235_PbcCorr.copyValuesFrom(getRadiogenicIsotopeRatioByName(RadRatios.r207_235r.getName()));
            }

            // testing - class used for output to file for Noah
            PbcCorrectionDetails.upperPhi_r206_204 = upperPhiMap.get("upperPhi_r206_204");
            PbcCorrectionDetails.upperPhi_r207_204 = upperPhiMap.get("upperPhi_r207_204");
            PbcCorrectionDetails.upperPhi_r208_204 = upperPhiMap.get("upperPhi_r208_204");

            PbcCorrectionDetails.r206_204fc = r206_204fc;
            PbcCorrectionDetails.r207_204fc = r207_204fc;
            PbcCorrectionDetails.r208_204fc = r208_204fc;

            PbcCorrectionDetails.r207_206_PbcCorr = r207_206_PbcCorr;
            PbcCorrectionDetails.r206_238_PbcCorr = r206_238_PbcCorr;
            PbcCorrectionDetails.r238_206_PbcCorr = r238_206_PbcCorr;
            PbcCorrectionDetails.r208_232_PbcCorr = r208_232_PbcCorr;
            PbcCorrectionDetails.r207_235_PbcCorr = r207_235_PbcCorr;

            // test if real correction
            if (!upperPhiMap.containsValue(0.0) || commonLeadLossCorrectionScheme.getName().contains("B2")) {
                // uncertainty calcs need to refactor to move to correction scheme

                double dR68pbcc__dR206_204fc = 0;
                try {
                    dR68pbcc__dR206_204fc = (r206_204c.getValue().doubleValue() * r206_238fc.getValue().doubleValue()) / Math.pow(r206_204fc.getValue().doubleValue(), 2);
                } catch (Exception e) {
                }
                if (Double.isInfinite(dR68pbcc__dR206_204fc)) {
                    dR68pbcc__dR206_204fc = 0.0;
                }

                double dR68pbcc__dR206_204c = 0;
                try {
                    dR68pbcc__dR206_204c = -r206_238fc.getValue().doubleValue() / r206_204fc.getValue().doubleValue();
                } catch (Exception e) {
                }
                if (Double.isInfinite(dR68pbcc__dR206_204c)) {
                    dR68pbcc__dR206_204c = 0.0;
                }

                double dR68pbcc__dR206_238fc = 0;
                try {
                    dR68pbcc__dR206_238fc = 1.0 - (r206_204c.getValue().doubleValue() / r206_204fc.getValue().doubleValue());
                } catch (Exception e) {
                }
                if (Double.isInfinite(dR68pbcc__dR206_238fc)) {
                    dR68pbcc__dR206_238fc = 0.0;
                }

                double dR76pbcc__dR207_204fc = 0;
                try {
                    dR76pbcc__dR207_204fc = -1.0 / (r206_204c.getValue().doubleValue() - r206_204fc.getValue().doubleValue());
                } catch (Exception e) {
                }
                if (Double.isInfinite(dR76pbcc__dR207_204fc)) {
                    dR76pbcc__dR207_204fc = 0.0;
                }

                double dR76pbcc__dR207_204c = 0;
                try {
                    dR76pbcc__dR207_204c = 1.0 / (r206_204c.getValue().doubleValue() - r206_204fc.getValue().doubleValue());
                } catch (Exception e) {
                }
                if (Double.isInfinite(dR76pbcc__dR207_204c)) {
                    dR76pbcc__dR207_204c = 0.0;
                }

                double dR76pbcc__dR206_204fc = 0;
                try {
                    dR76pbcc__dR206_204fc = (r207_204c.getValue().doubleValue() - r207_204fc.getValue().doubleValue()) / Math.pow(r206_204c.getValue().doubleValue() - r206_204fc.getValue().doubleValue(), 2);
                } catch (Exception e) {
                }
                if (Double.isInfinite(dR76pbcc__dR206_204fc)) {
                    dR76pbcc__dR206_204fc = 0.0;
                }

                double dR76pbcc__dR206_204c = 0;
                try {
                    dR76pbcc__dR206_204c = -(r207_204c.getValue().doubleValue() - r207_204fc.getValue().doubleValue()) / Math.pow(r206_204c.getValue().doubleValue() - r206_204fc.getValue().doubleValue(), 2);
                } catch (Exception e) {
                }
                if (Double.isInfinite(dR76pbcc__dR206_204c)) {
                    dR76pbcc__dR206_204c = 0.0;
                }

                double dR86pbcc__dR206_204fc = 0;
                try {
                    dR86pbcc__dR206_204fc = -(r206_204c.getValue().doubleValue() * (1.0 / r206_238fc.getValue().doubleValue())) / Math.pow(r206_204c.getValue().doubleValue() - r206_204fc.getValue().doubleValue(), 2);
                } catch (Exception e) {
                }
                if (Double.isInfinite(dR86pbcc__dR206_204fc)) {
                    dR86pbcc__dR206_204fc = 0.0;
                }

                double dR86pbcc__dR206_204c = 0;
                try {
                    dR86pbcc__dR206_204c = (r206_204fc.getValue().doubleValue() * (1.0 / r206_238fc.getValue().doubleValue())) / Math.pow(r206_204c.getValue().doubleValue() - r206_204fc.getValue().doubleValue(), 2);
                } catch (Exception e) {
                }
                if (Double.isInfinite(dR86pbcc__dR206_204c)) {
                    dR86pbcc__dR206_204c = 0.0;
                }

                double dR86pbcc__dR238_206fc = 0;
                try {
                    dR86pbcc__dR238_206fc = -r206_204fc.getValue().doubleValue() / (r206_204c.getValue().doubleValue() - r206_204fc.getValue().doubleValue());
                } catch (Exception e) {
                }
                if (Double.isInfinite(dR86pbcc__dR238_206fc)) {
                    dR86pbcc__dR238_206fc = 0.0;
                }

                double dR82pbcc__dR208_204fc = 0;
                try {
                    dR82pbcc__dR208_204fc = (r208_204c.getValue().doubleValue() * r208_232fc.getValue().doubleValue()) / Math.pow(r208_204fc.getValue().doubleValue(), 2);
                } catch (Exception e) {
                }
                if (Double.isInfinite(dR82pbcc__dR208_204fc)) {
                    dR82pbcc__dR208_204fc = 0.0;
                }

                double dR82pbcc__dR208_204c = 0;
                try {
                    dR82pbcc__dR208_204c = -r208_232fc.getValue().doubleValue() / r208_204fc.getValue().doubleValue();
                } catch (Exception e) {
                }
                if (Double.isInfinite(dR82pbcc__dR208_204c)) {
                    dR82pbcc__dR208_204c = 0.0;
                }

                double dR82pbcc__dR208_232fc = 0;
                try {
                    dR82pbcc__dR208_232fc = 1 - r208_204c.getValue().doubleValue() / r208_204fc.getValue().doubleValue();
                } catch (Exception e) {
                }

                if (Double.isInfinite(dR82pbcc__dR208_232fc)) {
                    dR82pbcc__dR208_232fc = 0.0;
                }

                double dR75pbcc__dR207_204fc = 0;
                try {
                    dR75pbcc__dR207_204fc = (r207_204c.getValue().doubleValue() * r207_235fc.getValue().doubleValue()) / Math.pow(r207_204fc.getValue().doubleValue(), 2);
                } catch (Exception e) {
                }
                if (Double.isInfinite(dR75pbcc__dR207_204fc)) {
                    dR75pbcc__dR207_204fc = 0.0;
                }

                double dR75pbcc__dR207_204c = 0;
                try {
                    dR75pbcc__dR207_204c = -r207_235fc.getValue().doubleValue() / r207_204fc.getValue().doubleValue();
                } catch (Exception e) {
                }
                if (Double.isInfinite(dR75pbcc__dR207_204c)) {
                    dR75pbcc__dR207_204c = 0.0;
                }

                double dR75pbcc__dR207_235fc = 0;
                try {
                    dR75pbcc__dR207_235fc = 1.0 - (r207_204c.getValue().doubleValue() / r207_204fc.getValue().doubleValue());
                } catch (Exception e) {
                }
                if (Double.isInfinite(dR75pbcc__dR207_235fc)) {
                    dR75pbcc__dR207_235fc = 0.0;
                }

                PbcCorrectionDetails.dR68pbcc__dR206_204fc = dR68pbcc__dR206_204fc;
                PbcCorrectionDetails.dR68pbcc__dR206_204c = dR68pbcc__dR206_204c;
                PbcCorrectionDetails.dR68pbcc__dR206_238fc = dR68pbcc__dR206_238fc;

                PbcCorrectionDetails.dR75pbcc__dR207_204fc = dR75pbcc__dR207_204fc;
                PbcCorrectionDetails.dR75pbcc__dR207_204c = dR75pbcc__dR207_204c;
                PbcCorrectionDetails.dR75pbcc__dR207_235fc = dR75pbcc__dR207_235fc;

                PbcCorrectionDetails.dR76pbcc__dR207_204fc = dR76pbcc__dR207_204fc;
                PbcCorrectionDetails.dR76pbcc__dR207_204c = dR76pbcc__dR207_204c;
                PbcCorrectionDetails.dR76pbcc__dR206_204fc = dR76pbcc__dR206_204fc;
                PbcCorrectionDetails.dR76pbcc__dR206_204c = dR76pbcc__dR206_204c;

                PbcCorrectionDetails.dR86pbcc__dR206_204fc = dR86pbcc__dR206_204fc;
                PbcCorrectionDetails.dR86pbcc__dR206_204c = dR86pbcc__dR206_204c;
                PbcCorrectionDetails.dR86pbcc__dR238_206fc = dR86pbcc__dR238_206fc;

                PbcCorrectionDetails.dR82pbcc__dR208_204fc = dR82pbcc__dR208_204fc;
                PbcCorrectionDetails.dR82pbcc__dR208_204c = dR82pbcc__dR208_204c;
                PbcCorrectionDetails.dR82pbcc__dR208_232fc = dR82pbcc__dR208_232fc;
            }

        }// end of check for scheme B
    }

    /**
     *
     */
    public void calculateSchemeA1A2CommonLeadCorrectedDate() {

        // zero out date
        ValueModel PbcCorr_UPb_Date = new ValueModel(RadDates.PbcCorr_UPb_Date.toString());

        if (commonLeadLossCorrectionScheme.getName().contains("A")) {
            // calculated parameters
            ValueModel r238_206fc = getMeasuredRatioByName("r206_238m").copy();
            // invert value
            try {
                r238_206fc.setName("r238_206fc");
                r238_206fc.setValue(BigDecimal.ONE.divide(r238_206fc.getValue(), ReduxConstants.mathContext15));
            } catch (Exception e) {
            }
            commonLeadCorrectionParameters.put("r238_206fc", r238_206fc);

            ValueModel r207_206fc = getMeasuredRatioByName("r206_207m").copy();
            // invert value
            try {
                r207_206fc.setName("r207_206fc");
                r207_206fc.setValue(BigDecimal.ONE.divide(r207_206fc.getValue(), ReduxConstants.mathContext15));
            } catch (Exception e) {
            }
            commonLeadCorrectionParameters.put("r207_206fc", r207_206fc);

            try {
                PbcCorr_UPb_Date = //
                        commonLeadLossCorrectionScheme.calculatePbCorrectedAge(//
                                commonLeadCorrectionParameters, //
                                staceyKramerCorrectionParameters, //
                                useStaceyKramer, //
                                ReduxLabData.getInstance().getDefaultR238_235s(), //
                                ReduxLabData.getInstance().getDefaultPhysicalConstantsModel().getDatumByName(Lambdas.lambda235.getName()), //
                                ReduxLabData.getInstance().getDefaultPhysicalConstantsModel().getDatumByName(Lambdas.lambda238.getName()));
            } catch (BadLabDataException badLabDataException) {
            }

        }

        setRadiogenicIsotopeDateByName(RadDates.PbcCorr_UPb_Date, PbcCorr_UPb_Date);

    }

    /**
     *
     * @param ipmName
     * @return
     */
    public ValueModel getInitialPbModelRatioByName(String ipmName) {
        return getInitialPbModel().getDatumByName(ipmName);
    }

    /**
     *
     * @return
     */
    public static String[] getColumnNames() {
        return columnNames;
    }

    /**
     *
     * @return
     */
    @Override
    public int getAliquotNumber() {
        return aliquotNumber;
    }

    /**
     *
     * @param aliquotNumber
     */
    @Override
    public void setAliquotNumber(int aliquotNumber) {
        this.aliquotNumber = aliquotNumber;
        setChanged(true);
    }

    /**
     *
     * @return
     */
    @Override
    public String getRatioType() {
        return ratioType;
    }

    /**
     *
     * @param RatioType
     */
    @Override
    public void setRatioType(String RatioType) {
        this.ratioType = RatioType;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isChanged() {
        return changed;
    }

    /**
     *
     * @param changed
     */
    @Override
    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isDeleted() {
        return deleted;
    }

    /**
     *
     * @param deleted
     */
    @Override
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    /**
     *
     * @return
     */
    @Override
    public String getPhysicalConstantsModelID() {
        return getPhysicalConstantsModel().getNameAndVersion();
    }

    /**
     *
     * @return
     */
    @Override
    public AbstractRatiosDataModel getPhysicalConstantsModel() {
        if (physicalConstantsModel == null) {
            physicalConstantsModel = PhysicalConstantsModel.getEARTHTIMEPhysicalConstantsModel();
        }
        return physicalConstantsModel;
    }

    /**
     *
     * @param physicalConstantsModel
     */
    @Override
    public void setPhysicalConstantsModel(AbstractRatiosDataModel physicalConstantsModel) {
        if ((this.physicalConstantsModel == null)
                || (!this.physicalConstantsModel.equals(physicalConstantsModel))) {
            this.physicalConstantsModel = physicalConstantsModel;
            this.setChanged(true);
            System.out.println(this.getFractionID() //
                    + "  is getting new physical constants model = "//
                    + physicalConstantsModel.getNameAndVersion());
        }
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isRejected() {
        return rejected;
    }

    /**
     *
     * @param rejected
     */
    @Override
    public void setRejected(boolean rejected) {
        this.rejected = rejected;
    }

    /**
     *
     */
    public void toFileAllDataValues() {
        File dataValuesFile = new File("ALL_VALUES_" + getFractionID() + ".txt");
        PrintWriter outputWriter;
        try {
            outputWriter = new PrintWriter(new FileWriter(dataValuesFile));
            outputWriter.println("\n\n******   FRACTION " + getFractionID() + "   ********************\n\n");

            // measured ratios
            outputWriter.println("measured ratios");
            for (ValueModel measuredRatio : getMeasuredRatios()) {
                outputWriter.println(measuredRatio.formatValueAndOneSigmaABSForTesting());
            }
            outputWriter.println();

            // AnalysisMeasures
            outputWriter.println("AnalysisMeasures");
            Arrays.sort(getAnalysisMeasures());
            for (ValueModel analysisMeasure : getAnalysisMeasures()) {
                outputWriter.println(analysisMeasure.formatValueAndOneSigmaABSForTesting());
            }
            outputWriter.println();

            // CompositionalMeasures
            outputWriter.println("CompositionalMeasures");
            Arrays.sort(getCompositionalMeasures());
            for (ValueModel compositionalMeasure : getCompositionalMeasures()) {
                outputWriter.println(compositionalMeasure.formatValueAndOneSigmaABSForTesting());
            }
            outputWriter.println();

            // RadiogenicIsotopeRatios
            outputWriter.println("RadiogenicIsotopeRatios");
            Arrays.sort(getRadiogenicIsotopeRatios());
            for (ValueModel radiogenicIsotopeRatio : getRadiogenicIsotopeRatios()) {
                outputWriter.println(radiogenicIsotopeRatio.formatValueAndOneSigmaABSForTesting());
            }
            outputWriter.println();

            // RadiogenicIsotopeDates
            outputWriter.println("RadiogenicIsotopeDates");
            Arrays.sort(getRadiogenicIsotopeDates());
            for (ValueModel radiogenicIsotopeDate : getRadiogenicIsotopeDates()) {
                outputWriter.println(radiogenicIsotopeDate.formatValueAndOneSigmaABSForTesting());
            }
            outputWriter.println();

            outputWriter.close();

        } catch (IOException iOException) {
        }

        try {
            BrowserControl.displayURL(dataValuesFile.getCanonicalPath());
        } catch (IOException ex) {
        }

    }

    /**
     *
     */
    public void calculateTeraWasserburgRho() {
        // created to handle legacy data

        double rho = getRadiogenicIsotopeRatioByName("rhoR206_238r__r207_235r").getValue().doubleValue();
        if ((rho >= -1.0) && (rho <= 1.0)) {
            try {
                double[][] rawCovW = new double[2][2];
                rawCovW[0][0] = getRadiogenicIsotopeRatioByName("r206_238r").getOneSigmaAbs().pow(2).doubleValue();
                rawCovW[0][1] = getRadiogenicIsotopeRatioByName("rhoR206_238r__r207_235r").getValue().//
                        multiply(getRadiogenicIsotopeRatioByName("r206_238r").getOneSigmaAbs()).//
                        multiply(getRadiogenicIsotopeRatioByName("r207_235r").getOneSigmaAbs()).doubleValue();
                rawCovW[1][0] = rawCovW[0][1];
                rawCovW[1][1] = getRadiogenicIsotopeRatioByName("r207_235r").getOneSigmaAbs().pow(2).doubleValue();
                Matrix CovW = new Matrix(rawCovW);
//        System.out.println(">>>>  CovW");
//        CovW.print(10, 10);

                double[][] rawJ = new double[2][2];
                rawJ[0][0] = -1.0 / getRadiogenicIsotopeRatioByName("r206_238r").getValue().pow(2).doubleValue();
                rawJ[0][1] = 0.0;
                rawJ[1][0] = -1.0 //
                        * (getRadiogenicIsotopeRatioByName("r207_235r").getValue().//
                        divide(getRadiogenicIsotopeRatioByName("r206_238r").getValue().pow(2), ReduxConstants.mathContext15).//
                        // missing from legacy data as of oct 2010 divide( getAnalysisMeasure( AnalysisMeasures.r238_235s.getName() ).getValue(), ReduxConstants.mathContext15 ).doubleValue());
                        divide(new BigDecimal(137.88), ReduxConstants.mathContext15).doubleValue());
                rawJ[1][1] = BigDecimal.ONE.//
                        divide(getRadiogenicIsotopeRatioByName("r206_238r").getValue(), ReduxConstants.mathContext15).//
                        //divide( getAnalysisMeasure( AnalysisMeasures.r238_235s.getName() ).getValue(), ReduxConstants.mathContext15 ).doubleValue();
                        divide(new BigDecimal(137.88), ReduxConstants.mathContext15).doubleValue();
                Matrix J = new Matrix(rawJ);
//        System.out.println(">>>>  J");
//        J.print(10,10);

                Matrix CovTW = J.times(CovW).times(J.transpose());
//        System.out.println(">>>>  CovTW");
//        CovTW.print(10,10);

                getRadiogenicIsotopeRatioByName("rhoR207_206r__r238_206r").setValue( //
                        new BigDecimal(CovTW.get(0, 1) / Math.sqrt(CovTW.get(0, 0) * CovTW.get(1, 1))));
            } catch (Exception e) {
                getRadiogenicIsotopeRatioByName("rhoR207_206r__r238_206r").setValue(new BigDecimal(ReduxConstants.NO_RHO_FLAG));
            }
        } else {
            getRadiogenicIsotopeRatioByName("rhoR207_206r__r238_206r").setValue(new BigDecimal(ReduxConstants.NO_RHO_FLAG));
        }

    }

    /**
     *
     * @return
     */
    @Override
    /**
     * Added to handle text view in combo box
     */
    public String toString() {
        return getFractionID();
    }

    /**
     * @return the errorEllipsePath
     */
    @Override
    public Path2D getErrorEllipsePath() {
        return errorEllipsePath;
    }

    /**
     * @param errorEllipsePath the errorEllipsePath to set
     */
    @Override
    public void setErrorEllipsePath(Path2D errorEllipsePath) {
        this.errorEllipsePath = errorEllipsePath;
    }

    /**
     * @return the hasMeasuredLead
     */
    @Override
    public boolean hasMeasuredLead() {
        return //
                getMeasuredRatioByName(MeasuredRatios.r206_207m.getName()).hasPositiveValue();

    }

    /**
     * @param existsMeasuredLead
     */
    public void setHasMeasuredLead(boolean existsMeasuredLead) {
        this.hasMeasuredLead = existsMeasuredLead;
    }

    /**
     * @return the hasMeasuredUranium
     */
    @Override
    public boolean hasMeasuredUranium() {
        return //
                getMeasuredRatioByName("r206_238m").hasPositiveValue()
                || getMeasuredRatioByName("r208_232m").hasPositiveValue();

    }

    /**
     * @param existsMeasuredUranium
     */
    public void setHasMeasuredUranium(boolean existsMeasuredUranium) {
        this.hasMeasuredUranium = existsMeasuredUranium;
    }

    // feb 2010 these methods are placeholders due to use of
    // UpbFractionsI ... will disappear once legacy development is matured
    // TODO: needed for backward compatibility feb 2009 REMOVE by 2010
    /**
     *
     */
    @Override
    public void setSavedFractionationModels() {
        // do nothing for legacy
    }

    /**
     * @return the fractionNotes
     */
    @Override
    public String getFractionNotes() {
        if (fractionNotes == null) {
            fractionNotes = "";
        }
        return fractionNotes;
    }

    /**
     * @param fractionNotes the fractionNotes to set
     */
    @Override
    public void setFractionNotes(String fractionNotes) {
        this.fractionNotes = fractionNotes;
    }

    /**
     *
     * @return
     */
    @Override
    public AbstractRatiosDataModel getPbBlank() {
        return null;
    }

    /**
     *
     * @param pbBlank
     */
    @Override
    public void setPbBlank(AbstractRatiosDataModel pbBlank) {
    }

    /**
     *
     * @return
     */
    @Override
    public AbstractRatiosDataModel getTracer() {
        return null;
    }

    /**
     *
     * @param Tracer
     */
    @Override
    public void setTracer(AbstractRatiosDataModel Tracer) {
    }

    /**
     *
     * @return
     */
    @Override
    public ValueModel getAlphaPbModel() {
        return null;
    }

    /**
     *
     * @param alphaPbModel
     */
    @Override
    public void setAlphaPbModel(ValueModel alphaPbModel) {
    }

    /**
     *
     * @return
     */
    @Override
    public ValueModel getAlphaUModel() {
        return null;
    }

    /**
     *
     * @param alphaUModel
     */
    @Override
    public void setAlphaUModel(ValueModel alphaUModel) {
    }

    /**
     *
     * @param ratioName
     * @return
     */
    @Override
    public ValueModel getRadiogenicIsotopeDateWithTracerUnctByName(String ratioName) {
        return new ValueModel(ratioName,
                BigDecimal.ZERO,
                "ABS",
                BigDecimal.ZERO, BigDecimal.ZERO);
    }

    /**
     *
     * @param ratioName
     * @return
     */
    @Override
    public ValueModel getRadiogenicIsotopeDateWithAllUnctByName(String ratioName) {
        return new ValueModel(ratioName,
                BigDecimal.ZERO,
                "ABS",
                BigDecimal.ZERO, BigDecimal.ZERO);
    }

    /**
     * @return the ellipseTilt
     */
    @Override
    public double getEllipseRho() {
        return ellipseRho;
    }

    /**
     * @param ellipseRho
     */
    @Override
    public void setEllipseRho(double ellipseRho) {
        this.ellipseRho = ellipseRho;
    }

    /**
     * @return the filtered
     */
    @Override
    public boolean isFiltered() {
        return filtered;
    }

    /**
     * @param filtered the filtered to set
     */
    @Override
    public void setFiltered(boolean filtered) {
        this.filtered = filtered;
    }

    /**
     * @return the selectedInDataTable
     */
    @Override
    public boolean isSelectedInDataTable() {
        return selectedInDataTable;
    }

    /**
     * @param selectedInDataTable the selectedInDataTable to set
     */
    @Override
    public void setSelectedInDataTable(boolean selectedInDataTable) {
        this.selectedInDataTable = selectedInDataTable;
    }

    /**
     *
     */
    @Override
    public void toggleRejectedStatus() {
        this.rejected = !this.rejected;
    }

    /**
     * @param commonLeadCorrectionParameters the commonLeadCorrectionParameters
     * to set
     */
    public void setCommonLeadCorrectionParameters(SortedMap<String, ValueModel> commonLeadCorrectionParameters) {
        this.commonLeadCorrectionParameters = commonLeadCorrectionParameters;
    }

    /**
     * @param commonLeadLossCorrectionScheme the commonLeadLossCorrectionScheme
     * to set
     */
    public void setCommonLeadLossCorrectionScheme(AbstractCommonLeadLossCorrectionScheme commonLeadLossCorrectionScheme) {
        this.commonLeadLossCorrectionScheme = commonLeadLossCorrectionScheme;
    }

//    /**
//     * @return the errorEllipseNode
//     */
//    public org.cirdles.isoplot.chart.concordia.ErrorEllipse getErrorEllipseNode() {
//        return errorEllipseNode;
//    }
//
//    /**
//     * @param errorEllipseNode the errorEllipseNode to set
//     */
//    public void setErrorEllipseNode(org.cirdles.isoplot.chart.concordia.ErrorEllipse errorEllipseNode) {
//        this.errorEllipseNode = errorEllipseNode;
//    }
    /**
     * @return the staceyKramerCorrectionParameters
     */
    public SortedMap<String, BigDecimal> getStaceyKramerCorrectionParameters() {
        return staceyKramerCorrectionParameters;
    }

    /**
     * @param staceyKramerCorrectionParameters the
     * staceyKramerCorrectionParameters to set
     */
    public void setStaceyKramerCorrectionParameters(SortedMap<String, BigDecimal> staceyKramerCorrectionParameters) {
        this.staceyKramerCorrectionParameters = staceyKramerCorrectionParameters;
    }

    /**
     * @return the useStaceyKramer
     */
    public boolean isUseStaceyKramer() {
        return useStaceyKramer;
    }

    /**
     * @param useStaceyKramer the useStaceyKramer to set
     */
    public void setUseStaceyKramer(boolean useStaceyKramer) {
        this.useStaceyKramer = useStaceyKramer;
    }

    /**
     * @return the standard
     */
    @Override
    public boolean isStandard() {
        return standard;
    }

    /**
     * @param standard the standard to set
     */
    @Override
    public void setStandard(boolean standard) {
        this.standard = standard;
    }

    /**
     * @return the commonLeadLossCorrectionScheme
     */
    public AbstractCommonLeadLossCorrectionScheme getCommonLeadLossCorrectionScheme() {
        return commonLeadLossCorrectionScheme;
    }

    /**
     * @return the upperPhiMap
     */
    public SortedMap<String, Double> getUpperPhiMap() {
        return upperPhiMap;
    }

    public void initializeUpperPhiMap() {
        if (upperPhiMap == null) {
            upperPhiMap = new TreeMap<>();
        }
        upperPhiMap.put("upperPhi_r206_204", 0.0);
        upperPhiMap.put("upperPhi_r207_204", 0.0);
        upperPhiMap.put("upperPhi_r208_204", 0.0);
    }

    /**
     * @return the SfciTotal
     */
    public Matrix getSfciTotal() {
        return SfciTotal;
    }

    /**
     * @param SfciTotal
     */
    public void setSfciTotal(Matrix SfciTotal) {
        this.SfciTotal = SfciTotal;
    }

    /**
     * @return the SrAll
     */
    public Matrix getSrAll() {
        return SrAll;
    }

    /**
     * @param SrAll the SrAll to set
     */
    public void setSrAll(Matrix SrAll) {
        this.SrAll = SrAll;
    }

    /**
     * @return the commonLeadCorrectionParameters
     */
    public SortedMap<String, ValueModel> getCommonLeadCorrectionParameters() {
        return commonLeadCorrectionParameters;
    }

    /**
     * @return the SFc204
     */
    public Matrix getSFc204() {
        return SFc204;
    }

    /**
     * @param SFc204 the SFc204 to set
     */
    public void setSFc204(Matrix SFc204) {
        this.SFc204 = SFc204;
    }

    /**
     * @return the JPbccs
     */
    public Matrix getJPbccs() {
        return JPbccs;
    }

    /**
     * @param JPbccs the JPbccs to set
     */
    public void setJPbccs(Matrix JPbccs) {
        this.JPbccs = JPbccs;
    }

    public boolean hasCommonLeadLossCorrectionSchemeGreaterThanTypeA() {
        return ((!(commonLeadLossCorrectionScheme instanceof CommonLeadLossCorrectionSchemeNONE))//
                && //
                (!(commonLeadLossCorrectionScheme instanceof CommonLeadLossCorrectionSchemeA1))//
                && //
                (!(commonLeadLossCorrectionScheme instanceof CommonLeadLossCorrectionSchemeA2)));
    }

    @Override
    public boolean isCommonLeadLossCorrected() {
        return !(commonLeadLossCorrectionScheme instanceof CommonLeadLossCorrectionSchemeNONE);
    }

    /**
     * @return the radDateForSKSynch
     */
    public RadDatesForPbCorrSynchEnum getRadDateForSKSynch() {
        return radDateForSKSynch;
    }

    /**
     * @param radDateForSKSynch the radDateForSKSynch to set
     */
    public void setRadDateForSKSynch(RadDatesForPbCorrSynchEnum radDateForSKSynch) {
        this.radDateForSKSynch = radDateForSKSynch;
    }

    /**
     * @return the correctedForPbc
     */
    public boolean isCorrectedForPbc() {
        return correctedForPbc;
    }

    /**
     * @param correctedForPbc the correctedForPbc to set
     */
    public void setCorrectedForPbc(boolean correctedForPbc) {
        this.correctedForPbc = correctedForPbc;
    }

    @Override
    public boolean hasXMLUSourceFile() {
        return false;
    }

    @Override
    public boolean hasXMLPbSourceFile() {
        return false;
    }

    @Override
    public boolean isAnOxide() {
        return false;
    }
}
