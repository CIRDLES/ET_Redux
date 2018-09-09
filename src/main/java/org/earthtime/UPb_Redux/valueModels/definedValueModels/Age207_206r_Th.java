/*
 * Age207_206r_Th.java
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
public class Age207_206r_Th extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables
    private static final long serialVersionUID = 4590063800282772449L;
    private final static String NAME = RadDates.age207_206r_Th.getName();
    private final static String UNCT_TYPE = "ABS";
    // Instance variables
    private ValueModel r238_235s;
    private ValueModel r207_206r_Th;
    private ValueModel age207_206r;
    private ValueModel lambda235;
    private ValueModel lambda238;

    /** Creates a new instance of Age207_206r_Th */
    public Age207_206r_Th () {
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

        r238_235s = inputValueModels[0];
        r207_206r_Th = inputValueModels[1];
        age207_206r = inputValueModels[2];
        lambda235 = inputValueModels[3];
        lambda238 = inputValueModels[4];

        double xn = age207_206r.getValue().doubleValue();

        for (int i = 0; i < 100; i ++) {

            double expLambda238xnMinus1 = Math.expm1( lambda238.getValue().doubleValue() * xn );
            double expLambda235xnMinus1 = Math.expm1( lambda235.getValue().doubleValue() * xn );

            double new10 =
                    r207_206r_Th.getValue().negate().doubleValue()//
                    + expLambda235xnMinus1 //
                    / r238_235s.getValue().doubleValue()//
                    / expLambda238xnMinus1;

            double new11 =
                    (r238_235s.getValue().doubleValue() //
                    * expLambda238xnMinus1 //
                    * lambda235.getValue().doubleValue() //
                    * (1.0 + expLambda235xnMinus1) //
                    - expLambda235xnMinus1 //
                    * r238_235s.getValue().doubleValue() //
                    * lambda238.getValue().doubleValue() //
                    * (1.0 + expLambda238xnMinus1)) //
                    / r238_235s.getValue().pow( 2 ).doubleValue()//
                    / expLambda238xnMinus1 //
                    / expLambda238xnMinus1;

            xn = xn - (new10 / new11);

        }

        try {
            setValue(new BigDecimal( xn, ReduxConstants.mathContext15  ) );
        } catch (Exception e) {
            setValue( BigDecimal.ZERO );
        }

        try {
            setValueTree( new ExpTreeII( xn  ) );
        } catch (Exception e) {
            setValueTree( ExpTreeII.ZERO );
        }


        BigDecimal BDexpLambda238xnMinus1 = //
                new BigDecimal(//
                //
                //
                //
                Math.expm1( lambda238.getValue().doubleValue()//
                * getValue().doubleValue() ), ReduxConstants.mathContext15  );

        BigDecimal BDexpLambda235xnMinus1 = //
                new BigDecimal(//
                //
                //
                //
                Math.expm1( lambda235.getValue().doubleValue()//
                * getValue().doubleValue() ), ReduxConstants.mathContext15  );

        BigDecimal denominator = //
                lambda235.getValue().//
                multiply( BigDecimal.ONE.//
                add( BDexpLambda235xnMinus1 ) ).//

                subtract( r238_235s.getValue().//
                multiply( r207_206r_Th.getValue() ).//
                multiply( lambda238.getValue() ).//
                multiply( BigDecimal.ONE.//
                add( BDexpLambda238xnMinus1 ) ) );



        try {
            BigDecimal dAge207_206r_Th__dR207_206r_Th = r238_235s.getValue().//
                    multiply( BDexpLambda238xnMinus1 ).//
                    divide(denominator, ReduxConstants.mathContext15 );
            parDerivTerms.put( "dAge207_206r_Th__dR207_206r_Th", dAge207_206r_Th__dR207_206r_Th );
        } catch (Exception e) {
            parDerivTerms.put( "dAge207_206r_Th__dR207_206r_Th", BigDecimal.ZERO );
        }

        try {
            BigDecimal dAge207_206r_Th__dLambda235 =getValue().//
                    multiply( BigDecimal.ONE.//
                    add( BDexpLambda235xnMinus1 ) ).//
                    divide(denominator, ReduxConstants.mathContext15 ).//
                    negate();// negate added July 2012 by Noah
            parDerivTerms.put( "dAge207_206r_Th__dLambda235", dAge207_206r_Th__dLambda235 );
        } catch (Exception e) {
            parDerivTerms.put( "dAge207_206r_Th__dLambda235", BigDecimal.ZERO );
        }

        try {
            BigDecimal dAge207_206r_Th__dLambda238 = r238_235s.getValue().//
                    multiply( r207_206r_Th.getValue() ).//
                    multiply( getValue() ).//
                    multiply( BigDecimal.ONE.//
                    add( BDexpLambda238xnMinus1 ) ).//
                    divide(denominator, ReduxConstants.mathContext15 );
            parDerivTerms.put( "dAge207_206r_Th__dLambda238", dAge207_206r_Th__dLambda238 );
        } catch (Exception e) {
            parDerivTerms.put( "dAge207_206r_Th__dLambda238", BigDecimal.ZERO );
        }

        try {
            BigDecimal dAge207_206r_Th__dR238_235s = r207_206r_Th.getValue().//
                    multiply( BDexpLambda238xnMinus1 ).//
                    divide(denominator, ReduxConstants.mathContext15 );
            parDerivTerms.put( "dAge207_206r_Th__dR238_235s", dAge207_206r_Th__dR238_235s );
        } catch (Exception e) {
            parDerivTerms.put( "dAge207_206r_Th__dR238_235s", BigDecimal.ZERO );
        }



    }
}
