/*
 * ReportSpecifications.java
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

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author James F. Bowring
 */
public class ReportSpecifications {
    // Static
    // Report specifications
    // Report column order =
    //  displayName1, displayName2, displayName3, units, retrieveMethodName, retrieveParameterName, uncertaintyType,
    //     footnoteSpec, visible, useArbitrary? for value, digitcount value, unct visible (if required), description where needed,
    //     needsLead, needsUranium

    /**
     *
     */
    public static final String[][] ReportCategory_Fraction = new String[][]{
        {"", "", "Fraction  ", "", "getFractionID", "", "",
            "", "true", "false", "0", "", "", "false", "false"
        },
        {"", "No. of", "Grains", "", "getNumberOfGrains", "", "",
            "", "false", "false", "0", "", "", "false", "false"
        }
    };
    /**
     *
     */
    public static final String[][] ReportCategory_Fraction2 = new String[][]{
        {"", "", "Fraction  ", "", "getFractionID", "", "",
            "", "true", "false", "0", "", "Fraction (customizable location)", "false", "false"
        }
    };
    // Report column order =
    //  displayName1, displayName2, displayName3, units, retrieveMethodName, retrieveParameterName, uncertaintyType,
    //     footnoteSpec, visible, useArbitrary? for value, digitcount value, unct visible (if required), description where needed,
    //     needsLead, needsUranium
    /**
     *
     */
    public static final String[][] ReportCategory_Composition = new String[][]{
        {"", "Mass", "", "g", "getAnalysisMeasure", AnalysisMeasures.fractionMass.getName(), "",
            "", "false", "false", "6", "", "fraction mass", "false", "false"
        },
        {"", "mass", "U", "\u03bcg", "getCompositionalMeasureByName", "massU", "",
            "", "false", "false", "6", "", "mass of U", "false", "true"
        },
        {"", "conc", "U", "ppm", "getCompositionalMeasureByName", "concU", "",
            "", "false", "false", "6", "", "concentration of U", "false", "true"
        },
        {"", "Th", "", "ppm", "getCompositionalMeasureByName", "concTh", "",
            "FN-1", "false", "false", "6", "", "concentration of Th", "true", "false"
        },
        {"", "Th/", "U", "", "getCompositionalMeasureByName", "rTh_Usample", "",
            "FN-1", "true", "true", "2", "", "", "true", "true"
        },
        {"", "Pb*", "", "pg", "getCompositionalMeasureByName", "totRadiogenicPbMass", "",
            "FN-3", "true", "false", "3", "", "Pb* mass", "true", "false"
        },
        {"", "Pbc", "", "pg", "getCompositionalMeasureByName", "totCommonPbMass", "",
            "FN-4", "true", "true", "2", "", "Pbc mass (initial plus blank)", "true", "false"
        },
        {"total", "mass", "Pb", "pg", "getCompositionalMeasureByName", "massPb_rib", "",
            "", "false", "false", "6", "", "Pb mass (total)", "true", "false"
        },
        {"conc", "Pb*", "", "ppm", "getCompositionalMeasureByName", "concPb_r", "",
            "", "false", "false", "6", "", "Pb* concentration", "true", "false"
        },
        {"conc", "Pbc", "", "ppm", "getCompositionalMeasureByName", "concPb_i", "",
            "", "false", "false", "6", "", "Pbc concentration (initial)", "true", "false"
        },
        {"conc", "Pb", "", "ppm", "getCompositionalMeasureByName", "concPb_rib", "",
            "", "false", "false", "6", "", "Pb concentration (total)", "true", "false"
        },
        {"", "Pb*/", "Pbc", "", "getCompositionalMeasureByName", "radToCommonTotal", "",
            "FN-2", "true", "true", "0", "", "", "true", "false"
        },
        {"", "[231Pa/", "235U]", "", "getAnalysisMeasure", AnalysisMeasures.ar231_235sample.getName(), "",
            "FN-10", "false", "true", "3", "", "[231Pa/235U] activity ratio", "false", "false"
        },
        {"", "Th/U", "(magma)", "", "getAnalysisMeasure", AnalysisMeasures.rTh_Umagma.getName(), "",
            "FN-11", "false", "true", "3", "", "Th/U magma", "true", "false"
        },
    };

