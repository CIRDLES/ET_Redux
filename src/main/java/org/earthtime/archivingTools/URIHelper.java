/*
 * URIHelper.java
 *
 * Created on April 28, 2007, 7:50 AM
 *
 *
 * Copyright 2006-2018 James F. Bowring, CIRDLES.org, and Earth-Time.org
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
//            JOptionPane.showMessageDialog(null,
//                    new String[]{"Error reaching server: "//
//                        + iOException.getMessage()});
        }

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
            if (bufR != null) {
                try {
                    String s = null;
                    while ((s = bufR.readLine()) != null) {
                        retval.append(s);
                        retval.append(System.getProperty("line.separator"));
                    }
                } finally {
                    bufR.close();
                }
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
            if (filename.startsWith("http")) {
                try {
                    inReader = new InputStreamReader(getInputStreamFromURI(filename));
                    reader = new BufferedReader(inReader);
                } catch (Exception e) {
                }
            } else {
                inReader = new FileReader(filename);
                reader = new BufferedReader(inReader);
            }

        } catch (FileNotFoundException eFile) {
            System.out.println(eFile.getMessage());
            throw new FileNotFoundException(
                    "File: " + filename + " does not exist.");
        }
        return reader;
    }

    /**
     *
     * @param reader
     * @param xmlURI
     * @param schemaURI
     * @return
     */
    public static boolean validateXML(BufferedReader reader, String xmlURI, String schemaURI) {
        return validateXML(xmlURI, schemaURI);
    }

    private static boolean validateXML(String xmlURI, String schemaURI) {
        boolean retVal = true;

        if (CONNECTED_TO_INTERNET) {
            try {
                URL url = new URL("http://cnn.com");
                //System.out.println(url.getHost());
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setReadTimeout(20000);
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
                        if ((parserConfigurationException instanceof FileNotFoundException)//
                                || //
                                ((parserConfigurationException instanceof IOException) //
                                && (parserConfigurationException.getMessage().contains("502")))) {
                            CONNECTED_TO_INTERNET = false;
                            new ETWarningDialog("ET_Redux could not find the schema file: \n" + schemaURI + "\n and will not validate XML files until ET_Redux restarts.").setVisible(true);
                        } else {
                            retVal = false;
                        }
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

    /**
     *
     * @param connectionString
     * @return
     */
    public static org.w3c.dom.Document RetrieveXMLfromServerAsDOMdocument(String connectionString) {

        Document convertedDocument = null;
        String tempSESARcontents = URIHelper.getTextFromURI(connectionString);

        // sept 2016 SESAR started returning 400 for bad igsn
        if (tempSESARcontents.length() > 0) {
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
            }

            File tempFile = new File(tempSESARFileName);
            convertedDocument = convertXMLTextToDOMdocument(tempFile);
            tempFile.delete();
        }

        return convertedDocument;
    }

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
