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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import org.cirdles.shrimp.PrawnFile;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public class Driver {

    private static File totalIonCountsAtMassFile;
    private static File totalSBMCountsAtMassFile;

    /**
     * Driver to test results
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

            prepReportFiles(nameOfMount, namesOfSpecies, peakMeasurementsCount);

            for (int f = 0; f < prawnFile.getRuns(); f++) {
                PrawnFile.Run runFraction = prawnFile.getRun().get(f);

                //if (runFraction.getPar().get(0).getValue().startsWith("T.1.1.1")) {
                ShrimpFraction shrimpFraction = PrawnRunFractionParser.processRunFraction(runFraction);
                shrimpFraction.setSpotNumber(f + 1);
                reportTotalIonCountsAtMass(shrimpFraction, namesOfSpecies.length);
                reportTotalSBMCountsAtMass(shrimpFraction, namesOfSpecies.length);
                // }
            } // end of fractions loop

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
    public static void reportTotalIonCountsAtMass(ShrimpFraction shrimpFraction, int countOfSpecies) {

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
     *
     * Step “0b” – Total SBM counts at mass As for step “0a” in all respects ,
     * except that in the fifth ‘left-hand’ column, dead_time_ns should be
     * discarded and replaced by SBM_zero_cps = analysis-specific integer read
     * from XML
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
    public static void reportTotalSBMCountsAtMass(ShrimpFraction shrimpFraction, int countOfSpecies) {

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

    public static void prepReportFiles(String nameOfMount, String[] namesOfSpecies, int countOfIntegrations) {
        totalIonCountsAtMassFile = new File("Calamari_TotalIonCountsAtMass_for_" + nameOfMount + ".txt");
        StringBuilder header = new StringBuilder();
        header.append("Title, Date, Scan, Type, Dead_time_ns");

        for (int s = 0; s < namesOfSpecies.length; s++) {
            header.append(", ").append(namesOfSpecies[s]).append(".count_time_sec");
            for (int i = 0; i < countOfIntegrations; i++) {
                header.append(", ").append(namesOfSpecies[s]).append(".").append(String.valueOf(i + 1));
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

        for (int s = 0; s < namesOfSpecies.length; s++) {
            header.append(", ").append(namesOfSpecies[s]).append(".count_time_sec");
            for (int i = 0; i < countOfIntegrations; i++) {
                header.append(", ").append(namesOfSpecies[s]).append(".SBM.").append(String.valueOf(i + 1));
            }
        }
        header.append("\n");

        try {
            Files.write(header, totalSBMCountsAtMassFile, Charsets.UTF_8);
        } catch (IOException iOException) {
        }

    }

    private static String getFormattedDate(long milliseconds) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(milliseconds);
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");

        return dateFormat.format(calendar.getTime());
    }

    public static void reportInterpolatedRatios(ShrimpFraction shrimpFraction) {
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
