/*
 * NonParametricStatsTest.java
 *
 * Created on October 20, 2016.
 *
 * Developed for the CIRDLES lab at the College of Charleston by Griffin Hiers.
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

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * October 31, 2016
 * @author Griffin Hiers
 */
public class NonParametricStatsTest
{
    private NonParametricStats instance;

    /**
     * Test of getInstance method, of class NonParametricStats.
     */
    @Test
    public void testGetInstance()
    {
        System.out.println("getInstance");
        
        //First tests that the first call to getInstace initializes instance
        instance = NonParametricStats.getInstance();
        assertNotNull("instance is not being initialized", instance);
        
        //tests that the values are 0 by default
        double expectedSampleMean = 0.0;
        double expectedVariance = 0.0;
        double expectedStdErrSampleMean = 0.0;
        assertEquals(expectedSampleMean, instance.getSampleMean(), 0.0);
        assertEquals(expectedVariance, instance.getVariance(), 0.0);
        assertEquals(expectedStdErrSampleMean, instance.getStdErrSampleMean(), 0.0);
        
        //Tests that subsequent calls to getInsance don't reinitialize instace
        NonParametricStats expectedResult = instance;
        instance = NonParametricStats.getInstance();
        assertSame("instance is getting reinitialized", expectedResult, instance);
        
        //sets the values to something other than zero
        boolean[] dataActiveMap = {true, true};
        double[] sample = {1, 10};
        instance.calculateStats(dataActiveMap, sample);
        
        //tests that the values are 0 by default
        instance = NonParametricStats.getInstance();
        expectedSampleMean = 0.0;
        expectedVariance = 0.0;
        expectedStdErrSampleMean = 0.0;
        assertEquals(expectedSampleMean, instance.getSampleMean(), 0.0);
        assertEquals(expectedVariance, instance.getVariance(), 0.0);
        assertEquals(expectedStdErrSampleMean, instance.getStdErrSampleMean(), 0.0);
        
        //tests that old references to instance also get reset
        assertEquals(expectedSampleMean, expectedResult.getSampleMean(), 0.0);
        assertEquals(expectedVariance, expectedResult.getVariance(), 0.0);
        assertEquals(expectedStdErrSampleMean, expectedResult.getStdErrSampleMean(), 0.0);
        
        
        
    }

