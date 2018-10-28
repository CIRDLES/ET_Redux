/*
 * SampleDateInterpretationsManager.java
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
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
import org.earthtime.UPb_Redux.beans.ReduxSpinner;
import org.earthtime.UPb_Redux.beans.ReduxSuppressComponentEventsI;
import org.earthtime.UPb_Redux.customJTrees.CheckBoxNode;
import org.earthtime.UPb_Redux.dateInterpretation.DateInterpretationUpdateViewI;
import org.earthtime.UPb_Redux.dateInterpretation.DateProbabilityDensityPanel;
import org.earthtime.UPb_Redux.dateInterpretation.SampleTreeChangeI;
import org.earthtime.UPb_Redux.dateInterpretation.SampleTreeI;
import org.earthtime.UPb_Redux.dateInterpretation.WeightedMeanGraphPanel;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.AliquotDetailsDisplayInterface;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.ConcordiaGraphPanel;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.ConcordiaPlotDisplayInterface;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.GraphPanelModeChangeI;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.PlottingDetailsDisplayInterface;
import org.earthtime.UPb_Redux.dateInterpretation.graphPersistence.DateInterpretationBoxPanel;
import org.earthtime.UPb_Redux.dialogs.aliquotManagers.AliquotOptionsDialog;
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
import org.earthtime.dataDictionaries.SampleAnalysisTypesEnum;
import org.earthtime.dialogs.DialogEditor;
import org.earthtime.exceptions.ETException;
import org.earthtime.exceptions.ETWarningDialog;
import org.earthtime.fractions.ETFractionInterface;
import org.earthtime.plots.AbstractDataView;
import org.earthtime.plots.AbstractPlot;
import org.earthtime.plots.PlotAxesSetupInterface;
import org.earthtime.plots.PlotInterface;
import org.earthtime.plots.anyTwo.PlotAny2Panel;
import org.earthtime.plots.evolution.EvolutionPlotPanelII;
import org.earthtime.plots.isochrons.IsochronsPanel;
import org.earthtime.plots.isochrons.UseriesIsochronPlotDisplayInterface;
import org.earthtime.plots.isochrons.evolution.IsochronsEvolutionSelectorDialog;
import org.earthtime.reduxLabData.ReduxLabData;
import org.earthtime.reports.ReportColumnInterface;
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
    private JLayeredPane plotAny2Panel;
    private JLayeredPane useriesIsochronPanel;
    private JLayeredPane evolutionPlotPanel;
    /**
     *
     */
    protected JLayeredPane probabilityPanel;
    protected SampleInterface sample;
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
     * @param projectSampleAnalysisType
     * @param sampleAnalysisType
     * @param ampleAnalysisType
     * @param evolutionPlotPanel
     * @param modal
     * @param concordiaGraphPanel
     * @param plotAny2Panel the value of plotAny2Panel
     * @param useriesIsochronPanel
     * @param weightedMeanGraphPanel
     * @param normedProbabilityPanel
     * @param sample
     * @param dateTreeByAliquot
     * @param dateTreeBySample
     */
    public SampleDateInterpretationsManager(
            SampleDateInterpretationSubscribeInterface parent,
            JLayeredPane concordiaGraphPanel,
            JLayeredPane plotAny2Panel,
            JLayeredPane useriesIsochronPanel,
            JLayeredPane evolutionPlotPanel,
            JPanel weightedMeanGraphPanel,
            JLayeredPane normedProbabilityPanel,
            SampleInterface sample,
            SampleTreeI dateTreeByAliquot,
            SampleTreeI dateTreeBySample) {

        super(null, false);

        this.parentFrame = parent;

        this.sample = sample;

        initComponents();
        setSize(1200, 750);
        setLocation(getX() + 100, getY());

        this.concordiaGraphPanel = concordiaGraphPanel;
        initConcordiaGraphPanel();

        this.plotAny2Panel = plotAny2Panel;
        initPlotAny2Panel();

        this.useriesIsochronPanel = useriesIsochronPanel;
        try {
            initUseriesIsochronPanel();
        } catch (Exception e) {
        }

        this.weightedMeanGraphPanel = weightedMeanGraphPanel;
        initWeightedMeanGraphPanel();

        this.evolutionPlotPanel = evolutionPlotPanel;
        try {
            initEvolutionPlotPanel();
        } catch (Exception e) {
        }

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
        ((PlottingDetailsDisplayInterface) concordiaGraphPanel).refreshPanel(true, false);
        ((PlottingDetailsDisplayInterface) plotAny2Panel).refreshPanel(true, false);
        try {
            ((PlottingDetailsDisplayInterface) useriesIsochronPanel).refreshPanel(true, false);
        } catch (Exception e) {
        }

        // feb 2017
        // let's focus on the skin or at least the tab
        // SampleAnalysisTypesEnum.USERIES_CARB.equals(SampleAnalysisTypesEnum.valueOf(getSampleAnalysisType()))
        SampleAnalysisTypesEnum sampleAnalysisType = SampleAnalysisTypesEnum.IDTIMS;
        try {
            sampleAnalysisType = SampleAnalysisTypesEnum.valueOf(sample.getSampleAnalysisType());
        } catch (Exception e) {
        }

        switch (SampleAnalysisTypesEnum.valueOf(sample.getSampleAnalysisType())) {
            case IDTIMS:
            case LAICPMS:
                graphPanels_TabbedPane.setSelectedComponent(concordiaLayeredPane);
                graphPanels_TabbedPane.remove(useriesIsochronLayeredPane);
                graphPanels_TabbedPane.remove(evolutionLayeredPane);
                break;
            case USERIES_IGN:
                graphPanels_TabbedPane.setSelectedComponent(useriesIsochronLayeredPane);
                graphPanels_TabbedPane.remove(concordiaLayeredPane);
                graphPanels_TabbedPane.remove(weightedMeanLayeredPane);
                graphPanels_TabbedPane.remove(normedProbabilityLayeredPane);
                graphPanels_TabbedPane.remove(evolutionLayeredPane);
                graphPanels_TabbedPane.remove(any2LayeredPane);

                choosePDFPeaks_menu.setVisible(false);
                weightedMeansPlotOptions_menu.setVisible(false);
//                displayOptions_menu.setVisible(false);
//                heatMap_Menu.setVisible(false);
                break;
            case USERIES_CARB:
                graphPanels_TabbedPane.setSelectedComponent(evolutionLayeredPane);
                graphPanels_TabbedPane.remove(concordiaLayeredPane);
                graphPanels_TabbedPane.remove(weightedMeanLayeredPane);
                graphPanels_TabbedPane.remove(normedProbabilityLayeredPane);
                graphPanels_TabbedPane.remove(useriesIsochronLayeredPane);
                graphPanels_TabbedPane.remove(any2LayeredPane);

                choosePDFPeaks_menu.setVisible(false);
                weightedMeansPlotOptions_menu.setVisible(false);
                displayOptions_menu.setVisible(false);
                heatMap_Menu.setVisible(false);
                break;

            default:
        }

    }

    /**
     *
     * @param doReScale the value of doReScale
     * @param inLiveMode the value of inLiveMode
     */
    public void refreshSampleDateInterpretations(boolean doReScale, boolean inLiveMode) {

        String expansionHistory = dateTreeByAliquot.collectExpansionHistory();
        int selRow = dateTreeByAliquot.getSelRow();

        dateTreeByAliquot.buildTree();
        ((DefaultTreeModel) ((JTree) dateTreeByAliquot).getModel()).reload();

        dateTreeByAliquot.expandToHistory(expansionHistory);
        ((JTree) dateTreeByAliquot).setSelectionRow(selRow);
        ((JTree) dateTreeByAliquot).scrollRowToVisible(selRow);

        ((PlottingDetailsDisplayInterface) concordiaGraphPanel).resetPanel(doReScale, inLiveMode);
        ((AbstractPlot) plotAny2Panel).fitMcLeanRegression();
        ((PlottingDetailsDisplayInterface) plotAny2Panel).resetPanel(doReScale, inLiveMode);

        ((PlottingDetailsDisplayInterface) useriesIsochronPanel).resetPanel(doReScale, inLiveMode);
        ((PlottingDetailsDisplayInterface) evolutionPlotPanel).resetPanel(doReScale, inLiveMode);

        try {
            // June 2010 ensures backward compatibility with previous versions that used dummy aliquot in this list
            setupWeightedMeansPanelForAliquots();
        } catch (Exception e) {
        }
        ((PlottingDetailsDisplayInterface) weightedMeanGraphPanel).resetPanel(doReScale, inLiveMode);
        ((PlottingDetailsDisplayInterface) probabilityPanel).resetPanel(doReScale, inLiveMode);

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
            ellipseCenters_checkbox.setSelected(Boolean.parseBoolean(CGO.get("showEllipseCenters")));
            ((ConcordiaGraphPanel) concordiaGraphPanel).setShowEllipseCenters(ellipseCenters_checkbox.isSelected());
        }
        if (CGO.containsKey("showEllipseLabels")) {
            ellipseLabels_checkbox.setSelected(Boolean.parseBoolean(CGO.get("showEllipseLabels")));
            ((ConcordiaGraphPanel) concordiaGraphPanel).setShowEllipseLabels(ellipseLabels_checkbox.isSelected());
        }

        if (CGO.containsKey("showExcludedEllipses")) {
            showExcludedFractions_checkbox.setSelected(Boolean.parseBoolean(CGO.get("showExcludedEllipses")));
            ((ConcordiaGraphPanel) concordiaGraphPanel).setShowExcludedEllipses(showExcludedFractions_checkbox.isSelected());
        }

        if (CGO.containsKey("showFilteredEllipses")) {
            showFilteredFractions_checkbox.setSelected(Boolean.parseBoolean(CGO.get("showFilteredEllipses")));
            ((AliquotDetailsDisplayInterface) concordiaGraphPanel).setShowFilteredEllipses(showFilteredFractions_checkbox.isSelected());
        }

        if (CGO.containsKey("useUncertaintyCrosses")) {
            ((ConcordiaGraphPanel) concordiaGraphPanel).setUseUncertaintyCrosses(Boolean.valueOf(CGO.get("useUncertaintyCrosses")));
        }

        if (CGO.containsKey("showConcordiaErrors")) {
            concordiaErrors_checkbox.setSelected(Boolean.parseBoolean(CGO.get("showConcordiaErrors")));
            ((ConcordiaGraphPanel) concordiaGraphPanel).setShowConcordiaErrorBars(concordiaErrors_checkbox.isSelected());
        }
        if (sample.isSampleTypeLegacy() && (!sample.getSampleAnalysisType().equalsIgnoreCase(SampleAnalysisTypesEnum.IDTIMS.getName()))) {
            thoriumCorrectionSelector_checkbox.setEnabled(false);
            protactiniumCorrectionSelector_checkbox.setEnabled(false);
        } else {
            if (CGO.containsKey("display_r206_238r_Th")) {
                boolean display_r206_238r_Th = Boolean.parseBoolean(CGO.get("display_r206_238r_Th"));
                thoriumCorrectionSelector_checkbox.setSelected(display_r206_238r_Th);
                ((ConcordiaGraphPanel) concordiaGraphPanel).setDisplay_r206_238r_Th(display_r206_238r_Th);
            }
            if (CGO.containsKey("display_r206_238r_Pa")) {
                boolean display_r206_238r_Pa = Boolean.parseBoolean(CGO.get("display_r206_238r_Pa"));
                protactiniumCorrectionSelector_checkbox.setSelected(display_r206_238r_Pa);
                ((ConcordiaGraphPanel) concordiaGraphPanel).setDisplay_r206_238r_Pa(display_r206_238r_Pa);
            }
            if (CGO.containsKey("display_PbcCorr")) {
                boolean display_PbcCorr = Boolean.parseBoolean(CGO.get("display_PbcCorr"));
                commonLeadCorrectionSelector_checkbox.setSelected(display_PbcCorr);
                ((ConcordiaGraphPanel) concordiaGraphPanel).setDisplay_PbcCorr(display_PbcCorr);
            }
        }

        ((ConcordiaGraphPanel) concordiaGraphPanel).setShowTitleBox(true);

        concordiaLayeredPane.add(concordiaGraphPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        // zoom buttons
        zoomInX2_button.addActionListener((ActionEvent arg0) -> {
            ((PlottingDetailsDisplayInterface) concordiaGraphPanel).performZoom(4.0);
            ((PlottingDetailsDisplayInterface) concordiaGraphPanel).getCurrentPlotAxesSetup().setUseAutomaticAxisTics(true);
            concordiaGraphPanel.repaint();
        });

        zoomOutX2_button.addActionListener((ActionEvent arg0) -> {
            ((PlottingDetailsDisplayInterface) concordiaGraphPanel).performZoom(-2.0);
            ((PlottingDetailsDisplayInterface) concordiaGraphPanel).getCurrentPlotAxesSetup().setUseAutomaticAxisTics(true);
            concordiaGraphPanel.repaint();
        });

    }

    private void initPlotAny2Panel() {
        ((AbstractPlot) plotAny2Panel).setGraphPanelModeChanger(this);

        // set toolbar choices per options
        Map<String, String> CGO = sample.getSampleDateInterpretationGUISettings().getConcordiaOptions();

        if (CGO.containsKey("showEllipseCenters")) {
            ellipseCenters_checkbox.setSelected(Boolean.parseBoolean(CGO.get("showEllipseCenters")));
            ((AbstractPlot) plotAny2Panel).setShowEllipseCenters(ellipseCenters_checkbox.isSelected());
        }
        if (CGO.containsKey("showEllipseLabels")) {
            ellipseLabels_checkbox.setSelected(Boolean.parseBoolean(CGO.get("showEllipseLabels")));
            ((AbstractPlot) plotAny2Panel).setShowEllipseLabels(ellipseLabels_checkbox.isSelected());
        }

        if (CGO.containsKey("showExcludedEllipses")) {
            showExcludedFractions_checkbox.setSelected(Boolean.parseBoolean(CGO.get("showExcludedEllipses")));
            ((AbstractPlot) plotAny2Panel).setShowExcludedEllipses(showExcludedFractions_checkbox.isSelected());
        }

        if (CGO.containsKey("showFilteredEllipses")) {
            showFilteredFractions_checkbox.setSelected(Boolean.parseBoolean(CGO.get("showFilteredEllipses")));
            ((AbstractPlot) plotAny2Panel).setShowFilteredEllipses(showFilteredFractions_checkbox.isSelected());
        }

        if (CGO.containsKey("useUncertaintyCrosses")) {
            ((AbstractPlot) plotAny2Panel).setUseUncertaintyCrosses(Boolean.parseBoolean(CGO.get("useUncertaintyCrosses")));
        }

        any2LayeredPane.add(plotAny2Panel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        // zoom buttons
        zoomInAny2X2_button.addActionListener((ActionEvent arg0) -> {
            ((PlottingDetailsDisplayInterface) plotAny2Panel).performZoom(4.0);
            ((PlottingDetailsDisplayInterface) plotAny2Panel).getCurrentPlotAxesSetup().setUseAutomaticAxisTics(true);
            plotAny2Panel.repaint();
        });

        zoomOutAny2X2_button.addActionListener((ActionEvent arg0) -> {
            ((PlottingDetailsDisplayInterface) plotAny2Panel).performZoom(-2.0);
            ((PlottingDetailsDisplayInterface) plotAny2Panel).getCurrentPlotAxesSetup().setUseAutomaticAxisTics(true);
            plotAny2Panel.repaint();
        });
    }

    private void initUseriesIsochronPanel() {
        ((AbstractPlot) useriesIsochronPanel).setGraphPanelModeChanger(this);

        // set toolbar choices per options
        Map<String, String> UIO = sample.getSampleDateInterpretationGUISettings().getuSeriesIsochronOptions();

        if (UIO.containsKey("showEllipseCenters")) {
            ellipseCentersIsochron_Checkbox.setSelected(Boolean.parseBoolean(UIO.get("showEllipseCenters")));
            ((AbstractPlot) useriesIsochronPanel).setShowEllipseCenters(ellipseCentersIsochron_Checkbox.isSelected());
        }
        if (UIO.containsKey("showEllipseLabels")) {
            ellipseLabelsIsochron_checkbox.setSelected(Boolean.parseBoolean(UIO.get("showEllipseLabels")));
            ((AbstractPlot) useriesIsochronPanel).setShowEllipseLabels(ellipseLabelsIsochron_checkbox.isSelected());
        }
        if (UIO.containsKey("showExcludedEllipses")) {
            showExcludedFractionsIsochron_checkbox.setSelected(Boolean.parseBoolean(UIO.get("showExcludedEllipses")));
            ((AbstractPlot) useriesIsochronPanel).setShowExcludedEllipses(showExcludedFractionsIsochron_checkbox.isSelected());
        }
        if (UIO.containsKey("showEquiline")) {
            showEquiLineIsochron_checkbox.setSelected(Boolean.parseBoolean(UIO.get("showEquiline")));
            ((AbstractPlot) useriesIsochronPanel).setShowEquiline(showEquiLineIsochron_checkbox.isSelected());
        }
        if (UIO.containsKey("showRegressionLine")) {
            showRegressionLineIsochron_checkbox.setSelected(Boolean.parseBoolean(UIO.get("showRegressionLine")));
            ((AbstractPlot) useriesIsochronPanel).setShowExcludedEllipses(showRegressionLineIsochron_checkbox.isSelected());
        }
        if (UIO.containsKey("showRegressionUnct")) {
            showRegressionUnctIsochron_checkbox.setSelected(Boolean.parseBoolean(UIO.get("showRegressionUnct")));
            ((AbstractPlot) useriesIsochronPanel).setShowExcludedEllipses(showRegressionUnctIsochron_checkbox.isSelected());
        }
        if (UIO.containsKey("showIsochrons")) {
            showIsochrons_checkbox.setSelected(Boolean.parseBoolean(UIO.get("showIsochrons")));
            ((AbstractPlot) useriesIsochronPanel).setShowExcludedEllipses(showIsochrons_checkbox.isSelected());
        }

        useriesIsochronLayeredPane.add(useriesIsochronPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        // zoom buttons
        zoomInX2Isochron_button.addActionListener((ActionEvent arg0) -> {
            ((PlottingDetailsDisplayInterface) useriesIsochronPanel).performZoom(4.0);
            ((PlottingDetailsDisplayInterface) useriesIsochronPanel).getCurrentPlotAxesSetup().setUseAutomaticAxisTics(true);
            useriesIsochronPanel.repaint();
        });

        zoomOutX2Isochron_button.addActionListener((ActionEvent arg0) -> {
            ((PlottingDetailsDisplayInterface) useriesIsochronPanel).performZoom(-2.0);
            ((PlottingDetailsDisplayInterface) useriesIsochronPanel).getCurrentPlotAxesSetup().setUseAutomaticAxisTics(true);
            useriesIsochronPanel.repaint();
        });
    }

    private void initEvolutionPlotPanel() {

        evolutionLayeredPane.add(evolutionPlotPanel);

        int heightWE = evolutionLayeredPane.getHeight();
        int widthWE = evolutionLayeredPane.getWidth();

        evolutionToolPanel.setBounds(
                1,
                heightWE - 27,
                widthWE,
                35);

        ((AliquotDetailsDisplayInterface) evolutionPlotPanel).//
                setSelectedFractions(sample.getFractions());
        ((AbstractDataView) evolutionPlotPanel).refreshPanel(true);

    }

    /**
     *
     */
    @Override
    public void repaintActiveTab() {
        concordiaGraphPanel.repaint();

        plotAny2Panel.repaint();
        try {
            useriesIsochronPanel.repaint();
        } catch (Exception e) {
        }

        try {
            evolutionPlotPanel.repaint();
        } catch (Exception e) {
        }

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
            jrb.addActionListener((ActionEvent arg0) -> {
                weightedMeanOptions.put("fractionSortOrder", arg0.getActionCommand());
                weightedMeanGraphPanel.repaint();
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
        zoomInX2_WeightedMean_button.addActionListener((ActionEvent arg0) -> {
            double rangeY = ((WeightedMeanGraphPanel) weightedMeanGraphPanel).getRangeY();

            ((WeightedMeanGraphPanel) weightedMeanGraphPanel).//
                    setMinY(((WeightedMeanGraphPanel) weightedMeanGraphPanel).getMinY()//
                            + rangeY / 4.0);

            ((WeightedMeanGraphPanel) weightedMeanGraphPanel).//
                    setMaxY(((WeightedMeanGraphPanel) weightedMeanGraphPanel).getMaxY()//
                            - rangeY / 4.0);

            weightedMeanGraphPanel.repaint();
        });

        zoomOutX2_WeightedMean_button.addActionListener((ActionEvent arg0) -> {
            double rangeY = ((WeightedMeanGraphPanel) weightedMeanGraphPanel).getRangeY();

            ((WeightedMeanGraphPanel) weightedMeanGraphPanel).//
                    setMinY(((WeightedMeanGraphPanel) weightedMeanGraphPanel).getMinY()//
                            - rangeY / 2.0);

            ((WeightedMeanGraphPanel) weightedMeanGraphPanel).//
                    setMaxY(((WeightedMeanGraphPanel) weightedMeanGraphPanel).getMaxY()//
                            + rangeY / 2.0);

            weightedMeanGraphPanel.repaint();
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
            jrb.addActionListener((ActionEvent arg0) -> {
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
        zoomInProbability_button.addActionListener((ActionEvent arg0) -> {
            ((DateProbabilityDensityPanel) probabilityPanel).zoomIn();
        });

        zoomOutProbability_button.addActionListener((ActionEvent arg0) -> {
            double rangeX = ((DateProbabilityDensityPanel) probabilityPanel).getRangeX_Display();

            double saveMinx = ((DateProbabilityDensityPanel) probabilityPanel).getMinX();
            double proposedMinX = saveMinx - rangeX / 2.0;

            ((DateProbabilityDensityPanel) probabilityPanel).//
                    setMinX(Math.max(
                            proposedMinX, DateProbabilityDensityPanel.DEFAULT_DISPLAY_MINX));

            // reset offset if hit the left wall
            double shiftMax = 0;
            if (proposedMinX <= DateProbabilityDensityPanel.DEFAULT_DISPLAY_MINX) {
                ((DateProbabilityDensityPanel) probabilityPanel).setDisplayOffsetX(0);
                shiftMax = DateProbabilityDensityPanel.DEFAULT_DISPLAY_MINX - proposedMinX;
            }

            ((DateProbabilityDensityPanel) probabilityPanel).//
                    setMaxX(Math.min(
                            (((DateProbabilityDensityPanel) probabilityPanel).getMaxX()//
                            + rangeX / 2.0 + shiftMax), DateProbabilityDensityPanel.DEFAULT_DISPLAY_MAXX));

            ((DateProbabilityDensityPanel) probabilityPanel).//
                    setSelectedHistogramBinCount(0);

            probabilityPanel.repaint();
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
        binWidth_text.addActionListener((ActionEvent e) -> {
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
        } // capture enter key
        );

        // pass bin width textField into panel
        ((DateProbabilityDensityPanel) probabilityPanel).setExternalBinWidthTextField((ReduxSuppressComponentEventsI) binWidth_text);

        normedProbabilityLayeredPane.add(probabilityPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        performFilteringPerSliders(false);

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

                performFilteringPerSliders(false);

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

                probabilityChartOptions.put(slider.getName(), Integer.toString(slider.getValue()));

                performFilteringPerSliders(false);

                // oct 2016
                parentFrame.refreshReportTableData();

                wasChanging = false;
            }
        }
    }

    /**
     *
     * @param myClearFiltering
     * @param clearFiltering the value of clearFiltering
     */
    public void performFilteringPerSliders(boolean myClearFiltering) {

        boolean clearFiltering
                = myClearFiltering
                || sample.isSampleTypeLegacy()
                || sample.isAnalysisTypeCOMPILED()
                || sample.isAnalysisTypeUSERIES();

        Vector<ETFractionInterface> filteredFractions;
        if (clearFiltering) {
            filteredFractions = sample.getUpbFractionsUnknown();
        } else {
            filteredFractions = filterActiveUPbFractions(sample.getUpbFractionsUnknown());
        }

        // oct 2016 collect filtered fractions (those that still count) so that report table can show filtered out
        SortedSet<String> filteredFractionIDs = Collections.synchronizedSortedSet(new TreeSet<>());
        for (int i = 0; i < filteredFractions.size(); i++) {
            filteredFractionIDs.add(filteredFractions.get(i).getFractionID());
        }
        //need to also include reference material as all filtered in for report table
        Vector<ETFractionInterface> filteredRefMats = sample.getUpbFractionsReferenceMaterial();
        for (int i = 0; i < filteredRefMats.size(); i++) {
            filteredFractionIDs.add(filteredRefMats.get(i).getFractionID());
        }

        sample.setFilteredFractionIDs(filteredFractionIDs);

        ((AliquotDetailsDisplayInterface) concordiaGraphPanel).//
                setFilteredFractions(filteredFractions);

        ((AliquotDetailsDisplayInterface) plotAny2Panel).//
                setFilteredFractions(filteredFractions);

        try {
            ((AliquotDetailsDisplayInterface) useriesIsochronPanel).//
                    setFilteredFractions(filteredFractions);
        } catch (Exception e) {
        }
        try {
            ((AliquotDetailsDisplayInterface) evolutionPlotPanel).//
                    setFilteredFractions(filteredFractions);
            ((AbstractDataView) evolutionPlotPanel).refreshPanel(true);
        } catch (Exception e) {
        }

        ((DateProbabilityDensityPanel) probabilityPanel).//
                setSelectedFractions(filteredFractions);

        // fire off date model to filter its deselected fractions
        try {
            dateTreeByAliquot.performLastUserSelectionOfSampleDate();
        } catch (Exception selectionError) {
        }
        ((DateProbabilityDensityPanel) probabilityPanel).prepareAndPaintPanel();
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
    public void synchronizePanelSizes(PlotAxesSetupInterface currentGraphAxesSetup) {

        // may 2010 added here to handle opening of an imported .redux file now that we persist size
        int ww = graphPanels_TabbedPane.getWidth();
        int hh = graphPanels_TabbedPane.getHeight();

        int adjustedWidth = ww - 30;
        int adjustedHeight = hh - 75;

        currentGraphAxesSetup.setGraphWidth(adjustedWidth);
        currentGraphAxesSetup.setGraphHeight(adjustedHeight);

        ((AbstractPlot) plotAny2Panel).setGraphWidth(adjustedWidth);
        ((AbstractPlot) plotAny2Panel).setGraphHeight(adjustedHeight);

        try {
            ((AbstractPlot) useriesIsochronPanel).setGraphWidth(adjustedWidth);
            ((AbstractPlot) useriesIsochronPanel).setGraphHeight(adjustedHeight);
        } catch (Exception e) {
        }

        // evolutionPlotPanel.setPreferredSize(new Dimension(adjustedWidth, adjustedHeight - 400));
        ((WeightedMeanGraphPanel) weightedMeanGraphPanel).setGraphWidth(adjustedWidth);
        ((WeightedMeanGraphPanel) weightedMeanGraphPanel).setGraphHeight(adjustedHeight);

        ((DateProbabilityDensityPanel) probabilityPanel).setGraphWidth(adjustedWidth);
        ((DateProbabilityDensityPanel) probabilityPanel).setGraphHeight(adjustedHeight - 15);

    }

    /**
     *
     */
    private void setupWeightedMeansPanelForAliquots() {
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

    private void selectEvolutionIsochrons() {
        DialogEditor myDialog
                = new IsochronsEvolutionSelectorDialog(
                        null,
                        true,
                        ((SampleDateModel) sample.getSampleDateModelByName("DEFAULT")));
        myDialog.setSize(340, 625);
        myDialog.setVisible(true);

        myDialog.dispose();

        ((EvolutionPlotPanelII) evolutionPlotPanel).buildIsochronsAndContours();
        evolutionPlotPanel.repaint();
    }

    // June 2010 new sections for additional tabs
    // any 2 ratios plot tab
    // Revisited for first time in Dec 2016 now that McLeanRegression Library is available
    private void selectAny2Variables() {
        // here we leverage the work of reductionhandler and Kwiki clumps to generate list of
        // possible axes;  need to be sure to include all possible from selected fractions
        // as tracers may vary

        // strategy = complete plan to have reduction handler use matrixspecname instead of matrixspecs
        // then here call a method to build set of all names of matrix components across all fractions chosen
        ETFractionInterface firstFraction = sample.getFractions().get(0);
        Vector<String> variablesListing = new Vector<>();

        if (firstFraction instanceof UPbFraction) {
            variablesListing
                    = CollectionHelpers.vectorSortedUniqueMembers( //
                            MatrixSpecifications.getMatrixSpecsByName(//
                                    ((UPbFraction) firstFraction).getReductionHandler().getMatrixSpecsName()));// "mixed_202_205_233_235_Zircon_NotFcU_FcPb" ));
            // remove lambdas
            variablesListing.remove(Lambdas.lambda230.getName());
            variablesListing.remove(Lambdas.lambda231.getName());
            variablesListing.remove(Lambdas.lambda232.getName());
            variablesListing.remove(Lambdas.lambda234.getName());
            variablesListing.remove(Lambdas.lambda235.getName());
            variablesListing.remove(Lambdas.lambda238.getName());

            // remove variables with no values
            List<String> removals = new ArrayList();
            for (String variable : variablesListing) {
                ValueModel variableValue = firstFraction.retrieveValueModelByName(variable);
                if (variableValue == null) {
                    removals.add(variable);
                }
            }

            for (String variable : removals) {
                variablesListing.remove(variable);
            }

        } else {
            SortedSet<String> varNames = new TreeSet<>();
            ReportColumnInterface[] reportColumns = sample.getReportSettingsModel().getIsotopicRatiosCategory().getCategoryColumns();
            for (ReportColumnInterface reportColumn : reportColumns) {
                if (reportColumn.isVisible()) {
                    varNames.add(reportColumn.getRetrieveVariableName());
                }
            }

            reportColumns = sample.getReportSettingsModel().getCompositionCategory().getCategoryColumns();
            for (ReportColumnInterface reportColumn : reportColumns) {
                if (reportColumn.isVisible()) {
                    varNames.add(reportColumn.getRetrieveVariableName());
                }
            }

            for (String var : varNames) {
                variablesListing.add(var);
            }
        }

        if (variablesListing.isEmpty()) {
            variablesListing.add("NONE AVAILABLE");
        }

        DialogEditor myDialog
                = new SampleDateInterpretationAny2VariablesChooser(null, true, (PlotAny2Panel) plotAny2Panel, variablesListing);
        myDialog.setSize(555, 460);
//        JDialog.setDefaultLookAndFeelDecorated(true);
        myDialog.setVisible(true);

        ((AbstractPlot) plotAny2Panel).fitMcLeanRegression();
        ((PlottingDetailsDisplayInterface) plotAny2Panel).setShowTightToEdges(false);
        ((PlottingDetailsDisplayInterface) plotAny2Panel).resetPanel(true, false);

//        ((AbstractPlot) useriesIsochronPanel).fitMcLeanRegression();
//        ((PlottingDetailsDisplayInterface) useriesIsochronPanel).setShowTightToEdges(false);
//        ((PlottingDetailsDisplayInterface) useriesIsochronPanel).resetPanel(true, false);
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
        showFilteredFractions_checkbox = new javax.swing.JCheckBox();
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
        evolutionLayeredPane = new javax.swing.JLayeredPane();
        evolutionToolPanel = new javax.swing.JPanel();
        resetEvolution_button =  new ET_JButton();
        ellipseCentersEvolutionOnToggle_checkbox = new javax.swing.JCheckBox();
        ellipseLabelsEvolutionOnToggle_checkbox = new javax.swing.JCheckBox();
        zoomBoxEvolution_toggleButton =  new ET_JToggleButton();
        showTightEvolution_toggleButton =  new ET_JToggleButton();
        isochrons_button =  new ET_JButton();
        useriesIsochronLayeredPane = new javax.swing.JLayeredPane();
        uSeriesIsochronToolPanel = new javax.swing.JPanel();
        zoomInX2Isochron_button =  new ET_JButton();
        zoomOutX2Isochron_button =  new ET_JButton();
        resetGraphDisplayIsochron_button =  new ET_JButton();
        ellipseCentersIsochron_Checkbox = new javax.swing.JCheckBox();
        ellipseLabelsIsochron_checkbox = new javax.swing.JCheckBox();
        showEquiLineIsochron_checkbox = new javax.swing.JCheckBox();
        panIsochron_toggleButton =  new ET_JToggleButton();
        zoomBox_toggleIsochron_button =  new ET_JToggleButton();
        thoriumFlavorIsochron_radioButton = new javax.swing.JRadioButton();
        showTightIsochron_toggleButton =  new ET_JToggleButton();
        showExcludedFractionsIsochron_checkbox = new javax.swing.JCheckBox();
        radiumFlavorIsochron_radioButton = new javax.swing.JRadioButton();
        showRegressionLineIsochron_checkbox = new javax.swing.JCheckBox();
        showIsochrons_checkbox = new javax.swing.JCheckBox();
        showRegressionUnctIsochron_checkbox = new javax.swing.JCheckBox();
        any2LayeredPane = new javax.swing.JLayeredPane();
        any2ToolPanel = new javax.swing.JPanel();
        zoomInAny2X2_button =  new ET_JButton();
        zoomOutAny2X2_button =  new ET_JButton();
        resetGraphAny2Display_button =  new ET_JButton();
        ellipseCentersAny2OnToggle_checkbox = new javax.swing.JCheckBox();
        ellipseLabelsAny2OnToggle_checkbox = new javax.swing.JCheckBox();
        panAny2_toggleButton =  new ET_JToggleButton();
        zoomBoxAny2_toggleButton =  new ET_JToggleButton();
        showTightAny2_toggleButton =  new ET_JToggleButton();
        selectAny2_button =  new ET_JButton();
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
        clearFilters_button =  new ET_JButton();
        defaultFilters_button =  new ET_JButton();
        jPanel1 = new javax.swing.JPanel();
        close_button =  new ET_JButton();
        writeVisiblePlotSvgPdf_button =  new ET_JButton();
        sampleAgeGUIMenuBar = new javax.swing.JMenuBar();
        displayOptions_menu = new javax.swing.JMenu();
        sampleConcordiaOptions_menuItem = new javax.swing.JMenuItem();
        aliquotsChooser_menuItem = new javax.swing.JMenuItem();
        aliquotSpecificOptions_menu = new javax.swing.JMenu();
        weightedMeansPlotOptions_menu = new javax.swing.JMenu();
        weightedMeansChooser_menuItem = new javax.swing.JMenuItem();
        sortFractionsDateAsc_menuItemCheckBox = new javax.swing.JCheckBoxMenuItem();
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

        dateTrees_tabs.setForeground(new java.awt.Color(255, 51, 51));
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
        graphPanels_TabbedPane.setOpaque(true);
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
        concordiaLayeredPane.setName("Concordia"); // NOI18N
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

        ellipseCenters_checkbox.setBackground(new java.awt.Color(255, 241, 230));
        ellipseCenters_checkbox.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        ellipseCenters_checkbox.setSelected(true);
        ellipseCenters_checkbox.setText("<html>Ellipse<br> Centers</html>");
        ellipseCenters_checkbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ellipseCenters_checkboxActionPerformed(evt);
            }
        });
        concordiaToolPanel.add(ellipseCenters_checkbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 2, 70, 25));

        ellipseLabels_checkbox.setBackground(new java.awt.Color(255, 241, 230));
        ellipseLabels_checkbox.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        ellipseLabels_checkbox.setSelected(true);
        ellipseLabels_checkbox.setText("<html>Ellipse<br> Labels</html>");
        ellipseLabels_checkbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ellipseLabels_checkboxActionPerformed(evt);
            }
        });
        concordiaToolPanel.add(ellipseLabels_checkbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 2, 70, 25));

        concordiaErrors_checkbox.setBackground(new java.awt.Color(255, 241, 230));
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

        concordiaFlavor_radioButton.setBackground(new java.awt.Color(255, 241, 230));
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

        terraWasserburgFlavor_radioButton.setBackground(new java.awt.Color(255, 241, 230));
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

        thoriumCorrectionSelector_checkbox.setBackground(new java.awt.Color(255, 241, 230));
        thoriumCorrectionSelector_checkbox.setFont(new java.awt.Font("SansSerif", 1, 9)); // NOI18N
        thoriumCorrectionSelector_checkbox.setText("Th");
        thoriumCorrectionSelector_checkbox.setToolTipText("Correct for Thorium");
        thoriumCorrectionSelector_checkbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                thoriumCorrectionSelector_checkboxActionPerformed(evt);
            }
        });
        concordiaToolPanel.add(thoriumCorrectionSelector_checkbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(435, 2, -1, 25));

        protactiniumCorrectionSelector_checkbox.setBackground(new java.awt.Color(255, 241, 230));
        protactiniumCorrectionSelector_checkbox.setFont(new java.awt.Font("SansSerif", 1, 9)); // NOI18N
        protactiniumCorrectionSelector_checkbox.setText("Pa");
        protactiniumCorrectionSelector_checkbox.setToolTipText("Correct for Protactinium");
        protactiniumCorrectionSelector_checkbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                protactiniumCorrectionSelector_checkboxActionPerformed(evt);
            }
        });
        concordiaToolPanel.add(protactiniumCorrectionSelector_checkbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 2, -1, 25));

        jLabel4.setBackground(new java.awt.Color(255, 241, 230));
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

        showExcludedFractions_checkbox.setBackground(new java.awt.Color(255, 241, 230));
        showExcludedFractions_checkbox.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        showExcludedFractions_checkbox.setSelected(true);
        showExcludedFractions_checkbox.setText("<html>Excluded<br> Fractions</html>");
        showExcludedFractions_checkbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showExcludedFractions_checkboxActionPerformed(evt);
            }
        });
        concordiaToolPanel.add(showExcludedFractions_checkbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 2, 75, 25));

        thoriumConcordiaFlavor_radioButton.setBackground(new java.awt.Color(255, 241, 230));
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

        showFilteredFractions_checkbox.setBackground(new java.awt.Color(255, 241, 230));
        showFilteredFractions_checkbox.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        showFilteredFractions_checkbox.setText("<html>Filtering ON<br> </html>");
        showFilteredFractions_checkbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showFilteredFractions_checkboxActionPerformed(evt);
            }
        });
        concordiaToolPanel.add(showFilteredFractions_checkbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 2, 80, 25));

        commonLeadCorrectionSelector_checkbox.setBackground(new java.awt.Color(255, 241, 230));
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
        concordiaToolPanel.setBounds(0, 604, 920, 36);

        graphPanels_TabbedPane.addTab("Concordia", concordiaLayeredPane);

        weightedMeanLayeredPane.setBackground(new java.awt.Color(229, 250, 229));
        weightedMeanLayeredPane.setName("WeightedMean"); // NOI18N
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
        zoomInX2_WeightedMean_button.setOpaque(true);
        zoomInX2_WeightedMean_button.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        weightedMeanToolPanel.add(zoomInX2_WeightedMean_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 2, 30, 30));

        zoomOutX2_WeightedMean_button.setBackground(new java.awt.Color(255, 255, 255));
        zoomOutX2_WeightedMean_button.setFont(new java.awt.Font("Braggadocio", 1, 24)); // NOI18N
        zoomOutX2_WeightedMean_button.setText("-");
        zoomOutX2_WeightedMean_button.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        zoomOutX2_WeightedMean_button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        zoomOutX2_WeightedMean_button.setOpaque(true);
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
        restoreGraphDisplay_WeightedMean_button.setOpaque(true);
        restoreGraphDisplay_WeightedMean_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                restoreGraphDisplay_WeightedMean_buttonActionPerformed(evt);
            }
        });
        weightedMeanToolPanel.add(restoreGraphDisplay_WeightedMean_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(62, 2, 51, 30));

        pan_WeightedMean_toggleButton.setBackground(new java.awt.Color(255, 255, 255));
        weightedMeanPanZoom_buttonGroup.add(pan_WeightedMean_toggleButton);
        pan_WeightedMean_toggleButton.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        pan_WeightedMean_toggleButton.setText("Pan");
        pan_WeightedMean_toggleButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        pan_WeightedMean_toggleButton.setName("PAN"); // NOI18N
        pan_WeightedMean_toggleButton.setOpaque(true);
        weightedMeanToolPanel.add(pan_WeightedMean_toggleButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(113, 2, 28, 30));

        jLabel1.setBackground(new java.awt.Color(229, 250, 229));
        jLabel1.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        jLabel1.setText("Order:");
        weightedMeanToolPanel.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(369, 9, -1, -1));

        fractionOrderByName_radioButton.setBackground(new java.awt.Color(229, 250, 229));
        weightedMeanFractionOrderButtonGroup.add(fractionOrderByName_radioButton);
        fractionOrderByName_radioButton.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        fractionOrderByName_radioButton.setText("name");
        fractionOrderByName_radioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        fractionOrderByName_radioButton.setName("name"); // NOI18N
        weightedMeanToolPanel.add(fractionOrderByName_radioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(412, 6, -1, -1));

        fractionOrderByWeight_radioButton.setBackground(new java.awt.Color(229, 250, 229));
        weightedMeanFractionOrderButtonGroup.add(fractionOrderByWeight_radioButton);
        fractionOrderByWeight_radioButton.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        fractionOrderByWeight_radioButton.setText("weight");
        fractionOrderByWeight_radioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        fractionOrderByWeight_radioButton.setName("weight"); // NOI18N
        weightedMeanToolPanel.add(fractionOrderByWeight_radioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(476, 6, -1, -1));

        fractionOrderByRandom_radioButton.setBackground(new java.awt.Color(229, 250, 229));
        weightedMeanFractionOrderButtonGroup.add(fractionOrderByRandom_radioButton);
        fractionOrderByRandom_radioButton.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        fractionOrderByRandom_radioButton.setText("RND");
        fractionOrderByRandom_radioButton.setActionCommand("random");
        fractionOrderByRandom_radioButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        fractionOrderByRandom_radioButton.setName("random"); // NOI18N
        weightedMeanToolPanel.add(fractionOrderByRandom_radioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(555, 6, -1, -1));

        fractionOrderByDate_radioButton.setBackground(new java.awt.Color(229, 250, 229));
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
        weightedMeanToolPanel.setBounds(1, 604, 910, 36);

        graphPanels_TabbedPane.addTab("Weighted Mean", weightedMeanLayeredPane);

        evolutionLayeredPane.setBackground(new java.awt.Color(231, 255, 253));
        evolutionLayeredPane.setName("Evolution"); // NOI18N
        evolutionLayeredPane.setOpaque(true);

        evolutionToolPanel.setBackground(new java.awt.Color(231, 255, 253));
        evolutionToolPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        evolutionToolPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        resetEvolution_button.setBackground(new java.awt.Color(255, 255, 255));
        resetEvolution_button.setFont(new java.awt.Font("SansSerif", 1, 9)); // NOI18N
        resetEvolution_button.setText("Reset");
        resetEvolution_button.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        resetEvolution_button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        resetEvolution_button.setOpaque(true);
        resetEvolution_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetEvolution_buttonActionPerformed(evt);
            }
        });
        evolutionToolPanel.add(resetEvolution_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 2, 35, 30));

        ellipseCentersEvolutionOnToggle_checkbox.setBackground(new java.awt.Color(231, 255, 253));
        ellipseCentersEvolutionOnToggle_checkbox.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        ellipseCentersEvolutionOnToggle_checkbox.setSelected(true);
        ellipseCentersEvolutionOnToggle_checkbox.setText("Ellipse Centers");
        ellipseCentersEvolutionOnToggle_checkbox.setOpaque(true);
        ellipseCentersEvolutionOnToggle_checkbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ellipseCentersEvolutionOnToggle_checkboxActionPerformed(evt);
            }
        });
        evolutionToolPanel.add(ellipseCentersEvolutionOnToggle_checkbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(556, 6, -1, -1));

        ellipseLabelsEvolutionOnToggle_checkbox.setBackground(new java.awt.Color(231, 255, 253));
        ellipseLabelsEvolutionOnToggle_checkbox.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        ellipseLabelsEvolutionOnToggle_checkbox.setText("Labels");
        ellipseLabelsEvolutionOnToggle_checkbox.setOpaque(true);
        ellipseLabelsEvolutionOnToggle_checkbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ellipseLabelsEvolutionOnToggle_checkboxActionPerformed(evt);
            }
        });
        evolutionToolPanel.add(ellipseLabelsEvolutionOnToggle_checkbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(685, 6, -1, -1));

        zoomBoxEvolution_toggleButton.setBackground(new java.awt.Color(255, 255, 255));
        concordiaPanZoom_buttonGroup.add(zoomBoxEvolution_toggleButton);
        zoomBoxEvolution_toggleButton.setFont(new java.awt.Font("SansSerif", 1, 9)); // NOI18N
        zoomBoxEvolution_toggleButton.setText("Zoom-Box");
        zoomBoxEvolution_toggleButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        zoomBoxEvolution_toggleButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        zoomBoxEvolution_toggleButton.setName("ZOOM"); // NOI18N
        zoomBoxEvolution_toggleButton.setOpaque(true);
        zoomBoxEvolution_toggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoomBoxEvolution_toggleButtonActionPerformed(evt);
            }
        });
        evolutionToolPanel.add(zoomBoxEvolution_toggleButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(95, 2, 55, 30));

        showTightEvolution_toggleButton.setBackground(new java.awt.Color(255, 255, 255));
        concordiaPanZoom_buttonGroup.add(showTightEvolution_toggleButton);
        showTightEvolution_toggleButton.setFont(new java.awt.Font("SansSerif", 1, 9)); // NOI18N
        showTightEvolution_toggleButton.setText("Tight");
        showTightEvolution_toggleButton.setToolTipText("Zooms in until ellipses touch borders.");
        showTightEvolution_toggleButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        showTightEvolution_toggleButton.setContentAreaFilled(false);
        showTightEvolution_toggleButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        showTightEvolution_toggleButton.setName("TIGHT"); // NOI18N
        showTightEvolution_toggleButton.setOpaque(true);
        showTightEvolution_toggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showTightEvolution_toggleButtonActionPerformed(evt);
            }
        });
        evolutionToolPanel.add(showTightEvolution_toggleButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 2, 30, 30));

        isochrons_button.setBackground(new java.awt.Color(255, 255, 255));
        isochrons_button.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        isochrons_button.setText("Isochrons");
        isochrons_button.setAlignmentY(0.0F);
        isochrons_button.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        isochrons_button.setFocusable(false);
        isochrons_button.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        isochrons_button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        isochrons_button.setOpaque(true);
        isochrons_button.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        isochrons_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                isochrons_buttonActionPerformed(evt);
            }
        });
        evolutionToolPanel.add(isochrons_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 2, 70, 30));

        evolutionLayeredPane.add(evolutionToolPanel);
        evolutionToolPanel.setBounds(0, 604, 790, 35);

        graphPanels_TabbedPane.addTab("Evolution", evolutionLayeredPane);

        useriesIsochronLayeredPane.setBackground(new java.awt.Color(255, 237, 255));
        useriesIsochronLayeredPane.setName("UseriesIsochrons"); // NOI18N
        useriesIsochronLayeredPane.setOpaque(true);

        uSeriesIsochronToolPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        uSeriesIsochronToolPanel.setMinimumSize(new java.awt.Dimension(0, 0));
        uSeriesIsochronToolPanel.setOpaque(false);
        uSeriesIsochronToolPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        zoomInX2Isochron_button.setBackground(new java.awt.Color(255, 255, 255));
        zoomInX2Isochron_button.setFont(new java.awt.Font("Braggadocio", 1, 24)); // NOI18N
        zoomInX2Isochron_button.setText("+");
        zoomInX2Isochron_button.setAlignmentY(0.0F);
        zoomInX2Isochron_button.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        zoomInX2Isochron_button.setContentAreaFilled(false);
        zoomInX2Isochron_button.setFocusable(false);
        zoomInX2Isochron_button.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        zoomInX2Isochron_button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        zoomInX2Isochron_button.setOpaque(true);
        zoomInX2Isochron_button.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        uSeriesIsochronToolPanel.add(zoomInX2Isochron_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 2, 30, 30));

        zoomOutX2Isochron_button.setBackground(new java.awt.Color(255, 255, 255));
        zoomOutX2Isochron_button.setFont(new java.awt.Font("Braggadocio", 1, 24)); // NOI18N
        zoomOutX2Isochron_button.setText("-");
        zoomOutX2Isochron_button.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        zoomOutX2Isochron_button.setContentAreaFilled(false);
        zoomOutX2Isochron_button.setOpaque(true);
        uSeriesIsochronToolPanel.add(zoomOutX2Isochron_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 2, 30, 30));

        resetGraphDisplayIsochron_button.setBackground(new java.awt.Color(255, 255, 255));
        resetGraphDisplayIsochron_button.setFont(new java.awt.Font("SansSerif", 1, 9)); // NOI18N
        resetGraphDisplayIsochron_button.setText("Reset");
        resetGraphDisplayIsochron_button.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        resetGraphDisplayIsochron_button.setContentAreaFilled(false);
        resetGraphDisplayIsochron_button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        resetGraphDisplayIsochron_button.setOpaque(true);
        resetGraphDisplayIsochron_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetGraphDisplayIsochron_buttonActionPerformed(evt);
            }
        });
        uSeriesIsochronToolPanel.add(resetGraphDisplayIsochron_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 2, 35, 30));

        ellipseCentersIsochron_Checkbox.setBackground(new java.awt.Color(255, 237, 255));
        ellipseCentersIsochron_Checkbox.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        ellipseCentersIsochron_Checkbox.setSelected(true);
        ellipseCentersIsochron_Checkbox.setText("<html>Ellipse<br> Centers</html>");
        ellipseCentersIsochron_Checkbox.setOpaque(true);
        ellipseCentersIsochron_Checkbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ellipseCentersIsochron_CheckboxActionPerformed(evt);
            }
        });
        uSeriesIsochronToolPanel.add(ellipseCentersIsochron_Checkbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(690, 2, 70, 25));

        ellipseLabelsIsochron_checkbox.setBackground(new java.awt.Color(255, 237, 255));
        ellipseLabelsIsochron_checkbox.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        ellipseLabelsIsochron_checkbox.setText("<html>Ellipse<br> Labels</html>");
        ellipseLabelsIsochron_checkbox.setOpaque(true);
        ellipseLabelsIsochron_checkbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ellipseLabelsIsochron_checkboxActionPerformed(evt);
            }
        });
        uSeriesIsochronToolPanel.add(ellipseLabelsIsochron_checkbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(760, 2, 70, 25));

        showEquiLineIsochron_checkbox.setBackground(new java.awt.Color(255, 237, 255));
        showEquiLineIsochron_checkbox.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        showEquiLineIsochron_checkbox.setSelected(true);
        showEquiLineIsochron_checkbox.setText("<html>Equi-<br>Line</html>");
        showEquiLineIsochron_checkbox.setName(""); // NOI18N
        showEquiLineIsochron_checkbox.setOpaque(true);
        showEquiLineIsochron_checkbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showEquiLineIsochron_checkboxActionPerformed(evt);
            }
        });
        uSeriesIsochronToolPanel.add(showEquiLineIsochron_checkbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 2, 77, 25));

        panIsochron_toggleButton.setBackground(new java.awt.Color(255, 255, 255));
        concordiaPanZoom_buttonGroup.add(panIsochron_toggleButton);
        panIsochron_toggleButton.setFont(new java.awt.Font("SansSerif", 1, 9)); // NOI18N
        panIsochron_toggleButton.setText("Pan");
        panIsochron_toggleButton.setToolTipText("Use mouse to pan image");
        panIsochron_toggleButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panIsochron_toggleButton.setContentAreaFilled(false);
        panIsochron_toggleButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        panIsochron_toggleButton.setName("PAN"); // NOI18N
        panIsochron_toggleButton.setOpaque(true);
        panIsochron_toggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                panIsochron_toggleButtonActionPerformed(evt);
            }
        });
        uSeriesIsochronToolPanel.add(panIsochron_toggleButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(95, 2, 30, 30));

        zoomBox_toggleIsochron_button.setBackground(new java.awt.Color(255, 255, 255));
        concordiaPanZoom_buttonGroup.add(zoomBox_toggleIsochron_button);
        zoomBox_toggleIsochron_button.setFont(new java.awt.Font("SansSerif", 1, 9)); // NOI18N
        zoomBox_toggleIsochron_button.setText("Zoom-Box");
        zoomBox_toggleIsochron_button.setToolTipText("Use mouse to define zoom-to box");
        zoomBox_toggleIsochron_button.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        zoomBox_toggleIsochron_button.setContentAreaFilled(false);
        zoomBox_toggleIsochron_button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        zoomBox_toggleIsochron_button.setName("ZOOM"); // NOI18N
        zoomBox_toggleIsochron_button.setOpaque(true);
        zoomBox_toggleIsochron_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoomBox_toggleIsochron_buttonActionPerformed(evt);
            }
        });
        uSeriesIsochronToolPanel.add(zoomBox_toggleIsochron_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(125, 2, 55, 30));

        thoriumFlavorIsochron_radioButton.setBackground(new java.awt.Color(255, 237, 255));
        concordiaTeraW_buttonGroup.add(thoriumFlavorIsochron_radioButton);
        thoriumFlavorIsochron_radioButton.setFont(new java.awt.Font("SansSerif", 1, 9)); // NOI18N
        thoriumFlavorIsochron_radioButton.setText("Th");
        thoriumFlavorIsochron_radioButton.setToolTipText("Wetherill Concordia");
        thoriumFlavorIsochron_radioButton.setOpaque(true);
        thoriumFlavorIsochron_radioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                thoriumFlavorIsochron_radioButtonActionPerformed(evt);
            }
        });
        uSeriesIsochronToolPanel.add(thoriumFlavorIsochron_radioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 1, -1, 28));

        showTightIsochron_toggleButton.setBackground(new java.awt.Color(255, 255, 255));
        concordiaPanZoom_buttonGroup.add(showTightIsochron_toggleButton);
        showTightIsochron_toggleButton.setFont(new java.awt.Font("SansSerif", 1, 9)); // NOI18N
        showTightIsochron_toggleButton.setText("Tight");
        showTightIsochron_toggleButton.setToolTipText("Zooms in until ellipses touch borders.");
        showTightIsochron_toggleButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        showTightIsochron_toggleButton.setContentAreaFilled(false);
        showTightIsochron_toggleButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        showTightIsochron_toggleButton.setName("TIGHT"); // NOI18N
        showTightIsochron_toggleButton.setOpaque(true);
        showTightIsochron_toggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showTightIsochron_toggleButtonActionPerformed(evt);
            }
        });
        uSeriesIsochronToolPanel.add(showTightIsochron_toggleButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 2, 30, 30));

        showExcludedFractionsIsochron_checkbox.setBackground(new java.awt.Color(255, 237, 255));
        showExcludedFractionsIsochron_checkbox.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        showExcludedFractionsIsochron_checkbox.setSelected(true);
        showExcludedFractionsIsochron_checkbox.setText("<html>Excluded<br> Fractions</html>");
        showExcludedFractionsIsochron_checkbox.setOpaque(true);
        showExcludedFractionsIsochron_checkbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showExcludedFractionsIsochron_checkboxActionPerformed(evt);
            }
        });
        uSeriesIsochronToolPanel.add(showExcludedFractionsIsochron_checkbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 2, 75, 25));

        radiumFlavorIsochron_radioButton.setBackground(new java.awt.Color(255, 237, 255));
        concordiaTeraW_buttonGroup.add(radiumFlavorIsochron_radioButton);
        radiumFlavorIsochron_radioButton.setFont(new java.awt.Font("SansSerif", 1, 9)); // NOI18N
        radiumFlavorIsochron_radioButton.setText("Ra");
        radiumFlavorIsochron_radioButton.setToolTipText("Thorium-based Wetherill Concordia");
        radiumFlavorIsochron_radioButton.setOpaque(true);
        radiumFlavorIsochron_radioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                radiumFlavorIsochron_radioButtonActionPerformed(evt);
            }
        });
        uSeriesIsochronToolPanel.add(radiumFlavorIsochron_radioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 1, -1, 28));

        showRegressionLineIsochron_checkbox.setBackground(new java.awt.Color(255, 237, 255));
        showRegressionLineIsochron_checkbox.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        showRegressionLineIsochron_checkbox.setSelected(true);
        showRegressionLineIsochron_checkbox.setText("<html>Regression<br>Line</html>");
        showRegressionLineIsochron_checkbox.setOpaque(true);
        showRegressionLineIsochron_checkbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showRegressionLineIsochron_checkboxActionPerformed(evt);
            }
        });
        uSeriesIsochronToolPanel.add(showRegressionLineIsochron_checkbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 2, 90, 25));

        showIsochrons_checkbox.setBackground(new java.awt.Color(255, 237, 255));
        showIsochrons_checkbox.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        showIsochrons_checkbox.setSelected(true);
        showIsochrons_checkbox.setText("<html>Isochrons</html>");
        showIsochrons_checkbox.setOpaque(true);
        showIsochrons_checkbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showIsochrons_checkboxActionPerformed(evt);
            }
        });
        uSeriesIsochronToolPanel.add(showIsochrons_checkbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(325, 2, 90, 25));

        showRegressionUnctIsochron_checkbox.setBackground(new java.awt.Color(255, 237, 255));
        showRegressionUnctIsochron_checkbox.setFont(new java.awt.Font("SansSerif", 1, 10)); // NOI18N
        showRegressionUnctIsochron_checkbox.setText("<html>Regression<br>Uncertainty</html>");
        showRegressionUnctIsochron_checkbox.setOpaque(true);
        showRegressionUnctIsochron_checkbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showRegressionUnctIsochron_checkboxActionPerformed(evt);
            }
        });
        uSeriesIsochronToolPanel.add(showRegressionUnctIsochron_checkbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 2, 90, 25));

        useriesIsochronLayeredPane.add(uSeriesIsochronToolPanel);
        uSeriesIsochronToolPanel.setBounds(0, 604, 920, 36);

        graphPanels_TabbedPane.addTab("USeries Isochrons", useriesIsochronLayeredPane);

        any2LayeredPane.setBackground(new java.awt.Color(231, 255, 253));
        any2LayeredPane.setName("Any2"); // NOI18N
        any2LayeredPane.setOpaque(true);

        any2ToolPanel.setBackground(new java.awt.Color(231, 255, 253));
        any2ToolPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        any2ToolPanel.setOpaque(false);
        any2ToolPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        zoomInAny2X2_button.setBackground(new java.awt.Color(255, 255, 255));
        zoomInAny2X2_button.setFont(new java.awt.Font("Braggadocio", 1, 24)); // NOI18N
        zoomInAny2X2_button.setText("+");
        zoomInAny2X2_button.setAlignmentY(0.0F);
        zoomInAny2X2_button.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        zoomInAny2X2_button.setFocusable(false);
        zoomInAny2X2_button.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        zoomInAny2X2_button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        zoomInAny2X2_button.setOpaque(true);
        zoomInAny2X2_button.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        zoomInAny2X2_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoomInAny2X2_buttonActionPerformed(evt);
            }
        });
        any2ToolPanel.add(zoomInAny2X2_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 2, 30, 30));

        zoomOutAny2X2_button.setBackground(new java.awt.Color(255, 255, 255));
        zoomOutAny2X2_button.setFont(new java.awt.Font("Braggadocio", 1, 24)); // NOI18N
        zoomOutAny2X2_button.setText("-");
        zoomOutAny2X2_button.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        zoomOutAny2X2_button.setOpaque(true);
        any2ToolPanel.add(zoomOutAny2X2_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 2, 30, 30));

        resetGraphAny2Display_button.setBackground(new java.awt.Color(255, 255, 255));
        resetGraphAny2Display_button.setFont(new java.awt.Font("SansSerif", 1, 9)); // NOI18N
        resetGraphAny2Display_button.setText("Reset");
        resetGraphAny2Display_button.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        resetGraphAny2Display_button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        resetGraphAny2Display_button.setOpaque(true);
        resetGraphAny2Display_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetGraphAny2Display_buttonActionPerformed(evt);
            }
        });
        any2ToolPanel.add(resetGraphAny2Display_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 2, 35, 30));

        ellipseCentersAny2OnToggle_checkbox.setBackground(new java.awt.Color(231, 255, 253));
        ellipseCentersAny2OnToggle_checkbox.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        ellipseCentersAny2OnToggle_checkbox.setSelected(true);
        ellipseCentersAny2OnToggle_checkbox.setText("Ellipse Centers");
        ellipseCentersAny2OnToggle_checkbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ellipseCentersAny2OnToggle_checkboxActionPerformed(evt);
            }
        });
        any2ToolPanel.add(ellipseCentersAny2OnToggle_checkbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(556, 6, -1, -1));

        ellipseLabelsAny2OnToggle_checkbox.setBackground(new java.awt.Color(231, 255, 253));
        ellipseLabelsAny2OnToggle_checkbox.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        ellipseLabelsAny2OnToggle_checkbox.setSelected(true);
        ellipseLabelsAny2OnToggle_checkbox.setText("Labels");
        ellipseLabelsAny2OnToggle_checkbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ellipseLabelsAny2OnToggle_checkboxActionPerformed(evt);
            }
        });
        any2ToolPanel.add(ellipseLabelsAny2OnToggle_checkbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(685, 6, -1, -1));

        panAny2_toggleButton.setBackground(new java.awt.Color(255, 255, 255));
        concordiaPanZoom_buttonGroup.add(panAny2_toggleButton);
        panAny2_toggleButton.setFont(new java.awt.Font("SansSerif", 1, 9)); // NOI18N
        panAny2_toggleButton.setText("Pan");
        panAny2_toggleButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        panAny2_toggleButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        panAny2_toggleButton.setName("PAN"); // NOI18N
        panAny2_toggleButton.setOpaque(true);
        panAny2_toggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                panAny2_toggleButtonActionPerformed(evt);
            }
        });
        any2ToolPanel.add(panAny2_toggleButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(95, 2, 30, 30));

        zoomBoxAny2_toggleButton.setBackground(new java.awt.Color(255, 255, 255));
        concordiaPanZoom_buttonGroup.add(zoomBoxAny2_toggleButton);
        zoomBoxAny2_toggleButton.setFont(new java.awt.Font("SansSerif", 1, 9)); // NOI18N
        zoomBoxAny2_toggleButton.setText("Zoom-Box");
        zoomBoxAny2_toggleButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        zoomBoxAny2_toggleButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        zoomBoxAny2_toggleButton.setName("ZOOM"); // NOI18N
        zoomBoxAny2_toggleButton.setOpaque(true);
        zoomBoxAny2_toggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoomBoxAny2_toggleButtonActionPerformed(evt);
            }
        });
        any2ToolPanel.add(zoomBoxAny2_toggleButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(125, 2, 55, 30));

        showTightAny2_toggleButton.setBackground(new java.awt.Color(255, 255, 255));
        concordiaPanZoom_buttonGroup.add(showTightAny2_toggleButton);
        showTightAny2_toggleButton.setFont(new java.awt.Font("SansSerif", 1, 9)); // NOI18N
        showTightAny2_toggleButton.setText("Tight");
        showTightAny2_toggleButton.setToolTipText("Zooms in until ellipses touch borders.");
        showTightAny2_toggleButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        showTightAny2_toggleButton.setContentAreaFilled(false);
        showTightAny2_toggleButton.setMargin(new java.awt.Insets(0, 0, 0, 0));
        showTightAny2_toggleButton.setName("TIGHT"); // NOI18N
        showTightAny2_toggleButton.setOpaque(true);
        showTightAny2_toggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showTightAny2_toggleButtonActionPerformed(evt);
            }
        });
        any2ToolPanel.add(showTightAny2_toggleButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 2, 30, 30));

        selectAny2_button.setBackground(new java.awt.Color(255, 255, 255));
        selectAny2_button.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        selectAny2_button.setText("Select Any 2 Variables");
        selectAny2_button.setAlignmentY(0.0F);
        selectAny2_button.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        selectAny2_button.setFocusable(false);
        selectAny2_button.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        selectAny2_button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        selectAny2_button.setOpaque(true);
        selectAny2_button.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        selectAny2_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAny2_buttonActionPerformed(evt);
            }
        });
        any2ToolPanel.add(selectAny2_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 2, 150, 30));

        any2LayeredPane.add(any2ToolPanel);
        any2ToolPanel.setBounds(0, 604, 790, 36);

        graphPanels_TabbedPane.addTab("Any 2", any2LayeredPane);

        normedProbabilityLayeredPane.setBackground(new java.awt.Color(241, 230, 255));
        normedProbabilityLayeredPane.setName("Probability"); // NOI18N
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

        ageR207_206r_radioB.setBackground(new java.awt.Color(241, 230, 255));
        probabilityDateButtonGroup.add(ageR207_206r_radioB);
        ageR207_206r_radioB.setFont(new java.awt.Font("Arial", 1, 10)); // NOI18N
        ageR207_206r_radioB.setText("207/206");
        ageR207_206r_radioB.setName("age207_206r"); // NOI18N
        probabilityToolPanel.add(ageR207_206r_radioB, new org.netbeans.lib.awtextra.AbsoluteConstraints(175, 20, -1, -1));

        ageR206_238r_radioB.setBackground(new java.awt.Color(241, 230, 255));
        probabilityDateButtonGroup.add(ageR206_238r_radioB);
        ageR206_238r_radioB.setFont(new java.awt.Font("Arial", 1, 10)); // NOI18N
        ageR206_238r_radioB.setText("206/238");
        ageR206_238r_radioB.setName("age206_238r"); // NOI18N
        probabilityToolPanel.add(ageR206_238r_radioB, new org.netbeans.lib.awtextra.AbsoluteConstraints(175, 0, -1, -1));

        positivePctDiscordance_slider.setBackground(new java.awt.Color(241, 230, 255));
        positivePctDiscordance_slider.setFont(new java.awt.Font("Arial", 1, 10)); // NOI18N
        positivePctDiscordance_slider.setMajorTickSpacing(10);
        positivePctDiscordance_slider.setMinorTickSpacing(1);
        positivePctDiscordance_slider.setPaintLabels(true);
        positivePctDiscordance_slider.setPaintTicks(true);
        positivePctDiscordance_slider.setSnapToTicks(true);
        positivePctDiscordance_slider.setAutoscrolls(true);
        positivePctDiscordance_slider.setName("positivePerCentDiscordanceSliderValue"); // NOI18N
        probabilityToolPanel.add(positivePctDiscordance_slider, new org.netbeans.lib.awtextra.AbsoluteConstraints(550, 5, 200, 38));

        percentUncertainty_slider.setBackground(new java.awt.Color(241, 230, 255));
        percentUncertainty_slider.setFont(new java.awt.Font("Arial", 1, 10)); // NOI18N
        percentUncertainty_slider.setMajorTickSpacing(10);
        percentUncertainty_slider.setMinorTickSpacing(1);
        percentUncertainty_slider.setPaintLabels(true);
        percentUncertainty_slider.setPaintTicks(true);
        percentUncertainty_slider.setSnapToTicks(true);
        percentUncertainty_slider.setAutoscrolls(true);
        percentUncertainty_slider.setName("uncertaintyPerCentSliderValue"); // NOI18N
        probabilityToolPanel.add(percentUncertainty_slider, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 5, 168, 38));

        negativePctDiscordance_slider.setBackground(new java.awt.Color(241, 230, 255));
        negativePctDiscordance_slider.setFont(new java.awt.Font("Arial", 1, 10)); // NOI18N
        negativePctDiscordance_slider.setMajorTickSpacing(10);
        negativePctDiscordance_slider.setMaximum(0);
        negativePctDiscordance_slider.setMinimum(-100);
        negativePctDiscordance_slider.setMinorTickSpacing(1);
        negativePctDiscordance_slider.setPaintLabels(true);
        negativePctDiscordance_slider.setPaintTicks(true);
        negativePctDiscordance_slider.setSnapToTicks(true);
        negativePctDiscordance_slider.setValue(-50);
        negativePctDiscordance_slider.setName("negativePerCentDiscordanceSliderValue"); // NOI18N
        probabilityToolPanel.add(negativePctDiscordance_slider, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 5, 200, 38));

        positivePctDiscordance_text.setBackground(new java.awt.Color(241, 230, 255));
        positivePctDiscordance_text.setFont(new java.awt.Font("Arial", 1, 10)); // NOI18N
        positivePctDiscordance_text.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        positivePctDiscordance_text.setText("Positive % discordance");
        positivePctDiscordance_text.setAlignmentX(0.0F);
        positivePctDiscordance_text.setAlignmentY(0.0F);
        positivePctDiscordance_text.setBorder(null);
        probabilityToolPanel.add(positivePctDiscordance_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(586, 45, 150, 15));

        negativePctDiscordance_text.setBackground(new java.awt.Color(241, 230, 255));
        negativePctDiscordance_text.setFont(new java.awt.Font("Arial", 1, 10)); // NOI18N
        negativePctDiscordance_text.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        negativePctDiscordance_text.setText("Negative % discordance");
        negativePctDiscordance_text.setAlignmentX(0.0F);
        negativePctDiscordance_text.setAlignmentY(0.0F);
        negativePctDiscordance_text.setBorder(null);
        probabilityToolPanel.add(negativePctDiscordance_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(345, 45, 150, 15));

        pctUncertainty_text.setBackground(new java.awt.Color(241, 230, 255));
        pctUncertainty_text.setFont(new java.awt.Font("Arial", 1, 10)); // NOI18N
        pctUncertainty_text.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        pctUncertainty_text.setText("% uncertainty");
        pctUncertainty_text.setAlignmentX(0.0F);
        pctUncertainty_text.setAlignmentY(0.0F);
        pctUncertainty_text.setBorder(null);
        probabilityToolPanel.add(pctUncertainty_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(767, 45, 140, 15));

        ageBest_radio.setBackground(new java.awt.Color(241, 230, 255));
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
        probabilityToolPanel.add(linkedUnlinkedDiscordance, new org.netbeans.lib.awtextra.AbsoluteConstraints(530, 5, 20, 20));

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

        commonLeadCorrectionSelectorPDF_checkbox.setBackground(new java.awt.Color(241, 230, 255));
        commonLeadCorrectionSelectorPDF_checkbox.setFont(new java.awt.Font("SansSerif", 1, 9)); // NOI18N
        commonLeadCorrectionSelectorPDF_checkbox.setText("PbcCorr");
        commonLeadCorrectionSelectorPDF_checkbox.setToolTipText("Correct for Common Lead");
        commonLeadCorrectionSelectorPDF_checkbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                commonLeadCorrectionSelectorPDF_checkboxActionPerformed(evt);
            }
        });
        probabilityToolPanel.add(commonLeadCorrectionSelectorPDF_checkbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 2, -1, 25));

        DatePbCorrSchemeA_radio.setBackground(new java.awt.Color(241, 230, 255));
        probabilityDateButtonGroup.add(DatePbCorrSchemeA_radio);
        DatePbCorrSchemeA_radio.setFont(new java.awt.Font("Arial", 1, 10)); // NOI18N
        DatePbCorrSchemeA_radio.setText("<html>UPb Date PbcCorr</html>");
        DatePbCorrSchemeA_radio.setActionCommand("bestAge");
        DatePbCorrSchemeA_radio.setName("PbcCorr_UPb_Date"); // NOI18N
        probabilityToolPanel.add(DatePbCorrSchemeA_radio, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 33, 80, 30));

        clearFilters_button.setBackground(new java.awt.Color(255, 255, 255));
        clearFilters_button.setFont(new java.awt.Font("Helvetica", 1, 10)); // NOI18N
        clearFilters_button.setText("Clear");
        clearFilters_button.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        clearFilters_button.setContentAreaFilled(false);
        clearFilters_button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        clearFilters_button.setOpaque(true);
        clearFilters_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearFilters_buttonActionPerformed(evt);
            }
        });
        probabilityToolPanel.add(clearFilters_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 50, 40, 18));

        defaultFilters_button.setBackground(new java.awt.Color(255, 255, 255));
        defaultFilters_button.setFont(new java.awt.Font("Helvetica", 1, 10)); // NOI18N
        defaultFilters_button.setText("Default");
        defaultFilters_button.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        defaultFilters_button.setContentAreaFilled(false);
        defaultFilters_button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        defaultFilters_button.setOpaque(true);
        defaultFilters_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                defaultFilters_buttonActionPerformed(evt);
            }
        });
        probabilityToolPanel.add(defaultFilters_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(500, 50, 40, 18));

        normedProbabilityLayeredPane.add(probabilityToolPanel);
        probabilityToolPanel.setBounds(0, 574, 920, 68);

        graphPanels_TabbedPane.addTab("Probability", normedProbabilityLayeredPane);

        interpretations_SplitPane.setRightComponent(graphPanels_TabbedPane);

        jPanel1.setBackground(new java.awt.Color(249, 237, 189));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(null);

        close_button.setForeground(new java.awt.Color(255, 51, 0));
        close_button.setText("Close");
        close_button.setAlignmentY(0.0F);
        close_button.setPreferredSize(new java.awt.Dimension(140, 23));
        close_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                close_buttonActionPerformed(evt);
            }
        });
        jPanel1.add(close_button);
        close_button.setBounds(2, 2, 176, 25);

        writeVisiblePlotSvgPdf_button.setForeground(new java.awt.Color(255, 51, 0));
        writeVisiblePlotSvgPdf_button.setText("Visible Plot - write SVG and PDF files");
        writeVisiblePlotSvgPdf_button.setAlignmentY(0.0F);
        writeVisiblePlotSvgPdf_button.setPreferredSize(new java.awt.Dimension(140, 23));
        writeVisiblePlotSvgPdf_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                writeVisiblePlotSvgPdf_buttonActionPerformed(evt);
            }
        });
        jPanel1.add(writeVisiblePlotSvgPdf_button);
        writeVisiblePlotSvgPdf_button.setBounds(500, 2, 460, 25);

        displayOptions_menu.setText("Display Options");
        displayOptions_menu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                displayOptions_menuMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                displayOptions_menuMouseEntered(evt);
            }
        });

        sampleConcordiaOptions_menuItem.setText("Sample Display Options");
        sampleConcordiaOptions_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sampleConcordiaOptions_menuItemActionPerformed(evt);
            }
        });
        displayOptions_menu.add(sampleConcordiaOptions_menuItem);

        aliquotsChooser_menuItem.setText("Aliquots Chooser");
        aliquotsChooser_menuItem.setEnabled(false);
        displayOptions_menu.add(aliquotsChooser_menuItem);

        aliquotSpecificOptions_menu.setText("Aliquots Display Options");
        displayOptions_menu.add(aliquotSpecificOptions_menu);

        sampleAgeGUIMenuBar.add(displayOptions_menu);

        weightedMeansPlotOptions_menu.setText("Weighted Means Plot");

        weightedMeansChooser_menuItem.setText("Weighted Means Chooser");
        weightedMeansChooser_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                weightedMeansChooser_menuItemActionPerformed(evt);
            }
        });
        weightedMeansPlotOptions_menu.add(weightedMeansChooser_menuItem);

        sortFractionsDateAsc_menuItemCheckBox.setText("Sort Fractions by Date Ascending in Weighted Means Chooser");
        sortFractionsDateAsc_menuItemCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sortFractionsDateAsc_menuItemCheckBoxActionPerformed(evt);
            }
        });
        weightedMeansPlotOptions_menu.add(sortFractionsDateAsc_menuItemCheckBox);

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
                .add(interpretations_SplitPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 685, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 31, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void resetGraphDisplay_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetGraphDisplay_buttonActionPerformed
        ((PlottingDetailsDisplayInterface) concordiaGraphPanel).setShowTightToEdges(false);
        ((PlottingDetailsDisplayInterface) concordiaGraphPanel).resetPanel(true, false);
}//GEN-LAST:event_resetGraphDisplay_buttonActionPerformed
//todo clean up this code with a change listener
private void ellipseCenters_checkboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ellipseCenters_checkboxActionPerformed
    boolean state = ((ConcordiaGraphPanel) concordiaGraphPanel).isShowEllipseCenters();
    ((ConcordiaGraphPanel) concordiaGraphPanel).setShowEllipseCenters(!state);
    ellipseCenters_checkbox.setSelected(!state);

    ((ConcordiaPlotDisplayInterface) concordiaGraphPanel).getConcordiaOptions()//
            .put("showEllipseCenters", Boolean.toString(!state));

    concordiaGraphPanel.repaint();
}//GEN-LAST:event_ellipseCenters_checkboxActionPerformed

