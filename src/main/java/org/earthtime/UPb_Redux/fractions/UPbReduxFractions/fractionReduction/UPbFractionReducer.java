/*
 * UPbFractionReducer.java
 *
 * Created on September 9, 2007, 1:29 PM
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
package org.earthtime.UPb_Redux.fractions.UPbReduxFractions.fractionReduction;

import Jama.Matrix;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.earthtime.Tripoli.dataModels.sessionModels.SessionCorrectedUnknownsSummary;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.fractions.FractionI;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFraction;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFractionI;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbLAICPMSFraction;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.Age206_238r;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.Age206_238r_Th;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.Age207_206r;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.Age207_206r_Pa;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.Age207_206r_Th;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.Age207_206r_ThPa;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.Age207_235r;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.Age207_235r_Pa;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.Age208_232r;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.AlphaPb;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.AlphaU;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.BlankPbGramsMol;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.ConcPb206_ib;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.ConcPb206_r;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.ConcPb206_rib;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.ConcPb_i;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.ConcPb_ib;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.ConcPb_r;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.ConcPb_rib;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.ConcTh;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.ConcU;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.InitCommonPbMass;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.MassPb_rib;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.MassU;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.MolPb204b;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.MolPb204c;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.MolPb204tc;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.MolPb205t;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.MolPb206b;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.MolPb206c;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.MolPb206r;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.MolPb206r_Th;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.MolPb206s;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.MolPb207b;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.MolPb207c;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.MolPb207r;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.MolPb207r_Pa;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.MolPb207s;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.MolPb208b;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.MolPb208c;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.MolPb208r;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.MolPb208s;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.MolTh232s;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.MolU233t;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.MolU235b;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.MolU235s;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.MolU235t;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.MolU236t;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.MolU238b;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.MolU238s;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.MolU238t;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.MolsU;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.PercentDiscordance;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.R204_205fc;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.R204_206s;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.R204_207s;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.R206_204r;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.R206_204tfc;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.R206_238r;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.R206_238r_Th;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.R207_206r;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.R207_206r_Pa;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.R207_206r_Th;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.R207_206r_ThPa;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.R207_206s;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.R207_235r;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.R207_235r_Pa;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.R208_204s;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.R208_206r;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.R208_232r;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.R232_204s;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.R233_235oc;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.R233_236oc;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.R235_204s;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.R235_207s;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.R238_204s;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.R238_206s;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.R238_207s;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.R238_235oc;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.R238_236oc;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.RTh_Usample;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.RadToCommonPb206;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.RadToCommonPb207;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.RadToCommonPb208;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.RadToCommonTotal;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.RhoR202_204s__r208_204s;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.RhoR207_206s__r204_206s;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.RhoR235_204s__r207_204s;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.RhoR238_204s__r206_204s;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.RhoR238_206s__r207_206s;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.TotCommonPbMass;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.TotRadiogenicPbMass;
import org.earthtime.dataDictionaries.AnalysisMeasures;
import org.earthtime.dataDictionaries.MeasuredRatios;
import org.earthtime.dataDictionaries.RadDates;
import org.earthtime.dataDictionaries.RadRatios;
import org.earthtime.dataDictionaries.RadRatiosPbcCorrected;
import org.earthtime.dataDictionaries.TracerUPbRatiosAndConcentrations;
import org.earthtime.fractions.ETFractionInterface;
import org.earthtime.fractions.fractionReduction.FractionReducer;
import org.earthtime.matrices.matrixModels.AbstractMatrixModel;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataModels.physicalConstantsModels.PhysicalConstantsModel;
import org.earthtime.reduxLabData.ReduxLabData;

/**
 *
 * @author James F. Bowring
 */
public class UPbFractionReducer extends FractionReducer {

    private static UPbFractionReducer instance;
    // Instance variablesInOrder
//    // 0. lab constants
//    private static ValueModel lambda230;
//    private static ValueModel lambda231;
//    private static ValueModel lambda232;
//    private static ValueModel lambda234;
//    private static ValueModel lambda235;
//    private static ValueModel lambda238;
//    private static ValueModel gmol204;
//    private static ValueModel gmol206;
//    private static ValueModel gmol207;
//    private static ValueModel gmol208;
//    private static ValueModel gmol235;
//    private static ValueModel gmol238;
    // 1. Pb
    // 1a. tracer *****************************************************
    private static ValueModel alphaPb;
    private static ValueModel molPb205t;
    // 1b. Pb  Blank *************************************************
    private static ValueModel r204_205fc;
    private static ValueModel molPb204tc;
    private static ValueModel molPb204b;
    private static ValueModel molPb206b;
    private static ValueModel molPb207b;
    private static ValueModel molPb208b;
    private static ValueModel molPb204c;
    private static ValueModel molPb206c;
    private static ValueModel molPb207c;
    private static ValueModel molPb208c;
    private static ValueModel blankPbGramsMol;
    private static ValueModel blankPbMass;
    // 1c radiogenic Pb, sample Pb *******************************************
    private static ValueModel molPb206r;
    private static ValueModel molPb207r;
    private static ValueModel molPb208r;
    private static ValueModel molPb206s;
    private static ValueModel molPb207s;
    private static ValueModel molPb208s;
    // 2. U *******************************************************************
    //  private static ValueModel r270_265m;
    // 2a. tracer
    private static ValueModel molU235b;
    private static ValueModel molU238b;
    private static ValueModel molU235t;
    private static ValueModel molU236t;
    private static ValueModel molU238t;
    // 2b. sample U *********************************************************
    private static ValueModel molU233t;
    private static ValueModel molU235s;
    private static ValueModel molU238s;
    // 3. Ages
    // 3a. radiogenic isotope ratios
    private static ValueModel r206_238r;
    private static ValueModel r207_235r;
    private static ValueModel r207_206r;
    private static ValueModel r206_204r;
    private static ValueModel r208_206r;
    private static ValueModel r208_232r;
    private static ValueModel r206_238r_Th;
    // 3b. radiogenic isotope ages ********************************************
    private static ValueModel age206_238r;
    private static ValueModel age207_235r;
    private static ValueModel age207_206r;
    private static ValueModel age208_232r;
    // 3c. Th and Pa correction ***********************************************
    private static ValueModel age206_238r_Th;
    private static ValueModel molTh232s;
    private static ValueModel molPb206r_Th;
    private static ValueModel r207_206r_Th;
    private static ValueModel age207_206r_Th;
    private static ValueModel age207_235r_Pa;
    private static ValueModel molPb207r_Pa;
    private static ValueModel r207_206r_Pa;
    private static ValueModel r207_235r_Pa;
    private static ValueModel age207_206r_Pa;
    private static ValueModel r207_206r_ThPa;
    private static ValueModel age207_206r_ThPa;
    private static ValueModel molsU;
    // 4. Isochron Ratios ********************************************
    private static ValueModel r206_204tfc;
    private static ValueModel r204_206s;
    private static ValueModel r238_204s;
    private static ValueModel rhoR238_204s__r206_204s;
    private static ValueModel r235_204s;
    private static ValueModel r204_207s;
    private static ValueModel rhoR235_204s__r207_204s;
    private static ValueModel r232_204s;
    private static ValueModel r208_204s;
    private static ValueModel rhoR202_204s__r208_204s;
    private static ValueModel r238_206s;
    private static ValueModel r207_206s;
    private static ValueModel rhoR238_206s__r207_206s;
    private static ValueModel rhoR207_206s__r204_206s;
    private static ValueModel r238_207s;
    private static ValueModel r235_207s;
    // 5. Outputs **********************************************************
    // 5a. Pb Calculations // 5b. U calculations
    private static ValueModel initCommonPbMass;
    private static ValueModel totCommonPbMass;
    private static ValueModel radToCommonPb206;
    private static ValueModel radToCommonPb207;
    private static ValueModel radToCommonPb208;
    private static ValueModel percentDiscordance;
    private static ValueModel alphaU;
    private static ValueModel massU;
    private static ValueModel concU;
    private static ValueModel concTh;
    private static ValueModel rTh_Usample;
    private static ValueModel massPb_rib;
    private static ValueModel concPb_rib;
    private static ValueModel concPb_r;
    private static ValueModel concPb_i;
    private static ValueModel concPb_ib;
    private static ValueModel concPb206_rib;
    private static ValueModel concPb206_r;
    private static ValueModel concPb206_ib;
    private static ValueModel radToCommonTotal;
    // 6. helpers
//    private static Map<String, String> outputTable;
    private final static MathContext mathContext15 = ReduxConstants.mathContext15;
    // temp partial results for new oxidation correction
    private static ValueModel r238_235oc;
    private static ValueModel r233_235oc;
    // jan 2011 for tracer type 236
    private static ValueModel r238_236oc;
    private static ValueModel r233_236oc;
    private static ValueModel totRadiogenicPbMass;// intermediate
    // store partial derivatives
    private static ConcurrentMap<String, BigDecimal> parDerivTerms;
    private static Map<String, BigDecimal> coVariances;
    private static Map<String, BigDecimal> inputVariances;
    private static SortedMap<Integer, ValueModel> variablesInOrder;
    private static SortedMap<Integer, ValueModel> specialInputVariablesInOrder;
    private static boolean treatFractionAsZircon;
    // march 2013 modernizing approach to encapsulate what is sent to redux
    private SortedMap<RadRatios, SessionCorrectedUnknownsSummary> sessionCorrectedUnknownsSummaries;

    private UPbFractionReducer() {
    }

    /**
     *
     * @return
     */
    public static UPbFractionReducer getInstance() {
        if (instance == null) {
            instance = new UPbFractionReducer();
        }
        return instance;
    }

    /**
     *
     * @param fraction
     * @param calculateCovariances
     */
    public static void fullFractionReduce(
            FractionI fraction,
            boolean calculateCovariances) {

        if (fraction instanceof UPbFraction) {
            fullFractionReduce_IDTIMS(fraction, calculateCovariances);
        } else if (fraction instanceof UPbLAICPMSFraction) {
            fullFractionReduce_LAICPMS(fraction, calculateCovariances);
        }

        // copy map - no map if legacy
        //nov 2013 added try
        try {
            ConcurrentMap<String, BigDecimal> parDerivTermsCopy = new ConcurrentHashMap<>();
            Iterator<String> parDerivTermsIterator = parDerivTerms.keySet().iterator();
            while (parDerivTermsIterator.hasNext()) {
                String key = parDerivTermsIterator.next();
                BigDecimal value = parDerivTerms.get(key);
                parDerivTermsCopy.put(key, value);
            }

            fraction.setParDerivTerms(parDerivTermsCopy);
        } catch (Exception e) {
        }
    }

