/*
 * AbstractSampleLegacyManagerDialog.java
 *
 *
 * Copyright 2006-2018 James F. Bowring, CIRDLES.org, and Earth-Time.org
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
package org.earthtime.UPb_Redux.dialogs.sampleManagers.legacyManagers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Vector;
import org.earthtime.UPb_Redux.ReduxConstants.ANALYSIS_PURPOSE;
import org.earthtime.UPb_Redux.exceptions.BadImportedCSVLegacyFileException;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.UPb_Redux.fractions.FractionI;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFractionI;
import org.earthtime.UPb_Redux.samples.UPbSampleInterface;
import org.earthtime.UPb_Redux.samples.sampleImporters.AbstractSampleImporterFromLegacyCSVFile;
import org.earthtime.aliquots.AliquotInterface;
import org.earthtime.aliquots.ReduxAliquotInterface;
import org.earthtime.beans.ET_JButton;
import org.earthtime.dataDictionaries.MineralTypes;
import org.earthtime.dataDictionaries.SampleRegistries;
import org.earthtime.dialogs.DialogEditor;
import org.earthtime.exceptions.ETException;
import org.earthtime.exceptions.ETWarningDialog;
import org.earthtime.fractions.ETFractionInterface;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.reduxLabData.ReduxLabData;
import org.earthtime.samples.SampleInterface;

/**
 *
 * @author James F. Bowring
 */
public abstract class AbstractSampleLegacyManagerDialog extends DialogEditor {

    private SampleInterface mySample = null;
    private File importFractionFolderMRU;
    private boolean initialized = false;
    private boolean newSample = false;
    private AbstractSampleImporterFromLegacyCSVFile converter;

    /**
     * Creates new form AbstractSampleLegacyManagerDialog
     *
     * @param parent
     * @param importFractionFolderMRU
     * @param modal
     * @param sample
     * @param dataTypeTitle
     * @param converter
     */
    public AbstractSampleLegacyManagerDialog(
            java.awt.Frame parent,
            boolean modal,
            String dataTypeTitle,
            SampleInterface sample,
            AbstractSampleImporterFromLegacyCSVFile converter,
            File importFractionFolderMRU) {
        super(parent, modal);

        this.importFractionFolderMRU = importFractionFolderMRU;

        initComponents();

        this.mySample = sample;

        initSampleFields();

        if (sample.getFractions().size() > 0) {
            // we are in edit mode 
            fractionDestinationPanel_panel.setVisible(false);
            fractionSourcePanel_panel.setVisible(false);
        }

        sampleType_label.setText(dataTypeTitle + sampleType_label.getText());

        this.converter = converter;

        // april 2011
        // removed sept 2016
        //       validateSampleID();
    }

    /**
     *
     */
    public void setSize() {
        setSize(480, 685);
    }

    /**
     *
     * @return
     */
    public File getImportFractionFolderMRU() {
        return importFractionFolderMRU;
    }

    /**
     *
     * @param importFractionFolderMRU
     */
    public void setImportFractionFolderMRU(File importFractionFolderMRU) {
        this.importFractionFolderMRU = importFractionFolderMRU;
    }

    private void validateSampleID() {

        mySample.setSampleIGSN(((SampleRegistries) sampleRegistryChooser.getSelectedItem()).getCode() + "." + sampleIGSN_text.getText().trim());
        mySample.setSampleRegistry((SampleRegistries) sampleRegistryChooser.getSelectedItem());

        if (!mySample.isArchivedInRegistry()) {
            boolean valid = SampleRegistries.isSampleIdentifierValidAtRegistry(//
                    mySample.getSampleIGSN());
            validSampleIGSN_label.setText((String) (valid ? "Sample IGSN is Valid at registry." : "Sample IGSN is NOT valid at registry."));
            mySample.setValidatedSampleIGSN(valid);
        }
    }

