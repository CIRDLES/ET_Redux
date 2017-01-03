/*
 * LaserchronElementIIFileHandler
 *
 * Copyright 2006-2017 James F. Bowring and www.Earth-Time.org
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
package org.earthtime.Tripoli.rawDataFiles.handlers.Variant810Quadrupole;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.JFrame;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;
import org.earthtime.Tripoli.dataModels.DataModelInterface;
import org.earthtime.Tripoli.fractions.TripoliFraction;
import org.earthtime.Tripoli.rawDataFiles.handlers.AbstractRawDataFileHandler;
import org.earthtime.UPb_Redux.filters.PrnFileFilter;
import org.earthtime.reduxLabData.ReduxLabData;
import org.earthtime.utilities.FileHelper;

/**
 *
 * @author James F. Bowring
 */
public class UHoustonVarian810FileHandler extends AbstractRawDataFileHandler {

    // Class variables
    //private static final long serialVersionUID = -2860923405769819758L;
    private static final UHoustonVarian810FileHandler instance = new UHoustonVarian810FileHandler();
    private static Map<String, Integer> referenceMaterialIncrementerMap = null;

    // Instance variables
    private List<String> fractionData;

    /**
     *
     * @param massSpec
     * @param rawDataFileTemplate
     */
    private UHoustonVarian810FileHandler() {

        super();
        NAME = "U Houston Varian810 '.prn' file";
        aboutInfo = "Details: This is the U Houston single-file protocol for a Varian810. ";

        baselineStartIndex = 3;
        baselineEndIndex = 65;
        peakStartIndex = 85;
        peakEndIndex = 200;
    }

    /**
     *
     * @return
     */
    public static UHoustonVarian810FileHandler getInstance() {
        return instance;
    }

    /**
     *
     * @param tripoliRawDataFolder
     * @return
     */
    @Override
    public File validateAndGetHeaderDataFromRawIntensityFile(File tripoliRawDataFolder) {
        String dialogTitle = "Select a U Houston Varian810 '.prn' file:";
        final String fileExtension = ".prn";
        FileFilter fileFilter = new PrnFileFilter();

        rawDataFile = FileHelper.AllPlatformGetFile(//
                dialogTitle, tripoliRawDataFolder, fileExtension, fileFilter, true, new JFrame())[0];

        // TODO: validate file contents
        return rawDataFile;
    }

