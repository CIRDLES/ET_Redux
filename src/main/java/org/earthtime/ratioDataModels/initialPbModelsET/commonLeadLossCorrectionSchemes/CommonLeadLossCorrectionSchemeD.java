/*
 * CommonLeadLossCorrectionSchemeD.java
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
package org.earthtime.ratioDataModels.initialPbModelsET.commonLeadLossCorrectionSchemes;

import java.math.BigDecimal;
import java.util.SortedMap;
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public class CommonLeadLossCorrectionSchemeD extends AbstractCommonLeadLossCorrectionScheme {

    // Class variables
    private static final long serialVersionUID = 1061000978307657863L;
    private static CommonLeadLossCorrectionSchemeD instance = null;

    private CommonLeadLossCorrectionSchemeD() {
        super("D", false);
    }

    /**
     *
     * @return
     */
    public static CommonLeadLossCorrectionSchemeD getInstance() {
        if (instance == null) {
            instance = new CommonLeadLossCorrectionSchemeD();
        }
        return instance;
    }

    /**
     *
     * @param parameters the value of parameterz
     * @param staceyKramerCorrectionParameters the value of staceyKramerCorrectionParameters
     * @param useStaceyKramer the value of useStaceyKramer
     * @param r238_235sVM the value of parameters
     * @param lambda235VM the value of r238_235s
     * @param lambda238VM the value of lambda235
     * @return 
     */
    @Override
    public ValueModel calculatePbCorrectedAge(SortedMap<String, ValueModel> parameters, SortedMap<String,BigDecimal> staceyKramerCorrectionParameters, boolean useStaceyKramer, ValueModel r238_235sVM, ValueModel lambda235VM, ValueModel lambda238VM) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