    private void initSampleFields() {
        // init input fields

        sampleName_text.setDocument(
                new UnDoAbleDocument(sampleName_text, !mySample.isArchivedInRegistry()));
        sampleName_text.setText(getMySample().getSampleName());

        sampleIGSN_text.setDocument(
                new UnDoAbleDocument(sampleIGSN_text, !mySample.isArchivedInRegistry()));
        sampleIGSN_text.setText(getMySample().getSampleIGSNnoRegistry());

        for (SampleRegistries sr : SampleRegistries.values()) {
            sampleRegistryChooser.addItem(sr);
        }
        sampleRegistryChooser.setEnabled(!mySample.isArchivedInRegistry());
        sampleRegistryChooser.setSelectedItem(mySample.getSampleRegistry());
        sampleRegistryChooser.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                mySample.setSampleIGSN(((SampleRegistries) sampleRegistryChooser.getSelectedItem()).getCode() + "." + sampleIGSN_text.getText());
                validateSampleID();
            }
        });

        sampleNotes_textArea.setDocument(new UnDoAbleDocument(sampleNotes_textArea, true));
        sampleNotes_textArea.setText(getMySample().getSampleAnnotations());

        // init display fields - html allows multi-line
        sampleReduxFileName_label.setText(
                "<html><p>" + getMySample().getReduxSampleFilePath() + "</p></html>");
        sampleReduxFileName_label.setToolTipText(getMySample().getReduxSampleFilePath());

        physicalConstantsModelChooser.removeAllItems();
        ArrayList<AbstractRatiosDataModel> physicalConstantsModels = ReduxLabData.getInstance().getPhysicalConstantsModels();
        for (int i = (physicalConstantsModels.size() > 1 ? 1 : 0); i < physicalConstantsModels.size(); i++) {
            physicalConstantsModelChooser.addItem(physicalConstantsModels.get(i).getReduxLabDataElementName());
        }

        physicalConstantsModelChooser.setSelectedIndex(0);
        try {
            physicalConstantsModelChooser.setSelectedItem(getMySample().getPhysicalConstantsModel().getReduxLabDataElementName());
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }

        // set up StandardMineral chooser
        standardMineralNameChooser.removeAllItems();
        for (int i = 0; i < MineralTypes.values().length; i++) {
            standardMineralNameChooser.addItem(MineralTypes.values()[i].getName());
        }

        standardMineralNameChooser.setSelectedItem(mySample.getMineralName());
        standardMineralNameChooser.addItemListener(new MineralNameItemListener());

        // set up analysisPurposeChooser
        analysisPurposeChooser.removeAllItems();
        for (ANALYSIS_PURPOSE ap : ANALYSIS_PURPOSE.values()) {
            analysisPurposeChooser.addItem(ap.toString());
        }

        analysisPurposeChooser.setSelectedItem(mySample.getAnalysisPurpose().toString());
        analysisPurposeChooser.addItemListener(new AnalysisPurposeItemListener());

        if (getMySample().isCalculateTWrhoForLegacyData()) {
            TWCalculateRho_radioBut.setSelected(true);
        } else {
            TWZeroRho_radioBut.setSelected(true);
        }

    }

    class MineralNameItemListener implements ItemListener {
        // This method is called only if a new item has been selected.

        @Override
        public void itemStateChanged(ItemEvent evt) {

            if (evt.getStateChange() == ItemEvent.SELECTED) {
                // Item was just selected
                mySample.setMineralName((String) evt.getItem());

            } else if (evt.getStateChange() == ItemEvent.DESELECTED) {
                // Item is no longer selected
            }
        }
    }

    class AnalysisPurposeItemListener implements ItemListener {
        // This method is called only if a new item has been selected.

        public void itemStateChanged(ItemEvent evt) {

            if (evt.getStateChange() == ItemEvent.SELECTED) {
                // Item was just selected
                mySample.setAnalysisPurpose(ANALYSIS_PURPOSE.valueOf((String) evt.getItem()));

            } else if (evt.getStateChange() == ItemEvent.DESELECTED) {
                // Item is no longer selected
            }
        }
    }

    private void saveSampleData()
            throws ETException {
        // validate sample name
        if ((sampleName_text.getText().trim().length() == 0)
                || (aliquotName_text.getText().trim().length() == 0)) {
            return;
        }

        mySample.setSampleName(sampleName_text.getText().trim());
        mySample.setSampleIGSN(((SampleRegistries) sampleRegistryChooser.getSelectedItem()).getCode() + "." + sampleIGSN_text.getText().trim());
        mySample.setSampleRegistry((SampleRegistries) sampleRegistryChooser.getSelectedItem());
        mySample.setSampleAnnotations(sampleNotes_textArea.getText());

        String currentPhysicalConstantsModelName = "";
        try {
            currentPhysicalConstantsModelName = getMySample().getPhysicalConstantsModel().getNameAndVersion();

        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
        if (!((String) physicalConstantsModelChooser.getSelectedItem()).equalsIgnoreCase(currentPhysicalConstantsModelName)) {
            try {
                getMySample().setPhysicalConstantsModel(
                        ReduxLabData.getInstance().
                        getAPhysicalConstantsModel(((String) physicalConstantsModelChooser.getSelectedItem())));

            } catch (BadLabDataException ex) {
                new ETWarningDialog(ex).setVisible(true);
            }
        }

        // in legacy mode we only allow one aliquot
        if (getMySample().getFractions().isEmpty()) {
            AliquotInterface myAliquot = getMySample().addNewAliquot(aliquotName_text.getText().trim());
            // May 2010 allows publication of legacy results
            ((ReduxAliquotInterface) myAliquot).setCompiled(false);
            int myAliquotNumber = myAliquot.getAliquotNumber();

            // test for manual mode or bulk import from CSV file
            if (manualMode_radioBut.isSelected()) {
                try {
                    ((UPbSampleInterface) mySample).addDefaultUPbLegacyFractionToAliquot(myAliquotNumber);
                    setInitialized(true);
                    mySample.setChanged(true);
                } catch (BadLabDataException ex) {
                    new ETWarningDialog(ex).setVisible(true);
                }
            } else {
                // bulk mode
                try {
                    converter.setMruFolder(importFractionFolderMRU);
                    mySample.addFractionsVector(converter.readInFractions(), myAliquotNumber);
                    myAliquot.setAliquotName(converter.getAliquotName());
                    setInitialized(true);
                    getMySample().setChanged(true);

                    setImportFractionFolderMRU(converter.getMruFolder());
                } catch (FileNotFoundException fileNotFoundException) {
                } catch (BadImportedCSVLegacyFileException ex) {
                    new ETWarningDialog(ex).setVisible(true);
                }
            }

        }

        if (TWZeroRho_radioBut.isSelected()) {
            getMySample().setCalculateTWrhoForLegacyData(false);
        } else {
            getMySample().setCalculateTWrhoForLegacyData(true);
        }

        // moved outside conditional oct 2010 and added MineralName, etc ;;June 2010 add physical constants model
        for (ETFractionInterface f : getMySample().getFractions()) {
            try {
                f.setPhysicalConstantsModel(getMySample().getPhysicalConstantsModel());

                ((FractionI) f).setMineralName(mySample.getMineralName());
                if (mySample.getMineralName().equalsIgnoreCase("zircon")) {
                    ((FractionI) f).setZircon(true);
                } else {
                    ((FractionI) f).setZircon(false);
                }

                f.setLegacy(true);

                if (TWZeroRho_radioBut.isSelected()) {
                    // set all T-W to zero
                    f.getRadiogenicIsotopeRatioByName("rhoR207_206r__r238_206r")//
                            .setValue(BigDecimal.ZERO);
                } else {
                    // calculate all T-W
                    ((UPbFractionI) f).calculateTeraWasserburgRho();
                }

            } catch (BadLabDataException ex) {
                new ETWarningDialog(ex).setVisible(true);
            }
        }

        // there should be only one aliquot
        Vector<AliquotInterface> aliquots = mySample.getActiveAliquots();
        for (AliquotInterface a : aliquots) {
            a.setAnalysisPurpose(mySample.getAnalysisPurpose());
        }

    }

    /**
     *
     * @return
     */
    public SampleInterface getMySample() {
        return mySample;
    }

    /**
     *
     * @param mySample
     */
    public void setMySample(SampleInterface mySample) {
        this.mySample = mySample;
    }

    /**
     *
     * @return
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     *
     * @param isSaved
     */
    public void setInitialized(boolean isSaved) {
        this.initialized = isSaved;
    }

    /**
     *
     * @return
     */
    public boolean isNewSample() {
        return newSample;
    }

    /**
     *
     * @param newSample
     */
    public void setNewSample(boolean newSample) {
        this.newSample = newSample;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        sourceOfFractionsOptions_buttonGroup = new javax.swing.ButtonGroup();
        destinationOfFractionsOptions_buttonGroup = new javax.swing.ButtonGroup();
        updateMode_buttonGroup = new javax.swing.ButtonGroup();
        modeChooser_buttonGroup = new javax.swing.ButtonGroup();
        TWsource = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        sampleName_label = new javax.swing.JLabel();
        sampleName_text = new javax.swing.JTextField();
        sampleIGSN_label = new javax.swing.JLabel();
        sampleIGSN_text = new javax.swing.JTextField();
        sampleReduxFile_label = new javax.swing.JLabel();
        sampleReduxFileName_label = new javax.swing.JLabel();
        sampleNotes_label = new javax.swing.JLabel();
        sampleNotes_scrollPane = new javax.swing.JScrollPane();
        sampleNotes_textArea = new javax.swing.JTextArea();
        fractionDestinationPanel_panel = new javax.swing.JPanel();
        aliquotName_text = new javax.swing.JTextField();
        aliquotName_label = new javax.swing.JLabel();
        physicalConstantsModelChooser = new javax.swing.JComboBox<>();
        defaultHeader_label = new javax.swing.JLabel();
        fractionSourcePanel_panel = new javax.swing.JPanel();
        manualMode_radioBut = new javax.swing.JRadioButton();
        bulkMode_radioBut = new javax.swing.JRadioButton();
        standardMineralNameChooser = new javax.swing.JComboBox<>();
        chooseStandardMineral_label = new javax.swing.JLabel();
        chooseAnalysisPurpose_label = new javax.swing.JLabel();
        analysisPurposeChooser = new javax.swing.JComboBox<>();
        chooseTWrho_label = new javax.swing.JLabel();
        TWZeroRho_radioBut = new javax.swing.JRadioButton();
        TWCalculateRho_radioBut = new javax.swing.JRadioButton();
        sampleIGSN_label1 = new javax.swing.JLabel();
        sampleRegistryChooser = new javax.swing.JComboBox<>();
        validSampleIGSN_label = new javax.swing.JLabel();
        validateIGSN = new ET_JButton();
        jPanel2 = new javax.swing.JPanel();
        close = new ET_JButton();
        saveAndClose = new ET_JButton();
        sampleType_panel = new javax.swing.JPanel();
        sampleType_label = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(245, 236, 206));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jPanel1.setMaximumSize(new java.awt.Dimension(480, 620));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        sampleName_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        sampleName_label.setText("Lab's Local Sample Name:");
        jPanel1.add(sampleName_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(109, 9, -1, -1));

        sampleName_text.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        sampleName_text.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        sampleName_text.setText("Sample Name");
        jPanel1.add(sampleName_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(259, 2, 199, -1));

        sampleIGSN_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        sampleIGSN_label.setText("Sample IGSN:");
        jPanel1.add(sampleIGSN_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(225, 42, -1, -1));

        sampleIGSN_text.setEditable(false);
        sampleIGSN_text.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        sampleIGSN_text.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        sampleIGSN_text.setText("<none>");
        sampleIGSN_text.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                sampleIGSN_textFocusLost(evt);
            }
        });
        jPanel1.add(sampleIGSN_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 35, 158, -1));

        sampleReduxFile_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        sampleReduxFile_label.setText("File path for this Sample:");
        jPanel1.add(sampleReduxFile_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 515, -1, -1));

        sampleReduxFileName_label.setText("<Not Saved>");
        sampleReduxFileName_label.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        sampleReduxFileName_label.setAutoscrolls(true);
        jPanel1.add(sampleReduxFileName_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(28, 535, 407, 64));

        sampleNotes_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        sampleNotes_label.setText("Notes about this Sample:");
        jPanel1.add(sampleNotes_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 405, -1, -1));

        sampleNotes_textArea.setColumns(20);
        sampleNotes_textArea.setRows(5);
        sampleNotes_textArea.setMaximumSize(new java.awt.Dimension(250, 80));
        sampleNotes_textArea.setPreferredSize(new java.awt.Dimension(250, 80));
        sampleNotes_scrollPane.setViewportView(sampleNotes_textArea);

        jPanel1.add(sampleNotes_scrollPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 425, 464, -1));

        fractionDestinationPanel_panel.setBackground(new java.awt.Color(255, 255, 224));
        fractionDestinationPanel_panel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Destination of Fractions", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 13), new java.awt.Color(204, 0, 0))); // NOI18N

        aliquotName_text.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        aliquotName_text.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        aliquotName_text.setText("aliquot");

        aliquotName_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        aliquotName_label.setText("Enter your aliquot name:");

        org.jdesktop.layout.GroupLayout fractionDestinationPanel_panelLayout = new org.jdesktop.layout.GroupLayout(fractionDestinationPanel_panel);
        fractionDestinationPanel_panel.setLayout(fractionDestinationPanel_panelLayout);
        fractionDestinationPanel_panelLayout.setHorizontalGroup(
            fractionDestinationPanel_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(fractionDestinationPanel_panelLayout.createSequentialGroup()
                .addContainerGap()
                .add(aliquotName_label)
                .add(18, 18, 18)
                .add(aliquotName_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 195, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(62, Short.MAX_VALUE))
        );
        fractionDestinationPanel_panelLayout.setVerticalGroup(
            fractionDestinationPanel_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, fractionDestinationPanel_panelLayout.createSequentialGroup()
                .add(fractionDestinationPanel_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(aliquotName_label)
                    .add(aliquotName_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel1.add(fractionDestinationPanel_panel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 210, 450, -1));

        physicalConstantsModelChooser.setBackground(new java.awt.Color(245, 236, 206));
        physicalConstantsModelChooser.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        jPanel1.add(physicalConstantsModelChooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(183, 115, 290, -1));

        defaultHeader_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        defaultHeader_label.setForeground(new java.awt.Color(204, 51, 0));
        defaultHeader_label.setText("Set Physical Constants Model:");
        jPanel1.add(defaultHeader_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 120, -1, -1));

        fractionSourcePanel_panel.setBackground(new java.awt.Color(255, 255, 224));
        fractionSourcePanel_panel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Source of Fractions", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 13), new java.awt.Color(204, 0, 0))); // NOI18N

        modeChooser_buttonGroup.add(manualMode_radioBut);
        manualMode_radioBut.setText("Manual Mode");

        modeChooser_buttonGroup.add(bulkMode_radioBut);
        bulkMode_radioBut.setSelected(true);
        bulkMode_radioBut.setText("Bulk Mode from CSV file");

        org.jdesktop.layout.GroupLayout fractionSourcePanel_panelLayout = new org.jdesktop.layout.GroupLayout(fractionSourcePanel_panel);
        fractionSourcePanel_panel.setLayout(fractionSourcePanel_panelLayout);
        fractionSourcePanel_panelLayout.setHorizontalGroup(
            fractionSourcePanel_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(fractionSourcePanel_panelLayout.createSequentialGroup()
                .addContainerGap()
                .add(bulkMode_radioBut)
                .add(29, 29, 29)
                .add(manualMode_radioBut)
                .addContainerGap(80, Short.MAX_VALUE))
        );
        fractionSourcePanel_panelLayout.setVerticalGroup(
            fractionSourcePanel_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(fractionSourcePanel_panelLayout.createSequentialGroup()
                .add(fractionSourcePanel_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(bulkMode_radioBut)
                    .add(manualMode_radioBut))
                .addContainerGap(8, Short.MAX_VALUE))
        );

        jPanel1.add(fractionSourcePanel_panel, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 150, 464, -1));
        jPanel1.add(standardMineralNameChooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(298, 329, 174, -1));

        chooseStandardMineral_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chooseStandardMineral_label.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        chooseStandardMineral_label.setText("  Specify standard mineral for all fractions:");
        jPanel1.add(chooseStandardMineral_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(46, 335, -1, -1));

        chooseAnalysisPurpose_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chooseAnalysisPurpose_label.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        chooseAnalysisPurpose_label.setText("  Specify analysis purpose for this sample:");
        jPanel1.add(chooseAnalysisPurpose_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 302, -1, -1));
        jPanel1.add(analysisPurposeChooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(298, 296, 174, -1));

        chooseTWrho_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chooseTWrho_label.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        chooseTWrho_label.setText("Set source of Terra-Wasserberg rho:");
        jPanel1.add(chooseTWrho_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(76, 368, -1, -1));

        TWsource.add(TWZeroRho_radioBut);
        TWZeroRho_radioBut.setText("Zero");
        jPanel1.add(TWZeroRho_radioBut, new org.netbeans.lib.awtextra.AbsoluteConstraints(302, 362, -1, -1));

        TWsource.add(TWCalculateRho_radioBut);
        TWCalculateRho_radioBut.setSelected(true);
        TWCalculateRho_radioBut.setText("Calculated");
        jPanel1.add(TWCalculateRho_radioBut, new org.netbeans.lib.awtextra.AbsoluteConstraints(374, 362, -1, -1));

        sampleIGSN_label1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        sampleIGSN_label1.setText("Registry:");
        jPanel1.add(sampleIGSN_label1, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 42, -1, -1));

        sampleRegistryChooser.setBackground(new java.awt.Color(245, 236, 206));
        jPanel1.add(sampleRegistryChooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(65, 36, 150, -1));

        validSampleIGSN_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        validSampleIGSN_label.setForeground(new java.awt.Color(204, 51, 0));
        validSampleIGSN_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        validSampleIGSN_label.setText("Sample IGSN is Valid at registry.");
        jPanel1.add(validSampleIGSN_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(238, 67, 220, -1));

        validateIGSN.setForeground(new java.awt.Color(255, 51, 0));
        validateIGSN.setText("Verify Sample ID");
        validateIGSN.setMargin(new java.awt.Insets(0, 0, 0, 0));
        validateIGSN.setPreferredSize(new java.awt.Dimension(110, 23));
        validateIGSN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                validateIGSNActionPerformed(evt);
            }
        });
        jPanel1.add(validateIGSN, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 85, 230, 25));

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        close.setForeground(new java.awt.Color(255, 51, 0));
        close.setText("Cancel");
        close.setMargin(new java.awt.Insets(0, 0, 0, 0));
        close.setPreferredSize(new java.awt.Dimension(110, 23));
        close.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeActionPerformed(evt);
            }
        });

        saveAndClose.setForeground(new java.awt.Color(255, 51, 0));
        saveAndClose.setText("OK");
        saveAndClose.setMargin(new java.awt.Insets(0, 0, 0, 0));
        saveAndClose.setPreferredSize(new java.awt.Dimension(110, 23));
        saveAndClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAndCloseActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(saveAndClose, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 168, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(105, 105, 105)
                .add(close, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 168, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(saveAndClose, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(close, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        sampleType_panel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        sampleType_label.setBackground(new java.awt.Color(255, 204, 102));
        sampleType_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        sampleType_label.setText("   L E G A C Y   M O D E  for a Sample");
        sampleType_label.setOpaque(true);

        org.jdesktop.layout.GroupLayout sampleType_panelLayout = new org.jdesktop.layout.GroupLayout(sampleType_panel);
        sampleType_panel.setLayout(sampleType_panelLayout);
        sampleType_panelLayout.setHorizontalGroup(
            sampleType_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(sampleType_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 477, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        sampleType_panelLayout.setVerticalGroup(
            sampleType_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(sampleType_label)
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(sampleType_panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(sampleType_panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void closeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeActionPerformed
        close();
    }//GEN-LAST:event_closeActionPerformed

    private void saveAndCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAndCloseActionPerformed
        try {
            saveSampleData();
            close();
        } catch (ETException ex) {
            ex.printStackTrace();
            new ETWarningDialog(ex).setVisible(true);
        }

    }//GEN-LAST:event_saveAndCloseActionPerformed

    private void sampleIGSN_textFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sampleIGSN_textFocusLost
        validateSampleID();
    }//GEN-LAST:event_sampleIGSN_textFocusLost

    private void validateIGSNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_validateIGSNActionPerformed
        validateSampleID();
    }//GEN-LAST:event_validateIGSNActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton TWCalculateRho_radioBut;
    private javax.swing.JRadioButton TWZeroRho_radioBut;
    private javax.swing.ButtonGroup TWsource;
    private javax.swing.JLabel aliquotName_label;
    private javax.swing.JTextField aliquotName_text;
    private javax.swing.JComboBox<String> analysisPurposeChooser;
    private javax.swing.JRadioButton bulkMode_radioBut;
    private javax.swing.JLabel chooseAnalysisPurpose_label;
    private javax.swing.JLabel chooseStandardMineral_label;
    private javax.swing.JLabel chooseTWrho_label;
    private javax.swing.JButton close;
    private javax.swing.JLabel defaultHeader_label;
    private javax.swing.ButtonGroup destinationOfFractionsOptions_buttonGroup;
    private javax.swing.JPanel fractionDestinationPanel_panel;
    private javax.swing.JPanel fractionSourcePanel_panel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JRadioButton manualMode_radioBut;
    private javax.swing.ButtonGroup modeChooser_buttonGroup;
    private javax.swing.JComboBox<String> physicalConstantsModelChooser;
    private javax.swing.JLabel sampleIGSN_label;
    private javax.swing.JLabel sampleIGSN_label1;
    private javax.swing.JTextField sampleIGSN_text;
    private javax.swing.JLabel sampleName_label;
    private javax.swing.JTextField sampleName_text;
    private javax.swing.JLabel sampleNotes_label;
    private javax.swing.JScrollPane sampleNotes_scrollPane;
    private javax.swing.JTextArea sampleNotes_textArea;
    private javax.swing.JLabel sampleReduxFileName_label;
    private javax.swing.JLabel sampleReduxFile_label;
    private javax.swing.JComboBox<SampleRegistries> sampleRegistryChooser;
    private javax.swing.JLabel sampleType_label;
    private javax.swing.JPanel sampleType_panel;
    private javax.swing.JButton saveAndClose;
    private javax.swing.ButtonGroup sourceOfFractionsOptions_buttonGroup;
    private javax.swing.JComboBox<String> standardMineralNameChooser;
    private javax.swing.ButtonGroup updateMode_buttonGroup;
    private javax.swing.JLabel validSampleIGSN_label;
    private javax.swing.JButton validateIGSN;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the converter
     */
    public AbstractSampleImporterFromLegacyCSVFile getConverter() {
        return converter;
    }

    /**
     * @param converter the converter to set
     */
    public void setConverter(AbstractSampleImporterFromLegacyCSVFile converter) {
        this.converter = converter;
    }
}
