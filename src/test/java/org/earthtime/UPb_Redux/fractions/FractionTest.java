/*
 * Copyright 2015 CIRDLES.
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
package org.earthtime.UPb_Redux.fractions;

import org.earthtime.dataDictionaries.AnalysisMeasures;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author parizotclement
 */
public class FractionTest {
    
     /**
     * Integration Test of class Fraction
     * Testing the correct name of AnalysisMeasure
     */
    @Test
    public void testName() {
        AnalysisMeasures am = AnalysisMeasures.alphaPb;
        assertEquals(am.getName(), "alphaPb");
    }
    
}
