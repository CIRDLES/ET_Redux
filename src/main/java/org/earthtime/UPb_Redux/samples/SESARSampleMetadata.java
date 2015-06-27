/*
 * SESARSampleMetadata.java
 *
 * Created Oct 30, 2010
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

package org.earthtime.UPb_Redux.samples;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.user.UPbReduxConfigurator;
import org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException;
import org.earthtime.archivingTools.URIHelper;
import org.earthtime.exceptions.ETException;
import org.earthtime.xmlUtilities.XMLSerializationI;

/**
 *
 * @author James F. Bowring
 */
public class SESARSampleMetadata 
        implements XMLSerializationI, Serializable {

     // Class variables
    /**
     * identifies object in binary serialization
     */
    private static final long serialVersionUID = 2325815081780592936L;

    /**
     * @return the serialVersionUID
     */
    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
    /**
     * holds URL to find XML schema for storage and retrieval
     */
    private transient String SESARSampleMetadataSchemaURL;
    // Instance variables
    private String stratigraphicFormationName;
    private String stratigraphicGeologicAgeMa;
    private double stratigraphicMinAbsoluteAgeMa;
    private double stratigraphicMaxAbsoluteAgeMa;
    private String detritalType;

    /**
     * 
     */
    public SESARSampleMetadata(){
        stratigraphicFormationName = "NONE";
        stratigraphicGeologicAgeMa = "NONE ( 0 Ma - 0 Ma )";
        stratigraphicMinAbsoluteAgeMa = 0;
        stratigraphicMaxAbsoluteAgeMa = 0;
        detritalType = "NONE";
    }

    /**
     * @return the stratigraphicFormationName
     */
    public String getStratigraphicFormationName () {
        return stratigraphicFormationName;
    }

    /**
     * @param stratigraphicFormationName the stratigraphicFormationName to set
     */
    public void setStratigraphicFormationName ( String stratigraphicFormationName ) {
        this.stratigraphicFormationName = stratigraphicFormationName;
    }

    /**
     * @return the stratigraphicGeologicAgeMa
     */
    public String getStratigraphicGeologicAgeMa () {
        return stratigraphicGeologicAgeMa;
    }

    /**
     * @param stratigraphicGeologicAgeMa the stratigraphicGeologicAgeMa to set
     */
    public void setStratigraphicGeologicAgeMa ( String stratigraphicGeologicAgeMa ) {
        this.stratigraphicGeologicAgeMa = stratigraphicGeologicAgeMa;
    }

    /**
     * @return the stratigraphicMinAbsoluteAgeMa
     */
    public double getStratigraphicMinAbsoluteAgeMa () {
        return stratigraphicMinAbsoluteAgeMa;
    }

    /**
     * @param stratigraphicMinAbsoluteAgeMa the stratigraphicMinAbsoluteAgeMa to set
     */
    public void setStratigraphicMinAbsoluteAgeMa ( double stratigraphicMinAbsoluteAgeMa ) {
        this.stratigraphicMinAbsoluteAgeMa = stratigraphicMinAbsoluteAgeMa;
    }

    /**
     * @return the stratigraphicMaxAbsoluteAgeMa
     */
    public double getStratigraphicMaxAbsoluteAgeMa () {
        return stratigraphicMaxAbsoluteAgeMa;
    }

    /**
     * @param stratigraphicMaxAbsoluteAgeMa the stratigraphicMaxAbsoluteAgeMa to set
     */
    public void setStratigraphicMaxAbsoluteAgeMa ( double stratigraphicMaxAbsoluteAgeMa ) {
        this.stratigraphicMaxAbsoluteAgeMa = stratigraphicMaxAbsoluteAgeMa;
    }

    /**
     * @return the detritalType
     */
    public String getDetritalType () {
        return detritalType;
    }

    /**
     * @param detritalType the detritalType to set
     */
    public void setDetritalType ( String detritalType ) {
        this.detritalType = detritalType;
    }

 // XML Serialization
    /**
     * gets an <code>XStream</code> writer. Creates, customizes, and returns
     * <code>XStream</code> for XML serialization
     *
     * @pre     <code>XStream</code> package is available
     * @post    <code>XStream</code> for XML encoding is returned
     * @return  <code>XStream</code> - for XML serialization encoding
     */
    public XStream getXStreamWriter() {
        XStream xstream = new XStream();

        customizeXstream(xstream);

        return xstream;
    }

    /**
     * gets an <code>XStream</code> reader. Creates, customizes, and returns
     * <code>XStream</code> for XML serialization
     *
     * @pre     <code>XStream</code> package is available
     * @post    <code>XStream</code> for XML decoding is returned
     * @return  <code>XStream</code> - for XML serialization decoding
     */
    public XStream getXStreamReader() {

        XStream xstream = new XStream(new DomDriver());

        customizeXstream(xstream);

        return xstream;
    }

    /**
     * registers converter for argument <code>xstream</code> and sets aliases to
     * make the XML file more human-readable
     *
     * @pre     argument <code>xstream</code> is a valid <code>XStream</code>
     * @post    argument <code>xstream</code> is customized to produce a cleaner
     *          output <code>file</code>
     * @param   xstream     <code>XStream</code> to be customized
     */
    public void customizeXstream(XStream xstream) {

        xstream.registerConverter(new SESARSampleMetadataXMLConverter());

        xstream.alias("SESARSampleMetadata", SESARSampleMetadata.class);


        setClassXMLSchemaURL();
    }

    /**
     * sets the XML schema. Initializes <code>UPbReduxConfigurator</code> and
     * sets the location of the XML Schema
     *
     * @pre     <code>UPbReduxConfigurator</code> class is available
     * @post    <code>SESARSampleMetadataXMLSchemaURL</code> will be set
     */
    public void setClassXMLSchemaURL() {
        UPbReduxConfigurator myConfigurator = new UPbReduxConfigurator();

        SESARSampleMetadataSchemaURL =
                myConfigurator.getResourceURI("URI_SESARSampleMetadataSchemaURLXMLSchema");
    }

    /**
     * encodes this <code>SESARSampleMetadata</code> to the <code>file</code> specified
     * by the argument <code>filename</code>
     *
     * @pre     this <code>SESARSampleMetadata</code> exists
     * @post    this <code>SESARSampleMetadata</code> is stored in the specified XML <code>file</code>
     * @param   filename    location to store data to
     */
    public void serializeXMLObject(String filename) {
        XStream xstream = getXStreamWriter();

        String xml = xstream.toXML(this);

        xml = ReduxConstants.XML_Header + xml;

        xml = xml.replaceFirst("SESARSampleMetadata",
                "SESARSampleMetadata  "//
                + ReduxConstants.XML_ResourceHeader//
                + SESARSampleMetadataSchemaURL//
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
     * decodes <code>SESARSampleMetadata</code> from <code>file</code> specified by
     * argument <code>filename</code>
     *
     * @param filename    location to read data from
     * @param doValidate the value of doValidate
     * @return <code>Object</code> - the <code>SESARSampleMetadata</code> created from
     * the specified XML <code>file</code>
     * @throws java.io.FileNotFoundException
     * @throws org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException
     * @pre <code>filename</code> references an XML <code>file</code>
     * @post <code>SESARSampleMetadata</code> stored in <code>filename</code> is returned
     */
    public Object readXMLObject(String filename, boolean doValidate)
            throws FileNotFoundException, ETException, FileNotFoundException, BadOrMissingXMLSchemaException {
        SESARSampleMetadata SESARSampleMetadata = null;

        BufferedReader reader = URIHelper.getBufferedReader(filename);

        if (reader != null) {
            boolean isValidOrAirplaneMode = !doValidate;
            
            XStream xstream = getXStreamReader();

            isValidOrAirplaneMode = URIHelper.validateXML(reader, filename, SESARSampleMetadataSchemaURL);

            if (isValidOrAirplaneMode) {
                // re-create reader
                reader = URIHelper.getBufferedReader(filename);
                try {
                    SESARSampleMetadata = (SESARSampleMetadata) xstream.fromXML(reader);
                } catch (ConversionException e) {
                    throw new ETException(null, e.getMessage());
                }

//                System.out.println("This is your SESARSampleMetadata that was just read successfully:\n");

//                String xml2 = getXStreamWriter().toXML(SESARSampleMetadata);
//
//                System.out.println(xml2);
//                System.out.flush();
            } else {
                throw new ETException( null, "XML data file does not conform to schema." );
            }
        } else {
            throw new FileNotFoundException( "Missing XML data file." );
        }

        return SESARSampleMetadata;
    }

    /**
     * 
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        SESARSampleMetadata SESARSampleMetadata =
                new SESARSampleMetadata();
        String testFileName = "SESARSampleMetadataTEST.xml";

        SESARSampleMetadata.serializeXMLObject(testFileName);
        SESARSampleMetadata.readXMLObject(testFileName, true);
     
    }
}

