/*
 * ETReduxFrame.java
 *
 * Created on March 14, 2006, 8:27 PM
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
package org.earthtime;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.filechooser.FileFilter;
import org.earthtime.Tripoli.dataModels.MaskingSingleton;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.aliquots.UPbReduxAliquot;
import org.earthtime.UPb_Redux.dateInterpretation.DateProbabilityDensityPanel;
import org.earthtime.UPb_Redux.dateInterpretation.SampleTreeAnalysisMode;
import org.earthtime.UPb_Redux.dateInterpretation.SampleTreeCompilationMode;
import org.earthtime.UPb_Redux.dateInterpretation.SampleTreeI;
import org.earthtime.UPb_Redux.dateInterpretation.WeightedMeanGraphPanel;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.ConcordiaGraphPanel;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.ConcordiaPlotDisplayInterface;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.PlottingDetailsDisplayInterface;
import org.earthtime.UPb_Redux.dialogs.aliquotManagers.AliquotEditorDialog;
import org.earthtime.UPb_Redux.dialogs.aliquotManagers.AliquotEditorForLAICPMS;
import org.earthtime.UPb_Redux.dialogs.aliquotManagers.AliquotLegacyEditorForIDTIMS;
import org.earthtime.UPb_Redux.dialogs.aliquotManagers.AliquotLegacyEditorForLAICPMS;
import org.earthtime.UPb_Redux.dialogs.fractionManagers.UPbFractionEditorDialog;
import org.earthtime.UPb_Redux.dialogs.fractionManagers.UPbLegacyFractionEditorDialog;
import org.earthtime.UPb_Redux.dialogs.projectManagers.ProjectManagerFor_LAICPMS_FromRawData;
import org.earthtime.UPb_Redux.dialogs.projectManagers.ProjectManagerFor_SHRIMP_FromRawData;
import org.earthtime.UPb_Redux.dialogs.projectManagers.ProjectManagerSubscribeInterface;
import org.earthtime.UPb_Redux.dialogs.projectManagers.exportManagers.GeochronProjectExportManager;
import org.earthtime.UPb_Redux.dialogs.sampleManagers.GeochronSampleCustomMetadataDialog;
import org.earthtime.UPb_Redux.dialogs.sampleManagers.analysisManagers.SampleAnalysisWorkflowManagerIDTIMS;
import org.earthtime.UPb_Redux.dialogs.sampleManagers.analysisManagers.SampleAnalysisWorkflowManagerInterface;
import org.earthtime.UPb_Redux.dialogs.sampleManagers.compilationManagers.SampleCompilationManagerDialog;
import org.earthtime.UPb_Redux.dialogs.sampleManagers.legacyManagers.AbstractSampleLegacyManagerDialog;
import org.earthtime.UPb_Redux.dialogs.sampleManagers.legacyManagers.SampleLegacyDataManagerDialogForIDTIMS_MIT;
import org.earthtime.UPb_Redux.dialogs.sampleManagers.legacyManagers.SampleLegacyDataManagerDialogForLAICPMS_MC_UA;
import org.earthtime.UPb_Redux.dialogs.sampleManagers.legacyManagers.SampleLegacyDataManagerDialogForLAICPMS_MC_UA_VV;
import org.earthtime.UPb_Redux.dialogs.sampleManagers.legacyManagers.SampleLegacyDataManagerDialogForLAICPMS_NIGL;
import org.earthtime.UPb_Redux.dialogs.sampleManagers.legacyManagers.SampleLegacyDataManagerDialogForLAICPMS_SC_WSU_vA;
import org.earthtime.UPb_Redux.dialogs.sampleManagers.legacyManagers.SampleLegacyDataManagerDialogForLAICPMS_SC_WSU_vB;
import org.earthtime.UPb_Redux.dialogs.sampleManagers.legacyManagers.SampleLegacyDataManagerDialogForLAICPMS_UH;
import org.earthtime.UPb_Redux.dialogs.sampleManagers.legacyManagers.SampleLegacyDataManagerDialogForLASS;
import org.earthtime.UPb_Redux.dialogs.sampleManagers.sampleDateInterpretationManagers.SampleDateInterpretationSubscribeInterface;
import org.earthtime.UPb_Redux.dialogs.sampleManagers.sampleDateInterpretationManagers.SampleDateInterpretationsManager;
import org.earthtime.UPb_Redux.dialogs.sampleManagers.sampleFromProjectManagers.AbstractSampleFromProjectManagerDialog;
import org.earthtime.UPb_Redux.dialogs.sampleManagers.sampleFromProjectManagers.SampleManagerDialogForTripolizedLAICPMS;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.UPb_Redux.filters.ReduxFileFilter;
import org.earthtime.UPb_Redux.filters.XMLFileFilter;
import org.earthtime.UPb_Redux.fractions.FractionI;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFractionTable;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbLAICPMSFraction;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.fractionReduction.PbcCorrectionDetails;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.fractionReduction.UPbFractionReducer;
import org.earthtime.UPb_Redux.reports.ReportSettings;
import org.earthtime.UPb_Redux.reports.excelReports.CsvResultsTable;
import org.earthtime.UPb_Redux.reports.excelReports.ExcelResultsTable;
import org.earthtime.UPb_Redux.samples.Sample;
import org.earthtime.UPb_Redux.samples.sampleImporters.SampleImporterFromIDTIMSLegacyCSVFile_MIT;
import org.earthtime.UPb_Redux.samples.sampleImporters.SampleImporterFromLAICPMSLegacyCVSFile_MC_UA;
import org.earthtime.UPb_Redux.samples.sampleImporters.SampleImporterFromLAICPMSLegacyCVSFile_NIGL;
import org.earthtime.UPb_Redux.samples.sampleImporters.SampleImporterFromLAICPMSLegacyCVSFile_SC_WSU_vA;
import org.earthtime.UPb_Redux.samples.sampleImporters.SampleImporterFromLAICPMSLegacyCVSFile_SC_WSU_vB;
import org.earthtime.UPb_Redux.user.ReduxPersistentState;
import org.earthtime.UPb_Redux.utilities.AnnouncementPane;
import org.earthtime.UPb_Redux.utilities.BrowserControl;
import static org.earthtime.UPb_Redux.utilities.BrowserControl.urlEncode;
import org.earthtime.UPb_Redux.utilities.CustomIcon;
import org.earthtime.UPb_Redux.utilities.ETSerializer;
import org.earthtime.plots.evolution.TopsoilEvolutionPlot;
import org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException;
import org.earthtime.aliquots.AliquotInterface;
import org.earthtime.aliquots.ReduxAliquotInterface;
import org.earthtime.beans.ET_JButton;
import org.earthtime.dataDictionaries.AnalysisMeasures;
import org.earthtime.dataDictionaries.SampleAnalysisTypesEnum;
import static org.earthtime.dataDictionaries.SampleAnalysisTypesEnum.COMPILED;
import static org.earthtime.dataDictionaries.SampleAnalysisTypesEnum.GENERIC_GA_UPB;
import static org.earthtime.dataDictionaries.SampleAnalysisTypesEnum.GENERIC_SQUID3_UPB;
import static org.earthtime.dataDictionaries.SampleAnalysisTypesEnum.GENERIC_UPB;
import static org.earthtime.dataDictionaries.SampleAnalysisTypesEnum.IDTIMS;
import static org.earthtime.dataDictionaries.SampleAnalysisTypesEnum.LAICPMSMC;
import static org.earthtime.dataDictionaries.SampleAnalysisTypesEnum.LAICPMS_MCUA;
import static org.earthtime.dataDictionaries.SampleAnalysisTypesEnum.LAICPMS_NIGL;
import static org.earthtime.dataDictionaries.SampleAnalysisTypesEnum.LAICPMS_SCWSU_vA;
import static org.earthtime.dataDictionaries.SampleAnalysisTypesEnum.LAICPMS_SCWSU_vB;
import static org.earthtime.dataDictionaries.SampleAnalysisTypesEnum.LAICPMS_SCWSU_vV;
import static org.earthtime.dataDictionaries.SampleAnalysisTypesEnum.LAICPMS_UH;
import static org.earthtime.dataDictionaries.SampleAnalysisTypesEnum.LASS;
import static org.earthtime.dataDictionaries.SampleAnalysisTypesEnum.USERIES_CARB;
import static org.earthtime.dataDictionaries.SampleAnalysisTypesEnum.USERIES_IGN;
import org.earthtime.dataDictionaries.SampleTypesEnum;
import org.earthtime.dialogs.AboutBox;
import org.earthtime.dialogs.DialogEditor;
import org.earthtime.dialogs.LabDataEditorDialogUPb;
import org.earthtime.dialogs.LabDataEditorDialogUTh;
import org.earthtime.dialogs.PreferencesEditorDialog;
import org.earthtime.dialogs.projectManagers.projectLegacyManagers.AbstractProjectOfLegacySamplesDataManagerDialog;
import org.earthtime.dialogs.projectManagers.projectLegacyManagers.ProjectOfLegacySamplesDataManagerDialogForGenericUPb_A;
import org.earthtime.dialogs.projectManagers.projectLegacyManagers.ProjectOfLegacySamplesDataManagerDialogForGenericUPb_GA;
import org.earthtime.dialogs.projectManagers.projectLegacyManagers.ProjectOfLegacySamplesDataManagerDialogForUCSB_LASS_A;
import org.earthtime.dialogs.projectManagers.projectLegacyManagers.ProjectOfLegacySamplesDataManagerUseries_Carb;
import org.earthtime.dialogs.projectManagers.projectLegacyManagers.ProjectOfLegacySamplesDataManagerUseries_Ign;
import org.earthtime.dialogs.projectManagers.projectLegacyManagers.ProjectOfSamplesDataManagerDialogForGenericUPb_Squid3;
import org.earthtime.exceptions.ETException;
import org.earthtime.exceptions.ETWarningDialog;
import org.earthtime.fractions.ETFractionInterface;
import org.earthtime.plots.anyTwo.PlotAny2Panel;
import org.earthtime.plots.evolution.AgeByDelta234UPlotPanel;
import org.earthtime.plots.evolution.EvolutionPlotPanel;
import org.earthtime.plots.isochrons.IsochronsPanel;
import org.earthtime.projects.EarthTimeSerializedFileInterface;
import org.earthtime.projects.Project;
import org.earthtime.projects.ProjectInterface;
import org.earthtime.projects.ProjectSample;
import org.earthtime.projects.projectImporters.UPbProjectImporters.ProjectOfLegacySamplesImporterFromCSVFile_GenericUPbIsotopic_A;
import org.earthtime.projects.projectImporters.UPbProjectImporters.ProjectOfLegacySamplesImporterFromCSVFile_UCSB_LASS_A;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataModels.mineralStandardModels.MineralStandardUPbModel;
import org.earthtime.reduxLabData.ReduxLabData;
import org.earthtime.reportViews.ReportAliquotFractionsView;
import org.earthtime.reportViews.ReportPainterI;
import org.earthtime.reportViews.ReportUpdaterInterface;
import org.earthtime.reportViews.TabbedReportViews;
import org.earthtime.reports.ReportSettingsInterface;
import org.earthtime.samples.SampleInterface;
import org.earthtime.utilities.FileHelper;

/**
 *
 * @author James F. Bowring
 */
public class ETReduxFrame extends javax.swing.JFrame implements ReportPainterI, ReportUpdaterInterface, SampleDateInterpretationSubscribeInterface {

    // user-specific configurations
    public final static int FRAME_WIDTH = 1175;
    private ReduxPersistentState myState;
    private final ClassLoader cldr;
    private final java.net.URL imageURL;
//    private final InputStream changeLogURL;
    private final InputStream creditsURL;
    /**
     *
     */
    protected ImageIcon reduxIcon;
    // theSample is serialized as a *.redux file
    private transient SampleInterface theSample = null;
    // theProject is serialized as a *.redux file
    private Project theProject = null;
    // updated July 2012
    private final JTabbedPane reportTableTabbedPane;
    private JLayeredPane myConcordiaGraphPanel;
//    private JFXPanel concordiaGraphPanelIsoplot;
    private JLayeredPane myPlotAnyPanel;
    private JLayeredPane myUseriesIsochronPanel;
    private JLayeredPane myEvolutionPlotPanel;
    private JLayeredPane myAgeDelta234PlotPanel;

    /**
     *
     */
    protected JPanel myWeightedMeanGraphPanel;
    /**
     *
     */
    private JLayeredPane myNormedProbabilityPanel;
//    private SelectionListener fractionTableListener;
    /**
     *
     */
    protected JLayeredPane fractionCorner;
    // flags Mac OS
    boolean MAC_OS_X = false;
    // lab data editor ==> to be sure only one copy exists
    private DialogEditor myLabDataEditor = null;
    private DialogEditor myAboutBox = null;
    /**
     *
     */
    protected JLayeredPane announcementPane = null;
    private final Timer liveUpdateTimer;

    private DialogEditor myProjectManager;
    //oct 2014
    private DialogEditor sampleDateInterpDialog;
    private DialogEditor myFractionEditor;
    private DialogEditor geochronProjectExportManager;
    // April 2016 temporary
    private TopsoilEvolutionPlot topsoilEvolutionChart;

    /**
     * Creates new form UPbReduxFrame
     *
     * @param myState
     * @param myLabData
     * @param reduxFile
     * @throws BadLabDataException
     */
    public ETReduxFrame(
            ReduxPersistentState myState,
            File reduxFile)
            throws BadLabDataException {

        this.myState = myState;

        /* Set the Metal look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Metal is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Metal".equals(info.getName())) { //Nimbus (original), Motif, Metal
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
        }
        //</editor-fold>

        initComponents();

        announcementPane = new AnnouncementPane();

        // TODO: refactor these things into another init method
        // load resources
        cldr = this.getClass().getClassLoader();

        // produce application icon
        // http://www.centerkey.com/mac/java/
        imageURL = cldr.getResource("org/earthtime/images/uth-pb-redux-logo.png");
        reduxIcon = new CustomIcon(imageURL);
        ((CustomIcon) reduxIcon).setSize(25, 16);
        setIconImage(reduxIcon.getImage());

//        // persist changeLog
//        changeLogURL = cldr.getResourceAsStream("org/earthtime/UPb_Redux/resources/docs/ChangeLog.html");
//        File localChangeLogFile = new File("ChangeLog.html");
//        try {
//            InputStream in = changeLogURL;
//            // Overwrite the file.
//            OutputStream out = new FileOutputStream(localChangeLogFile);
//
//            while (in.available() > 0) {
//                byte[] buf = new byte[1024];
//                int len;
//                while ((len = in.read(buf)) > 0) {
//                    out.write(buf, 0, len);
//                }
//            }
//            in.close();
//            out.close();
//
//        } catch (IOException iOException) {
//        }
        // July 2017 elided in favor of GitHub
        changeLogMenuItem.setVisible(false);

        // persist credits
        creditsURL = cldr.getResourceAsStream("org/earthtime/UPb_Redux/resources/docs/Credits.html");
        File localCreditsFile = new File("Credits.html");
        try {
            InputStream in = creditsURL;
            // Overwrite the file.
            OutputStream out = new FileOutputStream(localCreditsFile);

            while (in.available() > 0) {
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
            in.close();
            out.close();

        } catch (IOException iOException) {
        }

        /**
         * Set size and Center in user's screen
         */
        setSizeAndCenterFrame(FRAME_WIDTH, 750);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        // customize menus colors
        for (int menu = 0; menu < mainMenuBar.getMenuCount(); menu++) {
            mainMenuBar.getMenu(menu).setBackground(ReduxConstants.myFractionGreenColor);
            for (int i = 0; i < mainMenuBar.getMenu(menu).getMenuComponentCount(); i++) {
                if (mainMenuBar.getMenu(menu).getMenuComponent(i) instanceof JMenu) {
                    JMenu mySubMenu = ((JMenu) mainMenuBar.getMenu(menu).getMenuComponent(i));
                    mySubMenu.setBackground(ReduxConstants.myFractionGreenColor);
                    for (int ii = 0; ii < mySubMenu.getMenuComponentCount(); ii++) {
                        mySubMenu.getMenuComponent(ii).setBackground(ReduxConstants.myFractionGreenColor);
                    }
                } else {
                    mainMenuBar.getMenu(menu).getMenuComponent(i).setBackground(ReduxConstants.myFractionGreenColor);
                }
            }

        }

        // check for MacOS
        String lcOSName = System.getProperty("os.name").toLowerCase();
        MAC_OS_X = lcOSName.startsWith("mac os x");

        if (MAC_OS_X) {
//            new MacOSAboutHandler(this);
        }

        /**
         * Setup theFractionsTable
         */
        // setup the tabulated data
        // July 2012 upograde
        reportTableTabbedPane = new TabbedReportViews(this);
        ((TabbedReportViews) reportTableTabbedPane).initializeTabs();
        reportTableTabbedPane.setSize(calculateTabulatedResultsSize());
        fractionsTabulatedResultsLayeredPane.add(reportTableTabbedPane, JLayeredPane.DEFAULT_LAYER);

        // reduxfile may exist if supplied to command line
        if (reduxFile.exists()) {
            openTheSample(reduxFile, true);
        } else {
            setUpEmptySample();
            System.out.println();
        }

        liveUpdateTimer = new Timer(5000, (ActionEvent e) -> {
            liveUpdateSample();
        } //5 sec
        );

        loadLastProject_button.setBackground(Color.WHITE);
        openProject_button.setBackground(Color.WHITE);
        manageProjectRawData_button.setBackground(Color.WHITE);
        loadLastSample_button.setBackground(Color.WHITE);
        openSample_button.setBackground(Color.WHITE);
        saveAndQuit_button.setBackground(Color.WHITE);
        save_button.setBackground(Color.WHITE);
        quit_button.setBackground(Color.WHITE);
        updateData_button.setBackground(Color.WHITE);
        startStopLiveUpdate_button.setBackground(Color.WHITE);
        reduceAll_button.setBackground(Color.WHITE);
        interpretSampleDates_button.setBackground(Color.WHITE);
    }

    private Dimension calculateTabulatedResultsSize() {
        Dimension mySize = fractionsTabulatedResultsLayeredPane.getSize();
        return new Dimension((int) mySize.getWidth() - 3, (int) mySize.getHeight());
    }

    private void changeContentOfTopPanel(ReduxConstants.TOP_PANEL_CONTENTS contents) {

        switch (contents) {
            case WELCOME:
                JButton visitCIRDLESbutton = new ET_JButton("Welcome!  Please Visit us at GitHub.com");// 3/25/2015 updated CIRDLES.org");
                visitCIRDLESbutton.setFont(ReduxConstants.sansSerif_12_Bold);
                visitCIRDLESbutton.setBounds(FRAME_WIDTH / 2 - 175, 130, 350, 25);
                visitCIRDLESbutton.addActionListener(new ActionListenerForGotoCirdles());
                announcementPane.add(visitCIRDLESbutton);
                
                JButton gitubIssuebutton = new ET_JButton("Create an Issue at GitHub.com");
                gitubIssuebutton.setFont(ReduxConstants.sansSerif_12_Bold);
                gitubIssuebutton.setBounds(FRAME_WIDTH / 2 - 175, 160, 350, 25);
                gitubIssuebutton.addActionListener(new ActionListenerForCreateIssueAtGithub());
                announcementPane.add(gitubIssuebutton);

                JTextArea announce = new JTextArea(//
                        "ANNOUNCEMENT:         We are looking forward to a productive 2016. "//
                        + "ET_Redux, for EARTHTIME Redux, will complete LA-CIP MS functionality for Laserchron in preparation for additional isotope systems, such as U-series.  "//
                        + "We are moving the project to a GitHub repository in the near future to make collaboration easier and more transparent.  "
                        + "We are seeking a new logo for ET_Redux and invite your submissions.");
                announce.setFont(ReduxConstants.sansSerif_12_Bold);
                announce.setBounds(FRAME_WIDTH / 2 - 412, 170, 825, 65);
                announce.setLineWrap(true);
                announce.setWrapStyleWord(true);
                announce.setAlignmentX(CENTER_ALIGNMENT);
                announce.setAlignmentY(CENTER_ALIGNMENT);
                announce.setOpaque(true);
                //announce.setBorder(new LineBorder(Color.black));
                announce.setBackground(announcementPane.getBackground());
//                announcementPane.add(announce);

                JButton visitTOPSOIL = new ET_JButton("Checkout Topsoil - the Isoplot replacement project that you can join at github.com/CIRDLES/topsoil");
                visitTOPSOIL.setFont(ReduxConstants.sansSerif_12_Bold);
                visitTOPSOIL.setBounds(FRAME_WIDTH / 2 - 325, 575, 650, 25);//(475, 140, 650, 25);
                visitTOPSOIL.addActionListener((ActionEvent e) -> {
                    BrowserControl.displayURL("https://github.com/CIRDLES/topsoil");
                });
                announcementPane.add(visitTOPSOIL);

                JButton releaseNotes = new ET_JButton("Click to see Latest Release Notes.");
                releaseNotes.setFont(ReduxConstants.sansSerif_10_Bold);
                releaseNotes.setBounds(475, 500, 210, 20);//(150, 180, 220, 20);
                releaseNotes.addActionListener((ActionEvent e) -> {
                    BrowserControl.displayURL("https://docs.google.com/document/d/18CDAMmabkGiNTBiVkqSaNlW8OOXSr4CHGa5TFg4gKwg/edit");
                });

                theFractionTableScrollPane.setViewportView(announcementPane);
                break;
            case WAITING:
                ((AnnouncementPane) announcementPane).initializeAnnouncementPane("Waiting");
                theFractionTableScrollPane.setViewportView(announcementPane);
                break;
            case FRACTIONS:
//                theFractionTableScrollPane.setViewportView(theFractionTable);
        }

        theFractionTableScrollPane.repaint();

    }

    private static class ActionListenerForGotoCirdles implements ActionListener {

