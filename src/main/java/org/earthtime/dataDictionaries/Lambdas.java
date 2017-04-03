/*
 * Lambdas.java
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
public enum Lambdas {

    // decay constants
    /**
     *
     */
    lambda226( "lambda226", "lambda 226", 0 ),
    lambda230( "lambda230", "lambda 230", 0 ),
    /**
     *
     */
    lambda231( "lambda231", "lambda 231", 1 ),
    /**
     *
     */
    lambda232( "lambda232", "lambda 232", 2 ),
    /**
     *
     */
    lambda234( "lambda234", "lambda 234", 3 ),
    /**
     *
     */
    lambda235( "lambda235", "lambda 235", 4 ),
    /**
     *
     */
    lambda238( "lambda238", "lambda 238", 5 );
    private String name;
    private String displayName;
    private int index;

    private Lambdas ( String name, String displayName, int index ) {
        this.name = name;
        this.displayName = displayName;
        this.index = index;
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
    public int getIndex () {
        return index;
    }

    /**
     *
     * @return
     */
    public static String[] getNames () {
        String[] retVal = new String[Lambdas.values().length];
        for (int i = 0; i < Lambdas.values().length; i ++) {
            retVal[i] = Lambdas.values()[i].getName();
        }
        return retVal;
    }

    /**
     *
     * @return
     */
    public String getDisplayName () {
        return displayName;
    }

    /**
     *
     * @param checkString
     * @return
     */
    public static boolean contains ( String checkString ) {
        boolean retVal = true;
        try {
            Lambdas.valueOf( checkString );
        } catch (IllegalArgumentException e) {
            retVal = false;
        }

        return retVal;
    }
}
