/*
 * SantaBarbaraNUPlasmaMultiCollFaradayTRARawDataTemplate.java
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
import org.earthtime.Tripoli.massSpecSetups.multiCollector.NUPlasma.CottlesNUPlasmaSetupUPbFarTRA;
import org.earthtime.Tripoli.rawDataFiles.templates.AbstractRawDataFileTemplate;
import org.earthtime.dataDictionaries.FileTypeEnum;

/**
 *
 * @author James F. Bowring
 */
public final class SantaBarbaraNUPlasmaMultiCollFaradayTRARawDataTemplate extends AbstractRawDataFileTemplate implements //
        Comparable<AbstractRawDataFileTemplate>,
        Serializable {

    private static SantaBarbaraNUPlasmaMultiCollFaradayTRARawDataTemplate instance = new SantaBarbaraNUPlasmaMultiCollFaradayTRARawDataTemplate();

    private SantaBarbaraNUPlasmaMultiCollFaradayTRARawDataTemplate() {
        super();

        this.NAME = "Santa Barbara NUPlasma TRA";
        this.aboutInfo = "U-Th-Pb Faraday TRA analysis runs";
        this.fileType = FileTypeEnum.txt;
        this.startOfFirstLine = "Version 4";
        this.startOfDataSectionFirstLine = "Spare text";
        this.startOfEachBlockFirstLine = "Spare text";
        this.endOfEachBlockLastLine = "";
        this.blockStartOffset = 0; // not used
        this.blockSize = 0;// determined from data values; not constant
        this.standardIDs = new String[]//
        {"MZ_44069", "44069", "stern", "manangotry", "91500", "gj"};
        this.timeZone = TimeZone.getTimeZone("PST");
        this.defaultParsingOfFractionsBehavior = 1;
        this.massSpecSetup = CottlesNUPlasmaSetupUPbFarTRA.getInstance();
    }

    /**
     *
     * @return
     */
    public static SantaBarbaraNUPlasmaMultiCollFaradayTRARawDataTemplate getInstance() {
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

//    private void readObject(
//            ObjectInputStream stream)
//            throws IOException, ClassNotFoundException {
//        stream.defaultReadObject();
//        ObjectStreamClass myObject = ObjectStreamClass.lookup(Class.forName(SantaBarbaraNUPlasmaMultiCollFaradayTRARawDataTemplate.class.getCanonicalName()));
//        long theSUID = myObject.getSerialVersionUID();
//        System.out.println("Customized De-serialization of SantaBarbaraNUPlasmaMultiCollFaradayTRARawDataTemplate " + theSUID);
//    }
}
