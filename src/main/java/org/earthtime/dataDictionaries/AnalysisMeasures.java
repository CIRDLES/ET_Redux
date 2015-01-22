/*
 * AnalysisMeasures.java
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
public enum AnalysisMeasures {

    // tracer ratios
    /**
     * 
     */
    fractionMass( "fractionMass" ),
    /**
     * 
     */
    tracerMassInGrams( "tracerMassInGrams" ),
    /**
     * 
     */
    uBlankMassInGrams( "uBlankMassInGrams" ),
    /**
     * 
     */
    pbBlankMassInGrams( "pbBlankMassInGrams" ),
    /**
     * 
     */
    alphaPb( "alphaPb" ),
    /**
     * 
     */
    alphaU( "alphaU" ),
    /**
     * 
     */
    r238_235b( "r238_235b" ),
    /**
     * 
     */
    r238_235s( "r238_235s" ),
    /**
     * 
     */
    r18O_16O( "r18O_16O" ),
    /**
     * 
     */
    r18O_16O_revised( "r18O_16O_revised" ),
    /**
     * 
     */
    rTh_Umagma( "rTh_Umagma" ),
    /**
     * 
     */
    ar231_235sample( "ar231_235sample" ),
    /**
     * 
     */
    r270_267m( "r270_267m" ),
    /**
     * 
     */
    r265_267m( "r265_267m" ),
    /**
     * 
     */
    r270_265m( "r270_265m" ),
    /**
     * 
     */
    r270_268m( "r270_268m" ),
    /**
     * 
     */
    r265_268m( "r265_268m" );

    private String name;

    private AnalysisMeasures ( String name ) {
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
        String[] retVal = new String[AnalysisMeasures.values().length];
        for (int i = 0; i < AnalysisMeasures.values().length; i ++) {
            retVal[i] = AnalysisMeasures.values()[i].getName();
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
            AnalysisMeasures.valueOf( checkString );
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
    public static String[] getNamesSorted () {
        String[] retVal = new String[AnalysisMeasures.values().length];
        for (int i = 0; i < AnalysisMeasures.values().length; i ++) {
            retVal[i] = AnalysisMeasures.values()[i].getName();
        }
        Arrays.sort(retVal);
        return retVal;
    }
    //*************************
}
