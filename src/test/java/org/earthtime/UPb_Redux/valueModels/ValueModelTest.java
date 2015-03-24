/*
 * ValueModel_Test_01152014Test.java
 *
 * The naming of this file is the name of the .java file being tested, combined
 * with test since this is the test file, combined with the creation date of
 * this file. The test at the end is so that the file runs from NetBeans when
 * testing an entire project.
 *
 * Created on January 15, 2014.
 *
 *Version History:
 *January 15 2014 : File created and method tests began.
 *February 14 2014 : Method and constructor tests completed. Some unfinished
 *                  tests exist at the bottom of the file.
 *
 *
 *Developed for the CIRDLES lab at the College of Charleston by Patrick Brewer.
 *
 *
 */

package org.earthtime.UPb_Redux.valueModels;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import java.math.BigDecimal;
import java.math.MathContext;
import org.earthtime.UPb_Redux.expressions.ExpTreeII;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.UnknownHostException;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.earthtime.exceptions.ETException;
import org.earthtime.archivingTools.URIHelper;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import java.io.File;

/**
 * @author Patrick Brewer
 */

public class ValueModelTest {
      
    /////////////////////////
    ////Constructor Tests////
    /////////////////////////
    
     /**
     * Test of ValueModel() method, of class ValueModel.
     */
    @Test
    public void test_constructor_0(){
	System.out.println("Testing ValueModel's ValueModel()");
        //Tests if values are correct
        ValueModel instance=new ValueModel();
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
        }
        
     /**
     * Test of ValueModel(Name) method, of class ValueModel.
     */    
    @Test
    public void test_constructor_1(){
    	System.out.println("Testing ValueModel's ValueModel(String Name)");    
        //Tests if values are correct
        ValueModel instance=new ValueModel("pkb");
        String expResult="pkb";
        String result=instance.getName();
        assertEquals(expResult,result);
        expResult="NONE";
        result=instance.getUncertaintyType();
        assertEquals(expResult,result);
        BigDecimal expectedResult=new BigDecimal("0");
        BigDecimal actualResult=instance.getValue();
        assertEquals(expectedResult,actualResult);
        actualResult=instance.getOneSigma();
        assertEquals(expectedResult,actualResult);  
    }
    
     /**
     * Test of ValueModel(Name,UncertaintyType) method, of class ValueModel.
     */  
    @Test
    public void test_constructor_2(){
    	System.out.println("Testing ValueModel's ValueModel(String Name,String UncertaintyType)");    
        //Tests if values are correct
        ValueModel instance=new ValueModel("pkb","ABS");
        String expResult="pkb";
        String result=instance.getName();
        assertEquals(expResult,result);
        expResult="ABS";
        result=instance.getUncertaintyType();
        assertEquals(expResult,result);
        BigDecimal expectedResult=new BigDecimal("0");
        BigDecimal actualResult=instance.getValue();
        assertEquals(expectedResult,actualResult);
        actualResult=instance.getOneSigma();
        assertEquals(expectedResult,actualResult); 
    }
       
     /**
     * Test of ValueModel(Name,value,uncertaintyType,oneSigma) method, of class 
     * ValueModel.
     */ 
    @Test
    public void test_constructor_3(){
    	System.out.println("Testing ValueModel's ValueModel(String Name,BigDecimal value,String uncertaintyType,BigDecimal oneSigma)");    
        //Tests if values are correct
        ValueModel instance=new ValueModel("r207_339",new BigDecimal("-12.34567890"),"PCT",new BigDecimal(".9876543210"), BigDecimal.ZERO);                  
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
    }
    
    ////////////////////
    ////Method Tests////
    ////////////////////
    
     /**
     * Test of method "copy", in the file ValueModel.java.
     */
    @Test
    public void test_Copy() {
        //This tests the method with default values.
        System.out.println("Testing ValueModel's copy()");
        ValueModel instance = new ValueModel();
        ValueModel expectedResult = instance;
        ValueModel result = instance.copy();
        assertEquals(expectedResult, result);

        //This tests the method with specified values.
        instance = new ValueModel("testing",new BigDecimal("12.34567890"),"ABS",new BigDecimal("0.987654321"), BigDecimal.ZERO);
        expectedResult = instance;
        result = instance.copy();
        assertEquals(expectedResult, result);
            }

