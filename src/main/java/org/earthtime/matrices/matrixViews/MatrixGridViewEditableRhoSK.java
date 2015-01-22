/*
 * MatrixGridViewEditableRhoSK.java
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
package org.earthtime.matrices.matrixViews;

import java.awt.Color;
import org.earthtime.matrices.matrixModels.AbstractMatrixModel;
import org.earthtime.ratioDataViews.DataEntryDetectorInterface;

/**
 *
 * @author James F. Bowring
 */
public class MatrixGridViewEditableRhoSK extends AbstractMatrixGridView {

    /**
     *
     *
     * @param matrixModel
     * @param dataEntryDetector the value of dataEntryDetector
     * @param showTableOnly the value of showTableOnly
     * @param lockDiagonal the value of lockDiagonal
     */
    public MatrixGridViewEditableRhoSK(AbstractMatrixModel matrixModel, DataEntryDetectorInterface dataEntryDetector, boolean showTableOnly, boolean lockDiagonal) {
        super(matrixModel, true, showTableOnly);

        matrixTableViewModelEditable = new MatrixTableViewModelEditableRhoSK(matrixModel, dataEntryDetector);
        table = new JTableForMatrices(matrixTableViewModelEditable);

        table.setBackground(Color.white);

        ((JTableForMatrices)table).setMatrixCellRenderer(matrixTableViewModelEditable.matrixCellRenderer);
        
        initGridView();
    }
}
