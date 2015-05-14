/*
 * KwikiPDFToolBar.java
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
import java.util.Enumeration;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JRadioButton;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.dateInterpretation.DateProbabilityDensityPanel;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.GraphPanelModeChangeI;
import org.earthtime.UPb_Redux.dateInterpretation.graphPersistence.GraphAxesSetup;
import org.earthtime.UPb_Redux.samples.Sample;
import org.earthtime.beans.ET_JButton;
import org.earthtime.samples.SampleInterface;

/**
 *
 * @author James F. Bowring
 */
public class KwikiPDFToolBar extends JLayeredPane implements GraphPanelModeChangeI {

    private final PropertyChangeSupport propertySupport;
    private JButton zoomInProbability_button;
    private JButton zoomOutProbability_button;
    private JButton restoreZoom;
    private JRadioButton date206_238_radioButton;
    private JRadioButton date207_206_radioButton;
    private JRadioButton dateBest_radioButton;
    private ButtonGroup dateChooserButtonGroup;
    private SampleInterface sample;

    private final JLayeredPane pdfGraphPanel;

    /**
     * Creates a new instance of KwikiConcordiaToolBar
     *
     * @param x
     * @param y
     * @param aPDFGraphPanel
     * @param kwikiDateModesSelectorListener
     * @param sample the value of sample
     */
    public KwikiPDFToolBar(
            int x, int y, JLayeredPane aPDFGraphPanel, PropertyChangeListener kwikiDateModesSelectorListener, SampleInterface sample) {

        super();

        setOpaque(true);

        setBackground(Color.white);

        setBounds(x, y, 57, 48);

        this.pdfGraphPanel = aPDFGraphPanel;

        propertySupport = new PropertyChangeSupport(this);
        //   addPropertyChangeListener(kwikiDateModesSelectorListener);

        SetupZoomToggleButtons();
        SetupDateChooserButtons();

        ((DateProbabilityDensityPanel) pdfGraphPanel).setGraphPanelModeChanger(this);
        
        this.sample = sample;

    }

    private void SetupDateChooserButtons() {
        dateChooserButtonGroup = new ButtonGroup();

        date206_238_radioButton = new JRadioButton("206/238");
        dateChooserButtonGroup.add(date206_238_radioButton);
        date206_238_radioButton.setFont(new java.awt.Font("Arial", 1, 10));
        date206_238_radioButton.setText("206/238");
        date206_238_radioButton.setName("age206_238r");
        date206_238_radioButton.setBounds(75, 1, 55, 15);
        add(date206_238_radioButton);

        date207_206_radioButton = new JRadioButton("207/206");
        dateChooserButtonGroup.add(date207_206_radioButton);
        date207_206_radioButton.setFont(new java.awt.Font("Arial", 1, 10));
        date207_206_radioButton.setText("207/206");
        date207_206_radioButton.setName("age207_206r");
        date207_206_radioButton.setBounds(75, 17, 55, 15);
        add(date207_206_radioButton);

        dateBest_radioButton = new JRadioButton("best");
        dateChooserButtonGroup.add(dateBest_radioButton);
        dateBest_radioButton.setFont(new java.awt.Font("Arial", 1, 10));
        dateBest_radioButton.setText("207/206");
        dateBest_radioButton.setName("bestAge");
        dateBest_radioButton.setBounds(75, 32, 55, 15);
        add(dateBest_radioButton);

        // choose date
        for (Enumeration e = dateChooserButtonGroup.getElements(); e.hasMoreElements();) {
            final JRadioButton jrb = (JRadioButton) e.nextElement();
            jrb.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent arg0) {
                    // oct 2014 handle new Pbc corrections
                    String chosenDateName = jrb.getName();

                    ((DateProbabilityDensityPanel) pdfGraphPanel).setChosenDateName(chosenDateName);
                    ((DateProbabilityDensityPanel) pdfGraphPanel).//
                            setSelectedFractions( ((Sample)sample).getUpbFractionsUnknown());
                    ((DateProbabilityDensityPanel) pdfGraphPanel).prepareAndPaintPanel();//.refreshPanel();
                }
            });
