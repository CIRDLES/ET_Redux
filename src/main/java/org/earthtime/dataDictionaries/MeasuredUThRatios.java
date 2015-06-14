/*
 * MeasuredUThRatios.java
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
package org.earthtime.dataDictionaries;

/**
 *
 * @author James F. Bowring
 */

public enum MeasuredUThRatios {

    // measured UTh ratios
    /**
     * 
     */
    r234U_238U( "r234U_238U" ),
    /**
     * 
     */
    r230Th_232Th( "r230Th_232Th" ),
    /**
     * 
     */
    r228Ra_226Ra( "r228Ra_226Ra" ),
    /**
     * 
     */
    r231Pa_233Pa( "r231Pa_233Pa" ),    
    /**
     * 
     */
    r238U_206Pb( "r238U_206Pb" ),
    /**
     * 
     */
    r207Pb_206Pb( "r207Pb_206Pb" );


    private String name;

    private MeasuredUThRatios ( String name ) {
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
        String[] retVal = new String[MeasuredUThRatios.values().length];
        for (int i = 0; i < MeasuredUThRatios.values().length; i ++) {
            retVal[i] = MeasuredUThRatios.values()[i].getName();
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
            MeasuredUThRatios.valueOf( checkString );
        } catch (IllegalArgumentException e) {
            retVal = false;
        }

        return retVal;
    }
}