        public ActionListenerForGotoCirdles() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            BrowserControl.displayURL("https://github.com/CIRDLES/ET_Redux");// 25 Mar 2015 updated https://cirdles.org");
        }
    }
    
    private static class ActionListenerForCreateIssueAtGithub implements ActionListener {

        public ActionListenerForCreateIssueAtGithub() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            contributeIssueOnGitHubAction();
        }
    }

    // serialize to persistent state
    /**
     *
     * @return
     */
    public boolean askAndSaveReduxPersistentState() {
        // retval signals that user agreed to save changes if any
        boolean retval = checkSavedStatusTheSample();
        if (retval) {
            quickSaveReduxPersistentState();
        }
        return retval;
    }

    /**
     *
     */
    public void quickSaveReduxPersistentState() {
        try {
            ETSerializer.SerializeObjectToFile(myState, ReduxPersistentState.getMySerializedName());
        } catch (ETException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }

        try {
            ETSerializer.SerializeObjectToFile(ReduxLabData.getInstance(), ReduxLabData.getMySerializedName());
        } catch (ETException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
    }

    private void editLabDataUPb(int selectedTab)
            throws BadLabDataException {
        if (myLabDataEditor instanceof LabDataEditorDialogUTh) {
            myLabDataEditor.close();
            LabDataEditorDialogUTh.amOpen = false;
        }

        if (!LabDataEditorDialogUPb.amOpen) {
            myLabDataEditor
                    = new LabDataEditorDialogUPb(this, false, ReduxLabData.getInstance(), selectedTab);
            ((LabDataEditorDialogUPb) myLabDataEditor).setSize();
            myLabDataEditor.setVisible(true);
        } else {
            ((LabDataEditorDialogUPb) myLabDataEditor).setDetailsPaneTab(selectedTab);
        }
    }

    private void editLabDataUTh(int selectedTab)
            throws BadLabDataException {

        if (myLabDataEditor == null) {
            LabDataEditorDialogUPb.amOpen = false;
        } else if (myLabDataEditor instanceof LabDataEditorDialogUPb) {
            myLabDataEditor.close();
            LabDataEditorDialogUPb.amOpen = false;
        }

        if (!LabDataEditorDialogUTh.amOpen) {
            myLabDataEditor
                    = new LabDataEditorDialogUTh(this, false, ReduxLabData.getInstance(), selectedTab);
            ((LabDataEditorDialogUTh) myLabDataEditor).setSize();
            myLabDataEditor.setVisible(true);
        } else {
            ((LabDataEditorDialogUTh) myLabDataEditor).setDetailsPaneTab(selectedTab);
        }
    }

    private void sampleMenuOnTrueProjectMenuOnFalse(boolean sampleTrueProjectFalse) {

        saveAndQuit_button.setEnabled(true);
        save_button.setEnabled(true);

        boolean dateIntepretationsVisible = false;
        try {
            dateIntepretationsVisible = sampleDateInterpDialog.isVisible();
        } catch (Exception e) {
        }
        interpretSampleDates_button.setEnabled(!dateIntepretationsVisible);//   true);

        boolean isTripolized = false;
        if (theProject != null) {
            SampleInterface superSample = theProject.getSuperSample();
            if (superSample != null) {
                isTripolized = SampleInterface.isAnalysisTypeTripolized(superSample.getSampleAnalysisType());
            }
        }

        // project open
        manageProject_menuItem.setEnabled(!sampleTrueProjectFalse);// && isTripolized);
        manageRawData_menuItem.setEnabled(!sampleTrueProjectFalse && isTripolized);
        saveProjectFile_menuItem.setEnabled(!sampleTrueProjectFalse);
        saveProjectFileAs_menuItem.setEnabled(!sampleTrueProjectFalse);
        closeProjectFile_menuItem.setEnabled(!sampleTrueProjectFalse);
        exportProjectSamples.setEnabled(!sampleTrueProjectFalse);
        exportProjectSamplesToGeochron.setEnabled(!sampleTrueProjectFalse);

        // sample open
        manageSampleModel_menuItem.setEnabled(sampleTrueProjectFalse);
        customizeSampleMetadata_menuItem.setEnabled(sampleTrueProjectFalse);

        saveSampleFile.setEnabled(sampleTrueProjectFalse);
        saveSampleFileAs.setEnabled(sampleTrueProjectFalse);
        closeSampleFile.setEnabled(sampleTrueProjectFalse);
        openSampleFile.setEnabled(true);//sampleTrueProjectFalse);
        selectAllFractions_menuItem.setEnabled(sampleTrueProjectFalse);
        deSelectAllFractions_menuItem.setEnabled(sampleTrueProjectFalse);
    }

    /**
     *
     */
    public void forceCloseOfSampleDateInterpretations() {
        try {
            ((SampleDateInterpretationsManager) sampleDateInterpDialog).forceClose();
        } catch (Exception e) {
        }

        try {
            sampleDateInterpDialog.dispose();
        } catch (Exception e) {
        }

    }
    // ********** start **************** P R O J E C T S ******************************    

    private void openProjectFile() {
        String dialogTitle = "Select a Redux Project file to OPEN: *.redux";
        final String fileExtension = ".redux";
        FileFilter nonMacFileFilter = new ReduxFileFilter();

        File returnFile
                = FileHelper.AllPlatformGetFile(//
                        dialogTitle, //
                        myState.getMRUProjectFile(), //
                        fileExtension, nonMacFileFilter, false, this)[0];

        if (returnFile != null) {
            openTheProject(returnFile);
        } else {
            forceCloseOfSampleDateInterpretations();
        }
    }

    private void openTheProject(File projectReduxFile) {

        closeProjectAndOrSample();

        theProject = null;
        System.gc();

        EarthTimeSerializedFileInterface deserializedFile
                = (EarthTimeSerializedFileInterface) ETSerializer.GetSerializedObjectFromFile(projectReduxFile.getPath());

        if (deserializedFile instanceof SampleInterface) {
            System.out.println("Trying to open a SAMPLE .redux file as a Project.");
        } else if (!(deserializedFile instanceof ProjectInterface)) {
            System.out.println("Trying to open an invalid .redux file.");
        } else {
            // project
            stopLiveUpdate();

            myState.setMRUProjectFile(projectReduxFile);
            theProject = (Project) deserializedFile;

            // update the project so it knows where it is
            theProject.setLocationOfProjectReduxFile(projectReduxFile);

            // update MRU status
            myState.updateMRUProjectList(projectReduxFile);

            // jan 2014 
            // set masking shade to that of tripolisession
            try {
                // does not apply to all projects - especially legacy
                MaskingSingleton.setInstance(theProject.getTripoliSession().getMaskingSingleton());
            } catch (Exception e) {
            }
            // decide what to do based on presence of compiled super sample
            if (theProject.getSuperSample() != null) {
                // oct 2014 first check for updated reportsettings
                theProject.getSuperSample().setReportSettingsModel(//
                        ReportSettings.getReportSettingsModelUpdatedToLatestVersion(theProject.getSuperSample().getReportSettingsModel()));
                theProject.getSuperSample().setLegacyStatusForReportTable();

                // July 2017
                ReportAliquotFractionsView.showAliquotBars = !theProject.getSuperSample().isAnalysisTypeUSERIES();

                theProject.saveTheProjectAsSerializedReduxFile();

                // go straight to data table display
                myProjectManager = null;
                initializeProject(false);

            } else { // instantiate project manager so processing can be initialited

                if (theProject.getSampleAnalysisType().compareTo(SampleAnalysisTypesEnum.LAICPMS) == 0) {
                    myProjectManager
                            = new ProjectManagerFor_LAICPMS_FromRawData(this, true, myState, theProject);
                } else if (theProject.getSampleAnalysisType().compareTo(SampleAnalysisTypesEnum.SHRIMP) == 0) {
                    myProjectManager
                            = new ProjectManagerFor_SHRIMP_FromRawData(this, true, myState, theProject);
                }
                myProjectManager.initDialogContent();
                myProjectManager.setVisible(true);
            }

            sampleMenuOnTrueProjectMenuOnFalse(false);
        }
    }

    private boolean checkSavedStatusTheProject() {
        boolean retval = true;
        try {
            if (theProject.isChanged()) {
                // Modal dialog with OK/cancel and a text field
                int response = JOptionPane.showConfirmDialog(this,
                        new String[]{"The current Project " + theProject.getProjectName() + " has been changed.",
                            "Do you want to Save it before proceeding ?"
                        },
                        "ET Redux Warning",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                switch (response) {
                    case JOptionPane.YES_OPTION:
                        try {
                            saveTheProject();
                        } catch (BadLabDataException ex) {
                            new ETWarningDialog(ex).setVisible(true);
                            retval = false;
                            throw new RuntimeException();
                        }
                        break;
                    case JOptionPane.NO_OPTION:
                        theProject.setChanged(false);
                        break;
                    case JOptionPane.CLOSED_OPTION:
                        retval = false;
                }
            }
        } catch (RuntimeException e) {
            // do nothing
        }
        return retval;
    }

    private void saveTheProject() throws BadLabDataException {
        if (theProject != null) {
            myState.getReduxPreferences().setDefaultSampleAnalysisPurpose(theProject.getAnalysisPurpose());
            theProject.saveTheProjectAsSerializedReduxFile();
            setUpTheProject(false, false);
        }
    }

    /**
     *
     * @return @throws BadLabDataException
     */
    private boolean saveProjectFileAs() throws BadLabDataException {

        File selectedFile = theProject.saveProjectFileAs();

        if (selectedFile != null) {
            myState.updateMRUProjectList(selectedFile);
            setUpTheProject(false, false);

            saveProjectFile_menuItem.setEnabled(false);

            saveProjectFileAs_menuItem.setEnabled(true);

            closeProjectFile_menuItem.setEnabled(true);

            try {
                myState.setMRUProjectFolderPath(selectedFile.getParentFile().getCanonicalPath());
            } catch (IOException iOException) {
            }
        }

        return selectedFile != null;
    }

    /**
     *
     * @param inLiveMode the value of inLiveMode
     */
    public void initializeProject(boolean inLiveMode) {
        theSample = theProject.getSuperSample();
        theSample.setChanged(false);

        setUpTheProject(false, inLiveMode);
    }

    /**
     *
     */
    public void updateProjectDisplayTitleBar() {
        try {
            this.setTitle(
                    "ET Redux  " //
                    + theSample.getSampleAnalysisType()//
                    + "  Project:   "//
                    + theProject.getProjectName() //
                    + " <" + theProject.getLocationOfProjectReduxFile().getAbsolutePath() + ">");
        } catch (Exception e) {
        }
    }

    /**
     *
     * @param performReduction
     * @param inLiveMode the value of inLiveMode
     */
    public void setUpTheProject(boolean performReduction, boolean inLiveMode) {
        SampleInterface.registerSampleWithLabData(theSample);

        SampleInterface superSample = theProject.getSuperSample();

        if (superSample != null) {
            if (SampleInterface.isAnalysisTypeTripolized(superSample.getSampleAnalysisType())) {
                // oct 2012 register mineralstandardmodel
                AbstractRatiosDataModel primaryMineralStandard = theProject.getTripoliSession().getPrimaryMineralStandard();
                if (primaryMineralStandard != null) {
                    try {
                        ReduxLabData.getInstance().registerMineralStandardModel(primaryMineralStandard, false);
                        if (((MineralStandardUPbModel) primaryMineralStandard).hasInitialPb()) {
                            ReduxLabData.getInstance().registerInitialPbModel(((MineralStandardUPbModel) primaryMineralStandard).getInitialPbModelET(), false);
                        }
                    } catch (BadLabDataException badLabDataException) {
                    }
                }
            }

            try {
                superSample.setLegacyStatusForReportTable();
            } catch (Exception e) {
            }

        }

        updateProjectDisplayTitleBar();

        rebuildFractionDisplays(performReduction, inLiveMode);

        // set up concordia for use on fraction details window
        //as well as interpret date window and archiving
        myConcordiaGraphPanel = new ConcordiaGraphPanel(theSample, this);
        UPbFractionEditorDialog.setConcordiaGraphPanel(myConcordiaGraphPanel);

        // set up plotAny2Panel for use on fraction details window
        //as well as interpret date window and archiving
        myPlotAnyPanel = new PlotAny2Panel(theSample, this);
        myUseriesIsochronPanel = new IsochronsPanel(theSample, this);

        // set up probabilitydensity for archiving
        myNormedProbabilityPanel = new DateProbabilityDensityPanel(theSample);

        startStopLiveUpdate_button.setEnabled(false);
        manageProjectRawData_button.setEnabled(true);

        updateData_button.setEnabled(false);

        changeContentOfTopPanel(ReduxConstants.TOP_PANEL_CONTENTS.FRACTIONS);

        sampleMenuOnTrueProjectMenuOnFalse(false);

        buildAliquotsMenu();

        customizeReduxSkin();
    }

    private void manageTheProject() {
        //TODO: polymorphic solution to project manager invocation
        if (myProjectManager == null) {
            String isotopeSystem = theProject.getSuperSample().getIsotopeSystem();
            if (isotopeSystem.equalsIgnoreCase("UTh")) {
                myProjectManager
                        = new ProjectOfLegacySamplesDataManagerUseries_Carb(
                                this,
                                true,
                                theProject,
                                myState.getMRUImportFolderCompilationMode());

                ((AbstractProjectOfLegacySamplesDataManagerDialog) myProjectManager).setSize();

                // remembers last folder used for import of single or set of fractions
                myState.setMRUImportFolderCompilationMode(
                        ((AbstractProjectOfLegacySamplesDataManagerDialog) myProjectManager).getImportFractionFolderMRU().toString());

                if (!theProject.getProjectSamples().isEmpty()) {
                    setUpTheProject(false, false);
                    try {
                        saveTheProject();
                    } catch (BadLabDataException ex) {
                        new ETWarningDialog(ex).setVisible(true);
                    }
                }
            } else if (theProject.getSampleAnalysisType().compareTo(SampleAnalysisTypesEnum.LAICPMS) == 0) {
                myProjectManager
                        = new ProjectManagerFor_LAICPMS_FromRawData(this, true, myState, theProject);
            } else if (theProject.getSampleAnalysisType().compareTo(SampleAnalysisTypesEnum.SHRIMP) == 0) {
                myProjectManager
                        = new ProjectManagerFor_SHRIMP_FromRawData(this, true, myState, theProject);
            }
            myProjectManager.initDialogContent();
        }

        myProjectManager.setVisible(true);

        updateReportTable();

        saveProject();
    }

    private void manageRawDataSession() {
        if (theProject.getSampleAnalysisType().compareTo(SampleAnalysisTypesEnum.LAICPMS) == 0) {
            if ((myProjectManager == null) || !(myProjectManager instanceof ProjectManagerFor_LAICPMS_FromRawData)) {
                myProjectManager
                        = new ProjectManagerFor_LAICPMS_FromRawData(this, true, myState, theProject);
                myProjectManager.initDialogContent();
            }
        }
        if (theProject.getSampleAnalysisType().compareTo(SampleAnalysisTypesEnum.SHRIMP) == 0) {
            if ((myProjectManager == null) || !(myProjectManager instanceof ProjectManagerFor_SHRIMP_FromRawData)) {
                myProjectManager
                        = new ProjectManagerFor_SHRIMP_FromRawData(this, true, myState, theProject);
                myProjectManager.initDialogContent();
            }
        }

        ((ProjectManagerSubscribeInterface) myProjectManager).initializeSessionManager(false, true, false);
    }

    private void setUpNewTripolizedProject(SampleAnalysisTypesEnum sampleAnalysisType) {
        theProject = new Project(myState);
        theProject.setSampleAnalysisType(sampleAnalysisType);

        if (sampleAnalysisType.compareTo(SampleAnalysisTypesEnum.LAICPMS) == 0) {
            myProjectManager
                    = new ProjectManagerFor_LAICPMS_FromRawData(this, true, myState, theProject);
        } else if (sampleAnalysisType.compareTo(SampleAnalysisTypesEnum.SHRIMP) == 0) {
            myProjectManager
                    = new ProjectManagerFor_SHRIMP_FromRawData(this, true, myState, theProject);
        }

        // modal call
        myProjectManager.initDialogContent();
        myProjectManager.setVisible(true);

        if (!theProject.getProjectSamples().isEmpty()) {
            setUpTheProject(false, false);
            try {
                saveTheProject();
            } catch (BadLabDataException ex) {
                new ETWarningDialog(ex).setVisible(true);
            }
        } else {
            theProject = null;
        }

    }

    /**
     *
     * @param sampleAnalysisType the value of sampleAnalysisType
     * @param isotopeSystem the value of isotopeSystem
     */
    private void setUpNewCompiledLegacyProject(String sampleAnalysisType, String isotopeSystem, String defaultReportSpecsType) {

        theProject = new Project(myState);

        try {
            theSample
                    = new ProjectSample(//
                            SampleTypesEnum.PROJECT.getName(),
                            SampleTypesEnum.COMPILATION.getName(),
                            sampleAnalysisType,
                            myState.getReduxPreferences().getDefaultSampleAnalysisPurpose(),//
                            true,
                            isotopeSystem,
                            defaultReportSpecsType);
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }

        //set flag for whether analysis was performed elsewhere and we just have legacy results
        theSample.setAnalyzed(true);

        theProject.setSuperSample(theSample);

        if (sampleAnalysisType.equalsIgnoreCase(SampleAnalysisTypesEnum.GENERIC_UPB.getName())) {
            myProjectManager
                    = new ProjectOfLegacySamplesDataManagerDialogForGenericUPb_A(
                            this,
                            true,
                            theProject,
                            myState.getMRUImportFolderCompilationMode());
        } else if (sampleAnalysisType.equalsIgnoreCase(SampleAnalysisTypesEnum.GENERIC_GA_UPB.getName())) {
            myProjectManager
                    = new ProjectOfLegacySamplesDataManagerDialogForGenericUPb_GA(
                            this,
                            true,
                            theProject,
                            myState.getMRUImportFolderCompilationMode());
        } else if (sampleAnalysisType.equalsIgnoreCase(SampleAnalysisTypesEnum.GENERIC_SQUID3_UPB.getName())) {
            myProjectManager
                    = new ProjectOfSamplesDataManagerDialogForGenericUPb_Squid3(
                            this,
                            true,
                            theProject,
                            myState.getMRUImportFolderCompilationMode());
        } else if (sampleAnalysisType.equalsIgnoreCase(SampleAnalysisTypesEnum.LASS.getName())) {
            myProjectManager
                    = new ProjectOfLegacySamplesDataManagerDialogForUCSB_LASS_A(
                            this,
                            true,
                            theProject,
                            myState.getMRUImportFolderCompilationMode());
        } else if (sampleAnalysisType.equalsIgnoreCase(SampleAnalysisTypesEnum.USERIES_CARB.getName())) {
            myProjectManager
                    = new ProjectOfLegacySamplesDataManagerUseries_Carb(
                            this,
                            true,
                            theProject,
                            myState.getMRUImportFolderCompilationMode());
        } else if (sampleAnalysisType.equalsIgnoreCase(SampleAnalysisTypesEnum.USERIES_IGN.getName())) {
            myProjectManager
                    = new ProjectOfLegacySamplesDataManagerUseries_Ign(
                            this,
                            true,
                            theProject,
                            myState.getMRUImportFolderCompilationMode());
        }

        ((AbstractProjectOfLegacySamplesDataManagerDialog) myProjectManager).setSize();

        // modal call
        myProjectManager.setVisible(true);

        // remembers last folder used for import of single or set of fractions
        myState.setMRUImportFolderCompilationMode(
                ((AbstractProjectOfLegacySamplesDataManagerDialog) myProjectManager).getImportFractionFolderMRU().toString());

        if (!theProject.getProjectSamples().isEmpty()) {
            setUpTheProject(false, false);
            try {
                saveTheProject();
            } catch (BadLabDataException ex) {
                new ETWarningDialog(ex).setVisible(true);
            }
        }
    }

    private void saveProject() {

        theProject.saveTheProjectAsSerializedReduxFile();
        myState.setMRUProjectFile(theProject.getLocationOfProjectReduxFile());
    }

    private void exportProjectSamples() {

        try {
            theProject.exportProjectSamples();
        } catch (ETException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
    }

    private void exportProjectSamplesToGeochron() {

        theProject.prepareSamplesForExport();

        // launch manager
        geochronProjectExportManager
                = GeochronProjectExportManager.getInstance(null, false, theProject, myState);

        geochronProjectExportManager.setVisible(true);
    }

    // ********* end  ***************** P R O J E C T S ******************************
    private boolean checkSavedStatusTheSample() {
        boolean retval = true;
        try {
            if (theSample.isChanged()) {
                // Modal dialog with OK/cancel and a text field
                int response = JOptionPane.showConfirmDialog(this,
                        new String[]{"The current Sample " + theSample.getSampleName() + " has been changed.",
                            "Do you want to Save it before proceeding ?"
                        },
                        "ET Redux Warning",
                        JOptionPane.YES_NO_OPTION,//.YES_NO_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                switch (response) {
                    case JOptionPane.YES_OPTION:
                        saveTheSampleOrProject();
                    case JOptionPane.NO_OPTION:
                        theSample.setChanged(false);
                        break;
                    case JOptionPane.CLOSED_OPTION:
                        retval = false;
                }
            }
        } catch (Exception e) {
            // do nothing
        }
        return retval;
    }

    private void openSampleFile() throws BadLabDataException {
        String dialogTitle = "Select an ET Redux Sample file to OPEN: *.redux";
        final String fileExtension = ".redux";
        FileFilter nonMacFileFilter = new ReduxFileFilter();

        File returnFile
                = FileHelper.AllPlatformGetFile(//
                        dialogTitle, //
                        new File(myState.getMRUSampleFolderPath()), //
                        fileExtension, nonMacFileFilter, false, this)[0];

        if (returnFile != null) {
            openTheSample(returnFile, true);
        } else {
            forceCloseOfSampleDateInterpretations();
        }
    }

    /**
     *
     * @param sampleType
     * @param sampleAnalysisType the value of sampleAnalysisType
     * @param isotopeSystem the value of isotopeSystem
     * @throws BadLabDataException
     */
    private void setUpNewSample(String sampleType, String sampleAnalysisType, String isotopeSystem, String defaultReportSpecsType)
            throws BadLabDataException, ETException {
        // check to see if existing sample has been saved
        // returns false if user cancels, etc.

        stopLiveUpdate();

        if (checkSavedStatusTheSample()) {
            // good to proceed
            // save off the existing sample
            SampleInterface saveTheSample = theSample;

            // set up a new empty sample based on sampleType
            theSample
                    = new Sample("NONE", sampleType, sampleAnalysisType, myState.getReduxPreferences().getDefaultSampleAnalysisPurpose(), isotopeSystem, defaultReportSpecsType);
            SampleInterface.specializeNewSample(theSample);

            // manageTheSample sets up the correct form and returns whether it was successful
            // meantime, the form is opened modally to process user setting up sample data
            boolean sampleSetupInitialized = manageTheSample();

            if (sampleSetupInitialized) {
                // rewrite sept 2010
                // for type analysis, manager has already done a file save as
                boolean resultOfSaveSampleFileAs = true;
                if (!theSample.isSampleTypeAnalysis()) {
                    resultOfSaveSampleFileAs = saveSampleFileAs();
                }
                if (theSample.isSampleTypeAnalysis() || resultOfSaveSampleFileAs) {
                    setUpTheSample(!theSample.isAnalyzed());
                } else {
                    // revert
                    setTheSample(saveTheSample);
                }

            } else {
                // revert
                setTheSample(saveTheSample);
            }

        }

    }

    /**
     *
     * @return
     */
    private boolean manageTheSample() throws ETException {
        DialogEditor mySampleManager = null;

        if (theSample.isSampleTypeProject()) {
            // JULY 2012 start migration to project metaphor where project = super sample

            theProject = new Project();
            // july 2017
            theProject.setSampleAnalysisType(SampleAnalysisTypesEnum.valueOf(theSample.getSampleAnalysisType()));

            theProject.setSuperSample(theSample);
            if (theSample.getSampleAnalysisType().equalsIgnoreCase(SampleAnalysisTypesEnum.COMPILED.getName())) {
                mySampleManager
                        = new ProjectOfLegacySamplesDataManagerDialogForGenericUPb_A(
                                this,
                                true,
                                theProject,
                                myState.getMRUImportFolderLegacyMode());
            }

            ((AbstractProjectOfLegacySamplesDataManagerDialog) mySampleManager).setSize();

            // modal call to manager
            mySampleManager.setVisible(true);

            // remembers last folder used for import of single or set of fractions
            myState.setMRUImportFolderCompilationMode(
                    ((AbstractProjectOfLegacySamplesDataManagerDialog) mySampleManager).getImportFractionFolderMRU().toString());

            return ((AbstractProjectOfLegacySamplesDataManagerDialog) mySampleManager).isInitialized();

        } else if (theSample.isSampleTypeCompilation()) {//  getSampleType().equalsIgnoreCase(SampleTypesEnum.COMPILATION.getName())) {

            mySampleManager
                    = new SampleCompilationManagerDialog(
                            myState.getReduxPreferences(),
                            true,
                            theSample,
                            myState.getMRUImportFolderCompilationMode());

            ((SampleCompilationManagerDialog) mySampleManager).setSize();
            //JDialog.setDefaultLookAndFeelDecorated( true );
            // modal call to manager
            mySampleManager.setVisible(true);

            // remembers last folder used for import of single or set of fractions
            myState.setMRUImportFolderCompilationMode(
                    ((SampleCompilationManagerDialog) mySampleManager).getImportFractionFolderMRU().toString());

            return ((SampleCompilationManagerDialog) mySampleManager).isInitialized();

        } else if (((theSample.getSampleType().equalsIgnoreCase(SampleTypesEnum.SAMPLEFOLDER.getName()))
                ||//
                (theSample.isSampleTypeLiveWorkflow())
                ||//
                (theSample.isSampleTypeAnalysis()))
                &&//
                theSample.isAnalysisTypeIDTIMS()) {

            mySampleManager
                    = new SampleAnalysisWorkflowManagerIDTIMS(
                            this,
                            true,
                            theSample,
                            myState.getMRUSampleFolder(),
                            myState.getMRUSampleMetaDataFolder(),
                            myState.getMRUImportedXMLFractionsFolder());

            ((SampleAnalysisWorkflowManagerInterface) mySampleManager).setSize();
//            JDialog.setDefaultLookAndFeelDecorated(true);
            // modal call to manager
            mySampleManager.setVisible(true);

            // remembers last folder used for SampleMetaData used by C-Sharp Tripoli even if dialog not completed
            try {
                myState.setMRUSampleMetaDataFolder(
                        ((SampleAnalysisWorkflowManagerInterface) mySampleManager).getSampleMetaDataFolder().toString());
            } catch (Exception e) {
            }

            if (((SampleAnalysisWorkflowManagerInterface) mySampleManager).isInitialized()) {
                // remembers last folder used for creating sample folder for live update
                try {
                    myState.setMRUSampleFolder(
                            ((SampleAnalysisWorkflowManagerInterface) mySampleManager).getSampleFolder().toString());
                } catch (Exception e) {
                }

                // remembers last folder used for import of single or set of fractions
                try {
                    myState.setMRUImportedXMLFractionsFolder(
                            ((SampleAnalysisWorkflowManagerInterface) mySampleManager).getImportedXMLFractionsFolder().toString());
                } catch (Exception e) {
                }

                // remembers last folder used for Java Tripoli raw data files
                try {
                    myState.setMRUTripoliRawDataFolder(
                            ((SampleAnalysisWorkflowManagerInterface) mySampleManager).getTripoliRawDataFolder().toString());
                } catch (Exception e) {
                }
            }

            return ((SampleAnalysisWorkflowManagerInterface) mySampleManager).isInitialized();

        } else if (((theSample.getSampleType().equalsIgnoreCase(SampleTypesEnum.SAMPLEFOLDER.getName()))
                ||//
                (theSample.isSampleTypeLiveWorkflow())
                ||//
                (theSample.isSampleTypeAnalysis()))
                &&//
                theSample.isAnalysisTypeLAICPMS()) {

            mySampleManager
                    = new SampleManagerDialogForTripolizedLAICPMS(
                            this,
                            true,
                            theSample,
                            myState.getMRUImportFolderLegacyMode());

//            JDialog.setDefaultLookAndFeelDecorated(true);
            ((AbstractSampleFromProjectManagerDialog) mySampleManager).setSize();
            // modal call to manager
            mySampleManager.setVisible(true);

            return ((AbstractSampleFromProjectManagerDialog) mySampleManager).isInitialized();

        } else {
            // LEGACY **********************
            if (theSample.getSampleAnalysisType().equalsIgnoreCase(SampleAnalysisTypesEnum.IDTIMS.getName())) {
                mySampleManager
                        = new SampleLegacyDataManagerDialogForIDTIMS_MIT(
                                this,
                                true,
                                theSample,
                                myState.getMRUImportFolderLegacyMode());
            } else if (theSample.getSampleAnalysisType().equalsIgnoreCase("LAICPMS_MCUA")//
                    || // the old data from UArizona
                    theSample.getSampleAnalysisType().equalsIgnoreCase(SampleAnalysisTypesEnum.LAICPMS.getName())) {
                mySampleManager
                        = new SampleLegacyDataManagerDialogForLAICPMS_MC_UA(
                                this,
                                true,
                                theSample,
                                myState.getMRUImportFolderLegacyMode());
            } else if (theSample.getSampleAnalysisType().equalsIgnoreCase("LAICPMS_SCWSU_vV")//
                    || // Victor Valencia
                    theSample.getSampleAnalysisType().equalsIgnoreCase(SampleAnalysisTypesEnum.LAICPMS.getName())) {
                mySampleManager
                        = new SampleLegacyDataManagerDialogForLAICPMS_MC_UA_VV(
                                this,
                                true,
                                theSample,
                                myState.getMRUImportFolderLegacyMode());
            } else if (theSample.getSampleAnalysisType().equalsIgnoreCase("LAICPMS_SCWSU_vB")) {
                mySampleManager
                        = new SampleLegacyDataManagerDialogForLAICPMS_SC_WSU_vB(
                                this,
                                true,
                                theSample,
                                myState.getMRUImportFolderLegacyMode());
            } else if (theSample.getSampleAnalysisType().equalsIgnoreCase("LAICPMS_SCWSU_vA")) {
                mySampleManager
                        = new SampleLegacyDataManagerDialogForLAICPMS_SC_WSU_vA(
                                this,
                                true,
                                theSample,
                                myState.getMRUImportFolderLegacyMode());
            } else if (theSample.getSampleAnalysisType().equalsIgnoreCase("LAICPMS_SCWSU")) {// backward compatible
                mySampleManager
                        = new SampleLegacyDataManagerDialogForLAICPMS_SC_WSU_vA(
                                this,
                                true,
                                theSample,
                                myState.getMRUImportFolderLegacyMode());
            } else if (theSample.getSampleAnalysisType().equalsIgnoreCase("LAICPMS_NIGL")) {
                mySampleManager
                        = new SampleLegacyDataManagerDialogForLAICPMS_NIGL(
                                this,
                                true,
                                theSample,
                                myState.getMRUImportFolderLegacyMode());
            } else if (theSample.getSampleAnalysisType().equalsIgnoreCase("LAICPMS_UH")) {
                mySampleManager
                        = new SampleLegacyDataManagerDialogForLAICPMS_UH(
                                this,
                                true,
                                theSample,
                                myState.getMRUImportFolderLegacyMode());
            } else if (theSample.getSampleAnalysisType().equalsIgnoreCase("LASS")) {
                mySampleManager
                        = new SampleLegacyDataManagerDialogForLASS(
                                this,
                                true,
                                theSample,
                                myState.getMRUImportFolderLegacyMode());
            }

//            JDialog.setDefaultLookAndFeelDecorated(true);
            if (mySampleManager == null) {
                return false;
            } else {
                ((AbstractSampleLegacyManagerDialog) mySampleManager).setSize();
                // modal call to manager
                mySampleManager.setVisible(true);

                try {
                    // remembers last folder used for import of single or set of fractions
                    myState.setMRUImportFolderLegacyMode(
                            ((AbstractSampleLegacyManagerDialog) mySampleManager).getImportFractionFolderMRU().toString());
                } catch (Exception e) {
                }
                return ((AbstractSampleLegacyManagerDialog) mySampleManager).isInitialized();
            }
        }

    }

    private void saveTheSampleOrProject() throws BadLabDataException {
        if (theSample != null) {
            if (theSample.isSampleTypeProject()) {
                saveTheProject();
            } else {
                saveTheSample();
            }
        }
    }

    /**
     *
     * @throws BadLabDataException
     */
    private void saveTheSample() throws BadLabDataException {
        if (theSample != null) {
            myState.getReduxPreferences().setDefaultSampleAnalysisPurpose(theSample.getAnalysisPurpose());
            SampleInterface.saveSampleAsSerializedReduxFile(theSample);
            setUpTheSample(false);
        }
    }

    /**
     *
     * @return @throws BadLabDataException
     */
    private boolean saveSampleFileAs() throws BadLabDataException {

        File selectedFile = SampleInterface.saveSampleFileAs(theSample, myState.getMRUSampleFolderPath());

        if (selectedFile != null) {
            myState.updateMRUSampleList(selectedFile);
            setUpTheSample(false);

            saveSampleFileAs.setEnabled(true);

            closeSampleFile.setEnabled(true);

            // handle LIVEUPDATEMODE because it contains no data yet
            if (theSample.getSampleType().equalsIgnoreCase(SampleTypesEnum.ANALYSIS.getName())) {
                try {
                    myState.setMRUSampleFolder(selectedFile.getParentFile().getCanonicalPath());
                } catch (IOException iOException) {
                }
            }

        }
        return selectedFile != null;
    }

    /**
     *
     * @param performReduction
     * @throws BadLabDataException
     */
    @Override
    public synchronized void setUpTheSample(boolean performReduction) {

        theSample.setUpSample();

        String sampleUpdateType = "Manual Data Entry";
        if (theSample.isSampleTypeLiveWorkflow()) {
            sampleUpdateType = "Live Workflow";
        } else if (theSample.isAutomaticDataUpdateMode()) {
            sampleUpdateType = "Auto Update";
        }

        this.setTitle(
                "[" + sampleUpdateType + "] for "
                + "ET Redux" //
                + "  Sample:   "//
                + theSample.getSampleName() //
                + " { " //
                + theSample.getReduxSampleFileName() //
                + " } using "//
                + theSample.getSampleAnalysisType()//
                + " data");

        // initialize sample with current user preferences
        theSample.setFractionDataOverriddenOnImport(
                myState.getReduxPreferences().isFractionDataOverriddenOnImport());

        // this true causes data reduction
        // may 2012 first pass reduction
        if (performReduction) {
            theSample.reduceSampleData(false);
        }

        // feb 2010 added legacyData field to force display when no reduction happening
        theSample.setLegacyStatusForReportTable();

        rebuildFractionDisplays(performReduction, false);

        // set up concordia for use on fraction details window
        //as well as interpret date window and archiving
        myConcordiaGraphPanel = new ConcordiaGraphPanel(theSample, this);
        UPbFractionEditorDialog.setConcordiaGraphPanel(getMyConcordiaGraphPanel());

        // set up plotAny2Panel for use on fraction details window
        //as well as interpret date window and archiving
        myPlotAnyPanel = new PlotAny2Panel(theSample, this);
        myUseriesIsochronPanel = new IsochronsPanel(theSample, this);

        // march 2014
        // set up probabilitydensity for archiving
        myNormedProbabilityPanel = new DateProbabilityDensityPanel(theSample);

        manageProjectRawData_button.setEnabled(false);

        startStopLiveUpdate_button.setEnabled(//
                theSample.isAutomaticDataUpdateMode()
                && //
                (theSample.isSampleTypeAnalysis() || theSample.isSampleTypeLiveWorkflow()));

        updateData_button.setEnabled(//
                theSample.isAutomaticDataUpdateMode()
                && //
                (theSample.isSampleTypeAnalysis() || theSample.isSampleTypeLiveWorkflow()));

        reduceAll_button.setEnabled(theSample.isSampleTypeAnalysis() || theSample.isSampleTypeLiveWorkflow());

        changeContentOfTopPanel(ReduxConstants.TOP_PANEL_CONTENTS.FRACTIONS);

        buildAliquotsMenu();

        sampleMenuOnTrueProjectMenuOnFalse(true);

        customizeReduxSkin();
    }

    private void buildAliquotsMenu() {
        // aug 2010 provide aliquot management menu
        boolean doActivate = (theSample.getActiveAliquots().size() > 0);
        aliquotsMenu.removeAll();
        aliquotsMenu.setEnabled(doActivate);
        fractionsMenu.setEnabled(doActivate);
        selectAllFractions_menuItem.setEnabled(doActivate);
        deSelectAllFractions_menuItem.setEnabled(doActivate);

        theSample.getActiveAliquots().stream().forEach((a) -> {
            JMenuItem menuItem = aliquotsMenu.add(new JMenuItem(a.getAliquotName()));
            menuItem.addActionListener((java.awt.event.ActionEvent evt) -> {
                editAliquotByNumber(((ReduxAliquotInterface) a).getAliquotNumber());
            });
        });

        //forces menu update
        pack();
    }

    private void customizeReduxSkin() {
        // July 2012 first pass at customizing skin; oct 2014
        if (theSample.isSampleTypeProject() || theSample.isSampleTypeLegacy() || theSample.isAnalysisTypeLAICPMS() || theSample.isAnalysisTypeCompiled()) {
            jSplitPane1.setDividerLocation(0);
        } else if (theSample.isAnalysisTypeIDTIMS()) {
            jSplitPane1.setDividerLocation(0);//.4);
        } else {
            // show the welcome page
            jSplitPane1.setDividerLocation(1.0);
        }

        // jan 2016
        if (theSample.isSampleTypeProject() && theSample.isAnalysisTypeCOMPILED()) {
            manageProjectRawData_button.setText("Manage Project");
        } else {
            manageProjectRawData_button.setText("Project Raw Data");
        }

        // reports menu activated
        for (Component m : reportMenu.getMenuComponents()) {
            m.setEnabled(true);
        }

        if (theSample.getFractions().size() > 0) {
            showReportHeadings_menuItem.setVisible(false);

            String isotopeSystem = theSample.getIsotopeSystem();

            reportMenu.setText(isotopeSystem + " Reports");

            if (isotopeSystem.compareToIgnoreCase("UPb") == 0) {
                loadEARTHTIMEDefaultReportSettingsModel_UPb_menuItem.setVisible(true);
                loadEARTHTIMEDefaultReportSettingsModel_UTh_Carb_menuItem.setVisible(false);
                loadEARTHTIMEDefaultReportSettingsModel_UTh_Ign_menuItem.setVisible(false);
            } else {
                loadEARTHTIMEDefaultReportSettingsModel_UPb_menuItem.setVisible(false);
                loadEARTHTIMEDefaultReportSettingsModel_UTh_Carb_menuItem.setVisible(true);
                loadEARTHTIMEDefaultReportSettingsModel_UTh_Ign_menuItem.setVisible(true);
            }
        } else {
            reportMenu.setText("Reports");
            showReportHeadings_menuItem.setVisible(true);
            loadEARTHTIMEDefaultReportSettingsModel_UPb_menuItem.setVisible(true);
            loadEARTHTIMEDefaultReportSettingsModel_UTh_Carb_menuItem.setVisible(true);
            loadEARTHTIMEDefaultReportSettingsModel_UTh_Ign_menuItem.setVisible(true);
        }

    }

    /**
     *
     * @param performReduction
     * @param inLiveMode the value of inLiveMode
     */
    @Override
    public void rebuildFractionDisplays(boolean performReduction, boolean inLiveMode) {
        ((TabbedReportViews) getReportTableTabbedPane()).setSample(theSample);

        updateReportTable(performReduction, inLiveMode, "");
    }

    /**
     *
     */
    @Override
    public void updateReportTable() {
        updateReportTable(false, false, "");
    }

    /**
     *
     * @param performReduction
     * @param inLiveMode the value of inLiveMode
     * @param fractionIdToFocus the value of fractionIdToFocus
     */
    @Override
    public void updateReportTable(boolean performReduction, boolean inLiveMode, String fractionIdToFocus) {
        // march 2013
        try {
            UPbFractionReducer.getInstance().setSessionCorrectedUnknownsSummaries(//
                    theProject.getTripoliSession().getSessionCorrectedUnknownsSummaries());
        } catch (Exception e) {
        }

        if (performReduction) {
            theSample.reduceSampleData(inLiveMode);
        }

        loadAndShowReportTableData(fractionIdToFocus);

        // oct 2014
        // this statement makes a difference if user went back to project manager and changed things
        if (sampleDateInterpDialog != null) {
            ((SampleDateInterpretationsManager) sampleDateInterpDialog).setSample(theSample);
        }

        if ((theSample.getSampleAnalysisType().length() == 0)
                || theSample.isAnalysisTypeUSERIES()) {
            // oct 2018 problem of time sink involving setselectedrow of tree
        } else {
            try {
                ((SampleDateInterpretationsManager) sampleDateInterpDialog).refreshSampleDateInterpretations(false, inLiveMode);
            } catch (Exception e) {
            }
        }
    }

    /**
     *
     * @param fractionIdToFocus the value of fractionIdToFocus
     */
    @Override
    public void loadAndShowReportTableData(String fractionIdToFocus) {
        ((TabbedReportViews) getReportTableTabbedPane()).prepareTabs();
    }

    /**
     *
     */
    @Override
    public void refreshReportTableData() {
        ((TabbedReportViews) getReportTableTabbedPane()).refreshTabs();
    }

    private void openTheSample(File selFile, boolean checkSavedStatus) {

        closeProjectAndOrSample();

        if (checkSavedStatus) {
            checkSavedStatusTheSample();
        }

        setLiveUpdateTimerIsRunning(false);

        EarthTimeSerializedFileInterface deserializedFile
                = (EarthTimeSerializedFileInterface) ETSerializer.GetSerializedObjectFromFile(selFile.getPath());

        // TODO: Oct 2011 check for Project file?? or is it automatically a project file
        // created here for a single sample??
        if (deserializedFile instanceof Project) {
            System.out.println("Opening a project");
        } else {

            SampleInterface deserializedSample = (SampleInterface) deserializedFile;

            if (deserializedSample != null) {

                setTheSample(deserializedSample);

                // dec 2011 = fix the legacy problem of badly numbered aliquots
                // compress them to 1,2,3...
                ((Sample) theSample).repairAliquotNumberingDec2011();

                // update MRU status
                myState.updateMRUSampleList(selFile);

                // oct 2014 first check for updated reportsettings
                theSample.setReportSettingsModel(//
                        ReportSettings.getReportSettingsModelUpdatedToLatestVersion(theSample.getReportSettingsModel()));
                theSample.setLegacyStatusForReportTable();

                // feb 2010 catch case where saved as LIVEWORKFLOW
                if (theSample.isSampleTypeLiveWorkflow()) {
                    theSample.setSampleType(SampleTypesEnum.ANALYSIS.getName());
                }
                setUpTheSample(!theSample.isAnalyzed());

                // march 2009 - handle reflection for automatic update
                // this means if sample is automode, then set its path to where found
                if (deserializedSample.isAutomaticDataUpdateMode()) {
                    deserializedSample.setSampleFolderSaved(selFile.getParentFile());
                    for (AliquotInterface a : deserializedSample.getAliquots()) {
                        ((UPbReduxAliquot) a).setContainingSampleDataFolder(selFile.getParentFile());
                    }
                }

                // sept 2007 to be safe, we now save it back to update any minor changes due to version updates
                theSample.setChanged(false);
                SampleInterface.saveSampleAsSerializedReduxFile(theSample, selFile);
            }
        }

    }

    private void closeProjectAndOrSample() {

        try {
            closeTheSample();
        } catch (BadLabDataException badLabDataException) {
        }
        try {
            closeTheProject();
        } catch (BadLabDataException badLabDataException) {
        }

    }

    private boolean closeTheSample() throws BadLabDataException {

        boolean retval = checkSavedStatusTheSample();

        setLiveUpdateTimerIsRunning(false);

        closeOpenDialogs();

        if (retval) {
            setUpEmptySample();
        }

        return retval;
    }

    private boolean closeTheProject() throws BadLabDataException {

        saveTheProject();
        closeOpenDialogs();

        setUpEmptySample();

        return true;
    }

    private void closeOpenDialogs() {
        GeochronProjectExportManager.removeInstance();

        forceCloseOfSampleDateInterpretations();
    }

    private void setUpEmptySample() //
            throws BadLabDataException {
        theSample
                = new Sample("NONE", "NONE", "", myState.getReduxPreferences().getDefaultSampleAnalysisPurpose(), "UPb", "UPb");
        SampleInterface.specializeNewSample(theSample);

        setUpTheSample(false);

        // editSample menu de-activated
        manageSampleModel_menuItem.setEnabled(false);
        customizeSampleMetadata_menuItem.setEnabled(false);

        // save menuItem de-activated
        saveSampleFile.setEnabled(false);
        // save-as menuItem de-activated
        saveSampleFileAs.setEnabled(false);
        // closeFile menuItem de-activated
        closeSampleFile.setEnabled(false);

        theSample.setChanged(false);

        changeContentOfTopPanel(ReduxConstants.TOP_PANEL_CONTENTS.WELCOME);

        saveAndQuit_button.setEnabled(false);
        save_button.setEnabled(false);
        interpretSampleDates_button.setEnabled(false);

    }
//
//    private javax.swing.JTable getTheFractionTable() {
//        return theFractionTable;
//    }
//
//    /**
//     *
//     * @param theFractionTable
//     */
//    public void setTheFractionTable(javax.swing.JTable theFractionTable) {
//        this.theFractionTable = theFractionTable;
//    }

    /**
     * @return the myConcordiaGraphPanel
     */
    public JLayeredPane getMyConcordiaGraphPanel() {
        return myConcordiaGraphPanel;
    }

    /**
     * @return the myNormedProbabilityPanel
     */
    public JLayeredPane getMyNormedProbabilityPanel() {
        return myNormedProbabilityPanel;
    }

    /**
     * @return the reportTableTabbedPane
     */
    public JTabbedPane getReportTableTabbedPane() {
        return reportTableTabbedPane;
    }

    /**
     *
     * @param width
     * @param height
     */
    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);

    }

    /**
     *
     * @param size
     */
    @Override
    public void setSize(Dimension size) {
        setSize(size.width, size.height);
    }

    private void setSizeAndCenterFrame(int width, int height) {
        setSize(width, height);

        //Get the screen size
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();

        //Calculate the frame location
        int x = (screenSize.width - getWidth()) / 2;
        int y = (screenSize.height - getHeight()) / 2;

        //Set the new frame location
        setLocation(x, y);
    }

    /**
     *
     * @return
     */
    public ReduxPersistentState getMyState() {
        return myState;
    }

    /**
     *
     * @param myState
     */
    public void setMyState(ReduxPersistentState myState) {
        this.myState = myState;
    }

    /**
     *
     * @return
     */
    public SampleInterface getTheSample() {
        return theSample;
    }

    /**
     *
     * @param theSample
     */
    public void setTheSample(SampleInterface theSample) {
        this.theSample = theSample;
    }

    /**
     *
     */
    public void showDataDictionary() {
        BrowserControl.displayURL(
                "http://www.earth-time.org/projects/upb/public_docs/DataDictionary.pdf");
    }

    /**
     *
     */
    public void showChangeLog() {

        BrowserControl.displayURL("ChangeLog.html");

    }

    /**
     *
     */
    public void showCredits() {
        BrowserControl.displayURL("Credits.html");
    }

    /**
     *
     * @param isNumeric
     */
    public void produceExcelReport(boolean isNumeric) {
        
        String[][] reportFractions;
        
        int tab = ((TabbedReportViews) getReportTableTabbedPane()).getSelectedIndex();      
        if (tab == 0){
            reportFractions = SampleInterface.reportActiveFractionsByNumberStyle(theSample, isNumeric);
            ReportAliquotFractionsView.sortReportColumn(//
                    reportFractions, //
                    ((TabbedReportViews) getReportTableTabbedPane()).getActiveFractionsSortedColumn(),//
                    ((TabbedReportViews) getReportTableTabbedPane()).getActiveFractionsSortedColumnDirection());
        }else{
            reportFractions = SampleInterface.reportRejectedFractionsByNumberStyle(theSample, isNumeric);
            ReportAliquotFractionsView.sortReportColumn(//
                reportFractions, //
                ((TabbedReportViews) getReportTableTabbedPane()).getRejectedFractionsSortedColumn(),//
                ((TabbedReportViews) getReportTableTabbedPane()).getRejectedFractionsSortedColumnDirection());
        }

        // generate excel report
        ExcelResultsTable.produceExcelResultsTable(//
                theSample.getSampleName(), reportFractions, isNumeric);
    }
    
    public static void contributeIssueOnGitHubAction() {
        String version = "ET_Redux Version: " + ETRedux.VERSION;
        String javaVersion = "Java Version: " + System.getProperties().getProperty("java.version");
        String operatingSystem = "OS: " + System.getProperties().getProperty("os.name") + " " + System.getProperties().getProperty("os.version");

        StringBuilder issueBody = new StringBuilder();
        issueBody.append(urlEncode(version + "\n"));
        issueBody.append(urlEncode(javaVersion + "\n"));
        issueBody.append(urlEncode(operatingSystem + "\n"));
        issueBody.append(urlEncode("\nIssue details:\n"));

        BrowserControl.displayURL("https://github.com/CIRDLES/ET_Redux/issues/new?body=" + issueBody);
    }

    /**
     *
     */
    public void produceCSVReport() {
        String[][] reportFractions;
        
        int tab = ((TabbedReportViews) getReportTableTabbedPane()).getSelectedIndex();      
        if (tab == 0){
            reportFractions = SampleInterface.reportActiveFractionsByNumberStyle(theSample, true);
            ReportAliquotFractionsView.sortReportColumn(//
                    reportFractions, //
                    ((TabbedReportViews) getReportTableTabbedPane()).getActiveFractionsSortedColumn(),//
                    ((TabbedReportViews) getReportTableTabbedPane()).getActiveFractionsSortedColumnDirection());
        }else{
            reportFractions = SampleInterface.reportRejectedFractionsByNumberStyle(theSample, true);
            ReportAliquotFractionsView.sortReportColumn(//
                reportFractions, //
                ((TabbedReportViews) getReportTableTabbedPane()).getRejectedFractionsSortedColumn(),//
                ((TabbedReportViews) getReportTableTabbedPane()).getRejectedFractionsSortedColumnDirection());
        }

        // generate csv report
        CsvResultsTable.produceCSVResultsTable(//
                theSample.getSampleName(), reportFractions);
    }

    /**
     *
     */
    public void producePbcCorrectionReport() {

        // TODO: move to Project class
        if (theProject != null) {
            if (SampleInterface.isAnalysisTypeTripolized(theProject.getSuperSample().getSampleAnalysisType())) {
                // oct 2014 have a LAICPMS project
                File pbcCorrReportFile = new File("LAICPMS_Pbc_Correction_Report_for_" + theProject.getProjectName() + ".txt");
                PrintWriter outputWriter = null;
                try {
                    outputWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(pbcCorrReportFile), StandardCharsets.UTF_8));//new FileWriter(pbcCorrReportFile));
                    for (AliquotInterface activeAliquot : theProject.getSuperSample().getActiveAliquots()) {
                        outputWriter.println("Fractions from " + activeAliquot.getAliquotName() + "\n");
                        outputWriter.println(PbcCorrectionDetails.headerString() + "\n");
                        Vector<ETFractionInterface> fractions = ((UPbReduxAliquot) activeAliquot).getActiveAliquotFractions();
                        for (int i = 0; i < fractions.size(); i++) {
                            ETFractionInterface fraction = fractions.get(i);

                            // static values available after this run
                            PbcCorrectionDetails.zeroAllValues();
                            PbcCorrectionDetails.fraction_ID = fraction.getFractionID();
                            PbcCorrectionDetails.pbcCorrScheme = ((UPbLAICPMSFraction) fraction).getCommonLeadLossCorrectionScheme().getName();
                            UPbFractionReducer.getInstance().fullFractionReduce((FractionI) fraction, true);
                            outputWriter.println(PbcCorrectionDetails.dataString());

                            outputWriter.println();
                            outputWriter.println("***************    SfciTotal   **********************");
                            try {
                                ((UPbLAICPMSFraction) fraction).getSfciTotal().print(outputWriter, new DecimalFormat("0.000000E00"), 15);
                            } catch (Exception e) {
                                outputWriter.println("       NO SfciTotal for this fraction  <><><><><>");
                            }

                            outputWriter.println();
                            outputWriter.println();
                            outputWriter.println("***************    SrAll   **********************");
                            try {
                                ((UPbLAICPMSFraction) fraction).getSrAll().print(outputWriter, new DecimalFormat("0.000000E00"), 15);
                            } catch (Exception e) {
                                outputWriter.println("       NO SrAll for this fraction  <><><><><>");
                            }

                            outputWriter.println();
                            outputWriter.println();
                            outputWriter.println("***************    JPbccs   **********************");
                            try {
                                ((UPbLAICPMSFraction) fraction).getJPbccs().print(outputWriter, new DecimalFormat("0.000000E00"), 15);
                            } catch (Exception e) {
                                outputWriter.println("       NO JPbccs for this fraction  <><><><><>");
                            }

                            outputWriter.println();
                            outputWriter.println();
                            outputWriter.println("***************    SFc204   **********************");
                            try {
                                ((UPbLAICPMSFraction) fraction).getSFc204().print(outputWriter, new DecimalFormat("0.000000E00"), 15);
                            } catch (Exception e) {
                                outputWriter.println("       NO SFc204 for this fraction  <><><><><>");
                            }

                            outputWriter.println();

                        }

                    }

                    outputWriter.println();

                    outputWriter.flush();
                    outputWriter.close();

                } catch (IOException iOException) {
                }

                try {
                    BrowserControl.displayURL(pbcCorrReportFile.getCanonicalPath());
                } catch (IOException iOException) {
                }
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the
     * form.setModel(new javax.swing.table.DefaultTableModel(data, columnNames))
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        manageSampleModel_buttonGroup = new javax.swing.ButtonGroup();
        buttonBar_panel = new javax.swing.JPanel();
        saveAndQuit_button = new ET_JButton();
        loadLastSample_button = new ET_JButton();
        quit_button = new ET_JButton();
        save_button = new ET_JButton();
        interpretSampleDates_button = new ET_JButton();
        reduceAll_button = new ET_JButton();
        startStopLiveUpdate_button = new ET_JButton();
        openSample_button = new ET_JButton();
        updateData_button = new ET_JButton();
        loadLastProject_button = new ET_JButton();
        openProject_button = new ET_JButton();
        manageProjectRawData_button = new ET_JButton();
        jSplitPane1 = new javax.swing.JSplitPane();
        theFractionTableScrollPane = new javax.swing.JScrollPane();
        theFractionTable = new javax.swing.JTable();
        fractionsTabulatedResultsLayeredPane = new javax.swing.JLayeredPane();
        mainMenuBar = new javax.swing.JMenuBar();
        project_menu = new javax.swing.JMenu();
        jMenuItem2 = new javax.swing.JMenuItem();
        newProjectFromRawData_menu = new javax.swing.JMenu();
        newProjectRawDataIDTIMS = new javax.swing.JMenuItem();
        newProjectRawDataLAICPMS = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();
        newProjectFromLegacyDataTable_menu = new javax.swing.JMenu();
        uPbLegacyDataMenu = new javax.swing.JMenu();
        genericUPbDataTableInCSV_menuItem = new javax.swing.JMenuItem();
        genericUPbGADataTableCSV_menuItem = new javax.swing.JMenuItem();
        ucsb_LASS_A_DataTableInCSV_menuItem = new javax.swing.JMenuItem();
        uPbSquid3Data_menu = new javax.swing.JMenu();
        genericUPbSquid3DataTableCSV_menuItem = new javax.swing.JMenuItem();
        uSeriesLegacyData = new javax.swing.JMenu();
        USeriesCarbonate = new javax.swing.JMenuItem();
        USeriesIgneous = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JPopupMenu.Separator();
        manageProject_menuItem = new javax.swing.JMenuItem();
        manageRawData_menuItem = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        exportProjectSamples = new javax.swing.JMenuItem();
        exportProjectSamplesToGeochron = new javax.swing.JMenuItem();
        jSeparator10 = new javax.swing.JPopupMenu.Separator();
        openProjectFile_menuItem = new javax.swing.JMenuItem();
        saveProjectFile_menuItem = new javax.swing.JMenuItem();
        saveProjectFileAs_menuItem = new javax.swing.JMenuItem();
        closeProjectFile_menuItem = new javax.swing.JMenuItem();
        MRUProject_menu = new javax.swing.JMenu();
        jSeparator11 = new javax.swing.JPopupMenu.Separator();
        exit_menuItem = new javax.swing.JMenuItem();
        sampleFileMenu = new javax.swing.JMenu();
        newSampleAnalysisForIDTIMS_menuItem = new javax.swing.JMenuItem();
        newSampleAnalysisForLAICPMS_MC_menuItem = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        newSampleFileFromLegacyAnalysis_Menu = new javax.swing.JMenu();
        ID_TIMSLegacyAnalysis_MIT_menuItem = new javax.swing.JMenuItem();
        LAICPMS_LegacyAnalysis_MC_UA_menuItem = new javax.swing.JMenuItem();
        LAICPMS_LegacyAnalysis_SC_WSU_vA_menuItem = new javax.swing.JMenuItem();
        LAICPMS_LegacyAnalysis_SC_WSU_vB_menuItem = new javax.swing.JMenuItem();
        LAICPMS_LegacyAnalysis_SC_WSU_vV_menuItem = new javax.swing.JMenuItem();
        LAICPMS_LegacyAnalysis_NIGL_menuItem = new javax.swing.JMenuItem();
        LAICPMS_LegacyAnalysis_UH_menuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        newSampleCompilation_menuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        manageSampleModel_menuItem = new javax.swing.JMenuItem();
        customizeSampleMetadata_menuItem = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        openSampleFile = new javax.swing.JMenuItem();
        saveSampleFile = new javax.swing.JMenuItem();
        saveSampleFileAs = new javax.swing.JMenuItem();
        closeSampleFile = new javax.swing.JMenuItem();
        MRUSampleMenu = new javax.swing.JMenu();
        exitSeparator = new javax.swing.JSeparator();
        exitRedux = new javax.swing.JMenuItem();
        aliquotsMenu = new javax.swing.JMenu();
        fractionsMenu = new javax.swing.JMenu();
        selectAllFractions_menuItem = new javax.swing.JMenuItem();
        deSelectAllFractions_menuItem = new javax.swing.JMenuItem();
        labDataMenu = new javax.swing.JMenu();
        labDataUPb = new javax.swing.JMenu();
        editLabTracers = new javax.swing.JMenuItem();
        editLabFractionationModels = new javax.swing.JMenuItem();
        editPbBlanks = new javax.swing.JMenuItem();
        editInitialPbModels = new javax.swing.JMenuItem();
        editPhysicalConstantsModels = new javax.swing.JMenuItem();
        editMineralStandardsModels = new javax.swing.JMenuItem();
        manageRareEarthElementModels = new javax.swing.JMenuItem();
        manageTIMSLabDefaults = new javax.swing.JMenuItem();
        manageLAICPMSLabDefaults = new javax.swing.JMenuItem();
        labDataUTh = new javax.swing.JMenu();
        editPhysicalConstantsModelsUTh = new javax.swing.JMenuItem();
        editDetritalUraniumThoriumModels = new javax.swing.JMenuItem();
        jMenuItem1 = new javax.swing.JMenuItem();
        reportMenu = new javax.swing.JMenu();
        editCurrentReportSettingsModel_menuItem = new javax.swing.JMenuItem();
        showReportHeadings_menuItem = new javax.swing.JMenuItem();
        jSeparator12 = new javax.swing.JPopupMenu.Separator();
        saveCurrentReportSettingsModelAsLocalXMLFile = new javax.swing.JMenuItem();
        loadReportSettingsModelFromLocalXMLFile = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        saveCurrentReportSettingsAsDefault_menuItem = new javax.swing.JMenuItem();
        loadDefaultReportSettingsModel = new javax.swing.JMenuItem();
        jSeparator13 = new javax.swing.JPopupMenu.Separator();
        loadEARTHTIMEDefaultReportSettingsModel_UPb_menuItem = new javax.swing.JMenuItem();
        loadEARTHTIMEDefaultReportSettingsModel_UTh_Carb_menuItem = new javax.swing.JMenuItem();
        loadEARTHTIMEDefaultReportSettingsModel_UTh_Ign_menuItem = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        reportResultsTableAsPDF_menuItem = new javax.swing.JMenuItem();
        reportResultsTableAsStringsInExcel_menuItem = new javax.swing.JMenuItem();
        reportResultsTableAsNumbersInExcel_menuItem = new javax.swing.JMenuItem();
        reportResultsTableAsNumbersInCSV_menuItem = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        reportSettingsHelp = new javax.swing.JMenuItem();
        referencesMenu = new javax.swing.JMenu();
        gCubedPaperSoftwareLAICPMS_menuItem = new javax.swing.JMenuItem();
        gCubedPaperSoftwareTIMS_menuItem = new javax.swing.JMenuItem();
        gCubedPaperReductionEquations_menuItem = new javax.swing.JMenuItem();
        uncertaintyEllipses_menuItem = new javax.swing.JMenuItem();
        jSeparator14 = new javax.swing.JPopupMenu.Separator();
        paperOnUSeriesDataReportingStandards_menuItem = new javax.swing.JMenuItem();
        earthTimeWebSiteMenu = new javax.swing.JMenu();
        visitCIRDLES = new javax.swing.JMenuItem();
        visitEarthTimeOrg = new javax.swing.JMenuItem();
        visitGeochron = new javax.swing.JMenuItem();
        visitGeoSamplesOrg = new javax.swing.JMenuItem();
        visitUseriesRocks = new javax.swing.JMenuItem();
        toolsMenu = new javax.swing.JMenu();
        reduxPreferences = new javax.swing.JMenuItem();
        templatesForLegacyProjects_menu = new javax.swing.JMenu();
        writeCSVFileOfGenericUPbIsotopicLegacyDataSampleFieldNames_A = new javax.swing.JMenuItem();
        writeCSVFileOfProjectLegacyDataSampleFieldNames_UCSB_LASS_A = new javax.swing.JMenuItem();
        templatesForLegacySample_menu = new javax.swing.JMenu();
        writeCSVFileOfIDTIMSLegacyDataSampleFieldNames_MIT = new javax.swing.JMenuItem();
        writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_MC_USA = new javax.swing.JMenuItem();
        writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_SC_WSU_vA = new javax.swing.JMenuItem();
        writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_SC_WSU_vB = new javax.swing.JMenuItem();
        writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_NIGL = new javax.swing.JMenuItem();
        producePbCCorrReport_jMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        helpMenuItem = new javax.swing.JMenuItem();
        changeLogMenuItem = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();
        credits_menuItem = new javax.swing.JMenuItem();
        createGithubIssue = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("EARTHTIME Redux");
        setBackground(new java.awt.Color(237, 242, 250));
        setLocationByPlatform(true);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                ProgramExiting(evt);
            }
        });

        buttonBar_panel.setBackground(new java.awt.Color(235, 255, 255));
        buttonBar_panel.setBorder(javax.swing.BorderFactory.createLineBorder(null));
        buttonBar_panel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        saveAndQuit_button.setBackground(new java.awt.Color(204, 204, 204));
        saveAndQuit_button.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        saveAndQuit_button.setForeground(new java.awt.Color(255, 51, 0));
        saveAndQuit_button.setText("Save & Quit");
        saveAndQuit_button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        saveAndQuit_button.setPreferredSize(new java.awt.Dimension(140, 23));
        saveAndQuit_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAndQuit_buttonActionPerformed(evt);
            }
        });
        buttonBar_panel.add(saveAndQuit_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 6, 90, 25));

        loadLastSample_button.setBackground(new java.awt.Color(204, 204, 204));
        loadLastSample_button.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        loadLastSample_button.setForeground(new java.awt.Color(255, 51, 0));
        loadLastSample_button.setText("Last Sample");
        loadLastSample_button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        loadLastSample_button.setPreferredSize(new java.awt.Dimension(140, 23));
        loadLastSample_button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loadLastSample_buttonMouseEntered(evt);
            }
        });
        loadLastSample_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadLastSample_buttonActionPerformed(evt);
            }
        });
        buttonBar_panel.add(loadLastSample_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(325, 6, 95, 25));

        quit_button.setBackground(new java.awt.Color(204, 204, 204));
        quit_button.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        quit_button.setForeground(new java.awt.Color(255, 51, 0));
        quit_button.setText("Quit");
        quit_button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        quit_button.setPreferredSize(new java.awt.Dimension(140, 23));
        quit_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quit_buttonActionPerformed(evt);
            }
        });
        buttonBar_panel.add(quit_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 6, 50, 25));

        save_button.setBackground(new java.awt.Color(204, 204, 204));
        save_button.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        save_button.setForeground(new java.awt.Color(255, 51, 0));
        save_button.setText("Save");
        save_button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        save_button.setPreferredSize(new java.awt.Dimension(140, 23));
        save_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                save_buttonActionPerformed(evt);
            }
        });
        buttonBar_panel.add(save_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(610, 6, 60, 25));

        interpretSampleDates_button.setBackground(new java.awt.Color(204, 204, 204));
        interpretSampleDates_button.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        interpretSampleDates_button.setForeground(new java.awt.Color(255, 51, 0));
        interpretSampleDates_button.setText("Sample Dates");
        interpretSampleDates_button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        interpretSampleDates_button.setPreferredSize(new java.awt.Dimension(140, 23));
        interpretSampleDates_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                interpretSampleDates_buttonActionPerformed(evt);
            }
        });
        buttonBar_panel.add(interpretSampleDates_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(1055, 6, 105, 25));

        reduceAll_button.setBackground(new java.awt.Color(204, 204, 204));
        reduceAll_button.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        reduceAll_button.setForeground(new java.awt.Color(255, 51, 0));
        reduceAll_button.setText("Reduce All");
        reduceAll_button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        reduceAll_button.setPreferredSize(new java.awt.Dimension(140, 23));
        reduceAll_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reduceAll_buttonActionPerformed(evt);
            }
        });
        buttonBar_panel.add(reduceAll_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(965, 6, 90, 25));

        startStopLiveUpdate_button.setBackground(new java.awt.Color(204, 204, 204));
        startStopLiveUpdate_button.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        startStopLiveUpdate_button.setForeground(new java.awt.Color(255, 51, 0));
        startStopLiveUpdate_button.setText("START Live Workflow");
        startStopLiveUpdate_button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        startStopLiveUpdate_button.setPreferredSize(new java.awt.Dimension(140, 23));
        startStopLiveUpdate_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startStopLiveUpdate_buttonActionPerformed(evt);
            }
        });
        buttonBar_panel.add(startStopLiveUpdate_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(820, 6, 145, 25));

        openSample_button.setBackground(new java.awt.Color(204, 204, 204));
        openSample_button.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        openSample_button.setForeground(new java.awt.Color(255, 51, 0));
        openSample_button.setText("Open Sample");
        openSample_button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        openSample_button.setPreferredSize(new java.awt.Dimension(140, 23));
        openSample_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openSampleFileActionPerformed(evt);
            }
        });
        buttonBar_panel.add(openSample_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 6, 100, 25));

        updateData_button.setBackground(new java.awt.Color(204, 204, 204));
        updateData_button.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        updateData_button.setForeground(new java.awt.Color(255, 51, 0));
        updateData_button.setText("Update Data");
        updateData_button.setToolTipText("<html>Update will re-read every fraction file in the aliquot source folders. <br><br> To update only recently changed files, start ilve workflow.</html>");
        updateData_button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        updateData_button.setPreferredSize(new java.awt.Dimension(140, 23));
        updateData_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateData_buttonActionPerformed(evt);
            }
        });
        buttonBar_panel.add(updateData_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(720, 6, 100, 25));

        loadLastProject_button.setBackground(new java.awt.Color(204, 204, 204));
        loadLastProject_button.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        loadLastProject_button.setForeground(new java.awt.Color(255, 51, 0));
        loadLastProject_button.setText("Last Project");
        loadLastProject_button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        loadLastProject_button.setPreferredSize(new java.awt.Dimension(140, 23));
        loadLastProject_button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loadLastProject_buttonMouseEntered(evt);
            }
        });
        loadLastProject_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadLastProject_buttonActionPerformed(evt);
            }
        });
        buttonBar_panel.add(loadLastProject_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 6, 95, 25));

        openProject_button.setBackground(new java.awt.Color(204, 204, 204));
        openProject_button.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        openProject_button.setForeground(new java.awt.Color(255, 51, 0));
        openProject_button.setText("Open Project");
        openProject_button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        openProject_button.setPreferredSize(new java.awt.Dimension(140, 23));
        openProject_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openProject_buttonopenSampleFileActionPerformed(evt);
            }
        });
        buttonBar_panel.add(openProject_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 6, 95, 25));

        manageProjectRawData_button.setBackground(new java.awt.Color(204, 204, 204));
        manageProjectRawData_button.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        manageProjectRawData_button.setForeground(new java.awt.Color(255, 51, 0));
        manageProjectRawData_button.setText("Project Raw Data");
        manageProjectRawData_button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        manageProjectRawData_button.setPreferredSize(new java.awt.Dimension(140, 23));
        manageProjectRawData_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manageProjectRawData_buttonopenSampleFileActionPerformed(evt);
            }
        });
        buttonBar_panel.add(manageProjectRawData_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(195, 6, 130, 25));

        jSplitPane1.setBackground(new java.awt.Color(0, 102, 102));
        jSplitPane1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jSplitPane1.setDividerLocation(265);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setOneTouchExpandable(true);

        theFractionTableScrollPane.setBorder(javax.swing.BorderFactory.createEtchedBorder(new java.awt.Color(204, 153, 0), new java.awt.Color(204, 255, 255)));
        theFractionTableScrollPane.setViewportBorder(javax.swing.BorderFactory.createEtchedBorder());
        theFractionTableScrollPane.setAutoscrolls(true);

        theFractionTable.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        theFractionTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        theFractionTable.setAutoscrolls(false);
        theFractionTable.setRowSelectionAllowed(false);
        theFractionTable.setSelectionBackground(new java.awt.Color(113, 164, 255));

        // Custom Post-Init Code
        theFractionTable = new UPbFractionTable();
        theFractionTableScrollPane.setViewportView(theFractionTable);

        jSplitPane1.setLeftComponent(theFractionTableScrollPane);

        fractionsTabulatedResultsLayeredPane.setBackground(new java.awt.Color(255, 255, 255));
        fractionsTabulatedResultsLayeredPane.setOpaque(true);
        fractionsTabulatedResultsLayeredPane.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                fractionsTabulatedResultsLayeredPaneComponentResized(evt);
            }
        });
        jSplitPane1.setRightComponent(fractionsTabulatedResultsLayeredPane);

        mainMenuBar.setBackground(new java.awt.Color(212, 231, 232));

        project_menu.setText("Project");
        project_menu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                project_menuMenuSelected(evt);
            }
        });

        jMenuItem2.setText("New Empty Project");
        jMenuItem2.setEnabled(false);
        project_menu.add(jMenuItem2);

        newProjectFromRawData_menu.setText("New Project from Raw Data");

        newProjectRawDataIDTIMS.setText("ID TIMS");
        newProjectRawDataIDTIMS.setEnabled(false);
        newProjectFromRawData_menu.add(newProjectRawDataIDTIMS);

        newProjectRawDataLAICPMS.setText("LA-ICP MS");
        newProjectRawDataLAICPMS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newProjectRawDataLAICPMSActionPerformed(evt);
            }
        });
        newProjectFromRawData_menu.add(newProjectRawDataLAICPMS);

        project_menu.add(newProjectFromRawData_menu);

        jMenuItem4.setText("New Project from Archived Aliquots");
        jMenuItem4.setEnabled(false);
        project_menu.add(jMenuItem4);

        jMenuItem5.setText("New Project from Saved Samples");
        jMenuItem5.setEnabled(false);
        project_menu.add(jMenuItem5);

        jMenuItem9.setText("New Project from Saved Projects");
        jMenuItem9.setEnabled(false);
        project_menu.add(jMenuItem9);

        newProjectFromLegacyDataTable_menu.setText("New Project from Legacy Data Table");

        uPbLegacyDataMenu.setText("UPb Legacy Data");

        genericUPbDataTableInCSV_menuItem.setText("Generic UPb Legacy Data Table in CSV UTF-8 format");
        genericUPbDataTableInCSV_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genericUPbDataTableInCSV_menuItemActionPerformed(evt);
            }
        });
        uPbLegacyDataMenu.add(genericUPbDataTableInCSV_menuItem);

        genericUPbGADataTableCSV_menuItem.setText("Generic UPb GA Legacy Data Table in CSV UTF-8 format");
        genericUPbGADataTableCSV_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genericUPbGADataTableCSV_menuItemActionPerformed(evt);
            }
        });
        uPbLegacyDataMenu.add(genericUPbGADataTableCSV_menuItem);

        ucsb_LASS_A_DataTableInCSV_menuItem.setText("UCSB LASS A Legacy Data Table in CSV UTF-8 format");
        ucsb_LASS_A_DataTableInCSV_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ucsb_LASS_A_DataTableInCSV_menuItemActionPerformed(evt);
            }
        });
        uPbLegacyDataMenu.add(ucsb_LASS_A_DataTableInCSV_menuItem);

        newProjectFromLegacyDataTable_menu.add(uPbLegacyDataMenu);

        uPbSquid3Data_menu.setText("Squid3 UPb Data");

        genericUPbSquid3DataTableCSV_menuItem.setText("Generic UPb Squid3 Data Table in CSV UTF-8 format");
        genericUPbSquid3DataTableCSV_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genericUPbSquid3DataTableCSV_menuItemActionPerformed(evt);
            }
        });
        uPbSquid3Data_menu.add(genericUPbSquid3DataTableCSV_menuItem);

        newProjectFromLegacyDataTable_menu.add(uPbSquid3Data_menu);

        uSeriesLegacyData.setText("U-series Legacy Data");

        USeriesCarbonate.setText("Carbonate Data from Single Source in tab-delimited format");
        USeriesCarbonate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                USeriesCarbonateActionPerformed(evt);
            }
        });
        uSeriesLegacyData.add(USeriesCarbonate);

        USeriesIgneous.setText("Igneous Data from Single Source in tab-delimited format");
        USeriesIgneous.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                USeriesIgneousActionPerformed(evt);
            }
        });
        uSeriesLegacyData.add(USeriesIgneous);

        newProjectFromLegacyDataTable_menu.add(uSeriesLegacyData);

        project_menu.add(newProjectFromLegacyDataTable_menu);
        project_menu.add(jSeparator9);

        manageProject_menuItem.setText("Manage Project");
        manageProject_menuItem.setEnabled(false);
        manageProject_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manageProject_menuItemActionPerformed(evt);
            }
        });
        project_menu.add(manageProject_menuItem);

        manageRawData_menuItem.setText("Manage Raw Data Session");
        manageRawData_menuItem.setEnabled(false);
        manageRawData_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manageRawData_menuItemActionPerformed(evt);
            }
        });
        project_menu.add(manageRawData_menuItem);
        project_menu.add(jSeparator5);

        exportProjectSamples.setText("Export Project Samples to Local Folder");
        exportProjectSamples.setEnabled(false);
        exportProjectSamples.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportProjectSamplesActionPerformed(evt);
            }
        });
        project_menu.add(exportProjectSamples);

        exportProjectSamplesToGeochron.setText("Export Project Samples to Geochron");
        exportProjectSamplesToGeochron.setEnabled(false);
        exportProjectSamplesToGeochron.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportProjectSamplesToGeochronActionPerformed(evt);
            }
        });
        project_menu.add(exportProjectSamplesToGeochron);
        project_menu.add(jSeparator10);

        openProjectFile_menuItem.setText("Open Project File");
        openProjectFile_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openProjectFile_menuItemActionPerformed(evt);
            }
        });
        project_menu.add(openProjectFile_menuItem);

        saveProjectFile_menuItem.setText("Save Project File");
        saveProjectFile_menuItem.setEnabled(false);
        saveProjectFile_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveProjectFile_menuItemActionPerformed(evt);
            }
        });
        project_menu.add(saveProjectFile_menuItem);

        saveProjectFileAs_menuItem.setText("Save Project File As ...");
        saveProjectFileAs_menuItem.setEnabled(false);
        saveProjectFileAs_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveProjectFileAs_menuItemActionPerformed(evt);
            }
        });
        project_menu.add(saveProjectFileAs_menuItem);

        closeProjectFile_menuItem.setText("Close Project File");
        closeProjectFile_menuItem.setEnabled(false);
        closeProjectFile_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeProjectFile_menuItemActionPerformed(evt);
            }
        });
        project_menu.add(closeProjectFile_menuItem);

        MRUProject_menu.setText("Recent Project Files");
        project_menu.add(MRUProject_menu);
        project_menu.add(jSeparator11);

        exit_menuItem.setText("Exit");
        exit_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exit_menuItemActionPerformed(evt);
            }
        });
        project_menu.add(exit_menuItem);

        mainMenuBar.add(project_menu);

        sampleFileMenu.setBackground(new java.awt.Color(212, 231, 232));
        sampleFileMenu.setText("Sample");
        sampleFileMenu.setToolTipText("UPb Redux Sample files are saved as [sample name].redux.");
        sampleFileMenu.setActionCommand("Redux File");
        sampleFileMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
                sampleFileMenuMenuDeselected(evt);
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                sampleFileMenuMenuSelected(evt);
            }
        });
        sampleFileMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sampleFileMenuActionPerformed(evt);
            }
        });

        newSampleAnalysisForIDTIMS_menuItem.setText("New Sample Analysis for ID-TIMS");
        newSampleAnalysisForIDTIMS_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newSampleAnalysisForIDTIMS_menuItemActionPerformed(evt);
            }
        });
        sampleFileMenu.add(newSampleAnalysisForIDTIMS_menuItem);

        newSampleAnalysisForLAICPMS_MC_menuItem.setText("New Sample Analysis for LA-ICP-MS MC (in development)");
        newSampleAnalysisForLAICPMS_MC_menuItem.setEnabled(false);
        newSampleAnalysisForLAICPMS_MC_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newSampleAnalysisForLAICPMS_MC_menuItemActionPerformed(evt);
            }
        });
        sampleFileMenu.add(newSampleAnalysisForLAICPMS_MC_menuItem);
        sampleFileMenu.add(jSeparator7);

        newSampleFileFromLegacyAnalysis_Menu.setText("New Sample File from Legacy Analysis ...");

        ID_TIMSLegacyAnalysis_MIT_menuItem.setText("ID-TIMS Legacy Analysis (MIT)");
        ID_TIMSLegacyAnalysis_MIT_menuItem.setToolTipText("Please use CSV template available under the Tools menu.");
        ID_TIMSLegacyAnalysis_MIT_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ID_TIMSLegacyAnalysis_MIT_menuItemActionPerformed(evt);
            }
        });
        newSampleFileFromLegacyAnalysis_Menu.add(ID_TIMSLegacyAnalysis_MIT_menuItem);

        LAICPMS_LegacyAnalysis_MC_UA_menuItem.setText("LA-ICP MS Legacy Analysis (MC - UA)");
        LAICPMS_LegacyAnalysis_MC_UA_menuItem.setToolTipText("Please use CSV template available under the Tools menu.");
        LAICPMS_LegacyAnalysis_MC_UA_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LAICPMS_LegacyAnalysis_MC_UA_menuItemActionPerformed(evt);
            }
        });
        newSampleFileFromLegacyAnalysis_Menu.add(LAICPMS_LegacyAnalysis_MC_UA_menuItem);

        LAICPMS_LegacyAnalysis_SC_WSU_vA_menuItem.setText("LA-ICP MS Legacy Analysis (SC - WSU vA)");
        LAICPMS_LegacyAnalysis_SC_WSU_vA_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LAICPMS_LegacyAnalysis_SC_WSU_vA_menuItemActionPerformed(evt);
            }
        });
        newSampleFileFromLegacyAnalysis_Menu.add(LAICPMS_LegacyAnalysis_SC_WSU_vA_menuItem);

        LAICPMS_LegacyAnalysis_SC_WSU_vB_menuItem.setText("LA-ICP MS Legacy Analysis (SC - WSU vB)");
        LAICPMS_LegacyAnalysis_SC_WSU_vB_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LAICPMS_LegacyAnalysis_SC_WSU_vB_menuItemActionPerformed(evt);
            }
        });
        newSampleFileFromLegacyAnalysis_Menu.add(LAICPMS_LegacyAnalysis_SC_WSU_vB_menuItem);

        LAICPMS_LegacyAnalysis_SC_WSU_vV_menuItem.setText("LA-ICP MS Legacy Analysis (SC - WSU)_vV");
        LAICPMS_LegacyAnalysis_SC_WSU_vV_menuItem.setToolTipText("Please use CSV template available under the Tools menu.");
        LAICPMS_LegacyAnalysis_SC_WSU_vV_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LAICPMS_LegacyAnalysis_SC_WSU_vV_menuItemActionPerformed(evt);
            }
        });
        newSampleFileFromLegacyAnalysis_Menu.add(LAICPMS_LegacyAnalysis_SC_WSU_vV_menuItem);

        LAICPMS_LegacyAnalysis_NIGL_menuItem.setText("LA-ICP MS Legacy Analysis (NIGL)");
        LAICPMS_LegacyAnalysis_NIGL_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LAICPMS_LegacyAnalysis_NIGL_menuItemActionPerformed(evt);
            }
        });
        newSampleFileFromLegacyAnalysis_Menu.add(LAICPMS_LegacyAnalysis_NIGL_menuItem);

        LAICPMS_LegacyAnalysis_UH_menuItem.setText("LA-ICP MS Legacy Analysis (UH)");
        LAICPMS_LegacyAnalysis_UH_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LAICPMS_LegacyAnalysis_UH_menuItemActionPerformed(evt);
            }
        });
        newSampleFileFromLegacyAnalysis_Menu.add(LAICPMS_LegacyAnalysis_UH_menuItem);

        sampleFileMenu.add(newSampleFileFromLegacyAnalysis_Menu);
        sampleFileMenu.add(jSeparator1);

        newSampleCompilation_menuItem.setText("New Compilation of Aliquots into a \"Super-Sample\" File");
        newSampleCompilation_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newSampleCompilation_menuItemActionPerformed(evt);
            }
        });
        sampleFileMenu.add(newSampleCompilation_menuItem);
        sampleFileMenu.add(jSeparator2);

        manageSampleModel_menuItem.setText("Manage Sample");
        manageSampleModel_menuItem.setActionCommand("Model Name and Default Values");
        manageSampleModel_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manageSampleModel_menuItemActionPerformed(evt);
            }
        });
        sampleFileMenu.add(manageSampleModel_menuItem);

        customizeSampleMetadata_menuItem.setText("Provide Additional Geochron Sample Metadata");
        customizeSampleMetadata_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                customizeSampleMetadata_menuItemActionPerformed(evt);
            }
        });
        sampleFileMenu.add(customizeSampleMetadata_menuItem);
        sampleFileMenu.add(jSeparator3);

        openSampleFile.setBackground(javax.swing.UIManager.getDefaults().getColor("InternalFrame.inactiveTitleForeground"));
        openSampleFile.setText("Open Sample File");
        openSampleFile.setToolTipText("Open a saved UPb Redux sample file of the form [sample name].redux.");
        openSampleFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openSampleFileActionPerformed(evt);
            }
        });
        sampleFileMenu.add(openSampleFile);

        saveSampleFile.setBackground(java.awt.SystemColor.inactiveCaptionText);
        saveSampleFile.setText("Save Sample File");
        saveSampleFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveSampleFileActionPerformed(evt);
            }
        });
        sampleFileMenu.add(saveSampleFile);

        saveSampleFileAs.setBackground(java.awt.SystemColor.inactiveCaptionText);
        saveSampleFileAs.setText("Save Sample File As ...");
        saveSampleFileAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveSampleFileAsActionPerformed(evt);
            }
        });
        sampleFileMenu.add(saveSampleFileAs);

        closeSampleFile.setBackground(java.awt.SystemColor.inactiveCaptionText);
        closeSampleFile.setText("Close Sample File");
        closeSampleFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeSampleFileActionPerformed(evt);
            }
        });
        sampleFileMenu.add(closeSampleFile);

        MRUSampleMenu.setText("Recent Sample Files ...");
        sampleFileMenu.add(MRUSampleMenu);
        sampleFileMenu.add(exitSeparator);

        exitRedux.setBackground(java.awt.SystemColor.inactiveCaptionText);
        exitRedux.setText("Exit");
        exitRedux.setToolTipText("Saves current state and exits UPb Redux.");
        exitRedux.setName(""); // NOI18N
        exitRedux.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitReduxActionPerformed(evt);
            }
        });
        sampleFileMenu.add(exitRedux);

        mainMenuBar.add(sampleFileMenu);

        aliquotsMenu.setText("Aliquots");
        mainMenuBar.add(aliquotsMenu);

        fractionsMenu.setText("Fractions");

        selectAllFractions_menuItem.setText("Select All Fractions");
        selectAllFractions_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAllFractions_menuItemActionPerformed(evt);
            }
        });
        fractionsMenu.add(selectAllFractions_menuItem);

        deSelectAllFractions_menuItem.setText("De-select All Fractions");
        deSelectAllFractions_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deSelectAllFractions_menuItemActionPerformed(evt);
            }
        });
        fractionsMenu.add(deSelectAllFractions_menuItem);

        mainMenuBar.add(fractionsMenu);

        labDataMenu.setText("Lab Data");
        labDataMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
                labDataMenuMenuDeselected(evt);
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
            }
        });

        labDataUPb.setText("UPb");

        editLabTracers.setText("Tracer Models");
        editLabTracers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editLabTracersActionPerformed(evt);
            }
        });
        labDataUPb.add(editLabTracers);

        editLabFractionationModels.setText("Fractionation Models");
        editLabFractionationModels.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editLabFractionationModelsActionPerformed(evt);
            }
        });
        labDataUPb.add(editLabFractionationModels);

        editPbBlanks.setText("Pb Blank IC Models");
        editPbBlanks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editPbBlanksActionPerformed(evt);
            }
        });
        labDataUPb.add(editPbBlanks);

        editInitialPbModels.setText("Initial Pb Models");
        editInitialPbModels.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editInitialPbModelsActionPerformed(evt);
            }
        });
        labDataUPb.add(editInitialPbModels);

        editPhysicalConstantsModels.setText("Physical Constants Models");
        editPhysicalConstantsModels.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editPhysicalConstantsModelsActionPerformed(evt);
            }
        });
        labDataUPb.add(editPhysicalConstantsModels);

        editMineralStandardsModels.setText("Reference Material Models");
        editMineralStandardsModels.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editMineralStandardsModelsActionPerformed(evt);
            }
        });
        labDataUPb.add(editMineralStandardsModels);

        manageRareEarthElementModels.setText("Rare Earth Element Models");
        manageRareEarthElementModels.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manageRareEarthElementModelsActionPerformed(evt);
            }
        });
        labDataUPb.add(manageRareEarthElementModels);

        manageTIMSLabDefaults.setText("Manage TIMS Lab Data Defaults");
        manageTIMSLabDefaults.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manageTIMSLabDefaultsActionPerformed(evt);
            }
        });
        labDataUPb.add(manageTIMSLabDefaults);

        manageLAICPMSLabDefaults.setText("Manage LAICPMS Lab Data Defaults");
        manageLAICPMSLabDefaults.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manageLAICPMSLabDefaultsActionPerformed(evt);
            }
        });
        labDataUPb.add(manageLAICPMSLabDefaults);

        labDataMenu.add(labDataUPb);

        labDataUTh.setText("UTh");

        editPhysicalConstantsModelsUTh.setText("Physical Constants Models");
        editPhysicalConstantsModelsUTh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editPhysicalConstantsModelsUThActionPerformed(evt);
            }
        });
        labDataUTh.add(editPhysicalConstantsModelsUTh);

        editDetritalUraniumThoriumModels.setText("Detrital Thorium Uranium Models");
        editDetritalUraniumThoriumModels.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editDetritalUraniumThoriumModelsActionPerformed(evt);
            }
        });
        labDataUTh.add(editDetritalUraniumThoriumModels);

        jMenuItem1.setText("Sea Water Models");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        labDataUTh.add(jMenuItem1);

        labDataMenu.add(labDataUTh);

        mainMenuBar.add(labDataMenu);

        reportMenu.setText("Reports");
        reportMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
                reportMenuMenuDeselected(evt);
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
            }
        });

        editCurrentReportSettingsModel_menuItem.setText("Report Settings Manager");
        editCurrentReportSettingsModel_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editCurrentReportSettingsModel_menuItemActionPerformed(evt);
            }
        });
        reportMenu.add(editCurrentReportSettingsModel_menuItem);

        showReportHeadings_menuItem.setText("Show / Hide Report Headings");
        showReportHeadings_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showReportHeadings_menuItemActionPerformed(evt);
            }
        });
        reportMenu.add(showReportHeadings_menuItem);
        reportMenu.add(jSeparator12);

        saveCurrentReportSettingsModelAsLocalXMLFile.setText("Save Current Report Settings Model as local XML file");
        saveCurrentReportSettingsModelAsLocalXMLFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveCurrentReportSettingsModelAsLocalXMLFileActionPerformed(evt);
            }
        });
        reportMenu.add(saveCurrentReportSettingsModelAsLocalXMLFile);

        loadReportSettingsModelFromLocalXMLFile.setText("Load Report Settings Model from local XML file");
        loadReportSettingsModelFromLocalXMLFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadReportSettingsModelFromLocalXMLFileActionPerformed(evt);
            }
        });
        reportMenu.add(loadReportSettingsModelFromLocalXMLFile);
        reportMenu.add(jSeparator8);

        saveCurrentReportSettingsAsDefault_menuItem.setText("Set Current Report Settings Model as Default Model");
        saveCurrentReportSettingsAsDefault_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveCurrentReportSettingsAsDefault_menuItemActionPerformed(evt);
            }
        });
        reportMenu.add(saveCurrentReportSettingsAsDefault_menuItem);

        loadDefaultReportSettingsModel.setText("Load Default Report Settings Model");
        loadDefaultReportSettingsModel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadDefaultReportSettingsModelActionPerformed(evt);
            }
        });
        reportMenu.add(loadDefaultReportSettingsModel);
        reportMenu.add(jSeparator13);

        loadEARTHTIMEDefaultReportSettingsModel_UPb_menuItem.setText("Load EARTHTIME Default Report Settings Model UPb");
        loadEARTHTIMEDefaultReportSettingsModel_UPb_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadEARTHTIMEDefaultReportSettingsModel_UPb_menuItemActionPerformed(evt);
            }
        });
        reportMenu.add(loadEARTHTIMEDefaultReportSettingsModel_UPb_menuItem);

        loadEARTHTIMEDefaultReportSettingsModel_UTh_Carb_menuItem.setText("Load EARTHTIME Default Report Settings Model UTh_Carb");
        loadEARTHTIMEDefaultReportSettingsModel_UTh_Carb_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadEARTHTIMEDefaultReportSettingsModel_UTh_Carb_menuItemActionPerformed(evt);
            }
        });
        reportMenu.add(loadEARTHTIMEDefaultReportSettingsModel_UTh_Carb_menuItem);

        loadEARTHTIMEDefaultReportSettingsModel_UTh_Ign_menuItem.setText("Load EARTHTIME Default Report Settings Model UTh_Ign");
        loadEARTHTIMEDefaultReportSettingsModel_UTh_Ign_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadEARTHTIMEDefaultReportSettingsModel_UTh_Ign_menuItemActionPerformed(evt);
            }
        });
        reportMenu.add(loadEARTHTIMEDefaultReportSettingsModel_UTh_Ign_menuItem);
        reportMenu.add(jSeparator4);

        reportResultsTableAsPDF_menuItem.setText("Report Results Table as SVG, PDF");
        reportResultsTableAsPDF_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reportResultsTableAsPDF_menuItemActionPerformed(evt);
            }
        });
        reportMenu.add(reportResultsTableAsPDF_menuItem);

        reportResultsTableAsStringsInExcel_menuItem.setText("Report Results Table as Strings in Excel");
        reportResultsTableAsStringsInExcel_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reportResultsTableAsStringsInExcel_menuItemActionPerformed(evt);
            }
        });
        reportMenu.add(reportResultsTableAsStringsInExcel_menuItem);

        reportResultsTableAsNumbersInExcel_menuItem.setText("Report Results Table as Numbers in Excel");
        reportResultsTableAsNumbersInExcel_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reportResultsTableAsNumbersInExcel_menuItemActionPerformed(evt);
            }
        });
        reportMenu.add(reportResultsTableAsNumbersInExcel_menuItem);

        reportResultsTableAsNumbersInCSV_menuItem.setText("Report Results Table as Numbers in '.csv' format");
        reportResultsTableAsNumbersInCSV_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reportResultsTableAsNumbersInCSV_menuItemActionPerformed(evt);
            }
        });
        reportMenu.add(reportResultsTableAsNumbersInCSV_menuItem);
        reportMenu.add(jSeparator6);

        reportSettingsHelp.setText("Help");
        reportSettingsHelp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reportSettingsHelpActionPerformed(evt);
            }
        });
        reportMenu.add(reportSettingsHelp);

        mainMenuBar.add(reportMenu);

        referencesMenu.setText("References");
        referencesMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
                referencesMenuMenuDeselected(evt);
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
            }
        });

        gCubedPaperSoftwareLAICPMS_menuItem.setText("G-Cubed paper on this software for LA-ICPMS");
        gCubedPaperSoftwareLAICPMS_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gCubedPaperSoftwareLAICPMS_menuItemActionPerformed(evt);
            }
        });
        referencesMenu.add(gCubedPaperSoftwareLAICPMS_menuItem);

        gCubedPaperSoftwareTIMS_menuItem.setText("G-Cubed paper on this software for TIMS");
        gCubedPaperSoftwareTIMS_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gCubedPaperSoftwareTIMS_menuItemActionPerformed(evt);
            }
        });
        referencesMenu.add(gCubedPaperSoftwareTIMS_menuItem);

        gCubedPaperReductionEquations_menuItem.setText("G-Cubed paper on UPb Isotope Dilution Reduction");
        gCubedPaperReductionEquations_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gCubedPaperReductionEquations_menuItemActionPerformed(evt);
            }
        });
        referencesMenu.add(gCubedPaperReductionEquations_menuItem);

        uncertaintyEllipses_menuItem.setText("Uncertainty Ellipses");
        uncertaintyEllipses_menuItem.setEnabled(false);
        uncertaintyEllipses_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uncertaintyEllipses_menuItemActionPerformed(evt);
            }
        });
        referencesMenu.add(uncertaintyEllipses_menuItem);
        referencesMenu.add(jSeparator14);

        paperOnUSeriesDataReportingStandards_menuItem.setText("Paper on U-Series Data Reporting Standards");
        paperOnUSeriesDataReportingStandards_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paperOnUSeriesDataReportingStandards_menuItemActionPerformed(evt);
            }
        });
        referencesMenu.add(paperOnUSeriesDataReportingStandards_menuItem);

        mainMenuBar.add(referencesMenu);

        earthTimeWebSiteMenu.setText("Web Resources");
        earthTimeWebSiteMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
                earthTimeWebSiteMenuMenuDeselected(evt);
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
            }
        });

        visitCIRDLES.setText("CIRDLES.org");
        visitCIRDLES.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                visitCIRDLESActionPerformed(evt);
            }
        });
        earthTimeWebSiteMenu.add(visitCIRDLES);

        visitEarthTimeOrg.setText("Earth-Time.org");
        visitEarthTimeOrg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                visitEarthTimeOrgActionPerformed(evt);
            }
        });
        earthTimeWebSiteMenu.add(visitEarthTimeOrg);

        visitGeochron.setText("Geochron.org");
        visitGeochron.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                visitGeochronActionPerformed(evt);
            }
        });
        earthTimeWebSiteMenu.add(visitGeochron);

        visitGeoSamplesOrg.setText("SESAR (geoSamples.org)");
        visitGeoSamplesOrg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                visitGeoSamplesOrgActionPerformed(evt);
            }
        });
        earthTimeWebSiteMenu.add(visitGeoSamplesOrg);

        visitUseriesRocks.setText("USeries.rocks");
        visitUseriesRocks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                visitUseriesRocksActionPerformed(evt);
            }
        });
        earthTimeWebSiteMenu.add(visitUseriesRocks);

        mainMenuBar.add(earthTimeWebSiteMenu);

        toolsMenu.setText("Tools");

        reduxPreferences.setText("Preferences");
        reduxPreferences.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reduxPreferencesActionPerformed(evt);
            }
        });
        toolsMenu.add(reduxPreferences);

        templatesForLegacyProjects_menu.setText("Templates for Legacy Data for Projects of one or more Samples");

        writeCSVFileOfGenericUPbIsotopicLegacyDataSampleFieldNames_A.setText("Open CSV File of Generic UPb Isotopic Legacy Data Sample Field Names (A)");
        writeCSVFileOfGenericUPbIsotopicLegacyDataSampleFieldNames_A.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                writeCSVFileOfGenericUPbIsotopicLegacyDataSampleFieldNames_AActionPerformed(evt);
            }
        });
        templatesForLegacyProjects_menu.add(writeCSVFileOfGenericUPbIsotopicLegacyDataSampleFieldNames_A);

        writeCSVFileOfProjectLegacyDataSampleFieldNames_UCSB_LASS_A.setText("Open CSV File of UPb Isotopic Legacy Data Sample Field Names UCSB LASS (A)");
        writeCSVFileOfProjectLegacyDataSampleFieldNames_UCSB_LASS_A.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                writeCSVFileOfProjectLegacyDataSampleFieldNames_UCSB_LASS_AActionPerformed(evt);
            }
        });
        templatesForLegacyProjects_menu.add(writeCSVFileOfProjectLegacyDataSampleFieldNames_UCSB_LASS_A);

        toolsMenu.add(templatesForLegacyProjects_menu);

        templatesForLegacySample_menu.setText("Templates for Legacy Data for Single Samples");

        writeCSVFileOfIDTIMSLegacyDataSampleFieldNames_MIT.setText("Open CSV File of ID-TIMS Legacy Data Sample Field Names (MIT)");
        writeCSVFileOfIDTIMSLegacyDataSampleFieldNames_MIT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                writeCSVFileOfIDTIMSLegacyDataSampleFieldNames_MITActionPerformed(evt);
            }
        });
        templatesForLegacySample_menu.add(writeCSVFileOfIDTIMSLegacyDataSampleFieldNames_MIT);

        writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_MC_USA.setText("Open CSV File of LA-ICP MS Legacy Data Sample Field Names (MC - UA)");
        writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_MC_USA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_MC_USAActionPerformed(evt);
            }
        });
        templatesForLegacySample_menu.add(writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_MC_USA);

        writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_SC_WSU_vA.setText("Open CSV File of LA-ICP MS Legacy Data Sample Field Names (SC - WSU) vA");
        writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_SC_WSU_vA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_SC_WSU_vAActionPerformed(evt);
            }
        });
        templatesForLegacySample_menu.add(writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_SC_WSU_vA);

        writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_SC_WSU_vB.setText("Open CSV File of LA-ICP MS Legacy Data Sample Field Names (SC - WSU) vB");
        writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_SC_WSU_vB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_SC_WSU_vBActionPerformed(evt);
            }
        });
        templatesForLegacySample_menu.add(writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_SC_WSU_vB);

        writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_NIGL.setText("Open CSV File of LA-ICP MS Legacy Data Sample Field Names (NIGL)");
        writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_NIGL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_NIGLActionPerformed(evt);
            }
        });
        templatesForLegacySample_menu.add(writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_NIGL);

        toolsMenu.add(templatesForLegacySample_menu);

        producePbCCorrReport_jMenuItem.setText("Produce Pbc Correction Report");
        producePbCCorrReport_jMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                producePbCCorrReport_jMenuItemActionPerformed(evt);
            }
        });
        toolsMenu.add(producePbCCorrReport_jMenuItem);

        mainMenuBar.add(toolsMenu);

        helpMenu.setText("Help");
        helpMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
                helpMenuMenuDeselected(evt);
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
            }
        });
        helpMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                helpMenuMouseClicked(evt);
            }
        });

        helpMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        helpMenuItem.setText("Help from CIRDLES.org");
        helpMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(helpMenuItem);

        changeLogMenuItem.setText("Release Notes");
        changeLogMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changeLogMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(changeLogMenuItem);

        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutMenuItem);

        credits_menuItem.setText("Credits");
        credits_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                credits_menuItemActionPerformed(evt);
            }
        });
        helpMenu.add(credits_menuItem);

        createGithubIssue.setText("Create Github Issue");
        createGithubIssue.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createGithubIssueActionPerformed(evt);
            }
        });
        helpMenu.add(createGithubIssue);

        mainMenuBar.add(helpMenu);

        setJMenuBar(mainMenuBar);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, buttonBar_panel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jSplitPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 576, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(buttonBar_panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void save_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_save_buttonActionPerformed
        try {
            saveTheSampleOrProject();
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }

        quickSaveReduxPersistentState();
    }//GEN-LAST:event_save_buttonActionPerformed

    private void quit_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quit_buttonActionPerformed
        quickSaveReduxPersistentState();
        System.exit(0);
    }//GEN-LAST:event_quit_buttonActionPerformed

    private void loadLastSample_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadLastSample_buttonActionPerformed
        loadMostRecentSample(true);
    }//GEN-LAST:event_loadLastSample_buttonActionPerformed

    private void saveAndQuit_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAndQuit_buttonActionPerformed
        try {
            saveTheSampleOrProject();
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }

        quickSaveReduxPersistentState();
        System.exit(0);
    }//GEN-LAST:event_saveAndQuit_buttonActionPerformed

    private void ProgramExiting(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_ProgramExiting
        quickSaveReduxPersistentState();
        // if (askAndSaveReduxPersistentState()) {
        System.exit(0);
        //}
    }//GEN-LAST:event_ProgramExiting

    private void visitGeoSamplesOrgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_visitGeoSamplesOrgActionPerformed
        BrowserControl.displayURL("http://www.geosamples.org/");
    }//GEN-LAST:event_visitGeoSamplesOrgActionPerformed

    private void mruSampleMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        // open the MRU sample file
        if (checkSavedStatusTheSample()) {
            openTheSample(new File(((AbstractButton) evt.getSource()).getText()), true);
        }
    }

    private void editLabTracersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editLabTracersActionPerformed
        try {
            editLabDataUPb(0);
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
}//GEN-LAST:event_editLabTracersActionPerformed

    private void sampleFileMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sampleFileMenuActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_sampleFileMenuActionPerformed

    /**
     *
     * @param checkSavedStatus
     */
    public void loadMostRecentProject(boolean checkSavedStatus) {
        ArrayList<String> myMRUs = myState.getMRUProjectList();
        if (!myMRUs.isEmpty()) {
            openTheProject(new File(myMRUs.get(0)));
        }
    }

    /**
     *
     * @param checkSavedStatus
     */
    public void loadMostRecentSample(boolean checkSavedStatus) {
        ArrayList<String> myMRUs = myState.getMRUSampleList();
        if (!myMRUs.isEmpty()) {
            openTheSample(new File(myMRUs.get(0)), checkSavedStatus);
        }
    }

    /**
     *
     */
    public void setupMRUSampleFiles() {

        // september 2009
        ArrayList<String> myMRUs = myState.getMRUSampleList();

        ArrayList<JMenuItem> MRUMenuItemList = new ArrayList<>();

        // validate File mappings and set up menu items
        for (int i = 0; i
                < myMRUs.size(); i++) {
            File f = new File(myMRUs.get(i));
            if (!f.isFile()) {
                myMRUs.set(i, "");
            } else {
                try {
                    MRUMenuItemList.add(new JMenuItem(f.getCanonicalPath()));
                } catch (IOException iOException) {
                }
            }
        }

        //reset menu
        MRUSampleMenu.removeAll();

        // add valid items to menu
        MRUMenuItemList.stream().map((jmi) -> {
            jmi.addActionListener((java.awt.event.ActionEvent evt) -> {
                mruSampleMenuItemActionPerformed(evt);
            });
            return jmi;
        }).forEach((jmi) -> {
            MRUSampleMenu.add(jmi);
        });

        if (MRUSampleMenu.getItemCount() == 0) {
            MRUSampleMenu.add(new JMenuItem("<empty>"));
            MRUSampleMenu.getItem(0).setEnabled(false);
        }

    }

    private void sampleFileMenuMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_sampleFileMenuMenuSelected
        // set state of saveSampleFile menuItem to theSample's changed flag
        saveSampleFile.setEnabled(theSample.isChanged() && (theProject == null));
        //save_button.setEnabled(theSample.isChanged());
        //saveAndQuit_button.setEnabled(theSample.isChanged());

        setupMRUSampleFiles();

    }//GEN-LAST:event_sampleFileMenuMenuSelected

    //**************************************PROJECT MENUS **********************
