/*
 * ProjectOfLegacySamplesImporterFromTSVFileUseries_Carb.java
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.earthtime.UPb_Redux.ReduxConstants;
import static org.earthtime.UPb_Redux.ReduxConstants.timeInMillisecondsOfYear1950Since1970;
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
import org.earthtime.dataDictionaries.SampleAnalysisTypesEnum;
import org.earthtime.dataDictionaries.SampleTypesEnum;
import org.earthtime.dataDictionaries.UThAnalysisMeasures;
import org.earthtime.dataDictionaries.UThCompositionalMeasures;
import org.earthtime.dataDictionaries.UThFractionationCorrectedIsotopicRatios;
import org.earthtime.exceptions.ETException;
import org.earthtime.fractions.ETFractionInterface;
import org.earthtime.projects.ProjectInterface;
import org.earthtime.projects.projectImporters.AbstractProjectImporterFromLegacyDelimitedTextFile;
import org.earthtime.reduxLabData.ReduxLabData;
import org.earthtime.samples.SampleInterface;

/**
 *
 * @author James F. Bowring This importer services UC Santa Barbara Laser
 * Ablation Split Stream legacy data starting 20 June 2014.
 */
public class ProjectOfLegacySamplesImporterFromTSVFileUseries_Carb_NEW extends AbstractProjectImporterFromLegacyDelimitedTextFile {

    public ProjectOfLegacySamplesImporterFromTSVFileUseries_Carb_NEW(FileDelimiterTypesEnum fileDelimiter) {
        super(fileDelimiter);
    }

