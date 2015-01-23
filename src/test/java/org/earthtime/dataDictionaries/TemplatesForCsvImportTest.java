/*
 * TemplatesForCsvImport_Test_05052014Test.java
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
public class TemplatesForCsvImportTest {
    
    

    
    ////////////////////////////////////////////
    ////Constructor Tests///&///Method Tests////
    ////////////////////////////////////////////      
    
     /**
     * Test of TemplatesForCsvImport() method, of class TemplatesForCsvImport.
     */
    @Test
    public void test_constructor_0(){
	System.out.println("Testing TemplatesForCsvImport's enumerations and getters.");
        //Tests if values are correct for all enumerations, and tests the getters as well. You cannot instantiate new inumerations.
        
        String ave=TemplatesForCsvImport.GenericUPbIsotopicLegacyDataSampleFieldNames_A;
        String answer="Sample and fractions, mass(g), # of grains, Pb conc (ppm), U conc (ppm), Th/U, Pb*/Pbc, Pbc (pg), 206Pb/204Pb, 208Pb/206Pb, 207Pb/206Pb, 2-sig%, 207Pb/235Pb, 2-sig%, 206Pb/238U, 2-sig%, rho 6/8-7/5, age 207Pb/206Pb, 2-sig, age 207Pb/235U, 2-sig, age 206Pb/238U, 2-sig ";
        
        assertEquals(answer,ave);

        ave=TemplatesForCsvImport.IDTIMSLegacyDataSampleFieldNames_MIT;
        answer="Fraction, 206/238, 2-sig%, 207/235, 2-sig%, 207/206, 2-sig%, rho 6/8-7/5, 206/204, 208/206, Pb*/Pbc, Pb* pg, Pbc pg, conc U, conc Pb, Th/U samp, frac mass, age206/238, 2-sig, age207/235, 2-sig, age207/206, 2-sig, age206/238xTh, 2-sig, age207/235xPa, 2-sig, age207/206xTh, 2-sig, 207/206xPa, 2-sig";
        
        assertEquals(answer,ave);        
    
        ave=TemplatesForCsvImport.LAICPMSLegacyDataSampleFieldNames_MC_UA;
        answer="Analysis, U(ppm), 206/204, U/Th, 206/207, 1-sig%, 207/235, 1-sig%, 206/238, 1-sig%, rho 6/8-7/5, age206/238, 1-sig, age207/235, 1-sig, age207/206, 1-sig, Best age, 1-sig";
        
        assertEquals(answer,ave);      
    
        ave=TemplatesForCsvImport.LAICPMSLegacyDataSampleFieldNames_NIGL;
        answer="Analysis, Pb(ppm), U(ppm), 206/204, 1-sig%, 207/206, 1-sig%, 207/235, 1-sig%, 206/238, 1-sig%, rho 6/8-7/5, age207/206, 2-sig-abs, age206/238, 2-sig-abs, age207/235, 2-sig-abs";
        
        assertEquals(answer,ave);          
    
        ave=TemplatesForCsvImport.LAICPMSLegacyDataSampleFieldNames_SC_WSU_vA;
        answer="Analysis, 207Pb/235U, 1-sig abs, 206Pb/238U, 1-sig abs, 207Pb/206Pb,1-sig abs, Th/U,207Pb/235U,1-sig abs,206Pb/238U,1-sig abs,ignored,ignored,207Pb/206Pb,1-sig abs, ignored,ignored,ignored,ignored,ignored,rho 6/8-7/5";
        
        assertEquals(answer,ave);          
    
        ave=TemplatesForCsvImport.LAICPMSLegacyDataSampleFieldNames_SC_WSU_vB;
        answer="Analysis, 207Pb/235U, 1-sig abs, 206Pb/238U, 1-sig abs, 207Pb/206Pb,1-sig abs, Th/U,207Pb/235U,1-sig abs,206Pb/238U,1-sig abs, 207Pb/206Pb,1-sig abs, ignored,ignored,ignored,ignored,ignored,ignored,ignored,ignored,ignored,rho 6/8-7/5";
        
        assertEquals(answer,ave);          
    }
    
    
    
    
    
    
}
