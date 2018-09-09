/*
 * SampleUPbRatios.java
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

/**
 *
 * @author James F. Bowring
 */
/*
 *
 */
public enum SampleUPbRatios {

    // sample UPb ratios
    /**
     * 
     */
    r206_204s( "r206_204s" ),
    /**
     * 
     */
    r206_207s( "r206_207s" ),
    /**
     * 
     */
    r206_208s( "r206_208s" ),
    /**
     * 
     */
    r206_238s( "r206_238s" ),
    /**
     * 
     */
    r208_232s( "r208_232s" ),
    /**
     * 
     */
    r238_235s( "r238_235s" );


    private String name;

    private SampleUPbRatios ( String name ) {
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
    public static String[] getNames () {
        String[] retVal = new String[SampleUPbRatios.values().length];
        for (int i = 0; i < SampleUPbRatios.values().length; i ++) {
            retVal[i] = SampleUPbRatios.values()[i].getName();
        }
        return retVal;
    }

    /**
     * 
     * @param checkString
     * @return
     */
    public static boolean contains ( String checkString ) {
        boolean retVal = true;
        try {
            SampleUPbRatios.valueOf( checkString );
        } catch (IllegalArgumentException e) {
            retVal = false;
        }

        return retVal;
    }
}
