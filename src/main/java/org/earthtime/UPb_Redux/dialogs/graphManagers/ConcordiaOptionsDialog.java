/*
 * ConcordiaOptionsDialog.java
 *
 *
 * Copyright 2006-2017 James F. Bowring and www.Earth-Time.org
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
package org.earthtime.UPb_Redux.dialogs.graphManagers;

import java.awt.Color;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseListener;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JRadioButton;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import org.earthtime.UPb_Redux.listeners.ColorChooserListener;
import org.earthtime.UPb_Redux.user.SampleDateInterpretationGUIOptions;
import org.earthtime.beans.ET_JButton;
import org.earthtime.dialogs.DialogEditor;

/**
 *
 * @author  James F. Bowring
 */
public class ConcordiaOptionsDialog extends DialogEditor {

    // instance variables
    private Map<String, String> concordiaOptions;

    private Map<String, String> concordiaOptionsSaved;

    private String concordiaLineColor;

    /** Creates new form ConcordiaOptionsDialog
     * @param parent 
     * @param concordiaOptions
     * @param modal  
     */
    public ConcordiaOptionsDialog(java.awt.Frame parent,
            boolean modal,
            Map<String, String> concordiaOptions) {

        super(parent, modal);

        setSizeAndCenter(531, 717 + 25);
        setAlwaysOnTop(modal);

        initComponents();

        this.concordiaOptions = concordiaOptions;

        concordiaOptionsSaved = new HashMap<>();
        concordiaOptionsSaved.putAll(concordiaOptions);

        MouseListener myColorChooserListener =
                new ColorChooserListener(concordiaOptions);
        concordiaLineColor_label.addMouseListener(myColorChooserListener);
        interceptLineColor_label.addMouseListener(myColorChooserListener);
        
        CustomInit();

    }

