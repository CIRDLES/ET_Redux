/*
 * PbcCorrectionDetails.java
 *
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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 * Used solely for testing purposes to enable output of intermediate results
 *
 * @author James F. Bowring
 */
public final class PbcCorrectionDetails {

    /**
     *
     */
    public static String fraction_ID;

    /**
     *
     */
    public static String pbcCorrScheme;
    //for A1 and A2:

    /**
     *
     */
    public static double df0_dR238_206fc;

    /**
     *
     */
    public static double df0_dR207_206fc;

    /**
     *
     */
    public static double df0_dR207_206c;

    /**
     *
     */
    public static double df0_dR238_235s;

    /**
     *
     */
    public static double df0_dLambda238;

    /**
     *
     */
    public static double df0_dLambda235;

    /**
     *
     */
    public static double df0_dt;

    /**
     *
     */
    public static double dt_dR238_206fc;

    /**
     *
     */
    public static double dt_dR207_206fc;

    /**
     *
     */
    public static double dt_dR207_206c;

    /**
     *
     */
    public static double dt_dR238_235s;

    /**
     *
     */
    public static double dt_dLambda238;

    /**
     *
     */
    public static double dt_dLambda235;

//    for B1:
    /**
     *
     */
    public static double upperPhi_r206_204;

    /**
     *
     */
    public static double upperPhi_r207_204;

    /**
     *
     */
    public static double upperPhi_r208_204;

    /**
     *
     */
    public static ValueModel r206_204fc;

    /**
     *
     */
    public static ValueModel r207_204fc;

    /**
     *
     */
    public static ValueModel r208_204fc;

    /**
     *
     */
    public static ValueModel r207_206_PbcCorr;

    /**
     *
     */
    public static ValueModel r206_238_PbcCorr;

    /**
     *
     */
    public static ValueModel r238_206_PbcCorr;

    /**
     *
     */
    public static ValueModel r208_232_PbcCorr;

    /**
     *
     */
    public static ValueModel r207_235_PbcCorr;

    public static double dR68pbcc__dR206_204fc;
    public static double dR68pbcc__dR206_204c;
    public static double dR68pbcc__dR206_238fc;

    public static double dR75pbcc__dR207_204fc;
    public static double dR75pbcc__dR207_204c;
    public static double dR75pbcc__dR207_235fc;

    /**
     *
     */
    public static double dR76pbcc__dR207_204fc;

    /**
     *
     */
    public static double dR76pbcc__dR207_204c;

    /**
     *
     */
    public static double dR76pbcc__dR206_204fc;

    /**
     *
     */
    public static double dR76pbcc__dR206_204c;

    /**
     *
     */
    public static double dR86pbcc__dR238_206fc;

    /**
     *
     */
    public static double dR86pbcc__dR206_204c;

    /**
     *
     */
    public static double dR86pbcc__dR206_204fc;

    /**
     *
     */
    public static double dR82pbcc__dR208_204fc;

    /**
     *
     */
    public static double dR82pbcc__dR208_204c;

    /**
     *
     */
    public static double dR82pbcc__dR208_232fc;

    /**
     *
     */
    public static void zeroAllValues() {
        fraction_ID = "";
        pbcCorrScheme = "N";

        df0_dR238_206fc = 0.0;
        df0_dR207_206fc = 0.0;
        df0_dR207_206c = 0.0;
        df0_dR238_235s = 0.0;
        df0_dLambda238 = 0.0;
        df0_dLambda235 = 0.0;
        df0_dt = 0.0;

        dt_dR238_206fc = 0.0;
        dt_dR207_206fc = 0.0;
        dt_dR207_206c = 0.0;
        dt_dR238_235s = 0.0;
        dt_dLambda238 = 0.0;
        dt_dLambda235 = 0.0;

        upperPhi_r206_204 = 0.0;
        upperPhi_r207_204 = 0.0;
        upperPhi_r208_204 = 0.0;

        r206_204fc = new ValueModel();
        r207_204fc = new ValueModel();
        r208_204fc = new ValueModel();

        r207_206_PbcCorr = new ValueModel();
        r206_238_PbcCorr = new ValueModel();
        r238_206_PbcCorr = new ValueModel();
        r208_232_PbcCorr = new ValueModel();
        r207_235_PbcCorr = new ValueModel();

        dR68pbcc__dR206_204fc = 0.0;
        dR68pbcc__dR206_204c = 0.0;
        dR68pbcc__dR206_238fc = 0.0;

        dR75pbcc__dR207_204c = 0.0;
        dR75pbcc__dR207_204fc = 0.0;
        dR75pbcc__dR207_235fc = 0.0;

        dR76pbcc__dR207_204fc = 0.0;
        dR76pbcc__dR207_204c = 0.0;
        dR76pbcc__dR206_204fc = 0.0;
        dR76pbcc__dR206_204c = 0.0;

        dR86pbcc__dR206_204fc = 0.0;
        dR86pbcc__dR206_204c = 0.0;
        dR86pbcc__dR238_206fc = 0.0;

        dR82pbcc__dR208_204fc = 0.0;
        dR82pbcc__dR208_204c = 0.0;
        dR82pbcc__dR208_232fc = 0.0;

    }

