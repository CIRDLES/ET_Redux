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
package org.earthtime.ratioDataViews;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataModels.initialPbModelsET.InitialPbModelET;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author parizotclement
 */
public class RatiosDataViewNotEditableTest {
    
     /**
     * Integration Test of class RatiosDataViewNotEditable
     * Testing the display
     * @throws java.lang.Exception
     */   
    @Test
    public void testDisplay() throws Exception
    {
         ValueModel[] myRatios = new ValueModel[3];
        myRatios[0] = new ValueModel(//
                //
                "r206_204c", //
                BigDecimal.ZERO, //
                "ABS", //
                new BigDecimal( 0.06298816629854530000 / 2.0 ), BigDecimal.ZERO );
        myRatios[1] = new ValueModel(//
                //
                "r207_204c", //
                BigDecimal.ZERO, //
                "ABS", //
                new BigDecimal( 0.92376003656586900000 / 2.0 ), BigDecimal.ZERO );
        myRatios[2] = new ValueModel(//
                //
                "r208_204c", //
                BigDecimal.ZERO, //
                "ABS", //
                new BigDecimal( 0.00040104065069202200 / 2.0 ), BigDecimal.ZERO );


        Map<String, BigDecimal> correlations = new HashMap<String, BigDecimal>();
        correlations.put( "rhoR206_204c__r207_204c", new BigDecimal(  - 0.0400671215735759 ) );
        correlations.put( "rhoR206_204c__r208_204c", new BigDecimal(  - 0.0400671215735759 ) );
        correlations.put( "rhoR207_204c__r208_204c", new BigDecimal(  - 0.0400671215735759 ) );
        AbstractRatiosDataModel initialPbModel1 = //
                InitialPbModelET.createInstance(//
                "initialPbModel1", 1, 0,"Test Lab", "2012-04-01", "NO REF", "NO COMMENT", myRatios, correlations );

        AbstractRatiosDataView testView = new RatiosDataViewNotEditable( initialPbModel1, null, false);
        
        
        // Still need to figure out how to close the window
        //testView.displayModelInFrame();


    }
    
}