//    private ArrayList<String>
    /**
     *
     */
    public void setupMRUProjectFiles() {

        // october 2012
        ArrayList<String> myMRUs = myState.getMRUProjectList();

        ArrayList<JMenuItem> MRUMenuItemList = new ArrayList<>();

        // validate File mappings and set up menu items
        for (int i = 0; i < myMRUs.size(); i++) {
            File f = new File(myMRUs.get(i));
            if (!f.isFile()) {
                myMRUs.set(i, "");
            } else {
                try {
                    MRUMenuItemList.add(new JMenuItem(f.getCanonicalPath()));
                } catch (IOException iOException) {
                }
            }
        }

        //reset menu
        MRUProject_menu.removeAll();

        // add valid items to menu
        MRUMenuItemList.stream().map((jmi) -> {
            jmi.addActionListener((java.awt.event.ActionEvent evt) -> {
                mruProjectMenuItemActionPerformed(evt);
            });
            return jmi;
        }).forEach((jmi) -> {
            MRUProject_menu.add(jmi);
        });

        if (MRUProject_menu.getItemCount() == 0) {
            MRUProject_menu.add(new JMenuItem("<empty>"));
            MRUProject_menu.getItem(0).setEnabled(false);
        }
    }

    private void mruProjectMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        openTheProject(new File(((JMenuItem) evt.getSource()).getText()));
    }

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        try {
            AboutBoxDisplay();
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
    }//GEN-LAST:event_aboutMenuItemActionPerformed

    private void AboutBoxDisplay()
            throws BadLabDataException {
        if (!AboutBox.amOpen) {
            myAboutBox = new AboutBox(this, true);
//            JDialog.setDefaultLookAndFeelDecorated(true);
            myAboutBox.setVisible(true);
        } else {
        }

    }

    private void manageSampleModel_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manageSampleModel_menuItemActionPerformed
        setLiveUpdateTimerIsRunning(false);

        // feb 2010 simplification
        // first save the existing sample
        SampleInterface.saveSampleAsSerializedReduxFile(theSample);

        // open the manager
        try {
            manageTheSample();
        } catch (ETException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }

        // setup sample and views with no datareduction flag = true
        setUpTheSample(!theSample.isAnalyzed());

}//GEN-LAST:event_manageSampleModel_menuItemActionPerformed

    private void saveSampleFileAsActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveSampleFileAsActionPerformed
        try {
            saveSampleFileAs();
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
    }//GEN-LAST:event_saveSampleFileAsActionPerformed

    private void closeSampleFileActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeSampleFileActionPerformed
        try {
            closeTheSample();
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
    }//GEN-LAST:event_closeSampleFileActionPerformed

    private void saveSampleFileActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveSampleFileActionPerformed
        try {
            saveTheSample();
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
    }//GEN-LAST:event_saveSampleFileActionPerformed

    private void helpMenuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_helpMenuMouseClicked
}//GEN-LAST:event_helpMenuMouseClicked

    private void visitEarthTimeOrgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_visitEarthTimeOrgActionPerformed
        BrowserControl.displayURL("http://www.earth-time.org");
    }//GEN-LAST:event_visitEarthTimeOrgActionPerformed

    private void exitReduxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitReduxActionPerformed
        if (askAndSaveReduxPersistentState()) {
            System.exit(0);
        }
    }//GEN-LAST:event_exitReduxActionPerformed

    private void openSampleFileActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openSampleFileActionPerformed
        if (checkSavedStatusTheSample()) {
            try {
                openSampleFile();
            } catch (BadLabDataException ex) {
                new ETWarningDialog(ex).setVisible(true);
            }
        }
    }//GEN-LAST:event_openSampleFileActionPerformed

    private void editLabFractionationModelsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editLabFractionationModelsActionPerformed
        try {
            editLabDataUPb(1);
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
    }//GEN-LAST:event_editLabFractionationModelsActionPerformed

    private void editPbBlanksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editPbBlanksActionPerformed
        try {
            editLabDataUPb(2);
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
}//GEN-LAST:event_editPbBlanksActionPerformed

    private void editInitialPbModelsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editInitialPbModelsActionPerformed
        try {
            editLabDataUPb(3);
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
    }//GEN-LAST:event_editInitialPbModelsActionPerformed

    private void editPhysicalConstantsModelsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editPhysicalConstantsModelsActionPerformed
        try {
            editLabDataUPb(4);
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
    }//GEN-LAST:event_editPhysicalConstantsModelsActionPerformed

    private void manageTIMSLabDefaultsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manageTIMSLabDefaultsActionPerformed
        try {
            editLabDataUPb(7);
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
    }//GEN-LAST:event_manageTIMSLabDefaultsActionPerformed

    private void editMineralStandardsModelsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editMineralStandardsModelsActionPerformed
        try {
            editLabDataUPb(5);
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
}//GEN-LAST:event_editMineralStandardsModelsActionPerformed

    private void labDataMenuMenuDeselected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_labDataMenuMenuDeselected
