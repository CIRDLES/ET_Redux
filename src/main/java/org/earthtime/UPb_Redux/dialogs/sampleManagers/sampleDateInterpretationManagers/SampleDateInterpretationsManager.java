/*
 * SampleDateInterpretationsManager.java
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
package org.earthtime.UPb_Redux.dialogs.sampleManagers.sampleDateInterpretationManagers;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import org.apache.batik.swing.JSVGCanvas;
import org.earthtime.UPb_Redux.aliquots.UPbReduxAliquot;
import org.earthtime.UPb_Redux.beans.ReduxSpinner;
import org.earthtime.UPb_Redux.beans.ReduxSuppressComponentEventsI;
import org.earthtime.UPb_Redux.customJTrees.CheckBoxNode;
import org.earthtime.UPb_Redux.dateInterpretation.DateInterpretationUpdateViewI;
import org.earthtime.UPb_Redux.dateInterpretation.DateProbabilityDensityPanel;
import org.earthtime.UPb_Redux.dateInterpretation.SampleTreeAnalysisMode;
import org.earthtime.UPb_Redux.dateInterpretation.SampleTreeChangeI;
import org.earthtime.UPb_Redux.dateInterpretation.SampleTreeCompilationMode;
import org.earthtime.UPb_Redux.dateInterpretation.SampleTreeI;
import org.earthtime.UPb_Redux.dateInterpretation.WeightedMeanGraphPanel;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.AliquotDetailsDisplayInterface;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.ConcordiaGraphPanel;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.GraphPanelModeChangeI;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.PlottingDetailsDisplayInterface;
import org.earthtime.UPb_Redux.dateInterpretation.graphPersistence.DateInterpretationBoxPanel;
import org.earthtime.UPb_Redux.dateInterpretation.graphPersistence.GraphAxesSetup;
import org.earthtime.UPb_Redux.dialogs.aliquotManagers.AliquotOptionsDialog;
import org.earthtime.UPb_Redux.dialogs.graphManagers.ConcordiaOptionsDialog;
import org.earthtime.UPb_Redux.dialogs.graphManagers.WeightedMeanOptionsDialog;
import org.earthtime.UPb_Redux.dialogs.sampleManagers.heatMapManagers.HeatMapManager;
import org.earthtime.UPb_Redux.filters.PDFFileFilter;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFraction;
import org.earthtime.UPb_Redux.utilities.BrowserControl;
import org.earthtime.UPb_Redux.valueModels.SampleDateModel;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.aliquots.AliquotInterface;
import org.earthtime.aliquots.ReduxAliquotInterface;
import org.earthtime.beans.ET_JButton;
import org.earthtime.beans.ET_JToggleButton;
import org.earthtime.dataDictionaries.Lambdas;
import org.earthtime.dataDictionaries.MatrixSpecifications;
import org.earthtime.dataDictionaries.RadDates;
import org.earthtime.dataDictionaries.SampleAnalysisTypesEnum;
import org.earthtime.dialogs.DialogEditor;
import org.earthtime.exceptions.ETException;
import org.earthtime.exceptions.ETWarningDialog;
import org.earthtime.fractions.ETFractionInterface;
import org.earthtime.reduxLabData.ReduxLabData;
import org.earthtime.samples.SampleInterface;
import org.earthtime.utilities.CollectionHelpers;
import org.earthtime.utilities.FileHelper;

/**
 *
 * @author James F. Bowring
 */
