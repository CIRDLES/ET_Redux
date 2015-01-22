/*
 * AbstractFitFunctionView.java
 *
 * Created Aug 3, 2011
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
package org.earthtime.Tripoli.dataViews.fitFunctionViews;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import javax.swing.JLayeredPane;
import javax.swing.JRadioButton;
import org.earthtime.Tripoli.fitFunctions.AbstractFunctionOfX;
import org.earthtime.UPb_Redux.beans.ValueModelValueSlider;

/**
 *
 * @author James F. Bowring
 */
public abstract class AbstractFitFunctionView extends JLayeredPane {

    /**
     *
     */
    protected final static int leftMargin = 5;
    /**
     *
     */
    protected AbstractFunctionOfX fittedFofX;
    /**
     *
     */
    protected ValueModelValueSlider parameterAValueSlider;
    /**
     *
     */
    protected ValueModelValueSlider parameterBValueSlider;
    /**
     *
     */
    protected ValueModelValueSlider parameterCValueSlider;
    private final JRadioButton functionChoiceRadioButton;
    private final Font textFont;

    /**
     *
     * @param fittedFofX
     * @param functionChoiceRadioButton
     * @param bounds
     */
    protected AbstractFitFunctionView (//
            AbstractFunctionOfX fittedFofX,//
            JRadioButton functionChoiceRadioButton,//
            Rectangle bounds ) {
        super();

        this.fittedFofX = fittedFofX;
        this.functionChoiceRadioButton = functionChoiceRadioButton;

        setBounds( bounds );

        setOpaque( true );
        setBackground( Color.white );
        textFont = new Font( "SansSerif", Font.PLAIN, 10 );

    }

    /**
     *
     */
    public abstract void resetValueModelSliders ();

    /**
     *
     * @param g
     */
    @Override
    protected void paintComponent ( Graphics g ) {
        super.paintComponent( g );

        paint( (Graphics2D) g );
    }

    /**
     *
     * @param g2d
     */
    public void paint ( Graphics2D g2d ) {

        int mswdTextOffset = 120;//155
        RenderingHints rh = g2d.getRenderingHints();
        rh.put( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        rh.put( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
        g2d.setRenderingHints( rh );

        g2d.setPaint( Color.black );
        g2d.setFont( textFont );

        // draw border
        g2d.drawRect( 0, 0, getWidth() - 1, getHeight() - 1 );

        if (( fittedFofX != null ) && fittedFofX.verifyPositiveVariances()) {
            g2d.drawString( fittedFofX.getDescription(), leftMargin, 30 );

            g2d.setPaint( Color.red );

            // jan 2013 update to show MSWD and BIC
            g2d.setPaint( Color.red );
            g2d.setFont( new Font( "SansSerif", Font.PLAIN, 9 ) );
            try {
                g2d.drawString( "MSWD"//
                        + "= " + new BigDecimal( fittedFofX.getMSWD() ).setScale( 1, RoundingMode.HALF_UP ).toPlainString()//
                        + " BIC= " + new BigDecimal( fittedFofX.getBIC() ).setScale( 1, RoundingMode.HALF_UP ).toPlainString()//
                        , getWidth() - mswdTextOffset, 14 );
            } catch (RuntimeException e) {
            }

            g2d.setFont( textFont );

            if (  ! (this instanceof SmoothingSplineFitFunctionView) ) {
                g2d.setPaint( Color.black );

                g2d.setFont( new Font( "Monospaced", Font.BOLD, 10 ) );

                g2d.drawString( "a =", leftMargin, 49 );
                g2d.drawString( "Failed fit.", leftMargin + 30, 49 );
                if ( parameterAValueSlider != null ) {
                    g2d.drawString( formatValueModelSliderTwoSigmaForDisplay( parameterAValueSlider, 2 ), 118, 49 );
                }

                if ( fittedFofX.getCountOfParameters() > 1 ) {
                    g2d.drawString( "b =", leftMargin, 69 );
                    g2d.drawString( "Failed fit.", leftMargin + 30, 69 );
                }
                if ( parameterBValueSlider != null ) {
                    g2d.drawString( formatValueModelSliderTwoSigmaForDisplay( parameterBValueSlider, 2 ), 118, 69 );
                }

                if ( fittedFofX.getCountOfParameters() > 2 ) {
                    g2d.drawString( "c =", leftMargin, 89 );
                    g2d.drawString( "Failed fit.", leftMargin + 30, 89 );
                }
                if ( parameterCValueSlider != null ) {
                    g2d.drawString( formatValueModelSliderTwoSigmaForDisplay( parameterCValueSlider, 2 ), 118, 89 );
                }
            }
        } else if (  ! (this instanceof SmoothingSplineFitFunctionView) ) {
            g2d.setPaint( Color.red );

            // jan 2013 update to show unavailable fit
            g2d.setPaint( Color.red );
            g2d.setFont( new Font( "SansSerif", Font.PLAIN, 9 ) );
            g2d.drawString( "Unable to Fit Function", getWidth() - mswdTextOffset, 14 );

            g2d.setPaint( Color.black );
        }
    }

    /**
     *
     * @param valueModelSlider
     * @param countOfSigDig
     * @return
     */
    protected String formatValueModelSliderTwoSigmaForDisplay ( ValueModelValueSlider valueModelSlider, int countOfSigDig ) {
        String formattedValue = "";

        if ( valueModelSlider != null ) {

            // modified to handle big numbers for splinefit functions sept 2012
            BigDecimal currentValue = valueModelSlider.getValueModelSavedCopy().getValue();
            if ( currentValue.abs().compareTo( new BigDecimal( 10e3 ) ) > 0 ) {
                DecimalFormat formatterForBigNumbers = new DecimalFormat( "0.00E00" );
                formattedValue = formatterForBigNumbers.format( currentValue.doubleValue() );

                String formattedUnct = formatterForBigNumbers.format( valueModelSlider.getValueModelSavedCopy().getOneSigmaAbs().doubleValue() * 2 );

                formattedValue += " \u00B1 " + formattedUnct + "(2\u03C3)";;
            } else {
                formattedValue =
                        valueModelSlider.getValueModelSavedCopy().formatValueAndTwoSigmaForPublicationSigDigMode( "ABS", 0, countOfSigDig ) + "(2\u03C3)";
            }

            //alignment
            if ( valueModelSlider.getValueModelSavedCopy().getValue().doubleValue() >= 0 ) {
                // add space for missing + sign
                formattedValue = " " + formattedValue;
            }

        }

        return formattedValue;
    }

    /**
     *
     */
    public void preparePanel () {

        add( functionChoiceRadioButton, javax.swing.JLayeredPane.DEFAULT_LAYER );

        if ( parameterAValueSlider != null ) {
            add( parameterAValueSlider, javax.swing.JLayeredPane.DEFAULT_LAYER );
        }

        if ( parameterBValueSlider != null && (fittedFofX.getCountOfParameters() > 1) ) {
            add( parameterBValueSlider, javax.swing.JLayeredPane.DEFAULT_LAYER );
        }

        if ( parameterCValueSlider != null && (fittedFofX.getCountOfParameters() > 2) ) {
            add( parameterCValueSlider, javax.swing.JLayeredPane.DEFAULT_LAYER );
        }
        
        if ((fittedFofX == null) || !fittedFofX.verifyPositiveVariances()){
            functionChoiceRadioButton.setEnabled(false);
        }

    }

    /**
     * @return the functionChoiceRadioButton
     */
    public JRadioButton getFunctionChoiceRadioButton () {
        return functionChoiceRadioButton;
    }
}
