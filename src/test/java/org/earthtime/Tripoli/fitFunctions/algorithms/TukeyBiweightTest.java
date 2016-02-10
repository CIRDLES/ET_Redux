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
        double tuningConstant = 6.0;
        double[] values = {48866, 48859, 48864, 48908, 48878, 48893, 48873, 48891, 48883, 48873};
        // oracle by ???
        double expResult = 48878.35610118363;
        double result = TukeyBiweight.calculateTukeyBiweightMean(name, tuningConstant, values).getValue().doubleValue();
        assertEquals(expResult, result, 1e-10);
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
