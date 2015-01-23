/*
 * AbstractRatiosDataModel_Test_02172014Test.java
 *
 * The naming of this file is the name of the .java file being tested, combined
 * with test since this is the test file, combined with the creation date of
 * this file. The test at the end is so that the file runs from NetBeans when
 * testing an entire project.
 *
 * Created on February 17, 2014.
 *
 *Version History:
 *February 17 2014 : File Created. Constructor tests completed.
 *March 28 2014: Method tests completed. Unfinished tests exist and are present
 *               at the bottom of this file. The list may not include all of the
 *               tests omitted.
 *
 *
 *Developed for the CIRDLES lab at the College of Charleston by Patrick Brewer.
 *
 *
 */
package org.earthtime.ratioDataModels;

import Jama.Matrix;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.matrices.matrixModels.AbstractMatrixModel;
import org.earthtime.matrices.matrixModels.CovarianceMatrixModel;
import org.earthtime.utilities.DateHelpers;
import org.junit.Assert;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author Patrick Brewer
 */
public class AbstractRatiosDataModelTest {

    /////////////////////////
    ////Constructor Tests////
    /////////////////////////
    /**
     * Test of AbstractRatiosDataModel() method, of class
     * AbstractRatiosDataModel.
     */
    @Test
    public void test_constructor_0() {
        System.out.println("Testing AbstractRatiosDataModel's AbstractRatiosDataModel()");
        //Tests if default values are correct. This omits some of the correlation and covariance models.
        AbstractRatiosDataModel instance = new AbstractTester();
        String expResult = "";
        String result = instance.XMLSchemaURL;
        assertEquals(expResult, result);

        expResult = "NONE";
        result = instance.getModelName();
        assertEquals(expResult, result);

        expResult = "NONE";
        result = instance.getLabName();
        assertEquals(expResult, result);

        Calendar currentDate = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        expResult = formatter.format(currentDate.getTime());
        result = instance.getDateCertified();
        assertEquals(expResult, result);

        expResult = "None";
        result = instance.getReference();
        assertEquals(expResult, result);

        expResult = "None";
        result = instance.getComment();
        assertEquals(expResult, result);

        int expectedResult = 1;
        int actualResult = instance.getVersionNumber();
        assertEquals(expectedResult, actualResult);

        expectedResult = 0;
        actualResult = instance.getMinorVersionNumber();
        assertEquals(expectedResult, actualResult);

        ValueModel[] expeResult = new ValueModel[0];
        ValueModel[] actuResult = instance.ratios;
        Assert.assertArrayEquals(expeResult, actuResult);

        Map expecResult = new HashMap();
        Map actuaResult = instance.getRhosVarUnct();
        assertEquals(expecResult, actuaResult);

        Boolean exResult = false;
        Boolean acResult = instance.immutable;
        assertEquals(exResult, acResult);
    }

    /**
     * Test of
     * AbstractRatiosDataModel(modelName,versionNumber,minorVersionNumber,labName,dateCertified,reference,comment)
     * method, of class AbstractRatiosDataModel.
     */
    @Test
    public void test_constructor_1() {
        System.out.println("Testing AbstractRatiosDataModel's AbstractRatiosDataModel(String modelName,int versionNumber,int minorVersionNumber,String labName,String dateCertified,String reference,String comment)");
        //Tests if default values are correct. This omits some of the correlation and covariance models.
        String modelName = "pkb";
        int versionNumber = 1337;
        int minorVersionNumber = 7777;
        String labName = "Satbatsu";
        String dateCertified = "0870-01-05";
        String reference = "Reference reporting in";
        String comment = "Comment reporting in";
        AbstractRatiosDataModel instance = new AbstractTester(modelName, versionNumber, minorVersionNumber, labName, dateCertified, reference, comment);
        String expResult = "";
        String result = instance.XMLSchemaURL;
        assertEquals(expResult, result);

        expResult = modelName;
        result = instance.getModelName();
        assertEquals(expResult, result);

        expResult = labName;
        result = instance.getLabName();
        assertEquals(expResult, result);

        expResult = dateCertified;
        result = instance.getDateCertified();
        assertEquals(expResult, result);

        expResult = reference;
        result = instance.getReference();
        assertEquals(expResult, result);

        expResult = comment;
        result = instance.getComment();
        assertEquals(expResult, result);

        int expectedResult = versionNumber;
        int actualResult = instance.getVersionNumber();
        assertEquals(expectedResult, actualResult);

        expectedResult = minorVersionNumber;
        actualResult = instance.getMinorVersionNumber();
        assertEquals(expectedResult, actualResult);

        ValueModel[] expeResult = new ValueModel[0];
        ValueModel[] actuResult = instance.ratios;
        Assert.assertArrayEquals(expeResult, actuResult);

        Map expecResult = new HashMap();
        Map actuaResult = instance.getRhosVarUnct();
        assertEquals(expecResult, actuaResult);

        Boolean exResult = false;
        Boolean acResult = instance.immutable;
        assertEquals(exResult, acResult);
    }

    ////////////////////
    ////Method Tests////
    ////////////////////
    /**
     * Test of copyModel method, of class AbstractRatiosDataModel.
     */
    @Test
    public void test_CopyModel() {
        System.out.println("Testing AbstractRatiosDataModel's copyModel()");
        //Default
        AbstractRatiosDataModel instance = new AbstractTester();
        AbstractRatiosDataModel result = instance.copyModel();
        instance.setModelName(instance.getModelName() + "-COPY");
        AbstractRatiosDataModel expResult = instance;
        assertEquals(expResult, result);
        //Specified
        instance = new AbstractTester("testModelName", 2, 3, "testLabName", "TestDateCertified", "Reference Reporting in", "Comment reporting in");
        result = instance.copyModel();
        instance.setModelName(instance.getModelName() + "-COPY");
        expResult = instance;
        assertEquals(expResult, result);
    }

    /**
     * Test of compareTo method, of class AbstractRatiosDataModel.
     */
    @Test
    public void test_CompareTo() {
        System.out.println("Testing AbstractRatiosDataModel's compareTo(AbstractRatiosDataModel model)");
        //Case of 0
        AbstractRatiosDataModel instance = new AbstractTester();
        AbstractRatiosDataModel model = instance;
        int expResult = 0;
        int result = instance.compareTo(model);
        assertEquals(expResult, result);
        //Testing for positive
        instance = new AbstractTester("testModelName", 2, 3, "testLabName", "TestDateCertified", "Reference Reporting in", "Comment reporting in");
        result = instance.compareTo(model);
        if (result <= 0) {
            fail("Result was supposed to be positive, and returned " + result);
        }
        //Testing for negative
        result = model.compareTo(instance);
        if (result >= 0) {
            fail("Result was supposed to be negative, and returned " + result);
        }
    }

