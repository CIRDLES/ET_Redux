/*
 * FractionI.java
 *
 * Created on August 2, 2007, 10:05 AM
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
package org.earthtime.UPb_Redux.fractions;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.concurrent.ConcurrentMap;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.UPb_Redux.reduxLabData.ReduxLabData;
import org.earthtime.UPb_Redux.utilities.comparators.IntuitiveStringComparator;
import org.earthtime.UPb_Redux.valueModels.MeasuredRatioModel;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.dataDictionaries.Lambdas;
import org.earthtime.dataDictionaries.MeasuredRatios;
import org.earthtime.dataDictionaries.RadDates;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataModels.initialPbModelsET.StaceyKramersInitialPbModelET;

/**
 *
 * @author James F. Bowring
 */
public interface FractionI {

    // accessors
    /**
     *
     * @return
     */
    abstract String getSampleName();

    /**
     *
     * @param sampleName
     */
    abstract void setSampleName(String sampleName);

    /**
     *
     * @return
     */
    abstract String getFractionID();

    /**
     *
     * @param fractionID
     */
    abstract void setFractionID(String fractionID);

    /**
     *
     * @return
     */
    abstract String getImageURL();

    /**
     *
     * @param imageURL
     */
    abstract void setImageURL(String imageURL);

    /**
     *
     * @return
     */
    abstract Date getTimeStamp();

    /**
     *
     * @param timeStamp
     */
    abstract void setTimeStamp(Date timeStamp);

    /**
     *
     * @return
     */
    abstract String getMineralName();

    /**
     *
     * @param mineralName
     */
    abstract void setMineralName(String mineralName);

    /**
     *
     * @return
     */
    abstract String getSettingType();

    /**
     *
     * @param settingType
     */
    abstract void setSettingType(String settingType);

    /**
     *
     * @return
     */
    abstract int getNumberOfGrains();

    /**
     *
     * @param numberOfGrains
     */
    abstract void setNumberOfGrains(int numberOfGrains);

    /**
     *
     * @return
     */
    abstract BigDecimal getEstimatedDate();

    /**
     *
     * @param estimatedAge
     */
    abstract void setEstimatedDate(BigDecimal estimatedAge);

    /**
     *
     * @return
     */
    abstract boolean isPhysicallyAbraded();

    /**
     *
     * @param physicallyAbraded
     */
    abstract void setPhysicallyAbraded(boolean physicallyAbraded);

    /**
     *
     * @return
     */
    abstract boolean isLeachedInHFAcid();

    /**
     *
     * @param leachedInHFAcid
     */
    abstract void setLeachedInHFAcid(boolean leachedInHFAcid);

    /**
     *
     * @return
     */
    abstract boolean isAnnealedAndChemicallyAbraded();

    /**
     *
     * @param annealedAndChemicallyAbraded
     */
    abstract void setAnnealedAndChemicallyAbraded(boolean annealedAndChemicallyAbraded);

    /**
     *
     * @return
     */
    abstract boolean isChemicallyPurifiedUPb();

    /**
     *
     * @param chemicallyPurifiedUPb
     */
    abstract void setChemicallyPurifiedUPb(boolean chemicallyPurifiedUPb);

    /**
     *
     * @return
     */
    abstract String getAnalysisFractionComment();

    /**
     *
     * @param analysisFractionComment
     */
    abstract void setAnalysisFractionComment(String analysisFractionComment);

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

    /**
     *
     * @return
     */
    abstract String getTracerID();

    /**
     *
     * @param tracerID
     */
    abstract void setTracerID(String tracerID);

    /**
     *
     * @return
     */
    abstract AbstractRatiosDataModel getInitialPbModel();

    /**
     *
     * @param initialPbModel
     */
    abstract void setInitialPbModel(AbstractRatiosDataModel initialPbModel);

    /**
     *
     * @return
     */
    abstract String getPbCollectorType();

    /**
     *
     * @param pbCollectorType
     */
    abstract void setPbCollectorType(String pbCollectorType);

    /**
     *
     * @return
     */
    abstract String getUCollectorType();

    /**
     *
     * @param uCollectorType
     */
    abstract void setUCollectorType(String uCollectorType);

    /**
     *
     * @return
     */
    abstract ValueModel[] getMeasuredRatios();

    /**
     *
     * @param measuredRatio
     */
    abstract void setMeasuredRatios(ValueModel[] measuredRatio);

    /**
     *
     * @return
     */
    abstract ValueModel[] getRadiogenicIsotopeRatios();

