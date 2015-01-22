/*
 * IsotopesEnum_Test_05032014Test.java
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
 *May 3 2014: File Created. Constructor and method tests completed.
 *
 *
 *
 *Developed for the CIRDLES lab at the College of Charleston by Patrick Brewer.
 *
 *
 */

package org.earthtime.isotopes;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author patrickbrewer
 */
public class IsotopesEnum_Test_05032014Test {
    
    
    
    ////////////////////////////////////////////
    ////Constructor Tests///&///Method Tests////
    ////////////////////////////////////////////       
    

     /**
     * Test of IsotopesEnum() method, of class IsotopesEnum.
     */
    @Test
    public void test_constructor_0(){
	System.out.println("Testing IsotopesEnum's enumerations and getters.");
        //Tests if values are correct for all enumerations, and tests the getters as well. You cannot instantiate new inumerations.
        
        IsotopesEnum ave=IsotopesEnum.Hg202;
        assertEquals("Hg",ave.getSymbol());
        assertEquals(202,ave.getAtomicMass());
        assertEquals("202Hg",ave.getPrettyName());
        assertEquals("Hg202",ave.getName());

        ave=IsotopesEnum.Pb204;
        assertEquals("Pb",ave.getSymbol());
        assertEquals(204,ave.getAtomicMass());
        assertEquals("204Pb",ave.getPrettyName());
        assertEquals("Pb204",ave.getName());    
    
        ave=IsotopesEnum.Pb206;
        assertEquals("Pb",ave.getSymbol());
        assertEquals(206,ave.getAtomicMass());
        assertEquals("206Pb",ave.getPrettyName());
        assertEquals("Pb206",ave.getName());    
    
        ave=IsotopesEnum.Pb207;
        assertEquals("Pb",ave.getSymbol());
        assertEquals(207,ave.getAtomicMass());
        assertEquals("207Pb",ave.getPrettyName());
        assertEquals("Pb207",ave.getName());
    
        ave=IsotopesEnum.Pb208;
        assertEquals("Pb",ave.getSymbol());
        assertEquals(208,ave.getAtomicMass());
        assertEquals("208Pb",ave.getPrettyName());
        assertEquals("Pb208",ave.getName());    
    
        ave=IsotopesEnum.Th232;
        assertEquals("Th",ave.getSymbol());
        assertEquals(232,ave.getAtomicMass());
        assertEquals("232Th",ave.getPrettyName());
        assertEquals("Th232",ave.getName());    
    
        ave=IsotopesEnum.U235;
        assertEquals("U",ave.getSymbol());
        assertEquals(235,ave.getAtomicMass());
        assertEquals("235U",ave.getPrettyName());
        assertEquals("U235",ave.getName());    
    
        ave=IsotopesEnum.U238;
        assertEquals("U",ave.getSymbol());
        assertEquals(238,ave.getAtomicMass());
        assertEquals("238U",ave.getPrettyName());
        assertEquals("U238",ave.getName());    
    }    
    
    
    
    
    
    
}
