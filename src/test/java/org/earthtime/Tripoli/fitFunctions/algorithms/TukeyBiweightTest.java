/*
 * Copyright 2006-2016 CIRDLES.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.earthtime.Tripoli.fitFunctions.algorithms;

import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public class TukeyBiweightTest {

    public TukeyBiweightTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of calculateTukeyBiweightMean method, of class TukeyBiweight.
     */
    @Test
    public void testCalculateTukeyBiweightMean() {
        System.out.println("calculateTukeyBiweightMean");
        String name = "";
        double tuningConstant = 9.0;
        double[] values = {2494, 2524, 2455, 2427, 2396, 2545, 2483, 2436, 2548, 2619};
        // oracle by Squid
        double expValue = 24925.1139904333;
        double expSigma = 206.312497307535;
        ValueModel result = TukeyBiweight.calculateTukeyBiweightMean(name, tuningConstant, values);
        double value = result.getValue().doubleValue();
        double sigma = result.getOneSigmaAbs().doubleValue();
////        assertEquals(expValue, value, 1e-10);
////        assertEquals(expSigma, sigma, 1e-10);

        tuningConstant = 9.0;
        values = new double[]{0.302198828429556,
            0.300788957475996,
            0.297713166278977,
            0.297778760994429,
            0.297483827242158};
        // oracle by Simon Bodokos by hand
        expValue = 0.297659637730707;
        expSigma = 0.000166784902889577;
        result = TukeyBiweight.calculateTukeyBiweightMean(name, tuningConstant, values);
        value = result.getValue().doubleValue();
        sigma = result.getOneSigmaAbs().doubleValue();
        assertEquals(expValue, value, 1e-10);
        assertEquals(expSigma, sigma, 1e-10);

    }

    /**
     * Test of calculateMedian method, of class TukeyBiweight.
     */
    @Test
    public void testCalculateMedian() {
        System.out.println("calculateMedian");
        double[] values = {6, 5, 4, 3, 2, 1};
        // oracle by inspection
        double expResult = 3.5;
        double result = TukeyBiweight.calculateMedian(values);
        assertEquals(expResult, result, 0.0);

        values = new double[]{5, 4, 3, 2, 1};
        expResult = 3.0;
        result = TukeyBiweight.calculateMedian(values);
        assertEquals(expResult, result, 0.0);
    }

}
