/*
 * KwikiConcordiaToolBar.java
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
package org.earthtime.UPb_Redux.dateInterpretation.kwiki;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLayeredPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.ConcordiaGraphPanel;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.GraphPanelModeChangeI;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.PlottingDetailsDisplayInterface;
import org.earthtime.UPb_Redux.dateInterpretation.graphPersistence.GraphAxesSetup;

/**
 *
 * @author James F. Bowring
 */
public class KwikiConcordiaToolBar extends JLayeredPane implements GraphPanelModeChangeI {

    private final PropertyChangeSupport propertySupport;
    private JButton zoomInX2;
    private JButton zoomOutX2;
    private JButton restoreZoom;
    private JButton panToggle;
    private JButton zoomBoxToggle;
    private JCheckBox ellipseCentersToggle;
    private JCheckBox ellipseLabelsToggle;
    private JCheckBox concordiaErrorsToggle;
    private final JLayeredPane concordiaGraphPanel;


    /** Creates a new instance of KwikiConcordiaToolBar
     * @param x 
     * @param kwikiDateModesSelectorListener
     * @param concordiaGraphPanel 
     * @param y  
     */
    public KwikiConcordiaToolBar(
            int x,
            int y,
            JLayeredPane concordiaGraphPanel,
            PropertyChangeListener kwikiDateModesSelectorListener) {

        super();

        setOpaque(true);

        setBackground(Color.white);

        setBounds(x, y, 178, 48);

        this.concordiaGraphPanel = concordiaGraphPanel;

        propertySupport = new PropertyChangeSupport(this);
        addPropertyChangeListener(kwikiDateModesSelectorListener);

        SetupZoomToggleButtons();
        SetupOptionsCheckBoxes();

        ((ConcordiaGraphPanel)concordiaGraphPanel).setGraphPanelModeChanger( this);

    }