    /**
     * Test of equals method, of class AbstractRatiosDataModel.
     */
    @Test
    public void test_Equals() {
        System.out.println("Testing AbstractRatiosDataModel's equals(AbstractRatiosDataModel model)");
        //Test Default&Specific False
        AbstractRatiosDataModel model = new AbstractTester("testModelName", 2, 3, "testLabName", "TestDateCertified", "Reference Reporting in", "Comment reporting in");
        AbstractRatiosDataModel instance = new AbstractTester();
        boolean expResult = false;
        boolean result = instance.equals(model);
        assertEquals(expResult, result);
        //Test Default False
        model = null;
        instance = new AbstractTester();
        expResult = false;
        result = instance.equals(model);
        assertEquals(expResult, result);
        //Test Default True
        model = new AbstractTester();
        instance = new AbstractTester();
        expResult = true;
        result = instance.equals(model);
        assertEquals(expResult, result);
        //Test Specific True
        model = new AbstractTester("testModelName", 2, 3, "testLabName", "TestDateCertified", "Reference Reporting in", "Comment reporting in");
        instance = new AbstractTester("testModelName", 2, 3, "testLabName", "TestDateCertified", "Reference Reporting in", "Comment reporting in");
        expResult = true;
        result = instance.equals(model);
        assertEquals(expResult, result);
    }

    /**
     * Test of hashCode method, of class AbstractRatiosDataModel.
     */
    @Test
    public void test_HashCode() {
        //Should always return 0
        System.out.println("Testing AbstractRatiosDataModel's hash_Code()");
        AbstractRatiosDataModel instance = new AbstractTester();
        int expResult = 0;
        int result = instance.hashCode();
        assertEquals(expResult, result);
    }

    /**
     * Test of initializeModel method, of class AbstractRatiosDataModel.
     */
    @Test
    public void test_InitializeModel_0args() {
        //Test to ensure initalization occurs
        System.out.println("Testing AbstractRatiosDataModel's initializeModel()");
        AbstractRatiosDataModel instance = new AbstractTester();
        Matrix expResult = null;
        Matrix result = instance.dataCovariancesVarUnct.getMatrix();
        Matrix result2 = instance.dataCorrelationsVarUnct.getMatrix();
        assertEquals(expResult, result);
        assertEquals(expResult, result2);
        instance.initializeModel();
        result = instance.dataCovariancesVarUnct.getMatrix();
        if (expResult == result) {
            fail("Matrix was not initialized");
        }
        result2 = instance.dataCorrelationsVarUnct.getMatrix();
        if (expResult == result2) {
            fail("Matrix was not initialized");
        }
    }

    /**
     * Test of refreshModel method, of class AbstractRatiosDataModel.
     */
    @Test
    public void test_RefreshModel() {
        System.out.println("Testing AbstractRatiosDataModel's refreshModel()");
        //Ensures refresh...refreshes. This actually forces a matrix generation which is what I test for the existence of.
        AbstractRatiosDataModel instance = new AbstractTester("testModelName", 2, 3, "testLabName", "TestDateCertified", "Reference Reporting in", "Comment reporting in");
        Matrix expResult = null;
        Matrix result = instance.dataCovariancesVarUnct.getMatrix();
        Matrix result2 = instance.dataCorrelationsVarUnct.getMatrix();
        assertEquals(expResult, result2);
        assertEquals(expResult, result);
        instance.dataCovariancesVarUnct = new CovarianceMatrixModel();
        result = instance.dataCovariancesVarUnct.getMatrix();
        result2 = instance.dataCorrelationsVarUnct.getMatrix();
        assertEquals(expResult, result2);
        assertEquals(expResult, result);
        instance.refreshModel();
        result = instance.dataCovariancesVarUnct.getMatrix();
        result2 = instance.dataCorrelationsVarUnct.getMatrix();
        if (expResult == result2) {
            fail("Matrix was not initialized");
        }
        if (expResult == result) {
            fail("Matrix was not initialized");
        }
    }

    /**
     * Test of isImmutable method, of class AbstractRatiosDataModel.
     */
    @Test
    public void test_IsImmutable() {
        System.out.println("Testing AbstractRatiosDataModel's isImmutable()");
        //False Default
        AbstractRatiosDataModel instance = new AbstractTester();
        boolean expResult = false;
        boolean result = instance.isImmutable();
        assertEquals(expResult, result);
        //True
        instance.immutable = true;
        expResult = true;
        result = instance.isImmutable();
        assertEquals(expResult, result);
        //Set False
        instance.immutable = false;
        expResult = false;
        result = instance.isImmutable();
        assertEquals(expResult, result);
    }

    /**
     * Test of setImmutable method, of class AbstractRatiosDataModel.
     */
    @Test
    public void test_SetImmutable() {
        System.out.println("Testing AbstractRatiosDataModel's setImmutable(boolean immutable)");
        //Set default false to true
        boolean immutable = true;
        AbstractRatiosDataModel instance = new AbstractTester();
        instance.setImmutable(immutable);
        boolean expResult = true;
        boolean result = instance.immutable;
        assertEquals(expResult, result);
        //Set default false to false
        immutable = false;
        instance = new AbstractTester();
        instance.setImmutable(immutable);
        expResult = false;
        result = instance.immutable;
        assertEquals(expResult, result);
        //Set from specified false to false
        instance = new AbstractTester("testModelName", 2, 3, "testLabName", "TestDateCertified", "Reference Reporting in", "Comment reporting in");
        instance.immutable = false;
        instance.setImmutable(immutable);
        result = instance.immutable;
        assertEquals(expResult, result);
        //Set from specified false to true
        immutable = true;
        instance = new AbstractTester("testModelName", 2, 3, "testLabName", "TestDateCertified", "Reference Reporting in", "Comment reporting in");
        instance.setImmutable(immutable);
        expResult = true;
        result = instance.immutable;
        assertEquals(expResult, result);
        //Set from specified true to true
        instance.setImmutable(immutable);
        result = instance.immutable;
        assertEquals(expResult, result);
        //Set from specified true to false
        immutable = false;
        instance = new AbstractTester("testModelName", 2, 3, "testLabName", "TestDateCertified", "Reference Reporting in", "Comment reporting in");
        instance.setImmutable(immutable);
        expResult = false;
        result = instance.immutable;
        assertEquals(expResult, result);
    }

    /**
     * Test of getMinorVersionNumber method, of class AbstractRatiosDataModel.
     */
    @Test
    public void test_GetMinorVersionNumber() {
        System.out.println("Testing AbstractRatiosDataModel's getMinorVersionNumber()");
        //Default
        AbstractRatiosDataModel instance = new AbstractTester();
        int expResult = 0;
        int result = instance.getMinorVersionNumber();
        assertEquals(expResult, result);
        //Specified from default
        expResult = 7;
        instance.minorVersionNumber = 7;
        result = instance.getMinorVersionNumber();
        assertEquals(expResult, result);
        //Specified from specified
        expResult = 21;
        instance.minorVersionNumber = 21;
        result = instance.getMinorVersionNumber();
        assertEquals(expResult, result);
    }

    /**
     * Test of setMinorVersionNumber method, of class AbstractRatiosDataModel.
     */
    @Test
    public void test_SetMinorVersionNumber() {
        System.out.println("Testing AbstractRatiosDataModel's setMinorVersionNumber(int minorVersionNumber)");
        //Default to specific and ensuring default is 0
        int minorVersionNumber = 20;
        int expResult = 0;
        AbstractRatiosDataModel instance = new AbstractTester();
        int result = instance.minorVersionNumber;
        assertEquals(expResult, result);
        instance.setMinorVersionNumber(minorVersionNumber);
        expResult = 20;
        result = instance.minorVersionNumber;
        assertEquals(expResult, result);
        //Specific to Specific
        minorVersionNumber = 11336;
        instance.setMinorVersionNumber(minorVersionNumber);
        expResult = 11336;
        result = instance.minorVersionNumber;
        assertEquals(expResult, result);
    }

