/*
 * RawRatioNamesSHRIMP.java
 *
 *
 * Copyright 2006-2016 James F. Bowring and www.Earth-Time.org
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
package org.earthtime.Tripoli.rawDataFiles.handlers.shrimp;

/**
 *
 * @author James F. Bowring
 */
public enum RawRatioNamesSHRIMP {

    // raw ratios
    r204_206w("r204_206w", "204 / 206"),
    r207_206w("r207_206w", "207 / 206"),
    r208_206w("r208_206w", "208 / 206"),
    r238_196w("r238_196w", "238 / 196"),
    r206_238w("r206_238w", "206 / 238"),
    r254_238w("r254_238w", "254 / 238"),
    r248_254w("r248_254w", "248 / 254"),
    r206_270w("r206_270w", "206 / 270"),
    r270_254w("r270_254w", "270 / 254"),
    r206_254w("r206_254w", "206 / 254");
    
    private final String name;
    private final String displayName;

    private RawRatioNamesSHRIMP(String name, String displayName) {
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
        String[] retVal = new String[RawRatioNamesSHRIMP.values().length];
        for (int i = 0; i < RawRatioNamesSHRIMP.values().length; i++) {
            retVal[i] = RawRatioNamesSHRIMP.values()[i].getName();
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
            RawRatioNamesSHRIMP.valueOf(checkString);
        } catch (IllegalArgumentException e) {
            retVal = false;
        }

        return retVal;
    }
}