    private static void fullFractionReduce_LAICPMS(
            ETFractionInterface fraction,
            boolean calculateCovariances) {
//        System.out.println( "LAICPMS REDUCER" );
     /*
         * Noah by email 12 september 2011 This is a stopgap calculation until
         * we implement more rigorous uncertainty propagation:
         *
         * VARIABLES:
         *
         * use r238_235s: same as in Redux, value usually 137.88 (but will
         * change soon!) r206_238s and r206_238sOneSigmaAbs: Mean and 1 standard
         * error (absolute) for the fractionation-corrected 206/238 ratios
         * r207_206s and r207_206sOneSigmaAbs: Mean and 1 standard error
         * (absolute) for the fractionation-corrected 207/206 ratios
         *
         * to calculate r207_235s and r207_235sOneSigmaAbs: Mean and 1 standard
         * error (absolute) for data table, use in plotting
         *
         * EQUATION: r207_235s = r238_235s * r206_238s * r207_206s
         *
         * r207_253sOneSigmaAbs = SQRT( r206_238s^2 * r238_235s^2 *
         * r207_206sOneSigmaAbs^2 + r207_206s^2 * r238_235s^2 *
         * r206_238sOneSigmaAbs^2 )
         *
         * Assume all correlation coefficients are zero. (for now!)
         *
         * Let's make three new variables - r206_238s, r207_206s, and r207_235s,
         * and populate them with the three means of the fractionation-corrected
         * data. Same for r208_232s. We might already have r207_206s defined for
         * TIMS -- this is fine, the LA-ICPMS value is directly analogous. When
         * we get done with the common Pb correction, then we will be
         * calculating r206_238r, r207_235r, r207_206r, etc. For concordia
         * plotting, Jeff mentioned yesterday that he wants to see both an
         * ellipse for (r206_238s, r207_235s) before common Pb correction and
         * (r206_238r, r207_235r) after common Pb correction on the same plot.
         */

        // playing by forcing into radiogenic
//        if ( fraction.getFractionID().contains( "R33" ) ) {
//            System.out.println( "333" );
//        }
        initializeAtomicMolarMasses(
                fraction.getPhysicalConstantsModel());

        initializeDecayConstants(fraction.getPhysicalConstantsModel());

        //fraction.getAnalysisMeasure( AnalysisMeasures.r238_235s.getName() ).setValue( new BigDecimal( 137.88 ) );
        // updated feb 2014
        fraction.getAnalysisMeasure(AnalysisMeasures.r238_235s.getName()).setValue(ReduxLabData.getInstance().getDefaultR238_235s().getValue());
        fraction.getAnalysisMeasure(AnalysisMeasures.r238_235s.getName()).setOneSigma(ReduxLabData.getInstance().getDefaultR238_235s().getOneSigma());

        parDerivTerms = new ConcurrentHashMap<>();

        // ratio flipper
        r207_206r = new ValueModel("r207_206r");
        try {
            r207_206r.setValue(BigDecimal.ONE.//
                    divide(fraction.getMeasuredRatioByName(MeasuredRatios.r206_207m.getName()).getValue(), ReduxConstants.mathContext15));
        } catch (Exception e) {
        }
        r207_206r.setUncertaintyType("PCT");
        r207_206r.setOneSigma(//
                fraction.getMeasuredRatioByName(MeasuredRatios.r206_207m.getName()).getOneSigmaPct());
        r207_206r.toggleUncertaintyType();

        fraction.setRadiogenicIsotopeRatioByName("r207_206r", r207_206r);

        r206_238r = new ValueModel("r206_238r");
        r206_238r.setValue(//
                fraction.getMeasuredRatioByName("r206_238m").getValue());
        r206_238r.setUncertaintyType("ABS");
        r206_238r.setOneSigma(//
                fraction.getMeasuredRatioByName("r206_238m").getOneSigmaAbs());

        fraction.setRadiogenicIsotopeRatioByName("r206_238r", r206_238r);

        r208_232r = new ValueModel("r208_232r");
        r208_232r.setValue(//
                fraction.getMeasuredRatioByName("r208_232m").getValue());
        r208_232r.setUncertaintyType("ABS");
        r208_232r.setOneSigma(//
                fraction.getMeasuredRatioByName("r208_232m").getOneSigmaAbs());

        fraction.setRadiogenicIsotopeRatioByName("r208_232r", r208_232r);

        r207_235r = new ValueModel("r207_235r");
        r207_235r.setValue(//
                fraction.getAnalysisMeasure(AnalysisMeasures.r238_235s.getName()).getValue().//
                multiply(r206_238r.getValue()).//
                multiply(r207_206r.getValue()));

        r207_235r.setUncertaintyType("ABS");
        try {
            r207_235r.setOneSigma( //
                    new BigDecimal(Math.sqrt(
                            r206_238r.getValue().pow(2).//
                            multiply(fraction.getAnalysisMeasure(AnalysisMeasures.r238_235s.getName()).getValue().pow(2)).//
                            multiply(r207_206r.getOneSigmaAbs().pow(2)).//

                            add(r207_206r.getValue().pow(2).//
                                    multiply(fraction.getAnalysisMeasure(AnalysisMeasures.r238_235s.getName()).getValue().pow(2)).//
                                    multiply(r206_238r.getOneSigmaAbs().pow(2))).//
                            doubleValue())));
        } catch (Exception e) {
            System.out.println("BAD 207235 sigma FractionReducer Line 456");
        }

        fraction.setRadiogenicIsotopeRatioByName("r207_235r", r207_235r);

        age206_238r = new Age206_238r();
        fraction.setRadiogenicIsotopeDateByName(RadDates.age206_238r, age206_238r);
        // mar 2013 a little hack till we get logratios directly
        calculateDate206_238r( //
                age206_238r, //
                r206_238r.getValue().doubleValue(), //
                r206_238r.getValue().doubleValue() + r206_238r.getOneSigmaAbs().doubleValue() * 2.0);

        age208_232r = new Age208_232r();
        fraction.setRadiogenicIsotopeDateByName(RadDates.age208_232r, age208_232r);
        // mar 2013 a little hack till we get logratios directly
        calculateDate208_232r( //
                age208_232r, //
                r208_232r.getValue().doubleValue(), //
                r208_232r.getValue().doubleValue() + r208_232r.getOneSigmaAbs().doubleValue() * 2.0);

        age207_235r = new Age207_235r();
        fraction.setRadiogenicIsotopeDateByName(RadDates.age207_235r, age207_235r);
        calculateDate207_235r(//
                age207_235r,//
                r207_235r.getValue().doubleValue(),//
                r207_235r.getValue().doubleValue() + r207_235r.getOneSigmaAbs().doubleValue() * 2.0);

        age207_206r = new Age207_206r();
        fraction.setRadiogenicIsotopeDateByName(RadDates.age207_206r, age207_206r);
        // mar 2013 a little hack till we get logratios directly
        calculateDate207_206r(fraction.getAnalysisMeasure(AnalysisMeasures.r238_235s.getName()),
                age207_206r, //
                age206_238r,
                r207_206r.getValue().doubleValue(), //
                r207_206r.getValue().doubleValue() + r207_206r.getOneSigmaAbs().doubleValue() * 2.0);

        percentDiscordance = new PercentDiscordance();
        fraction.setRadiogenicIsotopeDateByName(RadDates.percentDiscordance, percentDiscordance);
        percentDiscordance.calculateValue(
                new ValueModel[]{
                    age206_238r,
                    age207_206r},
                null);

        // june 2013 reject bad fractions
        if (fraction.getRadiogenicIsotopeRatioByName(//
                "r207_235r").amPositiveAndLessThanTolerance()) {
            fraction.setRejected(true);
        }
        if (fraction.getRadiogenicIsotopeRatioByName(//
                RadRatios.r206_238r.getName()).amPositiveAndLessThanTolerance()) {
            fraction.setRejected(true);
        }
        if (fraction.getRadiogenicIsotopeRatioByName(//
                RadRatios.r207_206r.getName()).amPositiveAndLessThanTolerance()) {
            fraction.setRejected(true);
        }
        if (fraction.getRadiogenicIsotopeRatioByName(//
                RadRatios.r208_232r.getName()).amPositiveAndLessThanTolerance()) {
            fraction.setRejected(true);
        }

        // nov 2014 new math for rhos and Pbc uncertainty etc. continued from section 11
        double[] JSomeAllRatiosValues = new double[]{0, 1, 0, 0, 0, 0, 0, -1, 1, 0, 0, 0, 0, 1, -1, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0};
        Matrix JSomeAllRatios = new Matrix(JSomeAllRatiosValues, 7).transpose();

        Matrix SrAll = null;
        try {
            Matrix SfciTotalAll = JSomeAllRatios.times(((UPbLAICPMSFraction) fraction).getSfciTotal()).times(JSomeAllRatios.transpose());

            Matrix Jlrr = new Matrix(8, 8, 0.0);
            // Jlrr = diag( r206_238fc, r207_235fc, r207_206fc, r238_206fc,..r208_232fc .. r206_204fc, r207_204fc, r208_204fc).  
            Jlrr.set(0, 0, fraction.getRadiogenicIsotopeRatioByName(RadRatios.r206_238r.getName()).getValue().doubleValue());
            Jlrr.set(1, 1, fraction.getRadiogenicIsotopeRatioByName(RadRatios.r207_235r.getName()).getValue().doubleValue());
            Jlrr.set(2, 2, fraction.getRadiogenicIsotopeRatioByName(RadRatios.r207_206r.getName()).getValue().doubleValue());
            try {
                Jlrr.set(3, 3, 1.0 / fraction.getRadiogenicIsotopeRatioByName(RadRatios.r206_238r.getName()).getValue().doubleValue());
            } catch (Exception e) {
            }
            Jlrr.set(4, 4, fraction.getRadiogenicIsotopeRatioByName(RadRatios.r208_232r.getName()).getValue().doubleValue());
            Jlrr.set(5, 5, fraction.getMeasuredRatioByName(MeasuredRatios.r206_204m.getName()).getValue().doubleValue());
            Jlrr.set(6, 6, fraction.getMeasuredRatioByName(MeasuredRatios.r207_204m.getName()).getValue().doubleValue());
            Jlrr.set(7, 7, fraction.getMeasuredRatioByName(MeasuredRatios.r208_204m.getName()).getValue().doubleValue());

            // SrAll (ratios All) is a eight-by-eight matrix per fraction that contains all the uncertainties and covariances 
            // (used to calculate rhos) needed to make Conventional and Tera-Wasserburg plots for non-common-Pb corrected ratios.  
            // These are the real (not fake) covariance/correlation terms.  
            SrAll = Jlrr.times(SfciTotalAll).times(Jlrr.transpose());
            ((UPbLAICPMSFraction) fraction).setSrAll(SrAll);

            // rho Wetherill 
            double rhoR206_238r__r207_235r = SrAll.get(0, 1) / Math.sqrt(SrAll.get(0, 0) * SrAll.get(1, 1));
            fraction.getRadiogenicIsotopeRatioByName("rhoR206_238r__r207_235r").setValue(rhoR206_238r__r207_235r);

            // rho T-W
            double rhoR207_206r__r238_206r = SrAll.get(2, 3) / Math.sqrt(SrAll.get(2, 2) * SrAll.get(3, 3));
            fraction.getRadiogenicIsotopeRatioByName("rhoR207_206r__r238_206r").setValue(rhoR207_206r__r238_206r);

            // default values for pbc corrected ratios with below detection 204
            //if (((UPbLAICPMSFraction) fraction).hasCommonLeadLossCorrectionSchemeGreaterThanTypeA()) {
            // rho Wetherill 
            fraction.getRadiogenicIsotopeRatioByName("rhoR206_238PbcCorr__r207_235PbcCorr").setValue(rhoR206_238r__r207_235r);

            // rho T-W
            fraction.getRadiogenicIsotopeRatioByName("rhoR207_206PbcCorr__r238_206PbcCorr").setValue(rhoR207_206r__r238_206r);
            //}

            // prepare for common lead loss corrections
            if (!fraction.isRejected()) {
                ((UPbLAICPMSFraction) fraction).performCommonLeadLossCorrectionsToRatios();

                ValueModel r206_238_PbcCorr = fraction.getRadiogenicIsotopeRatioByName(RadRatiosPbcCorrected.r206_238_PbcCorr.getName());
                ValueModel r207_235_PbcCorr = fraction.getRadiogenicIsotopeRatioByName(RadRatiosPbcCorrected.r207_235_PbcCorr.getName());
                ValueModel r207_206_PbcCorr = fraction.getRadiogenicIsotopeRatioByName(RadRatiosPbcCorrected.r207_206_PbcCorr.getName());
                ValueModel r238_206_PbcCorr = fraction.getRadiogenicIsotopeRatioByName(RadRatiosPbcCorrected.r238_206_PbcCorr.getName());
                ValueModel r208_232_PbcCorr = fraction.getRadiogenicIsotopeRatioByName(RadRatiosPbcCorrected.r208_232_PbcCorr.getName());

                // clear out corrected dates
                ValueModel age206_238_PbcCorr = new Age206_238r();
                age206_238_PbcCorr.setName(RadDates.age206_238_PbcCorr.getName());
                fraction.setRadiogenicIsotopeDateByName(RadDates.age206_238_PbcCorr, age206_238_PbcCorr);

                ValueModel age208_232_PbcCorr = new Age208_232r();
                age208_232_PbcCorr.setName(RadDates.age208_232_PbcCorr.getName());
                fraction.setRadiogenicIsotopeDateByName(RadDates.age208_232_PbcCorr, age208_232_PbcCorr);

                ValueModel age207_235_PbcCorr = new Age207_235r();
                age207_235_PbcCorr.setName(RadDates.age207_235_PbcCorr.getName());
                fraction.setRadiogenicIsotopeDateByName(RadDates.age207_235_PbcCorr, age207_235_PbcCorr);

                ValueModel age207_206_PbcCorr = new Age207_206r();
                age207_206_PbcCorr.setName(RadDates.age207_206_PbcCorr.getName());
                fraction.setRadiogenicIsotopeDateByName(RadDates.age207_206_PbcCorr, age207_206_PbcCorr);

                ValueModel percentDiscordance_PbCorr = new PercentDiscordance();
                percentDiscordance_PbCorr.setName(RadDates.percentDiscordance_PbcCorr.getName());
                fraction.setRadiogenicIsotopeDateByName(RadDates.percentDiscordance_PbcCorr, percentDiscordance_PbCorr);

                // Pbc correction yields non-zero upper phi 
                if (((UPbLAICPMSFraction) fraction).getCommonLeadLossCorrectionScheme().getName().contains("B2")//
                        || //
                        (((UPbLAICPMSFraction) fraction).getCommonLeadLossCorrectionScheme().getName().contains("B1") && !((UPbLAICPMSFraction) fraction).getUpperPhiMap().containsValue(0.0))) {

//                }
//                
//                if (((UPbLAICPMSFraction) fraction).hasCommonLeadLossCorrectionSchemeGreaterThanTypeA() && !((UPbLAICPMSFraction) fraction).getUpperPhiMap().containsValue(0.0)) {
                    //           r206_238fc, r207_235fc, r207_206fc, r238_206fc, r208_232fc, r206_204fc, r207_204fc, r208_204fc, r206_204c, r207_204c, r208_204c
                    //                 0           1         2           3            4          5           6            7          8          9         10
                    // 0 r68pbcc
                    // 1 r75pbcc
                    // 2 r76pbcc
                    // 3 r86pbcc
                    // 4 r82pbcc 
                    Matrix JPbccs = new Matrix(5, 11, 0.0);

                    JPbccs.set(0, 0, PbcCorrectionDetails.dR68pbcc__dR206_238fc);
                    JPbccs.set(0, 5, PbcCorrectionDetails.dR68pbcc__dR206_204fc);
                    JPbccs.set(0, 8, PbcCorrectionDetails.dR68pbcc__dR206_204c);

                    JPbccs.set(1, 1, PbcCorrectionDetails.dR75pbcc__dR207_235fc);
                    JPbccs.set(1, 6, PbcCorrectionDetails.dR75pbcc__dR207_204fc);
                    JPbccs.set(1, 9, PbcCorrectionDetails.dR75pbcc__dR207_204c);

                    JPbccs.set(2, 5, PbcCorrectionDetails.dR76pbcc__dR206_204fc);
                    JPbccs.set(2, 6, PbcCorrectionDetails.dR76pbcc__dR207_204fc);
                    JPbccs.set(2, 8, PbcCorrectionDetails.dR76pbcc__dR206_204c);
                    JPbccs.set(2, 9, PbcCorrectionDetails.dR76pbcc__dR207_204c);

                    JPbccs.set(3, 3, PbcCorrectionDetails.dR86pbcc__dR238_206fc);
                    JPbccs.set(3, 5, PbcCorrectionDetails.dR86pbcc__dR206_204fc);
                    JPbccs.set(3, 8, PbcCorrectionDetails.dR86pbcc__dR206_204c);

                    JPbccs.set(4, 10, PbcCorrectionDetails.dR82pbcc__dR208_204c);
                    JPbccs.set(4, 7, PbcCorrectionDetails.dR82pbcc__dR208_204fc);
                    JPbccs.set(4, 4, PbcCorrectionDetails.dR82pbcc__dR208_232fc);

                    Matrix SrAll204 = new Matrix(SrAll.getRowDimension() + 3, SrAll.getColumnDimension() + 3, 0.0);
                    SrAll204.setMatrix(0, SrAll.getRowDimension() - 1, 0, SrAll.getColumnDimension() - 1, SrAll);
                    //8 = 6/4, 9 = 7/4, 10 = 8/4
                    // figure out the kind of initial pb model
                    // first set the diagonals to 1
                    SrAll204.set(8, 8, 1.0);
                    SrAll204.set(9, 9, 1.0);
                    SrAll204.set(10, 10, 1.0);

                    if (((UPbLAICPMSFraction) fraction).isUseStaceyKramer()) {
                        double skRhoVarUnct = ((UPbLAICPMSFraction) fraction).getStaceyKramerCorrectionParameters().get("skRhoVarUnct").doubleValue();

                        double r206_204c_OneSigmaAbs = ((UPbLAICPMSFraction) fraction).getCommonLeadCorrectionParameters().get("r206_204c").getOneSigmaAbs().doubleValue();

                        double r207_204c_OneSigmaAbs = ((UPbLAICPMSFraction) fraction).getCommonLeadCorrectionParameters().get("r207_204c").getOneSigmaAbs().doubleValue();

                        double r208_204c_OneSigmaAbs = ((UPbLAICPMSFraction) fraction).getCommonLeadCorrectionParameters().get("r208_204c").getOneSigmaAbs().doubleValue();

                        double covR206_204c__207_204c = 0;
                        try {
                            covR206_204c__207_204c = skRhoVarUnct * Math.sqrt(Math.pow(r206_204c_OneSigmaAbs, 2) * Math.pow(r207_204c_OneSigmaAbs, 2));
                        } catch (Exception e) {
                        }
                        double covR206_204c__208_204c = 0;
                        try {
                            covR206_204c__208_204c = skRhoVarUnct * Math.sqrt(Math.pow(r206_204c_OneSigmaAbs, 2) * Math.pow(r208_204c_OneSigmaAbs, 2));
                        } catch (Exception e) {
                        }
                        double covR207_204c__208_204c = 0;
                        try {
                            covR207_204c__208_204c = skRhoVarUnct * Math.sqrt(Math.pow(r207_204c_OneSigmaAbs, 2) * Math.pow(r208_204c_OneSigmaAbs, 2));
                        } catch (Exception e) {
                        }
                        SrAll204.set(8, 9, covR206_204c__207_204c);
                        SrAll204.set(9, 8, covR206_204c__207_204c);

                        SrAll204.set(8, 10, covR206_204c__208_204c);
                        SrAll204.set(10, 8, covR206_204c__208_204c);

                        SrAll204.set(9, 10, covR207_204c__208_204c);
                        SrAll204.set(10, 9, covR207_204c__208_204c);

                    } else {
                        System.out.println("NOT SK MODEL HERE .. todo !!");
                    }

                    Matrix SFc204 = JPbccs.times(SrAll204).times(JPbccs.transpose());

                    ((UPbLAICPMSFraction) fraction).setJPbccs(JPbccs);
                    ((UPbLAICPMSFraction) fraction).setSFc204(SFc204);
//                    System.out.println("JPbccs for Fraction " + fraction.getFractionID());
//                    JPbccs.print(new DecimalFormat("0.000000E00"), 14);
//
//                    System.out.println("SFc204 for Fraction " + fraction.getFractionID());
//                    SFc204.print(new DecimalFormat("0.000000E00"), 14);

                    // Rho = sigmaxy/sqrt(sigma^2_x * sigma^2_y) where sigmaxy is the off-diagonal term in the covariance matrix, 
                    // and sigma^2_x and sigma^2_y are variances that are on the diagonal.  rhoxy = (x,y) / sqrt( (x,x)*(y,y) ).
                    // rhos are by default -9 until calculated seee ReduxConstants
                    // The matrix Sfc204 contains the uncertainties and uncertainty correlations for the 
                    // Pbc-corrected 206Pb/238U, 207Pb/235U, 207Pb/206Pb, 238U/206Pb, and 208Pb/232Th ratios,
                    // respectively, to output to the data table and use for conventional or Tera-Wasserburg concordia plotting. 
                    // rho Wetherill 
                    if (((UPbLAICPMSFraction) fraction).isCorrectedForPbc()) {
                        double rhoR206_238PbcCorr__r207_235PbcCorr = SFc204.get(0, 1) / Math.sqrt(SFc204.get(0, 0) * SFc204.get(1, 1));
                        fraction.getRadiogenicIsotopeRatioByName("rhoR206_238PbcCorr__r207_235PbcCorr").setValue(rhoR206_238PbcCorr__r207_235PbcCorr);

                        // rho T-W
                        double rhoR207_206PbcCorr__r238_206PbcCorr = SFc204.get(2, 3) / Math.sqrt(SFc204.get(2, 2) * SFc204.get(3, 3));
                        fraction.getRadiogenicIsotopeRatioByName("rhoR207_206PbcCorr__r238_206PbcCorr").setValue(rhoR207_206PbcCorr__r238_206PbcCorr);

                        r206_238_PbcCorr.setOneSigma(new BigDecimal(Math.sqrt(SFc204.get(0, 0))));

                        r207_235_PbcCorr.setOneSigma(new BigDecimal(Math.sqrt(SFc204.get(1, 1))));

                        r207_206_PbcCorr.setOneSigma(new BigDecimal(Math.sqrt(SFc204.get(2, 2))));

                        r238_206_PbcCorr.setOneSigma(new BigDecimal(Math.sqrt(SFc204.get(3, 3))));

                        r208_232_PbcCorr.setOneSigma(new BigDecimal(Math.sqrt(SFc204.get(4, 4))));
                    }

                } else {
                    // there is no common lead correction for B1 or B2
                }

                if (((UPbLAICPMSFraction) fraction).hasCommonLeadLossCorrectionSchemeGreaterThanTypeA()) {
                    calculateDate206_238r( //
                            age206_238_PbcCorr, //
                            r206_238_PbcCorr.getValue().doubleValue(), //
                            r206_238_PbcCorr.getValue().doubleValue() + r206_238_PbcCorr.getOneSigmaAbs().doubleValue() * 2.0);

                    // mar 2013 a little hack till we get logratios directly
                    calculateDate208_232r( //
                            age208_232_PbcCorr, //
                            r208_232_PbcCorr.getValue().doubleValue(), //
                            r208_232_PbcCorr.getValue().doubleValue() + r208_232_PbcCorr.getOneSigmaAbs().doubleValue() * 2.0);

                    // mar 2013 a little hack till we get logratios directly
                    calculateDate207_235r(//
                            age207_235_PbcCorr,//
                            r207_235_PbcCorr.getValue().doubleValue(),//
                            r207_235_PbcCorr.getValue().doubleValue() + r207_235_PbcCorr.getOneSigmaAbs().doubleValue() * 2.0);

                    // mar 2013 a little hack till we get logratios directly
                    calculateDate207_206r(fraction.getAnalysisMeasure(AnalysisMeasures.r238_235s.getName()),
                            age207_206_PbcCorr, //
                            age206_238_PbcCorr,
                            r207_206_PbcCorr.getValue().doubleValue(), //
                            r207_206_PbcCorr.getValue().doubleValue() + r207_206_PbcCorr.getOneSigmaAbs().doubleValue() * 2.0);

                    percentDiscordance_PbCorr.calculateValue(
                            new ValueModel[]{
                                age206_238_PbcCorr,
                                age207_206_PbcCorr},
                            null);

                }
            }

        } catch (Exception e) {
        }

        // end of new math nov 2014
    }  // end full laicpms reduction