    /**
     * Test of buildRhosMap method, of class AbstractRatiosDataModel.
     */
    @Test
    public void test_BuildRhosMap() {
        System.out.println("Testing AbstractRatiosDataModel's buildRhosMap()");
        //Checks that it is the same as default without a put
        AbstractRatiosDataModel instance = new AbstractTester();
        String before = instance.rhos.toString();
        instance.buildRhosMap();
        String after = instance.rhos.toString();
        assertEquals(before, after);
        //checks that it wipes from specified
        instance = new AbstractTester("testModelName", 2, 3, "testLabName", "TestDateCertified", "Reference Reporting in", "Comment reporting in");
        instance.rhos.put("hello", BigDecimal.ZERO);
        before = instance.rhos.toString();
        instance.buildRhosMap();
        after = instance.rhos.toString();
        if (before.equals(after)) {
            fail("The RhosMap before and after this build should not be the same");
        }
        //checks that it wipes from default
        instance = new AbstractTester();
        instance.rhos.put("hello", BigDecimal.ZERO);
        before = instance.rhos.toString();
        instance.buildRhosMap();
        after = instance.rhos.toString();
        if (before.equals(after)) {
            fail("The RhosMap before and after this build should not be the same");
        }
    }

    /**
     * Test of initializeDataCorrelations method, of class
     * AbstractRatiosDataModel.
     */
    @Test
    public void test_InitializeDataCorrelations() {
        System.out.println("Testing AbstractRatiosDataModel's initializeBothDataCorrelationM()");
        //Default No matrix becomes specified
        AbstractRatiosDataModel instance = new AbstractTester();
        Matrix before = instance.dataCorrelationsVarUnct.getMatrix();
        instance.initializeBothDataCorrelationM();
        Matrix after = instance.dataCorrelationsVarUnct.getMatrix();
        if (before == after) {
            fail("The matrix before and after should not be the same");
        }
        //Specified no matrix becomes specified
        instance = new AbstractTester("testModelName", 2, 3, "testLabName", "TestDateCertified", "Reference Reporting in", "Comment reporting in");
        before = instance.dataCorrelationsVarUnct.getMatrix();
        instance.initializeBothDataCorrelationM();
        after = instance.dataCorrelationsVarUnct.getMatrix();
        if (before == after) {
            fail("The matrix before and after should not be the same");
        }
        //Specified Matrix is replaced with another one
        before = instance.dataCorrelationsVarUnct.getMatrix();
        instance.initializeBothDataCorrelationM();
        after = instance.dataCorrelationsVarUnct.getMatrix();
        if (before == after) {
            fail("The matrix before and after should not be the same");
        }
    }

    /**
     * Test of initializeModel method, of class AbstractRatiosDataModel.
     */
    @Test
    public void test_InitializeModel_ValueModelArr_Map() {
        System.out.println("Testing AbstractRatiosDataModel's initializeModel(ValueModel[] dataIncoming,Map<String,BigDecimal> rhos)");
        //Tests if Rhos is null.
        //Tests and ensures dataCovariance and dataCorrelation matrixes are different before and after.       
        //Tests if update occurs.   

        //Setup dataIncoming to be passed in
        ValueModel[] dataIncoming = new ValueModel[3];
        ValueModel instance0 = new ValueModel("r207_339", new BigDecimal("-12.34567890"), "PCT", new BigDecimal(".9876543210"), BigDecimal.ZERO);
        ValueModel instance1 = new ValueModel("pkb", new BigDecimal("2030"), "ABS", new BigDecimal("6060"), BigDecimal.ZERO);
        ValueModel instance2 = new ValueModel();
        dataIncoming[0] = instance2;
        dataIncoming[1] = instance1;
        dataIncoming[2] = instance0;

        //Setup Rhos to be passed in
        AbstractRatiosDataModel instance25 = new AbstractTester("testModelName1", 3, 4, "testLabName1", "TestDateCertified1", "Reference Reporting in1", "Comment reporting in1");
        instance25.rhos.put("five", new BigDecimal("5"));
        instance25.rhos.put("seven", new BigDecimal("7"));
        instance25.rhos.put("six", new BigDecimal("6"));
        instance25.rhos.put("eight", new BigDecimal("8"));
        instance25.rhos.put("zero", new BigDecimal("0.000"));
        instance25.rhos.put("zeroo", BigDecimal.ZERO);
        Map<String, BigDecimal> rhos = instance25.getRhosVarUnct();

        //Setup instance to be tested, give it a rhos
        AbstractRatiosDataModel instance = new AbstractTester("testModelName", 2, 3, "testLabName", "TestDateCertified", "Reference Reporting in", "Comment reporting in");
        instance.rhos.put("zero", new BigDecimal("0.001"));
        instance.rhos.put("hi", new BigDecimal(".666"));
        instance.rhos.put("sup", new BigDecimal("1.666"));
        instance.rhos.put("hello there", new BigDecimal("-1.666"));
        instance.rhos.put("what's up", new BigDecimal("2.666"));

        Matrix expResult = instance.dataCovariancesVarUnct.getMatrix();
        Matrix expResult2 = instance.dataCorrelationsVarUnct.getMatrix();

        String before = instance.rhos.toString();

        //Actual Method Call
        instance.initializeModel(dataIncoming, rhos,null);

        //Asserts for update testing
        String after = instance.rhos.toString();
        if (before.equals(after)) {
            fail("Rhos of instance should not be the same after update");
        }
        //Asserts for ensuring dataCovariancesVarUnct and DataCorrelations have different matrixes no matter what
        Matrix result = instance.dataCovariancesVarUnct.getMatrix();
        Matrix result2 = instance.dataCorrelationsVarUnct.getMatrix();
        if (expResult2 == result2) {
            fail("dataCorrelations should not be the same before and after");
        }
        if (expResult == result) {
            fail("dataCovariancesVarUnct should not be the same before and after");
        }
        //Asserts for Rhos Null test
        rhos = null;
        instance.rhos.put("For Null Test", BigDecimal.ZERO);
        String expResult3 = instance.rhos.toString();
        instance.initializeModel(dataIncoming, rhos,null);
        String result3 = instance.rhos.toString();
        if (expResult3.equals(result3)) {
            fail("Rhos should not be the same after being wiped by BuildRhosMap");
        }
    }

