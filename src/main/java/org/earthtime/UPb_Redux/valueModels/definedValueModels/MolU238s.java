/*
 * MMMolU238s.java
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

/**
 *
 * @author James F. Bowring
 */
public class MolU238s extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables
    private static final long serialVersionUID = 6557345649920932995L;
    private final static String NAME = "molU238s";
    private final static String UNCT_TYPE = "ABS";
    // Instance variables
    private boolean tracerIsMixed233_236;
    // not a metal
    private ValueModel molU235s;
    private ValueModel r238_235s;
    // is a metal
    private ValueModel alphaU;
    private ValueModel molU236t;
    private ValueModel molU238b;
    private ValueModel r238_236oc;
    private ValueModel r238_233t;
    private ValueModel r233_236t;

    /** Creates a new instance of MolU238s */
    public MolU238s () {
        super( NAME, UNCT_TYPE );
        this.tracerIsMixed233_236 = false;
    }

    /**
     * Used to estimate U in the absence of U data.
     * @param molPb206r
     * @param age206_238r
     * @param lambda238
     */
    public void EstimateValue (
            ValueModel molPb206r,
            ValueModel age206_238r,
            ValueModel lambda238 ) {

        double expLambda238Age206_238rMinusOne =//
                Math.expm1( lambda238.getValue().doubleValue()//
                * age206_238r.getValue().doubleValue() );
        try {
            setValue(molPb206r.getValue().//
                    divide(new BigDecimal( expLambda238Age206_238rMinusOne ), ReduxConstants.mathContext15 ) );
        } catch (Exception e) {
            setValue( BigDecimal.ZERO );
        }

        try {
            setValueTree( molPb206r.getValueTree().//
                    divide( new ExpTreeII( expLambda238Age206_238rMinusOne ) ) );
        } catch (Exception e) {
            setValue( BigDecimal.ZERO );
        }

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

        // jan 2011 added in logic for tracer is a metal
        if (  ! tracerIsMixed233_236 ) {
            molU235s = inputValueModels[0];
            r238_235s = inputValueModels[1];

            setValue( molU235s.getValue().multiply( r238_235s.getValue() ) );

            setValueTree( molU235s.getValueTree().multiply( r238_235s.getValueTree() ) );

//            System.out.println( differenceValueCalcs() );

            try {
                BigDecimal dMolU238s__dMolU235s = //
                        r238_235s.getValue();
                parDerivTerms.put( "dMolU238s__dMolU235s", dMolU238s__dMolU235s );
            } catch (Exception e) {
                parDerivTerms.put( "dMolU238s__dMolU235s", BigDecimal.ZERO );
            }

            try {
                BigDecimal dMolU238s__dR238_235s = //
                        molU235s.getValue();
                parDerivTerms.put( "dMolU238s__dR238_235s", dMolU238s__dR238_235s );
            } catch (Exception e) {
                parDerivTerms.put( "dMolU238s__dR238_235s", BigDecimal.ZERO );
            }
        } else {
            // tracer is a metal
            // molU238s = molU236t = r238 236m(1 + 2 alphaU)? r238 233t ? r233 236t ? molU238b
            alphaU = inputValueModels[0];
            molU236t = inputValueModels[1];
            molU238b = inputValueModels[2];
            r238_236oc = inputValueModels[3];
            r238_233t = inputValueModels[4];
            r233_236t = inputValueModels[5];

            setValue( //
                    molU236t.getValue().//
                    multiply( r238_236oc.getValue().//
                    multiply( BigDecimal.ONE.//
                    add( new BigDecimal( 2.0 ).//
                    multiply( alphaU.getValue() ) ) ).//
                    subtract( r238_233t.getValue().//
                    multiply( r233_236t.getValue() ) ) ).//
                    subtract( molU238b.getValue() ) );


             setValueTree( //
                    molU236t.getValueTree().//
                    multiply( r238_236oc.getValueTree().//
                    multiply( ExpTreeII.ONE.//
                    add( ExpTreeII.TWO.//
                    multiply( alphaU.getValueTree() ) ) ).//
                    subtract( r238_233t.getValueTree().//
                    multiply( r233_236t.getValueTree() ) ) ).//
                    subtract( molU238b.getValueTree() ) );
// System.out.println( differenceValueCalcs() );

            BigDecimal dMolU238s__dMolU236t = //
                    r238_236oc.getValue().//
                    multiply( BigDecimal.ONE.//
                    add( new BigDecimal( 2.0 ).//
                    multiply( alphaU.getValue() ) ) ).//
                    subtract( r238_233t.getValue().//
                    multiply( r233_236t.getValue() ) );
            parDerivTerms.put( "dMolU238s__dMolU236t", dMolU238s__dMolU236t );


            BigDecimal dMolU238s__dR238_236oc = //
                    molU236t.getValue().//
                    multiply( BigDecimal.ONE.//
                    add( new BigDecimal( 2.0 ).//
                    multiply( alphaU.getValue() ) ) );
            parDerivTerms.put( "dMolU238s__dR238_236oc", dMolU238s__dR238_236oc );

            BigDecimal dMolU238s__dAlphaU = //
                    new BigDecimal( 2.0 ).//
                    multiply( molU236t.getValue().//
                    multiply( r238_236oc.getValue() ) );
            parDerivTerms.put( "dMolU238s__dAlphaU", dMolU238s__dAlphaU );

            BigDecimal dMolU238s__dR238_233t = //
                    molU236t.getValue().negate().//
                    multiply( r233_236t.getValue() );
            parDerivTerms.put( "dMolU238s__dR238_233t", dMolU238s__dR238_233t );

            BigDecimal dMolU238s__dR233_236t = //
                    molU236t.getValue().negate().//
                    multiply( r238_233t.getValue() );
            parDerivTerms.put( "dMolU238s__dR233_236t", dMolU238s__dR233_236t );

            BigDecimal dMolU238s__dMolU238b = //
                    BigDecimal.ONE.negate();
            parDerivTerms.put( "dMolU238s__dMolU238b", dMolU238s__dMolU238b );
        }
    }

    /**
     * @param tracerIsMixed233_236 the tracerIsMixed233_236 to set
     */
    public void setTracerIsMixed233_236 ( boolean tracerIsMixed233_236 ) {
        this.tracerIsMixed233_236 = tracerIsMixed233_236;
    }
}
