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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.earthtime.UPb_Redux.user.ReduxPersistentState;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public class IEDACredentialsValidator {

    public static String validateSesarCredentials(String username, String password, boolean isVerbose) {

        String sesarCredentialsService = "http://app.geosamples.org/webservices/credentials_service.php";//note test http://sesar3.geoinfogeochem.org/webservices/credentials_service.php
        boolean valid = false;
        // Feb 2015 userCode is prefix for IGSN
        String userCode = "";

        // using geochron as identifier since both are same and need backward compartibility for serialization
        ReduxPersistentState myState = ReduxPersistentState.getExistingPersistentState();
        myState.getReduxPreferences().setGeochronUserName(username);
        myState.getReduxPreferences().setGeochronPassWord(password);
        myState.serializeSelf();

        Document doc = HTTP_PostAndResponse(username, password, sesarCredentialsService);

        if (doc != null) {
            if (doc.getElementsByTagName("valid").getLength() > 0) {
                valid = doc.getElementsByTagName("valid").item(0).getTextContent().trim().equalsIgnoreCase("yes");
                if (valid) {
                    userCode = doc.getElementsByTagName("user_code").item(0).getTextContent().trim();
                }
            }

            if (isVerbose) {
                JOptionPane.showMessageDialog(null,
                        new String[]{
                            valid ? "SESAR credentials are VALID!\n" : "SESAR credentials NOT valid!\n"
                        });
            }
        } else {
            if (isVerbose) {
                JOptionPane.showMessageDialog(null,
                        new String[]{"SESAR Credentials Server " + sesarCredentialsService + " cannot be located.\n"
                        });
            }
        }
        return userCode;
    }

    /**
     *
     * http://www.geochronportal.org/post_to_credentials_service.html
     *
     * @param username
     * @param password
     * @param isVerbose
     * @return
     */
    public static String validateGeochronCredentials(String username, String password, boolean isVerbose) {

        String geochronCredentialsService = "http://www.geochron.org/credentials_service.php";
        boolean valid = false;
        // Feb 2015 userCode is prefix for IGSN
        String userCode = "";

        // using geochron as identifier since both are same and need backward compartibility for serialization
        ReduxPersistentState.getExistingPersistentState().getReduxPreferences().setGeochronUserName(username);
        ReduxPersistentState.getExistingPersistentState().getReduxPreferences().setGeochronPassWord(password);

        Document doc = HTTP_PostAndResponse(username, password, geochronCredentialsService);

        if (doc != null) {
            if (doc.getElementsByTagName("valid").getLength() > 0) {
                valid = doc.getElementsByTagName("valid").item(0).getTextContent().trim().equalsIgnoreCase("yes");
                if (valid) {
                    userCode = doc.getElementsByTagName("user_code").item(0).getTextContent().trim();
                }
            }

            if (isVerbose) {
                JOptionPane.showMessageDialog(null,
                        new String[]{
                            valid ? "GeoChron credentials are VALID!\n" : "GeoChron credentials NOT valid!\n"
                        });
            }
        } else {
            if (isVerbose) {
                JOptionPane.showMessageDialog(null,
                        new String[]{"Credentials Server " + geochronCredentialsService + " cannot be located.\n"
                        });
            }
        }

        return userCode;
    }

    private static Document HTTP_PostAndResponse(String userName, String password, String credentialsService) {
        Document doc = null;
        Map<String, String> dataToPost = new HashMap<>();
        dataToPost.put("username", userName);
        dataToPost.put("password", password);

        CloseableHttpClient httpclient = HttpClients.createDefault();
        org.apache.http.client.methods.HttpPost httpPost = new HttpPost(credentialsService);
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("username", userName));
        nameValuePairs.add(new BasicNameValuePair("password", password));
        CloseableHttpResponse httpResponse = null;
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            httpResponse = httpclient.execute(httpPost);
            HttpEntity myEntity = httpResponse.getEntity();
            InputStream response = myEntity.getContent();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            try {
                doc = factory.newDocumentBuilder().parse(response);
                //System.out.println("CCCC" + doc.getElementsByTagName("valid").item(0).getTextContent().trim());
            } catch (ParserConfigurationException | SAXException | IOException parserConfigurationException) {
                System.out.println("PARSE error " + parserConfigurationException.getMessage());
            }

            EntityUtils.consume(myEntity);
        } catch (IOException iOException) {

        } finally {
            try {
                httpResponse.close();
            } catch (IOException iOException) {
            }
        }

        return doc;
    }
}
