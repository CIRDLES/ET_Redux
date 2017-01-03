/*
 * TracerUPbTypesEnum.java
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author James F. Bowring
 */
/*
 *
 *
 */
public enum TracerUPbTypesEnum {

    // tracer ratios
    /**
     *
     */
    NONE( "NONE", false, new TracerUPbRatiosAndConcentrations[]{} ),
    /**
     *
     */
    mixed_205_235( "mixed 205-235", true, new TracerUPbRatiosAndConcentrations[]{//
TracerUPbRatiosAndConcentrations.r202_205t,
TracerUPbRatiosAndConcentrations.r233_235t,
TracerUPbRatiosAndConcentrations.r233_236t,
TracerUPbRatiosAndConcentrations.r238_233t,
TracerUPbRatiosAndConcentrations.concU236t} ),
    /**
     *
     */
    mixed_205_233_235( "mixed 205-233-235", true, new TracerUPbRatiosAndConcentrations[]{//
TracerUPbRatiosAndConcentrations.r202_205t, //
TracerUPbRatiosAndConcentrations.r233_236t, //
TracerUPbRatiosAndConcentrations.r238_233t,
TracerUPbRatiosAndConcentrations.concU236t} ),
    /**
     *
     */
    mixed_208_235( "mixed 208-235", false, new TracerUPbRatiosAndConcentrations[]{} ),
    /**
     *
     */
    mixed_205_233_236( "mixed 205-233-236", true, new TracerUPbRatiosAndConcentrations[]{//
TracerUPbRatiosAndConcentrations.r202_205t, //
TracerUPbRatiosAndConcentrations.r233_235t, //
TracerUPbRatiosAndConcentrations.r238_235t, //
TracerUPbRatiosAndConcentrations.concU235t} ),
    /**
     *
     */
    mixed_202_205_233_235( "mixed 202-205-233-235", true, new TracerUPbRatiosAndConcentrations[]{//
TracerUPbRatiosAndConcentrations.r233_236t,//
TracerUPbRatiosAndConcentrations.r238_233t,
TracerUPbRatiosAndConcentrations.concU236t} ),
    /**
     *
     */
    mixed_202_205_233_236( "mixed 202-205-233-236", true, new TracerUPbRatiosAndConcentrations[]{//
TracerUPbRatiosAndConcentrations.r233_235t, //
TracerUPbRatiosAndConcentrations.r238_235t,
TracerUPbRatiosAndConcentrations.concU235t} ),
    /**
     *
     */
    mixed_205_233_235_230Th( "mixed 205-233-235-230Th", false, new TracerUPbRatiosAndConcentrations[]{} );
    

    private String name;
    private boolean supported;
    private TracerUPbRatiosAndConcentrations[] excludedRatios;

    private TracerUPbTypesEnum ( String name, boolean supported, TracerUPbRatiosAndConcentrations[] excludedRatios ) {
        this.name = name;
        this.supported = supported;
        this.excludedRatios = excludedRatios;

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
    public boolean isSupported () {
        return supported;
    }

    /**
     *
     * @param ratio
     * @return
     */
    public boolean excludesRatioOrConcentration ( TracerUPbRatiosAndConcentrations ratio ) {
        boolean retVal = false;
        for (int i = 0; i < excludedRatios.length; i ++) {
            if ( excludedRatios[i].equals( ratio ) ) {
                retVal = true;
                break;
            }
        }
        return retVal;
    }

    /**
     *
     * @return
     */
    public static String[] getNames () {
        String[] retVal = new String[TracerUPbTypesEnum.values().length];
        for (int i = 0; i < TracerUPbTypesEnum.values().length; i ++) {
            retVal[i] = TracerUPbTypesEnum.values()[i].getName();
        }
        return retVal;
    }

    /**
     *
     * @return
     */
    public static TracerUPbTypesEnum[] getSupportedTypes () {
        ArrayList<TracerUPbTypesEnum> retVal = new ArrayList<TracerUPbTypesEnum>();
        for (int i = 0; i < TracerUPbTypesEnum.values().length; i ++) {
            if ( TracerUPbTypesEnum.values()[i].isSupported() ) {
                retVal.add( TracerUPbTypesEnum.values()[i] );
            }
        }

        TracerUPbTypesEnum[] selectedTypes = retVal.toArray( new TracerUPbTypesEnum[retVal.size()] );

        return selectedTypes;
    }

    /**
     *
     * @param checkString
     * @return
     */
    public static boolean contains ( String checkString ) {
        boolean retVal = true;
        try {
            TracerUPbTypesEnum.valueOf( checkString );
        } catch (IllegalArgumentException e) {
            retVal = false;
        }

        return retVal;
    }
    
    /**
     *
     * @param name
     * @return
     */
    public static  TracerUPbTypesEnum valueFromName(String name){
        name = name.replaceAll( " ", "_");
        name = name.replaceAll( "-", "_");
        return TracerUPbTypesEnum.valueOf( name );
    }

    public String toString () {
        return this.getName();
    }
}
