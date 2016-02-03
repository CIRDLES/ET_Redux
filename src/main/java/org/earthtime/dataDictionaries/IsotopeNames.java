/*
 * IsotopeNames.java
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
public enum IsotopeNames {

    // IsotopeNames
    /**
     * 
     */
    U238( "U238"),
    /**
     * 
     */
    U235("U235"),
    /**
     * 
     */
    Th232( "Th232"),
    /**
     * 
     */
    Pb206( "Pb206"),
    /**
     * 
     */
    Pb208( "Pb208"),
    /**
     * 
     */
    Pb207( "Pb207"),
    /**
     * 
     */
    Pb204( "Pb204"),
    /**
     * 
     */
    Hg202( "Hg202"),
    Hf176( "Hf176");

    private String name;

    private IsotopeNames ( String name ) {
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
