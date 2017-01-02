/*
 * DataDictionary.java
 *
 * Created on April 23, 2007, 7:06 AM
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
package org.earthtime.dataDictionaries;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFraction;

/**
 *
 * @author James F. Bowring
 */
public final class DataDictionary {

    // TODO: refactor out magic strings to classes / enums :  started fall 2010
    /**
     *
     */
    public final static String[] AliquotInstrumentalMethod = new String[]{
        "ID-TIMS",
        "SHRIMP Ion Probe",
        "Cameca Ion Probe",
        "Quad ICPMS",
        "HR-ICPMS",
        "MC-ICPMS"
    };
    // Physical Constants
    /**
     *
     */
    public final static String[][] AtomicMolarMasses = new String[][]{
        {"gmol204", "203.973028"},
        {"gmol205", "204.9737"},
        {"gmol206", "205.974449"},
        {"gmol207", "206.975880"},
        {"gmol208", "207.976636"},
        {"gmol238", "238.050785"},
        {"gmol235", "235.043922"}};
    /**
     *
     */
    public final static String[][] MeasuredConstants = new String[][]{
        {Lambdas.lambda230.getName(), "0.00000912516", "0.15244", "Cheng et al. 2000"},
        {Lambdas.lambda231.getName(), "0.0000211887", "0.33578", "Robert et al. 1969"},
        {Lambdas.lambda232.getName(), "0.0000000000493343", "0.042769", "Holden 1990"},
        {Lambdas.lambda234.getName(), "0.0000028262", "0.00000000285", "Cheng et al. 2000"},
        {Lambdas.lambda235.getName(), "0.00000000098485", "0.068031", "Jaffey et al. 1971"},
        {Lambdas.lambda238.getName(), "0.000000000155125", "0.053505", "Jaffey et al. 1971"}
    };
    /**
     *
     */
    public final static String[] MineralStandardNames = new String[]{
        "FC-1",
        "FC-5",
        "Fish Canyon Tuff",
        "GJ-1",
        "Manicouagan",
        "North Mountain Basalt",
        "Peixe",
        "Plesovice",
        "Sri Lanka",
        "Temora",};
    /**
     *
     */
    public static final String[] SettingType = new String[]{
        "other",
        "situ",
        "grain mount",
        "loose grain"
    };

    /**
     *
     * @param settingType
     * @return
     */
    public static String validateSettingType(String settingType) {
        String retVal = SettingType[0];
        for (String s : SettingType) {
            if (s.equalsIgnoreCase(settingType)) {
                retVal = s;
            }
        }
        return retVal;
    }
    /**
     *
     */
    public static final String[] PbCollectorType = new String[]{
        "NONE",
        "Faraday",
        "Daly",
        "SEM"
    };

    /**
     *
     * @param pbCollectorType
     * @return
     */
    public static String validatePbCollectorType(String pbCollectorType) {
        String retVal = PbCollectorType[0];
        for (String s : PbCollectorType) {
            if (s.equalsIgnoreCase(pbCollectorType)) {
                retVal = s;
            }
        }
        return retVal;
    }
    /**
     *
     */
    public static final String[] UCollectorType = new String[]{
        "NONE",
        "Faraday",
        "Daly",
        "SEM"
    };

