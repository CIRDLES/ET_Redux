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
package org.earthtime.archivingTools;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Vector;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.dateInterpretation.DateProbabilityDensityPanel;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.ConcordiaGraphPanel;
import org.earthtime.UPb_Redux.dateInterpretation.graphPersistence.GraphAxesSetup;
import org.earthtime.UPb_Redux.samples.Sample;
import org.earthtime.aliquots.AliquotInterface;
import static org.earthtime.archivingTools.GeoSamplesWebServices.isSampleRegistered;
import static org.earthtime.archivingTools.GeoSamplesWebServices.isSampleRegisteredToParent;
import org.earthtime.archivingTools.forSESAR.SesarSample;
import org.earthtime.archivingTools.forSESAR.SesarSampleManager;
import org.earthtime.beans.ET_JButton;
import org.earthtime.dataDictionaries.RadDates;
import org.earthtime.dialogs.DialogEditor;
import org.earthtime.projects.ProjectInterface;
import org.earthtime.samples.SampleInterface;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public class GeochronAliquotManager extends JPanel {

    private ProjectInterface project;
    private SampleInterface sample;
    private final String userName;
    private final String password;
    private String userCode;
    private String sampleIGSN;
    private final int height;
    private final int width;
    private final static int ROW_HEIGHT = 25;
    private final int LEFT_MARGIN = 5;
    private final int TOP_MARGIN = 5;
    private JTextField sampleNameText;
    private JTextField sampleIGSNText;
    private SesarSample sesarSample;
    private SesarSample[] sesarAliquots;
    private JButton saveButton;
    private JButton registerNewSampleButton;
    private JButton viewSampleRecordButton;
    private JLabel checkMarkForValidSampleIGSN;
    private JLabel xMarkForInValidSampleIGSN;
    // aliquot fields
    private Vector<AliquotInterface> activeAliquots;
    private JTextField[] aliquotName_TextFields;
    private JTextField[] aliquotIGSN_TextFields;
    private JLabel[] checkMarkForValidAliquotIGSNs;
    private JLabel[] xMarkForInValidAliqutIGSNs;
    private JButton[] aliquotUploadButtons;
    private String[] aliquotIGSNs;
    private JButton[] registerNewAliquotButtons;
    private JButton[] viewAliquotRecordButtons;
    private JLabel[] childStatusLabels;
    private JButton[] showConcordiaButtons;
    private JButton[] showPDFButtons;
    private JCheckBox[] publicOptionCheckBoxes;

    public GeochronAliquotManager(ProjectInterface project, SampleInterface sample, String userName, String password, String userCode, int x, int y, int width, int height) {
        this.project = project;
        this.sample = sample;
        this.userName = userName;
        this.password = password;
        this.userCode = userCode;
        this.height = height;
        this.width = width;

        this.sampleIGSN = "IGSN";
        this.sesarSample = new SesarSample(userCode, userName, password, false);

        setOpaque(true);
        setBackground(Color.white);
        setBorder(null);
        setBounds(x, y, this.width, this.height);

        setLayout(null);

        initSampleView();
    }

    private void initSampleView() {
        String sampleName = sample.getSampleName();
        sampleIGSN = sample.getSampleIGSN();

        int cumulativeWidth = LEFT_MARGIN;
        add(labelFactory("Sample Name:", cumulativeWidth, TOP_MARGIN, 100));
        cumulativeWidth += 100;

        sampleNameText = new JTextField(sampleName);
        sampleNameText.setBounds(cumulativeWidth, TOP_MARGIN, 150, ROW_HEIGHT);
        sampleNameText.setFont(ReduxConstants.sansSerif_12_Bold);
        add(sampleNameText);
        sampleNameText.setInputVerifier(new InputVerifier() {

            @Override
            public boolean verify(JComponent input) {
                JTextField textField = (JTextField) input;
                if (textField.getText().trim().length() == 0) {
                    textField.setText(sample.getSampleName());
                }
                return true;
            }
        });
        cumulativeWidth += 150;

        add(labelFactory("IGSN:", cumulativeWidth, TOP_MARGIN, 35));
        cumulativeWidth += 35;

        sampleIGSNText = new JTextField(sampleIGSN);
        sampleIGSNText.setBounds(cumulativeWidth, TOP_MARGIN, 90, ROW_HEIGHT);
        sampleIGSNText.setFont(ReduxConstants.sansSerif_12_Bold);
        add(sampleIGSNText);
        sampleIGSNText.setInputVerifier(new SampleIGSNVerifier());
        sampleIGSNText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int key = e.getKeyCode();
                if ((key == KeyEvent.VK_ENTER) || (key == KeyEvent.VK_TAB)) {
                    sampleIGSNText.getInputVerifier().verify(sampleIGSNText);
                }
            }
        }
        );
        cumulativeWidth += 90;

        // next two occupy same space and show depending on condition
        checkMarkForValidSampleIGSN = new JLabel();
        checkMarkForValidSampleIGSN.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/earthtime/UPb_Redux/images/check_icon.png")));
        checkMarkForValidSampleIGSN.setToolTipText("Sample IGSN is VALID.");
        checkMarkForValidSampleIGSN.setIconTextGap(0);
        checkMarkForValidSampleIGSN.setBounds(cumulativeWidth, TOP_MARGIN, 35, 25);
        checkMarkForValidSampleIGSN.setVisible(false);
        add(checkMarkForValidSampleIGSN);

        xMarkForInValidSampleIGSN = new JLabel();
        xMarkForInValidSampleIGSN.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/earthtime/UPb_Redux/images/icon_red_x.png"))); // NOI18N
        xMarkForInValidSampleIGSN.setToolTipText("Sample IGSN is NOT valid.");
        xMarkForInValidSampleIGSN.setIconTextGap(0);
        xMarkForInValidSampleIGSN.setBounds(cumulativeWidth, TOP_MARGIN, 35, 25);
        xMarkForInValidSampleIGSN.setVisible(false);
        add(xMarkForInValidSampleIGSN);
        cumulativeWidth += 40;

        // next two occupy same space and show depending on condition
        registerNewSampleButton = new ET_JButton("Register New Sample");
        registerNewSampleButton.setBounds(cumulativeWidth, TOP_MARGIN, 135, 25);
        registerNewSampleButton.setFont(ReduxConstants.sansSerif_12_Bold);
        registerNewSampleButton.setVisible(false);
        add(registerNewSampleButton);
        registerNewSampleButton.addActionListener((ActionEvent e) -> {
            saveSample();
            sesarSample.setIGSN(userCode);
            sesarSample.setName(sample.getSampleName());
            DialogEditor sesarSampleManager
                    = new SesarSampleManager(null, true, sesarSample, true);
            sesarSampleManager.setVisible(true);
            sample.setSampleIGSN(sesarSample.getIGSN());
            sampleIGSNText.setText(sesarSample.getIGSN());
            sampleIGSNText.getInputVerifier().verify(sampleIGSNText);
            saveSample();
        });

        viewSampleRecordButton = new ET_JButton("View Existing Record");
        viewSampleRecordButton.setBounds(cumulativeWidth, TOP_MARGIN, 135, 25);
        viewSampleRecordButton.setFont(ReduxConstants.sansSerif_12_Bold);
        viewSampleRecordButton.setVisible(false);
        add(viewSampleRecordButton);
        viewSampleRecordButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                saveSample();
                // get the igsn record and create a SesarSample to view
                SesarSample mySesarSample = SesarSample.createSesarSampleFromSesarRecord(sample.getSampleIGSN());
                if (mySesarSample != null) {
                    sesarSample = mySesarSample;
                    sesarSample.setNameOfLocalSample(sample.getSampleName());
                    DialogEditor sesarSampleManager
                            = new SesarSampleManager(null, true, sesarSample, false);
                    sesarSampleManager.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(
                            null,
                            new String[]{"Could not retrieve sample details."},
                            "ET Redux Warning",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        cumulativeWidth += 135;

        saveButton = new ET_JButton("Save");
        saveButton.setBounds((int) (getBounds().getWidth() - 75.0), TOP_MARGIN, 73, 25);
        saveButton.setFont(ReduxConstants.sansSerif_12_Bold);
        saveButton.setVisible(false);
        add(saveButton);
        saveButton.addActionListener((ActionEvent e) -> {
            saveSample();
        });

        sampleIGSNText.getInputVerifier().verify(sampleIGSNText);

        initAliquotsOfSampleViews();
    }

    private void initAliquotsOfSampleViews() {
        // aliquots
        // the initial working assumption is one aliquot per sample, but we will eventually support n aliquots per sample
        activeAliquots = sample.getActiveAliquots();
        int aliquotCount = activeAliquots.size();
        aliquotName_TextFields = new JTextField[aliquotCount];
        aliquotUploadButtons = new JButton[aliquotCount];
        aliquotIGSN_TextFields = new JTextField[aliquotCount];
        checkMarkForValidAliquotIGSNs = new JLabel[aliquotCount];
        xMarkForInValidAliqutIGSNs = new JLabel[aliquotCount];
        aliquotIGSNs = new String[aliquotCount];
        registerNewAliquotButtons = new JButton[aliquotCount];
        viewAliquotRecordButtons = new JButton[aliquotCount];
        sesarAliquots = new SesarSample[aliquotCount];
        childStatusLabels = new JLabel[aliquotCount];
        showConcordiaButtons = new JButton[aliquotCount];

        for (int i = 0; i < aliquotCount; i++) {
            System.out.println(sample.getSampleName() + "  >  " + activeAliquots.get(i).getAliquotName());
            AliquotInterface aliquot = activeAliquots.get(i);
            String aliquotName = aliquot.getAliquotName();
            aliquotIGSNs[i] = aliquot.getAliquotIGSN();
            sesarAliquots[i] = new SesarSample(userCode, userName, password, true);
            final SesarSample sesarAliquot = sesarAliquots[i];

            int cumulativeWidth = LEFT_MARGIN;
            add(labelFactory("Aliquot Name:", cumulativeWidth, TOP_MARGIN + 30 * (i + 1), 100));
            cumulativeWidth += 100;

            aliquotName_TextFields[i] = new JTextField(aliquotName);
            aliquotName_TextFields[i].setBounds(cumulativeWidth, TOP_MARGIN + 30 * (i + 1), 150, ROW_HEIGHT);
            aliquotName_TextFields[i].setFont(ReduxConstants.sansSerif_12_Bold);
            add(aliquotName_TextFields[i]);
            aliquotName_TextFields[i].setInputVerifier(new InputVerifier() {

                @Override
                public boolean verify(JComponent input) {
                    JTextField textField = (JTextField) input;
                    if (textField.getText().trim().length() == 0) {
                        textField.setText(aliquotName);
                    }
                    return true;
                }
            });
            cumulativeWidth += 150;

            JTextField aliquotName_TextField = aliquotName_TextFields[i];

            add(labelFactory("IGSN:", cumulativeWidth, TOP_MARGIN + 30 * (i + 1), 35));
            cumulativeWidth += 35;

            aliquotIGSN_TextFields[i] = new JTextField(aliquotIGSNs[i]);
            aliquotIGSN_TextFields[i].setBounds(cumulativeWidth, TOP_MARGIN + 30 * (i + 1), 90, ROW_HEIGHT);
            aliquotIGSN_TextFields[i].setFont(ReduxConstants.sansSerif_12_Bold);
            add(aliquotIGSN_TextFields[i]);
            aliquotIGSN_TextFields[i].setInputVerifier(new AliquotIGSNVerifier(i));
            aliquotIGSN_TextFields[i].addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    JTextField textField = (JTextField) e.getSource();
                    int key = e.getKeyCode();
                    if ((key == KeyEvent.VK_ENTER) || (key == KeyEvent.VK_TAB)) {
                        textField.getInputVerifier().verify(textField);
                    }
                }
            }
            );
            JTextField aliquotIGSN_TextField = aliquotIGSN_TextFields[i];
            cumulativeWidth += 90;

            // next two occupy same space and show depending on condition
            checkMarkForValidAliquotIGSNs[i] = new JLabel();
            checkMarkForValidAliquotIGSNs[i].setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/earthtime/UPb_Redux/images/check_icon.png")));
            checkMarkForValidAliquotIGSNs[i].setToolTipText("Aliquot IGSN is VALID.");
            checkMarkForValidAliquotIGSNs[i].setIconTextGap(0);
            checkMarkForValidAliquotIGSNs[i].setBounds(cumulativeWidth, TOP_MARGIN + 30 * (i + 1), 35, 25);
            checkMarkForValidAliquotIGSNs[i].setVisible(false);
            add(checkMarkForValidAliquotIGSNs[i]);

            xMarkForInValidAliqutIGSNs[i] = new JLabel();
            xMarkForInValidAliqutIGSNs[i].setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/earthtime/UPb_Redux/images/icon_red_x.png"))); // NOI18N
            xMarkForInValidAliqutIGSNs[i].setToolTipText("Aliquot IGSN is NOT valid.");
            xMarkForInValidAliqutIGSNs[i].setIconTextGap(0);
            xMarkForInValidAliqutIGSNs[i].setBounds(cumulativeWidth, TOP_MARGIN + 30 * (i + 1), 35, 25);
            xMarkForInValidAliqutIGSNs[i].setVisible(false);
            add(xMarkForInValidAliqutIGSNs[i]);
            cumulativeWidth += 40;

            // next two occupy same space and show depending on condition
            registerNewAliquotButtons[i] = new ET_JButton("Register New Aliquot");
            registerNewAliquotButtons[i].setBounds(cumulativeWidth, TOP_MARGIN + 30 * (i + 1), 135, 25);
            registerNewAliquotButtons[i].setFont(ReduxConstants.sansSerif_12_Bold);
            registerNewAliquotButtons[i].setVisible(false);
            add(registerNewAliquotButtons[i]);
            registerNewAliquotButtons[i].addActionListener((ActionEvent e) -> {
                saveAliquot(aliquot, aliquotIGSN_TextField.getText(), aliquotName_TextField);
                sesarAliquot.setIGSN(userCode);
                sesarAliquot.setName(aliquot.getAliquotName());
                sesarAliquot.setParentIGSN(sample.getSampleIGSN());
                DialogEditor sesarSampleManager
                        = 
                        new SesarSampleManager(null, true, sesarAliquot, true);
                sesarSampleManager.setVisible(true);
                aliquot.setAliquotIGSN(sesarAliquot.getIGSN());
                aliquotIGSN_TextField.setText(sesarAliquot.getIGSN());
                aliquotIGSN_TextField.getInputVerifier().verify(aliquotIGSN_TextField);
                saveAliquot(aliquot, aliquotIGSN_TextField.getText(), aliquotName_TextField);
            });

            viewAliquotRecordButtons[i] = new ET_JButton("View Existing Record");
            viewAliquotRecordButtons[i].setBounds(cumulativeWidth, TOP_MARGIN + 30 * (i + 1), 135, 25);
            viewAliquotRecordButtons[i].setFont(ReduxConstants.sansSerif_12_Bold);
            viewAliquotRecordButtons[i].setVisible(false);
            add(viewAliquotRecordButtons[i]);
            viewAliquotRecordButtons[i].addActionListener((ActionEvent e) -> {
                saveAliquot(aliquot, aliquotIGSN_TextField.getText(), aliquotName_TextField);
                // get the igsn record and create a SesarSample to view
                SesarSample mySesarAliquot = SesarSample.createSesarSampleFromSesarRecord(aliquot.getAliquotIGSN());
                if (mySesarAliquot != null) {
                    //sesarAliquot = mySesarAliquot;
                    mySesarAliquot.setNameOfLocalSample(aliquot.getAliquotName());
                    DialogEditor sesarSampleManager
                            = //
                            new SesarSampleManager(null, true, mySesarAliquot, false);
                    sesarSampleManager.setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(
                            null,
                            new String[]{"Could not retrieve aliquot details."},
                            "ET Redux Warning",
                            JOptionPane.WARNING_MESSAGE);
                }
            });
            cumulativeWidth += 140;

            childStatusLabels[i] = new JLabel("Aliquot IGSN is not a valid child of Sample IGSN.");
            childStatusLabels[i].setBounds(cumulativeWidth, TOP_MARGIN + 30 * (i + 1), 300, 25);
            childStatusLabels[i].setFont(ReduxConstants.sansSerif_12_Bold);
            childStatusLabels[i].setForeground(Color.red);
            add(childStatusLabels[i]);
            //cumulativeWidth += 202;

            showConcordiaButtons[i] = new ET_JButton("View Concordia in browser");
            showConcordiaButtons[i].setBounds(cumulativeWidth, TOP_MARGIN + 30 * (i + 1), 150, 25);
            showConcordiaButtons[i].setFont(ReduxConstants.sansSerif_10_Bold);
            showConcordiaButtons[i].setVisible(false);
            add(showConcordiaButtons[i], JLayeredPane.DEFAULT_LAYER);
            showConcordiaButtons[i].addActionListener((ActionEvent e) -> {
                produceConcordiaGraph(sample);
            });

