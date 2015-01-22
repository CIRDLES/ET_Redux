/*
 * ValueModelI.java
 *
 * Created on August 6, 2007, 7:09 AM
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

import java.math.BigDecimal;

/**
 * Any Class that implements <code>ValueModelI</code> should contain scientifically
 * measured quantities and their errors. It should also provide additional
 * methods for manipulating and publishing these values.
 * 
 * @author James F. Bowring, javaDocs by Stan Gasque
 */
public interface ValueModelI {
    /**
     * should set the <code>value</code>, <code>uncertainty type</code>, and <code>one sigma</code>
     * of this <code>ValueModel</code> to those of the argument <code>valueModel</code>
     *
     * @pre     <code>valueModel</code> exists
     * @post    this <code>ValueModel</code>'s <code>value</code>, <code>uncertainty
     *          type</code>, and <code>one sigma</code> should be set to the argument
     *          <code>ValueModel</code>'s respective values
     * @param   valueModel  <code>ValueModel</code> whose fields will be copied
     */
    abstract void copyValuesFrom(ValueModel valueModel);

    /**
     * should standardize output of <code>ValueModels</code> with organized
     * <code>String</code> output for testing
     *
     * @pre     this <code>ValueModel</code> exists
     * @post    formatted <code>String</code> containing the relevant fields of
     *          this <code>ValueModel</code> should be returned
     * @return  <code>String</code> - any relevant fields of this <code>ValueModel</code>
     */
    public abstract String formatValueAndOneSigmaABSForTesting();

//    /**
//     * should standardize output of <code>ValueModels</code> with organized <code>
//     * String</code> output for publication. <code>value</code> should be modified to
//     * contain the approriate number of significant digits based on <code>twoSigma</code>
//     *
//     * @param   divideByPowerOfTen      power of ten to divide sigma by
//     * @param   uncertaintySigDigits    number of significant digits in two sigma error
//     * @pre     this <code>ValueModel</code> exists
//     * @post    formatted <code>String</code> containing the <code>value</code>
//     *          and <code>two sigma</code>, each with the appropriate number of
//     *          significant digits, of this <code>ValueModel</code> should be returned
//     * @return  <code>String</code> - <code>value</code> and <code>two sigma</code>
//     *          formatted for significant digits
//     */
//    abstract String FormatValueAndTwoSigmaABSForPublication(int divideByPowerOfTen, int uncertaintySigDigits);

    /**
     * should compare this <code>ValueModel</code> with the argument <code>valueModel
     * </code> by <code>name</code>. Should return an <code>integer</code> based on comparison
     *
     * @pre     <code>valueModel</code> exists
     * @post    an <code>integer</code> should be returned that relates whether the given
     *          <code>ValueModel</code> is lexicographically less than, equal to,
     *          or greater than this <code>ValueModel</code>
     * @param   valueModel  the <code>ValueModel</code> to be compared to this one
     * @return  int - 0 if the argument <code>ValueModel</code> is equal to this
     *          <code>ValueModel</code>, less than 0 if it is less than this <code>
     *          ValueModel</code>, and greater than 0 if it is greater than this
     *          <code>ValueModel</code>
     * @throws  java.lang.ClassCastException    a ClassCastException
     */
    abstract int compareTo(ValueModel valueModel) throws ClassCastException;

    /**
     * should return a new <code>ValueModel</code> whose fields are equal to those of
     * this <code>ValueModel</code>
     *
     * @pre     this <code>ValueModel</code> exists
     * @post    a new <code>ValueModel</code> with identical data to this <code>
     *          ValueModel</code> should be returned
     * @return  <code>ValueModel</code> - a new <code>ValueModel</code> whose fields
     *          match those of this <code>ValueModel</code>
     */
    abstract ValueModel copy();

    /**
     * should check for lexicographic equivalence between this <code>ValueModel</code>
     * and the argument <code>valueModel</code>. Should return <code>false</code> if
     * the <code>object</code> given is not a <code>ValueModel</code>
     *
     * @pre     <code>valueModel</code> exists
     * @post    a <code>boolean</code> should be returned - <code>true</code> if argument
     *          is equal to this <code>ValueModel</code>, else <code>false</code>
     * @param   valueModel  the <code>Object</code> to be compared to this <code>ValueModel</code>
     * @return  boolean - <code>true</code> if the argument <code>valueModel</code> is this
     *          <code>ValueModel</code> or is lexicographically equivalent, else <code>false</code>
     */
    @Override
    abstract boolean equals(Object valueModel);

    /**
     * should get the value of the <code>name</code> field
     *
     * @pre     this <code>ValueModel</code> exists
     * @post    <code>name</code> of this <code>ValueModel</code> should be returned
     * @return  <code>String</code> - <code>name</code> of this <code>ValueModel</code>
     */
    abstract String getName();

