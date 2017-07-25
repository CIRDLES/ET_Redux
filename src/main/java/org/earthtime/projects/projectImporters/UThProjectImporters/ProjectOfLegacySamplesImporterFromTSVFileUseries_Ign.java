/*
 * ProjectOfLegacySamplesImporterFromTSVFileUseries_Ign.java
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
package org.earthtime.projects.projectImporters.UThProjectImporters;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.earthtime.UPb_Redux.ReduxConstants;
import static org.earthtime.UPb_Redux.ReduxConstants.TIME_IN_MILLISECONDS_FROM_1970_TO_2000;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.UTh_Redux.aliquots.UThReduxAliquot;
import org.earthtime.UTh_Redux.fractions.UThFraction;
import org.earthtime.UTh_Redux.fractions.UThLegacyFraction;
import org.earthtime.UTh_Redux.fractions.UThLegacyFractionI;
import org.earthtime.UTh_Redux.fractions.fractionReduction.UThFractionReducer;
import org.earthtime.UTh_Redux.samples.SampleUTh;
import org.earthtime.aliquots.AliquotInterface;
import org.earthtime.aliquots.ReduxAliquotInterface;
import org.earthtime.dataDictionaries.FileDelimiterTypesEnum;
import static org.earthtime.dataDictionaries.SampleAnalysisTypesEnum.USERIES_IGN;
import org.earthtime.dataDictionaries.SampleTypesEnum;
import org.earthtime.dataDictionaries.UThAnalysisMeasures;
import org.earthtime.dataDictionaries.UThCompositionalMeasures;
import org.earthtime.exceptions.ETException;
import org.earthtime.fractions.ETFractionInterface;
import org.earthtime.projects.ProjectInterface;
import org.earthtime.projects.projectImporters.AbstractProjectImporterFromLegacyDelimitedTextFile;
import org.earthtime.samples.SampleInterface;

/**
 *
 * @author James F. Bowring This importer services UC Santa Barbara Laser
 * Ablation Split Stream legacy data starting 20 June 2014.
 */
public class ProjectOfLegacySamplesImporterFromTSVFileUseries_Ign extends AbstractProjectImporterFromLegacyDelimitedTextFile {

    public ProjectOfLegacySamplesImporterFromTSVFileUseries_Ign(FileDelimiterTypesEnum fileDelimiter) {
        super(fileDelimiter);
    }

