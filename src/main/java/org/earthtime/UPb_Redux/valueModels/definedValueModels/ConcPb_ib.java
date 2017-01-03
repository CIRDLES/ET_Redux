/*
 * ConcPb_ib.java
 *
 * Created on Feb 28, 2009
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

/**
 *
 * @author James F. Bowring
 */
public class ConcPb_ib extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables
    private static final long serialVersionUID = -6784877178802063542L;
    private final static String NAME = "concPb_ib";
    private final static String UNCT_TYPE = "ABS";
    // Instance variables
    private ValueModel blankPbMass;
    private ValueModel initCommonPbMass;
    private ValueModel fractionMass;

    /** Creates a new instance of ConcPb_ib */
    public ConcPb_ib () {
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


        blankPbMass = inputValueModels[0];
        initCommonPbMass = inputValueModels[1];
        fractionMass = inputValueModels[2];

        try {
            setValue(blankPbMass.getValue().//
                    add( initCommonPbMass.getValue() ).//
                    divide(fractionMass.getValue(), ReduxConstants.mathContext15 ) );
        } catch (Exception e) {
            setValue( BigDecimal.ZERO );
        }

        try {
            setValueTree(//
                    blankPbMass.getValueTree().//
                    add( initCommonPbMass.getValueTree() ).//
                    divide( fractionMass.getValueTree() ) );
        } catch (Exception e) {
            setValueTree( ExpTreeII.ZERO );
        }

    }
}
