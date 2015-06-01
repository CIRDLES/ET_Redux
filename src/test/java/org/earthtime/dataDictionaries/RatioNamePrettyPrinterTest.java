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

import static org.earthtime.dataDictionaries.RatioNamePrettyPrinter.makePrettyHTMLString;
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
    
    
    /**
     * Integration Test of class RatioNamePrettyPrinterTest
     * Assert the correct output of method makePrettyHTMLString
     */
    @Test
    public void testPrettyUrl() {
        //Asserting correct output
        assertEquals(makePrettyHTMLString("r206_204r"), "<html><sup>206</sup>Pb*/ <sup>204</sup>Pb*</html>");
        assertEquals(makePrettyHTMLString("r206_238r"), "<html><sup>206</sup>Pb*/ <sup>238</sup>U</html>");
        assertEquals(makePrettyHTMLString("r208_232r"), "<html><sup>208</sup>Pb*/ <sup>232</sup>Th</html>");
        assertEquals(makePrettyHTMLString("r238_235s"), "<html><sup>238</sup>U/ <sup>235</sup>U</html>");   
    }
}