public class SampleDateInterpretationsManager extends DialogEditor
        implements
        SampleTreeChangeI,
        GraphPanelModeChangeI,
        DateInterpretationUpdateViewI {

    // Fields
    private JLayeredPane concordiaGraphPanel;
    private JPanel weightedMeanGraphPanel;
//    private JFXPanel ConcordiaGraphPanelIsoplot;
    /**
     *
     */
    protected JLayeredPane probabilityPanel;
    private SampleInterface sample;
    private JSVGCanvas svgConcordiaCanvas;
    private JSVGCanvas svgWeightedMeanCanvas;
    private SampleTreeI dateTreeByAliquot;
    private SampleTreeI dateTreeBySample;
    private String concordiaFlavor;
    private Map<String, String> weightedMeanOptions;
    private boolean doLinkDiscordances;
    private SampleDateInterpretationSubscribeInterface parentFrame;
    // oct 2014
    private Map<String, String> probabilityChartOptions;

    /**
     * Creates new form SampleDateInterpretations
     *
     * @param parent
     * @param modal
     * @param concordiaGraphPanel
     * @param weightedMeanGraphPanel
     * @param normedProbabilityPanel
     * @param sample
     * @param dateTreeByAliquot
     * @param dateTreeBySample
     */
    public SampleDateInterpretationsManager( //
            SampleDateInterpretationSubscribeInterface parent, //
            boolean modal, JLayeredPane concordiaGraphPanel, //
            JPanel weightedMeanGraphPanel, //
            JLayeredPane normedProbabilityPanel, SampleInterface sample, SampleTreeI dateTreeByAliquot, SampleTreeI dateTreeBySample) {

        super(null, modal);

        this.parentFrame = parent;

        this.sample = sample;

        initComponents();
        setSize(1200, 750);
        setLocation(getX() + 100, getY());

        this.concordiaGraphPanel = concordiaGraphPanel;
        initConcordiaGraphPanel();

////         march 2014 temp exploration using isoplot
//        this.ConcordiaGraphPanelIsoplot = myConcordiaGraphPanelIsoplot;
//        ConcordiaGraphPanelIsoplot.setBounds(0, 0, concordiaIsoplotLayeredPane.getWidth(), concordiaIsoplotLayeredPane.getHeight());
//        concordiaIsoplotLayeredPane.add(ConcordiaGraphPanelIsoplot, javax.swing.JLayeredPane.DEFAULT_LAYER);
//        concordiaIsoplotLayeredPane.validate();
//        concordiaIsoplotLayeredPane.repaint();
        this.weightedMeanGraphPanel = weightedMeanGraphPanel;
        initWeightedMeanGraphPanel();

        this.probabilityPanel = normedProbabilityPanel;
        initNormedProbabilityPanel();

        this.dateTreeByAliquot = dateTreeByAliquot;
        dateTreeByAliquot.setSampleTreeChange(this);
        dateTreeByAliquot.buildTree();

        this.dateTreeBySample = dateTreeBySample;
        dateTreeBySample.setSampleTreeChange(this);
        dateTreeBySample.buildTree();

        dateTreeByAliquot_ScrollPane.setViewportView((Component) dateTreeByAliquot);

        dateTreeBySample_ScrollPane.setViewportView((Component) dateTreeBySample);

        try {
            // June 2010 ensures backward compatibility with previous versions that used dummy aliquot in this list
            setupWeightedMeansPanelForAliquots();
        } catch (Exception e) {
        }

        doLinkDiscordances = true;

        // fire off a refresh to get things started
        ((PlottingDetailsDisplayInterface) concordiaGraphPanel).refreshPanel(true);

    }

    /**
     *
     * @param doReScale the value of doReScale
     */
    public void refreshSampleDateInterpretations(boolean doReScale) {

        String expansionHistory = "";
        if (!doReScale) {
            expansionHistory = dateTreeByAliquot.collectExpansionHistory();
        }
            dateTreeByAliquot = new SampleTreeAnalysisMode(sample);
            dateTreeByAliquot.setSampleTreeChange(this);
            dateTreeByAliquot.buildTree();
            dateTreeByAliquot_ScrollPane.setViewportView((Component) dateTreeByAliquot);
        if (!doReScale) {
            dateTreeByAliquot.expandToHistory(expansionHistory);
        }

            dateTreeBySample = new SampleTreeCompilationMode(sample);
            dateTreeBySample.setSampleTreeChange(this);
            dateTreeBySample.buildTree();
            dateTreeBySample_ScrollPane.setViewportView((Component) dateTreeBySample);


        ((PlottingDetailsDisplayInterface) concordiaGraphPanel).resetPanel(doReScale);

        try {
            // June 2010 ensures backward compatibility with previous versions that used dummy aliquot in this list
            setupWeightedMeansPanelForAliquots();
        } catch (Exception e) {
        }
        ((PlottingDetailsDisplayInterface) weightedMeanGraphPanel).resetPanel(doReScale);
        ((PlottingDetailsDisplayInterface) probabilityPanel).resetPanel(doReScale);

    }

    private void initConcordiaGraphPanel() {
        ((ConcordiaGraphPanel) concordiaGraphPanel).setGraphPanelModeChanger(this);

        // set toolbar choices per options
        Map<String, String> CGO = sample.getSampleDateInterpretationGUISettings().getConcordiaOptions();

        if (CGO.containsKey("concordiaFlavor")) {
            if (CGO.get("concordiaFlavor").equalsIgnoreCase("T-W")) {
                terraWasserburgFlavor_radioButton.setSelected(true);
            } else if (CGO.get("concordiaFlavor").equalsIgnoreCase("Th")) {
                thoriumConcordiaFlavor_radioButton.setSelected(true);
            } else {
                concordiaFlavor_radioButton.setSelected(true);
            }
            ((ConcordiaGraphPanel) concordiaGraphPanel).setConcordiaFlavor(CGO.get("concordiaFlavor"));
        }

        if (CGO.containsKey("showEllipseCenters")) {
            ellipseCenters_checkbox.setSelected(Boolean.valueOf(CGO.get("showEllipseCenters")));
            ((ConcordiaGraphPanel) concordiaGraphPanel).setShowEllipseCenters(ellipseCenters_checkbox.isSelected());
        }
        if (CGO.containsKey("showEllipseLabels")) {
            ellipseLabels_checkbox.setSelected(Boolean.valueOf(CGO.get("showEllipseLabels")));
            ((ConcordiaGraphPanel) concordiaGraphPanel).setShowEllipseLabels(ellipseLabels_checkbox.isSelected());
        }

        if (CGO.containsKey("showExcludedEllipses")) {
            showExcludedFractions_checkbox.setSelected(Boolean.valueOf(CGO.get("showExcludedEllipses")));
            ((ConcordiaGraphPanel) concordiaGraphPanel).setShowExcludedEllipses(showExcludedFractions_checkbox.isSelected());
        }

        if (CGO.containsKey("useUncertaintyCrosses")) {
            useUncertaintyCrosses_checkbox.setSelected(Boolean.valueOf(CGO.get("useUncertaintyCrosses")));
            ((ConcordiaGraphPanel) concordiaGraphPanel).setUseUncertaintyCrosses(useUncertaintyCrosses_checkbox.isSelected());
        }

        if (CGO.containsKey("showConcordiaErrors")) {
            concordiaErrors_checkbox.setSelected(Boolean.valueOf(CGO.get("showConcordiaErrors")));
            ((ConcordiaGraphPanel) concordiaGraphPanel).setShowConcordiaErrorBars(concordiaErrors_checkbox.isSelected());
        }
        if (sample.isSampleTypeLegacy() && (!sample.getSampleAnalysisType().equalsIgnoreCase(SampleAnalysisTypesEnum.IDTIMS.getName()))) {
            thoriumCorrectionSelector_checkbox.setEnabled(false);
            protactiniumCorrectionSelector_checkbox.setEnabled(false);
        } else {
            if (CGO.containsKey("display_r206_238r_Th")) {
                boolean display_r206_238r_Th = Boolean.valueOf(CGO.get("display_r206_238r_Th"));
                thoriumCorrectionSelector_checkbox.setSelected(display_r206_238r_Th);
                ((ConcordiaGraphPanel) concordiaGraphPanel).setDisplay_r206_238r_Th(display_r206_238r_Th);
            }
            if (CGO.containsKey("display_r206_238r_Pa")) {
                boolean display_r206_238r_Pa = Boolean.valueOf(CGO.get("display_r206_238r_Pa"));
                protactiniumCorrectionSelector_checkbox.setSelected(display_r206_238r_Pa);
                ((ConcordiaGraphPanel) concordiaGraphPanel).setDisplay_r206_238r_Pa(display_r206_238r_Pa);
            }
            if (CGO.containsKey("display_PbcCorr")) {
                boolean display_PbcCorr = Boolean.valueOf(CGO.get("display_PbcCorr"));
                commonLeadCorrectionSelector_checkbox.setSelected(display_PbcCorr);
                ((ConcordiaGraphPanel) concordiaGraphPanel).setDisplay_PbcCorr(display_PbcCorr);
            }
        }

        ((ConcordiaGraphPanel) concordiaGraphPanel).setShowTitleBox(true);

        concordiaLayeredPane.add(concordiaGraphPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        // zoom buttons
        zoomInX2_button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                ((ConcordiaGraphPanel) concordiaGraphPanel).performZoom(4.0);
                ((ConcordiaGraphPanel) concordiaGraphPanel).getCurrentGraphAxesSetup().setUseAutomaticAxisTics(true);
                ((ConcordiaGraphPanel) concordiaGraphPanel).repaint();
            }
        });

        zoomOutX2_button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                ((ConcordiaGraphPanel) concordiaGraphPanel).performZoom(-2.0);
                ((ConcordiaGraphPanel) concordiaGraphPanel).getCurrentGraphAxesSetup().setUseAutomaticAxisTics(true);
                concordiaGraphPanel.repaint();
            }
        });

    }

    /**
     *
     */
    @Override
    public void repaintActiveTab() {
        concordiaGraphPanel.repaint();

        weightedMeanGraphPanel.repaint();
    }

    class WeightedMeanFractionOrderActionListener implements ActionListener {

        Map<String, String> WMO;

        public WeightedMeanFractionOrderActionListener(Map<String, String> WMO) {
            this.WMO = WMO;
        }

        @Override
        public void actionPerformed(ActionEvent arg0) {
            WMO.put("fractionSortOrder", arg0.getActionCommand());
            weightedMeanGraphPanel.repaint();
        }
    }

    private void initWeightedMeanGraphPanel() {
        // set toolbar choices per options
        weightedMeanOptions = sample.getSampleDateInterpretationGUISettings().getWeightedMeanOptions();
        ((WeightedMeanGraphPanel) weightedMeanGraphPanel).setWeightedMeanOptions(weightedMeanOptions);

        String fractionSortOrder = "name";  //random";//weight";
        if (weightedMeanOptions.containsKey("fractionSortOrder")) {
            fractionSortOrder = weightedMeanOptions.get("fractionSortOrder");
        }

        for (Enumeration e = weightedMeanFractionOrderButtonGroup.getElements(); e.hasMoreElements();) {
            JRadioButton jrb = (JRadioButton) e.nextElement();
            try {
                jrb.removeActionListener(jrb.getActionListeners()[0]);
            } catch (Exception e2) {
            }
            jrb.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent arg0) {
                    weightedMeanOptions.put("fractionSortOrder", arg0.getActionCommand());
                    ((WeightedMeanGraphPanel) weightedMeanGraphPanel).repaint();
                }
            });

            if (jrb.getName().equalsIgnoreCase(fractionSortOrder)) {
                jrb.setSelected(true);
            }

        }

        weightedMeanLayeredPane.add(weightedMeanGraphPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        int heightWP = ((WeightedMeanGraphPanel) weightedMeanGraphPanel).getGraphHeight();
        int widthWP = ((WeightedMeanGraphPanel) weightedMeanGraphPanel).getGraphWidth();
        weightedMeanGraphPanel.setBounds(
                1,
                1,
                widthWP + ((WeightedMeanGraphPanel) weightedMeanGraphPanel).getLeftMargin(),
                heightWP + 16);

        weightedMeanToolPanel.setBounds(
                1,
                heightWP + 15,
                widthWP + ((WeightedMeanGraphPanel) weightedMeanGraphPanel).getLeftMargin(),
                35);

        // zoom buttons
        zoomInX2_WeightedMean_button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                // zoom
                double rangeY = ((WeightedMeanGraphPanel) weightedMeanGraphPanel).getRangeY();

                ((WeightedMeanGraphPanel) weightedMeanGraphPanel).//
                        setMinY(//
                                ((WeightedMeanGraphPanel) weightedMeanGraphPanel).getMinY()//
                                + rangeY / 4.0);

                ((WeightedMeanGraphPanel) weightedMeanGraphPanel).//
                        setMaxY(//
                                ((WeightedMeanGraphPanel) weightedMeanGraphPanel).getMaxY()//
                                - rangeY / 4.0);

                weightedMeanGraphPanel.repaint();

            }
        });

        zoomOutX2_WeightedMean_button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                // zoom
                double rangeY = ((WeightedMeanGraphPanel) weightedMeanGraphPanel).getRangeY();

                ((WeightedMeanGraphPanel) weightedMeanGraphPanel).//
                        setMinY(//
                                ((WeightedMeanGraphPanel) weightedMeanGraphPanel).getMinY()//
                                - rangeY / 2.0);

                ((WeightedMeanGraphPanel) weightedMeanGraphPanel).//
                        setMaxY(//
                                ((WeightedMeanGraphPanel) weightedMeanGraphPanel).getMaxY()//
                                + rangeY / 2.0);

                weightedMeanGraphPanel.repaint();

            }
        });
    }

    private void initNormedProbabilityPanel() {

        // set toolbar choices per options
        probabilityChartOptions = sample.getSampleDateInterpretationGUISettings().getProbabilityChartOptions();

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
            boolean correctedForPbc = Boolean.valueOf(probabilityChartOptions.get("correctedForPbc"));
            commonLeadCorrectionSelectorPDF_checkbox.setSelected(correctedForPbc);
        } catch (Exception e) {
        }
        try {
            boolean showHistogram = Boolean.valueOf(probabilityChartOptions.get("showHistogram"));
            ((DateProbabilityDensityPanel) probabilityPanel).setShowHistogram(showHistogram);
            updateShowHistogramButton();
        } catch (Exception e) {
        }
        try {
            String chosenDateName = probabilityChartOptions.get("chosenDateName");
            ((DateProbabilityDensityPanel) probabilityPanel).setChosenDateName(chosenDateName);

        } catch (Exception e) {
        }
        // choose date
        for (Enumeration e = probabilityDateButtonGroup.getElements(); e.hasMoreElements();) {
            final JRadioButton jrb = (JRadioButton) e.nextElement();
            jrb.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent arg0) {
                    // oct 2014 handle new Pbc corrections
                    String chosenDateName = jrb.getName();
                    if (commonLeadCorrectionSelectorPDF_checkbox.isSelected()) {
                        chosenDateName = chosenDateName.replace("r", "") + "_PbcCorr";
                    }

                    ((DateProbabilityDensityPanel) probabilityPanel).setChosenDateName(chosenDateName);
                    probabilityChartOptions.put("chosenDateName", chosenDateName);
                    ((DateProbabilityDensityPanel) probabilityPanel).//
                            setSelectedFractions(filterActiveUPbFractions(sample.getUpbFractionsUnknown()));
                    ((DateProbabilityDensityPanel) probabilityPanel).prepareAndPaintPanel();
                }
            });
            if (((DateProbabilityDensityPanel) probabilityPanel).getChosenDateName().replace("r", "").startsWith(jrb.getName().replace("r", ""))) {
                jrb.setSelected(true);
            }
        }

        // percent discordance sliders
        positivePctDiscordance_slider.addChangeListener(new SliderChangeListener());
        positivePctDiscordance_slider.addKeyListener(new SliderKeyListener());

        negativePctDiscordance_slider.addChangeListener(new SliderChangeListener());
        negativePctDiscordance_slider.addKeyListener(new SliderKeyListener());

        // percent uncertainty slider
        percentUncertainty_slider.addChangeListener(new SliderChangeListener());
        percentUncertainty_slider.addKeyListener(new SliderKeyListener());

        // zoom buttons
        zoomInProbability_button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                // zoom
                ((DateProbabilityDensityPanel) probabilityPanel).zoomIn();
            }
        });

        zoomOutProbability_button.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
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
            }
        });

        showTightGraphProbability_button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ((DateProbabilityDensityPanel) probabilityPanel).showTight();
            }
        });

        // histogram services
        SpinnerModel histogramBinCount_spinnerModel
                = new SpinnerNumberModel(//
                        1, 1, 999, 1);
        histogramBinCount_spinner.setModel(histogramBinCount_spinnerModel);

        histogramBinCount_spinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                //int binCount = (Integer) histogramBinCount_spinner.getValue();
                Double binCountRead = Math.floor(Double.valueOf(histogramBinCount_spinner.getValue().toString()));
                int binCount = binCountRead.intValue();
                ((DateProbabilityDensityPanel) probabilityPanel).setSelectedHistogramBinCount(binCount);
                probabilityPanel.repaint();
            }
        });

        // pass spinner into panel
        ((DateProbabilityDensityPanel) probabilityPanel).setExternalBinCountIntegerSpinner((ReduxSuppressComponentEventsI) histogramBinCount_spinner);

        binWidth_text.setDocument(new IntegerDocument(binWidth_text, true));
        binWidth_text.addActionListener(new ActionListener() {

            // capture enter key
            @Override
            public void actionPerformed(ActionEvent e) {
                if (binWidth_text.getText().trim().length() == 0) {
                    binWidth_text.setText("100");
                }
                Double binCountRead = Math.floor(Double.valueOf(histogramBinCount_spinner.getValue().toString()));
                int binCount = binCountRead.intValue();
                double binWidth = ((DateProbabilityDensityPanel) probabilityPanel).getAdjustedScottsBinWidth();
                double revisedBinWidth = Double.parseDouble(binWidth_text.getText());
                ((DateProbabilityDensityPanel) probabilityPanel).setAdjustedScottsBinWidth(revisedBinWidth);
                histogramBinCount_spinner.setValue((binCount * (binWidth / revisedBinWidth)));
                probabilityPanel.repaint();
            }
        });

        // pass bin width textField into panel
        ((DateProbabilityDensityPanel) probabilityPanel).setExternalBinWidthTextField((ReduxSuppressComponentEventsI) binWidth_text);

        normedProbabilityLayeredPane.add(probabilityPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);

    } // initNormedProbabilityPanel

    private class SliderKeyListener implements KeyListener {

        boolean validKey = false;

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {

            JSlider slider = ((JSlider) e.getSource());

            if (e.getKeyCode() == KeyEvent.VK_LEFT) {
                slider.setValue(//
                        slider.getValue() - slider.getMinorTickSpacing());
                validKey = true;
            }
            if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
                slider.setValue(//
                        slider.getValue() + slider.getMinorTickSpacing());
                validKey = true;
            }
            if (e.getKeyCode() == KeyEvent.MOUSE_EVENT_MASK) {
                slider.setValue(//
                        slider.getValue() - slider.getMinorTickSpacing());
                validKey = true;
            }

            if (validKey) {
                // test for linkage
                updateSlidersStatus(slider);

                ((DateProbabilityDensityPanel) probabilityPanel).//
                        setSelectedFractions(filterActiveUPbFractions(sample.getUpbFractionsUnknown()));
                // fire off date model to filter its deselected fractions
                try {
                    ((SampleTreeI) dateTreeByAliquot).performLastUserSelectionOfSampleDate();
                } catch (Exception selectionError) {
                }
                ((DateProbabilityDensityPanel) probabilityPanel).prepareAndPaintPanel();
                validKey = false;
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
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

                // oct 2014 make choices stick to data table
                Vector<ETFractionInterface> filteredFractions = filterActiveUPbFractions(sample.getUpbFractionsUnknown());
                sample.updateSetOfActiveFractions(filteredFractions);
                // oct 2014 repaint table
                parentFrame.updateReportTable();

                ((DateProbabilityDensityPanel) probabilityPanel).//
                        setSelectedFractions(filteredFractions);
                probabilityChartOptions.put(slider.getName(), Integer.toString(slider.getValue()));

                // fire off date model to filter its deselected fractions
                try {
                    dateTreeByAliquot.performLastUserSelectionOfSampleDate();
                } catch (Exception selectionError) {
                }
                ((DateProbabilityDensityPanel) probabilityPanel).prepareAndPaintPanel();
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

    /**
     *
     * @param currentGraphAxesSetup
     */
    @Override
    public void synchronizePanelSizes(GraphAxesSetup currentGraphAxesSetup) {

        // may 2010 added here to handle opening of an imported .redux file now that we persist size
        int ww = concordiaLayeredPane.getWidth();
        int hh = concordiaLayeredPane.getHeight();
        currentGraphAxesSetup.setGraphWidth(ww - 25.0);
        currentGraphAxesSetup.setGraphHeight(hh - 50.0);

        ((WeightedMeanGraphPanel) weightedMeanGraphPanel).setGraphWidth(ww - 25);
        ((WeightedMeanGraphPanel) weightedMeanGraphPanel).setGraphHeight(hh - 50);
        ((DateProbabilityDensityPanel) probabilityPanel).setGraphWidth(ww - 25);

        ((DateProbabilityDensityPanel) probabilityPanel).setGraphHeight(hh - 65);

    }

    /**
     *
     */
    public void setupWeightedMeansPanelForAliquots() {
        // TODO refactor
        // set up weighted means panel ******************************************
        // extract selected models for use by weighted mean visualization
//        weightedMeanOptions = sample.getSampleDateInterpretationGUISettings().getWeightedMeanOptions();

        Object[][] selectedModels = new Object[sample.getActiveAliquots().size()][9];

        // set aliquots
        for (int a = 0; a
                < sample.getActiveAliquots().size(); a++) {
            selectedModels[a][0] = sample.getActiveAliquots().get(a);

        }
        // a zero means no, a 1 means yes - note possible error if user changes aliquots - need to reset if this happens
        // default is one aliquot set to 0 all positions

        initializeWeightedMeanOptions(
                selectedModels, "weighted mean 207Pb/235U", 1);
        initializeWeightedMeanOptions(
                selectedModels, "weighted mean 206Pb/238U", 2);
        initializeWeightedMeanOptions(
                selectedModels, "weighted mean 207Pb/206Pb", 3);
        initializeWeightedMeanOptions(
                selectedModels, "weighted mean 206Pb/238U (Th-corrected)", 4);
        initializeWeightedMeanOptions(
                selectedModels, "weighted mean 207Pb/235U (Pa-corrected)", 5);
        initializeWeightedMeanOptions(
                selectedModels, "weighted mean 207Pb/206Pb (Th-corrected)", 6);
        initializeWeightedMeanOptions(
                selectedModels, "weighted mean 207Pb/206Pb (Pa-corrected)", 7);
        initializeWeightedMeanOptions(
                selectedModels, "weighted mean 207Pb/206Pb (Th- and Pa-corrected)", 8);

        ((WeightedMeanGraphPanel) weightedMeanGraphPanel).setSelectedSampleDateModels(selectedModels);

//        ((WeightedMeanGraphPanel) weightedMeanGraphPanel).setWeightedMeanOptions( weightedMeanOptions );
    }

    /**
     *
     */
    public void setupWeightedMeansPanelForSample() {

        Object[][] selectedModels = new Object[1][9];

        // this standin aliquot for the whole sample makes weighted means graphs work
        // updated april 2016
        AliquotInterface standInAliquot
                = sample.generateDefaultAliquot(1, sample.getSampleName(), ReduxLabData.getInstance().getDefaultPhysicalConstantsModel(), sample.isAnalyzed(), sample.getMySESARSampleMetadata());
//        standInAliquot.setAliquotName(sample.getSampleName());

        ((ReduxAliquotInterface) standInAliquot).setAliquotFractions(sample.getFractions());

        selectedModels[0][0] = standInAliquot;

        for (int i = 0; i
                < sample.getSampleDateModels().size(); i++) {
            if (((SampleDateModel) sample.getSampleDateModels().get(i)).isDisplayedAsGraph()) {
                selectedModels[0][i + 1] = sample.getSampleDateModels().get(i);

                ((SampleDateModel) sample.getSampleDateModels().get(i)).CalculateDateInterpretationForSample();
                ((SampleDateModel) sample.getSampleDateModels().get(i)).setAliquot(standInAliquot);
            }
        }

        ((WeightedMeanGraphPanel) weightedMeanGraphPanel).setSelectedSampleDateModels(selectedModels);
    }

    private void initializeWeightedMeanOptions(
            Object[][] selectedModels,
            String wmSampleDateName,
            int index) {

        String aliquotFlags = weightedMeanOptions.get(wmSampleDateName);

        if (aliquotFlags == null) {
            weightedMeanOptions.put(wmSampleDateName, "0");
            aliquotFlags = "0";
        }
        for (int bit = 0; bit
                < aliquotFlags.length(); bit++) {
            if (aliquotFlags.substring(bit, bit + 1).equalsIgnoreCase("1")) {
                selectedModels[bit][index]
                        = //
                        sample.getActiveAliquots().get(bit).getASampleDateModelByName(wmSampleDateName);
            }
        }
    }

    // June 2010 new sections for additional tabs
    // any 2 ratios plot tab
    private void selectAny2Axes() {
        // here we leverage the work of reductionhandler and Kwiki clumps to generate list of
        // possible axes;  need to be sure to include all possible from selected fractions
        // as tracers may vary

        // strategy = complete plan to have reduction handler use matrixspecname instead of matrixspecs
        // then here call a method to build set of all names of matrix components across all fractions chosen
        ETFractionInterface firstFraction = sample.getFractions().get(0);
        Vector<String> axesListing = new Vector<>();

        if (firstFraction instanceof UPbFraction) {
            axesListing
                    = //
                    CollectionHelpers.vectorSortedUniqueMembers( //
                            MatrixSpecifications.getMatrixSpecsByName(//
                                    ((UPbFraction) sample.getFractions().get(0)).getReductionHandler().getMatrixSpecsName()));// "mixed_202_205_233_235_Zircon_NotFcU_FcPb" ));
            // remove lambdas
            axesListing.remove(Lambdas.lambda230.getName());
            axesListing.remove(Lambdas.lambda231.getName());
            axesListing.remove(Lambdas.lambda232.getName());
            axesListing.remove(Lambdas.lambda234.getName());
            axesListing.remove(Lambdas.lambda235.getName());
            axesListing.remove(Lambdas.lambda238.getName());

        } else {
            axesListing.add("NONE AVAILABLE");
        }

        DialogEditor myDialog
                = //
                new SampleDateInterpretationAny2AxesChooser(null, true, axesListing);
        myDialog.setSize(350, 500);
        JDialog.setDefaultLookAndFeelDecorated(true);
        myDialog.setVisible(true);
    }

    /**
     *
     */
    @Override
    public void close() {

        // Save concordia options 
        ((ConcordiaGraphPanel) getConcordiaGraphPanel()).saveSettings();

        // oct 2014 send signal to main frame to enable sampledate button
        parentFrame.publishClosingOfSampleDateInterpretation();

        super.close();
    }

    /**
     *
     */
    public void forceClose() {
        close();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        concordiaPanZoom_buttonGroup = new javax.swing.ButtonGroup();
        weightedMeanPanZoom_buttonGroup = new javax.swing.ButtonGroup();
        weightedMeanFractionOrderButtonGroup = new javax.swing.ButtonGroup();
        concordiaTeraW_buttonGroup = new javax.swing.ButtonGroup();
        probabilityDateButtonGroup = new javax.swing.ButtonGroup();
        interpretations_SplitPane = new javax.swing.JSplitPane();
        dateTrees_tabs = new javax.swing.JTabbedPane();
        dateTreeByAliquot_ScrollPane = new javax.swing.JScrollPane();
        dateTreeBySample_ScrollPane = new javax.swing.JScrollPane();
        graphPanels_TabbedPane = new javax.swing.JTabbedPane();
        concordiaLayeredPane = new javax.swing.JLayeredPane();
        concordiaToolPanel = new javax.swing.JPanel();
        zoomInX2_button =  new ET_JButton();
        zoomOutX2_button =  new ET_JButton();
        resetGraphDisplay_button =  new ET_JButton();
        ellipseCenters_checkbox = new javax.swing.JCheckBox();
        ellipseLabels_checkbox = new javax.swing.JCheckBox();
        concordiaErrors_checkbox = new javax.swing.JCheckBox();
        pan_toggleButton =  new ET_JToggleButton();
        zoomBox_toggleButton =  new ET_JToggleButton();
        concordiaFlavor_radioButton = new javax.swing.JRadioButton();
        terraWasserburgFlavor_radioButton = new javax.swing.JRadioButton();
        thoriumCorrectionSelector_checkbox = new javax.swing.JCheckBox();
        protactiniumCorrectionSelector_checkbox = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
        showTight_toggleButton =  new ET_JToggleButton();
        showExcludedFractions_checkbox = new javax.swing.JCheckBox();
        thoriumConcordiaFlavor_radioButton = new javax.swing.JRadioButton();
        useUncertaintyCrosses_checkbox = new javax.swing.JCheckBox();
        commonLeadCorrectionSelector_checkbox = new javax.swing.JCheckBox();
        weightedMeanLayeredPane = new javax.swing.JLayeredPane();
        weightedMeanToolPanel = new javax.swing.JPanel();
        zoomInX2_WeightedMean_button =  new ET_JButton();
        zoomOutX2_WeightedMean_button =  new ET_JButton();
        restoreGraphDisplay_WeightedMean_button =  new ET_JButton();
        pan_WeightedMean_toggleButton =  new ET_JToggleButton();
        jLabel1 = new javax.swing.JLabel();
        fractionOrderByName_radioButton = new javax.swing.JRadioButton();
        fractionOrderByWeight_radioButton = new javax.swing.JRadioButton();
        fractionOrderByRandom_radioButton = new javax.swing.JRadioButton();
        fractionOrderByDate_radioButton = new javax.swing.JRadioButton();
        any2LayeredPane = new javax.swing.JLayeredPane();
        any2ToolPanel = new javax.swing.JPanel();
        zoomInAny2X2_button = new javax.swing.JButton();
        zoomOutAny2X2_button = new javax.swing.JButton();
        resetGraphAny2Display_button = new javax.swing.JButton();
        ellipseCentersAny2OnToggle_checkbox = new javax.swing.JCheckBox();
        ellipseLabelsAny2OnToggle_checkbox = new javax.swing.JCheckBox();
        panAny2_toggleButton = new javax.swing.JToggleButton();
        zoomBoxAny2_toggleButton = new javax.swing.JToggleButton();
        selectAny2Axes_toggleButton = new javax.swing.JToggleButton();
        any3LayeredPane = new javax.swing.JLayeredPane();
        normedProbabilityLayeredPane = new javax.swing.JLayeredPane();
        probabilityToolPanel = new javax.swing.JPanel();
        zoomInProbability_button =  new ET_JButton();
        zoomOutProbability_button =  new ET_JButton();
        resetGraphProbability_button =  new ET_JButton();
        ageR207_206r_radioB = new javax.swing.JRadioButton();
        ageR206_238r_radioB = new javax.swing.JRadioButton();
        positivePctDiscordance_slider = new javax.swing.JSlider();
        percentUncertainty_slider = new javax.swing.JSlider();
        negativePctDiscordance_slider = new javax.swing.JSlider();
        positivePctDiscordance_text = new javax.swing.JTextField();
        negativePctDiscordance_text = new javax.swing.JTextField();
        pctUncertainty_text = new javax.swing.JTextField();
        ageBest_radio = new javax.swing.JRadioButton();
        linkedUnlinkedDiscordance = new javax.swing.JButton();
        showHistogram_button = new javax.swing.JButton();
        histogramBinCount_spinner = new ReduxSpinner();
        lockUnlockHistogramBins = new javax.swing.JButton();
        showTightGraphProbability_button =  new ET_JButton();
        binWidth_text = new org.earthtime.UPb_Redux.beans.ReduxTextField();
        jLabel2 = new javax.swing.JLabel();
        commonLeadCorrectionSelectorPDF_checkbox = new javax.swing.JCheckBox();
        DatePbCorrSchemeA_radio = new javax.swing.JRadioButton();
        jPanel1 = new javax.swing.JPanel();
        writeConcordiaPDF_button =  new ET_JButton();
        close_button =  new ET_JButton();
        writeWeightedMeanPDF_button =  new ET_JButton();
        writeProbabilityDensityPDF_button =  new ET_JButton();
        sampleAgeGUIMenuBar = new javax.swing.JMenuBar();
        concordiaOptions_menu = new javax.swing.JMenu();
        sampleConcordiaOptions_menuItem = new javax.swing.JMenuItem();
        aliquotsChooser_menuItem = new javax.swing.JMenuItem();
        aliquotSpecificOptions_menu = new javax.swing.JMenu();
        weightedMeansPlotOptions_menu = new javax.swing.JMenu();
        weightedMeansChooser_menuItem = new javax.swing.JMenuItem();
        weightedMeansLookAndFeel_menuItem = new javax.swing.JMenuItem();
        choosePDFPeaks_menu = new javax.swing.JMenu();
        heatMap_Menu = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Sample Date Interpretations");
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                sampleDateWindowResized(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                sampleDateInterpretationsClosing(evt);
            }
        });

        interpretations_SplitPane.setDividerLocation(250);

        dateTrees_tabs.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        dateTrees_tabs.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dateTrees_tabsMouseClicked(evt);
            }
        });
        dateTrees_tabs.addTab("by Aliquot", dateTreeByAliquot_ScrollPane);
        dateTrees_tabs.addTab("by Sample", dateTreeBySample_ScrollPane);

        interpretations_SplitPane.setLeftComponent(dateTrees_tabs);

        graphPanels_TabbedPane.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        graphPanels_TabbedPane.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        graphPanels_TabbedPane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                graphViewTabChanged(evt);
            }
        });
        graphPanels_TabbedPane.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                graphPanelsTabbedPaneResized(evt);
            }
        });

        concordiaLayeredPane.setBackground(new java.awt.Color(255, 241, 230));
        concordiaLayeredPane.setOpaque(true);

        concordiaToolPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        concordiaToolPanel.setMinimumSize(new java.awt.Dimension(0, 0));
        concordiaToolPanel.setOpaque(false);
        concordiaToolPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        zoomInX2_button.setBackground(new java.awt.Color(255, 255, 255));
        zoomInX2_button.setFont(new java.awt.Font("Braggadocio", 1, 24)); // NOI18N
        zoomInX2_button.setText("+");
        zoomInX2_button.setAlignmentY(0.0F);
        zoomInX2_button.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        zoomInX2_button.setContentAreaFilled(false);
        zoomInX2_button.setFocusable(false);
        zoomInX2_button.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        zoomInX2_button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        zoomInX2_button.setOpaque(true);
        zoomInX2_button.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        zoomInX2_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoomInX2_buttonActionPerformed(evt);
            }
        });
        concordiaToolPanel.add(zoomInX2_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 2, 30, 30));

        zoomOutX2_button.setBackground(new java.awt.Color(255, 255, 255));
        zoomOutX2_button.setFont(new java.awt.Font("Braggadocio", 1, 24)); // NOI18N
        zoomOutX2_button.setText("-");
        zoomOutX2_button.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        zoomOutX2_button.setContentAreaFilled(false);
        zoomOutX2_button.setOpaque(true);
        concordiaToolPanel.add(zoomOutX2_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 2, 30, 30));

        resetGraphDisplay_button.setBackground(new java.awt.Color(255, 255, 255));
        resetGraphDisplay_button.setFont(new java.awt.Font("SansSerif", 1, 9)); // NOI18N
        resetGraphDisplay_button.setText("Reset");
        resetGraphDisplay_button.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        resetGraphDisplay_button.setContentAreaFilled(false);
        resetGraphDisplay_button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        resetGraphDisplay_button.setOpaque(true);
        resetGraphDisplay_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetGraphDisplay_buttonActionPerformed(evt);
            }
        });
        concordiaToolPanel.add(resetGraphDisplay_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 2, 35, 30));

        ellipseCenters_checkbox.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        ellipseCenters_checkbox.setSelected(true);
        ellipseCenters_checkbox.setText("<html>Ellipse<br> Centers</html>");
        ellipseCenters_checkbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ellipseCenters_checkboxActionPerformed(evt);
            }
        });
        concordiaToolPanel.add(ellipseCenters_checkbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 2, 70, 25));

        ellipseLabels_checkbox.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        ellipseLabels_checkbox.setSelected(true);
        ellipseLabels_checkbox.setText("<html>Ellipse<br> Labels</html>");
        ellipseLabels_checkbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ellipseLabels_checkboxActionPerformed(evt);
            }
        });
        concordiaToolPanel.add(ellipseLabels_checkbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 2, 70, 25));

        concordiaErrors_checkbox.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        concordiaErrors_checkbox.setSelected(true);
        concordiaErrors_checkbox.setText("<html>Concordia<br> Unct Env</html>");
        concordiaErrors_checkbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                concordiaErrors_checkboxActionPerformed(evt);
            }
        });
        concordiaToolPanel.add(concordiaErrors_checkbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 2, 77, 25));

        pan_toggleButton.setBackground(new java.awt.Color(255, 255, 255));
        concordiaPanZoom_buttonGroup.add(pan_toggleButton);
        pan_toggleButton.setFont(new java.awt.Font("SansSerif", 1, 9)); // NOI18N
        pan_toggleButton.setText("Pan");
        pan_toggleButton.setToolTipText("Use mouse to pan image");
        pan_toggleButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pan_toggleButton.setContentAreaFilled(false);
        pan_toggleButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        pan_toggleButton.setName("PAN"); // NOI18N
        pan_toggleButton.setOpaque(true);
        pan_toggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pan_toggleButtonActionPerformed(evt);
            }
        });
        concordiaToolPanel.add(pan_toggleButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(95, 2, 30, 30));

        zoomBox_toggleButton.setBackground(new java.awt.Color(255, 255, 255));
        concordiaPanZoom_buttonGroup.add(zoomBox_toggleButton);
        zoomBox_toggleButton.setFont(new java.awt.Font("SansSerif", 1, 9)); // NOI18N
        zoomBox_toggleButton.setText("Zoom-Box");
        zoomBox_toggleButton.setToolTipText("Use mouse to define zoom-to box");
        zoomBox_toggleButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        zoomBox_toggleButton.setContentAreaFilled(false);
        zoomBox_toggleButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        zoomBox_toggleButton.setName("ZOOM"); // NOI18N
        zoomBox_toggleButton.setOpaque(true);
        zoomBox_toggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoomBox_toggleButtonActionPerformed(evt);
            }
        });
        concordiaToolPanel.add(zoomBox_toggleButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(125, 2, 55, 30));

        concordiaFlavor_radioButton.setBackground(new java.awt.Color(204, 204, 255));
        concordiaTeraW_buttonGroup.add(concordiaFlavor_radioButton);
        concordiaFlavor_radioButton.setFont(new java.awt.Font("SansSerif", 1, 9)); // NOI18N
        concordiaFlavor_radioButton.setText("C");
        concordiaFlavor_radioButton.setToolTipText("Wetherill Concordia");
        concordiaFlavor_radioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                concordiaFlavor_radioButtonActionPerformed(evt);
            }
        });
        concordiaToolPanel.add(concordiaFlavor_radioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 1, -1, 28));

        terraWasserburgFlavor_radioButton.setBackground(new java.awt.Color(204, 204, 255));
        concordiaTeraW_buttonGroup.add(terraWasserburgFlavor_radioButton);
        terraWasserburgFlavor_radioButton.setFont(new java.awt.Font("SansSerif", 1, 9)); // NOI18N
        terraWasserburgFlavor_radioButton.setText("TW");
        terraWasserburgFlavor_radioButton.setToolTipText("Terra-Wasserburg");
        terraWasserburgFlavor_radioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                terraWasserburgFlavor_radioButtonActionPerformed(evt);
            }
        });
        concordiaToolPanel.add(terraWasserburgFlavor_radioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(295, 1, -1, 28));

        thoriumCorrectionSelector_checkbox.setFont(new java.awt.Font("SansSerif", 1, 9)); // NOI18N
        thoriumCorrectionSelector_checkbox.setText("Th");
        thoriumCorrectionSelector_checkbox.setToolTipText("Correct for Thorium");
        thoriumCorrectionSelector_checkbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                thoriumCorrectionSelector_checkboxActionPerformed(evt);
            }
        });
        concordiaToolPanel.add(thoriumCorrectionSelector_checkbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(435, 2, -1, 25));

        protactiniumCorrectionSelector_checkbox.setFont(new java.awt.Font("SansSerif", 1, 9)); // NOI18N
        protactiniumCorrectionSelector_checkbox.setText("Pa");
        protactiniumCorrectionSelector_checkbox.setToolTipText("Correct for Protactinium");
        protactiniumCorrectionSelector_checkbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                protactiniumCorrectionSelector_checkboxActionPerformed(evt);
            }
        });
        concordiaToolPanel.add(protactiniumCorrectionSelector_checkbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 2, -1, 25));

        jLabel4.setFont(new java.awt.Font("SansSerif", 1, 9)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 0, 51));
        jLabel4.setText("Correct:");
        concordiaToolPanel.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(350, 2, -1, 25));

        showTight_toggleButton.setBackground(new java.awt.Color(255, 255, 255));
        concordiaPanZoom_buttonGroup.add(showTight_toggleButton);
        showTight_toggleButton.setFont(new java.awt.Font("SansSerif", 1, 9)); // NOI18N
        showTight_toggleButton.setText("Tight");
        showTight_toggleButton.setToolTipText("Zooms in until ellipses touch borders.");
        showTight_toggleButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        showTight_toggleButton.setContentAreaFilled(false);
        showTight_toggleButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        showTight_toggleButton.setName("TIGHT"); // NOI18N
        showTight_toggleButton.setOpaque(true);
        showTight_toggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showTight_toggleButtonActionPerformed(evt);
            }
        });
        concordiaToolPanel.add(showTight_toggleButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 2, 30, 30));

        showExcludedFractions_checkbox.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        showExcludedFractions_checkbox.setSelected(true);
        showExcludedFractions_checkbox.setText("<html>Excluded<br> Fractions</html>");
        showExcludedFractions_checkbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showExcludedFractions_checkboxActionPerformed(evt);
            }
        });
        concordiaToolPanel.add(showExcludedFractions_checkbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 2, 75, 25));

        thoriumConcordiaFlavor_radioButton.setBackground(new java.awt.Color(204, 204, 255));
        concordiaTeraW_buttonGroup.add(thoriumConcordiaFlavor_radioButton);
        thoriumConcordiaFlavor_radioButton.setFont(new java.awt.Font("SansSerif", 1, 9)); // NOI18N
        thoriumConcordiaFlavor_radioButton.setText("Th");
        thoriumConcordiaFlavor_radioButton.setToolTipText("Thorium-based Wetherill Concordia");
        thoriumConcordiaFlavor_radioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                thoriumConcordiaFlavor_radioButtonActionPerformed(evt);
            }
        });
        concordiaToolPanel.add(thoriumConcordiaFlavor_radioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 1, -1, 28));

        useUncertaintyCrosses_checkbox.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        useUncertaintyCrosses_checkbox.setText("<html>use<br> Crosses</html>");
        useUncertaintyCrosses_checkbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useUncertaintyCrosses_checkboxActionPerformed(evt);
            }
        });
        concordiaToolPanel.add(useUncertaintyCrosses_checkbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 2, 70, 25));

        commonLeadCorrectionSelector_checkbox.setFont(new java.awt.Font("SansSerif", 1, 9)); // NOI18N
        commonLeadCorrectionSelector_checkbox.setText("Pbc");
        commonLeadCorrectionSelector_checkbox.setToolTipText("Correct for Common Lead");
        commonLeadCorrectionSelector_checkbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                commonLeadCorrectionSelector_checkboxActionPerformed(evt);
            }
        });
        concordiaToolPanel.add(commonLeadCorrectionSelector_checkbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 2, -1, 25));

        concordiaLayeredPane.add(concordiaToolPanel);
        concordiaToolPanel.setBounds(0, 605, 920, 36);

        graphPanels_TabbedPane.addTab("Concordia", concordiaLayeredPane);

        weightedMeanLayeredPane.setBackground(new java.awt.Color(229, 250, 229));
        weightedMeanLayeredPane.setOpaque(true);

        weightedMeanToolPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        weightedMeanToolPanel.setOpaque(false);
        weightedMeanToolPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        zoomInX2_WeightedMean_button.setBackground(new java.awt.Color(255, 255, 255));
        zoomInX2_WeightedMean_button.setFont(new java.awt.Font("Braggadocio", 1, 24)); // NOI18N
        zoomInX2_WeightedMean_button.setText("+");
        zoomInX2_WeightedMean_button.setAlignmentY(0.0F);
        zoomInX2_WeightedMean_button.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        zoomInX2_WeightedMean_button.setFocusable(false);
        zoomInX2_WeightedMean_button.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        zoomInX2_WeightedMean_button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        zoomInX2_WeightedMean_button.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        weightedMeanToolPanel.add(zoomInX2_WeightedMean_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 2, 30, 30));

        zoomOutX2_WeightedMean_button.setBackground(new java.awt.Color(255, 255, 255));
        zoomOutX2_WeightedMean_button.setFont(new java.awt.Font("Braggadocio", 1, 24)); // NOI18N
        zoomOutX2_WeightedMean_button.setText("-");
        zoomOutX2_WeightedMean_button.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        zoomOutX2_WeightedMean_button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        zoomOutX2_WeightedMean_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoomOutX2_WeightedMean_buttonActionPerformed(evt);
            }
        });
        weightedMeanToolPanel.add(zoomOutX2_WeightedMean_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(32, 2, 30, 30));

        restoreGraphDisplay_WeightedMean_button.setBackground(new java.awt.Color(255, 255, 255));
        restoreGraphDisplay_WeightedMean_button.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        restoreGraphDisplay_WeightedMean_button.setText("Reset");
        restoreGraphDisplay_WeightedMean_button.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        restoreGraphDisplay_WeightedMean_button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        restoreGraphDisplay_WeightedMean_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                restoreGraphDisplay_WeightedMean_buttonActionPerformed(evt);
            }
        });
        weightedMeanToolPanel.add(restoreGraphDisplay_WeightedMean_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(62, 2, 51, 30));

        pan_WeightedMean_toggleButton.setBackground(new java.awt.Color(255, 255, 255));
        weightedMeanPanZoom_buttonGroup.add(pan_WeightedMean_toggleButton);
        pan_WeightedMean_toggleButton.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        pan_WeightedMean_toggleButton.setSelected(true);
        pan_WeightedMean_toggleButton.setText("Pan");
        pan_WeightedMean_toggleButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pan_WeightedMean_toggleButton.setName("PAN"); // NOI18N
        pan_WeightedMean_toggleButton.setOpaque(true);
        weightedMeanToolPanel.add(pan_WeightedMean_toggleButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(113, 2, 28, 30));

        jLabel1.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel1.setText("Order:");
        weightedMeanToolPanel.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(369, 9, -1, -1));

        weightedMeanFractionOrderButtonGroup.add(fractionOrderByName_radioButton);
        fractionOrderByName_radioButton.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        fractionOrderByName_radioButton.setSelected(true);
        fractionOrderByName_radioButton.setText("name");
        fractionOrderByName_radioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        fractionOrderByName_radioButton.setName("name"); // NOI18N
        weightedMeanToolPanel.add(fractionOrderByName_radioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(412, 6, -1, -1));

        weightedMeanFractionOrderButtonGroup.add(fractionOrderByWeight_radioButton);
        fractionOrderByWeight_radioButton.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        fractionOrderByWeight_radioButton.setText("weight");
        fractionOrderByWeight_radioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        fractionOrderByWeight_radioButton.setName("weight"); // NOI18N
        weightedMeanToolPanel.add(fractionOrderByWeight_radioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(476, 6, -1, -1));

        weightedMeanFractionOrderButtonGroup.add(fractionOrderByRandom_radioButton);
        fractionOrderByRandom_radioButton.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        fractionOrderByRandom_radioButton.setText("RND");
        fractionOrderByRandom_radioButton.setActionCommand("random");
        fractionOrderByRandom_radioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        fractionOrderByRandom_radioButton.setName("random"); // NOI18N
        weightedMeanToolPanel.add(fractionOrderByRandom_radioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(555, 6, -1, -1));

        weightedMeanFractionOrderButtonGroup.add(fractionOrderByDate_radioButton);
        fractionOrderByDate_radioButton.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        fractionOrderByDate_radioButton.setText("date");
        fractionOrderByDate_radioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        fractionOrderByDate_radioButton.setName("date"); // NOI18N
        fractionOrderByDate_radioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fractionOrderByDate_radioButtonActionPerformed(evt);
            }
        });
        weightedMeanToolPanel.add(fractionOrderByDate_radioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 6, -1, -1));

        weightedMeanLayeredPane.add(weightedMeanToolPanel);
        weightedMeanToolPanel.setBounds(1, 605, 910, 36);

        graphPanels_TabbedPane.addTab("Weighted Mean", weightedMeanLayeredPane);

        any2ToolPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        any2ToolPanel.setOpaque(false);

        zoomInAny2X2_button.setFont(new java.awt.Font("Braggadocio", 1, 24)); // NOI18N
        zoomInAny2X2_button.setText("+");
        zoomInAny2X2_button.setAlignmentY(0.0F);
        zoomInAny2X2_button.setFocusable(false);
        zoomInAny2X2_button.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        zoomInAny2X2_button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        zoomInAny2X2_button.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        zoomInAny2X2_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoomInAny2X2_buttonActionPerformed(evt);
            }
        });

        zoomOutAny2X2_button.setFont(new java.awt.Font("Braggadocio", 1, 24)); // NOI18N
        zoomOutAny2X2_button.setText("-");

        resetGraphAny2Display_button.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        resetGraphAny2Display_button.setText("Reset");
        resetGraphAny2Display_button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        resetGraphAny2Display_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetGraphAny2Display_buttonActionPerformed(evt);
            }
        });

        ellipseCentersAny2OnToggle_checkbox.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        ellipseCentersAny2OnToggle_checkbox.setSelected(true);
        ellipseCentersAny2OnToggle_checkbox.setText("Ellipse Centers");
        ellipseCentersAny2OnToggle_checkbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ellipseCentersAny2OnToggle_checkboxActionPerformed(evt);
            }
        });

        ellipseLabelsAny2OnToggle_checkbox.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        ellipseLabelsAny2OnToggle_checkbox.setSelected(true);
        ellipseLabelsAny2OnToggle_checkbox.setText("Labels");
        ellipseLabelsAny2OnToggle_checkbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ellipseLabelsAny2OnToggle_checkboxActionPerformed(evt);
            }
        });

        concordiaPanZoom_buttonGroup.add(panAny2_toggleButton);
        panAny2_toggleButton.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        panAny2_toggleButton.setText("Pan");
        panAny2_toggleButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        panAny2_toggleButton.setName("PAN"); // NOI18N
        panAny2_toggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                panAny2_toggleButtonActionPerformed(evt);
            }
        });

        concordiaPanZoom_buttonGroup.add(zoomBoxAny2_toggleButton);
        zoomBoxAny2_toggleButton.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        zoomBoxAny2_toggleButton.setText("Z-Box");
        zoomBoxAny2_toggleButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        zoomBoxAny2_toggleButton.setName("ZOOM"); // NOI18N
        zoomBoxAny2_toggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoomBoxAny2_toggleButtonActionPerformed(evt);
            }
        });

        concordiaPanZoom_buttonGroup.add(selectAny2Axes_toggleButton);
        selectAny2Axes_toggleButton.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        selectAny2Axes_toggleButton.setText("Select Any 2 Axes");
        selectAny2Axes_toggleButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        selectAny2Axes_toggleButton.setName("ZOOM"); // NOI18N
        selectAny2Axes_toggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAny2Axes_toggleButtonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout any2ToolPanelLayout = new org.jdesktop.layout.GroupLayout(any2ToolPanel);
        any2ToolPanel.setLayout(any2ToolPanelLayout);
        any2ToolPanelLayout.setHorizontalGroup(
            any2ToolPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(any2ToolPanelLayout.createSequentialGroup()
                .add(zoomInAny2X2_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 57, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(zoomOutAny2X2_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 57, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(resetGraphAny2Display_button)
                .add(2, 2, 2)
                .add(panAny2_toggleButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 70, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(zoomBoxAny2_toggleButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 79, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(selectAny2Axes_toggleButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                .add(18, 18, 18)
                .add(ellipseCentersAny2OnToggle_checkbox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 117, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(ellipseLabelsAny2OnToggle_checkbox)
                .add(34, 34, 34))
        );
        any2ToolPanelLayout.setVerticalGroup(
            any2ToolPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(any2ToolPanelLayout.createSequentialGroup()
                .add(any2ToolPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(zoomInAny2X2_button)
                    .add(zoomOutAny2X2_button)
                    .add(resetGraphAny2Display_button)
                    .add(panAny2_toggleButton)
                    .add(zoomBoxAny2_toggleButton)
                    .add(ellipseCentersAny2OnToggle_checkbox)
                    .add(ellipseLabelsAny2OnToggle_checkbox)
                    .add(selectAny2Axes_toggleButton))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        any2LayeredPane.add(any2ToolPanel);
        any2ToolPanel.setBounds(0, 605, 790, 36);

        graphPanels_TabbedPane.addTab("Any 2", any2LayeredPane);
        graphPanels_TabbedPane.addTab("Any 3", any3LayeredPane);

        normedProbabilityLayeredPane.setBackground(new java.awt.Color(241, 230, 255));
        normedProbabilityLayeredPane.setOpaque(true);

        probabilityToolPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        probabilityToolPanel.setOpaque(false);
        probabilityToolPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        zoomInProbability_button.setBackground(new java.awt.Color(255, 255, 255));
        zoomInProbability_button.setFont(new java.awt.Font("Braggadocio", 1, 24)); // NOI18N
        zoomInProbability_button.setText("+");
        zoomInProbability_button.setToolTipText("Click to Zoom IN.");
        zoomInProbability_button.setAlignmentY(0.0F);
        zoomInProbability_button.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        zoomInProbability_button.setContentAreaFilled(false);
        zoomInProbability_button.setFocusable(false);
        zoomInProbability_button.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        zoomInProbability_button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        zoomInProbability_button.setOpaque(true);
        zoomInProbability_button.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        zoomInProbability_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoomInProbability_buttonActionPerformed(evt);
            }
        });
        probabilityToolPanel.add(zoomInProbability_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 40, 30, 25));

        zoomOutProbability_button.setBackground(new java.awt.Color(255, 255, 255));
        zoomOutProbability_button.setFont(new java.awt.Font("Braggadocio", 1, 24)); // NOI18N
        zoomOutProbability_button.setText("-");
        zoomOutProbability_button.setToolTipText("Click to Zoom OUT.");
        zoomOutProbability_button.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        zoomOutProbability_button.setContentAreaFilled(false);
        zoomOutProbability_button.setOpaque(true);
        zoomOutProbability_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoomOutProbability_buttonActionPerformed(evt);
            }
        });
        probabilityToolPanel.add(zoomOutProbability_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 40, 30, 25));

        resetGraphProbability_button.setBackground(new java.awt.Color(255, 255, 255));
        resetGraphProbability_button.setFont(new java.awt.Font("Helvetica", 1, 10)); // NOI18N
        resetGraphProbability_button.setText("Reset");
        resetGraphProbability_button.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        resetGraphProbability_button.setContentAreaFilled(false);
        resetGraphProbability_button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        resetGraphProbability_button.setOpaque(true);
        resetGraphProbability_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetGraphProbability_buttonActionPerformed(evt);
            }
        });
        probabilityToolPanel.add(resetGraphProbability_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 40, 40, 25));

        probabilityDateButtonGroup.add(ageR207_206r_radioB);
        ageR207_206r_radioB.setFont(new java.awt.Font("Arial", 1, 10)); // NOI18N
        ageR207_206r_radioB.setText("207/206");
        ageR207_206r_radioB.setName("age207_206r"); // NOI18N
        probabilityToolPanel.add(ageR207_206r_radioB, new org.netbeans.lib.awtextra.AbsoluteConstraints(175, 20, -1, -1));

        probabilityDateButtonGroup.add(ageR206_238r_radioB);
        ageR206_238r_radioB.setFont(new java.awt.Font("Arial", 1, 10)); // NOI18N
        ageR206_238r_radioB.setText("206/238");
        ageR206_238r_radioB.setName("age206_238r"); // NOI18N
        probabilityToolPanel.add(ageR206_238r_radioB, new org.netbeans.lib.awtextra.AbsoluteConstraints(175, 0, -1, -1));

        positivePctDiscordance_slider.setFont(new java.awt.Font("Arial", 1, 10)); // NOI18N
        positivePctDiscordance_slider.setMajorTickSpacing(10);
        positivePctDiscordance_slider.setMinorTickSpacing(2);
        positivePctDiscordance_slider.setPaintLabels(true);
        positivePctDiscordance_slider.setPaintTicks(true);
        positivePctDiscordance_slider.setSnapToTicks(true);
        positivePctDiscordance_slider.setAutoscrolls(true);
        positivePctDiscordance_slider.setName("positivePerCentDiscordanceSliderValue"); // NOI18N
        probabilityToolPanel.add(positivePctDiscordance_slider, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 0, 200, 38));

        percentUncertainty_slider.setFont(new java.awt.Font("Arial", 1, 10)); // NOI18N
        percentUncertainty_slider.setMajorTickSpacing(10);
        percentUncertainty_slider.setMinorTickSpacing(2);
        percentUncertainty_slider.setPaintLabels(true);
        percentUncertainty_slider.setPaintTicks(true);
        percentUncertainty_slider.setSnapToTicks(true);
        percentUncertainty_slider.setAutoscrolls(true);
        percentUncertainty_slider.setName("uncertaintyPerCentSliderValue"); // NOI18N
        probabilityToolPanel.add(percentUncertainty_slider, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 0, 168, 38));

        negativePctDiscordance_slider.setFont(new java.awt.Font("Arial", 1, 10)); // NOI18N
        negativePctDiscordance_slider.setMajorTickSpacing(10);
        negativePctDiscordance_slider.setMaximum(0);
        negativePctDiscordance_slider.setMinimum(-100);
        negativePctDiscordance_slider.setMinorTickSpacing(2);
        negativePctDiscordance_slider.setPaintLabels(true);
        negativePctDiscordance_slider.setPaintTicks(true);
        negativePctDiscordance_slider.setSnapToTicks(true);
        negativePctDiscordance_slider.setValue(-50);
        negativePctDiscordance_slider.setName("negativePerCentDiscordanceSliderValue"); // NOI18N
        probabilityToolPanel.add(negativePctDiscordance_slider, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 0, 200, 38));

        positivePctDiscordance_text.setBackground(new java.awt.Color(241, 230, 255));
        positivePctDiscordance_text.setFont(new java.awt.Font("Arial", 1, 10)); // NOI18N
        positivePctDiscordance_text.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        positivePctDiscordance_text.setText("Positive % discordance");
        positivePctDiscordance_text.setAlignmentX(0.0F);
        positivePctDiscordance_text.setAlignmentY(0.0F);
        positivePctDiscordance_text.setBorder(null);
        probabilityToolPanel.add(positivePctDiscordance_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(568, 40, 160, 15));

        negativePctDiscordance_text.setBackground(new java.awt.Color(241, 230, 255));
        negativePctDiscordance_text.setFont(new java.awt.Font("Arial", 1, 10)); // NOI18N
        negativePctDiscordance_text.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        negativePctDiscordance_text.setText("Negative % discordance");
        negativePctDiscordance_text.setAlignmentX(0.0F);
        negativePctDiscordance_text.setAlignmentY(0.0F);
        negativePctDiscordance_text.setBorder(null);
        probabilityToolPanel.add(negativePctDiscordance_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(347, 40, 160, 15));

        pctUncertainty_text.setBackground(new java.awt.Color(241, 230, 255));
        pctUncertainty_text.setFont(new java.awt.Font("Arial", 1, 10)); // NOI18N
        pctUncertainty_text.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        pctUncertainty_text.setText("% uncertainty");
        pctUncertainty_text.setAlignmentX(0.0F);
        pctUncertainty_text.setAlignmentY(0.0F);
        pctUncertainty_text.setBorder(null);
        probabilityToolPanel.add(pctUncertainty_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(767, 40, 140, 15));

        probabilityDateButtonGroup.add(ageBest_radio);
        ageBest_radio.setFont(new java.awt.Font("Arial", 1, 10)); // NOI18N
        ageBest_radio.setText("best");
        ageBest_radio.setActionCommand("bestAge");
        ageBest_radio.setName("bestAge"); // NOI18N
        probabilityToolPanel.add(ageBest_radio, new org.netbeans.lib.awtextra.AbsoluteConstraints(175, 40, -1, -1));

        linkedUnlinkedDiscordance.setBackground(new java.awt.Color(241, 230, 255));
        linkedUnlinkedDiscordance.setFont(new java.awt.Font("Braggadocio", 1, 24)); // NOI18N
        linkedUnlinkedDiscordance.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/earthtime/images/unlinked.png"))); // NOI18N
        linkedUnlinkedDiscordance.setToolTipText("Click to Unlock sliders.");
        linkedUnlinkedDiscordance.setAlignmentY(0.0F);
        linkedUnlinkedDiscordance.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        linkedUnlinkedDiscordance.setContentAreaFilled(false);
        linkedUnlinkedDiscordance.setDoubleBuffered(true);
        linkedUnlinkedDiscordance.setFocusable(false);
        linkedUnlinkedDiscordance.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        linkedUnlinkedDiscordance.setMargin(new java.awt.Insets(0, 0, 0, 0));
        linkedUnlinkedDiscordance.setOpaque(true);
        linkedUnlinkedDiscordance.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/earthtime/images/linked.png"))); // NOI18N
        linkedUnlinkedDiscordance.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        linkedUnlinkedDiscordance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                linkedUnlinkedDiscordanceActionPerformed(evt);
            }
        });
        probabilityToolPanel.add(linkedUnlinkedDiscordance, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 0, 20, 20));

        showHistogram_button.setBackground(new java.awt.Color(241, 230, 255));
        showHistogram_button.setFont(new java.awt.Font("Braggadocio", 1, 24)); // NOI18N
        showHistogram_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/earthtime/images/noHistogram.png"))); // NOI18N
        showHistogram_button.setToolTipText("Click to Hide Histogram.\"");
        showHistogram_button.setAlignmentY(0.0F);
        showHistogram_button.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        showHistogram_button.setContentAreaFilled(false);
        showHistogram_button.setDoubleBuffered(true);
        showHistogram_button.setFocusable(false);
        showHistogram_button.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        showHistogram_button.setIconTextGap(0);
        showHistogram_button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        showHistogram_button.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/earthtime/images/histogram.png"))); // NOI18N
        showHistogram_button.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        showHistogram_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showHistogram_buttonActionPerformed(evt);
            }
        });
        probabilityToolPanel.add(showHistogram_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 15, 25, 20));

        histogramBinCount_spinner.setFont(histogramBinCount_spinner.getFont().deriveFont(histogramBinCount_spinner.getFont().getStyle() | java.awt.Font.BOLD, 10));
        histogramBinCount_spinner.setToolTipText("Adjust number of histogram bins for this view.");
        probabilityToolPanel.add(histogramBinCount_spinner, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 15, 57, 20));

        lockUnlockHistogramBins.setBackground(new java.awt.Color(241, 230, 255));
        lockUnlockHistogramBins.setFont(new java.awt.Font("Braggadocio", 1, 24)); // NOI18N
        lockUnlockHistogramBins.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/earthtime/images/unlinked.png"))); // NOI18N
        lockUnlockHistogramBins.setToolTipText("Click to Freeze Histogram bin width.");
        lockUnlockHistogramBins.setAlignmentY(0.0F);
        lockUnlockHistogramBins.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        lockUnlockHistogramBins.setContentAreaFilled(false);
        lockUnlockHistogramBins.setDoubleBuffered(true);
        lockUnlockHistogramBins.setFocusable(false);
        lockUnlockHistogramBins.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lockUnlockHistogramBins.setMargin(new java.awt.Insets(0, 0, 0, 0));
        lockUnlockHistogramBins.setOpaque(true);
        lockUnlockHistogramBins.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/earthtime/images/linked.png"))); // NOI18N
        lockUnlockHistogramBins.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        lockUnlockHistogramBins.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lockUnlockHistogramBinsMouseEntered(evt);
            }
        });
        lockUnlockHistogramBins.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lockUnlockHistogramBinsActionPerformed(evt);
            }
        });
        probabilityToolPanel.add(lockUnlockHistogramBins, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 15, 20, 20));

        showTightGraphProbability_button.setBackground(new java.awt.Color(255, 255, 255));
        showTightGraphProbability_button.setFont(new java.awt.Font("Helvetica", 1, 10)); // NOI18N
        showTightGraphProbability_button.setText("Show Tight");
        showTightGraphProbability_button.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        showTightGraphProbability_button.setContentAreaFilled(false);
        showTightGraphProbability_button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        showTightGraphProbability_button.setOpaque(true);
        probabilityToolPanel.add(showTightGraphProbability_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 40, 60, 25));

        binWidth_text.setFont(new java.awt.Font("Helvetica", 1, 12)); // NOI18N
        binWidth_text.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        binWidth_text.setText("0");
        probabilityToolPanel.add(binWidth_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(90, 15, 50, 20));

        jLabel2.setFont(new java.awt.Font("Helvetica", 1, 10)); // NOI18N
        jLabel2.setText(" ?Hist    #Bins      ?WidthMa  ?Lock");
        probabilityToolPanel.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 170, 15));

        commonLeadCorrectionSelectorPDF_checkbox.setFont(new java.awt.Font("SansSerif", 1, 9)); // NOI18N
        commonLeadCorrectionSelectorPDF_checkbox.setText("PbcCorr");
        commonLeadCorrectionSelectorPDF_checkbox.setToolTipText("Correct for Common Lead");
        commonLeadCorrectionSelectorPDF_checkbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                commonLeadCorrectionSelectorPDF_checkboxActionPerformed(evt);
            }
        });
        probabilityToolPanel.add(commonLeadCorrectionSelectorPDF_checkbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 2, -1, 25));

        probabilityDateButtonGroup.add(DatePbCorrSchemeA_radio);
        DatePbCorrSchemeA_radio.setFont(new java.awt.Font("Arial", 1, 10)); // NOI18N
        DatePbCorrSchemeA_radio.setText("<html>UPb Date PbcCorr</html>");
        DatePbCorrSchemeA_radio.setActionCommand("bestAge");
        DatePbCorrSchemeA_radio.setName("PbcCorr_UPb_Date"); // NOI18N
        probabilityToolPanel.add(DatePbCorrSchemeA_radio, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 33, 80, 30));

        normedProbabilityLayeredPane.add(probabilityToolPanel);
        probabilityToolPanel.setBounds(0, 560, 920, 70);

        graphPanels_TabbedPane.addTab("Probability", normedProbabilityLayeredPane);

        interpretations_SplitPane.setRightComponent(graphPanels_TabbedPane);

        jPanel1.setBackground(new java.awt.Color(249, 237, 189));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        writeConcordiaPDF_button.setForeground(new java.awt.Color(255, 51, 0));
        writeConcordiaPDF_button.setText("Write Concordia SVG and PDF");
        writeConcordiaPDF_button.setAlignmentY(0.0F);
        writeConcordiaPDF_button.setPreferredSize(new java.awt.Dimension(140, 23));
        writeConcordiaPDF_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                writeConcordiaPDF_buttonActionPerformed(evt);
            }
        });

        close_button.setForeground(new java.awt.Color(255, 51, 0));
        close_button.setText("Close");
        close_button.setAlignmentY(0.0F);
        close_button.setPreferredSize(new java.awt.Dimension(140, 23));
        close_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                close_buttonActionPerformed(evt);
            }
        });

        writeWeightedMeanPDF_button.setForeground(new java.awt.Color(255, 51, 0));
        writeWeightedMeanPDF_button.setText("Write WeightedMean SVG and PDF");
        writeWeightedMeanPDF_button.setAlignmentY(0.0F);
        writeWeightedMeanPDF_button.setPreferredSize(new java.awt.Dimension(140, 23));
        writeWeightedMeanPDF_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                writeWeightedMeanPDF_buttonActionPerformed(evt);
            }
        });

        writeProbabilityDensityPDF_button.setForeground(new java.awt.Color(255, 51, 0));
        writeProbabilityDensityPDF_button.setText("Write ProbabilityDensity SVG and PDF");
        writeProbabilityDensityPDF_button.setAlignmentY(0.0F);
        writeProbabilityDensityPDF_button.setPreferredSize(new java.awt.Dimension(140, 23));
        writeProbabilityDensityPDF_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                writeProbabilityDensityPDF_buttonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(close_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 176, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(92, 92, 92)
                .add(writeConcordiaPDF_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 270, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(writeWeightedMeanPDF_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 284, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(writeProbabilityDensityPDF_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 284, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(71, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(close_button, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(writeConcordiaPDF_button, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(writeWeightedMeanPDF_button, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(writeProbabilityDensityPDF_button, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .add(17, 17, 17))
        );

        concordiaOptions_menu.setText("Concordia Plot");
        concordiaOptions_menu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                concordiaOptions_menuMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                concordiaOptions_menuMouseEntered(evt);
            }
        });

        sampleConcordiaOptions_menuItem.setText("Sample Display Options");
        sampleConcordiaOptions_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sampleConcordiaOptions_menuItemActionPerformed(evt);
            }
        });
        concordiaOptions_menu.add(sampleConcordiaOptions_menuItem);

        aliquotsChooser_menuItem.setText("Aliquots Chooser");
        aliquotsChooser_menuItem.setEnabled(false);
        concordiaOptions_menu.add(aliquotsChooser_menuItem);

        aliquotSpecificOptions_menu.setText("Aliquots Display Options");
        concordiaOptions_menu.add(aliquotSpecificOptions_menu);

        sampleAgeGUIMenuBar.add(concordiaOptions_menu);

        weightedMeansPlotOptions_menu.setText("Weighted Means Plot");

        weightedMeansChooser_menuItem.setText("Weighted Means Chooser");
        weightedMeansChooser_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                weightedMeansChooser_menuItemActionPerformed(evt);
            }
        });
        weightedMeansPlotOptions_menu.add(weightedMeansChooser_menuItem);

        weightedMeansLookAndFeel_menuItem.setText("Weighted Means Display Options");
        weightedMeansLookAndFeel_menuItem.setEnabled(false);
        weightedMeansPlotOptions_menu.add(weightedMeansLookAndFeel_menuItem);

        sampleAgeGUIMenuBar.add(weightedMeansPlotOptions_menu);

        choosePDFPeaks_menu.setText("Choose PDF Peaks");
        choosePDFPeaks_menu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                choosePDFPeaks_menuMenuSelected(evt);
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
        });
        sampleAgeGUIMenuBar.add(choosePDFPeaks_menu);

        heatMap_Menu.setText("HeatMap");
        heatMap_Menu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                heatMap_MenuMenuSelected(evt);
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
        });
        sampleAgeGUIMenuBar.add(heatMap_Menu);

        setJMenuBar(sampleAgeGUIMenuBar);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(interpretations_SplitPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1199, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(interpretations_SplitPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 683, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 31, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void resetGraphDisplay_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetGraphDisplay_buttonActionPerformed
        ((ConcordiaGraphPanel) concordiaGraphPanel).setShowTightToEdges(false);
        ((PlottingDetailsDisplayInterface) concordiaGraphPanel).resetPanel(true);
}//GEN-LAST:event_resetGraphDisplay_buttonActionPerformed
    private void writeConcordiaPDF_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_writeConcordiaPDF_buttonActionPerformed
        try {
            createConcordiaSVGandPDF();
        } catch (ETException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
}//GEN-LAST:event_writeConcordiaPDF_buttonActionPerformed
//todo clean up this code with a change listener
private void ellipseCenters_checkboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ellipseCenters_checkboxActionPerformed
    boolean state = ((ConcordiaGraphPanel) concordiaGraphPanel).isShowEllipseCenters();
    ((ConcordiaGraphPanel) concordiaGraphPanel).setShowEllipseCenters(!state);
    ellipseCenters_checkbox.setSelected(!state);

    ((ConcordiaGraphPanel) concordiaGraphPanel).getConcordiaOptions()//
            .put("showEllipseCenters", Boolean.toString(!state));

    ((ConcordiaGraphPanel) concordiaGraphPanel).repaint();
}//GEN-LAST:event_ellipseCenters_checkboxActionPerformed

