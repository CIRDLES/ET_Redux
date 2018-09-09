/*
 * LineFitFunctionView.java
 *
 * Created Aug 3, 2011
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
package org.earthtime.Tripoli.dataViews.fitFunctionViews;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.JRadioButton;
import org.earthtime.Tripoli.fitFunctions.AbstractFunctionOfX;
import org.earthtime.UPb_Redux.beans.ValueModelValueSlider;

/**
 *
 * @author James F. Bowring
 */
public class LineFitFunctionView extends AbstractFitFunctionView {

    /**
     *
     * @param lineFitFofX
     * @param parameterAValueSlider
     * @param parameterBValueSlider
     * @param functionChoiceRadioButton
     * @param bounds
     */
    public LineFitFunctionView (//
            AbstractFunctionOfX lineFitFofX,//
            ValueModelValueSlider parameterAValueSlider,//
            ValueModelValueSlider parameterBValueSlider,//
            JRadioButton functionChoiceRadioButton,//
            Rectangle bounds ) {

        super( lineFitFofX, functionChoiceRadioButton, bounds );

        this.parameterAValueSlider = parameterAValueSlider;
        this.parameterBValueSlider = parameterBValueSlider;

    }

    /**
     *
     */
    @Override
    public void resetValueModelSliders () {
        parameterAValueSlider.resetSliderBox();
        parameterBValueSlider.resetSliderBox();
    }

    /**
     *
     * @param g2d
     */
    @Override
    public void paint ( Graphics2D g2d ) {
        super.paint( g2d );

    }

    /**
     *
     */
    @Override
    public void preparePanel () {
        super.preparePanel();


    }
}
