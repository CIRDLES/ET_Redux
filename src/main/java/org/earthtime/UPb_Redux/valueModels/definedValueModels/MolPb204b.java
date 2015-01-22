/*
 * MolPb204b.java
 *
 * Created on Dec 12, 2008
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
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.expressions.ExpTreeII;
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public class MolPb204b extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables sept 2009
    private static final long serialVersionUID = 1409347833093993331L;
    private final static String NAME = "molPb204b";
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

    /** Creates a new instance of MolPb204b */
    public MolPb204b () {
        super( NAME, UNCT_TYPE );
        zircon = false;
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
            r204_205fc = inputValueModels[0];
            r204_205t = inputValueModels[1];
            molPb205t = inputValueModels[2];

            try {
                setValue(//
                        r204_205fc.getValue().subtract( r204_205t.getValue() )//
                        .multiply( molPb205t.getValue() )//
                        );
            } catch (Exception e) {
                setValue( BigDecimal.ZERO);
            }

            try {
                setValueTree(//
                        r204_205fc.getValueTree().subtract( r204_205t.getValueTree() )//
                        .multiply( molPb205t.getValueTree() )//
                        );
            } catch (Exception e) {
                setValueTree( ExpTreeII.ZERO );
            }

            try {
                BigDecimal dMolPb204b__dR204_205fc = //
                        molPb205t.getValue();
                parDerivTerms.put( "dMolPb204b__dR204_205fc", dMolPb204b__dR204_205fc );
            } catch (Exception e) {
                parDerivTerms.put( "dMolPb204b__dR204_205fc", BigDecimal.ZERO );
            }
            try {
                BigDecimal dMolPb204b__dR204_205t = //
                        molPb205t.getValue().negate();// negate added July 2012 by Noah
                parDerivTerms.put( "dMolPb204b__dR204_205t", dMolPb204b__dR204_205t );
            } catch (Exception e) {
                parDerivTerms.put( "dMolPb204b__dR204_205t", BigDecimal.ZERO );
            }
            try {
                BigDecimal dMolPb204b__dMolPb205t = //
                        r204_205fc.getValue().//
                        subtract( r204_205t.getValue() );
                parDerivTerms.put( "dMolPb204b__dMolPb205t", dMolPb204b__dMolPb205t );
            } catch (Exception e) {
                parDerivTerms.put( "dMolPb204b__dMolPb205t", BigDecimal.ZERO );
            }

        } else {
            labPbBlankMass = inputValueModels[0];
            blankPbGramsMol = inputValueModels[1];

            // June 2011 updated from double conversion
            setValue(labPbBlankMass.getValue().//
                    divide(blankPbGramsMol.getValue(), ReduxConstants.mathContext15 ) );

            setValueTree(//
                    labPbBlankMass.getValueTree().//
                    divide( blankPbGramsMol.getValueTree()) );

            try {
                BigDecimal dMolPb204b__dPbBlankMassInGrams = BigDecimal.ONE.//
                        divide(blankPbGramsMol.getValue(), ReduxConstants.mathContext15 );
                parDerivTerms.put( "dMolPb204b__dPbBlankMassInGrams", dMolPb204b__dPbBlankMassInGrams );
            } catch (Exception e) {
                parDerivTerms.put( "dMolPb204b__dPbBlankMassInGrams", BigDecimal.ZERO );
            }

            try {
                BigDecimal dMolPb204b__dBlankPbGramsMol = labPbBlankMass.getValue().//
                        divide(blankPbGramsMol.getValue().pow( 2 ), ReduxConstants.mathContext15 ).//
                        negate();
                parDerivTerms.put( "dMolPb204b__dBlankPbGramsMol", dMolPb204b__dBlankPbGramsMol );
            } catch (Exception e) {
                parDerivTerms.put( "dMolPb204b__dBlankPbGramsMol", BigDecimal.ZERO );
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