private void ellipseLabels_checkboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ellipseLabels_checkboxActionPerformed
    ((ConcordiaGraphPanel) concordiaGraphPanel).setShowEllipseLabels(!((ConcordiaGraphPanel) concordiaGraphPanel).isShowEllipseLabels());
    ellipseLabels_checkbox.setSelected(((ConcordiaGraphPanel) concordiaGraphPanel).isShowEllipseLabels());

    ((AliquotDetailsDisplayInterface) concordiaGraphPanel).getConcordiaOptions()//
            .put("showEllipseLabels", Boolean.toString(((ConcordiaGraphPanel) concordiaGraphPanel).isShowEllipseLabels()));

    concordiaGraphPanel.repaint();
}//GEN-LAST:event_ellipseLabels_checkboxActionPerformed

private void concordiaErrors_checkboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_concordiaErrors_checkboxActionPerformed
    ((ConcordiaGraphPanel) concordiaGraphPanel).setShowConcordiaErrorBars(!((ConcordiaGraphPanel) concordiaGraphPanel).isShowConcordiaErrorBars());
    concordiaErrors_checkbox.setSelected(((ConcordiaGraphPanel) concordiaGraphPanel).isShowConcordiaErrorBars());

    ((AliquotDetailsDisplayInterface) concordiaGraphPanel).getConcordiaOptions()//
            .put("showConcordiaErrors", Boolean.toString(((ConcordiaGraphPanel) concordiaGraphPanel).isShowConcordiaErrorBars()));

    concordiaGraphPanel.repaint();
}//GEN-LAST:event_concordiaErrors_checkboxActionPerformed
private void close_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_close_buttonActionPerformed
    close();
}//GEN-LAST:event_close_buttonActionPerformed