    /**
     * Reads tsv files generated from Andrea Dutton's excel file of coral
     * analyses of May 2015
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

            // ignore first line
            // we only allow one source ID for now
            // we allow multiple sampleIDs
            String savedSourceID = "";
            String savedSampleID = "";
            for (int i = 1; i < fractionData.size(); i++) {
                @SuppressWarnings("UseOfObsoleteCollectionType")
                Vector<String> myFractionData = processLegacyTSVLine(fractionData.get(i));
                if (!myFractionData.get(0).equals("0")) {
                    // column A
                    String sourceID = myFractionData.get(0);
                    if (savedSourceID.equalsIgnoreCase("")) {
                        savedSourceID = sourceID;
                    }
                    if (savedSourceID.equalsIgnoreCase(sourceID)) {
                        // column H
                        String sampleID = myFractionData.get(7);
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
                                            SampleAnalysisTypesEnum.USERIES_CARB.getName(), //
                                            ReduxConstants.ANALYSIS_PURPOSE.SingleAge, "UTh");

                                    projectSamples.add(currentSample);

                                    currentAliquot = currentSample.addNewAliquot(sampleID);

                                } catch (BadLabDataException badLabDataException) {
                                } catch (ETException eTException) {
                                }
                            }
                        }

                        if (currentSample != null) {
                            // process fractions
                            UThLegacyFractionI myFraction = new UThLegacyFraction();

                            myFraction.setFractionID(myFractionData.get(8));
                            myFraction.setGrainID(myFractionData.get(8));

                            System.out.println("Reading legacy fraction " + myFraction.getFractionID());

                            // Read in Lagacy MetaData as string in Notes 
                            StringBuilder metaData = new StringBuilder();

                            metaData.append("Replicate = ").append(myFractionData.get(10).trim()).append("\n");

                            metaData.append("Pa/Th age? = ").append(myFractionData.get(29).trim()).append("\n");
                            metaData.append("14C age? = ").append(myFractionData.get(30).trim()).append("\n");
                            metaData.append("Instrument = ").append(myFractionData.get(2).trim()).append("\n");

                            metaData.append("Decay cnsts = ").append(myFractionData.get(3).trim()).append("\n");

                            metaData.append("Reported date (ka) = ").append(myFractionData.get(25).trim()).append("\n");
                            metaData.append("Reported date uncert. (±2_)s = ").append(myFractionData.get(26).trim()).append("\n");
                            metaData.append("Reported d234U initial (‰) = ").append(myFractionData.get(27).trim()).append("\n");
                            metaData.append("Reported d234U uncert. (±2_) = ").append(myFractionData.get(28).trim()).append("\n");

                            metaData.append("PublishedID = ").append(myFractionData.get(9).trim()).append("\n");
                            metaData.append("Location = ").append(myFractionData.get(52).trim()).append("\n");
                            metaData.append("Site = ").append(myFractionData.get(53).trim()).append("\n");
                            metaData.append("Additional Site Info = ").append(myFractionData.get(54).trim()).append("\n");
                            metaData.append("LatitudeWGS84 = ").append(myFractionData.get(55).trim()).append("\n");
                            metaData.append("LongitudeWGS84 = ").append(myFractionData.get(56).trim()).append("\n");
                            metaData.append("LatLongEstimated = ").append(myFractionData.get(57).trim()).append("\n");
                            metaData.append("Tectonic Category = ").append(myFractionData.get(58).trim()).append("\n");
                            metaData.append("Tectonic Category comments = ").append(myFractionData.get(59).trim()).append("\n");
                            metaData.append("Published Uplift rate m/ky = ").append(myFractionData.get(60).trim()).append("\n");
                            metaData.append("Published Uplift rate Unct m/ky = ").append(myFractionData.get(61).trim()).append("\n");
                            metaData.append("Interpreted Uplift rate m/ky = ").append(myFractionData.get(62).trim()).append("\n");
//                            metaData.append("Published Uplift rate Unct m/ky = ").append(myFractionData.get(16).trim()).append("\n");
                            metaData.append("Comments (uplift) = ").append(myFractionData.get(63).trim()).append("\n");
                            metaData.append("Original elevation datum = ").append(myFractionData.get(64).trim()).append("\n");
                            metaData.append("Elevation measurement methodology = ").append(myFractionData.get(65).trim()).append("\n");

                            metaData.append("Published elevation (m) = ").append(myFractionData.get(66).trim()).append("\n");
                            metaData.append("Published elevation Unct (m) = ").append(myFractionData.get(67).trim()).append("\n");
                            metaData.append("Elevation from a different source = ").append(myFractionData.get(68).trim()).append("\n");
                            metaData.append("Elevation from a different source Unct = ").append(myFractionData.get(69).trim()).append("\n");
                            metaData.append("Interpreted Elevation rel to mean sea level (m) = ").append(myFractionData.get(70).trim()).append("\n");
                            metaData.append("Interpreted Elevation Unct (m) = ").append(myFractionData.get(71).trim()).append("\n");
                            metaData.append("Comments Elevation incl Unct = ").append(myFractionData.get(72).trim()).append("\n");

                            metaData.append("Facies = ").append(myFractionData.get(73).trim()).append("\n");
                            metaData.append("Reported as in situ = ").append(myFractionData.get(74).trim()).append("\n");

                            metaData.append("Interpreted as in growth position = ").append(myFractionData.get(75).trim()).append("\n");
                            metaData.append("Taxonomic ID = ").append(myFractionData.get(76).trim()).append("\n");
                            metaData.append("Comments (taxon) = ").append(myFractionData.get(77).trim()).append("\n");

//                            metaData.append("Species = ").append(myFractionData.get(28).trim()).append("\n");
//                            metaData.append("Comments (species) = ").append(myFractionData.get(29).trim()).append("\n");
                            metaData.append("Published assemblage description = ").append(myFractionData.get(78).trim()).append("\n");
                            metaData.append("Published paleowater depth estimate = ").append(myFractionData.get(79).trim()).append("\n");
                            metaData.append("Interpreted paleowater depth estimate = ").append(myFractionData.get(80).trim()).append("\n");

                            metaData.append("Uncertainty in Interpreted paleowater depth estimate = ").append(myFractionData.get(81).trim()).append("\n");
                            metaData.append("Comments-- paleowater depth interpretation = ").append(myFractionData.get(82).trim()).append("\n");

                            // June 2016 per Noah email 29 March 2016
                            /*
                            Yesterday, Andrea, Ken, and I agreed to go with the following approach to re-calculating legacy data 
                            reported with a secular equilibrium tracer.  A more permanent algorithm might be a bit more complicated 
                            -- we will ask around and see if there are two different methods used in the community for reporting the
                            [234/238] activity ratio.  If that's true, we'll need two methods for treating SE data and we'll have to
                            subdivide the SE column into say SE1 and SE2.  

                            Until then, though, you should do this for data reported with an SE tracer:

                            You are given a set of input (reported) and output decay constants and the reported [234/238] and 
                            [230/238] activity ratios.  No matter what set of decay constants were used for the reported data, 
                            use the output (e.g. D3) decay constants to calculate isotope ratios from the reported [234/238] and 
                            [230/238] activity ratios.  For instance, multiply the reported [234/238] activity ratio by 
                            (lambda238_D3 / lambda234_D3).  Use these (234/238) and (230/238) isotope ratios and the output (e.g. D3) 
                            decay constants to calculate the output date and del234Ui.  Note that you never have to use the input
                            (e.g. D1) set of decay constants in this calculation.  
                             */
                            String spikeCalibration = myFractionData.get(4).trim();// june 2017 now for 230/238 ... waiting to hear about 234/238
                            if (spikeCalibration.equalsIgnoreCase("G")) {
                                if (myFractionData.get(3).trim().compareToIgnoreCase("D1") == 0) {
                                    myFraction.useLegacyPhysicalConstantsD1();
                                } else if (myFractionData.get(3).trim().compareToIgnoreCase("D2") == 0) {
                                    myFraction.useLegacyPhysicalConstantsD2();
                                } else if (myFractionData.get(3).trim().compareToIgnoreCase("D3") == 0) {
                                    myFraction.useLegacyPhysicalConstantsD3();
                                }
                            } else /* assume Secular Equilibrium*/ {
                                myFraction.useLegacyPhysicalConstantsD3();
                            }

