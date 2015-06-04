/*
 * CovarianceMatrixModelWithSubMatricesModel_Test_05012014Test.java
 *
 * The naming of this file is the name of the .java file being tested, combined
 * with test since this is the test file, combined with the creation date of
 * this file. The test at the end is so that the file runs from NetBeans when
 * testing an entire project.
 *
 * Created on May 1, 2014.
 *
 *
 *Version History:
 *May 1 2014: File Created. 
 *May 2 2014: Constructor and most method tests done. Unfinished tests exist at
 *              at the bottom of the file.
 *
 *
 *Developed for the CIRDLES lab at the College of Charleston by Patrick Brewer.
 *
 *
 */

package org.earthtime.matrices.matrixModels;

import java.util.Map;
import Jama.Matrix;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author patrickbrewer
 */
public class CovarianceMatrixWithSubMatricesModelTest {
    
    
    /////////////////////////
    ////Constructor Tests////
    /////////////////////////      
    
    
    
     /**
     * Test of CovarianceMatrixModelWithSubMatricesModel() method, of class CovarianceMatrixModelWithSubMatricesModel.
     */
    @Test
    public void test_constructor_0(){
	System.out.println("Testing CovarianceMatrixWithSubMatricesModel's CovarianceMatrixWithSubMatricesModel()");
        //Tests if values are correct
        CovarianceMatrixWithSubMatricesModel hi=new CovarianceMatrixWithSubMatricesModel();
        String result=hi.levelName;
        Map<Integer,String> ro=hi.rows;
        Map<String,Integer> cp=hi.cols;
        Matrix ma=hi.matrix;
         
        assertEquals("Covariances",result);
        assertEquals("{}",ro.toString());
        assertEquals("{}",cp.toString());
        assertEquals(null,ma);
        assertEquals(null,hi.getLambdasCovarianceMatrix());
        assertEquals(null,hi.getTracerCovarianceMatrix());
        assertEquals(null,hi.getAnalyticalCovarianceMatrix());
        assertEquals(false,hi.isSubMatricesExist());

        }            
        
    
    
    ////////////////////
    ////Method Tests////
    ////////////////////      
    
    
    
    

    /**
     * Test of copy method, of class CovarianceMatrixWithSubMatricesModel.
     */
    @Test
    public void test_Copy() {
        System.out.println("Testing CovarianceMatrixWithSubMatricesModel's copy(CovarianceMatrixWithSubMatricesModel parent)");
        CovarianceMatrixWithSubMatricesModel instance = new CovarianceMatrixWithSubMatricesModel();
        
        Matrix ma=new Matrix(1,1);
        ma.set(0, 0, 51);
        
        instance.setMatrix(ma);
        
        Map<Integer,String> co=new HashMap();
        Map<Integer,String> ro=new HashMap();
        co.put(1, "one");
        ro.put(2, "two");
        
        instance.setCols(co);
        instance.setRows(ro);
        
        instance.setSubMatricesExist(true);
        
        CovarianceMatrixWithSubMatricesModel b4=(CovarianceMatrixWithSubMatricesModel)instance.getLambdasCovarianceMatrix();
        CovarianceMatrixWithSubMatricesModel before=(CovarianceMatrixWithSubMatricesModel)instance.getTracerCovarianceMatrix();
        CovarianceMatrixWithSubMatricesModel b34=(CovarianceMatrixWithSubMatricesModel)instance.getAnalyticalCovarianceMatrix();
        
        
        CovarianceMatrixWithSubMatricesModel lamb=new CovarianceMatrixWithSubMatricesModel();
        instance.setLambdasCovarianceMatrix(lamb);
        
        
        CovarianceMatrixWithSubMatricesModel trac=new CovarianceMatrixWithSubMatricesModel();
        instance.setTracerCovarianceMatrix(trac);
        
        
        CovarianceMatrixWithSubMatricesModel anal=new CovarianceMatrixWithSubMatricesModel();
        instance.setAnalyticalCovarianceMatrix(anal);
        
        AbstractMatrixModel result = instance.copy();

        assertEquals("{one=1}",result.cols.toString() );
        assertEquals("{2=two}",result.rows.toString() );
        assertEquals(51,(int)result.getMatrix().get(0, 0) );
        assertEquals(true,((CovarianceMatrixWithSubMatricesModel)result).isSubMatricesExist());
        if(instance.getLambdasCovarianceMatrix().equals(b4))
            fail("Lambas should not be null!");
        if(instance.getTracerCovarianceMatrix().equals(before))
            fail("Lambas should not be null!");
        if(instance.getAnalyticalCovarianceMatrix().equals(b34))
            fail("Lambas should not be null!");        
    }
 
        /**
     * Test of isSubMatricesExist method, of class CovarianceMatrixWithSubMatricesModel.
     *   */
    @Test
    public void test_IsSubMatricesExist() {
        System.out.println("Testing CovarianceMatrixWithSubMatricesModel's isSubMatricesExist()");
        CovarianceMatrixWithSubMatricesModel instance = new CovarianceMatrixWithSubMatricesModel();
        boolean expResult = false;
        boolean result = instance.isSubMatricesExist();
        assertEquals(expResult, result);
        expResult=true;
        instance.setSubMatricesExist(true);
        result=instance.isSubMatricesExist();
        assertEquals(expResult, result);
    }
    