//            if (((DateProbabilityDensityPanel) probabilityPanel).getChosenDateName().replace("r", "").startsWith(jrb.getName().replace("r", ""))) {
//                jrb.setSelected(true);
//            }
        }

    }

    private void SetupZoomToggleButtons() {
        zoomInProbability_button = new ET_JButton("+");
        zoomInProbability_button.setOpaque(false);
        zoomInProbability_button.setForeground(Color.black);
        zoomInProbability_button.setFont(ReduxConstants.sansSerif_12_Bold);
        zoomInProbability_button.setBounds(1, 1, 55, 15);
        zoomInProbability_button.setMargin(new Insets(0, 0, 0, 0));

        zoomInProbability_button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                // zoom
                ((DateProbabilityDensityPanel) pdfGraphPanel).zoomIn();
            }
        });

        add(zoomInProbability_button, javax.swing.JLayeredPane.DEFAULT_LAYER);

        zoomOutProbability_button = new ET_JButton("-");
        zoomOutProbability_button.setOpaque(false);
        zoomOutProbability_button.setForeground(Color.black);
        zoomOutProbability_button.setFont(ReduxConstants.sansSerif_12_Bold);
        zoomOutProbability_button.setBounds(1, 17, 55, 15);
        zoomOutProbability_button.setMargin(new Insets(0, 0, 0, 0));

        zoomOutProbability_button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                // zoom
                double rangeX = ((DateProbabilityDensityPanel) pdfGraphPanel).getRangeX_Display();
                //System.out.println( "RANGE OUT = " + rangeX + "   offset = " + ((DateProbabilityDensityPanel) probabilityPanel).getDisplayOffsetX());

                double saveMinx = ((DateProbabilityDensityPanel) pdfGraphPanel).getMinX();
                double proposedMinX = saveMinx - rangeX / 2.0;

                ((DateProbabilityDensityPanel) pdfGraphPanel).//
                        setMinX(//
                                Math.max(//
                                        proposedMinX, DateProbabilityDensityPanel.DEFAULT_DISPLAY_MINX));

                // reset offset if hit the left wall
                double shiftMax = 0;
                if (proposedMinX <= DateProbabilityDensityPanel.DEFAULT_DISPLAY_MINX) {
                    ((DateProbabilityDensityPanel) pdfGraphPanel).setDisplayOffsetX(0);
                    shiftMax = DateProbabilityDensityPanel.DEFAULT_DISPLAY_MINX - proposedMinX;
                }

                ((DateProbabilityDensityPanel) pdfGraphPanel).//
                        setMaxX(//
                                Math.min(//
                                        (((DateProbabilityDensityPanel) pdfGraphPanel).getMaxX()//
                                        + rangeX / 2.0 + shiftMax), DateProbabilityDensityPanel.DEFAULT_DISPLAY_MAXX));

                ((DateProbabilityDensityPanel) pdfGraphPanel).//
                        setSelectedHistogramBinCount(0);

                pdfGraphPanel.repaint();
            }
        });

        add(zoomOutProbability_button, javax.swing.JLayeredPane.DEFAULT_LAYER);

        restoreZoom = new ET_JButton("R");
        restoreZoom.setOpaque(false);
        restoreZoom.setForeground(Color.black);
        restoreZoom.setFont(ReduxConstants.sansSerif_12_Bold);
        restoreZoom.setBounds(1, 32, 55, 15);
        restoreZoom.setMargin(new Insets(0, 0, 0, 0));

        restoreZoom.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                ((DateProbabilityDensityPanel) pdfGraphPanel).refreshPanel();
            }
        });
        add(restoreZoom, javax.swing.JLayeredPane.DEFAULT_LAYER);

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
