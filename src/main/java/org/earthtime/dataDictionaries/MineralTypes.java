/*
 * MineralTypes.java
 *
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

import java.io.Serializable;

/**
 *
 * @author James F. Bowring
 */
public enum MineralTypes implements Serializable//
{

    /**
     * 
     */
    OTHER( "other" ),
    /**
     * 
     */
    APATITE( "apatite" ),
    /**
     * 
     */
    CALCITE( "calcite" ),
    /**
     * 
     */
    MONAZITE( "monazite" ),
    /**
     * 
     */
    RUTILE( "rutile" ),
    /**
     * 
     */
    TITANITE( "titanite" ),
    /**
     * 
     */
    WHOLE_ROCK( "whole rock" ),
    /**
     * 
     */
    XENOTIME( "xenotime" ),
    /**
     * 
     */
    ZIRCON( "zircon" );

    private String name;

    private MineralTypes ( String name ) {
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
     * @param name
     * @return
     */
    public static String validateStandardMineralTypeName(String name){
        String retVal = "other";

        for (MineralTypes smt : MineralTypes.values()){
            if (smt.getName().equalsIgnoreCase( name)){
                retVal = name;
            }
        }

        return retVal;
    }
    
    /**
     * 
     * @return
     */
    public static String[] getNames () {
        String[] retVal = new String[MineralTypes.values().length];
        for (int i = 0; i < MineralTypes.values().length; i ++) {
            retVal[i] = MineralTypes.values()[i].getName();
        }
        return retVal;
    }
}


