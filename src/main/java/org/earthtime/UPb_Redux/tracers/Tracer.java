/*
 * Tracer.java
 *
 * Created on April 13, 2007, 6:19 AM
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
package org.earthtime.UPb_Redux.tracers;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.reduxLabData.ReduxLabDataListElementI;
import org.earthtime.UPb_Redux.user.UPbReduxConfigurator;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.UPb_Redux.valueModels.ValueModelXMLConverter;
import org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException;
import org.earthtime.archivingTools.URIHelper;
import org.earthtime.dataDictionaries.DataDictionary;
import org.earthtime.dataDictionaries.TracerIsotopes;
import org.earthtime.dataDictionaries.TracerRatiosEnum;
import org.earthtime.exceptions.ETException;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataModels.tracers.TracerUPbModel;
import org.earthtime.utilities.DateHelpers;
import org.earthtime.xmlUtilities.XMLSerializationI;

/**
 * Deprecated June 2012.  Needed for compatibility with legacy serializations (archives).
 * Replaced with TracerUPbModel.
 * @author James F. Bowring, javaDocs by Stan Gasque
 */
public class Tracer implements
        Comparable<Tracer>,
        Serializable,
        XMLSerializationI,
        ReduxLabDataListElementI,
        TracerI {

    // Class variables
    /**
     * identifies object in binary serialization
     */
    private static final long serialVersionUID = -1575105271214161011L;

    /**
     * @return the serialVersionUID
     */
    public static long getSerialVersionUID () {
        return serialVersionUID;
    }
    /**
     * holds URL to find XML schema for storage and retrieval
     */
    private transient String tracerXMLSchemaURL;
    // Instance variables
    /**
     * name of this
     * <code>Tracer</code>
     */
    private String tracerName;
    /**
     * version number of this
     * <code>Tracer</code>
     */
    private int versionNumber;
    /**
     * type of this
     * <code>Tracer</code>
     */
    private String tracerType;
    /**
     * lab name for this
     * <code>Tracer</code>
     */
    private String labName;
    /**
     * date that this
     * <code>Tracer</code> was certified
     */
    private String dateCertified;
    /**
     * collection of ratios for this
     * <code>Tracer</code>
     */
    private ValueModel[] ratios;
    /**
     * collection of isotope concentrations for this
     * <code>Tracer</code>
     */
    private ValueModel[] isotopeConcentrations;

    // Constructors
    /**
     * creates a new instance of Tracer with blank fields.
     */
    public Tracer () {
        this.tracerName = ReduxConstants.DEFAULT_OBJECT_NAME;
        this.versionNumber = 0;
        this.tracerType = DataDictionary.TracerType[0].trim();
        this.labName = ReduxConstants.DEFAULT_OBJECT_NAME;

        this.dateCertified = DateHelpers.defaultEarthTimeDateString();

        ratios = new ValueModel[TracerRatiosEnum.getNames().length];
        for (int ratioIndex = 0; ratioIndex < TracerRatiosEnum.getNames().length; ratioIndex ++) {
            ratios[ratioIndex] = new ValueModel( TracerRatiosEnum.getNames()[ratioIndex], "PCT" );
        }

        Arrays.sort( ratios );

        this.isotopeConcentrations = //
                new ValueModel[TracerIsotopes.getNames().length];  //DataDictionary.isotopeNames.length];
        for (int isotopeIndex = 0; isotopeIndex < TracerIsotopes.getNames().length; isotopeIndex ++) {
            isotopeConcentrations[isotopeIndex] =
                    new ValueModel( TracerIsotopes.getNames()[isotopeIndex], "PCT" );
        }

        Arrays.sort( isotopeConcentrations );
    }

    /**
     * creates a new instance of Tracer with its
     * <code>tracerName</code> set to argument
     * <code>tracerName</code> and all other fields initialized to default
     * values.
     *
     * @param tracerName value to set this new
     * <code>Tracer</code>'s
     * <code>tracerName</code> to
     */
    public Tracer ( String tracerName ) {
        this();
        this.tracerName = tracerName.trim();
    }

    // used for deserialization
    /**
     *
     * @return
     */
    protected Object readResolve () {

        // march 2012 conversion to new TracerUPbModel
        AbstractRatiosDataModel tracerUPbModel;

        if ( (this.tracerName.equalsIgnoreCase( "<none>" )) ) {

            tracerUPbModel = TracerUPbModel.getNoneInstance();
        } else {
            tracerUPbModel = convertModel( this );
        }


        return tracerUPbModel;
    }

    /**
     *
     * @param model
     * @return
     */
    public static AbstractRatiosDataModel convertModel ( Tracer model ) {

        // add isotope concentrations into ratios
        ArrayList<ValueModel> tracerRatios = new ArrayList<ValueModel>();
        tracerRatios.addAll( Arrays.asList( model.ratios ) );
        tracerRatios.addAll( Arrays.asList( model.isotopeConcentrations ) );
        ValueModel[] tracerRatiosArray = tracerRatios.toArray( new ValueModel[tracerRatios.size()] );

        return//
                TracerUPbModel.createInstance( //
                model.tracerName, //
                model.versionNumber, //
                0, //
                model.tracerType,//
                model.labName,//
                model.dateCertified,//
                "No reference", //
                "No comment", //
                tracerRatiosArray, //
                null );
    }

    /**
     * compares this
     * <code>Tracer</code> to argument
     * <code>tracer</code> by their
     * <code>name</code> and
     * <code>version</code>.
     *
     * @pre argument
     * <code>tracer</code> is a valid
     * <code>Tracer</code> @post returns an
     * <code>int</code> representing the comparison between this
     * <code>Tracer</code> and argument
     * <code>tracer</code>
     *
     * @param tracer
     * <code>Tracer</code> to compare this
     * <code>Tracer</code> to
     * @return
     * <code>int</code> - 0 if this
     * <code>Tracer</code>'s
     * <code>name</code> and
     * <code>version</code> is the same as argument
     * <code>tracer</code>'s, -1 if they are lexicographically less than
     * argument
     * <code>tracer</code>'s, and 1 if they are greater than argument
     * <code>tracer</code>'s
     * @throws java.lang.ClassCastException a ClassCastException
     */
    @Override
    public int compareTo ( Tracer tracer ) throws ClassCastException {
        String tracerID =//
                ((Tracer) tracer).getNameAndVersion().trim() //
                + ((Tracer) tracer).getTracerType().trim();
        return (this.getNameAndVersion().trim() //
                + this.getTracerType().trim()).compareToIgnoreCase( tracerID );
    }

    /**
     * compares this
     * <code>Tracer</code> to argument
     * <code>tracer</code> by
     * their
     * <code>name</code> and
     * <code>version</code>.
     *
     * @pre argument
     * <code>tracer</code> is a valid
     * <code>Tracer</code> @post returns a
     * <code>boolean</code> representing the equality of this
     * <code>Tracer</code> and argument
     * <code>tracer</code> based on their
     * <code>name</code> and
     * <code>version</code>
     *
     * @param tracer
     * <code>Tracer</code> to compare this
     * <code>Tracer</code> to
     * @return
     * <code>boolean</code> -
     * <code>true</code> if argument
     * <code>
     *          tracer</code> is this
     * <code>Tracer</code> or their
     * <code>name</code> and
     * <code>version</code> are identical, else
     * <code>false</code>
     */
    @Override
    public boolean equals ( Object tracer ) {
        //check for self-comparison
        if ( this == tracer ) {
            return true;
        }
        if (  ! (tracer instanceof Tracer) ) {
            return false;
        }

        Tracer myTracer = (Tracer) tracer;
        return (this.getNameAndVersion().trim().compareToIgnoreCase( myTracer.getNameAndVersion().trim() ) == 0);
    }

    /**
     * returns 0 as the hashcode for this
     * <code>Tracer</code>. Implemented to meet equivalency requirements as
     * documented by
     * <code>java.lang.Object</code>
     *
     * @pre this
     * <code>Tracer</code> exists @post hashcode of 0 is returned for this
     * <code>Tracer</code>
     *
     * @return
     * <code>int</code> - 0
     */
    // http://www.javaworld.com/javaworld/jw-01-1999/jw-01-object.html?page=4
    @Override
    public int hashCode () {

        return 0;
    }

    /**
     * gets the
     * <code>tracerName</code> of this
     * <code>Tracer</code>.
     *
     * @pre this
     * <code>Tracer</code> exists @post returns this
     * <code>Tracer</code>'s
     * <code>tracerName</code>
     *
     * @return
     * <code>String</code> - this
     * <code>Tracer</code>'s
     * <code>tracerName</code>
     */
    @Override
    public String getTracerName () {
        return tracerName;
    }

    /**
     * gets the
     * <code>versionNumber</code> of this
     * <code>Tracer</code>.
     *
     * @pre this
     * <code>Tracer</code> exists @post returns this
     * <code>Tracer</code>'s
     * <code>versionNumber</code>
     *
     * @return
     * <code>int</code> - this
     * <code>Tracer</code>'s
     * <code>versionNumber</code>
     */
    @Override
    public int getVersionNumber () {
        return versionNumber;
    }

    /**
     * gets the
     * <code>labName</code> of this
     * <code>Tracer</code>.
     *
     * @pre this
     * <code>Tracer</code> exists @post returns this
     * <code>Tracer</code>'s
     * <code>labName</code>
     *
     * @return
     * <code>String</code> - this
     * <code>Tracer</code>'s
     * <code>labName</code>
     */
    @Override
    public String getLabName () {
        return labName;
    }

    /**
     * gets the
     * <code>dateCertified</code> of this
     * <code>Tracer</code>.
     *
     * @pre this
     * <code>Tracer</code> exists @post returns this
     * <code>Tracer</code>'s
     * <code>dateCertified</code>
     *
     * @return
     * <code>String</code> - this
     * <code>Tracer</code>'s
     * <code>dateCertified</code>
     */
    @Override
    public String getDateCertified () {
        return dateCertified;
    }

    /**
     * sets this
     * <code>Tracer</code>'s
     * <code>tracerName</code> to argument
     * <code>tracerName</code>.
     *
     * @pre argument
     * <code>tracerName</code> is a valid
     * <code>String</code> @post this
     * <code>Tracer</code>'s
     * <code>tracerName</code> is set to argument
     * <code>tracerName</code>
     *
     * @param tracerName value to set this
     * <code>Tracer</code>'s
     * <code>tracerName</code> to
     */
    @Override
    public void setTracerName ( String tracerName ) {
        this.tracerName = tracerName.trim();
    }

    /**
     * sets this
     * <code>Tracer</code>'s versionNumber</code> to argument
     * <code>versionNumber</code>.
     *
     * @pre argument
     * <code>versionNumber</code> is a valid
     * <code>int</code> @post this
     * <code>Tracer</code>'s
     * <code>versionNumber</code> is set to argument
     * <code>versionNumber</code>
     *
     * @param versionNumber value to set this
     * <code>Tracer</code>'s
     * <code>versionNumber</code> to
     */
    @Override
    public void setVersionNumber ( int versionNumber ) {
        this.versionNumber = versionNumber;
    }

    /**
     * sets this
     * <code>Tracer</code>'s
     * <code>labName</code> to argument
     * <code>labName</code>.
     *
     * @pre argument
     * <code>labName</code> is a valid
     * <code>String</code> @post this
     * <code>Tracer</code>'s
     * <code>labName</code> is set to argument
     * <code>labName</code>
     *
     * @param labName value to set this
     * <code>Tracer</code>'s
     * <code>labName</code> to
     */
    @Override
    public void setLabName ( String labName ) {
        this.labName = labName.trim();
    }

    /**
     * sets this
     * <code>Tracer</code>'s
     * <code>dateCertified</code> to argument
     * <code>dateCertified</code>.
     *
     * @pre argument
     * <code>dateCertified</code> is a valid
     * <code>String</code> @post this
     * <code>Tracer</code>'s
     * <code>dateCertified</code> is set to argument
     * <code>dateCertified</code>
     *
     * @param dateCertified value to set this
     * <code>Tracer</code>'s
     * <code>dateCertified</code> to
     */
    @Override
    public void setDateCertified ( String dateCertified ) {
        DateFormat df = new SimpleDateFormat( "yyyy-MM-dd" );
        try {
            Date testDate = df.parse( dateCertified.trim() );
        } catch (ParseException parseException) {
            this.dateCertified = DateHelpers.defaultEarthTimeDateString();
        }

        this.dateCertified = dateCertified.trim();
    }

    /**
     * gets the
     * <code>tracerType</code> of this
     * <code>Tracer</code>.
     *
     * @pre this
     * <code>Tracer</code> exists @post returns the this
     * <code>Tracer</code>'s
     * <code>tracerType</code>
     *
     * @return
     * <code>String</code> - this
     * <code>Tracer</code>'s
     * <code>tracerType</code>
     */
    @Override
    public String getTracerType () {
        return tracerType;
    }

    /**
     * sets this
     * <code>Tracer</code>'s
     * <code>tracerType</code> to argument
     * <code>tracerType</code>.
     *
     * @pre argument
     * <code>tracerType</code> is a valid
     * <code>String</code> @post this
     * <code>Tracer</code>'s
     * <code>tracerType</code> is set to argument
     * <code>tracerType</code>
     *
     * @param tracerType value to set this
     * <code>Tracer</code>'s
     * <code>tracerType</code> to
     */
    @Override
    public void setTracerType ( String tracerType ) {
        String temp = tracerType.trim();
        if ( DataDictionary.legalTracerType( temp ) ) {
            this.tracerType = temp;
        } else {
            this.tracerType = "NONE";
        }
    }

    /**
     * gets a
     * <code>String</code> containing this
     * <code>Tracer</code>'s
     * <code>tracerName</code> and
     * <code>versionNumber</code>.
     *
     * @pre this
     * <code>Tracer</code> exists @post returns a
     * <code>String</code> containing this
     * <code>Tracer</code>'s
     * <code>tracerName</code> and
     * <code>versionNumber</code>
     *
     * @return
     * <code>String</code> - this
     * <code>Tracer</code>'s
     * <code>tracerName</code> and
     * <code>versionNumber</code>
     */
    @Override
    public String getNameAndVersion () {
        return getTracerName().trim()//
                + " v." + getVersionNumber();
    }

    /**
     * gets the
     * <code>ratios</code> of this
     * <code>Tracer</code>.
     *
     * @pre this
     * <code>Tracer</code> exists @post returns this
     * <code>Tracer</code>'s
     * <code>ratios</code>
     *
     * @return
     * <code>ValueModel[]</code> - collection of this
     * <code>Tracer</code>'s
     * <code>ratios</code>
     */
    @Override
    public ValueModel[] getRatios () {
        return ratios;
    }

    /**
     * sets this
     * <code>Tracer</code>'s
     * <code>ratios</code> to argument
     * <code>ratios</code>.
     *
     * @pre argument
     * <code>ratios</code> is a valid collection of
     * <code>ValueModel</code> @post this
     * <code>Tracer</code>'s
     * <code>ratios</code> is set to argument
     * <code>ratios</code>
     *
     * @param ratios collection of values to set this
     * <code>Tracer</code>'s
     * <code>ratios</code> to
     */
    @Override
    public void setRatios ( ValueModel[] ratios ) {
        this.ratios = ValueModel.cullNullsFromArray( ratios );
    }

    /**
     * gets the
     * <code>isotopeConcentrations</code> of this
     * <code>Tracer</code>.
     *
     * @pre this
     * <code>Tracer</code> exists @post returns this
     * <code>Tracer</code>'s
     * <code>isotopeConcentrations</code>
     *
     * @return
     * <code>ValueModel[]</code> - collection of this
     * <code>Tracer</code>'s
     * <code>isotopeConcentrations</code>
     */
    @Override
    public ValueModel[] getIsotopeConcentrations () {
        return isotopeConcentrations;
    }

    /**
     * sets this
     * <code>Tracer</code>'s
     * <code>isotopeConcentrations</code> to argument
     * <code>isotopeConcentrations</code>.
     *
     * @pre argument
     * <code>isotopeConcentrations</code> is a valid collection of
     * <code>ValueModel</code> @post this
     * <code>Tracer</code>'s
     * <code>isotopeConcentrations</code> is set to argument
     * <code>isotopeConcentrations</code>
     *
     * @param isotopeConcentrations collection of values to set this
     * <code>Tracer</code>'s
     * <code>
     *                                  isotopeConcentration</code> to
     */
    @Override
    public void setIsotopeConcentrations ( ValueModel[] isotopeConcentrations ) {
        this.isotopeConcentrations = ValueModel.cullNullsFromArray( isotopeConcentrations );
    }

    /**
     * gets the
     * <code>tracerName</code> and
     * <code>versionNumber</code> of this
     * <code>Tracer</code> via {@link #getNameAndVersion getNameAndVersion}.
     *
     * @pre this
     * <code>Tracer</code> exists @post returns a
     * <code>String</code> containing this
     * <code>Tracer</code>'s
     * <code>tracerName</code> and
     * <code>versionNumber</code>
     *
     * @return
     * <code>String</code> - this
     * <code>Tracer</code>'s
     * <code>
     *          tracerName</code> and
     * <code>versionNumber</code>
     */
    @Override
    public String getReduxLabDataElementName () {
        return getNameAndVersion();
    }

    /**
     * gets a single ratio from this
     * <code>Tracer</code>'s
     * <code>ratios</code> specified by argument
     * <code>ratioName</code>. Returns a new, empty
     * <code>
     * ValueModel</code> if no matching ratio is found.
     *
     * @pre argument
     * <code>ratioName</code> is a valid
     * <code>String</code> @post returns the
     * <code>ValueModel</code> found in this
     * <code>Tracer</code>'s
     * <code>ratios</code> whose name matches argument
     * <code>ratioName</code>
     *
     * @param ratioName name of the ratio to search for
     * @return
     * <code>ValueModel</code> - ratio found in
     * <code>ratios</code> whose name matches argument
     * <code>ratioName</code> or a new
     * <code>
     *          ValueModel</code> if no match is found
     */
    @Override
    public ValueModel getRatioByName ( String ratioName ) {

        ValueModel retVal = null;
        for (int i = 0; i < ratios.length; i ++) {
            if ( ratios[i].getName().equals( ratioName ) ) {
                retVal = ratios[i];
            }
        }

        if ( retVal == null ) {
            // not found
            // jan 2011 make more robust to handle changing specs for tracers
            retVal = new ValueModel( ratioName, "PCT" );

            ValueModel[] temp = new ValueModel[ratios.length + 1];
            System.arraycopy( ratios, 0, temp, 0, ratios.length );

            temp[temp.length - 1] = retVal;
            ratios = temp;
        }

        return retVal;
    }

    /**
     * gets the isotope concentration found in this
     * <code>Tracer</code>'s
     * <code>isotopeConcentrations</code> whose name matches argument
     * <code>isotopeConc</code>. Returns a new, empty
     * <code>ValueModel</code> if no match is found.
     *
     * @pre argument isotopeConc is a valid
     * <code>String</code> @post returns the
     * <code>ValueModel</code> found in
     * <code>
     *          isotopeConcentrations</code> whose name matches argument
     * <code>isotopeConc</code>
     *
     * @param isotopeConc name of the isotope concentration to search for
     * @return
     * <code>ValueModel</code> - isotope concentration found in
     * <code>isotopeConcentrations</code> whose name matches argument
     * <code>isotopeConc</code> or a new
     * <code>ValueModel</code> if no match is found
     */
    @Override
    public ValueModel getIsotopeConcByName ( String isotopeConc ) {

        ValueModel retVal = null;
        for (int i = 0; i < getIsotopeConcentrations().length; i ++) {
            if ( getIsotopeConcentrations()[i].getName().equals( isotopeConc ) ) {
                retVal = getIsotopeConcentrations()[i];
            }
        }
        if ( retVal == null ) {
            // not found
            // jan 2011 make more robust to handle changing specs for tracers
            retVal = new ValueModel( isotopeConc, "PCT" );

            ValueModel[] temp = new ValueModel[getIsotopeConcentrations().length + 1];
            System.arraycopy( getIsotopeConcentrations(), 0, temp, 0, getIsotopeConcentrations().length );

            temp[temp.length - 1] = retVal;
            setIsotopeConcentrations( temp );
        }

        return retVal;
    }

    /**
     *
     *
     * @return @pre @post
     */
    public static String[] getListOfEarthTimeTracers () {
        UPbReduxConfigurator myConfigurator = new UPbReduxConfigurator();

        String tracers =
                URIHelper.getTextFromURI(
                myConfigurator.getResourceURI( "URI_EARTHTIME_XMLTracers" )//
                + ".tracerList.txt" );

        String[] retVal = tracers.split( "\n" );

        return retVal;
    }

    // XML Serialization
    /**
     * gets an
     * <code>XStream</code> writer. Creates, customizes, and returns
     * <code>XStream</code> for XML serialization
     *
     * @pre
     * <code>XStream</code> package is available @post
     * <code>XStream</code> for XML encoding is returned
     *
     * @return
     * <code>XStream</code> - for XML serialization encoding
     */
    private XStream getXStreamWriter () {
        XStream xstream = new XStream( new DomDriver() );

        customizeXstream( xstream );

        return xstream;
    }

    /**
     * gets an
     * <code>XStream</code> reader. Creates, customizes, and returns
     * <code>XStream</code> for XML serialization
     *
     * @pre
     * <code>XStream</code> package is available @post
     * <code>XStream</code> for XML decoding is returned
     *
     * @return
     * <code>XStream</code> - for XML serialization decoding
     */
    private XStream getXStreamReader () {

        XStream xstream = new XStream( new DomDriver() );

        customizeXstream( xstream );

        return xstream;
    }

    /**
     * registers converter for argument
     * <code>xstream</code> and sets aliases to make the XML file more
     * human-readable
     *
     * @pre argument
     * <code>xstream</code> is a valid
     * <code>XStream</code> @post argument
     * <code>xstream</code> is customized to produce a cleaner output
     * <code>file</code>
     *
     * @param xstream
     * <code>XStream</code> to be customized
     */
    private void customizeXstream ( XStream xstream ) {

        xstream.registerConverter( new ValueModelXMLConverter() );
        xstream.registerConverter( new TracerXMLConverter() );

        xstream.alias( "Tracer", Tracer.class );
        xstream.alias( "ValueModel", ValueModel.class );

        setClassXMLSchemaURL();
    }

    /**
     * sets the XML schema. Initializes
     * <code>UPbReduxConfigurator</code> and sets the location of the XML Schema
     *
     * @pre
     * <code>UPbReduxConfigurator</code> class is available @post
     * <code>TracerXMLSchemaURL</code> will be set
     */
    private void setClassXMLSchemaURL () {
        UPbReduxConfigurator myConfigurator = new UPbReduxConfigurator();

        tracerXMLSchemaURL =
                myConfigurator.getResourceURI( "URI_TracerXMLSchema" );
    }

    /**
     * encodes this
     * <code>Tracer</code> to the
     * <code>file</code> specified by the argument
     * <code>filename</code>
     *
     * @pre this
     * <code>Tracer</code> exists @post this
     * <code>Tracer</code> is stored in the specified XML
     * <code>file</code>
     *
     * @param filename location to store data to
     */
    @Override
    public void serializeXMLObject ( String filename ) {
        XStream xstream = getXStreamWriter();

        String xml = xstream.toXML( this );

        xml = ReduxConstants.XML_Header + xml;

        xml = xml.replaceFirst("Tracer",
                "Tracer  "//
                + ReduxConstants.XML_ResourceHeader//
                + tracerXMLSchemaURL//
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
//            e.printStackTrace();
        }
    }

    /**
     * decodes
     * <code>Tracer</code> from
     * <code>file</code> specified by argument
     * <code>filename</code>
     *
     * @param filename location to read data from
     * @param doValidate the value of doValidate
     * @return
     * <code>Object</code> - the
     * <code>Tracer</code> created from the specified XML
     * <code>file</code>
     * @throws java.io.FileNotFoundException
     * @throws org.earthtime.XMLExceptions.ETException
     * @throws org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException @pre
     * <code>filename</code> references an XML
     * <code>file</code> @post
     * <code>Tracer</code> stored in
     * <code>filename</code> is returned
     */
    @Override
    public Object readXMLObject ( String filename, boolean doValidate )
            throws FileNotFoundException, ETException, BadOrMissingXMLSchemaException {
        Tracer myTracer = null;

        BufferedReader reader = URIHelper.getBufferedReader( filename );

        if ( reader != null ) {
            XStream xstream = getXStreamReader();

            boolean temp = URIHelper.validateXML( reader, filename, tracerXMLSchemaURL );

            if ( temp ) {
                // re-create reader
                reader = URIHelper.getBufferedReader( filename );
                try {
                    myTracer = (Tracer) xstream.fromXML( reader );
                } catch (ConversionException e) {
                    throw new ETException( null, e.getMessage() );
                }

//                System.out.println( "This is your Tracer that was just read successfully:\n" );

//                String xml2 = getXStreamWriter().toXML( myTracer );
//
//                System.out.println( xml2 );
//                System.out.flush();

            } else {
                throw new ETException( null, "Badly formed XML data file." );
            }
        } else {
            throw new FileNotFoundException( "Missing XML data file." );
        }

        return myTracer;
    }

    /**
     *
     * @param args
     * @throws Exception
     */
    public static void main ( String[] args ) throws Exception {

        Tracer tracer =
                new Tracer( "Test Tracer" );
        String testFileName = "TracerTEST.xml";

        tracer.serializeXMLObject( testFileName );
        tracer.readXMLObject( testFileName, true );

        String[] test = getListOfEarthTimeTracers();

        for (int i = 0; i < test.length; i ++) {
            System.out.println( "\n\nChecking " + test[i] + " tracer" );
            System.out.flush();
            tracer =
                    (Tracer) tracer.readXMLObject(
                    (new UPbReduxConfigurator()).getResourceURI( "URI_EARTHTIME_XMLTracers" )//
                    + test[i], true );
            System.out.println( "\n\n" );
            System.out.flush();
        }
    }

    /**
     *
     */
    @Override
    public void removeSelf () {
//        throw new UnsupportedOperationException( "Not supported yet." );
    }
}
