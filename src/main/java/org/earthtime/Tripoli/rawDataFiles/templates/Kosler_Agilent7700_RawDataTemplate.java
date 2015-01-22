/*
 * Kosler_Agilent7700_RawDataTemplate
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
package org.earthtime.Tripoli.rawDataFiles.templates;

import java.io.Serializable;
import java.util.TimeZone;
import org.earthtime.Tripoli.dataModels.inputParametersModels.AbstractAcquisitionModel;
import org.earthtime.Tripoli.dataModels.inputParametersModels.SingleCollectorAcquisition;
import org.earthtime.dataDictionaries.FileTypeEnum;

/**
 *
 * @author James F. Bowring
 */
public final class Kosler_Agilent7700_RawDataTemplate extends AbstractRawDataFileTemplate implements //
        Comparable<AbstractRawDataFileTemplate>,
        Serializable {

    private static Kosler_Agilent7700_RawDataTemplate instance = null;

    private Kosler_Agilent7700_RawDataTemplate () {
        super();

        this.NAME = "Kosler Agilent 7700";
        this.aboutInfo = "analysis runs setup by Kosler";
        this.fileType = FileTypeEnum.csv;
        this.startOfFirstLine = "Intensity";
        this.startOfDataSectionFirstLine = "Time";
        this.startOfEachBlockFirstLine = "Time";
        this.blockStartOffset = 4;
        this.blockSize = 360;
        this.standardIDs = new String[]//
        {"91500"};
        this.timeZone = TimeZone.getTimeZone( "GMT" );
        this.defaultParsingOfFractionsBehavior = 1;

    }

    /**
     *
     * @return
     */
    public static Kosler_Agilent7700_RawDataTemplate getInstance () {
        if ( instance == null ) {
            instance = new Kosler_Agilent7700_RawDataTemplate();
        }
        return instance;
    }
    
    /**
     *
     * @return
     */
    @Override
     public AbstractAcquisitionModel makeNewAcquisitionModel () {
        this.acquisitionModel = new SingleCollectorAcquisition();
        return acquisitionModel;
    }
}
