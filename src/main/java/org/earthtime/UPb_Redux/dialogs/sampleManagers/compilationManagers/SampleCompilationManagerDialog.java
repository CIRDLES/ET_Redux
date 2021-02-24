/*
 * SampleManagerDialog.java
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
package org.earthtime.UPb_Redux.dialogs.sampleManagers.compilationManagers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.earthtime.UPb_Redux.aliquots.UPbReduxAliquot;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.UPb_Redux.user.ReduxPersistentState;
import org.earthtime.UPb_Redux.user.ReduxPreferences;
import org.earthtime.UPb_Redux.utilities.BrowserControl;
import org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException;
import org.earthtime.aliquots.AliquotInterface;
import org.earthtime.archivingTools.GeoPassIDValidator;
import org.earthtime.archivingTools.GeochronRetrievalUtility;
import org.earthtime.beans.ET_JButton;
import org.earthtime.dialogs.DialogEditor;
import org.earthtime.exceptions.ETException;
import org.earthtime.exceptions.ETWarningDialog;
import org.earthtime.samples.SampleInterface;
import org.earthtime.utilities.FileHelper;
import org.earthtime.xmlUtilities.XMLSerializationI;

/**
 *
 * @author James F. Bowring
 */
public class SampleCompilationManagerDialog extends DialogEditor {

    private SampleInterface mySample = null;
    private File importFractionFolderMRU;
    private boolean initialized = false;
    private final boolean automaticFractionCreation = false;
    private final ReduxPreferences reduxPreferences;

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

    private enum FractionSources {

        SINGLE_LOCAL, FOLDER_LOCAL, ONE_OR_MORE_GEOCHRON, MANY_GEOCHRON
    };

    private enum FractionDestinations {

        ADD, EDIT
    };
    private FractionSources fractionSource = FractionSources.SINGLE_LOCAL;
    private boolean newSample = false;

    /**
     * Creates new form SampleManagerDialog
     *
     * @param reduxPreferences the value of reduxPreferences
     * @param modal
     * @param sample
     * @param importFractionFolderMRU
     */
    public SampleCompilationManagerDialog(//
            ReduxPreferences reduxPreferences,//
            boolean modal, SampleInterface sample,//
            File importFractionFolderMRU) {
        super(null, modal);

        this.reduxPreferences = reduxPreferences;

        this.importFractionFolderMRU = importFractionFolderMRU;

        initComponents();

        //Register listeners for the radio buttons.
        SourceOfFractionsListener mySourceOfFractionsListener = new SourceOfFractionsListener();

        sourceFolder_jRadioButton.addActionListener(mySourceOfFractionsListener);
        sourceSingle_jRadioButton.addActionListener(mySourceOfFractionsListener);
        sourceGeochron_jRadioButton.addActionListener(mySourceOfFractionsListener);

        this.mySample = sample;

        InitSampleFields();

        credentialsValidReport_label.setVisible(false);

    }

