/*
 * SampleDateModel_Test_05032014Test.java
 *
 * The naming of this file is the name of the .java file being tested, combined
 * with test since this is the test file, combined with the creation date of
 * this file. The test at the end is so that the file runs from NetBeans when
 * testing an entire project.
 *
 * Created on May 3, 2014.
 *
 *
 *Version History:
 *May 3 2014: File Created. Constructor tests completed. Method tests partially
 *            completed. Unfinished tests exist at the bottom. Some may exist
 *            elsewhere, as this list may not be all encompassing.
 *
 *Developed for the CIRDLES lab at the College of Charleston by Patrick Brewer.
 *
 *
 */

package org.earthtime.UPb_Redux.valueModels;

import java.math.BigDecimal;
import java.util.Vector;
import org.earthtime.UPb_Redux.aliquots.Aliquot;
import org.earthtime.UPb_Redux.aliquots.UPbReduxAliquot;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.YorkLineFit;
import org.earthtime.UPb_Redux.fractions.Fraction;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFraction;
import org.earthtime.UPb_Redux.samples.Sample;
import org.earthtime.dataDictionaries.SampleAnalysisTypesEnum;
import org.earthtime.samples.SampleInterface;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author patrickbrewer
 */
public class SampleDateModelTest {
    
    
    
    /////////////////////////
    ////Constructor Tests////
    /////////////////////////     
    
    

    
    
    
     /**
     * Test of SampleDateModel() method, of class SampleDateModel.
     */
    @Test
    public void test_constructor_0(){
	System.out.println("Testing SampleDateModel's SampleDateModel()");
        //Tests if values are correct
        SampleDateModel hi=new SampleDateModel();
        assertEquals("NONE",hi.name);
        assertEquals("",hi.getMethodName());
        assertEquals("",hi.getDateName());
        assertEquals(new BigDecimal("0"),hi.value);
        assertEquals("NONE",hi.uncertaintyType);
        assertEquals(new BigDecimal("0"),hi.oneSigma);
        
        }          
    
    
     /**
     * Test of SampleDateModel() method, of class SampleDateModel.
     */
    @Test
    public void test_constructor_1(){
	System.out.println("Testing SampleDateModel's SampleDateModel(String name,String methodName,String dateName,BigDecimal value,String uncertaintyType,BigDecimal oneSigma)");
        //Tests if values are correct
        SampleDateModel hi=new SampleDateModel("hello","there","silly",new BigDecimal("430"),"ABS",new BigDecimal("51"));
        assertEquals("hello",hi.name);
        assertEquals("there",hi.getMethodName());
        assertEquals("silly",hi.getDateName());
        assertEquals(new BigDecimal("430"),hi.value);
        assertEquals("ABS",hi.uncertaintyType);
        assertEquals(new BigDecimal("51"),hi.oneSigma);        
        }                
        
    
    
    
    ////////////////////
    ////Method Tests////
    ////////////////////         
    
    
    
    
    /**
     * Test of copy method, of class SampleDateModel.
    */ 
    @Test
    public void test_Copy() {
        System.out.println("Testing SampleDateModel's copy()");
        SampleDateModel instance=new SampleDateModel("hello","there","silly",new BigDecimal("430"),"ABS",new BigDecimal("51"));
        
        instance.setMeanSquaredWeightedDeviation(new BigDecimal("51238"));
        instance.setInternalTwoSigmaUnct(new BigDecimal("51238"));
        instance.setInternalTwoSigmaUnctWithTracerCalibrationUnct(new BigDecimal("51238"));
        instance.setInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct(new BigDecimal("51238"));
        instance.setExplanation("ave!");
        instance.setComment("ave!");
        instance.setPreferred(true);
        
        SampleDateModel result = instance.copy();
        
        assertEquals("hello",result.name);
        assertEquals("there",result.getMethodName());
        assertEquals("silly",result.getDateName());
        assertEquals(new BigDecimal("430"),result.value);
        assertEquals("ABS",result.uncertaintyType);
        assertEquals(new BigDecimal("51"),result.oneSigma); 
        assertEquals(new BigDecimal("51238"),result.getMeanSquaredWeightedDeviation()); 
        assertEquals(new BigDecimal("51238"),result.getInternalTwoSigmaUnct()); 
        assertEquals(new BigDecimal("51238"),result.getInternalTwoSigmaUnctWithTracerCalibrationUnct()); 
        assertEquals(new BigDecimal("51238"),result.getInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct()); 
        assertEquals("ave!",result.getExplanation());
        assertEquals("ave!",result.getComment());
        assertEquals(true,result.isPreferred());        
        
    }

