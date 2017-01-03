/*
 * SampleImporterFromSHRIMPLegacyCSVFile_SIRCOMBE.java
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
package org.earthtime.UPb_Redux.samples.sampleImporters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Scanner;
import java.util.Vector;
import org.earthtime.UPb_Redux.fractions.FractionI;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbLegacyFraction;
import org.earthtime.UPb_Redux.utilities.BrowserControl;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.PercentDiscordance;
import org.earthtime.dataDictionaries.AnalysisMeasures;
import org.earthtime.dataDictionaries.RadDates;
import org.earthtime.dataDictionaries.TemplatesForCsvImport;
import org.earthtime.fractions.ETFractionInterface;


/**
 *
 * @author James F. Bowring
 */
public class SampleImporterFromSHRIMPLegacyCSVFile_SIRCOMBE extends AbstractSampleImporterFromLegacyCSVFile {

    /**
     * 
     * @param file
     * @return
     * @throws FileNotFoundException
     */
    @Override
    protected Vector<ETFractionInterface> extractFractionsFromFile(File file)
            throws FileNotFoundException {
        Vector<ETFractionInterface> retFractions = new Vector<>();
        boolean readingFractions = false;

        //first use a Scanner to get each line
        Scanner scanner = new Scanner(file);
        try {
            while (scanner.hasNextLine()) {
                // get content of line
                Vector<String> myFractionData = processLegacyCSVLine(scanner.nextLine());

                // determine content of line where a zero for fraction name = blank line

                if ( readingFractions && !myFractionData.get( 0).equalsIgnoreCase( "0")) {
                    // process fraction line
                    System.out.println("Reading Fraction " + myFractionData.get(0));

                    FractionI myFraction = new UPbLegacyFraction("NONE");

                    myFraction.setRatioType("UPb");

                    int index = 0;

                    myFraction.setFractionID(myFractionData.get(index++));
                    myFraction.setGrainID(myFraction.getFractionID());

                    String ratioName = "r206_238r";
                    myFraction.getRadiogenicIsotopeRatioByName(ratioName)//
                            .setValue(readCSVCell(myFractionData.get(index++)));
                    ValueModel ratio = myFraction.getRadiogenicIsotopeRatioByName(ratioName);

                    // convert 2-sigma to 1-sigma
                    BigDecimal oneSigmaPct = readCSVCell(myFractionData.get(index++)).//
                            divide(new BigDecimal(2.0));
                    myFraction.getRadiogenicIsotopeRatioByName(ratioName)//
                            .setOneSigma(ValueModel.convertOneSigmaPctToAbsIfRequired(ratio, oneSigmaPct));

                    ratioName = "r207_235r";
                    myFraction.getRadiogenicIsotopeRatioByName(ratioName)//
                            .setValue(readCSVCell(myFractionData.get(index++)));
                    ratio = myFraction.getRadiogenicIsotopeRatioByName(ratioName);

                    // convert 2-sigma to 1-sigma
                    oneSigmaPct = readCSVCell(myFractionData.get(index++)).//
                            divide(new BigDecimal(2.0));
                    myFraction.getRadiogenicIsotopeRatioByName(ratioName)//
                            .setOneSigma(ValueModel.convertOneSigmaPctToAbsIfRequired(ratio, oneSigmaPct));

                    ratioName = "r207_206r";
                    myFraction.getRadiogenicIsotopeRatioByName(ratioName)//
                            .setValue(readCSVCell(myFractionData.get(index++)));
                    ratio = myFraction.getRadiogenicIsotopeRatioByName(ratioName);

                    // convert 2-sigma to 1-sigma
                    oneSigmaPct = readCSVCell(myFractionData.get(index++)).//
                            divide(new BigDecimal(2.0));
                    myFraction.getRadiogenicIsotopeRatioByName(ratioName)//
                            .setOneSigma(ValueModel.convertOneSigmaPctToAbsIfRequired(ratio, oneSigmaPct));

                    ratioName = "rhoR206_238r__r207_235r";
                    myFraction.getRadiogenicIsotopeRatioByName(ratioName)//
                            .setValue(readCSVCell(myFractionData.get(index++)));

                    ((UPbLegacyFraction)myFraction).calculateTeraWasserburgRho();

                    ratioName = "r206_204tfc";
                    myFraction.getSampleIsochronRatiosByName(ratioName)//
                            .setValue(readCSVCell(myFractionData.get(index++)));

                    ratioName = "r208_206r";
                    myFraction.getRadiogenicIsotopeRatioByName(ratioName)//
                            .setValue(readCSVCell(myFractionData.get(index++)));

                    ratioName = "radToCommonTotal"; // Pb*/Pb
                    myFraction.getCompositionalMeasureByName(ratioName)//
                            .setValue(readCSVCell(myFractionData.get(index++)));

                    ratioName = "totRadiogenicPbMass"; //Pb*
                    myFraction.getCompositionalMeasureByName(ratioName)//
                            .setValue(readCSVCell(myFractionData.get(index++)).//
                            movePointLeft(12));

                    ratioName = "totCommonPbMass"; //Pbc
                    myFraction.getCompositionalMeasureByName(ratioName)//
                            .setValue(readCSVCell(myFractionData.get(index++)).//
                            movePointLeft(12));

                    ratioName = "concU";
                    myFraction.getCompositionalMeasureByName(ratioName)//
                            .setValue(readCSVCell(myFractionData.get(index++)).//
                            movePointLeft(6));

                    ratioName = "concPb_ib";
                    myFraction.getCompositionalMeasureByName(ratioName)//
                            .setValue(readCSVCell(myFractionData.get(index++)).//
                            movePointLeft(6));

                    ratioName = "rTh_Usample";
                    myFraction.getCompositionalMeasureByName(ratioName)//
                            .setValue(readCSVCell(myFractionData.get(index++)));

                    ratioName = AnalysisMeasures.fractionMass.getName();
                    myFraction.getAnalysisMeasure(ratioName)//
                            .setValue(readCSVCell(myFractionData.get(index++)));

                    // Isotopic Dates
                    ratioName = RadDates.age206_238r.getName();
                    myFraction.getRadiogenicIsotopeDateByName(ratioName)//
                            .setValue(readCSVCell(myFractionData.get(index++)).//
                            movePointRight(6));
                    myFraction.getRadiogenicIsotopeDateByName(ratioName)//
                            .setOneSigma(readCSVCell(myFractionData.get(index++)).//
                            divide(new BigDecimal(2.0)).movePointRight(6));

                    ratioName = RadDates.age207_235r.getName();
                    myFraction.getRadiogenicIsotopeDateByName(ratioName)//
                            .setValue(readCSVCell(myFractionData.get(index++)).//
                            movePointRight(6));
                    myFraction.getRadiogenicIsotopeDateByName(ratioName)//
                            .setOneSigma(readCSVCell(myFractionData.get(index++)).//
                            divide(new BigDecimal(2.0)).movePointRight(6));

                    ratioName = RadDates.age207_206r.getName();
                    myFraction.getRadiogenicIsotopeDateByName(ratioName)//
                            .setValue(readCSVCell(myFractionData.get(index++)).//
                            movePointRight(6));
                    myFraction.getRadiogenicIsotopeDateByName(ratioName)//
                            .setOneSigma(readCSVCell(myFractionData.get(index++)).//
                            divide(new BigDecimal(2.0)).movePointRight(6));

                    ratioName = RadDates.age206_238r_Th.getName();
                    myFraction.getRadiogenicIsotopeDateByName(ratioName)//
                            .setValue(readCSVCell(myFractionData.get(index++)).//
                            movePointRight(6));
                    myFraction.getRadiogenicIsotopeDateByName(ratioName)//
                            .setOneSigma(readCSVCell(myFractionData.get(index++)).//
                            divide(new BigDecimal(2.0)).movePointRight(6));

                    ratioName = RadDates.age207_235r_Pa.getName();
                    myFraction.getRadiogenicIsotopeDateByName(ratioName)//
                            .setValue(readCSVCell(myFractionData.get(index++)).//
                            movePointRight(6));
                    myFraction.getRadiogenicIsotopeDateByName(ratioName)//
                            .setOneSigma(readCSVCell(myFractionData.get(index++)).//
                            divide(new BigDecimal(2.0)).movePointRight(6));

                    ratioName = RadDates.age207_206r_Th.getName();
                    myFraction.getRadiogenicIsotopeDateByName(ratioName)//
                            .setValue(readCSVCell(myFractionData.get(index++)).//
                            movePointRight(6));
                    myFraction.getRadiogenicIsotopeDateByName(ratioName)//
                            .setOneSigma(readCSVCell(myFractionData.get(index++)).//
                            divide(new BigDecimal(2.0)).movePointRight(6));

                    ratioName = RadDates.age207_206r_Pa.getName();
                    myFraction.getRadiogenicIsotopeDateByName(ratioName)//
                            .setValue(readCSVCell(myFractionData.get(index++)).//
                            movePointRight(6));
                    myFraction.getRadiogenicIsotopeDateByName(ratioName)//
                            .setOneSigma(readCSVCell(myFractionData.get(index)).//
                            divide(new BigDecimal(2.0)).movePointRight(6));

                    // calculate percentDiscordance
                    ValueModel percentDiscordance = new PercentDiscordance();
                    myFraction.setRadiogenicIsotopeDateByName(RadDates.percentDiscordance, percentDiscordance);
                    percentDiscordance.calculateValue(
                            new ValueModel[]{
                                myFraction.getRadiogenicIsotopeDateByName(RadDates.age206_238r),
                                myFraction.getRadiogenicIsotopeDateByName(RadDates.age207_206r)},
                            null);

                    retFractions.add(myFraction);

                }

                if (myFractionData.get(0).compareToIgnoreCase("Fraction") == 0) {
                    // the next line(s) contain fraction data or are blank
                    readingFractions = true;
                }

            }
        } finally {
            //ensure the underlying stream is always closed
            scanner.close();
        }

        return retFractions;
    }

    /**
     * 
     */
    public static void writeAndOpenCSVFileOfLegacyDataSampleFieldNames() {
        String fieldNames = TemplatesForCsvImport.IDTIMSLegacyDataSampleFieldNames_MIT;
        File CSVFile = new File("IDTIMSLegacySampleFieldNamesTemplate" + ".csv");
        CSVFile.delete();
        PrintWriter outputWriter = null;
        try {
            outputWriter = new PrintWriter(new FileWriter(CSVFile));

            outputWriter.println("LEGACY ID-TIMS (MIT) DATA SAMPLE FIELD NAMES FOR IMPORT INTO U-Pb_Redux\n");
            outputWriter.println("FractionName, IsotopicRatios, , , , , , , , , Composition, , , , , , , IsotopicDates (Ma)");
            outputWriter.println(fieldNames);

            outputWriter.close();

        } catch (IOException iOException) {
        }

        try {
            BrowserControl.displayURL(CSVFile.getCanonicalPath());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}
