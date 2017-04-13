/*
 * UPbFractionI.java
 *
 * Created on 21 February 2010
 *
 *
 * Copyright 2006-2017 James F. Bowring and www.Earth-Time.org
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
package org.earthtime.UPb_Redux.fractions.UPbReduxFractions;

import org.earthtime.Tripoli.fractions.TripoliFraction;
import org.earthtime.UPb_Redux.fractions.FractionI;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;

/**
 *
 * @author James F. Bowring
 */
public interface UPbFractionI extends FractionI {

    /**
     *
     */
    public final static String[] columnNames = {
        "Aliquot", // for button that opens aliquot
        "Incl",//uded", // oct 2009 checkbox for toggling fraction selection
        "Note",
        "Fraction",
        "206/204",
        "206/207",
        "206/208",
        "206/205",
        "207/205",
        "208/205",
        "202/205",
        "238/235",
        "233/235",
        "233/236",
        "Tracer"
    };

    /**
     *
     * @return
     */
    abstract Object[] getFractionTableRowData();

    /**
     *
     */
    abstract void setSavedFractionationModels();

    /**
     *
     * @return
     */
    abstract AbstractRatiosDataModel getPbBlank();

    /**
     *
     * @param pbBlank
     */
    abstract void setPbBlank(AbstractRatiosDataModel pbBlank);

    /**
     *
     * @return
     */
    abstract AbstractRatiosDataModel getTracer();

    /**
     *
     * @param Tracer
     */
    abstract void setTracer(AbstractRatiosDataModel Tracer);

    /**
     *
     * @return
     */
    abstract ValueModel getAlphaPbModel();

    /**
     *
     * @param alphaPbModel
     */
    abstract void setAlphaPbModel(ValueModel alphaPbModel);

    /**
     *
     * @return
     */
    abstract ValueModel getAlphaUModel();

    /**
     *
     * @param alphaUModel
     */
    abstract void setAlphaUModel(ValueModel alphaUModel);

    /**
     *
     * @param ratioName
     * @return
     */
    abstract ValueModel getRadiogenicIsotopeDateWithTracerUnctByName(String ratioName);

    /**
     *
     * @param ratioName
     * @return
     */
    abstract ValueModel getRadiogenicIsotopeDateWithAllUnctByName(String ratioName);

    /**
     *
     * @return
     */
    abstract boolean hasMeasuredLead();

    /**
     *
     * @return
     */
    abstract boolean hasMeasuredUranium();
//
//    /**
//     *
//     * @return
//     */
//    abstract boolean isFiltered();
//
//    /**
//     *
//     * @param rejected
//     */
//    abstract void setFiltered(boolean rejected);

    /**
     * @param fractionationCorrectedPb the fractionationCorrectedPb to set
     */
    public void setFractionationCorrectedPb(boolean fractionationCorrectedPb);

    /**
     *
     */
    public void calculateTeraWasserburgRho();

    public boolean isCommonLeadLossCorrected();

    public boolean hasXMLUSourceFile();

    public boolean hasXMLPbSourceFile();

    public boolean isAnOxide();

    /**
     *
     * @return
     */
    abstract String getPbBlankID();

    /**
     *
     * @param pbBlankID
     */
    abstract void setPbBlankID(String pbBlankID);
    
    public TripoliFraction getTripoliFraction();
    public void setTripoliFraction(TripoliFraction tripoliFraction);
}
