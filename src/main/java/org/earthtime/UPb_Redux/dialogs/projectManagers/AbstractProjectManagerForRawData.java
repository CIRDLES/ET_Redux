/*
 * ProjectManagerFor_LAICPMS_FromRawData.java
 *
 *
 * Copyright 2006-2016 James F. Bowring and www.Earth-Time.org
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
package org.earthtime.UPb_Redux.dialogs.projectManagers;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.earthtime.ETReduxFrame;
import org.earthtime.Tripoli.dataModels.DataModelInterface;
import org.earthtime.Tripoli.dataModels.MaskingSingleton;
import org.earthtime.Tripoli.dataModels.RawIntensityDataModel;
import org.earthtime.Tripoli.dataModels.inputParametersModels.AbstractAcquisitionModel;
import org.earthtime.Tripoli.fractions.TripoliFraction;
import org.earthtime.Tripoli.massSpecSetups.AbstractMassSpecSetup;
import org.earthtime.Tripoli.rawDataFiles.handlers.AbstractRawDataFileHandler;
import org.earthtime.Tripoli.rawDataFiles.templates.AbstractRawDataFileTemplate;
import org.earthtime.Tripoli.rawDataFiles.templates.shrimp.ShrimpPrawn_RawDataTemplate_GA;
import org.earthtime.Tripoli.samples.AbstractTripoliSample;
import org.earthtime.Tripoli.sessions.TripoliSession;
import org.earthtime.Tripoli.sessions.TripoliSessionInterface;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.ReduxConstants.ANALYSIS_PURPOSE;
import org.earthtime.UPb_Redux.dialogs.sessionManagers.SessionAnalysisWorkflowManagerInterface;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.UPb_Redux.user.ReduxPersistentState;
import org.earthtime.UPb_Redux.utilities.BrowserControl;
import org.earthtime.UPb_Redux.utilities.ETSerializer;
import org.earthtime.beans.ET_JButton;
import org.earthtime.dialogs.DialogEditor;
import org.earthtime.exceptions.ETException;
import org.earthtime.exceptions.ETWarningDialog;
import org.earthtime.projects.Project;
import org.earthtime.projects.ProjectInterface;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.reduxLabData.ReduxLabData;
import org.earthtime.utilities.TimeToString;

/**
 *
 * @author James F. Bowring
 *
 */
public abstract class AbstractProjectManagerForRawData extends DialogEditor implements ProjectManagerSubscribeInterface {

    protected SamplesOrganizerPane samplesOrganizerPane;
    protected SamplesCommonLeadPane samplesCommonLeadPane;
    protected final ReduxPersistentState myState;
    protected ProjectInterface project;
    protected TripoliSessionInterface tripoliSession;
    protected AbstractRawDataFileHandler rawDataFileHandler;
    protected ArrayList<AbstractRawDataFileHandler> knownRawDataFileHandlers;
    protected ArrayList<AbstractTripoliSample> tripoliSamplesSorted;
    protected final ETReduxFrame uPbReduxFrame;
    protected SessionAnalysisWorkflowManagerInterface mySessionManager;
    protected transient SwingWorker loadDataTask;
    protected boolean amChanged;
    protected JLayeredPane parametersView;

    /**
     * Creates new form ProjectManagerFor_LAICPMS_FromRawData
     *
     * @param parent
     * @param modal
     * @param myState
     * @param project
     */
    public AbstractProjectManagerForRawData(//
            ETReduxFrame parent, //
            boolean modal,
            ReduxPersistentState myState, //
            Project project) {
        super(parent, modal);

        this.uPbReduxFrame = parent;

        this.myState = myState;
        this.project = project;

        initComponents();

        loadDataTaskProgressBar.setVisible(false);
        rawDataFileChosen_scrollPane.setVisible(false);
    }

    @Override
    public void initDialogContent() {
        fileHandlerComboBox.removeAllItems();
        for (int i = 0; i < knownRawDataFileHandlers.size(); i++) {
            fileHandlerComboBox.addItem( //
                    knownRawDataFileHandlers.get(i));
        }

        fileHandlerComboBox.addActionListener((ActionEvent e) -> {
            AbstractRawDataFileHandler fileHandler = ((AbstractRawDataFileHandler) fileHandlerComboBox.getSelectedItem());

            rawDataTemplateComboBox.removeAllItems();
            SortedSet<AbstractRawDataFileTemplate> templates = fileHandler.getAvailableRawDataFileTemplates();
            Iterator<AbstractRawDataFileTemplate> templatesIterator = templates.iterator();
            while (templatesIterator.hasNext()) {
                rawDataTemplateComboBox.addItem(templatesIterator.next());
            }
        });

        rawDataTemplateComboBox.addActionListener((ActionEvent e) -> {
            AbstractRawDataFileHandler fileHandler = ((AbstractRawDataFileHandler) fileHandlerComboBox.getSelectedItem());
            AbstractRawDataFileTemplate filetemplate = ((AbstractRawDataFileTemplate) rawDataTemplateComboBox.getSelectedItem());
            try {
                aboutInfo_textPanel.setText(
                        fileHandler.getAboutInfo() //
                        + "  This handler will expect raw data files for "//
                        + filetemplate.getAboutInfo()//
                        + ".");
            } catch (Exception badTemplate) {
            }
        });

        // set up analysisPurposeChooser
        analysisPurposeChooser.removeAllItems();
        for (ReduxConstants.ANALYSIS_PURPOSE ap : ReduxConstants.ANALYSIS_PURPOSE.values()) {
            analysisPurposeChooser.addItem(ap.toString());
        }

        analysisPurposeChooser.addItemListener(new AnalysisPurposeItemListener());

        loadProject();

        amChanged = false;
    }