        /**
     * Test of compareTo method, of class SampleDateModel.
     */
    @Test
    public void test_CompareTo() {
        System.out.println("Testing SampleDateModel's compareTo(SampleDateModel comparedTo)");
        SampleDateModel instance0=new SampleDateModel("hello","there","silly",new BigDecimal("430"),"ABS",new BigDecimal("51"));
        SampleDateModel instance = new SampleDateModel();
        SampleDateModel instance2=new SampleDateModel("hello","there","silly",new BigDecimal("430"),"ABS",new BigDecimal("51"));
        
        int result = instance.compareTo(instance0);
        assertEquals(6,result);

        result = instance2.compareTo(instance0);
        assertEquals(0,result);
        
        result = instance2.compareTo(instance);
        assertEquals(-6,result);
        
    }

        /**
     * Test of equals method, of class SampleDateModel.
     */ 
    @Test
    public void test_Equals() {
        System.out.println("Testing SampleDateModel's equals(SampleDateModel equals)");
        SampleDateModel instance0=new SampleDateModel("hello","there","silly",new BigDecimal("430"),"ABS",new BigDecimal("51"));
        SampleDateModel instance = new SampleDateModel();
        SampleDateModel instance2=new SampleDateModel("hello","there","silly",new BigDecimal("430"),"ABS",new BigDecimal("51"));

        
        boolean result = instance.equals(instance0);
        assertEquals(false, result);

        result=instance0.equals(instance2);
        assertEquals(true,result);
    }

        /**
     * Test of hashCode method, of class SampleDateModel.
     */
    @Test
    public void test_HashCode() {
        System.out.println("Testing SampleDateModel's hashCode()");
        SampleDateModel instance = new SampleDateModel();
        int result = instance.hashCode();
        assertEquals(0, result);

        
    }

        /**
     * Test of toString method, of class SampleDateModel.
     */
    @Test
    public void test_ToString() {
        System.out.println("Testing SampleDateModel's toString()");
        SampleDateModel instance = new SampleDateModel();
        SampleDateModel instance2=new SampleDateModel("hello","there","silly",new BigDecimal("430"),"ABS",new BigDecimal("51"));
        
        String expResult = "NONE";
        String result = instance.toString();
        
        
        assertEquals(expResult, result);
        result = instance2.toString();
        
        expResult="hello";
        assertEquals(expResult, result);
    }

        /**
     * Test of getMeanSquaredWeightedDeviation method, of class SampleDateModel.
     */
    @Test
    public void test_GetMeanSquaredWeightedDeviation() {
        System.out.println("Testing SampleDateModel's getMeanSquaredWeightedDeviation()");
        SampleDateModel instance = new SampleDateModel();
        BigDecimal expResult = BigDecimal.ZERO;
        BigDecimal result = instance.getMeanSquaredWeightedDeviation();
        assertEquals(expResult, result);

        expResult=new BigDecimal("51");
        instance.setMeanSquaredWeightedDeviation(expResult);
        result=instance.getMeanSquaredWeightedDeviation();
        assertEquals(expResult, result);
    }

        /**
     * Test of setMeanSquaredWeightedDeviation method, of class SampleDateModel.
     */
    @Test
    public void test_SetMeanSquaredWeightedDeviation() {
        System.out.println("Testing SampleDateModel's setMeanSquaredWeightedDeviation(BigDecimal meanSquaredWeightedDeviation)");
        SampleDateModel instance = new SampleDateModel();
        BigDecimal expResult = BigDecimal.ZERO;
        BigDecimal result = instance.getMeanSquaredWeightedDeviation();
        assertEquals(expResult, result);

        expResult=new BigDecimal("51");
        instance.setMeanSquaredWeightedDeviation(expResult);
        result=instance.getMeanSquaredWeightedDeviation();
        assertEquals(expResult, result);
    }
    
        /**
     * Test of getInternalTwoSigmaUnctWithTracerCalibrationUnct method, of class SampleDateModel.
     */
    @Test
    public void test_GetInternalTwoSigmaUnctWithTracerCalibrationUnct() {
        System.out.println("Testing SampleDateModel's getInternalTwoSigmaUnctWithTracerCalibrationUnct()");
        SampleDateModel instance = new SampleDateModel();
        BigDecimal expResult = BigDecimal.ZERO;
        BigDecimal result = instance.getInternalTwoSigmaUnctWithTracerCalibrationUnct();
        assertEquals(expResult, result);

        expResult=new BigDecimal("51");
        instance.setInternalTwoSigmaUnctWithTracerCalibrationUnct(expResult);
        result=instance.getInternalTwoSigmaUnctWithTracerCalibrationUnct();
        assertEquals(expResult, result);
    }
    