    /**
     * should get the value of the <code>one sigma</code> field
     *
     * @pre     this <code>ValueModel</code> exists
     * @post    <code>one sigma</code> of this <code>ValueModel</code> should be returned
     * @return  <code>BigDecimal</code> - <code>one sigma</code> of this <code>ValueModel</code>
     */
    abstract BigDecimal getOneSigma();

    /**
     * should get the value of the <code>one sigma</code> field as ABS value
     *
     * @pre     this <code>ValueModel</code> exists
     * @post    <code>one sigma</code> of this <code>ValueModel</code> when using
     *          ABS <code>uncertainty type</code> should be returned
     * @return  <code>BigDecimal</code> - <code>one sigma</code> value of this
     *          <code>ValueModel</code> using ABS <code>uncertainty type</code>
     */
    abstract BigDecimal getOneSigmaAbs();

    /**
     *
     * @return
     */
    abstract BigDecimal getOneSigmaSysAbs();

    /**
     * should get the value of the <code>one sigma</code> field as PCT value
     *
     * @pre     this <code>ValueModel</code> exists
     * @post    <code>one sigma</code> of this <code>ValueModel</code> when using
     *          PCT <code>uncertainty type</code> should be returned
     * @return  <code>BigDecimal</code> - <code>one sigma</code> value of this
     *          <code>ValueModel</code> using PCT <code>uncertainty type</code>
     */
    abstract BigDecimal getOneSigmaPct();

    /**
     *
     * @return
     */
    abstract BigDecimal getOneSigmaSysPct();

    /**
     * should get the value of two sigma as ABS value
     *
     * @pre     this <code>ValueModel</code> exists
     * @post    two sigma of this <code>ValueModel</code> using ABS
     *          <code>uncertainty type</code> should be returned
     * @return  <code>BigDecimal</code> - two sigma value of this <code>ValueModel
     *          </code> using ABS <code>uncertainty type</code>
     */
    abstract BigDecimal getTwoSigmaAbs();

    /**
     * should get the value of the <code>uncertainty type</code> field
     *
     * @pre     this <code>ValueModel</code> exists
     * @post    <code>uncertainty type</code> of this <code>ValueModel</code> should be returned
     * @return  <code>String</code> - <code>uncertainty type</code> of this <code>ValueModel</code>
     */
    abstract String getUncertaintyType();

    /**
     * should get the value of the <code>value</code> field
     *
     * @pre     this <code>ValueModel</code> exists
     * @post    <code>value</code> of this <code>ValueModel</code> should be returned
     * @return  <code>BigDecimal</code> - <code>value</code> of this <code>ValueModel</code>
     */
    abstract BigDecimal getValue();

    /**
     * should return the hashcode for this <code>ValueModel</code>. Implemented to meet equivalency
     * requirements as documented by <code>java.lang.Object</code>
     *
     * @pre     <code>ValueModel</code> exists
     * @post    hashcode should be returned for this <code>ValueModel</code>
     * @return  int - hashcode
     */
    @Override
    abstract int hashCode();

    /**
     * should set the value of the <code>name</code> field
     *
     * @pre     argument <code>ratioName</code> is a valid <code>String</code>
     * @post    <code>name</code> of this <code>ValueModel</code> should be set
     *          to argument <code>ratioName</code>
     * @param   ratioName    value to which this <code>ValueModel</code>'s
     *                       <code>name</code> should be set
     */
    abstract void setName(String ratioName);

    /**
     * should set the value of the <code>one sigma</code> field
     *
     * @pre     argument <code>ratioError</code> is a valid <code>BigDecimal</code>
     * @post    <code>one sigma</code> of this <code>ValueModel</code> should be
     *          set to argument <code>ratioError</code>
     * @param   ratioError    value to which this <code>ValueModel</code>'s
     *                        <code>one sigma</code> should be set
     */
    abstract void setOneSigma(BigDecimal ratioError);

    /**
     * should set the value of the <code>uncertainty type</code> field
     *
     * @pre     argument <code>ratioUncertaintyType</code> is a valid <code>String</code>
     * @post    <code>uncertainty type</code> of this <code>ValueModel</code>
     *          should be set to argument <code>ratioUncertaintyType</code>
     * @param   ratioUncertaintyType     value to which this <code>ValueModel</code>'s
     *                                   <code>uncertainty type</code> should be set
     */
    abstract void setUncertaintyType(String ratioUncertaintyType);

    /**
     * should set the value of the <code>value</code> field
     *
     * @pre     argument <code>ratioValue</code> is a valid <code>BigDecimal</code>
     * @post    <code>value</code> of this <code>ValueModel</code> should be set
     *          to argument <code>ratioValue</code>
     * @param   ratioValue   value to which this <code>ValueModel</code>'s
     *                       <code>value</code> should be set
     */
    abstract void setValue(BigDecimal ratioValue);
    
    /**
     *
     * @return
     */
    abstract public String formatNameValuePCTVarSysTightReadyForHTML();
}
