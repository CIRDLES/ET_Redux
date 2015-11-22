/*
 * AbstractRatiosDataModel_Test_02172014Test.java
 *
 * The naming of this file is the name of the .java file being tested, combined
 * with test since this is the test file, combined with the creation date of
 * this file. The test at the end is so that the file runs from NetBeans when
 * testing an entire project.
 *
 * Created on March 29, 2014.
 *
 *Version History:
 *March 29 2014: File Created.
 *April 2 2014: Constructor and Method tests completed.
 *
 *
 *Developed for the CIRDLES lab at the College of Charleston by Patrick Brewer.
 *
 *
 */



package org.earthtime.ratioDataModels.initialPbModelsET;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author patrickbrewer
 */


public class StaceyKramerInitialPbModelETTest {
    
      
    /////////////////////////
    ////Constructor Tests////
    /////////////////////////
    
    
    /**
     * Test of StaceyKramersInitialPbModelET() method, of class StaceyKramersInitialPbModelET.
    */
    @Test
    public void test_Constructor_0() {
        System.out.println("Testing StaceyKramersInitialPbModelET's StaceyKramersInitialPbModelET()");
        StaceyKramersInitialPbModelET instance = new StaceyKramersInitialPbModelET();

        String expResult="InitialPbModelET";
        String result=instance.getClassNameAliasForXML();
        assertEquals(expResult,result);
        
        expResult="StaceyKramers";
        result=instance.getModelName();
        assertEquals(expResult,result);
        
        expResult="EARTHTIME";
        result=instance.getLabName();
        assertEquals(expResult,result);
        
        Calendar currentDate = Calendar.getInstance();
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd");
        expResult = formatter.format(currentDate.getTime());
        result=instance.getDateCertified();
        assertEquals(expResult,result);
        
        expResult="Stacey, J. C. and Kramers, J., 1975. EPSL 26, pp. 207:221";
        result=instance.getReference();
        assertEquals(expResult,result);        
        
        expResult="Model calculates initial Pb from fraction's est Date and uncertainty.";
        result=instance.getComment();
        assertEquals(expResult,result);
        
        int expectedResult=1;
        int actualResult=instance.getVersionNumber();
        assertEquals(expectedResult,actualResult);
        
        expectedResult=0;
        actualResult=instance.getMinorVersionNumber();
        assertEquals(expectedResult,actualResult);
        
        int ex=3;
        Map actuaResult=instance.getRhosVarUnct();
        int le=actuaResult.size();
        assertEquals(ex,le);
        
        Boolean exResult=false;
        Boolean acResult=instance.isImmutable();
        assertEquals(exResult,acResult); 
    }
    
    
    ////////////////////
    ////Method Tests////
    ////////////////////

    /**
     * Test of copyModel method, of class StaceyKramersInitialPbModelET.
    */ 
    @Test
    public void test_CopyModel() {
        System.out.println("Testing StaceyKramersInitialPbModelET's copyModel()");
        StaceyKramersInitialPbModelET instance = new StaceyKramersInitialPbModelET();
        StaceyKramersInitialPbModelET expResult = instance;
        AbstractRatiosDataModel result = instance.copyModel(true);
        result.setModelName("StaceyKramers");
        assertEquals(expResult, result);
    }

    /**
     * Test of calculateRatios method, of class StaceyKramersInitialPbModelET.
   */
    @Test
    public void test_CalculateRatios() {
        System.out.println("Testing StaceyKramersInitialPbModelET's calculateRatios(BigDecimal estimatedAgeinMA,BigDecimal lambda238, BigDecimal lambda235, BigDecimal lambda232)");
        BigDecimal estimatedAgeInMA = new BigDecimal("3800");
        BigDecimal lambda238 = new BigDecimal("0.00000000098485");
        BigDecimal lambda235 = new BigDecimal("0.00000000098485");
        BigDecimal lambda232 = new BigDecimal("0.00000000098485");
        StaceyKramersInitialPbModelET instance = new StaceyKramersInitialPbModelET();
        
        //If comparison to 3700 is 1
        BigDecimal before=instance.getDatumByName("r206_204c").getValue();
        instance.calculateRatios(estimatedAgeInMA, lambda238, lambda235, lambda232);
        BigDecimal after=instance.getDatumByName("r206_204c").getValue();
        if(before.equals(after))
            fail("CalculateRatios should have changed the value of instance");
        
        //If comparison is not 1
        StaceyKramersInitialPbModelET instance0=new StaceyKramersInitialPbModelET();
        estimatedAgeInMA=new BigDecimal("3600");
        before=instance0.getDatumByName("r206_204c").getValue();
        instance0.calculateRatios(estimatedAgeInMA, lambda238, lambda235, lambda232);
        after=instance0.getDatumByName("r206_204c").getValue();
        if(before.equals(after))
            fail("CalculateRatios should have changed the value of instance");        
    }
    
    /**
     * Test of calculateUncertaintiesAndRhos method, of class StaceyKramersInitialPbModelET.
    */
    @Test
    public void test_CalculateUncertaintiesAndRhos() {
        System.out.println("Testing StaceyKramersInitialPbModelET's calculateUncertaintiesAndRhos(BigDecimal oneSigmaPct, BigDecimal skRho)");
        BigDecimal oneSigmaPct = new BigDecimal("700");
        BigDecimal skRho = new BigDecimal("869");
        StaceyKramersInitialPbModelET instance = new StaceyKramersInitialPbModelET();
        
        String uncB4=instance.getDatumByName("r206_204c").getUncertaintyType();
        BigDecimal oneB4=instance.getDatumByName("r206_204c").getOneSigma();
        String rhosB4=instance.getRhosVarUnct().toString();
        
        instance.calculateUncertaintiesAndRhos(oneSigmaPct, skRho, BigDecimal.ZERO, BigDecimal.ZERO);

        String uncAfter=instance.getDatumByName("r206_204c").getUncertaintyType();
        BigDecimal oneAfter=instance.getDatumByName("r206_204c").getOneSigma();
        String rhosAfter=instance.getRhosVarUnct().toString();
        
        if(uncB4.equals(uncAfter))
            fail("Uncertainty should not be the same before and after");
    
        if(oneB4.equals(oneAfter))
            fail("OneSigma should not be the same before and after");
        
        if(rhosB4.equals(rhosAfter))
            fail("Rhos should not be the same before and after");        
    }
    

}
    
    
    
    
    
    
    
    
    
    
    
    
    