//        this.getTheFractionTable().repaint();
    }//GEN-LAST:event_labDataMenuMenuDeselected

    private void sampleFileMenuMenuDeselected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_sampleFileMenuMenuDeselected
//        this.getTheFractionTable().repaint();
    }//GEN-LAST:event_sampleFileMenuMenuDeselected

    private void reportMenuMenuDeselected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_reportMenuMenuDeselected
//        this.getTheFractionTable().repaint();
    }//GEN-LAST:event_reportMenuMenuDeselected

    private void referencesMenuMenuDeselected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_referencesMenuMenuDeselected
//        this.getTheFractionTable().repaint();
    }//GEN-LAST:event_referencesMenuMenuDeselected

    private void earthTimeWebSiteMenuMenuDeselected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_earthTimeWebSiteMenuMenuDeselected
//        this.getTheFractionTable().repaint();
    }//GEN-LAST:event_earthTimeWebSiteMenuMenuDeselected

    private void helpMenuMenuDeselected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_helpMenuMenuDeselected
//        this.getTheFractionTable().repaint();
    }//GEN-LAST:event_helpMenuMenuDeselected

    private void interpretSampleDates_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_interpretSampleDates_buttonActionPerformed
        // Dec 2015 experiment with customization of skins
//        if (theSample.getSampleAnalysisType().compareToIgnoreCase(SampleAnalysisTypesEnum.USERIES_CARB.getName()) == 0) {
////            if (topsoilEvolutionChart != null) {
////                topsoilEvolutionChart.close();
////                topsoilEvolutionChart = null;
////            }
////            if (topsoilEvolutionChart == null) {
////                topsoilEvolutionChart = TopsoilEvolutionPlot.getInstance();
////            }
//            topsoilEvolutionChart = new TopsoilEvolutionPlot();
//            topsoilEvolutionChart.setSelectedFractions(theSample.getActiveFractionsSortedByAliquot());
//            topsoilEvolutionChart.preparePanel();
//            topsoilEvolutionChart.showPanel();
//        } else {
        manageSampleDateInterpretation(//
                new SampleTreeAnalysisMode(theSample),
                new SampleTreeCompilationMode(theSample));
