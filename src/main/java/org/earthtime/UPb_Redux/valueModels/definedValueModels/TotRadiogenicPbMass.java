/*
 * TotRadiogenicPbMass.java
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
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public class TotRadiogenicPbMass extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables
    private static final long serialVersionUID =  2572538780713143024L;
    private final static String NAME = "totRadiogenicPbMass";
    private final static String UNCT_TYPE = "ABS";

    // Instance variables
    private ValueModel molPb206r;
    private ValueModel gmol206;
    private ValueModel molPb207r;
    private ValueModel gmol207;
    private ValueModel molPb208r;
    private ValueModel gmol208;

    /** Creates a new instance of TotRadiogenicPbMass */
    public TotRadiogenicPbMass() {
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


        molPb206r = inputValueModels[0];
        gmol206 = inputValueModels[1];
        molPb207r = inputValueModels[2];
        gmol207 = inputValueModels[3];
        molPb208r = inputValueModels[4];
        gmol208 = inputValueModels[5];

        setValue(//
                molPb206r.getValue().multiply(gmol206.getValue())//
                .add(molPb207r.getValue().multiply(gmol207.getValue()))//
                .add(molPb208r.getValue().multiply(gmol208.getValue())));
    }

}
