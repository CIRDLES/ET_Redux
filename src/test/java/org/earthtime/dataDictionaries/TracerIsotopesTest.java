/*
 * TracerIsotopes_Test_05042014Test.java
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
public class TracerIsotopesTest {
   
    
    
    ////////////////////////////////////////////
    ////Constructor Tests///&///Method Tests////
    ////////////////////////////////////////////      
    
     /**
     * Test of FileTypeEnum() method, of class FileTypeEnum.
     */
    @Test
    public void test_constructor_0(){
	System.out.println("Testing TracerIsotopes's enumerations and getters.");
        //Tests if values are correct for all enumerations, and tests the getters as well. You cannot instantiate new inumerations.
        
        TracerIsotopes ave=TracerIsotopes.concPb205t;
        assertEquals("concPb205t",ave.getName());

        ave=TracerIsotopes.concU235t;
        assertEquals("concU235t",ave.getName());
        
        ave=TracerIsotopes.concU236t;
        assertEquals("concU236t",ave.getName());        
        
        String[] list=TracerIsotopes.getNames();
        assertEquals("concPb205t",list[0]);
        assertEquals("concU235t",list[1]);
        assertEquals("concU236t",list[2]);
        
        
        
    }    
        
    
    
    
    
    //////////////////
    //Method Tests////
    //////////////////      
    
    
    
    /**
     * Test of contains method, of class TracerIsotopes.
    */ 
    @Test
    public void test_Contains() {
        System.out.println("Testing TracerIsotopes's contains(String checkString)");
        String checkString = "";
        boolean expResult = false;
        boolean result = TracerIsotopes.contains(checkString);
        assertEquals(expResult, result);

        checkString="fractionMass";
        result = TracerIsotopes.contains(checkString);
        assertEquals(expResult, result);
        
        checkString="concPb205t";
        expResult=true;
        result = TracerIsotopes.contains(checkString);
        assertEquals(expResult, result);        
    }
        
    
    
    
}