    /**
     *
     * @param amChanged the value of amChanged
     */
    @Override
    public void updateDataChangeStatus(boolean amChanged) {
        loadRawData_button.setEnabled(tripoliSession == null);
        saveProject_button.setEnabled(amChanged);
        revertProjectChanges_button.setEnabled(amChanged && (tripoliSession != null));
        openRawDataManager_button.setEnabled(!amChanged);

        project.setProjectName(projectName_text.getText().trim());

        this.amChanged = amChanged;
    }

    class AnalysisPurposeItemListener implements ItemListener {
        // This method is called only if a new item has been selected.

        @Override
        public void itemStateChanged(ItemEvent evt) {

            if (evt.getStateChange() == ItemEvent.SELECTED) {
                // Item was just selected
                project.setAnalysisPurpose(ANALYSIS_PURPOSE.valueOf((String) evt.getItem()));

            } else if (evt.getStateChange() == ItemEvent.DESELECTED) {
                // Item is no longer selected
            }
        }
    }

    protected void loadProject() {

        tripoliSession = project.getTripoliSession();

        projectName_text.setDocument(
                new UnDoAbleDocument(projectName_text, true));
        projectName_text.setText(project.getProjectName());
        projectName_text.getDocument().addDocumentListener(new TextDocChangeListener());

        rawDataFileHandler = project.getRawDataFileHandler();

        boolean rawDataFileHandlerExists = rawDataFileHandler != null;

        if (rawDataFileHandlerExists) {
            fileHandlerComboBox.setSelectedItem(rawDataFileHandler);

            rawDataTemplateComboBox.setSelectedItem(rawDataFileHandler.getRawDataFileTemplate());
        } else {

            fileHandlerComboBox.setSelectedIndex(0);

            rawDataTemplateComboBox.setSelectedIndex(0);
        }

        try {
            analysisPurposeChooser.setSelectedItem(this.project.getAnalysisPurpose().toString());
        } catch (Exception e) {
        }

        if (tripoliSession != null) {

            // update parameters
//            rawDataFileHandler.updateMassSpecFromAcquisitionModel();
            tripoliSamplesSorted = tripoliSession.getTripoliSamples();

            displayRawDataSamples();
        }

        fileHandlerComboBox.setEnabled(!rawDataFileHandlerExists);
        rawDataTemplateComboBox.setEnabled(!rawDataFileHandlerExists);

        manageButtons(project.getRawDataFileHandler() != null, rawDataFileHandlerExists, false);

    }

    private void saveProjectFields() {
        project.setProjectName(projectName_text.getText().trim());
        try {
            uPbReduxFrame.updateProjectDisplayTitleBar();
        } catch (Exception e) {
        }

        // may 2013 need to catch renaming of samples
        try {
            samplesOrganizerPane.saveChanges();
        } catch (Exception e) {
        }
    }

    private void revertProject() {
        try {
            project
                    = (ProjectInterface) ETSerializer.GetSerializedObjectFromFile(//
                            project.getLocationOfProjectReduxFile().getAbsolutePath());

            loadProject();

            amChanged = false;
        } catch (Exception e) {
        }
    }

    private void saveProject() {

        saveProjectFields();
        // oct 2014 - to refresh panels
        displayRawDataSamples();

        project.saveTheProjectAsSerializedReduxFile();
        myState.setMRUProjectFile(project.getLocationOfProjectReduxFile());
    }

    private void saveProjectAs() {
        boolean saveChanged = saveProject_button.isEnabled();

        saveProjectFields();
        project.saveProjectFileAs();
        myState.setMRUProjectFile(project.getLocationOfProjectReduxFile());

        amChanged = saveChanged;
    }

    private synchronized void loadRawDataWorkflow() {

        // lets garbage collect
        rawDataFileHandler = null;
        tripoliSession = null;

        System.gc();

        // user-selected handler
        rawDataFileHandler
                = (AbstractRawDataFileHandler) fileHandlerComboBox.getSelectedItem();
        project.setRawDataFileHandler(rawDataFileHandler);
        rawDataFileHandler.reInitialize();

        // user selected raw data template
        AbstractRawDataFileTemplate rawDataFileTemplate
                = (AbstractRawDataFileTemplate) rawDataTemplateComboBox.getSelectedItem();
        AbstractMassSpecSetup massSpec = rawDataFileTemplate.getMassSpecSetup();
        rawDataFileHandler.setMassSpec(massSpec);

        rawDataFileHandler.setRawDataFileTemplate(rawDataFileTemplate);

        // may 2013 split task to allow for custom parameters ********************
        AbstractAcquisitionModel acquisitionModel = rawDataFileTemplate.makeNewAcquisitionModel();
        if (rawDataFileTemplate instanceof ShrimpPrawn_RawDataTemplate_GA) {
//                if (acquisitionModel.getPrimaryMineralStandardModel() == null) {
//                    acquisitionModel.setPrimaryMineralStandardModel(ReduxLabData.getInstance().getDefaultLAICPMSPrimaryMineralStandardModel());
//                }
                acquisitionModel.setLeftShadeCount(ReduxLabData.getInstance().getDefaultLeftShadeCountForSHRIMPAquisitions());

        } else {
            try {
                if (acquisitionModel.getPrimaryMineralStandardModel() == null) {
                    acquisitionModel.setPrimaryMineralStandardModel(ReduxLabData.getInstance().getDefaultLAICPMSPrimaryMineralStandardModel());
                }
                acquisitionModel.setLeftShadeCount(ReduxLabData.getInstance().getDefaultLeftShadeCountForLAICPMSAquisitions());

            } catch (BadLabDataException ex) {
                new ETWarningDialog(ex).setVisible(true);
            }
        }

        project.setAcquisitionModel(acquisitionModel);

        // user selects raw data file(s)
        File rawDataFile = rawDataFileHandler.validateAndGetHeaderDataFromRawIntensityFile(//
                myState.getMRUTripoliRawDataFolder());

        if (rawDataFile != null) {
            try {
                rawDataFileChosen_scrollPane.setVisible(true);
                rawDataFileChosen_textArea.setText("Raw Data File: " + rawDataFile.getCanonicalPath());
            } catch (IOException ex) {
                Logger.getLogger(AbstractProjectManagerForRawData.class.getName()).log(Level.SEVERE, null, ex);
            }
            // here we present the data setup frame and re-update massspec with any changes
            rawDataFileHandler.updateAcquisitionModelWithRawDataFile();

            try {
                myState.setMRUTripoliRawDataFolder(rawDataFile.getParentFile().toString());
            } catch (Exception e) {
            }

            manageButtons(true, true, true);

            if (showParametersView()) {

                fireLoadDataTask();
                manageButtons(true, true, true);
            }
        } else {
            manageButtons(false, false, false);
        }
    }

