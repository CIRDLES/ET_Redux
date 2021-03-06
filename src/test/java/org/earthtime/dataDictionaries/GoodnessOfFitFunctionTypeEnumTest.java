/*
 * GoodnessOfFitFunctionTypeEnum_Test_05042014Test.java
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
public class GoodnessOfFitFunctionTypeEnumTest {
    
    
    
    
    
    ////////////////////////////////////////////
    ////Constructor Tests///&///Method Tests////
    ////////////////////////////////////////////      
    
     /**
     * Test of GoodnessOfFitFunctionTypeEnum() method, of class GoodnessOfFitFunctionTypeEnum.
     */
    @Test
    public void test_constructor_0(){
	System.out.println("Testing GoodnessOfFitFunctionTypeEnum's enumerations and getters.");
        //Tests if values are correct for all enumerations, and tests the getters as well. You cannot instantiate new inumerations.
        
        GoodnessOfFitFunctionTypeEnum ave=GoodnessOfFitFunctionTypeEnum.CHISQ;
        assertEquals("CHISQ",ave.getName());
        
        ave=GoodnessOfFitFunctionTypeEnum.MSWD;
        assertEquals("MSWD",ave.getName());   
        
        ave=GoodnessOfFitFunctionTypeEnum.NAN;
        assertEquals("NAN",ave.getName());           
        
        ave=GoodnessOfFitFunctionTypeEnum.SSE;
        assertEquals("SSE",ave.getName());           
    }    
        
    
        
    
    
    
    
    
    
}
