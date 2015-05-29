/*
 * ProjectOfLegacySamplesFieldNames_UCSB_LASS_A.java
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
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.UPb_Redux.fractions.Fraction;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFractionI;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbLegacyFraction;
import org.earthtime.UPb_Redux.reduxLabData.ReduxLabData;
import org.earthtime.UPb_Redux.samples.Sample;
import org.earthtime.UPb_Redux.utilities.BrowserControl;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.PercentDiscordance;
import org.earthtime.aliquots.AliquotI;
import org.earthtime.dataDictionaries.RadDates;
import org.earthtime.dataDictionaries.SampleTypesEnum;
import org.earthtime.dataDictionaries.TemplatesForCsvImport;
import org.earthtime.dataDictionaries.TraceElements;
import org.earthtime.exceptions.ETException;
import org.earthtime.projects.ProjectInterface;
import org.earthtime.projects.projectImporters.AbstractProjectImporterFromLegacyCSVFile;
import org.earthtime.samples.SampleInterface;

/**
 *
 * @author James F. Bowring This importer services UC Santa Barbara Laser
 * Ablation Split Stream legacy data starting 20 June 2014.
 */
public class ProjectOfLegacySamplesImporterFromCSVFile_UCSB_LASS_A extends AbstractProjectImporterFromLegacyCSVFile {

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
        // april 2014
        project.setProjectSamples(projectSamples);

        SampleInterface currentSample = null;
        AliquotI currentAliquot = null;

        boolean readingFractions = false;

