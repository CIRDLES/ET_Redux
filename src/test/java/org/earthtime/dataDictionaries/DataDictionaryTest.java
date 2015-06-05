/*
 * DataDictionary_Test_05052014Test.java
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


import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author patrickbrewer
 */
public class DataDictionaryTest {
    
    
  

    
    ////////////////////////////////////////////
    ////Constructor Tests///&///Method Tests////
    ////////////////////////////////////////////      
    
     /**
     * Test of DataDictionary() method, of class DataDictionary.
     */
    @Test
    public void test_constructor_0(){
	System.out.println("Testing DataDictionary's enumerations and getters.");
        //Tests if values are correct for all enumerations, and tests the getters as well. You cannot instantiate new inumerations.
        
        String[] ave=DataDictionary.AliquotInstrumentalMethod;

        assertEquals("ID-TIMS",ave[0]);
        assertEquals("SHRIMP Ion Probe",ave[1]);
        assertEquals("Cameca Ion Probe",ave[2]);
        assertEquals("Quad ICPMS",ave[3]);
        assertEquals("HR-ICPMS",ave[4]);
        assertEquals("MC-ICPMS",ave[5]);

        
 
        // TODO: use schema as source
//        ave=DataDictionary.MineralStandardNames;
//
//        assertEquals("FC-1",ave[0]);
//        assertEquals("FC-5",ave[1]);
//        assertEquals("Fish Canyon Tuff",ave[2]);
//        assertEquals("Manicouagan",ave[3]);
//        assertEquals("North Mountain Basalt",ave[4]);
//        assertEquals("Peixe",ave[5]);
//        assertEquals("Plesovice",ave[6]);
//        assertEquals("Sri Lanka",ave[7]);
//        assertEquals("Temora",ave[8]);
//        
        
        
        ave=DataDictionary.PbCollectorType;
        assertEquals("NONE",ave[0]);
        assertEquals("Faraday",ave[1]);
        assertEquals("Daly",ave[2]);
        assertEquals("SEM",ave[3]);        
        

        ave=DataDictionary.RadiogenicIsotopeRatioTypes;
        assertEquals("r206_204r",ave[0]);
        assertEquals("r208_206r",ave[1]);
        assertEquals("r206_238r",ave[2]);
        assertEquals("r207_235r",ave[3]);            
        assertEquals("r207_206r",ave[4]);
        assertEquals("r208_232r",ave[5]);
        assertEquals("r206_238r_Th",ave[6]);
        assertEquals("r207_235r_Pa",ave[7]);    
        assertEquals("r207_206r_Th",ave[8]);        
        assertEquals("r207_206r_Pa",ave[9]);        
        assertEquals("r207_206r_ThPa",ave[10]);        
        assertEquals("rhoR206_238r__r207_235r",ave[11]);        
        assertEquals("rhoR207_206r__r238_206r",ave[12]);        
        

        
        ave=DataDictionary.SampleIsochronRatioNames;
        assertEquals("r206_204tfc",ave[0]);
        assertEquals("r204_206s",ave[1]);
        assertEquals("r238_204s",ave[2]);
        assertEquals("rhoR238_204s__r206_204s",ave[3]);            
        assertEquals("r235_204s",ave[4]);            
        assertEquals("r204_207s",ave[5]);            
        assertEquals("rhoR235_204s__r207_204s",ave[6]);            
        assertEquals("r232_204s",ave[7]);            
        assertEquals("r208_204s",ave[8]);            
        assertEquals("rhoR202_204s__r208_204s",ave[9]);            
        assertEquals("r238_206s",ave[10]);            
        assertEquals("r207_206s",ave[11]);            
        assertEquals("rhoR238_206s__r207_206s",ave[12]);            
        assertEquals("rhoR207_206s__r204_206s",ave[13]);            
        assertEquals("r238_207s",ave[14]);            
        assertEquals("r235_207s",ave[15]);            

        
        ave=DataDictionary.SettingType;
        assertEquals("other",ave[0]);
        assertEquals("situ",ave[1]);
        assertEquals("grain mount",ave[2]);
        assertEquals("loose grain",ave[3]);            
        

        
        ave=DataDictionary.TracerType;
        assertEquals("NONE",ave[0]);
        assertEquals("mixed 205-235",ave[1]);
        assertEquals("mixed 205-233-235",ave[2]);
        assertEquals("mixed 208-235",ave[3]);            
        assertEquals("mixed 205-233-236",ave[4]);            
        assertEquals("mixed 202-205-233-235",ave[5]);            
        assertEquals("mixed 202-205-233-236",ave[6]);            
        assertEquals("mixed 205-233-235-230Th",ave[7]);           
        

        
        ave=DataDictionary.UCollectorType;
        assertEquals("NONE",ave[0]);
        assertEquals("Faraday",ave[1]);
        assertEquals("Daly",ave[2]);
        assertEquals("SEM",ave[3]);         

        
        ave=DataDictionary.earthTimeInitialPbModelCorrelationCoeffNames;
        assertEquals("rhoR206_204c__r207_204c",ave[0]);
        assertEquals("rhoR207_204c__r208_204c",ave[1]);
        assertEquals("rhoR206_204c__r208_204c",ave[2]);
        

        
        ave=DataDictionary.earthTimeInitialPbModelRatioNames;
        assertEquals("r206_204c",ave[0]);
        assertEquals("r207_204c",ave[1]);
        assertEquals("r208_204c",ave[2]);        

        
        
        ave=DataDictionary.earthTimePbBlankICRatioNames;
        assertEquals("r206_204b",ave[0]);
        assertEquals("r207_204b",ave[1]);
        assertEquals("r208_204b",ave[2]);          

        
        
        ave=DataDictionary.earthTimePbBlankRhoCorrelationNames;
        assertEquals("rhoR206_204b__r207_204b",ave[0]);
        assertEquals("rhoR206_204b__r208_204b",ave[1]);
        assertEquals("rhoR207_204b__r208_204b",ave[2]);              
        
        
        
        ave=DataDictionary.earthTimeUPbCompositionalMeasuresNames;
        assertEquals("concU",ave[0]);
        assertEquals("concTh",ave[1]);
        assertEquals("rTh_Usample",ave[2]);
        assertEquals("concPb_rib",ave[3]);            
        assertEquals("concPb_r",ave[4]);            
        assertEquals("concPb_i",ave[5]);            
        assertEquals("concPb_ib",ave[6]);            
        assertEquals("concPb206_rib",ave[7]);          
        assertEquals("concPb206_r",ave[8]);
        assertEquals("concPb206_ib",ave[9]);
        assertEquals("totRadiogenicPbMass",ave[10]);
        assertEquals("radToCommonTotal",ave[11]);
        assertEquals("totCommonPbMass",ave[12]);
        
        
        ave=DataDictionary.uPbReduxOutputNames;
        assertEquals("initCommonPbMass",ave[0]);
        assertEquals("radToCommonPb206",ave[1]);
        assertEquals("radToCommonPb207",ave[2]);
        assertEquals("radToCommonPb208",ave[3]);            
        assertEquals("uTracerMassInGrams",ave[4]);            
        assertEquals("molPb206r",ave[5]);            
        assertEquals("molU238b",ave[6]);            
        assertEquals("molU238t",ave[7]);          
        assertEquals("molU235b",ave[8]);
        assertEquals("molU235t",ave[9]);
        assertEquals("molU233t",ave[10]);
        assertEquals("molU235s",ave[11]);
        assertEquals("molU238s",ave[12]);        
        assertEquals("blankPbMass",ave[13]);        
        

        String[][] sieg=DataDictionary.ArrayMapOfInputsToFractionClassMethodNames;
        assertEquals("getMeasuredRatioByName",sieg[0][0]);
        assertEquals("r206_204m",sieg[1][0]);
        assertEquals("getAnalysisMeasure",sieg[2][0]);
        assertEquals("fractionMass",sieg[3][0]);            
        assertEquals("getTracerRatioByName",sieg[4][0]);            
        assertEquals("r202_205t",sieg[5][0]);          
        
        

        sieg=DataDictionary.AtomicMolarMasses;     
        assertEquals("gmol204",sieg[0][0]);
        assertEquals("gmol205",sieg[1][0]);
        assertEquals("gmol206",sieg[2][0]);
        assertEquals("gmol207",sieg[3][0]);            
        assertEquals("gmol208",sieg[4][0]);            
        assertEquals("gmol238",sieg[5][0]);               
        
        sieg=DataDictionary.MeasuredConstants;     
        assertEquals("lambda230",sieg[0][0]);
        assertEquals("lambda231",sieg[1][0]);
        assertEquals("lambda232",sieg[2][0]);
        assertEquals("lambda234",sieg[3][0]);            
        assertEquals("lambda235",sieg[4][0]);            
        assertEquals("lambda238",sieg[5][0]);                    
        
       
        
        String[][][] blut=DataDictionary.universalClumpedInputVariableSpecs;
        assertEquals("r202_205m",blut[0][0][0]);
        assertEquals("alphaPb",blut[1][0][0]);
        assertEquals("tracerMassInGrams",blut[2][0][0]);
        assertEquals("pbBlankMassInGrams",blut[3][0][0]);            
        assertEquals("r206_204c",blut[4][0][0]);            
        assertEquals("r238_235s",blut[5][0][0]);         
        
        
        

    }    
        
    
    
    
    //////////////////
    //Method Tests////
    //////////////////      
    


