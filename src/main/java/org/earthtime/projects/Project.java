/*
 * Project.java
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

import java.awt.Frame;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.filechooser.FileFilter;
import org.earthtime.Tripoli.dataModels.inputParametersModels.AbstractAcquisitionModel;
import org.earthtime.Tripoli.fractions.TripoliFraction;
import org.earthtime.Tripoli.rawDataFiles.handlers.AbstractRawDataFileHandler;
import org.earthtime.Tripoli.samples.AbstractTripoliSample;
import org.earthtime.Tripoli.sessions.TripoliSessionInterface;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.aliquots.UPbReduxAliquot;
import org.earthtime.UPb_Redux.filters.ReduxFileFilter;
import org.earthtime.UPb_Redux.fractions.FractionI;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFractionI;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbLAICPMSFraction;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbSHRIMPFraction;
import org.earthtime.UPb_Redux.samples.Sample;
import org.earthtime.UPb_Redux.user.ReduxPersistentState;
import org.earthtime.UPb_Redux.utilities.ETSerializer;
import org.earthtime.aliquots.AliquotInterface;
import org.earthtime.aliquots.ReduxAliquotInterface;
import org.earthtime.dataDictionaries.DataDictionary;
import org.earthtime.dataDictionaries.SampleAnalysisTypesEnum;
import org.earthtime.dataDictionaries.SampleTypesEnum;
import org.earthtime.exceptions.ETException;
import org.earthtime.samples.SampleInterface;
import org.earthtime.utilities.FileHelper;

/**
 * Project is an organizing principle proposed at "ReduxFest 2010" at MIT that
 * provides for the assembly of disparate samples and their aliquots. Project
 * implementation began in Oct 2011. We will keep the same functionality for
 * Samples, with the caveat that now, any Sample will belong to a default
 * project of one sample only. Any Sample can be a member of any number of
 * Projects. The .redux file extension will do double duty for samples and
 * projects.
 *
 * @author James F. Bowring
 */
