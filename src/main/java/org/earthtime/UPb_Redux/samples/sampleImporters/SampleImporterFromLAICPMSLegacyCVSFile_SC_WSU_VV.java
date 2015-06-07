/*
 * SampleImporterFromLAICPMSLegacyCVSFile_SC_WSU_VV.java
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
import java.math.BigDecimal;
import java.util.Scanner;
import java.util.Vector;
import org.earthtime.UPb_Redux.fractions.FractionI;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFractionI;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbLegacyFraction;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.PercentDiscordance;
import org.earthtime.dataDictionaries.RadDates;

/**
 *
 * @author James F. Bowring
 */
public class SampleImporterFromLAICPMSLegacyCVSFile_SC_WSU_VV //
        extends AbstractSampleImporterFromLegacyCSVFile {

    /**
     * Notes:
    1.  Built for Victor Valencia
     * @param file 
     * @return
     * @throws FileNotFoundException  
     */
    @Override
    protected Vector<FractionI> extractFractionsFromFile ( File file )
            throws FileNotFoundException {
        Vector<FractionI> retFractions = new Vector<>();
        boolean readingFractions = false;

        //first use a Scanner to get each line
        Scanner scanner = new Scanner( file );
        try {
            while (scanner.hasNextLine()) {
                // get content of line
                Vector<String> myFractionData = processLegacyCSVLine( scanner.nextLine() );

                // determine content of line where a zero for fraction name = blank line

                if ( readingFractions &&  ! myFractionData.get( 0 ).equalsIgnoreCase( "0" ) ) {
                    // process fraction line
                    System.out.println( "Reading Fraction " + myFractionData.get( 0 ) );

                    FractionI myFraction = new UPbLegacyFraction( "NONE" );

                    ((UPbFractionI) myFraction).setRatioType( "UPb" );

                    int index = 0;

                    myFraction.setFractionID( myFractionData.get( index ++ ) );
                    myFraction.setGrainID( myFraction.getFractionID() );


                    //Sets r207_235r ratio and sigma
                    String ratioName = "r207_235r";
                    myFraction.getRadiogenicIsotopeRatioByName( ratioName ).//
                            setValue( readCSVCell( myFractionData.get( index ++ ) ) );
                    ValueModel ratio = myFraction.getRadiogenicIsotopeRatioByName( ratioName );

                    // 1-sigma
                    BigDecimal oneSigmaAbs = readCSVCell( myFractionData.get( index ++ ) );
                    myFraction.getRadiogenicIsotopeRatioByName( ratioName ).//
                            setOneSigma( oneSigmaAbs );

                    //Sets r206_238r ratio and sigma
                    ratioName = "r206_238r";
                    myFraction.getRadiogenicIsotopeRatioByName( ratioName ).//
                            setValue( readCSVCell( myFractionData.get( index ++ ) ) );
                    ratio = myFraction.getRadiogenicIsotopeRatioByName( ratioName );

                    // 1-sigma
                    oneSigmaAbs = readCSVCell( myFractionData.get( index ++ ) );
                    myFraction.getRadiogenicIsotopeRatioByName( ratioName ).//
                            setOneSigma( oneSigmaAbs );

                    //Sets r206_238r ratio and sigma
                    ratioName = "r207_206r";
                    myFraction.getRadiogenicIsotopeRatioByName( ratioName ).//
                            setValue( readCSVCell( myFractionData.get( index ++ ) ) );
                    ratio = myFraction.getRadiogenicIsotopeRatioByName( ratioName );

                    // 1-sigma
                    oneSigmaAbs = readCSVCell( myFractionData.get( index ++ ) );
                    myFraction.getRadiogenicIsotopeRatioByName( ratioName ).//
                            setOneSigma( oneSigmaAbs );


                    // skip col H
                    index ++;

                    //Sets age207_235r
                    ratioName = RadDates.age207_235r.getName();
                    myFraction.getRadiogenicIsotopeDateByName( ratioName )//
                            .setValue( readCSVCell( myFractionData.get( index ++ ) ).//
                            movePointRight( 6 ) );
                    myFraction.getRadiogenicIsotopeDateByName( ratioName )//
                            .setOneSigma( readCSVCell( myFractionData.get( index ++ ) ).//
                            movePointRight( 6 ) );

                    //Sets age206_238r
                    ratioName = RadDates.age206_238r.getName();
                    myFraction.getRadiogenicIsotopeDateByName( ratioName )//
                            .setValue( readCSVCell( myFractionData.get( index ++ ) ).//
                            movePointRight( 6 ) );
                    myFraction.getRadiogenicIsotopeDateByName( ratioName )//
                            .setOneSigma( readCSVCell( myFractionData.get( index ++ ) ).//
                            movePointRight( 6 ) );

                    //Sets age207_206r
                    ratioName = RadDates.age207_206r.getName();
                    myFraction.getRadiogenicIsotopeDateByName( ratioName )//
                            .setValue( readCSVCell( myFractionData.get( index ++ ) ).//
                            movePointRight( 6 ) );
                    myFraction.getRadiogenicIsotopeDateByName( ratioName )//
                            .setOneSigma( readCSVCell( myFractionData.get( index ++ ) ).//
                            movePointRight( 6 ) );

                    //Sets Ratio ThU
                    myFraction.getCompositionalMeasureByName( "rTh_Usample" ).//
                            setValue( readCSVCell( myFractionData.get( index ++ ) ) );

                    ratioName = "concU";
                    myFraction.getCompositionalMeasureByName( ratioName )//
                            .setValue( readCSVCell( myFractionData.get( index ++ ) ).//
                            movePointLeft( 6 ) );


                    // calculate percentDiscordance
                    ValueModel percentDiscordance = new PercentDiscordance();
                    myFraction.setRadiogenicIsotopeDateByName( RadDates.percentDiscordance, percentDiscordance );
                    percentDiscordance.calculateValue(
                            new ValueModel[]{
                                myFraction.getRadiogenicIsotopeDateByName( RadDates.age206_238r ),
                                myFraction.getRadiogenicIsotopeDateByName( RadDates.age207_206r )},
                            null );

                    retFractions.add( myFraction );


                    // skip columns Q,R,S,T,U,V,W,X,Y
                    index += 9;

                    //Sets rhoR206_238r__r207_235r (correlation Coeff)
                    myFraction.getRadiogenicIsotopeRatioByName( "rhoR206_238r__r207_235r" ).//
                            setValue( readCSVCell( myFractionData.get( index ++ ) ) );

                    ((UPbLegacyFraction) myFraction).calculateTeraWasserburgRho();
                    
                    
                    // april 2012
                    //skip columns AA-AS to get to best age
                    index += 19;
                    
                    //Sets bestAge
                    ratioName = RadDates.bestAge.getName();
                    myFraction.getRadiogenicIsotopeDateByName( ratioName )//
                            .setValue( readCSVCell( myFractionData.get( index ++ ) ).//
                            movePointRight( 6 ) );
                    myFraction.getRadiogenicIsotopeDateByName( ratioName )//
                            .setOneSigma( readCSVCell( myFractionData.get( index ++ ) ).//
                            movePointRight( 6 ) );


                }

                if ( (myFractionData.get( 0 ).compareToIgnoreCase( "All analyses" ) == 0)//
//                        || //
//                        (myFractionData.get( 0 ).compareToIgnoreCase( "0" ) == 0)
                        ) {
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
    public static void writeAndOpenCSVFileOfLegacyDataSampleFieldNames () {
////        String fieldNames = TemplatesForCsvImport.LAICPMSLegacyDataSampleFieldNames_SC_WSU_vB;
////        File CSVFile = new File( "LAICPMSLegacySampleFieldNamesTemplateWSU_vB" + ".csv" );
////        CSVFile.delete();
////        PrintWriter outputWriter = null;
////        try {
////            outputWriter = new PrintWriter( new FileWriter( CSVFile ) );
////
////            outputWriter.println( "LEGACY LA-ICP MS Version B (Single Collector from WSU) DATA SAMPLE FIELD NAMES FOR IMPORT INTO U-Pb_Redux\n" );
////            outputWriter.println( "AnalysisName,IsotopicRatios,,,,,,,Dates (Ma)" );
////            outputWriter.println( fieldNames );
////
////            outputWriter.close();
////
////        } catch (IOException iOException) {
////        }
////
////        try {
////            BrowserControl.displayURL( CSVFile.getCanonicalPath() );
////        } catch (IOException ex) {
//////            ex.printStackTrace();
////        }

    }
}
