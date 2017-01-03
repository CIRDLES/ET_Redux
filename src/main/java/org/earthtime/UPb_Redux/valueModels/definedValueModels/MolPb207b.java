/*
 * MolPb207b.java
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
public class MolPb207b extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables
    private static final long serialVersionUID =  7401402819987647589L;
    private final static String NAME = "molPb207b";
    private final static String UNCT_TYPE = "ABS";

    // Instance variables
    private boolean zircon;
    // zircon
    private ValueModel r204_205fc;
    private ValueModel r204_205t;
    private ValueModel molPb205t;
    // not zircon
    private ValueModel labPbBlankMass;
    private ValueModel blankPbGramsMol;
    private ValueModel r207_204b;
    private ValueModel molPb204b;

    /** Creates a new instance of MolPb207b */
    public MolPb207b() {
        super(NAME, UNCT_TYPE);
    }

    /**
     * 
     * @param isZircon
     */
    public MolPb207b(boolean isZircon) {
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
            r204_205fc = inputValueModels[0];
            r204_205t = inputValueModels[1];
            molPb205t = inputValueModels[2];
            r207_204b = inputValueModels[3];

            try {
                setValue(//
                        r204_205fc.getValue().//
                        subtract( r204_205t.getValue() )//
                        .multiply( molPb205t.getValue() )//
                        .multiply( r207_204b.getValue() ) );
            } catch (Exception e) {
            }
            try {
                BigDecimal dMolPb207b__dR207_204b = //
                        molPb205t.getValue().//
                        multiply(r204_205fc.getValue().//
                        subtract(r204_205t.getValue()));
                parDerivTerms.put("dMolPb207b__dR207_204b", dMolPb207b__dR207_204b);
            } catch (Exception e) {
                parDerivTerms.put("dMolPb207b__dR207_204b", BigDecimal.ZERO);
            }
            try {
                BigDecimal dMolPb207b__dMolPb205t = //
                        r207_204b.getValue().//
                        multiply(r204_205fc.getValue().//
                        subtract(r204_205t.getValue()));
                parDerivTerms.put("dMolPb207b__dMolPb205t", dMolPb207b__dMolPb205t);
            } catch (Exception e) {
                parDerivTerms.put("dMolPb207b__dMolPb205t", BigDecimal.ZERO);
            }
            try {
                BigDecimal dMolPb207b__dR204_205fc = //
                        r207_204b.getValue().//
                        multiply(molPb205t.getValue());
                parDerivTerms.put("dMolPb207b__dR204_205fc", dMolPb207b__dR204_205fc);
            } catch (Exception e) {
                parDerivTerms.put("dMolPb207b__dR204_205fc", BigDecimal.ZERO);
            }
            try {
                BigDecimal dMolPb207b__dR204_205t = //
                        r207_204b.getValue().negate().//
                        multiply(molPb205t.getValue());
                parDerivTerms.put("dMolPb207b__dR204_205t", dMolPb207b__dR204_205t);
            } catch (Exception e) {
                parDerivTerms.put("dMolPb207b__dR204_205t", BigDecimal.ZERO);
            }

        } else {
            labPbBlankMass = inputValueModels[0];
            blankPbGramsMol = inputValueModels[1];
            r207_204b = inputValueModels[2];
            molPb204b = inputValueModels[3];

            setValue(//
                    new BigDecimal(//
                    labPbBlankMass.getValue().doubleValue() /
                    blankPbGramsMol.getValue().doubleValue())//
                    .multiply(r207_204b.getValue()));

            try {
                BigDecimal dMolPb207b__dR207_204b = //
                        molPb204b.getValue();
                parDerivTerms.put("dMolPb207b__dR207_204b", dMolPb207b__dR207_204b);
            } catch (Exception e) {
                parDerivTerms.put("dMolPb207b__dR207_204b", BigDecimal.ZERO);
            }
            try {
                BigDecimal dMolPb207b__dMolPb204b = //
                        r207_204b.getValue();
                parDerivTerms.put("dMolPb207b__dMolPb204b", dMolPb207b__dMolPb204b);
            } catch (Exception e) {
                parDerivTerms.put("dMolPb207b__dMolPb204b", BigDecimal.ZERO);
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