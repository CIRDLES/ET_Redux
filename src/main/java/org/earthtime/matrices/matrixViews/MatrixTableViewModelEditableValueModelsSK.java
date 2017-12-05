/*
 * MatrixTableViewModelEditableValueModelsSK.java
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
public class MatrixTableViewModelEditableValueModelsSK extends AbstractMatrixTableViewModel {

    private final DataEntryDetectorInterface dataEntryDetector;
    private boolean showAbsUnct;

    /**
     *
     *
     * @param matrixModel
     * @param dataEntryDetector the value of dataEntryDetector
     * @param showAbsUnct the value of showAbsUnct
     */
    public MatrixTableViewModelEditableValueModelsSK(AbstractMatrixModel matrixModel, DataEntryDetectorInterface dataEntryDetector, boolean showAbsUnct) {
        super(matrixModel);
        this.dataEntryDetector = dataEntryDetector;
        this.showAbsUnct = showAbsUnct;
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
        // Special Case for StaceyKramer parameters to allow edit of top row of var and sys uncertainties
        return ((col == 2) || (col == 3)) && (row == 0);
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
        // NOTE: this model is ued to record valuemodels so the second and third columns need to be checked for PCT or ABS 
        try {
            double myDouble = Double.valueOf((String) value);

            matrixModel.setValueAt(row, column - 1, myDouble);

            dataEntryDetector.dataEntryDetected();
            
        } catch (NumberFormatException numberFormatException) {
        }
    }

}