    /**
     *
     * @param date206_238r
     * @param ratio
     * @param ratioPlustwoSigma
     * @return
     */
    public static ValueModel calculateDate206_238r(ValueModel date206_238r, double ratio, double ratioPlustwoSigma) {

        date206_238r.calculateValue(
                new ValueModel[]{
                    new ValueModel("r206_238r", new BigDecimal(ratio), "ABS", BigDecimal.ZERO, BigDecimal.ZERO),
                    lambda238},
                parDerivTerms);

        // feb 2013 per section 12 - temp hack until measured ratios handle upper and lower sigmas
        // using the ratio plus 2sigma
        ValueModel date206_238rPlusTwoSigma = new Age206_238r();
        date206_238rPlusTwoSigma.setName("date206_238rPlusTwoSigma");
        date206_238rPlusTwoSigma.calculateValue(
                new ValueModel[]{
                    new ValueModel("r206_238rPlusTwoSigma", new BigDecimal(ratioPlustwoSigma), "ABS", BigDecimal.ZERO, BigDecimal.ZERO),
                    lambda238},
                null); //dont overwrite deriv

        // for now take half the distance from the upper 2 sigma to the mean
        date206_238r.setOneSigma(date206_238rPlusTwoSigma.getValue().subtract(date206_238r.getValue()).multiply(new BigDecimal(0.5)));

        return date206_238r;
    }

    /**
     *
     * @param date208_232r
     * @param ratio
     * @param ratioPlustwoSigma
     * @return
     */
    public static ValueModel calculateDate208_232r(ValueModel date208_232r, double ratio, double ratioPlustwoSigma) {

        date208_232r.calculateValue(
                new ValueModel[]{
                    new ValueModel("r208_232r", new BigDecimal(ratio), "ABS", BigDecimal.ZERO, BigDecimal.ZERO),
                    lambda232},
                parDerivTerms);

        // feb 2013 per section 12 - temp hack until measured ratios handle upper and lower sigmas
        // using the ratio plus 2sigma
        ValueModel date208_232rPlusTwoSigma = new Age208_232r();
        date208_232rPlusTwoSigma.setName("date208_232rPlusTwoSigma");
        date208_232rPlusTwoSigma.calculateValue(
                new ValueModel[]{
                    new ValueModel("r208_232rPlusTwoSigma", new BigDecimal(ratioPlustwoSigma), "ABS", BigDecimal.ZERO, BigDecimal.ZERO),
                    lambda232},
                null);

        // for now take half the distance from the upper 2 sigma to the mean
        date208_232r.setOneSigma(date208_232rPlusTwoSigma.getValue().subtract(date208_232r.getValue()).multiply(new BigDecimal(0.5)));

        return date208_232r;
    }

    /**
     *
     * @param date207_235r
     * @param ratio
     * @param ratioPlustwoSigma
     * @return
     */
    public static ValueModel calculateDate207_235r(ValueModel date207_235r, double ratio, double ratioPlustwoSigma) {

        date207_235r.calculateValue(
                new ValueModel[]{
                    new ValueModel("r207_235r", new BigDecimal(ratio), "ABS", BigDecimal.ZERO, BigDecimal.ZERO),
                    lambda235},
                parDerivTerms);

        // feb 2013 per section 12 - temp hack until measured ratios handle upper and lower sigmas
        // using the ratio plus 2sigma
        ValueModel date207_235rPlusTwoSigma = new Age207_235r();
        date207_235rPlusTwoSigma.setName("date207_235rPlusTwoSigma");
        date207_235rPlusTwoSigma.calculateValue(
                new ValueModel[]{
                    new ValueModel("r207_235rPlusTwoSigma", new BigDecimal(ratioPlustwoSigma), "ABS", BigDecimal.ZERO, BigDecimal.ZERO),
                    lambda235},
                null);

        // for now take half the distance from the upper 2 sigma to the mean
        date207_235r.setOneSigma(date207_235rPlusTwoSigma.getValue().subtract(date207_235r.getValue()).multiply(new BigDecimal(0.5)));

        return date207_235r;
    }

    /**
     *
     * @param r238_235s
     * @param date207_206r
     * @param date206_238r
     * @param ratio
     * @param ratioPlustwoSigma
     * @return
     */
    public static ValueModel calculateDate207_206r(ValueModel r238_235s, ValueModel date207_206r, ValueModel date206_238r, double ratio, double ratioPlustwoSigma) {

        date207_206r.calculateValue(
                new ValueModel[]{
                    r238_235s,
                    new ValueModel("r207_206r", new BigDecimal(ratio), "ABS", BigDecimal.ZERO, BigDecimal.ZERO),
                    date206_238r,
                    lambda235,
                    lambda238},
                parDerivTerms);

        // feb 2013 per section 12 - temp hack until measured ratios handle upper and lower sigmas
        // using the ratio plus 2sigma
        ValueModel date207_206rPlusTwoSigma = new Age207_206r();
        date207_206rPlusTwoSigma.setName("date207_206rPlusTwoSigma");
        date207_206rPlusTwoSigma.calculateValue(
                new ValueModel[]{
                    r238_235s,
                    new ValueModel("r207_206rPlusTwoSigma", new BigDecimal(ratioPlustwoSigma), "ABS", BigDecimal.ZERO, BigDecimal.ZERO),
                    date206_238r,
                    lambda235,
                    lambda238},
                null);

        // for now take half the distance from the upper 2 sigma to the mean
        date207_206r.setOneSigma(date207_206rPlusTwoSigma.getValue().subtract(date207_206r.getValue()).multiply(new BigDecimal(0.5)));

        return date207_206r;
    }