    /**
     * Test of calculateStats method, of class NonParametricStats.
     */
    @Test
    public void testCalculateStats()
    {
        System.out.println("calculateStats");
        instance = NonParametricStats.getInstance();
        double expectedSampleMean;
        double expectedVariance;
        double expectedStdErrSampleMean;
        
        //First test, all values are active and equal
        boolean[] dataActiveMap = {true, true, true, true, true, true};
        double[] sample = {10, 10, 10, 10, 10, 10};
        expectedSampleMean = 10;
        expectedVariance = 0;
        expectedStdErrSampleMean = 0;
        instance.calculateStats(dataActiveMap, sample);
        assertEquals(expectedSampleMean, instance.getSampleMean(), 0);
        assertEquals(expectedVariance, instance.getVariance(), 0);
        assertEquals(expectedStdErrSampleMean, instance.getStdErrSampleMean(), 0);
        
        //all active values are equal, and one inactive outlier
        dataActiveMap[5] = false;
        sample[5] = 10000;
        expectedSampleMean = 10;
        expectedVariance = 0;
        expectedStdErrSampleMean = 0;
        instance.calculateStats(dataActiveMap, sample);
        assertEquals(expectedSampleMean, instance.getSampleMean(), 0);
        assertEquals(expectedVariance, instance.getVariance(), 0);
        assertEquals(expectedStdErrSampleMean, instance.getStdErrSampleMean(), 0);
        dataActiveMap[5] = true; sample[5] = 10;
        
        //all values are active, but not equal, but mean doesn't change
        sample[0] = 5; sample[5] = 15;
        expectedSampleMean = 10;
        expectedVariance = 10;
        expectedStdErrSampleMean = 1.29099444874;
        instance.calculateStats(dataActiveMap, sample);
        assertEquals(expectedSampleMean, instance.getSampleMean(), 0);
        assertEquals(expectedVariance, instance.getVariance(), 0);
        assertEquals(expectedStdErrSampleMean, instance.getStdErrSampleMean(), 0.0000000001);
        
        //all values are active, totally random
        sample[0] = 9; sample[1] = 54; sample[2] = 47;
        sample[3] = 35; sample[4] = 89; sample[5] = 91;
        expectedSampleMean = 54.16666666667;
        expectedVariance = 1005.7666666667;
        expectedStdErrSampleMean = 12.9471146507;
        instance.calculateStats(dataActiveMap, sample);
        assertEquals(expectedSampleMean, instance.getSampleMean(), 0.0000000001);
        assertEquals(expectedVariance, instance.getVariance(), 0.0000000001);
        assertEquals(expectedStdErrSampleMean, instance.getStdErrSampleMean(), 0.000000001);
        
        //same values, three inactive
        dataActiveMap[1] = false; dataActiveMap[3] = false; dataActiveMap[4] = false;
        expectedSampleMean = 49;
        expectedVariance = 1684;
        expectedStdErrSampleMean = 23.6924741919;
        instance.calculateStats(dataActiveMap, sample);
        assertEquals(expectedSampleMean, instance.getSampleMean(), 0.0000000001);
        assertEquals(expectedVariance, instance.getVariance(), 0.0000000001);
        assertEquals(expectedStdErrSampleMean, instance.getStdErrSampleMean(), 0.000000001);
        
        //all inactive
        dataActiveMap[0] = false; dataActiveMap[2] = false; dataActiveMap[5] = false;
        expectedSampleMean = Double.NaN;
        expectedVariance = Double.NaN;
        expectedStdErrSampleMean = Double.NaN;
        instance.calculateStats(dataActiveMap, sample);
        assertEquals(expectedSampleMean, instance.getSampleMean(), 0.0000000001);
        assertEquals(expectedVariance, instance.getVariance(), 0.0000000001);
        assertEquals(expectedStdErrSampleMean, instance.getStdErrSampleMean(), 0.000000001);
        
        //one active value that is negative
        dataActiveMap = new boolean[1]; dataActiveMap[0] = true;
        sample = new double[1]; sample[0] = -100;
        expectedSampleMean = -100;
        expectedVariance = 0;
        expectedStdErrSampleMean = 0;
        instance.calculateStats(dataActiveMap, sample);
        assertEquals(expectedSampleMean, instance.getSampleMean(), 0.0000000001);
        assertEquals(expectedVariance, instance.getVariance(), 0.0000000001);
        assertEquals(expectedStdErrSampleMean, instance.getStdErrSampleMean(), 0.000000001);
        
        //one active value that is NaN
        sample[0] = Double.NaN;
        expectedSampleMean = Double.NaN;
        expectedVariance = 0;
        expectedStdErrSampleMean = 0;
        instance.calculateStats(dataActiveMap, sample);
        assertEquals(expectedSampleMean, instance.getSampleMean(), 0.0000000001);
        assertEquals(expectedVariance, instance.getVariance(), 0.0000000001);
        assertEquals(expectedStdErrSampleMean, instance.getStdErrSampleMean(), 0.000000001);
        
        //two active values, one is NaN, the other is zero
        dataActiveMap = new boolean[2]; dataActiveMap[0] = true; dataActiveMap[1] = true;
        sample = new double[2]; sample[0] = Double.NaN; sample[1] = 0;
        expectedSampleMean = Double.NaN;
        expectedVariance = Double.NaN;
        expectedStdErrSampleMean = Double.NaN;
        instance.calculateStats(dataActiveMap, sample);
        assertEquals(expectedSampleMean, instance.getSampleMean(), 0.0000000001);
        assertEquals(expectedVariance, instance.getVariance(), 0.0000000001);
        assertEquals(expectedStdErrSampleMean, instance.getStdErrSampleMean(), 0.000000001);
        
        //two active values, one is Double.MAX_VALUE, the other is negative Double.MAX_VALUE
        sample[0] = Double.MAX_VALUE; sample[1] = - Double.MAX_VALUE;
        expectedSampleMean = 0;
        expectedVariance = Double.POSITIVE_INFINITY;
        expectedStdErrSampleMean = Double.POSITIVE_INFINITY;
        instance.calculateStats(dataActiveMap, sample);
        assertEquals(expectedSampleMean, instance.getSampleMean(), 0.0000000001);
        assertEquals(expectedVariance, instance.getVariance(), 0.0000000001);
        assertEquals(expectedStdErrSampleMean, instance.getStdErrSampleMean(), 0.000000001);
        
        //two active values, one is Double.MIN_VALUE, the other is negative Double.MIN_VALUE
        sample[0] = Double.MIN_VALUE; sample[1] = - Double.MIN_VALUE;
        expectedSampleMean = 0;
        expectedVariance = 0;
        expectedStdErrSampleMean = 0;
        instance.calculateStats(dataActiveMap, sample);
        assertEquals(expectedSampleMean, instance.getSampleMean(), 0);
        assertEquals(expectedVariance, instance.getVariance(), 0);
        assertEquals(expectedStdErrSampleMean, instance.getStdErrSampleMean(), 0);
        
        //the dataActiveMap array is larger than sample array
        dataActiveMap = new boolean[6];
        for(int i = 0; i < dataActiveMap.length; ++i) {dataActiveMap[i] = true;}
        sample[0] = 10; sample[1] = 10;
        expectedSampleMean = 0;
        expectedVariance = 0;
        expectedStdErrSampleMean = 0;
        instance.calculateStats(dataActiveMap, sample);
        assertEquals(expectedSampleMean, instance.getSampleMean(), 0);
        assertEquals(expectedVariance, instance.getVariance(), 0);
        assertEquals(expectedStdErrSampleMean, instance.getStdErrSampleMean(), 0);
        
        
        //the dataActiveMap array is smaller than sample array
        sample = new double[9];
        for(int i = 0; i < sample.length; ++i) {sample[i] = 10;}
        expectedSampleMean = 0;
        expectedVariance = 0;
        expectedStdErrSampleMean = 0;
        instance.calculateStats(dataActiveMap, sample);
        assertEquals(expectedSampleMean, instance.getSampleMean(), 0);
        assertEquals(expectedVariance, instance.getVariance(), 0);
        assertEquals(expectedStdErrSampleMean, instance.getStdErrSampleMean(), 0);
        
        //the dataActiveMap array is null
        dataActiveMap = null;
        expectedSampleMean = 0;
        expectedVariance = 0;
        expectedStdErrSampleMean = 0;
        instance.calculateStats(dataActiveMap, sample);
        assertEquals(expectedSampleMean, instance.getSampleMean(), 0);
        assertEquals(expectedVariance, instance.getVariance(), 0);
        assertEquals(expectedStdErrSampleMean, instance.getStdErrSampleMean(), 0);
        
        //the sample array is null
        dataActiveMap = new boolean[9];
        for(int i = 0; i < dataActiveMap.length; ++i) {dataActiveMap[i] = true;}
        sample = null;
        expectedSampleMean = 0;
        expectedVariance = 0;
        expectedStdErrSampleMean = 0;
        instance.calculateStats(dataActiveMap, sample);
        assertEquals(expectedSampleMean, instance.getSampleMean(), 0);
        assertEquals(expectedVariance, instance.getVariance(), 0);
        assertEquals(expectedStdErrSampleMean, instance.getStdErrSampleMean(), 0);
        
        //both arrays are null
        dataActiveMap = null;
        expectedSampleMean = 0;
        expectedVariance = 0;
        expectedStdErrSampleMean = 0;
        instance.calculateStats(dataActiveMap, sample);
        assertEquals(expectedSampleMean, instance.getSampleMean(), 0);
        assertEquals(expectedVariance, instance.getVariance(), 0);
        assertEquals(expectedStdErrSampleMean, instance.getStdErrSampleMean(), 0);
    }

