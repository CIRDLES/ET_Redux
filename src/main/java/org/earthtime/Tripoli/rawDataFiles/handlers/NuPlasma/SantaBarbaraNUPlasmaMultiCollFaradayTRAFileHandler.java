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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.JFrame;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.earthtime.Tripoli.dataModels.DataModelInterface;
import org.earthtime.Tripoli.dataModels.RawIntensityDataModel;
import org.earthtime.Tripoli.fractions.TripoliFraction;
import org.earthtime.Tripoli.rawDataFiles.handlers.AbstractRawDataFileHandler;
import org.earthtime.UPb_Redux.filters.RunFileFilter;
import org.earthtime.UPb_Redux.filters.TxtFileFilter;
import org.earthtime.isotopes.IsotopesEnum;
import org.earthtime.utilities.FileHelper;

/**
 *
 * @author James F. Bowring
 */
public class SantaBarbaraNUPlasmaMultiCollFaradayTRAFileHandler extends AbstractRawDataFileHandler implements //
        Serializable {

    // Class variables
    private static final long serialVersionUID = 2903132020030712554L;
    private static SantaBarbaraNUPlasmaMultiCollFaradayTRAFileHandler instance = new SantaBarbaraNUPlasmaMultiCollFaradayTRAFileHandler();
    private String[] fractionNames;
    private List<String> fractionData;
    private int dataFoundIndex;
    private static Map<String, Integer> referenceMaterialIncrementerMap = null;

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

        fractionNames = new String[0];
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
        String fileExtension = ".run";
        FileFilter nonMacFileFilter = new RunFileFilter();

        rawDataFile = FileHelper.AllPlatformGetFile( //
                dialogTitle, tripoliRawDataFolder, fileExtension, nonMacFileFilter, false, new JFrame())[0];

        // ask for list of fraction names
        dialogTitle = "Select tab-delimited file of sample names exported from log file: *.txt";
        fileExtension = ".txt";
        nonMacFileFilter = new TxtFileFilter();

        File fractionNamesLogFile = FileHelper.AllPlatformGetFile( //
                dialogTitle, tripoliRawDataFolder, fileExtension, nonMacFileFilter, false, new JFrame())[0];

        try {
            List<String> fractionNamesData = Files.readLines(fractionNamesLogFile, Charsets.ISO_8859_1);
            // skip column names in row 0
            fractionNames = new String[fractionNamesData.size() - 1];
            for (int i = 1; i < fractionNamesData.size(); i++) {
                String[] lineContents = fractionNamesData.get(i).split("\t");
                fractionNames[i - 1] = lineContents[1];
            }

            massSpec.setFractionNames(fractionNames);

        } catch (IOException iOException) {
        }

        return rawDataFile;
    }

    /**
     *
     * @return the boolean
     */
    @Override
    public boolean getAndLoadRawIntensityDataForReview() {
        // July 2016 for TRA and review of raw data before processing
        boolean retVal = false;
        fractionData = null;

        if (referenceMaterialIncrementerMap == null) {
            referenceMaterialIncrementerMap = new ConcurrentHashMap<>();
            for (int i = 0; i < rawDataFileTemplate.getStandardIDs().length; i++) {
                referenceMaterialIncrementerMap.put(rawDataFileTemplate.getStandardIDs()[i], 1);
            }
        }

        try {
            fractionData = Files.readLines(rawDataFile, Charsets.ISO_8859_1);
            // validate file
            // TODO:
            //
            // build one giant set of rawratios
//            SortedSet<DataModelInterface> rawRatios = massSpec.rawRatiosFactoryRevised();

            // walk file to find start of data            
            dataFoundIndex = -1;
            for (int i = 0; i < fractionData.size(); i++) {
                if (fractionData.get(i).contains(rawDataFileTemplate.getStartOfDataSectionFirstLine())) {
                    dataFoundIndex = i + 1;
                    break;
                }
            }

            //populate peak with all data 
            ArrayList<double[]> backgroundAcquisitions = new ArrayList<>();
            ArrayList<double[]> peakAcquisitions = new ArrayList<>();
            if (dataFoundIndex > 0) {

                for (int i = dataFoundIndex; i < fractionData.size(); i++) {
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
                double[] onPeak = getSumOfIntensities();//((RawIntensityDataModel) massSpec.getU238()).getOnPeakVirtualCollector().getIntensities();
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

                // calculate good right to graph
                DescriptiveStatistics timeZeroDeltas = new DescriptiveStatistics();
                for (int i = 0; i < sessionTimeZeroIndices.size() - 1; i++) {
                    timeZeroDeltas.addValue(Math.abs(sessionTimeZeroIndices.get(i + 1) - sessionTimeZeroIndices.get(i)));
                }
                int timeToNextTimeZero = (int) timeZeroDeltas.getPercentile(95);

                Map<IsotopesEnum, DataModelInterface> isotopeToRawIntensitiesMap = massSpec.getIsotopeMappingModel().getIsotopeToRawIntensitiesMap();
                isotopeToRawIntensitiesMap.forEach((isotope, dataModel) -> {
                    ((RawIntensityDataModel) dataModel).setSessionTimeZeroIndices(sessionTimeZeroIndices);
                    ((RawIntensityDataModel) dataModel).setTimeToNextTimeZero(timeToNextTimeZero);
                    // educated guesses
                    ((RawIntensityDataModel) dataModel).setPeakLeftShade(5);
                    ((RawIntensityDataModel) dataModel).setPeakWidth(timeToNextTimeZero / 2);//140
                    ((RawIntensityDataModel) dataModel).setBackgroundRightShade(5);
                    ((RawIntensityDataModel) dataModel).setBackgroundWidth(timeToNextTimeZero / 8);//25
                    ((RawIntensityDataModel) dataModel).setTimeZeroRelativeIndex(timeToNextTimeZero / 4);//75
                });

            } else {
                retVal = false;
            }

        } catch (IOException iOException) {
        }

        return retVal;
    }

    private double[] getSumOfIntensities() {
        double[] sumOfIntensities = new double[((RawIntensityDataModel) massSpec.getU238()).getOnPeakVirtualCollector().getIntensities().length];
        Map<IsotopesEnum, DataModelInterface> isotopeToRawIntensitiesMap = massSpec.getIsotopeMappingModel().getIsotopeToRawIntensitiesMap();
        isotopeToRawIntensitiesMap.forEach((isotope, dataModel) -> {
            double[] onPeak = ((RawIntensityDataModel) dataModel).getOnPeakVirtualCollector().getIntensities();
            for (int i = 0; i < sumOfIntensities.length; i++) {
                sumOfIntensities[i] += onPeak[i];
            }
        });

        return sumOfIntensities;
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
            tripoliFractions = loadRawDataFile(loadDataTask, usingFullPropagation, leftShadeCount, ignoreFirstFractions, inLiveMode);
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

        SortedSet myTripoliFractions = new TreeSet<>();

        String referenceMaterialfractionID = fractionNames[0];

        // time stamp in form of  "18/02/2014","05:07 PM"
        Date runDate = null;
        DateFormat runDateFormatter = new SimpleDateFormat("dd/MM/yyyy,hh:mm aa");
        String runDateText = fractionData.get(dataFoundIndex - 2);
        long runEndTime = 0l;

        try {
            runDate = runDateFormatter.parse(runDateText.replace("\"", ""));
            runEndTime = runDate.getTime();
        } catch (ParseException parseException) {
        }
        long runStartTime = runEndTime - 200l * (fractionData.size() - dataFoundIndex);

        // each fraction name is in fractionNames and the rawIntensitiesDataModels for the session contain
        // the time-zero indices and background and peak offsets from time zero
        // we need to recover this info from only one of the data models as all are identical for now
        DataModelInterface firstModel = (DataModelInterface) massSpec.getVirtualCollectorModelMapToFieldIndexes().keySet().toArray()[0];
        List<Integer> sessionTimeZeroIndices = ((RawIntensityDataModel) firstModel).getSessionTimeZeroIndices();
        int peakLeftShade = ((RawIntensityDataModel) firstModel).getPeakLeftShade();
        int peakWidth = ((RawIntensityDataModel) firstModel).getPeakWidth();
        int backgroundRightShade = ((RawIntensityDataModel) firstModel).getBackgroundRightShade();
        int backgroundWidth = ((RawIntensityDataModel) firstModel).getBackgroundWidth();

        // walk the zero-time indices - one for each fraction
        int countIgnoredFractions = 0;
        for (int fractionIndex = 0; fractionIndex < sessionTimeZeroIndices.size(); fractionIndex++) {
            // test for ignored
            if (sessionTimeZeroIndices.get(fractionIndex) > 0) {
                if (loadDataTask.isCancelled()) {
                    break;
                }
                loadDataTask.firePropertyChange("progress", 0, ((100 * fractionIndex) / sessionTimeZeroIndices.size()));

                // background 
                List<double[]> backgroundAcquisitions = new ArrayList<>();

                int startOfBackgroundIndex = sessionTimeZeroIndices.get(fractionIndex) - backgroundRightShade - backgroundWidth;
                for (int acquisitionIndex = startOfBackgroundIndex; acquisitionIndex < sessionTimeZeroIndices.get(fractionIndex); acquisitionIndex++) {
                    // 238  232  208 207 206 204
                    double[] backgroundIntensities = new double[6];
                    backgroundAcquisitions.add(backgroundIntensities);

                    String[] acquisition = fractionData.get(dataFoundIndex + acquisitionIndex).split(",");
                    for (int index = 0; index < 6; index++) {
                        backgroundIntensities[index] = Double.parseDouble(acquisition[index]);
                    }
                }

                // peak                       
                List<double[]> peakAcquisitions = new ArrayList<>();

                int endOfPeakIndex = sessionTimeZeroIndices.get(fractionIndex) + peakLeftShade + peakWidth;
                for (int acquisitionIndex = sessionTimeZeroIndices.get(fractionIndex); acquisitionIndex < endOfPeakIndex; acquisitionIndex++) {
                    // 238  232  208 207 206 204
                    double[] peakIntensities = new double[6];
                    peakAcquisitions.add(peakIntensities);

                    String[] acquisition = fractionData.get(dataFoundIndex + acquisitionIndex).split(",");
                    for (int index = 0; index < 6; index++) {
                        peakIntensities[index] = Double.parseDouble(acquisition[index]);
                    }
                }

                // specify fraction and turn off data points at ends
                // calculate dates
                String fractionID = fractionNames[fractionIndex - countIgnoredFractions];
                boolean isReferenceMaterial = (fractionID.toLowerCase().contains(referenceMaterialfractionID));
                if (referenceMaterialIncrementerMap.containsKey(fractionID)) {
                    int refMatIndex = referenceMaterialIncrementerMap.get(fractionID);
                    referenceMaterialIncrementerMap.put(fractionID, refMatIndex + 1);
                    fractionID = fractionID + "-" + String.valueOf(refMatIndex);
                }

                // peak stamp = timezero
                long fractionPeakTimeStamp = runStartTime + 200l * sessionTimeZeroIndices.get(fractionIndex);
                long fractionBackgroundTimeStamp = fractionPeakTimeStamp - 200l * (backgroundRightShade + backgroundWidth);

                TripoliFraction tripoliFraction
                        = new TripoliFraction( //
                                //
                                fractionID, //
                                massSpec.getCommonLeadCorrectionHighestLevel(), //
                                isReferenceMaterial, false,
                                fractionBackgroundTimeStamp, //
                                fractionPeakTimeStamp,
                                peakAcquisitions.size());

                SortedSet<DataModelInterface> rawRatios = massSpec.rawRatiosFactoryRevised();

                tripoliFraction.setRawRatios(rawRatios);

                massSpec.setCountOfAcquisitions(peakAcquisitions.size());

                massSpec.processFractionRawRatiosII(null, null, //
                        backgroundAcquisitions, peakAcquisitions, usingFullPropagation, tripoliFraction, inLiveMode);

                tripoliFraction.shadeDataActiveMapLeft(0);

                // turn off data point between time-zero and end background, start peak
                for (int offDataIndex = 0; offDataIndex < peakLeftShade; offDataIndex++) {
                    tripoliFraction.toggleOneDataAquisition(offDataIndex, false);
                }
                // don't have system for background yet

                System.out.println("\n**** Element II FractionID  " + fractionID + " refMat? " + isReferenceMaterial + "  livemode = " + inLiveMode + " <<<<<<<<<<<<<<<<<<\n");

                myTripoliFractions.add(tripoliFraction);

                if (isReferenceMaterial) {
                    loadDataTask.firePropertyChange("refMaterialLoaded", 0, 1);
                }

            } else {
                countIgnoredFractions++;
            }
        }

        return myTripoliFractions;
    }

}
