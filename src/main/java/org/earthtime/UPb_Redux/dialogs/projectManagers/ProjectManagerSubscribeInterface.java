/*
 * ProjectManagerSubscribeInterface.java
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
package org.earthtime.UPb_Redux.dialogs.projectManagers;

import java.util.ArrayList;
import org.earthtime.Tripoli.samples.AbstractTripoliSample;
import org.earthtime.Tripoli.sessions.TripoliSessionInterface;
import org.earthtime.projects.ProjectI;

/**
 *
 * @author samuelbowring
 */
public interface ProjectManagerSubscribeInterface {

    /**
     *
     * @param amChanged the value of amChanged
     */
    public void updateDataChangeStatus(boolean amChanged);

    /**
     *
     * @param usingFullPropagation
     */
    public void reProcessFractionRawRatios(boolean usingFullPropagation);

    /**
     *
     * @return
     */
    public ProjectI getProject();// needs to be tripoli's sample not project for commonLeadCorrectionHighestLevel

    /**
     *
     * @param tripoliSession the value of tripoliSession
     * @param tripoliSamplesSorted the value of tripoliSamplesSorted
     */
    public void displaySamples(TripoliSessionInterface tripoliSession, ArrayList<AbstractTripoliSample> tripoliSamplesSorted);

    public void initializeSessionManager(boolean doSetup, boolean doShow, boolean doCorrections);
}
