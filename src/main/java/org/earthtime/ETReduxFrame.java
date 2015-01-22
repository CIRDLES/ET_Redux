/*
 * ETReduxFrame.java
 *
 * Created on March 14, 2006, 8:27 PM
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
package org.earthtime;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.help.DefaultHelpBroker;
import javax.help.HelpBroker;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
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
import org.earthtime.UPb_Redux.aliquots.Aliquot;
import org.earthtime.UPb_Redux.aliquots.UPbReduxAliquot;
import org.earthtime.UPb_Redux.dateInterpretation.DateProbabilityDensityPanel;
import org.earthtime.UPb_Redux.dateInterpretation.SampleTreeAnalysisMode;
import org.earthtime.UPb_Redux.dateInterpretation.SampleTreeCompilationMode;
import org.earthtime.UPb_Redux.dateInterpretation.SampleTreeI;
import org.earthtime.UPb_Redux.dateInterpretation.WeightedMeanGraphPanel;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.AliquotDetailsDisplayInterface;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.ConcordiaGraphPanel;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.PlottingDetailsDisplayInterface;
import org.earthtime.UPb_Redux.dialogs.AboutBox;
import org.earthtime.UPb_Redux.dialogs.DialogEditor;
import org.earthtime.UPb_Redux.dialogs.LabDataEditorDialog;
import org.earthtime.UPb_Redux.dialogs.PreferencesEditorDialog;
import org.earthtime.UPb_Redux.dialogs.ReportSettingsManager;
import org.earthtime.UPb_Redux.dialogs.fractionManagers.UPbFractionEditorDialog;
import org.earthtime.UPb_Redux.dialogs.projectManagers.ProjectManagerFor_LAICPMS_FromRawData;
import org.earthtime.UPb_Redux.dialogs.projectManagers.projectLegacyManagers.AbstractProjectOfLegacySamplesDataManagerDialog;
import org.earthtime.UPb_Redux.dialogs.projectManagers.projectLegacyManagers.ProjectOfLegacySamplesDataManagerDialogForGenericUPb_A;
import org.earthtime.UPb_Redux.dialogs.projectManagers.projectLegacyManagers.ProjectOfLegacySamplesDataManagerDialogForUCSB_LASS_A;
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
import org.earthtime.UPb_Redux.fractions.Fraction;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFractionTable;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFractionTableModel;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbLAICPMSFraction;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.fractionReduction.PbcCorrectionDetails;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.fractionReduction.UPbFractionReducer;
import org.earthtime.UPb_Redux.reduxLabData.ReduxLabData;
import org.earthtime.UPb_Redux.reports.ReportPainterI;
import org.earthtime.UPb_Redux.reports.ReportSettings;
import org.earthtime.UPb_Redux.reports.excelReports.CsvResultsTable;
import org.earthtime.UPb_Redux.reports.excelReports.ExcelResultsTable;
import org.earthtime.UPb_Redux.reports.reportViews.ReportAliquotFractionsView;
import org.earthtime.UPb_Redux.reports.reportViews.ReportUpdaterInterface;
import org.earthtime.UPb_Redux.reports.reportViews.TabbedReportViews;
import org.earthtime.UPb_Redux.samples.Sample;
import org.earthtime.UPb_Redux.samples.SampleI;
import org.earthtime.UPb_Redux.samples.sampleImporters.SampleImporterFromIDTIMSLegacyCSVFile_MIT;
import org.earthtime.UPb_Redux.samples.sampleImporters.SampleImporterFromLAICPMSLegacyCVSFile_MC_UA;
import org.earthtime.UPb_Redux.samples.sampleImporters.SampleImporterFromLAICPMSLegacyCVSFile_NIGL;
import org.earthtime.UPb_Redux.samples.sampleImporters.SampleImporterFromLAICPMSLegacyCVSFile_SC_WSU_vA;
import org.earthtime.UPb_Redux.samples.sampleImporters.SampleImporterFromLAICPMSLegacyCVSFile_SC_WSU_vB;
import org.earthtime.UPb_Redux.user.ReduxPersistentState;
import org.earthtime.UPb_Redux.utilities.AnnouncementPane;
import org.earthtime.UPb_Redux.utilities.BrowserControl;
import org.earthtime.UPb_Redux.utilities.CustomIcon;
import org.earthtime.UPb_Redux.utilities.ETSerializer;
import org.earthtime.UPb_Redux.utilities.JHelpAction;
import org.earthtime.UPb_Redux.utilities.MacOSAboutHandler;
import org.earthtime.beans.ET_JButton;
import org.earthtime.dataDictionaries.SampleAnalysisTypesEnum;
import org.earthtime.dataDictionaries.SampleTypesEnum;
import org.earthtime.exceptions.ETException;
import org.earthtime.projects.EarthTimeSerializedFileInterface;
import org.earthtime.projects.Project;
import org.earthtime.projects.projectImporters.ProjectOfLegacySamplesImporterFromCSVFile_GenericUPbIsotopic_A;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataModels.mineralStandardModels.MineralStandardUPbModel;
import org.earthtime.utilities.FileHelper;

/**
 *
 * @author James F. Bowring
 */
public class ETReduxFrame extends javax.swing.JFrame implements ReportPainterI, ReportUpdaterInterface, SampleDateInterpretationSubscribeInterface {

    // user-specific configurations
    private ReduxPersistentState myState;
    private ReduxLabData myLabData;
    private final ClassLoader cldr;
    private final java.net.URL imageURL;
    private final InputStream changeLogURL;
    private final InputStream creditsURL;
    /**
     *
     */
    protected ImageIcon reduxIcon;
    // theSample is serialized as a *.redux file
    private Sample theSample = null;
    // theProject is serialized as a *.redux file
    private Project theProject = null;
    // updated July 2012
    private final JTabbedPane reportTableTabbedPane;
    private JLayeredPane myConcordiaGraphPanel;
//    private JFXPanel concordiaGraphPanelIsoplot;
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
    private DialogEditor myReportSettingsEditor = null;
    private DialogEditor myAboutBox = null;
    /**
     *
     */
    protected JLayeredPane messagePane = null;
    private final Timer liveUpdateTimer;
////    private int DATATABLE_TOP_HEIGHT = 62;//72;//62
    private DialogEditor myProjectManager;
    //oct 2014
    private DialogEditor sampleDateInterpDialog;

