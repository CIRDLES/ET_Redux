/*
 * LaserChronNUPlasmaMultiCollIonCounterRawDataTemplate.java
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
package org.earthtime.Tripoli.rawDataFiles.templates.NuPlasma;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.util.TimeZone;
import org.earthtime.Tripoli.dataModels.inputParametersModels.AbstractAcquisitionModel;
import org.earthtime.Tripoli.dataModels.inputParametersModels.StaticAcquisition;
import org.earthtime.Tripoli.massSpecSetups.multiCollector.NUPlasma.GehrelsNUPlasmaSetupUPbIonCounter;
import org.earthtime.Tripoli.rawDataFiles.templates.AbstractRawDataFileTemplate;
import org.earthtime.dataDictionaries.FileTypeEnum;

/**
 *
 * @author James F. Bowring
 */
public final class LaserChronNUPlasmaMultiCollIonCounterRawDataTemplate extends AbstractRawDataFileTemplate implements //
        Comparable<AbstractRawDataFileTemplate>,
        Serializable {

    private static LaserChronNUPlasmaMultiCollIonCounterRawDataTemplate instance = new LaserChronNUPlasmaMultiCollIonCounterRawDataTemplate();

    private LaserChronNUPlasmaMultiCollIonCounterRawDataTemplate() {
        super();

        this.NAME = "LaserChron NUPlasma";
        this.aboutInfo = "U-Th-Pb IonCounter analysis runs";
        this.fileType = FileTypeEnum.txt;
        this.startOfFirstLine = "Run File";
        this.startOfDataSectionFirstLine = " U-Th-Pb IC Analysis";
        this.startOfEachBlockFirstLine = "Sample Name is ";
        this.blockStartOffset = 23;
        this.blockSize = 15;
        this.standardIDs = new String[]//
        {"SL"};
        this.timeZone = TimeZone.getTimeZone("MST");
        this.defaultParsingOfFractionsBehavior = 1;
        this.massSpecSetup = GehrelsNUPlasmaSetupUPbIonCounter.getInstance();
    }

    /**
     *
     * @return
     */
    public static LaserChronNUPlasmaMultiCollIonCounterRawDataTemplate getInstance() {
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

    private void readObject(
            ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        ObjectStreamClass myObject = ObjectStreamClass.lookup(
                Class.forName(LaserChronNUPlasmaMultiCollIonCounterRawDataTemplate.class.getCanonicalName()));
        long theSUID = myObject.getSerialVersionUID();
        System.out.println("Customized De-serialization of LaserChronNUPlasmaMultiCollIonCounterRawDataTemplate " + theSUID);
    }
}