    /**
     * Test of copyRhosFromCorrelationMatrix method, of class
     * AbstractRatiosDataModel.
     */
    @Test
    public void test_CopyRhosFromCorrelationMatrix() {
        System.out.println("Testing AbstractRatiosDataModel's copyBothRhosFromEachCorrelationM()");
        //If a dataCorrelation's matrix is changed and this is called, change the rhos, if there is a rhos in the first place. Otherwise do not change it.
        //If a dataCorrelation's matrix is empty but there isa rhos, the rhos should remain equal.
        //ArrayIndexOutOfBoundsException should be thrown if "__" is not in the name of a rhos entry.

        AbstractRatiosDataModel instance = new AbstractTester();
        AbstractRatiosDataModel instance2 = new AbstractTester();

        //Create Matrix to be Set
        Matrix ma = new Matrix(2, 2);
        ma.set(0, 0, 0.0);
        ma.set(0, 1, 0.1);
        ma.set(1, 0, 1.0);
        ma.set(1, 1, 1.1);
        instance.dataCorrelationsVarUnct.setMatrix(ma);

        instance.rhos.put("two here number__zwei", new BigDecimal("2"));
        instance.rhos.put("three here numba__drei", new BigDecimal("3"));

        String before = instance.rhos.toString();

        //Actual Call
        instance.copyBothRhosFromEachCorrelationM();
        String after = instance.rhos.toString();

        //Working Correctly Assertion
        if (before.equals(after)) {
            fail("Rhos should not be the same before and after");
        }

        //Nothing should happen if no rhos exists
        String b4 = instance2.rhos.toString();
        instance2.dataCorrelationsVarUnct.setMatrix(ma);
        instance2.copyBothRhosFromEachCorrelationM();
        String afta = instance2.rhos.toString();
        assertEquals(b4, afta);

        //Nothing should happen if no matrix or no rhos exist
        AbstractRatiosDataModel instance3 = new AbstractTester();
        //Nothing should happen if no rhos exists
        String b3f0r3 = instance3.rhos.toString();
        instance3.copyBothRhosFromEachCorrelationM();
        String aft3r = instance3.rhos.toString();
        assertEquals(b3f0r3, aft3r);

        //Should error if "__" is not in the name of a rhos entry
        instance2.rhos.put("hello", BigDecimal.ZERO);
        try {
            instance2.copyBothRhosFromEachCorrelationM();
            fail("This should have errored due to the rhos name not containing '__' in it");
        } catch (java.lang.ArrayIndexOutOfBoundsException e) {
        }

        //Nothing should happen if no Matrix exists in DataCorrelations
        AbstractRatiosDataModel instance0 = new AbstractTester();
        instance0.rhos.put("Hello there friend how__are you", BigDecimal.ZERO);
        String bef = instance0.rhos.toString();
        instance0.copyBothRhosFromEachCorrelationM();
        String af = instance0.rhos.toString();
        assertEquals(bef, af);
    }

    /**
     * Test of getModelName method, of class AbstractRatiosDataModel.
     */
    @Test
    public void test_GetModelName() {
        System.out.println("Testing AbstractRatiosDataModel's getModelName()");
        //Default is correct
        AbstractRatiosDataModel instance = new AbstractTester();
        String expResult = "NONE";
        String result = instance.getModelName();
        assertEquals(expResult, result);
        //Specified is correct
        instance.modelName = "hihihi";
        result = instance.getModelName();
        expResult = "hihihi";
        assertEquals(expResult, result);
    }

    /**
     * Test of getVersionNumber method, of class AbstractRatiosDataModel.
     */
    @Test
    public void test_GetVersionNumber() {
        System.out.println("Testing AbstractRatiosDataModel's getVersionNumber()");
        //Default
        AbstractRatiosDataModel instance = new AbstractTester();
        int expResult = 1;
        int result = instance.getVersionNumber();
        assertEquals(expResult, result);
        //Specified
        instance.versionNumber = 9;
        expResult = 9;
        result = instance.getVersionNumber();
        assertEquals(expResult, result);
    }

