/*
 * CorrelationMatrixModel_Test_05012014Test.java
 *
 * The naming of this file is the name of the .java file being tested, combined
 * with test since this is the test file, combined with the creation date of
 * this file. The test at the end is so that the file runs from NetBeans when
 * testing an entire project.
 *
 * Created on May 1, 2014.
 *
 *Version History:
 *May 1 2014: File Created. Constructor and method tests done.
 *
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;


/**
 *
 * @author patrickbrewer
 */
public class CorrelationMatrixModelTest {
    
    
    
    /////////////////////////
    ////Constructor Tests////
    /////////////////////////  
    
    
     /**
     * Test of CorrelationMatrixModel() method, of class CorrelationMatrixModel.
     */
    @Test
    public void test_constructor_0(){
	System.out.println("Testing CorrelationMatrixModel's CorrelationMatrixModel()");
        //Tests if values are correct
        CorrelationMatrixModel hi=new CorrelationMatrixModel();
        String result=hi.levelName;
        Map<Integer,String> ro=hi.rows;
        Map<String,Integer> cp=hi.cols;
        Matrix ma=hi.matrix;
         
        assertEquals("Correlations",result);
        assertEquals("{}",ro.toString());
        assertEquals("{}",cp.toString());
        assertEquals(null,ma);
        }        
    