    // x, y, sigma x, sigma y, rho
    private static final double[][] DATA = new double[][]{
        {0.0722539075, 0.0110295656, 0.0002049758, 0.0000063126, 0.5365532874},
        {0.0721971452, 0.0110309854, 0.0001783027, 0.0000056173, 0.5325448483},
        {0.0721480905, 0.0110333887, 0.0001262722, 0.0000053814, 0.5693849155},
        {0.0720208987, 0.0110278685, 0.0001041118, 0.0000051695, 0.6034598793},
        {0.0722006985, 0.0110287224, 0.0001150679, 0.0000053550, 0.6488140173},
        {0.0721043666, 0.0110269651, 0.0001536438, 0.0000055438, 0.4514464090},
        {0.0721563039, 0.0110282194, 0.0001241486, 0.0000054189, 0.5407720667},
        {0.0721973299, 0.0110274879, 0.0001224165, 0.0000055660, 0.5557499444},
        {0.0721451656, 0.0110281849, 0.0001461117, 0.0000054048, 0.5309378161},
        {0.0720654237, 0.0110247729, 0.0001547497, 0.0000053235, 0.2337854029},
        {0.0721799174, 0.0110318201, 0.0001485404, 0.0000056511, 0.5177944463},
        {0.0721826355, 0.0110283902, 0.0001377158, 0.0000056126, 0.5953348385},
        {0.0720275042, 0.0110278402, 0.0001875497, 0.0000058909, 0.5274591815},
        {0.0721360819, 0.0110276418, 0.0001252055, 0.0000054561, 0.5760966585}
    };

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
            ReduxLabData myLabData,
            File reduxFile)
            throws BadLabDataException {

        this.myState = myState;
        this.myLabData = myLabData;
        initComponents();

        messagePane = new AnnouncementPane();

        // TODO: refactor these things into another init method
        // load resources
        cldr = this.getClass().getClassLoader();

        // produce application icon
        // http://www.centerkey.com/mac/java/
        imageURL = cldr.getResource("org/earthtime/UPb_Redux/images/U-Pb_Redux_Icon.png");
        reduxIcon = new CustomIcon(imageURL);
        ((CustomIcon) reduxIcon).setSize(16, 16);
        setIconImage(reduxIcon.getImage());

        // persist changeLog
        changeLogURL = cldr.getResourceAsStream("org/earthtime/UPb_Redux/resources/docs/ChangeLog.html");
        File localChangeLogFile = new File("ChangeLog.html");
        try {
            InputStream in = changeLogURL;
            // Overwrite the file.
            OutputStream out = new FileOutputStream(localChangeLogFile);

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
        setSizeAndCenterFrame(1180, 750);

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

            new MacOSAboutHandler(this);
        }

        /**
         * Setup theFractionsTable
         */
        // setup the tabulated data
        // July 2012 upograde
        reportTableTabbedPane = new TabbedReportViews(this);
        ((TabbedReportViews) reportTableTabbedPane).initializeTabs();
        reportTableTabbedPane.setSize(fractionsTabulatedResultsLayeredPane.getSize());
        fractionsTabulatedResultsLayeredPane.add(reportTableTabbedPane, JLayeredPane.DEFAULT_LAYER);

        // reduxfile may exist if supplied to command line
        if (reduxFile.exists()) {
            openTheSample(reduxFile, true);
        } else {
            setUpEmptySample();
            System.out.println();
        }

        liveUpdateTimer = new Timer(5000, new ActionListener() {//5 sec
            public void actionPerformed(ActionEvent e) {
                liveUpdateSample();
            }
        });

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

    private void changeContentOfTopPanel(ReduxConstants.TOP_PANEL_CONTENTS contents) {

        switch (contents) {
            case WELCOME:
                JButton visitCIRDLESbutton = new ET_JButton("Welcome!  Please Visit us at CIRDLES.org");
                visitCIRDLESbutton.setFont(ReduxConstants.sansSerif_12_Bold);
                visitCIRDLESbutton.setBounds(400, 75, 350, 25);
                visitCIRDLESbutton.addActionListener(new ActionListenerForGotoCirdles());
                messagePane.add(visitCIRDLESbutton);

                JTextArea announce = new JTextArea(//
                        "ANNOUNCEMENT:   2015 brings changes to this project. "//
                        + "We are changing the name to ET_Redux, for EARTHTIME Redux, in preparation for additional isotope systems, such as U-series.  "//
                        + "We are moving the project to a GitHub repository in the near future to make collaboration easier and more transparent.  "
                        + "We are seeking a new logo for ET_Redux and invite your submissions.");
                announce.setFont(ReduxConstants.sansSerif_12_Bold);
                announce.setBounds(200, 125, 750, 100);
                announce.setLineWrap(true);
                announce.setWrapStyleWord(true);
                announce.setAlignmentX(CENTER_ALIGNMENT);
                announce.setAlignmentY(CENTER_ALIGNMENT);
                messagePane.add(announce);

                JButton visitTOPSOIL = new ET_JButton("Checkout Topsoil - the Isoplot replacement project that you can join at github.com/CIRDLES/topsoil");
                visitTOPSOIL.setFont(ReduxConstants.sansSerif_12_Bold);
                visitTOPSOIL.setBounds(265, 575, 650, 25);//(475, 140, 650, 25);
                visitTOPSOIL.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        BrowserControl.displayURL("https://github.com/CIRDLES/topsoil");
                    }
                });
                messagePane.add(visitTOPSOIL);

                JButton releaseNotes = new ET_JButton("Click to see Latest Release Notes.");
                releaseNotes.setFont(ReduxConstants.sansSerif_10_Bold);
                releaseNotes.setBounds(475, 500, 210, 20);//(150, 180, 220, 20);
                releaseNotes.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        BrowserControl.displayURL("https://docs.google.com/document/d/18CDAMmabkGiNTBiVkqSaNlW8OOXSr4CHGa5TFg4gKwg/edit");
                    }
                });

                theFractionTableScrollPane.setViewportView(messagePane);
                break;
            case WAITING:
                ((AnnouncementPane) messagePane).initializeAnnouncementPane("Waiting");
                theFractionTableScrollPane.setViewportView(messagePane);
                break;
            case FRACTIONS:
                theFractionTableScrollPane.setViewportView(theFractionTable);
        }

        theFractionTableScrollPane.repaint();

    }

    private static class ActionListenerForGotoCirdles implements ActionListener {

        public ActionListenerForGotoCirdles() {
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            BrowserControl.displayURL("https://cirdles.org");
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
            ETSerializer.SerializeObjectToFile(getMyState(), ReduxPersistentState.getMySerializedName());
        } catch (ETException eTException) {
        }

        try {
            ETSerializer.SerializeObjectToFile(myLabData, ReduxLabData.getMySerializedName());
        } catch (ETException eTException) {
            System.out.println("LabData did not save");
        }
    }

    private void editLabData(int selectedTab)
            throws BadLabDataException {
        if (!LabDataEditorDialog.amOpen) {
            myLabDataEditor
                    = new LabDataEditorDialog(this, false, getMyLabData(), selectedTab);
            ((LabDataEditorDialog) myLabDataEditor).setSize();
            JDialog.setDefaultLookAndFeelDecorated(true);
            myLabDataEditor.setVisible(true);
        } else {
            ((LabDataEditorDialog) myLabDataEditor).setDetailsPaneTab(selectedTab);
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
            SampleI superSample = theProject.getSuperSample();
            if (superSample != null) {
                isTripolized = superSample.isAnalysisTypeTripolized();
            }
        }
        // project open
        manageProject_menuItem.setEnabled(!sampleTrueProjectFalse && isTripolized);
        manageRawData_menuItem.setEnabled(!sampleTrueProjectFalse && isTripolized);
        saveProjectFile_menuItem.setEnabled(!sampleTrueProjectFalse);
        saveProjectFileAs_menuItem.setEnabled(!sampleTrueProjectFalse);
        closeProjectFile_menuItem.setEnabled(!sampleTrueProjectFalse);

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

        forceCloseOfSampleDateInterpretations();

        theProject = null;
        System.gc();

        EarthTimeSerializedFileInterface deserializedFile =//
                (EarthTimeSerializedFileInterface) ETSerializer.GetSerializedObjectFromFile(projectReduxFile.getPath());

        if (deserializedFile instanceof Sample) {
            System.out.println("Trying to open a SAMPLE .redux file as a Project.");
        } else if (!(deserializedFile instanceof Project)) {
            System.out.println("Trying to open an invalid .redux file.");
        } else {

            stopLiveUpdate();

            myState.setMRUProjectFile(projectReduxFile);
            theProject = (Project) deserializedFile;

            // update the project so it knows where it is
            theProject.setLocationOfProjectReduxFile(projectReduxFile);

            // update MRU status
            getMyState().updateMRUProjectList(projectReduxFile);

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
                ReportSettings projectReportSettings = theProject.getSuperSample().getReportSettingsModelUpdatedToLatestVersion();
                ((Sample) theProject.getSuperSample()).setReportSettingsModel(projectReportSettings);
                theProject.saveTheProjectAsSerializedReduxFile();

                // go straight to data table display
                myProjectManager = null;
                initializeProject();

            } else { // instantiate project manager so processing can be initialited

                myProjectManager = //
                        new ProjectManagerFor_LAICPMS_FromRawData(this, true, myState, theProject);
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
                int response = JOptionPane.showConfirmDialog(null,
                        new String[]{"The current Project " + theProject.getProjectName() + " has been changed.",
                            "Do you want to Save it before proceeding ?"
                        },
                        "U-Pb Redux Warning",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                switch (response) {
                    case JOptionPane.YES_OPTION:
                        try {
                            saveTheProject();
                        } catch (BadLabDataException badLabDataException) {
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
            getMyState().getReduxPreferences().setDefaultSampleAnalysisPurpose(theProject.getAnalysisPurpose());
            theProject.saveTheProjectAsSerializedReduxFile();
            setUpTheProject(false);
        }
    }

    /**
     *
     * @return @throws BadLabDataException
     */
    private boolean saveProjectFileAs() throws BadLabDataException {

        File selectedFile = theProject.saveProjectFileAs();

        if (selectedFile != null) {
            getMyState().updateMRUProjectList(selectedFile);
            setUpTheProject(false);

            saveProjectFile_menuItem.setEnabled(false);

            saveProjectFileAs_menuItem.setEnabled(true);

            closeProjectFile_menuItem.setEnabled(true);

            try {
                getMyState().setMRUProjectFolderPath(selectedFile.getParentFile().getCanonicalPath());
            } catch (IOException iOException) {
            }
        }

        return selectedFile != null;
    }

    /**
     *
     */
    public void initializeProject() {
        theSample = (Sample) theProject.getSuperSample();
        theSample.setChanged(false);

        setUpTheProject(false);

        // oct 2014 these are already handled
//        loadAndShowReportTableData();
//        updateReportTable(true);
    }

    /**
     *
     */
    public void updateProjectDisplayTitleBar() {
        try {
            this.setTitle(
                    "U-Pb Redux  " //
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
     */
    public void setUpTheProject(boolean performReduction) {
        theSample.registerSampleWithLabData(myLabData);

        SampleI superSample = theProject.getSuperSample();

        if (superSample != null) {
            if (superSample.isAnalysisTypeTripolized()) {
                // oct 2012 register mineralstandardmodel
                AbstractRatiosDataModel primaryMineralStandard = theProject.getTripoliSession().getPrimaryMineralStandard();
                if (primaryMineralStandard != null) {
                    myLabData.registerMineralStandardModel(primaryMineralStandard, false);
                    if (((MineralStandardUPbModel) primaryMineralStandard).hasInitialPb()) {
                        myLabData.registerInitialPbModel(((MineralStandardUPbModel) primaryMineralStandard).getInitialPbModelET(), false);
                    }
                }
            }
        }

        updateProjectDisplayTitleBar();

        // static call to leave pointer to this frame
        Sample.parentFrame = this;

        // oct 2014 reportsettings
        ReportSettings reportSettingsModel = superSample.getReportSettingsModel();
        if (reportSettingsModel != null) {
            reportSettingsModel.setLegacyData(superSample.isAnalyzed());
        }

        rebuildFractionDisplays(performReduction);

        // set up concordia for use on fraction details window
        //as well as interpret date window and archiving
        myConcordiaGraphPanel = new ConcordiaGraphPanel(theSample, this);
        UPbFractionEditorDialog.setConcordiaGraphPanel(getMyConcordiaGraphPanel());

        // set up probabilitydensity for archiving
        myNormedProbabilityPanel = new DateProbabilityDensityPanel(theSample);

        startStopLiveUpdate_button.setEnabled(false);
        manageProjectRawData_button.setEnabled(true);

        updateData_button.setEnabled(false);

        changeContentOfTopPanel(ReduxConstants.TOP_PANEL_CONTENTS.FRACTIONS);

        // reports menu activated
        for (Component m : reportMenu.getMenuComponents()) {
            m.setEnabled(true);
        }

        sampleMenuOnTrueProjectMenuOnFalse(false);

        buildAliquotsMenu();

        customizeReduxSkin();
    }

    private void manageTheProject() {
        if (myProjectManager == null) {
            myProjectManager = //
                    new ProjectManagerFor_LAICPMS_FromRawData(this, true, myState, theProject);
        }

        myProjectManager.setVisible(true);

    }

    private void manageRawDataSession() {
        if ((myProjectManager == null) || !(myProjectManager instanceof ProjectManagerFor_LAICPMS_FromRawData)) {
            myProjectManager = //
                    new ProjectManagerFor_LAICPMS_FromRawData(this, true, myState, theProject);
        }
        ((ProjectManagerFor_LAICPMS_FromRawData) myProjectManager).initializeSessionManager(false, true, false);
    }

    private void setUpNewProject(String projectAnalysisType, String sampleAnalysisType) {

        theProject = new Project(myState);

        if (projectAnalysisType.equalsIgnoreCase(SampleAnalysisTypesEnum.TRIPOLIZED.getName()) //
                &&//
                sampleAnalysisType.equalsIgnoreCase(SampleAnalysisTypesEnum.LAICPMS.getName())) {

            myProjectManager = //
                    new ProjectManagerFor_LAICPMS_FromRawData(this, true, myState, theProject);

            // modal call
            myProjectManager.setVisible(true);
        }

//        if (projectAnalysisType.equalsIgnoreCase(SampleAnalysisTypesEnum.COMPILED.getName()) //
//                &&//
//                sampleAnalysisType.equalsIgnoreCase(SampleAnalysisTypesEnum.GENERIC_UPB.getName())) {
//
//            try {
//                theSample = Sample.initializeNewSample(//
//                        SampleTypesEnum.PROJECT.getName(), //
//                        SampleAnalysisTypesEnum.COMPILED.getName(),//
//                        myLabData,//
//                        myState.getReduxPreferences().getDefaultSampleAnalysisPurpose());
//            } catch (BadLabDataException badLabDataException) {
//            }
//
//            theProject.setSuperSample(theSample);
//            myProjectManager
//                    = new ProjectOfLegacySamplesDataManagerDialogForGenericUPb_A(
//                            this,
//                            true,
//                            theProject,
//                            getMyState().getMRUImportFolderCompilationMode());
//
//            ((AbstractProjectOfLegacySamplesDataManagerDialog) myProjectManager).setSize();
//
//            // modal call
//            myProjectManager.setVisible(true);
//
//            // remembers last folder used for import of single or set of fractions
//            getMyState().setMRUImportFolderCompilationMode(
//                    ((AbstractProjectOfLegacySamplesDataManagerDialog) myProjectManager).getImportFractionFolderMRU().toString());
//
//        }
        if (projectAnalysisType.equalsIgnoreCase(SampleAnalysisTypesEnum.COMPILED.getName())) {

            try {
                theSample = Sample.initializeNewSample(//
                        SampleTypesEnum.PROJECT.getName(), //
                        SampleAnalysisTypesEnum.COMPILED.getName(),//
                        myLabData,//
                        myState.getReduxPreferences().getDefaultSampleAnalysisPurpose());
            } catch (BadLabDataException badLabDataException) {
            }

            theProject.setSuperSample(theSample);

            if (sampleAnalysisType.equalsIgnoreCase(SampleAnalysisTypesEnum.GENERIC_UPB.getName())) {
                myProjectManager
                        = new ProjectOfLegacySamplesDataManagerDialogForGenericUPb_A(
                                this,
                                true,
                                theProject,
                                getMyState().getMRUImportFolderCompilationMode());
            } else if (sampleAnalysisType.equalsIgnoreCase(SampleAnalysisTypesEnum.LASS.getName())) {
                myProjectManager
                        = new ProjectOfLegacySamplesDataManagerDialogForUCSB_LASS_A(
                                this,
                                true,
                                theProject,
                                getMyState().getMRUImportFolderCompilationMode());
            }

            ((AbstractProjectOfLegacySamplesDataManagerDialog) myProjectManager).setSize();

            // modal call
            myProjectManager.setVisible(true);

            // remembers last folder used for import of single or set of fractions
            getMyState().setMRUImportFolderCompilationMode(
                    ((AbstractProjectOfLegacySamplesDataManagerDialog) myProjectManager).getImportFractionFolderMRU().toString());

        }

        if (!theProject.getProjectSamples().isEmpty()) {
            setUpTheProject(false);
            try {
                saveTheProject();
            } catch (BadLabDataException badLabDataException) {
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
        } catch (ETException eTException) {
        }
    }

    // ********* end  ***************** P R O J E C T S ******************************
    private boolean checkSavedStatusTheSample() {
        boolean retval = true;
        try {
            if (theSample.isChanged()) {
                // Modal dialog with OK/cancel and a text field
                int response = JOptionPane.showConfirmDialog(null,
                        new String[]{"The current Sample " + theSample.getSampleName() + " has been changed.",
                            "Do you want to Save it before proceeding ?"
                        },
                        "U-Pb Redux Warning",
                        JOptionPane.YES_NO_OPTION,//.YES_NO_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                switch (response) {
                    case JOptionPane.YES_OPTION:
                        saveTheSampleOrProject();
                    case JOptionPane.NO_OPTION:
                        theSample.setChanged(false);
                        break;
//                    case JOptionPane.CANCEL_OPTION:
//                        retval = false;
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
        String dialogTitle = "Select a Redux Sample file to OPEN: *.redux";
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
     * @throws BadLabDataException
     */
    private void setUpNewSample(String sampleType, String sampleAnalysisType)
            throws BadLabDataException, ETException {
        // check to see if existing sample has been saved
        // returns false if user cancels, etc.

        stopLiveUpdate();

        if (checkSavedStatusTheSample()) {
            // good to proceed
            // save off the existing sample
            Sample saveTheSample = theSample;

            // set up a new empty sample based on sampleType
            theSample = Sample.initializeNewSample(//
                    sampleType, sampleAnalysisType, myLabData, myState.getReduxPreferences().getDefaultSampleAnalysisPurpose());

            // manageTheSample sets up the correct form and returns whether it was successful
            // meantime, the form is opened modally to process user setting up sample data
            boolean sampleSetupInitialized = manageTheSample();

            if (sampleSetupInitialized) {
                // rewrite sept 2010
                // for type analysis, manager has already done a file save as
                boolean resultOfSaveSampleFileAs = true;
                if (!theSample.isTypeAnalysis()) {
                    resultOfSaveSampleFileAs = saveSampleFileAs();
                }
                if (theSample.isTypeAnalysis() || resultOfSaveSampleFileAs) {
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

        if (theSample.isTypeProject()) {
            // JULY 2012 start migration to project metaphor where project = super sample

            theProject = new Project();
            theProject.setSuperSample(theSample);
            if (theSample.getSampleAnalysisType().equalsIgnoreCase(SampleAnalysisTypesEnum.COMPILED.getName())) {
                mySampleManager
                        = new ProjectOfLegacySamplesDataManagerDialogForGenericUPb_A(
                                this,
                                true,
                                theProject,
                                getMyState().getMRUImportFolderLegacyMode());
            }

            ((AbstractProjectOfLegacySamplesDataManagerDialog) mySampleManager).setSize();

            // modal call to manager
            mySampleManager.setVisible(true);

            // remembers last folder used for import of single or set of fractions
            getMyState().setMRUImportFolderCompilationMode(
                    ((AbstractProjectOfLegacySamplesDataManagerDialog) mySampleManager).getImportFractionFolderMRU().toString());

            return ((AbstractProjectOfLegacySamplesDataManagerDialog) mySampleManager).isInitialized();

        } else if (theSample.getSampleType().equalsIgnoreCase(SampleTypesEnum.COMPILATION.getName())) {

            mySampleManager
                    = new SampleCompilationManagerDialog(
                            this,
                            true,
                            theSample,
                            getMyState().getMRUImportFolderCompilationMode());

            ((SampleCompilationManagerDialog) mySampleManager).setSize();
            //JDialog.setDefaultLookAndFeelDecorated( true );
            // modal call to manager
            mySampleManager.setVisible(true);

            // remembers last folder used for import of single or set of fractions
            getMyState().setMRUImportFolderCompilationMode(
                    ((SampleCompilationManagerDialog) mySampleManager).getImportFractionFolderMRU().toString());

            return ((SampleCompilationManagerDialog) mySampleManager).isInitialized();

        } else if (((theSample.getSampleType().equalsIgnoreCase(SampleTypesEnum.SAMPLEFOLDER.getName())) ||//
                (theSample.isTypeLiveUpdate()) ||//
                (theSample.isTypeAnalysis())) &&//
                theSample.isAnalysisTypeIDTIMS()) {

            mySampleManager
                    = new SampleAnalysisWorkflowManagerIDTIMS(
                            this,
                            true,
                            theSample,
                            getMyState().getMRUSampleFolder(),
                            getMyState().getMRUSampleMetaDataFolder(),
                            getMyState().getMRUImportedXMLFractionsFolder());

            ((SampleAnalysisWorkflowManagerInterface) mySampleManager).setSize();
            JDialog.setDefaultLookAndFeelDecorated(true);
            // modal call to manager
            mySampleManager.setVisible(true);

            // remembers last folder used for SampleMetaData used by C-Sharp Tripoli even if dialog not completed
            try {
                getMyState().setMRUSampleMetaDataFolder(
                        ((SampleAnalysisWorkflowManagerInterface) mySampleManager).getSampleMetaDataFolder().toString());
            } catch (Exception e) {
            }

            if (((SampleAnalysisWorkflowManagerInterface) mySampleManager).isInitialized()) {
                // remembers last folder used for creating sample folder for live update
                try {
                    getMyState().setMRUSampleFolder(
                            ((SampleAnalysisWorkflowManagerInterface) mySampleManager).getSampleFolder().toString());
                } catch (Exception e) {
                }

                // remembers last folder used for import of single or set of fractions
                try {
                    getMyState().setMRUImportedXMLFractionsFolder(
                            ((SampleAnalysisWorkflowManagerInterface) mySampleManager).getImportedXMLFractionsFolder().toString());
                } catch (Exception e) {
                }

                // remembers last folder used for Java Tripoli raw data files
                try {
                    getMyState().setMRUTripoliRawDataFolder(
                            ((SampleAnalysisWorkflowManagerInterface) mySampleManager).getTripoliRawDataFolder().toString());
                } catch (Exception e) {
                }
            }

            return ((SampleAnalysisWorkflowManagerInterface) mySampleManager).isInitialized();

        } else if (((theSample.getSampleType().equalsIgnoreCase(SampleTypesEnum.SAMPLEFOLDER.getName())) ||//
                (theSample.isTypeLiveUpdate()) ||//
                (theSample.isTypeAnalysis())) &&//
                theSample.isAnalysisTypeLAICPMS()) {

            mySampleManager
                    = new SampleManagerDialogForTripolizedLAICPMS(
                            this,
                            true,
                            theSample,
                            getMyState().getMRUImportFolderLegacyMode());

            JDialog.setDefaultLookAndFeelDecorated(true);

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
                                getMyState().getMRUImportFolderLegacyMode());
            } else if (theSample.getSampleAnalysisType().equalsIgnoreCase("LAICPMS_MCUA")//
                    || // the old data from UArizona
                    theSample.getSampleAnalysisType().equalsIgnoreCase(SampleAnalysisTypesEnum.LAICPMS.getName())) {
                mySampleManager
                        = new SampleLegacyDataManagerDialogForLAICPMS_MC_UA(
                                this,
                                true,
                                theSample,
                                getMyState().getMRUImportFolderLegacyMode());
            } else if (theSample.getSampleAnalysisType().equalsIgnoreCase("LAICPMS_SCWSU_vV")//
                    || // Victor Valencia
                    theSample.getSampleAnalysisType().equalsIgnoreCase(SampleAnalysisTypesEnum.LAICPMS.getName())) {
                mySampleManager
                        = new SampleLegacyDataManagerDialogForLAICPMS_MC_UA_VV(
                                this,
                                true,
                                theSample,
                                getMyState().getMRUImportFolderLegacyMode());
            } else if (theSample.getSampleAnalysisType().equalsIgnoreCase("LAICPMS_SCWSU_vB")) {
                mySampleManager
                        = new SampleLegacyDataManagerDialogForLAICPMS_SC_WSU_vB(
                                this,
                                true,
                                theSample,
                                getMyState().getMRUImportFolderLegacyMode());
            } else if (theSample.getSampleAnalysisType().equalsIgnoreCase("LAICPMS_SCWSU_vA")) {
                mySampleManager
                        = new SampleLegacyDataManagerDialogForLAICPMS_SC_WSU_vA(
                                this,
                                true,
                                theSample,
                                getMyState().getMRUImportFolderLegacyMode());
            } else if (theSample.getSampleAnalysisType().equalsIgnoreCase("LAICPMS_SCWSU")) {// backward compatible
                mySampleManager
                        = new SampleLegacyDataManagerDialogForLAICPMS_SC_WSU_vA(
                                this,
                                true,
                                theSample,
                                getMyState().getMRUImportFolderLegacyMode());
            } else if (theSample.getSampleAnalysisType().equalsIgnoreCase("LAICPMS_NIGL")) {
                mySampleManager
                        = new SampleLegacyDataManagerDialogForLAICPMS_NIGL(
                                this,
                                true,
                                theSample,
                                getMyState().getMRUImportFolderLegacyMode());
            } else if (theSample.getSampleAnalysisType().equalsIgnoreCase("LAICPMS_UH")) {
                mySampleManager
                        = new SampleLegacyDataManagerDialogForLAICPMS_UH(
                                this,
                                true,
                                theSample,
                                getMyState().getMRUImportFolderLegacyMode());
            } else if (theSample.getSampleAnalysisType().equalsIgnoreCase("LASS")) {
                mySampleManager
                        = new SampleLegacyDataManagerDialogForLASS(
                                this,
                                true,
                                theSample,
                                getMyState().getMRUImportFolderLegacyMode());
            }

            JDialog.setDefaultLookAndFeelDecorated(true);

            if (mySampleManager == null) {
                return false;
            } else {
                ((AbstractSampleLegacyManagerDialog) mySampleManager).setSize();
                // modal call to manager
                mySampleManager.setVisible(true);

                try {
                    // remembers last folder used for import of single or set of fractions
                    getMyState().setMRUImportFolderLegacyMode(
                            ((AbstractSampleLegacyManagerDialog) mySampleManager).getImportFractionFolderMRU().toString());
                } catch (Exception e) {
                }
                return ((AbstractSampleLegacyManagerDialog) mySampleManager).isInitialized();
            }
        }

    }

    private void saveTheSampleOrProject() throws BadLabDataException {
        if (theSample != null) {
            if (theSample.isTypeProject()) {
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
            getMyState().getReduxPreferences().setDefaultSampleAnalysisPurpose(theSample.getAnalysisPurpose());
            theSample.saveTheSampleAsSerializedReduxFile();
            setUpTheSample(false);
        }
    }

    /**
     *
     * @return @throws BadLabDataException
     */
    private boolean saveSampleFileAs() throws BadLabDataException {

        File selectedFile = theSample.saveSampleFileAs();

        if (selectedFile != null) {
            getMyState().updateMRUSampleList(selectedFile);
            setUpTheSample(false);

            saveSampleFileAs.setEnabled(true);

            closeSampleFile.setEnabled(true);

            // handle LIVEUPDATEMODE because it contains no data yet
            if (theSample.getSampleType().equalsIgnoreCase(SampleTypesEnum.ANALYSIS.getName())) {
                try {
                    getMyState().setMRUSampleFolder(selectedFile.getParentFile().getCanonicalPath());
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
    public synchronized void setUpTheSample(boolean performReduction)
            throws BadLabDataException {

        theSample.setUpSample(myLabData);

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

        // static call to leave pointer to this frame
        Sample.parentFrame = this;

        // initialize sample with current user preferences
        theSample.setFractionDataOverriddenOnImport(
                myState.getReduxPreferences().isFractionDataOverriddenOnImport());

        // this true causes data reduction
        //System.out.println("setup the sample call");
        // may 2012 first pass reduction
        if (performReduction) {
            theSample.reduceSampleData();
        }

        // feb 2010 added legacyData field to force display when no reduction happening
        theSample.setLegacyStatusForReportTable();

        rebuildFractionDisplays(performReduction);

        // set up concordia for use on fraction details window
        //as well as interpret date window and archiving
        myConcordiaGraphPanel = new ConcordiaGraphPanel(theSample, this);
        UPbFractionEditorDialog.setConcordiaGraphPanel(getMyConcordiaGraphPanel());

        // march 2014
//        concordiaGraphPanelIsoplot = new ConcordiaGraphPanelIsoplot(theSample);
        // set up probabilitydensity for archiving
        myNormedProbabilityPanel = new DateProbabilityDensityPanel(theSample);

        manageProjectRawData_button.setEnabled(false);

        startStopLiveUpdate_button.setEnabled(//
                theSample.isAutomaticDataUpdateMode() && //
                (theSample.isTypeAnalysis() || theSample.isTypeLiveUpdate()));

        updateData_button.setEnabled(//
                theSample.isAutomaticDataUpdateMode() && //
                (theSample.isTypeAnalysis() || theSample.isTypeLiveUpdate()));

        reduceAll_button.setEnabled(theSample.isTypeAnalysis() || theSample.isTypeLiveUpdate());

        changeContentOfTopPanel(ReduxConstants.TOP_PANEL_CONTENTS.FRACTIONS);

        buildAliquotsMenu();

        sampleMenuOnTrueProjectMenuOnFalse(true);

        // reports menu activated
        for (Component m : reportMenu.getMenuComponents()) {
            m.setEnabled(true);
        }

        customizeReduxSkin();
    }

    private void buildAliquotsMenu() {
        // aug 2010 provide aliquot management menu
        aliquotsMenu.removeAll();
        aliquotsMenu.setEnabled(false);
        fractionsMenu.setEnabled(false);
        selectAllFractions_menuItem.setEnabled(false);
        deSelectAllFractions_menuItem.setEnabled(false);

        for (final Aliquot a : theSample.getActiveAliquots()) {
            JMenuItem menuItem = aliquotsMenu.add(new JMenuItem(a.getAliquotName()));

            menuItem.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    theSample.editAliquotByNumber(((UPbReduxAliquot) a).getAliquotNumber());
                }
            });

            aliquotsMenu.setEnabled(true);
            fractionsMenu.setEnabled(true);
            selectAllFractions_menuItem.setEnabled(true);
            deSelectAllFractions_menuItem.setEnabled(true);
        }
    }

    private void customizeReduxSkin() {
        // July 2012 first pass at customizing skin; oct 2014
        if (theSample.isTypeProject() || theSample.isTypeLegacy() || theSample.isAnalysisTypeLAICPMS() || theSample.isAnalysisTypeCompiled()) {
            jSplitPane1.setDividerLocation(0);
        } else if (theSample.isAnalysisTypeIDTIMS()) {
            jSplitPane1.setDividerLocation(0.4);
        } else {
            // show the welcome page
            jSplitPane1.setDividerLocation(1.0);
        }
    }

    /**
     *
     * @param performReduction
     */
    @Override
    public void rebuildFractionDisplays(boolean performReduction) {

        ((UPbFractionTable) getTheFractionTable()).setMySample(this, theSample);

        ((UPbFractionTable) getTheFractionTable()).formatTable();

        ((TabbedReportViews) getReportTableTabbedPane()).setSample(theSample);

        updateReportTable(performReduction);

    }

    /**
     *
     */
    public void updateReportTable() {
        updateReportTable(false);
    }

    /**
     *
     * @param performReduction
     */
    @Override
    public void updateReportTable(boolean performReduction) {

//        System.out.println("performreduction = " + performReduction);
        // march 2013
        try {
            UPbFractionReducer.getInstance().setSessionCorrectedUnknownsSummaries(//
                    theProject.getTripoliSession().getSessionCorrectedUnknownsSummaries());
        } catch (Exception e) {
        }

        if (performReduction) {
            theSample.reduceSampleData();
        }
        // this call is for top table of measured ratios
        ((UPbFractionTableModel) getTheFractionTable().getModel()).//
                refreshTableData();

        loadAndShowReportTableData();

        // oct 2014
        // removed next line nov 2014
//        theSample.updateAndSaveSampleDateModelsByAliquot();
        // this statement makes a difference if user went back to project manager and changed things
        if (sampleDateInterpDialog != null) {
            ((SampleDateInterpretationsManager) sampleDateInterpDialog).setSample(theSample);
        }

        try {
            ((SampleDateInterpretationsManager) sampleDateInterpDialog).refreshSampleDateInterpretations();
        } catch (Exception e) {
        }
    }

    /**
     *
     */
    @Override
    public void loadAndShowReportTableData() {

        ((TabbedReportViews) getReportTableTabbedPane()).prepareTabs();
    }

    private void openTheSample(File selFile, boolean checkSavedStatus) throws BadLabDataException {

        forceCloseOfSampleDateInterpretations();

        if (checkSavedStatus) {
            checkSavedStatusTheSample();
        }

        setLiveUpdateTimerIsRunning(false);

        EarthTimeSerializedFileInterface deserializedFile = (EarthTimeSerializedFileInterface) ETSerializer.GetSerializedObjectFromFile(selFile.getPath());//     Sample.getTheSampleFromSerializedReduxFile( selFile );

        // TODO: Oct 2011 check for Project file?? or is it automatically a project file
        // created here for a single sample??
        if (deserializedFile instanceof Project) {
            System.out.println("Opening a project");
        } else {

            Sample deserializedSample = (Sample) deserializedFile;

            if (deserializedSample != null) {

                setTheSample(deserializedSample);

                // dec 2011 = fix the legacy problem of badly numbered aliquots
                // compress them to 1,2,3...
                theSample.repairAliquotNumberingDec2011();

                // update MRU status
                getMyState().updateMRUSampleList(selFile);

                // oct 2014 first check for updated reportsettings
                ReportSettings sampleReportSettings = theSample.getReportSettingsModelUpdatedToLatestVersion();
                theSample.setReportSettingsModel(sampleReportSettings);

                // feb 2010 catch case where saved as LIVEWORKFLOW
                if (theSample.isTypeLiveUpdate()) {
                    theSample.setSampleType(SampleTypesEnum.ANALYSIS.getName());
                }
                setUpTheSample(!theSample.isAnalyzed());//.isTypeAnalysis());

                // march 2009 - handle reflection for automatic update
                // this means if sample is automode, then set its path to where found
                if (deserializedSample.isAutomaticDataUpdateMode()) {
                    deserializedSample.setSampleFolderSaved(selFile.getParentFile());
                    for (Aliquot a : deserializedSample.getAliquots()) {
                        ((UPbReduxAliquot) a).setContainingSampleDataFolder(selFile.getParentFile());
                    }
                }

                // sept 2007 to be safe, we now save it back to update any minor changes due to version updates
                theSample.setChanged(false);
                theSample.saveTheSampleAsSerializedReduxFile(selFile);
            }
        }

    }

    private boolean closeTheSample() throws BadLabDataException {

        boolean retval = checkSavedStatusTheSample();

        setLiveUpdateTimerIsRunning(false);

        forceCloseOfSampleDateInterpretations();

        if (retval) {
            setUpEmptySample();//theSample.getSampleType() );
        }

        return retval;
    }

    private boolean closeTheProject() throws BadLabDataException {

        saveTheProject();
        //boolean retval = checkSavedStatusTheSample();
        forceCloseOfSampleDateInterpretations();
        //setLiveUpdateTimerIsRunning( false );
        //if ( retval ) {
        setUpEmptySample();//theSample.getSampleType() );
        //}

        return true;
    }

    private void setUpEmptySample() //
            throws BadLabDataException {
        setTheSample(Sample.initializeNewSample("NONE", "", myLabData, myState.getReduxPreferences().getDefaultSampleAnalysisPurpose()));
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

    private javax.swing.JTable getTheFractionTable() {
        return theFractionTable;
    }

    /**
     *
     * @param theFractionTable
     */
    public void setTheFractionTable(javax.swing.JTable theFractionTable) {
        this.theFractionTable = theFractionTable;
    }

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
    public Sample getTheSample() {
        return theSample;
    }

    /**
     *
     * @param theSample
     */
    public void setTheSample(Sample theSample) {
        this.theSample = theSample;
    }

    /**
     *
     * @return
     */
    public ReduxLabData getMyLabData() {
        return myLabData;
    }

    /**
     *
     * @param myLabData
     */
    public void setMyLabData(ReduxLabData myLabData) {
        this.myLabData = myLabData;
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
        String[][] reportFractions = theSample.reportActiveFractionsByNumberStyle(isNumeric);
        ReportAliquotFractionsView.sortReportColumn(//
                reportFractions, //
                ((TabbedReportViews) getReportTableTabbedPane()).getActiveFractionsSortedColumn(),//
                ((TabbedReportViews) getReportTableTabbedPane()).getActiveFractionsSortedColumnDirection());

        // generate excel report
        ExcelResultsTable.produceExcelResultsTable(//
                theSample.getSampleName(), reportFractions, isNumeric);
    }

    /**
     *
     */
    public void produceCSVReport() {
        String[][] reportFractions = theSample.reportActiveFractionsByNumberStyle(true);
        ReportAliquotFractionsView.sortReportColumn(//
                reportFractions, //
                ((TabbedReportViews) getReportTableTabbedPane()).getActiveFractionsSortedColumn(),//
                ((TabbedReportViews) getReportTableTabbedPane()).getActiveFractionsSortedColumnDirection());

        // generate csv report
        CsvResultsTable.produceCSVResultsTable(//
                theSample.getSampleName(), reportFractions);
    }

    /**
     *
     */
    public void producePbcCorrectionReport() {
        if (theProject != null) {
            if (theProject.getSuperSample().isAnalysisTypeTripolized()) {
                // oct 2014 have a LAICPMS project
                File pbcCorrReportFile = new File("LAICPMS_Pbc_Correction_Report_for_" + theProject.getProjectName() + ".txt");
                PrintWriter outputWriter = null;
                try {
                    outputWriter = new PrintWriter(new FileWriter(pbcCorrReportFile));
                    for (Aliquot activeAliquot : theProject.getSuperSample().getActiveAliquots()) {
                        outputWriter.println("Fractions from " + activeAliquot.getAliquotName() + "\n");
                        outputWriter.println(PbcCorrectionDetails.headerString() + "\n");
                        Vector<Fraction> fractions = ((UPbReduxAliquot) activeAliquot).getActiveAliquotFractions();
                        for (int i = 0; i < fractions.size(); i++) {
                            Fraction fraction = fractions.get(i);

                            // static values available after this run
                            PbcCorrectionDetails.zeroAllValues();
                            PbcCorrectionDetails.fraction_ID = fraction.getFractionID();
                            PbcCorrectionDetails.pbcCorrScheme = ((UPbLAICPMSFraction) fraction).getCommonLeadLossCorrectionScheme().getName();
                            UPbFractionReducer.getInstance().fullFractionReduce(fraction, true);
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
        jMenuItem13 = new javax.swing.JMenuItem();
        newProjectRawDataLAICPMS = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();
        newProjectFromLegacyDataTable_menu = new javax.swing.JMenu();
        genericUPbDataTableInCSV_menuItem = new javax.swing.JMenuItem();
        ucsb_LASS_A_DataTableInCSV_menuItem = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JPopupMenu.Separator();
        manageProject_menuItem = new javax.swing.JMenuItem();
        manageRawData_menuItem = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        exportProjectSamples = new javax.swing.JMenuItem();
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
        editLabTracers = new javax.swing.JMenuItem();
        editLabFractionationModels = new javax.swing.JMenuItem();
        editPbBlanks = new javax.swing.JMenuItem();
        editInitialPbModels = new javax.swing.JMenuItem();
        editPhysicalConstantsModels = new javax.swing.JMenuItem();
        editMineralStandardsModels = new javax.swing.JMenuItem();
        manageRareEarthElementModels = new javax.swing.JMenuItem();
        manageTIMSLabDefaults = new javax.swing.JMenuItem();
        manageLAICPMSLabDefaults = new javax.swing.JMenuItem();
        reportMenu = new javax.swing.JMenu();
        editCurrentReportSettingsModel_menuItem = new javax.swing.JMenuItem();
        jSeparator12 = new javax.swing.JPopupMenu.Separator();
        saveCurrentReportSettingsModelAsLocalXMLFile = new javax.swing.JMenuItem();
        loadReportSettingsModelFromLocalXMLFile = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        saveCurrentReportSettingsAsDefault_menuItem = new javax.swing.JMenuItem();
        loadDefaultReportSettingsModel = new javax.swing.JMenuItem();
        loadEARTHTIMEDefaultReportSettingsModel_menuItem = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        reportResultsTableAsStringsInExcel_menuItem = new javax.swing.JMenuItem();
        reportResultsTableAsNumbersInExcel_menuItem = new javax.swing.JMenuItem();
        reportResultsTableAsNumbersInCSV_menuItem = new javax.swing.JMenuItem();
        reportResultsTableAsPDF_menuItem = new javax.swing.JMenuItem();
        referencesMenu = new javax.swing.JMenu();
        gCubedPaperSoftware_menuItem = new javax.swing.JMenuItem();
        gCubedPaperReductionEquations_menuItem = new javax.swing.JMenuItem();
        uncertaintyEllipses_menuItem = new javax.swing.JMenuItem();
        earthTimeWebSiteMenu = new javax.swing.JMenu();
        visitCIRDLES = new javax.swing.JMenuItem();
        visitEarthTimeOrg = new javax.swing.JMenuItem();
        visitGeochron = new javax.swing.JMenuItem();
        visitGeoSamplesOrg = new javax.swing.JMenuItem();
        toolsMenu = new javax.swing.JMenu();
        reduxPreferences = new javax.swing.JMenuItem();
        templatesForLegacyProjects_menu = new javax.swing.JMenu();
        writeCSVFileOfGenericUPbIsotopicLegacyDataSampleFieldNames_A = new javax.swing.JMenuItem();
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
        buttonBar_panel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
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
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                project_menuMenuSelected(evt);
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
        });

        jMenuItem2.setText("New Empty Project");
        jMenuItem2.setEnabled(false);
        project_menu.add(jMenuItem2);

        newProjectFromRawData_menu.setText("New Project from Raw Data");

        jMenuItem13.setText("ID TIMS");
        jMenuItem13.setEnabled(false);
        newProjectFromRawData_menu.add(jMenuItem13);

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

        genericUPbDataTableInCSV_menuItem.setText("Generic UPb Legacy Data Table in .csv format");
        genericUPbDataTableInCSV_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genericUPbDataTableInCSV_menuItemActionPerformed(evt);
            }
        });
        newProjectFromLegacyDataTable_menu.add(genericUPbDataTableInCSV_menuItem);

        ucsb_LASS_A_DataTableInCSV_menuItem.setText("UCSB LASS A Legacy Data Table in .csv format");
        ucsb_LASS_A_DataTableInCSV_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ucsb_LASS_A_DataTableInCSV_menuItemActionPerformed(evt);
            }
        });
        newProjectFromLegacyDataTable_menu.add(ucsb_LASS_A_DataTableInCSV_menuItem);

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

        exportProjectSamples.setText("Export Project Samples");
        exportProjectSamples.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportProjectSamplesActionPerformed(evt);
            }
        });
        project_menu.add(exportProjectSamples);
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
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                sampleFileMenuMenuSelected(evt);
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
                sampleFileMenuMenuDeselected(evt);
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
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
        aliquotsMenu.setEnabled(false);
        mainMenuBar.add(aliquotsMenu);

        fractionsMenu.setText("Fractions");

        selectAllFractions_menuItem.setText("Select All Fractions");
        selectAllFractions_menuItem.setEnabled(false);
        selectAllFractions_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAllFractions_menuItemActionPerformed(evt);
            }
        });
        fractionsMenu.add(selectAllFractions_menuItem);

        deSelectAllFractions_menuItem.setText("De-select All Fractions");
        deSelectAllFractions_menuItem.setEnabled(false);
        deSelectAllFractions_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deSelectAllFractions_menuItemActionPerformed(evt);
            }
        });
        fractionsMenu.add(deSelectAllFractions_menuItem);

        mainMenuBar.add(fractionsMenu);

        labDataMenu.setText("Lab Data");
        labDataMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
                labDataMenuMenuDeselected(evt);
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
        });

        editLabTracers.setText("Tracer Models");
        editLabTracers.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editLabTracersActionPerformed(evt);
            }
        });
        labDataMenu.add(editLabTracers);

        editLabFractionationModels.setText("Fractionation Models");
        editLabFractionationModels.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editLabFractionationModelsActionPerformed(evt);
            }
        });
        labDataMenu.add(editLabFractionationModels);

        editPbBlanks.setText("Pb Blank IC Models");
        editPbBlanks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editPbBlanksActionPerformed(evt);
            }
        });
        labDataMenu.add(editPbBlanks);

        editInitialPbModels.setText("Initial Pb Models");
        editInitialPbModels.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editInitialPbModelsActionPerformed(evt);
            }
        });
        labDataMenu.add(editInitialPbModels);

        editPhysicalConstantsModels.setText("Physical Constants Models");
        editPhysicalConstantsModels.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editPhysicalConstantsModelsActionPerformed(evt);
            }
        });
        labDataMenu.add(editPhysicalConstantsModels);

        editMineralStandardsModels.setText("Mineral Standards Models");
        editMineralStandardsModels.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editMineralStandardsModelsActionPerformed(evt);
            }
        });
        labDataMenu.add(editMineralStandardsModels);

        manageRareEarthElementModels.setText("Rare Earth Element Models");
        manageRareEarthElementModels.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manageRareEarthElementModelsActionPerformed(evt);
            }
        });
        labDataMenu.add(manageRareEarthElementModels);

        manageTIMSLabDefaults.setText("Manage TIMS Lab Data Defaults");
        manageTIMSLabDefaults.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manageTIMSLabDefaultsActionPerformed(evt);
            }
        });
        labDataMenu.add(manageTIMSLabDefaults);

        manageLAICPMSLabDefaults.setText("Manage LAICPMS Lab Data Defaults");
        manageLAICPMSLabDefaults.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                manageLAICPMSLabDefaultsActionPerformed(evt);
            }
        });
        labDataMenu.add(manageLAICPMSLabDefaults);

        mainMenuBar.add(labDataMenu);

        reportMenu.setText("Reports");
        reportMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
                reportMenuMenuDeselected(evt);
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
        });

        editCurrentReportSettingsModel_menuItem.setText("Edit Current Report Settings");
        editCurrentReportSettingsModel_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editCurrentReportSettingsModel_menuItemActionPerformed(evt);
            }
        });
        reportMenu.add(editCurrentReportSettingsModel_menuItem);
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

        loadEARTHTIMEDefaultReportSettingsModel_menuItem.setText("Load EARTHTIME Default Report Settings Model");
        loadEARTHTIMEDefaultReportSettingsModel_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadEARTHTIMEDefaultReportSettingsModel_menuItemActionPerformed(evt);
            }
        });
        reportMenu.add(loadEARTHTIMEDefaultReportSettingsModel_menuItem);
        reportMenu.add(jSeparator4);

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

        reportResultsTableAsPDF_menuItem.setText("Report Results Table as SVG, PDF");
        reportResultsTableAsPDF_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reportResultsTableAsPDF_menuItemActionPerformed(evt);
            }
        });
        reportMenu.add(reportResultsTableAsPDF_menuItem);

        mainMenuBar.add(reportMenu);

        referencesMenu.setText("References");
        referencesMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
                referencesMenuMenuDeselected(evt);
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
        });

        gCubedPaperSoftware_menuItem.setText("G-Cubed paper on this software");
        gCubedPaperSoftware_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gCubedPaperSoftware_menuItemActionPerformed(evt);
            }
        });
        referencesMenu.add(gCubedPaperSoftware_menuItem);

        gCubedPaperReductionEquations_menuItem.setText("G-Cubed paper on reduction equations");
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

        mainMenuBar.add(referencesMenu);

        earthTimeWebSiteMenu.setText("Web Resources");
        earthTimeWebSiteMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
                earthTimeWebSiteMenuMenuDeselected(evt);
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
        });

        visitCIRDLES.setText("CIRDLES");
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

        visitGeochron.setText("Geochron");
        visitGeochron.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                visitGeochronActionPerformed(evt);
            }
        });
        earthTimeWebSiteMenu.add(visitGeochron);

        visitGeoSamplesOrg.setText("SESAR (IGSN)");
        visitGeoSamplesOrg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                visitGeoSamplesOrgActionPerformed(evt);
            }
        });
        earthTimeWebSiteMenu.add(visitGeoSamplesOrg);

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
        helpMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                helpMenuMouseClicked(evt);
            }
        });
        helpMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
                helpMenuMenuDeselected(evt);
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
        });

        helpMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        helpMenuItem.setText("Help-currently under construction");
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
                .add(jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 528, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(buttonBar_panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void save_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_save_buttonActionPerformed
        try {
            saveTheSampleOrProject();
        } catch (BadLabDataException ex) {
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
            try {
                openTheSample(new File(((AbstractButton) evt.getSource()).getText()), true);
            } catch (BadLabDataException ex) {
            }

        }
    }

    private void editLabTracersActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editLabTracersActionPerformed
        try {
            editLabData(0);
        } catch (BadLabDataException ex) {
            //           ex.printStackTrace();
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
        setupMRUProjectFiles();
        openTheProject(new File(MRUProject_menu.getItem(0).getText()));

    }

    /**
     *
     * @param checkSavedStatus
     */
    public void loadMostRecentSample(boolean checkSavedStatus) {
        setupMRUSampleFiles();
        try {
            openTheSample(new File(MRUSampleMenu.getItem(0).getText()), checkSavedStatus);
        } catch (BadLabDataException ex) {
            System.out.println("No Recent File");
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
        for (JMenuItem jmi : MRUMenuItemList) {
            jmi.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mruSampleMenuItemActionPerformed(evt);
                }
            });
            MRUSampleMenu.add(jmi);
        }

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
    /**
     *
     */
    public void setupMRUProjectFiles() {

        // october 2012
        ArrayList<String> myMRUs = myState.getMRUProjectList();

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
        MRUProject_menu.removeAll();

        // add valid items to menu
        for (JMenuItem jmi : MRUMenuItemList) {
            jmi.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    mruProjectMenuItemActionPerformed(evt);
                }
            });
            MRUProject_menu.add(jmi);
        }

        if (MRUProject_menu.getItemCount() == 0) {
            MRUProject_menu.add(new JMenuItem("<empty>"));
            MRUProject_menu.getItem(0).setEnabled(false);
        }

    }

    private void mruProjectMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        // open the MRU sample file
