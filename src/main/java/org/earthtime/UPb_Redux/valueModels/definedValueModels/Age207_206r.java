/*
 * Age207_206r.java
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
import org.earthtime.dataDictionaries.RadDates;

/**
 *
 * @author James F. Bowring
 */
public class Age207_206r extends ValueModel implements
        Comparable<ValueModel>,
        Serializable {

    // Class variables
    private static final long serialVersionUID = 213273456373713978L;
    private final static String NAME = RadDates.age207_206r.getName();
    private final static String UNCT_TYPE = "ABS";
    // Instance variables
    private ValueModel r238_235s;
    private ValueModel r207_206r;
    private ValueModel age206_238r;
    private ValueModel lambda235;
    private ValueModel lambda238;

    /**
     * Creates a new instance of Age207_206r
     */
    public Age207_206r() {
        super(NAME, UNCT_TYPE);
    }

    /**
     *
     * @param lambda235
     * @param lambda238
     */
    public Age207_206r(ValueModel lambda235, ValueModel lambda238) {
        super(NAME, UNCT_TYPE);
        this.lambda235 = lambda235;
        this.lambda238 = lambda238;
    }

    /**
     *
     * @param r238_235sDouble
     * @param r207_206rDouble
     * @param startDate
     * @return
     */
    public double calculateDate(
            double r238_235sDouble,
            double r207_206rDouble,
            double startDate) {
        double xn = startDate;

        if (xn <= 0.0) {
            // xn = 5.0e8;
            // Noah correction sept 2013
            // xn = 10^9 * (4.5695 - 5.3011*exp(-5.4731*r207_206r) )
            xn = 10.0e9 * (4.5695 - 5.3011 * (Math.exp(-5.4731 * r207_206rDouble)));
        }

        for (int i = 0; i
                < 35; i++) {

            double expLambda238xnMinus1 = Math.expm1(lambda238.getValue().doubleValue() * xn);
            double expLambda235xnMinus1 = Math.expm1(lambda235.getValue().doubleValue() * xn);

            double new10
                    = (expLambda235xnMinus1
                    / expLambda238xnMinus1
                    / r238_235sDouble)
                    - r207_206rDouble;

            double new11
                    = ((r238_235sDouble
                    * expLambda238xnMinus1
                    * lambda235.getValue().doubleValue()
                    * (1.0 + expLambda235xnMinus1))
                    - (expLambda235xnMinus1
                    * r238_235sDouble
                    * lambda238.getValue().doubleValue()
                    * (1.0 + expLambda238xnMinus1)))
                    / r238_235sDouble
                    / r238_235sDouble
                    / expLambda238xnMinus1
                    / expLambda238xnMinus1;

            xn -= (new10 / new11);
        }
        return xn;
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

        r238_235s = inputValueModels[0];
        r207_206r = inputValueModels[1];
        age206_238r = inputValueModels[2];
        lambda235 = inputValueModels[3];
        lambda238 = inputValueModels[4];

        double r238_235sDouble = r238_235s.getValue().doubleValue();
        double xn = age206_238r.getValue().doubleValue();

        try {
            setValue(new BigDecimal(calculateDate(//
                    r238_235sDouble, r207_206r.getValue().doubleValue(), xn), ReduxConstants.mathContext15));
        } catch (Exception e) {
            setValue(BigDecimal.ZERO);
        }

        try {
            setValueTree(new ExpTreeII(calculateDate(//
                    r238_235sDouble, r207_206r.getValue().doubleValue(), xn)));
        } catch (Exception e) {
            setValueTree(ExpTreeII.ZERO);
        }

        BigDecimal BDexpLambda238xnMinus1 = //
                new BigDecimal(//
                        //
                        //
                        //
                        Math.expm1(lambda238.getValue().doubleValue()//
                                * getValue().doubleValue()), ReduxConstants.mathContext15);

        BigDecimal BDexpLambda235xnMinus1 = //
                new BigDecimal(//
                        //
                        //
                        //
                        Math.expm1(lambda235.getValue().doubleValue()//
                                * getValue().doubleValue()), ReduxConstants.mathContext15);

        BigDecimal denominator = //
                lambda235.getValue().//
                multiply(BigDecimal.ONE.//
                        add(BDexpLambda235xnMinus1)).//

                subtract(r238_235s.getValue().//
                        multiply(r207_206r.getValue()).//
                        multiply(lambda238.getValue()).//
                        multiply(BigDecimal.ONE.//
                                add(BDexpLambda238xnMinus1)));

        if (parDerivTerms != null) {
            // oct 2014 to handle common lead
            String partialDerivativeNameAge__Ratio = "dA" + name.substring(1) + "__dR" + r207_206r.getName().substring(1);//dAge207_206r__dR207_206r
            try {
                BigDecimal dAge207_206r__dR207_206r = r238_235s.getValue().//
                        multiply(BDexpLambda238xnMinus1).//
                        divide(denominator, ReduxConstants.mathContext15);
                parDerivTerms.put(partialDerivativeNameAge__Ratio, dAge207_206r__dR207_206r);
            } catch (Exception e) {
                parDerivTerms.put(partialDerivativeNameAge__Ratio, BigDecimal.ZERO);
            }

            String partialDerivativeNameAge__Lambda = "dA" + name.substring(1) + "__dLambda235";//dAge207_206r__dLambda235
            try {
                BigDecimal dAge207_206r__dLambda235 =getValue().//
                        multiply(BigDecimal.ONE.//
                                add(BDexpLambda235xnMinus1)).//
                        divide(denominator, ReduxConstants.mathContext15).//
                        negate(); // negated July 2012 by Noah 
                parDerivTerms.put(partialDerivativeNameAge__Lambda, dAge207_206r__dLambda235);
            } catch (Exception e) {
                parDerivTerms.put(partialDerivativeNameAge__Lambda, BigDecimal.ZERO);
            }

            partialDerivativeNameAge__Lambda = "dA" + name.substring(1) + "__dLambda238";//dAge207_206r__dLambda238
            try {
                BigDecimal dAge207_206r__dLambda238 = r238_235s.getValue().//
                        multiply(r207_206r.getValue()).//
                        multiply(getValue()).//
                        multiply(BigDecimal.ONE.//
                                add(BDexpLambda238xnMinus1)).//
                        divide(denominator, ReduxConstants.mathContext15);
                parDerivTerms.put(partialDerivativeNameAge__Lambda, dAge207_206r__dLambda238);
            } catch (Exception e) {
                parDerivTerms.put(partialDerivativeNameAge__Lambda, BigDecimal.ZERO);
            }

            partialDerivativeNameAge__Ratio = "dA" + name.substring(1) + "__dR238_235s";//dAge207_206r__dR238_235s
            try {
                BigDecimal dAge207_206r__dR238_235s = r207_206r.getValue().//
                        multiply(BDexpLambda238xnMinus1).//
                        divide(denominator, ReduxConstants.mathContext15);
                parDerivTerms.put(partialDerivativeNameAge__Ratio, dAge207_206r__dR238_235s);
            } catch (Exception e) {
                parDerivTerms.put(partialDerivativeNameAge__Ratio, BigDecimal.ZERO);
            }

        }
    }
}
