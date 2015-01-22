/*
 * MassPb_rib.java
 *
 * Created on Oct 25, 2009
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
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.expressions.ExpTreeII;
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public class MassPb_rib extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables
    private static final long serialVersionUID = 8221674464600679567L;
    private final static String NAME = "massPb_rib";
    private final static String UNCT_TYPE = "ABS";
    // Instance variables
    private ValueModel totRadiogenicPbMass;
    private ValueModel totCommonPbMass;

    /** Creates a new instance of MassPb_rib */
    public MassPb_rib () {
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

        totRadiogenicPbMass = inputValueModels[0];
        totCommonPbMass = inputValueModels[1];

        try {
            setValue(//
                    totRadiogenicPbMass.getValue().//
                    add( totCommonPbMass.getValue() ) );
        } catch (Exception e) {
            setValue( BigDecimal.ZERO );
        }

        try {
            setValueTree(//
                    totRadiogenicPbMass.getValueTree().//
                    add( totCommonPbMass.getValueTree() ) );
        } catch (Exception e) {
            setValueTree( ExpTreeII.ZERO );
        }
    }

}