    /**
     * Test of validateSettingType method, of class DataDictionary.
     */
    @Test
    public void test_ValidateSettingType() {
        System.out.println("Testing DataDictionary's validateSettingType(String settingType)");
        String settingType = "fdsfdsfsdfds";
        String expResult = "other";
        String result = DataDictionary.validateSettingType(settingType);
        assertEquals(expResult, result);

        
        settingType = "situ";
        expResult = "situ";
        result = DataDictionary.validateSettingType(settingType);
        assertEquals(expResult, result);        
        
        settingType = "grain mount";
        expResult = "grain mount";
        result = DataDictionary.validateSettingType(settingType);
        assertEquals(expResult, result);  
    
        settingType = "loose grain";
        expResult = "loose grain";
        result = DataDictionary.validateSettingType(settingType);
        assertEquals(expResult, result);      
    
    }
    
    /**
     * Test of validatePbCollectorType method, of class DataDictionary.
     */
    @Test
    public void test_ValidatePbCollectorType() {
        System.out.println("Testing DataDictionary's validatePbCollectorType(pbCollectorType pbCollectorType)");
        String settingType = "fdsfdsfsdfds";
        String expResult = "NONE";
        String result = DataDictionary.validatePbCollectorType(settingType);
        assertEquals(expResult, result);

        
        settingType = "faraday";
        expResult = "Faraday";
        result = DataDictionary.validatePbCollectorType(settingType);
        assertEquals(expResult, result);        
        
        settingType = "Daly";
        expResult = "Daly";
        result = DataDictionary.validatePbCollectorType(settingType);
        assertEquals(expResult, result);  
    
        settingType = "SEM";
        expResult = "SEM";
        result = DataDictionary.validatePbCollectorType(settingType);
        assertEquals(expResult, result);      
    }
    
