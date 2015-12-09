/*
 * AliquotOptionsDialog.java
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
package org.earthtime.UPb_Redux.dialogs.aliquotManagers;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import org.earthtime.dialogs.DialogEditor;
import org.earthtime.UPb_Redux.listeners.ColorChooserListener;
import org.earthtime.UPb_Redux.user.SampleDateInterpretationGUIOptions;

/**
 *
 * @author  James F. Bowring
 */
public class AliquotOptionsDialog extends DialogEditor {

    // instance variables
    private String aliquotName;

    private Map<String, String> aliquotOptions;

    private Map<String, String> aliquotOptionsSaved;

    /** Creates new form AliquotOptionsDialog
     * @param parent 
     * @param modal 
     * @param aliquotName
     * @param aliquotOptions  
     */
    public AliquotOptionsDialog(java.awt.Frame parent,
            boolean modal, String aliquotName,
            Map<String, String> aliquotOptions) {

        super(parent, modal);

        setLocationRelativeTo(parent);
        setAlwaysOnTop(modal);

        initComponents();

        this.aliquotName = aliquotName;
        this.aliquotOptions = aliquotOptions;

        this.aliquotOptionsSaved = new HashMap<String, String>();
        aliquotOptionsSaved.putAll(aliquotOptions);

        alquotName_label.setText("Aliquot: " + getAliquotName());

        MouseListener myColorChooserListener =
                new ColorChooserListener(aliquotOptions);
        includedBorderColor_label.addMouseListener(myColorChooserListener);
        includedFillColor_label.addMouseListener(myColorChooserListener);
        includedCenterColor_label.addMouseListener(myColorChooserListener);
        excludedBorderColor_label.addMouseListener(myColorChooserListener);
        excludedFillColor_label.addMouseListener(myColorChooserListener);
        excludedCenterColor_label.addMouseListener(myColorChooserListener);


        CustomInit();

    }

    private void CustomInit() {
        // ELLIPSE *********************************************************
        // Included ellipses
        // Ellipse weight

        String includedBorderWeight = RetrieveOptionField("includedBorderWeight");
        SpinnerModel includedBorderWeight_spinnerModel =
                new SpinnerNumberModel(//
                (Number) Float.valueOf(includedBorderWeight), 0.0f, 10.0f, 0.5f);
        includedBorderWeight_spinner.setModel(includedBorderWeight_spinnerModel);

        // included ellipse border color
        Color colorRGB = RetrieveOptionColorField("includedBorderColor");
        includedBorderColor_label.setBackground(colorRGB);
        includedBorderColor_label.setForeground(InvertColor(colorRGB));

        colorRGB = RetrieveOptionColorField("includedFillColor");
        includedFillColor_label.setBackground(colorRGB);
        includedFillColor_label.setForeground(InvertColor(colorRGB));

        colorRGB = RetrieveOptionColorField("includedCenterColor");
        includedCenterColor_label.setBackground(colorRGB);
        includedCenterColor_label.setForeground(InvertColor(colorRGB));

        String includedFillTransparencyPCT = RetrieveOptionField("includedFillTransparencyPCT");
        SpinnerModel includedFillTransparencyPCT_spinnerModel =
                new SpinnerNumberModel(//
                (Number) Float.valueOf(includedFillTransparencyPCT), 0.0f, 100.0f, 5.0f);
        includedFillTransparencyPCT_spinner.setModel(includedFillTransparencyPCT_spinnerModel);

        String includedCenterSize = RetrieveOptionField("includedCenterSize");
        SpinnerModel includedCenterSize_spinnerModel =
                new SpinnerNumberModel(//
                (Number) Float.valueOf(includedCenterSize), 0.0f, 20.0f, 1.0f);
        includedCenterSize_spinner.setModel(includedCenterSize_spinnerModel);


        // Excluded ellipses
        // Ellipse weight

        String excludedBorderWeight = RetrieveOptionField("excludedBorderWeight");
        SpinnerModel excludedBorderWeight_spinnerModel =
                new SpinnerNumberModel(//
                (Number) Float.valueOf(excludedBorderWeight), 0.0f, 10.0f, 0.5f);
        excludedBorderWeight_spinner.setModel(excludedBorderWeight_spinnerModel);

        // excluded ellipse border color
        colorRGB = RetrieveOptionColorField("excludedBorderColor");
        excludedBorderColor_label.setBackground(colorRGB);
        excludedBorderColor_label.setForeground(InvertColor(colorRGB));

        colorRGB = RetrieveOptionColorField("excludedFillColor");
        excludedFillColor_label.setBackground(colorRGB);
        excludedFillColor_label.setForeground(InvertColor(colorRGB));

        colorRGB = RetrieveOptionColorField("excludedCenterColor");
        excludedCenterColor_label.setBackground(colorRGB);
        excludedCenterColor_label.setForeground(InvertColor(colorRGB));

        String excludedFillTransparencyPCT = RetrieveOptionField("excludedFillTransparencyPCT");
        SpinnerModel excludedFillTransparencyPCT_spinnerModel =
                new SpinnerNumberModel(//
                (Number) Float.valueOf(excludedFillTransparencyPCT), 0.0f, 100.0f, 5.0f);
        excludedFillTransparencyPCT_spinner.setModel(excludedFillTransparencyPCT_spinnerModel);

        String excludedCenterSize = RetrieveOptionField("excludedCenterSize");
        SpinnerModel excludedCenterSize_spinnerModel =
                new SpinnerNumberModel(//
                (Number) Float.valueOf(excludedCenterSize), 0.0f, 20.0f, 1.0f);
        excludedCenterSize_spinner.setModel(excludedCenterSize_spinnerModel);




        // Preferred Date Box
        // font
        String listOfFonts[] =
                GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

        dateBoxFonts_comboBox.removeAllItems();
        for (int i = 0; i < listOfFonts.length; i++) {
            dateBoxFonts_comboBox.addItem(listOfFonts[i].trim());
        }
        String dateFont = RetrieveOptionField("dateFont");
        dateBoxFonts_comboBox.setSelectedItem(dateFont.trim());

        // date font size
        String dateFontSize = RetrieveOptionField("dateFontSize");
        SpinnerModel dateFontSize_spinnerModel =
                new SpinnerNumberModel(//
                (int) Integer.parseInt(dateFontSize), 5, 30, 1);
        dateBoxFontSize_spinner.setModel(dateFontSize_spinnerModel);

        boolean visibleDateBoxOutline = Boolean.valueOf(RetrieveOptionField("visibleDateBoxOutline"));
        dateBoxShow_checkBox.setSelected(visibleDateBoxOutline);

        boolean dateShowAge = Boolean.valueOf(RetrieveOptionField("dateShowDate"));
        dateShowDate_chkBox.setSelected(dateShowAge);

        boolean dateShowMSWD = Boolean.valueOf(RetrieveOptionField("dateShowMSWD"));
        dateShowMSWD_chkBox.setSelected(dateShowMSWD);

        boolean dateShowN = Boolean.valueOf(RetrieveOptionField("dateShowN"));
        dateShowN_chkBox.setSelected(dateShowN);
    }

