/*
 * RatiosDataViewNotEditable.java
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
package org.earthtime.ratioDataViews;

import java.awt.Dimension;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.earthtime.UPb_Redux.valueModelPanelViews.ValueModelsPanelViewNotEditable;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.matrices.matrixViews.MatrixGridViewNotEditable;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataModels.initialPbModelsET.InitialPbModelET;

/**
 *
 * @author James F. Bowring
 */
public class RatiosDataViewNotEditable extends AbstractRatiosDataView {

    /**
     *
     *
     * @param ratiosDataModel the value of ratiosDataModel
     * @param parentDimension the value of parentDimension
     * @param showTableOnly the value of showTableOnly
     */
    public RatiosDataViewNotEditable ( AbstractRatiosDataModel ratiosDataModel, Dimension parentDimension, boolean showTableOnly) {
        super( ratiosDataModel, parentDimension);

        this.valueModelsPanelView = new ValueModelsPanelViewNotEditable(//
                ratiosDataModel.getData());

        this.covarianceVarUnctMatrixView = new MatrixGridViewNotEditable( ratiosDataModel.getDataCovariancesVarUnct(),showTableOnly );
        this.correlationVarUnctMatrixView = new MatrixGridViewNotEditable( ratiosDataModel.getDataCorrelationsVarUnct(),showTableOnly );

        initView(false);
    }

    /**
     * 
     *
     * @param checkCovarianceValidity the value of checkCovarianceValidity
     */
    
    @Override
    protected void saveEdits (boolean checkCovarianceValidity) {
        throw new UnsupportedOperationException( "Not saveable." );
    }

    
   
}