    /**
     *
     * @param radiogenicIsotopeRatios
     */
    abstract void setRadiogenicIsotopeRatios(ValueModel[] radiogenicIsotopeRatios);

    /**
     *
     * @return
     */
    abstract ValueModel[] getCompositionalMeasures();

    /**
     *
     * @param compositionalMeasures
     */
    abstract void setCompositionalMeasures(ValueModel[] compositionalMeasures);

    /**
     *
     * @param ratioName
     * @return
     */
    abstract ValueModel getMeasuredRatioByName(String ratioName);

    /**
     *
     * @param myMeasuredRatio
     * @return
     */
    abstract ValueModel getMeasuredRatioByName(MeasuredRatios myMeasuredRatio);

    /**
     * @return the parDerivTerms
     */
    public ConcurrentMap<String, BigDecimal> getParDerivTerms();

    /**
     * @param parDerivTerms the parDerivTerms to set
     */
    public void setParDerivTerms(ConcurrentMap<String, BigDecimal> parDerivTerms);

    /**
     *
     * @param amName
     * @return
     */
    public default ValueModel getAnalysisMeasure(String amName) {
        ValueModel amModel = null;
        for (int i = 0; i < getAnalysisMeasures().length; i++) {
            if (getAnalysisMeasures()[i].getName().equalsIgnoreCase(amName)) {
                amModel = getAnalysisMeasures()[i];
            }
        }

        if (amModel == null) {
            // return a new model - handles backwards compatible
            // have to add element to array
            ValueModel[] temp = new ValueModel[getAnalysisMeasures().length + 1];
            System.arraycopy(getAnalysisMeasures(), 0, temp, 0, getAnalysisMeasures().length);

            amModel
                    = new ValueModel(amName,
                            BigDecimal.ZERO,
                            "ABS",
                            BigDecimal.ZERO, BigDecimal.ZERO);

            temp[getAnalysisMeasures().length] = amModel;

            setAnalysisMeasures(temp);
        }

        return amModel;
    }

    /**
     *
     * @return
     */
    public ValueModel[] getAnalysisMeasures();

    /**
     *
     * @param analysisMeasures
     */
    public void setAnalysisMeasures(ValueModel[] analysisMeasures);

    /**
     *
     * @param riaName
     * @param valueModel
     */
    public default void setRadiogenicIsotopeDateByName(String riaName, ValueModel valueModel) {
        // make sure it exists
        getRadiogenicIsotopeDateByName(riaName.trim());

        for (int i = 0; i < getRadiogenicIsotopeDates().length; i++) {
            if (getRadiogenicIsotopeDates()[i].getName().equalsIgnoreCase(riaName.trim())) {
                getRadiogenicIsotopeDates()[i] = valueModel;
            }
        }
    }

    /**
     *
     * @param riaName
     * @param valueModel
     */
    public default void setRadiogenicIsotopeDateByName(RadDates riaName, ValueModel valueModel) {
        setRadiogenicIsotopeDateByName(riaName.getName(), valueModel);
    }

    /**
     *
     * @param ratioName
     * @return
     */
    public default ValueModel getRadiogenicIsotopeDateByName(String ratioName) {
        for (ValueModel radiogenicIsotopeDate : getRadiogenicIsotopeDates()) {
            if (radiogenicIsotopeDate.getName().equalsIgnoreCase(ratioName.trim())) {
                return radiogenicIsotopeDate;
            }
        }

        // return a new model - handles backwards compatible
        // have to add element to array
        ValueModel[] temp = new ValueModel[getRadiogenicIsotopeDates().length + 1];
        System.arraycopy(getRadiogenicIsotopeDates(), 0, temp, 0, getRadiogenicIsotopeDates().length);

        ValueModel riaModel
                = new ValueModel(ratioName.trim(),
                        BigDecimal.ZERO,
                        "ABS",
                        BigDecimal.ZERO, BigDecimal.ZERO);

        temp[getRadiogenicIsotopeDates().length] = riaModel;

        setRadiogenicIsotopeDates(temp);

        return riaModel;

    }

    /**
     *
     * @param ratioName
     * @return
     */
    public default ValueModel getRadiogenicIsotopeDateByName(RadDates ratioName) {
        return getRadiogenicIsotopeDateByName(ratioName.getName());
    }

    /**
     *
     * @return
     */
    public ValueModel[] getRadiogenicIsotopeDates();