    /**
     * Test of getLabName method, of class AbstractRatiosDataModel.
     */
    @Test
    public void test_GetLabName() {
        System.out.println("Testing AbstractRatiosDataModel's getLabName()");
        //Default
        AbstractRatiosDataModel instance = new AbstractTester();
        String expResult = "NONE";
        String result = instance.getLabName();
        assertEquals(expResult, result);
        //Specified
        expResult = "lab Z";
        instance.labName = "lab Z";
        result = instance.getLabName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDateCertified method, of class AbstractRatiosDataModel.
     */
    @Test
    public void test_GetDateCertified() {
        System.out.println("Testing AbstractRatiosDataModel's getDateCertified()");
        //Default
        AbstractRatiosDataModel instance = new AbstractTester();
        String expResult = DateHelpers.defaultEarthTimeDateString();
        String result = instance.getDateCertified();
        assertEquals(expResult, result);
        //Specified
        instance.dateCertified = "5/1/2014";
        expResult = "5/1/2014";
        result = instance.getDateCertified();
        assertEquals(expResult, result);
    }

    /**
     * Test of getNameAndVersion method, of class AbstractRatiosDataModel.
     */
    @Test
    public void test_GetNameAndVersion() {
        System.out.println("Testing AbstractRatiosDataModel's getNameAndVersion()");
        //Default
        AbstractRatiosDataModel instance = new AbstractTester();
        String expResult = "NONE v.1.0";
        String result = instance.getNameAndVersion();
        assertEquals(expResult, result);
        //Default Name, specified version
        instance.versionNumber = 9;
        expResult = "NONE v.9.0";
        result = instance.getNameAndVersion();
        assertEquals(expResult, result);
        //Specified Name, specified version
        instance.modelName = "experiment";
        expResult = "experiment v.9.0";
        result = instance.getNameAndVersion();
        assertEquals(expResult, result);
        //Specified name, default version
        AbstractRatiosDataModel instance0 = new AbstractTester();
        instance0.modelName = "experiment";
        result = instance0.getNameAndVersion();
        expResult = "experiment v.1.0";
        assertEquals(expResult, result);
    }

    /**
     * Test of makeNameAndVersion method, of class AbstractRatiosDataModel.
     */
    @Test
    public void test_MakeNameAndVersion() {
        System.out.println("Testing AbstractRatiosDataModel's makeNameAndVersion(String name,int version, int minorVersionNumber)");
        //Default
        String name = "";
        int version = 0;
        int minorVersionNumber = 0;
        String expResult = " v.0.0";
        String result = AbstractRatiosDataModel.makeNameAndVersion(name, version, minorVersionNumber);
        assertEquals(expResult, result);
        //Specified Name
        name = "Experiment";
        expResult = "Experiment v.0.0";
        result = AbstractRatiosDataModel.makeNameAndVersion(name, version, minorVersionNumber);
        assertEquals(expResult, result);
        //Specified Name and version
        version = 9;
        expResult = "Experiment v.9.0";
        result = AbstractRatiosDataModel.makeNameAndVersion(name, version, minorVersionNumber);
        assertEquals(expResult, result);
        //Specified Name and version and version number
        minorVersionNumber = 9;
        expResult = "Experiment v.9.9";
        result = AbstractRatiosDataModel.makeNameAndVersion(name, version, minorVersionNumber);
        assertEquals(expResult, result);
        //Due to there never actually having the default values used (as all parameters are passed in), no more testing is required.
        //I have kept unneeded test cses as they have already been built and add more security while consuming mininmal resources.
    }

    /**
     * Test of getRhos method, of class AbstractRatiosDataModel.
     */
    @Test
    public void test_GetRhos() {
        System.out.println("Testing AbstractRatiosDataModel's getRhosVarUnct()");
        //Default
        AbstractRatiosDataModel instance = new AbstractTester();
        String expResult = "{}";
        String result = instance.getRhosVarUnct().toString();
        assertEquals(expResult, result);
        //Specified
        instance.rhos.put("zero", BigDecimal.ZERO);
        expResult = "{zero=0}";
        result = instance.getRhosVarUnct().toString();
        assertEquals(expResult, result);
        instance.rhos.put("two", new BigDecimal("2"));
        instance.rhos.put("three", new BigDecimal("3"));
        expResult = "{zero=0, two=2, three=3}";
        result = instance.getRhosVarUnct().toString();
        assertEquals(expResult, result);
    }

    /**
     * Test of getReference method, of class AbstractRatiosDataModel.
     */
    @Test
    public void test_GetReference() {
        System.out.println("Testing AbstractRatiosDataModel's getReference()");
        //Default
        AbstractRatiosDataModel instance = new AbstractTester();
        String expResult = "None";
        String result = instance.getReference();
        assertEquals(expResult, result);
        //Specific
        instance.reference = "Ave";
        expResult = "Ave";
        result = instance.getReference();
        assertEquals(expResult, result);
    }

    /**
     * Test of getComment method, of class AbstractRatiosDataModel.
     */
    @Test
    public void test_GetComment() {
        System.out.println("Testing AbstractRatiosDataModel's getComment()");
        //Default
        AbstractRatiosDataModel instance = new AbstractTester();
        String expResult = "None";
        String result = instance.getComment();
        assertEquals(expResult, result);
        //Specific
        expResult = "Ave";
        instance.comment = "Ave";
        result = instance.getComment();
        assertEquals(expResult, result);
    }

    /**
     * Test of setModelName method, of class AbstractRatiosDataModel.
     */
    @Test
    public void test_SetModelName() {
        System.out.println("Testing AbstractRatiosDataModel's setModelName(String name)");
        String modelName = "Ave";
        AbstractRatiosDataModel instance = new AbstractTester();
        String result = instance.getModelName();
        instance.setModelName(modelName);
        String result0 = instance.getModelName();
        if (result.equals(result0)) {
            fail("The name of the model should not be the same after a setter is called using a different name");
        }
        instance.setModelName(modelName);
        String result1 = instance.getModelName();
        assertEquals(result0, result1);
    }

    /**
     * Test of setReference method, of class AbstractRatiosDataModel.
     */
    @Test
    public void test_SetReference() {
        System.out.println("Testing AbstractRatiosDataModel's setReference(String reference)");
        String reference = "Ave";
        AbstractRatiosDataModel instance = new AbstractTester();
        String result = instance.getReference();
        instance.setReference(reference);
        String result0 = instance.getReference();
        if (result.equals(result0)) {
            fail("The reference should not be the same before and after a setter is called using a different string");
        }
        instance.setReference(reference);
        String result1 = instance.getReference();
        assertEquals(result0, result1);
    }

    /**
     * Test of setComment method, of class AbstractRatiosDataModel.
     */
    @Test
    public void test_SetComment() {
        System.out.println("Testing AbstractRatiosDataModel's setComment(String comment)");
        String comment = "Ave";
        AbstractRatiosDataModel instance = new AbstractTester();
        String result = instance.getComment();
        instance.setComment(comment);
        String result0 = instance.getComment();
        if (result.equals(result0)) {
            fail("The comment should not be the same before and after a setter is called using a different string");
        }
        instance.setComment(comment);
        String result1 = instance.getComment();
        assertEquals(result0, result1);
    }

    /**
     * Test of setVersionNumber method, of class AbstractRatiosDataModel.
     */
    @Test
    public void test_SetVersionNumber() {
        System.out.println("Testing AbstractRatiosDataModel's setVersionNumber(int versionNumber)");
        int versionNumber = 669;
        AbstractRatiosDataModel instance = new AbstractTester();
        int result = instance.getVersionNumber();
        instance.setVersionNumber(versionNumber);
        int result0 = instance.getVersionNumber();
        if (result == result0) {
            fail("The version number should not be the same before and after a setter is called using a different string");
        }
        instance.setVersionNumber(versionNumber);
        int result1 = instance.getVersionNumber();
        assertEquals(result0, result1);
    }

    /**
     * Test of setLabName method, of class AbstractRatiosDataModel.
     */
    @Test
    public void test_SetLabName() {
        System.out.println("Testing AbstractRatiosDataModel's setLabName(String labName)");
        String labName = "Ave";
        AbstractRatiosDataModel instance = new AbstractTester();
        String result = instance.getLabName();
        instance.setLabName(labName);
        String result0 = instance.getLabName();
        if (result.equals(result0)) {
            fail("The labName should not be the same before and after a setter is called using a different string");
        }
        instance.setLabName(labName);
        String result1 = instance.getLabName();
        assertEquals(result0, result1);
    }

    /**
     * Test of setDateCertified method, of class AbstractRatiosDataModel.
     */
    @Test
    public void test_SetDateCertified() {
        System.out.println("Testing AbstractRatiosDataModel's setDateCertified(String dateCertified)");
        String datecertified = "Ave";
        AbstractRatiosDataModel instance = new AbstractTester();
        String result = instance.getDateCertified();
        instance.setDateCertified(datecertified);
        String result0 = instance.getDateCertified();
        if (result.equals(result0)) {
            fail("The datecertified should not be the same before and after a setter is called using a different string");
        }
        instance.setDateCertified(datecertified);
        String result1 = instance.getDateCertified();
        assertEquals(result0, result1);
    }

    /**
     * Test of setRatios method, of class AbstractRatiosDataModel.
     */
    @Test
    public void test_SetRatios() {
        System.out.println("Testing AbstractRatiosDataModel's setRatios(ValueModel[] ratios)");
        ValueModel[] ratios = new ValueModel[1];
        AbstractRatiosDataModel instance = new AbstractTester();
        ValueModel[] result = instance.ratios;
        ratios[0] = new ValueModel();
        instance.setRatios(ratios);
        ValueModel[] result0 = instance.ratios;
        if (Arrays.equals(result, result0)) {
            fail("Ratios should not be the same before and after you set it with different parameters");
        }
        instance.setRatios(ratios);
        ValueModel[] result1 = instance.ratios;
        Assert.assertArrayEquals(result0, result1);
    }

    /**
     * Test of setRhosVarUnct method, of class AbstractRatiosDataModel.
     */
    @Test
    public void test_SetRhos() {
        System.out.println("Testing AbstractRatiosDataModel's setRhos(Map<String, BigDecimal> rhos)");
        AbstractRatiosDataModel instance0 = new AbstractTester();
        instance0.rhos.put("lol", BigDecimal.ZERO);
        Map<String, BigDecimal> rhos = instance0.rhos;
        AbstractRatiosDataModel instance = new AbstractTester();
        Map<String, BigDecimal> result = instance.getRhosVarUnct();
        instance.setRhosVarUnct(rhos);
        Map<String, BigDecimal> result0 = instance.getRhosVarUnct();
        if (result.equals(result0)) {
            fail("Rhos should not be the same before and after you set it with different parameters");
        }
        instance.setRhosVarUnct(rhos);
        Map<String, BigDecimal> result1 = instance.getRhosVarUnct();
        assertEquals(result0, result1);
    }

    /**
     * Test of toString method, of class AbstractRatiosDataModel.
     */
    @Test
    public void test_ToString() {
        System.out.println("Testing AbstractRatiosDataModel's toString()");
        //Default
        AbstractRatiosDataModel instance = new AbstractTester();
        String expResult = "NONE v.1.0";
        String result = instance.toString();
        assertEquals(expResult, result);
        //Specified
        instance.modelName = "Hi";
        instance.versionNumber = 2;
        instance.minorVersionNumber = 3;
        expResult = "Hi v.2.3";
        result = instance.toString();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDataCovariancesVarUnct method, of class AbstractRatiosDataModel.
     *
     */
    @Test
    public void test_GetDataCovariances() {
        System.out.println("Testing AbstractRatiosDataModel's getDataCovariancesVarUnct()");
        //Default Tests if Same
        AbstractRatiosDataModel instance = new AbstractTester();
        String expResult = "MATRIX#=Covariances    \n";
        AbstractMatrixModel result00 = instance.getDataCovariancesVarUnct();
        String result = result00.ToStringWithLabels();
        assertEquals(expResult, result);

        //Specific using two methods of ensuring it changed
        Matrix ma = new Matrix(2, 2);
        ma.set(0, 0, 0.0);
        ma.set(0, 1, 0.1);
        ma.set(1, 0, 1.0);
        ma.set(1, 1, 1.1);
        String[] rows = {"row0", "row1", "row2"};
        Map<Integer, String> col = new HashMap<>();
        col.put(0, "col0");
        col.put(1, "col1");
        col.put(2, "col2");
        instance.dataCovariancesVarUnct.setRows(rows);
        instance.dataCovariancesVarUnct.setCols(col);
        instance.dataCovariancesVarUnct.setMatrix(ma);
        AbstractMatrixModel result01 = instance.getDataCovariancesVarUnct();
        result = result01.ToStringWithLabels();
        if (expResult.equals(result)) {
            fail("DataCovariances should not be the same before and after changing");
        }

        //Should Stay the Same...checked with two different methods
        col.put(0, "col0");
        col.put(1, "col1");
        col.put(2, "col2");
        instance.dataCovariancesVarUnct.setRows(rows);
        instance.dataCovariancesVarUnct.setCols(col);
        instance.dataCovariancesVarUnct.setMatrix(ma);
        AbstractMatrixModel result02 = instance.getDataCovariancesVarUnct();
        String result3 = result02.ToStringWithLabels();
        assertEquals(result, result3);
    }

    /**
     * Test of getDataCorrelations method, of class AbstractRatiosDataModel.
     */
    @Test
    public void test_GetDataCorrelations() {
        System.out.println("Testing AbstractRatiosDataModel's getDataCorrelations()");
        //Default Tests if Same
        AbstractRatiosDataModel instance = new AbstractTester();
        String expResult = "MATRIX#=Correlations   \n";
        AbstractMatrixModel result00 = instance.getDataCorrelationsVarUnct();
        String result = result00.ToStringWithLabels();
        assertEquals(expResult, result);

        //Specific using two methods of ensuring it changed
        Matrix ma = new Matrix(2, 2);
        ma.set(0, 0, 0.0);
        ma.set(0, 1, 0.1);
        ma.set(1, 0, 1.0);
        ma.set(1, 1, 1.1);
        String[] rows = {"row0", "row1", "row2"};
        Map<Integer, String> col = new HashMap<>();
        col.put(0, "col0");
        col.put(1, "col1");
        col.put(2, "col2");
        instance.dataCorrelationsVarUnct.setRows(rows);
        instance.dataCorrelationsVarUnct.setCols(col);
        instance.dataCorrelationsVarUnct.setMatrix(ma);
        AbstractMatrixModel result01 = instance.getDataCorrelationsVarUnct();
        result = result01.ToStringWithLabels();
        if (expResult.equals(result)) {
            fail("DataCorrelations should not be the same before and after changing");
        }

        //Should Stay the Same...checked with two different methods
        col.put(0, "col0");
        col.put(1, "col1");
        col.put(2, "col2");
        instance.dataCorrelationsVarUnct.setRows(rows);
        instance.dataCorrelationsVarUnct.setCols(col);
        instance.dataCorrelationsVarUnct.setMatrix(ma);
        AbstractMatrixModel result02 = instance.getDataCorrelationsVarUnct();
        String result3 = result02.ToStringWithLabels();
        assertEquals(result, result3);
    }

    /**
     * Test of getData method, of class AbstractRatiosDataModel.
     */
    @Test
    public void test_GetData() {
        System.out.println("Testing AbstractRatiosDataModel's getData()");
        //Default
        AbstractRatiosDataModel instance = new AbstractTester();
        AbstractRatiosDataModel instance0 = new AbstractTester();
        ValueModel[] expResult = instance.ratios;
        ValueModel[] result = instance.getData();
        assertArrayEquals(expResult, result);
        //Specified
        expResult = instance0.ratios;
        instance.ratios = expResult;
        result = instance.getData();
        Assert.assertArrayEquals(expResult, result);
    }

    /**
     * Test of cloneData method, of class AbstractRatiosDataModel.
     */
    @Test
    public void test_CloneData() {
        System.out.println("Testing AbstractRatiosDataModel's cloneData()");
        //Default
        AbstractRatiosDataModel instance = new AbstractTester();
        ValueModel[] expResult = new ValueModel[0];
        ValueModel[] result = instance.cloneData();
        assertArrayEquals(expResult, result);
        //Specified
        instance.ratios = new ValueModel[0];
        ValueModel[] result0 = instance.cloneData();
        if (result == result0) {
            fail("Shouldn't be the same after setting, should have cloned a different array");
        }
    }

    /**
     * Test of getReduxLabDataElementName method, of class
     * AbstractRatiosDataModel.
     */
    @Test
    public void test_GetReduxLabDataElementName() {
        System.out.println("Testing AbstractRatiosDataModel's getReduxLabDataElementName()");
        //
        AbstractRatiosDataModel instance = new AbstractTester();
        String expResult = "NONE v.1.0";
        String result = instance.getReduxLabDataElementName();
        assertEquals(expResult, result);
        // Specified
        instance.modelName = "hi";
        instance.versionNumber = 3;
        expResult = "hi v.3.0";
        result = instance.getReduxLabDataElementName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDatumByName method, of class AbstractRatiosDataModel.
     */
    @Test
    public void test_GetDatumByName() {
        System.out.println("Testing AbstractRatiosDataModel's getDatumByName(String datumName)");
        //Default
        String datumName = "r207_339";
        AbstractRatiosDataModel instance = new AbstractTester();
        String expResult = "r207_339";
        ValueModel result = instance.getDatumByName(datumName);
        assertEquals(expResult, result.getName());
        //Specified
        ValueModel instance0 = new ValueModel("r207_339", new BigDecimal("-12.34567890"), "PCT", new BigDecimal(".9876543210"), BigDecimal.ZERO);
        ValueModel[] newRatios = new ValueModel[1];
        newRatios[0] = instance0;
        instance.ratios = newRatios;
        ValueModel expResult0 = instance0;
        result = instance.getDatumByName(datumName);
        assertEquals(expResult0, result);
    }

    /**
     * Test of cloneRhos method, of class AbstractRatiosDataModel.
     */
    @Test
    public void test_CloneRhos() {
        System.out.println("Testing AbstractRatiosDataModel's cloneRhos()");
        AbstractRatiosDataModel instance = new AbstractTester();
        Map<String, BigDecimal> expResult = new HashMap<>();
        Map<String, BigDecimal> result = instance.cloneRhosVarUnct();
        assertEquals(expResult, result);
        //Specified
        instance.rhos.put("zero", BigDecimal.ZERO);
        instance.rhos.put("three", new BigDecimal("3"));
        instance.rhos.put("one", new BigDecimal("1"));
        expResult.put("zero", BigDecimal.ZERO);
        expResult.put("three", new BigDecimal("3"));
        expResult.put("one", new BigDecimal("1"));
        result = instance.cloneRhosVarUnct();
        assertEquals(expResult, result);
    }

    /**
     * Test of getRhoByName method, of class AbstractRatiosDataModel.
     *
     */
    @Test
    public void test_GetRhoByName() {
        System.out.println("Testing AbstractRatiosDataModel's getRhoByName(String name)");
        String name = "three";
        AbstractRatiosDataModel instance = new AbstractTester();
        BigDecimal expResult = null;
        BigDecimal result = instance.getRhoByName(name);
        assertEquals(expResult, result);
        //Specified
        instance.rhos.put("zero", BigDecimal.ZERO);
        instance.rhos.put("three", new BigDecimal("3"));
        instance.rhos.put("one", new BigDecimal("1"));
        expResult = new BigDecimal("3");
        result = instance.getRhoByName(name);
        assertEquals(expResult, result);
    }

    /**
     * Test of getRhosForXMLSerialization method, of class
     * AbstractRatiosDataModel.
     */
    @Test
    public void test_GetRhosForXMLSerialization() {
        System.out.println("Testing AbstractRatiosDataModel's getRhosForXMLSerialization()");
        AbstractRatiosDataModel instance = new AbstractTester();
        Map<String, BigDecimal> expResult = new HashMap<>();
        Map<String, BigDecimal> result = instance.getRhosVarUnctForXMLSerialization();
        assertEquals(expResult, result);
        //Specified
        instance.rhos.put("zero", BigDecimal.ZERO);
        instance.rhos.put("three", new BigDecimal("3"));
        instance.rhos.put("one", new BigDecimal("1"));
        expResult.put("three", new BigDecimal("3"));
        expResult.put("one", new BigDecimal("1"));
        result = instance.getRhosVarUnctForXMLSerialization();
        assertEquals(expResult, result);
    }

    /**
     * Test of getCorrelationCoefficientByName method, of class
     * AbstractRatiosDataModel.
     */
    @Test
    public void test_GetCorrelationCoefficientByName() {
        System.out.println("Testing AbstractRatiosDataModel's getCorrelationCoefficientByName(String coeffName)");
        //Default
        String coeffName = "default";
        AbstractRatiosDataModel instance = new AbstractTester();
        String expResult = "default";
        BigDecimal expResult0 = new BigDecimal("0");
        ValueModel result = instance.getRhoVarUnctByName(coeffName);
        String result0 = result.getName();
        BigDecimal result1 = result.getValue();
        assertEquals(expResult, result0);
        assertEquals(expResult0, result1);
        //Specified
        coeffName = "hell";
        BigDecimal value = new BigDecimal("9.99");
        instance.rhos.put(coeffName, value);
        ValueModel result4 = instance.getRhoVarUnctByName("hell");
        String result2 = result4.getName();
        BigDecimal result3 = result4.getValue();
        assertEquals(coeffName, result2);
        assertEquals(value, result3);
    }

    /**
     * Test of generateCovarianceMFromCorrelationM method, of class
     * AbstractRatiosDataModel.
     */
    @Test
    public void test_GenerateCovarianceMFromCorrelationM() {
        System.out.println("Testing AbstractRatiosDataModel's generateCovarianceMFromCorrelationM()");
        //covariances empty correlation empty, covariance should remain empty
        AbstractRatiosDataModel instance = new AbstractTester();
        instance.dataCorrelationsVarUnct.initializeMatrix();
        instance.dataCovariancesVarUnct.initializeMatrix();
        String before = (instance.dataCovariancesVarUnct.ToStringWithLabels());
        instance.generateBothUnctCovarianceMFromEachUnctCorrelationM();
        String after = (instance.dataCovariancesVarUnct.ToStringWithLabels());
        assertEquals(before, after);

        //covariances specified and correlation empty,covariance should be empty
        Matrix ma = new Matrix(2, 2);
        ma.set(0, 0, 2);
        ma.set(0, 1, 2);
        ma.set(1, 0, 1);
        ma.set(1, 1, 1);
        instance.dataCovariancesVarUnct.setMatrix(ma);
        String[] hello = new String[2];
        hello[0] = "hello";
        hello[1] = "there";
        instance.dataCovariancesVarUnct.setRows(hello);
        instance.generateBothUnctCovarianceMFromEachUnctCorrelationM();
        String after0 = instance.dataCovariancesVarUnct.ToStringWithLabels();
        assertEquals(after, after0);

        //covariances empty correlation specified, covariance should specify
        instance.dataCovariancesVarUnct = new CovarianceMatrixModel();
        instance.dataCovariancesVarUnct.initializeMatrix();
        instance.dataCorrelationsVarUnct.setMatrix(ma);
        instance.dataCorrelationsVarUnct.setRows(hello);
        before = instance.dataCovariancesVarUnct.ToStringWithLabels();
        instance.generateBothUnctCovarianceMFromEachUnctCorrelationM();
        after = instance.dataCovariancesVarUnct.ToStringWithLabels();
        if (before.equals(after)) {
            fail("Before should not be the same as after, should go from empty to specified");
        }

        //covariances specified and correlation specified, covariance specifications should be overwritten
        hello[1] = "buddy";
        instance.dataCovariancesVarUnct.setRows(hello);
        before = instance.dataCovariancesVarUnct.ToStringWithLabels();
        after0 = after;
        instance.generateBothUnctCovarianceMFromEachUnctCorrelationM();
        after = instance.dataCovariancesVarUnct.ToStringWithLabels();
        if (before.equals(after)) {
            fail("Before should not be the same as after,second row should have a different name");
        }
        assertEquals(after, after0);
    }

    /**
     * Test of generateCorrelationMFromCovarianceM method, of class
     * AbstractRatiosDataModel.
     */
    @Test
    public void test_GenerateCorrelationMFromCovarianceM() {
        System.out.println("Testing AbstractRatiosDataModel's generateCorrelationMFromCovarianceM()");
        //covariances empty correlation empty, covariance should remain empty
        AbstractRatiosDataModel instance = new AbstractTester();
        instance.dataCorrelationsVarUnct.initializeMatrix();
        instance.dataCovariancesVarUnct.initializeMatrix();
        String before = (instance.dataCorrelationsVarUnct.ToStringWithLabels());
        Matrix ma0 = new Matrix(0, 0);
        instance.dataCovariancesVarUnct.setMatrix(ma0);
        instance.generateBothUnctCorrelationMFromEachUnctCovarianceM();
        String after = (instance.dataCorrelationsVarUnct.ToStringWithLabels());
        assertEquals(before, after);

        //correlation specified and covariance empty,correlation should be empty
        Matrix ma = new Matrix(2, 2);
        ma.set(0, 0, 2);
        ma.set(0, 1, 2);
        ma.set(1, 0, 1);
        ma.set(1, 1, 1);
        instance.dataCorrelationsVarUnct.setMatrix(ma);
        String[] hello = new String[2];
        hello[0] = "hello";
        hello[1] = "there";
        instance.dataCorrelationsVarUnct.setRows(hello);
        instance.generateBothUnctCorrelationMFromEachUnctCovarianceM();
        String after0 = instance.dataCorrelationsVarUnct.ToStringWithLabels();
        assertEquals(after, after0);

        //correlation empty covariance specified, correlation should specify
        instance.dataCorrelationsVarUnct = new CovarianceMatrixModel();
        instance.dataCorrelationsVarUnct.initializeMatrix();
        instance.dataCovariancesVarUnct.setMatrix(ma);
        instance.dataCovariancesVarUnct.setRows(hello);
        before = instance.dataCorrelationsVarUnct.ToStringWithLabels();
        instance.generateBothUnctCorrelationMFromEachUnctCovarianceM();
        after = instance.dataCorrelationsVarUnct.ToStringWithLabels();
        if (before.equals(after)) {
            fail("Before should not be the same as after, should go from empty to specified");
        }

        //covariances specified and correlation specified, correlation specifications should be overwritten
        hello[1] = "buddy";
        instance.dataCorrelationsVarUnct.setRows(hello);
        before = instance.dataCorrelationsVarUnct.ToStringWithLabels();
        after0 = after;
        instance.generateBothUnctCorrelationMFromEachUnctCovarianceM();
        after = instance.dataCorrelationsVarUnct.ToStringWithLabels();
        if (before.equals(after)) {
            fail("Before should not be the same as after,second row should have a different name");
        }
        assertEquals(after, after0);
    }

    /**
     * Test of saveEdits method, of class AbstractRatiosDataModel.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void test_SaveEdits() throws Exception {
        System.out.println("Testing AbstractRatiosDataModel's saveEdits(boolean checkCovarianceValidity)");
        AbstractRatiosDataModel instance = new AbstractTester();
        AbstractRatiosDataModel instance0 = new AbstractTester();
        AbstractRatiosDataModel instance1 = new AbstractTester();
        boolean checkCovarianceValidity = false;
////        //If Data Correlations Null, Rhos are null, Rhos should stay null,regardless of checkCovarianceValidity
////        instance.dataCorrelationsVarUnct = null;
////        //instance.rhos = null;
////        Map<String, BigDecimal> b4 = instance.rhos;
////        instance.saveEdits(checkCovarianceValidity);
////        Map<String, BigDecimal> afta = new HashMap<>();
////        assertEquals(b4, afta);
////        checkCovarianceValidity = true;
////        b4 = instance.rhos;
////        instance.saveEdits(checkCovarianceValidity);
////        afta = new HashMap<>();
////        assertEquals(b4, afta);

        //if datacorrelations empty, and rhos specified, rhos keeps names but bigdecimal values are emptied.
        instance0.rhos.put("o__n__e", new BigDecimal("1"));
        Map<String, BigDecimal> b34 = instance0.rhos;
        String beforeInString = b34.toString();
        instance0.dataCorrelationsVarUnct.initializeMatrix();
        instance0.saveEdits(checkCovarianceValidity);
        Map<String, BigDecimal> afta3 = instance0.rhos;
        String afterInString = afta3.toString();
        if (beforeInString.equals(afterInString)) {
            fail("The rhos should not be the same before and after");
        }

        //If specified datacorrelations but no rhos, rhos does not change
        instance1.dataCorrelationsVarUnct.initializeMatrix();
        String[] hello = new String[2];
        hello[0] = "hello";
        hello[1] = "there";
        instance1.dataCorrelationsVarUnct.setRows(hello);
        Matrix ma = new Matrix(2, 2);
        ma.set(0, 0, 2);
        ma.set(0, 1, 2);
        ma.set(1, 0, 1);
        ma.set(1, 1, 1);
        ma.set(0, 0, 0.0);
        instance1.dataCorrelationsVarUnct.setMatrix(ma);
        Map<String, BigDecimal> b3f0r3 = instance1.rhos;
        String b4InString = b3f0r3.toString();
        checkCovarianceValidity = false;
        instance1.saveEdits(checkCovarianceValidity);
        Map<String, BigDecimal> ft3r = instance1.rhos;
        String fterInString = ft3r.toString();
        assertEquals(b4InString, fterInString);

        //Datacorrelations specified, specified rhos, datacorrelations should copy over.
        AbstractRatiosDataModel instance2 = new AbstractTester();
        instance2.dataCorrelationsVarUnct.initializeMatrix();
        hello = new String[2];
        instance2.dataCorrelationsVarUnct.setRows(hello);
        instance2.dataCorrelationsVarUnct.setMatrix(ma);
        instance2.rhos.put("two here number__zwei", new BigDecimal("2"));
        b3f0r3 = instance2.rhos;
        b4InString = b3f0r3.toString();
        checkCovarianceValidity = false;
        instance2.saveEdits(checkCovarianceValidity);
        ft3r = instance2.rhos;
        fterInString = ft3r.toString();
        if (b4InString.equals(fterInString)) {
            fail("Rhos should not be the same before and after!");
        }

//        //No DataCorrelations? Error
//        AbstractRatiosDataModel instance3 = new AbstractTester();
//        int err = 0;
//        try {
//            instance3.saveEdits(checkCovarianceValidity);
//        } catch (java.lang.NullPointerException except1) {
//            err = 1;
//        }
//        if (err == 0) {
//            fail("A NullPointerException should have been thrown");
//        }

        //ensure that the exception ETException is thrown when (checkCovarianceValidity && !dataCovariancesVarUnct.isCovMatrixSymmetricAndPositiveDefinite()
        AbstractRatiosDataModel instance4 = new AbstractTester();
        instance4.dataCorrelationsVarUnct.initializeMatrix();
        hello = new String[2];
        instance4.dataCorrelationsVarUnct.setRows(hello);
        instance4.dataCorrelationsVarUnct.setMatrix(ma);
        instance4.rhos.put("two here number__zwei", new BigDecimal("2"));
        checkCovarianceValidity = true;
        int err = 0;
        try {
            instance4.saveEdits(checkCovarianceValidity);
        } catch (org.earthtime.exceptions.ETException except1) {
            err = 1;
        }
        if (err == 0) {
            fail("An ETException should have been thrown");
        }
    }

    ////////////////////////
    ////Unfinished Tests////
    //////////////////////// 
    /*
     *
     *     
     *
     *
     *     
     *AbstractRatiosDataModel.cloneModel()
     *AbstractRatiosDataModel.getClassNameAliasForXML ()
     *AbstractRatiosDataModel.readXMLObject ( String filename, boolean doValidate )
     *AbstractRatiosDataModel.setClassXMLSchemaURL ( String resourceURI )
     *AbstractRatiosDataModel.customizeXstream ( XStream xstream )
     *AbstractRatiosDataModel.getXStream()
     *AbstractRatiosDataModel.initializeNewRatiosAndRhos( boolean updateOnly )
     *AbstractRatiosDataModel.serializeXMLObject(String filename)
     *  These tests were decided by me and Dr. Bowring to best be done in the
     *      implementation instead of here in the Abstract class.
     *
     *
     *There is also a class within AbstractRatiosDataModel that has one method.
     *protected class DataValueModelNameComparator implements Comparator<ValueModel>
     *compare ( ValueModel vm1, ValueModel vm2 )
     *  This method was not tested.
     */
}
