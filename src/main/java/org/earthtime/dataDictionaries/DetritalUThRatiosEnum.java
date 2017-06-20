/*
 * DetritalUThRatiosEnum.java
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

public enum DetritalUThRatiosEnum {
    /**
     * 
     */// DetritalUraniumAndThorium ratios
    /**
     * 
     */
    ar230Th_238U( "ar230Th_238U" ),
    /**
     * 
     */
    ar232Th_238U( "ar232Th_238U" ),
    /**
     * 
     */
    ar234U_238U( "ar234U_238U" );
    private String name;

    private DetritalUThRatiosEnum ( String name ) {
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
        String[] retVal = new String[DetritalUThRatiosEnum.values().length];
        for (int i = 0; i < DetritalUThRatiosEnum.values().length; i ++) {
            retVal[i] = DetritalUThRatiosEnum.values()[i].getName();
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
            DetritalUThRatiosEnum.valueOf( checkString );
        } catch (IllegalArgumentException e) {
            retVal = false;
        }

        return retVal;
    }
}
