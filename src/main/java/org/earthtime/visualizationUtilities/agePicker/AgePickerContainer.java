/*
 * AgePickerContainer.java
 *
 * Created Oct 26, 2011
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
package org.earthtime.visualizationUtilities.agePicker;

import javax.swing.JLayeredPane;
import javax.swing.JPanel;

/**
 *
 * @author James F. Bowring
 */
public class AgePickerContainer extends AbstractAgeWidget {

    /**
     * 
     * @param leftX
     * @param topY
     * @param boxWidth
     * @param boxHeight
     */
    public AgePickerContainer ( int leftX, int topY, int boxWidth, int boxHeight ) {
        super( leftX, topY, boxWidth, boxHeight );

        JLayeredPane ageExtents = new AgeExtents( 150, 50, 750, 50, 0.0, 4500.0 );
        this.add( ageExtents, javax.swing.JLayeredPane.DRAG_LAYER );
        
        //JLayeredPane leftAgeSlider = new AgeValueSlider( 125, 25, 50, 100, 875, 0, 4500);
        this.add( ((AgeExtents)ageExtents).getLeftAgeSlider(), javax.swing.JLayeredPane.DEFAULT_LAYER );
    }
}
