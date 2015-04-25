/*
 * UPbLegacyFractionEditorDialog.java
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
package org.earthtime.UPb_Redux.dialogs.fractionManagers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import javax.swing.JComboBox;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.aliquots.Aliquot;
import org.earthtime.UPb_Redux.aliquots.UPbReduxAliquot;
import org.earthtime.UPb_Redux.dialogs.DialogEditor;
import org.earthtime.UPb_Redux.fractions.Fraction;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFraction;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFractionI;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.fractionReduction.ReductionHandler;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.fractionReduction.UncertaintyZoomLayer;
import org.earthtime.dataDictionaries.AnalysisMeasures;
import org.earthtime.exceptions.ETException;
import org.earthtime.exceptions.ETWarningDialog;

/**
 *
 * @author  James F. Bowring
 */
public class UPbLegacyFractionEditorDialog extends DialogEditor {


    private static JPanel concordiaGraphPanel;

    private Aliquot aliquot;

    private Fraction myFraction;

    private ArrayList<String> fractionIDs;

    private boolean analyzed;

    private JLayeredPane uncertaintyGraphPanel;


    /**
     * In Order reference to dates: 206/238, 207/235, 207/206
     */
    private boolean[] showDates;

    private String uncertaintyModeForDates;

//    private String correctionModeForDates;
//
//    private SampleUpdateAliquotFromFolderI sampleAliquotUpdater;

//    private boolean inAutoUraniumMode;
    /**
     * Creates new form FractionEditorDialog
     * @param parent 
     * @param modal 
     * @param selectedTab
     * @param aliquot
     * @param fraction 
     * @param analyzed  
     */
    public UPbLegacyFractionEditorDialog(
            java.awt.Frame parent,
            boolean modal,
            Aliquot aliquot,
            Fraction fraction,
            int selectedTab,
            boolean analyzed) {

        super(parent, modal);

        if (fraction == null) {
            return;
        }

//        this.sampleAliquotUpdater = sampleAliquotUpdater;

        setAnalyzed(analyzed);

        this.aliquot = aliquot;

        myFraction = fraction;

        initComponents();
        setDefaultLookAndFeelDecorated(true);
        setSize();

        // setup info panels
        aliquotInfo_panel.setBackground(ReduxConstants.myAliquotGrayColor);
        aliquotName_label.setText("Aliquot = " + aliquot.getAliquotName());

        fractionPanel.setBackground(ReduxConstants.myFractionGreenColor);

        fractionPanel.setBackground(ReduxConstants.myFractionGreenColor);
        buttonsPanel.setBackground(ReduxConstants.myFractionGreenColor);

        // populate combobox for fractions -- added march 2009 to allow navigation from within this form
        fraction_Chooser.removeAllItems();
        // add selected fraction if it is rejected as the user clicked it
        if (((UPbFractionI) fraction).isRejected()) {
            fraction_Chooser.addItem(fraction);
        }
        // add the not-rejected fractions
        for (Fraction f : ((UPbReduxAliquot) aliquot).getAliquotFractions()) {
            if (!((UPbFractionI) f).isRejected()) {
                fraction_Chooser.addItem(f);
            }
        }

        fraction_Chooser.setSelectedItem(myFraction);
        fraction_Chooser.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox) e.getSource();

                myFraction = (Fraction) cb.getSelectedItem();
                InitializeFractionData();
            }
        });

        // first create a list of used fractionids so that we
        // can tell user if edited fraction name is already in use
        fractionIDs = new ArrayList<String>();
        for (Fraction f : ((UPbReduxAliquot) aliquot).getAliquotFractions()) {
            fractionIDs.add(f.getFractionID());
        }


        InitializeFractionData();

        // display selectedTab