public class Project implements
        Serializable,
        EarthTimeSerializedFileInterface,
        ProjectInterface {

    // Class variables
    private static final long serialVersionUID = 6292924571103425985L;
    // instance variables
    private String projectName;
    private SampleInterface compiledSuperSample;
    private ArrayList<SampleInterface> projectSamples;
    private TripoliSessionInterface tripoliSession;
    private File locationOfProjectReduxFile;
    private boolean changed;
    private ReduxConstants.ANALYSIS_PURPOSE analysisPurpose;
    private AbstractAcquisitionModel acquisitionModel;
    private AbstractRawDataFileHandler rawDataFileHandler;
    private ReduxPersistentState myState;
    // for Legacy projects
    private File locationOfDataImportFile;
    private SampleAnalysisTypesEnum sampleAnalysisType;

    /**
     *
     */
    public Project() {
        this.projectName = "Empty Project";
        this.compiledSuperSample = null;
        this.projectSamples = new ArrayList<>();
        this.tripoliSession = null;
        this.locationOfProjectReduxFile = null;

        this.acquisitionModel = null;
        this.rawDataFileHandler = null;
        this.sampleAnalysisType = SampleAnalysisTypesEnum.LAICPMS;

    }

    /**
     *
     * @param myState
     */
    public Project(ReduxPersistentState myState) {
        this();
        this.myState = myState;
    }

    /**
     *
     * @return
     */
    @Override
    public File saveProjectFileAs() {

        String dialogTitle = "Save Redux file for this Project: *.redux";
        final String fileExtension = ".redux";
        String projectFileName = projectName + fileExtension;
        FileFilter nonMacFileFilter = new ReduxFileFilter();

        File selectedFile;
        String projectFolderPath;
        if (locationOfProjectReduxFile != null) {
            projectFolderPath = locationOfProjectReduxFile.getParent();
        } else {
            projectFolderPath = myState.getMRUProjectFolderPath();
        }

        selectedFile = FileHelper.AllPlatformSaveAs(
                new Frame(),
                dialogTitle,
                projectFolderPath,
                fileExtension,
                projectFileName,
                nonMacFileFilter);

        if (selectedFile != null) {
            saveTheProjectAsSerializedReduxFile(selectedFile);
        }
        return selectedFile;
    }

    /**
     *
     */
    @Override
    public final void saveTheProjectAsSerializedReduxFile() {

        if (locationOfProjectReduxFile == null) {
            locationOfProjectReduxFile = saveProjectFileAs();
        }

        if (locationOfProjectReduxFile != null) {
            try {
                ETSerializer.SerializeObjectToFile(this, locationOfProjectReduxFile.getCanonicalPath());
            } catch (IOException ex) {
                Logger.getLogger(Project.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ETException etexception) {

            }
        }

    }

    /**
     *
     * @param file
     */
    @Override
    public final void saveTheProjectAsSerializedReduxFile(
            File file) {
        locationOfProjectReduxFile = file;
        saveTheProjectAsSerializedReduxFile();

        // APRIL 2014 update the project so it knows where it is
        setLocationOfProjectReduxFile(locationOfProjectReduxFile);
        // update MRU status
        myState.updateMRUProjectList(locationOfProjectReduxFile);
    }

    /**
     *
     */
    @Override
    public void prepareSamplesForRedux() {
        System.out.println("Preparing Samples for Redux");
        
        // walk the tripolisamples and convert to samples
        // Redux will end up with a set of aliquots (aka compiled sample) each named for the sample (1-to-1)
        // and a set of fractions each associated with an aliquot

        // make a super-sample or projectsample to leverage existing Redux
        if (compiledSuperSample == null) {
                compiledSuperSample = new Sample( //
                        projectName, //
                        SampleTypesEnum.PROJECT.getName(), //
                        SampleAnalysisTypesEnum.TRIPOLIZED.getName(), //
                        ReduxConstants.ANALYSIS_PURPOSE.DetritalSpectrum, "UPb", "UPb");
        }

        ArrayList<AbstractTripoliSample> tripoliSamples = tripoliSession.getTripoliSamples();
        for (AbstractTripoliSample tripoliSample : tripoliSamples) {
            // check for primary standard and leave it out
            if (true) {//oct 2014 want to include standards now (!tripoliSample.isPrimaryReferenceMaterial()) {

                // june 2016
                // determine if sample already processed
                SampleInterface sample = null;
                AliquotInterface aliquot = null;
                for (int i = 0; i < projectSamples.size(); i++) {
                    if (projectSamples.get(i).getSampleName().equalsIgnoreCase(tripoliSample.getSampleName())) {
                        sample = projectSamples.get(i);
                        aliquot = sample.getAliquots().get(0);
                        break;
                    }
                }
                if (sample == null) {
                    sample = new Sample( //
                            tripoliSample.getSampleName(), //
                            SampleTypesEnum.ANALYSIS.getName(), //
                            sampleAnalysisType.getName(), //
                            analysisPurpose, "UPb", "UPb");

                    projectSamples.add(sample);

                    try {
                        aliquot = sample.addNewAliquot(tripoliSample.getSampleName());
                        aliquot.setAnalysisPurpose(analysisPurpose);
                        // TODO: Enum of inst methods
                        aliquot.setAliquotInstrumentalMethod(DataDictionary.AliquotInstrumentalMethod[5]);
                    } catch (ETException eTException) {
                    }

                    System.out.println("New Aliquot is # " + ((UPbReduxAliquot) aliquot).getAliquotNumber() + " = " + aliquot.getAliquotName());
                }

                SortedSet<TripoliFraction> tripoliSampleFractions = tripoliSample.getSampleFractions();
                for (Iterator<TripoliFraction> it = tripoliSampleFractions.iterator(); it.hasNext();) {
                    TripoliFraction tf = it.next();

                    // june 2016
                    // determine if fraction already exists
                    if (!sample.containsFractionByName(tf.getFractionID())) {

                        // feb 2016
                        FractionI reduxVersionTripolizedFraction = null;
                        if (sampleAnalysisType.compareTo(SampleAnalysisTypesEnum.LAICPMS) == 0) {
                            reduxVersionTripolizedFraction = new UPbLAICPMSFraction(tf.getFractionID());
                        } else if (sampleAnalysisType.compareTo(SampleAnalysisTypesEnum.SHRIMP) == 0) {
                            reduxVersionTripolizedFraction = new UPbSHRIMPFraction(tf.getFractionID());
                        }

                        reduxVersionTripolizedFraction.setSampleName(tripoliSample.getSampleName());
                        // add to tripoli fraction so its UPbFraction can be contiunously updated
                        tf.setuPbFraction(reduxVersionTripolizedFraction);
                        // dec 2015
                        ((UPbFractionI) reduxVersionTripolizedFraction).setTripoliFraction(tf);
                        reduxVersionTripolizedFraction.setRejected(!tf.isIncluded());

                        // automatically added to aliquot #1 as we are assuming only one aliquot in this scenario
                        sample.addFraction(reduxVersionTripolizedFraction);
                        // feb 2015 in prep for export
                        ((ReduxAliquotInterface) aliquot).getAliquotFractions().add(reduxVersionTripolizedFraction);
                    }
                }

                // this forces aliquot fraction population
                SampleInterface.copyAliquotIntoSample(compiledSuperSample, sample.getAliquotByName(aliquot.getAliquotName()), new UPbReduxAliquot());

            }// if true
        }

        // first pass without any user interaction
        tripoliSession.setEstimatedPlottingPointsCount(1000);

    }

    /**
     *
     * @return @throws org.earthtime.exceptions.ETException
     */
    @Override
    public Path exportProjectSamples() throws ETException {

        File projectSamplesFolder = new File(locationOfProjectReduxFile.getParent() + File.separatorChar + projectName + "_Samples");
        boolean jobCompleted = true;

        if (projectSamplesFolder.exists()) {
            File[] filesFound = projectSamplesFolder.listFiles();
            for (File filesFound1 : filesFound) {
                jobCompleted = jobCompleted && filesFound1.delete();
            }
        } else {
            jobCompleted = jobCompleted && projectSamplesFolder.mkdir();
        }

        if (jobCompleted) {
            prepareSamplesForExport();

            for (int i = 0; i < projectSamples.size(); i++) {
                SampleInterface sample = projectSamples.get(i);

                File sampleFile = new File(//
                        projectSamplesFolder.getAbsolutePath() + File.separatorChar + sample.getSampleName());

                // first write sample out
                SampleInterface.saveSampleAsSerializedReduxFile(sample, sampleFile);

            }
        } else {
            throw new ETException(null, "Unable to process " + projectSamplesFolder.getAbsolutePath());
        }

        return projectSamplesFolder.toPath();
    }

    public void prepareSamplesForExport() {
        for (int i = 0; i < projectSamples.size(); i++) {
            SampleInterface sample = projectSamples.get(i);
            System.out.println("Preparing for export Sample: " + sample.getSampleName());

            // oct 2014 specify sample types
            if (SampleInterface.isAnalysisTypeTripolized(compiledSuperSample.getSampleAnalysisType())) {
                sample.setSampleType(SampleTypesEnum.ANALYSIS.getName());
            } else {
                sample.setSampleType(SampleTypesEnum.LEGACY.getName());
                sample.setAnalyzed(true);
            }
            sample.setLegacyStatusForReportTable();
        }
    }

    /**
     * @return the projectSamples
     */
    @Override
    public ArrayList<SampleInterface> getProjectSamples() {
        return projectSamples;
    }

    /**
     * @param projectSamples the projectSamples to set
     */
    @Override
    public void setProjectSamples(ArrayList<SampleInterface> projectSamples) {
        this.projectSamples = projectSamples;
    }

    /**
     * @return the tripoliSession
     */
    @Override
    public TripoliSessionInterface getTripoliSession() {
        return tripoliSession;
    }

    /**
     * @param tripoliSession the tripoliSession to set
     */
    @Override
    public void setTripoliSession(TripoliSessionInterface tripoliSession) {
        this.tripoliSession = tripoliSession;
    }

    /**
     * @return the projectName
     */
    @Override
    public String getProjectName() {
        return projectName;
    }

    /**
     * @param projectName the projectName to set
     */
    @Override
    public void setProjectName(String projectName) {
        if (projectName.length() > 0) {
            this.projectName = projectName;
        }
    }

    /**
     * @return the locationOfProjectReduxFile
     */
    @Override
    public File getLocationOfProjectReduxFile() {
        return locationOfProjectReduxFile;
    }

    /**
     * @param locationOfProjectReduxFile the locationOfProjectReduxFile to set
     */
    @Override
    public void setLocationOfProjectReduxFile(File locationOfProjectReduxFile) {
        this.locationOfProjectReduxFile = locationOfProjectReduxFile;
    }

    /**
     * @return the compiledSuperSample
     */
    @Override
    public SampleInterface getSuperSample() {
        return compiledSuperSample;
    }

    /**
     * @param superSample
     */
    @Override
    public void setSuperSample(SampleInterface superSample) {
        this.compiledSuperSample = superSample;
    }

    /**
     * @return the changed
     */
    public boolean isChanged() {
        return changed;
    }

    /**
     * @param changed the changed to set
     */
    public void setChanged(boolean changed) {
        this.changed = changed;
    }

    /**
     * @return the analysisPurpose
     */
    @Override
    public ReduxConstants.ANALYSIS_PURPOSE getAnalysisPurpose() {
        return analysisPurpose;
    }

    /**
     * @param analysisPurpose the analysisPurpose to set
     */
    @Override
    public void setAnalysisPurpose(ReduxConstants.ANALYSIS_PURPOSE analysisPurpose) {
        this.analysisPurpose = analysisPurpose;
        
        for (int i = 0; i < projectSamples.size(); i ++){
            projectSamples.get(i).setAnalysisPurpose(analysisPurpose);
        }
    }

    /**
     * @return the acquisitionModel
     */
    @Override
    public AbstractAcquisitionModel getAcquisitionModel() {
        return acquisitionModel;
    }

    /**
     * @param acquisitionModel the acquisitionModel to set
     */
    @Override
    public void setAcquisitionModel(AbstractAcquisitionModel acquisitionModel) {
        this.acquisitionModel = acquisitionModel;
    }

    /**
     * @return the rawDataFileHandler
     */
    @Override
    public AbstractRawDataFileHandler getRawDataFileHandler() {
        return rawDataFileHandler;
    }

    /**
     * @param rawDataFileHandler the rawDataFileHandler to set
     */
    @Override
    public void setRawDataFileHandler(AbstractRawDataFileHandler rawDataFileHandler) {
        this.rawDataFileHandler = rawDataFileHandler;
    }

    /**
     * @return the compiledSuperSample
     */
    @Override
    public SampleInterface getCompiledSuperSample() {
        return compiledSuperSample;
    }

    /**
     * @return the locationOfDataImportFile
     */
    @Override
    public File getLocationOfDataImportFile() {
        if (locationOfDataImportFile == null) {
            locationOfDataImportFile = new File(":");
        }
        return locationOfDataImportFile;
    }

    /**
     * @param locationOfDataImportFile the locationOfDataImportFile to set
     */
    @Override
    public void setLocationOfDataImportFile(File locationOfDataImportFile) {
        this.locationOfDataImportFile = locationOfDataImportFile;
    }

    /**
     * @return the sampleAnalysisType
     */
    public SampleAnalysisTypesEnum getSampleAnalysisType() {
        if (sampleAnalysisType == null) {
            sampleAnalysisType = SampleAnalysisTypesEnum.LAICPMS;
        }
        return sampleAnalysisType;
    }

    /**
     * @param sampleAnalysisType the sampleAnalysisType to set
     */
    public void setSampleAnalysisType(SampleAnalysisTypesEnum sampleAnalysisType) {
        this.sampleAnalysisType = sampleAnalysisType;
    }
}