//        }
}//GEN-LAST:event_interpretSampleDates_buttonActionPerformed

    /**
     *
     */
    @Override
    public void publishClosingOfSampleDateInterpretation() {
        interpretSampleDates_button.setEnabled(true);
    }

    private void manageSampleDateInterpretation(SampleTreeI dateTreeByAliquot, SampleTreeI dateTreeBySample) {
        if (theSample != null) {

            // oct 2014 per bug report by Matt Rioux email
            if (theSample.isAnalyzed()) {
                SampleInterface.updateAndSaveSampleDateModelsByAliquot(theSample);
            }

            SampleInterface.saveSampleAsSerializedReduxFile(theSample);
            myWeightedMeanGraphPanel
                    = new WeightedMeanGraphPanel(theSample);

            myPlotAnyPanel = new PlotAny2Panel(theSample, this);
            myUseriesIsochronPanel = new IsochronsPanel(theSample, this);

            myEvolutionPlotPanel = new EvolutionPlotPanel(theSample, this);
            myAgeDelta234PlotPanel = new AgeByDelta234UPlotPanel(theSample, this);

            theSample.getSampleDateInterpretationGUISettings().//
                    setConcordiaOptions(((ConcordiaPlotDisplayInterface) myConcordiaGraphPanel).getConcordiaOptions());
            ((ConcordiaGraphPanel) myConcordiaGraphPanel).//
                    setFadedDeselectedFractions(false);
            theSample.setChanged(true);

            interpretSampleDates_button.setEnabled(false);

            ((PlottingDetailsDisplayInterface) myConcordiaGraphPanel).setShowTightToEdges(true);
            ((PlottingDetailsDisplayInterface) myConcordiaGraphPanel).resetPanel(true, false);
            ((PlottingDetailsDisplayInterface) myPlotAnyPanel).setShowTightToEdges(true);
            ((PlottingDetailsDisplayInterface) myPlotAnyPanel).resetPanel(true, false);
            ((PlottingDetailsDisplayInterface) myUseriesIsochronPanel).setShowTightToEdges(true);
            ((PlottingDetailsDisplayInterface) myUseriesIsochronPanel).resetPanel(true, false);

            if (sampleDateInterpDialog != null) {
                sampleDateInterpDialog.dispose();
            }
            sampleDateInterpDialog
                    = new SampleDateInterpretationsManager(
                            this,
                            myConcordiaGraphPanel,
                            myPlotAnyPanel,
                            myUseriesIsochronPanel,
                            myEvolutionPlotPanel,
                            myAgeDelta234PlotPanel,
                            myWeightedMeanGraphPanel,
                            myNormedProbabilityPanel,
                            theSample,
                            dateTreeByAliquot,
                            dateTreeBySample);

            sampleDateInterpDialog.setVisible(true);

        }

    }

    public void updateEvolutionPlot() {
        if (((EvolutionPlotPanel) myEvolutionPlotPanel).isShowMe()) {
            ((EvolutionPlotPanel) myEvolutionPlotPanel).repaint();
        }
    }

    /**
     *
     * @param MRUreportSettingsModelFolder
     * @return
     */
    public String setReportSettingsModelFromXMLFile(String MRUreportSettingsModelFolder) {

        String retVal = MRUreportSettingsModelFolder;
        String dialogTitle = "Select a Report Settings Model xml file to LOAD: *.xml";
        final String fileExtension = ".xml";
        FileFilter nonMacFileFilter = new XMLFileFilter();

        File returnFile
                = FileHelper.AllPlatformGetFile(//
                        dialogTitle, //
                        new File(MRUreportSettingsModelFolder), //
                        fileExtension, nonMacFileFilter, false, this)[0];

        if (returnFile != null) {
            ReportSettingsInterface reportSettings = new ReportSettings();
            try {
                ReportSettingsInterface reportSettingsModel = (ReportSettingsInterface) reportSettings.readXMLObject(returnFile.getAbsolutePath(), true);
                theSample.setReportSettingsModel(reportSettingsModel);
                retVal = returnFile.getParent();
            } catch (FileNotFoundException | ETException | BadOrMissingXMLSchemaException fileNotFoundException) {
            }
        }

        return retVal;
    }

    /**
     * Method to be call when Import MC_ICPMS Excel File is selected
     *
     * @param evt
     */
