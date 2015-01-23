/*
 * MeasuredRatioModel_Test_04142014Test.java
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
 *April 14 2014: File Created and constructor tests finished. Method tests
 *               started. Unknown finish date.
 *
 *
 *Developed for the CIRDLES lab at the College of Charleston by Patrick Brewer.
 *
 *
 */
package org.earthtime.UPb_Redux.valueModels;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import org.earthtime.exceptions.ETException;
import org.earthtime.utilities.URIHelper;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author patrickbrewer
 */
public class MeasuredRatioModelTest {
    
    
    
    
    /////////////////////////
    ////Constructor Tests////
    /////////////////////////        
    
    
     /**
     * Test of MeasuredRatioModel() method, of class MeasuredRatioModel.
          */
    @Test
    public void test_constructor_0(){
	System.out.println("Testing MeasuredRatioModel's MeasuredRatioModel()");
        //Tests if default values are correct
        MeasuredRatioModel instance=new MeasuredRatioModel();
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
        assertEquals(false,instance.isFracCorr());
        assertEquals(false,instance.isOxideCorr());
        }    
    
     /**
     * Test of MeasuredRatioModel(String Name,BigDecimal value,String uncertaintyType,BigDecimal oneSigma,boolean FracCorr,boolean OxideCorr)
     *  method, of class MeasuredRatioModel.
     */
    @Test
    public void test_constructor_1(){
    	System.out.println("Testing MeasuredRatioModel's MeasuredRatioModel(String Name,BigDecimal value,String uncertaintyType,BigDecimal oneSigma,boolean FracCorr,boolean OxideCorr)");    
        //Tests if default values are correct
        MeasuredRatioModel instance=new MeasuredRatioModel("r207_339",new BigDecimal("-12.34567890"),"PCT",new BigDecimal(".9876543210"),false,true);                  
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
        assertEquals(false,instance.isFracCorr());       
        assertEquals(true,instance.isOxideCorr());       
    }    
 
    
    
    ////////////////////
    ////Method Tests////
    ////////////////////         
    
    

    /**
     * Test of copy() method, of class MeasuredRatioModel.
     */
    @Test
    public void test_Copy() {
        //This tests the method with default values.
        System.out.println("Testing MeasuredRatioModel's copy()");
        MeasuredRatioModel instance = new MeasuredRatioModel();
        MeasuredRatioModel expectedResult = instance;
        MeasuredRatioModel result = instance.copy();
        assertEquals(expectedResult, result);

        //This tests the method with specified values.
        instance=new MeasuredRatioModel("hello",new BigDecimal("3.87695"),"ABS",new BigDecimal("1.25"),false,true);
        expectedResult = instance;
        result = instance.copy();
        assertEquals(expectedResult, result);
            }
 
