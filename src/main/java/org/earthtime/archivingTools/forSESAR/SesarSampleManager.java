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
import static org.earthtime.archivingTools.GeoSamplesWebServices.isSampleRegistered;
import org.earthtime.beans.ET_JButton;
import org.earthtime.dataDictionaries.SESAR_MaterialTypesEnum;
import org.earthtime.dataDictionaries.SESAR_ObjectTypesEnum;
import org.earthtime.dialogs.DialogEditor;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public class SesarSampleManager extends DialogEditor {

    private SesarSample sesarSample;
    private final static int ROW_HEIGHT = 25;
    private JTextField sampleIGSNText;
    private JCheckBox autoGenerateCheckBox;

    /**
     * Creates new form SesarSampleManager
     */
    public SesarSampleManager(Frame parent, boolean modal, SesarSample sesarSample, boolean editable) {
        super(parent, modal);

        this.sesarSample = sesarSample;

        initComponents();

        setSize(450, 300);

        initSesarSample(editable);
    }

    private void initSesarSample(boolean editable) {

        sesarSampleDetailsLayeredPane.add(labelFactory("IGSN:", 10, 10, 100));

        sampleIGSNText = new JTextField(sesarSample.getIGSN());
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
                        sampleIGSNText.setText(sesarSample.getUser_code() + "------".substring(0, (9 - sesarSample.getUser_code().length())));
                        sampleIGSNText.setEditable(false);
                    } else {
                        sampleIGSNText.setText(sesarSample.getIGSN());
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
                BrowserControl.displayURL("http://app.geosamples.org/sample/igsn/" + sesarSample.getIGSN());
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
            if (sesarSample.hasNameClashBetweenLocalAndSesar()) {
                JLabel nameClashWarning = labelFactory("<html>The local sample name <" + sesarSample.getNameOfLocalSample() + "> differs!</html>", 275, 28, 150);
                nameClashWarning.setForeground(Color.red);
                nameClashWarning.setSize(150, 50);
                sesarSampleDetailsLayeredPane.add(nameClashWarning);
            }
        }

        sesarSampleDetailsLayeredPane.add(labelFactory("Sample Type:", 10, 70, 100));

        JComboBox<SESAR_ObjectTypesEnum> sesarObjectTypesCombo = new JComboBox<>();
        sesarObjectTypesCombo.setEnabled(editable);
        sesarObjectTypesCombo.setModel(new DefaultComboBoxModel<>(SESAR_ObjectTypesEnum.values()));
        sesarObjectTypesCombo.setBounds(120, 70, 200, ROW_HEIGHT);
        sesarObjectTypesCombo.setFont(ReduxConstants.sansSerif_12_Bold);
        sesarSampleDetailsLayeredPane.add(sesarObjectTypesCombo);
        sesarObjectTypesCombo.setSelectedItem(sesarSample.getSesarObjectType());

        sesarSampleDetailsLayeredPane.add(labelFactory("Material Type:", 10, 100, 100));

        JComboBox<SESAR_MaterialTypesEnum> sesarMaterialTypesCombo = new JComboBox<>();
        sesarMaterialTypesCombo.setEnabled(editable);
        sesarMaterialTypesCombo.setModel(new DefaultComboBoxModel<>(SESAR_MaterialTypesEnum.values()));
        sesarMaterialTypesCombo.setBounds(120, 100, 200, ROW_HEIGHT);
        sesarMaterialTypesCombo.setFont(ReduxConstants.sansSerif_12_Bold);
        sesarSampleDetailsLayeredPane.add(sesarMaterialTypesCombo);
        sesarMaterialTypesCombo.setSelectedItem(sesarSample.getSesarMaterialType());

        sesarSampleDetailsLayeredPane.add(labelFactory("decimal Lat:", 10, 130, 100));
        JTextField decimalLatitude = new JTextField();
        decimalLatitude.setDocument(new BigDecimalDocument(decimalLatitude, editable));
        decimalLatitude.setText(sesarSample.getLatitude().setScale(6).toPlainString());
        decimalLatitude.setBounds(120, 130, 100, ROW_HEIGHT);
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

        sesarSampleDetailsLayeredPane.add(labelFactory("decimal Long:", 10, 160, 100));
        JTextField decimalLongitude = new JTextField();
        decimalLongitude.setDocument(new BigDecimalDocument(decimalLongitude, editable));
        decimalLongitude.setText(sesarSample.getLongitude().setScale(6).toPlainString());
        decimalLongitude.setBounds(120, 160, 100, ROW_HEIGHT);
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
            registerSampleButton.setBounds(120, 200, 200, 25);
            registerSampleButton.setFont(ReduxConstants.sansSerif_12_Bold);
            sesarSampleDetailsLayeredPane.add(registerSampleButton);
            registerSampleButton.addActionListener((ActionEvent e) -> {
                boolean doRegister = false;
                String messageText = "";
                if (!autoGenerateCheckBox.isSelected()) {
                    String proposedIGSN = sampleIGSNText.getText();
                    if (!sesarSample.confirmUserCodeCompliance(proposedIGSN)) {
                        messageText = "User code prefix of IGSN should be: " + sesarSample.getUser_code();
                    } else if (isSampleRegistered(proposedIGSN)) {
                        messageText = "The IGSN: " + proposedIGSN + " is already in use.";
                    } else if (!SesarSample.isWellFormedIGSN(proposedIGSN, sesarSample.getUser_code())) {
                        messageText = "The IGSN: " + proposedIGSN + " is not of the form " + sesarSample.getUser_code() + "NNNNNNN\".substring(0, (9 - userCode.length())) + \", where N is any digit or any capital letter.";
                    } else {
                        sesarSample.setIGSN(proposedIGSN);
                        doRegister = true;
                    }
                } else {
                    sesarSample.setIGSN("");
                    doRegister = true;
                }
                if (doRegister) {
                    sesarSample.setSampleType(((SESAR_ObjectTypesEnum) sesarObjectTypesCombo.getSelectedItem()).getName());
                    sesarSample.setMaterial(((SESAR_MaterialTypesEnum) sesarMaterialTypesCombo.getSelectedItem()).getName());
                    sesarSample.setLatitude(new BigDecimal(decimalLatitude.getText()));
                    sesarSample.setLongitude(new BigDecimal(decimalLongitude.getText()));
                    // register at SESAR
                    String igsnValue = sesarSample.uploadAndRegisterSesarSample();
                    if (igsnValue.length() > 0) {
                        sesarSample.setIGSN(igsnValue);
                        close();
                    } else {
                        sesarSample.setIGSN("NONE");
                    }
                    
                } else {
                    JOptionPane.showMessageDialog(
                            null,
                            new String[]{messageText},
                            "ET Redux Warning",
                            JOptionPane.WARNING_MESSAGE);
                }
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

        sesarSampleDetailsLayeredPane = new javax.swing.JLayeredPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(null);

        sesarSampleDetailsLayeredPane.setBackground(new java.awt.Color(255, 255, 255));
        sesarSampleDetailsLayeredPane.setOpaque(true);
        getContentPane().add(sesarSampleDetailsLayeredPane);
        sesarSampleDetailsLayeredPane.setBounds(0, 0, 630, 470);

        pack();
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLayeredPane sesarSampleDetailsLayeredPane;
    // End of variables declaration//GEN-END:variables
}
