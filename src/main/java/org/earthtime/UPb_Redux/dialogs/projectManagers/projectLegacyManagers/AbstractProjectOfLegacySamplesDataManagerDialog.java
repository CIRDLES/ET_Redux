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
package org.earthtime.UPb_Redux.dialogs.projectManagers.projectLegacyManagers;

import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import org.earthtime.UPb_Redux.dialogs.DialogEditor;
import org.earthtime.UPb_Redux.exceptions.BadImportedCSVLegacyFileException;
import org.earthtime.UPb_Redux.reduxLabData.ReduxLabData;
import org.earthtime.exceptions.ETException;
import org.earthtime.exceptions.ETWarningDialog;
import org.earthtime.projects.ProjectInterface;
import org.earthtime.projects.projectImporters.AbstractProjectImporterFromLegacyCSVFile;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;

/**
 *
 * @author James F. Bowring
 */
public abstract class AbstractProjectOfLegacySamplesDataManagerDialog extends DialogEditor {

    private ProjectInterface myProject = null;
    private File importFractionFolderMRU;
    private boolean initialized = false;
    private boolean newSample = false;
    private AbstractProjectImporterFromLegacyCSVFile converter;

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
     * @param parent
     * @param modal
     * @param dataTypeTitle
     * @param project
     * @param converter
     * @param importFractionFolderMRU
     */
    public AbstractProjectOfLegacySamplesDataManagerDialog (
            Frame parent, //
            boolean modal, //
            String dataTypeTitle, //
            ProjectInterface project, //
            AbstractProjectImporterFromLegacyCSVFile converter,//
            File importFractionFolderMRU) {
        super( parent, modal );

        this.importFractionFolderMRU = importFractionFolderMRU;

        initComponents();

        this.myProject = project;

        initSampleFields();

        sampleType_label.setText( dataTypeTitle + sampleType_label.getText() );

        this.converter = converter;
        
        this.parent = parent;
        
        sampleName_text.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
                saveAndClose.setEnabled(sampleName_text.getText().trim().length() > 0);
            }

            @Override
            public void keyPressed(KeyEvent e) {
                saveAndClose.setEnabled(sampleName_text.getText().trim().length() > 0);
            }

