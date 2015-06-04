/*
 * JacobianMatrixModel_Test_04252014Test.java
 *
 * The naming of this file is the name of the .java file being tested, combined
 * with test since this is the test file, combined with the creation date of
 * this file. The test at the end is so that the file runs from NetBeans when
 * testing an entire project.
 *
 * Created on April 25, 2014.
 *
 *Version History:
 *April 25 2014: File Created. Some framework imported. Constructors done.
 *April 29 2014: Method tests completed.
 *
 *
 *Developed for the CIRDLES lab at the College of Charleston by Patrick Brewer.
 *
 *
 */
package org.earthtime.matrices.matrixModels;

import Jama.Matrix;
import java.util.HashMap;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author patrickbrewer
 */
public class JacobianMatrixModelTest {
    
    /////////////////////////
    ////Constructor Tests////
    /////////////////////////  
    
    
     /**
     * Test of JacobianMatrixModel(string) method, of class JacobianMatrixModel.
     */
    @Test
    public void test_constructor_0(){
	System.out.println("Testing JacobianMatrixModel's JacobianMatrixModel(String levelName)");
        //Tests if values are correct
        JacobianMatrixModel hi=new JacobianMatrixModel("why");
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
     * Test of copy method, of class JacobianMatrixModel.
     */
    @Test
    public void test_Copy() {
        System.out.println("Testing JacobianMatrixModel's copy(JacobianMatrixModel parent)");
        JacobianMatrixModel instance=new JacobianMatrixModel("why");
        
        Map<Integer,String> col=new HashMap<>();
        Map<Integer,String> ro=new HashMap<>();
        col.put(1, "one");
        ro.put(1, "two");
        instance.setCols(col);
        instance.setRows(ro);
        
        Matrix ma=new Matrix(1,1);
        ma.set(0, 0, 23);
        instance.setMatrix(ma);
        
        AbstractMatrixModel result = instance.copy();
    
        assertEquals(instance.cols, result.cols);
        assertEquals(instance.rows, result.rows);
        assertEquals((int)instance.matrix.get(0, 0), (int)result.matrix.get(0, 0));
        
    }

    /**
     * Test of setValueAt method, of class JacobianMatrixModel.
    */ 
    @Test
    public void test_SetValueAt() {
        System.out.println("Testing JacobianMatrixModel's setValueAt(int row,int col, double value)");
        JacobianMatrixModel instance=new JacobianMatrixModel("why");
        
        int row = 81;
        int col = 81;
        
        double value = 1337;
        
        Matrix ma=new Matrix(100,100);
        
        instance.setMatrix(ma);
        
        instance.setValueAt(row, col, value);

        double tempValue=instance.matrix.get(row, col);
        
        assertEquals((int)value,(int)tempValue);
               
    }

    /**
     * Test of initializeMatrixModelWithDerivedTerms method, of class JacobianMatrixModel.
     */
    @Test
    public void test_InitializeMatrixModelWithDerivedTerms() {
        System.out.println("Testing JacobianMatrixModel's initializeMatrixModelWithDerivedTerms(Map<String,BigDecimal> derivedTerms");
        
        Map<String, BigDecimal> derivedTerms = new HashMap<>();
        
        JacobianMatrixModel instance=new JacobianMatrixModel("why");
        
        //both empty so nothing should be initialized at all
        boolean result = instance.initializeMatrixModelWithDerivedTerms(derivedTerms);
        
        assertEquals(result,false);
        assertEquals(null,instance.getMatrix());
        
        //columns are empty so it should be false and nothing initialized at all
        Map<Integer,String> hell=new HashMap<>();
        hell.put(1, "one");
        Map<Integer,String> hello=new HashMap<>();
        hello.put(0, "zero");
        
        instance.setCols(hell);
        
        result = instance.initializeMatrixModelWithDerivedTerms(derivedTerms);
        
        assertEquals(result,false);
        assertEquals(null,instance.getMatrix());        
        
        //rows are empty so it should be false and nothing should initialize at all
        
        instance.setRows(hello);
        instance=new JacobianMatrixModel("why");
        result = instance.initializeMatrixModelWithDerivedTerms(derivedTerms);
        
        assertEquals(false,result);
        assertEquals(null,instance.getMatrix());                
        
        //If true, matrix created, if match value of cell set to 1
        
        hell.put(0, "zero");        
        
        instance.setCols(hell);
        instance.setRows(hello);
        
        Matrix before=instance.getMatrix();
        
        result = instance.initializeMatrixModelWithDerivedTerms(derivedTerms);

        Matrix after=instance.getMatrix();
        
        assertEquals(true, result);

        assertEquals(1,(int)instance.getMatrix().get(0, 0));
        
        if(before==after)
            fail("Shouldn't be the same before and after, matrix should be initialized");        
        
        //If true, matrix created, if no match look for match for partial derivatives and set cell
        hell=new HashMap<>();
        hell.put(0, "zero");
        hell.put(0, "zero");
        hell.put(0, "zero");
        hell.put(2, "two");
        hell.put(3, "three");
        hello=new HashMap<>();
        hello.put(1, "one");
        hello.put(1, "one");
        hello.put(1, "one");
        hello.put(4, "four");
        hello.put(5, "five");        
        instance=new JacobianMatrixModel("why");
        instance.setCols(hell);
        instance.setRows(hello);        
        
        derivedTerms.put("dOne__dTwo", new BigDecimal("51"));
        
        before=instance.getMatrix();
        
        result = instance.initializeMatrixModelWithDerivedTerms(derivedTerms);
        
        after=instance.getMatrix();
        
        assertEquals(true,result);
        
        assertEquals(51,(int)instance.getMatrix().get(1, 2));
       
        if(before==after)
            fail("Shouldn't be the same before and after, matrix should be initialized");                
    }

     /**
     * Integration Test of class JacobianMatrixModel
     * Testing the initializations of Matrices
     */
    @Test
    public void testInitMatrix () {
        JacobianMatrixModel myMatrix = new JacobianMatrixModel( "0" );

        String[] rowNames = new String[]{"first", "second", "fourth"};
        myMatrix.setRows( rowNames );
        myMatrix.setCols( myMatrix.getRows() );

        rowNames = new String[]{"first", "second", "third", "fourth", "fifth"};
        myMatrix.setRows( rowNames );

        ConcurrentMap<String, BigDecimal> parDerivTerms = new ConcurrentHashMap<>();
        parDerivTerms.put( "dThird__dSecond", new BigDecimal( 99.9 ) );
        parDerivTerms.put( "dThird__dFourth", new BigDecimal( 11.1 ) );
        parDerivTerms.put( "dFifth__dFirst", new BigDecimal( 22.2 ) );

        if ( myMatrix.initializeMatrixModelWithDerivedTerms( parDerivTerms ) ) {           
            String results = 
                "MATRIX#=0              first                  second                 fourth                 \n" +
                "first                  1.000000000E00         0.000000000E00         0.000000000E00         \n" +
                "second                 0.000000000E00         1.000000000E00         0.000000000E00         \n" +
                "third                  0.000000000E00         9.990000000E01         1.110000000E01         \n" +
                "fourth                 0.000000000E00         0.000000000E00         1.000000000E00         \n" +
                "fifth                  2.220000000E01         0.000000000E00         0.000000000E00         \n" +
                "";
            assertEquals(results, myMatrix.ToStringWithLabels());
        }
    }   
    
    
}
