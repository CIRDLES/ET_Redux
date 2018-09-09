/*
 * SampleMetaData.java
 *
 * Created on Novemebr 1, 2009
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
package org.earthtime.UPb_Redux.samples;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.NullPermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.fractions.FractionMetaData;
import org.earthtime.UPb_Redux.user.UPbReduxConfigurator;
import org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException;
import org.earthtime.archivingTools.URIHelper;
import org.earthtime.exceptions.ETException;
import org.earthtime.xmlUtilities.XMLSerializationI;

/**
 *
 * @author James F. Bowring
 */
public class SampleMetaData implements XMLSerializationI {

    private transient String sampleMetaDataXMLSchemaURL;

    private String sampleName;

    private String sampleAnalysisFolderPath;

    private FractionMetaData[] fractionsMetaData;

    /**
     *
     * @param sampleName
     * @param sampleAnalysisFolderPath
     */
    public SampleMetaData(String sampleName, String sampleAnalysisFolderPath) {
        this.sampleName = sampleName;
        this.sampleAnalysisFolderPath = sampleAnalysisFolderPath;
    }

    /**
     * @return the fractionsMetaData
     */
    public FractionMetaData[] getFractionsMetaData() {
        return fractionsMetaData;
    }

    /**
     * @param fractionsMetaData the fractionsMetaData to set
     */
    public void setFractionsMetaData(FractionMetaData[] fractionsMetaData) {
        this.fractionsMetaData = fractionsMetaData;
    }

    /**
     * @return the sampleMetaDataXMLSchemaURL
     */
    public String getSampleMetaDataXMLSchemaURL() {
        return sampleMetaDataXMLSchemaURL;
    }

    /**
     * @param sampleMetaDataXMLSchemaURL the sampleMetaDataXMLSchemaURL to set
     */
    public void setSampleMetaDataXMLSchemaURL(String sampleMetaDataXMLSchemaURL) {
        this.sampleMetaDataXMLSchemaURL = sampleMetaDataXMLSchemaURL;
    }

    /**
     * @return the sampleName
     */
    public String getSampleName() {
        return sampleName;
    }

    /**
     * @param sampleName the sampleName to set
     */
    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    /**
     * @return the sampleAnalysisFolderPath
     */
    public String getSampleAnalysisFolderPath() {
        return sampleAnalysisFolderPath;
    }

    /**
     * @param sampleAnalysisFolderPath the sampleAnalysisFolderPath to set
     */
    public void setSampleAnalysisFolderPath(String sampleAnalysisFolderPath) {
        this.sampleAnalysisFolderPath = sampleAnalysisFolderPath;
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

        // http://x-stream.github.io/security.html
        XStream.setupDefaultSecurity(xstream);
        // clear out existing permissions and set own ones
        xstream.addPermission(NoTypePermission.NONE);
        // allow some basics
        xstream.addPermission(NullPermission.NULL);
        xstream.addPermission(PrimitiveTypePermission.PRIMITIVES);
        xstream.allowTypeHierarchy(Collection.class);
        xstream.addPermission(AnyTypePermission.ANY);

        return xstream;
    }

    /**
     * registers converter for argument <code>xstream</code> and sets aliases to
     * make the XML file more human-readable
     *
     * @pre argument <code>xstream</code> is a valid <code>XStream</code>
     * @post argument <code>xstream</code> is customized to produce a cleaner
     * output <code>file</code>
     * @param xstream     <code>XStream</code> to be customized
     */
    public void customizeXstream(XStream xstream) {

        //xstream.registerConverter(new ValueModelXMLConverter());
        xstream.alias("SampleMetaData", SampleMetaData.class);
        xstream.alias("FractionMetaData", FractionMetaData.class);

        setClassXMLSchemaURL();
    }

    /**
     * sets the XML schema. Initializes <code>UPbReduxConfigurator</code> and
     * sets the location of the XML Schema
     *
     * @pre     <code>UPbReduxConfigurator</code> class is available
     * @post    <code>sampleMetaDataXMLSchemaURL</code> will be set
     */
    public void setClassXMLSchemaURL() {
        UPbReduxConfigurator myConfigurator = new UPbReduxConfigurator();

        setSampleMetaDataXMLSchemaURL(myConfigurator.getResourceURI("URI_SampleMetaDataXMLSchema"));
    }

    /**
     * encodes this <code>ValueModel</code> to the <code>file</code> specified
     * by the argument <code>filename</code>
     *
     * @pre this <code>ValueModel</code> exists
     * @post this <code>ValueModel</code> is stored in the specified XML
     * <code>file</code>
     * @param filename location to store data to
     */
    public void serializeXMLObject(String filename) {
        XStream xstream = getXStreamWriter();

        String xml = xstream.toXML(this);

        xml = ReduxConstants.XML_Header + xml;

        xml = xml.replaceFirst("SampleMetaData",
                "SampleMetaData " + ReduxConstants.XML_ResourceHeader + getSampleMetaDataXMLSchemaURL() + "\"");

        try {
            FileWriter outFile = new FileWriter(filename);
            PrintWriter out = new PrintWriter(outFile);

            // Write xml to file
            out.println(xml);
            out.flush();
            out.close();
            outFile.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * decodes <code>ValueModel</code> from <code>file</code> specified by
     * argument <code>filename</code>
     *
     * @param filename location to read data from
     * @param doValidate the value of doValidate
     * @return <code>Object</code> - the <code>ValueModel</code> created from
     * the specified XML <code>file</code>
     * @throws java.io.FileNotFoundException
     * @throws org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException
     * @pre <code>filename</code> references an XML <code>file</code>
     * @post <code>ValueModel</code> stored in <code>filename</code> is returned
     */
    public Object readXMLObject(String filename, boolean doValidate)
            throws FileNotFoundException, ETException, FileNotFoundException, BadOrMissingXMLSchemaException {
        SampleMetaData mySampleMetaData = null;

        BufferedReader reader = URIHelper.getBufferedReader(filename);

        if (reader != null) {
            boolean validXML = false;
            XStream xstream = getXStreamReader();

            validXML = true;//URIHelper.validateXML(reader, getSampleMetaDataXMLSchemaURL());

            if (validXML) {
                // re-create reader
                reader = URIHelper.getBufferedReader(filename);
                try {
                    mySampleMetaData = (SampleMetaData) xstream.fromXML(reader);
                } catch (ConversionException e) {
                    throw new ETException(null, e.getMessage());
                }

//                System.out.println("\nThis is your SampleMetaData that was just read successfully:\n");
//                String xml2 = getXStreamWriter().toXML(mySampleMetaData);
//
//                System.out.println(xml2);
//                System.out.flush();
            }

        } else {
            throw new FileNotFoundException("Badly formed or missing XML data file.");
        }

        return mySampleMetaData;
    }

    /**
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        SampleMetaData sampleMetaData
                = new SampleMetaData("SampleJim", "C:xyz/Sector54Data");

        sampleMetaData.setFractionsMetaData(new FractionMetaData[]//
        {new FractionMetaData("F-1", "Aliquot1", "F-1_U.xml", "F-1_Pb.xml"),//
            new FractionMetaData("F-2", "Aliquot1", "F-2_U.xml", "F-2_Pb.xml")}//
        );

        String testFileName = "SampleMetaDataTEST.xml";

        sampleMetaData.serializeXMLObject(testFileName);
        sampleMetaData.readXMLObject(testFileName, true);

    }
}