private void LAICPMS_LegacyAnalysis_MC_UA_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LAICPMS_LegacyAnalysis_MC_UA_menuItemActionPerformed
    try {
        setUpNewSample(SampleTypesEnum.LEGACY.getName(), LAICPMS_MCUA.getName(), LAICPMS_MCUA.getIsotypeSystem(), LAICPMS_MCUA.getDefaultReportSpecsType());
        changeContentOfTopPanel(ReduxConstants.TOP_PANEL_CONTENTS.FRACTIONS);
    } catch (ETException ex) {
        new ETWarningDialog(ex).setVisible(true);
    }

}//GEN-LAST:event_LAICPMS_LegacyAnalysis_MC_UA_menuItemActionPerformed

private void reduceAll_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reduceAll_buttonActionPerformed
    updateReportTable(true, false, "");
}//GEN-LAST:event_reduceAll_buttonActionPerformed

private void reportResultsTableAsStringsInExcel_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reportResultsTableAsStringsInExcel_menuItemActionPerformed
    produceExcelReport(false);
}//GEN-LAST:event_reportResultsTableAsStringsInExcel_menuItemActionPerformed

private void loadReportSettingsModelFromLocalXMLFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadReportSettingsModelFromLocalXMLFileActionPerformed
    myState.setMRUReportSettingsModelFolder(//
            setReportSettingsModelFromXMLFile(myState.getMRUReportSettingsModelFolder()));

    updateReportTable(false, false, "");

}//GEN-LAST:event_loadReportSettingsModelFromLocalXMLFileActionPerformed