    /**
     *
     * @param uCollectorType
     * @return
     */
    public static String validateUCollectorType(String uCollectorType) {
        String retVal = UCollectorType[0];
        for (String s : UCollectorType) {
            if (s.equalsIgnoreCase(uCollectorType)) {
                retVal = s;
            }
        }
        return retVal;
    }
    /**
     *
     */
    public static final String[] RadiogenicIsotopeRatioTypes = new String[]{
        "r206_204r",
        "r208_206r",
        "r206_238r",
        "r207_235r",
        "r207_206r",
        "r208_232r",
        "r206_238r_Th",
        // add Th and Pa September 2009
        "r207_235r_Pa",
        "r207_206r_Th",
        "r207_206r_Pa",
        "r207_206r_ThPa",
        "rhoR206_238r__r207_235r",
        // april 2010 removed as deprecated a while ago "rhoR208_232r__r207_235r",
        // TODO: list all rhos here
        "rhoR207_206r__r238_206r"
    };
//    public final static String[] UPbReduxMeasuredRatioNames = new String[]{
//        MeasuredRatios.r206_204m.getName(),
//        MeasuredRatios.r207_204m.getName(), // april 2008
//        MeasuredRatios.r208_204m.getName(), // april 2008
//        MeasuredRatios.r206_207m.getName(),
//        MeasuredRatios.r206_208m.getName(),
//        MeasuredRatios.r204_205m.getName(), // april 2008
//        MeasuredRatios.r206_205m.getName(),
//        MeasuredRatios.r207_205m.getName(),
//        MeasuredRatios.r208_205m.getName(),
//        MeasuredRatios.r202_205m.getName(),
//        MeasuredRatios.r238_235m.getName(),
//        MeasuredRatios.r233_235m.getName(),
//        MeasuredRatios.r238_233m.getName(),
//        MeasuredRatios.r238_236m.getName(), // jan 2011
//        MeasuredRatios.r233_236m.getName()
//    };
    /**
     *
     */
    public final static String[] TracerType = new String[]{
        "NONE",
        "mixed 205-235",
        "mixed 205-233-235",
        "mixed 208-235",
        "mixed 205-233-236",
        "mixed 202-205-233-235",
        "mixed 202-205-233-236",
        "mixed 205-233-235-230Th"
    };
    /**
     *
     */
    public final static String[] SampleIsochronRatioNames = new String[]{
        "r206_204tfc",
        "r204_206s",
        "r238_204s",
        "rhoR238_204s__r206_204s",
        "r235_204s",
        "r204_207s",
        "rhoR235_204s__r207_204s",
        "r232_204s",
        "r208_204s",
        "rhoR202_204s__r208_204s",
        "r238_206s",
        "r207_206s",
        "rhoR238_206s__r207_206s",
        "rhoR207_206s__r204_206s",
        "r238_207s",
        "r235_207s"
    };
    /**
     *
     */
    public final static String[] earthTimePbBlankICRatioNames = new String[]{
        "r206_204b",
        "r207_204b",
        "r208_204b"//,
    //AnalysisMeasures.r238_235b.getName()
    };
    /**
     *
     */
    public final static String[] earthTimePbBlankRhoCorrelationNames = new String[]{
        "rhoR206_204b__r207_204b",
        "rhoR206_204b__r208_204b",
        "rhoR207_204b__r208_204b"
    };
    /**
     *
     */
    public final static String[] earthTimeInitialPbModelRatioNames = new String[]{
        "r206_204c",
        "r207_204c",
        "r208_204c"//,
    //"r207_206c"
    };
    /**
     *
     */
    public final static String[] earthTimeInitialPbModelCorrelationCoeffNames = new String[]{
        "rhoR206_204c__r207_204c",
        "rhoR207_204c__r208_204c",
        "rhoR206_204c__r208_204c"
    };
//    public static final String[] earthTimeAnalysisMeasuresNames = new String[]{
//        AnalysisMeasures.fractionMass.getName(),
//        AnalysisMeasures.tracerMassInGrams.getName(),
//        AnalysisMeasures.uBlankMassInGrams.getName(),
//        AnalysisMeasures.pbBlankMassInGrams.getName(),
//        AnalysisMeasures.alphaPb.getName(),
//        AnalysisMeasures.alphaU.getName(),
//        AnalysisMeasures.r238_235b.getName(),
//        AnalysisMeasures.r238_235s.getName(),
//        AnalysisMeasures.r18O_16O.getName(),
//        AnalysisMeasures.r18O_16O_revised.getName(), // added nov 2009 to permit re-oxide corrections
//        AnalysisMeasures.rTh_Umagma.getName(),
//        AnalysisMeasures.ar231_235sample.getName(),
//        // added for matrix reductions june 2008
//        AnalysisMeasures.r270_267m.getName(),
//        AnalysisMeasures.r265_267m.getName(),
//        AnalysisMeasures.r270_265m.getName(),
//        // added Jan 2011 for handling Tracer 233_236
//        AnalysisMeasures.r270_268m.getName(),
//        AnalysisMeasures.r265_268m.getName()
//    };
    /**
     *
     */
    public static final String[] earthTimeUPbCompositionalMeasuresNames = new String[]{
        "concU",
        "concTh",
        "rTh_Usample",
        "concPb_rib",
        "concPb_r",
        "concPb_i",
        "concPb_ib",
        "concPb206_rib",
        "concPb206_r",
        "concPb206_ib",
        "totRadiogenicPbMass",
        "radToCommonTotal",
        "totCommonPbMass",};
    // added march 2009
    /**
     *
     */
    public static final String[] uPbReduxOutputNames = new String[]{
        "initCommonPbMass",
        "radToCommonPb206",
        "radToCommonPb207",
        "radToCommonPb208",
        "uTracerMassInGrams",
        // added partial outputs to aid with auto-generation of U data
        "molPb206r",
        "molU238b",
        "molU238t",
        "molU235b",
        "molU235t",
        "molU233t",
        "molU235s",
        "molU238s",
        "blankPbMass"
    };

    /**
     *
     * @param index
     * @return
     */
    public static String getPbBlankRatioName(
            int index) {
        return earthTimePbBlankICRatioNames[index];
    }

    /**
     *
     * @param index
     * @return
     */
    public static String getPbBlankRhoCorrelationName(
            int index) {
        return earthTimePbBlankRhoCorrelationNames[index];
    }

