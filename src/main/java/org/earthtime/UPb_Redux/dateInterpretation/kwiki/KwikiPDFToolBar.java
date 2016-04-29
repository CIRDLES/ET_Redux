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
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.dateInterpretation.DateProbabilityDensityPanel;
import org.earthtime.UPb_Redux.dateInterpretation.SampleTreeI;
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
    private final SampleInterface sample;

    private final JLayeredPane probabilityPanel;
    private final JLayeredPane concordiaGraphPanel;
    private final Map<String, String> probabilityChartOptions;
    private final javax.swing.JSlider negativePctDiscordance_slider;
    private JTextField negativePctDiscordance_text;
    private final javax.swing.JSlider positivePctDiscordance_slider;
    private JTextField positivePctDiscordance_text;
    private final javax.swing.JSlider percentUncertainty_slider;
    private JTextField pctUncertainty_text;
    private final JButton linkedUnlinkedDiscordance;
    private boolean doLinkDiscordances;
    private final SampleTreeI dateTreeByAliquot;

    /**
     * Creates a new instance of KwikiConcordiaToolBar
     *
     * @param x
     * @param y
     * @param aPDFGraphPanel
     * @param aConcordiaGraphPanel
     * @param sample the value of sample
     * @param dateTreeByAliquot
     */
    public KwikiPDFToolBar(
            int x, int y, JLayeredPane aPDFGraphPanel, JLayeredPane aConcordiaGraphPanel, SampleInterface sample, SampleTreeI dateTreeByAliquot) {

        super();

        setOpaque(true);

        setBackground(Color.white);

        setBounds(x, y, aPDFGraphPanel.getWidth() + 7, 56);

        this.probabilityPanel = aPDFGraphPanel;
        this.concordiaGraphPanel = aConcordiaGraphPanel;

        this.sample = sample;

        this.dateTreeByAliquot = dateTreeByAliquot;
        probabilityChartOptions = sample.getSampleDateInterpretationGUISettings().getProbabilityChartOptions();

        doLinkDiscordances = true;

        SetupZoomToggleButtons();
        SetupDateChooserButtons();

        negativePctDiscordance_slider = new javax.swing.JSlider();
        negativePctDiscordance_slider.setFont(new java.awt.Font("Arial", 1, 9));
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
        negativePctDiscordance_slider.setBounds(105, 1, 175, 38);
        negativePctDiscordance_slider.addChangeListener(new SliderChangeListener());
        add(negativePctDiscordance_slider);

        negativePctDiscordance_text = new JTextField();
        negativePctDiscordance_text.setOpaque(false);
        negativePctDiscordance_text.setFont(new java.awt.Font("Arial", 1, 10));
        negativePctDiscordance_text.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        negativePctDiscordance_text.setText("Negative % discordance");
        negativePctDiscordance_text.setAlignmentX(0.0F);
        negativePctDiscordance_text.setAlignmentY(0.0F);
        negativePctDiscordance_text.setBorder(null);
        negativePctDiscordance_text.setBounds(105, 38, 175, 17);
        add(negativePctDiscordance_text);

        linkedUnlinkedDiscordance = new JButton();
        linkedUnlinkedDiscordance.setBackground(new java.awt.Color(241, 230, 255));
        linkedUnlinkedDiscordance.setFont(new java.awt.Font("Braggadocio", 1, 24));
        linkedUnlinkedDiscordance.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/earthtime/images/unlinked.png")));
        linkedUnlinkedDiscordance.setToolTipText("Click to Unlock sliders.");
        linkedUnlinkedDiscordance.setAlignmentY(0.0F);
        linkedUnlinkedDiscordance.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        linkedUnlinkedDiscordance.setContentAreaFilled(false);
        linkedUnlinkedDiscordance.setDoubleBuffered(true);
        linkedUnlinkedDiscordance.setFocusable(false);
        linkedUnlinkedDiscordance.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        linkedUnlinkedDiscordance.setMargin(new java.awt.Insets(0, 0, 0, 0));
        linkedUnlinkedDiscordance.setOpaque(true);
        linkedUnlinkedDiscordance.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/earthtime/images/linked.png")));
        linkedUnlinkedDiscordance.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        linkedUnlinkedDiscordance.setBounds(278, 5, 20, 20);
        linkedUnlinkedDiscordance.addActionListener((java.awt.event.ActionEvent evt) -> {
            linkedUnlinkedDiscordanceActionPerformed(evt);
        });
        add(linkedUnlinkedDiscordance);

        positivePctDiscordance_slider = new javax.swing.JSlider();
        positivePctDiscordance_slider.setFont(new java.awt.Font("Arial", 1, 9));
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
        positivePctDiscordance_slider.setBounds(295, 1, 175, 38);
        positivePctDiscordance_slider.addChangeListener(new SliderChangeListener());
        add(positivePctDiscordance_slider);

        positivePctDiscordance_text = new JTextField();
        positivePctDiscordance_text.setOpaque(false);
        positivePctDiscordance_text.setFont(new java.awt.Font("Arial", 1, 10));
        positivePctDiscordance_text.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        positivePctDiscordance_text.setText("Positive % discordance");
        positivePctDiscordance_text.setAlignmentX(0.0F);
        positivePctDiscordance_text.setAlignmentY(0.0F);
        positivePctDiscordance_text.setBorder(null);
        positivePctDiscordance_text.setBounds(295, 38, 175, 17);
        add(positivePctDiscordance_text);

        percentUncertainty_slider = new javax.swing.JSlider();
        percentUncertainty_slider.setFont(new java.awt.Font("Arial", 1, 9));
        percentUncertainty_slider.setMaximum(100);
        percentUncertainty_slider.setMinimum(0);
        percentUncertainty_slider.setMajorTickSpacing(10);
        percentUncertainty_slider.setMinorTickSpacing(2);
        percentUncertainty_slider.setPaintLabels(true);
        percentUncertainty_slider.setPaintTicks(true);
        percentUncertainty_slider.setSnapToTicks(true);
        percentUncertainty_slider.setAutoscrolls(true);
        percentUncertainty_slider.setName("uncertaintyPerCentSliderValue");
        percentUncertainty_slider.setBounds(460, 1, 175, 38);
        percentUncertainty_slider.addChangeListener(new SliderChangeListener());
        add(percentUncertainty_slider);

        pctUncertainty_text = new JTextField();
        pctUncertainty_text.setOpaque(false);
        pctUncertainty_text.setFont(new java.awt.Font("Arial", 1, 10));
        pctUncertainty_text.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        pctUncertainty_text.setText("% uncertainty");
        pctUncertainty_text.setAlignmentX(0.0F);
        pctUncertainty_text.setAlignmentY(0.0F);
        pctUncertainty_text.setBorder(null);
        pctUncertainty_text.setBounds(460, 38, 175, 17);
        add(pctUncertainty_text);

        try {
            int uncertaintyPerCentSliderValue = Integer.parseInt(probabilityChartOptions.get("uncertaintyPerCentSliderValue"));
            percentUncertainty_slider.setValue(uncertaintyPerCentSliderValue);
            updateSlidersStatus(percentUncertainty_slider);
        } catch (NumberFormatException numberFormatException) {
        }
        try {
            int positivePerCentDiscordanceSliderValue = Integer.parseInt(probabilityChartOptions.get("positivePerCentDiscordanceSliderValue"));
            positivePctDiscordance_slider.setValue(positivePerCentDiscordanceSliderValue);
            updateSlidersStatus(positivePctDiscordance_slider);
        } catch (NumberFormatException numberFormatException) {
        }
        try {
            int negativePerCentDiscordanceSliderValue = Integer.parseInt(probabilityChartOptions.get("negativePerCentDiscordanceSliderValue"));
            negativePctDiscordance_slider.setValue(negativePerCentDiscordanceSliderValue);
            updateSlidersStatus(negativePctDiscordance_slider);
        } catch (NumberFormatException numberFormatException) {
        }

        try {
            String chosenDateName = probabilityChartOptions.get("chosenDateName");
            ((DateProbabilityDensityPanel) probabilityPanel).setChosenDateName(chosenDateName);

        } catch (Exception e) {
        }
        // choose date
        for (Enumeration e = dateChooserButtonGroup.getElements(); e.hasMoreElements();) {
            final JRadioButton jrb = (JRadioButton) e.nextElement();
            jrb.addActionListener((ActionEvent arg0) -> {
                // oct 2014 handle new Pbc corrections
                String chosenDateName = jrb.getName();
                
                ((DateProbabilityDensityPanel) probabilityPanel).setChosenDateName(chosenDateName);
                probabilityChartOptions.put("chosenDateName", chosenDateName);
                performFilteringPerSliders();
                ((DateProbabilityDensityPanel) probabilityPanel).prepareAndPaintPanel();
            });
            if (((DateProbabilityDensityPanel) probabilityPanel).getChosenDateName().replace("r", "").startsWith(jrb.getName().replace("r", ""))) {
                jrb.setSelected(true);
            }
        }

        ((DateProbabilityDensityPanel) probabilityPanel).setGraphPanelModeChanger(this);

    }

    private void linkedUnlinkedDiscordanceActionPerformed(java.awt.event.ActionEvent evt) {
        Icon oldPressed = linkedUnlinkedDiscordance.getPressedIcon();
        linkedUnlinkedDiscordance.setPressedIcon(linkedUnlinkedDiscordance.getIcon());
        linkedUnlinkedDiscordance.setIcon(oldPressed);
        doLinkDiscordances = !doLinkDiscordances;
        if (doLinkDiscordances) {
            linkedUnlinkedDiscordance.setToolTipText("Click to Unlock sliders.");
        } else {
            linkedUnlinkedDiscordance.setToolTipText("Click to Lock sliders.");
        }
        // test for linkage
        updateSlidersStatus(positivePctDiscordance_slider);
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
        if (doLinkDiscordances) {
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

        if (slider.equals(negativePctDiscordance_slider)) {
            negativePctDiscordance_text.setText("Negative % discordance = " + negativePctDiscordance_slider.getValue());
            if (doLinkDiscordances) {
                positivePctDiscordance_text.setText("Positive % discordance = " + positivePctDiscordance_slider.getValue());
            }
        }

        if (slider.equals(positivePctDiscordance_slider)) {
            positivePctDiscordance_text.setText("Positive % discordance = " + positivePctDiscordance_slider.getValue());
            if (doLinkDiscordances) {
                negativePctDiscordance_text.setText("Negative % discordance = " + negativePctDiscordance_slider.getValue());
            }
        }

        if (slider.equals(percentUncertainty_slider)) {
            pctUncertainty_text.setText("% uncertainty = " + percentUncertainty_slider.getValue());
        }
    }

    public void performFilteringPerSliders() {

        Vector<ETFractionInterface> filteredFractions = SampleDateInterpretationsUtilities.filterActiveUPbFractions(//
                sample.getUpbFractionsUnknown(),//
                ((DateProbabilityDensityPanel) probabilityPanel).getChosenDateName(),//
                positivePctDiscordance_slider.getValue(), //
                negativePctDiscordance_slider.getValue(), //
                percentUncertainty_slider.getValue());

        ((AliquotDetailsDisplayInterface) concordiaGraphPanel).//
                setFilteredFractions(filteredFractions);

        ((DateProbabilityDensityPanel) probabilityPanel).//
                setSelectedFractions(filteredFractions);

        try {
            dateTreeByAliquot.performLastUserSelectionOfSampleDate();
        } catch (Exception selectionError) {
        }

        ((DateProbabilityDensityPanel) probabilityPanel).prepareAndPaintPanel();
        concordiaGraphPanel.repaint();
    }

    private void SetupDateChooserButtons() {
        dateChooserButtonGroup = new ButtonGroup();

        date206_238_radioButton = new JRadioButton("206/238");
        dateChooserButtonGroup.add(date206_238_radioButton);
        date206_238_radioButton.setFont(new java.awt.Font("Arial", 1, 10));
        date206_238_radioButton.setText("206/238");
        date206_238_radioButton.setName("age206_238r");
        date206_238_radioButton.setBounds(40, 1, 75, 17);
        date206_238_radioButton.setSelected(true);
        add(date206_238_radioButton);

        date207_206_radioButton = new JRadioButton("207/206");
        dateChooserButtonGroup.add(date207_206_radioButton);
        date207_206_radioButton.setFont(new java.awt.Font("Arial", 1, 10));
        date207_206_radioButton.setText("207/206");
        date207_206_radioButton.setName("age207_206r");
        date207_206_radioButton.setBounds(40, 19, 75, 17);
        add(date207_206_radioButton);

        dateBest_radioButton = new JRadioButton("best");
        dateChooserButtonGroup.add(dateBest_radioButton);
        dateBest_radioButton.setFont(new java.awt.Font("Arial", 1, 10));
        dateBest_radioButton.setText("best");
        dateBest_radioButton.setName("bestAge");
        dateBest_radioButton.setBounds(40, 37, 75, 17);
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
        zoomInProbability_button.setBounds(1, 1, 35, 17);
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
        zoomOutProbability_button.setBounds(1, 19, 35, 17);
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
        restoreZoom.setBounds(1, 37, 35, 17);
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
