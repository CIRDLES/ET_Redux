/*
 * AbstractmatrixModel_Test_04152014Test.java
 *
 * The naming of this file is the name of the .java file being tested, combined
 * with test since this is the test file, combined with the creation date of
 * this file. The test at the end is so that the file runs from NetBeans when
 * testing an entire project.
 *
 * Created on April 15, 2014.
 *
 *Version History:
 *April 15 2014: File Created.
 *April 25 2014: Constructors and method tests done.
 *
 *
 *Developed for the CIRDLES lab at the College of Charleston by Patrick Brewer.
 *
 *
 */
package org.earthtime.matrices.matrixModels;

import Jama.Matrix;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author patrickbrewer
 */
public class AbstractMatrixModelTest {

    
    
    
    
    /////////////////////////
    ////Constructor Tests////
    /////////////////////////  
    

    
     /**
     * Test of AbstractMatrixModel() method, of class AbstractMatrixModel.
     */
    @Test
    public void test_constructor_0(){
	System.out.println("Testing AbstractmatrixModel's AbstractMatrixModel(String levelName)");
        //Tests if values are correct
        MatrixTester hi=new MatrixTester("why");
        String result=hi.levelName;
        Map<Integer,String> ro=hi.rows;
        Map<String,Integer> cp=hi.cols;
        Matrix ma=hi.matrix;
         
        assertEquals("why",result);
        assertEquals("{}",ro.toString());
        assertEquals("{}",cp.toString());
        assertEquals(null,ma);
        }
        
    
    
    ////////////////////
    ////Method Tests////
    ////////////////////       
    
    
    
    
    /**
     * Test of copyValuesFrom method, of class AbstractMatrixModel.
     */
    @Test
    public void test_CopyValuesFrom() {
        System.out.println("Testing AbstractmatrixModel's copyValuesFrom(AbstractMatrixModel parent)");
        MatrixTester hello=new MatrixTester("why");
        MatrixTester hi=new MatrixTester("whynumber2");
                
        hello.initializeMatrix();
        Matrix ma=new Matrix(2,2);
        hello.setMatrix(ma);
        
        Map<String,Integer> b4=hi.cols;
        Map<Integer,String> before=hi.rows;

        
        
        hello.cols.put("one thing", 1);
        hello.rows.put(2, "two things");
        
        hi.copyValuesFrom(hello);
        
        Map<String,Integer> af=hi.cols;
        Map<Integer,String> afta=hi.rows;        
        Matrix ma2=hi.getMatrix();
        
        if(ma2 == null)
            fail("Matrix should not be null");
        
        if(b4.equals(af))
            fail("Columns shouldn't be the same before and after");
    
        if(before.equals(afta))
            fail("Rows shouldn't be the same before and after");        

    }

    /**
     * Test of isCovMatrixSymmetricAndPositiveDefinite method, of class AbstractMatrixModel.
     */
    @Test
    public void test_IsCovMatrixSymmetricAndPositiveDefinite() {
        System.out.println("Testing AbstractmatrixModel's isCovMatrixSymmetricAndPositiveDefinite()");
        MatrixTester instance=new MatrixTester("why");
        
        //asymmetric and negative should give false
        boolean expResult = false;
        boolean result = instance.isCovMatrixSymmetricAndPositiveDefinite();
        assertEquals(expResult, result);

        
        //symmetric and positive should give true
        instance.initializeMatrix();
        Matrix ma=new Matrix(1,1);
        instance.setMatrix(ma);
        
        instance.cols.put("one thing", 1);
        instance.cols.put("two thing", 2);
        instance.rows.put(1, "one thing");
        instance.rows.put(2, "two thing");
        
        instance.setValueAt(0, 0, 1);
        
        expResult = true;
        result = instance.isCovMatrixSymmetricAndPositiveDefinite();
        assertEquals(expResult, result);       
        
        
        //symmetric and negative should give false
        
        instance.setValueAt(0,0,-1432432523.3);
        
        result = instance.isCovMatrixSymmetricAndPositiveDefinite();
        
        expResult=false;
        assertEquals(expResult, result);       

       //asymmetric and positive should give false

        instance.initializeMatrix();
        ma=new Matrix(2,2);
        instance.setMatrix(ma);
        
        instance.cols.put("one thing", 1);
        instance.cols.put("two thing", 2);
        instance.rows.put(1, "one thing");
        instance.rows.put(2, "two thing");       
        
        
        instance.setValueAt(0,0,2);
        result = instance.isCovMatrixSymmetricAndPositiveDefinite();
        
        assertEquals(expResult, result);       
    }
    
