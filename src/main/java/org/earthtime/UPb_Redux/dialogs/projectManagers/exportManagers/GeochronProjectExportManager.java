/*
 * GeochronProjectExportManager.java
 *
 * Copyright 2006-2015 James F. Bowring and www.Earth-Time.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.earthtime.UPb_Redux.dialogs.projectManagers.exportManagers;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Frame;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import org.earthtime.UPb_Redux.user.ReduxPersistentState;
import org.earthtime.archivingTools.GeochronAliquotManager;
import org.earthtime.archivingTools.IEDACredentialsValidator;
import org.earthtime.beans.ET_JButton;
import org.earthtime.dialogs.DialogEditor;
import org.earthtime.projects.ProjectInterface;
import org.earthtime.reportViews.ReportPainterI;
import org.earthtime.samples.SampleInterface;

/**
 *
 * @author sbowring
 */
public class GeochronProjectExportManager extends DialogEditor {

    private ReportPainterI parent;
    private ReduxPersistentState myState;
    private ProjectInterface project;
    private String userCode;
    private String sesarUserCodeForConfirm;
    private boolean userIsValidated;
    private boolean samplesAreLoaded;
    private ArrayList<JButton> sampleShowConcordiaButtons;
    private ArrayList<JButton> sampleShowPDFButtons;
    private ArrayList<JCheckBox> samplePublicCheckBoxes;

    private ArrayList<JButton> sampleUploadButtons;

    /**
     * Creates new form GeochronProjectExportManager
     *
     * @param parent the value of parent
     * @param modal the value of modal
     * @param project the value of project
     * @param myState the value of myState
     */
    public GeochronProjectExportManager(ReportPainterI parent, boolean modal, ProjectInterface project, ReduxPersistentState myState) {
        super((Frame) parent, modal);

        this.parent = parent;
        this.myState = myState;
        this.project = project;
        this.userCode = "";
        this.sesarUserCodeForConfirm = "";
        this.userIsValidated = false;
        this.samplesAreLoaded = false;

        initComponents();
        setSize(1200, 750);

        initDialogContent();

        processUserValidation();

    }

    @Override
    public void initDialogContent() {

        geochronUserName_text.setDocument(new UnDoAbleDocument(geochronUserName_text, true));
        geochronUserName_text.setText(myState.getReduxPreferences().getGeochronUserName());

        geochronPassword_passwordField.setDocument(new UnDoAbleDocument(geochronPassword_passwordField, true));
        geochronPassword_passwordField.setText(myState.getReduxPreferences().getGeochronPassWord());

        validateGeochronAndSESARCredentials(false);

    }

    private void initSamplesDisplay() {
        aliquotsLayeredPane.removeAll();
        
        sampleShowConcordiaButtons = new ArrayList<>();
        sampleShowPDFButtons = new ArrayList<>();
        samplePublicCheckBoxes = new ArrayList<>();
        sampleUploadButtons = new ArrayList<>();

        int leftMargin = 40;
        int topMarginForSampleDetails = 10;

        int row = 0;
        for (SampleInterface sample : project.getProjectSamples()) {
            JPanel geochronAliquotManager = //
                    new GeochronAliquotManager(//
                            project,// needs to be interfaced
                            sample, //
                            myState.getReduxPreferences().getGeochronUserName(), //
                            myState.getReduxPreferences().getGeochronPassWord(), //
                            userCode, //
                            leftMargin, //
                            topMarginForSampleDetails + row * 100, 1100, 100);
            aliquotsLayeredPane.add(geochronAliquotManager, JLayeredPane.DEFAULT_LAYER);
            geochronAliquotManager.repaint();

            row++;
        }

        aliquotsLayeredPane.setPreferredSize(new Dimension(1100, topMarginForSampleDetails + (row + 1) * 100));
        aliquotsLayeredPane.validate();

        instructionsTextPane.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    if (Desktop.isDesktopSupported()) {
                        try {
                            Desktop.getDesktop().browse(e.getURL().toURI());
                        } catch (IOException | URISyntaxException e1) {
                            // TODO Auto-generated catch block
                            e1.printStackTrace();
                        }
                    }
                }
            }
        });

    }

