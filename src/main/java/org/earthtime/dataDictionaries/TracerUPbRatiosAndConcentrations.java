/*
 * TracerUPbRatiosAndConcentrations.java
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
/* TracerUPbRatiosAndConcentrations for new models starting May 2012
 */
public enum TracerUPbRatiosAndConcentrations {

    /**
     *
     */
    r202_205t( "r202_205t" ),
    /**
     *
     */
    r204_205t( "r204_205t" ),
    /**
     *
     */
    r206_205t( "r206_205t" ),
    /**
     *
     */
    r207_205t( "r207_205t" ),
    /**
     *
     */
    r208_205t( "r208_205t" ),
    /**
     *
     */
    r233_235t( "r233_235t" ),
    /**
     *
     */
    r238_235t( "r238_235t" ),
    /**
     *
     */
    r233_236t( "r233_236t" ),
    /**
     *
     */
    r238_233t( "r238_233t" ),

    /**
     *
     */
    concPb205t( "concPb205t" ),

    /**
     *
     */
    concU235t( "concU235t" ),

    /**
     *
     */
    concU236t( "concU236t" );
    private String name;

    private TracerUPbRatiosAndConcentrations ( String name ) {
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
        String[] retVal = new String[TracerUPbRatiosAndConcentrations.values().length];
        for (int i = 0; i < TracerUPbRatiosAndConcentrations.values().length; i ++) {
            retVal[i] = TracerUPbRatiosAndConcentrations.values()[i].getName();
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
            TracerUPbRatiosAndConcentrations.valueOf( checkString );
        } catch (IllegalArgumentException e) {
            retVal = false;
        }

        return retVal;
    }
}