private void ellipseLabels_checkboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ellipseLabels_checkboxActionPerformed
    ((ConcordiaGraphPanel) concordiaGraphPanel).setShowEllipseLabels(!((ConcordiaGraphPanel) concordiaGraphPanel).isShowEllipseLabels());
    ellipseLabels_checkbox.setSelected(((ConcordiaGraphPanel) concordiaGraphPanel).isShowEllipseLabels());

    ((ConcordiaPlotDisplayInterface) concordiaGraphPanel).getConcordiaOptions()//
            .put("showEllipseLabels", Boolean.toString(((ConcordiaGraphPanel) concordiaGraphPanel).isShowEllipseLabels()));

    concordiaGraphPanel.repaint();
}//GEN-LAST:event_ellipseLabels_checkboxActionPerformed

private void concordiaErrors_checkboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_concordiaErrors_checkboxActionPerformed
    ((ConcordiaGraphPanel) concordiaGraphPanel).setShowConcordiaErrorBars(!((ConcordiaGraphPanel) concordiaGraphPanel).isShowConcordiaErrorBars());
    concordiaErrors_checkbox.setSelected(((ConcordiaGraphPanel) concordiaGraphPanel).isShowConcordiaErrorBars());

    ((ConcordiaPlotDisplayInterface) concordiaGraphPanel).getConcordiaOptions()//
            .put("showConcordiaErrors", Boolean.toString(((ConcordiaGraphPanel) concordiaGraphPanel).isShowConcordiaErrorBars()));

    concordiaGraphPanel.repaint();
}//GEN-LAST:event_concordiaErrors_checkboxActionPerformed
private void close_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_close_buttonActionPerformed
    close();
}//GEN-LAST:event_close_buttonActionPerformed