    /**
     *
     * @param radiogenicIsotopeDates
     */
    public void setRadiogenicIsotopeDates(ValueModel[] radiogenicIsotopeDates);

    /**
     *
     * @param riaName
     * @param valueModel
     */
    public default void setRadiogenicIsotopeRatioByName(String riaName, ValueModel valueModel) {
        // make sure it exists
        getRadiogenicIsotopeRatioByName(riaName.trim());

        for (int i = 0; i < getRadiogenicIsotopeRatios().length; i++) {
            if (getRadiogenicIsotopeRatios()[i].getName().equalsIgnoreCase(riaName.trim())) {
                getRadiogenicIsotopeRatios()[i] = valueModel;
            }
        }
    }

    /**
     *
     * @param ratioName
     * @return
     */
    public default ValueModel getRadiogenicIsotopeRatioByName(String ratioName) {
        for (ValueModel radiogenicIsotopeRatio : getRadiogenicIsotopeRatios()) {
            if (radiogenicIsotopeRatio.getName().equalsIgnoreCase(ratioName.trim())) {
                return radiogenicIsotopeRatio;
            }
        }

        // return a new model - handles backwards compatible
        // have to add element to array
        ValueModel[] temp = new ValueModel[getRadiogenicIsotopeRatios().length + 1];
        System.arraycopy(getRadiogenicIsotopeRatios(), 0, temp, 0, getRadiogenicIsotopeRatios().length);

        ValueModel rirModel
                = new ValueModel(ratioName.trim(),
                        ratioName.startsWith("rho") ?//
                                new BigDecimal(ReduxConstants.NO_RHO_FLAG, ReduxConstants.mathContext15) //
                                : BigDecimal.ZERO,// June 2010 to force out of range of legal cov [-1,,,1]0.0;
                        "ABS",
                        BigDecimal.ZERO, BigDecimal.ZERO);

        temp[getRadiogenicIsotopeRatios().length] = rirModel;

        setRadiogenicIsotopeRatios(temp);

        return rirModel;
    }

    /**
     * @return the isLegacy
     */
    public boolean isLegacy();

    /**
     *
     */
    public default void calculateStaceyKramersInitialPbModelValues() {
        if (getInitialPbModel() instanceof StaceyKramersInitialPbModelET) {

            AbstractRatiosDataModel physicalConstantsModel;
            try {
                physicalConstantsModel = //
                        ReduxLabData.getInstance().getAPhysicalConstantsModel(getPhysicalConstantsModelID());

                ((StaceyKramersInitialPbModelET) getInitialPbModel()).calculateRatios(
                        getEstimatedDate(),
                        physicalConstantsModel.getDatumByName(Lambdas.lambda238.getName()).getValue(),
                        physicalConstantsModel.getDatumByName(Lambdas.lambda235.getName()).getValue(),
                        physicalConstantsModel.getDatumByName(Lambdas.lambda232.getName()).getValue());

                // set ratio uncertainties based on pct uncertainty
                ((StaceyKramersInitialPbModelET) getInitialPbModel()).calculateUncertaintiesAndRhos(getStaceyKramersOnePctUnct(),
                        getStaceyKramersCorrelationCoeffs(), BigDecimal.ZERO, BigDecimal.ZERO);
            } catch (BadLabDataException badLabDataException) {
            }

            getInitialPbModel().initializeModel();
        }
    }

    /**
     *
     * @return
     */
    public String getPhysicalConstantsModelID();

    /**
     * @return the staceyKramersCorrelationCoeffs
     */
    public BigDecimal getStaceyKramersCorrelationCoeffs();

    /**
     * @return the staceyKramersOnePctUnct
     */
    public BigDecimal getStaceyKramersOnePctUnct();

    /**
     *
     * @return
     */
    public boolean isZircon();

    /**
     *
     * @param zircon
     */
    public void setZircon(boolean zircon);

    /**
     *
     * @param amName
     * @param valueModel
     */
    public default void setAnalysisMeasureByName(String amName, ValueModel valueModel) {
        // make sure it exists
        getAnalysisMeasure(amName.trim());
        for (int i = 0; i < getAnalysisMeasures().length; i++) {
            if (getAnalysisMeasures()[i].getName().equalsIgnoreCase(amName.trim())) {
                getAnalysisMeasures()[i] = valueModel;
            }
        }
    }

    /**
     * @return the fractionationCorrectedPb
     */
    public boolean isFractionationCorrectedPb();

