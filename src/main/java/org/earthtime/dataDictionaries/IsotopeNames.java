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
    UO270("UO270", 270),
    UO254("UO254", 254),
    ThO248("ThO248", 248),
    /**
     * 
     */
    U238( "U238", 238),
    /**
     * 
     */
    U235("U235", 235),
    /**
     * 
     */
    Th232( "Th232", 232),
    /**
     * 
     */
    Pb206( "Pb206", 206),
    /**
     * 
     */
    Pb208( "Pb208", 208),
    /**
     * 
     */
    Pb207( "Pb207", 207),
    /**
     * 
     */
    Pb204( "Pb204", 204),
    /**
     * 
     */
    Hg202( "Hg202", 202),
    Zr2O196("Zr2O196", 196),
    Hf176( "Hf176", 176),
    BKGND("BKGND", 0);

    private String name;
    private int atomicMass;

    private IsotopeNames ( String name, int atomicMass ) {
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
     * @return the atomicMass
     */
    public int getAtomicMass() {
        return atomicMass;
    }

  
}