private void sampleConcordiaOptions_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sampleConcordiaOptions_menuItemActionPerformed

    // if (graphPanels_TabbedPane.getSelectedIndex() == graphPanels_TabbedPane.indexOfTab("Concordia")) {
    ((ConcordiaPlotDisplayInterface) concordiaGraphPanel).showConcordiaDisplayOptionsDialog();
    // } else if (graphPanels_TabbedPane.getSelectedIndex() == graphPanels_TabbedPane.indexOfTab("Any 2")) {
    //     ((ConcordiaPlotDisplayInterface) plotAny2Panel).showConcordiaDisplayOptionsDialog();
    // }


}//GEN-LAST:event_sampleConcordiaOptions_menuItemActionPerformed
private void displayOptions_menuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_displayOptions_menuMouseClicked
    buildAliquotOptionsMenu();
}//GEN-LAST:event_displayOptions_menuMouseClicked
private void displayOptions_menuMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_displayOptions_menuMouseEntered
    buildAliquotOptionsMenu();
}//GEN-LAST:event_displayOptions_menuMouseEntered
private void pan_toggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pan_toggleButtonActionPerformed
    ((ConcordiaGraphPanel) concordiaGraphPanel).setImageMode(pan_toggleButton.getName());
}//GEN-LAST:event_pan_toggleButtonActionPerformed
private void zoomBox_toggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomBox_toggleButtonActionPerformed
    ((ConcordiaGraphPanel) concordiaGraphPanel).setImageMode(zoomBox_toggleButton.getName());
}//GEN-LAST:event_zoomBox_toggleButtonActionPerformed

