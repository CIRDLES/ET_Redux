/*
 * MolPb204c.java
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
import org.earthtime.UPb_Redux.expressions.ExpTreeII;
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public class MolPb204c extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables
    private static final long serialVersionUID = 6141356579510320554L;
    private final static String NAME = "molPb204c";
    private final static String UNCT_TYPE = "ABS";
    // Instance variables
    private boolean zircon;
    // not zircon
    private ValueModel molPb204tc;
    private ValueModel molPb204b;

    /** Creates a new instance of MolPb204c */
    public MolPb204c () {
        super( NAME, UNCT_TYPE );
    }

    /**
     * 
     * @param isZircon
     */
    public MolPb204c ( boolean isZircon ) {
        super( NAME, UNCT_TYPE );
        this.zircon = isZircon;
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

        if ( zircon ) {
            setValue( BigDecimal.ZERO );
            setValueTree( ExpTreeII.ZERO );
        } else {
            molPb204tc = inputValueModels[0];
            molPb204b = inputValueModels[1];

            setValue(//
                    molPb204tc.getValue().//
                    subtract( molPb204b.getValue() ) );

            setValueTree(//
                    molPb204tc.getValueTree().//
                    subtract( molPb204b.getValueTree() ) );

            try {
                BigDecimal dMolPb204c__dMolPb204tc = //
                        BigDecimal.ONE;
                parDerivTerms.put( "dMolPb204c__dMolPb204tc", dMolPb204c__dMolPb204tc );
            } catch (Exception e) {
                parDerivTerms.put( "dMolPb204c__dMolPb204tc", BigDecimal.ZERO );
            }
            try {
                BigDecimal dMolPb204c__dMolPb204b = //
                        BigDecimal.ONE.negate();
                parDerivTerms.put( "dMolPb204c__dMolPb204b", dMolPb204c__dMolPb204b );
            } catch (Exception e) {
                parDerivTerms.put( "dMolPb204c__dMolPb204b", BigDecimal.ZERO );
            }
        }
    }

    /**
     * @param zircon the zircon to set
     */
    public void setZircon ( boolean zircon ) {
        this.zircon = zircon;
    }
}
