/*
 * RTh_Usample.java
 *
 * Created on Feb 28, 2009
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
public class RTh_Usample extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables sept 2009
    private static final long serialVersionUID = 5660034405368362854L;
    private final static String NAME = "rTh_Usample";
    private final static String UNCT_TYPE = "ABS";
    // Instance variables
    private ValueModel molTh232s;
    private ValueModel molU238s;
    private ValueModel molU235s;

    /** Creates a new instance of RTh_Usample */
    public RTh_Usample () {
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

        molTh232s = inputValueModels[0];
        molU238s = inputValueModels[1];
        molU235s = inputValueModels[2];

        try {
            setValue(molTh232s.getValue().//
                    divide(molU238s.getValue()//
                    .add( molU235s.getValue() ), ReduxConstants.mathContext15 ) );
        } catch (Exception e) {
            setValue( BigDecimal.ZERO );
        }

    }

}
