/*
 * UPbFractionI.java
 *
 * Created on 21 February 2010
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
package org.earthtime.UPb_Redux.fractions.UPbReduxFractions;

import java.awt.geom.Path2D;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;

/**
 *
 * @author James F. Bowring
 */
public interface UPbFractionI {

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
    abstract String getRatioType();

    /**
     *
     * @param RatioType
     */
    abstract void setRatioType(String RatioType);

    /**
     *
     * @return
     */
    abstract boolean isChanged();

    /**
     *
     * @param changed
     */
    abstract void setChanged(boolean changed);

    /**
     *
     * @return
     */
    abstract int getAliquotNumber();

    /**
     *
     * @param aliquotNumber
     */
    abstract void setAliquotNumber(int aliquotNumber);

    /**
     *
     * @return
     */
    abstract AbstractRatiosDataModel getPhysicalConstantsModel();

    /**
     *
     * @param physicalConstantsModel
     */
    abstract void setPhysicalConstantsModel(AbstractRatiosDataModel physicalConstantsModel);

    /**
     *
     * @return
     */
    abstract Object[] getFractionTableRowData();

    /**
     *
     * @return
     */
    abstract String getFractionNotes();

    /**
     *
     * @param fractionNotes
     */
    abstract void setFractionNotes(String fractionNotes);

    /**
     *
     * @return
     */
    abstract boolean isRejected();

    /**
     *
     * @param rejected
     */
    abstract void setRejected(boolean rejected);

    /**
     *
     */
    abstract void toggleRejectedStatus();

    /**
     *
     */
    abstract void setSavedFractionationModels();

    /**
     *
     * @return
     */
    abstract boolean isDeleted();

    /**
     *
     * @param deleted
     */
    abstract void setDeleted(boolean deleted);

    /**
     *
     * @return
     */
    abstract Path2D getErrorEllipsePath();

    /**
     *
     * @param errorEllipsePath
     */
    abstract void setErrorEllipsePath(Path2D errorEllipsePath);

    /**
     *
     * @return
     */
    abstract double getEllipseRho();

    /**
     *
     * @param ellipseRho
     */
    abstract void setEllipseRho(double ellipseRho);

    /**
     *
     * @param cmName
     * @return
     */
    abstract ValueModel getCompositionalMeasureByName(String cmName);

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
//    abstract String getTracerType();

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
     * @return
     */
    abstract AbstractRatiosDataModel getInitialPbModel();

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

    /**
     *
     * @return
     */
    abstract boolean isFiltered();

    /**
     *
     * @param rejected
     */
    abstract void setFiltered(boolean rejected);

    /**
     *
     * @return
     */
    abstract boolean isFractionationCorrectedU();

    /**
     *
     * @return
     */
    abstract boolean isFractionationCorrectedPb();

    /**
     *
     * @return
     */
    abstract boolean isZircon();

    /**
     *
     * @param selectedInDataTable
     */
    abstract void setSelectedInDataTable(boolean selectedInDataTable);

    /**
     *
     * @return
     */
    public boolean isStandard();

    /**
     *
     * @param standard
     */
    public void setStandard(boolean standard);

    /**
     *
     */
    public void calculateTeraWasserburgRho();

    public boolean isCommonLeadLossCorrected();

    public boolean hasXMLUSourceFile();

    public boolean hasXMLPbSourceFile();

    public boolean isAnOxide();
}
