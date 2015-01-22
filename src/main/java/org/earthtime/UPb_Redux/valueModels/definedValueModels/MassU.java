/*
 * MassU.java
 *
 * Created on Oct 26, 2009
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
import org.earthtime.UPb_Redux.expressions.ExpTreeII;
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public class MassU extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables 
    private static final long serialVersionUID = 91578352426137348L;
    private final static String NAME = "massU";
    private final static String UNCT_TYPE = "ABS";
    // Instance variables
    private ValueModel molU235s;
    private ValueModel gmol235;
    private ValueModel molU238s;
    private ValueModel gmol238;

    /** Creates a new instance of MassU */
    public MassU () {
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

        molU235s = inputValueModels[0];
        gmol235 = inputValueModels[1];
        molU238s = inputValueModels[2];
        gmol238 = inputValueModels[3];

        try {
            setValue(//
                    molU235s.getValue().
                    multiply( gmol235.getValue() )//
                    .add( molU238s.getValue().
                    multiply( gmol238.getValue() ) ) );
        } catch (Exception e) {
            setValue( BigDecimal.ZERO );
        }

        try {
            setValueTree(//
                    molU235s.getValueTree().
                    multiply( gmol235.getValueTree() )//
                    .add( molU238s.getValueTree().
                    multiply( gmol238.getValueTree() ) ) );
        } catch (Exception e) {
            setValueTree( ExpTreeII.ZERO );
        }

    }
}
