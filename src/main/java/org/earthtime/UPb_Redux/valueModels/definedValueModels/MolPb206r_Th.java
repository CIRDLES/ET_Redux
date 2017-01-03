/*
 * MolPb206r_Th.java
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
import org.earthtime.UPb_Redux.expressions.ExpTreeII;
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public class MolPb206r_Th extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables
    private static final long serialVersionUID = -7880223408927157867L;
    private final static String NAME = "molPb206r_Th";
    private final static String UNCT_TYPE = "ABS";
    // Instance variables
//    private ValueModel lambda230;
//    private ValueModel lambda232;
    private ValueModel lambda238;
    private ValueModel age206_238r_Th;
    private ValueModel molU238s;
//    private ValueModel molTh232s;
//    private ValueModel rTh_Umagma;
//    private ValueModel molsU;

    /** Creates a new instance of MolPb206r_Th */
    public MolPb206r_Th () {
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

        lambda238 = inputValueModels[0];
        age206_238r_Th = inputValueModels[1];
        molU238s = inputValueModels[2];

        BigDecimal expLambda238_Age206_238r_Th_Minus1 =
                new BigDecimal(//
                Math.expm1( lambda238.getValue().doubleValue() * age206_238r_Th.getValue().doubleValue() )//
                );

        // revised April 2009
        try {
            setValue(//
                    molU238s.getValue().//
                    multiply( expLambda238_Age206_238r_Th_Minus1 ) );

        } catch (Exception e) {
            setValue( BigDecimal.ZERO );
        }

        try {
            setValueTree(//
                    molU238s.getValueTree().//
                    multiply( new ExpTreeII( expLambda238_Age206_238r_Th_Minus1 ) ) );

        } catch (Exception e) {
            setValue( BigDecimal.ZERO );
        }

        // revised April 2009
        try {
            BigDecimal dMolPb206r_Th__dMolU238s =//
                    expLambda238_Age206_238r_Th_Minus1;
            parDerivTerms.put( "dMolPb206r_Th__dMolU238s", dMolPb206r_Th__dMolU238s );
        } catch (Exception e) {
            parDerivTerms.put( "dMolPb206r_Th__dMolU238s", BigDecimal.ZERO );
        }


        try {
            BigDecimal dMolPb206r_Th__dAge206_238r_Th =//
                    lambda238.getValue().//
                    multiply( molU238s.getValue() ).//
                    multiply( expLambda238_Age206_238r_Th_Minus1.add( BigDecimal.ONE ) );
            parDerivTerms.put( "dMolPb206r_Th__dAge206_238r_Th", dMolPb206r_Th__dAge206_238r_Th );
        } catch (Exception e) {
            parDerivTerms.put( "dMolPb206r_Th__dAge206_238r_Th", BigDecimal.ZERO );
        }

        // revised April 2009
        try {
            BigDecimal dMolPb206r_Th__dLambda238 =//
                    molU238s.getValue().//
                    multiply( age206_238r_Th.getValue().//
                    multiply( expLambda238_Age206_238r_Th_Minus1.add( BigDecimal.ONE ) ) );
            parDerivTerms.put( "dMolPb206r_Th__dLambda238", dMolPb206r_Th__dLambda238 );
        } catch (Exception e) {
            parDerivTerms.put( "dMolPb206r_Th__dLambda238", BigDecimal.ZERO );
        }
    }

 
}