        try ( //first use a Scanner to get each line
                Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                // get content of line and remove quotes
                @SuppressWarnings("UseOfObsoleteCollectionType")
                Vector<String> myFractionData = processLegacyCSVLine(scanner.nextLine());

                if ((myFractionData.get(0).toUpperCase().startsWith("SAMPLE"))//
                        || //
                        myFractionData.get(0).equalsIgnoreCase("0")) {

                    // either first line or blank line separating samples
                    // the next line(s) contain /sample fraction data or are blank
                    myFractionData = processLegacyCSVLine(scanner.nextLine());
                    readingFractions = !myFractionData.get(0).equalsIgnoreCase("0");

                    // process existing if not first;
                    if ((currentSample != null) && (currentAliquot != null)) {
                        // this forces population of aliquot fractions
                        SampleInterface.copyAliquotIntoSample(currentSample.getAliquotByName(currentAliquot.getAliquotName()), project.getSuperSample());
                    }

                    if (readingFractions) {
                        try {
                            currentSample = new Sample(//
                                    myFractionData.get(0), //
                                    SampleTypesEnum.LEGACY.getName(), //
                                    "LASS", //
                                    ReduxLabData.getInstance(), //
                                    ReduxConstants.ANALYSIS_PURPOSE.NONE);

                            projectSamples.add(currentSample);

                            String aliquotName = myFractionData.get(1);
                            if (aliquotName.length() == 0) {
                                aliquotName = myFractionData.get(0); // sample name
                            }
                            currentAliquot = currentSample.addNewAliquot(aliquotName);

                        } catch (BadLabDataException badLabDataException) {
                        } catch (ETException eTException) {
                        }
                    }
                }

                if (readingFractions) {
                    // process fraction line
                    System.out.println("Reading Fraction " //
                            + myFractionData.get(0) + "." + myFractionData.get(1) //
                            + "." + myFractionData.get(2) + "." + myFractionData.get(3));

                    Fraction myFraction = new UPbLegacyFraction("NONE");

                    ((UPbFractionI) myFraction).setRatioType("UPb");

                    String fractionNamePart1 = myFractionData.get(1);
                    String fractionNamePart2 = myFractionData.get(2);
                    String fractionNamePart3 = myFractionData.get(3);
                    String fractionID = //
                            ((fractionNamePart1.length() == 0) ? "" : (fractionNamePart1 + ".")) //
                            + fractionNamePart2 //
                            + ((fractionNamePart3.length() == 0) ? "" : ("." + fractionNamePart3));

                    myFraction.setFractionID(fractionID);
                    myFraction.setGrainID(fractionID);

                    String datumName = "concPb_rib";
                    myFraction.getCompositionalMeasureByName(datumName)//
                            .setValue(readCSVCell(myFractionData.get(7)).// column H
                                    movePointLeft(6));

                    datumName = "concU";
                    myFraction.getCompositionalMeasureByName(datumName)//
                            .setValue(readCSVCell(myFractionData.get(8)).// column I
                                    movePointLeft(6));

                    datumName = "concTh";
                    myFraction.getCompositionalMeasureByName(datumName)//
                            .setValue(readCSVCell(myFractionData.get(9)).// column J
                                    movePointLeft(6));

                    datumName = "rTh_Usample";
                    myFraction.getCompositionalMeasureByName(datumName)//
                            .setValue(readCSVCell(myFractionData.get(10)));// column K

                    datumName = "r206_204tfc";
                    myFraction.getSampleIsochronRatiosByName(datumName)//
                            .setValue(readCSVCell(myFractionData.get(11)));// column L

                    // convert 2-sigma to 1-sigma
                    BigDecimal oneSigmaAbs = readCSVCell(myFractionData.get(12)).// column M
                            divide(new BigDecimal(2.0));
                    myFraction.getSampleIsochronRatiosByName(datumName)//
                            .setOneSigma(oneSigmaAbs);

                    datumName = "r207_206r";
                    myFraction.getRadiogenicIsotopeRatioByName(datumName)//
                            .setValue(readCSVCell(myFractionData.get(13)));// column N

                    // convert 2-sigma to 1-sigma
                    oneSigmaAbs = readCSVCell(myFractionData.get(14)).// column O
                            divide(new BigDecimal(2.0));
                    myFraction.getRadiogenicIsotopeRatioByName(datumName)//
                            .setOneSigma(oneSigmaAbs);

//                    datumName = "r206_238r";
//                    // recorded as 238/206, so invert
//                    myFraction.getRadiogenicIsotopeRatioByName(datumName)//
//                            .setValue(BigDecimal.ONE.divide(readCSVCell(myFractionData.get(15)), MathContext.DECIMAL64));// column P
//
//                    // first convert 2-sigma ABS to 1-sigma ABS for inverted value using oneSigmaAbs/value^2 or onesigmaAbs*invVal^2
//                    ValueModel datum = myFraction.getRadiogenicIsotopeRatioByName(datumName);
//                    oneSigmaAbs = readCSVCell(myFractionData.get(16)).// column Q
//                            divide(new BigDecimal(2.0));
//                    BigDecimal oneSigmaAbsOfInverted = oneSigmaAbs.multiply(datum.getValue().pow(2));
//                    myFraction.getRadiogenicIsotopeRatioByName(datumName)//
//                            .setOneSigma(oneSigmaAbsOfInverted);
//                    
                    datumName = "r207_235r";
                    myFraction.getRadiogenicIsotopeRatioByName(datumName)//
                            .setValue(readCSVCell(myFractionData.get(17)));// column R

                    // convert 2-sigma to 1-sigma
                    oneSigmaAbs = readCSVCell(myFractionData.get(18)).// column S
                            divide(new BigDecimal(2.0));
                    myFraction.getRadiogenicIsotopeRatioByName(datumName)//
                            .setOneSigma(oneSigmaAbs);

                    datumName = "r206_238r";
                    myFraction.getRadiogenicIsotopeRatioByName(datumName)//
                            .setValue(readCSVCell(myFractionData.get(19)));// column T

                    // convert 2-sigma to 1-sigma
                    oneSigmaAbs = readCSVCell(myFractionData.get(20)).// column U
                            divide(new BigDecimal(2.0));
                    myFraction.getRadiogenicIsotopeRatioByName(datumName)//
                            .setOneSigma(oneSigmaAbs);

                    datumName = "rhoR206_238r__r207_235r";
                    myFraction.getRadiogenicIsotopeRatioByName(datumName)//
                            .setValue(readCSVCell(myFractionData.get(21))); // column V

                    ((UPbFractionI) myFraction).calculateTeraWasserburgRho();

                    datumName = "r208_232r";
                    myFraction.getRadiogenicIsotopeRatioByName(datumName)//
                            .setValue(readCSVCell(myFractionData.get(22)));// column W

                    // convert 2-sigma to 1-sigma
                    oneSigmaAbs = readCSVCell(myFractionData.get(23)).// column X
                            divide(new BigDecimal(2.0));
                    myFraction.getRadiogenicIsotopeRatioByName(datumName)//
                            .setOneSigma(oneSigmaAbs);

                    // Isotopic Dates
                    datumName = RadDates.age207_206r.getName();
                    myFraction.getRadiogenicIsotopeDateByName(datumName)//
                            .setValue(readCSVCell(myFractionData.get(24)).// column Y
                                    movePointRight(6));
                    myFraction.getRadiogenicIsotopeDateByName(datumName)//
                            .setOneSigma(readCSVCell(myFractionData.get(25)).// column Z
                                    divide(new BigDecimal(2.0)).movePointRight(6));

                    datumName = RadDates.age207_235r.getName();
                    myFraction.getRadiogenicIsotopeDateByName(datumName)//
                            .setValue(readCSVCell(myFractionData.get(26)).// column AA
                                    movePointRight(6));
                    myFraction.getRadiogenicIsotopeDateByName(datumName)//
                            .setOneSigma(readCSVCell(myFractionData.get(27)).// column AB
                                    divide(new BigDecimal(2.0)).movePointRight(6));

                    datumName = RadDates.age206_238r.getName();
                    myFraction.getRadiogenicIsotopeDateByName(datumName)//
                            .setValue(readCSVCell(myFractionData.get(28)).// column AC
                                    movePointRight(6));
                    myFraction.getRadiogenicIsotopeDateByName(datumName)//
                            .setOneSigma(readCSVCell(myFractionData.get(29)).// column AD
                                    divide(new BigDecimal(2.0)).movePointRight(6));

                    // oct 2014 inserted
                    datumName = RadDates.age206_238r_Th.getName();
                    myFraction.getRadiogenicIsotopeDateByName(datumName)//
                            .setValue(readCSVCell(myFractionData.get(30)).// column AE
                                    movePointRight(6));
                    myFraction.getRadiogenicIsotopeDateByName(datumName)//
                            .setOneSigma(readCSVCell(myFractionData.get(31)).// column AF
                                    divide(new BigDecimal(2.0)).movePointRight(6));

                    datumName = RadDates.age208_232r.getName();
                    myFraction.getRadiogenicIsotopeDateByName(datumName)//
                            .setValue(readCSVCell(myFractionData.get(32)).// column AG
                                    movePointRight(6));
                    myFraction.getRadiogenicIsotopeDateByName(datumName)//
                            .setOneSigma(readCSVCell(myFractionData.get(33)).// column AH
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

                    // Trace Elements
                    traceElementProcessor(TraceElements.Si.getName(), myFraction, myFractionData, 34);
                    traceElementProcessor(TraceElements.P.getName(), myFraction, myFractionData, 35);
                    traceElementProcessor(TraceElements.Ca.getName(), myFraction, myFractionData, 36);
                    traceElementProcessor(TraceElements.Ti.getName(), myFraction, myFractionData, 37);
                    traceElementProcessor(TraceElements.Rb.getName(), myFraction, myFractionData, 38);
                    traceElementProcessor(TraceElements.Sr.getName(), myFraction, myFractionData, 39);
                    traceElementProcessor(TraceElements.Y.getName(), myFraction, myFractionData, 40);
                    traceElementProcessor(TraceElements.Zr.getName(), myFraction, myFractionData, 41);
                    traceElementProcessor(TraceElements.La.getName(), myFraction, myFractionData, 42);
                    traceElementProcessor(TraceElements.Ce.getName(), myFraction, myFractionData, 43);
                    traceElementProcessor(TraceElements.Pr.getName(), myFraction, myFractionData, 44);
                    traceElementProcessor(TraceElements.Nd.getName(), myFraction, myFractionData, 45);
                    traceElementProcessor(TraceElements.Sm.getName(), myFraction, myFractionData, 46);
                    traceElementProcessor(TraceElements.Eu.getName(), myFraction, myFractionData, 47);
                    traceElementProcessor(TraceElements.Gd.getName(), myFraction, myFractionData, 48);
                    traceElementProcessor(TraceElements.Tb.getName(), myFraction, myFractionData, 49);
                    traceElementProcessor(TraceElements.Dy.getName(), myFraction, myFractionData, 50);
                    traceElementProcessor(TraceElements.Ho.getName(), myFraction, myFractionData, 51);
                    traceElementProcessor(TraceElements.Er.getName(), myFraction, myFractionData, 52);
                    traceElementProcessor(TraceElements.Tm.getName(), myFraction, myFractionData, 53);
                    traceElementProcessor(TraceElements.Yb.getName(), myFraction, myFractionData, 54);
                    traceElementProcessor(TraceElements.Lu.getName(), myFraction, myFractionData, 55);
                    traceElementProcessor(TraceElements.Hf.getName(), myFraction, myFractionData, 56);
                }
            }

            // end of file
            if ((currentSample != null) && (currentAliquot != null)) {
                // this forces population of aliquot fractions
                SampleInterface.copyAliquotIntoSample(currentSample.getAliquotByName(currentAliquot.getAliquotName()), project.getSuperSample());
            }
        }

        return project;
    }

    private void traceElementProcessor(String datumName, Fraction myFraction, Vector<String> myFractionData, int col) {
        // check for missing fields at end of row
        if (col < myFractionData.size()) {
            myFraction.getTraceElementByName(datumName)//
                    .setValue(readCSVCell(myFractionData.get(col)).
                            movePointLeft(6));
        } else {
            myFraction.getTraceElementByName(datumName)//
                    .setValue(BigDecimal.ZERO);
        }
    }

    /**
     *
     */
    public static void writeAndOpenCSVFileOfLegacyDataSampleFieldNames() {
        String fieldNames = TemplatesForCsvImport.ProjectOfLegacySamplesFieldNames_UCSB_LASS_A;
        File CSVFile = new File("ProjectOfLegacySamples_UCSB_LASS_A_CSVFile_Template" + ".csv");
        CSVFile.delete();
        PrintWriter outputWriter = null;
        try {
            outputWriter = new PrintWriter(new FileWriter(CSVFile));

            outputWriter.println(fieldNames);

            outputWriter.close();

        } catch (IOException iOException) {
        }

        try {
            BrowserControl.displayURL(CSVFile.getCanonicalPath());
        } catch (IOException ex) {
        }

    }
}