private void sampleConcordiaOptions_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sampleConcordiaOptions_menuItemActionPerformed
    DialogEditor myConcordiaOptionsDialog
            = //
            new ConcordiaOptionsDialog(
                    null, true,
                    ((AliquotDetailsDisplayInterface) concordiaGraphPanel).getConcordiaOptions());

    myConcordiaOptionsDialog.setLocation(getLocation());
    myConcordiaOptionsDialog.setVisible(true);

    ((ConcordiaGraphPanel) concordiaGraphPanel).//
            setConcordiaOptions(//
                    ((ConcordiaOptionsDialog) myConcordiaOptionsDialog).getConcordiaOptions());

    concordiaGraphPanel.repaint();
}//GEN-LAST:event_sampleConcordiaOptions_menuItemActionPerformed
private void concordiaOptions_menuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_concordiaOptions_menuMouseClicked
    buildAliquotOptionsMenu();
}//GEN-LAST:event_concordiaOptions_menuMouseClicked
private void concordiaOptions_menuMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_concordiaOptions_menuMouseEntered
    buildAliquotOptionsMenu();
}//GEN-LAST:event_concordiaOptions_menuMouseEntered
private void pan_toggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pan_toggleButtonActionPerformed
    ((ConcordiaGraphPanel) concordiaGraphPanel).setImageMode(pan_toggleButton.getName());
}//GEN-LAST:event_pan_toggleButtonActionPerformed
private void zoomBox_toggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomBox_toggleButtonActionPerformed
    ((ConcordiaGraphPanel) concordiaGraphPanel).setImageMode(zoomBox_toggleButton.getName());
}//GEN-LAST:event_zoomBox_toggleButtonActionPerformed

