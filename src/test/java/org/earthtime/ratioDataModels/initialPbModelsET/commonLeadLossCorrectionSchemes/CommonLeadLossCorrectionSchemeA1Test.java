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
public class CommonLeadLossCorrectionSchemeA1Test {
    
    /**
     * Integration Test of class CommonLeadLossCorrectionSchemeA1
     * Testing the instantiation
     * @throws Exception
     */
    @Test
    public void testInstantiation() throws Exception {

        CommonLeadLossCorrectionSchemeA1 cllcsA = new CommonLeadLossCorrectionSchemeA1();

        double ageA1 = cllcsA.ageForScheme(null, 137.8185, 14.766, 0.0567, 0.87143);
        assertEquals(4.217015417080999E8, ageA1, 0.0001);

    }
}
   