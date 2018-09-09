/*
 * ColorGradient.java
 *
 * Created Jul 26, 2011
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
package org.earthtime.visualizationUtilities;

import java.awt.Color;

/**
 *
 * @author James F. Bowring
 */
public class ColorGradient {

    // Noah in 4-Aug-2011 email: The RGB colormap I was using in MATLAB leaves R at 0 and runs G from 0 to 1 and B from 1 to 0.5.
    
    /**
     * 
     * @param color1
     * @param color2
     * @param countOfGrades
     * @return
     */
    public static Color[] generateGradient ( Color color1, Color color2, int countOfGrades ) {
        Color[] gradients = new Color[countOfGrades];

        int R1 = color1.getRed();
        int G1 = color1.getGreen();
        int B1 = color1.getBlue();

        int R2 = color2.getRed();
        int G2 = color2.getGreen();
        int B2 = color2.getBlue();

        for (double i = 0; i < countOfGrades; i ++) {
            int R = R1 + (int) Math.round( (i * (R2 - R1) / (double) countOfGrades) );
            int G = G1 + (int) Math.round( (i * (G2 - G1) / (double) countOfGrades) );
            int B = B1 + (int) Math.round( (i * (B2 - B1) / (double) countOfGrades) / 2.0);

            gradients[(int)i] = new Color( 0, G, B );
        }

        return gradients;
    }
}
