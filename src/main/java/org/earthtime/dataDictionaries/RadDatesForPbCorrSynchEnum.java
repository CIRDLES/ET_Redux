/*
 * RadDatesForPbCorrSynchEnum.java
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
public enum RadDatesForPbCorrSynchEnum {

    // radiogenic dates
    /**
     * 
     */
    date206_238r( "206Pb/238U" ),
    /**
     * 
     */
    date207_235r( "207Pb/235U" ),
    /**
     * 
     */
    date208_232r( "208Pb/232Th" );


    private String name;

    private RadDatesForPbCorrSynchEnum ( String name ) {
        this.name = name;
    }

    /**
     * 
     * @return
     */
    public String getName () {
        return name;
    }

    /**
     * 
     * @return
     */
    public static String[] getNamesSorted () {
        String[] retVal = new String[RadDatesForPbCorrSynchEnum.values().length];
        for (int i = 0; i < RadDatesForPbCorrSynchEnum.values().length; i ++) {
            retVal[i] = RadDatesForPbCorrSynchEnum.values()[i].getName();
        }
        Arrays.sort(retVal);
        return retVal;
    }
    
    @Override
    public String toString(){
        return name;
    }
}
