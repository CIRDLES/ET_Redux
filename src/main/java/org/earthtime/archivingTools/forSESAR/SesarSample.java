/*
 * SesarSample.java
 *
 * Copyright 2006-2018 James F. Bowring, CIRDLES.org, and Earth-Time.org
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
package org.earthtime.archivingTools.forSESAR;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.security.AnyTypePermission;
import com.thoughtworks.xstream.security.NoTypePermission;
import com.thoughtworks.xstream.security.NullPermission;
import com.thoughtworks.xstream.security.PrimitiveTypePermission;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.earthtime.UPb_Redux.utilities.ClientHttpRequest;
import org.earthtime.archivingTools.URIHelper;
import org.earthtime.dataDictionaries.SESAR_MaterialTypesEnum;
import org.earthtime.dataDictionaries.SESAR_ObjectTypesEnum;
import org.earthtime.exceptions.ETException;
import org.xml.sax.SAXException;

/**
 * Based on http://www.iedadata.org/services/sesar_api
 *
 * @author James F. Bowring
 */
public class SesarSample {

    private String user_code;
    private String userName;
    private String password;
    private String sampleType;
    private String material;
    private String IGSN;
    private String parentIGSN;
    private String name;
    private String nameOfLocalSample;
    private String description;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private String primaryLocationName;
    private String country;
    private String collection_start_date;
    private String originalArchive;
    private String[] children;
    private boolean isChild;

    public SesarSample() {
        this("", "", "", false);
    }

    /**
     *
     * @param userCode the value of user_code
     * @param userName the value of userName
     * @param password the value of password
     * @param isParent the value of isParent
     */
    public SesarSample(String userCode, String userName, String password, boolean isChild) {
        this.user_code = userCode;
        this.userName = userName;
        this.password = password;
        this.sampleType = SESAR_ObjectTypesEnum.IndividualSample.getName();
        this.material = SESAR_MaterialTypesEnum.Mineral.getName();
        this.IGSN = "";
        this.parentIGSN = "";
        this.name = "";
        this.nameOfLocalSample = "";
        this.description = "";
        this.latitude = BigDecimal.ZERO;
        this.longitude = BigDecimal.ZERO;
        this.primaryLocationName = "";
        this.country = "";
        this.collection_start_date = "";
        this.originalArchive = "";
        this.children = new String[0];
        this.isChild = isChild;
    }

    public boolean confirmUserCodeCompliance(String proposedIGSN) {
        return proposedIGSN.toUpperCase().startsWith(user_code.toUpperCase());
    }

    public SESAR_ObjectTypesEnum getSesarObjectType() {
        SESAR_ObjectTypesEnum retval;
        if (sampleType.length() == 0) {
            retval = SESAR_ObjectTypesEnum.Other;
        } else {
            try {
                retval = SESAR_ObjectTypesEnum.valueOf(sampleType.replace(" ", ""));
            } catch (Exception e) {
                retval = SESAR_ObjectTypesEnum.Other;
            }
        }

        return retval;
    }

    public SESAR_MaterialTypesEnum getSesarMaterialType() {
        SESAR_MaterialTypesEnum retval;
        if (material.length() == 0) {
            retval = SESAR_MaterialTypesEnum.Other;
        } else {
            try {
                retval = SESAR_MaterialTypesEnum.valueOf(material.replace(" ", ""));
            } catch (Exception e) {
                retval = SESAR_MaterialTypesEnum.Other;
            }
        }

        return retval;
    }

    public boolean hasNameClashBetweenLocalAndSesar() {
        return (name.compareToIgnoreCase(nameOfLocalSample) != 0);
    }

    public static SesarSample createSesarSampleFromSesarRecord(String igsn) {
        SesarSample sesarSample = null;
        try {
            File sample = retrieveXMLFileFromSesarForIGSN(igsn);

            // replace the results tag with sample tag
            // todo make more robust
            Path file = sample.toPath();
            byte[] fileArray;
            fileArray = Files.readAllBytes(file);
            String str = new String(fileArray, "UTF-8");
            str = str.replace("results", "sample");
            fileArray = str.getBytes();
            Files.write(file, fileArray);

            sesarSample = (SesarSample) readXMLObject(file.toString());
        } catch (IOException | ETException iOException) {
        }

        return sesarSample;
    }

