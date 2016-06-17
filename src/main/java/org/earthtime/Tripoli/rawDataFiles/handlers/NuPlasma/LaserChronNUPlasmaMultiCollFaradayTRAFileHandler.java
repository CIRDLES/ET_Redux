/*
 * LaserChronNUPlasmaMultiCollFaradayTRAFileHandler.java
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

import java.io.File;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;
import org.earthtime.Tripoli.dataModels.DataModelInterface;
import org.earthtime.Tripoli.dataModels.inputParametersModels.AbstractAcquisitionModel;
import org.earthtime.Tripoli.dataModels.inputParametersModels.StaticAcquisition;
import org.earthtime.Tripoli.fractions.TripoliFraction;
import org.earthtime.Tripoli.massSpecSetups.multiCollector.NUPlasma.GehrelsNUPlasmaSetupUPbFarTRA;
import org.earthtime.Tripoli.massSpecSetups.multiCollector.NUPlasma.NUPlasmaCollectorsEnum;
import org.earthtime.Tripoli.rawDataFiles.handlers.AbstractRawDataFileHandler;
import org.earthtime.UPb_Redux.filters.TxtFileFilter;
import org.earthtime.archivingTools.URIHelper;
import org.earthtime.utilities.FileHelper;
import org.earthtime.utilities.TimeToString;

/**
 *
 * @author James F. Bowring
 */