//        if ( checkSavedStatusTheSample() ) {
//            try {
        openTheProject(new File(((JMenuItem) evt.getSource()).getText()));
//            } catch (BadLabDataException ex) {
//            }

//        }
    }

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        try {
            AboutBoxDisplay();
        } catch (BadLabDataException badLabDataException) {
        }
    }//GEN-LAST:event_aboutMenuItemActionPerformed

    private void AboutBoxDisplay()
            throws BadLabDataException {
        if (!AboutBox.amOpen) {
            myAboutBox = new AboutBox(this, true);
            JDialog.setDefaultLookAndFeelDecorated(true);
            myAboutBox.setVisible(true);
        } else {
        }

    }

    private void manageSampleModel_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manageSampleModel_menuItemActionPerformed
        setLiveUpdateTimerIsRunning(false);

        // feb 2010 simplification
        // first save the existing sample
        theSample.saveTheSampleAsSerializedReduxFile();

        // open the manager
        try {
            manageTheSample();
        } catch (ETException eTException) {
        }

        // setup sample and views with no datareduction flag = true
        try {
            setUpTheSample(!theSample.isAnalyzed());
        } catch (BadLabDataException badLabDataException) {
        }
}//GEN-LAST:event_manageSampleModel_menuItemActionPerformed

    private void saveSampleFileAsActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveSampleFileAsActionPerformed
        try {
            saveSampleFileAs();
        } catch (BadLabDataException ex) {
        }
    }//GEN-LAST:event_saveSampleFileAsActionPerformed

    private void closeSampleFileActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeSampleFileActionPerformed
        try {
            closeTheSample();
        } catch (BadLabDataException ex) {
        }
    }//GEN-LAST:event_closeSampleFileActionPerformed

    private void saveSampleFileActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveSampleFileActionPerformed
        try {
            saveTheSample();
        } catch (BadLabDataException ex) {
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
            }

        }
    }//GEN-LAST:event_openSampleFileActionPerformed

    private void editLabFractionationModelsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editLabFractionationModelsActionPerformed
        try {
            editLabData(1);
        } catch (BadLabDataException ex) {
        }
    }//GEN-LAST:event_editLabFractionationModelsActionPerformed

    private void editPbBlanksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editPbBlanksActionPerformed
        try {
            editLabData(2);
        } catch (BadLabDataException ex) {
        }
}//GEN-LAST:event_editPbBlanksActionPerformed

    private void editInitialPbModelsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editInitialPbModelsActionPerformed
        try {
            editLabData(3);
        } catch (BadLabDataException ex) {
        }
    }//GEN-LAST:event_editInitialPbModelsActionPerformed

    private void editPhysicalConstantsModelsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editPhysicalConstantsModelsActionPerformed
        try {
            editLabData(4);
        } catch (BadLabDataException ex) {
        }
    }//GEN-LAST:event_editPhysicalConstantsModelsActionPerformed

    private void manageTIMSLabDefaultsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manageTIMSLabDefaultsActionPerformed
        try {
            editLabData(7);
        } catch (BadLabDataException ex) {
        }
    }//GEN-LAST:event_manageTIMSLabDefaultsActionPerformed

    private void editMineralStandardsModelsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editMineralStandardsModelsActionPerformed
        try {
            editLabData(5);
        } catch (BadLabDataException ex) {
        }
}//GEN-LAST:event_editMineralStandardsModelsActionPerformed

    private void labDataMenuMenuDeselected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_labDataMenuMenuDeselected
        this.getTheFractionTable().repaint();
    }//GEN-LAST:event_labDataMenuMenuDeselected

    private void sampleFileMenuMenuDeselected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_sampleFileMenuMenuDeselected
        this.getTheFractionTable().repaint();
    }//GEN-LAST:event_sampleFileMenuMenuDeselected

    private void reportMenuMenuDeselected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_reportMenuMenuDeselected
        this.getTheFractionTable().repaint();
    }//GEN-LAST:event_reportMenuMenuDeselected

    private void referencesMenuMenuDeselected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_referencesMenuMenuDeselected
        this.getTheFractionTable().repaint();
    }//GEN-LAST:event_referencesMenuMenuDeselected

    private void earthTimeWebSiteMenuMenuDeselected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_earthTimeWebSiteMenuMenuDeselected
        this.getTheFractionTable().repaint();
    }//GEN-LAST:event_earthTimeWebSiteMenuMenuDeselected

    private void helpMenuMenuDeselected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_helpMenuMenuDeselected
        this.getTheFractionTable().repaint();
    }//GEN-LAST:event_helpMenuMenuDeselected

    private void interpretSampleDates_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_interpretSampleDates_buttonActionPerformed
        manageSampleDateInterpretation(//
                new SampleTreeAnalysisMode(theSample),
                new SampleTreeCompilationMode(theSample));
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
            theSample.updateAndSaveSampleDateModelsByAliquot();

            theSample.saveTheSampleAsSerializedReduxFile();
            myWeightedMeanGraphPanel
                    = new WeightedMeanGraphPanel(theSample);

            theSample.getSampleDateInterpretationGUISettings().//
                    setConcordiaOptions(((AliquotDetailsDisplayInterface) myConcordiaGraphPanel).getConcordiaOptions());
            ((ConcordiaGraphPanel) myConcordiaGraphPanel).//
                    setFadedDeselectedFractions(false);
            theSample.setChanged(true);

            interpretSampleDates_button.setEnabled(false);

            ((ConcordiaGraphPanel) myConcordiaGraphPanel).setShowTightToEdges(true);
            ((PlottingDetailsDisplayInterface) myConcordiaGraphPanel).resetPanel();

            if (sampleDateInterpDialog != null) {
                sampleDateInterpDialog.dispose();
            }
            sampleDateInterpDialog = //
                    new SampleDateInterpretationsManager(
                            this,
                            false,// try floating as of october 2014 true,
                            myConcordiaGraphPanel,
                            myWeightedMeanGraphPanel,
                            myNormedProbabilityPanel,
                            theSample,
                            dateTreeByAliquot,
                            dateTreeBySample);

