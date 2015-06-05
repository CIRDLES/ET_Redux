/*
 * ConcordiaOptionsDialog.java
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
import org.earthtime.UPb_Redux.dialogs.DialogEditor;
import org.earthtime.UPb_Redux.listeners.ColorChooserListener;
import org.earthtime.UPb_Redux.user.SampleDateInterpretationGUIOptions;

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

        setLocationRelativeTo(parent);
        setAlwaysOnTop(modal);

        initComponents();

        setConcordiaOptions(concordiaOptions);

        setConcordiaOptionsSaved(new HashMap<>());
        getConcordiaOptionsSaved().putAll(getConcordiaOptions());

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


        } catch (Exception e) {
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
        close_button = new javax.swing.JButton();
        restoreDefaults_button = new javax.swing.JButton();
        save_button = new javax.swing.JButton();
        revert_button = new javax.swing.JButton();
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
        titleFonts_comboBox = new javax.swing.JComboBox<String>();
        axesLabelFontSize_spinner = new javax.swing.JSpinner();
        axesLabelFonts_comboBox = new javax.swing.JComboBox<String>();
        axesTicLabelFontSize_spinner = new javax.swing.JSpinner();
        axesTicLabelFonts_comboBox = new javax.swing.JComboBox<String>();
        concordiaLabelFontSize_spinner = new javax.swing.JSpinner();
        concordiaLabelFonts_comboBox = new javax.swing.JComboBox<String>();
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
        ellipseFonts_comboBox = new javax.swing.JComboBox<String>();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Concordia Plot Display Settings");
        setBackground(new java.awt.Color(255, 255, 255));
        setForeground(new java.awt.Color(255, 255, 255));
        setResizable(false);

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
                .add(save_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(close_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(restoreDefaults_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(revert_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        jLabel19.setFont(new java.awt.Font("Lucida Grande", 1, 16)); // NOI18N
        jLabel19.setText("Title box:");

        jLabel15.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel15.setText("choose tic label font:");

        jLabel3.setFont(new java.awt.Font("Lucida Grande", 1, 16)); // NOI18N
        jLabel3.setText("Concordia:");

        jLabel2.setFont(new java.awt.Font("Lucida Grande", 1, 16)); // NOI18N
        jLabel2.setText("Ellipses:");

        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("choose label font:");

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel6.setText("label font size:");

        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("error size:");

        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("show errors as:");

        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel8.setText("line weight pixels; color:");

        jLabel9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel9.setText("line color:");

        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("tic shape:");

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel11.setText("tic weight:");

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel13.setText("choose label font:");

        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel12.setText("label font size:");

        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel16.setText("tic label font size:");

        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel17.setText("choose axes label font:");

        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel18.setText("axes label font size:");

        jLabel20.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel20.setText("choose title font:");

        jLabel21.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel21.setText("title font size:");

        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel22.setText("subtitle text:");

        concordiaTickShape_buttonGroup.add(concordiaTicAsLine_rButton);
        concordiaTicAsLine_rButton.setText("line");
        concordiaTicAsLine_rButton.setName("line"); // NOI18N

        concordiaTickShape_buttonGroup.add(concordiaTicAsSquare_rButton);
        concordiaTicAsSquare_rButton.setText("square");
        concordiaTicAsSquare_rButton.setName("square"); // NOI18N

        concordiaTickShape_buttonGroup.add(concordiaTicAsCircle_rButton);
        concordiaTicAsCircle_rButton.setText("circle");
        concordiaTicAsCircle_rButton.setName("circle"); // NOI18N

        concordiaErrorStyle_buttonGroup.add(errorStyleDottedConcordia_rButton);
        errorStyleDottedConcordia_rButton.setText("dotted line");
        errorStyleDottedConcordia_rButton.setName("dotted"); // NOI18N

        concordiaErrorStyle_buttonGroup.add(errorStyleShadedConcordia_rButton);
        errorStyleShadedConcordia_rButton.setText("shaded");
        errorStyleShadedConcordia_rButton.setName("shaded"); // NOI18N

        ellipseSize_buttonGroup.add(oneSigmaEllipse_rButton);
        oneSigmaEllipse_rButton.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        oneSigmaEllipse_rButton.setText("1sigma");
        oneSigmaEllipse_rButton.setName("1"); // NOI18N
        oneSigmaEllipse_rButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                oneSigmaEllipse_rButtonActionPerformed(evt);
            }
        });

        ellipseSize_buttonGroup.add(twoSigmaEllipse_rButton);
        twoSigmaEllipse_rButton.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        twoSigmaEllipse_rButton.setText("2sigma");
        twoSigmaEllipse_rButton.setName("2"); // NOI18N

        ellipseSize_buttonGroup.add(ninetyFivePCTEllipse_rButton);
        ninetyFivePCTEllipse_rButton.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        ninetyFivePCTEllipse_rButton.setText("95%");
        ninetyFivePCTEllipse_rButton.setName("2.4477"); // NOI18N

        concordiaLineColor_label.setBackground(new java.awt.Color(255, 255, 255));
        concordiaLineColor_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        concordiaLineColor_label.setText("click to change color");
        concordiaLineColor_label.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        concordiaLineColor_label.setName("concordiaLineColor"); // NOI18N
        concordiaLineColor_label.setOpaque(true);

        jLabel14.setFont(new java.awt.Font("Lucida Grande", 1, 16)); // NOI18N
        jLabel14.setText("Axes:");

        jLabel23.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel23.setText("draw box outline:");

        titleBoxSubtitle_text.setText("Subtitle");

        titleBoxShow_checkBox.setSelected(true);

        jLabel24.setText("line weight in pixels; color:");

        interceptLineColor_label.setBackground(new java.awt.Color(255, 255, 255));
        interceptLineColor_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        interceptLineColor_label.setText("click to change color");
        interceptLineColor_label.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        interceptLineColor_label.setName("interceptLineColor"); // NOI18N
        interceptLineColor_label.setOpaque(true);

        jLabel25.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel25.setText("show error lines as:");

        interceptErrorLineStyle_btnGroup.add(errorStyleDottedInterceptLine_rButton);
        errorStyleDottedInterceptLine_rButton.setSelected(true);
        errorStyleDottedInterceptLine_rButton.setText("dashed");
        errorStyleDottedInterceptLine_rButton.setName("dashed"); // NOI18N

        interceptErrorLineStyle_btnGroup.add(errorStyleSolidInterceptLine_rButton);
        errorStyleSolidInterceptLine_rButton.setText("shaded");
        errorStyleSolidInterceptLine_rButton.setName("shaded"); // NOI18N

        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel26.setText("truncate regression curves:");

        truncateRegressionCurves_chkBox.setText("below concordia");

        jLabel27.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel27.setText("choose label contents:");

        concordiaLabelContents_comboBox.setEnabled(false);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(10, 10, 10)
                .add(jLabel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)
                .addContainerGap(263, Short.MAX_VALUE))
            .add(buttonsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(84, 84, 84)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(layout.createSequentialGroup()
                        .add(47, 47, 47)
                        .add(jLabel27, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(jLabel24)
                            .add(jLabel26, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 205, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(jLabel25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 160, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                                .add(interceptLineWeight_spinner, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(interceptLineColor_label))
                            .add(truncateRegressionCurves_chkBox)
                            .add(layout.createSequentialGroup()
                                .add(errorStyleDottedInterceptLine_rButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                                .add(errorStyleSolidInterceptLine_rButton)))
                        .add(11, 11, 11))
                    .add(concordiaLabelContents_comboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 216, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
            .add(layout.createSequentialGroup()
                .add(10, 10, 10)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jLabel14)
                        .add(84, 84, 84)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel15, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 142, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel16)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel17, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 158, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel18, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 134, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(axesLabelFontSize_spinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 76, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(axesTicLabelFonts_comboBox, 0, 207, Short.MAX_VALUE)
                            .add(axesTicLabelFontSize_spinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 77, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(axesLabelFonts_comboBox, 0, 207, Short.MAX_VALUE)))
                    .add(layout.createSequentialGroup()
                        .add(jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE)
                        .add(18, 18, 18)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel5, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel6, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 109, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel8, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel9, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel10, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel11, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel13, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 173, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel12, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 102, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(concordiaLabelFonts_comboBox, 0, 207, Short.MAX_VALUE)
                            .add(layout.createSequentialGroup()
                                .add(concordiaTicAsLine_rButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(concordiaTicAsSquare_rButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(concordiaTicAsCircle_rButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 85, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .add(errorStyleDottedConcordia_rButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 106, Short.MAX_VALUE)
                                .add(24, 24, 24)
                                .add(errorStyleShadedConcordia_rButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .add(oneSigmaEllipse_rButton)
                                .add(7, 7, 7)
                                .add(twoSigmaEllipse_rButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(ninetyFivePCTEllipse_rButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 59, Short.MAX_VALUE))
                            .add(concordiaLineColor_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 207, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(ellipseFonts_comboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 202, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(concordiaLabelFontSize_spinner, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)
                                    .add(concordiaTicWeight_spinner, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)
                                    .add(concordiaLineWeight_spinner, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE)
                                    .add(ellipseLabelFontSize_spinner, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 97, Short.MAX_VALUE))
                                .add(117, 117, 117)))))
                .addContainerGap())
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(jLabel23, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 118, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(titleBoxShow_checkBox))
                    .add(jLabel19))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel20)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel22)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jLabel21))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(titleFontSize_spinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 79, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(titleFonts_comboBox, 0, 207, Short.MAX_VALUE)
                    .add(titleBoxSubtitle_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 207, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(22, 22, 22))
        );

        layout.linkSize(new java.awt.Component[] {axesLabelFonts_comboBox, axesTicLabelFonts_comboBox, concordiaLabelFonts_comboBox, concordiaLineColor_label, ellipseFonts_comboBox, titleBoxSubtitle_text, titleFonts_comboBox}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(errorStyleDottedConcordia_rButton)
                            .add(errorStyleShadedConcordia_rButton)
                            .add(jLabel7)
                            .add(jLabel3))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                            .add(jLabel8)
                            .add(concordiaLineWeight_spinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel9)
                            .add(concordiaLineColor_label))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel10)
                            .add(concordiaTicAsLine_rButton)
                            .add(concordiaTicAsSquare_rButton)
                            .add(concordiaTicAsCircle_rButton))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                            .add(jLabel11)
                            .add(concordiaTicWeight_spinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(8, 8, 8))
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(oneSigmaEllipse_rButton)
                                    .add(ninetyFivePCTEllipse_rButton)
                                    .add(twoSigmaEllipse_rButton))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(jLabel5)
                                    .add(ellipseFonts_comboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(jLabel6)
                                    .add(ellipseLabelFontSize_spinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                            .add(layout.createSequentialGroup()
                                .add(3, 3, 3)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                    .add(jLabel4)
                                    .add(jLabel2))))
                        .add(162, 162, 162)))
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel13)
                    .add(concordiaLabelFonts_comboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(jLabel12)
                    .add(concordiaLabelFontSize_spinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel27)
                    .add(concordiaLabelContents_comboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(jLabel24)
                    .add(interceptLineWeight_spinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(interceptLineColor_label))
                .add(8, 8, 8)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(errorStyleDottedInterceptLine_rButton)
                    .add(errorStyleSolidInterceptLine_rButton)
                    .add(jLabel25))
                .add(9, 9, 9)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(truncateRegressionCurves_chkBox)
                    .add(jLabel26))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel14)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel15)
                            .add(axesTicLabelFonts_comboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                            .add(jLabel16)
                            .add(axesTicLabelFontSize_spinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel17)
                            .add(axesLabelFonts_comboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                            .add(jLabel18)
                            .add(axesLabelFontSize_spinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(18, 18, 18)
                        .add(jLabel19)
                        .add(47, 47, 47)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                            .add(jLabel23)
                            .add(titleBoxShow_checkBox)))
                    .add(layout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                            .add(jLabel20)
                            .add(titleFonts_comboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                            .add(jLabel21)
                            .add(titleFontSize_spinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                            .add(jLabel22)
                            .add(titleBoxSubtitle_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 8, Short.MAX_VALUE)
                .add(buttonsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

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
