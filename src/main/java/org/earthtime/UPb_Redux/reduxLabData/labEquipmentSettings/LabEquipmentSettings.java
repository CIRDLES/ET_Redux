/*
 * LabEquipmentSettings.java
 *
 * Created on October, 2010
 *
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
package org.earthtime.UPb_Redux.reduxLabData.labEquipmentSettings;

import java.io.Serializable;
import org.earthtime.UPb_Redux.reduxLabData.ReduxLabDataListElementI;
import org.earthtime.xmlUtilities.XMLSerializationI;

/**
 *
 * @author James F. Bowring
 */
public abstract class LabEquipmentSettings implements
        Comparable<LabEquipmentSettings>,
        Serializable,
        XMLSerializationI,
        ReduxLabDataListElementI {

    // Class variables
    /**
     * identifies object in binary serialization
     */
    //private static final long serialVersionUID = -1575105271214161011L;
    // Instance variables
    private String settingsName;
    private String instrumentalMethod;

    /**
     * 
     * @param labEquipmentSettings
     * @return
     * @throws ClassCastException
     */
    public int compareTo ( LabEquipmentSettings labEquipmentSettings ) throws ClassCastException {
        String settingsID =//
                ((LabEquipmentSettings) labEquipmentSettings).getSettingsName().trim();
        return (this.getSettingsName().trim().compareToIgnoreCase( settingsID ));
    }

    /**
     * 
     * @param labEquipmentSettings
     * @return
     */
    @Override
    public boolean equals ( Object labEquipmentSettings ) {
        //check for self-comparison
        if ( this == labEquipmentSettings ) {
            return true;
        }
        if (  ! (labEquipmentSettings instanceof LabEquipmentSettings) ) {
            return false;
        }

        LabEquipmentSettings mySettings = (LabEquipmentSettings) labEquipmentSettings;
        return (this.getSettingsName().trim().compareToIgnoreCase( mySettings.getSettingsName().trim() ) == 0);
    }

    // http://www.javaworld.com/javaworld/jw-01-1999/jw-01-object.html?page=4
    /**
     * 
     * @return
     */
    @Override
    public int hashCode () {

        return 0;
    }

    /**
     * @return the settingsName
     */
    public String getSettingsName () {
        return settingsName;
    }

    /**
     * @param settingsName the settingsName to set
     */
    public void setSettingsName ( String settingsName ) {
        this.settingsName = settingsName;
    }

    /**
     * @return the instrumentalMethod
     */
    public String getInstrumentalMethod () {
        return instrumentalMethod;
    }

    /**
     * @param instrumentalMethod the instrumentalMethod to set
     */
    public void setInstrumentalMethod ( String instrumentalMethod ) {
        this.instrumentalMethod = instrumentalMethod;
    }
}