    // Report column order =
    //  displayName1, displayName2, displayName3, units, retrieveMethodName, retrieveParameterName, uncertaintyType,
    //     footnoteSpec, visible, useArbitrary? for value, digitcount value, unct visible (if required), description where needed,
    //     needsLead, needsUranium
    /**
     *
     */
    public static final String[][] ReportCategory_CompositionUTh = new String[][]{
        {"conc", "232Th", "", "ppb", "getCompositionalMeasureByName", "conc232Th", "ABS",
            "FN-17", "true", "true", "3", "", "concentration of 232Th", "true", "false"
        },
        {"conc", "238U", "", "ppm", "getCompositionalMeasureByName", "conc238U", "ABS",
            "", "true", "true", "3", "", "concentration of 238U", "false", "true"
        },
        {"", "[230Th/", "232Th]", "", "getAnalysisMeasure", UThActivityRatios.ar230Th_232Thfc.getName(), "",
            "FN-17", "true", "true", "3", "", "[230Th/232Th] activity ratio", "false", "false"
        },
        {"[232Th/", "238U]", "", "*1e5", "getAnalysisMeasure", UThActivityRatios.ar232Th_238Ufc.getName(), "",
            "FN-17", "true", "true", "3", "", "[232Th/238U] activity ratio", "false", "false"
        },
        {"", "[230Th/", "238U]", "", "getAnalysisMeasure", UThActivityRatios.ar230Th_238Ufc.getName(), "ABS",
            "FN-17", "true", "false", "3", "true", "[230Th/238U] activity ratio", "false", "false"
        },
        {"", "[234U/", "238U]", "", "getAnalysisMeasure", UThActivityRatios.ar234U_238Ufc.getName(), "ABS",
            "FN-17", "true", "false", "3", "true", "[234U/238U] activity ratio", "false", "false"
        },};
    // Report column order =
    //  displayName1, displayName2, displayName3, units, retrieveMethodName, retrieveParameterName, uncertaintyType,
    //     footnoteSpec, visible, useArbitrary? for value, digitcount value, unct visible (if required), description where needed,
    //     needsLead, needsUranium
    /**
     *
     */
    public static final String[][] ReportCategory_IsotopicRatios = new String[][]{
        {"", "206Pb/", "204Pb", "", "getSampleIsochronRatiosByName", "r206_204tfc", "",
            "FN-6", "true", "true", "0", "false", "", "true", "false"
        },
        {"", "208Pb/", "206Pb", "", "getRadiogenicIsotopeRatioByName", "r208_206r", "",
            "FN-5", "false", "true", "3", "false", "", "true", "false"
        },
        {"", "206Pb/", "238U", "", "getRadiogenicIsotopeRatioByName", "r206_238r", "PCT",
            "FN-5", "true", "false", "2", "true", "", "true", "true"
        },
        {"", "207Pb/", "235U", "", "getRadiogenicIsotopeRatioByName", "r207_235r", "PCT",
            "FN-5", "true", "false", "2", "true", "", "true", "true"
        },
        {"", "207Pb/", "206Pb", "", "getRadiogenicIsotopeRatioByName", "r207_206r", "PCT",
            "FN-5", "true", "false", "2", "true", "", "true", "false"
        },
        // added march 2013
        {"", "208Pb/", "232Th", "", "getRadiogenicIsotopeRatioByName", "r208_232r", "PCT",
            "FN-5", "true", "false", "2", "true", "", "true", "false"
        },
        {"206Pb/", "238U", "<Th>", "", "getRadiogenicIsotopeRatioByName", "r206_238r_Th", "PCT",
            "FN-5&FN-12", "false", "true", "6", "true", "206Pb/238U (Th\u2212corrected)", "true", "true"
        },
        {"207Pb/", "235U", "<Pa>", "", "getRadiogenicIsotopeRatioByName", "r207_235r_Pa", "PCT",
            "FN-5&FN-13", "false", "true", "6", "true", "207Pb/235U (Pa\u2212corrected)", "true", "true"
        },
        {"207Pb/", "206Pb", "<Th>", "", "getRadiogenicIsotopeRatioByName", "r207_206r_Th", "PCT",
            "FN-5&FN-12", "false", "true", "6", "true", "207Pb/206Pb (Th\u2212corrected)", "true", "true"
        },
        {"207Pb/", "206Pb", "<Pa>", "", "getRadiogenicIsotopeRatioByName", "r207_206r_Pa", "PCT",
            "FN-5&FN-13", "false", "true", "6", "true", "207Pb/206Pb (Pa\u2212corrected)", "true", "false"
        },
        {"207Pb/", "206Pb", "<ThPa>", "", "getRadiogenicIsotopeRatioByName", "r207_206r_ThPa", "PCT",
            "FN-5&FN-12&FN-13", "false", "true", "6", "true", "207Pb/206Pb (Th\u2212 and Pa\u2212corrected)", "true", "true"
        },
        {"", "Corr.", "coef.", "", "getRadiogenicIsotopeRatioByName", "rhoR206_238r__r207_235r", "",
            "", "false", "true", "3", "", "Correlation coefficient", "true", "true"
        }
    };
    
    
    
