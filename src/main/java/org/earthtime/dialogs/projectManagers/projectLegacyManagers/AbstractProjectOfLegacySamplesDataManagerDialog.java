/*
 * AbstractProjectOfLegacySamplesDataManagerDialog.java
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
package org.earthtime.dialogs.projectManagers.projectLegacyManagers;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import javax.swing.JButton;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.exceptions.BadImportedCSVLegacyFileException;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.UTh_Redux.fractions.UThLegacyFractionI;
import org.earthtime.UTh_Redux.fractions.fractionReduction.UThFractionReducer;
import org.earthtime.beans.ET_JButton;
import org.earthtime.dialogs.DialogEditor;
import org.earthtime.exceptions.ETException;
import org.earthtime.exceptions.ETWarningDialog;
import org.earthtime.projects.ProjectInterface;
import org.earthtime.projects.projectImporters.AbstractProjectImporterFromLegacyDelimitedTextFile;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataViews.AbstractRatiosDataView;
import org.earthtime.ratioDataViews.PhysicalConstantsDataViewNotEditable;
import org.earthtime.reduxLabData.ReduxLabData;
import org.earthtime.samples.SampleInterface;

/**
 *
 * @author James F. Bowring
 */
public abstract class AbstractProjectOfLegacySamplesDataManagerDialog extends DialogEditor {

    private ProjectInterface myProject = null;
    private File importFractionFolderMRU;
    private boolean initialized = false;
    private boolean newSample = false;
    private AbstractProjectImporterFromLegacyDelimitedTextFile converter;
    private ET_JButton chooseFileButton;
    private JButton viewPhysicalConstantsModel_button;

    /**
     *
     */
    protected Frame parent;

    /**
     * Creates new form AbstractSampleLegacyManagerDialog
     *
     * @param parent
     * @param modal
     * @param dataTypeTitle
     * @param converter
     * @param importFractionFolderMRU
     */
    /**
     * Creates new form AbstractSampleLegacyManagerDialog
     *
     * @param parent
     * @param modal
     * @param dataTypeTitle
     * @param project
     * @param converter
     * @param importFractionFolderMRU
     */
    public AbstractProjectOfLegacySamplesDataManagerDialog(
            Frame parent, //
            boolean modal, //
            String dataTypeTitle, //
            ProjectInterface project, //
            AbstractProjectImporterFromLegacyDelimitedTextFile converter,//
            File importFractionFolderMRU) {
        super(parent, modal);

        this.importFractionFolderMRU = importFractionFolderMRU;

        initComponents();

        this.myProject = project;

        initDialogContent();

        sampleType_label.setText(dataTypeTitle + sampleType_label.getText());

        this.converter = converter;

        this.parent = parent;

        saveAndClose.setEnabled(!myProject.getProjectSamples().isEmpty());

    }

    /**
     *
     */
    public void setSize() {
        setSize(645, 480);
    }

    /**
     *
     * @return
     */
    public File getImportFractionFolderMRU() {
        return importFractionFolderMRU;
    }

    /**
     *
     * @param importFractionFolderMRU
     */
    public void setImportFractionFolderMRU(File importFractionFolderMRU) {
        this.importFractionFolderMRU = importFractionFolderMRU;
    }