//            ((ConcordiaGraphPanel) myConcordiaGraphPanel).setShowTightToEdges(true);
//            ((AliquotDetailsDisplayInterface) myConcordiaGraphPanel).resetPanel();
            sampleDateInterpDialog.setVisible(true);

        }

    }

    /**
     * Method to be call when Import MC_ICPMS Excel File is selected
     *
     * @param evt
     */
private void LAICPMS_LegacyAnalysis_MC_UA_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LAICPMS_LegacyAnalysis_MC_UA_menuItemActionPerformed
    try {
        setUpNewSample(SampleTypesEnum.LEGACY.getName(), "LAICPMS_MCUA");
        changeContentOfTopPanel(ReduxConstants.TOP_PANEL_CONTENTS.FRACTIONS);
    } catch (BadLabDataException ex) {
    } catch (ETException ete) {
    }

}//GEN-LAST:event_LAICPMS_LegacyAnalysis_MC_UA_menuItemActionPerformed

private void reduceAll_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reduceAll_buttonActionPerformed
    updateReportTable(true);
}//GEN-LAST:event_reduceAll_buttonActionPerformed

private void reportResultsTableAsStringsInExcel_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reportResultsTableAsStringsInExcel_menuItemActionPerformed
    produceExcelReport(false);
}//GEN-LAST:event_reportResultsTableAsStringsInExcel_menuItemActionPerformed

private void loadReportSettingsModelFromLocalXMLFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadReportSettingsModelFromLocalXMLFileActionPerformed
    myState.setMRUReportSettingsModelFolder(//
            theSample.setReportSettingsModelFromXMLFile(myState.getMRUReportSettingsModelFolder()));

    updateReportTable(false);

}//GEN-LAST:event_loadReportSettingsModelFromLocalXMLFileActionPerformed

private void editCurrentReportSettingsModel_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editCurrentReportSettingsModel_menuItemActionPerformed

    try {
        EditReportSettings();
    } catch (BadLabDataException badLabDataException) {
    }

    updateReportTable(false);
}//GEN-LAST:event_editCurrentReportSettingsModel_menuItemActionPerformed

    private void EditReportSettings()
            throws BadLabDataException {
        if (!ReportSettingsManager.amOpen) {
            myReportSettingsEditor
                    = new ReportSettingsManager(this, true, theSample.getReportSettingsModel());//Initialized());
            myReportSettingsEditor.setSize(455, 685);
            DialogEditor.setDefaultLookAndFeelDecorated(true);
            myReportSettingsEditor.setVisible(true);
        } else {
        }

    }

private void reportResultsTableAsPDF_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reportResultsTableAsPDF_menuItemActionPerformed

    try {
        ((TabbedReportViews) getReportTableTabbedPane()).createReportSVGandPDF(theSample.getSampleName().trim(), true);
    } catch (ETException ex) {
        Logger.getLogger(ETReduxFrame.class.getName()).log(Level.SEVERE, null, ex);
    }
}//GEN-LAST:event_reportResultsTableAsPDF_menuItemActionPerformed

