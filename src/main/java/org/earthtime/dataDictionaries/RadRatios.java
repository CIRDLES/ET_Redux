/*
 * RadRatios.java
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
public enum RadRatios {

    // radiogenic ratios
    // added mar 2013 for laicpms
    /**
     *
     */
    r206_207r("r206_207r"),
    /**
     *
     */
    r207_206r("r207_206r"),
    /**
     *
     */
    r207_235r("r207_235r"),
    /**
     *
     */
    r206_238r("r206_238r"),
    /**
     *
     */
    r238_206r("r238_206r"),
    /**
     *
     */
    r208_232r("r208_232r"),
    // oct 2014 added for common lead correction

    /**
     *
     */
    r206_204r("r206_204r"),
    /**
     *
     */
    r207_204r("r207_204r"),
    /**
     *
     */
    r208_204r("r208_204r"),
    // added dec 2016 in move to cosolidate two sources of these (see datadictionary)
    r208_206r("r208_206r"),
    r206_238r_Th("r206_238r_Th"),
    r207_235r_Pa("r207_235r_Pa"),
    r207_206r_Th("r207_206r_Th"),
    r207_206r_Pa("r207_206r_Pa"),
    r207_206r_ThPa("r207_206r_ThPa"),
    // TODO: list all rhos here
    rhoR206_238r__r207_235r("rhoR206_238r__r207_235r"),
    rhoR207_206r__r238_206r("rhoR207_206r__r238_206r");

    private String name;

    private RadRatios(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    //*************************
    /**
     *
     * @return
     */
    public static String[] getNamesSorted() {
        String[] retVal = new String[RadRatios.values().length];
        for (int i = 0; i < RadRatios.values().length; i++) {
            retVal[i] = RadRatios.values()[i].getName();
        }
        Arrays.sort(retVal);
        return retVal;
    }
    //*************************
}
