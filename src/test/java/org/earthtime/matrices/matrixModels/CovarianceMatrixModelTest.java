/*
 * CovarianceMatrixModel_Test_05012014Test.java
 *
 * The naming of this file is the name of the .java file being tested, combined
 * with test since this is the test file, combined with the creation date of
 * this file. The test at the end is so that the file runs from NetBeans when
 * testing an entire project.
 *
 * Created on May 1, 2014.
 *
 *Version History:
 *May 1 2014: File Created. Constructor and easy method tests finished. Some
 *            unfinished tests exist at the bottom of the file.
 *
 *Developed for the CIRDLES lab at the College of Charleston by Patrick Brewer.
 *
 *
 */
package org.earthtime.matrices.matrixModels;

import java.math.BigDecimal;
import java.util.Map;
import Jama.Matrix;
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
public class CovarianceMatrixModelTest {
    
    
    
    /////////////////////////
    ////Constructor Tests////
    /////////////////////////      
    
    
    
     /**
     * Test of CovarianceMatrixModel() method, of class CovarianceMatrixModel.
     */
    @Test
    public void test_constructor_0(){
	System.out.println("Testing CovarianceMatrixModel's CovarianceMatrixModel()");
        //Tests if values are correct
        CovarianceMatrixModel hi=new CovarianceMatrixModel();
        String result=hi.levelName;
        Map<Integer,String> ro=hi.rows;
        Map<String,Integer> cp=hi.cols;
        Matrix ma=hi.matrix;
         
        assertEquals("Covariances",result);
        assertEquals("{}",ro.toString());
        assertEquals("{}",cp.toString());
        assertEquals(null,ma);
        }            
    
    
    
    ////////////////////
    ////Method Tests////
    ////////////////////  
    
    
    
    /**
     * Test of copy method, of class CovarianceMatrixModel.
     */
    @Test
    public void test_Copy() {
        System.out.println("Testing CovarianceMatrixModel's copy(CovarianceMatrixModel parent)");
        CovarianceMatrixModel instance = new CovarianceMatrixModel();
        
        Matrix ma=new Matrix(1,1);
        ma.set(0, 0, 51);
        
        instance.setMatrix(ma);
        
        Map<Integer,String> co=new HashMap();
        Map<Integer,String> ro=new HashMap();
        co.put(1, "one");
        ro.put(2, "two");
        
        instance.setCols(co);
        instance.setRows(ro);
        
        AbstractMatrixModel result = instance.copy();

        assertEquals("{one=1}",result.cols.toString() );
        assertEquals("{2=two}",result.rows.toString() );
        assertEquals(51,(int)result.getMatrix().get(0, 0) );
        
    }

    /**
     * Test of setValueAt method, of class CovarianceMatrixModel.
     */
    @Test
    public void test_SetValueAt() {
        System.out.println("Testing CovarianceMatrixModel's setValueAt(int row,int column, double value)");

        CovarianceMatrixModel instance = new CovarianceMatrixModel();
        Matrix ma=new Matrix(2,2);
        
        instance.setMatrix(ma); 
        
        instance.setValueAt(0, 0, 51);

        assertEquals(51,(int)instance.getMatrix().get(0, 0));        
    }   
    
    /**
     * Test of getCovarianceCell method, of class CovarianceMatrixModel.
     */
    @Test
    public void test_GetCovarianceCell() {
        System.out.println("Testing CovarianceMatrixModel's getCovarianceCell(String leftSide, String rightside)");
        
        
        String leftSide = "ave";
        String rightSide = "hail!";
        CovarianceMatrixModel instance = new CovarianceMatrixModel();

        //with incorrect left and right
        double result = instance.getCovarianceCell(leftSide, rightSide);
        assertEquals(0,(int)result);        

        //with correct parameters
        Matrix ma=new Matrix(2,2);
        ma.set(1, 0, 51);
        
        instance.setMatrix(ma);
        
        Map<Integer,String> colz=new HashMap<>();
        colz.put(1, "ave");
        colz.put(0, "hail!");
        
        instance.setCols(colz);
        
        result = instance.getCovarianceCell(leftSide, rightSide);
        
        assertEquals(51,(int)result);
    }
    