                            metaData.append("Spike Calib = ").append(spikeCalibration).append("\n"); // ??? two dif ones?

                            metaData.append("Method of mineralogy assessment = ").append(myFractionData.get(48).trim()).append("\n");
                            metaData.append("Published % calcite = ").append(myFractionData.get(49).trim()).append("\n");
                            metaData.append("Interpreted % calcite = ").append(myFractionData.get(50).trim()).append("\n");

                            myFraction.setFractionNotes(metaData.toString());

                            myFraction.setSampleName(currentSample.getSampleName());
                            // in case a sample occurs out of order
                            if (currentSample.getFractions().size() > 0) {
                                myFraction.setAliquotNumber(currentSample.getFractions().get(0).getAliquotNumber());
                            }
                            currentSample.addFraction(myFraction);
                            ((ReduxAliquotInterface) currentAliquot).getAliquotFractions().add(myFraction);

                            // TODO: add uncertainty columns
                            // column 11 is conc232Th in ppb
                            String ratioName = UThCompositionalMeasures.conc232Th.getName();
                            myFraction.getCompositionalMeasureByName(ratioName)//
                                    .setValue(readDelimitedTextCell(myFractionData.get(11)).//
                                            movePointLeft(9));

                            // column 12 is conc232Th uncertainty in ppb
                            // convert 2-sigma to 1-sigma
                            BigDecimal oneSigmaAbs = readDelimitedTextCell(myFractionData.get(12)).
                                    divide(new BigDecimal(2.0)).//
                                    movePointLeft(9);
                            myFraction.getCompositionalMeasureByName(ratioName)//
                                    .setOneSigma(oneSigmaAbs);

                            // column 13 is ar230Th_232Thfc 
                            ratioName = UThAnalysisMeasures.ar230Th_232Thfc.getName();
                            myFraction.getLegacyActivityRatioByName(ratioName)//
                                    .setValue(readDelimitedTextCell(myFractionData.get(13)));

                            // column 14 is ar230Th_232Thfc uncertainty 
                            // convert 2-sigma to 1-sigma
                            oneSigmaAbs = readDelimitedTextCell(myFractionData.get(14)).
                                    divide(new BigDecimal(2.0));
                            myFraction.getLegacyActivityRatioByName(ratioName)//
                                    .setOneSigma(oneSigmaAbs);

                            // column 15 is r232Th_238Ufc * 10^5
                            ratioName = UThFractionationCorrectedIsotopicRatios.r232Th_238Ufc.getName();
                            myFraction.getRadiogenicIsotopeRatioByName(ratioName)//
                                    .setValue(readDelimitedTextCell(myFractionData.get(15)).//
                                            movePointLeft(5));
                            // column 16 is r232Th_238Ufc * 10^5 uncertainty 
                            // convert 2-sigma to 1-sigma
                            oneSigmaAbs = readDelimitedTextCell(myFractionData.get(16)).movePointLeft(5).
                                    divide(new BigDecimal(2.0));
                            myFraction.getLegacyActivityRatioByName(ratioName)//
                                    .setOneSigma(oneSigmaAbs);

                            // column 17 is conc238U in ppm
                            ratioName = UThCompositionalMeasures.conc238U.getName();
                            myFraction.getCompositionalMeasureByName(ratioName)//
                                    .setValue(readDelimitedTextCell(myFractionData.get(17)).//
                                            movePointLeft(6));

