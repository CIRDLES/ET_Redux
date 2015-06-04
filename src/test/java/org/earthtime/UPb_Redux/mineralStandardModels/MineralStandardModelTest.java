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
package org.earthtime.UPb_Redux.mineralStandardModels;

import java.io.File;
import java.math.BigDecimal;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.junit.After;
import org.junit.Test;

/**
 *
 * @author parizotclement
 */
public class MineralStandardModelTest {
    
     /**
     * Integration Test of class MineralStandardModel
     * Testing the writing and reading of a file
     * @throws Exception
     */
    @Test
    public void testSerialization () throws Exception {

        MineralStandardModel mineralStandardModel = new MineralStandardModel().Copy();
        mineralStandardModel.getTrueAge().setValue( new BigDecimal( "1.1112" ) );
        mineralStandardModel.getMeasuredAge().setValue( new BigDecimal( "1.444" ) );

        mineralStandardModel.setRadiogenicIsotopeRatios( new ValueModel[]{new ValueModel( "r207_235r", new BigDecimal( 1011 ), "PCT", new BigDecimal( 1 ), BigDecimal.ZERO )} );

        String testFileName = "MineralStandardModelTEST.xml";
        mineralStandardModel.serializeXMLObject( testFileName );
        mineralStandardModel.readXMLObject( testFileName, true );

    }
    
    
     /**
     * Delete the file created previously
     * @throws Exception 
     */
    @After
    public void cleanFiles() throws Exception
    {
        File file = new File("MineralStandardModelTEST.xml"); //Get the file
        if(!(file.delete())) //delete
        {
            //throw exception in case of error
            throw new Exception("Testing File 'MineralStandardModelTEST.xml' couldn't be deleted");
        }
    }
        
}
