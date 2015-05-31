/*
 * SampleUPbRatios_Test_05052014Test.java
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
public class SampleUPbRatiosTest {
    
    

    
    ////////////////////////////////////////////
    ////Constructor Tests///&///Method Tests////
    ////////////////////////////////////////////      
    
     /**
     * Test of SampleUPbRatios() method, of class SampleUPbRatios.
     */
    @Test
    public void test_constructor_0(){
	System.out.println("Testing SampleUPbRatios's enumerations and getters.");
        //Tests if values are correct for all enumerations, and tests the getters as well. You cannot instantiate new inumerations.
        
        SampleUPbRatios ave=SampleUPbRatios.r206_204s;
        assertEquals("r206_204s",ave.getName());

        ave=SampleUPbRatios.r206_207s;
        assertEquals("r206_207s",ave.getName());
        
        ave=SampleUPbRatios.r206_208s;
        assertEquals("r206_208s",ave.getName());        
        
        ave=SampleUPbRatios.r206_238s;
        assertEquals("r206_238s",ave.getName());        
        
        ave=SampleUPbRatios.r208_232s;
        assertEquals("r208_232s",ave.getName());        
        
        ave=SampleUPbRatios.r238_235s;
        assertEquals("r238_235s",ave.getName());  
        
        
        String[] list=SampleUPbRatios.getNames();

        assertEquals("r206_204s",list[0]);
        assertEquals("r206_207s",list[1]);
        assertEquals("r206_208s",list[2]);
        assertEquals("r206_238s",list[3]);
        assertEquals("r208_232s",list[4]);
        assertEquals("r238_235s",list[5]);

        

        
    }    
        
    
    
    
    
    //////////////////
    //Method Tests////
    //////////////////      
    
    
    
    /**
     * Test of contains method, of class SampleUPbRatios.
    */ 
    @Test
    public void test_Contains() {
        System.out.println("Testing SampleUPbRatios's contains(String checkString)");
        String checkString = "";
        boolean expResult = false;
        boolean result = SampleUPbRatios.contains(checkString);
        assertEquals(expResult, result);

        checkString="fractionMass";
        result = SampleUPbRatios.contains(checkString);
        assertEquals(expResult, result);
        
        checkString="r206_204s";
        expResult=true;
        result = SampleUPbRatios.contains(checkString);
        assertEquals(expResult, result);        
    }    
        
    
    
    
}
