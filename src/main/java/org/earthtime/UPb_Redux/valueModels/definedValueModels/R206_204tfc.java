/*
 * R206_204tfc.java
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
public class R206_204tfc extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables
    private static final long serialVersionUID = -1711566948889949270L;
    private final static String NAME = "r206_204tfc";
    private final static String UNCT_TYPE = "ABS";
    // Instance variables
    private ValueModel molPb205t;
    private ValueModel r206_205m;
    private ValueModel alphaPb;
    private ValueModel r206_205t;
    private ValueModel molPb204c;
    private ValueModel molPb204b;

    /** Creates a new instance of R206_204tfc */
    public R206_204tfc () {
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

        molPb205t = inputValueModels[0];
        r206_205m = inputValueModels[1];
        alphaPb = inputValueModels[2];
        r206_205t = inputValueModels[3];
        molPb204c = inputValueModels[4];
        molPb204b = inputValueModels[5];

        try {
            setValue(molPb205t.getValue().//
                    multiply( r206_205m.getValue().//
                    multiply( BigDecimal.ONE.//
                    add( alphaPb.getValue() ) ).//
                    subtract( r206_205t.getValue() ) ).//
                    divide(molPb204c.getValue().//
                    add( molPb204b.getValue() ), ReduxConstants.mathContext15 ) );
        } catch (Exception e) {
            setValue( BigDecimal.ZERO );
        }
    }
}
