/*
 * AliquotLegacyEditorForIDTIMS.java
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
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import org.earthtime.ETReduxFrame;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.aliquots.UPbReduxAliquot;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.UPb_Redux.fractions.Fraction;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFraction;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFractionI;
import org.earthtime.UPb_Redux.reduxLabData.ReduxLabData;
import org.earthtime.UPb_Redux.renderers.EditFractionButton;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.aliquots.AliquotInterface;
import org.earthtime.dataDictionaries.AnalysisMeasures;
import org.earthtime.dataDictionaries.RadDates;
import org.earthtime.exceptions.ETException;
import org.earthtime.exceptions.ETWarningDialog;
import org.earthtime.samples.SampleInterface;
import org.jdesktop.layout.GroupLayout.ParallelGroup;
import org.jdesktop.layout.GroupLayout.SequentialGroup;

/**
 *
 * @author James F. Bowring
 */
public class AliquotLegacyEditorForIDTIMS extends AliquotEditorDialog {

    // Instance variables
    // composition
    private ArrayList<JComponent> fractionRadToCommonTotal_Text;
    private ArrayList<JComponent> fractionTotRadiogenicPbMass_Text;
    private ArrayList<JLabel> fractionTotRadiogenicPbMassPICOGRAMS;
    private ArrayList<JComponent> fractionTotCommonPbMass_Text;
    private ArrayList<JLabel> fractionTotCommonPbMassPICOGRAMS;
    private ArrayList<JComponent> fractionConcU_Text;
    private ArrayList<JLabel> fractionConcUPPM;
    private ArrayList<JComponent> fractionConcPb_ib_Text;
    private ArrayList<JLabel> fractionConcPb_ibPPM;
    private ArrayList<JComponent> fractionRTh_Usample_Text;
    private ArrayList<JComponent> fractionMass_Text;
    // Isotopic ratios
    private ArrayList<JComponent> fractionR206_238r_Text;
    private ArrayList<JComponent> fractionR206_238r2SigmaPct_Text;
    private ArrayList<JComponent> fractionR207_235r_Text;
    private ArrayList<JComponent> fractionR207_235r2SigmaPct_Text;
    private ArrayList<JComponent> fractionR207_206r_Text;
    private ArrayList<JComponent> fractionR207_206r2SigmaPct_Text;
    private ArrayList<JComponent> fractionRhoR206_238r__r207_235r_Text;
    private ArrayList<JComponent> fractionR206_204tfc_Text;
    private ArrayList<JComponent> fractionR208_206r_Text;
    // Isotopic Dates
    private ArrayList<JComponent> fractionDate206_238r_Text;
    private ArrayList<JComponent> fractionDate206_238r2SigmaAbs_Text;
    private ArrayList<JComponent> fractionDate207_235r_Text;
    private ArrayList<JComponent> fractionDate207_235r2SigmaAbs_Text;
    private ArrayList<JComponent> fractionDate207_206r_Text;
    private ArrayList<JComponent> fractionDate207_206r2SigmaAbs_Text;
    private ArrayList<JComponent> fractionDate206_238r_Th_Text;
    private ArrayList<JComponent> fractionDate206_238r_Th2SigmaAbs_Text;
    private ArrayList<JComponent> fractionDate207_235r_Pa_Text;
    private ArrayList<JComponent> fractionDate207_235r_Pa2SigmaAbs_Text;
    private ArrayList<JComponent> fractionDate207_206r_Th_Text;
    private ArrayList<JComponent> fractionDate207_206r_Th2SigmaAbs_Text;
    private ArrayList<JComponent> fractionDate207_206r_Pa_Text;
    private ArrayList<JComponent> fractionDate207_206r_Pa2SigmaAbs_Text;

    /**
     * Creates new form AliquotEditorDialog
     * @param parent
     * @param modal 
     * @param sample 
     * @param aliquot  
     */
    public AliquotLegacyEditorForIDTIMS (
            ETReduxFrame parent,
            boolean modal,
            SampleInterface sample,
            AliquotInterface aliquot ) {
        super( parent, modal, sample, aliquot );


        saveAndClose_button.removeActionListener( saveAndClose_button.getActionListeners()[0] );
        saveAndClose_button.addActionListener( new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed ( java.awt.event.ActionEvent evt ) {
                saveAndClose_buttonActionPerformed( evt );
            }
        } );

        save_button.removeActionListener( save_button.getActionListeners()[0] );
        save_button.addActionListener( new java.awt.event.ActionListener() {

            @Override
            public void actionPerformed ( java.awt.event.ActionEvent evt ) {
                save_buttonActionPerformed( evt );
            }
        } );

        restore_button.removeActionListener( restore_button.getActionListeners()[0] );
        restore_button.addActionListener( new java.awt.event.ActionListener() {

            public void actionPerformed ( java.awt.event.ActionEvent evt ) {
                restore_buttonActionPerformed( evt );
            }
        } );

        exportXMLAliquot_button.removeActionListener( exportXMLAliquot_button.getActionListeners()[0] );
        exportXMLAliquot_button.addActionListener( new java.awt.event.ActionListener() {

            public void actionPerformed ( java.awt.event.ActionEvent evt ) {
                exportXMLAliquot_buttonActionPerformed( evt );
            }
        } );

        saveAndPreviewXMLAliquotAsHTML_button.removeActionListener( saveAndPreviewXMLAliquotAsHTML_button.getActionListeners()[0] );
        saveAndPreviewXMLAliquotAsHTML_button.addActionListener( new java.awt.event.ActionListener() {

            public void actionPerformed ( java.awt.event.ActionEvent evt ) {
                saveAndPreviewXMLAliquotAsHTML_buttonActionPerformed( evt );
            }
        } );

