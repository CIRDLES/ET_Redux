/*
 * TracerUPbRatiosAndConcentrations_Test_05052014Test.java
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
public class TracerUPbRatiosAndConcentrationsTest {
    
    
    
    
    
    

    
    ////////////////////////////////////////////
    ////Constructor Tests///&///Method Tests////
    ////////////////////////////////////////////      
    
     /**
     * Test of TracerUPbRatiosAndConcentrations() method, of class TracerUPbRatiosAndConcentrations.
     */
    @Test
    public void test_constructor_0(){
	System.out.println("Testing TracerUPbRatiosAndConcentrations's enumerations and getters.");
        //Tests if values are correct for all enumerations, and tests the getters as well. You cannot instantiate new inumerations.
        
        TracerUPbRatiosAndConcentrations ave=TracerUPbRatiosAndConcentrations.concPb205t;
        assertEquals("concPb205t",ave.getName());

        ave=TracerUPbRatiosAndConcentrations.concU235t;
        assertEquals("concU235t",ave.getName());
        
        ave=TracerUPbRatiosAndConcentrations.concU236t;
        assertEquals("concU236t",ave.getName());        
        
        ave=TracerUPbRatiosAndConcentrations.r202_205t;
        assertEquals("r202_205t",ave.getName());        
        
        ave=TracerUPbRatiosAndConcentrations.r204_205t;
        assertEquals("r204_205t",ave.getName());        
        
        ave=TracerUPbRatiosAndConcentrations.r206_205t;
        assertEquals("r206_205t",ave.getName());        
        
        ave=TracerUPbRatiosAndConcentrations.r207_205t;
        assertEquals("r207_205t",ave.getName());        
        
        ave=TracerUPbRatiosAndConcentrations.r208_205t;
        assertEquals("r208_205t",ave.getName());        
        
        ave=TracerUPbRatiosAndConcentrations.r233_235t;
        assertEquals("r233_235t",ave.getName());        
        
        ave=TracerUPbRatiosAndConcentrations.r233_236t;
        assertEquals("r233_236t",ave.getName());        
        
        ave=TracerUPbRatiosAndConcentrations.r238_233t;
        assertEquals("r238_233t",ave.getName());        
        
        ave=TracerUPbRatiosAndConcentrations.r238_235t;
        assertEquals("r238_235t",ave.getName());
        
        String[] list=TracerUPbRatiosAndConcentrations.getNames();

        assertEquals("r202_205t",list[0]);
        assertEquals("r204_205t",list[1]);
        assertEquals("r206_205t",list[2]);
        assertEquals("r207_205t",list[3]);
        assertEquals("r208_205t",list[4]);
        assertEquals("r233_235t",list[5]);
        assertEquals("r238_235t",list[6]);
        assertEquals("r233_236t",list[7]);
        assertEquals("r238_233t",list[8]);
        assertEquals("concPb205t",list[9]);
        assertEquals("concU235t",list[10]);
        assertEquals("concU236t",list[11]);
        

        
    }    
        
    
    
    
    
    //////////////////
    //Method Tests////
    //////////////////      
    
    
    
    /**
     * Test of contains method, of class TracerUPbRatiosAndConcentrations.
    */ 
    @Test
    public void test_Contains() {
        System.out.println("Testing TracerUPbRatiosAndConcentrations's contains(String checkString)");
        String checkString = "";
        boolean expResult = false;
        boolean result = TracerUPbRatiosAndConcentrations.contains(checkString);
        assertEquals(expResult, result);

        checkString="fractionMass";
        result = TracerUPbRatiosAndConcentrations.contains(checkString);
        assertEquals(expResult, result);
        
        checkString="concPb205t";
        expResult=true;
        result = TracerUPbRatiosAndConcentrations.contains(checkString);
        assertEquals(expResult, result);        
    }    
    
    
}
