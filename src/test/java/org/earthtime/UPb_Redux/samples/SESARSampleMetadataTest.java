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
package org.earthtime.UPb_Redux.samples;

import java.io.File;
import org.junit.After;
import org.junit.Test;

/**
 *
 * @author parizotclement
 */
public class SESARSampleMetadataTest {
    
     /**
     * Integration Test of class SESARSampleMetadata
     * Testing the writing and reading of a file
     * @throws Exception
     */
    @Test
    public void testSerialization() throws Exception {

        SESARSampleMetadata SESARSampleMetadata =
                new SESARSampleMetadata();
        String testFileName = "SESARSampleMetadataTEST.xml";

        SESARSampleMetadata.serializeXMLObject(testFileName);
        //SESARSampleMetadata.readXMLObject(testFileName, true);
     
    }
    
    /**
     * Delete the file created previously
     * @throws Exception 
     */
    @After
    public void cleanFiles() throws Exception
    {
        File file = new File("SESARSampleMetadataTEST.xml"); //Get the file
        if(!(file.delete())) //delete
        {
            //throw exception in case of error
            throw new Exception("Testing File 'SESARSampleMetadataTEST.xml' couldn't be deleted");
        }
    }
}
