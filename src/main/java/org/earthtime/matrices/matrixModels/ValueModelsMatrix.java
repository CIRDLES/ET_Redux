/*
 * ValueModelsMatrix.java
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
package org.earthtime.matrices.matrixModels;

import Jama.Matrix;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;

/**
 *
 * @author James F. Bowring
 */
public class ValueModelsMatrix extends AbstractMatrixModel {

    /**
     *
     */
    public ValueModelsMatrix() {
        super(null);
    }

    /**
     *
     * @param levelName the value of levelName
     */
    public ValueModelsMatrix(String levelName) {
        super(levelName);
    }

    @Override
    public AbstractMatrixModel copy() {
        AbstractMatrixModel retval = new ValueModelsMatrix(levelName);

        retval.setRows(rows);
        retval.copyCols(cols);
        retval.setMatrix(matrix.copy());

        return retval;
    }

    /**
     *
     * @param parameterModel the value of parameterModel
     * @param showAbsUnct the value of showAbsUnct
     */
    public void initializeMatrixModelWithParameterModel(AbstractRatiosDataModel parameterModel, boolean showAbsUnct) {
        ValueModel[] ratios = parameterModel.getData();

        // build rows
        cols.put("Val", 0);
        cols.put("Var", 1);
        cols.put("Sys", 2);

        // build columns and matrix
        matrix = new Matrix(ratios.length, 3);//new Matrix(3, ratios.length);
        for (int i = 0; i < ratios.length; i++) {
            rows.put(i, ratios[i].getName());
            matrix.set(i, 0, ratios[i].getValue().doubleValue());
            if (showAbsUnct) {
                matrix.set(i, 1, ratios[i].getOneSigmaAbs().doubleValue());
                matrix.set(i, 2, ratios[i].getOneSigmaSysAbs().doubleValue());
            } else {
                matrix.set(i, 1, ratios[i].getOneSigmaPct().doubleValue());
                matrix.set(i, 2, ratios[i].getOneSigmaSysPct().doubleValue());
            }
        }
    }

    @Override
    public void setValueAt(int row, int col, double value) {
        getMatrix().set(row, col, value);
    }

}
