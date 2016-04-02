/*
 * Copyright 2015 CIRDLES.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.earthtime.archivingTools;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;
import org.earthtime.UPb_Redux.user.ReduxPersistentState;
import org.earthtime.exceptions.ETWarningDialog;
import org.xml.sax.SAXException;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public class GeoPassIDValidator {

    /**
     * 
     * @param username
     * @param password
     * @param isVerbose
     * @return 
     */
    public static String validateGeoPassID(String username, String password, boolean isVerbose) {

        // using geochron as identifier since both are same and need backward compartibility for serialization
        ReduxPersistentState myState = ReduxPersistentState.getExistingPersistentState();
        myState.getReduxPreferences().setGeochronUserName(username);
        myState.getReduxPreferences().setGeochronPassWord(password);
        myState.serializeSelf();

        ArrayList<String> userCodes = null;
        
        try {
            userCodes = org.geosamples.credentials.CredentialsValidator.validateUserCredentialsProductionServiceV2(username, password);
        } catch (IOException | ParserConfigurationException | SAXException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException anException) {
            if (isVerbose) {
                new ETWarningDialog(anException.getMessage()).setVisible(true);
            }
        }

        return userCodes.size() > 0 ? userCodes.get(0).trim() : "NONEXXXXX";
    }
}
