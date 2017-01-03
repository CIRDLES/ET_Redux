/*
 * MineralStandardUPbRatiosEnum.java
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

public enum MineralStandardUPbRatiosEnum {
    /**
     * 
     */// MineralStandard ratios
    /**
     * 
     */
    r206_207r( "r206_207r" ),
    /**
     * 
     */
    r206_208r( "r206_208r" ),
    /**
     * 
     */
    r206_238r( "r206_238r" ),
    /**
     * 
     */
    r208_232r( "r208_232r" ),
    /**
     * 
     */
    r238_235s( "r238_235s" );
    private String name;

    private MineralStandardUPbRatiosEnum ( String name ) {
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
        String[] retVal = new String[MineralStandardUPbRatiosEnum.values().length];
        for (int i = 0; i < MineralStandardUPbRatiosEnum.values().length; i ++) {
            retVal[i] = MineralStandardUPbRatiosEnum.values()[i].getName();
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
            MineralStandardUPbRatiosEnum.valueOf( checkString );
        } catch (IllegalArgumentException e) {
            retVal = false;
        }

        return retVal;
    }
}