    /**
     *
     * @return
     */
    public static String dataString() {

        NumberFormat formatter2 = new DecimalFormat("0.0000000000E0");

        String retval = "";

        retval += String.format("%1$-15s", fraction_ID);
        retval += String.format("%1$-7s", " (" + pbcCorrScheme + ")");

        retval += String.format("%1$-20s", formatter2.format(df0_dR238_206fc));
        retval += String.format("%1$-20s", formatter2.format(df0_dR207_206fc));
        retval += String.format("%1$-20s", formatter2.format(df0_dR207_206c));
        retval += String.format("%1$-20s", formatter2.format(df0_dR238_235s));
        retval += String.format("%1$-20s", formatter2.format(df0_dLambda238));
        retval += String.format("%1$-20s", formatter2.format(df0_dLambda235));
        retval += String.format("%1$-20s", formatter2.format(df0_dt));

        retval += String.format("%1$-20s", formatter2.format(dt_dR238_206fc));
        retval += String.format("%1$-20s", formatter2.format(dt_dR207_206fc));
        retval += String.format("%1$-20s", formatter2.format(dt_dR207_206c));
        retval += String.format("%1$-20s", formatter2.format(dt_dR238_235s));
        retval += String.format("%1$-20s", formatter2.format(dt_dLambda238));
        retval += String.format("%1$-20s", formatter2.format(dt_dLambda235));

        retval += String.format("%1$-20s", formatter2.format(upperPhi_r206_204));
        retval += String.format("%1$-20s", formatter2.format(upperPhi_r207_204));
        retval += String.format("%1$-20s", formatter2.format(upperPhi_r208_204));
        retval += String.format("%1$-20s", formatter2.format(r206_204fc.getValue().doubleValue()));
        retval += String.format("%1$-20s", formatter2.format(r207_204fc.getValue().doubleValue()));
        retval += String.format("%1$-20s", formatter2.format(r208_204fc.getValue().doubleValue()));
        retval += String.format("%1$-20s", formatter2.format(r207_206_PbcCorr.getValue().doubleValue()));
        retval += String.format("%1$-20s", formatter2.format(r206_238_PbcCorr.getValue().doubleValue()));
        retval += String.format("%1$-20s", formatter2.format(r238_206_PbcCorr.getValue().doubleValue()));
        retval += String.format("%1$-20s", formatter2.format(r208_232_PbcCorr.getValue().doubleValue()));
        retval += String.format("%1$-20s", formatter2.format(r207_235_PbcCorr.getValue().doubleValue()));

        retval += String.format("%1$-22s", formatter2.format(dR68pbcc__dR206_204fc));
        retval += String.format("%1$-22s", formatter2.format(dR68pbcc__dR206_204c));
        retval += String.format("%1$-22s", formatter2.format(dR68pbcc__dR206_238fc));

        retval += String.format("%1$-22s", formatter2.format(dR75pbcc__dR207_204fc));
        retval += String.format("%1$-22s", formatter2.format(dR75pbcc__dR207_204c));
        retval += String.format("%1$-22s", formatter2.format(dR75pbcc__dR207_235fc));

        retval += String.format("%1$-22s", formatter2.format(dR76pbcc__dR207_204fc));
        retval += String.format("%1$-22s", formatter2.format(dR76pbcc__dR207_204c));
        retval += String.format("%1$-22s", formatter2.format(dR76pbcc__dR206_204fc));
        retval += String.format("%1$-22s", formatter2.format(dR76pbcc__dR206_204c));

        retval += String.format("%1$-22s", formatter2.format(dR86pbcc__dR206_204fc));
        retval += String.format("%1$-22s", formatter2.format(dR86pbcc__dR206_204c));
        retval += String.format("%1$-22s", formatter2.format(dR86pbcc__dR238_206fc));

        retval += String.format("%1$-22s", formatter2.format(dR82pbcc__dR208_204fc));
        retval += String.format("%1$-22s", formatter2.format(dR82pbcc__dR208_204c));
        retval += String.format("%1$-22s", formatter2.format(dR82pbcc__dR208_232fc));

        return retval;
    }

