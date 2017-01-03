/*
 * KwikiConcordiaToolBar.java
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
package org.earthtime.UPb_Redux.dateInterpretation.kwiki;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLayeredPane;
import javax.swing.event.ChangeEvent;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.ConcordiaGraphPanel;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.GraphPanelModeChangeI;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.PlottingDetailsDisplayInterface;
import org.earthtime.beans.ET_JButton;
import org.earthtime.plots.PlotAxesSetupInterface;

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

    /**
     * Creates a new instance of KwikiConcordiaToolBar
     *
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

        ((ConcordiaGraphPanel) concordiaGraphPanel).setGraphPanelModeChanger(this);

    }

    private void SetupZoomToggleButtons() {
        zoomInX2 = new ET_JButton("+");
        zoomInX2.setBounds(1, 1, 55, 16);

        zoomInX2.addActionListener((ActionEvent arg0) -> {
            ((ConcordiaGraphPanel) concordiaGraphPanel).performZoom(4.0);
            concordiaGraphPanel.repaint();
        });

        add(zoomInX2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        zoomOutX2 = new ET_JButton("-");
        zoomOutX2.setBounds(1, 16, 55, 16);

        zoomOutX2.addActionListener((ActionEvent arg0) -> {
            ((ConcordiaGraphPanel) concordiaGraphPanel).performZoom(-2.0);
            concordiaGraphPanel.repaint();
        });

        add(zoomOutX2, javax.swing.JLayeredPane.DEFAULT_LAYER);

        restoreZoom = new ET_JButton("Reset");
        restoreZoom.setBounds(1, 31, 55, 16);

        restoreZoom.addActionListener((ActionEvent arg0) -> {
            ((PlottingDetailsDisplayInterface) concordiaGraphPanel).resetPanel(true, false);
        });

        add(restoreZoom, javax.swing.JLayeredPane.DEFAULT_LAYER);

        panToggle = new ET_JButton("Pan");
        panToggle.setBounds(55, 1, 65, 24);

        panToggle.addActionListener((ActionEvent arg0) -> {
            ((ConcordiaGraphPanel) concordiaGraphPanel).setImageMode("PAN");
            panToggle.requestFocusInWindow();
        });

        add(panToggle, javax.swing.JLayeredPane.DEFAULT_LAYER);

        zoomBoxToggle = new ET_JButton("Zbox");
        zoomBoxToggle.setBounds(55, 24, 65, 24);

        zoomBoxToggle.addActionListener((ActionEvent arg0) -> {
            ((ConcordiaGraphPanel) concordiaGraphPanel).setImageMode("ZOOM");
        });

        add(zoomBoxToggle, javax.swing.JLayeredPane.DEFAULT_LAYER);
    }

    private void SetupOptionsCheckBoxes() {

        ellipseCentersToggle = new JCheckBox("E Ctr", false);
        ellipseCentersToggle.setOpaque(false);
        ellipseCentersToggle.setForeground(Color.black);
        ellipseCentersToggle.setFont(ReduxConstants.sansSerif_12_Bold);
        ellipseCentersToggle.setBounds(119, 2, 60, 15);
        ellipseCentersToggle.setMargin(new Insets(0, 0, 0, 0));

        ellipseCentersToggle.addChangeListener((ChangeEvent e) -> {
            ((ConcordiaGraphPanel) concordiaGraphPanel).setShowEllipseCenters(((AbstractButton) e.getSource()).isSelected());
            concordiaGraphPanel.repaint();
        });
        // set it true
        ellipseCentersToggle.doClick();

        add(ellipseCentersToggle, javax.swing.JLayeredPane.DEFAULT_LAYER);

        ellipseLabelsToggle = new JCheckBox("E Lbl", true);
        ellipseLabelsToggle.setOpaque(false);
        ellipseLabelsToggle.setForeground(Color.black);
        ellipseLabelsToggle.setFont(ReduxConstants.sansSerif_12_Bold);
        ellipseLabelsToggle.setBounds(119, 17, 60, 15);
        ellipseLabelsToggle.setMargin(new Insets(0, 0, 0, 0));

        ellipseLabelsToggle.addChangeListener((ChangeEvent e) -> {
            ((ConcordiaGraphPanel) concordiaGraphPanel).setShowEllipseLabels(((AbstractButton) e.getSource()).isSelected());
            concordiaGraphPanel.repaint();
        });

        // set it false
        ellipseLabelsToggle.doClick();

        add(ellipseLabelsToggle, javax.swing.JLayeredPane.DEFAULT_LAYER);

        concordiaErrorsToggle = new JCheckBox("C Err", false);
        concordiaErrorsToggle.setOpaque(false);
        concordiaErrorsToggle.setForeground(Color.black);
        concordiaErrorsToggle.setFont(ReduxConstants.sansSerif_12_Bold);
        concordiaErrorsToggle.setBounds(119, 32, 60, 15);
        concordiaErrorsToggle.setMargin(new Insets(0, 0, 0, 0));

        concordiaErrorsToggle.addChangeListener((ChangeEvent e) -> {
            ((ConcordiaGraphPanel) concordiaGraphPanel).setShowConcordiaErrorBars(((AbstractButton) e.getSource()).isSelected());
            concordiaGraphPanel.repaint();
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
    public void switchToPanMode() {
        panToggle.doClick();
    }

    @Override
    public void synchronizePanelSizes(PlotAxesSetupInterface currentGraphAxesSetup) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