    /**
     * Test of determineIfBelowDetectionLimitUsingTwoSigma method, of class NonParametricStats.
     */
    @Test
    public void testDetermineIfBelowDetectionLimitUsingTwoSigma()
    {
        System.out.println("determineIfBelowDetectionLimitUsingTwoSigma");
        instance = NonParametricStats.getInstance();
        boolean expectedResult;
        boolean result;
        
        //First test, all values are active and equal
        boolean[] dataActiveMap = {true, true, true, true, true, true};
        double[] sample = {10, 10, 10, 10, 10, 10};
        expectedResult = false;
        result = instance.determineIfBelowDetectionLimitUsingTwoSigma(dataActiveMap, sample);
        assertEquals(expectedResult, result);
        
        //all active values are equal, and one inactive outlier
        dataActiveMap[5] = false; sample[5] = 10000;
        expectedResult = false;
        result = instance.determineIfBelowDetectionLimitUsingTwoSigma(dataActiveMap, sample);
        assertEquals(expectedResult, result);
        
        //one outlier, that is outside of two*sigma
        dataActiveMap[5] = true;
        expectedResult = false;
        result = instance.determineIfBelowDetectionLimitUsingTwoSigma(dataActiveMap, sample);
        assertEquals(expectedResult, result);
        
        /*huge original standard deviation, and a second outlier within
          two*sigma, so that the recalculated variance is also huge*/
        sample[4] = 3000;
        expectedResult = true;
        result = instance.determineIfBelowDetectionLimitUsingTwoSigma(dataActiveMap, sample);
        assertEquals(expectedResult, result);
        
        //all values are active, totally random
        sample[0] = 9; sample[1] = 54; sample[2] = 47;
        sample[3] = 35; sample[4] = 89; sample[5] = 91;
        expectedResult = false;
        result = instance.determineIfBelowDetectionLimitUsingTwoSigma(dataActiveMap, sample);
        assertEquals(expectedResult, result);
        
        //all values inactive
        dataActiveMap[0] = false; dataActiveMap[1] = false; dataActiveMap[2] = false;
        dataActiveMap[3] = false; dataActiveMap[4] = false; dataActiveMap[5] = false;
        expectedResult = false;
        result = instance.determineIfBelowDetectionLimitUsingTwoSigma(dataActiveMap, sample);
        assertEquals(expectedResult, result);
        
    }

