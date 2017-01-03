/*
 * VervoortWashStateElementII_RawDataTemplate_Meth1
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
import org.earthtime.Tripoli.massSpecSetups.singleCollector.ThermoFinnigan.WashStateElementIISetupUPbMeth2;
import org.earthtime.Tripoli.rawDataFiles.templates.AbstractRawDataFileTemplate;
import org.earthtime.dataDictionaries.FileTypeEnum;

/**
 *
 * @author James F. Bowring
 */
public final class VervoortWashStateElementII_RawDataTemplate_Meth2 extends AbstractRawDataFileTemplate implements //
        Comparable<AbstractRawDataFileTemplate>,
        Serializable {

//    private static final long serialVersionUID = -8145804446090022397l;
    
    private static VervoortWashStateElementII_RawDataTemplate_Meth2 instance = new VervoortWashStateElementII_RawDataTemplate_Meth2();

    private VervoortWashStateElementII_RawDataTemplate_Meth2() {
        super();

        this.NAME = "Vervoort Wash State Element II Method 2";
        this.aboutInfo = "analysis runs setup by Vervoort for Method 2";
        this.fileType = FileTypeEnum.txt;
        this.startOfFirstLine = "Trace for Mass:";
        this.startOfDataSectionFirstLine = "Time";
        this.startOfEachBlockFirstLine = "Time";
        this.blockStartOffset = 6;
        this.blockSize = 250;//300;
        this.standardIDs = new String[]//
        {"Plesovice", "Peixe", "91500", "FC1"};
        this.timeZone = TimeZone.getTimeZone("PST");
        this.defaultParsingOfFractionsBehavior = 1;
        this.massSpecSetup = WashStateElementIISetupUPbMeth2.getInstance();
    }

    /**
     *
     * @return
     */
    public static VervoortWashStateElementII_RawDataTemplate_Meth2 getInstance() {
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
