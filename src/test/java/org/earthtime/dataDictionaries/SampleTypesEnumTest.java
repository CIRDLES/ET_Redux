/*
 * SampleTypesEnum_Test_05052014Test.java
 *
 * The naming of this file is the name of the .java file being tested, combined
 * with test since this is the test file, combined with the creation date of
 * this file. The test at the end is so that the file runs from NetBeans when
 * testing an entire project.
 *
 * Created on May 5, 2014.
 *
 *
 *Version History:
 *May 5 2014: File Created. Constructor and method tests completed.
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
public class SampleTypesEnumTest {
    
    
    
    


    
    ////////////////////////////////////////////
    ////Constructor Tests///&///Method Tests////
    ////////////////////////////////////////////      
    
     /**
     * Test of SampleTypesEnum() method, of class SampleTypesEnum.
     */
    @Test
    public void test_constructor_0(){
	System.out.println("Testing SampleTypesEnum's enumerations and getters.");
        //Tests if values are correct for all enumerations, and tests the getters as well. You cannot instantiate new inumerations.
        
        SampleTypesEnum ave=SampleTypesEnum.ANALYSIS;
        assertEquals("ANALYSIS",ave.getName());

        ave=SampleTypesEnum.COMPILATION;
        assertEquals("COMPILATION",ave.getName());
        
        ave=SampleTypesEnum.LEGACY;
        assertEquals("LEGACY",ave.getName());        
        
        ave=SampleTypesEnum.LIVEWORKFLOW;
        assertEquals("LIVEWORKFLOW",ave.getName());        
        
        ave=SampleTypesEnum.NONE;
        assertEquals("NONE",ave.getName());        
        
        ave=SampleTypesEnum.PROJECT;
        assertEquals("PROJECT",ave.getName());        
        
        ave=SampleTypesEnum.SAMPLEFOLDER;
        assertEquals("SAMPLEFOLDER",ave.getName());        
        
        
        
    }
    
    
}
