/*
 * RhoR207_206s__r204_206s.java
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
public class RhoR207_206s__r204_206s extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables
    private static final long serialVersionUID = -3192101318183290093L;
    private final static String NAME = "rhoR207_206s__r204_206s";
    private final static String UNCT_TYPE = ValueModel.DEFAULT_UNCERTAINTY_TYPE;

    /** Creates a new instance of rhoR207_206s__r204_206s */
    public RhoR207_206s__r204_206s() {
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
            setValue(BigDecimal.ZERO);
    }

}