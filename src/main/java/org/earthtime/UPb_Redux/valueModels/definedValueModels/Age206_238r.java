/*
 * Age206_238r.java
 *
 * Created on Dec 12, 2008
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
public class Age206_238r extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables sept 2009
    private static final long serialVersionUID = 4128909761448808610L;
    private final static String NAME = RadDates.age206_238r.getName();
    private final static String UNCT_TYPE = "ABS";
    // Instance variables
    private ValueModel r206_238r;
    private ValueModel lambda238;

    /**
     * Creates a new instance of Age206_238r
     */
    public Age206_238r () {
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

        r206_238r = inputValueModels[0];
        lambda238 = inputValueModels[1];

        try {
            setValue(//                    
                    new BigDecimal(//
                    Double.toString( // testing may 2012
                    Math.log1p( r206_238r.getValue().doubleValue() ) //
                    / lambda238.getValue().doubleValue() ) ) );
        } catch (Exception e) {
            setValue( BigDecimal.ZERO );
        }


        try {
            setValueTree(//
                    r206_238r.getValueTree().add( ExpTreeII.ONE ).log(). //
                    divide( lambda238.getValueTree() ) );
        } catch (Exception e) {
            setValue( BigDecimal.ZERO );
        }

//        System.out.println(differenceValueCalcs());
        if ( parDerivTerms != null ) {
            // oct 2014 to handle common lead
            String partialDerivativeNameAge__Lambda = "dA" + name.substring(1) + "__dLambda238";//dAge206_238r__dLambda238
            try {
                BigDecimal dAge206_238r__dLambda238 = new BigDecimal( Double.toString( Math.log1p( r206_238r.getValue().doubleValue() ) ) ).//
                        divide(lambda238.getValue().pow( 2 ), ReduxConstants.mathContext15 ).negate();// negate added July 2012 by Noah
                parDerivTerms.put( partialDerivativeNameAge__Lambda, dAge206_238r__dLambda238 );
            } catch (Exception e) {
                parDerivTerms.put( partialDerivativeNameAge__Lambda, BigDecimal.ZERO );
            }

            // oct 2014 to handle common lead
            String partialDerivativeNameAge__Ratio = "dA" + name.substring(1) + "__dR" + r206_238r.getName().substring(1);//dAge206_238r__dR206_238r
            try {
                BigDecimal dAge206_238r__dR206_238r = BigDecimal.ONE.//
                        divide(lambda238.getValue(), ReduxConstants.mathContext15 ).//
                        divide(r206_238r.getValue().add( BigDecimal.ONE ), ReduxConstants.mathContext15 );
                parDerivTerms.put( partialDerivativeNameAge__Ratio, dAge206_238r__dR206_238r );
            } catch (Exception e) {
                parDerivTerms.put( partialDerivativeNameAge__Ratio, BigDecimal.ZERO );
            }
        }
    }
}
