/*
 * ProjectOfLegacySamplesImporterFromCSVFile_GenericUPbIsotopic_A.java
 *
 * Copyright 2006-2018 James F. Bowring, CIRDLES.org, and Earth-Time.org
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
import org.earthtime.UPb_Redux.aliquots.UPbReduxAliquot;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.UPb_Redux.fractions.FractionI;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFractionI;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbLegacyFraction;
import org.earthtime.UPb_Redux.samples.Sample;
import org.earthtime.UPb_Redux.utilities.BrowserControl;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.PercentDiscordance;
import org.earthtime.aliquots.AliquotInterface;
import org.earthtime.dataDictionaries.AnalysisMeasures;
import org.earthtime.dataDictionaries.FileDelimiterTypesEnum;
import org.earthtime.dataDictionaries.RadDates;
import org.earthtime.dataDictionaries.RadRatios;
import org.earthtime.dataDictionaries.RadRatiosPbcCorrected;
import org.earthtime.dataDictionaries.SampleTypesEnum;
import org.earthtime.dataDictionaries.TemplatesForCsvImport;
import org.earthtime.exceptions.ETException;
import org.earthtime.projects.ProjectInterface;
import org.earthtime.projects.projectImporters.AbstractProjectImporterFromLegacyDelimitedTextFile;
import org.earthtime.samples.SampleInterface;

/**
 *
 * @author James F. Bowring
 */
public class ProjectOfSamplesImporterFromCSVFile_Squid3 extends AbstractProjectImporterFromLegacyDelimitedTextFile {

    public ProjectOfSamplesImporterFromCSVFile_Squid3(FileDelimiterTypesEnum fileDelimiter) {
        super(fileDelimiter);
    }