    class AutomaticFractionsListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent e) {
            Object source = e.getItemSelectable();
            sourceFolder_jRadioButton.setEnabled(automaticFractionCreation);
            sourceSingle_jRadioButton.setEnabled(automaticFractionCreation);
            sourceGeochron_jRadioButton.setEnabled(automaticFractionCreation);

        }
    }

    class SourceOfFractionsListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent evt) {
            System.out.println(evt.getActionCommand());
            fractionSource = FractionSources.valueOf(evt.getActionCommand());
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

    private void SaveAndImportSampleData()
            throws ETException,
            FileNotFoundException,
            BadLabDataException,
            IOException,
            BadOrMissingXMLSchemaException {
        // TODO: validate fields - make this more sophisticated
        if (sampleName_text.getText().length() > 0) {
            setVisible(false);

            String success = "";

            // get aliquots as specified
            switch (fractionSource) {
                case SINGLE_LOCAL:
                    try {
                        success = SampleInterface.importAliquotFromLocalXMLFileIntoSample(getMySample(), importFractionFolderMRU);
                    } catch (BadLabDataException | IOException | BadOrMissingXMLSchemaException badLabDataException) {
                    }
                    if (!success.equalsIgnoreCase("")) {
                        getMySample().setSampleName(sampleName_text.getText());

                        getMySample().setSampleAnnotations(sampleNotes_textArea.getText());

                        setImportFractionFolderMRU(new File(success));

                        setInitialized(true);
                    }
                    break;
                case FOLDER_LOCAL:
                    // finally added june 2017
                    String dialogTitle = "Select a Folder of Aliquot '.xml' files for import:";

                    File aliquotXmlFolder = FileHelper.AllPlatformGetFolder(dialogTitle, null);
                    Path dir = aliquotXmlFolder.toPath();

                    try (DirectoryStream<Path> stream
                            = Files.newDirectoryStream(dir, "*.{xml,XML}")) {
                        for (Path entry : stream) {
                            System.out.println(entry.getFileName());

                            AliquotInterface myAliquotFile = new UPbReduxAliquot();
                            try {
                                myAliquotFile
                                        = (AliquotInterface) ((XMLSerializationI) myAliquotFile).readXMLObject(
                                                entry.toAbsolutePath().toString(), true);

                                System.out.println(myAliquotFile.getAliquotName());

                                if (myAliquotFile.usesIDTIMS() || myAliquotFile.usesMCIPMS()) {
                                    // xml is added here for consistency and because we test whether aliquot source file is xml ... probably
                                    // should get rid of xml test and just make it aliquot non-zero length string
                                    try {
                                        SampleInterface.importAliquotIntoSample(//
                                                mySample, myAliquotFile, entry.toAbsolutePath().toString());
                                        success = "Found";
                                        System.out.println("got one " + entry.toAbsolutePath().toString());
                                    } catch (IOException | ETException iOException) {
                                        System.out.println("BAD one " + myAliquotFile.getAnalystName());
                                    }
                                } else {
                                    throw new ETException("This sample is not TIMS or LAICPMS but is " + myAliquotFile.getAliquotInstrumentalMethod());
                                }
                            } catch (FileNotFoundException | ETException | BadOrMissingXMLSchemaException fileNotFoundException) {
                                System.out.println("NOT one " + myAliquotFile.getAnalystName());
                            }
                        }

                        if (success.contains("Found")) {
                            mySample.setSampleName(sampleName_text.getText());

                            mySample.setSampleAnnotations(sampleNotes_textArea.getText());

                            setInitialized(true);
                        }

                    } catch (IOException x) {
                        // IOException can never be thrown by the iteration.
                        // In this snippet, it can // only be thrown by newDirectoryStream.
                        System.err.println(x);
                    }

                    break;
                case ONE_OR_MORE_GEOCHRON:
                    success = GeochronRetrievalUtility.importOneOrMoreGeochronAliquotXMLDataFiles(//
                            mySample,//
                            reduxPreferences.getGeochronUserName(),//
                            reduxPreferences.getGeochronPassWord());
                    if (success.contains("Found")) {
                        mySample.setSampleName(sampleName_text.getText());

                        mySample.setSampleAnnotations(sampleNotes_textArea.getText());

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
    public SampleInterface getMySample() {
        return mySample;
    }

    /**
     *
     * @param mySample
     */
    public void setMySample(SampleInterface mySample) {
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
        validateGeochronCredentials_button = new ET_JButton();
        geochronPassword_passwordField = new javax.swing.JPasswordField();
        visitGeochron_button = new ET_JButton();
        userNameGeochron_label = new javax.swing.JLabel();
        passwordGeochron_label = new javax.swing.JLabel();
        credentialsValidReport_label = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        sampleName_label1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        cancel_button = new ET_JButton();
        saveAndCloseAndProceedToAliquotChooser_button = new ET_JButton();
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
        sampleName_label.setBounds(10, 80, 224, 25);

        sampleName_text.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        sampleName_text.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        sampleName_text.setText("Sample Name");
        jPanel1.add(sampleName_text);
        sampleName_text.setBounds(240, 80, 230, 25);

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
        jPanel3.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        sourceFolder_jRadioButton.setBackground(new java.awt.Color(255, 255, 224));
        sourceOfFractionsOptions_buttonGroup.add(sourceFolder_jRadioButton);
        sourceFolder_jRadioButton.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        sourceFolder_jRadioButton.setText("<html>Local Aliquots folder <b><font color=\"red\"> New</b></html>");
        sourceFolder_jRadioButton.setActionCommand(fractionSource.FOLDER_LOCAL.toString());
        sourceFolder_jRadioButton.setContentAreaFilled(false);
        sourceFolder_jRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sourceFolder_jRadioButtonActionPerformed(evt);
            }
        });
        jPanel3.add(sourceFolder_jRadioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 42, 189, -1));

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
        jPanel3.add(sourceSingle_jRadioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 18, 189, -1));

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
        jPanel3.add(sourceGeochron_jRadioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(12, 80, 401, -1));

        geochronUserName_text.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        geochronUserName_text.setText("username");
        jPanel3.add(geochronUserName_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 110, 159, 25));

        validateGeochronCredentials_button.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        validateGeochronCredentials_button.setForeground(new java.awt.Color(255, 51, 51));
        validateGeochronCredentials_button.setText("<html><b>Validate</b> GeoPass ID</html>");
        validateGeochronCredentials_button.setName("false"); // NOI18N
        validateGeochronCredentials_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                validateGeochronCredentials_buttonActionPerformed(evt);
            }
        });
        jPanel3.add(validateGeochronCredentials_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 140, 165, 25));

        geochronPassword_passwordField.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        geochronPassword_passwordField.setText("############");
        jPanel3.add(geochronPassword_passwordField, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 140, 159, 25));

        visitGeochron_button.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        visitGeochron_button.setText("Visit Geochron");
        visitGeochron_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                visitGeochron_buttonActionPerformed(evt);
            }
        });
        jPanel3.add(visitGeochron_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 170, 325, 25));

        userNameGeochron_label.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        userNameGeochron_label.setText("user name:");
        jPanel3.add(userNameGeochron_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 110, -1, 25));

        passwordGeochron_label.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        passwordGeochron_label.setText("password:");
        jPanel3.add(passwordGeochron_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 140, -1, 25));

        credentialsValidReport_label.setBackground(new java.awt.Color(255, 255, 255));
        credentialsValidReport_label.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        credentialsValidReport_label.setForeground(new java.awt.Color(255, 51, 51));
        credentialsValidReport_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        credentialsValidReport_label.setText("credentials are VALID");
        credentialsValidReport_label.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        credentialsValidReport_label.setOpaque(true);
        jPanel3.add(credentialsValidReport_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 110, 165, 25));
        jPanel3.add(jSeparator1, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 66, 400, -1));

        jPanel1.add(jPanel3);
        jPanel3.setBounds(31, 109, 440, 201);

        sampleName_label1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        sampleName_label1.setText("<html>Note: Compilation mode provides a mechanism to load in various aliquots - that have been exported by ET_Redux into '.xml' files - from the same or different Samples and to view and save them as a single ET_Redux compiled sample\n file.</html>");
        jPanel1.add(sampleName_label1);
        sampleName_label1.setBounds(8, 8, 470, 60);

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
                .add(saveAndCloseAndProceedToAliquotChooser_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(cancel_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
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
            SaveAndImportSampleData();
            close();
        } catch (BadLabDataException | BadOrMissingXMLSchemaException | IOException ex) {
            Logger.getLogger(SampleCompilationManagerDialog.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ETException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }

    }//GEN-LAST:event_saveAndCloseAndProceedToAliquotChooser_buttonActionPerformed

    private void validateGeoPassID(boolean isVerbose) {
        String userCode
                = //
                GeoPassIDValidator.validateGeoPassID(//
                        geochronUserName_text.getText().trim(),//
                        new String(geochronPassword_passwordField.getPassword()),
                        isVerbose);

        boolean valid = (userCode.trim().length() > 0) && (!userCode.equalsIgnoreCase("NONEXXXXX"));
        if (valid) {
            credentialsValidReport_label.setText("GeoPass ID is VALID.");
        } else {
            credentialsValidReport_label.setText("GeoPass ID is NOT valid.");
        }

        saveAndCloseAndProceedToAliquotChooser_button.setEnabled(valid);
        credentialsValidReport_label.setVisible(true);

    }

    private void sourceSingle_jRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sourceSingle_jRadioButtonActionPerformed
        saveAndCloseAndProceedToAliquotChooser_button.setEnabled(true);
}//GEN-LAST:event_sourceSingle_jRadioButtonActionPerformed

    private void validateGeochronCredentials_buttonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_validateGeochronCredentials_buttonActionPerformed
        validateGeoPassID(true);
}//GEN-LAST:event_validateGeochronCredentials_buttonActionPerformed

    private void visitGeochron_buttonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_visitGeochron_buttonActionPerformed
        BrowserControl.displayURL("https://www.geochron.org/");
}//GEN-LAST:event_visitGeochron_buttonActionPerformed

    private void sourceGeochron_jRadioButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sourceGeochron_jRadioButtonActionPerformed
        validateGeoPassID(false);
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
    private javax.swing.JSeparator jSeparator1;
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
