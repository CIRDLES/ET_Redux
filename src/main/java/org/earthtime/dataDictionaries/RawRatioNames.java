/*
 * RawRatioNames.java
 *
 *
 * Copyright 2006-2017James F. Bowring and www.Earth-Time.org
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
public enum RawRatioNames {

    // raw ratios
    /**
     *
     */
    r206_207w("r206_207w", "206 / 207"),
    r207_206w("r207_206w", "207 / 206"),
    r208_206w("r208_206w", "208 / 206"),
    /**
     *
     */
    r206_238w("r206_238w", "206 / 238"),
    /**
     *
     */
    r208_232w("r208_232w", "208 / 232"),
    /**
     *
     */
    r206_204w("r206_204w", "206 / 204"),
    r204_206w("r204_206w", "204 / 206"),
    /**
     *
     */
    r207_204w("r207_204w", "207 / 204"),
    /**
     *
     */
    r208_204w("r208_204w", "208 / 204"),
    /**
     *
     */
    r207_235w("r207_235w", "207 / 235"),
    // special case
    /**
     *
     */
    r176_176w("r176_176w", "176 / 176"),
    r235_235w("r235_235w", "235 / 235"),
    r196_196w("r196_196w", "196 / 196"),
    r238_196w("r238_196w", "238 / 196"),
    r248_248w("r248_248w", "248 / 248"),
    r254_254w("r254_254w", "254 / 254"),
    r254_238w("r254_238w", "254 / 238"),
    r248_254w("r248_254w", "248 / 254"),
    r270_270w("r270_270w", "270 / 270"),
    r206_270w("r206_270w", "206 / 270"),
    r270_254w("r270_254w", "270 / 254"),
    r206_254w("r206_254w", "206 / 254"),
    r202_202w("r202_202w", "202 / 202");
    private final String name;
    private final String displayName;

    private RawRatioNames(String name, String displayName) {
        this.name = name;
        this.displayName = displayName;
    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @return
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     *
     * @return
     */
    public static String[] getNames() {
        String[] retVal = new String[RawRatioNames.values().length];
        for (int i = 0; i < RawRatioNames.values().length; i++) {
            retVal[i] = RawRatioNames.values()[i].getName();
        }
        return retVal;
    }

    /**
     *
     * @param checkString
     * @return
     */
    public static boolean contains(String checkString) {
        boolean retVal = true;
        try {
            RawRatioNames.valueOf(checkString);
        } catch (IllegalArgumentException e) {
            retVal = false;
        }

        return retVal;
    }
}
