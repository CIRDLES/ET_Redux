/*
 * WashStateElementIISingleCollFileHandler
 *
 * Copyright 2006-2015 James F. Bowring and www.Earth-Time.org
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
package org.earthtime.Tripoli.rawDataFiles.handlers;

import java.io.File;
import java.io.Serializable;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import org.earthtime.Tripoli.fractions.TripoliFraction;
import org.earthtime.utilities.FileHelper;
import org.earthtime.archivingTools.URIHelper;

/**
 *
 * @author James F. Bowring
 */
public class WashStateElementIISingleCollFileHandler extends AbstractRawDataFileHandler implements //
        Comparable<AbstractRawDataFileHandler>,
        Serializable {

    private static WashStateElementIISingleCollFileHandler instance = null;
    private File[] analysisFiles;

    /**
     *
     * @param massSpec
     * @param rawDataFileTemplate
     */
    private WashStateElementIISingleCollFileHandler() {

        super();

        NAME = "Thermo Finnigan Element II SC File";

        aboutInfo = "Details: This is the default protocol for Washington State University's Thermo Finnigan Element II.";
    }

    /**
     *
     * @return
     */
    public static WashStateElementIISingleCollFileHandler getInstance() {
        if (instance == null) {
            instance = new WashStateElementIISingleCollFileHandler();//massSpec, rawDataFileTemplate );
        }
        return instance;
    }

    /**
     *
     * @param tripoliRawDataFolder
     * @return
     */
    @Override
    public File validateAndGetHeaderDataFromRawIntensityFile(File tripoliRawDataFolder) {
        String dialogTitle = "Select an Element II Raw Data Folder:";

        rawDataFile = FileHelper.AllPlatformGetFolder(dialogTitle, tripoliRawDataFolder);

        return rawDataFile;
    }

    /**
     *
     * @param loadDataTask the value of loadRawDataPropertyChangeListener
     * @param usingFullPropagation the value of usingFullPropagation
     * @param leftShadeCount the value of leftShadeCount
     * @param ignoreFirstFractions the value of ignoreFirstFractions
     * @return
     */
    @Override
    public File getAndLoadRawIntensityDataFile(SwingWorker loadDataTask, boolean usingFullPropagation, int leftShadeCount, int ignoreFirstFractions) {

        // get .txt files from the folder and check the first one
        analysisFiles = rawDataFile.listFiles((File dir, String name) //
                -> (name.toLowerCase().endsWith(".txt"))//
                && //
                (!name.toLowerCase().endsWith("_b.txt")));

        if (analysisFiles.length > 0) {
            String onPeakFileContents = URIHelper.getTextFromURI(analysisFiles[0].getAbsolutePath());
            if (isValidRawDataFileType(analysisFiles[0]) //
                    && //
                    areKeyWordsPresent(onPeakFileContents)) {
                // create fractions from raw data and perform corrections and calculate ratios
                loadRawDataFile(loadDataTask, usingFullPropagation, leftShadeCount, 0);
            }
        } else {
            JOptionPane.showMessageDialog(
                    null,
                    new String[]{"Selected raw data folder does not contain valid files."},
                    "ET Redux Warning",
                    JOptionPane.WARNING_MESSAGE);

            rawDataFile = null;
        }

        return rawDataFile;
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
     * @return
     */
    @Override
    protected SortedSet<TripoliFraction> loadRawDataFile(SwingWorker loadDataTask, boolean usingFullPropagation, int leftShadeCount, int ignoreFirstFractions) {
        tripoliFractions = new TreeSet<>();

        // assume we are golden        
        for (int f = 0; f < analysisFiles.length; f++) {

            if (loadDataTask.isCancelled()) {
                break;
            }
            loadDataTask.firePropertyChange("progress", 0, ((100 * f) / analysisFiles.length));

            String fractionID = analysisFiles[f].getName().toUpperCase().replace(".TXT", "");
            long fractionPeakTimeStamp = analysisFiles[f].lastModified();

            String onPeakFileContents = URIHelper.getTextFromURI(analysisFiles[f].getAbsolutePath());
            String[] onPeakFileRows = onPeakFileContents.split("\n");

            // check for background file
            File backgroundFile = new File(analysisFiles[f].getAbsolutePath().replace(".TXT", "_b.TXT"));
            System.out.println("Background exists = " + backgroundFile.exists());
            if (backgroundFile.exists()) {
                String backgroundFileContents = URIHelper.getTextFromURI(backgroundFile.getAbsolutePath());
                String[] backgroundFileRows = backgroundFileContents.split("\n");
                long fractionBackgroundTimeStamp = backgroundFile.lastModified();

                // note each row has relative time stamp which we are hiding for now by using frequency
                int expectedRowsOfData = rawDataFileTemplate.getBlockSize();
                String[][] scanData = //
                        new String[expectedRowsOfData][massSpec.getVIRTUAL_COLLECTOR_COUNT()];

                if (f == 137) {
                    System.out.println();
                }
                System.out.println("Fract # " + f + "   named  " + analysisFiles[f].getName() + "  row count = " + onPeakFileRows.length);
                //TODO possible missing condition here if file lengths vary from template spec and onPeakFileRows is too big
                for (int i = 0; i < rawDataFileTemplate.getBlockSize(); i++) {

                    String[] onPeakCollectorsColumns = new String[]{"0", "0", "0", "0", "0", "0", "0", "0", "0", "0",};
                    String[] backgroundCollectorsColumns = new String[]{"0", "0", "0", "0", "0", "0", "0", "0", "0", "0",};
                    // handle case where there is not as many lines of data as expected
                    if (onPeakFileRows.length > (i + rawDataFileTemplate.getBlockStartOffset())) {
                        onPeakCollectorsColumns = //
                                onPeakFileRows[i + rawDataFileTemplate.getBlockStartOffset()].split("\t");
                    }

                    // handle case where there is not as many lines of data as expected
                    if (backgroundFileRows.length > (i + rawDataFileTemplate.getBlockStartOffset())) {
                        backgroundCollectorsColumns =//
                                backgroundFileRows[i + rawDataFileTemplate.getBlockStartOffset()].split("\t");
                    }

                    // background
                    for (int j = 1; j < 9; j++) {
                        scanData[i][j - 1] = backgroundCollectorsColumns[j].trim(); // ignore timestamp
                    }
                    // onpeak
                    for (int j = 1; j < 9; j++) {
                        scanData[i][8 + j - 1] = onPeakCollectorsColumns[j].trim(); // ignore timestamp
                    }
                }

                // extract isStandard
                boolean isStandard = isStandardFractionID(fractionID);

                TripoliFraction tripoliFraction = //                           
                        new TripoliFraction( //
                                fractionID, //
                                massSpec.getCommonLeadCorrectionHighestLevel(), //
                                isStandard,
                                fractionBackgroundTimeStamp, //
                                fractionPeakTimeStamp, massSpec.rawRatiosFactory(scanData, isStandard, fractionID, usingFullPropagation, null));

                tripoliFraction.shadeDataActiveMapLeft(leftShadeCount);
                tripoliFractions.add(tripoliFraction);
//                System.out.println( "Successfully added frac " + fractionID + " >>  " + tripoliFractions.add( tripoliFraction ) );
//
//                System.out.println( "                                       Count of stored fractions = " + tripoliFractions.size() );
//                System.out.println( fractionID //
//                        + " " + isStandard + " time = " + TimeToString.secondsAsLongToTimeString( fractionPeakTimeStamp ) );

            }
        }

        if (tripoliFractions.isEmpty()) {
            tripoliFractions = null;
        }

        return tripoliFractions;
    }
}
