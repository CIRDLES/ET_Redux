/*
 * KwikiDateModesSelectorPanel.java
 *
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
package org.earthtime.UPb_Redux.dateInterpretation.kwiki;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLayeredPane;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author James F. Bowring
 */
public class KwikiDateModesSelectorPanel extends JLayeredPane {

    /**
     * 
     */
    public static final String UNCERTAINTY_MODE_PROPERTY = "uncertaintyModeForDates";
    /**
     * 
     */
    public static final String DATE_CORRECTION_PROPERTY = "correctionModeForDates";
    /**
     * 
     */
    public static final String SLIDER_LOCK_PROPERTY = "sliderLock";
    private static String dateCorrection = "None";

    /**
     * @param aDateCorrection the dateCorrection to set
     */
    public static void setDateCorrection ( String aDateCorrection ) {
        dateCorrection = aDateCorrection;
    }
    private PropertyChangeSupport propertySupport;
    private JCheckBox correctTh;
    private JCheckBox correctPa;
    private JCheckBox sliderLockCheckBox;
    private ButtonGroup uncertaintyModeGroup;

    /** Creates a new instance of KwikiDateModesSelectorPanel
     * @param x 
     * @param y 
     * @param enableThPa 
     */
    public KwikiDateModesSelectorPanel (
            int x,
            int y,
            boolean enableThPa/*,
            PropertyChangeListener kwikiDateModesSelectorListener */ ) {

        super();

        setOpaque( true );

        setBackground( Color.white );

        setBounds( x, y, 178, 80 );

        propertySupport = new PropertyChangeSupport( this );
        //this.addPropertyChangeListener( kwikiDateModesSelectorListener );

        SetupDateCorrectionCheckBoxes( enableThPa );
        SetupUncertaintyModeButtonGroup();
        SetupSliderLockCheckBox();

    }

    /**
     * 
     * @param enableThPa
     */
    public void SetupDateCorrectionCheckBoxes ( boolean enableThPa ) {

        ActionListener dateCorrectionChkBoxActionListener = new ActionListener() {

            public void actionPerformed ( ActionEvent e ) {
                AbstractButton abstractButton =
                        (AbstractButton) e.getSource();
                String choice = abstractButton.getActionCommand();
                CalculateDateCorrectionMode( choice );
            }
        };

        correctTh = new JCheckBox( "Th" );
        correctTh.setActionCommand( "Th" );
        correctTh.setOpaque( false );
        correctTh.setForeground( new java.awt.Color( 204, 0, 0 ) );
        correctTh.setFont( new Font( "SansSerif", Font.BOLD, 9 ) );
        correctTh.setBounds( 125, 20, 45, 15 );
        correctTh.setEnabled( enableThPa );
        correctTh.addActionListener( dateCorrectionChkBoxActionListener );
        add( correctTh, javax.swing.JLayeredPane.DEFAULT_LAYER );

        correctPa = new JCheckBox( "Pa" );
        correctPa.setActionCommand( "Pa" );
        correctPa.setOpaque( false );
        correctPa.setForeground( new java.awt.Color( 204, 0, 0 ) );
        correctPa.setFont( new Font( "SansSerif", Font.BOLD, 9 ) );
        correctPa.setBounds( 125, 40, 45, 15 );
        correctPa.setEnabled( enableThPa );
        correctPa.addActionListener( dateCorrectionChkBoxActionListener );
        add( correctPa, javax.swing.JLayeredPane.DEFAULT_LAYER );
    }

    private void SetupSliderLockCheckBox () {

        ChangeListener sliderLockChangeListener = new ChangeListener() {

            public void stateChanged ( ChangeEvent changeEvent ) {
                propertySupport.firePropertyChange(//
                        SLIDER_LOCK_PROPERTY,
                        "",
                        String.valueOf( ((JCheckBox) changeEvent.getSource()).isSelected() ) );
            }
        };

        sliderLockCheckBox = new JCheckBox( "Lock Uncertainties" );
        sliderLockCheckBox.setOpaque( false );
        sliderLockCheckBox.setForeground( new java.awt.Color( 204, 0, 0 ) );
        sliderLockCheckBox.setFont( new Font( "SansSerif", Font.BOLD, 9 ) );
        sliderLockCheckBox.setBounds( 1, 60, 120, 15 );
        sliderLockCheckBox.addChangeListener( sliderLockChangeListener );
        add( sliderLockCheckBox, javax.swing.JLayeredPane.DEFAULT_LAYER );

    }

