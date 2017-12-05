/*
 * PercentDiscordance.java
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
import org.earthtime.dataDictionaries.RadDates;

/**
 *
 * @author James F. Bowring
 */
public class PercentDiscordance extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables
    private static final long serialVersionUID = -7926106667313373709L;
    private final static String NAME = RadDates.percentDiscordance.getName();
    private final static String UNCT_TYPE = ValueModel.DEFAULT_UNCERTAINTY_TYPE;
    // Instance variables
    private ValueModel age206_238r;
    private ValueModel age207_206r;

    /** Creates a new instance of PercentDiscordance */
    public PercentDiscordance() {
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


        age206_238r = inputValueModels[0];
        age207_206r = inputValueModels[1];

        try {
            setValue(new BigDecimal("100.0"). //
                    multiply(BigDecimal.ONE.//
                    subtract(age206_238r.getValue(). //
                    divide(age207_206r.getValue(), ReduxConstants.mathContext15))));
        } catch (Exception e) {
            setValue(BigDecimal.ZERO);
        }
    }
}