        // Report column order =
    //  displayName1, displayName2, displayName3, units, retrieveMethodName, retrieveParameterName, uncertaintyType,
    //     footnoteSpec, visible, useArbitrary? for value, digitcount value, unct visible (if required), description where needed,
    //     needsLead, needsUranium
    /**
     *
     */
    public static final String[][] ReportCategory_fractionationCorrectedIsotopicRatios = new String[][]{
        {"", "r230Th/", "232Thfc", "", "getFractionationCorrectedIsotopeRatios", "r230Th_232Thfc", "",
            "", "true", "true", "0", "false", "", "true", "false"
        },
        
    };

    
    
    

    // Report column order =
    //  displayName1, displayName2, displayName3, units, retrieveMethodName, retrieveParameterName, uncertaintyType,
    //     footnoteSpec, visible, useArbitrary? for value, digitcount value, unct visible (if required), description where needed,
    //     needsLead, needsUranium
    /**
     *
     */
    public static final String[][] ReportCategory_PbcCorrIsotopicRatios = new String[][]{
        {"207Pb/", "206Pb", "PbcCorr", "", "getRadiogenicIsotopeRatioByName", RadRatiosPbcCorrected.r207_206_PbcCorr.getName(), "PCT",
            "", "true", "false", "2", "true", "", "true", "false"
        },
        {"206Pb/", "238U", "PbcCorr", "", "getRadiogenicIsotopeRatioByName", RadRatiosPbcCorrected.r206_238_PbcCorr.getName(), "PCT",
            "", "true", "false", "2", "true", "", "true", "false"
        },
        {"208Pb/", "232U", "PbcCorr", "", "getRadiogenicIsotopeRatioByName", RadRatiosPbcCorrected.r208_232_PbcCorr.getName(), "PCT",
            "", "true", "false", "2", "true", "", "true", "false"
        },
        {"207Pb/", "235U", "PbcCorr", "", "getRadiogenicIsotopeRatioByName", RadRatiosPbcCorrected.r207_235_PbcCorr.getName(), "PCT",
            "", "true", "false", "2", "true", "", "true", "false"
        },
        {"", "Corr.", "coef.", "", "getRadiogenicIsotopeRatioByName", "rhoR206_238PbcCorr__r207_235PbcCorr", "",
            "", "false", "true", "3", "", "Correlation coefficient", "true", "true"
        }
    };

