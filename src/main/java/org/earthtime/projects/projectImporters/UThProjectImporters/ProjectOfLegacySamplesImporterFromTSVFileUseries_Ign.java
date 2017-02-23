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
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.UTh_Redux.aliquots.UThReduxAliquot;
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
     * standards paper 2017
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
//            String savedSourceID = "none";
            String savedSampleID = "";
            for (int i = 1; i < fractionData.size(); i++) {
                @SuppressWarnings("UseOfObsoleteCollectionType")
                Vector<String> myFractionData = processLegacyTSVLine(fractionData.get(i));
                if (!myFractionData.get(0).equals("0")) {
//                    // column B
//                    String sourceID = myFractionData.get(1);
//                    if (savedSourceID.equalsIgnoreCase("")) {
//                        savedSourceID = sourceID;
//                    }
//                    if (savedSourceID.equalsIgnoreCase(sourceID)) {
                    // column A
                    String sampleID = myFractionData.get(0);
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
                                        SampleAnalysisTypesEnum.USERIES_IGN.getName(), //
                                        ReduxConstants.ANALYSIS_PURPOSE.SingleAge, "UTh");

                                projectSamples.add(currentSample);

                                currentAliquot = currentSample.addNewAliquot(sampleID);

                            } catch (BadLabDataException badLabDataException) {
                            } catch (ETException eTException) {
                            }
                        }
                    }

                    if ((currentSample != null)&& (myFractionData.size() > 1)) {
                        // process fractions
                        UThLegacyFractionI myFraction = new UThLegacyFraction();
                        // column D
                        myFraction.setFractionID(myFractionData.get(1));
                        myFraction.setGrainID(myFractionData.get(1));

                        System.out.println("Reading legacy fraction " + myFraction.getFractionID());

                        // Read in Lagacy MetaData as string in Notes columns E-Z = 4-24
//                            StringBuilder metaData = new StringBuilder();
//                            metaData.append("PublishedID = ").append(myFractionData.get(4).trim()).append("\n");
//                            metaData.append("Location = ").append(myFractionData.get(5).trim()).append("\n");
//                            metaData.append("Site = ").append(myFractionData.get(6).trim()).append("\n");
//                            metaData.append("Additional Site Info = ").append(myFractionData.get(7).trim()).append("\n");
//                            metaData.append("LatitudeWGS84 = ").append(myFractionData.get(8).trim()).append("\n");
//                            metaData.append("LongitudeWGS84 = ").append(myFractionData.get(9).trim()).append("\n");
//                            metaData.append("LatLongEstimated = ").append(myFractionData.get(10).trim()).append("\n");
//                            metaData.append("Tectonic Category = ").append(myFractionData.get(11).trim()).append("\n");
//                            metaData.append("Tectonic Category comments = ").append(myFractionData.get(12).trim()).append("\n");
//                            metaData.append("Published Uplift rate m/ky = ").append(myFractionData.get(13).trim()).append("\n");
//                            metaData.append("Published Uplift rate m/ky = ").append(myFractionData.get(14).trim()).append("\n");
//                            metaData.append("Interpreted Uplift rate m/ky = ").append(myFractionData.get(15).trim()).append("\n");
//                            metaData.append("Published Uplift rate Unct m/ky = ").append(myFractionData.get(16).trim()).append("\n");
//                            metaData.append("Comments (uplift) = ").append(myFractionData.get(17).trim()).append("\n");
//                            metaData.append("Original elevation datum = ").append(myFractionData.get(18).trim()).append("\n");
//                            metaData.append("Published elevation (m) = ").append(myFractionData.get(19).trim()).append("\n");
//                            metaData.append("Published elevation Unct (m) = ").append(myFractionData.get(20).trim()).append("\n");
//                            metaData.append("Elevation from a different source = ").append(myFractionData.get(21).trim()).append("\n");
//                            metaData.append("Elevation from a different source Unct = ").append(myFractionData.get(22).trim()).append("\n");
//                            metaData.append("Interpreted Elevation (m) = ").append(myFractionData.get(23).trim()).append("\n");
//                            metaData.append("Interpreted Elevation Unct (m) = ").append(myFractionData.get(24).trim()).append("\n");
//                            metaData.append("Comments Elevation Unct = ").append(myFractionData.get(25).trim()).append("\n");
//                            // columns AA-AO = 26-40
//                            metaData.append("Facies = ").append(myFractionData.get(26).trim()).append("\n");
//                            metaData.append("In situ or in growth position (strict) = ").append(myFractionData.get(27).trim()).append("\n");
//                            metaData.append("Species = ").append(myFractionData.get(28).trim()).append("\n");
//                            metaData.append("Comments (species) = ").append(myFractionData.get(29).trim()).append("\n");
//                            metaData.append("Published assemblage description = ").append(myFractionData.get(30).trim()).append("\n");
//                            metaData.append("Published paleodepth interpretation = ").append(myFractionData.get(31).trim()).append("\n");
//                            metaData.append("Replicate = ").append(myFractionData.get(32).trim()).append("\n");
//                            metaData.append("Pa/Th age? = ").append(myFractionData.get(33).trim()).append("\n");
//                            metaData.append("14C age? = ").append(myFractionData.get(34).trim()).append("\n");
//                            metaData.append("Instrument = ").append(myFractionData.get(35).trim()).append("\n");
//                            metaData.append("Decay cnsts = ").append(myFractionData.get(36).trim()).append("\n");
//                            String spikeCalibration = myFractionData.get(37).trim();
//                            if (spikeCalibration.equalsIgnoreCase("G")) {
//                                if (myFractionData.get(36).trim().compareToIgnoreCase("D1") == 0) {
//                                    myFraction.useLegacyPhysicalConstantsD1();
//                                } else if (myFractionData.get(36).trim().compareToIgnoreCase("D2") == 0) {
//                                    myFraction.useLegacyPhysicalConstantsD2();
//                                } else if (myFractionData.get(36).trim().compareToIgnoreCase("D3") == 0) {
//                                    myFraction.useLegacyPhysicalConstantsD3();
//                                }
//                            } else /* assume Secular Equilibrium*/ {
                        myFraction.useLegacyPhysicalConstantsD3();
//                            }

//                            metaData.append("Spike Calib = ").append(spikeCalibration).append("\n");
//                            metaData.append("Published % calcite = ").append(myFractionData.get(38).trim()).append("\n");
//                            metaData.append("Interpreted % calcite = ").append(myFractionData.get(39).trim()).append("\n");
//                            metaData.append("Method of mineralogy assessment = ").append(myFractionData.get(40).trim()).append("\n");
//
//                            myFraction.setFractionNotes(metaData.toString());
                        myFraction.setSampleName(currentSample.getSampleName());
                        // in case a sample occurs out of order
                        if (currentSample.getFractions().size() > 0) {
                            myFraction.setAliquotNumber(currentSample.getFractions().get(0).getAliquotNumber());
                        }
                        currentSample.addFraction(myFraction);
                        ((ReduxAliquotInterface) currentAliquot).getAliquotFractions().add(myFraction);

                        // column C=2 is conc232Th in ppm
                        String ratioName = UThCompositionalMeasures.conc232Th.getName();
                        myFraction.getCompositionalMeasureByName(ratioName)//
                                .setValue(readDelimitedTextCell(myFractionData.get(2)).//
                                        movePointLeft(6));

                        // column D=3 is conc232Th uncertainty in ppm
                        // convert 2-sigma to 1-sigma
                        BigDecimal oneSigmaAbs = readDelimitedTextCell(myFractionData.get(3)).
                                divide(new BigDecimal(2.0)).//
                                movePointLeft(6);
                        myFraction.getCompositionalMeasureByName(ratioName)//
                                .setOneSigma(oneSigmaAbs);

                        // column E=4 is conc238U in ppm
                        ratioName = UThCompositionalMeasures.conc238U.getName();
                        myFraction.getCompositionalMeasureByName(ratioName)//
                                .setValue(readDelimitedTextCell(myFractionData.get(4)).//
                                        movePointLeft(6));

                        // column F=5 is conc238U uncertainty in ppm
                        // convert 2-sigma to 1-sigma
                        oneSigmaAbs = readDelimitedTextCell(myFractionData.get(5)).
                                divide(new BigDecimal(2.0)).//
                                movePointLeft(6);
                        myFraction.getCompositionalMeasureByName(ratioName)//
                                .setOneSigma(oneSigmaAbs);

                        // column G=6 is ar238U_232Thfc
                        ratioName = UThAnalysisMeasures.ar238U_232Thfc.getName();
                        myFraction.getLegacyActivityRatioByName(ratioName)//
                                .setValue(readDelimitedTextCell(myFractionData.get(6)));

                        // column H=7 is r238U_232Thfc uncertainty
                        // convert 2-sigma to 1-sigma
                        oneSigmaAbs = readDelimitedTextCell(myFractionData.get(7)).
                                divide(new BigDecimal(2.0));
                        myFraction.getLegacyActivityRatioByName(ratioName)//
                                .setOneSigma(oneSigmaAbs);

                        // column i=8 is ar230Th_232Thfc 
                        ratioName = UThAnalysisMeasures.ar230Th_232Thfc.getName();
                        myFraction.getLegacyActivityRatioByName(ratioName)//
                                .setValue(readDelimitedTextCell(myFractionData.get(8)));

                        // column 9 is r238U_232Thfc uncertainty
                        // convert 2-sigma to 1-sigma
                        oneSigmaAbs = readDelimitedTextCell(myFractionData.get(9)).
                                divide(new BigDecimal(2.0));
                        myFraction.getLegacyActivityRatioByName(ratioName)//
                                .setOneSigma(oneSigmaAbs);

                        UThFractionReducer.calculateMeasuredAtomRatiosFromLegacyActivityRatios(myFraction);
                    }

//                    }// end of checking if same source
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