    /**
     * Test of ToStringWithLabels method, of class AbstractMatrixModel.
     */
    @Test
    public void test_ToStringWithLabels() {
        System.out.println("Testing AbstractmatrixModel's ToStringWithLabels()");
        
        //blank one
        MatrixTester instance=new MatrixTester("why");
        String expResult = "MATRIX#=why            \n";
        String result = instance.ToStringWithLabels();
                
        assertEquals(expResult, result);
        
        //columns and values
        instance.initializeMatrix();
        Matrix ma=new Matrix(1,1);
        instance.setMatrix(ma);
        
        instance.cols.put("one thing", 1);
        instance.cols.put("two thing", 2);
        instance.rows.put(1, "one thing");
        instance.rows.put(2, "two thing");
        instance.setValueAt(0, 0, 1);
        
        expResult="MATRIX#=why            null                   one thing              \n" +
                    "null                   1.000000000E00         \n" +
                       "one thing              \n";
        result=instance.ToStringWithLabels();
        assertEquals(expResult,result);
        
    }

    /**
     * Test of getRows method, of class AbstractMatrixModel.
     */
    @Test
    public void test_GetRows() {
        System.out.println("Testing AbstractmatrixModel's getRows()");
        MatrixTester instance=new MatrixTester("why");
        
        //blank
        String expResult = "{}";
        
        Map<Integer, String> result = instance.getRows();

        assertEquals(expResult, result.toString());
        
        //specified
        expResult = "{1=one thing}";
        
        instance.rows.put(1, "one thing");

        result = instance.getRows();

        assertEquals(expResult, result.toString());

    }
    
    /**
     * Test of getCols method, of class AbstractMatrixModel.
     */
    @Test
    public void test_GetCols() {
        System.out.println("Testing AbstractmatrixModel's getCols()");
        
        MatrixTester instance=new MatrixTester("why");
        
        //blank
        String expResult = "{}";
        
        Map<String, Integer> result = instance.getCols();

        assertEquals(expResult, result.toString());
        
        //specified
        expResult = "{one thing=1}";
        
        instance.cols.put("one thing", 1);

        result = instance.getCols();

        assertEquals(expResult, result.toString());

    }

    /**
     * Test of setLevelName method, of class AbstractMatrixModel.
     */
    @Test
    public void test_SetLevelName() {
        System.out.println("Testing AbstractmatrixModel's setLevelName(String levelName)");
        String levelName = "whyhellothere";
        MatrixTester instance=new MatrixTester("why");
        instance.setLevelName(levelName);
        String result=instance.levelName;
        assertEquals(levelName,result);

    }

    /**
     * Test of getMatrix method, of class AbstractMatrixModel.
     */
    @Test
    public void test_GetMatrix() {
        System.out.println("Testing AbstractmatrixModel's getMatrix()");
        MatrixTester instance=new MatrixTester("why");
        
       //default is 0
        Matrix expResult0=instance.getMatrix();

        assertEquals(null,expResult0);
        
        instance.initializeMatrix();
        Matrix ma=new Matrix(1,1);
        instance.setMatrix(ma);        
        
        Matrix result = instance.getMatrix();
        
        //matrix dimensions set        
        assertEquals(1, result.getColumnDimension());
        
    }
    
    /**
     * Test of getLevelName method, of class AbstractMatrixModel.
     */
    @Test
    public void test_GetLevelName() {
        System.out.println("Testing AbstractmatrixModel's getLevelName()");
        MatrixTester instance=new MatrixTester("why");
        String expResult = "why";
        String result = instance.getLevelName();
        assertEquals(expResult, result);
        instance.levelName="hello there";
        result=instance.getLevelName();
        assertEquals("hello there",result);
    }

    /**
     * Test of set_Rows method, of class AbstractMatrixModel.
     */
    @Test
    public void testSetRows_StringArr() {
        System.out.println("Testing AbstractmatrixModel's setRows(String[] Array)");
        String[] rowNames = new String[2];
        MatrixTester instance=new MatrixTester("why");
        rowNames[0]="hello";
        rowNames[1]="there";
        
        instance.setRows(rowNames);
        
        assertEquals("{0=hello, 1=there}",instance.getRows().toString());
    }

    /**
     * Test of setRows method, of class AbstractMatrixModel.
     */
    @Test
    public void test_SetRows_Map() {
        System.out.println("Testing AbstractmatrixModel's setRows(Map<Integer,String> myRows)");
        Map<Integer, String> myRows = new HashMap<>();
        MatrixTester instance=new MatrixTester("why");
        
        myRows.put(0, "zero");
        myRows.put(1, "one");
        
        instance.setRows(myRows);

        assertEquals("{0=zero, 1=one}",instance.getRows().toString());
        
    }
    
    /**
     * Test of setCols method, of class AbstractMatrixModel.
     */
    @Test
    public void test_SetCols() {
        System.out.println("Testing AbstractmatrixModel's setCols(Map<Integer,String> myCols)");
        Map<Integer, String> cols = new HashMap<>();
        MatrixTester instance=new MatrixTester("why");
        
        cols.put(0, "zero");
        cols.put(1, "one");
        
        instance.setCols(cols);
        
        assertEquals("{zero=0, one=1}",instance.getCols().toString());
        
    }
    
