/*
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
package org.earthtime.visualizationUtilities.agePicker;

import java.util.ArrayList;

/**
 *
 * @author bowring
 */
public final class GeoAges {
    
    /**
     * 
     */
    public static final ArrayList<Double> level1 = new ArrayList<Double>();
    /**
     * 
     */
    public static final String[] level1Names;
    
    /**
     * 
     */
    public static final ArrayList<Double> level2 = new ArrayList<Double>();
    /**
     * 
     */
    public static final String[] level2Names;
   
    static{  
        level1.add( 65.5);
        level1.add(251.0);
        level1.add(542.0);
        level1.add( 3850.0);
        level1.add( 4500.0);
        
        level1Names = new String[]{"Cenozoic", "Mesozoic", "Paleozoic", "PreCambrian", "Hadean"};
        
        level2.add(2.6);
        level2.add(65.5);
        
        level2Names = new String[]{"Quarternary", "Tertiary"};
    }
    
}
