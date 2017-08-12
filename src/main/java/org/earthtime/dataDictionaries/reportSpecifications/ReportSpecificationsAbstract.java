/*
 * Copyright 2017 CIRDLES.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.earthtime.dataDictionaries.reportSpecifications;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author James F. Bowring
 */
public abstract class ReportSpecificationsAbstract {

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
        {"", "", "Sample  ", "", "getSampleName", "", "",
            "", "false", "false", "0", "", "", "false", "false"
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
        "auto", "Ma", "ka", "a"
    };
    /**
     * mult units are referred to in units position 4 of report column and
     * converted by lookup in ReduxConstants
     */
    public final static String[] multUnits = new String[]{
        "*1e5", "*1", "*1e3 dpm/g", "*1e6 dpm/g", "*1e9 dpm/g"
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
        unitsType.put("a", "date");
        unitsType.put("*1e5", "mult");
        unitsType.put("*1", "mult");
        unitsType.put("*1e3 dpm/g", "mult");
        unitsType.put("*1e6 dpm/g", "mult");
        unitsType.put("*1e9 dpm/g", "mult");
        unitsType.put("dpm/g", "activity");

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
}
