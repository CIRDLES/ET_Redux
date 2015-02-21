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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.aliquots.Aliquot;
import org.earthtime.UPb_Redux.aliquots.UPbReduxAliquot;
import org.earthtime.UPb_Redux.dialogs.DialogEditor;
import org.earthtime.UPb_Redux.samples.Sample;
import org.earthtime.UPb_Redux.samples.SampleI;
import org.earthtime.archivingTools.forSESAR.SesarSample;
import org.earthtime.archivingTools.forSESAR.SesarSampleManager;
import org.earthtime.beans.ET_JButton;
import org.earthtime.projects.ProjectI;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public class GeochronAliquotManager extends JPanel {

    private ProjectI project;
    private SampleI sample;
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
    private ArrayList<SesarSample> sesarAliquots;
    private JButton saveButton;
    private JButton registerNewSampleButton;
    private JButton viewSampleRecordButton;
    private JButton uploadButton;
    private JLabel checkMarkForValidSampleIGSN_label;
    private JLabel xMarkForInValidSampleIGSN_label;

    public GeochronAliquotManager(ProjectI project, SampleI sample, String userName, String password, String userCode, int x, int y, int width, int height) {
        this.project = project;
        this.sample = sample;
        this.userName = userName;
        this.password = password;
        this.userCode = userCode;
        this.height = height;
        this.width = width;

        this.sampleIGSN = "IGSN";
        this.sesarSample = new SesarSample(userCode, userName, password, false);
        this.sesarAliquots = new ArrayList<>();

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
        sampleIGSNText.setInputVerifier(new IGSNVerifier());
        sampleIGSNText.addKeyListener(new KeyAdapter() {
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
        checkMarkForValidSampleIGSN_label = new JLabel();
        checkMarkForValidSampleIGSN_label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/earthtime/UPb_Redux/images/check_icon.png")));
        checkMarkForValidSampleIGSN_label.setToolTipText("Sample IGSN is VALID.");
        checkMarkForValidSampleIGSN_label.setIconTextGap(0);
        checkMarkForValidSampleIGSN_label.setBounds(cumulativeWidth, TOP_MARGIN, 35, 25);
        checkMarkForValidSampleIGSN_label.setVisible(false);
        add(checkMarkForValidSampleIGSN_label);

        xMarkForInValidSampleIGSN_label = new JLabel();
        xMarkForInValidSampleIGSN_label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/earthtime/UPb_Redux/images/icon_red_x.png"))); // NOI18N
        xMarkForInValidSampleIGSN_label.setToolTipText("Sample IGSN is NOT valid.");
        xMarkForInValidSampleIGSN_label.setIconTextGap(0);
        xMarkForInValidSampleIGSN_label.setBounds(cumulativeWidth, TOP_MARGIN, 35, 25);
        xMarkForInValidSampleIGSN_label.setVisible(false);
        add(xMarkForInValidSampleIGSN_label);
        cumulativeWidth += 35;

        // next two occupy same space and show depending on condition
        registerNewSampleButton = new ET_JButton("Register New Sample");
        registerNewSampleButton.setBounds(cumulativeWidth, TOP_MARGIN, 135, 25);
        registerNewSampleButton.setFont(ReduxConstants.sansSerif_12_Bold);
        registerNewSampleButton.setVisible(false);
        add(registerNewSampleButton);
        registerNewSampleButton.addActionListener((ActionEvent e) -> {
            saveSample();
            sesarSample.setIGSN("IGSN");
            sesarSample.setName(sample.getSampleName());
            DialogEditor sesarSampleManager = //
                    new SesarSampleManager(null, true, sesarSample, true);
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
                // get the igsn record and create a SesarSample to view
                SesarSample mySesarSample = SesarSample.createSesarSampleFromSesarRecord(sample.getSampleIGSN());
                if (mySesarSample != null) {
                    sesarSample = mySesarSample;
                    sesarSample.setNameOfLocalSample(sample.getSampleName());
                    DialogEditor sesarSampleManager = //
                            new SesarSampleManager(null, true, sesarSample, false);
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

        // aliquots
        uploadButton = new ET_JButton("Upload");
        uploadButton.setBounds(LEFT_MARGIN + 800, TOP_MARGIN, 75, 25);
        uploadButton.setFont(ReduxConstants.sansSerif_12_Bold);
        uploadButton.setVisible(false);
        add(uploadButton);
        uploadButton.addActionListener((ActionEvent e) -> {
            UPbReduxAliquot aliquot = (UPbReduxAliquot) sample.getActiveAliquots().get(0);
            aliquot.setSampleIGSN("SSR." + sampleIGSN.trim());
            GeochronUploaderUtility.uploadAliquotToGeochron(//
                    (Sample) sample, aliquot, //
                    userName, //
                    password, //
                    true, true);
        });

        sampleIGSNText.getInputVerifier().verify(sampleIGSNText);
    }

    private void saveSample() {
        sample.setSampleIGSN(sampleIGSN.trim().toUpperCase());
        // rename supersample aliquots with new sample name
        Vector<Aliquot> aliquots = project.getCompiledSuperSample().getAliquots();
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

    private JLabel labelFactory(String text, int x, int y, int width) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, width, ROW_HEIGHT);
        label.setFont(ReduxConstants.sansSerif_12_Bold);

        return label;
    }

    private void updateValidSampleDisplay(boolean valid) {
        checkMarkForValidSampleIGSN_label.setVisible(valid);
        xMarkForInValidSampleIGSN_label.setVisible(!valid);

        viewSampleRecordButton.setVisible(valid);
        registerNewSampleButton.setVisible(!valid);

        saveButton.setVisible(valid);
        uploadButton.setVisible(valid);
    }

    private class IGSNVerifier extends InputVerifier {

        public boolean verify(JComponent input) {
            JTextField textField = (JTextField) input;
            String proposedIGSN = textField.getText().toUpperCase();
            textField.setText(proposedIGSN);
            if (SesarSample.validateIGSNatSESAR(proposedIGSN)) {
                sampleIGSN = proposedIGSN.trim().toUpperCase();
                saveSample();
                updateValidSampleDisplay(true);
            } else {
                if (userCode.trim().length() == 0) {
                    xMarkForInValidSampleIGSN_label.setToolTipText("Please validate GeochronPortal.org credentials above.");
                } //note: allow any igsn as parent //else if (!proposedIGSN.toUpperCase().startsWith(userCode.toUpperCase())) {
                //xMarkForInValidSampleIGSN_label.setToolTipText(proposedIGSN + " uses incorrect User Code.  Your User Code is: " + userCode);
                // } 
                else {
                    xMarkForInValidSampleIGSN_label.setToolTipText("SESAR does not have a record of IGSN " + proposedIGSN);
                }

                sampleIGSN = "IGSN";
                updateValidSampleDisplay(false);
            }
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
