/*
 * ShrimpFileHandler
 *
 * Copyright 2006-2016 James F. Bowring and www.Earth-Time.org
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
package org.earthtime.Tripoli.rawDataFiles.handlers.shrimp;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.cirdles.shrimp.PrawnFile;
import org.earthtime.Tripoli.fractions.TripoliFraction;
import org.earthtime.Tripoli.rawDataFiles.handlers.AbstractRawDataFileHandler;
import org.earthtime.utilities.FileHelper;

/**
 *
 * @author James F. Bowring
 */
public class ShrimpFileHandler extends AbstractRawDataFileHandler {

    // Class variables
    // private static final long serialVersionUID = -2860923405769819758L;
    private static ShrimpFileHandler instance = new ShrimpFileHandler();

    /**
     *
     * @param massSpec
     * @param rawDataFileTemplate
     */
    private ShrimpFileHandler() {

        super();
        NAME = "Shrimp Prawn File '.xml'";
        aboutInfo = "Details: This is the Prawn xml file form the Shrimp. ";
    }

    /**
     *
     * @return
     */
    public static ShrimpFileHandler getInstance() {
        return instance;
    }

    /**
     *
     * @param tripoliRawDataFolder
     * @return
     */
    @Override
    public File validateAndGetHeaderDataFromRawIntensityFile(File tripoliRawDataFolder) {
        String dialogTitle = "Select a Shrimp Prawn '.xml' file:";

        rawDataFile = FileHelper.AllPlatformGetFolder(dialogTitle, tripoliRawDataFolder);
        return rawDataFile;
    }

    /**
     *
     * @param loadDataTask the value of loadRawDataTaskListener
     * @param usingFullPropagation the value of usingFullPropagation
     * @param leftShadeCount the value of leftShadeCount
     * @param ignoreFirstFractions the value of ignoreFirstFracts
     */
    @Override
    public void getAndLoadRawIntensityDataFile(SwingWorker loadDataTask, boolean usingFullPropagation, int leftShadeCount, int ignoreFirstFractions) {

        // temp       
        rawDataFile = new File("/Users/sbowring/Documents/Development_XSD/100142_G6147_10111109.43 10.33.37 AM.xml");

        if (rawDataFile != null) {

            // create fractions from raw data and perform corrections and calculate ratios
            tripoliFractions = loadRawDataFile(loadDataTask, usingFullPropagation, leftShadeCount, ignoreFirstFractions);
        } else {
            JOptionPane.showMessageDialog(
                    null,
                    new String[]{"Selected raw data file is empty."},
                    "ET Redux Warning",
                    JOptionPane.WARNING_MESSAGE);
        }

    }

    /**
     *
     * @return
     */
    @Override
    protected boolean areKeyWordsPresent(String fileContents
    ) {
        boolean retVal = true;

        retVal = retVal && fileContents.contains(getRawDataFileTemplate().getStartOfFirstLine().trim());

        return retVal;
    }

    /**
     *
     * @param fractionID
     * @return
     */
    @Override
    public boolean isStandardFractionID(String fractionID
    ) {
        boolean retVal = false;
        for (String standardID : getRawDataFileTemplate().getStandardIDs()) {
            retVal = retVal || fractionID.toUpperCase(Locale.US).contains(standardID.toUpperCase(Locale.US));
        }

        return retVal;
    }

    /**
     *
     *
     * @param loadDataTask the value of loadDataTask
     * @param usingFullPropagation the value of usingFullPropagation
     * @param leftShadeCount the value of leftShadeCount
     * @param ignoreFirstFractions the value of ignoreFirstFractions
     * @return
     */
    @Override
    protected SortedSet<TripoliFraction> loadRawDataFile(//
            SwingWorker loadDataTask, boolean usingFullPropagation, int leftShadeCount, int ignoreFirstFractions) {

        SortedSet myTripoliFractions = new TreeSet<>();
        PrawnFile prawnFile = null;

        try {
            // remote copy of example file
            java.net.URL url = null;
            url = new URL("https://raw.githubusercontent.com/bowring/XSD/master/SHRIMP/EXAMPLE_100142_G6147_10111109.43_10.33.37%20AM.xml");

            JAXBContext jaxbContext = JAXBContext.newInstance(PrawnFile.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            prawnFile = (PrawnFile) jaxbUnmarshaller.unmarshal(rawDataFile);//  url);

            // assume we are golden   
            // a 'run' is an analysis or fraction
            for (int f = ignoreFirstFractions; f < prawnFile.getRuns(); f++) {

                if (loadDataTask.isCancelled()) {
                    break;
                }
                loadDataTask.firePropertyChange("progress", 0, ((100 * f) / prawnFile.getRuns()));

                PrawnFile.Run runFraction = prawnFile.getRun().get(f);

                processRunFraction(runFraction);

            } // end of files loop

            if (myTripoliFractions.isEmpty()) {
                myTripoliFractions = null;
            }
        } catch (JAXBException | MalformedURLException jAXBException) {
            JOptionPane.showMessageDialog(
                    null,
                    new String[]{"Selected Prawn file does not conform to schema."},
                    "ET Redux Warning",
                    JOptionPane.WARNING_MESSAGE);
        }
        return myTripoliFractions;
    }

    private void processRunFraction(PrawnFile.Run runFraction) {
        String fractionID = runFraction.getPar().get(0).getValue();

        // needs to be more robust
        boolean isStandard = (fractionID.compareToIgnoreCase(rawDataFileTemplate.getStandardIDs()[0]) == 0);

        double[][] extractedData = PrawnRunFractionParser.parsedRunFractionData(runFraction);

    }


}