private void restoreGraphDisplay_WeightedMean_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_restoreGraphDisplay_WeightedMean_buttonActionPerformed
    ((PlottingDetailsDisplayInterface) weightedMeanGraphPanel).refreshPanel(true, false);
}//GEN-LAST:event_restoreGraphDisplay_WeightedMean_buttonActionPerformed

private void fractionOrderByDate_radioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fractionOrderByDate_radioButtonActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_fractionOrderByDate_radioButtonActionPerformed

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

    int ww = graphPanels_TabbedPane.getWidth();
    int hh = graphPanels_TabbedPane.getHeight();

    int adjustedWidth = ww - 30;
    int adjustedHeight = hh - 75;

    ((ConcordiaGraphPanel) concordiaGraphPanel).setGraphWidth(adjustedWidth);
    ((ConcordiaGraphPanel) concordiaGraphPanel).setGraphHeight(adjustedHeight);

    int widthCP = (int) ((ConcordiaGraphPanel) concordiaGraphPanel).getGraphWidth();
    int heightCP = (int) ((ConcordiaGraphPanel) concordiaGraphPanel).getGraphHeight();
    int leftMarginCP = ((ConcordiaGraphPanel) concordiaGraphPanel).getLeftMargin();

    concordiaGraphPanel.setBounds(
            1, 1, widthCP + leftMarginCP, heightCP + 16);

    concordiaToolPanel.setBounds(
            1, heightCP + 16, widthCP + leftMarginCP, 35);

    concordiaGraphPanel.repaint();

    ((AbstractPlot) plotAny2Panel).setGraphWidth(widthCP);
    ((AbstractPlot) plotAny2Panel).setGraphHeight(heightCP);

    plotAny2Panel.setBounds(
            1, 1, widthCP + leftMarginCP, heightCP + 16);

    any2ToolPanel.setBounds(
            1, heightCP + 16, widthCP + leftMarginCP, 35);

    ((PlottingDetailsDisplayInterface) plotAny2Panel).refreshPanel(true, false);

    try {
        ((AbstractPlot) useriesIsochronPanel).setGraphWidth(widthCP);
        ((AbstractPlot) useriesIsochronPanel).setGraphHeight(heightCP);

        useriesIsochronPanel.setBounds(
                1, 1, widthCP + leftMarginCP, heightCP + 16);

        uSeriesIsochronToolPanel.setBounds(
                1, heightCP + 16, widthCP + leftMarginCP, 35);

        ((PlottingDetailsDisplayInterface) useriesIsochronPanel).refreshPanel(true, false);
    } catch (Exception e) {
    }

    //  evolutionPlotPanel.setBounds(0,0,evolutionLayeredPane.getWidth(), evolutionLayeredPane.getHeight() - 400);
    //evolutionPlotPanel.setSize(evolutionLayeredPane.getSize());
    ((org.earthtime.plots.AbstractDataView) evolutionPlotPanel).refreshPanel(true);

    ((WeightedMeanGraphPanel) weightedMeanGraphPanel).setGraphWidth(adjustedWidth);

    ((WeightedMeanGraphPanel) weightedMeanGraphPanel).setGraphHeight(adjustedHeight);

    weightedMeanGraphPanel.setBounds(
            1, 1, widthCP + leftMarginCP, heightCP + 16);

    weightedMeanToolPanel.setBounds(
            1, heightCP + 16, widthCP + leftMarginCP, 35);

    ((PlottingDetailsDisplayInterface) weightedMeanGraphPanel).refreshPanel(true, false);

    // june 2010 expansion to include additional panels
    ((DateProbabilityDensityPanel) probabilityPanel).setGraphWidth(adjustedWidth);

    ((DateProbabilityDensityPanel) probabilityPanel).setGraphHeight(adjustedHeight - 35);
    probabilityPanel.setBounds(
            1, 1, widthCP + leftMarginCP, heightCP - 17);
    probabilityToolPanel.setBounds(
            1, heightCP - 15, widthCP + leftMarginCP, 70);

    probabilityPanel.repaint();


}//GEN-LAST:event_graphPanelsTabbedPaneResized

