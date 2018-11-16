/*
 * SampleDateModel.java
 *
 * Created on February 5, 2008, 5:44 PM
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

import Jama.Matrix;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import static org.apache.commons.math3.special.Erf.erf;
import org.cirdles.mcLeanRegression.core.McLeanRegressionLineInterface;
import org.earthtime.Tripoli.dataModels.sessionModels.SessionCorrectedUnknownsSummary;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.aliquots.UPbReduxAliquot;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.YorkLineFit;
import org.earthtime.UPb_Redux.fractions.FractionI;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFraction;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFractionI;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.fractionReduction.ReductionHandler;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.fractionReduction.UPbFractionReducer;
import org.earthtime.UPb_Redux.utilities.comparators.IntuitiveStringComparator;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.Age206_238r;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.Age207_206r;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.Age207_235r;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.Age208_232r;
import org.earthtime.aliquots.AliquotInterface;
import org.earthtime.aliquots.ReduxAliquotInterface;
import org.earthtime.dataDictionaries.AnalysisMeasures;
import org.earthtime.dataDictionaries.Lambdas;
import org.earthtime.dataDictionaries.RadDates;
import org.earthtime.dataDictionaries.RadRatios;
import org.earthtime.dataDictionaries.SampleAnalysisTypesEnum;
import org.earthtime.dataDictionaries.TracerUPbRatiosAndConcentrations;
import org.earthtime.dataDictionaries.TracerUPbTypesEnum;
import org.earthtime.dataDictionaries.UThAnalysisMeasures;
import org.earthtime.exceptions.ETException;
import org.earthtime.fractions.ETFractionInterface;
import org.earthtime.matrices.matrixModels.AbstractMatrixModel;
import org.earthtime.matrices.matrixModels.CovarianceMatrixModel;
import org.earthtime.matrices.matrixModels.JacobianMatrixModel;
import org.earthtime.plots.McLeanRegressionLineFit;
import org.earthtime.plots.isochrons.IsochronModel;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.reduxLabData.ReduxLabData;
import org.earthtime.samples.SampleInterface;

/**
 * A
 * <code>SampleDateModel</code> contains all the scientific data and its unct
 * related to a <code>Sample</code>'s date as well as additional methods for
 * manipulating and publishing these values.
 *
 * @author James F. Bowring, javaDocs by Stan Gasque
 */
