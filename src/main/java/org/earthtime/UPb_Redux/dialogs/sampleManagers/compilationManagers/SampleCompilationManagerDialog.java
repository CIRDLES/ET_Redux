/*
 * SampleManagerDialog.java
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
package org.earthtime.UPb_Redux.dialogs.sampleManagers.compilationManagers;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.earthtime.UPb_Redux.dialogs.DialogEditor;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.UPb_Redux.samples.Sample;
import org.earthtime.UPb_Redux.user.ReduxPersistentState;
import org.earthtime.UPb_Redux.utilities.BrowserControl;
import org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException;
import org.earthtime.archivingTools.IEDACredentialsValidator;
import org.earthtime.exceptions.ETException;

/**
 *
 * @author James F. Bowring
 */
public class SampleCompilationManagerDialog extends DialogEditor {

    private Sample mySample = null;
    private File importFractionFolderMRU;
    private boolean initialized = false;
    private final boolean automaticFractionCreation = false;
    private final Frame parent;

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

    private enum fractionSources {

        SINGLE_LOCAL, FOLDER_LOCAL, ONE_OR_MORE_GEOCHRON, MANY_GEOCHRON
    };

    private enum fractionDestinations {

        ADD, EDIT
    };
    private fractionSources fractionSource = fractionSources.SINGLE_LOCAL;
    private boolean newSample = false;

    /**
     * Creates new form SampleManagerDialog
     *
     * @param parent
     * @param modal
     * @param importFractionFolderMRU
     * @param sample
     */
    public SampleCompilationManagerDialog(
            java.awt.Frame parent,
            boolean modal,
            Sample sample,
            File importFractionFolderMRU) {
        super(parent, modal);

        this.parent = parent;

        this.importFractionFolderMRU = importFractionFolderMRU;

        initComponents();

        //Register listeners for the radio buttons.
        sourceOfFractionsListener mySourceOfFractionsListener = new sourceOfFractionsListener();

        sourceFolder_jRadioButton.addActionListener(mySourceOfFractionsListener);
        sourceSingle_jRadioButton.addActionListener(mySourceOfFractionsListener);
        sourceGeochron_jRadioButton.addActionListener(mySourceOfFractionsListener);

        this.mySample = sample;

        InitSampleFields();

        credentialsValidReport_label.setVisible(false);

    }

    class automaticFractionsListener implements ItemListener {

        public void itemStateChanged(ItemEvent e) {
            Object source = e.getItemSelectable();
            sourceFolder_jRadioButton.setEnabled(automaticFractionCreation);
            sourceSingle_jRadioButton.setEnabled(automaticFractionCreation);
            sourceGeochron_jRadioButton.setEnabled(automaticFractionCreation);

        }
    }

    class sourceOfFractionsListener implements ActionListener {

        public void actionPerformed(ActionEvent evt) {
            System.out.println(evt.getActionCommand());
            fractionSource = fractionSources.valueOf(evt.getActionCommand());
        }
    }

