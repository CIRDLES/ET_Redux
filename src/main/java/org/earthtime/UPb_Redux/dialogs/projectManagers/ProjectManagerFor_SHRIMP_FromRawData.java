/*
 * Copyright 2006-2016 CIRDLES.org.
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
package org.earthtime.UPb_Redux.dialogs.projectManagers;

import java.util.ArrayList;
import org.earthtime.ETReduxFrame;
import org.earthtime.Tripoli.rawDataFiles.handlers.AbstractRawDataFileHandler;
import org.earthtime.Tripoli.rawDataFiles.handlers.shrimp.ShrimpFileHandler;
import org.earthtime.Tripoli.rawDataFiles.templates.shrimp.ShrimpPrawn_RawDataTemplate_GA;
import org.earthtime.UPb_Redux.dialogs.parameterManagers.AbstractProjectParametersManager;
import org.earthtime.UPb_Redux.dialogs.parameterManagers.ShrimpProjectParamaetersManager;
import org.earthtime.UPb_Redux.dialogs.sessionManagers.SessionAnalysisWorkflowManagerSHRIMP2;
import org.earthtime.UPb_Redux.user.ReduxPersistentState;
import org.earthtime.dialogs.DialogEditor;
import org.earthtime.projects.Project;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public class ProjectManagerFor_SHRIMP_FromRawData extends AbstractProjectManagerForRawData {

    public ProjectManagerFor_SHRIMP_FromRawData(//
            ETReduxFrame parent, //
            boolean modal,
            ReduxPersistentState myState, //
            Project project) {

        super(parent, modal, myState, project);
    }

    @Override
    public void initDialogContent() {
        setSizeAndCenter(1200, 750);

        projectType_label.setText("Project Type: SHRIMP Data aquisition and reduction");

        // initialize all known machines and protocols etc
        // eventually move to xml external files
        knownRawDataFileHandlers = new ArrayList<>();

        // LaserChron Element 2 
        AbstractRawDataFileHandler theSHRIMPFileHandler
                = ShrimpFileHandler.getInstance();
        theSHRIMPFileHandler.getAvailableRawDataFileTemplates()//
                .add(ShrimpPrawn_RawDataTemplate_GA.getInstance());
        knownRawDataFileHandlers.add(theSHRIMPFileHandler);

        super.initDialogContent();
    }

    /**
     *
     * @param doSetup the value of doSetup
     * @param doShow the value of doShow
     * @param doCorrections the value of doCorrections
     */
    @Override
    public void initializeSessionManager(boolean doSetup, boolean doShow, boolean doCorrections) {

        setVisible(false);

        if (doSetup || (mySessionManager == null)) {
            // kill existing
            if (mySessionManager != null) {
                ((DialogEditor) mySessionManager).close();
            }
            mySessionManager
                    = new SessionAnalysisWorkflowManagerSHRIMP2(
                            this, //
                            uPbReduxFrame,
                            false, //
                            tripoliSession);
            mySessionManager.setupTripoliSessionRawDataView();
        }

        // modal call to manager
        mySessionManager.setVisible(doShow);
    }

    @Override
    public boolean showParametersView() {

        parametersView = null;

        parametersView = new ShrimpProjectParamaetersManager(project, this, uPbReduxFrame);
        ((AbstractProjectParametersManager)parametersView).initView();
        ((AbstractProjectParametersManager)parametersView).displayModelInFrame();

        return ((AbstractProjectParametersManager)parametersView).isReadyToProcessData();
    }
}