    // Report column order =
    //  displayName1, displayName2, displayName3, units, retrieveMethodName, retrieveParameterName, uncertaintyType,
    //     footnoteSpec, visible, useArbitrary? for value, digitcount value, unct visible (if required), description where needed,
    //     needsLead, needsUranium
    /**
     *
     */
    public static final String[][] ReportCategory_Dates = new String[][]{
        {"", "206Pb/", "238U", "Ma", "getRadiogenicIsotopeDateByName", RadDates.age206_238r.getName(), "ABS",
            "FN-7", "true", "false", "2", "true", "", "true", "true"
        },
        {"206Pb/", "238U", "<Th>", "Ma", "getRadiogenicIsotopeDateByName", RadDates.age206_238r_Th.getName(), "ABS",
            "FN-12", "false", "false", "2", "true", "206Pb/238U (Th\u2212corrected)", "true", "true"
        },
        {"", "207Pb/", "235U", "Ma", "getRadiogenicIsotopeDateByName", RadDates.age207_235r.getName(), "ABS",
            "FN-7", "true", "false", "2", "true", "", "true", "true"
        },
        {"", "207Pb/", "206Pb", "Ma", "getRadiogenicIsotopeDateByName", RadDates.age207_206r.getName(), "ABS",
            "FN-7", "true", "false", "2", "true", "", "true", "false"
        },
        {"", "Corr.", "coef.", "", "getRadiogenicIsotopeRatioByName", "rhoR206_238r__r207_235r", "",
            "", "true", "true", "3", "", "Correlation coefficient", "true", "true"
        },
        {"", "", "% disc", "", "getRadiogenicIsotopeDateByName", RadDates.percentDiscordance.getName(), "",
            "FN-8", "true", "true", "2", "", "percent discordance", "true", "true"
        },
        {"", "208Pb/", "232Th", "Ma", "getRadiogenicIsotopeDateByName", RadDates.age208_232r.getName(), "ABS",
            "FN-9", "false", "false", "2", "true", "", "true", "true"
        },
        {"207Pb/", "206Pb", "<Th>", "Ma", "getRadiogenicIsotopeDateByName", RadDates.age207_206r_Th.getName(), "ABS",
            "FN-12", "false", "false", "2", "true", "207Pb/206Pb (Th\u2212corrected)", "true", "true"
        },
        {"207Pb/", "235U", "<Pa>", "Ma", "getRadiogenicIsotopeDateByName", RadDates.age207_235r_Pa.getName(), "ABS",
            "FN-13", "false", "false", "2", "true", "207Pb/235U (Pa\u2212corrected)", "true", "true"
        },
        {"207Pb/", "206Pb", "<Pa>", "Ma", "getRadiogenicIsotopeDateByName", RadDates.age207_206r_Pa.getName(), "ABS",
            "FN-13", "false", "false", "2", "true", "207Pb/206Pb (Pa\u2212corrected)", "true", "false"
        },
        {"207Pb/", "206Pb", "<ThPa>", "Ma", "getRadiogenicIsotopeDateByName", RadDates.age207_206r_ThPa.getName(), "ABS",
            "FN-12&FN-13", "false", "false", "2", "true", "207Pb/206Pb <Th\u2212 and Pa\u2212corrected>", "true", "true"
        },
        {"", "best", "date", "Ma", "getRadiogenicIsotopeDateByName", RadDates.bestAge.getName(), "ABS",
            "", "false", "false", "2", "true", "best date", "true", "true"
        }
    };

    // Report column order =
    //  displayName1, displayName2, displayName3, units, retrieveMethodName, retrieveParameterName, uncertaintyType,
    //     footnoteSpec, visible, useArbitrary? for value, digitcount value, unct visible (if required), description where needed,
    //     needsLead, needsUranium
    /**
     *
     */
    public static final String[][] ReportCategory_PbcCorrDates = new String[][]{
        {"", "U-Pb Date", "Pbc-corr", "Ma", "getRadiogenicIsotopeDateByName", RadDates.PbcCorr_UPb_Date.getName(), "ABS",
            "", "true", "false", "2", "true", "U-Pb PbcCorr Date", "true", "true"
        },
        {"206Pb/", "238U", "PbcCorr", "Ma", "getRadiogenicIsotopeDateByName", RadDates.age206_238_PbcCorr.getName(), "ABS",
            "", "true", "false", "2", "true", "", "true", "true"
        },
        {"208Pb/", "232Th", "PbcCorr", "Ma", "getRadiogenicIsotopeDateByName", RadDates.age208_232_PbcCorr.getName(), "ABS",
            "", "true", "false", "2", "true", "", "true", "true"
        },
        {"207Pb/", "235U", "PbcCorr", "Ma", "getRadiogenicIsotopeDateByName", RadDates.age207_235_PbcCorr.getName(), "ABS",
            "", "true", "false", "2", "true", "", "true", "true"
        },
        {"", "Corr.", "coef.", "", "getRadiogenicIsotopeRatioByName", "rhoR206_238PbcCorr__r207_235PbcCorr", "",
            "", "true", "true", "3", "", "Correlation coefficient", "true", "true"
        },
        {"207Pb/", "206Pb", "PbcCorr", "Ma", "getRadiogenicIsotopeDateByName", RadDates.age207_206_PbcCorr.getName(), "ABS",
            "", "true", "false", "2", "true", "", "true", "false"
        },
        {"", "% disc", "PbcCorr", "", "getRadiogenicIsotopeDateByName", RadDates.percentDiscordance_PbcCorr.getName(), "",
            "FN-8", "true", "true", "2", "", "% discordance PbcCorr", "true", "true"
        },
        {"best", "date", "PbcCorr", "Ma", "getRadiogenicIsotopeDateByName", RadDates.bestAge_PbcCorr.getName(), "ABS",
            "", "false", "false", "2", "true", "best date Pbc Corr", "true", "true"
        }
    };

