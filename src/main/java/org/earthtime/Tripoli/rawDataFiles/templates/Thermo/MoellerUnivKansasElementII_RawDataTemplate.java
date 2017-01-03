/*
 * MoellerUnivKansasElementII_RawDataTemplate
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
package org.earthtime.Tripoli.rawDataFiles.templates.Thermo;

import java.io.Serializable;
import java.util.TimeZone;
import org.earthtime.Tripoli.dataModels.inputParametersModels.AbstractAcquisitionModel;
import org.earthtime.Tripoli.dataModels.inputParametersModels.SingleCollectorAcquisition;
import org.earthtime.Tripoli.massSpecSetups.singleCollector.ThermoFinnigan.UnivKansasElementIISetupUPb;
import org.earthtime.Tripoli.rawDataFiles.templates.AbstractRawDataFileTemplate;
import org.earthtime.dataDictionaries.FileTypeEnum;

/**
 *
 * @author James F. Bowring
 */
public final class MoellerUnivKansasElementII_RawDataTemplate extends AbstractRawDataFileTemplate implements //
        Comparable<AbstractRawDataFileTemplate>,
        Serializable {

     // Class variables
    private static final long serialVersionUID = -7723699052591631306L;
    private static MoellerUnivKansasElementII_RawDataTemplate instance = new MoellerUnivKansasElementII_RawDataTemplate();

    private MoellerUnivKansasElementII_RawDataTemplate() {
        super();

        this.NAME = "Univ Kansas Element II";
        this.aboutInfo = "analysis runs setup by students";
        this.fileType = FileTypeEnum.fin2;
        this.startOfFirstLine = "Finnigan";
        this.startOfDataSectionFirstLine = "Time";
        this.startOfEachBlockFirstLine = "Time";
        this.blockStartOffset = 8;
        this.blockSize = 190;//165;//222;
        // per Noah email 26 March 2016: For each analysis, the sample name will precede the first hyphen in the filename. 
        // Any one of these could be used as a primary or secondary reference material or any given session. 
        //
        //GJ1 or GJ - GJ1 zircon
        //MT - Mud Tank zircon
        //PL - Plesovice zircon
        //FCT - Fish Canyon Tuff zircon
        this.standardIDs = new String[]//
        {"GJ", "GJ1", "MT", "PL", "FCT"};
        this.timeZone = TimeZone.getTimeZone("GMT");
        this.defaultParsingOfFractionsBehavior = 1;
        this.massSpecSetup = UnivKansasElementIISetupUPb.getInstance();
    }

    /**
     *
     * @return
     */
    public static MoellerUnivKansasElementII_RawDataTemplate getInstance() {
        return instance;
    }

    /**
     *
     * @return
     */
    @Override
    public AbstractAcquisitionModel makeNewAcquisitionModel() {
        this.acquisitionModel = new SingleCollectorAcquisition();
        return acquisitionModel;
    }
}
