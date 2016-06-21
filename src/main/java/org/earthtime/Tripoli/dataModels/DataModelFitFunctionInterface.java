/*
 * DataModelFitFunctionInterface.java
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
package org.earthtime.Tripoli.dataModels;

import java.util.Map;
import org.earthtime.Tripoli.fitFunctions.AbstractFunctionOfX;
import org.earthtime.dataDictionaries.FitFunctionTypeEnum;
import org.earthtime.dataDictionaries.RawRatioNames;

/**
 *
 * @author James F. Bowring
 */
public interface DataModelFitFunctionInterface {

    /**
     *
     *
     * @param propagateUncertainties the value of propagateUncertainties
     * @param doApplyMaskingArray the value of doApplyMaskingArray
     * @param inLiveMode
     */
    public void generateSetOfFitFunctions(boolean propagateUncertainties, boolean doApplyMaskingArray, boolean inLiveMode);

   // public void generateSelectedFitFunction ();
    /**
     *
     */
    public void cleanupUnctCalcs();

    /**
     *
     * @return
     */
    public Map<String, AbstractFunctionOfX> getFitFunctions();

    /**
     *
     * @return
     */
    public boolean isOverDispersionSelected();

    /**
     * @param overDispersionSelected the overDispersionSelected to set
     * @param isDownHole the value of isDownHole
     */
    public void setOverDispersionSelected(boolean overDispersionSelected);

    /**
     *
     * @param fitFunctionType
     * @return
     */
    public boolean doesFitFunctionTypeHaveOD(FitFunctionTypeEnum fitFunctionType);

    /**
     *
     * @param fitFunctionType
     * @return
     */
    public double getXIforFitFunction(FitFunctionTypeEnum fitFunctionType);

//    /**
//     *
//     * @param fitFunctionTypeName
//     */
//    public void calculateFittedFunctions(String fitFunctionTypeName);
//
    /**
     *
     * @param selectedFitFunctionType
     */
    public void setSelectedFitFunctionType(FitFunctionTypeEnum selectedFitFunctionType);

    /**
     *
     * @return
     */
    public FitFunctionTypeEnum getSelectedFitFunctionType();

    /**
     *
     * @return
     */
    public AbstractFunctionOfX getSelectedFitFunction();
    public AbstractFunctionOfX getSelectedDownHoleFitFunction();

    /**
     *
     * @return
     */
    public RawRatioNames getRawRatioModelName();

    /**
     *
     * @return
     */
    public boolean isCalculatedInitialFitFunctions();

    /**
     *
     * @param fitFunctionType
     * @return
     */
    public boolean containsFitFunction(FitFunctionTypeEnum fitFunctionType);

}
