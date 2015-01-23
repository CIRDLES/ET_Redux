/*
 * SampleAnalysisTypesEnum_Test_05032014Test.java
 *
 * The naming of this file is the name of the .java file being tested, combined
 * with test since this is the test file, combined with the creation date of
 * this file. The test at the end is so that the file runs from NetBeans when
 * testing an entire project.
 *
 * Created on May 3, 2014.
 *
 *
 *Version History:
 *May 3 2014: File Created. Constructor and method tests completed.
 *
 *
 *
 *Developed for the CIRDLES lab at the College of Charleston by Patrick Brewer.
 *
 *
 */
package org.earthtime.dataDictionaries;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author patrickbrewer
 */
public class SampleAnalysisTypesEnumTest {
    
    
    
    
    ////////////////////////////////////////////
    ////Constructor Tests///&///Method Tests////
    ////////////////////////////////////////////       
    

     /**
     * Test of SampleAnalysisTypesEnum() method, of class SampleAnalysisTypesEnum.
     */
    @Test
    public void test_constructor_0(){
	System.out.println("Testing SampleAnalysisTypesEnum's enumerations and getters.");
        //Tests if values are correct for all enumerations, and tests the getters as well. You cannot instantiate new inumerations.
        
        SampleAnalysisTypesEnum ave=SampleAnalysisTypesEnum.COMPILED;
        assertEquals("COMPILED",ave.getName());

        ave=SampleAnalysisTypesEnum.GENERIC_UPB;
        assertEquals("GENERIC_UPB",ave.getName());
  
        ave=SampleAnalysisTypesEnum.IDTIMS;
        assertEquals("IDTIMS",ave.getName());
    
        ave=SampleAnalysisTypesEnum.LAICPMS;
        assertEquals("LAICPMS",ave.getName());
    
        ave=SampleAnalysisTypesEnum.TRIPOLIZED;
        assertEquals("TRIPOLIZED",ave.getName());
 
    }    
        
    
    
    
    
}
