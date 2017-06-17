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
    /**
     *
     */
    public static final String[][] ReportCategory_USeriesReportTable = new String[][]{
        {"conc", "238U", "", "ppm", "getCompositionalMeasureByName", UThCompositionalMeasures.conc238U.getName(), "ABS",
            "", "true", "true", "3", "", "238U concentration", "false", "true"
        },
        {"conc", "232Th", "", "ppb", "getCompositionalMeasureByName", UThCompositionalMeasures.conc232Th.getName(), "ABS",
            "", "true", "true", "3", "", "232Th concentration", "true", "false"
        },
        {"conc", "230Th", "", "ppt", "getCompositionalMeasureByName", UThCompositionalMeasures.conc230Th.getName(), "ABS",
            "", "true", "false", "3", "", "230Th concentration", "true", "false"
        },
        //
        {"", "[230Th/", "232Th]", "", "getAnalysisMeasure", UThAnalysisMeasures.ar230Th_232Thfc.getName(), "ABS",
            "", "true", "false", "3", "true", "[230Th/232Th]", "false", "false"
        },
        {"", "230Th/", "232Th", "", "getRadiogenicIsotopeRatioByName", UThFractionationCorrectedIsotopicRatios.r230Th_232Thfc.getName(), "ABS",
            "", "true", "false", "3", "true", "230Th/232Th", "false", "false"
        },
        //
        {"[232Th/", "238U]", "", "*1e5", "getAnalysisMeasure", UThAnalysisMeasures.ar232Th_238Ufc.getName(), "ABS",
            "", "true", "false", "3", "true", "[232Th/238U]", "false", "false"
        },
        {"232Th/", "238U", "", "*1e5", "getRadiogenicIsotopeRatioByName", UThFractionationCorrectedIsotopicRatios.r232Th_238Ufc.getName(), "ABS",
            "", "true", "false", "3", "true", "232Th/238U", "false", "false"
        },
        //
        {"", "[230Th/", "238U]", "", "getAnalysisMeasure", UThAnalysisMeasures.ar230Th_238Ufc.getName(), "ABS",
            "", "true", "false", "3", "true", "[230Th/238U] (not detrital Th-corr.)", "false", "false"
        },
        {"", "230Th/", "238U", "*1e5", "getRadiogenicIsotopeRatioByName", UThFractionationCorrectedIsotopicRatios.r230Th_238Ufc.getName(), "ABS",
            "", "true", "false", "3", "true", "230Th/238U (not detrital Th-corr.)", "false", "false"
        },
        //
        {"", "delta", "234U", "", "getAnalysisMeasure", UThAnalysisMeasures.delta234U.getName(), "ABS",
            "", "true", "false", "3", "true", "delta 234U (not detrital Th-corr.)", "false", "false"
        },
        {"", "[234U/", "238U]", "", "getAnalysisMeasure", UThAnalysisMeasures.ar234U_238Ufc.getName(), "ABS",
            "", "true", "false", "3", "true", "[234U/238U] (not detrital Th-corr.)", "false", "false"
        },
        {"", "234U/", "238U", "*1e5", "getRadiogenicIsotopeRatioByName", UThFractionationCorrectedIsotopicRatios.r234U_238Ufc.getName(), "ABS",
            "", "true", "false", "3", "true", "234U/238U (not detrital Th-corr.)", "false", "false"
        },
        //
        {"", "[230Th/", "238U]", "", "getAnalysisMeasure", UThAnalysisMeasures.ar230Th_238Udc.getName(), "ABS",
            "", "true", "false", "3", "true", "[230Th/238U] (detrital Th-corr.)", "false", "false"
        },
        {"", "230Th/", "238U", "*1e5", "getRadiogenicIsotopeRatioByName", UThFractionationCorrectedIsotopicRatios.r230Th_238Udc.getName(), "ABS",
            "", "true", "false", "3", "true", "230Th/238U (detrital Th-corr.)", "false", "false"
        },
        //
        {"", "delta", "234U", "", "getAnalysisMeasure", UThAnalysisMeasures.delta234Udc.getName(), "ABS",
            "", "true", "false", "3", "true", "delta 234U (detrital Th-corr.)", "false", "false"
        },
        {"", "[234U/", "238U]", "", "getAnalysisMeasure", UThAnalysisMeasures.ar234U_238Udc.getName(), "ABS",
            "", "true", "false", "3", "true", "[234U/238U] (detrital Th-corr.)", "false", "false"
        },
        {"", "234U/", "238U", "*1e5", "getRadiogenicIsotopeRatioByName", UThFractionationCorrectedIsotopicRatios.r234U_238Udc.getName(), "ABS",
            "", "true", "false", "3", "true", "234U/238U (detrital Th-corr.)", "false", "false"
        },
        //
        {"", "", "Date", "ka", "getRadiogenicIsotopeDateByName", RadDates.date.getName(), "ABS",
            "", "true", "false", "2", "true", "Date (not detrital Th-corr.)", "false", "false"
        },
        {"", "", "Date", "ka", "getRadiogenicIsotopeDateByName", RadDates.dateCorr.getName(), "ABS",
            "", "true", "false", "2", "true", "Date (detrital Th-corr.)", "false", "false"
        },
        //
        {"delta", "234U", "initial", "", "getAnalysisMeasure", UThAnalysisMeasures.delta234Ui.getName(), "ABS",
            "", "true", "false", "3", "true", "initial delta 234U (not detrital Th-corr.)", "false", "false"
        },
        {"[234U/", "238U]", "initial", "", "getAnalysisMeasure", UThAnalysisMeasures.ar234U_238Ui.getName(), "ABS",
            "", "true", "false", "3", "true", "initial [234U/238U] (not detrital Th-corr.)", "false", "false"
        },
        {"234U/", "238U", "initial", "", "getRadiogenicIsotopeRatioByName", UThFractionationCorrectedIsotopicRatios.r234U_238Ui.getName(), "ABS",
            "", "true", "false", "3", "true", "initial 234U/238U (not detrital Th-corr.)", "false", "false"
        },
        //
        {"delta", "234U", "initial", "", "getAnalysisMeasure", UThAnalysisMeasures.delta234Uidc.getName(), "ABS",
            "", "true", "false", "3", "true", "initial delta 234U (detrital Th-corr.)", "false", "false"
        },
        {"[234U/", "238U]", "initial", "", "getAnalysisMeasure", UThAnalysisMeasures.ar234U_238Uidc.getName(), "ABS",
            "", "true", "false", "3", "true", "initial [234U/238U] (detrital Th-corr.)", "false", "false"
        },
        {"234U/", "238U", "initial", "", "getRadiogenicIsotopeRatioByName", UThFractionationCorrectedIsotopicRatios.r234U_238Uidc.getName(), "ABS",
            "", "true", "false", "3", "true", "initial 234U/238U (detrital Th-corr.)", "false", "false"
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
                "Th contents calculated from radiogenic 208Pb and 230Th-corrected 206Pb/238U "// corrected text 25 April 2014 per McLean
                + "date of the sample, assuming concordance between U-Pb Th-Pb systems.");
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
                "Isotopic dates calculated using <lambda238> and <lambda235>.");
        reportTableFootnotes.put(//
                "FN-8", //
                "% discordance = 100 - (100 * (206Pb/238U date) / (207Pb/206Pb date))");
        reportTableFootnotes.put(//
                "FN-9", //
                "Isotopic date calculated using <lambda232>");
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
                "Activity Ratios calculated using <lambda238>.");
        reportTableFootnotes.put(//
                "FN-18", //
                "Activity Ratios calculated using <lambda230>.");
        reportTableFootnotes.put(//
                "FN-19", //
                "Activity Ratios calculated using <lambda232>.");
        reportTableFootnotes.put(//
                "FN-20", //
                "Activity Ratios calculated using <lambda234>.");

    }
}