    // Report column order =
    //  displayName1, displayName2, displayName3, units, retrieveMethodName, retrieveParameterName, uncertaintyType,
    //     footnoteSpec, visible, useArbitrary? for value, digitcount value, unct visible (if required), description where needed,
    //     needsLead, needsUranium
    /**
     *
     */
    public static final String[][] ReportCategory_CorrelationCoefficients = new String[][]{
        {"206Pb/238U", "-", "207Pb/235U", "", "getRadiogenicIsotopeRatioByName", "rhoR206_238r__r207_235r", "",
            "", "false", "false", "2", "", "", "true", "true"
        },
        {"206Pb/238U", "207Pb/235U", "PbcCorr", "", "getRadiogenicIsotopeRatioByName", "rhoR206_238PbcCorr__r207_235PbcCorr", "",
            "", "false", "false", "2", "", "", "true", "true"
        },
        {"206Pb/238U", "<Th>-", "207Pb/235U", "", "getRadiogenicIsotopeRatioByName", "rhoR206_238r_Th__r207_235r", "",
            "", "false", "false", "2", "", "", "true", "true"
        },
        {"206Pb/238U", "-207Pb/", "235U <Pa>", "", "getRadiogenicIsotopeRatioByName", "rhoR206_238r__r207_235r_Pa", "",
            "", "false", "false", "2", "", "", "true", "true"
        },
        {"206Pb/238U", "<Th>-207Pb/", "235U <Pa>", "", "getRadiogenicIsotopeRatioByName", "rhoR206_238r_Th__r207_235r_Pa", "",
            "", "false", "false", "2", "", "", "true", "true"
        },
        {"207Pb/206Pb", "-", "238U/206Pb", "", "getRadiogenicIsotopeRatioByName", "rhoR207_206r__r238_206r", "",
            "", "false", "false", "2", "", "", "true", "true"
        },
        {"207Pb/206Pb", "238U/206Pb", "PbcCorr", "", "getRadiogenicIsotopeRatioByName", "rhoR207_206PbcCorr__r238_206PbcCorr", "",
            "", "false", "false", "2", "", "", "true", "true"
        },
        {"207Pb/206Pb", "<Pa>-", "238U/206Pb", "", "getRadiogenicIsotopeRatioByName", "rhoR207_206r_Pa__r238_206r", "",
            "", "false", "false", "2", "", "", "true", "true"
        },
        {"207Pb/206Pb", "<Th>-238U/", "207Pb <Th>", "", "getRadiogenicIsotopeRatioByName", "rhoR207_206r_Th__r238_206r_Th", "",
            "", "false", "false", "2", "", "", "true", "true"
        },
        {"207Pb/206Pb", "<ThPa>-238U/", "206Pb <Th>", "", "getRadiogenicIsotopeRatioByName", "rhoR207_206r_ThPa__r238_206r_Th", "",
            "", "false", "false", "2", "", "", "true", "true"
        }
    };
    // Report column order =
    //  displayName1, displayName2, displayName3, units, retrieveMethodName, retrieveParameterName, uncertaintyType,
    //     footnoteSpec, visible, useArbitrary? for value, digitcount value, unct visible (if required), description where needed,
    //     needsLead, needsUranium
    /**
     *
     */
    public static final String[][] ReportCategory_TraceElements = new String[][]{
        {"", "", "Si", "ppm", "getTraceElementByName", "Si", "",
            "", "false", "false", "3", "", "", "false", "false"
        },
        {"", "", "P", "ppm", "getTraceElementByName", "P", "",
            "", "false", "false", "3", "", "", "false", "false"
        },
        {"", "", "Ca", "ppm", "getTraceElementByName", "Ca", "",
            "", "false", "false", "3", "", "", "false", "false"
        },
        {"", "", "Ti", "ppm", "getTraceElementByName", "Ti", "",
            "", "false", "false", "3", "", "", "false", "false"
        },
        {"", "", "Rb", "ppm", "getTraceElementByName", "Rb", "",
            "", "false", "false", "3", "", "", "false", "false"
        },
        {"", "", "Sr", "ppm", "getTraceElementByName", "Sr", "",
            "", "false", "false", "3", "", "", "false", "false"
        },
        {"", "", "Y", "ppm", "getTraceElementByName", "Y", "",
            "", "false", "false", "3", "", "", "false", "false"
        },
        {"", "", "Zr", "ppm", "getTraceElementByName", "Zr", "",
            "", "false", "false", "3", "", "", "false", "false"
        },
        {"", "", "La", "ppm", "getTraceElementByName", "La", "",
            "", "false", "false", "3", "", "", "false", "false"
        },
        {"", "", "Ce", "ppm", "getTraceElementByName", "Ce", "",
            "", "false", "false", "3", "", "", "false", "false"
        },
        {"", "", "Pr", "ppm", "getTraceElementByName", "Pr", "",
            "", "false", "false", "3", "", "", "false", "false"
        },
        {"", "", "Nd", "ppm", "getTraceElementByName", "Nd", "",
            "", "false", "false", "3", "", "", "false", "false"
        },
        {"", "", "Sm", "ppm", "getTraceElementByName", "Sm", "",
            "", "false", "false", "3", "", "", "false", "false"
        },
        {"", "", "Eu", "ppm", "getTraceElementByName", "Eu", "",
            "", "false", "false", "3", "", "", "false", "false"
        },
        {"", "", "Gd", "ppm", "getTraceElementByName", "Gd", "",
            "", "false", "false", "3", "", "", "false", "false"
        },
        {"", "", "Tb", "ppm", "getTraceElementByName", "Tb", "",
            "", "false", "false", "3", "", "", "false", "false"
        },
        {"", "", "Dy", "ppm", "getTraceElementByName", "Dy", "",
            "", "false", "false", "3", "", "", "false", "false"
        },
        {"", "", "Ho", "ppm", "getTraceElementByName", "Ho", "",
            "", "false", "false", "3", "", "", "false", "false"
        },
        {"", "", "Er", "ppm", "getTraceElementByName", "Er", "",
            "", "false", "false", "3", "", "", "false", "false"
        },
        {"", "", "Tm", "ppm", "getTraceElementByName", "Tm", "",
            "", "false", "false", "3", "", "", "false", "false"
        },
        {"", "", "Yb", "ppm", "getTraceElementByName", "Yb", "",
            "", "false", "false", "3", "", "", "false", "false"
        },
        {"", "", "Lu", "ppm", "getTraceElementByName", "Lu", "",
            "", "false", "false", "3", "", "", "false", "false"
        },
        {"", "", "Hf", "ppm", "getTraceElementByName", "Hf", "",
            "", "false", "false", "3", "", "", "false", "false"
        }
    };
    // mass units are referred to in units postion 4 of report column and converted by lookup in ReduxConstants
    /**
     *
     */
    public final static String[] massUnits = new String[]{
        "g", "mg", "\u03bcg", "ng", "pg", "fg"
    };
    // concentration units are referred to in units postion 4 of report column and converted by lookup in ReduxConstants
    /**
     *
     */
    public final static String[] concUnits = new String[]{
        "\u0025", "\u2030", "ppm", "ppb"
    };
    // date units are referred to in units postion 4 of report column and converted by lookup in ReduxConstants
    /**
     *
     */
    public final static String[] dateUnits = new String[]{
        "auto", "Ma", "ka"
    };
    /**
     *
     */
    public final static Map<String, String> unitsType = new HashMap<>();