    private void SetupUncertaintyModeButtonGroup () {
        uncertaintyModeGroup = new ButtonGroup();

        ActionListener uncertaintyModeActionListener = new ActionListener() {

            public void actionPerformed ( ActionEvent e ) {
                propertySupport.firePropertyChange(//
                        UNCERTAINTY_MODE_PROPERTY,
                        "",
                        ((JRadioButton) e.getSource()).getActionCommand() );
            }
        };


        JRadioButton analyticalRB = new JRadioButton( "Analytical", true );
        analyticalRB.setActionCommand( "Analytical" );
        uncertaintyModeGroup.add( analyticalRB );
        analyticalRB.setOpaque( false );
        analyticalRB.setForeground( new java.awt.Color( 204, 0, 0 ) );
        analyticalRB.setFont( new Font( "SansSerif", Font.BOLD, 9 ) );
        analyticalRB.setBounds( 1, 2, 130, 16 );
        analyticalRB.addActionListener( uncertaintyModeActionListener );
        add( analyticalRB, javax.swing.JLayeredPane.DEFAULT_LAYER );

        JRadioButton tracerRB = new JRadioButton( "+ Tracer", false );
        tracerRB.setActionCommand( "Tracer" );
        uncertaintyModeGroup.add( tracerRB );
        tracerRB.setOpaque( false );
        tracerRB.setForeground( new java.awt.Color( 204, 0, 0 ) );
        tracerRB.setFont( new Font( "SansSerif", Font.BOLD, 9 ) );
        tracerRB.setBounds( 1, 21, 130, 16 );
        tracerRB.addActionListener( uncertaintyModeActionListener );
        add( tracerRB, javax.swing.JLayeredPane.DEFAULT_LAYER );

        JRadioButton lambdaRB = new JRadioButton( "+ decay constants", false );
        lambdaRB.setActionCommand( "Lambda" );
        uncertaintyModeGroup.add( lambdaRB );
        lambdaRB.setOpaque( false );
        lambdaRB.setForeground( new java.awt.Color( 204, 0, 0 ) );
        lambdaRB.setFont( new Font( "SansSerif", Font.BOLD, 9 ) );
        lambdaRB.setBounds( 1, 40, 130, 16 );
        lambdaRB.addActionListener( uncertaintyModeActionListener );
        add( lambdaRB, javax.swing.JLayeredPane.DEFAULT_LAYER );

    }

    /**
     * 
     * @param choice
     */
    public void CalculateDateCorrectionMode ( String choice ) {
        String oldValue = dateCorrection;
        if ( dateCorrection.equalsIgnoreCase( "None" ) ) {
            dateCorrection = "";
        }

        if ( choice.equalsIgnoreCase( "Th" ) ) {
            if ( dateCorrection.contains( "Th" ) ) {
                dateCorrection = dateCorrection.replace( "Th", "" );
            } else {
                dateCorrection = "Th" + dateCorrection;
                // correctTh.setSelected( true );
            }
        }


        if ( choice.equalsIgnoreCase( "Pa" ) ) {
            if ( dateCorrection.contains( "Pa" ) ) {
                dateCorrection = dateCorrection.replace( "Pa", "" );
            } else {
                dateCorrection = dateCorrection + "Pa";
            }
        }

        if ( dateCorrection.equals( "" ) ) {
            dateCorrection = "None";
        }

        propertySupport.firePropertyChange( DATE_CORRECTION_PROPERTY, oldValue, dateCorrection );
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
        g2d.setColor( Color.red );

        DrawBounds( g2d );

        // vertical divider
        g2d.drawLine( 120, 0, 120, getHeight() );
        // horizontal divider
        g2d.drawLine( 0, 57, 120, 57 );

    }

    private void DrawBounds ( Graphics2D g2d ) {

        g2d.drawRect( 0, 0, getWidth() - 1, getHeight() - 1 );

    }

    /**
     * 
     * @param listener
     */
    @Override
    public void addPropertyChangeListener ( PropertyChangeListener listener ) {
        propertySupport.addPropertyChangeListener( listener );
    }

//    @Override
//    public void removePropertyChangeListener(PropertyChangeListener listener) {
//        propertySupport.removePropertyChangeListener(listener);
//    }
    /**
     * 
     * @param selected
     */
    public void setCorrectThSelected ( boolean selected ) {
        correctTh.setSelected( selected );
        //correctTh.doClick();
    }

    /**
     * 
     * @param selected
     */
    public void setCorrectPaSelected ( boolean selected ) {
        correctPa.setSelected( selected );
        //correctPa.repaint();
    }
}