private void concordiaFlavor_radioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_concordiaFlavor_radioButtonActionPerformed
    ((ConcordiaPlotDisplayInterface) concordiaGraphPanel).getConcordiaOptions().//
            put("concordiaFlavor", "C");

    ((ConcordiaGraphPanel) concordiaGraphPanel).setConcordiaFlavor("C");

    ((PlottingDetailsDisplayInterface) concordiaGraphPanel).refreshPanel(true, false);
}//GEN-LAST:event_concordiaFlavor_radioButtonActionPerformed
private void terraWasserburgFlavor_radioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_terraWasserburgFlavor_radioButtonActionPerformed
    ((ConcordiaPlotDisplayInterface) concordiaGraphPanel).getConcordiaOptions().//
            put("concordiaFlavor", "T-W");

    ((ConcordiaGraphPanel) concordiaGraphPanel).setConcordiaFlavor("T-W");

    ((PlottingDetailsDisplayInterface) concordiaGraphPanel).refreshPanel(true, false);

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

    plotAny2Panel.repaint();

    try {
        useriesIsochronPanel.repaint();
    } catch (Exception e) {
    }

    evolutionPlotPanel.repaint();

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

    ((PlottingDetailsDisplayInterface) weightedMeanGraphPanel).preparePanel(true, false);
}//GEN-LAST:event_weightedMeansChooser_menuItemActionPerformed
private void zoomInAny2X2_buttonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomInAny2X2_buttonActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_zoomInAny2X2_buttonActionPerformed

private void resetGraphAny2Display_buttonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetGraphAny2Display_buttonActionPerformed
    ((PlottingDetailsDisplayInterface) plotAny2Panel).setShowTightToEdges(false);
    ((PlottingDetailsDisplayInterface) plotAny2Panel).resetPanel(true, false);
}//GEN-LAST:event_resetGraphAny2Display_buttonActionPerformed

private void ellipseCentersAny2OnToggle_checkboxActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ellipseCentersAny2OnToggle_checkboxActionPerformed
    boolean state = ((AbstractPlot) plotAny2Panel).isShowEllipseCenters();
    ((AbstractPlot) plotAny2Panel).setShowEllipseCenters(!state);
    ellipseCentersAny2OnToggle_checkbox.setSelected(!state);

    ((ConcordiaPlotDisplayInterface) plotAny2Panel).getConcordiaOptions()//
            .put("showEllipseCenters", Boolean.toString(!state));

    plotAny2Panel.repaint();
}//GEN-LAST:event_ellipseCentersAny2OnToggle_checkboxActionPerformed

