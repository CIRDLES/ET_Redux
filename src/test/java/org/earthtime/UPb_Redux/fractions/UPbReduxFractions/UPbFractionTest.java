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
package org.earthtime.UPb_Redux.fractions.UPbReduxFractions;

import java.io.File;
import org.earthtime.UPb_Redux.fractions.FractionI;
import org.earthtime.xmlUtilities.XMLSerializationI;
import org.junit.After;
import org.junit.Test;

/**
 *
 * @author parizotclement
 */
public class UPbFractionTest {
    
     /**
     * Integration Test of class UPbFraction
     * Testing the writing and reading of a file
     * @throws Exception
     */
    @Test
    public void testSerialization() throws Exception {

        FractionI myUPbReduxFraction = new UPbFraction("NONE");

        ((XMLSerializationI) myUPbReduxFraction).serializeXMLObject("UPbFractionTEST.xml");

        myUPbReduxFraction = ((UPbFraction) myUPbReduxFraction).readXMLFraction("UPbFractionTEST.xml", 1, true);

    }
    
     /**
     * Delete the file created previously
     * @throws Exception 
     */
    @After
    public void cleanFiles() throws Exception
    {
        File file = new File("UPbFractionTEST.xml"); //Get the file
        if(!(file.delete())) //delete
        {
            //throw exception in case of error
            throw new Exception("Testing File 'UPbFractionTEST.xml' couldn't be deleted");
        }
    }
    
}