        /**
     * Test of setInternalTwoSigmaUnctWithTracerCalibrationUnct method, of class SampleDateModel.
     */
    @Test
    public void test_SetInternalTwoSigmaUnctWithTracerCalibrationUnct() {
        System.out.println("Testing SampleDateModel's setInternalTwoSigmaUnctWithTracerCalibrationUnct(BigDecimal internalTwoSigmaUnctWithTracerCalibrationUnct)");
        SampleDateModel instance = new SampleDateModel();
        BigDecimal expResult = BigDecimal.ZERO;
        BigDecimal result = instance.getInternalTwoSigmaUnctWithTracerCalibrationUnct();
        assertEquals(expResult, result);

        expResult=new BigDecimal("51");
        instance.setInternalTwoSigmaUnctWithTracerCalibrationUnct(expResult);
        result=instance.getInternalTwoSigmaUnctWithTracerCalibrationUnct();
        assertEquals(expResult, result);  
    }
    
        /**
     * Test of getInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct method, of class SampleDateModel.
    */
    @Test
    public void test_GetInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct() {
        System.out.println("Testing SampleDateModel's getInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct()");
        SampleDateModel instance = new SampleDateModel();
        BigDecimal expResult = BigDecimal.ZERO;
        BigDecimal result = instance.getInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct();
        assertEquals(expResult, result);

        expResult=new BigDecimal("51");
        instance.setInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct(expResult);
        result=instance.getInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct();
        assertEquals(expResult, result);  
        
    }
    
        /**
     * Test of setInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct method, of class SampleDateModel.
     */
    @Test
    public void test_SetInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct() {
        System.out.println("Testing SampleDateModel's setInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct(BigDecimal internalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct)");
        SampleDateModel instance = new SampleDateModel();
        BigDecimal expResult = BigDecimal.ZERO;
        BigDecimal result = instance.getInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct();
        assertEquals(expResult, result);

        expResult=new BigDecimal("51");
        instance.setInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct(expResult);
        result=instance.getInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct();
        assertEquals(expResult, result); 
        
    }

        /**
     * Test of getExplanation method, of class SampleDateModel.
     */
    @Test
    public void test_GetExplanation() {
        System.out.println("Testing SampleDateModel's getExplanation()");
        SampleDateModel instance = new SampleDateModel();
        String expResult = "Explanation";
        String result = instance.getExplanation();
        assertEquals(expResult, result);

        expResult="sieg";
        instance.setExplanation(expResult);
        result=instance.getExplanation();
        assertEquals(expResult, result);   
    
    }
    
    /**
     * Test of setExplanation method, of class SampleDateModel.
     */
    @Test
    public void test_SetExplanation() {
        System.out.println("Testing SampleDateModel's setExplanation(String explanation)");
        SampleDateModel instance = new SampleDateModel();
        String expResult = "Explanation";
        String result = instance.getExplanation();
        assertEquals(expResult, result);

        expResult="sieg";
        instance.setExplanation(expResult);
        result=instance.getExplanation();
        assertEquals(expResult, result);   
       
    }
    
    /**
     * Test of getComment method, of class SampleDateModel.
     */
    @Test
    public void test_GetComment() {
        System.out.println("Testing SampleDateModel's getComment()");
        SampleDateModel instance = new SampleDateModel();
        String expResult = "Comment";
        String result = instance.getComment();
        assertEquals(expResult, result);

        expResult="sieg";
        instance.setComment(expResult);
        result=instance.getComment();
        assertEquals(expResult, result);   
       
    }
    
    /**
     * Test of setComment method, of class SampleDateModel.
     */
    @Test
    public void test_SetComment() {
        System.out.println("Testing SampleDateModel's setComment(String Comment)");
        SampleDateModel instance = new SampleDateModel();
        String expResult = "Comment";
        String result = instance.getComment();
        assertEquals(expResult, result);

        expResult="sieg";
        instance.setComment(expResult);
        result=instance.getComment();
        assertEquals(expResult, result);   
       
    }
    
        /**
     * Test of isPreferred method, of class SampleDateModel.
     */
    @Test
    public void test_IsPreferred() {
        System.out.println("Testing SampleDateModel's isPreferred()");
        SampleDateModel instance = new SampleDateModel();
        boolean expResult = false;
        boolean result = instance.isPreferred();
        assertEquals(expResult, result);

        expResult=true;
        instance.setPreferred(expResult);
        result=instance.isPreferred();
        assertEquals(expResult, result);
    }
    
    /**
     * Test of setPreferred method, of class SampleDateModel.
     */
    @Test
    public void test_SetPreferred() {
        System.out.println("Testing SampleDateModel's setPreferred(boolean pref)");
        SampleDateModel instance = new SampleDateModel();
        boolean expResult = false;
        boolean result = instance.isPreferred();
        assertEquals(expResult, result);

        expResult=true;
        instance.setPreferred(expResult);
        result=instance.isPreferred();
        assertEquals(expResult, result); 
    }
    
