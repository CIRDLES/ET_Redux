/*
 * GeochronUploadImagesHelper.java
 *
 * Created Nov 10, 2010
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
package org.earthtime.utilities;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.earthtime.UPb_Redux.utilities.ClientHttpRequest;
import org.earthtime.dataDictionaries.AnalysisImageTypes;
import org.xml.sax.SAXException;

/**
 *
 * @author James F. Bowring
 */
public final class GeochronUploadImagesHelper {

    /**
     * 
     * @param imageFile
     * @param username
     * @param password
     * @param imageType
     * @return
     */
    public static String uploadImage ( File imageFile, String username, String password, AnalysisImageTypes imageType ) {
        InputStream response = null;
        try {
            response = ClientHttpRequest.post(//
                    new URL( "http://www.geochron.org/imageservice.php" ),//
                    "username",
                    username,
                    "password",
                    password,
                    "imagetype",
                    imageType.getName(),
                    "uploadfile",
                    imageFile );
        } catch (IOException iOException) {
        }

        org.w3c.dom.Document doc = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating( false );
        try {
            doc = factory.newDocumentBuilder().parse( response );
        } catch (ParserConfigurationException parserConfigurationException) {
        } catch (SAXException sAXException) {
        } catch (IOException iOException) {
        }


        String error = "yes";
        String message = "";
        String imageurl = "";
        if ( doc != null ) {
            if ( doc.getElementsByTagName( "error" ).getLength() > 0 ) {
                error = doc.getElementsByTagName( "error" ).item( 0 ).getTextContent();
                message = doc.getElementsByTagName( "message" ).item( 0 ).getTextContent();
                imageurl = doc.getElementsByTagName( "imageurl" ).item( 0 ).getTextContent();
            }
        }


        return imageurl;
    }
}
