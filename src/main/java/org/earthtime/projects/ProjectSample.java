/*
 * ProjectSample.java
 *
 *
 *
 * Copyright 2006-2018 James F. Bowring, CIRDLES.org, and Earth-Time.org
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
package org.earthtime.projects;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import math.geom2d.Point2D;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.ReduxConstants.ANALYSIS_PURPOSE;
import org.earthtime.UPb_Redux.dateInterpretation.graphPersistence.GraphAxesSetup;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.UPb_Redux.samples.SESARSampleMetadata;
import org.earthtime.UPb_Redux.user.SampleDateInterpretationGUIOptions;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.aliquots.AliquotInterface;
import org.earthtime.dataDictionaries.SampleRegistries;
import org.earthtime.dialogs.DialogEditor;
import org.earthtime.exceptions.ETException;
import org.earthtime.fractions.ETFractionInterface;
import org.earthtime.plots.evolution.LowerBoundaryComparator;
import org.earthtime.plots.evolution.UpperBoundaryComparator;
import org.earthtime.plots.evolution.openSystem.OpenSystemIsochronTableModel;
import org.earthtime.plots.evolution.seaWater.SeaWaterInitialDelta234UTableModel;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.reduxLabData.ReduxLabData;
import org.earthtime.reports.ReportSettingsInterface;
import org.earthtime.samples.SampleInterface;

/**
 *
 * @author James F. Bowring
 */