    private void CustomInit() {
        // new fields will cause default options first time use = development strategy
        try {

            // Ellipses
            // april 2016 use crosses option
            boolean useUncertaintyCrosses = Boolean.valueOf(RetrieveOptionField("useUncertaintyCrosses"));
            useUncertaintyCrosses_checkbox.setSelected(useUncertaintyCrosses);
            
            // Ellipse size
            oneSigmaEllipse_rButton.setText("1\u03C3");
            twoSigmaEllipse_rButton.setText("2\u03C3");
            ninetyFivePCTEllipse_rButton.setText("95%");

            String ellipseSize = RetrieveOptionField("ellipseSize");

            for (Enumeration e = ellipseSize_buttonGroup.getElements(); e.hasMoreElements();) {
                JRadioButton jrb = (JRadioButton) e.nextElement();
                if (jrb.getName().equalsIgnoreCase(ellipseSize)) {
                    jrb.setSelected(true);
                }
            }

            // Ellipse label font
            String listOfFonts[] =
                    GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
            ellipseFonts_comboBox.removeAllItems();
            for (int i = 0; i < listOfFonts.length; i++) {
                ellipseFonts_comboBox.addItem(listOfFonts[i].trim());
            }
            String ellipseLabelFont = RetrieveOptionField("ellipseLabelFont");

            ellipseFonts_comboBox.setSelectedItem(ellipseLabelFont.trim());

            // Ellipse label font size
            String ellipseLabelFontSize = RetrieveOptionField("ellipseLabelFontSize");

            SpinnerModel ellipseLabelFontSize_spinnerModel =
                    new SpinnerNumberModel(//
                    (int) Integer.parseInt(ellipseLabelFontSize), 5, 25, 1);
            ellipseLabelFontSize_spinner.setModel(ellipseLabelFontSize_spinnerModel);

            // CONCORDIA *******************************************************
            // Concordia error style
            String concordiaErrorStyle = RetrieveOptionField("concordiaErrorStyle");
            for (Enumeration e = concordiaErrorStyle_buttonGroup.getElements(); e.hasMoreElements();) {
                JRadioButton jrb = (JRadioButton) e.nextElement();
                if (jrb.getName().equalsIgnoreCase(concordiaErrorStyle)) {
                    jrb.setSelected(true);
                }
            }

            // Concordia line weight
            String concordiaLineWeight = RetrieveOptionField("concordiaLineWeight");
            SpinnerModel concordiaLineWeight_spinnerModel =
                    new SpinnerNumberModel(//
                    (Number) Float.valueOf(concordiaLineWeight), 0.5f, 9.5f, 0.5f);
            concordiaLineWeight_spinner.setModel(concordiaLineWeight_spinnerModel);

            // concordia line color
            Color colorRGB = RetrieveOptionColorField("concordiaLineColor");
            concordiaLineColor_label.setBackground(colorRGB);
            concordiaLineColor_label.setForeground(InvertColor(colorRGB));

            String concordiaTicShape = RetrieveOptionField("concordiaTicShape");
            for (Enumeration e = concordiaTickShape_buttonGroup.getElements(); e.hasMoreElements();) {
                JRadioButton jrb = (JRadioButton) e.nextElement();
                if (jrb.getName().equalsIgnoreCase(concordiaTicShape)) {
                    jrb.setSelected(true);
                }
            }

            // Concordia tic weight
            String concordiaTicWeight = RetrieveOptionField("concordiaTicWeight");
            SpinnerModel concordiaTicWeight_spinnerModel =
                    new SpinnerNumberModel(//
                    (Number) Float.valueOf(concordiaTicWeight), 0.5f, 9.5f, 0.5f);
            concordiaTicWeight_spinner.setModel(concordiaTicWeight_spinnerModel);



            // Concordia label font
            concordiaLabelFonts_comboBox.removeAllItems();
            for (int i = 0; i < listOfFonts.length; i++) {
                concordiaLabelFonts_comboBox.addItem(listOfFonts[i].trim());
            }
            String concordiaLabelFont = RetrieveOptionField("concordiaLabelFont");
            concordiaLabelFonts_comboBox.setSelectedItem(concordiaLabelFont.trim());

            // Concordia label font size
            String concordiaLabelFontSize = RetrieveOptionField("concordiaLabelFontSize");
            SpinnerModel concordiaLabelFontSize_spinnerModel =
                    new SpinnerNumberModel(//
                    (int) Integer.valueOf(concordiaLabelFontSize), 5, 25, 1);
            concordiaLabelFontSize_spinner.setModel(concordiaLabelFontSize_spinnerModel);


            // Intercept line
            String interceptLineWeight = RetrieveOptionField("interceptLineWeight");
            SpinnerModel interceptLineWeight_spinnerModel =
                    new SpinnerNumberModel(//
                    (Number) Float.valueOf(interceptLineWeight), 0.0f, 10.0f, 0.5f);
            interceptLineWeight_spinner.setModel(interceptLineWeight_spinnerModel);

            // Intercept line color
            colorRGB = RetrieveOptionColorField("interceptLineColor");
            interceptLineColor_label.setBackground(colorRGB);
            interceptLineColor_label.setForeground(InvertColor(colorRGB));

            String interceptLineErrorStyle = RetrieveOptionField("interceptErrorLineStyle");
            for (Enumeration e = interceptErrorLineStyle_btnGroup.getElements(); e.hasMoreElements();) {
                JRadioButton jrb = (JRadioButton) e.nextElement();
                if (jrb.getName().equalsIgnoreCase(interceptLineErrorStyle)) {
                    jrb.setSelected(true);
                }
            }

            boolean truncateRegressionCurves = Boolean.valueOf(RetrieveOptionField("truncateRegressionCurves"));
            truncateRegressionCurves_chkBox.setSelected(truncateRegressionCurves);


            // Axes tic label font
            axesTicLabelFonts_comboBox.removeAllItems();
            for (int i = 0; i < listOfFonts.length; i++) {
                axesTicLabelFonts_comboBox.addItem(listOfFonts[i].trim());
            }
            String axesTicLabelFont = RetrieveOptionField("axesTicLabelFont");
            axesTicLabelFonts_comboBox.setSelectedItem(axesTicLabelFont.trim());

            // Axes tic label font size
            String axesTicLabelFontSize = RetrieveOptionField("axesTicLabelFontSize");

            SpinnerModel axesTicLabelFontSize_spinnerModel =
                    new SpinnerNumberModel(//
                    (int) Integer.valueOf(axesTicLabelFontSize), 10, 16, 1);
            axesTicLabelFontSize_spinner.setModel(axesTicLabelFontSize_spinnerModel);

            // Axes label font
            axesLabelFonts_comboBox.removeAllItems();
            for (int i = 0; i < listOfFonts.length; i++) {
                axesLabelFonts_comboBox.addItem(listOfFonts[i].trim());
            }
            String axesLabelFont = RetrieveOptionField("axesLabelFont");
            axesLabelFonts_comboBox.setSelectedItem(axesLabelFont.trim());

            // Axes label font size
            String axesLabelFontSize = RetrieveOptionField("axesLabelFontSize");
            SpinnerModel axesLabelFontSize_spinnerModel =
                    new SpinnerNumberModel(//
                    (int) Integer.valueOf(axesLabelFontSize), 12, 30, 1);
            axesLabelFontSize_spinner.setModel(axesLabelFontSize_spinnerModel);

            // Title font
            titleFonts_comboBox.removeAllItems();
            for (int i = 0; i < listOfFonts.length; i++) {
                titleFonts_comboBox.addItem(listOfFonts[i].trim());
            }
            String titleFont = RetrieveOptionField("titleFont");
            titleFonts_comboBox.setSelectedItem(titleFont.trim());

            // Title font size
            String titleFontSize = RetrieveOptionField("titleFontSize");

            SpinnerModel titleFontSize_spinnerModel =
                    new SpinnerNumberModel(//
                    (int) Integer.valueOf(titleFontSize), 5, 30, 1);
            titleFontSize_spinner.setModel(titleFontSize_spinnerModel);

            // Subtitle text
            String subtitleText = RetrieveOptionField("subTitleText");
            titleBoxSubtitle_text.setText(subtitleText);

            boolean titleBoxShow = Boolean.valueOf(RetrieveOptionField("titleBoxShow"));
            titleBoxShow_checkBox.setSelected(titleBoxShow);


        } catch (NumberFormatException e) {
            // during development, handles problems
            setConcordiaOptions(new SampleDateInterpretationGUIOptions().getConcordiaOptions());
        }
    }