private void editCurrentReportSettingsModel_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editCurrentReportSettingsModel_menuItemActionPerformed

    ReportSettingsInterface.EditReportSettings(theSample.getReportSettingsModel(), this);
    updateReportTable(false, false, "");
}//GEN-LAST:event_editCurrentReportSettingsModel_menuItemActionPerformed

private void reportResultsTableAsPDF_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reportResultsTableAsPDF_menuItemActionPerformed

    try {
        ((TabbedReportViews) getReportTableTabbedPane()).createReportSVGandPDF(theSample.getSampleName().trim(), true);
    } catch (ETException ex) {
        new ETWarningDialog(ex).setVisible(true);
        Logger.getLogger(ETReduxFrame.class.getName()).log(Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_reportResultsTableAsPDF_menuItemActionPerformed

private void visitGeochronActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_visitGeochronActionPerformed
    BrowserControl.displayURL("https://www.geochron.org/");
}//GEN-LAST:event_visitGeochronActionPerformed

private void newSampleCompilation_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newSampleCompilation_menuItemActionPerformed
    try {
        setUpNewSample(SampleTypesEnum.COMPILATION.getName(), COMPILED.getName(), COMPILED.getIsotypeSystem(), COMPILED.getDefaultReportSpecsType());
    } catch (ETException ex) {
        new ETWarningDialog(ex).setVisible(true);
    }
}//GEN-LAST:event_newSampleCompilation_menuItemActionPerformed

private void gCubedPaperReductionEquations_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gCubedPaperReductionEquations_menuItemActionPerformed
    BrowserControl.displayURL(
            "http://cirdles.org/assets/documents/an-algorithm-for-u-pb-isotope-dilution-data-reduction-and-uncertainty-propagation.pdf");

}//GEN-LAST:event_gCubedPaperReductionEquations_menuItemActionPerformed

private void uncertaintyEllipses_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uncertaintyEllipses_menuItemActionPerformed
    BrowserControl.displayURL(
            "http://www.earth-time.org/projects/upb/public_docs/ErrorEllipses.pdf");

}//GEN-LAST:event_uncertaintyEllipses_menuItemActionPerformed

private void reduxPreferencesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reduxPreferencesActionPerformed
    PreferencesEditorDialog myPrefs
            = new PreferencesEditorDialog(
                    this,
                    true,
                    myState.getReduxPreferences());
    myPrefs.setSize(375, 540);
    myPrefs.setVisible(true);

    theSample.setFractionDataOverriddenOnImport(
            myPrefs.getReduxPreferences().isFractionDataOverriddenOnImport());
}//GEN-LAST:event_reduxPreferencesActionPerformed

private void startStopLiveUpdate_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startStopLiveUpdate_buttonActionPerformed
    toggleLiveUpdate();
}//GEN-LAST:event_startStopLiveUpdate_buttonActionPerformed

    /**
     *
     * @return
     */
    public boolean liveUpdateIsRunning() {
        return liveUpdateTimer.isRunning();
    }

    /**
     *
     * @param running
     */
    public void setLiveUpdateTimerIsRunning(boolean running) {
        // toggle state of live update by fooling timer
        if (running) {
            liveUpdateTimer.stop();
        } else {
            liveUpdateTimer.start();
        }

        toggleLiveUpdate();
    }

    /**
     *
     * @return
     */
    public String toggleLiveUpdate() {

        // if the live workflow timer is running, stop it
        // otherwise update the sample data
        if (liveUpdateTimer.isRunning()) {
            stopLiveUpdate();

        } else {
            if (theSample.isSampleTypeAnalysis()) {
                theSample.setSampleType(SampleTypesEnum.LIVEWORKFLOW.getName());
            }
            liveUpdateTimer.start();
            startStopLiveUpdate_button.setText("STOP Live Workflow");
        }

        return startStopLiveUpdate_button.getText();
    }

    /**
     *
     */
    public void stopLiveUpdate() {
        liveUpdateTimer.stop();
        // Text is "STOP Update"
        startStopLiveUpdate_button.setText("START Live Workflow");
        if (theSample.isSampleTypeLiveWorkflow()) {
            theSample.setSampleType(SampleTypesEnum.ANALYSIS.getName());
        }
    }

    private void performSampleDataUpdate() {
        if (liveUpdateTimer.isRunning()) {
            liveUpdateTimer.stop();
            // Text is "STOP Update"
            startStopLiveUpdate_button.setText("START Live Workflow");
        }
        theSample.setSampleType(SampleTypesEnum.ANALYSIS.getName());
        liveUpdateSample();

        setUpTheSample(false);

    }

    /**
     *
     * @return
     */
    public String getupdateSample_buttonText() {
        return startStopLiveUpdate_button.getText();
    }

    private synchronized void liveUpdateSample() {
        try {
            theSample.automaticUpdateOfUPbSampleFolder(theSample, myFractionEditor);
        } catch (ETException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }

        rebuildFractionDisplays(false, false);
    }

    /**
     * opens aliquot modal editor for the <code>Fraction</code> indicated by
     * argument <code>fraction</code> and opened to the editing tab indicated by
     * argument <code>selectedTab</code>. <code>selectedTab</code> is valid only
     * if it contains aliquot number between zero and seven inclusive.
     *
     * @pre the <code>Fraction</code> corresponding to <code>fraction</code>
     * exists in this <code>Sample</code> and <code>selectedTab</code> is a
     * valid tab number
     * @post an editor for the specified <code>Fraction</code> is opened to the
     * specified tab
     *
     * @param fraction the <code>Fraction</code> to be edited
     * @param selectedTab the tab to open the editor to
     */
    @Override
    public void editFraction(ETFractionInterface fraction, int selectedTab) {

        AliquotInterface aliquot = theSample.getAliquotByNumber(fraction.getAliquotNumber());
        myFractionEditor = null;

        if (theSample.isSampleTypeLegacy()
                || (theSample.isSampleTypeCompilation()
                && !fraction.getAnalysisMeasure(AnalysisMeasures.tracerMassInGrams.getName()).hasPositiveValue())//
                || fraction.isLegacy()) {
            myFractionEditor
                    = new UPbLegacyFractionEditorDialog(
                            this,
                            true,
                            aliquot,
                            fraction,
                            selectedTab,
                            false);
        } else if (theSample.getSampleAnalysisType().equalsIgnoreCase(SampleAnalysisTypesEnum.IDTIMS.getName())
                || (theSample.isSampleTypeCompilation())) {
            // TODO: Need kwiki page for LAICPMS               

            myFractionEditor
                    = new UPbFractionEditorDialog(
                            this,
                            true,
                            aliquot,
                            fraction,
                            selectedTab,
                            theSample.isSampleTypeCompilation());
        }

        if (myFractionEditor != null) {
            try {
                myFractionEditor.setTitle(
                        "Sample: "// 
                        + theSample.getSampleName()// 
                        + "   [Physical Constants: "//
                        + theSample.getPhysicalConstantsModel().getNameAndVersion() + "]");

            } catch (BadLabDataException badLabDataException) {
            }

            // oct 2014
            forceCloseOfSampleDateInterpretations();

            myFractionEditor.setVisible(true);

            // post-process the editor's results
            theSample.setChanged(theSample.isChanged() || fraction.isChanged());
            // feb 2010
            if (theSample.isChanged()) {
                SampleInterface.saveSampleAsSerializedReduxFile(theSample);
            }

            if (fraction.isDeleted()) {
                theSample.removeUPbReduxFraction(fraction);
            }

            // these statements release editor and prevent livwWorkflow from backtracking as it must do
            // if navigating fractions while staying open
            myFractionEditor.dispose();
            myFractionEditor = null;
        }

    }

    /**
     * opens aliquot modal editor for the </code>Aliquot</code> specified by
     * <code>aliquotNum</code>. The <code>Aliquot</code>'s
     * <code>Fractions</code> are populated on the fly.
     *
     * @pre an <code>Aliquot</code> exists with the number specified by argument
     * <code>aliquotNum</code>
     * @post an editor for the specified <code>Aliquot</code> is opened
     *
     * @param aliquotNum the number of the <code>Aliquot</code> to be edited
     */
    public void editAliquotByNumber(int aliquotNum) {

        // added march 2009 so that changes to fraction tab are saved upon use of aliquot button
        SampleInterface.saveSampleAsSerializedReduxFile(theSample);

        editAliquotByProjectType(theSample.getAliquotByNumber(aliquotNum));
    }

    private void editAliquot(SampleInterface sample, AliquotInterface aliquot) {
        DialogEditor myEditor = null;

        // theSample is sampleType: (SAMPLEFOLDER == LIVEWORKFLOW), COMPILATION, ANALYSIS, LEGACY
        // sampleType COMPILATION is a holdover from compiled SuperSamples created under the Samples menu
        // the plan is to make these into proper Projects - these were the original Compiled from 
        // various aliquots that Sam Bowring saw as compilation mode and turned out to be useful and powerful
        // note also the flag analyzed = true means either compiled legacy or compiled from existing
        // aliquots and imported into compilation; while false means that analysis will occur within redux
        // June 2015 assume for now that we decide purely on sampleAnalysisType
        // flavors: USERIES_CARB, IDTIMS, LAICPMS, LASS, GENERICUPB, and for superSamples of Projects: COMPILED, TRIPOLIZED
        if (sample.isAnalysisTypeIDTIMS()) {
            if (sample.isSampleTypeAnalysis()) {
                myEditor = new AliquotEditorDialog(this, true, theSample, aliquot);
            } else if (sample.isSampleTypeLegacy()) {
                myEditor = new AliquotLegacyEditorForIDTIMS(this, true, theSample, aliquot);
            }

        } else if (sample.isAnalysisTypeLAICPMS()) {
            if (sample.isSampleTypeAnalysis()) {
                myEditor = new AliquotEditorForLAICPMS(this, true, sample, aliquot);
            } else if (sample.isSampleTypeLegacy()) {
                myEditor = new AliquotLegacyEditorForLAICPMS(this, true, theSample, aliquot);
            }

        } else if (sample.isAnalysisTypeLASS()) {
            if (sample.isSampleTypeAnalysis()) {

            } else if (sample.isSampleTypeLegacy()) {
                myEditor = new AliquotLegacyEditorForLAICPMS(this, true, theSample, aliquot);
            }

        } else if (sample.isAnalysisTypeUSERIES()) {
            if (sample.isSampleTypeAnalysis()) {

            } else if (sample.isSampleTypeLegacy()) {

            }

        } else if (sample.isAnalysisTypeGENERIC_UPB()) {
            if (sample.isSampleTypeAnalysis()) {

            } else if (sample.isSampleTypeLegacy()) {

            }

        }

        if (myEditor != null) {
//            JDialog.setDefaultLookAndFeelDecorated(true);

            myEditor.setVisible(true);
        }
    }

    /**
     *
     * @param aliquot
     */
    public void editAliquotByProjectType(AliquotInterface aliquot) {
        if (theSample.isSampleTypeProject() //
                && !theSample.getSampleAnalysisType().equalsIgnoreCase("TRIPOLIZED")) {
            // Project has a compiledSuperSample made up of actual projectSamples
            // we need the actual sample associated with this aliquot
            // this aliquot is a copy for compiled super sample and we need
            // the aliquot with the name from name1::name and its sample
            // there are two anaylsisTypes of Project superSample: "COMPILED" and "TRIPOLIZED"
            // eventually Aliquot Editors may need to differentiate the two
            String aliquotName = aliquot.getAliquotName().split("::")[1];
            ArrayList<SampleInterface> samples = theProject.getProjectSamples();
            for (int i = 0; i < samples.size(); i++) {
                AliquotInterface sampleAliquot = samples.get(i).getAliquotByName(aliquotName);
                if (sampleAliquot != null) {
                    // now choose aliquot manager
                    // note that for a SuperSample sampleAnalysisType of "COMPILED", there may eventually
                    // be a variety of sub samples with different origins
                    editAliquot(samples.get(i), sampleAliquot);
                    break;
                }
            }
        } else {
            ((ReduxAliquotInterface) aliquot).setCompiled(false);
            editAliquot(theSample, aliquot);
        }
    }

private void ID_TIMSLegacyAnalysis_MIT_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ID_TIMSLegacyAnalysis_MIT_menuItemActionPerformed
    try {
        setUpNewSample(SampleTypesEnum.LEGACY.getName(), IDTIMS.getName(), IDTIMS.getIsotypeSystem(), IDTIMS.getDefaultReportSpecsType());
        changeContentOfTopPanel(ReduxConstants.TOP_PANEL_CONTENTS.FRACTIONS);
    } catch (ETException ex) {
        new ETWarningDialog(ex).setVisible(true);
    }
}//GEN-LAST:event_ID_TIMSLegacyAnalysis_MIT_menuItemActionPerformed

private void visitCIRDLESActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_visitCIRDLESActionPerformed
    BrowserControl.displayURL("https://cirdles.org");
}//GEN-LAST:event_visitCIRDLESActionPerformed

private void reportResultsTableAsNumbersInExcel_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reportResultsTableAsNumbersInExcel_menuItemActionPerformed
    produceExcelReport(true);
}//GEN-LAST:event_reportResultsTableAsNumbersInExcel_menuItemActionPerformed

private void writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_MC_USAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_MC_USAActionPerformed
    SampleImporterFromLAICPMSLegacyCVSFile_MC_UA.writeAndOpenCSVFileOfLegacyDataSampleFieldNames();
}//GEN-LAST:event_writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_MC_USAActionPerformed

private void changeLogMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeLogMenuItemActionPerformed
    //showChangeLog();
}//GEN-LAST:event_changeLogMenuItemActionPerformed

private void writeCSVFileOfIDTIMSLegacyDataSampleFieldNames_MITActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_writeCSVFileOfIDTIMSLegacyDataSampleFieldNames_MITActionPerformed
    SampleImporterFromIDTIMSLegacyCSVFile_MIT.writeAndOpenCSVFileOfLegacyDataSampleFieldNames();
}//GEN-LAST:event_writeCSVFileOfIDTIMSLegacyDataSampleFieldNames_MITActionPerformed

private void newSampleAnalysisForIDTIMS_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newSampleAnalysisForIDTIMS_menuItemActionPerformed
    try {
        setUpNewSample(SampleTypesEnum.ANALYSIS.getName(), IDTIMS.getName(), IDTIMS.getIsotypeSystem(), IDTIMS.getDefaultReportSpecsType());
    } catch (ETException ex) {
        new ETWarningDialog(ex).setVisible(true);
    }
}//GEN-LAST:event_newSampleAnalysisForIDTIMS_menuItemActionPerformed

private void updateData_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateData_buttonActionPerformed
    performSampleDataUpdate();
}//GEN-LAST:event_updateData_buttonActionPerformed

private void selectAllFractions_menuItemActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAllFractions_menuItemActionPerformed
    theSample.selectAllFractions();
    updateReportTable(false, false, "");
}//GEN-LAST:event_selectAllFractions_menuItemActionPerformed

private void deSelectAllFractions_menuItemActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deSelectAllFractions_menuItemActionPerformed
    theSample.deSelectAllFractions();
    updateReportTable(false, false, "");
}//GEN-LAST:event_deSelectAllFractions_menuItemActionPerformed

private void credits_menuItemActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_credits_menuItemActionPerformed
    showCredits();
}//GEN-LAST:event_credits_menuItemActionPerformed

private void saveCurrentReportSettingsModelAsLocalXMLFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveCurrentReportSettingsModelAsLocalXMLFileActionPerformed
    try {
        String reportSettingsFileParentFolder = theSample.saveReportSettingsToFile(myState.getMRUReportSettingsModelFolder());
        myState.setMRUReportSettingsModelFolder(reportSettingsFileParentFolder);
    } catch (BadLabDataException ex) {
        new ETWarningDialog(ex).setVisible(true);
    }
}//GEN-LAST:event_saveCurrentReportSettingsModelAsLocalXMLFileActionPerformed

private void loadDefaultReportSettingsModelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadDefaultReportSettingsModelActionPerformed
    theSample.restoreDefaultReportSettingsModel();
    theSample.setLegacyStatusForReportTable();
    updateReportTable(false, false, "");
}//GEN-LAST:event_loadDefaultReportSettingsModelActionPerformed

private void writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_SC_WSU_vBActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_SC_WSU_vBActionPerformed
    SampleImporterFromLAICPMSLegacyCVSFile_SC_WSU_vB.writeAndOpenCSVFileOfLegacyDataSampleFieldNames();
}//GEN-LAST:event_writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_SC_WSU_vBActionPerformed

private void LAICPMS_LegacyAnalysis_SC_WSU_vB_menuItemActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LAICPMS_LegacyAnalysis_SC_WSU_vB_menuItemActionPerformed
    try {
        setUpNewSample(SampleTypesEnum.LEGACY.getName(), LAICPMS_SCWSU_vB.getName(), LAICPMS_SCWSU_vB.getIsotypeSystem(), LAICPMS_SCWSU_vB.getDefaultReportSpecsType());
        changeContentOfTopPanel(ReduxConstants.TOP_PANEL_CONTENTS.FRACTIONS);
    } catch (ETException ex) {
        if (ex instanceof BadLabDataException) {
        }

        new ETWarningDialog(ex).setVisible(true);
    }
}//GEN-LAST:event_LAICPMS_LegacyAnalysis_SC_WSU_vB_menuItemActionPerformed

private void writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_NIGLActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_NIGLActionPerformed
    SampleImporterFromLAICPMSLegacyCVSFile_NIGL.writeAndOpenCSVFileOfLegacyDataSampleFieldNames();
}//GEN-LAST:event_writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_NIGLActionPerformed

private void LAICPMS_LegacyAnalysis_NIGL_menuItemActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LAICPMS_LegacyAnalysis_NIGL_menuItemActionPerformed
    try {
        setUpNewSample(SampleTypesEnum.LEGACY.getName(), LAICPMS_NIGL.getName(), LAICPMS_NIGL.getIsotypeSystem(), LAICPMS_NIGL.getDefaultReportSpecsType());
        changeContentOfTopPanel(ReduxConstants.TOP_PANEL_CONTENTS.FRACTIONS);
    } catch (ETException ex) {
        new ETWarningDialog(ex).setVisible(true);
    }
}//GEN-LAST:event_LAICPMS_LegacyAnalysis_NIGL_menuItemActionPerformed

private void formComponentResized (java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
    // TODO add your handling code here:
}//GEN-LAST:event_formComponentResized

private void fractionsTabulatedResultsLayeredPaneComponentResized (java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_fractionsTabulatedResultsLayeredPaneComponentResized
    try {
        reportTableTabbedPane.//
                setSize(calculateTabulatedResultsSize());
    } catch (Exception e) {
    }
}//GEN-LAST:event_fractionsTabulatedResultsLayeredPaneComponentResized

private void writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_SC_WSU_vAActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_SC_WSU_vAActionPerformed
    SampleImporterFromLAICPMSLegacyCVSFile_SC_WSU_vA.writeAndOpenCSVFileOfLegacyDataSampleFieldNames();
}//GEN-LAST:event_writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_SC_WSU_vAActionPerformed

private void LAICPMS_LegacyAnalysis_SC_WSU_vA_menuItemActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LAICPMS_LegacyAnalysis_SC_WSU_vA_menuItemActionPerformed
    try {
        setUpNewSample(SampleTypesEnum.LEGACY.getName(), LAICPMS_SCWSU_vA.getName(), LAICPMS_SCWSU_vA.getIsotypeSystem(), LAICPMS_SCWSU_vA.getDefaultReportSpecsType());
        changeContentOfTopPanel(ReduxConstants.TOP_PANEL_CONTENTS.FRACTIONS);
    } catch (ETException ex) {
        if (ex instanceof BadLabDataException) {
//            ex.printStackTrace();
        }

        new ETWarningDialog(ex).setVisible(true);
    }
}//GEN-LAST:event_LAICPMS_LegacyAnalysis_SC_WSU_vA_menuItemActionPerformed

private void customizeSampleMetadata_menuItemActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_customizeSampleMetadata_menuItemActionPerformed
    if (!GeochronSampleCustomMetadataDialog.amOpen) {
        DialogEditor mySESARSampleCustomMetadataDialog
                = new GeochronSampleCustomMetadataDialog(this, true, theSample);
        ((GeochronSampleCustomMetadataDialog) mySESARSampleCustomMetadataDialog).setSize(440, 685);
//        JDialog.setDefaultLookAndFeelDecorated(true);
        mySESARSampleCustomMetadataDialog.setVisible(true);
    } else {
    }
}//GEN-LAST:event_customizeSampleMetadata_menuItemActionPerformed

private void LAICPMS_LegacyAnalysis_SC_WSU_vV_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LAICPMS_LegacyAnalysis_SC_WSU_vV_menuItemActionPerformed
    try {
        setUpNewSample(SampleTypesEnum.LEGACY.getName(), LAICPMS_SCWSU_vV.getName(), LAICPMS_SCWSU_vV.getIsotypeSystem(), LAICPMS_SCWSU_vV.getDefaultReportSpecsType());
        changeContentOfTopPanel(ReduxConstants.TOP_PANEL_CONTENTS.FRACTIONS);
    } catch (ETException ex) {
        new ETWarningDialog(ex).setVisible(true);
    }

}//GEN-LAST:event_LAICPMS_LegacyAnalysis_SC_WSU_vV_menuItemActionPerformed

