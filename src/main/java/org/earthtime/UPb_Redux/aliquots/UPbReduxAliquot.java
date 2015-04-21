/*
 * UPbReduxAliquot.java
 *
 * Created on May 19, 2007, 12:22 PM
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
package org.earthtime.UPb_Redux.aliquots;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.UPb_Redux.fractions.AnalysisFraction;
import org.earthtime.UPb_Redux.fractions.AnalysisFractionXMLConverter;
import org.earthtime.UPb_Redux.fractions.Fraction;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFraction;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFractionI;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.fractionReduction.UPbFractionReducer;
import org.earthtime.UPb_Redux.reduxLabData.ReduxLabData;
import org.earthtime.UPb_Redux.reports.ReportRowGUIInterface;
import org.earthtime.UPb_Redux.samples.SESARSampleMetadata;
import org.earthtime.UPb_Redux.user.UPbReduxConfigurator;
import org.earthtime.UPb_Redux.valueModels.MeasuredRatioModel;
import org.earthtime.UPb_Redux.valueModels.MeasuredRatioModelXMLConverter;
import org.earthtime.UPb_Redux.valueModels.MineralStandardUPbRatioModel;
import org.earthtime.UPb_Redux.valueModels.MineralStandardUPbRatioModelXMLConverter;
import org.earthtime.UPb_Redux.valueModels.SampleDateInterceptModel;
import org.earthtime.UPb_Redux.valueModels.SampleDateInterceptModelXMLConverter;
import org.earthtime.UPb_Redux.valueModels.SampleDateModel;
import org.earthtime.UPb_Redux.valueModels.SampleDateModelXMLConverter;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.UPb_Redux.valueModels.ValueModelReferenced;
import org.earthtime.UPb_Redux.valueModels.ValueModelReferencedXMLConverter;
import org.earthtime.UPb_Redux.valueModels.ValueModelXMLConverter;
import org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException;
import org.earthtime.dataDictionaries.AnalysisImageTypes;
import org.earthtime.dataDictionaries.RadDates;
import org.earthtime.exceptions.ETException;
import org.earthtime.physicalConstants.PhysicalConstantsXMLConverter;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataModels.initialPbModelsET.InitialPbModelET;
import org.earthtime.ratioDataModels.initialPbModelsET.InitialPbModelETXMLConverter;
import org.earthtime.ratioDataModels.mineralStandardModels.MineralStandardUPbModel;
import org.earthtime.ratioDataModels.mineralStandardModels.MineralStandardUPbModelXMLConverter;
import org.earthtime.ratioDataModels.pbBlankICModels.PbBlankICModel;
import org.earthtime.ratioDataModels.pbBlankICModels.PbBlankICModelXMLConverter;
import org.earthtime.ratioDataModels.physicalConstantsModels.PhysicalConstantsModel;
import org.earthtime.ratioDataModels.physicalConstantsModels.PhysicalConstantsModelXMLConverter;
import org.earthtime.ratioDataModels.tracers.TracerUPbModel;
import org.earthtime.ratioDataModels.tracers.TracerUPbModelXMLConverter;
import org.earthtime.archivingTools.URIHelper;
import org.earthtime.xmlUtilities.XMLSerializationI;

/**
 * Working version of Aliquot for interactive calculations prior to publication.
 *
 * @author James F. Bowring
 * @version 2.0
 * @since 1.0
 */