    private static void fullFractionReduce_IDTIMS(
            FractionI fraction,
            boolean calculateCovariances) {

        // feb 2010 this statement serves as a filter during compilation to prevent reduction of legacy aliquots 
        if (!fraction.isLegacy()) {

            // oct 2009 initialize outputs
            ((UPbFraction) fraction).setOutputs(new ValueModel[0]);

            //System.out.println( "Reducing Fraction = " + fraction.getFractionID() );
            // Feb 2011 reorganized logic here as treatFractionAsZircon could not be calculated here !!!
            initializeAtomicMolarMasses(
                    fraction.getPhysicalConstantsModel());

            initializeDecayConstants(fraction.getPhysicalConstantsModel());

            // march 2012 correction
            // if initial pb is stacey kramers, values must be calculated each time, as only one copy of this model exists
            fraction.calculateStaceyKramersInitialPbModelValues();

            parDerivTerms = new ConcurrentHashMap<>();

            initializeAndEvalSpecialInputVariablesInOrder(fraction);

            initializeVariablesInOrder(fraction);

            evaluateVariablesInOrderI(fraction);

            // feb 2011 moved here, because initializeReductionHandler depends on this value to select correct matrix
            // aug 2010 made a transient field for UPbFraction to inform matrix choices
            // the following is true unless fraction isNOT zircon AND molPb204tc exceeds blankratio
            // in truth table, 3 out of four cases evaluate to true
            try {
                treatFractionAsZircon = //
                        !(!fraction.isZircon()//
                        && (molPb204tc.getValue().compareTo(//
                                fraction.getAnalysisMeasure(AnalysisMeasures.pbBlankMassInGrams.getName()).getValue().//
                                divide(blankPbGramsMol.getValue(), mathContext15)) == 1));
                ((UPbFraction) fraction).setTreatFractionAsZircon(treatFractionAsZircon);
            } catch (Exception e) {
            }

            // Feb 2011 broke evaluateVariables into two parts to properly handle treatFractionAsZircon
            evaluateVariablesInOrderII(fraction);

            // calculate covariances will be false during Kwiki page sensitivity changes in value sliders
            // vs changes in uncertaintly sliders
            // the following statement restarts the reduction process unless covariances are not wanted for Kwiki
            if (calculateCovariances) {
                calculateCovariances = ((UPbFraction) fraction).initializeReductionHandler();
            }

            if (((UPbFraction) fraction).getReductionHandler() != null) {

                calculateCovariancesMap(fraction);

                initializeInputVariances(fraction);

                try { //TODO Handle throw exception
                    ((UPbFraction) fraction).getReductionHandler().//
                            populateCovarianceMatrices(inputVariances, coVariances);
                } catch (Exception e) {
                }

                try {//TODO Handle throw exception
                    ((UPbFraction) fraction).getReductionHandler().//
                            populateJacobianMatrices(parDerivTerms);

                    ((UPbFraction) fraction).getReductionHandler().calculateDateCovMatModel(true);
                } catch (Exception e) {
                    System.out.println("FractionReducer Line 371   >>   " + e.getMessage());
                }
            }

            ((UPbFraction) fraction).setCoVariances(coVariances);
            fraction.setParDerivTerms(parDerivTerms);
            // sept 2010 restore alphaPb if needed
            if ((((UPbFraction) fraction).getMeanAlphaPb().compareTo(BigDecimal.ZERO) == 1)//
                    && //
                    !alphaPb.hasPositiveValue()) {
                alphaPb.setValue(((UPbFraction) fraction).getMeanAlphaPb());
                alphaPb.setValue(BigDecimal.ZERO);
            }
            // special for noah sep 2010
            ((UPbFraction) fraction).variablesInOrder = variablesInOrder;
            ((UPbFraction) fraction).specialInputVariablesInOrder = specialInputVariablesInOrder;
            // }
        } else {
            // this is legacy fraction and we need to show data
            // these flags override the "-" produced by reportcategory
            // june 2010 handled in report category
//            ((UPbFraction) fraction).setHasMeasuredLead( true );
//            ((UPbFraction) fraction).setHasMeasuredUranium( true );
        }
    }

    private static void evaluateVariablesInOrderI(ETFractionInterface fraction) {

        AbstractRatiosDataModel fractionTracer = ((UPbFractionI) fraction).getTracer();

        // 0b. preparation
        // feb 2009 bring use of model here for sensitivity use
        if ((((AlphaPb) alphaPb).getFractionMeanAlphaPb().compareTo(BigDecimal.ZERO) == 0)
                && (fraction.getMeasuredRatioByName(MeasuredRatios.r202_205m.getName()).//
                getValue().compareTo(BigDecimal.ZERO) == 0)) {

            alphaPb = fraction.getAnalysisMeasure(AnalysisMeasures.alphaPb.getName());

        } else {

            alphaPb.calculateValue(
                    new ValueModel[]{
                        ((UPbFraction) fraction).getMeasuredRatioByName(MeasuredRatios.r202_205m.getName()),
                        fractionTracer.getDatumByName(TracerUPbRatiosAndConcentrations.r202_205t.getName())},
                    parDerivTerms);
            fraction.setAnalysisMeasureByName(AnalysisMeasures.alphaPb.getName(), alphaPb);
        }

        // 1. Pb
        // 1a. tracer *****************************************************!!!
        molPb205t.calculateValue(
                new ValueModel[]{
                    fractionTracer.getDatumByName(TracerUPbRatiosAndConcentrations.concPb205t.getName()),
                    fraction.getAnalysisMeasure(AnalysisMeasures.tracerMassInGrams.getName())},
                parDerivTerms);

        // 1b. Pb blank ****************************************************!!!
        r204_205fc.calculateValue(
                new ValueModel[]{
                    fraction.getMeasuredRatioByName(MeasuredRatios.r204_205m.getName()),
                    alphaPb},
                parDerivTerms);

        molPb204tc.calculateValue(
                new ValueModel[]{
                    fractionTracer.getDatumByName(TracerUPbRatiosAndConcentrations.r204_205t.getName()),
                    r204_205fc,
                    molPb205t},
                parDerivTerms);

        blankPbGramsMol.calculateValue(
                null,
                parDerivTerms);

    }