private void newSampleAnalysisForLAICPMS_MC_menuItemActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newSampleAnalysisForLAICPMS_MC_menuItemActionPerformed
    try {
        setUpNewSample(SampleTypesEnum.ANALYSIS.getName(), LAICPMSMC.getName(), LAICPMSMC.getIsotypeSystem(), LAICPMSMC.getDefaultReportSpecsType());
    } catch (ETException ex) {
        new ETWarningDialog(ex).setVisible(true);
    }
}//GEN-LAST:event_newSampleAnalysisForLAICPMS_MC_menuItemActionPerformed

private void LAICPMS_LegacyAnalysis_UH_menuItemActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LAICPMS_LegacyAnalysis_UH_menuItemActionPerformed
    try {
        setUpNewSample(SampleTypesEnum.LEGACY.getName(), LAICPMS_UH.getName(), LAICPMS_UH.getIsotypeSystem(), LAICPMS_UH.getDefaultReportSpecsType());
        changeContentOfTopPanel(ReduxConstants.TOP_PANEL_CONTENTS.FRACTIONS);
    } catch (ETException ex) {
        new ETWarningDialog(ex).setVisible(true);
    }

}//GEN-LAST:event_LAICPMS_LegacyAnalysis_UH_menuItemActionPerformed

    private void saveCurrentReportSettingsAsDefault_menuItemActionPerformed ( java.awt.event.ActionEvent evt ) {//GEN-FIRST:event_saveCurrentReportSettingsAsDefault_menuItemActionPerformed
        ReduxLabData.getInstance().setDefaultReportSettingsModelByIsotopeStyle(theSample.getReportSettingsModel());
    }//GEN-LAST:event_saveCurrentReportSettingsAsDefault_menuItemActionPerformed

    private void loadEARTHTIMEDefaultReportSettingsModel_UPb_menuItemActionPerformed ( java.awt.event.ActionEvent evt ) {//GEN-FIRST:event_loadEARTHTIMEDefaultReportSettingsModel_UPb_menuItemActionPerformed
        SampleInterface.loadDefaultEARTHTIMEReportSettingsModel_UPb(theSample);
        theSample.setLegacyStatusForReportTable();
        updateReportTable(false, false, "");
    }//GEN-LAST:event_loadEARTHTIMEDefaultReportSettingsModel_UPb_menuItemActionPerformed

    private void exit_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exit_menuItemActionPerformed
        if (askAndSaveReduxPersistentState()) {
            System.exit(0);
        }
    }//GEN-LAST:event_exit_menuItemActionPerformed

    private void reportResultsTableAsNumbersInCSV_menuItemActionPerformed ( java.awt.event.ActionEvent evt ) {//GEN-FIRST:event_reportResultsTableAsNumbersInCSV_menuItemActionPerformed
        produceCSVReport();
    }//GEN-LAST:event_reportResultsTableAsNumbersInCSV_menuItemActionPerformed

    private void newProjectRawDataLAICPMSActionPerformed ( java.awt.event.ActionEvent evt ) {//GEN-FIRST:event_newProjectRawDataLAICPMSActionPerformed
        setUpNewTripolizedProject(SampleAnalysisTypesEnum.LAICPMS);
    }//GEN-LAST:event_newProjectRawDataLAICPMSActionPerformed

    private void openProjectFile_menuItemActionPerformed ( java.awt.event.ActionEvent evt ) {//GEN-FIRST:event_openProjectFile_menuItemActionPerformed
        openProjectFile();
    }//GEN-LAST:event_openProjectFile_menuItemActionPerformed

    private void gCubedPaperSoftwareTIMS_menuItemActionPerformed ( java.awt.event.ActionEvent evt ) {//GEN-FIRST:event_gCubedPaperSoftwareTIMS_menuItemActionPerformed
        BrowserControl.displayURL(
                "http://cirdles.org/assets/documents/engineering-cyber-infrastructure-for-u-pb-geochronology.pdf");
    }//GEN-LAST:event_gCubedPaperSoftwareTIMS_menuItemActionPerformed

    private void writeCSVFileOfGenericUPbIsotopicLegacyDataSampleFieldNames_AActionPerformed ( java.awt.event.ActionEvent evt ) {//GEN-FIRST:event_writeCSVFileOfGenericUPbIsotopicLegacyDataSampleFieldNames_AActionPerformed
        ProjectOfLegacySamplesImporterFromCSVFile_GenericUPbIsotopic_A.writeAndOpenCSVFileOfLegacyDataSampleFieldNames();
    }//GEN-LAST:event_writeCSVFileOfGenericUPbIsotopicLegacyDataSampleFieldNames_AActionPerformed

    private void manageProject_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manageProject_menuItemActionPerformed
        manageTheProject();
    }//GEN-LAST:event_manageProject_menuItemActionPerformed

    private void saveProjectFile_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveProjectFile_menuItemActionPerformed
        saveProject();
    }//GEN-LAST:event_saveProjectFile_menuItemActionPerformed

    private void manageRawData_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manageRawData_menuItemActionPerformed
        manageRawDataSession();
    }//GEN-LAST:event_manageRawData_menuItemActionPerformed

    private void genericUPbDataTableInCSV_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genericUPbDataTableInCSV_menuItemActionPerformed
        setUpNewCompiledLegacyProject(GENERIC_UPB.getName(), GENERIC_UPB.getIsotypeSystem(), GENERIC_UPB.getDefaultReportSpecsType());
    }//GEN-LAST:event_genericUPbDataTableInCSV_menuItemActionPerformed

    private void closeProjectFile_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeProjectFile_menuItemActionPerformed
        try {
            closeTheProject();
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }


    }//GEN-LAST:event_closeProjectFile_menuItemActionPerformed

    private void saveProjectFileAs_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveProjectFileAs_menuItemActionPerformed
        try {
            saveProjectFileAs();
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }


    }//GEN-LAST:event_saveProjectFileAs_menuItemActionPerformed

    private void project_menuMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_project_menuMenuSelected
        setupMRUProjectFiles();
    }//GEN-LAST:event_project_menuMenuSelected

    private void exportProjectSamplesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportProjectSamplesActionPerformed
        exportProjectSamples();
    }//GEN-LAST:event_exportProjectSamplesActionPerformed

    private void loadLastProject_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadLastProject_buttonActionPerformed
        loadMostRecentProject(true);
    }//GEN-LAST:event_loadLastProject_buttonActionPerformed

    private void openProject_buttonopenSampleFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openProject_buttonopenSampleFileActionPerformed
        if (checkSavedStatusTheProject()) {
            openProjectFile();
        }
    }//GEN-LAST:event_openProject_buttonopenSampleFileActionPerformed

    private void manageProjectRawData_buttonopenSampleFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manageProjectRawData_buttonopenSampleFileActionPerformed
        if (theSample.getIsotopeSystem().compareToIgnoreCase("UTh") == 0) {
            manageTheProject();
        } else {
            manageRawDataSession();
        }
    }//GEN-LAST:event_manageProjectRawData_buttonopenSampleFileActionPerformed

    private void ucsb_LASS_A_DataTableInCSV_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ucsb_LASS_A_DataTableInCSV_menuItemActionPerformed
        setUpNewCompiledLegacyProject(LASS.getName(), LASS.getIsotypeSystem(), LASS.getDefaultReportSpecsType());
    }//GEN-LAST:event_ucsb_LASS_A_DataTableInCSV_menuItemActionPerformed

    private void producePbCCorrReport_jMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_producePbCCorrReport_jMenuItemActionPerformed
        producePbcCorrectionReport();
    }//GEN-LAST:event_producePbCCorrReport_jMenuItemActionPerformed

    private void manageRareEarthElementModelsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manageRareEarthElementModelsActionPerformed
        try {
            editLabDataUPb(6);
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }        // TODO add your handling code here:
    }//GEN-LAST:event_manageRareEarthElementModelsActionPerformed

    private void manageLAICPMSLabDefaultsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manageLAICPMSLabDefaultsActionPerformed
        try {
            editLabDataUPb(9);
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }        // TODO add your handling code here:
    }//GEN-LAST:event_manageLAICPMSLabDefaultsActionPerformed

    private void exportProjectSamplesToGeochronActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportProjectSamplesToGeochronActionPerformed
        exportProjectSamplesToGeochron();
    }//GEN-LAST:event_exportProjectSamplesToGeochronActionPerformed

    private void writeCSVFileOfProjectLegacyDataSampleFieldNames_UCSB_LASS_AActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_writeCSVFileOfProjectLegacyDataSampleFieldNames_UCSB_LASS_AActionPerformed
        ProjectOfLegacySamplesImporterFromCSVFile_UCSB_LASS_A.writeAndOpenCSVFileOfLegacyDataSampleFieldNames();
    }//GEN-LAST:event_writeCSVFileOfProjectLegacyDataSampleFieldNames_UCSB_LASS_AActionPerformed

    private void loadLastProject_buttonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_loadLastProject_buttonMouseEntered
        ArrayList<String> myMRUs = myState.getMRUProjectList();
        loadLastProject_button.setToolTipText(myMRUs.isEmpty() ? "No recent projects" : myMRUs.get(0));
    }//GEN-LAST:event_loadLastProject_buttonMouseEntered

    private void loadLastSample_buttonMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_loadLastSample_buttonMouseEntered
        ArrayList<String> myMRUs = myState.getMRUSampleList();
        loadLastSample_button.setToolTipText(myMRUs.isEmpty() ? "No recent samples" : myMRUs.get(0));
    }//GEN-LAST:event_loadLastSample_buttonMouseEntered

    private void USeriesCarbonateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_USeriesCarbonateActionPerformed
        setUpNewCompiledLegacyProject(USERIES_CARB.getName(), USERIES_CARB.getIsotypeSystem(), USERIES_CARB.getDefaultReportSpecsType());
    }//GEN-LAST:event_USeriesCarbonateActionPerformed

    private void reportSettingsHelpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reportSettingsHelpActionPerformed
        BrowserControl.displayURL("http://cirdles.org/projects/et_redux/#reports_overview");
    }//GEN-LAST:event_reportSettingsHelpActionPerformed

    private void USeriesIgneousActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_USeriesIgneousActionPerformed
        setUpNewCompiledLegacyProject(USERIES_IGN.getName(), USERIES_IGN.getIsotypeSystem(), USERIES_IGN.getDefaultReportSpecsType());
    }//GEN-LAST:event_USeriesIgneousActionPerformed

    private void visitUseriesRocksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_visitUseriesRocksActionPerformed
        BrowserControl.displayURL("https://sites.google.com/site/useriesrocks/");
    }//GEN-LAST:event_visitUseriesRocksActionPerformed

    private void editPhysicalConstantsModelsUThActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editPhysicalConstantsModelsUThActionPerformed
        try {
            editLabDataUTh(0);
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
    }//GEN-LAST:event_editPhysicalConstantsModelsUThActionPerformed

    private void editDetritalUraniumThoriumModelsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editDetritalUraniumThoriumModelsActionPerformed
        try {
            editLabDataUTh(1);
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
    }//GEN-LAST:event_editDetritalUraniumThoriumModelsActionPerformed

    private void loadEARTHTIMEDefaultReportSettingsModel_UTh_Carb_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadEARTHTIMEDefaultReportSettingsModel_UTh_Carb_menuItemActionPerformed
        SampleInterface.loadDefaultEARTHTIMEReportSettingsModel_UTh_Carb(theSample);
        theSample.setLegacyStatusForReportTable();
        updateReportTable(false, false, "");
    }//GEN-LAST:event_loadEARTHTIMEDefaultReportSettingsModel_UTh_Carb_menuItemActionPerformed

    private void showReportHeadings_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showReportHeadings_menuItemActionPerformed
        jSplitPane1.setDividerLocation(jSplitPane1.getDividerLocation() == 1 ? getHeight() : 0);
    }//GEN-LAST:event_showReportHeadings_menuItemActionPerformed

    private void loadEARTHTIMEDefaultReportSettingsModel_UTh_Ign_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadEARTHTIMEDefaultReportSettingsModel_UTh_Ign_menuItemActionPerformed
        SampleInterface.loadDefaultEARTHTIMEReportSettingsModel_UTh_Ign(theSample);
        theSample.setLegacyStatusForReportTable();
        updateReportTable(false, false, "");
    }//GEN-LAST:event_loadEARTHTIMEDefaultReportSettingsModel_UTh_Ign_menuItemActionPerformed

    private void gCubedPaperSoftwareLAICPMS_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gCubedPaperSoftwareLAICPMS_menuItemActionPerformed
        BrowserControl.displayURL(
                "http://cirdles.org/assets/documents/LAICPMS%20ET_Redux.pdf");
    }//GEN-LAST:event_gCubedPaperSoftwareLAICPMS_menuItemActionPerformed

    private void paperOnUSeriesDataReportingStandards_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paperOnUSeriesDataReportingStandards_menuItemActionPerformed
        BrowserControl.displayURL(
                "http://cirdles.org/assets/documents/USeriesDataReportingStandardsPaper.pdf");
    }//GEN-LAST:event_paperOnUSeriesDataReportingStandards_menuItemActionPerformed

    private void genericUPbGADataTableCSV_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genericUPbGADataTableCSV_menuItemActionPerformed
        setUpNewCompiledLegacyProject(GENERIC_GA_UPB.getName(), GENERIC_UPB.getIsotypeSystem(), GENERIC_UPB.getDefaultReportSpecsType());
    }//GEN-LAST:event_genericUPbGADataTableCSV_menuItemActionPerformed

    private void genericUPbSquid3DataTableCSV_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genericUPbSquid3DataTableCSV_menuItemActionPerformed
        setUpNewCompiledLegacyProject(GENERIC_SQUID3_UPB.getName(), GENERIC_SQUID3_UPB.getIsotypeSystem(), GENERIC_SQUID3_UPB.getDefaultReportSpecsType());
    }//GEN-LAST:event_genericUPbSquid3DataTableCSV_menuItemActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        try {
            editLabDataUTh(3);
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void createGithubIssueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createGithubIssueActionPerformed
        contributeIssueOnGitHubAction();
    }//GEN-LAST:event_createGithubIssueActionPerformed

    private void helpMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        BrowserControl.displayURL("http://cirdles.org/projects/et_redux/");
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem ID_TIMSLegacyAnalysis_MIT_menuItem;
    private javax.swing.JMenuItem LAICPMS_LegacyAnalysis_MC_UA_menuItem;
    private javax.swing.JMenuItem LAICPMS_LegacyAnalysis_NIGL_menuItem;
    private javax.swing.JMenuItem LAICPMS_LegacyAnalysis_SC_WSU_vA_menuItem;
    private javax.swing.JMenuItem LAICPMS_LegacyAnalysis_SC_WSU_vB_menuItem;
    private javax.swing.JMenuItem LAICPMS_LegacyAnalysis_SC_WSU_vV_menuItem;
    private javax.swing.JMenuItem LAICPMS_LegacyAnalysis_UH_menuItem;
    private javax.swing.JMenu MRUProject_menu;
    private javax.swing.JMenu MRUSampleMenu;
    private javax.swing.JMenuItem USeriesCarbonate;
    private javax.swing.JMenuItem USeriesIgneous;
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JMenu aliquotsMenu;
    private javax.swing.JPanel buttonBar_panel;
    private javax.swing.JMenuItem changeLogMenuItem;
    private javax.swing.JMenuItem closeProjectFile_menuItem;
    private javax.swing.JMenuItem closeSampleFile;
    private javax.swing.JMenuItem createGithubIssue;
    private javax.swing.JMenuItem credits_menuItem;
    private javax.swing.JMenuItem customizeSampleMetadata_menuItem;
    private javax.swing.JMenuItem deSelectAllFractions_menuItem;
    private javax.swing.JMenu earthTimeWebSiteMenu;
    private javax.swing.JMenuItem editCurrentReportSettingsModel_menuItem;
    private javax.swing.JMenuItem editDetritalUraniumThoriumModels;
    private javax.swing.JMenuItem editInitialPbModels;
    private javax.swing.JMenuItem editLabFractionationModels;
    private javax.swing.JMenuItem editLabTracers;
    private javax.swing.JMenuItem editMineralStandardsModels;
    private javax.swing.JMenuItem editPbBlanks;
    private javax.swing.JMenuItem editPhysicalConstantsModels;
    private javax.swing.JMenuItem editPhysicalConstantsModelsUTh;
    private javax.swing.JMenuItem exitRedux;
    private javax.swing.JSeparator exitSeparator;
    private javax.swing.JMenuItem exit_menuItem;
    private javax.swing.JMenuItem exportProjectSamples;
    private javax.swing.JMenuItem exportProjectSamplesToGeochron;
    private javax.swing.JMenu fractionsMenu;
    private javax.swing.JLayeredPane fractionsTabulatedResultsLayeredPane;
    private javax.swing.JMenuItem gCubedPaperReductionEquations_menuItem;
    private javax.swing.JMenuItem gCubedPaperSoftwareLAICPMS_menuItem;
    private javax.swing.JMenuItem gCubedPaperSoftwareTIMS_menuItem;
    private javax.swing.JMenuItem genericUPbDataTableInCSV_menuItem;
    private javax.swing.JMenuItem genericUPbGADataTableCSV_menuItem;
    private javax.swing.JMenuItem genericUPbSquid3DataTableCSV_menuItem;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuItem helpMenuItem;
    private javax.swing.JButton interpretSampleDates_button;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator10;
    private javax.swing.JPopupMenu.Separator jSeparator11;
    private javax.swing.JPopupMenu.Separator jSeparator12;
    private javax.swing.JPopupMenu.Separator jSeparator13;
    private javax.swing.JPopupMenu.Separator jSeparator14;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JPopupMenu.Separator jSeparator9;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JMenu labDataMenu;
    private javax.swing.JMenu labDataUPb;
    private javax.swing.JMenu labDataUTh;
    private javax.swing.JMenuItem loadDefaultReportSettingsModel;
    private javax.swing.JMenuItem loadEARTHTIMEDefaultReportSettingsModel_UPb_menuItem;
    private javax.swing.JMenuItem loadEARTHTIMEDefaultReportSettingsModel_UTh_Carb_menuItem;
    private javax.swing.JMenuItem loadEARTHTIMEDefaultReportSettingsModel_UTh_Ign_menuItem;
    private javax.swing.JButton loadLastProject_button;
    private javax.swing.JButton loadLastSample_button;
    private javax.swing.JMenuItem loadReportSettingsModelFromLocalXMLFile;
    private javax.swing.JMenuBar mainMenuBar;
    private javax.swing.JMenuItem manageLAICPMSLabDefaults;
    private javax.swing.JButton manageProjectRawData_button;
    private javax.swing.JMenuItem manageProject_menuItem;
    private javax.swing.JMenuItem manageRareEarthElementModels;
    private javax.swing.JMenuItem manageRawData_menuItem;
    private javax.swing.ButtonGroup manageSampleModel_buttonGroup;
    private javax.swing.JMenuItem manageSampleModel_menuItem;
    private javax.swing.JMenuItem manageTIMSLabDefaults;
    private javax.swing.JMenu newProjectFromLegacyDataTable_menu;
    private javax.swing.JMenu newProjectFromRawData_menu;
    private javax.swing.JMenuItem newProjectRawDataIDTIMS;
    private javax.swing.JMenuItem newProjectRawDataLAICPMS;
    private javax.swing.JMenuItem newSampleAnalysisForIDTIMS_menuItem;
    private javax.swing.JMenuItem newSampleAnalysisForLAICPMS_MC_menuItem;
    private javax.swing.JMenuItem newSampleCompilation_menuItem;
    private javax.swing.JMenu newSampleFileFromLegacyAnalysis_Menu;
    private javax.swing.JMenuItem openProjectFile_menuItem;
    private javax.swing.JButton openProject_button;
    private javax.swing.JMenuItem openSampleFile;
    private javax.swing.JButton openSample_button;
    private javax.swing.JMenuItem paperOnUSeriesDataReportingStandards_menuItem;
    private javax.swing.JMenuItem producePbCCorrReport_jMenuItem;
    private javax.swing.JMenu project_menu;
    private javax.swing.JButton quit_button;
    private javax.swing.JButton reduceAll_button;
    private javax.swing.JMenuItem reduxPreferences;
    private javax.swing.JMenu referencesMenu;
    private javax.swing.JMenu reportMenu;
    private javax.swing.JMenuItem reportResultsTableAsNumbersInCSV_menuItem;
    private javax.swing.JMenuItem reportResultsTableAsNumbersInExcel_menuItem;
    private javax.swing.JMenuItem reportResultsTableAsPDF_menuItem;
    private javax.swing.JMenuItem reportResultsTableAsStringsInExcel_menuItem;
    private javax.swing.JMenuItem reportSettingsHelp;
    private javax.swing.JMenu sampleFileMenu;
    private javax.swing.JButton saveAndQuit_button;
    private javax.swing.JMenuItem saveCurrentReportSettingsAsDefault_menuItem;
    private javax.swing.JMenuItem saveCurrentReportSettingsModelAsLocalXMLFile;
    private javax.swing.JMenuItem saveProjectFileAs_menuItem;
    private javax.swing.JMenuItem saveProjectFile_menuItem;
    private javax.swing.JMenuItem saveSampleFile;
    private javax.swing.JMenuItem saveSampleFileAs;
    private javax.swing.JButton save_button;
    private javax.swing.JMenuItem selectAllFractions_menuItem;
    private javax.swing.JMenuItem showReportHeadings_menuItem;
    public javax.swing.JButton startStopLiveUpdate_button;
    private javax.swing.JMenu templatesForLegacyProjects_menu;
    private javax.swing.JMenu templatesForLegacySample_menu;
    private javax.swing.JTable theFractionTable;
    private javax.swing.JScrollPane theFractionTableScrollPane;
    private javax.swing.JMenu toolsMenu;
    private javax.swing.JMenu uPbLegacyDataMenu;
    private javax.swing.JMenu uPbSquid3Data_menu;
    private javax.swing.JMenu uSeriesLegacyData;
    private javax.swing.JMenuItem ucsb_LASS_A_DataTableInCSV_menuItem;
    private javax.swing.JMenuItem uncertaintyEllipses_menuItem;
    private javax.swing.JButton updateData_button;
    private javax.swing.JMenuItem visitCIRDLES;
    private javax.swing.JMenuItem visitEarthTimeOrg;
    private javax.swing.JMenuItem visitGeoSamplesOrg;
    private javax.swing.JMenuItem visitGeochron;
    private javax.swing.JMenuItem visitUseriesRocks;
    private javax.swing.JMenuItem writeCSVFileOfGenericUPbIsotopicLegacyDataSampleFieldNames_A;
    private javax.swing.JMenuItem writeCSVFileOfIDTIMSLegacyDataSampleFieldNames_MIT;
    private javax.swing.JMenuItem writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_MC_USA;
    private javax.swing.JMenuItem writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_NIGL;
    private javax.swing.JMenuItem writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_SC_WSU_vA;
    private javax.swing.JMenuItem writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_SC_WSU_vB;
    private javax.swing.JMenuItem writeCSVFileOfProjectLegacyDataSampleFieldNames_UCSB_LASS_A;
    // End of variables declaration//GEN-END:variables

}
