/*
 * UPbFraction.java
 *
 * Created on April 22, 2007, 11:23 AM
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

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.awt.geom.Path2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.UPb_Redux.fractions.Fraction;
import org.earthtime.UPb_Redux.fractions.FractionI;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.fractionReduction.ReductionHandler;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.fractionReduction.UPbFractionReducer;
import org.earthtime.reduxLabData.ReduxLabData;
import org.earthtime.reportViews.ReportRowGUIInterface;
import org.earthtime.UPb_Redux.tracers.Tracer;
import org.earthtime.UPb_Redux.tracers.TracerXMLConverter;
import org.earthtime.UPb_Redux.user.UPbReduxConfigurator;
import org.earthtime.UPb_Redux.utilities.BrowserControl;
import org.earthtime.UPb_Redux.utilities.comparators.IntuitiveStringComparator;
import org.earthtime.UPb_Redux.valueModels.MeasuredRatioModel;
import org.earthtime.UPb_Redux.valueModels.MeasuredRatioModelXMLConverter;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.UPb_Redux.valueModels.ValueModelXMLConverter;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.MolU235s;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.MolU238s;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.R265_267m;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.R265_268m;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.R270_265m;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.R270_267m;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.R270_268m;
import org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException;
import org.earthtime.archivingTools.URIHelper;
import org.earthtime.dataDictionaries.AnalysisMeasures;
import org.earthtime.dataDictionaries.DataDictionary;
import org.earthtime.dataDictionaries.Lambdas;
import org.earthtime.dataDictionaries.MatrixSpecifications;
import org.earthtime.dataDictionaries.MeasuredRatios;
import org.earthtime.dataDictionaries.RadDates;
import org.earthtime.dataDictionaries.TracerUPbRatiosAndConcentrations;
import org.earthtime.exceptions.ETException;
import org.earthtime.fractions.ETFractionInterface;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataModels.physicalConstantsModels.PhysicalConstantsModel;
import org.earthtime.ratioDataModels.tracers.TracerUPbModel;
import org.earthtime.ratioDataModels.tracers.TracerUPbModelXMLConverter;
import org.earthtime.utilities.CollectionHelpers;
import org.earthtime.xmlUtilities.XMLSerializationI;

/**
 *
 * @author James F. Bowring
 */
