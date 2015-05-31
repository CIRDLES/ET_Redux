/*
 * MatrixSpecifications_Test_05042014Test.java
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

import org.junit.Assert;

import org.junit.Test;

/**
 *
 * @author patrickbrewer
 */
public class MatrixSpecificationsTest {

    ////////////////////////////////////////////
    ////Constructor Tests///&///Method Tests////
    ////////////////////////////////////////////      
    /**
     * Test of FileTypeEnum() method, of class FileTypeEnum.
     */
    @Test
    public void test_constructor_0() {
        System.out.println("Testing MatrixSpecifications's enumerations and getters.");
        //Tests if values are correct for all enumerations, and tests the getters as well. You cannot instantiate new inumerations.

        String[][] ave = MatrixSpecifications.mixed_205_233_235_NotZircon_NotFcU;
        try {
            String[][] result = MatrixSpecifications.getMatrixSpecsByName("");
            Assert.assertNull(result);
        } catch (java.lang.NullPointerException e1) {
        }
        String[][] result2 = MatrixSpecifications.getMatrixSpecsByName("mixed_205_233_235_NotZircon_NotFcU");

        Assert.assertArrayEquals(ave, result2);

        ave = MatrixSpecifications.mixed_205_233_236_NotZircon_NotFcU;
        result2 = MatrixSpecifications.getMatrixSpecsByName("mixed_205_233_236_NotZircon_NotFcU");
        Assert.assertArrayEquals(ave, result2);

        ave = MatrixSpecifications.mixed_205_235_NotZircon;
        result2 = MatrixSpecifications.getMatrixSpecsByName("mixed_205_235_NotZircon");
        Assert.assertArrayEquals(ave, result2);

        ave = MatrixSpecifications.mixed_205_235_Zircon;
        result2 = MatrixSpecifications.getMatrixSpecsByName("mixed_205_235_Zircon");
        Assert.assertArrayEquals(ave, result2);

    }

}
