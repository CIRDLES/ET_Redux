/*
 * FitFunctionTypeEnum_Test_05042014Test.java
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
public class FitFunctionTypeEnumTest {
    
    
    
    
    
    
    ////////////////////////////////////////////
    ////Constructor Tests///&///Method Tests////
    ////////////////////////////////////////////      
    
     /**
     * Test of FitFunctionTypeEnum() method, of class FitFunctionTypeEnum.
     */
    @Test
    public void test_constructor_0(){
	System.out.println("Testing FitFunctionTypeEnum's enumerations and getters.");
        //Tests if values are correct for all enumerations, and tests the getters as well. You cannot instantiate new inumerations.
        
        FitFunctionTypeEnum ave=FitFunctionTypeEnum.CONSTANT;
        assertEquals("CONSTANT",ave.getName());
        assertEquals("Constant",ave.getPrettyName());

        ave=FitFunctionTypeEnum.EXPFAST;
        assertEquals("EXPFAST",ave.getName());  
        assertEquals("Exponential fast",ave.getPrettyName());
        
        ave=FitFunctionTypeEnum.EXPMAT;
        assertEquals("EXPMAT",ave.getName());  
        assertEquals("Exponential Cov Mat",ave.getPrettyName());
        
        ave=FitFunctionTypeEnum.EXPONENTIAL;
        assertEquals("EXPONENTIAL",ave.getName()); 
        assertEquals("Exp",ave.getPrettyName());
        
        ave=FitFunctionTypeEnum.LINE;
        assertEquals("LINE",ave.getName()); 
        assertEquals("Line",ave.getPrettyName());
        
        ave=FitFunctionTypeEnum.MEAN;
        assertEquals("MEAN",ave.getName()); 
        assertEquals("Mean",ave.getPrettyName());

        ave=FitFunctionTypeEnum.NONE;
        assertEquals("NONE",ave.getName()); 
        assertEquals("NONE",ave.getPrettyName());
        
        ave=FitFunctionTypeEnum.SMOOTHING_SPLINE;
        assertEquals("SMOOTHING_SPLINE",ave.getName());         
        assertEquals("Spline",ave.getPrettyName());
    }    
    
    
            
    
    
    
    
    
}