private void visitGeochronActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_visitGeochronActionPerformed
    BrowserControl.displayURL("http://www.geochron.org/");
}//GEN-LAST:event_visitGeochronActionPerformed

private void newSampleCompilation_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newSampleCompilation_menuItemActionPerformed
    try {
        setUpNewSample(SampleTypesEnum.COMPILATION.getName(), SampleAnalysisTypesEnum.COMPILED.getName());
    } catch (BadLabDataException ex) {
    } catch (ETException ete) {
    }
}//GEN-LAST:event_newSampleCompilation_menuItemActionPerformed

private void gCubedPaperReductionEquations_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gCubedPaperReductionEquations_menuItemActionPerformed
    BrowserControl.displayURL(
            "http://www.agu.org/journals/gc/gc1106/2010GC003478/2010GC003478.pdf");

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
                    getMyState().getReduxPreferences());
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
            if (theSample.isTypeAnalysis()) {
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
        if (theSample.isTypeLiveUpdate()) {
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
        try {
            setUpTheSample(false);
        } catch (BadLabDataException badLabDataException) {
        }
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
            theSample.automaticUpdateOfUPbSampleFolder();
        } catch (ETException eTException) {
        }

        rebuildFractionDisplays(false);
    }

private void ID_TIMSLegacyAnalysis_MIT_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ID_TIMSLegacyAnalysis_MIT_menuItemActionPerformed
    try {
        setUpNewSample(SampleTypesEnum.LEGACY.getName(), SampleAnalysisTypesEnum.IDTIMS.getName());
        changeContentOfTopPanel(ReduxConstants.TOP_PANEL_CONTENTS.FRACTIONS);
    } catch (BadLabDataException ex) {
    } catch (ETException ete) {
    }
}//GEN-LAST:event_ID_TIMSLegacyAnalysis_MIT_menuItemActionPerformed

