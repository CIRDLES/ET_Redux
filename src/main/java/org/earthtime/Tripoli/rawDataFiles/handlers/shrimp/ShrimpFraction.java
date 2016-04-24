/*
 * Copyright 2006-2016 CIRDLES.org.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.earthtime.Tripoli.rawDataFiles.handlers.shrimp;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public class ShrimpFraction implements Serializable {

    private String name;
    private int spotNumber;
    private long dateTimeMilliseconds;
    private List<IsotopeRatioModelSHRIMP> isotopicRatios;
    private double[][] extractedRunData;
    private double[] totalCps;

    public ShrimpFraction() {
    }

    public ShrimpFraction(List<IsotopeRatioModelSHRIMP> isotopicRatios) {
        this.isotopicRatios = isotopicRatios;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the spotNumber
     */
    public int getSpotNumber() {
        return spotNumber;
    }

    /**
     * @param spotNumber the spotNumber to set
     */
    public void setSpotNumber(int spotNumber) {
        this.spotNumber = spotNumber;
    }

    /**
     * @return the dateTimeMilliseconds
     */
    public long getDateTimeMilliseconds() {
        return dateTimeMilliseconds;
    }

    /**
     * @param dateTimeMilliseconds the dateTimeMilliseconds to set
     */
    public void setDateTimeMilliseconds(long dateTimeMilliseconds) {
        this.dateTimeMilliseconds = dateTimeMilliseconds;
    }

    /**
     * @return the isotopicRatios
     */
    public List<IsotopeRatioModelSHRIMP> getIsotopicRatios() {
        return isotopicRatios;
    }

    /**
     * @param isotopicRatios the isotopicRatios to set
     */
    public void setIsotopicRatios(List<IsotopeRatioModelSHRIMP> isotopicRatios) {
        this.isotopicRatios = isotopicRatios;
    }

    /**
     * @return the extractedRunData
     */
    public double[][] getExtractedRunData() {
        return extractedRunData;
    }

    /**
     * @param extractedRunData the extractedRunData to set
     */
    public void setExtractedRunData(double[][] extractedRunData) {
        this.extractedRunData = extractedRunData;
    }

    /**
     * @return the totalCps
     */
    public double[] getTotalCps() {
        return totalCps;
    }

    /**
     * @param totalCps the totalCps to set
     */
    public void setTotalCps(double[] totalCps) {
        this.totalCps = totalCps;
    }

}
