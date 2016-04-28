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
import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.dateInterpretation.DateProbabilityDensityPanel;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.AliquotDetailsDisplayInterface;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.GraphPanelModeChangeI;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.PlottingDetailsDisplayInterface;
import org.earthtime.UPb_Redux.dateInterpretation.graphPersistence.GraphAxesSetup;
import org.earthtime.UPb_Redux.dialogs.sampleManagers.sampleDateInterpretationManagers.SampleDateInterpretationsUtilities;
import org.earthtime.beans.ET_JButton;
import org.earthtime.fractions.ETFractionInterface;
import org.earthtime.samples.SampleInterface;

/**
 *
 * @author James F. Bowring
 */
public class KwikiPDFToolBar extends JLayeredPane implements GraphPanelModeChangeI {

    private JButton zoomInProbability_button;
    private JButton zoomOutProbability_button;
    private JButton restoreZoom;
    private JRadioButton date206_238_radioButton;
    private JRadioButton date207_206_radioButton;
    private JRadioButton dateBest_radioButton;
    private ButtonGroup dateChooserButtonGroup;
    private SampleInterface sample;

    private final JLayeredPane probabilityPanel;
    private final JLayeredPane concordiaGraphPanel;
    private Map<String, String> probabilityChartOptions;
    private javax.swing.JSlider negativePctDiscordance_slider;
    private javax.swing.JSlider positivePctDiscordance_slider;

    /**
     * Creates a new instance of KwikiConcordiaToolBar
     *
     * @param x
     * @param y
     * @param aPDFGraphPanel
     * @param aConcordiaGraphPanel
     * @param sample the value of sample
     */
    public KwikiPDFToolBar(
            int x, int y, JLayeredPane aPDFGraphPanel, JLayeredPane aConcordiaGraphPanel, SampleInterface sample) {

        super();

        setOpaque(true);

        setBackground(Color.white);

        setBounds(x, y, 552, 56);

        this.probabilityPanel = aPDFGraphPanel;
        this.concordiaGraphPanel = aConcordiaGraphPanel;

        this.sample = sample;
        probabilityChartOptions = sample.getSampleDateInterpretationGUISettings().getProbabilityChartOptions();

        SetupZoomToggleButtons();
        SetupDateChooserButtons();

        negativePctDiscordance_slider = new javax.swing.JSlider();
        negativePctDiscordance_slider.setFont(new java.awt.Font("Arial", 1, 10));
        negativePctDiscordance_slider.setMaximum(0);
        negativePctDiscordance_slider.setMinimum(-100);
        negativePctDiscordance_slider.setMajorTickSpacing(10);
        negativePctDiscordance_slider.setMinorTickSpacing(2);
        negativePctDiscordance_slider.setPaintLabels(true);
        negativePctDiscordance_slider.setPaintTicks(true);
        negativePctDiscordance_slider.setSnapToTicks(true);
        negativePctDiscordance_slider.setAutoscrolls(true);
        negativePctDiscordance_slider.setValue(Integer.parseInt(probabilityChartOptions.get("negativePerCentDiscordanceSliderValue")));
        negativePctDiscordance_slider.setName("negativePerCentDiscordanceSliderValue");
        negativePctDiscordance_slider.setBounds(125, 1, 185, 38);
        negativePctDiscordance_slider.addChangeListener(new SliderChangeListener());
        add(negativePctDiscordance_slider);

        positivePctDiscordance_slider = new javax.swing.JSlider();
        positivePctDiscordance_slider.setFont(new java.awt.Font("Arial", 1, 10));
        positivePctDiscordance_slider.setMaximum(100);
        positivePctDiscordance_slider.setMinimum(0);
        positivePctDiscordance_slider.setMajorTickSpacing(10);
        positivePctDiscordance_slider.setMinorTickSpacing(2);
        positivePctDiscordance_slider.setPaintLabels(true);
        positivePctDiscordance_slider.setPaintTicks(true);
        positivePctDiscordance_slider.setSnapToTicks(true);
        positivePctDiscordance_slider.setAutoscrolls(true);
        positivePctDiscordance_slider.setValue(Integer.parseInt(probabilityChartOptions.get("positivePerCentDiscordanceSliderValue")));
        positivePctDiscordance_slider.setName("positivePerCentDiscordanceSliderValue");
        positivePctDiscordance_slider.setBounds(325, 1, 185, 38);
        positivePctDiscordance_slider.addChangeListener(new SliderChangeListener());
        add(positivePctDiscordance_slider);

        ((DateProbabilityDensityPanel) probabilityPanel).setGraphPanelModeChanger(this);

    }