private void visitCIRDLESActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_visitCIRDLESActionPerformed
    BrowserControl.displayURL("https://cirdles.cs.cofc.edu");

}//GEN-LAST:event_visitCIRDLESActionPerformed

private void reportResultsTableAsNumbersInExcel_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reportResultsTableAsNumbersInExcel_menuItemActionPerformed
    produceExcelReport(true);
}//GEN-LAST:event_reportResultsTableAsNumbersInExcel_menuItemActionPerformed

private void writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_MC_USAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_MC_USAActionPerformed
    SampleImporterFromLAICPMSLegacyCVSFile_MC_UA.writeAndOpenCSVFileOfLegacyDataSampleFieldNames();
}//GEN-LAST:event_writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_MC_USAActionPerformed

private void changeLogMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changeLogMenuItemActionPerformed
    showChangeLog();
}//GEN-LAST:event_changeLogMenuItemActionPerformed

private void writeCSVFileOfIDTIMSLegacyDataSampleFieldNames_MITActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_writeCSVFileOfIDTIMSLegacyDataSampleFieldNames_MITActionPerformed
    SampleImporterFromIDTIMSLegacyCSVFile_MIT.writeAndOpenCSVFileOfLegacyDataSampleFieldNames();
}//GEN-LAST:event_writeCSVFileOfIDTIMSLegacyDataSampleFieldNames_MITActionPerformed

private void newSampleAnalysisForIDTIMS_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newSampleAnalysisForIDTIMS_menuItemActionPerformed
    try {
        setUpNewSample(SampleTypesEnum.ANALYSIS.getName(), SampleAnalysisTypesEnum.IDTIMS.getName());
    } catch (BadLabDataException ex) {
    } catch (ETException ete) {
    }
}//GEN-LAST:event_newSampleAnalysisForIDTIMS_menuItemActionPerformed

private void updateData_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateData_buttonActionPerformed
    performSampleDataUpdate();
}//GEN-LAST:event_updateData_buttonActionPerformed

private void selectAllFractions_menuItemActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAllFractions_menuItemActionPerformed
    theSample.selectAllFractions();
    updateReportTable(false);
}//GEN-LAST:event_selectAllFractions_menuItemActionPerformed

private void deSelectAllFractions_menuItemActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deSelectAllFractions_menuItemActionPerformed
    theSample.deSelectAllFractions();
    updateReportTable(false);
}//GEN-LAST:event_deSelectAllFractions_menuItemActionPerformed

private void credits_menuItemActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_credits_menuItemActionPerformed
    showCredits();
}//GEN-LAST:event_credits_menuItemActionPerformed

private void saveCurrentReportSettingsModelAsLocalXMLFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveCurrentReportSettingsModelAsLocalXMLFileActionPerformed
    try {
        String reportSettingsFileParentFolder = theSample.saveReportSettingsToFile(myState.getMRUReportSettingsModelFolder());
        myState.setMRUReportSettingsModelFolder(reportSettingsFileParentFolder);
    } catch (BadLabDataException badLabDataException) {
    }
}//GEN-LAST:event_saveCurrentReportSettingsModelAsLocalXMLFileActionPerformed

private void loadDefaultReportSettingsModelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadDefaultReportSettingsModelActionPerformed
    theSample.restoreDefaultReportSettingsModel();

    updateReportTable(false);
}//GEN-LAST:event_loadDefaultReportSettingsModelActionPerformed

private void writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_SC_WSU_vBActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_SC_WSU_vBActionPerformed
    SampleImporterFromLAICPMSLegacyCVSFile_SC_WSU_vB.writeAndOpenCSVFileOfLegacyDataSampleFieldNames();
}//GEN-LAST:event_writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_SC_WSU_vBActionPerformed

private void LAICPMS_LegacyAnalysis_SC_WSU_vB_menuItemActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LAICPMS_LegacyAnalysis_SC_WSU_vB_menuItemActionPerformed
    try {
        setUpNewSample(SampleTypesEnum.LEGACY.getName(), "LAICPMS_SCWSU_vB");
        changeContentOfTopPanel(ReduxConstants.TOP_PANEL_CONTENTS.FRACTIONS);
    } catch (BadLabDataException ex) {
        ex.printStackTrace();
    } catch (ETException ete) {
    }
}//GEN-LAST:event_LAICPMS_LegacyAnalysis_SC_WSU_vB_menuItemActionPerformed

private void writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_NIGLActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_NIGLActionPerformed
    SampleImporterFromLAICPMSLegacyCVSFile_NIGL.writeAndOpenCSVFileOfLegacyDataSampleFieldNames();
}//GEN-LAST:event_writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_NIGLActionPerformed

private void LAICPMS_LegacyAnalysis_NIGL_menuItemActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LAICPMS_LegacyAnalysis_NIGL_menuItemActionPerformed
    try {
        setUpNewSample(SampleTypesEnum.LEGACY.getName(), "LAICPMS_NIGL");
        changeContentOfTopPanel(ReduxConstants.TOP_PANEL_CONTENTS.FRACTIONS);
    } catch (BadLabDataException ex) {
    } catch (ETException ete) {
    }
}//GEN-LAST:event_LAICPMS_LegacyAnalysis_NIGL_menuItemActionPerformed

private void formComponentResized (java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
    // TODO add your handling code here:
}//GEN-LAST:event_formComponentResized

private void fractionsTabulatedResultsLayeredPaneComponentResized (java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_fractionsTabulatedResultsLayeredPaneComponentResized
    try {
        getReportTableTabbedPane().//
                setSize(fractionsTabulatedResultsLayeredPane.getSize());
    } catch (Exception e) {
    }
}//GEN-LAST:event_fractionsTabulatedResultsLayeredPaneComponentResized

private void writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_SC_WSU_vAActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_SC_WSU_vAActionPerformed
    SampleImporterFromLAICPMSLegacyCVSFile_SC_WSU_vA.writeAndOpenCSVFileOfLegacyDataSampleFieldNames();
}//GEN-LAST:event_writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_SC_WSU_vAActionPerformed

private void LAICPMS_LegacyAnalysis_SC_WSU_vA_menuItemActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LAICPMS_LegacyAnalysis_SC_WSU_vA_menuItemActionPerformed
    try {
        setUpNewSample(SampleTypesEnum.LEGACY.getName(), "LAICPMS_SCWSU_vA");
        changeContentOfTopPanel(ReduxConstants.TOP_PANEL_CONTENTS.FRACTIONS);
    } catch (BadLabDataException ex) {
        ex.printStackTrace();
    } catch (ETException ete) {
    }
}//GEN-LAST:event_LAICPMS_LegacyAnalysis_SC_WSU_vA_menuItemActionPerformed

private void customizeSampleMetadata_menuItemActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_customizeSampleMetadata_menuItemActionPerformed
    if (!GeochronSampleCustomMetadataDialog.amOpen) {
        DialogEditor mySESARSampleCustomMetadataDialog
                = new GeochronSampleCustomMetadataDialog(this, true, theSample);
        ((GeochronSampleCustomMetadataDialog) mySESARSampleCustomMetadataDialog).setSize(440, 685);
        JDialog.setDefaultLookAndFeelDecorated(true);
        mySESARSampleCustomMetadataDialog.setVisible(true);
    } else {
    }
}//GEN-LAST:event_customizeSampleMetadata_menuItemActionPerformed

private void LAICPMS_LegacyAnalysis_SC_WSU_vV_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LAICPMS_LegacyAnalysis_SC_WSU_vV_menuItemActionPerformed
    try {
        setUpNewSample(SampleTypesEnum.LEGACY.getName(), "LAICPMS_SCWSU_vV");
        changeContentOfTopPanel(ReduxConstants.TOP_PANEL_CONTENTS.FRACTIONS);
    } catch (BadLabDataException ex) {
    } catch (ETException ete) {
    }

}//GEN-LAST:event_LAICPMS_LegacyAnalysis_SC_WSU_vV_menuItemActionPerformed

