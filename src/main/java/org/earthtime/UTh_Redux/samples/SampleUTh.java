/*
 * Copyright 2015 CIRDLES.
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
package org.earthtime.UTh_Redux.samples;

import java.io.File;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.dateInterpretation.graphPersistence.GraphAxesSetup;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.UPb_Redux.samples.SESARSampleMetadata;
import org.earthtime.UPb_Redux.user.SampleDateInterpretationGUIOptions;
import org.earthtime.UPb_Redux.valueModels.SampleDateModel;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.UTh_Redux.aliquots.UThReduxAliquot;
import org.earthtime.aliquots.AliquotInterface;
import org.earthtime.dataDictionaries.SampleRegistries;
import org.earthtime.dialogs.DialogEditor;
import org.earthtime.exceptions.ETException;
import org.earthtime.fractions.ETFractionInterface;
import org.earthtime.plots.evolution.seaWater.SeaWaterInitialDelta234UTableModel;
import org.earthtime.plots.isochrons.IsochronModel;
import org.earthtime.projects.EarthTimeSerializedFileInterface;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.reduxLabData.ReduxLabData;
import org.earthtime.reports.ReportSettingsInterface;
import org.earthtime.samples.ETSample;
import org.earthtime.samples.SampleInterface;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public class SampleUTh extends ETSample implements
        SampleInterface,
        Serializable,
        EarthTimeSerializedFileInterface {

    /**
     * identifies object in binary serialization
     */
    private static final long serialVersionUID = -6369235712538414391L;

    /**
     * ReduxLabData, which is made available to any active sample; contains
     * information regarding tracers and the various models for each sample
     */
    private transient ReduxLabData myReduxLabData;
    /**
     * the file of this <code>Sample</code>.
     */
    private String sampleName;
    /**
     * the type that this <code>Sample</code> is classified as. Valid fields
     * are: "ANALYSIS" or "COMPILATION"
     */
    private String sampleType;
    private ReduxConstants.ANALYSIS_PURPOSE analysisPurpose;
    private String sampleAnalysisType;
    /**
     * used to flag analyzed files such as imported aliquots and MC-ICPMS Excel
     * files. Set to <code>true</code> when the <code>Sample</code> has been
     * analyzed and <code>false</code> when it has not.
     */
    private boolean analyzed;
    /**
     * the International Geo Sample Number of this <code>Sample</code>.
     */
    private String sampleIGSN;
    private boolean validatedSampleIGSN;
    /**
     * the file that this <code>Sample</code> will be saved under
     */
    private String reduxSampleFileName;
    /**
     * the path to which this <code>Sample</code> will be saved.
     */
    private String reduxSampleFilePath;

    /**
     * UPb or UTh as of July 2015
     */
    private String isotopeSystem;
    private String defaultReportSpecsType;
    /**
     * any comments or clarifications regarding this <code>Sample</code>.
     */
    private String sampleAnnotations;
    /**
     * the <code>PhysicalConstants</code> for use with this <code>Sample</code>,
     * containing information regarding atomic molar masses and measured
     * constants.
     */
    private AbstractRatiosDataModel physicalConstantsModel;
    /**
     * the <code>ReportSettings</code> for use with this <code>Sample</code>,
     * containing information regarding columnar report layout.
     */
    private ReportSettingsInterface reportSettingsModel;
    /**
     * the collection of aliquots created for this <code>Sample</code>;
     * contained in aliquot vector for thread safety
     */
    private Vector<AliquotInterface> aliquots;
    private Vector<ETFractionInterface> UThFractions;
    /**
     * used to show whether this <code>Sample</code> has been altered. Set to
     * <code>true</code> when it has been changed, <code>false</code> if it has
     * not.
     */

    /**
     * the settings for the user interface of the sample age interpretation.
     */
    private SampleDateInterpretationGUIOptions sampleDateInterpretationGUISettings;

    private SESARSampleMetadata mySESARSampleMetadata;

    private boolean changed;

    private SortedSet<String> filteredFractionIDs;

    private Vector<ValueModel> sampleDateModels;

    /**
     *
     */
    public SampleUTh() {
    }

    /**
     * creates aliquot new instance of <code>Sample</code> with aliquot
     * specified <code>sampleName</code>, <code>sampleType</code>, and
     * <code>myReduxLabData</code>. All other fields are initialized to default
     * values and aliquot default <code>Aliquot</code> is added to the
     * <code>Sample</code>.
     *
     * @param sampleName
     * @param sampleType the type of this <code>Sample</code> to which
     * <code>sampleType</code> will be set
     * @param sampleAnalysisType
     * @param defaultAnalysisPurpose
     * @param isotopeSystem the value of isotopeSystem
     * @param defaultReportSpecsType
     * @throws org.earthtime.UPb_Redux.exceptions.BadLabDataException
     * BadLabDataException
     */
    public SampleUTh(
            String sampleName, String sampleType, String sampleAnalysisType, ReduxConstants.ANALYSIS_PURPOSE defaultAnalysisPurpose, String isotopeSystem, String defaultReportSpecsType)
            throws BadLabDataException {

        this.myReduxLabData = ReduxLabData.getInstance();

        this.sampleName = sampleName;
        this.sampleType = sampleType;
        this.analysisPurpose = defaultAnalysisPurpose;
        this.sampleAnalysisType = sampleAnalysisType;
        this.analyzed = false;
        this.sampleIGSN = ReduxConstants.DEFAULT_IGSN;
        this.validatedSampleIGSN = false;
        this.reduxSampleFileName = "";
        this.reduxSampleFilePath = "";
        this.isotopeSystem = isotopeSystem;
        this.defaultReportSpecsType = defaultReportSpecsType;

        this.sampleAnnotations = "";

        this.reportSettingsModel = myReduxLabData.getDefaultReportSettingsModelBySpecsType(defaultReportSpecsType);
        this.physicalConstantsModel = myReduxLabData.getDefaultPhysicalConstantsModel();

        this.aliquots = new Vector<>();
        this.UThFractions = new Vector<>();

//        this.tripoliSession = null;
        this.sampleDateInterpretationGUISettings = new SampleDateInterpretationGUIOptions();
//
//        this.sampleFolderSaved = null;
        this.sampleDateModels = new Vector<>();
//
//        this.mineralName = "zircon";
        this.mySESARSampleMetadata = new SESARSampleMetadata();
//
//        this.archivedInRegistry = false;
//
//        this.sampleRegistry = SampleRegistries.SESAR;
        this.changed = false;

//        initFilteredFractionsToAll();
    }

    @Override
    public AliquotInterface generateDefaultAliquot(//
            int aliquotNumber,
            String aliquotName,
            AbstractRatiosDataModel physicalConstants,
            boolean compiled,
            SESARSampleMetadata mySESARSampleMetadata) {

        return new UThReduxAliquot(aliquotNumber, aliquotName, physicalConstants, compiled, mySESARSampleMetadata);
    }

    @Override
    public AliquotInterface generateDefaultAliquot() {
        return new UThReduxAliquot();
    }

    /**
     *
     */
    @Override
    public void setUpSample() {

        // force aliquot registry onto sample SESAR starting oct 2014
        if (getSampleRegistry() == null) {
            setSampleRegistry(SampleRegistries.SESAR);
        }

        // April 2011 we are altering SampleIGSN to be of form rrr.IGSN
        //  where rrr is registry as per enum SampleRegistries
        // this means that if sample is already flagged as validated - i.e. at SESAR
        // we check that it is valid at GeochronID and change SampleIGSN and percolate it
        // down to all Aliquots
        updateWithRegistrySampleIGSN();

    }

    /**
     * @return the myReduxLabData
     */
    public ReduxLabData getMyReduxLabData() {
        return myReduxLabData;
    }

    /**
     * @param myReduxLabData the myReduxLabData to set
     */
    public void setMyReduxLabData(ReduxLabData myReduxLabData) {
        this.myReduxLabData = myReduxLabData;
    }

    /**
     * @return the sampleType
     */
    @Override
    public String getSampleType() {
        return sampleType;
    }

    /**
     * @param sampleType the sampleType to set
     */
    @Override
    public void setSampleType(String sampleType) {
        this.sampleType = sampleType;
    }

    @Override
    public ReduxConstants.ANALYSIS_PURPOSE getAnalysisPurpose() {
        return analysisPurpose;
    }

    @Override
    public void setAnalysisPurpose(ReduxConstants.ANALYSIS_PURPOSE analysisPurpose) {
        this.analysisPurpose = analysisPurpose;
    }

    /**
     * @return the analyzed
     */
    @Override
    public boolean isAnalyzed() {
        return analyzed;
    }

    /**
     * @param analyzed the analyzed to set
     */
    @Override
    public void setAnalyzed(boolean analyzed) {
        this.analyzed = analyzed;
    }

    /**
     * @return the sampleName
     */
    @Override
    public String getSampleName() {
        return sampleName;
    }

    /**
     * @param sampleName the sampleName to set
     */
    @Override
    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    /**
     * @return the sampleIGSN
     */
    @Override
    public String getSampleIGSN() {
        return sampleIGSN;
    }

    /**
     * @param sampleIGSN the sampleIGSN to set
     */
    @Override
    public void setSampleIGSN(String sampleIGSN) {
        this.sampleIGSN = sampleIGSN;
    }

    /**
     * @return the validatedSampleIGSN
     */
    @Override
    public boolean isValidatedSampleIGSN() {
        return validatedSampleIGSN;
    }

    /**
     * @param validatedSampleIGSN the validatedSampleIGSN to set
     */
    @Override
    public void setValidatedSampleIGSN(boolean validatedSampleIGSN) {
        this.validatedSampleIGSN = validatedSampleIGSN;
    }

    @Override
    public String getReduxSampleFileName() {
        return reduxSampleFileName;
    }

    /**
     * @param reduxSampleFileName the reduxSampleFileName to set
     */
    @Override
    public void setReduxSampleFileName(String reduxSampleFileName) {
        this.reduxSampleFileName = reduxSampleFileName;
    }

    @Override
    public String getReduxSampleFilePath() {
        return reduxSampleFilePath;
    }

    @Override
    public void setReduxSampleFilePath(String reduxSampleFilePath) {
        this.reduxSampleFilePath = reduxSampleFilePath;
    }

    @Override
    public String getSampleAnnotations() {
        return sampleAnnotations;
    }

    @Override
    public void setSampleAnnotations(String sampleAnnotations) {
        this.sampleAnnotations = sampleAnnotations;
    }

    @Override
    public ReportSettingsInterface getReportSettingsModel() {
        return reportSettingsModel;
    }

    @Override
    public void setReportSettingsModel(ReportSettingsInterface reportSettingsModel) {
        this.reportSettingsModel = reportSettingsModel;
    }

    @Override
    public AbstractRatiosDataModel getPhysicalConstantsModel() {
        return physicalConstantsModel;
    }

    @Override
    public void setPhysicalConstantsModel(AbstractRatiosDataModel physicalConstantsModel) {
        this.physicalConstantsModel = physicalConstantsModel;
    }

    @Override
    public Vector<AliquotInterface> getAliquots() {
        return aliquots;
    }

    @Override
    public void setAliquots(Vector<AliquotInterface> aliquots) {
        this.aliquots = aliquots;
    }

    /**
     * gets the <code>UPbFractions</code> of this <code>Sample</code>.
     *
     * @pre this <code>Sample</code> exists
     * @post returns the <code>UPbFractions</code> of this <code>Sample</code>
     *
     * @return <code>Vector</code> - set of <code>Fractions</code> that make up
     * the <code>UPbFractions</code> of this <code>Sample</code>
     */
    @Override
    public Vector<ETFractionInterface> getFractions() {
        return UThFractions;
    }

    /**
     * sets the <code>UPbFractions</code> of this <code>Sample</code> to the
     * argument <code>UPbFractions</code>
     *
     * @param UThFractions
     * @pre argument <code>UPbFractions</code> is a valid set of
     * <code>UPbFractions</code>
     * @post this <code>Sample</code>'s <code>UPbFractions</code> is set to
     * argument <code>UPbFractions</code>
     */
    @Override
    public void setFractions(Vector<ETFractionInterface> UThFractions) {
        this.UThFractions = UThFractions;
    }

    @Override
    public SampleDateInterpretationGUIOptions getSampleDateInterpretationGUISettings() {
        return sampleDateInterpretationGUISettings;
    }

    @Override
    public void setSampleAgeInterpretationGUISettings(SampleDateInterpretationGUIOptions sampleDateInterpretationGUISettings) {
        this.sampleDateInterpretationGUISettings = sampleDateInterpretationGUISettings;
    }

    @Override
    public SESARSampleMetadata getMySESARSampleMetadata() {
        return mySESARSampleMetadata;
    }

    @Override
    public boolean isChanged() {
        return changed;
    }

    @Override
    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    @Override
    public void setSampleAnalysisType(String sampleAnalysisType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isSampleTypeProject() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getSampleAnalysisType() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getMineralName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setMineralName(String mineralName) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public File getSampleFolderSaved() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setSampleFolderSaved(File sampleFolderSaved) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setAutomaticDataUpdateMode(boolean automaticDataUpdateMode) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isAutomaticDataUpdateMode() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setCalculateTWrhoForLegacyData(boolean calculateTWrhoForLegacyData) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isCalculateTWrhoForLegacyData() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }


    /**
     * @param sampleDateModels the sampleDateModels to set
     */
    @Override
    public void setSampleDateModels(Vector<ValueModel> sampleDateModels) {
        this.sampleDateModels = sampleDateModels;
    }

    /**
     * @return the sampleDateModels
     */
    @Override
    public Vector<ValueModel> getSampleDateModels() {
        if (sampleDateModels == null) {
            sampleDateModels = new Vector<>();
        }
        return sampleDateModels;
    }
    
    public static ValueModel generateDefaultSampleDateModel(){
        ValueModel defaultSDM = new SampleDateModel();
        
        defaultSDM.setName("DEFAULT");
        ((SampleDateModel)defaultSDM).setIsochronModels(
                IsochronModel.generateDefaultEvolutionIsochronModels());
        ((SampleDateModel)defaultSDM).setAutomaticIsochronSelection(true);
        
        ((SampleDateModel)defaultSDM).setAr48icntrs(IsochronModel.generateDefaultEvolutionAr48icntrs());
        ((SampleDateModel)defaultSDM).setAutomaticInitDelta234USelection(true);
        
        return defaultSDM;
    }

    @Override
    public void setFractionDataOverriddenOnImport(boolean fractionDataOverriddenOnImport) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setSampleRegistry(SampleRegistries sampleRegistry) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getSampleIGSNnoRegistry() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isArchivedInRegistry() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setArchivedInRegistry(boolean archivedInRegistry) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SampleRegistries getSampleRegistry() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public GraphAxesSetup getTerraWasserburgGraphAxesSetup() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public GraphAxesSetup getConcordiaGraphAxesSetup() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void automaticUpdateOfUPbSampleFolder(SampleInterface sample, DialogEditor myFractionEditor) throws ETException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addFractionsVector(Vector<ETFractionInterface> fractions, int aliquotNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     */
    @Override
    public void restoreDefaultReportSettingsModel() {
        setReportSettingsModel(ReduxLabData.getInstance().getDefaultReportSettingsModelBySpecsType(getDefaultReportSpecsType()));
    }

    /**
     * @return the isotopeSystem
     */
    @Override
    public String getIsotopeSystem() {
        if (isotopeSystem == null) {
            isotopeSystem = "UTh";
        }
        return isotopeSystem;
    }

    /**
     * @return the filteredFractionIDs
     */
    @Override
    public SortedSet<String> getFilteredFractionIDs() {
        if (filteredFractionIDs == null) {
            initFilteredFractionsToAll();
        }
        return filteredFractionIDs;
    }

    /**
     * @param filteredFractionIDs the filteredFractionIDs to set
     */
    @Override
    public void setFilteredFractionIDs(SortedSet<String> filteredFractionIDs) {
        this.filteredFractionIDs = filteredFractionIDs;
    }

    @Override
    public void initFilteredFractionsToAll() {
        this.filteredFractionIDs = Collections.synchronizedSortedSet(new TreeSet<>());
        for (int i = 0; i < UThFractions.size(); i++) {
            filteredFractionIDs.add(UThFractions.get(i).getFractionID());
        }
    }

    /**
     * @return the defaultReportSpecsType
     */
    @Override
    public String getDefaultReportSpecsType() {
        if (defaultReportSpecsType == null) {
            defaultReportSpecsType = "UTh_Carb";
        }
        return defaultReportSpecsType;
    }

    @Override
    public List<SeaWaterInitialDelta234UTableModel> getSeaWaterInitialDelta234UTableModels() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setSeaWaterInitialDelta234UTableModels(List<SeaWaterInitialDelta234UTableModel> seaWaterInitialDelta234UTableModels) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
