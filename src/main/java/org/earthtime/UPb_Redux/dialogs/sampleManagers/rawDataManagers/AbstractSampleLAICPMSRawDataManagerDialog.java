/*
 * AbstractSampleLAICPMSRawDataManagerDialog.java
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
package org.earthtime.UPb_Redux.dialogs.sampleManagers.rawDataManagers;

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
import org.earthtime.UPb_Redux.aliquots.Aliquot;
import org.earthtime.UPb_Redux.aliquots.UPbReduxAliquot;
import org.earthtime.UPb_Redux.dialogs.DialogEditor;
import org.earthtime.UPb_Redux.exceptions.BadImportedCSVLegacyFileException;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.UPb_Redux.fractions.Fraction;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFractionI;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbLegacyFraction;
import org.earthtime.UPb_Redux.samples.Sample;
import org.earthtime.UPb_Redux.samples.sampleImporters.AbstractSampleImporterFromLegacyCSVFile;
import org.earthtime.dataDictionaries.MineralTypes;
import org.earthtime.dataDictionaries.SampleRegistries;
import org.earthtime.exceptions.ETException;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;

/**
 *
 * @author  James F. Bowring
 */
public abstract class AbstractSampleLAICPMSRawDataManagerDialog extends DialogEditor {

    private Sample mySample = null;
    private File importFractionFolderMRU;
    private boolean initialized = false;
    private boolean newSample = false;
    private AbstractSampleImporterFromLegacyCSVFile converter;

    /**
     * Creates new form AbstractSampleLegacyManagerDialog
     * @param parent 
     * @param importFractionFolderMRU
     * @param modal
     * @param sample 
     * @param dataTypeTitle
     * @param converter  
     */
    public AbstractSampleLAICPMSRawDataManagerDialog (
            java.awt.Frame parent,
            boolean modal,
            String dataTypeTitle,
            Sample sample,
            AbstractSampleImporterFromLegacyCSVFile converter,
            File importFractionFolderMRU ) {
        super( parent, modal );

        this.importFractionFolderMRU = importFractionFolderMRU;

        initComponents();

        this.mySample = sample;

        initSampleFields();

        if ( sample.getUPbFractions().size() > 0 ) {
            // we are in edit mode 
            fractionDestinationPanel_panel.setVisible( false );
            fractionSourcePanel_panel.setVisible( false );
        }

        sampleType_label.setText( dataTypeTitle + sampleType_label.getText() );

        this.converter = converter;

    }

    /**
     * 
     */
    public void setSize () {
        setSize( 480, 670 );
    }

    /**
     * 
     * @return
     */
    public File getImportFractionFolderMRU () {
        return importFractionFolderMRU;
    }

    /**
     * 
     * @param importFractionFolderMRU
     */
    public void setImportFractionFolderMRU ( File importFractionFolderMRU ) {
        this.importFractionFolderMRU = importFractionFolderMRU;
    }

    private void validateSampleID () {
        if (  ! mySample.isArchivedInRegistry() ) {
            boolean valid = SampleRegistries.isSampleIdentifierValidAtRegistry(//
                    mySample.getSampleIGSN() );
            validSampleID_label.setText( (String) (valid ? "Sample ID is Valid at registry." : "Sample ID is NOT valid at registry.") );
            mySample.setValidatedSampleIGSN( valid );
        }
    }