    /**
     * Test of validateUCollectorType method, of class DataDictionary.
    */ 
    @Test
    public void test_ValidateUCollectorType() {
        System.out.println("Testing DataDictionary's validateUCollectorType(String uCollectorType)");
        String settingType = "fdsfdsfsdfds";
        String expResult = "NONE";
        String result = DataDictionary.validateUCollectorType(settingType);
        assertEquals(expResult, result);

        
        settingType = "faraday";
        expResult = "Faraday";
        result = DataDictionary.validateUCollectorType(settingType);
        assertEquals(expResult, result);        
        
        settingType = "Daly";
        expResult = "Daly";
        result = DataDictionary.validateUCollectorType(settingType);
        assertEquals(expResult, result);  
    
        settingType = "SEM";
        expResult = "SEM";
        result = DataDictionary.validateUCollectorType(settingType);
        assertEquals(expResult, result);    
    }
    
    /**
     * Test of getPbBlankRatioName method, of class DataDictionary.
     */
    @Test
    public void test_GetPbBlankRatioName() {
        System.out.println("Testing DataDictionary's getPbBlankRatioName(int index)");
        int index = 0;
        String expResult = "r206_204b";
        String result = DataDictionary.getPbBlankRatioName(index);
        assertEquals(expResult, result);

        index = 1;
        expResult = "r207_204b";
        result = DataDictionary.getPbBlankRatioName(index);
        assertEquals(expResult, result); 
    
        index = 2;
        expResult = "r208_204b";
        result = DataDictionary.getPbBlankRatioName(index);
        assertEquals(expResult, result);     
    
        index=99;
        try{
        result = DataDictionary.getPbBlankRatioName(index);
        fail("Should not have gotten here");
        System.out.println(result);
        }
        catch(java.lang.IndexOutOfBoundsException e){}
                
    }
    
    /**
     * Test of getPbBlankRhoCorrelationName method, of class DataDictionary.
     */
    @Test
    public void test_GetPbBlankRhoCorrelationName() {
        System.out.println("Testing DataDictionary's getPbBlankRhoCorrelationName(int index)");
        int index = 0;
        String expResult = "rhoR206_204b__r207_204b";
        String result = DataDictionary.getPbBlankRhoCorrelationName(index);
        assertEquals(expResult, result);

        index = 1;
        expResult = "rhoR206_204b__r208_204b";
        result = DataDictionary.getPbBlankRhoCorrelationName(index);
        assertEquals(expResult, result); 
    
        index = 2;
        expResult = "rhoR207_204b__r208_204b";
        result = DataDictionary.getPbBlankRhoCorrelationName(index);
        assertEquals(expResult, result);     
    
        index=99;
        try{
        result = DataDictionary.getPbBlankRhoCorrelationName(index);
        fail("Should not have gotten here");
        System.out.println(result);        
        }
        catch(java.lang.IndexOutOfBoundsException e){}
                
        
    }
    