    ////////////////////
    ////Method Tests////
    ////////////////////      
    
    
    /**
     * Test of copy method, of class CorrelationMatrixModel.
     */
    @Test
    public void test_Copy() {
        System.out.println("Testing CorrelationMatrixModel's copy(CorrelationMatrixModel parent)");
        CorrelationMatrixModel instance = new CorrelationMatrixModel();
        
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
     * Test of initializeMatrix method, of class CorrelationMatrixModel.
    */
    @Test
    public void test_InitializeMatrix() {
        System.out.println("Testing CorrelationMatrixModel's initializeMatrix()");
        CorrelationMatrixModel instance = new CorrelationMatrixModel();
        
        Map<Integer,String> hell=new HashMap<>();
        hell.put(1, "one");
        Map<Integer,String> hello=new HashMap<>();
        hello.put(2, "two");
        
        instance.setCols(hell);
        instance.setRows(hello);
        
        
        Matrix before=instance.getMatrix();
        instance.initializeMatrix();
        Matrix after=instance.getMatrix();
       
        if(before==after)
            fail("Shouldn't be the same before and after, matrix should be initialized");
        

    }

    /**
     * Test of initializeCorrelations method, of class CorrelationMatrixModel.
      */
    
    @Test
    public void test_InitializeCorrelations() {
        System.out.println("Testing CorrelationMatrixModel's initializeCorrelations(Map<String,BigDecimal> correlations");
     
        Map<String, BigDecimal> correlations = new HashMap<>();
        
        correlations.put("corr1__bull2", new BigDecimal("1.00000"));
        
        CorrelationMatrixModel instance = new CorrelationMatrixModel();

        //false if both empty
        boolean result = instance.initializeCorrelations(correlations);
        assertEquals(result,false);
        assertEquals(null,instance.getMatrix());
        
        //false if columns empty
        Map<Integer,String> colz=new HashMap<>();
        colz.put(1, "r1");
        colz.put(2, "bull2");
        colz.put(3, "words");
        colz.put(4, "exist");

        instance.setRows(colz);
        result = instance.initializeCorrelations(correlations);
        assertEquals(result,false);
        assertEquals(null,instance.getMatrix());        
        
        //false if rows empty
        instance=new CorrelationMatrixModel();
        instance.setCols(colz);
        result = instance.initializeCorrelations(correlations);
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
       
        result = instance.initializeCorrelations(correlations);

        assertEquals(true,result);
        assertEquals(1,(int)instance.getMatrix().get(1,2));
        
        //if cols and rows but not with left and right

        roz=new HashMap<>();
        colz=new HashMap<>();
        instance=new CorrelationMatrixModel();
        
        roz.put(666, "lol");
        colz.put(51, "hail!");
        
        instance.setCols(colz);
        instance.setRows(roz);
        
        result = instance.initializeCorrelations(correlations);

        assertEquals(true,result);
        assertEquals(null,instance.getMatrix());
        
        //if cols and rows but not with left
        colz.put(1, "r1");
        instance.setCols(colz);
        
        result = instance.initializeCorrelations(correlations);

        assertEquals(true,result);
        assertEquals(null,instance.getMatrix());    
        
        //if cols and worws but not with right
        colz=new HashMap<>();
        colz.put(2, "bull2");
                
        instance.setCols(colz);
        
        result = instance.initializeCorrelations(correlations);

        assertEquals(true,result);
        assertEquals(null,instance.getMatrix());        
        
    }

    /**
     * Test of getCorrelationCell(string) method, of class CorrelationMatrixModel.
      */
    @Test
    public void test_GetCorrelationCell_String() {
        System.out.println("Testing CorrelationMatrixModel's getCorrelationCell(String CorrelationName))");
        String correlationName = "corr1__ave";
        CorrelationMatrixModel instance = new CorrelationMatrixModel();
       
        //with incorrect left and right
        double result = instance.getCorrelationCell(correlationName);

        assertEquals(0,(int)result);        

        //with correct parameters
        Matrix ma=new Matrix(2,2);
        ma.set(1, 0, 51);
        
        instance.setMatrix(ma);
        
        Map<Integer,String> colz=new HashMap<>();
        colz.put(1, "r1");
        colz.put(0, "ave");
        
        instance.setCols(colz);
        
        result = instance.getCorrelationCell(correlationName);
        
        assertEquals(51,(int)result);

    }

    /**
     * Test of getCorrelationCell(string,string) method, of class CorrelationMatrixModel.
     */ 
    @Test
    public void test_GetCorrelationCell_String_String() {
        System.out.println("Testing CorrelationMatrixModel's getCorrelationCell(String leftSide,String rightSide)");
        String leftSide = "ave";
        String rightSide = "hail!";
        CorrelationMatrixModel instance = new CorrelationMatrixModel();

        //with incorrect left and right
        double result = instance.getCorrelationCell(leftSide, rightSide);
        assertEquals(0,(int)result);        

        //with correct parameters
        Matrix ma=new Matrix(2,2);
        ma.set(1, 0, 51);
        
        instance.setMatrix(ma);
        
        Map<Integer,String> colz=new HashMap<>();
        colz.put(1, "ave");
        colz.put(0, "hail!");
        
        instance.setCols(colz);
        
        result = instance.getCorrelationCell(leftSide, rightSide);
        
        assertEquals(51,(int)result);
    }

    /**
     * Test of setCorrelationCells(string,double) method, of class CorrelationMatrixModel.
     */
    @Test
    public void test_SetCorrelationCells_String_double() {
        System.out.println("Testing CorrelationMatrixModel's setCorrelationCells(String CorrelationName,double value)");
        String correlationName = "corr1_ave";
        CorrelationMatrixModel instance = new CorrelationMatrixModel();
        
        //If doesn't work, error out
        try{
        instance.setCorrelationCells(correlationName, 51.0);
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
        
        instance.setCorrelationCells(correlationName, 51.0);
        
        assertEquals(51,(int)instance.getMatrix().get(1, 0));
    }

    /**
     * Test of setCorrelationCell method, of class CorrelationMatrixModel.
     */
    @Test
    public void test_SetCorrelationCell() {
        System.out.println("Testing CorrelationMatrixModel's setCorrelationCell(String leftSide,String rightSide,double value)");
        String leftSide = "corr1";
        String rightSide = "ave";

        CorrelationMatrixModel instance = new CorrelationMatrixModel();
        
        //false if stuff doesn't exist
        boolean result = instance.setCorrelationCell(leftSide, rightSide, 51.0);
        
        assertEquals(false, result);

        //true if stuff does exist
        
        Matrix ma=new Matrix(2,2);
        
        instance.setMatrix(ma);
        
        Map<Integer,String> colz=new HashMap<>();
        colz.put(1, "corr1");
        colz.put(0, "ave");
        
        instance.setCols(colz);     
    
        result = instance.setCorrelationCell(leftSide, rightSide, 51.0);
        
        assertEquals(true, result);    
    
        assertEquals(51,(int)instance.getMatrix().get(1, 0));
    
    }
    
    /**
     * Test of setCorrelationCells(string,string,double) method, of class CorrelationMatrixModel.
    */
    @Test
    public void test_SetCorrelationCells_3args() {
        System.out.println("Testing CorrelationMatrixModel's setCorrelationCells(String leftSide,String rightSide,double value)");
        String leftSide = "corr1";
        String rightSide = "ave";

        //should be null if stuff doesn't exist...
        CorrelationMatrixModel instance = new CorrelationMatrixModel();
        
        Matrix ma=new Matrix(2,2);
        
        instance.setMatrix(ma);        
        
        instance.setCorrelationCells(leftSide, rightSide, 51.0);
        assertEquals(0,(int)instance.getMatrix().get(1, 0));

        //lets make this work now eh?        
        Map<Integer,String> colz=new HashMap<>();
        colz.put(1, "corr1");
        colz.put(0, "ave");
        
        instance.setCols(colz);             
      
        instance.setCorrelationCells(leftSide, rightSide, 51.0);
        assertEquals(51,(int)instance.getMatrix().get(1, 0));        
        
    }
    
    /**
     * Test of setValueAt method, of class CorrelationMatrixModel.
    */
    @Test
    public void test_SetValueAt() {
        System.out.println("Testing CorrelationMatrixModel's setValueAt(int row,int column,double value)");
        CorrelationMatrixModel instance = new CorrelationMatrixModel();

        Matrix ma=new Matrix(2,2);
        
        instance.setMatrix(ma); 
        
        //fail if not between -1 and 1
        
        instance.setValueAt(0, 0, 51);
        if(51==instance.getMatrix().get(0, 0))
            fail("Should not have worked...");
        
        instance.setValueAt(0, 0, -51);
        if(-51==instance.getMatrix().get(0, 0))
            fail("Should not have worked...");        

        //work if between -1 and 1
        instance.setValueAt(0, 0, 1);

        assertEquals(1,(int)instance.getMatrix().get(0, 0));

        instance.setValueAt(0, 0, -1);

        assertEquals(-1,(int)instance.getMatrix().get(0, 0));
        
        instance.setValueAt(0, 0, 0);

        assertEquals(0,(int)instance.getMatrix().get(0, 0));        
    }    
    
     
    
}