//        details_Pane.setSelectedIndex(selectedTab);
    }

    private void InitializeFractionData() {

        // setup buttons
        saveAndClose_button.setEnabled(false);
        save_button.setEnabled(false);

        // if this is an added default fraction, the isdeleted field is true and
        // hence we do not need the button active
        delete_button.setEnabled(!((UPbFractionI) myFraction).isDeleted());
        restore_button.setEnabled(false);

    }

    /**
     * 
     */
    public void setSize() {
        setSize(1125, 754);
    }

    private void reduceFraction(boolean isAnalyzed) {
        ((UPbFraction)myFraction).reduceData(true);
    }

    /**
     * 
     * @return
     */
    public boolean isAnalyzed() {
        return analyzed;
    }

    /**
     * 
     * @param analyzed
     */
    public void setAnalyzed(boolean analyzed) {
        this.analyzed = analyzed;
    }

    /**
     * 
     * @return
     */
    public JLayeredPane getUncertaintyGraphPanel() {
        return uncertaintyGraphPanel;
    }

    /**
     * 
     * @param uncertaintyGraphPanel
     */
    public void setUncertaintyGraphPanel(JLayeredPane uncertaintyGraphPanel) {
        this.uncertaintyGraphPanel = uncertaintyGraphPanel;
    }

    /**
     * @return the concordiaGraphPanel
     */
    public static JPanel getConcordiaGraphPanel() {
        return concordiaGraphPanel;
    }

    /**
     * @param concordiaGraphPanel the concordiaGraphPanel to set
     */
    public static void setConcordiaGraphPanel(JPanel concordiaGraphPanel) {
        UPbLegacyFractionEditorDialog.concordiaGraphPanel = concordiaGraphPanel;
    }

    /**
     * @return the showDates
     */
    public boolean[] getShowDates() {
        return showDates;
    }

    /**
     * @param showDates the showDates to set
     */
    public void setShowDates(boolean[] showDates) {
        this.showDates = showDates;
    }


    private UncertaintyZoomLayer GenerateUncertaintyZoomLayer(String dateName, String mode) {
        ReductionHandler rh = ((UPbFraction) myFraction).getReductionHandler();
        return //
                new UncertaintyZoomLayer(//
                dateName,
                rh.chooseCovarianceMatrixModelByMode(uncertaintyModeForDates),
                //((CovarianceMatrixModel) rh.getMatrices()[0]).getAnalyticalCovarianceMatrix(),
                rh.extractSensitivityVectorForDate(dateName, mode));
    }



private void InitializeTextBoxes(
            final boolean editableU,
            final boolean editablePb,
            final boolean editableOxide) {

        // set properties of text boxes

        // top panel
        fractionID_text.setDocument(new UnDoAbleDocument(fractionID_text, (editablePb && editableU)));

        fractionMass_text.setDocument(new BigDecimalDocument(fractionMass_text, !isAnalyzed()));
    }

    private void ShowSavedData() {

        // header panel
        fractionID_text.setText(myFraction.getFractionID());

        fractionMass_text.setText(myFraction.getAnalysisMeasure(AnalysisMeasures.fractionMass.getName()).//
                getValue().setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE, RoundingMode.HALF_UP).toPlainString());

    }


    private void Save()
            throws ETException {
        // check to see if fractionid is in use
        if (!myFraction.getFractionID().equals(fractionID_text.getText())) {
            if (fractionIDs.contains(fractionID_text.getText())) {
                throw new ETException(null, "FractionID duplicates another.");
            }
        }

        // renamed
        fractionIDs.add(fractionID_text.getText());
        fractionIDs.remove(myFraction.getFractionID());

        myFraction.setFractionID(fractionID_text.getText());
        //TODO: handle grainID

        try {
            myFraction.getAnalysisMeasure(AnalysisMeasures.fractionMass.getName()).
                    setValue(new BigDecimal(fractionMass_text.getText()));


        } catch (NumberFormatException ex) {
            throw new ETException(
                    this,
                    "Blank numeric fields not allowed ... please enter a value.");

        }

        ((UPbFraction) myFraction).setChanged(true);
        // the next line undoes the default deleted status of a new default fraction
        ((UPbFraction) myFraction).setDeleted(false);


        // enable delete and restore button if off because of new default fraction
        delete_button.setEnabled(((UPbFractionI) myFraction).isChanged());
        restore_button.setEnabled(((UPbFractionI) myFraction).isChanged());

        ((UPbFraction) myFraction).initializeReductionHandler();
        reduceFraction(isAnalyzed());

    }