//    private void updateValidSampleDisplay(int row, boolean visible) {
//        sampleShowConcordiaButtons.get(row).setVisible(visible);
//        sampleShowPDFButtons.get(row).setVisible(visible);
//        samplePublicCheckBoxes.get(row).setVisible(visible);
//
//        sampleUploadButtons.get(row).setVisible(visible);
//    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        exportManagerLayeredPane = new javax.swing.JLayeredPane();
        geochronPassword_passwordField = new javax.swing.JPasswordField();
        geochronUserName_text = new javax.swing.JTextField();
        geochronCredentialsValidReport_label = new javax.swing.JLabel();
        validateGeochronAndSesarCredentials_button = new ET_JButton();
        userNameGeochron_label = new javax.swing.JLabel();
        passwordGeochron_label = new javax.swing.JLabel();
        step1_label = new javax.swing.JLabel();
        credentialSummaryLabel = new javax.swing.JLabel();
        step2_label = new javax.swing.JLabel();
        sesarCredentialsValidReport_label = new javax.swing.JLabel();
        instructionsScrollPane = new javax.swing.JScrollPane();
        instructionsTextPane = new javax.swing.JTextPane();
        aliquotsScrollPane = new javax.swing.JScrollPane();
        aliquotsLayeredPane = new javax.swing.JLayeredPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(null);

        exportManagerLayeredPane.setBackground(new java.awt.Color(245, 255, 255));
        exportManagerLayeredPane.setOpaque(true);
        exportManagerLayeredPane.setPreferredSize(new java.awt.Dimension(1000, 600));

        geochronPassword_passwordField.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        geochronPassword_passwordField.setText("############");
        exportManagerLayeredPane.add(geochronPassword_passwordField);
        geochronPassword_passwordField.setBounds(380, 160, 150, 25);

        geochronUserName_text.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        geochronUserName_text.setText("username");
        exportManagerLayeredPane.add(geochronUserName_text);
        geochronUserName_text.setBounds(380, 130, 150, 25);

        geochronCredentialsValidReport_label.setBackground(new java.awt.Color(255, 255, 255));
        geochronCredentialsValidReport_label.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        geochronCredentialsValidReport_label.setForeground(new java.awt.Color(102, 204, 0));
        geochronCredentialsValidReport_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        geochronCredentialsValidReport_label.setText("GeoChron credentials are NOT valid.");
        geochronCredentialsValidReport_label.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        geochronCredentialsValidReport_label.setOpaque(true);
        geochronCredentialsValidReport_label.setPreferredSize(new java.awt.Dimension(255, 25));
        exportManagerLayeredPane.add(geochronCredentialsValidReport_label);
        geochronCredentialsValidReport_label.setBounds(710, 130, 360, 25);

        validateGeochronAndSesarCredentials_button.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        validateGeochronAndSesarCredentials_button.setForeground(new java.awt.Color(255, 51, 51));
        validateGeochronAndSesarCredentials_button.setText("<html><style>p{margin:10px;text-align:center;}</style>\n<p>Validate credentials at <b>Geochron</b> and <b>SESAR</b></p>\n</html>");
        validateGeochronAndSesarCredentials_button.setActionCommand("<html><style>p{margin:1px;text-align:center;}</style>\n<p>Validate credentials at <b>Geochron</b> and <b>SESAR</b></p>\n</html>");
        validateGeochronAndSesarCredentials_button.setName("false"); // NOI18N
        validateGeochronAndSesarCredentials_button.setPreferredSize(new java.awt.Dimension(255, 25));
        validateGeochronAndSesarCredentials_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                validateGeochronAndSesarCredentials_buttonActionPerformed(evt);
            }
        });
        exportManagerLayeredPane.add(validateGeochronAndSesarCredentials_button);
        validateGeochronAndSesarCredentials_button.setBounds(530, 130, 180, 55);

        userNameGeochron_label.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        userNameGeochron_label.setText("user name:");
        exportManagerLayeredPane.add(userNameGeochron_label);
        userNameGeochron_label.setBounds(300, 130, 68, 25);

        passwordGeochron_label.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        passwordGeochron_label.setText("password:");
        exportManagerLayeredPane.add(passwordGeochron_label);
        passwordGeochron_label.setBounds(300, 160, 63, 25);

        step1_label.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        step1_label.setText("Step 1) Validate GeoPass credentials:");
        exportManagerLayeredPane.add(step1_label);
        step1_label.setBounds(10, 130, 280, 25);

        credentialSummaryLabel.setFont(new java.awt.Font("SansSerif", 3, 14)); // NOI18N
        credentialSummaryLabel.setText("Note: your user code is XXXXX and your IGSNs will be of the form XXXXXNNNN, where N is any digit or any capital letter.");
        exportManagerLayeredPane.add(credentialSummaryLabel);
        credentialSummaryLabel.setBounds(190, 190, 880, 25);

        step2_label.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        step2_label.setText("Step 2) Confirm Sample Details and upload to Geochron.org (Note - uploads overwrite previous uploads):");
        exportManagerLayeredPane.add(step2_label);
        step2_label.setBounds(10, 220, 830, 25);

        sesarCredentialsValidReport_label.setBackground(new java.awt.Color(255, 255, 255));
        sesarCredentialsValidReport_label.setFont(new java.awt.Font("Tahoma", 3, 12)); // NOI18N
        sesarCredentialsValidReport_label.setForeground(new java.awt.Color(102, 204, 0));
        sesarCredentialsValidReport_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        sesarCredentialsValidReport_label.setText("SESAR credentials are NOT valid.");
        sesarCredentialsValidReport_label.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        sesarCredentialsValidReport_label.setOpaque(true);
        sesarCredentialsValidReport_label.setPreferredSize(new java.awt.Dimension(255, 25));
        exportManagerLayeredPane.add(sesarCredentialsValidReport_label);
        sesarCredentialsValidReport_label.setBounds(710, 160, 360, 25);

        instructionsScrollPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        instructionsScrollPane.setFont(new java.awt.Font("Serif", 0, 12)); // NOI18N

        instructionsTextPane.setEditable(false);
        instructionsTextPane.setContentType("text/html"); // NOI18N
        instructionsTextPane.setText("<html>\n  <head>\n\n  </head>\n  <body>\n    <p style=\"margin: 5px 0px 0px 5px; font-family:sansserif; font-size:14\">\n      <strong style=\"color:red\">Instructions: </strong>We want you to uniquely register your samples and aliquots by giving them an IGSN (International Geo Sample Number).  \n\tTo do so, you need credentials -<strong style=\"color:red\">username</strong> and <strong style=\"color:red\">password</strong> - established \n\tat <a href=\"https://geopass.iedadata.org\">GeoPass</a>.  \n\tYou use these credentials to register at <a href=\"http://geochron.org/\">Geochron</> and at <a href=\"http://geosamples.org\">SEASAR</a>.\n\tAt SESAR, you will choose a 5-letter user code (3-letter codes are grandfathered) that will be the prefix used for the IGSNs for every sample you register.\n\tFor each sample you register, you will specify (or ask SESAR to generate) a 4-character code (6 characters if you have 3-letter user code) using any combination\n\tof digits and capital letters.  For example, if your user code is JAMES, then an IGSN might be JAMES09AZ.  You will assign each <strong>Aliquot</strong> \n\tan IGSN in the same way and it will be a child of the parent sample's IGSN.  While you can specify any IGSN for the parent - say you have a piece of a sample from a colleague -\n\tyour Aliquot or child IGSNs will always have your user code as a prefix.\n    </p>\n  </body>\n</html>\n");
        instructionsScrollPane.setViewportView(instructionsTextPane);

        exportManagerLayeredPane.add(instructionsScrollPane);
        instructionsScrollPane.setBounds(10, 10, 1180, 110);

        aliquotsLayeredPane.setBackground(new java.awt.Color(245, 255, 255));
        aliquotsLayeredPane.setOpaque(true);
        aliquotsScrollPane.setViewportView(aliquotsLayeredPane);

        exportManagerLayeredPane.add(aliquotsScrollPane);
        aliquotsScrollPane.setBounds(10, 250, 1180, 450);

        getContentPane().add(exportManagerLayeredPane);
        exportManagerLayeredPane.setBounds(0, 0, 1200, 700);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        exportManagerLayeredPane.setSize(this.getSize());
    }//GEN-LAST:event_formComponentResized

    private void validateGeochronAndSesarCredentials_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_validateGeochronAndSesarCredentials_buttonActionPerformed
        validateGeochronAndSESARCredentials(false);
    }//GEN-LAST:event_validateGeochronAndSesarCredentials_buttonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        parent.loadAndShowReportTableData();
    }//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLayeredPane aliquotsLayeredPane;
    private javax.swing.JScrollPane aliquotsScrollPane;
    private javax.swing.JLabel credentialSummaryLabel;
    private javax.swing.JLayeredPane exportManagerLayeredPane;
    private javax.swing.JLabel geochronCredentialsValidReport_label;
    private javax.swing.JPasswordField geochronPassword_passwordField;
    private javax.swing.JTextField geochronUserName_text;
    private javax.swing.JScrollPane instructionsScrollPane;
    private javax.swing.JTextPane instructionsTextPane;
    private javax.swing.JLabel passwordGeochron_label;
    private javax.swing.JLabel sesarCredentialsValidReport_label;
    private javax.swing.JLabel step1_label;
    private javax.swing.JLabel step2_label;
    private javax.swing.JLabel userNameGeochron_label;
    private javax.swing.JButton validateGeochronAndSesarCredentials_button;
    // End of variables declaration//GEN-END:variables

    private void validateGeochronAndSESARCredentials(boolean isVerbose) {
        userCode = //
                IEDACredentialsValidator.validateGeochronCredentials(//
                        geochronUserName_text.getText().trim(),//
                        new String(geochronPassword_passwordField.getPassword()), isVerbose);

        boolean valid = (userCode.trim().length() > 0);
        if (valid) {
            geochronCredentialsValidReport_label.setText("GeoChron credentials are VALID. User Code = " + userCode);
        } else {
            geochronCredentialsValidReport_label.setText("GeoChron credentials are NOT valid.");
        }

        sesarUserCodeForConfirm = //
                IEDACredentialsValidator.validateSesarCredentials(//
                        geochronUserName_text.getText().trim(),//
                        new String(geochronPassword_passwordField.getPassword()), isVerbose);

        boolean validSesar = (sesarUserCodeForConfirm.trim().length() > 0);
        if (validSesar) {
            sesarCredentialsValidReport_label.setText("SESAR Credentials are VALID. User Code = " + sesarUserCodeForConfirm);
        } else {
            sesarCredentialsValidReport_label.setText("SESAR Credentials are NOT valid.");
        }

        if ((userCode.length() > 0) && (userCode.compareToIgnoreCase(sesarUserCodeForConfirm) == 0)) {
            credentialSummaryLabel.setText(//
                    "Note: your user code is " + userCode + " and your IGSNs will be of the form " //
                    + userCode + "NNNNNNN".substring(0, (9 - userCode.length())) + ", where N is any digit or any capital letter." //
            );
            userIsValidated = true;
        } else {
            userIsValidated = false;
        }

        samplesAreLoaded = false;
        
        processUserValidation();
    }

    private void processUserValidation() {
        credentialSummaryLabel.setVisible(userIsValidated);
        step2_label.setVisible(userIsValidated);

        aliquotsScrollPane.setVisible(userIsValidated);
        if (userIsValidated && !samplesAreLoaded) {
            initSamplesDisplay();
            samplesAreLoaded = true;
        } 
    }


}
