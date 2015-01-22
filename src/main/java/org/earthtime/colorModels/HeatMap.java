/*
 * 
 * HeatMap.java
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

package org.earthtime.colorModels;

import java.awt.Color;
import java.util.ArrayList;

/**
 *
 * @author James F. Bowring
 */
public class HeatMap {

    // http://dba.med.sc.edu/price/irf/Adobe_tg/models/hsb.html
    private final static ArrayList<Integer> rgb;
    private final static float blueHSB = 250f;
    private final static float redHSB = 342f;

    static {
        rgb = new ArrayList<>();

        // create colors from HSB redHSB to blueHSB as the HeatMap in reverse order so blue = cold = left
        for (float i = ( blueHSB + 360f - redHSB); i > 0 ; i = i - 0.25f) {
            rgb.add(Color.HSBtoRGB(((i + 342f) % 360f) / 360.0f, 0.9f, 1f));
        }
    }

    /**
     * @return the rgb
     */
    public static ArrayList<Integer> getRgb() {
        return rgb;
    }
    
    /**
     *
     * @param leftShift
     * @param rightShift
     * @param fractionalPart
     * @return
     */
    public static int selectColorInRange(int leftShift, int rightShift, double fractionalPart){
        // leftShift and rightShift constrain the ends of the HeatMap as index counts
        // then using the remaining HeatMap, the color is selected at the index closest
        // to the fractionalPart (between 0 and 1 inclusive) of the remaining HeatMap from left to right
        
        int selectedIndex = 0;
        int indexRange = (rgb.size() - leftShift - rightShift - 1);
        
        if (indexRange > 0){
            selectedIndex = (int)(fractionalPart * indexRange) + leftShift;
        }
        
       // System.out.println("frac = " + fractionalPart + "  range = " + indexRange + "  index = " + selectedIndex);

//        return rgb.get(selectedIndex);
        return selectedIndex; // this preserves access to index
    }
}
