/*
 * AliquotInterface.java
 *
 * Created on July 23, 2007, 12:52 PM
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
package org.earthtime.aliquots;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Vector;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.fractions.FractionI;
import org.earthtime.UPb_Redux.valueModels.SampleDateInterceptModel;
import org.earthtime.UPb_Redux.valueModels.SampleDateModel;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.dataDictionaries.SampleDateTypes;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.reduxLabData.ReduxLabData;

/**
 *
 * @author James F. Bowring
 */
public interface AliquotInterface {

    // Field accessors
    abstract int getAliquotNumber();

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
    abstract AbstractRatiosDataModel getPhysicalConstantsModel();

    /**
     *
     * @param physicalConstants
     */
    abstract void setPhysicalConstantsModel(AbstractRatiosDataModel physicalConstants);

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
    public static void toggleAliquotFractionsRejectedStatus(ReduxAliquotInterface aliquot) {
        for (int i = 0; i < aliquot.getAliquotFractions().size(); i++) {
            aliquot.getAliquotFractions().get(i).toggleRejectedStatus();
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
    public default boolean containsSampleDateModelByName(String sampleDateModelName) {
        boolean retVal = false;
        for (ValueModel sam : getSampleDateModels()) {
            if (sam.getName().equalsIgnoreCase(sampleDateModelName.trim())) {
                retVal = true;
            }
        }
        return retVal;
    }

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

    public void setMyReduxLabData(ReduxLabData myReduxLabData);

    /**
     *
     * @return
     */
    public default Vector<ValueModel> determineUnusedSampleDateModels() {
        Vector<ValueModel> retVal = new Vector<>();
        // choose models not already in use by Aliquot
        for (int i = 0; i < SampleDateTypes.getSampleDateModelTypes().length; i++) {
            if (getASampleDateModelByName(SampleDateTypes.getSampleDateType(i)) == null) {
                ValueModel tempModel;
                if (SampleDateTypes.getSampleDateType(i).endsWith("intercept")) {
                    tempModel
                            = new SampleDateInterceptModel(//
                                    SampleDateTypes.getSampleDateType(i),
                                    SampleDateTypes.getSampleDateTypeMethod(i),
                                    SampleDateTypes.getSampleDateTypeName(i),
                                    BigDecimal.ZERO,
                                    "ABS",
                                    BigDecimal.ZERO);
                } else {
                    tempModel
                            = new SampleDateModel(//
                                    SampleDateTypes.getSampleDateType(i),
                                    SampleDateTypes.getSampleDateTypeMethod(i),
                                    SampleDateTypes.getSampleDateTypeName(i),
                                    BigDecimal.ZERO,
                                    "ABS",
                                    BigDecimal.ZERO);
                }
                ((SampleDateModel) tempModel).setAliquot(this);
                retVal.add(tempModel);
            }
        }
        return retVal;
    }

    /**
     *
     * @return
     */
    public Vector<FractionI> getAnalysisFractions();

    /**
     *
     * @param modelNameAndVersion
     * @return
     */
    public default AbstractRatiosDataModel getAMineralStandardModelByName(String modelNameAndVersion) {
        AbstractRatiosDataModel retVal = null;
        for (AbstractRatiosDataModel msm : getMineralStandardModels()) {
            if (msm.getNameAndVersion().equalsIgnoreCase(modelNameAndVersion.trim())) {
                retVal = msm;
            }
        }
        return retVal;
    }

    /**
     *
     * @param modelName
     * @return
     */
    public default ValueModel getASampleDateModelByName(String modelName) {
        ValueModel retVal = null;
        for (ValueModel sdm : getSampleDateModels()) {
            if (sdm.getName().equalsIgnoreCase(modelName.trim())) {
                retVal = sdm;
            }
        }
        return retVal;
    }

    /**
     *
     * @param tracerNameandVersion
     * @return
     */
    public default AbstractRatiosDataModel getATracer(String tracerNameandVersion) {
        AbstractRatiosDataModel retVal = null;
        for (AbstractRatiosDataModel t : getTracers()) {
            if (t.getNameAndVersion().equalsIgnoreCase(tracerNameandVersion.trim())) {
                retVal = t;
            }
        }
        return retVal;
    }

    /**
     *
     * @return
     */
    public default ValueModel getPreferredSampleDateModel() {
        ValueModel retVal = null;
        for (ValueModel psdm : getSampleDateModels()) {
            if (((SampleDateModel) psdm).isPreferred()) {
                retVal = psdm;
            }
        }
        return retVal;
    }

    /**
     *
     * @param sampleDateModel
     */
    public default void setPreferredSampleDateModel(ValueModel sampleDateModel) {
        // set all to false
        for (ValueModel sam : getSampleDateModels()) {
            ((SampleDateModel) sam).setPreferred(false);
        }
        ((SampleDateModel) sampleDateModel).setPreferred(true);
        Collections.sort(getSampleDateModels());
    }

    /**
     *
     */
    public default void updateSampleDateModels() {
        // Nov 2008
        // process all sampleDateModels' included fraction vectors to remove missing fractions
        Vector<String> includedFractionIDs = ((ReduxAliquotInterface) this).getAliquotFractionIDs();
        Vector<String> excludedFractionIDs = new Vector<>();

        boolean existsPreferredDate = false;
        for (ValueModel SAM : getSampleDateModels()) {
            ((SampleDateModel) SAM).getIncludedFractionIDsVector().stream().filter((fractionID) //
                    -> (!includedFractionIDs.contains(fractionID))).forEach((fractionID) //
                    -> {
                excludedFractionIDs.add(fractionID);
            });
            // remove found exclusions (these are ones that were rejected after processing
            excludedFractionIDs.stream().forEach((fractionID) -> {
                ((SampleDateModel) SAM).getIncludedFractionIDsVector().remove(fractionID);
            });

            if (((SampleDateModel) SAM).isPreferred()) {
                existsPreferredDate = true;
            }

            // oct 2014 per Matt Rioux email report
            try {
                ((SampleDateModel) SAM).setAliquot(this);
                ((SampleDateModel) SAM).CalculateDateInterpretationForAliquot();
            } catch (Exception e) {
            }
        }

        // guarantee preferred date model
        if (!existsPreferredDate && (getSampleDateModels().size() > 0)) {
            ((SampleDateModel) getSampleDateModels().get(0)).setPreferred(true);
        }

    }

    /**
     *
     * @return
     */
    public default Vector<ValueModel> legalizeSampleDateModels() {
        // created april 2010 to remove any sample date model that has no fractions
        // in preparation for publishing as xml

        Vector<ValueModel> tempSampleDateModels = new Vector<>();
        boolean existsPreferredDate = false;

        // first update models to clean up fraction lists
        updateSampleDateModels();

        // now check for empties
        for (ValueModel vm : getSampleDateModels()) {
            if (!((SampleDateModel) vm).getIncludedFractionIDsVector().isEmpty()) {
                tempSampleDateModels.add(vm);
                if (((SampleDateModel) vm).isPreferred()) {
                    existsPreferredDate = true;
                }
            }
        }

        // guarantee preferred date model
        if (!existsPreferredDate && (getSampleDateModels().size() > 0)) {
            ((SampleDateModel) getSampleDateModels().get(0)).setPreferred(true);
        }

        setSampleDateModels(tempSampleDateModels);

        return tempSampleDateModels;
    }

    /* "ID-TIMS",
     "SHRIMP Ion Probe",
     "Cameca Ion Probe",
     "Quad ICPMS",
     "HR-ICPMS",
     "MC-ICPMS"
     */
    public default boolean usesIDTIMS() {
        return (getAliquotInstrumentalMethod().equalsIgnoreCase("ID-TIMS"));
    }

    public default boolean usesMCIPMS() {
        return (getAliquotInstrumentalMethod().equalsIgnoreCase("MC-ICPMS"));
    }

}
