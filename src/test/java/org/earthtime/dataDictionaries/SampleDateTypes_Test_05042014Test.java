/*
 * SampleDateTypes_Test_05042014Test.java
 *
 * The naming of this file is the name of the .java file being tested, combined
 * with test since this is the test file, combined with the creation date of
 * this file. The test at the end is so that the file runs from NetBeans when
 * testing an entire project.
 *
 * Created on May 4, 2014.
 *
 *
 *Version History:
 *May 4 2014: File Created. Constructor and method tests completed.
 *
 *
 *
 *Developed for the CIRDLES lab at the College of Charleston by Patrick Brewer.
 *
 *
 */

package org.earthtime.dataDictionaries;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author patrickbrewer
 */
public class SampleDateTypes_Test_05042014Test {
    
    
    

    //////////////////
    //Method Tests////
    //////////////////     
    

    /**
     * Test of getSampleDateType method, of class SampleDateTypes.
     */
    @Test
    public void test_GetSampleDateType() {
        System.out.println("Testing SampleDateTypes's getSampleDateType(int index)");
        int index = 0;
        String expResult = "single analysis 206Pb/238U";
        String result = SampleDateTypes.getSampleDateType(index);
        assertEquals(expResult, result);

        index=10;
        result=SampleDateTypes.getSampleDateType(index);
        expResult="weighted mean 207Pb/206Pb (Th-corrected)";
        assertEquals(expResult, result);
    }

    
    
    /**
     * Test of getSampleDateTypeMethod method, of class SampleDateTypes.
     */
    @Test
    public void test_GetSampleDateTypeMethod() {
        System.out.println("Testing SampleDateTypes's getSampleDateTypeMethod(int index)");
        int index = 0;
        String expResult = "SA206_238";
        String result = SampleDateTypes.getSampleDateTypeMethod(index);
        assertEquals(expResult, result);

        index=10;
        result=SampleDateTypes.getSampleDateTypeMethod(index);
        expResult="WM207_206r_Th";
        assertEquals(expResult, result);
        
    }

    
    
    /**
     * Test of getSampleDateTypeName method, of class SampleDateTypes.
     */
    @Test
    public void test_GetSampleDateTypeName() {
        System.out.println("Testing SampleDateTypes's getSampleDateTypeName(int index)");
        int index = 0;
        String expResult = "age206_238r";
        String result = SampleDateTypes.getSampleDateTypeName(index);
        assertEquals(expResult, result);

        index=10;
        result=SampleDateTypes.getSampleDateTypeName(index);
        expResult="age207_206r_Th";
        assertEquals(expResult, result);
        
        
    }

    ////////////////////////
    ////Unfinished Tests////
    //////////////////////// 
    
    /*
     *
     *SampleDateTypes.testGetSampleDateModelTypes()
     *
     *
     *
     */
        
    /**
     * Test of getSampleDateModelTypes method, of class SampleDateTypes.
    */
    @Test
    public void testGetSampleDateModelTypes() {
        System.out.println("Testing SampleDateTypes's getSampleDateModelTypes()");
        String expResult="single analysis 206Pb/238U";
        String[][] result = SampleDateTypes.getSampleDateModelTypes();
        
        assertEquals(expResult, result[0][0]);

        
    }    
     
    
    
}
