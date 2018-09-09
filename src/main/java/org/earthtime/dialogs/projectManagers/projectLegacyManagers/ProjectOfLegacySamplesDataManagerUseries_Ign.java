/*
 * ProjectOfLegacySamplesDataManagerUseries_Ign.java
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
package org.earthtime.dialogs.projectManagers.projectLegacyManagers;

import java.awt.Frame;
import java.io.File;
import org.earthtime.dataDictionaries.FileDelimiterTypesEnum;
import org.earthtime.projects.ProjectInterface;
import org.earthtime.projects.projectImporters.UThProjectImporters.ProjectOfLegacySamplesImporterFromTSVFileUseries_Ign;

/**
 *
 * @author James F. Bowring
 */
public class ProjectOfLegacySamplesDataManagerUseries_Ign extends AbstractProjectOfLegacySamplesDataManagerDialog {

    /**
     * 
     * @param parent
     * @param modal
     * @param project
     * @param importFractionFolderMRU
     */
    public ProjectOfLegacySamplesDataManagerUseries_Ign (
            Frame parent,
            boolean modal,
            ProjectInterface project,
            File importFractionFolderMRU ) {

        super(parent,
                modal,
                "Legacy Useries Igneous Data tab-delimited",
                project,
                new ProjectOfLegacySamplesImporterFromTSVFileUseries_Ign(FileDelimiterTypesEnum.TAB),
                importFractionFolderMRU);
    }
}
