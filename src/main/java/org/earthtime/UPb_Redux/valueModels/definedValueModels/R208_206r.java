/*
 * R208_206r.java
 *
 * Created on Dec 12, 2008
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
public class R208_206r extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables
    private static final long serialVersionUID = -3043625002823560L;
    private final static String NAME = "r208_206r";
    private final static String UNCT_TYPE = "ABS";
    // Instance variables
    private ValueModel molPb208r;
    private ValueModel molPb206r;

    /** Creates a new instance of R208_206r */
    public R208_206r () {
        super( NAME, UNCT_TYPE );
    }

    /**
     * 
     * @param inputValueModels  an array of <code>ValueModel</code> in this order:
     * <ol>
     *  <code>
     *      <li>molPb208r
     *      <li>molPb206r
     *  </code>
     *  </ol>
     * @param parDerivTerms
     * @see ValueModel
     * @see MolPb208r
     * @see MolPb206r
     */
    @Override
    public void calculateValue (
            ValueModel[] inputValueModels,
            ConcurrentMap<String, BigDecimal> parDerivTerms ) {

        molPb208r = inputValueModels[0];
        molPb206r = inputValueModels[1];

        try {
            setValue(molPb208r.getValue().//
                    divide(molPb206r.getValue(), ReduxConstants.mathContext15 ) );
        } catch (Exception e) {
            setValue( BigDecimal.ZERO );
        }
    }
}
