/*
 * MassSpecTypeEnum_Test_05042014Test.java
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
public class MassSpecTypeEnumTest {
    
    
    
    
    
    ////////////////////////////////////////////
    ////Constructor Tests///&///Method Tests////
    ////////////////////////////////////////////      
    
     /**
     * Test of MassSpecTypeEnum() method, of class MassSpecTypeEnum.
     */
    @Test
    public void test_constructor_0(){
	System.out.println("Testing FileTypeEnum's enumerations and getters.");
        //Tests if values are correct for all enumerations, and tests the getters as well. You cannot instantiate new inumerations.
        
        MassSpecTypeEnum ave=MassSpecTypeEnum.MULTI;
        assertEquals("MULTI",ave.getName());
        assertEquals("Multi-Collector",ave.getPrettyName());

        ave=MassSpecTypeEnum.SINGLE;
        assertEquals("SINGLE",ave.getName());
        assertEquals("Single-Collector",ave.getPrettyName());
    
    
    
    }        
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
