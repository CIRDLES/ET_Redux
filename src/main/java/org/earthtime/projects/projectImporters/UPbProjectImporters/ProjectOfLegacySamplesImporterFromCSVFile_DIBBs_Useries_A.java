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
package org.earthtime.projects.projectImporters.UPbProjectImporters;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Vector;
import org.earthtime.aliquots.AliquotInterface;
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

        try ( //first use a Scanner to get headers of 4 lines
                // http://en.wikipedia.org/wiki/Windows-1252
                Scanner scanner = new Scanner(file, "CP1252")) {
            for (int i = 0; i < 4; i++) {
                scanner.nextLine();
            }

            // now ingest samples
            while (scanner.hasNextLine()) {
                // get content of line and remove quotes
                @SuppressWarnings("UseOfObsoleteCollectionType")
                Vector<String> fractionData = processLegacyCSVLine(scanner.nextLine());

                // suppress blank lines
                if (!fractionData.get(0).equalsIgnoreCase("0")) {
                    System.out.println(fractionData.get(0) + "  " + fractionData.get(2));
                }
            }

            //ensure the underlying stream is always closed
            scanner.close();
        }

        return project;
    }

}
