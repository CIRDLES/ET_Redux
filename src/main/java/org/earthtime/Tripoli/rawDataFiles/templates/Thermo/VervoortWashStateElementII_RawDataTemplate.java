/*
 * VervoortWashStateElementII_RawDataTemplate
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
package org.earthtime.Tripoli.rawDataFiles.templates.Thermo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.util.TimeZone;
import org.earthtime.Tripoli.dataModels.inputParametersModels.AbstractAcquisitionModel;
import org.earthtime.Tripoli.dataModels.inputParametersModels.SingleCollectorAcquisition;
import org.earthtime.Tripoli.massSpecSetups.singleCollector.ThermoFinnigan.WashStateElementIISetupUPb;
import org.earthtime.Tripoli.rawDataFiles.templates.AbstractRawDataFileTemplate;
import org.earthtime.dataDictionaries.FileTypeEnum;

/**
 *
 * @author James F. Bowring
 */
public final class VervoortWashStateElementII_RawDataTemplate extends AbstractRawDataFileTemplate implements //
        Comparable<AbstractRawDataFileTemplate>,
        Serializable {

    private static VervoortWashStateElementII_RawDataTemplate instance = new VervoortWashStateElementII_RawDataTemplate();

    private VervoortWashStateElementII_RawDataTemplate() {
        super();

        this.NAME = "Vervoort Wash State Element II";
        this.aboutInfo = "analysis runs setup by Vervoort";
        this.fileType = FileTypeEnum.txt;
        this.startOfFirstLine = "Trace for Mass:";
        this.startOfDataSectionFirstLine = "Time";
        this.startOfEachBlockFirstLine = "Time";
        this.blockStartOffset = 6;
        this.blockSize = 250;//300;
        this.standardIDs = new String[]//
        {"Peixe","1","2","3","4"};
        this.timeZone = TimeZone.getTimeZone("PST");
        this.defaultParsingOfFractionsBehavior = 1;
        this.massSpecSetup = WashStateElementIISetupUPb.getInstance();
    }

    /**
     *
     * @return
     */
    public static VervoortWashStateElementII_RawDataTemplate getInstance() {
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

    private void readObject(
            ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        ObjectStreamClass myObject = ObjectStreamClass.lookup(
                Class.forName(VervoortWashStateElementII_RawDataTemplate.class.getCanonicalName()));
        long theSUID = myObject.getSerialVersionUID();
        System.out.println("Customized De-serialization of VervoortWashStateElementII_RawDataTemplate " + theSUID);
    }
}