    private void fireLoadDataTask() {
        boolean isUsingFullPropagation = rawDataFileHandler.getAcquisitionModel().isUsingFullPropagation();

        int leftShadeCount = rawDataFileHandler.getAcquisitionModel().getLeftShadeCount();

        loadDataTask = new LoadDataTask(isUsingFullPropagation, leftShadeCount, 0);
        loadDataTask.addPropertyChangeListener(new LoadRawDataPropertyChangeListener());
        loadDataTask.execute();

        //comment out next line to allow for threading
        //loadAndShowRawData(rawDataFileHandler.getAcquisitionModel().isUsingFullPropagation(), 0);
    }

    /**
     *
     *
     * @param usingFullPropagation the value of usingFullPropagation
     * @param ignoreFirstFractions the value of ignoreFirstFractions
     */
    private void loadAndShowRawData(boolean usingFullPropagation, int leftShadeCount, int ignoreFirstFractions) {
        // april 2014
        MaskingSingleton.getInstance().setLeftShadeCount(leftShadeCount);
        MaskingSingleton.getInstance().setRightShadeCount(-1);

        rawDataFileHandler.getAndLoadRawIntensityDataFile(loadDataTask, usingFullPropagation, leftShadeCount, ignoreFirstFractions, false);
    }

    private void loadAndShowRawDataFinishUp() {

        rawDataFileHandler.updateAcquisitionModelWithRawDataFileProcessedFlag(true);

        // now use abstractRawDataFileHandler to parse tripoliFractions into standards and samples that will
        // be displayed and manipulated graphically and then used to drive workflow organization
        tripoliSamplesSorted = rawDataFileHandler.parseFractionsIntoSamples();

        if (tripoliSamplesSorted.size() > 0) {
            // create session
            tripoliSession
                    = new TripoliSession(//
                            rawDataFileHandler, tripoliSamplesSorted);

            tripoliSession.setPrimaryMineralStandard(project.getAcquisitionModel().getPrimaryMineralStandardModel());
            tripoliSession.setLeftShadeCount(project.getAcquisitionModel().getLeftShadeCount());

            displaySamples();
        }

    }

    private void displaySamples() {

        if (tripoliSession != null) {
            project.setTripoliSession(tripoliSession);

            project.saveTheProjectAsSerializedReduxFile();

            displayRawDataSamples();

            manageButtons(true, true, false);// amChanged);//   false);
        }
    }

    /**
     *
     * @param tripoliSession the value of tripoliSession
     * @param tripoliSamplesSorted the value of tripoliSamplesSorted
     */
    @Override
    public void displaySamples(TripoliSessionInterface tripoliSession, ArrayList<AbstractTripoliSample> tripoliSamplesSorted) {
        this.tripoliSession = tripoliSession;
        this.tripoliSamplesSorted = tripoliSamplesSorted;
        displaySamples();
    }

    public boolean showParametersView() {
        return false;
    }

