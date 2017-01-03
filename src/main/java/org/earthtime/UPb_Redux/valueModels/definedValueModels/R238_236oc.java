/*
 * R238_236oc.java
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
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public class R238_236oc extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables
    private static final long serialVersionUID = -3044577602406288650L;
    private final static String NAME = "r238_236oc";
    private final static String UNCT_TYPE = "ABS";
    // Instance variables
    private ValueModel r18O_16O;
    private ValueModel r270_268m;

    /** Creates a new instance of R238_235oc */
    public R238_236oc () {
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

        r18O_16O = inputValueModels[0];
        r270_268m = inputValueModels[1];

        BigDecimal BD2 = new BigDecimal( 2.0 );


        setValue(//
                r270_268m.getValue().//
                subtract( BD2.//
                multiply( r18O_16O.getValue() ) ) );



        BigDecimal dR238_236oc__dR270_268m = BigDecimal.ONE;
        parDerivTerms.put( "dR238_236oc__dR270_268m", dR238_236oc__dR270_268m );

        BigDecimal dR238_236oc__dR18O_16O = BD2.negate();
        parDerivTerms.put( "dR238_236oc__dR18O_16O", dR238_236oc__dR18O_16O );

    }

}