                            // column 18 is conc238U uncertainty in ppm
                            // convert 2-sigma to 1-sigma
                            oneSigmaAbs = readDelimitedTextCell(myFractionData.get(18)).
                                    divide(new BigDecimal(2.0)).//
                                    movePointLeft(6);
                            myFraction.getCompositionalMeasureByName(ratioName)//
                                    .setOneSigma(oneSigmaAbs);

                            // column 19 is ar230Th_234Ufc 
                            ratioName = UThAnalysisMeasures.ar230Th_234Ufc.getName();
                            myFraction.getLegacyActivityRatioByName(ratioName)//
                                    .setValue(readDelimitedTextCell(myFractionData.get(19)));

                            // column 20 is ar230Th_234Ufc uncertainty 
                            // convert 2-sigma to 1-sigma
                            oneSigmaAbs = readDelimitedTextCell(myFractionData.get(20)).
                                    divide(new BigDecimal(2.0));
                            myFraction.getLegacyActivityRatioByName(ratioName)//
                                    .setOneSigma(oneSigmaAbs);

                            // column 21 is ar230Th_238Ufc 
                            // June 2017
                            // first get correction factor for ratio
                            BigDecimal ar230Th_238Ufc_correction_factor = readDelimitedTextCell(myFractionData.get(41));
                            // default value
                            if (ar230Th_238Ufc_correction_factor.compareTo(BigDecimal.ZERO) == 0) {
                                ar230Th_238Ufc_correction_factor = BigDecimal.ONE;
                            }
                            ratioName = UThAnalysisMeasures.ar230Th_238Ufc.getName();
                            myFraction.getLegacyActivityRatioByName(ratioName)//
                                    .setValue(readDelimitedTextCell(myFractionData.get(21))
                                            .multiply(ar230Th_238Ufc_correction_factor));

                            // column 22 is ar230Th_238Ufc uncertainty 
                            // June 2017
                            // TODO:  get correction factor for unct - IGNORE FOR NOW PER NOAH
                            // convert 2-sigma to 1-sigma
                            oneSigmaAbs = readDelimitedTextCell(myFractionData.get(22)).
                                    divide(new BigDecimal(2.0));
                            myFraction.getLegacyActivityRatioByName(ratioName)//
                                    .setOneSigma(oneSigmaAbs);

                            // column 23 is ar234U_238Ufc 
                            // June 2017
                            // first get correction factor for ratio
                            BigDecimal ar234U_238Ufc_correction_factor = readDelimitedTextCell(myFractionData.get(43));
                            // default value
                            if (ar234U_238Ufc_correction_factor.compareTo(BigDecimal.ZERO) == 0) {
                                ar234U_238Ufc_correction_factor = BigDecimal.ONE;
                            }
                            ratioName = UThAnalysisMeasures.ar234U_238Ufc.getName();
                            myFraction.getLegacyActivityRatioByName(ratioName)//
                                    .setValue(readDelimitedTextCell(myFractionData.get(23))
                                            .multiply(ar234U_238Ufc_correction_factor));

                            // column 24 is ar234U_238Ufc uncertainty 
                            // June 2017
                            // TODO:  get correction factor for unct - IGNORE FOR NOW PER NOAH
                            // convert 2-sigma to 1-sigma
                            oneSigmaAbs = readDelimitedTextCell(myFractionData.get(24)).
                                    divide(new BigDecimal(2.0));
                            myFraction.getLegacyActivityRatioByName(ratioName)//
                                    .setOneSigma(oneSigmaAbs);

                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd");
                            String dateAnalyzed = myFractionData.get(6).trim();
                            if (dateAnalyzed.length() == 0) {
                                dateAnalyzed = "1950-01-01";
                            }

                            long dateTimeMilliseconds = timeInMillisecondsOfYear1950Since1970;
                            try {
                                dateTimeMilliseconds = dateFormat.parse(dateAnalyzed).getTime();
                            } catch (ParseException parseException) {
                            }

                            ((UThFraction) myFraction).setDateTimeMillisecondsOfAnalysis(dateTimeMilliseconds);

                            myFraction.setDetritalUThModel(ReduxLabData.getInstance().getDefaultDetritalUraniumAndThoriumModel());

                            UThFractionReducer.calculateMeasuredAtomRatiosFromLegacyActivityRatios(myFraction);

                        }

                    }// end of checking if same source
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