    private static void evaluateVariablesInOrderII(ETFractionInterface fraction) {

        AbstractRatiosDataModel fractionTracer = ((UPbFractionI) fraction).getTracer();
        String tracerType = ((UPbFraction) fraction).getTracerType().trim();

        // new logic as of 6 Nov 2008
        // check for special non-zircon case first
        if (!treatFractionAsZircon) {

            ((MolPb204b) molPb204b).setZircon(false);
            molPb204b.calculateValue(
                    new ValueModel[]{
                        fraction.getAnalysisMeasure(AnalysisMeasures.pbBlankMassInGrams.getName()),
                        blankPbGramsMol},
                    parDerivTerms);

            ((MolPb206b) molPb206b).setZircon(false);
            molPb206b.calculateValue(
                    new ValueModel[]{
                        fraction.getAnalysisMeasure(AnalysisMeasures.pbBlankMassInGrams.getName()),
                        blankPbGramsMol,
                        ((UPbFractionI) fraction).getPbBlank().getDatumByName("r206_204b"),
                        //                        ((UPbFraction) fraction).getPbBlank().getDatumByName( "r207_204b" ),
                        //                        ((UPbFraction) fraction).getPbBlank().getDatumByName( "r208_204b" ),
                        molPb204b},
                    parDerivTerms);

            ((MolPb207b) molPb207b).setZircon(false);
            molPb207b.calculateValue(
                    new ValueModel[]{
                        fraction.getAnalysisMeasure(AnalysisMeasures.pbBlankMassInGrams.getName()),
                        blankPbGramsMol,
                        ((UPbFractionI) fraction).getPbBlank().getDatumByName("r207_204b"),
                        molPb204b},
                    parDerivTerms);

            ((MolPb208b) molPb208b).setZircon(false);
            molPb208b.calculateValue(
                    new ValueModel[]{
                        fraction.getAnalysisMeasure(AnalysisMeasures.pbBlankMassInGrams.getName()),
                        blankPbGramsMol,
                        ((UPbFractionI) fraction).getPbBlank().getDatumByName("r208_204b"),
                        molPb204b},
                    parDerivTerms);

            ((MolPb204c) molPb204c).setZircon(false);
            molPb204c.calculateValue(
                    new ValueModel[]{
                        molPb204tc,
                        molPb204b},
                    parDerivTerms);

            ((MolPb206c) molPb206c).setZircon(false);
            molPb206c.calculateValue(
                    new ValueModel[]{
                        ((FractionI) fraction).getInitialPbModel().getDatumByName("r206_204c"),
                        molPb204c},
                    parDerivTerms);

            ((MolPb207c) molPb207c).setZircon(false);
            molPb207c.calculateValue(
                    new ValueModel[]{
                        ((FractionI) fraction).getInitialPbModel().getDatumByName("r207_204c"),
                        molPb204c},
                    parDerivTerms);

            ((MolPb208c) molPb208c).setZircon(false);
            molPb208c.calculateValue(
                    new ValueModel[]{
                        ((FractionI) fraction).getInitialPbModel().getDatumByName("r208_204c"),
                        molPb204c},
                    parDerivTerms);

            blankPbMass = fraction.getAnalysisMeasure(AnalysisMeasures.pbBlankMassInGrams.getName()).copy();
            // convert name for output table
            blankPbMass.setName("blankPbMass");

        } else {
            // fraction will be treated as a zircon
            ((MolPb204b) molPb204b).setZircon(true);
            molPb204b.calculateValue(
                    new ValueModel[]{
                        r204_205fc,
                        fractionTracer.getDatumByName(TracerUPbRatiosAndConcentrations.r204_205t.getName()),
                        molPb205t},
                    parDerivTerms);

            ((MolPb206b) molPb206b).setZircon(true);
            molPb206b.calculateValue(
                    new ValueModel[]{
                        r204_205fc,
                        fractionTracer.getDatumByName(TracerUPbRatiosAndConcentrations.r204_205t.getName()),
                        molPb205t,
                        ((UPbFractionI) fraction).getPbBlank().getDatumByName("r206_204b")},
                    parDerivTerms);

            ((MolPb207b) molPb207b).setZircon(true);
            molPb207b.calculateValue(
                    new ValueModel[]{
                        r204_205fc,
                        fractionTracer.getDatumByName(TracerUPbRatiosAndConcentrations.r204_205t.getName()),
                        molPb205t,
                        ((UPbFractionI) fraction).getPbBlank().getDatumByName("r207_204b")},
                    parDerivTerms);
            ((MolPb208b) molPb208b).setZircon(true);
            molPb208b.calculateValue(
                    new ValueModel[]{
                        r204_205fc,
                        fractionTracer.getDatumByName(TracerUPbRatiosAndConcentrations.r204_205t.getName()),
                        molPb205t,
                        ((UPbFractionI) fraction).getPbBlank().getDatumByName("r208_204b")},
                    parDerivTerms);

            ((MolPb204c) molPb204c).setZircon(true);

            ((MolPb206c) molPb206c).setZircon(true);

            ((MolPb207c) molPb207c).setZircon(true);

            ((MolPb208c) molPb208c).setZircon(true);

            blankPbMass.setValue(//
                    molPb204b.getValue().multiply(gmol204.getValue()).
                    add(molPb206b.getValue().multiply(gmol206.getValue())).
                    add(molPb207b.getValue().multiply(gmol207.getValue())).
                    add(molPb208b.getValue().multiply(gmol208.getValue())));

        }// end zircon test

        ((UPbFraction) fraction).setOutputByName("blankPbMass", blankPbMass);

        // 1c. radiogenic Pb, sample Pb *************************************
        molPb206r.calculateValue(
                new ValueModel[]{
                    fraction.getMeasuredRatioByName(MeasuredRatios.r206_205m.getName()),
                    fractionTracer.getDatumByName(TracerUPbRatiosAndConcentrations.r206_205t.getName()),
                    molPb205t,
                    alphaPb,
                    molPb206b,
                    molPb206c},
                parDerivTerms);

        molPb207r.calculateValue(
                new ValueModel[]{
                    fraction.getMeasuredRatioByName(MeasuredRatios.r207_205m.getName()),
                    fractionTracer.getDatumByName(TracerUPbRatiosAndConcentrations.r207_205t.getName()),
                    molPb205t,
                    alphaPb,
                    molPb207b,
                    molPb207c},
                parDerivTerms);

        molPb208r.calculateValue(
                new ValueModel[]{
                    fraction.getMeasuredRatioByName(MeasuredRatios.r208_205m.getName()),
                    fractionTracer.getDatumByName(TracerUPbRatiosAndConcentrations.r208_205t.getName()),
                    molPb205t,
                    alphaPb,
                    molPb208b,
                    molPb208c},
                parDerivTerms);

        molPb206s.calculateValue(
                new ValueModel[]{
                    fraction.getMeasuredRatioByName(MeasuredRatios.r206_205m.getName()),
                    fractionTracer.getDatumByName(TracerUPbRatiosAndConcentrations.r206_205t.getName()),
                    molPb205t,
                    alphaPb,
                    molPb206b},
                parDerivTerms);

        molPb207s.calculateValue(
                new ValueModel[]{
                    fraction.getMeasuredRatioByName(MeasuredRatios.r207_205m.getName()),
                    fractionTracer.getDatumByName(TracerUPbRatiosAndConcentrations.r207_205t.getName()),
                    molPb205t,
                    alphaPb,
                    molPb207b},
                parDerivTerms);

        molPb208s.calculateValue(
                new ValueModel[]{
                    fraction.getMeasuredRatioByName(MeasuredRatios.r208_205m.getName()),
                    fractionTracer.getDatumByName(TracerUPbRatiosAndConcentrations.r208_205t.getName()),
                    molPb205t,
                    alphaPb,
                    molPb208b},
                parDerivTerms);

        // 2a. tracer
        molU235b.calculateValue(
                new ValueModel[]{
                    fraction.getAnalysisMeasure(AnalysisMeasures.r238_235b.getName()),
                    gmol238,
                    gmol235,
                    fraction.getAnalysisMeasure(AnalysisMeasures.uBlankMassInGrams.getName())},
                parDerivTerms);

        molU238b.calculateValue(
                new ValueModel[]{
                    fraction.getAnalysisMeasure(AnalysisMeasures.r238_235b.getName()),
                    molU235b},
                parDerivTerms);

        molU235t.calculateValue(
                new ValueModel[]{
                    fractionTracer.getDatumByName(TracerUPbRatiosAndConcentrations.concU235t.getName()),
                    fraction.getAnalysisMeasure(AnalysisMeasures.tracerMassInGrams.getName())},
                parDerivTerms);

        molU236t.calculateValue(
                new ValueModel[]{
                    fractionTracer.getDatumByName(TracerUPbRatiosAndConcentrations.concU236t.getName()),
                    fraction.getAnalysisMeasure(AnalysisMeasures.tracerMassInGrams.getName())},
                parDerivTerms);

        molU238t.calculateValue(
                new ValueModel[]{
                    fractionTracer.getDatumByName(TracerUPbRatiosAndConcentrations.r238_235t.getName()),
                    molU235t},
                parDerivTerms);

        // determine sampleU alphaU ********************************************
        // aug 2010 force a calculation to set alphaU to meanAlphaU if it exits
        // need to test here too ... refactor?
        if (((AlphaU) alphaU).getFractionMeanAlphaU().compareTo(BigDecimal.ZERO) == 1) {
            ((AlphaU) alphaU).setModelCopy(false);
            alphaU.calculateValue(null, null);
            fraction.setAnalysisMeasureByName(AnalysisMeasures.alphaU.getName(), alphaU);
        }

        if (tracerType.equalsIgnoreCase("mixed 205-233-236")
                || tracerType.equalsIgnoreCase("mixed 202-205-233-236")) {

            if (((UPbFractionI) fraction).hasMeasuredUranium()) {
                // Calculate AlphaU_233_236 Tracer As Metal *******************************************
                ((AlphaU) alphaU).setModelCopy(false);
                ((AlphaU) alphaU).setDoubleSpike(false);
                alphaU.calculateValue(
                        new ValueModel[]{
                            fractionTracer.getDatumByName(TracerUPbRatiosAndConcentrations.r233_236t.getName()),
                            r233_236oc},//((UPbFraction) fraction).getMeasuredRatioByName( MeasuredRatios.r233_236m.getName() )},
                        parDerivTerms);
                fraction.setAnalysisMeasureByName(AnalysisMeasures.alphaU.getName(), alphaU);

                // jan 2011 new math for molu238 here
                ((MolU238s) molU238s).setTracerIsMixed233_236(true);
                molU238s.calculateValue(
                        new ValueModel[]{
                            fraction.getAnalysisMeasure(AnalysisMeasures.alphaU.getName()),
                            molU236t,
                            molU238b,
                            r238_236oc,//fraction.getMeasuredRatioByName( MeasuredRatios.r238_236m.getName() ),
                            fractionTracer.getDatumByName(TracerUPbRatiosAndConcentrations.r238_233t.getName()),
                            fractionTracer.getDatumByName(TracerUPbRatiosAndConcentrations.r233_236t.getName())},
                        parDerivTerms);
                ((UPbFraction) fraction).setOutputByName("molU238s", molU238s);

                // jan 2011 new math for molu235 here
                ((MolU235s) molU235s).setTracerIsMixed233_236(true);
                molU235s.calculateValue(
                        new ValueModel[]{
                            molU238s,
                            fraction.getAnalysisMeasure(AnalysisMeasures.r238_235s.getName())},
                        parDerivTerms);
                ((UPbFraction) fraction).setOutputByName("molU235s", molU235s);
            }
        }

        if (!((UPbFractionI) fraction).isFractionationCorrectedU()) {//.getMeanAlphaU().compareTo( BigDecimal.ZERO ) == 0 ) {
            if (tracerType.equalsIgnoreCase("mixed 205-233-235")
                    || tracerType.equalsIgnoreCase("mixed 202-205-233-235")
                    || tracerType.equalsIgnoreCase("mixed 205-233-235-230Th")) {

                // calculate double spike
                ((MolU233t) molU233t).setDoubleSpike(true);
                molU233t.calculateValue(
                        new ValueModel[]{
                            fractionTracer.getDatumByName(TracerUPbRatiosAndConcentrations.r233_235t.getName()),
                            molU235t},
                        parDerivTerms);
                ((UPbFraction) fraction).setOutputByName("molU233t", molU233t);

                if (((UPbFractionI) fraction).hasMeasuredUranium()) {//********************************* ?? URANIUM
                    ((MolU235s) molU235s).setDoubleSpike(true);
                    molU235s.calculateValue(
                            new ValueModel[]{
                                // fraction.getAnalysisMeasure( AnalysisMeasures.r238_235b.getName() ),
                                // fraction.getMeasuredRatioByName( MeasuredRatios.r238_233m.getName() ),
                                r238_235oc,
                                r233_235oc,
                                fraction.getAnalysisMeasure(AnalysisMeasures.r238_235s.getName()),
                                //fractionTracer.getDatumByName( TracerRatiosEnum.r238_235t.getName() ),
                                //fractionTracer.getDatumByName( TracerRatiosEnum.r233_235t.getName() ),
                                //fractionTracer.getDatumByName( TracerRatiosEnum.r238_233t.getName() ),
                                molU235b,
                                molU238b,
                                molU235t,
                                molU238t,
                                ((UPbFraction) fraction).getOutputsByName("molU233t"),
                                fraction.getMeasuredRatioByName(MeasuredRatios.r233_235m.getName())},
                            parDerivTerms);
                    ((UPbFraction) fraction).setOutputByName("molU235s", molU235s);
                }

                ((AlphaU) alphaU).setModelCopy(false);
                ((AlphaU) alphaU).setDoubleSpike(true);
                alphaU.calculateValue(
                        new ValueModel[]{
                            r233_235oc,
                            r238_235oc,
                            fraction.getAnalysisMeasure(AnalysisMeasures.r238_235s.getName()),
                            ((UPbFraction) fraction).getOutputsByName("molU233t"),
                            molU235b,
                            molU235t,
                            molU238b,
                            molU238t,
                            molU235s},
                        parDerivTerms);
                fraction.setAnalysisMeasureByName(AnalysisMeasures.alphaU.getName(), alphaU);
                // jan 2011 moved here because now tracer as a metal calculates molu238s and molu235s differently
                molU238s.calculateValue(
                        new ValueModel[]{
                            ((UPbFraction) fraction).getOutputsByName("molU235s"),
                            fraction.getAnalysisMeasure(AnalysisMeasures.r238_235s.getName())},
                        parDerivTerms);
                ((UPbFraction) fraction).setOutputByName("molU238s", molU238s);
            } // else { // modifed jan 2011 to simplify logic
            else if (tracerType.equalsIgnoreCase("mixed 205-235")
                    || tracerType.equalsIgnoreCase("mixed 208-235")) {
                ((MolU233t) molU233t).setDoubleSpike(false);
                molU233t.calculateValue(null, null);
                ((UPbFraction) fraction).setOutputByName("molU233t", molU233t);

                ((MolU235s) molU235s).setDoubleSpike(false);
                molU235s.calculateValue(
                        new ValueModel[]{
                            fraction.getAnalysisMeasure(AnalysisMeasures.alphaU.getName()),
                            ((UPbFraction) fraction).getOutputsByName("molU233t"),
                            molU235t,
                            molU238t,
                            molU235b,
                            molU238b,
                            //fraction.getAnalysisMeasure( AnalysisMeasures.r238_235b.getName() ),
                            r238_235oc,
                            fraction.getAnalysisMeasure(AnalysisMeasures.r238_235s.getName())},
                        parDerivTerms);
                ((UPbFraction) fraction).setOutputByName("molU235s", molU235s);

                // jan 2011 moved here because now tracer as a metal calculates molu238s and molu235s differently
                molU238s.calculateValue(
                        new ValueModel[]{
                            ((UPbFraction) fraction).getOutputsByName("molU235s"),
                            fraction.getAnalysisMeasure(AnalysisMeasures.r238_235s.getName())},
                        parDerivTerms);
                ((UPbFraction) fraction).setOutputByName("molU238s", molU238s);
            }
        }

        // 3. Ages
        // 3a. radiogenic isotope ratios
        r206_238r.calculateValue(
                new ValueModel[]{
                    molPb206r,
                    ((UPbFraction) fraction).getOutputsByName("molU238s")},
                parDerivTerms);

        r207_235r.calculateValue(
                new ValueModel[]{
                    molPb207r,
                    ((UPbFraction) fraction).getOutputsByName("molU235s")},
                parDerivTerms);

        r207_206r.calculateValue(
                new ValueModel[]{
                    molPb207r,
                    molPb206r},
                parDerivTerms);

        r208_206r.calculateValue(
                new ValueModel[]{
                    molPb208r,
                    molPb206r},
                parDerivTerms);

        r206_204r.calculateValue(
                new ValueModel[]{},
                parDerivTerms);

        r208_232r.calculateValue(
                new ValueModel[]{},
                parDerivTerms);

        // 3b. radiogenic isotope ages
        age206_238r.calculateValue(
                new ValueModel[]{
                    r206_238r,
                    lambda238},
                parDerivTerms);

        age207_235r.calculateValue(
                new ValueModel[]{
                    r207_235r,
                    lambda235},
                parDerivTerms);

        age207_206r.calculateValue(
                new ValueModel[]{
                    fraction.getAnalysisMeasure(AnalysisMeasures.r238_235s.getName()),
                    r207_206r,
                    age206_238r,
                    lambda235,
                    lambda238},
                parDerivTerms);

        age208_232r.calculateValue(
                new ValueModel[]{
                    r208_232r,
                    lambda232},
                parDerivTerms);

        // 3c. Th and Pa correction ****************************************
        if (((UPbFraction) fraction).hasMeasuredUranium()) {//********************************* ?? URANIUM
            molsU.calculateValue(
                    new ValueModel[]{
                        molU235b,
                        molU238b,
                        ((UPbFraction) fraction).getOutputsByName("molU235s"),
                        ((UPbFraction) fraction).getOutputsByName("molU238s"),
                        ((UPbFraction) fraction).getOutputsByName("molU233t"),
                        molU235t,
                        molU238t,
                        fraction.getMeasuredRatioByName(MeasuredRatios.r238_233m.getName()),
                        fraction.getMeasuredRatioByName(MeasuredRatios.r238_235m.getName()),
                        fraction.getAnalysisMeasure(AnalysisMeasures.r238_235s.getName())},
                    parDerivTerms);
        }

        age206_238r_Th.calculateValue(
                new ValueModel[]{
                    lambda230,
                    lambda232,
                    lambda238,
                    age206_238r,
                    r206_238r,
                    molPb208r,
                    ((UPbFraction) fraction).getOutputsByName("molU235s"),
                    ((UPbFraction) fraction).getOutputsByName("molU238s"),
                    fraction.getAnalysisMeasure(AnalysisMeasures.rTh_Umagma.getName()),
                    molsU,
                    fraction.getAnalysisMeasure(AnalysisMeasures.r238_235s.getName()),
                    molPb205t,
                    alphaPb,
                    r204_205fc,
                    fractionTracer.getDatumByName(TracerUPbRatiosAndConcentrations.r204_205t.getName()),},
                parDerivTerms);

        molTh232s.calculateValue(
                new ValueModel[]{
                    lambda232,
                    age206_238r_Th,
                    molPb208r},
                parDerivTerms);

        molPb206r_Th.calculateValue(
                new ValueModel[]{
                    lambda238,
                    age206_238r_Th,
                    ((UPbFraction) fraction).getOutputsByName("molU238s")},
                parDerivTerms);

        r206_238r_Th.calculateValue(
                new ValueModel[]{
                    molPb206r_Th,
                    ((UPbFraction) fraction).getOutputsByName("molU238s")},
                parDerivTerms);

        r207_206r_Th.calculateValue(
                new ValueModel[]{
                    molPb207r,
                    molPb206r_Th},
                parDerivTerms);

        age207_206r_Th.calculateValue(
                new ValueModel[]{
                    fraction.getAnalysisMeasure(AnalysisMeasures.r238_235s.getName()),
                    r207_206r_Th,
                    age207_206r,
                    lambda235,
                    lambda238},
                parDerivTerms);

        age207_235r_Pa.calculateValue(
                new ValueModel[]{
                    fraction.getAnalysisMeasure(AnalysisMeasures.ar231_235sample.getName()),
                    r207_235r,
                    lambda235,
                    lambda231},
                parDerivTerms);

        molPb207r_Pa.calculateValue(
                new ValueModel[]{
                    //jan 2011 fraction.getAnalysisMeasure( AnalysisMeasures.ar231_235sample.getName() ),
                    age207_235r_Pa,
                    lambda235,
                    //jan 2011 lambda231,
                    ((UPbFraction) fraction).getOutputsByName("molU235s")},
                parDerivTerms);

        r207_206r_Pa.calculateValue(
                new ValueModel[]{
                    //jan 2011 molPb207r,
                    molPb207r_Pa,
                    molPb206r},
                parDerivTerms);

        r207_235r_Pa.calculateValue(
                new ValueModel[]{
                    //jan 2011 molPb207r,
                    ((UPbFraction) fraction).getOutputsByName("molU235s"),
                    molPb207r_Pa},
                parDerivTerms);

        age207_206r_Pa.calculateValue(
                new ValueModel[]{
                    fraction.getAnalysisMeasure(AnalysisMeasures.r238_235s.getName()),
                    r207_206r_Pa,
                    age207_206r,
                    lambda235,
                    lambda238},
                parDerivTerms);

        r207_206r_ThPa.calculateValue(
                new ValueModel[]{
                    //jan 2011 molPb207r,
                    molPb206r_Th,
                    molPb207r_Pa},
                parDerivTerms);

        age207_206r_ThPa.calculateValue(
                new ValueModel[]{
                    fraction.getAnalysisMeasure(AnalysisMeasures.r238_235s.getName()),
                    r207_206r_ThPa,
                    age207_206r,
                    lambda235,
                    lambda238},
                parDerivTerms);

        // 4. Isochron Ratios ********************************************
        r206_204tfc.calculateValue(
                new ValueModel[]{
                    molPb205t,
                    fraction.getMeasuredRatioByName(MeasuredRatios.r206_205m.getName()),
                    alphaPb,
                    fractionTracer.getDatumByName(TracerUPbRatiosAndConcentrations.r206_205t.getName()),
                    molPb204c,
                    molPb204b},
                parDerivTerms);

        r204_206s.calculateValue(
                new ValueModel[]{
                    molPb204c,
                    molPb206s},
                parDerivTerms);

        r238_204s.calculateValue(
                new ValueModel[]{
                    molPb204c,
                    ((UPbFraction) fraction).getOutputsByName("molU238s")},
                parDerivTerms);

        rhoR238_204s__r206_204s.calculateValue(
                new ValueModel[]{},
                parDerivTerms);

        r235_204s.calculateValue(
                new ValueModel[]{
                    molPb204c,
                    ((UPbFraction) fraction).getOutputsByName("molU235s")},
                parDerivTerms);

        r204_207s.calculateValue(
                new ValueModel[]{
                    molPb204c,
                    molPb207s},
                parDerivTerms);

        rhoR235_204s__r207_204s.calculateValue(
                new ValueModel[]{},
                parDerivTerms);

        r232_204s.calculateValue(
                new ValueModel[]{},
                parDerivTerms);

        r208_204s.calculateValue(
                new ValueModel[]{},
                parDerivTerms);

        rhoR202_204s__r208_204s.calculateValue(
                new ValueModel[]{},
                parDerivTerms);

        r238_206s.calculateValue(
                new ValueModel[]{
                    ((UPbFraction) fraction).getOutputsByName("molU238s"),
                    molPb206s},
                parDerivTerms);

        r207_206s.calculateValue(
                new ValueModel[]{
                    molPb207s,
                    molPb206s},
                parDerivTerms);

        rhoR238_206s__r207_206s.calculateValue(
                new ValueModel[]{},
                parDerivTerms);

        rhoR207_206s__r204_206s.calculateValue(
                new ValueModel[]{},
                parDerivTerms);

        r238_207s.calculateValue(
                new ValueModel[]{
                    ((UPbFraction) fraction).getOutputsByName("molU238s"),
                    molPb207s},
                parDerivTerms);

        r235_207s.calculateValue(
                new ValueModel[]{
                    ((UPbFraction) fraction).getOutputsByName("molU235s"),
                    molPb207s},
                parDerivTerms);

        // 5. Outputs
        // 5a. Pb calculations
        initCommonPbMass.calculateValue(
                new ValueModel[]{
                    molPb204c,
                    gmol204,
                    ((UPbFraction) fraction).getInitialPbModel().getDatumByName("r206_204c"),
                    gmol206,
                    ((UPbFraction) fraction).getInitialPbModel().getDatumByName("r207_204c"),
                    gmol207,
                    ((UPbFraction) fraction).getInitialPbModel().getDatumByName("r208_204c"),
                    gmol204},
                null);

        totCommonPbMass.calculateValue(
                new ValueModel[]{
                    blankPbMass,
                    initCommonPbMass},
                null);

        totRadiogenicPbMass.calculateValue(
                new ValueModel[]{
                    molPb206r,
                    gmol206,
                    molPb207r,
                    gmol207,
                    molPb208r,
                    gmol208
                },
                null);

        try {
            radToCommonPb206.calculateValue(
                    new ValueModel[]{
                        molPb206r,
                        molPb206b,
                        molPb206c,
                        new ValueModel(//
                                //
                                "molPb206t",
                                fractionTracer.getDatumByName(TracerUPbRatiosAndConcentrations.r206_205t.getName()).getValue().//
                                multiply(molPb205t.getValue()),
                                "ABS",
                                BigDecimal.ZERO, BigDecimal.ZERO)},
                    null);
        } catch (Exception e) {
        }
        try {
            radToCommonPb207.calculateValue(
                    new ValueModel[]{
                        molPb207r,
                        molPb207b,
                        molPb207c,
                        new ValueModel(//
                                //
                                "molPb207t",
                                fractionTracer.getDatumByName(TracerUPbRatiosAndConcentrations.r207_205t.getName()).getValue().//
                                multiply(molPb205t.getValue()),
                                "ABS",
                                BigDecimal.ZERO, BigDecimal.ZERO)},
                    null);
        } catch (Exception e) {
        }

        try {
            radToCommonPb208.calculateValue(
                    new ValueModel[]{
                        molPb208r,
                        molPb208b,
                        molPb208c,
                        new ValueModel(//
                                //
                                "molPb208t",
                                fractionTracer.getDatumByName(TracerUPbRatiosAndConcentrations.r208_205t.getName()).getValue().//
                                multiply(molPb205t.getValue()),
                                "ABS",
                                BigDecimal.ZERO, BigDecimal.ZERO)},
                    null);
        } catch (Exception e) {
        }

        percentDiscordance.calculateValue(
                new ValueModel[]{
                    age206_238r,
                    age207_206r},
                null);

        massU.calculateValue(
                new ValueModel[]{
                    ((UPbFraction) fraction).getOutputsByName("molU235s"),
                    gmol235,
                    ((UPbFraction) fraction).getOutputsByName("molU238s"),
                    gmol238},
                null);

        concU.calculateValue(
                new ValueModel[]{
                    massU,
                    fraction.getAnalysisMeasure(AnalysisMeasures.fractionMass.getName())},
                null);

        concTh.calculateValue(
                new ValueModel[]{},
                null);

        rTh_Usample.calculateValue(
                new ValueModel[]{
                    molTh232s,
                    ((UPbFraction) fraction).getOutputsByName("molU238s"),
                    ((UPbFraction) fraction).getOutputsByName("molU235s")},
                null);

        massPb_rib.calculateValue(
                new ValueModel[]{
                    totRadiogenicPbMass,
                    totCommonPbMass},
                null);

        concPb_rib.calculateValue(
                new ValueModel[]{
                    massPb_rib,
                    fraction.getAnalysisMeasure(AnalysisMeasures.fractionMass.getName())},
                null);

        concPb_r.calculateValue(
                new ValueModel[]{
                    totRadiogenicPbMass,
                    fraction.getAnalysisMeasure(AnalysisMeasures.fractionMass.getName())},
                null);

        concPb_i.calculateValue(
                new ValueModel[]{
                    initCommonPbMass,
                    fraction.getAnalysisMeasure(AnalysisMeasures.fractionMass.getName())},
                null);

        concPb_ib.calculateValue(
                new ValueModel[]{
                    blankPbMass,
                    initCommonPbMass,
                    fraction.getAnalysisMeasure(AnalysisMeasures.fractionMass.getName())},
                null);

        concPb206_rib.calculateValue(
                new ValueModel[]{
                    gmol206,
                    molPb206r,
                    molPb206b,
                    molPb206c,
                    fraction.getAnalysisMeasure(AnalysisMeasures.fractionMass.getName())},
                null);

        concPb206_r.calculateValue(
                new ValueModel[]{
                    gmol206,
                    molPb206r,
                    fraction.getAnalysisMeasure(AnalysisMeasures.fractionMass.getName())},
                null);

        concPb206_ib.calculateValue(
                new ValueModel[]{
                    gmol206,
                    molPb206b,
                    molPb206c,
                    fraction.getAnalysisMeasure(AnalysisMeasures.fractionMass.getName())},
                null);

        radToCommonTotal.calculateValue(
                new ValueModel[]{
                    totRadiogenicPbMass,
                    totCommonPbMass},
                null);

        ValueModel uTracerMassInGrams = new ValueModel("uTracerMassInGrams", "ABS");
//        BigDecimal molU236txxx = BigDecimal.ZERO;
//        try {
//            molU236txxx = //
//                    ((UPbFraction) fraction).getOutputsByName( "molU233t" ).getValue().//
//                    divide( fractionTracer.getDatumByName( TracerRatiosEnum.r233_236t.getName() ).getValue(), //
//                    ReduxConstants.mathContext15 );
//        } catch (Exception e) {
//        }

        uTracerMassInGrams.setValue(//
                ((UPbFraction) fraction).getOutputsByName("molU233t").getValue().//
                multiply(new BigDecimal(233.0)).//
                add(molU235t.getValue().//
                        multiply(gmol235.getValue())).//
                add(molU236t.getValue().//
                        multiply(new BigDecimal(236.0))).//
                add(molU238t.getValue().//
                        multiply(gmol238.getValue())));

        ((UPbFraction) fraction).setOutputByName("uTracerMassInGrams", uTracerMassInGrams);

        ValueModel uSampleMassInGrams = new ValueModel("uSampleMassInGrams", "ABS");
        uSampleMassInGrams.setValue(//
                ((UPbFraction) fraction).getOutputsByName("molU235s").getValue().//
                multiply(gmol235.getValue()).//
                add(((UPbFraction) fraction).getOutputsByName("molU238s").getValue().//
                        multiply(gmol238.getValue())));

        ((UPbFraction) fraction).setOutputByName("uSampleMassInGrams", uSampleMassInGrams);

        // July 2011
        if (alphaPb instanceof AlphaPb) {
            ((AlphaPb) alphaPb).restoreValueFromFractionMeanAlphaPb();
        }

    } // end evaluateVariablesInOrderII