    /**
     * Test of getEarthTimeInitialPbModelRatioNames method, of class DataDictionary.
     */
    @Test
    public void test_GetEarthTimeInitialPbModelRatioNames() {
        System.out.println("Testing DataDictionary's getEarthTimeInitialPbModelRatioNames(int index)");
        int index = 0;
        String expResult = "r206_204c";
        String result = DataDictionary.getEarthTimeInitialPbModelRatioNames(index);
        assertEquals(expResult, result);

        index = 1;
        expResult = "r207_204c";
        result = DataDictionary.getEarthTimeInitialPbModelRatioNames(index);
        assertEquals(expResult, result); 
    
        index = 2;
        expResult = "r208_204c";
        result = DataDictionary.getEarthTimeInitialPbModelRatioNames(index);
        assertEquals(expResult, result);     
    
        index=99;
        try{
        result = DataDictionary.getEarthTimeInitialPbModelRatioNames(index);
        fail("Should not have gotten here");
        System.out.println(result);        
        }
        catch(java.lang.IndexOutOfBoundsException e){}
                
    }
    
    /**
     * Test of getEarthTimePbBlankICModelRatioNames method, of class DataDictionary.
      */
    @Test
    public void test_GetEarthTimePbBlankICModelRatioNames() {
        System.out.println("Testing DataDictionary's getEarthTimePbBlankICModelRatioNames(int index)");
        int index = 0;
        String expResult = "r206_204b";
        String result = DataDictionary.getEarthTimePbBlankICModelRatioNames(index);
        assertEquals(expResult, result);

        index = 1;
        expResult = "r207_204b";
        result = DataDictionary.getEarthTimePbBlankICModelRatioNames(index);
        assertEquals(expResult, result); 
    
        index = 2;
        expResult = "r208_204b";
        result = DataDictionary.getEarthTimePbBlankICModelRatioNames(index);
        assertEquals(expResult, result);     
    
        index=99;
        try{
        result = DataDictionary.getEarthTimePbBlankICModelRatioNames(index);
        fail("Should not have gotten here");
        System.out.println(result);        
        }
        catch(java.lang.IndexOutOfBoundsException e){}
    }
    
    /**
     * Test of getEarthTimeInitialPbModelCorrelationCoeffNamesint method, of class DataDictionary.
     */
    @Test
    public void test_GetEarthTimeInitialPbModelCorrelationCoeffNamesint() {
        System.out.println("Testing DataDictionary's getEarthTimeInitialPbModelCorrelationCoeffNamesint(int index)");
        int index = 0;
        String expResult = "rhoR206_204c__r207_204c";
        String result = DataDictionary.getEarthTimeInitialPbModelCorrelationCoeffNamesint(index);
        assertEquals(expResult, result);

        index = 1;
        expResult = "rhoR207_204c__r208_204c";
        result = DataDictionary.getEarthTimeInitialPbModelCorrelationCoeffNamesint(index);
        assertEquals(expResult, result); 
    
        index = 2;
        expResult = "rhoR206_204c__r208_204c";
        result = DataDictionary.getEarthTimeInitialPbModelCorrelationCoeffNamesint(index);
        assertEquals(expResult, result);     
    
        index=99;
        try{
        result = DataDictionary.getEarthTimeInitialPbModelCorrelationCoeffNamesint(index);
        fail("Should not have gotten here");
        System.out.println(result);        
        }
        catch(java.lang.IndexOutOfBoundsException e){}
    }
    
    /**
     * Test of getElementNameOfRatio method, of class DataDictionary.
     */
    @Test
    public void test_GetElementNameOfRatio() {
        System.out.println("Testing DataDictionary's getElementNameOfRatio(String name)");
        String name = "";
        String expResult = "U";
        String result = DataDictionary.getElementNameOfRatio(name);
        assertEquals(expResult, result);

        name = "20";
        expResult = "Pb";
        result = DataDictionary.getElementNameOfRatio(name);
        assertEquals(expResult, result);   
    
    
    }
    
    /**
     * Test of getTracerType method, of class DataDictionary.
     */
    @Test
    public void test_GetTracerType() {
        System.out.println("Testing DataDictionary's getTracerType()");
        String[] expResult = DataDictionary.TracerType;
        String[] result = DataDictionary.getTracerType();
        assertArrayEquals(expResult, result);

        
    }
    