    /**
     *
     * @param index
     * @return
     */
    public static String getEarthTimeInitialPbModelRatioNames(
            int index) {
        return earthTimeInitialPbModelRatioNames[index];
    }

    /**
     *
     * @param index
     * @return
     */
    public static String getEarthTimePbBlankICModelRatioNames(
            int index) {
        return earthTimePbBlankICRatioNames[index];
    }

    /**
     *
     * @param index
     * @return
     */
    public static String getEarthTimeInitialPbModelCorrelationCoeffNamesint(
            int index) {
        return earthTimeInitialPbModelCorrelationCoeffNames[index];
    }

    /**
     *
     * @param name
     * @return
     */
    public static String getElementNameOfRatio(
            String name) {
        if (name.indexOf("20") > -1) {
            return "Pb";
        } else {
            return "U";
        }

    }

    /**
     *
     * @return
     */
    public static String[] getTracerType() {
        return TracerType;
    }

    /**
     *
     * @param tracerType
     * @return
     */
    public static boolean legalTracerType(String tracerType) {
        boolean retVal = false;
        for (int i = 0; i < TracerType.length; i++) {
            retVal = retVal || (TracerType[i].equalsIgnoreCase(tracerType.trim()));
        }

        return retVal;
    }

    /**
     *
     * @param index
     * @return
     */
    public static String getRadiogenicIsotopeRatioTypes(
            int index) {
        return RadiogenicIsotopeRatioTypes[index];
    }

    /**
     *
     * @param index
     * @return
     */
    public static String getSampleIsochronRatioNames(
            int index) {
        return SampleIsochronRatioNames[index];
    }

    /**
     *
     * @param index
     * @return
     */
    public static String getEarthTimeUPbCompositionalMeasuresNames(
            int index) {
        return earthTimeUPbCompositionalMeasuresNames[index];
    }

    /**
     *
     * @return
     */
    public static String[] getMineralStandardNames() {
        return MineralStandardNames;
    }
    /**
     *
     */
    public final static String[][] ArrayMapOfInputsToFractionClassMethodNames = new String[][]{
        {"getMeasuredRatioByName"}, MeasuredRatios.getNames(),
        {"getAnalysisMeasure"}, AnalysisMeasures.getNames(),
        {"getTracerRatioByName"}, TracerUPbRatiosAndConcentrations.getNames(),
        {"getCompositionalMeasureByName"}, earthTimeUPbCompositionalMeasuresNames,
        //        {"getTracerIsotopeConcByName"}, TracerIsotopes.getNames(),
        {"getPbBlankRatioByName"}, earthTimePbBlankICRatioNames,
        {"getInitialPbModelRatioByName"}, earthTimeInitialPbModelRatioNames,
        {"getRadiogenicIsotopeDateByName"}, RadDates.getNamesSorted(),
        {"getRadiogenicIsotopeRatioByName"}, RadRatios.getNamesSorted()
    };
    /**
     *
     */
    public final static String[][][] universalClumpedInputVariableSpecs = new String[][][]{
        // format: data item identifiers and display names and units for clumped displays on kwiki tab
        {{MeasuredRatios.r202_205m.getName(), "202Pb/205Pb meas", ""}, //
        {MeasuredRatios.r204_205m.getName(), "204Pb/205Pb meas", ""}, //
        {MeasuredRatios.r206_205m.getName(), "206Pb/205Pb meas", ""}, //
        {MeasuredRatios.r207_205m.getName(), "207Pb/205Pb meas", ""}, //
        {MeasuredRatios.r208_205m.getName(), "208Pb/205Pb meas", ""}, //
        {MeasuredRatios.r238_235m.getName(), "238U/235U meas", ""}, //
        {AnalysisMeasures.r270_267m.getName(), "270U/267U meas", ""}, //
        {MeasuredRatios.r233_235m.getName(), "233U/235U meas", ""}, //
        {AnalysisMeasures.r265_267m.getName(), "265U/267U meas", ""},
        {AnalysisMeasures.r265_268m.getName(), "265U/268U meas", ""},
        {AnalysisMeasures.r270_268m.getName(), "270U/268U meas", ""}},//
        {{AnalysisMeasures.alphaPb.getName(), "alphaPb (%/amu)", "%/amu"}, //
        {AnalysisMeasures.alphaU.getName(), "alphaU (%/amu)", "%/amu"}},//
        {{AnalysisMeasures.tracerMassInGrams.getName(), "tracer mass grams", ""}, //
        {TracerUPbRatiosAndConcentrations.r202_205t.getName(), "202Pb/205Pb tr", ""},//
        {TracerUPbRatiosAndConcentrations.r204_205t.getName(), "204Pb/205Pb tr", ""}, //
        {TracerUPbRatiosAndConcentrations.r206_205t.getName(), "206Pb/205Pb tr", ""}, //
        {TracerUPbRatiosAndConcentrations.r207_205t.getName(), "207Pb/205Pb tr", ""}, //
        {TracerUPbRatiosAndConcentrations.r208_205t.getName(), "208Pb/205Pb tr", ""}, //
        {TracerUPbRatiosAndConcentrations.r238_235t.getName(), "238U/235U tr", ""}, //
        {TracerUPbRatiosAndConcentrations.r238_233t.getName(), "238U/233U tr", ""}, //
        {TracerUPbRatiosAndConcentrations.r233_235t.getName(), "233U/235U tr", ""}, //
        {TracerUPbRatiosAndConcentrations.concPb205t.getName(), "conc 205Pb tr (pmol/g)", "pg"}, //
        {TracerUPbRatiosAndConcentrations.concU235t.getName(), "conc 235U tr (pmol/g)", "pg"}, //
        {TracerUPbRatiosAndConcentrations.concU236t.getName(), "conc 236U tr (pmol/g)", "pg"}},//
        {{AnalysisMeasures.pbBlankMassInGrams.getName(), "Pb blank mass (pg)", "pg"}, //
        {AnalysisMeasures.uBlankMassInGrams.getName(), "U blank mass (pg)", "pg"}, //
        {"r206_204b", "206Pb/204Pb blank", ""}, //
        {"r207_204b", "207Pb/204Pb blank", ""},//
        {"r208_204b", "208Pb/204Pb blank", ""}, //
        {AnalysisMeasures.r238_235b.getName(), "238U/235U blank", ""}},//
        {{"r206_204c", "206Pb/204Pb com", ""}, //
        {"r207_204c", "207Pb/204Pb com", ""}, //
        {"r208_204c", "208Pb/204Pb com", ""}},//
        {{AnalysisMeasures.r238_235s.getName(), "238U/235U sample", ""}, //
        {AnalysisMeasures.ar231_235sample.getName(), "[231Pa]/[235U] sample", ""}, //
        {AnalysisMeasures.rTh_Umagma.getName(), "Th/U magma", ""},//
        {AnalysisMeasures.r18O_16O.getName(), "18O/16O Uoxide", ""}}
    };
    // builds a dictionary
    /**
     *
     */
    public final static Map<String, String> inputsNameTranslator = new HashMap<>();

