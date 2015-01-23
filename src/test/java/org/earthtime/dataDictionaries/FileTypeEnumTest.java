/*
 * FileTypeEnum_Test_05042014Test.java
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
public class FileTypeEnumTest {
 
    
    ////////////////////////////////////////////
    ////Constructor Tests///&///Method Tests////
    ////////////////////////////////////////////      
    
     /**
     * Test of FileTypeEnum() method, of class FileTypeEnum.
     */
    @Test
    public void test_constructor_0(){
	System.out.println("Testing FileTypeEnum's enumerations and getters.");
        //Tests if values are correct for all enumerations, and tests the getters as well. You cannot instantiate new inumerations.
        
        FileTypeEnum ave=FileTypeEnum.txt;
        assertEquals("txt",ave.getName());


        
    }    
        
    
    
    
    
    //////////////////
    //Method Tests////
    //////////////////      
    
    
    
    /**
     * Test of contains method, of class FileTypeEnum.
    */ 
    @Test
    public void test_Contains() {
        System.out.println("Testing FileTypeEnum's contains(String checkString)");
        String checkString = "";
        boolean expResult = false;
        boolean result = FileTypeEnum.contains(checkString);
        assertEquals(expResult, result);

        checkString="fractionMass";
        result = FileTypeEnum.contains(checkString);
        assertEquals(expResult, result);
        
        checkString="txt";
        expResult=true;
        result = FileTypeEnum.contains(checkString);
        assertEquals(expResult, result);        
    }
    
    
}