    private void initSampleFields () {
        // init input fields

        sampleName_text.setDocument(
                new UnDoAbleDocument( sampleName_text,  ! mySample.isArchivedInRegistry() ) );
        sampleName_text.setText( getMySample().getSampleName() );

        sampleIGSN_text.setDocument(
                new UnDoAbleDocument( sampleIGSN_text,  ! mySample.isArchivedInRegistry() ) );
        sampleIGSN_text.setText( getMySample().getSampleIGSNnoRegistry() );


        for (SampleRegistries sr : SampleRegistries.values()) {
            sampleRegistryChooser.addItem( sr );
        }
        sampleRegistryChooser.setEnabled(  ! mySample.isArchivedInRegistry() );
        sampleRegistryChooser.setSelectedItem( mySample.getSampleRegistry() );
        sampleRegistryChooser.addActionListener( new ActionListener() {

            @Override
            public void actionPerformed ( ActionEvent e ) {
                mySample.setSampleIGSN( ((SampleRegistries) sampleRegistryChooser.getSelectedItem()).getCode() + "." + sampleIGSN_text.getText() );
                validateSampleID();
            }
        } );

        // april 2011
        validateSampleID();

        sampleNotes_textArea.setDocument( new UnDoAbleDocument( sampleNotes_textArea, true ) );
        sampleNotes_textArea.setText( getMySample().getSampleAnnotations() );

        // init display fields - html allows multi-line
        sampleReduxFileName_label.setText(
                "<html><p>" + getMySample().getReduxSampleFilePath() + "</p></html>" );
        sampleReduxFileName_label.setToolTipText( getMySample().getReduxSampleFilePath() );

        physicalConstantsModelChooser.removeAllItems();
        ArrayList<AbstractRatiosDataModel> physicalConstantsModels = getMySample().getMyReduxLabData().getPhysicalConstantsModels();
        for (int i = (physicalConstantsModels.size() > 1 ? 1 : 0); i < physicalConstantsModels.size(); i ++) {
            physicalConstantsModelChooser.addItem( physicalConstantsModels.get( i ).getNameAndVersion() );
        }

        physicalConstantsModelChooser.setSelectedIndex( 0 );
        try {
            physicalConstantsModelChooser.setSelectedItem( getMySample().getPhysicalConstantsModel().getNameAndVersion() );
        } catch (BadLabDataException ex) {
        }

        // set up StandardMineral chooser
        standardMineralNameChooser.removeAllItems();
        for (int i = 0; i < MineralTypes.values().length; i ++) {
            standardMineralNameChooser.addItem( MineralTypes.values()[i].getName() );
        }

        standardMineralNameChooser.setSelectedItem( mySample.getMineralName() );
        standardMineralNameChooser.addItemListener( new mineralNameItemListener() );


        // set up analysisPurposeChooser
        analysisPurposeChooser.removeAllItems();
        for (ANALYSIS_PURPOSE ap : ANALYSIS_PURPOSE.values()) {
            analysisPurposeChooser.addItem( ap.toString() );
        }

        analysisPurposeChooser.setSelectedItem( mySample.getAnalysisPurpose().toString() );
        analysisPurposeChooser.addItemListener( new analysisPurposeItemListener() );

        if ( getMySample().isCalculateTWrhoForLegacyData() ) {
            TWCalculateRho_radioBut.setSelected( true );
        } else {
            TWZeroRho_radioBut.setSelected( true );
        }


    }

    class mineralNameItemListener implements ItemListener {
        // This method is called only if a new item has been selected.

        @Override
        public void itemStateChanged ( ItemEvent evt ) {

            if ( evt.getStateChange() == ItemEvent.SELECTED ) {
                // Item was just selected
                mySample.setMineralName( (String) evt.getItem() );

            } else if ( evt.getStateChange() == ItemEvent.DESELECTED ) {
                // Item is no longer selected
            }
        }
    }

    class analysisPurposeItemListener implements ItemListener {
        // This method is called only if a new item has been selected.

        public void itemStateChanged ( ItemEvent evt ) {

            if ( evt.getStateChange() == ItemEvent.SELECTED ) {
                // Item was just selected
                mySample.setAnalysisPurpose( ANALYSIS_PURPOSE.valueOf( (String) evt.getItem() ) );

            } else if ( evt.getStateChange() == ItemEvent.DESELECTED ) {
                // Item is no longer selected
            }
        }
    }

