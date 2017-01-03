
/*
 * Aliquot.java
 *
 * Created on June 7, 2007, 3:40 PM
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
package org.earthtime.UPb_Redux.aliquots;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Vector;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.ReduxConstants.ANALYSIS_PURPOSE;
import org.earthtime.UPb_Redux.fractions.FractionI;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.aliquots.AliquotForUPbInterface;
import org.earthtime.aliquots.AliquotInterface;
import org.earthtime.dataDictionaries.DataDictionary;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataModels.pbBlankICModels.PbBlankICModel;
import org.earthtime.ratioDataModels.tracers.TracerUPbModel;
import org.earthtime.reduxLabData.ReduxLabData;

/**
 *
 * @author James F. Bowring
 */
public abstract class Aliquot implements AliquotInterface, AliquotForUPbInterface, Serializable {

    // Class variables
    private static final long serialVersionUID = 6355007168312036059L;
    // Instance variables
    /**
     * SESAR produces IGSN and eventually we will tie to their database.
     */
    protected String sampleIGSN;
    /**
     * Lab's local name for the Aliquot.
     */
    private String aliquotName;
    protected String aliquotIGSN;
    private String laboratoryName;
    private String analystName;
    private String aliquotReference;
    private String aliquotInstrumentalMethod;
    private String aliquotInstrumentalMethodReference;
    private String aliquotComment;
    private BigDecimal calibrationUnct206_238;
    private BigDecimal calibrationUnct208_232;
    private BigDecimal calibrationUnct207_206;
    protected Vector<ValueModel> sampleDateModels;
    private AbstractRatiosDataModel physicalConstantsModel;
    private Vector<AbstractRatiosDataModel> pbBlanks;
    private Vector<AbstractRatiosDataModel> tracers;
    private Vector<ValueModel> alphaPbModels;
    private Vector<ValueModel> alphaUModels;
    private Vector<AbstractRatiosDataModel> MineralStandardModels;
    private Vector<FractionI> analysisFractions;
    private ANALYSIS_PURPOSE analysisPurpose;
    private String keyWordsCSV;

    //may 2014
    /**
     *
     */
    protected BigDecimal bestAgeDivider206_238;

    /**
     * Creates a new instance of Aliquot
     */
    public Aliquot() {
        this.sampleIGSN = ReduxConstants.DEFAULT_IGSN;
        this.aliquotName = ReduxConstants.DEFAULT_OBJECT_NAME;
        this.aliquotIGSN = ReduxConstants.DEFAULT_ALIQUOT_IGSN;
        this.laboratoryName = "NONE";
        this.analystName = "NONE";
        this.aliquotReference = "NONE";
        this.aliquotInstrumentalMethod = DataDictionary.AliquotInstrumentalMethod[0];
        this.aliquotInstrumentalMethodReference = "NONE";
        this.aliquotComment = "NONE";

        this.calibrationUnct206_238 = BigDecimal.ZERO;
        this.calibrationUnct207_206 = BigDecimal.ZERO;
        this.calibrationUnct208_232 = BigDecimal.ZERO;

        this.sampleDateModels = new Vector<>();

        this.physicalConstantsModel = ReduxLabData.getInstance().getDefaultPhysicalConstantsModel();

        this.pbBlanks = new Vector<>();

        this.tracers = new Vector<>();
        this.alphaPbModels = new Vector<>();
        this.alphaUModels = new Vector<>();

        this.MineralStandardModels = new Vector<>();

        this.analysisFractions = new Vector<>();

        this.analysisPurpose = ANALYSIS_PURPOSE.SingleAge;

        this.keyWordsCSV = "";

        this.bestAgeDivider206_238 = BigDecimal.ZERO;

    }

    /**
     *
     * @param aliquotIGSN
     */
    public Aliquot(String aliquotIGSN) {
        this();
        this.aliquotIGSN = aliquotIGSN.trim();
    }

    // public accessors
    /**
     *
     * @return
     */
    public String getSampleIGSN() {
        return sampleIGSN;
    }

    /**
     *
     * @return
     */
    public String getSampleIGSNnoRegistry() {
        String retVal = "";
        String parse[] = sampleIGSN.split("\\.");
        if (parse.length > 0) {
            // returns index 0 if no registry, 1 otherwise
            retVal = parse[parse.length - 1];
        }

        return retVal;
    }

    public String getAlliquotIGSNnoRegistry() {
        String retVal = "";
        String parse[] = aliquotIGSN.split("\\.");
        if (parse.length > 0) {
            // returns index 0 if no registry, 1 otherwise
            retVal = parse[parse.length - 1];
        }

        return retVal;
    }

    /**
     *
     * @param sampleIGSN
     */
    @Override
    public void setSampleIGSN(String sampleIGSN) {
        this.sampleIGSN = sampleIGSN.trim();
    }

