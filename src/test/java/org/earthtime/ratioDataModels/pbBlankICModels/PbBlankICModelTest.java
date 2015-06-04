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
package org.earthtime.ratioDataModels.pbBlankICModels;

import java.io.File;
import org.earthtime.UPb_Redux.utilities.ETSerializer;
import org.earthtime.exceptions.ETException;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.junit.After;
import org.junit.Test;

/**
 *
 * @author parizotclement
 */
public class PbBlankICModelTest {

    /**
     * Integration Test of class PbBlankICModel
     * Testing the writing and reading of a file
     */
    @Test
    public void testSerialization () throws Exception
    {

        AbstractRatiosDataModel pbBlankModelETs = PbBlankICModel.getEARTHTIMEExamplePbBlankICModel();

        try {
            ETSerializer.SerializeObjectToFile(pbBlankModelETs, "PbBlankICModelTEST.ser");
        } catch (ETException eTException) {
        }
        AbstractRatiosDataModel pbBlankModelET = (AbstractRatiosDataModel) ETSerializer.GetSerializedObjectFromFile( "PbBlankICModelTEST.ser" );

        String testFileName = "PbBlankICModelTEST.xml";

        pbBlankModelET.serializeXMLObject( testFileName );

        //Throws Exception
        pbBlankModelET.readXMLObject( testFileName, true );
    }   
    
    /**
     * Delete the files created previously
     * @throws Exception 
     */
    @After
    public void cleanFiles() throws Exception
    {
        File file = new File("PbBlankICModelTEST.ser"); //Get the file
        if(!(file.delete())) //delete
        {
            //throw exception in case of error
            throw new Exception("Testing File 'PbBlankICModelTEST.ser' couldn't be deleted");
        }
        
        file = new File("PbBlankICModelTEST.xml"); //Get the file
        if(!(file.delete())) //delete
        {
            //throw exception in case of error
            throw new Exception("Testing File 'PbBlankICModelTEST.xml' couldn't be deleted");
        }
    } 
}
