/*
 * AnalysisMeasures_Test_05042014Test.java
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

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author patrickbrewer
 */
public class AnalysisMeasures_Test_05042014Test {
    
    
    
    
    
    ////////////////////////////////////////////
    ////Constructor Tests///&///Method Tests////
    ////////////////////////////////////////////       
    

     /**
     * Test of AnalysisMeasures() method, of class AnalysisMeasures.
     */
    @Test
    public void test_constructor_0(){
	System.out.println("Testing AnalysisMeasures's enumerations and getters.");
        //Tests if values are correct for all enumerations, and tests the getters as well. You cannot instantiate new inumerations.
        
        AnalysisMeasures ave=AnalysisMeasures.alphaPb;
        assertEquals("alphaPb",ave.getName());
        
        ave=AnalysisMeasures.alphaU;
        assertEquals("alphaU",ave.getName());
        
        ave=AnalysisMeasures.ar231_235sample;
        assertEquals("ar231_235sample",ave.getName());

        ave=AnalysisMeasures.fractionMass;
        assertEquals("fractionMass",ave.getName());
    
        ave=AnalysisMeasures.pbBlankMassInGrams;
        assertEquals("pbBlankMassInGrams",ave.getName());    
    
        ave=AnalysisMeasures.r18O_16O;
        assertEquals("r18O_16O",ave.getName());    
    
        ave=AnalysisMeasures.r18O_16O_revised;
        assertEquals("r18O_16O_revised",ave.getName());
        
        ave=AnalysisMeasures.r238_235b;
        assertEquals("r238_235b",ave.getName());        
        
        ave=AnalysisMeasures.r265_267m;
        assertEquals("r265_267m",ave.getName());        
        
        ave=AnalysisMeasures.r265_268m;
        assertEquals("r265_268m",ave.getName());        
        
        ave=AnalysisMeasures.r270_265m;
        assertEquals("r270_265m",ave.getName());
        
        ave=AnalysisMeasures.r270_267m;
        assertEquals("r270_267m",ave.getName());        
        
        ave=AnalysisMeasures.r270_268m;
        assertEquals("r270_268m",ave.getName());        
        
        ave=AnalysisMeasures.rTh_Umagma;
        assertEquals("rTh_Umagma",ave.getName());        
        
        ave=AnalysisMeasures.tracerMassInGrams;
        assertEquals("tracerMassInGrams",ave.getName());        
       
        ave=AnalysisMeasures.uBlankMassInGrams;
        assertEquals("uBlankMassInGrams",ave.getName());     
    
    }    

    
    
    //////////////////
    //Method Tests////
    //////////////////       
    
    
    
    
    /**
     * Test of getNames method, of class AnalysisMeasures.
     */
    @Test
    public void test_GetNames() {
        System.out.println("Testing AnalysisMeasures's getNames()");
        String[] result = AnalysisMeasures.getNames();
        
        assertEquals("fractionMass",result[0]);
        assertEquals("tracerMassInGrams",result[1]);
        assertEquals("uBlankMassInGrams",result[2]);
        assertEquals("pbBlankMassInGrams",result[3]);
        assertEquals("alphaPb",result[4]);
        assertEquals("alphaU",result[5]);        
        assertEquals("r238_235b",result[6]);
        assertEquals("r238_235s",result[7]);
        assertEquals("r18O_16O",result[8]);
        assertEquals("r18O_16O_revised",result[9]);
        assertEquals("rTh_Umagma",result[10]);
        assertEquals("ar231_235sample",result[11]);
        assertEquals("r270_267m",result[12]);
        assertEquals("r265_267m",result[13]);
        assertEquals("r270_265m",result[14]);
        assertEquals("r270_268m",result[15]);
        assertEquals("r265_268m",result[16]);
        
    }

    /**
     * Test of contains method, of class AnalysisMeasures.
    */ 
    @Test
    public void test_Contains() {
        System.out.println("Testing AnalysisMeasures's contains(String checkString)");
        String checkString = "";
        boolean expResult = false;
        boolean result = AnalysisMeasures.contains(checkString);
        assertEquals(expResult, result);

        checkString="fractionMass";
        expResult=true;
        result = AnalysisMeasures.contains(checkString);
        assertEquals(expResult, result);
        
        checkString="actionMass";
        expResult=false;
        result = AnalysisMeasures.contains(checkString);
        assertEquals(expResult, result);        
    }

    /**
     * Test of getNamesSorted method, of class AnalysisMeasures.
     */
    @Test
    public void test_GetNamesSorted() {
        System.out.println("Testing AnalysisMeasures's getNamesSorted()");
        String[] result = AnalysisMeasures.getNamesSorted();
        
        assertEquals("alphaPb",result[0]);
        assertEquals("alphaU",result[1]);
        assertEquals("ar231_235sample",result[2]);
        assertEquals("fractionMass",result[3]);
        assertEquals("pbBlankMassInGrams",result[4]);
        assertEquals("r18O_16O",result[5]);        
        assertEquals("r18O_16O_revised",result[6]);
        assertEquals("r238_235b",result[7]);
        assertEquals("r238_235s",result[8]);
        assertEquals("r265_267m",result[9]);
        assertEquals("r265_268m",result[10]);
        assertEquals("r270_265m",result[11]);
        assertEquals("r270_267m",result[12]);
        assertEquals("r270_268m",result[13]);
        assertEquals("rTh_Umagma",result[14]);
        assertEquals("tracerMassInGrams",result[15]);
        assertEquals("uBlankMassInGrams",result[16]);
    }    
    
    
    
    
    
    
    
    
}