    /**
     *
     * @return
     */
    @Override
    public String getAliquotIGSN() {
        return aliquotIGSN;
    }

    /**
     *
     * @param aliquotIGSN
     */
    @Override
    public void setAliquotIGSN(String aliquotIGSN) {
        this.aliquotIGSN = aliquotIGSN.trim();
    }

    /**
     *
     * @return
     */
    @Override
    public String getLaboratoryName() {
        return laboratoryName;
    }

    /**
     *
     * @param laboratoryName
     */
    @Override
    public void setLaboratoryName(String laboratoryName) {
        this.laboratoryName = laboratoryName.trim();
    }

    /**
     *
     * @return
     */
    @Override
    public String getAnalystName() {
        return analystName;
    }

    /**
     *
     * @param analystName
     */
    @Override
    public void setAnalystName(String analystName) {
        this.analystName = analystName.trim();

    }

    /**
     *
     * @return
     */
    @Override
    public String getAliquotReference() {
        return aliquotReference;
    }

    /**
     *
     * @param aliquotReference
     */
    @Override
    public void setAliquotReference(String aliquotReference) {
        this.aliquotReference = aliquotReference.trim();
    }

    /**
     *
     * @return
     */
    @Override
    public String getAliquotInstrumentalMethod() {
        return aliquotInstrumentalMethod;
    }

    /**
     *
     * @param aliquotInstrumentalMethod
     */
    @Override
    public void setAliquotInstrumentalMethod(String aliquotInstrumentalMethod) {
        this.aliquotInstrumentalMethod = aliquotInstrumentalMethod.trim();
    }

    /**
     *
     * @return
     */
    @Override
    public String getAliquotInstrumentalMethodReference() {
        return aliquotInstrumentalMethodReference;
    }

    /**
     *
     * @param aliquotInstrumentalMethodReference
     */
    @Override
    public void setAliquotInstrumentalMethodReference(String aliquotInstrumentalMethodReference) {
        this.aliquotInstrumentalMethodReference = aliquotInstrumentalMethodReference.trim();
    }

    /**
     *
     * @return
     */
    @Override
    public String getAliquotComment() {
        return aliquotComment;
    }

    /**
     *
     * @param aliquotComment
     */
    @Override
    public void setAliquotComment(String aliquotComment) {
        this.aliquotComment = aliquotComment.trim();
    }

    /**
     *
     * @return
     */
    @Override
    public AbstractRatiosDataModel getPhysicalConstantsModel() {
        return physicalConstantsModel;
    }

    /**
     *
     * @param physicalConstants
     */
    @Override
    public void setPhysicalConstantsModel(AbstractRatiosDataModel physicalConstants) {
        this.physicalConstantsModel = physicalConstants;
    }

    /**
     *
     * @return
     */
    @Override
    public Vector<AbstractRatiosDataModel> getPbBlanks() {
        return pbBlanks;
    }

    /**
     *
     * @return
     */
    public Vector<AbstractRatiosDataModel> getPbBlanksForXMLSerialization() {
        // remove placeholder <none>
        Vector<AbstractRatiosDataModel> temp = new Vector<>();
        pbBlanks.stream().filter((p) -> (!(p.equals(PbBlankICModel.getNoneInstance())))).forEach((p) -> {
            temp.add(p);
        });
        return temp;
    }

    /**
     *
     * @param pbBlanks
     */
    @Override
    public void setPbBlanks(Vector<AbstractRatiosDataModel> pbBlanks) {
        this.pbBlanks = pbBlanks;
    }

    /**
     *
     * @return
     */
    @Override
    public Vector<AbstractRatiosDataModel> getTracers() {
        return tracers;
    }

    /**
     *
     * @return
     */
    public Vector<AbstractRatiosDataModel> getTracersForXMLSerialization() {
        // remove placeholder <none>
        Vector<AbstractRatiosDataModel> temp = new Vector<>();
        tracers.stream().filter((t) -> (!(t.equals(TracerUPbModel.getNoneInstance())))).forEach((t) -> {
            temp.add(t);
        });
        return temp;
    }

    /**
     *
     * @param tracers
     */
    public void setTracers(Vector<AbstractRatiosDataModel> tracers) {
        this.tracers = tracers;
    }

    /**
     * @return the alphaPbModels
     */
    public Vector<ValueModel> getAlphaPbModels() {
        return alphaPbModels;
    }

    /**
     *
     * @return
     */
    public Vector<ValueModel> getAlphaPbModelsForXMLSerialization() {
        // remove placeholder <none>
        Vector<ValueModel> temp = new Vector<ValueModel>();
        for (ValueModel a : alphaPbModels) {
            if (!(a.getName().startsWith("<none>") || a.getName().startsWith(ReduxConstants.NONE))) {
                temp.add(a);
            }
        }
        return temp;
    }

    /**
     * @param alphaPbModels the alphaPbModels to set
     */
    public void setAlphaPbModels(Vector<ValueModel> alphaPbModels) {
        this.alphaPbModels = alphaPbModels;
    }

