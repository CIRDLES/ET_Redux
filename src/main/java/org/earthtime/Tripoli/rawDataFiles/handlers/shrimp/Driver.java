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

            for (int f = 0; f < prawnFile.getRuns(); f++) {
                PrawnFile.Run runFraction = prawnFile.getRun().get(f);

                if (!runFraction.getPar().get(0).getValue().startsWith("T.")) {
                    ShrimpFraction shrimpFraction = PrawnRunFractionParser.processRunFraction(runFraction);
                    shrimpFraction.setSpotNumber(f + 1);
                    reportInterpolatedRatios(shrimpFraction);
                }
            } // end of fractions loop

        } catch (JAXBException | MalformedURLException exception) {
            System.out.println(exception.getMessage());
        }
    }

    public static void reportInterpolatedRatios(ShrimpFraction shrimpFraction) {
        System.out.print("\n" + shrimpFraction.getName() + ", ");
        shrimpFraction.getIsotopicRatios().stream().forEach((isotopeRatioModel) -> {
            System.out.print("Time, '" + isotopeRatioModel.prettyPrintSimpleName() + ", " + "'+/-1sig abs, ");
        });

        System.out.println();

        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(shrimpFraction.getDateTimeMilliseconds());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        System.out.print(dateFormat.format(calendar.getTime()) + ", ");

        shrimpFraction.getIsotopicRatios().stream().forEach((isotopeRatioModel) -> {
            System.out.print(isotopeRatioModel.getRatEqTime().get(0) + ", " + isotopeRatioModel.getRatEqVal().get(0) + ", " + isotopeRatioModel.getRatEqErr().get(0) + ", ");
        });

        System.out.println();

        System.out.print("Spot# " + shrimpFraction.getSpotNumber() + ", ");

        shrimpFraction.getIsotopicRatios().stream().forEach((isotopeRatioModel) -> {
            System.out.print(isotopeRatioModel.getRatEqTime().get(1) == 0.0 ? ", , , " : isotopeRatioModel.getRatEqTime().get(1) + ", " + isotopeRatioModel.getRatEqVal().get(1) + ", " + isotopeRatioModel.getRatEqErr().get(1) + ", ");
        });

        System.out.println();

        System.out.print(", ");

        shrimpFraction.getIsotopicRatios().stream().forEach((isotopeRatioModel) -> {
            System.out.print(isotopeRatioModel.getRatEqTime().get(2) == 0.0 ? ", , , " : isotopeRatioModel.getRatEqTime().get(2) + ", " + isotopeRatioModel.getRatEqVal().get(2) + ", " + isotopeRatioModel.getRatEqErr().get(2) + ", ");
        });

        System.out.println();

        System.out.print(", ");

        shrimpFraction.getIsotopicRatios().stream().forEach((isotopeRatioModel) -> {
            System.out.print(isotopeRatioModel.getRatEqTime().get(3) == 0.0 ? ", , , " : isotopeRatioModel.getRatEqTime().get(3) + ", " + isotopeRatioModel.getRatEqVal().get(3) + ", " + isotopeRatioModel.getRatEqErr().get(3) + ", ");
        });

        System.out.println();

        System.out.print(", ");

        shrimpFraction.getIsotopicRatios().stream().forEach((isotopeRatioModel) -> {
            System.out.print(isotopeRatioModel.getRatEqTime().get(4) == 0.0 ? ", , , " : isotopeRatioModel.getRatEqTime().get(4) + ", " + isotopeRatioModel.getRatEqVal().get(4) + ", " + isotopeRatioModel.getRatEqErr().get(4) + ", ");
        });

        System.out.println();
    }

    public static void reportSummaryRawData(ShrimpFraction shrimpFraction) {
        System.out.println("\n" + shrimpFraction.getName() + "  ***********************\n");

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

        System.out.print(shrimpFraction.getName() + ", ");

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