    /**
     * Test of invertRowMap method, of class AbstractMatrixModel.
     */
    @Test
    public void test_InvertRowMap() {
        System.out.println("Testing AbstractmatrixModel's invertRowMap(Map<Integer,String> rowMap)");
        Map<Integer, String> rowMap = new HashMap<>();
        
        Map<String, Integer> result = AbstractMatrixModel.invertRowMap(rowMap);

        assertEquals("{}", result.toString());

        rowMap.put(0, "zero");
        rowMap.put(1,"one");
        
        assertEquals("{0=zero, 1=one}",rowMap.toString());
        
        result = AbstractMatrixModel.invertRowMap(rowMap);
        
        assertEquals("{zero=0, one=1}",result.toString());
    }

    /**
     * Test of invertColMap method, of class AbstractMatrixModel.
     */
    @Test
    public void test_InvertColMap() {
        System.out.println("Testing AbstractmatrixModel's invertColMap(Map<String,Integer> colMap)");
        Map<String, Integer> colMap = new HashMap<>();

        Map<Integer, String> result = AbstractMatrixModel.invertColMap(colMap);
        
        assertEquals("{}", result.toString());

        colMap.put("zero", 0);
        colMap.put("one", 1);
        
        assertEquals("{zero=0, one=1}",colMap.toString());

        result=AbstractMatrixModel.invertColMap(colMap);
        
        assertEquals("{0=zero, 1=one}",result.toString());

    }
    
    /**
     * Test of copyCols method, of class AbstractMatrixModel.
    */
    @Test
    public void test_CopyCols() {
        System.out.println("Testing AbstractmatrixModel's copyCols(Map<String,Integer> colMap)");
        Map<String, Integer> colMap = new HashMap<>();
        MatrixTester instance=new MatrixTester("why");
        
        colMap.put("hello", 257);
        
        assertEquals("{}",instance.getCols().toString());
        
        instance.copyCols(colMap);

        assertEquals("{hello=257}",instance.getCols().toString());
    } 

    /**
     * Test of setMatrix method, of class AbstractMatrixModel.
     */
    @Test
    public void test_SetMatrix() {
        System.out.println("Testing AbstractmatrixModel's setMatrix(Matrix newMatrix)");
        Matrix matrix = new Matrix(257,513);
        MatrixTester instance=new MatrixTester("why");
        matrix.set(81, 81, 1337);
        
        instance.setMatrix(matrix);
        
        assertEquals(257,instance.getMatrix().getRowDimension());
        assertEquals(513,instance.getMatrix().getColumnDimension());
        
        assertEquals(1337,(int)instance.getMatrix().get(81, 81));

    }
    
    /**
     * Test of sumOfRowsMatrix method, of class AbstractMatrixModel.
     */
    @Test
    public void test_SumOfRowsMatrix() {
        System.out.println("Testing AbstractmatrixModel's sumOfRowsMatrix()");
        MatrixTester instance=new MatrixTester("why");
        Matrix matrix = new Matrix(257,513);
        matrix.set(81, 81, 1337);
        instance.setMatrix(matrix);
        
        double[] expResult = new double[1];
        expResult[0]=1337.0;
        
        double[] result = instance.sumOfRowsMatrix();
  
        assertEquals((int)expResult[0], (int)result[81]);

    }
    
    /**
     * Test of createPartialDerivName method, of class AbstractMatrixModel.
     */
    @Test
    public void test_CreatePartialDerivName() {
        System.out.println("Testing AbstractmatrixModel's createPartialDerivName(String variableName)");
        String variableName = "whatInThe";
        MatrixTester instance=new MatrixTester("why");
        String expResult = "dWhatInThe";
        String result = instance.createPartialDerivName(variableName);
        assertEquals(expResult, result);

    }    

    /**
     * Test of initializeMatrix method, of class AbstractMatrixModel.
     */
    @Test
    public void test_InitializeMatrix() {
        System.out.println("Testing AbstractmatrixModel's initializeMatrix()");
        MatrixTester instance=new MatrixTester("why");
        
        
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
     * Test of initializeMatrixModelFromMatrixModel method, of class AbstractMatrixModel.
     */
    @Test
    public void test_InitializeMatrixModelFromMatrixModel() {
        System.out.println("Testing AbstractmatrixModel's initializeMatrixModelFromMatrixModel(AbstractMatrixModel parent)");
        
        
        MatrixTester instance0=new MatrixTester("helloThere");
        
        MatrixTester instance=new MatrixTester("why");
        
        
        Map<Integer,String> hell=new HashMap<>();
        hell.put(1, "one");
        Map<Integer,String> hello=new HashMap<>();
        hello.put(2, "two");
        
        instance.setCols(hell);
        instance.setRows(hello);
        
        Matrix before=instance.getMatrix();
        
        instance.initializeMatrixModelFromMatrixModel(instance0);

        Matrix after=instance.getMatrix();
   
        if(before==after)
            fail("Shouldn't be the same before and after, matrix should be initialized");

        
    }
    
    
    
}