    /**
     *
     * @param usingFullPropagation
     */
    @Override
    public void reProcessFractionRawRatios(boolean usingFullPropagation) {
        AbstractMassSpecSetup myMassSpec = project.getTripoliSession().getMassSpec();

        SortedSet<TripoliFraction> tripoliFractions = project.getTripoliSession().getTripoliFractions();

        Iterator<TripoliFraction> tripoliFractionsIterator = tripoliFractions.iterator();
        while (tripoliFractionsIterator.hasNext()) {
            TripoliFraction tf = tripoliFractionsIterator.next();
            myMassSpec.setRawRatios(tf.getRawRatios());
            myMassSpec.reProcessFractionRawRatios(usingFullPropagation, tf.getFractionID(), tf);
        }

        tripoliSession.calculateSessionFitFunctionsForPrimaryStandard(false);
        // jan 2015 moved to calculate sessionfittripoliSession.applyCorrections();

        try {
            uPbReduxFrame.updateReportTable(true);
        } catch (Exception e) {
        }

        initializeSessionManager(true, false, true);
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
                loadDataTaskProgressBar.validate();
            } else if ("projectName".equalsIgnoreCase(pce.getPropertyName())) {
                project.setProjectName((String) pce.getNewValue());
                projectName_text.setText(project.getProjectName());
            }
        }
    }

    /**
     *
     */
    public class LoadDataTask extends SwingWorker<Void, Void> {

        /*
         * Main loadDataTask. Executed in background thread.
         */
        private final boolean usingFullPropagation;
        private final int ignoreFirstFractions;
        private final int leftShadeCount;

        /**
         *
         * @param usingFullPropagation
         * @param leftShadeCount
         * @param ignoreFirstFractions
         */
        public LoadDataTask(boolean usingFullPropagation, int leftShadeCount, int ignoreFirstFractions) {
            this.usingFullPropagation = usingFullPropagation;
            this.ignoreFirstFractions = ignoreFirstFractions;
            this.leftShadeCount = leftShadeCount;
        }

        @Override
        public Void doInBackground() {
            //Initialize progress property.
            loadDataTaskProgressBar.setVisible(true);
            setProgress(0);
            manageButtons(false, false, false);
            loadAndShowRawData(usingFullPropagation, leftShadeCount, ignoreFirstFractions);
            return null;
        }

        /*
         * Executed in event dispatching thread
         */
        @Override
        public void done() {
            loadDataTaskProgressBar.setVisible(false);
//            manageButtons(true, true, false);//true);
            System.out.println("LOADING TASK DONE !!");
            loadAndShowRawDataFinishUp();
        }
    }

    private void manageButtons(boolean rawDataFileSelected, boolean projectDataLoaded, boolean iAmChanged) {

        loadRawData_button.setEnabled(!rawDataFileSelected);

        saveProjectAs_button.setEnabled(projectDataLoaded);
        setDataParameters_button.setEnabled(projectDataLoaded);
        saveProject_button.setEnabled(false);
        revertProjectChanges_button.setEnabled(false);
        openRawDataManager_button.setEnabled(!iAmChanged && (tripoliSession != null));
        outputDataForNoah_button.setEnabled(!iAmChanged && (tripoliSession != null));
    }

    private void displayRawDataSamples() {
        // cleanup
        if (samplesOrganizerPane != null) {
            projectSampleWorkPane.remove(samplesOrganizerPane);
        }

        if (samplesCommonLeadPane != null) {
            projectSampleCommonLeadWorkPane.remove(samplesCommonLeadPane);
        }

        // setup samplesOrganizerPane         
        samplesOrganizerPane
                = new SamplesOrganizerPane(//
                        "Organize Samples:", 10, 10, this.getWidth() - 25, projectSampleWorkPane.getHeight() - 25, tripoliSamplesSorted, this);
        projectSampleWorkPane.add(samplesOrganizerPane, JLayeredPane.DRAG_LAYER);

        // setup samplesCommonLeadPane         
        samplesCommonLeadPane
                = new SamplesCommonLeadPane(//
                        10, 10, this.getWidth() - 25, //
                        projectSampleCommonLeadWorkPane.getHeight() - 25, //
                        tripoliSamplesSorted, //
                        tripoliSession.getCommonLeadCorrectionHighestLevelFromMasspec(), //
                        this);
        projectSampleCommonLeadWorkPane.add(samplesCommonLeadPane, JLayeredPane.DRAG_LAYER);

    }

    private void processRawData() throws ETException {
        if (rawDataFileHandler != null) {

            // first align standard status of all fractions
            tripoliSession.updateFractionsToSampleMembership();

            // april 2012
            // save mineral standard model choice and update all raw ratios with standard value
            AbstractRatiosDataModel primaryMineralStandard = tripoliSamplesSorted.get(0).getMineralStandardModel();
            if (primaryMineralStandard == null) {
                throw new ETException(null, "Please choose mineral standard model for primary standard.");
            } else {

                tripoliSession.setPrimaryMineralStandard(primaryMineralStandard);

                tripoliSession.prepareFractionTimeStamps();
                tripoliSession.processRawData(false);

                tripoliSession.postProcessDataForCommonLeadLossPreparation();
            }

        }
    }

    /**
     * This method is called from within the constructor to reInitialize the
     * form. WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JLayeredPane();
        projectName_label = new javax.swing.JLabel();
        fileHandlerComboBox = new javax.swing.JComboBox<>();
        loadRawData_button =  new ET_JButton();
        fileProtocol_label = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        aboutInfo_textPanel = new javax.swing.JTextPane();
        jLabel6 = new javax.swing.JLabel();
        rawDataTemplateComboBox = new javax.swing.JComboBox<>();
        projectType_label = new javax.swing.JLabel();
        projectName_text = new javax.swing.JTextField();
        analysisPurposeChooser = new javax.swing.JComboBox<>();
        title_label = new javax.swing.JLabel();
        projectFinalizationPane = new javax.swing.JLayeredPane();
        openRawDataManager_button =  new ET_JButton("Display Processed Raw Data");
        closeProjectManager_button =  new ET_JButton("Close");
        saveProjectAs_button =  new ET_JButton("Save Project");
        outputDataForNoah_button =  new ET_JButton();
        setDataParameters_button =  new ET_JButton("Display Processed Raw Data");
        saveProject_button =  new ET_JButton("Display Processed Raw Data");
        revertProjectChanges_button =  new ET_JButton("Display Processed Raw Data");
        manager_JTab = new javax.swing.JTabbedPane();
        projectSampleWorkPane = new javax.swing.JLayeredPane();
        noteToUser_label = new javax.swing.JLabel();
        loadDataTaskProgressBar = new javax.swing.JProgressBar();
        rawDataFileChosen_scrollPane = new javax.swing.JScrollPane();
        rawDataFileChosen_textArea = new javax.swing.JTextArea();
        projectSampleCommonLeadWorkPane = new javax.swing.JLayeredPane();
        noteToUser_label1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModalityType(java.awt.Dialog.ModalityType.APPLICATION_MODAL);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });

        mainPanel.setBackground(new java.awt.Color(255, 255, 255));
        mainPanel.setOpaque(true);

        projectName_label.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        projectName_label.setText("Project Name:");
        mainPanel.add(projectName_label);
        projectName_label.setBounds(10, 7, 110, 17);

        fileHandlerComboBox.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        mainPanel.add(fileHandlerComboBox);
        fileHandlerComboBox.setBounds(220, 30, 380, 27);

        loadRawData_button.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        loadRawData_button.setText("Select Raw Data to Load and Process with chosen Protocol and Raw Data Template");
        loadRawData_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadRawData_buttonActionPerformed(evt);
            }
        });
        mainPanel.add(loadRawData_button);
        loadRawData_button.setBounds(10, 125, 580, 29);

        fileProtocol_label.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        fileProtocol_label.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        fileProtocol_label.setText("Choose file handling protocol:");
        mainPanel.add(fileProtocol_label);
        fileProtocol_label.setBounds(20, 35, 195, 16);

        jLabel2.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Choose analysis purpose:");
        mainPanel.add(jLabel2);
        jLabel2.setBounds(40, 95, 170, 16);

        aboutInfo_textPanel.setEditable(false);
        aboutInfo_textPanel.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        jScrollPane1.setViewportView(aboutInfo_textPanel);

        mainPanel.add(jScrollPane1);
        jScrollPane1.setBounds(620, 30, 520, 100);

        jLabel6.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("Choose raw data template:");
        mainPanel.add(jLabel6);
        jLabel6.setBounds(20, 65, 195, 16);

        rawDataTemplateComboBox.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        rawDataTemplateComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rawDataTemplateComboBoxActionPerformed(evt);
            }
        });
        mainPanel.add(rawDataTemplateComboBox);
        rawDataTemplateComboBox.setBounds(220, 60, 380, 27);

        projectType_label.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        projectType_label.setText("Project Type: ?? Data aquisition and reduction");
        mainPanel.add(projectType_label);
        projectType_label.setBounds(360, 7, 530, 17);

        projectName_text.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        projectName_text.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projectName_textActionPerformed(evt);
            }
        });
        mainPanel.add(projectName_text);
        projectName_text.setBounds(120, 0, 230, 26);

        analysisPurposeChooser.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        mainPanel.add(analysisPurposeChooser);
        analysisPurposeChooser.setBounds(220, 90, 220, 27);

        title_label.setBackground(new java.awt.Color(153, 255, 204));
        title_label.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        title_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        title_label.setText("PROJECT MANAGER");
        title_label.setOpaque(true);

        projectFinalizationPane.setBackground(new java.awt.Color(255, 255, 255));
        projectFinalizationPane.setOpaque(true);
        projectFinalizationPane.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        openRawDataManager_button.setText("Save and Open Raw Data Manager");
        openRawDataManager_button.setEnabled(false);
        openRawDataManager_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openRawDataManager_buttonActionPerformed(evt);
            }
        });
        projectFinalizationPane.add(openRawDataManager_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 4, 250, -1));

        closeProjectManager_button.setText("Close");
        closeProjectManager_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeProjectManager_buttonActionPerformed(evt);
            }
        });
        projectFinalizationPane.add(closeProjectManager_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(960, 4, 80, -1));

        saveProjectAs_button.setText("Save Project As");
        saveProjectAs_button.setEnabled(false);
        saveProjectAs_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveProjectAs_buttonActionPerformed(evt);
            }
        });
        projectFinalizationPane.add(saveProjectAs_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(170, 4, 120, -1));

        outputDataForNoah_button.setText("DataDump");
        outputDataForNoah_button.setEnabled(false);
        outputDataForNoah_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outputDataForNoah_buttonActionPerformed(evt);
            }
        });
        projectFinalizationPane.add(outputDataForNoah_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(1040, 4, 110, -1));

        setDataParameters_button.setText("Manage Parameters and Processing");
        setDataParameters_button.setEnabled(false);
        setDataParameters_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setDataParameters_buttonActionPerformed(evt);
            }
        });
        projectFinalizationPane.add(setDataParameters_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 4, 250, -1));

        saveProject_button.setText("Save Project");
        saveProject_button.setEnabled(false);
        saveProject_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveProject_buttonActionPerformed(evt);
            }
        });
        projectFinalizationPane.add(saveProject_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 4, 110, -1));

        revertProjectChanges_button.setText("Revert Project Changes");
        revertProjectChanges_button.setEnabled(false);
        revertProjectChanges_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                revertProjectChanges_buttonActionPerformed(evt);
            }
        });
        projectFinalizationPane.add(revertProjectChanges_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 4, 170, -1));

        manager_JTab.setBackground(new java.awt.Color(153, 255, 204));
        manager_JTab.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        manager_JTab.setPreferredSize(new java.awt.Dimension(1180, 67));

        projectSampleWorkPane.setBackground(new java.awt.Color(244, 244, 244));
        projectSampleWorkPane.setOpaque(true);

        noteToUser_label.setBackground(new java.awt.Color(236, 255, 238));
        noteToUser_label.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        noteToUser_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        noteToUser_label.setText("After your data file is loaded, you can organize and name your samples and standards here.");
        projectSampleWorkPane.add(noteToUser_label);
        noteToUser_label.setBounds(260, 50, 680, 30);

        loadDataTaskProgressBar.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        loadDataTaskProgressBar.setStringPainted(true);
        projectSampleWorkPane.add(loadDataTaskProgressBar);
        loadDataTaskProgressBar.setBounds(330, 180, 540, 20);

        rawDataFileChosen_scrollPane.setBorder(null);
        rawDataFileChosen_scrollPane.setOpaque(false);

        rawDataFileChosen_textArea.setEditable(false);
        rawDataFileChosen_textArea.setBackground(new java.awt.Color(244, 244, 244));
        rawDataFileChosen_textArea.setColumns(1);
        rawDataFileChosen_textArea.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        rawDataFileChosen_textArea.setLineWrap(true);
        rawDataFileChosen_textArea.setRows(1);
        rawDataFileChosen_textArea.setText("Raw Data File:");
        rawDataFileChosen_textArea.setWrapStyleWord(true);
        rawDataFileChosen_textArea.setBorder(null);
        rawDataFileChosen_scrollPane.setViewportView(rawDataFileChosen_textArea);

        projectSampleWorkPane.add(rawDataFileChosen_scrollPane);
        rawDataFileChosen_scrollPane.setBounds(134, 104, 930, 60);

        manager_JTab.addTab("Organize Reference Materials (Standards) and Samples", projectSampleWorkPane);

        projectSampleCommonLeadWorkPane.setBackground(new java.awt.Color(244, 244, 244));
        projectSampleCommonLeadWorkPane.setOpaque(true);

        noteToUser_label1.setBackground(new java.awt.Color(236, 255, 238));
        noteToUser_label1.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        noteToUser_label1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        noteToUser_label1.setText("Assign Common Lead Models to Fractions here.");
        projectSampleCommonLeadWorkPane.add(noteToUser_label1);
        noteToUser_label1.setBounds(360, 120, 400, 30);

        manager_JTab.addTab("Common Lead Assignment", projectSampleCommonLeadWorkPane);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(title_label, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(mainPanel)
            .add(manager_JTab, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(projectFinalizationPane)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(title_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 40, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, 0)
                .add(mainPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 160, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(manager_JTab, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 484, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(projectFinalizationPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 42, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void loadRawData_buttonActionPerformed ( java.awt.event.ActionEvent evt ) {//GEN-FIRST:event_loadRawData_buttonActionPerformed

        loadRawDataWorkflow();
    }//GEN-LAST:event_loadRawData_buttonActionPerformed

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        title_label.setPreferredSize(new Dimension(((Component) evt.getSource()).getWidth(), title_label.getHeight()));
        title_label.setSize(new Dimension(((Component) evt.getSource()).getWidth(), title_label.getHeight()));
        mainPanel.setSize(this.getWidth(), mainPanel.getHeight());
        projectSampleWorkPane.setSize(this.getWidth(), projectSampleWorkPane.getHeight());
        try {
            samplesOrganizerPane.setMyWidth(this.getWidth() - 25);
            samplesOrganizerPane.refreshSampleFractionListsPane();
            samplesCommonLeadPane.setMyWidth(this.getWidth() - 25);
            samplesCommonLeadPane.refreshSampleFractionListsPane();
        } catch (Exception e) {
        }
        projectFinalizationPane.setSize(//
                this.getWidth(),//
                this.getHeight() - mainPanel.getHeight() - projectSampleWorkPane.getHeight() - 50);
    }//GEN-LAST:event_formComponentResized

    private void openRawDataManager_buttonActionPerformed ( java.awt.event.ActionEvent evt ) {//GEN-FIRST:event_openRawDataManager_buttonActionPerformed

        if (amChanged || !tripoliSession.isDataProcessed()) {//  || !tripoliSession.isCalculatedInitialFitFunctions()) {
            // march 2014 on return from monitoring need to capture session
            tripoliSession = project.getTripoliSession();
            tripoliSamplesSorted = tripoliSession.getTripoliSamples();

            saveProjectFields();

//            try {
            // processRawData();
// feb 2016 temporarily for shrimp (next two calls are in processrawdata
            tripoliSession.prepareFractionTimeStamps();
            tripoliSession.updateFractionsToSampleMembership();

            project.prepareSamplesForRedux();

            uPbReduxFrame.initializeProject();

            initializeSessionManager(true, true, true);
//            } catch (ETException ex) {
//                new ETWarningDialog(ex).setVisible(true);
//            }
        } else {
            refreshMaskingArray();
            initializeSessionManager(false, true, true);
        }
    }//GEN-LAST:event_openRawDataManager_buttonActionPerformed

    /**
     *
     * @param doSetup the value of doSetup
     * @param doShow the value of doShow
     * @param doCorrections the value of doCorrections
     */
    @Override
    public void initializeSessionManager(boolean doSetup, boolean doShow, boolean doCorrections) {
    }

    private void closeProjectManager_buttonActionPerformed ( java.awt.event.ActionEvent evt ) {//GEN-FIRST:event_closeProjectManager_buttonActionPerformed

        if (loadDataTask == null) {
            close();
        } else {
            try {
                if (!loadDataTask.isDone()) {
                    JOptionPane jopt = new JOptionPane();
                    int userChoice = JOptionPane.showConfirmDialog(//
                            this,//
                            "U-Pb_Redux is still loading data, do you really want to quit?",//
                            "Confirm close?",//
                            JOptionPane.YES_NO_OPTION);
                    if (userChoice == JOptionPane.YES_OPTION) {
                        loadDataTask.cancel(true);
                        close();
                    }
                } else {
                    close();
                }
            } catch (NullPointerException nullPointerException) {
            }
        }
    }//GEN-LAST:event_closeProjectManager_buttonActionPerformed

    private void saveProjectAs_buttonActionPerformed ( java.awt.event.ActionEvent evt ) {//GEN-FIRST:event_saveProjectAs_buttonActionPerformed
        saveProjectAs();
    }//GEN-LAST:event_saveProjectAs_buttonActionPerformed

    private void outputDataForNoah_buttonActionPerformed ( java.awt.event.ActionEvent evt ) {//GEN-FIRST:event_outputDataForNoah_buttonActionPerformed
        saveProject();

        // this retrieval also provides a test of whether data was correctly harvested
        // walk samples and set fraction standard flags 
        for (int i = 0; i < tripoliSamplesSorted.size(); i++) {
            tripoliSamplesSorted.get(i).setFractionsSampleFlags();
        }

        NumberFormat formatter2 = new DecimalFormat("0.000000000E0");
//        long firstFractionBackgroundTimeStamp = tripoliSamplesSorted.get(0).getSampleFractions().first().getBackgroundTimeStamp();
//        long firstFractionPeakTimeStamp = tripoliSamplesSorted.get(0).getSampleFractions().first().getPeakTimeStamp();

        File dataValuesFile = new File("DUMP of Raw Intensities From " + project.getProjectName() + ".txt");

        try {
            PrintWriter outputWriter = new PrintWriter(new FileWriter(dataValuesFile));
            outputWriter.println("\n\n***************   DUMP of Raw Intensities From " //
                    + project.getProjectName() + "   ********************\n\n");

            for (int i = 0; i < tripoliSamplesSorted.size(); i++) {
                AbstractTripoliSample ts = tripoliSamplesSorted.get(i);

                outputWriter.println( //
                        "\n\nSampleName = " //
                        + ts.getSampleName()//
                        + "   containing " //
                        + ts.getSampleFractions().size()//
                        + "  Fractions displayed in pairs of background / peak  ****************************\n");

                Iterator<TripoliFraction> tripoliSampleFractionsIterator = ts.getSampleFractions().iterator();
                while (tripoliSampleFractionsIterator.hasNext()) {
                    TripoliFraction tf = tripoliSampleFractionsIterator.next();

                    String[][] retrievedIntensitiesForFraction
                            = new String[tf.getDataActiveMap().length * 2 + 3]//
                            [tf.getIncludedIsotopes().size() + 1];

                    String fractionBackgroundHeader
                            = "TimeStamp-Background FILE " + tf.getFractionID() + "_B.txt  = "//
                            + TimeToString.timeStampString(tf.getBackgroundTimeStamp())//
                            + "  = " + tf.getBackgroundTimeStamp() + " milliseconds since January 1, 1970, 00:00:00 GMT";

                    String fractionPeakHeader
                            = "TimeStamp-Peak FILE " + tf.getFractionID() + ".txt  = "//
                            + TimeToString.timeStampString(tf.getPeakTimeStamp())//
                            + "  = " + tf.getPeakTimeStamp() + " milliseconds since January 1, 1970, 00:00:00 GMT";

                    SortedSet<DataModelInterface> includedIsotopes = tf.getIncludedIsotopes();
                    Iterator<DataModelInterface> tripoliFractionIsotopesIterator = includedIsotopes.iterator();
                    int countOfIsotopes = 0;
                    while (tripoliFractionIsotopesIterator.hasNext()) {
                        DataModelInterface isotope = tripoliFractionIsotopesIterator.next();

                        double[] backgroundIntensities = ((RawIntensityDataModel) isotope).getBackgroundVirtualCollector().getIntensities();
                        double[] peakIntensities = ((RawIntensityDataModel) isotope).getOnPeakVirtualCollector().getIntensities();
                        double[] onPeakAquireTimes = ((RawIntensityDataModel) isotope).getNormalizedOnPeakAquireTimes();//.getOnPeakVirtualCollector().getAquireTimes();
                        double[] backgroundAquireTimes = ((RawIntensityDataModel) isotope).getNormalizedBackgroundAquireTimes();//.getBackgroundVirtualCollector().getAquireTimes();

                        // first backgrounds
                        retrievedIntensitiesForFraction[0][0]
                                = fractionBackgroundHeader //
                                + "\n\n" + "TimeStamp-Back";
                        retrievedIntensitiesForFraction[0][1 + countOfIsotopes] = isotope.getDataModelName() + "-Back";

                        retrievedIntensitiesForFraction[tf.getDataActiveMap().length + 1][0] = "";
                        retrievedIntensitiesForFraction[tf.getDataActiveMap().length + 1][1 + countOfIsotopes] = "";
                        retrievedIntensitiesForFraction[tf.getDataActiveMap().length + 2][0]
                                = fractionPeakHeader //
                                + "\n\n" + "TimeStamp-Peak";
                        retrievedIntensitiesForFraction[tf.getDataActiveMap().length + 2][1 + countOfIsotopes] = isotope.getDataModelName() + "-Peak";

                        for (int j = 0; j < backgroundIntensities.length; j++) {
                            retrievedIntensitiesForFraction[j + 1][1 + countOfIsotopes]
                                    = formatter2.format(backgroundIntensities[j]);
                            retrievedIntensitiesForFraction[tf.getDataActiveMap().length + j + 3][1 + countOfIsotopes]
                                    = formatter2.format(peakIntensities[j]);

//                            Long backgroundTime = new Long(//
//                                    (tf.getBackgroundTimeStamp() - firstFractionBackgroundTimeStamp + (int) backgroundAquireTimes[j]) );
//                            retrievedIntensitiesForFraction[j + 1][0] = backgroundTime.toString() + "       ";//
//                            
                            retrievedIntensitiesForFraction[j + 1][0] = Double.toString(backgroundAquireTimes[j]) + "       ";//

//                            Long peakTime = new Long(//
//                                    (tf.getPeakTimeStamp() - firstFractionPeakTimeStamp + (int) onPeakAquireTimes[j]) );
//                            retrievedIntensitiesForFraction[tf.getDataActiveMap().length + j + 3][0] = peakTime.toString() + "       ";//
                            retrievedIntensitiesForFraction[tf.getDataActiveMap().length + j + 3][0] = Double.toString(onPeakAquireTimes[j]) + "       ";//
                        }

                        countOfIsotopes++;

                    }
                    String standardStatus = "";
                    if (tf.isStandard()) {
                        try {
                            standardStatus = "       Primary Std = " + ts.getMineralStandardModel().getModelName();
                        } catch (Exception e) {
                            System.out.println("BAD SAMPLE TYPE " + ts.getSampleName()//
                                    + "    FractionName = " //
                                    + tf.getFractionID());
                        }
                    }

//                    System.out.println( "\n\n\nSampleName = " //
//                            + ts.getSampleName()//
//                            + "    FractionName = " //
//                            + tf.getFractionID()//
//                            + standardStatus );
                    outputWriter.println( //
                            "\n>>>>>>>>>  FractionName = " //
                            + tf.getFractionID()//
                            + "  (includes background acquisitions followed by peak acquisitions)   "
                            + standardStatus
                            + "\n");

                    for (int a = 0; a < retrievedIntensitiesForFraction.length; a++) {
                        StringBuilder analysis = new StringBuilder();

                        for (int j = 0; j < retrievedIntensitiesForFraction[a].length; j++) {
                            analysis.append(retrievedIntensitiesForFraction[a][j]);
                            analysis.append("\t");
                        }

//                        System.out.println( analysis.toString() );
                        outputWriter.println(analysis.toString());
                    }
                }

            }
            outputWriter.println();

            outputWriter.flush();
            outputWriter.close();

        } catch (IOException iOException) {
        }

        try {
            BrowserControl.displayURL(dataValuesFile.getCanonicalPath());
        } catch (IOException ex) {
        }
    }//GEN-LAST:event_outputDataForNoah_buttonActionPerformed

    private void setDataParameters_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setDataParameters_buttonActionPerformed
        if (showParametersView()) {
            if (rawDataFileHandler == null) {
                rawDataFileHandler = project.getRawDataFileHandler();
            }
            this.setVisible(true);

        } else {
            manageButtons(true, true, false);//false);
        }
    }//GEN-LAST:event_setDataParameters_buttonActionPerformed

    private void saveProject_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveProject_buttonActionPerformed
        saveProject();
        // override
        amChanged = true;
    }//GEN-LAST:event_saveProject_buttonActionPerformed

    private void revertProjectChanges_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_revertProjectChanges_buttonActionPerformed
        revertProject();

        revertProjectChanges_button.setEnabled(false);
        saveProject_button.setEnabled(false);
    }//GEN-LAST:event_revertProjectChanges_buttonActionPerformed

    private void projectName_textActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_projectName_textActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_projectName_textActionPerformed

    private void rawDataTemplateComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rawDataTemplateComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_rawDataTemplateComboBoxActionPerformed
