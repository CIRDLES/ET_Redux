/*
 * RadRatiosPbcCorrected.java
 *
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
public enum RadRatiosPbcCorrected {

    // radiogenic ratios PbcCorrected for schme B1 and B2
    // added oct 2014 for laicpms

    /**
     *
     */
        r207_206_PbcCorr("r207_206_PbcCorr"),

    /**
     *
     */
    r206_238_PbcCorr("r206_238_PbcCorr"),

    /**
     *
     */
    r238_206_PbcCorr("r238_206_PbcCorr"),

    /**
     *
     */
    r208_232_PbcCorr("r208_232_PbcCorr"),

    /**
     *
     */
    r207_235_PbcCorr("r207_235_PbcCorr");
    
    private String name;

    private RadRatiosPbcCorrected(String name) {
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
        String[] retVal = new String[RadRatiosPbcCorrected.values().length];
        for (int i = 0; i < RadRatiosPbcCorrected.values().length; i++) {
            retVal[i] = RadRatiosPbcCorrected.values()[i].getName();
        }
        Arrays.sort(retVal);
        return retVal;
    }
    //*************************
}
