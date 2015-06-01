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
package org.earthtime.ratioDataModels.physicalConstantsModels;

import java.io.File;
import java.io.FileNotFoundException;
import org.earthtime.UPb_Redux.utilities.ETSerializer;
import org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException;
import org.earthtime.exceptions.ETException;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataViews.AbstractRatiosDataView;
import org.earthtime.ratioDataViews.PhysicalConstantsDataViewEditable;
import org.junit.After;
import org.junit.Test;


/**
 *
 * @author parizotclement
 */
public class PhysicalConstantsModelTest {
    
    /**
     * Integration Test of class PhysicalConstantsModel
     * Testing the writing and reading of a file
     * @throws Exception
     */
    @Test
    public void testSerialization () throws Exception {

        AbstractRatiosDataModel physicalConstantsModel = PhysicalConstantsModel.getEARTHTIMEPhysicalConstantsModel();

        //Throws Exception
        ETSerializer.SerializeObjectToFile(physicalConstantsModel, "PhysicalConstantsModelTEST.ser");

        AbstractRatiosDataModel physicalConstantsModel2 = (AbstractRatiosDataModel) ETSerializer.GetSerializedObjectFromFile( "PhysicalConstantsModelTEST.ser" );

        String testFileName = "PhysicalConstantsModelTEST.xml";

        physicalConstantsModel2.serializeXMLObject( testFileName );

        //Throws Exception
        physicalConstantsModel2.readXMLObject(testFileName, true );


        AbstractRatiosDataView testView = new PhysicalConstantsDataViewEditable( PhysicalConstantsModel.getEARTHTIMEPhysicalConstantsModel(), null, false);

        //Still need to figure out how to close the window
        //testView.displayModelInFrame();
    }
    
    /**
     * Delete the files created previously
     * @throws Exception 
     */
    @After
    public void cleanFiles() throws Exception
    {
        File file = new File("PhysicalConstantsModelTEST.ser"); //Get the file
        if(!(file.delete())) //delete
        {
            //throw exception in case of error
            throw new Exception("Testing File 'PhysicalConstantsModelTEST.ser' couldn't be deleted");
        }
        
        file = new File("PhysicalConstantsModelTEST.xml"); //Get the file
        if(!(file.delete())) //delete
        {
            //throw exception in case of error
            throw new Exception("Testing File 'PhysicalConstantsModelTEST.xml' couldn't be deleted");
        }
    }
    
}