////    private boolean ExportUPbFractionPerInputSchema() {
////
////        boolean retval = true;
////        File selectedFile = null;
////
////        int response = JOptionPane.showConfirmDialog(this,
////                new String[]{"Save data and continue with export ?"},
////                "ET Redux Warning",
////                JOptionPane.YES_NO_OPTION,
////                JOptionPane.WARNING_MESSAGE);
////        if (response == JOptionPane.NO_OPTION) {
////            retval = false;
////        } else {
////            try {
////                Save();
////            } catch (ETException ex) {
////                ex.printStackTrace();
////            }
////        }
////
////        if (retval) {
////            // proceed with export
////            // forces dialog to front
////            setAlwaysOnTop(false);
////
////            String dialogTitle = "Save this UPbFraction as xml: *.xml";
////            final String fileExtension = ".xml";
////            String fractionFileName =
////                    ((UPbFraction) getMyFraction()).getSampleName()//
////                    + "_" + getMyFraction().getFractionID()//
////                    + "_" + ((UPbFraction) getMyFraction()).getRatioType()//
////                    + fileExtension;
////
////            FileFilter nonMacFileFilter = new XMLFileFilter();
////
////
////            selectedFile = FileHelper.AllPlatformSaveAs(
////                    new Frame(), dialogTitle, null, fileExtension, fractionFileName, nonMacFileFilter);
////
////            setAlwaysOnTop(true);
////
////        }
////
////        if (selectedFile != null) {
////            try {
////                // export
////                ((UPbFraction) getMyFraction()).serializeXMLObject(selectedFile.getCanonicalPath());
////            } catch (IOException ex) {
////                ex.printStackTrace();
////            }
////        }
////
////        //  setVisible(true);
////        return (selectedFile != null);
////    }

    /**
     * 
     */
    @Override
    public void close() {

        super.close();
    }

    /**
     * 
     * @return
     */
    public Fraction getMyFraction() {
        return myFraction;
    }

    /**
     * 
     * @param myFraction
     */
    public void setMyFraction(Fraction myFraction) {
        this.myFraction = myFraction;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel3 = new javax.swing.JPanel();
        details_Pane = new javax.swing.JTabbedPane();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        jLabel1 = new javax.swing.JLabel();
        jLayeredPane2 = new javax.swing.JLayeredPane();
        jLayeredPane3 = new javax.swing.JLayeredPane();
        fractionPanel = new javax.swing.JPanel();
        fractionMass_text = new javax.swing.JTextField();
        fractionID_text = new javax.swing.JTextField();
        fractionMassInGrams_label = new javax.swing.JLabel();
        fraction_Chooser = new javax.swing.JComboBox<Fraction>();
        fractionID_label1 = new javax.swing.JLabel();
        fractionID_label = new javax.swing.JLabel();
        buttonsPanel = new javax.swing.JPanel();
        saveAndClose_button = new javax.swing.JButton();
        restore_button = new javax.swing.JButton();
        delete_button = new javax.swing.JButton();
        close_button = new javax.swing.JButton();
        save_button = new javax.swing.JButton();
        aliquotInfo_panel = new javax.swing.JPanel();
        aliquotName_label = new javax.swing.JLabel();

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 100, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setLocationByPlatform(true);
        setModal(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                UPbFractionEditorClosing(evt);
            }
        });

        details_Pane.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        details_Pane.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        details_Pane.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        details_Pane.setMaximumSize(new java.awt.Dimension(640, 680));
        details_Pane.setMinimumSize(new java.awt.Dimension(0, 0));
        details_Pane.setPreferredSize(new java.awt.Dimension(0, 0));
        details_Pane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ChangedTabs(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Arial Black", 0, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(204, 0, 51));
        jLabel1.setText("Stay Tuned as we engineer the legacy mode ...");
        jLayeredPane1.add(jLabel1);
        jLabel1.setBounds(160, 110, 680, 80);

        details_Pane.addTab("Isotopic Ratios", jLayeredPane1);
        details_Pane.addTab("Composition", jLayeredPane2);
        details_Pane.addTab("Isotopic Dates", jLayeredPane3);

        fractionPanel.setBackground(new java.awt.Color(212, 231, 232));
        fractionPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        fractionMass_text.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        fractionMass_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        fractionMass_text.setText("0.0");

        fractionID_text.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        fractionID_text.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        fractionID_text.setText("Fraction ID");

        fractionMassInGrams_label.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        fractionMassInGrams_label.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        fractionMassInGrams_label.setText("fraction mass in g:");

        fractionID_label1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        fractionID_label1.setText("Choose Fraction:");

        fractionID_label.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        fractionID_label.setText("Fraction ID:");

        org.jdesktop.layout.GroupLayout fractionPanelLayout = new org.jdesktop.layout.GroupLayout(fractionPanel);
        fractionPanel.setLayout(fractionPanelLayout);
        fractionPanelLayout.setHorizontalGroup(
            fractionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(fractionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(fractionID_label1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(fraction_Chooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 158, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 259, Short.MAX_VALUE)
                .add(fractionID_label)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(fractionID_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 111, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(105, 105, 105)
                .add(fractionMassInGrams_label)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(fractionMass_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 96, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        fractionPanelLayout.setVerticalGroup(
            fractionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(fractionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(fractionID_label1)
                .add(fraction_Chooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(fractionMass_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(fractionMassInGrams_label)
                .add(fractionID_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(fractionID_label))
        );

        buttonsPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        saveAndClose_button.setForeground(new java.awt.Color(255, 51, 0));
        saveAndClose_button.setText("Save & Close");
        saveAndClose_button.setPreferredSize(new java.awt.Dimension(140, 23));
        saveAndClose_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAndClose_buttonActionPerformed(evt);
            }
        });

        restore_button.setForeground(new java.awt.Color(255, 51, 0));
        restore_button.setText("Restore");
        restore_button.setPreferredSize(new java.awt.Dimension(140, 23));
        restore_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                restore_buttonActionPerformed(evt);
            }
        });

        delete_button.setForeground(new java.awt.Color(255, 51, 0));
        delete_button.setText("Delete");
        delete_button.setPreferredSize(new java.awt.Dimension(140, 23));
        delete_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delete_buttonActionPerformed(evt);
            }
        });

        close_button.setForeground(new java.awt.Color(255, 51, 0));
        close_button.setText("Close");
        close_button.setPreferredSize(new java.awt.Dimension(140, 23));
        close_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                close_buttonActionPerformed(evt);
            }
        });

        save_button.setForeground(new java.awt.Color(255, 51, 0));
        save_button.setText("Save");
        save_button.setPreferredSize(new java.awt.Dimension(140, 23));
        save_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                save_buttonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout buttonsPanelLayout = new org.jdesktop.layout.GroupLayout(buttonsPanel);
        buttonsPanel.setLayout(buttonsPanelLayout);
        buttonsPanelLayout.setHorizontalGroup(
            buttonsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(buttonsPanelLayout.createSequentialGroup()
                .add(137, 137, 137)
                .add(saveAndClose_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 190, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(save_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 119, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(close_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 115, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(restore_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 115, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(delete_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 115, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(236, Short.MAX_VALUE))
        );
        buttonsPanelLayout.setVerticalGroup(
            buttonsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(buttonsPanelLayout.createSequentialGroup()
                .add(buttonsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(delete_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(restore_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(close_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(save_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(saveAndClose_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        aliquotInfo_panel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        aliquotName_label.setText("jLabel1");

        org.jdesktop.layout.GroupLayout aliquotInfo_panelLayout = new org.jdesktop.layout.GroupLayout(aliquotInfo_panel);
        aliquotInfo_panel.setLayout(aliquotInfo_panelLayout);
        aliquotInfo_panelLayout.setHorizontalGroup(
            aliquotInfo_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(aliquotInfo_panelLayout.createSequentialGroup()
                .add(aliquotName_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 432, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(613, Short.MAX_VALUE))
        );
        aliquotInfo_panelLayout.setVerticalGroup(
            aliquotInfo_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(aliquotInfo_panelLayout.createSequentialGroup()
                .add(aliquotName_label)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(buttonsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(aliquotInfo_panel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(fractionPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(details_Pane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1041, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(aliquotInfo_panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 22, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(fractionPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(details_Pane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 622, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(buttonsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 31, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void close_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_close_buttonActionPerformed
        close();
    }//GEN-LAST:event_close_buttonActionPerformed

    private void restore_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_restore_buttonActionPerformed
        // recovers data pre-autouranium
        ShowSavedData();
    }//GEN-LAST:event_restore_buttonActionPerformed

    private void saveAndClose_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAndClose_buttonActionPerformed
        try {
            Save();
            close();
        } catch (ETException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
}//GEN-LAST:event_saveAndClose_buttonActionPerformed

    private void delete_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delete_buttonActionPerformed
        // check if they mean it ***************
        // Modal dialog with OK/cancel and a text field
        int response = JOptionPane.showConfirmDialog(
                this,
                new String[]{"Are you sure you want to delete this fraction?"},
                "ET Redux Warning",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.WARNING_MESSAGE);
        switch (response) {
            case JOptionPane.YES_OPTION:
                ((UPbFractionI) myFraction).setChanged(true);
                ((UPbFractionI) myFraction).setDeleted(true);
            case JOptionPane.CANCEL_OPTION:
                close();
            case JOptionPane.NO_OPTION:
                break;
            case JOptionPane.CLOSED_OPTION:
                break;
        }

    }//GEN-LAST:event_delete_buttonActionPerformed

private void UPbFractionEditorClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_UPbFractionEditorClosing
}//GEN-LAST:event_UPbFractionEditorClosing

private void save_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_save_buttonActionPerformed
    try {
        Save();
        InitializeFractionData();

    //  Save();
    // InitializeKwikiTab();
    } catch (ETException ex) {
        new ETWarningDialog(ex).setVisible(true);
    }
}//GEN-LAST:event_save_buttonActionPerformed

private void ChangedTabs(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ChangedTabs

}//GEN-LAST:event_ChangedTabs

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel aliquotInfo_panel;
    private javax.swing.JLabel aliquotName_label;
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JButton close_button;
    private javax.swing.JButton delete_button;
    private javax.swing.JTabbedPane details_Pane;
    private javax.swing.JLabel fractionID_label;
    private javax.swing.JLabel fractionID_label1;
    private javax.swing.JTextField fractionID_text;
    private javax.swing.JLabel fractionMassInGrams_label;
    private javax.swing.JTextField fractionMass_text;
    private javax.swing.JPanel fractionPanel;
    private javax.swing.JComboBox<Fraction> fraction_Chooser;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JLayeredPane jLayeredPane2;
    private javax.swing.JLayeredPane jLayeredPane3;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JButton restore_button;
    private javax.swing.JButton saveAndClose_button;
    private javax.swing.JButton save_button;
    // End of variables declaration//GEN-END:variables
}