    /**
     *
     * @param project
     * @param file
     * @return
     * @throws FileNotFoundException
     */
    @Override
    @SuppressWarnings("ValueOfIncrementOrDecrementUsed")
    protected ProjectInterface extractProjectFromDelimitedTextFile(ProjectInterface project, File file)
            throws FileNotFoundException {

        ArrayList<SampleInterface> projectSamples = new ArrayList<>();

        int columnStartReduxData = -1;

        SampleInterface currentSample = null;
        AliquotInterface currentAliquot = null;
        String savedSampleName = "";
        int aliquotCounter = 1;

        boolean readingSamples = false;
        boolean readingFractions = false;

        try (
                Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                // get content of line and remove quotes
                @SuppressWarnings("UseOfObsoleteCollectionType")
                Vector<String> myFractionData = processLegacyCSVLine(scanner.nextLine());
                if (myFractionData.size() > 1) {

                    // use first line to determine starting column of "Correction-Independent Data"
                    if (myFractionData.get(1).startsWith("Spot Fundamentals")) {
                        // walk vector to find entry
                        for (int i = 0; i < myFractionData.size(); i++) {
                            if (myFractionData.get(i).compareToIgnoreCase("Correction-Independent Data") == 0) {
                                columnStartReduxData = i;
                                break;
                            }
                        }
                    }

                    if (myFractionData.get(0).startsWith("Fraction")) {
                        // the next line(s) contain fraction data or are blank
                        myFractionData = processLegacyCSVLine(scanner.nextLine());
                        readingSamples = true;
                    }

                    if (readingSamples) {
                        if ((myFractionData.get(1).compareToIgnoreCase("0") == 0)
                                && (myFractionData.get(0).compareTo(savedSampleName) != 0)) {
                            // we have a new sample
                            // process existing if not first;
                            if ((currentSample != null) && (currentAliquot != null)) {
                                // this forces population of aliquot fractions
                                SampleInterface.copyAliquotIntoSample(
                                        project.getSuperSample(), currentSample.getAliquotByName(currentAliquot.getAliquotName()), new UPbReduxAliquot());

                                currentSample.initFilteredFractionsToAll();

                                currentSample = null;
                            }

                            try {
                                savedSampleName = myFractionData.get(0);
                                currentSample = new Sample(//
                                        myFractionData.get(0), //
                                        SampleTypesEnum.LEGACY.getName(), //
                                        "GENERIC_UPb", //
                                        ReduxConstants.ANALYSIS_PURPOSE.NONE, "UPb", "UPb");

                                projectSamples.add(currentSample);

                                currentAliquot = currentSample.addNewAliquot(myFractionData.get(0));

                                aliquotCounter = 1;

                                readingFractions = true;
                                myFractionData = processLegacyCSVLine(scanner.nextLine());

                            } catch (BadLabDataException badLabDataException) {
                            } catch (ETException eTException) {
                            }
                        }
                    }

                    if (readingFractions) {
                        FractionI myFraction = new UPbLegacyFraction("NONE");
                        myFraction.setAliquotNumber(aliquotCounter);

                        myFraction.setRatioType("UPb");
                        ((UPbLegacyFraction) myFraction).setSquidLegacy(true);

                        myFraction.setFractionID(myFractionData.get(0));
                        myFraction.setGrainID(myFraction.getFractionID());

                        String ratioName = AnalysisMeasures.fractionMass.getName();
                        myFraction.getAnalysisMeasure(ratioName)//
                                .setValue(BigDecimal.ZERO);

                        myFraction.setNumberOfGrains(1);

                        ratioName = "concU";
                        myFraction.getCompositionalMeasureByName(ratioName)//
                                .setValue(readDelimitedTextCell(myFractionData.get(columnStartReduxData + 4)).//
                                        movePointLeft(6));

                        ratioName = "concTh";
                        myFraction.getCompositionalMeasureByName(ratioName)//
                                .setValue(readDelimitedTextCell(myFractionData.get(columnStartReduxData + 5)).//
                                        movePointLeft(6));

                        ratioName = "rTh_Usample";
                        myFraction.getCompositionalMeasureByName(ratioName)//
                                .setValue(readDelimitedTextCell(myFractionData.get(columnStartReduxData + 6)));

                        ratioName = "pctComm206";
                        myFraction.getCompositionalMeasureByName(ratioName)//
                                .setValue(readDelimitedTextCell(myFractionData.get(columnStartReduxData + 16)));
                        
                        ratioName = "pctComm208";
                        myFraction.getCompositionalMeasureByName(ratioName)//
                                .setValue(readDelimitedTextCell(myFractionData.get(columnStartReduxData + 17)));


                        // Uncorrected TW ratios *******************************
                        ratioName = RadRatios.r238_206r.getName();
                        myFraction.getRadiogenicIsotopeRatioByName(ratioName)//
                                .setValue(readDelimitedTextCell(myFractionData.get(columnStartReduxData + 12)));
                        ValueModel ratio = myFraction.getRadiogenicIsotopeRatioByName(ratioName);

                        // convert 1-sigma to 1-sigma
                        BigDecimal oneSigmaPct = readDelimitedTextCell(myFractionData.get(columnStartReduxData + 13)).//
                                divide(new BigDecimal(1.0));
                        myFraction.getRadiogenicIsotopeRatioByName(ratioName)//
                                .setOneSigma(ValueModel.convertOneSigmaPctToAbsIfRequired(ratio, oneSigmaPct));

                        ratioName = RadRatios.r207_206r.getName();
                        myFraction.getRadiogenicIsotopeRatioByName(ratioName)//
                                .setValue(readDelimitedTextCell(myFractionData.get(columnStartReduxData + 14)));
                        ratio = myFraction.getRadiogenicIsotopeRatioByName(ratioName);

                        // convert 1-sigma to 1-sigma
                        oneSigmaPct = readDelimitedTextCell(myFractionData.get(columnStartReduxData + 15)).//
                                divide(new BigDecimal(1.0));
                        myFraction.getRadiogenicIsotopeRatioByName(ratioName)//
                                .setOneSigma(ValueModel.convertOneSigmaPctToAbsIfRequired(ratio, oneSigmaPct));

                        ratioName = "rhoR207_206r__r238_206r";
                        myFraction.getRadiogenicIsotopeRatioByName(ratioName)//
                                .setValue(BigDecimal.ZERO);

                        // 4-corrected ratios for wetherill and TW
                        ratioName = RadRatiosPbcCorrected.r238_206_PbcCorr.getName();
                        myFraction.getRadiogenicIsotopeRatioByName(ratioName)//
                                .setValue(readDelimitedTextCell(myFractionData.get(columnStartReduxData + 31)));
                        ratio = myFraction.getRadiogenicIsotopeRatioByName(ratioName);

                        // convert 1-sigma to 1-sigma
                        oneSigmaPct = readDelimitedTextCell(myFractionData.get(columnStartReduxData + 32)).//
                                divide(new BigDecimal(1.0));
                        myFraction.getRadiogenicIsotopeRatioByName(ratioName)//
                                .setOneSigma(ValueModel.convertOneSigmaPctToAbsIfRequired(ratio, oneSigmaPct));

                        ratioName = RadRatiosPbcCorrected.r207_206_PbcCorr.getName();
                        myFraction.getRadiogenicIsotopeRatioByName(ratioName)//
                                .setValue(readDelimitedTextCell(myFractionData.get(columnStartReduxData + 33)));
                        ratio = myFraction.getRadiogenicIsotopeRatioByName(ratioName);

                        // convert 1-sigma to 1-sigma
                        oneSigmaPct = readDelimitedTextCell(myFractionData.get(columnStartReduxData + 34)).//
                                divide(new BigDecimal(1.0));
                        myFraction.getRadiogenicIsotopeRatioByName(ratioName)//
                                .setOneSigma(ValueModel.convertOneSigmaPctToAbsIfRequired(ratio, oneSigmaPct));

                        ratioName = RadRatiosPbcCorrected.r207_235_PbcCorr.getName();
                        myFraction.getRadiogenicIsotopeRatioByName(ratioName)//
                                .setValue(readDelimitedTextCell(myFractionData.get(columnStartReduxData + 35)));
                        ratio = myFraction.getRadiogenicIsotopeRatioByName(ratioName);

                        // convert 2-sigma to 1-sigma
                        oneSigmaPct = readDelimitedTextCell(myFractionData.get(columnStartReduxData + 36)).//
                                divide(new BigDecimal(1.0));
                        myFraction.getRadiogenicIsotopeRatioByName(ratioName)//
                                .setOneSigma(ValueModel.convertOneSigmaPctToAbsIfRequired(ratio, oneSigmaPct));

                        ratioName = RadRatiosPbcCorrected.r206_238_PbcCorr.getName();
                        myFraction.getRadiogenicIsotopeRatioByName(ratioName)//
                                .setValue(readDelimitedTextCell(myFractionData.get(columnStartReduxData + 37)));
                        ratio = myFraction.getRadiogenicIsotopeRatioByName(ratioName);

                        // convert 2-sigma to 1-sigma
                        oneSigmaPct = readDelimitedTextCell(myFractionData.get(columnStartReduxData + 38)).//
                                divide(new BigDecimal(1.0));
                        myFraction.getRadiogenicIsotopeRatioByName(ratioName)//
                                .setOneSigma(ValueModel.convertOneSigmaPctToAbsIfRequired(ratio, oneSigmaPct));

                        ratioName = "rhoR206_238PbcCorr__r207_235PbcCorr";
                        myFraction.getRadiogenicIsotopeRatioByName(ratioName)//
                                .setValue(readDelimitedTextCell(myFractionData.get(columnStartReduxData + 39)));

//                        ((UPbFractionI) myFraction).calculateTeraWasserburgRho();
                        ratioName = "rhoR207_206PbcCorr__r238_206PbcCorr";
                        myFraction.getRadiogenicIsotopeRatioByName(ratioName)//
                                .setValue(BigDecimal.ZERO);

                        // Corrected Isotopic Dates
                        ratioName = RadDates.age206_238_PbcCorr.getName();
                        myFraction.getRadiogenicIsotopeDateByName(ratioName)//
                                .setValue(readDelimitedTextCell(myFractionData.get(columnStartReduxData + 22)).//
                                        movePointRight(6));
                        myFraction.getRadiogenicIsotopeDateByName(ratioName)//
                                .setOneSigma(readDelimitedTextCell(myFractionData.get(columnStartReduxData + 23)).//
                                        divide(new BigDecimal(1.0)).movePointRight(6));

                        ratioName = RadDates.age207_206_PbcCorr.getName();
                        myFraction.getRadiogenicIsotopeDateByName(ratioName)//
                                .setValue(readDelimitedTextCell(myFractionData.get(columnStartReduxData + 24)).//
                                        movePointRight(6));
                        myFraction.getRadiogenicIsotopeDateByName(ratioName)//
                                .setOneSigma(readDelimitedTextCell(myFractionData.get(columnStartReduxData + 25)).//
                                        divide(new BigDecimal(1.0)).movePointRight(6));

                        ratioName = RadDates.age208_232_PbcCorr.getName();
                        myFraction.getRadiogenicIsotopeDateByName(ratioName)//
                                .setValue(readDelimitedTextCell(myFractionData.get(columnStartReduxData + 26)).//
                                        movePointRight(6));
                        myFraction.getRadiogenicIsotopeDateByName(ratioName)//
                                .setOneSigma(readDelimitedTextCell(myFractionData.get(columnStartReduxData + 27)).//
                                        divide(new BigDecimal(1.0)).movePointRight(6));

                        ratioName = RadDates.percentDiscordance_PbcCorr.getName();
                        myFraction.getRadiogenicIsotopeDateByName(ratioName)//
                                .setValue(readDelimitedTextCell(myFractionData.get(columnStartReduxData + 28)));

                        // calculate percentDiscordance
//                        ValueModel percentDiscordance = new PercentDiscordance();
//                        myFraction.setRadiogenicIsotopeDateByName(RadDates.percentDiscordance, percentDiscordance);
//                        percentDiscordance.calculateValue(
//                                new ValueModel[]{
//                                    myFraction.getRadiogenicIsotopeDateByName(RadDates.age206_238r),
//                                    myFraction.getRadiogenicIsotopeDateByName(RadDates.age207_206r)},
//                                null);
                        myFraction.setSampleName(currentSample.getSampleName());
                        currentSample.addFraction(myFraction);
                    }
                }
            }

            // process existing if not first;
            if ((currentSample != null) && (currentAliquot != null)) {
                // this forces population of aliquot fractions
                SampleInterface.copyAliquotIntoSample(project.getSuperSample(), currentSample.getAliquotByName(currentAliquot.getAliquotName()), new UPbReduxAliquot());

                currentSample.initFilteredFractionsToAll();

                // april 2014
                project.setProjectSamples(projectSamples);
                project.getSuperSample().initFilteredFractionsToAll();

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

            outputWriter.println("GENERIC UPb Isotopic LEGACY DATA SAMPLE FIELD NAMES FOR IMPORT INTO ET_Redux\n");
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
