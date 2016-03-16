/*
 * Copyright 2006-2016 CIRDLES.org.
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
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import org.earthtime.exceptions.ETWarningDialog;
import org.geosamples.samples.Samples;
import org.xml.sax.SAXException;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public class GeoSamplesWebServices {

    /**
     * This class is used to control access to GeoSamples services, since we are
     * experimenting with flavors.
     *
     * @param igsn
     * @param isVerbose
     * @return
     */
    public static Samples getSampleMetaDataFromGeoSamplesIGSN(String igsn, boolean isVerbose) {
        Samples samples = new Samples();

        try {
            samples = Samples.deserializeTestIGSN(igsn);
        } catch (IOException | JAXBException | ParserConfigurationException | SAXException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException anException) {
            if (isVerbose) {
                new ETWarningDialog(anException.getMessage()).setVisible(true);
            }
        }

        return samples;
    }

    public static boolean isSampleRegistered(String igsn) {
        Samples downloadedSample = getSampleMetaDataFromGeoSamplesIGSN(igsn, false);
        
        boolean isRegistered = false;
        try {
            isRegistered = downloadedSample.getSample().get(0).getIgsn() != null;
        } catch (Exception e) {
        }
        return isRegistered;
    }

    public static boolean isSampleRegisteredToParent(String igsn, String parentIgsn) {
        Samples downloadedSample = getSampleMetaDataFromGeoSamplesIGSN(igsn, false);
        boolean isRegistered = false;
        try {
            isRegistered = (downloadedSample.getSample().get(0).getIgsn() != null) && (downloadedSample.getSample().get(0).getParentIgsn() != null);
        } catch (Exception e) {
        }
        if (isRegistered ){
            isRegistered = isRegistered && (downloadedSample.getSample().get(0).getParentIgsn().equalsIgnoreCase(parentIgsn));
        }
        
        return isRegistered;
    }
}
