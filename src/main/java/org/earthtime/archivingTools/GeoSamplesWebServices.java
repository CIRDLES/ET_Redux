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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import org.earthtime.exceptions.ETWarningDialog;
import org.geosamples.XMLDocumentInterface;
import org.geosamples.samples.Samples;
import static org.geosamples.samples.Samples.registerSampleMetaDataWithSesarTestService;
import static org.geosamples.samples.Samples.serializeSamplesToCompliantXMLPrettyPrint;
import org.xml.sax.SAXException;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public class GeoSamplesWebServices {

    public static String CURRENT_GEOSAMPLES_WEBSERVICE_FOR_DOWNLOAD_IGSN
            = org.geosamples.Constants.GEOSAMPLES_TEST_SAMPLES_SERVER + org.geosamples.Constants.GEOSAMPLES_SAMPLE_IGSN_WEBSERVICE_NAME;

    /**
     * This class is used to control access to GeoSamples services, since we are
     * experimenting with flavors.
     *
     * @param igsn
     * @param username
     * @param password
     * @param isVerbose
     * @return
     */
    public static XMLDocumentInterface getSampleMetaDataFromGeoSamplesIGSN(String igsn, String username, String password, boolean isVerbose) {
        XMLDocumentInterface samples = new Samples();

        try {
            samples = Samples.downloadSampleMetadataFromTestSesarIGSN(igsn, username, password);
        } catch (IOException | JAXBException | ParserConfigurationException | SAXException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException anException) {
            if (isVerbose) {
                new ETWarningDialog(anException.getMessage()).setVisible(true);
            }
        }

        return samples;
    }

    public static XMLDocumentInterface getSampleMetaDataFromGeoSamplesIGSN(String igsn, boolean isVerbose) {
        XMLDocumentInterface samples = new Samples();

        try {
            samples = Samples.downloadSampleMetadataFromTestSesarIGSN(igsn);
        } catch (IOException | JAXBException | ParserConfigurationException | SAXException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException anException) {
            if (isVerbose) {
                new ETWarningDialog(anException.getMessage()).setVisible(true);
            }
        }

        return samples;
    }

    public static XMLDocumentInterface registerSampleAtGeoSamplesIGSN(Samples.Sample sesarSample, boolean isVerbose) {
        XMLDocumentInterface mySamples = new Samples();
        mySamples.getSample().add(sesarSample);

        try {
            System.out.println(serializeSamplesToCompliantXMLPrettyPrint(mySamples));
        } catch (JAXBException jAXBException) {
        }
        
        XMLDocumentInterface success = null;
        try {
            success = registerSampleMetaDataWithSesarTestService("bowring@gmail.com", "redux00", mySamples);
            System.out.println("REGISTERED!!!");
        } catch (JAXBException | NoSuchAlgorithmException | KeyStoreException | KeyManagementException | IOException ex) {
            Logger.getLogger(Samples.class.getName()).log(Level.SEVERE, null, ex);
        }

        return success;
    }

    public static boolean isSampleRegistered(String igsn) {
        XMLDocumentInterface downloadedSample = getSampleMetaDataFromGeoSamplesIGSN(igsn, false);

        boolean isRegistered = false;
        try {
            isRegistered = downloadedSample.getSample().get(0).getIgsn() != null;
        } catch (Exception e) {
        }
        return isRegistered;
    }

    public static boolean isSampleRegisteredToParent(String igsn, String parentIgsn) {
        XMLDocumentInterface downloadedSample = getSampleMetaDataFromGeoSamplesIGSN(igsn, false);
        boolean isRegistered = false;
        try {
            isRegistered = (downloadedSample.getSample().get(0).getIgsn() != null) && (downloadedSample.getSample().get(0).getParentIgsn() != null);
        } catch (Exception e) {
        }
        if (isRegistered) {
            isRegistered = isRegistered && (downloadedSample.getSample().get(0).getParentIgsn().equalsIgnoreCase(parentIgsn));
        }

        return isRegistered;
    }

    public static boolean isWellFormedIGSN(String igsn, String userCode) {
        boolean retval = (igsn.length() == 9);

        if (userCode.length() == 3) {
            retval = retval && igsn.substring(0, 3).toUpperCase().matches("^[A-Z]{3}");
        } else { // assume length 5
            retval = retval && igsn.substring(0, 5).toUpperCase().matches("^[A-Z]{5}");
        }

        retval = retval && igsn.substring(userCode.length(), 9).matches("^[A-Z0-9]{" + (igsn.length() - userCode.length()) + "}");

        return retval;

    }
}