        /**
     * Test of setSubMatricesExist method, of class CovarianceMatrixWithSubMatricesModel.
     * */
    @Test
    public void test_SetSubMatricesExist() {
        System.out.println("Testing CovarianceMatrixWithSubMatricesModel's setSubMatricesExist(boolean Existence)");
        boolean subMatricesExist = true;
        CovarianceMatrixWithSubMatricesModel instance = new CovarianceMatrixWithSubMatricesModel();
        instance.setSubMatricesExist(subMatricesExist);
        assertEquals(true,instance.isSubMatricesExist());
        subMatricesExist=false;
        instance.setSubMatricesExist(subMatricesExist);
        assertEquals(false,instance.isSubMatricesExist());
        
    }   
    
    /**
     * Test of getLambdasCovarianceMatrix method, of class CovarianceMatrixWithSubMatricesModel.
    */
    @Test
    public void test_GetLambdasCovarianceMatrix() {
        System.out.println("Testing CovarianceMatrixWithSubMatricesModel's getLambdasCovarianceMatrix()");
        CovarianceMatrixWithSubMatricesModel instance = new CovarianceMatrixWithSubMatricesModel();
        AbstractMatrixModel expResult = null;
        
        
        CovarianceMatrixWithSubMatricesModel result=(CovarianceMatrixWithSubMatricesModel)instance.getLambdasCovarianceMatrix();
        assertEquals(expResult, result);

        CovarianceMatrixWithSubMatricesModel set = new CovarianceMatrixWithSubMatricesModel();
        instance.setLambdasCovarianceMatrix(set);
        
        CovarianceMatrixWithSubMatricesModel result2=(CovarianceMatrixWithSubMatricesModel)instance.getLambdasCovarianceMatrix();
        
        if(result2.equals(result))
            fail("Lambas should not be null!");    
    }

        /**
     * Test of getTracerCovarianceMatrix method, of class CovarianceMatrixWithSubMatricesModel.
   */ 
    @Test
    public void test_GetTracerCovarianceMatrix() {
        System.out.println("Testing CovarianceMatrixWithSubMatricesModel's getTracerCovarianceMatrix()");
        CovarianceMatrixWithSubMatricesModel instance = new CovarianceMatrixWithSubMatricesModel();
        AbstractMatrixModel expResult = null;
        
        
        CovarianceMatrixWithSubMatricesModel result=(CovarianceMatrixWithSubMatricesModel)instance.getTracerCovarianceMatrix();
        assertEquals(expResult, result);

        CovarianceMatrixWithSubMatricesModel set = new CovarianceMatrixWithSubMatricesModel();
        instance.setTracerCovarianceMatrix(set);
        
        CovarianceMatrixWithSubMatricesModel result2=(CovarianceMatrixWithSubMatricesModel)instance.getTracerCovarianceMatrix();
        
        if(result2.equals(result))
            fail("Tracer should not be null!"); 
    }

        /**
     * Test of getAnalyticalCovarianceMatrix method, of class CovarianceMatrixWithSubMatricesModel.
     */
    @Test
    public void test_GetAnalyticalCovarianceMatrix() {
        System.out.println("Testing CovarianceMatrixWithSubMatricesModel's getAnalyticalCovarianceMatrix()");
        CovarianceMatrixWithSubMatricesModel instance = new CovarianceMatrixWithSubMatricesModel();
        AbstractMatrixModel expResult = null;
        
        
        CovarianceMatrixWithSubMatricesModel result=(CovarianceMatrixWithSubMatricesModel)instance.getAnalyticalCovarianceMatrix();
        assertEquals(expResult, result);

        CovarianceMatrixWithSubMatricesModel set = new CovarianceMatrixWithSubMatricesModel();
        instance.setAnalyticalCovarianceMatrix(set);
        
        CovarianceMatrixWithSubMatricesModel result2=(CovarianceMatrixWithSubMatricesModel)instance.getAnalyticalCovarianceMatrix();
        
        if(result2.equals(result))
            fail("Analytical should not be null!"); 
    }
    
        /**
     * Test of setLambdasCovarianceMatrix method, of class CovarianceMatrixWithSubMatricesModel.
    */
    @Test
    public void test_SetLambdasCovarianceMatrix() {
        System.out.println("Testing CovarianceMatrixWithSubMatricesModel's setLambdasCovarianceMatrix(CovarianceMatrixWithSubMatricesModel lambda)");
        CovarianceMatrixWithSubMatricesModel instance = new CovarianceMatrixWithSubMatricesModel();
        AbstractMatrixModel expResult = null;
        
        CovarianceMatrixWithSubMatricesModel result=(CovarianceMatrixWithSubMatricesModel)instance.getLambdasCovarianceMatrix();
        assertEquals(expResult, result);

        CovarianceMatrixWithSubMatricesModel set = new CovarianceMatrixWithSubMatricesModel();
        instance.setLambdasCovarianceMatrix(set);
        
        CovarianceMatrixWithSubMatricesModel result2=(CovarianceMatrixWithSubMatricesModel)instance.getLambdasCovarianceMatrix();
        
        if(result2.equals(result))
            fail("Lambas should not be null!");

        instance.setLambdasCovarianceMatrix(null);
        result=(CovarianceMatrixWithSubMatricesModel)instance.getLambdasCovarianceMatrix();
        assertEquals(expResult, result);
    
    }
    
