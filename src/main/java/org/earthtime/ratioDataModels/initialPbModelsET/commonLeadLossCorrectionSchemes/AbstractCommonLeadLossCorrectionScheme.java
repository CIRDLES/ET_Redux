/*
 * AbstractCommonLeadLossCorrectionScheme.java
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
package org.earthtime.ratioDataModels.initialPbModelsET.commonLeadLossCorrectionSchemes;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.SortedMap;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.dataDictionaries.Lambdas;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataModels.initialPbModelsET.InitialPbModelET;
import org.earthtime.ratioDataModels.initialPbModelsET.StaceyKramersInitialPbModelET;
import org.earthtime.ratioDataModels.physicalConstantsModels.PhysicalConstantsModel;

/**
 *
 * @author James F. Bowring
 */
public abstract class AbstractCommonLeadLossCorrectionScheme implements Serializable {

    // Class variables
    private static final long serialVersionUID = 3964677330936840116L;
    // Instance Attributes

    /**
     *
     */
    protected String name;
    // Schems A2 and B2 calcluate estimated date after reduction
    private boolean calculatesSKEstimatedDate;

    /**
     *
     */
    public AbstractCommonLeadLossCorrectionScheme() {
    }

    /**
     *
     * @param name the value of name
     * @param calculatesSKEstimatedDate
     * @param usesNewtonSolver the value of calculatesSKEstimatedDate
     */
    public AbstractCommonLeadLossCorrectionScheme(String name, boolean calculatesSKEstimatedDate) {
        this.name = name;
        this.calculatesSKEstimatedDate = calculatesSKEstimatedDate;
    }

    /**
     *
     * @param parameters the value of parameters
     * @param staceyKramerCorrectionParameters the value of
     * staceyKramerCorrectionParameters
     * @param useStaceyKramer the value of useStaceyKramer
     * @param r238_235sVM the value of r238_235s
     * @param lambda235VM the value of lambda235
     * @param lambda238VM the value of lambda238
     * @return
     */
    public abstract ValueModel calculatePbCorrectedAge(SortedMap<String, ValueModel> parameters, SortedMap<String, BigDecimal> staceyKramerCorrectionParameters, boolean useStaceyKramer, ValueModel r238_235sVM, ValueModel lambda235VM, ValueModel lambda238VM);

    /**
     *
     * @param staceyKramerCorrectionParameters the value of
     * staceyKramerCorrectionParameters
     * @return
     */
    protected ValueModel determineR207_206cUsingStaceyKramer(SortedMap<String, BigDecimal> staceyKramerCorrectionParameters) {
        AbstractRatiosDataModel skInitialPbModel = new StaceyKramersInitialPbModelET();
        AbstractRatiosDataModel physicalConstantsModel
                = PhysicalConstantsModel.getMostRecentEARTHTIMEPhysicalConstantsModel();

        BigDecimal lambda238 = physicalConstantsModel.getDatumByName(Lambdas.lambda238.getName()).getValue();
        BigDecimal lambda235 = physicalConstantsModel.getDatumByName(Lambdas.lambda235.getName()).getValue();
        BigDecimal lambda232 = physicalConstantsModel.getDatumByName(Lambdas.lambda232.getName()).getValue();
        ((StaceyKramersInitialPbModelET) skInitialPbModel).calculateRatios(//
                staceyKramerCorrectionParameters.get("skEstimatedDate"), lambda238, lambda235, lambda232);

        ((StaceyKramersInitialPbModelET) skInitialPbModel).calculateUncertaintiesAndRhos(//
                staceyKramerCorrectionParameters.get("skOneSigmaVarUnctPct"), staceyKramerCorrectionParameters.get("skRhoVarUnct"), //
                staceyKramerCorrectionParameters.get("skOneSigmaSysUnctPct"), staceyKramerCorrectionParameters.get("skRhoSysUnct"));

        ValueModel r207_206c = ((InitialPbModelET) skInitialPbModel).calculateR207_206c();
        ((StaceyKramersInitialPbModelET) skInitialPbModel).setR207_206cUnctPCT(r207_206c.getOneSigmaPct());
        return r207_206c;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the calculatesSKEstimatedDate
     */
    public boolean doesCalculateSKEstimatedDate() {
        return calculatesSKEstimatedDate;
    }
}
