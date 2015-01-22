/*
 * MolPb208c.java
 *
 * Created on Dec 12, 2008
 *
 *
 * Copyright 2008-2008 James F. Bowring and www.Earth-Time.org
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
public class MolPb208c extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables
    private static final long serialVersionUID =  9174694927333759979L;
    private final static String NAME = "molPb208c";
    private final static String UNCT_TYPE = "ABS";

    // Instance variables
    private boolean zircon;
    private ValueModel r208_204c;
    private ValueModel molPb204c;

    /** Creates a new instance of MolPb208c */
    public MolPb208c() {
        super(NAME, UNCT_TYPE);
    }

    /**
     * 
     * @param isZircon
     */
    public MolPb208c(boolean isZircon) {
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
            r208_204c = inputValueModels[0];
            molPb204c = inputValueModels[1];

            setValue(//
                    r208_204c.getValue().//
                    multiply(molPb204c.getValue()));

            try {
                BigDecimal dMolPb208c__dR208_204c = //
                        molPb204c.getValue();
                parDerivTerms.put("dMolPb208c__dR208_204c", dMolPb208c__dR208_204c);
            } catch (Exception e) {
                parDerivTerms.put("dMolPb208c__dR208_204c", BigDecimal.ZERO);
            }
            try {
                BigDecimal dMolPb208c__dMolPb204c = //
                        r208_204c.getValue();
                parDerivTerms.put("dMolPb208c__dMolPb204c", dMolPb208c__dMolPb204c);
            } catch (Exception e) {
                parDerivTerms.put("dMolPb208c__dMolPb204c", BigDecimal.ZERO);
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