    private void saveSampleData ()
            throws ETException {
        // validate sample name
        if ( (sampleName_text.getText().trim().length() == 0)
                || (aliquotName_text.getText().trim().length() == 0) ) {
            return;
        }

        mySample.setSampleName( sampleName_text.getText().trim() );
        mySample.setSampleIGSN( ((SampleRegistries) sampleRegistryChooser.getSelectedItem()).getCode() + "." + sampleIGSN_text.getText().trim() );
        mySample.setSampleRegistry( (SampleRegistries) sampleRegistryChooser.getSelectedItem() );
        mySample.setSampleAnnotations( sampleNotes_textArea.getText() );

        String currentPhysicalConstantsModelName = "";
        try {
            currentPhysicalConstantsModelName = getMySample().getPhysicalConstantsModel().getNameAndVersion();

        } catch (BadLabDataException badLabDataException) {
        }
        if (  ! ((String) physicalConstantsModelChooser.getSelectedItem()).equalsIgnoreCase( currentPhysicalConstantsModelName ) ) {
            try {
                getMySample().setPhysicalConstantsModel(
                        getMySample().getMyReduxLabData().
                        getAPhysicalConstantsModel( ((String) physicalConstantsModelChooser.getSelectedItem()) ) );

            } catch (BadLabDataException badLabDataException) {
            }
        }

        // in legacy mode we only allow one aliquot
        if ( getMySample().getUPbFractions().isEmpty() ) {
            Aliquot myAliquot = getMySample().addNewAliquot( aliquotName_text.getText().trim() );
            // May 2010 allows publication of legacy results
            ((UPbReduxAliquot) myAliquot).setCompiled( false );
            int myAliquotNumber = ((UPbReduxAliquot) myAliquot).getAliquotNumber();

            // test for manual mode or bulk import from CSV file
            if ( manualMode_radioBut.isSelected() ) {
                try {
                    getMySample().addDefaultUPbLegacyFractionToAliquot( myAliquotNumber );
                    setInitialized( true );
                    getMySample().setChanged( true );
                } catch (BadLabDataException badLabDataException) {
                }
            } else {
                // bulk mode
                try {
                    converter.setMruFolder( importFractionFolderMRU );
                    getMySample().addUPbFractionVector( converter.readInFractions(), myAliquotNumber );
                    myAliquot.setAliquotName( converter.getAliquotName() );
                    setInitialized( true );
                    getMySample().setChanged( true );

                    setImportFractionFolderMRU( converter.getMruFolder() );
                } catch (FileNotFoundException fileNotFoundException) {
                } catch (BadImportedCSVLegacyFileException badImportedCSVLegacyFileException) {
                }
            }


        }

        if ( TWZeroRho_radioBut.isSelected() ) {
            getMySample().setCalculateTWrhoForLegacyData( false );
        } else {
            getMySample().setCalculateTWrhoForLegacyData( true );
        }

        // moved outside conditional oct 2010 and added MineralName, etc ;;June 2010 add physical constants model
        for (Fraction f : getMySample().getUPbFractions()) {
            try {
                ((UPbFractionI) f).setPhysicalConstantsModel( getMySample().getPhysicalConstantsModel() );

                f.setMineralName( mySample.getMineralName() );
                if ( mySample.getMineralName().equalsIgnoreCase( "zircon" ) ) {
                    f.setZircon( true );
                } else {
                    f.setZircon( false );
                }

                f.setIsLegacy( true );

                if ( TWZeroRho_radioBut.isSelected() ) {
                    // set all T-W to zero
                    ((UPbLegacyFraction) f).getRadiogenicIsotopeRatioByName( "rhoR207_206r__r238_206r" )//
                            .setValue( BigDecimal.ZERO );
                } else {
                    // calculate all T-W
                    ((UPbLegacyFraction) f).calculateTeraWasserburgRho();
                }

            } catch (BadLabDataException badLabDataException) {
            }
        }

        // there should be only one aliquot
        Vector<Aliquot> aliquots = mySample.getActiveAliquots();
        for (Aliquot a : aliquots) {
            a.setAnalysisPurpose( mySample.getAnalysisPurpose() );
        }


    }