        /**
     * Test of setTracerCovarianceMatrix method, of class CovarianceMatrixWithSubMatricesModel.
    */
    @Test
    public void test_SetTracerCovarianceMatrix() {
        System.out.println("Testing CovarianceMatrixWithSubMatricesModel's setTracerCovarianceMatrix(CovarianceMatrixWithSubMatricesModel tracer)");
        CovarianceMatrixWithSubMatricesModel instance = new CovarianceMatrixWithSubMatricesModel();
        AbstractMatrixModel expResult = null;
        
        CovarianceMatrixWithSubMatricesModel result=(CovarianceMatrixWithSubMatricesModel)instance.getTracerCovarianceMatrix();
        assertEquals(expResult, result);

        CovarianceMatrixWithSubMatricesModel set = new CovarianceMatrixWithSubMatricesModel();
        instance.setTracerCovarianceMatrix(set);
        
        CovarianceMatrixWithSubMatricesModel result2=(CovarianceMatrixWithSubMatricesModel)instance.getTracerCovarianceMatrix();
        
        if(result2.equals(result))
            fail("Lambas should not be null!");

        instance.setTracerCovarianceMatrix(null);
        result=(CovarianceMatrixWithSubMatricesModel)instance.getTracerCovarianceMatrix();
        assertEquals(expResult, result);
        
        
    }
    
        /**
     * Test of setAnalyticalCovarianceMatrix method, of class CovarianceMatrixWithSubMatricesModel.
     * */
    @Test
    public void test_SetAnalyticalCovarianceMatrix() {
        System.out.println("Testing CovarianceMatrixWithSubMatricesModel's setAnalyticalCovarianceMatrix(CovarianceMatrixWithSubMatricesModel analytical)");
        CovarianceMatrixWithSubMatricesModel instance = new CovarianceMatrixWithSubMatricesModel();
        AbstractMatrixModel expResult = null;
        
        
        CovarianceMatrixWithSubMatricesModel result=(CovarianceMatrixWithSubMatricesModel)instance.getAnalyticalCovarianceMatrix();
        assertEquals(expResult, result);

        CovarianceMatrixWithSubMatricesModel set = new CovarianceMatrixWithSubMatricesModel();
        instance.setAnalyticalCovarianceMatrix(set);
        
        CovarianceMatrixWithSubMatricesModel result2=(CovarianceMatrixWithSubMatricesModel)instance.getAnalyticalCovarianceMatrix();
        
        if(result2.equals(result))
            fail("Lambas should not be null!");

        instance.setAnalyticalCovarianceMatrix(null);
        result=(CovarianceMatrixWithSubMatricesModel)instance.getAnalyticalCovarianceMatrix();
        assertEquals(expResult, result);
    }

    /**
     * Integration Test of class CovarienceMatrixWithSubMatricesModel
     * Testing the initializations of Matrices
     */
    @Test
    public void testMatrixInit () {
        CovarianceMatrixWithSubMatricesModel myMatrix = new CovarianceMatrixWithSubMatricesModel();

        String[] rowNames = new String[]{"first", "second", "third", "fourth", "fifth"};
        myMatrix.setRows( rowNames );
        myMatrix.setCols( myMatrix.getRows() );

        ConcurrentMap<String, BigDecimal> varianceTerms = new ConcurrentHashMap<>();
        varianceTerms.put( "third", new BigDecimal( 1 ) );
        varianceTerms.put( "fourth", new BigDecimal( 2 ) );
        varianceTerms.put( "fifth", new BigDecimal( 3 ) );

        Map<String, BigDecimal> coVariances = new HashMap<>();
        coVariances.put( "covThird__fourth", new BigDecimal( 9 ) );

    }    
    
    ////////////////////////
    ////Unfinished Tests////
    //////////////////////// 
    
    /*
     *
     *
     *CovarianceMatrixWithSubMatricesModel.setCovarianceCells(String covarianceName,double coVariance)
     *  This method has some comments in it that may or may not mean that
     *      functionality is not correct at the moment.
     *  These methods will simply involve too much time and effort to be
     *      implemented at this point in time, as it involves a lot of digging.
     *      I do not have enough time alotted to complete these at this time. 
     *
     *
     *CovarianceMatrixWithSubMatricesModel.recalculateSubCovariances(String[] covaryingTerms, BigDecimal oneSigma, boolean lockVariancesTogether)
     *CovarianceMatrixWithSubMatricesModel.initializeMatrixModelWithVariances(Map<String, BigDecimal> variances)
     *  These methods will simply involve too much time and effort to be
     *      implemented at this point in time, as it involves a lot of digging.
     *      I do not have enough time alotted to complete these at this time.     *
     */


    
    
    
        

 }