private void ellipseLabelsAny2OnToggle_checkboxActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ellipseLabelsAny2OnToggle_checkboxActionPerformed
    ((AbstractPlot) plotAny2Panel).setShowEllipseLabels(!((AbstractPlot) plotAny2Panel).isShowEllipseLabels());
    ellipseLabelsAny2OnToggle_checkbox.setSelected(((AbstractPlot) plotAny2Panel).isShowEllipseLabels());

    ((ConcordiaPlotDisplayInterface) plotAny2Panel).getConcordiaOptions()//
            .put("showEllipseLabels", Boolean.toString(((AbstractPlot) plotAny2Panel).isShowEllipseLabels()));

    plotAny2Panel.repaint();
}//GEN-LAST:event_ellipseLabelsAny2OnToggle_checkboxActionPerformed

private void panAny2_toggleButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_panAny2_toggleButtonActionPerformed
    ((AbstractPlot) plotAny2Panel).setImageMode(panAny2_toggleButton.getName());
}//GEN-LAST:event_panAny2_toggleButtonActionPerformed

private void zoomBoxAny2_toggleButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomBoxAny2_toggleButtonActionPerformed
    ((AbstractPlot) plotAny2Panel).setImageMode(zoomBoxAny2_toggleButton.getName());
}//GEN-LAST:event_zoomBoxAny2_toggleButtonActionPerformed

private void graphViewTabChanged (java.awt.event.MouseEvent evt) {//GEN-FIRST:event_graphViewTabChanged
    if (graphPanels_TabbedPane.getSelectedIndex() == graphPanels_TabbedPane.indexOfTab("Probability")) {
        if (((DateProbabilityDensityPanel) probabilityPanel).getSelectedFractions().isEmpty()) {
            ((DateProbabilityDensityPanel) probabilityPanel).//
                    setSelectedFractions(filterActiveUPbFractions(sample.getUpbFractionsUnknown()));
            ((DateProbabilityDensityPanel) probabilityPanel).//
                    getDeSelectedFractions().clear();
            ((PlottingDetailsDisplayInterface) probabilityPanel).//
                    refreshPanel(true, false);
        } else {
            probabilityPanel.repaint();
        }
    }
}//GEN-LAST:event_graphViewTabChanged

private void zoomInProbability_buttonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomInProbability_buttonActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_zoomInProbability_buttonActionPerformed

private void resetGraphProbability_buttonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetGraphProbability_buttonActionPerformed

// oct 2016 removed this behavior and moved it to new button "clear filters"
//    positivePctDiscordance_slider.setValue(100);
//    negativePctDiscordance_slider.setValue(-100);
//    percentUncertainty_slider.setValue(100);
//
//    ((DateProbabilityDensityPanel) probabilityPanel).//
//            setSelectedFractions(filterActiveUPbFractions(sample.getUpbFractionsUnknown()));
    ((PlottingDetailsDisplayInterface) probabilityPanel).refreshPanel(true, false);
}//GEN-LAST:event_resetGraphProbability_buttonActionPerformed

private void thoriumCorrectionSelector_checkboxActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_thoriumCorrectionSelector_checkboxActionPerformed
    ((ConcordiaGraphPanel) concordiaGraphPanel).toggleDisplay_r206_238r_Th();
    thoriumCorrectionSelector_checkbox.setSelected(((ConcordiaGraphPanel) concordiaGraphPanel).isDisplay_r206_238r_Th());

    concordiaGraphPanel.repaint();
}//GEN-LAST:event_thoriumCorrectionSelector_checkboxActionPerformed

private void protactiniumCorrectionSelector_checkboxActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_protactiniumCorrectionSelector_checkboxActionPerformed
    ((ConcordiaGraphPanel) concordiaGraphPanel).toggleDisplay_r206_238r_Pa();
    protactiniumCorrectionSelector_checkbox.setSelected(((ConcordiaGraphPanel) concordiaGraphPanel).isDisplay_r206_238r_Pa());

    concordiaGraphPanel.repaint();
}//GEN-LAST:event_protactiniumCorrectionSelector_checkboxActionPerformed

private void linkedUnlinkedDiscordanceActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_linkedUnlinkedDiscordanceActionPerformed
    toggleLinkLockDiscordanceSliders();
    // test for linkage
    updateSlidersStatus(positivePctDiscordance_slider);
    performFilteringPerSliders(false);
}//GEN-LAST:event_linkedUnlinkedDiscordanceActionPerformed

    private void toggleLinkLockDiscordanceSliders() {
        Icon oldPressed = linkedUnlinkedDiscordance.getPressedIcon();
        linkedUnlinkedDiscordance.setPressedIcon(linkedUnlinkedDiscordance.getIcon());
        linkedUnlinkedDiscordance.setIcon(oldPressed);
        doLinkDiscordances = !doLinkDiscordances;
        if (doLinkDiscordances) {
            linkedUnlinkedDiscordance.setToolTipText("Click to Unlock sliders.");
        } else {
            linkedUnlinkedDiscordance.setToolTipText("Click to Lock sliders.");
        }
    }
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


private void lockUnlockHistogramBinsActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lockUnlockHistogramBinsActionPerformed
    Icon oldPressed = lockUnlockHistogramBins.getPressedIcon();
    lockUnlockHistogramBins.setPressedIcon(lockUnlockHistogramBins.getIcon());
    lockUnlockHistogramBins.setIcon(oldPressed);
    ((DateProbabilityDensityPanel) probabilityPanel).setFreezeHistogramBinWidth(//
            !((DateProbabilityDensityPanel) probabilityPanel).isFreezeHistogramBinWidth());

    probabilityPanel.repaint();
}//GEN-LAST:event_lockUnlockHistogramBinsActionPerformed