    private static void initializeAndEvalSpecialInputVariablesInOrder(ETFractionInterface fraction) {

        specialInputVariablesInOrder = new TreeMap<>();

        int index = 0;

        r238_235oc = new R238_235oc();
        specialInputVariablesInOrder.put(index++, r238_235oc);

        r233_235oc = new R233_235oc();
        specialInputVariablesInOrder.put(index++, r233_235oc);

        // added jan 2011 for tracer type 236
        r238_236oc = new R238_236oc();
        specialInputVariablesInOrder.put(index++, r238_236oc);

        r233_236oc = new R233_236oc();
        specialInputVariablesInOrder.put(index++, r233_236oc);

        if (((UPbFraction) fraction).hasMeasuredUranium()) {
            r238_235oc.calculateValue(
                    new ValueModel[]{
                        fraction.getAnalysisMeasure(AnalysisMeasures.r18O_16O_revised.getName()),
                        fraction.getAnalysisMeasure(AnalysisMeasures.r265_267m.getName()),
                        fraction.getAnalysisMeasure(AnalysisMeasures.r270_267m.getName()),
                        fraction.getAnalysisMeasure(AnalysisMeasures.r270_265m.getName())},
                    parDerivTerms);

            r233_235oc.calculateValue(
                    new ValueModel[]{
                        fraction.getAnalysisMeasure(AnalysisMeasures.r18O_16O_revised.getName()),
                        fraction.getAnalysisMeasure(AnalysisMeasures.r265_267m.getName())},
                    parDerivTerms);

            // jan 2011 for tracer type 236
            r238_236oc.calculateValue(
                    new ValueModel[]{
                        fraction.getAnalysisMeasure(AnalysisMeasures.r18O_16O_revised.getName()),
                        fraction.getAnalysisMeasure(AnalysisMeasures.r270_268m.getName())},
                    parDerivTerms);

            r233_236oc.calculateValue(
                    new ValueModel[]{
                        fraction.getAnalysisMeasure(AnalysisMeasures.r265_268m.getName())},
                    parDerivTerms);

        }

    }