//            JButton showPDFButton = new ET_JButton("View PDF in browser");
//            showPDFButton.setBounds(leftMargin + 150 + 100 + 50 + 2 + 150 + 2, topMarginForSampleDetails + row * 25, 150, 25);
//            showPDFButton.setFont(ReduxConstants.sansSerif_10_Bold);
//            showPDFButton.setName(String.valueOf(row));
//            showPDFButton.setVisible(false);
//            sampleShowPDFButtons.add(showPDFButton);
//            exportManagerLayeredPane.add(showPDFButton, JLayeredPane.DEFAULT_LAYER);
//            showPDFButton.addActionListener((ActionEvent e) -> {
//                EarthTimeSerializedFileInterface deserializedFile = //
//                        (EarthTimeSerializedFileInterface) ETSerializer.GetSerializedObjectFromFile(ss.getSampleReduxFilePath().toString());
//                Sample sample = (Sample) deserializedFile;
//                producePDFImage(sample);
//            });
//
//            JCheckBox publicOptionCheckBox = new JCheckBox("Public ?");
//            publicOptionCheckBox.setBounds(leftMargin + 150 + 100 + 50 + 2 + 150 + 2 + 150 + 2, topMarginForSampleDetails + row * 25, 100, 25);
//            publicOptionCheckBox.setFont(ReduxConstants.sansSerif_10_Bold);
//            publicOptionCheckBox.setName(String.valueOf(row));
//            publicOptionCheckBox.setVisible(false);
//            samplePublicCheckBoxes.add(publicOptionCheckBox);
//            exportManagerLayeredPane.add(publicOptionCheckBox, JLayeredPane.DEFAULT_LAYER);
////           
//            
//            
//            
            aliquotUploadButtons[i] = new ET_JButton("Upload");
            aliquotUploadButtons[i].setBounds(LEFT_MARGIN + 800, TOP_MARGIN + 30 * (i + 1), 75, 25);
            aliquotUploadButtons[i].setFont(ReduxConstants.sansSerif_12_Bold);
            aliquotUploadButtons[i].setVisible(false);
            add(aliquotUploadButtons[i]);
            aliquotUploadButtons[i].addActionListener((ActionEvent e) -> {
                aliquot.setSampleIGSN("SSR." + sampleIGSN.trim());
                GeochronUploaderUtility.uploadAliquotToGeochron(//
                        sample, //
                        aliquot, //
                        userName, //
                        password, //
                        true, true);
            });

            aliquotIGSN_TextFields[i].getInputVerifier().verify(aliquotIGSN_TextFields[i]);

        }

    }

    private void saveSample() {
        sample.setSampleIGSN(sampleIGSN.trim().toUpperCase());
        // rename supersample aliquots with new sample name
        Vector<AliquotInterface> aliquots = project.getCompiledSuperSample().getAliquots();
        aliquots.stream().forEach((aliquot) -> {
            String aliquotName = aliquot.getAliquotName();
            String sName = sample.getSampleName().trim();
            if (aliquotName.startsWith(sName + "::")) {
                aliquotName = aliquotName.replace(sName + "::", sampleNameText.getText().trim() + "::");
                aliquot.setAliquotName(aliquotName);
            }
        });
        sample.setSampleName(sampleNameText.getText().trim());
    }

    private void saveAliquot(AliquotInterface myAliquot, String aliquotIGSN, JTextField aliquotName_TextField) {
        myAliquot.setAliquotIGSN(aliquotIGSN);
        // rename supersample aliquot also
        Vector<AliquotInterface> aliquots = project.getCompiledSuperSample().getAliquots();
        aliquots.stream().forEach((aliquot) -> {
            String aliquotName = aliquot.getAliquotName();
            String aName = myAliquot.getAliquotName().trim();
            if (aliquotName.endsWith("::" + aName)) {
                aliquotName = aliquotName.replace("::" + aName, "::" + aliquotName_TextField.getText().trim());
                aliquot.setAliquotName(aliquotName);
            }
        });

        myAliquot.setAliquotName(aliquotName_TextField.getText().trim());
    }

    private JLabel labelFactory(String text, int x, int y, int width) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, width, ROW_HEIGHT);
        label.setFont(ReduxConstants.sansSerif_12_Bold);

        return label;
    }

    private void updateValidSampleDisplay(boolean valid) {
        checkMarkForValidSampleIGSN.setVisible(valid);
        xMarkForInValidSampleIGSN.setVisible(!valid);

        viewSampleRecordButton.setVisible(valid);
        registerNewSampleButton.setVisible(!valid);

    }

    private void updateValidAliquotDisplay(int index, boolean valid) {
        checkMarkForValidAliquotIGSNs[index].setVisible(valid);
        xMarkForInValidAliqutIGSNs[index].setVisible(!valid);

        viewAliquotRecordButtons[index].setVisible(valid);
        registerNewAliquotButtons[index].setVisible(!valid);

        childStatusLabels[index].setVisible(!valid);
        showConcordiaButtons[index].setVisible(valid);
    }

    private class SampleIGSNVerifier extends InputVerifier {

        @Override
        public boolean verify(JComponent input) {
            JTextField textField = (JTextField) input;
            String proposedIGSN = textField.getText().toUpperCase();
            textField.setText(proposedIGSN);

            boolean isValid = false;
            sampleIGSN = "IGSN";
            if (proposedIGSN.length() == 9) {
                isValid = isSampleRegistered(proposedIGSN);
                if (isValid) {
                    sampleIGSN = proposedIGSN.trim().toUpperCase();
                } else {
                    if (userCode.trim().length() == 0) {
                        xMarkForInValidSampleIGSN.setToolTipText("Please validate credentials above.");
                    } else {
                        xMarkForInValidSampleIGSN.setToolTipText("SESAR does not have a record of IGSN " + proposedIGSN);
                    }
                }
            }
            saveSample();
            updateValidSampleDisplay(isValid);
            return true;
        }
    }

    private class AliquotIGSNVerifier extends InputVerifier {

        private int index;

        public AliquotIGSNVerifier(int index) {
            this.index = index;
        }

        public boolean verify(JComponent input) {
            JTextField textField = (JTextField) input;
            String proposedIGSN = textField.getText().toUpperCase();
            textField.setText(proposedIGSN);
            boolean isValid = false;
            aliquotIGSNs[index] = "IGSN";
            if (proposedIGSN.length() == 9) {
                isValid = isSampleRegisteredToParent(proposedIGSN, sample.getSampleIGSN());
                if (isValid) {
                    aliquotIGSNs[index] = proposedIGSN.trim().toUpperCase();
                } else {
                    if (userCode.trim().length() == 0) {
                        xMarkForInValidAliqutIGSNs[index].setToolTipText("Please validate credentials above.");
                    } else if (!proposedIGSN.toUpperCase().startsWith(userCode.toUpperCase())) {
                        xMarkForInValidAliqutIGSNs[index].setToolTipText(proposedIGSN + " uses incorrect User Code.  Your User Code is: " + userCode);
                    } else {
                        xMarkForInValidAliqutIGSNs[index].setToolTipText("SESAR does not have a record of IGSN " + proposedIGSN);
                    }
                }
            }
            saveAliquot(activeAliquots.get(index), aliquotIGSNs[index], aliquotName_TextFields[index]);
            updateValidAliquotDisplay(index, isValid);
            return true;
        }
    }

    private void produceConcordiaGraph(SampleInterface sample) {
        // feb 2015 code copied and modified from aliquot manager for user interface prototyping
        // TODO: refactor both locations to smaple and make more robust
        // TODO: use create virtual file system

        File tempConcordiaSVG = new File(sample.getSampleName() + "_tempConcordia.svg");

        ConcordiaGraphPanel concordiaGraphPanel = new ConcordiaGraphPanel((SampleInterface) sample, null);

        sample.getSampleDateInterpretationGUISettings().//
                setConcordiaOptions(concordiaGraphPanel.getConcordiaOptions());
        concordiaGraphPanel.//
                setFadedDeselectedFractions(false);

        // set choices per options code copied (TODO: REFACTOR ME) from SampleDateInterpretations
        Map<String, String> CGO = concordiaGraphPanel.getConcordiaOptions();
        if (CGO.containsKey("showEllipseLabels")) {
            concordiaGraphPanel.setShowEllipseLabels(false);
        }
        if (CGO.containsKey("showExcludedEllipses")) {
            concordiaGraphPanel.setShowExcludedEllipses(true);
        }

        concordiaGraphPanel.setSelectedFractions(sample.getFractions());

        concordiaGraphPanel.setBounds(510, 0, 580, 405);
        concordiaGraphPanel.setCurrentGraphAxesSetup(new GraphAxesSetup("C", 2));
        concordiaGraphPanel.setGraphWidth(565 - GraphAxesSetup.DEFAULT_GRAPH_LEFT_MARGIN_VERTICAL_LABELS);
        concordiaGraphPanel.setGraphHeight(385);

        concordiaGraphPanel.setYorkFitLine(null);
        concordiaGraphPanel.getDeSelectedFractions().clear();
        concordiaGraphPanel.setPreferredDatePanel(null);

        concordiaGraphPanel.setShowTightToEdges(true);

        concordiaGraphPanel.refreshPanel();

        concordiaGraphPanel.setShowTightToEdges(false);

        boolean saveShowTitleBox = concordiaGraphPanel.isShowTitleBox();
        concordiaGraphPanel.setShowTitleBox(false);
        concordiaGraphPanel.setUploadToGeochronMode(true);

        concordiaGraphPanel.outputToSVG(tempConcordiaSVG);
        // concordiaGraphPanel.outputToSVG(virtualPathToSVG.toFile());

        concordiaGraphPanel.setShowTitleBox(saveShowTitleBox);
        concordiaGraphPanel.setUploadToGeochronMode(false);

        // show in a browser
//            Desktop.getDesktop().browse(virtualPathToSVG.toUri());
        try {
            Desktop.getDesktop().browse(tempConcordiaSVG.toURI());
        } catch (IOException iOException) {
            System.out.println("Browser issue " + iOException.getMessage());
        }

    }

    private void producePDFImage(Sample sample) {
        File tempProbabilitySVG = new File(sample.getSampleName() + "_tempProbabilityDensity.svg");

        DateProbabilityDensityPanel probabilityPanel = new DateProbabilityDensityPanel(sample);

        // use default if user has not initialized
        if (probabilityPanel.getSelectedFractions().isEmpty()) {
            probabilityPanel.//
                    setSelectedFractions(sample.getUpbFractionsUnknown());
            probabilityPanel.//
                    getDeSelectedFractions().clear();

            probabilityPanel.setGraphWidth(565);
            probabilityPanel.setGraphHeight(385);

            probabilityPanel.setSelectedHistogramBinCount(5);

            if (sample.isSampleTypeLegacy() & sample.getAnalysisPurpose().equals(ReduxConstants.ANALYSIS_PURPOSE.DetritalSpectrum)) {
                probabilityPanel.setChosenDateName(RadDates.bestAge.getName());
            } else {
                probabilityPanel.setChosenDateName(RadDates.age207_206r.getName());
            }

            probabilityPanel.showTight();

        } else {
            probabilityPanel.setGraphWidth(565);
            probabilityPanel.setGraphHeight(385);
        }

        probabilityPanel.setUploadToGeochronMode(true);

        probabilityPanel.outputToSVG(tempProbabilitySVG);

        probabilityPanel.setUploadToGeochronMode(false);

        try {
            Desktop.getDesktop().browse(tempProbabilitySVG.toURI());
        } catch (IOException iOException) {
            System.out.println("Browser issue " + iOException.getMessage());
        }

    }

    /**
     *
     * @param g
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        paint((Graphics2D) g);
    }

    /**
     *
     * @param g2d
     */
    public void paint(Graphics2D g2d) {

        RenderingHints rh = g2d.getRenderingHints();
        rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHints(rh);

        // bottom border
        g2d.setColor(Color.black);
        // g2d.drawRect(0, getBounds().height - 1, getWidth() - 1, getBounds().height - 1);
        g2d.drawRect(1, 1, getBounds().width - 2, getBounds().height - 2);
    }
}
