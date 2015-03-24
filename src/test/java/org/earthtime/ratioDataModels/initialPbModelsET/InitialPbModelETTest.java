/*
 * InitialPbModelET_Test_04042014Test.java
 *
 * The naming of this file is the name of the .java file being tested, combined
 * with test since this is the test file, combined with the creation date of
 * this file. The test at the end is so that the file runs from NetBeans when
 * testing an entire project.
 *
 * Created on April 4, 2014.
 *
 *Version History:
 *April 4 2014: File Created and constructor tests finsihed. Method tests were
 *              completed at an unknown date. Unfinished tests exist at the
 *              bottom of the file.
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
import java.util.HashMap;
import java.util.Map;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.utilities.DateHelpers;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author patrickbrewer
 */
public class InitialPbModelETTest {
    
    
    
    /////////////////////////
    ////Constructor Tests////
    /////////////////////////    
    
     /**
     * Test of InitialPbModelET() method, of class InitialPbModelET.
     */
    @Test
    public void test_constructor_0(){
	System.out.println("Testing InitialPbModelET's InitialPbModelET(String Name,int versionNumber,int minorVersionNumber,String labName,String dateCertified,String Reference, String comment)");
        //Tests if default values are correct. This omits some of the correlation and covariance models.
        InitialPbModelET instance = new InitialPbModelET("tester",2,3,"sieg","5/1","this is a reference","this is a comment");
        String expResult="InitialPbModelET";
        String result=instance.getClassNameAliasForXML();
        assertEquals(expResult,result);
        
        expResult="tester";
        result=instance.getModelName();
        assertEquals(expResult,result);
        
        expResult="sieg";
        result=instance.getLabName();
        assertEquals(expResult,result);
        
        Calendar currentDate = Calendar.getInstance();
        SimpleDateFormat formatter= new SimpleDateFormat("5/1");
        expResult = formatter.format(currentDate.getTime());
        result=instance.getDateCertified();
        assertEquals(expResult,result);
        
        expResult="this is a reference";
        result=instance.getReference();
        assertEquals(expResult,result);        
        
        expResult="this is a comment";
        result=instance.getComment();
        assertEquals(expResult,result);
        
        int expectedResult=2;
        int actualResult=instance.getVersionNumber();
        assertEquals(expectedResult,actualResult);
        
        expectedResult=3;
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
     * Test of initializeNewRatiosAndRhos method, of class InitialPbModelET.
     */
    @Test
    public void test_InitializeNewRatiosAndRhos() {
        //Tests for rhos differences before and after function call
        System.out.println("Testing InitialPbModelET's initializeNewRatiosAndRhos(boolean UpdateOnly)");
        boolean updateOnly = false;
        InitialPbModelET instance = new InitialPbModelET("tester",2,3,"sieg","5/1","this is a reference","this is a comment");
        
        
        Map<String,BigDecimal> hello = new HashMap();
        hello.put("1", new BigDecimal("1.0"));
        hello.put("3", new BigDecimal("3.0"));
        hello.put("2", new BigDecimal("2.0"));
        instance.setRhosVarUnct(hello);
        
        String b4=instance.getRhosVarUnct().toString();
        
        instance.initializeNewRatiosAndRhos(updateOnly);
        
        String afta=instance.getRhosVarUnct().toString();
        
        if(b4.equals(afta))
            fail("Rhos before and after initialization should not be the same");
    }

    /**
     * Test of getNoneInstance method, of class InitialPbModelET.
     */
    @Test
    public void test_GetNoneInstance() {
        //Confirms the instance returned contains the correct parameters
        System.out.println("Testing InitialPbModelET's getNoneInstance()");
        AbstractRatiosDataModel result = InitialPbModelET.getNoneInstance();
        
        String act=result.getNameAndVersion();
        String exp="#NONE# v.1.0";
        assertEquals(exp, act);

        
        act=result.getLabName();
        exp="No Lab";
        assertEquals(exp,act);
        
        act=result.getComment();
        exp="empty model";
        assertEquals(exp,act);
        
        act=result.getReference();
        assertEquals(exp,act);
        
        int ex=3;
        Map actuaResult=result.getRhosVarUnct();
        int le=actuaResult.size();
        assertEquals(ex,le);
    }

    /**
     * Test of get_StaceyKramersInstance method, of class InitialPbModelET.
     */
    @Test
    public void test_GetStaceyKramersInstance() {
        //Tests that a StaceyKramer instance is created
        System.out.println("Testing InitialPbModelET's getStaceyKramersInstance()");
        AbstractRatiosDataModel result = InitialPbModelET.getStaceyKramersInstance();
        
        String expResult="InitialPbModelET";
        String res=result.getClassNameAliasForXML();
        assertEquals(expResult,res);
        
        expResult="StaceyKramers";
        res=result.getModelName();
        assertEquals(expResult,res);
        
        expResult="EARTHTIME";
        res=result.getLabName();
        assertEquals(expResult,res);
        
        Calendar currentDate = Calendar.getInstance();
        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd");
        expResult = formatter.format(currentDate.getTime());
        res=result.getDateCertified();
        assertEquals(expResult,res);
        
        expResult="Stacey, J. C. and Kramers, J., 1975. EPSL 26, pp. 207:221";
        res=result.getReference();
        assertEquals(expResult,res);        
        
        expResult="Model calculates initial Pb from fraction's est Date and uncertainty.";
        res=result.getComment();
        assertEquals(expResult,res);
        
        int expectedResult=1;
        int actualResult=result.getVersionNumber();
        assertEquals(expectedResult,actualResult);
        
        expectedResult=0;
        actualResult=result.getMinorVersionNumber();
        assertEquals(expectedResult,actualResult);
        
        int ex=3;
        Map actuaResult=result.getRhosVarUnct();
        int le=actuaResult.size();
        assertEquals(ex,le);
        
        Boolean exResult=true;
        Boolean acResult=result.isImmutable();
        assertEquals(exResult,acResult);         

    }
    
    /**
     * Test of getEARTHTIMESriLankaInitialPbModel method, of class InitialPbModelET.
     */
    @Test
    public void test_GetEARTHTIMESriLankaInitialPbModel() {
        System.out.println("Testing InitialPbModelET's getEARTHTIMESriLankaInitialPbModel()");
        AbstractRatiosDataModel result = InitialPbModelET.getEARTHTIMESriLankaInitialPbModel();
        
        String expResult="InitialPbModelET";
        String res=result.getClassNameAliasForXML();
        assertEquals(expResult,res);
        
        expResult="EARTHTIME SriLanka InitialPb";
        res=result.getModelName();
        assertEquals(expResult,res);
        
        expResult="EARTHTIME";
        res=result.getLabName();
        assertEquals(expResult,res);
        
        expResult="No reference";
        res=result.getReference();
        assertEquals(expResult,res);        
        
        expResult="EARTHTIME-supplied model";
        res=result.getComment();
        assertEquals(expResult,res);
        
        int expectedResult=1;
        int actualResult=result.getVersionNumber();
        assertEquals(expectedResult,actualResult);
        
        expectedResult=0;
        actualResult=result.getMinorVersionNumber();
        assertEquals(expectedResult,actualResult);
        
        int ex=3;
        Map actuaResult=result.getRhosVarUnct();
        int le=actuaResult.size();
        assertEquals(ex,le);
        
        Boolean exResult=true;
        Boolean acResult=result.isImmutable();
        assertEquals(exResult,acResult);         
    }

    /**
     * Test of createInstance method, of class InitialPbModelET.
     */
    @Test
    public void test_CreateInstance() {
        System.out.println("Testing InitialPbModelET's createInstance(String modelName,int versionNumber, int minorVersionNumber, String labName,String dateCertified,String reference, String comment, ValueModel[] ratios,Map<String,BigDecimal> rhos) ");
        String modelName = "test instance";
        int versionNumber = 0;
        int minorVersionNumber = 0;
        String labName = "lab instance";
        String dateCertified = "date instance";
        String reference = "reference instance";
        String comment = "comment instance";
        ValueModel[] ratios = new ValueModel[1];
        ValueModel instance0=new ValueModel("r207_339",new BigDecimal("-12.34567890"),"PCT",new BigDecimal(".9876543210"), BigDecimal.ZERO);          
        ratios[0]=instance0;
        Map<String, BigDecimal> rhos = new HashMap();
        rhos.put("hello", new BigDecimal("20.067"));
        AbstractRatiosDataModel result = InitialPbModelET.createInstance(modelName, versionNumber, minorVersionNumber, labName, dateCertified, reference, comment, ratios, rhos);
        
        String model=result.getModelName();
        int version=result.getVersionNumber();
        int minor=result.getMinorVersionNumber();
        String lab=result.getLabName();
        String date=result.getDateCertified();
        String ref=result.getReference();
        String com=result.getComment();
        int rho=result.getRhosVarUnct().size();
        int rhlength=3;
        
        assertEquals(modelName, model);
        assertEquals(versionNumber,version);
        assertEquals(minorVersionNumber,minor);
        assertEquals(labName,lab);
        assertEquals(dateCertified,date);
        assertEquals(reference,ref);
        assertEquals(comment,com);
        assertEquals(rhlength,rho);
    }

    /**
     * Test of createNewInstance method, of class InitialPbModelET.
     */
    @Test
    public void test_CreateNewInstance() {
        System.out.println("Testing InitialPbModelET's createNewInstance()");
        AbstractRatiosDataModel result = InitialPbModelET.createNewInstance();

        String name="New InitialPb Model";
        int version=1;
        int minorversion=0;
        String lab="No Lab";
        String date=DateHelpers.defaultEarthTimeDateString();
        String ref="No reference";
        String comment="No comment";
         
        assertEquals(name, result.getModelName());
        assertEquals(version,result.getVersionNumber());
        assertEquals(minorversion,result.getMinorVersionNumber());
        assertEquals(lab,result.getLabName());
        assertEquals(date,result.getDateCertified());
        assertEquals(ref,result.getReference());
        assertEquals(comment,result.getComment());
    }
    
    /**
     * Test of cloneModel method, of class InitialPbModelET.
     */
    @Test
    public void test_CloneModel() {
        System.out.println("Testing InitialPbModelET's cloneModel()");
        String modelName = "test instance";
        int versionNumber = 0;
        int minorVersionNumber = 0;
        String labName = "lab instance";
        String dateCertified = "date instance";
        String reference = "reference instance";
        String comment = "comment instance";

        AbstractRatiosDataModel result = new InitialPbModelET(modelName,versionNumber,minorVersionNumber,labName,dateCertified,reference,comment);

        AbstractRatiosDataModel actualResult=result.cloneModel();
        
        String model=actualResult.getModelName();
        int version=actualResult.getVersionNumber();
        int minor=actualResult.getMinorVersionNumber();
        String lab=actualResult.getLabName();
        String date=actualResult.getDateCertified();
        String ref=actualResult.getReference();
        String com=actualResult.getComment();

        assertEquals(modelName, model);
        assertEquals(versionNumber,version);
        assertEquals(minorVersionNumber,minor);
        assertEquals(labName,lab);
        assertEquals(dateCertified,date);
        assertEquals(reference,ref);
        assertEquals(comment,com);
        
    }

    /**
     * Test of getClassNameAliasForXML method, of class InitialPbModelET.
     */
    @Test
    public void test_GetClassNameAliasForXML() {
        System.out.println("Testing InitialPbModelET's getClassNameAliasForXML()");
        String modelName = "test instance";
        int versionNumber = 0;
        int minorVersionNumber = 0;
        String labName = "lab instance";
        String dateCertified = "date instance";
        String reference = "reference instance";
        String comment = "comment instance";

        AbstractRatiosDataModel instance = new InitialPbModelET(modelName,versionNumber,minorVersionNumber,labName,dateCertified,reference,comment);
        
        String expResult = "InitialPbModelET";
        String result = instance.getClassNameAliasForXML();
        
        assertEquals(expResult, result);
    }

        
    
    
    
    ////////////////////////
    ////Unfinished Tests////
    //////////////////////// 
    
    
    
    
    
    /*
     *
     *
     *
     *
     *
     *InitialPbModelET.getClassNameAliasForXML()
     *  I cannot devise a proper test at this time that ensures that the Xstream
     *      is customized for this class as opposed to a ValueModel. The test
     *      I previously devised works fine for both, but there should be some
     *      difference - I cannot get the customized string for this instance.
     *      My notes from time past state that I need a getter.
     *
     *
     *
     *
     *InitialPbModelET.readResolve()
     *InitialPbModelET.removeSelf()
     *InitialPbModelET.getArrayListOfModels()
     *  At the moment there is a persistence issue that does not allow me to
     *      properly test these methods. Bowring is going to look at it and has
     *      more details. I also do not have a proper getter to the persistent
     *      array.
     *
     *
     *
     *
     */    
    
    

     
    
    
 
    
    
}