    /**
     * Test of getInternalTwoSigmaUnct method, of class SampleDateModel.
     */
    @Test
    public void test_GetInternalTwoSigmaUnct() {
        System.out.println("Testing SampleDateModel's getInternalTwoSigmaUnct()");
        SampleDateModel instance = new SampleDateModel();
        BigDecimal expResult = BigDecimal.ZERO;
        BigDecimal result = instance.getInternalTwoSigmaUnct();
        assertEquals(expResult, result);

        expResult=new BigDecimal("51");
        instance.setInternalTwoSigmaUnct(expResult);
        result=instance.getInternalTwoSigmaUnct();
        assertEquals(expResult, result); 
        
    }
    
    /**
     * Test of setInternalTwoSigmaUnct method, of class SampleDateModel.
    */
    @Test
    public void test_SetInternalTwoSigmaUnct() {
        System.out.println("Testing SampleDateModel's setInternalTwoSigmaUnct(BigDecimal internalTwoSigmaUnct)");
        SampleDateModel instance = new SampleDateModel();
        BigDecimal expResult = BigDecimal.ZERO;
        BigDecimal result = instance.getInternalTwoSigmaUnct();
        assertEquals(expResult, result);

        expResult=new BigDecimal("51");
        instance.setInternalTwoSigmaUnct(expResult);
        result=instance.getInternalTwoSigmaUnct();
        assertEquals(expResult, result); 
        
    }
    
    /**
     * Test of getIncludedFractionIDsVector method, of class SampleDateModel.
     */
    @Test
    public void test_GetIncludedFractionIDsVector() {
        System.out.println("Testing SampleDateModel's getIncludedFractionIDsVector()");
        SampleDateModel instance = new SampleDateModel();
        Vector<String> bob=new Vector<>(51);
        bob.add("hello");
        instance.setIncludedFractionIDsVector(bob);
        Vector<String> result = instance.getIncludedFractionIDsVector();
        assertEquals("hello", result.get(0));

    }
    
    /**
     * Test of setIncludedFractionIDsVector method, of class SampleDateModel.
     */
    @Test
    public void test_SetIncludedFractionIDsVector() {
        System.out.println("Testing SampleDateModel's setIncludedFractionIDsVector(Vector<String> includedFractionIDsVector)");
        SampleDateModel instance = new SampleDateModel();
        Vector<String> bob=new Vector<>(51);
        bob.add("hello");
        instance.setIncludedFractionIDsVector(bob);
        Vector<String> result = instance.getIncludedFractionIDsVector();
        assertEquals("hello", result.get(0));
   
    }

        /**
     * Test of includesFractionByName method, of class SampleDateModel.
     */
    @Test
    public void test_IncludesFractionByName() {
        System.out.println("Testing SampleDateModel's includesFractionByName(String fractionID)");
        String fractionID = "";
        SampleDateModel instance = new SampleDateModel();
        boolean expResult = false;
        boolean result = instance.includesFractionByName(fractionID);
        assertEquals(expResult, result);
        
        expResult=true;
        Vector<String> bob=new Vector<>(51);
        bob.add("hello");
        instance.setIncludedFractionIDsVector(bob);
        fractionID="hello";
        result = instance.includesFractionByName(fractionID);
        assertEquals(expResult, result);    
    
    }
    
    /**
     * Test of ToggleAliquotFractionByName method, of class SampleDateModel.
     */
    @Test
    public void test_ToggleAliquotFractionByName() {
        //Note that this test throws the java.lang.NoSuchMethodException:org.earthtime.UPb_Redux.valueModels.SampleDateModel.(java.util.Vector)
        System.out.println("Testing SampleDateModel's ToggleAliquotFractionByName(String fractionID)");
        String fractionID = "hello";
        SampleDateModel instance = new SampleDateModel();
        
        Vector<String> bob=new Vector<>(51);
        bob.add("hello");
        instance.setIncludedFractionIDsVector(bob);        
        
        assertEquals("[hello]",instance.getIncludedFractionIDsVector().toString());
   
        instance.ToggleAliquotFractionByName(fractionID);

        assertEquals("[]",instance.getIncludedFractionIDsVector().toString());
        
    }
    
    /**
     * Test of ToggleSampleFractionByName method, of class SampleDateModel.
     */
    @Test
    public void test_ToggleSampleFractionByName() {
        //Note that this test throws the java.lang.NoSuchMethodException:org.earthtime.UPb_Redux.valueModels.SampleDateModel.(java.util.Vector)        
        System.out.println("Testing SampleDateModel's ToggleSampleFractionByName(String fractionID)");
        String fractionID = "hello";
        SampleDateModel instance = new SampleDateModel();
        
        Vector<String> bob=new Vector<>(51);
        bob.add("hello");
        instance.setIncludedFractionIDsVector(bob);        
        
        assertEquals("[hello]",instance.getIncludedFractionIDsVector().toString());
   
        instance.ToggleSampleFractionByName(fractionID);

        assertEquals("[]",instance.getIncludedFractionIDsVector().toString());
        
    }
    
