/*
 * Age206_238r_Th.java
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
public class Age206_238r_Th extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables sept 2009
    private static final long serialVersionUID = 5471370245490468851L;
    private final static String NAME = RadDates.age206_238r_Th.getName();
    private final static String UNCT_TYPE = "ABS";
    // Instance variables
    private ValueModel lambda230;
    private ValueModel lambda232;
    private ValueModel lambda238;
    private ValueModel age206_238r;
    private ValueModel r206_238r;
    private ValueModel molPb208r;
    private ValueModel molU235s;
    private ValueModel molU238s;
    private ValueModel rTh_Umagma;
    private ValueModel molsU;
    private ValueModel r238_235s;
    private ValueModel molPb205t;
    private ValueModel alphaPb;
    private ValueModel r204_205fc;
    private ValueModel r204_205t;

    /** Creates a new instance of Age206_238r_Th */
    public Age206_238r_Th () {
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

        lambda230 = inputValueModels[0];
        lambda232 = inputValueModels[1];
        lambda238 = inputValueModels[2];
        age206_238r = inputValueModels[3];
        r206_238r = inputValueModels[4];
        molPb208r = inputValueModels[5];
        molU235s = inputValueModels[6];
        molU238s = inputValueModels[7];
        rTh_Umagma = inputValueModels[8];
        molsU = inputValueModels[9];
        r238_235s = inputValueModels[10];
        molPb205t = inputValueModels[11];
        alphaPb = inputValueModels[12];
        r204_205fc = inputValueModels[13];
        r204_205t = inputValueModels[14];



        double xn = age206_238r.getValue().doubleValue();

        for (int i = 0; i < 100; i ++) {

            double expLambda238xnMinus1 = Math.expm1( lambda238.getValue().doubleValue() * xn );
            double expLambda232xnMinus1 = Math.expm1( lambda232.getValue().doubleValue() * xn );

            double new10 =
                    r206_238r.getValue().doubleValue() //
                    - (expLambda238xnMinus1 //
                    + lambda238.getValue().doubleValue()//
                    / lambda230.getValue().doubleValue()//
                    * (molPb208r.getValue().doubleValue() //
                    / expLambda232xnMinus1 //
                    / (molU238s.getValue().doubleValue() + molU235s.getValue().doubleValue()) //
                    / rTh_Umagma.getValue().doubleValue() //
                    - 1.0));

            double new11 =
                    (expLambda232xnMinus1 + 1.0) //
                    * lambda232.getValue().doubleValue() //
                    * lambda238.getValue().doubleValue() //
                    * molPb208r.getValue().doubleValue() //
                    / expLambda232xnMinus1 //
                    / expLambda232xnMinus1 //
                    / lambda230.getValue().doubleValue() //
                    / (molU238s.getValue().doubleValue() + molU235s.getValue().doubleValue()) //
                    / rTh_Umagma.getValue().doubleValue() //
                    - lambda238.getValue().doubleValue()//
                    * (1.0 + expLambda238xnMinus1);

            xn = xn - (new10 / new11);

        }

        try {
            setValue(new BigDecimal( xn, ReduxConstants.mathContext15  ) );
        } catch (Exception e) {
            setValue( BigDecimal.ZERO );
        }

       try {
            setValueTree( new ExpTreeII( xn ) );
        } catch (Exception e) {
            setValueTree( ExpTreeII.ZERO );
        }

        double t = getValue().doubleValue();
        double expLambda232Lambda238t = //
                1.0 //
                + Math.expm1( lambda232.getValue().//
                add( lambda238.getValue() ).doubleValue() * t );
        double exp2Lambda232Lambda238t = //
                1.0 //
                + Math.expm1(new BigDecimal( 2.0, ReduxConstants.mathContext15  ).multiply( lambda232.getValue() ).//
                add( lambda238.getValue() ).doubleValue() * t );
        double expLambda238t = //
                1.0 //
                + Math.expm1( lambda238.getValue().doubleValue() * t );
        double expLambda232t = //
                1.0 //
                + Math.expm1( lambda232.getValue().doubleValue() * t );



        double commonDenom = //
                lambda230.getValue().doubleValue()//
                * molsU.getValue().doubleValue()//
                * rTh_Umagma.getValue().doubleValue()//
                * (2.0//
                * expLambda232Lambda238t//
                - exp2Lambda232Lambda238t//
                - expLambda238t)//
                + lambda232.getValue().doubleValue()//
                * molPb208r.getValue().doubleValue()//
                * expLambda232t;

        try {
            double dAge206_238r_Th__dR206_238r =//
                    1.0 //
                    / (lambda238.getValue().doubleValue()//
                    * expLambda238t //
                    - (lambda232.getValue().doubleValue() //
                    * lambda238.getValue().doubleValue() //
                    * molPb208r.getValue().doubleValue() //
                    * expLambda232t) //
                    / (lambda230.getValue().doubleValue() //
                    * molsU.getValue().doubleValue() //
                    * rTh_Umagma.getValue().doubleValue() //
                    * (expLambda232t - 1.0) * (expLambda232t - 1.0)));
            parDerivTerms.put("dAge206_238r_Th__dR206_238r", new BigDecimal( dAge206_238r_Th__dR206_238r, ReduxConstants.mathContext15  ) );
        } catch (Exception e) {
            parDerivTerms.put( "dAge206_238r_Th__dR206_238r", BigDecimal.ZERO );
        }


        try {
            double dAge206_238r_Th__dMolPb208r =//
                    (expLambda232t - 1.0) //
                    / commonDenom;
            parDerivTerms.put("dAge206_238r_Th__dMolPb208r", new BigDecimal( dAge206_238r_Th__dMolPb208r, ReduxConstants.mathContext15  ) );
        } catch (Exception e) {
            parDerivTerms.put( "dAge206_238r_Th__dMolPb208r", BigDecimal.ZERO );
        }


        try {
            double dAge206_238r_Th__dRTh_Umagma =//
                    molPb208r.getValue().negate().doubleValue()//
                    * (expLambda232t - 1.0) //
                    / rTh_Umagma.getValue().doubleValue() //
                    / commonDenom;
            parDerivTerms.put("dAge206_238r_Th__dRTh_Umagma", new BigDecimal( dAge206_238r_Th__dRTh_Umagma, ReduxConstants.mathContext15  ) );
        } catch (Exception e) {
            parDerivTerms.put( "dAge206_238r_Th__dRTh_Umagma", BigDecimal.ZERO );
        }

        try {
            double dAge206_238r_Th__dMolsU =//
                    molPb208r.getValue().negate().doubleValue()//
                    * (expLambda232t - 1.0)//
                    / molsU.getValue().doubleValue()//
                    / commonDenom;
            parDerivTerms.put("dAge206_238r_Th__dMolsU", new BigDecimal( dAge206_238r_Th__dMolsU, ReduxConstants.mathContext15  ) );
        } catch (Exception e) {
            parDerivTerms.put( "dAge206_238r_Th__dMolsU", BigDecimal.ZERO );
        }

        try {
            double dAge206_238r_Th__dLambda238 =//
                    (expLambda232t - 1.0)//
                    * (molPb208r.getValue().doubleValue()//
                    + molsU.getValue().doubleValue()//
                    * rTh_Umagma.getValue().doubleValue()//
                    * (expLambda232t - 1.0)//
                    * (t * lambda230.getValue().doubleValue() * expLambda238t - 1.0))//
                    / lambda238.getValue().doubleValue()//
                    / commonDenom;
            parDerivTerms.put("dAge206_238r_Th__dLambda238", new BigDecimal( dAge206_238r_Th__dLambda238, ReduxConstants.mathContext15  ) );
        } catch (Exception e) {
            parDerivTerms.put( "dAge206_238r_Th__dLambda238", BigDecimal.ZERO );
        }

        try {
            double dAge206_238r_Th__dLambda230 =//
                    (expLambda232t - 1.0)//
                    * (molsU.getValue().doubleValue()//
                    * rTh_Umagma.getValue().doubleValue()//
                    * (expLambda232t - 1.0)//
                    - molPb208r.getValue().doubleValue())//
                    / lambda230.getValue().doubleValue()//
                    / commonDenom;
            parDerivTerms.put("dAge206_238r_Th__dLambda230", new BigDecimal( dAge206_238r_Th__dLambda230, ReduxConstants.mathContext15  ) );
        } catch (Exception e) {
            parDerivTerms.put( "dAge206_238r_Th__dLambda230", BigDecimal.ZERO );
        }

        try {
            double dAge206_238r_Th__dLambda232 =//
                     - t//
                    * molPb208r.getValue().doubleValue()//
                    * expLambda232t//
                    / commonDenom;
            parDerivTerms.put("dAge206_238r_Th__dLambda232", new BigDecimal( dAge206_238r_Th__dLambda232, ReduxConstants.mathContext15  ) );
        } catch (Exception e) {
            parDerivTerms.put( "dAge206_238r_Th__dLambda232", BigDecimal.ZERO );
        }
    }

}
