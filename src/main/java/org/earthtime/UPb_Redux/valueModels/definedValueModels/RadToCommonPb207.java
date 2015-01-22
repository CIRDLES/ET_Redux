/*
 * RadToCommonPb207.java
 *
 * Created on Feb 28, 2009
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
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public class RadToCommonPb207 extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {
    // Class variables

    private static final long serialVersionUID = 3558618820550634832L;
    private final static String NAME = "radToCommonPb207";
    private final static String UNCT_TYPE = "ABS";
    // Instance variables
    private ValueModel molPb207r;
    private ValueModel molPb207b;
    private ValueModel molPb207c;
    private ValueModel molPb207t;

    /** Creates a new instance of RadToCommonPb207 */
    public RadToCommonPb207 () {
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


        molPb207r = inputValueModels[0];
        molPb207b = inputValueModels[1];
        molPb207c = inputValueModels[2];
        molPb207t = inputValueModels[3];

        try {
            setValue(molPb207r.getValue().//
                    divide(molPb207r.getValue().//
                    add( molPb207b.getValue() ).//
                    add( molPb207c.getValue() ).//
                    add( molPb207t.getValue() ), ReduxConstants.mathContext15 ) );
        } catch (Exception e) {
            setValue( BigDecimal.ZERO );
        }
    }
}