private void restoreGraphDisplay_WeightedMean_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_restoreGraphDisplay_WeightedMean_buttonActionPerformed
    ((PlottingDetailsDisplayInterface) weightedMeanGraphPanel).refreshPanel(true);
}//GEN-LAST:event_restoreGraphDisplay_WeightedMean_buttonActionPerformed

private void fractionOrderByDate_radioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fractionOrderByDate_radioButtonActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_fractionOrderByDate_radioButtonActionPerformed

private void writeWeightedMeanPDF_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_writeWeightedMeanPDF_buttonActionPerformed
    createWeightedMeanSVGandPDF();
}//GEN-LAST:event_writeWeightedMeanPDF_buttonActionPerformed
private void zoomOutX2_WeightedMean_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomOutX2_WeightedMean_buttonActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_zoomOutX2_WeightedMean_buttonActionPerformed

private void sampleDateInterpretationsClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_sampleDateInterpretationsClosing
    close();
}//GEN-LAST:event_sampleDateInterpretationsClosing
private void sampleDateWindowResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_sampleDateWindowResized
}//GEN-LAST:event_sampleDateWindowResized

private void graphPanelsTabbedPaneResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_graphPanelsTabbedPaneResized
    // resize concordia and weighted means and additional tabbed panels with sample date window
    interpretations_SplitPane.repaint();

    int ww = concordiaLayeredPane.getWidth();
    int hh = concordiaLayeredPane.getHeight();

    ((ConcordiaGraphPanel) concordiaGraphPanel).setGraphWidth((double) ww - 25.0);
    ((ConcordiaGraphPanel) concordiaGraphPanel).setGraphHeight((double) hh - 50.0);

    int widthCP = (int) ((ConcordiaGraphPanel) concordiaGraphPanel).getGraphWidth();
    int heightCP = (int) ((ConcordiaGraphPanel) concordiaGraphPanel).getGraphHeight();
    int leftMarginCP = ((ConcordiaGraphPanel) concordiaGraphPanel).getLeftMargin();

    concordiaGraphPanel.setBounds(
            1, 1, widthCP + leftMarginCP, heightCP + 16);

    concordiaToolPanel.setBounds(
            1, heightCP + 16, widthCP + leftMarginCP, 35);

    ((ConcordiaGraphPanel) concordiaGraphPanel).repaint();

    ((WeightedMeanGraphPanel) weightedMeanGraphPanel).setGraphWidth(ww - 25);

    ((WeightedMeanGraphPanel) weightedMeanGraphPanel).setGraphHeight(hh - 50);

    weightedMeanGraphPanel.setBounds(
            1, 1, widthCP + leftMarginCP, heightCP + 16);

    weightedMeanToolPanel.setBounds(
            1, heightCP + 16, widthCP + leftMarginCP, 35);

    ((PlottingDetailsDisplayInterface) weightedMeanGraphPanel).refreshPanel(true);

    // june 2010 expansion to include additional panels
    any2ToolPanel.setBounds(
            1, heightCP + 16, widthCP + leftMarginCP, 35);

    ((DateProbabilityDensityPanel) probabilityPanel).setGraphWidth(ww - 25);

    ((DateProbabilityDensityPanel) probabilityPanel).setGraphHeight(hh - 85);
    probabilityPanel.setBounds(
            1, 1, widthCP + leftMarginCP, heightCP - 17);
    probabilityToolPanel.setBounds(
            1, heightCP - 15, widthCP + leftMarginCP, 70);

    probabilityPanel.repaint();


}//GEN-LAST:event_graphPanelsTabbedPaneResized
private void zoomInX2_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomInX2_buttonActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_zoomInX2_buttonActionPerformed

private void concordiaFlavor_radioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_concordiaFlavor_radioButtonActionPerformed
    ((AliquotDetailsDisplayInterface) concordiaGraphPanel).getConcordiaOptions().//
            put("concordiaFlavor", "C");

    ((ConcordiaGraphPanel) concordiaGraphPanel).setConcordiaFlavor("C");

    ((PlottingDetailsDisplayInterface) concordiaGraphPanel).refreshPanel(true);
}//GEN-LAST:event_concordiaFlavor_radioButtonActionPerformed
private void terraWasserburgFlavor_radioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_terraWasserburgFlavor_radioButtonActionPerformed
    ((AliquotDetailsDisplayInterface) concordiaGraphPanel).getConcordiaOptions().//
            put("concordiaFlavor", "T-W");

    ((ConcordiaGraphPanel) concordiaGraphPanel).setConcordiaFlavor("T-W");

    ((PlottingDetailsDisplayInterface) concordiaGraphPanel).refreshPanel(true);

}//GEN-LAST:event_terraWasserburgFlavor_radioButtonActionPerformed
private void dateTrees_tabsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dateTrees_tabsMouseClicked
    ((ConcordiaGraphPanel) concordiaGraphPanel).//
            setPreferredDatePanel(null);

    if (((JTabbedPane) evt.getSource()).getSelectedIndex() == 1) {
        // Sample tab
        weightedMeansPlotOptions_menu.setVisible(false);
        //setupWeightedMeansPanelForSample();
        dateTreeBySample.performLastUserSelection();
    } else {
        // aliquot case
        weightedMeansPlotOptions_menu.setVisible(true);
        setupWeightedMeansPanelForAliquots();
        dateTreeByAliquot.performLastUserSelection();
    }

    concordiaGraphPanel.repaint();

    weightedMeanGraphPanel.repaint();

    probabilityPanel.repaint();
}//GEN-LAST:event_dateTrees_tabsMouseClicked

private void weightedMeansChooser_menuItemActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_weightedMeansChooser_menuItemActionPerformed
    DialogEditor myWMChooser = new WeightedMeanOptionsDialog(
            null,
            true, sample);

    myWMChooser.setVisible(true);

    ((WeightedMeanGraphPanel) weightedMeanGraphPanel).//
            setSelectedSampleDateModels(((WeightedMeanOptionsDialog) myWMChooser).getSelectedModels());

    ((PlottingDetailsDisplayInterface) weightedMeanGraphPanel).preparePanel(true);
}//GEN-LAST:event_weightedMeansChooser_menuItemActionPerformed
private void zoomInAny2X2_buttonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomInAny2X2_buttonActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_zoomInAny2X2_buttonActionPerformed

private void resetGraphAny2Display_buttonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetGraphAny2Display_buttonActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_resetGraphAny2Display_buttonActionPerformed

private void ellipseCentersAny2OnToggle_checkboxActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ellipseCentersAny2OnToggle_checkboxActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_ellipseCentersAny2OnToggle_checkboxActionPerformed

private void ellipseLabelsAny2OnToggle_checkboxActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ellipseLabelsAny2OnToggle_checkboxActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_ellipseLabelsAny2OnToggle_checkboxActionPerformed

private void panAny2_toggleButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_panAny2_toggleButtonActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_panAny2_toggleButtonActionPerformed

private void zoomBoxAny2_toggleButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomBoxAny2_toggleButtonActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_zoomBoxAny2_toggleButtonActionPerformed

private void selectAny2Axes_toggleButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAny2Axes_toggleButtonActionPerformed
    selectAny2Axes();
}//GEN-LAST:event_selectAny2Axes_toggleButtonActionPerformed
private void graphViewTabChanged (java.awt.event.MouseEvent evt) {//GEN-FIRST:event_graphViewTabChanged
    if (graphPanels_TabbedPane.getSelectedIndex() == graphPanels_TabbedPane.indexOfTab("Probability")) {
        if (((DateProbabilityDensityPanel) probabilityPanel).getSelectedFractions().isEmpty()) {
            ((DateProbabilityDensityPanel) probabilityPanel).//
                    setSelectedFractions(filterActiveUPbFractions(sample.getUpbFractionsUnknown()));
            ((DateProbabilityDensityPanel) probabilityPanel).//
                    getDeSelectedFractions().clear();
            ((PlottingDetailsDisplayInterface) probabilityPanel).//
                    refreshPanel(true);
        } else {
            probabilityPanel.repaint();
        }
    }
}//GEN-LAST:event_graphViewTabChanged

private void zoomInProbability_buttonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomInProbability_buttonActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_zoomInProbability_buttonActionPerformed

