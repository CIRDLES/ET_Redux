/*
 * GeochronUploaderUtility.java
 *
 * Copyright 2006-2015 James F. Bowring and www.Earth-Time.org
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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.earthtime.UPb_Redux.aliquots.Aliquot;
import org.earthtime.UPb_Redux.aliquots.UPbReduxAliquot;
import org.earthtime.UPb_Redux.utilities.ClientHttpRequest;
import org.earthtime.samples.SampleInterface;
import org.xml.sax.SAXException;

/**
 *
 * @author James F. Bowring
 */
public class GeochronUploaderUtility {

    
    public static void uploadAliquotToGeochron(SampleInterface sample, Aliquot aliquot, String userName, String password, boolean isPublic, boolean overWrite) {
        // feb 2015 part of refactoring effort ... this code cduplicate code in AliquotManager
        //TODO: complete refactoring
              
        String content = ((UPbReduxAliquot)aliquot).serializeXMLObject();
        
        // Construct data
        String isPublicString = isPublic ? "yes" : "no";
        String overWriteString = overWrite ? "yes" : "no";
        String data = "";
        try {
            data = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(userName, "UTF-8");
            data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
            data += "&" + URLEncoder.encode("public", "UTF-8") + "=" + URLEncoder.encode(isPublicString, "UTF-8");
            data += "&" + URLEncoder.encode("content", "UTF-8") + "=" + URLEncoder.encode(content, "UTF-8");
            data += "&" + URLEncoder.encode("overwrite", "UTF-8") + "=" + URLEncoder.encode(overWriteString, "UTF-8");
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
        }

        // april 2009 move to zipping for improved upload performance
        // http://www.exampledepot.com/egs/java.util.zip/CreateZip.html
        // These are the files to include in the ZIP file
        // geochron expects this file name exactly
        String fileName = "tempDataForAliquotUpload";

        // Create a buffer for reading the files
        byte[] buf = new byte[2048];

        try {
            // Create the ZIP file
            String outFilename = "tempDataForAliquotUploadzip";
            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFilename));

            // Compress the file
            //FileInputStream in = new FileInputStream(fileName);
            InputStream in = new ByteArrayInputStream(data.getBytes());

            // Add ZIP entry to output stream.
            out.putNextEntry(new ZipEntry(fileName));

            // Transfer bytes from the file to the ZIP file
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

            // Complete the entry
            out.closeEntry();
            in.close();

            // Complete the ZIP file
            out.close();
        } catch (IOException e) {
        }

        File uploadFile = new File("tempDataForAliquotUploadzip");

        InputStream response = null;
        try {
            response = ClientHttpRequest.post(//
                    new URL("http://www.geochron.org/redux_service.php"),//
                    "filetoupload",
                    uploadFile);
        } catch (IOException iOException) {
        }

        org.w3c.dom.Document doc = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        try {
            doc = factory.newDocumentBuilder().parse(response);
        } catch (ParserConfigurationException | SAXException | IOException parserConfigurationException) {
        }

        String error = "no";
        String message = "";
        if (doc != null) {
            if (doc.getElementsByTagName("error").getLength() > 0) {
                error = doc.getElementsByTagName("error").item(0).getTextContent();
                message = doc.getElementsByTagName("message").item(0).getTextContent();
            }
        }

        sample.setArchivedInRegistry(error.equalsIgnoreCase("no"));

        JOptionPane.showMessageDialog(null,
                new String[]{
                    !error.equalsIgnoreCase("no") ? "Failure!\n" : "Success!\n",
                    message + "   " + aliquot.getSampleIGSN() + "::" + aliquot.getAliquotName()
                });
    }

}
