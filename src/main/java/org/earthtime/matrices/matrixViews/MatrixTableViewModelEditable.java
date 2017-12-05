/*
 * MatrixTableViewModelEditable.java
 *
 * Copyright 2006-2018 James F. Bowring, CIRDLES.org, and Earth-Time.org
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
package org.earthtime.matrices.matrixViews;

import org.earthtime.matrices.matrixModels.AbstractMatrixModel;
import org.earthtime.ratioDataViews.DataEntryDetectorInterface;

/**
 *
 * @author James F. Bowring
 */
public class MatrixTableViewModelEditable extends AbstractMatrixTableViewModel {

    private final DataEntryDetectorInterface dataEntryDetector;
    private final boolean lockDiagonal;

    /**
     *
     *
     * @param matrixModel
     * @param dataEntryDetector the value of dataEntryDetector
     * @param lockDiagonal
     */
    public MatrixTableViewModelEditable(AbstractMatrixModel matrixModel, DataEntryDetectorInterface dataEntryDetector, boolean lockDiagonal) {
        super(matrixModel);
        this.dataEntryDetector = dataEntryDetector;
        this.lockDiagonal = lockDiagonal;
    }

    /**
     *
     * @param row
     * @param col
     * @return
     */
    @Override
    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears on screen.
        // no edit of column of labels or diagonals unless not lockDiagonal
        return !((col < 1) || (lockDiagonal && (row == (col - 1))));
    }

    /**
     *
     * @param value
     * @param row
     * @param column
     */
    @Override
    public void setValueAt(Object value, int row, int column) {
        // matrix itself does not have row labels so column value is offset by 1

        try {
            double myDouble = Double.valueOf((String) value);

            matrixModel.setValueAt(row, column - 1, myDouble);
            dataEntryDetector.dataEntryDetected();

        } catch (NumberFormatException numberFormatException) {
        }
    }
}
