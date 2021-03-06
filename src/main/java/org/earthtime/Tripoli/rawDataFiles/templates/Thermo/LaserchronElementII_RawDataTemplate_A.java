/*
 * LaserchronElementII_RawDataTemplate_A
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
import org.earthtime.Tripoli.massSpecSetups.singleCollector.ThermoFinnigan.LaserchronElementIISetupUPb_A;
import org.earthtime.Tripoli.rawDataFiles.templates.AbstractRawDataFileTemplate;
import org.earthtime.dataDictionaries.FileTypeEnum;

/**
 *
 * @author James F. Bowring
 */
public final class LaserchronElementII_RawDataTemplate_A extends AbstractRawDataFileTemplate{

    //Class variables   
    private static final long serialVersionUID = -5515737439750386077L;
    private static LaserchronElementII_RawDataTemplate_A instance = new LaserchronElementII_RawDataTemplate_A();

    private LaserchronElementII_RawDataTemplate_A () {
        super();

        this.NAME = "Laserchron Element II 202-238";
        this.aboutInfo = "analysis runs setup to process 202, 204, 206, 207, 208, 232, 238";
        this.fileType = FileTypeEnum.dat;
        this.startOfFirstLine = "C H d r F i l e ";//C H d r F i l e        x      ";
        this.startOfDataSectionFirstLine = "Time";
        this.startOfEachBlockFirstLine = "Time";
        this.blockStartOffset = 0;
        this.blockSize = 55;
        this.standardIDs = new String[]//
        {"FC", "SL", "R33"};
        this.timeZone = TimeZone.getTimeZone( "GMT" );
        this.defaultParsingOfFractionsBehavior = 1;
        this.elementsByIsotopicMass = new String[]{"202", "204", "206", "207", "208", "232", "238"};
        this.massSpecSetup = LaserchronElementIISetupUPb_A.getInstance();
    }

    /**
     *
     * @return
     */
    public static LaserchronElementII_RawDataTemplate_A getInstance () {
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