    /**
     *
     */
    public void setSize() {
        setSize(480, 660);
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

    private void InitSampleFields() {
        // init input fields

        // sample name is fixed once populated with fractions
        sampleName_text.setDocument(
                new UnDoAbleDocument(sampleName_text, true));
        sampleName_text.setText(getMySample().getSampleName());

        sampleNotes_textArea.setDocument(new UnDoAbleDocument(sampleNotes_textArea, true));
        sampleNotes_textArea.setText(getMySample().getSampleAnnotations());

        // init display fields - html allows multi-line
        sampleReduxFileName_label.setText(
                "<html><p>" + getMySample().getReduxSampleFilePath() + "</p></html>");

        geochronUserName_text.setDocument(new UnDoAbleDocument(geochronUserName_text, true));
        geochronUserName_text.setText(ReduxPersistentState.getExistingPersistentState().getReduxPreferences().getGeochronUserName());

        geochronPassword_passwordField.setDocument(new UnDoAbleDocument(geochronPassword_passwordField, true));
        geochronPassword_passwordField.setText(ReduxPersistentState.getExistingPersistentState().getReduxPreferences().getGeochronPassWord());

    }

    private void SaveSampleData()
            throws ETException,
            FileNotFoundException,
            BadLabDataException,
            IOException,
            BadOrMissingXMLSchemaException {
        // TODO: validate fields - make this more sophisticated
        if (sampleName_text.getText().length() == 0) {
            return;
        } else {
            setVisible(false);

            String success = "";

            // get aliquots as specified
            switch (fractionSource) {
                case SINGLE_LOCAL:
                    success = getMySample().importAliquotLocalXMLDataFile(importFractionFolderMRU);
                    if (!success.equalsIgnoreCase("")) {
                        getMySample().setSampleName(sampleName_text.getText());

                        getMySample().setSampleAnnotations(sampleNotes_textArea.getText());

                        setImportFractionFolderMRU(new File(success));

                        setInitialized(true);
                    }
                    break;
                case FOLDER_LOCAL:
                    break;
                case ONE_OR_MORE_GEOCHRON:
                    success = getMySample().importOneOrMoreGeochronAliquotXMLDataFiles();
                    if (success.contains("Found")) {
                        getMySample().setSampleName(sampleName_text.getText());

                        getMySample().setSampleAnnotations(sampleNotes_textArea.getText());

                        setInitialized(true);
                    }

                    if (success.contains("Missing")) {
                        System.out.println(success);
                        JOptionPane.showMessageDialog(this,
                                success,
                                "Geochron Warning",
                                JOptionPane.WARNING_MESSAGE);
                    }
                    break;
            }
        }
    }

    /**
     *
     * @return
     */
    public Sample getMySample() {
        return mySample;
    }

    /**
     *
     * @param mySample
     */
    public void setMySample(Sample mySample) {
        this.mySample = mySample;
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
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sourceOfFractionsOptions_buttonGroup = new javax.swing.ButtonGroup();
        destinationOfFractionsOptions_buttonGroup = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        sampleName_label = new javax.swing.JLabel();
        sampleName_text = new javax.swing.JTextField();
        sampleReduxFile_label = new javax.swing.JLabel();
        sampleReduxFileName_label = new javax.swing.JLabel();
        sampleNotes_label = new javax.swing.JLabel();
        sampleNotes_scrollPane = new javax.swing.JScrollPane();
        sampleNotes_textArea = new javax.swing.JTextArea();
        jPanel3 = new javax.swing.JPanel();
        sourceFolder_jRadioButton = new javax.swing.JRadioButton();
        sourceSingle_jRadioButton = new javax.swing.JRadioButton();
        sourceGeochron_jRadioButton = new javax.swing.JRadioButton();
        geochronUserName_text = new javax.swing.JTextField();
        validateGeochronCredentials_button = new javax.swing.JButton();
        geochronPassword_passwordField = new javax.swing.JPasswordField();
        visitGeochron_button = new javax.swing.JButton();
        userNameGeochron_label = new javax.swing.JLabel();
        passwordGeochron_label = new javax.swing.JLabel();
        credentialsValidReport_label = new javax.swing.JLabel();
        sampleName_label1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        cancel_button = new javax.swing.JButton();
        saveAndCloseAndProceedToAliquotChooser_button = new javax.swing.JButton();
        sampleType_panel = new javax.swing.JPanel();
        sampleType_label = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(245, 236, 206));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jPanel1.setMaximumSize(new java.awt.Dimension(480, 620));
        jPanel1.setLayout(null);

        sampleName_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        sampleName_label.setText("Lab's Name for this Compilation Sample:");
        jPanel1.add(sampleName_label);
        sampleName_label.setBounds(8, 63, 224, 14);

        sampleName_text.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        sampleName_text.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        sampleName_text.setText("Sample Name");
        jPanel1.add(sampleName_text);
        sampleName_text.setBounds(238, 56, 218, 27);

        sampleReduxFile_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        sampleReduxFile_label.setText("File path for this Sample:");
        jPanel1.add(sampleReduxFile_label);
        sampleReduxFile_label.setBounds(8, 447, 139, 14);

        sampleReduxFileName_label.setText("<Not Saved>");
        sampleReduxFileName_label.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        jPanel1.add(sampleReduxFileName_label);
        sampleReduxFileName_label.setBounds(28, 467, 428, 64);

        sampleNotes_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        sampleNotes_label.setText("Notes about this Sample:");
        jPanel1.add(sampleNotes_label);
        sampleNotes_label.setBounds(8, 337, 141, 14);

        sampleNotes_textArea.setColumns(20);
        sampleNotes_textArea.setRows(5);
        sampleNotes_scrollPane.setViewportView(sampleNotes_textArea);

        jPanel1.add(sampleNotes_scrollPane);
        sampleNotes_scrollPane.setBounds(32, 357, 440, 84);

        jPanel3.setBackground(new java.awt.Color(255, 255, 224));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Source of Aliquots", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 13), new java.awt.Color(204, 0, 0))); // NOI18N

        sourceFolder_jRadioButton.setBackground(new java.awt.Color(255, 255, 224));
        sourceOfFractionsOptions_buttonGroup.add(sourceFolder_jRadioButton);
        sourceFolder_jRadioButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        sourceFolder_jRadioButton.setText("<html>Local Aliquots folder</html>");
        sourceFolder_jRadioButton.setActionCommand(fractionSource.FOLDER_LOCAL.toString());
        sourceFolder_jRadioButton.setContentAreaFilled(false);
        sourceFolder_jRadioButton.setEnabled(false);
        sourceFolder_jRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sourceFolder_jRadioButtonActionPerformed(evt);
            }
        });

        sourceOfFractionsOptions_buttonGroup.add(sourceSingle_jRadioButton);
        sourceSingle_jRadioButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        sourceSingle_jRadioButton.setSelected(true);
        sourceSingle_jRadioButton.setText("<html>Single Local Aliquot</html>");
        sourceSingle_jRadioButton.setActionCommand(fractionSource.SINGLE_LOCAL.toString());
        sourceSingle_jRadioButton.setContentAreaFilled(false);
        sourceSingle_jRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sourceSingle_jRadioButtonActionPerformed(evt);
            }
        });

        sourceOfFractionsOptions_buttonGroup.add(sourceGeochron_jRadioButton);
        sourceGeochron_jRadioButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        sourceGeochron_jRadioButton.setText("<html>Import one or more Aliquots from <b>Geochron.org</b> database</html>");
        sourceGeochron_jRadioButton.setActionCommand(fractionSource.ONE_OR_MORE_GEOCHRON.toString());
        sourceGeochron_jRadioButton.setContentAreaFilled(false);
        sourceGeochron_jRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sourceGeochron_jRadioButtonActionPerformed(evt);
            }
        });

        geochronUserName_text.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        geochronUserName_text.setText("username");

        validateGeochronCredentials_button.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        validateGeochronCredentials_button.setForeground(new java.awt.Color(255, 51, 51));
        validateGeochronCredentials_button.setText("<html><b>Validate</b> Geochron credentials</html>");
        validateGeochronCredentials_button.setName("false"); // NOI18N
        validateGeochronCredentials_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                validateGeochronCredentials_buttonActionPerformed(evt);
            }
        });

        geochronPassword_passwordField.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        geochronPassword_passwordField.setText("############");

        visitGeochron_button.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        visitGeochron_button.setText("Visit Geochron");
        visitGeochron_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                visitGeochron_buttonActionPerformed(evt);
            }
        });

        userNameGeochron_label.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        userNameGeochron_label.setText("user name:");

        passwordGeochron_label.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        passwordGeochron_label.setText("password:");

        credentialsValidReport_label.setBackground(new java.awt.Color(255, 255, 255));
        credentialsValidReport_label.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        credentialsValidReport_label.setForeground(new java.awt.Color(255, 51, 51));
        credentialsValidReport_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        credentialsValidReport_label.setText("credentials are VALID");
        credentialsValidReport_label.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        credentialsValidReport_label.setOpaque(true);

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel3Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                            .add(sourceGeochron_jRadioButton)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel3Layout.createSequentialGroup()
                                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, userNameGeochron_label)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, passwordGeochron_label))
                                .add(2, 2, 2)
                                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(geochronPassword_passwordField)
                                    .add(geochronUserName_text))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(credentialsValidReport_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 217, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(validateGeochronCredentials_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 226, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                            .add(jPanel3Layout.createSequentialGroup()
                                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(sourceSingle_jRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(sourceFolder_jRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .add(259, 259, 259))))
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(102, 102, 102)
                        .add(visitGeochron_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 208, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(sourceSingle_jRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(5, 5, 5)
                        .add(sourceFolder_jRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel3Layout.createSequentialGroup()
                        .add(54, 54, 54)
                        .add(sourceGeochron_jRadioButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(userNameGeochron_label)
                    .add(geochronUserName_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(credentialsValidReport_label))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(validateGeochronCredentials_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(passwordGeochron_label)
                    .add(geochronPassword_passwordField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(visitGeochron_button))
        );

        jPanel1.add(jPanel3);
        jPanel3.setBounds(31, 109, 440, 210);

        sampleName_label1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        sampleName_label1.setText("<html>Note: Compilation mode provides a mechanism to load in various <br>  aliquots from the same or different Samples and to view and save them <br>as a single ET_Redux sample\n file.</html>");
        jPanel1.add(sampleName_label1);
        sampleName_label1.setBounds(8, 8, 448, 38);

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        cancel_button.setForeground(new java.awt.Color(255, 51, 0));
        cancel_button.setText("Cancel");
        cancel_button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        cancel_button.setPreferredSize(new java.awt.Dimension(110, 23));
        cancel_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancel_buttonActionPerformed(evt);
            }
        });

        saveAndCloseAndProceedToAliquotChooser_button.setForeground(new java.awt.Color(255, 51, 0));
        saveAndCloseAndProceedToAliquotChooser_button.setText("Proceed to Aliquot Chooser");
        saveAndCloseAndProceedToAliquotChooser_button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        saveAndCloseAndProceedToAliquotChooser_button.setPreferredSize(new java.awt.Dimension(110, 23));
        saveAndCloseAndProceedToAliquotChooser_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAndCloseAndProceedToAliquotChooser_buttonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(saveAndCloseAndProceedToAliquotChooser_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 242, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(cancel_button, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(saveAndCloseAndProceedToAliquotChooser_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 32, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(cancel_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 32, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        sampleType_panel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        sampleType_label.setBackground(new java.awt.Color(255, 204, 102));
        sampleType_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        sampleType_label.setText("C O M P I L A T I O N       M O D E");
        sampleType_label.setOpaque(true);

        org.jdesktop.layout.GroupLayout sampleType_panelLayout = new org.jdesktop.layout.GroupLayout(sampleType_panel);
        sampleType_panel.setLayout(sampleType_panelLayout);
        sampleType_panelLayout.setHorizontalGroup(
            sampleType_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(sampleType_label, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 488, Short.MAX_VALUE)
        );
        sampleType_panelLayout.setVerticalGroup(
            sampleType_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(sampleType_label)
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(sampleType_panel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel1, 0, 490, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(sampleType_panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 501, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void cancel_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancel_buttonActionPerformed
        close();
    }//GEN-LAST:event_cancel_buttonActionPerformed

    private void saveAndCloseAndProceedToAliquotChooser_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAndCloseAndProceedToAliquotChooser_buttonActionPerformed
        try {
            SaveSampleData();
            close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SampleCompilationManagerDialog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BadLabDataException | BadOrMissingXMLSchemaException | IOException ex) {
            Logger.getLogger(SampleCompilationManagerDialog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ETException ex) {
        }

    }//GEN-LAST:event_saveAndCloseAndProceedToAliquotChooser_buttonActionPerformed

    private void validateGeochronCredentials(boolean isVerbose) {
        String userCode = //
                IEDACredentialsValidator.validateGeochronCredentials(//
                        geochronUserName_text.getText().trim(),//
                        new String(geochronPassword_passwordField.getPassword()), isVerbose);

        boolean valid = (userCode.trim().length() > 0);
        if (valid) {
            credentialsValidReport_label.setText("Credentials are VALID.");
        } else {
            credentialsValidReport_label.setText("Credentials are NOT valid.");
        }

        saveAndCloseAndProceedToAliquotChooser_button.setEnabled(valid);
        credentialsValidReport_label.setVisible(true);

    }

    private void sourceSingle_jRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sourceSingle_jRadioButtonActionPerformed
        saveAndCloseAndProceedToAliquotChooser_button.setEnabled(true);
}//GEN-LAST:event_sourceSingle_jRadioButtonActionPerformed

    private void validateGeochronCredentials_buttonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_validateGeochronCredentials_buttonActionPerformed
        validateGeochronCredentials(true);
}//GEN-LAST:event_validateGeochronCredentials_buttonActionPerformed

    private void visitGeochron_buttonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_visitGeochron_buttonActionPerformed
        BrowserControl.displayURL("http://www.geochron.org/");
}//GEN-LAST:event_visitGeochron_buttonActionPerformed

    private void sourceGeochron_jRadioButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sourceGeochron_jRadioButtonActionPerformed
        validateGeochronCredentials(false);
    }//GEN-LAST:event_sourceGeochron_jRadioButtonActionPerformed

    private void sourceFolder_jRadioButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sourceFolder_jRadioButtonActionPerformed
        saveAndCloseAndProceedToAliquotChooser_button.setEnabled(true);
    }//GEN-LAST:event_sourceFolder_jRadioButtonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancel_button;
    private javax.swing.JLabel credentialsValidReport_label;
    private javax.swing.ButtonGroup destinationOfFractionsOptions_buttonGroup;
    private javax.swing.JPasswordField geochronPassword_passwordField;
    private javax.swing.JTextField geochronUserName_text;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel passwordGeochron_label;
    private javax.swing.JLabel sampleName_label;
    private javax.swing.JLabel sampleName_label1;
    private javax.swing.JTextField sampleName_text;
    private javax.swing.JLabel sampleNotes_label;
    private javax.swing.JScrollPane sampleNotes_scrollPane;
    private javax.swing.JTextArea sampleNotes_textArea;
    private javax.swing.JLabel sampleReduxFileName_label;
    private javax.swing.JLabel sampleReduxFile_label;
    private javax.swing.JLabel sampleType_label;
    private javax.swing.JPanel sampleType_panel;
    private javax.swing.JButton saveAndCloseAndProceedToAliquotChooser_button;
    private javax.swing.JRadioButton sourceFolder_jRadioButton;
    private javax.swing.JRadioButton sourceGeochron_jRadioButton;
    private javax.swing.ButtonGroup sourceOfFractionsOptions_buttonGroup;
    private javax.swing.JRadioButton sourceSingle_jRadioButton;
    private javax.swing.JLabel userNameGeochron_label;
    private javax.swing.JButton validateGeochronCredentials_button;
    private javax.swing.JButton visitGeochron_button;
    // End of variables declaration//GEN-END:variables
}
