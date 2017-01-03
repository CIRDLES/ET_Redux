/*
 * TraceElements.java
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

import java.util.Arrays;

/**
 *
 * @author James F. Bowring
 */
/*
 *
 *
 * Created Feb 2011 as part of major refactoring to control magic strings
 */
public enum TraceElements {

    // tracer ratios
    /**
     *
     */
    Si("Si", false),
    /**
     *
     */
    P("P", false),
    /**
     *
     */
    Ca("Ca", false),
    /**
     *
     */
    Ti("Ti", false),
    /**
     *
     */
    Rb("Rb", false),
    /**
     *
     */
    Sr("Sr", false),
    /**
     *
     */
    Y("Y", false),
    /**
     *
     */
    Zr("Zr", false),
    /**
     *
     */
    La("La", true),
    /**
     *
     */
    Ce("Ce", true),
    /**
     *
     */
    Pr("Pr", true),
    /**
     *
     */
    Nd("Nd", true),
    /**
     *
     */
    Sm("Sm", true),
    /**
     *
     */
    Eu("Eu", true),
    /**
     *
     */
    Gd("Gd", true),
    /**
     *
     */
    Tb("Tb", true),
    /**
     *
     */
    Dy("Dy", true),
    /**
     *
     */
    Ho("Ho", true),
    /**
     *
     */
    Er("Er", true),
    /**
     *
     */
    Tm("Tm", true),
    /**
     *
     */
    Yb("Yb", true),
    /**
     *
     */
    Lu("Lu", true),
    /**
     *
     */
    Hf("Hf", false);

    private String name;

    boolean rareEarthElement;

    private TraceElements(String name, boolean rareEarthElement) {
        this.name = name;
        this.rareEarthElement = rareEarthElement;
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
        String[] retVal = new String[TraceElements.values().length];
        for (int i = 0; i < TraceElements.values().length; i++) {
            retVal[i] = TraceElements.values()[i].getName();
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
            TraceElements.valueOf(checkString);
        } catch (IllegalArgumentException e) {
            retVal = false;
        }

        return retVal;
    }

    //*************************
    /**
     *
     * @return
     */
    public static String[] getNamesSorted() {
        String[] retVal = new String[TraceElements.values().length];
        for (int i = 0; i < TraceElements.values().length; i++) {
            retVal[i] = TraceElements.values()[i].getName();
        }
        Arrays.sort(retVal);
        return retVal;
    }
    //*************************

    /**
     * @return the rareEarthElement
     */
    public boolean isRareEarthElement() {
        return rareEarthElement;
    }
}