    /**
     * Test of getInternalTwoSigmaUnctWithStandardRatioVarUnct method, of class SampleDateModel.
     */
    @Test
    public void test_GetInternalTwoSigmaUnctWithStandardRatioVarUnct() {
        System.out.println("Testing SampleDateModel's getInternalTwoSigmaUnctWithStandardRatioVarUnct()");
        SampleDateModel instance = new SampleDateModel();
        BigDecimal expResult = null;
        BigDecimal result = instance.getInternalTwoSigmaUnctWithStandardRatioVarUnct();
        assertEquals(expResult, result);

        expResult=new BigDecimal("51");
        instance.setInternalTwoSigmaUnctWithStandardRatioVarUnct(expResult);
        result=instance.getInternalTwoSigmaUnctWithStandardRatioVarUnct();
        assertEquals(expResult, result); 
    }
    
    /**
     * Test of setInternalTwoSigmaUnctWithStandardRatioVarUnct method, of class SampleDateModel.
     */
    @Test
    public void test_SetInternalTwoSigmaUnctWithStandardRatioVarUnct() {
        System.out.println("Testing SampleDateModel's setInternalTwoSigmaUnctWithStandardRatioVarUnct(BigDecimal internalTwoSigmaUnctWithStandardRatioVarUnct)");
        SampleDateModel instance = new SampleDateModel();
        BigDecimal expResult = null;
        BigDecimal result = instance.getInternalTwoSigmaUnctWithStandardRatioVarUnct();
        assertEquals(expResult, result);

        expResult=new BigDecimal("51");
        instance.setInternalTwoSigmaUnctWithStandardRatioVarUnct(expResult);
        result=instance.getInternalTwoSigmaUnctWithStandardRatioVarUnct();
        assertEquals(expResult, result); 
    }
    
    /**
     * Test of getMethodName method, of class SampleDateModel.
     */
    @Test
    public void test_GetMethodName() {
        System.out.println("Testing SampleDateModel's getMethodName()");
        SampleDateModel instance = new SampleDateModel();
        String expResult = "";
        String result = instance.getMethodName();
        assertEquals(expResult, result);

        expResult="sieg";
        instance.setMethodName(expResult);
        result=instance.getMethodName();
        assertEquals(expResult, result);   
       
    }
    
    /**
     * Test of setMethodName method, of class SampleDateModel.
     */
    @Test
    public void test_SetMethodName() {
        System.out.println("Testing SampleDateModel's setMethodName(String MethodName)");
        SampleDateModel instance = new SampleDateModel();
        String expResult = "";
        String result = instance.getMethodName();
        assertEquals(expResult, result);

        expResult="sieg";
        instance.setMethodName(expResult);
        result=instance.getMethodName();
        assertEquals(expResult, result);   
       
    }
    
    /**
     * Test of showFractionIdWithDateAndUnct method, of class SampleDateModel.
     */
    @Test
    public void test_ShowFractionIdWithDateAndUnct() {
        System.out.println("Testing SampleDateModel's showFractionIdWithDateAndUnct(Fraction fraction, String dateUnit)");
        Fraction fraction = new UPbFraction();
        
        String dateUnit = "";
        SampleDateModel instance = new SampleDateModel();
        String expResult = "NONE";
        String result = instance.showFractionIdWithDateAndUnct(fraction, dateUnit);
        
        
        assertEquals(expResult, result);
        
        fraction = new UPbFraction("hello");
        dateUnit = "hell";
        expResult="hello";
        
        result = instance.showFractionIdWithDateAndUnct(fraction, dateUnit);
        
        assertEquals(expResult, result);

        
    }
    
    /**
     * Test of ShowCustomDateNode method, of class SampleDateModel.
     */
    @Test
    public void test_ShowCustomDateNode() {
        System.out.println("Testing SampleDateModel's ShowCustomDateNode()");
        SampleDateModel instance = new SampleDateModel();
        String expResult = "date = 0 ± 0/0.00/0.00 Ma 2σ";
        String result = instance.ShowCustomDateNode();
        
        assertEquals(expResult, result);
        
        instance=new SampleDateModel("hello","there","silly",new BigDecimal("430"),"ABS",new BigDecimal("51"));        
        result = instance.ShowCustomDateNode();
        expResult="date = 0.00043 ± 0.00010/0.00/0.00 Ma 2σ";
        
        assertEquals(expResult, result);

        
    }
    