    /**
     * Test of legalTracerType method, of class DataDictionary.
     */
    @Test
    public void test_LegalTracerType() {
        System.out.println("Testing DataDictionary's legalTracerType(String tracerType)");
        String tracerType = "@$$";
        boolean expResult = false;
        boolean result = DataDictionary.legalTracerType(tracerType);
        assertEquals(expResult, result);

        tracerType = "mixed 205-233-235";
        expResult = true;
        result = DataDictionary.legalTracerType(tracerType);
        assertEquals(expResult, result);
    
    
    }
    
    /**
     * Test of getRadiogenicIsotopeRatioTypes method, of class DataDictionary.
       */
    @Test
    public void test_GetRadiogenicIsotopeRatioTypes() {
        System.out.println("Testing DataDictionary's getRadiogenicIsotopeRatioTypes(int index)");
        int index = 0;
        String expResult = "r206_204r";
        String result = DataDictionary.getRadiogenicIsotopeRatioTypes(index);
        assertEquals(expResult, result);

        index = 1;
        expResult = "r208_206r";
        result = DataDictionary.getRadiogenicIsotopeRatioTypes(index);
        assertEquals(expResult, result); 
    
        index = 2;
        expResult = "r206_238r";
        result = DataDictionary.getRadiogenicIsotopeRatioTypes(index);
        assertEquals(expResult, result);     
    
        index=99;
        try{
        result = DataDictionary.getRadiogenicIsotopeRatioTypes(index);
        fail("Should not have gotten here");
        System.out.println(result);        
        }
        catch(java.lang.IndexOutOfBoundsException e){}
    }
    
    /**
     * 
     * Test of getSampleIsochronRatioNames method, of class DataDictionary.
     */
    @Test
    public void test_GetSampleIsochronRatioNames() {
        System.out.println("Testing DataDictionary's getSampleIsochronRatioNames(int index)");
        int index = 0;
        String expResult = "r206_204tfc";
        String result = DataDictionary.getSampleIsochronRatioNames(index);
        assertEquals(expResult, result);

        index = 1;
        expResult = "r204_206s";
        result = DataDictionary.getSampleIsochronRatioNames(index);
        assertEquals(expResult, result); 
    
        index = 2;
        expResult = "r238_204s";
        result = DataDictionary.getSampleIsochronRatioNames(index);
        assertEquals(expResult, result);     
    
        index=99;
        try{
        result = DataDictionary.getSampleIsochronRatioNames(index);
        fail("Should not have gotten here");
        System.out.println(result);        
        }
        catch(java.lang.IndexOutOfBoundsException e){}
        
    }
    
    /**
     * Test of getEarthTimeUPbCompositionalMeasuresNames method, of class DataDictionary.
     */
    @Test
    public void test_GetEarthTimeUPbCompositionalMeasuresNames() {
        System.out.println("Testing DataDictionary's getEarthTimeUPbCompositionalMeasuresNames(int index)");
        int index = 0;
        String expResult = "concU";
        String result = DataDictionary.getEarthTimeUPbCompositionalMeasuresNames(index);
        assertEquals(expResult, result);

        index = 1;
        expResult = "concTh";
        result = DataDictionary.getEarthTimeUPbCompositionalMeasuresNames(index);
        assertEquals(expResult, result); 
    
        index = 2;
        expResult = "rTh_Usample";
        result = DataDictionary.getEarthTimeUPbCompositionalMeasuresNames(index);
        assertEquals(expResult, result);     
    
        index=99;
        try{
        result = DataDictionary.getEarthTimeUPbCompositionalMeasuresNames(index);
        fail("Should not have gotten here");
        System.out.println(result);        
        }
        catch(java.lang.IndexOutOfBoundsException e){}
    }
    
    /**
     * Test of getMineralStandardNames method, of class DataDictionary.
     */
    @Test
    public void test_GetMineralStandardNames() {
        System.out.println("Testing DataDictionary's getMineralStandardNames()");
        String[] expResult = DataDictionary.MineralStandardNames;
        String[] result = DataDictionary.getMineralStandardNames();
        assertArrayEquals(expResult, result);

        
    }
        
    
    
    
    ////////////////////////
    ////Unfinished Tests////
    //////////////////////// 
    
    
    
    
    /*
     *
     *
     *DataDictionary.MapOfInputsToMethodNames
     *DataDictionary.inputsNameTranslator
     *  At the moment I do not have time to go through and figure out the keys
     *      to properly test these <String,String> Maps.
     *
     *
     *
    */
    
        

    
}