    /**
     *
     * @return
     */
    public static String headerString() {
        String retval = "";

        retval += String.format("%1$-15s", "fraction_ID");
        retval += String.format("%1$-7s", " (CS)");

        retval += String.format("%1$-20s", "df0_dR238_206fc");
        retval += String.format("%1$-20s", "df0_dR207_206fc");
        retval += String.format("%1$-20s", "df0_dR207_206c");
        retval += String.format("%1$-20s", "df0_dR238_235s");
        retval += String.format("%1$-20s", "df0_dLambda238");
        retval += String.format("%1$-20s", "df0_dLambda235");
        retval += String.format("%1$-20s", "df0_dt");

        retval += String.format("%1$-20s", "dt_dR238_206fc");
        retval += String.format("%1$-20s", "dt_dR207_206fc");
        retval += String.format("%1$-20s", "dt_dR207_206c");
        retval += String.format("%1$-20s", "dt_dR238_235s");
        retval += String.format("%1$-20s", "dt_dLambda238");
        retval += String.format("%1$-20s", "dt_dLambda235");

        retval += String.format("%1$-20s", "upperPhi_r206_204");
        retval += String.format("%1$-20s", "upperPhi_r207_204");
        retval += String.format("%1$-20s", "upperPhi_r208_204");
        retval += String.format("%1$-20s", "r206_204fc");
        retval += String.format("%1$-20s", "r207_204fc");
        retval += String.format("%1$-20s", "r208_204fc");
        retval += String.format("%1$-20s", "r207_206_PbcCorr");
        retval += String.format("%1$-20s", "r206_238_PbcCorr");
        retval += String.format("%1$-20s", "r238_206_PbcCorr");
        retval += String.format("%1$-20s", "r208_232_PbcCorr");
        retval += String.format("%1$-20s", "r207_235_PbcCorr");

        retval += String.format("%1$-22s", "dR68pbcc__dR206_204fc");
        retval += String.format("%1$-22s", "dR68pbcc__dR206_204c");
        retval += String.format("%1$-22s", "dR68pbcc__dR206_238fc");

        retval += String.format("%1$-22s", "dR75pbcc__dR207_204fc");
        retval += String.format("%1$-22s", "dR75pbcc__dR207_204c");
        retval += String.format("%1$-22s", "dR75pbcc__dR207_235fc");

        retval += String.format("%1$-22s", "dR76pbcc__dR207_204fc");
        retval += String.format("%1$-22s", "dR76pbcc__dR207_204c");
        retval += String.format("%1$-22s", "dR76pbcc__dR206_204fc");
        retval += String.format("%1$-22s", "dR76pbcc__dR206_204c");

        retval += String.format("%1$-22s", "dR86pbcc__dR206_204fc");
        retval += String.format("%1$-22s", "dR86pbcc__dR206_204c");
        retval += String.format("%1$-22s", "dR86pbcc__dR238_206fc");

        retval += String.format("%1$-22s", "dR82pbcc__dR208_204fc");
        retval += String.format("%1$-22s", "dR82pbcc__dR208_204c");
        retval += String.format("%1$-22s", "dR82pbcc__dR208_232fc");

        return retval;
    }

//
//Srpbc
//Jtrpbc
//Su
//
//for A1 weighted means:
//
//JDatePbcIC
//r207_206cOneSigmaSysAbs
//SuPbcIC
//
//
//for B1:
//
//upperPhi_r206_204
//upperPhi_r207_204
//upperPhi_r208_204
//
//r206_204fc
//r207_204fc
//r208_204fc
//
//r207_206_PbcCorr
//r206_238_PbcCorr
//r238_206_PbcCorr
//r208_232_PbcCorr
//r207_235_PbcCorr
//
//dR76pbcc__dR207_204fc
//dR76pbcc__dR207_204c
//dR76pbcc__dR206_204fc
//dR76pbcc__dR206_204c
//dR68pbcc__dR206_204fc
//dR68pbcc__dR206_204c
//dR68pbcc__dR206_238fc
//dR86pbcc__dR206_204fc
//dR86pbcc__dR206_204c
//dR86pbcc__dR206_238fc
//dR82pbcc__dR208_204fc
//dR82pbcc__dR208_204c
//dR82pbcc__dR208_232fc
//
//dR75pbcc__dR207_204fc
//dR75pbcc__dR207_204c
//dR75pbcc__dR206_238fc
//////dR75pbcc__dR206_207fc
//////dR75pbcc_dR238_235s
}
