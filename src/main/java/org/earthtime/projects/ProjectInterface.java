/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.earthtime.projects;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import org.earthtime.Tripoli.dataModels.inputParametersModels.AbstractAcquisitionModel;
import org.earthtime.Tripoli.rawDataFiles.handlers.AbstractRawDataFileHandler;
import org.earthtime.Tripoli.sessions.TripoliSessionInterface;
import org.earthtime.UPb_Redux.ReduxConstants.ANALYSIS_PURPOSE;
import org.earthtime.exceptions.ETException;
import org.earthtime.samples.SampleInterface;

/**
 *
 * @author samuelbowring
 */
public interface ProjectInterface {

    /**
     * @return the locationOfProjectReduxFile
     */
    File getLocationOfProjectReduxFile ();

    /**
     * @return the projectName
     */
    String getProjectName ();

    /**
     * @return the projectSamples
     */
    ArrayList<SampleInterface> getProjectSamples ();

    /**
     * @return the compiledSuperSample
     */
    SampleInterface getSuperSample ();

    /**
     * @return the tripoliSession
     */
    TripoliSessionInterface getTripoliSession ();

    /**
     *
     */
    void prepareSamplesForRedux ();

    /**
     *
     * @return
     */
    File saveProjectFileAs ();

    /**
     *
     */
    void saveTheProjectAsSerializedReduxFile ();

    /**
     *
     * @param file
     */
    void saveTheProjectAsSerializedReduxFile ( File file );

    /**
     * @param locationOfProjectReduxFile the locationOfProjectReduxFile to set
     */
    void setLocationOfProjectReduxFile ( File locationOfProjectReduxFile );

    /**
     * @param projectName the projectName to set
     */
    void setProjectName ( String projectName );

    /**
     * @param projectSamples the projectSamples to set
     */
    void setProjectSamples ( ArrayList<SampleInterface> projectSamples );

    /**
     * @param superSample
     * @param compiledSuperSample the compiledSuperSample to set
     */
    void setSuperSample ( SampleInterface superSample );

    /**
     * @param tripoliSession the tripoliSession to set
     */
    void setTripoliSession ( TripoliSessionInterface tripoliSession );

    /**
     *
     * @return
     */
    public ANALYSIS_PURPOSE getAnalysisPurpose ();

    /**
     * @param analysisPurpose the analysisPurpose to set
     */
    public void setAnalysisPurpose ( ANALYSIS_PURPOSE analysisPurpose );

    /**
     *
     * @return
     */
    public AbstractAcquisitionModel getAcquisitionModel ();

    /**
     *
     * @param acquisitionModel
     */
    public void setAcquisitionModel ( AbstractAcquisitionModel acquisitionModel );

    /**
     *
     * @return
     */
    public AbstractRawDataFileHandler getRawDataFileHandler ();

    /**
     *
     * @param rawDataFileHandler
     */
    public void setRawDataFileHandler ( AbstractRawDataFileHandler rawDataFileHandler );
    
    /**
     *
     */
    public Path exportProjectSamples()throws ETException;
    
    public SampleInterface getCompiledSuperSample();
    
    /**
     * @return the locationOfDataImportFile
     */
    public File getLocationOfDataImportFile();
    /**
     * @param locationOfDataImportFile the locationOfDataImportFile to set
     */
    public void setLocationOfDataImportFile(File locationOfDataImportFile);

}
