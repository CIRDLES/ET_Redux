/*
 * TracerUPbTypesEnum_Test_05052014Test.java
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
public class TracerUPbTypesEnumTest {
    

    
    ////////////////////////////////////////////
    ////Constructor Tests///&///Method Tests////
    ////////////////////////////////////////////      
    
     /**
     * Test of TracerUPbTypesEnum() method, of class TracerUPbTypesEnum.
     */
    @Test
    public void test_constructor_0(){
	System.out.println("Testing TracerUPbTypesEnum's enumerations and getters.");
        //Tests if values are correct for all enumerations, and tests the getters as well. You cannot instantiate new inumerations.
        
        TracerUPbTypesEnum ave=TracerUPbTypesEnum.NONE;
        assertEquals("NONE",ave.getName());

        ave=TracerUPbTypesEnum.mixed_202_205_233_235;
        assertEquals("mixed 202-205-233-235",ave.getName());
        
        ave=TracerUPbTypesEnum.mixed_202_205_233_236;
        assertEquals("mixed 202-205-233-236",ave.getName());        
        
        ave=TracerUPbTypesEnum.mixed_205_233_235;
        assertEquals("mixed 205-233-235",ave.getName());        
        
        ave=TracerUPbTypesEnum.mixed_205_233_235_230Th;
        assertEquals("mixed 205-233-235-230Th",ave.getName());        
        
        ave=TracerUPbTypesEnum.mixed_205_233_236;
        assertEquals("mixed 205-233-236",ave.getName());        
        
        ave=TracerUPbTypesEnum.mixed_205_235;
        assertEquals("mixed 205-235",ave.getName());        
        
        ave=TracerUPbTypesEnum.mixed_208_235;
        assertEquals("mixed 208-235",ave.getName());        
        
        
        String[] list=TracerUPbTypesEnum.getNames();

        assertEquals("NONE",list[0]);
        assertEquals("mixed 205-235",list[1]);
        assertEquals("mixed 205-233-235",list[2]);
        assertEquals("mixed 208-235",list[3]);
        assertEquals("mixed 205-233-236",list[4]);
        assertEquals("mixed 202-205-233-235",list[5]);
        assertEquals("mixed 202-205-233-236",list[6]);
        assertEquals("mixed 205-233-235-230Th",list[7]);

        

        
    }    
        
    
    
    
    
    //////////////////
    //Method Tests////
    //////////////////      
    
    
    
    /**
     * Test of contains method, of class TracerUPbTypesEnum.
    */ 
    @Test
    public void test_Contains() {
        System.out.println("Testing TracerUPbTypesEnum's contains(String checkString)");
        String checkString = "";
        boolean expResult = false;
        boolean result = TracerUPbTypesEnum.contains(checkString);
        assertEquals(expResult, result);

        checkString="fractionMass";
        result = TracerUPbTypesEnum.contains(checkString);
        assertEquals(expResult, result);
        
        checkString="NONE";
        expResult=true;
        result = TracerUPbTypesEnum.contains(checkString);
        assertEquals(expResult, result);        
    }    
        

    
    /**
     * Test of isSupported method, of class TracerUPbTypesEnum.
     */
    @Test
    public void test_IsSupported() {
        System.out.println("Testing TracerUPbTypesEnum's isSupported()");
        TracerUPbTypesEnum instance = TracerUPbTypesEnum.NONE;
        boolean expResult = false;
        boolean result = instance.isSupported();
        assertEquals(expResult, result);

        instance = TracerUPbTypesEnum.mixed_205_233_235_230Th;
        result = instance.isSupported();
        assertEquals(expResult, result);       
        
        instance = TracerUPbTypesEnum.mixed_208_235;
        result = instance.isSupported();
        assertEquals(expResult, result);       
        
        expResult=true;
        
        instance = TracerUPbTypesEnum.mixed_202_205_233_235;
        result = instance.isSupported();
        assertEquals(expResult, result);          
        
        instance = TracerUPbTypesEnum.mixed_202_205_233_236;
        result = instance.isSupported();
        assertEquals(expResult, result);    
    
        instance = TracerUPbTypesEnum.mixed_205_233_235;
        result = instance.isSupported();
        assertEquals(expResult, result);     
        
        instance = TracerUPbTypesEnum.mixed_205_233_236;
        result = instance.isSupported();
        assertEquals(expResult, result);          
        
        instance = TracerUPbTypesEnum.mixed_205_235;
        result = instance.isSupported();
        assertEquals(expResult, result);               

    }
     
    
    
    /**
     * Test of excludesRatioOrConcentration method, of class TracerUPbTypesEnum.
     */
    @Test
    public void test_ExcludesRatioOrConcentration() {
        System.out.println("Testing TracerUPbTypesEnum's excludesRatioOrConcentration(TracerUPbRatiosAndConcentrations ratio)");
        TracerUPbRatiosAndConcentrations ratio = TracerUPbRatiosAndConcentrations.concPb205t;
        TracerUPbTypesEnum instance = TracerUPbTypesEnum.NONE;
        boolean expResult = false;
        boolean result = instance.excludesRatioOrConcentration(ratio);
        assertEquals(expResult, result);

        
        
        
    }

    
    /**
     * 
     * Test of getSupportedTypes method, of class TracerUPbTypesEnum.
     */
    @Test
    public void test_GetSupportedTypes() {
        System.out.println("Testing TracerUPbTypesEnum's getSupportedTypes()");
        TracerUPbTypesEnum[] result = TracerUPbTypesEnum.getSupportedTypes();

        
        assertEquals("mixed 205-235",result[0].getName());
        assertEquals("mixed 205-233-235",result[1].getName());
        assertEquals("mixed 205-233-236",result[2].getName());
        assertEquals("mixed 202-205-233-235",result[3].getName());
        assertEquals("mixed 202-205-233-236",result[4].getName());

    }


    
    
    
    

    /**
     * Test of valueFromName method, of class TracerUPbTypesEnum.
      */
    @Test
    public void test_ValueFromName() {
        System.out.println("Testing TracerUPbTypesEnum's valueFromName(String name)");
        String name = "NONE";
        TracerUPbTypesEnum expResult = TracerUPbTypesEnum.NONE;
        TracerUPbTypesEnum result = TracerUPbTypesEnum.valueFromName(name);
        assertEquals(expResult, result);

        
    }
   
    
    /**
     * Test of toString method, of class TracerUPbTypesEnum.
     */
    @Test
    public void test_ToString() {
        System.out.println("Testing TracerUPbTypesEnum's toString()");
        TracerUPbTypesEnum instance = TracerUPbTypesEnum.NONE;
        String expResult = "NONE";
        String result = instance.toString();
        assertEquals(expResult, result);

        
        
    }
        
    
    
    
    
    
}
