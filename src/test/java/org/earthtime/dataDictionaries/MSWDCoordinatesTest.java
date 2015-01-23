/*
 * MSWDCoordinates_Test_05042014Test.java
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
import java.math.BigDecimal;

/**
 *
 * @author patrickbrewer
 */
public class MSWDCoordinatesTest {
    
    
    ////////////////////////////////////////////
    ////Constructor Tests///&///Method Tests////
    ////////////////////////////////////////////      
    
     /**
     * Test of MSWDCoordinates() method, of class MSWDCoordinates.
     */
    @Test
    public void test_constructor_0(){
	System.out.println("Testing MSWDCoordinates's enumerations and getters.");
        //Tests if values are correct for all enumerations, and tests the getters as well. You cannot instantiate new inumerations.
        
        double[][] ave=MSWDCoordinates.valuesByPointCount;
        BigDecimal lol=new BigDecimal("1.394662164000000093011522039887495338916778564453125");
        double lol1=ave[51][3];
        assertEquals(lol,new BigDecimal(lol1));
        
    }    
    
    
    
    
    
    
}
