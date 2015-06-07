/*
 * AliquotEditorForLAICPMS.java
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
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;
import org.earthtime.ETReduxFrame;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.aliquots.UPbReduxAliquot;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.UPb_Redux.fractions.FractionI;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFraction;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFractionI;
import org.earthtime.UPb_Redux.reduxLabData.ReduxLabData;
import org.earthtime.UPb_Redux.renderers.EditFractionButton;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.aliquots.AliquotInterface;
import org.earthtime.dataDictionaries.MeasuredRatios;
import org.earthtime.dataDictionaries.RadDates;
import org.earthtime.exceptions.ETException;
import org.earthtime.exceptions.ETWarningDialog;
import org.earthtime.samples.SampleInterface;
import org.earthtime.xmlUtilities.XMLSerializationI;
import org.jdesktop.layout.GroupLayout.ParallelGroup;
import org.jdesktop.layout.GroupLayout.SequentialGroup;

/**
 *
 * @author James F. Bowring
 */
public class AliquotEditorForLAICPMS extends AliquotEditorDialog {

    // Instance variables
    // measured ratios
    private ArrayList<JComponent> fractionR206_204m_Text;
    // composition
    private ArrayList<JComponent> fractionConcU_Text;
    private ArrayList<JLabel> fractionConcUPPM;
    private ArrayList<JComponent> fractionRTh_Usample_Text;
    // Isotopic ratios
    private ArrayList<JComponent> fractionR206_238r_Text;
    private ArrayList<JComponent> fractionR206_238r2SigmaPct_Text;
    private ArrayList<JComponent> fractionR207_235r_Text;
    private ArrayList<JComponent> fractionR207_235r2SigmaPct_Text;
    private ArrayList<JComponent> fractionR207_206r_Text;
    private ArrayList<JComponent> fractionR207_206r2SigmaPct_Text;
    private ArrayList<JComponent> fractionRhoR206_238r__r207_235r_Text;
    // Isotopic Dates
    private ArrayList<JComponent> fractionDate206_238r_Text;
    private ArrayList<JComponent> fractionDate206_238r2SigmaAbs_Text;
    private ArrayList<JComponent> fractionDate207_235r_Text;
    private ArrayList<JComponent> fractionDate207_235r2SigmaAbs_Text;
    private ArrayList<JComponent> fractionDate207_206r_Text;
    private ArrayList<JComponent> fractionDate207_206r2SigmaAbs_Text;