        saveAndUploadAliquotToGeochron_button.removeActionListener( saveAndUploadAliquotToGeochron_button.getActionListeners()[0] );
        saveAndUploadAliquotToGeochron_button.addActionListener( new java.awt.event.ActionListener() {

            public void actionPerformed ( java.awt.event.ActionEvent evt ) {
                saveAndUploadAliquotToGeochron_buttonActionPerformed( evt );
            }
        } );


    }

    /**
     * 
     */
    @Override
    public void initAliquot () {

        fastEdits_panel.removeAll();
        //       showSavedDataI();

        deletedFractions =
                new Vector<Fraction>();
        addedFractions =
                new Vector<Fraction>();

        fastEdits_panel.setBackground(ReduxConstants.myFractionGreenColor );

        fractionDeleteButtons =
                new ArrayList<JComponent>();

        fractionEditButtons =
                new ArrayList<JComponent>();

        fractionR206_238r_Text =
                new ArrayList<JComponent>();
        fractionR206_238r2SigmaPct_Text =
                new ArrayList<JComponent>();

        fractionR207_235r_Text =
                new ArrayList<JComponent>();
        fractionR207_235r2SigmaPct_Text =
                new ArrayList<JComponent>();

        fractionR207_206r_Text =
                new ArrayList<JComponent>();
        fractionR207_206r2SigmaPct_Text =
                new ArrayList<JComponent>();

        fractionRhoR206_238r__r207_235r_Text =
                new ArrayList<JComponent>();

        fractionR206_204tfc_Text =
                new ArrayList<JComponent>();

        fractionR208_206r_Text =
                new ArrayList<JComponent>();

        // composition
        fractionRadToCommonTotal_Text =
                new ArrayList<JComponent>();

        fractionTotRadiogenicPbMass_Text =
                new ArrayList<JComponent>();
        fractionTotRadiogenicPbMassPICOGRAMS =
                new ArrayList<JLabel>();

        fractionTotCommonPbMass_Text =
                new ArrayList<JComponent>();
        fractionTotCommonPbMassPICOGRAMS =
                new ArrayList<JLabel>();


        fractionConcU_Text =
                new ArrayList<JComponent>();
        fractionConcUPPM =
                new ArrayList<JLabel>();

        fractionConcPb_ib_Text =
                new ArrayList<JComponent>();
        fractionConcPb_ibPPM =
                new ArrayList<JLabel>();

        fractionRTh_Usample_Text =
                new ArrayList<JComponent>();

        fractionMass_Text =
                new ArrayList<JComponent>();

        // Isotopic Dates

        fractionDate206_238r_Text =
                new ArrayList<JComponent>();
        fractionDate206_238r2SigmaAbs_Text =
                new ArrayList<JComponent>();

        fractionDate207_235r_Text =
                new ArrayList<JComponent>();
        fractionDate207_235r2SigmaAbs_Text =
                new ArrayList<JComponent>();

        fractionDate207_206r_Text =
                new ArrayList<JComponent>();
        fractionDate207_206r2SigmaAbs_Text =
                new ArrayList<JComponent>();

        fractionDate206_238r_Th_Text =
                new ArrayList<JComponent>();
        fractionDate206_238r_Th2SigmaAbs_Text =
                new ArrayList<JComponent>();

        fractionDate207_235r_Pa_Text =
                new ArrayList<JComponent>();
        fractionDate207_235r_Pa2SigmaAbs_Text =
                new ArrayList<JComponent>();

        fractionDate207_206r_Th_Text =
                new ArrayList<JComponent>();
        fractionDate207_206r_Th2SigmaAbs_Text =
                new ArrayList<JComponent>();

        fractionDate207_206r_Pa_Text =
                new ArrayList<JComponent>();
        fractionDate207_206r_Pa2SigmaAbs_Text =
                new ArrayList<JComponent>();


        // populate rows

        for (int row = 0; row
                < getMyAliquot().getAliquotFractions().size(); row ++) {

            Fraction tempFrac = getMyAliquot().getAliquotFractions().get( row );
            int max = getMyAliquot().getAliquotFractions().size();
            addFractionRow( tempFrac, row, max );

        }

        // populate the components with fraction data
        showSavedDataII();

        buildFastEditDisplayPanel();

    }

    /**
     * 
     * @param tempFrac
     * @param row
     * @param max
     */
    protected void addFractionRow ( Fraction tempFrac, int row, int max ) {

        // Buttons to allow deletion of fractions
        JButton tempJB = new EditFractionButton( "X", row, true );
        tempJB.setForeground( Color.red );
        tempJB.setToolTipText( "Click to DELETE Fraction!" );
        tempJB.setMargin( new Insets( 0, 0, 0, 0 ) );
        tempJB.addActionListener( new deleteFractionListener( tempFrac, row ) );
        //tempJB.setFont(new Font("SansSerif", Font.PLAIN, 10));
        fractionDeleteButtons.add( tempJB );
        modifyComponentKeyMapForTable( tempJB, fractionDeleteButtons, max );

        // Buttons to open fraction editor
        tempJB =
                new EditFractionButton( "Kwiki", row, true );
        //     tempJB.addActionListener(new editFractionListener(tempFrac, row));
        fractionEditButtons.add( tempJB );
        modifyComponentKeyMapForTable( tempJB, fractionEditButtons, max );

        // legacy isotopic ratios
        insertTableTextField( fractionR206_238r_Text, max );
        insertTableTextField( fractionR206_238r2SigmaPct_Text, max );

        insertTableTextField( fractionR207_235r_Text, max );
        insertTableTextField( fractionR207_235r2SigmaPct_Text, max );

        insertTableTextField( fractionR207_206r_Text, max );
        insertTableTextField( fractionR207_206r2SigmaPct_Text, max );

        insertTableTextField( fractionRhoR206_238r__r207_235r_Text, max );

        insertTableTextField( fractionR206_204tfc_Text, max );

        insertTableTextField( fractionR208_206r_Text, max );




        // Composition
        insertTableTextField( fractionRadToCommonTotal_Text, max );

        insertTableTextField( fractionTotRadiogenicPbMass_Text, max );
        // TotRadiogenicPbMass picograms label
        JLabel tempJL = new JLabel( " pg" );
        tempJL.setForeground( Color.RED );
        fractionTotRadiogenicPbMassPICOGRAMS.add( tempJL );

        insertTableTextField( fractionTotCommonPbMass_Text, max );
        // totCommonPbMass picograms label
        tempJL =
                new JLabel( " pg" );
        tempJL.setForeground( Color.RED );
        fractionTotCommonPbMassPICOGRAMS.add( tempJL );

        insertTableTextField( fractionConcU_Text, max );
        // mass label
        tempJL = new JLabel( "ppm" );
        tempJL.setForeground( Color.RED );
        fractionConcUPPM.add( tempJL );

        insertTableTextField( fractionConcPb_ib_Text, max );
        // totCommonPbMass picograms label
        tempJL =
                new JLabel( "ppm" );
        tempJL.setForeground( Color.RED );
        fractionConcPb_ibPPM.add( tempJL );

        insertTableTextField( fractionRTh_Usample_Text, max );

        insertTableTextField( fractionMass_Text, max );


        // Isotopic Dates
        insertTableTextField( fractionDate206_238r_Text, max );
        insertTableTextField( fractionDate206_238r2SigmaAbs_Text, max );

        insertTableTextField( fractionDate207_235r_Text, max );
        insertTableTextField( fractionDate207_235r2SigmaAbs_Text, max );

        insertTableTextField( fractionDate207_206r_Text, max );
        insertTableTextField( fractionDate207_206r2SigmaAbs_Text, max );

        insertTableTextField( fractionDate206_238r_Th_Text, max );
        insertTableTextField( fractionDate206_238r_Th2SigmaAbs_Text, max );

        insertTableTextField( fractionDate207_235r_Pa_Text, max );
        insertTableTextField( fractionDate207_235r_Pa2SigmaAbs_Text, max );

        insertTableTextField( fractionDate207_206r_Th_Text, max );
        insertTableTextField( fractionDate207_206r_Th2SigmaAbs_Text, max );

        insertTableTextField( fractionDate207_206r_Pa_Text, max );
        insertTableTextField( fractionDate207_206r_Pa2SigmaAbs_Text, max );


    }

    private void buildFastEditDisplayPanel () {

        fastEdits_panel.removeAll();

        // build display
        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout( fastEdits_panel );
        fastEdits_panel.setLayout( jPanel2Layout );

        ParallelGroup myHorizFraction = jPanel2Layout.createParallelGroup( org.jdesktop.layout.GroupLayout.LEADING, false );
        SequentialGroup myVerticalFraction = jPanel2Layout.createSequentialGroup();

        // create title row elements
        JLabel headDelete = new JLabel( "DELETE" );
        headDelete.setFont( redHeadFont );
        headDelete.setForeground( Color.RED );

        JLabel headFraction = new JLabel( "EDIT Fraction" );
        headFraction.setFont( redHeadFont );
        headFraction.setForeground( Color.RED );


        // isotopic ratios
        JLabel headR206_238r = new JLabel( "206Pb/238U" );
        headR206_238r.setFont( redHeadFont );
        headR206_238r.setForeground( Color.RED );

        JLabel headR206_238r2SigmaPct = new JLabel( "2-sigma%" );
        headR206_238r2SigmaPct.setFont( redHeadFont );
        headR206_238r2SigmaPct.setForeground( Color.RED );

        JLabel headR207_235r = new JLabel( "207Pb/235U" );
        headR207_235r.setFont( redHeadFont );
        headR207_235r.setForeground( Color.RED );

        JLabel headR207_235r2SigmaPct = new JLabel( "2-sigma%" );
        headR207_235r2SigmaPct.setFont( redHeadFont );
        headR207_235r2SigmaPct.setForeground( Color.RED );

        JLabel headR207_206r = new JLabel( "207Pb/206Pb" );
        headR207_206r.setFont( redHeadFont );
        headR207_206r.setForeground( Color.RED );

        JLabel headR207_206r2SigmaPct = new JLabel( "2-sigma%" );
        headR207_206r2SigmaPct.setFont( redHeadFont );
        headR207_206r2SigmaPct.setForeground( Color.RED );

        JLabel headRhoR206_238r__r207_235r = new JLabel( "rho 6/8-7/35" );
        headRhoR206_238r__r207_235r.setFont( redHeadFont );
        headRhoR206_238r__r207_235r.setForeground( Color.RED );

        JLabel headR206_204r = new JLabel( "206Pb/204Pb" );
        headR206_204r.setFont( redHeadFont );
        headR206_204r.setForeground( Color.RED );

        JLabel headR208_206r = new JLabel( "208Pb/206Pb" );
        headR208_206r.setFont( redHeadFont );
        headR208_206r.setForeground( Color.RED );


        // Composition
        JLabel headRadToCommonTotal = new JLabel( "Pb*/Pbc" );
        headRadToCommonTotal.setFont( redHeadFont );
        headRadToCommonTotal.setForeground( Color.RED );

        JLabel headTotRadiogenicPbMass = new JLabel( "Pb* (pg)" );
        headTotRadiogenicPbMass.setFont( redHeadFont );
        headTotRadiogenicPbMass.setForeground( Color.RED );

        JLabel headTotCommonPbMass = new JLabel( "Pbc (pg)" );
        headTotCommonPbMass.setFont( redHeadFont );
        headTotCommonPbMass.setForeground( Color.RED );

        JLabel headConcU = new JLabel( "conc U" );
        headConcU.setFont( redHeadFont );
        headConcU.setForeground( Color.RED );

        JLabel headConcPb_ib = new JLabel( "conc Pb" );
        headConcPb_ib.setFont( redHeadFont );
        headConcPb_ib.setForeground( Color.RED );

        JLabel headRTh_Usample = new JLabel( "Th/U samp" );
        headRTh_Usample.setFont( redHeadFont );
        headRTh_Usample.setForeground( Color.RED );

        JLabel headMass = new JLabel( "Fraction mass" );
        headMass.setFont( redHeadFont );
        headMass.setForeground( Color.RED );

        // Isotopic Dates
        JLabel headDate206_238r = new JLabel( "206Pb/238U" );
        headDate206_238r.setFont( redHeadFont );
        headDate206_238r.setForeground( Color.RED );

        JLabel headDate206_238r2SigmaAbs = new JLabel( "2-sigma" );
        headDate206_238r2SigmaAbs.setFont( redHeadFont );
        headDate206_238r2SigmaAbs.setForeground( Color.RED );

        JLabel headDate207_235r = new JLabel( "207Pb/235U" );
        headDate207_235r.setFont( redHeadFont );
        headDate207_235r.setForeground( Color.RED );

        JLabel headDate207_235r2SigmaAbs = new JLabel( "2-sigma" );
        headDate207_235r2SigmaAbs.setFont( redHeadFont );
        headDate207_235r2SigmaAbs.setForeground( Color.RED );

        JLabel headDate207_206r = new JLabel( "207Pb/206Pb" );
        headDate207_206r.setFont( redHeadFont );
        headDate207_206r.setForeground( Color.RED );

        JLabel headDate207_206r2SigmaAbs = new JLabel( "2-sigma" );
        headDate207_206r2SigmaAbs.setFont( redHeadFont );
        headDate207_206r2SigmaAbs.setForeground( Color.RED );

        JLabel headDate206_238r_Th = new JLabel( "206Pb/238UxTh" );
        headDate206_238r_Th.setFont( redHeadFont );
        headDate206_238r_Th.setForeground( Color.RED );

        JLabel headDate206_238r_Th2SigmaAbs = new JLabel( "2-sigma" );
        headDate206_238r_Th2SigmaAbs.setFont( redHeadFont );
        headDate206_238r_Th2SigmaAbs.setForeground( Color.RED );

        JLabel headDate207_235r_Pa = new JLabel( "207Pb/235UxPa" );
        headDate207_235r_Pa.setFont( redHeadFont );
        headDate207_235r_Pa.setForeground( Color.RED );

        JLabel headDate207_235r_Pa2Sigma = new JLabel( "2-sigma" );
        headDate207_235r_Pa2Sigma.setFont( redHeadFont );
        headDate207_235r_Pa2Sigma.setForeground( Color.RED );

        JLabel headDate207_206r_Th = new JLabel( "207Pb/206UxTh" );
        headDate207_206r_Th.setFont( redHeadFont );
        headDate207_206r_Th.setForeground( Color.RED );

        JLabel headDate207_206r_Th2SigmaAbs = new JLabel( "2-sigma" );
        headDate207_206r_Th2SigmaAbs.setFont( redHeadFont );
        headDate207_206r_Th2SigmaAbs.setForeground( Color.RED );

        JLabel headDate207_206r_Pa = new JLabel( "207Pb/206PbxPa" );
        headDate207_206r_Pa.setFont( redHeadFont );
        headDate207_206r_Pa.setForeground( Color.RED );

        JLabel headDate207_206r_Pa2SigmaAbs = new JLabel( "2-sigma" );
        headDate207_206r_Pa2SigmaAbs.setFont( redHeadFont );
        headDate207_206r_Pa2SigmaAbs.setForeground( Color.RED );


        // build display *******************************************************

        JLabel headIsotopicRatios = new JLabel( "Isotopic Ratios          Isotopic Ratios          Isotopic Ratios         |" );
        headIsotopicRatios.setFont( new Font( "Monospaced", Font.BOLD, 18 ) );
        headIsotopicRatios.setForeground( Color.RED );

        JLabel headComposition = new JLabel( "Composition          Composition          Composition       |" );
        headComposition.setFont( new Font( "Monospaced", Font.BOLD, 18 ) );
        headComposition.setForeground( Color.RED );

        JLabel headIsotopicDates = new JLabel( "Isotopic Dates Ma          Isotopic Dates Ma          Isotopic Dates Ma          Isotopic Dates Ma" );
        headIsotopicDates.setFont( new Font( "Monospaced", Font.BOLD, 18 ) );
        headIsotopicDates.setForeground( Color.RED );

        // master fields
        myHorizFraction.add( jPanel2Layout.createSequentialGroup()//
                .add( 50, 50, 50 ) // left margin
              //  .add( masterNewFractionName, 110, 110, 110 )//
                .add( 115, 115, 115 )//
                .add( headIsotopicRatios, 850, 850, 850 )//
                .add( 5, 5, 5 )//
                .add( headComposition, 725, 725, 725 )//
                .add( 5, 5, 5 )//
                .add( headIsotopicDates, 1100, 1100, 1100 )//
                .add( 5, 5, 5 )//
                );

        myVerticalFraction.add( 5, 5, 5 ) // top margin
                .add( jPanel2Layout.createParallelGroup( org.jdesktop.layout.GroupLayout.TRAILING )//.BASELINE)//
               // .add( masterNewFractionName, 22, 22, 22 )//
                .add( headIsotopicRatios, 22, 22, 22 )//
                .add( headComposition, 22, 22, 22 )//
                .add( headIsotopicDates, 22, 22, 22 )//
                );

        // fill buttons

        myHorizFraction.add( jPanel2Layout.createSequentialGroup()//
                .add( 50, 50, 50 ) // left margin
                //.add( masterNewFractionNameAdder, 110, 110, 110 )//
                .add( 115, 115, 115 )//
                );

        myVerticalFraction.add( 1, 1, 1 ) // top margin
                .add( jPanel2Layout.createParallelGroup( org.jdesktop.layout.GroupLayout.TRAILING )//.BASELINE)//
              //  .add( masterNewFractionNameAdder )//
                );


        myHorizFraction.add( jPanel2Layout.createSequentialGroup()//
                .add( 8, 8, 8 ) // left margin
                .add( headDelete, 50, 50, 50 )//
                .add( 6, 6, 6 )//
                .add( headFraction, 100, 100, 100 )//
                .add( 15, 15, 15 )//
                .add( headR206_238r, 90, 90, 90 )//
                .add( 5, 5, 5 )//
                .add( headR206_238r2SigmaPct, 60, 60, 60 )//
                .add( 15, 15, 15 )//
                .add( headR207_235r, 90, 90, 90 )//
                .add( 5, 5, 5 )//
                .add( headR207_235r2SigmaPct, 60, 60, 60 )//
                .add( 15, 15, 15 )//
                .add( headR207_206r, 90, 90, 90 )//
                .add( 5, 5, 5 )//
                .add( headR207_206r2SigmaPct, 60, 60, 60 )//
                .add( 15, 15, 15 )//
                .add( headRhoR206_238r__r207_235r, 90, 90, 90 )//
                .add( 15, 15, 15 )//
                .add( headR206_204r, 90, 90, 90 )//
                .add( 15, 15, 15 )//
                .add( headR208_206r, 90, 90, 90 )//
                .add( 15, 15, 15 )//
                .add( headRadToCommonTotal, 90, 90, 90 )//
                .add( 15, 15, 15 )//
                .add( headTotRadiogenicPbMass, 90, 90, 90 )//
                .add( 15, 15, 15 )//
                .add( headTotCommonPbMass, 60, 60, 60 )//
                .add( 15, 15, 15 )//
                .add( headConcU, 90, 90, 90 )//
                .add( 15, 15, 15 )//
                .add( headConcPb_ib, 90, 90, 90 )//
                .add( 15, 15, 15 )//
                .add( headRTh_Usample, 90, 90, 90 )//
                .add( 5, 5, 5 )//
                .add( headMass, 90, 90, 90 )//
                .add( 25, 25, 25 )//
                .add( headDate206_238r, 90, 90, 90 )//
                .add( 5, 5, 5 )//
                .add( headDate206_238r2SigmaAbs, 60, 60, 60 )//
                .add( 15, 15, 15 )//
                .add( headDate207_235r, 90, 90, 90 )//
                .add( 5, 5, 5 )//
                .add( headDate207_235r2SigmaAbs, 60, 60, 60 )//
                .add( 15, 15, 15 )//
                .add( headDate207_206r, 90, 90, 90 )//
                .add( 5, 5, 5 )//
                .add( headDate207_206r2SigmaAbs, 60, 60, 60 )//
                .add( 15, 15, 15 )//
                .add( headDate206_238r_Th, 90, 90, 90 )//
                .add( 5, 5, 5 )//
                .add( headDate206_238r_Th2SigmaAbs, 60, 60, 60 )//
                .add( 15, 15, 15 )//
                .add( headDate207_235r_Pa, 90, 90, 90 )//
                .add( 5, 5, 5 )//
                .add( headDate207_235r_Pa2Sigma, 60, 60, 60 )//
                .add( 15, 15, 15 )//
                .add( headDate207_206r_Th, 90, 90, 90 )//
                .add( 5, 5, 5 )//
                .add( headDate207_206r_Th2SigmaAbs, 60, 60, 60 )//
                .add( 10, 10, 10 )//
                .add( headDate207_206r_Pa, 90, 90, 90 )//
                .add( 5, 5, 5 )//
                .add( headDate207_206r_Pa2SigmaAbs, 60, 60, 60 )//
                .add( 15, 15, 15 )//
                );

        myVerticalFraction//
                .add( 10, 10, 10 ) // top margin
                .add( jPanel2Layout.createParallelGroup( org.jdesktop.layout.GroupLayout.TRAILING )//.BASELINE)//
                .add( headDelete )//
                .add( headFraction )//
                .add( headR206_238r )//
                .add( headR206_238r2SigmaPct )//
                .add( headR207_235r )//
                .add( headR207_235r2SigmaPct )//
                .add( headR207_206r )//
                .add( headR207_206r2SigmaPct )//
                .add( headRhoR206_238r__r207_235r )//
                .add( headR206_204r )//
                .add( headR208_206r )//
                .add( headRadToCommonTotal )//
                .add( headTotRadiogenicPbMass )//
                .add( headTotCommonPbMass )//
                .add( headConcU )//
                .add( headConcPb_ib )//
                .add( headRTh_Usample )//
                .add( headMass )//
                .add( headDate206_238r )//
                .add( headDate206_238r2SigmaAbs )//
                .add( headDate207_235r )//
                .add( headDate207_235r2SigmaAbs )//
                .add( headDate207_206r )//
                .add( headDate207_206r2SigmaAbs )//
                .add( headDate206_238r_Th )//
                .add( headDate206_238r_Th2SigmaAbs )//
                .add( headDate207_235r_Pa )//
                .add( headDate207_235r_Pa2Sigma )//
                .add( headDate207_206r_Th )//
                .add( headDate207_206r_Th2SigmaAbs )//
                .add( headDate207_206r_Pa )//
                .add( headDate207_206r_Pa2SigmaAbs )//
                )//
                .add( 2, 2, 2 );


        // stop delete when only one fraction
        fractionDeleteButtons.get( 0 ).setEnabled( fractionDeleteButtons.size() != 1 );

        for (int f = 0; f
                < fractionDeleteButtons.size(); f ++) {
            myHorizFraction.add( jPanel2Layout.createSequentialGroup()//
                    .add( 4, 4, 4 ) // left-hand margin
                    .add( fractionDeleteButtons.get( f ), 50, 50, 50 ) //
                    .add( 3, 3, 3 )//
                    .add( fractionEditButtons.get( f ), 100, 100, 100 ) //
                    .add( 5, 5, 5 )//
                    .add( fractionR206_238r_Text.get( f ), 100, 100, 100 ) //
                    .add( 5, 5, 5 )//
                    .add( fractionR206_238r2SigmaPct_Text.get( f ), 60, 60, 60 )//
                    .add( 5, 5, 5 )//
                    .add( fractionR207_235r_Text.get( f ), 100, 100, 100 ) //
                    .add( 5, 5, 5 )//
                    .add( fractionR207_235r2SigmaPct_Text.get( f ), 60, 60, 60 )//
                    .add( 5, 5, 5 )//
                    .add( fractionR207_206r_Text.get( f ), 100, 100, 100 ) //
                    .add( 5, 5, 5 )//
                    .add( fractionR207_206r2SigmaPct_Text.get( f ), 60, 60, 60 )//
                    .add( 5, 5, 5 )//
                    .add( fractionRhoR206_238r__r207_235r_Text.get( f ), 100, 100, 100 ) //
                    .add( 5, 5, 5 )//
                    .add( fractionR206_204tfc_Text.get( f ), 100, 100, 100 ) //
                    .add( 5, 5, 5 )//
                    .add( fractionR208_206r_Text.get( f ), 100, 100, 100 ) //
                    .add( 5, 5, 5 )//
                    .add( fractionRadToCommonTotal_Text.get( f ), 100, 100, 100 ) //
                    .add( 10, 10, 10 )//
                    .add( fractionTotRadiogenicPbMass_Text.get( f ), 80, 80, 80 )//
                    .add( fractionTotRadiogenicPbMassPICOGRAMS.get( f ) )//
                    .add( 5, 5, 5 )//
                    .add( fractionTotCommonPbMass_Text.get( f ), 60, 60, 60 )//
                    .add( fractionTotCommonPbMassPICOGRAMS.get( f ) )//
                    .add( 5, 5, 5 )//
                    .add( fractionConcU_Text.get( f ), 60, 60, 60 )//
                    .add( fractionConcUPPM.get( f ) )//
                    .add( 5, 5, 5 )//
                    .add( fractionConcPb_ib_Text.get( f ), 60, 60, 60 )//
                    .add( fractionConcPb_ibPPM.get( f ) )//
                    .add( 5, 5, 5 )//
                    .add( fractionRTh_Usample_Text.get( f ), 100, 100, 100 ) //
                    .add( 10, 10, 10 )//
                    .add( fractionMass_Text.get( f ), 100, 100, 100 ) //
                    .add( 10, 10, 10 )//
                    .add( fractionDate206_238r_Text.get( f ), 100, 100, 100 ) //
                    .add( 5, 5, 5 )//
                    .add( fractionDate206_238r2SigmaAbs_Text.get( f ), 60, 60, 60 )//
                    .add( 5, 5, 5 )//
                    .add( fractionDate207_235r_Text.get( f ), 100, 100, 100 ) //
                    .add( 5, 5, 5 )//
                    .add( fractionDate207_235r2SigmaAbs_Text.get( f ), 60, 60, 60 )//
                    .add( 5, 5, 5 )//
                    .add( fractionDate207_206r_Text.get( f ), 100, 100, 100 ) //
                    .add( 5, 5, 5 )//
                    .add( fractionDate207_206r2SigmaAbs_Text.get( f ), 60, 60, 60 )//
                    .add( 5, 5, 5 )//
                    .add( fractionDate206_238r_Th_Text.get( f ), 100, 100, 100 ) //
                    .add( 5, 5, 5 )//
                    .add( fractionDate206_238r_Th2SigmaAbs_Text.get( f ), 60, 60, 60 )//
                    .add( 5, 5, 5 )//
                    .add( fractionDate207_235r_Pa_Text.get( f ), 100, 100, 100 ) //
                    .add( 5, 5, 5 )//
                    .add( fractionDate207_235r_Pa2SigmaAbs_Text.get( f ), 60, 60, 60 )//
                    .add( 5, 5, 5 )//
                    .add( fractionDate207_206r_Th_Text.get( f ), 100, 100, 100 ) //
                    .add( 5, 5, 5 )//
                    .add( fractionDate207_206r_Th2SigmaAbs_Text.get( f ), 60, 60, 60 )//
                    .add( 5, 5, 5 )//
                    .add( fractionDate207_206r_Pa_Text.get( f ), 100, 100, 100 ) //
                    .add( 5, 5, 5 )//
                    .add( fractionDate207_206r_Pa2SigmaAbs_Text.get( f ), 60, 60, 60 )//
                    .add( 5, 5, 5 )//
                    );

            myVerticalFraction//
                    .add( jPanel2Layout.createParallelGroup( org.jdesktop.layout.GroupLayout.TRAILING )//
                    .add( fractionDeleteButtons.get( f ), 22, 22, 22 )//
                    .add( fractionEditButtons.get( f ), 22, 22, 22 )//
                    .add( fractionR206_238r_Text.get( f ), 22, 22, 22 )//
                    .add( fractionR206_238r2SigmaPct_Text.get( f ), 22, 22, 22 )//
                    .add( fractionR207_235r_Text.get( f ), 22, 22, 22 )//
                    .add( fractionR207_235r2SigmaPct_Text.get( f ), 22, 22, 22 )//
                    .add( fractionR207_206r_Text.get( f ), 22, 22, 22 )//
                    .add( fractionR207_206r2SigmaPct_Text.get( f ), 22, 22, 22 )//
                    //                    .add(fractionR238_206r_Text.get(f), 22, 22, 22)//
                    //                    .add(fractionR238_206r2SigmaPct_Text.get(f), 22, 22, 22)//
                    .add( fractionRhoR206_238r__r207_235r_Text.get( f ), 22, 22, 22 )//
                    .add( fractionR206_204tfc_Text.get( f ), 22, 22, 22 )//
                    .add( fractionR208_206r_Text.get( f ), 22, 22, 22 )//
                    .add( fractionRadToCommonTotal_Text.get( f ), 22, 22, 22 )//
                    .add( fractionTotRadiogenicPbMass_Text.get( f ), 22, 22, 22 )//
                    .add( fractionTotRadiogenicPbMassPICOGRAMS.get( f ) )//
                    .add( fractionTotCommonPbMass_Text.get( f ), 22, 22, 22 )//
                    .add( fractionTotCommonPbMassPICOGRAMS.get( f ) )//
                    .add( fractionConcU_Text.get( f ), 22, 22, 22 )//
                    .add( fractionConcUPPM.get( f ) )//
                    .add( fractionConcPb_ib_Text.get( f ), 22, 22, 22 )//
                    .add( fractionConcPb_ibPPM.get( f ) )//
                    .add( fractionRTh_Usample_Text.get( f ), 22, 22, 22 )//
                    .add( fractionMass_Text.get( f ), 22, 22, 22 )//
                    .add( fractionDate206_238r_Text.get( f ), 22, 22, 22 )//
                    .add( fractionDate206_238r2SigmaAbs_Text.get( f ), 22, 22, 22 )//
                    .add( fractionDate207_235r_Text.get( f ), 22, 22, 22 )//
                    .add( fractionDate207_235r2SigmaAbs_Text.get( f ), 22, 22, 22 )//
                    .add( fractionDate207_206r_Text.get( f ), 22, 22, 22 )//
                    .add( fractionDate207_206r2SigmaAbs_Text.get( f ), 22, 22, 22 )//
                    .add( fractionDate206_238r_Th_Text.get( f ), 22, 22, 22 )//
                    .add( fractionDate206_238r_Th2SigmaAbs_Text.get( f ), 22, 22, 22 )//
                    .add( fractionDate207_235r_Pa_Text.get( f ), 22, 22, 22 )//
                    .add( fractionDate207_235r_Pa2SigmaAbs_Text.get( f ), 22, 22, 22 )//
                    .add( fractionDate207_206r_Th_Text.get( f ), 22, 22, 22 )//
                    .add( fractionDate207_206r_Th2SigmaAbs_Text.get( f ), 22, 22, 22 )//
                    .add( fractionDate207_206r_Pa_Text.get( f ), 22, 22, 22 )//
                    .add( fractionDate207_206r_Pa2SigmaAbs_Text.get( f ), 22, 22, 22 )//
                    );
        }

        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup( org.jdesktop.layout.GroupLayout.LEADING ).add( jPanel2Layout.createSequentialGroup().add( myHorizFraction ) ) );

        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup( org.jdesktop.layout.GroupLayout.LEADING ).add( myVerticalFraction ) );


    }

    /**
     * 
     */
    @Override
    protected void showSavedDataII () {

        showSavedDataI();

        // fraction details

        for (int row = 0; row
                < getMyAliquot().getAliquotFractions().size(); row ++) {
            updateFractionRow( getMyAliquot().getAliquotFractions().get( row ), row );
        }


    }

    private void updateFractionRow ( Fraction tempFrac, int row ) {

        ((JButton) fractionEditButtons.get( row )).setText( tempFrac.getFractionID() );

        // radiogenic isotopic ratios
        ((JTextField) fractionR206_238r_Text.get( row )).setText(tempFrac.getRadiogenicIsotopeRatioByName( "r206_238r" ).getValue().
                setScale(ReduxConstants.DEFAULT_DEFAULTS_SCALE,
                RoundingMode.HALF_UP ).toPlainString() );
        ((JTextField) fractionR206_238r2SigmaPct_Text.get( row )).setText(tempFrac.getRadiogenicIsotopeRatioByName( "r206_238r" ).getTwoSigmaPct().
                setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE,
                RoundingMode.HALF_UP ).toPlainString() );

        ((JTextField) fractionR207_235r_Text.get( row )).setText(tempFrac.getRadiogenicIsotopeRatioByName( "r207_235r" ).getValue().
                setScale(ReduxConstants.DEFAULT_DEFAULTS_SCALE,
                RoundingMode.HALF_UP ).toPlainString() );
        ((JTextField) fractionR207_235r2SigmaPct_Text.get( row )).setText(tempFrac.getRadiogenicIsotopeRatioByName( "r207_235r" ).getTwoSigmaPct().
                setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE,
                RoundingMode.HALF_UP ).toPlainString() );

        ((JTextField) fractionR207_206r_Text.get( row )).setText(tempFrac.getRadiogenicIsotopeRatioByName( "r207_206r" ).getValue().
                setScale(ReduxConstants.DEFAULT_DEFAULTS_SCALE,
                RoundingMode.HALF_UP ).toPlainString() );
        ((JTextField) fractionR207_206r2SigmaPct_Text.get( row )).setText(tempFrac.getRadiogenicIsotopeRatioByName( "r207_206r" ).getTwoSigmaPct().
                setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE,
                RoundingMode.HALF_UP ).toPlainString() );

        ((JTextField) fractionRhoR206_238r__r207_235r_Text.get( row )).setText(tempFrac.getRadiogenicIsotopeRatioByName( "rhoR206_238r__r207_235r" ).getValue().
                setScale(ReduxConstants.DEFAULT_DEFAULTS_SCALE,
                RoundingMode.HALF_UP ).toPlainString() );

        ((JTextField) fractionR206_204tfc_Text.get( row )).setText(tempFrac.getSampleIsochronRatiosByName( "r206_204tfc" ).getValue().
                setScale(ReduxConstants.DEFAULT_DEFAULTS_SCALE,
                RoundingMode.HALF_UP ).toPlainString() );

        ((JTextField) fractionR208_206r_Text.get( row )).setText(tempFrac.getRadiogenicIsotopeRatioByName( "r208_206r" ).getValue().
                setScale(ReduxConstants.DEFAULT_DEFAULTS_SCALE,
                RoundingMode.HALF_UP ).toPlainString() );




        // Composition
        ((JTextField) fractionRadToCommonTotal_Text.get( row )).setText(tempFrac.getCompositionalMeasureByName( "radToCommonTotal" ).getValue().
                setScale(ReduxConstants.DEFAULT_DEFAULTS_SCALE,
                RoundingMode.HALF_UP ).toPlainString() );

        ((JTextField) fractionTotRadiogenicPbMass_Text.get( row )).setText(tempFrac.getCompositionalMeasureByName( "totRadiogenicPbMass" ).getValue().multiply(ReduxConstants.PicoGramsPerGram ).setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE,
                RoundingMode.HALF_UP ).toPlainString() );

        ((JTextField) fractionTotCommonPbMass_Text.get( row )).setText(((UPbFractionI) tempFrac).getCompositionalMeasureByName( "totCommonPbMass" ).getValue().multiply(ReduxConstants.PicoGramsPerGram ).setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE,
                RoundingMode.HALF_UP ).toPlainString() );

        ((JTextField) fractionConcU_Text.get( row )).setText(
                tempFrac.getCompositionalMeasureByName( "concU" ).getValue().
                movePointRight( 6 ).setScale( 1,
                RoundingMode.HALF_UP ).toPlainString() );

        ((JTextField) fractionConcPb_ib_Text.get( row )).setText(
                tempFrac.getCompositionalMeasureByName( "concPb_ib" ).getValue().
                movePointRight( 6 ).setScale( 3,
                RoundingMode.HALF_UP ).toPlainString() );

        ((JTextField) fractionRTh_Usample_Text.get( row )).setText(tempFrac.getCompositionalMeasureByName( "rTh_Usample" ).getValue().
                setScale(ReduxConstants.DEFAULT_DEFAULTS_SCALE,
                RoundingMode.HALF_UP ).toPlainString() );

        ((JTextField) fractionMass_Text.get( row )).setText(tempFrac.getAnalysisMeasure( AnalysisMeasures.fractionMass.getName() ).getValue().
                setScale(ReduxConstants.DEFAULT_DEFAULTS_SCALE,
                RoundingMode.HALF_UP ).toPlainString() );

        // Isotopic Dates


        ((JTextField) fractionDate206_238r_Text.get( row )).setText(tempFrac.getRadiogenicIsotopeDateByName( RadDates.age206_238r ).getValue().
                movePointLeft( 6 ).setScale(ReduxConstants.DEFAULT_DEFAULTS_SCALE,
                RoundingMode.HALF_UP ).toPlainString() );

        ((JTextField) fractionDate206_238r2SigmaAbs_Text.get( row )).setText(tempFrac.getRadiogenicIsotopeDateByName( RadDates.age206_238r ).getTwoSigmaAbs().
                movePointLeft( 6 ).setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE,
                RoundingMode.HALF_UP ).toPlainString() );

        ((JTextField) fractionDate207_235r_Text.get( row )).setText(tempFrac.getRadiogenicIsotopeDateByName( RadDates.age207_235r ).getValue().
                movePointLeft( 6 ).setScale(ReduxConstants.DEFAULT_DEFAULTS_SCALE,
                RoundingMode.HALF_UP ).toPlainString() );

        ((JTextField) fractionDate207_235r2SigmaAbs_Text.get( row )).setText(tempFrac.getRadiogenicIsotopeDateByName( RadDates.age207_235r ).getTwoSigmaAbs().
                movePointLeft( 6 ).setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE,
                RoundingMode.HALF_UP ).toPlainString() );

        ((JTextField) fractionDate207_206r_Text.get( row )).setText(tempFrac.getRadiogenicIsotopeDateByName( RadDates.age207_206r ).getValue().
                movePointLeft( 6 ).setScale(ReduxConstants.DEFAULT_DEFAULTS_SCALE,
                RoundingMode.HALF_UP ).toPlainString() );

        ((JTextField) fractionDate207_206r2SigmaAbs_Text.get( row )).setText(tempFrac.getRadiogenicIsotopeDateByName( RadDates.age207_206r ).getTwoSigmaAbs().
                movePointLeft( 6 ).setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE,
                RoundingMode.HALF_UP ).toPlainString() );

        ((JTextField) fractionDate206_238r_Th_Text.get( row )).setText(tempFrac.getRadiogenicIsotopeDateByName( RadDates.age206_238r_Th ).getValue().
                movePointLeft( 6 ).setScale(ReduxConstants.DEFAULT_DEFAULTS_SCALE,
                RoundingMode.HALF_UP ).toPlainString() );

        ((JTextField) fractionDate206_238r_Th2SigmaAbs_Text.get( row )).setText(tempFrac.getRadiogenicIsotopeDateByName( RadDates.age206_238r_Th ).getTwoSigmaAbs().
                movePointLeft( 6 ).setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE,
                RoundingMode.HALF_UP ).toPlainString() );

        ((JTextField) fractionDate207_235r_Pa_Text.get( row )).setText(tempFrac.getRadiogenicIsotopeDateByName( RadDates.age207_235r_Pa ).getValue().
                movePointLeft( 6 ).setScale(ReduxConstants.DEFAULT_DEFAULTS_SCALE,
                RoundingMode.HALF_UP ).toPlainString() );

        ((JTextField) fractionDate207_235r_Pa2SigmaAbs_Text.get( row )).setText(tempFrac.getRadiogenicIsotopeDateByName( RadDates.age207_235r_Pa ).getTwoSigmaAbs().
                movePointLeft( 6 ).setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE,
                RoundingMode.HALF_UP ).toPlainString() );

        ((JTextField) fractionDate207_206r_Th_Text.get( row )).setText(tempFrac.getRadiogenicIsotopeDateByName( RadDates.age207_206r_Th ).getValue().
                movePointLeft( 6 ).setScale(ReduxConstants.DEFAULT_DEFAULTS_SCALE,
                RoundingMode.HALF_UP ).toPlainString() );

        ((JTextField) fractionDate207_206r_Th2SigmaAbs_Text.get( row )).setText(tempFrac.getRadiogenicIsotopeDateByName( RadDates.age207_206r_Th ).getTwoSigmaAbs().
                movePointLeft( 6 ).setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE,
                RoundingMode.HALF_UP ).toPlainString() );

        ((JTextField) fractionDate207_206r_Pa_Text.get( row )).setText(tempFrac.getRadiogenicIsotopeDateByName( RadDates.age207_206r_Pa.getName() ).getValue().
                movePointLeft( 6 ).setScale(ReduxConstants.DEFAULT_DEFAULTS_SCALE,
                RoundingMode.HALF_UP ).toPlainString() );

        ((JTextField) fractionDate207_206r_Pa2SigmaAbs_Text.get( row )).setText(tempFrac.getRadiogenicIsotopeDateByName( RadDates.age207_206r_Pa.getName() ).getTwoSigmaAbs().
                movePointLeft( 6 ).setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE,
                RoundingMode.HALF_UP ).toPlainString() );

    }

    private void removeFractionRow ( int row ) {
        fractionDeleteButtons.remove( row );
        fractionEditButtons.remove( row );
        fractionR206_238r_Text.remove( row );
        fractionR206_238r2SigmaPct_Text.remove( row );
        fractionR207_235r_Text.remove( row );
        fractionR207_235r2SigmaPct_Text.remove( row );
        fractionR207_206r_Text.remove( row );
        fractionR207_206r2SigmaPct_Text.remove( row );
        fractionRhoR206_238r__r207_235r_Text.remove( row );
        fractionR206_204tfc_Text.remove( row );
        fractionR208_206r_Text.remove( row );

        fractionRadToCommonTotal_Text.remove( row );
        fractionTotRadiogenicPbMass_Text.remove( row );
        fractionTotCommonPbMass_Text.remove( row );

        fractionConcU_Text.remove( row );
        fractionConcPb_ib_Text.remove( row );
        fractionRTh_Usample_Text.remove( row );
        fractionMass_Text.remove( row );

        fractionDate206_238r_Text.remove( row );
        fractionDate206_238r2SigmaAbs_Text.remove( row );
        fractionDate207_235r_Text.remove( row );
        fractionDate207_235r2SigmaAbs_Text.remove( row );
        fractionDate207_206r_Text.remove( row );
        fractionDate207_206r2SigmaAbs_Text.remove( row );

        fractionDate206_238r_Th_Text.remove( row );
        fractionDate206_238r_Th2SigmaAbs_Text.remove( row );
        fractionDate207_235r_Pa_Text.remove( row );
        fractionDate207_235r_Pa2SigmaAbs_Text.remove( row );
        fractionDate207_206r_Th_Text.remove( row );
        fractionDate207_206r_Th2SigmaAbs_Text.remove( row );
        fractionDate207_206r_Pa_Text.remove( row );
        fractionDate207_206r_Pa2SigmaAbs_Text.remove( row );


        // fix row pointers in buttons
        for (int f = 0; f
                < fractionDeleteButtons.size(); f ++) {
            Fraction myFraction =
                    ((deleteFractionListener) ((JButton) fractionDeleteButtons.get( f )).getActionListeners()[0]).getFraction();

            ((JButton) fractionDeleteButtons.get( f )).removeActionListener( ((JButton) fractionDeleteButtons.get( f )).getActionListeners()[0] );
            ((JButton) fractionDeleteButtons.get( f )).addActionListener( new deleteFractionListener( myFraction, f ) );

//            ((JButton) fractionEditButtons.get(f)).removeActionListener(((JButton) fractionEditButtons.get(f)).getActionListeners()[0]);
//            ((JButton) fractionEditButtons.get(f)).addActionListener(new editFractionListener(myFraction, f));

        }

    }

    private void addNewFractionRow ( Fraction fraction ) {
        int row = fractionDeleteButtons.size();

        addFractionRow( fraction, row, row + 1 );
        updateFractionRow( fraction, row );

        // update the keystroke actionlisteners for previous row in table
        modifyComponentKeyMapForTable( fractionEditButtons.get( row - 1 ), fractionEditButtons, row + 1 );
        modifyComponentKeyMapForTable( fractionR206_238r_Text.get( row - 1 ), fractionR206_238r_Text, row + 1 );
        modifyComponentKeyMapForTable( fractionR206_238r2SigmaPct_Text.get( row - 1 ), fractionR206_238r2SigmaPct_Text, row + 1 );

        modifyComponentKeyMapForTable( fractionR207_235r_Text.get( row - 1 ), fractionR207_235r_Text, row + 1 );
        modifyComponentKeyMapForTable( fractionR207_235r2SigmaPct_Text.get( row - 1 ), fractionR207_235r2SigmaPct_Text, row + 1 );

        modifyComponentKeyMapForTable( fractionR207_206r_Text.get( row - 1 ), fractionR207_206r_Text, row + 1 );
        modifyComponentKeyMapForTable( fractionR207_206r2SigmaPct_Text.get( row - 1 ), fractionR207_206r2SigmaPct_Text, row + 1 );

//        modifyComponentKeyMapForTable(fractionR238_206r_Text.get(row - 1), fractionR238_206r_Text, row + 1);
//        modifyComponentKeyMapForTable(fractionR238_206r2SigmaPct_Text.get(row - 1), fractionR238_206r2SigmaPct_Text, row + 1);

        modifyComponentKeyMapForTable( fractionRhoR206_238r__r207_235r_Text.get( row - 1 ), fractionRhoR206_238r__r207_235r_Text, row + 1 );
        modifyComponentKeyMapForTable( fractionR206_204tfc_Text.get( row - 1 ), fractionR206_204tfc_Text, row + 1 );
        modifyComponentKeyMapForTable( fractionR208_206r_Text.get( row - 1 ), fractionR208_206r_Text, row + 1 );

        modifyComponentKeyMapForTable( fractionRadToCommonTotal_Text.get( row - 1 ), fractionRadToCommonTotal_Text, row + 1 );
        modifyComponentKeyMapForTable( fractionTotRadiogenicPbMass_Text.get( row - 1 ), fractionTotRadiogenicPbMass_Text, row + 1 );
        modifyComponentKeyMapForTable( fractionTotCommonPbMass_Text.get( row - 1 ), fractionTotCommonPbMass_Text, row + 1 );

        modifyComponentKeyMapForTable( fractionConcU_Text.get( row - 1 ), fractionConcU_Text, row + 1 );
        modifyComponentKeyMapForTable( fractionConcPb_ib_Text.get( row - 1 ), fractionConcPb_ib_Text, row + 1 );
        modifyComponentKeyMapForTable( fractionRTh_Usample_Text.get( row - 1 ), fractionRTh_Usample_Text, row + 1 );
        modifyComponentKeyMapForTable( fractionMass_Text.get( row - 1 ), fractionMass_Text, row + 1 );
        modifyComponentKeyMapForTable( fractionDate206_238r_Text.get( row - 1 ), fractionDate206_238r_Text, row + 1 );
        modifyComponentKeyMapForTable( fractionDate206_238r2SigmaAbs_Text.get( row - 1 ), fractionDate206_238r2SigmaAbs_Text, row + 1 );
        modifyComponentKeyMapForTable( fractionDate207_235r_Text.get( row - 1 ), fractionDate207_235r_Text, row + 1 );
        modifyComponentKeyMapForTable( fractionDate207_235r2SigmaAbs_Text.get( row - 1 ), fractionDate207_235r2SigmaAbs_Text, row + 1 );

        modifyComponentKeyMapForTable( fractionDate207_206r_Text.get( row - 1 ), fractionDate207_206r_Text, row + 1 );
        modifyComponentKeyMapForTable( fractionDate207_206r2SigmaAbs_Text.get( row - 1 ), fractionDate207_206r2SigmaAbs_Text, row + 1 );
        modifyComponentKeyMapForTable( fractionDate206_238r_Th_Text.get( row - 1 ), fractionDate206_238r_Th_Text, row + 1 );
        modifyComponentKeyMapForTable( fractionDate206_238r_Th2SigmaAbs_Text.get( row - 1 ), fractionDate206_238r_Th2SigmaAbs_Text, row + 1 );
        modifyComponentKeyMapForTable( fractionDate207_235r_Pa_Text.get( row - 1 ), fractionDate207_235r_Pa_Text, row + 1 );
        modifyComponentKeyMapForTable( fractionDate207_235r_Pa2SigmaAbs_Text.get( row - 1 ), fractionDate207_235r_Pa2SigmaAbs_Text, row + 1 );
        modifyComponentKeyMapForTable( fractionDate207_206r_Th_Text.get( row - 1 ), fractionDate207_206r_Th_Text, row + 1 );
        modifyComponentKeyMapForTable( fractionDate207_206r_Th2SigmaAbs_Text.get( row - 1 ), fractionDate207_206r_Th2SigmaAbs_Text, row + 1 );
        modifyComponentKeyMapForTable( fractionDate207_206r_Pa_Text.get( row - 1 ), fractionDate207_206r_Pa_Text, row + 1 );
        modifyComponentKeyMapForTable( fractionDate207_206r_Pa2SigmaAbs_Text.get( row - 1 ), fractionDate207_206r_Pa2SigmaAbs_Text, row + 1 );

        buildFastEditDisplayPanel();

    }

    private class editFractionListener implements ActionListener {

        private int row;
        private Fraction fraction;

        public editFractionListener ( Fraction fraction, int row ) {
            this.row = row;
            this.fraction = fraction;
        }

        public void actionPerformed ( ActionEvent e ) {
            // prompt for save if aliquot edited
            boolean proceed = true;

            if ( deletedFractions.size() + addedFractions.size() > 0 ) {
                int result =
                        JOptionPane.showConfirmDialog(
                        null,
                        new String[]{"You must first save the Aliquot ... proceed?"},
                        "ET Redux Warning",
                        JOptionPane.WARNING_MESSAGE );
                if ( result == JOptionPane.OK_OPTION ) {
                    proceed = true;
                } else {
                    proceed = false;
                }
            }

            if ( proceed ) {
                saveAliquot();
                saveAliquotFraction( fraction );
                parent.editFraction( fraction, 8 );
                updateFractionRow(
                        fraction,
                        getMyAliquot().getAliquotFractions().indexOf( fraction ) );
            }

        }
    }

    private class deleteFractionListener implements ActionListener {

        private int row;
        private Fraction fraction;

        public deleteFractionListener ( Fraction fraction, int row ) {
            this.row = row;
            this.fraction = fraction;
        }

        public Fraction getFraction () {
            return fraction;
        }

        public void actionPerformed ( ActionEvent e ) {
            // check to see if pending as added fraction or existing fraction
            if ( row < getMyAliquot().getAliquotFractions().size() ) {
                deletedFractions.add( fraction );
            } else {
                addedFractions.remove( fraction );
            }

            removeFractionRow( row );
            buildFastEditDisplayPanel();
        }
    }

    private void saveAliquot () {

        // general info
        getMyAliquot().setAliquotName( aliquotName_text.getText() );
        this.setTitle( "Aliquot # " + getMyAliquot().getAliquotNumber() + " <> " + getMyAliquot().getAliquotName() );

        getMyAliquot().setAnalystName( analystName_text.getText() );
        ((UPbReduxAliquot) getMyAliquot()).getMyReduxLabData().setAnalystName( analystName_text.getText() );
        getMyAliquot().setAliquotInstrumentalMethodReference( instMethodRef_text.getText() );
        getMyAliquot().setAliquotReference( reference_text.getText() );
        getMyAliquot().setAliquotComment( comment_textArea.getText() );



        // calibration and mineral standards

        getMyAliquot().getMineralStandardModels().clear();
        for (JComponent cb : mineralStandardsCheckBoxes) {
            if ( ((JCheckBox) cb).isSelected() ) {
                try {
                    getMyAliquot().getMineralStandardModels().add(//
                            ReduxLabData.getInstance().getAMineralStandardModel( ((JCheckBox) cb).getText() ) );
                } catch (BadLabDataException ex) {
                    new ETWarningDialog(ex).setVisible(true);
                }
            }
        }

        // handle deleted fractions
        for (int f = 0; f
                < deletedFractions.size(); f ++) {
            getSample().removeUPbReduxFraction( (UPbFraction) deletedFractions.get( f ) );
            getMyAliquot().getAliquotFractions().remove( deletedFractions.get( f ) );
        }

        deletedFractions.clear();

        // handle added fractions
        for (int f = 0; f
                < addedFractions.size(); f ++) {
            getSample().addFraction( (UPbFraction) addedFractions.get( f ) );
            getMyAliquot().getAliquotFractions().add( addedFractions.get( f ) );
        }

        addedFractions.clear();

        // master fields

        for (Fraction f : getMyAliquot().getAliquotFractions()) {
            saveAliquotFraction( f );
        }

        // save the sample
        SampleInterface.saveSampleAsSerializedReduxFile(sample);

        System.out.println( "**************** PRE-PUBLISH CHECKLIST FOR ALIQUOT" );

    }

    private void saveAliquotFraction ( Fraction tempFrac )
            throws NumberFormatException {

        int row = getMyAliquot().getAliquotFractions().indexOf( tempFrac );
        // april 2009

        // radiogenic isotopic ratios
        tempFrac.getRadiogenicIsotopeRatioByName( "r206_238r" )//
                .setValue(new BigDecimal( ((JTextField) fractionR206_238r_Text.get( row )).getText(), ReduxConstants.mathContext15 ) );
        ValueModel ratio = tempFrac.getRadiogenicIsotopeRatioByName( "r206_238r" );
        BigDecimal oneSigmaPct = new BigDecimal( ((JTextField) fractionR206_238r2SigmaPct_Text.get( row )).getText(), ReduxConstants.mathContext15 ).//
                divide(new BigDecimal( 2.0 ), ReduxConstants.mathContext15 );
        tempFrac.getRadiogenicIsotopeRatioByName( "r206_238r" )//
                .setOneSigma( ValueModel.convertOneSigmaPctToAbsIfRequired( ratio, oneSigmaPct ) );

        tempFrac.getRadiogenicIsotopeRatioByName( "r207_235r" )//
                .setValue(new BigDecimal( ((JTextField) fractionR207_235r_Text.get( row )).getText(), ReduxConstants.mathContext15 ) );
        ratio = tempFrac.getRadiogenicIsotopeRatioByName( "r207_235r" );
        oneSigmaPct = new BigDecimal( ((JTextField) fractionR207_235r2SigmaPct_Text.get( row )).getText() ).//
                divide(new BigDecimal( 2.0, ReduxConstants.mathContext15 ), ReduxConstants.mathContext15 );
        tempFrac.getRadiogenicIsotopeRatioByName( "r207_235r" )//
                .setOneSigma( ValueModel.convertOneSigmaPctToAbsIfRequired( ratio, oneSigmaPct ) );

        tempFrac.getRadiogenicIsotopeRatioByName( "r207_206r" ).//
                setValue(new BigDecimal( ((JTextField) fractionR207_206r_Text.get( row )).getText(), ReduxConstants.mathContext15 ) );
        ratio = tempFrac.getRadiogenicIsotopeRatioByName( "r207_206r" );
        oneSigmaPct = new BigDecimal( ((JTextField) fractionR207_206r2SigmaPct_Text.get( row )).getText(), ReduxConstants.mathContext15 ).//
                divide(new BigDecimal( 2.0, ReduxConstants.mathContext15 ), ReduxConstants.mathContext15 );
        tempFrac.getRadiogenicIsotopeRatioByName( "r207_206r" )//
                .setOneSigma( ValueModel.convertOneSigmaPctToAbsIfRequired( ratio, oneSigmaPct ) );

        tempFrac.getRadiogenicIsotopeRatioByName( "r238_206r" )//
                .setOneSigma( ValueModel.convertOneSigmaPctToAbsIfRequired( ratio, oneSigmaPct ) );

        tempFrac.getRadiogenicIsotopeRatioByName( "rhoR206_238r__r207_235r" )//
                .setValue(new BigDecimal( ((JTextField) fractionRhoR206_238r__r207_235r_Text.get( row )).getText(), ReduxConstants.mathContext15 ) );

        tempFrac.getSampleIsochronRatiosByName( "r206_204tfc" )//
                .setValue(new BigDecimal( ((JTextField) fractionR206_204tfc_Text.get( row )).getText(), ReduxConstants.mathContext15 ) );

        tempFrac.getRadiogenicIsotopeRatioByName( "r208_206r" )//
                .setValue(new BigDecimal( ((JTextField) fractionR208_206r_Text.get( row )).getText(), ReduxConstants.mathContext15 ) );





        // Composition
        tempFrac.getCompositionalMeasureByName( "radToCommonTotal" ).//
                setValue(new BigDecimal( ((JTextField) fractionRadToCommonTotal_Text.get( row )).getText(), ReduxConstants.mathContext15 ) );

        tempFrac.getCompositionalMeasureByName( "totRadiogenicPbMass" )//
                .setValue(new BigDecimal( ((JTextField) fractionTotRadiogenicPbMass_Text.get( row )).getText(), ReduxConstants.mathContext15 ).//
                movePointLeft( 12 ) );

        ((UPbFractionI) tempFrac).getCompositionalMeasureByName( "totCommonPbMass" )//
                .setValue(new BigDecimal( ((JTextField) fractionTotCommonPbMass_Text.get( row )).getText(), ReduxConstants.mathContext15 ).//
                movePointLeft( 12 ) );

        tempFrac.getCompositionalMeasureByName( "concU" )//
                .setValue(new BigDecimal( ((JTextField) fractionConcU_Text.get( row )).getText(), ReduxConstants.mathContext15 ).//
                movePointLeft( 6 ) );

        tempFrac.getCompositionalMeasureByName( "concPb_ib" )//
                .setValue(new BigDecimal( ((JTextField) fractionConcPb_ib_Text.get( row )).getText(), ReduxConstants.mathContext15 ).//
                movePointLeft( 6 ) );

        tempFrac.getCompositionalMeasureByName( "rTh_Usample" )//
                .setValue(new BigDecimal( ((JTextField) fractionRTh_Usample_Text.get( row )).getText(), ReduxConstants.mathContext15 ) );

        tempFrac.getAnalysisMeasure( AnalysisMeasures.fractionMass.getName() )//
                .setValue(new BigDecimal( ((JTextField) fractionMass_Text.get( row )).getText(), ReduxConstants.mathContext15 ) );

        // Isotopic Dates
        tempFrac.getRadiogenicIsotopeDateByName( RadDates.age206_238r )//
                .setValue(new BigDecimal( ((JTextField) fractionDate206_238r_Text.get( row )).getText(), ReduxConstants.mathContext15 ).//
                movePointRight( 6 ) );
        tempFrac.getRadiogenicIsotopeDateByName( RadDates.age206_238r )//
                .setOneSigma(new BigDecimal( ((JTextField) fractionDate206_238r2SigmaAbs_Text.get( row )).getText(), ReduxConstants.mathContext15 ).//
                divide(new BigDecimal( 2.0, ReduxConstants.mathContext15 ) ).movePointRight( 6 ) );

        tempFrac.getRadiogenicIsotopeDateByName( RadDates.age207_235r )//
                .setValue(new BigDecimal( ((JTextField) fractionDate207_235r_Text.get( row )).getText(), ReduxConstants.mathContext15 ).//
                movePointRight( 6 ) );
        tempFrac.getRadiogenicIsotopeDateByName( RadDates.age207_235r )//
                .setOneSigma(new BigDecimal( ((JTextField) fractionDate207_235r2SigmaAbs_Text.get( row )).getText(), ReduxConstants.mathContext15 ).//
                divide(new BigDecimal( 2.0, ReduxConstants.mathContext15 ) ).movePointRight( 6 ) );

        tempFrac.getRadiogenicIsotopeDateByName( RadDates.age207_206r )//
                .setValue(new BigDecimal( ((JTextField) fractionDate207_206r_Text.get( row )).getText(), ReduxConstants.mathContext15 ).//
                movePointRight( 6 ) );
        tempFrac.getRadiogenicIsotopeDateByName( RadDates.age207_206r )//
                .setOneSigma(new BigDecimal( ((JTextField) fractionDate207_206r2SigmaAbs_Text.get( row )).getText(), ReduxConstants.mathContext15 ).//
                divide(new BigDecimal( 2.0, ReduxConstants.mathContext15 ) ).movePointRight( 6 ) );

        tempFrac.getRadiogenicIsotopeDateByName( RadDates.age206_238r_Th )//
                .setValue(new BigDecimal( ((JTextField) fractionDate206_238r_Th_Text.get( row )).getText(), ReduxConstants.mathContext15 ).//
                movePointRight( 6 ) );
        tempFrac.getRadiogenicIsotopeDateByName( RadDates.age206_238r_Th )//
                .setOneSigma(new BigDecimal( ((JTextField) fractionDate206_238r_Th2SigmaAbs_Text.get( row )).getText(), ReduxConstants.mathContext15 ).//
                divide(new BigDecimal( 2.0, ReduxConstants.mathContext15 ) ).movePointRight( 6 ) );

        tempFrac.getRadiogenicIsotopeDateByName( RadDates.age207_235r_Pa )//
                .setValue(new BigDecimal( ((JTextField) fractionDate207_235r_Pa_Text.get( row )).getText(), ReduxConstants.mathContext15 ).//
                movePointRight( 6 ) );
        tempFrac.getRadiogenicIsotopeDateByName( RadDates.age207_235r_Pa )//
                .setOneSigma(new BigDecimal( ((JTextField) fractionDate207_235r_Pa2SigmaAbs_Text.get( row )).getText(), ReduxConstants.mathContext15 ).//
                divide(new BigDecimal( 2.0, ReduxConstants.mathContext15 ) ).movePointRight( 6 ) );


        tempFrac.getRadiogenicIsotopeDateByName( RadDates.age207_206r_Th )//
                .setValue(new BigDecimal( ((JTextField) fractionDate207_206r_Th_Text.get( row )).getText(), ReduxConstants.mathContext15 ).//
                movePointRight( 6 ) );
        tempFrac.getRadiogenicIsotopeDateByName( RadDates.age207_206r_Th )//
                .setOneSigma(new BigDecimal( ((JTextField) fractionDate207_206r_Th2SigmaAbs_Text.get( row )).getText(), ReduxConstants.mathContext15 ).//
                divide(new BigDecimal( 2.0, ReduxConstants.mathContext15 ) ).movePointRight( 6 ) );

        tempFrac.getRadiogenicIsotopeDateByName( RadDates.age207_206r_Pa.getName() )//
                .setValue(new BigDecimal( ((JTextField) fractionDate207_206r_Pa_Text.get( row )).getText(), ReduxConstants.mathContext15 ).//
                movePointRight( 6 ) );
        tempFrac.getRadiogenicIsotopeDateByName( RadDates.age207_206r_Pa.getName() )//
                .setOneSigma(new BigDecimal( ((JTextField) fractionDate207_206r_Pa2SigmaAbs_Text.get( row )).getText(), ReduxConstants.mathContext15 ).//
                divide(new BigDecimal( 2.0, ReduxConstants.mathContext15 ) ).movePointRight( 6 ) );


        // better safe than sorry for now
        ((UPbFractionI) tempFrac).setChanged( true );

    }

    /**
     * 
     * @param evt
     */
    protected void restore_buttonActionPerformed ( java.awt.event.ActionEvent evt ) {
        initAliquot();//showSavedDataII();
    }

    /**
     * 
     * @param evt
     */
    protected void saveAndClose_buttonActionPerformed ( java.awt.event.ActionEvent evt ) {
        saveAliquot();
        close();
    }

    /**
     * 
     * @param evt
     */
    protected void save_buttonActionPerformed ( java.awt.event.ActionEvent evt ) {
        saveAliquot();
    }

    /**
     * 
     * @param evt
     */
    protected void exportXMLAliquot_buttonActionPerformed ( java.awt.event.ActionEvent evt ) {
        saveAliquot();
        exportAliquotToXML();
    }

    /**
     * 
     * @param evt
     */
    protected void saveAndPreviewXMLAliquotAsHTML_buttonActionPerformed ( java.awt.event.ActionEvent evt ) {
        saveAliquot();
        // save off the Aliquot as a temp file
        String tempAliquotXML = "TempAliquot.xml";
        getMyAliquot().serializeXMLObject( tempAliquotXML );
        viewXMLAliquotAsHTML( tempAliquotXML );
    }

    /**
     * 
     * @param evt
     */
    protected void saveAndUploadAliquotToGeochron_buttonActionPerformed ( java.awt.event.ActionEvent evt ) {
        saveAliquot();
        try {
            uploadAliquotToGeochronZip();
        } catch (ETException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
    }
}