    /**
     * 
     * @return
     */
    public Sample getMySample () {
        return mySample;
    }

    /**
     * 
     * @param mySample
     */
    public void setMySample ( Sample mySample ) {
        this.mySample = mySample;
    }

    /**
     * 
     * @return
     */
    public boolean isInitialized () {
        return initialized;
    }

    /**
     * 
     * @param isSaved
     */
    public void setInitialized ( boolean isSaved ) {
        this.initialized = isSaved;
    }

    /**
     * 
     * @return
     */
    public boolean isNewSample () {
        return newSample;
    }

    /**
     * 
     * @param newSample
     */
    public void setNewSample ( boolean newSample ) {
        this.newSample = newSample;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
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
        physicalConstantsModelChooser = new javax.swing.JComboBox<String>();
        defaultHeader_label = new javax.swing.JLabel();
        fractionSourcePanel_panel = new javax.swing.JPanel();
        manualMode_radioBut = new javax.swing.JRadioButton();
        bulkMode_radioBut = new javax.swing.JRadioButton();
        standardMineralNameChooser = new javax.swing.JComboBox<String>();
        chooseStandardMineral_label = new javax.swing.JLabel();
        chooseAnalysisPurpose_label = new javax.swing.JLabel();
        analysisPurposeChooser = new javax.swing.JComboBox<String>();
        chooseTWrho_label = new javax.swing.JLabel();
        TWZeroRho_radioBut = new javax.swing.JRadioButton();
        TWCalculateRho_radioBut = new javax.swing.JRadioButton();
        sampleIGSN_label1 = new javax.swing.JLabel();
        sampleRegistryChooser = new javax.swing.JComboBox<SampleRegistries>();
        validSampleID_label = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        close = new javax.swing.JButton();
        saveAndClose = new javax.swing.JButton();
        sampleType_panel = new javax.swing.JPanel();
        sampleType_label = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(245, 236, 206));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jPanel1.setMaximumSize(new java.awt.Dimension(480, 620));

        sampleName_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        sampleName_label.setText("Lab's Local Sample Name:");

        sampleName_text.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        sampleName_text.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        sampleName_text.setText("Sample Name");

        sampleIGSN_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        sampleIGSN_label.setText("Sample ID:");