    private void SaveSettings() {
        // note colors are saved by colorlistener

        getAliquotOptions().put("includedBorderWeight", //
                Float.toString((Float) includedBorderWeight_spinner.getValue()));
        getAliquotOptions().put("includedFillTransparencyPCT", //
                Float.toString((Float) includedFillTransparencyPCT_spinner.getValue()));
        getAliquotOptions().put("includedCenterSize", //
                Float.toString((Float) includedCenterSize_spinner.getValue()));

        getAliquotOptions().put("excludedBorderWeight", //
                Float.toString((Float) excludedBorderWeight_spinner.getValue()));
        getAliquotOptions().put("excludedFillTransparencyPCT", //
                Float.toString((Float) excludedFillTransparencyPCT_spinner.getValue()));
        getAliquotOptions().put("excludedCenterSize", //
                Float.toString((Float) excludedCenterSize_spinner.getValue()));


        getAliquotOptions().put("dateFont", //
                (String) dateBoxFonts_comboBox.getSelectedItem());
        getAliquotOptions().put("dateFontSize", //
                Integer.toString((Integer) dateBoxFontSize_spinner.getValue()));
        getAliquotOptions().put("visibleDateBoxOutline", //
                Boolean.toString(dateBoxShow_checkBox.isSelected()));

        getAliquotOptions().put("dateShowDate", //
                Boolean.toString(dateShowDate_chkBox.isSelected()));
        getAliquotOptions().put("dateShowMSWD", //
                Boolean.toString(dateShowMSWD_chkBox.isSelected()));
        getAliquotOptions().put("dateShowN", //
                Boolean.toString(dateShowN_chkBox.isSelected()));


    }

    private String RetrieveOptionField(String optionFieldName) {
        String optionValue = null;
        optionValue = getAliquotOptions().get(optionFieldName);
        if (optionValue == null) {
            setAliquotOptions(new SampleDateInterpretationGUIOptions().getAliquotOptionsMapByName(getAliquotName(), 0));
            optionValue = getAliquotOptions().get(optionFieldName);
        }

        return optionValue;
    }

