/*
 * TracerRatiosEnum_Test_05042014Test.java
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

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author patrickbrewer
 */
public class TracerRatiosEnum_Test_05042014Test {
    
    
    
    
    
    
    

    ////////////////////////////////////////////
    ////Constructor Tests///&///Method Tests////
    ////////////////////////////////////////////      
    
     /**
     * Test of TracerRatiosEnum() method, of class TracerRatiosEnum.
     */
    @Test
    public void test_constructor_0(){
	System.out.println("Testing TracerRatiosEnum's enumerations and getters.");
        //Tests if values are correct for all enumerations, and tests the getters as well. You cannot instantiate new inumerations.
        
        TracerRatiosEnum ave=TracerRatiosEnum.r202_205t;
        assertEquals("r202_205t",ave.getName());

        ave=TracerRatiosEnum.r204_205t;
        assertEquals("r204_205t",ave.getName());
        
        ave=TracerRatiosEnum.r206_204t;
        assertEquals("r206_204t",ave.getName());        
        
        ave=TracerRatiosEnum.r206_205t;
        assertEquals("r206_205t",ave.getName());        
        
        ave=TracerRatiosEnum.r206_208t;
        assertEquals("r206_208t",ave.getName());        
        
        ave=TracerRatiosEnum.r207_204t;
        assertEquals("r207_204t",ave.getName());        
        
        ave=TracerRatiosEnum.r207_205t;
        assertEquals("r207_205t",ave.getName());
        
        ave=TracerRatiosEnum.r207_206t;
        assertEquals("r207_206t",ave.getName());        
        
        ave=TracerRatiosEnum.r208_204t;
        assertEquals("r208_204t",ave.getName());        
        
        ave=TracerRatiosEnum.r208_205t;
        assertEquals("r208_205t",ave.getName());
        
        ave=TracerRatiosEnum.r233_235t;        
        assertEquals("r233_235t",ave.getName());
        
        ave=TracerRatiosEnum.r233_236t;        
        assertEquals("r233_236t",ave.getName());
        
        ave=TracerRatiosEnum.r235_205t;        
        assertEquals("r235_205t",ave.getName());        
        
        ave=TracerRatiosEnum.r238_233t;        
        assertEquals("r238_233t",ave.getName());        
        
        ave=TracerRatiosEnum.r238_235t;        
        assertEquals("r238_235t",ave.getName());
        
        
        String[] list =TracerRatiosEnum.getNames();
        
        assertEquals("r206_204t",list[0]);
        assertEquals("r207_204t",list[1]);
        assertEquals("r208_204t",list[2]);
        assertEquals("r207_206t",list[3]);
        assertEquals("r206_208t",list[4]);
        assertEquals("r202_205t",list[5]);
        assertEquals("r204_205t",list[6]);
        assertEquals("r206_205t",list[7]);
        assertEquals("r207_205t",list[8]);
        assertEquals("r208_205t",list[9]);
        assertEquals("r233_235t",list[10]);
        assertEquals("r238_235t",list[11]);
        assertEquals("r238_233t",list[12]);
        assertEquals("r233_236t",list[13]);
        assertEquals("r235_205t",list[14]);
    }    
        
    
    
    
    
    //////////////////
    //Method Tests////
    //////////////////      
    
    
    
    /**
     * Test of contains method, of class TracerRatiosEnum.
    */ 
    @Test
    public void test_Contains() {
        System.out.println("Testing TracerRatiosEnum's contains(String checkString)");
        String checkString = "";
        boolean expResult = false;
        boolean result = TracerRatiosEnum.contains(checkString);
        assertEquals(expResult, result);

        checkString="fractionMass";
        result = TracerRatiosEnum.contains(checkString);
        assertEquals(expResult, result);
        
        checkString="r202_205t";
        expResult=true;
        result = TracerRatiosEnum.contains(checkString);
        assertEquals(expResult, result);        
    }    
    
    
}