    /**
     * Test of ShowCustomMSWDwithN method, of class SampleDateModel.
     */
    @Test
    public void test_ShowCustomMSWDwithN() {
        System.out.println("Testing SampleDateModel's ShowCustomMSWDwithN()");
        SampleDateModel instance = new SampleDateModel();
        String expResult = "MSWD = 0, n = 0";
        String result = instance.ShowCustomMSWDwithN();
        
        assertEquals(expResult, result);
        
        instance=new SampleDateModel("hello","there","silly",new BigDecimal("6666666"),"ABS",new BigDecimal("84984"));  
        instance.setMeanSquaredWeightedDeviation(new BigDecimal("51"));
        Vector<String> bob=new Vector<>(51);
        bob.add("ave");
        instance.setIncludedFractionIDsVector(bob);
        
        expResult = "MSWD = 51, n = 1";
        
        result = instance.ShowCustomMSWDwithN();
        
        assertEquals(expResult, result);

    }
    
    /**
     * Test of ShowCustomMSWD method, of class SampleDateModel.
    */
    @Test
    public void test_ShowCustomMSWD() {
        System.out.println("Testing SampleDateModel's ShowCustomMSWD()");
        SampleDateModel instance = new SampleDateModel();
        String expResult = "MSWD = 0";
        String result = instance.ShowCustomMSWD();
        
        assertEquals(expResult, result);
        
        instance=new SampleDateModel("hello","there","silly",new BigDecimal("6666666"),"ABS",new BigDecimal("84984"));  
        instance.setMeanSquaredWeightedDeviation(new BigDecimal("51"));
        Vector<String> bob=new Vector<>(51);
        bob.add("ave");
        instance.setIncludedFractionIDsVector(bob);
        
        expResult = "MSWD = 51";
        
        result = instance.ShowCustomMSWD();
        
        assertEquals(expResult, result);

    }
    
    /**
     * Test of ShowCustomN method, of class SampleDateModel.
     */
    @Test
    public void test_ShowCustomN() {
        System.out.println("Testing SampleDateModel's ShowCustomN()");
        SampleDateModel instance = new SampleDateModel();
        String expResult = "n = 0";
        String result = instance.ShowCustomN();
        
        assertEquals(expResult, result);
        
        instance=new SampleDateModel("hello","there","silly",new BigDecimal("6666666"),"ABS",new BigDecimal("84984"));  
        instance.setMeanSquaredWeightedDeviation(new BigDecimal("51"));
        Vector<String> bob=new Vector<>(51);
        bob.add("ave");
        instance.setIncludedFractionIDsVector(bob);
        
        expResult = "n = 1";
        
        result = instance.ShowCustomN();
        
        assertEquals(expResult, result);

    }
    
    /**
     * Test of ShowCustomFractionCountNode method, of class SampleDateModel.
     */
    @Test
    public void test_ShowCustomFractionCountNode() {
        System.out.println("Testing SampleDateModel's ShowCustomFractionCountNode()");
        SampleDateModel instance = new SampleDateModel();
        String expResult = "n = 0";
        String result = instance.ShowCustomFractionCountNode();
        
        assertEquals(expResult, result);
        
        instance=new SampleDateModel("hello","there","silly",new BigDecimal("6666666"),"ABS",new BigDecimal("84984"));  
        instance.setMeanSquaredWeightedDeviation(new BigDecimal("51"));
        Vector<String> bob=new Vector<>(51);
        bob.add("ave");
        instance.setIncludedFractionIDsVector(bob);
        
        expResult = "n = 1";
        
        result = instance.ShowCustomFractionCountNode();
        
        assertEquals(expResult, result);
        
    }
    /**
     * Test of FormatValueAndTwoSigmaABSThreeWaysForPublication method, of class SampleDateModel.
     */
    @Test
    public void test_FormatValueAndTwoSigmaABSThreeWaysForPublication() {
        System.out.println("Testing SampleDateModel's FormatValueAndTwoSigmaABSThreeWaysForPublication(int divideByPowerOfTen,int uncertaintySigDigits)");
        int divideByPowerOfTen = 0;
        int uncertaintySigDigits = 0;
        SampleDateModel instance = new SampleDateModel();
        String expResult = "0 ± 0/0.00/0.00";
        String result = instance.FormatValueAndTwoSigmaABSThreeWaysForPublication(divideByPowerOfTen, uncertaintySigDigits);
        assertEquals(expResult, result);
        
        instance=new SampleDateModel("hello","there","silly",new BigDecimal("6666666"),"ABS",new BigDecimal("84984"));  
        instance.setMeanSquaredWeightedDeviation(new BigDecimal("51"));
        Vector<String> bob=new Vector<>(51);
        bob.add("ave");
        instance.setIncludedFractionIDsVector(bob);
        
        expResult = "6.67 ± 0.17/0.00/0.00";
        
        result = instance.FormatValueAndTwoSigmaABSThreeWaysForPublication(divideByPowerOfTen, uncertaintySigDigits);
        
        assertEquals(expResult, result);    }
    
