/*
 * AbstractDataMonitorView.java
 *
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
package org.earthtime.Tripoli.dataViews.dataMonitorViews;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import org.earthtime.ETReduxFrame;
import org.earthtime.Tripoli.dataModels.DataModelInterface;
import org.earthtime.Tripoli.dataModels.MaskingSingleton;
import org.earthtime.Tripoli.dataViews.AbstractRawDataView;
import org.earthtime.Tripoli.dataViews.fitFunctionPresentationViews.SessionFitFunctionsPresentationView;
import org.earthtime.Tripoli.dataViews.simpleViews.FitFunctionDataInterface;
import org.earthtime.Tripoli.dataViews.simpleViews.SessionOfStandardView;
import org.earthtime.Tripoli.dataViews.simpleViews.YAxisView;
import org.earthtime.Tripoli.fractions.TripoliFraction;
import org.earthtime.Tripoli.rawDataFiles.handlers.AbstractRawDataFileHandler;
import org.earthtime.Tripoli.samples.AbstractTripoliSample;
import org.earthtime.Tripoli.sessions.TripoliSession;
import org.earthtime.Tripoli.sessions.TripoliSessionFractionationCalculatorInterface;
import org.earthtime.Tripoli.sessions.TripoliSessionInterface;
import org.earthtime.UPb_Redux.dateInterpretation.DateProbabilityDensityPanel;
import org.earthtime.UPb_Redux.dateInterpretation.SampleTreeAnalysisMode;
import org.earthtime.UPb_Redux.dateInterpretation.SampleTreeChangeI;
import org.earthtime.UPb_Redux.dateInterpretation.SampleTreeI;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.AliquotDetailsDisplayInterface;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.ConcordiaGraphPanel;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.PlottingDetailsDisplayInterface;
import org.earthtime.UPb_Redux.dateInterpretation.graphPersistence.GraphAxesSetup;
import org.earthtime.UPb_Redux.dateInterpretation.kwiki.KwikiConcordiaToolBar;
import org.earthtime.UPb_Redux.dateInterpretation.kwiki.KwikiPDFToolBar;
import org.earthtime.UPb_Redux.dialogs.parameterManagers.LAICPMSProjectParametersManager;
import org.earthtime.UPb_Redux.dialogs.projectManagers.ProjectManagerSubscribeInterface;
import org.earthtime.UPb_Redux.fractions.FractionsFilterInterface;
import org.earthtime.UPb_Redux.utilities.CustomIcon;
import org.earthtime.aliquots.AliquotInterface;
import org.earthtime.aliquots.ReduxAliquotInterface;
import org.earthtime.beans.ET_JButton;
import org.earthtime.dataDictionaries.DataPresentationModeEnum;
import org.earthtime.dataDictionaries.FractionSelectionTypeEnum;
import org.earthtime.dataDictionaries.FractionationTechniquesEnum;
import org.earthtime.dataDictionaries.IncludedTypeEnum;
import org.earthtime.fractions.ETFractionInterface;
import org.earthtime.projects.ProjectInterface;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.reduxLabData.ReduxLabData;
import org.earthtime.reportViews.TabbedReportViews;
import org.earthtime.reports.ReportSettingsInterface;
import org.earthtime.samples.SampleInterface;
import org.earthtime.utilities.TicGeneratorForAxes;

/**
 *
 * @author James F. Bowring
 */