    private static File retrieveXMLFileFromSesarForIGSN(String igsn) {

        String productionService1 = "http://app.geosamples.org/webservices/display.php?igsn=";
        String productionService2 = "http://app.geosamples.org/sample/igsn/";
        String testService = "http://sesardev.geoinfogeochem.org/sample/igsn/";

//        Map<String, String> dataToPost = new HashMap<>();
//        dataToPost.put("username", userName);
//        dataToPost.put("password", password);
//        dataToPost.put("content", content);
//
//        InputStream response = null;
//        org.w3c.dom.Document doc = null;
//        try {
//            response = ClientHttpRequest   .post(//
//                    new URL(testServiceV2),//
//                    dataToPost);
//
//            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//            factory.setValidating(false);
//            try {
//                doc = factory.newDocumentBuilder().parse(response);
//            } catch (ParserConfigurationException | SAXException | IOException parserConfigurationException) {
//                System.out.println("PARSE error " + parserConfigurationException.getMessage());
//            }
//        } catch (IOException iOException) {
//            System.out.println(iOException.getMessage());
//        }
//        
//        
//        
//        
//        
//        
//        
//        
//        
        File retVal = null;
        String tempSESARcontents
                = URIHelper.getTextFromURI(testService + igsn);

        if (tempSESARcontents.length() > 0) {
            // write this to a file
            String fileNameForIGSN = "IGSN_" + igsn + "_fromSESAR.xml";
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(fileNameForIGSN);
            } catch (FileNotFoundException ex) {
            }

            OutputStreamWriter out = new OutputStreamWriter(fos);
            try {
                out.write(tempSESARcontents);
                out.flush();
                out.close();
                retVal = new File(fileNameForIGSN);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null,
                        new String[]{
                            "Error reaching server: "//
                            + ex.getMessage()
                        });
            }
        }

        return retVal;
    }

    public static Object readXMLObject(String filename)
            throws FileNotFoundException, ETException {
        SesarSample sesarSample = null;

        BufferedReader reader = URIHelper.getBufferedReader(filename);

        if (reader != null) {

            XStream xstream = getXStreamReader();

            try {
                sesarSample = (SesarSample) xstream.fromXML(reader);
            } catch (ConversionException e) {
                throw new ETException(null, e.getMessage());
            }

//            System.out.println("\nThis is your SesarSample that was just read successfully:\n");
//            String xml2 = getXStreamWriter().toXML(sesarSample);
//
//            System.out.println(xml2);
//            System.out.flush();
        } else {
            throw new FileNotFoundException("Missing XML data file.");
        }

        return sesarSample;
    }

    public String uploadAndRegisterSesarSample() {
        String productionServiceV1 = "http://app.geosamples.org/webservices/uploadservice.php";
        String productionServiceV2 = "http://app.geosamples.org/webservices/upload.php";
        String testServiceV2 = "http://sesardev.geoinfogeochem.org/webservices/upload.php";

        String content = serializeForUploadToSesar();

        Map<String, String> dataToPost = new HashMap<>();
        dataToPost.put("username", userName);
        dataToPost.put("password", password);
        dataToPost.put("content", content);

        InputStream response = null;
        org.w3c.dom.Document doc = null;
        try {
            response = ClientHttpRequest.post(//
                    new URL(testServiceV2),//
                    dataToPost);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            try {
                doc = factory.newDocumentBuilder().parse(response);
            } catch (ParserConfigurationException | SAXException | IOException parserConfigurationException) {
                System.out.println("PARSE error " + parserConfigurationException.getMessage());
            }
        } catch (IOException iOException) {
            System.out.println(iOException.getMessage());
        }

        String statusMessage = "SESAR registration status: ";
        String igsnMessage = "IGSN = ";
        String igsnValue = "";
        String errorMessage = "";
        if (doc != null) {
            if (doc.getElementsByTagName("status").getLength() > 0) {
                statusMessage += doc.getElementsByTagName("status").item(0).getTextContent();
                if (doc.getElementsByTagName("igsn").getLength() > 0) {
                    // success
                    igsnValue = doc.getElementsByTagName("igsn").item(0).getTextContent();
                    igsnMessage += igsnValue;
                } else if (doc.getElementsByTagName("error").getLength() > 0) {
                    // take first error
                    errorMessage = doc.getElementsByTagName("error").item(0).getTextContent();
                }
            }
        }

        JOptionPane.showMessageDialog(null,
                new String[]{
                    statusMessage,
                    (igsnValue.length() == 0) ? errorMessage : igsnMessage
                }, //
                (igsnValue.length() == 0) ? "ET Redux Warning" : "ET Redux Information",//
                (igsnValue.length() == 0) ? JOptionPane.WARNING_MESSAGE : JOptionPane.INFORMATION_MESSAGE);

        return igsnValue;
    }

    // XML Serialization
    /**
     * gets an <code>XStream</code> writer. Creates, customizes, and returns
     * <code>XStream</code> for XML serialization
     *
     * @pre <code>XStream</code> package is available @post <code>XStream</code>
     * for XML encoding is returned
     *
     * @return <code>XStream</code> - for XML serialization encoding
     */
    public static XStream getXStreamWriter() {
        XStream xstream = new XStream();

        customizeXstream(xstream);

        return xstream;
    }

    /**
     * gets an <code>XStream</code> reader. Creates, customizes, and returns
     * <code>XStream</code> for XML serialization
     *
     * @pre <code>XStream</code> package is available @post <code>XStream</code>
     * for XML decoding is returned
     *
     * @return <code>XStream</code> - for XML serialization decoding
     */
    public static XStream getXStreamReader() {

        XStream xstream = new XStream(new DomDriver());

        customizeXstream(xstream);

        // http://x-stream.github.io/security.html
        XStream.setupDefaultSecurity(xstream);
        // clear out existing permissions and set own ones
        xstream.addPermission(NoTypePermission.NONE);
        // allow some basics
        xstream.addPermission(NullPermission.NULL);
        xstream.addPermission(PrimitiveTypePermission.PRIMITIVES);
        xstream.allowTypeHierarchy(Collection.class);
        xstream.addPermission(AnyTypePermission.ANY);

        return xstream;
    }

    /**
     * registers converter for argument <code>xstream</code> and sets aliases to
     * make the XML file more human-readable
     *
     * @pre argument <code>xstream</code> is a valid <code>XStream</code> @post
     * argument <code>xstream</code> is customized to produce a cleaner output
     * <code>file</code>
     *
     * @param xstream <code>XStream</code> to be customized
     */
    public static void customizeXstream(XStream xstream) {

        xstream.registerConverter(new SesarSampleXMLConverter());

        xstream.alias("sample", SesarSample.class);
    }

    public String serializeForUploadToSesar() {
        XStream xstream = getXStreamWriter();

        String xml = xstream.toXML(this);
        // fix double underscores
        xml = xml.replace("__", "_");

        xml = "<samples>" + xml + "</samples>";

        System.out.println(xml);

        return xml;
    }

    public void serializeXMLObject(String filename) {
        XStream xstream = getXStreamWriter();

        String xml = xstream.toXML(this);
        // fix double underscores
        xml = xml.replace("__", "_");

        try {
            FileWriter outFile = new FileWriter(filename);
            PrintWriter out = new PrintWriter(outFile);

            // Write xml to file
            out.println(xml);
            out.flush();
            out.close();
            outFile.close();

        } catch (IOException e) {
        }
    }