    /**
     * Reads tsv files generated from Ken Rubin's data set published in our
     * standards paper 2017 and establishing a template.
     *
     * @param project
     * @param file
     * @return
     * @throws FileNotFoundException
     */
    @Override
    protected ProjectInterface extractProjectFromDelimitedTextFile(ProjectInterface project, File file)
            throws FileNotFoundException {

        ArrayList<SampleInterface> projectSamples = new ArrayList<>();

        project.setProjectSamples(projectSamples);

        SampleInterface currentSample = null;
        AliquotInterface currentAliquot = null;

        List<String> fractionData;
        try {
            fractionData = Files.readLines(file, Charsets.ISO_8859_1);

            // based on this: https://drive.google.com/open?id=0B2El51RQ1MQnVXFCekJ2Xy1iOGM
            // ignore first line
            // we only allow one source ID for now
            // we allow multiple sampleIDs
            String savedSampleID = "";
            for (int i = 1; i < fractionData.size(); i++) {
                @SuppressWarnings("UseOfObsoleteCollectionType")
                Vector<String> myFractionData = processLegacyTSVLine(fractionData.get(i));
                // check for empty rows
                if (!myFractionData.get(1).equals("")) {

                    // column B
                    String sampleID = myFractionData.get(1);
                    if (!savedSampleID.equalsIgnoreCase(sampleID)) {
                        savedSampleID = sampleID;

                        // process existing if not first;
                        if ((currentSample != null) && (currentAliquot != null)) {
                            processSuperSample(project.getSuperSample(), currentSample, currentAliquot);
                            currentSample = null;
                            currentAliquot = null;
                        }

                        // test if sample already exists
                        for (SampleInterface sample : projectSamples) {
                            if (sample.getSampleName().equalsIgnoreCase(sampleID)) {
                                currentSample = sample;
                                // there is only one aliquot in this USeries scenario
                                currentAliquot = sample.getAliquots().get(0);
                                break;
                            }
                        }
                        if ((currentSample == null) && (currentAliquot == null)) {
                            // new sample
                            try {
                                currentSample = new SampleUTh(//
                                        sampleID, //
                                        SampleTypesEnum.LEGACY.getName(), //
                                        USERIES_IGN.getName(), //
                                        ReduxConstants.ANALYSIS_PURPOSE.SingleAge,
                                        USERIES_IGN.getIsotypeSystem(),
                                        USERIES_IGN.getDefaultReportSpecsType());

                                projectSamples.add(currentSample);

                                currentAliquot = currentSample.addNewAliquot(sampleID);

                            } catch (BadLabDataException badLabDataException) {
                            } catch (ETException eTException) {
                            }
                        }
                    }

                    if ((currentSample != null) && (myFractionData.size() > 1)) {
                        // process fractions
                        UThLegacyFractionI myFraction = new UThLegacyFraction();
                        // column C = Phase
                        myFraction.setFractionID(myFractionData.get(2));
                        myFraction.setGrainID(myFractionData.get(2));

                        System.out.println("Reading legacy fraction " + myFraction.getFractionID());

                        myFraction.useLegacyPhysicalConstantsD1();

                        myFraction.setSampleName(currentSample.getSampleName());
                        // in case a sample occurs out of order
                        if (currentSample.getFractions().size() > 0) {
                            myFraction.setAliquotNumber(currentSample.getFractions().get(0).getAliquotNumber());
                        }
                        currentSample.addFraction(myFraction);
                        ((ReduxAliquotInterface) currentAliquot).getAliquotFractions().add(myFraction);

                        // column D=3 is conc232Th in ppm
                        String ratioName = UThCompositionalMeasures.conc232Th.getName();
                        myFraction.getCompositionalMeasureByName(ratioName)//
                                .setValue(readDelimitedTextCell(myFractionData.get(3)).//
                                        movePointLeft(6));

                        // column E=4 is conc232Th uncertainty in ppm
                        // convert 2-sigma to 1-sigma
                        BigDecimal oneSigmaAbs = readDelimitedTextCell(myFractionData.get(4)).
                                divide(new BigDecimal(2.0)).//
                                movePointLeft(6);
                        myFraction.getCompositionalMeasureByName(ratioName)//
                                .setOneSigma(oneSigmaAbs);

                        // column F=5 is conc238U in ppm
                        ratioName = UThCompositionalMeasures.conc238U.getName();
                        myFraction.getCompositionalMeasureByName(ratioName)//
                                .setValue(readDelimitedTextCell(myFractionData.get(5)).//
                                        movePointLeft(6));

                        // column G=6 is conc238U uncertainty in ppm
                        // convert 2-sigma to 1-sigma
                        oneSigmaAbs = readDelimitedTextCell(myFractionData.get(6)).
                                divide(new BigDecimal(2.0)).//
                                movePointLeft(6);
                        myFraction.getCompositionalMeasureByName(ratioName)//
                                .setOneSigma(oneSigmaAbs);

                        // column H=7 is ar238U_232Thfc
                        ratioName = UThAnalysisMeasures.ar238U_232Thfc.getName();
                        myFraction.getLegacyActivityRatioByName(ratioName)//
                                .setValue(readDelimitedTextCell(myFractionData.get(7)));

                        // column I=8 is r238U_232Thfc uncertainty
                        // convert 2-sigma to 1-sigma
                        oneSigmaAbs = readDelimitedTextCell(myFractionData.get(8)).
                                divide(new BigDecimal(2.0));
                        myFraction.getLegacyActivityRatioByName(ratioName)//
                                .setOneSigma(oneSigmaAbs);

                        // column J=9 is ar230Th_232Thfc 
                        ratioName = UThAnalysisMeasures.ar230Th_232Thfc.getName();
                        myFraction.getLegacyActivityRatioByName(ratioName)//
                                .setValue(readDelimitedTextCell(myFractionData.get(9)));

                        // column K=10 is r230U_232Thfc uncertainty
                        // convert 2-sigma to 1-sigma
                        oneSigmaAbs = readDelimitedTextCell(myFractionData.get(10)).
                                divide(new BigDecimal(2.0));
                        myFraction.getLegacyActivityRatioByName(ratioName)//
                                .setOneSigma(oneSigmaAbs);

                        // column L=11 is ar234U_238Ufc
                        ratioName = UThAnalysisMeasures.ar234U_238Ufc.getName();
                        myFraction.getLegacyActivityRatioByName(ratioName)//
                                .setValue(readDelimitedTextCell(myFractionData.get(11)));

                        // column M=12 is ar234U_238Ufc uncertainty
                        // convert 2-sigma to 1-sigma
                        oneSigmaAbs = readDelimitedTextCell(myFractionData.get(12)).
                                divide(new BigDecimal(2.0));
                        myFraction.getLegacyActivityRatioByName(ratioName)//
                                .setOneSigma(oneSigmaAbs);

                        // column N=13 is activity of 230Th concentration ***********************************
                        ratioName = UThCompositionalMeasures.arConc230Th.getName();
                        BigDecimal activity = readDelimitedTextCell(myFractionData.get(13));
                        myFraction.getCompositionalMeasureByName(ratioName)//
                                .setValue(activity);

                        // column O=14 is activity of 230Th concentration uncertainty
                        // convert 2-sigma to 1-sigma
                        oneSigmaAbs = readDelimitedTextCell(myFractionData.get(14)).
                                divide(new BigDecimal(2.0));
                        myFraction.getCompositionalMeasureByName(ratioName)//
                                .setOneSigma(oneSigmaAbs);

                        // now calculate and save concentration and uncertainty based on legacy Physical Constants
                        ratioName = UThCompositionalMeasures.conc230Th.getName();
                        myFraction.getCompositionalMeasureByName(ratioName)//
                                .setValue(((UThLegacyFraction)myFraction).calculateConcentrationOrUnct230ThFromActivityUsingLegacyLambda(activity.doubleValue()));
                        myFraction.getCompositionalMeasureByName(ratioName)//
                                .setOneSigma(((UThLegacyFraction)myFraction).calculateConcentrationOrUnct230ThFromActivityUsingLegacyLambda(oneSigmaAbs.doubleValue()));
                        // *************************************************************************************    

                        // column P=15 is activity of 226Ra concentration ***********************************
                        ratioName = UThCompositionalMeasures.arConc226Ra.getName();
                        activity = readDelimitedTextCell(myFractionData.get(15));
                        myFraction.getCompositionalMeasureByName(ratioName)//
                                .setValue(activity);

                        // column Q=16 is activity of 226Ra concentration uncertainty
                        // convert 2-sigma to 1-sigma
                        oneSigmaAbs = readDelimitedTextCell(myFractionData.get(16)).
                                divide(new BigDecimal(2.0));
                        myFraction.getCompositionalMeasureByName(ratioName)//
                                .setOneSigma(oneSigmaAbs);
                        
                        // now calculate and save concentration and uncertainty based on legacy Physical Constants
                        ratioName = UThCompositionalMeasures.conc226Ra.getName();
                        myFraction.getCompositionalMeasureByName(ratioName)//
                                .setValue(((UThLegacyFraction)myFraction).calculateConcentrationOrUnct226RaFromActivityUsingLegacyLambda(activity.doubleValue()));
                        myFraction.getCompositionalMeasureByName(ratioName)//
                                .setOneSigma(((UThLegacyFraction)myFraction).calculateConcentrationOrUnct226RaFromActivityUsingLegacyLambda(oneSigmaAbs.doubleValue()));

                        // *************************************************************************************    

                        // column R=17 is ar226Ra_230Thfc
                        ratioName = UThAnalysisMeasures.ar226Ra_230Thfc.getName();
                        myFraction.getLegacyActivityRatioByName(ratioName)//
                                .setValue(readDelimitedTextCell(myFractionData.get(17)));

                        // column S=18 is ar226Ra_230Thfc uncertainty
                        // convert 2-sigma to 1-sigma
                        oneSigmaAbs = readDelimitedTextCell(myFractionData.get(18)).
                                divide(new BigDecimal(2.0));
                        myFraction.getLegacyActivityRatioByName(ratioName)//
                                .setOneSigma(oneSigmaAbs);

                        // column T=19 is concBa in ppm
                        ratioName = UThCompositionalMeasures.concBa.getName();
                        myFraction.getCompositionalMeasureByName(ratioName)//
                                .setValue(readDelimitedTextCell(myFractionData.get(19)).//
                                        movePointLeft(6));

                        // JULY 2017 placeholders
                        ((UThFraction) myFraction).setSpikeCalibrationR230_238IsSecular(false);
                        ((UThFraction) myFraction).setSpikeCalibrationR234_238IsSecular(false);

                        long dateTimeMilliseconds = TIME_IN_MILLISECONDS_FROM_1970_TO_2000;// per Noah 12 June 2017
                        ((UThFraction) myFraction).setDateTimeMillisecondsOfAnalysis(dateTimeMilliseconds);

                        ((UThFraction) myFraction).setR230Th_238Ufc_referenceMaterialName("");
                        ((UThFraction) myFraction).setR234U_238Ufc_referenceMaterialName("");

                        UThFractionReducer.calculateMeasuredAtomRatiosFromLegacyActivityRatios(myFraction);

                    }
                }
            }

            // end of file
            if ((currentSample != null) && (currentAliquot != null)) {
                processSuperSample(project.getSuperSample(), currentSample, currentAliquot);
                currentSample.initFilteredFractionsToAll();
            }

        } catch (IOException iOException) {
        }

        return project;
    }

    private void processSuperSample(SampleInterface superSample, SampleInterface currentSample, AliquotInterface currentAliquot) {
        // this forces population of aliquot fractions
        // check if supersample already has this aliquot
        AliquotInterface existingSuperSampleAliquot
                = superSample.getAliquotByNameForProjectSuperSample(currentSample.getSampleName() + "::" + currentAliquot.getAliquotName());
        if (existingSuperSampleAliquot == null) {
            SampleInterface.copyAliquotIntoSample(superSample, currentSample.getAliquotByNameForProjectSuperSample(currentAliquot.getAliquotName()), new UThReduxAliquot());
        } else {
            for (ETFractionInterface fraction : ((ReduxAliquotInterface) currentAliquot).getAliquotFractions()) {
                if (!((ReduxAliquotInterface) existingSuperSampleAliquot).getAliquotFractions().contains(fraction)) {
                    ((ReduxAliquotInterface) existingSuperSampleAliquot).getAliquotFractions().add(fraction);
                    superSample.getFractions().add(fraction);
                }
            }
        }
        superSample.initFilteredFractionsToAll();
    }

}
