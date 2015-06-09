/*
 * SampleManagerForLASS_LegacySamples.java
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
package org.earthtime.UPb_Redux.samples.sampleImporters;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Vector;
import org.earthtime.UPb_Redux.exceptions.BadImportedCSVLegacyFileException;
import org.earthtime.fractions.ETFractionInterface;


/**
 *
 * @author James F. Bowring
 */
public class SampleManagerForLASS_LegacySamples //
        extends AbstractSampleImporterFromLegacyCSVFile {

    /**
     * Notes:										
    1.  Uncertainties for individual analyses are reported at the 1-sigma level, and include only measurement errors. 										
    2.  Systematic errors are shown to the right of each set of analyses (206Pb/238U, 206Pb/207Pb), with uncertainties at the 2-sigma level.										
    3.  U concentration and U/Th are calibrated relative to Sri Lanka zircon standard and NIST SRM 610, and are accurate to ~20%.										
    4.  Common Pb correction is from measured 204Pb.										
    5.  Common Pb composition interpreted from Stacey and Kramers (1975).										
    6.  Common Pb composition assigned uncertainties of 1.0 for 206Pb/204Pb, 0.3 for 207Pb/204Pb, and 2.0 for 208Pb/204Pb.										
    7.  U/Pb and 206Pb/207Pb fractionation is calibrated relative to fragments of a large Sri Lanka zircon of 564 +/- 4 Ma (2-sigma).   										
    8.  U decay constants and composition as follows: 238U = 9.8485 x 10-10, 235U = 1.55125 x 10-10, 238U/235U = 137.88										
    9.  Best age is 206Pb/238U age for younger grains and 206Pb/207Pb age for older grains.										
    Division ranges from 800 and 1400 Ma, and is selected to avoid dividing clusters of analyses.									
     * @param file
     * @return 
     * @throws FileNotFoundException 
     * @throws BadImportedCSVLegacyFileException  
     */
    @Override
    protected Vector<ETFractionInterface> extractFractionsFromFile ( File file )
            throws FileNotFoundException, BadImportedCSVLegacyFileException {

        return null;
    }

//    /**
//     * 
//     */
//    public static void writeAndOpenCSVFileOfLegacyDataSampleFieldNames () {
//        String fieldNames = TemplatesForCsvImport.LAICPMSLegacyDataSampleFieldNames_MC_UA;
//        File CSVFile = new File( "LAICPMSLegacySampleFieldNamesTemplateUA" + ".csv" );
//        CSVFile.delete();
//        PrintWriter outputWriter = null;
//        try {
//            outputWriter = new PrintWriter( new FileWriter( CSVFile ) );
//
//            outputWriter.println( "LEGACY LA-ICP MS (Multi-Collector from UA) DATA SAMPLE FIELD NAMES FOR IMPORT INTO U-Pb_Redux\n" );
//            outputWriter.println( "AnalysisName,,,,,,IsotopicRatios,,,,,ApparentIsotopicDates (Ma)" );
//            outputWriter.println( fieldNames );
//
//            outputWriter.close();
//
//        } catch (IOException iOException) {
//        }
//
//        try {
//            BrowserControl.displayURL( CSVFile.getCanonicalPath() );
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
//
//    }
}
