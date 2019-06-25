/*
 * RadDates.java
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
package org.earthtime.dataDictionaries;

import java.util.Arrays;

/**
 *
 * @author James F. Bowring
 */
/*
 *
 *
 * Created Oct 2010 as part of major refactoring to control magic strings
 */
public enum RadDates {

    // radiogenic dates
    /**
     * 
     */
    age206_238r( "age206_238r" ),
    /**
     * 
     */
    age207_235r( "age207_235r" ),
    /**
     * 
     */
    age207_206r( "age207_206r" ),
    /**
     * 
     */
    age208_232r( "age208_232r" ),
    /**
     * 
     */
    percentDiscordance( "percentDiscordance" ),
    /**
     * 
     */
    age206_238r_Th( "age206_238r_Th" ),
    /**
     * 
     */
    age207_206r_Th( "age207_206r_Th" ),
    /**
     * 
     */
    age207_235r_Pa( "age207_235r_Pa" ),
    /**
     * 
     */
    age207_206r_Pa( "age207_206r_Pa" ),
    /**
     * 
     */
    age207_206r_ThPa( "age207_206r_ThPa" ),

    /**
     *
     */
    PbcCorr_UPb_Date("PbcCorr_UPb_Date"),

    /**
     *
     */
    age206_238_PbcCorr("age206_238_PbcCorr"),

    /**
     *
     */
    age208_232_PbcCorr("age208_232_PbcCorr"),

    /**
     *
     */
    age207_235_PbcCorr("age207_235_PbcCorr"),

    /**
     *
     */
    age207_206_PbcCorr("age207_206_PbcCorr"),

    /**
     *
     */
    percentDiscordance_PbcCorr("percentDiscordance_PbcCorr"),
    /**
     * 
     */
    bestAge( "bestAge" ),

    /**
     *
     */
    bestAge_PbcCorr( "bestAge_PbcCorr" ),
    date("date"),
    dateCorr("dateCorr"),
    dateBP("dateBP"),
    dateCorrBP("dateCorrBP"),
    dateOpenSys("dateOpenSys");


    private String name;

    private RadDates ( String name ) {
        this.name = name;
    }

    /**
     * 
     * @return
     */
    public String getName () {
        return name;
    }
    
     //*************************
    /**
     * 
     * @return
     */
    public static String[] getNamesSorted () {
        String[] retVal = new String[RadDates.values().length];
        for (int i = 0; i < RadDates.values().length; i ++) {
            retVal[i] = RadDates.values()[i].getName();
        }
        Arrays.sort(retVal);
        return retVal;
    }
    //*************************
}
