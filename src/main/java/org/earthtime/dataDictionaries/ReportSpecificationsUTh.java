/*
 * ReportSpecificationsUTh.java
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
package org.earthtime.dataDictionaries;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author James F. Bowring
 */
public class ReportSpecificationsUTh {
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
    // details: https://docs.google.com/spreadsheets/d/1w15Nc4uD2WEg_e5KvAsNJrBaIImTPPTM8f33NaKkw34/edit#gid=0
    /**
     *
     */
    public static final String[][] ReportCategory_USeriesReportTable = new String[][]{
        //        {"", "", "IGSN", "", "getCompositionalMeasureByName", UThCompositionalMeasures.conc238U.getName(), "ABS",
        //            "", "true", "true", "3", "", "238U concentration", "false", "false"
        //        },
        {"", "238U", "", "ppm", "getCompositionalMeasureByName", UThCompositionalMeasures.conc238U.getName(), "ABS",
            "", "true", "true", "3", "true", "238U concentration", "false", "false"
        },
        {"", "[238U]", "", "dpm/g", "getCompositionalMeasureByName", UThCompositionalMeasures.arConc238U.getName(), "ABS",
            "FN-1", "true", "true", "3", "true", "238U concentration activity", "false", "false"
        },
        //
        {"", "232Th", "", "ppb", "getCompositionalMeasureByName", UThCompositionalMeasures.conc232Th.getName(), "ABS",
            "", "true", "true", "3", "true", "232Th concentration", "false", "false"
        },
        {"", "[232Th]", "", "*1e6 dpm/g", "getCompositionalMeasureByName", UThCompositionalMeasures.arConc232Th.getName(), "ABS",
            "FN-1", "true", "true", "3", "true", "232Th concentration activity", "false", "false"
        },
        //
        {"", "230Th", "", "ppt", "getCompositionalMeasureByName", UThCompositionalMeasures.conc230Th.getName(), "ABS",
            "", "true", "false", "3", "true", "230Th concentration", "false", "false"
        },
        {"", "[230Th]", "", "*1e9 dpm/g", "getCompositionalMeasureByName", UThCompositionalMeasures.arConc230Th.getName(), "ABS",
            "FN-1", "true", "false", "3", "true", "230Th concentration activiry", "false", "false"
        },
        //
        {"", "[230Th/", "232Th]", "", "getAnalysisMeasure", UThAnalysisMeasures.ar230Th_232Thfc.getName(), "ABS",
            "FN-1", "true", "false", "3", "true", "[230Th/232Th]", "false", "false"
        },
        {"", "230Th/", "232Th", "", "getRadiogenicIsotopeRatioByName", UThFractionationCorrectedIsotopicRatios.r230Th_232Thfc.getName(), "ABS",
            "", "true", "false", "3", "true", "230Th/232Th", "false", "false"
        },
        //
        {"[232Th/", "238U]", "", "*1e5", "getAnalysisMeasure", UThAnalysisMeasures.ar232Th_238Ufc.getName(), "ABS",
            "FN-1", "true", "false", "3", "true", "[232Th/238U]", "false", "false"
        },
        {"232Th/", "238U", "", "*1e5", "getRadiogenicIsotopeRatioByName", UThFractionationCorrectedIsotopicRatios.r232Th_238Ufc.getName(), "ABS",
            "", "true", "false", "3", "true", "232Th/238U", "false", "false"
        },
        //
        {"", "[230Th/", "238U]", "", "getAnalysisMeasure", UThAnalysisMeasures.ar230Th_238Ufc.getName(), "ABS",
            "FN-1&FN-2", "true", "false", "3", "true", "[230Th/238U] (not detrital Th-corr.)", "false", "false"
        },
        {"", "230Th/", "238U", "*1e5", "getRadiogenicIsotopeRatioByName", UThFractionationCorrectedIsotopicRatios.r230Th_238Ufc.getName(), "ABS",
            "FN-2", "true", "false", "3", "true", "230Th/238U (not detrital Th-corr.)", "false", "false"
        },
        //
        {"", "delta", "234U", "", "getAnalysisMeasure", UThAnalysisMeasures.delta234U.getName(), "ABS",
            "FN-3", "true", "false", "3", "true", "delta 234U (not detrital Th-corr.)", "false", "false"
        },
        {"", "[234U/", "238U]", "", "getAnalysisMeasure", UThAnalysisMeasures.ar234U_238Ufc.getName(), "ABS",
            "FN-1&FN-3", "true", "false", "3", "true", "[234U/238U] (not detrital Th-corr.)", "false", "false"
        },
        {"", "234U/", "238U", "*1e5", "getRadiogenicIsotopeRatioByName", UThFractionationCorrectedIsotopicRatios.r234U_238Ufc.getName(), "ABS",
            "FN-3", "true", "false", "3", "true", "234U/238U (not detrital Th-corr.)", "false", "false"
        },
        //
        {"", "[230Th/", "238U]", "", "getAnalysisMeasure", UThAnalysisMeasures.ar230Th_238Udc.getName(), "ABS",
            "FN-1&FN-2", "true", "false", "3", "true", "[230Th/238U] (detrital Th-corr.)", "false", "false"
        },
        {"", "230Th/", "238U", "*1e5", "getRadiogenicIsotopeRatioByName", UThFractionationCorrectedIsotopicRatios.r230Th_238Udc.getName(), "ABS",
            "FN-2", "true", "false", "3", "true", "230Th/238U (detrital Th-corr.)", "false", "false"
        },
        //
        {"", "delta", "234U", "", "getAnalysisMeasure", UThAnalysisMeasures.delta234Udc.getName(), "ABS",
            "", "true", "false", "3", "true", "delta 234U (detrital Th-corr.)", "false", "false"
        },
        {"", "[234U/", "238U]", "", "getAnalysisMeasure", UThAnalysisMeasures.ar234U_238Udc.getName(), "ABS",
            "FN-1", "true", "false", "3", "true", "[234U/238U] (detrital Th-corr.)", "false", "false"
        },
        {"", "234U/", "238U", "*1e5", "getRadiogenicIsotopeRatioByName", UThFractionationCorrectedIsotopicRatios.r234U_238Udc.getName(), "ABS",
            "", "true", "false", "3", "true", "234U/238U (detrital Th-corr.)", "false", "false"
        },
        //
        {"", "", "Date", "ka", "getRadiogenicIsotopeDateByName", RadDates.date.getName(), "ABS",
            "FN-6", "true", "false", "2", "true", "Date (not detrital Th-corr.)", "false", "false"
        },
        {"BP", "", "Date", "ka", "getRadiogenicIsotopeDateByName", RadDates.dateBP.getName(), "ABS",
            "FN-7", "true", "false", "2", "true", "Date BP (not detrital Th-corr.)", "false", "false"
        },
        {"", "", "Date", "ka", "getRadiogenicIsotopeDateByName", RadDates.dateCorr.getName(), "ABS",
            "", "true", "false", "2", "true", "Date (detrital Th-corr.)", "false", "false"
        },
        {"BP", "", "Date", "ka", "getRadiogenicIsotopeDateByName", RadDates.dateCorrBP.getName(), "ABS",
            "", "true", "false", "2", "true", "Date BP (detrital Th-corr.)", "false", "false"
        },
        //
        {"delta", "234U", "initial", "", "getAnalysisMeasure", UThAnalysisMeasures.delta234Ui.getName(), "ABS",
            "", "true", "false", "3", "true", "initial delta 234U (not detrital Th-corr.)", "false", "false"
        },
        {"[234U/", "238U]", "initial", "", "getAnalysisMeasure", UThAnalysisMeasures.ar234U_238Ui.getName(), "ABS",
            "FN-1", "true", "false", "3", "true", "initial [234U/238U] (not detrital Th-corr.)", "false", "false"
        },
        {"234U/", "238U", "initial", "", "getRadiogenicIsotopeRatioByName", UThFractionationCorrectedIsotopicRatios.r234U_238Ui.getName(), "ABS",
            "", "true", "false", "3", "true", "initial 234U/238U (not detrital Th-corr.)", "false", "false"
        },
        //
        {"delta", "234U", "initial", "", "getAnalysisMeasure", UThAnalysisMeasures.delta234Uidc.getName(), "ABS",
            "", "true", "false", "3", "true", "initial delta 234U (detrital Th-corr.)", "false", "false"
        },
        {"[234U/", "238U]", "initial", "", "getAnalysisMeasure", UThAnalysisMeasures.ar234U_238Uidc.getName(), "ABS",
            "FN-1", "true", "false", "3", "true", "initial [234U/238U] (detrital Th-corr.)", "false", "false"
        },
        {"234U/", "238U", "initial", "", "getRadiogenicIsotopeRatioByName", UThFractionationCorrectedIsotopicRatios.r234U_238Uidc.getName(), "ABS",
            "", "true", "false", "3", "true", "initial 234U/238U (detrital Th-corr.)", "false", "false"
        },
        //
        {"rho", "date", "delta234U", "", "getAnalysisMeasure", UThAnalysisMeasures.rhoDate__delta234Ui.getName(), "",
            "", "true", "true", "3", "", "Corr. Coef. date - del234Ui", "false", "false"
        }
    };

