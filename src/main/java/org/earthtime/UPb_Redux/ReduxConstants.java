/*
 * ReduxConstants.java
 *
 * Created on April 7, 2006, 7:32 PM
 *
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
package org.earthtime.UPb_Redux;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.awt.Color;
import java.awt.Font;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import javax.swing.JLabel;

/**
 *
 * @author James F. Bowring
 */
final public class ReduxConstants {

    // tripoli colors
    static private final float[] myRawIsotopeGreen
            = Color.RGBtoHSB(173, 204, 182, null);//(246, 254, 235, null);
    /**
     *
     */
    static public final Color ColorOfRawIsotopeGreen
            = Color.getHSBColor(myRawIsotopeGreen[0], myRawIsotopeGreen[1], myRawIsotopeGreen[2]);
    static private final float[] myCorrIsotopeYellow
            = Color.RGBtoHSB(204, 199, 173, null);//(246, 254, 235, null);
    /**
     *
     */
    static public final Color ColorOfCorrIsotopeYellow
            = Color.getHSBColor(myCorrIsotopeYellow[0], myCorrIsotopeYellow[1], myCorrIsotopeYellow[2]);
    /**
     *
     */
    static public final Color LightBlueForUnknowns
            = new Color(245, 251, 252);
    // redux colors
    static private final float[] myLightGreen
            = Color.RGBtoHSB(220, 255, 235, null);//(246, 254, 235, null);
    /**
     *
     */
    static public final Color ColorOfLead
            = Color.getHSBColor(myLightGreen[0], myLightGreen[1], myLightGreen[2]);
    static private final float[] myLightYellow
            = Color.RGBtoHSB(250, 250, 220, null);
    /**
     *
     */
    static public final Color ColorOfUranium
            = Color.getHSBColor(myLightYellow[0], myLightYellow[1], myLightYellow[2]);
    static private final float[] myLightRed
            = Color.RGBtoHSB(252, 237, 237, null);
    /**
     *
     */
    static public final Color ColorOfRedux
            = Color.getHSBColor(myLightRed[0], myLightRed[1], myLightRed[2]);
    static private final float[] mySampleYellow
            = Color.RGBtoHSB(245, 236, 206, null);
    /**
     *
     */
    static public final Color mySampleYellowColor
            = Color.getHSBColor(mySampleYellow[0], mySampleYellow[1], mySampleYellow[2]);
    static private final float[] myFractionGreen
            = Color.RGBtoHSB(212, 231, 232, null);
    /**
     *
     */
    static public final Color myFractionGreenColor
            = Color.getHSBColor(myFractionGreen[0], myFractionGreen[1], myFractionGreen[2]);
    static private final float[] myAliquotGray
            = Color.RGBtoHSB(180, 180, 180, null);//Color.RGBtoHSB(153, 153, 153, null);
    /**
     *
     */
    static public final Color myAliquotGrayColor
            = Color.getHSBColor(myAliquotGray[0], myAliquotGray[1], myAliquotGray[2]);
    static private final float[] myNotEditingGrey
            = Color.RGBtoHSB(222, 222, 222, null);
    /**
     *
     */
    static public final Color myNotEditingGreyColor
            = Color.getHSBColor(myNotEditingGrey[0], myNotEditingGrey[1], myNotEditingGrey[2]);
    static private final float[] myEditingWhite
            = Color.RGBtoHSB(255, 255, 255, null);
    /**
     *
     */
    static public final Color myEditingWhiteColor
            = Color.getHSBColor(myEditingWhite[0], myEditingWhite[1], myEditingWhite[2]);
    static private final float[] myAgesOrange
            = Color.RGBtoHSB(255, 234, 183, null);
    /**
     *
     */
    static public final Color myAgesOrangeColor
            = Color.getHSBColor(myAgesOrange[0], myAgesOrange[1], myAgesOrange[2]);
    /**
     *
     */
    static public final Color dataModelGray
            = //
            new Color(235, 235, 235);

    /**
     *
     */
    static public final Color palePinkBelowDetection
            = //
            new Color(255, 248, 248);//pale pink
    /**
     *
     */
    static public String myUsersUPbReduxDataFolderName = "U-Pb_Redux Data";
    static public String myUsersETReduxDataFolderName = "ET_Redux Data";
    /**
     *
     */
    static public BigDecimal PicoGramsPerGram = new BigDecimal("1000000000000");
    /**
     *
     */
    static public MathContext MCforBigD_5 = new MathContext(5);
    /**
     *
     */
    static public int DEFAULT_RATIO_SCALE = 20;
    /**
     *
     */
    static public int DEFAULT_LAMBDAS_SCALE = 18;
    /**
     *
     */
    static public int DEFAULT_SCALE = 25;
    /**
     *
     */
    static public int DEFAULT_CONSTANTS_SCALE = 10;
    /**
     *
     */
    static public int DEFAULT_DEFAULTS_SCALE = 6;
    /**
     *
     */
    static public int DEFAULT_DATE_SCALE = 5;
    /**
     *
     */
    static public int DEFAULT_MASS_DISPLAY_SCALE = 5;
    /**
     *
     */
    static public int DEFAULT_PARAMETERS_SCALE = 4;
    /**
     *
     */
    static public int DEFAULT_SIGMA_SCALE = 2;
    /**
     *
     */
    static public int DEFAULT_DATE_MA_SCALE = 1;
    /**
     *
     */
    static public int DEFAULT_REPORT_SCALE = 25;
    /**
     *
     */
    static public int DEFAULT_PRECISION = 15;
    /**
     *
     */
    static public MathContext mathContext15 = new MathContext(15, RoundingMode.HALF_UP);