public class AbstractDataMonitorView extends AbstractRawDataView
        implements TripoliSessionFractionationCalculatorInterface, SampleTreeChangeI {

    private static SwingWorker loadDataTask;
    private final static Integer LAYER_FIVE = 5;
    private final ClassLoader cldr = this.getClass().getClassLoader();
    private final java.net.URL imageReduxURL = cldr.getResource("org/earthtime/images/uth-pb-redux-logo.png");
    private final ImageIcon myReduxIcon;
    private JLabel redux_Icon_label;

    /**
     *
     */
    protected static ProjectInterface project;

    /**
     *
     */
    protected static DataMonitorViewDialog dataMonitorViewDialog;

    /**
     *
     */
    protected static Dimension parentDimension;

    /**
     *
     */
    protected AbstractRawDataFileHandler rawDataFileHandler;

    /**
     *
     */
    protected static Timer dataMonitorTimer;

    /**
     *
     */
    protected static File monitoredFolder;

    /**
     *
     */
    protected static long saveMonitoredTime;

    /**
     *
     */
    protected static int savedCountOfFractions;

    /**
     *
     */
    protected SortedSet<TripoliFraction> tripoliFractions;

    /**
     *
     */
    protected static TripoliSessionInterface tripoliSession;

    /**
     *
     */
    private static JLayeredPane concordiaGraphPanel;
    private static JLayeredPane kwikiConcordiaToolBar;

    private static JLayeredPane probabilityPanel;
    private static JLayeredPane kwikiPDFToolBar;

    private static ProjectManagerSubscribeInterface projectManager;

    private ArrayList<AbstractTripoliSample> tripoliSamplesSorted;

    private static JProgressBar loadDataTaskProgressBar;

    private static JTextArea rawDataFilePathTextArea;

    private final static int pdfWidth = 625;
    private final static int pdfHeight = 575;

    private JTabbedPane reportTableTabbedPane;
    private Map<String, String> probabilityChartOptions;

    private SampleTreeI dateTreeByAliquot;

    /**
     *
     */
    public AbstractDataMonitorView() {
        super();

        myReduxIcon = new CustomIcon(imageReduxURL);
        redux_Icon_label = new JLabel();
        redux_Icon_label.setBounds(leftMargin + 5, 5, 78, 50);
        ((CustomIcon) myReduxIcon).setSize(redux_Icon_label.getWidth(), redux_Icon_label.getHeight());
        redux_Icon_label.setIcon(myReduxIcon);
    }

    /**
     *
     * @param myProject
     * @param myProjectManager
     * @param uPbReduxFrame
     */
    public AbstractDataMonitorView(ProjectInterface myProject, ProjectManagerSubscribeInterface myProjectManager, ETReduxFrame uPbReduxFrame) {
        this();

        project = myProject;
        projectManager = myProjectManager;
        this.uPbReduxFrame = uPbReduxFrame;
        rawDataFileHandler = project.getRawDataFileHandler();
        leftMargin = 50;
        topMargin = 10;
        monitoredFolder = rawDataFileHandler.getAcquisitionModel().getRawDataFile();
        saveMonitoredTime = 0L;
        tripoliFractions = new TreeSet<>();

        parentDimension = new Dimension(2050, 1250);

        initView();
    }

    private void initView() {
        setOpaque(true);
        setBackground(Color.white);
        try {

            this.add(redux_Icon_label);

            rawDataFilePathTextFactory();

            buttonFactory();

            progressBarFactory();

            dataMonitorTimer = new Timer(2500, (ActionEvent e) -> {
                monitorDataFile();
            });

            dataMonitorTimer.start();

        } catch (IOException ex) {
            Logger.getLogger(LAICPMSProjectParametersManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private synchronized void monitorDataFile() {
        long lastMonitoredTime = monitoredFolder.lastModified();

        if (lastMonitoredTime > saveMonitoredTime) {

            Calendar cal = Calendar.getInstance();
            cal.clear();
            cal.setTimeInMillis(lastMonitoredTime);
            SimpleDateFormat date_format = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");

            if (loadDataTask != null) {
                if (loadDataTask.isDone()) {
                    saveMonitoredTime = lastMonitoredTime;
                    fireLoadDataTask();
                }
            } else {
                fireLoadDataTask();
            }
        }
    }

    private void fireLoadDataTask() {
        boolean isUsingFullPropagation = rawDataFileHandler.getAcquisitionModel().isUsingFullPropagation();
        int leftShadeCount = rawDataFileHandler.getAcquisitionModel().getLeftShadeCount();

        loadDataTask = new LoadDataTask(isUsingFullPropagation, leftShadeCount, savedCountOfFractions);
        loadDataTask.addPropertyChangeListener(new LoadRawDataPropertyChangeListener());
        loadDataTask.execute();

        //comment out next line for threading
        //loadAndShowRawData(rawDataFileHandler.getAcquisitionModel().isUsingFullPropagation(), 0);
    }

    private void updateDisplays() {
        // need to decide when to fit sessions
        // initially let's just show the standards

        this.removeAll();

        // june 2016 check for ref material
        if (!tripoliSession.isRefMaterialSessionFittedForLiveMode() || tripoliSession.getTripoliFractions().last().isStandard()) {
            tripoliSession.calculateSessionFitFunctionsForPrimaryStandard(true);
            tripoliSession.setRefMaterialSessionFittedForLiveMode(true);
        }

        try {
            tripoliSession.applyCorrections(true);
        } catch (Exception e) {
        }

        try {
            tripoliSession.prepareForReductionAndCommonLeadCorrection(true);
        } catch (Exception e) {
        }
        
        try {
            getuPbReduxFrame().updateReportTable(true);
        } catch (Exception e) {
        }

        preparePanel(true);
    }

    @Override
    public void preparePanel(boolean doReScale) {

        this.removeAll();

        this.add(redux_Icon_label);

        this.add(rawDataFilePathTextArea, JLayeredPane.DEFAULT_LAYER);

        int count = 0;

        SortedSet<TripoliFraction> standardFractions = FractionsFilterInterface.getTripoliFractionsFiltered(tripoliFractions, FractionSelectionTypeEnum.STANDARD, IncludedTypeEnum.ALL);

        if (tripoliFractions.size() > 0) {
            SortedSet<DataModelInterface> dataModels = tripoliFractions.first().getNonPbRatiosForFractionFitting();
            Iterator<DataModelInterface> dataModelsIterator = dataModels.iterator();

            while (dataModelsIterator.hasNext()) {
                DataModelInterface dm = dataModelsIterator.next();

                AbstractRawDataView rawDataModelView
                        = new SessionOfStandardView( //
                                this,//
                                tripoliSession.getCurrentSessionForStandardsFractionation().get(dm.getRawRatioModelName()), //
                                standardFractions,//
                                dm, //
                                DataPresentationModeEnum.RATIO,//
                                new Rectangle(//
                                        90, //
                                        count * (160 + 50) + topMargin + 50, //
                                        250,//
                                        160));

                rawDataModelView.preparePanel(doReScale);

                this.add(rawDataModelView, JLayeredPane.DEFAULT_LAYER);

                double overallMinY = rawDataModelView.getMinY();
                double overallMaxY = rawDataModelView.getMaxY();

                // generate tics array for standards all
                BigDecimal[] yAxisTics = null;

                yAxisTics = TicGeneratorForAxes.generateTics(overallMinY, overallMaxY, (int) (160 / 20.0));
                if (yAxisTics.length > 15) {
                    yAxisTics = TicGeneratorForAxes.generateTics(overallMinY, overallMaxY, (int) (160 / 32.0));
                }

                // create margins for y-values after axis tics calculated
                double yMarginStretch = TicGeneratorForAxes.generateMarginAdjustment(overallMinY, overallMaxY, 12.0 / 160);
                overallMinY -= yMarginStretch;
                overallMaxY += yMarginStretch;

                AbstractRawDataView yAxisPane = new YAxisView( //
                        new AbstractRawDataView[]{rawDataModelView},
                        this,//
                        Color.white, //
                        overallMinY,//
                        overallMaxY,//
                        new Rectangle( //
                                5, count * (160 + 50) + topMargin + 50, //
                                90, //
                                160),//
                        false, true);
                yAxisPane.setTics(yAxisTics);

                this.add(yAxisPane, JLayeredPane.DEFAULT_LAYER);

                AbstractRawDataView sessionFitFunctionsPresentationView
                        = new SessionFitFunctionsPresentationView( //
                                this,//
                                tripoliSession.getCurrentSessionForStandardsFractionation().get(dm.getRawRatioModelName()), //
                                (FitFunctionDataInterface) rawDataModelView,//
                                DataPresentationModeEnum.RATIO, //
                                new Rectangle( //
                                        345, //
                                        count * (160 + 50) + topMargin + 50, //
                                        250, //
                                        180));

                sessionFitFunctionsPresentationView.preparePanel(doReScale);

                this.add(sessionFitFunctionsPresentationView, JLayeredPane.DEFAULT_LAYER);

                count++;
            }
            prepareConcordia();

            preparePDF();

            prepareDateTree();
        }

        buttonFactory();

        savedCountOfFractions = tripoliFractions.size();

        reportTableTabbedPane = new TabbedReportViews(uPbReduxFrame, project.getSuperSample());
        ((TabbedReportViews) reportTableTabbedPane).initializeTabs();
        ((TabbedReportViews) reportTableTabbedPane).prepareTabs();

        reportTableTabbedPane.setBounds(leftMargin, topMargin + 705, 1900, 500);
        this.add(reportTableTabbedPane, LAYER_FIVE);

    }

    private void rawDataFilePathTextFactory() throws IOException {
        // show rawdatafile path
        rawDataFilePathTextArea = new JTextArea(//
                "Monitoring Raw data location: "
                + rawDataFileHandler.getAcquisitionModel().getRawDataFile().getCanonicalPath());

        rawDataFilePathTextArea.setBounds(leftMargin + 50, topMargin, parentDimension.width - 100, 40);
        rawDataFilePathTextArea.setLineWrap(true);
        rawDataFilePathTextArea.setEditable(false);
        rawDataFilePathTextArea.setForeground(Color.RED);
        this.add(rawDataFilePathTextArea, JLayeredPane.DEFAULT_LAYER);
    }

    private void buttonFactory() {
        ET_JButton closeAndReviewButton = new ET_JButton("Halt Processing and Review Samples");
        closeAndReviewButton.setBounds(leftMargin + 0, topMargin + 660, 450, 25);
        closeAndReviewButton.addActionListener((ActionEvent ae) -> {
            try {
                loadDataTask.cancel(true);
            } catch (Exception e) {
            }
            projectManager.updateDataChangeStatus(false);
            projectManager.displaySamples(tripoliSession, tripoliSamplesSorted);
            dataMonitorTimer.stop();
            dataMonitorViewDialog.dispose();
        });

        closeAndReviewButton.setEnabled(true);
        this.add(closeAndReviewButton, LAYER_FIVE);

        ET_JButton recalcButton = new ET_JButton("Re-calculate rhos");
        recalcButton.setBounds(leftMargin + 450, topMargin + 660, 120, 25);
        recalcButton.addActionListener((ActionEvent ae) -> {
            try {
                tripoliSession.interceptCalculatePbcCorrAndRhos();
            } catch (Exception e) {
            }
            try {
                getuPbReduxFrame().updateReportTable(true);
            } catch (Exception e) {
            }
            preparePanel(true);
        });

        recalcButton.setEnabled(true);
        this.add(recalcButton, LAYER_FIVE);

        ET_JButton refreshButton = new ET_JButton("Refresh Views");
        refreshButton.setBounds(leftMargin + 570, topMargin + 660, 120, 25);
        refreshButton.addActionListener((ActionEvent ae) -> {
            preparePanel(true);
        });

        refreshButton.setEnabled(true);
        this.add(refreshButton, LAYER_FIVE);

        ET_JButton editReportSettingsButton = new ET_JButton("Edit Report Settings");
        editReportSettingsButton.setBounds(602, topMargin + 600, 120, 25);
        editReportSettingsButton.addActionListener((ActionEvent ae) -> {
            ReportSettingsInterface.EditReportSettings(project.getSuperSample().getReportSettingsModel(), uPbReduxFrame);
            uPbReduxFrame.updateReportTable(false);
            ((TabbedReportViews) reportTableTabbedPane).prepareTabs();
        });

        editReportSettingsButton.setEnabled(true);
        this.add(editReportSettingsButton, LAYER_FIVE);

        ET_JButton concordiaSettingsButton = new ET_JButton("Concordia Settings");
        concordiaSettingsButton.setBounds(602, topMargin + 550, 120, 25);
        concordiaSettingsButton.addActionListener((ActionEvent ae) -> {
            ((AliquotDetailsDisplayInterface) concordiaGraphPanel).showConcordiaDisplayOptionsDialog();
        });

        concordiaSettingsButton.setEnabled(true);
        this.add(concordiaSettingsButton, LAYER_FIVE);
    }

    private void progressBarFactory() {
        loadDataTaskProgressBar = new javax.swing.JProgressBar();
        loadDataTaskProgressBar.setFont(new java.awt.Font("SansSerif", 0, 13));
        loadDataTaskProgressBar.setStringPainted(true);
        loadDataTaskProgressBar.setMaximum(100);
        loadDataTaskProgressBar.setMinimum(0);
        loadDataTaskProgressBar.setValue(0);
        loadDataTaskProgressBar.setBounds(leftMargin + 0, 695, 450, 20);
    }

    /**
     *
     */
    public void prepareConcordia() {

        if (concordiaGraphPanel != null) {
            this.remove(concordiaGraphPanel);
        }
        if (kwikiConcordiaToolBar != null) {
            this.remove(kwikiConcordiaToolBar);
        }

        if (savedCountOfFractions == 0) {
            concordiaGraphPanel = new ConcordiaGraphPanel(project.getSuperSample(), getuPbReduxFrame());

            ((ConcordiaGraphPanel) concordiaGraphPanel).setCurrentGraphAxesSetup(new GraphAxesSetup("C", 2));

            setConcordiaBounds(725, 620, 625);

            ((ConcordiaGraphPanel) concordiaGraphPanel).setShowTitleBox(false);

            ((ConcordiaGraphPanel) concordiaGraphPanel).//
                    setYorkFitLine(null);
            ((ConcordiaGraphPanel) concordiaGraphPanel).//
                    setFadedDeselectedFractions(true);

            project.getSuperSample().getSampleDateInterpretationGUISettings().//
                    setConcordiaOptions(((AliquotDetailsDisplayInterface) concordiaGraphPanel).getConcordiaOptions());
            probabilityChartOptions = project.getSuperSample().getSampleDateInterpretationGUISettings().getProbabilityChartOptions();

            ((ConcordiaGraphPanel) concordiaGraphPanel).//
                    setFadedDeselectedFractions(false);

            ((ConcordiaGraphPanel) concordiaGraphPanel).setShowTightToEdges(true);

            kwikiConcordiaToolBar = new KwikiConcordiaToolBar(//
                    940, topMargin + concordiaGraphPanel.getHeight() + topMargin + 50, concordiaGraphPanel, null);

        }

        ((ConcordiaGraphPanel) concordiaGraphPanel).setSample(project.getSuperSample());
        ((ConcordiaGraphPanel) concordiaGraphPanel).setViewOptions();
        ((ConcordiaGraphPanel) concordiaGraphPanel).setShowBestDateDivider206_238(true);

        add(concordiaGraphPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        try {
            Vector<ETFractionInterface> selectedFractions
                    = project.getSuperSample().getFractions();

            ((AliquotDetailsDisplayInterface) concordiaGraphPanel).//
                    setSelectedFractions(selectedFractions);
            ((PlottingDetailsDisplayInterface) concordiaGraphPanel).resetPanel(true);
        } catch (Exception e) {
        }

        add(kwikiConcordiaToolBar, javax.swing.JLayeredPane.DEFAULT_LAYER);

        JCheckBox showFilteredFractions_checkbox = new JCheckBox();
        showFilteredFractions_checkbox.setFont(new java.awt.Font("SansSerif", 1, 10));
        showFilteredFractions_checkbox.setText("<html>Filtering ON<br> </html>");
        showFilteredFractions_checkbox.setBounds(leftMargin + 1075, topMargin + 660, 120, 25);
        showFilteredFractions_checkbox.addActionListener((java.awt.event.ActionEvent evt) -> {
            boolean state = ((AliquotDetailsDisplayInterface) concordiaGraphPanel).isShowFilteredEllipses();
            ((AliquotDetailsDisplayInterface) concordiaGraphPanel).setShowFilteredEllipses(!state);
            showFilteredFractions_checkbox.setSelected(!state);
            probabilityChartOptions.put("showFilteredEllipses", Boolean.toString(!state));
            concordiaGraphPanel.repaint();
        });

        if (probabilityChartOptions.containsKey("showFilteredEllipses")) {
            showFilteredFractions_checkbox.setSelected(Boolean.valueOf(probabilityChartOptions.get("showFilteredEllipses")));
            ((AliquotDetailsDisplayInterface) concordiaGraphPanel).setShowFilteredEllipses(showFilteredFractions_checkbox.isSelected());
        }

        this.add(showFilteredFractions_checkbox, LAYER_FIVE);

    }

    private void setConcordiaBounds(int leftSide, int widthC, int heightC) {
        ((ConcordiaGraphPanel) concordiaGraphPanel).setGraphWidth(widthC - 25.0);
        ((ConcordiaGraphPanel) concordiaGraphPanel).setGraphHeight(heightC - 50.0);

        int widthCP = (int) ((ConcordiaGraphPanel) concordiaGraphPanel).getGraphWidth();
        int heightCP = (int) ((ConcordiaGraphPanel) concordiaGraphPanel).getGraphHeight();
        int leftMarginCP = ((ConcordiaGraphPanel) concordiaGraphPanel).getLeftMargin();

        concordiaGraphPanel.setBounds(
                leftSide, topMargin + 50, widthCP + leftMarginCP, heightCP + 16);
    }

    /**
     *
     */
    public void preparePDF() {

        if (probabilityPanel != null) {
            this.remove(probabilityPanel);
        }
        if (kwikiPDFToolBar != null) {
            this.remove(kwikiPDFToolBar);
        }

        probabilityPanel = new DateProbabilityDensityPanel(project.getSuperSample());

        ((DateProbabilityDensityPanel) probabilityPanel).//
                setSelectedFractions(filterActiveUPbFractions(project.getSuperSample().getUpbFractionsUnknown()));

        probabilityPanel.setBounds(1355, topMargin + 60, pdfWidth, pdfHeight - 5);

        ((DateProbabilityDensityPanel) probabilityPanel).setChosenDateName("age206_238r");

        ((DateProbabilityDensityPanel) probabilityPanel).setGraphWidth(pdfWidth - 25);

        ((DateProbabilityDensityPanel) probabilityPanel).setGraphHeight(pdfHeight - 25);

        ((DateProbabilityDensityPanel) probabilityPanel).prepareAndPaintPanel();

        add(probabilityPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        kwikiPDFToolBar = new KwikiPDFToolBar(1350, concordiaGraphPanel.getHeight() + topMargin + 60, probabilityPanel, concordiaGraphPanel, project.getSuperSample(), dateTreeByAliquot);
        add(kwikiPDFToolBar, javax.swing.JLayeredPane.DEFAULT_LAYER);

    }

    public void prepareDateTree() {
        // april 2016 TODO: move to own method
        JScrollPane dateTreeByAliquot_ScrollPane = new javax.swing.JScrollPane();
        dateTreeByAliquot_ScrollPane.setBounds(600, topMargin + 50, 125, 500);
        dateTreeByAliquot = new SampleTreeAnalysisMode(project.getSuperSample());
        dateTreeByAliquot.setSampleTreeChange(this);
        dateTreeByAliquot.buildTree();
        dateTreeByAliquot.expandAllNodes();
        dateTreeByAliquot_ScrollPane.setViewportView((Component) dateTreeByAliquot);

        add(dateTreeByAliquot_ScrollPane);
    }

    private Vector<ETFractionInterface> filterActiveUPbFractions(Vector<ETFractionInterface> fractions) {

        Vector<ETFractionInterface> filteredFractions = new Vector<>();

        String dateName = ((DateProbabilityDensityPanel) probabilityPanel).getChosenDateName();

        for (ETFractionInterface f : fractions) {
            boolean doAddFraction = !f.isRejected();
//            double pctDiscordance = f.getRadiogenicIsotopeDateByName(RadDates.percentDiscordance).getValue().doubleValue();
//
//            if (pctDiscordance >= 0.0) {  //
//                // positive percent discordance
//                doAddFraction = doAddFraction && (pctDiscordance <= positivePctDiscordance_slider.getValue());
//            } else {
//                // negative percent discordance
//                doAddFraction = doAddFraction && (pctDiscordance >= negativePctDiscordance_slider.getValue());
//            }
//            doAddFraction = doAddFraction //
//                    && f.getRadiogenicIsotopeDateByName(dateName).getOneSigmaPct().doubleValue() //
//                    <= percentUncertainty_slider.getValue();

            if (doAddFraction) {
                filteredFractions.add(f);
            }
        }
        return filteredFractions;
    }

    @Override
    public DataModelInterface getDataModel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void calculateDownholeFitSummariesForPrimaryStandard() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void calculateSessionFitFunctionsForPrimaryStandard(boolean inLiveMode) {
        tripoliSession.calculateSessionFitFunctionsForPrimaryStandard(inLiveMode);
    }

    @Override
    public void applyCorrections(boolean inLiveMode) {
        tripoliSession.applyCorrections(inLiveMode);
    }

    @Override
    public boolean isCalculatedInitialFitFunctions() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void loadAndShowRawData(//
            boolean usingFullPropagation, int leftShadeCount, int ignoreFirstFractions) {

        MaskingSingleton.getInstance().setLeftShadeCount(leftShadeCount);
        MaskingSingleton.getInstance().setRightShadeCount(-1);

        rawDataFileHandler.getAndLoadRawIntensityDataFile(//
                loadDataTask, usingFullPropagation, leftShadeCount, ignoreFirstFractions, true);

    }

    private void loadAndShowRawDataFinishUp() {
        SortedSet<TripoliFraction> tripoliFractionsCurrent;

        if (savedCountOfFractions == 0) {

            if (tripoliFractions == null) {
                tripoliFractions = new TreeSet<>();
            }

            tripoliSamplesSorted = rawDataFileHandler.parseFractionsIntoSamples();

            tripoliFractionsCurrent = rawDataFileHandler.getTripoliFractions();
            tripoliFractions.addAll(tripoliFractionsCurrent);

            // create session
            tripoliSession
                    = new TripoliSession(//
                            rawDataFileHandler, tripoliSamplesSorted);

            project.setTripoliSession(tripoliSession);

            tripoliSession.setPrimaryMineralStandard(project.getAcquisitionModel().getPrimaryMineralStandardModel());
            tripoliSession.setLeftShadeCount(project.getAcquisitionModel().getLeftShadeCount());

            // first pass without any user interaction
            tripoliSession.setEstimatedPlottingPointsCount(1000);

            tripoliSession.setFractionationTechnique(FractionationTechniquesEnum.INTERCEPT);
        } else {

            tripoliFractionsCurrent = rawDataFileHandler.getTripoliFractions();
            tripoliFractions.addAll(tripoliFractionsCurrent);
            rawDataFileHandler.setTripoliFractions(tripoliFractions);

            tripoliSamplesSorted = rawDataFileHandler.parseFractionsIntoSamples();

            tripoliSession.setTripoliFractions(tripoliFractions);
            tripoliSession.setTripoliSamples(tripoliSamplesSorted);

        }

        if (tripoliFractions.size() > 0) {
            // first align standard status of all fractions
            tripoliSession.updateFractionsToSampleMembership();

            // april 2012
            // save mineral standard model choice and update all raw ratios with standard value
            AbstractRatiosDataModel primaryMineralStandard = tripoliSamplesSorted.get(0).getMineralStandardModel();
            if (primaryMineralStandard == null) {
                System.out.println("Auto selecting mineral standard model for primary standard.");
                primaryMineralStandard = ReduxLabData.getInstance().getMineralStandardModels().get(3);//3=sri lanka
            } //else {

            tripoliSession.setPrimaryMineralStandard(primaryMineralStandard);

            tripoliSession.getTripoliSamples().get(0).setMineralStandardModel(primaryMineralStandard);

            tripoliSession.prepareFractionTimeStamps();
            tripoliSession.processRawData(true);

//            tripoliSession.postProcessDataForCommonLeadLossPreparation();
            try {
                loadDataTaskProgressBar.repaint();

            } catch (Exception e) {
            }

            project.prepareSamplesForRedux();

            getuPbReduxFrame().initializeProject();

            updateDisplays();
        }

    }

    private class LoadDataTask extends SwingWorker<Void, Void> {

        /*
         * Main loadDataTask. Executed in background thread.
         */
        private final boolean usingFullPropagation;
        private final int ignoreFirstFractions;
        private final int leftShadeCount;

        public LoadDataTask(boolean usingFullPropagation, int leftShadeCount, int ignoreFirstFractions) {
            this.usingFullPropagation = usingFullPropagation;
            this.ignoreFirstFractions = ignoreFirstFractions;
            this.leftShadeCount = leftShadeCount;
        }

        @Override
        public Void doInBackground() {
            //Initialize progress property.
            add(loadDataTaskProgressBar);
            //loadDataTaskProgressBar.setVisible(true);
            loadDataTaskProgressBar.setValue(0);
            loadDataTaskProgressBar.repaint();
            //setProgress(0);
            repaint();

            loadAndShowRawData(usingFullPropagation, leftShadeCount, ignoreFirstFractions);
            return null;
        }

        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
//            Toolkit.getDefaultToolkit().beep();
            remove(loadDataTaskProgressBar);
            //loadDataTaskProgressBar.setVisible(false);
            loadDataTaskProgressBar.setValue(0);
            loadDataTaskProgressBar.repaint();
            //setProgress(0);
            repaint();
            System.out.println("LOADING TASK DONE !!");
            loadAndShowRawDataFinishUp();
        }
    }

    /**
     *
     */
    public class LoadRawDataPropertyChangeListener implements PropertyChangeListener {

        /**
         *
         */
        public LoadRawDataPropertyChangeListener() {
        }

        @Override
        public void propertyChange(PropertyChangeEvent pce) {
            if ("progress".equalsIgnoreCase(pce.getPropertyName())) {
                int progress = (Integer) pce.getNewValue();
                loadDataTaskProgressBar.setValue(progress);
                loadDataTaskProgressBar.repaint();//.validate();
            }
        }
    }

    /**
     *
     */
    protected class DataMonitorViewDialog extends javax.swing.JDialog {

        /**
         *
         * @param owner
         * @param modal
         */
        public DataMonitorViewDialog(Dialog owner, boolean modal) {
            super(owner, modal);
        }
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
    @Override
    protected void paintInit(Graphics2D g2d) {
        RenderingHints rh = g2d.getRenderingHints();
        rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHints(rh);

        g2d.setPaint(Color.BLACK);
        g2d.setStroke(new BasicStroke(1.0f));
        g2d.setFont(new Font(
                "SansSerif",
                Font.BOLD,
                10));

    }

    /**
     *
     * @param g2d
     */
    @Override
    public void paint(Graphics2D g2d) {
        paintInit(g2d);

        // draw box around possibly missing pdf
//        g2d.drawRect(1145, topMargin - 2, 510, 500);
        g2d.drawRect(1350, topMargin + 50, pdfWidth + 5, pdfHeight + 5);
    }

    /**
     *
     * @return
     */
    public JDialog displayModelInFrame() {

        dataMonitorViewDialog = new DataMonitorViewDialog(null, true);
        dataMonitorViewDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        dataMonitorViewDialog.setBounds( //
                400, 400, //
                (int) parentDimension.getWidth(),
                (int) parentDimension.getHeight());
        dataMonitorViewDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);//.HIDE_ON_CLOSE);

        //Get the screen size
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();

        //Calculate the frame location
        int x = (screenSize.width - (int) parentDimension.getWidth()) / 2;
        int y = (screenSize.height - (int) parentDimension.getHeight()) / 2;

        //Set the new frame location centered
        dataMonitorViewDialog.setLocation(x, y);

        JScrollPane liveDataScrollPane = new javax.swing.JScrollPane();

        liveDataScrollPane.setPreferredSize(dataMonitorViewDialog.getSize());//new Dimension(500, 500));

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(this);
        this.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
                jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(25, dataMonitorViewDialog.getWidth() + 25, Short.MAX_VALUE)
        );
        jLayeredPane1Layout.setVerticalGroup(
                jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(25, dataMonitorViewDialog.getHeight() + 25, Short.MAX_VALUE)
        );

        liveDataScrollPane.setViewportView(this);

        //        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(dataMonitorViewDialog.getContentPane());
        //        dataMonitorViewDialog.getContentPane().setLayout(layout);
        //        layout.setHorizontalGroup(
        //            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        //            .addComponent(liveDataScrollPane)
        //        );
        //        layout.setVerticalGroup(
        //            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        //            .addComponent(liveDataScrollPane)
        //        );
        dataMonitorViewDialog.add(liveDataScrollPane);
        dataMonitorViewDialog.pack();

        dataMonitorViewDialog.addComponentListener(new ComponentListener() {

            @Override
            public void componentResized(ComponentEvent e) {
//                setConcordiaBounds(605, dataMonitorViewDialog.getWidth() / 4, dataMonitorViewDialog.getHeight() / 4);
//                concordiaGraphPanel.repaint();
            }

            @Override
            public void componentMoved(ComponentEvent e) {
                //                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void componentShown(ComponentEvent e) {
                //                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void componentHidden(ComponentEvent e) {
                //                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });

//        try {
        dataMonitorViewDialog.setVisible(true);
//        } catch (Exception e) {
//        }
        return dataMonitorViewDialog;
    }

    /**
     * @return the concordiaGraphPanel
     */
    public JLayeredPane getConcordiaGraphPanel() {
        return concordiaGraphPanel;
    }

    @Override
    public void sampleTreeChangeCompilationMode(Object node) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sampleTreeChangeAnalysisMode(Object node) {
        //  System.out.println("WOW MODE CALL BACK");
        Object nodeInfo = ((DefaultMutableTreeNode) node).getUserObject();
        if (nodeInfo instanceof SampleInterface) {

            ((ConcordiaGraphPanel) concordiaGraphPanel).//
                    setYorkFitLine(null);
            ((AliquotDetailsDisplayInterface) concordiaGraphPanel).//
                    setSelectedFractions(project.getSuperSample().getFractions());
            concordiaGraphPanel.repaint();
            // zap deselected list as it is meaningless at level of aliquot or sample
            ((AliquotDetailsDisplayInterface) concordiaGraphPanel).//
                    getDeSelectedFractions().clear();
            ((ConcordiaGraphPanel) concordiaGraphPanel).//
                    setPreferredDatePanel(null);

            // may 2014 show best date line
            ((ConcordiaGraphPanel) concordiaGraphPanel).setShowingSingleAliquot(false);

            ((DateProbabilityDensityPanel) probabilityPanel).//
                    setSelectedFractions(filterActiveUPbFractions(project.getSuperSample().getUpbFractionsUnknown()));
            ((DateProbabilityDensityPanel) probabilityPanel).//
                    getDeSelectedFractions().clear();
            ((DateProbabilityDensityPanel) probabilityPanel).//
                    setSelectedAliquot(0);
            ((PlottingDetailsDisplayInterface) probabilityPanel).//
                    refreshPanel(true);

        } else if (nodeInfo instanceof AliquotInterface) {

            // oct 2011 removed conditionals here to force all tabs to update
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

            ((DateProbabilityDensityPanel) probabilityPanel).//
                    setSelectedAliquot(((ReduxAliquotInterface) nodeInfo).getAliquotNumber());
            ((DateProbabilityDensityPanel) probabilityPanel).//
                    getDeSelectedFractions().clear();
            probabilityPanel.repaint();
        }
    }
}
