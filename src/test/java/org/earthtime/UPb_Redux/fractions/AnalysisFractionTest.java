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
import java.io.File;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFraction;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.fractionReduction.UPbFractionReducer;
import org.earthtime.UPb_Redux.user.UPbReduxConfigurator;
import org.earthtime.xmlUtilities.XMLSerializationI;
import org.junit.After;
import org.junit.Test;

/**
 *
 * @author parizotclement
 */
public class AnalysisFractionTest {
    
     /**
     * Integration Test of class AnalysisFraction
     * 
     * @throws java.lang.Exception
     */
    @Test
    public void testSerialization () throws Exception {
        UPbReduxConfigurator myConfigurator = new UPbReduxConfigurator();

        FractionI analysisFraction = new UPbFraction( "NONE" );
        // new AnalysisFraction("Test Sample");

        UPbFractionReducer.getInstance().fullFractionReduce((UPbFraction) analysisFraction, true);

        FractionI myAnalysisFraction = new AnalysisFraction( analysisFraction, false );

        String testFractionName = "AnalysisFractionTEST.xml";

        ((XMLSerializationI) myAnalysisFraction).serializeXMLObject( testFractionName );
        ((XMLSerializationI) myAnalysisFraction).readXMLObject( testFractionName, true );

    }
    
    /**
     * Delete the file created previously
     * @throws Exception 
     */
    @After
    public void cleanFiles() throws Exception
    {
        
        File file = new File("AnalysisFractionTEST.xml"); //Get the file
        if(!(file.delete())) //delete
        {
            //throw exception in case of error
            throw new Exception("Testing File 'AnalysisFractionTEST.xml' couldn't be deleted");
        }
    } 
    
}
