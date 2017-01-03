/*
 * MolPb205t.java
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
public class MolPb205t extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables sept 2009
    private static final long serialVersionUID = -5340960547841947064L;
    private final static String NAME = "molPb205t";
    private final static String UNCT_TYPE = "ABS";
    // Instance variables
    private ValueModel concPb205t;
    private ValueModel tracerMassInGrams;

    /** Creates a new instance of MolPb205t */
    public MolPb205t () {
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

        concPb205t = inputValueModels[0];
        tracerMassInGrams = inputValueModels[1];

        try {
            setValue(//
                    concPb205t.getValue()//
                    .multiply( tracerMassInGrams.getValue() ) );
        } catch (Exception e) {
            setValue( BigDecimal.ZERO);
        }
        
        try {
            setValueTree(//
                    concPb205t.getValueTree()//
                    .multiply( tracerMassInGrams.getValueTree() ) );
        } catch (Exception e) {
            setValueTree( ExpTreeII.ZERO );
        }
        
        try {
            BigDecimal dMolPb205t__dTracerMassInGrams = concPb205t.getValue();
            parDerivTerms.put( "dMolPb205t__dTracerMassInGrams", dMolPb205t__dTracerMassInGrams );
        } catch (Exception e) {
            parDerivTerms.put( "dMolPb205t__dTracerMassInGrams", BigDecimal.ZERO );
        }

        try {
            BigDecimal dMolPb205t__dConcPb205t = tracerMassInGrams.getValue();
            parDerivTerms.put( "dMolPb205t__dConcPb205t", dMolPb205t__dConcPb205t );
        } catch (Exception e) {
            parDerivTerms.put( "dMolPb205t__dConcPb205t", BigDecimal.ZERO );
        }


    }

}
