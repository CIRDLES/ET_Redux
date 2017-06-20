/*
 * RadDates_Test_05042014Test.java
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
public class RadDatesTest {

    ////////////////////////////////////////////
    ////Constructor Tests///&///Method Tests////
    ////////////////////////////////////////////      
    /**
     * Test of RadDates() method, of class RadDates.
     */
    @Test
    public void test_constructor_0() {
        System.out.println("Testing RadDates's enumerations and getters.");
        //Tests if values are correct for all enumerations, and tests the getters as well. You cannot instantiate new inumerations.

        RadDates ave = RadDates.age206_238r;
        assertEquals("age206_238r", ave.getName());

        ave = RadDates.age206_238r_Th;
        assertEquals("age206_238r_Th", ave.getName());

        ave = RadDates.age207_206r;
        assertEquals("age207_206r", ave.getName());

        ave = RadDates.age207_206r_Pa;
        assertEquals("age207_206r_Pa", ave.getName());

        ave = RadDates.age207_206r_Th;
        assertEquals("age207_206r_Th", ave.getName());

        ave = RadDates.age207_206r_ThPa;
        assertEquals("age207_206r_ThPa", ave.getName());

        ave = RadDates.age207_235r;
        assertEquals("age207_235r", ave.getName());

        ave = RadDates.age207_235r_Pa;
        assertEquals("age207_235r_Pa", ave.getName());

        ave = RadDates.age208_232r;
        assertEquals("age208_232r", ave.getName());

        ave = RadDates.bestAge;
        assertEquals("bestAge", ave.getName());

        ave = RadDates.percentDiscordance;
        assertEquals("percentDiscordance", ave.getName());

        String[] list = RadDates.getNamesSorted();
        assertEquals("PbcCorr_UPb_Date", list[0]);
        assertEquals("age206_238_PbcCorr", list[1]);
        assertEquals("age206_238r", list[2]);
        assertEquals("age206_238r_Th", list[3]);
        assertEquals("age207_206_PbcCorr", list[4]);
        assertEquals("age207_206r", list[5]);
        assertEquals("age207_206r_Pa", list[6]);
        assertEquals("age207_206r_Th", list[7]);
        assertEquals("age207_206r_ThPa", list[8]);
        assertEquals("age207_235_PbcCorr", list[9]);
        assertEquals("age207_235r", list[10]);
        assertEquals("age207_235r_Pa", list[11]);
        assertEquals("age208_232_PbcCorr", list[12]);
        assertEquals("age208_232r", list[13]);
        assertEquals("bestAge", list[14]);
        assertEquals("bestAge_PbcCorr", list[15]);
        assertEquals("date", list[16]);
        assertEquals("dateBP", list[17]);
        assertEquals("dateCorr", list[18]);
        assertEquals("dateCorrBP", list[19]);
        assertEquals("percentDiscordance", list[20]);
        assertEquals("percentDiscordance_PbcCorr", list[21]);

    }

}
