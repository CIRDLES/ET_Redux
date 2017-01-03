/*
 * MeasuredRatios.java
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

/**
 *
 * @author James F. Bowring
 */
/*
 *
 *
 * Created Dec 2010 as part of major refactoring to control magic strings
 */
public enum MeasuredRatios {

    // measured ratios
    /**
     * 
     */
    r206_204m( "r206_204m" ),
    /**
     * 
     */
    r207_204m( "r207_204m" ),
    /**
     * 
     */
    r208_204m( "r208_204m" ),
    /**
     * 
     */
    r206_207m( "r206_207m" ),    
    /**
     * 
     */
    r206_208m( "r206_208m" ),
    /**
     * 
     */
    r204_205m( "r204_205m" ),
    /**
     * 
     */
    r206_205m( "r206_205m" ),
    /**
     * 
     */
    r207_205m( "r207_205m" ),
    /**
     * 
     */
    r208_205m( "r208_205m" ),
    /**
     * 
     */
    r202_205m( "r202_205m" ),
    /**
     * 
     */
    r238_235m( "r238_235m" ),
    /**
     * 
     */
    r233_235m( "r233_235m" ),
    /**
     * 
     */
    r238_233m( "r238_233m" ),
    /**
     * 
     */
    r238_236m( "r238_236m" ),
    /**
     * 
     */
    r233_236m( "r233_236m" );
 



    private String name;

    private MeasuredRatios ( String name ) {
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
        String[] retVal = new String[MeasuredRatios.values().length];
        for (int i = 0; i < MeasuredRatios.values().length; i ++) {
            retVal[i] = MeasuredRatios.values()[i].getName();
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
            MeasuredRatios.valueOf( checkString );
        } catch (IllegalArgumentException e) {
            retVal = false;
        }

        return retVal;
    }
}
