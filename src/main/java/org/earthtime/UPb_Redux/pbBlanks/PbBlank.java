/*
 * PbBlank.java
 *
 * Created on October 9, 2006, 4:41 PM
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
package org.earthtime.UPb_Redux.pbBlanks;

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
import org.earthtime.ratioDataModels.pbBlankICModels.PbBlankICModel;
import org.earthtime.utilities.DateHelpers;
import org.earthtime.xmlUtilities.XMLSerializationI;

/**
 * Deprecated June 2012. Needed for compatibility with legacy serializations
 * (archives).
 *
 * @author James F. Bowring, javaDocs by Stan Gasque
 */
public class PbBlank implements
        Comparable<PbBlank>,
        PbBlankI,
        XMLSerializationI,
        Serializable,
        ReduxLabDataListElementI {

    /**
     * identifies object in binary serialization
     */
    private static final long serialVersionUID = 7499998266349391512L;
    /**
     * holds URL to find XML schema for storage and retrieval
     */
    private transient String PbBlankXMLSchemaURL;
    /**
     * name of the <code>PbBlank</code>
     */
    private String name;
    /**
     * collection of <code>ValueModel</code> ratios for this
     * <code>PbBlank</code>
     */
    private ValueModel[] ratios;
    /**
     * collection of <code>ValueModel</code> RHO correlations for this
     * <code>PbBlank</code>
     */
    private ValueModel[] rhoCorrelations;

    /**
     * creates a new instance of PbBlank with no values initialized.
     */
    public PbBlank() {
        this.name = ReduxConstants.DEFAULT_OBJECT_NAME;

        this.ratios = new ValueModel[DataDictionary.earthTimePbBlankICRatioNames.length];
        for (int ratioIndex = 0; ratioIndex < ratios.length; ratioIndex++) {
            ratios[ratioIndex]
                    = new ValueModel(DataDictionary.getPbBlankRatioName(ratioIndex),
                            BigDecimal.ZERO,
                            "ABS",
                            BigDecimal.ZERO, BigDecimal.ZERO);
        }

        Arrays.sort(ratios);

        this.rhoCorrelations = new ValueModel[DataDictionary.earthTimePbBlankRhoCorrelationNames.length];
        for (int rhoCorrIndex = 0; rhoCorrIndex < rhoCorrelations.length; rhoCorrIndex++) {
            rhoCorrelations[rhoCorrIndex]
                    = new ValueModel(DataDictionary.getPbBlankRhoCorrelationName(rhoCorrIndex),
                            BigDecimal.ZERO,
                            "NONE",
                            BigDecimal.ZERO, BigDecimal.ZERO);
        }

        Arrays.sort(rhoCorrelations);
    }

    /**
     * creates a new instance of PbBlank with its <code>name</code> set to
     * argument <code>name</code> and its <code>ratios</code> and
     * <code>rhoCorrelations</code> set to data taken from the data dictionary.
     *
     * @param name the name to give the new <code>PbBlank</code>
     */
    public PbBlank(String name) {
        this();
        this.name = name.trim();
    }

    // used for deserialization
    /**
     *
     * @return
     */
    protected Object readResolve() {

        AbstractRatiosDataModel pbBlankModel;

        if ((this.name.equalsIgnoreCase("<none>"))) {

            pbBlankModel = PbBlankICModel.getNoneInstance();
        } else if ((this.name.toLowerCase().startsWith("example"))) {
            // catch old earthtime example
            pbBlankModel = PbBlankICModel.getEARTHTIMEExamplePbBlankICModel();
        } else {
            pbBlankModel = convertModel(this);
        }

        return pbBlankModel;
    }

    /**
     *
     * @param model
     * @return
     */
    public static AbstractRatiosDataModel convertModel(PbBlank model) {

        // convert correlations
        Map<String, BigDecimal> correlations = new HashMap<String, BigDecimal>();
        for (int i = 0; i < model.rhoCorrelations.length; i++) {
            correlations.put(//
                    model.rhoCorrelations[i].getName(),//
                    model.rhoCorrelations[i].getValue());
        }
        return//
                PbBlankICModel.createInstance( //
                        model.name, //
                        1, 0, //
                        "Unknown Lab",//
                        DateHelpers.defaultEarthTimeDateString(),//
                        "No reference", //
                        "No comment", //
                        model.ratios, //
                        correlations);

    }

    /**
     * returns a deep copy of this <code>PbBlank</code>.
     *
     * @pre this <code>PbBlank</code> exists @post returns a new
     * <code>PbBlank</code> with data identical to that of this
     * <code>PbBlank</code>
     *
     * @return <code>PbBlank</code> - a new <code>PbBlank</code> whose fields
     * match those of this <code>PbBlank</code>
     */
    @Override
    public PbBlank Copy() {
        PbBlank retCopy = new PbBlank(getName());

        ValueModel[] retRatios = new ValueModel[getRatios().length];
        for (int ratioIndex = 0; ratioIndex < getRatios().length; ratioIndex++) {
            retRatios[ratioIndex] = getRatios()[ratioIndex].copy();
        }

        retCopy.setRatios(retRatios);

        ValueModel[] retRhos = new ValueModel[getRhoCorrelations().length];
        for (int rhoCorrIndex = 0; rhoCorrIndex < getRhoCorrelations().length; rhoCorrIndex++) {
            retRhos[rhoCorrIndex] = getRhoCorrelations()[rhoCorrIndex].copy();
        }

        retCopy.setRhoCorrelations(retRhos);

        return retCopy;
    }

    /**
     * gets the <code>name</code> of this <code>PbBlank</code>.
     *
     * @pre this <code>PbBlank</code> exists @post returns the <code>name</code>
     * of this <code>PbBlank</code>; returns <code>null</code> if the
     * <code>name</code> was never initialized.
     *
     * @return <code>String</code> - <code>name</code> of this
     * <code>PbBlank</code>
     */
    public String getName() {
        return name;
    }

    /**
     * gets the <code>name</code> of this <code>PbBlank</code>.
     *
     * @pre this <code>PbBlank</code> exists @post returns the <code>name</code>
     * of this <code>PbBlank</code> via {@link #getName() getName}
     *
     * @return <code>String</code> - <code>name</code> of this
     * <code>PbBlank</code>
     */
    public String getReduxLabDataElementName() {
        return getName();
    }

    /**
     * sets the <code>name</code> of this <code>PbBlank</code>.
     *
     * @pre argument <code>name</code> is a valid <code>String</code> @post
     * <code>name</code> of this <code>PbBlank</code> is set to argument
     * <code>name</code>
     *
     * @param name value to set this <code>PbBlank</code>'s <code>name</code> to
     */
    public void setName(String name) {
        this.name = name.trim();
    }

    /**
     * compares this <code>PbBlank</code> to argument <code>pbBlank</code>
     * lexicographically by <code>name</code>.
     *
     * @pre argument <code>pbBlank</code> is a valid <code>PbBlank</code> @post
     * returns the lexicographical equivalence between this
     * <code>PbBlank</code>'s <code>name</code> and argument
     * <code>pbBlank</code>'s <code>name</code>
     *
     * @param pbBlank <code>PbBlank</code> to compare this <code>PbBlank</code>
     * against
     * @return <code>int</code> - -1 if this <code>PbBlank</code> is
     * lexicographically less than argument <code>pbBlank</code>, 0 if they are
     * equal, and 1 if it is greater than argument <code>pbBlank</code>
     * @throws java.lang.ClassCastException ClassCastException
     */
    public int compareTo(PbBlank pbBlank) throws ClassCastException {
        String pbBlankName = ((PbBlank) pbBlank).getName();
        return this.getName().trim().compareToIgnoreCase(pbBlankName.trim());
    }

    /**
     * compares this <code>PbBlank</code> with argument <code>pbBlank</code>
     * lexicographically by <code>name</code>.
     *
     * @pre argument <code>pbBlank</code> is a valid <code>PbBlank</code> @post
     * returns <code>true</code> if this <code>PbBlank</code> is argument
     * <code>pbBlank</code> or if their <code>name</code> fields are
     * lexicographically equivalent, else <code>false</code>
     *
     * @param pbBlank <code>PbBlank</code> to compare this <code>PbBlank</code>
     * against
     * @return <code>boolean</code> - <code>true</code> if this
     * <code>PbBlank</code> is argument <code>pbBlank</code> or if their
     * <code>name</code> fields are lexicographically equivalent, else
     * <code>false</code>
     */
    @Override
    public boolean equals(Object pbBlank) {
        //check for self-comparison
        if (this == pbBlank) {
            return true;
        }
        if (!(pbBlank instanceof PbBlank)) {
            return false;
        }

        PbBlank argPbBlank = (PbBlank) pbBlank;

        return (this.getName().trim().compareToIgnoreCase(argPbBlank.getName().trim()) == 0);
    }

    /**
     * returns 0 as the hashcode for this <code>PbBlank</code>. Implemented to
     * meet equivalency requirements as documented by
     * <code>java.lang.Object</code>
     *
     * @pre this <code>PbBlank</code> exists @post hashcode of 0 is returned for
     * this <code>PbBlank</code>
     *
     * @return <code>int</code> - 0
     */
    // http://www.javaworld.com/javaworld/jw-01-1999/jw-01-object.html?page=4
    @Override
    public int hashCode() {

        return 0;
    }

    /**
     * finds and returns the <code>ValueModel</code> from <code>ratios</code>
     * whose <code>name</code> field matches the argument
     * <code>ratioName</code>.
     *
     * @pre argument <code>ratioName</code> is a valid <code>String</code> @post
     * returns the <code>ValueModel</code> from <code>ratios</code> whose name
     * is <code>ratioName</code> or <code>null</code> if none is found
     *
     * @param ratioName name of the <code>ValueModel</code> to search for
     * @return <code>ValueModel</code> - the member of <code>ratios</code> whose
     * <code>name</code> field is equivalent to argument <code>ratioName</code>;
     * <code>null</code> if no matching <code>ValueModel</code> is found
     */
    public ValueModel getRatioByName(String ratioName) {
        for (int ratioIndex = 0; ratioIndex < getRatios().length; ratioIndex++) {
            if (getRatios()[ratioIndex].getName().equals(ratioName)) {
                return getRatios()[ratioIndex];
            }
        }
        return new ValueModel(ratioName, "ABS");
    }

    /**
     * finds and returns the <code>ValueModel</code> from
     * <code>rhoCorrelations</code> whose <code>name</code> field matches the
     * argument <code>rhoCorr</code>.
     *
     * @pre argument <code>rhoCorr</code> is a valid <code>String</code> @post
     * returns the <code>ValueModel</code> from <code>rhoCorrelations</code>
     * whose name is <code>rhoCorr</code> or <code>null</code> if none is found
     *
     * @param rhoCorr name of the <code>ValueModel</code> to search for
     * @return <code>ValueModel</code> - the member of
     * <code>rhoCorrelations</code> whose <code>name</code> field is equivalent
     * to argument <code>rhoCorr</code>; <code>null</code> if no matching
     * <code>ValueModel</code> is found
     */
    public ValueModel getRhoCorrelationByName(String rhoCorr) {
        for (int rhoCorrIndex = 0; rhoCorrIndex < getRhoCorrelations().length; rhoCorrIndex++) {
            if (getRhoCorrelations()[rhoCorrIndex].getName().equals(rhoCorr)) {
                return getRhoCorrelations()[rhoCorrIndex];
            }
        }
        return new ValueModel(rhoCorr, "NONE");
    }

    /**
     * gets the <code>ratios</code> of this <code>PbBlank</code>.
     *
     * @pre this <code>PbBlank</code> exists @post returns the
     * <code>ratios</code> of this <code>PbBlank</code>
     *
     * @return <code>ValueModel[]</code> - the <code>ratios</code> of this
     * <code>PbBlank</code>
     */
    public ValueModel[] getRatios() {
        return ratios;
    }

    /**
     * sets the <code>ratios</code> of this <code>PbBlank</code>.
     *
     * @pre argument <code>ratios</code> is a valid collection of
     * <code>ValueModel</code> @post this <code>PbBlanks</code>'s
     * <code>ratio</code> field is set to argument <code>ratios</code>
     *
     * @param ratios value to set this <code>PbBlank</code>'s
     * <code>ratios</code> to
     */
    public void setRatios(ValueModel[] ratios) {
        this.ratios = ValueModel.cullNullsFromArray(ratios);
    }

    /**
     * gets the <code>rhoCorrelations</code> of this <code>PbBlank</code>.
     *
     * @pre this <code>PbBlank</code> exists @post returns the
     * <code>rhoCorrelations</code> of this <code>PbBlank</code>
     *
     * @return <code>ValueModel[]</code> - this <code>PbBlank</code>'s
     * <code>rhoCorrelations</code>
     */
    public ValueModel[] getRhoCorrelations() {
        return rhoCorrelations;
    }

    /**
     * sets the <code>rhoCorrelations</code> of this <code>PbBlank</code> to
     * argument <code>rhoCorrelations</code>.
     *
     * @pre argument <code>rhoCorrelations</code> is a valid collection of
     * <code>ValueModel</code> @post this <code>PbBlank</code>'s
     * <code>rhoCorrelations</code> is set to argument
     * <code>rhoCorrelations</code>
     *
     * @param rhoCorrelations value to set this <code>PbBlanks</code>'s
     * <code>rhoCorrelations</code> to
     */
    public void setRhoCorrelations(ValueModel[] rhoCorrelations) {
        this.rhoCorrelations = ValueModel.cullNullsFromArray(rhoCorrelations);
    }

    // XML Serialization
    /**
     * gets an <code>XStream</code> writer. Creates, customizes, and returns
     * <code>XStream</code> for XML serialization
     *
     * @pre <code>XStream</code> package is available @post <code>XStream</code>
     * for XML encoding is returned
     *
     * @return <code>XStream</code> - for XML serialization encoding
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
     * @pre <code>XStream</code> package is available @post <code>XStream</code>
     * for XML decoding is returned
     *
     * @return <code>XStream</code> - for XML serialization decoding
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
     * @pre argument <code>xstream</code> is a valid <code>XStream</code> @post
     * argument <code>xstream</code> is customized to produce a cleaner output
     * <code>file</code>
     *
     * @param xstream <code>XStream</code> to be customized
     */
    public void customizeXstream(XStream xstream) {

        xstream.registerConverter(new PbBlankXMLConverter());
        xstream.registerConverter(new ValueModelXMLConverter());

        xstream.alias("PbBlank", PbBlank.class);
        xstream.alias("ValueModel", ValueModel.class);

        setClassXMLSchemaURL();
    }

    /**
     * sets the XML schema. Initializes <code>UPbReduxConfigurator</code> and
     * sets the location of the XML Schema
     *
     * @pre <code>UPbReduxConfigurator</code> class is available @post
     * <code>valueModelXMLSchemaURL</code> will be set
     */
    public void setClassXMLSchemaURL() {
        UPbReduxConfigurator myConfigurator = new UPbReduxConfigurator();

        PbBlankXMLSchemaURL
                = myConfigurator.getResourceURI("URI_PbBlankXMLSchema");
    }

    /**
     * encodes this <code>PbBlank</code> to the <code>file</code> specified by
     * the argument <code>filename</code>
     *
     * @pre this <code>PbBlank</code> exists @post this <code>PbBlankl</code> is
     * stored in the specified XML <code>file</code>
     *
     * @param filename location to store data to
     */
    public void serializeXMLObject(String filename) {
        XStream xstream = getXStreamWriter();

        String xml = xstream.toXML(this);

        xml = ReduxConstants.XML_Header + xml;

        xml = xml.replaceFirst("PbBlank",
                "PbBlank  " + ReduxConstants.XML_ResourceHeader + PbBlankXMLSchemaURL + "\"");

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
     * decodes <code>PbBlank</code> from <code>file</code> specified by argument
     * <code>filename</code>
     *
     * @param filename location to read data from
     * @param doValidate the value of doValidate
     * @return <code>Object</code> - the <code>PbBlank</code> created from the
     * specified XML <code>file</code>
     * @throws java.io.FileNotFoundException
     * @throws org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException @pre
     * <code>filename</code> references an XML <code>file</code> @post
     * <code>PbBlank</code> stored in <code>filename</code> is returned
     */
    public Object readXMLObject(String filename, boolean doValidate)
            throws FileNotFoundException, ETException, FileNotFoundException, BadOrMissingXMLSchemaException {
        PbBlank retPbBlank = null;

        BufferedReader reader = URIHelper.getBufferedReader(filename);

        if (reader != null) {
            boolean validXML = true;
            XStream xstream = getXStreamReader();

            if (doValidate) {
                validXML = URIHelper.validateXML(reader, filename, PbBlankXMLSchemaURL);
            }

            if (validXML) {

                // re-create reader
                reader = URIHelper.getBufferedReader(filename);
                try {
                    retPbBlank = (PbBlank) xstream.fromXML(reader);
                } catch (ConversionException e) {
                    throw new ETException(null, e.getMessage());
                }

//                System.out.println( "This is your PbBlank that was just read successfully:\n" );
//                String xml2 = getXStreamWriter().toXML( retPbBlank );
//
//                System.out.println( xml2 );
//                System.out.flush();
            }

        } else {
            throw new FileNotFoundException("Badly formed or missing XML data file.");
        }

        return retPbBlank;
    }

    // testing
    /**
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        PbBlank pbBlank
                = new PbBlank("Test Blank");
        String testFileName = "PbBlankTEST.xml";

        pbBlank.serializeXMLObject(testFileName);
        pbBlank.readXMLObject(testFileName, true);

    }

    /**
     *
     */
    @Override
    public void removeSelf() {
//        throw new UnsupportedOperationException( "Not supported yet." );
    }
}
