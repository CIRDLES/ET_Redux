/*
 * SampleLegacyDataManagerDialogForIDTIMS_MIT.java
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
package org.earthtime.UPb_Redux.dialogs.sampleManagers.legacyManagers;

import java.io.File;
import org.earthtime.UPb_Redux.samples.sampleImporters.SampleImporterFromIDTIMSLegacyCSVFile_MIT;
import org.earthtime.samples.SampleInterface;

/**
 *
 * @author James F. Bowring
 */
public class SampleLegacyDataManagerDialogForIDTIMS_MIT extends AbstractSampleLegacyManagerDialog {

    /**
     * 
     * @param parent
     * @param modal
     * @param sample
     * @param importFractionFolderMRU
     */
    public SampleLegacyDataManagerDialogForIDTIMS_MIT (
            java.awt.Frame parent,
            boolean modal,
            SampleInterface sample,
            File importFractionFolderMRU ) {

        super( parent,
                modal,
                "ID-TIMS Data",
                sample,
                new SampleImporterFromIDTIMSLegacyCSVFile_MIT(),
                importFractionFolderMRU );
    }
}
