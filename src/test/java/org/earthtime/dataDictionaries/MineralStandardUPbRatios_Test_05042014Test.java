/*
 * MineralStandardUPbRatios_Test_05042014Test.java
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
public class MineralStandardUPbRatios_Test_05042014Test {
    
    
    
    

    
    ////////////////////////////////////////////
    ////Constructor Tests///&///Method Tests////
    ////////////////////////////////////////////      
    
     /**
     * Test of MineralStandardUPbRatiosEnum() method, of class MineralStandardUPbRatiosEnum.
     */
    @Test
    public void test_constructor_0(){
	System.out.println("Testing MineralStandardUPbRatios's enumerations and getters.");
        //Tests if values are correct for all enumerations, and tests the getters as well. You cannot instantiate new inumerations.
        
        MineralStandardUPbRatiosEnum ave=MineralStandardUPbRatiosEnum.r206_207r;
        assertEquals("r206_207r",ave.getName());

        ave=MineralStandardUPbRatiosEnum.r206_208r;
        assertEquals("r206_208r",ave.getName());        
        
        ave=MineralStandardUPbRatiosEnum.r206_238r;
        assertEquals("r206_238r",ave.getName());         
        
        ave=MineralStandardUPbRatiosEnum.r208_232r;
        assertEquals("r208_232r",ave.getName());         
        
        ave=MineralStandardUPbRatiosEnum.r238_235s;
        assertEquals("r238_235s",ave.getName()); 
        
        String[] list=MineralStandardUPbRatiosEnum.getNames();
        assertEquals("r206_207r",list[0]);
        assertEquals("r206_208r",list[1]);
        assertEquals("r206_238r",list[2]);
        assertEquals("r208_232r",list[3]);
        assertEquals("r238_235s",list[4]);
        
        
    }    
        
    
    
    
    
    //////////////////
    //Method Tests////
    //////////////////      
    
    
    
    /**
     * Test of contains method, of class MineralStandardUPbRatiosEnum.
    */ 
    @Test
    public void test_Contains() {
        System.out.println("Testing MineralStandardUPbRatios's contains(String checkString)");
        String checkString = "";
        boolean expResult = false;
        boolean result = MineralStandardUPbRatiosEnum.contains(checkString);
        assertEquals(expResult, result);

        checkString="fractionMass";
        result = MineralStandardUPbRatiosEnum.contains(checkString);
        assertEquals(expResult, result);
        
        checkString="r206_207r";
        expResult=true;
        result = MineralStandardUPbRatiosEnum.contains(checkString);
        assertEquals(expResult, result);        
    }
    
        
    
    
    
    
    
    
    
    
    
}