    /**
     *
     */
    static public MathContext mathContextSlider = new MathContext(5, RoundingMode.HALF_UP);

    /**
     *
     */
    static public MathContext mathContext10 = new MathContext(10, RoundingMode.HALF_UP);

    static public double MINUTES_PER_YEAR_FOR_USERIES_CONVERSIONS = 525960;

    /**
     *
     */
    static public MathContext mathContext5 = new MathContext(5, RoundingMode.HALF_UP);
    /**
     *
     */
    static public String DEFAULT_IGSN = "IGSN";
    /**
     *
     */
    static public String DEFAULT_ALIQUOT_IGSN = "IGSN";
    /**
     *
     */
    static public String NAME_OF_SAMPLEMETADATA_FOLDER = "SampleMetaData";
    /**
     *
     */
    static public String XML_Header
            = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" //
            + "<!--\n" //
            + "    *****************************************************************************\n" //
            + "    * This XML file was generated by ET_Redux.                                  *\n" //
            + "    * For more info, visit https://CIRDLES.org                                  *\n" //
            + "    *****************************************************************************\n" //
            + "-->\n";
    /**
     *
     */
    static public String XML_ResourceHeader
            = "xmlns=\"https://raw.githubusercontent.com/EARTHTIME/Schema\"\n" //
            + "        xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\n" //
            + "        xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" //
            + "        xsi:schemaLocation=\"https://raw.githubusercontent.com/EARTHTIME/Schema\n" //
            + "                            ";
    /**
     *
     */
    static public double MAX_DATE_ANNUM = 4.7E9;
    /**
     *
     */
    static public double NO_RHO_FLAG = -9.0;// -999999.99;
    /**
     *
     */
    static public String DEFAULT_OBJECT_NAME = "NONE";//
    //
    //
    /**
     *
     */
    private static final Map<String, Integer> UnitConversions = new HashMap<>();

    // key = units, value = points to shift right
    static {

        UnitConversions.put("", 0);

        // mass is stored in grams
        UnitConversions.put("g", 0);
        UnitConversions.put("mg", 3);
        UnitConversions.put("\u03bcg", 6);
        UnitConversions.put("ng", 9);
        UnitConversions.put("pg", 12);
        UnitConversions.put("fg", 15);

        // concentrations
        UnitConversions.put("\u0025", 2);
        UnitConversions.put("\u2030", 3);
        UnitConversions.put("ppm", 6);
        UnitConversions.put("ppb", 9);
        UnitConversions.put("ppt", 12);
        UnitConversions.put("g/g", 0);
        UnitConversions.put("*1e5", 5);
        UnitConversions.put("*1", 0);
        UnitConversions.put("dpm/g", 0);
        UnitConversions.put("*1e3 dpm/g", 3);
        UnitConversions.put("*1e6 dpm/g", 6);
        UnitConversions.put("*1e9 dpm/g", 9);

        // dates are stored in years
        UnitConversions.put("a", 0);
        UnitConversions.put("ka", -3);
        UnitConversions.put("Ma", -6);
        UnitConversions.put("Ga", -9);

        // misc in % per amu
        UnitConversions.put("%/amu", 2);

        // time in seconds
        UnitConversions.put("ns", 9);
    }

    /**
     *
     * @param unit
     * @return
     */
    static public int getUnitConversionMoveCount(String unit) {
        return UnitConversions.get(unit);
    }

    /**
     *
     */
    static public enum TOP_PANEL_CONTENTS//
    {

        /**
         *
         */
        WELCOME,
        /**
         *
         */
        FRACTIONS,
        /**
         *
         */
        WAITING;
    }

    // graphaxesdialog creates this map on the fly ... should be moved
    /**
     * Dictionary has been deprecated but still lives in JSlider so this must
     * stay a Hashtable ! (Jan 2012)
     */
    public static class ConcordiaTicFormatStrings extends Hashtable<Integer, JLabel> {

