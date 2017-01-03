/*
 * MolPb207s.java
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
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public class MolPb207s extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables
    private static final long serialVersionUID = -1251977222090854341L;
    private final static String NAME = "molPb207s";
    private final static String UNCT_TYPE = "ABS";
    // Instance variables
    private ValueModel r207_205m;
    private ValueModel r207_205t;
    private ValueModel molPb205t;
    private ValueModel alphaPb;
    private ValueModel molPb207b;

    /** Creates a new instance of MolPb207s */
    public MolPb207s () {
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

        r207_205m = inputValueModels[0];
        r207_205t = inputValueModels[1];
        molPb205t = inputValueModels[2];
        alphaPb = inputValueModels[3];
        molPb207b = inputValueModels[4];

        try {
            setValue(//
                    molPb205t.getValue().
                    multiply( r207_205m.getValue().
                    multiply( alphaPb.getValue().multiply( new BigDecimal( "2.0" ) ).add( BigDecimal.ONE ) ).
                    subtract( r207_205t.getValue() ) ).
                    subtract( molPb207b.getValue() ) );
        } catch (Exception e) {
        }
    }

}
