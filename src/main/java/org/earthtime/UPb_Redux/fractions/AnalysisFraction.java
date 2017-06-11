/*
 * AnalysisFraction.java
 *
 * Created on August 3, 2007, 11:16 AM
 *
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
package org.earthtime.UPb_Redux.fractions;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.awt.geom.Path2D;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFraction;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFractionI;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.fractionReduction.UPbFractionReducer;
import org.earthtime.UPb_Redux.mineralStandardModels.MineralStandardModelXMLConverter;
import org.earthtime.UPb_Redux.user.UPbReduxConfigurator;
import org.earthtime.UPb_Redux.valueModels.MeasuredRatioModel;
import org.earthtime.UPb_Redux.valueModels.MeasuredRatioModelXMLConverter;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.UPb_Redux.valueModels.ValueModelXMLConverter;
import org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException;
import org.earthtime.archivingTools.URIHelper;
import org.earthtime.exceptions.ETException;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataModels.initialPbModelsET.InitialPbModelET;
import org.earthtime.ratioDataModels.initialPbModelsET.InitialPbModelETXMLConverter;
import org.earthtime.xmlUtilities.XMLSerializationI;

/**
 *
 * @author James F. Bowring
 */
public class AnalysisFraction extends Fraction implements
        FractionI,
        XMLSerializationI {

    // Class variables
    private static final long serialVersionUID = -8825197390767165841L;
    private transient String analysisFractionXMLSchemaURL;

    private boolean filtered;

    /**
     * Creates a new instance of AnalysisFraction
     */
    public AnalysisFraction() {
        super("Empty", "NONE");
    }

    /**
     *
     * @param sampleName
     */
    public AnalysisFraction(String sampleName) {
        super(sampleName, "NONE");
    }

    // Used to create fraction for export and to elide Redux fields in XML
    /**
     *
     * @param fraction
     * @param analyzed
     */
    public AnalysisFraction(
            FractionI fraction,
            boolean analyzed) {

        this(fraction.getSampleName());

        this.setFractionID(fraction.getFractionID());
        this.setGrainID(fraction.getGrainID());

        this.getValuesFrom(fraction, true);

        // april 2010 handle UPbLegacyFraction
        if (fraction instanceof UPbFraction) {
            setTracerID(fraction.getTracerID());
            setAlphaPbModelID(fraction.getAlphaPbModelID());
            setAlphaUModelID(fraction.getAlphaUModelID());
            setPbBlankID(((UPbFractionI) fraction).getPbBlankID());
            setPhysicalConstantsModelID(fraction.getPhysicalConstantsModelID());
        }

        setMeasuredRatios(fraction.copyMeasuredRatios());
    }

    @Override
    public int compareTo(Fraction fraction) throws ClassCastException {
        String FractionID = fraction.getFractionID();
        return (this.getFractionID().compareTo(FractionID));
    }

    // XML Serialization *******************************************************
    /**
     *
     */
    public void setClassXMLSchemaURL() {
        UPbReduxConfigurator myConfigurator = new UPbReduxConfigurator();

        analysisFractionXMLSchemaURL
                = myConfigurator.getResourceURI("URI_AnalysisFractionXMLSchemaURL");
    }

    /**
     *
     * @param filename
     */
    @Override
    public void serializeXMLObject(String filename) {

        XStream xstream = getXStreamWriter();

        String xml = xstream.toXML(this);

        xml = ReduxConstants.XML_Header + xml;

        xml = xml.replaceFirst("AnalysisFraction",
                "AnalysisFraction "//
                + ReduxConstants.XML_ResourceHeader//
                + analysisFractionXMLSchemaURL//
                + "\"");

        try {
            try (FileWriter outFile = new FileWriter(filename)) {
                PrintWriter out = new PrintWriter(outFile);

                // Write xml to file
                out.println(xml);
                out.flush();
                out.close();
            }

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

        FractionI myFraction = null;

        BufferedReader reader = URIHelper.getBufferedReader(filename);

        if (reader != null) {
            boolean isValidOrAirplaneMode = !doValidate;

            XStream xstream = getXStreamReader();

            isValidOrAirplaneMode = URIHelper.validateXML(reader, filename, analysisFractionXMLSchemaURL);
            if (isValidOrAirplaneMode) {
                // re-create reader
                reader = URIHelper.getBufferedReader(filename);
                try {
                    myFraction = (FractionI) xstream.fromXML(reader);
                } catch (ConversionException e) {
                    throw new ETException(null, e.getMessage());
                }

//                System.out.println( "This is your AnalysisFraction that was just read successfully:\n" );
//
//                String xml2 = getXStreamWriter().toXML( myFraction );
//
//                System.out.println( xml2 );
//                System.out.flush();
            } else {
                throw new ETException(null, "XML data file does not conform to schema.");
            }
        } else {
            throw new FileNotFoundException("Missing XML data file.");
        }

        return myFraction;
    }

    /**
     *
     * @return
     */
    public XStream getXStreamWriter() {

        XStream xstream = new XStream();

        customizeXstream(xstream);

        return xstream;
    }

    /**
     *
     * @param xstream
     */
    public void customizeXstream(XStream xstream) {

        xstream.registerConverter(new AnalysisFractionXMLConverter());
        xstream.registerConverter(new MineralStandardModelXMLConverter());

        xstream.registerConverter(new InitialPbModelETXMLConverter());
        xstream.registerConverter(new ValueModelXMLConverter());
        xstream.registerConverter(new MeasuredRatioModelXMLConverter());

        // alias necessary to elide fully qualified name in xml8
        xstream.alias("AnalysisFraction", AnalysisFraction.class);
        xstream.alias("MeasuredRatioModel", MeasuredRatioModel.class);
        xstream.alias("InitialPbModelET", InitialPbModelET.class);
        xstream.alias("ValueModel", ValueModel.class);

        setClassXMLSchemaURL();
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
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        UPbReduxConfigurator myConfigurator = new UPbReduxConfigurator();

        FractionI analysisFraction = new UPbFraction("NONE");
        // new AnalysisFraction("Test Sample");

        UPbFractionReducer.getInstance().fullFractionReduce((UPbFraction) analysisFraction, true);

        FractionI myAnalysisFraction = new AnalysisFraction(analysisFraction, false);

        String testFractionName = "AnalysisFractionTEST.xml";

        ((XMLSerializationI) myAnalysisFraction).serializeXMLObject(testFractionName);
        ((XMLSerializationI) myAnalysisFraction).readXMLObject(testFractionName, true);

    }

    @Override
    public String getRatioType() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setRatioType(String RatioType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isChanged() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setChanged(boolean changed) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getAliquotNumber() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setAliquotNumber(int aliquotNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AbstractRatiosDataModel getPhysicalConstantsModel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setPhysicalConstantsModel(AbstractRatiosDataModel physicalConstantsModel) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getFractionNotes() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setFractionNotes(String fractionNotes) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isRejected() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setRejected(boolean rejected) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void toggleRejectedStatus() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isDeleted() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setDeleted(boolean deleted) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Path2D getErrorEllipsePath() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setErrorEllipsePath(Path2D errorEllipsePath) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public double getEllipseRho() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setEllipseRho(double ellipseRho) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isStandard() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setStandard(boolean standard) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isSecondaryStandard() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setSecondaryStandard(boolean secondaryStandard) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isFiltered() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setFiltered(boolean rejected) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ValueModel getTracerRatioByName(String trName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ValueModel getPbBlankRatioByName(String trName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ValueModel getInitialPbModelRatioByName(String trName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AbstractRatiosDataModel getDetritalUThModel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setDetritalUThModel(AbstractRatiosDataModel detritalUThModel) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
