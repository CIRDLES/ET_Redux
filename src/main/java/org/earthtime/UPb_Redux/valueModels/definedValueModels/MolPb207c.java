/*
 * MolPb207c.java
 *
 * Created on Dec 12, 2008
 *
 *
 * Copyright 2007-2008 James F. Bowring and www.Earth-Time.org
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
public class MolPb207c extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables
    private static final long serialVersionUID =  -4052775595703726177L;
    private final static String NAME = "molPb207c";
    private final static String UNCT_TYPE = "ABS";

    // Instance variables
    private boolean zircon;
    private ValueModel r207_204c;
    private ValueModel molPb204c;

    /** Creates a new instance of MolPb207c */
    public MolPb207c() {
        super(NAME, UNCT_TYPE);
    }

    /**
     * 
     * @param isZircon
     */
    public MolPb207c(boolean isZircon) {
        super(NAME, UNCT_TYPE);
        this.zircon = isZircon;
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

        if (zircon) {
            setValue(BigDecimal.ZERO);
        } else {
            r207_204c = inputValueModels[0];
            molPb204c = inputValueModels[1];

            setValue(//
                    r207_204c.getValue().//
                    multiply(molPb204c.getValue()));

            try {
                BigDecimal dMolPb207c__dR207_204c = //
                        molPb204c.getValue();
                parDerivTerms.put("dMolPb207c__dR207_204c", dMolPb207c__dR207_204c);
            } catch (Exception e) {
                parDerivTerms.put("dMolPb207c__dR207_204c", BigDecimal.ZERO);
            }
            try {
                BigDecimal dMolPb207c__dMolPb204c = //
                        r207_204c.getValue();
                parDerivTerms.put("dMolPb207c__dMolPb204c", dMolPb207c__dMolPb204c);
            } catch (Exception e) {
                parDerivTerms.put("dMolPb207c__dMolPb204c", BigDecimal.ZERO);
            }
        }
    }

    /**
     * @param zircon the zircon to set
     */
    public void setZircon(boolean zircon) {
        this.zircon = zircon;
    }
}