    public void initDialogContent() {
        projectName_text.setDocument(
                new UnDoAbleDocument(projectName_text, true));
        projectName_text.setText(myProject.getProjectName());

        dataSourceNameLabel.setText(": " + myProject.getLocationOfDataImportFile().getName());
        dataSourceNameLabel.setToolTipText(myProject.getLocationOfDataImportFile().getAbsolutePath());
        sampleNotes_textArea.setDocument(new UnDoAbleDocument(sampleNotes_textArea, true));

        try {
            projectReduxFileName_label.setText(myProject.getLocationOfProjectReduxFile().getAbsolutePath());
        } catch (Exception e) {
        }
        physicalConstantsModelChooser.removeAllItems();
        ArrayList<AbstractRatiosDataModel> physicalConstantsModels = ReduxLabData.getInstance().getPhysicalConstantsModels();
        for (int i = (physicalConstantsModels.size() > 1 ? 1 : 0); i < physicalConstantsModels.size(); i++) {
            physicalConstantsModelChooser.addItem(physicalConstantsModels.get(i));
        }

        physicalConstantsModelChooser.setSelectedIndex(0);
        try {
            physicalConstantsModelChooser.setSelectedItem(myProject.getSuperSample().getPhysicalConstantsModel());
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }

        // view physical constants model
        viewPhysicalConstantsModel_button = new ET_JButton("View");
        viewPhysicalConstantsModel_button.addActionListener((ActionEvent e) -> {
            AbstractRatiosDataModel selectedModel
                    = ((AbstractRatiosDataModel) physicalConstantsModelChooser.getSelectedItem());
            AbstractRatiosDataView modelView
                    = new PhysicalConstantsDataViewNotEditable(selectedModel, null, false);
            modelView.displayModelInFrame();
        });
        viewPhysicalConstantsModel_button.setFont(ReduxConstants.sansSerif_10_Bold);
        viewPhysicalConstantsModel_button.setBounds(//
                physicalConstantsModelChooser.getX() + physicalConstantsModelChooser.getWidth() + 10, physicalConstantsModelChooser.getY(), 30, 23);
        infoPanel.add(viewPhysicalConstantsModel_button);

        // file choose button
        chooseFileButton = new ET_JButton("Choose import file");
        chooseFileButton.setEnabled(myProject.getProjectSamples().isEmpty() && (projectName_text.getText().trim().length() > 0));
        chooseFileButton.setFont(ReduxConstants.sansSerif_10_Bold);
        chooseFileButton.setBounds(//
                dataSourceLabel.getX() + dataSourceLabel.getWidth(), dataSourceLabel.getY() - 2, 100, 23);
        chooseFileButton.addActionListener((ActionEvent e) -> {
            try {
                converter.setMruFolder(importFractionFolderMRU);
                converter.readInProjectSamples(myProject);
                setInitialized(true);
                saveAndClose.setEnabled(true);
                chooseFileButton.setEnabled(false);
                dataSourceNameLabel.setText(": " + myProject.getLocationOfDataImportFile().getName());
                dataSourceNameLabel.setToolTipText(myProject.getLocationOfDataImportFile().getAbsolutePath());

                setImportFractionFolderMRU(converter.getMruFolder());
            } catch (FileNotFoundException fileNotFoundException) {
            } catch (BadImportedCSVLegacyFileException ex) {
                new ETWarningDialog(ex).setVisible(true);
            }
        });
        infoPanel.add(chooseFileButton);

        projectName_text.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                chooseFileButton.setEnabled(myProject.getProjectSamples().isEmpty() && (projectName_text.getText().trim().length() > 0));
            }

            @Override
            public void keyPressed(KeyEvent e) {
                chooseFileButton.setEnabled(myProject.getProjectSamples().isEmpty() && (projectName_text.getText().trim().length() > 0));
            }

