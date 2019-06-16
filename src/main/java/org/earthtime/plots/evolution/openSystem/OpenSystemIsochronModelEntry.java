/*
 * Copyright 2019 CIRDLES.
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
package org.earthtime.plots.evolution.openSystem;

import java.io.Serializable;

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class OpenSystemIsochronModelEntry implements Serializable {

    // Class variables
    private static final long serialVersionUID = 8968177089497421791L;

    // attributes
    private double ageInKa;
    private double pctLoss;
    private double rStart;
    private double rEnd;

    public OpenSystemIsochronModelEntry() {
        this(0, 0, 0, 0);
    }

    public OpenSystemIsochronModelEntry(double ageInKa, double pctLoss, double rStart, double rEnd) {
        this.ageInKa = ageInKa;
        this.pctLoss = pctLoss;
        this.rStart = rStart;
        this.rEnd = rEnd;
    }

    /**
     * @return the ageInKa
     */
    public double getAgeInKa() {
        return ageInKa;
    }

    /**
     * @param ageInKa the ageInKa to set
     */
    public void setAgeInKa(double ageInKa) {
        this.ageInKa = ageInKa;
    }

    /**
     * @return the pctLoss
     */
    public double getPctLoss() {
        return pctLoss;
    }

    /**
     * @param pctLoss the pctLoss to set
     */
    public void setPctLoss(double pctLoss) {
        this.pctLoss = pctLoss;
    }

    /**
     * @return the rStart
     */
    public double getrStart() {
        return rStart;
    }

    /**
     * @param rStart the rStart to set
     */
    public void setrStart(double rStart) {
        this.rStart = rStart;
    }

    /**
     * @return the rEnd
     */
    public double getrEnd() {
        return rEnd;
    }

    /**
     * @param rEnd the rEnd to set
     */
    public void setrEnd(double rEnd) {
        this.rEnd = rEnd;
    }

}