    private String RetrieveOptionField(String optionFieldName) {
        String optionValue = null;
        optionValue = getConcordiaOptions().get(optionFieldName);
        if (optionValue == null) {
            setConcordiaOptions(new SampleDateInterpretationGUIOptions().getConcordiaOptions());
            optionValue = getConcordiaOptions().get(optionFieldName);
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

    private void SaveSettings() {
        for (Enumeration e = ellipseSize_buttonGroup.getElements(); e.hasMoreElements();) {
            JRadioButton jrb = (JRadioButton) e.nextElement();
            if (jrb.isSelected()) {
                getConcordiaOptions().put("ellipseSize", jrb.getName());
            }
        }

        getConcordiaOptions()//
                .put("useUncertaintyCrosses", Boolean.toString(useUncertaintyCrosses_checkbox.isSelected()));

        getConcordiaOptions().put("ellipseLabelFont", (String) ellipseFonts_comboBox.getSelectedItem());

        getConcordiaOptions().put("ellipseLabelFontSize", //
                Integer.toString((Integer) ellipseLabelFontSize_spinner.getValue()));

        for (Enumeration e = concordiaErrorStyle_buttonGroup.getElements(); e.hasMoreElements();) {
            JRadioButton jrb = (JRadioButton) e.nextElement();
            if (jrb.isSelected()) {
                getConcordiaOptions().put("concordiaErrorStyle", jrb.getName());
            }
        }

        getConcordiaOptions().put("concordiaLineWeight", //
                Float.toString((Float) concordiaLineWeight_spinner.getValue()));

//        getConcordiaOptions().put("concordiaLineColor", getConcordiaLineColor());

        for (Enumeration e = concordiaTickShape_buttonGroup.getElements(); e.hasMoreElements();) {
            JRadioButton jrb = (JRadioButton) e.nextElement();
            if (jrb.isSelected()) {
                getConcordiaOptions().put("concordiaTicShape", jrb.getName());
            }
        }

        getConcordiaOptions().put("concordiaTicWeight", //
                Float.toString((Float) concordiaTicWeight_spinner.getValue()));

        getConcordiaOptions().put("concordiaLabelFont", //
                (String) concordiaLabelFonts_comboBox.getSelectedItem());

        getConcordiaOptions().put("concordiaLabelFontSize", //
                Integer.toString((Integer) concordiaLabelFontSize_spinner.getValue()));

        getConcordiaOptions().put("interceptLineWeight", //
                Float.toString((Float) interceptLineWeight_spinner.getValue()));

        for (Enumeration e = interceptErrorLineStyle_btnGroup.getElements(); e.hasMoreElements();) {
            JRadioButton jrb = (JRadioButton) e.nextElement();
            if (jrb.isSelected()) {
                getConcordiaOptions().put("interceptErrorLineStyle", jrb.getName());
            }
        }

        getConcordiaOptions().put("truncateRegressionCurves", //
                Boolean.toString(truncateRegressionCurves_chkBox.isSelected()));



        // Axes
        getConcordiaOptions().put("axesTicLabelFont", //
                (String) axesTicLabelFonts_comboBox.getSelectedItem());

        getConcordiaOptions().put("axesTicLabelFontSize", //
                Integer.toString((Integer) axesTicLabelFontSize_spinner.getValue()));

        getConcordiaOptions().put("axesLabelFont", //
                (String) axesLabelFonts_comboBox.getSelectedItem());

        getConcordiaOptions().put("axesLabelFontSize", //
                Integer.toString((Integer) axesLabelFontSize_spinner.getValue()));

        // Title and Subtitle
        getConcordiaOptions().put("titleFont", //
                (String) titleFonts_comboBox.getSelectedItem());

        getConcordiaOptions().put("titleFontSize", //
                Integer.toString((Integer) titleFontSize_spinner.getValue()));

        getConcordiaOptions().put("subTitleText", //
                titleBoxSubtitle_text.getText());

        getConcordiaOptions().put("titleBoxShow", //
                Boolean.toString(titleBoxShow_checkBox.isSelected()));

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        ellipseSize_buttonGroup = new javax.swing.ButtonGroup();
        concordiaErrorStyle_buttonGroup = new javax.swing.ButtonGroup();
        concordiaTickShape_buttonGroup = new javax.swing.ButtonGroup();
        interceptErrorLineStyle_btnGroup = new javax.swing.ButtonGroup();
        buttonsPanel = new javax.swing.JPanel();
        close_button = new ET_JButton();
        restoreDefaults_button = new ET_JButton();
        save_button = new ET_JButton();
        revert_button = new ET_JButton();
        jLabel19 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        titleFontSize_spinner = new javax.swing.JSpinner();
        titleFonts_comboBox = new javax.swing.JComboBox<>();
        axesLabelFontSize_spinner = new javax.swing.JSpinner();
        axesLabelFonts_comboBox = new javax.swing.JComboBox<>();
        axesTicLabelFontSize_spinner = new javax.swing.JSpinner();
        axesTicLabelFonts_comboBox = new javax.swing.JComboBox<>();
        concordiaLabelFontSize_spinner = new javax.swing.JSpinner();
        concordiaLabelFonts_comboBox = new javax.swing.JComboBox<>();
        concordiaTicWeight_spinner = new javax.swing.JSpinner();
        concordiaTicAsLine_rButton = new javax.swing.JRadioButton();
        concordiaTicAsSquare_rButton = new javax.swing.JRadioButton();
        concordiaTicAsCircle_rButton = new javax.swing.JRadioButton();
        concordiaLineWeight_spinner = new javax.swing.JSpinner();
        errorStyleDottedConcordia_rButton = new javax.swing.JRadioButton();
        errorStyleShadedConcordia_rButton = new javax.swing.JRadioButton();
        ellipseLabelFontSize_spinner = new javax.swing.JSpinner();
        oneSigmaEllipse_rButton = new javax.swing.JRadioButton();
        twoSigmaEllipse_rButton = new javax.swing.JRadioButton();
        ninetyFivePCTEllipse_rButton = new javax.swing.JRadioButton();
        ellipseFonts_comboBox = new javax.swing.JComboBox<>();
        concordiaLineColor_label = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        titleBoxSubtitle_text = new javax.swing.JTextField();
        titleBoxShow_checkBox = new javax.swing.JCheckBox();
        jLabel24 = new javax.swing.JLabel();
        interceptLineWeight_spinner = new javax.swing.JSpinner();
        interceptLineColor_label = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        errorStyleDottedInterceptLine_rButton = new javax.swing.JRadioButton();
        errorStyleSolidInterceptLine_rButton = new javax.swing.JRadioButton();
        jLabel26 = new javax.swing.JLabel();
        truncateRegressionCurves_chkBox = new javax.swing.JCheckBox();
        jLabel27 = new javax.swing.JLabel();
        concordiaLabelContents_comboBox = new javax.swing.JComboBox();
        useUncertaintyCrosses_checkbox = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Concordia Plot Display Settings");
        setBackground(new java.awt.Color(255, 255, 255));
        setForeground(new java.awt.Color(255, 255, 255));
        setResizable(false);
        getContentPane().setLayout(null);

        buttonsPanel.setBackground(new java.awt.Color(252, 236, 235));
        buttonsPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

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

        save_button.setForeground(new java.awt.Color(255, 51, 0));
        save_button.setText("OK");
        save_button.setMargin(new java.awt.Insets(0, 1, 0, 1));
        save_button.setPreferredSize(new java.awt.Dimension(140, 23));
        save_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                save_buttonActionPerformed(evt);
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
            .add(org.jdesktop.layout.GroupLayout.TRAILING, buttonsPanelLayout.createSequentialGroup()
                .add(save_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 91, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(revert_button, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(restoreDefaults_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 160, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(close_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 109, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        buttonsPanelLayout.setVerticalGroup(
            buttonsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(buttonsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(save_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(close_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(restoreDefaults_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(revert_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        getContentPane().add(buttonsPanel);
        buttonsPanel.setBounds(0, 689, 532, 29);

        jLabel19.setFont(new java.awt.Font("Lucida Grande", 1, 16)); // NOI18N
        jLabel19.setText("Title box:");
        getContentPane().add(jLabel19);
        jLabel19.setBounds(6, 591, 75, 20);

        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel15.setText("choose tic label font:");
        getContentPane().add(jLabel15);
        jLabel15.setBounds(153, 453, 142, 16);

        jLabel3.setFont(new java.awt.Font("Lucida Grande", 1, 16)); // NOI18N
        jLabel3.setText("Concordia:");
        getContentPane().add(jLabel3);
        jLabel3.setBounds(10, 103, 259, 20);

        jLabel2.setFont(new java.awt.Font("Lucida Grande", 1, 16)); // NOI18N
        jLabel2.setText("Ellipses:");
        getContentPane().add(jLabel2);
        jLabel2.setBounds(10, 3, 109, 20);

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("choose label font:");
        getContentPane().add(jLabel5);
        jLabel5.setBounds(137, 33, 164, 16);

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("label font size:");
        getContentPane().add(jLabel6);
        jLabel6.setBounds(137, 67, 164, 16);

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("error size:");
        getContentPane().add(jLabel4);
        jLabel4.setBounds(137, 6, 164, 16);

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("show errors as:");
        getContentPane().add(jLabel7);
        jLabel7.setBounds(192, 106, 109, 16);

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("line weight pixels; color:");
        getContentPane().add(jLabel8);
        jLabel8.setBounds(137, 136, 164, 16);

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("line color:");
        getContentPane().add(jLabel9);
        jLabel9.setBounds(137, 164, 164, 16);

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("tic shape:");
        getContentPane().add(jLabel10);
        jLabel10.setBounds(137, 191, 164, 16);

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("tic weight:");
        getContentPane().add(jLabel11);
        jLabel11.setBounds(137, 221, 164, 16);

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel13.setText("choose label font:");
        getContentPane().add(jLabel13);
        jLabel13.setBounds(137, 254, 164, 16);

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setText("label font size:");
        getContentPane().add(jLabel12);
        jLabel12.setBounds(199, 288, 102, 16);

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel16.setText("tic label font size:");
        getContentPane().add(jLabel16);
        jLabel16.setBounds(182, 487, 113, 16);

        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel17.setText("choose axes label font:");
        getContentPane().add(jLabel17);
        jLabel17.setBounds(137, 518, 158, 16);

        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel18.setText("axes label font size:");
        getContentPane().add(jLabel18);
        jLabel18.setBounds(161, 552, 134, 16);

        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel20.setText("choose title font:");
        getContentPane().add(jLabel20);
        jLabel20.setBounds(174, 587, 108, 16);

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel21.setText("title font size:");
        getContentPane().add(jLabel21);
        jLabel21.setBounds(194, 621, 88, 16);

        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel22.setText("subtitle text:");
        getContentPane().add(jLabel22);
        jLabel22.setBounds(201, 653, 81, 16);
        getContentPane().add(titleFontSize_spinner);
        titleFontSize_spinner.setBounds(288, 616, 79, 26);
        getContentPane().add(titleFonts_comboBox);
        titleFonts_comboBox.setBounds(288, 583, 207, 27);
        getContentPane().add(axesLabelFontSize_spinner);
        axesLabelFontSize_spinner.setBounds(301, 547, 76, 26);
        getContentPane().add(axesLabelFonts_comboBox);
        axesLabelFonts_comboBox.setBounds(301, 514, 207, 27);
        getContentPane().add(axesTicLabelFontSize_spinner);
        axesTicLabelFontSize_spinner.setBounds(301, 482, 77, 26);
        getContentPane().add(axesTicLabelFonts_comboBox);
        axesTicLabelFonts_comboBox.setBounds(301, 449, 207, 27);
        getContentPane().add(concordiaLabelFontSize_spinner);
        concordiaLabelFontSize_spinner.setBounds(307, 283, 102, 26);
        getContentPane().add(concordiaLabelFonts_comboBox);
        concordiaLabelFonts_comboBox.setBounds(307, 250, 207, 27);
        getContentPane().add(concordiaTicWeight_spinner);
        concordiaTicWeight_spinner.setBounds(307, 216, 102, 26);

        concordiaTickShape_buttonGroup.add(concordiaTicAsLine_rButton);
        concordiaTicAsLine_rButton.setText("line");
        concordiaTicAsLine_rButton.setName("line"); // NOI18N
        getContentPane().add(concordiaTicAsLine_rButton);
        concordiaTicAsLine_rButton.setBounds(307, 187, 55, 23);

        concordiaTickShape_buttonGroup.add(concordiaTicAsSquare_rButton);
        concordiaTicAsSquare_rButton.setText("square");
        concordiaTicAsSquare_rButton.setName("square"); // NOI18N
        getContentPane().add(concordiaTicAsSquare_rButton);
        concordiaTicAsSquare_rButton.setBounds(368, 187, 74, 23);

        concordiaTickShape_buttonGroup.add(concordiaTicAsCircle_rButton);
        concordiaTicAsCircle_rButton.setText("circle");
        concordiaTicAsCircle_rButton.setName("circle"); // NOI18N
        getContentPane().add(concordiaTicAsCircle_rButton);
        concordiaTicAsCircle_rButton.setBounds(448, 187, 78, 23);
        getContentPane().add(concordiaLineWeight_spinner);
        concordiaLineWeight_spinner.setBounds(307, 131, 102, 26);

        concordiaErrorStyle_buttonGroup.add(errorStyleDottedConcordia_rButton);
        errorStyleDottedConcordia_rButton.setText("dotted line");
        errorStyleDottedConcordia_rButton.setName("dotted"); // NOI18N
        getContentPane().add(errorStyleDottedConcordia_rButton);
        errorStyleDottedConcordia_rButton.setBounds(307, 102, 109, 23);

        concordiaErrorStyle_buttonGroup.add(errorStyleShadedConcordia_rButton);
        errorStyleShadedConcordia_rButton.setText("shaded");
        errorStyleShadedConcordia_rButton.setName("shaded"); // NOI18N
        getContentPane().add(errorStyleShadedConcordia_rButton);
        errorStyleShadedConcordia_rButton.setBounds(440, 102, 86, 23);
        getContentPane().add(ellipseLabelFontSize_spinner);
        ellipseLabelFontSize_spinner.setBounds(307, 62, 102, 26);

        ellipseSize_buttonGroup.add(oneSigmaEllipse_rButton);
        oneSigmaEllipse_rButton.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        oneSigmaEllipse_rButton.setText("1sigma");
        oneSigmaEllipse_rButton.setName("1"); // NOI18N
        oneSigmaEllipse_rButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                oneSigmaEllipse_rButtonActionPerformed(evt);
            }
        });
        getContentPane().add(oneSigmaEllipse_rButton);
        oneSigmaEllipse_rButton.setBounds(307, 0, 74, 23);

        ellipseSize_buttonGroup.add(twoSigmaEllipse_rButton);
        twoSigmaEllipse_rButton.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        twoSigmaEllipse_rButton.setText("2sigma");
        twoSigmaEllipse_rButton.setName("2"); // NOI18N
        getContentPane().add(twoSigmaEllipse_rButton);
        twoSigmaEllipse_rButton.setBounds(388, 0, 74, 23);

        ellipseSize_buttonGroup.add(ninetyFivePCTEllipse_rButton);
        ninetyFivePCTEllipse_rButton.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        ninetyFivePCTEllipse_rButton.setText("95%");
        ninetyFivePCTEllipse_rButton.setName("2.4477"); // NOI18N
        getContentPane().add(ninetyFivePCTEllipse_rButton);
        ninetyFivePCTEllipse_rButton.setBounds(468, 0, 58, 23);
        getContentPane().add(ellipseFonts_comboBox);
        ellipseFonts_comboBox.setBounds(307, 29, 207, 27);

        concordiaLineColor_label.setBackground(new java.awt.Color(255, 255, 255));
        concordiaLineColor_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        concordiaLineColor_label.setText("click to change color");
        concordiaLineColor_label.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        concordiaLineColor_label.setName("concordiaLineColor"); // NOI18N
        concordiaLineColor_label.setOpaque(true);
        getContentPane().add(concordiaLineColor_label);
        concordiaLineColor_label.setBounds(307, 163, 207, 18);

        jLabel14.setFont(new java.awt.Font("Lucida Grande", 1, 16)); // NOI18N
        jLabel14.setText("Axes:");
        getContentPane().add(jLabel14);
        jLabel14.setBounds(10, 449, 43, 20);

        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel23.setText("draw box outline:");
        getContentPane().add(jLabel23);
        jLabel23.setBounds(16, 661, 118, 16);

        titleBoxSubtitle_text.setText("Subtitle");
        getContentPane().add(titleBoxSubtitle_text);
        titleBoxSubtitle_text.setBounds(288, 648, 207, 26);

        titleBoxShow_checkBox.setSelected(true);
        getContentPane().add(titleBoxShow_checkBox);
        titleBoxShow_checkBox.setBounds(140, 658, 28, 23);

        jLabel24.setText("line weight in pixels; color:");
        getContentPane().add(jLabel24);
        jLabel24.setBounds(132, 353, 171, 16);
        getContentPane().add(interceptLineWeight_spinner);
        interceptLineWeight_spinner.setBounds(309, 348, 72, 26);

        interceptLineColor_label.setBackground(new java.awt.Color(255, 255, 255));
        interceptLineColor_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        interceptLineColor_label.setText("click to change color");
        interceptLineColor_label.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        interceptLineColor_label.setName("interceptLineColor"); // NOI18N
        interceptLineColor_label.setOpaque(true);
        getContentPane().add(interceptLineColor_label);
        interceptLineColor_label.setBounds(387, 352, 134, 18);

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel25.setText("show error lines as:");
        getContentPane().add(jLabel25);
        jLabel25.setBounds(143, 386, 160, 16);

        interceptErrorLineStyle_btnGroup.add(errorStyleDottedInterceptLine_rButton);
        errorStyleDottedInterceptLine_rButton.setSelected(true);
        errorStyleDottedInterceptLine_rButton.setText("dashed");
        errorStyleDottedInterceptLine_rButton.setName("dashed"); // NOI18N
        getContentPane().add(errorStyleDottedInterceptLine_rButton);
        errorStyleDottedInterceptLine_rButton.setBounds(309, 382, 77, 23);

        interceptErrorLineStyle_btnGroup.add(errorStyleSolidInterceptLine_rButton);
        errorStyleSolidInterceptLine_rButton.setText("shaded");
        errorStyleSolidInterceptLine_rButton.setName("shaded"); // NOI18N
        getContentPane().add(errorStyleSolidInterceptLine_rButton);
        errorStyleSolidInterceptLine_rButton.setBounds(398, 382, 77, 23);

        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel26.setText("truncate regression curves:");
        getContentPane().add(jLabel26);
        jLabel26.setBounds(98, 418, 205, 16);

        truncateRegressionCurves_chkBox.setText("below concordia");
        getContentPane().add(truncateRegressionCurves_chkBox);
        truncateRegressionCurves_chkBox.setBounds(309, 414, 135, 23);

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel27.setText("choose label contents:");
        getContentPane().add(jLabel27);
        jLabel27.setBounds(131, 319, 172, 16);

        concordiaLabelContents_comboBox.setEnabled(false);
        getContentPane().add(concordiaLabelContents_comboBox);
        concordiaLabelContents_comboBox.setBounds(309, 315, 216, 27);

        useUncertaintyCrosses_checkbox.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        useUncertaintyCrosses_checkbox.setText("use Crosses");
        getContentPane().add(useUncertaintyCrosses_checkbox);
        useUncertaintyCrosses_checkbox.setBounds(7, 29, 112, 23);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void close_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_close_buttonActionPerformed
        getConcordiaOptions().putAll(getConcordiaOptionsSaved());
        close();
    }//GEN-LAST:event_close_buttonActionPerformed

    private void restoreDefaults_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_restoreDefaults_buttonActionPerformed
        setConcordiaOptions(new SampleDateInterpretationGUIOptions().getConcordiaOptions());
        CustomInit();
    }//GEN-LAST:event_restoreDefaults_buttonActionPerformed

    private void save_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_save_buttonActionPerformed
        SaveSettings();
        close();
    }//GEN-LAST:event_save_buttonActionPerformed

    private void oneSigmaEllipse_rButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_oneSigmaEllipse_rButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_oneSigmaEllipse_rButtonActionPerformed

private void revert_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_revert_buttonActionPerformed
    getConcordiaOptions().putAll(getConcordiaOptionsSaved());
    CustomInit();
}//GEN-LAST:event_revert_buttonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSpinner axesLabelFontSize_spinner;
    private javax.swing.JComboBox<String> axesLabelFonts_comboBox;
    private javax.swing.JSpinner axesTicLabelFontSize_spinner;
    private javax.swing.JComboBox<String> axesTicLabelFonts_comboBox;
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JButton close_button;
    private javax.swing.ButtonGroup concordiaErrorStyle_buttonGroup;
    private javax.swing.JComboBox concordiaLabelContents_comboBox;
    private javax.swing.JSpinner concordiaLabelFontSize_spinner;
    private javax.swing.JComboBox<String> concordiaLabelFonts_comboBox;
    private javax.swing.JLabel concordiaLineColor_label;
    private javax.swing.JSpinner concordiaLineWeight_spinner;
    private javax.swing.JRadioButton concordiaTicAsCircle_rButton;
    private javax.swing.JRadioButton concordiaTicAsLine_rButton;
    private javax.swing.JRadioButton concordiaTicAsSquare_rButton;
    private javax.swing.JSpinner concordiaTicWeight_spinner;
    private javax.swing.ButtonGroup concordiaTickShape_buttonGroup;
    private javax.swing.JComboBox<String> ellipseFonts_comboBox;
    private javax.swing.JSpinner ellipseLabelFontSize_spinner;
    private javax.swing.ButtonGroup ellipseSize_buttonGroup;
    private javax.swing.JRadioButton errorStyleDottedConcordia_rButton;
    private javax.swing.JRadioButton errorStyleDottedInterceptLine_rButton;
    private javax.swing.JRadioButton errorStyleShadedConcordia_rButton;
    private javax.swing.JRadioButton errorStyleSolidInterceptLine_rButton;
    private javax.swing.ButtonGroup interceptErrorLineStyle_btnGroup;
    private javax.swing.JLabel interceptLineColor_label;
    private javax.swing.JSpinner interceptLineWeight_spinner;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JRadioButton ninetyFivePCTEllipse_rButton;
    private javax.swing.JRadioButton oneSigmaEllipse_rButton;
    private javax.swing.JButton restoreDefaults_button;
    private javax.swing.JButton revert_button;
    private javax.swing.JButton save_button;
    private javax.swing.JCheckBox titleBoxShow_checkBox;
    private javax.swing.JTextField titleBoxSubtitle_text;
    private javax.swing.JSpinner titleFontSize_spinner;
    private javax.swing.JComboBox<String> titleFonts_comboBox;
    private javax.swing.JCheckBox truncateRegressionCurves_chkBox;
    private javax.swing.JRadioButton twoSigmaEllipse_rButton;
    private javax.swing.JCheckBox useUncertaintyCrosses_checkbox;
    // End of variables declaration//GEN-END:variables

    /**
     * 
     * @return
     */
    public Map<String, String> getConcordiaOptions() {
        return concordiaOptions;
    }

    /**
     * 
     * @param concordiaOptions
     */
    public void setConcordiaOptions(Map<String, String> concordiaOptions) {
        this.concordiaOptions = concordiaOptions;
    }

    /**
     * 
     * @return
     */
    public String getConcordiaLineColor() {
        return concordiaLineColor;
    }

    /**
     * 
     * @param concordiaLineColor
     */
    public void setConcordiaLineColor(String concordiaLineColor) {
        this.concordiaLineColor = concordiaLineColor;
    }

    /**
     * 
     * @return
     */
    public Map<String, String> getConcordiaOptionsSaved() {
        return concordiaOptionsSaved;
    }

    /**
     * 
     * @param concordiaOptionsSaved
     */
    public void setConcordiaOptionsSaved(Map<String, String> concordiaOptionsSaved) {
        this.concordiaOptionsSaved = concordiaOptionsSaved;
    }
}