    /**
     * Test of copyValuesFrom(MeasuredRatioModel parent) method, of class MeasuredRatioModel.
     */
    @Test
    public void test_CopyValuesFrom() {
       //This test uses specified values.
        System.out.println("Testing MeasuredRatioModel's copyValuesFrom(MeasuredRatioModel parent)");
        MeasuredRatioModel instance0 = new MeasuredRatioModel("Specific",new BigDecimal("12.34567890"),"ABS",new BigDecimal("0.987654321"),false,true);
        MeasuredRatioModel blank = new MeasuredRatioModel();

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
        assertEquals(instance0.isFracCorr(),blank.isFracCorr());
        assertEquals(instance0.isOxideCorr(),blank.isOxideCorr());
        
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
        instance0 = new MeasuredRatioModel();
        blank = new MeasuredRatioModel();
        blank.copyValuesFrom(instance0);
        assertEquals(instance0.value,blank.value);
        assertEquals(instance0.oneSigma,blank.oneSigma);
        assertEquals(instance0.uncertaintyType,blank.uncertaintyType);
        assertEquals(instance0.name,blank.name);
        assertEquals(instance0.isFracCorr(),blank.isFracCorr());
        assertEquals(instance0.isOxideCorr(),blank.isOxideCorr());
        //This test uses default values, save for the name field.
        instance0 = new MeasuredRatioModel("Specific",new BigDecimal("213"),"ABS",new BigDecimal("0.4324"),true,false);
        blank = new MeasuredRatioModel("Specific",new BigDecimal("12.4332"),"ABS",new BigDecimal("0.654654"),false,true);
       
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
        assertEquals(instance0.isFracCorr(),blank.isFracCorr());
        assertEquals(instance0.isOxideCorr(),blank.isOxideCorr());
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
     * Test of customizeXstream() method, of class MeasuredRatioModel.
     */
    @Test
    public void test_CustomizeXstream() {
       //This tests if the stream is customized.
        System.out.println("Testing MeasuredRatioModel's customizeXstream(Xstream xstream)");
        MeasuredRatioModel instance = new MeasuredRatioModel();
        XStream xstream = new XStream();
        instance.customizeXstream(xstream);
        boolean expResult = true;
        boolean result=false;
        String expResult2;
        String result2;
        if(xstream instanceof XStream){
            result=true;        
            expResult2="http://earth-time.org/projects/upb/public_data/XSD/ValueModelXMLSchema.xsd";
            result2=instance.getValueModelXMLSchemaURL();
            assertEquals(expResult2,result2);            
                                     }
        assertEquals(expResult,result);
    }

    /**
     * Test of toTableFormat method, of class MeasuredRatioModel.
     */
    @Test
    public void test_ToTableFormat() {
        System.out.println("Testing MeasuredRatioModel's toTableFormat()");
        MeasuredRatioModel instance = new MeasuredRatioModel();
        String expResult = "0 : 0";
        String result = instance.toTableFormat();
        assertEquals(expResult, result);
        instance = new MeasuredRatioModel("Specific",new BigDecimal("213"),"ABS",new BigDecimal("0.4324"),false,true);
        result = instance.toTableFormat();
        expResult="213 : 0.4324";
        assertEquals(expResult, result);
    }
    
    /**
     * Test of isFracCorr method, of class MeasuredRatioModel.
    */
    @Test
    public void test_IsFracCorr() {
        System.out.println("Testing MeasuredRatioModel's isFracCorr()");
        MeasuredRatioModel instance = new MeasuredRatioModel();
        boolean expResult = false;
        boolean result = instance.isFracCorr();
        assertEquals(expResult, result);
        instance = new MeasuredRatioModel("Specific",new BigDecimal("213"),"ABS",new BigDecimal("0.4324"),true,true);
        result = instance.isFracCorr();
        expResult=true;
        assertEquals(expResult, result);
    }
 
    /**
     * Test of setFracCorr method, of class MeasuredRatioModel.
     */
    @Test
    public void test_SetFracCorr() {
        System.out.println("Testing MeasuredRatioModel's setFracCorr(boolean value)");
        MeasuredRatioModel instance = new MeasuredRatioModel();
        boolean expResult = true;
        instance.setFracCorr(true);
        boolean result = instance.isFracCorr();
        assertEquals(expResult, result);
        instance.setFracCorr(false);
        result = instance.isFracCorr();
        expResult=false;
        assertEquals(expResult, result);
    }
    
    /**
     * Test of isOxideCorr method, of class MeasuredRatioModel.
    */
    @Test
    public void test_IsOxideCorr() {
        System.out.println("Testing MeasuredRatioModel's isOxideCorr()");
        MeasuredRatioModel instance = new MeasuredRatioModel();
        boolean expResult = false;
        boolean result = instance.isOxideCorr();
        assertEquals(expResult, result);
        instance = new MeasuredRatioModel("Specific",new BigDecimal("213"),"ABS",new BigDecimal("0.4324"),true,true);
        result = instance.isOxideCorr();
        expResult=true;
        assertEquals(expResult, result);
    }
 
    /**
     * Test of setOxideCorr method, of class MeasuredRatioModel.
     */
    @Test
    public void test_SetOxideCorr() {
        System.out.println("Testing MeasuredRatioModel's setOxideCorr(boolean value)");
        MeasuredRatioModel instance = new MeasuredRatioModel();
        boolean expResult = true;
        instance.setOxideCorr(true);
        boolean result = instance.isOxideCorr();
        assertEquals(expResult, result);
        instance.setOxideCorr(false);
        result = instance.isOxideCorr();
        expResult=false;
        assertEquals(expResult, result);
    }    
    
    /**
     * Test of serializeXMLObject method, of class MeasuredRatioModel.
     */
    @Test
    public void testSerializeXMLObject() {
        //Test to see if serialization is succesful for default. NOTE THIS PATH MUST BE CHANGED FOR EVERY USER!!
        //This is done in this way, without using the read operation to ensure a problem with read does not affect the serialization test.
        System.out.println("Testing MeasuredRatioModel's serializeXMLObject(String filename)");
        String filename= System.getProperty("user.dir");
        filename=filename.concat(File.separator);
        filename=filename.concat("test_SerializeXMLObject_MeasuredRatioModel");
        
        MeasuredRatioModel instance = new MeasuredRatioModel();
        MeasuredRatioModel myValueModel = null;
        //Write file to disk
        instance.serializeXMLObject(filename);
        //Read back
        try{
            XStream xstream = instance.getXStreamReader();
            boolean isValidOrAirplaneMode = instance.validateXML(filename);
            if (isValidOrAirplaneMode) {
               BufferedReader reader = URIHelper.getBufferedReader(filename);
                try {
                    myValueModel = (MeasuredRatioModel) xstream.fromXML(reader);
                    } catch (ConversionException e) {
                    throw new ETException(null, e.getMessage());
                                                    }
                                        }
            }
      catch(java.io.FileNotFoundException | org.earthtime.exceptions.ETException e){
          System.out.println(e);
                        }
        String expResult=instance.getXStreamWriter().toXML(instance);
        String result=instance.getXStreamWriter().toXML(myValueModel);
        assertEquals(expResult,result);
        
        //Tests if serialization works for specified ValueModels
         instance=new MeasuredRatioModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".9876543210"),true,true);    
         myValueModel = null;    
        //Write file to disk
        instance.serializeXMLObject(filename);
        //Read back
        try{
            XStream xstream = instance.getXStreamReader();
            boolean isValidOrAirplaneMode = instance.validateXML(filename);
            if (isValidOrAirplaneMode) {
               BufferedReader reader = URIHelper.getBufferedReader(filename);
                try {
                    myValueModel = (MeasuredRatioModel) xstream.fromXML(reader);
                    } catch (ConversionException e) {
                    throw new ETException(null, e.getMessage());
                                                    }
                                        }
            }
      catch(FileNotFoundException | ETException e){
          System.out.println(e);
                        }
         expResult=instance.getXStreamWriter().toXML(instance);
         result=instance.getXStreamWriter().toXML(myValueModel);
         assertEquals(expResult,result);
    }
    
    /**
     * Test of readXMLObject method, of class MeasuredRatioModel.
     * @throws java.lang.Exception
     */
    @Test
    public void testReadXMLObject() throws Exception {
        //This tests if read is working correctly by writing to a file and reading it back, then comparing the read result.
        System.out.println("Testing MeasuredRatioModel's readXMLObject(String filename, boolean doValidate)");
        String filename= System.getProperty("user.dir");
        filename=filename.concat(File.separator);
        filename=filename.concat("test_ReadXMLObject_MeasuredRatioModel");
        
        MeasuredRatioModel instance = new MeasuredRatioModel();
        //Write file to disk
        instance.serializeXMLObject(filename);
        //Read back
        Object expResult=instance.readXMLObject(filename, true);
        Object result=instance;
        assertEquals(expResult,result);
    
        //Tests if reading works for specified ValueModels
         instance=new MeasuredRatioModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".9876543210"),true,true);    
        //Write file to disk
        instance.serializeXMLObject(filename);
        //Read back
        expResult=instance.readXMLObject(filename, true);
        result=instance;
        assertEquals(expResult,result);
    }
    
    
    
    
    
    
}
