/*
 * SessionSplineRangeOD.java
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
package org.earthtime.Tripoli.dataViews.fitFunctionViews;

import java.io.Serializable;
import java.util.SortedMap;
import java.util.TreeMap;
import org.earthtime.Tripoli.fitFunctions.AbstractFunctionOfX;

/**
 *
 * @author James F. Bowring
 */
public class SessionSplineRangeOD implements Serializable {

    // Class variables
    private static final long serialVersionUID = 4816765602640647347L;

    private SortedMap<Double, AbstractFunctionOfX> sessionOfStandardsSplinesWithOD;
    private double startOD;
    private double stepOD;
    private double stopOD;

    /**
     *
     */
    public SessionSplineRangeOD() {
        this.sessionOfStandardsSplinesWithOD = new TreeMap<>();
        this.startOD = 0.0;
        this.stepOD = 0.0;
        this.stopOD = 0.0;
    }

    /**
     *
     * @param sessionOfStandardsSplinesWithOD
     * @param startOD
     * @param stepOD
     * @param stopOD
     */
    public SessionSplineRangeOD(SortedMap<Double, AbstractFunctionOfX> sessionOfStandardsSplinesWithOD, double startOD, double stepOD, double stopOD) {
        this.sessionOfStandardsSplinesWithOD = sessionOfStandardsSplinesWithOD;
        this.startOD = startOD;
        this.stepOD = stepOD;
        this.stopOD = stopOD;
    }

    /**
     *
     * @return
     */
    public AbstractFunctionOfX getLastSessionSplineFofX() {
        return sessionOfStandardsSplinesWithOD.get(sessionOfStandardsSplinesWithOD.lastKey());
    }

    /**
     *
     * @param key
     * @param spline
     */
    public void putSplineInSessionSplines(Double key, AbstractFunctionOfX spline) {
        sessionOfStandardsSplinesWithOD.put(key, spline);
    }

    /**
     * @return the sessionOfStandardsSplinesWithOD
     */
    public SortedMap<Double, AbstractFunctionOfX> getSessionOfStandardsSplinesWithOD() {
        return sessionOfStandardsSplinesWithOD;
    }

    /**
     * @param sessionOfStandardsSplinesWithOD the
     * sessionOfStandardsSplinesWithOD to set
     */
    public void setSessionOfStandardsSplinesWithOD(SortedMap<Double, AbstractFunctionOfX> sessionOfStandardsSplinesWithOD) {
        this.sessionOfStandardsSplinesWithOD = sessionOfStandardsSplinesWithOD;
    }

    /**
     * @return the startOD
     */
    public double getStartOD() {
        return startOD;
    }

    /**
     * @param startOD the startOD to set
     */
    public void setStartOD(double startOD) {
        this.startOD = startOD;
    }

    /**
     * @return the stepOD
     */
    public double getStepOD() {
        return stepOD;
    }

    /**
     * @param stepOD the stepOD to set
     */
    public void setStepOD(double stepOD) {
        this.stepOD = stepOD;
    }

    /**
     * @return the stopOD
     */
    public double getStopOD() {
        return stopOD;
    }

    /**
     * @param stopOD the stopOD to set
     */
    public void setStopOD(double stopOD) {
        this.stopOD = stopOD;
    }
}
