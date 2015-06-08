/*
 * ProjectOfLegacySamplesImporterFromCSVFile_DIBBs_Useries_A.java
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
package org.earthtime.projects.projectImporters.UThProjectImporters;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.UPb_Redux.fractions.Fraction;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbLegacyFraction;
import org.earthtime.UPb_Redux.reduxLabData.ReduxLabData;
import org.earthtime.UPb_Redux.samples.Sample;
import org.earthtime.aliquots.AliquotInterface;
import org.earthtime.dataDictionaries.SampleAnalysisTypesEnum;
import org.earthtime.dataDictionaries.SampleTypesEnum;
import org.earthtime.exceptions.ETException;
import org.earthtime.projects.ProjectInterface;
import org.earthtime.projects.projectImporters.AbstractProjectImporterFromLegacyCSVFile;
import org.earthtime.samples.SampleInterface;

/**
 *
 * @author James F. Bowring This importer services UC Santa Barbara Laser
 * Ablation Split Stream legacy data starting 20 June 2014.
 */
public class ProjectOfLegacySamplesImporterFromCSVFile_DIBBs_Useries_A extends AbstractProjectImporterFromLegacyCSVFile {

    /**
     * Reads csv files generated from Andrea Dutton's excel file of coral
     * analyses of May 2015
     *
     * @param project
     * @param file
     * @return
     * @throws FileNotFoundException
     */
    @Override
    protected ProjectInterface extractProjectFromCSVFile(ProjectInterface project, File file)
            throws FileNotFoundException {

        ArrayList<SampleInterface> projectSamples = new ArrayList<>();

        project.setProjectSamples(projectSamples);

        SampleInterface currentSample = null;
        AliquotInterface currentAliquot = null;

        List<String> fractionData = null;
        try {
            fractionData = Files.readLines(file, Charsets.ISO_8859_1);

            // ignore first 4 lines
            // we only allow one source ID for now
            // we allow multiple sampleIDs
            String savedSourceID = "";
            String savedSampleID = "";
            for (int i = 4; i < fractionData.size(); i++) {
                @SuppressWarnings("UseOfObsoleteCollectionType")
                Vector<String> myFractionData = processLegacyCSVLine(fractionData.get(i));
                if (!myFractionData.get(0).equals("0")) {
                    String sourceID = myFractionData.get(1);
                    if (savedSourceID.equalsIgnoreCase("")) {
                        savedSourceID = sourceID;
                    }
                    if (savedSourceID.equalsIgnoreCase(sourceID)) {

                        String sampleID = myFractionData.get(2);
                        if (sampleID.equalsIgnoreCase("")) {
                            savedSampleID = sampleID;
                        }
                        if (savedSampleID.equalsIgnoreCase(sampleID)) {
                            // existing sample
                        } else {
                            // new sample
                            try {
                                currentSample = new Sample(//
                                        sampleID, //
                                        SampleTypesEnum.LEGACY.getName(), //
                                        SampleAnalysisTypesEnum.USERIES.getName(), //
                                        ReduxLabData.getInstance(), //
                                        ReduxConstants.ANALYSIS_PURPOSE.SingleAge);

                                projectSamples.add(currentSample);

                                currentAliquot = currentSample.addNewAliquot(sampleID);

                            } catch (BadLabDataException badLabDataException) {
                            } catch (ETException eTException) {
                            }

                        }

                        // process fractions
                        Fraction myFraction = new UPbLegacyFraction("NONE");
//
//                        ((UPbFractionI) myFraction).setRatioType("UPb");
//
//                        String fractionNamePart1 = myFractionData.get(1);
//                        String fractionNamePart2 = myFractionData.get(2);
//                        String fractionNamePart3 = myFractionData.get(3);
//                        String fractionID = //
//                                ((fractionNamePart1.length() == 0) ? "" : (fractionNamePart1 + ".")) //
//                                + fractionNamePart2 //
//                                + ((fractionNamePart3.length() == 0) ? "" : ("." + fractionNamePart3));
//
//                        myFraction.setFractionID(fractionID);
//                        myFraction.setGrainID(fractionID);
                    }
                }
            }

        } catch (IOException iOException) {
        }

        return project;
    }

}
