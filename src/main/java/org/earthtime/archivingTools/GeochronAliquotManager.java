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
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
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
import org.earthtime.aliquots.AliquotInterface;
import static org.earthtime.archivingTools.GeoSamplesWebServices.isSampleRegisteredToParentAtTestGeoSamples;
import static org.earthtime.archivingTools.GeochronUploaderUtility.produceConcordiaGraphForUploading;
import static org.earthtime.archivingTools.GeochronUploaderUtility.producePDFImageForUploading;
import org.earthtime.archivingTools.forSESAR.SesarSampleManager;
import org.earthtime.beans.ET_JButton;
import org.earthtime.dialogs.DialogEditor;
import org.earthtime.projects.ProjectInterface;
import org.earthtime.samples.SampleInterface;
import org.geosamples.samples.Samples;
import static org.earthtime.archivingTools.GeoSamplesWebServices.isSampleRegisteredAtTestSesar;

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
    private Samples.Sample sesarSample;
    private Samples.Sample[] sesarAliquots;
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
    private JCheckBox[] uploadConcordiaCheckBoxes;
    private JButton[] viewConcordiaButtons;
    private JCheckBox[] uploadPDFCheckBoxes;
    private JButton[] viewPDFButtons;
    private JCheckBox[] publicOptionCheckBoxes;
    private JCheckBox[] updateOptionCheckBoxes;
    private File tempConcordiaSVGforUploading;
    private File tempProbabilitySVGforUploading;

    public GeochronAliquotManager(ProjectInterface project, SampleInterface sample, String userName, String password, String userCode, int x, int y, int width, int height) {
        this.project = project;
        this.sample = sample;
        this.userName = userName;
        this.password = password;
        this.userCode = userCode;
        this.height = height;
        this.width = width;

        this.sampleIGSN = "IGSN";
        this.sesarSample = new org.geosamples.samples.Samples.Sample();
        this.sesarSample.setSampleType(org.geosamples.samples.SampleType.INDIVIDUAL_SAMPLE.value());
        this.sesarSample.setMaterial(org.geosamples.samples.Material.ROCK.value());

        initManager(new Rectangle(x, y, this.width, this.height));

        initSampleView();
    }

    private void initManager(Rectangle bounds) {
        setOpaque(true);
        setBackground(Color.white);
        setBorder(null);
        setBounds(bounds);

        setLayout(null);

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
        }
        //</editor-fold>

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
            sesarSample.setIgsn(userCode);
            sesarSample.setUserCode(userCode);
            sesarSample.setName(sample.getSampleName());
            DialogEditor sesarSampleManager
                    = new SesarSampleManager(null, true, sesarSample, sample.getSampleName(), true, userName, password);
            sesarSampleManager.setVisible(true);
            sample.setSampleIGSN(sesarSample.getIgsn());
            sampleIGSNText.setText(sesarSample.getIgsn());
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
                sesarSample = GeoSamplesWebServices.getSampleMetaDataFromTestSesarIGSN(sampleIGSN, userName, password, false).getSample().get(0);
                if (sesarSample != null) {
                    DialogEditor sesarSampleManager
                            = new SesarSampleManager(null, true, sesarSample, sample.getSampleName(), false, userName, password);
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
        saveButton.setVisible(true);
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
        sesarAliquots = new Samples.Sample[aliquotCount];
        childStatusLabels = new JLabel[aliquotCount];
        uploadConcordiaCheckBoxes = new JCheckBox[aliquotCount];
        viewConcordiaButtons = new JButton[aliquotCount];
        uploadPDFCheckBoxes = new JCheckBox[aliquotCount];
        viewPDFButtons = new JButton[aliquotCount];
        publicOptionCheckBoxes = new JCheckBox[aliquotCount];
        updateOptionCheckBoxes = new JCheckBox[aliquotCount];

        for (int i = 0; i < aliquotCount; i++) {
            System.out.println(sample.getSampleName() + "  >  " + activeAliquots.get(i).getAliquotName());
            AliquotInterface aliquot = activeAliquots.get(i);
            String aliquotName = aliquot.getAliquotName();
            aliquotIGSNs[i] = aliquot.getAliquotIGSN();
            sesarAliquots[i] = new Samples.Sample();
            sesarAliquots[i].setSampleType(org.geosamples.samples.SampleType.INDIVIDUAL_SAMPLE.value());
            sesarAliquots[i].setMaterial(org.geosamples.samples.Material.ROCK.value());

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

            add(labelFactory("IGSN:", cumulativeWidth, TOP_MARGIN + 30 * (i + 1), 35));
            cumulativeWidth += 35;

            aliquotIGSN_TextFields[i] = new JTextField(aliquotIGSNs[i].contains(".") ? aliquotIGSNs[i].split("\\.")[1] : aliquotIGSNs[i]);
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
            registerNewAliquotButtons[i].addActionListener(
                    new aliquotRegistrationActionListener(activeAliquots.get(i), sesarAliquots[i], aliquotIGSN_TextFields[i], aliquotName_TextFields[i]));

            viewAliquotRecordButtons[i] = new ET_JButton("View Existing Record");
            viewAliquotRecordButtons[i].setBounds(cumulativeWidth, TOP_MARGIN + 30 * (i + 1), 135, 25);
            viewAliquotRecordButtons[i].setFont(ReduxConstants.sansSerif_12_Bold);
            viewAliquotRecordButtons[i].setVisible(false);
            add(viewAliquotRecordButtons[i]);
            viewAliquotRecordButtons[i].addActionListener(
                    new aliquotReviewActionListener(activeAliquots.get(i), sesarAliquots[i], aliquotIGSN_TextFields[i], aliquotName_TextFields[i]));

            cumulativeWidth += 135;

            childStatusLabels[i] = new JLabel("Aliquot IGSN is not a valid child of Sample IGSN.");
            childStatusLabels[i].setBounds(cumulativeWidth, TOP_MARGIN + 30 * (i + 1), 300, 25);
            childStatusLabels[i].setFont(ReduxConstants.sansSerif_12_Bold);
            childStatusLabels[i].setForeground(Color.red);
            childStatusLabels[i].setOpaque(false);
            add(childStatusLabels[i]);

            tempConcordiaSVGforUploading = produceConcordiaGraphForUploading(sample, aliquot);

            uploadConcordiaCheckBoxes[i] = new JCheckBox("Upload Concordia");
            uploadConcordiaCheckBoxes[i].setBounds(cumulativeWidth, TOP_MARGIN + 30 * (i + 1), 125, 25);
            uploadConcordiaCheckBoxes[i].setFont(ReduxConstants.sansSerif_10_Bold);
            uploadConcordiaCheckBoxes[i].setVisible(false);
            uploadConcordiaCheckBoxes[i].setOpaque(false);
            add(uploadConcordiaCheckBoxes[i], JLayeredPane.DEFAULT_LAYER);

            cumulativeWidth += 125;

            viewConcordiaButtons[i] = new ET_JButton("View");
            viewConcordiaButtons[i].setBounds(cumulativeWidth, TOP_MARGIN + 30 * (i + 1), 30, 25);
            viewConcordiaButtons[i].setFont(ReduxConstants.sansSerif_10_Bold);
            viewConcordiaButtons[i].setVisible(false);
            viewConcordiaButtons[i].setOpaque(false);
            add(viewConcordiaButtons[i], JLayeredPane.DEFAULT_LAYER);
            viewConcordiaButtons[i].addActionListener((ActionEvent e) -> {
                // show in a browser
                try {
                    Desktop.getDesktop().browse(tempConcordiaSVGforUploading.toURI());
                } catch (IOException iOException) {
                    System.out.println("Browser issue " + iOException.getMessage());
                }

            });

            cumulativeWidth += 30;

            uploadPDFCheckBoxes[i] = new JCheckBox("Upload PDF");
            uploadPDFCheckBoxes[i].setBounds(cumulativeWidth, TOP_MARGIN + 30 * (i + 1), 95, 25);
            uploadPDFCheckBoxes[i].setFont(ReduxConstants.sansSerif_10_Bold);
            uploadPDFCheckBoxes[i].setVisible(false);
            uploadPDFCheckBoxes[i].setOpaque(false);
            add(uploadPDFCheckBoxes[i], JLayeredPane.DEFAULT_LAYER);

            cumulativeWidth += 95;

            //TODO: Add context check for isotope type
            try {
                tempProbabilitySVGforUploading = producePDFImageForUploading(sample, aliquot);
            } catch (Exception e) {
            }
            
            viewPDFButtons[i] = new ET_JButton("View");
            viewPDFButtons[i].setBounds(cumulativeWidth, TOP_MARGIN + 30 * (i + 1), 30, 25);
            viewPDFButtons[i].setFont(ReduxConstants.sansSerif_10_Bold);
            viewPDFButtons[i].setVisible(false);
            viewPDFButtons[i].setOpaque(false);
            add(viewPDFButtons[i], JLayeredPane.DEFAULT_LAYER);
            viewPDFButtons[i].addActionListener((ActionEvent e) -> {
                try {
                    Desktop.getDesktop().browse(tempProbabilitySVGforUploading.toURI());
                } catch (IOException iOException) {
                    System.out.println("Browser issue " + iOException.getMessage());
                }
            });

            cumulativeWidth += 35;

            publicOptionCheckBoxes[i] = new JCheckBox("Public?");
            publicOptionCheckBoxes[i].setBounds(cumulativeWidth, TOP_MARGIN + 30 * (i + 1), 70, 25);
            publicOptionCheckBoxes[i].setFont(ReduxConstants.sansSerif_10_Bold);
            publicOptionCheckBoxes[i].setVisible(true);
            publicOptionCheckBoxes[i].setOpaque(false);
            add(publicOptionCheckBoxes[i], JLayeredPane.DEFAULT_LAYER);

            cumulativeWidth += 70;

            updateOptionCheckBoxes[i] = new JCheckBox("Update?");
            updateOptionCheckBoxes[i].setBounds(cumulativeWidth, TOP_MARGIN + 30 * (i + 1), 75, 25);
            updateOptionCheckBoxes[i].setFont(ReduxConstants.sansSerif_10_Bold);
            updateOptionCheckBoxes[i].setVisible(true);
            updateOptionCheckBoxes[i].setOpaque(false);
            add(updateOptionCheckBoxes[i], JLayeredPane.DEFAULT_LAYER);

            cumulativeWidth += 75;

            aliquotUploadButtons[i] = new ET_JButton("Upload");
            aliquotUploadButtons[i].setBounds(saveButton.getX(), TOP_MARGIN + 30 * (i + 1), 73, 25);
            aliquotUploadButtons[i].setFont(ReduxConstants.sansSerif_12_Bold);
            aliquotUploadButtons[i].setVisible(true);
            add(aliquotUploadButtons[i]);
            aliquotUploadButtons[i].addActionListener(new AliquotUploadActionListener(
                    aliquot, uploadConcordiaCheckBoxes[i], uploadPDFCheckBoxes[i], publicOptionCheckBoxes[i], updateOptionCheckBoxes[i]));

            aliquotIGSN_TextFields[i].getInputVerifier().verify(aliquotIGSN_TextFields[i]);

        }

        revalidate();

    }

    private class AliquotUploadActionListener implements ActionListener {

        private final AliquotInterface aliquot;
        private final JCheckBox uploadConcordiaCheckBox;
        private final JCheckBox uploadPDFCheckBox;
        private final JCheckBox publicOptionCheckBox;
        private final JCheckBox updateOptionCheckBox;

        /**
         *
         * @param aliquot the value of aliquot
         * @param uploadConcordiaCheckBox the value of uploadConcordiaCheckBox
         * @param uploadPDFCheckBox the value of uploadPDFCheckBox
         * @param publicOptionCheckBox the value of publicOptionCheckBox
         * @param updateOptionCheckBox the value of updateOptionCheckBox
         */
        public AliquotUploadActionListener(AliquotInterface aliquot, JCheckBox uploadConcordiaCheckBox, JCheckBox uploadPDFCheckBox, JCheckBox publicOptionCheckBox, JCheckBox updateOptionCheckBox) {
            this.aliquot = aliquot;
            this.uploadConcordiaCheckBox = uploadConcordiaCheckBox;
            this.uploadPDFCheckBox = uploadPDFCheckBox;
            this.publicOptionCheckBox = publicOptionCheckBox;
            this.updateOptionCheckBox = updateOptionCheckBox;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            aliquot.setSampleIGSN("SSX." + sampleIGSN.trim().replaceAll("SSX.", ""));
            aliquot.setAliquotIGSN("SSX." + aliquot.getAliquotIGSN().trim().replaceAll("SSX.", ""));

            if (uploadConcordiaCheckBox.isSelected()) {
                GeochronUploaderUtility.uploadConcordiaImage(tempConcordiaSVGforUploading, aliquot, userName, password);
            }
            if (uploadPDFCheckBox.isSelected()) {
                GeochronUploaderUtility.uploadPDFImage(tempProbabilitySVGforUploading, aliquot, userName, password);
            }

            GeochronUploaderUtility.uploadAliquotToGeochron(//
                    sample, //
                    aliquot, //
                    userName, //
                    password, //
                    publicOptionCheckBox.isSelected(),
                    updateOptionCheckBox.isSelected());
        }

    }

    private class aliquotRegistrationActionListener implements ActionListener {

        private final AliquotInterface aliquot;
        private final Samples.Sample sesarAliquot;
        private final JTextField aliquotIGSN_TextField;
        private final JTextField aliquotName_TextField;

        public aliquotRegistrationActionListener(AliquotInterface aliquot, Samples.Sample sesarAliquot, JTextField aliquotIGSN_TextField, JTextField aliquotName_TextField) {
            this.aliquot = aliquot;
            this.sesarAliquot = sesarAliquot;
            this.aliquotIGSN_TextField = aliquotIGSN_TextField;
            this.aliquotName_TextField = aliquotName_TextField;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            saveAliquot(aliquot, aliquotIGSN_TextField.getText(), aliquotName_TextField);
            sesarAliquot.setIgsn(userCode);
            sesarAliquot.setUserCode(userCode);
            sesarAliquot.setName(aliquot.getAliquotName());
            sesarAliquot.setParentIgsn(sample.getSampleIGSN());
            DialogEditor sesarSampleManager
                    = new SesarSampleManager(null, true, sesarAliquot, aliquot.getAliquotName(), true, userName, password);
            sesarSampleManager.setVisible(true);
            aliquot.setAliquotIGSN(sesarAliquot.getIgsn());
            aliquotIGSN_TextField.setText(sesarAliquot.getIgsn());
            aliquotIGSN_TextField.getInputVerifier().verify(aliquotIGSN_TextField);
            saveAliquot(aliquot, aliquotIGSN_TextField.getText(), aliquotName_TextField);
        }
    }

    private class aliquotReviewActionListener implements ActionListener {

        private final AliquotInterface aliquot;
        private Samples.Sample sesarAliquot;
        private final JTextField aliquotIGSN_TextField;
        private final JTextField aliquotName_TextField;

        public aliquotReviewActionListener(AliquotInterface aliquot, Samples.Sample sesarAliquot, JTextField aliquotIGSN_TextField, JTextField aliquotName_TextField) {
            this.aliquot = aliquot;
            this.sesarAliquot = sesarAliquot;
            this.aliquotIGSN_TextField = aliquotIGSN_TextField;
            this.aliquotName_TextField = aliquotName_TextField;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            saveAliquot(aliquot, aliquotIGSN_TextField.getText(), aliquotName_TextField);
            // get the igsn record and create a SesarSample to view
            sesarAliquot = GeoSamplesWebServices.getSampleMetaDataFromTestSesarIGSN(aliquotIGSN_TextField.getText(), userName, password, false).getSample().get(0);
            if (sesarAliquot != null) {
                DialogEditor sesarSampleManager
                        = new SesarSampleManager(null, true, sesarAliquot, aliquot.getAliquotName(), false, userName, password);
                sesarSampleManager.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(
                        null,
                        new String[]{"Could not retrieve sample details."},
                        "ET Redux Warning",
                        JOptionPane.WARNING_MESSAGE);
            }
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
        uploadConcordiaCheckBoxes[index].setVisible(valid);
        viewConcordiaButtons[index].setVisible(valid);
        uploadPDFCheckBoxes[index].setVisible(valid);
        viewPDFButtons[index].setVisible(valid);
        publicOptionCheckBoxes[index].setVisible(valid);
        updateOptionCheckBoxes[index].setVisible(valid);
        aliquotUploadButtons[index].setVisible(valid);

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
                isValid = isSampleRegisteredAtTestSesar(proposedIGSN);
                if (isValid) {
                    sampleIGSN = proposedIGSN.trim().toUpperCase();
                } else if (userCode.trim().length() == 0) {
                    xMarkForInValidSampleIGSN.setToolTipText("Please validate credentials above.");
                } else {
                    xMarkForInValidSampleIGSN.setToolTipText("SESAR does not have a record of IGSN " + proposedIGSN);
                }
            }

            saveSample();

            // force validation of children
            if (aliquotIGSN_TextFields != null) {
                for (int i = 0; i < aliquotIGSN_TextFields.length; i++) {
                    aliquotIGSN_TextFields[i].getInputVerifier().verify(aliquotIGSN_TextFields[i]);
                }
            }

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
                isValid = isSampleRegisteredToParentAtTestGeoSamples(proposedIGSN, sample.getSampleIGSN());
                if (isValid) {
                    aliquotIGSNs[index] = proposedIGSN.trim().toUpperCase();
                } else if (userCode.trim().length() == 0) {
                    xMarkForInValidAliqutIGSNs[index].setToolTipText("Please validate credentials above.");
                } else if (!proposedIGSN.toUpperCase().startsWith(userCode.toUpperCase())) {
                    xMarkForInValidAliqutIGSNs[index].setToolTipText(proposedIGSN + " uses incorrect User Code.  Your User Code is: " + userCode);
                } else {
                    xMarkForInValidAliqutIGSNs[index].setToolTipText("SESAR does not have a record of IGSN " + proposedIGSN);
                }
            }
            saveAliquot(activeAliquots.get(index), aliquotIGSNs[index], aliquotName_TextFields[index]);
            updateValidAliquotDisplay(index, isValid);
            return true;
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
