/*
 * LaserChronNUPlasmaMultiCollFaradayRawDataTemplate.java
 *
 * Created Jul 1, 2011
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
package org.earthtime.Tripoli.rawDataFiles.templates.NuPlasma;

import java.io.Serializable;
import java.util.TimeZone;
import org.earthtime.Tripoli.dataModels.inputParametersModels.AbstractAcquisitionModel;
import org.earthtime.Tripoli.dataModels.inputParametersModels.StaticAcquisition;
import org.earthtime.Tripoli.massSpecSetups.multiCollector.NUPlasma.GehrelsNUPlasmaSetupUPbFar;
import org.earthtime.Tripoli.rawDataFiles.templates.AbstractRawDataFileTemplate;
import org.earthtime.dataDictionaries.FileTypeEnum;

/**
 *
 * @author James F. Bowring
 */
public final class LaserChronNUPlasmaMultiCollFaradayRawDataTemplate extends AbstractRawDataFileTemplate implements //
        Comparable<AbstractRawDataFileTemplate>,
        Serializable {

    //Class variables
    private static final long serialVersionUID = -2270593876198978125L;

    private static LaserChronNUPlasmaMultiCollFaradayRawDataTemplate instance = new LaserChronNUPlasmaMultiCollFaradayRawDataTemplate();

    private LaserChronNUPlasmaMultiCollFaradayRawDataTemplate() {
        super();

        this.NAME = "LaserChron NUPlasma";
        this.aboutInfo = "U-Th-Pb Faraday analysis runs";
        this.fileType = FileTypeEnum.txt;
        this.startOfFirstLine = "Run File";
        this.startOfDataSectionFirstLine = " U-Th-Pb FAR Analysis";
        this.startOfEachBlockFirstLine = "Sample Name is ";
        this.endOfEachBlockLastLine = "End of Analysis";
        this.blockStartOffset = 23;
        this.blockSize = 15;
        this.standardIDs = new String[]//
        {"SL"};
        this.timeZone = TimeZone.getTimeZone("MST");
        this.defaultParsingOfFractionsBehavior = 1;
        this.massSpecSetup = GehrelsNUPlasmaSetupUPbFar.getInstance();
    }

    /**
     *
     * @return
     */
    public static LaserChronNUPlasmaMultiCollFaradayRawDataTemplate getInstance() {
        return instance;
    }

    /**
     *
     * @return
     */
    @Override
    public AbstractAcquisitionModel makeNewAcquisitionModel() {
        this.acquisitionModel = new StaticAcquisition();
        return acquisitionModel;
    }

}
