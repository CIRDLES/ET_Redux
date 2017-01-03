/*
 * MineralStandardUPbConcentrationsPPMEnum.java
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

public enum MineralStandardUPbConcentrationsPPMEnum {
    /**
     * MineralStandard concentrations
     *//**
     * MineralStandard concentrations
     */

    concU238ppm("concU"),
    concTh232ppm("concTh"),
    rTh_Usample("rTh_Usample");
    private String name;

    private MineralStandardUPbConcentrationsPPMEnum ( String name ) {
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
        String[] retVal = new String[MineralStandardUPbConcentrationsPPMEnum.values().length];
        for (int i = 0; i < MineralStandardUPbConcentrationsPPMEnum.values().length; i ++) {
            retVal[i] = MineralStandardUPbConcentrationsPPMEnum.values()[i].getName();
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
            MineralStandardUPbConcentrationsPPMEnum.valueOf( checkString );
        } catch (IllegalArgumentException e) {
            retVal = false;
        }

        return retVal;
    }
}
