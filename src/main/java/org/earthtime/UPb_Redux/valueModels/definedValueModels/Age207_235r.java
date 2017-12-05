/*
 * Age207_235r.java
 *
 * Created on Dec 12, 2008
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
public class Age207_235r extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables
    private static final long serialVersionUID = -5062220655350086611L;
    private final static String NAME = RadDates.age207_235r.getName();
    private final static String UNCT_TYPE = "ABS";
    // Instance variables
    private ValueModel r207_235r;
    private ValueModel lambda235;

    /**
     * Creates a new instance of Age207_235r
     */
    public Age207_235r () {
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

        r207_235r = inputValueModels[0];
        lambda235 = inputValueModels[1];

        try {
            setValue(//
                    new BigDecimal(//
                    //
                    //
                    //
                    Math.log1p( r207_235r.getValue().doubleValue() ) //
                    / lambda235.getValue().doubleValue(), ReduxConstants.mathContext15 ) );
        } catch (Exception e) {
            setValue( BigDecimal.ZERO );
        }

        try {
            setValueTree(//
                    r207_235r.getValueTree().add( ExpTreeII.ONE ).log(). //
                    divide( lambda235.getValueTree() ) );
        } catch (Exception e) {
            setValueTree( ExpTreeII.ZERO );
        }
        
        
        if ( parDerivTerms != null ) {
            // oct 2014 to handle common lead
            String partialDerivativeNameAge__Lambda = "dA" + name.substring(1) + "__dLambda235";//dAge207_235r__dLambda235
            try {
                BigDecimal dAge207_235r__dLambda235 =new BigDecimal( Math.log1p( r207_235r.getValue().doubleValue() ), ReduxConstants.mathContext15 ).negate().//
                        divide(lambda235.getValue().pow( 2 ), ReduxConstants.mathContext15 );
                parDerivTerms.put(partialDerivativeNameAge__Lambda, dAge207_235r__dLambda235 );
            } catch (Exception e) {
                parDerivTerms.put(partialDerivativeNameAge__Lambda, BigDecimal.ZERO );
            }

            // oct 2014 to handle common lead
            String partialDerivativeNameAge__Ratio = "dA" + name.substring(1) + "__dR" + r207_235r.getName().substring(1);//dAge207_235r__dR207_235r
            try {
                BigDecimal dAge207_235r__dR207_235r = BigDecimal.ONE.//
                        divide(lambda235.getValue(), ReduxConstants.mathContext15 ).// removed July 2012 by Noah negate().//
                        divide(r207_235r.getValue().add( BigDecimal.ONE ), ReduxConstants.mathContext15 );
                parDerivTerms.put( partialDerivativeNameAge__Ratio, dAge207_235r__dR207_235r );
            } catch (Exception e) {
                parDerivTerms.put( partialDerivativeNameAge__Ratio, BigDecimal.ZERO );
            }
        }
    }
}
