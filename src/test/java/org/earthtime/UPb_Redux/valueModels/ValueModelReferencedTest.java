/*
 * ValueModelReferenced_Test_04092014Test.java
 *
 * The naming of this file is the name of the .java file being tested, combined
 * with test since this is the test file, combined with the creation date of
 * this file. The test at the end is so that the file runs from NetBeans when
 * testing an entire project.
 *
 * Created on April 9, 2014.
 *
 *
 *Version History:
 *April 9 2014: File Created.
 *April 14 2014: Constructor tests finished. Method tests started. Finish date
 *               unknown.
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
import org.earthtime.archivingTools.URIHelper;
import org.earthtime.exceptions.ETException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author patrickbrewer
 */
public class ValueModelReferencedTest {
    
    
    
    /////////////////////////
    ////Constructor Tests////
    /////////////////////////    


    
     /**
     * Test of ValueModel() method, of class ValueModelReferenced.
     */
    @Test
    public void test_constructor_0(){
	System.out.println("Testing ValueModelReferenced's ValueModelReferenced()");
        //Tests if values are correct
        ValueModelReferenced instance=new ValueModelReferenced();
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
        expResult="NONE";
        result=instance.getReference();
        assertEquals(expResult,result);
        }
    
     /**
     * Test of ValueModel(name,value,uncertaintyType,oneSigma,reference) method, of class ValueModelReferenced.
     */
    @Test
    public void test_constructor_1(){
	System.out.println("Testing ValueModelReferenced's ValueModelReferenced(String name,BigDecimal value,String uncertaintyType,BigDecimal oneSigma,String reference)");
        //Tests if values are correct
        String name="Hello";
        BigDecimal value=new BigDecimal("6.69");
        String type="ABS";
        BigDecimal sig=new BigDecimal("13.37");
        String reference="CIRDLES";
        ValueModelReferenced instance=new ValueModelReferenced(name,value,type,sig, BigDecimal.ZERO,reference);
        String expResult="Hello";
        String result=instance.getName();
        assertEquals(expResult,result);
        expResult="ABS";
        result=instance.getUncertaintyType();
        assertEquals(expResult,result);
        BigDecimal expectedResult=new BigDecimal("6.69");
        BigDecimal actualResult=instance.getValue();
        assertEquals(expectedResult,actualResult);
        expectedResult=new BigDecimal("13.37");        
        actualResult=instance.getOneSigma();
        assertEquals(expectedResult,actualResult);
        expResult="CIRDLES";
        result=instance.getReference();
        assertEquals(expResult,result);
        }    
    
    
    ////////////////////
    ////Method Tests////
    ////////////////////     
        
    
    
    /**
     * Test of copy method, of class ValueModelReferenced.
    */ 
    @Test
    public void test_Copy() {
        //This tests the method with default values.
        System.out.println("Testing ValueModelReferenced's copy()");
        ValueModelReferenced instance = new ValueModelReferenced();
        ValueModelReferenced expectedResult = instance;
        ValueModelReferenced result = instance.copy();
        assertEquals(expectedResult, result);

        //This tests the method with specified values.
        instance=new ValueModelReferenced("hello",new BigDecimal("3.87695"),"ABS",new BigDecimal("1.25"), BigDecimal.ZERO,"CIRDLES");
        expectedResult = instance;
        result = instance.copy();
        assertEquals(expectedResult, result);
            }

