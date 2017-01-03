/*
 * InitCommonPbMass.java
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
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public class InitCommonPbMass extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables
    private static final long serialVersionUID = 6833440548171883867L;
    private final static String NAME = "initCommonPbMass";
    private final static String UNCT_TYPE = "ABS";
    // Instance variables
    private ValueModel molPb204c;
    private ValueModel gmol204;
    private ValueModel r206_204c;
    private ValueModel gmol206;
    private ValueModel r207_204c;
    private ValueModel gmol207;
    private ValueModel r208_204c;
    private ValueModel gmol208;

    /** Creates a new instance of InitCommonPbMass */
    public InitCommonPbMass () {
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


        molPb204c = inputValueModels[0];
        gmol204 = inputValueModels[1];
        r206_204c = inputValueModels[2];
        gmol206 = inputValueModels[3];
        r207_204c = inputValueModels[4];
        gmol207 = inputValueModels[5];
        r208_204c = inputValueModels[6];
        gmol208 = inputValueModels[7];

        setValue(//
                molPb204c.getValue().
                multiply( gmol204.getValue().
                add( r206_204c.getValue().
                multiply( gmol206.getValue() ) ).
                add( r207_204c.getValue().
                multiply( gmol207.getValue() ) ).
                add( r208_204c.getValue().
                multiply( gmol208.getValue() ) ) ) );

        setValueTree(//
                molPb204c.getValueTree().
                multiply( gmol204.getValueTree().
                add( r206_204c.getValueTree().
                multiply( gmol206.getValueTree() ) ).
                add( r207_204c.getValueTree().
                multiply( gmol207.getValueTree() ) ).
                add( r208_204c.getValueTree().
                multiply( gmol208.getValueTree() ) ) ) );
    }
}