private void newSampleAnalysisForLAICPMS_MC_menuItemActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newSampleAnalysisForLAICPMS_MC_menuItemActionPerformed
    try {
        setUpNewSample(SampleTypesEnum.ANALYSIS.getName(), "LAICPMSMC");
    } catch (BadLabDataException ex) {
    } catch (ETException ete) {
    }
}//GEN-LAST:event_newSampleAnalysisForLAICPMS_MC_menuItemActionPerformed

private void LAICPMS_LegacyAnalysis_UH_menuItemActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LAICPMS_LegacyAnalysis_UH_menuItemActionPerformed
    try {
        setUpNewSample(SampleTypesEnum.LEGACY.getName(), "LAICPMS_UH");
        changeContentOfTopPanel(ReduxConstants.TOP_PANEL_CONTENTS.FRACTIONS);
    } catch (BadLabDataException ex) {
    } catch (ETException ete) {
    }

}//GEN-LAST:event_LAICPMS_LegacyAnalysis_UH_menuItemActionPerformed

    private void saveCurrentReportSettingsAsDefault_menuItemActionPerformed ( java.awt.event.ActionEvent evt ) {//GEN-FIRST:event_saveCurrentReportSettingsAsDefault_menuItemActionPerformed
//        try {
        theSample.getMyReduxLabData().setDefaultreportSettingsModel(theSample.getReportSettingsModel());//Initialized());getReportSettingsModelUpdatedToLatestVersion());
//        } catch (BadLabDataException badLabDataException) {
//        }

    }//GEN-LAST:event_saveCurrentReportSettingsAsDefault_menuItemActionPerformed

    private void loadEARTHTIMEDefaultReportSettingsModel_menuItemActionPerformed ( java.awt.event.ActionEvent evt ) {//GEN-FIRST:event_loadEARTHTIMEDefaultReportSettingsModel_menuItemActionPerformed

        theSample.setReportSettingsModel(ReportSettings.EARTHTIMEReportSettings());

        updateReportTable(false);
    }//GEN-LAST:event_loadEARTHTIMEDefaultReportSettingsModel_menuItemActionPerformed

    private void exit_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exit_menuItemActionPerformed
        if (askAndSaveReduxPersistentState()) {
            System.exit(0);
        }
    }//GEN-LAST:event_exit_menuItemActionPerformed

    private void reportResultsTableAsNumbersInCSV_menuItemActionPerformed ( java.awt.event.ActionEvent evt ) {//GEN-FIRST:event_reportResultsTableAsNumbersInCSV_menuItemActionPerformed
        produceCSVReport();
    }//GEN-LAST:event_reportResultsTableAsNumbersInCSV_menuItemActionPerformed

    private void newProjectRawDataLAICPMSActionPerformed ( java.awt.event.ActionEvent evt ) {//GEN-FIRST:event_newProjectRawDataLAICPMSActionPerformed
        setUpNewProject(SampleAnalysisTypesEnum.TRIPOLIZED.getName(), SampleAnalysisTypesEnum.LAICPMS.getName());
    }//GEN-LAST:event_newProjectRawDataLAICPMSActionPerformed

    private void openProjectFile_menuItemActionPerformed ( java.awt.event.ActionEvent evt ) {//GEN-FIRST:event_openProjectFile_menuItemActionPerformed
        openProjectFile();
    }//GEN-LAST:event_openProjectFile_menuItemActionPerformed

    private void gCubedPaperSoftware_menuItemActionPerformed ( java.awt.event.ActionEvent evt ) {//GEN-FIRST:event_gCubedPaperSoftware_menuItemActionPerformed
        BrowserControl.displayURL(
                "http://www.agu.org/journals/gc/gc1106/2010GC003479/2010GC003479.pdf");
    }//GEN-LAST:event_gCubedPaperSoftware_menuItemActionPerformed

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
//        try {
        setUpNewProject(SampleAnalysisTypesEnum.COMPILED.getName(), SampleAnalysisTypesEnum.GENERIC_UPB.getName());
//            setUpNewSample( SampleTypesEnum.PROJECT.getName(), SampleAnalysisTypesEnum.COMPILED.getName() );
//            changeContentOfTopPanel( ReduxConstants.TOP_PANEL_CONTENTS.FRACTIONS );
//        } catch (BadLabDataException ex) {
//        } catch (ETException ete) {
//        }
    }//GEN-LAST:event_genericUPbDataTableInCSV_menuItemActionPerformed

    private void closeProjectFile_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeProjectFile_menuItemActionPerformed
        try {
            closeTheProject();
        } catch (BadLabDataException badLabDataException) {
        }


    }//GEN-LAST:event_closeProjectFile_menuItemActionPerformed

    private void saveProjectFileAs_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveProjectFileAs_menuItemActionPerformed
        try {
            saveProjectFileAs();
        } catch (BadLabDataException badLabDataException) {
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
//            try {
            openProjectFile();
//            } catch (BadLabDataException ex) {
//            }

        }
    }//GEN-LAST:event_openProject_buttonopenSampleFileActionPerformed

    private void manageProjectRawData_buttonopenSampleFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manageProjectRawData_buttonopenSampleFileActionPerformed
        manageRawDataSession();
    }//GEN-LAST:event_manageProjectRawData_buttonopenSampleFileActionPerformed

    private void ucsb_LASS_A_DataTableInCSV_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ucsb_LASS_A_DataTableInCSV_menuItemActionPerformed
        setUpNewProject(SampleAnalysisTypesEnum.COMPILED.getName(), SampleAnalysisTypesEnum.LASS.getName());
    }//GEN-LAST:event_ucsb_LASS_A_DataTableInCSV_menuItemActionPerformed

    private void producePbCCorrReport_jMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_producePbCCorrReport_jMenuItemActionPerformed
        producePbcCorrectionReport();
    }//GEN-LAST:event_producePbCCorrReport_jMenuItemActionPerformed

    private void manageRareEarthElementModelsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manageRareEarthElementModelsActionPerformed
        try {
            editLabData(6);
        } catch (BadLabDataException ex) {
        }        // TODO add your handling code here:
    }//GEN-LAST:event_manageRareEarthElementModelsActionPerformed

    private void manageLAICPMSLabDefaultsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_manageLAICPMSLabDefaultsActionPerformed
        try {
            editLabData(9);
        } catch (BadLabDataException ex) {
        }        // TODO add your handling code here:
    }//GEN-LAST:event_manageLAICPMSLabDefaultsActionPerformed

    private void helpMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        //Needed for having a nice look in windows... weird
        ETReduxFrame.setDefaultLookAndFeelDecorated(false);
        HelpBroker heb = JHelpAction.getHelpBroker();
        //To Bypass the modal parent window issue.
        ((DefaultHelpBroker) heb).setActivationWindow(this);
        JHelpAction.setHelpBroker(heb);
        JHelpAction.showHelp();
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
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JMenu aliquotsMenu;
    private javax.swing.JPanel buttonBar_panel;
    private javax.swing.JMenuItem changeLogMenuItem;
    private javax.swing.JMenuItem closeProjectFile_menuItem;
    private javax.swing.JMenuItem closeSampleFile;
    private javax.swing.JMenuItem credits_menuItem;
    private javax.swing.JMenuItem customizeSampleMetadata_menuItem;
    private javax.swing.JMenuItem deSelectAllFractions_menuItem;
    private javax.swing.JMenu earthTimeWebSiteMenu;
    private javax.swing.JMenuItem editCurrentReportSettingsModel_menuItem;
    private javax.swing.JMenuItem editInitialPbModels;
    private javax.swing.JMenuItem editLabFractionationModels;
    private javax.swing.JMenuItem editLabTracers;
    private javax.swing.JMenuItem editMineralStandardsModels;
    private javax.swing.JMenuItem editPbBlanks;
    private javax.swing.JMenuItem editPhysicalConstantsModels;
    private javax.swing.JMenuItem exitRedux;
    private javax.swing.JSeparator exitSeparator;
    private javax.swing.JMenuItem exit_menuItem;
    private javax.swing.JMenuItem exportProjectSamples;
    private javax.swing.JMenu fractionsMenu;
    private javax.swing.JLayeredPane fractionsTabulatedResultsLayeredPane;
    private javax.swing.JMenuItem gCubedPaperReductionEquations_menuItem;
    private javax.swing.JMenuItem gCubedPaperSoftware_menuItem;
    private javax.swing.JMenuItem genericUPbDataTableInCSV_menuItem;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuItem helpMenuItem;
    private javax.swing.JButton interpretSampleDates_button;
    private javax.swing.JMenuItem jMenuItem13;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator10;
    private javax.swing.JPopupMenu.Separator jSeparator11;
    private javax.swing.JPopupMenu.Separator jSeparator12;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JPopupMenu.Separator jSeparator9;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JMenu labDataMenu;
    private javax.swing.JMenuItem loadDefaultReportSettingsModel;
    private javax.swing.JMenuItem loadEARTHTIMEDefaultReportSettingsModel_menuItem;
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
    private javax.swing.JMenuItem newProjectRawDataLAICPMS;
    private javax.swing.JMenuItem newSampleAnalysisForIDTIMS_menuItem;
    private javax.swing.JMenuItem newSampleAnalysisForLAICPMS_MC_menuItem;
    private javax.swing.JMenuItem newSampleCompilation_menuItem;
    private javax.swing.JMenu newSampleFileFromLegacyAnalysis_Menu;
    private javax.swing.JMenuItem openProjectFile_menuItem;
    private javax.swing.JButton openProject_button;
    private javax.swing.JMenuItem openSampleFile;
    private javax.swing.JButton openSample_button;
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
    public javax.swing.JButton startStopLiveUpdate_button;
    private javax.swing.JMenu templatesForLegacyProjects_menu;
    private javax.swing.JMenu templatesForLegacySample_menu;
    private javax.swing.JTable theFractionTable;
    private javax.swing.JScrollPane theFractionTableScrollPane;
    private javax.swing.JMenu toolsMenu;
    private javax.swing.JMenuItem ucsb_LASS_A_DataTableInCSV_menuItem;
    private javax.swing.JMenuItem uncertaintyEllipses_menuItem;
    private javax.swing.JButton updateData_button;
    private javax.swing.JMenuItem visitCIRDLES;
    private javax.swing.JMenuItem visitEarthTimeOrg;
    private javax.swing.JMenuItem visitGeoSamplesOrg;
    private javax.swing.JMenuItem visitGeochron;
    private javax.swing.JMenuItem writeCSVFileOfGenericUPbIsotopicLegacyDataSampleFieldNames_A;
    private javax.swing.JMenuItem writeCSVFileOfIDTIMSLegacyDataSampleFieldNames_MIT;
    private javax.swing.JMenuItem writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_MC_USA;
    private javax.swing.JMenuItem writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_NIGL;
    private javax.swing.JMenuItem writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_SC_WSU_vA;
    private javax.swing.JMenuItem writeCSVFileOfLAICPMSLegacyDataSampleFieldNames_SC_WSU_vB;
    // End of variables declaration//GEN-END:variables

}
