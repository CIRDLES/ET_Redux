/*
 * R238_235oc.java
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
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.expressions.ExpTreeII;
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public class R238_235oc extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables sept 2009
    private static final long serialVersionUID = -7480623629302978832L;
    private final static String NAME = "r238_235oc";
    private final static String UNCT_TYPE = "ABS";
    // Instance variables
    private ValueModel r18O_16O;
    private ValueModel r265_267m;
    private ValueModel r270_267m;
    private ValueModel r270_265m;

    /** Creates a new instance of R238_235oc */
    public R238_235oc () {
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

        r18O_16O = inputValueModels[0];
        r265_267m = inputValueModels[1];
        r270_267m = inputValueModels[2];
        r270_265m = inputValueModels[3];

        BigDecimal BD2 = new BigDecimal( 2.0 );

        try {
            setValue(r270_267m.getValue().//
                    divide(BigDecimal.ONE.//
                    subtract( BD2.//
                    multiply( r18O_16O.getValue().//
                    multiply( r265_267m.getValue() ) ) ), ReduxConstants.mathContext15 ) );
        } catch (Exception e) {
            setValue( BigDecimal.ZERO );
        }

        setValueTree(//
                r270_267m.getValueTree().//
                divide( ExpTreeII.ONE.//
                subtract( ExpTreeII.TWO.//
                multiply( r18O_16O.getValueTree().//
                multiply( r265_267m.getValueTree() ) ) ) ) );



        //System.out.println(getValue().toPlainString() + "\n" + getValueTree().treeToString(1));
        if ( getValue().compareTo( getValueTree().getNodeValue() ) != 0 ) {
//            System.out.println( differenceValueCalcs() );
        }







        try {
            BigDecimal dR238_235oc__dR270_267m = BigDecimal.ONE.//
                    divide(BigDecimal.ONE.//
                    subtract( BD2.//
                    multiply( r18O_16O.getValue().//
                    multiply( r265_267m.getValue() ) ) ), ReduxConstants.mathContext15 );
            parDerivTerms.put( "dR238_235oc__dR270_267m", dR238_235oc__dR270_267m );
        } catch (Exception e) {
            parDerivTerms.put( "dR238_235oc__dR270_267m", BigDecimal.ZERO );
        }

        try {
            BigDecimal dR238_235oc__dR265_267m = BD2.//
                    multiply( r18O_16O.getValue().//
                    multiply( r270_267m.getValue() ) ).//

                    divide(BigDecimal.ONE.//
                    subtract( BD2.//
                    multiply( r18O_16O.getValue().//
                    multiply( r265_267m.getValue() ) ) ).pow( 2 ), ReduxConstants.mathContext15 );
            parDerivTerms.put( "dR238_235oc__dR265_267m", dR238_235oc__dR265_267m );
        } catch (Exception e) {
            parDerivTerms.put( "dR238_235oc__dR265_267m", BigDecimal.ZERO );
        }

        try {
            BigDecimal dR238_235oc__dR18O_16O = BD2.//
                    multiply( r265_267m.getValue().//
                    multiply( r270_267m.getValue() ) ).//

                    divide(BigDecimal.ONE.//
                    subtract( BD2.//
                    multiply( r18O_16O.getValue().//
                    multiply( r265_267m.getValue() ) ) ).pow( 2 ), ReduxConstants.mathContext15 );
            parDerivTerms.put( "dR238_235oc__dR18O_16O", dR238_235oc__dR18O_16O );
        } catch (Exception e) {
            parDerivTerms.put( "dR238_235oc__dR18O_16O", BigDecimal.ZERO );
        }
    }


}