//
//    public static boolean validateSampleIGSNatSESAR(String igsn) {
//
//        boolean retVal = false;
//        if (!(igsn.startsWith("XYZ")) && !(igsn.equalsIgnoreCase("NONE"))) {
//            File file = retrieveXMLFileFromSesarForIGSN(igsn);
//            if (file != null) {
//                Document doc = convertXMLTextToDOMdocument(file);
//                if (doc != null) {
//                    if (doc.hasChildNodes()) {
//                        boolean resultsElementPresent = doc.getFirstChild().getNodeName().equalsIgnoreCase("results");
//                        if (resultsElementPresent) {
//                            retVal = doc.getElementsByTagName("error").getLength() == 0;
//                        }
//                    }
//                }
//            }
//        }
//        return retVal;
//    }
//
//    public static boolean validateAliquotIGSNatSESAR(String aliquotIgsn, String parentIgsn) {
//
//        boolean retVal = false;
//        if (!(aliquotIgsn.startsWith("XYZ")) && !(aliquotIgsn.equalsIgnoreCase("NONE"))) {
//            File file = retrieveXMLFileFromSesarForIGSN(aliquotIgsn);
//            if (file != null) {
//                Document doc = convertXMLTextToDOMdocument(file);
//                if (doc != null) {
//                    if (doc.hasChildNodes()) {
//                        boolean resultsElementPresent = doc.getFirstChild().getNodeName().equalsIgnoreCase("results");
//                        if (resultsElementPresent) {
//                            retVal = doc.getElementsByTagName("error").getLength() == 0;
//                            if (retVal) {
//                                // test if parent present
//                                String parentIgsnFromSesar = doc.getElementsByTagName("parent_igsn").item(0).getTextContent();
//                                retVal = parentIgsnFromSesar.compareToIgnoreCase(parentIgsn) == 0;
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        return retVal;
//    }

