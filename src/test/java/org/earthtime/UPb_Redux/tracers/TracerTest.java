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
package org.earthtime.UPb_Redux.tracers;

import java.io.File;
import static org.earthtime.UPb_Redux.tracers.Tracer.getListOfEarthTimeTracers;
import org.earthtime.UPb_Redux.user.UPbReduxConfigurator;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author parizotclement
 */
public class TracerTest {
    
    /**
     * Integration Test of class Tracer
     * Testing the writing and reading of a file
     * @throws Exception
     */
    @Test
    public void testSerialization() throws Exception {

        Tracer tracer =
                new Tracer( "Test Tracer" );
        String testFileName = "TracerTEST.xml";

        tracer.serializeXMLObject( testFileName );
        tracer.readXMLObject( testFileName, true );

        String[] test = getListOfEarthTimeTracers();

        assertEquals("ET2535-v2.xml",test[0]);       
    }
    
     /**
     * Delete the file created previously
     * @throws Exception 
     */
    @After
    public void cleanFiles() throws Exception
    {
        File file = new File("TracerTEST.xml"); //Get the file
        if(!(file.delete())) //delete
        {
            //throw exception in case of error
            throw new Exception("Testing File 'TracerTEST.xml' couldn't be deleted");
        }
    }
    
}
