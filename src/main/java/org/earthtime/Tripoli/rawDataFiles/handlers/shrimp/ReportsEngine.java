/*
 * Copyright 2006-2016 CIRDLES.org.
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
package org.earthtime.Tripoli.rawDataFiles.handlers.shrimp;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Map;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.cirdles.shrimp.PrawnFile;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public class ReportsEngine {

    private static File totalIonCountsAtMassFile;
    private static File totalSBMCountsAtMassFile;
    private static File totalCountsAtTimeStampAndTrimMass;
    private static File totalCountsPerSecondPerSpeciesPerAnalysis;
    private static File withinSpotRatiosAtInterpolatedTimes;
    private static StringBuilder refMatFractionsTotalCountsPerSecondPerSpeciesPerAnalysis;
    private static StringBuilder unknownFractionsTotalCountsPerSecondPerSpeciesPerAnalysis;
    private static StringBuilder refMatWithinSpotRatiosAtInterpolatedTimes;
    private static StringBuilder unknownWithinSpotRatiosAtInterpolatedTimes;

    /**
     * ReportsEngine to test results
     *
     * @param args
     */
    public static void main(String[] args) {

        // remote copy of example file
        java.net.URL prawnFileURL = null;
        try {
            prawnFileURL = new URL("https://raw.githubusercontent.com/bowring/XSD/master/SHRIMP/EXAMPLE_100142_G6147_10111109.43_10.33.37%20AM.xml");

            JAXBContext jaxbContext = JAXBContext.newInstance(PrawnFile.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            PrawnFile prawnFile = (PrawnFile) jaxbUnmarshaller.unmarshal(prawnFileURL);
            String nameOfMount = prawnFile.getMount();

            // gather general info for all runs  from first fraction
            String[] firstFractionIntegrations = prawnFile.getRun().get(0).getSet().getScan().get(0).getMeasurement().get(0).getData().get(0).getValue().split(",");
            int peakMeasurementsCount = firstFractionIntegrations.length;
            int countOfSpecies = prawnFile.getRun().get(0).getRunTable().getEntries();
            String[] namesOfSpecies = new String[countOfSpecies];
            for (int s = 0; s < countOfSpecies; s++) {
                namesOfSpecies[s] = prawnFile.getRun().get(0).getRunTable().getEntry().get(s).getPar().get(0).getValue();
            }

            prepSpeciesReportFiles(nameOfMount, namesOfSpecies, peakMeasurementsCount);
            ShrimpFraction firstShrimpFraction = PrawnRunFractionParser.processRunFraction(prawnFile.getRun().get(0));
            prepRatiosReportFiles(nameOfMount, firstShrimpFraction);

            for (int f = 0; f < prawnFile.getRuns(); f++) {
                PrawnFile.Run runFraction = prawnFile.getRun().get(f);

//                if (runFraction.getPar().get(0).getValue().startsWith("T.1.1.1")) {
                    ShrimpFraction shrimpFraction = PrawnRunFractionParser.processRunFraction(runFraction);
                    shrimpFraction.setSpotNumber(f + 1);
                    reportTotalIonCountsAtMass(shrimpFraction, namesOfSpecies.length);
                    reportTotalSBMCountsAtMass(shrimpFraction, namesOfSpecies.length);
                    reportTotalCountsAtTimeStampAndTrimMass(shrimpFraction);
                    reportTotalCountsPerSecondPerSpeciesPerAnalysis(shrimpFraction);
                    reportWithinSpotRatiosAtInterpolatedTimes(shrimpFraction);
//                }
            } // end of fractions loop

            finishSpeciesReportFiles();
            finishRatiosReportFiles();

        } catch (JAXBException | MalformedURLException exception) {
            System.out.println(exception.getMessage());
        }
    }

    /**
     * 2016.May.3 email from Simon Bodorkos to Jim Bowring Step “0a” – Total ion
     * counts at mass We’ve touched on this one once before, informally. It is a
     * direct extract from the XML, with one row per scan, and one column per
     * ‘integration-value’. For the demo XML, the array will have 684 rows of
     * data (114 analyses x 6 scans), and 115 columns (5 for row identifiers,
     * then for each of the 10 measured species, 11 columns comprising
     * count_time_sec and the integer values of the 10 integrations).
     *
     * It needs five ‘left-hand’ columns to allow the rows to be identified and
     * sorted: Title = analysis-specific text-string read from XML Date =
     * analysis-specific date read from XML, to be expressed as YYYY-MM-DD
     * HH24:MI:SS Scan = integer, starting at 1 within each analysis Type =
     * “standard” or “unknown”; analyses with prefix “T.” to be labelled
     * “standard”, all others “unknown” Dead_time_ns = analysis-specific integer
     * read from XML
     *
     * These are to be followed by 11 columns for each species (i.e. 110 columns
     * for the demo XML): [entry-label].count_time_sec = analysis-specific
     * integer read from XML [entry-label].1 = integer value corresponding to
     * the first of 10 ‘integrations’ within tags “<data name = [entry-label]>
     * </data>” for the specified combination of analysis, scan and species
     * [entry-label].2 = integer value corresponding to the second of 10
     * ‘integrations’ within tags “<data name = [entry-label]> </data>” for the
     * specified combination of analysis, scan and species … [entry-label].10 =
     * integer value corresponding to the tenth of 10 ‘integrations’ within tags
     * “<data name = [entry-label]> </data>” for the specified combination of
     * analysis, scan and species
     *
     * Sorting: Primary criterion = Date, secondary criterion = Scan
     *
     * @param shrimpFraction
     * @param countOfSpecies
     */
    private static void reportTotalIonCountsAtMass(ShrimpFraction shrimpFraction, int countOfSpecies) {

        for (int scanNum = 0; scanNum < shrimpFraction.getRawPeakData().length; scanNum++) {
            StringBuilder dataLine = new StringBuilder();
            dataLine.append(shrimpFraction.getFractionID()).append(", ");
            dataLine.append(getFormattedDate(shrimpFraction.getDateTimeMilliseconds())).append(", ");
            dataLine.append(String.valueOf(scanNum + 1)).append(", ");
            dataLine.append(shrimpFraction.isReferenceMaterial() ? "ref mat" : "unknown").append(", ");
            dataLine.append(String.valueOf(shrimpFraction.getDeadTimeNanoseconds()));

            for (int i = 0; i < shrimpFraction.getRawPeakData()[scanNum].length; i++) {
                if ((i % countOfSpecies) == 0) {
                    dataLine.append(", ").append(String.valueOf(shrimpFraction.getCountTimeSec()[i / countOfSpecies]));
                }
                dataLine.append(", ").append(shrimpFraction.getRawPeakData()[scanNum][i]);
            }

            try {
                Files.append(dataLine + "\n", totalIonCountsAtMassFile, Charsets.UTF_8);
            } catch (IOException iOException) {
            }
        }
    }

    /**
     * 2016.May.3 email from Simon Bodorkos to Jim Bowring Step “0b” – Total SBM
     * counts at mass As for step “0a” in all respects , except that in the
     * fifth ‘left-hand’ column, dead_time_ns should be discarded and replaced
     * by SBM_zero_cps = analysis-specific integer read from XML
     *
     * And the 11 columns for each species are: [entry-label].count_time_sec =
     * analysis-specific integer read from XML [entry-label].SBM.1 = integer
     * value corresponding to the first of 10 ‘integrations’ within tags “<data name = SBM
     * > </data>” for the specified combination of analysis, scan and species
     * [entry-label].SBM.2 = integer value corresponding to the second of 10
     * ‘integrations’ within tags “<data name = SBM > </data>” for the specified
     * combination of analysis, scan and species … [entry-label].SBM.10 =
     * integer value corresponding to the tenth of 10 ‘integrations’ within tags
     * “<data name = SBM> </data>” for the specified combination of analysis,
     * scan and species
     *
     * Sorting: Primary criterion = Date (ascending), secondary criterion = Scan
     * (ascending)
     *
     * @param shrimpFraction
     * @param countOfSpecies
     */
    private static void reportTotalSBMCountsAtMass(ShrimpFraction shrimpFraction, int countOfSpecies) {

        for (int scanNum = 0; scanNum < shrimpFraction.getRawSBMData().length; scanNum++) {
            StringBuilder dataLine = new StringBuilder();
            dataLine.append(shrimpFraction.getFractionID()).append(", ");
            dataLine.append(getFormattedDate(shrimpFraction.getDateTimeMilliseconds())).append(", ");
            dataLine.append(String.valueOf(scanNum + 1)).append(", ");
            dataLine.append(shrimpFraction.isReferenceMaterial() ? "ref mat" : "unknown").append(", ");
            dataLine.append(String.valueOf(shrimpFraction.getSbmZeroCps()));

            for (int i = 0; i < shrimpFraction.getRawSBMData()[scanNum].length; i++) {
                if ((i % countOfSpecies) == 0) {
                    dataLine.append(", ").append(String.valueOf(shrimpFraction.getCountTimeSec()[i / countOfSpecies]));
                }
                dataLine.append(", ").append(shrimpFraction.getRawSBMData()[scanNum][i]);
            }

            try {
                Files.append(dataLine + "\n", totalSBMCountsAtMassFile, Charsets.UTF_8);
            } catch (IOException iOException) {
            }
        }
    }

    /**
     * 2016.May.3 email from Simon Bodorkos to Jim Bowring Step 1 – Total counts
     * at time-stamp and trim-mass This is intended to replicate the current
     * Step 1 sanity-check, with one row per scan, and one column per key
     * attribute of “total counts at peak”. For the demo XML, the array will
     * have 684 rows of data (114 analyses x 6 scans), and 54 columns (4 for row
     * identifiers, then for each of the 10 measured species, 5 columns as
     * specified below).
     *
     * It needs four ‘left-hand’ columns to allow the rows to be identified and
     * sorted: Title = analysis-specific text-string read from XML Date =
     * analysis-specific date read from XML, to be expressed as YYYY-MM-DD
     * HH24:MI:SS Scan = integer, starting at 1 within each analysis Type =
     * “standard” or “unknown”; analyses with prefix “T.” to be labelled
     * “standard”, all others “unknown”
     *
     * These are to be followed by 5 columns for each species (i.e. 50 columns
     * for the demo XML): [entry-label].Time = integer “time_stamp_sec” read
     * from XML for the specified combination of analysis, scan and species
     * [entry-label].TotalCounts = calculated decimal value for “total counts at
     * mass” from Step 1, for the specified combination of analysis, scan and
     * species [entry-label].1SigmaAbs = calculated decimal value for “+/-1sigma
     * at mass” from Step 1, for the specified combination of analysis, scan and
     * species [entry-label].TotalSBM = calculated decimal value for “total SBM
     * counts” from Step 1, for the specified combination of analysis, scan and
     * species [entry-label].TrimMass = decimal “trim_mass_amu” read from XML
     * for the specified combination of analysis, scan and species
     *
     * Sorting: Primary criterion = Date (ascending), secondary criterion = Scan
     * (ascending)
     *
     * @param shrimpFraction
     */
    private static void reportTotalCountsAtTimeStampAndTrimMass(ShrimpFraction shrimpFraction) {

        for (int scanNum = 0; scanNum < shrimpFraction.getTimeStampSec().length; scanNum++) {
            StringBuilder dataLine = new StringBuilder();
            dataLine.append(shrimpFraction.getFractionID()).append(", ");
            dataLine.append(getFormattedDate(shrimpFraction.getDateTimeMilliseconds())).append(", ");
            dataLine.append(String.valueOf(scanNum + 1)).append(", ");
            dataLine.append(shrimpFraction.isReferenceMaterial() ? "ref mat" : "unknown");

            for (int i = 0; i < shrimpFraction.getTimeStampSec()[scanNum].length; i++) {
                dataLine.append(", ").append(shrimpFraction.getTimeStampSec()[scanNum][i]);
                dataLine.append(", ").append(shrimpFraction.getTotalCounts()[scanNum][i]);
                dataLine.append(", ").append(shrimpFraction.getTotalCountsOneSigmaAbs()[scanNum][i]);
                dataLine.append(", ").append(shrimpFraction.getTotalCountsSBM()[scanNum][i]);
                dataLine.append(", ").append(shrimpFraction.getTrimMass()[scanNum][i]);
            }

            try {
                Files.append(dataLine + "\n", totalCountsAtTimeStampAndTrimMass, Charsets.UTF_8);
            } catch (IOException iOException) {
            }
        }
    }

    /**
     * 2016.May.3 email from Simon Bodorkos to Jim Bowring Step 2 – Total
     * counts-per-second, per species, per analysis This is intended to
     * replicate the current Step 2 sanity-check, with one row per *analysis*,
     * and one column per species. For the demo XML, the array will have 114
     * rows of data (one per analysis), and 13 columns (3 for row identifiers,
     * then one for each of the 10 measured species).
     *
     * It needs three ‘left-hand’ columns to allow the rows to be identified and
     * sorted: Title = analysis-specific text-string read from XML Date =
     * analysis-specific date read from XML, to be expressed as YYYY-MM-DD
     * HH24:MI:SS Type = “standard” or “unknown”; analyses with prefix “T.” to
     * be labelled “standard”, all others “unknown”
     *
     * These are to be followed by 1 column for each species (i.e. 10 columns
     * for the demo XML): [entry-label].TotalCps = calculated decimal value for
     * “total counts per second” from Step 2, for the specified combination of
     * analysis and species
     *
     * Sorting: Primary criterion = Type (ascending; “standard” before unknown,
     * so alphabetical would do), secondary criterion = Date (ascending)
     *
     * @param shrimpFraction the value of shrimpFraction
     */
    private static void reportTotalCountsPerSecondPerSpeciesPerAnalysis(ShrimpFraction shrimpFraction) {

        // need to sort by reference material vs unknown
        StringBuilder dataLine = new StringBuilder();
        dataLine.append(shrimpFraction.getFractionID()).append(", ");
        dataLine.append(getFormattedDate(shrimpFraction.getDateTimeMilliseconds())).append(", ");
        dataLine.append(shrimpFraction.isReferenceMaterial() ? "ref mat" : "unknown");

        for (int i = 0; i < shrimpFraction.getTotalCps().length; i++) {
            dataLine.append(", ").append(shrimpFraction.getTotalCps()[i]);
        }

        dataLine.append("\n");
        if (shrimpFraction.isReferenceMaterial()) {
            refMatFractionsTotalCountsPerSecondPerSpeciesPerAnalysis.append(dataLine);
        } else {
            unknownFractionsTotalCountsPerSecondPerSpeciesPerAnalysis.append(dataLine);
        }

    }

    private static void reportWithinSpotRatiosAtInterpolatedTimes(ShrimpFraction shrimpFraction) {

        int nDodCount = shrimpFraction.getIsotopicRatios().entrySet().iterator().next().getValue().getRatEqTime().size();

        for (int nDodNum = 0; nDodNum < nDodCount; nDodNum++) {
            // need to sort by reference material vs unknown
            StringBuilder dataLine = new StringBuilder();
            dataLine.append(shrimpFraction.getFractionID()).append(", ");
            dataLine.append(getFormattedDate(shrimpFraction.getDateTimeMilliseconds())).append(", ");
            dataLine.append(String.valueOf(nDodNum)).append(", ");
            dataLine.append(shrimpFraction.isReferenceMaterial() ? "ref mat" : "unknown");

            for (Map.Entry<RawRatioNamesSHRIMP, IsotopeRatioModelSHRIMP> entry : shrimpFraction.getIsotopicRatios().entrySet()) {
                dataLine.append(", ").append(String.valueOf(entry.getValue().getRatEqTime().get(nDodNum)));
                dataLine.append(", ").append(String.valueOf(entry.getValue().getRatEqVal().get(nDodNum)));
                dataLine.append(", ").append(String.valueOf(entry.getValue().getRatEqErr().get(nDodNum)));
            }

            dataLine.append("\n");
            if (shrimpFraction.isReferenceMaterial()) {
                refMatWithinSpotRatiosAtInterpolatedTimes.append(dataLine);
            } else {
                unknownWithinSpotRatiosAtInterpolatedTimes.append(dataLine);
            }
        }
    }

    private static void prepSpeciesReportFiles(String nameOfMount, String[] namesOfSpecies, int countOfIntegrations) {
        totalIonCountsAtMassFile = new File("Calamari_TotalIonCountsAtMass_for_" + nameOfMount + ".txt");
        StringBuilder header = new StringBuilder();
        header.append("Title, Date, Scan, Type, Dead_time_ns");

        for (String nameOfSpecies : namesOfSpecies) {
            header.append(", ").append(nameOfSpecies).append(".count_time_sec");
            for (int i = 0; i < countOfIntegrations; i++) {
                header.append(", ").append(nameOfSpecies).append(".").append(String.valueOf(i + 1));
            }
        }
        header.append("\n");

        try {
            Files.write(header, totalIonCountsAtMassFile, Charsets.UTF_8);
        } catch (IOException iOException) {
        }

        totalSBMCountsAtMassFile = new File("Calamari_TotalSBMCountsAtMass_for_" + nameOfMount + ".txt");
        header = new StringBuilder();
        header.append("Title, Date, Scan, Type, SBM_zero_cps");

        for (String nameOfSpecies : namesOfSpecies) {
            header.append(", ").append(nameOfSpecies).append(".count_time_sec");
            for (int i = 0; i < countOfIntegrations; i++) {
                header.append(", ").append(nameOfSpecies).append(".SBM.").append(String.valueOf(i + 1));
            }
        }
        header.append("\n");

        try {
            Files.write(header, totalSBMCountsAtMassFile, Charsets.UTF_8);
        } catch (IOException iOException) {
        }

        totalCountsAtTimeStampAndTrimMass = new File("Calamari_TotalCountsAtTimeStampAndTrimMass_for_" + nameOfMount + ".txt");
        header = new StringBuilder();
        header.append("Title, Date, Scan, Type");

        for (String nameOfSpecies : namesOfSpecies) {
            header.append(", ").append(nameOfSpecies).append(".Time");
            header.append(", ").append(nameOfSpecies).append(".TotalCounts");
            header.append(", ").append(nameOfSpecies).append(".1SigmaAbs");
            header.append(", ").append(nameOfSpecies).append(".TotalSBM");
            header.append(", ").append(nameOfSpecies).append(".TrimMass");
        }
        header.append("\n");

        try {
            Files.write(header, totalCountsAtTimeStampAndTrimMass, Charsets.UTF_8);
        } catch (IOException iOException) {
        }

        totalCountsPerSecondPerSpeciesPerAnalysis = new File("Calamari_TotalCountsPerSecondPerSpeciesPerAnalysis_for_" + nameOfMount + ".txt");
        header = new StringBuilder();
        header.append("Title, Date, Type");

        for (String nameOfSpecies : namesOfSpecies) {
            header.append(", ").append(nameOfSpecies).append(".TotalCps");
        }
        header.append("\n");

        try {
            Files.write(header, totalCountsPerSecondPerSpeciesPerAnalysis, Charsets.UTF_8);
        } catch (IOException iOException) {
        }
        refMatFractionsTotalCountsPerSecondPerSpeciesPerAnalysis = new StringBuilder();
        unknownFractionsTotalCountsPerSecondPerSpeciesPerAnalysis = new StringBuilder();
    }

    private static void prepRatiosReportFiles(String nameOfMount, ShrimpFraction shrimpFraction) {
        withinSpotRatiosAtInterpolatedTimes = new File("Calamari_WithinSpotRatiosAtInterpolatedTimes_for_" + nameOfMount + ".txt");
        StringBuilder header = new StringBuilder();
        header.append("Title, Date, Ndod, Type");

        for (Map.Entry<RawRatioNamesSHRIMP, IsotopeRatioModelSHRIMP> entry : shrimpFraction.getIsotopicRatios().entrySet()) {
            header.append(", ").append(entry.getKey().getDisplayName().replaceAll(" ", "")).append(".InterpTIme");
            header.append(", ").append(entry.getKey().getDisplayName().replaceAll(" ", "")).append(".Value");
            header.append(", ").append(entry.getKey().getDisplayName().replaceAll(" ", "")).append(".1SigmaAbs");
        }

        header.append("\n");

        try {
            Files.write(header, withinSpotRatiosAtInterpolatedTimes, Charsets.UTF_8);
        } catch (IOException iOException) {
        }
        refMatWithinSpotRatiosAtInterpolatedTimes = new StringBuilder();
        unknownWithinSpotRatiosAtInterpolatedTimes = new StringBuilder();
    }

    private static void finishSpeciesReportFiles() {
        try {
            Files.append(refMatFractionsTotalCountsPerSecondPerSpeciesPerAnalysis, totalCountsPerSecondPerSpeciesPerAnalysis, Charsets.UTF_8);
            Files.append(unknownFractionsTotalCountsPerSecondPerSpeciesPerAnalysis, totalCountsPerSecondPerSpeciesPerAnalysis, Charsets.UTF_8);
        } catch (IOException iOException) {
        }
    }

    private static void finishRatiosReportFiles() {
        try {
            Files.append(refMatWithinSpotRatiosAtInterpolatedTimes, withinSpotRatiosAtInterpolatedTimes, Charsets.UTF_8);
            Files.append(unknownWithinSpotRatiosAtInterpolatedTimes, withinSpotRatiosAtInterpolatedTimes, Charsets.UTF_8);
        } catch (IOException iOException) {
        }
    }

    private static String getFormattedDate(long milliseconds) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(milliseconds);
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");

        return dateFormat.format(calendar.getTime());
    }

    private static void reportInterpolatedRatios(ShrimpFraction shrimpFraction) {
        System.out.print("\n" + shrimpFraction.getFractionID() + ", ");
        shrimpFraction.getIsotopicRatios().forEach((rawRatioName, isotopeRatioModel) -> {
            System.out.print("Time, '" + isotopeRatioModel.prettyPrintSimpleName() + ", " + "'+/-1sig abs, ");
        });

        System.out.println();

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(shrimpFraction.getDateTimeMilliseconds());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        System.out.print(dateFormat.format(calendar.getTime()) + ", ");

        shrimpFraction.getIsotopicRatios().forEach((rawRatioName, isotopeRatioModel) -> {
            System.out.print(isotopeRatioModel.getRatEqTime().get(0) + ", " + isotopeRatioModel.getRatEqVal().get(0) + ", " + isotopeRatioModel.getRatEqErr().get(0) + ", ");
        });

        System.out.println();

        System.out.print("Spot# " + shrimpFraction.getSpotNumber() + ", ");

        shrimpFraction.getIsotopicRatios().forEach((rawRatioName, isotopeRatioModel) -> {
            System.out.print(isotopeRatioModel.getRatEqTime().get(1) == 0.0 ? ", , , " : isotopeRatioModel.getRatEqTime().get(1) + ", " + isotopeRatioModel.getRatEqVal().get(1) + ", " + isotopeRatioModel.getRatEqErr().get(1) + ", ");
        });

        System.out.println();

        System.out.print(", ");

        shrimpFraction.getIsotopicRatios().forEach((rawRatioName, isotopeRatioModel) -> {
            System.out.print(isotopeRatioModel.getRatEqTime().get(2) == 0.0 ? ", , , " : isotopeRatioModel.getRatEqTime().get(2) + ", " + isotopeRatioModel.getRatEqVal().get(2) + ", " + isotopeRatioModel.getRatEqErr().get(2) + ", ");
        });

        System.out.println();

        System.out.print(", ");

        shrimpFraction.getIsotopicRatios().forEach((rawRatioName, isotopeRatioModel) -> {
            System.out.print(isotopeRatioModel.getRatEqTime().get(3) == 0.0 ? ", , , " : isotopeRatioModel.getRatEqTime().get(3) + ", " + isotopeRatioModel.getRatEqVal().get(3) + ", " + isotopeRatioModel.getRatEqErr().get(3) + ", ");
        });

        System.out.println();

        System.out.print(", ");

        shrimpFraction.getIsotopicRatios().forEach((rawRatioName, isotopeRatioModel) -> {
            System.out.print(isotopeRatioModel.getRatEqTime().get(4) == 0.0 ? ", , , " : isotopeRatioModel.getRatEqTime().get(4) + ", " + isotopeRatioModel.getRatEqVal().get(4) + ", " + isotopeRatioModel.getRatEqErr().get(4) + ", ");
        });

        System.out.println();
    }

    public static void reportSummaryRawData(ShrimpFraction shrimpFraction) {
        System.out.println("\n" + shrimpFraction.getFractionID() + "  ***********************\n");

        for (double[] scannedData : shrimpFraction.getExtractedRunData()) {
            for (int j = 0; j < scannedData.length; j++) {
                System.out.print(scannedData[j]);
                if (j < (scannedData.length - 1)) {
                    System.out.print(",");
                }
            }
            System.out.print("\n");
        }

        System.out.println();
    }

    public static void reportTotalCps(ShrimpFraction shrimpFraction) {

        System.out.print(shrimpFraction.getFractionID() + ", ");

        double[] totalCps = shrimpFraction.getTotalCps();
        for (int j = 0; j < totalCps.length; j++) {
            System.out.print(totalCps[j]);
            if (j < (totalCps.length - 1)) {
                System.out.print(",");
            }
        }

        System.out.println();
    }

    public static void reportForSimon() {
        java.net.URL prawnFileURL = null;
        try {
            prawnFileURL = new URL("https://raw.githubusercontent.com/bowring/XSD/master/SHRIMP/EXAMPLE_100142_G6147_10111109.43_10.33.37%20AM.xml");
        } catch (MalformedURLException malformedURLException) {
            System.out.println(malformedURLException.getMessage());

        }

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(PrawnFile.class
            );
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            PrawnFile prawnFile = (PrawnFile) jaxbUnmarshaller.unmarshal(prawnFileURL);

            // print headers
            System.out.print("Spot, Scan#, Time, ");//196,  204, BKGRND, 206, 207 208 238 248 254 270");
            PrawnFile.Run firstFraction = prawnFile.getRun().get(0);
            for (int i = 0; i < 10; i++) {
                String speciesName = firstFraction.getRunTable().getEntry().get(i).getPar().get(0).getValue();
                for (int j = 0; j < 10; j++) {
                    System.out.print(speciesName + "." + (j + 1) + ", ");
                }
            }
            System.out.println();

            for (int f = 0; f < prawnFile.getRuns(); f++) {
                PrawnFile.Run runFraction = prawnFile.getRun().get(f);
                PrawnFile.Run.Set mySet = runFraction.getSet();
                for (int scan = 0; scan < 6; scan++) {
                    PrawnFile.Run.Set.Scan myScan = mySet.getScan().get(scan);
                    System.out.print(runFraction.getPar().get(0).getValue() //
                            + ", " + (scan + 1) + ", " //
                            + mySet.getPar().get(0).getValue() + " " + mySet.getPar().get(1).getValue() + ", ");
                    for (int species = 0; species < 10; species++) {
                        PrawnFile.Run.Set.Scan.Measurement mySpecies = myScan.getMeasurement().get(species);
                        System.out.print(mySpecies.getData().get(0).getValue() + ", ");
                    }

                    System.out.println();
                }

            } // end of fractions loop

            System.out.println();

        } catch (JAXBException jAXBException) {
            System.out.println(jAXBException.getMessage());
        }
    }

}