public class UPbReduxAliquot extends Aliquot
        implements AliquotI,
        //        Comparable<Aliquot>,
        ReportRowGUIInterface,
        XMLSerializationI {

    // Class variables
    private static final long serialVersionUID = -1503596071033797228L;
// Instance variables
    private transient String aliquotXMLSchemaURL;
    private transient Date aliquotFolderTimeStamp;
    /**
     * LabData are available to every Aliquot.
     */
    private transient ReduxLabData myReduxLabData;
    private transient boolean selectedInDataTable;
    private int aliquotNumber;
    private Vector<Fraction> aliquotFractions;
    private boolean defaultIsZircon;
    private String defaultTracerID;
    private String defaultTracerMassText;
    private String defaultFractionMassText;
    private String defaultAlphaPbModelID;
    private String defaultAlphaUModelID;
    private String defaultPbBlankID;
    private String defaultInitialPbModelID;
    private String defaultEstimatedDateText;
    private String defaultStaceyKramersOnePctUnctText;
    private String defaultStaceyKramersCorrelationCoeffsText;
    private String defaultPbBlankMassText;
    private String defaultUBlankMassText;
    private String defaultR238_235sText;
    private String defaultR238_235bText;
    private String defaultR18O_16OText;
    private String defaultRTh_UmagmaText;
    private String defaultAr231_235sampleText;
    // uncertainties
    private String defaultTracerMassOneSigmaText;
    private String defaultUBlankMassOneSigmaText;
    private String defaultR238_235sOneSigmaText;
    private String defaultR238_235bOneSigmaText;
    private String defaultPbBlankMassOneSigmaText;
    private String default18O_16OOneSigmaText;
    private String defaultRTh_UmagmaOneSigmaText;
    private String defaultAr231_235sampleOneSigmaText;
    // misc
    private boolean compiled;
    private boolean automaticDataUpdateMode;
    private File containingSampleDataFolder;
    private SESARSampleMetadata mySESARSampleMetadata;
//    private boolean isValidatedSESARChild;
    // list built on demand when archiving to Geochron
    private ArrayList<AnalysisImage> analysisImages;

    /**
     * Creates a new instance of UPbReduxAliquot
     */
    public UPbReduxAliquot() {
    }

    /**
     *
     * @param aliquotNumber
     * @param aliquotName
     * @param reduxLabData
     * @param physicalConstants
     * @param compiled
     * @param mySESARSampleMetadata
     */
    public UPbReduxAliquot(
            int aliquotNumber,
            String aliquotName,
            ReduxLabData reduxLabData,
            AbstractRatiosDataModel physicalConstants,
            boolean compiled,
            SESARSampleMetadata mySESARSampleMetadata) {

        super(ReduxConstants.DEFAULT_ALIQUOT_IGSN);
        setAliquotName(aliquotName);

        this.aliquotNumber = aliquotNumber;

        this.myReduxLabData = reduxLabData;
        setPhysicalConstants(physicalConstants);

        this.compiled = compiled;

        setAliquotFractions(new Vector<Fraction>());

        setSampleIGSN("NONE");
        setLaboratoryName(getMyReduxLabData().getLabName());
        setAnalystName(getMyReduxLabData().getAnalystName());

        // setup default text values for editing
        try {
            setDefaultTracerID(getMyReduxLabData().getDefaultLabTracer().getNameAndVersion());

        } catch (BadLabDataException badLabDataException) {
        }

        try {
            setDefaultAlphaPbModelID(getMyReduxLabData().getDefaultLabAlphaPbModel().getName());

        } catch (BadLabDataException badLabDataException) {
        }

        try {
            setDefaultAlphaUModelID(getMyReduxLabData().getDefaultLabAlphaUModel().getName());

        } catch (BadLabDataException badLabDataException) {
        }

        this.defaultTracerMassText = "0.0000";

        setDefaultFractionMassText("0.0000");

        setDefaultUBlankMassText(getMyReduxLabData().getDefaultAssumedUBlankMassInGrams().
                getValue().multiply(ReduxConstants.PicoGramsPerGram).
                setScale(ReduxConstants.DEFAULT_MASS_DISPLAY_SCALE,
                        RoundingMode.HALF_UP).toPlainString());

        setDefaultR238_235sText(getMyReduxLabData().getDefaultR238_235s()//
                .getValue().setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE,
                        RoundingMode.HALF_UP).toPlainString());

        setDefaultR238_235bText(getMyReduxLabData().getDefaultR238_235b()//
                .getValue().setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE,
                        RoundingMode.HALF_UP).toPlainString());

        setDefaultR18O_16OText(getMyReduxLabData().getDefaultR18O_16O()//
                .getValue().setScale(ReduxConstants.DEFAULT_MASS_DISPLAY_SCALE,
                        RoundingMode.HALF_UP).toPlainString());

        try {
            setDefaultPbBlankID(getMyReduxLabData().getDefaultLabPbBlank().getReduxLabDataElementName());

        } catch (BadLabDataException badLabDataException) {
        }

        try {
            setDefaultInitialPbModelID(getMyReduxLabData().getDefaultLabInitialPbModel().getReduxLabDataElementName());

        } catch (BadLabDataException badLabDataException) {
        }

        setDefaultPbBlankMassText(getMyReduxLabData().getDefaultPbBlankMassInGrams().
                getValue().multiply(ReduxConstants.PicoGramsPerGram).
                setScale(ReduxConstants.DEFAULT_MASS_DISPLAY_SCALE,
                        RoundingMode.HALF_UP).toPlainString());

        setDefaultRTh_UmagmaText(getMyReduxLabData().getDefaultRTh_Umagma().
                getValue().setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE,
                        RoundingMode.HALF_UP).toPlainString());

        setDefaultAr231_235sampleText(getMyReduxLabData().getDefaultAr231_235sample().
                getValue().setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE,
                        RoundingMode.HALF_UP).toPlainString());

        setDefaultEstimatedDateText("0.0000");

        setDefaultStaceyKramersOnePctUnctText(getMyReduxLabData().getDefaultStaceyKramersOnePctUnct().//
                setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE,
                        RoundingMode.HALF_UP).toPlainString());

        setDefaultStaceyKramersCorrelationCoeffsText(getMyReduxLabData().getDefaultStaceyKramersCorrelationCoeffs().//
                setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE,
                        RoundingMode.HALF_UP).toPlainString());

        // uncertainties
        setDefaultTracerMassOneSigmaText(getMyReduxLabData().getDefaultTracerMass()//
                .getOneSigmaAbs().setScale(ReduxConstants.DEFAULT_MASS_DISPLAY_SCALE,
                        RoundingMode.HALF_UP).toPlainString());

        setDefaultUBlankMassOneSigmaText(getMyReduxLabData().getDefaultAssumedUBlankMassInGrams()//
                .getOneSigmaAbs().multiply(ReduxConstants.PicoGramsPerGram)//
                .setScale(ReduxConstants.DEFAULT_MASS_DISPLAY_SCALE,
                        RoundingMode.HALF_UP).toPlainString());

        setDefaultR238_235sOneSigmaText(getMyReduxLabData().getDefaultR238_235s()//
                .getOneSigmaAbs().setScale(ReduxConstants.DEFAULT_MASS_DISPLAY_SCALE,
                        RoundingMode.HALF_UP).toPlainString());

        this.defaultR238_235bOneSigmaText = getMyReduxLabData().getDefaultR238_235b()//
                .getOneSigmaAbs().setScale(ReduxConstants.DEFAULT_MASS_DISPLAY_SCALE,
                        RoundingMode.HALF_UP).toPlainString();

        this.defaultPbBlankMassOneSigmaText = getMyReduxLabData().getDefaultPbBlankMassInGrams()//
                .getOneSigmaAbs().multiply(ReduxConstants.PicoGramsPerGram).//
                setScale(ReduxConstants.DEFAULT_MASS_DISPLAY_SCALE,
                        RoundingMode.HALF_UP).toPlainString();

        // TODO there is some odd duplication with this getters setters
        setDefaultR18O_16OOneSigmaText(getMyReduxLabData().getDefaultR18O_16O()//
                .getOneSigmaAbs().setScale(ReduxConstants.DEFAULT_MASS_DISPLAY_SCALE,
                        RoundingMode.HALF_UP).toPlainString());

        this.defaultRTh_UmagmaOneSigmaText = getMyReduxLabData().getDefaultRTh_Umagma()//
                .getOneSigmaAbs().setScale(ReduxConstants.DEFAULT_MASS_DISPLAY_SCALE,
                        RoundingMode.HALF_UP).toPlainString();

        this.defaultAr231_235sampleOneSigmaText = getMyReduxLabData().getDefaultAr231_235sample()//
                .getOneSigmaAbs().setScale(ReduxConstants.DEFAULT_MASS_DISPLAY_SCALE,
                        RoundingMode.HALF_UP).toPlainString();

        this.automaticDataUpdateMode = false;

        this.containingSampleDataFolder = null;

        this.mySESARSampleMetadata = mySESARSampleMetadata;

