/*
 * Copyright 2006-2015 CIRDLES.org.
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
package org.earthtime.UTh_Redux.aliquots;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Vector;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.fractions.FractionI;
import org.earthtime.UPb_Redux.samples.SESARSampleMetadata;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.aliquots.AliquotInterface;
import org.earthtime.aliquots.ReduxAliquotInterface;
import org.earthtime.archivingTools.AnalysisImageInterface;
import org.earthtime.dataDictionaries.AnalysisImageTypes;
import org.earthtime.fractions.ETFractionInterface;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.reduxLabData.ReduxLabData;
import org.earthtime.reportViews.ReportRowGUIInterface;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public class UThReduxAliquot implements //
        AliquotInterface, ReduxAliquotInterface, ReportRowGUIInterface, Serializable {

    // Class variables
    private static final long serialVersionUID = 8000633948734353979L;
    // Instance variables
    private transient boolean selectedInDataTable;
    /**
     * SESAR produces IGSN and eventually we will tie to their database.
     */
    protected String sampleIGSN;
    /**
     * Lab's local name for the Aliquot.
     */
    private String aliquotName;
    protected String aliquotIGSN;
    private int aliquotNumber;
    private String laboratoryName;
    private String analystName;
    private String aliquotReference;
    private String aliquotInstrumentalMethod;
    private String aliquotInstrumentalMethodReference;
    private String aliquotComment;
    protected Vector<ValueModel> sampleDateModels;
    private AbstractRatiosDataModel physicalConstantsModel;
    private Vector<AbstractRatiosDataModel> tracers;
    private Vector<AbstractRatiosDataModel> mineralStandardModels;
    private Vector<FractionI> analysisFractions;
    private ReduxConstants.ANALYSIS_PURPOSE analysisPurpose;
    private String keyWordsCSV;
    private ArrayList<AnalysisImageInterface> analysisImages;
    private boolean compiled;
    private Vector<ETFractionInterface> aliquotFractions;
    private transient ReduxLabData myReduxLabData;

    public UThReduxAliquot() {
    }

    /**
     *
     * @param aliquotNumber
     * @param aliquotName
     * @param physicalConstantsModel
     * @param reduxLabData
     * @param physicalConstants
     * @param compiled
     * @param mySESARSampleMetadata
     */
    public UThReduxAliquot(
            int aliquotNumber,
            String aliquotName,
            AbstractRatiosDataModel physicalConstantsModel,
            boolean compiled,
            SESARSampleMetadata mySESARSampleMetadata) {

        this.aliquotIGSN = ReduxConstants.DEFAULT_ALIQUOT_IGSN;
        this.aliquotName = aliquotName;

        this.aliquotNumber = aliquotNumber;

        this.physicalConstantsModel = physicalConstantsModel;

        this.compiled = compiled;

        this.sampleDateModels = new Vector<>();
        this.mineralStandardModels = new Vector<>();
        this.aliquotFractions = new Vector<>();

        this.sampleIGSN = "NONE";
        this.laboratoryName = ReduxLabData.getInstance().getLabName();
        this.analystName = ReduxLabData.getInstance().getAnalystName();

//        this.automaticDataUpdateMode = false;
//
//        this.containingSampleDataFolder = null;
//
//        this.mySESARSampleMetadata = mySESARSampleMetadata;
        analysisImages = new ArrayList<>();
        this.myReduxLabData = ReduxLabData.getInstance();
    }

    @Override
    public String getAliquotName() {
        return aliquotName;
    }

    @Override
    public void setAliquotName(String aliquotName) {
        this.aliquotName = aliquotName;
    }

    @Override
    public String getSampleIGSN() {
        return sampleIGSN;
    }

    @Override
    public void setSampleIGSN(String sampleIGSN) {
        this.sampleIGSN = sampleIGSN;
    }

    @Override
    public String getAliquotIGSN() {
        return aliquotIGSN;
    }

    @Override
    public void setAliquotIGSN(String aliquotIGSN) {
        this.aliquotIGSN = aliquotIGSN;
    }

    @Override
    public String getLaboratoryName() {
        return laboratoryName;
    }

    @Override
    public void setLaboratoryName(String laboratoryName) {
        this.laboratoryName = laboratoryName;
    }

    @Override
    public String getAnalystName() {
        return analystName;
    }

    @Override
    public void setAnalystName(String analystName) {
        this.analystName = analystName;
    }

    @Override
    public String getAliquotReference() {
        return aliquotReference;
    }

    @Override
    public void setAliquotReference(String aliquotReference) {
        this.aliquotReference = aliquotReference;
    }

    @Override
    public String getAliquotInstrumentalMethod() {
        return aliquotInstrumentalMethod;
    }

    @Override
    public void setAliquotInstrumentalMethod(String aliquotInstrumentalMethod) {
        this.aliquotInstrumentalMethod = aliquotInstrumentalMethod;
    }

    @Override
    public String getAliquotInstrumentalMethodReference() {
        return aliquotInstrumentalMethodReference;
    }

    @Override
    public void setAliquotInstrumentalMethodReference(String aliquotInstrumentalMethodReference) {
        this.aliquotInstrumentalMethodReference = aliquotInstrumentalMethodReference;
    }

    @Override
    public String getAliquotComment() {
        return aliquotComment;
    }

    @Override
    public void setAliquotComment(String aliquotComment) {
        this.aliquotComment = aliquotComment;
    }

    @Override
    public Vector<ValueModel> getSampleDateModels() {
        return sampleDateModels;
    }

    @Override
    public void setSampleDateModels(Vector<ValueModel> sampleDateModels) {
        this.sampleDateModels = sampleDateModels;
    }

    @Override
    public AbstractRatiosDataModel getPhysicalConstantsModel() {
        return physicalConstantsModel;
    }

    @Override
    public void setPhysicalConstantsModel(AbstractRatiosDataModel physicalConstantsModel) {
        this.physicalConstantsModel = physicalConstantsModel;

        // all existing fractions must be updated
        if (getAliquotFractions() != null) {
            for (int i = 0; i < getAliquotFractions().size(); i++) {
                getAliquotFractions().get(i).setPhysicalConstantsModel(physicalConstantsModel);
            }
        }
    }

    @Override
    public Vector<AbstractRatiosDataModel> getTracers() {
        return tracers;
    }

    @Override
    public void setTracers(Vector<AbstractRatiosDataModel> tracers) {
        this.tracers = tracers;
    }

    @Override
    public Vector<AbstractRatiosDataModel> getMineralStandardModels() {
        return mineralStandardModels;
    }

    @Override
    public void setMineralStandardModels(Vector<AbstractRatiosDataModel> MineralStandardModels) {
        this.mineralStandardModels = MineralStandardModels;
    }

    @Override
    public ReduxConstants.ANALYSIS_PURPOSE getAnalysisPurpose() {
        return analysisPurpose;
    }

    /**
     *
     * @param analysisPurpose
     */
    @Override
    public void setAnalysisPurpose(ReduxConstants.ANALYSIS_PURPOSE analysisPurpose) {
        this.analysisPurpose = analysisPurpose;
    }

    @Override
    public String getKeyWordsCSV() {
        return keyWordsCSV;
    }

    @Override
    public void setKeyWordsCSV(String keyWordsCSV) {
        this.keyWordsCSV = keyWordsCSV;
    }

    @Override
    public Vector<FractionI> getAnalysisFractions() {
        return analysisFractions;
    }

    @Override
    public Vector<ETFractionInterface> getAliquotFractions() {
        return aliquotFractions;
    }

    @Override
    public void setAliquotFractions(Vector<ETFractionInterface> aliquotFractions) {
        this.aliquotFractions = aliquotFractions;
    }

    @Override
    public boolean isCompiled() {
        return compiled;
    }

    @Override
    public void setCompiled(boolean compiled) {
        this.compiled = compiled;
    }

    /**
     *
     * @return
     */
    @Override
    public int getAliquotNumber() {
        return aliquotNumber;
    }

    /**
     *
     * @param aliquotNumber
     */
    public void setAliquotNumber(int aliquotNumber) {
        this.aliquotNumber = aliquotNumber;
    }

    @Override
    public void setMyReduxLabData(ReduxLabData myReduxLabData) {
        this.myReduxLabData = myReduxLabData;
    }

    /**
     * @return the selectedInDataTable
     */
    @Override
    public boolean isSelectedInDataTable() {
        return selectedInDataTable;
    }

    /**
     * @param selectedInDataTable the selectedInDataTable to set
     */
    @Override
    public void setSelectedInDataTable(boolean selectedInDataTable) {
        this.selectedInDataTable = selectedInDataTable;
    }

    /**
     *
     * @param inLiveMode the value of inLiveMode
     */
    @Override
    public void reduceData(boolean inLiveMode) {
        //TODO: Reduce Useries
    }

    @Override
    public AnalysisImageInterface getAnalysisImageByType(AnalysisImageTypes imageType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
