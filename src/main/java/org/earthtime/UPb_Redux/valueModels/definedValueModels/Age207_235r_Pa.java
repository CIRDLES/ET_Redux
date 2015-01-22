/*
 * Age207_235r_Pa.java
 *
 * Created on Dec 12, 2008
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
package org.earthtime.UPb_Redux.valueModels.definedValueModels;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.concurrent.ConcurrentMap;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.expressions.ExpTreeII;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.dataDictionaries.RadDates;

/**
 *
 * @author James F. Bowring
 */
public class Age207_235r_Pa extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables
    private static final long serialVersionUID = -6803061368159117076L;
    private final static String NAME = RadDates.age207_235r_Pa.getName();
    private final static String UNCT_TYPE = "ABS";
    // Instance variables
    private ValueModel ar231_235sample;
    private ValueModel r207_235r;
    private ValueModel lambda235;
    private ValueModel lambda231;

    /** Creates a new instance of Age207_235r_Pa */
    public Age207_235r_Pa () {
        super( NAME, UNCT_TYPE );
    }

    /**
     * 
     * @param inputValueModels
     * @param parDerivTerms
     */
    @Override
    public void calculateValue (
            ValueModel[] inputValueModels,
            ConcurrentMap<String, BigDecimal> parDerivTerms ) {

        ar231_235sample = inputValueModels[0];
        r207_235r = inputValueModels[1];
        lambda235 = inputValueModels[2];
        lambda231 = inputValueModels[3];


        // nov 2009 to clean up outputs
        if ( r207_235r.getValue().compareTo( BigDecimal.ZERO ) == 0 ) {
            setValue( BigDecimal.ZERO );
            setValueTree( ExpTreeII.ZERO );
        } else {
            try {
                // jan 2011
                double value =
                        (1.0 //
                        / lambda235.getValue().doubleValue()) //
                        * (Math.log1p( //
                        r207_235r.getValue().doubleValue() //
                        - (lambda235.getValue().doubleValue() //
                        / lambda231.getValue().doubleValue() )//
                        * (ar231_235sample.getValue().doubleValue() - 1.0) ));

                setValue(new BigDecimal( value, ReduxConstants.mathContext15 ) );
                setValueTree(//
                        ExpTreeII.ONE.//
                        divide( lambda235.getValueTree()).//
                        multiply( r207_235r.getValueTree().//
                        subtract(lambda235.getValueTree().divide( lambda231.getValueTree()).//
                        multiply( ar231_235sample.getValueTree().subtract( ExpTreeII.ONE ))).//
                        add( ExpTreeII.ONE ).log()));
            } catch (Exception e) {
                setValue( BigDecimal.ZERO );
                setValueTree( ExpTreeII.ZERO );
            }


//                System.out.println(differenceValueCalcs());
                
            // Jan 2011
            BigDecimal denominator = //
                    lambda231.getValue().//
                    multiply( BigDecimal.ONE.add( r207_235r.getValue() ) ).//
                    add( lambda235.getValue().//
                    multiply( BigDecimal.ONE.//
                    subtract( ar231_235sample.getValue() ) ) );

            // partial derivatives
            BigDecimal dAge207_235r_Pa__dLambda235 = BigDecimal.ZERO;
            BigDecimal dAge207_235r_Pa__dR207_235r = BigDecimal.ZERO;
            BigDecimal dAge207_235r_Pa__dLambda231 = BigDecimal.ZERO;
            BigDecimal dAge207_235r_Pa__dAr231_235sample = BigDecimal.ZERO;

            if ( denominator.compareTo( BigDecimal.ZERO ) != 0 ) {
                dAge207_235r_Pa__dLambda235 = BigDecimal.ONE.//
                        divide(lambda235.getValue(), ReduxConstants.mathContext15).//
                        multiply(BigDecimal.ONE.subtract( ar231_235sample.getValue()).//
                        divide(denominator , ReduxConstants.mathContext15).//
                        subtract( getValue()));

                dAge207_235r_Pa__dR207_235r = lambda231.getValue().//
                        divide(lambda235.getValue(), ReduxConstants.mathContext15).//
                        divide(denominator, ReduxConstants.mathContext15);

                dAge207_235r_Pa__dLambda231 = ar231_235sample.getValue().//
                        subtract( BigDecimal.ONE).//
                        divide(lambda231.getValue(), ReduxConstants.mathContext15).//
                        divide(denominator, ReduxConstants.mathContext15);

                dAge207_235r_Pa__dAr231_235sample = BigDecimal.ONE.negate().//
                        divide(denominator, ReduxConstants.mathContext15);

            }

            parDerivTerms.put( "dAge207_235r_Pa__dLambda235", dAge207_235r_Pa__dLambda235 );
            parDerivTerms.put( "dAge207_235r_Pa__dR207_235r", dAge207_235r_Pa__dR207_235r );
            parDerivTerms.put( "dAge207_235r_Pa__dLambda231", dAge207_235r_Pa__dLambda231 );
            parDerivTerms.put( "dAge207_235r_Pa__dAr231_235sample", dAge207_235r_Pa__dAr231_235sample );

        }

    }


}