public class LaserChronNUPlasmaMultiCollFaradayTRAFileHandler extends AbstractRawDataFileHandler implements //
        Serializable {

    // Class variables
    private static final long serialVersionUID = 4617107661618798359L;
    private static LaserChronNUPlasmaMultiCollFaradayTRAFileHandler instance = new LaserChronNUPlasmaMultiCollFaradayTRAFileHandler();
//    private static String dataDelimiter;

    /**
     *
     */
    public LaserChronNUPlasmaMultiCollFaradayTRAFileHandler() {

        super();

        NAME = "LaserChron NU Plasma MC Faraday TRA File";

        aboutInfo = "Details: This is the default protocol for handling files produced at "//
                + "the Arizona LaserChron Center "//
                + "for Faraday analysis on the NU-Plasma with TRA (time-resolved analysis.) "//
                + "Pattern of aquisitions dated 21 July 2014.";

    }

    /**
     *
     * @return
     */
    public static LaserChronNUPlasmaMultiCollFaradayTRAFileHandler getInstance() {
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
        String dialogTitle = "Select NU Plasma Faraday TRA Raw Data File(s): *.txt";
        final String fileExtension = ".txt";
        FileFilter nonMacFileFilter = new TxtFileFilter();

        // oct 2014 allow for multiselect
        // dec 2014 this file is a copy of file-based aquisition, so not yet sure if concatenaitonwill be supported
        rawDataFile = null;
        File[] filesToConcatenate = FileHelper.AllPlatformGetFile( //
                dialogTitle, tripoliRawDataFolder, fileExtension, nonMacFileFilter, true, new JFrame());

        // pre-process to be sure valid files
        ArrayList<File> validFilesForConcatenation = new ArrayList<>();
        for (int fileIndex = 0; fileIndex < filesToConcatenate.length; fileIndex++) {
            if (isValidRawDataFileType(filesToConcatenate[fileIndex])) {
                validFilesForConcatenation.add(filesToConcatenate[fileIndex]);
            }
        }

//        if (validFilesForConcatenation.size() > 1) {
//            Collections.sort(validFilesForConcatenation, new Comparator<File>() {
//
//                @Override
//                public int compare(File f1, File f2) {
//                    return Long.compare(f1.lastModified(), f2.lastModified());
//                }
//            });
//
//            String concatenatedFileName = "CONCAT_" + validFilesForConcatenation.get(0).getName().replace("." + rawDataFileTemplate.getFileType().getName(), "");
//
//            // concatenation process assume first file is good until the end then strip off trailing info
//            String firstLineInDataBlock = rawDataFileTemplate.getStartOfEachBlockFirstLine();
//            String lastLineInBlock = rawDataFileTemplate.getEndOfEachBlockLastLine();
//            String fileContents = URIHelper.getTextFromURI(validFilesForConcatenation.get(0).getAbsolutePath());
//
//            // remove all quotes
//            fileContents = fileContents.replaceAll("\"", "");
//
//            // find last good analysis
//            int indexOflastGoodData = fileContents.lastIndexOf(lastLineInBlock) + lastLineInBlock.length() + 1;// crlf
//            //System.out.println(fileContents.substring(indexOflastGoodData));
//            fileContents = fileContents.substring(0, indexOflastGoodData);
//
//            // now loop through remaining files and append their data blocks
//            for (int fileIndex = 1; fileIndex < validFilesForConcatenation.size(); fileIndex++) {
//                String fileContentsConcat = URIHelper.getTextFromURI(validFilesForConcatenation.get(fileIndex).getAbsolutePath());
//
//                // remove all quotes
//                fileContentsConcat = fileContentsConcat.replaceAll("\"", "");
//                int startingIndexOfBlockData = fileContentsConcat.indexOf(firstLineInDataBlock);
//
//                indexOflastGoodData = fileContentsConcat.lastIndexOf(lastLineInBlock) + lastLineInBlock.length() + 1;// crlf
//                fileContentsConcat = fileContentsConcat.substring(//
//                        startingIndexOfBlockData,//
//                        indexOflastGoodData + (fileContentsConcat.length() > indexOflastGoodData ? 1 : 0));
//
//                fileContents += fileContentsConcat;
////                System.out.println(fileContentsConcat);
//
//                concatenatedFileName += "_" + validFilesForConcatenation.get(fileIndex).getName().replace("." + rawDataFileTemplate.getFileType().getName(), "");
//            }
//            // write out concatenated file and then process it
//            // new java 7 technique
//            try {
//                String concatFilePath = //
//                        validFilesForConcatenation.get(0).getParent() + File.separator + concatenatedFileName + "." + rawDataFileTemplate.getFileType().getName();
//                Files.write(Paths.get(concatFilePath), fileContents.getBytes());
//                rawDataFile = new File(concatFilePath);
//            } catch (IOException iOException) {
//                rawDataFile = null;
//            }
//        } else {
        try {
            rawDataFile = validFilesForConcatenation.get(0);
        } catch (Exception e) {
            rawDataFile = null;
        }
//        }

        if (rawDataFile != null) {
            if (isValidRawDataFileType(rawDataFile)) {
                // load header data into acquisitionModel instance
                boolean success = loadDataSetupParametersFromRawDataFileHeader(rawDataFileTemplate.getAcquisitionModel());
                if (!success) {
                    rawDataFile = null;
                }

            } else {
                rawDataFile = null;

                JOptionPane.showMessageDialog(
                        null,
                        new String[]{"Selected raw data file was not valid."},
                        "ET Redux Warning",
                        JOptionPane.WARNING_MESSAGE);
            }
        }

        return rawDataFile;
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

//        return rawDataFile;
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
            retVal = //
                    retVal //
                    || fractionID.endsWith(rawDataFileTemplate.getStandardIDs()[i].trim())//
                    || fractionID.contains(rawDataFileTemplate.getStandardIDs()[i].trim());
        }

        return retVal;
    }

    /**
     *
     * @param acquisitionModel
     * @return 
     */
    protected boolean loadDataSetupParametersFromRawDataFileHeader(AbstractAcquisitionModel acquisitionModel) {
        boolean retVal = true;
        String fileContents = URIHelper.getTextFromURI(rawDataFile.getAbsolutePath());
        if (areKeyWordsPresent(fileContents)) {

            // remove all quotes
            fileContents = fileContents.replaceAll("\"", "");

            // split off header
            String[] headerData = fileContents.split( //
                    rawDataFileTemplate.getStartOfDataSectionFirstLine().trim());

            // OCT 2012
            // customize massspec setup for this data aquisition
            // extract gains and deadtimes
//            String[] headerDetails = headerData[0].trim().split("\n");

//            // march 2014 to handle possible tab delimiters such as 20 analysis runs
//            String gainsPass1 = headerDetails[1].split(":")[1];
//            if (gainsPass1.startsWith(" \t")) {
//                dataDelimiter = "\t";
//            } else {
//                dataDelimiter = ",";
//            }
//            // in both cases following, there is a leading dataDelimiter, so n + 1 elements are recovered
//            String[] gains = headerDetails[1].split(":")[1].split(dataDelimiter);
//            // build gains models
            Map<String, Double> collectorNameToRelativeGainsMap = new TreeMap<>();
            // first gain is bogus
            for (int i = 0; i < NUPlasmaCollectorsEnum.values().length; i++) {
                double relGain = 0.0;
//                try {
//                    relGain = Double.valueOf(gains[i + 1]);
//                } catch (NumberFormatException numberFormatException) {
//                }
                // in order
                try {
                    collectorNameToRelativeGainsMap.put(NUPlasmaCollectorsEnum.values()[i].name(), relGain);
                } catch (Exception e) {
                }
            }

            acquisitionModel.setCollectorNameToRelativeGainsMap(collectorNameToRelativeGainsMap);

//            String[] deadTimes = headerDetails[3].split(":")[1].split(dataDelimiter);
            // build gains models
            Map<String, Double> collectorNameToDeadTimesMap = new TreeMap<>();
            for (String ionCounterCollectorName : NUPlasmaCollectorsEnum.getIonCounterCollectorNames()) {
                double deadTime = 0.0;
//                try {
//                    deadTime = Double.valueOf(deadTimes[i + 1]);
//                } catch (NumberFormatException numberFormatException) {
//                }
                // in order
                try {
                    collectorNameToDeadTimesMap.put(ionCounterCollectorName, deadTime);
                }catch (Exception e) {
                }
            }

            acquisitionModel.setCollectorNameToDeadTimesMap(collectorNameToDeadTimesMap);

            ((StaticAcquisition) acquisitionModel).setCycleDurationInSeconds(//
                    massSpec.getCOLLECTOR_DATA_FREQUENCY_MILLISECS() / 1000.0);

            // this call sets all settable values, so they need to be primed from massspec setup first if appropriate
            acquisitionModel.updateMassSpec(massSpec);

        } else {
            JOptionPane.showMessageDialog(
                    null,
                    new String[]{"Selected raw data file was not valid."},
                    "ET Redux Warning",
                    JOptionPane.WARNING_MESSAGE);

            retVal = false;
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
     * @return the java.util.SortedSet<org.earthtime.Tripoli.fractions.TripoliFraction>
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
                } else if (((fractionCounter - 4) % 6) == 0){//          (fractionCounter == 124) {
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
                    int trimCountBack =15;
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
                    TripoliFraction tripoliFraction = //                           
                            new TripoliFraction( //
                                    //
                                    theFractionID, //
                                    massSpec.getCommonLeadCorrectionHighestLevel(), //
                                    isStandard,
                                    fractionBackgroundStartTime, //
                                    fractionBackgroundStartTime + readCountBackgroundAcquisitions * massSpec.getCOLLECTOR_DATA_FREQUENCY_MILLISECS(),
                                    peakAcquisitions.size());

                    SortedSet<DataModelInterface> rawRatios = ((GehrelsNUPlasmaSetupUPbFarTRA) massSpec).rawRatiosFactoryRevised();
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
