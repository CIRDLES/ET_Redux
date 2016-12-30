/*
 * NonParametricStats.java
 *
 * Created Jul 30, 2011
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
package org.earthtime.statistics;

import java.util.ArrayList;
import org.apache.commons.math3.stat.StatUtils;

/**
 *
 * @author James F. Bowring
 */
public class NonParametricStats {

    private static NonParametricStats instance = null;
    //
    private double sampleMean;
    private double variance;
    private double stdErrSampleMean;

    private NonParametricStats () {
        super();
    }

    /**
     * 
     * @return
     */
    public static NonParametricStats getInstance () {
        if ( instance == null ) {
            instance = new NonParametricStats();
        }
        return instance;
    }

    /**
     * 
     * @param dataActiveMap
     * @param sample
     */
    public void calculateStats ( boolean[] dataActiveMap, double[] sample ) {
        ArrayList<Double> liveSample = new ArrayList<>();
        sampleMean = 0.0;

        if ( sample.length > 0 ) {

            for (int i = 0; i < sample.length; i ++) {
                if ( dataActiveMap[i] ) {
                    sampleMean += sample[i];
                    liveSample.add( sample[i] );
                }
            }

            sampleMean /= liveSample.size();
        }
        
        double[] liveSampleArray = new double[liveSample.size()];
        for (int i = 0; i < liveSampleArray.length; i ++){
            liveSampleArray[i] = (double)liveSample.get( i );
        }
        
               
        sampleMean = StatUtils.mean( liveSampleArray );
        variance = StatUtils.variance( liveSampleArray );

        stdErrSampleMean = Math.sqrt(variance) / Math.sqrt( liveSampleArray.length );

    }
    
    /**
     *
     * @param dataActiveMap
     * @param sample
     * @return
     */
    public boolean determineIfBelowDetectionLimitUsingTwoSigma(boolean[] dataActiveMap, double[] sample){
        boolean belowDetection = false;
        
        calculateStats( dataActiveMap, sample );
        
        double stdDev = Math.sqrt( variance);
        boolean[] localDataActiveMap = dataActiveMap.clone();
        for (int i = 0; i < localDataActiveMap.length; i ++){
            if ((sample[i] - sampleMean) > 2.0 * stdDev){
                localDataActiveMap[i] = false;
            }
        }
        
        calculateStats( localDataActiveMap, sample);
        
        belowDetection = sampleMean <= 2.0 * stdErrSampleMean;
        
        return belowDetection;
    }
    
    public boolean determineIfTenPercentOrMoreAreNegative(boolean[] dataActiveMap, double[] sample){
        double countOfNegative = 0;
        double countOfValues = 0;;
        
        for (int i = 0; i < dataActiveMap.length; i ++){
            if (dataActiveMap[i]){
                countOfValues++;
                if (sample[i] < 0.0){
                    countOfNegative++;
                }
            }
        }
        
        return (countOfNegative / countOfValues) > 0.1;
    }

    /**
     * @return the sampleMean
     */
    public double getSampleMean () {
        return sampleMean;
    }

    /**
     * @return the stdErrSampleMean
     */
    public double getStdErrSampleMean () {
        return stdErrSampleMean;
    }

    /**
     * @return the variance
     */
    public double getVariance () {
        return variance;
    }
}
