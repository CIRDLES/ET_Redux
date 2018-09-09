/*
 * ConcPb206_ib.java
 *
 * Created on Feb 28, 2009
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
public class ConcPb206_ib extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables
    private static final long serialVersionUID = -3412851419609564860L;
    private final static String NAME = "concPb206_ib";
    private final static String UNCT_TYPE = "ABS";
    // Instance variables
    private ValueModel gmol206;
    private ValueModel molPb206b;
    private ValueModel molPb206c;
    private ValueModel fractionMass;

    /** Creates a new instance of ConcPb206_ib */
    public ConcPb206_ib () {
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

        gmol206 = inputValueModels[0];
        molPb206b = inputValueModels[1];
        molPb206c = inputValueModels[2];
        fractionMass = inputValueModels[3];

        try {
            setValue(gmol206.getValue().//
                    multiply( molPb206b.getValue().//
                    add( molPb206c.getValue() ) ).//
                    divide(fractionMass.getValue(), ReduxConstants.mathContext15 ) );
        } catch (Exception e) {
            setValue( BigDecimal.ZERO );
        }

        try {
            setValueTree(//
                    gmol206.getValueTree().//
                    multiply( molPb206b.getValueTree().//
                    add( molPb206c.getValueTree() ) ).//
                    divide( fractionMass.getValueTree() ) );
        } catch (Exception e) {
            setValueTree( ExpTreeII.ZERO );
        }

    }
}
