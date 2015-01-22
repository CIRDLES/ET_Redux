/*
 * ValueModelSlider.java
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
package org.earthtime.UPb_Redux.beans;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import javax.swing.JPanel;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public final class ValueModelValueSlider extends JPanel
        implements
        MouseListener,
        MouseMotionListener,
        /*Serializable,*/
        ValueModelSliderI {

    /**
     *
     */
    public static final String VALUE_PROPERTY = "value";
    private BigDecimal valueProperty;
    private PropertyChangeSupport propertySupport;
    private final JPanel valueModelSliderBox;
    private final int leftX;
    private final int topY;
    private final int width;
    private int height;
    private String units;
    private ValueModel valueModel;
    private ValueModel valueModelSavedCopy;
    private BigDecimal currentValue;
    private int sigDigits;
    private BigDecimal pixelSlideRange;
    private int lastX;
    private int nextX;
    private boolean mouseInsideValueModelSliderBox;

    /**
     *
     * @param leftX
     * @param topY
     * @param width
     * @param height
     * @param valueModel
     * @param units
     * @param kwikiListener
     */
    @SuppressWarnings ("LeakingThisInConstructor")
    public ValueModelValueSlider (
            int leftX,
            int topY,
            int width,
            int height,
            ValueModel valueModel,
            String units,
            PropertyChangeListener kwikiListener ) {

        this.leftX = leftX;
        this.topY = topY;
        sigDigits = 4;
        this.width = width;
        this.height = height;

        this.valueModel = valueModel;
        valueModelSavedCopy = valueModel.copy();

        this.units = units;

        currentValue = valueModel.getValue();
        propertySupport = new PropertyChangeSupport( this );
        addPropertyChangeListener( kwikiListener );
        this.valueProperty = currentValue;// oct 2010 setValueProperty(currentValue);

        setOpaque( true );
        setBackground( Color.white );
        addMouseListener( this );
        addMouseMotionListener( this );
        setBounds( leftX, topY, width, height );

        valueModelSliderBox = //
                new ValueModelSliderBox(//
                width, height - 3, getCurrentValueShowValue() );

        lastX = 0;
        nextX = 0;

        BigDecimal fourSigma = new BigDecimal( "4.0" ).//
                multiply( valueModel.getOneSigmaAbs() );

        pixelSlideRange = //
                new BigDecimal( Math.abs( fourSigma.doubleValue() )/* added abs July 2011 for tripoli alphas*/ / ((width - valueModelSliderBox.getWidth()) / 2) );

    }

    private String getCurrentValueShowValue () {
        // System.out.println(currentValue);
//        return ValueModel.formatBigDecimalForPublicationSigDigMode(//
//                new BigDecimal( currentValue.doubleValue(), ReduxConstants.mathContextSlider ).//
//                movePointRight( ReduxConstants.getUnitConversionMoveCount( units ) ), sigDigits );
//     
        // Sept 2012 simplification
        String formattedString = "";
        
        if ( currentValue.movePointRight(ReduxConstants.getUnitConversionMoveCount( units )).abs().compareTo( new BigDecimal(10e3)) > 0){
             DecimalFormat formatterForBigNumbers = new DecimalFormat("0.00E00");
             formattedString = formatterForBigNumbers.format(currentValue.movePointRight(ReduxConstants.getUnitConversionMoveCount( units )).doubleValue());
        } else {
            formattedString = currentValue.movePointRight(ReduxConstants.getUnitConversionMoveCount( units ) ).setScale( sigDigits, RoundingMode.HALF_UP ).toEngineeringString();
        }
              
        return formattedString;
    }

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

        // show all values, even zero
        RenderingHints rh = g2d.getRenderingHints();

        rh.put( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        rh.put( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
        g2d.setRenderingHints( rh );

        g2d.setColor( Color.black );
        DrawBounds( g2d );
        g2d.setColor( Color.red );
        g2d.drawLine( (width / 2), 0, (width / 2), height );

        ((ValueModelSliderBox) valueModelSliderBox).//
                setValueString( getCurrentValueShowValue() );

        ((ValueModelSliderBox) valueModelSliderBox).paint( g2d );

    }

    private void DrawBounds ( Graphics2D g2d ) {

        g2d.drawRect( 0, 0, width - 1, height - 1 );

    }

    /**
     *
     * @param evt
     * @return
     */
    public boolean mouseInsideValueModelSliderBox ( MouseEvent evt ) {
        // this is not very sensitive
        if ( (evt.getX() >= valueModelSliderBox.getX())
                && (evt.getX() <= (valueModelSliderBox.getX() + valueModelSliderBox.getWidth())) ) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * @return
     */
    public BigDecimal getValueProperty () {
        return valueProperty;
    }

    /**
     *
     * @param value
     */
    public void setValueProperty ( BigDecimal value ) {
        BigDecimal oldValue = valueProperty;
        valueProperty = value;

        BigDecimal change;
        try {
            change =currentValue.//
                    subtract( valueModelSavedCopy.getValue() ).//
                    divide(valueModelSavedCopy.getOneSigmaAbs(), ReduxConstants.mathContext15 );
        } catch (Exception e) {
            change = BigDecimal.ZERO;
        }

        setToolTipText(//
                ValueModel.formatBigDecimalForPublicationSigDigMode(//
                currentValue, sigDigits ) //
                + " (current) = " //
                + ValueModel.formatBigDecimalForPublicationSigDigMode(//
                valueModelSavedCopy.getValue(), sigDigits ) //
                + " (orig) + " //
                + change.setScale( 1, RoundingMode.HALF_UP ).toPlainString()//
                + " sigma" );


        // change fraction's copy
        valueModel.setValue( value );
        propertySupport.firePropertyChange( VALUE_PROPERTY, oldValue, valueProperty );
    }

    /**
     *
     * @param listener
     */
    @Override
    public void addPropertyChangeListener ( PropertyChangeListener listener ) {
        propertySupport.addPropertyChangeListener( listener );
    }

    /**
     *
     * @param listener
     */
    @Override
    public void removePropertyChangeListener ( PropertyChangeListener listener ) {
        propertySupport.removePropertyChangeListener( listener );
    }

    /**
     * single click outside valuebox will reset valuebox to original value
     *
     * @param e
     */
    public void mouseClicked ( MouseEvent e ) {
        if (  ! mouseInsideValueModelSliderBox ) {
            resetSliderBox();
        }
    }

    /**
     *
     */
    @Override
    public void resetSliderBox () {
        currentValue = valueModelSavedCopy.getValue();
        setValueProperty( currentValue );

        ((ValueModelSliderBox) valueModelSliderBox).//
                centerSliderBox( ValueModel.//
                formatBigDecimalForPublicationSigDigMode( currentValue, sigDigits ) );
        repaint();

        valueModel.setValue( valueModelSavedCopy.getValue() );

    }

    /**
     *
     * @param e
     */
    public void mousePressed ( MouseEvent e ) {
        lastX = e.getX();
        mouseInsideValueModelSliderBox = mouseInsideValueModelSliderBox( e );
    }

    /**
     *
     * @param e
     */
    public void mouseReleased ( MouseEvent e ) {
        nextX = lastX;//e.getX();
        if ( mouseInsideValueModelSliderBox ) {
            // recalculate fraction - fire property
            setValueProperty( currentValue );
            mouseInsideValueModelSliderBox = false;
        }
    }

    /**
     *
     * @param e
     */
    public void mouseEntered ( MouseEvent e ) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     *
     * @param e
     */
    public void mouseExited ( MouseEvent e ) {
        nextX = lastX;//e.getX();
        if ( mouseInsideValueModelSliderBox ) {
            // recalculate fraction - fire property
            setValueProperty( currentValue );
            mouseInsideValueModelSliderBox = false;
        }
    }

    /**
     *
     * @param e
     */
    public void mouseDragged ( MouseEvent e ) {
        nextX = e.getX();
        //System.out.println("in at x = " + e.getX() + "  lastx = " + lastX + "  nextx = " + nextX);
        if ( mouseInsideValueModelSliderBox ) {
            // check for moving past limits of valueModelSlider
            int pos = valueModelSliderBox.getX() + (nextX - lastX);
            if ( (pos + valueModelSliderBox.getWidth()) <= (getWidth() + 1)
                    && (pos >= -1) ) {
                valueModelSliderBox.setLocation(//
                        pos,
                        valueModelSliderBox.getY() );

                currentValue = //
                        currentValue.//
                        add( pixelSlideRange.//
                        multiply( new BigDecimal( nextX - lastX ) ) );

                lastX = nextX;

                repaint();
            }
        }
    }

    /**
     *
     * @param e
     */
    public void mouseMoved ( MouseEvent e ) {
        // throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * @return the valueModelSavedCopy
     */
    public ValueModel getValueModelSavedCopy () {
        return valueModelSavedCopy;
    }
}
