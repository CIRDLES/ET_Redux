/*
 * ParametricCurveSegmentI.java
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
package org.earthtime.UPb_Redux.dateInterpretation.concordia;

/**
 *
 * @author James F. Bowring
 */
public interface ParametricCurveSegmentI {

    /**
     * 
     */
    void SplitLeft ();

    /**
     * 
     * @return
     */
    ParametricCurveSegmentI getLeftSeg ();

    /**
     * 
     * @return
     */
    double getMaxT ();

    /**
     * 
     * @return
     */
    double getMinT ();

    /**
     * 
     * @return
     */
    ParametricCurveSegmentI getRightSeg ();

    /**
     * 
     * @param aspectRatio
     * @return
     */
    public abstract double maxLessSigmaX ( double aspectRatio );

    /**
     * 
     * @param aspectRatio
     * @return
     */
    public abstract double maxLessSigmaY ( double aspectRatio );

    /**
     * 
     * @param aspectRatio
     * @return
     */
    public abstract double maxPlusSigmaX ( double aspectRatio );

    /**
     * 
     * @param aspectRatio
     * @return
     */
    public abstract double maxPlusSigmaY ( double aspectRatio );

    /**
     * 
     * @return
     */
    double maxX ();

    /**
     * 
     * @return
     */
    double maxY ();

    /**
     * 
     * @param aspectRatio
     * @return
     */
    public abstract double minLessSigmaX ( double aspectRatio );

    /**
     * 
     * @param aspectRatio
     * @return
     */
    public abstract double minLessSigmaY ( double aspectRatio );

    /**
     * 
     * @param aspectRatio
     * @return
     */
    public abstract double minPlusSigmaX ( double aspectRatio );

    /**
     * 
     * @param aspectRatio
     * @return
     */
    public abstract double minPlusSigmaY ( double aspectRatio );

    /**
     * 
     * @return
     */
    double minX ();

    /**
     * 
     * @return
     */
    double minY ();

    /**
     * 
     * @param leftSeg
     */
    void setLeftSeg ( ParametricCurveSegmentI leftSeg );

    /**
     * 
     * @param maxT
     */
    void setMaxT ( double maxT );

    /**
     * 
     * @param minT
     */
    void setMinT ( double minT );

    /**
     * 
     * @param rightSeg
     */
    void setRightSeg ( ParametricCurveSegmentI rightSeg );

    /**
     * 
     * @return
     */
    double controlX ();

    /**
     * 
     * @return
     */
    double controlY ();

    /**
     * 
     * @param theT
     * @return
     */
    double theConcordiaSlope ( double theT );

    /**
     * 
     * @param aspectRatio
     * @return
     */
    double controlUpperX ( double aspectRatio );

    /**
     * 
     * @param aspectRatio
     * @return
     */
    double controlUpperY ( double aspectRatio );

    /**
     * 
     * @param aspectRatio
     * @return
     */
    double controlLowerX ( double aspectRatio );

    /**
     * 
     * @param aspectRatio
     * @return
     */
    double controlLowerY ( double aspectRatio );

    /**
     * 
     * @param theT
     * @return
     */
    public double theSlope ( double theT );
}
