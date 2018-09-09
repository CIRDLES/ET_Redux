/*
 * PlaceholderInitialPbModel.java
 *
 *
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
package org.earthtime.ratioDataModels.initialPbModelsET;

import Jama.Matrix;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.earthtime.Tripoli.fractions.TripoliFraction;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.matrices.matrixModels.AbstractMatrixModel;
import org.earthtime.utilities.DateHelpers;

/**
 *
 * @author samuelbowring
 */
public final class PlaceholderInitialPbModel extends InitialPbModelET {

    // class variables
    private static final long serialVersionUID = 7505044045083278070L;
    private TripoliFraction tripoliFraction;

    /**
     *
     */
    public PlaceholderInitialPbModel() {
        super("Custom Pb Model", //
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
    public PlaceholderInitialPbModel(TripoliFraction tripoliFraction) {
        super("Custom Pb Model", //
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

        tripoliFraction.setInitialPbPlaceHolderModelR206_204c(getDatumByName("r206_204c"));
        tripoliFraction.setInitialPbSchemeB_R206_204c(getDatumByName("r206_204c"));

        tripoliFraction.setInitialPbPlaceHolderModelR207_204c(getDatumByName("r207_204c"));
        tripoliFraction.setInitialPbSchemeB_R207_204c(getDatumByName("r207_204c"));

        tripoliFraction.setInitialPbPlaceHolderModelR208_204c(getDatumByName("r208_204c"));
        tripoliFraction.setInitialPbSchemeB_R208_204c(getDatumByName("r208_204c"));

        tripoliFraction.setInitialPbSchemeA_r207_206c(calculateR207_206c());

    }

    /**
     *
     * @param correlationVarUnctMatrixView
     * @param correlationSysUnctMatrixView
     */
    public void savePlaceHolderRhoVarSysToFraction(//
            AbstractMatrixModel correlationVarUnctMatrixView, AbstractMatrixModel correlationSysUnctMatrixView) {

        // matrices only have meaningful rhos
        Matrix correlationVarUnctMatrix = correlationVarUnctMatrixView.getMatrix();
        Matrix correlationSysUnctMatrix = correlationSysUnctMatrixView.getMatrix();
        Map<String, BigDecimal> initialPlaceholderRhos;
        try {
            initialPlaceholderRhos = tripoliFraction.getInitialPbPlaceHolderVarRhos();
        } catch (Exception e) {
            initialPlaceholderRhos = new HashMap<>();
        }

        if (correlationVarUnctMatrix != null) {

            initialPlaceholderRhos.put("rhoR206_204c__r207_204c", new BigDecimal(correlationVarUnctMatrix.get(0, 1)));

            initialPlaceholderRhos.put("rhoR206_204c__r208_204c", new BigDecimal(correlationVarUnctMatrix.get(0, 2)));

            initialPlaceholderRhos.put("rhoR207_204c__r208_204c", new BigDecimal(correlationVarUnctMatrix.get(1, 2)));
        }
        if (correlationSysUnctMatrix != null) {

            initialPlaceholderRhos.put("rhoR206_204c__r207_204c", new BigDecimal(correlationSysUnctMatrix.get(0, 1)));

            initialPlaceholderRhos.put("rhoR206_204c__r208_204c", new BigDecimal(correlationSysUnctMatrix.get(0, 2)));

            initialPlaceholderRhos.put("rhoR207_204c__r208_204c", new BigDecimal(correlationSysUnctMatrix.get(1, 2)));

        }
    }

    /**
     *
     */
    public void resetModelFromTripoliFraction() {
        getDatumByName("r206_204c").copyValuesFrom(tripoliFraction.getInitialPbPlaceHolderModelR206_204c());
        getDatumByName("r207_204c").copyValuesFrom(tripoliFraction.getInitialPbPlaceHolderModelR207_204c());
        getDatumByName("r208_204c").copyValuesFrom(tripoliFraction.getInitialPbPlaceHolderModelR208_204c());

        try {
            rhos = tripoliFraction.copyInitialPbPlaceHolderVarRhos();
        } catch (Exception e) {
            rhos = new HashMap<>();
        }
        try {
            rhosSysUnct = tripoliFraction.copyInitialPbPlaceHolderSysRhos();
        } catch (Exception e) {
            rhosSysUnct = new HashMap<>();
        }
        initializeModel();

        tripoliFraction.setInitialPbSchemeA_r207_206c(calculateR207_206c());

        tripoliFraction.setInitialPbSchemeB_R206_204c(getDatumByName("r206_204c"));
        tripoliFraction.setInitialPbSchemeB_R207_204c(getDatumByName("r207_204c"));
        tripoliFraction.setInitialPbSchemeB_R208_204c(getDatumByName("r208_204c"));

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
