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
package org.earthtime.ratioDataModels.initialPbModelsET.commonLeadLossCorrectionSchemes;

import static org.junit.Assert.assertEquals;
import org.junit.Test;


/**
 *
 * @author parizotclement
 */
public class CommonLeadLossCorrectionSchemeA2Test {
    
    /**
     * Integration Test of class CommonLeadLossCorrectionSchemeA2
     * Testing the instantiation
     * @throws Exception
     */
    @Test
    public void testInstantiation() throws Exception {

        CommonLeadLossCorrectionSchemeA2 cllcsA = new CommonLeadLossCorrectionSchemeA2();

        double ageA2 = cllcsA.ageForScheme(null, 137.818, 5, 0.2);
        assertEquals(ageA2, 1009.9831897808921, 0.001);

        double ageA3 = cllcsA.ageForScheme(null, 137.818, 1.2, 0.402);
        assertEquals(ageA3, 4011.1153641436326, 0.001);
    }
    
}