//        this.isValidatedSESARChild = false;
        analysisImages = new ArrayList<AnalysisImage>();

    }

//////    @Override
//////    public int compareTo ( Aliquot aliquot ) throws ClassCastException {
//////        String aliquotTwoName = aliquot.getAliquotName().trim();
//////        String aliquotOneName = this.getAliquotName().trim();
//////
//////        // oct 2010 put here
//////        Comparator<String> forNoah = new IntuitiveStringComparator<String>();
//////        return forNoah.compare( aliquotOneName, aliquotTwoName );
//////
//////    }
//////
//////    /**
//////     *
//////     * @param fraction
//////     * @return
//////     */
//////    @Override
//////    public boolean equals ( Object aliquot ) {
//////        //check for self-comparison
//////        if ( this == aliquot ) {
//////            return true;
//////        }
//////        if (  ! (aliquot instanceof Aliquot) ) {
//////            return false;
//////        }
//////
//////        Aliquot myAliquot = (Aliquot) aliquot;
//////
//////        // oct 2010 put here
//////        Comparator<String> forNoah = new IntuitiveStringComparator<String>();
//////        return forNoah.compare( this.getAliquotName().trim(), myAliquot.getAliquotName().trim() ) == 0;
//////
//////    }
//////
//////    // http://www.javaworld.com/javaworld/jw-01-1999/jw-01-object.html?page=4
//////    /**
//////     *
//////     * @return
//////     */
//////    @Override
//////    public int hashCode () {
//////
//////        int hash = 1;
//////        hash = hash * 31 + ((Aliquot)this).getAliquotName().hashCode();
//////        return hash;
//////    }
    /**
     *
     */
    public void reduceData() {
        // may 2014 modified to determine best date  
        ArrayList<Double> sorted206_238 = new ArrayList<>();
        for (Fraction f : getAliquotFractions()) {
            // APRIL 2014 WARNING FOR NOW DO NOT MAKE CALL TO STATIC METHOD DIRECTLY so that parameters can be assembled ... need to refactor
            UPbFractionReducer.getInstance().fullFractionReduce(f, true);

            ValueModel date206_238r = f.getRadiogenicIsotopeDateByName(RadDates.age206_238r);
            // pick out the dates in the likely range for spliting 500 - 1100 MA
            if ((date206_238r.getValue().movePointLeft(6).compareTo(new BigDecimal(500)) > 0)//8
                    && //
                    (date206_238r.getValue().movePointLeft(6).compareTo(new BigDecimal(1100)) < 0)) {
                sorted206_238.add(date206_238r.getValue().doubleValue());
            }
        }

        if (bestAgeDivider206_238 == null){
            // backwards compatible
            bestAgeDivider206_238 = BigDecimal.ZERO;
        }
        if (bestAgeDivider206_238.compareTo(BigDecimal.ZERO) == 0) {
            // if not done previously
            //  sort the collection of the 206_238 dates between 500 ma and 1100 ma
            Collections.sort(sorted206_238);
            double maxGap = 0.0;
            double middleMaxGap = 0.0;
            for (int i = 0; i < sorted206_238.size() - 1; i++) {
                if ((sorted206_238.get(i + 1) - sorted206_238.get(i)) > maxGap) {
                    maxGap = sorted206_238.get(i + 1) - sorted206_238.get(i);
                    middleMaxGap = sorted206_238.get(i) + maxGap / 2.0;
                }
            }
            bestAgeDivider206_238 = new BigDecimal(middleMaxGap == 0.0 ? 800000000.0 : middleMaxGap);
            System.out.println("BEST DATE DIVIDER ma = " + bestAgeDivider206_238.movePointLeft(6).toPlainString());

            updateBestAge();
        }

    }

    /**
     *
     */
    public void updateBestAge() {
        // now set best age
        for (Fraction f : getAliquotFractions()) {
            if (f.getRadiogenicIsotopeDateByName(RadDates.age206_238r).getValue().compareTo(bestAgeDivider206_238) < 0) {
                ValueModel bestDate = f.getRadiogenicIsotopeDateByName(RadDates.age206_238r).copy();
                bestDate.setName(RadDates.bestAge.getName());
                f.setRadiogenicIsotopeDateByName(RadDates.bestAge, bestDate);
            } else {
                ValueModel bestDate = f.getRadiogenicIsotopeDateByName(RadDates.age207_206r).copy();
                bestDate.setName(RadDates.bestAge.getName());
                f.setRadiogenicIsotopeDateByName(RadDates.bestAge, bestDate);
            }

            // Pbc Corrected
            if (f.getRadiogenicIsotopeDateByName(RadDates.age206_238_PbcCorr).getValue().compareTo(bestAgeDivider206_238) < 0) {
                ValueModel bestDate = f.getRadiogenicIsotopeDateByName(RadDates.age206_238_PbcCorr).copy();
                bestDate.setName(RadDates.bestAge_PbcCorr.getName());
                f.setRadiogenicIsotopeDateByName(RadDates.bestAge_PbcCorr, bestDate);
            } else {
                ValueModel bestDate = f.getRadiogenicIsotopeDateByName(RadDates.age207_206_PbcCorr).copy();
                bestDate.setName(RadDates.bestAge_PbcCorr.getName());
                f.setRadiogenicIsotopeDateByName(RadDates.bestAge_PbcCorr, bestDate);
            }
        }
    }

    /**
     *
     * @return
     */
    public String reportFractionMeasuredRatioUncertaintiesValidity() {
        String retval = "";

        for (Fraction f : getActiveAliquotFractions()) {
            try {
                retval += ((UPbFraction) f).getReductionHandler().getMeasuredRatioUncertaintiesValidity();
            } catch (Exception e) {
                retval += "";
            }
        }

        return retval;
    }

    // XML Serialization********************************************************
    /**
     *
     */
    public void setClassXMLSchemaURL() {
        UPbReduxConfigurator myConfigurator = new UPbReduxConfigurator();

        aliquotXMLSchemaURL
                = myConfigurator.getResourceURI("URI_AliquotXMLSchema");
    }

    /**
     * Creates XStream for writing XML
     *
     * @return XStream object
     * @see XStream
     */
    public XStream getXStreamWriter() {

        XStream xstream = new XStream();//new Sun14ReflectionProvider(new FieldDictionary(sorter)));

        customizeXstream(xstream);

        return xstream;
    }

    /**
     *
     * @param xstream
     */
    public void customizeXstream(XStream xstream) {

        xstream.registerConverter(new UPbReduxAliquotXMLConverter());
        xstream.registerConverter(new AnalysisImageXMLConverter());

        xstream.registerConverter(new AnalysisFractionXMLConverter());
        xstream.registerConverter(new MineralStandardUPbModelXMLConverter());
        xstream.registerConverter(new PhysicalConstantsXMLConverter());
        xstream.registerConverter(new PhysicalConstantsModelXMLConverter());

        xstream.registerConverter(new PbBlankICModelXMLConverter());
        xstream.registerConverter(new TracerUPbModelXMLConverter());

        xstream.registerConverter(new InitialPbModelETXMLConverter());
        xstream.registerConverter(new ValueModelXMLConverter());
        xstream.registerConverter(new ValueModelReferencedXMLConverter());
        xstream.registerConverter(new MeasuredRatioModelXMLConverter());
        xstream.registerConverter(new SampleDateModelXMLConverter());
        xstream.registerConverter(new SampleDateInterceptModelXMLConverter());
        xstream.registerConverter(new MineralStandardUPbRatioModelXMLConverter());

        xstream.alias("Aliquot", UPbReduxAliquot.class);
        xstream.alias("AnalysisImage", AnalysisImage.class);

        xstream.alias("AnalysisFraction", AnalysisFraction.class);
        xstream.alias("MineralStandardUPbModel", MineralStandardUPbModel.class);
        xstream.alias("PhysicalConstantsModel", PhysicalConstantsModel.class);

        xstream.alias("PbBlankICModel", PbBlankICModel.class);

        xstream.alias("TracerUPbModel", TracerUPbModel.class);

        xstream.alias("InitialPbModelET", InitialPbModelET.class);
        xstream.alias("ValueModel", ValueModel.class);
        xstream.alias("ValueModelReferenced", ValueModelReferenced.class);
        xstream.alias("MeasuredRatioModel", MeasuredRatioModel.class);
        xstream.alias("SampleDateModel", SampleDateModel.class);
        xstream.alias("SampleDateInterceptModel", SampleDateInterceptModel.class);
        xstream.alias("MineralStandardUPbRatioModel", MineralStandardUPbRatioModel.class);

        xstream.alias("SESARSampleMetadata", SESARSampleMetadata.class);

        setClassXMLSchemaURL();
    }

    /**
     *
     * @return
     */
    public String serializeXMLObject() {
        prepareUPbReduxAliquotForXMLSerialization();

        XStream xstream = getXStreamWriter();

        String xml = xstream.toXML(this);

        xml = ReduxConstants.XML_Header + xml;

        xml = xml.replaceFirst("Aliquot",
                "Aliquot  "//
                + ReduxConstants.XML_ResourceHeader//
                + aliquotXMLSchemaURL///
                + "\"");

        return xml;
    }

    /**
     *
     * @param filename
     */
    public void serializeXMLObject(String filename) {

        String xml = serializeXMLObject();

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
     * @return
     */
    public XStream getXStreamReader() {

        XStream xstream = new XStream(new DomDriver());

        customizeXstream(xstream);

        return xstream;
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
    public Object readXMLObject(String filename, boolean doValidate)
            throws FileNotFoundException,
            ETException,
            FileNotFoundException,
            BadOrMissingXMLSchemaException {

        Aliquot myAliquot = null;

        BufferedReader reader = URIHelper.getBufferedReader(filename);

        if (reader != null) {
            boolean isValidOrAirplaneMode = !doValidate;

            XStream xstream = getXStreamReader();

            if (doValidate) {
                isValidOrAirplaneMode = URIHelper.validateXML(reader, filename, aliquotXMLSchemaURL);
            }
//            else {
//                isValidOrAirplaneMode = true;
//            }

            if (isValidOrAirplaneMode) {
                // re-create reader
                reader = URIHelper.getBufferedReader(filename);
                try {
                    myAliquot = (Aliquot) xstream.fromXML(reader);
                } catch (ConversionException e) {
                    throw new ETException(null, e.getMessage());
                }
            } else {
                throw new ETException(null, "XML data file does not conform to schema.");
            }
        } else {
            throw new FileNotFoundException("Missing XML data file.");
        }

        return myAliquot;
    }

    /**
     * Extracts unique Tracers and PbBlanks from fractions and also processes
     * fractions into ANalysisFractions for publication.
     *
     */
    public synchronized void prepareUPbReduxAliquotForXMLSerialization() {
        // populate with blanks and tracers actually used in this aliquot by scanning all the fractions
        // setup isValidOrAirplaneMode Set objects to prevent repeat entries
        Set<AbstractRatiosDataModel> tempBlanks = new TreeSet<>();
        Set<AbstractRatiosDataModel> tempTracers = new TreeSet<>();
        Set<ValueModel> tempAlphaPbModels = new TreeSet<>();
        Set<ValueModel> tempAlphaUModels = new TreeSet<>();

        getAnalysisFractions().clear();

        // note fractions and SampleDateModels are already unique
        Collections.sort(aliquotFractions);
        if (sampleDateModels != null){
            Collections.sort(sampleDateModels);
        }

        Iterator it = getAliquotFractions().iterator();
        while (it.hasNext()) {
            Fraction fraction = (Fraction) it.next();
            if (!((UPbFractionI) fraction).isRejected()) {
                // april 2010 differentiate between UPbFractions and UPbLegacyFractions
                if (fraction instanceof UPbFraction) {
                    tempBlanks.add(((UPbFractionI) fraction).getPbBlank());
                    tempTracers.add(((UPbFractionI) fraction).getTracer());
                    tempAlphaPbModels.add(((UPbFractionI) fraction).getAlphaPbModel());
                    tempAlphaUModels.add(((UPbFractionI) fraction).getAlphaUModel());

                    if (fraction.getImageURL().startsWith("http://thevaccinator.com/earth-time.org/public-data/images/ZirconCrystal.jpg")) {
                        fraction.setImageURL("");
                    }
                }
                getAnalysisFractions().add(new AnalysisFraction(fraction, isCompiled()));
            }
        }

        // populate unique Tracers and PbBlanks
        getPbBlanks().clear();

        it = tempBlanks.iterator();
        while (it.hasNext()) {
            getPbBlanks().add((AbstractRatiosDataModel) it.next());
        }

        getTracers().clear();

        it = tempTracers.iterator();
        while (it.hasNext()) {
            getTracers().add((AbstractRatiosDataModel) it.next());
        }

        getAlphaPbModels().clear();

        it = tempAlphaPbModels.iterator();
        while (it.hasNext()) {
            getAlphaPbModels().add((ValueModel) it.next());
        }

        getAlphaUModels().clear();

        it = tempAlphaUModels.iterator();
        while (it.hasNext()) {
            getAlphaUModels().add((ValueModel) it.next());
        }
    }

    /**
     *
     */
    public void initializeFractionReductionHandlers() {
        for (Fraction f : getAliquotFractions()) {
            //((UPbFraction) f).initializeReductionHandler();
            UPbFractionReducer.getInstance().fullFractionReduce(f, true);
        }
    }

    /**
     *
     * @return
     */
    public int getAliquotNumber() {
        return aliquotNumber;
    }

    /**
     *
     * @param aliquotNumber
     */
    public void setAliquotNumber(int aliquotNumber) {
        this.aliquotNumber = aliquotNumber;
    }

    /**
     *
     * @return
     */
    public Vector<Fraction> getAliquotFractions() {
        return aliquotFractions;
    }

    /**
     *
     * @return
     */
    public Vector<Fraction> getActiveAliquotFractions() {
        Vector<Fraction> retVal = new Vector<Fraction>();
        for (Fraction f : aliquotFractions) {
            if (!((UPbFractionI) f).isRejected()) {
                retVal.add(f);
            }
        }
        return retVal;
    }

    /**
     *
     * @return
     */
    public Vector<String> getAliquotFractionIDs() {
        Vector<String> retVal = new Vector<String>();
        for (Fraction f : aliquotFractions) {
            if (!((UPbFractionI) f).isRejected()) {
                retVal.add(f.getFractionID());
            }
        }
        return retVal;
    }

    /**
     *
     * @param name
     * @return
     */
    public Fraction getAliquotFractionByName(String name) {
        Fraction retVal = null;
        for (Fraction f : getAliquotFractions()) {
            if (f.getFractionID().equalsIgnoreCase(name)) {
                retVal = f;
            }
        }
        return retVal;
    }

    /**
     *
     * @param selectedFractionIDs
     * @return
     */
    public Vector<Fraction> getAliquotSampleDateModelSelectedFractions(Vector<String> selectedFractionIDs) {
        Vector<Fraction> retVal = new Vector<Fraction>();
        for (String fID : selectedFractionIDs) {
            retVal.add(getAliquotFractionByName(fID));
        }

        return retVal;
    }

    /**
     *
     * @param selectedFractionIDs
     * @return
     */
    public Vector<Fraction> getAliquotSampleDateModelDeSelectedFractions(Vector<String> selectedFractionIDs) {
        Vector<Fraction> retVal = new Vector<Fraction>();
        for (String fID : getAliquotFractionIDs()) {
            if (!selectedFractionIDs.contains(fID)) {
                retVal.add(getAliquotFractionByName(fID));
            }
        }

        return retVal;
    }

    /**
     *
     * @param aliquotFractions
     */
    public void setAliquotFractions(Vector<Fraction> aliquotFractions) {
        this.aliquotFractions = aliquotFractions;
    }

    /**
     *
     * @return
     */
    public ReduxLabData getMyReduxLabData() {
        return myReduxLabData;
    }

    /**
     *
     * @param myReduxLabData
     */
    public void setMyReduxLabData(ReduxLabData myReduxLabData) {
        this.myReduxLabData = myReduxLabData;
    }

    @Override
    public void setPhysicalConstants(AbstractRatiosDataModel physicalConstants) {
        super.setPhysicalConstants(physicalConstants);

        // all existing fractions must be updated
        if (getAliquotFractions() != null) {
            for (int i = 0; i < getAliquotFractions().size(); i++) {
                ((UPbFractionI) getAliquotFractions().get(i)).setPhysicalConstantsModel(physicalConstants);
            }
        }
    }

    /**
     *
     * @return
     */
    public String getDefaultTracerID() {
        return defaultTracerID;
    }

    /**
     *
     * @param defaultTracerID
     */
    public void setDefaultTracerID(String defaultTracerID) {
        this.defaultTracerID = defaultTracerID;
    }

    /**
     *
     * @return
     */
    public String getDefaultPbBlankID() {
        return defaultPbBlankID;
    }

    /**
     *
     * @param defaultPbBlankID
     */
    public void setDefaultPbBlankID(String defaultPbBlankID) {
        this.defaultPbBlankID = defaultPbBlankID;
    }

    /**
     *
     * @return
     */
    public String getDefaultInitialPbModelID() {
        return defaultInitialPbModelID;
    }

    /**
     *
     * @return
     */
    public AbstractRatiosDataModel getDefaultInitialPbModel() {
        AbstractRatiosDataModel retVal = null;
        try {
            retVal = getMyReduxLabData().getAnInitialPbModel(defaultInitialPbModelID);
        } catch (BadLabDataException badLabDataException) {
        }
        return retVal;
    }

    /**
     *
     * @param defaultInitialPbModelID
     */
    public void setDefaultInitialPbModelID(String defaultInitialPbModelID) {
        this.defaultInitialPbModelID = defaultInitialPbModelID;
    }

    /**
     *
     * @return
     */
    public String getDefaultTracerMassText() {
        return defaultTracerMassText;
    }

    /**
     *
     * @param defaultTracerMassText
     */
    public void setDefaultTracerMassText(String defaultTracerMassText) {
        this.defaultTracerMassText = defaultTracerMassText;
    }

    /**
     *
     * @return
     */
    public String getDefaultPbBlankMassText() {
        return defaultPbBlankMassText;
    }

    /**
     *
     * @param defaultBlankPbMassText
     */
    public void setDefaultPbBlankMassText(String defaultBlankPbMassText) {
        this.defaultPbBlankMassText = defaultBlankPbMassText;
    }

    /**
     *
     * @return
     */
    public String getDefaultUBlankMassText() {
        return defaultUBlankMassText;
    }

    /**
     *
     * @param defaultBlankUMassText
     */
    public void setDefaultUBlankMassText(String defaultBlankUMassText) {
        this.defaultUBlankMassText = defaultBlankUMassText;
    }

    /**
     *
     * @return
     */
    public String getDefaultFractionMassText() {
        return defaultFractionMassText;
    }

    /**
     *
     * @param defaultFractionMassText
     */
    public void setDefaultFractionMassText(String defaultFractionMassText) {
        this.defaultFractionMassText = defaultFractionMassText;
    }

    /**
     *
     * @return
     */
    public String getDefaultEstimatedDateText() {
        return defaultEstimatedDateText;
    }

    /**
     *
     * @param defaultEstimatedDateText
     */
    public void setDefaultEstimatedDateText(String defaultEstimatedDateText) {
        this.defaultEstimatedDateText = defaultEstimatedDateText;
    }

    /**
     *
     * @return
     */
    public String getDefaultR238_235sText() {
        return defaultR238_235sText;
    }

    /**
     *
     * @param defaultR238_235sText
     */
    public void setDefaultR238_235sText(String defaultR238_235sText) {
        this.defaultR238_235sText = defaultR238_235sText;
    }

    /**
     *
     * @return
     */
    public String getDefaultR238_235bText() {
        return defaultR238_235bText;
    }

    /**
     *
     * @param defaultR238_235bText
     */
    public void setDefaultR238_235bText(String defaultR238_235bText) {
        this.defaultR238_235bText = defaultR238_235bText;
    }

    /**
     *
     * @return
     */
    public String getDefaultR18O_16OText() {
        return defaultR18O_16OText;
    }

    /**
     *
     * @param default18O_16OText
     */
    public void setDefaultR18O_16OText(String default18O_16OText) {
        this.defaultR18O_16OText = default18O_16OText;
    }

    /**
     *
     * @return
     */
    public String getDefaultTracerMassOneSigmaText() {
        return defaultTracerMassOneSigmaText;
    }

    /**
     *
     * @param defaultTracerMassOneSigmaText
     */
    public void setDefaultTracerMassOneSigmaText(String defaultTracerMassOneSigmaText) {
        this.defaultTracerMassOneSigmaText = defaultTracerMassOneSigmaText;
    }

    /**
     *
     * @return
     */
    public String getDefaultUBlankMassOneSigmaText() {
        return defaultUBlankMassOneSigmaText;
    }

    /**
     *
     * @param defaultUBlankMassOneSigmaText
     */
    public void setDefaultUBlankMassOneSigmaText(String defaultUBlankMassOneSigmaText) {
        this.defaultUBlankMassOneSigmaText = defaultUBlankMassOneSigmaText;
    }

    /**
     *
     * @return
     */
    public String getDefaultR238_235sOneSigmaText() {
        return defaultR238_235sOneSigmaText;
    }

    /**
     *
     * @param defaultR238_235sOneSigmaText
     */
    public void setDefaultR238_235sOneSigmaText(String defaultR238_235sOneSigmaText) {
        this.defaultR238_235sOneSigmaText = defaultR238_235sOneSigmaText;
    }

    /**
     *
     * @return
     */
    public String getDefaultR238_235bOneSigmaText() {
        return defaultR238_235bOneSigmaText;
    }

    /**
     *
     * @param defaultR238_235bOneSigmaText
     */
    public void setDefaultR238_235bOneSigmaText(String defaultR238_235bOneSigmaText) {
        this.defaultR238_235bOneSigmaText = defaultR238_235bOneSigmaText;
    }

    /**
     *
     * @return
     */
    public String getDefaultPbBlankMassOneSigmaText() {
        return defaultPbBlankMassOneSigmaText;
    }

    /**
     *
     * @param defaultPbBlankMassOneSigmaText
     */
    public void setDefaultPbBlankMassOneSigmaText(String defaultPbBlankMassOneSigmaText) {
        this.defaultPbBlankMassOneSigmaText = defaultPbBlankMassOneSigmaText;
    }

    /**
     *
     * @return
     */
    public String getDefaultR18O_16OOneSigmaText() {
        return getDefault18O_16OOneSigmaText();
    }

    /**
     *
     * @param default18O_16OOneSigmaText
     */
    public void setDefaultR18O_16OOneSigmaText(String default18O_16OOneSigmaText) {
        this.setDefault18O_16OOneSigmaText(default18O_16OOneSigmaText);
    }

    /**
     *
     * @return
     */
    public boolean getDefaultIsZircon() {
        return defaultIsZircon;
    }

    /**
     *
     * @param defaultIsZircon
     */
    public void setDefaultIsZircon(boolean defaultIsZircon) {
        this.defaultIsZircon = defaultIsZircon;
    }

    /**
     *
     * @return
     */
    public boolean isCompiled() {
        return compiled;
    }

    /**
     *
     * @param compiled
     */
    public void setCompiled(boolean compiled) {
        this.compiled = compiled;
    }

    /**
     * @return the defaultRTh_UmagmaText
     */
    public String getDefaultRTh_UmagmaText() {
        return defaultRTh_UmagmaText;
    }

    /**
     * @param defaultRTh_UmagmaText the defaultRTh_UmagmaText to set
     */
    public void setDefaultRTh_UmagmaText(String defaultRTh_UmagmaText) {
        this.defaultRTh_UmagmaText = defaultRTh_UmagmaText;
    }

    /**
     * @return the defaultAr231_235sampleText
     */
    public String getDefaultAr231_235sampleText() {
        return defaultAr231_235sampleText;
    }

    /**
     * @param defaultAr231_235sampleText the defaultAr231_235sampleText to set
     */
    public void setDefaultAr231_235sampleText(String defaultAr231_235sampleText) {
        this.defaultAr231_235sampleText = defaultAr231_235sampleText;
    }

    /**
     * @return the default18O_16OOneSigmaText
     */
    public String getDefault18O_16OOneSigmaText() {
        return default18O_16OOneSigmaText;
    }

    /**
     * @param default18O_16OOneSigmaText the default18O_16OOneSigmaText to set
     */
    public void setDefault18O_16OOneSigmaText(String default18O_16OOneSigmaText) {
        this.default18O_16OOneSigmaText = default18O_16OOneSigmaText;
    }

    /**
     * @return the defaultRTh_UmagmaOneSigmaText
     */
    public String getDefaultRTh_UmagmaOneSigmaText() {
        return defaultRTh_UmagmaOneSigmaText;
    }

    /**
     * @param defaultRTh_UmagmaOneSigmaText the defaultRTh_UmagmaOneSigmaText to
     * set
     */
    public void setDefaultRTh_UmagmaOneSigmaText(String defaultRTh_UmagmaOneSigmaText) {
        this.defaultRTh_UmagmaOneSigmaText = defaultRTh_UmagmaOneSigmaText;
    }

    /**
     * @return the defaultAr231_235sampleOneSigmaText
     */
    public String getDefaultAr231_235sampleOneSigmaText() {
        return defaultAr231_235sampleOneSigmaText;
    }

    /**
     * @param defaultAr231_235sampleOneSigmaText the
     * defaultAr231_235sampleOneSigmaText to set
     */
    public void setDefaultAr231_235sampleOneSigmaText(String defaultAr231_235sampleOneSigmaText) {
        this.defaultAr231_235sampleOneSigmaText = defaultAr231_235sampleOneSigmaText;
    }

    /**
     * @return the defaultStaceyKramersOnePctUnctText
     */
    public String getDefaultStaceyKramersOnePctUnctText() {
        if (defaultStaceyKramersOnePctUnctText == null) {
            defaultStaceyKramersOnePctUnctText = "0.000";
        }
        return defaultStaceyKramersOnePctUnctText;
    }

    /**
     * @param defaultStaceyKramersOnePctUnctText the
     * defaultStaceyKramersOnePctUnctText to set
     */
    public void setDefaultStaceyKramersOnePctUnctText(String defaultStaceyKramersOnePctUnctText) {
        this.defaultStaceyKramersOnePctUnctText = defaultStaceyKramersOnePctUnctText;
    }

    /**
     * @return the defaultStaceyKramersCorrelationCoeffsText
     */
    public String getDefaultStaceyKramersCorrelationCoeffsText() {
        if (defaultStaceyKramersCorrelationCoeffsText == null) {
            defaultStaceyKramersCorrelationCoeffsText = "0.000";
        }
        return defaultStaceyKramersCorrelationCoeffsText;
    }

    /**
     * @param defaultStaceyKramersCorrelationCoeffsText the
     * defaultStaceyKramersCorrelationCoeffsText to set
     */
    public void setDefaultStaceyKramersCorrelationCoeffsText(String defaultStaceyKramersCorrelationCoeffsText) {
        this.defaultStaceyKramersCorrelationCoeffsText = defaultStaceyKramersCorrelationCoeffsText;
    }

    /**
     * @return the automaticDataUpdateMode
     */
    public boolean isAutomaticDataUpdateMode() {
        return automaticDataUpdateMode;
    }

    /**
     * @param automaticDataUpdateMode the automaticDataUpdateMode to set
     */
    public void setAutomaticDataUpdateMode(boolean automaticDataUpdateMode) {
        this.automaticDataUpdateMode = automaticDataUpdateMode;
    }

    /**
     * @return the containingSampleDataFolder
     */
    public File getContainingSampleDataFolder() {
        return containingSampleDataFolder;
    }

    /**
     * @param containingSampleDataFolder the containingSampleDataFolder to set
     */
    public void setContainingSampleDataFolder(File containingSampleDataFolder) {
        this.containingSampleDataFolder = containingSampleDataFolder;
    }

    /**
     * @return the aliquotFolderTimeStamp
     */
    public Date getAliquotFolderTimeStamp() {
        if (aliquotFolderTimeStamp == null) {
            aliquotFolderTimeStamp = new Date(0L);
        }
        return aliquotFolderTimeStamp;
    }

    /**
     * @param aliquotFolderTimeStamp the aliquotFolderTimeStamp to set
     */
    public void setAliquotFolderTimeStamp(Date aliquotFolderTimeStamp) {
        this.aliquotFolderTimeStamp = aliquotFolderTimeStamp;
    }

    /**
     * @return the defaultAlphaPbModelID
     */
    public String getDefaultAlphaPbModelID() {
        if (defaultAlphaPbModelID == null) {
            try {
                setDefaultAlphaPbModelID(getMyReduxLabData().getDefaultLabAlphaPbModel().getName());

            } catch (BadLabDataException badLabDataException) {
            }
        }
        return defaultAlphaPbModelID;
    }

    /**
     *
     * @return
     */
    public ValueModel getDefaultAlphaPbModel() {
        ValueModel retVal = null;
        try {
            retVal = getMyReduxLabData().getAnAlphaPbModel(getDefaultAlphaPbModelID());
        } catch (BadLabDataException badLabDataException) {
        }
        return retVal;
    }

    /**
     * @param defaultAlphaPbModelID the defaultAlphaPbModelID to set
     */
    public void setDefaultAlphaPbModelID(String defaultAlphaPbModelID) {
        this.defaultAlphaPbModelID = defaultAlphaPbModelID;
    }

    /**
     * @return the defaultAlphaUModelID
     */
    public String getDefaultAlphaUModelID() {
        if (defaultAlphaUModelID == null) {
            try {
                setDefaultAlphaUModelID(getMyReduxLabData().getDefaultLabAlphaUModel().getName());

            } catch (BadLabDataException badLabDataException) {
            }
        }
        return defaultAlphaUModelID;
    }

    /**
     *
     * @return
     */
    public ValueModel getDefaultAlphaUModel() {
        ValueModel retVal = null;
        try {
            retVal = getMyReduxLabData().getAnAlphaUModel(getDefaultAlphaUModelID());
        } catch (BadLabDataException badLabDataException) {
        }
        return retVal;
    }

    /**
     * @param defaultAlphaUModelID the defaultAlphaUModelID to set
     */
    public void setDefaultAlphaUModelID(String defaultAlphaUModelID) {
        this.defaultAlphaUModelID = defaultAlphaUModelID;
    }

    /**
     * @return the mySESARSampleMetadata
     */
    public SESARSampleMetadata getMySESARSampleMetadata() {
        if (mySESARSampleMetadata == null) {
            mySESARSampleMetadata = new SESARSampleMetadata();
        }
        return mySESARSampleMetadata;
    }

    /**
     * @param mySESARSampleMetadata the mySESARSampleMetadata to set
     */
    public void setMySESARSampleMetadata(SESARSampleMetadata mySESARSampleMetadata) {
        this.mySESARSampleMetadata = mySESARSampleMetadata;
    }

////    /**
////     * @return the isValidatedSESARChild
////     */
////    public boolean isIsValidatedSESARChild () {
////        return isValidatedSESARChild;
////    }
////    /**
////     * @param isValidatedSESARChild the isValidatedSESARChild to set
////     */
////    public void setIsValidatedSESARChild ( boolean isValidatedSESARChild ) {
////        this.isValidatedSESARChild = isValidatedSESARChild;
////    }
    /**
     * @return the analysisImages
     */
    public ArrayList<AnalysisImage> getAnalysisImages() {
        if (analysisImages == null) {
            analysisImages = new ArrayList<AnalysisImage>();
        }
        return analysisImages;
    }

    /**
     *
     * @param imageType
     * @return
     */
    public AnalysisImage getAnalysisImageByType(AnalysisImageTypes imageType) {
        // primethe pump
        getAnalysisImages();

        AnalysisImage retVal = null;
        for (int i = 0; i < analysisImages.size(); i++) {
            if (analysisImages.get(i).getImageType().getName().equals(imageType.getName())) {
                retVal = analysisImages.get(i);
                break;
            }
        }

        if (retVal == null) {
            retVal = new AnalysisImage(imageType, "");
            analysisImages.add(retVal);
        }

        return retVal;
    }

    /**
     * @param analysisImages the analysisImages to set
     */
    public void setAnalysisImages(ArrayList<AnalysisImage> analysisImages) {
        this.analysisImages = analysisImages;
    }

    /**
     * @return the selectedInDataTable
     */
    public boolean isSelectedInDataTable() {
        return selectedInDataTable;
    }

    /**
     * @param selectedInDataTable the selectedInDataTable to set
     */
    public void setSelectedInDataTable(boolean selectedInDataTable) {
        this.selectedInDataTable = selectedInDataTable;
    }
    
    /**
     *
     * @return
     */
    public boolean containsActiveFractions(){
        return (!getActiveAliquotFractions().isEmpty());
    }
}