    /**
     * @param fractionationCorrectedPb the fractionationCorrectedPb to set
     */
    public void setFractionationCorrectedPb(boolean fractionationCorrectedPb);

    /**
     *
     * @param sirName
     * @return
     */
    public default ValueModel getSampleIsochronRatiosByName(String sirName) {
        for (int i = 0; i < getSampleIsochronRatios().length; i++) {
            if (getSampleIsochronRatios()[i].getName().equalsIgnoreCase(sirName.trim())) {
                return getSampleIsochronRatios()[i];
            }
        }
        // return a new model - handles backwards compatible
        // have to add element to array
        ValueModel[] temp = new ValueModel[getSampleIsochronRatios().length + 1];
        System.arraycopy(getSampleIsochronRatios(), 0, temp, 0, getSampleIsochronRatios().length);

        ValueModel sirModel
                = new ValueModel(sirName.trim(),
                        BigDecimal.ZERO,
                        "ABS",
                        BigDecimal.ZERO, BigDecimal.ZERO);

        temp[getSampleIsochronRatios().length] = sirModel;

        setSampleIsochronRatios(temp);

        return sirModel;
    }

    /**
     *
     * @param sirName
     * @param valueModel
     */
    public default void setSampleIsochronRatiosByName(String sirName, ValueModel valueModel) {
        // make sure it exists
        getSampleIsochronRatiosByName(sirName.trim());
        //find it
        for (int i = 0; i < getSampleIsochronRatios().length; i++) {
            if (getSampleIsochronRatios()[i].getName().equalsIgnoreCase(sirName.trim())) {
                getSampleIsochronRatios()[i] = valueModel;
            }
        }
    }

    /**
     *
     * @return
     */
    public ValueModel[] getSampleIsochronRatios();

    /**
     *
     * @param sampleIsochronRatios
     */
    public void setSampleIsochronRatios(ValueModel[] sampleIsochronRatios);

    /**
     * @return the fractionationCorrectedU
     */
    public boolean isFractionationCorrectedU();

    /**
     * @param fractionationCorrectedU the fractionationCorrectedU to set
     */
    public void setFractionationCorrectedU(boolean fractionationCorrectedU);

    /**
     *
     * @param cmName
     * @return
     */
    public default ValueModel getCompositionalMeasureByName(String cmName) {
        for (int i = 0; i < getCompositionalMeasures().length; i++) {
            if (getCompositionalMeasures()[i].getName().equalsIgnoreCase(cmName.trim())) {
                return getCompositionalMeasures()[i];
            }
        }
        // return a new model - handles backwards compatible
        // have to add element to array
        ValueModel[] temp = new ValueModel[getCompositionalMeasures().length + 1];
        System.arraycopy(getCompositionalMeasures(), 0, temp, 0, getCompositionalMeasures().length);

        ValueModel crModel
                = new ValueModel(cmName.trim(),
                        BigDecimal.ZERO,
                        "ABS",
                        BigDecimal.ZERO, BigDecimal.ZERO);

        temp[getCompositionalMeasures().length] = crModel;

        setCompositionalMeasures(temp);

        return crModel;
    }

    /**
     *
     * @param cmName
     * @param valueModel
     */
    public default void setCompositionalMeasureByName(String cmName, ValueModel valueModel) {
        // make sure it exists
        getCompositionalMeasureByName(cmName.trim());
        //find it
        for (int i = 0; i < getCompositionalMeasures().length; i++) {
            if (getCompositionalMeasures()[i].getName().equalsIgnoreCase(cmName.trim())) {
                getCompositionalMeasures()[i] = valueModel;
            }
        }
    }

    /**
     * @return the rgbColor
     */
    public int getRgbColor();

    /**
     * @param rgbColor the rgbColor to set
     */
    public void setRgbColor(int rgbColor);

    /**
     * @param isLegacy the isLegacy to set
     */
    public void setIsLegacy(boolean isLegacy);

    static final Comparator FRACTION_ID_ORDER = (Object f1, Object f2) -> {
        String fractionTwoID = ((FractionI) f2).getFractionID().trim();
        String fractionOneID = ((FractionI) f1).getFractionID().trim();

        // oct 2010 put here
        Comparator<String> forNoah = new IntuitiveStringComparator<>();
        return forNoah.compare(fractionOneID, fractionTwoID);
    };

    /**
     * @return the grainID
     */
    public String getGrainID();

    /**
     * @param grainID the grainID to set
     */
    public void setGrainID(String grainID);

