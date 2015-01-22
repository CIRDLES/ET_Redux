/*
 * MolU233t.java
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
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public class MolU233t extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables sept 2009
    private static final long serialVersionUID = -2741491470552621222L;
    private final static String NAME = "molU233t";
    private final static String UNCT_TYPE = "ABS";
    // Instance variables
    private boolean doubleSpike;
    private ValueModel r233_235t;
    private ValueModel molU235t;

    /** Creates a new instance of MolU233t */
    public MolU233t () {
        super( NAME, UNCT_TYPE );
        doubleSpike = false;
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

        if ( doubleSpike ) {

            r233_235t = inputValueModels[0];
            molU235t = inputValueModels[1];

            setValue(//
                    r233_235t.getValue()//
                    .multiply( molU235t.getValue() ) );


            setValueTree(//
                    r233_235t.getValueTree()//
                    .multiply( molU235t.getValueTree() ) );


            //System.out.println(getValue().toPlainString() + "\n" + getValueTree().treeToString(1));
            if ( getValue().compareTo( getValueTree().getNodeValue() ) != 0 ) {
//                System.out.println( differenceValueCalcs() );
            }



            try {
                BigDecimal dMolU233t__dR233_235t = //
                        molU235t.getValue();
                parDerivTerms.put( "dMolU233t__dR233_235t", dMolU233t__dR233_235t );
            } catch (Exception e) {
                parDerivTerms.put( "dMolU233t__dR233_235t", BigDecimal.ZERO );
            }

            try {
                BigDecimal dMolU233t__dMolU235t = //
                        r233_235t.getValue();
                parDerivTerms.put( "dMolU233t__dMolU235t", dMolU233t__dMolU235t );
            } catch (Exception e) {
                parDerivTerms.put( "dMolU233t__dMolU235t", BigDecimal.ZERO );
            }
        } else {
            setValue( BigDecimal.ZERO );
        }

    }

    /**
     * @param zircon 
     */
    public void setDoubleSpike ( boolean zircon ) {
        this.doubleSpike = zircon;
    }
}
