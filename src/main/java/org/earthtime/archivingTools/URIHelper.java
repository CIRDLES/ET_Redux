/*
 * URIHelper.java
 *
 * Created on April 28, 2007, 7:50 AM
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
package org.earthtime.archivingTools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import javax.swing.JOptionPane;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import org.earthtime.exceptions.ETWarningDialog;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author James F. Bowring
 */
public class URIHelper {

    private static boolean CONNECTED_TO_INTERNET = true;

    /**
     * Creates a new instance of URIHelper
     */
    public URIHelper() {
    }

    /**
     *
     * @param fileURI
     * @return
     */
    public static InputStream getInputStreamFromURI(String fileURI) {
        URL url;
        URLConnection urlConn = null;

        InputStream retval = null;
        try {
            url = new URL(fileURI);
            urlConn = url.openConnection();
            urlConn.setDoInput(true);
            urlConn.setUseCaches(false);

            retval = urlConn.getInputStream();

        } catch (IOException iOException) {
            JOptionPane.showMessageDialog(null,
                    new String[]{"Error reaching server: "//
                        + iOException.getMessage()});
        }

//        InputStream retval = null;
//        try {
//            retval = urlConn.getInputStream();
//        } catch (IOException ex) {
//            JOptionPane.showMessageDialog( null,
//                    new String[]{"Error reaching server: "//
//                        + ex.getMessage()} );
//            // ex.printStackTrace();
//        }
        return retval;
    }

    /**
     *
     * @param fileURI
     * @return
     */
    public static String getTextFromURI(String fileURI) {
        StringBuilder retval = new StringBuilder();

        try {
            BufferedReader bufR = getBufferedReader(fileURI);
//                    new BufferedReader(
//                    new InputStreamReader( getInputStreamFromURI( fileURI ) ) );

            try {
                String s = null;
                while ((s = bufR.readLine()) != null) {
                    retval.append(s);
                    retval.append(System.getProperty("line.separator"));
                }
            } finally {
                bufR.close();
            }
        } catch (IOException ex) {
            //  ex.printStackTrace();
        }

        return retval.toString();
    }

    /**
     *
     * @param filename
     * @return
     * @throws java.io.FileNotFoundException
     */
    public static BufferedReader getBufferedReader(String filename)
            throws FileNotFoundException {

        BufferedReader reader = null;
        Reader inReader = null;

        try {
            if (filename.startsWith("http://")) {
                inReader = new InputStreamReader(getInputStreamFromURI(filename));
            } else {
                inReader = new FileReader(filename);
            }

            reader = new BufferedReader(inReader);

        } catch (FileNotFoundException eFile) {
            System.out.println(eFile.getMessage());
            throw new FileNotFoundException(
                    "File: " + filename + " does not exist.");
        }
        return reader;
    }

//    /**
//     *
//     * @param schemaURI
//     * @return
//     * @throws ETException
//     * @throws BadOrMissingXMLSchemaException
//     */
//    public static Validator createSchemaValidator(String schemaURI)
//            throws ETException, BadOrMissingXMLSchemaException {
//
//        Validator retval = null;
//
//        // create a SchemaFactory capable of understanding WXS schemas
//        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
//
//        // load a WXS schema, represented by a Schema instance
//        URL url;
//        URLConnection urlConn = null;
//        try {
//            url = new URL(schemaURI);
//            urlConn = url.openConnection();
//        } catch (MalformedURLException ex) {
//        } catch (IOException ex) {
//        }
//
//        urlConn.setDoInput(true);
//        urlConn.setUseCaches(false);
//
//        Source schemaFile = null;
//        try {
//            schemaFile = new StreamSource(urlConn.getInputStream());
//        } catch (IOException ex) {
//            throw new BadOrMissingXMLSchemaException(null,
//                    "Cannot locate XML Schema at URI:\n\n"//
//                    + schemaURI);//+ ex.getMessage());
//        }
//
//        Schema schema = null;
//        try {
//            schema = factory.newSchema(schemaFile);
//        } catch (SAXException ex) {
//            //          ex.printStackTrace();
//            throw new ETException(null,
//                    "The EarthTime XML Schema at URI:\n\n" //
//                    + schemaURI + "\n\nis unavailable." //
//                    + "  Please check your network connection.");// ex.getMessage());
//
//        }
//
//        // https://jaxp.dev.java.net/article/jaxp-1_3-article.html#Compile_Schema(s)
//        //Create a Validator which can be used to validate instance document against this schema(s)
//        retval = schema.newValidator();
//        retval.setErrorHandler(new SAXErrorHandler());
//
//        return retval;
//    }
    /**
     *
     * @param reader
     * @param xmlURI
     * @param schemaURI
     * @return
     */
    public static boolean validateXML(BufferedReader reader, String xmlURI, String schemaURI) {

//        if ( isInternetReachable() ) {
//            return validateXMLwithValidator( reader, createSchemaValidator( schemaURI ) );
//        } else {
//            return true;
//        }
        return validateXML(xmlURI, schemaURI);
    }

