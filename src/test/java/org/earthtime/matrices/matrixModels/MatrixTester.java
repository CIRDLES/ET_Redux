/*
 * MatrixTester.java
 *
 * This file exists so that methods of AbstractMatrixModel can be tested. All
 * of the implemented methods here are simply here in order to prevent errors
 * and are intended to be tested later in the implementations.
 *
 * Created on April 16, 2014.
 *
 *Version History:
 *April 16 2014 : File Created.
 *
 *
 *Developed for the CIRDLES lab at the College of Charleston by Patrick Brewer.
 *
 *
 */
package org.earthtime.matrices.matrixModels;


/**
 *
 * @author patrickbrewer
 */
public class MatrixTester extends AbstractMatrixModel {
    
    /**
     *
     * @param hi
     */
    public MatrixTester(String hi) {
            super(hi);
        }
        

    
    
        @Override
        public AbstractMatrixModel copy() {
            return null;
        }

        
        @Override
        public void setValueAt(int row, int col, double value) {
            
            
            matrix.set(row, col, value);
        }
    }    
    
    
    
    
    
    

