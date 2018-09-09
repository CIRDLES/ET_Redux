/*
 * R270_268m.java
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
import org.earthtime.dataDictionaries.AnalysisMeasures;

/**
 *
 * @author James F. Bowring
 */
public class R270_268m extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables
    private static final long serialVersionUID = 4416886051233300371L;
    private final static String NAME = AnalysisMeasures.r270_268m.getName();
    private final static String UNCT_TYPE = "ABS";
    // instance variables
    private ValueModel r238_236m;
    private ValueModel r18O_16O;

    /** Creates a new instance of R270_268m */
    public R270_268m () {
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

        r238_236m = inputValueModels[0];
        r18O_16O = inputValueModels[1];

        setValue(//
                r238_236m.getValue().//
                add( new BigDecimal( 2.0 ).//
                multiply( r18O_16O.getValue() ) ) );

    }

    /**
     * 
     * @param tracerUncertaintiesOn
     * @param lambdaUncertaintiesOn
     * @param parDerivTerms
     * @param coVariances
     */

    public void calculateOneSigma (
            boolean tracerUncertaintiesOn, boolean lambdaUncertaintiesOn,
            ConcurrentMap<String, BigDecimal> parDerivTerms, ConcurrentMap<String, BigDecimal> coVariances ) {


        setOneSigma(//
                r238_236m.getOneSigmaAbs() );

        // align this calculated measured ratio with the others
        toggleUncertaintyType();

    }
}
