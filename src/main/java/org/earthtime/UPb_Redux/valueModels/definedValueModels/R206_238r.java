/*
 * R206_238r.java
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
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public class R206_238r extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables sept 2009
    private static final long serialVersionUID = 5414928994651440506L;
    private final static String NAME = "r206_238r";
    private final static String UNCT_TYPE = "ABS";
    // Instance variables
    private ValueModel molPb206r;
    private ValueModel molU238s;

    /** Creates a new instance of R206_238r */
    public R206_238r () {
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

        molPb206r = inputValueModels[0];
        molU238s = inputValueModels[1];

        try {
            setValue(molPb206r.getValue().//
                    divide(molU238s.getValue(), ReduxConstants.mathContext15 ) );
        } catch (Exception e) {
            setValue( BigDecimal.ZERO );
        }

        try {
            setValueTree(
                    molPb206r.getValueTree().//
                    divide( molU238s.getValueTree()) );
        } catch (Exception e) {
            setValue( BigDecimal.ZERO );
        }

//               System.out.println(differenceValueCalcs());

        try {
            BigDecimal dR206_238r__dMolPb206r = BigDecimal.ONE.divide(molU238s.getValue(), ReduxConstants.mathContext15 );
            parDerivTerms.put( "dR206_238r__dMolPb206r", dR206_238r__dMolPb206r );
        } catch (Exception e) {
            parDerivTerms.put( "dR206_238r__dMolPb206r", BigDecimal.ZERO );
        }


        try {
            BigDecimal dR206_238r__dMolU238s = molPb206r.getValue().negate().//
                    divide(molU238s.getValue().pow( 2 ), ReduxConstants.mathContext15 );
            parDerivTerms.put( "dR206_238r__dMolU238s", dR206_238r__dMolU238s );
        } catch (Exception e) {
            parDerivTerms.put( "dR206_238r__dMolU238s", BigDecimal.ZERO );
        }

        try {
            BigDecimal dR206_238r__dConcU235t =//
                    parDerivTerms.get( "dR206_238r__dMolU238s" ).//
                    multiply( parDerivTerms.get( "dMolU238s__dMolU235s" ) ).//
                    multiply( parDerivTerms.get( "dMolU235s__dMolU235t" ).//
                    multiply( parDerivTerms.get( "dMolU235t__dConcU235t" ) ).//

                    add( parDerivTerms.get( "dMolU235s__dMolU238t" ).//
                    multiply( parDerivTerms.get( "dMolU238t__dMolU235t" ).//
                    multiply( parDerivTerms.get( "dMolU235t__dConcU235t" ) ) ) ).//

                    add( parDerivTerms.get( "dMolU235s__dMolU233t" ).//
                    multiply( parDerivTerms.get( "dMolU233t__dMolU235t" ).//
                    multiply( parDerivTerms.get( "dMolU235t__dConcU235t" ) ) ) ) );
            parDerivTerms.put( "dR206_238r__dConcU235t", dR206_238r__dConcU235t );
        } catch (Exception e) {
            parDerivTerms.put( "dR206_238r__dConcU235t", BigDecimal.ZERO );
        }

        try {
            BigDecimal dR206_238r__dConcPb205t = //
                    parDerivTerms.get( "dR206_238r__dMolPb206r" ).//
                    multiply( parDerivTerms.get( "dMolPb206r__dMolPb205t" ).//
                    multiply( parDerivTerms.get( "dMolPb205t__dConcPb205t" ) ).//
                    add( parDerivTerms.get( "dMolPb206r__dMolPb206b" ).//
                    multiply( parDerivTerms.get( "dMolPb206b__dMolPb205t" ).//
                    multiply( parDerivTerms.get( "dMolPb205t__dConcPb205t" ) ) ) ).//
                    add( parDerivTerms.get( "dMolPb206r__dMolPb206c" ).//
                    multiply( parDerivTerms.get( "dMolPb206c__dMolPb204c" ).//)
                    multiply( parDerivTerms.get( "dMolPb204c__dMolPb204tc" ).//)
                    multiply( parDerivTerms.get( "dMolPb204tc__dMolPb205t" ).//)
                    multiply( parDerivTerms.get( "dMolPb205t__dConcPb205t" ) ) ) ) ) ) );
            parDerivTerms.put( "dR206_238r__dConcPb205t", dR206_238r__dConcPb205t );
        } catch (Exception e) {
            parDerivTerms.put( "dR206_238r__dConcPb205t", BigDecimal.ZERO );
        }
    }

}
