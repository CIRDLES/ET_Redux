/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.earthtime.projects;

import java.io.File;
import java.util.ArrayList;
import org.earthtime.Tripoli.dataModels.inputParametersModels.AbstractAcquisitionModel;
import org.earthtime.Tripoli.rawDataFiles.handlers.AbstractRawDataFileHandler;
import org.earthtime.Tripoli.sessions.TripoliSessionInterface;
import org.earthtime.UPb_Redux.ReduxConstants.ANALYSIS_PURPOSE;
import org.earthtime.UPb_Redux.samples.SampleI;
import org.earthtime.exceptions.ETException;

/**
 *
 * @author samuelbowring
 */
public interface ProjectI {

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
    ArrayList<SampleI> getProjectSamples ();

    /**
     * @return the compiledSuperSample
     */
    SampleI getSuperSample ();

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
    void setProjectSamples ( ArrayList<SampleI> projectSamples );

    /**
     * @param compiledSuperSample the compiledSuperSample to set
     */
    void setSuperSample ( SampleI superSample );

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
    public void exportProjectSamples()throws ETException;
}
