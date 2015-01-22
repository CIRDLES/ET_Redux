/*
 * Intermediates_Test_05042014Test.java
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
public class Intermediates_Test_05042014Test {
    
    
    
    
    
    
    
    ////////////////////////////////////////////
    ////Constructor Tests///&///Method Tests////
    ////////////////////////////////////////////      
    
     /**
     * Test of Intermediates_Test_05042014Test() method, of class Intermediates_Test_05042014Test.
     */
    @Test
    public void test_constructor_0(){
	System.out.println("Testing Intermediates's enumerations and getters.");
        //Tests if values are correct for all enumerations, and tests the getters as well. You cannot instantiate new inumerations.
        
        Intermediates ave=Intermediates.blankPbGramsMol;
        assertEquals("blankPbGramsMol",ave.getName());


        
    }    
        
    
    
    
    
    //////////////////
    //Method Tests////
    //////////////////      
    
    
    
    
    /**
     * Test of contains method, of class Intermediates.
    */ 
    @Test
    public void test_Contains() {
        System.out.println("Testing Intermediates's contains(String checkString)");
        String checkString = "";
        boolean expResult = false;
        boolean result = Intermediates.contains(checkString);
        assertEquals(expResult, result);

        checkString="fractionMass";
        result = Intermediates.contains(checkString);
        assertEquals(expResult, result);
        
        checkString="blankPbGramsMol";
        expResult=true;
        result = Intermediates.contains(checkString);
        assertEquals(expResult, result);        
    }    
    
    
    
    
    
    
    
    
    
    
    
    
}