    /**
     * @return the alphaUModels
     */
    public Vector<ValueModel> getAlphaUModels() {
        return alphaUModels;
    }

    /**
     *
     * @return
     */
    public Vector<ValueModel> getAlphaUModelsForXMLSerialization() {
        // remove placeholder <none>
        Vector<ValueModel> temp = new Vector<>();
        for (ValueModel a : alphaUModels) {
            if (!(a.getName().startsWith("<none>") || a.getName().startsWith(ReduxConstants.NONE))) {
                temp.add(a);
            }
        }
        return temp;
    }

    /**
     * @param alphaUModels the alphaUModels to set
     */
    public void setAlphaUModels(Vector<ValueModel> alphaUModels) {
        this.alphaUModels = alphaUModels;
    }

    /**
     *
     * @return
     */
    @Override
    public Vector<FractionI> getAnalysisFractions() {
        return analysisFractions;
    }

    /**
     *
     * @param analysisFractions
     */
    public void setAnalysisFractions(Vector<FractionI> analysisFractions) {
        this.analysisFractions = analysisFractions;
    }

    /**
     *
     * @return
     */
    @Override
    public String getAliquotName() {
        return aliquotName;
    }

    /**
     *
     * @param aliquotName
     */
    @Override
    public void setAliquotName(String aliquotName) {
        if (aliquotName.trim().length() > 0) {
            this.aliquotName = aliquotName.trim();
        }
    }

    /**
     *
     * @return
     */
    @Override
    public Vector<AbstractRatiosDataModel> getMineralStandardModels() {
        return MineralStandardModels;
    }

    /**
     *
     * @param MineralStandards
     */
    @Override
    public void setMineralStandardModels(Vector<AbstractRatiosDataModel> MineralStandards) {
        this.MineralStandardModels = MineralStandards;
    }

    /**
     *
     * @return
     */
    @Override
    public Vector<ValueModel> getSampleDateModels() {
        return sampleDateModels;
    }

    /**
     *
     * @param sampleDateModels
     */
    @Override
    public void setSampleDateModels(Vector<ValueModel> sampleDateModels) {
        this.sampleDateModels = sampleDateModels;
    }

    /**
     *
     * @return
     */
    @Override
    public BigDecimal getCalibrationUnct206_238() {
        return calibrationUnct206_238;
    }

    /**
     *
     * @param calibrationUnct206_238
     */
    @Override
    public void setCalibrationUnct206_238(BigDecimal calibrationUnct206_238) {
        this.calibrationUnct206_238 = calibrationUnct206_238;
    }

    /**
     *
     * @return
     */
    @Override
    public BigDecimal getCalibrationUnct208_232() {
        return calibrationUnct208_232;
    }

    /**
     *
     * @param calibrationUnct208_232
     */
    @Override
    public void setCalibrationUnct208_232(BigDecimal calibrationUnct208_232) {
        this.calibrationUnct208_232 = calibrationUnct208_232;
    }

    /**
     *
     * @return
     */
    @Override
    public BigDecimal getCalibrationUnct207_206() {
        return calibrationUnct207_206;
    }

    /**
     *
     * @param calibrationUnct207_206
     */
    @Override
    public void setCalibrationUnct207_206(BigDecimal calibrationUnct207_206) {
        this.calibrationUnct207_206 = calibrationUnct207_206;
    }

    /**
     * @return the analysisPurpose
     */
    @Override
    public ANALYSIS_PURPOSE getAnalysisPurpose() {
        if (analysisPurpose == null) {
            analysisPurpose = ANALYSIS_PURPOSE.NONE;
        }
        return analysisPurpose;
    }

    /**
     * @param analysisPurpose the analysisPurpose to set
     */
    @Override
    public void setAnalysisPurpose(ANALYSIS_PURPOSE analysisPurpose) {
        this.analysisPurpose = analysisPurpose;
    }

    /**
     * @return the keyWordsCSV
     */
    @Override
    public String getKeyWordsCSV() {
        if (keyWordsCSV == null) {
            keyWordsCSV = "";
        }
        return keyWordsCSV;
    }

    /**
     * @param keyWordsCSV the keyWordsCSV to set
     */
    @Override
    public void setKeyWordsCSV(String keyWordsCSV) {
        this.keyWordsCSV = keyWordsCSV.trim();
    }

    /**
     * @return the bestAgeDivider206_238
     */
    @Override
    public BigDecimal getBestAgeDivider206_238() {
        return bestAgeDivider206_238;
    }

    /**
     * @param bestAgeDivider206_238 the bestAgeDivider206_238 to set
     */
    @Override
    public void setBestAgeDivider206_238(BigDecimal bestAgeDivider206_238) {
        this.bestAgeDivider206_238 = bestAgeDivider206_238;
    }
}
