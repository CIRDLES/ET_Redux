/*
 * KoslerAgilent7700FileHandler
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
package org.earthtime.Tripoli.rawDataFiles.handlers.Agilent;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ForkJoinPool;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.earthtime.Tripoli.dataModels.DataModelInterface;
import org.earthtime.Tripoli.fractions.TripoliFraction;
import org.earthtime.Tripoli.rawDataFiles.handlers.AbstractRawDataFileHandler;
import org.earthtime.archivingTools.URIHelper;
import org.earthtime.utilities.FileHelper;

/**
 *
 * @author James F. Bowring
 */
public class KoslerAgilent7700FileHandler extends AbstractRawDataFileHandler implements //
        Serializable {

    // Class variables
    // private static final long serialVersionUID = 3111511502335804607L;
    private static KoslerAgilent7700FileHandler instance = new KoslerAgilent7700FileHandler();
    public static ForkJoinPool fjPool = new ForkJoinPool();
    private File[] analysisFiles;

    /**
     *
     * @param massSpec
     * @param rawDataFileTemplate
     */
    private KoslerAgilent7700FileHandler() {

        super();
        NAME = "Kosler Agilent 7700 Folder";
        aboutInfo = "Details: This is the Kosler protocol for an Agilent 7700 used in 2015 round robin.";
    }

    /**
     *
     * @return
     */
    public static KoslerAgilent7700FileHandler getInstance() {
        return instance;
    }

    /**
     *
     * @param tripoliRawDataFolder
     * @return
     */
    @Override
    public File validateAndGetHeaderDataFromRawIntensityFile(File tripoliRawDataFolder) {
        String dialogTitle = "Select an Agilent 7700 Raw Data Folder:";

        rawDataFile = FileHelper.AllPlatformGetFolder(dialogTitle, tripoliRawDataFolder);
        return rawDataFile;
    }

    /**
     *
     * @param loadDataTask the value of loadRawDataTaskListener
     * @param usingFullPropagation the value of usingFullPropagation
     * @param leftShadeCount the value of leftShadeCount
     * @param ignoreFirstFractions the value of ignoreFirstFract
     * @param inLiveMode the value of inLiveMode
     */
    @Override
    public void getAndLoadRawIntensityDataFile(SwingWorker loadDataTask, boolean usingFullPropagation, int leftShadeCount, int ignoreFirstFractions, boolean inLiveMode) {

        // Agilent has folder of csv files plus some xls files
        analysisFiles = rawDataFile.listFiles((File dir, String name) -> (name.toLowerCase().endsWith(".csv")));

        if (analysisFiles.length > 0) {
            Arrays.sort(analysisFiles, new FractionFileModifiedComparator());

            String onPeakFileContents = URIHelper.getTextFromURI(analysisFiles[0].getAbsolutePath());
            if (isValidRawDataFileType(analysisFiles[0]) //
                    && //
                    areKeyWordsPresent(onPeakFileContents)) {
                // create fractions from raw data and perform corrections and calculate ratios
                tripoliFractions = loadRawDataFile(loadDataTask, usingFullPropagation, leftShadeCount, 0, inLiveMode);
            }
        } else {
            JOptionPane.showMessageDialog(
                    null,
                    new String[]{"Selected raw data folder does not contain valid files."},
                    "ET Redux Warning",
                    JOptionPane.WARNING_MESSAGE);

            rawDataFile = null;
        }
    }

    /**
     *
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
        for (String standardID : getRawDataFileTemplate().getStandardIDs()) {
            retVal = retVal || fractionID.toUpperCase().contains(standardID.toUpperCase());
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

//        RawFractionFolderProcesserTask rawFilesTask = new RawFractionFolderProcesserTask(analysisFiles);
//         assume we are golden        
        for (int f = 0; f < analysisFiles.length; f++) {

            if (loadDataTask.isCancelled()) {
                break;
            }
            loadDataTask.firePropertyChange("progress", 0, ((100 * f) / analysisFiles.length));
            String fractionID = analysisFiles[f].getName().toUpperCase().replace(".CSV", "");

            // hard-wired april 2015
            boolean isStandard = false;
            if (f < 3) {
                isStandard = true;
            } else if ((analysisFiles.length - f) < 4) {
                isStandard = true;
            }

            // get file contents
            String fractionFileContents = URIHelper.getTextFromURI(analysisFiles[f].getAbsolutePath());
            String[] fractionFileRows = fractionFileContents.split("\n");

            // first get time stamp for file in row 2
            // form = Acquired      : 04/04/2013 1:22:25 PM using AcqMethod SJ_ZRILC.m
            String timeStampFromRow2[] = fractionFileRows[2].split(" :")[1].split(" +");

            String fractionDate
                    = //
                    timeStampFromRow2[1] + " " // day/month/year
                    + timeStampFromRow2[2] + " " // hour:min:sec
                    + timeStampFromRow2[3] + " " // AM/PM
                    ;

            // Get the default MEDIUM/SHORT DateFormat
            DateFormat fractionTimeFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);

            // Parse the fractionDateValue
            Date fractionDateValue;
            try {
                fractionDateValue = fractionTimeFormat.parse(fractionDate);

                // each acquisition file contains background followed by peak follwed by background
                // iinitial soultion is to hard wire the first background and peak
                // later we will give user interactive tools to pick them out
                ArrayList<double[]> backgroundAcquisitions = new ArrayList<>();
                ArrayList<double[]> peakAcquisitions = new ArrayList<>();

                int assumedBackgrounRowCount = 175 - rawDataFileTemplate.getBlockStartOffset();
                long fractionBackgroundTimeStamp = fractionDateValue.getTime();
                long fractionPeakTimeStamp = fractionDateValue.getTime() + assumedBackgrounRowCount * massSpec.getCOLLECTOR_DATA_FREQUENCY_MILLISECS();

                for (int i = rawDataFileTemplate.getBlockStartOffset(); i < rawDataFileTemplate.getBlockSize(); i++) {
                    String[] fractionCollectorsColumns = fractionFileRows[i].split(",");

                    // Time [Sec]	Al27	Si29	Sr88	Zr96	Hg202	Pb204	Pb206	Pb207	Pb208	Th232	U238
                    // hard coded for now April 2015
                    if (i <= 175) {
                        double[] backgroundIntensities = new double[7];
                        backgroundAcquisitions.add(backgroundIntensities);
                        backgroundIntensities[0] = Double.parseDouble(fractionCollectorsColumns[5]);
                        backgroundIntensities[1] = Double.parseDouble(fractionCollectorsColumns[6]);
                        backgroundIntensities[2] = Double.parseDouble(fractionCollectorsColumns[7]);
                        backgroundIntensities[3] = Double.parseDouble(fractionCollectorsColumns[8]);
                        backgroundIntensities[4] = Double.parseDouble(fractionCollectorsColumns[9]);
                        backgroundIntensities[5] = Double.parseDouble(fractionCollectorsColumns[10]);
                        backgroundIntensities[6] = Double.parseDouble(fractionCollectorsColumns[11]);
                    } else if (i >= 185) {
                        double[] peakIntensities = new double[7];
                        peakAcquisitions.add(peakIntensities);
                        peakIntensities[0] = Double.parseDouble(fractionCollectorsColumns[5]);
                        peakIntensities[1] = Double.parseDouble(fractionCollectorsColumns[6]);
                        peakIntensities[2] = Double.parseDouble(fractionCollectorsColumns[7]);
                        peakIntensities[3] = Double.parseDouble(fractionCollectorsColumns[8]);
                        peakIntensities[4] = Double.parseDouble(fractionCollectorsColumns[9]);
                        peakIntensities[5] = Double.parseDouble(fractionCollectorsColumns[10]);
                        peakIntensities[6] = Double.parseDouble(fractionCollectorsColumns[11]);
                    }
                }  // i loop

                TripoliFraction tripoliFraction
                        = new TripoliFraction( //
                                //
                                fractionID, //
                                massSpec.getCommonLeadCorrectionHighestLevel(), //
                                isStandard, false,
                                fractionBackgroundTimeStamp, //
                                fractionPeakTimeStamp,
                                peakAcquisitions.size());

                SortedSet<DataModelInterface> rawRatios = massSpec.rawRatiosFactoryRevised();
                tripoliFraction.setRawRatios(rawRatios);

                massSpec.setCountOfAcquisitions(peakAcquisitions.size());

                // establish map of virtual collectors to field indexes
                Map<DataModelInterface, Integer> virtualCollectorModelMapToFieldIndexes = new HashMap<>();
                virtualCollectorModelMapToFieldIndexes.put(massSpec.getHg202(), 0);
                virtualCollectorModelMapToFieldIndexes.put(massSpec.getPb204(), 1);
                virtualCollectorModelMapToFieldIndexes.put(massSpec.getPb206(), 2);
                virtualCollectorModelMapToFieldIndexes.put(massSpec.getPb207(), 3);
                virtualCollectorModelMapToFieldIndexes.put(massSpec.getPb208(), 4);
                virtualCollectorModelMapToFieldIndexes.put(massSpec.getTh232(), 5);
                virtualCollectorModelMapToFieldIndexes.put(massSpec.getU238(), 6);

                massSpec.processFractionRawRatiosII(//
                        backgroundAcquisitions, peakAcquisitions, usingFullPropagation, tripoliFraction, virtualCollectorModelMapToFieldIndexes);

                tripoliFraction.shadeDataActiveMapLeft(leftShadeCount);
                System.out.println("\n**** AGILENT FractionID  " + fractionID + "  " + fractionDateValue.toString());

                myTripoliFractions.add(tripoliFraction);

            } catch (ParseException parseException) {
                // TODO: drop out here
            }
        }
//        
//
//        fjPool.invoke(rawFilesTask);
//        do {
//            System.out.printf("******************************************\n");
//            System.out.printf("Main: Parallelism: %d\n", fjPool.getParallelism());
//            System.out.printf("Main: Active Threads: %d\n", fjPool.getActiveThreadCount());
//            System.out.printf("Main: Task Count: %d\n", fjPool.getQueuedTaskCount());
//            System.out.printf("Main: Steal Count: %d\n", fjPool.getStealCount());
//            System.out.printf("******************************************\n");
//            try {
//                TimeUnit.SECONDS.sleep(1);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        } while (!rawFilesTask.isDone());
//        //Shut down ForkJoinPool using the shutdown() method.
//        fjPool.shutdown();
//
//        myTripoliFractions = rawFilesTask.join();
//        System.out.printf("System: %d raw fraction files processed.\n", tripoliFractions.size());

        if (myTripoliFractions.isEmpty()) {
            myTripoliFractions = null;
        }

        return myTripoliFractions;
    }

////    class RawFractionProcessorTask extends RecursiveTask<TripoliFraction> {
////
////        private final File rawFractionFile;
////
////        public RawFractionProcessorTask(File rawFractionFile) {
////            super();
////            this.rawFractionFile = rawFractionFile;
////        }
////
////        @Override
////        protected TripoliFraction compute() {
////            return convertRawFractionFile(rawFractionFile);
////        }
////    }
////
////    /**
////     * http://www.oracle.com/technetwork/articles/java/fork-join-422606.html
////     * http://howtodoinjava.com/2014/05/27/forkjoin-framework-tutorial-forkjoinpool-example/
////     */
////    class RawFractionFolderProcesserTask extends RecursiveTask<SortedSet<TripoliFraction>> {
////
////        private final File[] rawFractionFiles;
////
////        public RawFractionFolderProcesserTask(File[] rawFractionFiles) {
////            super();
////            this.rawFractionFiles = rawFractionFiles;
////        }
////
////        @Override
////        protected SortedSet<TripoliFraction> compute() {
////            SortedSet tripoliFractions = new TreeSet<>();
////            List<RecursiveTask<TripoliFraction>> tasks = new ArrayList<>();
////
////            for (File rawFractionFile : rawFractionFiles) {
////                RawFractionProcessorTask task = new RawFractionProcessorTask(rawFractionFile);
////                tasks.add(task);
////                task.fork();
////            }
////
////            for (RecursiveTask<TripoliFraction> task : tasks) {
////                tripoliFractions.add(task.join());
////            }
////            return tripoliFractions;
////        }
////    }
////
////    public TripoliFraction convertRawFractionFile(File rawFractionFile) {
////
////        TripoliFraction tripoliFraction = null;
////
////        String fractionID = rawFractionFile.getName().toUpperCase().replace(".CSV", "");
////
////        // hard-wired april 2015
////        boolean isStandard = true;
////
////        // get file contents
////        String fractionFileContents = URIHelper.getTextFromURI(rawFractionFile.getAbsolutePath());
////        String[] fractionFileRows = fractionFileContents.split("\n");
////
////        // first get time stamp for file in row 2
////        // form = Acquired      : 04/04/2013 1:22:25 PM using AcqMethod SJ_ZRILC.m
////        String timeStampFromRow2[] = fractionFileRows[2].split(" :")[1].split(" +");
////
////        String fractionDate = //
////                timeStampFromRow2[1] + " " // day/month/year
////                + timeStampFromRow2[2] + " " // hour:min:sec
////                + timeStampFromRow2[3] + " " // AM/PM
////                ;
////
////        // Get the default MEDIUM/SHORT DateFormat
////        DateFormat fractionTimeFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
////
////        // Parse the fractionDateValue
////        Date fractionDateValue;
////        try {
////            fractionDateValue = fractionTimeFormat.parse(fractionDate);
////
////            // each acquisition file contains background followed by peak follwed by background
////            // iinitial soultion is to hard wire the first background and peak
////            // later we will give user interactive tools to pick them out
////            ArrayList<double[]> backgroundAcquisitions = new ArrayList<>();
////            ArrayList<double[]> peakAcquisitions = new ArrayList<>();
////
////            int assumedBackgrounRowCount = 175 - rawDataFileTemplate.getBlockStartOffset();
////            long fractionBackgroundTimeStamp = fractionDateValue.getTime();
////            long fractionPeakTimeStamp = fractionDateValue.getTime() + assumedBackgrounRowCount * massSpec.getCOLLECTOR_DATA_FREQUENCY_MILLISECS();
////
////            for (int i = rawDataFileTemplate.getBlockStartOffset(); i < rawDataFileTemplate.getBlockSize(); i++) {
////                String[] fractionCollectorsColumns = fractionFileRows[i].split(",");
////
////                // Time [Sec]	Al27	Si29	Sr88	Zr96	Hg202	Pb204	Pb206	Pb207	Pb208	Th232	U238
////                // hard coded for now April 2015
////                if (i <= 175) {
////                    double[] backgroundIntensities = new double[7];
////                    backgroundAcquisitions.add(backgroundIntensities);
////                    backgroundIntensities[0] = Double.parseDouble(fractionCollectorsColumns[5]);
////                    backgroundIntensities[1] = Double.parseDouble(fractionCollectorsColumns[6]);
////                    backgroundIntensities[2] = Double.parseDouble(fractionCollectorsColumns[7]);
////                    backgroundIntensities[3] = Double.parseDouble(fractionCollectorsColumns[8]);
////                    backgroundIntensities[4] = Double.parseDouble(fractionCollectorsColumns[9]);
////                    backgroundIntensities[5] = Double.parseDouble(fractionCollectorsColumns[10]);
////                    backgroundIntensities[6] = Double.parseDouble(fractionCollectorsColumns[11]);
////                } else if (i >= 185) {
////                    double[] peakIntensities = new double[7];
////                    peakAcquisitions.add(peakIntensities);
////                    peakIntensities[0] = Double.parseDouble(fractionCollectorsColumns[5]);
////                    peakIntensities[1] = Double.parseDouble(fractionCollectorsColumns[6]);
////                    peakIntensities[2] = Double.parseDouble(fractionCollectorsColumns[7]);
////                    peakIntensities[3] = Double.parseDouble(fractionCollectorsColumns[8]);
////                    peakIntensities[4] = Double.parseDouble(fractionCollectorsColumns[9]);
////                    peakIntensities[5] = Double.parseDouble(fractionCollectorsColumns[10]);
////                    peakIntensities[6] = Double.parseDouble(fractionCollectorsColumns[11]);
////                }
////            }  // i loop
////
////            tripoliFraction = //                           
////                    new TripoliFraction( //
////                            fractionID, //
////                            massSpec.getCommonLeadCorrectionHighestLevel(), //
////                            isStandard,
////                            fractionBackgroundTimeStamp, //
////                            fractionPeakTimeStamp,
////                            peakAcquisitions.size());
////
////            SortedSet<DataModelInterface> rawRatios = ((KoslerAgilent7700SetupUPb) massSpec).rawRatiosFactoryRevised();
////            tripoliFraction.setRawRatios(rawRatios);
////
////            massSpec.setCountOfAcquisitions(peakAcquisitions.size());
////            massSpec.processFractionRawRatiosTRA(backgroundAcquisitions, peakAcquisitions, isStandard, fractionID, true/*usingFullPropagation*/, tripoliFraction);
////
////            //  tripoliFraction.shadeDataActiveMapLeft(leftShadeCount);
////            System.out.println("\n**** AGILENT FractionID  " + fractionID + "  " + fractionDateValue.toString());
////        } catch (ParseException parseException) {
////            // TODO: drop out here
////        }
////
////        return tripoliFraction;
////    }
    private void readObject(
            ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        ObjectStreamClass myObject = ObjectStreamClass.lookup(
                Class.forName(KoslerAgilent7700FileHandler.class.getCanonicalName()));
        long theSUID = myObject.getSerialVersionUID();
        System.out.println("Customized De-serialization of KoslerAgilent7700FileHandler " + theSUID);
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
