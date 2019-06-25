/*
 * SampleDateTypes.java
 *
 * Copyright 2006-2018 James F. Bowring, CIRDLES.org, and Earth-Time.org
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

/**
 *
 * @author James F. Bowring
 */
public final class SampleDateTypes {
// type, methodname, name of age if appropriate

    private static final String[][] SampleDateTypes = new String[][]{
        {"single analysis 206Pb/238U", "SA206_238", RadDates.age206_238r.getName()},
        {"single analysis 207Pb/235U", "SA207_235", RadDates.age207_235r.getName()},
        {"single analysis 207Pb/206Pb", "SA207_206", RadDates.age207_206r.getName()},
        {"single analysis 208Pb/232Th", "SA208_232", RadDates.age208_232r.getName()},
        {"weighted mean 206Pb/238U", "WM206_238", RadDates.age206_238r.getName()},
        {"weighted mean 207Pb/235U", "WM207_235", RadDates.age207_235r.getName()},
        {"weighted mean 207Pb/206Pb", "WM207_206", RadDates.age207_206r.getName()},
        {"weighted mean 208Pb/232Th", "WM208_232", RadDates.age208_232r.getName()},
        {"weighted mean 206Pb/238U (Th-corrected)", "WM206_238r_Th", RadDates.age206_238r_Th.getName()},
        {"weighted mean 207Pb/235U (Pa-corrected)", "WM207_235r_Pa", RadDates.age207_235r_Pa.getName()},
        {"weighted mean 207Pb/206Pb (Th-corrected)", "WM207_206r_Th", RadDates.age207_206r_Th.getName()},
        {"weighted mean 207Pb/206Pb (Pa-corrected)", "WM207_206r_Pa", RadDates.age207_206r_Pa.getName()},
        {"weighted mean 207Pb/206Pb (Th- and Pa-corrected)", "WM207_206r_ThPa", RadDates.age207_206r_ThPa.getName()},
        //{"Tuff Zirc 206Pb/238U (filtered median age)", "TZ206_238_fma", ""},
        //{"Tuff Zirc 207Pb/235U (filtered median age)", "TZ207_235_fma", ""},
        //{"Tuff Zirc 208Pb/232U (filtered median age)", "TZ207_238_fma", ""},
        //{"concordia", "Concordia", ""},
        // intercepts currently use this check for non zero values based on these dates
        {"lower intercept", "LowerIntercept", RadDates.age206_238r.getName()},
        {"upper intercept", "UpperIntercept", RadDates.age206_238r.getName()}, //
        //{"238U/206Pb isochron", "ISO238_206", ""},
        //{"235U/207Pb isochron", "ISO235_207", ""},
        //{"232Th/208Pb isochron", "ISO232_208", ""},
        //{"Semi-TotalPb isochron", "ISO_SemiTotalPb", ""},
        //{"Total Pb isochron", "ISO_TotalPb", ""},
        // USeries volcanic Aug 2017
        {"238U-230Th isochron", "ISO238_230", UThAnalysisMeasures.ar230Th_232Thfc.getName()},
        // USeries carbonate Nov 2018
        {"weighted mean Corrected Date", "WMDate", RadDates.dateCorr.getName()},
        {"weighted mean UnCorrected Date", "WMDate", RadDates.date.getName()},
        {"open-system Date", "WMDate", RadDates.dateOpenSys.getName()},
    };

    /**
     *
     * @param index
     * @return
     */
    public static String getSampleDateType(int index) {
        return SampleDateTypes[index][0];
    }

    /**
     *
     * @param index
     * @return
     */
    public static String getSampleDateTypeMethod(
            int index) {
        return SampleDateTypes[index][1];
    }

    /**
     *
     * @param index
     * @return
     */
    public static String getSampleDateTypeName(
            int index) {
        return SampleDateTypes[index][2];
    }

    /**
     *
     * @return
     */
    public static String[][] getSampleDateModelTypes() {
        return SampleDateTypes;
    }
}
