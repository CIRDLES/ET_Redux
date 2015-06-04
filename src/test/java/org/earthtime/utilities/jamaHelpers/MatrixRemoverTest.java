/*
 * Copyright 2015 CIRDLES.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.earthtime.utilities.jamaHelpers;

import Jama.Matrix;
import static org.earthtime.utilities.jamaHelpers.MatrixRemover.removeCol;
import static org.earthtime.utilities.jamaHelpers.MatrixRemover.removeRow;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author parizotclement
 */
public class MatrixRemoverTest {
    
    
    /**
     * Integration Test of class MatrixRemover
     * Testing the removal of a matrix content
     * @throws java.lang.Exception
     */
    @Test
    public void testMatrixRemove() throws Exception {
                
        double[] test = new double[]{1, 5, 9, 13, 2, 6, 10, 14, 3, 7, 11, 15, 4, 8, 12, 16};
        Matrix testM = new Matrix(test, 4);   
        assertEquals(4, testM.getRowDimension());
        assertEquals(4, testM.getColumnDimension());
        
        testM = removeRow(testM, 2);
        assertEquals(3, testM.getRowDimension());
        assertEquals(4, testM.getColumnDimension());

        testM = removeCol(testM, 2);
        assertEquals(3, testM.getRowDimension());
        assertEquals(3, testM.getColumnDimension());
        
    }
    
}