    static {

        for (int i = 0; i
                < universalClumpedInputVariableSpecs.length; i++) {
            for (int j = 0; j
                    < universalClumpedInputVariableSpecs[i].length; j++) {
                inputsNameTranslator.put( //
                        universalClumpedInputVariableSpecs[i][j][0],
                        universalClumpedInputVariableSpecs[i][j][1]);
            }

            inputsNameTranslator.put(Lambdas.lambda230.getName(), Lambdas.lambda230.getDisplayName());
            inputsNameTranslator.put(Lambdas.lambda231.getName(), Lambdas.lambda231.getDisplayName());
            inputsNameTranslator.put(Lambdas.lambda232.getName(), Lambdas.lambda232.getDisplayName());
            inputsNameTranslator.put(Lambdas.lambda234.getName(), Lambdas.lambda234.getDisplayName());
            inputsNameTranslator.put(Lambdas.lambda235.getName(), Lambdas.lambda235.getDisplayName());
            inputsNameTranslator.put(Lambdas.lambda238.getName(), Lambdas.lambda238.getDisplayName());
        }

    }
    // create mapping of input names to the UPbFraction method that gets them
    /**
     *
     */
    public final static Map<String, String> MapOfInputsToMethodNames = new HashMap<String, String>();

    static {
// data dictionary stores these in row pairs: even index holds methodname, odd holds list

        for (int i = 0; i < ArrayMapOfInputsToFractionClassMethodNames.length; i += 2) {
            for (int j = 0; j < ArrayMapOfInputsToFractionClassMethodNames[i + 1].length; j++) {
                MapOfInputsToMethodNames.put(//
                        ArrayMapOfInputsToFractionClassMethodNames[i + 1][j], //
                        ArrayMapOfInputsToFractionClassMethodNames[i][0]);
            }
        }
    }

    /**
     * Uses reflection to get method associated with a specific input name,
     * which is a rowForSpecificDate or column name in a matrix.
     *
     * @param inputName
     * @return
     */
    public static Method retrieveMethodNameForInput(String inputName) {
        Method meth = null;
        String methodName = MapOfInputsToMethodNames.get(inputName);
        if (methodName != null) {
            try {
                Class<?> fractionClass
                        = Class.forName(UPbFraction.class.getCanonicalName());

                meth = fractionClass.getMethod(//
                        methodName,
                        new Class[]{String.class});
            } catch (ClassNotFoundException | NoSuchMethodException | SecurityException classNotFoundException) {
            }
        }
        return meth;
    }
}
