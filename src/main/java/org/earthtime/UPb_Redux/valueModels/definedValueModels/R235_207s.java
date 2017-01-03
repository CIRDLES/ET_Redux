/*
 * R235_207s.java
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
public class R235_207s extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables
    private static final long serialVersionUID = 716581412139328788L;
    private final static String NAME = "r235_207s";
    private final static String UNCT_TYPE = "ABS";
    // Instance variables
    private ValueModel molU235s;
    private ValueModel molPb207s;

    /** Creates a new instance of r235_207s */
    public R235_207s() {
        super(NAME, UNCT_TYPE);
    }

    /**
     * 
     * @param inputValueModels
     * @param parDerivTerms
     */
    @Override
    public void calculateValue(
            ValueModel[] inputValueModels,
            ConcurrentMap<String, BigDecimal> parDerivTerms) {

        molU235s = inputValueModels[0];
        molPb207s = inputValueModels[1];

        try {
            setValue(molU235s.getValue().//
                    divide(molPb207s.getValue(), ReduxConstants.mathContext15));
        } catch (Exception e) {
            setValue(BigDecimal.ZERO);
        }

    }
}