//    public static boolean isWellFormedIGSN(String igsn, String userCode) {
//        boolean retval = (igsn.length() == 9);
//
//        if (userCode.length() == 3) {
//            retval = retval && igsn.substring(0, 3).toUpperCase().matches("^[A-Z]{3}");
//        } else { // assume length 5
//            retval = retval && igsn.substring(0, 5).toUpperCase().matches("^[A-Z]{5}");
//        }
//
//        retval = retval && igsn.substring(userCode.length(), 9).matches("^[A-Z0-9]{" + (igsn.length() - userCode.length()) + "}");
//
//        return retval;
//
//    }
    /**
     * @return the user_code
     */
    public String getUser_code() {
        return user_code;
    }

    /**
     * @param user_code the user_code to set
     */
    public void setUser_code(String user_code) {
        this.user_code = user_code;
    }

    /**
     * @return the sampleType
     */
    public String getSampleType() {
        return sampleType;
    }

    /**
     * @param sampleType the sampleType to set
     */
    public void setSampleType(String sampleType) {
        this.sampleType = sampleType;
    }

    /**
     * @return the material
     */
    public String getMaterial() {
        return material;
    }

    /**
     * @param material the material to set
     */
    public void setMaterial(String material) {
        this.material = material;
    }

    /**
     * @return the IGSN
     */
    public String getIGSN() {
        return IGSN;
    }

    /**
     * @param IGSN the IGSN to set
     */
    public void setIGSN(String IGSN) {
        this.IGSN = IGSN;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the latitude
     */
    public BigDecimal getLatitude() {
        return latitude;
    }

    /**
     * @param latitude the latitude to set
     */
    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }

    /**
     * @return the longitude
     */
    public BigDecimal getLongitude() {
        return longitude;
    }

    /**
     * @param longitude the longitude to set
     */
    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }

    /**
     * @return the primaryLocationName
     */
    public String getPrimaryLocationName() {
        return primaryLocationName;
    }

    /**
     * @param primaryLocationName the primaryLocationName to set
     */
    public void setPrimaryLocationName(String primaryLocationName) {
        this.primaryLocationName = primaryLocationName;
    }

    /**
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * @param country the country to set
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * @return the collection_start_date
     */
    public String getCollection_start_date() {
        return collection_start_date;
    }

    /**
     * @param collection_start_date the collection_start_date to set
     */
    public void setCollection_start_date(String collection_start_date) {
        this.collection_start_date = collection_start_date;
    }

    /**
     * @return the originalArchive
     */
    public String getOriginalArchive() {
        return originalArchive;
    }

    /**
     * @param originalArchive the originalArchive to set
     */
    public void setOriginalArchive(String originalArchive) {
        this.originalArchive = originalArchive;
    }

    /**
     * @return the parentIGSN
     */
    public String getParentIGSN() {
        return parentIGSN;
    }

    /**
     * @param parentIGSN the parentIGSN to set
     */
    public void setParentIGSN(String parentIGSN) {
        this.parentIGSN = parentIGSN;
    }

    /**
     * @return the children
     */
    public String[] getChildren() {
        return children;
    }

    /**
     * @param children the children to set
     */
    public void setChildren(String[] children) {
        this.children = children;
    }

    /**
     * @return the nameOfLocalSample
     */
    public String getNameOfLocalSample() {
        return nameOfLocalSample;
    }

    /**
     * @param nameOfLocalSample the nameOfLocalSample to set
     */
    public void setNameOfLocalSample(String nameOfLocalSample) {
        this.nameOfLocalSample = nameOfLocalSample;
    }
}