        // key = count of showing decimal places, value = actual appearance
        /**
         *
         * @param s
         */
        public ConcordiaTicFormatStrings(String s) {

            put(2, new JLabel(s + "00"));
            put(3, new JLabel(s + "0"));
            put(4, new JLabel(s));
            put(5, new JLabel("0." + s));
            put(6, new JLabel("0.0" + s));
            put(7, new JLabel("0.00" + s));
            put(8, new JLabel("0.000" + s));
            put(9, new JLabel("0.0000" + s));

        }
    }

    /**
     *
     */
    public static enum ANALYSIS_PURPOSE//
    {

        /**
         *
         */
        NONE("NONE"),
        /**
         *
         */
        DetritalSpectrum("DetritalSpectrum"),
        /**
         *
         */
        SingleAge("SingleAge"),
        /**
         *
         */
        Cooling("Cooling"),
        /**
         *
         */
        TimeScaleCalibration("TimeScaleCalibration");
        private final String appearance;

        private ANALYSIS_PURPOSE(String appearance) {
            this.appearance = appearance;
        }

        /**
         *
         * @return
         */
        @Override
        public String toString() {
            return appearance;
        }
    }
    /**
     *
     */
    static public final Font sansSerif_12_Bold = new Font(
            "SansSerif",
            Font.BOLD,
            12);
    /**
     *
     */
    static public final Font sansSerif_10_Bold = new Font(
            "SansSerif",
            Font.BOLD,
            10);
    /**
     *
     */
    static public final Font sansSerif_10_Plain = new Font(
            "SansSerif",
            Font.PLAIN,
            10);

    /**
     *
     */
    static public final Font sansSerif_11_Plain = new Font(
            "SansSerif",
            Font.PLAIN,
            11);

    /**
     *
     */
    static public final Font sansSerif_11_Bold = new Font(
            "SansSerif",
            Font.BOLD,
            11);
    /**
     *
     */
    static public final Font sansSerif_12_Plain = new Font(
            "SansSerif",
            Font.PLAIN,
            10);
    /**
     *
     */
    static public final String NONE = "#NONE#";
    private static final Map<String, Integer> MonthConversions = new HashMap<>();

    // key = months of year; value 0-based array index
    static {

        MonthConversions.put("JANUARY", 0);
        MonthConversions.put("FEBRUARY", 1);
        MonthConversions.put("MARCH", 2);
        MonthConversions.put("APRIL", 3);
        MonthConversions.put("MAY", 4);
        MonthConversions.put("JUNE", 5);
        MonthConversions.put("JULY", 6);
        MonthConversions.put("AUGUST", 7);
        MonthConversions.put("SEPTEMBER", 8);
        MonthConversions.put("OCTOBER", 9);
        MonthConversions.put("NOVEMBER", 10);
        MonthConversions.put("DECEMBER", 11);

        MonthConversions.put("JAN", 0);
        MonthConversions.put("FEB", 1);
        MonthConversions.put("MAR", 2);
        MonthConversions.put("APR", 3);
        MonthConversions.put("MAY", 4);
        MonthConversions.put("JUN", 5);
        MonthConversions.put("JUL", 6);
        MonthConversions.put("AUG", 7);
        MonthConversions.put("SEP", 8);
        MonthConversions.put("OCT", 9);
        MonthConversions.put("NOV", 10);
        MonthConversions.put("DEC", 11);

    }

    /**
     *
     * @param month
     * @return
     */
    static public int getMonthConversions(String month) {
        return MonthConversions.get(month.toUpperCase());
    }

    // map of indexes to scheme names
    /**
     *
     */
    public static final BiMap<Integer, String> biMapOfIndexesToCommonLeadCorrectionSchemaNames = HashBiMap.create();

    static {
        biMapOfIndexesToCommonLeadCorrectionSchemaNames.put(0, "NONE");
        biMapOfIndexesToCommonLeadCorrectionSchemaNames.put(1, "A1");
        biMapOfIndexesToCommonLeadCorrectionSchemaNames.put(2, "A2");
        biMapOfIndexesToCommonLeadCorrectionSchemaNames.put(3, "B1");
        biMapOfIndexesToCommonLeadCorrectionSchemaNames.put(4, "B2");
        biMapOfIndexesToCommonLeadCorrectionSchemaNames.put(5, "C");
        biMapOfIndexesToCommonLeadCorrectionSchemaNames.put(6, "D");
    }

    // June 2017 for UTh analysis
    public static final long TIME_IN_MILLISECONDS_FROM_1970_TO_1950 = -631152000000l;
    public static final long TIME_IN_MILLISECONDS_FROM_1970_TO_2000 = 946684800000l;
    public static final double AVOGADROS_NUMBER = 6.022140857e23;

    public static String makeFormattedDate(long milliseconds) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(milliseconds);
        SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY");

        return dateFormat.format(calendar.getTime());
    }
}
