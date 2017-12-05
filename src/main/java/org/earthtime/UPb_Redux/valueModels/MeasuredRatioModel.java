/*
 * MeasuredRatioModel.java
 *
 * Created on March 26, 2006, 1:13 PM
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
package org.earthtime.UPb_Redux.valueModels;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.math.BigDecimal;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException;
import org.earthtime.archivingTools.URIHelper;
import org.earthtime.exceptions.ETException;
import org.earthtime.xmlUtilities.XMLSerializationI;

/**
 * <code>MeasuredRatioModels</code> are special <code>ValueModels</code> that
 * abstract isotope ratios and uncertainties with <code>boolean</code> flags
 * denoting whether or not the ratio has been fractionation-corrected and
 * whether or not it has been oxide-corrected. Each flag is <code>true</code> if
 * the correction occurred on a datum-by-datum basis before the means were
 * calculated, else <code>false</code>.
 *
 * @author James F. Bowring, javaDocs by Stan Gasque
 */
public class MeasuredRatioModel extends ValueModel implements
        Comparable<ValueModel>,
        Serializable,
        XMLSerializationI {

    // Class variables
    /**
     * identifies object in binary serialization
     */
    private static final long serialVersionUID = 8893765636157337961L;
    // Instance variables
    /**
     * indicates whether or not the ratio has been fractionation-corrected;
     * should be set to <code>true</code> when data has been
     * fractionation-corrected on a per-datum basis.
     */
    private boolean fracCorr;
    /**
     * indicates whether or not the ratio has been oxide-corrected; should be
     * set to <code>true</code> when data has been oxide-corrected on a
     * per-datum basis.
     */
    private boolean oxideCorr;

    /**
     * creates a new instance of <code>MeasuredRatioModel</code> with values set
     * according to the default <code>ValueModel</code> constructor and
     * <code>fracCorr</code> and <code>oxideCorr</code> both initialized to
     * <code>false</code>.
     */
    public MeasuredRatioModel() {
        super();
        this.fracCorr = false;
        this.oxideCorr = false;
    }

    /**
     * creates a new instance of <code>MeasuredRatioModel</code> with its values
     * initialized to the given arguments.
     *
     * @param name the <code>name</code> of the ratio
     * @param value the <code>value</code> of the ratio
     * @param uncertaintyType the <code>uncertaintyType</code> of the ratio
     * @param oneSigma the <code>oneSigma</code> of the ratio
     * @param fracCorr whether the ratio has been fractionation-corrected or not
     * @param oxideCorr whether the ratio has been oxide-corrected or not
     */
    public MeasuredRatioModel(
            String name,
            BigDecimal value,
            String uncertaintyType,
            BigDecimal oneSigma,
            boolean fracCorr,
            boolean oxideCorr) {

        super(name, value, uncertaintyType, oneSigma, BigDecimal.ZERO);
        this.fracCorr = fracCorr;
        this.oxideCorr = oxideCorr;
    }

    /**
     * returns a deep copy of this <code>MeasuredRatioModel</code>.
     *
     * @pre this <code>MeasuredRatioModel</code> exists
     * @post a new <code>MeasuedRatioModel</code> with identical data to this
     * <code>MeasuredRatioModel</code> is returned
     * @return  <code>MeasuredRatioModel</code> - a new
     * <code>MeasuredRatioModel</code> whose fields match those of this
     * <code>MeasuredRatioModel</code>
     */
    @Override
    public MeasuredRatioModel copy() {
        return new MeasuredRatioModel(
                getName(),
                getValue(),
                getUncertaintyType(),
                getOneSigma(),
                isFracCorr(),
                isOxideCorr());
    }

    /**
     * copies the data from a given <code>ValueModel</code> into this <code>
     * MeasuredRatioModel</code>'s fields
     *
     * @pre argument <code>valueModel</code> is a valid
     * <code>MeasuredRatioModel</code>
     * @post this <code>MeasuredRatioModel</code>'s fields are set to the values
     * of argument <code>valueModel</code>'s respective fields
     * @param valueModel  <code>ValueModel</code> whose fields will be copied
     */
    @Override
    public void copyValuesFrom(ValueModel valueModel) {
        this.setValue(valueModel.getValue());
        this.setUncertaintyType(valueModel.getUncertaintyType());
        this.setOneSigma(valueModel.getOneSigma());
        this.setValueTree(valueModel.getValueTree());
        this.setFracCorr(((MeasuredRatioModel) valueModel).fracCorr);
        this.setOxideCorr(((MeasuredRatioModel) valueModel).oxideCorr);
    }

    /**
     * formats <code>value</code> and <code>oneSigma</code> for display as a
     * table; outputs in the format "(value) : (oneSigma)".
     *
     * @pre this <code>MeasuredRatioModel</code> exists
     * @post returns <code>value</code> and <code>oneSigma</code> of this <code>
     *          MeasuredRatioModel</code> formatted to be viewed as a table
     * @return  <code>String</code> - the formatted <code>value</code> and
     * <code>oneSigma</code> of this <code>MeasuredRatioModel</code>
     */
    public String toTableFormat() {
        return getValue().toPlainString()//
                + " : "//
                + getOneSigma().toPlainString();
    }

    /**
     * checks whether or not this <code>MeasuredRatioModel</code> has been
     * fractionation-corrected
     *
     * @pre this <code>MeasuredRatioModel</code> exists
     * @post returns this <code>MeasuredRatioModel</code>'s
     * <code>fracCorr</code>
     * @return  <code>boolean</code> - <code>true</code> if this <code>
     *          MeasuredRatioModel</code> has been fractionation-corrected, else
     * <code>false</code>
     */
    public boolean isFracCorr() {
        return fracCorr;
    }

    /**
     * sets whether or not this <code>MeasuredRatioModel</code> has been
     * fractionation-corrected
     *
     * @pre argument <code>fracCorr</code> is a valid <code>boolean</code>
     * @post this <code>MeasuredRatioModel</code>'s <code>fracCorr</code> is set
     * to argument <code>fracCorr</code>
     * @param fracCorr should be <code>true</code> if this <code>
     *                      MeasuredRatioModel</code> has been fractionation-corrected; else should
     * be <code>false</code>
     */
    public void setFracCorr(boolean fracCorr) {
        this.fracCorr = fracCorr;
    }

    /**
     * checks whether or not this <code>MeasuredRatioModel</code> has been
     * oxide-corrected
     *
     * @pre this <code>MeasuredRatioModel</code> exists
     * @post returns this <code>MeasuredRatioModel</code>'s
     * <code>oxideCorr</code>
     * @return  <code>boolean</code> - <code>true</code> if this <code>
     *          MeasuredRatioModel</code> has been oxide-corrected, else
     * <code>false</code>
     */
    public boolean isOxideCorr() {
        return oxideCorr;
    }

    /**
     * sets whether or not this <code>MeasuredRatioModel</code> has been
     * oxide-corrected
     *
     * @pre argument <code>oxideCorr</code> is a valid <code>boolean</code>
     * @post this <code>MeasuredRatioModel</code>'s <code>oxideCorr</code> is
     * set to argument <code>oxideCorr</code>
     * @param oxideCorr should be <code>true</code> if this <code>
     *                      MeasuredRatioModel</code> has been oxide-corrected; else should be
     * <code>false</code>
     */
    public void setOxideCorr(boolean oxideCorr) {
        this.oxideCorr = oxideCorr;
    }

    @Override
    public void customizeXstream(XStream xstream) {

        xstream.registerConverter(new MeasuredRatioModelXMLConverter());

        xstream.alias("MeasuredRatioModel", MeasuredRatioModel.class);

        setClassXMLSchemaURL();
    }

    /**
     * encodes this <code>MeasuredRatioModel</code> to the <code>file</code>
     * specified by the argument <code>filename</code>
     *
     * @pre this <code>MeasuredRatioModel</code> exists
     * @post this <code>MeasuredRatioModel</code> is stored in the specified XML
     * <code>file</code>
     * @param filename location to store data to
     */
    @Override
    public void serializeXMLObject(String filename) {
        XStream xstream = getXStreamWriter();

        String xml = xstream.toXML(this);

        xml = ReduxConstants.XML_Header + xml;

        xml = xml.replaceFirst("MeasuredRatioModel",
                "MeasuredRatioModel " + ReduxConstants.XML_ResourceHeader + getValueModelXMLSchemaURL() + "\"");

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
     * decodes <code>MeasuredRatioModel</code> from <code>file</code> specified
     * by argument <code>filename</code>
     *
     * @param filename location to read data from
     * @param doValidate the value of doValidate
     * @return <code>Object</code> - the <code>MeasuredRatioModel</code> created
     * from the specified XML <code>file</code>
     * @throws java.io.FileNotFoundException
     * @throws org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException
     * @pre <code>filename</code> references an XML <code>file</code>
     * @post <code>MeasuredRatioModel</code> stored in <code>filename</code> is
     * returned
     */
    @Override
    public Object readXMLObject(String filename, boolean doValidate)
            throws FileNotFoundException, ETException, FileNotFoundException, BadOrMissingXMLSchemaException {
        ValueModel myValueModel = null;

        BufferedReader reader = URIHelper.getBufferedReader(filename);

        if (reader != null) {
            boolean isValidOrAirplaneMode = true;
            XStream xstream = getXStreamReader();

            if (doValidate) {
                isValidOrAirplaneMode = URIHelper.validateXML(reader, filename, getValueModelXMLSchemaURL());
            }

            if (isValidOrAirplaneMode) {
                // re-create reader
                reader = URIHelper.getBufferedReader(filename);
                try {
                    myValueModel = (MeasuredRatioModel) xstream.fromXML(reader);
                } catch (ConversionException e) {
                    throw new ETException(null, e.getMessage());
                }

////                System.out.println( "\nThis is your MeasuredRatioModel that was just read successfully:\n" );
//                String xml2 = getXStreamWriter().toXML( myValueModel );
//
//                System.out.println( xml2 );
//                System.out.flush();
            } else {
                throw new ETException(null, "XML data file does not conform to schema.");
            }
        } else {
            throw new FileNotFoundException("Missing XML data file.");
        }

        return myValueModel;
    }

    /**
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        ValueModel valueModel
                = new MeasuredRatioModel(//
                        "r206_204b", new BigDecimal("1234567890"), "ABS", new BigDecimal("123000"), true, true);
        System.out.println(
                "Format Test: " + valueModel.formatValueAndTwoSigmaForPublicationSigDigMode("ABS", 6, 2));

        String testFileName = "MeasuredRatioModelTEST.xml";

        valueModel.serializeXMLObject(testFileName);
        valueModel.readXMLObject(testFileName, true);

    }
}
