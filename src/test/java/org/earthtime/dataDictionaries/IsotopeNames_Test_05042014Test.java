/*
 * IsotopeNames_Test_05042014Test.java
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

public class IsotopeNames_Test_05042014Test {
    
    
    
    
    
    ////////////////////////////////////////////
    ////Constructor Tests///&///Method Tests////
    ////////////////////////////////////////////      
    
     /**
     * Test of IsotopeNames() method, of class IsotopeNames.
     */
    @Test
    public void test_constructor_0(){
	System.out.println("Testing IsotopesNames's enumerations and getters.");
        //Tests if values are correct for all enumerations, and tests the getters as well. You cannot instantiate new inumerations.
        
        IsotopeNames ave=IsotopeNames.Hg202;
        assertEquals("Hg202",ave.getName());

        ave=IsotopeNames.Pb204;
        assertEquals("Pb204",ave.getName());
    
        ave=IsotopeNames.Pb206;
        assertEquals("Pb206",ave.getName());    
    
        ave=IsotopeNames.Pb207;
        assertEquals("Pb207",ave.getName());      
    
        ave=IsotopeNames.Pb208;
        assertEquals("Pb208",ave.getName());      
    
        ave=IsotopeNames.Th232;
        assertEquals("Th232",ave.getName());          
        
        ave=IsotopeNames.U235;
        assertEquals("U235",ave.getName());  
    
        ave=IsotopeNames.U238;
        assertEquals("U238",ave.getName());  
    
    
    }        
    
    
    
}
