/*
 * ProjectOfLegacySamplesImporterFromCSVFile_GenericUPbIsotopic_A.java
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
package org.earthtime.projects.projectImporters.UPbProjectImporters;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.aliquots.Aliquot;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.UPb_Redux.fractions.Fraction;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFractionI;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbLegacyFraction;
import org.earthtime.UPb_Redux.reduxLabData.ReduxLabData;
import org.earthtime.UPb_Redux.samples.Sample;
import org.earthtime.UPb_Redux.utilities.BrowserControl;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.PercentDiscordance;
import org.earthtime.dataDictionaries.AnalysisMeasures;
import org.earthtime.dataDictionaries.RadDates;
import org.earthtime.dataDictionaries.SampleTypesEnum;
import org.earthtime.dataDictionaries.TemplatesForCsvImport;
import org.earthtime.exceptions.ETException;
import org.earthtime.projects.ProjectInterface;
import org.earthtime.projects.projectImporters.AbstractProjectImporterFromLegacyCSVFile;
import org.earthtime.samples.SampleInterface;

/**
 *
 * @author James F. Bowring
 */
public class ProjectOfLegacySamplesImporterFromCSVFile_GenericUPbIsotopic_A extends AbstractProjectImporterFromLegacyCSVFile {

    /**
     *
     * @param project
     * @param file
     * @return
     * @throws FileNotFoundException
     */
    @Override
    @SuppressWarnings("ValueOfIncrementOrDecrementUsed")
    protected ProjectInterface extractProjectFromCSVFile(ProjectInterface project, File file)
            throws FileNotFoundException {

        ArrayList<SampleInterface> projectSamples = new ArrayList<>();

        SampleInterface currentSample = null;
        Aliquot currentAliquot = null;

        boolean readingSamples = false;
        boolean readingFractions = false;

        try ( //first use a Scanner to get each line
                // Assumptions per Blair Shoene July 2012
                // read lines until first cell = "Sample", then skip next line of footnotes
                // then start process of scanning where blank lines are ignored and a line with only an entry in the first cell
                // is a sample = aliquot name and then the following lies are fractions until a blank line or a sample name
                Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                // get content of line and remove quotes
                @SuppressWarnings("UseOfObsoleteCollectionType")
                Vector<String> myFractionData = processLegacyCSVLine(scanner.nextLine());

                // break on footnotes
                if (myFractionData.get(0).startsWith("(")) {
                    readingFractions = false;
                    readingSamples = false;
                }
                // a zero for fraction name = blank line
                if (myFractionData.get(0).equalsIgnoreCase("0")) {
                    readingFractions = false;

                    // process existing if not first;
                    if ((currentSample != null) && (currentAliquot != null)) {
                        // this forces population of aliquot fractions
                        SampleInterface.copyAliquotIntoSample(currentSample.getAliquotByName(currentAliquot.getAliquotName()), project.getSuperSample());
                        currentSample = null;
                    }
                }

                if (myFractionData.get(0).startsWith("Sample")) {

                    // read the next line of footnotes
                    myFractionData = processLegacyCSVLine(scanner.nextLine());

                    // the next line(s) contain fraction data or are blank
                    readingSamples = true;
                }

                if (readingSamples) {
                    // note readingFractions is false
                    if (AbstractProjectImporterFromLegacyCSVFile.lineHasOnlyFirstElement(myFractionData)) {
                        // we have a sample
                        // process existing if not first;
                        if ((currentSample != null) && (currentAliquot != null)) {
                            // this forces population of aliquot fractions
                            SampleInterface.copyAliquotIntoSample(currentSample.getAliquotByName(currentAliquot.getAliquotName()), project.getSuperSample());
                            currentSample = null;
                        }

                        try {
                            currentSample = new Sample(//
                                    myFractionData.get(0), //
                                    SampleTypesEnum.LEGACY.getName(), //
                                    "GENERIC_UPb", //
                                    ReduxLabData.getInstance(), //
                                    ReduxConstants.ANALYSIS_PURPOSE.NONE);

                            projectSamples.add(currentSample);

                            currentAliquot = currentSample.addNewAliquot(myFractionData.get(0));

                            readingFractions = true;

                            // read the next line = first fraction
                            myFractionData = processLegacyCSVLine(scanner.nextLine());

                        } catch (BadLabDataException badLabDataException) {
                        } catch (ETException eTException) {
                        }

                    }
                }

                if (readingFractions) {
                    Fraction myFraction = new UPbLegacyFraction("NONE");

                    ((UPbFractionI) myFraction).setRatioType("UPb");

                    int index = 0;

                    myFraction.setFractionID(myFractionData.get(index++));
                    myFraction.setGrainID(myFraction.getFractionID());

                    String ratioName = AnalysisMeasures.fractionMass.getName();
                    myFraction.getAnalysisMeasure(ratioName)//
                            .setValue(readCSVCell(myFractionData.get(index++)));

                    myFraction.setNumberOfGrains(readCSVCell(myFractionData.get(index++)).intValue());

                    ratioName = "concPb_rib";
                    myFraction.getCompositionalMeasureByName(ratioName)//
                            .setValue(readCSVCell(myFractionData.get(index++)).//
                                    movePointLeft(6));

                    ratioName = "concU";
                    myFraction.getCompositionalMeasureByName(ratioName)//
                            .setValue(readCSVCell(myFractionData.get(index++)).//
                                    movePointLeft(6));

                    ratioName = "rTh_Usample";
                    myFraction.getCompositionalMeasureByName(ratioName)//
                            .setValue(readCSVCell(myFractionData.get(index++)));

                    ratioName = "radToCommonTotal"; // Pb*/Pb
                    myFraction.getCompositionalMeasureByName(ratioName)//
                            .setValue(readCSVCell(myFractionData.get(index++)));

                    ratioName = "totCommonPbMass"; //Pbc
                    myFraction.getCompositionalMeasureByName(ratioName)//
                            .setValue(readCSVCell(myFractionData.get(index++)).//
                                    movePointLeft(12));

                    ratioName = "r206_204tfc";
                    myFraction.getSampleIsochronRatiosByName(ratioName)//
                            .setValue(readCSVCell(myFractionData.get(index++)));
                    ratioName = "r208_206r";
                    myFraction.getRadiogenicIsotopeRatioByName(ratioName)//
                            .setValue(readCSVCell(myFractionData.get(index++)));

                    ratioName = "r207_206r";
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

                    ratioName = "r206_238r";
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

                    ((UPbFractionI) myFraction).calculateTeraWasserburgRho();

                    // Isotopic Dates
                    ratioName = RadDates.age207_206r.getName();
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

                    ratioName = RadDates.age206_238r.getName();
                    myFraction.getRadiogenicIsotopeDateByName(ratioName)//
                            .setValue(readCSVCell(myFractionData.get(index++)).//
                                    movePointRight(6));
                    myFraction.getRadiogenicIsotopeDateByName(ratioName)//
                            .setOneSigma(readCSVCell(myFractionData.get(index++)).//
                                    divide(new BigDecimal(2.0)).movePointRight(6));

                    // calculate percentDiscordance
                    ValueModel percentDiscordance = new PercentDiscordance();
                    myFraction.setRadiogenicIsotopeDateByName(RadDates.percentDiscordance, percentDiscordance);
                    percentDiscordance.calculateValue(
                            new ValueModel[]{
                                myFraction.getRadiogenicIsotopeDateByName(RadDates.age206_238r),
                                myFraction.getRadiogenicIsotopeDateByName(RadDates.age207_206r)},
                            null);

                    myFraction.setSampleName(currentSample.getSampleName());
                    currentSample.addFraction(myFraction);

                }

            }

            // process existing if not first;
            if ((currentSample != null) && (currentAliquot != null)) {
                // this forces population of aliquot fractions
                SampleInterface.copyAliquotIntoSample(currentSample.getAliquotByName(currentAliquot.getAliquotName()), project.getSuperSample());
                // april 2014
                project.setProjectSamples(projectSamples);

                currentSample = null;
            }

        }

        return project;
    }

    /**
     *
     */
    public static void writeAndOpenCSVFileOfLegacyDataSampleFieldNames() {
        String fieldNames = TemplatesForCsvImport.GenericUPbIsotopicLegacyDataSampleFieldNames_A;
        File CSVFile = new File("GenericUPbLegacySampleFieldNamesTemplate_A" + ".csv");
        CSVFile.delete();
        PrintWriter outputWriter = null;
        try {
            outputWriter = new PrintWriter(new FileWriter(CSVFile));

            outputWriter.println("GENERIC UPb Isotopic LEGACY DATA SAMPLE FIELD NAMES FOR IMPORT INTO U-Pb_Redux\n");
//            outputWriter.println( "FractionName, IsotopicRatios, , , , , , , , , Composition, , , , , , , IsotopicDates (Ma)" );
            outputWriter.println(fieldNames);

            outputWriter.println("Leave this line blank");
            outputWriter.println("First Sample Name here");
            outputWriter.println("First fraction row here");

            outputWriter.close();

        } catch (IOException iOException) {
        }

        try {
            BrowserControl.displayURL(CSVFile.getCanonicalPath());
        } catch (IOException ex) {
        }

    }
}
