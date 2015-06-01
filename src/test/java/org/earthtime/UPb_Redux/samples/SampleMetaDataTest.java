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
import org.earthtime.UPb_Redux.fractions.FractionMetaData;
import org.junit.After;
import org.junit.Test;

/**
 *
 * @author parizotclement
 */
public class SampleMetaDataTest {
    
     /**
     * Integration Test of class SampleMetadata
     * Testing the writing and reading of a file
     * @throws Exception
     */
    @Test
    public void testSerialization() throws Exception {

        SampleMetaData sampleMetaData =
                new SampleMetaData("SampleJim", "C:xyz/Sector54Data");

        sampleMetaData.setFractionsMetaData(new FractionMetaData[]//
                {new FractionMetaData("F-1", "Aliquot1", "F-1_U.xml", "F-1_Pb.xml"),//
                    new FractionMetaData("F-2", "Aliquot1", "F-2_U.xml", "F-2_Pb.xml")}//
                );

        String testFileName = "SampleMetaDataTEST.xml";

        sampleMetaData.serializeXMLObject(testFileName);
        sampleMetaData.readXMLObject(testFileName, true);

    }
    
    /**
     * Delete the file created previously
     * @throws Exception 
     */
    @After
    public void cleanFiles() throws Exception
    {
        File file = new File("SampleMetaDataTEST.xml"); //Get the file
        if(!(file.delete())) //delete
        {
            //throw exception in case of error
            throw new Exception("Testing File 'SampleMetaDataTEST.xml' couldn't be deleted");
        }
    }
    
}