    static {
        unitsType.put("g", "mass");
        unitsType.put("mg", "mass");
        unitsType.put("\u03bcg", "mass");
        unitsType.put("ng", "mass");
        unitsType.put("pg", "mass");
        unitsType.put("fg", "mass");
        unitsType.put("\u0025", "conc");
        unitsType.put("\u2030", "conc");
        unitsType.put("ppm", "conc");
        unitsType.put("ppb", "conc");
        unitsType.put("Ma", "date");
        unitsType.put("ka", "date");

    }

    /**
     *
     */
    public final static Map<String, String> unicodeConversionsToXML = new HashMap<String, String>();

    static {
        unicodeConversionsToXML.put("\u03bcg", "MICROg");
        unicodeConversionsToXML.put("\u0025", "PERCENT");
        unicodeConversionsToXML.put("\u2030", "PERMILLE");
    }

    /**
     *
     */
    public final static Map<String, String> unicodeConversionsFromXML = new HashMap<String, String>();

    static {
        unicodeConversionsFromXML.put("MICROg", "\u03bcg");
        unicodeConversionsFromXML.put("PERCENT", "\u0025");
        unicodeConversionsFromXML.put("PERMILLE", "\u2030");
    }
    /**
     *
     */
    public final static Map<String, String> reportTableFootnotes = new HashMap<String, String>();

