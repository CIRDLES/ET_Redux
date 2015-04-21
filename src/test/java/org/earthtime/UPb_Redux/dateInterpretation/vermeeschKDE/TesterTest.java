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
package org.earthtime.UPb_Redux.dateInterpretation.vermeeschKDE;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author parizotclement
 */
public class TesterTest {
    
     /**
     * Integration Test of class Tester
     * @throws java.lang.Exception
     */   
    @Test
    public void testSample() throws Exception
    {
        Tester tester = new Tester();

        // 4096 = 2^12
        double[] timescale = new double[4096];
        for (int i = 0; i < timescale.length; i ++) {
            timescale[i] = (double) i;
        }

        // using Pieter's sample data
        double[] pdf = null;
        
        pdf = tester.getKDE( "UPbdata.csv", timescale );
    }
    
}
