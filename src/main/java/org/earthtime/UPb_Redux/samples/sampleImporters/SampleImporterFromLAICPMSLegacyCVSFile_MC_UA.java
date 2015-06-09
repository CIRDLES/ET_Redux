/*
 * SampleImporterFromLAICPMSLegacyCVSFile_MC_UA.java
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
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.Scanner;
import java.util.Vector;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.exceptions.BadImportedCSVLegacyFileException;
import org.earthtime.UPb_Redux.fractions.FractionI;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFractionI;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbLegacyFraction;
import org.earthtime.UPb_Redux.utilities.BrowserControl;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.PercentDiscordance;
import org.earthtime.dataDictionaries.MeasuredRatios;
import org.earthtime.dataDictionaries.RadDates;
import org.earthtime.dataDictionaries.SampleTypesEnum;
import org.earthtime.dataDictionaries.TemplatesForCsvImport;
import org.earthtime.fractions.ETFractionInterface;


/**
 *
 * @author James F. Bowring
 */
public class SampleImporterFromLAICPMSLegacyCVSFile_MC_UA //
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

        Vector<ETFractionInterface> retFractions = new Vector<>();

        boolean readingFractions = false;

        //first use a Scanner to get each line
        Scanner scanner = new Scanner( file );
        try {
            while (scanner.hasNextLine()) {
                // get content of line
                Vector<String> myFractionData = processLegacyCSVLine( scanner.nextLine() ); 

                // determine content of line where a zero for fraction name = blank line

                if ( readingFractions && !myFractionData.get( 0).equalsIgnoreCase( "0")) {
                    // process fraction line
                    System.out.println( "Reading Fraction " + myFractionData.get( 0 ) );

                    FractionI myFraction = new UPbLegacyFraction( "NONE" );

                    myFraction.setRatioType( "UPb" );

                    int index = 0;

                    myFraction.setFractionID( myFractionData.get( index ++ ) );
                    myFraction.setGrainID( myFraction.getFractionID() );

                    //Sets ConcU
                    myFraction.getCompositionalMeasureByName( "concU" ).//
                            setValue( readCSVCell( myFractionData.get( index ++ ) ).//
                            movePointLeft( 6 ) );

                    //Sets r206_204m
                    myFraction.getMeasuredRatioByName( MeasuredRatios.r206_204m.getName() ).//
                            setValue( readCSVCell( myFractionData.get( index ++ ) ) );

                    //Sets Ratio ThU from U/Th
                    try {
                        myFraction.getCompositionalMeasureByName( "rTh_Usample" ).//
                                setValue(BigDecimal.ONE.divide(//
                                readCSVCell( myFractionData.get( index ++ )), //
                                ReduxConstants.mathContext15 ) );
                    } catch (Exception e) {
                        myFraction.getCompositionalMeasureByName( "rTh_Usample" ).//
                                setValue( BigDecimal.ZERO);
                    }

                    //Sets r207_206r ratio and sigma from 206/207
                    String ratioName = "r207_206r";
                    // check for zero
                    BigDecimal readRatio = readCSVCell( myFractionData.get( index ++ ) );
                    if ( readRatio.compareTo( BigDecimal.ZERO ) != 0 ) {
                        myFraction.getRadiogenicIsotopeRatioByName( ratioName ).//
                                setValue(BigDecimal.ONE.divide(readRatio, //
                                ReduxConstants.mathContext15 ) );
                    } else {
                        myFraction.getRadiogenicIsotopeRatioByName( ratioName ).//
                                setValue( BigDecimal.ZERO );
                    }
                    ValueModel ratio = myFraction.getRadiogenicIsotopeRatioByName( ratioName );

                    // note percent sigma is correct regardless of ratio denominator / numerator order
                    BigDecimal oneSigmaPct = readCSVCell( myFractionData.get( index ++ ) );
                    myFraction.getRadiogenicIsotopeRatioByName( ratioName )//
                            .setOneSigma( ValueModel.convertOneSigmaPctToAbsIfRequired( ratio, oneSigmaPct ) );

                    //Sets r207_235r ratio and sigma
                    ratioName = "r207_235r";
                    myFraction.getRadiogenicIsotopeRatioByName( ratioName ).//
                            setValue( readCSVCell( myFractionData.get( index ++ ) ) );
                    ratio = myFraction.getRadiogenicIsotopeRatioByName( ratioName );

                    // 1-sigma
                    oneSigmaPct = readCSVCell( myFractionData.get( index ++ ) );
                    myFraction.getRadiogenicIsotopeRatioByName( ratioName ).//
                            setOneSigma( ValueModel.convertOneSigmaPctToAbsIfRequired( ratio, oneSigmaPct ) );

                    //Sets r206_238r ratio and sigma
                    ratioName = "r206_238r";
                    myFraction.getRadiogenicIsotopeRatioByName( ratioName ).//
                            setValue( readCSVCell( myFractionData.get( index ++ ) ) );
                    ratio = myFraction.getRadiogenicIsotopeRatioByName( ratioName );

                    // 1-sigma
                    oneSigmaPct = readCSVCell( myFractionData.get( index ++ ) );
                    myFraction.getRadiogenicIsotopeRatioByName( ratioName ).//
                            setOneSigma( ValueModel.convertOneSigmaPctToAbsIfRequired( ratio, oneSigmaPct ) );

                    //Sets rhoR206_238r__r207_235r
                    myFraction.getRadiogenicIsotopeRatioByName( "rhoR206_238r__r207_235r" ).//
                            setValue( readCSVCell( myFractionData.get( index ++ ) ) );

                    ((UPbFractionI)myFraction).calculateTeraWasserburgRho();

                    //Sets age206_238r
                    ratioName = RadDates.age206_238r.getName();
                    myFraction.getRadiogenicIsotopeDateByName( ratioName )//
                            .setValue( readCSVCell( myFractionData.get( index ++ ) ).//
                            movePointRight( 6 ) );
                    myFraction.getRadiogenicIsotopeDateByName( ratioName )//
                            .setOneSigma( readCSVCell( myFractionData.get( index ++ ) ).//
                            movePointRight( 6 ) );

                    //Sets age207_235r
                    ratioName = RadDates.age207_235r.getName();
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

                    //Sets bestAge
                    ratioName = RadDates.bestAge.getName();
                    myFraction.getRadiogenicIsotopeDateByName( ratioName )//
                            .setValue( readCSVCell( myFractionData.get( index ++ ) ).//
                            movePointRight( 6 ) );
                    myFraction.getRadiogenicIsotopeDateByName( ratioName )//
                            .setOneSigma( readCSVCell( myFractionData.get( index ++ ) ).//
                            movePointRight( 6 ) );

                    // calculate percentDiscordance
                    ValueModel percentDiscordance = new PercentDiscordance();
                    myFraction.setRadiogenicIsotopeDateByName( RadDates.percentDiscordance, percentDiscordance );
                    percentDiscordance.calculateValue(
                            new ValueModel[]{
                                myFraction.getRadiogenicIsotopeDateByName( RadDates.age206_238r ),
                                myFraction.getRadiogenicIsotopeDateByName( RadDates.age207_206r )},
                            null );

                    if (!retFractions.contains( myFraction)){
                        // TODO: Handle grainids for multiple grains
                        retFractions.add( myFraction );
                    }
                    

                }

                if ( myFractionData.get( 0 ).compareToIgnoreCase( SampleTypesEnum.ANALYSIS.getName() ) == 0 ) {
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
        String fieldNames = TemplatesForCsvImport.LAICPMSLegacyDataSampleFieldNames_MC_UA;
        File CSVFile = new File( "LAICPMSLegacySampleFieldNamesTemplateUA" + ".csv" );
        CSVFile.delete();
        PrintWriter outputWriter = null;
        try {
            outputWriter = new PrintWriter( new FileWriter( CSVFile ) );

            outputWriter.println( "LEGACY LA-ICP MS (Multi-Collector from UA) DATA SAMPLE FIELD NAMES FOR IMPORT INTO U-Pb_Redux\n" );
            outputWriter.println( "AnalysisName,,,,,,IsotopicRatios,,,,,ApparentIsotopicDates (Ma)" );
            outputWriter.println( fieldNames );

            outputWriter.close();

        } catch (IOException iOException) {
        }

        try {
            BrowserControl.displayURL( CSVFile.getCanonicalPath() );
        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
}
