/*
 * SampleTypesEnum.java
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

/**
 *
 * @author James F. Bowring
 */
/*
 *
 *
 * Created July 2011 as part of major refactoring to control magic strings
 */
public enum SampleTypesEnum {

    // Sample Types
    /**
     * 
     */
    ANALYSIS("ANALYSIS"),
    /**
     * 
     */
    LEGACY( "LEGACY" ),
 
    /**
     * 
     */
    LIVEWORKFLOW( "LIVEWORKFLOW" ),
 
    /**
     * 
     */
    NONE( "NONE" ),
 
    /**
     * 
     */
    SAMPLEFOLDER( "SAMPLEFOLDER" ),
    /**
     * 
     */
    COMPILATION( "COMPILATION" ),
    /**
     * PROJECT is the sample type for COMPILATION after Sept 2012.
     * Support for COMPILATION will continue due to legacy redux files.
     */
    PROJECT( "PROJECT" );
    private String name;

    private SampleTypesEnum ( String name ) {
        this.name = name;
    }

    /**
     * 
     * @return
     */
    public String getName () {
        return name;
    }
}
