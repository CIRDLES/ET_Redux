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
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.aliquots.UPbReduxAliquot;
import org.earthtime.UPb_Redux.dateInterpretation.DateProbabilityDensityPanel;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.ConcordiaGraphPanel;
import org.earthtime.UPb_Redux.dateInterpretation.graphPersistence.GraphAxesSetup;
import org.earthtime.UPb_Redux.utilities.ClientHttpRequest;
import org.earthtime.aliquots.AliquotInterface;
import org.earthtime.aliquots.ReduxAliquotInterface;
import org.earthtime.dataDictionaries.AnalysisImageTypes;
import org.earthtime.dataDictionaries.RadDates;
import org.earthtime.samples.SampleInterface;
import org.xml.sax.SAXException;

/**
 *
 * @author James F. Bowring
 */
public class GeochronUploaderUtility {

    public static void uploadAliquotToGeochron(SampleInterface sample, AliquotInterface aliquot, String userName, String password, boolean isPublic, boolean overWrite) {
        // feb 2015 part of refactoring effort ... this code duplicates code in AliquotManager
        //TODO: complete refactoring

        String content = ((UPbReduxAliquot) aliquot).serializeXMLObject();

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

    /**
     *
     * @param sample the value of sample
     * @param aliquot the value of aliquot
     */
    public static File produceConcordiaGraphForUploading(SampleInterface sample, AliquotInterface aliquot) {
        // feb 2015 code copied and modified from aliquot manager for user interface prototyping
        // TODO: refactor both locations to sample and make more robust
        // TODO: use create virtual file system

        File tempConcordiaSVGforUploading = new File(sample.getSampleName() + "-" + aliquot.getAliquotName() + "_tempConcordiaForUpload.svg");

        ConcordiaGraphPanel concordiaGraphPanel = new ConcordiaGraphPanel(sample, null);
        concordiaGraphPanel.setSelectedFractions(((ReduxAliquotInterface) aliquot).getAliquotFractions());
        concordiaGraphPanel.setCurAliquot(aliquot);

        sample.getSampleDateInterpretationGUISettings().//
                setConcordiaOptions(concordiaGraphPanel.getConcordiaOptions());
        concordiaGraphPanel.//
                setFadedDeselectedFractions(false);

        // set choices per options code copied (TODO: REFACTOR ME) from SampleDateInterpretations
        Map<String, String> CGO = concordiaGraphPanel.getConcordiaOptions();
        if (CGO.containsKey("showEllipseLabels")) {
            concordiaGraphPanel.setShowEllipseLabels(false);
        }
        if (CGO.containsKey("showExcludedEllipses")) {
            concordiaGraphPanel.setShowExcludedEllipses(true);
        }

        concordiaGraphPanel.setBounds(510, 0, 580, 405);
        concordiaGraphPanel.setCurrentGraphAxesSetup(new GraphAxesSetup("C", 2));
        concordiaGraphPanel.setGraphWidth(565 - GraphAxesSetup.DEFAULT_GRAPH_LEFT_MARGIN_VERTICAL_LABELS);
        concordiaGraphPanel.setGraphHeight(385);

        concordiaGraphPanel.setYorkFitLine(null);
        concordiaGraphPanel.getDeSelectedFractions().clear();
        concordiaGraphPanel.setPreferredDatePanel(null);

        concordiaGraphPanel.setShowTightToEdges(true);

        concordiaGraphPanel.refreshPanel(true, false);

        concordiaGraphPanel.setShowTightToEdges(false);

        boolean saveShowTitleBox = concordiaGraphPanel.isShowTitleBox();
        // prepare for SVG output for uploading
        concordiaGraphPanel.setShowTitleBox(false);
        concordiaGraphPanel.setUploadToGeochronMode(true);

        concordiaGraphPanel.outputToSVG(tempConcordiaSVGforUploading);

        // restore state
        concordiaGraphPanel.setShowTitleBox(saveShowTitleBox);
        concordiaGraphPanel.setUploadToGeochronMode(false);

        return tempConcordiaSVGforUploading;
    }

    /**
     *
     * @param sample the value of sample
     * @param aliquot the value of aliquot
     */
    public static File producePDFImageForUploading(SampleInterface sample, AliquotInterface aliquot) {
        File tempProbabilitySVGforUploading = new File(sample.getSampleName() + "_tempProbabilityDensity.svg");

        DateProbabilityDensityPanel probabilityPanel = new DateProbabilityDensityPanel(sample);
        probabilityPanel.setSelectedFractions(((ReduxAliquotInterface) aliquot).getAliquotFractions());
        probabilityPanel.setCurAliquot(aliquot);
        
//        // use default if user has not initialized
//        if (probabilityPanel.getSelectedFractions().isEmpty()) {
//            probabilityPanel.//
//                    setSelectedFractions(sample.getUpbFractionsUnknown());
//            probabilityPanel.//
//                    getDeSelectedFractions().clear();

            probabilityPanel.setGraphWidth(565);
            probabilityPanel.setGraphHeight(385);

            probabilityPanel.setSelectedHistogramBinCount(5);

            if (sample.isSampleTypeLegacy() & sample.getAnalysisPurpose().equals(ReduxConstants.ANALYSIS_PURPOSE.DetritalSpectrum)) {
                probabilityPanel.setChosenDateName(RadDates.bestAge.getName());
            } else {
                probabilityPanel.setChosenDateName(RadDates.age207_206r.getName());
            }

            probabilityPanel.refreshPanel(true, false);

//        } else {
//            probabilityPanel.setGraphWidth(565);
//            probabilityPanel.setGraphHeight(385);
//        }

        // prepare for upload
        probabilityPanel.setUploadToGeochronMode(true);
        probabilityPanel.outputToSVG(tempProbabilitySVGforUploading);

        //restore state
        probabilityPanel.setUploadToGeochronMode(false);

        return tempProbabilitySVGforUploading;
    }

    public static void uploadConcordiaImage(File tempConcordiaSVG, AliquotInterface aliquot, String userName, String password) {
        // april 2011 revise to check if concordia slot is already taken
        AnalysisImageInterface concordiaImage = ((ReduxAliquotInterface) aliquot).getAnalysisImageByType(AnalysisImageTypes.CONCORDIA);

        concordiaImage.setImageURL(//
                GeochronUploadImagesHelper.uploadImage(//
                        tempConcordiaSVG, //
                        userName, //
                        password,
                        AnalysisImageTypes.CONCORDIA));
    }

}
