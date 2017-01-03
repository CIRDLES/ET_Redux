/*
 * ValueModelClump.java
 *
 * Created on February 6 2009
 *
 *
 * Copyright 2006-2017 James F. Bowring and www.Earth-Time.org
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
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.dataDictionaries.DataDictionary;
import org.earthtime.fractions.ETFractionInterface;

/**
 * This panel contains value model sliders according to defined clumps from
 * dataDictionary.  The panel presents the siders in the specified order
 * if they are provided by name (names not provided, but in the clump do not render)
 * @author James F. Bowring
 */
public class ValueModelClump extends JLayeredPane
        implements
        MouseListener,
        MouseMotionListener,
        Serializable {

    // class variables
    private final static int valueSliderWidth = 115;
    private final static int uncertSliderWidth = 95;
    private final static int sliderHeight = 18;//5;
    private final static int sliderVerticalGap = 1;
    private final static int clumpWidth = 325;
    // instance variables
//    private String[][] clumpSpecs;
    private JPanel[] uncertaintySliders;

    /**
     * 
     * @param x
     * @param y
     * @param clumpSpecs
     * @param validityMessage
     * @param covaryingTerms
     * @param fraction
     * @param kwikiValueChangeListener
     */
    public ValueModelClump (
            int x,
            int y,
            String[][] clumpSpecs,
            String validityMessage,
            String[][] covaryingTerms,
            ETFractionInterface fraction,
            PropertyChangeListener kwikiValueChangeListener ) {

//        this.clumpSpecs = clumpSpecs;

        setOpaque( true );

        // used for displaying bad correlation coefficients among inputs
        if ( validityMessage.length() > 0 ) {
            setBackground( Color.pink );
            setToolTipText( "<html>" + validityMessage + "</html>" );
        } else {
            setBackground( Color.white );
        }

        // the size of this panel is determined by the number of sliders
        setBounds( x, y,//
                clumpWidth, //
                clumpSpecs.length * (sliderHeight + sliderVerticalGap) + 3 );

        int leftOffset = 110;
        int verticalOffset = sliderHeight + sliderVerticalGap;
        int rowNum = 0;

        uncertaintySliders = new ValueModelUncertSlider[clumpSpecs.length];

        for (int j = 0; j < clumpSpecs.length; j ++) {
            ValueModel vm = null;
            // looks up the correct method and applies it to input
            Method meth = 
                    DataDictionary.retrieveMethodNameForInput( clumpSpecs[j][0] );
            if ( meth != null ) {
                try {
                    vm = (ValueModel) meth.//
                            invoke( fraction, new Object[]{clumpSpecs[j][0]} );
                } catch (IllegalAccessException | IllegalArgumentException illegalAccessException) {
                } catch (InvocationTargetException invocationTargetException) {
                    System.out.println( invocationTargetException.getMessage() + "  AT ValueModelClump constructor" );
                }

                if ( vm != null ) {

                    add( new ValueModelSliderLabel(//
                            0, rowNum * verticalOffset + 2, clumpSpecs[j][1] ),
                            javax.swing.JLayeredPane.DEFAULT_LAYER );

                    add( new ValueModelValueSlider(//
                            leftOffset, //
                            rowNum * verticalOffset + 2, //
                            valueSliderWidth, //
                            sliderHeight,//
                            vm,
                            clumpSpecs[j][2],
                            kwikiValueChangeListener ),
                            javax.swing.JLayeredPane.DEFAULT_LAYER );

                    uncertaintySliders[j] =//
                            new ValueModelUncertSlider(//
                            clumpSpecs[j][0],
                            covaryingTerms[j],
                            uncertaintySliders,
                            leftOffset + valueSliderWidth + 2,//
                            rowNum ++ * verticalOffset + 2,//
                            uncertSliderWidth, sliderHeight,//
                            vm,
                            clumpSpecs[j][2],
                            kwikiValueChangeListener );

                    add( uncertaintySliders[j],
                            javax.swing.JLayeredPane.DEFAULT_LAYER );
                }
            }
        }

    }

    /**
     * 
     */
    public void ResetSliders () {
        for (Component vms : getComponents()) {
            if ( vms instanceof ValueModelSliderI ) {
                ((ValueModelSliderI) vms).resetSliderBox();
            }
        }
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

        RenderingHints rh = g2d.getRenderingHints();
        rh.put( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        rh.put( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
        g2d.setRenderingHints( rh );

        g2d.setColor( Color.black );
        DrawBounds( g2d );

        //  paint valuesliders
        for (Component vms : getComponents()) {
            if ( vms instanceof JPanel ) {
                vms.repaint();
            }
        }

    }

    private void DrawBounds ( Graphics2D g2d ) {

        g2d.drawRect( 0, 0, getWidth() - 1, getHeight() - 1 );

    }

    /**
     * 
     * @param e
     */
    public void mouseClicked ( MouseEvent e ) {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    /**
     * 
     * @param e
     */
    public void mousePressed ( MouseEvent e ) {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    /**
     * 
     * @param e
     */
    public void mouseReleased ( MouseEvent e ) {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    /**
     * 
     * @param e
     */
    public void mouseEntered ( MouseEvent e ) {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    /**
     * 
     * @param e
     */
    public void mouseExited ( MouseEvent e ) {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    /**
     * 
     * @param e
     */
    public void mouseDragged ( MouseEvent e ) {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    /**
     * 
     * @param e
     */
    public void mouseMoved ( MouseEvent e ) {
        throw new UnsupportedOperationException( "Not supported yet." );
    }

    /**
     * @return the uncertaintySliders
     */
    public JPanel[] getUncertaintySliders () {
        return uncertaintySliders;
    }

    /**
     * @param uncertaintySliders the uncertaintySliders to set
     */
    public void setUncertaintySliders ( JPanel[] uncertaintySliders ) {
        this.uncertaintySliders = uncertaintySliders;
    }
}
