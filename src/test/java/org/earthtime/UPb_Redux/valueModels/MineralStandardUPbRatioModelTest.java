/*
 * MineralStandardUPbRatioModel_Test_04142014Test.java
 *
 * The naming of this file is the name of the .java file being tested, combined
 * with test since this is the test file, combined with the creation date of
 * this file. The test at the end is so that the file runs from NetBeans when
 * testing an entire project.
 *
 * Created on April 14, 2014.
 *
 *
 *Version History:
 *April 14 2014: File Created and constructor tests finished. Method tests also 
 *               finished.
 *
 *
 *Developed for the CIRDLES lab at the College of Charleston by Patrick Brewer.
 *
 *
 */



package org.earthtime.UPb_Redux.valueModels;



import com.thoughtworks.xstream.XStream;
import java.math.BigDecimal;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;


/**
 *
 * @author patrickbrewer
 */
public class MineralStandardUPbRatioModelTest {
    
    
    
    /////////////////////////
    ////Constructor Tests////
    /////////////////////////        
    
    
     /**
     * Test of MineralStandardUPbRatioModel() method, of class MineralStandardUPbRatioModel.
     */     
    @Test
    public void test_constructor_0(){
	System.out.println("Testing MineralStandardUPbRatioModel's MineralStandardUPbRatioModel()");
        //Tests if values are correct
        MineralStandardUPbRatioModel instance=new MineralStandardUPbRatioModel();
        String expResult="NONE";
        String result=instance.getName();
        assertEquals(expResult,result);
        result=instance.getUncertaintyType();
        assertEquals(expResult,result);
        BigDecimal expectedResult=new BigDecimal("0");
        BigDecimal actualResult=instance.getValue();
        assertEquals(expectedResult,actualResult);
        actualResult=instance.getOneSigma();
        assertEquals(expectedResult,actualResult);
        assertEquals(true,instance.isMeasured());
        }    
    
     /**
     * Test of MineralStandardUPbRatioModel(Name) method, of class MineralStandardUPbRatioModel
     */ 
    @Test
    public void test_constructor_2(){
	System.out.println("Testing MineralStandardUPbRatioModel's MineralStandardUPbRatioModel(String name)");
        MineralStandardUPbRatioModel instance=new MineralStandardUPbRatioModel("hello");
        String expResult="NONE";
        String result=instance.getName();
        assertEquals("hello",result);
        result=instance.getUncertaintyType();
        assertEquals(expResult,result);
        BigDecimal expectedResult=new BigDecimal("0");
        BigDecimal actualResult=instance.getValue();
        assertEquals(expectedResult,actualResult);
        actualResult=instance.getOneSigma();
        assertEquals(expectedResult,actualResult);
        assertEquals(true,instance.isMeasured());
        }    

     /**
     * Test of MineralStandardUPbRatioModel(Name,value,uncertaintyType,oneSigma,measured) method, of class 
     * MineralStandardUPbRatioModel.
     */ 
    @Test
    public void test_constructor_1(){
    	System.out.println("Testing MineralStandardUPbRatioModel's MineralStandardUPbRatioModel(String Name,BigDecimal value,String uncertaintyType,BigDecimal oneSigma,Boolean FracCorr,boolean OxideCorr)");    
        //Tests if values are correct
        MineralStandardUPbRatioModel instance=new MineralStandardUPbRatioModel("r207_339",new BigDecimal("-12.34567890"),"PCT",new BigDecimal(".9876543210"),false);                  
        String expResult="r207_339";
        String result=instance.getName();
        assertEquals(expResult,result);
        expResult="PCT";
        result=instance.getUncertaintyType();
        assertEquals(expResult,result);
        BigDecimal expectedResult=new BigDecimal("-12.34567890");
        BigDecimal actualResult=instance.getValue();
        assertEquals(expectedResult,actualResult);
        actualResult=instance.getOneSigma();
        expectedResult=new BigDecimal(".9876543210");
        assertEquals(expectedResult,actualResult);
        assertEquals(false,instance.isMeasured());       
    }    

    
    
    ////////////////////
    ////Method Tests////
    ////////////////////         
    
    

    /**
     * Test of copy method, of class MineralStandardUPbRatioModel.
     */
    @Test
    public void test_Copy() {
        //This tests the method with default values.
        System.out.println("Testing MineralStandardUPbRatioModel's copy()");
        MineralStandardUPbRatioModel instance = new MineralStandardUPbRatioModel();
        MineralStandardUPbRatioModel expectedResult = instance;
        MineralStandardUPbRatioModel result = instance.copy();
        assertEquals(expectedResult, result);

        //This tests the method with specified values.
        instance=new MineralStandardUPbRatioModel("hello",new BigDecimal("3.87695"),"ABS",new BigDecimal("1.25"),false);
        expectedResult = instance;
        result = instance.copy();
        assertEquals(expectedResult, result);
            }
 