    /**
     * Test of getDateName method, of class SampleDateModel.
     */
    @Test
    public void test_GetDateName() {
        System.out.println("Testing SampleDateModel's getDateName()");
        SampleDateModel instance = new SampleDateModel();
        String expResult = "";
        String result = instance.getDateName();
        assertEquals(expResult, result);

        expResult="sieg";
        instance.setDateName(expResult);
        result=instance.getDateName();
        assertEquals(expResult, result);   
    }
    
    /**
     * Test of setDateName method, of class SampleDateModel.
     */
    @Test
    public void test_SetDateName() {
        System.out.println("Testing SampleDateModel's setDateName(String dateName)");
        SampleDateModel instance = new SampleDateModel();
        String expResult = "";
        String result = instance.getDateName();
        assertEquals(expResult, result);

        expResult="sieg";
        instance.setDateName(expResult);
        result=instance.getDateName();
        assertEquals(expResult, result);   
    }
    
    /**
     * Test of getAliquot method, of class SampleDateModel.
     */
    @Test
    public void test_GetAliquot() {
        System.out.println("Testing SampleDateModel's getAliquot()");
        SampleDateModel instance = new SampleDateModel();
        Aliquot expResult = new UPbReduxAliquot();
        Aliquot result = instance.getAliquot();
        assertEquals(null, result);

        expResult=null;
        instance.setAliquot(expResult);
        result=instance.getAliquot();
        assertEquals(expResult, result); 
        
    }
    
        /**
     * Test of setAliquot method, of class SampleDateModel.
     */
    @Test
    public void test_SetAliquot() {
        System.out.println("Testing SampleDateModel's setAliquot(Aliquot aliquot)");
        SampleDateModel instance = new SampleDateModel();
        Aliquot expResult = new UPbReduxAliquot();
        Aliquot result = instance.getAliquot();
        assertEquals(null, result);

        expResult=null;
        instance.setAliquot(expResult);
        result=instance.getAliquot();
        assertEquals(expResult, result); 
    }
    
    /**
     * Test of getYorkLineFit method, of class SampleDateModel.
     */
    @Test
    public void test_GetYorkLineFit() {
        System.out.println("Testing SampleDateModel's getYorkLineFit()");
        SampleDateModel instance = new SampleDateModel();
        YorkLineFit expResult = new YorkLineFit();
        YorkLineFit result = instance.getYorkLineFit();
        assertEquals(null, result);

        expResult=null;
        instance.setYorkLineFit(expResult);
        result=instance.getYorkLineFit();
        assertEquals(expResult, result); 
    }
    
    /**
     * Test of setYorkLineFit method, of class SampleDateModel.
     */
    @Test
    public void test_SetYorkLineFit() {
        System.out.println("Testing SampleDateModel's setYorkLineFit(YorkLineFit)");
        SampleDateModel instance = new SampleDateModel();
        YorkLineFit expResult = new YorkLineFit();
        YorkLineFit result = instance.getYorkLineFit();
        assertEquals(null, result);

        expResult=null;
        instance.setYorkLineFit(expResult);
        result=instance.getYorkLineFit();
        assertEquals(expResult, result); 
    }
    
    /**
     * Test of getSample method, of class SampleDateModel.
     */
    @Test
    public void test_GetSample() {
        System.out.println("Testing SampleDateModel's getSample()");
        SampleDateModel instance = new SampleDateModel();
        Sample expResult = new Sample();
        SampleInterface result = instance.getSample();
        assertEquals(null, result);

        expResult=null;
        instance.setSample(expResult);
        result=instance.getSample();
        assertEquals(expResult, result); 
    }
    
    /**
     * Test of setSample method, of class SampleDateModel.
     */
    @Test
    public void test_SetSample() {
        System.out.println("Testing SampleDateModel's setSample(Sample sample)");
        SampleDateModel instance = new SampleDateModel();
        Sample expResult = new Sample();
        SampleInterface result = instance.getSample();
        assertEquals(null, result);

        expResult=null;
        instance.setSample(expResult);
        result=instance.getSample();
        assertEquals(expResult, result); 
    }
    
    /**
     * Test of isDisplayedAsGraph method, of class SampleDateModel.
     */
    @Test
    public void test_IsDisplayedAsGraph() {
        System.out.println("Testing SampleDateModel's isDisplayedAsGraph()");
        SampleDateModel instance = new SampleDateModel();
        boolean expResult = false;
        boolean result = instance.isDisplayedAsGraph();
        assertEquals(expResult, result);
        
        expResult=true;
        instance.setDisplayedAsGraph(expResult);
        result = instance.isDisplayedAsGraph();
        assertEquals(expResult, result);
    
    }
    
