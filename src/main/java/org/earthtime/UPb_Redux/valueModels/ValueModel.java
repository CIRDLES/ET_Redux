/*
 * ValueModel.java
 *
 * Created on August 5, 2007, 7:53 AM
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
package org.earthtime.UPb_Redux.valueModels;

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
import java.math.MathContext;
import java.math.RoundingMode;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ConcurrentMap;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.expressions.ExpTreeII;
import org.earthtime.UPb_Redux.reduxLabData.ReduxLabDataListElementI;
import org.earthtime.UPb_Redux.user.UPbReduxConfigurator;
import org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException;
import org.earthtime.archivingTools.URIHelper;
import org.earthtime.dataDictionaries.DataDictionary;
import org.earthtime.dataDictionaries.RatioNamePrettyPrinter;
import org.earthtime.exceptions.ETException;
import org.earthtime.xmlUtilities.XMLSerializationI;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * A
 * <code>ValueModel</code> object represents scientifically measured quantities
 * and their errors. It also provides additional methods for manipulating and
 * publishing these values.
 *
 * @imports <a
 * href=http://xstream.codehaus.org/javadoc/com/thoughtworks/xstream/XStream.html>
 * com.thoughtworks.xstream.XSream</a> @imports <a
 * href=http://xstream.codehaus.org/javadoc/com/thoughtworks/xstream/converters/
 * ConversionException.html>com.thoughtworks.xstream.converters.ConversionException</a>
 * @imports <a
 * href=http://xstream.codehaus.org/javadoc/com/thoughtworks/xstream/io/xml/
 * DomDriver.html>com.thoughtworks.xstream.io.xml.DomDriver</a>
 *
 * @author James F. Bowring, javaDocs by Stan Gasque
 */