//    /**
//     * @param args the command line arguments
//     */
//    public static void main ( String args[] ) {
//        /*
//         * Set the Nimbus look and feel
//         */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /*
//         * If Nimbus (introduced in Java SE 6) is not available, stay with the
//         * default look and feel. For details see
//         * http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ( "Nimbus".equals( info.getName() ) ) {
//                    javax.swing.UIManager.setLookAndFeel( info.getClassName() );
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger( ProjectManagerFor_LAICPMS_FromRawData.class.getName() ).log( java.util.logging.Level.SEVERE, null, ex );
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger( ProjectManagerFor_LAICPMS_FromRawData.class.getName() ).log( java.util.logging.Level.SEVERE, null, ex );
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger( ProjectManagerFor_LAICPMS_FromRawData.class.getName() ).log( java.util.logging.Level.SEVERE, null, ex );
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger( ProjectManagerFor_LAICPMS_FromRawData.class.getName() ).log( java.util.logging.Level.SEVERE, null, ex );
//        }
//        //</editor-fold>
//
//        /*
//         * Create and display the dialog
//         */
//        java.awt.EventQueue.invokeLater( new Runnable() {
//
//            public void run () {
//                ProjectManagerFor_LAICPMS_FromRawData dialog = new ProjectManagerFor_LAICPMS_FromRawData(//
//                        new javax.swing.JFrame(), true, null, null );
//                dialog.addWindowListener( new java.awt.event.WindowAdapter() {
//
//                    @Override
//                    public void windowClosing ( java.awt.event.WindowEvent e ) {
//                        System.exit( 0 );
//                    }
//                } );
//                dialog.setVisible( true );
//            }
//        } );
//    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JTextPane aboutInfo_textPanel;
    protected javax.swing.JComboBox<String> analysisPurposeChooser;
    protected javax.swing.JButton closeProjectManager_button;
    protected javax.swing.JComboBox<AbstractRawDataFileHandler> fileHandlerComboBox;
    private javax.swing.JLabel fileProtocol_label;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JScrollPane jScrollPane1;
    protected javax.swing.JProgressBar loadDataTaskProgressBar;
    protected javax.swing.JButton loadRawData_button;
    private javax.swing.JLayeredPane mainPanel;
    protected javax.swing.JTabbedPane manager_JTab;
    protected javax.swing.JLabel noteToUser_label;
    private javax.swing.JLabel noteToUser_label1;
    protected javax.swing.JButton openRawDataManager_button;
    protected javax.swing.JButton outputDataForNoah_button;
    private javax.swing.JLayeredPane projectFinalizationPane;
    private javax.swing.JLabel projectName_label;
    protected javax.swing.JTextField projectName_text;
    protected javax.swing.JLayeredPane projectSampleCommonLeadWorkPane;
    protected javax.swing.JLayeredPane projectSampleWorkPane;
    protected javax.swing.JLabel projectType_label;
    private javax.swing.JScrollPane rawDataFileChosen_scrollPane;
    protected javax.swing.JTextArea rawDataFileChosen_textArea;
    protected javax.swing.JComboBox<AbstractRawDataFileTemplate> rawDataTemplateComboBox;
    protected javax.swing.JButton revertProjectChanges_button;
    protected javax.swing.JButton saveProjectAs_button;
    protected javax.swing.JButton saveProject_button;
    protected javax.swing.JButton setDataParameters_button;
    protected javax.swing.JLabel title_label;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the project
     */
    public ProjectInterface getProject() {
        return project;
    }

    /**
     *
     */
    public void refreshMaskingArray() {
        tripoliSession.refreshMaskingArray();
    }

    private class TextDocChangeListener implements DocumentListener {

        @Override
        public void insertUpdate(DocumentEvent de) {
            updateDataChangeStatus(true);
        }

        @Override
        public void removeUpdate(DocumentEvent de) {
            updateDataChangeStatus(true);
        }

        @Override
        public void changedUpdate(DocumentEvent de) {
            updateDataChangeStatus(true);
        }
    }

    public void close() {
        setVisible(false);
        dispose();
    }
}
