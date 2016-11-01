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
 * Singleton class for statistical calculations.
 * 
 * @author James F. Bowring
 */
public class NonParametricStats {
    
    private static NonParametricStats instance = null;
    private double sampleMean;
    private double variance;
    private double stdErrSampleMean;

    private NonParametricStats () {
        super();
    }
    
    /**
     * Called during getInstance() to set the values of instance to null.
     * @post All values are 0.0.
     */
    private void setToDefaultValues()
    {
        sampleMean = 0.0;
        variance = 0.0;
        stdErrSampleMean = 0.0;
    }

    /**
     * This returns the one instance of NonParametricStats. If one does not
     * exist yet, one is created.
     * 
     * @return the current instance of NonParametricStats
     */
    public static NonParametricStats getInstance () {
        if ( instance == null ) {
            instance = new NonParametricStats();
        }
        instance.setToDefaultValues();
        return instance;
    }

    /**
     * Calculates the sample mean, variance, and stdErrSample of a collection 
     * of double values.
     * 
     * @post If the arrays are of different length or either is null, the 
     * sample mean, variance, and stdErrSample are set to zero.
     * @param sample The collection of doubles
     * @param dataActiveMap Determines which of the doubles should be used in
     * the calculations. Each false means the equivalent index of sample is
     * ignored
     */
    public void calculateStats ( boolean[] dataActiveMap, double[] sample ) {
        ArrayList<Double> liveSample = new ArrayList<>();
        sampleMean = 0.0;
        variance = 0.0;
        stdErrSampleMean = 0.0;

        if (dataActiveMap != null && sample != null
                && dataActiveMap.length == sample.length
                && sample.length > 0)
        {
            for (int i = 0; i < sample.length; i ++) {
                if ( dataActiveMap[i] ) {
                    sampleMean += sample[i];
                    liveSample.add( sample[i] );
                }
            }
            sampleMean /= liveSample.size();
            
            double[] liveSampleArray = new double[liveSample.size()];
            for (int i = 0; i < liveSampleArray.length; i ++){
                liveSampleArray[i] = (double)liveSample.get( i );
            }

            sampleMean = StatUtils.mean( liveSampleArray );
            //The mean can be supplied to the variance method to save on computation
            variance = StatUtils.variance( liveSampleArray, sampleMean );
            stdErrSampleMean = Math.sqrt(variance) / Math.sqrt( liveSampleArray.length );
        }

    }
    
    /**
     * Calculates the sample mean and the standard error sample mean of only
     * the points within two standard units of the mean, then returns true if
     * the sample mean is less than or equal to 2 times the standard error
     * sample mean
     * 
     * @param sample The collection of doubles
     * @param dataActiveMap Determines which of the doubles should be used in
     * the calculations. Each false means the equivalent index of sample is
     * ignored
     * @return true if below the detection limit
     */
    public boolean determineIfBelowDetectionLimitUsingTwoSigma(boolean[] dataActiveMap, double[] sample){
        boolean belowDetection;
        
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
    
    /**
     * Determines if ten percent or more of the data points are negative.
     * 
     * @param sample The collection of doubles
     * @param dataActiveMap Determines which of the doubles should be used in
     * the calculations. Each false means the equivalent index of sample is
     * ignored
     * @return true if ten percent or more of the data points are negative.
     */
    public boolean determineIfTenPercentOrMoreAreNegative(boolean[] dataActiveMap, double[] sample){
        double countOfNegative = 0;
        double countOfValues = 0;
        
        for (int i = 0; i < dataActiveMap.length; i ++){
            if (dataActiveMap[i]){
                countOfValues++;
                if (sample[i] < 0.0){
                    countOfNegative++;
                }
            }
        }
        
        return (countOfNegative / countOfValues) >= 0.1;
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
