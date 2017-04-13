/*
 * GeochronProjectExportManager.java
 *
 * Copyright 2006-2017 James F. Bowring and www.Earth-Time.org
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

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Frame;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import org.earthtime.UPb_Redux.user.ReduxPersistentState;
import org.earthtime.archivingTools.GeoPassIDValidator;
import org.earthtime.archivingTools.GeochronAliquotManager;
import org.earthtime.beans.ET_JButton;
import org.earthtime.dialogs.DialogEditor;
import org.earthtime.projects.ProjectInterface;
import org.earthtime.reportViews.ReportPainterI;
import org.earthtime.samples.SampleInterface;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public final class GeochronProjectExportManager extends DialogEditor {

    private static GeochronProjectExportManager instance = null;

    private ReportPainterI parent;
    private ReduxPersistentState myState;
    private ProjectInterface project;
    private String geoSamplesUserCode;
    private boolean userIsValidated;
    private boolean samplesAreLoaded;

    public static GeochronProjectExportManager getInstance(ReportPainterI parent, boolean modal, ProjectInterface project, ReduxPersistentState myState) {

        removeInstance();
        instance = new GeochronProjectExportManager(parent, modal, project, myState);
        return instance;
    }

    public static void removeInstance() {
        if (instance != null) {
            try {
                instance.setVisible(false);
                instance.dispose();
                instance.finalize();
            } catch (Throwable ex) {
                Logger.getLogger(GeochronProjectExportManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Creates new form GeochronProjectExportManager
     *
     * @param parent the value of parent
     * @param modal the value of modal
     * @param project the value of project
     * @param myState the value of myState
     */
    private GeochronProjectExportManager(ReportPainterI parent, boolean modal, ProjectInterface project, ReduxPersistentState myState) {
        super((Frame) parent, modal);

        this.parent = parent;
        this.myState = myState;
        this.project = project;
        this.geoSamplesUserCode = "";
        this.userIsValidated = false;
        this.samplesAreLoaded = false;

        initComponents();

        initDialogContent();

        processUserValidation();

    }

    @Override
    public void initDialogContent() {

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
            java.util.logging.Logger.getLogger(org.cirdles.calamari.userInterface.CalamariUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        setSize(1200, 753);
        setTitle("Geochron Upload Manager");
        setResizable(true);
        saveAndClose_button.setBackground(Color.WHITE);

        geoPassUserName_text.setDocument(new UnDoAbleDocument(geoPassUserName_text, true));
        geoPassUserName_text.setText(myState.getReduxPreferences().getGeochronUserName());

        geoPassPassword_passwordField.setDocument(new UnDoAbleDocument(geoPassPassword_passwordField, true));
        geoPassPassword_passwordField.setText(myState.getReduxPreferences().getGeochronPassWord());

        validateGeoPassID(false);
    }

    private void initSamplesDisplay() {
        aliquotsLayeredPane.removeAll();

        int leftMargin = 40;
        int topMarginForSampleDetails = 10;

        int row = 0;
        int initialSampleHeight = 75;
        for (SampleInterface sample : project.getProjectSamples()) {
            if (!sample.isReferenceMaterial()) {
                JPanel geochronAliquotManager
                        = new GeochronAliquotManager(
                                project,// needs to be interfaced
                                sample, //
                                myState.getReduxPreferences().getGeochronUserName(), //
                                myState.getReduxPreferences().getGeochronPassWord(), //
                                geoSamplesUserCode, //
                                leftMargin, //
                                topMarginForSampleDetails + row * initialSampleHeight, 1100, initialSampleHeight);
                aliquotsLayeredPane.add(geochronAliquotManager, JLayeredPane.DEFAULT_LAYER);
                geochronAliquotManager.repaint();

                row++;
            }
        }

        aliquotsScrollPane.setPreferredSize(new Dimension(1100, topMarginForSampleDetails + (row + 1) * 100));
        aliquotsScrollPane.validate();
        aliquotsLayeredPane.setPreferredSize(new Dimension(1100, topMarginForSampleDetails + (row + 1) * 100));
        aliquotsLayeredPane.validate();

        instructionsTextPane.addHyperlinkListener((HyperlinkEvent e) -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                if (Desktop.isDesktopSupported()) {
                    try {
                        Desktop.getDesktop().browse(e.getURL().toURI());
                    } catch (IOException | URISyntaxException e1) {
                    }
                }
            }
        });

        revalidate();
    }

    private void validateGeoPassID(boolean isVerbose) {
        geoSamplesUserCode
                = GeoPassIDValidator.validateGeoPassID(//
                        geoPassUserName_text.getText().trim(),//
                        new String(geoPassPassword_passwordField.getPassword()),
                        isVerbose);

        boolean valid = (geoSamplesUserCode.trim().length() > 0) && (!geoSamplesUserCode.equalsIgnoreCase("NONEXXXXX"));
        if (valid) {
            geoPassIDValidReport_label.setText("GeoPass ID is VALID. User Code = " + geoSamplesUserCode);
            credentialSummaryLabel.setText(//
                    "Note: your user code is " + geoSamplesUserCode + " and your IGSNs will be of the form " //
                    + geoSamplesUserCode + "NNNNNNN".substring(0, (9 - geoSamplesUserCode.length())) + ", where N is any digit or any capital letter." //
            );
            userIsValidated = true;
        } else {
            geoPassIDValidReport_label.setText("GeoPass ID is NOT valid.");
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        exportManagerLayeredPane = new javax.swing.JLayeredPane();
        geoPassPassword_passwordField = new javax.swing.JPasswordField();
        geoPassUserName_text = new javax.swing.JTextField();
        geoPassIDValidReport_label = new javax.swing.JLabel();
        validateGeochronAndSesarCredentials_button = new ET_JButton();
        userNameGeochron_label = new javax.swing.JLabel();
        passwordGeochron_label = new javax.swing.JLabel();
        step1_label = new javax.swing.JLabel();
        credentialSummaryLabel = new javax.swing.JLabel();
        step2_label = new javax.swing.JLabel();
        instructionsScrollPane = new javax.swing.JScrollPane();
        instructionsTextPane = new javax.swing.JTextPane();
        aliquotsScrollPane = new javax.swing.JScrollPane();
        aliquotsLayeredPane = new javax.swing.JLayeredPane();
        buttonBar_panel = new javax.swing.JPanel();
        saveAndClose_button = new ET_JButton();

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
        exportManagerLayeredPane.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        geoPassPassword_passwordField.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        geoPassPassword_passwordField.setText("############");
        exportManagerLayeredPane.add(geoPassPassword_passwordField, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 170, 210, -1));

        geoPassUserName_text.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        geoPassUserName_text.setText("username");
        exportManagerLayeredPane.add(geoPassUserName_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(380, 140, 210, -1));

        geoPassIDValidReport_label.setBackground(new java.awt.Color(255, 255, 255));
        geoPassIDValidReport_label.setFont(new java.awt.Font("Lucida Grande", 3, 12)); // NOI18N
        geoPassIDValidReport_label.setForeground(new java.awt.Color(102, 204, 0));
        geoPassIDValidReport_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        geoPassIDValidReport_label.setText("GeoChron credentials are NOT valid.");
        geoPassIDValidReport_label.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        geoPassIDValidReport_label.setOpaque(true);
        geoPassIDValidReport_label.setPreferredSize(new java.awt.Dimension(255, 25));
        exportManagerLayeredPane.add(geoPassIDValidReport_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 140, 360, 55));

        validateGeochronAndSesarCredentials_button.setFont(new java.awt.Font("Lucida Grande", 3, 12)); // NOI18N
        validateGeochronAndSesarCredentials_button.setForeground(new java.awt.Color(255, 51, 51));
        validateGeochronAndSesarCredentials_button.setText("<html><style>p{margin:10px;text-align:center;}</style> <p>Validate credentials at <b>GeoPass</b></p> </html>");
        validateGeochronAndSesarCredentials_button.setActionCommand("<html><style>p{margin:1px;text-align:center;}</style>\n<p>Validate credentials at <b>Geochron</b> and <b>SESAR</b></p>\n</html>");
        validateGeochronAndSesarCredentials_button.setName("false"); // NOI18N
        validateGeochronAndSesarCredentials_button.setPreferredSize(new java.awt.Dimension(255, 25));
        validateGeochronAndSesarCredentials_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                validateGeochronAndSesarCredentials_buttonActionPerformed(evt);
            }
        });
        exportManagerLayeredPane.add(validateGeochronAndSesarCredentials_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 140, 180, 55));

        userNameGeochron_label.setFont(new java.awt.Font("Lucida Grande", 1, 12)); // NOI18N
        userNameGeochron_label.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        userNameGeochron_label.setText("user name:");
        exportManagerLayeredPane.add(userNameGeochron_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(283, 140, 90, 25));

        passwordGeochron_label.setFont(new java.awt.Font("Lucida Grande", 1, 12)); // NOI18N
        passwordGeochron_label.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        passwordGeochron_label.setText("password:");
        exportManagerLayeredPane.add(passwordGeochron_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(283, 170, 90, 25));

        step1_label.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        step1_label.setText("Step 1) Validate GeoPass credentials:");
        exportManagerLayeredPane.add(step1_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 140, 280, 25));

        credentialSummaryLabel.setFont(new java.awt.Font("Lucida Grande", 3, 14)); // NOI18N
        credentialSummaryLabel.setText("Note: your user code is XXXXX and your IGSNs will be of the form XXXXXNNNN, where N is any digit or any capital letter.");
        exportManagerLayeredPane.add(credentialSummaryLabel, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 200, 880, 25));

        step2_label.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        step2_label.setText("Step 2) Confirm Sample Details and upload to Geochron.org with Public? and Update? options.  NOTE: Currently uses SESAR testbed for IGSNs (not real):");
        exportManagerLayeredPane.add(step2_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 220, 1130, 25));

        instructionsScrollPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        instructionsScrollPane.setFont(new java.awt.Font("Serif", 0, 12)); // NOI18N

        instructionsTextPane.setEditable(false);
        instructionsTextPane.setContentType("text/html"); // NOI18N
        instructionsTextPane.setText("<html>\n  <head>\n\n  </head>\n  <body>\n    <p style=\"margin: 5px 0px 0px 5px; font-family:sansserif; font-size:14\">\n      <strong style=\"color:red\">Instructions: </strong>We want you to uniquely register your samples and aliquots by giving them an IGSN (International Geo Sample Number).  \n\tTo do so, you need credentials -<strong style=\"color:red\">username</strong> and <strong style=\"color:red\">password</strong> - established \n\tat <a href=\"https://geopass.iedadata.org\">GeoPass</a>.  \n\tYou use these credentials to register at <a href=\"http://geochron.org/\">Geochron</> and at <a href=\"http://geosamples.org\">SEASAR</a>.\n\tAt SESAR, you will choose a 5-letter user code (3-letter codes are grandfathered) that will be the prefix used for the IGSNs for every sample you register.\n\tFor each sample you register, you will specify (or ask SESAR to generate) a 4-character code (6 characters if you have 3-letter user code) using any combination\n\tof digits and capital letters.  For example, if your user code is JAMES, then an IGSN might be JAMES09AZ.  You will assign each <strong>Aliquot</strong> \n\tan IGSN in the same way and it will be a child of the parent sample's IGSN.  While you can specify any IGSN for the parent - say you have a piece of a sample from a colleague -\n\tyour Aliquot or child IGSNs will always have your user code as a prefix.\n    </p>\n  </body>\n</html>\n");
        instructionsScrollPane.setViewportView(instructionsTextPane);

        exportManagerLayeredPane.add(instructionsScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 0, 1170, 120));

        aliquotsLayeredPane.setBackground(new java.awt.Color(245, 255, 255));
        aliquotsLayeredPane.setOpaque(true);
        aliquotsScrollPane.setViewportView(aliquotsLayeredPane);

        exportManagerLayeredPane.add(aliquotsScrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 250, 1170, 450));

        buttonBar_panel.setBackground(new java.awt.Color(235, 255, 255));
        buttonBar_panel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        buttonBar_panel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        saveAndClose_button.setBackground(new java.awt.Color(204, 204, 204));
        saveAndClose_button.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        saveAndClose_button.setForeground(new java.awt.Color(255, 51, 0));
        saveAndClose_button.setText("Save and Close");
        saveAndClose_button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        saveAndClose_button.setPreferredSize(new java.awt.Dimension(140, 23));
        saveAndClose_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAndClose_buttonActionPerformed(evt);
            }
        });
        buttonBar_panel.add(saveAndClose_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 3, 150, 25));

        exportManagerLayeredPane.add(buttonBar_panel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 700, 1200, 30));

        getContentPane().add(exportManagerLayeredPane);
        exportManagerLayeredPane.setBounds(0, 0, 1200, 730);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        exportManagerLayeredPane.setSize(this.getSize());
    }//GEN-LAST:event_formComponentResized

    private void validateGeochronAndSesarCredentials_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_validateGeochronAndSesarCredentials_buttonActionPerformed
        validateGeoPassID(false);
    }//GEN-LAST:event_validateGeochronAndSesarCredentials_buttonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        //parent.loadAndShowReportTableData();
    }//GEN-LAST:event_formWindowClosing

    private void saveAndClose_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAndClose_buttonActionPerformed
        project.saveTheProjectAsSerializedReduxFile();
        close();
    }//GEN-LAST:event_saveAndClose_buttonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLayeredPane aliquotsLayeredPane;
    private javax.swing.JScrollPane aliquotsScrollPane;
    private javax.swing.JPanel buttonBar_panel;
    private javax.swing.JLabel credentialSummaryLabel;
    private javax.swing.JLayeredPane exportManagerLayeredPane;
    private javax.swing.JLabel geoPassIDValidReport_label;
    private javax.swing.JPasswordField geoPassPassword_passwordField;
    private javax.swing.JTextField geoPassUserName_text;
    private javax.swing.JScrollPane instructionsScrollPane;
    private javax.swing.JTextPane instructionsTextPane;
    private javax.swing.JLabel passwordGeochron_label;
    private javax.swing.JButton saveAndClose_button;
    private javax.swing.JLabel step1_label;
    private javax.swing.JLabel step2_label;
    private javax.swing.JLabel userNameGeochron_label;
    private javax.swing.JButton validateGeochronAndSesarCredentials_button;
    // End of variables declaration//GEN-END:variables

}
