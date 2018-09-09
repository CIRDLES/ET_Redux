/*
 * R204_205fc.java
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
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public class R204_205fc extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables sept 2009
    private static final long serialVersionUID = 5453666943858979625L;
    private final static String NAME = "r204_205fc";
    private final static String UNCT_TYPE = "ABS";
    // Instance variables
    private ValueModel r204_205m;
    private ValueModel alphaPb;

    /** Creates a new instance of R204_205fc */
    public R204_205fc () {
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


        r204_205m = inputValueModels[0];
        alphaPb = inputValueModels[1];

        setValue(//
                r204_205m.getValue()//
                .multiply( BigDecimal.ONE.subtract( alphaPb.getValue() ) ) );
        try {
            BigDecimal dR204_205fc__dR204_205m = //
                    BigDecimal.ONE.//
                    subtract( alphaPb.getValue() );
            parDerivTerms.put( "dR204_205fc__dR204_205m", dR204_205fc__dR204_205m );
        } catch (Exception e) {
            parDerivTerms.put( "dR204_205fc__dR204_205m", BigDecimal.ZERO );
        }

        try {
            BigDecimal dR204_205fc__dAlphaPb = r204_205m.getValue().negate();
            parDerivTerms.put( "dR204_205fc__dAlphaPb", dR204_205fc__dAlphaPb );
        } catch (Exception e) {
            parDerivTerms.put( "dR204_205fc__dAlphaPb", BigDecimal.ZERO );

        }

    }
}