     /**
     * Test of method "copyValuesFrom", in the file ValueModel.java.
     */
    @Test
    public void test_CopyValuesFrom() {
        //This test uses specified values.
        System.out.println("Testing ValueModel's copyValuesFrom(ValueModel parent)");
        ValueModel instance0 = new ValueModel("Specific",new BigDecimal("12.34567890"),"ABS",new BigDecimal("0.987654321"), BigDecimal.ZERO);
        ValueModel blank = new ValueModel("Blank");

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
        instance0 = new ValueModel();
        blank = new ValueModel();
        blank.copyValuesFrom(instance0);
        assertEquals(instance0.value,blank.value);
        assertEquals(instance0.oneSigma,blank.oneSigma);
        assertEquals(instance0.uncertaintyType,blank.uncertaintyType);
        assertEquals(instance0.name,blank.name);
        //This test uses default values, save for the name field.
        instance0 = new ValueModel("Specific");
        blank = new ValueModel("Blank");
       
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
     * Test of method compareTo, in the file ValueModel.java.
     */
    @Test
    public void test_CompareTo() {
        System.out.println("Testing ValueModel's compareTo(ValueModel comparedTo)");
        //Case of 0
        ValueModel valueModel = new ValueModel();
        ValueModel instance = new ValueModel();
        int expResult = 0;
        int result = instance.compareTo(valueModel);
        //Case of Equal
        assertEquals(expResult, result);
        //Case of Greater
        ValueModel filledModel = new ValueModel("r206_204b", new BigDecimal( "12.34567890" ), "ABS", new BigDecimal( "0.987654321" ), BigDecimal.ZERO);
        result = filledModel.compareTo(valueModel);   
        if(result<=0) fail();
        //Case of Lesser
        result = valueModel.compareTo(filledModel);
        if (result>=0) fail(); 
    }
    
     /**
     * Test of method equals, in the file ValueModel.java.
     */
    @Test
    public void test_Equals() {
        System.out.println("Testing ValueModel's equals(ValueModel equalled)");
        //Sets up two identical models and compares using default values
        ValueModel instance=new ValueModel();
        ValueModel blank = new ValueModel();
        boolean expResult=true;
        boolean result=instance.equals(blank);
        assertEquals(expResult,result);
        
        //Sets up two identical models and compares using specified values
        instance=new ValueModel("r206_204b");
        blank= new ValueModel("r206_204b");
        result=instance.equals(blank);
        assertEquals(expResult,result);
        
        //Sets up two different models, one with a default value and compares
        instance=new ValueModel("r206_204b");
        blank=new ValueModel();
        expResult=false;
        result=instance.equals(blank);
        assertEquals(expResult,result);
     
        //Sets up two different models, both specified, to be compared
        instance=new ValueModel("r206_204b");
        blank=new ValueModel("r206_204b2");
        result=instance.equals(blank);
        assertEquals(expResult,result);
     }
    
      /**
     * Test of method hashCode, in the file ValueModel.java.
     */
    @Test
    public void test_HashCode() {
        System.out.println("Testing ValueModel's hashCode()");
        //This method should always return 0.
        ValueModel instance = new ValueModel();
        int expResult = 0;
        int result = instance.hashCode();
        assertEquals(expResult, result);
    }
    
     /**
     * Test of method getReduxLabDataElementName, in the file ValueModel.java.
     */
    @Test
    public void test_GetReduxLabDataElementName() {
        System.out.println("Testing ValueModel's getReduxLabDataElementName()");
        //Testing for specific values
        ValueModel instance = new ValueModel("r206_204b");
        String expResult = "r206_204b";
        String result = instance.getReduxLabDataElementName();
        assertEquals(expResult, result);
        //Testing for default values
        instance=new ValueModel();
        expResult="NONE";
        result=instance.getReduxLabDataElementName();
        assertEquals(expResult,result);
    }
     
    /**
     * Test of method getName, in the file ValueModel.java.
     */
    @Test
    public void testGetName() {
        System.out.println("Testing ValueModel's getName()");
        //Verifying for Specific Values
        ValueModel instance = new ValueModel("r206_204b");
        String expResult = "r206_204b";
        String result = instance.getName();
        assertEquals(expResult, result);
        //Verifying for Default Values
        instance = new ValueModel();
        expResult = "NONE";
        result = instance.getName();
        assertEquals(expResult, result);
    }
    
    
     /**
     * Test of method testSetName of the file ValueMode.java.
     */
    @Test
    public void test_SetName() {
        System.out.println("Testing ValueModel's setName(String newName)");
        //Testing blanking a specified name
        String name = "";
        ValueModel instance = new ValueModel("r206_204b");
        instance.setName(name);
        assertEquals(instance.getName(),name);
        //Testing specifying a blank name
        name="r206_204b";
        instance=new ValueModel();
        instance.setName(name);
        assertEquals(instance.getName(),name);
        //Testing specifying a different specified name
        name="r206_204b";
        instance=new ValueModel("testName");
        instance.setName(name);
        assertEquals(instance.getName(),name);
        //Testing blanking a blank name
        name="";
        instance=new ValueModel();
        instance.setName(name);
        assertEquals(instance.getName(),name);
        //Testing specifying the same non-default name
        name="r206_204b";
        instance=new ValueModel("r206_204b");
        instance.setName(name);
        assertEquals(instance.getName(),name);
    }
    
     /**
     * Test of getValue method, of class ValueModel.
     */
    @Test
    public void test_GetValue() {
        System.out.println("Testing ValueModel's getValue()");
        //Testing with specified values
        ValueModel instance = new ValueModel("r206_204b", new BigDecimal( "12.34567890" ), "ABS", new BigDecimal( "0.987654321" ), BigDecimal.ZERO);
        BigDecimal expResult = new BigDecimal("12.34567890");
        BigDecimal result = instance.getValue();
        assertEquals(expResult, result);
        //Testing with default values
        instance=new ValueModel();
        expResult=new BigDecimal("0");
        result=instance.getValue();
        assertEquals(expResult,result);
    }
    
     /**
     * Test of getValueInUnits method, of class ValueModel.
     */
    @Test
    public void test_GetValueInUnits() {
        System.out.println("Testing ValueModel's getValueInUnits(String units)");
        //Testing with default values for Units ""
        String units = "";
        ValueModel instance = new ValueModel();
        BigDecimal expResult = new BigDecimal("0");
        BigDecimal result = instance.getValueInUnits(units);
        assertEquals(expResult, result);
        //Testing with specified values for Units ""
        instance = new ValueModel("r206_204b", new BigDecimal( "12.34567890" ), "ABS", new BigDecimal( "0.987654321" ), BigDecimal.ZERO);
        expResult = new BigDecimal( "12.34567890" );
        result = instance.getValueInUnits(units);
        assertEquals(expResult, result);
        
        //Testing with default values for units "g"
        units="g";
        instance=new ValueModel();
        expResult=new BigDecimal("0");
        result=instance.getValueInUnits(units);
        assertEquals(expResult,result);
        //Testing with specified values for units "g"
        instance = new ValueModel("r206_204b", new BigDecimal( "12.34567890" ), "ABS", new BigDecimal( "0.987654321" ), BigDecimal.ZERO);
        expResult = new BigDecimal( "12.34567890" );
        result = instance.getValueInUnits(units);
        assertEquals(expResult, result);
        
        //Testing with default values for units "mg"
        units="mg";
        instance=new ValueModel();
        expResult=new BigDecimal("0");
        result=instance.getValueInUnits(units);
        assertEquals(expResult,result);
        //Testing with specified values for units "mg"
        instance = new ValueModel("r206_204b", new BigDecimal( "12.34567890" ), "ABS", new BigDecimal( "0.987654321" ), BigDecimal.ZERO);
        expResult = new BigDecimal( "12345.67890" );
        result = instance.getValueInUnits(units);
        assertEquals(expResult, result);
        
        //Testing with default values for units "/u03bcg"
        units="\u03bcg";
        instance=new ValueModel();
        expResult=new BigDecimal("0");
        result=instance.getValueInUnits(units);
        assertEquals(expResult,result);
        //Testing with specified values for units "\u03bcg"
        instance = new ValueModel("r206_204b", new BigDecimal( "12.34567890" ), "ABS", new BigDecimal( "0.987654321" ), BigDecimal.ZERO);    
        expResult=new BigDecimal("12345678.90");
        result = instance.getValueInUnits(units);
        assertEquals(expResult, result);        
        
        //Testing with default values for units "ng"
        units="ng";
        instance=new ValueModel();
        expResult=new BigDecimal("0");
        result=instance.getValueInUnits(units);
        assertEquals(expResult,result);
        //Testing with specified values for units "ng"
        instance = new ValueModel("r206_204b", new BigDecimal( "12.34567890" ), "ABS", new BigDecimal( "0.987654321" ), BigDecimal.ZERO);
        expResult = new BigDecimal( "12345678900" );
        result = instance.getValueInUnits(units);
        assertEquals(expResult, result);
        
        //Testing with default values for units "pg"
        units="pg";
        instance=new ValueModel();
        expResult=new BigDecimal("0");
        result=instance.getValueInUnits(units);
        assertEquals(expResult,result);
        //Testing with specified values for units "ng"
        instance = new ValueModel("r206_204b", new BigDecimal( "12.34567890" ), "ABS", new BigDecimal( "0.987654321" ), BigDecimal.ZERO);
        expResult = new BigDecimal( "12345678900000" );
        result = instance.getValueInUnits(units);
        assertEquals(expResult, result);
        
        //Testing with default values for units "fg"
        units="fg";
        instance=new ValueModel();
        expResult=new BigDecimal("0");
        result=instance.getValueInUnits(units);
        assertEquals(expResult,result);
        //Testing with specified values for units "fg"
        instance = new ValueModel("r206_204b", new BigDecimal( "12.34567890" ), "ABS", new BigDecimal( "0.987654321" ), BigDecimal.ZERO);
        expResult = new BigDecimal( "12345678900000000" );
        result = instance.getValueInUnits(units);
        assertEquals(expResult, result);       
        
        //Testing with default values for units "\u0025"
        units="\u0025";
        instance=new ValueModel();
        expResult=new BigDecimal("0");
        result=instance.getValueInUnits(units);
        assertEquals(expResult,result);
        //Testing with specified values for units "\u0025"
        instance = new ValueModel("r206_204b", new BigDecimal( "12.34567890" ), "ABS", new BigDecimal( "0.987654321" ), BigDecimal.ZERO);
        expResult = new BigDecimal( "1234.567890" );
        result = instance.getValueInUnits(units);
        assertEquals(expResult, result);          
        
        //Testing with default values for units "\u2030"
        units="\u2030";
        instance=new ValueModel();
        expResult=new BigDecimal("0");
        result=instance.getValueInUnits(units);
        assertEquals(expResult,result);
        //Testing with specified values for units "\u2030"
        instance = new ValueModel("r206_204b", new BigDecimal( "12.34567890" ), "ABS", new BigDecimal( "0.987654321" ), BigDecimal.ZERO);
        expResult = new BigDecimal( "12345.67890" );
        result = instance.getValueInUnits(units);
        assertEquals(expResult, result);           
        
        //Testing with default values for units "ppm"
        units="ppm";
        instance=new ValueModel();
        expResult=new BigDecimal("0");
        result=instance.getValueInUnits(units);
        assertEquals(expResult,result);
        //Testing with specified values for units "ppm"
        instance = new ValueModel("r206_204b", new BigDecimal( "12.34567890" ), "ABS", new BigDecimal( "0.987654321" ), BigDecimal.ZERO);
        expResult = new BigDecimal( "12345678.90" );
        result = instance.getValueInUnits(units);
        assertEquals(expResult, result);              
        
        //Testing with default values for units "ppb"
        units="ppb";
        instance=new ValueModel();
        expResult=new BigDecimal("0");
        result=instance.getValueInUnits(units);
        assertEquals(expResult,result);
        //Testing with specified values for units "ppb"
        instance = new ValueModel("r206_204b", new BigDecimal( "12.34567890" ), "ABS", new BigDecimal( "0.987654321" ), BigDecimal.ZERO);
        expResult = new BigDecimal( "12345678900" );
        result = instance.getValueInUnits(units);
        assertEquals(expResult, result);                 
 
        //Testing with default values for units "g/g"
        units="g/g";
        instance=new ValueModel();
        expResult=new BigDecimal("0");
        result=instance.getValueInUnits(units);
        assertEquals(expResult,result);
        //Testing with specified values for units "g/g"
        instance = new ValueModel("r206_204b", new BigDecimal( "12.34567890" ), "ABS", new BigDecimal( "0.987654321" ), BigDecimal.ZERO);
        expResult = new BigDecimal( "12.34567890" );
        result = instance.getValueInUnits(units);
        assertEquals(expResult, result);           
        
         //Testing with default values for units "a"
        units="a";
        instance=new ValueModel();
        expResult=new BigDecimal("0");
        result=instance.getValueInUnits(units);
        assertEquals(expResult,result);
        //Testing with specified values for units "a"
        instance = new ValueModel("r206_204b", new BigDecimal( "12.34567890" ), "ABS", new BigDecimal( "0.987654321" ), BigDecimal.ZERO);
        expResult = new BigDecimal( "12.34567890" );
        result = instance.getValueInUnits(units);
        assertEquals(expResult, result);        
        
        //Testing with default values for units "ka"
        units="ka";
        instance=new ValueModel();
        expResult=new BigDecimal(".000");
        result=instance.getValueInUnits(units);
        assertEquals(expResult,result);
        //Testing with specified values for units "ka"
        instance = new ValueModel("r206_204b", new BigDecimal( "12.34567890" ), "ABS", new BigDecimal( "0.987654321" ), BigDecimal.ZERO);
        expResult = new BigDecimal( ".01234567890" );
        result = instance.getValueInUnits(units);
        assertEquals(expResult, result);          
  
        //Testing with default values for units "Ma"
        units="Ma";
        instance=new ValueModel();
        expResult=new BigDecimal(".000000");
        result=instance.getValueInUnits(units);
        assertEquals(expResult,result);
        //Testing with specified values for units "Ma"
        instance = new ValueModel("r206_204b", new BigDecimal( "12.34567890" ), "ABS", new BigDecimal( "0.987654321" ), BigDecimal.ZERO);
        expResult = new BigDecimal( ".00001234567890" );
        result = instance.getValueInUnits(units);
        assertEquals(expResult, result);           
        
         //Testing with default values for units "Ga"
        units="Ga";
        instance=new ValueModel();
        expResult=new BigDecimal(".000000000");
        result=instance.getValueInUnits(units);
        assertEquals(expResult,result);
        //Testing with specified values for units "Ga"
        instance = new ValueModel("r206_204b", new BigDecimal( "12.34567890" ), "ABS", new BigDecimal( "0.987654321" ), BigDecimal.ZERO);
        expResult = new BigDecimal( ".00000001234567890" );
        result = instance.getValueInUnits(units);
        assertEquals(expResult, result);         
        
        //Testing with default values for units "%/amu"
        units="%/amu";
        instance=new ValueModel();
        expResult=new BigDecimal("0");
        result=instance.getValueInUnits(units);
        assertEquals(expResult,result);
        //Testing with specified values for units "%/amu"
        instance = new ValueModel("r206_204b", new BigDecimal( "12.34567890" ), "ABS", new BigDecimal( "0.987654321" ), BigDecimal.ZERO);
        expResult = new BigDecimal( "1234.567890" );
        result = instance.getValueInUnits(units);
        assertEquals(expResult, result);          
        
        //Testing with default values for units "ns"
        units="ns";
        instance=new ValueModel();
        expResult=new BigDecimal("0");
        result=instance.getValueInUnits(units);
        assertEquals(expResult,result);
        //Testing with specified values for units "ns"
        instance = new ValueModel("r206_204b", new BigDecimal( "12.34567890" ), "ABS", new BigDecimal( "0.987654321" ), BigDecimal.ZERO);
        expResult = new BigDecimal( "12345678900" );
        result = instance.getValueInUnits(units);
        assertEquals(expResult, result);     
    }
    
    /**
     * Test of setValue method, of class ValueModel.
     */
    @Test
    public void test_SetValue_BigDecimal() {
        System.out.println("Testing ValueModel's setValue(BigDecimal newValue)");
        //Testing blank to null
        BigDecimal value = null;
        ValueModel instance = new ValueModel();
        instance.setValue(value);
        assertEquals(instance.getValue(),value);
        //Testing blank to specific
        value=new BigDecimal("12.34567890");
        instance=new ValueModel();
        instance.setValue(value);
        assertEquals(instance.getValue(),value);
        //Testing blank to blank
        value=new BigDecimal("0");
        instance=new ValueModel();
        instance.setValue(value);
        assertEquals(instance.getValue(),value);
        //Testing specific to blank
        instance=new ValueModel("r206_204b", new BigDecimal( "12.34567890" ), "ABS", new BigDecimal( "0.987654321" ), BigDecimal.ZERO);
        instance.setValue(value);
        assertEquals(instance.getValue(),value);
        //Testing specific to a different specific
        value=new BigDecimal("0.987654321");
        instance=new ValueModel("r206_204b", new BigDecimal( "12.34567890" ), "ABS", new BigDecimal( "0.987654321" ), BigDecimal.ZERO);
        instance.setValue(value);
        assertEquals(instance.getValue(),value);
        //Testing specific to null
        value=null;
        instance=new ValueModel("r206_204b", new BigDecimal( "12.34567890" ), "ABS", new BigDecimal( "0.987654321" ), BigDecimal.ZERO);
        instance.setValue(value);
        assertEquals(instance.getValue(),value);
        //Testing specific to same specific
        value=new BigDecimal("12.34567890");
        instance=new ValueModel("r206_204b", new BigDecimal( "12.34567890" ), "ABS", new BigDecimal( "0.987654321" ), BigDecimal.ZERO);
        instance.setValue(value);
        assertEquals(instance.getValue(),value);        
    }
    
     /**
     * Test of setValue method, of class ValueModel.
     */
    @Test
    public void test_SetValue_double() {
        System.out.println("Testing ValueModel's setValue(double newValue)");
        //Blank to blank
        double value = 0.0;
        ValueModel instance = new ValueModel();
        instance.setValue(value);
        assertEquals(instance.getValue(),new BigDecimal(value, MathContext.DECIMAL64));
        //Blank to Specific
        value = 2.0;
        instance = new ValueModel();
        instance.setValue(value);
        assertEquals(instance.getValue(),new BigDecimal(value, MathContext.DECIMAL64));
        //Specific to Blank
        value = 0.0;
        instance = new ValueModel("r206_204b", new BigDecimal( "12.34567890" ), "ABS", new BigDecimal( "0.987654321" ), BigDecimal.ZERO);
        instance.setValue(value);
        assertEquals(instance.getValue(),new BigDecimal(value, MathContext.DECIMAL64));
        //Specific to different Specific
        value = 2.0;
        instance =new ValueModel("r206_204b", new BigDecimal( "12.34567890" ), "ABS", new BigDecimal( "0.987654321" ), BigDecimal.ZERO);
        instance.setValue(value);
        assertEquals(instance.getValue(),new BigDecimal(value, MathContext.DECIMAL64));
        //Specific to same Specific
        value = 2.0;
        instance =new ValueModel("r206_204b", new BigDecimal( "2" ), "ABS", new BigDecimal( "0.987654321" ), BigDecimal.ZERO);
        instance.setValue(value);
        assertEquals(instance.getValue(),new BigDecimal(value, MathContext.DECIMAL64));
    }

     /**
     * Test of getUncertaintyType method, of class ValueModel.
     */
    @Test
    public void test_GetUncertaintyType() {
        System.out.println("Testing ValueModel's getUncertaintyType()");
        //UncertaintyType ABS
        ValueModel instance =new ValueModel("r206_204b", new BigDecimal( "12.34567890" ), "ABS", new BigDecimal( "0.987654321" ), BigDecimal.ZERO);
        String expResult = "ABS";
        String result = instance.getUncertaintyType();
        assertEquals(expResult, result);
        //UncertaintyType PCT
        instance =new ValueModel("r206_204b", new BigDecimal( "12.34567890" ), "PCT", new BigDecimal( "0.987654321" ), BigDecimal.ZERO);
        expResult = "PCT";
        result = instance.getUncertaintyType();
        assertEquals(expResult, result);
        //UncertaintyType Default ("NONE")
        instance =new ValueModel();
        expResult = "NONE";
        result = instance.getUncertaintyType();
        assertEquals(expResult, result);        
    }

    /**
     * Test of setUncertaintyType method, of class ValueModel.
     */
    @Test
    public void test_SetUncertaintyType() {
        System.out.println("Testing ValueModel's setUncertaintyType(String newType)");
        //NONE to ABS
        String uncertaintyType = "ABS";
        ValueModel instance = new ValueModel();
        instance.setUncertaintyType(uncertaintyType);
        assertEquals(instance.getUncertaintyType(),uncertaintyType);
        //NONE to PCT
        uncertaintyType = "PCT";
        instance = new ValueModel();
        instance.setUncertaintyType(uncertaintyType);  
        assertEquals(instance.getUncertaintyType(),uncertaintyType);
        //ABS to PCT
        uncertaintyType="PCT";
        instance = new ValueModel("r206_204b", new BigDecimal( "12.34567890" ), "ABS", new BigDecimal( "0.987654321" ), BigDecimal.ZERO);
        instance.setUncertaintyType(uncertaintyType);  
        assertEquals(instance.getUncertaintyType(),uncertaintyType);
        //ABS to NONE
        uncertaintyType="NONE";
        instance = new ValueModel("r206_204b", new BigDecimal( "12.34567890" ), "ABS", new BigDecimal( "0.987654321" ), BigDecimal.ZERO);
        instance.setUncertaintyType(uncertaintyType);  
        assertEquals(instance.getUncertaintyType(),uncertaintyType);        
        //PCT to ABS
        uncertaintyType="ABS";
        instance = new ValueModel("r206_204b", new BigDecimal( "12.34567890" ), "PCT", new BigDecimal( "0.987654321" ), BigDecimal.ZERO);
        instance.setUncertaintyType(uncertaintyType);  
        assertEquals(instance.getUncertaintyType(),uncertaintyType);        
        //PCT to NONE
        uncertaintyType="NONE";
        instance = new ValueModel("r206_204b", new BigDecimal( "12.34567890" ), "PCT", new BigDecimal( "0.987654321" ), BigDecimal.ZERO);
        instance.setUncertaintyType(uncertaintyType);  
        assertEquals(instance.getUncertaintyType(),uncertaintyType);        
    }
    
    /**
     * Test of setUncertaintyTypeABS method, of class ValueModel.
     */
    @Test
    public void test_SetUncertaintyTypeABS() {
        System.out.println("Testing ValueModel's setUncertaintyTypeABS()");
        //None to ABS
        ValueModel instance = new ValueModel();
        instance.setUncertaintyTypeABS();
        assertEquals(instance.getUncertaintyType(),"ABS");
        //ABS to ABS
        instance.setUncertaintyTypeABS();        
        assertEquals(instance.getUncertaintyType(),"ABS");
        //PCT to ABS
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".98765432210"), BigDecimal.ZERO);
        instance.setUncertaintyTypeABS();
        assertEquals(instance.getUncertaintyType(),"ABS");
    }
    
    /**
     * Test of setUncertaintyTypePCT method, of class ValueModel.
     */
    @Test
    public void test_SetUncertaintyTypePCT() {
        //None to PCT
        System.out.println("Testing ValueModel's setUncertaintyTypePCT()");
        ValueModel instance = new ValueModel();
        instance.setUncertaintyTypePCT();
        assertEquals(instance.getUncertaintyType(),"PCT");
        //PCT to PCT
        instance.setUncertaintyTypePCT();        
        assertEquals(instance.getUncertaintyType(),"PCT");
        //ABS to PCT
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"ABS",new BigDecimal(".98765432210"), BigDecimal.ZERO);
        instance.setUncertaintyTypePCT();
        assertEquals(instance.getUncertaintyType(),"PCT");
    }    
    
    /**
     * Test of getOneSigma method, of class ValueModel.
     */
    @Test
    public void test_GetOneSigma() {
        System.out.println("Testing ValueModel's getOneSigma()");
        //Get Default (0)
        ValueModel instance = new ValueModel();
        BigDecimal expResult = new BigDecimal("0");
        BigDecimal result = instance.getOneSigma();
        assertEquals(expResult, result);
        //Get Specific
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"ABS",new BigDecimal(".98765432210"), BigDecimal.ZERO);
        expResult=new BigDecimal(".98765432210");
        result=instance.getOneSigma();
        assertEquals(expResult,result);
    }
    
    /**
     * Test of setOneSigma method, of class ValueModel.
     */
    @Test
    public void testSetOneSigma_BigDecimal() {
        System.out.println("Testing ValueModel's setOneSigma(BigDecimal newOneSigma)");
        //0 to specific
        BigDecimal oneSigma = new BigDecimal(".98765432210");
        ValueModel instance = new ValueModel();
        instance.setOneSigma(oneSigma);
        assertEquals(instance.getOneSigma(),oneSigma);
        //Specific to 0
        oneSigma=new BigDecimal("0");
        instance.setOneSigma(oneSigma);
        assertEquals(instance.getOneSigma(),oneSigma);
        //0 to 0
        instance.setOneSigma(oneSigma);
        assertEquals(instance.getOneSigma(),oneSigma);
        //Specific to different specific
        oneSigma=new BigDecimal(".98765432210");
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"ABS",new BigDecimal(".123456789"), BigDecimal.ZERO);
        instance.setOneSigma(oneSigma);
        assertEquals(instance.getOneSigma(),oneSigma);
        //Specific to same specific
        instance.setOneSigma(oneSigma);        
        assertEquals(instance.getOneSigma(),oneSigma);   
    }
    
    /**
     * Test of setOneSigma method, of class ValueModel.
     */
    @Test
    public void test_SetOneSigma_double() {
        System.out.println("Testing ValueModel's setOneSigma(double newOneSigma)");
        double oneSigma = 0.0;
        ValueModel instance = new ValueModel();
        instance.setOneSigma(oneSigma);
        assertEquals(instance.getOneSigma(),new BigDecimal(oneSigma, MathContext.DECIMAL64));
        //Specific to 0
        oneSigma=0.0;
        instance.setOneSigma(oneSigma);
        assertEquals(instance.getOneSigma(),new BigDecimal(oneSigma, MathContext.DECIMAL64));
        //0 to 0
        instance.setOneSigma(oneSigma);
        assertEquals(instance.getOneSigma(),new BigDecimal(oneSigma, MathContext.DECIMAL64));
        //Specific to different specific
        oneSigma=2.0;
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"ABS",new BigDecimal(".123456789"), BigDecimal.ZERO);
        instance.setOneSigma(oneSigma);
        assertEquals(instance.getOneSigma(),new BigDecimal(oneSigma, ReduxConstants.mathContext15));
        //Specific to same specific
        instance.setOneSigma(oneSigma);        
        assertEquals(instance.getOneSigma(),new BigDecimal(oneSigma, ReduxConstants.mathContext15));
    }
    
    /**
     * Test of getOneSigmaAbs method, of class ValueModel.
     */
    @Test
    public void test_GetOneSigmaAbs() {
        System.out.println("Testing ValueModel's getOneSigmaAbs()");
        //Get default
        ValueModel instance = new ValueModel();
        BigDecimal expResult = new BigDecimal("0");
        BigDecimal result = instance.getOneSigmaAbs();
        assertEquals(expResult, result);
        //Get PCT
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".123456789"), BigDecimal.ZERO);
        result = instance.getOneSigmaAbs();
        expResult=instance.oneSigma.multiply(instance.value, ReduxConstants.mathContext15 ).movePointLeft( 2 );
        assertEquals(expResult,result);
        //Get ABS
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"ABS",new BigDecimal(".123456789"), BigDecimal.ZERO);
        result=instance.getOneSigmaAbs();
        expResult=new BigDecimal(".123456789");
        assertEquals(expResult,result);
    }
    
    /**
     * Test of amPositiveAndLessThanTolerance method, of class ValueModel.
     */
    @Test
    public void test_AmPositiveAndLessThanTolerance() {
        System.out.println("Testing ValueModel's amPositiveAndLessThanTolerance()");
        //Is positive and less than tolerance
        ValueModel instance =new ValueModel("r207_339",new BigDecimal("11e-25"),"ABS",new BigDecimal(".123456789"), BigDecimal.ZERO);
        boolean expResult = true;
        boolean result = instance.amPositiveAndLessThanTolerance();   
        assertEquals(expResult, result);   
        //positive and more than tolerance
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"ABS",new BigDecimal(".123456789"), BigDecimal.ZERO);
        expResult=false;
        result = instance.amPositiveAndLessThanTolerance();   
        assertEquals(expResult,result);
        //Equal to tolerance
        instance=new ValueModel("r207_339",new BigDecimal("10e-20"),"ABS",new BigDecimal(".123456789"), BigDecimal.ZERO);
        expResult=false;
        result = instance.amPositiveAndLessThanTolerance();   
        assertEquals(expResult,result);
        //Negative and less than tolerance
        instance=new ValueModel("r207_339",new BigDecimal("-15e-25"),"ABS",new BigDecimal(".123456789"), BigDecimal.ZERO);
        expResult=false;
        result = instance.amPositiveAndLessThanTolerance();   
        assertEquals(expResult,result);    
    }
   
    /**
     * Test of convertOneSigmaAbsToPctIfRequired method, of class ValueModel.
     */
    @Test
    public void test_ConvertOneSigmaAbsToPctIfRequired() {
        System.out.println("Testing ValueModel's convertOneSigmaAbsToPctIfRequired(ValueModel valueModel ,BigDecimal oneSigmaAbs)");
        //If ValueModel has default OneSigma
        ValueModel valueModel = new ValueModel();
        BigDecimal oneSigmaAbs = new BigDecimal(".123456789");
        BigDecimal expResult =  new BigDecimal(".123456789");        
        BigDecimal result = ValueModel.convertOneSigmaAbsToPctIfRequired(valueModel, oneSigmaAbs); 
        assertEquals(expResult, result);
        //If ValueModel has PCT    
        valueModel=new ValueModel("r207_339",new BigDecimal("112.34567890"),"PCT",new BigDecimal(".123456789"), BigDecimal.ZERO);
        oneSigmaAbs=new BigDecimal(".1234567890");
        expResult=oneSigmaAbs.divide(valueModel.getValue(), ReduxConstants.mathContext15 ).movePointRight( 2);
        result = ValueModel.convertOneSigmaAbsToPctIfRequired(valueModel, oneSigmaAbs);
        assertEquals(expResult, result);  
        //If Valuemodel has ABS
        valueModel=new ValueModel("r207_339",new BigDecimal("12.34567890"),"ABS",new BigDecimal(".123456789"), BigDecimal.ZERO);
        oneSigmaAbs=new BigDecimal(".123456789");
        expResult=oneSigmaAbs;
        result = ValueModel.convertOneSigmaAbsToPctIfRequired(valueModel, oneSigmaAbs);
        assertEquals(expResult, result); 
    }
       
    /**
     * Test of convertOneSigmaPctToAbsIfRequired method, of class ValueModel.
     */
    @Test
    public void test_ConvertOneSigmaPctToAbsIfRequired() {
        System.out.println("Testing ValueModel's convertOneSigmaPctToAbsIfRequired(ValueModel valueModel,BigDecimal OneSigmaPct)");
        //If ValueModel has default OneSigma
        ValueModel valueModel = new ValueModel();
        BigDecimal oneSigmaPct = new BigDecimal(".123456789");
        BigDecimal expResult =  new BigDecimal(".123456789");        
        BigDecimal result = ValueModel.convertOneSigmaAbsToPctIfRequired(valueModel, oneSigmaPct); 
        assertEquals(expResult, result);
        //If ValueModel has PCT    
        valueModel=new ValueModel("r207_339",new BigDecimal("112"),"PCT",new BigDecimal(".50"), BigDecimal.ZERO);
        oneSigmaPct=new BigDecimal(".50");
        expResult=oneSigmaPct.divide(valueModel.getValue(), ReduxConstants.mathContext15 ).movePointRight( 2 );
        result = ValueModel.convertOneSigmaAbsToPctIfRequired(valueModel, oneSigmaPct);
        assertEquals(expResult, result);  
        //If Valuemodel has ABS
        valueModel=new ValueModel("r207_339",new BigDecimal("12.34567890"),"ABS",new BigDecimal(".1234"), BigDecimal.ZERO);
        oneSigmaPct=new BigDecimal(".1234");    
        expResult=oneSigmaPct;
        result = ValueModel.convertOneSigmaAbsToPctIfRequired(valueModel, oneSigmaPct);
        assertEquals(expResult, result); 
    }
    
     /**
     * Test of getOneSigmaPct method, of class ValueModel.
     */
    @Test
    public void test_GetOneSigmaPct() {
        System.out.println("Testing ValueModel's getOneSigmaPct()");
        //Default value
        ValueModel instance = new ValueModel();
        BigDecimal expResult = new BigDecimal("0");
        BigDecimal result = instance.getOneSigmaPct();
        assertEquals(expResult, result);
        //Value is PCT
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".1234"), BigDecimal.ZERO);
        expResult=new BigDecimal(".1234");
        result=instance.getOneSigmaPct();
        assertEquals(expResult, result);
        //value is ABS
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"ABS",new BigDecimal(".1234"), BigDecimal.ZERO);    
        expResult=instance.oneSigma.divide(instance.value, ReduxConstants.mathContext15 ).movePointRight( 2 );
        result=instance.getOneSigmaPct();       
        assertEquals(expResult, result); 
    }
   
    /**
     * Test of toggleUncertaintyType method, of class ValueModel.
     */
    @Test
    public void test_ToggleUncertaintyType() {
        System.out.println("Testing ValueModel's toggleUncertaintyType()");
        //default
        ValueModel instance = new ValueModel();
        String expResult="PCT";
        instance.toggleUncertaintyType();
        String result=instance.getUncertaintyType();
        assertEquals(expResult,result);
        //abs to pct
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"ABS",new BigDecimal(".1234"), BigDecimal.ZERO);    
        expResult="PCT";
        instance.toggleUncertaintyType();
        result=instance.getUncertaintyType();
        assertEquals(expResult,result);
        //pct to abs
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".1234"), BigDecimal.ZERO);    
        expResult="ABS";
        instance.toggleUncertaintyType();
        result=instance.getUncertaintyType();
        assertEquals(expResult,result);
    }

    /**
     * Test of getTwoSigmaAbs method, of class ValueModel.
     */
    @Test
    public void test_GetTwoSigmaAbs() {
        System.out.println("Testing ValueModel's getTwoSigmaAbs()");
        //default value of 0
        ValueModel instance = new ValueModel();
        BigDecimal expResult = new BigDecimal("0.0");
        BigDecimal result = instance.getTwoSigmaAbs();
        assertEquals(expResult, result);
        //negative specified value
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal("-5.2"), BigDecimal.ZERO);    
        expResult=new BigDecimal( "2.0" ).multiply( instance.getOneSigmaAbs() );
        result = instance.getTwoSigmaAbs();
        assertEquals(expResult, result);        
        //specified positive value
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=new BigDecimal( "2.0" ).multiply( instance.getOneSigmaAbs() );        
        result = instance.getTwoSigmaAbs();
        assertEquals(expResult, result);        
    }
     
    /**
     * Test of getTwoSigmaPct method, of class ValueModel.
     */
    @Test
    public void test_GetTwoSigmaPct() {
        System.out.println("Testing ValueModel's getTwoSigmaPct()");
        //default value of 0
        ValueModel instance = new ValueModel();
        BigDecimal expResult = new BigDecimal("0.0");
        BigDecimal result = instance.getTwoSigmaPct();
        assertEquals(expResult, result);
        //negative specified value
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal("-5.2"), BigDecimal.ZERO);    
        expResult=new BigDecimal( "2.0" ).multiply( instance.getOneSigmaPct() );
        result = instance.getTwoSigmaPct();
        assertEquals(expResult, result);        
        //specified positive value
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=new BigDecimal( "2.0" ).multiply( instance.getOneSigmaPct() );        
        result = instance.getTwoSigmaPct();
        assertEquals(expResult, result);        
    }
    
    /**
     * Test of getTwoSigma method, of class ValueModel.
     */
    @Test
    public void testGet_TwoSigma() {
        System.out.println("Testing ValueModel's getTwoSigma(String uncertaintyType,String units)");
        //ABS uncertainty blank units, default value
        String uncertaintyType = "ABS";
        String units = "";
        ValueModel instance = new ValueModel();
        BigDecimal expResult = new BigDecimal("0.0");
        BigDecimal result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);        
        //ABS uncertainty blank units, specified value
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        int shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        expResult=instance.getTwoSigmaAbs().movePointRight( shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);              
        //ABS uncertainty g units, default value
        units="g";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaAbs().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result); 
        //ABS uncertainty g units, specified value
        units="g";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaAbs().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //ABS uncertainty mg units, default value
        units="mg";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaAbs().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //ABS uncertainty mg units, specified value
        units="mg";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaAbs().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //ABS uncertainty \u03bcg units, default value
        units="\u03bcg";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaAbs().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //ABS uncertainty \u03bcg units, specified value
        units="\u03bcg";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaAbs().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //ABS uncertainty ng units, default value
        units="ng";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaAbs().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //ABS uncertainty ng units, specified value
        units="ng";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaAbs().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);        
        //ABS uncertainty pg units, default value
        units="pg";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaAbs().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //ABS uncertainty pg units, specified value
        units="pg";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaAbs().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //ABS uncertainty fg units, default value
        units="fg";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaAbs().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //ABS uncertainty fg units, specified value
        units="fg";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaAbs().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //ABS uncertainty \u0025 units, default value
        units="\u0025";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaAbs().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //ABS uncertainty \u0025 units, specified value
        units="\u0025";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaAbs().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //ABS uncertainty \u0030 units, default value
        units="\u2030";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaAbs().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //ABS uncertainty \u0030 units, specified value
        units="\u2030";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaAbs().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //ABS uncertainty ppm units, default value
        units="ppm";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaAbs().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //ABS uncertainty ppm units, specified value
        units="ppm";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaAbs().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //ABS uncertainty ppb units, default value
        units="ppb";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaAbs().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //ABS uncertainty ppb units, specified value
        units="ppb";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaAbs().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //ABS uncertainty g/g units, default value
        units="g/g";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaAbs().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //ABS uncertainty g/g units, specified value
        units="g/g";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaAbs().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //ABS uncertainty a units, default value
        units="a";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaAbs().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //ABS uncertainty a units, specified value
        units="a";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaAbs().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //ABS uncertainty ka units, default value
        units="ka";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaAbs().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //ABS uncertainty ka units, specified value
        units="ka";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaAbs().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //ABS uncertainty Ma units, default value
        units="Ma";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaAbs().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //ABS uncertainty Ma units, specified value
        units="Ma";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaAbs().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //ABS uncertainty Ga units, default value
        units="Ga";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaAbs().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //ABS uncertainty Ga units, specified value
        units="Ga";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaAbs().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //ABS uncertainty %/amu units, default value
        units="%/amu";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaAbs().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //ABS uncertainty %/amu units, specified value
        units="%/amu";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaAbs().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //ABS uncertainty ns units, default value
        units="ns";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaAbs().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //ABS uncertainty ns units, specified value
        units="ns";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaAbs().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        
        //PCT uncertainty blank units, default value
        uncertaintyType = "PCT";
        units = "";
        instance = new ValueModel();
        expResult = new BigDecimal("0.0");
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);        
        //PCT uncertainty blank units, specified value
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        expResult=instance.getTwoSigmaPct().movePointRight( shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);              
        //PCT uncertainty g units, default value
        units="g";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaPct().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result); 
        //PCT uncertainty g units, specified value
        units="g";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaPct().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //PCT uncertainty mg units, default value
        units="mg";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaPct().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //PCT uncertainty mg units, specified value
        units="mg";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaPct().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //PCT uncertainty \u03bcg units, default value
        units="\u03bcg";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaPct().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //PCT uncertainty \u03bcg units, specified value
        units="\u03bcg";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaPct().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //PCT uncertainty ng units, default value
        units="ng";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaPct().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //PCT uncertainty ng units, specified value
        units="ng";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaPct().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);        
        //PCT uncertainty pg units, default value
        units="pg";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaPct().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //PCT uncertainty pg units, specified value
        units="pg";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaPct().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //PCT uncertainty fg units, default value
        units="fg";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaPct().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //PCT uncertainty fg units, specified value
        units="fg";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaPct().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //PCT uncertainty \u0025 units, default value
        units="\u0025";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaPct().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //PCT uncertainty \u0025 units, specified value
        units="\u0025";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaPct().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //PCT uncertainty \u0030 units, default value
        units="\u2030";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaPct().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //PCT uncertainty \u0030 units, specified value
        units="\u2030";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaPct().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //PCT uncertainty ppm units, default value
        units="ppm";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaPct().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //PCT uncertainty ppm units, specified value
        units="ppm";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaPct().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //PCT uncertainty ppb units, default value
        units="ppb";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaPct().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //PCT uncertainty ppb units, specified value
        units="ppb";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaPct().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //PCT uncertainty g/g units, default value
        units="g/g";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaPct().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //PCT uncertainty g/g units, specified value
        units="g/g";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaPct().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //PCT uncertainty a units, default value
        units="a";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaPct().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //PCT uncertainty a units, specified value
        units="a";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaPct().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //PCT uncertainty ka units, default value
        units="ka";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaPct().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //PCT uncertainty ka units, specified value
        units="ka";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaPct().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //PCT uncertainty Ma units, default value
        units="Ma";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaPct().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //PCT uncertainty Ma units, specified value
        units="Ma";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaPct().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //PCT uncertainty Ga units, default value
        units="Ga";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaPct().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //PCT uncertainty Ga units, specified value
        units="Ga";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaPct().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //PCT uncertainty %/amu units, default value
        units="%/amu";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaPct().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //PCT uncertainty %/amu units, specified value
        units="%/amu";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaPct().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //PCT uncertainty ns units, default value
        units="ns";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaPct().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
        //PCT uncertainty ns units, specified value
        units="ns";
        shiftPointRightCount = ReduxConstants.getUnitConversionMoveCount( units );
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".01234567890"), BigDecimal.ZERO);    
        expResult=instance.getTwoSigmaPct().movePointRight(shiftPointRightCount );          
        result = instance.getTwoSigma(uncertaintyType, units);
        assertEquals(expResult, result);
    }
    
     /**
     * Test of formatValueAndOneSigmaABSForTesting method, of class ValueModel.
     */
    @Test
    public void test_FormatValueAndOneSigmaABSForTesting() {
        System.out.println("Testing ValueModel's formatValueAndOneSigmaABSForTesting()");
        //blank valuemodel
        ValueModel instance = new ValueModel();
        String expResult="   NONE                             = 0.0000000000E0      ";
        String result=instance.formatValueAndOneSigmaABSForTesting();
        assertEquals(expResult, result);

        //specified valuemodel
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".9876543210"), BigDecimal.ZERO);    
        result=instance.formatValueAndOneSigmaABSForTesting();
        expResult="   r207_339                         = 1.2345678900E1      1-SigmaAbs = 1.2193263111E-1     ";
        assertEquals(expResult,result);
    }
   
     /**
     * Test of calculateCountOfDigitsAfterDecPoint method, of class ValueModel.
     */
    @Test
    public void test_CalculateCountOfDigitsAfterDecPoint() {
        System.out.println("Testing ValueModel's calculateCountOfDigitsAfterDecPoint(String twoSigError)");
        //works normally
        String twoSigError = ".9876543210";
        ValueModel instance = new ValueModel();
        int expResult = 10;
        int result = instance.calculateCountOfDigitsAfterDecPoint(twoSigError);
        assertEquals(expResult, result);
        //works with no .
        twoSigError="987654321";
        expResult=0;
        result = instance.calculateCountOfDigitsAfterDecPoint(twoSigError);
        assertEquals(expResult, result);
    }

     /**
     * Test of formatValueAndTwoSigmaForPublicationSigDigMode method, of class ValueModel.
     */
    @Test
    public void test_FormatValueAndTwoSigmaForPublicationSigDigMode() {
        System.out.println("Testing ValueModel's formatValueAndTwoSigmaForPublicationSigDigMode(String uncertaintyType, int MovePointRightCount, int uncertaintySigDigits)");
        //default abs without specified model
        String uncertaintyType = "ABS";
        int movePointRightCount = 0;
        int uncertaintySigDigits = 0;
        ValueModel instance = new ValueModel();
        String expResult = "0 ± 0";
        String result = instance.formatValueAndTwoSigmaForPublicationSigDigMode(uncertaintyType, movePointRightCount, uncertaintySigDigits);
        assertEquals(expResult, result);       
        //default pct without specifiedmodel
        uncertaintyType = "PCT";
        movePointRightCount = 0;
        uncertaintySigDigits = 0;
        expResult = "0 ± 0";
        result = instance.formatValueAndTwoSigmaForPublicationSigDigMode(uncertaintyType, movePointRightCount, uncertaintySigDigits);
        assertEquals(expResult, result);
        //default ABS with specified model
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".9876543210"), BigDecimal.ZERO);    
        uncertaintyType = "ABS";
        movePointRightCount = 0;
        uncertaintySigDigits = 0;
        expResult = "12.3456789000000000 ± 0.2438652622252700";
        result = instance.formatValueAndTwoSigmaForPublicationSigDigMode(uncertaintyType, movePointRightCount, uncertaintySigDigits);
        assertEquals(expResult, result);
        //default PCT with specified model
        uncertaintyType = "PCT";
        movePointRightCount = 0;
        uncertaintySigDigits = 0;
        expResult = "12.3456789000000000 ± 1.97530864200";
        result = instance.formatValueAndTwoSigmaForPublicationSigDigMode(uncertaintyType, movePointRightCount, uncertaintySigDigits);
        assertEquals(expResult, result);
        //specified abs with specified model
        uncertaintyType = "ABS";
        movePointRightCount = 5;
        uncertaintySigDigits = 3;
        expResult = "1234568 ± 24400";      
        result = instance.formatValueAndTwoSigmaForPublicationSigDigMode(uncertaintyType, movePointRightCount, uncertaintySigDigits);
        assertEquals(expResult, result); 
        //specified PCT with specified model
        uncertaintyType = "PCT";
        movePointRightCount = 6;
        uncertaintySigDigits = 2;
        expResult = "12345679 ± 2000000";
        result = instance.formatValueAndTwoSigmaForPublicationSigDigMode(uncertaintyType, movePointRightCount, uncertaintySigDigits);
        assertEquals(expResult, result);
    }
  
    /**
     * Test of formatTwoSigmaForPublicationSigDigMode method, of class ValueModel.
     */
    @Test
    public void test_FormatTwoSigmaForPublicationSigDigMode() {
        System.out.println("Testing ValueModel's formatTwoSigmaForPublicationSigDigMode(String uncertaintyType,int movePointRightCount,int uncertaintySigDigits)");
        //default abs without specified model
        String uncertaintyType = "ABS";
        int movePointRightCount = 0;
        int uncertaintySigDigits = 0;
        ValueModel instance = new ValueModel();
        String expResult = "0";
        String result = instance.formatTwoSigmaForPublicationSigDigMode(uncertaintyType, movePointRightCount, uncertaintySigDigits);
        assertEquals(expResult, result);       
        //default pct without specifiedmodel
        uncertaintyType = "PCT";
        movePointRightCount = 0;
        uncertaintySigDigits = 0;
        expResult = "0";
        result = instance.formatTwoSigmaForPublicationSigDigMode(uncertaintyType, movePointRightCount, uncertaintySigDigits);
        assertEquals(expResult, result);
        //default ABS with specified model
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".9876543210"), BigDecimal.ZERO);    
        uncertaintyType = "ABS";
        movePointRightCount = 0;
        uncertaintySigDigits = 0;
        expResult = "0.2438652622252700";
        result = instance.formatTwoSigmaForPublicationSigDigMode(uncertaintyType, movePointRightCount, uncertaintySigDigits);
        assertEquals(expResult, result);
        //default PCT with specified model
        uncertaintyType = "PCT";
        movePointRightCount = 0;
        uncertaintySigDigits = 0;
        expResult = "1.97530864200";
        result = instance.formatTwoSigmaForPublicationSigDigMode(uncertaintyType, movePointRightCount, uncertaintySigDigits);
        assertEquals(expResult, result);
        //specified abs with specified model
        uncertaintyType = "ABS";
        movePointRightCount = 5;
        uncertaintySigDigits = 3;
        expResult = "24400";      
        result = instance.formatTwoSigmaForPublicationSigDigMode(uncertaintyType, movePointRightCount, uncertaintySigDigits);
        assertEquals(expResult, result); 
        //specified PCT with specified model
        uncertaintyType = "PCT";
        movePointRightCount = 6;
        uncertaintySigDigits = 2;
        expResult = "2000000";
        result = instance.formatTwoSigmaForPublicationSigDigMode(uncertaintyType, movePointRightCount, uncertaintySigDigits);
        assertEquals(expResult, result);
    }
    
    /**
     * Test of formatBigDecimalForPublicationSigDigMode method, of class ValueModel.
     */
    @Test
    public void test_FormatBigDecimalForPublicationSigDigMode() {
        System.out.println("Testing ValueModel's formatBigDecimalForPublicationSigDigMode(BigDecimal number, int uncertaintySigDigits)");
        //with 0 number and 0 digits
        BigDecimal number = new BigDecimal("0");
        int uncertaintySigDigits = 0;
        String expResult = "0";
        String result = ValueModel.formatBigDecimalForPublicationSigDigMode(number, uncertaintySigDigits);
        assertEquals(expResult, result);        
        //with negative value and 0 digits
        number = new BigDecimal("-98765.3210");
        uncertaintySigDigits = 0;
        expResult = "-98765.3210";
        result = ValueModel.formatBigDecimalForPublicationSigDigMode(number, uncertaintySigDigits);
        assertEquals(expResult, result);
        //with positive value and 0 digits
        number =new BigDecimal("123.0456789");
        uncertaintySigDigits = 0;
        expResult = "123.0456789";
        result = ValueModel.formatBigDecimalForPublicationSigDigMode(number, uncertaintySigDigits);
        assertEquals(expResult,result);
        //with 0 value and specified digits
        number =new BigDecimal("0");
        uncertaintySigDigits = 5;
        expResult = "0";
        result = ValueModel.formatBigDecimalForPublicationSigDigMode(number, uncertaintySigDigits);
        assertEquals(expResult,result);
        //with negative value and specified digits
        number =new BigDecimal("-98765.3210");
        uncertaintySigDigits = 4;
        expResult = "-98770";
        result = ValueModel.formatBigDecimalForPublicationSigDigMode(number, uncertaintySigDigits);
        assertEquals(expResult,result);
        //with positive value and specified digits
        number =new BigDecimal("123.0456789");
        uncertaintySigDigits = 3;
        expResult = "123";
        result = ValueModel.formatBigDecimalForPublicationSigDigMode(number, uncertaintySigDigits);
        assertEquals(expResult,result);
    }
    
     /**
     * Test of formatBigDecimalForPublicationArbitraryMode method, of class ValueModel.
     */
    @Test
    public void test_FormatBigDecimalForPublicationArbitraryMode() {
        System.out.println("Testing ValueModel's formatBigDecimalForPublicationArbitraryMode(BigDecimal number, int roundingDigits)");
        //0 with no rounding
        BigDecimal number = new BigDecimal("0");
        int roundingDigits = 0;
        String expResult = "0";
        String result = ValueModel.formatBigDecimalForPublicationArbitraryMode(number, roundingDigits);
        assertEquals(expResult, result);
        //0 with rounding
        number = new BigDecimal("0");
        roundingDigits = 3;
        expResult = "0.000";
        result = ValueModel.formatBigDecimalForPublicationArbitraryMode(number, roundingDigits);
        assertEquals(expResult, result);  
        //number without rounding
         number = new BigDecimal("987.654");
        roundingDigits = 0;
        expResult = "988";
        result = ValueModel.formatBigDecimalForPublicationArbitraryMode(number, roundingDigits);
        assertEquals(expResult, result);
        //number with rounding
        number = new BigDecimal("123.567");
        roundingDigits = 2;
        expResult = "123.57";
        result = ValueModel.formatBigDecimalForPublicationArbitraryMode(number, roundingDigits);
        assertEquals(expResult, result);
    }

    /**
     * Test of formatValueFromTwoSigmaForPublicationSigDigMode method, of class ValueModel.
     */
    @Test
    public void test_FormatValueFromTwoSigmaForPublicationSigDigMode() {
        System.out.println("Testing ValueModel's formatValueFromTwoSigmaForPublicationSigDigMode(String uncertaintyType, int movePointRightCount, int uncertaintySigDigits)");
        //blank model pct no movement
        String uncertaintyType = "PCT";
        int movePointRightCount = 0;
        int uncertaintySigDigits = 0;
        ValueModel instance = new ValueModel();
        String expResult = "0";
        String result = instance.formatValueFromTwoSigmaForPublicationSigDigMode(uncertaintyType, movePointRightCount, uncertaintySigDigits);
        assertEquals(expResult, result);
        //blank model pct movement
        uncertaintyType = "PCT";
        movePointRightCount = 5;
        uncertaintySigDigits = 2;
        expResult = "0";
        result = instance.formatValueFromTwoSigmaForPublicationSigDigMode(uncertaintyType, movePointRightCount, uncertaintySigDigits);
        assertEquals(expResult, result);        
        //blank model abs no movement
        uncertaintyType = "ABS";
        movePointRightCount = 0;
        uncertaintySigDigits = 0;
        expResult = "0";
        result = instance.formatValueFromTwoSigmaForPublicationSigDigMode(uncertaintyType, movePointRightCount, uncertaintySigDigits);
        assertEquals(expResult, result);   
        //blank model abs movement
        uncertaintyType = "ABS";
        movePointRightCount = 3;
        uncertaintySigDigits = 5;
        expResult = "0";
        result = instance.formatValueFromTwoSigmaForPublicationSigDigMode(uncertaintyType, movePointRightCount, uncertaintySigDigits);
        assertEquals(expResult, result);   
        //model abs no movement
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".9876543210"), BigDecimal.ZERO);    
        uncertaintyType = "ABS";
        movePointRightCount = 0;
        uncertaintySigDigits = 0;
        expResult = "12.3456789000000000";
        result = instance.formatValueFromTwoSigmaForPublicationSigDigMode(uncertaintyType, movePointRightCount, uncertaintySigDigits);
        assertEquals(expResult, result);   
        //model abs movement
        uncertaintyType = "ABS";
        movePointRightCount = 7;
        uncertaintySigDigits = 1;
        expResult = "123456789";
        result = instance.formatValueFromTwoSigmaForPublicationSigDigMode(uncertaintyType, movePointRightCount, uncertaintySigDigits);
        assertEquals(expResult, result);   
        //model pct no movement
        uncertaintyType = "PCT";
        movePointRightCount = 0;
        uncertaintySigDigits = 0;
        expResult = "12.3456789000000000";
        result = instance.formatValueFromTwoSigmaForPublicationSigDigMode(uncertaintyType, movePointRightCount, uncertaintySigDigits);
        assertEquals(expResult, result);   
        //model pct movement
        uncertaintyType = "PCT";
        movePointRightCount = 8;
        uncertaintySigDigits = 7;
        expResult = "1234567890";
        result = instance.formatValueFromTwoSigmaForPublicationSigDigMode(uncertaintyType, movePointRightCount, uncertaintySigDigits);
        assertEquals(expResult, result);   
    }
    
    /**
     * Test of getXStreamWriter method, of class ValueModel.
     */
    @Test
    public void test_GetXStreamWriter() {
        //This tests if a writer based on this is created with a default ValueModel.
        System.out.println("Testing ValueModel's getXStreamWriter()");
        ValueModel instance = new ValueModel();
        XStream stream0 =new XStream();
        String expResult=stream0.toXML(this);
        XStream stream = instance.getXStreamWriter();
        String result = stream.toXML( this );
        assertEquals(expResult,result);
        //This tests if a writer based on this is created with a specified ValueModel.
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".9876543210"), BigDecimal.ZERO);    
        stream0 =new XStream();
        expResult=stream0.toXML(this);
        stream = instance.getXStreamWriter();
        result = stream.toXML( this );
        assertEquals(expResult,result);
    }
    
    /**
     * Test of getXStreamReader method, of class ValueModel.
     */
    @Test
    public void test_GetXStreamReader() {
        //This tests if a XStreamReader is created and customized correctly.
        System.out.println("Testing ValueModel's getXStreamReader()");
        ValueModel instance = new ValueModel();
        boolean expResult = true;
        boolean result=false;
        String expResult2;
        String result2;
        XStream stream = instance.getXStreamReader();
        if(stream instanceof XStream){
            result=true;        
            expResult2="http://earth-time.org/projects/upb/public_data/XSD/ValueModelXMLSchema.xsd";
            result2=instance.getValueModelXMLSchemaURL();
            assertEquals(expResult2,result2);            
                                     }
        assertEquals(expResult,result);
    }
    
     /**
     * Test of customizeXstream method, of class ValueModel.
     */
    @Test
    public void test_CustomizeXstream() {
       //This tests if the stream is customized.
        System.out.println("Testing ValueModel's customizeXstream(xstream xstream)");
        ValueModel instance = new ValueModel();
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
     * Test of setClassXMLSchemaURL method, of class ValueModel.
     */
    @Test
    public void test_SetClassXMLSchemaURL() {
        //This ensures that the URL returned is as expected.
        System.out.println("Testing ValueModel's setClassXMLSchemaURL()");
        ValueModel instance = new ValueModel();
        instance.setClassXMLSchemaURL();
        String expResult="http://earth-time.org/projects/upb/public_data/XSD/ValueModelXMLSchema.xsd";
        String result=instance.getValueModelXMLSchemaURL();
        assertEquals(expResult,result);
    } 
    
    /**
     * Test of serializeXMLObject method, of class ValueModel.
     */
    @Test
    public void test_SerializeXMLObject() {
        //Test to see if serialization is succesful for default. NOTE THIS PATH MUST BE CHANGED FOR EVERY USER!!
        //This is done in this way, without using the read operation to ensure a problem with read does not affect the serialization test.
        System.out.println("Testing ValueModel's serializeXMLObject(String fileName)");
        String filename= System.getProperty("user.dir");
        filename=filename.concat(File.separator);
        filename=filename.concat("test_SerializeXMLObject");
        
        ValueModel instance = new ValueModel();
        ValueModel myValueModel = null;
        //Write file to disk
        instance.serializeXMLObject(filename);
        //Read back
        try{
            XStream xstream = instance.getXStreamReader();
            boolean isValidOrAirplaneMode = instance.validateXML(filename);
            if (isValidOrAirplaneMode) {
               BufferedReader reader = URIHelper.getBufferedReader(filename);
                try {
                    myValueModel = (ValueModel) xstream.fromXML(reader);
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
         instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".9876543210"), BigDecimal.ZERO);    
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
                    myValueModel = (ValueModel) xstream.fromXML(reader);
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
     * Test of readXMLObject method, of class ValueModel.
     * @throws java.lang.Exception
     */
    @Test
    public void test_ReadXMLObject() throws Exception {
        //This tests if read is working correctly by writing to a file and reading it back, then comparing the read result.
        System.out.println("Testing ValueModel's readXMLObject(String filename, boolean doValidate)");
        String filename= System.getProperty("user.dir");
        filename=filename.concat(File.separator);
        filename=filename.concat("test_ReadXMLObject");
        
        ValueModel instance = new ValueModel();
        //Write file to disk
        instance.serializeXMLObject(filename);
        //Read back
        Object expResult=instance.readXMLObject(filename, true);
        Object result=instance;
        assertEquals(expResult,result);
    
        //Tests if reading works for specified ValueModels
         instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".9876543210"), BigDecimal.ZERO);    
        //Write file to disk
        instance.serializeXMLObject(filename);
        //Read back
        expResult=instance.readXMLObject(filename, true);
        result=instance;
        assertEquals(expResult,result);
    }
    
    /**
     * Test of validateXML method, of class ValueModel.
     */
    @Test
    public void test_ValidateXML() {
        //Test to see if the XML DOM tree is validated
        System.out.println("Testing ValueModel's validateXML(String xmlURI)");
        String xmlURI= System.getProperty("user.dir");
        xmlURI=xmlURI.concat(File.separator);
        xmlURI=xmlURI.concat("test_ValidateXML");
        ValueModel instance = new ValueModel();
        boolean expResult = false;
        boolean result = instance.validateXML(xmlURI);
        try {
            // parse an XML document into a DOM tree
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setNamespaceAware(true);
            DocumentBuilder parser = dbFactory.newDocumentBuilder();
            Document document = parser.parse(xmlURI);
            // create a SchemaFactory capable of understanding WXS schemas
            SchemaFactory schemaFactory =
                    SchemaFactory.newInstance(
                    XMLConstants.W3C_XML_SCHEMA_NS_URI);
            // load a WXS schema, represented by a Schema instance
            Source schemaFile = new StreamSource(
                    new URL(instance.getValueModelXMLSchemaURL()).openStream());
            Schema schema = schemaFactory.newSchema(schemaFile);
            // create a Validator instance, which can be used to validate an instance document
            Validator validator = schema.newValidator();
            // validate the DOM tree
            validator.validate(new DOMSource(document));
           }
            catch (ParserConfigurationException | SAXException | IOException ex) {
            result = ex instanceof UnknownHostException;
                                                                                 }               
        assertEquals(expResult, result);
    }
    
     /**
     * Test of getValueModelXMLSchemaURL method, of class ValueModel.
     */
    @Test
    public void test_GetValueModelXMLSchemaURL() {
        //Test to see if normal null is default, if a specified one also has this.
        System.out.println("Testing ValueModel's getValueModelXMLSchemaURL()");
        ValueModel instance = new ValueModel();
        String expResult = null;
        String result = instance.getValueModelXMLSchemaURL();
        assertEquals(expResult, result);
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".9876543210"), BigDecimal.ZERO);    
        expResult=null;
        result=instance.getValueModelXMLSchemaURL();
        assertEquals(expResult,result);
    }

     /**
     * Test of setValueModelXMLSchemaURL method, of class ValueModel.
     */
    @Test
    public void test_SetValueModelXMLSchemaURL() {
        //Tests if the setter works for both default ValueModels, and if it works if the URL is changed from a specified value.
        System.out.println("Testing ValueModel's setValueModelXMLSchemaURL(String input)");
        String expResult = "Testing_Setter";
        ValueModel instance = new ValueModel();
        instance.setValueModelXMLSchemaURL(expResult);
        String result=instance.getValueModelXMLSchemaURL();
        assertEquals(expResult,result);
        expResult="Testing_Setta";
        instance.setValueModelXMLSchemaURL(expResult);
        result=instance.getValueModelXMLSchemaURL();
        assertEquals(expResult,result);
        //Same test but for specified ValueModel
        expResult = "Testing_Setter";
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".9876543210"), BigDecimal.ZERO);    
        instance.setValueModelXMLSchemaURL(expResult);
        result=instance.getValueModelXMLSchemaURL();
        assertEquals(expResult,result);
        expResult="Testing_Setta";
        instance.setValueModelXMLSchemaURL(expResult);
        result=instance.getValueModelXMLSchemaURL();
        assertEquals(expResult,result);    
    }
    
    /**
     * Test of hasPositiveValue method, of class ValueModel.
     */
    @Test
    public void test_HasPositiveValue() {
        //Default Case
        System.out.println("Testing ValueModel's hasPositiveValue()");
        ValueModel instance = new ValueModel();
        boolean expResult = false;
        boolean result = instance.hasPositiveValue();
        assertEquals(expResult, result);
        //Specified Case true
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".9876543210"), BigDecimal.ZERO);    
        expResult = true;
        result = instance.hasPositiveValue();
        assertEquals(expResult, result);        
        //Specified Case False
        instance=new ValueModel("r207_339",new BigDecimal("-12.34567890"),"PCT",new BigDecimal(".9876543210"), BigDecimal.ZERO);    
        expResult = false;
        result = instance.hasPositiveValue();
        assertEquals(expResult, result);          
    }
    
     /**
     * Test of hasPositiveVarUnct method, of class ValueModel.
     */
    @Test
    public void test_HasPositiveUncertainty() {
        //Default Case
        System.out.println("Testing ValueModel's hasPositiveUncertainty()");
        ValueModel instance = new ValueModel();
        boolean expResult = false;
        boolean result = instance.hasPositiveVarUnct();
        assertEquals(expResult, result);
        //Specified Case true PCT
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".9876543210"), BigDecimal.ZERO);    
        expResult = true;
        result = instance.hasPositiveVarUnct();
        assertEquals(expResult, result);        
        //Specified Case False PCT
        BigDecimal newValue=BigDecimal.ZERO.subtract(new BigDecimal("999999999999999999999999"));
        instance=new ValueModel("r207_339",new BigDecimal("-12.34567890"),"PCT",newValue, BigDecimal.ZERO);    
        instance.oneSigma=newValue;
        expResult = false;
        result = instance.hasPositiveVarUnct();
        assertEquals(expResult, result);       
        //Specified Case true ABS
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"ABS",new BigDecimal(".9876543210"), BigDecimal.ZERO);    
        expResult = true;
        result = instance.hasPositiveVarUnct();
        assertEquals(expResult, result);        
        //Specified Case False ABS
        instance=new ValueModel("r207_339",new BigDecimal("-12.34567890"),"ABS",newValue, BigDecimal.ZERO);    
        instance.oneSigma=newValue;
        expResult = false;
        result = instance.hasPositiveVarUnct();
        assertEquals(expResult, result);               
    }
       
    /**
     * Test of getNonNegativeValue method, of class ValueModel.
     */
    @Test
    public void test_GetNonNegativeValue() {
        //Default should be 0
        System.out.println("Testing ValueModel's getNonNegativeValue()");
        ValueModel instance = new ValueModel();
        BigDecimal expResult = BigDecimal.ZERO;
        BigDecimal result = instance.getNonNegativeValue();
        assertEquals(expResult, result);
        //Specified case to make it return the actual value
        instance=new ValueModel("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".9876543210"), BigDecimal.ZERO);    
        expResult=new BigDecimal("12.34567890");
        result=instance.getNonNegativeValue();
        assertEquals(expResult,result);
        //Specified Case to make it return 0 (aka the value was actually negative)
        instance=new ValueModel("r207_339",new BigDecimal("-12.34567890"),"PCT",new BigDecimal(".9876543210"), BigDecimal.ZERO);    
        expResult=BigDecimal.ZERO;
        result=instance.getNonNegativeValue();
        assertEquals(expResult,result);        
    }

    /**
     * Test of getValueTree method, of class ValueModel.
     */
    @Test
    public void test_GetValueTree() {
        //Testing  (NOTE THIS METHOD USES THE SETTER TO TEST THE GETTER AS OTHERWISE I CANNOT PREDICT THE TREE NAME AND SUCH)
        System.out.println("Testing ValueModel's getValueTree()");
        ValueModel instance = new ValueModel();
        ValueModel instance2=new ValueModel("r207_339",new BigDecimal("-12.34567890"),"PCT",new BigDecimal(".9876543210"), BigDecimal.ZERO);          
        ExpTreeII expResult = instance2.getValueTree();
        instance.setValueTree(expResult);
        ExpTreeII result = instance.getValueTree();
        assertEquals(expResult, result);
    }
    
    /**
     * Test of setValueTree method, of class ValueModel.
     */
    @Test
    public void test_SetValueTree() {
        //Testing  (NOTE THIS METHOD USES THE SETTER TO TEST THE GETTER AS OTHERWISE I CANNOT PREDICT THE TREE NAME AND SUCH)
        System.out.println("Testing ValueModel's setValueTree(ExpTreeII input)");
        ValueModel instance = new ValueModel();
        ValueModel instance2=new ValueModel("r207_339",new BigDecimal("-12.34567890"),"PCT",new BigDecimal(".9876543210"), BigDecimal.ZERO);          
        ExpTreeII expResult = instance2.getValueTree();
        instance.setValueTree(expResult);
        ExpTreeII result = instance.getValueTree();
        assertEquals(expResult, result);
    }    
    
    /**
     * Test of differenceValueCalcs method, of class ValueModel.
     */
    @Test
    public void test_DifferenceValueCalcs() {
        //Default
        System.out.println("Testing ValueModel's differenceValueCalcs()");
        ValueModel instance = new ValueModel();
        String expResult = "ValueTree differs for NONE by: 0";
        String result = instance.differenceValueCalcs();
        assertEquals(expResult, result);
        //Specified
        instance=new ValueModel("r207_339",new BigDecimal("-12.34567890"),"PCT",new BigDecimal(".9876543210"), BigDecimal.ZERO);          
        result=instance.differenceValueCalcs();
        expResult="ValueTree differs for r207_339 by: 0.00E-6";
        assertEquals(expResult, result);
    }    
    
    /**
     * Test of differenceValueCalcsBigDecimal method, of class ValueModel.
     */
    @Test
    public void test_DifferenceValueCalcsBigDecimal() {
        //Default
        System.out.println("Testing ValueModel's differenceValueCalcsBigDecimal()");
        ValueModel instance = new ValueModel();
        BigDecimal expResult = BigDecimal.ZERO;
        BigDecimal result = instance.differenceValueCalcsBigDecimal();
        assertEquals(expResult, result);
        //Specified
        instance=new ValueModel("r207_339",new BigDecimal("-12.34567890"),"PCT",new BigDecimal(".9876543210"), BigDecimal.ZERO);          
        result=instance.differenceValueCalcsBigDecimal();
        expResult=new BigDecimal("0E-8");
        assertEquals(expResult, result);
    }
    
    /**
     * Test of cullNullsFromArray method, of class ValueModel.
     */
    @Test
    public void test_CullNullsFromArray() {
        //If null
        System.out.println("Testing ValueModel's cullNullsFromArray(ValueModel[] valueModels)");
        ValueModel[] vm = null;
        ValueModel[] expResult = new ValueModel[0];
        ValueModel[] result = ValueModel.cullNullsFromArray(vm);
        assertArrayEquals(expResult, result);
        //If array is empty
        vm = new ValueModel[10];
        result = ValueModel.cullNullsFromArray(vm);
        assertArrayEquals(expResult, result);
        //If array has only nulls
        vm = new ValueModel[10];
        for (int i=0;i<vm.length;i++){
            vm[i]=null;
        } 
        result = ValueModel.cullNullsFromArray(vm);
        assertArrayEquals(expResult, result);
        //If array has specified values
        vm = new ValueModel[10];
        ValueModel instance=new ValueModel("r207_339",new BigDecimal("-12.34567890"),"PCT",new BigDecimal(".9876543210"), BigDecimal.ZERO);          
        for (int i=0;i<vm.length;i++){
            vm[i]=instance;
        } 
        expResult = vm;
        result = ValueModel.cullNullsFromArray(vm);
        assertArrayEquals(expResult, result);        
        //If array has specified values and nulls
        vm = new ValueModel[10];
        for (int i=0;i<vm.length;i++){
            vm[i]=null;
        }
        vm[3]=instance;
        vm[7]=instance;
        expResult = new ValueModel[2];
        expResult[0]=instance;
        expResult[1]=instance;
        result = ValueModel.cullNullsFromArray(vm);
        assertArrayEquals(expResult, result);        
    }
    
    /**
     * Test of compressArrayOfValueModels method, of class ValueModel.
     */
    @Test
    public void test_CompressArrayOfValueModels() {
        System.out.println("Testing ValueModel's compressArrayOfValueModels(ValueModel[] myArray)");
        //Null array
        ValueModel[] myArray = null;
        ValueModel[] expResult = new ValueModel[0];
        ValueModel[] result = ValueModel.compressArrayOfValueModels(myArray);
        assertArrayEquals(expResult, result);
        //Empty array
        myArray = new ValueModel[0];
        result = ValueModel.compressArrayOfValueModels(myArray);
        assertArrayEquals(expResult, result);        
        //Specified only with 0 values
        myArray = new ValueModel[10];
        ValueModel instance=new ValueModel("r207_339",new BigDecimal("0"),"PCT",new BigDecimal(".9876543210"), BigDecimal.ZERO);                  
        for (int i=0;i<myArray.length;i++){
            myArray[i]=instance;
                                            } 
        result = ValueModel.compressArrayOfValueModels(myArray);
        assertArrayEquals(expResult, result);   
        //Specified only without 0 values
        instance=new ValueModel("r207_339",new BigDecimal("-12.34567890"),"PCT",new BigDecimal(".9876543210"), BigDecimal.ZERO);                  
        for (int i=0;i<myArray.length;i++){
            myArray[i]=instance;
                                            } 
        expResult =myArray;
        result = ValueModel.compressArrayOfValueModels(myArray);
        assertArrayEquals(expResult, result);          
        //Specified with and without 0 values
        instance=new ValueModel("r207_339",new BigDecimal("0"),"PCT",new BigDecimal(".9876543210"), BigDecimal.ZERO);                  
        for (int i=0;i<myArray.length;i++){
            myArray[i]=instance;
                                             } 
        instance=new ValueModel("r207_339",new BigDecimal("-12.34567890"),"PCT",new BigDecimal(".9876543210"), BigDecimal.ZERO);                  
        myArray[5]=instance;
        myArray[9]=instance;
        expResult =new ValueModel[2];
        expResult[0]=instance;
        expResult[1]=instance;
        result = ValueModel.compressArrayOfValueModels(myArray);
        assertArrayEquals(expResult, result);     
    }
    

    ////////////////////////
    ////Unfinished Tests////
    //////////////////////// 
    
    /*
     *ValueModel.removeSelf()
     *  Has no body, simply a comment that says it is unsupported. There is
     *      nothing to test.
     *
     *ValueModel.calculateValue(ValueModel[] inputValueModels,ConcurrentMap<String, BigDecimal> parDerivTerms )
     *  Has no body, nor any comments mentioning what happened. There is nothing
     *      to test.
     *
     *
     */
    
    


    
}