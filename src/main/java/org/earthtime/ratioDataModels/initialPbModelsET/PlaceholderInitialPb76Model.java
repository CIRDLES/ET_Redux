/*
 * PlaceholderInitialPb76Model.java
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
package org.earthtime.ratioDataModels.initialPbModelsET;

import java.math.BigDecimal;
import java.util.Iterator;
import org.earthtime.Tripoli.fractions.TripoliFraction;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.matrices.matrixModels.AbstractMatrixModel;
import org.earthtime.utilities.DateHelpers;

/**
 *
 * @author samuelbowring
 */
public final class PlaceholderInitialPb76Model extends InitialPbModelET {

    // class variables
    private static final long serialVersionUID = 7505044045083278070L;
    private TripoliFraction tripoliFraction;

    /**
     *
     */
    public PlaceholderInitialPb76Model() {
        super("Custom 7/6 Pb Model", //
                1, 0,//
                "EARTHTIME",//
                DateHelpers.defaultEarthTimeDateString(),//
                "",//
                "used for common lead.");

        this.initializeModel();
    }

    /**
     *
     * @param tripoliFraction
     */
    public PlaceholderInitialPb76Model(TripoliFraction tripoliFraction) {
        super("Custom 7/6 Pb Model", //
                1, 0,//
                "EARTHTIME",//
                DateHelpers.defaultEarthTimeDateString(),//
                "",//
                "used for common lead.");

        this.tripoliFraction = tripoliFraction;
        resetModelFromTripoliFraction();
        //this.initializeModel();
    }

    /**
     *
     * @param updateOnly
     */
    @Override
    public final void initializeNewRatiosAndRhos(boolean updateOnly) {
        // initial pb model has a defined set of ratios 
        this.ratios = new ValueModel[1];

        this.ratios[0]
                = new ValueModel("r207_206c",
                        BigDecimal.ZERO,
                        "ABS",
                        BigDecimal.ZERO, BigDecimal.ZERO);

        buildRhosMap();
        buildRhosSysUnctMap();
    }

    /**
     *
     * @param initialPbModelMatrix
     * @param showAbsUnct
     */
    public void savePlaceHolderParametersToFraction(AbstractMatrixModel initialPbModelMatrix, boolean showAbsUnct) {

        Iterator<Integer> modelMatrixRowKeyIterator = initialPbModelMatrix.getRows().keySet().iterator();
        while (modelMatrixRowKeyIterator.hasNext()) {
            int rowNum = modelMatrixRowKeyIterator.next();
            String name = initialPbModelMatrix.getRows().get(rowNum);
            // columns are value, 1sigmaVar, 1sigmaSys
            ValueModel ratio = getDatumByName(name);
            ratio.setValue(new BigDecimal(Double.toString(initialPbModelMatrix.getMatrix().get(rowNum, 0))));
            ratio.setOneSigma(new BigDecimal(initialPbModelMatrix.getMatrix().get(rowNum, 1)));
            ratio.setOneSigmaSys(new BigDecimal(initialPbModelMatrix.getMatrix().get(rowNum, 2)));
            if (showAbsUnct) {
                ratio.setUncertaintyTypeABS();
            } else {
                ratio.setUncertaintyTypePCT();
            }
        }

        tripoliFraction.setInitialPbPlaceHolderModelR207_206c(getDatumByName("r207_206c"));
    }

    /**
     *
     * @param correlationVarUnctMatrixView
     * @param correlationSysUnctMatrixView
     */
    public void savePlaceHolderRhoVarSysToFraction(//
            AbstractMatrixModel correlationVarUnctMatrixView, AbstractMatrixModel correlationSysUnctMatrixView) {
        tripoliFraction.setInitialPbPlaceHolderVarRhos(null);
        tripoliFraction.setInitialPbPlaceHolderSysRhos(null);
    }

    /**
     *
     */
    public void resetModelFromTripoliFraction() {
        getDatumByName("r207_206c").copyValuesFrom(tripoliFraction.getInitialPbPlaceHolderModelR207_206c());

        rhos = null;
        rhosSysUnct = null;

        initializeModel();

        tripoliFraction.setInitialPbSchemeA_r207_206c(calculateR207_206c());

    }

    /**
     *
     * @return
     */
    @Override
    public ValueModel calculateR207_206c() {
        return tripoliFraction.getInitialPbPlaceHolderModelR207_206c();
    }

    /**
     * @return the tripoliFraction
     */
    public TripoliFraction getTripoliFraction() {
        return tripoliFraction;
    }

    /**
     * @param tripoliFraction the tripoliFraction to set
     */
    public void setTripoliFraction(TripoliFraction tripoliFraction) {
        this.tripoliFraction = tripoliFraction;
    }
}
