/*
 * AliquotI.java
 *
 * Created on July 23, 2007, 12:52 PM
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
package org.earthtime.aliquots;

import java.math.BigDecimal;
import java.util.Vector;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.aliquots.UPbReduxAliquot;
import org.earthtime.UPb_Redux.fractions.Fraction;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFractionI;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;

/**
 *
 * @author James F. Bowring
 */
public interface AliquotI {

    // Field accessors
    /**
     *
     * @return
     */
    abstract String getSampleIGSN();

    /**
     *
     * @param sampleIGSN
     */
    abstract void setSampleIGSN(String sampleIGSN);

    /**
     *
     * @return
     */
    abstract String getAliquotIGSN();

    /**
     *
     * @param aliquotIGSN
     */
    abstract void setAliquotIGSN(String aliquotIGSN);

    /**
     *
     * @return
     */
    abstract String getLaboratoryName();

    /**
     *
     * @param laboratoryName
     */
    abstract void setLaboratoryName(String laboratoryName);

    /**
     *
     * @return
     */
    abstract String getAnalystName();

    /**
     *
     * @param analystName
     */
    abstract void setAnalystName(String analystName);

    /**
     *
     * @return
     */
    abstract String getAliquotReference();

    /**
     *
     * @param aliquotReference
     */
    abstract void setAliquotReference(String aliquotReference);

    /**
     *
     * @return
     */
    abstract String getAliquotInstrumentalMethod();

    /**
     *
     * @param aliquotInstrumentalMethod
     */
    abstract void setAliquotInstrumentalMethod(String aliquotInstrumentalMethod);

    /**
     *
     * @return
     */
    abstract String getAliquotInstrumentalMethodReference();

    /**
     *
     * @param aliquotInstrumentalMethodReference
     */
    abstract void setAliquotInstrumentalMethodReference(String aliquotInstrumentalMethodReference);

    /**
     *
     * @return
     */
    abstract String getAliquotComment();

    /**
     *
     * @param aliquotComment
     */
    abstract void setAliquotComment(String aliquotComment);

    /**
     *
     * @return
     */
    abstract Vector<ValueModel> getSampleDateModels();

    /**
     *
     * @param sampleAgeModels
     */
    abstract void setSampleDateModels(Vector<ValueModel> sampleAgeModels);

    /**
     *
     * @return
     */
    abstract AbstractRatiosDataModel getPhysicalConstants();

    /**
     *
     * @param physicalConstants
     */
    abstract void setPhysicalConstants(AbstractRatiosDataModel physicalConstants);

    /**
     *
     * @return
     */
    abstract Vector<AbstractRatiosDataModel> getPbBlanks();

    /**
     *
     * @param pbBlanks
     */
    abstract void setPbBlanks(Vector<AbstractRatiosDataModel> pbBlanks);

    /**
     *
     * @return
     */
    abstract Vector<AbstractRatiosDataModel> getTracers();

    /**
     *
     * @param tracers
     */
    abstract void setTracers(Vector<AbstractRatiosDataModel> tracers);

    /**
     *
     * @return
     */
    abstract Vector<AbstractRatiosDataModel> getMineralStandardModels();

    /**
     *
     * @param MineralStandards
     */
    abstract void setMineralStandardModels(Vector<AbstractRatiosDataModel> MineralStandards);

    /**
     *
     * @param aliquot
     */
    public static void toggleAliquotFractionsRejectedStatus(UPbReduxAliquot aliquot) {
        for (int i = 0; i < aliquot.getAliquotFractions().size(); i++) {
            ((UPbFractionI) aliquot.getAliquotFractions().get(i)).toggleRejectedStatus();
        }
    }

    public void setAnalysisPurpose(ReduxConstants.ANALYSIS_PURPOSE analysisPurpose);

    /**
     *
     * @return
     */
    public String getAliquotName();

    /**
     *
     * @param aliquotName
     */
    public void setAliquotName(String aliquotName);

    /**
     *
     * @param sampleDateModelName
     * @return
     */
    public boolean containsSampleDateModelByName(String sampleDateModelName);

    /**
     *
     */
    public void updateSampleDateModels();

    /**
     *
     * @param sampleDateModelName
     * @return
     */
    public ValueModel getSampleDateModelByName(String sampleDateModelName);

    /**
     *
     * @param modelName
     * @return
     */
    public ValueModel getASampleDateModelByName(String modelName);

    /**
     * @return the bestAgeDivider206_238
     */
    public BigDecimal getBestAgeDivider206_238();

    /**
     * @param bestAgeDivider206_238 the bestAgeDivider206_238 to set
     */
    public void setBestAgeDivider206_238(BigDecimal bestAgeDivider206_238);

    /**
     * @return the analysisPurpose
     */
    public ReduxConstants.ANALYSIS_PURPOSE getAnalysisPurpose();

    /**
     * @param keyWordsCSV the keyWordsCSV to set
     */
    public void setKeyWordsCSV(String keyWordsCSV);

    /**
     * @return the keyWordsCSV
     */
    public String getKeyWordsCSV();

    /**
     *
     * @return
     */
    public BigDecimal getCalibrationUnct206_238();

    /**
     *
     * @param calibrationUnct206_238
     */
    public void setCalibrationUnct206_238(BigDecimal calibrationUnct206_238);

    /**
     *
     * @return
     */
    public BigDecimal getCalibrationUnct208_232();

    /**
     *
     * @param calibrationUnct208_232
     */
    public void setCalibrationUnct208_232(BigDecimal calibrationUnct208_232);

    /**
     *
     * @return
     */
    public BigDecimal getCalibrationUnct207_206();

    /**
     *
     * @param calibrationUnct207_206
     */
    public void setCalibrationUnct207_206(BigDecimal calibrationUnct207_206);
    /**
     *
     * @return
     */
    public Vector<ValueModel> determineUnusedSampleDateModels();
        /**
     *
     * @param sampleDateModel
     */
    public void setPreferredSampleDateModel(ValueModel sampleDateModel);
        /**
     *
     * @return
     */
    public Vector<Fraction> getAnalysisFractions();
        public boolean usesIDTIMS() ;
    public boolean usesMCIPMS();
        /**
     *
     * @param tracerNameandVersion
     * @return
     */
    public AbstractRatiosDataModel getATracer(String tracerNameandVersion);
        /**
     *
     * @param pbBlankNameAndVersion
     * @return
     */
    public AbstractRatiosDataModel getAPbBlank(String pbBlankNameAndVersion);
        /**
     *
     * @param alphaPbModelName
     * @return
     */
    public ValueModel getAnAlphaPbModel(String alphaPbModelName);
        /**
     *
     * @param alphaUModelName
     * @return
     */
    public ValueModel getAnAlphaUModel(String alphaUModelName);
}