private void resetGraphProbability_buttonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetGraphProbability_buttonActionPerformed
    positivePctDiscordance_slider.setValue(100);
    negativePctDiscordance_slider.setValue(-100);
    percentUncertainty_slider.setValue(100);

    ((DateProbabilityDensityPanel) probabilityPanel).//
            setSelectedFractions(filterActiveUPbFractions(sample.getUpbFractionsUnknown()));

    ((PlottingDetailsDisplayInterface) probabilityPanel).refreshPanel(true);
}//GEN-LAST:event_resetGraphProbability_buttonActionPerformed

private void thoriumCorrectionSelector_checkboxActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_thoriumCorrectionSelector_checkboxActionPerformed
    ((ConcordiaGraphPanel) concordiaGraphPanel).toggleDisplay_r206_238r_Th();
    thoriumCorrectionSelector_checkbox.setSelected(((ConcordiaGraphPanel) concordiaGraphPanel).isDisplay_r206_238r_Th());

    ((ConcordiaGraphPanel) concordiaGraphPanel).repaint();
}//GEN-LAST:event_thoriumCorrectionSelector_checkboxActionPerformed

private void protactiniumCorrectionSelector_checkboxActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_protactiniumCorrectionSelector_checkboxActionPerformed
    ((ConcordiaGraphPanel) concordiaGraphPanel).toggleDisplay_r206_238r_Pa();
    protactiniumCorrectionSelector_checkbox.setSelected(((ConcordiaGraphPanel) concordiaGraphPanel).isDisplay_r206_238r_Pa());

    ((ConcordiaGraphPanel) concordiaGraphPanel).repaint();
}//GEN-LAST:event_protactiniumCorrectionSelector_checkboxActionPerformed

private void linkedUnlinkedDiscordanceActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_linkedUnlinkedDiscordanceActionPerformed
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
}//GEN-LAST:event_linkedUnlinkedDiscordanceActionPerformed

private void showHistogram_buttonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showHistogram_buttonActionPerformed
    toggleShowHistogramButton();
}//GEN-LAST:event_showHistogram_buttonActionPerformed

    private void updateShowHistogramButton() {
        boolean showHistogram = ((DateProbabilityDensityPanel) probabilityPanel).isShowHistogram();
        if (showHistogram) {
            probabilityChartOptions.put("showHistogram", "true");
            showHistogram_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/earthtime/images/noHistogram.png")));
            showHistogram_button.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/earthtime/images/histogram.png")));
            showHistogram_button.setToolTipText("Click to Hide Histogram.");
        } else {
            probabilityChartOptions.put("showHistogram", "false");
            showHistogram_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/earthtime/images/histogram.png")));
            showHistogram_button.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/earthtime/images/noHistogram.png")));
            showHistogram_button.setToolTipText("Click to Show Histogram.");
        }
    }

    private void toggleShowHistogramButton() {
        boolean showHistogram = ((DateProbabilityDensityPanel) probabilityPanel).isShowHistogram();
        ((DateProbabilityDensityPanel) probabilityPanel).setShowHistogram(!showHistogram);
        updateShowHistogramButton();
        probabilityPanel.repaint();
    }


private void writeProbabilityDensityPDF_buttonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_writeProbabilityDensityPDF_buttonActionPerformed
    createProbabilityDensitySVGandPDF();
}//GEN-LAST:event_writeProbabilityDensityPDF_buttonActionPerformed

private void lockUnlockHistogramBinsActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lockUnlockHistogramBinsActionPerformed
    Icon oldPressed = lockUnlockHistogramBins.getPressedIcon();
    lockUnlockHistogramBins.setPressedIcon(lockUnlockHistogramBins.getIcon());
    lockUnlockHistogramBins.setIcon(oldPressed);
    ((DateProbabilityDensityPanel) probabilityPanel).setFreezeHistogramBinWidth(//
            !((DateProbabilityDensityPanel) probabilityPanel).isFreezeHistogramBinWidth());

    ((DateProbabilityDensityPanel) probabilityPanel).repaint();
}//GEN-LAST:event_lockUnlockHistogramBinsActionPerformed

