/*
 * SessionForStandardDataModelDownholeFractionation.java
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
package org.earthtime.Tripoli.dataModels.sessionModels;

import java.util.SortedSet;
import org.earthtime.Tripoli.fractions.TripoliFraction;
import org.earthtime.Tripoli.sessions.TripoliSessionInterface;
import org.earthtime.dataDictionaries.RawRatioNames;

/**
 *
 * @author James F. Bowring
 */
public class SessionForStandardDataModelDownholeFractionation extends AbstractSessionForStandardDataModel {

    private static final long serialVersionUID = -4499822226009109873L;
    /**
     *
     *
     * @param tripoliSession the value of tripoliSession
     * @param rawRatioName
     * @param standardValue the value of standardValue
     * @param standardFractions
     */  
    public SessionForStandardDataModelDownholeFractionation (//
            TripoliSessionInterface tripoliSession, RawRatioNames rawRatioName, double standardValue, SortedSet<TripoliFraction> standardFractions) {
        super(tripoliSession, rawRatioName, standardValue, standardFractions);
        sessionTechnique = "DOWNHOLE";
    }

    /**
     *
     */
    
    @Override
    public void cleanupUnctCalcs () {
        throw new UnsupportedOperationException( "Not supported yet." );
    }
}
