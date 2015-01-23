/*
 * AcquisitionTypesEnum_Test_05042014Test.java
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
public class AcquisitionTypesEnumTest {
    
    
    
    ////////////////////////////////////////////
    ////Constructor Tests///&///Method Tests////
    ////////////////////////////////////////////       
    

     /**
     * Test of AcquisitionTypesEnum() method, of class AcquisitionTypesEnum.
     */
    @Test
    public void test_constructor_0(){
	System.out.println("Testing AcquisitionTypesEnum's enumerations and getters.");
        //Tests if values are correct for all enumerations, and tests the getters as well. You cannot instantiate new inumerations.
        
        AcquisitionTypesEnum ave=AcquisitionTypesEnum.MULTI_SEQUENCE;
        assertEquals("MULTI_SEQUENCE",ave.getName());
        
        ave=AcquisitionTypesEnum.SINGLE_COLLECTOR;
        assertEquals("SINGLE_COLLECTOR",ave.getName());
        
        ave=AcquisitionTypesEnum.STATIC;
        assertEquals("STATIC",ave.getName());

 
    }    




}
