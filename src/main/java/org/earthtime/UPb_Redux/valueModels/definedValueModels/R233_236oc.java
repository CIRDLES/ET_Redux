/*
 * R233_236oc.java
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
public class R233_236oc extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables
    private static final long serialVersionUID = 7628849825114142798L;
    private final static String NAME = "r233_236oc";
    private final static String UNCT_TYPE = "ABS";
    // instance variables
    private ValueModel r265_268m;

    /** Creates a new instance of R233_235oc */
    public R233_236oc () {
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

        r265_268m = inputValueModels[0];

        BigDecimal BD2 = new BigDecimal( 2.0 );

        setValue(//
                r265_268m.getValue() );


        BigDecimal dR233_236oc__dR265_268m = BigDecimal.ONE;
        parDerivTerms.put( "dR233_236oc__dR265_268m", dR233_236oc__dR265_268m );

    }

}
