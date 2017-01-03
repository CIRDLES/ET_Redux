/*
 * SampleDateInterceptModel.java
 *
 * Created on December 8 2008
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
package org.earthtime.UPb_Redux.valueModels;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public class SampleDateInterceptModel extends SampleDateModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables
    /**
     * identifies object in binary serialization
     */
    private static final long serialVersionUID = 6249977828870073992L;
    // Instance variables
    private BigDecimal plusInternalTwoSigmaUnct;
    private BigDecimal minusInternalTwoSigmaUnct;
    private BigDecimal plusInternalTwoSigmaUnctWithTracerCalibrationUnct;
    private BigDecimal minusInternalTwoSigmaUnctWithTracerCalibrationUnct;
    private BigDecimal plusInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct;
    private BigDecimal minusInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct;

    /**
     * creates a new instance of <code>SampleDateModel</code> with all of its
     * fields initialized to default values
     */
    public SampleDateInterceptModel () {
        super();
        this.plusInternalTwoSigmaUnct = BigDecimal.ZERO;
        this.minusInternalTwoSigmaUnct = BigDecimal.ZERO;

        this.plusInternalTwoSigmaUnctWithTracerCalibrationUnct = BigDecimal.ZERO;
        this.minusInternalTwoSigmaUnctWithTracerCalibrationUnct = BigDecimal.ZERO;

        this.plusInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct = BigDecimal.ZERO;
        this.minusInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct = BigDecimal.ZERO;
    }

    /**
     * 
     * @param name
     * @param methodName
     * @param dateName
     * @param value
     * @param uncertaintyType
     * @param oneSigma
     */
    public SampleDateInterceptModel (
            String name,
            String methodName,
            String dateName,
            BigDecimal value,
            String uncertaintyType,
            BigDecimal oneSigma ) {

        this();
        setName( name );
        setMethodName( methodName );
        setDateName( dateName );
        setValue( value );
        setUncertaintyType( uncertaintyType );
        setOneSigma( oneSigma );
    }

    /**
     * @return the plusInternalTwoSigmaUnct
     */
    public BigDecimal getPlusInternalTwoSigmaUnct () {
        return plusInternalTwoSigmaUnct;
    }

    /**
     * @param plusInternalTwoSigmaUnct the plusInternalTwoSigmaUnct to set
     */
    public void setPlusInternalTwoSigmaUnct ( BigDecimal plusInternalTwoSigmaUnct ) {
        this.plusInternalTwoSigmaUnct = plusInternalTwoSigmaUnct;
    }

    /**
     * @return the minusInternalTwoSigmaUnct
     */
    public BigDecimal getMinusInternalTwoSigmaUnct () {
        return minusInternalTwoSigmaUnct;
    }

    /**
     * @param minusInternalTwoSigmaUnct the minusInternalTwoSigmaUnct to set
     */
    public void setMinusInternalTwoSigmaUnct ( BigDecimal minusInternalTwoSigmaUnct ) {
        this.minusInternalTwoSigmaUnct = minusInternalTwoSigmaUnct;
    }

    /**
     * @return the plusInternalTwoSigmaUnctWithTracerCalibrationUnct
     */
    public BigDecimal getPlusInternalTwoSigmaUnctWithTracerCalibrationUnct () {
        return plusInternalTwoSigmaUnctWithTracerCalibrationUnct;
    }

    /**
     * @param plusInternalTwoSigmaUnctWithTracerCalibrationUnct the plusInternalTwoSigmaUnctWithTracerCalibrationUnct to set
     */
    public void setPlusInternalTwoSigmaUnctWithTracerCalibrationUnct ( BigDecimal plusInternalTwoSigmaUnctWithTracerCalibrationUnct ) {
        this.plusInternalTwoSigmaUnctWithTracerCalibrationUnct = plusInternalTwoSigmaUnctWithTracerCalibrationUnct;
    }

    /**
     * @return the minusInternalTwoSigmaUnctWithTracerCalibrationUnct
     */
    public BigDecimal getMinusInternalTwoSigmaUnctWithTracerCalibrationUnct () {
        return minusInternalTwoSigmaUnctWithTracerCalibrationUnct;
    }

    /**
     * @param minusInternalTwoSigmaUnctWithTracerCalibrationUnct the minusInternalTwoSigmaUnctWithTracerCalibrationUnct to set
     */
    public void setMinusInternalTwoSigmaUnctWithTracerCalibrationUnct ( BigDecimal minusInternalTwoSigmaUnctWithTracerCalibrationUnct ) {
        this.minusInternalTwoSigmaUnctWithTracerCalibrationUnct = minusInternalTwoSigmaUnctWithTracerCalibrationUnct;
    }

    @Override
    public String FormatValueAndTwoSigmaABSThreeWaysForPublication (
            int divideByPowerOfTen,
            int uncertaintySigDigits ) {

        // innovative method for reporting uncertainty for sample age interpretations
        // will use AAA.AA +/- analytical/analytplustracer/analytplustracerpluslambda
        // so first decide which unct is most precise

        // first determine the shape of specified significant digits of ABS uncertainty

        String twoSigAnalyticalUnctPlus = "0.00";

        if ( getPlusInternalTwoSigmaUnct().compareTo( BigDecimal.ZERO ) != 0 ) {
            twoSigAnalyticalUnctPlus = getPlusInternalTwoSigmaUnct().movePointLeft( divideByPowerOfTen ).//
                    round( new MathContext( uncertaintySigDigits, RoundingMode.HALF_UP ) ).toPlainString();
        }
        int countOfDigitsAfterDec_Aplus = calculateCountOfDigitsAfterDecPoint( twoSigAnalyticalUnctPlus );


        String twoSigAnalyticalUnctMinus = "0.00";

        if ( getMinusInternalTwoSigmaUnct().compareTo( BigDecimal.ZERO ) != 0 ) {
            twoSigAnalyticalUnctMinus = getMinusInternalTwoSigmaUnct().movePointLeft( divideByPowerOfTen ).//
                    round( new MathContext( uncertaintySigDigits, RoundingMode.HALF_UP ) ).toPlainString();
        }
        int countOfDigitsAfterDec_Aminus = calculateCountOfDigitsAfterDecPoint( twoSigAnalyticalUnctMinus );

        int countOfDigitsAfterDecPointInUnct = //
                Math.max( countOfDigitsAfterDec_Aplus, countOfDigitsAfterDec_Aminus );

        // create string from value
        String valueString = //
                getValue().movePointLeft( divideByPowerOfTen ).toPlainString();
        //round(new MathContext(valueSigDigits, RoundingMode.HALF_UP)).toPlainString();

        // determine location of decimal point in value
        int countOfDigitsAfterDecPointInValue = 0;
        int indexOfDecPoint = valueString.indexOf( "." );
        if ( indexOfDecPoint < 0 ) {
            countOfDigitsAfterDecPointInValue = 0;
        } else {
            countOfDigitsAfterDecPointInValue =//
                    valueString.length() - (indexOfDecPoint + 1);
        }

        // calculate how many digits to truncate
        int truncateCount = countOfDigitsAfterDecPointInValue - countOfDigitsAfterDecPointInUnct;
        if ( truncateCount < 0 ) {
            truncateCount = 0;
        }

        // truncate valueString to align decimal point with uncertainty
        valueString = valueString.substring( 0, valueString.length() - truncateCount );

        //strip trailing decimal point if any
        if ( valueString.endsWith( "." ) ) {
            valueString = valueString.substring( 0, valueString.length() - 1 );
        }

        return valueString //
                + " +" //
                + twoSigAnalyticalUnctPlus //
                + "/" + twoSigAnalyticalUnctMinus;
    }

    /**
     * @return the plusInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct
     */
    public BigDecimal getPlusInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct () {
        return plusInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct;
    }

    /**
     * @param plusInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct the plusInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct to set
     */
    public void setPlusInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct ( BigDecimal plusInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct ) {
        this.plusInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct = plusInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct;
    }

    /**
     * @return the minusInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct
     */
    public BigDecimal getMinusInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct () {
        return minusInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct;
    }

    /**
     * @param minusInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct the minusInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct to set
     */
    public void setMinusInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct ( BigDecimal minusInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct ) {
        this.minusInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct = minusInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct;
    }
}
