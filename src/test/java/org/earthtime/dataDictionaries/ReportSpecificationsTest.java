/*
 * ReportSpecifications_Test_05052014Test.java
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

import org.earthtime.dataDictionaries.reportSpecifications.ReportSpecificationsUPb;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author patrickbrewer
 */
public class ReportSpecificationsTest {

    ////////////////////////////////////////////
    ////Constructor Tests///&///Method Tests////
    ////////////////////////////////////////////      
    /**
     * Test of ReportSpecificationsUPb() method, of class ReportSpecificationsUPb.
     */
    @Test
    public void test_constructor_0() {
        System.out.println("Testing ReportSpecifications's enumerations and getters.");
        //Tests if values are correct for all enumerations, and tests the getters as well. You cannot instantiate new inumerations.

        String[][] ave = ReportSpecificationsUPb.ReportCategory_Composition;

        assertEquals("", ave[3][0]);

        ave = ReportSpecificationsUPb.ReportCategory_CorrelationCoefficients;

        assertEquals("-", ave[5][1]);

        ave = ReportSpecificationsUPb.ReportCategory_Dates;

        assertEquals("", ave[2][0]);

        ave = ReportSpecificationsUPb.ReportCategory_Fraction;

        assertEquals("", ave[1][0]);

        ave = ReportSpecificationsUPb.ReportCategory_Fraction2;

        assertEquals("", ave[0][5]);

        ave = ReportSpecificationsUPb.ReportCategory_IsotopicRatios;

        assertEquals("", ave[2][0]);

        String[] sieg = ReportSpecificationsUPb.concUnits;

        assertEquals("\u0025", sieg[0]);
        assertEquals("\u2030", sieg[1]);
        assertEquals("ppm", sieg[2]);
        assertEquals("ppb", sieg[3]);

        sieg = ReportSpecificationsUPb.dateUnits;

        assertEquals("auto", sieg[0]);
        assertEquals("Ma", sieg[1]);
        assertEquals("ka", sieg[2]);

        sieg = ReportSpecificationsUPb.massUnits;

        assertEquals("g", sieg[0]);
        assertEquals("mg", sieg[1]);
        assertEquals("\u03bcg", sieg[2]);
        assertEquals("ng", sieg[3]);
        assertEquals("pg", sieg[4]);
        assertEquals("fg", sieg[5]);

        Map<String, String> ave1 = ReportSpecificationsUPb.reportTableFootnotes;

        assertEquals(true, ave1.containsKey("FN-1"));
        assertEquals(true, ave1.containsKey("FN-2"));
        assertEquals(true, ave1.containsKey("FN-3"));
        assertEquals(true, ave1.containsKey("FN-4"));
        assertEquals(true, ave1.containsKey("FN-5"));
        assertEquals(true, ave1.containsKey("FN-5noZircon"));
        assertEquals(true, ave1.containsKey("FN-5mixed"));
        assertEquals(true, ave1.containsKey("FN-5zircon"));
        assertEquals(true, ave1.containsKey("FN-6"));
        assertEquals(true, ave1.containsKey("FN-7"));
        assertEquals(true, ave1.containsKey("FN-8"));
        assertEquals(true, ave1.containsKey("FN-9"));
        assertEquals(true, ave1.containsKey("FN-10"));
        assertEquals(true, ave1.containsKey("FN-11"));
        assertEquals(true, ave1.containsKey("FN-12"));
        assertEquals(true, ave1.containsKey("FN-13"));
        assertEquals(true, ave1.containsKey("FN-14"));
        assertEquals(true, ave1.containsKey("FN-15"));
//        assertEquals(false, ave1.containsKey("FN-16"));

        ave1 = ReportSpecificationsUPb.unicodeConversionsFromXML;

        assertEquals(true, ave1.containsKey("MICROg"));
        assertEquals(true, ave1.containsKey("PERCENT"));
        assertEquals(true, ave1.containsKey("PERMILLE"));
        assertEquals(false, ave1.containsKey("FN-14"));

        ave1 = ReportSpecificationsUPb.unicodeConversionsToXML;

        assertEquals(true, ave1.containsKey("\u03bcg"));
        assertEquals(true, ave1.containsKey("\u0025"));
        assertEquals(true, ave1.containsKey("\u2030"));
        assertEquals(false, ave1.containsKey("FN-14"));

        ave1 = ReportSpecificationsUPb.unitsType;

        assertEquals(true, ave1.containsKey("g"));
        assertEquals(true, ave1.containsKey("mg"));
        assertEquals(true, ave1.containsKey("\u03bcg"));
        assertEquals(true, ave1.containsKey("ng"));
        assertEquals(true, ave1.containsKey("pg"));
        assertEquals(true, ave1.containsKey("fg"));
        assertEquals(true, ave1.containsKey("\u0025"));
        assertEquals(true, ave1.containsKey("\u2030"));
        assertEquals(true, ave1.containsKey("ppm"));
        assertEquals(true, ave1.containsKey("ppb"));
        assertEquals(true, ave1.containsKey("Ma"));
        assertEquals(true, ave1.containsKey("ka"));

    }

}