    private static boolean validateXML(String xmlURI, String schemaURI) {
        boolean retVal = true;

        if (CONNECTED_TO_INTERNET) {
            try {
                URL url = new URL("http://geochron.org");
                //System.out.println(url.getHost());
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setReadTimeout(5000);
                con.connect();
                if (con.getResponseCode() == 200) {

                    CONNECTED_TO_INTERNET = true;
                    try {
                        // parse an XML document into a DOM tree
                        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                        dbFactory.setNamespaceAware(true);
                        DocumentBuilder parser = dbFactory.newDocumentBuilder();
                        Document document = parser.parse(xmlURI);

                        // create a SchemaFactory capable of understanding WXS schemas
                        SchemaFactory schemaFactory
                                = SchemaFactory.newInstance(
                                        XMLConstants.W3C_XML_SCHEMA_NS_URI);

                        // load a WXS schema, represented by a Schema instance
                        Source schemaFile = new StreamSource(
                                new URL(schemaURI).openStream());
                        Schema schema = schemaFactory.newSchema(schemaFile);

                        // create a Validator instance, which can be used to validate an instance document
                        Validator validator = schema.newValidator();

                        // validate the DOM tree
                        validator.validate(new DOMSource(document));
                    } catch (ParserConfigurationException | SAXException | IOException parserConfigurationException) {
                    }

                } else {
                    CONNECTED_TO_INTERNET = false;
                    new ETWarningDialog("ET_Redux could not find an Internet connection and will not validate XML files until restart.").setVisible(true);
                }
            } catch (IOException iOException) {
                CONNECTED_TO_INTERNET = false;
                new ETWarningDialog("ET_Redux could not find an Internet connection and will not validate XML files until restart.").setVisible(true);
            }
        }

        return retVal;
    }

//    /**
//     *
//     * @param reader
//     * @param validator
//     * @return
//     * @throws ETException
//     * @throws BadOrMissingXMLSchemaException
//     */
//    private static boolean validateXMLwithValidator(BufferedReader reader, Validator validator)
//            throws ETException, BadOrMissingXMLSchemaException {
//        boolean retval = false;
//
//        try {
//            //Validate this instance document against the Instance document supplied
//            validator.validate(new StreamSource(reader));
//            retval = true;
//        } catch (SAXException ex) {
//            throw new BadOrMissingXMLSchemaException(null,
//                    "XML schema file problem:\n" + ex.getMessage());
//        } catch (IOException ex) {
//        }
//        return retval;
//    }
    /**
     *
     * @param connectionString
     * @return
     */
    public static org.w3c.dom.Document RetrieveXMLfromServerAsDOMdocument(String connectionString) {

        org.w3c.dom.Document doc = null;

        String tempSESARcontents = URIHelper.getTextFromURI(connectionString);

        // write this to a file
        String tempSESARFileName = "TempXMLfromServer.xml";
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(tempSESARFileName);
        } catch (FileNotFoundException ex) {
        }

        OutputStreamWriter out = new OutputStreamWriter(fos);
        try {
            out.write(tempSESARcontents);
            out.flush();
            out.close();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null,
                    new String[]{
                        "Error reaching server: "//
                        + ex.getMessage()
                    });
            ex.printStackTrace();
        }

        File tempFile = new File(tempSESARFileName);
        Document convertedDocument = convertXMLTextToDOMdocument(tempFile);
        tempFile.delete();

        return convertedDocument;
    }

//    /**
//     *
//     * @param serviceURI
//     * @param data
//     * @return
//     */
//    public static File HTTP_PostAndResponse(String serviceURI, String data, String fileTag) {
//        File fileOut = null;
//        try {
//            // Send data
//            URL url = new URL(serviceURI);
//            URLConnection conn = url.openConnection();
//            conn.setDoOutput(true);
//            conn.setDoInput(true);
//
//            // The POST line
//            try (DataOutputStream dstream = new DataOutputStream(conn.getOutputStream())) {
//                // The POST line
//                dstream.writeBytes(data);
//            }
//
//            fileOut = new File(fileTag + "HTTP_PostAndResponse_tempXML.xml");
//            fileOut.delete();
//
//            FileOutputStream streamOut;
//
//            try ( // Read Response
//                    InputStream in = conn.getInputStream()) {//
//                streamOut = new FileOutputStream(fileOut);
//                int x;
//                while ((x = in.read()) != -1) {
//                    streamOut.write(x);
//                    //System.out.write(x);
//                }
//            }
//            streamOut.close();
//        } catch (Exception e) {
//            return null;
//        }
//
//        return fileOut;
//    }
    /**
     *
     * @param XMLfile
     * @return
     */
    public static Document convertXMLTextToDOMdocument(File XMLfile) {
        org.w3c.dom.Document doc = null;

        // Parses an XML file and returns a DOM document.
        // If validating is true, the contents is validated against the DTD
        // specified in the file.
        try {
            // Create a builder factory
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);

            // Create the builder and parse the file
            doc = factory.newDocumentBuilder().parse(XMLfile);
        } catch (SAXException e) {
            JOptionPane.showMessageDialog(null,
                    new String[]{"Document error: "//
                        + e.getMessage()});
        } catch (ParserConfigurationException e) {
            JOptionPane.showMessageDialog(null,
                    new String[]{"Parsing error: "//
                        + e.getMessage()});
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    new String[]{"File error: "//
                        + e.getMessage()});
        }

        return doc;
    }

//    private static boolean isInternetReachable() {
//        try {
//            //make a URL to a known source
//            URL url = new URL("http://www.google.com");
//
//            //open a connection to that source
//            HttpURLConnection urlConnect = (HttpURLConnection) url.openConnection();
//
//            //trying to retrieve data from the source. If there
//            //is no connection, this line will fail
//            Object objData = urlConnect.getContent();
//
//        } catch (UnknownHostException e) {
//            return false;
//        } catch (IOException e) {
//            return false;
//        }
//        return true;
//    }
    static class SAXErrorHandler implements ErrorHandler {

        @Override
        public void fatalError(SAXParseException e) throws SAXException {
            throw e;
        }

        @Override
        public void error(SAXParseException e) throws SAXException {
            throw e;
        }

        @Override
        public void warning(SAXParseException e) throws SAXException {
            throw e;
        }
    }
}
