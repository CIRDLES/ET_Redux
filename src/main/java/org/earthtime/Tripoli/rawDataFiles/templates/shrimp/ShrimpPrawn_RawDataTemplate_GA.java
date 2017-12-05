/*
 * ShrimpPrawn_RawDataTemplate_GA
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
package org.earthtime.Tripoli.rawDataFiles.templates.shrimp;

import java.util.TimeZone;
import org.earthtime.Tripoli.dataModels.inputParametersModels.AbstractAcquisitionModel;
import org.earthtime.Tripoli.dataModels.inputParametersModels.SingleCollectorAcquisition;
import org.earthtime.Tripoli.massSpecSetups.singleCollector.shrimp.ShrimpSetupUPb;
import org.earthtime.Tripoli.rawDataFiles.templates.AbstractRawDataFileTemplate;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.dataDictionaries.FileTypeEnum;
import org.earthtime.reduxLabData.ReduxLabData;

/**
 *
 * @author James F. Bowring
 */
public final class ShrimpPrawn_RawDataTemplate_GA extends AbstractRawDataFileTemplate {

    //Class variables   
    private static final long serialVersionUID = 7600002374256461569L;
    private static ShrimpPrawn_RawDataTemplate_GA instance = new ShrimpPrawn_RawDataTemplate_GA();

    private ShrimpPrawn_RawDataTemplate_GA() {
        super();

        this.NAME = "SHRIMP Prawn";
        this.aboutInfo = "analysis runs setup by Geosciences Australia";
        this.fileType = FileTypeEnum.xml;
        this.startOfFirstLine = "";
        this.startOfDataSectionFirstLine = "";
        this.startOfEachBlockFirstLine = "";
        this.blockStartOffset = 0;
        this.blockSize = 0;
        this.standardIDs = new String[]//
        {"T"};
        this.timeZone = TimeZone.getTimeZone("GMT");
        this.defaultParsingOfFractionsBehavior = 1;
        this.elementsByIsotopicMass = new String[]{};
        this.massSpecSetup = ShrimpSetupUPb.getInstance();
    }

    /**
     *
     * @return
     */
    public static ShrimpPrawn_RawDataTemplate_GA getInstance() {
        return instance;
    }

    /**
     *
     * @return
     */
    @Override
    public AbstractAcquisitionModel makeNewAcquisitionModel() {
        this.acquisitionModel = new SingleCollectorAcquisition();
        try {
            acquisitionModel.setPrimaryMineralStandardModel(ReduxLabData.getInstance().getAMineralStandardModel("Temora Placeholder v.1.0"));
        } catch (BadLabDataException badLabDataException) {
        }
        return acquisitionModel;
    }
}