public class ValueModel implements
        ValueModelI,
        Comparable<ValueModel>,
        XMLSerializationI,
        Serializable,
        ReduxLabDataListElementI {

    // Class variables
    /**
     * identifies object in binary serialization
     */
    private static final long serialVersionUID = -1667913664102101602L;
    /**
     *
     */
    public static transient String DEFAULT_UNCERTAINTY_TYPE = "NONE";
    /**
     * holds URL to find XML schema for storage and retrieval
     */
    private transient String valueModelXMLSchemaURL;
    private transient ExpTreeII valueTree;
    // Instance variables
    /**
     * name of <code>ValueModel</code>, such as ratio name. Lists of 'name' are
     * in the {@link DataDictionary DataDictionary} class.
     */
    protected String name;
    /**
     * numerical value of <code>ValueModel</code>
     */
    protected BigDecimal value;
    /**
     * type of uncertainty stored in <code>ValueModel</code>; ABS or PCT
     */
    protected String uncertaintyType;
    /**
     * one Sigma of <code>ValueModel</code>
     */
    protected BigDecimal oneSigma;
    // Sept 2014 per Noah include both Variability and Systematic sources of uncertainty.
    // Since this is a huge retrofit, the assumption is that the old oneSigma will be the Var flavor and eventaully
    // each model will become aware of its own needs in this regard
    // the mehtods will slowly be refactored to handle this
    private BigDecimal oneSigmaSys;

    /**
     * creates a new instance of <code>ValueModel</code> with <code>name</code>,
     * <code>value</code>, <code>uncertainty type</code>, and
     * <code>one sigma</code> fields initialized to "NONE", 0, "NONE", and 0
     * respectively.
     */
    public ValueModel() {
        this.name = ReduxConstants.DEFAULT_OBJECT_NAME;
        this.value = BigDecimal.ZERO;
        this.uncertaintyType = DEFAULT_UNCERTAINTY_TYPE;
        this.oneSigma = BigDecimal.ZERO;
        this.oneSigmaSys = BigDecimal.ZERO;
        this.valueTree = new ExpTreeII();
    }

    /**
     * creates a new instance of <code>ValueModel</code> with a specified
     * <code>name</code>, a default <code>value</code> and
     * <code>one sigma</code>, and <code>uncertainty type</code> initialized to
     * "NONE".
     *
     * @param name name of the ratio that this <code>ValueModel</code>
     * represents
     */
    public ValueModel(String name) {
        this();
        this.name = name.trim();
        this.valueTree.setNodeName(name.trim());
    }

    /**
     * creates a new instance of <code>ValueModel</code> with a specified
     * <code>name</code> and <code>uncertainty type</code> and a default
     * <code>value</code> and <code>one sigma</code>
     *
     * @param name name of the ratio that this <code>ValueModel</code>
     * represents
     * @param uncertaintyType type of uncertainty; ABS or PCT
     */
    public ValueModel(String name, String uncertaintyType) {
        this(name);
        String temp = uncertaintyType.trim();
        if (!temp.equalsIgnoreCase("ABS") && !temp.equalsIgnoreCase("PCT")) {
            temp = DEFAULT_UNCERTAINTY_TYPE;
        }
        this.uncertaintyType = temp;
    }

    /**
     * creates a new instance of <code>ValueModel</code> with a specified
     * <code>name</code>, <code>value</code>, <code>uncertainty type</code>, and
     * <code>one sigma</code>
     *
     * @param name name of the ratio that this <code>ValueModel</code>
     * represents
     * @param value numerical value of ratio
     * @param uncertaintyType type of uncertainty; ABS or PCT
     * @param oneSigma value of one standard deviation
     * @param oneSigmaSys the value of oneSigmaSys
     */
    public ValueModel(
            String name, BigDecimal value, String uncertaintyType, BigDecimal oneSigma, BigDecimal oneSigmaSys) {

        this(name, uncertaintyType);
        this.value = value;
        this.oneSigma = oneSigma.abs();
        this.oneSigmaSys = oneSigmaSys.abs();
        this.valueTree.setNodeValue(value);
    }

    /**
     * returns a deep copy of a <code>ValueModel</code>; a new      <code>ValueModel
     * </code> whose fields are equal to those of this <code>ValueModel</code>
     *
     * @pre this <code>ValueModel</code> exists @post a new
     * <code>ValueModel</code> with identical data to this      <code>
     *          ValueModel</code> is returned
     *
     * @return <code>ValueModel</code> - a new <code>ValueModel</code> whose
     * fields match those of this <code>ValueModel</code>
     */
    @Override
    public ValueModel copy() {
        ValueModel retval =//
                new ValueModel(
                        getName(),
                        getValue(),
                        getUncertaintyType(),
                        getOneSigma(), getOneSigmaSys());

        retval.setValueTree(getValueTree());

        return retval;
    }

    /**
     *
     * @param inputValueModels
     * @param parDerivTerms
     */
    public void calculateValue(
            ValueModel[] inputValueModels,
            ConcurrentMap<String, BigDecimal> parDerivTerms) {
    }

    /**
     * sets the <code>value</code>, <code>uncertainty type</code>, and
     * <code>one sigma</code> of this <code>ValueModel</code> to those of the
     * argument <code>valueModel</code>
     *
     * @pre argument <code>valueModel</code> is a valid <code>ValueModel</code>
     * @post this <code>ValueModel</code>'s <code>value</code>,      <code>uncertainty
     *          type</code>, and <code>one sigma</code> are set to the argument
     * <code>valueModel</code>'s respective values
     *
     * @param valueModel <code>ValueModel</code> whose fields will be copied
     */
    @Override
    public void copyValuesFrom(ValueModel valueModel) {
        this.setValue(valueModel.getValue());
        this.setUncertaintyType(valueModel.getUncertaintyType());
        this.setOneSigma(valueModel.getOneSigma());
        this.setOneSigmaSys(valueModel.getOneSigmaSys());
        this.setValueTree(valueModel.getValueTree());
    }

    /**
     * compares this <code>ValueModel</code> with the argument      <code>valueModel
     * </code> by <code>name</code>. Returns an <code>integer</code> based on
     * comparison
     *
     * @pre <code>valueModel</code> exists @post an <code>integer</code> is
     * returned that relates whether the given <code>ValueModel</code> is
     * lexicographically less than, equal to, or greater than this
     * <code>ValueModel</code>
     *
     * @param valueModel the <code>ValueModel</code> to be compared to this one
     * @return int - 0 if the argument <code>ValueModel</code> is equal to this
     * <code>ValueModel</code>, less than 0 if it is less than this      <code>
     *          ValueModel</code>, and greater than 0 if it is greater than this
     * <code>ValueModel</code>
     * @throws java.lang.ClassCastException a ClassCastException
     */
    @Override
    public int compareTo(ValueModel valueModel) throws ClassCastException {
        String argName = valueModel.getName();
        return this.getName().trim().compareToIgnoreCase(argName.trim());
    }

    /**
     * checks for lexicographic equivalence between this <code>ValueModel</code>
     * and the argument <code>valueModel</code>. Returns <code>false</code> if
     * the <code>object</code> given is not a <code>ValueModel</code>
     *
     * @pre <code>valueModel</code> exists @post a <code>boolean</code> is
     * returned - <code>true</code> if argument is equal to this
     * <code>ValueModel</code>, else <code>false</code>
     *
     * @param valueModel the <code>Object</code> to be compared to this
     * <code>ValueModel</code>
     * @return boolean - <code>true</code> if the argument
     * <code>valueModel</code> is this <code>ValueModel</code> or is
     * lexicographically equivalent, else <code>false</code>
     */
    @Override
    public boolean equals(Object valueModel) {
        //check for self-comparison
        if (this == valueModel) {
            return true;
        }
        if (!(valueModel instanceof ValueModel)) {
            return false;
        }

        ValueModel myValueModel = (ValueModel) valueModel;
        return (this.getName().trim().compareToIgnoreCase(myValueModel.getName().trim()) == 0);
    }

    /**
     * returns 0 as the hashcode for this <code>ValueModel</code>. Implemented
     * to meet equivalency requirements as documented by
     * <code>java.lang.Object</code>
     *
     * @pre <code>ValueModel</code> exists @post hashcode of 0 is returned for
     * this <code>ValueModel</code>
     *
     * @return int - 0
     */
    // http://www.javaworld.com/javaworld/jw-01-1999/jw-01-object.html?page=4
    @Override
    public int hashCode() {

        return 0;
    }

    // Field Accessors
    /**
     * gets the <code>name</code> of this <code>ValueModel</code> via the
     * {@link ValueModel#getName() getName} method
     *
     * @pre this <code>ValueModel</code> exists @post <code>name</code> of this
     * <code>ValueModel</code> is returned
     *
     * @return <code>String</code> - <code>name</code> of this
     * <code>ValueModel</code>
     */
    @Override
    public String getReduxLabDataElementName() {
        return getName();
    }

    /**
     * gets the value of the <code>name</code> field
     *
     * @pre this <code>ValueModel</code> exists @post <code>name</code> of this
     * <code>ValueModel</code> is returned
     *
     * @return <code>String</code> - <code>name</code> of this
     * <code>ValueModel</code>
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * sets the value of the <code>name</code> field
     *
     * @pre argument <code>name</code> is a valid <code>String</code> @post
     * <code>name</code> of this <code>ValueModel</code> is set to argument
     * <code>name</code>
     *
     * @param name value to which this <code>ValueModel</code>'s
     * <code>name</code> is set
     */
    @Override
    public void setName(String name) {
        this.name = name.trim();
        getValueTree().setNodeName(name.trim());
    }

    /**
     * gets the value of the <code>value</code> field
     *
     * @pre this <code>ValueModel</code> exists @post <code>value</code> of this
     * <code>ValueModel</code> is returned
     *
     * @return <code>BigDecimal</code> - <code>value</code> of this
     * <code>ValueModel</code>
     */
    @Override
    public BigDecimal getValue() {

        return value;
    }

    /**
     *
     * @param units
     * @return
     */
    public BigDecimal getValueInUnits(String units) {
        int shiftPointRightCount = 0;

        try {
            shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount(units);
        } catch (Exception e) {
        }

        return getValue().movePointRight(shiftPointRightCount);
    }

    /**
     * sets the value of the <code>value</code> field
     *
     * @pre argument <code>value</code> is a valid <code>BigDecimal</code> @post
     * <code>value</code> of this <code>ValueModel</code> is set to argument
     * <code>value</code>
     *
     * @param value value to which this <code>ValueModel</code>'s
     * <code>value</code> is set
     */
    @Override
    public void setValue(BigDecimal value) {
        this.value = value;
    }

    /**
     *
     * @param value
     */
    public void setValue(double value) {
        if (Double.isFinite(value)) {
            this.value = new BigDecimal(value);
        } else {
            this.value = BigDecimal.ZERO;
        }
    }

    /**
     * gets the value of the <code>uncertainty type</code> field
     *
     * @pre this <code>ValueModel</code> exists @post
     * <code>uncertainty type</code> of this <code>ValueModel</code> is returned
     *
     * @return <code>String</code> - <code>uncertainty type</code> of this
     * <code>ValueModel</code>
     */
    @Override
    public String getUncertaintyType() {
        return uncertaintyType;
    }

    /**
     * sets the value of the <code>uncertainty type</code> field
     *
     * @pre argument <code>uncertaintyType</code> is a valid <code>String</code>
     * @post <code>uncertainty type</code> of this <code>ValueModel</code> is
     * set to argument <code>uncertaintyType</code>
     *
     * @param uncertaintyType value to which this <code>ValueModel</code>'s
     * <code>uncertainty type</code> is set
     */
    @Override
    public void setUncertaintyType(String uncertaintyType) {
        String temp = uncertaintyType.trim();
        if (!temp.equalsIgnoreCase("ABS") && !temp.equalsIgnoreCase("PCT")) {
            temp = DEFAULT_UNCERTAINTY_TYPE;
        }
        this.uncertaintyType = temp;
    }

    /**
     *
     */
    public void setUncertaintyTypeABS() {
        this.uncertaintyType = "ABS";
    }

    /**
     *
     */
    public void setUncertaintyTypePCT() {
        this.uncertaintyType = "PCT";
    }

    /**
     * gets the value of the <code>one sigma</code> field
     *
     * @pre this <code>ValueModel</code> exists @post <code>one sigma</code> of
     * this <code>ValueModel</code> is returned
     *
     * @return <code>BigDecimal</code> - <code>one sigma</code> of this
     * <code>ValueModel</code>
     */
    @Override
    public BigDecimal getOneSigma() {
        return oneSigma;
    }

    /**
     * sets the value of the <code>one sigma</code> field
     *
     * @pre argument <code>oneSigma</code> is a valid <code>BigDecimal</code>
     * @post <code>one sigma</code> of this <code>ValueModel</code> is set to
     * argument <code>oneSigma</code>
     *
     * @param oneSigma value to which this <code>ValueModel</code>'s
     * <code>one sigma</code> is set
     */
    @Override
    public void setOneSigma(BigDecimal oneSigma) {
        this.oneSigma = oneSigma.abs();
    }

    /**
     *
     * @param oneSigma
     */
    public void setOneSigma(double oneSigma) {
        this.oneSigma = new BigDecimal(oneSigma).abs();
    }

    /**
     * gets the value of the <code>one sigma</code> field as ABS uncertainty and
     * if needed converts PCT uncertainty type to ABS by 100 *
     * variableOneSigmaAbs / variable.
     *
     * @pre this <code>ValueModel</code> exists @post <code>one sigma</code> of
     * this <code>ValueModel</code> when using ABS <code>uncertainty type</code>
     * is returned
     *
     * @return <code>BigDecimal</code> - <code>one sigma</code> value of this
     * <code>ValueModel</code> using ABS <code>uncertainty type</code>
     */
    @Override
    public BigDecimal getOneSigmaAbs() {
        if (getUncertaintyType().equalsIgnoreCase("PCT")) {
            return oneSigma.multiply(value, ReduxConstants.mathContext15).movePointLeft(2);
        } else {
            return oneSigma;
        }

        //variableOneSigmaPct = 100* variableOneSigmaAbs / variable.
    }

    /**
     *
     * @return
     */
    @Override
    public BigDecimal getOneSigmaSysAbs() {
        if (getUncertaintyType().equalsIgnoreCase("PCT")) {
            return oneSigmaSys.multiply(value, ReduxConstants.mathContext15).movePointLeft(2);
        } else {
            return oneSigmaSys;
        }
    }

    /**
     *
     * @return the boolean
     */
    public boolean amPositiveAndLessThanTolerance() {
        double tolerance = 10e-20;
        return (0.0 < value.doubleValue()) && (value.doubleValue() < tolerance);
    }

    /**
     *
     * @param valueModel
     * @param oneSigmaAbs
     * @return
     */
    public static BigDecimal convertOneSigmaAbsToPctIfRequired(//
            ValueModel valueModel, BigDecimal oneSigmaAbs) {
        if (valueModel.getUncertaintyType().equalsIgnoreCase("PCT")) {
            return oneSigmaAbs.divide(valueModel.getValue(), ReduxConstants.mathContext15)//
                    .movePointRight(2);
//            return new BigDecimal(//
//                    100.0 * oneSigmaAbs.doubleValue() / valueModel.getValue().doubleValue() );
        } else {
            return oneSigmaAbs;
        }
    }

    /**
     *
     * @param valueModel
     * @param oneSigmaPct
     * @return
     */
    public static BigDecimal convertOneSigmaPctToAbsIfRequired(ValueModel valueModel, BigDecimal oneSigmaPct) {
        if (valueModel.getUncertaintyType().equalsIgnoreCase("ABS")) {
            return oneSigmaPct.multiply(valueModel.getValue(), ReduxConstants.mathContext15).movePointLeft(2);
//            return new BigDecimal(//
//                    oneSigmaPct.movePointLeft( 2 ).doubleValue() * valueModel.getValue().doubleValue() );
        } else {
            return oneSigmaPct;
        }
    }

    /**
     * gets the value of the <code>one sigma</code> field as PCT value
     *
     * @pre this <code>ValueModel</code> exists @post <code>one sigma</code> of
     * this <code>ValueModel</code> when using PCT <code>uncertainty type</code>
     * is returned
     *
     * @return <code>BigDecimal</code> - <code>one sigma</code> value of this
     * <code>ValueModel</code> using PCT <code>uncertainty type</code>
     */
    @Override
    public BigDecimal getOneSigmaPct() {
        if (getUncertaintyType().equalsIgnoreCase("PCT")) {
            return oneSigma;
        } else if (value.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        } else {
            return oneSigma.divide(value, ReduxConstants.mathContext15).movePointRight(2);
        }
    }

    /**
     *
     * @return
     */
    @Override
    public BigDecimal getOneSigmaSysPct() {
        if (getUncertaintyType().equalsIgnoreCase("PCT")) {
            return oneSigmaSys;
        } else if (value.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        } else {
            return oneSigmaSys.divide(value, ReduxConstants.mathContext15).movePointRight(2);
        }
    }

    /**
     *
     */
    public void toggleUncertaintyType() {
        if (getUncertaintyType().equalsIgnoreCase("PCT")) {
            setOneSigma(getOneSigmaAbs());
            setUncertaintyType("ABS");
        } else {
            setOneSigma(getOneSigmaPct());
            setUncertaintyType("PCT");
        }
    }

    /**
     * gets the value of two sigma as ABS value
     *
     * @pre this <code>ValueModel</code> exists @post two sigma of this
     * <code>ValueModel</code> using ABS <code>uncertainty type</code> is
     * returned
     *
     * @return <code>BigDecimal</code> - two sigma value of this      <code>ValueModel
     * </code> using ABS <code>uncertainty type</code>
     */
    @Override
    public BigDecimal getTwoSigmaAbs() {
        return new BigDecimal("2.0").multiply(getOneSigmaAbs());
    }

    /**
     *
     * @return
     */
    public BigDecimal getTwoSigmaPct() {
        return new BigDecimal("2.0").multiply(getOneSigmaPct());
    }

    /**
     *
     * @param uncertaintyType
     * @param units
     * @return
     */
    public BigDecimal getTwoSigma(String uncertaintyType, String units) {
        int shiftPointRightCount = 0;

        try {
            shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount(units);
        } catch (Exception e) {
        }

        if (uncertaintyType.equalsIgnoreCase("PCT")) {
            return getTwoSigmaPct().movePointRight(shiftPointRightCount);
        } else {
            return getTwoSigmaAbs().movePointRight(shiftPointRightCount);
        }
    }

    /**
     * standardizes output of <code>ValueModels</code> with organized
     * <code>String</code> output for testing
     *
     * @pre this <code>ValueModel</code> exists @post formatted
     * <code>String</code> containing the <code>name</code>, <code>value</code>,
     * and <code>one sigma</code> using ABS      <code>
     *          uncertainty type</code> of this <code>ValueModel</code> is returned
     *
     * @return <code>String</code> - <code>name</code>, <code>value</code>, and
     * <code>one sigma</code> using ABS <code>uncertainty type</code>
     */
    @Override
    public String formatValueAndOneSigmaABSForTesting() {
        NumberFormat formatter = new DecimalFormat("0.0000000000E0");

        String retVal = //
                String.format("   %1$-32s", getName()) + " = " //
                + String.format("%1$-20s", formatter.format(getValue().doubleValue())); //

        if (!getUncertaintyType().equalsIgnoreCase(ValueModel.DEFAULT_UNCERTAINTY_TYPE)) {
            retVal += //
                    "1-SigmaAbs = " //
                    + String.format("%1$-20s", formatter.format(getOneSigmaAbs().doubleValue()));
        }

        return retVal;// + differenceValueCalcs();
    }

    public String formatValueAndOneSigmaQuickLook() {
        NumberFormat formatter = new DecimalFormat("0.00000E0");

        String retVal = //
                getName() + " = " //
                + formatter.format(getValue().doubleValue()); //

        if (!getUncertaintyType().equalsIgnoreCase(ValueModel.DEFAULT_UNCERTAINTY_TYPE)) {
            retVal += //
                    " \u00B1 " //
                    + formatter.format(getOneSigmaAbs().doubleValue());
        }

        return retVal;// + differenceValueCalcs();
    }

    /**
     *
     * @return
     */
    @Override
    public String formatNameValuePCTVarSysTightReadyForHTML() {
//        NumberFormat formatter = new DecimalFormat("0.000E0");
        NumberFormat formatter = new DecimalFormat("##0.00");
//        NumberFormat formatterPCT = new DecimalFormat("#0.0");
        NumberFormat formatterPCT = new DecimalFormat("#0.00");

        String retVal = //
                String.format("%1$-10s", RatioNamePrettyPrinter.makePrettyForHTMLString(getName())) + "=" //
                + String.format("%1$-10s", formatter.format(getValue().doubleValue())); //

        if (!getUncertaintyType().equalsIgnoreCase(ValueModel.DEFAULT_UNCERTAINTY_TYPE)) {
            retVal += //
                    " \u00B1 " //
                    + String.format("%1$-4s", formatterPCT.format(getOneSigmaAbs().doubleValue()))//
                    + "/"
                    + String.format("%1$-4s", formatterPCT.format(getOneSigmaSysAbs().doubleValue()));
        }

        return retVal;
    }

    /**
     *
     * @param twoSigError
     * @return
     */
    protected int calculateCountOfDigitsAfterDecPoint(String twoSigError) {
        // determine location of decimal point in uncertainty
        int countOfDigitsAfterDecPointInError;
        int indexOfDecPoint = twoSigError.indexOf(".");
        if (indexOfDecPoint < 0) {
            countOfDigitsAfterDecPointInError = 0;
        } else {
            countOfDigitsAfterDecPointInError =//
                    twoSigError.length() - (indexOfDecPoint + 1);
        }

        return countOfDigitsAfterDecPointInError;

    }

    /**
     * standardizes output of <code>ValueModels</code> with organized      <code>
     * String</code> output for publication. <code>value</code> is modified to
     * contain the appropriate number of significant digits based on
     * <code>twoSigma</code>
     *
     * @param uncertaintyType
     * @param movePointRightCount power of ten to divide sigma by
     * @param uncertaintySigDigits number of significant digits in two sigma
     * error @pre this <code>ValueModel</code> exists @post formatted
     * <code>String</code> containing the <code>value</code> and
     * <code>two sigma</code>, each with the appropriate number of significant
     * digits, of this <code>ValueModel</code> is returned
     * @return <code>String</code> - <code>value</code> and
     * <code>two sigma</code> formatted for significant digits
     */
    public String formatValueAndTwoSigmaForPublicationSigDigMode(
            String uncertaintyType,
            int movePointRightCount,
            int uncertaintySigDigits) {

        // sept 2009 modified to force use of ABS uncertainty for sigfig counting
        // first determine the shape of significant digits of ABS uncertainty
        String twoSigmaUnct = //
                formatTwoSigmaForPublicationSigDigMode(//
                        "ABS", movePointRightCount, uncertaintySigDigits);

        // then generate the ouput string based on uncertainty type
        String twoSigmaUnctOutput = //
                formatTwoSigmaForPublicationSigDigMode(//
                        uncertaintyType, movePointRightCount, uncertaintySigDigits);

        // determine location of decimal point in uncertainty
        int countOfDigitsAfterDecPointInError = calculateCountOfDigitsAfterDecPoint(twoSigmaUnct);

        // create string from value
        String valueString = //
                getValue().movePointRight(movePointRightCount).toPlainString();

        // revised feb 2008 to do rounding !!
        valueString = new BigDecimal(valueString).//
                setScale(countOfDigitsAfterDecPointInError, RoundingMode.HALF_UP).toPlainString();

        //strip trailing decimal point if any
        if (valueString.endsWith(".")) {
            valueString = valueString.substring(0, valueString.length() - 1);
            // now check if trailing zeroes in uncertainty and zero those in value
            if (twoSigmaUnct.length() > uncertaintySigDigits) {
                // the extra length is zeroes and these need to be transferred to value
                int zeroesCount = twoSigmaUnct.length() - uncertaintySigDigits;
                try {
                    valueString
                            = valueString.substring(0, valueString.length() - zeroesCount)//
                            + "000000000000".substring(0, zeroesCount);
                } catch (Exception e) {
                }
            }
        }

        return valueString + " \u00B1 " + twoSigmaUnctOutput;
    }

    /**
     * Produces a string of the 2-sigma uncertainty with specified significant
     * digits
     *
     * @param uncertaintyType
     * @param movePointRightCount
     * @param uncertaintySigDigits
     * @return
     */
    public String formatTwoSigmaForPublicationSigDigMode(
            String uncertaintyType,
            int movePointRightCount,
            int uncertaintySigDigits) {

        if (uncertaintyType.equalsIgnoreCase("PCT")) {
            return formatBigDecimalForPublicationSigDigMode(//
                    getTwoSigmaPct().movePointRight(movePointRightCount),//
                    uncertaintySigDigits);
        } else {
            return formatBigDecimalForPublicationSigDigMode(//
                    getTwoSigmaAbs().movePointRight(movePointRightCount),//
                    uncertaintySigDigits);
        }
    }

    /**
     *
     * @param number
     * @param uncertaintySigDigits
     * @return
     */
    public static String formatBigDecimalForPublicationSigDigMode(
            BigDecimal number,
            int uncertaintySigDigits) {

        if ((number.compareTo(BigDecimal.ZERO) == 0)//
                || // jan 2011 to trap for absurdly small uncertainties
                // july 2011 added abs to handle negative values in tripoli alphas
                (number.abs().doubleValue() < Math.pow(10, -1 * ReduxConstants.mathContext15.getPrecision()))) {
            return "0";
        } else {
            return number.round(new MathContext(//
                    uncertaintySigDigits, RoundingMode.HALF_UP)).toPlainString();
        }
    }

    /**
     *
     * @param number
     * @param roundingDigits
     * @return
     */
    public static String formatBigDecimalForPublicationArbitraryMode(
            BigDecimal number,
            int roundingDigits) {
        // if roundingDigits > -1, then return that many places to the right of decimal
        if (roundingDigits > -1) {
            return number.setScale(roundingDigits, RoundingMode.HALF_UP).toPlainString();
        } else {
            return "fix me";
        }
    }

    /**
     * standardizes output of <code>ValueModels</code> with organized      <code>
     * String</code> output for publication. <code>value</code> is modified to
     * contain the appropriate number of significant digits based on
     * <code>twoSigma</code>
     *
     * @pre this <code>ValueModel</code> exists @post formatted
     * <code>String</code> containing the <code>value</code> per
     * <code>two sigma</code> with the appropriate number of significant digits
     * of this <code>ValueModel</code> is returned
     *
     * @param uncertaintyType
     * @param movePointRightCount the power of ten to divide sigma by
     * @param uncertaintySigDigits number of significant digits in error
     * @return <code>String</code> - <code>value</code> per two sigma formatted
     * for significant digits
     */
    public String formatValueFromTwoSigmaForPublicationSigDigMode(
            String uncertaintyType,
            int movePointRightCount,
            int uncertaintySigDigits) {

        String temp = formatValueAndTwoSigmaForPublicationSigDigMode(//
                uncertaintyType, movePointRightCount, uncertaintySigDigits);

        String[] retVal = temp.split("\u00B1");

        return retVal[0].trim();
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

        xstream.registerConverter(new ValueModelXMLConverter());

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

        setValueModelXMLSchemaURL(myConfigurator.getResourceURI("URI_ValueModelXMLSchema"));
    }

    /**
     * encodes this <code>ValueModel</code> to the <code>file</code> specified
     * by the argument <code>filename</code>
     *
     * @pre this <code>ValueModel</code> exists @post this
     * <code>ValueModel</code> is stored in the specified XML <code>file</code>
     *
     * @param filename location to store data to
     */
    @Override
    public void serializeXMLObject(String filename) {
        XStream xstream = getXStreamWriter();

        String xml = xstream.toXML(this);

        xml = ReduxConstants.XML_Header + xml;

        xml = xml.replaceFirst("ValueModel",
                "ValueModel " + ReduxConstants.XML_ResourceHeader + getValueModelXMLSchemaURL() + "\"");

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

    @Override
    public Object readXMLObject(String filename, boolean doValidate)
            throws FileNotFoundException, ETException, FileNotFoundException, BadOrMissingXMLSchemaException {
        ValueModel myValueModel = null;

        BufferedReader reader = URIHelper.getBufferedReader(filename);

        if (reader != null) {
            boolean isValidOrAirplaneMode;

            XStream xstream = getXStreamReader();

            isValidOrAirplaneMode = validateXML(filename);

            if (isValidOrAirplaneMode) {
                // re-create reader
                reader = URIHelper.getBufferedReader(filename);
                try {
                    myValueModel = (ValueModel) xstream.fromXML(reader);
                } catch (ConversionException e) {
                    throw new ETException(null, e.getMessage());
                }

                System.out.println("\nThis is your ValueModel that was just read successfully:\n");

                String xml2 = getXStreamWriter().toXML(myValueModel);

                System.out.println(xml2);
                System.out.flush();
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
     * @param xmlURI
     * @return
     */
    public boolean validateXML(String xmlURI) {
        try {
            // parse an XML document into a DOM tree
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setNamespaceAware(true);
            DocumentBuilder parser = dbFactory.newDocumentBuilder();
            Document document = parser.parse(xmlURI);

            // create a SchemaFactory capable of understanding WXS schemas
            SchemaFactory schemaFactory
                    = SchemaFactory.newInstance(
                            XMLConstants.W3C_XML_SCHEMA_NS_URI);

            // load a WXS schema, represented by a Schema instance
            Source schemaFile = new StreamSource(
                    new URL(getValueModelXMLSchemaURL()).openStream());
            Schema schema = schemaFactory.newSchema(schemaFile);

            // create a Validator instance, which can be used to validate an instance document
            Validator validator = schema.newValidator();

            // validate the DOM tree
            validator.validate(new DOMSource(document));
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            if (ex instanceof UnknownHostException) {
                return true;
            } else {
                return false;
            }
        }

        return true;
    }

    /**
     * gets the value of the <code>valueModelXMLSchemaURL</code> field
     *
     * @pre this <code>ValueModel</code> exists @post
     * <code>valueModelXMLSchemaURL</code> of this <code>ValueModel</code> is
     * returned
     *
     * @return <code>String</code> - <code>valueModelXMLSchemaURL</code> of this
     * <code>ValueModel</code>
     */
    public String getValueModelXMLSchemaURL() {
        return valueModelXMLSchemaURL;
    }

    /**
     * sets the value of the <code>valueModelXMLSchemaURL</code> field
     *
     * @pre argument <code>valueModelXMLSchemaURL</code> is a valid
     * <code>String</code> @post <code>valueModelXMLSchemaURL</code> of this
     * <code>ValueModel</code> is set to argument
     * <code>valueModelXMLSchemaURL</code>
     *
     * @param valueModelXMLSchemaURL value to which this
     * <code>ValueModel</code>'s <code>valueModelXMLSchemaURL</code> is set
     */
    public void setValueModelXMLSchemaURL(String valueModelXMLSchemaURL) {
        this.valueModelXMLSchemaURL = valueModelXMLSchemaURL;
    }

    /**
     *
     * @return
     */
    public boolean hasPositiveValue() {
        return getValue().compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     *
     * @return
     */
    public boolean hasZeroValue() {
        return getValue().compareTo(BigDecimal.ZERO) == 0;
    }

    /**
     *
     * @return
     */
    public boolean hasPositiveVarUnct() {
        return getOneSigma().compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     *
     * @return
     */
    public boolean hasPositiveSysUnct() {
        return getOneSigmaSys().compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     *
     * @return
     */
    public BigDecimal getNonNegativeValue() {
        return (hasPositiveValue() ? getValue() : BigDecimal.ZERO);
    }


    /**
     * @return the valueTree
     */
    public ExpTreeII getValueTree() {
        // valueTree is transient, so need to ensure its initialization
        if (valueTree == null) {
            valueTree = new ExpTreeII();
            valueTree.setNodeName(name);
        }

        if (valueTree.isValueNode()) {
            valueTree.setNodeValue(value);
        }
        return valueTree;
    }

    /**
     * @param valueTree the valueTree to set
     */
    public void setValueTree(ExpTreeII valueTree) {
        this.valueTree = valueTree;
        this.valueTree.setNodeName(name);
    }

    /**
     *
     * @return
     */
    public String differenceValueCalcs() {
        return "ValueTree differs for " + getName() + " by: " + (getValue().subtract(getValueTree().getNodeValue())).toEngineeringString();

    }

    /**
     *
     * @return
     */
    public BigDecimal differenceValueCalcsBigDecimal() {
        return /*
                 * "ValueTree differs for " + getName() + " by: " +
                 */ (getValue().subtract(getValueTree().getNodeValue()));

    }

    /**
     *
     * @param vm
     * @return
     */
    public static ValueModel[] cullNullsFromArray(ValueModel[] vm) {

        ArrayList<ValueModel> vmArrayList = new ArrayList<ValueModel>();
        ValueModel[] retVal = new ValueModel[0];

        if (vm != null) {
            for (int i = 0; i < vm.length; i++) {
                if (vm[i] != null) {
                    vmArrayList.add(vm[i]);
                }
            }
            retVal = vmArrayList.toArray(new ValueModel[vmArrayList.size()]);
        }

        return retVal;

    }

    /**
     * Removes ValueModel elements that have value == zero
     *
     * @param myArray
     * @return
     */
    public static ValueModel[] compressArrayOfValueModels(ValueModel[] myArray) {

        ValueModel[] myCulledArray = cullNullsFromArray(myArray);

        // sort by ValueModel Name field
        Arrays.sort(myCulledArray);

        // clean out any zero values
        ArrayList<ValueModel> arrayOfNonZeroModels = new ArrayList<ValueModel>();
        for (int i = 0; i < myCulledArray.length; i++) {
            if (myCulledArray[i].getValue().compareTo(BigDecimal.ZERO) != 0) {
                arrayOfNonZeroModels.add(myCulledArray[i]);
            }
        }
        // may need to check for empty ?? returns array of length zero
        int idx = 0;
        ValueModel[] retVal = new ValueModel[arrayOfNonZeroModels.size()];
        for (ValueModel v : arrayOfNonZeroModels) {
            retVal[idx++] = v;
        }

        return retVal;
    }

    /**
     *
     */
    @Override
    public void removeSelf() {
//        throw new UnsupportedOperationException( "Not supported yet." );
    }

    /**
     * @return the oneSigmaSys
     */
    public BigDecimal getOneSigmaSys() {
        // sept 2014
        if (oneSigmaSys == null) {
            oneSigmaSys = BigDecimal.ZERO;
        }
        return oneSigmaSys;
    }

    /**
     * @param oneSigmaSys the oneSigmaSys to set
     */
    public void setOneSigmaSys(BigDecimal oneSigmaSys) {
        this.oneSigmaSys = oneSigmaSys;
    }

    public double varianceOfLogRatio() {
        // eventually check if this valuemodel is type log or RR2
        return getOneSigmaAbs().divide(value, ReduxConstants.mathContext15).pow(2).doubleValue();
    }
}
