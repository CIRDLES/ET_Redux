/*
 * SampleImporterFromLAICPMSLegacyCVSFile_UH.java
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
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.fractions.Fraction;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFractionI;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbLegacyFraction;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.PercentDiscordance;
import org.earthtime.dataDictionaries.RadDates;

/**
 *
 * @author James F. Bowring
 */
public class SampleImporterFromLAICPMSLegacyCVSFile_UH //
        extends AbstractSampleImporterFromLegacyCSVFile {
    

    /**
     * Notes:
    1.  Built for Tom Lapin University of Houston
     * @param file 
     * @return 
     * @throws FileNotFoundException 
     */
    @Override
    protected Vector<Fraction> extractFractionsFromFile ( File file )
            throws FileNotFoundException {
        Vector<Fraction> retFractions = new Vector<Fraction>();
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

                    Fraction myFraction = new UPbLegacyFraction( "NONE" );

                    ((UPbFractionI) myFraction).setRatioType( "UPb" );

                    int index = 0;

                    myFraction.setFractionID( myFractionData.get( index ++ ) );
                    myFraction.setGrainID( myFraction.getFractionID() );

                    // concentration U
                    String ratioName = "concU";
                    myFraction.getCompositionalMeasureByName( ratioName )//
                            .setValue( readCSVCell( myFractionData.get( index ++ ) ).//
                            movePointLeft( 6 ) );

                    ratioName = "concTh";
                    myFraction.getCompositionalMeasureByName( ratioName )//
                            .setValue( readCSVCell( myFractionData.get( index ++ ) ).//
                            movePointLeft( 6 ) );

                    //Sets Ratio ThU
                    myFraction.getCompositionalMeasureByName( "rTh_Usample" ).//
                            setValue( readCSVCell( myFractionData.get( index ++ ) ) );

                    // skip column e
                    index ++;

                    //Sets r206_204m ratio 
                    ratioName = "r206_204m";
                    myFraction.getMeasuredRatioByName( ratioName ).//
                            setValue( readCSVCell( myFractionData.get( index ++ ) ) );

                    //Sets r206_238r ratio and sigma
                    ratioName = "r206_238r";
                    myFraction.getRadiogenicIsotopeRatioByName( ratioName ).//
                            setValue( readCSVCell( myFractionData.get( index ++ ) ) );
                    ValueModel ratio = myFraction.getRadiogenicIsotopeRatioByName( ratioName );

                    // 1-sigma from 2-sigma%
                    BigDecimal oneSigmaPct = readCSVCell( myFractionData.get( index ++ ) )//
                            .divide(new BigDecimal( 2.0 ) , //
                                ReduxConstants.mathContext15 );
                    myFraction.getRadiogenicIsotopeRatioByName( ratioName )//
                            .setOneSigma( ValueModel.convertOneSigmaPctToAbsIfRequired( ratio, oneSigmaPct ) );


                    //skip column i
                    index ++;

                    //Sets r207_235r ratio and sigma
                    ratioName = "r207_235r";
                    myFraction.getRadiogenicIsotopeRatioByName( ratioName ).//
                            setValue( readCSVCell( myFractionData.get( index ++ ) ) );
                    ratio = myFraction.getRadiogenicIsotopeRatioByName( ratioName );

                    // 1-sigma from 2-sigma%
                    oneSigmaPct = readCSVCell( myFractionData.get( index ++ ) )//
                            .divide(new BigDecimal( 2.0 ) , //
                                ReduxConstants.mathContext15 );
                    myFraction.getRadiogenicIsotopeRatioByName( ratioName )//
                            .setOneSigma( ValueModel.convertOneSigmaPctToAbsIfRequired( ratio, oneSigmaPct ) );

                    //skip column l
                    index ++;


                    //Sets r207_206r ratio and sigma
                    ratioName = "r207_206r";
                    myFraction.getRadiogenicIsotopeRatioByName( ratioName ).//
                            setValue( readCSVCell( myFractionData.get( index ++ ) ) );
                    ratio = myFraction.getRadiogenicIsotopeRatioByName( ratioName );

                    // 1-sigma from 2-sigma%
                    oneSigmaPct = readCSVCell( myFractionData.get( index ++ ) )//
                            .divide(new BigDecimal( 2.0 ) , //
                                ReduxConstants.mathContext15 );
                    myFraction.getRadiogenicIsotopeRatioByName( ratioName )//
                            .setOneSigma( ValueModel.convertOneSigmaPctToAbsIfRequired( ratio, oneSigmaPct ) );


                    //Sets rhoR206_238r__r207_235r (correlation Coeff)
                    myFraction.getRadiogenicIsotopeRatioByName( "rhoR206_238r__r207_235r" ).//
                            setValue( readCSVCell( myFractionData.get( index ++ ) ) );

                    ((UPbLegacyFraction) myFraction).calculateTeraWasserburgRho();


                    //skip column p
                    index ++;


                    //Sets age206_238r
                    ratioName = RadDates.age206_238r.getName();
                    myFraction.getRadiogenicIsotopeDateByName( ratioName )//
                            .setValue( readCSVCell( myFractionData.get( index ++ ) ).//
                            movePointRight( 6 ) );
                    myFraction.getRadiogenicIsotopeDateByName( ratioName )//
                            .setOneSigma( readCSVCell( myFractionData.get( index ++ ) ).//
                            divide( new BigDecimal( 2.0 ) ).//
                            movePointRight( 6 ) );

                    //skip column s
                    index ++;


                    //Sets age207_235r
                    ratioName = RadDates.age207_235r.getName();
                    myFraction.getRadiogenicIsotopeDateByName( ratioName )//
                            .setValue( readCSVCell( myFractionData.get( index ++ ) ).//
                            movePointRight( 6 ) );
                    myFraction.getRadiogenicIsotopeDateByName( ratioName )//
                            .setOneSigma( readCSVCell( myFractionData.get( index ++ ) ).//
                            divide( new BigDecimal( 2.0 ) ).//
                            movePointRight( 6 ) );

                    //skip column v
                    index ++;

                    //Sets age207_206r
                    ratioName = RadDates.age207_206r.getName();
                    myFraction.getRadiogenicIsotopeDateByName( ratioName )//
                            .setValue( readCSVCell( myFractionData.get( index ++ ) ).//
                            movePointRight( 6 ) );
                    myFraction.getRadiogenicIsotopeDateByName( ratioName )//
                            .setOneSigma( readCSVCell( myFractionData.get( index ++ ) ).//
                            divide( new BigDecimal( 2.0 ) ).//
                            movePointRight( 6 ) );




                    // calculate percentDiscordance
                    ValueModel percentDiscordance = new PercentDiscordance();
                    myFraction.setRadiogenicIsotopeDateByName( RadDates.percentDiscordance, percentDiscordance );
                    percentDiscordance.calculateValue(
                            new ValueModel[]{
                                myFraction.getRadiogenicIsotopeDateByName( RadDates.age206_238r ),
                                myFraction.getRadiogenicIsotopeDateByName( RadDates.age207_206r )},
                            null );

                    retFractions.add( myFraction );

                }

                if ( (myFractionData.get( 0 ).compareToIgnoreCase( "Spot" ) == 0)//
                        //                        || //
                        //                        (myFractionData.get( 0 ).compareToIgnoreCase( "0" ) == 0)
                        ) {
                    
                    // the next line contains the aliquot name
                    myFractionData = processLegacyCSVLine( scanner.nextLine() );
                    aliquotName = myFractionData.get( 0 );
                    // and then the next line(s) contain fraction data or are blank
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

    }

  
}
