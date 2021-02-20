/*
 * SampleRegistries_Test_05052014Test.java
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
public class SampleRegistriesTest {

    
    
    

    
    ////////////////////////////////////////////
    ////Constructor Tests///&///Method Tests////
    ////////////////////////////////////////////      
    
     /**
     * Test of SampleRegistries() method, of class SampleRegistries.
     */
    @Test
    public void test_constructor_0(){
	System.out.println("Testing SampleRegistries' enumerations and getters.");
        //Tests if values are correct for all enumerations, and tests the getters as well. You cannot instantiate new inumerations.
        
        SampleRegistries ave=SampleRegistries.GeochronID;
        assertEquals("GeochronID",ave.getName());
        assertEquals("GCH",ave.getCode());
        assertEquals("https://www.geochron.org/igsnexists.php?igsn=",ave.getConnectionString());
        
        ave=SampleRegistries.SESAR;
        assertEquals("SESAR",ave.getName());
        assertEquals("SSR",ave.getCode());
        assertEquals("https://app.geosamples.org/webservices/display.php?igsn=",ave.getConnectionString());

        String[] list=SampleRegistries.getNames();

        assertEquals("GeochronID",list[1]);
        assertEquals("SESAR",list[0]);

        list=SampleRegistries.getCodes();

        assertEquals("GCH",list[1]);
        assertEquals("SSR",list[0]);
        
    }    
        
    
    
    
    
    //////////////////
    //Method Tests////
    //////////////////      
    

    

    /**
     * Test of getRegistryIfLegalCode method, of class SampleRegistries.
     */
    @Test
    public void test_GetRegistryIfLegalCode() {
        System.out.println("Testing SampleRegistries's getRegistryIfLegalCode(String checkCode)");
        String checkCode = "GCH";
        SampleRegistries expResult = SampleRegistries.GeochronID;
        SampleRegistries result = SampleRegistries.getRegistryIfLegalCode(checkCode);
        assertEquals(expResult, result);

        checkCode = "SSR";
        expResult = SampleRegistries.SESAR;
        result = SampleRegistries.getRegistryIfLegalCode(checkCode);
        assertEquals(expResult, result);       
        
        checkCode = "";
        expResult = null;
        result = SampleRegistries.getRegistryIfLegalCode(checkCode);
        assertEquals(expResult, result); 
    
    
    
    
    }

    
    
    /**
     * Test of updateSampleID method, of class SampleRegistries.
     */
    @Test
    public void test_UpdateSampleID() {
        System.out.println("Testing SampleRegistries's gupdateSampleID(String sampleId)");
        String sampleID = "hello";
        String expResult = "SSR.hello";
        String result = SampleRegistries.updateSampleID(sampleID);
        assertEquals(expResult, result);
        
        
    }

    ////////////////////////
    ////Unfinished Tests////
    //////////////////////// 
    
    
    
    /*
     *
     *
     *SampleRegistries.isSampleIdentifierValidAtRegistry(String sampleId)
     *  This test involves too much digging at the moment with how much time I
     *      have left.
     *
     *
     *
     *
    */
    
    
    
    
    
    
    
    
    
    
    
}