public class SampleDateModel extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables
    /**
     * identifies object in binary serialization
     */
    private static final long serialVersionUID = -7739770267101115814L;
    // Instance variables
    /**
     * name of the method to be invoked via reflection
     */
    private String methodName;
    /**
     * name of the date represented by this <code>SampleDateModel</code>
     */
    private String dateName;
    /**
     * the average weighted unct of this <code>SampleDateModel</code>'s value
     * squared
     */
    private BigDecimal meanSquaredWeightedDeviation;
    /**
     * the internal unct of this <code>SampleDateModel</code>'s value
     */
    private BigDecimal internalTwoSigmaUnct;
    // added Mar 2013

    /**
     *
     */
    protected BigDecimal internalTwoSigmaUnctWithStandardRatioVarUnct;
    /**
     * the internal uncertainty including tracer calibration of this
     * <code>SampleDateModel</code>'s value
     */
    private BigDecimal internalTwoSigmaUnctWithTracerCalibrationUnct;
    /**
     * the internal uncertainty including tracer calibration and decay of this
     * <code>SampleDateModel</code>'s value
     */
    private BigDecimal internalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct;
    /**
     * collection of names of the <code>Fractions</code> to be included in
     * calculations from given <code>Aliquots</code>
     */
    private Vector<String> includedFractionIDsVector;
    /**
     * brief explanation of this <code>SampleDateModel</code>
     */
    private String explanation;
    /**
     * brief comment regarding this <code>SampleDateModel</code>
     */
    private String comment;
    /**
     * flag regarding whether this <code>SampleDateModel</code> is preferred or
     * not
     */
    private boolean preferred;
    /**
     * Introduced to handle case of sample view of weighted means date
     * interpretatons
     */
    private boolean displayedAsGraph;
    // transient fields
    // sample is used in compilation mode
    private SampleInterface sample;
    // aliquot is used in analysis mode
    private AliquotInterface aliquot;
    private transient YorkLineFit yorkLineFit = null;
    // feb 2013
    // allows to differentiate among types so LAICPMS can use log-based analysis until we fully transition
    private SampleAnalysisTypesEnum sampleAnalysisType;
    // Feb 2017
    private transient McLeanRegressionLineInterface mcLeanRegressionLine;
    private static String unitsForYears = "Ma";
    private SortedSet<IsochronModel> isochronModels;
    // Oct 2018
    private boolean automaticIsochronSelection;
    private double[] ar48icntrs;
    private boolean automaticInitDelta234USelection;


    /**
     * creates a new instance of <code>SampleDateModel</code> with all of its
     * fields initialized to default values
     */
    public SampleDateModel() {
        super();
        this.meanSquaredWeightedDeviation = BigDecimal.ZERO;
        this.internalTwoSigmaUnct = BigDecimal.ZERO;
        this.internalTwoSigmaUnctWithTracerCalibrationUnct = BigDecimal.ZERO;
        this.internalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct = BigDecimal.ZERO;
        setIncludedFractionIDsVector(new Vector<>());
        explanation = "Explanation";
        comment = "Comment";
        preferred = false;
        displayedAsGraph = false;

        this.methodName = "";
        this.dateName = "";

        //this.unitsForYears = "Ma";
        this.isochronModels = new TreeSet<>();
        this.automaticIsochronSelection = true;
        this.ar48icntrs = new double[0];
        this.automaticInitDelta234USelection = true;
    }

    /**
     * creates a new instance of <code>SampleDateModel</code> with its      <code>
     * name</code>, <code>methodName</code>, <code>dateName</code>,      <code>
     * value</code>, <code>uncertaintyType</code>, and <code>oneSigma</code>
     * intialized to arguments <code>name</code>, <code>methodName</code>,
     * <code>dateName</code>, <code>value</code>, <code>uncertaintyType</code>,
     * and <code>oneSigma</code> respectively, and its remaining fields
     * initialized to default values.
     *
     * @param name name of this <code>SampleDateModel</code> to which
     * <code>name</code> will be set
     * @param methodName name of the method of this      <code>SampleDateModel
     * </code> to which <code>methodName</code> will be set
     * @param dateName name of the date of this <code>SampleDateModel</code> to
     * which <code>dateName</code> will be set
     * @param value value of this <code>SampleDateModel</code> to which
     * <code>value</code> will be set
     * @param uncertaintyType uncertainty type of this
     * <code>SampleDateModel</code> to which <code>uncertaintyType</code> will
     * be set
     * @param oneSigma one sigma unct of this <code>SampleDateModel</code> to
     * which <code>oneSigma</code> will be set
     */
    public SampleDateModel(
            String name,
            String methodName,
            String dateName,
            BigDecimal value,
            String uncertaintyType,
            BigDecimal oneSigma) {

        this();
        // these setters do work
        setName(name);
        setMethodName(methodName);
        setDateName(dateName);
        setValue(value);
        setUncertaintyType(uncertaintyType);
        setOneSigma(oneSigma);
    }

    /**
     * returns a deep copy of this <code>SampleDateModel</code>.
     *
     * @pre this <code>SampleDateModel</code> exists
     * @post returns a new <code>SampleDateModel</code> with identical data to
     * this <code>SampleDateModel</code>
     *
     * @return <code>SampleDateModel</code> - a new <code>SampleDateModel</code>
     * whose fields match those of this <code>SampleDateModel</code>
     */
    @Override
    public SampleDateModel copy() {
        SampleDateModel retModel = new SampleDateModel(
                getName(),
                getMethodName(),
                getDateName(),
                getValue(),
                getUncertaintyType(),
                getOneSigma());
        retModel.setMeanSquaredWeightedDeviation(getMeanSquaredWeightedDeviation());
        retModel.setInternalTwoSigmaUnct(getInternalTwoSigmaUnct());
        retModel.setInternalTwoSigmaUnctWithTracerCalibrationUnct(getInternalTwoSigmaUnctWithTracerCalibrationUnct());
        retModel.setInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct(getInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct());
        retModel.setIncludedFractionIDsVector(getIncludedFractionIDsVector());
        retModel.setExplanation(getExplanation());
        retModel.setComment(getComment());
        retModel.setPreferred(isPreferred());

        return retModel;
    }

    /**
     * compares this <code>SampleDateModel</code>'s <code>name</code> and      <code>
     * preferred</code> fields to those of argument
     * <code>sampleDateModel</code>.
     *
     * @pre argument <code>sampleDateModel</code> is a valid
     * <code>SampleDateModel</code>
     * @post returns the lexicographical equivalence between this      <code>
     *          sampleDateModel</code>'s <code>name</code> and      <code>
     *          preferred</code> fields and argument <code>sampleDateModel</code>'s
     * <code>name</code> and <code>preferred</code> fields
     *
     * @param sampleDateModel <code>SampleDateModel</code> to compare this
     * <code>SampleDateModel</code> against
     * @return <code>int</code> - -1 if this <code>SampleDateModel</code>'s
     * <code>preferred</code> status (0 if <code>true</code>, else 1) and
     * <code>name</code> is lexicographically less than argument
     * <code>sampleDateModel</code>, 0 if they are equal, and 1 if it is greater
     * than argument <code>sampleDateModel</code>
     * @throws java.lang.ClassCastException ClassCastException
     */
    @Override
    public int compareTo(ValueModel sampleDateModel) throws ClassCastException {
        // prepend a 0 for preferred = true, 1 otherwise        
        String argsName = (String) (((SampleDateModel) sampleDateModel).isPreferred() ? "0" : "1") + ((SampleDateModel) sampleDateModel).getName();
        String myName = (String) (this.isPreferred() ? "0" : "1") + this.getName();
        return myName.trim().compareToIgnoreCase(argsName.trim());
    }

    /**
     * compares this <code>SampleDateModel</code> with argument
     * <code>sampleDateModel</code> lexicographically by <code>name</code>.
     *
     * @pre argument <code>sampleDateModel</code> is a valid
     * <code>SampleDateModel</code>
     * @post returns <code>true</code> if this <code>SampleDateModel</code> is
     * argument <code>sampleDateModel</code> or if their <code>name</code>
     * fields are lexicographically equivalent, else <code>false</code>
     *
     * @param sampleDateModel <code>SampleDateModel</code> to compare this
     * <code>SampleDateModel</code> against
     * @return <code>boolean</code> - <code>true</code> if this
     * <code>SampleDateModel</code> is argument <code>sampleDateModel</code> or
     * if their <code>name</code> fields are lexicographically equivalent, else
     * <code>false</code>
     */
    @Override
    public boolean equals(Object sampleDateModel) {
        //check for self-comparison
        if (this == sampleDateModel) {
            return true;
        }
        if (!(sampleDateModel instanceof SampleDateModel)) {
            return false;
        }

        ValueModel argValueModel = (SampleDateModel) sampleDateModel;

        return (this.compareTo(argValueModel) == 0);
    }

    /**
     * returns 0 as the hashcode for this <code>SampleDateModel</code>.
     * Implemented to meet equivalency requirements as documented by
     * <code>java.lang.Object</code>
     *
     * @pre this <code>SampleDateModel</code> exists
     * @post hashcode of 0 is returned for this <code>SampleDateModel</code>
     *
     * @return <code>int</code> - 0
     */
    @Override
    public int hashCode() {
        return 0;
    }

    /**
     * returns this <code>SampleDateModel</code> as a <code>String</code>
     * consisting of its <code>name</code>.
     *
     * @pre this <code>SampleDateModel</code> exists
     * @post returns the <code>name</code> of this <code>SampleDateModel</code>
     *
     * @return <code>String</code> - <code>name</code> of this
     * <code>SampleDateModel</code>
     */
    @Override
    public String toString() {
        return getName();
    }

    /**
     * gets the <code>meanSquaredWeightedDeviation</code> of this
     * <code>SampleDateModel</code>.
     *
     * @pre this <code>SampleDateModel</code> exists
     * @post returns the <code>meanSquaredWeightedDeviation</code> of this
     * <code>SampleDateModel</code>
     *
     * @return <code>BigDecimal</code> -
     * <code>meanSquaredWeightedDeviation</code> of this
     * <code>SampleDateModel</code>
     */
    public BigDecimal getMeanSquaredWeightedDeviation() {
        return meanSquaredWeightedDeviation;
    }

    /**
     * sets the <code>meanSquaredWeightedDeviation</code> of this
     * <code>SampleDateModel</code> to argument
     * <code>meanSquaredWeightedDeviation</code>.
     *
     * @pre argument <code>meanSquaredWeightedDeviation</code> is a valid
     * <code>BigDecimal</code>
     * @post <code>meanSquaredWeightedDeviation</code> of this
     * <code>SampleDateModel</code> is set to argument
     * <code>menaSquaredWeightedDeviation</code>
     *
     * @param meanSquaredWeightedDeviation value to set this
     * <code>SampleDateModel</code>'s <code>meanSquaredWeightedDeviation</code>
     * to
     */
    public void setMeanSquaredWeightedDeviation(BigDecimal meanSquaredWeightedDeviation) {
        this.meanSquaredWeightedDeviation = meanSquaredWeightedDeviation;
    }

    /**
     * gets the <code>internalTwoSigmaUnctWithTracerCalibrationUnct</code> of
     * this <code>SampleDateModel</code>.
     *
     * @pre this <code>SampleDateModel</code> exists
     * @post returns the
     * <code>internalTwoSigmaUnctWithTracerCalibrationUnct</code> of this
     * <code>SampleDateModel</code>
     *
     * @return <code>BigDecimal</code> -
     * <code>internalTwoSigmaUnctWithTracerCalibrationUnct</code> of this
     * <code>SampleDateModel</code>
     */
    public BigDecimal getInternalTwoSigmaUnctWithTracerCalibrationUnct() {
        return internalTwoSigmaUnctWithTracerCalibrationUnct;
    }

    /**
     * sets the <code>internalTwoSigmaUnctWithTracerCalibrationUnct</code> of
     * this <code>SampleDateModel</code> to argument
     * <code>internalTwoSigmaUnctWithTracerCalibrationUnct</code>.
     *
     * @pre argument <code>internalTwoSigmaUnctWithTracerCalibrationUnct</code>
     * is a valid <code>BigDecimal</code>
     * @post <code>internalTwoSigmaUnctWithTracerCalibrationUnct</code> of this
     * <code>SampleDateModel</code> is set to argument
     * <code>internalTwoSigmaUnctWithTracerCalibrationUnct</code>
     *
     * @param internalTwoSigmaUnctWithTracerCalibrationUnct value to set this
     * <code>SampleDateModel</code>'s
     * <code>internalTwoSigmaUnctWithTracerCalibrationUnct</code> to
     */
    public void setInternalTwoSigmaUnctWithTracerCalibrationUnct(BigDecimal internalTwoSigmaUnctWithTracerCalibrationUnct) {
        this.internalTwoSigmaUnctWithTracerCalibrationUnct = internalTwoSigmaUnctWithTracerCalibrationUnct;
    }

    /**
     * gets the
     * <code>internalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct</code>
     * of this <code>SampleDateModel</code>.
     *
     * @pre this <code>SampleDateModel</code> exists
     * @post returns the
     * <code>internalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct</code>
     * of this <code>SampleDateModel</code>
     *
     * @return <code>BigDecimal</code> -
     * <code>internalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct</code>
     * of this <code>SampleDateModel</code>
     */
    public BigDecimal getInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct() {
        return internalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct;
    }

    /**
     * sets the
     * <code>internalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct</code>
     * of this <code>SampleDateModel</code> to argument
     * <code>internalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct</code>.
     *
     * @pre argument
     * <code>internalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct</code>
     * is a valid <code>BigDecimal</code>
     * @post
     * <code>internalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct</code>
     * of this <code>SampleDateModel</code> is set to argument
     * <code>internalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct</code>
     *
     * @param internalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct
     * value to set this <code>SampleDateModel</code>'s
     * <code>internalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct</code>
     * to
     */
    public void setInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct(BigDecimal internalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct) {
        this.internalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct = internalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct;
    }

    /**
     * gets the <code>explanation</code> of this <code>SampleDateModel</code>.
     *
     * @pre this <code>SampleDateModel</code> exists
     * @post returns the <code>explanation</code> of this
     * <code>SampleDateModel</code>
     *
     * @return <code>String</code> - <code>explanation</code> of this
     * <code>SampleDateModel</code>
     */
    public String getExplanation() {
        return explanation;
    }

    /**
     * sets the <code>explanation</code> of this <code>SampleDateModel</code> to
     * argument <code>explanation</code>.
     *
     * @pre argument <code>explanation</code> is a valid <code>String</code>
     * @post <code>explanation</code> of this <code>SampleDateModel</code> is
     * set to argument <code>explanation</code>
     *
     * @param explanation value to set this <code>SampleDateModel</code>'s
     * <code>explanation</code> to
     */
    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    /**
     * gets the <code>comment</code> of this <code>SampleDateModel</code>.
     *
     * @pre this <code>SampleDateModel</code> exists
     * @post returns the <code>comment</code> of this
     * <code>SampleDateModel</code>
     *
     * @return <code>String</code> - <code>comment</code> of this
     * <code>SampleDateModel</code>
     */
    public String getComment() {
        return comment;
    }

    /**
     * sets the <code>comment</code> of this <code>SampleDateModel</code> to
     * argument <code>comment</code>.
     *
     * @pre argument <code>comment</code> is a valid <code>String</code>
     * @post <code>comment</code> of this <code>SampleDateModel</code> is set to
     * argument <code>comment</code>
     *
     * @param comment value to set this <code>SampleDateModel</code>'s
     * <code>comment</code> to
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * gets the <code>preferred</code> field of this
     * <code>SampleDateModel</code>.
     *
     * @pre this <code>SampleDateModel</code> exists
     * @post returns the <code>preferred</code> field of this
     * <code>SampleDateModel</code>
     *
     * @return <code>boolean</code> - <code>preferred</code> field of this
     * <code>SampleDateModel</code>
     */
    public boolean isPreferred() {
        return preferred;
    }

    /**
     * sets the <code>preferred</code> field of this
     * <code>SampleDateModel</code> to argument <code>preferred</code>.
     *
     * @pre argument <code>preferred</code> is a valid <code>boolean</code>
     * @post <code>preferred</code> of this <code>SampleDateModel</code> is set
     * to argument <code>preferred</code>
     *
     * @param preferred value to set this <code>SampleDateModel</code>'s
     * <code>preferred</code> field to
     */
    public void setPreferred(boolean preferred) {
        this.preferred = preferred;
    }

    /**
     * gets the <code>internalTwoSigmaUnct</code> of this
     * <code>SampleDateModel</code>.
     *
     * @pre this <code>SampleDateModel</code> exists
     * @post returns the <code>internalTwoSigmaUnct</code> of this
     * <code>SampleDateModel</code>
     *
     * @return <code>BigDecimal</code> - <code>internalTwoSigmaUnct</code> of
     * this <code>SampleDateModel</code>
     */
    public BigDecimal getInternalTwoSigmaUnct() {
        return internalTwoSigmaUnct;
    }

    /**
     * sets the <code>internalTwoSigmaUnct</code> of this
     * <code>SampleDateModel</code> to argument
     * <code>internalTwoSigmaUnct</code>.
     *
     * @pre argument <code>internalTwoSigmaUnct</code> is a valid
     * <code>BigDecimal</code>
     * @post <code>internalTwoSigmaUnct</code> of this
     * <code>SampleDateModel</code> is set to argument
     * <code>internalTwoSigmaUnct</code>
     *
     * @param internalTwoSigmaUnct value to set this
     * <code>SampleDateModel</code>'s <code>internalTwoSigmaUnct</code> to
     */
    public void setInternalTwoSigmaUnct(BigDecimal internalTwoSigmaUnct) {
        this.internalTwoSigmaUnct = internalTwoSigmaUnct;
    }

    /**
     * gets the <code>includedFractionIDsVector</code> of this
     * <code>SampleDateModel</code>.
     *
     * @pre this <code>SampleDateModel</code> exists
     * @post returns the <code>includedFractionIDsVector</code> of this
     * <code>SampleDateModel</code>
     *
     * @return <code>Vector</code> - <code>includedFractionIDsVector</code> of
     * this <code>SampleDateModel</code> as a collection of <code>String</code>
     */
    public Vector<String> getIncludedFractionIDsVector() {
        return includedFractionIDsVector;
    }

    /**
     * sets the <code>includedFractionIDsVector</code> of this
     * <code>SampleDateModel</code> to argument
     * <code>includedFractionIDsVector</code>.
     *
     * @pre argument <code>includedFractionIDsVector</code> is a valid
     * <code>Vector</code> of <code>String</code>
     * @post <code>includedFractionIDsVector</code> of this
     * <code>SampleDateModel</code> is set to argument
     * <code>includedFractionIDsVector</code>
     *
     * @param includedFractionIDsVector value to set this
     * <code>SampleDateModel</code>'s <code>includedFractionIDsVector</code> to
     */
    public void setIncludedFractionIDsVector(Vector<String> includedFractionIDsVector) {

        // remove from activefractionIDs any fraction with 0 date
        Vector<String> zeroFractionDates = new Vector<String>();
        for (int i = 0; i < includedFractionIDsVector.size(); i++) {
            try {
                if (!fractionDateIsPositive(sample.getSampleFractionByName(includedFractionIDsVector.get(i)))) {
                    zeroFractionDates.add(includedFractionIDsVector.get(i));
                }
            } catch (Exception e) {
            }
        }
        for (int i = 0; i < zeroFractionDates.size(); i++) {
            includedFractionIDsVector.remove(zeroFractionDates.get(i));
        }
        this.includedFractionIDsVector = includedFractionIDsVector;
    }

    /**
     * searches for the <code>Fraction</code> whose name matches argument
     * <code>fractionID</code> within this <code>SampleDateModel</code>'s
     * <code>includedFractionIDsVector</code> and returns a <code>boolean</code>
     * indicating whether that <code>Fraction</code> is a part of the collection
     * or not.
     *
     * @pre argument <code>fractionID</code> is a valid <code>String</code>
     * @post returns <code>true</code> if argument <code>fractionID</code> is
     * the name of a <code>Fraction</code> found in
     * <code>includedFractionIDsVector</code>, else <code>false</code>
     *
     * @param fractionID name of <code>Fraction</code> to search for within
     * <code>includedFractionIDsVector</code>
     * @return <code>boolean</code> - <code>true</code> if argument      <code>
     *          fractionID</code> is the name of a <code>Fraction</code> found in
     * <code>includedFractionIDsVector</code>, else <code>false</code>
     */
    public boolean includesFractionByName(String fractionID) {
        boolean retVal = false;
        try {
            for (String fID : getIncludedFractionIDsVector()) {
                if (fID.equalsIgnoreCase(fractionID)) {
                    retVal = true;
                    break;
                }
            }
        } catch (Exception e) {
        }

        return retVal;
    }

    /**
     * toggles the <code>Fraction</code> specified by argument
     * <code>fractionID</code> into and out of
     * <code>includedFractionIDsVector</code>, adding it if it is not currently
     * a part of the collection and removing it if it is.
     *
     * @return
     * @pre argument <code>fractionID</code> is a valid <code>String</code> and
     * argument <code>aliquot</code> is a valid <code>Aliquot</code>
     * @post <code>Fraction</code> specified by argument <code>fractionID</code>
     * is removed from <code>includedFractionIDsVector</code> if it is currently
     * a member or added to it if it is not currently a member
     *
     * @param fractionID name of the <code>Fraction</code> to toggle
     */
    public boolean ToggleAliquotFractionByName(String fractionID) {
        boolean retval = false;

        if (includesFractionByName(fractionID)) {
            getIncludedFractionIDsVector().remove(fractionID);
        } else {
            getIncludedFractionIDsVector().add(fractionID);
            retval = true;
        }
        Collections.sort(getIncludedFractionIDsVector(), new IntuitiveStringComparator<String>());

//        CalculateDateInterpretationForAliquot();
        return retval;
    }

    /**
     *
     * @param fractionID
     * @return
     */
    public boolean ToggleSampleFractionByName(String fractionID) {
        boolean retval = false;

        if (includesFractionByName(fractionID)) {
            getIncludedFractionIDsVector().remove(fractionID);
        } else {
            getIncludedFractionIDsVector().add(fractionID);
            retval = true;
        }
        Collections.sort(getIncludedFractionIDsVector(), new IntuitiveStringComparator<String>());

        CalculateDateInterpretationForSample();

        return retval;
    }

    /**
     * zeroes out relative <code>BigDecimal</code> fields of this
     * <code>SampleDateModel</code>.
     *
     * @pre this <code>SampleDateModel</code> exists
     * @post this <code>SampleDateModel</code>'s <code>value</code>,
     * <code>oneSigma</code>, <code>internalTwoSigmaUnct</code>,
     * <code>internalTwoSigmaUnctWithTracerCalibrationUnct</code>,
     * <code>internalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct</code>,
     * and <code>meanSquaredWeightedDeviation</code> fields are zeroed out
     */
    private void ZeroAllValues() {
        // reset
        setValue(BigDecimal.ZERO);
        setOneSigma(BigDecimal.ZERO);
        setInternalTwoSigmaUnct(BigDecimal.ZERO);
        setInternalTwoSigmaUnctWithTracerCalibrationUnct(BigDecimal.ZERO);
        setInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct(BigDecimal.ZERO);
        setMeanSquaredWeightedDeviation(BigDecimal.ZERO);
    }

    // section for individual sample date model methods to be invoked by reflection
    /**
     * sets this <code>SampleDateModel</code>'s <code>name</code>,
     * <code>value</code>, <code>oneSigma</code>, and
     * <code>internalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct</code>
     * to those of the first <code>Fraction</code> found in
     * <code>includedFractionIDsVector</code> via reflection.
     *
     * @pre argument <code>aliquot</code> is a valid <code>Aliquot</code>
     * containing <code>Fractions</code> whose names correspond with those found
     * in <code>includedFractionIDsVector</code>
     * @post this <code>SampleDateModel</code>'s <code>name</code>,
     * <code>value</code>, <code>oneSigma</code>, and
     * <code>internalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct</code>
     * will be set to those of the first <code>Fraction</code> found in argument
     * <code>aliquot</code> whose name is listed in
     * <code>includedFractionIDsVector</code>
     */
    public void CalculateDateInterpretationForAliquot() {
        // http://java.sun.com/developer/technicalArticles/ALT/Reflection/index.html

        // check to make sure there are fractions with positive dates
        if (includedFractionIDsVector.size() > 0) {
            // create vector of fractions based on sample date model fraction list
            Vector<ETFractionInterface> includedFractions = new Vector<>();
            for (String fID : includedFractionIDsVector) {
                includedFractions.add(((ReduxAliquotInterface) aliquot).getAliquotFractionByName(fID));
            }

            // special case to detect upper/lower intercept
            if (methodName.equalsIgnoreCase("UpperIntercept")) {
                UpperIntercept(includedFractions, //
                        aliquot.getASampleDateModelByName("lower intercept"));
            } else {
                try {
                    Method meth
                            = this.getClass().getMethod(//
                                    methodName,
                                    new Class[]{Vector.class});
                    meth.invoke(this, new Object[]{includedFractions});
                } catch (Throwable e) {
                    System.err.println(e + " For: " + getMethodName() + "  in CalculateDateInterpretationForAliquot");
                }
            }
        } else {
            // new ETWarningDialog("No positive dates found.").setVisible(true);
        }
    }

    /**
     *
     */
    public void CalculateDateInterpretationForSample() {
        // http://java.sun.com/developer/technicalArticles/ALT/Reflection/index.html

        // check to make sure there are fractions with positive dates
        if (includedFractionIDsVector.size() > 0) {
            // create vector of fractions based on sample date model fraction list
            Vector<ETFractionInterface> includedFractions = new Vector<>();
            for (String fID : includedFractionIDsVector) {
                includedFractions.add(sample.getSampleFractionByName(fID));
            }

            // special case to detect upper/lower intercept
            if (getMethodName().equalsIgnoreCase("UpperIntercept")) {
                UpperIntercept(includedFractions, //
                        sample.getSampleDateModelByName("lower intercept"));
            } else {
                try {
                    Method meth
                            = this.getClass().getMethod(//
                                    getMethodName(),
                                    new Class[]{Vector.class});
                    meth.invoke(this, new Object[]{includedFractions});
                } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    System.err.println(e + " For: " + getMethodName() + "  in CalculateDateInterpretationForSample");
                }
            }
        } else {
            // new ETWarningDialog("No positive dates found.").setVisible(true);   
        }
    }

    /**
     * zeroes all values and sets this <code>SampleDateModel</code>'s
     * <code>name</code>, <code>value</code>, <code>oneSigma</code>, and
     * <code>internalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct</code>
     * to those of the first <code>Fraction</code> found in argument
     * <code>myFractions</code>.
     *
     * @pre argument <code>myFractions</code> is a valid collection of
     * <code>Fractions</code>
     * @post this <code>SampleDateModel</code>'s fields are zeroed out and its
     * <code>name</code>, <code>value</code>, <code>oneSigma</code>, and
     * <code>internalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct</code>
     * are set to those of the first <code>Fraction</code> found in argument
     * <code>myFractions</code>
     *
     * @param myFractions the collection of <code>Fractions</code> that will be
     * used to set this      <code>SampleDateModel</code>'s fields
     */
    public void SA206_238(Vector<FractionI> myFractions) {
        ZeroAllValues();
        String radiogenicIsotopeDateName = RadDates.age206_238r.getName();

        if (myFractions.size() > 0) {
            calculateSingleDateInterpretation(radiogenicIsotopeDateName, myFractions.get(0));
        }
    }

    /**
     * zeroes all values and sets this <code>SampleDateModel</code>'s
     * <code>name</code>, <code>value</code>, <code>oneSigma</code>, and
     * <code>internalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct</code>
     * to those of the first <code>Fraction</code> found in argument
     * <code>myFractions</code>.
     *
     * @pre argument <code>myFractions</code> is a valid collection of
     * <code>Fractions</code>
     * @post this <code>SampleDateModel</code>'s fields are zeroed out and its
     * <code>name</code>, <code>value</code>, <code>oneSigma</code>, and
     * <code>internalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct</code>
     * are set to those of the first <code>Fraction</code> found in argument
     * <code>myFractions</code>
     *
     * @param myFractions the collection of <code>Fractions</code> that will be
     * used to set this      <code>SampleDateModel</code>'s fields
     */
    public void SA207_235(Vector<FractionI> myFractions) {
        ZeroAllValues();
        String radiogenicIsotopeDateName = RadDates.age207_235r.getName();

        if (myFractions.size() > 0) {
            calculateSingleDateInterpretation(radiogenicIsotopeDateName, myFractions.get(0));
        }

    }

    /**
     * zeroes all values and sets this <code>SampleDateModel</code>'s
     * <code>name</code>, <code>value</code>, <code>oneSigma</code>, and
     * <code>internalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct</code>
     * to those of the first <code>Fraction</code> found in argument
     * <code>myFractions</code>.
     *
     * @pre argument <code>myFractions</code> is a valid collection of
     * <code>Fractions</code>
     * @post this <code>SampleDateModel</code>'s fields are zeroed out and its
     * <code>name</code>, <code>value</code>, <code>oneSigma</code>, and
     * <code>internalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct</code>
     * are set to those of the first <code>Fraction</code> found in argument
     * <code>myFractions</code>
     *
     * @param myFractions the collection of <code>Fractions</code> that will be
     * used to set this      <code>SampleDateModel</code>'s fields
     */
    public void SA207_206(Vector<FractionI> myFractions) {
        ZeroAllValues();
        String radiogenicIsotopeDateName = RadDates.age207_206r.getName();

        if (myFractions.size() > 0) {
            calculateSingleDateInterpretation(radiogenicIsotopeDateName, myFractions.get(0));
        }
    }

    /**
     * zeroes all values and sets this <code>SampleDateModel</code>'s
     * <code>name</code>, <code>value</code>, <code>oneSigma</code>, and
     * <code>internalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct</code>
     * to those of the first <code>Fraction</code> found in argument
     * <code>myFractions</code>.
     *
     * @pre argument <code>myFractions</code> is a valid collection of
     * <code>Fractions</code>
     * @post this <code>SampleDateModel</code>'s fields are zeroed out and its
     * <code>name</code>, <code>value</code>, <code>oneSigma</code>, and
     * <code>internalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct</code>
     * are set to those of the first <code>Fraction</code> found in argument
     * <code>myFractions</code>
     *
     * @param myFractions the collection of <code>Fractions</code> that will be
     * used to set this      <code>SampleDateModel</code>'s fields
     */
    public void SA208_232(Vector<FractionI> myFractions) {
        ZeroAllValues();
        String radiogenicIsotopeDateName = RadDates.age208_232r.getName();

        if (myFractions.size() > 0) {
            calculateSingleDateInterpretation(radiogenicIsotopeDateName, myFractions.get(0));
        }
    }

    /**
     * provides a delegate to calculate single date interpreations; is called
     * during date calculation if the given <code>Aliquot</code> has not yet
     * been analyzed. uses the provided fraction
     *
     * @pre argument <code>radiogenicIsotopeDateName</code> is the name of a
     * <code>radiogenicIsotopeDate</code> found in argument
     * <code>fraction</code>
     * @post this <code>SampleDateModel</code>'s <code>value</code> and various
     * unct fields are set according to data from argument <code>fraction</code>
     *
     * @param radiogenicIsotopeDateName name of the radiogenic isotope date
     * @param fraction <code>Fraction</code> that will be used to set internal
     * unct
     */
    private void calculateSingleDateInterpretation(
            String radiogenicIsotopeDateName,
            ETFractionInterface fraction) {

        setInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct(//
                ((UPbFractionI) fraction).//
                        getRadiogenicIsotopeDateWithAllUnctByName(radiogenicIsotopeDateName).getTwoSigmaAbs());

        setInternalTwoSigmaUnctWithTracerCalibrationUnct(//
                ((UPbFractionI) fraction).//
                        getRadiogenicIsotopeDateWithTracerUnctByName(radiogenicIsotopeDateName).getTwoSigmaAbs());

        setInternalTwoSigmaUnct(fraction.//
                getRadiogenicIsotopeDateByName(radiogenicIsotopeDateName).getTwoSigmaAbs());
        setOneSigma(fraction.//
                getRadiogenicIsotopeDateByName(radiogenicIsotopeDateName).getOneSigmaAbs());
        setValue(fraction.//
                getRadiogenicIsotopeDateByName(radiogenicIsotopeDateName).getValue());

    }

    /**
     * Replacement method from N McLean dated 10.May.2011 "Evaluating Weighted
     * Means with Systematic Uncertainties in ET_Redux"
     *
     * @param myFractions
     * @param radiogenicIsotopeDateName
     * @param partialDerivativeNames the value of partialDerivativeNames
     */
    private LogWMresults calculateWeightedMeansWithMSWD(
            Vector<ETFractionInterface> myFractions, String radiogenicIsotopeDateName, ArrayList<String> partialDerivativeNames) //
            throws ETException {

        // to handle pre-march-2013 cases mainly differentiates from TRIPOLIZED = within Redux
        if (sampleAnalysisType == null) {
            sampleAnalysisType = SampleAnalysisTypesEnum.COMPILED;
        }

        LogWMresults logWMresults = null;
        // TRIPOLIZED is the sample type given to compiled super samples fully processed in redux == PROJECT sample
        if (sampleAnalysisType.compareTo(SampleAnalysisTypesEnum.TRIPOLIZED) == 0) {
            logWMresults = calculateWeightedMeansWithMSWDforLogRatioBasedData(myFractions, radiogenicIsotopeDateName, partialDerivativeNames);
        } else {
            calculateWeightedMeansWithMSWDforRatioBasedData(myFractions, radiogenicIsotopeDateName);
        }

        return logWMresults;
    }

    /**
     * @return the internalTwoSigmaUnctWithStandardRatioVarUnct
     */
    public BigDecimal getInternalTwoSigmaUnctWithStandardRatioVarUnct() {
        return internalTwoSigmaUnctWithStandardRatioVarUnct;
    }

    /**
     * @param internalTwoSigmaUnctWithStandardRatioVarUnct the
     * internalTwoSigmaUnctWithStandardRatioVarUnct to set
     */
    public void setInternalTwoSigmaUnctWithStandardRatioVarUnct(BigDecimal internalTwoSigmaUnctWithStandardRatioVarUnct) {
        this.internalTwoSigmaUnctWithStandardRatioVarUnct = internalTwoSigmaUnctWithStandardRatioVarUnct;
    }

    private class LogWMresults {

        private double logRatioMean;
        private double logRatioMeanOneSigmaAnalytical;
        private double logRatioMeanOneSigmaAnalyticalPlusInterStd;
        private double logRatioMeanOneSigmaAnalyticalPlusInterStdPlusStd;
        private double logRatioMeanOneSigmaAnalyticalPlusInterStdPlusStdPlusLambda;
        private double MSWD;

        public LogWMresults() {
        }

        /**
         * @return the logRatioMean
         */
        public double getLogRatioMean() {
            return logRatioMean;
        }

        /**
         * @param logRatioMean the logRatioMean to set
         */
        public void setLogRatioMean(double logRatioMean) {
            this.logRatioMean = logRatioMean;
        }

        /**
         * @return the logRatioMeanOneSigmaAnalytical
         */
        public double getLogRatioMeanOneSigmaAnalytical() {
            return logRatioMeanOneSigmaAnalytical;
        }

        /**
         * @param logRatioMeanOneSigmaAnalytical the
         * logRatioMeanOneSigmaAnalytical to set
         */
        public void setLogRatioMeanOneSigmaAnalytical(double logRatioMeanOneSigmaAnalytical) {
            this.logRatioMeanOneSigmaAnalytical = logRatioMeanOneSigmaAnalytical;
        }

        /**
         * @return the logRatioMeanOneSigmaAnalyticalPlusInterStd
         */
        public double getLogRatioMeanOneSigmaAnalyticalPlusInterStd() {
            return logRatioMeanOneSigmaAnalyticalPlusInterStd;
        }

        /**
         * @param logRatioMeanOneSigmaAnalyticalPlusInterStd the
         * logRatioMeanOneSigmaAnalyticalPlusInterStd to set
         */
        public void setLogRatioMeanOneSigmaAnalyticalPlusInterStd(double logRatioMeanOneSigmaAnalyticalPlusInterStd) {
            this.logRatioMeanOneSigmaAnalyticalPlusInterStd = logRatioMeanOneSigmaAnalyticalPlusInterStd;
        }

        /**
         * @return the logRatioMeanOneSigmaAnalyticalPlusInterStdPlusStd
         */
        public double getLogRatioMeanOneSigmaAnalyticalPlusInterStdPlusStd() {
            return logRatioMeanOneSigmaAnalyticalPlusInterStdPlusStd;
        }

        /**
         * @param logRatioMeanOneSigmaAnalyticalPlusInterStdPlusStd the
         * logRatioMeanOneSigmaAnalyticalPlusInterStdPlusStd to set
         */
        public void setLogRatioMeanOneSigmaAnalyticalPlusInterStdPlusStd(double logRatioMeanOneSigmaAnalyticalPlusInterStdPlusStd) {
            this.logRatioMeanOneSigmaAnalyticalPlusInterStdPlusStd = logRatioMeanOneSigmaAnalyticalPlusInterStdPlusStd;
        }

        /**
         * @return the
         * logRatioMeanOneSigmaAnalyticalPlusInterStdPlusStdPlusLambda
         */
        public double getLogRatioMeanOneSigmaAnalyticalPlusInterStdPlusStdPlusLambda() {
            return logRatioMeanOneSigmaAnalyticalPlusInterStdPlusStdPlusLambda;
        }

        /**
         * @param logRatioMeanOneSigmaAnalyticalPlusInterStdPlusStdPlusLambda
         * the logRatioMeanOneSigmaAnalyticalPlusInterStdPlusStdPlusLambda to
         * set
         */
        public void setLogRatioMeanOneSigmaAnalyticalPlusInterStdPlusStdPlusLambda(double logRatioMeanOneSigmaAnalyticalPlusInterStdPlusStdPlusLambda) {
            this.logRatioMeanOneSigmaAnalyticalPlusInterStdPlusStdPlusLambda = logRatioMeanOneSigmaAnalyticalPlusInterStdPlusStdPlusLambda;
        }

        /**
         * @return the MSWD
         */
        public double getMSWD() {
            return MSWD;
        }

        /**
         * @param MSWD the MSWD to set
         */
        public void setMSWD(double MSWD) {
            this.MSWD = MSWD;
        }
    }

    private LogWMresults calculateWeightedMeansWithMSWDforLogRatioBasedData(
            Vector<ETFractionInterface> myFractions,
            String radiogenicIsotopeDateName,
            ArrayList<String> partialDerivativeNames) //
            throws ETException {

        NumberFormat matF = new DecimalFormat("0.000000000E00;-0.000000000E00");

        LogWMresults logWMresults = new LogWMresults();

        SortedMap<RadRatios, SessionCorrectedUnknownsSummary> sessionCorrectedUnknownsSummaries
                = //
                UPbFractionReducer.getInstance().getSessionCorrectedUnknownsSummaries();

        if (sessionCorrectedUnknownsSummaries.size() > 0) {
            try {
                RadRatios ratioName = RadRatios.valueOf(radiogenicIsotopeDateName.replace("age", "r"));

                SessionCorrectedUnknownsSummary sessionCorrectedUnknownsSummary
                        =//
                        sessionCorrectedUnknownsSummaries.get(ratioName);

                Matrix unknownsAnalyticalCovarianceSu = sessionCorrectedUnknownsSummary.getUnknownsAnalyticalCovarianceSu();
                Map<String, Integer> unknownFractionIDs = sessionCorrectedUnknownsSummary.getUnknownFractionIDs();
                Matrix unknownsLogRatioMeans = sessionCorrectedUnknownsSummary.getUnknownsLogRatioMeans();
                //ValueModel standardRatio = sessionCorrectedUnknownsSummary.getStandardRatio();
                double varianceOfStandardLogRatio = sessionCorrectedUnknownsSummary.getVarianceOfStandardLogRatio();

                // we are given a list of fractions to use in weighted mean, so we need to extract them from the matrices
                ArrayList<Integer> activeFractionIndices = new ArrayList<>();
                ArrayList<ETFractionInterface> activeFractions = new ArrayList<>();

                for (int i = 0; i < myFractions.size(); i++) {
                    if (unknownFractionIDs.containsKey(myFractions.get(i).getFractionID().trim())) {
                        activeFractionIndices.add(unknownFractionIDs.get(myFractions.get(i).getFractionID().trim()));
                        activeFractions.add(myFractions.get(i));
                    }
                }

                // need to sort and use toarryay
                int[] activeIndices = new int[activeFractionIndices.size()];
                for (int i = 0; i < activeFractionIndices.size(); i++) {
                    activeIndices[i] = activeFractionIndices.get(i);
                }

                Matrix logRatios = unknownsLogRatioMeans.getMatrix(activeIndices, 0, 0);
                Matrix Su = unknownsAnalyticalCovarianceSu.getMatrix(activeIndices, activeIndices);

//            System.out.println( "Su" );
//            Su.print( matF, 15 );
//
//            System.out.println( "logRatios" );
//            logRatios.print( matF, 15 );
                Matrix onesV = new Matrix(activeIndices.length, 1, 1.0);

                // SECTION A
                double logRatioMean = onesV.transpose().times(Su.solve(logRatios)).get(0, 0) //
                        / (onesV.transpose().times(Su.solve(onesV))).get(0, 0);

                logWMresults.setLogRatioMean(logRatioMean);

                double logRatioMeanOneSigmaAnalytical = Math.sqrt(1.0 / (onesV.transpose().times(Su.solve(onesV)).get(0, 0)));

                logWMresults.setLogRatioMeanOneSigmaAnalytical(logRatioMeanOneSigmaAnalytical);

                Matrix logRationResiduals = logRatios.copy();
                for (int i = 0; i < logRatios.getRowDimension(); i++) {
                    logRationResiduals.set(i, 0, logRatios.get(i, 0) - logRatioMean);
                }

                double logRatioMSWD = 1.0 / (activeIndices.length - 1) * logRationResiduals.transpose().times(Su.solve(logRationResiduals)).get(0, 0);

                logWMresults.setMSWD(logRatioMSWD);
                // Section B
                //TODO: provide lab data with values of ratio variability for each ratio
                // for now all = 2%
                // feb 2016 finally!
                double interReferenceMaterialReproducibility
                        = //
                        ReduxLabData.getInstance().getDefaultInterReferenceMaterialReproducibilityMap()//
                                .get(RadRatios.valueOf(radiogenicIsotopeDateName.replace("age", "r"))).getValue().doubleValue();

                Matrix SuInterStd = Su.plus(new Matrix(Su.getRowDimension(), Su.getColumnDimension(),//
                        interReferenceMaterialReproducibility * interReferenceMaterialReproducibility));

                double logRatioMeanOneSigmaAnalyticalPlusInterStd = Math.sqrt(1.0 / (onesV.transpose().times(SuInterStd.solve(onesV)).get(0, 0)));

                logWMresults.setLogRatioMeanOneSigmaAnalyticalPlusInterStd(logRatioMeanOneSigmaAnalyticalPlusInterStd);

                // section C
//            double logMeanPlusOneSigma = Math.log( standardRatio.getValue().doubleValue() + standardRatio.getOneSigmaAbs().doubleValue() );
//            double logMean = Math.log( standardRatio.getValue().doubleValue() );
//            double logMeanMinusOneSigma = Math.log( standardRatio.getValue().doubleValue() - standardRatio.getOneSigmaAbs().doubleValue() );
//
//            double maxDelta = Math.max( Math.abs( logMeanPlusOneSigma - logMean ), Math.abs( logMean - logMeanMinusOneSigma ) );
//
//            
                Matrix SuInterStdPlusStd = SuInterStd.plus(new Matrix(Su.getRowDimension(), Su.getColumnDimension(), varianceOfStandardLogRatio));

                double logRatioMeanOneSigmaAnalyticalPlusInterStdPlusStd = Math.sqrt(1.0 / (onesV.transpose().times(SuInterStdPlusStd.solve(onesV)).get(0, 0)));

                logWMresults.setLogRatioMeanOneSigmaAnalyticalPlusInterStdPlusStd(logRatioMeanOneSigmaAnalyticalPlusInterStdPlusStd);

//////            // section D
//////            //TODO: Encode this in our matrix class structure
//////            Matrix JDateLambda = new Matrix( activeFractions.size(), Lambdas.values().length );
//////            Matrix dDate_dLogratioVector = new Matrix( activeFractions.size(), 1 );
//////
//////            // first pass ... won't really work for 206/207
//////            for (int i = 0; i < activeFractions.size(); i ++) {
//////                Fraction unknown = activeFractions.get( i );
//////                for (int j = 0; j < partialDerivativeNames.size(); j ++) {
//////                    String pdName = partialDerivativeNames.get( j );
//////                    double derivative = unknown.getParDerivTerms().get( pdName ).doubleValue();
//////
//////                    int lambdaStart = pdName.toLowerCase().indexOf( "lambda23" );
//////                    String lambdaName = pdName.substring( lambdaStart, lambdaStart + 9 );
//////                    Lambdas lambda = Lambdas.valueOf( lambdaName.toLowerCase() );
//////                    JDateLambda.set( i, lambda.getIndex(), derivative );
//////
//////                    ValueModel lambdaVM = PhysicalConstantsModel.getEARTHTIMEPhysicalConstantsModel().getDatumByName( lambda.getName() );
//////
//////                    dDate_dLogratioVector.set( i, 0, 1.0 / (lambdaVM.getValue().doubleValue() * (1.0 + Math.exp(  - 1.0 * logRatios.get( i, 0 ) ))) );
//////                }
//////            }
//////
//////
//////// hack alert
//////            Matrix SuInterStdPlusStdPlusLambda = SuInterStdPlusStd.plus( new Matrix( Su.getRowDimension(), Su.getColumnDimension(), 0.000535 * 0.000535 ) );
//////
//////            double logRatioMeanOneSigmaAnalyticalPlusInterStdPlusStdPlusLambda = Math.sqrt( 1.0 / (onesV.transpose().times( SuInterStdPlusStdPlusLambda.solve( onesV ) ).get( 0, 0 )) );
//////
//////            logWMresults.setLogRatioMeanOneSigmaAnalyticalPlusInterStdPlusStdPlusLambda( logRatioMeanOneSigmaAnalyticalPlusInterStdPlusStdPlusLambda );
//////
//            System.out.println("JDateLambda");
//            JDateLambda.print(matF, 15);
//
//
//            System.out.println("dDate_dLogratioVector");
//            dDate_dLogratioVector.print(matF, 15);
//
//            Matrix d2Date_dLogratio2_matrix = dDate_dLogratioVector.times(dDate_dLogratioVector.transpose());
//            System.out.println("d2Date_dLogratio2_matrix");
//            d2Date_dLogratio2_matrix.print(matF, 15);
//
//            Matrix SuDateInterStdPlusStd = d2Date_dLogratio2_matrix.arrayTimes(SuInterStdPlusStd);
//            System.out.println("SuInterStdPlusStd");
//            SuInterStdPlusStd.print(matF, 15);
//            System.out.println("SuDateInterStdPlusStd");
//            SuDateInterStdPlusStd.print(matF, 15);
//
//            AbstractRatiosDataModel myPhysicalConstants = PhysicalConstantsModel.getEARTHTIMEPhysicalConstantsModel();
//            myPhysicalConstants.refreshModel();
//
//            Matrix LambdasCovMatrix = myPhysicalConstants.getDataCovariances().getMatrix();
//            System.out.println("LambdasCovMatrix");
//            LambdasCovMatrix.print(matF, 15);
//
//
//            Matrix SuDateLambda = JDateLambda.times(LambdasCovMatrix).times(JDateLambda.transpose());
//            System.out.println("SuDateLambda");
//            SuDateLambda.print(matF, 15);
//
//            Matrix SuDateInterStdPlusStdLambda = SuDateInterStdPlusStd.plus(SuDateLambda);
//            System.out.println("SuDateInterStdPlusStdLambda");
//            SuDateInterStdPlusStdLambda.print(matF, 15);
//
//
//            double logRatioMeanOneSigmaAnalyticalPlusInterStdPlusStdPlusLambda =//
//                    Math.sqrt(1.0 / (onesV.transpose().times(SuDateInterStdPlusStdLambda.solve(onesV)).get(0, 0)));
//
//            System.out.println("logRatioMeanOneSigmaAnalyticalPlusInterStdPlusStdPlusLambda");
//            System.out.println(matF.format(logRatioMeanOneSigmaAnalyticalPlusInterStdPlusStdPlusLambda));
//
//            logWMresults.setLogRatioMeanOneSigmaAnalyticalPlusInterStdPlusStdPlusLambda(logRatioMeanOneSigmaAnalyticalPlusInterStdPlusStdPlusLambda);
//
//                System.out.println("Logratio based WM have ARRIVED !!");
            } catch (Exception e) {
            }
        }
        return logWMresults;
    }

    private void calculateWeightedMeansWithMSWDforRatioBasedData(
            Vector<ETFractionInterface> myFractions,
            String radiogenicIsotopeDateName) //
            throws ETException {

        // document to reference "ReduxWeightdMeans" titled Evaluating Weighted Means with Systematic Uncertainties in ET_Redux"
        // by Noah McLean May 10 2011
        // remove zero ages from consideration
        // remove from activefractionIDs any fraction with 0 date
        // also detect if any fraction has been produced from legacy published data
        // by seeing if tracermass is zero
        // if so, then only analytical case will proceed
        boolean analyticalOnly = false;
        Vector<ETFractionInterface> zeroFractionDates = new Vector<>();
        for (int i = 0; i < myFractions.size(); i++) {
            if (!fractionDateIsPositive(myFractions.get(i))) {
                zeroFractionDates.add(myFractions.get(i));
            } else if (!myFractions.get(i).getAnalysisMeasure(AnalysisMeasures.tracerMassInGrams.getName()).hasPositiveValue()) {
                analyticalOnly = true;
            }
        }

        // clean out the zero-date fractions
        for (int i = 0; i < zeroFractionDates.size(); i++) {
            myFractions.remove(zeroFractionDates.get(i));
        }

        int countOfFractions = myFractions.size();

        // build vector of date values for specified date with one for each fraction
        Matrix vectorXBar = new Matrix(countOfFractions, 1);
        for (int i = 0; i < countOfFractions; i++) {
            vectorXBar.set(//
                    i, //
                    0, //
                    myFractions.get(i).getRadiogenicIsotopeDateByName(radiogenicIsotopeDateName).//
                            getValue().doubleValue());
        }

        Matrix sAnalyticalXbar = new Matrix(countOfFractions, countOfFractions);
        Matrix sTracerXbar = new Matrix(countOfFractions, countOfFractions);
        Matrix sLambdaXbar = new Matrix(countOfFractions, countOfFractions);

        if (analyticalOnly) { // generally the case for legacy data
            // populate the diagonal with the square of oneSigma abs = variance
            for (int i = 0; i < countOfFractions; i++) {
                double analyticalVar = fractionVarianceForThisDate(myFractions.get(i));
                sAnalyticalXbar.set(i, i, analyticalVar);
            }
        } else {
            /*
             * To take a weighed mean of several isotopic dates that includes
             * systematic uncertainties, a covariance matrix must first be
             * calculated which relates the uncertainties of all the fractions
             * to one another. Along the diagonal of the "total" covariance
             * matrix, the variances express the combined sum of the random and
             * systematic uncertainty contributions. Off the diagonal, the
             * covariance terms express the relationship between the
             * uncertainties of pairs of fractions due to their shared
             * systematic uncertainty contributions. The systematic
             * contributions to the covariance matrix can be calculated as the
             * quadratic form SIGMAts = Jt X SIGMAs X Jt(transpose) . The
             * measurement uncertainties may then be added to SIGMAts to
             * calculate the "total" covariance matrix for the dates, SIGMA,
             * which is a term in the existing generalized weighed mean
             * algorithm employed by U-Pb Redux.
             */

            // Step 1: Assemble the Jacobian (Sensitivity) Matrix, Jt for a specific date
            /*
             * The Jacobian Jt matrix for this calculation has a row for each
             * fraction corresponding to the specific date. The Jacobian matrix
             * has a column for each uncertainty that is being treated as a
             * systematic uncertainty here, the tracer and decay constant
             * uncertainties. These values can be found as rows in the tracer
             * mini date sensitivity matrix and the lambda mini date sensitivity
             * matrix that are already calculated for each fraction.
             */
            // the special case of analytical only will have no tracers or lambdas (See line 886 above)
            // first, need to count and accumulate the systematic variables, which essentially means determining
            // how many tracer variables there are based on the tracers present in the selected fractions
            ArrayList<String> wmSensitivityColNames = new ArrayList<String>();

            // TODO: move cov matrices for tracers and lambdas to their models in general = optimization
            // Note: Spring 2012 this is in motion and as of June, Tracers are so modeled in reduction
            // collect the unique tracer covariance matrices involved across all fractions in analysis
            Map<String, AbstractMatrixModel> savedTracerCovMats = new TreeMap<>();

            // there is only one lambdas cov matrix, so get it from first fraction = #0
            AbstractMatrixModel lambdasCovMat = ((UPbFraction) myFractions.get(0)).getReductionHandler().getLambdasMiniCovarianceMatrix();

            // collect names of fractions for row labels
            String[] fractionIDs = new String[myFractions.size()];

            // these collect the diagonals for the date under consideration for weighted mean
            double[] fractionAnalyticalDateCovariances = new double[countOfFractions];

            // as of June 2012, there is only one pair of tracers that can coexist in an analysis
            // other pairs may emerge in the future (conversation with Noah McLean)
            // flags to detect very special case of both earthtime tracers present
            boolean tracerET535Present = false;
            boolean tracerET2535Present = false;

            //August 2012 **********************************************************************************************
            // pre-process fractions to make sure tracers conform to rules for weighted means
            // for a given tracerName, all instances must have the same major version number
            Map<String, Integer> tracerSet = new TreeMap<>();
            for (int i = 0; i < countOfFractions; i++) {
                String tracerName = ((UPbFractionI) myFractions.get(i)).getTracer().getModelName();
                int tracerMajorVersionNumber = ((UPbFractionI) myFractions.get(i)).getTracer().getVersionNumber();

                if (tracerSet.containsKey(tracerName)) {
                    int storedVersion = tracerSet.get(tracerName);
                    if (storedVersion != tracerMajorVersionNumber) {
                        throw new ETException(//
                                null,//
                                new String[]{"You are attempting to calculate a weighted mean",
                                    "using more than one version of the same named Tracer: ",
                                    tracerName + " version " + storedVersion + "  and version " + tracerMajorVersionNumber + ".",
                                    "Please correct and proceed."});
                    }
                }

                tracerSet.put(tracerName, tracerMajorVersionNumber);
            }

            tracerET535Present = tracerSet.containsKey("ET535");
            tracerET2535Present = tracerSet.containsKey("ET2535");

            // special case for paired tracers 
            // we need to make sure all ET535 and ET2535 have same major version number
            // we know that each subset does by the  above test, so we just need to check one pair
            if (tracerET535Present && tracerET2535Present) {
                int storedET535Version = tracerSet.get("ET535");
                int storedET2535Version = tracerSet.get("ET2535");

                if (storedET535Version != storedET2535Version) {
                    // Alert user and abort
                    throw new ETException(//
                            null,//
                            new String[]{"You are attempting to calculate a weighted mean",
                                "using different versions of the Tracers ET535 and ET2535:",
                                "ET535 version " + storedET535Version + "  and ET2535 version " + storedET2535Version + ".",
                                "Please correct and proceed."});
                }

            }

            // now that Tracers pass muster, walk the fraction list and process
            // save out names of these special tracers to handle overlaps
            SortedSet<String> savedET535Names = new TreeSet<>();
            SortedSet<String> savedET2535Names = new TreeSet<>();
            String masterTracerName = "";

            for (int i = 0; i < countOfFractions; i++) {
                fractionIDs[i] = myFractions.get(i).getFractionID();

                // fraction - specific data handler
                ReductionHandler reductionHandlerI = ((UPbFraction) myFractions.get(i)).getReductionHandler();

                // retrieve and save analytical uncertainty component for three cases
                AbstractMatrixModel dateCovMat = reductionHandlerI.getAnalyticalMiniDateCovMatModel();
                int rowCol = dateCovMat.getCols().get(radiogenicIsotopeDateName);
                fractionAnalyticalDateCovariances[i] = dateCovMat.getMatrix().get(rowCol, rowCol);

                // since major version numbers are identical, use minor version to identify
                String tracerName
                        = //
                        ((UPbFractionI) myFractions.get(i)).getTracer().getModelName() //
                        + "." + ((UPbFraction) myFractions.get(i)).getTracer().getMinorVersionNumber();

                if (!savedTracerCovMats.containsKey(tracerName)) {

                    // new tracer, so add sensitivity matrix columns with name modifier for each tracer
                    Map<Integer, String> cols
                            =//
                            AbstractMatrixModel.invertColMap(reductionHandlerI.getTracerDateSensitivityVectors().getCols());

                    for (int c = 0; c < cols.size(); c++) {
                        wmSensitivityColNames.add(tracerName + "." + cols.get(c));
                    }

                    savedTracerCovMats.put(tracerName, //
                            reductionHandlerI.getTracerMiniCovarianceMatrix().copy());

                    String tracerType
                            = //
                            ((UPbFraction) myFractions.get(i)).getTracerType();
                    if (tracerType.equalsIgnoreCase(TracerUPbTypesEnum.mixed_205_233_235.getName())) {
                        savedET535Names.add(tracerName);
                    }

                    if (tracerType.equalsIgnoreCase(TracerUPbTypesEnum.mixed_202_205_233_235.getName())) {
                        savedET2535Names.add(tracerName);
                    }

                }
            }

            // Aug 2012 more robust treatment
            // in preparation for next step which is handling other tracer pairs in
            // a more general way
            if (tracerET535Present || tracerET2535Present) {
                // choose a master tracer, which will be 2535 if present that will contain the five common "ratios"

                if (!savedET2535Names.isEmpty()) {
                    masterTracerName = savedET2535Names.first();
                    savedET2535Names.remove(masterTracerName);
                } else {
                    masterTracerName = savedET535Names.first();
                    savedET535Names.remove(masterTracerName);
                }

                // now remove the common ratios column names from the sensitivity matrix
                Iterator<String> tracerNameIterator = savedET2535Names.iterator();
                while (tracerNameIterator.hasNext()) {
                    String tracerNameNext = tracerNameIterator.next() + ".";

                    wmSensitivityColNames.remove(tracerNameNext + TracerUPbRatiosAndConcentrations.r202_205t.getName());
                    wmSensitivityColNames.remove(tracerNameNext + TracerUPbRatiosAndConcentrations.r233_235t.getName());
                    wmSensitivityColNames.remove(tracerNameNext + TracerUPbRatiosAndConcentrations.r238_235t.getName());
                    wmSensitivityColNames.remove(tracerNameNext + TracerUPbRatiosAndConcentrations.concPb205t.getName());
                    wmSensitivityColNames.remove(tracerNameNext + TracerUPbRatiosAndConcentrations.concU235t.getName());
                }

                tracerNameIterator = savedET535Names.iterator();
                while (tracerNameIterator.hasNext()) {
                    String tracerNameNext = tracerNameIterator.next() + ".";

                    wmSensitivityColNames.remove(tracerNameNext + TracerUPbRatiosAndConcentrations.r233_235t.getName());
                    wmSensitivityColNames.remove(tracerNameNext + TracerUPbRatiosAndConcentrations.r238_235t.getName());
                    wmSensitivityColNames.remove(tracerNameNext + TracerUPbRatiosAndConcentrations.concPb205t.getName());
                    wmSensitivityColNames.remove(tracerNameNext + TracerUPbRatiosAndConcentrations.concU235t.getName());
                }

            }

            //JULY 2012 rework
            // create tracer only Jacobian by first creating square matrix and then replacing rows
            AbstractMatrixModel jacobianTracerOnlySensitivityMatrix = new JacobianMatrixModel("-3");
            String[] colNames = wmSensitivityColNames.toArray(new String[wmSensitivityColNames.size()]);
            jacobianTracerOnlySensitivityMatrix.setRows(colNames);
            jacobianTracerOnlySensitivityMatrix.setCols(jacobianTracerOnlySensitivityMatrix.getRows());

            // TODO: This is clunky
            // now replace rows with fraction names
            jacobianTracerOnlySensitivityMatrix.setRows(fractionIDs);

            // init with zeroes
            jacobianTracerOnlySensitivityMatrix.initializeMatrix();

            // add in Lambdas column names
            for (Lambdas l : Lambdas.values()) {
                wmSensitivityColNames.add(l.getName());
            }

            // create tracer and lambda Jacobian by first creating square matrix and then replacing rows
            AbstractMatrixModel jacobianTracerAndLambdaSensitivityMatrix = new JacobianMatrixModel("-3");
            colNames = wmSensitivityColNames.toArray(new String[wmSensitivityColNames.size()]);
            jacobianTracerAndLambdaSensitivityMatrix.setRows(colNames);
            jacobianTracerAndLambdaSensitivityMatrix.setCols(jacobianTracerAndLambdaSensitivityMatrix.getRows());

            // TODO: This is clunky
            // now replace rows with fraction names
            jacobianTracerAndLambdaSensitivityMatrix.setRows(fractionIDs);

            // init with zeroes
            jacobianTracerAndLambdaSensitivityMatrix.initializeMatrix();

            // now populate both jacobians with appropriate values from each fraction's tracer and lambda sensitivity matrices
            for (int i = 0; i < countOfFractions; i++) {

                // since major version numbers are identical, use minor version to identify
                String tracerName
                        = //
                        ((UPbFractionI) myFractions.get(i)).getTracer().getModelName() //
                        + "." + ((UPbFraction) myFractions.get(i)).getTracer().getMinorVersionNumber();

                copySensitivities(//
                        radiogenicIsotopeDateName,
                        ((UPbFraction) myFractions.get(i)).getReductionHandler().getTracerDateSensitivityVectors(),
                        jacobianTracerOnlySensitivityMatrix,//
                        i,
                        tracerName + ".", masterTracerName);

                copySensitivities(//
                        radiogenicIsotopeDateName,
                        ((UPbFraction) myFractions.get(i)).getReductionHandler().getTracerDateSensitivityVectors(),
                        jacobianTracerAndLambdaSensitivityMatrix,//
                        i,
                        tracerName + ".", masterTracerName);

                copySensitivities(//
                        radiogenicIsotopeDateName,
                        ((UPbFraction) myFractions.get(i)).getReductionHandler().getLambdaDateSensitivityVectors(),
                        jacobianTracerAndLambdaSensitivityMatrix,//
                        i,
                        "", masterTracerName);
            }

            // june 2012 test
            File matrixFile = new File("JACOBIAN_TRACER_AND_LAMBDA_SENSITIVITY_MATRIX_FILE_" + dateName + ".txt");
            PrintWriter matrixWriter;

            try {
                matrixWriter = new PrintWriter(new FileWriter(matrixFile));
                matrixWriter.println("\n\n******   JACOBIAN_SENSITIVITY " + dateName + "   ********************\n\n");

                matrixWriter.println(jacobianTracerAndLambdaSensitivityMatrix.ToStringWithLabels());

                matrixWriter.close();

            } catch (IOException iOException) {
            }
            /**
             * 2
             *
             * Create the Covariance Matrix for Systematic Variables, SIGMAs
             *
             * The covariance matrix for the systematic variables is created in
             * exactly the same way as it is now in Redux. The only tweak going
             * forward is that the covariance matrix for each tracer will be
             * provided in full. The rows and columns of the covariance matrix
             * must be in exactly the same order as the columns of Jt . Thus, if
             * there are multiple tracers, these must appear in the same order
             * in the covariance matrix as in the Jacobian matrix above. The
             * covariance between variables that belong to different tracers is
             * zero.
             */
            AbstractMatrixModel systematicTracerOnlyCovMatModel = new CovarianceMatrixModel();
            systematicTracerOnlyCovMatModel.setLevelName("-1");
            systematicTracerOnlyCovMatModel.setRows(AbstractMatrixModel.invertColMap(jacobianTracerOnlySensitivityMatrix.getCols()));
            systematicTracerOnlyCovMatModel.setCols(systematicTracerOnlyCovMatModel.getRows());
            systematicTracerOnlyCovMatModel.initializeMatrix();

            AbstractMatrixModel systematicCovMatModel = new CovarianceMatrixModel();
            systematicCovMatModel.setLevelName("-1");
            systematicCovMatModel.setRows(AbstractMatrixModel.invertColMap(jacobianTracerAndLambdaSensitivityMatrix.getCols()));
            systematicCovMatModel.setCols(systematicCovMatModel.getRows());
            systematicCovMatModel.initializeMatrix();

            // all used tracers
            // Aug 2012 more robust treatment
            if (tracerET535Present || tracerET2535Present) {

                // Aug 2012 generic handling of shrinking the overlapping tracer models
                // for each of the reamaining saved 2535 and 535 tracers (one was assigned master status)
                // collect the covariance between the 5 constant ratios and the 4 varying ratios
                // for later insertion into the finished matrix
                Map<Integer, String> rowCommonNames = new TreeMap<>();
                rowCommonNames.put(0, "concPb205t");
                rowCommonNames.put(1, "concU235t");
                rowCommonNames.put(2, "r202_205t");
                rowCommonNames.put(3, "r233_235t");
                rowCommonNames.put(4, "r238_235t");

                Map<Integer, String> rowChangingNames = new TreeMap<>();
                rowChangingNames.put(0, "r204_205t");
                rowChangingNames.put(1, "r206_205t");
                rowChangingNames.put(2, "r207_205t");
                rowChangingNames.put(3, "r208_205t");

                AbstractMatrixModel savedValuesMatrix = new CovarianceMatrixModel();
                savedValuesMatrix.setRows(rowCommonNames);
                savedValuesMatrix.setCols(rowChangingNames);

                // process the 2535 tracers
                Iterator<String> tracerNameIterator = savedET2535Names.iterator();
                while (tracerNameIterator.hasNext()) {
                    String tracer2535Name = tracerNameIterator.next();
                    AbstractMatrixModel tracerMatrix = savedTracerCovMats.get(tracer2535Name);

                    savedValuesMatrix.initializeMatrix();

                    Iterator<String> colNamesIterator = savedValuesMatrix.getCols().keySet().iterator();
                    while (colNamesIterator.hasNext()) {
                        String colName = colNamesIterator.next();
                        int colNumber = savedValuesMatrix.getCols().get(colName);
                        Iterator<Integer> rowNamesIterator = savedValuesMatrix.getRows().keySet().iterator();
                        while (rowNamesIterator.hasNext()) {
                            int rowNumber = rowNamesIterator.next();
                            String rowName = savedValuesMatrix.getRows().get(rowNumber);

                            savedValuesMatrix.getMatrix().set(//
                                    rowNumber,//
                                    colNumber,//
                                    ((CovarianceMatrixModel) tracerMatrix).getCovarianceCell(rowName, colName));
                        }
                    }

                    // now shrink tracerMatrix matrix by removing common first 2 are conc third is 202_205, last 2 are ratios
                    Map<Integer, String> oldRows = tracerMatrix.getRows();
                    Map<Integer, String> tempRows = new HashMap<>();
                    for (int i = 3; i < 7; i++) {
                        tempRows.put(i - 3, oldRows.get(i));
                    }

                    tracerMatrix.setRows(tempRows);
                    tracerMatrix.setCols(tempRows);

                    Matrix tempMat = tracerMatrix.getMatrix();
                    Matrix tempMatSelect = tempMat.getMatrix(3, 6, 3, 6);

                    tracerMatrix.setMatrix(tempMatSelect);

                    copyCovariances(tracer2535Name + ".", tracerMatrix, systematicTracerOnlyCovMatModel);
                    copyCovariances(tracer2535Name + ".", tracerMatrix, systematicCovMatModel);

                    // prevent tracer from being handled in the general case below
                    savedTracerCovMats.remove(tracer2535Name);

                    colNamesIterator = savedValuesMatrix.getCols().keySet().iterator();
                    while (colNamesIterator.hasNext()) {
                        String colName = colNamesIterator.next();
                        int colNumber = savedValuesMatrix.getCols().get(colName);
                        Iterator<Integer> rowNamesIterator = savedValuesMatrix.getRows().keySet().iterator();
                        while (rowNamesIterator.hasNext()) {
                            int rowNumber = rowNamesIterator.next();
                            String rowName = savedValuesMatrix.getRows().get(rowNumber);

                            ((CovarianceMatrixModel) systematicTracerOnlyCovMatModel).setCovarianceCells(//
                                    tracer2535Name + "." + colName, //
                                    masterTracerName + "." + rowName, //
                                    savedValuesMatrix.getMatrix().get(rowNumber, colNumber));

                            ((CovarianceMatrixModel) systematicCovMatModel).setCovarianceCells(//
                                    tracer2535Name + "." + colName, //
                                    masterTracerName + "." + rowName, //
                                    savedValuesMatrix.getMatrix().get(rowNumber, colNumber));
                        }
                    }
                }

                // process the 535 tracers
                tracerNameIterator = savedET535Names.iterator();
                while (tracerNameIterator.hasNext()) {
                    String tracer535Name = tracerNameIterator.next();
                    AbstractMatrixModel tracerMatrix = savedTracerCovMats.get(tracer535Name);

                    savedValuesMatrix.initializeMatrix();

                    Iterator<String> colNamesIterator = savedValuesMatrix.getCols().keySet().iterator();
                    while (colNamesIterator.hasNext()) {
                        String colName = colNamesIterator.next();
                        int colNumber = savedValuesMatrix.getCols().get(colName);
                        Iterator<Integer> rowNamesIterator = savedValuesMatrix.getRows().keySet().iterator();
                        while (rowNamesIterator.hasNext()) {
                            int rowNumber = rowNamesIterator.next();
                            String rowName = savedValuesMatrix.getRows().get(rowNumber);

                            savedValuesMatrix.getMatrix().set(//
                                    rowNumber,//
                                    colNumber,//
                                    ((CovarianceMatrixModel) tracerMatrix).getCovarianceCell(rowName, colName));
                        }
                    }

                    // now shrink tracerMatrix matrix by removing common first 2 are conc , last 2 are ratios
                    Map<Integer, String> oldRows = tracerMatrix.getRows();
                    Map<Integer, String> tempRows = new HashMap<Integer, String>();
                    for (int i = 2; i < 6; i++) {
                        tempRows.put(i - 2, oldRows.get(i));
                    }

                    tracerMatrix.setRows(tempRows);
                    tracerMatrix.setCols(tempRows);

                    Matrix tempMat = tracerMatrix.getMatrix();
                    Matrix tempMatSelect = tempMat.getMatrix(2, 5, 2, 5);

                    tracerMatrix.setMatrix(tempMatSelect);

                    copyCovariances(tracer535Name + ".", tracerMatrix, systematicTracerOnlyCovMatModel);
                    copyCovariances(tracer535Name + ".", tracerMatrix, systematicCovMatModel);

                    // prevent tracer from being handled in the general case below
                    savedTracerCovMats.remove(tracer535Name);

                    colNamesIterator = savedValuesMatrix.getCols().keySet().iterator();
                    while (colNamesIterator.hasNext()) {
                        String colName = colNamesIterator.next();
                        int colNumber = savedValuesMatrix.getCols().get(colName);
                        Iterator<Integer> rowNamesIterator = savedValuesMatrix.getRows().keySet().iterator();
                        while (rowNamesIterator.hasNext()) {
                            int rowNumber = rowNamesIterator.next();
                            String rowName = savedValuesMatrix.getRows().get(rowNumber);

                            ((CovarianceMatrixModel) systematicTracerOnlyCovMatModel).setCovarianceCells(//
                                    tracer535Name + "." + colName, //
                                    masterTracerName + "." + rowName, //
                                    savedValuesMatrix.getMatrix().get(rowNumber, colNumber));

                            ((CovarianceMatrixModel) systematicCovMatModel).setCovarianceCells(//
                                    tracer535Name + "." + colName, //
                                    masterTracerName + "." + rowName, //
                                    savedValuesMatrix.getMatrix().get(rowNumber, colNumber));
                        }
                    }

                }

            }

            // populate systematicCovMatModel with Tracer data
            for (Map.Entry<String, AbstractMatrixModel> entry : savedTracerCovMats.entrySet()) {
                copyCovariances(entry.getKey() + ".", entry.getValue(), systematicTracerOnlyCovMatModel);
                copyCovariances(entry.getKey() + ".", entry.getValue(), systematicCovMatModel);
            }

            // extract matrix for tracer only
            Matrix tracerSystematicCovMat = systematicTracerOnlyCovMatModel.getMatrix().copy();

            // now populate systematicCovMatModel with lambdas
            copyCovariances("", lambdasCovMat, systematicCovMatModel);

            // confirm matrix manipulations
            matrixFile = new File("systematicCovMatModel_FILE_" + dateName + ".txt");

            try {
                matrixWriter = new PrintWriter(new FileWriter(matrixFile));
                matrixWriter.println("\n\n******   systematicCovMatModel " + dateName + "   ********************\n\n");

                matrixWriter.println(systematicCovMatModel.ToStringWithLabels());

                matrixWriter.close();

            } catch (IOException iOException) {
            }

            /**
             * 3
             *
             * Multiply to Find SIGMAts
             *
             * The systematic covariance matrix SIGMAts has a row and column for
             * each fraction in the weighed mean, and expresses the covariance
             * between the fractions from systematic variables. It is calculated
             * by the matrix multiplication SIGMAts = Jt X SIGMAs X
             * Jt(transpose)
             *
             * This is the same as equation (67) in my G3 paper, though the
             * matrix transposes are switched for the Jacobian derivatives
             * because of the way I?ve derived them here.
             */
            // sAnalyticalXbar is defined as square zeroes matrix
            sAnalyticalXbar = new Matrix(countOfFractions, countOfFractions, 0.0);

            sTracerXbar
                    = //
                    jacobianTracerOnlySensitivityMatrix.getMatrix().//
                            times(tracerSystematicCovMat.//
                                    times(jacobianTracerOnlySensitivityMatrix.getMatrix().transpose()));

            sLambdaXbar
                    = //
                    jacobianTracerAndLambdaSensitivityMatrix.getMatrix().//
                            times(systematicCovMatModel.getMatrix().//
                                    times(jacobianTracerAndLambdaSensitivityMatrix.getMatrix().transpose()));

            /**
             * 4
             *
             * Replace the Diagonal of SIGMAts to determine SIGMA
             *
             * To determine the ?total? covariance matrix SIGMA, which combines
             * all sources of uncertainty, the measurement (analytical)
             * uncertainties need to be added to the systematic covariance
             * matrix SIGMAts . These affect only the individual fractions, so
             * they contribute to the diagonal terms in the covariance matrix.
             * All the off-diagonal terms in SIGMA are the same as in SIGMAts .
             * The diagonal terms in ?SIGMA can found in the ?date covariance
             * matrix? for each fraction: for the 206 Pb/ 238 U date, this is
             * the variance term in the first row and first column, for the
             * Th-corrected 206 Pb/ 238 U date it is the variance term in the
             * second row and second column, etc. The value of each fraction?s
             * date variance can be replaced in SIGMAts with the new value to
             * effectively replace the diagonal of SIGMAts to create SIGMA. It
             * is SIGMA that is used to calculate the generalized weighed mean
             * of the fractions in U-Pb Redux.
             */
            //sigma-a = sanalyticalxbar = square of zeros with diagonal
            for (int f = 0; f < countOfFractions; f++) {
                // analytical
                sAnalyticalXbar.set(f, f, fractionAnalyticalDateCovariances[f]);

                // analytical plus tracer
                sTracerXbar.set(f, f, sTracerXbar.get(f, f) + fractionAnalyticalDateCovariances[f]);
                // all sources
                sLambdaXbar.set(f, f, sLambdaXbar.get(f, f) + fractionAnalyticalDateCovariances[f]);
            }

            // setup utility model for printing
            AbstractMatrixModel utilityCovMatModel = new CovarianceMatrixModel();
            utilityCovMatModel.setRows(fractionIDs);
            utilityCovMatModel.setCols(utilityCovMatModel.getRows());

            matrixFile = new File("WEIGHTED_MEAN_MATRIX_FILE_" + dateName + ".txt");

            try {
                matrixWriter = new PrintWriter(new FileWriter(matrixFile));
                matrixWriter.println("\n\n******   WEIGHTED MEAN " + dateName + "   ********************\n\n");

                utilityCovMatModel.setLevelName("SigmaA " + radiogenicIsotopeDateName + " ");
                utilityCovMatModel.setMatrix(sAnalyticalXbar);
                matrixWriter.println(utilityCovMatModel.ToStringWithLabels());

                utilityCovMatModel.setLevelName("SigmaT " + radiogenicIsotopeDateName + " ");
                utilityCovMatModel.setMatrix(sTracerXbar);
                matrixWriter.println(utilityCovMatModel.ToStringWithLabels());

                utilityCovMatModel.setLevelName("SigmaL " + radiogenicIsotopeDateName + " ");
                utilityCovMatModel.setMatrix(sLambdaXbar);
                matrixWriter.println(utilityCovMatModel.ToStringWithLabels());

                matrixWriter.close();

            } catch (IOException iOException) {
            }
        }// end of case where more than analytical uncertainties are being considered

        // calculate alpha matrices for each of XYZ uncertainties
        Matrix sLambdaXbarInverse;
        Matrix U = new Matrix(countOfFractions, 1, 1.0);
        Matrix sAnalyticalXbarInverseU = null;
        try {
            sAnalyticalXbarInverseU = sAnalyticalXbar.solve(U);
        } catch (Exception e) {
            throw new ETException(//
                    null,//
                    new String[]{"Date Uncertainties are ZERO ... Cannot calculate weighted mean.",});
        }

        Matrix alphaAnalytical
                =//
                sAnalyticalXbarInverseU.//
                        times(1.0 / U.transpose().times(sAnalyticalXbarInverseU).get(0, 0));
        Matrix alphaTracer = null;
        Matrix alphaLambda = null;
        if (!analyticalOnly) {
            Matrix sTracerXbarInverseU = sTracerXbar.solve(U);
            alphaTracer
                    =//
                    sTracerXbarInverseU.//
                            times(1.0 / U.transpose().times(sTracerXbarInverseU).get(0, 0));

            sLambdaXbarInverse = sLambdaXbar.inverse();
            alphaLambda
                    =//
                    sLambdaXbarInverse.times(U).//
                            times(1.0 / U.transpose().times(sLambdaXbarInverse).times(U).get(0, 0));
        }
        // dot products
        double analyticalWM = 0.0;
        for (int f = 0;
                f < countOfFractions;
                f++) {
            analyticalWM += vectorXBar.get(f, 0) * alphaAnalytical.get(f, 0);
        }
        double analyticalOneSigma
                = //
                Math.sqrt(//
                        alphaAnalytical.transpose().//
                                times(sAnalyticalXbar).//
                                times(alphaAnalytical).get(0, 0));
        Matrix analyticalR = vectorXBar.minus(new Matrix(countOfFractions, 1, analyticalWM));
        Matrix sAnalyticalXbarInvR = sAnalyticalXbar.solve(analyticalR);
        double analyticalMSWD
                = //
                analyticalR.transpose().//
                        times(sAnalyticalXbarInvR).get(0, 0)//
                / (double) (countOfFractions - 1);
        if (!analyticalOnly) {

            double tracerOneSigma
                    = //
                    Math.sqrt(//
                            alphaTracer.transpose().//
                                    times(sTracerXbar).//
                                    times(alphaTracer).get(0, 0));

            double lambdaOneSigma
                    = //
                    Math.sqrt(//
                            alphaLambda.transpose().//
                                    times(sLambdaXbar).//
                                    times(alphaLambda).get(0, 0));

            setInternalTwoSigmaUnctWithTracerCalibrationUnct(new BigDecimal(2.0 * tracerOneSigma));
            setInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct(new BigDecimal(2.0 * lambdaOneSigma));

        }

        setValue(new BigDecimal(analyticalWM));
        setOneSigma(
                new BigDecimal(analyticalOneSigma));
        setInternalTwoSigmaUnct(
                new BigDecimal(2.0 * analyticalOneSigma));

        setMeanSquaredWeightedDeviation(
                new BigDecimal(analyticalMSWD));

    }

    private void copySensitivities(//
            String radiogenicIsotopeDateName, //
            AbstractMatrixModel fractionDateSensitivityVectors, AbstractMatrixModel jacobianSensitivityMatrix, int rowIndex, String columnPrefix, String masterTracerName) {

        String ET535Minor = "ET535.0.";

        // determine row number for current date
        Map<String, Integer> rows
                = //
                AbstractMatrixModel.invertRowMap(fractionDateSensitivityVectors.getRows());

        int calculatedDateRow = rows.get(radiogenicIsotopeDateName);

        // walk the vector columns and get the value at each cell of horizontal vector
        // this walk is safe for our special case of ET535 and ET2535
        Map<Integer, String> cols
                =//
                AbstractMatrixModel.invertColMap(fractionDateSensitivityVectors.getCols());

        for (int c = 0; c < cols.size(); c++) {

            String calculatedColName = (String) cols.get(c);
            double storedValue = fractionDateSensitivityVectors.getMatrix().get(calculatedDateRow, c);
            int colJacobian = 0;

            //TODO: make more robust for general case
            try {
                // determine column in jacobianTracerAndLambdaSensitivityMatrix by using tracer name prefix
                colJacobian = jacobianSensitivityMatrix.getCols().get(columnPrefix + calculatedColName);
            } catch (Exception e) {
                // handle special case of missing columns in ET535 and ET2535
                // the only case where this happens is for ET2535 for the uranium and concentrations
                try {
                    colJacobian = jacobianSensitivityMatrix.getCols().get(masterTracerName + "." + calculatedColName);
                } catch (Exception e2) {
                    storedValue = 0.0;
                }
            }

            // put value
            jacobianSensitivityMatrix.setValueAt(rowIndex, colJacobian, storedValue);

        }
    }

    private void copyCovariances(//
            String prefix,
            AbstractMatrixModel miniCovMat,//
            AbstractMatrixModel systematicCovMatModel) {
        // assume identical shape of miniCovMat inside systematicCovMat to speed copy
        // first locate row=col of miniCovMat 0,0 inside systematicCovMat (miniCovMats arranged on diagonal)

        int rowCol = systematicCovMatModel.getCols().get(prefix + miniCovMat.getRows().get(0));
        int width = miniCovMat.getMatrix().getColumnDimension() - 1;

        try {
            systematicCovMatModel.getMatrix().setMatrix(rowCol, rowCol + width, rowCol, rowCol + width, miniCovMat.getMatrix());
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
            System.out.println(e.getMessage());//java.lang.ArrayIndexOutOfBoundsException
        }
    }

    /**
     * zeroes all values, sets this <code>SampleDateModel</code>'s fields to
     * those of the first <code>Fraction</code> found in argument
     * <code>myFractions</code>, and computes its weighted mean.
     *
     * @throws org.earthtime.exceptions.ETException
     * @pre argument <code>myFractions</code> is a valid collection of
     * <code>Fractions</code>
     * @post this <code>SampleDateModel</code>'s fields are zeroed out and set
     * to those of the first <code>Fraction</code> found in argument
     * <code>myFractions</code> and the weighted mean is computed for this
     * <code>SampleDateModel</code>
     *
     * @param myFractions the collection of <code>Fractions</code> that will be
     * used to set this      <code>SampleDateModel</code>'s fields
     */
    public void WM206_238(Vector<ETFractionInterface> myFractions) //
            throws ETException {
        ZeroAllValues();

        ArrayList<String> partialDerivativeNames = new ArrayList<String>();
        partialDerivativeNames.add("dAge206_238r__dLambda238");

        LogWMresults logWMresults = calculateWeightedMeansWithMSWD(
                myFractions, //
                RadDates.age206_238r.getName(), //
                partialDerivativeNames);

        if (logWMresults != null) {
            //we have a post feb 2013 logratio solution
            ValueModel meanDate = UPbFractionReducer//
                    .calculateDate206_238r( //
                            new Age206_238r(),
                            Math.exp(logWMresults.getLogRatioMean()),
                            Math.exp(logWMresults.getLogRatioMean() + logWMresults.getLogRatioMeanOneSigmaAnalytical() * 2.0));

            setValue(meanDate.getValue());
            setOneSigma(meanDate.getValue().doubleValue() * logWMresults.getLogRatioMeanOneSigmaAnalytical());
            setInternalTwoSigmaUnct(getOneSigmaAbs().multiply(new BigDecimal(2.0)));

            // Section B
            meanDate = UPbFractionReducer//
                    .calculateDate206_238r( //
                            new Age206_238r(),
                            Math.exp(logWMresults.getLogRatioMean()),
                            Math.exp(logWMresults.getLogRatioMean() + logWMresults.getLogRatioMeanOneSigmaAnalyticalPlusInterStd() * 2.0));

            setInternalTwoSigmaUnctWithStandardRatioVarUnct(new BigDecimal(meanDate.getValue().doubleValue() * logWMresults.getLogRatioMeanOneSigmaAnalyticalPlusInterStd() * 2.0));

            // Section C
            meanDate = UPbFractionReducer//
                    .calculateDate206_238r( //
                            new Age206_238r(),
                            Math.exp(logWMresults.getLogRatioMean()),
                            Math.exp(logWMresults.getLogRatioMean() + logWMresults.getLogRatioMeanOneSigmaAnalyticalPlusInterStdPlusStd() * 2.0));

            setInternalTwoSigmaUnctWithTracerCalibrationUnct(new BigDecimal(meanDate.getValue().doubleValue() * logWMresults.getLogRatioMeanOneSigmaAnalyticalPlusInterStdPlusStd() * 2.0));

            // Section D           
            setInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct(//
                    new BigDecimal(logWMresults.getLogRatioMeanOneSigmaAnalyticalPlusInterStdPlusStdPlusLambda() * 2.0));

            // hack 
            setInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct(//
                    new BigDecimal(meanDate.getValue().doubleValue() * logWMresults.getLogRatioMeanOneSigmaAnalyticalPlusInterStdPlusStdPlusLambda() * 2.0));

            setMeanSquaredWeightedDeviation(
                    new BigDecimal(logWMresults.getMSWD()));

        }
    }

    /**
     * zeroes all values, sets this <code>SampleDateModel</code>'s fields to
     * those of the first <code>Fraction</code> found in argument
     * <code>myFractions</code>, and computes its weighted mean.
     *
     * @throws org.earthtime.exceptions.ETException
     * @pre argument <code>myFractions</code> is a valid collection of
     * <code>Fractions</code>
     * @post this <code>SampleDateModel</code>'s fields are zeroed out and set
     * to those of the first <code>Fraction</code> found in argument
     * <code>myFractions</code> and the weighted mean is computed for this
     * <code>SampleDateModel</code>
     *
     * @param myFractions the collection of <code>Fractions</code> that will be
     * used to set this      <code>SampleDateModel</code>'s fields
     */
    public void WM207_235(Vector<ETFractionInterface> myFractions) //
            throws ETException {
        ZeroAllValues();

        LogWMresults logWMresults = calculateWeightedMeansWithMSWD(
                myFractions, RadDates.age207_235r.getName(), null);

        if (logWMresults != null) {
            //we have a post feb 2013 logratio solution
            ValueModel meanDate = UPbFractionReducer//
                    .calculateDate207_235r( //
                            new Age207_235r(),
                            Math.exp(logWMresults.getLogRatioMean()),
                            Math.exp(logWMresults.getLogRatioMean() + logWMresults.getLogRatioMeanOneSigmaAnalytical() * 2.0));

            setValue(meanDate.getValue());
            setOneSigma(meanDate.getOneSigmaAbs());

        }
    }

    /**
     * zeroes all values, sets this <code>SampleDateModel</code>'s fields to
     * those of the first <code>Fraction</code> found in argument
     * <code>myFractions</code>, and computes its weighted mean.
     *
     * @throws org.earthtime.exceptions.ETException
     * @pre argument <code>myFractions</code> is a valid collection of
     * <code>Fractions</code>
     * @post this <code>SampleDateModel</code>'s fields are zeroed out and set
     * to those of the first <code>Fraction</code> found in argument
     * <code>myFractions</code> and the weighted mean is computed for this
     * <code>SampleDateModel</code>
     *
     * @param myFractions the collection of <code>Fractions</code> that will be
     * used to set this      <code>SampleDateModel</code>'s fields
     */
    public void WM207_206(Vector<ETFractionInterface> myFractions) //
            throws ETException {
        ZeroAllValues();
        LogWMresults logWMresults = calculateWeightedMeansWithMSWD(
                myFractions, RadDates.age207_206r.getName(), null);

        if (logWMresults != null) {
            //we have a post feb 2013 logratio solution
            ValueModel meanDate = UPbFractionReducer//
                    .calculateDate207_206r( //
                            ReduxLabData.getInstance().getDefaultR238_235s(),
                            new Age207_206r(),
                            myFractions.get(0).getRadiogenicIsotopeDateByName(RadDates.age206_238r),
                            Math.exp(logWMresults.getLogRatioMean()),
                            Math.exp(logWMresults.getLogRatioMean() + logWMresults.getLogRatioMeanOneSigmaAnalytical() * 2.0));

            setValue(meanDate.getValue());
            setOneSigma(meanDate.getOneSigmaAbs());

        }
    }

    /**
     *
     * @param myFractions
     * @throws org.earthtime.exceptions.ETException
     */
    public void WM208_232(Vector<ETFractionInterface> myFractions) //
            throws ETException {
        ZeroAllValues();

        ArrayList<String> partialDerivativeNames = new ArrayList<String>();
        partialDerivativeNames.add("dAge208_232r__dLambda232");

        LogWMresults logWMresults = calculateWeightedMeansWithMSWD(
                myFractions, ///
                RadDates.age208_232r.getName(), //
                partialDerivativeNames);

        if (logWMresults != null) {
            //we have a post feb 2013 logratio solution
            ValueModel meanDate = UPbFractionReducer//
                    .calculateDate208_232r( //
                            new Age208_232r(),
                            Math.exp(logWMresults.getLogRatioMean()),
                            Math.exp(logWMresults.getLogRatioMean() + logWMresults.getLogRatioMeanOneSigmaAnalytical() * 2.0));

            setValue(meanDate.getValue());
            setOneSigma(meanDate.getValue().doubleValue() * logWMresults.getLogRatioMeanOneSigmaAnalytical());
            setInternalTwoSigmaUnct(getOneSigmaAbs().multiply(new BigDecimal(2.0)));

            // Section B
            meanDate = UPbFractionReducer//
                    .calculateDate208_232r( //
                            new Age208_232r(),
                            Math.exp(logWMresults.getLogRatioMean()),
                            Math.exp(logWMresults.getLogRatioMean() + logWMresults.getLogRatioMeanOneSigmaAnalyticalPlusInterStd() * 2.0));

            setInternalTwoSigmaUnctWithStandardRatioVarUnct(new BigDecimal(meanDate.getValue().doubleValue() * logWMresults.getLogRatioMeanOneSigmaAnalyticalPlusInterStd() * 2.0));

            // Section C
            meanDate = UPbFractionReducer//
                    .calculateDate208_232r( //
                            new Age208_232r(),
                            Math.exp(logWMresults.getLogRatioMean()),
                            Math.exp(logWMresults.getLogRatioMean() + logWMresults.getLogRatioMeanOneSigmaAnalyticalPlusInterStdPlusStd() * 2.0));

            setInternalTwoSigmaUnctWithTracerCalibrationUnct(new BigDecimal(meanDate.getValue().doubleValue() * logWMresults.getLogRatioMeanOneSigmaAnalyticalPlusInterStdPlusStd() * 2.0));

            // Section D           
            setInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct(//
                    new BigDecimal(logWMresults.getLogRatioMeanOneSigmaAnalyticalPlusInterStdPlusStdPlusLambda() * 2.0));

            setMeanSquaredWeightedDeviation(
                    new BigDecimal(logWMresults.getMSWD()));
        }
    }

    /**
     *
     * @param myFractions
     * @throws org.earthtime.exceptions.ETException
     */
    public void WM206_238r_Th(Vector<ETFractionInterface> myFractions) //
            throws ETException {
        ZeroAllValues();
        LogWMresults logWMresults = calculateWeightedMeansWithMSWD(
                myFractions, RadDates.age206_238r_Th.getName(), null);
        if (logWMresults != null) {
        }
    }

    /**
     *
     * @param myFractions
     * @throws org.earthtime.exceptions.ETException
     */
    public void WM207_235r_Pa(Vector<ETFractionInterface> myFractions) //
            throws ETException {
        ZeroAllValues();
        LogWMresults logWMresults = calculateWeightedMeansWithMSWD(
                myFractions, RadDates.age207_235r_Pa.getName(), null);
        if (logWMresults != null) {
        }
    }

    /**
     *
     * @param myFractions
     * @throws org.earthtime.exceptions.ETException
     */
    public void WM207_206r_Th(Vector<ETFractionInterface> myFractions) //
            throws ETException {
        ZeroAllValues();
        LogWMresults logWMresults = calculateWeightedMeansWithMSWD(
                myFractions, RadDates.age207_206r_Th.getName(), null);
        if (logWMresults != null) {
        }
    }

    /**
     *
     * @param myFractions
     * @throws org.earthtime.exceptions.ETException
     */
    public void WM207_206r_Pa(Vector<ETFractionInterface> myFractions) //
            throws ETException {
        ZeroAllValues();
        LogWMresults logWMresults = calculateWeightedMeansWithMSWD(
                myFractions, RadDates.age207_206r_Pa.getName(), null);
        if (logWMresults != null) {
        }
    }

    /**
     *
     * @param myFractions
     * @throws org.earthtime.exceptions.ETException
     */
    public void WM207_206r_ThPa(Vector<ETFractionInterface> myFractions) //
            throws ETException {
        ZeroAllValues();
        LogWMresults logWMresults = calculateWeightedMeansWithMSWD(
                myFractions, RadDates.age207_206r_ThPa.getName(), null);
        if (logWMresults != null) {
        }
    }

    /**
     * 22 Nov 2008
     *
     * @param myFractions
     * @param lowerInterceptModel
     */
    public void UpperIntercept(
            Vector<ETFractionInterface> myFractions,
            ValueModel lowerInterceptModel) {
        ZeroAllValues();

        // set up lower intercept at the same time
        try {
            ((SampleDateModel) lowerInterceptModel).ZeroAllValues();

        } catch (Exception e) {
        }
        if (myFractions.isEmpty()) {
            setValue(BigDecimal.ZERO);

        } else {
            // initialize arrays
            int pointCount = myFractions.size();
            double[] X = new double[pointCount];
            double[] Y = new double[pointCount];
            double[] sigmaX = new double[pointCount];
            double[] sigmaY = new double[pointCount];
            double[] rho = new double[pointCount];

            for (int i = 0; i < myFractions.size(); i++) {
                ETFractionInterface nextFraction = myFractions.get(i);

                X[i] = nextFraction.getRadiogenicIsotopeRatioByName("r207_235r").//
                        getValue().doubleValue();
                Y[i] = nextFraction.getRadiogenicIsotopeRatioByName("r206_238r").//
                        getValue().doubleValue();

                sigmaX[i] = nextFraction.getRadiogenicIsotopeRatioByName("r207_235r").//
                        getOneSigmaAbs().doubleValue();
                sigmaY[i] = nextFraction.getRadiogenicIsotopeRatioByName("r206_238r").//
                        getOneSigmaAbs().doubleValue();

                rho[i] = nextFraction.getRadiogenicIsotopeRatioByName("rhoR206_238r__r207_235r").//
                        getValue().doubleValue();
            }

            setYorkLineFit(new YorkLineFit(X, Y, sigmaX, sigmaY, rho));

            AbstractRatiosDataModel myPhysicalConstants = myFractions.get(0).getPhysicalConstantsModel();

            // calculate upper intercept date
            try {
                setValue(new BigDecimal(DiscordiaInterceptNewtonMethod(/*
                         * 4.5E9
                         */ReduxConstants.MAX_DATE_ANNUM, myPhysicalConstants)));
                setMeanSquaredWeightedDeviation(
                        new BigDecimal(getYorkLineFit().getMSWD()));
            } catch (Exception e) {
            }

            // + uncertainty in upper intercept
            try {
                ((SampleDateInterceptModel) this).setPlusInternalTwoSigmaUnct(//
                        new BigDecimal(//
                                DiscordiaUncertaintyInterceptNewtonMethod(//
                                        getValue().doubleValue(), -1, myPhysicalConstants) - getValue().doubleValue()));

            } catch (Exception e) {
            }

            // - uncertainty in upper intercept
            try {
                ((SampleDateInterceptModel) this).setMinusInternalTwoSigmaUnct(//
                        new BigDecimal(//
                                DiscordiaUncertaintyInterceptNewtonMethod(//
                                        getValue().doubleValue(), +1, myPhysicalConstants) - getValue().doubleValue()));

            } catch (Exception e) {
            }

            // lower intercept
            try {
                ((SampleDateModel) lowerInterceptModel).setIncludedFractionIDsVector(getIncludedFractionIDsVector());
            } catch (Exception e) {
            }
            try {
                ((SampleDateModel) lowerInterceptModel).setYorkLineFit(getYorkLineFit());
            } catch (Exception e) {
            }
            // calculate lower intercept date
            try {
                lowerInterceptModel.setValue(//
                        new BigDecimal(DiscordiaInterceptNewtonMethod(0.0, myPhysicalConstants)));

                ((SampleDateModel) lowerInterceptModel).setMeanSquaredWeightedDeviation(//
                        new BigDecimal(getYorkLineFit().getMSWD()));

            } catch (Exception e) {
            }

            // + uncertainty in lower intercept
            try {
                ((SampleDateInterceptModel) lowerInterceptModel).setPlusInternalTwoSigmaUnct(//
                        new BigDecimal(//
                                DiscordiaUncertaintyInterceptNewtonMethod(//
                                        lowerInterceptModel.getValue().doubleValue(), +1, myPhysicalConstants) - lowerInterceptModel.getValue().doubleValue()));
            } catch (Exception e) {
            }

            // - uncertainty in lower intercept
            try {
                ((SampleDateInterceptModel) lowerInterceptModel).setMinusInternalTwoSigmaUnct(//
                        new BigDecimal(//
                                DiscordiaUncertaintyInterceptNewtonMethod(//
                                        lowerInterceptModel.getValue().doubleValue(), -1, myPhysicalConstants) - lowerInterceptModel.getValue().doubleValue()));
            } catch (Exception e) {
            }
        }
    }

    /**
     * currently does nothing
     *
     * @param myFractions
     */
    public void LowerIntercept(Vector<FractionI> myFractions) {
        // June 2010 ... no action needed here since upper intercept does calcs, this is used in reflection
    }

    private double DiscordiaInterceptNewtonMethod(double xn, AbstractRatiosDataModel physicalConstants) {

        ValueModel lambda235 = physicalConstants.getDatumByName(Lambdas.lambda235.getName()).copy();
        ValueModel lambda238 = physicalConstants.getDatumByName(Lambdas.lambda238.getName()).copy();

        double yIntercept = getYorkLineFit().getYIntercept();
        double slope = getYorkLineFit().getSlope();

        for (int i = 0; i < 100; i++) {

            double expLambda235xnMinus1 = Math.expm1(lambda235.getValue().doubleValue() * xn);
            double expLambda238xnMinus1 = Math.expm1(lambda238.getValue().doubleValue() * xn);

            double new10 = yIntercept + slope * expLambda235xnMinus1 - expLambda238xnMinus1;
            double new11 = slope * lambda235.getValue().doubleValue() * (expLambda235xnMinus1 + 1.0)//
                    - lambda238.getValue().doubleValue() * (expLambda238xnMinus1 + 1.0);

            xn = xn - new10 / new11;

        }

        return xn;
    }

    private double DiscordiaUncertaintyInterceptNewtonMethod(
            double xn,
            int sign,
            AbstractRatiosDataModel physicalConstants) {

        ValueModel lambda235 = physicalConstants.getDatumByName(Lambdas.lambda235.getName()).copy();
        ValueModel lambda238 = physicalConstants.getDatumByName(Lambdas.lambda238.getName()).copy();

        double yIntercept = getYorkLineFit().getYIntercept();
        double slope = getYorkLineFit().getSlope();
        double sigmaYIntercept = getYorkLineFit().getOneSigmaYIntercept();
        double sigmaSlope = getYorkLineFit().getOneSigmaSlope();
        double sigmaYInterceptSlope = getYorkLineFit().getCovYIntercept__slope();

        for (int i = 0; i < 100; i++) {

            double expLambda235xnMinus1 = Math.expm1(lambda235.getValue().doubleValue() * xn);
            double expLambda238xnMinus1 = Math.expm1(lambda238.getValue().doubleValue() * xn);
            double new10 = yIntercept + slope * expLambda235xnMinus1 - expLambda238xnMinus1//
                    + sign * 2.0 * Math.sqrt(//
                            (sigmaYIntercept * sigmaYIntercept) //
                            + (2.0 * sigmaYInterceptSlope * expLambda235xnMinus1)//
                            + sigmaSlope * sigmaSlope * expLambda235xnMinus1 * expLambda235xnMinus1);

            double new11
                    = //
                    slope * lambda235.getValue().doubleValue() //
                    * (expLambda235xnMinus1 + 1.0)//
                    - lambda238.getValue().doubleValue() //
                    * (expLambda238xnMinus1 + 1.0)//
                    + sign * 2.0 * lambda235.getValue().doubleValue() * (expLambda235xnMinus1 + 1.0)//
                    * (sigmaYInterceptSlope + sigmaSlope * sigmaSlope * expLambda235xnMinus1)//
                    / Math.sqrt(//
                            (sigmaYIntercept * sigmaYIntercept) //
                            + (2.0 * sigmaYInterceptSlope * expLambda235xnMinus1)//
                            + sigmaSlope * sigmaSlope * expLambda235xnMinus1 * expLambda235xnMinus1);

            xn = xn - new10 / new11;
        }

        return xn;
    }

    /**
     * currently does nothing
     *
     * @param myFractions
     */
    public void ISO238_206(Vector<FractionI> myFractions) {
        System.out.println("Successful call to ISO238_206");
    }

    /**
     * currently does nothing
     *
     * @param myFractions
     */
    public void ISO235_207(Vector<FractionI> myFractions) {
        System.out.println("Successful call to ISO235_207");
    }

    /**
     * currently does nothing
     *
     * @param myFractions
     */
    public void ISO232_208(Vector<FractionI> myFractions) {
        System.out.println("Successful call to ISO232_208");
    }

    /**
     * currently does nothing
     *
     * @param myFractions
     */
    public void ISO_SemiTotalPb(Vector<FractionI> myFractions) {
        System.out.println("Successful call to ISO_SemiTotalPb");
    }

    /**
     * currently does nothing
     *
     * @param myFractions
     */
    public void ISO_TotalPb(Vector<FractionI> myFractions) {
        System.out.println("Successful call to ISO_TotalPb");
    }

    public void ISO238_230(Vector<ETFractionInterface> myFractions) {
        ZeroAllValues();

        // Feb 2017 in support of Useries isochrons
        // TODO: make more robust
        unitsForYears = "ka";

        if (myFractions.isEmpty()) {
            setValue(BigDecimal.ZERO);

        } else {
            McLeanRegressionLineFit mcLeanRegressionLineFit
                    = new McLeanRegressionLineFit(myFractions,
                            UThAnalysisMeasures.ar238U_232Thfc.getName(),
                            UThAnalysisMeasures.ar230Th_232Thfc.getName());

            mcLeanRegressionLine = mcLeanRegressionLineFit.getMcLeanRegressionLine();

            AbstractRatiosDataModel physicalConstants = myFractions.get(0).getPhysicalConstantsModel();
            ValueModel lambda230 = physicalConstants.getDatumByName(Lambdas.lambda230.getName()).copy();

            // May 2017 new math from Noah
            // McleanRegression a = vertical vector of [0][0] = 0.0, [1][0] = x-y y-intercept, [2][0] = x-z z-intercept 
            // McleanRegression v = vertical vector of [0][0] = 1.0, [1][0] = x-y slope,       [2][0] = x-z slope 
            // SAV is covariance sized 2*(d-1) where d is dimension (say x-y = 2, x-y-z = 3)
            // SAV diagonal for d=2 >> (2 x 2) is [0][0] = variance of x-y y-intercept
            //                                    [1][1] = variance of x-y slope
            // SAV diagonal for d=3 >> (4 x 4) is [0][0] = variance of x-y y-intercept
            //                                    [1][1] = variance of x-z z-intercept
            //                                    [2][2] = variance of x-y slope
            //                                    [3][3] = variance of x-z slope
            // calculated slope from McleanRegression
            double slopeMean = mcLeanRegressionLine.getV()[1][0];
            // oneSigmaAbs for the slope
            double slopeStdv = Math.sqrt(mcLeanRegressionLine.getSav()[1][1]);

            // t is the age equation estimate of the age
            double t;
            if (slopeMean < 1.0) {
                t = (-1.0 / lambda230.getValue().doubleValue()) * StrictMath.log(1.0 - slopeMean);
            } else {
                t = 5.0E5;
            }

            double tOneSigmaAbs = Math.abs(1.0 / (lambda230.getValue().doubleValue() * (1 - slopeMean))) * slopeStdv;

            // the upper and lower limits of the probability distribution functions we’ll calculate for the age of the sample
            double mint = StrictMath.max(0.0, t - 4.0 * tOneSigmaAbs);
            double maxt = StrictMath.min(1.0e6, t + 4.0 * tOneSigmaAbs);

            double slopeVar = slopeStdv * slopeStdv;
            // number of vector elements for estimation: larger n >> longer calculation time
            int nt = 1000;

            // Create a vector named tvec that consists of nt = 1000 equally spaced values, 
            // starting at mint and ending at maxt.  So if mint = 1 and maxt = 1000, 
            // then tvec would be [1, 2, 3, 4… 998, 999, 1000].
            double[] tvec = new double[nt];
            double[] ptp = new double[nt];
            double[] Ptp = new double[nt];
            double[] ptnorm = new double[nt];

            double tint = (maxt - mint) / (nt - 1);
            int i = 0;
            double sumPtp = 0.0;

            for (double d = mint; d <= maxt; d += tint) {
                tvec[i] = d;

                ptp[i] = StrictMath.exp(-lambda230.getValue().doubleValue() * tvec[i])
                        * StrictMath.exp(-Math.pow((1.0 - Math.exp(-lambda230.getValue().doubleValue() * tvec[i]) - slopeMean), 2) / (2.0 * slopeVar));
                sumPtp += ptp[i];

                Ptp[i] = -erf((slopeMean + StrictMath.exp(-lambda230.getValue().doubleValue() * tvec[i]) - 1.0) / StrictMath.sqrt(2 * slopeVar))
                        + erf(slopeMean / StrictMath.sqrt(2 * slopeVar));

                i++;
            }

            List<Double> ptList = new ArrayList<>();
            List<Double> tzList = new ArrayList<>();
            for (i = 0; i < ptp.length; i++) {
                ptnorm[i] = StrictMath.abs(ptp[i] / (sumPtp * tint));

                if ((Ptp[i] > 1.0e-15) && (Ptp[i] < Ptp[nt - 1])) {
                    ptList.add(Ptp[i] / Ptp[nt - 1]);
                    tzList.add(tvec[i]);
                }
            }

            double[] ptArray = ptList.stream().mapToDouble(d -> d).toArray();
            double[] tzArray = tzList.stream().mapToDouble(d -> d).toArray();

            LinearInterpolator interp = new LinearInterpolator();
            PolynomialSplineFunction interFunc = interp.interpolate(ptArray, tzArray);

            // limsCV contains the lower and upper ages for the 95% confidence limit for the age.
            double[] limsCV = new double[]{interFunc.value(0.025), interFunc.value(0.975)};
            double medianCV = interFunc.value(0.5);

            double meanCV = 0.0;
            for (i = 0; i < nt; i++) {
                meanCV += ptnorm[i] * tvec[i];
            }

            double modeCV = -1.0 / lambda230.getValue().doubleValue()
                    * StrictMath.log((1.0 - slopeMean + StrictMath.sqrt((1.0 - slopeMean) * (1.0 - slopeMean) + 4.0 * slopeVar)) / 2.0);

            /*
             * Plus and minus uncertainties (2-sigma equivalent ). 
             */
            double minusUnct = modeCV - limsCV[0];
            double plusUnct = limsCV[1] - modeCV;

            /**
             * We should report meanCV, medianCV, and modeCV as our outputs: the
             * mean, median, and mode of the asymmetric probability distribution
             * function, along with limsCV, the 95% confidence limit on these.
             * You should use the modeCV in the data table as the age of the
             * sample, and minusUnct and plusUnct as its plus and minus
             * uncertainties. All of these calculations can be re-used for the
             * radium isochrons as well (section B part a: “Simple Assumption:
             * Ra = Ba” below), just use lambda226 instead of lambda230, and use
             * the slope from that isochron. *
             */
            
            
            
            // previous method  
            // getV()[1][0] = slope
            double myDate = -1.0 / lambda230.getValue().doubleValue()
                    * StrictMath.log(1.0 - mcLeanRegressionLine.getV()[1][0]);

            setValue(myDate);

            setOneSigma(100.0);
        }
    }

    /**
     * gets the <code>methodName</code> of this <code>SampleDateModel</code>.
     *
     * @pre this <code>SampleDateModel</code> exists
     * @post returns this <code>SampleDateModel</code>'s <code>methodName</code>
     *
     * @return <code>String</code> - <code>methodName</code> of this
     * <code>SampleDateModel</code>
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * sets the <code>methodName</code> of this <code>SampleDateModel</code> to
     * argument <code>methodName</code>.
     *
     * @pre argument <code>methodName</code> is a valid <code>String</code>
     * @post this <code>SampleDateModel</code>'s <code>methodName</code> is set
     * to argument <code>methodName</code>
     *
     * @param methodName value to set this <code>SampleDateModel</code>'s
     * <code>methodName</code> to
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     *
     * @param fraction
     * @return
     */
    public boolean fractionDateIsPositive(ETFractionInterface fraction) {
        boolean retVal = false;
        if (methodName.compareToIgnoreCase("ISO238_230") == 0) {
            retVal = fraction.getAnalysisMeasure(dateName).hasPositiveValue();
        } else {
            retVal = fraction.getRadiogenicIsotopeDateByName(dateName).hasPositiveValue();
        }

        return retVal;
    }

    private double fractionVarianceForThisDate(ETFractionInterface fraction) {
        return fraction.getRadiogenicIsotopeDateByName(dateName).getOneSigmaAbs().pow(2).doubleValue();
    }

    /**
     * returns a <code>String</code> consisting of the argument      <code>
     * fraction</code>'s name and date formatted with two sigma ABS unct.
     *
     * @pre argument <code>fraction</code> is a valid <code>Fraction</code>
     * @post returns a <code>String</code> containing the argument      <code>
     *          fraction</code>'s name and date
     *
     * @param fraction the <code>Fraction</code> to convert to a
     * <code>String</code>
     * @param dateUnit
     * @return <code>String</code> - the name and date ( formatted with two
     * sigma ABS unct ) of argument <code>fraction</code>
     */
    public String showFractionIdWithDateAndUnct(ETFractionInterface fraction) {

        String contents = "";
        if ((dateName.length() > 0) && (methodName.compareToIgnoreCase("ISO238_230") != 0)) {
            contents
                    = //
                    " : date = "//
                    + fraction.getRadiogenicIsotopeDateByName(dateName)//
                            .formatValueAndTwoSigmaForPublicationSigDigMode(//
                                    "ABS", ReduxConstants.getUnitConversionMoveCount(unitsForYears), 2)//
                    + " " + unitsForYears + " 2\u03C3";//               " Ma 2\u03C3";
        }

        return fraction.getFractionID()//
                + contents;
    }

    /**
     * creates an output <code>String</code> containing this
     * <code>SampleDateModel</code>'s value and unct formatted for publication
     * with a label.
     *
     * @pre this <code>SampleDateModel</code> exists
     * @post returns the value and unct fields of this
     * <code>SampleDateModel</code> formatted for publication
     *
     * @return <code>String</code> - value and unct of this
     * <code>SampleDateModel</code> formatted for publication
     */
    public String ShowCustomDateNode() {
        return //
                "date = " //
                + FormatValueAndTwoSigmaABSThreeWaysForPublication(6, 2) + " " + unitsForYears + " 2\u03C3";
    }

    /**
     * creates an output <code>String</code> containing this
     * <code>SampleDateModel</code>'s <code>meanSquaredWeightedDeviation</code>.
     *
     * @pre this <code>SampleDateModel</code> exists
     * @post returns a <code>String</code> containing the
     * <code>meanSquaredWeightedDeviation</code> of this
     * <code>SampleDateModel</code>
     *
     * @return <code>String</code> - <code>meanSquaredWeightedDeviation</code>
     * of this <code>SampleDateModel</code> formatted for output
     */
    public String ShowCustomMSWDwithN() {
        return //
                "MSWD = " //
                + getMeanSquaredWeightedDeviation().round(new MathContext(2, RoundingMode.HALF_UP)).toPlainString() + ", n = " //
                + getIncludedFractionIDsVector().size();
    }

    /**
     *
     * @return
     */
    public String ShowCustomMSWD() {
        return //
                "MSWD = " //
                + getMeanSquaredWeightedDeviation().round(new MathContext(2, RoundingMode.HALF_UP)).toPlainString();
    }

    /**
     *
     * @return
     */
    public String ShowCustomN() {
        return //
                "n = " + getIncludedFractionIDsVector().size();
    }

    /**
     * creates an output <code>String</code> containing the size of this
     * <code>SampleDateModel</code>'s <code>includedFractionIDsVector</code>
     *
     * @pre this <code>SampleDateModel</code> exists
     * @post returns a <code>String</code> containing the size of this
     * <code>SampleDateModel</code>'s <code>includedFractionIDsVector</code>
     *
     * @return <code>String</code> - the size of this
     * <code>SampleDateModel</code>'s <code>includedFractionIDsVector</code>
     * formatted for output
     */
    public String ShowCustomFractionCountNode() {
        return //
                "n = " //
                + getIncludedFractionIDsVector().size();
    }

    /**
     * creates an output <code>String</code> containing this
     * <code>SampleDateModel</code>'s value and various unct formatted for
     * publication.
     *
     * @pre this <code>SampleDateModel</code> exists
     * @post returns the value and unct fields of this
     * <code>SampleDateModel</code> formatted for publication
     *
     * @param divideByPowerOfTen
     * @param uncertaintySigDigits
     * @return <code>String</code> - value and unct of this
     * <code>SampleDateModel</code> formatted for publication
     */
    public String FormatValueAndTwoSigmaABSThreeWaysForPublication(
            int divideByPowerOfTen,
            int uncertaintySigDigits) {

        // innovative method for reporting uncertainty for sample date interpretations
        // will use AAA.AA +/- analytical/analytplustracer/analytplustracerpluslambda
        // so first decide which unct is most precise
        // first determine the shape of specified significant digits of ABS uncertainty
        String twoSigAnalyticalUnct = "0.00";
        if (getInternalTwoSigmaUnct().compareTo(BigDecimal.ZERO) != 0) {
            twoSigAnalyticalUnct = getInternalTwoSigmaUnct().movePointLeft(divideByPowerOfTen).//
                    round(new MathContext(uncertaintySigDigits, RoundingMode.HALF_UP)).toPlainString();
        }

        int countOfDigitsAfterDec_A = calculateCountOfDigitsAfterDecPoint(twoSigAnalyticalUnct);

        // added in March 2013 to handle LAICPMS uncert due to standard ratio variability
        String twoSigAnalyticalPlusStdRatioVariability = "0.00";

        if (getSampleAnalysisType().compareTo(SampleAnalysisTypesEnum.TRIPOLIZED) == 0) {

            try {
                if (getInternalTwoSigmaUnctWithStandardRatioVarUnct().compareTo(BigDecimal.ZERO) != 0) {
                    twoSigAnalyticalPlusStdRatioVariability = getInternalTwoSigmaUnctWithStandardRatioVarUnct().//
                            movePointLeft(divideByPowerOfTen).//
                            round(new MathContext(uncertaintySigDigits, RoundingMode.HALF_UP)).toPlainString();
                }
            } catch (Exception e) {
            }
        }

        String twoSigAnalyticalPlusTracerUnct = "0.00";

        try {
            if (getInternalTwoSigmaUnctWithTracerCalibrationUnct().compareTo(BigDecimal.ZERO) != 0) {
                twoSigAnalyticalPlusTracerUnct = getInternalTwoSigmaUnctWithTracerCalibrationUnct().//
                        movePointLeft(divideByPowerOfTen).//
                        round(new MathContext(uncertaintySigDigits, RoundingMode.HALF_UP)).toPlainString();
            }
        } catch (Exception e) {
        }

        int countOfDigitsAfterDec_B = calculateCountOfDigitsAfterDecPoint(twoSigAnalyticalPlusTracerUnct);

        String twoSigAnalyticalPlusTracerUnctPlusLambdaUnct = "0.00";

        try {
            if (getInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct().compareTo(BigDecimal.ZERO) != 0) {
                twoSigAnalyticalPlusTracerUnctPlusLambdaUnct
                        =//
                        getInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct().//
                                movePointLeft(divideByPowerOfTen).//
                                round(new MathContext(uncertaintySigDigits, RoundingMode.HALF_UP)).toPlainString();
            }
        } catch (Exception e) {
        }

        int countOfDigitsAfterDec_C = calculateCountOfDigitsAfterDecPoint(twoSigAnalyticalPlusTracerUnctPlusLambdaUnct);

        // get the most precise (note this is broken if all unct istoleft of decimal point)
        int countOfDigitsAfterDecPointInUnct
                = Math.max(Math.max(countOfDigitsAfterDec_A, countOfDigitsAfterDec_B), countOfDigitsAfterDec_C);

        return formatValueAndTwoSigmaForPublicationSigDigMode(//
                "ABS", ReduxConstants.getUnitConversionMoveCount(unitsForYears), 2) //+ " \u00B1 " //
                //+ twoSigAnalyticalUnct //
                + (String) ((getSampleAnalysisType().compareTo(SampleAnalysisTypesEnum.TRIPOLIZED) == 0) ? ("/" + twoSigAnalyticalPlusStdRatioVariability) : "")
                + "/" + twoSigAnalyticalPlusTracerUnct //
                + "/" + twoSigAnalyticalPlusTracerUnctPlusLambdaUnct;
    }

    /**
     * gets the <code>dateName</code> of this <code>SampleDateModel</code>.
     *
     * @pre this <code>SampleDateModel</code> exists
     * @post returns this <code>SampleDateModel</code>'s <code>dateName</code>
     *
     * @return <code>String</code> - this <code>SampleDateModel</code>'s
     * <code>dateName</code>
     */
    public String getDateName() {
        return dateName;
    }

    /**
     * sets this <code>SampleDateModel</code>'s <code>dateName</code> to
     * argument <code>dateName</code>.
     *
     * @pre argument <code>dateName</code> is a valid <code>String</code>
     * @post this <code>SampleDateModel</code>'s <code>dateName</code> is set to
     * argument <code>dateName</code>
     *
     * @param dateName value to set this <code>SampleDateModel</code>'s
     * <code>dateName</code> to
     */
    public void setDateName(String dateName) {
        this.dateName = dateName;
    }

    /**
     *
     * @return
     */
    public double DetermineMaxDatePlusTwoSigma() {
        double retVal = 0.0;
        // altered july 2008 to use all fractions, so that de-selected ones can be grayed out

        for (ETFractionInterface f : ((UPbReduxAliquot) aliquot).getAliquotFractions()) {
            if (!f.isRejected()
                    && f.getRadiogenicIsotopeDateByName(dateName).hasPositiveValue()) {
                double date = f.getRadiogenicIsotopeDateByName(getDateName()).getValue().doubleValue();
                double twoSigma = f.getRadiogenicIsotopeDateByName(getDateName()).getTwoSigmaAbs().doubleValue();

                if ((date + twoSigma) > retVal) {
                    retVal = (date + twoSigma);
                }
            }
        }

        return retVal;
    }

    /**
     *
     * @return
     */
    public double DetermineMinDateLessTwoSigma() {
        double retVal = ReduxConstants.MAX_DATE_ANNUM;// 4.5E9;
        // altered july 2008 to use all fractions, so that de-selected ones can be grayed out
        for (ETFractionInterface f : ((UPbReduxAliquot) aliquot).getAliquotFractions()) {
            if (!f.isRejected()
                    && f.getRadiogenicIsotopeDateByName(dateName).hasPositiveValue()) {
                double date = f.getRadiogenicIsotopeDateByName(getDateName()).getValue().doubleValue();
                double twoSigma = f.getRadiogenicIsotopeDateByName(getDateName()).getTwoSigmaAbs().doubleValue();

                if ((date - twoSigma) < retVal) {
                    retVal = (date - twoSigma);
                }
            }
        }

        return retVal;
    }

    /**
     *
     * @return
     */
    public AliquotInterface getAliquot() {
        return aliquot;
    }

    /**
     *
     * @param aliquot
     */
    public void setAliquot(AliquotInterface aliquot) {
        this.aliquot = aliquot;
    }

    /**
     *
     * @return
     */
    public YorkLineFit getYorkLineFit() {
        return yorkLineFit;
    }

    /**
     *
     * @param yorkLineFit
     */
    public void setYorkLineFit(YorkLineFit yorkLineFit) {
        this.yorkLineFit = yorkLineFit;
    }

    /**
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        ValueModel valueModel
                = new SampleDateModel("WM208_232", "WM208_232",//
                        "r206_204b", new BigDecimal("1234567890"), "ABS", new BigDecimal("123000"));
        System.out.println(
                "Format Test: " + valueModel.formatValueAndTwoSigmaForPublicationSigDigMode("ABS", 6, 2));

        String testFileName = "SampleDateModelTEST.xml";

        valueModel.serializeXMLObject(testFileName);
        valueModel.readXMLObject(testFileName, true);
    }

    /**
     * @return the sample
     */
    public SampleInterface getSample() {
        return sample;
    }

    /**
     * @param sample the sample to set
     */
    public void setSample(SampleInterface sample) {
        this.sample = sample;
    }

    /**
     * @return the displayedAsGraph
     */
    public boolean isDisplayedAsGraph() {
        return displayedAsGraph;
    }

    /**
     * @param displayedAsGraph the displayedAsGraph to set
     */
    public void setDisplayedAsGraph(boolean displayedAsGraph) {
        this.displayedAsGraph = displayedAsGraph;
    }

    /**
     * @return the sampleAnalysisType
     */
    public SampleAnalysisTypesEnum getSampleAnalysisType() {
        // to handle pre-march-2013 cases mainly differentiates from TRIPOLIZED = within Redux
        if (sampleAnalysisType == null) {
            sampleAnalysisType = SampleAnalysisTypesEnum.COMPILED;
        }
        return sampleAnalysisType;
    }

    /**
     * @param sampleAnalysisType the sampleAnalysisType to set
     */
    public void setSampleAnalysisType(SampleAnalysisTypesEnum sampleAnalysisType) {
        // to handle pre-march-2013 cases mainly differentiates from TRIPOLIZED = within Redux
        if (sampleAnalysisType == null) {
            this.sampleAnalysisType = SampleAnalysisTypesEnum.COMPILED;
        } else {
            this.sampleAnalysisType = sampleAnalysisType;
        }
    }

    /**
     * @return the mcLeanRegressionLine
     */
    public McLeanRegressionLineInterface getMcLeanRegressionLine() {
        return mcLeanRegressionLine;
    }

    /**
     * @param mcLeanRegressionLine the mcLeanRegressionLine to set
     */
    public void setMcLeanRegressionLine(McLeanRegressionLineInterface mcLeanRegressionLine) {
        this.mcLeanRegressionLine = mcLeanRegressionLine;
    }

    /**
     * @return the unitsForYears
     */
    public String getUnitsForYears() {
        return unitsForYears;
    }

    /**
     * @param myUnitsForYears
     */
    public static void setUnitsForYears(String myUnitsForYears) {
        unitsForYears = myUnitsForYears;
    }

    /**
     * @return the isochronModels
     */
    public SortedSet<IsochronModel> getIsochronModels() {
        if (isochronModels == null) {
            this.isochronModels = new TreeSet<>();
        }
        return isochronModels;
    }

    /**
     * @param isochronModels the isochronModels to set
     */
    public void setIsochronModels(SortedSet<IsochronModel> isochronModels) {
        this.isochronModels = isochronModels;
    }

    /**
     * @return the automaticIsochronSelection
     */
    public boolean isAutomaticIsochronSelection() {
        return automaticIsochronSelection;
    }

    /**
     * @param automaticIsochronSelection the automaticIsochronSelection to set
     */
    public void setAutomaticIsochronSelection(boolean automaticIsochronSelection) {
        this.automaticIsochronSelection = automaticIsochronSelection;
    }

    /**
     * 
     * @return 
     */
    public boolean isAutomaticInitDelta234USelection() {
        return automaticInitDelta234USelection;
    }

    /**
     * 
     * @param automaticInitDelta234USelection 
     */
    public void setAutomaticInitDelta234USelection(boolean automaticInitDelta234USelection) {
        this.automaticInitDelta234USelection = automaticInitDelta234USelection;
    }

    /**
     * @return the ar48icntrs
     */
    public double[] getAr48icntrs() {
        if (ar48icntrs == null){
            ar48icntrs = new double[0];
        }
        return ar48icntrs;
    }

    /**
     * @param ar48icntrs the ar48icntrs to set
     */
    public void setAr48icntrs(double[] ar48icntrs) {
        this.ar48icntrs = ar48icntrs;
    }
    
    
}