public class UPbFraction extends Fraction implements
        FractionI,
        UPbFractionI,
        ETFractionInterface,
        ReportRowGUIInterface,
        Serializable,
        XMLSerializationI {

    // Class variables
    private static final long serialVersionUID = 8710141778033282061L;
    // **********  serialization customization notes
    // http://www.mactech.com/articles/mactech/Vol.14/14.04/JavaSerialization/index.html
    // ****************
    private static transient String XMLSchemaURL;

    // Instance variables
    // transient fields for uncertainty reduction
    // went static feb 2010
    private transient ReductionHandler reductionHandler;
    // aug 2010 refinement
    private transient boolean treatFractionAsZircon;
    // the CURRENT ReduxLabData are made available to any active fraction
    private transient ReduxLabData myLabData;
    // transient fields to store working results of data reductions
    private transient ValueModel[] radiogenicIsotopeDatesWithTracerUncertainty;
    private transient ValueModel[] radiogenicIsotopeDatesWithAllUncertainty;
    private transient ValueModel[] radiogenicIsotopeRatiosWithTracerUncertainty;
    private transient ValueModel[] radiogenicIsotopeRatiosWithAllUncertainty;
    // transient fields to store reduction calculation artifacts
    private transient ConcurrentMap<String, BigDecimal> parDerivTerms;
    private transient Map<String, BigDecimal> coVariances;
    private transient Path2D errorEllipsePath;
    private transient double ellipseRho;
    private transient boolean selectedInDataTable;
    // these fields are read in from xml UPbRedux files from Tripoli, for example
    // any of U, Pb, or UPb can be read in; 
    private String ratioType;
    private String pedigree;
    private BigDecimal meanAlphaU;  // if > 0 then U-fractionation correction occurred
    private BigDecimal meanAlphaPb; // if > 0 then Pb-fractionation correction occurred
    // used only for pre-processed corrections
    // this means that tripoli created and redux serialized versions of UPbFractions have this field
    // which is only used once to read in what Tripoli (or some xml-schema-compliant file) produces
    private BigDecimal r18O_16O;
    // april 2008 - these variables plus the already existing
    // r18O_16O will become analysis measures for analysis fraction
    // these variables are read in during import but not used during analysis
    // because their values are moved to analysisMeasures
    private BigDecimal labUBlankMass;
    private BigDecimal r238_235b;
    private BigDecimal r238_235s;
    private BigDecimal tracerMass;
    private AbstractRatiosDataModel tracer; // fraction class has tracerID
    // the following fields are added to an xml imported fraction
    private ValueModel alphaPbModel;
    private ValueModel alphaUModel;
    // these saved models allow for sensitivity view at kwiki tab and a reset to orig values
    private ValueModel alphaPbModelSaved;
    private ValueModel alphaUModelSaved;
    private AbstractRatiosDataModel pbBlank; // fraction class has PbBlankID
    private AbstractRatiosDataModel physicalConstantsModel; // fraction class has physicalConstantsModelID
    private int aliquotNumber;
    private String pedigreePb;
    private String pedigreeU;
    private boolean changed;
    private boolean deleted;
    private String sourceFilePb;
    private String sourceFileU;
    // added nov 2009 as part of live update workflow manager development
    private String fractionNotes;
    // added march 2008 to store fraction selections in persistent state
    private boolean rejected;
    // added march 2009 to preserve outputs for kwiki page
    private ValueModel[] outputs;
    private boolean inAutoUraniumMode;
    private BigDecimal inputDate206_238r;
    private ValueModel inputAlphaU;
    private BigDecimal inputOneSigmaPct;
    // added july 2010 for detrital filtering
    private boolean filtered;
    // isValidOrAirplaneMode add on for noah sept 2010
    private boolean standard;
    /**
     *
     */
    public transient SortedMap<Integer, ValueModel> variablesInOrder;
    /**
     *
     */
    public transient SortedMap<Integer, ValueModel> specialInputVariablesInOrder;

    /**
     *
     */
    public UPbFraction() {
        super(ReduxConstants.DEFAULT_OBJECT_NAME, ReduxConstants.DEFAULT_OBJECT_NAME);

        // transient fields
        this.myLabData = ReduxLabData.getInstance();
        this.reductionHandler = null;
        this.parDerivTerms = new ConcurrentHashMap<>();
        this.coVariances = new HashMap<>();
        this.radiogenicIsotopeDatesWithTracerUncertainty = new ValueModel[0];
        this.radiogenicIsotopeDatesWithAllUncertainty = new ValueModel[0];
        this.radiogenicIsotopeRatiosWithTracerUncertainty = new ValueModel[0];
        this.radiogenicIsotopeRatiosWithAllUncertainty = new ValueModel[0];

        setLegacy(false);

        this.ratioType = "UPb";
        this.pedigree = "NONE";

        this.meanAlphaU = BigDecimal.ZERO;
        this.meanAlphaPb = BigDecimal.ZERO;
        this.r18O_16O = BigDecimal.ZERO;
        this.labUBlankMass = BigDecimal.ZERO;
        this.r238_235b = BigDecimal.ZERO;
        this.r238_235s = BigDecimal.ZERO;
        this.tracerMass = BigDecimal.ZERO;

        try {
            // aug 2011 changed to none tracer since we are now sending tracer info from tripoli with either u or pb
            this.tracer = myLabData.getNoneTracer();

            // modified aug 2010
            this.alphaPbModel = myLabData.getNoneAlphaPbModel();//.getDefaultLabAlphaPbModel();
            // modified aug 2010
            this.alphaUModel = myLabData.getNoneAlphaUModel();//.getDefaultLabAlphaUModel();

            this.inputAlphaU = alphaUModel.copy();
            this.pbBlank = myLabData.getDefaultLabPbBlank();

            // Initial Pb Model is used by UPbFraction but stored in parent class
            // needs to be copy because of stacey-kramers
            setInitialPbModel(myLabData.getFirstInitialPbModel());//.getDefaultLabInitialPbModel().copy() );

        } catch (BadLabDataException badLabDataException) {
        }

        this.physicalConstantsModel = PhysicalConstantsModel.getEARTHTIMEPhysicalConstantsModel();

        this.aliquotNumber = 1;
        this.pedigreePb = "";
        this.pedigreeU = "";
        this.changed = true;
        this.deleted = false;

        this.sourceFilePb = "NONE";
        this.sourceFileU = "NONE";

        this.fractionNotes = "";

        this.rejected = false;

        this.filtered = false;
        this.standard = false;

        // set parent fields
        setImageURL("");

        getAnalysisMeasure(AnalysisMeasures.pbBlankMassInGrams.getName())//
                .copyValuesFrom(myLabData.getDefaultPbBlankMassInGrams());
        getAnalysisMeasure(AnalysisMeasures.uBlankMassInGrams.getName())//
                .copyValuesFrom(myLabData.getDefaultAssumedUBlankMassInGrams());
        getAnalysisMeasure(AnalysisMeasures.tracerMassInGrams.getName())//
                .copyValuesFrom(myLabData.getDefaultTracerMass());
        getAnalysisMeasure(AnalysisMeasures.r238_235b.getName())//
                .copyValuesFrom(myLabData.getDefaultR238_235b());
        getAnalysisMeasure(AnalysisMeasures.r238_235s.getName())//
                .copyValuesFrom(myLabData.getDefaultR238_235s());

        ValueModel r18O_16OZeroWithZeroUnct = //
                new ValueModel("tempZero", BigDecimal.ZERO, "ABS", BigDecimal.ZERO, BigDecimal.ZERO);
        getAnalysisMeasure(AnalysisMeasures.r18O_16O.getName())//
                .copyValuesFrom(r18O_16OZeroWithZeroUnct);
        getAnalysisMeasure(AnalysisMeasures.r18O_16O_revised.getName())//
                .copyValuesFrom(r18O_16OZeroWithZeroUnct);

        getAnalysisMeasure(AnalysisMeasures.rTh_Umagma.getName())//
                .copyValuesFrom(myLabData.getDefaultRTh_Umagma());

        getAnalysisMeasure(AnalysisMeasures.ar231_235sample.getName())//
                .copyValuesFrom(myLabData.getDefaultAr231_235sample());

        this.outputs = new ValueModel[DataDictionary.uPbReduxOutputNames.length];
        for (int i = 0; i < DataDictionary.uPbReduxOutputNames.length; i++) {
            outputs[i]
                    = new ValueModel(DataDictionary.uPbReduxOutputNames[i],
                            BigDecimal.ZERO,
                            "ABS",
                            BigDecimal.ZERO, BigDecimal.ZERO);
        }

        setStaceyKramersOnePctUnct(myLabData.getDefaultStaceyKramersOnePctUnct());
        setStaceyKramersCorrelationCoeffs(myLabData.getDefaultStaceyKramersCorrelationCoeffs());
        this.inAutoUraniumMode = false;

        this.inputOneSigmaPct = BigDecimal.ZERO;
        this.inputDate206_238r = getRadiogenicIsotopeDateByName("age206_207r").getValue();

        treatFractionAsZircon = isZircon();

        selectedInDataTable = false;

    }

    /**
     * Creates a new instance of UPbFraction with default attributes.
     * UPbFraction is used to receive incoming fraction data and to save
     * fraction data via xml serialization. UPbFraction is also used as the
     * analysis framework, but is not propagated directly to the Aliquot.
     *
     * @param fractionID
     */
    public UPbFraction(String fractionID) {
        this();
        this.setFractionID(fractionID);
    }

    /**
     * Creates a new UPbFraction from an imported AnalysisFraction. The imported
     * fraction cannot be changed, only manipulated.
     *
     * @param aliquotNum
     * @param fraction
     * @param labData
     * @param tracer
     * @param pbBlank
     * @throws org.earthtime.UPb_Redux.exceptions.BadLabDataException
     */
    public UPbFraction(
            int aliquotNum,
            FractionI fraction,
            ReduxLabData labData,
            AbstractRatiosDataModel tracer,
            AbstractRatiosDataModel pbBlank) throws BadLabDataException {

        this();

        // custom settings
        this.aliquotNumber = aliquotNum;
        this.myLabData = labData;
        this.tracer = tracer;
        this.pbBlank = pbBlank;
        this.changed = false;

        // Fraction fields
        this.setSampleName(fraction.getSampleName());
        this.setFractionID(fraction.getFractionID());
        this.setGrainID(fraction.getFractionID());

        this.getValuesFrom(fraction, true);

        this.setMeasuredRatios((MeasuredRatioModel[]) fraction.copyMeasuredRatios());

        treatFractionAsZircon = isZircon();

    }

    /**
     *
     * @return
     */
    public boolean initializeReductionHandler() {

        // treatFractionAsZircon is set by fraction reducer
        String matrixSpecsName = "EMPTY";
        String tracerType = "";

        if ((getTracer() != null)
                && (hasMeasuredLead())) {
            tracerType = getTracerType();
            if (tracerType.equalsIgnoreCase("mixed 205-233-235")) {
                if (!isFractionationCorrectedU()) {//     getMeanAlphaU().compareTo( BigDecimal.ZERO ) == 0 ) {
                    // NotFcU **************************************************
                    if (treatFractionAsZircon) {//isZircon() ) {
                        matrixSpecsName = "mixed_205_233_235_Zircon_NotFcU";
                    } else {
                        matrixSpecsName = "mixed_205_233_235_NotZircon_NotFcU";
                    }
                } else {
                    // FcU *****************************************************
                }
            } else if (tracerType.equalsIgnoreCase("mixed 205-235")) {
                if (treatFractionAsZircon) {//isZircon() ) {
                    matrixSpecsName = "mixed_205_235_Zircon";
                } else {
                    matrixSpecsName = "mixed_205_235_NotZircon";
                }
            } else if (tracerType.equalsIgnoreCase("mixed 202-205-233-235")) {
                if (!isFractionationCorrectedU()) {//getMeanAlphaU().compareTo( BigDecimal.ZERO ) == 0 ) {
                    // NotFcU **************************************************
                    if ((!isFractionationCorrectedPb())//     getMeanAlphaPb().compareTo( BigDecimal.ZERO ) == 0)
                            && (hasMeasured202_205())) {//
                        //getValue().compareTo( BigDecimal.ZERO ) != 0) ) {
                        // NotFcPb *********************************************
                        if (treatFractionAsZircon) {//note email Sept 19 2010 Noah re: set alphaPb to zero for this temporarily for reductions
                            matrixSpecsName = "mixed_202_205_233_235_Zircon_NotFcU_NotFcPb";
                        } else {
                        }
                    } else {
                        // FcPb
                        if (treatFractionAsZircon) {//isZircon() ) {
                            matrixSpecsName = "mixed_202_205_233_235_Zircon_NotFcU_FcPb";
                        } else {
                        }
                    }
                } else {
                    // FcU *****************************************************
                }
            } else if (tracerType.equalsIgnoreCase("mixed 205-233-236")) {
                if (!isFractionationCorrectedU()) {
                    // NotFcU **************************************************
                    if (treatFractionAsZircon) {
                        matrixSpecsName = "mixed_205_233_236_Zircon_NotFcU";
                    } else {
                        matrixSpecsName = "mixed_205_233_236_NotZircon_NotFcU";
                    }
                } else {
                    // FcU *****************************************************
                }
            } else if (tracerType.equalsIgnoreCase("mixed 202-205-233-236")) {
            }
        }

        if (matrixSpecsName.equalsIgnoreCase("EMPTY")//        (matrixSpecs[0][0].compareToIgnoreCase( "EMPTY" ) == 0)
                || (!getAnalysisMeasure(AnalysisMeasures.tracerMassInGrams.getName()).hasPositiveValue())
                || (!getMeasuredRatioByName(MeasuredRatios.r204_205m.getName()).hasPositiveValue())
                && (!getMeasuredRatioByName(MeasuredRatios.r206_205m.getName()).hasPositiveValue())
                && (!getMeasuredRatioByName(MeasuredRatios.r207_205m.getName()).hasPositiveValue())
                && (!getMeasuredRatioByName(MeasuredRatios.r208_205m.getName()).hasPositiveValue())) {
            setReductionHandler(null);
        } else {
            setReductionHandler(//
                    new ReductionHandler(this, matrixSpecsName));

            if (tracerType.trim().contains("235")) {
                // *************FOR  TRACER  233_235 **********************
                // added march 2009 to prepare for recalculate oxide correction
                ValueModel r270_267m = new R270_267m();
                setAnalysisMeasureByName(AnalysisMeasures.r270_267m.getName(), r270_267m);
                r270_267m.calculateValue(
                        new ValueModel[]{
                            getMeasuredRatioByName(MeasuredRatios.r238_235m.getName()),
                            getAnalysisMeasure(AnalysisMeasures.r18O_16O.getName()),
                            getMeasuredRatioByName(MeasuredRatios.r233_235m.getName())},
                        null);

                ((R270_267m) r270_267m).calculateOneSigma(false,
                        false,
                        null,
                        null);

                ValueModel r265_267m = new R265_267m();
                setAnalysisMeasureByName(AnalysisMeasures.r265_267m.getName(), r265_267m);
                r265_267m.calculateValue(
                        new ValueModel[]{
                            getAnalysisMeasure(AnalysisMeasures.r18O_16O.getName()),
                            getMeasuredRatioByName(MeasuredRatios.r233_235m.getName())},
                        null);

                ((R265_267m) r265_267m).calculateOneSigma(false,
                        false,
                        null,
                        null);

                // nov 2009 moved here from reducer to facilitate logic of providing for
                // re-oxide correction and old/new r18O_16O
                ValueModel r270_265m = new R270_265m("235");
                setAnalysisMeasureByName(AnalysisMeasures.r270_265m.getName(), r270_265m);
                r270_265m.calculateValue(
                        new ValueModel[]{
                            getMeasuredRatioByName(MeasuredRatios.r238_233m.getName())},
                        null);

                ((R270_265m) r270_265m).calculateOneSigma(false,
                        false,
                        null,
                        null);

            } else if (tracerType.trim().contains("236")) {
                // ADDED (by copy from above) Jan 2011
                // *************FOR  TRACER  233_236 **********************
                // added march 2009 to prepare for recalculate oxide correction
                ValueModel r270_268m = new R270_268m();
                setAnalysisMeasureByName(AnalysisMeasures.r270_268m.getName(), r270_268m);
                r270_268m.calculateValue(
                        new ValueModel[]{
                            getMeasuredRatioByName(MeasuredRatios.r238_236m.getName()),
                            getAnalysisMeasure(AnalysisMeasures.r18O_16O.getName())},
                        null);

                ((R270_268m) r270_268m).calculateOneSigma(false,
                        false,
                        null,
                        null);

                ValueModel r265_268m = new R265_268m();
                setAnalysisMeasureByName(AnalysisMeasures.r265_268m.getName(), r265_268m);
                r265_268m.calculateValue(
                        new ValueModel[]{
                            getMeasuredRatioByName(MeasuredRatios.r233_236m.getName())},
                        null);

                ((R265_268m) r265_268m).calculateOneSigma(false,
                        false,
                        null,
                        null);

                // nov 2009 moved here from reducer to facilitate logic of providing for
                // re-oxide correction and old/new r18O_16O
                ValueModel r270_265m = new R270_265m("236");
                setAnalysisMeasureByName(AnalysisMeasures.r270_265m.getName(), r270_265m);
                r270_265m.calculateValue(
                        new ValueModel[]{
                            getMeasuredRatioByName(MeasuredRatios.r238_233m.getName()),
                            getAnalysisMeasure(AnalysisMeasures.r18O_16O.getName())},
                        null);

                ((R270_265m) r270_265m).calculateOneSigma(false,
                        false,
                        null,
                        null);

            }

            // nov 2009 initialize revised oxide correction for legacy files
            if (getAnalysisMeasure(AnalysisMeasures.r18O_16O_revised.getName()).getValue().compareTo(BigDecimal.ZERO) == 0) {
                ValueModel revisedOxide = getAnalysisMeasure(AnalysisMeasures.r18O_16O.getName()).copy();
                revisedOxide.setName(AnalysisMeasures.r18O_16O_revised.getName());
                setAnalysisMeasureByName(AnalysisMeasures.r18O_16O_revised.getName(), revisedOxide);
            }

            // March 2009 bring use of model here for sensitivity use - prevents resetting of model
            if ((!isFractionationCorrectedPb())//     getMeanAlphaPb().compareTo( BigDecimal.ZERO ) == 0)
                    && (!hasMeasured202_205())) {//
                //getValue().compareTo( BigDecimal.ZERO ) == 0) ) {

                ValueModel alphaPb = getAlphaPbModel().copy();
                alphaPb.setName(AnalysisMeasures.alphaPb.getName());
                setAnalysisMeasureByName(AnalysisMeasures.alphaPb.getName(), alphaPb);
            }

            // March 2009 bring use of model here for sensitivity use - prevents resetting of model
            ValueModel alphaU = null;
            if (isInAutoUraniumMode()) {
                alphaU = getInputAlphaU().copy();
            } else {
                // modified march 2011
                if (needsAlphaUModel()) {
                    alphaU = getAlphaUModel().copy();
                }
            }
            // modified march 2011
            if (alphaU != null) {
                alphaU.setName(AnalysisMeasures.alphaU.getName());
                setAnalysisMeasureByName(AnalysisMeasures.alphaU.getName(), alphaU);
            }

        }

        return (reductionHandler != null);
    }

    /**
     *
     * @param calculateCovariances
     */
    public void reduceData(boolean calculateCovariances) {
        UPbFractionReducer.getInstance().fullFractionReduce(this, calculateCovariances);
    }

    /**
     *
     */
    public void autoGenerateMeasuredUranium() {
//TODO: parameterize
        BigDecimal oneSigmaPct = getInputOneSigmaPct();

        ValueModel molU238s = new MolU238s();
        ValueModel molU235s = new MolU235s();

        ((MolU238s) molU238s).EstimateValue(//
                getOutputsByName("molPb206r"),
                new ValueModel("age207_206", getInputDate206_238r(), "ABS", BigDecimal.ZERO, BigDecimal.ZERO),
                getPhysicalConstantsModel().getDatumByName(Lambdas.lambda238.getName()));

        molU238s.setOneSigma(ValueModel.convertOneSigmaPctToAbsIfRequired(molU238s, oneSigmaPct));
        setOutputByName("molU238s", molU238s);

        ((MolU235s) molU235s).EstimateValue(//
                molU238s,
                getAnalysisMeasure(AnalysisMeasures.r238_235s.getName()));

        molU235s.setOneSigma(ValueModel.convertOneSigmaPctToAbsIfRequired(molU235s, oneSigmaPct));
        setOutputByName("molU235s", molU235s);

        try {
            BigDecimal calcR238_235m = //
                    BigDecimal.ONE.//
                    divide(BigDecimal.ONE.//
                            add(new BigDecimal(3.0).//
                                    multiply(getInputAlphaU().getValue())), ReduxConstants.mathContext15).//

                    multiply(getOutputsByName("molU238b").getValue().//
                            add(getOutputsByName("molU238t").getValue().//
                                    add(getAnalysisMeasure(AnalysisMeasures.r238_235s.getName()).getValue().//
                                            multiply(molU235s.getValue())))).//

                    divide(molU235s.getValue().//
                            add(getOutputsByName("molU235b").getValue().//
                                    add(getOutputsByName("molU235t").getValue())), ReduxConstants.mathContext15);

            getMeasuredRatioByName(MeasuredRatios.r238_235m.getName()).setValue(calcR238_235m);
            getMeasuredRatioByName(MeasuredRatios.r238_235m.getName()).setOneSigma(oneSigmaPct);
        } catch (Exception e) {
        }

        if (getTracerType().equalsIgnoreCase("mixed 205-233-236")
                || getTracerType().equalsIgnoreCase("mixed 202-205-233-236")) {
            // calculate r233_236m
            try {
                BigDecimal calcR233_236m = //
                        getTracer().getDatumByName(TracerUPbRatiosAndConcentrations.r233_236t.getName()).getValue().//
                        divide(BigDecimal.ONE.//
                                subtract(new BigDecimal(3.0).//
                                        multiply(getInputAlphaU().getValue())), ReduxConstants.mathContext15);

                getMeasuredRatioByName(MeasuredRatios.r233_236m.getName()).setValue(calcR233_236m);
                getMeasuredRatioByName(MeasuredRatios.r233_236m.getName()).setOneSigma(oneSigmaPct);
            } catch (Exception e) {
            }

        } else if (getTracerType().equalsIgnoreCase("mixed 205-233-235")
                || getTracerType().equalsIgnoreCase("mixed 202-205-233-235")
                || getTracerType().equalsIgnoreCase("mixed 205-233-235-230Th")) {

            try {
                BigDecimal calcR233_235m = //
                        getOutputsByName("molU233t").getValue().//
                        divide(BigDecimal.ONE.//
                                subtract(new BigDecimal(2.0).//
                                        multiply(getInputAlphaU().getValue())), ReduxConstants.mathContext15).//

                        divide(molU235s.getValue().//
                                add(getOutputsByName("molU235b").getValue().//
                                        add(getOutputsByName("molU235t").getValue())), ReduxConstants.mathContext15);

                getMeasuredRatioByName(MeasuredRatios.r233_235m.getName()).setValue(calcR233_235m);
                getMeasuredRatioByName(MeasuredRatios.r233_235m.getName()).setOneSigma(oneSigmaPct);
            } catch (Exception e) {
            }

        }


        setInAutoUraniumMode( true );
 
    }

    /**
     *
     */
    public void zeroUraniumRatios() {
        getMeasuredRatioByName(MeasuredRatios.r238_235m.getName()).setValue(BigDecimal.ZERO);
        getMeasuredRatioByName(MeasuredRatios.r238_235m.getName()).setOneSigma(ReduxConstants.NO_RHO_FLAG);//   BigDecimal.ZERO);

        getMeasuredRatioByName(MeasuredRatios.r233_235m.getName()).setValue(BigDecimal.ZERO);
        getMeasuredRatioByName(MeasuredRatios.r233_235m.getName()).setOneSigma(ReduxConstants.NO_RHO_FLAG);//   BigDecimal.ZERO);

        getMeasuredRatioByName(MeasuredRatios.r238_233m.getName()).setValue(BigDecimal.ZERO);
        getMeasuredRatioByName(MeasuredRatios.r238_233m.getName()).setOneSigma(ReduxConstants.NO_RHO_FLAG);//   BigDecimal.ZERO);

        getMeasuredRatioByName(MeasuredRatios.r233_236m.getName()).setValue(BigDecimal.ZERO);
        getMeasuredRatioByName(MeasuredRatios.r233_236m.getName()).setOneSigma(ReduxConstants.NO_RHO_FLAG);//   BigDecimal.ZERO);

        setInAutoUraniumMode(false);
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
        String uPbFractionID = fraction.getFractionID();
        String uPbFractionAliquotNum = String.valueOf(fraction.getAliquotNumber());
        String myID = (uPbFractionAliquotNum + "." + uPbFractionID).toUpperCase();

        Comparator<String> forNoah = new IntuitiveStringComparator<>();

        return forNoah.compare((String.valueOf(this.getAliquotNumber()) + "." + this.getFractionID()).toUpperCase(), myID);
    }

    /**
     *
     * @param fractionFromFile
     * @param overrideData
     * @return
     * @throws BadLabDataException
     * @throws ETException
     */
    public boolean updateUPbFraction(FractionI fractionFromFile, boolean overrideData)
            throws BadLabDataException, ETException {
        // walk the new fraction being imported into an existing fraction
        // and check whether to override

        boolean didUpdate = false;

        // april 2008 check for conflicting tracers between u and pb
        // AUG 2011 update to use noneTracer
        if (!getRatioType().equalsIgnoreCase(((UPbFraction) fractionFromFile).getRatioType())
                && (!((UPbFraction) this).getTracer().equals(getMyLabData().getNoneTracer()))
                && (!((UPbFraction) fractionFromFile).getTracer().equals(getMyLabData().getNoneTracer()))) {
            if (!((UPbFraction) this).getTracer().getNameAndVersion().//
                    equalsIgnoreCase(((UPbFraction) fractionFromFile).getTracer().getNameAndVersion())) {
                throw new ETException(null,
                        "Tracer in imported fraction " + fractionFromFile.getFractionID() + " conflicts with existing fraction's tracer.");
            }
        }

        // update each ratio
        for (int r = 0; r < fractionFromFile.getMeasuredRatios().length; r++) {
            MeasuredRatioModel tempR = (MeasuredRatioModel) fractionFromFile.getMeasuredRatios()[r];
            MeasuredRatioModel knownR = (MeasuredRatioModel) getMeasuredRatioByName(tempR.getName());

            // determine whether to overwrite the existing known ratio
            if (((tempR.hasPositiveValue()) && overrideData)) {
                knownR.copyValuesFrom(tempR);

                didUpdate = true;
            }

        }

        // update ratio type and other fields
        if (didUpdate) {
            // detect that u and pb are now together as they are the only options at this point
            if (!getRatioType().equalsIgnoreCase(((UPbFraction) fractionFromFile).getRatioType())) {
                setRatioType("UPb");
            }

            // handle incoming uranium fraction
            if (((UPbFraction) fractionFromFile).getRatioType().contains("U")) {
                setSourceFileU(((UPbFraction) fractionFromFile).getSourceFileU());
                setPedigreeU(((UPbFraction) fractionFromFile).getPedigreeU());
                setMeanAlphaU(((UPbFraction) fractionFromFile).getMeanAlphaU());

                populateAnalysisMeasuresFromImportedFraction(fractionFromFile, this);

            }

            //handle incoming pb fraction
            if (((UPbFraction) fractionFromFile).getRatioType().contains("Pb")) {
                setSourceFilePb(((UPbFraction) fractionFromFile).getSourceFilePb());
                setPedigreePb(((UPbFraction) fractionFromFile).getPedigreePb());
                setMeanAlphaPb(((UPbFraction) fractionFromFile).getMeanAlphaPb()); // future feature

            }
        }

        try {
            // check for importing tracer (checked above for conflict)
            // rewritten march 2009 to handle auto update
            // update AUG 2011 to use noneTracer
            if (((getTracer().equals(getMyLabData().getNoneTracer())) && overrideData)) {
                if (((UPbFraction) fractionFromFile).getTracer().equals(getMyLabData().getNoneTracer())) {
                    setTracer(getMyLabData().getDefaultLabTracer());
                } else {
                    setTracer(((UPbFraction) fractionFromFile).getTracer());
                }
                didUpdate = true;
            }

        } catch (BadLabDataException ex) {
        }

        getMyLabData().registerTracer(getTracer(), false);

        // added Sept 2012 - Matt Rioux wanted to make sure reduction happended on live update
        UPbFractionReducer.getInstance().fullFractionReduce(this, true);

        return didUpdate;
    }

    /**
     *
     * @return
     */
    @Override
    public Object[] getFractionTableRowData() {
        String tracerName = "N/A";
        if (getTracer() != null) {
            tracerName = getTracer().getNameAndVersion();
        }

        Object[] retval = {
            String.valueOf(getAliquotNumber()), // for aliquot button
            Boolean.valueOf(!isRejected()), // oct 2009 for fraction selector where SELECTED = NOT rejected
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

        if (((MeasuredRatioModel) getMeasuredRatioByName(measuredRatio)).getValue().compareTo(BigDecimal.ZERO) != 0) {
            retVal = ((MeasuredRatioModel) getMeasuredRatioByName(measuredRatio)).toTableFormat();
        }

        return retVal;
    }

    /**
     *
     * @param trName
     * @return
     */
    public ValueModel getTracerRatioByName(String trName) {
        return getTracer().getDatumByName(trName);
    }

    /**
     *
     * @param pbrName
     * @return
     */
    public ValueModel getPbBlankRatioByName(String pbrName) {
        return getPbBlank().getDatumByName(pbrName);
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
    public AbstractRatiosDataModel getTracer() {
        return tracer;
    }

    /**
     *
     * @return
     */
    public String getTracerType() {
        return ((TracerUPbModel) tracer).getTracerType();
    }

    /**
     *
     * @param Tracer
     */
    @Override
    public void setTracer(AbstractRatiosDataModel Tracer) {
        this.tracer = Tracer;
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
    public String getSourceFilePb() {
        return sourceFilePb;
    }

    /**
     *
     * @param sourceFilePb
     */
    public void setSourceFilePb(String sourceFilePb) {
        this.sourceFilePb = sourceFilePb;
    }

    /**
     *
     * @return
     */
    public String getSourceFileU() {
        return sourceFileU;
    }

    /**
     *
     * @param sourceFileU
     */
    public void setSourceFileU(String sourceFileU) {
        this.sourceFileU = sourceFileU;
    }

    /**
     *
     * @return
     */
    public String getPedigree() {
        return pedigree;
    }

    /**
     *
     * @param Pedigree
     */
    public void setPedigree(String Pedigree) {
        this.pedigree = Pedigree;
    }

    /**
     *
     * @return
     */
    public String getPedigreePb() {
        return pedigreePb;
    }

    /**
     *
     * @param pedigreePb
     */
    public void setPedigreePb(String pedigreePb) {
        this.pedigreePb = pedigreePb;
    }

    /**
     *
     * @return
     */
    public String getPedigreeU() {
        return pedigreeU;
    }

    /**
     *
     * @param pedigreeU
     */
    public void setPedigreeU(String pedigreeU) {
        this.pedigreeU = pedigreeU;
    }

    /**
     *
     * @return
     */
    public ReduxLabData getMyLabData() {
        if (myLabData == null) {
            myLabData = ReduxLabData.getInstance();
        }
        return myLabData;
    }

    /**
     *
     * @param myLabData
     */
    public void setMyLabData(ReduxLabData myLabData) {
        this.myLabData = myLabData;
    }

    /**
     *
     * @return
     */
    @Override
    public String getTracerID() {
        if (getTracer() == null) {
            return ReduxConstants.NONE;
        } else {
            return getTracer().getNameAndVersion();
        }
    }

    /**
     *
     * @return
     */
    public boolean hasATracer() {
        if (getTracer() == null) {
            return false;
        } else {
            return !(getTracer().equals(TracerUPbModel.getNoneInstance()));//  .getTracerName().equalsIgnoreCase( "<none>" ) || getTracer().getTracerName().equalsIgnoreCase( ReduxConstants.NONE ));
        }
    }

    /**
     *
     * @return
     */
    @Override
    public String getAlphaPbModelID() {
        return getAlphaPbModel().getName();
    }

    /**
     *
     * @return
     */
    @Override
    public String getAlphaUModelID() {
        return getAlphaUModel().getName();
    }

    /**
     *
     * @return
     */
    @Override
    public String getPbBlankID() {
        return getPbBlank().getNameAndVersion();
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
    public BigDecimal getMeanAlphaU() {
        return meanAlphaU;
    }

    /**
     *
     * @param MeanAlphaU
     */
    public void setMeanAlphaU(BigDecimal MeanAlphaU) {
        this.meanAlphaU = MeanAlphaU;
    }

    /**
     *
     * @return
     */
    public BigDecimal getMeanAlphaPb() {
        return meanAlphaPb;
    }

    /**
     *
     * @param MeanAlphaPb
     */
    public void setMeanAlphaPb(BigDecimal MeanAlphaPb) {
        this.meanAlphaPb = MeanAlphaPb;
    }

    /**
     *
     * @return
     */
    @Override
    public AbstractRatiosDataModel getPbBlank() {
        return pbBlank;
    }

    /**
     *
     * @param pbBlank
     */
    @Override
    public void setPbBlank(AbstractRatiosDataModel pbBlank) {
        this.pbBlank = pbBlank;
    }

    /**
     *
     * @return
     */
    @Override
    public AbstractRatiosDataModel getPhysicalConstantsModel() {
        if (physicalConstantsModel == null) {
            try {
                physicalConstantsModel = getMyLabData().getDefaultPhysicalConstantsModel();
            } catch (BadLabDataException badLabDataException) {
            }
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
    public ValueModel getAlphaPbModel() {
        return alphaPbModel;
    }

    /**
     *
     * @param alphaPbModel
     */
    @Override
    public void setAlphaPbModel(ValueModel alphaPbModel) {
        if ((this.alphaPbModel == null)
                || (!this.alphaPbModel.equals(alphaPbModel))) {
            this.alphaPbModel = alphaPbModel;
            this.alphaPbModelSaved = alphaPbModel.copy();
            this.setChanged(true);
            System.out.println(this.getFractionID() //
                    + "  is getting new alphaPbModel = "//
                    + alphaPbModel.getName());
        }
    }

    /**
     *
     * @return
     */
    public boolean needsAlphaPbModel() {
        return (//hasMeasuredLead()//
                //&&//
                (!isFractionationCorrectedPb())//
                &&//
                (!hasMeasured202_205())//
                &&//
                (!hasDoublePbSpikeTracer()));
    }

    // this method used for temporary changes in fastfraction tab
    /**
     *
     * @param tracerType
     * @return
     */
    public boolean needsAlphaPbModel(String tracerType) {

        boolean retVal = //
                (!isFractionationCorrectedPb())//
                &&//
                (!hasMeasured202_205())//
                &&//
                (!tracerType.contains("202"));

        return retVal;
    }

    /**
     *
     * @return
     */
    public boolean hasMeasured202_205() {
        return getMeasuredRatioByName(MeasuredRatios.r202_205m.getName()).hasPositiveValue();
    }

    /**
     *
     * @return
     */
    public boolean hasDoublePbSpikeTracer() {
        boolean retVal = false;

        try {
            String tracerType
                    = ((TracerUPbModel) getMyLabData().//
                    getATracerModel(getTracerID())).getTracerType();

            retVal = tracerType.contains("202");

        } catch (BadLabDataException badLabDataException) {
        }
        return retVal;
    }

    /**
     *
     * @return
     */
    @Override
    public ValueModel getAlphaUModel() {
        return alphaUModel;
    }

    /**
     *
     * @param alphaUModel
     */
    @Override
    public void setAlphaUModel(ValueModel alphaUModel) {
        if ((this.alphaUModel == null)
                || (!this.alphaUModel.equals(alphaUModel))) {
            this.alphaUModel = alphaUModel;
            this.alphaUModelSaved = alphaUModel;
            this.setChanged(true);
            System.out.println(this.getFractionID() //
                    + "  is getting new alphaUModel = "//
                    + alphaUModel.getName());
        }
    }

    /**
     *
     * @return
     */
    public boolean needsAlphaUModel() {

        // sep 2010 made parallel construct to alphaPb
        boolean retVal =//
                (!isFractionationCorrectedU())//
                &&//
                (!hasDoubleUSpikeTracer());

        return retVal;
    }

    // this method used for temporary changes in fastfraction tab
    /**
     *
     * @param tracerType
     * @return
     */
    public boolean needsAlphaUModel(String tracerType) {

        boolean retVal = //
                (!isFractionationCorrectedU())//
                &&//
                (!tracerType.contains("233"));

        return retVal;
    }

    /**
     *
     * @return
     */
    public boolean hasDoubleUSpikeTracer() {
        boolean retVal = false;

        try {
            String tracerType
                    = ((TracerUPbModel) getMyLabData().//
                    getATracerModel(getTracerID())).getTracerType();

            retVal = tracerType.contains("233");

        } catch (BadLabDataException badLabDataException) {
        }
        return retVal;
    }

    // XML Serialization *******************************************************
    /**
     *
     */
    private void setClassXMLSchemaURL() {
        UPbReduxConfigurator myConfigurator = new UPbReduxConfigurator();

        XMLSchemaURL
                = myConfigurator.getResourceURI("URI_UPbReduxFractionXMLSchemaURL");
    }

    /**
     *
     * @param filename
     */
    @Override
    public void serializeXMLObject(String filename) {

        XStream xstream = getXStreamWriter(onExportShouldOmitTracer());

        String xml = xstream.toXML(this);

        xml = ReduxConstants.XML_Header + xml;

        xml = xml.replaceFirst("UPbReduxFraction",
                "UPbReduxFraction "//
                + ReduxConstants.XML_ResourceHeader//
                + XMLSchemaURL//
                + "\"");

        try {
            FileWriter outFile = new FileWriter(filename);
            PrintWriter out = new PrintWriter(outFile);

            // Write xml to file
            out.println(xml);
            out.flush();
            out.close();
            outFile.close();

        } catch (IOException e) {
        }

    }

    /**
     *
     * @param filename
     * @param doValidate
     * @return
     * @throws FileNotFoundException
     * @throws ETException
     * @throws BadOrMissingXMLSchemaException
     */
    @Override
    public Object readXMLObject(String filename, boolean doValidate)
            throws FileNotFoundException,
            ETException,
            FileNotFoundException,
            BadOrMissingXMLSchemaException {

        FractionI myUPbReduxFraction = null;

        BufferedReader reader = URIHelper.getBufferedReader(filename);

        if (reader != null) {
            boolean isValidOrAirplaneMode = !doValidate;

            XStream xstream = getXStreamReader();

            if (doValidate) {
                isValidOrAirplaneMode = URIHelper.validateXML(reader, filename, XMLSchemaURL);
            }

            if (isValidOrAirplaneMode) {
                // re-create reader
                reader = URIHelper.getBufferedReader(filename);
                try {
                    myUPbReduxFraction = (UPbFraction) xstream.fromXML(reader);
                } catch (ConversionException e) {
                    throw new ETException(null, e.getMessage());
                }
            } else {
                throw new ETException(null, "XML data file does not conform to schema.");
            }
        } else {
            throw new FileNotFoundException("Missing XML data file.");
        }

        return (UPbFraction) myUPbReduxFraction;

    }

    /**
     * Provides for optional inclusion of Tracer with UPbFraction XML output.
     *
     * @param omitTracer
     * @return
     */
    public XStream getXStreamWriter(boolean omitTracer) {

        XStream xstream = getXStreamWriter();

        if (omitTracer) {
            xstream.omitField(UPbFraction.class, "Tracer");
        }

        return xstream;
    }

    /**
     * Determines whether Tracer should be exported to XML file of UPbFraction.
     *
     * @return
     */
    public boolean onExportShouldOmitTracer() {

        return !((((MeasuredRatioModel) getMeasuredRatioByName(MeasuredRatios.r238_235m.getName())).isFracCorr())
                || (((MeasuredRatioModel) getMeasuredRatioByName(MeasuredRatios.r233_235m.getName())).isFracCorr()));
    }

    /**
     *
     * @return
     */
    private XStream getXStreamWriter() {
        XStream xstream = new XStream();

        customizeXstream(xstream);

        return xstream;
    }

    /**
     *
     * @param xstream
     */
    private void customizeXstream(XStream xstream) {

        xstream.registerConverter(new UPbFractionXMLConverter(this));
        xstream.registerConverter(new TracerXMLConverter());
        xstream.registerConverter(new TracerUPbModelXMLConverter());
        xstream.registerConverter(new ValueModelXMLConverter());
        xstream.registerConverter(new MeasuredRatioModelXMLConverter());

        xstream.alias("UPbReduxFraction", UPbFraction.class);
        xstream.alias("ValueModel", ValueModel.class);
        xstream.alias("MeasuredRatioModel", MeasuredRatioModel.class);
        xstream.alias("Tracer", Tracer.class);
        xstream.alias("tracerUPbModel", TracerUPbModel.class);

        setClassXMLSchemaURL();
    }

    /**
     *
     * @return
     */
    private XStream getXStreamReader() {

        XStream xstream = new XStream(new DomDriver());

        customizeXstream(xstream);

        return xstream;
    }

    /**
     * Reads in a standard UPbReduxFraction and post-processes it for analysis.
     *
     * @param filename
     * @param aliquotNumber
     * @param doValidate
     * @return
     * @throws BadOrMissingXMLSchemaException
     */
    public UPbFraction readXMLFraction(String filename, int aliquotNumber, boolean doValidate)
            throws BadOrMissingXMLSchemaException {

        FractionI myUPbReduxFraction = null;

        try {
            myUPbReduxFraction = (FractionI) readXMLObject(filename, doValidate);

            // set LabData
            ((UPbFraction) myUPbReduxFraction).setMyLabData(this.getMyLabData());

            // fill missing fields
            myUPbReduxFraction.setAliquotNumber(aliquotNumber);

            ((UPbFraction) myUPbReduxFraction).setPedigreePb("");
            ((UPbFraction) myUPbReduxFraction).setPedigreeU("");

            myUPbReduxFraction.setChanged(true);
            myUPbReduxFraction.setDeleted(false);

            ((UPbFraction) myUPbReduxFraction).setNotesPb("");
            ((UPbFraction) myUPbReduxFraction).setNotesU("");
            ((UPbFraction) myUPbReduxFraction).setSourceFilePb("NONE");
            ((UPbFraction) myUPbReduxFraction).setSourceFileU("NONE");

            // AUG 2011 Big BUG
            // Tracer is used for either case now that Bariatric etc corrections are made
            // this slipped though the cracks since it wasn't ever really used
            // the following is wrong and replaced with correct logic
            // in calling method Sample.processXMLFractionFile
            //
            // assumption = only U or UPb fractions have tracers (for U)
            // therefore if this is U or UPb and no tracer is specified, we use
            // the default otherwise it is Pb only and we use <none>

            /*
             * WRONG if ( ((UPbFraction) myUPbReduxFraction).getTracer() == null
             * ) { if ( (((UPbFraction)
             * myUPbReduxFraction).getRatioType().equalsIgnoreCase( "U" )) || //
             * (((UPbFraction)
             * myUPbReduxFraction).getRatioType().equalsIgnoreCase( "UPb" )) ) {
             * ((UPbFraction) myUPbReduxFraction).setTracer( this.getTracer() );
             * } else { try { ((UPbFraction) myUPbReduxFraction).setTracer(
             * getMyLabData().getFirstTracer() ); } catch (BadLabDataException
             * ex) { } } }
             */
            ((UPbFractionI) myUPbReduxFraction).setAlphaPbModel(this.getAlphaPbModel());
            ((UPbFractionI) myUPbReduxFraction).setAlphaUModel(this.getAlphaUModel());
            ((UPbFractionI) myUPbReduxFraction).setPbBlank(this.getPbBlank());

            myUPbReduxFraction.setPhysicalConstantsModel(this.getPhysicalConstantsModel());

            // initialize parent fields
            myUPbReduxFraction.getValuesFrom(this, false);

            // may 2008 discovered that reading u first did not set analysis measures
            populateAnalysisMeasuresFromImportedFraction(myUPbReduxFraction, myUPbReduxFraction);

            // aug 2010
            ((UPbFractionI)myUPbReduxFraction).setFractionationCorrectedU( //
                    ((UPbFraction) myUPbReduxFraction).getMeanAlphaU().compareTo(BigDecimal.ZERO) == 1);
            // aug 2010
            ((UPbFractionI)myUPbReduxFraction).setFractionationCorrectedPb(//
                    ((UPbFraction) myUPbReduxFraction).getMeanAlphaPb().compareTo(BigDecimal.ZERO) == 1);

            // check ratio_type and set source file
            if (!((myUPbReduxFraction.getRatioType().equalsIgnoreCase("U"))//
                    || (myUPbReduxFraction.getRatioType().equalsIgnoreCase("Pb")) //
                    || (myUPbReduxFraction.getRatioType().equalsIgnoreCase("UPb")))) {
                throw new ETException(null, "RatioType is NOT recognized.");
            }

            if (myUPbReduxFraction.getRatioType().equalsIgnoreCase("U")//
                    || myUPbReduxFraction.getRatioType().equalsIgnoreCase("UPb")) {
                ((UPbFraction) myUPbReduxFraction).setSourceFileU(filename);
                ((UPbFraction) myUPbReduxFraction).setPedigreeU(getPedigree());

            }

            if (myUPbReduxFraction.getRatioType().equalsIgnoreCase("Pb") //
                    || myUPbReduxFraction.getRatioType().equalsIgnoreCase("UPb")) {
                ((UPbFraction) myUPbReduxFraction).setSourceFilePb(filename);
                ((UPbFraction) myUPbReduxFraction).setPedigreePb(getPedigree());
            }

        } catch (FileNotFoundException | ETException ex) {
            Logger.getLogger(UPbFraction.class.getName()).log(Level.SEVERE, null, ex);
        }

        return (UPbFraction) myUPbReduxFraction;

    }

    /**
     *
     * @return
     */
    public boolean isAnOxide() {
        return //
                ((((MeasuredRatioModel) getMeasuredRatioByName(MeasuredRatios.r238_235m.getName())).isOxideCorr())
                & (((MeasuredRatioModel) getMeasuredRatioByName(MeasuredRatios.r233_235m.getName())).isOxideCorr())//
                ||//
                (((MeasuredRatioModel) getMeasuredRatioByName(MeasuredRatios.r238_236m.getName())).isOxideCorr())
                & (((MeasuredRatioModel) getMeasuredRatioByName(MeasuredRatios.r233_236m.getName())).isOxideCorr()));
    }

    // modified jan 2011
    /**
     *
     * @param isAnOxide
     * @param tracerType
     */
    public void setFractionIsAnOxide(boolean isAnOxide, String tracerType) {
        if (tracerType.contains("235")) {
            ((MeasuredRatioModel) getMeasuredRatioByName(MeasuredRatios.r238_235m.getName())).setOxideCorr(isAnOxide);
            ((MeasuredRatioModel) getMeasuredRatioByName(MeasuredRatios.r233_235m.getName())).setOxideCorr(isAnOxide);
        } else if (tracerType.contains("236")) {
            ((MeasuredRatioModel) getMeasuredRatioByName(MeasuredRatios.r238_236m.getName())).setOxideCorr(isAnOxide);
            ((MeasuredRatioModel) getMeasuredRatioByName(MeasuredRatios.r233_236m.getName())).setOxideCorr(isAnOxide);
        }
    }

    private void populateAnalysisMeasuresFromImportedFraction(
            FractionI sourceFraction,
            FractionI sinkFraction) {

        if (isAnOxide()) {
            sinkFraction.getAnalysisMeasure(AnalysisMeasures.r18O_16O.getName()).setValue(((UPbFraction) sourceFraction).getR18O16O());
            sinkFraction.getAnalysisMeasure(AnalysisMeasures.r18O_16O.getName()).setOneSigma(getMyLabData().getDefaultR18O_16O().getOneSigmaAbs());

            // march 2012 = prevent overwriting of revised values
            ValueModel r18O_16O_revised = sinkFraction.getAnalysisMeasure(AnalysisMeasures.r18O_16O_revised.getName());
            if (!r18O_16O_revised.hasPositiveValue()) {
                sinkFraction.getAnalysisMeasure(AnalysisMeasures.r18O_16O_revised.getName())//
                        .setValue(((UPbFraction) sourceFraction).getR18O16O());
                sinkFraction.getAnalysisMeasure(AnalysisMeasures.r18O_16O_revised.getName())//
                        .setOneSigma(getMyLabData().getDefaultR18O_16O().getOneSigmaAbs());
            }
        }

        updateAnalysisMeasure(//
                ((UPbFraction) sourceFraction).getLabUBlankMass().movePointLeft(12),//
                getMyLabData().getDefaultAssumedUBlankMassInGrams(),
                sinkFraction.getAnalysisMeasure(AnalysisMeasures.uBlankMassInGrams.getName()));

        updateAnalysisMeasure(//
                ((UPbFraction) sourceFraction).getR238_235s(),//
                getMyLabData().getDefaultR238_235s(),
                sinkFraction.getAnalysisMeasure(AnalysisMeasures.r238_235s.getName()));

        updateAnalysisMeasure(//
                ((UPbFraction) sourceFraction).getR238_235b(),//
                getMyLabData().getDefaultR238_235b(),
                sinkFraction.getAnalysisMeasure(AnalysisMeasures.r238_235b.getName()));

        updateAnalysisMeasure(//
                ((UPbFraction) sourceFraction).getTracerMass(),//
                getMyLabData().getDefaultTracerMass(),
                sinkFraction.getAnalysisMeasure(AnalysisMeasures.tracerMassInGrams.getName()));
    }

    private void updateAnalysisMeasure(BigDecimal sourceValue, ValueModel labDefault, ValueModel sinkModel) {
        if (sinkModel.getValue().compareTo(BigDecimal.ZERO) == 0) {
            if (sourceValue.compareTo(BigDecimal.ZERO) == 0) {
                sinkModel.setValue(labDefault.getValue());
            } else {
                sinkModel.setValue(sourceValue);
            }
        }
        if (sinkModel.getOneSigma().compareTo(BigDecimal.ZERO) == 0) {
            sinkModel.setOneSigma(labDefault.getOneSigma());
        }
    }

    /**
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        FractionI myUPbReduxFraction = new UPbFraction("NONE");

        ((XMLSerializationI) myUPbReduxFraction).serializeXMLObject("UPbFractionTEST.xml");

        myUPbReduxFraction = ((UPbFraction) myUPbReduxFraction).readXMLFraction("UPbFractionTEST.xml", 1, true);

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
    public void toggleRejectedStatus() {
        this.rejected = !this.rejected;
    }

    /**
     *
     * @return
     */
    public BigDecimal getR18O16O() {
        return r18O_16O;
    }

    /**
     *
     * @param r18O16O
     */
    public void setR18O16O(BigDecimal r18O16O) {
        this.r18O_16O = r18O16O;
    }

    /**
     *
     * @return
     */
    public BigDecimal getLabUBlankMass() {
        return labUBlankMass;
    }

    /**
     *
     * @param labUBlankMass
     */
    public void setLabUBlankMass(BigDecimal labUBlankMass) {
        this.labUBlankMass = labUBlankMass;
    }

    /**
     *
     * @return
     */
    public BigDecimal getR238_235b() {
        return r238_235b;
    }

    /**
     *
     * @param r238_235b
     */
    public void setR238_235b(BigDecimal r238_235b) {
        this.r238_235b = r238_235b;
    }

    /**
     *
     * @return
     */
    public BigDecimal getR238_235s() {
        return r238_235s;
    }

    /**
     *
     * @param r238_235s
     */
    public void setR238_235s(BigDecimal r238_235s) {
        this.r238_235s = r238_235s;
    }

    /**
     *
     * @return
     */
    public BigDecimal getTracerMass() {
        return tracerMass;
    }

    /**
     *
     * @param tracerMass
     */
    public void setTracerMass(BigDecimal tracerMass) {
        this.tracerMass = tracerMass;
    }

    /**
     *
     * @return
     */
    public ValueModel[] getRadiogenicIsotopeDatesWithTracerUncertainty() {
        return radiogenicIsotopeDatesWithTracerUncertainty;
    }

    /**
     *
     * @param radiogenicIsotopeDatesWithTracerUncertainty
     */
    public void setRadiogenicIsotopeDatesWithTracerUncertainty(ValueModel[] radiogenicIsotopeDatesWithTracerUncertainty) {
        this.radiogenicIsotopeDatesWithTracerUncertainty = radiogenicIsotopeDatesWithTracerUncertainty;
    }

    /**
     *
     * @param ratioName
     * @return
     */
    @Override
    public ValueModel getRadiogenicIsotopeDateWithTracerUnctByName(String ratioName) {
        if (getRadiogenicIsotopeDatesWithTracerUncertainty() == null) {
            setRadiogenicIsotopeDatesWithTracerUncertainty(new ValueModel[0]);
        }

        for (int i = 0; i < getRadiogenicIsotopeDatesWithTracerUncertainty().length; i++) {
            if (getRadiogenicIsotopeDatesWithTracerUncertainty()[i].getName().equalsIgnoreCase(ratioName)) {
                return getRadiogenicIsotopeDatesWithTracerUncertainty()[i];
            }
        }

        // return a new model - handles backwards compatible
        // have to add element to array
        ValueModel[] temp = new ValueModel[getRadiogenicIsotopeDatesWithTracerUncertainty().length + 1];
        System.arraycopy(getRadiogenicIsotopeDatesWithTracerUncertainty(), 0, temp, 0, getRadiogenicIsotopeDatesWithTracerUncertainty().length);

        ValueModel riaModel
                = new ValueModel(ratioName,
                        BigDecimal.ZERO,
                        "ABS",
                        BigDecimal.ZERO, BigDecimal.ZERO);

        temp[getRadiogenicIsotopeDatesWithTracerUncertainty().length] = riaModel;

        setRadiogenicIsotopeDatesWithTracerUncertainty(temp);

        return riaModel;
    }

    /**
     *
     * @return
     */
    public ValueModel[] getRadiogenicIsotopeDatesWithAllUncertainty() {
        return radiogenicIsotopeDatesWithAllUncertainty;
    }

    /**
     *
     * @param radiogenicIsotopeDatesWithAllUncertainty
     */
    public void setRadiogenicIsotopeDatesWithAllUncertainty(
            ValueModel[] radiogenicIsotopeDatesWithAllUncertainty) {
        this.radiogenicIsotopeDatesWithAllUncertainty = radiogenicIsotopeDatesWithAllUncertainty;
    }

    /**
     *
     * @param ratioName
     * @return
     */
    @Override
    public ValueModel getRadiogenicIsotopeDateWithAllUnctByName(String ratioName) {
        if (getRadiogenicIsotopeDatesWithAllUncertainty() == null) {
            setRadiogenicIsotopeDatesWithAllUncertainty(new ValueModel[0]);
        }

        for (int i = 0; i < getRadiogenicIsotopeDatesWithAllUncertainty().length; i++) {
            if (getRadiogenicIsotopeDatesWithAllUncertainty()[i].getName().equalsIgnoreCase(ratioName)) {
                return getRadiogenicIsotopeDatesWithAllUncertainty()[i];
            }
        }

        // return a new model - handles backwards compatible
        // have to add element to array
        ValueModel[] temp = new ValueModel[getRadiogenicIsotopeDatesWithAllUncertainty().length + 1];
        System.arraycopy(getRadiogenicIsotopeDatesWithAllUncertainty(), 0, temp, 0, getRadiogenicIsotopeDatesWithAllUncertainty().length);

        ValueModel riaModel
                = new ValueModel(ratioName,
                        BigDecimal.ZERO,
                        "ABS",
                        BigDecimal.ZERO, BigDecimal.ZERO);

        temp[getRadiogenicIsotopeDatesWithAllUncertainty().length] = riaModel;

        setRadiogenicIsotopeDatesWithAllUncertainty(temp);

        return riaModel;
    }

    /**
     *
     * @return
     */
    public ValueModel[] getRadiogenicIsotopeRatiosWithTracerUncertainty() {
        return radiogenicIsotopeRatiosWithTracerUncertainty;
    }

    /**
     *
     * @param radiogenicIsotopeRatiosWithTracerUncertainty
     */
    public void setRadiogenicIsotopeRatiosWithTracerUncertainty(ValueModel[] radiogenicIsotopeRatiosWithTracerUncertainty) {
        this.radiogenicIsotopeRatiosWithTracerUncertainty = radiogenicIsotopeRatiosWithTracerUncertainty;
    }

    /**
     *
     * @param ratioName
     * @return
     */
    public ValueModel getRadiogenicIsotopeRatioWithTracerUnctByName(String ratioName) {
        if (getRadiogenicIsotopeRatiosWithTracerUncertainty() == null) {
            setRadiogenicIsotopeRatiosWithTracerUncertainty(new ValueModel[0]);
        }

        for (int i = 0; i < getRadiogenicIsotopeRatiosWithTracerUncertainty().length; i++) {
            if (getRadiogenicIsotopeRatiosWithTracerUncertainty()[i].getName().equalsIgnoreCase(ratioName)) {
                return getRadiogenicIsotopeRatiosWithTracerUncertainty()[i];
            }
        }

        // return a new model - handles backwards compatible
        // have to add element to array
        ValueModel[] temp = new ValueModel[getRadiogenicIsotopeRatiosWithTracerUncertainty().length + 1];
        System.arraycopy(getRadiogenicIsotopeRatiosWithTracerUncertainty(), 0, temp, 0, getRadiogenicIsotopeRatiosWithTracerUncertainty().length);

        ValueModel rirModel
                = new ValueModel(ratioName,
                        BigDecimal.ZERO,
                        "ABS",
                        BigDecimal.ZERO, BigDecimal.ZERO);

        temp[getRadiogenicIsotopeRatiosWithTracerUncertainty().length] = rirModel;

        setRadiogenicIsotopeRatiosWithTracerUncertainty(temp);

        return rirModel;

    }

    /**
     *
     * @return
     */
    public ValueModel[] getRadiogenicIsotopeRatiosWithAllUncertainty() {
        return radiogenicIsotopeRatiosWithAllUncertainty;
    }

    /**
     *
     * @param radiogenicIsotopeRatiosWithAllUncertainty
     */
    public void setRadiogenicIsotopeRatiosWithAllUncertainty(ValueModel[] radiogenicIsotopeRatiosWithAllUncertainty) {
        this.radiogenicIsotopeRatiosWithAllUncertainty = radiogenicIsotopeRatiosWithAllUncertainty;
    }

    /**
     *
     * @param ratioName
     * @return
     */
    public ValueModel getRadiogenicIsotopeRatioWithAllUncertaintyByName(String ratioName) {
        if (getRadiogenicIsotopeRatiosWithAllUncertainty() == null) {
            setRadiogenicIsotopeRatiosWithAllUncertainty(new ValueModel[0]);
        }

        for (int i = 0; i < getRadiogenicIsotopeRatiosWithAllUncertainty().length; i++) {
            if (getRadiogenicIsotopeRatiosWithAllUncertainty()[i].getName().equalsIgnoreCase(ratioName)) {
                return getRadiogenicIsotopeRatiosWithAllUncertainty()[i];
            }
        }
        // return a new model - handles backwards compatible
        // have to add element to array
        ValueModel[] temp = new ValueModel[getRadiogenicIsotopeRatiosWithAllUncertainty().length + 1];
        System.arraycopy(getRadiogenicIsotopeRatiosWithAllUncertainty(), 0, temp, 0, getRadiogenicIsotopeRatiosWithAllUncertainty().length);

        ValueModel rirModel
                = new ValueModel(ratioName,
                        BigDecimal.ZERO,
                        "ABS",
                        BigDecimal.ZERO, BigDecimal.ZERO);

        temp[getRadiogenicIsotopeRatiosWithAllUncertainty().length] = rirModel;

        setRadiogenicIsotopeRatiosWithAllUncertainty(temp);

        return rirModel;
    }

    /**
     * @return the reductionHandler
     */
    public ReductionHandler getReductionHandler() {
        return reductionHandler;
    }

    /**
     * @param reductionHandler the reductionHandler to set
     */
    public void setReductionHandler(ReductionHandler reductionHandler) {
        this.reductionHandler = reductionHandler;
    }

    /**
     *
     */
    public void restoreSavedFractionationModels() {
        this.alphaPbModel = this.alphaPbModelSaved.copy();
        this.alphaUModel = this.alphaUModelSaved.copy();
    }

    // TODO: needed for backward compatibility feb 2009 REMOVE by 2010
    /**
     *
     */
    @Override
    public void setSavedFractionationModels() {
        try {
            this.alphaPbModelSaved = this.alphaPbModel.copy();
        } catch (Exception e) {
        }
        try {
            this.alphaUModelSaved = this.alphaUModel.copy();
        } catch (Exception e) {
        }
    }

    /**
     *
     */
    public void toFileAllDataValues() {
        File dataValuesFile = new File("ALL_VALUES_" + getFractionID() + ".txt");
        PrintWriter outputWriter = null;
        try {
            outputWriter = new PrintWriter(new FileWriter(dataValuesFile));
            outputWriter.println("\n\n******   FRACTION " + getFractionID() + "   ********************\n\n");

            // measured ratios
            outputWriter.println("measured ratios");
            for (int i = 0; i < getMeasuredRatios().length; i++) {
                // special treatment for measured ratios printed here only per Noah July 2011
                String dataLine = getMeasuredRatios()[i].formatValueAndOneSigmaABSForTesting();
                if (isFractionationCorrectedPb()) {
                    // TODO: regular expresssion
                    dataLine = dataLine.replace("04m", "04fc");
                    dataLine = dataLine.replace("07m", "07fc");
                    dataLine = dataLine.replace("08m", "08fc");
                    dataLine = dataLine.replace("05m", "05fc");
                }
                outputWriter.println(dataLine);
            }
            outputWriter.println();

            // tracer ratios
            outputWriter.println("tracer ratios and IsotopeConcentrations");
            for (int i = 0; i < getTracer().getData().length; i++) {
                outputWriter.println(getTracer().getData()[i].formatValueAndOneSigmaABSForTesting());
            }
            outputWriter.println();

            outputWriter.println("Tracer Correlations");
            Iterator<String> correlationsIterator = getTracer().getRhosVarUnct().keySet().iterator();
            while (correlationsIterator.hasNext()) {
                String rhoName = correlationsIterator.next();
                BigDecimal rhoValue = getTracer().getRhosVarUnct().get(rhoName);
                if (rhoValue.compareTo(BigDecimal.ZERO) != 0) {
                    outputWriter.println("   " + rhoName + "\t = \t" + rhoValue.toString());
                }
            }
            outputWriter.println();

            // pbblank ratios
            outputWriter.println("pbblank ratios");
            for (int i = 0; i < getPbBlank().getData().length; i++) {
                outputWriter.println(getPbBlank().getData()[i].formatValueAndOneSigmaABSForTesting());
            }
            outputWriter.println();

            outputWriter.println("pbblank Correlations");
            Iterator<String> pbblankIterator = getPbBlank().getRhosVarUnct().keySet().iterator();
            while (pbblankIterator.hasNext()) {
                String rhoName = pbblankIterator.next();
                BigDecimal rhoValue = getPbBlank().getRhosVarUnct().get(rhoName);
                if (rhoValue.compareTo(BigDecimal.ZERO) != 0) {
                    outputWriter.println("   " + rhoName + "\t = \t" + rhoValue.toString());
                }
            }
            outputWriter.println();

            // InitialPbModel ratios
            outputWriter.println("InitialPbModel ratios");
            for (int i = 0; i < getInitialPbModel().getData().length; i++) {
                outputWriter.println(getInitialPbModel().getData()[i].formatValueAndOneSigmaABSForTesting());
            }
            outputWriter.println();

            outputWriter.println("InitialPbModel Correlations");
            Iterator<String> initialPbModelIterator = getInitialPbModel().getRhosVarUnct().keySet().iterator();
            while (initialPbModelIterator.hasNext()) {
                String rhoName = initialPbModelIterator.next();
                BigDecimal rhoValue = getInitialPbModel().getRhosVarUnct().get(rhoName);
                if (rhoValue.compareTo(BigDecimal.ZERO) != 0) {
                    outputWriter.println("   " + rhoName + "\t = \t" + rhoValue.toString());
                }
            }
            outputWriter.println();

            // PhysicalConstantsModel AtomicMolarMasses
            outputWriter.println("PhysicalConstantsModel AtomicMolarMasses");
            Iterator<String> atomicMolarMassesIterator = ((PhysicalConstantsModel) getPhysicalConstantsModel()).getAtomicMolarMasses().keySet().iterator();
            while (atomicMolarMassesIterator.hasNext()) {
                String massName = atomicMolarMassesIterator.next();
                BigDecimal massValue = ((PhysicalConstantsModel) getPhysicalConstantsModel()).getAtomicMolarMasses().get(massName);
                outputWriter.println("   " + massName + "\t = \t" + massValue.toString());
            }
            outputWriter.println();

            // PhysicalConstantsModel MeasuredConstants
            outputWriter.println("PhysicalConstantsModel MeasuredConstants");
            for (int i = 0; i < getPhysicalConstantsModel().getData().length; i++) {
                outputWriter.println(getPhysicalConstantsModel().getData()[i].formatValueAndOneSigmaABSForTesting());
            }
            outputWriter.println();

            outputWriter.println("MeasuredConstants Correlations");
            Iterator<String> measuredConstantsIterator = getPhysicalConstantsModel().getRhosVarUnct().keySet().iterator();
            while (measuredConstantsIterator.hasNext()) {
                String rhoName = measuredConstantsIterator.next();
                BigDecimal rhoValue = getPhysicalConstantsModel().getRhosVarUnct().get(rhoName);
                if (rhoValue.compareTo(BigDecimal.ZERO) != 0) {
                    outputWriter.println("   " + rhoName + "\t = \t" + rhoValue.toString());
                }
            }
            outputWriter.println();

            // SampleIsochronRatios
            outputWriter.println("SampleIsochronRatios");
            Arrays.sort(getSampleIsochronRatios());
            for (int i = 0; i < getSampleIsochronRatios().length; i++) {
                outputWriter.println(getSampleIsochronRatios()[i].formatValueAndOneSigmaABSForTesting());
            }
            outputWriter.println();

            // AnalysisMeasures
            outputWriter.println("AnalysisMeasures");
            Arrays.sort(getAnalysisMeasures());
            for (int i = 0; i < getAnalysisMeasures().length; i++) {
                outputWriter.println(getAnalysisMeasures()[i].formatValueAndOneSigmaABSForTesting());
            }
            outputWriter.println();

            // CompositionalMeasures
            outputWriter.println("CompositionalMeasures");
            Arrays.sort(getCompositionalMeasures());
            for (int i = 0; i < getCompositionalMeasures().length; i++) {
                outputWriter.println(getCompositionalMeasures()[i].formatValueAndOneSigmaABSForTesting());
            }
            outputWriter.println();

            // RadiogenicIsotopeRatios
            outputWriter.println("RadiogenicIsotopeRatios");
            Arrays.sort(getRadiogenicIsotopeRatios());
            for (int i = 0; i < getRadiogenicIsotopeRatios().length; i++) {
                outputWriter.println(getRadiogenicIsotopeRatios()[i].formatValueAndOneSigmaABSForTesting());
            }
            outputWriter.println();

            // RadiogenicIsotopeDates
            outputWriter.println("RadiogenicIsotopeDates");
            Arrays.sort(getRadiogenicIsotopeDates());
            for (int i = 0; i < getRadiogenicIsotopeDates().length; i++) {
                outputWriter.println(getRadiogenicIsotopeDates()[i].formatValueAndOneSigmaABSForTesting());
            }
            outputWriter.println();

            // outputs
            outputWriter.println("outputs");
            for (int i = 0; i < getOutputs().length; i++) {
                outputWriter.println(getOutputs()[i].formatValueAndOneSigmaABSForTesting());
            }
            outputWriter.println();

            // input covariances
            outputWriter.println("input covariances");

            NumberFormat formatter2 = null;
            try {
                Map<String, BigDecimal> sortedMap = new TreeMap<String, BigDecimal>(getCoVariances());
                formatter2 = new DecimalFormat("0.0000000000E0");

                Iterator it = sortedMap.keySet().iterator();
                while (it.hasNext()) {
                    String key = (String) it.next();
                    outputWriter.println(String.format("   %1$-34s", key) //
                            + " = " //
                            + String.format("%1$-20s", formatter2.format(((BigDecimal) sortedMap.get(key)).doubleValue()))//
                    );

                }
            } catch (Exception e) {
            }
            outputWriter.println();

            // partial derivatives
            outputWriter.println("partial derivatives");
            try {
                Map<String, BigDecimal> sortedMap = new TreeMap<>(getParDerivTerms());
                for (Iterator<String> it = sortedMap.keySet().iterator(); it.hasNext();) {
                    String key = it.next();
                    if (key.startsWith("d")) {
                        outputWriter.println(String.format("   %1$-34s", key) //
                                + " = " //
                                + String.format("%1$-20s", formatter2.format(sortedMap.get(key).doubleValue()))//
                        );
                    }
                }
            } catch (Exception e) {
            }
            outputWriter.println();

            // sept 2010
            // matrix entries
            if (getReductionHandler() != null) {
                outputWriter.println("matrix entries for " + getReductionHandler().getMatrixSpecsName() + "\n");
                Vector<String> matrixEntriesListing = new Vector<String>();

                matrixEntriesListing = //
                        CollectionHelpers.vectorSortedUniqueMembers( //
                                MatrixSpecifications.getMatrixSpecsByName(//
                                        getReductionHandler().getMatrixSpecsName()));
                // remove lambdas
                matrixEntriesListing.remove(Lambdas.lambda230.getName());
                matrixEntriesListing.remove(Lambdas.lambda231.getName());
                matrixEntriesListing.remove(Lambdas.lambda232.getName());
                matrixEntriesListing.remove(Lambdas.lambda234.getName());
                matrixEntriesListing.remove(Lambdas.lambda235.getName());
                matrixEntriesListing.remove(Lambdas.lambda238.getName());

                // brute force find
                for (String entry : matrixEntriesListing) {

                    ValueModel foundVal = null;
                    for (ValueModel measuredRatio : getMeasuredRatios()) {
                        if (measuredRatio.getName().equalsIgnoreCase(entry)) {
                            foundVal = measuredRatio;
                            break;
                        }
                    }

                    if (foundVal == null) {
                        for (ValueModel analysisMeasure : getAnalysisMeasures()) {
                            if (analysisMeasure.getName().equalsIgnoreCase(entry)) {
                                foundVal = analysisMeasure;
                                break;
                            }
                        }
                    }

                    if (foundVal == null) {
                        for (ValueModel compositionalMeasure : getCompositionalMeasures()) {
                            if (compositionalMeasure.getName().equalsIgnoreCase(entry)) {
                                foundVal = compositionalMeasure;
                                break;
                            }
                        }
                    }

                    if (foundVal == null) {
                        for (ValueModel data : getTracer().getData()) {
                            if (data.getName().equalsIgnoreCase(entry)) {
                                foundVal = data;
                                break;
                            }
                        }
                    }

                    if (foundVal == null) {
                        for (ValueModel data : getPbBlank().getData()) {
                            if (data.getName().equalsIgnoreCase(entry)) {
                                foundVal = data;
                                break;
                            }
                        }
                    }

                    if (foundVal == null) {
                        for (int i = 0; i < specialInputVariablesInOrder.size(); i++) {
                            if (specialInputVariablesInOrder.get(i).getName().equalsIgnoreCase(entry)) {
                                foundVal = specialInputVariablesInOrder.get(i);
                                break;
                            }
                        }
                    }

                    if (foundVal == null) {
                        for (int i = 0; i < variablesInOrder.size(); i++) {
                            if (variablesInOrder.get(i).getName().equalsIgnoreCase(entry)) {
                                foundVal = variablesInOrder.get(i);
                                break;
                            }
                        }
                    }

                    if (foundVal != null) {
                        outputWriter.println(foundVal.formatValueAndOneSigmaABSForTesting());
                    } else {
                        outputWriter.println(entry);
                    }
                }
            }
            outputWriter.println();

            outputWriter.flush();
            outputWriter.close();

        } catch (IOException iOException) {
        }

        try {
            BrowserControl.displayURL(dataValuesFile.getCanonicalPath());
        } catch (IOException ex) {
        }

    }

    /**
     * @return the outputs
     */
    public ValueModel[] getOutputs() {
        if (outputs == null) {
            outputs = new ValueModel[0];
        }
        return outputs;
    }

    /**
     * @param outputs the outputs to set
     */
    public void setOutputs(ValueModel[] outputs) {
        this.outputs = outputs;
    }

    /**
     *
     * @param oName
     * @return
     */
    public ValueModel getOutputsByName(String oName) {
        for (ValueModel output : getOutputs()) {
            if (output.getName().equalsIgnoreCase(oName)) {
                return output;
            }
        }
        // return a new model - handles backwards compatible
        // have to add element to array
        ValueModel[] temp = new ValueModel[getOutputs().length + 1];
        System.arraycopy(getOutputs(), 0, temp, 0, getOutputs().length);

        ValueModel oModel
                = new ValueModel(oName,
                        BigDecimal.ZERO,
                        "ABS",
                        BigDecimal.ZERO, BigDecimal.ZERO);

        temp[getOutputs().length] = oModel;

        setOutputs(temp);

        return oModel;
    }

    /**
     *
     * @param oName
     * @param valueModel
     */
    public void setOutputByName(String oName, ValueModel valueModel) {
        // make sure it exists
        getOutputsByName(oName);
        for (int i = 0; i < getOutputs().length; i++) {
            if (getOutputs()[i].getName().equalsIgnoreCase(oName)) {
                getOutputs()[i] = valueModel;
            }
        }
    }

    /**
     * @return the parDerivTerms
     */
    @Override
    public ConcurrentMap<String, BigDecimal> getParDerivTerms() {
        return parDerivTerms;
    }

    /**
     * @param parDerivTerms the parDerivTerms to set
     */
    @Override
    public void setParDerivTerms(ConcurrentMap<String, BigDecimal> parDerivTerms) {
        this.parDerivTerms = parDerivTerms;
    }

    /**
     * @return the coVariances
     */
    public Map<String, BigDecimal> getCoVariances() {
        return coVariances;
    }

    /**
     * @param coVariances the coVariances to set
     */
    public void setCoVariances(Map<String, BigDecimal> coVariances) {
        this.coVariances = coVariances;
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
     * @return the inAutoUraniumMode
     */
    public boolean isInAutoUraniumMode() {
        return inAutoUraniumMode;
    }

    /**
     * @param inAutoUraniumMode the inAutoUraniumMode to set
     */
    public void setInAutoUraniumMode(boolean inAutoUraniumMode) {
        this.inAutoUraniumMode = inAutoUraniumMode;
    }

    /**
     * @return the inputDate206_238r
     */
    public BigDecimal getInputDate206_238r() {
        if (inputDate206_238r == null) {
            inputDate206_238r = getRadiogenicIsotopeDateByName(RadDates.age207_206r).getValue();
        }
        if (inputDate206_238r.compareTo(BigDecimal.ZERO) == 0) {
            inputDate206_238r = getRadiogenicIsotopeDateByName(RadDates.age207_206r).getValue();
        }
        return inputDate206_238r;
    }

    /**
     * @param inputDate206_238r the inputDate206_238r to set
     */
    public void setInputDate206_238r(BigDecimal inputDate206_238r) {
        this.inputDate206_238r = inputDate206_238r;
    }

    /**
     * @return the inputAlphaU
     */
    public ValueModel getInputAlphaU() {
        if (inputAlphaU == null) {
            try {
                inputAlphaU = alphaUModelSaved.copy();
            } catch (Exception e) {
            }
        }
        return inputAlphaU;
    }

    /**
     * @param inputAlphaU the inputAlphaU to set
     */
    public void setInputAlphaU(ValueModel inputAlphaU) {
        this.inputAlphaU = inputAlphaU;
    }

    /**
     * @return the inputOneSigmaPct
     */
    public BigDecimal getInputOneSigmaPct() {
        if (inputOneSigmaPct == null) {
            inputOneSigmaPct = BigDecimal.ZERO;
        }
        return inputOneSigmaPct;
    }

    /**
     * @param inputOneSigmaPct the inputOneSigmaPct to set
     */
    public void setInputOneSigmaPct(BigDecimal inputOneSigmaPct) {
        this.inputOneSigmaPct = inputOneSigmaPct;
    }

    /**
     *
     * @return
     */
    public boolean tracerTypeIncludes233() {
        boolean retval = false;

        try {
            retval = getTracerType().indexOf("233") > 0;
        } catch (Exception e) {
        }

        return retval;
    }

    /**
     *
     * @return
     */
    public boolean hasXMLUSourceFile() {
        return getSourceFileU().matches("(?i).*.xml");
    }

    /**
     *
     * @return
     */
    public boolean hasXMLPbSourceFile() {
        return getSourceFilePb().matches("(?i).*.xml");
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
     *
     * @return
     */
    public String getNotesPb() {
        return fractionNotes;//notesPb;
    }

    /**
     *
     * @param NotesPb
     */
    public void setNotesPb(String NotesPb) {
//        this.notesPb = NotesPb;
        this.fractionNotes = NotesPb;
    }

    /**
     *
     * @return
     */
    public String getNotesU() {
        return fractionNotes;// notesU;
    }

    /**
     *
     * @param NotesU
     */
    public void setNotesU(String NotesU) {
//        this.notesU = NotesU;
        this.fractionNotes = NotesU;
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
     * @return the boolean showing presence of any lead > 0
     */
    @Override
    public boolean hasMeasuredLead() {
        return //
                getMeasuredRatioByName(MeasuredRatios.r206_204m.getName()).hasPositiveValue()
                || getMeasuredRatioByName(MeasuredRatios.r207_204m.getName()).hasPositiveValue()
                || getMeasuredRatioByName(MeasuredRatios.r208_204m.getName()).hasPositiveValue()
                || getMeasuredRatioByName(MeasuredRatios.r206_207m.getName()).hasPositiveValue()
                || getMeasuredRatioByName(MeasuredRatios.r206_208m.getName()).hasPositiveValue()
                || getMeasuredRatioByName(MeasuredRatios.r204_205m.getName()).hasPositiveValue()
                || getMeasuredRatioByName(MeasuredRatios.r206_205m.getName()).hasPositiveValue()
                || getMeasuredRatioByName(MeasuredRatios.r207_205m.getName()).hasPositiveValue()
                || getMeasuredRatioByName(MeasuredRatios.r208_205m.getName()).hasPositiveValue()
                || hasMeasured202_205();

    }

    /**
     * @return a boolean if any uranium values > 0
     */
    @Override
    public boolean hasMeasuredUranium() {
        return //
                getMeasuredRatioByName(MeasuredRatios.r238_235m.getName()).hasPositiveValue()
                || getMeasuredRatioByName(MeasuredRatios.r233_236m.getName()).hasPositiveValue()
                || getMeasuredRatioByName(MeasuredRatios.r238_233m.getName()).hasPositiveValue()
                || getMeasuredRatioByName(MeasuredRatios.r233_236m.getName()).hasPositiveValue();

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

    @Override
    /**
     * Override for backward compatibility Aug 2010
     */
    public boolean isFractionationCorrectedU() {
        // AUG 2011 made to handle negative numbers
        setFractionationCorrectedU(getMeanAlphaU().compareTo(BigDecimal.ZERO) != 0);
        return super.isFractionationCorrectedU();
    }

    @Override
    /**
     * Override for backward compatibility Aug 2010
     */
    public boolean isFractionationCorrectedPb() {
        // AUG 2011 made to handle negative numbers
        setFractionationCorrectedPb(getMeanAlphaPb().compareTo(BigDecimal.ZERO) != 0);
        return super.isFractionationCorrectedPb();
    }

    /**
     * @return the treatFractionAsZircon
     */
    public boolean treatFractionAsZircon() {
        return treatFractionAsZircon;
    }

    /**
     * @param treatFractionAsZircon the treatFractionAsZircon to set
     */
    public void setTreatFractionAsZircon(boolean treatFractionAsZircon) {
        this.treatFractionAsZircon = treatFractionAsZircon;
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
     * @return the standard
     */
    public boolean isStandard() {
        return standard;
    }

    /**
     * @param standard the standard to set
     */
    public void setStandard(boolean standard) {
        this.standard = standard;
    }

    /**
     *
     */
    @Override
    public void calculateTeraWasserburgRho() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isCommonLeadLossCorrected() {
        return false; // dec 2014 only true in redux for laicpms fractions
    }
}