    // Report column order =
    //  displayName1, displayName2, displayName3, units, retrieveMethodName, retrieveParameterName, uncertaintyType,
    //     footnoteSpec, visible, useArbitrary? for value, digitcount value, unct visible (if required), description where needed,
    //     needsLead, needsUranium
    /**
     *
     */
    public static final String[][] ReportCategory_CompositionUTh = new String[][]{
        {"[238U/", "232Th]", "", "", "getAnalysisMeasure", UThAnalysisMeasures.ar238U_232Thfc.getName(), "ABS",
            "FN-19&FN-17", "false", "true", "3", "true", "[238U/232Th] activity ratio", "false", "false"
        },
        {"", "", "[230Th]", "", "getAnalysisMeasure", UThAnalysisMeasures.a230Thfc.getName(), "ABS",
            "FN-20&FN-17", "true", "false", "3", "true", "[230Th] activity", "false", "false"
        },
        {"", "", "[226Ra]", "", "getAnalysisMeasure", UThAnalysisMeasures.a226Rafc.getName(), "ABS",
            "FN-20&FN-17", "true", "false", "3", "true", "[226Ra] activity", "false", "false"
        },
        {"", "[226Ra/", "230Th]", "", "getAnalysisMeasure", UThAnalysisMeasures.ar226Ra_230Thfc.getName(), "ABS",
            "FN-20&FN-17", "true", "false", "3", "true", "[226Ra/230Th] activity ratio", "false", "false"
        },
        {"conc", "Ba", "", "ppm", "getCompositionalMeasureByName", UThCompositionalMeasures.concBa.getName(), "ABS",
            "", "true", "true", "3", "", "concentration of Ba", "true", "false"
        },};

