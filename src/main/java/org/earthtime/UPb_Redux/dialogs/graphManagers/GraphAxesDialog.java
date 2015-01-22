/*
 * GraphAxesDialog.java
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

import java.awt.Dimension;
import java.awt.Toolkit;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Enumeration;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.ConcordiaGraphPanel;
import org.earthtime.UPb_Redux.dateInterpretation.graphPersistence.GraphAxesSetup;
import org.earthtime.UPb_Redux.dialogs.DialogEditor.DoubleDocument;
import org.earthtime.UPb_Redux.dialogs.DialogEditor.UnDoAbleDocument;

/**
 *
 * @author James F. Bowring
 */
public class GraphAxesDialog extends javax.swing.JDialog {

    // instance variables
    private GraphAxesSetup graphAxesSetup;
    private int xDecimalPlaceCount;
    private int xTicIncrement;
    private int xTicLabelFrequency;
    private double xMin;
    private double xMax;
    private double xMin_T;
    private double xMax_T;
    private int yDecimalPlaceCount;
    private int yTicIncrement;
    private int yTicLabelFrequency;
    private double yMin;
    private double yMax;
    private double yMin_T;
    private double yMax_T;
    // concordia axis
    private int cDecimalPlaceCount;
    private int cTicIncrement;
    private int cTicLabelFrequency;
    private double min_T;
    private double max_T;
    private ConcordiaGraphPanel concordiaGraphPanel;
    private DocumentListener masterListener;

    private enum TextFields {

        xMin_text, xDateMin_text, yMin_text, yDateMin_text,//
        xMax_text, xDateMax_text, yMax_text, yDateMax_text
    }

    /**
     * Creates new form GraphAxesDialog
     *
     * @param parent
     * @param graphAxesSetup
     * @param modal
     * @param concordiaGraphPanel
     */
    public GraphAxesDialog( //
            java.awt.Frame parent, //
            boolean modal,
            GraphAxesSetup graphAxesSetup,
            ConcordiaGraphPanel concordiaGraphPanel) {

        super(parent, modal);

        this.graphAxesSetup = graphAxesSetup;
        graphAxesSetup.calculateConcordiaDateBoundsFromRatiosBounds();

        this.xDecimalPlaceCount = graphAxesSetup.getXaxisSetup().getDecimalPlaceCount();
        this.xTicIncrement = graphAxesSetup.getXaxisSetup().getTicIncrement();
        this.xTicLabelFrequency = graphAxesSetup.getXaxisSetup().getTicLabelFrequency();
        this.xMin = graphAxesSetup.getXaxisSetup().getMin_Display();
        this.xMax = graphAxesSetup.getXaxisSetup().getMax_Display();
        this.xMin_T = graphAxesSetup.getXaxisSetup().getMin_T();
        this.xMax_T = graphAxesSetup.getXaxisSetup().getMax_T();

        this.yDecimalPlaceCount = graphAxesSetup.getYaxisSetup().getDecimalPlaceCount();
        this.yTicIncrement = graphAxesSetup.getYaxisSetup().getTicIncrement();
        this.yTicLabelFrequency = graphAxesSetup.getYaxisSetup().getTicLabelFrequency();
        this.yMin = graphAxesSetup.getYaxisSetup().getMin_Display();
        this.yMax = graphAxesSetup.getYaxisSetup().getMax_Display();
        this.yMin_T = graphAxesSetup.getYaxisSetup().getMin_T();
        this.yMax_T = graphAxesSetup.getYaxisSetup().getMax_T();

        this.cDecimalPlaceCount = graphAxesSetup.getcDecimalPlaceCount();
        this.cTicIncrement = graphAxesSetup.getcTicIncrement();
        this.cTicLabelFrequency = graphAxesSetup.getcTicLabelFrequency();
        this.min_T = graphAxesSetup.getMin_T();
        this.max_T = graphAxesSetup.getMax_T();

        this.concordiaGraphPanel = concordiaGraphPanel;

        initComponents();

        setSize(560, 680);
        //Get the screen size
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();

        //Calculate the frame location
        int x = (screenSize.width - getWidth()) / 2;
        int y = (screenSize.height - getHeight()) / 2;

        //Set the new frame location
        setLocation(x, y);


        masterListener = new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                updateFields((JTextComponent) ((UnDoAbleDocument) e.getDocument()).getTextComp());
            }

            public void removeUpdate(DocumentEvent e) {
                updateFields((JTextComponent) ((UnDoAbleDocument) e.getDocument()).getTextComp());
            }

            public void changedUpdate(DocumentEvent e) {
            }

