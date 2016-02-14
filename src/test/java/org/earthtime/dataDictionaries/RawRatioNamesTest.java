/*
 * RawRatioNames_Test_05042014Test.java
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
public class RawRatioNamesTest {

    ////////////////////////////////////////////
    ////Constructor Tests///&///Method Tests////
    ////////////////////////////////////////////      
    /**
     * Test of RawRatioNames() method, of class RawRatioNames.
     */
    @Test
    public void test_constructor_0() {
        System.out.println("Testing RawRatioNames's enumerations and getters.");
        //Tests if values are correct for all enumerations, and tests the getters as well. You cannot instantiate new inumerations.

        RawRatioNames ave = RawRatioNames.r202_202w;
        assertEquals("r202_202w", ave.getName());

        ave = RawRatioNames.r206_204w;
        assertEquals("r206_204w", ave.getName());

        ave = RawRatioNames.r206_207w;
        assertEquals("r206_207w", ave.getName());

        ave = RawRatioNames.r206_238w;
        assertEquals("r206_238w", ave.getName());

        ave = RawRatioNames.r207_235w;
        assertEquals("r207_235w", ave.getName());

        ave = RawRatioNames.r208_204w;
        assertEquals("r208_204w", ave.getName());

        ave = RawRatioNames.r208_232w;
        assertEquals("r208_232w", ave.getName());

        String[] list = RawRatioNames.getNames();
        assertEquals("r206_238w", list[1]);
        assertEquals("r206_207w", list[0]);
        assertEquals("r206_204w", list[3]);
        assertEquals("r208_232w", list[2]);
        assertEquals("r208_204w", list[5]);
        assertEquals("r207_235w", list[6]);
        assertEquals("r202_202w", list[8]);

    }

    //////////////////
    //Method Tests////
    //////////////////      
    /**
     * Test of contains method, of class RawRatioNames.
     */
    @Test
    public void test_Contains() {
        System.out.println("Testing RawRatioNames's contains(String checkString)");
        String checkString = "";
        boolean expResult = false;
        boolean result = RawRatioNames.contains(checkString);
        assertEquals(expResult, result);

        checkString = "fractionMass";
        result = RawRatioNames.contains(checkString);
        assertEquals(expResult, result);

        checkString = "r202_202w";
        expResult = true;
        result = RawRatioNames.contains(checkString);
        assertEquals(expResult, result);
    }

}