public class ProjectSample implements//
        SampleInterface,
        Serializable,
        EarthTimeSerializedFileInterface {

    private static final long serialVersionUID = -638058212764252304L;
    private String sampleName;
    private String sampleType;
    private String sampleAnalysisType;
    private ANALYSIS_PURPOSE analysisPurpose;
    private boolean analyzed;
    private String isotopeSystem;
    private String defaultReportSpecsType;
    private Vector<AliquotInterface> aliquots;
    private Vector<ETFractionInterface> fractions;
    private ReportSettingsInterface reportSettingsModel;
    private AbstractRatiosDataModel physicalConstantsModel;
    private SampleDateInterpretationGUIOptions sampleAgeInterpretationGUISettings;
    private boolean changed;
    private String reduxSampleFileName;
    private String reduxSampleFilePath;
    private GraphAxesSetup concordiaGraphAxesSetup;
    private GraphAxesSetup terraWasserburgGraphAxesSetup;
    private Vector<ValueModel> sampleDateModels;

    private transient ReduxLabData reduxLabData;

    private SortedSet<String> filteredFractionIDs;

    // maps age to delta
    private Map<Double, Double> upperBoundary;
    private Map<Double, Double> lowerBoundary;

    private List<SeaWaterInitialDelta234UTableModel> seaWaterInitialDelta234UTableModels;

    private Map<String, OpenSystemIsochronTableModel> mapOfSeawaterModelsToOpenIsochronsModels;

    /**
     *
     * @param sampleName the value of sampleName
     * @param sampleType the value of sampleType
     * @param sampleAnalysisType the value of sampleAnalysisType
     * @param analysisPurpose the value of analysisPurpose
     * @param analyzed the value of analyzed
     * @param isotopeSystem the value of isotopeSystem
     * @param defaultReportSpecsType
     * @throws BadLabDataException
     */
    public ProjectSample(
            String sampleName, String sampleType, String sampleAnalysisType, ANALYSIS_PURPOSE analysisPurpose, boolean analyzed, String isotopeSystem, String defaultReportSpecsType)
            throws BadLabDataException {

        this.sampleName = sampleName;
        this.sampleType = sampleType;
        this.sampleAnalysisType = sampleAnalysisType;
        this.analysisPurpose = analysisPurpose;
        this.analyzed = analyzed;
        this.isotopeSystem = isotopeSystem;
        this.defaultReportSpecsType = defaultReportSpecsType;
        this.aliquots = new Vector<>();
        this.fractions = new Vector<>();

        this.reduxLabData = ReduxLabData.getInstance();
        this.reportSettingsModel = reduxLabData.getDefaultReportSettingsModelBySpecsType(defaultReportSpecsType);
        this.physicalConstantsModel = reduxLabData.getDefaultPhysicalConstantsModel();
        this.sampleAgeInterpretationGUISettings = new SampleDateInterpretationGUIOptions();
        this.changed = false;
        this.reduxSampleFileName = "";
        this.reduxSampleFilePath = "";
        this.concordiaGraphAxesSetup = new GraphAxesSetup("C", 2);
        this.terraWasserburgGraphAxesSetup = new GraphAxesSetup("T-W", 2);
        this.sampleDateModels = new Vector<>();

        this.upperBoundary = new TreeMap<>(new UpperBoundaryComparator());
        this.lowerBoundary = new TreeMap<>(new LowerBoundaryComparator());

        this.seaWaterInitialDelta234UTableModels = ReduxLabData.getInstance().getSeaWaterModels();

        this.mapOfSeawaterModelsToOpenIsochronsModels = new TreeMap<>();

        initFilteredFractionsToAll();

    }

    /**
     *
     * @param x
     * @param y
     * @return
     */
    public boolean calculateIfEllipseIncluded(double x, double y) {
        boolean retVal = true;
        if (upperBoundary == null) {
            upperBoundary = new TreeMap<>(new UpperBoundaryComparator());
        }
        if (lowerBoundary == null) {
            lowerBoundary = new TreeMap<>(new LowerBoundaryComparator());
        }

        if (upperBoundary.size() > 1 && lowerBoundary.size() > 1) {
            // build polygon
            List<Point2D> polygon = new ArrayList<>();

            Iterator<Double> upperKeys = upperBoundary.keySet().iterator();
            while (upperKeys.hasNext()) {
                double age = upperKeys.next();
                double initDelta = upperBoundary.get(age);
                Point2D point = new Point2D(age, initDelta);
                polygon.add(point);
            }

            Iterator<Double> lowerKeys = lowerBoundary.keySet().iterator();
            while (lowerKeys.hasNext()) {
                double age = lowerKeys.next();
                double initDelta = lowerBoundary.get(age);
                Point2D point = new Point2D(age, initDelta);
                polygon.add(point);
            }

            retVal = (math.geom2d.polygon.Polygons2D.windingNumber(polygon, new Point2D(x, y)) != 0);
        }
        return retVal;
    }

    /**
     *
     * @param aliquotNumber the value of aliquotNumber
     * @param aliquotName the value of aliquotName
     * @param physicalConstants the value of physicalConstants
     * @param compiled the value of compiled
     * @param mySESARSampleMetadata the value of mySESARSampleMetadata
     * @param sampleAnalysisType the value of sampleAnalysisType
     */
    @Override
    public AliquotInterface generateDefaultAliquot(//
            int aliquotNumber, String aliquotName, AbstractRatiosDataModel physicalConstants, boolean compiled, SESARSampleMetadata mySESARSampleMetadata, String sampleAnalysisType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    //
    
//    @Override
//    public AliquotInterface generateDefaultAliquot() {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }

    @Override
    public void setUpSample() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getSampleName() {
        return sampleName;
    }

    @Override
    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    @Override
    public void setSampleType(String sampleType) {
        this.sampleType = sampleType;
    }

    @Override
    public String getSampleType() {
        return sampleType;
    }

    @Override
    public void setAnalyzed(boolean analyzed) {
        this.analyzed = analyzed;
    }

    @Override
    public boolean isAnalyzed() {
        return analyzed;
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
    public void setSampleAnnotations(String annotations) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getSampleAnnotations() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ReduxConstants.ANALYSIS_PURPOSE getAnalysisPurpose() {
        return analysisPurpose;
    }

    @Override
    public void setAnalysisPurpose(ReduxConstants.ANALYSIS_PURPOSE analysisPurpose) {
        this.analysisPurpose = analysisPurpose;
        for (int i = 0; i < aliquots.size(); i++) {
            aliquots.get(i).setAnalysisPurpose(analysisPurpose);
        }
    }

    /**
     * gets the <code>reduxSampleFileName</code> of this <code>Sample</code>.
     *
     * @pre this <code>Sample</code> exists
     * @post returns the <code>reduxSampleFileName</code> of this
     * <code>Sample</code>
     *
     * @return <code>String</code> - <code>reduxSampleFileName</code> of this
     * <code>Sample</code>
     */
    @Override
    public String getReduxSampleFileName() {
        return reduxSampleFileName;
    }

    @Override
    public void setReduxSampleFileName(String reduxSampleFileName) {
        this.reduxSampleFileName = reduxSampleFileName;
    }

    /**
     * gets the <code>reduxSampleFilePath</code> of this <code>Sample</code>.
     *
     * @pre this <code>Sample</code> exists
     * @post returns the <code>reduxSampleFilePath</code> of this
     * <code>Sample</code>
     *
     * @return <code>String</code> - <code>reduxSampleFilePath</code> of this
     * <code>Sample</code>
     */
    @Override
    public String getReduxSampleFilePath() {
        return reduxSampleFilePath;
    }

    @Override
    public void setReduxSampleFilePath(String reduxSampleFilePath) {
        this.reduxSampleFilePath = reduxSampleFilePath;
    }

    @Override
    public void setSampleAnalysisType(String sampleAnalysisType) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isSampleTypeProject() {
        return true;
    }

    @Override
    public String getSampleAnalysisType() {
        return sampleAnalysisType;
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
        return false;
    }

    @Override
    public void setCalculateTWrhoForLegacyData(boolean calculateTWrhoForLegacyData) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isCalculateTWrhoForLegacyData() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector<AliquotInterface> getAliquots() {
        return aliquots;
    }

    @Override
    public void setAliquots(Vector<AliquotInterface> aliquots) {
        this.aliquots = aliquots;
    }

    @Override
    public Vector<ETFractionInterface> getFractions() {
        return fractions;
    }

    @Override
    public void setFractions(Vector<ETFractionInterface> UPbFractions) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

    /**
     * @param sampleDateModels the sampleDateModels to set
     */
    @Override
    public void setSampleDateModels(Vector<ValueModel> sampleDateModels) {
        this.sampleDateModels = sampleDateModels;
    }

    @Override
    public void setFractionDataOverriddenOnImport(boolean fractionDataOverriddenOnImport) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setReportSettingsModel(ReportSettingsInterface reportSettingsModel) {
        this.reportSettingsModel = reportSettingsModel;
    }

    @Override
    public ReportSettingsInterface getReportSettingsModel() {
        return reportSettingsModel;
    }

    @Override
    public void setSampleIGSN(String sampleIGSN) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getSampleIGSN() {
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
    public void setValidatedSampleIGSN(boolean validatedSampleIGSN) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isValidatedSampleIGSN() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public SESARSampleMetadata getMySESARSampleMetadata() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setPhysicalConstantsModel(AbstractRatiosDataModel physicalConstantsModel) {
        this.physicalConstantsModel = physicalConstantsModel;
    }

    @Override
    public AbstractRatiosDataModel getPhysicalConstantsModel() throws BadLabDataException {
        return physicalConstantsModel;
    }

    /**
     * @return the terraWasserburgGraphAxesSetup
     */
    @Override
    public GraphAxesSetup getTerraWasserburgGraphAxesSetup() {
        if (terraWasserburgGraphAxesSetup == null) {
            terraWasserburgGraphAxesSetup = new GraphAxesSetup("T-W", 2);
        }
        return terraWasserburgGraphAxesSetup;
    }

    /**
     * @return the concordiaGraphAxesSetup
     */
    @Override
    public GraphAxesSetup getConcordiaGraphAxesSetup() {
        if (concordiaGraphAxesSetup == null) {
            concordiaGraphAxesSetup = new GraphAxesSetup("C", 2);
        }
        return concordiaGraphAxesSetup;
    }

    @Override
    public SampleDateInterpretationGUIOptions getSampleDateInterpretationGUISettings() {
        return sampleAgeInterpretationGUISettings;
    }

    /**
     * sets the <code>sampleAgeInterpretationGUISettings</code> of this
     * <code>Sample</code> to the argument
     * <code>sampleAgeInterpretationGUISettings</code>
     *
     * @pre argument <code>sampleAgeInterpretationGUISettings</code> is a valid
     * <code>SampleDateInterpretationGUIOptions</code>
     * @post this <code>Sample</code>'s
     * <code>sampleAgeInterpretationGUISettings</code> is set to argument
     * <code>sampleAgeInterpretationGUISettings</code>
     *
     * @param sampleAgeInterpretationGUISettings value to which      <code>
     *                                              sampleAgeInterpretationGUISettings</code> of this <code>Sample</code>
     * will be set
     */
    @Override
    public void setSampleAgeInterpretationGUISettings(SampleDateInterpretationGUIOptions sampleAgeInterpretationGUISettings) {
        this.sampleAgeInterpretationGUISettings = sampleAgeInterpretationGUISettings;
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
            isotopeSystem = "UPb";
        }
        return isotopeSystem;
    }

    /**
     * @return the defaultReportSpecsType
     */
    public String getDefaultReportSpecsType() {
        if (defaultReportSpecsType == null) {
            defaultReportSpecsType = "UPb";
        }
        return defaultReportSpecsType;
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
        for (int i = 0; i < fractions.size(); i++) {
            filteredFractionIDs.add(fractions.get(i).getFractionID());
        }
    }

    /**
     * @return the upperBoundary
     */
    public Map<Double, Double> getUpperBoundary() {
        if (upperBoundary == null) {
            upperBoundary = new TreeMap<>(new UpperBoundaryComparator());
        }

        return upperBoundary;
    }

    /**
     * @param upperBoundary the upperBoundary to set
     */
    public void setUpperBoundary(Map<Double, Double> upperBoundary) {
        this.upperBoundary = upperBoundary;
    }

    /**
     * @return the lowerBoundary
     */
    public Map<Double, Double> getLowerBoundary() {
        if (lowerBoundary == null) {
            lowerBoundary = new TreeMap<>(new LowerBoundaryComparator());
        }
        return lowerBoundary;
    }

    /**
     * @param lowerBoundary the lowerBoundary to set
     */
    public void setLowerBoundary(Map<Double, Double> lowerBoundary) {
        this.lowerBoundary = lowerBoundary;
    }

    /**
     * @return the seaWaterInitialDelta234UTableModel
     */
    @Override
    public List<SeaWaterInitialDelta234UTableModel> getSeaWaterInitialDelta234UTableModels() {
        if (seaWaterInitialDelta234UTableModels == null) {
            this.seaWaterInitialDelta234UTableModels = ReduxLabData.getInstance().getSeaWaterModels();
        }
        return seaWaterInitialDelta234UTableModels;
    }

    /**
     * @param seaWaterInitialDelta234UTableModel the
     * seaWaterInitialDelta234UTableModel to set
     */
    public void setSeaWaterInitialDelta234UTableModels(List<SeaWaterInitialDelta234UTableModel> seaWaterInitialDelta234UTableModel) {
        this.seaWaterInitialDelta234UTableModels = seaWaterInitialDelta234UTableModel;
    }

    /**
     * @return the mapOfSeawaterModelsToOpenIsochronsModels
     */
    public Map<String, OpenSystemIsochronTableModel> updateMapOfSeawaterModelsToOpenIsochronsModels() {
        if (mapOfSeawaterModelsToOpenIsochronsModels == null) {
            this.mapOfSeawaterModelsToOpenIsochronsModels = new TreeMap<>();
        }

        // update list
        for (SeaWaterInitialDelta234UTableModel swm : ReduxLabData.getInstance().getSeaWaterModels()) {
            if (!mapOfSeawaterModelsToOpenIsochronsModels.containsKey(swm.getNameAndVersion())) {
                mapOfSeawaterModelsToOpenIsochronsModels.put(swm.getNameAndVersion(), new OpenSystemIsochronTableModel());
            }
        }
        return mapOfSeawaterModelsToOpenIsochronsModels;
    }

    public List<OpenSystemIsochronTableModel> updateListOfOpenIsochronModels() {
        // first update lab data
        for (SeaWaterInitialDelta234UTableModel model : seaWaterInitialDelta234UTableModels) {
            if (!ReduxLabData.getInstance().getSeaWaterModels().contains(model)) {
                ReduxLabData.getInstance().addSeaWaterModel(model);
            }
        }
        // then update this sample
        for (SeaWaterInitialDelta234UTableModel model : ReduxLabData.getInstance().getSeaWaterModels()) {
            if (!seaWaterInitialDelta234UTableModels.contains(model)) {
                seaWaterInitialDelta234UTableModels.add(model);
                mapOfSeawaterModelsToOpenIsochronsModels.put(model.getNameAndVersion(), new OpenSystemIsochronTableModel());
            }
        }

        List<OpenSystemIsochronTableModel> models = new ArrayList<>();
        List<String> badModels = new ArrayList<>();

        for (Entry<String, OpenSystemIsochronTableModel> entry : updateMapOfSeawaterModelsToOpenIsochronsModels().entrySet()) {
            try {
                SeaWaterInitialDelta234UTableModel swm = ReduxLabData.getInstance().getASeaWaterModel(entry.getKey());
                entry.getValue().setSeaWaterInitialDelta234UTableModel(swm);
                models.add(entry.getValue());
                if (!seaWaterInitialDelta234UTableModels.contains(swm)) {
                    seaWaterInitialDelta234UTableModels.add(swm);
                }
            } catch (BadLabDataException badLabDataException) {
                badModels.add(entry.getKey());
            }
        }

        return models;
    }

    private boolean seawaterModelsListContainsModelByName(String modelName, List<OpenSystemIsochronTableModel> models) {
        boolean retVal = false;

        return retVal;
    }
}
