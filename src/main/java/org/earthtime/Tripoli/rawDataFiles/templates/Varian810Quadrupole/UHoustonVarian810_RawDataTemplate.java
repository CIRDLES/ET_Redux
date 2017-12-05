/*
 * LaserchronElementII_RawDataTemplate_C
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
package org.earthtime.Tripoli.rawDataFiles.templates.Varian810Quadrupole;

import java.util.TimeZone;
import org.earthtime.Tripoli.dataModels.inputParametersModels.AbstractAcquisitionModel;
import org.earthtime.Tripoli.dataModels.inputParametersModels.SingleCollectorAcquisition;
import org.earthtime.Tripoli.massSpecSetups.singleCollector.Varian810Quadrupole.UHoustonVarian810Setup;
import org.earthtime.Tripoli.rawDataFiles.templates.AbstractRawDataFileTemplate;
import org.earthtime.dataDictionaries.FileTypeEnum;

/**
 *
 * @author James F. Bowring
 */
public final class UHoustonVarian810_RawDataTemplate extends AbstractRawDataFileTemplate {

    //Class variables   
    //private static final long serialVersionUID = -4082959888559262169L;
    private static UHoustonVarian810_RawDataTemplate instance = new UHoustonVarian810_RawDataTemplate();

    private UHoustonVarian810_RawDataTemplate() {
        super();

        this.NAME = "UHouston Varian810";
        this.aboutInfo = "analysis runs setup to process 201, 202, 204, 206, 207, 208, 232, 238";
        this.fileType = FileTypeEnum.prn;
        this.startOfFirstLine = "Processed Time/Date";
        this.startOfDataSectionFirstLine = "";
        this.startOfEachBlockFirstLine = "";
        this.blockStartOffset = 0;
        this.blockSize = 0;
        this.standardIDs = new String[]//
        {"PL", "FC", "SL", "R3"};
        this.timeZone = TimeZone.getTimeZone("GMT");
        this.defaultParsingOfFractionsBehavior = 1;
        this.elementsByIsotopicMass = new String[]{};
        this.massSpecSetup = UHoustonVarian810Setup.getInstance();
    }

    /**
     *
     * @return
     */
    public static UHoustonVarian810_RawDataTemplate getInstance() {
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
