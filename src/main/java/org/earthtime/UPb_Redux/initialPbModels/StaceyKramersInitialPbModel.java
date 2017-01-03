/*
 * StaceyKramersInitialPbModel.java
 *
 * Created on October 14, 2007, 8:29 AM
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
package org.earthtime.UPb_Redux.initialPbModels;

import com.thoughtworks.xstream.XStream;
import java.math.BigDecimal;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.UPb_Redux.valueModels.ValueModelXMLConverter;

/**
 *
 * @author James F. Bowring
 */
public class StaceyKramersInitialPbModel extends InitialPbModel {
    // Class variables

    private static final long serialVersionUID = 7092382717291759832L;

    /**
     * Creates a new instance of StaceyKramersInitialPbModel
     */
    public StaceyKramersInitialPbModel () {
        super( "Stacey-Kramers" );
        setReference( "Stacey, J. C. and Kramers, J., 1975. EPSL 26, pp. 207-221" );
        setCalculated( true );
    }

    /**
     *
     * @return
     */
    @Override
    public InitialPbModel copy () {
        InitialPbModel tempModel = new StaceyKramersInitialPbModel();

        copyFieldsTo( tempModel );
        return tempModel;
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

        if ( estimatedAgeInMA.compareTo( new BigDecimal( "3700.0" ) ) == 1 ) {
            BigDecimal ageConst = new BigDecimal( "4.57E9" );

            getRatioByName( "r206_204c" ).
                    setValue(//
                    new BigDecimal( "9.307" ).//
                    add( new BigDecimal( "7.19" ).//
                    multiply( calcExponentialTerm( lambda238, ageConst, estimatedAgeInMA ) ) ) );

            getRatioByName( "r207_204c" ).
                    setValue(//
                    new BigDecimal( "10.294" ).//
                    add( new BigDecimal( Double.toString( 7.19 / 137.88 ) ).//
                    multiply( calcExponentialTerm( lambda235, ageConst, estimatedAgeInMA ) ) ) );

            getRatioByName( "r208_204c" ).
                    setValue(//
                    new BigDecimal( "29.487" ).//
                    add( new BigDecimal( "33.21" ).//
                    multiply( calcExponentialTerm( lambda232, ageConst, estimatedAgeInMA ) ) ) );


        } else {
            BigDecimal ageConst = new BigDecimal( "3.7E9" );

            getRatioByName( "r206_204c" ).
                    setValue(//
                    new BigDecimal( "11.152" ).//
                    add( new BigDecimal( "9.74" ).//
                    multiply( calcExponentialTerm( lambda238, ageConst, estimatedAgeInMA ) ) ) );

            getRatioByName( "r207_204c" ).
                    setValue(//
                    new BigDecimal( "12.998" ).//
                    add( new BigDecimal( Double.toString( 9.74 / 137.88 ) ).//
                    multiply( calcExponentialTerm( lambda235, ageConst, estimatedAgeInMA ) ) ) );

            getRatioByName( "r208_204c" ).
                    setValue(//
                    new BigDecimal( "31.23" ).//
                    add( new BigDecimal( "36.84" ).//
                    multiply( calcExponentialTerm( lambda232, ageConst, estimatedAgeInMA ) ) ) );

        }
    }

    /**
     *
     * @param oneSigmaPct
     * @param rhos
     */
    @Override
    public void calculateUncertaintiesAndRhos ( BigDecimal oneSigmaPct, BigDecimal rhos ) {

        getRatioByName( "r206_204c" ).setOneSigma(//
                ValueModel.convertOneSigmaPctToAbsIfRequired( getRatioByName( "r206_204c" ),
                oneSigmaPct ) );
        getRatioByName( "r207_204c" ).setOneSigma(//
                ValueModel.convertOneSigmaPctToAbsIfRequired( getRatioByName( "r207_204c" ),
                oneSigmaPct ) );
        getRatioByName( "r208_204c" ).setOneSigma(//
                ValueModel.convertOneSigmaPctToAbsIfRequired( getRatioByName( "r208_204c" ),
                oneSigmaPct ) );

        // correlation coeffs are all the same
        getCorrelationCoefficientByName( "rhoR206_204c__r207_204c" ).//
                setValue( rhos );
        getCorrelationCoefficientByName( "rhoR207_204c__r208_204c" ).//
                setValue( rhos );
        getCorrelationCoefficientByName( "rhoR206_204c__r208_204c" ).//
                setValue( rhos );
    }

    private BigDecimal calcExponentialTerm (
            BigDecimal lambda,
            BigDecimal ageConst,
            BigDecimal commonPbAge ) {
        Double term1 = 1.0 + Math.expm1( (lambda.multiply( ageConst )).doubleValue() );
        Double term2 = 1.0 + Math.expm1( (lambda.multiply(
                commonPbAge.multiply( new BigDecimal( "1.0E6" ) ) )).doubleValue() );

        return new BigDecimal( Double.toString( term1 - term2 ) );
    }

    /**
     *
     * @param xstream
     */
    @Override
    public void customizeXstream ( XStream xstream ) {

        xstream.registerConverter( new ValueModelXMLConverter() );

        xstream.alias( "InitialPbModel", StaceyKramersInitialPbModel.class );
        xstream.alias( "ValueModel", ValueModel.class );

        xstream.omitField( InitialPbModel.class, "initialPbModelXMLSchemaURL" );

        setClassXMLSchemaURL();
    }
}