    /**
     *
     * @return
     */
    public default ValueModel[] copyAnalysisMeasures() {
        ValueModel[] retval = new ValueModel[getAnalysisMeasures().length];
        for (int i = 0; i < retval.length; i++) {
            retval[i] = getAnalysisMeasures()[i].copy();
        }
        return retval;
    }

    /**
     *
     * @return
     */
    public default ValueModel[] copyCompositionalMeasures() {
        ValueModel[] retval = new ValueModel[getCompositionalMeasures().length];
        for (int i = 0; i < retval.length; i++) {
            retval[i] = getCompositionalMeasures()[i].copy();
        }
        return retval;
    }

    /**
     *
     * @return
     */
    public default ValueModel[] copyMeasuredRatios() {
        ValueModel[] retval = new MeasuredRatioModel[getMeasuredRatios().length];
        for (int i = 0; i < retval.length; i++) {
            retval[i] = getMeasuredRatios()[i].copy();
        }
        return retval;
    }

    /**
     *
     * @return
     */
    public default ValueModel[] copyRadiogenicIsotopeDates() {
        ValueModel[] retval = new ValueModel[getRadiogenicIsotopeDates().length];
        for (int i = 0; i < retval.length; i++) {
            retval[i] = getRadiogenicIsotopeDates()[i].copy();
        }
        return retval;
    }

    /**
     *
     * @return
     */
    public default ValueModel[] copyRadiogenicIsotopeRatios() {
        ValueModel[] retval = new ValueModel[getRadiogenicIsotopeRatios().length];
        for (int i = 0; i < retval.length; i++) {
            retval[i] = getRadiogenicIsotopeRatios()[i].copy();
        }
        return retval;
    }

    /**
     *
     * @return
     */
    public default ValueModel[] copySampleIsochronRatios() {
        ValueModel[] retval = new ValueModel[getSampleIsochronRatios().length];
        for (int i = 0; i < retval.length; i++) {
            retval[i] = getSampleIsochronRatios()[i].copy();
        }
        return retval;
    }

    /**
     *
     * @return
     */
    public default ValueModel[] copyTraceElements() {
        initializeTraceElements();

        ValueModel[] retval = new ValueModel[getTraceElements().length];
        for (int i = 0; i < retval.length; i++) {
            retval[i] = getTraceElements()[i].copy();
        }
        return retval;
    }

    /**
     * @return the traceElements
     */
    public ValueModel[] getTraceElements();

    /**
     * @param traceElements the traceElements to set
     */
    public void setTraceElements(ValueModel[] traceElements);

    /**
     *
     * @param tmName
     * @return
     */
    public default ValueModel getTraceElementByName(String tmName) {
        initializeTraceElements();
        for (ValueModel traceElement : getTraceElements()) {
            if (traceElement.getName().equalsIgnoreCase(tmName.trim())) {
                return traceElement;
            }
        }
        // return a new model - handles backwards compatible
        // have to add element to array
        ValueModel[] temp = new ValueModel[getTraceElements().length + 1];
        System.arraycopy(getTraceElements(), 0, temp, 0, getTraceElements().length);

        ValueModel trModel
                = new ValueModel(tmName.trim(),
                        BigDecimal.ZERO,
                        "ABS",
                        BigDecimal.ZERO, BigDecimal.ZERO);

        temp[getTraceElements().length] = trModel;

        setTraceElements(temp);

        return trModel;
    }

    /*
     needed summer 2014 for backward compatibility
     */
    public void initializeTraceElements();

    /**
     * @param staceyKramersOnePctUnct the staceyKramersOnePctUnct to set
     */
    public void setStaceyKramersOnePctUnct(BigDecimal staceyKramersOnePctUnct);

    /**
     * @param staceyKramersCorrelationCoeffs the staceyKramersCorrelationCoeffs
     * to set
     */
    public void setStaceyKramersCorrelationCoeffs(BigDecimal staceyKramersCorrelationCoeffs);
    
    /**
     *
     * @param fraction
     * @param copyAnalysisMeasures
     */
    public void getValuesFrom(FractionI fraction, boolean copyAnalysisMeasures) ;
    
    
    /**
     *
     * @return
     */
    public String getAlphaPbModelID() ;

    /**
     *
     * @param alphaPbModelID
     */
    public void setAlphaPbModelID(String alphaPbModelID) ;

    /**
     *
     * @return
     */
    public String getAlphaUModelID();

    /**
     *
     * @param alphaUModelID
     */
    public void setAlphaUModelID(String alphaUModelID);

}
