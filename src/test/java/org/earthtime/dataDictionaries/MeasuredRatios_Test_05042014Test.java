/*
 * MeasuredRatios_Test_05042014Test.java
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
public class MeasuredRatios_Test_05042014Test {

    
    ////////////////////////////////////////////
    ////Constructor Tests///&///Method Tests////
    ////////////////////////////////////////////      
    
     /**
     * Test of MeasuredRatios() method, of class MeasuredRatios.
     */
    @Test
    public void test_constructor_0(){
	System.out.println("Testing MeasuredRatios's enumerations and getters.");
        //Tests if values are correct for all enumerations, and tests the getters as well. You cannot instantiate new inumerations.
        
        String[] answer=MeasuredRatios.getNames();
        
        MeasuredRatios ave=MeasuredRatios.r202_205m;
        assertEquals("r202_205m",ave.getName());

        ave=MeasuredRatios.r204_205m;
        assertEquals("r204_205m",ave.getName());
        
        ave=MeasuredRatios.r206_204m;
        assertEquals("r206_204m",ave.getName());
        
        ave=MeasuredRatios.r206_205m;
        assertEquals("r206_205m",ave.getName());
        
        ave=MeasuredRatios.r206_207m;
        assertEquals("r206_207m",ave.getName());
        
        ave=MeasuredRatios.r206_208m;
        assertEquals("r206_208m",ave.getName());
        
        ave=MeasuredRatios.r207_204m;
        assertEquals("r207_204m",ave.getName());
        
        ave=MeasuredRatios.r207_205m;
        assertEquals("r207_205m",ave.getName());
        
        ave=MeasuredRatios.r208_204m;
        assertEquals("r208_204m",ave.getName());
        
        ave=MeasuredRatios.r208_205m;
        assertEquals("r208_205m",ave.getName());
        
        ave=MeasuredRatios.r233_235m;
        assertEquals("r233_235m",ave.getName());
        
        ave=MeasuredRatios.r233_236m;
        assertEquals("r233_236m",ave.getName());
        
        ave=MeasuredRatios.r238_233m;
        assertEquals("r238_233m",ave.getName());        
        
        ave=MeasuredRatios.r238_235m;
        assertEquals("r238_235m",ave.getName());  
        
        ave=MeasuredRatios.r238_236m;
        assertEquals("r238_236m",ave.getName());          
        
        assertEquals("r206_204m",answer[0]);
        assertEquals("r207_204m",answer[1]);
        assertEquals("r208_204m",answer[2]);
        assertEquals("r206_207m",answer[3]);
        assertEquals("r206_208m",answer[4]);
        assertEquals("r204_205m",answer[5]);
        assertEquals("r206_205m",answer[6]);
        assertEquals("r207_205m",answer[7]);
        assertEquals("r208_205m",answer[8]);
        assertEquals("r202_205m",answer[9]);
        assertEquals("r238_235m",answer[10]);
        assertEquals("r233_235m",answer[11]);
        assertEquals("r238_233m",answer[12]);
        assertEquals("r238_236m",answer[13]);
        assertEquals("r233_236m",answer[14]);
        
    }    
        
    
    
    
    
    //////////////////
    //Method Tests////
    //////////////////      
    
    
    
    /**
     * Test of contains method, of class MeasuredRatios.
     */
    @Test
    public void test_Contains() {
        System.out.println("Testing MeasuredRatios's contains(String checkString)");
        String checkString = "";
        boolean expResult = false;
        boolean result = MeasuredRatios.contains(checkString);
        assertEquals(expResult, result);

        checkString="fractionMass";
        result = MeasuredRatios.contains(checkString);
        assertEquals(expResult, result);
        
        checkString="r202_205m";
        expResult=true;
        result = MeasuredRatios.contains(checkString);
        assertEquals(expResult, result);        
    }
    
    






}