    /**
     * Test of setCovarianceCells(string,string,double) method, of class CovarianceMatrixModel.
     */
    @Test
    public void testSetCovarianceCells_3args() {
        System.out.println("Testing CovarianceMatrixModel's setCovarianceCells(String leftSide,String rightSide ,double value)");
        String leftSide = "corr1";
        String rightSide = "ave";

        //should be null if stuff doesn't exist...
        CovarianceMatrixModel instance = new CovarianceMatrixModel();
        
        Matrix ma=new Matrix(2,2);
        
        instance.setMatrix(ma);        
        
        instance.setCovarianceCells(leftSide, rightSide, 51.0);
        assertEquals(0,(int)instance.getMatrix().get(1, 0));

        //lets make this work now eh?        
        Map<Integer,String> colz=new HashMap<>();
        colz.put(1, "corr1");
        colz.put(0, "ave");
        
        instance.setCols(colz);             
      
        instance.setCovarianceCells(leftSide, rightSide, 51.0);
        assertEquals(51,(int)instance.getMatrix().get(1, 0));        
        
    }

    /**
     * Test of setCovarianceCell(string,string,double) method, of class CovarianceMatrixModel.
     * */
    @Test
    public void test_SetCovarianceCell() {
        System.out.println("Testing CovarianceMatrixModel's setCovarianceCell(String leftSide,String rightSide ,double value)");
        String leftSide = "corr1";
        String rightSide = "ave";

        CovarianceMatrixModel instance = new CovarianceMatrixModel();
        
        //false if stuff doesn't exist
        boolean result = instance.setCovarianceCell(leftSide, rightSide, 51.0);
        
        assertEquals(false, result);

        //true if stuff does exist
        
        Matrix ma=new Matrix(2,2);
        
        instance.setMatrix(ma);
        
        Map<Integer,String> colz=new HashMap<>();
        colz.put(1, "corr1");
        colz.put(0, "ave");
        
        instance.setCols(colz);     
    
        result = instance.setCovarianceCell(leftSide, rightSide, 51.0);
        
        assertEquals(true, result);    
    
        assertEquals(51,(int)instance.getMatrix().get(1, 0));
        
    }

    /**
     * Test of setCovarianceCells method, of class CovarianceMatrixModel.
     */ 
    @Test
    public void test_SetCovarianceCells_String_double() {
        System.out.println("Testing CovarianceMatrixModel's setCovarianceCells(String correlationName,double value)");
        String correlationName = "corr1_ave";
        CovarianceMatrixModel instance = new CovarianceMatrixModel();
        
        //If doesn't work, error out
        try{
        instance.setCovarianceCells(correlationName, 51.0);
        fail("Should not have gotten this far, an error should have been thrown!");
        }
        catch(Exception e)
        {
        }
       
        //working as intended
        correlationName = "corr1__ave";
        
        Matrix ma=new Matrix(2,2);
        
        instance.setMatrix(ma);
        
        Map<Integer,String> colz=new HashMap<>();
        colz.put(1, "r1");
        colz.put(0, "ave");
        
        instance.setCols(colz);        
        
        instance.setCovarianceCells(correlationName, 51.0);
        
        assertEquals(51,(int)instance.getMatrix().get(1, 0));
    }
    
