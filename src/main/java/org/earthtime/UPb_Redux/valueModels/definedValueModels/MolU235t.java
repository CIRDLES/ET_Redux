/*
 * MolU235t.java
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
import org.earthtime.UPb_Redux.expressions.ExpTreeII;
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public class MolU235t extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables sept 2009
    private static final long serialVersionUID = -2778760417782651074L;
    private final static String NAME = "molU235t";
    private final static String UNCT_TYPE = "ABS";
    // Instance variables
    private ValueModel concU235t;
    private ValueModel tracerMassInGrams;

    /** Creates a new instance of MolU235t */
    public MolU235t () {
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

        concU235t = inputValueModels[0];
        tracerMassInGrams = inputValueModels[1];

        try {
            setValue(//
                    concU235t.getValue()//
                    .multiply( tracerMassInGrams.getValue() ) );
        } catch (Exception e) {
            setValue( BigDecimal.ZERO);
        }
        
        

        try {
            setValueTree(//
                    concU235t.getValueTree()//
                    .multiply( tracerMassInGrams.getValueTree() ) );
        } catch (Exception e) {
            setValueTree(ExpTreeII.ZERO);
        }

        //System.out.println(getValue().toPlainString() + "\n" + getValueTree().treeToString(1));
        if ( getValue().compareTo( getValueTree().getNodeValue() ) != 0 ) {
//            System.out.println( differenceValueCalcs() );
        }


        try {
            BigDecimal dMolU235t__dConcU235t = //
                    tracerMassInGrams.getValue();
            parDerivTerms.put( "dMolU235t__dConcU235t", dMolU235t__dConcU235t );
        } catch (Exception e) {
            parDerivTerms.put( "dMolU235t__dConcU235t", BigDecimal.ZERO );
        }

        try {
            BigDecimal dMolU235t__dTracerMassInGrams = //
                    concU235t.getValue();
            parDerivTerms.put( "dMolU235t__dTracerMassInGrams", dMolU235t__dTracerMassInGrams );
        } catch (Exception e) {
            parDerivTerms.put( "dMolU235t__dTracerMassInGrams", BigDecimal.ZERO );
        }
    }
}