    /**
     * Test of copyValuesFrom method, of class MineralStandardUPbRatioModel.
     */
    @Test
    public void test_CopyValuesFrom() {
       //This test uses specified values.
        System.out.println("Testing MineralStandardUPbRatioModel's copyValuesFrom(MineralStandardUPbRatioModel parent)");
        MineralStandardUPbRatioModel instance0 = new MineralStandardUPbRatioModel("Specific",new BigDecimal("12.34567890"),"ABS",new BigDecimal("0.987654321"),false);
        MineralStandardUPbRatioModel blank = new MineralStandardUPbRatioModel();

        /**Initializing a Variable to test if names are equal before or after
        * If this value is set to 1 in the following try catch block, then the
        * names were not equal beforehand and should not be equal afterwards.
        * If the variable remains at 0 then the names were equal beforehand and
        * therefore should remain the same afterwards.
        */
        
        int nameAfter=0;
        try{
            assertEquals(instance0.name,blank.name);
        }
        catch(org.junit.ComparisonFailure except1){
           nameAfter=1;
        }
        //Copying Values from Specific ValueModel to Default
        blank.copyValuesFrom(instance0);
        //Test if Expected is Equal to the Result
        assertEquals(instance0.value,blank.value);
        assertEquals(instance0.oneSigma,blank.oneSigma);
        assertEquals(instance0.uncertaintyType,blank.uncertaintyType);
        assertEquals(instance0.isMeasured(),blank.isMeasured());
        
        if(nameAfter==1){
        //The name field should not be equal between the two ValueModels.
            try{
                assertEquals(instance0.name,blank.name);
                //Fail if expected Exception not Thrown
                fail("org.junit.ComparisonFailure throwable not thrown - the Name Field is being copied!");
                }
            catch(org.junit.ComparisonFailure except0){
       
                  }
                        }
        //This test uses default values.
        instance0 = new MineralStandardUPbRatioModel();
        blank = new MineralStandardUPbRatioModel();
        blank.copyValuesFrom(instance0);
        assertEquals(instance0.value,blank.value);
        assertEquals(instance0.oneSigma,blank.oneSigma);
        assertEquals(instance0.uncertaintyType,blank.uncertaintyType);
        assertEquals(instance0.name,blank.name);
        assertEquals(instance0.isMeasured(),blank.isMeasured());

        //This test uses default values, save for the name field.
        instance0 = new MineralStandardUPbRatioModel("Specific",new BigDecimal("213"),"ABS",new BigDecimal("0.4324"),true);
        blank = new MineralStandardUPbRatioModel("Specific",new BigDecimal("12.4332"),"ABS",new BigDecimal("0.654654"),false);
       
        /**Initializing a Variable to test if names are equal before or after
        If this value is set to 1 in the following try catch block, then the
        * names were not equal beforehand and should not be equal afterwards.
        * If the variable remains at 0 then the names were equal beforehand and
        * therefore should remain the same afterwards.
        */
        
        nameAfter=0;
        try{
            assertEquals(instance0.name,blank.name);
        }
        catch(org.junit.ComparisonFailure except1){
           nameAfter=1;
        }
        
        //Copying Values from Specific ValueModel to Default
        blank.copyValuesFrom(instance0);
        //Test if Expected is Equal to the Result
        assertEquals(instance0.value,blank.value);
        assertEquals(instance0.oneSigma,blank.oneSigma);
        assertEquals(instance0.uncertaintyType,blank.uncertaintyType);
        assertEquals(instance0.isMeasured(),blank.isMeasured());

        if(nameAfter==1){
            //The name field should not be equal between the two ValueModels.
            try{
                assertEquals(instance0.name,blank.name);
                //Fail if expected Exception not Thrown
                fail("org.junit.ComparisonFailure throwable not thrown - the Name Field is being copied!");
                }
             catch(org.junit.ComparisonFailure except0){
       
                    }
                    }
    }
    
    /**
     * Test of isMeasured method, of class MineralStandardUPbRatioModel.
     */
    @Test
    public void test_IsMeasured() {
        System.out.println("Testing MineralStandardUPbRatioModel's isMeasured()");
        MineralStandardUPbRatioModel instance = new MineralStandardUPbRatioModel();
        boolean expResult = true;
        boolean result = instance.isMeasured();
        assertEquals(expResult, result);
        instance = new MineralStandardUPbRatioModel("Specific",new BigDecimal("213"),"ABS",new BigDecimal("0.4324"),false);
        result = instance.isMeasured();
        assertEquals(false, result);
        
    }

    /**
     * Test of setMeasured method, of class MineralStandardUPbRatioModel.
     */
    @Test
    public void test_SetMeasured() {
        System.out.println("Testing MineralStandardUPbRatioModel's setMeasured(boolean isMeasured)");
        MineralStandardUPbRatioModel instance = new MineralStandardUPbRatioModel();
        boolean expResult = false;
        instance.setMeasured(false);
        boolean result = instance.isMeasured();
        assertEquals(expResult, result);
    }

    /**
     * Test of customizeXstream method, of class MineralStandardUPbRatioModel.
     */
    @Test
    public void test_CustomizeXstream() {
       //This tests if the stream is customized.
        System.out.println("Testing MineralStandardUPbRatioModel's customizeXstream(XStream xstream)");
        MineralStandardUPbRatioModel instance = new MineralStandardUPbRatioModel();
        XStream xstream = new XStream();
        instance.customizeXstream(xstream);
        boolean expResult = true;
        boolean result=false;
        String expResult2;
        String result2;
        if(xstream instanceof XStream){
            result=true;        
            expResult2="https://raw.githubusercontent.com/EARTHTIME/Schema/master/ValueModelXMLSchema.xsd";
            result2=instance.getValueModelXMLSchemaURL();
            assertEquals(expResult2,result2);            
                                     }
        assertEquals(expResult,result);
    }

    /**
     * Test of toTableFormat method, of class MineralStandardUPbRatioModel.
     */
    @Test
    public void test_ToTableFormat() {
        System.out.println("Testing MineralStandardUPbRatioModel's toTableFormat()");
        MineralStandardUPbRatioModel instance = new MineralStandardUPbRatioModel();
        String expResult = "0 : 0";
        String result = instance.toTableFormat();
        assertEquals(expResult, result);
        instance = new MineralStandardUPbRatioModel("Specific",new BigDecimal("213"),"ABS",new BigDecimal("0.4324"),false);
        result = instance.toTableFormat();
        expResult="213 : 0.4324";
        assertEquals(expResult, result);
    }
    
    
    
    
    
}
