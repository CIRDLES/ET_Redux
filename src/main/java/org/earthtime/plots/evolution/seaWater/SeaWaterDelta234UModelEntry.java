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
package org.earthtime.plots.evolution.seaWater;

import java.io.Serializable;

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class SeaWaterDelta234UModelEntry implements Serializable {

    // Class variables
    private static final long serialVersionUID = -1572136942180250643L;

    protected double ageInKa;
    protected double delta234UPerMil;
    protected double oneSigmaAbsUnct;

    public SeaWaterDelta234UModelEntry() {
        ageInKa = -1;
        delta234UPerMil = 145;
        oneSigmaAbsUnct = 1;
    }

    public SeaWaterDelta234UModelEntry(double ageInKa, double delta234UPerMil, double oneSigmaAbsUnct) {
        this.ageInKa = ageInKa;
        this.delta234UPerMil = delta234UPerMil;
        this.oneSigmaAbsUnct = oneSigmaAbsUnct;
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
     * @return the delta234UPerMil
     */
    public double getDelta234UPerMil() {
        return delta234UPerMil;
    }

    /**
     * @param delta234UPerMil the delta234UPerMil to set
     */
    public void setDelta234UPerMil(double delta234UPerMil) {
        this.delta234UPerMil = delta234UPerMil;
    }

    /**
     * @return the oneSigmaAbsUnct
     */
    public double getOneSigmaAbsUnct() {
        return oneSigmaAbsUnct;
    }

    /**
     * @param oneSigmaAbsUnct the oneSigmaAbsUnct to set
     */
    public void setOneSigmaAbsUnct(double oneSigmaAbsUnct) {
        this.oneSigmaAbsUnct = oneSigmaAbsUnct;
    }

}