private void lockUnlockHistogramBinsMouseEntered (java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lockUnlockHistogramBinsMouseEntered

    String currentBinWidth
            = //
            (new DecimalFormat("###0").//
            format(((DateProbabilityDensityPanel) probabilityPanel).getAdjustedScottsBinWidth()))//
            + " Ma";
    if (((DateProbabilityDensityPanel) probabilityPanel).isFreezeHistogramBinWidth()) {
        lockUnlockHistogramBins.setToolTipText("Click to Thaw Histogram bin width from current " //
                + currentBinWidth);
    } else {
        lockUnlockHistogramBins.setToolTipText("Click to Freeze Histogram bin width at " //
                + currentBinWidth);
    }
}//GEN-LAST:event_lockUnlockHistogramBinsMouseEntered

    private void showTight_toggleButtonActionPerformed ( java.awt.event.ActionEvent evt ) {//GEN-FIRST:event_showTight_toggleButtonActionPerformed
        ((ConcordiaGraphPanel) concordiaGraphPanel).setShowTightToEdges(true);
        ((PlottingDetailsDisplayInterface) concordiaGraphPanel).resetPanel(true);
    }//GEN-LAST:event_showTight_toggleButtonActionPerformed

    private void choosePDFPeaks_menuMenuSelected ( javax.swing.event.MenuEvent evt ) {//GEN-FIRST:event_choosePDFPeaks_menuMenuSelected
        buildChoosePDFPeaksMenu();
    }//GEN-LAST:event_choosePDFPeaks_menuMenuSelected

    private void showExcludedFractions_checkboxActionPerformed ( java.awt.event.ActionEvent evt ) {//GEN-FIRST:event_showExcludedFractions_checkboxActionPerformed
        ((ConcordiaGraphPanel) concordiaGraphPanel).setShowExcludedEllipses(!((ConcordiaGraphPanel) concordiaGraphPanel).isShowExcludedEllipses());
        showExcludedFractions_checkbox.setSelected(((ConcordiaGraphPanel) concordiaGraphPanel).isShowExcludedEllipses());

        ((AliquotDetailsDisplayInterface) concordiaGraphPanel).getConcordiaOptions()//
                .put("showExcludedEllipses", Boolean.toString(((ConcordiaGraphPanel) concordiaGraphPanel).isShowExcludedEllipses()));

        concordiaGraphPanel.repaint();
    }//GEN-LAST:event_showExcludedFractions_checkboxActionPerformed

    private void heatMap_MenuMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_heatMap_MenuMenuSelected
        DialogEditor myHeatMapDialog = new HeatMapManager(
                null,
                true, sample, this);

        myHeatMapDialog.setVisible(true);
    }//GEN-LAST:event_heatMap_MenuMenuSelected

    private void zoomOutProbability_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomOutProbability_buttonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_zoomOutProbability_buttonActionPerformed

    private void useUncertaintyCrosses_checkboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useUncertaintyCrosses_checkboxActionPerformed
        ((ConcordiaGraphPanel) concordiaGraphPanel).setUseUncertaintyCrosses(!((ConcordiaGraphPanel) concordiaGraphPanel).isUseUncertaintyCrosses());
        useUncertaintyCrosses_checkbox.setSelected(((ConcordiaGraphPanel) concordiaGraphPanel).isUseUncertaintyCrosses());

        ((AliquotDetailsDisplayInterface) concordiaGraphPanel).getConcordiaOptions()//
                .put("useUncertaintyCrosses", Boolean.toString(((ConcordiaGraphPanel) concordiaGraphPanel).isUseUncertaintyCrosses()));

        concordiaGraphPanel.repaint();
    }//GEN-LAST:event_useUncertaintyCrosses_checkboxActionPerformed

    private void thoriumConcordiaFlavor_radioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_thoriumConcordiaFlavor_radioButtonActionPerformed
        ((AliquotDetailsDisplayInterface) concordiaGraphPanel).getConcordiaOptions().//
                put("concordiaFlavor", "Th");

        ((ConcordiaGraphPanel) concordiaGraphPanel).setConcordiaFlavor("Th");

        ((PlottingDetailsDisplayInterface) concordiaGraphPanel).refreshPanel(true);
    }//GEN-LAST:event_thoriumConcordiaFlavor_radioButtonActionPerformed

    private void commonLeadCorrectionSelectorPDF_checkboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_commonLeadCorrectionSelectorPDF_checkboxActionPerformed
        // reclick chosen date to refresh view
        for (Enumeration e = probabilityDateButtonGroup.getElements(); e.hasMoreElements();) {
            final JRadioButton jrb = (JRadioButton) e.nextElement();
            if (jrb.isSelected()) {
                jrb.doClick();
            }
        }
        probabilityChartOptions.put("correctedForPbc", Boolean.toString(commonLeadCorrectionSelectorPDF_checkbox.isSelected()));
    }//GEN-LAST:event_commonLeadCorrectionSelectorPDF_checkboxActionPerformed

    private void commonLeadCorrectionSelector_checkboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_commonLeadCorrectionSelector_checkboxActionPerformed
        ((ConcordiaGraphPanel) concordiaGraphPanel).toggleDisplay_PbcCorr();
        commonLeadCorrectionSelector_checkbox.setSelected(((ConcordiaGraphPanel) concordiaGraphPanel).isDisplay_PbcCorr());

        ((PlottingDetailsDisplayInterface) concordiaGraphPanel).refreshPanel(true);
    }//GEN-LAST:event_commonLeadCorrectionSelector_checkboxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton DatePbCorrSchemeA_radio;
    private javax.swing.JRadioButton ageBest_radio;
    private javax.swing.JRadioButton ageR206_238r_radioB;
    private javax.swing.JRadioButton ageR207_206r_radioB;
    private javax.swing.JMenu aliquotSpecificOptions_menu;
    private javax.swing.JMenuItem aliquotsChooser_menuItem;
    private javax.swing.JLayeredPane any2LayeredPane;
    private javax.swing.JPanel any2ToolPanel;
    private javax.swing.JLayeredPane any3LayeredPane;
    private javax.swing.JTextField binWidth_text;
    private javax.swing.JMenu choosePDFPeaks_menu;
    private javax.swing.JButton close_button;
    private javax.swing.JCheckBox commonLeadCorrectionSelectorPDF_checkbox;
    private javax.swing.JCheckBox commonLeadCorrectionSelector_checkbox;
    private javax.swing.JCheckBox concordiaErrors_checkbox;
    private javax.swing.JRadioButton concordiaFlavor_radioButton;
    private javax.swing.JLayeredPane concordiaLayeredPane;
    private javax.swing.JMenu concordiaOptions_menu;
    private javax.swing.ButtonGroup concordiaPanZoom_buttonGroup;
    private javax.swing.ButtonGroup concordiaTeraW_buttonGroup;
    private javax.swing.JPanel concordiaToolPanel;
    private javax.swing.JScrollPane dateTreeByAliquot_ScrollPane;
    private javax.swing.JScrollPane dateTreeBySample_ScrollPane;
    private javax.swing.JTabbedPane dateTrees_tabs;
    private javax.swing.JCheckBox ellipseCentersAny2OnToggle_checkbox;
    private javax.swing.JCheckBox ellipseCenters_checkbox;
    private javax.swing.JCheckBox ellipseLabelsAny2OnToggle_checkbox;
    private javax.swing.JCheckBox ellipseLabels_checkbox;
    private javax.swing.JRadioButton fractionOrderByDate_radioButton;
    private javax.swing.JRadioButton fractionOrderByName_radioButton;
    private javax.swing.JRadioButton fractionOrderByRandom_radioButton;
    private javax.swing.JRadioButton fractionOrderByWeight_radioButton;
    private javax.swing.JTabbedPane graphPanels_TabbedPane;
    private javax.swing.JMenu heatMap_Menu;
    private javax.swing.JSpinner histogramBinCount_spinner;
    private javax.swing.JSplitPane interpretations_SplitPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton linkedUnlinkedDiscordance;
    private javax.swing.JButton lockUnlockHistogramBins;
    private javax.swing.JSlider negativePctDiscordance_slider;
    private javax.swing.JTextField negativePctDiscordance_text;
    private javax.swing.JLayeredPane normedProbabilityLayeredPane;
    private javax.swing.JToggleButton panAny2_toggleButton;
    private javax.swing.JToggleButton pan_WeightedMean_toggleButton;
    private javax.swing.JToggleButton pan_toggleButton;
    private javax.swing.JTextField pctUncertainty_text;
    private javax.swing.JSlider percentUncertainty_slider;
    private javax.swing.JSlider positivePctDiscordance_slider;
    private javax.swing.JTextField positivePctDiscordance_text;
    private javax.swing.ButtonGroup probabilityDateButtonGroup;
    private javax.swing.JPanel probabilityToolPanel;
    private javax.swing.JCheckBox protactiniumCorrectionSelector_checkbox;
    private javax.swing.JButton resetGraphAny2Display_button;
    private javax.swing.JButton resetGraphDisplay_button;
    private javax.swing.JButton resetGraphProbability_button;
    private javax.swing.JButton restoreGraphDisplay_WeightedMean_button;
    private javax.swing.JMenuBar sampleAgeGUIMenuBar;
    private javax.swing.JMenuItem sampleConcordiaOptions_menuItem;
    private javax.swing.JToggleButton selectAny2Axes_toggleButton;
    private javax.swing.JCheckBox showExcludedFractions_checkbox;
    private javax.swing.JButton showHistogram_button;
    private javax.swing.JButton showTightGraphProbability_button;
    private javax.swing.JToggleButton showTight_toggleButton;
    private javax.swing.JRadioButton terraWasserburgFlavor_radioButton;
    private javax.swing.JRadioButton thoriumConcordiaFlavor_radioButton;
    private javax.swing.JCheckBox thoriumCorrectionSelector_checkbox;
    private javax.swing.JCheckBox useUncertaintyCrosses_checkbox;
    private javax.swing.ButtonGroup weightedMeanFractionOrderButtonGroup;
    private javax.swing.JLayeredPane weightedMeanLayeredPane;
    private javax.swing.ButtonGroup weightedMeanPanZoom_buttonGroup;
    private javax.swing.JPanel weightedMeanToolPanel;
    private javax.swing.JMenuItem weightedMeansChooser_menuItem;
    private javax.swing.JMenuItem weightedMeansLookAndFeel_menuItem;
    private javax.swing.JMenu weightedMeansPlotOptions_menu;
    private javax.swing.JButton writeConcordiaPDF_button;
    private javax.swing.JButton writeProbabilityDensityPDF_button;
    private javax.swing.JButton writeWeightedMeanPDF_button;
    private javax.swing.JToggleButton zoomBoxAny2_toggleButton;
    private javax.swing.JToggleButton zoomBox_toggleButton;
    private javax.swing.JButton zoomInAny2X2_button;
    private javax.swing.JButton zoomInProbability_button;
    private javax.swing.JButton zoomInX2_WeightedMean_button;
    private javax.swing.JButton zoomInX2_button;
    private javax.swing.JButton zoomOutAny2X2_button;
    private javax.swing.JButton zoomOutProbability_button;
    private javax.swing.JButton zoomOutX2_WeightedMean_button;
    private javax.swing.JButton zoomOutX2_button;
    // End of variables declaration//GEN-END:variables

    /**
     *
     * @return
     */
    public JLayeredPane getConcordiaGraphPanel() {
        return concordiaGraphPanel;
    }

    /**
     *
     * @param concordiaGraphPanel
     */
    public void setConcordiaGraphPanel(JLayeredPane concordiaGraphPanel) {
        this.concordiaGraphPanel = concordiaGraphPanel;
    }

    /**
     *
     * @param node
     */
    @Override
    public void sampleTreeChangeAnalysisMode(Object node) {
        //  System.out.println("WOW MODE CALL BACK");
        Object nodeInfo = ((DefaultMutableTreeNode) node).getUserObject();

        if (nodeInfo instanceof SampleInterface) {

            if (graphPanels_TabbedPane.getSelectedIndex() == graphPanels_TabbedPane.indexOfTab("Concordia")) {

                ((ConcordiaGraphPanel) concordiaGraphPanel).//
                        setYorkFitLine(null);
                ((AliquotDetailsDisplayInterface) concordiaGraphPanel).//
                        setSelectedFractions(sample.getFractions());
                concordiaGraphPanel.repaint();
                // zap deselected list as it is meaningless at level of aliquot or sample
                ((AliquotDetailsDisplayInterface) concordiaGraphPanel).//
                        getDeSelectedFractions().clear();
                ((ConcordiaGraphPanel) concordiaGraphPanel).//
                        setPreferredDatePanel(null);

                // may 2014 show best date line
                ((ConcordiaGraphPanel) concordiaGraphPanel).setShowingSingleAliquot(false);

//            } else if (graphPanels_TabbedPane.getSelectedIndex() == graphPanels_TabbedPane.indexOfTab("Isoplot")) {
//
//                // march 2014 isoplot experiment
//                ((AliquotDetailsDisplayInterface) ConcordiaGraphPanelIsoplot).//
//                        setSelectedFractions(sample.getFractions());
//                ((AliquotDetailsDisplayInterface) ConcordiaGraphPanelIsoplot).//
//                        refreshPanel();
            } else {

                ((DateProbabilityDensityPanel) probabilityPanel).//
                        setSelectedFractions(filterActiveUPbFractions(sample.getUpbFractionsUnknown()));
                ((DateProbabilityDensityPanel) probabilityPanel).//
                        getDeSelectedFractions().clear();
                ((DateProbabilityDensityPanel) probabilityPanel).//
                        setSelectedAliquot(0);
                ((PlottingDetailsDisplayInterface) probabilityPanel).//
                        refreshPanel(true);
            }

        } else if (nodeInfo instanceof AliquotInterface) {

            // oct 2011 removed conditionals here to force all tabs to update
            // if ( graphPanels_TabbedPane.getSelectedIndex() == graphPanels_TabbedPane.indexOfTab( "Concordia" ) ) {
            ((ConcordiaGraphPanel) concordiaGraphPanel).//
                    setYorkFitLine(null);
            ((AliquotDetailsDisplayInterface) concordiaGraphPanel).//
                    setSelectedFractions(((ReduxAliquotInterface) nodeInfo).getAliquotFractions());
            concordiaGraphPanel.repaint();

            // zap deselected list as it is meaningless at level of aliquot or sample
            ((AliquotDetailsDisplayInterface) concordiaGraphPanel).//
                    getDeSelectedFractions().clear();
            ((ConcordiaGraphPanel) concordiaGraphPanel).setPreferredDatePanel(null);

            // may 2014 show best date line
            ((ConcordiaGraphPanel) concordiaGraphPanel).setShowingSingleAliquot(true);
            ((ConcordiaGraphPanel) concordiaGraphPanel).determineCurrentAliquot();

////////            // march 2014 isoplot experiment
////////            ((AliquotDetailsDisplayInterface) ConcordiaGraphPanelIsoplot).//
////////                    setSelectedFractions(((UPbReduxAliquot) nodeInfo).getAliquotFractions());
////////            ((AliquotDetailsDisplayInterface) ConcordiaGraphPanelIsoplot).//
////////                    refreshPanel();
            // update weighted means in case of delete or (oct 2010) add
            weightedMeanOptions = sample.getSampleDateInterpretationGUISettings().getWeightedMeanOptions();
            ((WeightedMeanGraphPanel) weightedMeanGraphPanel).setWeightedMeanOptions(weightedMeanOptions);

            setupWeightedMeansPanelForAliquots();
            ((PlottingDetailsDisplayInterface) weightedMeanGraphPanel).refreshPanel(true);

            // } else {
            // probability density tab ... select and paint aliquot
            ((DateProbabilityDensityPanel) probabilityPanel).//
                    setSelectedAliquot(((ReduxAliquotInterface) nodeInfo).getAliquotNumber());
            ((DateProbabilityDensityPanel) probabilityPanel).//
                    getDeSelectedFractions().clear();
            probabilityPanel.repaint();
            //}

        } else if (nodeInfo instanceof ValueModel) { // sample date model *****************************
            // get aliquot and retrieve subset of fractions for this sample date
            Object aliquotNodeInfo
                    = //
                    ((DefaultMutableTreeNode) ((TreeNode) node).getParent()).getUserObject();

            if (graphPanels_TabbedPane.getSelectedIndex() == graphPanels_TabbedPane.indexOfTab("Concordia")) {

                // in case user skipped over choosing aliquot
                ((ConcordiaGraphPanel) concordiaGraphPanel).determineCurrentAliquot();

                // check for special case interpretations: lower and upper intercepts
                ((ConcordiaGraphPanel) concordiaGraphPanel).//
                        setYorkFitLine(((SampleDateModel) nodeInfo).getYorkLineFit());
                ((AliquotDetailsDisplayInterface) concordiaGraphPanel).//
                        setSelectedFractions(((ReduxAliquotInterface) aliquotNodeInfo).//
                                getAliquotSampleDateModelSelectedFractions(((SampleDateModel) nodeInfo).//
                                        getIncludedFractionIDsVector()));
                ((ConcordiaGraphPanel) concordiaGraphPanel).//
                        setDeSelectedFractions(((ReduxAliquotInterface) aliquotNodeInfo).//
                                getAliquotSampleDateModelDeSelectedFractions(((SampleDateModel) nodeInfo).//
                                        getIncludedFractionIDsVector()));

                // for sample date interpretation, display date title box
                DateInterpretationBoxPanel dateInterpretationBoxPanel
                        = //
                        new DateInterpretationBoxPanel(((ValueModel) nodeInfo));

                ((ConcordiaGraphPanel) concordiaGraphPanel).//
                        setPreferredDatePanel(dateInterpretationBoxPanel);

                concordiaGraphPanel.repaint();

////////                // march 2014 for isoplot
////////                ((AliquotDetailsDisplayInterface) ConcordiaGraphPanelIsoplot).//
////////                        setSelectedFractions(((UPbReduxAliquot) aliquotNodeInfo).//
////////                                getAliquotSampleDateModelSelectedFractions(((SampleDateModel) nodeInfo).//
////////                                        getIncludedFractionIDsVector()));
////////                ((AliquotDetailsDisplayInterface) ConcordiaGraphPanelIsoplot).refreshPanel();
            } else if (graphPanels_TabbedPane.getSelectedIndex() == graphPanels_TabbedPane.indexOfTab("Weighted Mean")) {
                weightedMeanGraphPanel.repaint();

            } else {

                // probability tab
                ((DateProbabilityDensityPanel) probabilityPanel).//
                        setSelectedAliquot(((ReduxAliquotInterface) aliquotNodeInfo).getAliquotNumber());
                // july 2010 refine this so that included fractions are filtered on the fly
                // TODO: implement detrital sample date models that will actually have these removed from model based on filter
                Vector<ETFractionInterface> tempDeselected
                        = //
                        ((ReduxAliquotInterface) aliquotNodeInfo).//
                        getAliquotSampleDateModelDeSelectedFractions(((SampleDateModel) nodeInfo).//
                                getIncludedFractionIDsVector());
                ((DateProbabilityDensityPanel) probabilityPanel).//
                        setDeSelectedFractions(filterActiveUPbFractions(tempDeselected));

                probabilityPanel.repaint();

            }

        } else if (nodeInfo instanceof String) {
            System.out.println("STRING HIT");

            if (((String) nodeInfo).equalsIgnoreCase("Fractions")) {
                // perform date interpretation things for user (same as clicking on interpretation name)
                // get aliquot and retrieve subset of fractions for this sample date
                Object aliquotNodeInfo
                        = //
                        ((DefaultMutableTreeNode) //
                        ((TreeNode) node).getParent().getParent()).getUserObject();

                Object sampleDateNodeInfo
                        = //
                        ((DefaultMutableTreeNode) //
                        ((TreeNode) node).getParent()).getUserObject();

                if (graphPanels_TabbedPane.getSelectedIndex() == graphPanels_TabbedPane.indexOfTab("Concordia")) {

                    // check for special case interpretations: lower and upper intercepts
                    ((ConcordiaGraphPanel) concordiaGraphPanel).//
                            setYorkFitLine(((SampleDateModel) sampleDateNodeInfo).getYorkLineFit());

                    ((AliquotDetailsDisplayInterface) concordiaGraphPanel).//
                            setSelectedFractions(((ReduxAliquotInterface) aliquotNodeInfo).//
                                    getAliquotSampleDateModelSelectedFractions(((SampleDateModel) sampleDateNodeInfo).//
                                            getIncludedFractionIDsVector()));

                    ((ConcordiaGraphPanel) concordiaGraphPanel).//
                            setDeSelectedFractions(((ReduxAliquotInterface) aliquotNodeInfo).//
                                    getAliquotSampleDateModelDeSelectedFractions(((SampleDateModel) sampleDateNodeInfo).//
                                            getIncludedFractionIDsVector()));

                    // for sample date interpretation, display date title box
                    DateInterpretationBoxPanel dateInterpretationBoxPanel
                            = //
                            new DateInterpretationBoxPanel(((ValueModel) sampleDateNodeInfo));

                    ((ConcordiaGraphPanel) concordiaGraphPanel).//
                            setPreferredDatePanel(dateInterpretationBoxPanel);

                    concordiaGraphPanel.repaint();
//                } else if (graphPanels_TabbedPane.getSelectedIndex() == graphPanels_TabbedPane.indexOfTab("Isoplot")) {
//                    ((AliquotDetailsDisplayInterface) ConcordiaGraphPanelIsoplot).//
//                            setSelectedFractions(((UPbReduxAliquot) aliquotNodeInfo).//
//                                    getAliquotSampleDateModelSelectedFractions(((SampleDateModel) sampleDateNodeInfo).//
//                                            getIncludedFractionIDsVector()));
//                    ((AliquotDetailsDisplayInterface) ConcordiaGraphPanelIsoplot).refreshPanel();

                } else {

                    // probability tab
                    ((DateProbabilityDensityPanel) probabilityPanel).//
                            setSelectedAliquot(((ReduxAliquotInterface) aliquotNodeInfo).getAliquotNumber());

//                    ((DateProbabilityDensityPanel) probabilityPanel).//
//                            setDeSelectedFractions( ((UPbReduxAliquot) aliquotNodeInfo).//
//                            getAliquotSampleDateModelDeSelectedFractions( ((SampleDateModel) sampleDateNodeInfo).//
//                            getIncludedFractionIDsVector() ) );
                    // july 2010 refine this so that included fractions are filtered on the fly
                    // TODO: implement detrital sample date models that will actually have these removed from model based on filter
                    Vector<ETFractionInterface> tempDeselected
                            = //
                            ((ReduxAliquotInterface) aliquotNodeInfo).//
                            getAliquotSampleDateModelDeSelectedFractions(((SampleDateModel) sampleDateNodeInfo).//
                                    getIncludedFractionIDsVector());
                    ((DateProbabilityDensityPanel) probabilityPanel).//
                            setDeSelectedFractions(filterActiveUPbFractions(tempDeselected));
                    probabilityPanel.repaint();
                }
            }

        } else if (nodeInfo instanceof CheckBoxNode) {
            System.out.println("CHECKBOX " + ((CheckBoxNode) nodeInfo).getText() + "  was " + ((CheckBoxNode) nodeInfo).isSelected());

            // change the sample age model
            // toggle value
            DefaultMutableTreeNode parentNode
                    = (DefaultMutableTreeNode) ((TreeNode) node).getParent();
            DefaultMutableTreeNode sampleAgeNode
                    = (DefaultMutableTreeNode) parentNode.getParent();
            DefaultMutableTreeNode aliquotNode
                    = (DefaultMutableTreeNode) sampleAgeNode.getParent();
            Object sampleDateNodeInfo = sampleAgeNode.getUserObject();
            Object aliquotNodeInfo = aliquotNode.getUserObject();

            // check for special case interpretations: lower and upper intercepts
            ((ConcordiaGraphPanel) concordiaGraphPanel).//
                    setYorkFitLine(((SampleDateModel) sampleDateNodeInfo).getYorkLineFit());

            // toggle fraction selection and re-calculate sample age model
            // first strip out fraction name
            String[] temp = ((CheckBoxNode) nodeInfo).getText().split(":");
            // oct 2014 modified to be sure to work no matter where text is clicked
            ((CheckBoxNode) nodeInfo).setSelected(//
                    ((SampleDateModel) sampleDateNodeInfo).//
                    ToggleAliquotFractionByName(//
                            temp[0].trim()));//,

            SampleInterface.updateAndSaveSampleDateModelsByAliquot(sample);

            if (graphPanels_TabbedPane.getSelectedIndex() == graphPanels_TabbedPane.indexOfTab("Concordia")) {
                // now redraw the sample age from the aliquot view
                ((AliquotDetailsDisplayInterface) concordiaGraphPanel).//
                        setSelectedFractions(((ReduxAliquotInterface) aliquotNodeInfo).//
                                getAliquotSampleDateModelSelectedFractions(((SampleDateModel) sampleDateNodeInfo).getIncludedFractionIDsVector()));
                ((ConcordiaGraphPanel) concordiaGraphPanel).//
                        setDeSelectedFractions(((ReduxAliquotInterface) aliquotNodeInfo).//
                                getAliquotSampleDateModelDeSelectedFractions(((SampleDateModel) sampleDateNodeInfo).getIncludedFractionIDsVector()));

                // fix dateTreeByAliquot
                ((DefaultTreeModel) ((JTree) dateTreeByAliquot).getModel()).//
                        nodeChanged(((TreeNode) node).//
                                getParent().//
                                getParent().//
                                getChildAt(0));

                // handle special case interpretations for plotting: lower and upper intercepts
                ((ConcordiaGraphPanel) concordiaGraphPanel).//
                        setYorkFitLine(((SampleDateModel) sampleDateNodeInfo).getYorkLineFit());
                // for sample date interpretation, display date title box
                DateInterpretationBoxPanel dateInterpretationBoxPanel
                        = //
                        new DateInterpretationBoxPanel(((ValueModel) sampleDateNodeInfo));
                ((ConcordiaGraphPanel) concordiaGraphPanel).//
                        setPreferredDatePanel(dateInterpretationBoxPanel);
                concordiaGraphPanel.repaint();
//            } else if (graphPanels_TabbedPane.getSelectedIndex() == graphPanels_TabbedPane.indexOfTab("Isoplot")) {
//                // march 2104 for isoplot
//                ((AliquotDetailsDisplayInterface) ConcordiaGraphPanelIsoplot).//
//                        setSelectedFractions(((UPbReduxAliquot) aliquotNodeInfo).//
//                                getAliquotSampleDateModelSelectedFractions(((SampleDateModel) sampleDateNodeInfo).getIncludedFractionIDsVector()));
//                ((AliquotDetailsDisplayInterface) ConcordiaGraphPanelIsoplot).refreshPanel();
            } else if (graphPanels_TabbedPane.getSelectedIndex() == graphPanels_TabbedPane.indexOfTab("Weighted Mean")) {

                weightedMeanGraphPanel.repaint();
            } else {
                // probability panel
//            ((DateProbabilityDensityPanel) probabilityPanel).//
//                    setDeSelectedFractions( ((UPbReduxAliquot) aliquotNodeInfo).//
//                    getAliquotSampleDateModelDeSelectedFractions( ((SampleDateModel) sampleDateNodeInfo).getIncludedFractionIDsVector() ) );

                // july 2010 refine this so that included fractions are filtered on the fly
                // TODO: implement detrital sample date models that will actually have these removed from model based on filter
                Vector<ETFractionInterface> tempDeselected
                        = //
                        ((ReduxAliquotInterface) aliquotNodeInfo).//
                        getAliquotSampleDateModelDeSelectedFractions(((SampleDateModel) sampleDateNodeInfo).//
                                getIncludedFractionIDsVector());
                ((DateProbabilityDensityPanel) probabilityPanel).//
                        setDeSelectedFractions(filterActiveUPbFractions(tempDeselected));
                probabilityPanel.repaint();
            }
        }
    }

    /**
     *
     * @param node
     */
    @Override
    public void sampleTreeChangeCompilationMode(Object node) {
//        System.out.println("WOW A SAMPLE CALL BACK");
        Object nodeInfo = ((DefaultMutableTreeNode) node).getUserObject();

        if (nodeInfo instanceof SampleInterface) {

            ((ConcordiaGraphPanel) concordiaGraphPanel).//
                    setYorkFitLine(null);

            ((AliquotDetailsDisplayInterface) concordiaGraphPanel).//
                    setSelectedFractions(sample.getFractions());

            concordiaGraphPanel.//
                    repaint();

            // zap delselected list as it is meaningless at level of aliquot or sample
            ((AliquotDetailsDisplayInterface) concordiaGraphPanel).//
                    getDeSelectedFractions().clear();
            ((ConcordiaGraphPanel) concordiaGraphPanel).//
                    setPreferredDatePanel(null);

            // setup weighted means
            weightedMeanGraphPanel.repaint();

        } else if (nodeInfo instanceof AliquotInterface) {
        } else if (nodeInfo instanceof ValueModel) {
            // get aliquot and retrieve subset of fractions for this sample date
            Object sampleNodeInfo
                    = //
                    ((DefaultMutableTreeNode) ((TreeNode) node).getParent()).getUserObject();

            // check for special case interpretations: lower and upper intercepts
            ((ConcordiaGraphPanel) concordiaGraphPanel).//
                    setYorkFitLine(((SampleDateModel) nodeInfo).getYorkLineFit());

            ((AliquotDetailsDisplayInterface) concordiaGraphPanel).//
                    setSelectedFractions(((SampleInterface) sampleNodeInfo).//
                            getSampleDateModelSelectedFractions(((SampleDateModel) nodeInfo).//
                                    getIncludedFractionIDsVector()));

            ((ConcordiaGraphPanel) concordiaGraphPanel).//
                    setDeSelectedFractions(((SampleInterface) sampleNodeInfo).//
                            getSampleDateModelDeSelectedFractions(((SampleDateModel) nodeInfo).//
                                    getIncludedFractionIDsVector()));

            ((ConcordiaGraphPanel) concordiaGraphPanel).//
                    setPreferredDatePanel(null);

            concordiaGraphPanel.repaint();

            // setup weighted means
//            setupWeightedMeansPanelForSample();
//            weightedMeanGraphPanel.repaint();
            probabilityPanel.repaint();

        } else if (nodeInfo instanceof String) {
            System.out.println("STRING HIT");

            if (((String) nodeInfo).equalsIgnoreCase("Fractions")) {
                // perform date interpretation things for user (same as clicking on interpretation name)
                // get aliquot and retrieve subset of fractions for this sample date
                Object sampleNodeInfo
                        = //
                        ((DefaultMutableTreeNode) //
                        ((TreeNode) node).getParent().getParent()).getUserObject();

                Object sampleDateNodeInfo
                        = //
                        ((DefaultMutableTreeNode) //
                        ((TreeNode) node).getParent()).getUserObject();

                // check for special case interpretations: lower and upper intercepts
                ((ConcordiaGraphPanel) concordiaGraphPanel).//
                        setYorkFitLine(((SampleDateModel) sampleDateNodeInfo).getYorkLineFit());
                ((AliquotDetailsDisplayInterface) concordiaGraphPanel).//
                        setSelectedFractions(((SampleInterface) sampleNodeInfo).//
                                getSampleDateModelSelectedFractions(((SampleDateModel) sampleDateNodeInfo).//
                                        getIncludedFractionIDsVector()));
                ((ConcordiaGraphPanel) concordiaGraphPanel).//
                        setDeSelectedFractions(((SampleInterface) sampleNodeInfo).//
                                getSampleDateModelDeSelectedFractions(((SampleDateModel) sampleDateNodeInfo).//
                                        getIncludedFractionIDsVector()));

                // for sample date interpretation, display date title box
                DateInterpretationBoxPanel dateInterpretationBoxPanel
                        = //
                        new DateInterpretationBoxPanel(((ValueModel) sampleDateNodeInfo));

                ((ConcordiaGraphPanel) concordiaGraphPanel).//
                        setPreferredDatePanel(null);

                concordiaGraphPanel.repaint();

            }

        } else if (nodeInfo instanceof CheckBoxNode) {
            System.out.println("CHECKBOX " + ((CheckBoxNode) nodeInfo).getText() + "  was " + ((CheckBoxNode) nodeInfo).isSelected());

            // change the sample age model
            // toggle value
            DefaultMutableTreeNode aliquotNameNode
                    = (DefaultMutableTreeNode) ((TreeNode) node).getParent();
            DefaultMutableTreeNode aliquotFractionsLabelNode
                    = (DefaultMutableTreeNode) ((DefaultMutableTreeNode) aliquotNameNode).getParent();
            DefaultMutableTreeNode sampleDateNode
                    = (DefaultMutableTreeNode) ((DefaultMutableTreeNode) aliquotFractionsLabelNode).getParent();
            DefaultMutableTreeNode sampleNode
                    = (DefaultMutableTreeNode) ((DefaultMutableTreeNode) sampleDateNode).getParent();
            Object sampleDateNodeInfo = sampleDateNode.getUserObject();
            Object sampleNodeInfo = sampleNode.getUserObject();

            // check for special case interpretations: lower and upper intercepts
            ((ConcordiaGraphPanel) concordiaGraphPanel).//
                    setYorkFitLine(((SampleDateModel) sampleDateNodeInfo).getYorkLineFit());

            // toggle fraction selection and re-calculate sample age model
            // first strip out fraction name
            String[] temp = ((CheckBoxNode) nodeInfo).getText().split(":");

            // oct 2014 modified to be sure to work no matter where text is clicked
            ((CheckBoxNode) nodeInfo).setSelected(//
                    ((SampleDateModel) sampleDateNodeInfo).//
                    ToggleSampleFractionByName(//
                            temp[0].trim()));//,

            sample.updateSampleDateModels();

            // now redraw the sample age from the sample view
            ((AliquotDetailsDisplayInterface) concordiaGraphPanel).//
                    setSelectedFractions(((SampleInterface) sampleNodeInfo).//
                            getSampleDateModelSelectedFractions(//
                                    ((SampleDateModel) sampleDateNodeInfo).getIncludedFractionIDsVector()));
            ((ConcordiaGraphPanel) concordiaGraphPanel).//
                    setDeSelectedFractions(((SampleInterface) sampleNodeInfo).//
                            getSampleDateModelDeSelectedFractions(//
                                    ((SampleDateModel) sampleDateNodeInfo).getIncludedFractionIDsVector()));

            // fix dateTreeByAliquot
            ((DefaultTreeModel) ((JTree) dateTreeBySample).getModel()).//
                    nodeChanged(((DefaultMutableTreeNode) node).//
                            getParent().// aliquotnamenode
                            getParent().// aliquotfractionslabelnode
                            getParent().// sampledatenode
                            getChildAt(0));//string rep of date =

            // handle special case interpretations for plotting: lower and upper intercepts
            ((ConcordiaGraphPanel) concordiaGraphPanel).//
                    setYorkFitLine(((SampleDateModel) sampleDateNodeInfo).getYorkLineFit());

            ((ConcordiaGraphPanel) concordiaGraphPanel).//
                    setPreferredDatePanel(null);
            concordiaGraphPanel.repaint();

            weightedMeanGraphPanel.repaint();

            probabilityPanel.repaint();

        }
    }

    private Vector<ETFractionInterface> filterActiveUPbFractions(Vector<ETFractionInterface> fractions) {

        Vector<ETFractionInterface> filteredFractions = new Vector<>();

        String dateName = ((DateProbabilityDensityPanel) probabilityPanel).getChosenDateName();

        for (ETFractionInterface f : fractions) {
            boolean doAddFraction = !f.isRejected();
            double pctDiscordance = f.getRadiogenicIsotopeDateByName(RadDates.percentDiscordance).getValue().doubleValue();

            if (pctDiscordance >= 0.0) {  //
                // positive percent discordance
                doAddFraction = doAddFraction && (pctDiscordance <= positivePctDiscordance_slider.getValue());
            } else {
                // negative percent discordance
                doAddFraction = doAddFraction && (pctDiscordance >= negativePctDiscordance_slider.getValue());
            }

//            System.out.println("1 pct unct " + f.getFractionID() + "  " + f.getRadiogenicIsotopeDateByName(chosenDateName).getOneSigmaPct().doubleValue());
            doAddFraction = doAddFraction //
                    && f.getRadiogenicIsotopeDateByName(dateName).getOneSigmaPct().doubleValue() //
                    <= percentUncertainty_slider.getValue();

            //oct 2014
            doAddFraction = doAddFraction //
                    && f.getRadiogenicIsotopeDateByName(dateName).getOneSigmaPct().doubleValue() != 0.0;

            if (doAddFraction) {
                filteredFractions.add(f);
            }
        }
        return filteredFractions;
    }

    /**
     *
     * @return @throws ETException
     */
    public File createConcordiaSVGandPDF() throws ETException {
        File selectedFile = null;
        File selectedFileSVG = null;

//        setAlwaysOnTop(false);
        String dialogTitle = "Save this Concordia as pdf (also as svg): *.pdf";
        final String fileExtension = ".pdf";
        String concordiaFileName
                = sample.getSampleName() + "_CONCORDIA" + fileExtension;
        FileFilter nonMacFileFilter = new PDFFileFilter();

        selectedFile = FileHelper.AllPlatformSaveAs(
                new Frame(), dialogTitle, null, fileExtension, concordiaFileName, nonMacFileFilter);

        if (selectedFile != null) {

            try {
                selectedFileSVG
                        = //
                        new File(selectedFile.getCanonicalPath().replaceFirst(".pdf", ".svg"));
            } catch (IOException iOException) {
            }
            try {
                ((ConcordiaGraphPanel) concordiaGraphPanel).outputToSVG(selectedFileSVG);
                ((ConcordiaGraphPanel) concordiaGraphPanel).outputToPDF(selectedFileSVG);
                viewPDF(
                        selectedFile.getCanonicalPath());

            } catch (IOException ex) {
            }
        }

//        setAlwaysOnTop(false);
        return selectedFileSVG;
    }

    /**
     *
     */
    public void createWeightedMeanSVGandPDF() {
        File selectedFile = null;
        File selectedFileSVG = null;

//        setAlwaysOnTop(false);
        String dialogTitle = "Save this WeightedMean as pdf (also as svg): *.pdf";
        final String fileExtension = ".pdf";
        String weightedMeanFileName
                = sample.getSampleName() + "_WEIGHTEDMEAN" + fileExtension;
        FileFilter nonMacFileFilter = new PDFFileFilter();

        selectedFile = FileHelper.AllPlatformSaveAs(
                new Frame(), dialogTitle, null, fileExtension, weightedMeanFileName, nonMacFileFilter);

        if (selectedFile != null) {

            try {
                selectedFileSVG
                        = //
                        new File(selectedFile.getCanonicalPath().replaceFirst(".pdf", ".svg"));

            } catch (IOException iOException) {
            }
            try {
                ((WeightedMeanGraphPanel) weightedMeanGraphPanel).outputToSVG(selectedFileSVG);
                ((WeightedMeanGraphPanel) weightedMeanGraphPanel).outputToPDF(selectedFileSVG);
                viewPDF(selectedFile.getCanonicalPath());
            } catch (IOException ex) {
            }
        }

//        setAlwaysOnTop(false);
    }

    /**
     *
     */
    public void createProbabilityDensitySVGandPDF() {
        File selectedFile;
        File selectedFileSVG = null;

//        setAlwaysOnTop(false);
        String dialogTitle = "Save this ProbabilityDensity as pdf (also as svg): *.pdf";
        final String fileExtension = ".pdf";
        String weightedMeanFileName
                = sample.getSampleName() + "_PROBABILITY-DENSITY" + fileExtension;
        FileFilter nonMacFileFilter = new PDFFileFilter();

        selectedFile = FileHelper.AllPlatformSaveAs(
                new Frame(), dialogTitle, null, fileExtension, weightedMeanFileName, nonMacFileFilter);

        if (selectedFile != null) {

            try {
                selectedFileSVG
                        = //
                        new File(selectedFile.getCanonicalPath().replaceFirst(".pdf", ".svg"));

            } catch (IOException iOException) {
            }
            try {
                ((DateProbabilityDensityPanel) probabilityPanel).outputToSVG(selectedFileSVG);

                ((DateProbabilityDensityPanel) probabilityPanel).outputToPDF(selectedFileSVG);
                viewPDF(selectedFile.getCanonicalPath());

            } catch (IOException ex) {
            }
        }

//        setAlwaysOnTop(false);
    }

    /**
     *
     * @param fileURL
     */
    public void viewPDF(String fileURL) {
        BrowserControl.displayURL(fileURL);
    }

    /**
     *
     * @return
     */
    public File createConcordiaSVG() {
        File svgFile = new File("TEST_CONCORDIA.svg");

        ((ConcordiaGraphPanel) concordiaGraphPanel).outputToSVG(svgFile);

        return svgFile;
    }

    /**
     *
     * @return
     */
    public File createWeightedMeanSVG() {
        File svgFile = new File("TEST_WEIGHTED_MEAN.svg");

        ((WeightedMeanGraphPanel) weightedMeanGraphPanel).outputToSVG(svgFile);

        return svgFile;
    }

    /**
     *
     */
    public void toggleConcordiaSVGView() {
        if (svgConcordiaCanvas == null) {
            File file = createConcordiaSVG();
            svgConcordiaCanvas = new JSVGCanvas();

            ((ConcordiaGraphPanel) concordiaGraphPanel).removeAll();

            ((ConcordiaGraphPanel) concordiaGraphPanel).setLayout(null);
            svgConcordiaCanvas.setBounds(0, 0, concordiaGraphPanel.getWidth() - 1, concordiaGraphPanel.getHeight() - 1);
            concordiaGraphPanel.add(svgConcordiaCanvas);
            concordiaGraphPanel.validate();

            try {
                svgConcordiaCanvas.setURI(file.toURI().toURL().toString());
                zoomInX2_button.setEnabled(false);
                zoomOutX2_button.setEnabled(false);
                resetGraphDisplay_button.setEnabled(false);
                pan_toggleButton.setEnabled(false);
                zoomBox_toggleButton.setEnabled(false);
                ellipseCenters_checkbox.setEnabled(false);
                ellipseLabels_checkbox.setEnabled(false);
                concordiaErrors_checkbox.setEnabled(false);

            } catch (MalformedURLException malformedURLException) {
            }
        } else {
            zoomInX2_button.setEnabled(true);
            zoomOutX2_button.setEnabled(true);
            resetGraphDisplay_button.setEnabled(true);
            pan_toggleButton.setEnabled(true);
            zoomBox_toggleButton.setEnabled(true);
            ellipseCenters_checkbox.setEnabled(true);
            ellipseLabels_checkbox.setEnabled(true);
            concordiaErrors_checkbox.setEnabled(true);
            concordiaGraphPanel.remove(svgConcordiaCanvas);
            concordiaGraphPanel.validate();

            ((ConcordiaGraphPanel) concordiaGraphPanel).repaint();
            svgConcordiaCanvas = null;
        }
    }

    /**
     *
     */
    public void toggleWeightedMeanSVGView() {
        if (svgWeightedMeanCanvas == null) {
            File file = createWeightedMeanSVG();
            svgWeightedMeanCanvas = new JSVGCanvas();

            ((WeightedMeanGraphPanel) weightedMeanGraphPanel).removeAll();

            ((WeightedMeanGraphPanel) weightedMeanGraphPanel).setLayout(null);
            svgWeightedMeanCanvas.setBounds(0, 0, weightedMeanGraphPanel.getWidth() - 1, weightedMeanGraphPanel.getHeight() - 1);
            weightedMeanGraphPanel.add(svgWeightedMeanCanvas);
            weightedMeanGraphPanel.validate();

            try {
                svgWeightedMeanCanvas.setURI(file.toURI().toURL().toString());
                zoomInX2_WeightedMean_button.setEnabled(false);
                zoomOutX2_WeightedMean_button.setEnabled(false);
                restoreGraphDisplay_WeightedMean_button.setEnabled(false);
                pan_WeightedMean_toggleButton.setEnabled(false);

            } catch (MalformedURLException malformedURLException) {
            }
        } else {
            zoomInX2_WeightedMean_button.setEnabled(true);
            zoomOutX2_WeightedMean_button.setEnabled(true);
            restoreGraphDisplay_WeightedMean_button.setEnabled(true);
            pan_WeightedMean_toggleButton.setEnabled(true);
            zoomBox_toggleButton.setEnabled(true);

            weightedMeanGraphPanel.remove(svgWeightedMeanCanvas);
            weightedMeanGraphPanel.validate();

            ((WeightedMeanGraphPanel) weightedMeanGraphPanel).repaint();
            svgWeightedMeanCanvas = null;
        }
    }

    /**
     * creates a menu item for each aliquot and provides data for aliquot
     * options invocation
     */
    private void buildAliquotOptionsMenu() {
        // add menu item for each aliquot
        aliquotSpecificOptions_menu.removeAll();

        for (final AliquotInterface a : sample.getActiveAliquots()) {//Aliquots()) {
            // april 2010 refine to leave out empty aliquots = no fractions
            //if ( ((UPbReduxAliquot) a).getAliquotFractions().size() > 0 ) {
            JMenuItem menuItem = aliquotSpecificOptions_menu.add(new JMenuItem(a.getAliquotName()));

            menuItem.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    aliquotSpecificOptions_menuItemActionPerformed(evt, a.getAliquotName(), ((UPbReduxAliquot) a).getAliquotNumber());

                }
            });

        }
    }

    private void aliquotSpecificOptions_menuItemActionPerformed(java.awt.event.ActionEvent evt, String aliquotName, int aliquotNumber) {
        System.out.println("Aliquot menu item for " + aliquotName);

        DialogEditor myAliquotOptionsDialog = new AliquotOptionsDialog(
                null,
                true,
                aliquotName,
                sample.getSampleDateInterpretationGUISettings().getAliquotOptionsMapByName(aliquotName, aliquotNumber));

        myAliquotOptionsDialog.setLocation(getLocation());

        myAliquotOptionsDialog.setVisible(true);

        sample.getSampleDateInterpretationGUISettings().setAliquotOptionsByName(
                aliquotName,
                ((AliquotOptionsDialog) myAliquotOptionsDialog).getAliquotOptions());

        getConcordiaGraphPanel().repaint();

    }

    private void buildChoosePDFPeaksMenu() {
        // add menu item for each probability peak
        int sizeOfPeakGroups = 20;
        choosePDFPeaks_menu.removeAll();

        final Map<Integer, Boolean> maximaShown
                = //
                ((DateProbabilityDensityPanel) probabilityPanel).getMaximaShown();

        int counterOfPeaks = 0;
        int counterOfPeakGroups = 0;

        Iterator<Integer> maximaShownIterator = maximaShown.keySet().iterator();

        JMenu groupMenu = new JMenu("NOT");
        while (maximaShownIterator.hasNext()) {
            final int maxDate = maximaShownIterator.next();

            if (counterOfPeaks % sizeOfPeakGroups == 0) {
                counterOfPeakGroups++;
                groupMenu = new JMenu("Choose PDF Peaks starting at age " + maxDate);
                choosePDFPeaks_menu.add(groupMenu);
            }

            JMenuItem dateChoiceMenuItem
                    = //
                    groupMenu.add(//
                            new JCheckBoxMenuItem(//
                                    Integer.toString(maxDate), maximaShown.get(maxDate)));

            dateChoiceMenuItem.addActionListener(new java.awt.event.ActionListener() {

                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    maximaShown.put(maxDate, ((JCheckBoxMenuItem) evt.getSource()).isSelected());
                    ((DateProbabilityDensityPanel) probabilityPanel).repaint();
                }
            });

            counterOfPeaks++;
        }
    }

    /**
     *
     * @return
     */
    public JPanel getWeightedMeanGraphPanel() {
        return weightedMeanGraphPanel;
    }

    /**
     *
     * @param weightedMeanGraphPanel
     */
    public void setWeightedMeanGraphPanel(JPanel weightedMeanGraphPanel) {
        this.weightedMeanGraphPanel = weightedMeanGraphPanel;
    }

    /**
     * @return the concordiaFlavor
     */
    public String getConcordiaFlavor() {
        return concordiaFlavor;
    }

    /**
     * @param concordiaFlavor the concordiaFlavor to set
     */
    public void setConcordiaFlavor(String concordiaFlavor) {
        this.concordiaFlavor = concordiaFlavor;
    }

    /**
     * @param sample the sample to set
     */
    public void setSample(SampleInterface sample) {
        this.sample = sample;
    }

    /**
     *
     */
    @Override
    public void switchToPanMode() {
        pan_toggleButton.doClick();
    }
}
