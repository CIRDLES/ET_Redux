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
package org.earthtime.UPb_Redux.reports;

import java.io.File;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author parizotclement
 */
public class ReportSettingsTest {
    
     /**
     * Integration Test of class ReportSettings
     * Testing the writing and reading of a file
     * @throws Exception
     */
    @Test
    public void testSerialization() throws Exception {

        ReportSettings reportSettings
                = new ReportSettings("Test ReportSettings");
        String testFileName = "ReportSettingsTEST.xml";

        reportSettings.serializeXMLObject(testFileName);
        reportSettings.readXMLObject(testFileName, true);

    }
    
    
     /**
     * Delete the file created previously
     * @throws Exception 
     */
    @After
    public void cleanFiles() throws Exception
    {
        File file = new File("ReportSettingsTEST.xml"); //Get the file
        if(!(file.delete())) //delete
        {
            //throw exception in case of error
            throw new Exception("Testing File 'ReportSettingsTEST.xml' couldn't be deleted");
        }
    }
}