    private static void initializeVariablesInOrder(ETFractionInterface fraction) {

        variablesInOrder = new TreeMap<>();
        int index = 0;

//        // oct 2009 initialize outputs
//        ((UPbFraction) fraction).setOutputs(new ValueModel[0]);
        alphaPb = new AlphaPb(((UPbFraction) fraction).getMeanAlphaPb());
        //   handled during evaluation: fraction.setAnalysisMeasureByName(AnalysisMeasures.alphaPb.getName(), alphaPb);
        variablesInOrder.put(index++, alphaPb);

        molPb205t = new MolPb205t();
        variablesInOrder.put(index++, molPb205t);

        r204_205fc = new R204_205fc();
        variablesInOrder.put(index++, r204_205fc);

        molPb204tc = new MolPb204tc();
        variablesInOrder.put(index++, molPb204tc);

        blankPbGramsMol = new BlankPbGramsMol(
                ((UPbFraction) fraction).getPbBlank(),
                ((UPbFraction) fraction).getPhysicalConstantsModel());
        variablesInOrder.put(index++, blankPbGramsMol);

        molPb204b = new MolPb204b();
        variablesInOrder.put(index++, molPb204b);

        molPb206b = new MolPb206b();
        variablesInOrder.put(index++, molPb206b);

        molPb207b = new MolPb207b();
        variablesInOrder.put(index++, molPb207b);

        molPb208b = new MolPb208b();
        variablesInOrder.put(index++, molPb208b);

        molPb204c = new MolPb204c();
        variablesInOrder.put(index++, molPb204c);

        molPb206c = new MolPb206c();
        variablesInOrder.put(index++, molPb206c);

        molPb207c = new MolPb207c();
        variablesInOrder.put(index++, molPb207c);

        molPb208c = new MolPb208c();
        variablesInOrder.put(index++, molPb208c);

        blankPbMass = new ValueModel("blankPbMass", "ABS");
        variablesInOrder.put(index++, blankPbMass);

        molPb206r = new MolPb206r();
        ((UPbFraction) fraction).setOutputByName("molPb206r", molPb206r);
        variablesInOrder.put(index++, molPb206r);

        molPb207r = new MolPb207r();
        variablesInOrder.put(index++, molPb207r);

        molPb208r = new MolPb208r();
        variablesInOrder.put(index++, molPb208r);

        molPb206s = new MolPb206s();
        variablesInOrder.put(index++, molPb206s);

        molPb207s = new MolPb207s();
        variablesInOrder.put(index++, molPb207s);

        molPb208s = new MolPb208s();
        variablesInOrder.put(index++, molPb208s);

        molU235b = new MolU235b();
        ((UPbFraction) fraction).setOutputByName("molU235b", molU235b);
        variablesInOrder.put(index++, molU235b);

        molU238b = new MolU238b();
        ((UPbFraction) fraction).setOutputByName("molU238b", molU238b);
        variablesInOrder.put(index++, molU238b);

        molU235t = new MolU235t();
        ((UPbFraction) fraction).setOutputByName("molU235t", molU235t);
        variablesInOrder.put(index++, molU235t);

        // added jan 2011
        molU236t = new MolU236t();
        ((UPbFraction) fraction).setOutputByName("molU236t", molU236t);
        variablesInOrder.put(index++, molU236t);

        molU238t = new MolU238t();
        ((UPbFraction) fraction).setOutputByName("molU238t", molU238t);
        variablesInOrder.put(index++, molU238t);

        molU233t = new MolU233t();
        ((UPbFraction) fraction).setOutputByName("molU233t", molU233t);
        variablesInOrder.put(index++, molU233t);

        molU235s = new MolU235s();
        if (!((UPbFraction) fraction).isInAutoUraniumMode()) {
            ((UPbFraction) fraction).setOutputByName("molU235s", molU235s);
        }
        variablesInOrder.put(index++, molU235s);

        alphaU = new AlphaU(((UPbFraction) fraction).getMeanAlphaU());
        ((AlphaU) alphaU).setModelCopy(true);
        variablesInOrder.put(index++, alphaU);

        molU238s = new MolU238s();
        if (!((UPbFraction) fraction).isInAutoUraniumMode()) {
            ((UPbFraction) fraction).setOutputByName("molU238s", molU238s);
        }
        variablesInOrder.put(index++, molU238s);

        r206_238r = new R206_238r();
        fraction.setRadiogenicIsotopeRatioByName("r206_238r", r206_238r);
        variablesInOrder.put(index++, r206_238r);

        r207_235r = new R207_235r();
        fraction.setRadiogenicIsotopeRatioByName("r207_235r", r207_235r);
        variablesInOrder.put(index++, r207_235r);

        r207_206r = new R207_206r();
        fraction.setRadiogenicIsotopeRatioByName("r207_206r", r207_206r);
        variablesInOrder.put(index++, r207_206r);

        r208_206r = new R208_206r();
        fraction.setRadiogenicIsotopeRatioByName("r208_206r", r208_206r);
        variablesInOrder.put(index++, r208_206r);

        r206_204r = new R206_204r();
        fraction.setRadiogenicIsotopeRatioByName("r206_204r", r206_204r);
        variablesInOrder.put(index++, r206_204r);

        r208_232r = new R208_232r();
        fraction.setRadiogenicIsotopeRatioByName("r208_232r", r208_232r);
        variablesInOrder.put(index++, r208_232r);

        r206_238r_Th = new R206_238r_Th();
        fraction.setRadiogenicIsotopeRatioByName("r206_238r_Th", r206_238r_Th);
        variablesInOrder.put(index++, r206_238r_Th);

        age206_238r = new Age206_238r();
        fraction.setRadiogenicIsotopeDateByName(RadDates.age206_238r, age206_238r);
        variablesInOrder.put(index++, age206_238r);

        age207_235r = new Age207_235r();
        fraction.setRadiogenicIsotopeDateByName(RadDates.age207_235r, age207_235r);
        variablesInOrder.put(index++, age207_235r);

        age207_206r = new Age207_206r();
        fraction.setRadiogenicIsotopeDateByName(RadDates.age207_206r, age207_206r);
        variablesInOrder.put(index++, age207_206r);

        age208_232r = new Age208_232r();
        fraction.setRadiogenicIsotopeDateByName(RadDates.age208_232r, age208_232r);
        variablesInOrder.put(index++, age208_232r);

        molsU = new MolsU();
        variablesInOrder.put(index++, molsU);

        age206_238r_Th = new Age206_238r_Th();
        fraction.setRadiogenicIsotopeDateByName(RadDates.age206_238r_Th, age206_238r_Th);
        variablesInOrder.put(index++, age206_238r_Th);

        molTh232s = new MolTh232s();
        variablesInOrder.put(index++, molTh232s);

        molPb206r_Th = new MolPb206r_Th();
        variablesInOrder.put(index++, molPb206r_Th);

        r207_206r_Th = new R207_206r_Th();
        fraction.setRadiogenicIsotopeRatioByName("r207_206r_Th", r207_206r_Th);
        variablesInOrder.put(index++, r207_206r_Th);

        age207_206r_Th = new Age207_206r_Th();
        fraction.setRadiogenicIsotopeDateByName(RadDates.age207_206r_Th, age207_206r_Th);
        variablesInOrder.put(index++, age207_206r_Th);

        age207_235r_Pa = new Age207_235r_Pa();
        fraction.setRadiogenicIsotopeDateByName(RadDates.age207_235r_Pa, age207_235r_Pa);
        variablesInOrder.put(index++, age207_235r_Pa);

        molPb207r_Pa = new MolPb207r_Pa();
        variablesInOrder.put(index++, molPb207r_Pa);

        r207_206r_Pa = new R207_206r_Pa();
        fraction.setRadiogenicIsotopeRatioByName("r207_206r_Pa", r207_206r_Pa);
        variablesInOrder.put(index++, r207_206r_Pa);

        r207_235r_Pa = new R207_235r_Pa();
        fraction.setRadiogenicIsotopeRatioByName("r207_235r_Pa", r207_235r_Pa);
        variablesInOrder.put(index++, r207_235r_Pa);

        age207_206r_Pa = new Age207_206r_Pa();
        fraction.setRadiogenicIsotopeDateByName(RadDates.age207_206r_Pa.getName(), age207_206r_Pa);
        variablesInOrder.put(index++, age207_206r_Pa);

        r207_206r_ThPa = new R207_206r_ThPa();
        fraction.setRadiogenicIsotopeRatioByName("r207_206r_ThPa", r207_206r_ThPa);
        variablesInOrder.put(index++, r207_206r_ThPa);

        age207_206r_ThPa = new Age207_206r_ThPa();
        fraction.setRadiogenicIsotopeDateByName(RadDates.age207_206r_ThPa.getName(), age207_206r_ThPa);
        variablesInOrder.put(index++, age207_206r_ThPa);

        r206_204tfc = new R206_204tfc();
        fraction.setSampleIsochronRatiosByName("r206_204tfc", r206_204tfc);
        variablesInOrder.put(index++, r206_204tfc);

        r204_206s = new R204_206s();
        fraction.setSampleIsochronRatiosByName("r204_206s", r204_206s);
        variablesInOrder.put(index++, r204_206s);

        r238_204s = new R238_204s();
        fraction.setSampleIsochronRatiosByName("r238_204s", r238_204s);
        variablesInOrder.put(index++, r238_204s);

        rhoR238_204s__r206_204s = new RhoR238_204s__r206_204s();
        fraction.setSampleIsochronRatiosByName("rhoR238_204s__r206_204s", rhoR238_204s__r206_204s);
        variablesInOrder.put(index++, rhoR238_204s__r206_204s);

        r235_204s = new R235_204s();
        fraction.setSampleIsochronRatiosByName("r235_204s", r235_204s);
        variablesInOrder.put(index++, r235_204s);

        r204_207s = new R204_207s();
        fraction.setSampleIsochronRatiosByName("r204_207s", r204_207s);
        variablesInOrder.put(index++, r204_207s);

        rhoR235_204s__r207_204s = new RhoR235_204s__r207_204s();
        fraction.setSampleIsochronRatiosByName("rhoR235_204s__r207_204s", rhoR235_204s__r207_204s);
        variablesInOrder.put(index++, rhoR235_204s__r207_204s);

        r232_204s = new R232_204s();
        fraction.setSampleIsochronRatiosByName("r232_204s", r232_204s);
        variablesInOrder.put(index++, r232_204s);

        r208_204s = new R208_204s();
        fraction.setSampleIsochronRatiosByName("r208_204s", r208_204s);
        variablesInOrder.put(index++, r208_204s);

        rhoR202_204s__r208_204s = new RhoR202_204s__r208_204s();
        fraction.setSampleIsochronRatiosByName("rhoR202_204s__r208_204s", rhoR202_204s__r208_204s);
        variablesInOrder.put(index++, rhoR202_204s__r208_204s);

        r238_206s = new R238_206s();
        fraction.setSampleIsochronRatiosByName("r238_206s", r238_206s);
        variablesInOrder.put(index++, r238_206s);

        r207_206s = new R207_206s();
        fraction.setSampleIsochronRatiosByName("r207_206s", r207_206s);
        variablesInOrder.put(index++, r207_206s);

        rhoR238_206s__r207_206s = new RhoR238_206s__r207_206s();
        fraction.setSampleIsochronRatiosByName("rhoR238_206s__r207_206s", rhoR238_206s__r207_206s);
        variablesInOrder.put(index++, rhoR238_206s__r207_206s);

        rhoR207_206s__r204_206s = new RhoR207_206s__r204_206s();
        fraction.setSampleIsochronRatiosByName("rhoR207_206s__r204_206s", rhoR207_206s__r204_206s);
        variablesInOrder.put(index++, rhoR207_206s__r204_206s);

        r238_207s = new R238_207s();
        fraction.setSampleIsochronRatiosByName("r238_207s", r238_207s);
        variablesInOrder.put(index++, r238_207s);

        r235_207s = new R235_207s();
        fraction.setSampleIsochronRatiosByName("r235_207s", r235_207s);
        variablesInOrder.put(index++, r235_207s);

        initCommonPbMass = new InitCommonPbMass();
        ((UPbFraction) fraction).setOutputByName("initCommonPbMass", initCommonPbMass);
        variablesInOrder.put(index++, initCommonPbMass);

        totCommonPbMass = new TotCommonPbMass();
        fraction.setCompositionalMeasureByName("totCommonPbMass", totCommonPbMass);
        variablesInOrder.put(index++, totCommonPbMass);

        totRadiogenicPbMass = new TotRadiogenicPbMass();
        fraction.setCompositionalMeasureByName("totRadiogenicPbMass", totRadiogenicPbMass);
        variablesInOrder.put(index++, totRadiogenicPbMass);

        radToCommonPb206 = new RadToCommonPb206();
        ((UPbFraction) fraction).setOutputByName("radToCommonPb206", radToCommonPb206);
        variablesInOrder.put(index++, radToCommonPb206);

        radToCommonPb207 = new RadToCommonPb207();
        ((UPbFraction) fraction).setOutputByName("radToCommonPb207", radToCommonPb207);
        variablesInOrder.put(index++, radToCommonPb207);

        radToCommonPb208 = new RadToCommonPb208();
        ((UPbFraction) fraction).setOutputByName("radToCommonPb208", radToCommonPb208);
        variablesInOrder.put(index++, radToCommonPb208);

        percentDiscordance = new PercentDiscordance();
        fraction.setRadiogenicIsotopeDateByName(RadDates.percentDiscordance, percentDiscordance);
        variablesInOrder.put(index++, percentDiscordance);

        massU = new MassU();
        fraction.setCompositionalMeasureByName("massU", massU);
        variablesInOrder.put(index++, massU);

        concU = new ConcU();
        fraction.setCompositionalMeasureByName("concU", concU);
        variablesInOrder.put(index++, concU);

        concTh = new ConcTh();
        fraction.setCompositionalMeasureByName("concTh", concTh);
        variablesInOrder.put(index++, concTh);

        rTh_Usample = new RTh_Usample();
        fraction.setCompositionalMeasureByName("rTh_Usample", rTh_Usample);
        variablesInOrder.put(index++, rTh_Usample);

        massPb_rib = new MassPb_rib();
        fraction.setCompositionalMeasureByName("massPb_rib", massPb_rib);
        variablesInOrder.put(index++, massPb_rib);

        concPb_rib = new ConcPb_rib();
        fraction.setCompositionalMeasureByName("concPb_rib", concPb_rib);
        variablesInOrder.put(index++, concPb_rib);

        concPb_r = new ConcPb_r();
        fraction.setCompositionalMeasureByName("concPb_r", concPb_r);
        variablesInOrder.put(index++, concPb_r);

        concPb_i = new ConcPb_i();
        fraction.setCompositionalMeasureByName("concPb_i", concPb_i);
        variablesInOrder.put(index++, concPb_i);

        concPb_ib = new ConcPb_ib();
        fraction.setCompositionalMeasureByName("concPb_ib", concPb_ib);
        variablesInOrder.put(index++, concPb_ib);

        concPb206_rib = new ConcPb206_rib();
        fraction.setCompositionalMeasureByName("concPb206_rib", concPb206_rib);
        variablesInOrder.put(index++, concPb206_rib);

        concPb206_r = new ConcPb206_r();
        fraction.setCompositionalMeasureByName("concPb206_r", concPb206_r);
        variablesInOrder.put(index++, concPb206_r);

        concPb206_ib = new ConcPb206_ib();
        fraction.setCompositionalMeasureByName("concPb206_ib", concPb206_ib);
        variablesInOrder.put(index++, concPb206_ib);

        radToCommonTotal = new RadToCommonTotal();
        fraction.setCompositionalMeasureByName("radToCommonTotal", radToCommonTotal);
        variablesInOrder.put(index++, radToCommonTotal);

    }

    private static void initializeInputVariances(ETFractionInterface fraction) {
        inputVariances = new HashMap<>();

        // June 2012 
        // TODO: refactor to one method
        ValueModel[] fractionPhysConstantsLambdas = fraction.getPhysicalConstantsModel().getData();
        for (int i = 0; i < fractionPhysConstantsLambdas.length; i++) {
            inputVariances.put(//
                    fractionPhysConstantsLambdas[i].getName(), //
                    fractionPhysConstantsLambdas[i].getOneSigmaAbs().pow(2));
        }

        ValueModel[] fractionTracerRatios = ((UPbFraction) fraction).getTracer().getData();
        for (int i = 0; i < fractionTracerRatios.length; i++) {
            inputVariances.put(//
                    fractionTracerRatios[i].getName(), //
                    fractionTracerRatios[i].getOneSigmaAbs().pow(2));
        }

        ValueModel[] fractionMeasuredRatios = ((UPbFraction) fraction).getMeasuredRatios();
        for (int i = 0; i < fractionMeasuredRatios.length; i++) {
            inputVariances.put(//
                    fractionMeasuredRatios[i].getName(), //
                    fractionMeasuredRatios[i].getOneSigmaAbs().pow(2));
        }

        ValueModel[] fractionAnalysisMeasures = ((UPbFraction) fraction).getAnalysisMeasures();
        for (int i = 0; i < fractionAnalysisMeasures.length; i++) {
            inputVariances.put(//
                    fractionAnalysisMeasures[i].getName(), //
                    fractionAnalysisMeasures[i].getOneSigmaAbs().pow(2));
        }

        ValueModel[] fractionPbBlankRatios = ((UPbFraction) fraction).getPbBlank().getData();
        for (int i = 0; i < fractionPbBlankRatios.length; i++) {
            inputVariances.put(//
                    fractionPbBlankRatios[i].getName(), //
                    fractionPbBlankRatios[i].getOneSigmaAbs().pow(2));
        }

        ValueModel[] fractionInitialPbRatios = ((UPbFraction) fraction).getInitialPbModel().getData();
        for (int i = 0; i < fractionInitialPbRatios.length; i++) {
            inputVariances.put(//
                    fractionInitialPbRatios[i].getName(), //
                    fractionInitialPbRatios[i].getOneSigmaAbs().pow(2));
        }
    }

