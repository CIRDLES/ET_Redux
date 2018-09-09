/*
 * RadToCommonPb208.java
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
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public class RadToCommonPb208 extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables
    private static final long serialVersionUID = 6296254431522599533L;
    private final static String NAME = "radToCommonPb208";
    private final static String UNCT_TYPE = "ABS";
    // Instance variables
    private ValueModel molPb208r;
    private ValueModel molPb208b;
    private ValueModel molPb208c;
    private ValueModel molPb208t;

    /** Creates a new instance of RadToCommonPb208 */
    public RadToCommonPb208 () {
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


        molPb208r = inputValueModels[0];
        molPb208b = inputValueModels[1];
        molPb208c = inputValueModels[2];
        molPb208t = inputValueModels[3];

        try {
            setValue(molPb208r.getValue().//
                    divide(molPb208r.getValue().//
                    add( molPb208b.getValue() ).//
                    add( molPb208c.getValue() ).//
                    add( molPb208t.getValue() ), ReduxConstants.mathContext15 ) );
        } catch (Exception e) {
            setValue( BigDecimal.ZERO );
        }
    }

}
