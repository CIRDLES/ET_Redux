/*
 * SantaBarbaraNUPlasmaMultiCollFaradayTRAFileHandler.java
 *
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
package org.earthtime.Tripoli.rawDataFiles.handlers.NuPlasma;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.JFrame;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.earthtime.Tripoli.dataModels.DataModelInterface;
import org.earthtime.Tripoli.dataModels.RawIntensityDataModel;
import org.earthtime.Tripoli.fractions.TripoliFraction;
import org.earthtime.Tripoli.rawDataFiles.handlers.AbstractRawDataFileHandler;
import org.earthtime.UPb_Redux.filters.RunFileFilter;
import org.earthtime.archivingTools.URIHelper;
import org.earthtime.isotopes.IsotopesEnum;
import org.earthtime.utilities.FileHelper;
import org.earthtime.utilities.TimeToString;

/**
 *
 * @author James F. Bowring
 */
public class SantaBarbaraNUPlasmaMultiCollFaradayTRAFileHandler extends AbstractRawDataFileHandler implements //
        Serializable {

    // Class variables
//    private static final long serialVersionUID = 4617107661618798359L;
    private static SantaBarbaraNUPlasmaMultiCollFaradayTRAFileHandler instance = new SantaBarbaraNUPlasmaMultiCollFaradayTRAFileHandler();

    /**
     *
     */
    public SantaBarbaraNUPlasmaMultiCollFaradayTRAFileHandler() {

        super();

        NAME = "Santa Barbara NU Plasma MC Faraday TRA File";

        aboutInfo = "Details: This is the default protocol for handling files produced at "//
                + "UC Santa Barbara "//
                + "for Faraday analysis on the NU-Plasma with TRA (time-resolved analysis.) "//
                + "Pattern of aquisitions dated July 2016.";
    }

    /**
     *
     * @return
     */
    public static SantaBarbaraNUPlasmaMultiCollFaradayTRAFileHandler getInstance() {
        return instance;
    }

    /**
     *
     *
     * @param tripoliRawDataFolder
     * @return
     */
    @Override
    public File validateAndGetHeaderDataFromRawIntensityFile(File tripoliRawDataFolder) {
        String dialogTitle = "Select NU Plasma Faraday TRA Raw Data File(s): *.run";
        final String fileExtension = ".run";
        FileFilter nonMacFileFilter = new RunFileFilter();

        rawDataFile = FileHelper.AllPlatformGetFile( //
                dialogTitle, tripoliRawDataFolder, fileExtension, nonMacFileFilter, false, new JFrame())[0];
        return rawDataFile;
    }

    /**
     *
     * @return the boolean
     */
    @Override
    public boolean getAndLoadRawIntensityDataFilePhaseI() {
        // July 2016 for TRA and review of raw data before processing
        boolean retVal = false;
        List<String> fractionData = null;

        try {
            fractionData = Files.readLines(rawDataFile, Charsets.ISO_8859_1);
            // validate file
            // TODO:
            //
            // build one giant set of rawratios
            SortedSet<DataModelInterface> rawRatios = massSpec.rawRatiosFactoryRevised();

            // walk file to find start of data            
            int dataFoundIndex = -1;
            for (int i = 0; i < fractionData.size(); i++) {
                if (fractionData.get(i).contains(rawDataFileTemplate.getStartOfDataSectionFirstLine())) {
                    dataFoundIndex = i;
                    break;
                }
            }

            //populate peak with all data 
            ArrayList<double[]> backgroundAcquisitions = new ArrayList<>();
            ArrayList<double[]> peakAcquisitions = new ArrayList<>();
            if (dataFoundIndex > 0) {
                for (int i = dataFoundIndex + 1; i < fractionData.size(); i++) {
                    // 238  232  208 207 206 204
                    double[] peakIntensities = new double[6];
                    peakAcquisitions.add(peakIntensities);

                    String[] acquisition = fractionData.get(i).split(",");
                    for (int index = 0; index < 6; index++) {
                        peakIntensities[index] = Double.parseDouble(acquisition[index]);
                    }
                }
                massSpec.initializeVirtualCollectorsWithData(backgroundAcquisitions, peakAcquisitions);
                massSpec.setCountOfAcquisitions(peakAcquisitions.size());

                // detect starts of onpeak
                NumberFormat format = new DecimalFormat("+#0.00;-#0.00");
                List<Integer> sessionTimeZeroIndices = new ArrayList<>();
                double[] onPeak = ((RawIntensityDataModel) massSpec.getU238()).getOnPeakVirtualCollector().getIntensities();
                boolean withinPeak = false;
                int lastNegativeIndex = 0;
                for (int i = 0; i < onPeak.length; i++) {
                    if (onPeak[i] > 0.0) {
                        // use a sliding window of 10
                        int windowSize = 10;
                        if (!withinPeak) {
                            DescriptiveStatistics window = new DescriptiveStatistics();
                            for (int j = i; j < i + windowSize; j++) {
                                window.addValue(onPeak[j]);
                            }
                            // test
                            if ((window.getElement(0) >= window.getElement(1))//
                                    && (window.getElement(0) >= window.getPercentile(50))//
                                    && (window.getElement(windowSize - 1) >= 0)) {
                                // we have local maxima of downward-sloping
                                withinPeak = true;
                                sessionTimeZeroIndices.add(lastNegativeIndex + 1);
                            }
                        }

                    } else {
                        withinPeak = false;
                        lastNegativeIndex = i;
                    }
                }
                retVal = true;
                Map<IsotopesEnum, DataModelInterface> isotopeToRawIntensitiesMap = massSpec.getIsotopeMappingModel().getIsotopeToRawIntensitiesMap();
                isotopeToRawIntensitiesMap.forEach((isotope, dataModel) -> {
                    ((RawIntensityDataModel) dataModel).setSessionTimeZeroIndices(sessionTimeZeroIndices);
                });

                massSpec.setCountOfFractions(sessionTimeZeroIndices.size());
            } else {
                retVal = false;
            }

        } catch (IOException iOException) {
        }

        return retVal;
    }

    /**
     *
     * @param loadDataTask the value of loadRawDataPropertyChangeListener
     * @param usingFullPropagation the value of usingFullPropagation
     * @param leftShadeCount the value of leftShadeCount
     * @param ignoreFirstFractions the value of ignoreFirstFractions
     * @param inLiveMode the value of inLiveMode
     */
    @Override
    public void getAndLoadRawIntensityDataFile(SwingWorker loadDataTask, boolean usingFullPropagation, int leftShadeCount, int ignoreFirstFractions, boolean inLiveMode) {

        if (rawDataFile != null) {
            // create fractions from raw data and perform corrections and calculate ratios
            loadRawDataFile(loadDataTask, usingFullPropagation, leftShadeCount, ignoreFirstFractions, inLiveMode);
        }

    }

    /**
     *
     * @param fileContents
     * @return
     */
    @Override
    protected boolean areKeyWordsPresent(String fileContents) {
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
    public boolean isStandardFractionID(String fractionID) {
        boolean retVal = false;

        for (int i = 0; i < getRawDataFileTemplate().getStandardIDs().length; i++) {
            retVal = retVal //
                    || fractionID.endsWith(rawDataFileTemplate.getStandardIDs()[i].trim())//
                    || fractionID.contains(rawDataFileTemplate.getStandardIDs()[i].trim());
        }

        return retVal;
    }

    /**
     *
     *
     * @param usingFullPropagation the value of usingFullPropagation
     * @param ignoreFirstFractions the value of ignoreFirstFractions
     * @return
     */
    /**
     *
     * @param loadDataTask
     * @param usingFullPropagation the value of usingFullPropagation
     * @param leftShadeCount the value of leftShadeCount
     * @param ignoreFirstFractions the value of ignoreFirstFractions
     * @param inLiveMode the value of inLiveMode
     * @return the
     * java.util.SortedSet<org.earthtime.Tripoli.fractions.TripoliFraction>
     */
    @Override
    protected SortedSet<TripoliFraction> loadRawDataFile(SwingWorker loadDataTask, boolean usingFullPropagation, int leftShadeCount, int ignoreFirstFractions, boolean inLiveMode) {

        Calendar calendar = Calendar.getInstance();
        calendar.clear();

        tripoliFractions = new TreeSet<>();

        String fileContents = URIHelper.getTextFromURI(rawDataFile.getAbsolutePath());

        // remove all quotes
        fileContents = fileContents.replaceAll("\"", "");

        // split off header
        String[] headerData = fileContents.split( //
                rawDataFileTemplate.getStartOfDataSectionFirstLine().trim());

        // split header to get sample name and date time
        String[] headerDetails = headerData[0].split("\n");

        // time stamp in form of 06/05/2014,01:27:57 PM
        Date runDate = null;
        DateFormat runDateFormatter = new SimpleDateFormat("MM/dd/yyyy,hh:mm:ss aa");// z");
        String runDateText = headerDetails[headerDetails.length - 1];
        long runStartTime = 0l;
        long fractionBackgroundStartTime = 0l;

        try {
            runDate = runDateFormatter.parse(runDateText);// + " " + rawDataFileTemplate.getTimeZone().getID());
            runStartTime = runDate.getTime();
            fractionBackgroundStartTime = runStartTime;
        } catch (ParseException parseException) {
        }

        String sampleRunName = headerDetails[headerDetails.length - 2];

        // split data into fractions consisting of background and peak
        // here the key is in knowing that the voltages of U238 go negative for background according to Gehrels
        // rawAcquisitions has one line for each set of readings
        String[] rawAcquisitions = headerData[1].split("\n");
        // each fraction has a run of background and then a run of peak acquisitions

        int fractionCounter = 0;
        boolean readingPeaks = false;
        ArrayList<double[]> backgroundAcquisitions = new ArrayList<>();
        ArrayList<double[]> peakAcquisitions = new ArrayList<>();

        // first acquisition is blank
        for (int i = 150; i < rawAcquisitions.length; i++) {
            if (loadDataTask.isCancelled()) {
                break;
            }

            final int limit = ((100 * (i)) / (rawAcquisitions.length));
            loadDataTask.firePropertyChange("progress", 0, limit);

            // this hard-wired specification from the lab dated july 21 2014
            if (fractionCounter < 133) {
                String[] rawIntensities = rawAcquisitions[i].split(",");
                double i238 = Double.parseDouble(rawIntensities[0]);
                boolean isStandard = false;
                if (fractionCounter < 5) {
                    isStandard = true;
                } else if (((fractionCounter - 4) % 6) == 0) {//          (fractionCounter == 124) {
                    isStandard = true;
                } else if (fractionCounter >= 130) {
                    isStandard = true;
                }

                // test if have  pair of background and peak = completed fraction
                if (readingPeaks && (i238 <= 0.001)) {
                    // process new fraction and reset data collectors *************

                    int readCountBackgroundAcquisitions = backgroundAcquisitions.size();
                    int readCountPeakAcquisitions = peakAcquisitions.size();
                    // trim front and back of data
                    int trimCountFront = 5;
                    for (int c = 0; c < trimCountFront; c++) {
                        backgroundAcquisitions.remove(0);
                        peakAcquisitions.remove(0);
                    }
                    int trimCountBack = 15;
                    for (int c = 0; c < trimCountBack; c++) {
                        backgroundAcquisitions.remove(backgroundAcquisitions.size() - 1);
                        peakAcquisitions.remove(peakAcquisitions.size() - 1);
                    }
                    System.out.println("read in fraction " + fractionCounter + "  " + backgroundAcquisitions.size() + "   " + peakAcquisitions.size());

                    String theFractionID;
                    if (isStandard) {
                        theFractionID = sampleRunName + "-RM." + String.valueOf(fractionCounter);
                    } else {
                        theFractionID = sampleRunName + "-" + String.valueOf(fractionCounter);
                    }

                    // nov 2014 broke into steps to provide cleaner logic
                    TripoliFraction tripoliFraction
                            = //                           
                            new TripoliFraction( //
                                    //
                                    theFractionID, //
                                    massSpec.getCommonLeadCorrectionHighestLevel(), //
                                    isStandard,
                                    fractionBackgroundStartTime, //
                                    fractionBackgroundStartTime + readCountBackgroundAcquisitions * massSpec.getCOLLECTOR_DATA_FREQUENCY_MILLISECS(),
                                    peakAcquisitions.size());

                    SortedSet<DataModelInterface> rawRatios = massSpec.rawRatiosFactoryRevised();
                    tripoliFraction.setRawRatios(rawRatios);

                    massSpec.setCountOfAcquisitions(peakAcquisitions.size());
                    massSpec.processFractionRawRatiosTRA(backgroundAcquisitions, peakAcquisitions, theFractionID, usingFullPropagation, tripoliFraction);

                    tripoliFraction.shadeDataActiveMapLeft(leftShadeCount);
                    tripoliFractions.add(tripoliFraction);

                    System.out.println(theFractionID + " " + isStandard + "  \t" + TimeToString.timeStampString(fractionBackgroundStartTime) + "\n\n<><><><><><><><><><><><><><><><><><><><><><><>");

                    // update time
                    fractionBackgroundStartTime += massSpec.getCOLLECTOR_DATA_FREQUENCY_MILLISECS() * (readCountBackgroundAcquisitions + readCountPeakAcquisitions);
                    // reset data collectors
                    fractionCounter++;
                    readingPeaks = false;
                    backgroundAcquisitions = new ArrayList<>();
                    peakAcquisitions = new ArrayList<>();
                }

                // we want 238, 232, skip, 208, 207, 206, 204, skip, 202
                if (i238 <= 0.001) {
                    double[] backgroundIntensities = new double[7];
                    backgroundAcquisitions.add(backgroundIntensities);
                    backgroundIntensities[0] = i238;
                    backgroundIntensities[1] = Double.parseDouble(rawIntensities[1]);
                    backgroundIntensities[2] = Double.parseDouble(rawIntensities[3]);
                    backgroundIntensities[3] = Double.parseDouble(rawIntensities[4]);
                    backgroundIntensities[4] = Double.parseDouble(rawIntensities[5]);
                    backgroundIntensities[5] = Double.parseDouble(rawIntensities[6]);
                    backgroundIntensities[6] = Double.parseDouble(rawIntensities[8]);
                } else {
                    double[] peakIntensities = new double[7];
                    peakAcquisitions.add(peakIntensities);
                    peakIntensities[0] = i238;
                    peakIntensities[1] = Double.parseDouble(rawIntensities[1]);
                    peakIntensities[2] = Double.parseDouble(rawIntensities[3]);
                    peakIntensities[3] = Double.parseDouble(rawIntensities[4]);
                    peakIntensities[4] = Double.parseDouble(rawIntensities[5]);
                    peakIntensities[5] = Double.parseDouble(rawIntensities[6]);
                    peakIntensities[6] = Double.parseDouble(rawIntensities[8]);

                    readingPeaks = true;
                }
            }
        }

//        if (tripoliFractions.isEmpty()) {
//            tripoliFractions = null;
//        }
        return tripoliFractions;
    }
}
