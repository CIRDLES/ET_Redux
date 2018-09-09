/*
 * TracerRatiosEnum.java
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
 *
 * Created Dec 2010 as part of major refactoring to control magic strings
 */
public enum TracerRatiosEnum {

    // tracer ratios
    /**
     * 
     */
    r206_204t( "r206_204t" ),
    /**
     * 
     */
    r207_204t( "r207_204t" ),
    /**
     * 
     */
    r208_204t( "r208_204t" ),
    /**
     * 
     */
    r207_206t( "r207_206t" ),
    /**
     * 
     */
    r206_208t( "r206_208t" ),
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
    r238_233t( "r238_233t" ),
    /**
     * 
     */
    r233_236t( "r233_236t" ),
    /**
     * 
     */
    r235_205t( "r235_205t" );
    private String name;

    private TracerRatiosEnum ( String name ) {
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
        String[] retVal = new String[TracerRatiosEnum.values().length];
        for (int i = 0; i < TracerRatiosEnum.values().length; i ++) {
            retVal[i] = TracerRatiosEnum.values()[i].getName();
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
            TracerRatiosEnum.valueOf( checkString );
        } catch (IllegalArgumentException e) {
            retVal = false;
        }

        return retVal;
    }
}