    /**
     * mass units are referred to in units position 4 of report column and
     * converted by lookup in ReduxConstants
     */
    public final static String[] massUnits = new String[]{
        "g", "mg", "\u03bcg", "ng", "pg", "fg"
    };

    /**
     * concentration units are referred to in units position 4 of report column
     * and converted by lookup in ReduxConstants
     */
    public final static String[] concUnits = new String[]{
        "\u0025", "\u2030", "ppm", "ppb", "ppt"
    };

    /**
     * date units are referred to in units position 4 of report column and
     * converted by lookup in ReduxConstants
     */
    public final static String[] dateUnits = new String[]{
        "auto", "Ma", "ka"
    };
    /**
     * mult units are referred to in units position 4 of report column and
     * converted by lookup in ReduxConstants
     */
    public final static String[] multUnits = new String[]{
        "*1e5", "*1"
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
        unitsType.put("ppt", "conc");
        unitsType.put("Ma", "date");
        unitsType.put("ka", "date");
        unitsType.put("*1e5", "mult");
        unitsType.put("*1", "mult");

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
                "Square brackets denote activity ratios.");
        reportTableFootnotes.put(//
                "FN-2", //
                "Ratio determined using a Th-U spike calibrated to a <secularOrGravimetric230238><named230Th238Umodel>");
        reportTableFootnotes.put(//
                "FN-3", //
                "Ratio calibrated to a <secularOrGravimetric234238><named234U238Umodel>");

        reportTableFootnotes.put(//
                "FN-6", //
                "Uncorrected, closed-system date calculated using the decay constants <lambda230>,\n<lambda234>, and <lambda238>.\nDates reported relative to"
                + " the date of analysis, <dateOfAnalysis>,\nand do not include uncertainties associated with decay constants.");
        reportTableFootnotes.put(//
                "FN-7", //
                "Uncorrected, closed-system date calculated using the decay constants <lambda230>,\n<lambda234>, and <lambda238>.\nDates reported as Before Present (BP),"
                + " where Present is the year 1950 CE.,\nand do not include uncertainties associated with decay constants.");

    }
}
