/*
 * MatrixRemover.java
 *
 * 
 * Copyright 2006-2015 James F. Bowring and www.Earth-Time.org
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.earthtime.utilities.jamaHelpers;

import Jama.Matrix;
import java.text.DecimalFormat;

/**
 *
 * @author James F. Bowring
 */
public class MatrixRemover {

    public static Matrix removeRow(Matrix matrix, int index) {
        // row too high takes last row
        // neg row behaves as positive row
        double[] columnsPacked = matrix.getColumnPackedCopy();
        int rowDim = matrix.getRowDimension();
        int colDim = matrix.getColumnDimension();

        double[] columnsPackedRowRemoved = new double[(rowDim - 1) * colDim];
        int j = 0;
        for (int i = 0; i < columnsPacked.length; i++) {
            if ((i - index) % rowDim != 0) {
                columnsPackedRowRemoved[j] = columnsPacked[i];
                j ++;
            }
        }

        Matrix rowRemoved = new Matrix(columnsPackedRowRemoved, rowDim - 1);

        return rowRemoved;
    }

    public static Matrix removeCol(Matrix matrix, int index) {
        // col too high takes last col
        // col behaves as positive col
        double[] columnsPacked = matrix.getColumnPackedCopy();
        int rowDim = matrix.getRowDimension();
        int colDim = matrix.getColumnDimension();

        double[] columnsPackedColRemoved = new double[(colDim - 1) * rowDim];
        int j = 0;
        for (int i = 0; i < columnsPacked.length; i++) {
            if ((i < (rowDim * index)) || (i >= rowDim * (index + 1))) {
                columnsPackedColRemoved[j] = columnsPacked[i];
                j ++;
            }
        }

        Matrix colRemoved = new Matrix(columnsPackedColRemoved, rowDim);

        return colRemoved;
    }

}
