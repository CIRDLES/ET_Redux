/*
 * RatioNamePrettyPrinter_Test_05052014Test.java
 *
 * The naming of this file is the name of the .java file being tested, combined
 * with test since this is the test file, combined with the creation date of
 * this file. The test at the end is so that the file runs from NetBeans when
 * testing an entire project.
 *
 * Created on May 5, 2014.
 *
 *
 *Version History:
 *May 5 2014: File Created. Constructor and method tests completed.
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
public class RatioNamePrettyPrinterTest {
    
    
    
    
    
    

    
    ////////////////////////////////////////////
    ////Constructor Tests///&///Method Tests////
    ////////////////////////////////////////////      
    
     /**
     * Test of RatioNamePrettyPrinter() method, of class RatioNamePrettyPrinter.
     */
    @Test
    public void test_constructor_0(){
	System.out.println("Testing RatioNamePrettyPrinter's enumerations and getters.");
        //Tests if values are correct for all enumerations, and tests the getters as well. You cannot instantiate new inumerations.
        
        String ave=RatioNamePrettyPrinter.makePrettyHTMLString("hello");
        assertEquals("<html>hello</html>",ave);


        
        
    }    
        
    
    
    
    
    
    
}