    private class SliderChangeListener implements ChangeListener {

        // this flag stops spurious keystrokes from firing off preparepanel, which is expensive
        boolean wasChanging = true;

        @Override
        public void stateChanged(ChangeEvent e) {
            JSlider slider = ((JSlider) e.getSource());

            if (slider.getValueIsAdjusting()) {
                wasChanging = true;
            }

            if (!slider.getValueIsAdjusting() && wasChanging) {
                updateSlidersStatus(slider);
                
                probabilityChartOptions.put(slider.getName(), Integer.toString(slider.getValue()));

                performFilteringPerSliders();

                wasChanging = false;
            }
        }
    }

    private void updateSlidersStatus(JSlider slider) {
        if (true) {
            if (slider.equals(positivePctDiscordance_slider)) {
                ChangeListener[] changeListeners = negativePctDiscordance_slider.getChangeListeners();
                negativePctDiscordance_slider.removeChangeListener(changeListeners[0]);
                negativePctDiscordance_slider.setValue(-1 * positivePctDiscordance_slider.getValue());
                probabilityChartOptions.put(negativePctDiscordance_slider.getName(), Integer.toString(negativePctDiscordance_slider.getValue()));
                negativePctDiscordance_slider.addChangeListener(changeListeners[0]);
            } else if (slider.equals(negativePctDiscordance_slider)) {
                ChangeListener[] changeListeners = positivePctDiscordance_slider.getChangeListeners();
                positivePctDiscordance_slider.removeChangeListener(changeListeners[0]);
                positivePctDiscordance_slider.setValue(-1 * negativePctDiscordance_slider.getValue());
                probabilityChartOptions.put(positivePctDiscordance_slider.getName(), Integer.toString(positivePctDiscordance_slider.getValue()));
                positivePctDiscordance_slider.addChangeListener(changeListeners[0]);
            }
        }
    }

    public void performFilteringPerSliders() {

        Vector<ETFractionInterface> filteredFractions = SampleDateInterpretationsUtilities.filterActiveUPbFractions(//
                sample.getUpbFractionsUnknown(),//
                ((DateProbabilityDensityPanel) probabilityPanel).getChosenDateName(),//
                positivePctDiscordance_slider.getValue(), //
                negativePctDiscordance_slider.getValue(), //
                100);//percentUncertainty_slider.getValue());

        ((AliquotDetailsDisplayInterface) concordiaGraphPanel).//
                setFilteredFractions(filteredFractions);

        ((DateProbabilityDensityPanel) probabilityPanel).//
                setSelectedFractions(filteredFractions);

        ((DateProbabilityDensityPanel) probabilityPanel).prepareAndPaintPanel();
    }

    private void SetupDateChooserButtons() {
        dateChooserButtonGroup = new ButtonGroup();

        date206_238_radioButton = new JRadioButton("206/238");
        dateChooserButtonGroup.add(date206_238_radioButton);
        date206_238_radioButton.setFont(new java.awt.Font("Arial", 1, 10));
        date206_238_radioButton.setText("206/238");
        date206_238_radioButton.setName("age206_238r");
        date206_238_radioButton.setBounds(60, 1, 75, 17);
        date206_238_radioButton.setSelected(true);
        add(date206_238_radioButton);

        date207_206_radioButton = new JRadioButton("207/206");
        dateChooserButtonGroup.add(date207_206_radioButton);
        date207_206_radioButton.setFont(new java.awt.Font("Arial", 1, 10));
        date207_206_radioButton.setText("207/206");
        date207_206_radioButton.setName("age207_206r");
        date207_206_radioButton.setBounds(60, 19, 75, 17);
        add(date207_206_radioButton);

        dateBest_radioButton = new JRadioButton("best");
        dateChooserButtonGroup.add(dateBest_radioButton);
        dateBest_radioButton.setFont(new java.awt.Font("Arial", 1, 10));
        dateBest_radioButton.setText("best");
        dateBest_radioButton.setName("bestAge");
        dateBest_radioButton.setBounds(60, 37, 75, 17);
        add(dateBest_radioButton);

        // choose date
        for (Enumeration e = dateChooserButtonGroup.getElements(); e.hasMoreElements();) {
            final JRadioButton jrb = (JRadioButton) e.nextElement();
            jrb.addActionListener((ActionEvent arg0) -> {
                // oct 2014 handle new Pbc corrections
                String chosenDateName = jrb.getName();

                ((DateProbabilityDensityPanel) probabilityPanel).setChosenDateName(chosenDateName);
                ((DateProbabilityDensityPanel) probabilityPanel).//
                        setSelectedFractions(sample.getUpbFractionsUnknown());
                ((DateProbabilityDensityPanel) probabilityPanel).prepareAndPaintPanel();
            });
        }

    }

