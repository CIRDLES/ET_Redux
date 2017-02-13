/*
 * UThAnalysisMeasures.java
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
public enum UThAnalysisMeasures {

    // UThAnalysisMeasures
    /**
     *
     */
    ar230Th_238Ufc("ar230Th_238Ufc"),
    ar230Th_234Ufc("ar230Th_234Ufc"),
    /**
     *
     */
    ar226Ra_230Thfc("ar226Ra_230Thfc"),
    /**
     *
     */
    ar231Pa_235Ufc("ar231Pa_235Ufc"),
    ar230Th_232Thfc("ar230Th_232Thfc"),
    ar232Th_238Ufc("ar232Th_238Ufc"),
    ar238U_232Thfc("ar238U_232Thfc"),
    ar234U_238Ufc("ar234U_238Ufc");

    private String name;

    private UThAnalysisMeasures(String name) {
        this.name = name;
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
    public static String[] getNames() {
        String[] retVal = new String[UThAnalysisMeasures.values().length];
        for (int i = 0; i < UThAnalysisMeasures.values().length; i++) {
            retVal[i] = UThAnalysisMeasures.values()[i].getName();
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
            UThAnalysisMeasures.valueOf(checkString);
        } catch (IllegalArgumentException e) {
            retVal = false;
        }

        return retVal;
    }
}