    private static void calculateCovariancesMap(ETFractionInterface fraction) {
        coVariances = new HashMap<>();

        //***********************************************************************
        //TODO:  may 2012 use tracer's new cov matrix ... needs to be refactored more elegantly, but for now to test is ok
        ((UPbFraction) fraction).getTracer().initializeModel();
        AbstractMatrixModel tracerCovariances = ((UPbFraction) fraction).getTracer().getDataCovariancesVarUnct();
        // build and add non-diagonal cov (bottom half)
        Iterator<Integer> rowIterator = tracerCovariances.getRows().keySet().iterator();
        while (rowIterator.hasNext()) {
            int row = rowIterator.next();
            String rowName = tracerCovariances.getRows().get(row);

            for (int i = row + 1; i < tracerCovariances.getRows().keySet().size(); i++) {
                String colName = tracerCovariances.getRows().get(i);
                String covName =//
                        "cov" //
                        + rowName.substring(0, 1).toUpperCase()//
                        + rowName.substring(1)//
                        + "__"
                        + colName;

                if (tracerCovariances.getMatrix().get(row, i) != 0.0) {
                    coVariances.put(covName, new BigDecimal(Double.toString(tracerCovariances.getMatrix().get(row, i))));
                }
            }
        }

        // physical constants covariances here ...waiting on Noah
        ((UPbFraction) fraction).getPhysicalConstantsModel().initializeModel();
        AbstractMatrixModel lambdaCovariances = ((UPbFraction) fraction).getPhysicalConstantsModel().getDataCovariancesVarUnct();
        // build and add non-diagonal cov (bottom half)
        rowIterator = lambdaCovariances.getRows().keySet().iterator();
        while (rowIterator.hasNext()) {
            int row = rowIterator.next();
            String rowName = lambdaCovariances.getRows().get(row);

            for (int i = row + 1; i < lambdaCovariances.getRows().keySet().size(); i++) {
                String colName = lambdaCovariances.getRows().get(i);
                String covName =//
                        "cov" //
                        + rowName.substring(0, 1).toUpperCase()//
                        + rowName.substring(1)//
                        + "__"
                        + colName;

                coVariances.put(covName, new BigDecimal(Double.toString(lambdaCovariances.getMatrix().get(row, i))));
            }
        }
        //***********************************************************************

        try {
            BigDecimal covR204_205m__r206_205m = //
                    fraction.getMeasuredRatioByName(MeasuredRatios.r206_205m.getName()).getValue().//
                    multiply(fraction.getMeasuredRatioByName(MeasuredRatios.r204_205m.getName()).getValue()).//
                    multiply(new BigDecimal("0.5")).movePointLeft(4).//
                    multiply(fraction.getMeasuredRatioByName(MeasuredRatios.r206_205m.getName()).getOneSigmaPct().pow(2).//
                            add(fraction.getMeasuredRatioByName(MeasuredRatios.r204_205m.getName()).getOneSigmaPct().pow(2)).//
                            subtract(fraction.getMeasuredRatioByName(MeasuredRatios.r206_204m.getName()).getOneSigmaPct().pow(2)));
            coVariances.put("covR204_205m__r206_205m", covR204_205m__r206_205m);
        } catch (Exception e) {
            coVariances.put("covR204_205m__r206_205m", BigDecimal.ZERO);
        }

        try {
            BigDecimal covR204_205m__r207_205m = //
                    fraction.getMeasuredRatioByName(MeasuredRatios.r207_205m.getName()).getValue().//
                    multiply(fraction.getMeasuredRatioByName(MeasuredRatios.r204_205m.getName()).getValue()).//
                    multiply(new BigDecimal("0.5")).movePointLeft(4).//
                    multiply(fraction.getMeasuredRatioByName(MeasuredRatios.r207_205m.getName()).getOneSigmaPct().pow(2).//
                            add(fraction.getMeasuredRatioByName(MeasuredRatios.r204_205m.getName()).getOneSigmaPct().pow(2)).//
                            subtract(fraction.getMeasuredRatioByName(MeasuredRatios.r207_204m.getName()).getOneSigmaPct().pow(2)));
            coVariances.put("covR204_205m__r207_205m", covR204_205m__r207_205m);
        } catch (Exception e) {
            coVariances.put("covR204_205m__r207_205m", BigDecimal.ZERO);
        }

        try {
            BigDecimal covR204_205m__r208_205m = //
                    fraction.getMeasuredRatioByName(MeasuredRatios.r208_205m.getName()).getValue().//
                    multiply(fraction.getMeasuredRatioByName(MeasuredRatios.r204_205m.getName()).getValue()).//
                    multiply(new BigDecimal("0.5")).movePointLeft(4).//
                    multiply(fraction.getMeasuredRatioByName(MeasuredRatios.r208_205m.getName()).getOneSigmaPct().pow(2).//
                            add(fraction.getMeasuredRatioByName(MeasuredRatios.r204_205m.getName()).getOneSigmaPct().pow(2)).//
                            subtract(fraction.getMeasuredRatioByName(MeasuredRatios.r208_204m.getName()).getOneSigmaPct().pow(2)));
            coVariances.put("covR204_205m__r208_205m", covR204_205m__r208_205m);
        } catch (Exception e) {
            coVariances.put("covR204_205m__r208_205m", BigDecimal.ZERO);
        }

        if (true) {//  (((UPbFraction) fraction).hasMeasuredUranium()) {//********************************* ?? URANIUM
            try {
                BigDecimal covR233_235m__r238_235m = fraction.getMeasuredRatioByName(MeasuredRatios.r233_235m.getName()).getValue().//
                        multiply(fraction.getMeasuredRatioByName(MeasuredRatios.r238_235m.getName()).getValue()).movePointLeft(4).//
                        divide(new BigDecimal(2.0), ReduxConstants.mathContext15).//
                        multiply(fraction.getMeasuredRatioByName(MeasuredRatios.r233_235m.getName()).getOneSigmaAbs().pow(2).//
                                add(fraction.getMeasuredRatioByName(MeasuredRatios.r238_235m.getName()).getOneSigmaAbs().pow(2).//
                                        subtract(fraction.getMeasuredRatioByName(MeasuredRatios.r238_233m.getName()).getOneSigmaAbs().pow(2))));
                coVariances.put("covR233_235m__r238_235m", covR233_235m__r238_235m);
            } catch (Exception e) {
                coVariances.put("covR233_235m__r238_235m", BigDecimal.ZERO);
            }
        }

        try {
            BigDecimal covR206_205m__r207_205m = //
                    fraction.getMeasuredRatioByName(MeasuredRatios.r206_205m.getName()).getValue().//
                    multiply(fraction.getMeasuredRatioByName(MeasuredRatios.r207_205m.getName()).getValue()).//
                    multiply(new BigDecimal("0.5")).//
                    movePointLeft(4).//

                    multiply(fraction.getMeasuredRatioByName(MeasuredRatios.r206_205m.getName()).getOneSigmaPct().pow(2).//
                            add(fraction.getMeasuredRatioByName(MeasuredRatios.r207_205m.getName()).getOneSigmaPct().pow(2).//
                                    subtract(fraction.getMeasuredRatioByName(MeasuredRatios.r206_207m.getName()).getOneSigmaPct().pow(2))));
            coVariances.put("covR206_205m__r207_205m", covR206_205m__r207_205m);
        } catch (Exception e) {
            coVariances.put("covR206_205m__r207_205m", BigDecimal.ZERO);
        }

        try {
            BigDecimal covR206_205m__r208_205m = //
                    fraction.getMeasuredRatioByName(MeasuredRatios.r206_205m.getName()).getValue().//
                    multiply(fraction.getMeasuredRatioByName(MeasuredRatios.r208_205m.getName()).getValue()).//
                    multiply(new BigDecimal("0.5")).//
                    movePointLeft(4).//

                    multiply(fraction.getMeasuredRatioByName(MeasuredRatios.r206_205m.getName()).getOneSigmaPct().pow(2).//
                            add(fraction.getMeasuredRatioByName(MeasuredRatios.r208_205m.getName()).getOneSigmaPct().pow(2).//
                                    subtract(fraction.getMeasuredRatioByName(MeasuredRatios.r206_208m.getName()).getOneSigmaPct().pow(2))));
            coVariances.put("covR206_205m__r208_205m", covR206_205m__r208_205m);
        } catch (Exception e) {
            coVariances.put("covR206_205m__r208_205m", BigDecimal.ZERO);
        }

        try {
            BigDecimal covR206_204b__r207_204b = //
                    ((UPbFractionI) fraction).getPbBlank().getRhoVarUnctByName("rhoR206_204b__r207_204b").getValue().//
                    multiply(((UPbFractionI) fraction).getPbBlank().getDatumByName("r206_204b").getOneSigmaAbs()).//
                    multiply(((UPbFractionI) fraction).getPbBlank().getDatumByName("r207_204b").getOneSigmaAbs());
            coVariances.put("covR206_204b__r207_204b", covR206_204b__r207_204b);
        } catch (Exception e) {
            coVariances.put("covR206_204b__r207_204b", BigDecimal.ZERO);
        }

        try {
            BigDecimal covR207_204b__r208_204b = //
                    ((UPbFractionI) fraction).getPbBlank().getRhoVarUnctByName("rhoR207_204b__r208_204b").getValue().//
                    multiply(((UPbFractionI) fraction).getPbBlank().getDatumByName("r207_204b").getOneSigmaAbs()).//
                    multiply(((UPbFractionI) fraction).getPbBlank().getDatumByName("r208_204b").getOneSigmaAbs());
            coVariances.put("covR207_204b__r208_204b", covR207_204b__r208_204b);
        } catch (Exception e) {
            coVariances.put("covR207_204b__r208_204b", BigDecimal.ZERO);
        }

        try {
            BigDecimal covR206_204b__r208_204b = //
                    ((UPbFractionI) fraction).getPbBlank().getRhoVarUnctByName("rhoR206_204b__r208_204b").getValue().//
                    multiply(((UPbFractionI) fraction).getPbBlank().getDatumByName("r206_204b").getOneSigmaAbs()).//
                    multiply(((UPbFractionI) fraction).getPbBlank().getDatumByName("r208_204b").getOneSigmaAbs());
            coVariances.put("covR206_204b__r208_204b", covR206_204b__r208_204b);
        } catch (Exception e) {
            coVariances.put("covR206_204b__r208_204b", BigDecimal.ZERO);
        }

        try {
            BigDecimal covR206_204c__r207_204c = //
                    ((UPbFractionI) fraction).getInitialPbModel().getRhoVarUnctByName("rhoR206_204c__r207_204c").getValue().//
                    multiply(((UPbFractionI) fraction).getInitialPbModel().getDatumByName("r206_204c").getOneSigmaAbs()).//
                    multiply(((UPbFractionI) fraction).getInitialPbModel().getDatumByName("r207_204c").getOneSigmaAbs());
            coVariances.put("covR206_204c__r207_204c", covR206_204c__r207_204c);
        } catch (Exception e) {
            coVariances.put("covR206_204c__r207_204c", BigDecimal.ZERO);
        }

        try {
            BigDecimal covR207_204c__r208_204c = //
                    ((UPbFractionI) fraction).getInitialPbModel().getRhoVarUnctByName("rhoR207_204c__r208_204c").getValue().//
                    multiply(((UPbFractionI) fraction).getInitialPbModel().getDatumByName("r207_204c").getOneSigmaAbs()).//
                    multiply(((UPbFractionI) fraction).getInitialPbModel().getDatumByName("r208_204c").getOneSigmaAbs());
            coVariances.put("covR207_204c__r208_204c", covR207_204c__r208_204c);
        } catch (Exception e) {
            coVariances.put("covR207_204c__r208_204c", BigDecimal.ZERO);
        }

        try {
            BigDecimal covR206_204c__r208_204c = //
                    ((UPbFractionI) fraction).getInitialPbModel().getRhoVarUnctByName("rhoR206_204c__r208_204c").getValue().//
                    multiply(((UPbFractionI) fraction).getInitialPbModel().getDatumByName("r206_204c").getOneSigmaAbs()).//
                    multiply(((UPbFractionI) fraction).getInitialPbModel().getDatumByName("r208_204c").getOneSigmaAbs());
            coVariances.put("covR206_204c__r208_204c", covR206_204c__r208_204c);
        } catch (Exception e) {
            coVariances.put("covR206_204c__r208_204c", BigDecimal.ZERO);
        }

        if (true) {// (((UPbFraction) fraction).hasMeasuredUranium()) {//********************************* ?? URANIUM

            try {
                BigDecimal covR265_267m__r270_267m = fraction.getAnalysisMeasure(AnalysisMeasures.r270_267m.getName()).getValue().//
                        multiply(fraction.getAnalysisMeasure(AnalysisMeasures.r265_267m.getName()).getValue()).movePointLeft(4).//
                        divide(new BigDecimal(2.0), ReduxConstants.mathContext15).//
                        multiply(fraction.getAnalysisMeasure(AnalysisMeasures.r270_267m.getName()).getOneSigmaPct().pow(2).//
                                add(fraction.getAnalysisMeasure(AnalysisMeasures.r265_267m.getName()).getOneSigmaPct().pow(2).//
                                        subtract(fraction.getAnalysisMeasure(AnalysisMeasures.r270_265m.getName()).getOneSigmaPct().pow(2))));
                coVariances.put("covR265_267m__r270_267m", covR265_267m__r270_267m);
            } catch (Exception e) {
                coVariances.put("covR265_267m__r270_267m", BigDecimal.ZERO);
            }

            // Jan 2011
            try {
                BigDecimal covR265_268m__r270_268m = fraction.getAnalysisMeasure(AnalysisMeasures.r270_268m.getName()).getValue().//
                        multiply(fraction.getAnalysisMeasure(AnalysisMeasures.r265_268m.getName()).getValue()).//
                        divide(new BigDecimal(2.0), ReduxConstants.mathContext15).//
                        movePointLeft(4).//
                        multiply(fraction.getAnalysisMeasure(AnalysisMeasures.r270_268m.getName()).getOneSigmaPct().pow(2).//
                                add(fraction.getAnalysisMeasure(AnalysisMeasures.r265_268m.getName()).getOneSigmaPct().pow(2).//
                                        subtract(fraction.getAnalysisMeasure(AnalysisMeasures.r270_265m.getName()).getOneSigmaPct().pow(2))));
                coVariances.put("covR265_268m__r270_268m", covR265_268m__r270_268m);
            } catch (Exception e) {
                coVariances.put("covR265_268m__r270_268m", BigDecimal.ZERO);
            }
        }

    }

    private static void initializeAtomicMolarMasses(
            AbstractRatiosDataModel physicalConstants) {

        gmol204 = ((PhysicalConstantsModel) physicalConstants).getAtomicMolarMassByName("gmol204");
//        gmol205 = physicalConstants.getAtomicMolarMassByName("gmol205");
        gmol206 = ((PhysicalConstantsModel) physicalConstants).getAtomicMolarMassByName("gmol206");
        gmol207 = ((PhysicalConstantsModel) physicalConstants).getAtomicMolarMassByName("gmol207");
        gmol208 = ((PhysicalConstantsModel) physicalConstants).getAtomicMolarMassByName("gmol208");

        gmol235 = ((PhysicalConstantsModel) physicalConstants).getAtomicMolarMassByName("gmol235");
        gmol238 = ((PhysicalConstantsModel) physicalConstants).getAtomicMolarMassByName("gmol238");
    }

    /**
     * @return the sessionCorrectedUnknownsSummaries
     */
    public SortedMap<RadRatios, SessionCorrectedUnknownsSummary> getSessionCorrectedUnknownsSummaries() {
        return sessionCorrectedUnknownsSummaries;
    }

    /**
     * @param aSessionCorrectedUnknownsSummaries the
     * sessionCorrectedUnknownsSummaries to set
     */
    public void setSessionCorrectedUnknownsSummaries(SortedMap<RadRatios, SessionCorrectedUnknownsSummary> aSessionCorrectedUnknownsSummaries) {
        sessionCorrectedUnknownsSummaries = aSessionCorrectedUnknownsSummaries;
    }

}
