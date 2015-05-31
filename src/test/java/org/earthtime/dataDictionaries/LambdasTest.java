/*
 * Lambdas_Test_05042014Test.java
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
public class LambdasTest {
    
    
    
    
    
    ////////////////////////////////////////////
    ////Constructor Tests///&///Method Tests////
    ////////////////////////////////////////////      
    
     /**
     * Test of Lambdas() method, of class Lambdas.
     */
    @Test
    public void test_constructor_0(){
	System.out.println("Testing Lambdas's enumerations and getters.");
        //Tests if values are correct for all enumerations, and tests the getters as well. You cannot instantiate new inumerations.
        
        Lambdas ave=Lambdas.lambda230;
        assertEquals("lambda230",ave.getName());
        assertEquals("lambda 230",ave.getDisplayName());
        assertEquals(0,ave.getIndex());
        
        ave=Lambdas.lambda231;
        assertEquals("lambda231",ave.getName());
        assertEquals("lambda 231",ave.getDisplayName());
        assertEquals(1,ave.getIndex());
    
        ave=Lambdas.lambda232;
        assertEquals("lambda232",ave.getName());
        assertEquals("lambda 232",ave.getDisplayName());
        assertEquals(2,ave.getIndex());    
    
        ave=Lambdas.lambda234;
        assertEquals("lambda234",ave.getName());
        assertEquals("lambda 234",ave.getDisplayName());
        assertEquals(3,ave.getIndex());    
    
        ave=Lambdas.lambda235;
        assertEquals("lambda235",ave.getName());
        assertEquals("lambda 235",ave.getDisplayName());
        assertEquals(4,ave.getIndex());
    
        ave=Lambdas.lambda238;
        assertEquals("lambda238",ave.getName());
        assertEquals("lambda 238",ave.getDisplayName());
        assertEquals(5,ave.getIndex());
    
    }    
        
    
    
    
        
    
    
    
    
    
    
    
    
}