            @Override
            public void keyReleased(KeyEvent e) {
                chooseFileButton.setEnabled(myProject.getProjectSamples().isEmpty() && (projectName_text.getText().trim().length() > 0));
            }
        });
    }

    private void saveProjectData()
            throws ETException {
        // validate sample name
        if (projectName_text.getText().trim().length() == 0) {
            return;
        }

        myProject.setProjectName(projectName_text.getText().trim());

        AbstractRatiosDataModel chosenPhysicalConstantsModel
                = (AbstractRatiosDataModel) physicalConstantsModelChooser.getSelectedItem();
        myProject.getSuperSample().setPhysicalConstantsModel(chosenPhysicalConstantsModel);

        // set physical constant models of each fraction to current
        ArrayList<SampleInterface> mySamples = myProject.getProjectSamples();
        mySamples.stream().map((mySample) -> {
            mySample.setPhysicalConstantsModel(chosenPhysicalConstantsModel);
            return mySample;
        }).map((mySample) -> mySample.getFractions()).forEach((myFractions) -> {
            myFractions.stream().map((myFraction) -> {
                myFraction.setPhysicalConstantsModel(chosenPhysicalConstantsModel);
                return myFraction;
            }).forEach((myFraction) -> {
                UThFractionReducer.reduceFraction((UThLegacyFractionI) myFraction);
            });
        });

        myProject.saveTheProjectAsSerializedReduxFile();
    }

    /**
     *
     * @return
     */
    public ProjectInterface getMyProject() {
        return myProject;
    }

    /**
     *
     * @param myProject
     */
    public void setMyProject(ProjectInterface myProject) {
        this.myProject = myProject;
    }

    /**
     *
     * @return
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     *
     * @param isSaved
     */
    public void setInitialized(boolean isSaved) {
        this.initialized = isSaved;
    }

    /**
     *
     * @return
     */
    public boolean isNewSample() {
        return newSample;
    }

    /**
     *
     * @param newSample
     */
    public void setNewSample(boolean newSample) {
        this.newSample = newSample;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sourceOfFractionsOptions_buttonGroup = new javax.swing.ButtonGroup();
        destinationOfFractionsOptions_buttonGroup = new javax.swing.ButtonGroup();
        updateMode_buttonGroup = new javax.swing.ButtonGroup();
        modeChooser_buttonGroup = new javax.swing.ButtonGroup();
        TWsource = new javax.swing.ButtonGroup();
        infoPanel = new javax.swing.JPanel();
        sampleName_label = new javax.swing.JLabel();
        projectName_text = new javax.swing.JTextField();
        sampleReduxFile_label = new javax.swing.JLabel();
        projectReduxFileName_label = new javax.swing.JLabel();
        sampleNotes_label = new javax.swing.JLabel();
        sampleNotes_scrollPane = new javax.swing.JScrollPane();
        sampleNotes_textArea = new javax.swing.JTextArea();
        physicalConstantsModelChooser = new javax.swing.JComboBox();
        defaultHeader_label = new javax.swing.JLabel();
        defaultHeader_label1 = new javax.swing.JLabel();
        dataSourceLabel = new javax.swing.JLabel();
        dataSourceNameLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        close = new javax.swing.JButton();
        saveAndClose = new javax.swing.JButton();
        sampleType_panel = new javax.swing.JPanel();
        sampleType_label = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        infoPanel.setBackground(new java.awt.Color(245, 236, 206));
        infoPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        infoPanel.setMaximumSize(new java.awt.Dimension(480, 620));
        infoPanel.setLayout(null);

        sampleName_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        sampleName_label.setText("Project Name:");
        infoPanel.add(sampleName_label);
        sampleName_label.setBounds(10, 10, 88, 20);

        projectName_text.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        projectName_text.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        projectName_text.setText("Project Name");
        infoPanel.add(projectName_text);
        projectName_text.setBounds(100, 10, 410, 20);

        sampleReduxFile_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        sampleReduxFile_label.setText("File path for this Project:");
        infoPanel.add(sampleReduxFile_label);
        sampleReduxFile_label.setBounds(10, 280, 139, 14);

        projectReduxFileName_label.setText("<Not Saved>");
        projectReduxFileName_label.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        projectReduxFileName_label.setAutoscrolls(true);
        infoPanel.add(projectReduxFileName_label);
        projectReduxFileName_label.setBounds(30, 300, 407, 64);

        sampleNotes_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        sampleNotes_label.setText("Notes about this Project:");
        infoPanel.add(sampleNotes_label);
        sampleNotes_label.setBounds(10, 160, 141, 14);

        sampleNotes_textArea.setColumns(20);
        sampleNotes_textArea.setRows(5);
        sampleNotes_textArea.setMaximumSize(new java.awt.Dimension(250, 80));
        sampleNotes_textArea.setPreferredSize(new java.awt.Dimension(250, 80));
        sampleNotes_scrollPane.setViewportView(sampleNotes_textArea);

        infoPanel.add(sampleNotes_scrollPane);
        sampleNotes_scrollPane.setBounds(10, 180, 630, 99);

        physicalConstantsModelChooser.setBackground(new java.awt.Color(245, 236, 206));
        infoPanel.add(physicalConstantsModelChooser);
        physicalConstantsModelChooser.setBounds(60, 130, 410, 27);

        defaultHeader_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        defaultHeader_label.setForeground(new java.awt.Color(204, 51, 0));
        defaultHeader_label.setText("Set Physical Constants Model for this Project:");
        infoPanel.add(defaultHeader_label);
        defaultHeader_label.setBounds(20, 110, 255, 20);

        defaultHeader_label1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        defaultHeader_label1.setForeground(new java.awt.Color(204, 51, 0));
        defaultHeader_label1.setText("Reference source of data for this project:");
        infoPanel.add(defaultHeader_label1);
        defaultHeader_label1.setBounds(20, 40, 250, 20);

        dataSourceLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        dataSourceLabel.setForeground(new java.awt.Color(204, 51, 0));
        dataSourceLabel.setText("Data source file:");
        infoPanel.add(dataSourceLabel);
        dataSourceLabel.setBounds(20, 70, 100, 20);

        dataSourceNameLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        dataSourceNameLabel.setForeground(new java.awt.Color(51, 51, 51));
        dataSourceNameLabel.setText(":");
        infoPanel.add(dataSourceNameLabel);
        dataSourceNameLabel.setBounds(230, 70, 410, 20);

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        close.setForeground(new java.awt.Color(255, 51, 0));
        close.setText("Cancel");
        close.setMargin(new java.awt.Insets(0, 0, 0, 0));
        close.setPreferredSize(new java.awt.Dimension(110, 23));
        close.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeActionPerformed(evt);
            }
        });
        jPanel2.add(close, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 0, 168, 32));

        saveAndClose.setForeground(new java.awt.Color(255, 51, 0));
        saveAndClose.setText("Save and Close");
        saveAndClose.setEnabled(false);
        saveAndClose.setMargin(new java.awt.Insets(0, 0, 0, 0));
        saveAndClose.setPreferredSize(new java.awt.Dimension(110, 23));
        saveAndClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAndCloseActionPerformed(evt);
            }
        });
        jPanel2.add(saveAndClose, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 2, 168, 32));

        sampleType_panel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        sampleType_label.setBackground(new java.awt.Color(255, 204, 102));
        sampleType_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        sampleType_label.setText("   LEGACY MODE: Import Project of Samples from text file");
        sampleType_label.setOpaque(true);

        org.jdesktop.layout.GroupLayout sampleType_panelLayout = new org.jdesktop.layout.GroupLayout(sampleType_panel);
        sampleType_panel.setLayout(sampleType_panelLayout);
        sampleType_panelLayout.setHorizontalGroup(
            sampleType_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(sampleType_label, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        sampleType_panelLayout.setVerticalGroup(
            sampleType_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(sampleType_label)
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 645, Short.MAX_VALUE)
            .add(sampleType_panel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(infoPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(sampleType_panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(6, 6, 6)
                .add(infoPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                .add(6, 6, 6)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void closeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeActionPerformed
        close();
    }//GEN-LAST:event_closeActionPerformed

    private void saveAndCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAndCloseActionPerformed
        try {
            saveProjectData();
            close();
        } catch (ETException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }

    }//GEN-LAST:event_saveAndCloseActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup TWsource;
    private javax.swing.JButton close;
    private javax.swing.JLabel dataSourceLabel;
    private javax.swing.JLabel dataSourceNameLabel;
    private javax.swing.JLabel defaultHeader_label;
    private javax.swing.JLabel defaultHeader_label1;
    private javax.swing.ButtonGroup destinationOfFractionsOptions_buttonGroup;
    private javax.swing.JPanel infoPanel;
    private javax.swing.JPanel jPanel2;
    private javax.swing.ButtonGroup modeChooser_buttonGroup;
    private javax.swing.JComboBox physicalConstantsModelChooser;
    private javax.swing.JTextField projectName_text;
    private javax.swing.JLabel projectReduxFileName_label;
    private javax.swing.JLabel sampleName_label;
    private javax.swing.JLabel sampleNotes_label;
    private javax.swing.JScrollPane sampleNotes_scrollPane;
    private javax.swing.JTextArea sampleNotes_textArea;
    private javax.swing.JLabel sampleReduxFile_label;
    private javax.swing.JLabel sampleType_label;
    private javax.swing.JPanel sampleType_panel;
    private javax.swing.JButton saveAndClose;
    private javax.swing.ButtonGroup sourceOfFractionsOptions_buttonGroup;
    private javax.swing.ButtonGroup updateMode_buttonGroup;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the converter
     */
    public AbstractProjectImporterFromLegacyDelimitedTextFile getConverter() {
        return converter;
    }

    /**
     * @param converter the converter to set
     */
    public void setConverter(AbstractProjectImporterFromLegacyDelimitedTextFile converter) {
        this.converter = converter;
    }
}
