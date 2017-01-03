/*
 * SampleAnalysisWorkflowManagerInterface.java
 *
  * Copyright 2006-2017 James F. Bowring and www.Earth-Time.org
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
package org.earthtime.UPb_Redux.dialogs.sampleManagers.analysisManagers;

import java.io.File;



/**
 *
 * @author James F. Bowring
 */
public interface SampleAnalysisWorkflowManagerInterface {

    /**
     * 
     */
    public void setSize ();

    /**
     * 
     * @return
     */
    public File getSampleMetaDataFolder ();

    /**
     * 
     * @param sampleMetaDataFolder
     */
    public void setSampleMetaDataFolder ( File sampleMetaDataFolder );

    /**
     * 
     * @return
     */
    public boolean isInitialized ();

    /**
     * 
     * @return
     */
    public boolean isNewSample ();

    /**
     * 
     * @param newSample
     */
    public void setNewSample ( boolean newSample );

    /**
     * 
     * @return
     */
    public File getSampleFolder ();

    /**
     * 
     * @param sampleFolder
     */
    public void setSampleFolder ( File sampleFolder );

    /**
     * 
     * @return
     */
    public File getImportedXMLFractionsFolder ();

    /**
     * 
     * @param importedXMLFractionsFolder
     */
    public void setImportedXMLFractionsFolder ( File importedXMLFractionsFolder );

    /**
     * 
     * @return
     */
    public File getTripoliRawDataFolder ();

    /**
     * 
     * @param tripoliRawDataFolder
     */
    public void setTripoliRawDataFolder ( File tripoliRawDataFolder );

    /**
     * 
     * @param b
     */
    public void setVisible ( boolean b );
    
    
}
