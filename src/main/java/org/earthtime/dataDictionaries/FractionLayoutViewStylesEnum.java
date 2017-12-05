/*
 * FractionLayoutViewStylesEnum.java
 *
 *
 * Copyright 2006-2018 James F. Bowring, CIRDLES.org, and Earth-Time.org
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
public enum FractionLayoutViewStylesEnum {
    
    /**
     * 
     */
    GRID("GRID"),
    /**
     * 
     */
    GRID_INTERCEPT("GRID_INTERCEPT"),
    /**
     * 
     */
    GRAPH("GRAPH"),
    /**
     * 
     */
    OVERLAY("OVERLAY"),
    /**
     * 
     */
    SESSION("SESSION");
    
    private String name;
    
    private FractionLayoutViewStylesEnum(String name){
        this.name = name;        
    }

    /**
     * @return the name
     */
    public String getName () {
        return name;
    }
}
