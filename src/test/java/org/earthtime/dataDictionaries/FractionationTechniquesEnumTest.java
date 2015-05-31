/*
 * FractionationTechniquesEnum_Test_05042014Test.java
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


public class FractionationTechniquesEnumTest {
    
    
    
    
    
    ////////////////////////////////////////////
    ////Constructor Tests///&///Method Tests////
    ////////////////////////////////////////////      
    
     /**
     * Test of FractionationTechniquesEnum() method, of class FractionationTechniquesEnum.
     */
    @Test
    public void test_constructor_0(){
	System.out.println("Testing FractionationTechniquesEnum's enumerations and getters.");
        //Tests if values are correct for all enumerations, and tests the getters as well. You cannot instantiate new inumerations.
        
        FractionationTechniquesEnum ave=FractionationTechniquesEnum.DOWNHOLE;
        assertEquals("DOWNHOLE",ave.getName());

        ave=FractionationTechniquesEnum.INTERCEPT;
        assertEquals("INTERCEPT",ave.getName());        
        
    }        
    
    
    
    
    
    
    
    
    
    
    
}
