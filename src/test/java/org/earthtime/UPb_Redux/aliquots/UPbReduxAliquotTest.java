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
package org.earthtime.UPb_Redux.aliquots;

import java.io.File;
import java.math.BigDecimal;
import org.earthtime.UPb_Redux.fractions.FractionI;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFraction;
import org.earthtime.UPb_Redux.samples.SESARSampleMetadata;
import org.earthtime.UPb_Redux.valueModels.SampleDateModel;
import org.earthtime.aliquots.ReduxAliquotInterface;
import org.earthtime.ratioDataModels.physicalConstantsModels.PhysicalConstantsModel;
import org.earthtime.xmlUtilities.XMLSerializationI;
import org.junit.After;
import org.junit.Test;

/**
 *
 * @author parizotclement
 */
public class UPbReduxAliquotTest {
    
    
    /**
     * Test of the main method of class UPBReduxAliquotTest
     * Test if class can write and read from an XML file
     * @throws Exception 
     */
    @Test
    public void testXMLSerialization() throws Exception {
        Aliquot aliquot
                = new UPbReduxAliquot(
                        0,
                        "Test Aliquot",
                        PhysicalConstantsModel.getMostRecentEARTHTIMEPhysicalConstantsModel(),
                        false,
                        new SESARSampleMetadata());

        FractionI uPbfraction = new UPbFraction("NONE");
        uPbfraction.setSampleName("TestSample");
        uPbfraction.setFractionID("TestFraction");
        uPbfraction.setGrainID("TestFraction");
        uPbfraction.setZircon(true);

//        aliquot.getMineralStandardModels().add( new MineralStandardModel( "Test Model" ) );
        SampleDateModel temp1 = new SampleDateModel(
                "WM208_232",
                "WM208_232",
                "",
                new BigDecimal("1.1"),
                "PCT",
                new BigDecimal("0.222"));

        temp1.setPreferred(true);
        aliquot.getSampleDateModels().add(temp1);

        ((ReduxAliquotInterface) aliquot).getAliquotFractions().clear();
        ((ReduxAliquotInterface) aliquot).getAliquotFractions().add(uPbfraction);
        temp1.setAliquot(aliquot);
        //     temp1.PopulateFractionVector();

        ((UPbReduxAliquot) aliquot).prepareUPbReduxAliquotForXMLSerialization();

        String testFileName = "UPbReduxAliquotTEST.xml";

        //Throws Exception
        ((XMLSerializationI) aliquot).serializeXMLObject(testFileName);
        ((XMLSerializationI) aliquot).readXMLObject(testFileName, true);
    }
    
    /**
     * After method to delete the file created during the test
     * @throws Exception 
     */
    @After
    public void cleanFiles() throws Exception
    {
        File file = new File("UPbReduxAliquotTEST.xml"); //Get the file
        if(!(file.delete())) //delete
        {
            //throw exception in case of error
            throw new Exception("Testing File 'UPbReduxAliquotTEST.xml' couldn't be deleted");
        }
    }
    
}