    static {

        reportTableFootnotes.put(//
                "FN-1", //
                "Th contents calculated from radiogenic 208Pb and the 230Th-corrected 206Pb/238U "// corrected text 25 April 2014 per McLean
                + "date of the sample, assuming concordance between the U-Pb and Th-Pb systems.");
        reportTableFootnotes.put(//
                "FN-2", //
                "Ratio of radiogenic Pb (including 208Pb) to common Pb.");
        reportTableFootnotes.put(//
                "FN-3", //
                "Total mass of radiogenic Pb.");
        reportTableFootnotes.put(//
                "FN-4", //
                "Total mass of common Pb.");
        reportTableFootnotes.put(//
                "FN-5", //
                "<zirconPopulationChoice>");
        reportTableFootnotes.put(//
                "FN-5noZircon", //
                "Measured ratios corrected for fractionation, tracer, blank and initial common Pb.");
        reportTableFootnotes.put(//
                "FN-5zircon", //
                "Measured ratios corrected for fractionation, tracer and blank.");
        reportTableFootnotes.put(//
                "FN-5mixed", //
                "Measured ratios corrected for fractionation, tracer, blank and, where applicable, initial common Pb.");
        reportTableFootnotes.put(//
                "FN-6", //
                "Measured ratio corrected for fractionation and spike contribution only.");
        reportTableFootnotes.put(//
                "FN-7", //
                "Isotopic dates calculated using the decay constants <lambda238> and <lambda235>.");
        reportTableFootnotes.put(//
                "FN-8", //
                "% discordance = 100 - (100 * (206Pb/238U date) / (207Pb/206Pb date))");
        reportTableFootnotes.put(//
                "FN-9", //
                "Isotopic date calculated using the decay constant <lambda232>");
        reportTableFootnotes.put(//
                "FN-10", //
                "Initial [231Pa]/[235U] activity ratio of mineral.");
        reportTableFootnotes.put(//
                "FN-11", //
                "Th/U ratio of magma from which mineral crystallized.");
        reportTableFootnotes.put(//
                "FN-12", //
                "Corrected for initial Th/U disequilibrium using radiogenic 208Pb "
                + "and Th/U[magma] <rTh_Umagma>.");
        reportTableFootnotes.put(//
                "FN-13", //
                "Corrected for initial Pa/U disequilibrium using "
                + "initial fraction activity ratio [231Pa]/[235U] <ar231_235sample>.");
        reportTableFootnotes.put(//
                "FN-14", //
                "Lower-intercept concordia date calculated by assuming that the "
                + "207Pb/206Pb ratio of the common Pb analyzed is <r207_206c>.");
        reportTableFootnotes.put(//
                "FN-15", //
                "Lower-intercept concordia date calculated by assuming the 207Pb/206Pb ratio of common Pb  "
                + "shares a Stacey-Kramers (1975) model date with the intercept date.");
        reportTableFootnotes.put(//
                "FN-16", //
                "Best Date threshold between 206Pb/238U and 206Pb/207Pb is <bestDateDivider> MA.");
        reportTableFootnotes.put(//
                "FN-17", //
                "USeries future footnote.");

    }
}