    /**
     * Creates new form AliquotEditorDialog
     *
     * @param parent
     * @param modal
     * @param sample
     * @param aliquot
     */
    public AliquotEditorForLAICPMS(
            ETReduxFrame parent,
            boolean modal,
            SampleInterface sample,
            AliquotInterface aliquot) {
        super(parent, modal, sample, aliquot);

        saveAndClose_button.removeActionListener(saveAndClose_button.getActionListeners()[0]);
        saveAndClose_button.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAndClose_buttonActionPerformed(evt);
            }
        });

        save_button.removeActionListener(save_button.getActionListeners()[0]);
        save_button.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                save_buttonActionPerformed(evt);
            }
        });

        restore_button.removeActionListener(restore_button.getActionListeners()[0]);
        restore_button.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                restore_buttonActionPerformed(evt);
            }
        });

        exportXMLAliquot_button.removeActionListener(exportXMLAliquot_button.getActionListeners()[0]);
        exportXMLAliquot_button.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportXMLAliquot_buttonActionPerformed(evt);
            }
        });

        saveAndPreviewXMLAliquotAsHTML_button.removeActionListener(saveAndPreviewXMLAliquotAsHTML_button.getActionListeners()[0]);
        saveAndPreviewXMLAliquotAsHTML_button.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAndPreviewXMLAliquotAsHTML_buttonActionPerformed(evt);
            }
        });

        saveAndUploadAliquotToGeochron_button.removeActionListener(saveAndUploadAliquotToGeochron_button.getActionListeners()[0]);
        saveAndUploadAliquotToGeochron_button.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAndUploadAliquotToGeochron_buttonActionPerformed(evt);
            }
        });

    }

    /**
     *
     */
    @Override
    public void initAliquot() {

        fastEdits_panel.removeAll();
//        showSavedDataI();

        deletedFractions = new Vector<>();
        addedFractions = new Vector<>();

        fastEdits_panel.setBackground(ReduxConstants.myFractionGreenColor);

        fractionDeleteButtons = new ArrayList<>();

        fractionEditButtons = new ArrayList<>();

        // composition
        fractionConcU_Text = new ArrayList<>();
        fractionConcUPPM = new ArrayList<>();

        fractionRTh_Usample_Text = new ArrayList<>();

        // measured
        fractionR206_204m_Text = new ArrayList<>();

        fractionR206_238r_Text = new ArrayList<>();
        fractionR206_238r2SigmaPct_Text = new ArrayList<>();

        fractionR207_235r_Text = new ArrayList<>();
        fractionR207_235r2SigmaPct_Text = new ArrayList<>();

        fractionR207_206r_Text = new ArrayList<>();
        fractionR207_206r2SigmaPct_Text = new ArrayList<>();

        fractionRhoR206_238r__r207_235r_Text = new ArrayList<>();

        // Isotopic Dates
        fractionDate206_238r_Text = new ArrayList<>();
        fractionDate206_238r2SigmaAbs_Text = new ArrayList<>();

        fractionDate207_235r_Text = new ArrayList<>();
        fractionDate207_235r2SigmaAbs_Text = new ArrayList<>();

        fractionDate207_206r_Text = new ArrayList<>();
        fractionDate207_206r2SigmaAbs_Text = new ArrayList<>();

        // create master row for filling others ********************************
        // new fraction namer
//        masterNewFractionName =
//                new JTextField();
//        masterNewFractionName.setDocument( new UnDoAbleDocument( masterNewFractionName, true ) );
//        masterNewFractionName.setText( "New Fraction" );
//
//        // new fraction creator button
//        masterNewFractionNameAdder =
//                new EditFractionButton( "ADD", -1, true );
//        masterNewFractionNameAdder.setToolTipText( "Click to ADD new Fraction" );
//        masterNewFractionNameAdder.addActionListener( new ActionListener() {
//
//            public void actionPerformed ( ActionEvent arg0 ) {
//                Fraction addedFraction = null;
//
//                // check to see if fractionid is in use
//                // first create a list of used fractionids so that we
//                // can tell user if new fraction name is already in use
//                Vector<String> fractionIDs = new Vector<String>();
//
//                for (int f = 0;
//                        f
//                        < getSample().getFractions().size();
//                        f ++) {
//                    fractionIDs.add( getSample().getFractions().get( f ).getFractionID() );
//                }
//
//                // add pending new fractions
//                for (int f = 0; f
//                        < addedFractions.size(); f ++) {
//                    fractionIDs.add( addedFractions.get( f ).getFractionID() );
//                }
//
//                // remove pending deleted fractions
//                for (int f = 0; f
//                        < deletedFractions.size(); f ++) {
//                    fractionIDs.remove( deletedFractions.get( f ).getFractionID() );
//                }
//
//                Collections.sort( fractionIDs, new IntuitiveStringComparator<String>() );//String.CASE_INSENSITIVE_ORDER );
//                //Arrays.sort(fractionIDs, String.CASE_INSENSITIVE_ORDER);
//
//                int index = Collections.binarySearch( fractionIDs, masterNewFractionName.getText() );
//                if ( index >= 0 ) {
//                    JOptionPane.showMessageDialog(
//                            null,
//                            new String[]{"Duplicate Fraction ID, please use another."},
//                            "ET Redux Warning",
//                            JOptionPane.WARNING_MESSAGE );
//
//                } else {
//                    // prepare fields
//
//                    try {
//                        addedFraction = new UPbFraction( "NONE" );
//                        addedFraction.setSampleName( getSample().getSampleName() );
//                        ((UPbFraction) addedFraction).setAliquotNumber( getMyAliquot().getAliquotNumber() );
//                        addedFraction.setFractionID( masterNewFractionName.getText().trim() );
//                        addedFraction.setGrainID( addedFraction.getFractionID() );
//
//                        ReduxLabData labData = getMyAliquot().getMyReduxLabData();
//                        ((UPbFraction) addedFraction).setMyLabData( labData );
//                        ((UPbFraction) addedFraction)//
//                                .setAlphaPbModel( labData.getDefaultLabAlphaPbModel() );
//                        ((UPbFraction) addedFraction)//
//                                .setAlphaUModel( labData.getDefaultLabAlphaUModel() );
//                        ((UPbFraction) addedFraction)//
//                                .setPhysicalConstantsModel( getSample().getPhysicalConstantsModel() );
//
//                    } catch (BadLabDataException badLabDataException) {
//                    }
//
//                    addedFractions.add( addedFraction );
//                    addNewFractionRow( addedFraction );
//                }
//
//            }
//        } );
        // populate rows
        for (int row = 0; row
                < getMyAliquot().getAliquotFractions().size(); row++) {

            FractionI tempFrac = getMyAliquot().getAliquotFractions().get(row);
            int max = getMyAliquot().getAliquotFractions().size();
            addFractionRow(tempFrac, row, max);

        }

        // populate the components with fraction data
        showSavedDataII();
//
//        buildFastEditDisplayPanel();

    }

    /**
     *
     * @param tempFrac
     * @param row
     * @param max
     */
    protected void addFractionRow(FractionI tempFrac, int row, int max) {

        // Buttons to allow deletion of fractions
        JButton tempJB = new EditFractionButton("X", row, true);
        tempJB.setForeground(Color.red);
        tempJB.setToolTipText("Click to DELETE Fraction!");
        tempJB.setMargin(new Insets(0, 0, 0, 0));
        tempJB.addActionListener(new deleteFractionListener(tempFrac, row));
        //tempJB.setFont(new Font("SansSerif", Font.PLAIN, 10));
        fractionDeleteButtons.add(tempJB);
        modifyComponentKeyMapForTable(tempJB, fractionDeleteButtons, max);

        // Buttons to open fraction editor
        tempJB
                = new EditFractionButton("Kwiki", row, true);
//        tempJB.addActionListener( new editFractionListener( tempFrac, row ) );
        fractionEditButtons.add(tempJB);
        modifyComponentKeyMapForTable(tempJB, fractionEditButtons, max);

        // Composition
        insertTableTextField(fractionConcU_Text, max);
        // mass label
        JLabel tempJL = new JLabel("ppm");
        tempJL.setForeground(Color.RED);
        fractionConcUPPM.add(tempJL);

        // composition // measured
        insertTableTextField(fractionConcU_Text, max);
        insertTableTextField(fractionR206_204m_Text, max);
        insertTableTextField(fractionRTh_Usample_Text, max);

        // legacy isotopic ratios
        insertTableTextField(fractionR207_206r_Text, max);
        insertTableTextField(fractionR207_206r2SigmaPct_Text, max);

        insertTableTextField(fractionR207_235r_Text, max);
        insertTableTextField(fractionR207_235r2SigmaPct_Text, max);

        insertTableTextField(fractionR206_238r_Text, max);
        insertTableTextField(fractionR206_238r2SigmaPct_Text, max);

        insertTableTextField(fractionRhoR206_238r__r207_235r_Text, max);

        // Isotopic Dates
        insertTableTextField(fractionDate206_238r_Text, max);
        insertTableTextField(fractionDate206_238r2SigmaAbs_Text, max);

        insertTableTextField(fractionDate207_235r_Text, max);
        insertTableTextField(fractionDate207_235r2SigmaAbs_Text, max);

        insertTableTextField(fractionDate207_206r_Text, max);
        insertTableTextField(fractionDate207_206r2SigmaAbs_Text, max);

    }

    /**
     *
     */
    protected void buildFastEditDisplayPanel() {

        fastEdits_panel.removeAll();

        // build display
        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(fastEdits_panel);
        fastEdits_panel.setLayout(jPanel2Layout);

        ParallelGroup myHorizFraction = jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false);
        SequentialGroup myVerticalFraction = jPanel2Layout.createSequentialGroup();

        // create title row elements
        JLabel headDelete = new JLabel("DELETE");
        headDelete.setFont(redHeadFont);
        headDelete.setForeground(Color.RED);

        JLabel headFraction = new JLabel("EDIT Fraction");
        headFraction.setFont(redHeadFont);
        headFraction.setForeground(Color.RED);

        // Composition
        JLabel headConcU = new JLabel("conc U");
        headConcU.setFont(redHeadFont);
        headConcU.setForeground(Color.RED);

        JLabel headRTh_Usample = new JLabel("Th/U");
        headRTh_Usample.setFont(redHeadFont);
        headRTh_Usample.setForeground(Color.RED);

        // meaured ratios
        JLabel headR206_204m = new JLabel("206Pb/204Pb");
        headR206_204m.setFont(redHeadFont);
        headR206_204m.setForeground(Color.RED);

        // isotopic ratios
        JLabel headR207_206r = new JLabel("207Pb/206Pb");
        headR207_206r.setFont(redHeadFont);
        headR207_206r.setForeground(Color.RED);

        JLabel headR207_206r1SigmaPct = new JLabel("1-sigma%");
        headR207_206r1SigmaPct.setFont(redHeadFont);
        headR207_206r1SigmaPct.setForeground(Color.RED);

        JLabel headR207_235r = new JLabel("207Pb/235U");
        headR207_235r.setFont(redHeadFont);
        headR207_235r.setForeground(Color.RED);

        JLabel headR207_235r1SigmaPct = new JLabel("1-sigma%");
        headR207_235r1SigmaPct.setFont(redHeadFont);
        headR207_235r1SigmaPct.setForeground(Color.RED);

        JLabel headR206_238r = new JLabel("206Pb/238U");
        headR206_238r.setFont(redHeadFont);
        headR206_238r.setForeground(Color.RED);

        JLabel headR206_238r1SigmaPct = new JLabel("1-sigma%");
        headR206_238r1SigmaPct.setFont(redHeadFont);
        headR206_238r1SigmaPct.setForeground(Color.RED);

        JLabel headRhoR206_238r__r207_235r = new JLabel("rho 6/8-7/35");
        headRhoR206_238r__r207_235r.setFont(redHeadFont);
        headRhoR206_238r__r207_235r.setForeground(Color.RED);

        // Isotopic Dates
        JLabel headDate206_238r = new JLabel("206Pb/238U");
        headDate206_238r.setFont(redHeadFont);
        headDate206_238r.setForeground(Color.RED);

        JLabel headDate206_238r1SigmaAbs = new JLabel("1-sigma");
        headDate206_238r1SigmaAbs.setFont(redHeadFont);
        headDate206_238r1SigmaAbs.setForeground(Color.RED);

        JLabel headDate207_235r = new JLabel("207Pb/235U");
        headDate207_235r.setFont(redHeadFont);
        headDate207_235r.setForeground(Color.RED);

        JLabel headDate207_235r1SigmaAbs = new JLabel("1"
                + "-sigma");
        headDate207_235r1SigmaAbs.setFont(redHeadFont);
        headDate207_235r1SigmaAbs.setForeground(Color.RED);

        JLabel headDate207_206r = new JLabel("207Pb/206Pb");
        headDate207_206r.setFont(redHeadFont);
        headDate207_206r.setForeground(Color.RED);

        JLabel headDate207_206r1SigmaAbs = new JLabel("1-sigma");
        headDate207_206r1SigmaAbs.setFont(redHeadFont);
        headDate207_206r1SigmaAbs.setForeground(Color.RED);

        // build display *******************************************************
        JLabel headComposition = new JLabel("Composition               |");
        headComposition.setFont(new Font("Monospaced", Font.BOLD, 18));
        headComposition.setForeground(Color.RED);

        JLabel headIsotopicRatios = new JLabel("Isotopic Ratios                                        |");
        headIsotopicRatios.setFont(new Font("Monospaced", Font.BOLD, 18));
        headIsotopicRatios.setForeground(Color.RED);

        JLabel headIsotopicDates = new JLabel("Isotopic Dates Ma");
        headIsotopicDates.setFont(new Font("Monospaced", Font.BOLD, 18));
        headIsotopicDates.setForeground(Color.RED);

        // master fields
        myHorizFraction.add(jPanel2Layout.createSequentialGroup()//
                .add(50, 50, 50) // left margin
                //.add( masterNewFractionName, 110, 110, 110 )//
                .add(115, 115, 115)//
                .add(headComposition, 300, 300, 300)//
                .add(5, 5, 5)//
                .add(headIsotopicRatios, 650, 650, 650)//
                .add(5, 5, 5)//
                .add(headIsotopicDates, 400, 400, 400)//
                .add(5, 5, 5)//
        );

        myVerticalFraction.add(5, 5, 5) // top margin
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)//.BASELINE)//
                        //.add( masterNewFractionName, 22, 22, 22 )//
                        .add(headComposition, 22, 22, 22)//
                        .add(headIsotopicRatios, 22, 22, 22)//
                        .add(headIsotopicDates, 22, 22, 22)//
                );

        // fill buttons
        myHorizFraction.add(jPanel2Layout.createSequentialGroup()//
                .add(50, 50, 50) // left margin
                // .add( masterNewFractionNameAdder, 110, 110, 110 )//
                .add(115, 115, 115)//
        );

        myVerticalFraction.add(1, 1, 1) // top margin
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)//.BASELINE)//
                // .add( masterNewFractionNameAdder )//
                );

        myHorizFraction.add(jPanel2Layout.createSequentialGroup()//
                .add(8, 8, 8) // left margin
                .add(headDelete, 50, 50, 50)//
                .add(6, 6, 6)//
                .add(headFraction, 100, 100, 100)//
                .add(15, 15, 15)//
                .add(headConcU, 90, 90, 90)//
                .add(15, 15, 15)//
                .add(headR206_204m, 90, 90, 90)//
                .add(15, 15, 15)//
                .add(headRTh_Usample, 90, 90, 90)//
                .add(15, 15, 15)//
                .add(headR207_206r, 90, 90, 90)//
                .add(5, 5, 5)//
                .add(headR207_206r1SigmaPct, 60, 60, 60)//
                .add(15, 15, 15)//
                .add(headR207_235r, 90, 90, 90)//
                .add(5, 5, 5)//
                .add(headR207_235r1SigmaPct, 60, 60, 60)//
                .add(15, 15, 15)//
                .add(headR206_238r, 90, 90, 90)//
                .add(5, 5, 5)//
                .add(headR206_238r1SigmaPct, 60, 60, 60)//
                .add(15, 15, 15)//
                .add(headRhoR206_238r__r207_235r, 90, 90, 90)//
                .add(15, 15, 15)//
                .add(headDate206_238r, 90, 90, 90)//
                .add(5, 5, 5)//
                .add(headDate206_238r1SigmaAbs, 60, 60, 60)//
                .add(15, 15, 15)//
                .add(headDate207_235r, 90, 90, 90)//
                .add(5, 5, 5)//
                .add(headDate207_235r1SigmaAbs, 60, 60, 60)//
                .add(15, 15, 15)//
                .add(headDate207_206r, 90, 90, 90)//
                .add(5, 5, 5)//
                .add(headDate207_206r1SigmaAbs, 60, 60, 60)//
                .add(15, 15, 15)//
        );

        myVerticalFraction//
                .add(10, 10, 10) // top margin
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)//.BASELINE)//
                        .add(headDelete)//
                        .add(headFraction)//
                        .add(headConcU)//
                        .add(headR206_204m)//
                        .add(headRTh_Usample)//
                        .add(headR207_206r)//
                        .add(headR207_206r1SigmaPct)//
                        .add(headR207_235r)//
                        .add(headR207_235r1SigmaPct)//
                        .add(headR206_238r)//
                        .add(headR206_238r1SigmaPct)//
                        .add(headRhoR206_238r__r207_235r)//
                        .add(headDate206_238r)//
                        .add(headDate206_238r1SigmaAbs)//
                        .add(headDate207_235r)//
                        .add(headDate207_235r1SigmaAbs)//
                        .add(headDate207_206r)//
                        .add(headDate207_206r1SigmaAbs)//
                )//
                .add(2, 2, 2);

        // stop delete when only one fraction
        fractionDeleteButtons.get(0).setEnabled(fractionDeleteButtons.size() != 1);

        for (int f = 0; f
                < fractionDeleteButtons.size(); f++) {
            myHorizFraction.add(jPanel2Layout.createSequentialGroup()//
                    .add(4, 4, 4) // left-hand margin
                    .add(fractionDeleteButtons.get(f), 50, 50, 50) //
                    .add(3, 3, 3)//
                    .add(fractionEditButtons.get(f), 100, 100, 100) //
                    .add(5, 5, 5)//
                    .add(fractionConcU_Text.get(f), 60, 60, 60)//
                    .add(fractionConcUPPM.get(f))//
                    .add(10, 10, 10)//
                    .add(fractionR206_204m_Text.get(f), 100, 100, 100) //
                    .add(5, 5, 5)//
                    .add(fractionRTh_Usample_Text.get(f), 100, 100, 100) //
                    .add(10, 10, 10)//
                    .add(fractionR207_206r_Text.get(f), 100, 100, 100) //
                    .add(5, 5, 5)//
                    .add(fractionR207_206r2SigmaPct_Text.get(f), 60, 60, 60)//
                    .add(5, 5, 5)//
                    .add(fractionR207_235r_Text.get(f), 100, 100, 100) //
                    .add(5, 5, 5)//
                    .add(fractionR207_235r2SigmaPct_Text.get(f), 60, 60, 60)//
                    .add(5, 5, 5)//
                    .add(fractionR206_238r_Text.get(f), 100, 100, 100) //
                    .add(5, 5, 5)//
                    .add(fractionR206_238r2SigmaPct_Text.get(f), 60, 60, 60)//
                    .add(5, 5, 5)//
                    .add(fractionRhoR206_238r__r207_235r_Text.get(f), 100, 100, 100) //
                    .add(5, 5, 5)//
                    .add(fractionDate206_238r_Text.get(f), 100, 100, 100) //
                    .add(5, 5, 5)//
                    .add(fractionDate206_238r2SigmaAbs_Text.get(f), 60, 60, 60)//
                    .add(5, 5, 5)//
                    .add(fractionDate207_235r_Text.get(f), 100, 100, 100) //
                    .add(5, 5, 5)//
                    .add(fractionDate207_235r2SigmaAbs_Text.get(f), 60, 60, 60)//
                    .add(5, 5, 5)//
                    .add(fractionDate207_206r_Text.get(f), 100, 100, 100) //
                    .add(5, 5, 5)//
                    .add(fractionDate207_206r2SigmaAbs_Text.get(f), 60, 60, 60)//
                    .add(5, 5, 5)//
            );

            myVerticalFraction//
                    .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)//
                            .add(fractionDeleteButtons.get(f), 22, 22, 22)//
                            .add(fractionEditButtons.get(f), 22, 22, 22)//
                            .add(fractionConcU_Text.get(f), 22, 22, 22)//
                            .add(fractionConcUPPM.get(f))//
                            .add(fractionR206_204m_Text.get(f), 22, 22, 22)//
                            .add(fractionRTh_Usample_Text.get(f), 22, 22, 22)//
                            .add(fractionR206_238r_Text.get(f), 22, 22, 22)//
                            .add(fractionR206_238r2SigmaPct_Text.get(f), 22, 22, 22)//
                            .add(fractionR207_235r_Text.get(f), 22, 22, 22)//
                            .add(fractionR207_235r2SigmaPct_Text.get(f), 22, 22, 22)//
                            .add(fractionR207_206r_Text.get(f), 22, 22, 22)//
                            .add(fractionR207_206r2SigmaPct_Text.get(f), 22, 22, 22)//
                            .add(fractionRhoR206_238r__r207_235r_Text.get(f), 22, 22, 22)//
                            .add(fractionDate206_238r_Text.get(f), 22, 22, 22)//
                            .add(fractionDate206_238r2SigmaAbs_Text.get(f), 22, 22, 22)//
                            .add(fractionDate207_235r_Text.get(f), 22, 22, 22)//
                            .add(fractionDate207_235r2SigmaAbs_Text.get(f), 22, 22, 22)//
                            .add(fractionDate207_206r_Text.get(f), 22, 22, 22)//
                            .add(fractionDate207_206r2SigmaAbs_Text.get(f), 22, 22, 22)//
                    );
        }

        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jPanel2Layout.createSequentialGroup().add(myHorizFraction)));

        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(myVerticalFraction));

    }

    /**
     *
     */
    @Override
    protected void showSavedDataII() {

        showSavedDataI();

        // fraction details
//        for (int row = 0; row
//                < getMyAliquot().getAliquotFractions().size(); row ++) {
//            updateFractionRow( getMyAliquot().getAliquotFractions().get( row ), row );
//        }
        // PUBLISHING SECTION
        //sampleIGSN_text.setText( getSample().getSampleIGSN() );
    }

    /**
     *
     * @param tempFrac
     * @param row
     */
    protected void updateFractionRow(FractionI tempFrac, int row) {

        ((AbstractButton) fractionEditButtons.get(row)).setText(tempFrac.getFractionID());

        // Composition
        ((JTextComponent) fractionConcU_Text.get(row)).setText(tempFrac.getCompositionalMeasureByName("concU").getValue().
                movePointRight(6).setScale(ReduxConstants.DEFAULT_CONSTANTS_SCALE,
                        RoundingMode.HALF_UP).toPlainString());
        ((JTextComponent) fractionConcU_Text.get(row)).setCaretPosition(0);

        ((JTextComponent) fractionRTh_Usample_Text.get(row)).setText(tempFrac.getCompositionalMeasureByName("rTh_Usample").getValue().
                setScale(ReduxConstants.DEFAULT_CONSTANTS_SCALE,
                        RoundingMode.HALF_UP).toPlainString());
        ((JTextComponent) fractionRTh_Usample_Text.get(row)).setCaretPosition(0);

        // measured
        ((JTextComponent) fractionR206_204m_Text.get(row)).setText(tempFrac.getMeasuredRatioByName(MeasuredRatios.r206_204m.getName()).getValue().
                setScale(ReduxConstants.DEFAULT_CONSTANTS_SCALE,
                        RoundingMode.HALF_UP).toPlainString());
        ((JTextComponent) fractionR206_204m_Text.get(row)).setCaretPosition(0);

        // radiogenic isotopic ratios
        ((JTextComponent) fractionR206_238r_Text.get(row)).setText(tempFrac.getRadiogenicIsotopeRatioByName("r206_238r").getValue().
                setScale(ReduxConstants.DEFAULT_CONSTANTS_SCALE,
                        RoundingMode.HALF_UP).toPlainString());
        ((JTextComponent) fractionR206_238r_Text.get(row)).setCaretPosition(0);

        ((JTextComponent) fractionR206_238r2SigmaPct_Text.get(row)).setText(tempFrac.getRadiogenicIsotopeRatioByName("r206_238r").getOneSigmaPct().
                setScale(ReduxConstants.DEFAULT_CONSTANTS_SCALE,
                        RoundingMode.HALF_UP).toPlainString());
        ((JTextComponent) fractionR206_238r2SigmaPct_Text.get(row)).setCaretPosition(0);

        ((JTextComponent) fractionR207_235r_Text.get(row)).setText(tempFrac.getRadiogenicIsotopeRatioByName("r207_235r").getValue().
                setScale(ReduxConstants.DEFAULT_CONSTANTS_SCALE,
                        RoundingMode.HALF_UP).toPlainString());
        ((JTextComponent) fractionR207_235r_Text.get(row)).setCaretPosition(0);

        ((JTextComponent) fractionR207_235r2SigmaPct_Text.get(row)).setText(tempFrac.getRadiogenicIsotopeRatioByName("r207_235r").getOneSigmaPct().
                setScale(ReduxConstants.DEFAULT_CONSTANTS_SCALE,
                        RoundingMode.HALF_UP).toPlainString());
        ((JTextComponent) fractionR207_235r2SigmaPct_Text.get(row)).setCaretPosition(0);

        ((JTextComponent) fractionR207_206r_Text.get(row)).setText(tempFrac.getRadiogenicIsotopeRatioByName("r207_206r").getValue().
                setScale(ReduxConstants.DEFAULT_CONSTANTS_SCALE,
                        RoundingMode.HALF_UP).toPlainString());
        ((JTextComponent) fractionR207_206r_Text.get(row)).setCaretPosition(0);

        ((JTextComponent) fractionR207_206r2SigmaPct_Text.get(row)).setText(tempFrac.getRadiogenicIsotopeRatioByName("r207_206r").getOneSigmaPct().
                setScale(ReduxConstants.DEFAULT_CONSTANTS_SCALE,
                        RoundingMode.HALF_UP).toPlainString());
        ((JTextComponent) fractionR207_206r2SigmaPct_Text.get(row)).setCaretPosition(0);

        ((JTextComponent) fractionRhoR206_238r__r207_235r_Text.get(row)).setText(tempFrac.getRadiogenicIsotopeRatioByName("rhoR206_238r__r207_235r").getValue().
                setScale(ReduxConstants.DEFAULT_CONSTANTS_SCALE,
                        RoundingMode.HALF_UP).toPlainString());
        ((JTextComponent) fractionRhoR206_238r__r207_235r_Text.get(row)).setCaretPosition(0);

        // Isotopic Dates
        ((JTextComponent) fractionDate206_238r_Text.get(row)).setText(tempFrac.getRadiogenicIsotopeDateByName(RadDates.age206_238r).getValue().
                movePointLeft(6).setScale(ReduxConstants.DEFAULT_CONSTANTS_SCALE,
                        RoundingMode.HALF_UP).toPlainString());
        ((JTextComponent) fractionDate206_238r_Text.get(row)).setCaretPosition(0);

        ((JTextComponent) fractionDate206_238r2SigmaAbs_Text.get(row)).setText(tempFrac.getRadiogenicIsotopeDateByName(RadDates.age206_238r).getOneSigmaAbs().
                movePointLeft(6).setScale(ReduxConstants.DEFAULT_CONSTANTS_SCALE,
                        RoundingMode.HALF_UP).toPlainString());
        ((JTextComponent) fractionDate206_238r2SigmaAbs_Text.get(row)).setCaretPosition(0);

        ((JTextComponent) fractionDate207_235r_Text.get(row)).setText(tempFrac.getRadiogenicIsotopeDateByName(RadDates.age207_235r).getValue().
                movePointLeft(6).setScale(ReduxConstants.DEFAULT_CONSTANTS_SCALE,
                        RoundingMode.HALF_UP).toPlainString());
        ((JTextComponent) fractionDate207_235r_Text.get(row)).setCaretPosition(0);

        ((JTextComponent) fractionDate207_235r2SigmaAbs_Text.get(row)).setText(tempFrac.getRadiogenicIsotopeDateByName(RadDates.age207_235r).getOneSigmaAbs().
                movePointLeft(6).setScale(ReduxConstants.DEFAULT_CONSTANTS_SCALE,
                        RoundingMode.HALF_UP).toPlainString());
        ((JTextComponent) fractionDate207_235r2SigmaAbs_Text.get(row)).setCaretPosition(0);

        ((JTextComponent) fractionDate207_206r_Text.get(row)).setText(tempFrac.getRadiogenicIsotopeDateByName(RadDates.age207_206r).getValue().
                movePointLeft(6).setScale(ReduxConstants.DEFAULT_CONSTANTS_SCALE,
                        RoundingMode.HALF_UP).toPlainString());
        ((JTextComponent) fractionDate207_206r_Text.get(row)).setCaretPosition(0);

        ((JTextComponent) fractionDate207_206r2SigmaAbs_Text.get(row)).setText(tempFrac.getRadiogenicIsotopeDateByName(RadDates.age207_206r).getOneSigmaAbs().
                movePointLeft(6).setScale(ReduxConstants.DEFAULT_CONSTANTS_SCALE,
                        RoundingMode.HALF_UP).toPlainString());
        ((JTextComponent) fractionDate207_206r2SigmaAbs_Text.get(row)).setCaretPosition(0);

    }

    /**
     *
     * @param row
     */
    protected void removeFractionRow(int row) {
        fractionDeleteButtons.remove(row);
        fractionEditButtons.remove(row);

        fractionConcU_Text.remove(row);
        fractionR206_204m_Text.remove(row);
        fractionRTh_Usample_Text.remove(row);
        fractionR206_238r_Text.remove(row);
        fractionR206_238r2SigmaPct_Text.remove(row);
        fractionR207_235r_Text.remove(row);
        fractionR207_235r2SigmaPct_Text.remove(row);
        fractionR207_206r_Text.remove(row);
        fractionR207_206r2SigmaPct_Text.remove(row);
        fractionRhoR206_238r__r207_235r_Text.remove(row);

        fractionDate206_238r_Text.remove(row);
        fractionDate206_238r2SigmaAbs_Text.remove(row);
        fractionDate207_235r_Text.remove(row);
        fractionDate207_235r2SigmaAbs_Text.remove(row);
        fractionDate207_206r_Text.remove(row);
        fractionDate207_206r2SigmaAbs_Text.remove(row);

        // fix row pointers in buttons
        for (int f = 0; f
                < fractionDeleteButtons.size(); f++) {
            FractionI fraction
                    = ((deleteFractionListener) ((JButton) fractionDeleteButtons.get(f)).getActionListeners()[0]).getFraction();

            ((AbstractButton) fractionDeleteButtons.get(f)).removeActionListener(((JButton) fractionDeleteButtons.get(f)).getActionListeners()[0]);
            ((AbstractButton) fractionDeleteButtons.get(f)).addActionListener(new deleteFractionListener(fraction, f));

//            ((JButton) fractionEditButtons.get( f )).removeActionListener( ((JButton) fractionEditButtons.get( f )).getActionListeners()[0] );
//            ((JButton) fractionEditButtons.get( f )).addActionListener( new editFractionListener( myFraction, f ) );
        }

    }

    private void addNewFractionRow(FractionI fraction) {
        int row = fractionDeleteButtons.size();

        addFractionRow(fraction, row, row + 1);
        updateFractionRow(fraction, row);

        // update the keystroke actionlisteners for previous row in table
        modifyComponentKeyMapForTable(fractionEditButtons.get(row - 1), fractionEditButtons, row + 1);
        modifyComponentKeyMapForTable(fractionConcU_Text.get(row - 1), fractionConcU_Text, row + 1);
        modifyComponentKeyMapForTable(fractionR206_204m_Text.get(row - 1), fractionR206_204m_Text, row + 1);
        modifyComponentKeyMapForTable(fractionRTh_Usample_Text.get(row - 1), fractionRTh_Usample_Text, row + 1);
        modifyComponentKeyMapForTable(fractionR206_238r_Text.get(row - 1), fractionR206_238r_Text, row + 1);
        modifyComponentKeyMapForTable(fractionR206_238r2SigmaPct_Text.get(row - 1), fractionR206_238r2SigmaPct_Text, row + 1);

        modifyComponentKeyMapForTable(fractionR207_235r_Text.get(row - 1), fractionR207_235r_Text, row + 1);
        modifyComponentKeyMapForTable(fractionR207_235r2SigmaPct_Text.get(row - 1), fractionR207_235r2SigmaPct_Text, row + 1);

        modifyComponentKeyMapForTable(fractionR207_206r_Text.get(row - 1), fractionR207_206r_Text, row + 1);
        modifyComponentKeyMapForTable(fractionR207_206r2SigmaPct_Text.get(row - 1), fractionR207_206r2SigmaPct_Text, row + 1);

        modifyComponentKeyMapForTable(fractionRhoR206_238r__r207_235r_Text.get(row - 1), fractionRhoR206_238r__r207_235r_Text, row + 1);

        modifyComponentKeyMapForTable(fractionDate206_238r_Text.get(row - 1), fractionDate206_238r_Text, row + 1);
        modifyComponentKeyMapForTable(fractionDate206_238r2SigmaAbs_Text.get(row - 1), fractionDate206_238r2SigmaAbs_Text, row + 1);
        modifyComponentKeyMapForTable(fractionDate207_235r_Text.get(row - 1), fractionDate207_235r_Text, row + 1);
        modifyComponentKeyMapForTable(fractionDate207_235r2SigmaAbs_Text.get(row - 1), fractionDate207_235r2SigmaAbs_Text, row + 1);

        modifyComponentKeyMapForTable(fractionDate207_206r_Text.get(row - 1), fractionDate207_206r_Text, row + 1);
        modifyComponentKeyMapForTable(fractionDate207_206r2SigmaAbs_Text.get(row - 1), fractionDate207_206r2SigmaAbs_Text, row + 1);

        buildFastEditDisplayPanel();

    }

    private class editFractionListener implements ActionListener {

        private int row;
        private FractionI fraction;

        public editFractionListener(FractionI fraction, int row) {
            this.row = row;
            this.fraction = fraction;
        }

        public void actionPerformed(ActionEvent e) {
            // prompt for save if aliquot edited
            boolean proceed = true;

            if (deletedFractions.size() + addedFractions.size() > 0) {
                int result
                        = JOptionPane.showConfirmDialog(
                                null,
                                new String[]{"You must first save the Aliquot ... proceed?"},
                                "ET Redux Warning",
                                JOptionPane.WARNING_MESSAGE);
                if (result == JOptionPane.OK_OPTION) {
                    proceed = true;
                } else {
                    proceed = false;
                }
            }

            if (proceed) {
                saveAliquot();
                saveAliquotFraction(fraction);
                parent.editFraction(fraction, 8);
                updateFractionRow(
                        fraction,
                        getMyAliquot().getAliquotFractions().indexOf(fraction));
            }

        }
    }

    private class deleteFractionListener implements ActionListener {

        private int row;
        private FractionI fraction;

        public deleteFractionListener(FractionI fraction, int row) {
            this.row = row;
            this.fraction = fraction;
        }

        public FractionI getFraction() {
            return fraction;
        }

        public void actionPerformed(ActionEvent e) {
            // check to see if pending as added fraction or existing fraction
            if (row < getMyAliquot().getAliquotFractions().size()) {
                deletedFractions.add(fraction);
            } else {
                addedFractions.remove(fraction);
            }

            removeFractionRow(row);
            buildFastEditDisplayPanel();
        }
    }

    private void saveAliquot() {

        // general info
        getMyAliquot().setAliquotName(aliquotName_text.getText());
        this.setTitle("Aliquot # " + getMyAliquot().getAliquotNumber() + " <> " + getMyAliquot().getAliquotName());

        getMyAliquot().setAnalystName(analystName_text.getText());
        ((UPbReduxAliquot) getMyAliquot()).getMyReduxLabData().setAnalystName(analystName_text.getText());
        getMyAliquot().setAliquotInstrumentalMethod(
                instrumentalMethod_jcombo.getSelectedItem().toString());
        getMyAliquot().setAliquotInstrumentalMethodReference(instMethodRef_text.getText());
        getMyAliquot().setAliquotReference(reference_text.getText());
        getMyAliquot().setAliquotComment(comment_textArea.getText());

        // calibration and mineral standards
        getMyAliquot().getMineralStandardModels().clear();
        for (JComponent cb : mineralStandardsCheckBoxes) {
            if (((JCheckBox) cb).isSelected()) {
                try {
                    getMyAliquot().getMineralStandardModels().add(//
                            ReduxLabData.getInstance().getAMineralStandardModel(((JCheckBox) cb).getText()));
                } catch (BadLabDataException ex) {
                    new ETWarningDialog(ex).setVisible(true);
                }
            }
        }

        getMyAliquot().setKeyWordsCSV(keyWordsCSV_text.getText());

        // handle deleted fractions
        for (int f = 0; f
                < deletedFractions.size(); f++) {
            getSample().removeUPbReduxFraction(deletedFractions.get(f));
            getMyAliquot().getAliquotFractions().remove(deletedFractions.get(f));
        }

        deletedFractions.clear();

        // handle added fractions
        for (int f = 0; f
                < addedFractions.size(); f++) {
            getSample().addFraction((UPbFraction) addedFractions.get(f));
            getMyAliquot().getAliquotFractions().add(addedFractions.get(f));
        }

        addedFractions.clear();

        // master fields
        for (FractionI f : getMyAliquot().getAliquotFractions()) {
            saveAliquotFraction(f);
        }

        // save the sample
        // removed april 2011 as part of registry upgrade
//        getMyAliquot().setSampleIGSN( sampleIGSN_text.getText().trim() );
//        sample.setSampleIGSN( sampleIGSN_text.getText().trim() );
        SampleInterface.saveSampleAsSerializedReduxFile(sample);

        System.out.println("**************** PRE-PUBLISH CHECKLIST FOR ALIQUOT");

    }

    /**
     *
     * @param tempFrac
     * @throws NumberFormatException
     */
    protected void saveAliquotFraction(FractionI tempFrac)
            throws NumberFormatException {

        int row = getMyAliquot().getAliquotFractions().indexOf(tempFrac);
        // april 2010

        // Composition
        try {
            tempFrac.getCompositionalMeasureByName("concU")//
                    .setValue(new BigDecimal(((JTextField) fractionConcU_Text.get(row)).getText(), ReduxConstants.mathContext15).//
                            movePointLeft(6));
        } catch (NumberFormatException e) {
        }
        try {
            tempFrac.getCompositionalMeasureByName("rTh_Usample")//
                    .setValue(new BigDecimal(((JTextField) fractionRTh_Usample_Text.get(row)).getText(), ReduxConstants.mathContext15));
        } catch (NumberFormatException e) {
        }

        // measured ratios
        try {
            tempFrac.getMeasuredRatioByName(MeasuredRatios.r206_204m.getName())//
                    .setValue(new BigDecimal(((JTextField) fractionR206_204m_Text.get(row)).getText(), ReduxConstants.mathContext15));
        } catch (NumberFormatException e) {
        }

        // radiogenic isotopic ratios
        ValueModel ratio = null;
        BigDecimal oneSigmaPct = null;
        try {
            tempFrac.getRadiogenicIsotopeRatioByName("r206_238r")//
                    .setValue(new BigDecimal(((JTextComponent) fractionR206_238r_Text.get(row)).getText(), ReduxConstants.mathContext15));
            ratio = tempFrac.getRadiogenicIsotopeRatioByName("r206_238r");
            oneSigmaPct = new BigDecimal(((JTextComponent) fractionR206_238r2SigmaPct_Text.get(row)).getText(), ReduxConstants.mathContext15);
            tempFrac.getRadiogenicIsotopeRatioByName("r206_238r")//
                    .setOneSigma(ValueModel.convertOneSigmaPctToAbsIfRequired(ratio, oneSigmaPct));
        } catch (NumberFormatException e) {
        }

        try {
            tempFrac.getRadiogenicIsotopeRatioByName("r207_235r")//
                    .setValue(new BigDecimal(((JTextComponent) fractionR207_235r_Text.get(row)).getText(), ReduxConstants.mathContext15));
            ratio = tempFrac.getRadiogenicIsotopeRatioByName("r207_235r");
            oneSigmaPct = new BigDecimal(((JTextComponent) fractionR207_235r2SigmaPct_Text.get(row)).getText(), ReduxConstants.mathContext15);
            tempFrac.getRadiogenicIsotopeRatioByName("r207_235r")//
                    .setOneSigma(ValueModel.convertOneSigmaPctToAbsIfRequired(ratio, oneSigmaPct));
        } catch (NumberFormatException e) {
        }

        try {
            tempFrac.getRadiogenicIsotopeRatioByName("r207_206r").//
                    setValue(new BigDecimal(((JTextComponent) fractionR207_206r_Text.get(row)).getText(), ReduxConstants.mathContext15));
            ratio = tempFrac.getRadiogenicIsotopeRatioByName("r207_206r");
            oneSigmaPct = new BigDecimal(((JTextComponent) fractionR207_206r2SigmaPct_Text.get(row)).getText(), ReduxConstants.mathContext15);
            tempFrac.getRadiogenicIsotopeRatioByName("r207_206r")//
                    .setOneSigma(ValueModel.convertOneSigmaPctToAbsIfRequired(ratio, oneSigmaPct));
        } catch (NumberFormatException e) {
        }

        try {
            tempFrac.getRadiogenicIsotopeRatioByName("rhoR206_238r__r207_235r")//
                    .setValue(new BigDecimal(((JTextField) fractionRhoR206_238r__r207_235r_Text.get(row)).getText(), ReduxConstants.mathContext15));
        } catch (NumberFormatException e) {
        }

        // Isotopic Dates
        try {
            tempFrac.getRadiogenicIsotopeDateByName(RadDates.age206_238r)//
                    .setValue(new BigDecimal(((JTextComponent) fractionDate206_238r_Text.get(row)).getText(), ReduxConstants.mathContext15).//
                            movePointRight(6));
            tempFrac.getRadiogenicIsotopeDateByName(RadDates.age206_238r)//
                    .setOneSigma(new BigDecimal(((JTextComponent) fractionDate206_238r2SigmaAbs_Text.get(row)).getText(), ReduxConstants.mathContext15).//
                            movePointRight(6));
        } catch (NumberFormatException e) {
        }

        try {
            tempFrac.getRadiogenicIsotopeDateByName(RadDates.age207_235r)//
                    .setValue(new BigDecimal(((JTextComponent) fractionDate207_235r_Text.get(row)).getText(), ReduxConstants.mathContext15).//
                            movePointRight(6));
            tempFrac.getRadiogenicIsotopeDateByName(RadDates.age207_235r)//
                    .setOneSigma(new BigDecimal(((JTextComponent) fractionDate207_235r2SigmaAbs_Text.get(row)).getText(), ReduxConstants.mathContext15).//
                            movePointRight(6));
        } catch (NumberFormatException e) {
        }

        try {
            tempFrac.getRadiogenicIsotopeDateByName(RadDates.age207_206r)//
                    .setValue(new BigDecimal(((JTextComponent) fractionDate207_206r_Text.get(row)).getText(), ReduxConstants.mathContext15).//
                            movePointRight(6));
            tempFrac.getRadiogenicIsotopeDateByName(RadDates.age207_206r)//
                    .setOneSigma(new BigDecimal(((JTextComponent) fractionDate207_206r2SigmaAbs_Text.get(row)).getText(), ReduxConstants.mathContext15).//
                            movePointRight(6));
        } catch (NumberFormatException e) {
        }
        // better safe than sorry for now
        ((UPbFractionI) tempFrac).setChanged(true);

    }

    /**
     *
     * @param evt
     */
    protected void restore_buttonActionPerformed(java.awt.event.ActionEvent evt) {
        initAliquot();//showSavedDataII();
    }

    /**
     *
     * @param evt
     */
    protected void saveAndClose_buttonActionPerformed(java.awt.event.ActionEvent evt) {
        saveAliquot();
        close();
    }

    /**
     *
     * @param evt
     */
    protected void save_buttonActionPerformed(java.awt.event.ActionEvent evt) {
        saveAliquot();
    }

    /**
     *
     * @param evt
     */
    protected void exportXMLAliquot_buttonActionPerformed(java.awt.event.ActionEvent evt) {
        saveAliquot();
        exportAliquotToXML();
    }

    /**
     *
     * @param evt
     */
    protected void saveAndPreviewXMLAliquotAsHTML_buttonActionPerformed(java.awt.event.ActionEvent evt) {
        saveAliquot();
        // save off the Aliquot as a temp file
        String tempAliquotXML = "TempAliquot.xml";
        ((XMLSerializationI) myAliquot).serializeXMLObject(tempAliquotXML);
        viewXMLAliquotAsHTML(tempAliquotXML);
    }

    /**
     *
     * @param evt
     */
    protected void saveAndUploadAliquotToGeochron_buttonActionPerformed(java.awt.event.ActionEvent evt) {
        saveAliquot();
        try {
            uploadAliquotToGeochronZip();
        } catch (ETException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
    }
}