    /**
     * Test of setDisplayedAsGraph method, of class SampleDateModel.
     */
    @Test
    public void test_SetDisplayedAsGraph() {
        System.out.println("Testing SampleDateModel's setDisplayedAsGraph(boolean displayedAsGraph)");
        SampleDateModel instance = new SampleDateModel();
        boolean expResult = false;
        boolean result = instance.isDisplayedAsGraph();
        assertEquals(expResult, result);
        
        expResult=true;
        instance.setDisplayedAsGraph(expResult);
        result = instance.isDisplayedAsGraph();
        assertEquals(expResult, result);
    }
    /**
     * Test of getSampleAnalysisType method, of class SampleDateModel.
     */
    @Test
    public void test_GetSampleAnalysisType() {
        System.out.println("Testing SampleDateModel's getSampleAnalysisType()");
        SampleDateModel instance = new SampleDateModel();
        SampleAnalysisTypesEnum expResult = SampleAnalysisTypesEnum.COMPILED;
        SampleAnalysisTypesEnum result = instance.getSampleAnalysisType();
        assertEquals(expResult, result);
        
        expResult=SampleAnalysisTypesEnum.GENERIC_UPB;
        instance.setSampleAnalysisType(expResult);
        result = instance.getSampleAnalysisType();
        assertEquals(expResult, result);    
    }
    
    /**
     * Test of setSampleAnalysisType method, of class SampleDateModel.
     */
    @Test
    public void test_SetSampleAnalysisType() {
        System.out.println("Testing SampleDateModel's setSampleAnalysisType(SampleAnalysisTypesEnum sampleAnalysisType)");
        SampleDateModel instance = new SampleDateModel();
        SampleAnalysisTypesEnum expResult = SampleAnalysisTypesEnum.COMPILED;
        SampleAnalysisTypesEnum result = instance.getSampleAnalysisType();
        assertEquals(expResult, result);
        
        expResult=SampleAnalysisTypesEnum.GENERIC_UPB;
        instance.setSampleAnalysisType(expResult);
        result = instance.getSampleAnalysisType();
        assertEquals(expResult, result);    
    }    
    
    
    
    
    
    
    ////////////////////////
    ////Unfinished Tests////
    //////////////////////// 
    
    /*
     *
     *
     *SampleDateModel.ISO_TotalPb(Vector<Fraction> myFractions)
     *SampleDateModel.ISO_SemiTotalPb(Vector<Fraction> myFractions)
     *SampleDateModel.ISO235_207(Vector<Fraction> myFractions)
     *SampleDateModel.ISO232_208(Vector<Fraction> myFractions)
     *SampleDateModel.ISO238_206(Vector<Fraction> myFractions)
     *  These methods literally do nothing but print out a line to the terminal
     *      that say that they were successfully call. Nothing to test.
     *
     *
     *
     *SampleDateModel.SA206_238(Vector<Fraction> myFractions)
     *SampleDateModel.SA207_235(Vector<Fraction> myFractions)
     *SampleDateModel.SA207_206(Vector<Fraction> myFractions)
     *SampleDateModel.SA208_232(Vector<Fraction> myFractions)
     *SampleDateModel.WM206_238(Vector<Fraction> myFractions)
     *SampleDateModel.WM207_235(Vector<Fraction> myFractions)
     *SampleDateModel.WM207_206(Vector<Fraction> myFractions)
     *SampleDateModel.WM208_232(Vector<Fraction> myFractions)
     *SampleDateModel.WM206_238r_Th(Vector<Fraction> myFractions)
     *SampleDateModel.WM207_235r_Pa(Vector<Fraction> myFractions)
     *SampleDateModel.WM207_206r_Th(Vector<Fraction> myFractions)
     *SampleDataModel.WM207_206r_Pa(Vector<Fraction> myFractions)
     *SampleDataModel.WM207_206r_ThPa(Vector<Fraction> myFractions)
     *SampleDataModel.fractionDateIsPositive(Fraction fraction)
     *SampleDataModel.UpperIntercept(Vector<Fraction> myFractions,ValueModel lowerInterceptModel)
     *SampleDataModel.LowerIntercept(Vector<Fraction> myFractions)
     *SampleDataModel.DetermineMaxDatePlusTwoSigma()
     *SampleDataModel.DetermineMinDateLessTwoSigma()
     *SampleDataModel.CalculateDateInterpretationForAliquot()
     *SampleDataModel.CalculateDateInterpretationForSample()
     *
     *
     *
     *
     *  These tests involve knowledge and digging, which due to the time I left,
     *      means I cannot complete them at this time. This list may not include
     *      every single method but should be rather comprehensive.
     *
     *
     *
     */    

        
    
    
    
    
    
    
    
    
    
}
