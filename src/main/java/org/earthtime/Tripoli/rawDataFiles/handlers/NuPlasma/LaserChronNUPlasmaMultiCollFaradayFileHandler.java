/*
 * LaserChronNUPlasmaMultiCollFaradayFileHandler.java
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
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
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
import org.earthtime.Tripoli.massSpecSetups.multiCollector.NUPlasma.NUPlasmaCollectorsEnum;
import org.earthtime.Tripoli.rawDataFiles.handlers.AbstractRawDataFileHandler;
import static org.earthtime.UPb_Redux.ReduxConstants.getMonthConversions;
import org.earthtime.UPb_Redux.filters.TxtFileFilter;
import org.earthtime.archivingTools.URIHelper;
import org.earthtime.utilities.FileHelper;
import org.earthtime.utilities.TimeToString;

/**
 *
 * @author James F. Bowring
 */
public class LaserChronNUPlasmaMultiCollFaradayFileHandler extends AbstractRawDataFileHandler implements //
        Serializable {

    // Class variables
    private static final long serialVersionUID = 4104909666221641003L;
    private static LaserChronNUPlasmaMultiCollFaradayFileHandler instance = new LaserChronNUPlasmaMultiCollFaradayFileHandler();
    private static String dataDelimiter;

    /**
     *
     */
    public LaserChronNUPlasmaMultiCollFaradayFileHandler() {

        super();

        NAME = "LaserChron NU Plasma MC Faraday File";

        aboutInfo = "Details: This is the default protocol for handling files produced at the Arizona LaserChron Center "//
                + " for Faraday analysis on the NU-Plasma.";

    }

    /**
     *
     * @return
     */
    public static LaserChronNUPlasmaMultiCollFaradayFileHandler getInstance() {
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
        String dialogTitle = "Select NU Plasma Faraday Raw Data File(s): *.txt";
        final String fileExtension = ".txt";
        FileFilter nonMacFileFilter = new TxtFileFilter();

        // oct 2014 allow for multiselect
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

        if (validFilesForConcatenation.size() > 1) {
            Collections.sort(validFilesForConcatenation, (File f1, File f2) -> Long.compare(f1.lastModified(), f2.lastModified()));

            String concatenatedFileName = "CONCAT_" + validFilesForConcatenation.get(0).getName().replace("." + rawDataFileTemplate.getFileType().getName(), "");

            // concatenation process assume first file is good until the end then strip off trailing info
            String firstLineInDataBlock = rawDataFileTemplate.getStartOfEachBlockFirstLine();
            String lastLineInBlock = rawDataFileTemplate.getEndOfEachBlockLastLine();
            String fileContents = URIHelper.getTextFromURI(validFilesForConcatenation.get(0).getAbsolutePath());

            // remove all quotes
            fileContents = fileContents.replaceAll("\"", "");

            // find last good analysis
            int indexOflastGoodData = fileContents.lastIndexOf(lastLineInBlock) + lastLineInBlock.length() + 1;// crlf
            //System.out.println(fileContents.substring(indexOflastGoodData));
            fileContents = fileContents.substring(0, indexOflastGoodData);

            // now loop through remaining files and append their data blocks
            for (int fileIndex = 1; fileIndex < validFilesForConcatenation.size(); fileIndex++) {
                String fileContentsConcat = URIHelper.getTextFromURI(validFilesForConcatenation.get(fileIndex).getAbsolutePath());

                // remove all quotes
                fileContentsConcat = fileContentsConcat.replaceAll("\"", "");
                int startingIndexOfBlockData = fileContentsConcat.indexOf(firstLineInDataBlock);

                indexOflastGoodData = fileContentsConcat.lastIndexOf(lastLineInBlock) + lastLineInBlock.length() + 1;// crlf
                fileContentsConcat = fileContentsConcat.substring(//
                        startingIndexOfBlockData,//
                        indexOflastGoodData + (fileContentsConcat.length() > indexOflastGoodData ? 1 : 0));

                fileContents += fileContentsConcat;
//                System.out.println(fileContentsConcat);

                concatenatedFileName += "_" + validFilesForConcatenation.get(fileIndex).getName().replace("." + rawDataFileTemplate.getFileType().getName(), "");
            }
            // write out concatenated file and then process it
            // new java 7 technique
            try {
                String concatFilePath = //
                        validFilesForConcatenation.get(0).getParent() + File.separator + concatenatedFileName + "." + rawDataFileTemplate.getFileType().getName();
                Files.write(Paths.get(concatFilePath), fileContents.getBytes());
                rawDataFile = new File(concatFilePath);
            } catch (IOException iOException) {
                rawDataFile = null;
            }
        } else {
            try {
                rawDataFile = validFilesForConcatenation.get(0);
            } catch (Exception e) {
                rawDataFile = null;
            }
        }

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
     * @param acquisition
     * @return Model
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
            String[] headerDetails = headerData[0].trim().split("\n");

            // march 2014 to handle possible tab delimiters such as 20 analysis runs
            String gainsPass1 = headerDetails[1].split(":")[1];
            if (gainsPass1.startsWith(" \t")) {
                dataDelimiter = "\t";
            } else {
                dataDelimiter = ",";
            }
            // in both cases following, there is a leading dataDelimiter, so n + 1 elements are recovered
            String[] gains = headerDetails[1].split(":")[1].split(dataDelimiter);
            // build gains models
            Map<String, Double> collectorNameToRelativeGainsMap = new TreeMap<>();
            // first gain is bogus
            for (int i = 0; i < NUPlasmaCollectorsEnum.values().length; i++) {
                double relGain = 0.0;
                try {
                    relGain = Double.valueOf(gains[i + 1]);
                } catch (NumberFormatException numberFormatException) {
                }
                // in order
                try {
                    collectorNameToRelativeGainsMap.put(NUPlasmaCollectorsEnum.values()[i].name(), relGain);
                } catch (Exception e) {
                }
            }

            acquisitionModel.setCollectorNameToRelativeGainsMap(collectorNameToRelativeGainsMap);

            String[] deadTimes = headerDetails[3].split(":")[1].split(dataDelimiter);
            // build gains models
            Map<String, Double> collectorNameToDeadTimesMap = new TreeMap<>();
            for (int i = 0; i < NUPlasmaCollectorsEnum.getIonCounterCollectorNames().length; i++) {
                double deadTime = 0.0;
                try {
                    deadTime = Double.valueOf(deadTimes[i + 1]);
                } catch (NumberFormatException numberFormatException) {
                }
                // in order
                try {
                    collectorNameToDeadTimesMap.put(NUPlasmaCollectorsEnum.getIonCounterCollectorNames()[i], deadTime);
                } catch (Exception e) {
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

        // split data into fractions
        String[] rawFractions = headerData[1].split(//
                rawDataFileTemplate.getStartOfEachBlockFirstLine().trim());

        String savedSampleName = null;

        // rawFractions[0] contains our time stamp and we want the date part
        String dateMY = rawFractions[0].substring(rawFractions[0].indexOf("on the") + 7).trim();
        // now check for the added day of week that someone put into these files
        String year = "";
        String month = "";
        String day = "";

        if (dateMY.matches("\\w+, \\w+ \\d+, \\d+")) {
            // this is of newer form Saturday, December 21, 2013
            String dateMYParts[] = dateMY.split(",");
            year = dateMYParts[2].trim();
            month = dateMYParts[1].trim().split(" ")[0].trim();
            day = dateMYParts[1].trim().split(" ")[1].trim();
        } else if (dateMY.matches("\\d+ \\w+ \\d+")) {
            // this is older form of 24 June 2011
            String dateMYParts[] = dateMY.split(" ");
            year = dateMYParts[2].trim();
            month = dateMYParts[1].trim().substring(0, 3);
            day = dateMYParts[0].trim();
        }

        calendar.set(Integer.parseInt(year), getMonthConversions(month), Integer.parseInt(day));

        Map<String, Integer> repeatedFractionNames = new HashMap<>();
//        DateFormat fractionTimeFormat = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);

        // split first block to get block size
        String[] fractionBlockOfScans = rawFractions[1].split("\n");
        int myBlockSize = fractionBlockOfScans.length - 2;

        for (int i = ignoreFirstFractions + 1; i < rawFractions.length; i++) {

            if (loadDataTask.isCancelled()) {
                break;
            }

            final int limit = ((100 * (i - ignoreFirstFractions)) / (rawFractions.length - ignoreFirstFractions));
            loadDataTask.firePropertyChange("progress", 0, limit);

            // split fractions into scans
            fractionBlockOfScans = rawFractions[i].split("\n");

            // test is number of scans correct allowing for 2 extra lines = first and last
            if ((fractionBlockOfScans.length - 2) == myBlockSize) {//rawDataFileTemplate.getBlockSize()) {
                // get fraction name and analysis start time
                // extract sample name from first fraction, and monitor for changes
                // "<>" is delimiter betwee name and aquisition time
                String[] sampleFractionTime = fractionBlockOfScans[0].split("<>");

                // sept 2012
                // find last "-" as delimeter to fractionID = number or standard name
                // split on "-" and find last one, then combine rest with trim for sample name
                String[] sampleFraction = sampleFractionTime[0].trim().split("-");

                int lengthSampleFraction = sampleFraction.length;
                String fractionID = sampleFraction[lengthSampleFraction - 1].trim();

                String sampleName = "";
                for (int j = 0; j < (lengthSampleFraction - 1); j++) {
                    sampleName += sampleFraction[j].trim();
                }

                // TODO: ask George about multiple samples 
                if (savedSampleName == null) {
                    savedSampleName = sampleName;
                }

                // extract time stamp : nonexistent before March 2011 see above definition of hs
                Date fractionDate = null;
                int AMPMval = 1;//PM
                if (sampleFractionTime.length > 1) {

                    String hms = sampleFractionTime[1].substring(20).trim();
                    // check for am or pm or nothing at end
                    if (hms.endsWith("AM")) {
                        //hms += " AM";
                        AMPMval = 0;
                    } else if (!hms.endsWith("M")) {
                        AMPMval = -1; // 24 hour clock
                    }

                    // april 2014 midnight problem
                    String[] hmsAMPM = hms.split(":");
                    String[] AMPM = hmsAMPM[2].split(" ");

                    // jan 2015 another change in output ??
                    // calendar roll over when no am pm present
                    if (AMPMval == -1) {
                        if (calendar.get(Calendar.HOUR_OF_DAY) > Integer.valueOf(hmsAMPM[0])) {
                            // we have rolled to new day
                            calendar.add(Calendar.DATE, 1);
                        }

                        calendar.set(Calendar.SECOND, Integer.valueOf(AMPM[0]));
                        calendar.set(Calendar.MINUTE, Integer.valueOf(hmsAMPM[1]));

                        calendar.set(Calendar.HOUR_OF_DAY, Integer.valueOf(hmsAMPM[0]));

                    } else {
                        calendar.set(Calendar.SECOND, Integer.valueOf(AMPM[0]));
                        calendar.set(Calendar.MINUTE, Integer.valueOf(hmsAMPM[1]));

                        calendar.set(Calendar.HOUR, Integer.valueOf(hmsAMPM[0]) % 12);

                        if (i == (ignoreFirstFractions + 1)) {
                            calendar.set(Calendar.AM_PM, AMPMval);
                        } else {
                            // check for rollover
                            if (calendar.get(Calendar.AM_PM) > AMPMval) {
                                // we have rolled to new day
                                calendar.add(Calendar.DATE, 1);
                            }
                        }
                        calendar.set(Calendar.AM_PM, AMPMval);
                    }

                    fractionDate = calendar.getTime();//   fractionTimeFormat.parse(fractionDateString);

                }

                // extract isStandard
                boolean isStandard = isStandardFractionID(fractionID);

                // check number of columns against number of collectors
                String[] columns = fractionBlockOfScans[1].split(dataDelimiter);
                // last entry is a counter
                if (columns.length == (getMassSpec().getVIRTUAL_COLLECTOR_COUNT() + 1)) {
                    // prepare block of scans
                    String[][] scanData = new String[myBlockSize][getMassSpec().getVIRTUAL_COLLECTOR_COUNT()];
                    for (int row = 1; row < fractionBlockOfScans.length - 1; row++) {
                        columns = fractionBlockOfScans[row].split(dataDelimiter);
                        System.arraycopy(columns, 0, scanData[row - 1], 0, getMassSpec().getVIRTUAL_COLLECTOR_COUNT());
                    }

                    String fullFractionId = sampleName + "-" + fractionID;
                    int count = 0;
                    if (repeatedFractionNames.containsKey(fullFractionId)) {
                        // increment count
                        count = repeatedFractionNames.get(fullFractionId);
                        count++;
                    }
                    repeatedFractionNames.put(fullFractionId, count);

                    String theFractionID = fullFractionId + ((count == 0) ? "" : "." + String.valueOf(count));

                    // nov 2014 broke into steps to provide cleaner logic
                    TripoliFraction tripoliFraction = //                           
                            new TripoliFraction( //
                                    theFractionID, //
                                    massSpec.getCommonLeadCorrectionHighestLevel(), //
                                    isStandard,
                                    fractionDate.getTime(), //
                                    fractionDate.getTime(),//
                                    myBlockSize);//, 
                    
                    SortedSet<DataModelInterface> rawRatios = massSpec.rawRatiosFactory(scanData, theFractionID, usingFullPropagation, tripoliFraction);
                    tripoliFraction.setRawRatios(rawRatios);
                    massSpec.processFractionRawRatios(scanData, fractionID, usingFullPropagation, tripoliFraction);

                    tripoliFraction.shadeDataActiveMapLeft(leftShadeCount);
                    tripoliFractions.add(tripoliFraction);

                    System.out.println(sampleName + "   " + theFractionID + " " + isStandard + "  \t" + TimeToString.timeStampString(fractionDate.getTime()));
                }
            }
        }

//        if (tripoliFractions.isEmpty()) {
//            tripoliFractions = null;
//        }

        return tripoliFractions;
    }
}
