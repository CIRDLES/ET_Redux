/*
 * Copyright 2015 CIRDLES.
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
package org.earthtime.archivingTools.forSESAR;

import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import javax.swing.DefaultComboBoxModel;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import static javax.swing.SwingConstants.RIGHT;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.utilities.BrowserControl;
import static org.earthtime.archivingTools.GeoSamplesWebServices.CURRENT_GEOSAMPLES_WEBSERVICE_FOR_DOWNLOAD_IGSN;
import static org.earthtime.archivingTools.GeoSamplesWebServices.isSampleRegistered;
import static org.earthtime.archivingTools.GeoSamplesWebServices.isWellFormedIGSN;
import static org.earthtime.archivingTools.GeoSamplesWebServices.registerSampleAtGeoSamplesIGSN;
import org.earthtime.beans.ET_JButton;
import org.earthtime.dialogs.DialogEditor;
import org.geosamples.XMLDocumentInterface;
import org.geosamples.samples.Samples;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public class SesarSampleManager extends DialogEditor {

    private Samples.Sample sesarSample;
    private final static int ROW_HEIGHT = 25;
    private JTextField sampleIGSNText;
    private JCheckBox autoGenerateCheckBox;
    private String localSampleName;
    private String userName;
    private String password;

    /**
     * Creates new form SesarSampleManager
     *
     * @param parent
     * @param modal
     * @param sesarSample
     * @param localSampleName
     * @param editable
     * @param userName the value of userName
     * @param password the value of password
     */
    public SesarSampleManager(
            Frame parent, boolean modal, Samples.Sample sesarSample, String localSampleName, boolean editable, String userName, String password) {
        super(parent, modal);

        this.sesarSample = sesarSample;
        this.localSampleName = localSampleName;
        this.userName = userName;
        this.password = password;

        initComponents();

        initDialogContent();
        initSesarSample(editable);
    }

    @Override
    public void initDialogContent() {
        setSize(450, 353);
        setTitle("Sesar IGSN Manager");
        close_button.setBackground(Color.WHITE);
    }

    private void initSesarSample(boolean editable) {

        sesarSampleDetailsLayeredPane.add(labelFactory("IGSN:", 10, 10, 100));

        sampleIGSNText = new JTextField(sesarSample.getIgsn());
        sampleIGSNText.setEditable(editable);
        sampleIGSNText.setBounds(120, 10, 100, ROW_HEIGHT);
        sampleIGSNText.setFont(ReduxConstants.sansSerif_12_Bold);
        sesarSampleDetailsLayeredPane.add(sampleIGSNText);

        if (editable) {
            autoGenerateCheckBox = new JCheckBox("Let SESAR generate IGSN");
            autoGenerateCheckBox.setBounds(225, 10, 250, ROW_HEIGHT);
            autoGenerateCheckBox.setFont(ReduxConstants.sansSerif_12_Bold);
            autoGenerateCheckBox.setSelected(false);
            sesarSampleDetailsLayeredPane.add(autoGenerateCheckBox);
            autoGenerateCheckBox.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent event) {
                    JCheckBox cb = (JCheckBox) event.getSource();
                    if (cb.isSelected()) {
                        sampleIGSNText.setText(sesarSample.getUserCode() + "------".substring(0, (9 - sesarSample.getUserCode().length())));
                        sampleIGSNText.setEditable(false);
                    } else {
                        sampleIGSNText.setText(sesarSample.getIgsn());
                        sampleIGSNText.setEditable(true);
                    }
                }
            });
        } else {
            // provide button to see full record
            JButton viewSesarRecordButton = new ET_JButton("View full record at SESAR");
            viewSesarRecordButton.setBounds(225, 10, 200, ROW_HEIGHT);
            viewSesarRecordButton.setFont(ReduxConstants.sansSerif_12_Bold);
            sesarSampleDetailsLayeredPane.add(viewSesarRecordButton);
            viewSesarRecordButton.addActionListener((ActionEvent e) -> {
                BrowserControl.displayURL(CURRENT_GEOSAMPLES_WEBSERVICE_FOR_DOWNLOAD_IGSN + sesarSample.getIgsn());
            });
        }

        sesarSampleDetailsLayeredPane.add(labelFactory("Sample Name:", 10, 40, 100));

        JTextField sampleNameText = new JTextField(sesarSample.getName());
        sampleNameText.setEditable(false);
        sampleNameText.setBounds(120, 40, 150, ROW_HEIGHT);
        sampleNameText.setFont(ReduxConstants.sansSerif_12_Bold);
        sesarSampleDetailsLayeredPane.add(sampleNameText);

        if (!editable) {
            // check for name inconsistency
            if (sesarSample.getName().compareToIgnoreCase(localSampleName) != 0) {
                JLabel nameClashWarning = labelFactory("<html>The local sample name <" + localSampleName + "> differs!</html>", 275, 28, 150);
                nameClashWarning.setForeground(Color.red);
                nameClashWarning.setSize(150, 50);
                sesarSampleDetailsLayeredPane.add(nameClashWarning);
            }
        }

        sesarSampleDetailsLayeredPane.add(labelFactory("Sample Type:", 10, 70, 100));

        JComboBox<org.geosamples.samples.SampleType> sesarSampleTypesCombo = new JComboBox<>();
        sesarSampleTypesCombo.setEnabled(editable);
        sesarSampleTypesCombo.setModel(new DefaultComboBoxModel<>(org.geosamples.samples.SampleType.values()));
        sesarSampleTypesCombo.setBounds(120, 70, 200, ROW_HEIGHT);
        sesarSampleTypesCombo.setFont(ReduxConstants.sansSerif_12_Bold);
        sesarSampleDetailsLayeredPane.add(sesarSampleTypesCombo);
        sesarSampleTypesCombo.setSelectedItem(org.geosamples.samples.SampleType.valueOf(sesarSample.getSampleType().toUpperCase().replaceAll(" ", "_")));

        sesarSampleDetailsLayeredPane.add(labelFactory("Material Type:", 10, 100, 100));

        JComboBox<org.geosamples.samples.Material> sesarMaterialTypesCombo = new JComboBox<>();
        sesarMaterialTypesCombo.setEnabled(editable);
        sesarMaterialTypesCombo.setModel(new DefaultComboBoxModel<>(org.geosamples.samples.Material.values()));
        sesarMaterialTypesCombo.setBounds(120, 100, 200, ROW_HEIGHT);
        sesarMaterialTypesCombo.setFont(ReduxConstants.sansSerif_12_Bold);
        sesarSampleDetailsLayeredPane.add(sesarMaterialTypesCombo);
        sesarMaterialTypesCombo.setSelectedItem(org.geosamples.samples.Material.valueOf(sesarSample.getMaterial().toUpperCase().replaceAll(" ", "_")));

