/*
 * MolTh232s.java
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
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public class MolTh232s extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables sept 2009
    private static final long serialVersionUID = 3144132791169315725L;
    private final static String NAME = "molTh232s";
    private final static String UNCT_TYPE = "ABS";
    // Instance variables
    private ValueModel lambda232;
    private ValueModel age206_238r_Th;
    private ValueModel molPb208r;

    /** Creates a new instance of MolTh232s */
    public MolTh232s () {
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

        lambda232 = inputValueModels[0];
        age206_238r_Th = inputValueModels[1];
        molPb208r = inputValueModels[2];

        BigDecimal expLambda232_Age206_238r_Th_Minus1 =
                new BigDecimal(//
                Math.expm1( lambda232.getValue().doubleValue() * age206_238r_Th.getValue().doubleValue() )//
                );

        try {
            setValue(molPb208r.getValue().//
                    divide(expLambda232_Age206_238r_Th_Minus1, ReduxConstants.mathContext15 ) );
        } catch (Exception e) {
            setValue( BigDecimal.ZERO );
        }

        try {
            double dMolTh232s__dMolPb208r =//
                    1.0 / expLambda232_Age206_238r_Th_Minus1.doubleValue();
            parDerivTerms.put( "dMolTh232s__dMolPb208r", new BigDecimal( dMolTh232s__dMolPb208r ) );
        } catch (Exception e) {
            parDerivTerms.put( "dMolTh232s__dMolPb208r", BigDecimal.ZERO );
        }

        try {
            double dMolTh232s__dAge206_238r_Th =//
                    lambda232.getValue().doubleValue() //
                    * molPb208r.getValue().doubleValue() //
                    / (2.0//
                    - 2.0 * Math.cosh( lambda232.getValue().doubleValue() * age206_238r_Th.getValue().doubleValue() ));
            parDerivTerms.put( "dMolTh232s__dAge206_238r_Th", new BigDecimal( dMolTh232s__dAge206_238r_Th ) );
        } catch (Exception e) {
            parDerivTerms.put( "dMolTh232s__dAge206_238r_Th", BigDecimal.ZERO );
        }

        try {
            double dMolTh232s__dLambda232 = //
                    age206_238r_Th.getValue().doubleValue() //
                    * molPb208r.getValue().doubleValue()//
                    / (2.0//
                    - 2.0 * Math.cosh( lambda232.getValue().doubleValue() * age206_238r_Th.getValue().doubleValue() ));
            parDerivTerms.put( "dMolTh232s__dLambda232", new BigDecimal( dMolTh232s__dLambda232 ) );
        } catch (Exception e) {
            parDerivTerms.put( "dMolTh232s__dLambda232", BigDecimal.ZERO );
        }




    }


}
