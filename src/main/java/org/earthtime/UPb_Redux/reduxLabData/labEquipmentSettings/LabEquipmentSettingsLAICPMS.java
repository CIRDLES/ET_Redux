/*
 * LabEquipmentSettingsLAICPMS.java
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

import java.io.FileNotFoundException;
import org.earthtime.UPb_Redux.reduxLabData.ReduxLabDataListElementI;
import org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException;
import org.earthtime.exceptions.ETException;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;

/**
 *
 * @author James F. Bowring
 */
public class LabEquipmentSettingsLAICPMS extends LabEquipmentSettings{

    /**
     * 
     * @param filename
     * @param doValidate
     * @return
     * @throws FileNotFoundException
     * @throws ETException
     * @throws BadOrMissingXMLSchemaException
     */
    @Override
    public Object readXMLObject ( String filename, boolean doValidate ) throws FileNotFoundException, ETException, FileNotFoundException, BadOrMissingXMLSchemaException {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    @Override
    public String getReduxLabDataElementName () {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    @Override
    public void serializeXMLObject ( String filename ) {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    /**
     * 
     */
    @Override
    public void removeSelf ( ) {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

   
}