            private void updateFields(JTextComponent textField) {

                switch (TextFields.valueOf(textField.getName())) {
                    case xMin_text:
                        try {
                            getGraphAxesSetup().getXaxisSetup().setMin(safeDoubleConversion(xMin_text.getText()));
                            getGraphAxesSetup().calculateConcordiaDateBoundsFromRatiosBounds();
                            if (!getGraphAxesSetup().isTerraWasserbergGraph()) {
                                xMin_T = getGraphAxesSetup().getXaxisSetup().getMin_T();
                                xDateMin_text.getDocument().removeDocumentListener(this);
                                try {
                                    xDateMin_text.setText(formattedDateValue(xMin_T));
                                } catch (Exception e) {
                                    System.out.println("IN xMin_text  " + e.getMessage());
                                }
                                xDateMin_text.getDocument().addDocumentListener(this);
                            } else {
                                xMax_T = getGraphAxesSetup().getXaxisSetup().getMax_T();
                                xDateMax_text.getDocument().removeDocumentListener(this);
                                try {
                                    xDateMax_text.setText(formattedDateValue(xMax_T));
                                } catch (Exception e) {
                                    System.out.println("IN xMin_text  " + e.getMessage());
                                }
                                xDateMax_text.getDocument().addDocumentListener(this);
                            }
                        } catch (NumberFormatException numberFormatException) {
                        }
                        break;
                    case xDateMin_text:
                        try {
                            // convert to annum from Ma
                            getGraphAxesSetup().getXaxisSetup().setMin_T(safeDoubleConversion(xDateMin_text.getText()) * 1e6);
                            getGraphAxesSetup().calculateRatiosBoundsFromConcordiaDateBounds();
                            if (!getGraphAxesSetup().isTerraWasserbergGraph()) {
                                xMin = getGraphAxesSetup().getXaxisSetup().getMin();
                                xMin_text.getDocument().removeDocumentListener(this);
                                try {
                                    xMin_text.setText(formattedRatioValue(xMin));
                                } catch (Exception e) {
                                    System.out.println("IN xDateMin_text   " + e.getMessage());
                                }
                                xMin_text.getDocument().addDocumentListener(this);
                            } else {
                                xMax = getGraphAxesSetup().getXaxisSetup().getMax();
                                xMax_text.getDocument().removeDocumentListener(this);
                                try {
                                    xMax_text.setText(formattedRatioValue(xMax));
                                } catch (Exception e) {
                                    System.out.println("IN xDateMin_text   " + e.getMessage());
                                }
                                xMax_text.getDocument().addDocumentListener(this);

                            }
                        } catch (NumberFormatException numberFormatException) {
                        }
                        break;
                    case yMin_text:
                        try {
                            getGraphAxesSetup().getYaxisSetup().setMin(safeDoubleConversion(yMin_text.getText()));
                            getGraphAxesSetup().calculateConcordiaDateBoundsFromRatiosBounds();
                            yMin_T = getGraphAxesSetup().getYaxisSetup().getMin_T();
                            yDateMin_text.getDocument().removeDocumentListener(this);
                            try {
                                yDateMin_text.setText(formattedDateValue(yMin_T));
                            } catch (Exception e) {
                                System.out.println("IN yMin_text  " + e.getMessage());
                            }
                            yDateMin_text.getDocument().addDocumentListener(this);
                        } catch (NumberFormatException numberFormatException) {
                        }
                        break;
                    case yDateMin_text:
                        try {
                            // convert to annum from Ma
                            getGraphAxesSetup().getYaxisSetup().setMin_T(safeDoubleConversion(yDateMin_text.getText()) * 1e6);
                            getGraphAxesSetup().calculateRatiosBoundsFromConcordiaDateBounds();
                            yMin = getGraphAxesSetup().getYaxisSetup().getMin();
                            yMin_text.getDocument().removeDocumentListener(this);
                            try {
                                yMin_text.setText(formattedRatioValue(yMin));
                            } catch (Exception e) {
                                System.out.println("IN yDateMin_text   " + e.getMessage());
                            }
                            yMin_text.getDocument().addDocumentListener(this);
                        } catch (NumberFormatException numberFormatException) {
                        }
                        break;
                    case xMax_text:
                        try {
                            getGraphAxesSetup().getXaxisSetup().setMax(safeDoubleConversion(xMax_text.getText()));
                            getGraphAxesSetup().calculateConcordiaDateBoundsFromRatiosBounds();
                            if (!getGraphAxesSetup().isTerraWasserbergGraph()) {
                                xMax_T = getGraphAxesSetup().getXaxisSetup().getMax_T();
                                xDateMax_text.getDocument().removeDocumentListener(this);
                                try {
                                    xDateMax_text.setText(formattedDateValue(xMax_T));
                                } catch (Exception e) {
                                    System.out.println("IN xMax_text  " + e.getMessage());
                                }
                                xDateMax_text.getDocument().addDocumentListener(this);
                            } else {
                                xMin_T = getGraphAxesSetup().getXaxisSetup().getMin_T();
                                xDateMin_text.getDocument().removeDocumentListener(this);
                                try {
                                    xDateMin_text.setText(formattedDateValue(xMin_T));
                                } catch (Exception e) {
                                    System.out.println("IN xMax_text  " + e.getMessage());
                                }
                                xDateMin_text.getDocument().addDocumentListener(this);
                            }
                        } catch (NumberFormatException numberFormatException) {
                        }
                        break;
                    case xDateMax_text:
                        try {
                            // convert to annum from Ma
                            getGraphAxesSetup().getXaxisSetup().setMax_T(safeDoubleConversion(xDateMax_text.getText()) * 1e6);
                            getGraphAxesSetup().calculateRatiosBoundsFromConcordiaDateBounds();
                            if (!getGraphAxesSetup().isTerraWasserbergGraph()) {
                                xMax = getGraphAxesSetup().getXaxisSetup().getMax();
                                xMax_text.getDocument().removeDocumentListener(this);
                                try {
                                    xMax_text.setText(formattedRatioValue(xMax));
                                } catch (Exception e) {
                                    System.out.println("IN xDateMin_text   " + e.getMessage());
                                }
                                xMax_text.getDocument().addDocumentListener(this);
                            } else {
                                xMin = getGraphAxesSetup().getXaxisSetup().getMin();
                                xMin_text.getDocument().removeDocumentListener(this);
                                try {
                                    xMin_text.setText(formattedRatioValue(xMin));
                                } catch (Exception e) {
                                    System.out.println("IN xDateMin_text   " + e.getMessage());
                                }
                                xMin_text.getDocument().addDocumentListener(this);
                            }
                        } catch (NumberFormatException numberFormatException) {
                        }
                        break;
                    case yMax_text:
                        try {
                            getGraphAxesSetup().getYaxisSetup().setMax(safeDoubleConversion(yMax_text.getText()));
                            getGraphAxesSetup().calculateConcordiaDateBoundsFromRatiosBounds();
                            yMax_T = getGraphAxesSetup().getYaxisSetup().getMax_T();
                            yDateMax_text.getDocument().removeDocumentListener(this);
                            try {
                                yDateMax_text.setText(formattedDateValue(yMax_T));
                            } catch (Exception e) {
                                System.out.println("IN yMin_text  " + e.getMessage());
                            }
                            yDateMax_text.getDocument().addDocumentListener(this);
                        } catch (NumberFormatException numberFormatException) {
                        }
                        break;
                    case yDateMax_text:
                        try {
                            // convert to annum from Ma
                            getGraphAxesSetup().getYaxisSetup().setMax_T(safeDoubleConversion(yDateMax_text.getText()) * 1e6);
                            getGraphAxesSetup().calculateRatiosBoundsFromConcordiaDateBounds();
                            yMax = getGraphAxesSetup().getYaxisSetup().getMax();
                            yMax_text.getDocument().removeDocumentListener(this);
                            try {
                                yMax_text.setText(formattedRatioValue(yMax));
                            } catch (Exception e) {
                                System.out.println("IN yDateMin_text   " + e.getMessage());
                            }
                            yMax_text.getDocument().addDocumentListener(this);
                        } catch (NumberFormatException numberFormatException) {
                        }
                        break;
                    default:

                }

            }
        };

        // x axis **************************************************************
        SpinnerModel xAxisDecimalPlaceCount_spinnerModel =
                new SpinnerNumberModel(//
                xDecimalPlaceCount, 0, 10, 1);

        xAxisDecimalPlaceCount_spinner.setModel(xAxisDecimalPlaceCount_spinnerModel);
        for (Enumeration e = xTicIncrement_group.getElements(); e.hasMoreElements();) {
            JRadioButton jrb = (JRadioButton) e.nextElement();
            if (jrb.getName().equalsIgnoreCase(Integer.toString(xTicIncrement))) {
                jrb.setSelected(true);
            }
        }
        SpinnerModel xTicLabelFrequency_spinnerModel =
                new SpinnerNumberModel(//
                xTicLabelFrequency, 1, 5, 1);

        xTicLabelFrequency_spinner.setModel(xTicLabelFrequency_spinnerModel);

        xMin_text.setDocument(new DoubleDocument(xMin_text,
                true));
        xMin_text.setText(formattedRatioValue(xMin));
        xMin_text.getDocument().addDocumentListener(masterListener);

        xMax_text.setDocument(
                new DoubleDocument(xMax_text, true));
        xMax_text.setText(formattedRatioValue(xMax));
        xMax_text.getDocument().addDocumentListener(masterListener);

        xDateMin_text.setDocument(
                new DoubleDocument(xDateMin_text, true));
        xDateMin_text.setText(formattedDateValue(xMin_T));
        xDateMin_text.getDocument().addDocumentListener(masterListener);

        xDateMax_text.setDocument(new DoubleDocument(xDateMax_text, ReduxConstants.MAX_DATE_ANNUM / 1e6, true));
        xDateMax_text.setText(formattedDateValue(xMax_T));
        xDateMax_text.getDocument().addDocumentListener(masterListener);


        // y axis **************************************************************
        SpinnerModel yAxisDecimalPlaceCount_spinnerModel =
                new SpinnerNumberModel(//
                yDecimalPlaceCount, 0, 10, 1);

        yAxisDecimalPlaceCount_spinner.setModel(yAxisDecimalPlaceCount_spinnerModel);
        for (Enumeration e = yTicIncrement_group.getElements(); e.hasMoreElements();) {
            JRadioButton jrb = (JRadioButton) e.nextElement();
            if (jrb.getName().equalsIgnoreCase(Integer.toString(yTicIncrement))) {
                jrb.setSelected(true);
            }
        }
        SpinnerModel yTicLabelFrequency_spinnerModel =
                new SpinnerNumberModel(//
                yTicLabelFrequency, 1, 5, 1);

        yTicLabelFrequency_spinner.setModel(yTicLabelFrequency_spinnerModel);

        yMin_text.setDocument(new DoubleDocument(yMin_text,
                true));
        yMin_text.setText(formattedRatioValue(yMin));
        yMin_text.getDocument().addDocumentListener(masterListener);

        yMax_text.setDocument(
                new DoubleDocument(yMax_text, true));
        yMax_text.setText(formattedRatioValue(yMax));
        yMax_text.getDocument().addDocumentListener(masterListener);

        yDateMin_text.setDocument(
                new DoubleDocument(yDateMin_text, true));
        yDateMin_text.setText(formattedDateValue(yMin_T));
        yDateMin_text.getDocument().addDocumentListener(masterListener);

        yDateMax_text.setDocument(new DoubleDocument(yDateMax_text, ReduxConstants.MAX_DATE_ANNUM / 1e6, true));
        yDateMax_text.setText(formattedDateValue(yMax_T));
        yDateMax_text.getDocument().addDocumentListener(masterListener);

        if (getGraphAxesSetup().isyAxisHorizontalTicLabels()) {
            yHorizontalLabels_radio.setSelected(true);
        } else {
            yVerticalLabels_radio.setSelected(true);
        }



        // concordia axis **************************************************************

        cLastDigit_slider.setMinimum(2);
        cLastDigit_slider.setMaximum(6);
        cLastDigit_slider.setMajorTickSpacing(1);
        cLastDigit_slider.setSnapToTicks(true);

        cLastDigit_slider.setLabelTable(new ReduxConstants.ConcordiaTicFormatStrings(Integer.toString(cTicIncrement)));
        cLastDigit_slider.setPaintTicks(true);
        cLastDigit_slider.setPaintLabels(true);

        try {
            cLastDigit_slider.addChangeListener(new ChangeListener() {

                public void stateChanged(ChangeEvent e) {
                    cDecimalPlaceCount = (Integer) ((JSlider) e.getSource()).getValue() - 5;
//                    String format = //
//                            ReduxConstants.getConcordiaTicFormatString((Integer) ((JSlider) e.getSource()).getValue());
//                    concordiaTicFormat_label.setText( format.replace( "n", Integer.toString( cTicIncrement ) ) );
                }
            });
        } catch (Exception e) {
        }
        cLastDigit_slider.setValue(cDecimalPlaceCount + 5);



        for (Enumeration e = cTicIncrement_group.getElements(); e.hasMoreElements();) {
            final JRadioButton jrb = (JRadioButton) e.nextElement();
            if (jrb.getName().equalsIgnoreCase(Integer.toString(cTicIncrement))) {
                jrb.setSelected(true);
            }
            try {
                jrb.addChangeListener(new ChangeListener() {

                    public void stateChanged(ChangeEvent e) {
                        cTicIncrement = Integer.parseInt(jrb.getName());
                        cLastDigit_slider.setLabelTable(new ReduxConstants.ConcordiaTicFormatStrings(Integer.toString(cTicIncrement)));
                        cLastDigit_slider.repaint();
                    }
                });
            } catch (NumberFormatException numberFormatException) {
            }
        }


        SpinnerModel cTicLabelFrequency_spinnerModel =
                new SpinnerNumberModel(//
                cTicLabelFrequency, 1, 5, 1);

        cTicLabelFrequency_spinner.setModel(cTicLabelFrequency_spinnerModel);


    }

    private String formattedRatioValue(double ratioValue) {
        NumberFormat vFormat = new DecimalFormat("0.000000000000000".substring(0, xDecimalPlaceCount + 6));


        return vFormat.format(ratioValue);


    }

    private String formattedDateValue(double dateValue) {
        NumberFormat dateFormat = null;
        String retVal = "";



        if (dateValue > 999999999) {
            dateFormat = new DecimalFormat("0000");
            retVal = dateFormat.format(dateValue / 1.0E6);


        } else {
            dateFormat = new DecimalFormat("##0.00");
            retVal = dateFormat.format(dateValue / 1.0E6);
            retVal = "        ".substring(0, 6 - retVal.length()) + retVal;


        }

        return retVal;


    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        yTicIncrement_group = new javax.swing.ButtonGroup();
        xTicIncrement_group = new javax.swing.ButtonGroup();
        yAxisTicLabelOrientation_group = new javax.swing.ButtonGroup();
        cTicIncrement_group = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        yAxisDecimalPlaceCount_spinner = new javax.swing.JSpinner();
        yTicIncrementBy1_radio = new javax.swing.JRadioButton();
        yTicIncrementBy2_radio = new javax.swing.JRadioButton();
        yTicIncrementBy5_radio = new javax.swing.JRadioButton();
        yTicIncrementBy10_radio = new javax.swing.JRadioButton();
        yTicLabelFrequency_spinner = new javax.swing.JSpinner();
        OK_button = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        xAxisDecimalPlaceCount_spinner = new javax.swing.JSpinner();
        xTicIncrementBy1_radio = new javax.swing.JRadioButton();
        xTicIncrementBy2_radio = new javax.swing.JRadioButton();
        xTicIncrementBy5_radio = new javax.swing.JRadioButton();
        xTicIncrementBy10_radio = new javax.swing.JRadioButton();
        xTicLabelFrequency_spinner = new javax.swing.JSpinner();
        apply_button = new javax.swing.JButton();
        yMin_text = new javax.swing.JTextField();
        bounds_label = new javax.swing.JLabel();
        bounds_label1 = new javax.swing.JLabel();
        bounds_label2 = new javax.swing.JLabel();
        yMax_text = new javax.swing.JTextField();
        xMin_text = new javax.swing.JTextField();
        xMax_text = new javax.swing.JTextField();
        xDateMax_text = new javax.swing.JTextField();
        xDateMin_text = new javax.swing.JTextField();
        bounds_label3 = new javax.swing.JLabel();
        bounds_label4 = new javax.swing.JLabel();
        bounds_label5 = new javax.swing.JLabel();
        bounds_label6 = new javax.swing.JLabel();
        yDateMin_text = new javax.swing.JTextField();
        yDateMax_text = new javax.swing.JTextField();
        snapXConcordiaToDiagonal_button = new javax.swing.JButton();
        snapYConcordiaToDiagonal_button = new javax.swing.JButton();
        yVerticalLabels_radio = new javax.swing.JRadioButton();
        yHorizontalLabels_radio = new javax.swing.JRadioButton();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        cTicLabelFrequency_spinner = new javax.swing.JSpinner();
        cTicIncrementBy5_radio = new javax.swing.JRadioButton();
        cTicIncrementBy1_radio = new javax.swing.JRadioButton();
        cTicIncrementBy2_radio = new javax.swing.JRadioButton();
        jLabel12 = new javax.swing.JLabel();
        cLastDigit_slider = new javax.swing.JSlider();

        setTitle("Setup Concordia Axes");
        setAlwaysOnTop(true);
        setBackground(new java.awt.Color(215, 215, 215));
        setResizable(false);

        jLabel1.setFont(new java.awt.Font("SansSerif", 1, 14));
        jLabel1.setText("y axis tics:");

        jLabel2.setFont(new java.awt.Font("SansSerif", 1, 14));
        jLabel2.setText("set count of decimal places:");

        jLabel3.setFont(new java.awt.Font("SansSerif", 1, 14));
        jLabel3.setText("increment last decimal place by:");

        jLabel4.setFont(new java.awt.Font("SansSerif", 1, 14));
        jLabel4.setText("set frequency of tic labels at every:");

        yTicIncrement_group.add(yTicIncrementBy1_radio);
        yTicIncrementBy1_radio.setText("1");
        yTicIncrementBy1_radio.setName("1"); // NOI18N

        yTicIncrement_group.add(yTicIncrementBy2_radio);
        yTicIncrementBy2_radio.setText("2");
        yTicIncrementBy2_radio.setName("2"); // NOI18N

        yTicIncrement_group.add(yTicIncrementBy5_radio);
        yTicIncrementBy5_radio.setText("5");
        yTicIncrementBy5_radio.setName("5"); // NOI18N

        yTicIncrement_group.add(yTicIncrementBy10_radio);
        yTicIncrementBy10_radio.setText("10");
        yTicIncrementBy10_radio.setName("10"); // NOI18N

        OK_button.setText("OK");
        OK_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                OK_buttonActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("SansSerif", 1, 14));
        jLabel5.setText("x axis tics:");

        jLabel6.setFont(new java.awt.Font("SansSerif", 1, 14));
        jLabel6.setText("set count of decimal places:");

        jLabel7.setFont(new java.awt.Font("SansSerif", 1, 14));
        jLabel7.setText("increment last decimal place by:");

        jLabel8.setFont(new java.awt.Font("SansSerif", 1, 14));
        jLabel8.setText("set frequency of tic labels at every:");

        xTicIncrement_group.add(xTicIncrementBy1_radio);
        xTicIncrementBy1_radio.setText("1");
        xTicIncrementBy1_radio.setName("1"); // NOI18N

        xTicIncrement_group.add(xTicIncrementBy2_radio);
        xTicIncrementBy2_radio.setText("2");
        xTicIncrementBy2_radio.setName("2"); // NOI18N

        xTicIncrement_group.add(xTicIncrementBy5_radio);
        xTicIncrementBy5_radio.setText("5");
        xTicIncrementBy5_radio.setName("5"); // NOI18N

        xTicIncrement_group.add(xTicIncrementBy10_radio);
        xTicIncrementBy10_radio.setText("10");
        xTicIncrementBy10_radio.setName("10"); // NOI18N

        apply_button.setText("Apply");
        apply_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                apply_buttonActionPerformed(evt);
            }
        });

        yMin_text.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        yMin_text.setText("0");
        yMin_text.setName("yMin_text"); // NOI18N

        bounds_label.setFont(new java.awt.Font("SansSerif", 1, 14));
        bounds_label.setText("bounds:");

        bounds_label1.setFont(new java.awt.Font("SansSerif", 1, 14));
        bounds_label1.setText("min");

        bounds_label2.setFont(new java.awt.Font("SansSerif", 1, 14));
        bounds_label2.setText("max");

        yMax_text.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        yMax_text.setText("0");
        yMax_text.setName("yMax_text"); // NOI18N

        xMin_text.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        xMin_text.setText("0");
        xMin_text.setName("xMin_text"); // NOI18N

        xMax_text.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        xMax_text.setText("0");
        xMax_text.setName("xMax_text"); // NOI18N

        xDateMax_text.setBackground(new java.awt.Color(220, 255, 255));
        xDateMax_text.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        xDateMax_text.setText("0");
        xDateMax_text.setName("xDateMax_text"); // NOI18N

        xDateMin_text.setBackground(new java.awt.Color(220, 255, 255));
        xDateMin_text.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        xDateMin_text.setText("0");
        xDateMin_text.setName("xDateMin_text"); // NOI18N

        bounds_label3.setFont(new java.awt.Font("SansSerif", 1, 14));
        bounds_label3.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        bounds_label3.setText("y ratio");
        bounds_label3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        bounds_label4.setFont(new java.awt.Font("SansSerif", 1, 14));
        bounds_label4.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        bounds_label4.setText("x ratio");
        bounds_label4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        bounds_label5.setFont(new java.awt.Font("SansSerif", 1, 14));
        bounds_label5.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        bounds_label5.setText("x date (Ma)");
        bounds_label5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        bounds_label6.setFont(new java.awt.Font("SansSerif", 1, 14));
        bounds_label6.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        bounds_label6.setText("y date (Ma)");
        bounds_label6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        yDateMin_text.setBackground(new java.awt.Color(220, 255, 255));
        yDateMin_text.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        yDateMin_text.setText("0");
        yDateMin_text.setName("yDateMin_text"); // NOI18N

        yDateMax_text.setBackground(new java.awt.Color(220, 255, 255));
        yDateMax_text.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        yDateMax_text.setText("0");
        yDateMax_text.setName("yDateMax_text"); // NOI18N

        snapXConcordiaToDiagonal_button.setText("<html><center>Snap ( X ) Concordia to diagonal</center></html>");
        snapXConcordiaToDiagonal_button.setActionCommand("<html><center>Snap (x) Concordia to diagonal</center></html>");
        snapXConcordiaToDiagonal_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                snapXConcordiaToDiagonal_buttonActionPerformed(evt);
            }
        });

        snapYConcordiaToDiagonal_button.setText("<html><center>Snap ( Y ) Concordia to diagonal</center></html>");
        snapYConcordiaToDiagonal_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                snapYConcordiaToDiagonal_buttonActionPerformed(evt);
            }
        });

        yAxisTicLabelOrientation_group.add(yVerticalLabels_radio);
        yVerticalLabels_radio.setSelected(true);
        yVerticalLabels_radio.setText("vertical labels");
        yVerticalLabels_radio.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                yVerticalLabels_radioActionPerformed(evt);
            }
        });

        yAxisTicLabelOrientation_group.add(yHorizontalLabels_radio);
        yHorizontalLabels_radio.setText("horizontal labels");

        jLabel9.setFont(new java.awt.Font("SansSerif", 1, 14));
        jLabel9.setForeground(new java.awt.Color(204, 0, 51));
        jLabel9.setText("Concordia Curve tic labels:");

        jLabel10.setFont(new java.awt.Font("SansSerif", 1, 14));
        jLabel10.setText("select increment and placement of last digit:");

        cTicIncrement_group.add(cTicIncrementBy5_radio);
        cTicIncrementBy5_radio.setText("5");
        cTicIncrementBy5_radio.setName("5"); // NOI18N

        cTicIncrement_group.add(cTicIncrementBy1_radio);
        cTicIncrementBy1_radio.setText("1");
        cTicIncrementBy1_radio.setName("1"); // NOI18N

        cTicIncrement_group.add(cTicIncrementBy2_radio);
        cTicIncrementBy2_radio.setText("2");
        cTicIncrementBy2_radio.setName("2"); // NOI18N

        jLabel12.setFont(new java.awt.Font("SansSerif", 1, 14));
        jLabel12.setText("select frequency of major tics at every:");

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(20, 20, 20)
                .add(bounds_label)
                .add(66, 66, 66)
                .add(bounds_label1)
                .add(87, 87, 87)
                .add(bounds_label2))
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(bounds_label4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(bounds_label3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(bounds_label5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(xMin_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(xDateMin_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, xMax_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .add(org.jdesktop.layout.GroupLayout.TRAILING, xDateMax_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                            .add(layout.createSequentialGroup()
                                .add(yMin_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(yMax_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                    .add(layout.createSequentialGroup()
                        .add(bounds_label6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(yDateMin_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(yDateMax_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(snapYConcordiaToDiagonal_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 116, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(snapXConcordiaToDiagonal_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 116, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel5)
                    .add(layout.createSequentialGroup()
                        .add(jLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 83, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(yVerticalLabels_radio)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(yHorizontalLabels_radio))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(6, 6, 6)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                    .add(jLabel6)
                                    .add(jLabel7)
                                    .add(jLabel8)
                                    .add(jLabel2)
                                    .add(jLabel3)
                                    .add(jLabel4)
                                    .add(jLabel12))
                                .add(5, 5, 5)
                                .add(cTicLabelFrequency_spinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 45, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(jLabel9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 292, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(layout.createSequentialGroup()
                                .add(24, 24, 24)
                                .add(jLabel10)))
                        .add(18, 18, 18)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(cTicIncrementBy1_radio)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(cTicIncrementBy2_radio)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(cTicIncrementBy5_radio))
                            .add(layout.createSequentialGroup()
                                .add(yTicIncrementBy1_radio)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(yTicIncrementBy2_radio)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(yTicIncrementBy5_radio)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(yTicIncrementBy10_radio))
                            .add(xTicLabelFrequency_spinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 45, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(layout.createSequentialGroup()
                                .add(xTicIncrementBy1_radio)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(xTicIncrementBy2_radio)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(xTicIncrementBy5_radio)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(xTicIncrementBy10_radio))
                            .add(yAxisDecimalPlaceCount_spinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 46, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(yTicLabelFrequency_spinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 45, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(xAxisDecimalPlaceCount_spinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 46, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, cLastDigit_slider, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 563, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .add(OK_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 250, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(apply_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 296, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {snapXConcordiaToDiagonal_button, snapYConcordiaToDiagonal_button}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(bounds_label)
                    .add(bounds_label1)
                    .add(bounds_label2))
                .add(8, 8, 8)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                            .add(bounds_label4)
                            .add(xMin_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(xMax_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(3, 3, 3)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                            .add(bounds_label5)
                            .add(xDateMin_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(xDateMax_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(snapXConcordiaToDiagonal_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(13, 13, 13)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                            .add(bounds_label3)
                            .add(yMin_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(yMax_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                                .add(bounds_label6)
                                .add(yDateMax_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(yDateMin_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(snapYConcordiaToDiagonal_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jLabel5)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(jLabel6)
                    .add(xAxisDecimalPlaceCount_spinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(8, 8, 8)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(jLabel7)
                    .add(xTicIncrementBy1_radio)
                    .add(xTicIncrementBy2_radio)
                    .add(xTicIncrementBy5_radio)
                    .add(xTicIncrementBy10_radio))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(jLabel8)
                    .add(xTicLabelFrequency_spinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(35, 35, 35)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(yVerticalLabels_radio)
                    .add(yHorizontalLabels_radio))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(jLabel2)
                    .add(yAxisDecimalPlaceCount_spinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(jLabel3)
                    .add(yTicIncrementBy1_radio)
                    .add(yTicIncrementBy2_radio)
                    .add(yTicIncrementBy5_radio)
                    .add(yTicIncrementBy10_radio))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(jLabel4)
                    .add(yTicLabelFrequency_spinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(layout.createSequentialGroup()
                        .add(jLabel9)
                        .add(6, 6, 6)
                        .add(jLabel10))
                    .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                        .add(cTicIncrementBy1_radio)
                        .add(cTicIncrementBy2_radio)
                        .add(cTicIncrementBy5_radio)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(cLastDigit_slider, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.CENTER)
                    .add(jLabel12)
                    .add(cTicLabelFrequency_spinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(OK_button)
                    .add(apply_button)))
        );

        layout.linkSize(new java.awt.Component[] {snapXConcordiaToDiagonal_button, snapYConcordiaToDiagonal_button}, org.jdesktop.layout.GroupLayout.VERTICAL);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void OK_buttonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_OK_buttonActionPerformed
        saveSettings();


        this.setVisible(false);

    }//GEN-LAST:event_OK_buttonActionPerformed
    private void apply_buttonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_apply_buttonActionPerformed
        saveSettings();


        ((ConcordiaGraphPanel) concordiaGraphPanel).repaint();
    }//GEN-LAST:event_apply_buttonActionPerformed
    private void snapXConcordiaToDiagonal_buttonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_snapXConcordiaToDiagonal_buttonActionPerformed
        yDateMin_text.setText(xDateMin_text.getText());
        yDateMax_text.setText(xDateMax_text.getText());
        graphAxesSetup.setUseAutomaticAxisTics(true);
    }//GEN-LAST:event_snapXConcordiaToDiagonal_buttonActionPerformed

    private void snapYConcordiaToDiagonal_buttonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_snapYConcordiaToDiagonal_buttonActionPerformed
        xDateMin_text.setText(yDateMin_text.getText());
        xDateMax_text.setText(yDateMax_text.getText());
        graphAxesSetup.setUseAutomaticAxisTics(true);
    }//GEN-LAST:event_snapYConcordiaToDiagonal_buttonActionPerformed

    private void yVerticalLabels_radioActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_yVerticalLabels_radioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_yVerticalLabels_radioActionPerformed

    private void saveSettings() {

        // x axis
        getGraphAxesSetup().getXaxisSetup().setDecimalPlaceCount((Integer) xAxisDecimalPlaceCount_spinner.getValue());

        for (Enumeration e = xTicIncrement_group.getElements(); e.hasMoreElements();) {
            JRadioButton jrb = (JRadioButton) e.nextElement();

            if (jrb.isSelected()) {
                getGraphAxesSetup().getXaxisSetup().setTicIncrement(Integer.parseInt(jrb.getName()));
            }
        }

        getGraphAxesSetup().getXaxisSetup().setTicLabelFrequency((Integer) xTicLabelFrequency_spinner.getValue());

        // setting axis bounds
        // first test for sanity
        if (safeDoubleConversion(xMin_text.getText()) > safeDoubleConversion(xMax_text.getText())) {
            xMax_text.setText(xMin_text.getText());
        }
        if (safeDoubleConversion(yMin_text.getText()) > safeDoubleConversion(yMax_text.getText())) {
            yMax_text.setText(yMin_text.getText());
        }

        getGraphAxesSetup().getXaxisSetup().setMin(safeDoubleConversion(xMin_text.getText()));
        getGraphAxesSetup().getXaxisSetup().setMax(safeDoubleConversion(xMax_text.getText()));
        getGraphAxesSetup().getXaxisSetup().setMin_T(safeDoubleConversion(xDateMin_text.getText()));
        getGraphAxesSetup().getXaxisSetup().setMax_T(safeDoubleConversion(xDateMax_text.getText()));
        getGraphAxesSetup().getXaxisSetup().setDisplayOffset(0.0);

        // y axis
        getGraphAxesSetup().getYaxisSetup().setDecimalPlaceCount((Integer) yAxisDecimalPlaceCount_spinner.getValue());

        for (Enumeration e = yTicIncrement_group.getElements(); e.hasMoreElements();) {
            JRadioButton jrb = (JRadioButton) e.nextElement();

            if (jrb.isSelected()) {
                getGraphAxesSetup().getYaxisSetup().setTicIncrement(Integer.valueOf(jrb.getName()));
            }
        }

        getGraphAxesSetup().getYaxisSetup().setTicLabelFrequency((Integer) yTicLabelFrequency_spinner.getValue());

        // setting axis bounds
        getGraphAxesSetup().getYaxisSetup().setMin(safeDoubleConversion(yMin_text.getText()));
        getGraphAxesSetup().getYaxisSetup().setMax(safeDoubleConversion(yMax_text.getText()));
        getGraphAxesSetup().getYaxisSetup().setMin_T(safeDoubleConversion(yDateMin_text.getText()));
        getGraphAxesSetup().getYaxisSetup().setMax_T(safeDoubleConversion(yDateMax_text.getText()));
        getGraphAxesSetup().getYaxisSetup().setDisplayOffset(0.0);

        getGraphAxesSetup().setyAxisHorizontalTicLabels(yHorizontalLabels_radio.isSelected());


        // concordia axis
        getGraphAxesSetup().setcDecimalPlaceCount((Integer) cLastDigit_slider.getValue() - 5);

        for (Enumeration e = cTicIncrement_group.getElements(); e.hasMoreElements();) {
            JRadioButton jrb = (JRadioButton) e.nextElement();

            if (jrb.isSelected()) {
                getGraphAxesSetup().setcTicIncrement(Integer.valueOf(jrb.getName()));
            }
        }

        getGraphAxesSetup().setcTicLabelFrequency((Integer) cTicLabelFrequency_spinner.getValue());

    }

    private double safeDoubleConversion(String numberText) {
        double retVal = 0.0;



        if (numberText.length() > 0) {
            retVal = Double.valueOf(numberText);


        }
        return retVal;


    }
    /**
     * @param args the command line arguments
     */
//    public static void main ( String args[] ) {
//        java.awt.EventQueue.invokeLater( new Runnable() {
//
//            public void run () {
//                GraphAxesDialog dialog = new GraphAxesDialog( new javax.swing.JFrame(), true, 1, 1, 10, 1, 1, 10 );
//                dialog.addWindowListener( new java.awt.event.WindowAdapter() {
//
//                    public void windowClosing ( java.awt.event.WindowEvent e ) {
//                        System.exit( 0 );
//                    }
//                } );
//                dialog.setVisible( true );
//            }
//        } );
//    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton OK_button;
    private javax.swing.JButton apply_button;
    private javax.swing.JLabel bounds_label;
    private javax.swing.JLabel bounds_label1;
    private javax.swing.JLabel bounds_label2;
    private javax.swing.JLabel bounds_label3;
    private javax.swing.JLabel bounds_label4;
    private javax.swing.JLabel bounds_label5;
    private javax.swing.JLabel bounds_label6;
    private javax.swing.JSlider cLastDigit_slider;
    private javax.swing.JRadioButton cTicIncrementBy1_radio;
    private javax.swing.JRadioButton cTicIncrementBy2_radio;
    private javax.swing.JRadioButton cTicIncrementBy5_radio;
    private javax.swing.ButtonGroup cTicIncrement_group;
    private javax.swing.JSpinner cTicLabelFrequency_spinner;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JButton snapXConcordiaToDiagonal_button;
    private javax.swing.JButton snapYConcordiaToDiagonal_button;
    private javax.swing.JSpinner xAxisDecimalPlaceCount_spinner;
    private javax.swing.JTextField xDateMax_text;
    private javax.swing.JTextField xDateMin_text;
    private javax.swing.JTextField xMax_text;
    private javax.swing.JTextField xMin_text;
    private javax.swing.JRadioButton xTicIncrementBy10_radio;
    private javax.swing.JRadioButton xTicIncrementBy1_radio;
    private javax.swing.JRadioButton xTicIncrementBy2_radio;
    private javax.swing.JRadioButton xTicIncrementBy5_radio;
    private javax.swing.ButtonGroup xTicIncrement_group;
    private javax.swing.JSpinner xTicLabelFrequency_spinner;
    private javax.swing.JSpinner yAxisDecimalPlaceCount_spinner;
    private javax.swing.ButtonGroup yAxisTicLabelOrientation_group;
    private javax.swing.JTextField yDateMax_text;
    private javax.swing.JTextField yDateMin_text;
    private javax.swing.JRadioButton yHorizontalLabels_radio;
    private javax.swing.JTextField yMax_text;
    private javax.swing.JTextField yMin_text;
    private javax.swing.JRadioButton yTicIncrementBy10_radio;
    private javax.swing.JRadioButton yTicIncrementBy1_radio;
    private javax.swing.JRadioButton yTicIncrementBy2_radio;
    private javax.swing.JRadioButton yTicIncrementBy5_radio;
    private javax.swing.ButtonGroup yTicIncrement_group;
    private javax.swing.JSpinner yTicLabelFrequency_spinner;
    private javax.swing.JRadioButton yVerticalLabels_radio;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the yDecimalPlaceCount
     */
    public int getyDecimalPlaceCount() {
        return yDecimalPlaceCount;


    }

    /**
     * @param yDecimalPlaceCount the yDecimalPlaceCount to set
     */
    public void setyDecimalPlaceCount(int yDecimalPlaceCount) {
        this.yDecimalPlaceCount = yDecimalPlaceCount;


    }

    /**
     * @return the yTicIncrement
     */
    public int getyTicIncrement() {
        return yTicIncrement;


    }

    /**
     * @param yTicIncrement the yTicIncrement to set
     */
    public void setyTicIncrement(int yTicIncrement) {
        this.yTicIncrement = yTicIncrement;


    }

    /**
     * @return the yTicLabelFrequency
     */
    public int getyTicLabelFrequency() {
        return yTicLabelFrequency;


    }

    /**
     * @param yTicLabelFrequency the yTicLabelFrequency to set
     */
    public void setyTicLabelFrequency(int yTicLabelFrequency) {
        this.yTicLabelFrequency = yTicLabelFrequency;


    }

    /**
     * @return the xDecimalPlaceCount
     */
    public int getxDecimalPlaceCount() {
        return xDecimalPlaceCount;


    }

    /**
     * @param xDecimalPlaceCount the xDecimalPlaceCount to set
     */
    public void setxDecimalPlaceCount(int xDecimalPlaceCount) {
        this.xDecimalPlaceCount = xDecimalPlaceCount;


    }

    /**
     * @return the xTicIncrement
     */
    public int getxTicIncrement() {
        return xTicIncrement;


    }

    /**
     * @param xTicIncrement the xTicIncrement to set
     */
    public void setxTicIncrement(int xTicIncrement) {
        this.xTicIncrement = xTicIncrement;


    }

    /**
     * @return the xTicLabelFrequency
     */
    public int getxTicLabelFrequency() {
        return xTicLabelFrequency;


    }

    /**
     * @param xTicLabelFrequency the xTicLabelFrequency to set
     */
    public void setxTicLabelFrequency(int xTicLabelFrequency) {
        this.xTicLabelFrequency = xTicLabelFrequency;


    }

    /**
     * @return the xMin
     */
    public double getxMin() {
        return xMin;


    }

    /**
     * @param xMin the xMin to set
     */
    public void setxMin(double xMin) {
        this.xMin = xMin;


    }

    /**
     * @return the xMax
     */
    public double getxMax() {
        return xMax;


    }

    /**
     * @param xMax the xMax to set
     */
    public void setxMax(double xMax) {
        this.xMax = xMax;


    }

    /**
     * @return the xMin_T
     */
    public double getxMin_T() {
        return xMin_T;


    }

    /**
     * @param xMin_T the xMin_T to set
     */
    public void setxMin_T(double xMin_T) {
        this.xMin_T = xMin_T;


    }

    /**
     * @return the xMax_T
     */
    public double getxMax_T() {
        return xMax_T;


    }

    /**
     * @param xMax_T the xMax_T to set
     */
    public void setxMax_T(double xMax_T) {
        this.xMax_T = xMax_T;


    }

    /**
     * @return the yMin
     */
    public double getyMin() {
        return yMin;


    }

    /**
     * @param yMin the yMin to set
     */
    public void setyMin(double yMin) {
        this.yMin = yMin;


    }

    /**
     * @return the yMax
     */
    public double getyMax() {
        return yMax;


    }

    /**
     * @param yMax the yMax to set
     */
    public void setyMax(double yMax) {
        this.yMax = yMax;


    }

    /**
     * @return the yMin_T
     */
    public double getyMin_T() {
        return yMin_T;


    }

    /**
     * @param yMin_T the yMin_T to set
     */
    public void setyMin_T(double yMin_T) {
        this.yMin_T = yMin_T;


    }

    /**
     * @return the yMax_T
     */
    public double getyMax_T() {
        return yMax_T;


    }

    /**
     * @param yMax_T the yMax_T to set
     */
    public void setyMax_T(double yMax_T) {
        this.yMax_T = yMax_T;


    }

    /**
     * @return the graphAxesSetup
     */
    public GraphAxesSetup getGraphAxesSetup() {
        return graphAxesSetup;


    }

    /**
     * @param graphAxesSetup the graphAxesSetup to set
     */
    public void setGraphAxesSetup(GraphAxesSetup graphAxesSetup) {
        this.graphAxesSetup = graphAxesSetup;

    }

    /**
     * @return the cDecimalPlaceCount
     */
    public int getcDecimalPlaceCount() {
        return cDecimalPlaceCount;
    }

    /**
     * @param cDecimalPlaceCount the cDecimalPlaceCount to set
     */
    public void setcDecimalPlaceCount(int cDecimalPlaceCount) {
        this.cDecimalPlaceCount = cDecimalPlaceCount;
    }

    /**
     * @return the cTicIncrement
     */
    public int getcTicIncrement() {
        return cTicIncrement;
    }

    /**
     * @param cTicIncrement the cTicIncrement to set
     */
    public void setcTicIncrement(int cTicIncrement) {
        this.cTicIncrement = cTicIncrement;
    }

    /**
     * @return the cTicLabelFrequency
     */
    public int getcTicLabelFrequency() {
        return cTicLabelFrequency;
    }

    /**
     * @param cTicLabelFrequency the cTicLabelFrequency to set
     */
    public void setcTicLabelFrequency(int cTicLabelFrequency) {
        this.cTicLabelFrequency = cTicLabelFrequency;
    }

    /**
     * @return the min_T
     */
    public double getMin_T() {
        return min_T;
    }

    /**
     * @param min_T the min_T to set
     */
    public void setMin_T(double min_T) {
        this.min_T = min_T;
    }

    /**
     * @return the max_T
     */
    public double getMax_T() {
        return max_T;
    }

    /**
     * @param max_T the max_T to set
     */
    public void setMax_T(double max_T) {
        this.max_T = max_T;
    }
}