    /**
     * Test of determineIfTenPercentOrMoreAreNegative method, of class NonParametricStats.
     */
    @Test
    public void testDetermineIfTenPercentOrMoreAreNegative()
    {
        System.out.println("determineIfTenPercentOrMoreAreNegative");
        instance = NonParametricStats.getInstance();
        boolean expectedResult;
        boolean result;
        
        //First test, all values are active and positive
        boolean[] dataActiveMap = {true, true, true, true, true, true, true, true, true, true};
        double[] sample = {1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        expectedResult = false;
        result = instance.determineIfTenPercentOrMoreAreNegative(dataActiveMap, sample);
        assertEquals(expectedResult, result);
        
        //all values are active and nonnegative
        sample[0] = 0; sample[1] = 0; sample[2] = 0; sample[3] = 0; sample[4] = 0;
        sample[5] = 0; sample[6] = 0; sample[7] = 0; sample[8] = 0; sample[9] = 0;
        expectedResult = false;
        result = instance.determineIfTenPercentOrMoreAreNegative(dataActiveMap, sample);
        assertEquals(expectedResult, result);
        
        //one value, of ten, is negative
        sample[0] = -1;
        expectedResult = true;
        result = instance.determineIfTenPercentOrMoreAreNegative(dataActiveMap, sample);
        assertEquals(expectedResult, result);
        
        //one value, of ten, is negative, another is inactive
        dataActiveMap[9] = false;
        expectedResult = true;
        result = instance.determineIfTenPercentOrMoreAreNegative(dataActiveMap, sample);
        assertEquals(expectedResult, result);
        
        //one value, of ten, is negative infinity
        sample[0] = Double.NEGATIVE_INFINITY; dataActiveMap[9] = true;
        expectedResult = true;
        result = instance.determineIfTenPercentOrMoreAreNegative(dataActiveMap, sample);
        assertEquals(expectedResult, result);
    }
    
}
