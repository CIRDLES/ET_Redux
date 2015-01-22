/*
 * IncludedTypeEnum_Test_05042014Test.java
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



public class IncludedTypeEnum_Test_05042014Test {
    
    

    ////////////////////////////////////////////
    ////Constructor Tests///&///Method Tests////
    ////////////////////////////////////////////      
    
     /**
     * Test of IncludedTypeEnum() method, of class IncludedTypeEnum.
     */
    @Test
    public void test_constructor_0(){
	System.out.println("Testing IncludedTypeEnum's enumerations and getters.");
        //Tests if values are correct for all enumerations, and tests the getters as well. You cannot instantiate new inumerations.
        
        IncludedTypeEnum ave=IncludedTypeEnum.ALL;
        assertEquals(true,ave.isObjectIncluded(true));
        assertEquals(true,ave.isObjectIncluded(false));
        
        ave=IncludedTypeEnum.EXCLUDED;
        assertEquals(false,ave.isObjectIncluded(true));
        assertEquals(true,ave.isObjectIncluded(false));
        
        ave=IncludedTypeEnum.INCLUDED;
        assertEquals(true,ave.isObjectIncluded(true));   
        assertEquals(false,ave.isObjectIncluded(false));
    }    
        
    
    
    
    
    
    
    
    
}