    private Color RetrieveOptionColorField(String optionFieldName) {
        String[] temp = RetrieveOptionField(optionFieldName).split(",");
        return new Color(//
                (int) Integer.parseInt(temp[0].trim()),
                (int) Integer.parseInt(temp[1].trim()),
                (int) Integer.parseInt(temp[2].trim()));
    }

    private Color InvertColor(Color colorRGB) {
        return new Color(
                255 - colorRGB.getRed(),
                255 - colorRGB.getGreen(),
                255 - colorRGB.getBlue());
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        interceptErrorLineStyle_btnGroup = new javax.swing.ButtonGroup();
        buttonsPanel = new javax.swing.JPanel();
        save_button = new javax.swing.JButton();
        close_button = new javax.swing.JButton();
        restoreDefaults_button = new javax.swing.JButton();
        revert_button = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        includedBorderColor_label = new javax.swing.JLabel();
        excludedBorderColor_label = new javax.swing.JLabel();
        includedFillColor_label = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        includedCenterColor_label = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        excludedFillColor_label = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        excludedCenterColor_label = new javax.swing.JLabel();
        alquotName_label = new javax.swing.JLabel();
        includedFillTransparencyPCT_spinner = new javax.swing.JSpinner();
        excludedFillTransparencyPCT_spinner = new javax.swing.JSpinner();
        includedCenterSize_spinner = new javax.swing.JSpinner();
        excludedCenterSize_spinner = new javax.swing.JSpinner();
        jLabel15 = new javax.swing.JLabel();
        includedBorderWeight_spinner = new javax.swing.JSpinner();
        excludedBorderWeight_spinner = new javax.swing.JSpinner();
        jLabel16 = new javax.swing.JLabel();
        dateBoxFontSize_spinner = new javax.swing.JSpinner();
        dateBoxFonts_comboBox = new javax.swing.JComboBox<String>();
        dateBoxShow_checkBox = new javax.swing.JCheckBox();
        dateShowDate_chkBox = new javax.swing.JCheckBox();
        dateShowMSWD_chkBox = new javax.swing.JCheckBox();
        dateShowN_chkBox = new javax.swing.JCheckBox();
        jLabel17 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Aliquot Ellipse Display Settings");
        setForeground(new java.awt.Color(255, 255, 255));
        setResizable(false);

        buttonsPanel.setBackground(new java.awt.Color(252, 236, 235));
        buttonsPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        save_button.setForeground(new java.awt.Color(255, 51, 0));
        save_button.setText("OK");
        save_button.setMargin(new java.awt.Insets(0, 1, 0, 1));
        save_button.setPreferredSize(new java.awt.Dimension(140, 23));
        save_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                save_buttonActionPerformed(evt);
            }
        });

        close_button.setForeground(new java.awt.Color(255, 51, 0));
        close_button.setText("Cancel");
        close_button.setMargin(new java.awt.Insets(0, 1, 0, 1));
        close_button.setPreferredSize(new java.awt.Dimension(140, 23));
        close_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                close_buttonActionPerformed(evt);
            }
        });

        restoreDefaults_button.setForeground(new java.awt.Color(255, 51, 0));
        restoreDefaults_button.setText("Restore Defaults");
        restoreDefaults_button.setMargin(new java.awt.Insets(0, 1, 0, 1));
        restoreDefaults_button.setPreferredSize(new java.awt.Dimension(140, 23));
        restoreDefaults_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                restoreDefaults_buttonActionPerformed(evt);
            }
        });

        revert_button.setForeground(new java.awt.Color(255, 51, 0));
        revert_button.setText("Revert to Saved");
        revert_button.setMargin(new java.awt.Insets(0, 1, 0, 1));
        revert_button.setPreferredSize(new java.awt.Dimension(140, 23));
        revert_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                revert_buttonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout buttonsPanelLayout = new org.jdesktop.layout.GroupLayout(buttonsPanel);
        buttonsPanel.setLayout(buttonsPanelLayout);
        buttonsPanelLayout.setHorizontalGroup(
            buttonsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(buttonsPanelLayout.createSequentialGroup()
                .add(save_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 75, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(12, 12, 12)
                .add(revert_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(restoreDefaults_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 147, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(11, 11, 11)
                .add(close_button, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE))
        );
        buttonsPanelLayout.setVerticalGroup(
            buttonsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(buttonsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(save_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(close_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(restoreDefaults_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(revert_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        jLabel1.setText("border weight in pixels; color:");

        jLabel2.setFont(new java.awt.Font("Lucida Grande", 1, 16)); // NOI18N
        jLabel2.setText("Ellipses - Included:");

        jLabel3.setFont(new java.awt.Font("Lucida Grande", 1, 16)); // NOI18N
        jLabel3.setText("Ellipses - Excluded:");

        jLabel4.setText("border weight pixels; color:");

        includedBorderColor_label.setBackground(new java.awt.Color(255, 255, 255));
        includedBorderColor_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        includedBorderColor_label.setText("click to change color");
        includedBorderColor_label.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        includedBorderColor_label.setName("includedBorderColor"); // NOI18N
        includedBorderColor_label.setOpaque(true);

        excludedBorderColor_label.setBackground(new java.awt.Color(255, 255, 255));
        excludedBorderColor_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        excludedBorderColor_label.setText("click to change color");
        excludedBorderColor_label.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        excludedBorderColor_label.setName("excludedBorderColor"); // NOI18N
        excludedBorderColor_label.setOpaque(true);

        includedFillColor_label.setBackground(new java.awt.Color(255, 255, 255));
        includedFillColor_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        includedFillColor_label.setText("click to change color");
        includedFillColor_label.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        includedFillColor_label.setName("includedFillColor"); // NOI18N
        includedFillColor_label.setOpaque(true);

        jLabel5.setText("fill opaqueness percent; color:");

        includedCenterColor_label.setBackground(new java.awt.Color(255, 255, 255));
        includedCenterColor_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        includedCenterColor_label.setText("click to change color");
        includedCenterColor_label.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        includedCenterColor_label.setName("includedCenterColor"); // NOI18N
        includedCenterColor_label.setOpaque(true);

        jLabel6.setText("center size pixels; color:");

        jLabel7.setText("fill opaqueness percent; color:");

        excludedFillColor_label.setBackground(new java.awt.Color(255, 255, 255));
        excludedFillColor_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        excludedFillColor_label.setText("click to change color");
        excludedFillColor_label.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        excludedFillColor_label.setName("excludedFillColor"); // NOI18N
        excludedFillColor_label.setOpaque(true);

        jLabel8.setText("center size pixels; color:");

        excludedCenterColor_label.setBackground(new java.awt.Color(255, 255, 255));
        excludedCenterColor_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        excludedCenterColor_label.setText("click to change color");
        excludedCenterColor_label.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        excludedCenterColor_label.setName("excludedCenterColor"); // NOI18N
        excludedCenterColor_label.setOpaque(true);

        alquotName_label.setBackground(new java.awt.Color(252, 236, 235));
        alquotName_label.setFont(new java.awt.Font("Lucida Grande", 1, 16)); // NOI18N
        alquotName_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        alquotName_label.setText("Aliquot: ");
        alquotName_label.setOpaque(true);

        jLabel15.setFont(new java.awt.Font("Lucida Grande", 1, 16)); // NOI18N
        jLabel15.setText("Preferred Date Interpretation Box:");

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel16.setText("choose font; size:");

        dateBoxShow_checkBox.setSelected(true);
        dateBoxShow_checkBox.setText("box outline");

        dateShowDate_chkBox.setSelected(true);
        dateShowDate_chkBox.setText("Date");

        dateShowMSWD_chkBox.setSelected(true);
        dateShowMSWD_chkBox.setText("MSWD");

        dateShowN_chkBox.setSelected(true);
        dateShowN_chkBox.setText("n");

        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel17.setText("show:");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(alquotName_label, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 527, Short.MAX_VALUE)
            .add(buttonsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel2)
                            .add(jLabel3))
                        .add(0, 339, Short.MAX_VALUE))
                    .add(jLabel15)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(87, 87, 87)
                                .add(jLabel16))
                            .add(layout.createSequentialGroup()
                                .add(35, 35, 35)
                                .add(jLabel17)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(dateBoxShow_checkBox)))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(dateBoxFonts_comboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 188, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 12, Short.MAX_VALUE)
                                .add(dateBoxFontSize_spinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 83, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(layout.createSequentialGroup()
                                .add(dateShowDate_chkBox)
                                .add(39, 39, 39)
                                .add(dateShowMSWD_chkBox)
                                .add(28, 28, 28)
                                .add(dateShowN_chkBox))))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(0, 81, Short.MAX_VALUE)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jLabel8)
                            .add(jLabel7)
                            .add(jLabel4)
                            .add(jLabel1)
                            .add(jLabel5)
                            .add(jLabel6))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, excludedCenterSize_spinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 75, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, excludedFillTransparencyPCT_spinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 74, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, excludedBorderWeight_spinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 75, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, excludedCenterColor_label)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, excludedFillColor_label)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, excludedBorderColor_label)))
                            .add(layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, includedBorderWeight_spinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 75, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                        .add(includedCenterSize_spinner)
                                        .add(includedFillTransparencyPCT_spinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 75, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, includedCenterColor_label)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, includedFillColor_label)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, includedBorderColor_label))))))
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {excludedBorderWeight_spinner, excludedCenterSize_spinner, excludedFillTransparencyPCT_spinner, includedBorderWeight_spinner, includedCenterSize_spinner, includedFillTransparencyPCT_spinner}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(alquotName_label)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(includedBorderColor_label)
                    .add(includedBorderWeight_spinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel1))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(includedFillColor_label)
                    .add(jLabel5)
                    .add(includedFillTransparencyPCT_spinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(includedCenterColor_label)
                    .add(jLabel6)
                    .add(includedCenterSize_spinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel3)
                .add(1, 1, 1)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(excludedBorderWeight_spinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(excludedBorderColor_label)
                    .add(jLabel4))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(excludedFillTransparencyPCT_spinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel7)
                    .add(excludedFillColor_label))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(excludedCenterColor_label)
                    .add(excludedCenterSize_spinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLabel8))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 18, Short.MAX_VALUE)
                .add(jLabel15)
                .add(8, 8, 8)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel16)
                    .add(dateBoxFonts_comboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(dateBoxFontSize_spinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(dateShowMSWD_chkBox)
                    .add(dateBoxShow_checkBox)
                    .add(jLabel17)
                    .add(dateShowDate_chkBox)
                    .add(dateShowN_chkBox))
                .add(38, 38, 38)
                .add(buttonsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void close_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_close_buttonActionPerformed
        getAliquotOptions().putAll(getAliquotOptionsSaved());
        close();
    }//GEN-LAST:event_close_buttonActionPerformed

    private void save_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_save_buttonActionPerformed
        SaveSettings();
        close();
    }//GEN-LAST:event_save_buttonActionPerformed

    private void restoreDefaults_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_restoreDefaults_buttonActionPerformed
        setAliquotOptions(new SampleDateInterpretationGUIOptions().getAliquotOptionsMapByName(getAliquotName(), 0));
        CustomInit();
}//GEN-LAST:event_restoreDefaults_buttonActionPerformed