     /**
     * Test of method "copyValuesFrom", in the file ValueModelReferenced.java.
     */
    @Test
    public void test_CopyValuesFrom() {
        //This test uses specified values.
        System.out.println("Testing ValueModelReferenced's copyValuesFrom(ValueModelReferenced parent)");
        ValueModelReferenced instance0 = new ValueModelReferenced("Specific",new BigDecimal("12.34567890"),"ABS",new BigDecimal("0.987654321"), BigDecimal.ZERO,"CIRDLES");
        ValueModelReferenced blank = new ValueModelReferenced();

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
        assertEquals(instance0.getReference(),blank.getReference());
        
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
        instance0 = new ValueModelReferenced();
        blank = new ValueModelReferenced();
        blank.copyValuesFrom(instance0);
        assertEquals(instance0.value,blank.value);
        assertEquals(instance0.oneSigma,blank.oneSigma);
        assertEquals(instance0.uncertaintyType,blank.uncertaintyType);
        assertEquals(instance0.name,blank.name);
        assertEquals(instance0.getReference(),blank.getReference());

        //This test uses default values, save for the name field.
        instance0 = new ValueModelReferenced("Specific",new BigDecimal("213"),"ABS",new BigDecimal("0.4324"), BigDecimal.ZERO,"C.I");
        blank = new ValueModelReferenced("Specific",new BigDecimal("12.4332"),"ABS",new BigDecimal("0.654654"), BigDecimal.ZERO,"R.D");
       
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
        assertEquals(instance0.getReference(),blank.getReference());

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
     * Test of getReference method, of class ValueModelReferenced.
     */
    @Test
    public void test_GetReference() {
        System.out.println("Testing ValueModelReferenced's getReference()");
        ValueModelReferenced instance = new ValueModelReferenced();
        String expResult = "NONE";
        String result = instance.getReference();
        assertEquals(expResult, result);
        instance=new ValueModelReferenced("hello",new BigDecimal("3.87695"),"ABS",new BigDecimal("1.25"), BigDecimal.ZERO,"CIRDLES");
        expResult="CIRDLES";
        result = instance.getReference();
        assertEquals(expResult, result);        
    }

    /**
     * Test of setReference method, of class ValueModelReferenced.
    */
    @Test
    public void test_SetReference() {
        System.out.println("Testing ValueModelReferenced's setReference(String reference)");
        String reference = "hello there";
        ValueModelReferenced instance = new ValueModelReferenced();
        assertEquals("NONE",instance.getReference());
        instance.setReference(reference);
        assertEquals(reference,instance.getReference());
    }

    /**
     * Test of customizeXstream method, of class ValueModelReferenced.
     */
    @Test
    public void test_CustomizeXstream() {
       //This tests if the stream is customized.
        System.out.println("Testing ValueModelReferenced's customizeXstream(xstream xstream)");
        ValueModelReferenced instance = new ValueModelReferenced();
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
     * Test of serializeXMLObject method, of class ValueModelReferenced.
     */
    @Test
    public void test_SerializeXMLObject() throws Exception {
        try {
            //Test to see if serialization is succesful for default. NOTE THIS PATH MUST BE CHANGED FOR EVERY USER!!
            //This is done in this way, without using the read operation to ensure a problem with read does not affect the serialization test.
            System.out.println("Testing ValueModelReferenced's serializeXMLObject(String filename)");
            String filename= System.getProperty("user.dir");
            filename=filename.concat(File.separator);
            filename=filename.concat("test_SerializeXMLObject_Reference");

            ValueModelReferenced instance = new ValueModelReferenced();
            ValueModelReferenced myValueModel = null;
            //Write file to disk
            instance.serializeXMLObject(filename);
            //Read back
            try{
                XStream xstream = instance.getXStreamReader();
                boolean isValidOrAirplaneMode = instance.validateXML(filename);
                if (isValidOrAirplaneMode) {
                   BufferedReader reader = URIHelper.getBufferedReader(filename);
                    try {
                        myValueModel = (ValueModelReferenced) xstream.fromXML(reader);
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
             instance=new ValueModelReferenced("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".9876543210"), BigDecimal.ZERO,"CIRDLES");    
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
                        myValueModel = (ValueModelReferenced) xstream.fromXML(reader);
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
        } finally {
            cleanFiles("test_SerializeXMLObject_Reference");
        }
    }
    
    /**
     * Test of readXMLObject method, of class ValueModelReferenced.
     * @throws java.lang.Exception
     */
    @Test
    public void test_ReadXMLObject() throws Exception {
        try {
            //This tests if read is working correctly by writing to a file and reading it back, then comparing the read result.
            System.out.println("Testing ValueModelReferenced's readXMLObject(String filename, boolean doValidate)");
            String filename= System.getProperty("user.dir");
            filename=filename.concat(File.separator);
            filename=filename.concat("test_ReadXMLObject_Referenced");

            ValueModelReferenced instance = new ValueModelReferenced();
            //Write file to disk
            instance.serializeXMLObject(filename);
            //Read back
            Object expResult=instance.readXMLObject(filename, true);
            Object result=instance;
            assertEquals(expResult,result);

            //Tests if reading works for specified ValueModels
             instance=new ValueModelReferenced("r207_339",new BigDecimal("12.34567890"),"PCT",new BigDecimal(".9876543210"), BigDecimal.ZERO,"CIRDLES");    
            //Write file to disk
            instance.serializeXMLObject(filename);
            //Read back
            expResult=instance.readXMLObject(filename, true);
            result=instance;
            assertEquals(expResult,result);
        } finally {
            cleanFiles("test_ReadXMLObject_Referenced");
        }
    }
    
     /**
     * Delete the files created previously
     * @throws Exception 
     */   
    private void cleanFiles(String name) throws Exception {
        
        File file = new File(name); //Get the file
        if(file.exists())
        {            
            if(!(file.delete())) //delete
            {
                //throw exception in case of error
                throw new Exception("Testing File '" + name + "' couldn't be deleted");
            }
        }
    }   
}