        sampleIGSN_text.setEditable(false);
        sampleIGSN_text.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        sampleIGSN_text.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        sampleIGSN_text.setText("<none>");
        sampleIGSN_text.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                sampleIGSN_textFocusLost(evt);
            }
        });

        sampleReduxFile_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        sampleReduxFile_label.setText("File path for this Sample:");

        sampleReduxFileName_label.setText("<Not Saved>");
        sampleReduxFileName_label.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        sampleReduxFileName_label.setAutoscrolls(true);

        sampleNotes_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        sampleNotes_label.setText("Notes about this Sample:");

        sampleNotes_textArea.setColumns(20);
        sampleNotes_textArea.setRows(5);
        sampleNotes_textArea.setMaximumSize(new java.awt.Dimension(250, 80));
        sampleNotes_textArea.setPreferredSize(new java.awt.Dimension(250, 80));
        sampleNotes_scrollPane.setViewportView(sampleNotes_textArea);

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

        physicalConstantsModelChooser.setBackground(new java.awt.Color(245, 236, 206));

        defaultHeader_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        defaultHeader_label.setForeground(new java.awt.Color(204, 51, 0));
        defaultHeader_label.setText("Set Physical Constants Model for this Sample:");

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
                .addContainerGap(12, Short.MAX_VALUE))
        );

        chooseStandardMineral_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chooseStandardMineral_label.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        chooseStandardMineral_label.setText("  Specify standard mineral for all fractions:");

        chooseAnalysisPurpose_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chooseAnalysisPurpose_label.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        chooseAnalysisPurpose_label.setText("  Specify analysis purpose for this sample:");

        chooseTWrho_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chooseTWrho_label.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        chooseTWrho_label.setText("Set source of Terra-Wasserberg rho:");

        TWsource.add(TWZeroRho_radioBut);
        TWZeroRho_radioBut.setText("Zero");

        TWsource.add(TWCalculateRho_radioBut);
        TWCalculateRho_radioBut.setSelected(true);
        TWCalculateRho_radioBut.setText("Calculated");

        sampleIGSN_label1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        sampleIGSN_label1.setText("Registry:");

        sampleRegistryChooser.setBackground(new java.awt.Color(245, 236, 206));

        validSampleID_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        validSampleID_label.setForeground(new java.awt.Color(204, 51, 0));
        validSampleID_label.setText("Sample ID is Valid at registry.");

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap(39, Short.MAX_VALUE)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, chooseTWrho_label)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, chooseStandardMineral_label)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, chooseAnalysisPurpose_label))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 9, Short.MAX_VALUE)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, analysisPurposeChooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, standardMineralNameChooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                        .add(TWZeroRho_radioBut)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(TWCalculateRho_radioBut)))
                .addContainerGap())
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(fractionDestinationPanel_panel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(20, 20, 20))
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(defaultHeader_label)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(physicalConstantsModelChooser, 0, 182, Short.MAX_VALUE)
                .add(20, 20, 20))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(fractionSourcePanel_panel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(validSampleID_label)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(sampleIGSN_label1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(sampleRegistryChooser, 0, 150, Short.MAX_VALUE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(sampleIGSN_label)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(sampleIGSN_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 158, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(sampleName_label)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(sampleName_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 199, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(20, 20, 20))
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(sampleNotes_scrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE)
                .addContainerGap())
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(20, 20, 20)
                                .add(sampleReduxFileName_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 407, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(sampleReduxFile_label)))
                    .add(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(sampleNotes_label)))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(sampleName_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(sampleName_label))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(sampleRegistryChooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(sampleIGSN_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(sampleIGSN_label)
                    .add(sampleIGSN_label1))
                .add(4, 4, 4)
                .add(validSampleID_label)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(defaultHeader_label)
                    .add(physicalConstantsModelChooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(fractionSourcePanel_panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(fractionDestinationPanel_panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 61, Short.MAX_VALUE)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(chooseAnalysisPurpose_label)
                    .add(analysisPurposeChooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(chooseStandardMineral_label)
                    .add(standardMineralNameChooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(6, 6, 6)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(TWCalculateRho_radioBut)
                    .add(chooseTWrho_label)
                    .add(TWZeroRho_radioBut))
                .add(20, 20, 20)
                .add(sampleNotes_label)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(sampleNotes_scrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(sampleReduxFile_label)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(sampleReduxFileName_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 64, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

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
                .addContainerGap(18, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(saveAndClose, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 32, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(close, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 32, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        sampleType_panel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        sampleType_label.setBackground(new java.awt.Color(255, 204, 102));
        sampleType_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        sampleType_label.setText("   R A W   D A T A   M O D E  for a Sample");
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
//            ex.printStackTrace();
        }

    }//GEN-LAST:event_saveAndCloseActionPerformed

    private void sampleIGSN_textFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sampleIGSN_textFocusLost
        validateSampleID();
    }//GEN-LAST:event_sampleIGSN_textFocusLost
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
    private javax.swing.JLabel validSampleID_label;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the converter
     */
    public AbstractSampleImporterFromLegacyCSVFile getConverter () {
        return converter;
    }

    /**
     * @param converter the converter to set
     */
    public void setConverter ( AbstractSampleImporterFromLegacyCSVFile converter ) {
        this.converter = converter;
    }
}
