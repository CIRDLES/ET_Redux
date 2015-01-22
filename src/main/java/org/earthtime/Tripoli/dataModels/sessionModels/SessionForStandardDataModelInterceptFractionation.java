/*
 * SessionForStandardDataModelInterceptFractionation.java
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
package org.earthtime.Tripoli.dataModels.sessionModels;

import java.util.SortedSet;
import org.earthtime.Tripoli.fractions.TripoliFraction;
import org.earthtime.Tripoli.sessions.TripoliSessionInterface;
import org.earthtime.dataDictionaries.RawRatioNames;

/**
 *
 * @author James F. Bowring
 */
public class SessionForStandardDataModelInterceptFractionation extends AbstractSessionForStandardDataModel {

    private static final long serialVersionUID = 6188661075381090126L;
    /**
     *
     *
     * @param tripoliSession the value of tripoliSession
     * @param rawRatioName
     * @param standardValue the value of standardValue
     * @param standardFractions
     */       
    public SessionForStandardDataModelInterceptFractionation (//
            TripoliSessionInterface tripoliSession, RawRatioNames rawRatioName, double standardValue, SortedSet<TripoliFraction> standardFractions) {
        super( tripoliSession, rawRatioName, standardValue, standardFractions );
        sessionTechnique = "INTERCEPT";
    }

//    @Override
//    public void generateSelectedFitFunction () {
//        throw new UnsupportedOperationException( "Not supported yet." );
//    }

//    /**
//     *
//     */
//    @Override
//    public void generateSetOfFitFunctions () {
//
//        
//
//        includedStandardsDataActiveMap = new boolean[getStandardFractions().size()];
//        includedStandardsAquireTimes = new double[includedStandardsDataActiveMap.length];
//        includedStandardsMeanLogRatios = new double[includedStandardsDataActiveMap.length];
//        includedStandardsMeanLogRatioStdErrs = new double[includedStandardsDataActiveMap.length];
//
//        Iterator fractionIterator = getStandardFractions().iterator();
//        int index = 0;
//        boolean atLeastOneFraction = false;
//        while (fractionIterator.hasNext()) {
//            TripoliFraction tf = (TripoliFraction) fractionIterator.next();
//            includedStandardsAquireTimes[index] = tf.getZeroBasedNormalizedTimeStamp();
//            includedStandardsDataActiveMap[index] = tf.isIncluded();
//
//            if ( ((RawRatioDataModel) tf.getRawRatioDataModelByName( rawRatioName )).isBelowDetection() ) {
//                includedStandardsDataActiveMap[index] = false;
//            }
//
//            if ( includedStandardsDataActiveMap[index] ) {
//                atLeastOneFraction = true;
//                includedStandardsMeanLogRatios[index] = //
//                        ((RawRatioDataModel) tf.getRawRatioDataModelByName( rawRatioName ))//
//                        .getSelectedFitFunction().getYIntercept();
//                includedStandardsMeanLogRatioStdErrs[index] = //
//                      ((RawRatioDataModel) tf.getRawRatioDataModelByName( rawRatioName ))//
//                        .getSelectedFitFunction().getYInterceptStdErr();
//            }
//            index ++;
//        }
//
//        if ( atLeastOneFraction ) {
//            calculateFitFunctions();
//        }
//
//    }

    /**
     *
     */
    
    @Override
    public void cleanupUnctCalcs () {
        throw new UnsupportedOperationException( "Not supported yet." );
    }
}
