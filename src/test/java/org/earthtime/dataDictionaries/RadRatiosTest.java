/*
 * RadRatios_Test_05042014Test.java
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

/**
 *
 * @author patrickbrewer
 */
public class RadRatiosTest {
    
    
    
    

    
    ////////////////////////////////////////////
    ////Constructor Tests///&///Method Tests////
    ////////////////////////////////////////////      
    
     /**
     * Test of RadRatios() method, of class RadRatios.
     */
    @Test
    public void test_constructor_0(){
	System.out.println("Testing RadRatios's enumerations and getters.");
        //Tests if values are correct for all enumerations, and tests the getters as well. You cannot instantiate new inumerations.
        
        RadRatios ave=RadRatios.r206_207r;
        assertEquals("r206_207r",ave.getName());
        
        ave=RadRatios.r206_238r;
        assertEquals("r206_238r",ave.getName());        
        
        ave=RadRatios.r207_206r;
        assertEquals("r207_206r",ave.getName());          
        
        ave=RadRatios.r207_235r;
        assertEquals("r207_235r",ave.getName());          

        ave=RadRatios.r208_232r;
        assertEquals("r208_232r",ave.getName());          
        
        String[] list=RadRatios.getNamesSorted();
        assertEquals("r206_207r",list[1]);
        assertEquals("r206_238r",list[2]);
        assertEquals("r207_206r",list[4]);
        assertEquals("r207_235r",list[5]);
        assertEquals("r208_232r",list[7]);

    
    }    
            
        
    
    
    
    
    
    
    
    
    
}