    /**
     * Test of initializeCoVariances method, of class CovarianceMatrixModel.
     *  */ 
    @Test
    public void test_InitializeCoVariances() {
        System.out.println("Testing CovarianceMatrixModel's initializeCoVariances(Map<String,BigDecimal> correlations)");
        Map<String, BigDecimal> correlations = new HashMap<>();
        
        correlations.put("corr1__bull2", new BigDecimal("1.00000"));
        
        CovarianceMatrixModel instance = new CovarianceMatrixModel();

        //false if both empty
        boolean result = instance.initializeCoVariances(correlations);
        assertEquals(result,false);
        assertEquals(null,instance.getMatrix());
        
        //false if columns empty
        Map<Integer,String> colz=new HashMap<>();
        colz.put(1, "r1");
        colz.put(2, "bull2");
        colz.put(3, "words");
        colz.put(4, "exist");

        instance.setRows(colz);
        result = instance.initializeCoVariances(correlations);
        assertEquals(result,false);
        assertEquals(null,instance.getMatrix());        
        
        //false if rows empty
        instance=new CovarianceMatrixModel();
        instance.setCols(colz);
        result = instance.initializeCoVariances(correlations);
        assertEquals(result,false);
        Matrix b4=instance.getMatrix();
        assertEquals(null,b4);        

        //if true get the rho value from correlations and set a correlation to be equal to that value or double it????
        Map<Integer,String> roz=new HashMap<>();
        roz.put(3, "bull");
        roz.put(1, "shhhh");
        roz.put(2, "hell");

        instance.setRows(roz);

        instance.initializeMatrix();
       
        result = instance.initializeCoVariances(correlations);

        assertEquals(true,result);
        assertEquals(1,(int)instance.getMatrix().get(1,2));
        
        //if cols and rows but not with left and right

        roz=new HashMap<>();
        colz=new HashMap<>();
        instance=new CovarianceMatrixModel();
        
        roz.put(666, "lol");
        colz.put(51, "hail!");
        
        instance.setCols(colz);
        instance.setRows(roz);
        
        result = instance.initializeCoVariances(correlations);

        assertEquals(true,result);
        assertEquals(null,instance.getMatrix());
        
        //if cols and rows but not with left
        colz.put(1, "r1");
        instance.setCols(colz);
        
        result = instance.initializeCoVariances(correlations);

        assertEquals(true,result);
        assertEquals(null,instance.getMatrix());    
        
        //if cols and worws but not with right
        colz=new HashMap<>();
        colz.put(2, "bull2");
                
        instance.setCols(colz);
        
        result = instance.initializeCoVariances(correlations);

        assertEquals(true,result);
        assertEquals(null,instance.getMatrix());        
        
    }
    
    /**
     * Test of initializeMatrixModelWithVariances method, of class CovarianceMatrixModel.
    */
    @Test
    public void test_InitializeMatrixModelWithVariances() {
        System.out.println("Testing CovarianceMatrixModel's initializeMatrixModelWithVariances(Map<String,BigDecimal variances)");
        Map<String, BigDecimal> variances = new HashMap<>();
        variances.put("corr1__bull2", new BigDecimal("6.00000"));
        variances.put("bull2", new BigDecimal("6.00000"));
        
        CovarianceMatrixModel instance = new CovarianceMatrixModel();

        //should be false
        boolean result = instance.initializeMatrixModelWithVariances(variances);
        
        assertEquals(false, result);

        //should be true
        Map<Integer,String> roz=new HashMap<>();
        roz.put(0, "corr1");
        roz.put(0, "bull2");
        Map<Integer,String> colz=new HashMap<>();
        colz.put(0, "bull2");
        colz.put(0, "corr1");
        
        instance.setRows(roz);
        instance.setCols(colz);

        result = instance.initializeMatrixModelWithVariances(variances);
        
        assertEquals(true, result);        
        assertEquals(6,(int)instance.getMatrix().get(0, 0));

    }

     /**
     * Integration Test of class CovarienceMatrixModel
     * Testing the initializations of Matrices
     */
    @Test
    public void testMatrixInit () {
        AbstractMatrixModel myMatrix = new CovarianceMatrixModel();

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
     *CovarianceMatrixModel.checkValidityOfMeasuredRatioUncertainties(String fractionID)
     *CovarianceMatrixModel.recalculateSubCovariances(String[] covaryingTerms, BigDecimal oneSigma, boolean lockVariancesTogether)
     *  These methods will simply involve too much time and effort to be
     *      implemented at this point in time, as it involves a lot of digging.
     *      I do not have enough time alotted to complete these at this time.
     *
     *
     *
     *
     */    
    
}