//        sesarSampleDetailsLayeredPane.add(labelFactory("Classification:", 10, 130, 100));
//
//        JComboBox<org.geosamples.samples.Classification> sesarClassificationCombo = new JComboBox<>();
//        sesarClassificationCombo.setEnabled(editable);
//        sesarClassificationCombo.setModel(new DefaultComboBoxModel<>(org.geosamples.samples.Classification.values()));
//        sesarClassificationCombo.setBounds(120, 130, 200, ROW_HEIGHT);
//        sesarClassificationCombo.setFont(ReduxConstants.sansSerif_12_Bold);
//        sesarSampleDetailsLayeredPane.add(sesarClassificationCombo);
//        sesarClassificationCombo.setSelectedItem(sesarSample.getClassification());
        sesarSampleDetailsLayeredPane.add(labelFactory("decimal Lat:", 10, 160, 100));
        JTextField decimalLatitude = new JTextField();
        decimalLatitude.setDocument(new BigDecimalDocument(decimalLatitude, editable));
        decimalLatitude.setText(sesarSample.getLatitude() == null ? "0" : sesarSample.getLatitude().setScale(6).toPlainString());
        decimalLatitude.setBounds(120, 160, 100, ROW_HEIGHT);
        decimalLatitude.setFont(ReduxConstants.sansSerif_12_Bold);
        sesarSampleDetailsLayeredPane.add(decimalLatitude);
        decimalLatitude.setInputVerifier(new InputVerifier() {

            @Override
            public boolean verify(JComponent input) {
                JTextField textField = (JTextField) input;
                if (textField.getText().trim().length() == 0) {
                    textField.setText("0.0");
                }
                double latitude = Double.parseDouble(textField.getText().trim());
                if (Math.abs(latitude) > 90.0) {
                    textField.setText(latitude < 0 ? "-90.0" : "90.0");
                }
                return true;
            }
        });
        decimalLatitude.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if ((key == KeyEvent.VK_ENTER) || (key == KeyEvent.VK_TAB)) {
                    decimalLatitude.getInputVerifier().verify(decimalLatitude);
                }
            }
        }
        );

        sesarSampleDetailsLayeredPane.add(labelFactory("decimal Long:", 10, 190, 100));
        JTextField decimalLongitude = new JTextField();
        decimalLongitude.setDocument(new BigDecimalDocument(decimalLongitude, editable));
        decimalLongitude.setText(sesarSample.getLongitude() == null ? "0" : sesarSample.getLongitude().setScale(6).toPlainString());
        decimalLongitude.setBounds(120, 190, 100, ROW_HEIGHT);
        decimalLongitude.setFont(ReduxConstants.sansSerif_12_Bold);
        sesarSampleDetailsLayeredPane.add(decimalLongitude);
        decimalLongitude.setInputVerifier(new InputVerifier() {

            @Override
            public boolean verify(JComponent input) {
                JTextField textField = (JTextField) input;
                if (textField.getText().trim().length() == 0) {
                    textField.setText("0.0");
                }
                double longitude = Double.parseDouble(textField.getText().trim());
                if (Math.abs(longitude) > 180.0) {
                    textField.setText(longitude < 0 ? "-180.0" : "180.0");
                }
                return true;
            }
        });
        decimalLongitude.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if ((key == KeyEvent.VK_ENTER) || (key == KeyEvent.VK_TAB)) {
                    decimalLongitude.getInputVerifier().verify(decimalLongitude);
                }
            }
        }
        );

        JButton showMapButton = new ET_JButton("Show map");
        showMapButton.setBounds(320, 145, 100, 25);
        showMapButton.setFont(ReduxConstants.sansSerif_12_Bold);
        sesarSampleDetailsLayeredPane.add(showMapButton);
        showMapButton.addActionListener((ActionEvent e) -> {
            CoordinateSystemConversions.launchGoogleMapsForLatLong(//
                    new BigDecimal(decimalLatitude.getText()), new BigDecimal(decimalLongitude.getText()));
        });

        if (editable) {
            JButton registerSampleButton = new ET_JButton("Register this Sample");
            registerSampleButton.setBounds(120, 230, 200, 25);
            registerSampleButton.setFont(ReduxConstants.sansSerif_12_Bold);
            sesarSampleDetailsLayeredPane.add(registerSampleButton);
            registerSampleButton.addActionListener((ActionEvent e) -> {
                boolean doRegister = false;
                String messageText = "";
                if (!autoGenerateCheckBox.isSelected()) {
                    String proposedIGSN = sampleIGSNText.getText();
                    if (!proposedIGSN.toUpperCase().startsWith(sesarSample.getUserCode().toUpperCase())) {
                        messageText = "User code prefix of IGSN should be: " + sesarSample.getUserCode();
                    } else if (isSampleRegistered(proposedIGSN)) {
                        messageText = "The IGSN: " + proposedIGSN + " is already in use.";
                    } else if (!isWellFormedIGSN(proposedIGSN, sesarSample.getUserCode())) {
                        messageText = "The IGSN: " + proposedIGSN + " is not of the form " + sesarSample.getUserCode() + "NNNNNNN\".substring(0, (9 - userCode.length())) + \", where N is any digit or any capital letter.";
                    } else {
                        sesarSample.setIgsn(proposedIGSN);
                        doRegister = true;
                    }
                } else {
                    sesarSample.setIgsn(null); // causes marshaller to ignore
                    doRegister = true;
                }
                if (doRegister) {
                    sesarSample.setSampleType(((org.geosamples.samples.SampleType) sesarSampleTypesCombo.getSelectedItem()).value());
                    sesarSample.setMaterial(((org.geosamples.samples.Material) sesarMaterialTypesCombo.getSelectedItem()).value());
                    sesarSample.setLatitude(new BigDecimal(decimalLatitude.getText()));
                    sesarSample.setLongitude(new BigDecimal(decimalLongitude.getText()));
                    // register at SESAR
                    XMLDocumentInterface success = registerSampleAtGeoSamplesIGSN(sesarSample, true, userName, password);
                    if (success != null) {
                        sesarSample.setIgsn(success.getSample().get(0).getIgsn());
                    } else {
                        sesarSample.setIgsn("NONE");
                        JOptionPane.showMessageDialog(
                                null,
                                new String[]{"There was a problem registering sample ... please inform developers."},
                                "ET Redux Warning",
                                JOptionPane.WARNING_MESSAGE);
                    }

                } else {
                    JOptionPane.showMessageDialog(
                            null,
                            new String[]{messageText},
                            "ET Redux Warning",
                            JOptionPane.WARNING_MESSAGE);
                }

                close();
            });
        }

    }

    private JLabel labelFactory(String text, int x, int y, int width) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, width, ROW_HEIGHT);
        label.setFont(ReduxConstants.sansSerif_12_Bold);
        label.setHorizontalAlignment(RIGHT);

        return label;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonBar_panel = new javax.swing.JPanel();
        close_button = new ET_JButton();
        sesarSampleDetailsLayeredPane = new javax.swing.JLayeredPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setSize(new java.awt.Dimension(450, 300));
        getContentPane().setLayout(null);

        buttonBar_panel.setBackground(new java.awt.Color(235, 255, 255));
        buttonBar_panel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        buttonBar_panel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        close_button.setBackground(new java.awt.Color(204, 204, 204));
        close_button.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        close_button.setForeground(new java.awt.Color(255, 51, 0));
        close_button.setText("Close");
        close_button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        close_button.setPreferredSize(new java.awt.Dimension(140, 23));
        close_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                close_buttonActionPerformed(evt);
            }
        });
        buttonBar_panel.add(close_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 3, 150, 25));

        getContentPane().add(buttonBar_panel);
        buttonBar_panel.setBounds(0, 300, 450, 28);

        sesarSampleDetailsLayeredPane.setBackground(new java.awt.Color(255, 255, 255));
        sesarSampleDetailsLayeredPane.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        sesarSampleDetailsLayeredPane.setOpaque(true);
        getContentPane().add(sesarSampleDetailsLayeredPane);
        sesarSampleDetailsLayeredPane.setBounds(0, 0, 450, 300);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void close_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_close_buttonActionPerformed
        close();
    }//GEN-LAST:event_close_buttonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel buttonBar_panel;
    private javax.swing.JButton close_button;
    private javax.swing.JLayeredPane sesarSampleDetailsLayeredPane;
    // End of variables declaration//GEN-END:variables
}
