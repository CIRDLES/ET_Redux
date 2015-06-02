/*
 * AnalysisFraction.java
 *
 * Created on August 3, 2007, 11:16 AM
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
package org.earthtime.UPb_Redux.fractions;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.*;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFraction;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.fractionReduction.UPbFractionReducer;
import org.earthtime.UPb_Redux.mineralStandardModels.MineralStandardModelXMLConverter;
import org.earthtime.UPb_Redux.user.UPbReduxConfigurator;
import org.earthtime.UPb_Redux.valueModels.MeasuredRatioModel;
import org.earthtime.UPb_Redux.valueModels.MeasuredRatioModelXMLConverter;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.UPb_Redux.valueModels.ValueModelXMLConverter;
import org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException;
import org.earthtime.exceptions.ETException;
import org.earthtime.ratioDataModels.initialPbModelsET.InitialPbModelET;
import org.earthtime.ratioDataModels.initialPbModelsET.InitialPbModelETXMLConverter;
import org.earthtime.archivingTools.URIHelper;
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

    /**
     * Creates a new instance of AnalysisFraction
     */
    public AnalysisFraction () {
        super( "Empty", "NONE" );
    }

    /**
     *
     * @param sampleName
     */
    public AnalysisFraction ( String sampleName ) {
        super( sampleName, "NONE" );
    }

    // Used to create fraction for export and to elide Redux fields in XML
    /**
     *
     * @param fraction
     * @param analyzed
     */
    public AnalysisFraction (
            Fraction fraction,
            boolean analyzed ) {

        this( fraction.getSampleName() );

        this.setFractionID( fraction.getFractionID() );
        this.setGrainID( fraction.getGrainID() );

        this.GetValuesFrom( fraction, true );

        // april 2010 handle UPbLegacyFraction
        if ( fraction instanceof UPbFraction ) {
            setTracerID( ((UPbFraction) fraction).getTracerID() );
            setAlphaPbModelID( ((UPbFraction) fraction).getAlphaPbModelID() );
            setAlphaUModelID( ((UPbFraction) fraction).getAlphaUModelID() );
            setPbBlankID( ((UPbFraction) fraction).getPbBlankID() );
            setPhysicalConstantsModelID( ((UPbFraction) fraction).getPhysicalConstantsModelID() );
        }

        setMeasuredRatios( (MeasuredRatioModel[]) fraction.copyMeasuredRatios() );
    }

    @Override
    public int compareTo ( Fraction fraction ) throws ClassCastException {
        String FractionID = fraction.getFractionID();
        return (this.getFractionID().compareTo( FractionID ));
    }

    // XML Serialization *******************************************************
    /**
     *
     */
    public void setClassXMLSchemaURL () {
        UPbReduxConfigurator myConfigurator = new UPbReduxConfigurator();

        analysisFractionXMLSchemaURL =
                myConfigurator.getResourceURI( "URI_AnalysisFractionXMLSchemaURL" );
    }

    /**
     *
     * @param filename
     */
    public void serializeXMLObject ( String filename ) {

        XStream xstream = getXStreamWriter();

        String xml = xstream.toXML( this );

        xml = ReduxConstants.XML_Header + xml;

        xml = xml.replaceFirst("AnalysisFraction",
                "AnalysisFraction "//
                + ReduxConstants.XML_ResourceHeader//
                + analysisFractionXMLSchemaURL//
                + "\"" );

        try {
            FileWriter outFile = new FileWriter( filename );
            PrintWriter out = new PrintWriter( outFile );

            // Write xml to file
            out.println( xml );
            out.flush();
            out.close();
            outFile.close();

        } catch (IOException e) {
            e.printStackTrace();
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
    public Object readXMLObject ( String filename, boolean doValidate )
            throws FileNotFoundException,
            ETException,
            FileNotFoundException,
            BadOrMissingXMLSchemaException {

        Fraction myFraction = null;

        BufferedReader reader = URIHelper.getBufferedReader( filename );

        if ( reader != null ) {
            boolean isValidOrAirplaneMode = !doValidate;
            
            XStream xstream = getXStreamReader();

            isValidOrAirplaneMode = URIHelper.validateXML( reader, filename, analysisFractionXMLSchemaURL );
            if ( isValidOrAirplaneMode ) {
                // re-create reader
                reader = URIHelper.getBufferedReader( filename );
                try {
                    myFraction = (Fraction) xstream.fromXML( reader );
                } catch (ConversionException e) {
                    throw new ETException( null, e.getMessage() );
                }

                System.out.println( "This is your AnalysisFraction that was just read successfully:\n" );

                String xml2 = getXStreamWriter().toXML( myFraction );

                System.out.println( xml2 );
                System.out.flush();
            } else {
                throw new ETException( null, "XML data file does not conform to schema." );
            }
        } else {
            throw new FileNotFoundException( "Missing XML data file." );
        }

        return myFraction;
    }

    /**
     *
     * @return
     */
    public XStream getXStreamWriter () {

        XStream xstream = new XStream();

        customizeXstream( xstream );

        return xstream;
    }

    /**
     *
     * @param xstream
     */
    public void customizeXstream ( XStream xstream ) {

        xstream.registerConverter( new AnalysisFractionXMLConverter() );
        xstream.registerConverter( new MineralStandardModelXMLConverter() );

        xstream.registerConverter( new InitialPbModelETXMLConverter() );
        xstream.registerConverter( new ValueModelXMLConverter() );
        xstream.registerConverter( new MeasuredRatioModelXMLConverter() );

        // alias necessary to elide fully qualified name in xml8
        xstream.alias( "AnalysisFraction", AnalysisFraction.class );
        xstream.alias( "MeasuredRatioModel", MeasuredRatioModel.class );
        xstream.alias( "InitialPbModelET", InitialPbModelET.class );
        xstream.alias( "ValueModel", ValueModel.class );

        setClassXMLSchemaURL();
    }

    /**
     *
     * @return
     */
    public XStream getXStreamReader () {

        XStream xstream = new XStream( new DomDriver() );

        customizeXstream( xstream );

        return xstream;
    }

    
}
