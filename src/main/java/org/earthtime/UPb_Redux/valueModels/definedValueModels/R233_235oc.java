/*
 * R233_235oc.java
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
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.expressions.ExpTreeII;
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public class R233_235oc extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables
    private static final long serialVersionUID = -5693664025950339545L;
    private final static String NAME = "r233_235oc";
    private final static String UNCT_TYPE = "ABS";
    // instance variables
    private ValueModel r18O_16O;
    private ValueModel r265_267m;

    /** Creates a new instance of R233_235oc */
    public R233_235oc () {
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

        BigDecimal BD2 = new BigDecimal( 2.0 );

        try {
            setValue(r265_267m.getValue().//
                    divide(BigDecimal.ONE.//
                    subtract( BD2.//
                    multiply( r18O_16O.getValue().//
                    multiply( r265_267m.getValue() ) ) ), ReduxConstants.mathContext15 ) );
        } catch (Exception e) {
            setValue( BigDecimal.ZERO );
        }

//******************* experiment
        try {
            setValueTree(//
                    r265_267m.getValueTree().//
                    divide( ExpTreeII.ONE.//
                    subtract( ExpTreeII.TWO.//
                    multiply( r18O_16O.getValueTree()).//
                    multiply( r265_267m.getValueTree()  ) ) ) );
        } catch (Exception e) {
            setValueTree( ExpTreeII.ZERO );
        }

 //       System.out.println(getName() + "  " + getValue().toPlainString() + "\n" + getValueTree().treeToLaTeX(4, false));
        if ( getValue().compareTo( getValueTree().getNodeValue() ) != 0 ) {
//            System.out.println( differenceValueCalcs() );
        }




        try {
            BigDecimal dR233_235oc__dR265_267m = BigDecimal.ONE.//
                    divide(BigDecimal.ONE.//
                    subtract( BD2.//
                    multiply( r18O_16O.getValue().//
                    multiply( r265_267m.getValue() ) ) ).pow( 2 ), ReduxConstants.mathContext15 );
            parDerivTerms.put( "dR233_235oc__dR265_267m", dR233_235oc__dR265_267m );
        } catch (Exception e) {
            parDerivTerms.put( "dR233_235oc__dR265_267m", BigDecimal.ZERO );
        }

        try {
            BigDecimal dR233_235oc__dR18O_16O = BD2.//
                    multiply( r265_267m.getValue().pow( 2 ) ).// this power of 2 added by Noah July 2012 as result of discovering a typo
                    divide(BigDecimal.ONE.//
                    subtract( BD2.//
                    multiply( r18O_16O.getValue().//
                    multiply( r265_267m.getValue() ) ) ).pow( 2 ), ReduxConstants.mathContext15 );
            parDerivTerms.put( "dR233_235oc__dR18O_16O", dR233_235oc__dR18O_16O );
        } catch (Exception e) {
            parDerivTerms.put( "dR233_235oc__dR18O_16O", BigDecimal.ZERO );
        }

    }
}
//2*r265_267m/(2*r18O_16O*r265_267m - 1)^2

//(2*r265_267m^2)/(2*r18O_16O*r265_267m - 1)^2

//  2*r265_267m/ (1 - 2*r18O_16O*r265_267m)^2

// (1-a)^2 = (a-1)^2