    private void SetupZoomToggleButtons() {
        zoomInX2 = new JButton("+");
        zoomInX2.setOpaque(false);
        zoomInX2.setForeground(Color.black);
        zoomInX2.setFont(ReduxConstants.sansSerif_12_Bold);
        zoomInX2.setBounds(1, 1, 55, 15);
        zoomInX2.setMargin(new Insets(0, 0, 0, 0));


        zoomInX2.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                ((ConcordiaGraphPanel) concordiaGraphPanel).performZoom(4.0);
                ((ConcordiaGraphPanel) concordiaGraphPanel).repaint();
            }
        });

        add(zoomInX2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        zoomOutX2 = new JButton("-");
        zoomOutX2.setOpaque(false);
        zoomOutX2.setForeground(Color.black);
        zoomOutX2.setFont(ReduxConstants.sansSerif_12_Bold);
        zoomOutX2.setBounds(1, 17, 55, 15);
        zoomOutX2.setMargin(new Insets(0, 0, 0, 0));


        zoomOutX2.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                ((ConcordiaGraphPanel) concordiaGraphPanel).performZoom(-2.0);
                concordiaGraphPanel.repaint();
            }
        });

        add(zoomOutX2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        restoreZoom = new JButton("R");
        restoreZoom.setOpaque(false);
        restoreZoom.setForeground(Color.black);
        restoreZoom.setFont(ReduxConstants.sansSerif_12_Bold);
        restoreZoom.setBounds(1, 32, 55, 15);
        restoreZoom.setMargin(new Insets(0, 0, 0, 0));

        restoreZoom.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                ((PlottingDetailsDisplayInterface) concordiaGraphPanel).resetPanel();
            }
        });

        add(restoreZoom, javax.swing.JLayeredPane.DEFAULT_LAYER);


        panToggle = new JButton("Pan");
        panToggle.setOpaque(false);
        panToggle.setForeground(Color.black);
        panToggle.setFont(ReduxConstants.sansSerif_12_Bold);
        panToggle.setBounds(55, 1, 68, 24);
        panToggle.setMargin(new Insets(0, 0, 0, 0));


        panToggle.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                ((ConcordiaGraphPanel) concordiaGraphPanel).setImageMode("PAN");
                panToggle.requestFocusInWindow();
            }
        });

        add(panToggle, javax.swing.JLayeredPane.DEFAULT_LAYER);

        zoomBoxToggle = new JButton("Zbox");
        zoomBoxToggle.setOpaque(false);
        zoomBoxToggle.setForeground(Color.black);
        zoomBoxToggle.setFont(ReduxConstants.sansSerif_12_Bold);
        zoomBoxToggle.setBounds(55, 25, 68, 24);
        zoomBoxToggle.setMargin(new Insets(0, 0, 0, 0));


        zoomBoxToggle.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                ((ConcordiaGraphPanel) concordiaGraphPanel).setImageMode("ZOOM");
            }
        });

        add(zoomBoxToggle, javax.swing.JLayeredPane.DEFAULT_LAYER);
    }

    private void SetupOptionsCheckBoxes() {

        ellipseCentersToggle = new JCheckBox("E Ctr", false);
        ellipseCentersToggle.setOpaque(false);
        ellipseCentersToggle.setForeground(Color.black);
        ellipseCentersToggle.setFont(ReduxConstants.sansSerif_12_Bold);
        ellipseCentersToggle.setBounds(121, 1, 60, 15);
        ellipseCentersToggle.setMargin(new Insets(0, 0, 0, 0));

        ellipseCentersToggle.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                ((ConcordiaGraphPanel) concordiaGraphPanel).setShowEllipseCenters(((AbstractButton) e.getSource()).isSelected());
                concordiaGraphPanel.repaint();
            }
        });
        // set it true
        ellipseCentersToggle.doClick();

        add(ellipseCentersToggle, javax.swing.JLayeredPane.DEFAULT_LAYER);

        ellipseLabelsToggle = new JCheckBox("E Lbl", true);
        ellipseLabelsToggle.setOpaque(false);
        ellipseLabelsToggle.setForeground(Color.black);
        ellipseLabelsToggle.setFont(ReduxConstants.sansSerif_12_Bold);
        ellipseLabelsToggle.setBounds(121, 16, 60, 15);
        ellipseLabelsToggle.setMargin(new Insets(0, 0, 0, 0));

        ellipseLabelsToggle.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                ((ConcordiaGraphPanel) concordiaGraphPanel).setShowEllipseLabels(((AbstractButton) e.getSource()).isSelected());
                concordiaGraphPanel.repaint();
            }
        });

        // set it false
        ellipseLabelsToggle.doClick();

        add(ellipseLabelsToggle, javax.swing.JLayeredPane.DEFAULT_LAYER);

        concordiaErrorsToggle = new JCheckBox("C Err", false);
        concordiaErrorsToggle.setOpaque(false);
        concordiaErrorsToggle.setForeground(Color.black);
        concordiaErrorsToggle.setFont(ReduxConstants.sansSerif_12_Bold);
        concordiaErrorsToggle.setBounds(121, 32, 60, 15);
        concordiaErrorsToggle.setMargin(new Insets(0, 0, 0, 0));

        concordiaErrorsToggle.addChangeListener(new ChangeListener() {

            public void stateChanged(ChangeEvent e) {
                ((ConcordiaGraphPanel) concordiaGraphPanel).setShowConcordiaErrorBars(((AbstractButton) e.getSource()).isSelected());
                concordiaGraphPanel.repaint();
            }
        });

        // set it true
        concordiaErrorsToggle.doClick();
        add(concordiaErrorsToggle, javax.swing.JLayeredPane.DEFAULT_LAYER);
    }


    /**
     * 
     * @param g
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        paint((Graphics2D) g);
    }

    /**
     * 
     * @param g2d
     */
    public void paint(Graphics2D g2d) {

        RenderingHints rh = g2d.getRenderingHints();
        rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHints(rh);
        g2d.setColor(Color.red);

        DrawBounds(g2d);


    }

    private void DrawBounds(Graphics2D g2d) {

        g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

    }

    /**
     * 
     * @param listener
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }

    /**
     * 
     * @param listener
     */
    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }

    /**
     * 
     */
    @Override
    public void switchToPanMode () {
        panToggle.doClick();
    }

    /**
     * 
     * @param currentGraphAxesSetup
     */
    @Override
    public void synchronizePanelSizes(GraphAxesSetup currentGraphAxesSetup) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }
}