private void revert_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_revert_buttonActionPerformed
    getAliquotOptions().putAll(getAliquotOptionsSaved());
    CustomInit();
}//GEN-LAST:event_revert_buttonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel alquotName_label;
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JButton close_button;
    private javax.swing.JSpinner dateBoxFontSize_spinner;
    private javax.swing.JComboBox<String> dateBoxFonts_comboBox;
    private javax.swing.JCheckBox dateBoxShow_checkBox;
    private javax.swing.JCheckBox dateShowDate_chkBox;
    private javax.swing.JCheckBox dateShowMSWD_chkBox;
    private javax.swing.JCheckBox dateShowN_chkBox;
    private javax.swing.JLabel excludedBorderColor_label;
    private javax.swing.JSpinner excludedBorderWeight_spinner;
    private javax.swing.JLabel excludedCenterColor_label;
    private javax.swing.JSpinner excludedCenterSize_spinner;
    private javax.swing.JLabel excludedFillColor_label;
    private javax.swing.JSpinner excludedFillTransparencyPCT_spinner;
    private javax.swing.JLabel includedBorderColor_label;
    private javax.swing.JSpinner includedBorderWeight_spinner;
    private javax.swing.JLabel includedCenterColor_label;
    private javax.swing.JSpinner includedCenterSize_spinner;
    private javax.swing.JLabel includedFillColor_label;
    private javax.swing.JSpinner includedFillTransparencyPCT_spinner;
    private javax.swing.ButtonGroup interceptErrorLineStyle_btnGroup;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JButton restoreDefaults_button;
    private javax.swing.JButton revert_button;
    private javax.swing.JButton save_button;
    // End of variables declaration//GEN-END:variables
    /**
     * 
     * @return
     */
    public Map<String, String> getAliquotOptions() {
        return aliquotOptions;
    }

    /**
     * 
     * @param aliquotOptions
     */
    public void setAliquotOptions(Map<String, String> aliquotOptions) {
        this.aliquotOptions = aliquotOptions;
    }

    /**
     * 
     * @return
     */
    public String getAliquotName() {
        return aliquotName;
    }

    /**
     * 
     * @return
     */
    public Map<String, String> getAliquotOptionsSaved() {
        return aliquotOptionsSaved;
    }

    /**
     * 
     * @param aliquotOptionsSaved
     */
    public void setAliquotOptionsSaved(Map<String, String> aliquotOptionsSaved) {
        this.aliquotOptionsSaved = aliquotOptionsSaved;
    }
}
