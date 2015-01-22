/*
 * MolU238b.java
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
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public class MolU238b extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables sept 2009
    private static final long serialVersionUID = -6370373166907180264L;
    private final static String NAME = "molU238b";
    private final static String UNCT_TYPE = "ABS";
    // Instance variables
    private ValueModel r238_235b;
    private ValueModel molU235b;

    /** Creates a new instance of MolU238b */
    public MolU238b () {
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

        r238_235b = inputValueModels[0];
        molU235b = inputValueModels[1];

        setValue(//
                r238_235b.getValue().multiply( molU235b.getValue() ) );

        try {
            BigDecimal dMolU238b__dMolU235b = //
                    r238_235b.getValue();
            parDerivTerms.put( "dMolU238b__dMolU235b", dMolU238b__dMolU235b );
        } catch (Exception e) {
            parDerivTerms.put( "dMolU238b__dMolU235b", BigDecimal.ZERO );
        }

        try {
            BigDecimal dMolU238b__dR238_235b = //
                    molU235b.getValue();
            parDerivTerms.put( "dMolU238b__dR238_235b", dMolU238b__dR238_235b );
        } catch (Exception e) {
            parDerivTerms.put( "dMolU238b__dR238_235b", BigDecimal.ZERO );
        }
    }
}
