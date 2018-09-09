/*
 * InitialPbModelSynchInterface.java
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
package org.earthtime.UPb_Redux.dialogs.projectManagers;

import java.math.BigDecimal;
import java.util.SortedMap;
import org.earthtime.Tripoli.fractions.TripoliFraction;
import org.earthtime.dataDictionaries.RadDatesForPbCorrSynchEnum;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;

/**
 *
 * @author James F. Bowring
 */
public interface InitialPbModelSynchInterface {
    
    /**
     *
     * @param parameters
     */
    public void synchToThisInitialPbModelSK(SortedMap<String, BigDecimal> parameters);
    
    /**
     *
     * @param tripoliFraction
     */
    public void synchToThisInitialPbModelPlaceHolderFromFraction(TripoliFraction tripoliFraction);
    
    public void synchToThisSynchronizedSKDate(RadDatesForPbCorrSynchEnum radDateForSKSynch);
    
    /**
     *
     * @param parameterModel
     */
    public void updateCalculatedParameters(AbstractRatiosDataModel parameterModel);
    
}