            @Override
            public void keyReleased(KeyEvent e) {
                saveAndClose.setEnabled(sampleName_text.getText().trim().length() > 0);
            }
        });

    }

    /**
     *
     */
    public void setSize () {
        setSize( 555, 480 );
    }

    /**
     *
     * @return
     */
    public File getImportFractionFolderMRU () {
        return importFractionFolderMRU;
    }

    /**
     *
     * @param importFractionFolderMRU
     */
    public void setImportFractionFolderMRU ( File importFractionFolderMRU ) {
        this.importFractionFolderMRU = importFractionFolderMRU;
    }

    private void initSampleFields () {
        // init input fields

        sampleName_text.setDocument(
                new UnDoAbleDocument( sampleName_text,  true));
 //       sampleName_text.setText( getMySample().getSampleName() );

        sampleNotes_textArea.setDocument( new UnDoAbleDocument( sampleNotes_textArea, true ) );
 //       sampleNotes_textArea.setText( getMySample().getSampleAnnotations() );

        // init display fields - html allows multi-line
////        sampleReduxFileName_label.setText(
////                "<html><p>" + getMySample().getReduxSampleFilePath() + "</p></html>" );
////        sampleReduxFileName_label.setToolTipText( getMySample().getReduxSampleFilePath() );

        physicalConstantsModelChooser.removeAllItems();
        ArrayList<AbstractRatiosDataModel> physicalConstantsModels = ReduxLabData.getInstance().getPhysicalConstantsModels();
        for (int i = (physicalConstantsModels.size() > 1 ? 1 : 0); i < physicalConstantsModels.size(); i ++) {
            physicalConstantsModelChooser.addItem( physicalConstantsModels.get( i ).getReduxLabDataElementName() );
        }

        physicalConstantsModelChooser.setSelectedIndex( 0 );
//        try {
//            physicalConstantsModelChooser.setSelectedItem( getMySample().getPhysicalConstantsModel().getReduxLabDataElementName() );
//        } catch (BadLabDataException ex) {
//        }
    }

    private void saveSampleData ()
            throws ETException {
        // validate sample name
        if ( sampleName_text.getText().trim().length() == 0 ) {
            return;
        }

        myProject.setProjectName(  sampleName_text.getText().trim() );
//        mySample.setSampleAnnotations( sampleNotes_textArea.getText() );

//        String currentPhysicalConstantsModelName = "";
//        try {
//            currentPhysicalConstantsModelName = myProject.getPhysicalConstantsModel().getNameAndVersion();
//
//        } catch (BadLabDataException badLabDataException) {
//        }
//        if (  ! ((String) physicalConstantsModelChooser.getSelectedItem()).equalsIgnoreCase( currentPhysicalConstantsModelName ) ) {
//            try {
//                mySample.setPhysicalConstantsModel(
//                        mySample.getMyReduxLabData().
//                        getAPhysicalConstantsModel( ((String) physicalConstantsModelChooser.getSelectedItem()) ) );
//
//            } catch (BadLabDataException badLabDataException) {
//            }
//        }


        try {
            converter.setMruFolder( importFractionFolderMRU );
            converter.readInProjectSamples(myProject);
            setInitialized( true );

            setImportFractionFolderMRU( converter.getMruFolder() );
        } catch (FileNotFoundException fileNotFoundException) {
        } catch (BadImportedCSVLegacyFileException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
    }

    /**
     *
     * @return
     */
    public ProjectInterface getMyProject () {
        return myProject;
    }

    /**
     *
     * @param myProject
     */
    public void setMyProject ( ProjectInterface myProject ) {
        this.myProject = myProject;
    }

    /**
     *
     * @return
     */
    public boolean isInitialized () {
        return initialized;
    }

    /**
     *
     * @param isSaved
     */
    public void setInitialized ( boolean isSaved ) {
        this.initialized = isSaved;
    }

    /**
     *
     * @return
     */
    public boolean isNewSample () {
        return newSample;
    }

    /**
     *
     * @param newSample
     */
    public void setNewSample ( boolean newSample ) {
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
        jPanel1 = new javax.swing.JPanel();
        sampleName_label = new javax.swing.JLabel();
        sampleName_text = new javax.swing.JTextField();
        sampleReduxFile_label = new javax.swing.JLabel();
        sampleReduxFileName_label = new javax.swing.JLabel();
        sampleNotes_label = new javax.swing.JLabel();
        sampleNotes_scrollPane = new javax.swing.JScrollPane();
        sampleNotes_textArea = new javax.swing.JTextArea();
        physicalConstantsModelChooser = new javax.swing.JComboBox<String>();
        defaultHeader_label = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        close = new javax.swing.JButton();
        saveAndClose = new javax.swing.JButton();
        sampleType_panel = new javax.swing.JPanel();
        sampleType_label = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(245, 236, 206));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jPanel1.setMaximumSize(new java.awt.Dimension(480, 620));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        sampleName_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        sampleName_label.setText("Project Name:");
        jPanel1.add(sampleName_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(62, 9, 88, -1));

        sampleName_text.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        sampleName_text.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        sampleName_text.setText("Project Name");
        jPanel1.add(sampleName_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(168, 2, 338, -1));

        sampleReduxFile_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        sampleReduxFile_label.setText("File path for this Project:");
        jPanel1.add(sampleReduxFile_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 256, -1, -1));

        sampleReduxFileName_label.setText("<Not Saved>");
        sampleReduxFileName_label.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        sampleReduxFileName_label.setAutoscrolls(true);
        jPanel1.add(sampleReduxFileName_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(28, 276, 407, 64));

        sampleNotes_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        sampleNotes_label.setText("Notes about this Project:");
        jPanel1.add(sampleNotes_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 146, -1, -1));

        sampleNotes_textArea.setColumns(20);
        sampleNotes_textArea.setRows(5);
        sampleNotes_textArea.setMaximumSize(new java.awt.Dimension(250, 80));
        sampleNotes_textArea.setPreferredSize(new java.awt.Dimension(250, 80));
        sampleNotes_scrollPane.setViewportView(sampleNotes_textArea);

        jPanel1.add(sampleNotes_scrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 166, 540, -1));

        physicalConstantsModelChooser.setBackground(new java.awt.Color(245, 236, 206));
        jPanel1.add(physicalConstantsModelChooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 70, 330, -1));

        defaultHeader_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        defaultHeader_label.setForeground(new java.awt.Color(204, 51, 0));
        defaultHeader_label.setText("Set Physical Constants Model for this Project:");
        jPanel1.add(defaultHeader_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 50, -1, -1));

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
        jPanel2.add(close, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 2, 168, 32));

        saveAndClose.setForeground(new java.awt.Color(255, 51, 0));
        saveAndClose.setText("OK");
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
        sampleType_label.setText("   LEGACY MODE: Import a Project of Samples from .csv file");
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
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 562, Short.MAX_VALUE)
            .add(sampleType_panel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(sampleType_panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(6, 6, 6)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
            saveSampleData();
            close();
        } catch (ETException ex) {
            ex.printStackTrace();
            new ETWarningDialog(ex).setVisible(true);
        }

    }//GEN-LAST:event_saveAndCloseActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup TWsource;
    private javax.swing.JButton close;
    private javax.swing.JLabel defaultHeader_label;
    private javax.swing.ButtonGroup destinationOfFractionsOptions_buttonGroup;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.ButtonGroup modeChooser_buttonGroup;
    private javax.swing.JComboBox<String> physicalConstantsModelChooser;
    private javax.swing.JLabel sampleName_label;
    private javax.swing.JTextField sampleName_text;
    private javax.swing.JLabel sampleNotes_label;
    private javax.swing.JScrollPane sampleNotes_scrollPane;
    private javax.swing.JTextArea sampleNotes_textArea;
    private javax.swing.JLabel sampleReduxFileName_label;
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
    public AbstractProjectImporterFromLegacyCSVFile getConverter () {
        return converter;
    }

    /**
     * @param converter the converter to set
     */
    public void setConverter ( AbstractProjectImporterFromLegacyCSVFile converter ) {
        this.converter = converter;
    }
}
