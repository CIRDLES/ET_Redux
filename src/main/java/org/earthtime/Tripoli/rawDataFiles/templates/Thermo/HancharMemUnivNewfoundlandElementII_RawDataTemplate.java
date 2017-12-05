/*
 * HancharMemUnivNewfoundlandElementII_RawDataTemplate
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
package org.earthtime.Tripoli.rawDataFiles.templates.Thermo;

import java.util.TimeZone;
import org.earthtime.Tripoli.dataModels.inputParametersModels.AbstractAcquisitionModel;
import org.earthtime.Tripoli.dataModels.inputParametersModels.SingleCollectorAcquisition;
import org.earthtime.Tripoli.massSpecSetups.singleCollector.ThermoFinnigan.MemUnivNewfoundlandElementIISetupUPb;
import org.earthtime.Tripoli.rawDataFiles.templates.AbstractRawDataFileTemplate;
import org.earthtime.dataDictionaries.FileTypeEnum;

/**
 *
 * @author James F. Bowring
 */
public final class HancharMemUnivNewfoundlandElementII_RawDataTemplate extends AbstractRawDataFileTemplate{

    //Class variables
    private static final long serialVersionUID = 2549874427568944698L;

    private static HancharMemUnivNewfoundlandElementII_RawDataTemplate instance = new HancharMemUnivNewfoundlandElementII_RawDataTemplate();

    private HancharMemUnivNewfoundlandElementII_RawDataTemplate() {
        super();

        this.NAME = "Memorial Univ Newfoundland Element II";
        this.aboutInfo = "analysis runs setup by Hanchar";
        this.fileType = FileTypeEnum.fin2;
        this.startOfFirstLine = "Finnigan";
        this.startOfDataSectionFirstLine = "Time";
        this.startOfEachBlockFirstLine = "Time";
        this.blockStartOffset = 8;
        this.blockSize = 500;
        this.standardIDs = new String[]//
        {"GJ1"};
        this.timeZone = TimeZone.getTimeZone("GMT");
        this.defaultParsingOfFractionsBehavior = 1;
        this.elementsByIsotopicMass = new String[]{"204", "206", "207", "208", "232", "238"};
        this.massSpecSetup = MemUnivNewfoundlandElementIISetupUPb.getInstance();
    }

    /**
     *
     * @return
     */
    public static HancharMemUnivNewfoundlandElementII_RawDataTemplate getInstance() {
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
