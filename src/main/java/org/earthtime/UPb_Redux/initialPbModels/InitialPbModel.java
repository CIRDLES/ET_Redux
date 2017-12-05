/*
 * InitialPbModel.java
 *
 * Created on October 9, 2006, 6:05 PM
 *
 *
 * Copyright 2006-2018 James F. Bowring, CIRDLES.org, and Earth-Time.org
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
package org.earthtime.UPb_Redux.initialPbModels;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.reduxLabData.ReduxLabDataListElementI;
import org.earthtime.UPb_Redux.user.UPbReduxConfigurator;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.UPb_Redux.valueModels.ValueModelXMLConverter;
import org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException;
import org.earthtime.archivingTools.URIHelper;
import org.earthtime.dataDictionaries.DataDictionary;
import org.earthtime.exceptions.ETException;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataModels.initialPbModelsET.InitialPbModelET;
import org.earthtime.utilities.DateHelpers;
import org.earthtime.xmlUtilities.XMLSerializationI;

/**
 * Deprecated June 2012.  Needed for compatibility with legacy serializations (archives).
 * Replaced with InitialPbModelET
 * @author James F. Bowring
 */
public class InitialPbModel implements
        InitialPbModelI,
        XMLSerializationI,
        Serializable,
        ReduxLabDataListElementI {

    // Class variables
    private static final long serialVersionUID = 1729534765700996173L;
    private transient String initialPbModelXMLSchemaURL;
    // instance variables
    private String name;
    private String reference;
    private boolean calculated;
    private ValueModel[] ratios;
    private ValueModel[] correlationCoefficients;

    /**
     * Creates a new instance of InitialPbModel
     */
    public InitialPbModel () {
        this.name = ReduxConstants.DEFAULT_OBJECT_NAME;
        this.reference = "NONE";

        this.ratios = new ValueModel[DataDictionary.earthTimeInitialPbModelRatioNames.length];
        for (int i = 0; i < DataDictionary.earthTimeInitialPbModelRatioNames.length; i ++) {
            this.ratios[i] =
                    new ValueModel( DataDictionary.getEarthTimeInitialPbModelRatioNames( i ),
                    BigDecimal.ZERO,
                    "ABS",
                    BigDecimal.ZERO, BigDecimal.ZERO );
        }

        Arrays.sort( ratios );

        this.correlationCoefficients = new ValueModel[DataDictionary.earthTimeInitialPbModelCorrelationCoeffNames.length];
        for (int i = 0; i < DataDictionary.earthTimeInitialPbModelCorrelationCoeffNames.length; i ++) {
            this.correlationCoefficients[i] =
                    new ValueModel( DataDictionary.getEarthTimeInitialPbModelCorrelationCoeffNamesint( i ),
                    BigDecimal.ZERO,
                    "NONE",
                    BigDecimal.ZERO, BigDecimal.ZERO );
        }

        Arrays.sort( correlationCoefficients );

        // this handles de-xml-serialization
        this.calculated = false;
    }

    /**
     *
     * @param name
     */
    public InitialPbModel ( String name ) {
        this();
        this.name = name.trim();
    }

    // used for deserialization
    /**
     *
     * @return
     */
    protected Object readResolve () {

        // march 2012 conversion to new InitialPbModelET
        AbstractRatiosDataModel initialPbModel1;

        if ( (this instanceof StaceyKramersInitialPbModel) //
                || this.calculated //
                || this.getName().toUpperCase().startsWith( "Stacey") ) {
            initialPbModel1 = InitialPbModelET.getStaceyKramersInstance();

        } else {

            if ( (this.name.equalsIgnoreCase( "<none>" )) ) {

                initialPbModel1 = InitialPbModelET.getNoneInstance();
            } else {
                initialPbModel1 = convertModel( this );
            }
        }

        return initialPbModel1;
    }

    /**
     * 
     * @param model
     * @return
     */
    public static AbstractRatiosDataModel convertModel ( InitialPbModel model ) {

        if ( model.isCalculated() ) {
            return InitialPbModelET.getStaceyKramersInstance();
        } else {
            // convert correlations
            Map<String, BigDecimal> correlations = new HashMap<String, BigDecimal>();
            for (int i = 0; i < model.correlationCoefficients.length; i ++) {
                correlations.put(//
                        model.correlationCoefficients[i].getName(),//
                        model.correlationCoefficients[i].getValue() );
            }
            return//
                    InitialPbModelET.createInstance( //
                    model.name, //
                    1, 0, //
                    "Unknown Lab",//
                    DateHelpers.defaultEarthTimeDateString(),//
                    model.reference, //
                    "No comment", //
                    model.ratios, //
                    correlations );
        }
    }

    /**
     *
     * @return
     */
    @Override
    public InitialPbModel copy () {
        InitialPbModel tempModel = new InitialPbModel( getName() );
        copyFieldsTo( tempModel );

        return tempModel;
    }

    /**
     *
     * @param tempModel
     */
    protected void copyFieldsTo ( InitialPbModel tempModel ) {
        tempModel.setReference( getReference() );
        tempModel.setCalculated( isCalculated() );

        ValueModel[] tempRatios = new ValueModel[getRatios().length];
        for (int i = 0; i < getRatios().length; i ++) {
            tempRatios[i] = getRatios()[i].copy();
        }

        tempModel.setRatios( tempRatios );

        ValueModel[] tempRhos = new ValueModel[getCorrelationCoefficients().length];
        for (int i = 0; i < getCorrelationCoefficients().length; i ++) {
            tempRhos[i] = getCorrelationCoefficients()[i].copy();
        }

        tempModel.setCorrelationCoefficients( tempRhos );

    }

    /**
     *
     * @param initialPbModel
     * @return
     * @throws ClassCastException
     */
    public int compareTo ( InitialPbModel initialPbModel ) throws ClassCastException {
        String initialPbModelName = ((InitialPbModel) initialPbModel).getName();
        return this.getName().trim().compareToIgnoreCase( initialPbModelName.trim() );
    }

    /**
     *
     * @param initialPbModel
     * @return
     */
    @Override
    public boolean equals ( Object initialPbModel ) {
        //check for self-comparison
        if ( this == initialPbModel ) {
            return true;
        }
        if (  ! (initialPbModel instanceof InitialPbModel) ) {
            return false;
        }

        InitialPbModel myInitialPbModel = (InitialPbModel) initialPbModel;

        return (this.getName().trim().
                compareToIgnoreCase( myInitialPbModel.getName().trim() ) == 0);

    }

    // http://www.javaworld.com/javaworld/jw-01-1999/jw-01-object.html?page=4
    /**
     *
     * @return
     */
    @Override
    public int hashCode () {

        return 0;
    }

    /**
     *
     * @return
     */
    public String getName () {
        return name;
    }

    /**
     *
     * @return
     */
    public String getReduxLabDataElementName () {
        return getName();
    }

    /**
     *
     * @param name
     */
    public void setName ( String name ) {
        this.name = name.trim();
    }

    /**
     *
     * @return
     */
    public String getReference () {
        return "Stacey, J. C. and Kramers, J., 1975. EPSL 26, pp. 207-221"; // oct 2010 to backwards comp with bad character in xml
    }

    /**
     *
     * @param reference
     */
    public void setReference ( String reference ) {
        this.reference = reference.trim();
    }

    /**
     *
     * @return
     */
    public boolean isCalculated () {
        return calculated;
    }

    /**
     *
     * @param calculated
     */
    public void setCalculated ( boolean calculated ) {
        this.calculated = calculated;
    }

    /**
     *
     * @return
     */
    @Override
    public ValueModel[] getRatios () {
        return ratios;
    }

    /**
     *
     * @param ratios
     */
    @Override
    public void setRatios ( ValueModel[] ratios ) {
        this.ratios = ratios;
    }

    /**
     *
     * @param ratioName
     * @return
     */
    @Override
    public ValueModel getRatioByName ( String ratioName ) {
        for (int i = 0; i < getRatios().length; i ++) {
            if ( getRatios()[i].getName().equals( ratioName ) ) {
                return getRatios()[i];
            }
        }
        return null;
    }

    /**
     * @return the correlationCoefficients
     */
    public ValueModel[] getCorrelationCoefficients () {
        return correlationCoefficients;
    }

    /**
     * @param correlationCoefficients the correlationCoefficients to set
     */
    public void setCorrelationCoefficients ( ValueModel[] correlationCoefficients ) {
        this.correlationCoefficients = correlationCoefficients;
    }

    /**
     *
     * @param estimatedAgeInMA
     * @param lambda238
     * @param lambda235
     * @param lambda232
     */
    @Override
    public void calculateRatios (
            BigDecimal estimatedAgeInMA,
            BigDecimal lambda238,
            BigDecimal lambda235,
            BigDecimal lambda232 ) {
        // do nothing here as ratios are fixed in general, see stacey-kramers
    }

    /**
     *
     * @param oneSigmaPct
     * @param rhos
     */
    public void calculateUncertaintiesAndRhos ( BigDecimal oneSigmaPct, BigDecimal rhos ) {
        // do nothing here as ratios are fixed in general, see stacey-kramers
    }

    /**
     *
     * @param coeffName
     * @return
     */
    public ValueModel getCorrelationCoefficientByName ( String coeffName ) {

        if ( getCorrelationCoefficients() == null ) {
            setCorrelationCoefficients( new ValueModel[0] );
        }

        for (int i = 0; i < getCorrelationCoefficients().length; i ++) {
            if ( getCorrelationCoefficients()[i].getName().equals( coeffName ) ) {
                return getCorrelationCoefficients()[i];
            }
        }

        // return a new model - handles backwards compatible
        // have to add element to array
        ValueModel[] temp = new ValueModel[getCorrelationCoefficients().length + 1];
        for (int i = 0; i < getCorrelationCoefficients().length; i ++) {
            temp[i] = getCorrelationCoefficients()[i];
        }

        ValueModel coeffModel =
                new ValueModel( coeffName,
                BigDecimal.ZERO,
                "NONE",
                BigDecimal.ZERO, BigDecimal.ZERO );

        temp[getCorrelationCoefficients().length] = coeffModel;

        setCorrelationCoefficients( temp );

        return coeffModel;
    }

    // XML Serialization
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
     * @return
     */
    public XStream getXStreamReader () {
        XStream xstream = new XStream( new DomDriver() );

        customizeXstream( xstream );

        return xstream;
    }

    /**
     *
     * @param xstream
     */
    public void customizeXstream ( XStream xstream ) {
        xstream.registerConverter( new InitialPbModelXMLConverter() );
        xstream.registerConverter( new ValueModelXMLConverter() );

        xstream.alias( "InitialPbModel", InitialPbModel.class );
        xstream.alias( "ValueModel", ValueModel.class );

        setClassXMLSchemaURL();
    }

    /**
     *
     */
    public void setClassXMLSchemaURL () {
        UPbReduxConfigurator myConfigurator = new UPbReduxConfigurator();

        initialPbModelXMLSchemaURL =
                myConfigurator.getResourceURI( "URI_InitialPbModelXMLSchema" );
    }

    /**
     *
     * @param filename
     */
    public void serializeXMLObject ( String filename ) {
        XStream xstream = getXStreamWriter();

        String xml = xstream.toXML( this );

        xml = ReduxConstants.XML_Header + xml;

        xml = xml.replaceFirst("InitialPbModel",
                "InitialPbModel "//
                + ReduxConstants.XML_ResourceHeader//
                + initialPbModelXMLSchemaURL +//
                "\"" );


        try {
            FileWriter outFile = new FileWriter( filename );
            PrintWriter out = new PrintWriter( outFile );

            // Write xml to file
            out.println( xml );
            out.flush();
            out.close();
            outFile.close();

        } catch (IOException e) {
        }
    }

    /**
     *
     *
     * @param filename
     * @param doValidate the value of doValidate
     * @return
     * @throws FileNotFoundException
     * @throws ETException
     * @throws BadOrMissingXMLSchemaException
     */
    public Object readXMLObject ( String filename, boolean doValidate )
            throws FileNotFoundException, ETException, FileNotFoundException, BadOrMissingXMLSchemaException {
        InitialPbModel myInitialPbModel = null;

        BufferedReader reader = URIHelper.getBufferedReader( filename );

        if ( reader != null ) {
            boolean temp = false;
            XStream xstream = getXStreamReader();

            temp = URIHelper.validateXML( reader, filename, initialPbModelXMLSchemaURL );

            if ( temp ) {
                // re-create reader
                reader = URIHelper.getBufferedReader( filename );
                try {
                    myInitialPbModel = (InitialPbModel) xstream.fromXML( reader );
                } catch (ConversionException e) {
                    throw new ETException( null, e.getMessage() );
                }

//                System.out.println( "This is your InitialPbModel that was just read successfully:\n" );
//
//                String xml2 = getXStreamWriter().toXML( myInitialPbModel );
//
//                System.out.println( xml2 );
//                System.out.flush();
            }

        } else {
            throw new FileNotFoundException( "Badly formed or missing XML data file." );
        }



        return myInitialPbModel;
    }

    /**
     *
     * @param args
     * @throws Exception
     */
    public static void main ( String[] args ) throws Exception {

//        InitialPbModel initialPbModel = new StaceyKramersInitialPbModel().copy();
//
//        BigDecimal lambda238 = PhysicalConstants.EARTHTIMEPhysicalConstants().getMeasuredConstantByName( Lambdas.lambda238.getName() ).getValue();
//        BigDecimal lambda235 = PhysicalConstants.EARTHTIMEPhysicalConstants().getMeasuredConstantByName( Lambdas.lambda235.getName() ).getValue();
//        BigDecimal lambda232 = PhysicalConstants.EARTHTIMEPhysicalConstants().getMeasuredConstantByName( Lambdas.lambda232.getName() ).getValue();
//
//
//        initialPbModel.calculateRatios( new BigDecimal( 60.0, ReduxConstants.mathContext15 ), lambda238, lambda235, lambda232 );
//        initialPbModel.calculateUncertaintiesAndRhos( new BigDecimal( 0.02, ReduxConstants.mathContext15 ), new BigDecimal( 0.50 ) );
//
//        String testFileName = "InitialPbModelTEST.xml";
//
//        initialPbModel.serializeXMLObject( testFileName );
//        InitialPbModel initialPbModelR = (InitialPbModel) initialPbModel.readXMLObject( testFileName, true );
//
//        System.out.println( initialPbModelR.name );

    }


    /**
     * 
     */
    @Override
    public void removeSelf () {
//        throw new UnsupportedOperationException( "Not supported yet." );
    }
}
