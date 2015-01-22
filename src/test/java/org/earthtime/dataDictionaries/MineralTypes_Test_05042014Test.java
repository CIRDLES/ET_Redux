/*
 * MineralTypes_Test_05042014Test.java
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
public class MineralTypes_Test_05042014Test {
    
    
    

    ////////////////////////////////////////////
    ////Constructor Tests///&///Method Tests////
    ////////////////////////////////////////////      
    
     /**
     * Test of MineralTypes() method, of class MineralTypes.
     */
    @Test
    public void test_constructor_0(){
	System.out.println("Testing MineralTypes's enumerations and getters.");
        //Tests if values are correct for all enumerations, and tests the getters as well. You cannot instantiate new inumerations.
        
        MineralTypes ave=MineralTypes.APATITE;
        assertEquals("apatite",ave.getName());

        ave=MineralTypes.CALCITE;
        assertEquals("calcite",ave.getName());        
        
        ave=MineralTypes.MONAZITE;
        assertEquals("monazite",ave.getName());        
        
        ave=MineralTypes.OTHER;
        assertEquals("other",ave.getName());
        
        ave=MineralTypes.RUTILE;
        assertEquals("rutile",ave.getName());        
        
        ave=MineralTypes.TITANITE;
        assertEquals("titanite",ave.getName());        
        
        ave=MineralTypes.WHOLE_ROCK;
        assertEquals("whole rock",ave.getName());        
        
        ave=MineralTypes.XENOTIME;
        assertEquals("xenotime",ave.getName());        
        
        ave=MineralTypes.ZIRCON;
        assertEquals("zircon",ave.getName());        

        String[] list=MineralTypes.getNames();
        assertEquals("other",list[0]);
        assertEquals("apatite",list[1]);
        assertEquals("calcite",list[2]);
        assertEquals("monazite",list[3]);
        assertEquals("rutile",list[4]);
        assertEquals("titanite",list[5]);
        assertEquals("whole rock",list[6]);
        assertEquals("xenotime",list[7]);
        assertEquals("zircon",list[8]);

    }    
            
    
    
    
    
    //////////////////
    //Method Tests////
    //////////////////       
    
    
    
    
    /**
     * Test of validateStandardMineralTypeName method, of class MineralTypes.
    */ 
    @Test
    public void test_ValidateStandardMineralTypeName() {
        System.out.println("Testing MineralTypes's validateStandardMineralTypeName(String checkString)");
        String checkString = "";
        String result = MineralTypes.validateStandardMineralTypeName(checkString);
        assertEquals("other", result);

        checkString="zircon";
        result=MineralTypes.validateStandardMineralTypeName(checkString);
        assertEquals("zircon",result);
    }
        
    
    
    
    
    
}