    private void SetupZoomToggleButtons() {
        zoomInProbability_button = new ET_JButton("+");
        zoomInProbability_button.setOpaque(false);
        zoomInProbability_button.setForeground(Color.black);
        zoomInProbability_button.setFont(ReduxConstants.sansSerif_12_Bold);
        zoomInProbability_button.setBounds(1, 1, 55, 17);
        zoomInProbability_button.setMargin(new Insets(0, 0, 0, 0));

        zoomInProbability_button.addActionListener((ActionEvent arg0) -> {
            // zoom
            ((DateProbabilityDensityPanel) probabilityPanel).zoomIn();
        });

        add(zoomInProbability_button, javax.swing.JLayeredPane.DEFAULT_LAYER);

        zoomOutProbability_button = new ET_JButton("-");
        zoomOutProbability_button.setOpaque(false);
        zoomOutProbability_button.setForeground(Color.black);
        zoomOutProbability_button.setFont(ReduxConstants.sansSerif_12_Bold);
        zoomOutProbability_button.setBounds(1, 19, 55, 17);
        zoomOutProbability_button.setMargin(new Insets(0, 0, 0, 0));

        zoomOutProbability_button.addActionListener((ActionEvent arg0) -> {
            // zoom
            double rangeX = ((DateProbabilityDensityPanel) probabilityPanel).getRangeX_Display();
            //System.out.println( "RANGE OUT = " + rangeX + "   offset = " + ((DateProbabilityDensityPanel) probabilityPanel).getDisplayOffsetX());

            double saveMinx = ((DateProbabilityDensityPanel) probabilityPanel).getMinX();
            double proposedMinX = saveMinx - rangeX / 2.0;

            ((DateProbabilityDensityPanel) probabilityPanel).//
                    setMinX(//
                            Math.max(//
                                    proposedMinX, DateProbabilityDensityPanel.DEFAULT_DISPLAY_MINX));

            // reset offset if hit the left wall
            double shiftMax = 0;
            if (proposedMinX <= DateProbabilityDensityPanel.DEFAULT_DISPLAY_MINX) {
                ((DateProbabilityDensityPanel) probabilityPanel).setDisplayOffsetX(0);
                shiftMax = DateProbabilityDensityPanel.DEFAULT_DISPLAY_MINX - proposedMinX;
            }

            ((DateProbabilityDensityPanel) probabilityPanel).//
                    setMaxX(//
                            Math.min(//
                                    (((DateProbabilityDensityPanel) probabilityPanel).getMaxX()//
                                    + rangeX / 2.0 + shiftMax), DateProbabilityDensityPanel.DEFAULT_DISPLAY_MAXX));

            ((DateProbabilityDensityPanel) probabilityPanel).//
                    setSelectedHistogramBinCount(0);

            probabilityPanel.repaint();
        });

        add(zoomOutProbability_button, javax.swing.JLayeredPane.DEFAULT_LAYER);

        restoreZoom = new ET_JButton("R");
        restoreZoom.setOpaque(false);
        restoreZoom.setForeground(Color.black);
        restoreZoom.setFont(ReduxConstants.sansSerif_12_Bold);
        restoreZoom.setBounds(1, 37, 55, 17);
        restoreZoom.setMargin(new Insets(0, 0, 0, 0));

        restoreZoom.addActionListener((ActionEvent arg0) -> {
            ((PlottingDetailsDisplayInterface) probabilityPanel).refreshPanel(true);
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