private void lockUnlockHistogramBinsMouseEntered (java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lockUnlockHistogramBinsMouseEntered

    String currentBinWidth
            = (new DecimalFormat("###0").//
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
        ((PlottingDetailsDisplayInterface) concordiaGraphPanel).setShowTightToEdges(true);
        ((PlottingDetailsDisplayInterface) concordiaGraphPanel).resetPanel(true, false);
    }//GEN-LAST:event_showTight_toggleButtonActionPerformed

    private void choosePDFPeaks_menuMenuSelected ( javax.swing.event.MenuEvent evt ) {//GEN-FIRST:event_choosePDFPeaks_menuMenuSelected
        buildChoosePDFPeaksMenu();
    }//GEN-LAST:event_choosePDFPeaks_menuMenuSelected

    private void showExcludedFractions_checkboxActionPerformed ( java.awt.event.ActionEvent evt ) {//GEN-FIRST:event_showExcludedFractions_checkboxActionPerformed
        ((ConcordiaGraphPanel) concordiaGraphPanel).setShowExcludedEllipses(!((ConcordiaGraphPanel) concordiaGraphPanel).isShowExcludedEllipses());
        showExcludedFractions_checkbox.setSelected(((ConcordiaGraphPanel) concordiaGraphPanel).isShowExcludedEllipses());

        ((ConcordiaPlotDisplayInterface) concordiaGraphPanel).getConcordiaOptions()//
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

    private void showFilteredFractions_checkboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showFilteredFractions_checkboxActionPerformed

        boolean state = ((AliquotDetailsDisplayInterface) concordiaGraphPanel).isShowFilteredEllipses();
        ((AliquotDetailsDisplayInterface) concordiaGraphPanel).setShowFilteredEllipses(!state);
        showFilteredFractions_checkbox.setSelected(!state);

        ((ConcordiaPlotDisplayInterface) concordiaGraphPanel).getConcordiaOptions()//
                .put("showFilteredEllipses", Boolean.toString(!state));

        concordiaGraphPanel.repaint();
    }//GEN-LAST:event_showFilteredFractions_checkboxActionPerformed

    private void thoriumConcordiaFlavor_radioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_thoriumConcordiaFlavor_radioButtonActionPerformed
        ((ConcordiaPlotDisplayInterface) concordiaGraphPanel).getConcordiaOptions().//
                put("concordiaFlavor", "Th");

        ((ConcordiaGraphPanel) concordiaGraphPanel).setConcordiaFlavor("Th");

        ((PlottingDetailsDisplayInterface) concordiaGraphPanel).refreshPanel(true, false);
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

        ((PlottingDetailsDisplayInterface) concordiaGraphPanel).refreshPanel(true, false);
    }//GEN-LAST:event_commonLeadCorrectionSelector_checkboxActionPerformed

    private void sortFractionsDateAsc_menuItemCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sortFractionsDateAsc_menuItemCheckBoxActionPerformed
        dateTreeByAliquot.toggleSortByDateAsc();
        refreshSampleDateInterpretations(false, false);
    }//GEN-LAST:event_sortFractionsDateAsc_menuItemCheckBoxActionPerformed

    private void clearFilters_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearFilters_buttonActionPerformed

        if (!doLinkDiscordances) {
            toggleLinkLockDiscordanceSliders();
        }

        negativePctDiscordance_slider.setValue(-100);
        updateSlidersStatus(negativePctDiscordance_slider);

        percentUncertainty_slider.setValue(100);
        updateSlidersStatus(percentUncertainty_slider);

        performFilteringPerSliders(true);

        parentFrame.refreshReportTableData();
    }//GEN-LAST:event_clearFilters_buttonActionPerformed

    private void defaultFilters_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_defaultFilters_buttonActionPerformed

        int negPctDis = ReduxLabData.getInstance().getDefaultNegPctDiscordanceFilter();
        int posPctDis = ReduxLabData.getInstance().getDefaultPosPctDiscordanceFilter();
        boolean testDoLinkDiscordances = (Math.abs(negPctDis) == posPctDis);
        if (doLinkDiscordances != testDoLinkDiscordances) {
            toggleLinkLockDiscordanceSliders();
        }

        if (doLinkDiscordances) {
            negativePctDiscordance_slider.setValue(ReduxLabData.getInstance().getDefaultNegPctDiscordanceFilter());
            updateSlidersStatus(negativePctDiscordance_slider);

        } else {
            negativePctDiscordance_slider.setValue(ReduxLabData.getInstance().getDefaultNegPctDiscordanceFilter());
            positivePctDiscordance_slider.setValue(ReduxLabData.getInstance().getDefaultPosPctDiscordanceFilter());
            updateSlidersStatus(negativePctDiscordance_slider);
            updateSlidersStatus(positivePctDiscordance_slider);
        }

        percentUncertainty_slider.setValue(ReduxLabData.getInstance().getDefaultPctUncertaintyFilter());
        updateSlidersStatus(percentUncertainty_slider);

        performFilteringPerSliders(false);

        parentFrame.refreshReportTableData();

    }//GEN-LAST:event_defaultFilters_buttonActionPerformed

    private void showTightAny2_toggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showTightAny2_toggleButtonActionPerformed
        ((PlottingDetailsDisplayInterface) plotAny2Panel).setShowTightToEdges(true);
        ((PlottingDetailsDisplayInterface) plotAny2Panel).resetPanel(true, false);
    }//GEN-LAST:event_showTightAny2_toggleButtonActionPerformed

    private void selectAny2_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAny2_buttonActionPerformed
        try {
            selectAny2Variables();
        } catch (Exception e) {
            System.out.println("HELP");
        }
    }//GEN-LAST:event_selectAny2_buttonActionPerformed

    private void resetGraphDisplayIsochron_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetGraphDisplayIsochron_buttonActionPerformed
        ((PlottingDetailsDisplayInterface) useriesIsochronPanel).setShowTightToEdges(false);
        ((PlottingDetailsDisplayInterface) useriesIsochronPanel).resetPanel(true, false);
    }//GEN-LAST:event_resetGraphDisplayIsochron_buttonActionPerformed

    private void ellipseCentersIsochron_CheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ellipseCentersIsochron_CheckboxActionPerformed
        boolean state = ((AbstractPlot) useriesIsochronPanel).isShowEllipseCenters();
        ((AbstractPlot) useriesIsochronPanel).setShowEllipseCenters(!state);
        ellipseCentersIsochron_Checkbox.setSelected(!state);

        ((UseriesIsochronPlotDisplayInterface) useriesIsochronPanel).getUSeriesIsochronOptions()//
                .put("showEllipseCenters", Boolean.toString(!state));

        useriesIsochronPanel.repaint();
    }//GEN-LAST:event_ellipseCentersIsochron_CheckboxActionPerformed

    private void ellipseLabelsIsochron_checkboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ellipseLabelsIsochron_checkboxActionPerformed
        ((AbstractPlot) useriesIsochronPanel).setShowEllipseLabels(!((AbstractPlot) useriesIsochronPanel).isShowEllipseLabels());
        ellipseLabelsAny2OnToggle_checkbox.setSelected(((AbstractPlot) useriesIsochronPanel).isShowEllipseLabels());

        ((UseriesIsochronPlotDisplayInterface) useriesIsochronPanel).getUSeriesIsochronOptions()//
                .put("showEllipseLabels", Boolean.toString(((AbstractPlot) useriesIsochronPanel).isShowEllipseLabels()));

        useriesIsochronPanel.repaint();
    }//GEN-LAST:event_ellipseLabelsIsochron_checkboxActionPerformed

    private void showEquiLineIsochron_checkboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showEquiLineIsochron_checkboxActionPerformed
        ((AbstractPlot) useriesIsochronPanel).setShowEquiline(!((AbstractPlot) useriesIsochronPanel).isShowEquiline());
        showEquiLineIsochron_checkbox.setSelected(((AbstractPlot) useriesIsochronPanel).isShowEquiline());

        ((UseriesIsochronPlotDisplayInterface) useriesIsochronPanel).getUSeriesIsochronOptions()//
                .put("showEquiline", Boolean.toString(((AbstractPlot) useriesIsochronPanel).isShowEquiline()));

        useriesIsochronPanel.repaint();
    }//GEN-LAST:event_showEquiLineIsochron_checkboxActionPerformed

    private void panIsochron_toggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_panIsochron_toggleButtonActionPerformed
        ((AbstractPlot) useriesIsochronPanel).setImageMode(panIsochron_toggleButton.getName());    }//GEN-LAST:event_panIsochron_toggleButtonActionPerformed

    private void zoomBox_toggleIsochron_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomBox_toggleIsochron_buttonActionPerformed
        ((AbstractPlot) useriesIsochronPanel).setImageMode(zoomBox_toggleIsochron_button.getName());
    }//GEN-LAST:event_zoomBox_toggleIsochron_buttonActionPerformed

    private void thoriumFlavorIsochron_radioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_thoriumFlavorIsochron_radioButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_thoriumFlavorIsochron_radioButtonActionPerformed

    private void showTightIsochron_toggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showTightIsochron_toggleButtonActionPerformed
        ((PlottingDetailsDisplayInterface) useriesIsochronPanel).setShowTightToEdges(true);
        ((PlottingDetailsDisplayInterface) useriesIsochronPanel).resetPanel(true, false);
    }//GEN-LAST:event_showTightIsochron_toggleButtonActionPerformed

    private void showExcludedFractionsIsochron_checkboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showExcludedFractionsIsochron_checkboxActionPerformed
        ((AbstractPlot) useriesIsochronPanel).setShowExcludedEllipses(!((AbstractPlot) useriesIsochronPanel).isShowExcludedEllipses());
        showExcludedFractionsIsochron_checkbox.setSelected(((AbstractPlot) useriesIsochronPanel).isShowExcludedEllipses());

        ((UseriesIsochronPlotDisplayInterface) useriesIsochronPanel).getUSeriesIsochronOptions()//
                .put("showExcludedEllipses", Boolean.toString(((AbstractPlot) useriesIsochronPanel).isShowExcludedEllipses()));

        useriesIsochronPanel.repaint();
    }//GEN-LAST:event_showExcludedFractionsIsochron_checkboxActionPerformed

    private void radiumFlavorIsochron_radioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_radiumFlavorIsochron_radioButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_radiumFlavorIsochron_radioButtonActionPerformed

    private void showRegressionLineIsochron_checkboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showRegressionLineIsochron_checkboxActionPerformed
        ((AbstractPlot) useriesIsochronPanel).setShowRegressionLine(!((AbstractPlot) useriesIsochronPanel).isShowRegressionLine());
        showRegressionLineIsochron_checkbox.setSelected(((AbstractPlot) useriesIsochronPanel).isShowRegressionLine());

        ((UseriesIsochronPlotDisplayInterface) useriesIsochronPanel).getUSeriesIsochronOptions()//
                .put("showRegressionLine", Boolean.toString(((AbstractPlot) useriesIsochronPanel).isShowRegressionLine()));

        useriesIsochronPanel.repaint();

    }//GEN-LAST:event_showRegressionLineIsochron_checkboxActionPerformed

    private void showIsochrons_checkboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showIsochrons_checkboxActionPerformed
        ((AbstractPlot) useriesIsochronPanel).setShowIsochrons(!((AbstractPlot) useriesIsochronPanel).isShowIsochrons());
        showIsochrons_checkbox.setSelected(((AbstractPlot) useriesIsochronPanel).isShowIsochrons());

        ((UseriesIsochronPlotDisplayInterface) useriesIsochronPanel).getUSeriesIsochronOptions()//
                .put("showIsochrons", Boolean.toString(((AbstractPlot) useriesIsochronPanel).isShowIsochrons()));

        useriesIsochronPanel.repaint();
    }//GEN-LAST:event_showIsochrons_checkboxActionPerformed

    private void showRegressionUnctIsochron_checkboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showRegressionUnctIsochron_checkboxActionPerformed
        ((AbstractPlot) useriesIsochronPanel).setShowRegressionLineUnct(!((AbstractPlot) useriesIsochronPanel).isShowRegressionLineUnct());
        showRegressionUnctIsochron_checkbox.setSelected(((AbstractPlot) useriesIsochronPanel).isShowRegressionLineUnct());

        ((UseriesIsochronPlotDisplayInterface) useriesIsochronPanel).getUSeriesIsochronOptions()//
                .put("showRegressionUnct", Boolean.toString(((AbstractPlot) useriesIsochronPanel).isShowRegressionLineUnct()));

        useriesIsochronPanel.repaint();

    }//GEN-LAST:event_showRegressionUnctIsochron_checkboxActionPerformed

    private void writeVisiblePlotSvgPdf_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_writeVisiblePlotSvgPdf_buttonActionPerformed
        delegatePlotFileWriting();
    }//GEN-LAST:event_writeVisiblePlotSvgPdf_buttonActionPerformed

    private void resetEvolution_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetEvolution_buttonActionPerformed
        ((EvolutionPlotPanelII) evolutionPlotPanel).showLoose();
    }//GEN-LAST:event_resetEvolution_buttonActionPerformed

    private void ellipseCentersEvolutionOnToggle_checkboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ellipseCentersEvolutionOnToggle_checkboxActionPerformed
        ((EvolutionPlotPanelII)evolutionPlotPanel)
                .setShowCenters(ellipseCentersEvolutionOnToggle_checkbox.isSelected());
        ((EvolutionPlotPanelII)evolutionPlotPanel).repaint();
    }//GEN-LAST:event_ellipseCentersEvolutionOnToggle_checkboxActionPerformed

    private void ellipseLabelsEvolutionOnToggle_checkboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ellipseLabelsEvolutionOnToggle_checkboxActionPerformed
        ((EvolutionPlotPanelII)evolutionPlotPanel)
                .setShowLabels(ellipseLabelsEvolutionOnToggle_checkbox.isSelected());
        ((EvolutionPlotPanelII)evolutionPlotPanel).repaint();
    }//GEN-LAST:event_ellipseLabelsEvolutionOnToggle_checkboxActionPerformed

    private void zoomBoxEvolution_toggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomBoxEvolution_toggleButtonActionPerformed
        ((EvolutionPlotPanelII)evolutionPlotPanel).putInImageModeZoom();
    }//GEN-LAST:event_zoomBoxEvolution_toggleButtonActionPerformed

    private void showTightEvolution_toggleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showTightEvolution_toggleButtonActionPerformed
        ((EvolutionPlotPanelII) evolutionPlotPanel).showTight();
    }//GEN-LAST:event_showTightEvolution_toggleButtonActionPerformed

    private void isochrons_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_isochrons_buttonActionPerformed
        selectEvolutionIsochrons();
    }//GEN-LAST:event_isochrons_buttonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton DatePbCorrSchemeA_radio;
    private javax.swing.JRadioButton ageBest_radio;
    private javax.swing.JRadioButton ageR206_238r_radioB;
    private javax.swing.JRadioButton ageR207_206r_radioB;
    private javax.swing.JMenu aliquotSpecificOptions_menu;
    private javax.swing.JMenuItem aliquotsChooser_menuItem;
    private javax.swing.JLayeredPane any2LayeredPane;
    private javax.swing.JPanel any2ToolPanel;
    private javax.swing.JTextField binWidth_text;
    private javax.swing.JMenu choosePDFPeaks_menu;
    private javax.swing.JButton clearFilters_button;
    private javax.swing.JButton close_button;
    private javax.swing.JCheckBox commonLeadCorrectionSelectorPDF_checkbox;
    private javax.swing.JCheckBox commonLeadCorrectionSelector_checkbox;
    private javax.swing.JCheckBox concordiaErrors_checkbox;
    private javax.swing.JRadioButton concordiaFlavor_radioButton;
    private javax.swing.JLayeredPane concordiaLayeredPane;
    private javax.swing.ButtonGroup concordiaPanZoom_buttonGroup;
    private javax.swing.ButtonGroup concordiaTeraW_buttonGroup;
    private javax.swing.JPanel concordiaToolPanel;
    private javax.swing.JScrollPane dateTreeByAliquot_ScrollPane;
    private javax.swing.JScrollPane dateTreeBySample_ScrollPane;
    private javax.swing.JTabbedPane dateTrees_tabs;
    private javax.swing.JButton defaultFilters_button;
    private javax.swing.JMenu displayOptions_menu;
    private javax.swing.JCheckBox ellipseCentersAny2OnToggle_checkbox;
    private javax.swing.JCheckBox ellipseCentersEvolutionOnToggle_checkbox;
    private javax.swing.JCheckBox ellipseCentersIsochron_Checkbox;
    private javax.swing.JCheckBox ellipseCenters_checkbox;
    private javax.swing.JCheckBox ellipseLabelsAny2OnToggle_checkbox;
    private javax.swing.JCheckBox ellipseLabelsEvolutionOnToggle_checkbox;
    private javax.swing.JCheckBox ellipseLabelsIsochron_checkbox;
    private javax.swing.JCheckBox ellipseLabels_checkbox;
    private javax.swing.JLayeredPane evolutionLayeredPane;
    private javax.swing.JPanel evolutionToolPanel;
    private javax.swing.JRadioButton fractionOrderByDate_radioButton;
    private javax.swing.JRadioButton fractionOrderByName_radioButton;
    private javax.swing.JRadioButton fractionOrderByRandom_radioButton;
    private javax.swing.JRadioButton fractionOrderByWeight_radioButton;
    private javax.swing.JTabbedPane graphPanels_TabbedPane;
    private javax.swing.JMenu heatMap_Menu;
    private javax.swing.JSpinner histogramBinCount_spinner;
    private javax.swing.JSplitPane interpretations_SplitPane;
    private javax.swing.JButton isochrons_button;
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
    private javax.swing.JToggleButton panIsochron_toggleButton;
    private javax.swing.JToggleButton pan_WeightedMean_toggleButton;
    private javax.swing.JToggleButton pan_toggleButton;
    private javax.swing.JTextField pctUncertainty_text;
    private javax.swing.JSlider percentUncertainty_slider;
    private javax.swing.JSlider positivePctDiscordance_slider;
    private javax.swing.JTextField positivePctDiscordance_text;
    private javax.swing.ButtonGroup probabilityDateButtonGroup;
    private javax.swing.JPanel probabilityToolPanel;
    private javax.swing.JCheckBox protactiniumCorrectionSelector_checkbox;
    private javax.swing.JRadioButton radiumFlavorIsochron_radioButton;
    private javax.swing.JButton resetEvolution_button;
    private javax.swing.JButton resetGraphAny2Display_button;
    private javax.swing.JButton resetGraphDisplayIsochron_button;
    private javax.swing.JButton resetGraphDisplay_button;
    private javax.swing.JButton resetGraphProbability_button;
    private javax.swing.JButton restoreGraphDisplay_WeightedMean_button;
    private javax.swing.JMenuBar sampleAgeGUIMenuBar;
    private javax.swing.JMenuItem sampleConcordiaOptions_menuItem;
    private javax.swing.JButton selectAny2_button;
    private javax.swing.JCheckBox showEquiLineIsochron_checkbox;
    private javax.swing.JCheckBox showExcludedFractionsIsochron_checkbox;
    private javax.swing.JCheckBox showExcludedFractions_checkbox;
    private javax.swing.JCheckBox showFilteredFractions_checkbox;
    private javax.swing.JButton showHistogram_button;
    private javax.swing.JCheckBox showIsochrons_checkbox;
    private javax.swing.JCheckBox showRegressionLineIsochron_checkbox;
    private javax.swing.JCheckBox showRegressionUnctIsochron_checkbox;
    private javax.swing.JToggleButton showTightAny2_toggleButton;
    private javax.swing.JToggleButton showTightEvolution_toggleButton;
    private javax.swing.JButton showTightGraphProbability_button;
    private javax.swing.JToggleButton showTightIsochron_toggleButton;
    private javax.swing.JToggleButton showTight_toggleButton;
    private javax.swing.JCheckBoxMenuItem sortFractionsDateAsc_menuItemCheckBox;
    private javax.swing.JRadioButton terraWasserburgFlavor_radioButton;
    private javax.swing.JRadioButton thoriumConcordiaFlavor_radioButton;
    private javax.swing.JCheckBox thoriumCorrectionSelector_checkbox;
    private javax.swing.JRadioButton thoriumFlavorIsochron_radioButton;
    private javax.swing.JPanel uSeriesIsochronToolPanel;
    private javax.swing.JLayeredPane useriesIsochronLayeredPane;
    private javax.swing.ButtonGroup weightedMeanFractionOrderButtonGroup;
    private javax.swing.JLayeredPane weightedMeanLayeredPane;
    private javax.swing.ButtonGroup weightedMeanPanZoom_buttonGroup;
    private javax.swing.JPanel weightedMeanToolPanel;
    private javax.swing.JMenuItem weightedMeansChooser_menuItem;
    private javax.swing.JMenuItem weightedMeansLookAndFeel_menuItem;
    private javax.swing.JMenu weightedMeansPlotOptions_menu;
    private javax.swing.JButton writeVisiblePlotSvgPdf_button;
    private javax.swing.JToggleButton zoomBoxAny2_toggleButton;
    private javax.swing.JToggleButton zoomBoxEvolution_toggleButton;
    private javax.swing.JToggleButton zoomBox_toggleButton;
    private javax.swing.JToggleButton zoomBox_toggleIsochron_button;
    private javax.swing.JButton zoomInAny2X2_button;
    private javax.swing.JButton zoomInProbability_button;
    private javax.swing.JButton zoomInX2Isochron_button;
    private javax.swing.JButton zoomInX2_WeightedMean_button;
    private javax.swing.JButton zoomInX2_button;
    private javax.swing.JButton zoomOutAny2X2_button;
    private javax.swing.JButton zoomOutProbability_button;
    private javax.swing.JButton zoomOutX2Isochron_button;
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

            if ((graphPanels_TabbedPane.getSelectedIndex() == graphPanels_TabbedPane.indexOfTab("Concordia"))
                    || (graphPanels_TabbedPane.getSelectedIndex() == graphPanels_TabbedPane.indexOfTab("Any 2"))
                    || (graphPanels_TabbedPane.getSelectedIndex() == graphPanels_TabbedPane.indexOfTab("USeries Isochrons"))) {

                ((ConcordiaGraphPanel) concordiaGraphPanel).//
                        setYorkFitLine(null);
                ((AliquotDetailsDisplayInterface) concordiaGraphPanel).//
                        setSelectedFractions(sample.getFractions());
                // zap deselected list as it is meaningless at level of aliquot or sample
                ((AliquotDetailsDisplayInterface) concordiaGraphPanel).//
                        getDeSelectedFractions().clear();
                ((ConcordiaGraphPanel) concordiaGraphPanel).//
                        setPreferredDatePanel(null);
                concordiaGraphPanel.repaint();

                // may 2014 show best date line
                ((ConcordiaGraphPanel) concordiaGraphPanel).setShowingSingleAliquot(false);

                // dec 2016 plot any 2 experiment
                ((AliquotDetailsDisplayInterface) plotAny2Panel).
                        setSelectedFractions(sample.getFractions());
                // zap deselected list as it is meaningless at level of aliquot or sample
                ((AliquotDetailsDisplayInterface) plotAny2Panel).//
                        getDeSelectedFractions().clear();
                plotAny2Panel.repaint();

                ((AbstractPlot) useriesIsochronPanel).//
                        setMcLeanRegressionLine(null);
                ((AliquotDetailsDisplayInterface) useriesIsochronPanel).//
                        setSelectedFractions(sample.getFractions());
                // zap deselected list as it is meaningless at level of aliquot or sample
                ((AliquotDetailsDisplayInterface) useriesIsochronPanel).//
                        getDeSelectedFractions().clear();
                ((IsochronsPanel) useriesIsochronPanel).clearSetSampleDateModel();
                useriesIsochronPanel.repaint();

                ((AliquotDetailsDisplayInterface) evolutionPlotPanel).//
                        setSelectedFractions(sample.getFractions());
                try {
                    ((AbstractDataView) evolutionPlotPanel).refreshPanel(false);
                } catch (Exception e) {
                }
            } else {

                ((DateProbabilityDensityPanel) probabilityPanel).//
                        setSelectedFractions(filterActiveUPbFractions(sample.getUpbFractionsUnknown()));
                ((DateProbabilityDensityPanel) probabilityPanel).//
                        getDeSelectedFractions().clear();
                ((DateProbabilityDensityPanel) probabilityPanel).//
                        setSelectedAliquot(0);
                ((PlottingDetailsDisplayInterface) probabilityPanel).//
                        refreshPanel(true, false);
            }

        } else if (nodeInfo instanceof AliquotInterface) {

            // oct 2011 removed conditionals here to force all tabs to update
            // if ( graphPanels_TabbedPane.getSelectedIndex() == graphPanels_TabbedPane.indexOfTab( "Concordia" ) ) {
            ((ConcordiaGraphPanel) concordiaGraphPanel).//
                    setYorkFitLine(null);
            ((AliquotDetailsDisplayInterface) concordiaGraphPanel).//
                    setSelectedFractions(((ReduxAliquotInterface) nodeInfo).getAliquotFractions());

            // zap deselected list as it is meaningless at level of aliquot or sample
            ((AliquotDetailsDisplayInterface) concordiaGraphPanel).//
                    getDeSelectedFractions().clear();
            ((ConcordiaGraphPanel) concordiaGraphPanel).setPreferredDatePanel(null);

            // may 2014 show best date line
            ((ConcordiaGraphPanel) concordiaGraphPanel).setShowingSingleAliquot(true);
            ((PlotInterface) concordiaGraphPanel).determineCurrentAliquot();

            concordiaGraphPanel.repaint();

            // dec 2016 plot any 2 experiment
            ((AliquotDetailsDisplayInterface) plotAny2Panel).//
                    setSelectedFractions(((ReduxAliquotInterface) nodeInfo).getAliquotFractions());
            // zap deselected list as it is meaningless at level of aliquot or sample
            ((AliquotDetailsDisplayInterface) plotAny2Panel).//
                    getDeSelectedFractions().clear();

            plotAny2Panel.repaint();

            ((AbstractPlot) useriesIsochronPanel).//
                    setMcLeanRegressionLine(null);
            ((AliquotDetailsDisplayInterface) useriesIsochronPanel).//
                    setSelectedFractions(((ReduxAliquotInterface) nodeInfo).getAliquotFractions());
            // zap deselected list as it is meaningless at level of aliquot or sample
            ((AliquotDetailsDisplayInterface) useriesIsochronPanel).//
                    getDeSelectedFractions().clear();

            useriesIsochronPanel.repaint();

            ((AliquotDetailsDisplayInterface) evolutionPlotPanel).//
                    setSelectedFractions(sample.getFractions());
            ((AbstractDataView) evolutionPlotPanel).refreshPanel(true);

            // update weighted means in case of delete or (oct 2010) add
            weightedMeanOptions = sample.getSampleDateInterpretationGUISettings().getWeightedMeanOptions();
            ((WeightedMeanGraphPanel) weightedMeanGraphPanel).setWeightedMeanOptions(weightedMeanOptions);

            setupWeightedMeansPanelForAliquots();
            ((PlottingDetailsDisplayInterface) weightedMeanGraphPanel).refreshPanel(true, false);

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
                    = ((DefaultMutableTreeNode) ((TreeNode) node).getParent()).getUserObject();

            if (graphPanels_TabbedPane.getSelectedIndex() == graphPanels_TabbedPane.indexOfTab("Concordia")) {

                // in case user skipped over choosing aliquot
                ((PlotInterface) concordiaGraphPanel).determineCurrentAliquot();

                // check for special case interpretations: lower and upper intercepts
                ((ConcordiaGraphPanel) concordiaGraphPanel).//
                        setYorkFitLine(((SampleDateModel) nodeInfo).getYorkLineFit());
                ((AliquotDetailsDisplayInterface) concordiaGraphPanel).//
                        setSelectedFractions(((ReduxAliquotInterface) aliquotNodeInfo).//
                                getAliquotSampleDateModelSelectedFractions(((SampleDateModel) nodeInfo).//
                                        getIncludedFractionIDsVector()));
                ((AliquotDetailsDisplayInterface) concordiaGraphPanel).//
                        setDeSelectedFractions(((ReduxAliquotInterface) aliquotNodeInfo).//
                                getAliquotSampleDateModelDeSelectedFractions(((SampleDateModel) nodeInfo).//
                                        getIncludedFractionIDsVector()));

                // for sample date interpretation, display date title box
                DateInterpretationBoxPanel dateInterpretationBoxPanel
                        = new DateInterpretationBoxPanel(((ValueModel) nodeInfo));

                ((ConcordiaGraphPanel) concordiaGraphPanel).//
                        setPreferredDatePanel(dateInterpretationBoxPanel);

                concordiaGraphPanel.repaint();
            } else if (graphPanels_TabbedPane.getSelectedIndex() == graphPanels_TabbedPane.indexOfTab("Any 2")) {
                // dec 2016
                // in case user skipped over choosing aliquot
                ((PlotInterface) plotAny2Panel).determineCurrentAliquot();

                ((AliquotDetailsDisplayInterface) plotAny2Panel).//
                        setSelectedFractions(((ReduxAliquotInterface) aliquotNodeInfo).//
                                getAliquotSampleDateModelSelectedFractions(((SampleDateModel) nodeInfo).
                                        getIncludedFractionIDsVector()));
                ((AliquotDetailsDisplayInterface) plotAny2Panel).//
                        setDeSelectedFractions(((ReduxAliquotInterface) aliquotNodeInfo).
                                getAliquotSampleDateModelDeSelectedFractions(((SampleDateModel) nodeInfo).
                                        getIncludedFractionIDsVector()));

                plotAny2Panel.repaint();

            } else if (graphPanels_TabbedPane.getSelectedIndex() == graphPanels_TabbedPane.indexOfTab("USeries Isochrons")) {
                // dec 2016
                // in case user skipped over choosing aliquot
                ((PlotInterface) useriesIsochronPanel).determineCurrentAliquot();

                ((AbstractPlot) useriesIsochronPanel).//
                        setMcLeanRegressionLine(((SampleDateModel) nodeInfo).getMcLeanRegressionLine());

                ((AliquotDetailsDisplayInterface) useriesIsochronPanel).
                        setSelectedFractions(((ReduxAliquotInterface) aliquotNodeInfo).
                                getAliquotSampleDateModelSelectedFractions(((SampleDateModel) nodeInfo).
                                        getIncludedFractionIDsVector()));
                ((AliquotDetailsDisplayInterface) useriesIsochronPanel).//
                        setDeSelectedFractions(((ReduxAliquotInterface) aliquotNodeInfo).//
                                getAliquotSampleDateModelDeSelectedFractions(((SampleDateModel) nodeInfo).//
                                        getIncludedFractionIDsVector()));

                ((IsochronsPanel) useriesIsochronPanel).setSampleDateModel((ValueModel) nodeInfo);
                useriesIsochronPanel.repaint();

            } else if (graphPanels_TabbedPane.getSelectedIndex() == graphPanels_TabbedPane.indexOfTab("Evolution")) {
                // dec 2016
                // in case user skipped over choosing aliquot
                //((PlotInterface) useriesIsochronPanel).determineCurrentAliquot();             

                ((AliquotDetailsDisplayInterface) evolutionPlotPanel).
                        setSelectedFractions(((ReduxAliquotInterface) aliquotNodeInfo).
                                getAliquotSampleDateModelSelectedFractions(((SampleDateModel) nodeInfo).
                                        getIncludedFractionIDsVector()));
                evolutionPlotPanel.repaint();
//                ((AliquotDetailsDisplayInterface) useriesIsochronPanel).//
//                        setDeSelectedFractions(((ReduxAliquotInterface) aliquotNodeInfo).//
//                                getAliquotSampleDateModelDeSelectedFractions(((SampleDateModel) nodeInfo).//
//                                        getIncludedFractionIDsVector()));
//
//                ((IsochronsPanel) useriesIsochronPanel).setSampleDateModel((ValueModel) nodeInfo);
//                useriesIsochronPanel.repaint();

            } else if (graphPanels_TabbedPane.getSelectedIndex() == graphPanels_TabbedPane.indexOfTab("Weighted Mean")) {
                weightedMeanGraphPanel.repaint();

            } else {

                // probability tab
                ((DateProbabilityDensityPanel) probabilityPanel).//
                        setSelectedAliquot(((ReduxAliquotInterface) aliquotNodeInfo).getAliquotNumber());
                // july 2010 refine this so that included fractions are filtered on the fly
                // TODO: implement detrital sample date models that will actually have these removed from model based on filter
                Vector<ETFractionInterface> tempDeselected
                        = ((ReduxAliquotInterface) aliquotNodeInfo).//
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
                        = ((DefaultMutableTreeNode) //
                                ((TreeNode) node).getParent().getParent()).getUserObject();

                Object sampleDateNodeInfo
                        = ((DefaultMutableTreeNode) //
                                ((TreeNode) node).getParent()).getUserObject();

                if (graphPanels_TabbedPane.getSelectedIndex() == graphPanels_TabbedPane.indexOfTab("Concordia")) {

                    // check for special case interpretations: lower and upper intercepts
                    ((ConcordiaGraphPanel) concordiaGraphPanel).//
                            setYorkFitLine(((SampleDateModel) sampleDateNodeInfo).getYorkLineFit());

                    ((AliquotDetailsDisplayInterface) concordiaGraphPanel).//
                            setSelectedFractions(((ReduxAliquotInterface) aliquotNodeInfo).//
                                    getAliquotSampleDateModelSelectedFractions(((SampleDateModel) sampleDateNodeInfo).//
                                            getIncludedFractionIDsVector()));

                    ((AliquotDetailsDisplayInterface) concordiaGraphPanel).//
                            setDeSelectedFractions(((ReduxAliquotInterface) aliquotNodeInfo).//
                                    getAliquotSampleDateModelDeSelectedFractions(((SampleDateModel) sampleDateNodeInfo).//
                                            getIncludedFractionIDsVector()));

                    // for sample date interpretation, display date title box
                    DateInterpretationBoxPanel dateInterpretationBoxPanel
                            = new DateInterpretationBoxPanel(((ValueModel) sampleDateNodeInfo));

                    ((ConcordiaGraphPanel) concordiaGraphPanel).//
                            setPreferredDatePanel(dateInterpretationBoxPanel);

                    concordiaGraphPanel.repaint();
                } else if (graphPanels_TabbedPane.getSelectedIndex() == graphPanels_TabbedPane.indexOfTab("Any 2")) {
                    ((AliquotDetailsDisplayInterface) plotAny2Panel).//
                            setSelectedFractions(((ReduxAliquotInterface) aliquotNodeInfo).//
                                    getAliquotSampleDateModelSelectedFractions(((SampleDateModel) sampleDateNodeInfo).//
                                            getIncludedFractionIDsVector()));
                    ((AliquotDetailsDisplayInterface) plotAny2Panel).//
                            setDeSelectedFractions(((ReduxAliquotInterface) aliquotNodeInfo).//
                                    getAliquotSampleDateModelDeSelectedFractions(((SampleDateModel) sampleDateNodeInfo).//
                                            getIncludedFractionIDsVector()));
                    ((PlottingDetailsDisplayInterface) plotAny2Panel).refreshPanel(true, false);

                } else if (graphPanels_TabbedPane.getSelectedIndex() == graphPanels_TabbedPane.indexOfTab("USeries Isochrons")) {
                    ((AbstractPlot) useriesIsochronPanel).//
                            setMcLeanRegressionLine(((SampleDateModel) sampleDateNodeInfo).getMcLeanRegressionLine());
                    ((AliquotDetailsDisplayInterface) useriesIsochronPanel).//
                            setSelectedFractions(((ReduxAliquotInterface) aliquotNodeInfo).//
                                    getAliquotSampleDateModelSelectedFractions(((SampleDateModel) sampleDateNodeInfo).//
                                            getIncludedFractionIDsVector()));
                    ((AliquotDetailsDisplayInterface) useriesIsochronPanel).//
                            setDeSelectedFractions(((ReduxAliquotInterface) aliquotNodeInfo).//
                                    getAliquotSampleDateModelDeSelectedFractions(((SampleDateModel) sampleDateNodeInfo).//
                                            getIncludedFractionIDsVector()));
                    ((PlottingDetailsDisplayInterface) useriesIsochronPanel).refreshPanel(true, false);

                } else if (graphPanels_TabbedPane.getSelectedIndex() == graphPanels_TabbedPane.indexOfTab("Evolution")) {

                    ((AliquotDetailsDisplayInterface) evolutionPlotPanel).//
                            setSelectedFractions(((ReduxAliquotInterface) aliquotNodeInfo).//
                                    getAliquotSampleDateModelSelectedFractions(((SampleDateModel) sampleDateNodeInfo).//
                                            getIncludedFractionIDsVector()));
//                    ((AliquotDetailsDisplayInterface) useriesIsochronPanel).//
//                            setDeSelectedFractions(((ReduxAliquotInterface) aliquotNodeInfo).//
//                                    getAliquotSampleDateModelDeSelectedFractions(((SampleDateModel) sampleDateNodeInfo).//
//                                            getIncludedFractionIDsVector()));
                    ((AbstractDataView) evolutionPlotPanel).refreshPanel(true);

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
                            = ((ReduxAliquotInterface) aliquotNodeInfo).//
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
                ((AliquotDetailsDisplayInterface) concordiaGraphPanel).//
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
                        = new DateInterpretationBoxPanel(((ValueModel) sampleDateNodeInfo));
                ((ConcordiaGraphPanel) concordiaGraphPanel).//
                        setPreferredDatePanel(dateInterpretationBoxPanel);
                concordiaGraphPanel.repaint();
            } else if ((graphPanels_TabbedPane.getSelectedIndex() == graphPanels_TabbedPane.indexOfTab("Any 2"))
                    || (graphPanels_TabbedPane.getSelectedIndex() == graphPanels_TabbedPane.indexOfTab("USeries Isochrons"))) {
                // dec 2016
                ((AliquotDetailsDisplayInterface) plotAny2Panel).//
                        setSelectedFractions(((ReduxAliquotInterface) aliquotNodeInfo).//
                                getAliquotSampleDateModelSelectedFractions(((SampleDateModel) sampleDateNodeInfo).getIncludedFractionIDsVector()));
                ((AliquotDetailsDisplayInterface) plotAny2Panel).//
                        setDeSelectedFractions(((ReduxAliquotInterface) aliquotNodeInfo).//
                                getAliquotSampleDateModelDeSelectedFractions(((SampleDateModel) sampleDateNodeInfo).getIncludedFractionIDsVector()));

                // fix dateTreeByAliquot
                ((DefaultTreeModel) ((JTree) dateTreeByAliquot).getModel()).//
                        nodeChanged(((TreeNode) node).//
                                getParent().//
                                getParent().//
                                getChildAt(0));

                plotAny2Panel.repaint();

//            } else if (graphPanels_TabbedPane.getSelectedIndex() == graphPanels_TabbedPane.indexOfTab("USeries Isochrons")) {
                // dec 2016
                ((AbstractPlot) useriesIsochronPanel).//
                        setMcLeanRegressionLine(((SampleDateModel) sampleDateNodeInfo).getMcLeanRegressionLine());
                ((AliquotDetailsDisplayInterface) useriesIsochronPanel).//
                        setSelectedFractions(((ReduxAliquotInterface) aliquotNodeInfo).//
                                getAliquotSampleDateModelSelectedFractions(((SampleDateModel) sampleDateNodeInfo).getIncludedFractionIDsVector()));
                ((AliquotDetailsDisplayInterface) useriesIsochronPanel).//
                        setDeSelectedFractions(((ReduxAliquotInterface) aliquotNodeInfo).//
                                getAliquotSampleDateModelDeSelectedFractions(((SampleDateModel) sampleDateNodeInfo).getIncludedFractionIDsVector()));

//                // fix dateTreeByAliquot
//                ((DefaultTreeModel) ((JTree) dateTreeByAliquot).getModel()).//
//                        nodeChanged(((TreeNode) node).//
//                                getParent().//
//                                getParent().//
//                                getChildAt(0));
                useriesIsochronPanel.repaint();

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
                        = ((ReduxAliquotInterface) aliquotNodeInfo).//
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

            concordiaGraphPanel.repaint();

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
                    = ((DefaultMutableTreeNode) ((TreeNode) node).getParent()).getUserObject();

            // check for special case interpretations: lower and upper intercepts
            ((ConcordiaGraphPanel) concordiaGraphPanel).//
                    setYorkFitLine(((SampleDateModel) nodeInfo).getYorkLineFit());

            ((AliquotDetailsDisplayInterface) concordiaGraphPanel).//
                    setSelectedFractions(((SampleInterface) sampleNodeInfo).//
                            getSampleDateModelSelectedFractions(((SampleDateModel) nodeInfo).//
                                    getIncludedFractionIDsVector()));

            ((AliquotDetailsDisplayInterface) concordiaGraphPanel).//
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
                ((AliquotDetailsDisplayInterface) concordiaGraphPanel).//
                        setDeSelectedFractions(((SampleInterface) sampleNodeInfo).//
                                getSampleDateModelDeSelectedFractions(((SampleDateModel) sampleDateNodeInfo).//
                                        getIncludedFractionIDsVector()));

                // for sample date interpretation, display date title box
                DateInterpretationBoxPanel dateInterpretationBoxPanel
                        = new DateInterpretationBoxPanel(((ValueModel) sampleDateNodeInfo));

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
            ((AliquotDetailsDisplayInterface) concordiaGraphPanel).//
                    setDeSelectedFractions(((SampleInterface) sampleNodeInfo).//
                            getSampleDateModelDeSelectedFractions(//
                                    ((SampleDateModel) sampleDateNodeInfo).getIncludedFractionIDsVector()));

            // fix dateTreeByAliquot
            ((DefaultTreeModel) ((JTree) dateTreeBySample).getModel()).//
                    nodeChanged(((TreeNode) node).//
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

        return SampleDateInterpretationsUtilities.filterActiveUPbFractions(//
                fractions,//
                ((DateProbabilityDensityPanel) probabilityPanel).getChosenDateName(),//
                positivePctDiscordance_slider.getValue(), //
                negativePctDiscordance_slider.getValue(), //
                percentUncertainty_slider.getValue());
    }

    private void delegatePlotFileWriting() {
        String panelName = graphPanels_TabbedPane.getSelectedComponent().getName();
        if (panelName == null){
            panelName = "None";
        }
        switch (panelName) {
            case "Concordia":
                try {
                    createConcordiaSVGandPDF();
                } catch (ETException ex) {
                    new ETWarningDialog(ex).setVisible(true);
                }
                break;

            case "WeightedMean":
                createWeightedMeanSVGandPDF();
                break;

            case "Probability":
                createProbabilityDensitySVGandPDF();
                break;

            case "Evolution":
                createEvolutionSVGandPDF();
                break;

            default:
                JOptionPane.showMessageDialog(null,
                        new String[]{panelName + " " //
                            + " file output coming soon!"},
                        "ET Redux Info",
                        JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     *
     * @return @throws ETException
     */
    public File createConcordiaSVGandPDF() throws ETException {
        File selectedFile = null;
        File selectedFileSVG = null;

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

        return selectedFileSVG;
    }

    /**
     *
     */
    public void createWeightedMeanSVGandPDF() {
        File selectedFile = null;
        File selectedFileSVG = null;

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
                        = new File(selectedFile.getCanonicalPath().replaceFirst(".pdf", ".svg"));

            } catch (IOException iOException) {
            }
            try {
                ((WeightedMeanGraphPanel) weightedMeanGraphPanel).outputToSVG(selectedFileSVG);
                ((WeightedMeanGraphPanel) weightedMeanGraphPanel).outputToPDF(selectedFileSVG);
                viewPDF(selectedFile.getCanonicalPath());
            } catch (IOException ex) {
            }
        }
    }

    /**
     *
     */
    public void createProbabilityDensitySVGandPDF() {
        File selectedFile;
        File selectedFileSVG = null;

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
                        = new File(selectedFile.getCanonicalPath().replaceFirst(".pdf", ".svg"));

            } catch (IOException iOException) {
            }
            try {
                ((DateProbabilityDensityPanel) probabilityPanel).outputToSVG(selectedFileSVG);

                ((DateProbabilityDensityPanel) probabilityPanel).outputToPDF(selectedFileSVG);
                viewPDF(selectedFile.getCanonicalPath());

            } catch (IOException ex) {
            }
        }
    }
    
    
    /**
     *
     */
    public void createEvolutionSVGandPDF() {
        File selectedFile;
        File selectedFileSVG = null;

        String dialogTitle = "Save this Evolution as pdf (also as svg): *.pdf";
        final String fileExtension = ".pdf";
        String weightedMeanFileName
                = sample.getSampleName() + "_EVOLUTION" + fileExtension;
        FileFilter nonMacFileFilter = new PDFFileFilter();

        selectedFile = FileHelper.AllPlatformSaveAs(
                new Frame(), dialogTitle, null, fileExtension, weightedMeanFileName, nonMacFileFilter);

        if (selectedFile != null) {

            try {
                selectedFileSVG
                        = new File(selectedFile.getCanonicalPath().replaceFirst(".pdf", ".svg"));

            } catch (IOException iOException) {
            }
            try {
                ((EvolutionPlotPanelII) evolutionPlotPanel).outputToSVG(selectedFileSVG);

                ((EvolutionPlotPanelII) evolutionPlotPanel).outputToPDF(selectedFileSVG);
                viewPDF(selectedFile.getCanonicalPath());

            } catch (IOException ex) {
            }
        }
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

            concordiaGraphPanel.repaint();
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

            weightedMeanGraphPanel.removeAll();

            weightedMeanGraphPanel.setLayout(null);
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
                    aliquotSpecificOptions_menuItemActionPerformed(evt, a.getAliquotName(), a.getAliquotNumber());

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
                    maximaShown.put(maxDate, ((AbstractButton) evt.getSource()).isSelected());
                    probabilityPanel.repaint();
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