    /**
     *
     * @param loadDataTask the value of loadRawDataTaskListener
     * @param usingFullPropagation the value of usingFullPropagation
     * @param leftShadeCount the value of leftShadeCount
     * @param ignoreFirstFractions the value of ignoreFirstFracts
     * @param inLiveMode the value of inLiveMode
     */
    @Override
    public void getAndLoadRawIntensityDataFile(SwingWorker loadDataTask, boolean usingFullPropagation, int leftShadeCount, int ignoreFirstFractions, boolean inLiveMode) {

        // U Houston Varian810 has single ".prn" file containing all data
        // and include named fractions with each interna; data set
        if ((referenceMaterialIncrementerMap == null) || !inLiveMode) {
            referenceMaterialIncrementerMap = new ConcurrentHashMap<>();
            for (String standardID : rawDataFileTemplate.getStandardIDs()) {
                referenceMaterialIncrementerMap.put(standardID, 1);
            }
        }

        if (rawDataFile != null) {
            // create fractions from raw data and perform corrections and calculate ratios
            tripoliFractions = loadRawDataFile(loadDataTask, usingFullPropagation, leftShadeCount, ignoreFirstFractions, inLiveMode);
        };
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
     * @param inLiveMode the value of inLiveMode
     * @return the
     * java.util.SortedSet<org.earthtime.Tripoli.fractions.TripoliFraction>
     */
    @Override
    protected SortedSet<TripoliFraction> loadRawDataFile(//
            SwingWorker loadDataTask, boolean usingFullPropagation, int leftShadeCount, int ignoreFirstFractions, boolean inLiveMode) {

        SortedSet myTripoliFractions = new TreeSet<>();

        try {
            fractionData = Files.readLines(rawDataFile, Charsets.ISO_8859_1);

            // walk the file
            String fractionID = "";
            int fractionStartLine = -1;
            String primaryReferenceMaterialfractionID = "";
            String fractionTimeStamp = "";
            long fractionBackgroundTimeStamp = 0l;
            long fractionPeakTimeStamp = 0l;
            boolean isPrimaryReferenceMaterial = false;
            boolean isSecondaryReferenceMaterial = false;
            List<double[]> backgroundAcquisitions = new ArrayList<>();
            List<double[]> peakAcquisitions = new ArrayList<>();
            
            double r238_235s = ReduxLabData.getInstance().getDefaultR238_235s().getValue().doubleValue();

            for (int i = 0; i < fractionData.size(); i++) {

                if (loadDataTask.isCancelled()) {
                    break;
                }
                loadDataTask.firePropertyChange("progress", 0, ((100 * i) / fractionData.size()));

                String[] lineData = fractionData.get(i).replaceAll("\"", "").split(",");

                // detect new analysis fraction
                if (lineData[0].startsWith("Processed")) {
                    // beginning of analysis that ends with two blank lines
                    fractionStartLine = i;

                    // next line is name of fraction with time stamp followed by line of headers and then data
                    lineData = fractionData.get(i + 1).replaceAll("\"", "").split(",");
                    fractionID = lineData[0].trim();
                    if (i == 0) {
                        primaryReferenceMaterialfractionID = fractionID;
                    }

                    fractionTimeStamp = lineData[1].trim();

                    // Get the default MEDIUM/SHORT DateFormat
                    SimpleDateFormat fractionTimeFormat = new SimpleDateFormat();
                    // match Thu Nov 19 23:19:48 2015
                    fractionTimeFormat.applyPattern("EEE MMM dd HH:mm:ss yyyy");

                    // Parse the fractionDateValue
                    Date fractionDateValue = fractionTimeFormat.parse(fractionTimeStamp);
                    fractionBackgroundTimeStamp = fractionDateValue.getTime();

                    // needs to be more robust
                    isPrimaryReferenceMaterial = (fractionID.substring(0, 2).compareToIgnoreCase(primaryReferenceMaterialfractionID.substring(0, 2)) == 0);
                    isSecondaryReferenceMaterial = referenceMaterialIncrementerMap.containsKey(fractionID.substring(0, 3)) && !isPrimaryReferenceMaterial;

                    backgroundAcquisitions = new ArrayList<>();
                    peakAcquisitions = new ArrayList<>();

                    // process time stamp of first peak reading
                    fractionPeakTimeStamp = fractionBackgroundTimeStamp + baselineEndIndex * massSpec.getCOLLECTOR_DATA_FREQUENCY_MILLISECS();

                } // end test for "Processed" keyword

                // now reading lines of data until blanks found ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                if (i > ((fractionStartLine + 2)) && (lineData[0].trim().length() > 0)) {
                    // fields: Scan    Time(sec)	Hg202	Hg201	Pb204	Pb206	Pb207	Pb208	Th232	U238	Hg204	Po208	U232	Pu238
                    int scanNumber = Integer.parseInt(lineData[0]);

                    // Hg202	Hg201	Pb204	Pb206	Pb207	Pb208	Th232	calc U235 U238	XXXHg204	XXXPo208	XXXU232	     XXXPu238
                    double[] backgroundIntensities = new double[9];
                    double[] peakIntensities = new double[9];
                    if (legalBaselineIndex(scanNumber)) {
                        backgroundAcquisitions.add(backgroundIntensities);
                        backgroundIntensities[0] = Integer.parseInt(lineData[2]);
                        backgroundIntensities[1] = Integer.parseInt(lineData[3]);
                        backgroundIntensities[2] = Integer.parseInt(lineData[4]);
                        backgroundIntensities[3] = Integer.parseInt(lineData[5]);
                        backgroundIntensities[4] = Integer.parseInt(lineData[6]);
                        backgroundIntensities[5] = Integer.parseInt(lineData[7]);
                        backgroundIntensities[6] = Integer.parseInt(lineData[8]);
                        backgroundIntensities[8] = Integer.parseInt(lineData[9]);
                        backgroundIntensities[7] = backgroundIntensities[8] / r238_235s;
                    } else if (legalPeakIndex(scanNumber)) {
                        peakAcquisitions.add(peakIntensities);
                        peakIntensities[0] = Integer.parseInt(lineData[2]);
                        peakIntensities[1] = Integer.parseInt(lineData[3]);
                        peakIntensities[2] = Integer.parseInt(lineData[4]);
                        peakIntensities[3] = Integer.parseInt(lineData[5]);
                        peakIntensities[4] = Integer.parseInt(lineData[6]);
                        peakIntensities[5] = Integer.parseInt(lineData[7]);
                        peakIntensities[6] = Integer.parseInt(lineData[8]);
                        peakIntensities[8] = Integer.parseInt(lineData[9]);
                        peakIntensities[7] = peakIntensities[8] / r238_235s;                   }
                }

                // now create fraction from data  ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                if ((fractionStartLine > -1) && (lineData[0].trim().length() == 0)) {
                    // flag that we are here
                    fractionStartLine = -1;

                    TripoliFraction tripoliFraction
                            = new TripoliFraction(
                                    fractionID, //
                                    massSpec.getCommonLeadCorrectionHighestLevel(), //
                                    isPrimaryReferenceMaterial,
                                    isSecondaryReferenceMaterial,
                                    fractionBackgroundTimeStamp, //
                                    fractionPeakTimeStamp,
                                    peakAcquisitions.size());

                    SortedSet<DataModelInterface> rawRatios = massSpec.rawRatiosFactoryRevised();

                    tripoliFraction.setRawRatios(rawRatios);

                    massSpec.setCountOfAcquisitions(peakAcquisitions.size());

                    massSpec.processFractionRawRatiosII(null, null, //
                            backgroundAcquisitions, peakAcquisitions, usingFullPropagation, tripoliFraction, inLiveMode);

                    tripoliFraction.shadeDataActiveMapLeft(leftShadeCount);
                    
                    System.out.println("\n**** Varian 810 FractionID  " + fractionID + " refMat? " + isPrimaryReferenceMaterial + "  livemode = " + inLiveMode + " <<<<<<<<<<<<<<<<<<\n");

                    myTripoliFractions.add(tripoliFraction);

                    if (isPrimaryReferenceMaterial) {
                        loadDataTask.firePropertyChange("refMaterialLoaded", 0, 1);
                    }
                }

            } // end loop walking file

        } catch (IOException | ParseException iOException) {
        }

        return myTripoliFractions;
    }

    /**
     *
     * @return the boolean
     */
    @Override
    public boolean getAndLoadRawIntensityDataForReview() {
        return false;
    }
}
