/*
 * UPbFractionEditorDialog.java
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

import Jama.Matrix;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;
import org.earthtime.ETReduxFrame;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.aliquots.UPbReduxAliquot;
import org.earthtime.UPb_Redux.beans.ValueModelClump;
import org.earthtime.UPb_Redux.beans.ValueModelUncertSlider;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.AliquotDetailsDisplayInterface;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.ConcordiaGraphPanel;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.PlottingDetailsDisplayInterface;
import org.earthtime.UPb_Redux.dateInterpretation.graphPersistence.GraphAxesSetup;
import org.earthtime.UPb_Redux.dateInterpretation.kwiki.KwikiConcordiaToolBar;
import org.earthtime.UPb_Redux.dateInterpretation.kwiki.KwikiDateDisplayPanel;
import org.earthtime.UPb_Redux.dateInterpretation.kwiki.KwikiDateModesSelectorPanel;
import org.earthtime.UPb_Redux.dateInterpretation.kwiki.KwikiSynopticOutputPanel;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.UPb_Redux.filters.XMLFileFilter;
import org.earthtime.UPb_Redux.fractions.FractionI;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFraction;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFractionI;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.fractionReduction.ReductionHandler;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.fractionReduction.UPbFractionReducer;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.fractionReduction.UncertaintyGraphPanel;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.fractionReduction.UncertaintyZoomLayer;
import org.earthtime.UPb_Redux.utilities.Thumbnail;
import org.earthtime.UPb_Redux.valueModels.MeasuredRatioModel;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.aliquots.AliquotInterface;
import org.earthtime.aliquots.ReduxAliquotInterface;
import org.earthtime.beans.ET_JButton;
import org.earthtime.dataDictionaries.AnalysisMeasures;
import org.earthtime.dataDictionaries.DataDictionary;
import org.earthtime.dataDictionaries.MeasuredRatios;
import org.earthtime.dataDictionaries.MineralTypes;
import org.earthtime.dataDictionaries.RadDates;
import org.earthtime.dialogs.DialogEditor;
import org.earthtime.exceptions.ETException;
import org.earthtime.exceptions.ETWarningDialog;
import org.earthtime.fractions.ETFractionInterface;
import org.earthtime.matrices.matrixModels.CovarianceMatrixWithSubMatricesModel;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataModels.initialPbModelsET.StaceyKramersInitialPbModelET;
import org.earthtime.ratioDataViews.AbstractRatiosDataView;
import org.earthtime.ratioDataViews.RatiosDataViewNotEditable;
import org.earthtime.ratioDataViews.TracerUPbRatiosDataViewNotEditable;
import org.earthtime.reduxLabData.ReduxLabData;
import org.earthtime.utilities.FileHelper;

/**
 *
 * @author James F. Bowring
 */
public class UPbFractionEditorDialog extends DialogEditor {

    // class variables
    private final static Map<String, String> methodForDates;

    static {
        methodForDates = new HashMap<String, String>();
        methodForDates.put("Analytical", "getRadiogenicIsotopeDateByName");
        methodForDates.put("Tracer", "getRadiogenicIsotopeDateWithTracerUnctByName");
        methodForDates.put("Lambda", "getRadiogenicIsotopeDateWithAllUnctByName");
    }
    private final static Map<String, String[]> dateNamesByCorrection;

    static {
        dateNamesByCorrection = new HashMap<>();
        dateNamesByCorrection.put("None", new String[]{RadDates.age206_238r.getName(), RadDates.age207_235r.getName(), RadDates.age207_206r.getName()});
        dateNamesByCorrection.put("Th", new String[]{RadDates.age206_238r_Th.getName(), RadDates.age207_235r.getName(), RadDates.age207_206r_Th.getName()});
        dateNamesByCorrection.put("Pa", new String[]{RadDates.age206_238r.getName(), RadDates.age207_235r_Pa.getName(), RadDates.age207_206r_Pa.getName()});
        dateNamesByCorrection.put("ThPa", new String[]{RadDates.age206_238r_Th.getName(), RadDates.age207_235r_Pa.getName(), RadDates.age207_206r_ThPa.getName()});
    }
    /**
     *
     */
    public transient java.awt.Frame parent;
    // instance variables
    private javax.swing.JButton kwikiResetAll_button;
    private javax.swing.JButton kwikiAutoU_button;
    private static JLayeredPane concordiaGraphPanel;
    private AliquotInterface aliquot;
    private ETFractionInterface myFraction;
    private ArrayList<String> fractionIDs;
    private boolean compiled;
    private JLayeredPane uncertaintyGraphPanel;
    private ValueModelClump[] valueModelClumps;
    private JPanel kwikiDateDisplayPanel;
    private JLayeredPane kwikiDateModesSelectorPanel;
    private JPanel kwikiOutputPanel;
    private JLayeredPane kwikiConcordiaToolBar;
    private boolean doReduction = false;
    /**
     * In Order reference to dates: 206/238, 207/235, 207/206
     */
    private boolean[] showDates;
    private String uncertaintyModeForDates;
    private static String correctionModeForDates = "None";
    private boolean sliderLock;
    private JLayeredPane date206_238rUncertainties;
    private JLayeredPane date207_235rUncertainties;
    private JLayeredPane date207_206rUncertainties;
    private boolean savedZirconStateOfFraction;
    private JLabel reductionWarning;

    /**
     * Creates new form FractionEditorDialog
     *
     * @param parent
     * @param modal
     * @param selectedTab
     * @param aliquot
     * @param compiled
     * @param fraction
     */
    public UPbFractionEditorDialog(
            java.awt.Frame parent,
            boolean modal,
            AliquotInterface aliquot,
            ETFractionInterface fraction,
            int selectedTab,
            boolean compiled) {

        super(parent, modal);

        if (fraction == null) {
            return;
        }

        this.parent = parent;

        this.compiled = compiled;

        this.aliquot = aliquot;

        myFraction = fraction;
        showDates = new boolean[]{true, true, true};

        // inAutoUraniumMode = false;
        try {
            ((UPbFraction) myFraction).setInAutoUraniumMode(false);
        } catch (Exception e) {
        }
        initComponents();

        toggleStartStopLiveUpdate_button.setText(((ETReduxFrame) parent).getupdateSample_buttonText());

        ((ETReduxFrame) parent).getupdateSample_buttonText();
        setDefaultLookAndFeelDecorated(true);
        setSize();

        // setup info panels
        toggleStartStopLiveUpdate_button.setEnabled(//
                ((UPbReduxAliquot) aliquot).isAutomaticDataUpdateMode() && !compiled);

        aliquotInfo_panel.setBackground(ReduxConstants.myAliquotGrayColor);
        aliquotName_label.setText("Aliquot = " + aliquot.getAliquotName());

        fractionPanel.setBackground(ReduxConstants.myFractionGreenColor);

        fractionPanel.setBackground(ReduxConstants.myFractionGreenColor);
        buttonsPanel.setBackground(ReduxConstants.myFractionGreenColor);

        PbDataTab.setBackground(ReduxConstants.ColorOfLead);
        UDataTab.setBackground(ReduxConstants.ColorOfUranium);

        // populate combobox for fractions -- added march 2009 to allow navigation from within this form
        fraction_Chooser.removeAllItems();
        // add selected fraction if it is rejected as the user clicked it
        if (((UPbFractionI) fraction).isRejected()) {
            fraction_Chooser.addItem(fraction);
        }
        // add the not-rejected fractions
        for (ETFractionInterface f : ((ReduxAliquotInterface) aliquot).getAliquotFractions()) {
            if (!f.isRejected()) {
                fraction_Chooser.addItem(f);
            }
        }

        fraction_Chooser.setSelectedItem(myFraction);
        fraction_Chooser.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox) e.getSource();
                restoreFractionFromKwikiChanges(myFraction);

                reInitializeKwikiTab(myFraction);

                myFraction = (FractionI) cb.getSelectedItem();

                InitializeFractionData(myFraction);

                reInitializeKwikiTab(myFraction);

            }
        });

        // first create a list of used fractionids so that we
        // can tell user if edited fraction name is already in use
        fractionIDs = new ArrayList<String>();
        for (ETFractionInterface f : ((UPbReduxAliquot) aliquot).getAliquotFractions()) {
            fractionIDs.add(f.getFractionID());
        }

        // reset input estimated age for auto uranium mode to date 207/206
        try {
            ((UPbFraction) myFraction).//
                    setInputDate206_238r(myFraction.getRadiogenicIsotopeDateByName(RadDates.age207_206r).getValue());
        } catch (Exception e) {
        }
        InitializeFractionData(myFraction);

        InitializeKwikiTab(myFraction);

        // display selectedTab
        details_Pane.setSelectedIndex(selectedTab);

        staceyKramerCalculator_Panel.setVisible(false);
    }

    /**
     *
     * @param myFraction
     */
    public void InitializeFractionData(final ETFractionInterface myFraction) {

        savedZirconStateOfFraction = ((FractionI)myFraction).isZircon();

        // setup buttons
        saveAndClose_button.setEnabled(!isCompiled());
        save_button.setEnabled(!isCompiled());

        // if this is an added default fraction, the isdeleted field is true and
        // hence we do not need the button active
        delete_button.setEnabled(!((UPbFractionI) myFraction).isDeleted() && !isCompiled());

        InitializeTextBoxes(myFraction);
        showSavedData(myFraction);
    }

    /**
     *
     */
    public void setSize() {
        setSize(1120, 754);
    }

    /**
     *
     * @return
     */
    public boolean isCompiled() {
        return compiled;
    }

    /**
     *
     * @param compiled
     */
    public void setCompiled(boolean compiled) {
        this.compiled = compiled;
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
    public static JLayeredPane getConcordiaGraphPanel() {
        return concordiaGraphPanel;
    }

    /**
     * @param concordiaGraphPanel the concordiaGraphPanel to set
     */
    public static void setConcordiaGraphPanel(JLayeredPane concordiaGraphPanel) {
        UPbFractionEditorDialog.concordiaGraphPanel = concordiaGraphPanel;
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

    // helper class to manage zircon checkbox
    private class ActionIsZirconListener implements ActionListener {

        private ETFractionInterface myFraction;

        public ActionIsZirconListener(ETFractionInterface fraction) {
            myFraction = fraction;
        }

        public void actionPerformed(ActionEvent e) {
            setVisibleInitialPbTabComponents(!((AbstractButton) e.getSource()).isSelected());
            try {
                restoreFractionFromKwikiChanges(myFraction);

                ((FractionI)myFraction).setZircon(((AbstractButton) e.getSource()).isSelected());

                FireDataReducer(myFraction, true);//nov 2009
                reInitializeKwikiTab(myFraction);
            } catch (Exception ex) {
            }
        }
    }

    // helper class to manage auto uranium toggle
    // TODO: could /should use myFraction ?
    private class ActionAutoUraniumListener implements ActionListener {

        private ETFractionInterface fraction;

        public ActionAutoUraniumListener(ETFractionInterface fraction) {
            this.fraction = fraction;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            restoreFractionFromKwikiChanges(fraction);

            try {
                save(fraction);
            } catch (ETException ex) {
                new ETWarningDialog(ex).setVisible(true);
            }
            if (((UPbFraction) fraction).isInAutoUraniumMode()) {
                ((UPbFraction) fraction).zeroUraniumRatios();
            } else {
                ((UPbFraction) fraction).autoGenerateMeasuredUranium();
            }

            FireDataReducer(fraction, true);
            restoreFractionFromKwikiChanges(fraction);
            InitializeFractionData(fraction);
            reInitializeKwikiTab(fraction);

            // bring into view
            ((PlottingDetailsDisplayInterface) concordiaGraphPanel).resetPanel();
        }

        /**
         * @param fraction the fraction to set
         */
        public void setFraction(ETFractionInterface fraction) {
            this.fraction = fraction;
        }
    }

    // helper class to manage reset all values
    private class ActionResetSlidersListener implements ActionListener {

        private ETFractionInterface fraction;

        public ActionResetSlidersListener(ETFractionInterface fraction) {
            this.fraction = fraction;
        }

        public void actionPerformed(java.awt.event.ActionEvent evt) {
            restoreFractionFromKwikiChanges(fraction);
            reInitializeKwikiTab(fraction);
        }

        /**
         * @param fraction the fraction to set
         */
        public void setFraction(ETFractionInterface fraction) {
            this.fraction = fraction;
        }
    }

    private class KwikiDateModesSelectorListener implements PropertyChangeListener {

        private ETFractionInterface fraction;

        public KwikiDateModesSelectorListener(ETFractionInterface fraction) {
            this.fraction = fraction;
        }

        public void propertyChange(PropertyChangeEvent evt) {

            if (evt.getPropertyName().equalsIgnoreCase("correctionModeForDates")) {

                correctionModeForDates = (String) evt.getNewValue();
                String oldCorrectionModeForDates = (String) evt.getOldValue();

                if (correctionModeForDates.contains("Th") != oldCorrectionModeForDates.contains("Th")) {
                    ((ConcordiaGraphPanel) concordiaGraphPanel).toggleDisplay_r206_238r_Th();
                }
                if (correctionModeForDates.contains("Pa") != oldCorrectionModeForDates.contains("Pa")) {
                    ((ConcordiaGraphPanel) concordiaGraphPanel).toggleDisplay_r206_238r_Pa();
                }

                renewDisplay(fraction);

            } else if (evt.getPropertyName().equalsIgnoreCase("uncertaintyModeForDates")) {
                uncertaintyModeForDates = (String) evt.getNewValue();
                renewDisplay(fraction);

            } else if (evt.getPropertyName().equalsIgnoreCase("sliderLock")) {
                sliderLock = Boolean.valueOf((String) evt.getNewValue());
                resetKwikiTabValues(fraction);
            }
        }

        /**
         * @param fraction the fraction to set
         */
        public void setFraction(ETFractionInterface fraction) {
            this.fraction = fraction;
        }
    }

    private void setVisibleInitialPbTabComponents(boolean visible) {
        // turn off Initial Pb Tab for zircon case
        Component[] myComponents = initialPbModelsTab_panel.getComponents();
        for (int i = 0; i < myComponents.length; i++) {
            myComponents[i].setVisible(visible);
        }

        staceyKramerCalculator_Panel.setVisible(false);
    }

    private UncertaintyZoomLayer GenerateUncertaintyZoomLayer(String dateName, String mode, ETFractionInterface fraction) {
        ReductionHandler rh = ((UPbFraction) fraction).getReductionHandler();

//       File matrixFile = new File( "TESTING_ZOOOOOM_" + dateName + "_" + mode + ".txt" );
//            PrintWriter matrixWriter = null;
//
//            try {
//                matrixWriter = new PrintWriter( new FileWriter( matrixFile ) );
//                matrixWriter.println( "\n\n******   TESTING_ZOOOOOM " + dateName + "_" + mode + "   ********************\n\n" );
//
//                matrixWriter.println( rh.chooseCovarianceMatrixModelByMode( uncertaintyModeForDates ).ToStringWithLabels() );
//
//                matrixWriter.close();
//
//            } catch (IOException iOException) {
//            }
//        
        return //
                new UncertaintyZoomLayer(//
                        dateName,
                        rh.chooseCovarianceMatrixModelByMode(uncertaintyModeForDates),
                        rh.extractSensitivityVectorForDate(dateName, mode));
    }

    private void setKwikiVisibilities(boolean reducerExists) {
        // set visibilities
        kwikiAutoU_button.setVisible(reducerExists);
        autoUSettings_panel.setVisible(reducerExists);
        kwikiResetAll_button.setVisible(reducerExists);
        kwikiDateModesSelectorPanel.setVisible(reducerExists);
//        kwikiDateSensitivityPanel.setVisible( reducerExists );
        kwikiDateDisplayPanel.setVisible(reducerExists);
//        kwikiDateDisplaySelectorPanel.setVisible( reducerExists );
        date206_238rUncertainties.setVisible(reducerExists);
        date207_235rUncertainties.setVisible(reducerExists);
        date207_206rUncertainties.setVisible(reducerExists);
        concordiaGraphPanel.setVisible(reducerExists);
        kwikiConcordiaToolBar.setVisible(reducerExists);

        reductionWarning.setVisible(!reducerExists);

        kwikiTab.repaint();
    }

    // sept 2010 refactoring to improve performance and give users what they ask for ... stable concordia etc here
    /**
     *
     * @param fraction
     */
    public void reInitializeKwikiTab(ETFractionInterface fraction) {

        boolean reducerExists = ((UPbFraction) fraction).getReductionHandler() != null;

        for (Component c : kwikiTab.getComponents()) {
            if (c instanceof ValueModelClump) {
                kwikiTab.remove(c);
            }
        }

        setKwikiVisibilities(reducerExists);

        //output panel always visible
        ((KwikiSynopticOutputPanel) kwikiOutputPanel).setFraction(fraction);
        kwikiOutputPanel.repaint();

        // auto U
        if (((UPbFraction) fraction).isInAutoUraniumMode()) {
            kwikiAutoU_button.setText("UN-generate U");
        } else {
            kwikiAutoU_button.setText("auto-generate U");
        }

        ((ActionAutoUraniumListener) kwikiAutoU_button.getActionListeners()[0]).setFraction(fraction);
        // contrapositive
        kwikiAutoU_button.setEnabled(//
                (!(((UPbFraction) fraction).hasMeasuredUranium()//
                && !((UPbFraction) fraction).isInAutoUraniumMode())));

        // set visibility of auto-u details on U tab
        autoUSettings_panel.setVisible(!((UPbFraction) fraction).hasMeasuredUranium());

        // resetAll button
        ((ActionResetSlidersListener) kwikiResetAll_button.getActionListeners()[0]).setFraction(fraction);

        // date modes panel
        try {
            ((KwikiDateModesSelectorListener) kwikiDateModesSelectorPanel.getPropertyChangeListeners()[0]).setFraction(fraction);
        } catch (Exception e) {
        }
        KwikiDateModesSelectorPanel.setDateCorrection(correctionModeForDates);

        if (reducerExists) {
            initializeFullFractionKwiki(fraction);
        }
    }

    /**
     *
     * @param fraction
     */
    public void InitializeKwikiTab(final ETFractionInterface fraction) {

        kwikiTab.removeAll();

        boolean reducerExists = ((UPbFraction) fraction).getReductionHandler() != null;

        reductionWarning = new JLabel("Cannot reduce this fraction due to missing data or missing matrices !");
        reductionWarning.setBounds(200, 300, 450, 50);
        reductionWarning.setBackground(Color.white);
        reductionWarning.setForeground(Color.red);
        reductionWarning.setOpaque(true);
        reductionWarning.setVisible(false);
        reductionWarning.setHorizontalAlignment(SwingConstants.CENTER);
        kwikiTab.add(reductionWarning,
                javax.swing.JLayeredPane.DEFAULT_LAYER);

        uncertaintyModeForDates = "Analytical";
        calculateCorrectionModeForDates();

        sliderLock = false;

        // setup output panel = displayed regardless ****************************
        kwikiOutputPanel = new KwikiSynopticOutputPanel(328, 0, fraction);

        kwikiTab.add(//
                kwikiOutputPanel,
                javax.swing.JLayeredPane.DEFAULT_LAYER);

        // auto uranium
        kwikiAutoU_button = new javax.swing.JButton();
        kwikiAutoU_button.setForeground(new java.awt.Color(204, 0, 0));
        if (((UPbFraction) fraction).isInAutoUraniumMode()) {
            kwikiAutoU_button.setText("UN-generate U");
        } else {
            kwikiAutoU_button.setText("auto-generate U");
        }
        kwikiAutoU_button.addActionListener(new ActionAutoUraniumListener(fraction));

        // contrapositive
        kwikiAutoU_button.setEnabled(
                (!(((UPbFraction) fraction).hasMeasuredUranium() && !((UPbFraction) fraction).isInAutoUraniumMode())));////

        // set visibility of auto-u details on U tab
        autoUSettings_panel.setVisible(!((UPbFractionI) fraction).hasMeasuredUranium());

        // resetAll button
        kwikiResetAll_button = new javax.swing.JButton();
        kwikiResetAll_button.setForeground(new java.awt.Color(204, 0, 0));
        kwikiResetAll_button.setText("reset all values");
        kwikiResetAll_button.addActionListener(new ActionResetSlidersListener(fraction));

        // oct 2010 initialize all kwiki elements
        kwikiTab.add(kwikiResetAll_button, javax.swing.JLayeredPane.DEFAULT_LAYER);
        kwikiTab.add(kwikiAutoU_button, javax.swing.JLayeredPane.DEFAULT_LAYER);

        kwikiDateDisplayPanel = //
                new KwikiDateDisplayPanel(328, 582);//502 );
        kwikiTab.add(//
                kwikiDateDisplayPanel,
                javax.swing.JLayeredPane.DEFAULT_LAYER);
        ((KwikiDateDisplayPanel) kwikiDateDisplayPanel).preparePanel();

        date206_238rUncertainties = new UncertaintyGraphPanel(null, //
                new Rectangle(329, 407, 252, 175));
        kwikiTab.add(//
                date206_238rUncertainties,
                javax.swing.JLayeredPane.DEFAULT_LAYER);

        date207_235rUncertainties = new UncertaintyGraphPanel(null, //
                new Rectangle(583, 407, 252, 175));
        kwikiTab.add(//
                date207_235rUncertainties,
                javax.swing.JLayeredPane.DEFAULT_LAYER);

        date207_206rUncertainties = new UncertaintyGraphPanel(null, //
                new Rectangle(838, 407, 252, 175));
        kwikiTab.add(//
                date207_206rUncertainties,
                javax.swing.JLayeredPane.DEFAULT_LAYER);

        kwikiDateModesSelectorPanel = //
                new KwikiDateModesSelectorPanel(//
                        329, 326, true);
        kwikiDateModesSelectorPanel.addPropertyChangeListener(new KwikiDateModesSelectorListener(fraction));
        KwikiDateModesSelectorPanel.setDateCorrection(correctionModeForDates);

        kwikiTab.add(//
                kwikiDateModesSelectorPanel,
                javax.swing.JLayeredPane.DEFAULT_LAYER);

        concordiaGraphPanel.setBounds(510, 0, 580, 405);
        ((ConcordiaGraphPanel) concordiaGraphPanel).setCurrentGraphAxesSetup(new GraphAxesSetup("C", 2));
        ((ConcordiaGraphPanel) concordiaGraphPanel).setGraphWidth(565 - GraphAxesSetup.DEFAULT_GRAPH_LEFT_MARGIN_VERTICAL_LABELS);
        ((ConcordiaGraphPanel) concordiaGraphPanel).setGraphHeight(385);
        ((ConcordiaGraphPanel) concordiaGraphPanel).setShowTitleBox(false);

        ((ConcordiaGraphPanel) concordiaGraphPanel).//
                setYorkFitLine(null);
        ((ConcordiaGraphPanel) concordiaGraphPanel).//
                setFadedDeselectedFractions(true);

        kwikiTab.add(//
                concordiaGraphPanel,
                javax.swing.JLayeredPane.DEFAULT_LAYER);

        // setup concordia graph panel tool bar *******************************
        kwikiConcordiaToolBar = new KwikiConcordiaToolBar(329, 277, concordiaGraphPanel, null);
        kwikiTab.add(//
                kwikiConcordiaToolBar,
                javax.swing.JLayeredPane.DEFAULT_LAYER);

        setKwikiVisibilities(reducerExists);

        if (reducerExists) {

            initializeFullFractionKwiki(fraction);

        } else {
            // no reduction
            reductionWarning.setVisible(true);
            kwikiTab.repaint();
        }

    }

    private void initializeFullFractionKwiki(ETFractionInterface fraction) {
        ValueModel[] listDates = ListDates(fraction, false);
        setShowDates(new boolean[]{listDates[0].hasPositiveValue(), listDates[1].hasPositiveValue(), listDates[2].hasPositiveValue()});

        doReduction = false;

//        // setup date sensitivity panel ***************************************
//        ((KwikiDateSensitivityPanel) kwikiDateSensitivityPanel).//
//                setOriginalDates( ListDates( fraction, true ) );
//        ((KwikiDateSensitivityPanel) kwikiDateSensitivityPanel).//
//                setChangedDates( ListDates( fraction, false ) );
//        ((KwikiDateSensitivityPanel) kwikiDateSensitivityPanel).//
//                setShowDates( getShowDates() );
//        ((KwikiDateSensitivityPanel) kwikiDateSensitivityPanel).preparePanel();
        // panel to display date names and values *****************************
        ((KwikiDateDisplayPanel) kwikiDateDisplayPanel).//
                setChangedDates(ListDates(fraction, false));
        ((KwikiDateDisplayPanel) kwikiDateDisplayPanel).//
                setAutoUraniumGeneratedDate(((UPbFraction) fraction).isInAutoUraniumMode());

        ((UncertaintyGraphPanel) date206_238rUncertainties).setZoomLayer(//
                GenerateUncertaintyZoomLayer(
                        dateNamesByCorrection.get(correctionModeForDates)[0], uncertaintyModeForDates, fraction));
        ((UncertaintyGraphPanel) date206_238rUncertainties).preparePanel();
        date206_238rUncertainties.setVisible(showDates[0]);

        ((UncertaintyGraphPanel) date207_235rUncertainties).setZoomLayer(//
                GenerateUncertaintyZoomLayer(
                        dateNamesByCorrection.get(correctionModeForDates)[1], uncertaintyModeForDates, fraction));
        ((UncertaintyGraphPanel) date207_235rUncertainties).preparePanel();
        date207_235rUncertainties.setVisible(showDates[1]);

        ((UncertaintyGraphPanel) date207_206rUncertainties).setZoomLayer(//
                GenerateUncertaintyZoomLayer(
                        dateNamesByCorrection.get(correctionModeForDates)[2], uncertaintyModeForDates, fraction));
        ((UncertaintyGraphPanel) date207_206rUncertainties).preparePanel();
        date207_206rUncertainties.setVisible(showDates[2]);

        // if (reducerExists) {
        Vector<ETFractionInterface> selectedFractions = new Vector<>();
        selectedFractions.add(fraction);
        ((AliquotDetailsDisplayInterface) concordiaGraphPanel).//
                setSelectedFractions(selectedFractions);

        // now get all the other fractions and make them deselected
        Vector<ETFractionInterface> deSelectedFractions =//
                ((UPbReduxAliquot) aliquot).getActiveAliquotFractions();
        deSelectedFractions.remove(fraction);
        ((ConcordiaGraphPanel) concordiaGraphPanel).//
                setDeSelectedFractions(deSelectedFractions);

        ((KwikiDateModesSelectorPanel) kwikiDateModesSelectorPanel)//
                .setCorrectThSelected(((ConcordiaGraphPanel) concordiaGraphPanel).isDisplay_r206_238r_Th());
        ((KwikiDateModesSelectorPanel) kwikiDateModesSelectorPanel)//
                .setCorrectPaSelected(((ConcordiaGraphPanel) concordiaGraphPanel).isDisplay_r206_238r_Pa());
        ((KwikiDateModesSelectorPanel) kwikiDateModesSelectorPanel).CalculateDateCorrectionMode("None");

        ((PlottingDetailsDisplayInterface) concordiaGraphPanel).refreshPanel();

        drawSlidersByClump(fraction);

        kwikiResetAll_button.setBounds(2, 0, valueModelClumps[0].getWidth() / 2, 15);
        kwikiAutoU_button.setBounds(2 + valueModelClumps[0].getWidth() / 2, 0, //
                valueModelClumps[0].getWidth() / 2, 15);

        doReduction = true;
    }

    private void calculateCorrectionModeForDates() {

        correctionModeForDates = "None";

        Map<String, String> CGO = ((ConcordiaGraphPanel) concordiaGraphPanel).getConcordiaOptions();
        boolean display_r206_238r_Th = false;
        if (CGO.containsKey("display_r206_238r_Th")) {
            display_r206_238r_Th = Boolean.valueOf(CGO.get("display_r206_238r_Th"));
            ((ConcordiaGraphPanel) concordiaGraphPanel).setDisplay_r206_238r_Th(display_r206_238r_Th);
        }

        boolean display_r206_238r_Pa = false;
        if (CGO.containsKey("display_r206_238r_Pa")) {
            display_r206_238r_Pa = Boolean.valueOf(CGO.get("display_r206_238r_Pa"));
            ((ConcordiaGraphPanel) concordiaGraphPanel).setDisplay_r206_238r_Pa(display_r206_238r_Pa);
        }

        if (display_r206_238r_Th && display_r206_238r_Pa) {
            correctionModeForDates = "ThPa";
        } else if (display_r206_238r_Th) {
            correctionModeForDates = "Th";
        } else if (display_r206_238r_Pa) {
            correctionModeForDates = "Pa";
        }

    }

    private void drawSlidersByClump(final ETFractionInterface fraction) {
        PropertyChangeListener kwikiValueChangeListener =//
                new PropertyChangeListener() {

                    public void propertyChange(PropertyChangeEvent evt) {
                        if (doReduction) {
                            if (evt.getPropertyName().equalsIgnoreCase("uncertainty")) {
                                // to handle the case of covarying uncertainties, we detect that
                                // the uncertainty slider has changed and then we use the
                                // previously created list of covarying terms that is
                                // an attribute of the uncertainty slider with
                                // the new onesigma as inputs to recalculateSubCovariances() of
                                // CovarianceMatrixWithSubmatricesModel
                                // secondly, we re-perform matrix math with altered covariance matrix
                                Matrix oneSigmas =//
                                ((CovarianceMatrixWithSubMatricesModel) ((UPbFraction) fraction).//
                                getReductionHandler().getMatrices()[0]).recalculateSubCovariances(//
                                        ((ValueModelUncertSlider) evt.getSource()).getCovaryingTerms(),
                                        (BigDecimal) evt.getNewValue(), sliderLock);

                                if (sliderLock) {
                                    String[] covTerms = ((ValueModelUncertSlider) evt.getSource()).getCovaryingTerms();
                                    JPanel[] sisterSliders = ((ValueModelUncertSlider) evt.getSource()).getSisterSliders();
                                    // leave out self
                                    for (int i = 1; i < covTerms.length; i++) {
                                        for (int j = 0; j < sisterSliders.length; j++) {
                                            if (sisterSliders[j].getName().equalsIgnoreCase(covTerms[i])) {
                                                ((ValueModelUncertSlider) sisterSliders[j]).//
                                                forceCurrentUncertainty(new BigDecimal(oneSigmas.get(i, i)));
                                            }
                                        }
                                    }
                                }

                                ((UPbFraction) fraction).//
                                getReductionHandler().calculateDateCovMatModel(false);

                            } else {
                                // value changed not uncertainties
                                FireDataReducer(fraction, false);
                            }

                            renewDisplay(fraction);
                        }
                    }
                };

        String[][][] clumpSpecs = null;
        try {
            clumpSpecs = ((UPbFraction) fraction).getReductionHandler().getClumpSpecs();
//        } catch (Exception e) {
//        }
            valueModelClumps = new ValueModelClump[clumpSpecs.length];

            String[][][] covaryingTerms = ((UPbFraction) fraction).getReductionHandler().getCovaryingTerms();

            int verticalOffset = 16;
            for (int i = 0; i < valueModelClumps.length; i++) {
                if (clumpSpecs[i].length > 0) {
                    valueModelClumps[i] =//
                            new ValueModelClump(//
                                    2,
                                    verticalOffset, //
                                    clumpSpecs[i],
                                    // special case of first clump is measured ratios always
                                    (i == 0) ? ((UPbFraction) fraction).getReductionHandler().getMeasuredRatioUncertaintiesValidity() : "",
                                    covaryingTerms[i], //
                                    fraction,//
                                    kwikiValueChangeListener);

                    kwikiTab.add(valueModelClumps[i],
                            javax.swing.JLayeredPane.DEFAULT_LAYER);
                    verticalOffset += valueModelClumps[i].getHeight() + 1;
                }
            }
        } catch (Exception e) {
        }

    }

    private void resetKwikiTabValues(ETFractionInterface fraction) {

        if (((UPbFraction) fraction).getReductionHandler() != null) {
            restoreFractionFromKwikiChanges(fraction);

//            ((KwikiDateSensitivityPanel) kwikiDateSensitivityPanel).//
//                    setOriginalDates( ListDates( fraction, true ) );
            renewDisplay(fraction);
        }

    }

    private void restoreFractionFromKwikiChanges(ETFractionInterface fraction) {

        ((FractionI)fraction).setZircon(savedZirconStateOfFraction);

        if (((UPbFraction) fraction).getReductionHandler() != null) {
            // conserve
            doReduction = false;

            for (Component vms : kwikiTab.getComponents()) {
                if (vms instanceof ValueModelClump) {
                    ((ValueModelClump) vms).ResetSliders();
                }
            }

            doReduction = true;

            FireDataReducer(fraction, true);
        }

    }

    private void renewDisplay(ETFractionInterface fraction) {
        if (((UPbFraction) fraction).getReductionHandler() != null) {

            try {
//                ((KwikiDateSensitivityPanel) kwikiDateSensitivityPanel).//
//                        setChangedDates( ListDates( fraction, false ) );
//                ((KwikiDateSensitivityPanel) kwikiDateSensitivityPanel).refreshPanel();

                ((KwikiDateDisplayPanel) kwikiDateDisplayPanel).//
                        setChangedDates(ListDates(fraction, false));
                ((KwikiDateDisplayPanel) kwikiDateDisplayPanel).//
                        setAutoUraniumGeneratedDate(((UPbFraction) fraction).isInAutoUraniumMode());
                ((KwikiDateDisplayPanel) kwikiDateDisplayPanel).refreshPanel();//.repaint();

                concordiaGraphPanel.repaint();

                ((KwikiSynopticOutputPanel) kwikiOutputPanel).setFraction(fraction);
                kwikiOutputPanel.repaint();

                ((UncertaintyGraphPanel) date206_238rUncertainties).setZoomLayer(//
                        GenerateUncertaintyZoomLayer(dateNamesByCorrection.get(correctionModeForDates)[0], uncertaintyModeForDates, fraction));
                ((UncertaintyGraphPanel) date206_238rUncertainties).refreshPanel();
                date206_238rUncertainties.setVisible(showDates[0]);

                ((UncertaintyGraphPanel) date207_235rUncertainties).setZoomLayer(//
                        GenerateUncertaintyZoomLayer(dateNamesByCorrection.get(correctionModeForDates)[1], uncertaintyModeForDates, fraction));
                ((UncertaintyGraphPanel) date207_235rUncertainties).refreshPanel();
                date207_235rUncertainties.setVisible(showDates[1]);

                ((UncertaintyGraphPanel) date207_206rUncertainties).setZoomLayer(//
                        GenerateUncertaintyZoomLayer(dateNamesByCorrection.get(correctionModeForDates)[2], uncertaintyModeForDates, fraction));
                ((UncertaintyGraphPanel) date207_206rUncertainties).refreshPanel();
                date207_206rUncertainties.setVisible(showDates[2]);

            } catch (Exception e) {
            }
        }
    }

    private ValueModel[] ListDates(ETFractionInterface fraction, boolean copyValues) {
        ValueModel[] retval = new ValueModel[3];

        try {
            Class<?> fractionClass =//
                    Class.forName(UPbFraction.class.getCanonicalName());

            Method method = fractionClass.getMethod(//
                    methodForDates.get(uncertaintyModeForDates),
                    new Class[]{String.class});

            retval[0] = (ValueModel) method.//
                    invoke(fraction, //
                            new Object[]{dateNamesByCorrection.get(correctionModeForDates)[0]});
            retval[1] = (ValueModel) method.//
                    invoke(fraction, //
                            new Object[]{dateNamesByCorrection.get(correctionModeForDates)[1]});
            retval[2] = (ValueModel) method.//
                    invoke(fraction, //
                            new Object[]{dateNamesByCorrection.get(correctionModeForDates)[2]});

        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException classNotFoundException) {
        } catch (InvocationTargetException invocationTargetException) {
            System.out.println(invocationTargetException.getMessage() + "  AT ListDates method of ValueModel");
        }

        if (copyValues) {
            retval[0] = retval[0].copy();
            retval[1] = retval[1].copy();
            retval[2] = retval[2].copy();
        }

        return retval;
    }

    private void FireDataReducer(ETFractionInterface fraction, boolean calculateCovariances) {

        //if (reductionHandlerExists) {
        // currently non-static call on purpose - jfb
        UPbFractionReducer.getInstance().fullFractionReduce((FractionI)fraction, calculateCovariances);
        //}
    }

    private void InitializeTextBoxes(final ETFractionInterface myFraction) {

        // Determine whether the Pb and U data are editable based on presence of source files
        // April 2009 decided to make this more restrictive
        boolean editablePb
                = !((UPbFractionI) myFraction).hasXMLPbSourceFile()
                && !isCompiled();
        boolean editableU
                = !((UPbFractionI) myFraction).hasXMLUSourceFile()
                && !isCompiled();
        // Determine whether fraction is a metal or oxide for editing
        boolean fractionIsOxide = ((UPbFractionI) myFraction).isAnOxide();

        // set properties of text boxes
        // top panel
        fractionID_text.setDocument(new UnDoAbleDocument(fractionID_text, (editablePb && editableU)));

        fractionIsZircon_CheckBox.setEnabled(false);//april 2012 ! isCompiled() );
        fractionIsZircon_CheckBox.addActionListener(new ActionIsZirconListener(myFraction));

        // feb 2011
        fractionIsZircon_CheckBox.setVisible(true);//april 2012false );

        fractionMass_text.setDocument(new BigDecimalDocument(fractionMass_text, !isCompiled()));

        // Pb tab
        r206_204_text.setDocument(new BigDecimalDocument(r206_204_text, editablePb));
        e206_204_text.setDocument(new BigDecimalDocument(e206_204_text, editablePb));

        r207_204_text.setDocument(new BigDecimalDocument(r207_204_text, editablePb));
        e207_204_text.setDocument(new BigDecimalDocument(e207_204_text, editablePb));

        r208_204_text.setDocument(new BigDecimalDocument(r208_204_text, editablePb));
        e208_204_text.setDocument(new BigDecimalDocument(e208_204_text, editablePb));

        r206_207_text.setDocument(new BigDecimalDocument(r206_207_text, editablePb));
        e206_207_text.setDocument(new BigDecimalDocument(e206_207_text, editablePb));

        r206_208_text.setDocument(new BigDecimalDocument(r206_208_text, editablePb));
        e206_208_text.setDocument(new BigDecimalDocument(e206_208_text, editablePb));

        r204_205_text.setDocument(new BigDecimalDocument(r204_205_text, editablePb));
        e204_205_text.setDocument(new BigDecimalDocument(e204_205_text, editablePb));

        r206_205_text.setDocument(new BigDecimalDocument(r206_205_text, editablePb));
        e206_205_text.setDocument(new BigDecimalDocument(e206_205_text, editablePb));

        r207_205_text.setDocument(new BigDecimalDocument(r207_205_text, editablePb));
        e207_205_text.setDocument(new BigDecimalDocument(e207_205_text, editablePb));

        r208_205_text.setDocument(new BigDecimalDocument(r208_205_text, editablePb));
        e208_205_text.setDocument(new BigDecimalDocument(e208_205_text, editablePb));

        r202_205_text.setDocument(new BigDecimalDocument(r202_205_text, editablePb));
        e202_205_text.setDocument(new BigDecimalDocument(e202_205_text, editablePb));

        // set fractionation corrected checkmarks
        r206_204_FracCorr_label.setVisible(((MeasuredRatioModel) myFraction.//
                getMeasuredRatioByName(MeasuredRatios.r206_204m.getName())).isFracCorr());
        r207_204_FracCorr_label.setVisible(((MeasuredRatioModel) myFraction.//
                getMeasuredRatioByName(MeasuredRatios.r207_204m.getName())).isFracCorr());
        r208_204_FracCorr_label.setVisible(((MeasuredRatioModel) myFraction.//
                getMeasuredRatioByName(MeasuredRatios.r208_204m.getName())).isFracCorr());
        r206_207_FracCorr_label.setVisible(((MeasuredRatioModel) myFraction.//
                getMeasuredRatioByName(MeasuredRatios.r206_207m.getName())).isFracCorr());
        r206_208_FracCorr_label.setVisible(((MeasuredRatioModel) myFraction.//
                getMeasuredRatioByName(MeasuredRatios.r206_208m.getName())).isFracCorr());
        r204_205_FracCorr_label.setVisible(((MeasuredRatioModel) myFraction.//
                getMeasuredRatioByName(MeasuredRatios.r204_205m.getName())).isFracCorr());
        r206_205_FracCorr_label.setVisible(((MeasuredRatioModel) myFraction.//
                getMeasuredRatioByName(MeasuredRatios.r206_205m.getName())).isFracCorr());
        r207_205_FracCorr_label.setVisible(((MeasuredRatioModel) myFraction.//
                getMeasuredRatioByName(MeasuredRatios.r207_205m.getName())).isFracCorr());
        r208_205_FracCorr_label.setVisible(((MeasuredRatioModel) myFraction.//
                getMeasuredRatioByName(MeasuredRatios.r208_205m.getName())).isFracCorr());

        pbFracCorrFootnote_label.setVisible(
                ((MeasuredRatioModel) myFraction.getMeasuredRatioByName(MeasuredRatios.r206_204m.getName())).isFracCorr()
                || ((MeasuredRatioModel) myFraction.getMeasuredRatioByName(MeasuredRatios.r207_204m.getName())).isFracCorr()
                || ((MeasuredRatioModel) myFraction.getMeasuredRatioByName(MeasuredRatios.r208_204m.getName())).isFracCorr()
                || ((MeasuredRatioModel) myFraction.getMeasuredRatioByName(MeasuredRatios.r206_207m.getName())).isFracCorr()
                || ((MeasuredRatioModel) myFraction.getMeasuredRatioByName(MeasuredRatios.r206_208m.getName())).isFracCorr()
                || ((MeasuredRatioModel) myFraction.getMeasuredRatioByName(MeasuredRatios.r204_205m.getName())).isFracCorr()
                || ((MeasuredRatioModel) myFraction.getMeasuredRatioByName(MeasuredRatios.r206_205m.getName())).isFracCorr()
                || ((MeasuredRatioModel) myFraction.getMeasuredRatioByName(MeasuredRatios.r207_205m.getName())).isFracCorr()
                || ((MeasuredRatioModel) myFraction.getMeasuredRatioByName(MeasuredRatios.r208_205m.getName())).isFracCorr());

        notesPb.setDocument(new UnDoAbleDocument(notesPb, !isCompiled()));

        // U tab
        r238_235_text.setDocument(new BigDecimalDocument(r238_235_text, editableU));
        e238_235_text.setDocument(new BigDecimalDocument(e238_235_text, editableU));

        r233_235_text.setDocument(new BigDecimalDocument(r233_235_text, editableU));
        e233_235_text.setDocument(new BigDecimalDocument(e233_235_text, editableU));

        r238_233_text.setDocument(new BigDecimalDocument(r238_233_text, editableU));
        e238_233_text.setDocument(new BigDecimalDocument(e238_233_text, editableU));

        r233_236_text.setDocument(new BigDecimalDocument(r233_236_text, editableU));
        e233_236_text.setDocument(new BigDecimalDocument(e233_236_text, editableU));

        // jan 2011
        r238_236_text.setDocument(new BigDecimalDocument(r238_236_text, editableU));
        e238_236_text.setDocument(new BigDecimalDocument(e238_236_text, editableU));

        inputPerCentUncertainty_text.setDocument(new BigDecimalDocument(inputPerCentUncertainty_text, editableU));
        inputEst206_238Date_text.setDocument(new BigDecimalDocument(inputEst206_238Date_text, editableU));
        inputAlphaU_text.setDocument(new BigDecimalDocument(inputAlphaU_text, editableU));
        inputAlphaUOneSigmaAbs_text.setDocument(new BigDecimalDocument(//
                inputAlphaUOneSigmaAbs_text, !((UPbFraction) myFraction).tracerTypeIncludes233()));

        // set fractionation corrected checkmarks
        r233_235_FracCorr_label.setVisible(((MeasuredRatioModel) myFraction.//
                getMeasuredRatioByName(MeasuredRatios.r233_235m.getName())).isFracCorr());
        r238_235_FracCorr_label.setVisible(((MeasuredRatioModel) myFraction.//
                getMeasuredRatioByName(MeasuredRatios.r238_235m.getName())).isFracCorr());
        r238_233_FracCorr_label.setVisible(((MeasuredRatioModel) myFraction.//
                getMeasuredRatioByName(MeasuredRatios.r238_233m.getName())).isFracCorr());

        uFracCorrFootnote_label.setVisible(
                ((MeasuredRatioModel) myFraction.getMeasuredRatioByName(MeasuredRatios.r233_235m.getName())).isFracCorr()
                || ((MeasuredRatioModel) myFraction.getMeasuredRatioByName(MeasuredRatios.r238_235m.getName())).isFracCorr()
                || ((MeasuredRatioModel) myFraction.getMeasuredRatioByName(MeasuredRatios.r238_233m.getName())).isFracCorr());

        notesU.setDocument(new UnDoAbleDocument(notesU, !isCompiled()));

        // aug 2010 oxide correction refinements
        r18O_16OUsed_textOnUTab.setDocument(new BigDecimalDocument(r18O_16OUsed_textOnUTab, true));
        fractionIsMetal_rb.setEnabled(!((UPbFractionI) myFraction).hasXMLUSourceFile());
        fractionIsMetal_rb.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ValueModel r18O_16OZeroWithZeroUnct = //
                        new ValueModel("tempZero", BigDecimal.ZERO, "ABS", BigDecimal.ZERO, BigDecimal.ZERO);
                myFraction.getAnalysisMeasure(AnalysisMeasures.r18O_16O.getName())//
                        .copyValuesFrom(r18O_16OZeroWithZeroUnct);
                myFraction.getAnalysisMeasure(AnalysisMeasures.r18O_16O_revised.getName())//
                        .copyValuesFrom(r18O_16OZeroWithZeroUnct);

                r18O_16OUsed_text.setText("0");
                r18O_16OUsedError_text.setText("0");
                r18O_16ORevised_text.setText("0");
                r18O_16OUsed_textOnUTab.setText(("0"));

                ((UPbFraction) myFraction).setFractionIsAnOxide(false, ((UPbFraction) myFraction).getTracerType());
                r18O_16OUsed_textOnUTab.setEnabled(false);
            }
        });

        fractionIsOxide_rb.setEnabled(!((UPbFractionI) myFraction).hasXMLUSourceFile());
        r18O_16OUsed_textOnUTab.setEnabled(!((UPbFractionI) myFraction).hasXMLUSourceFile());
        fractionIsOxide_rb.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                r18O_16OUsed_textOnUTab.setEnabled(true);
                ((UPbFraction) myFraction).setFractionIsAnOxide(true, ((UPbFraction) myFraction).getTracerType());
            }
        });

        if (((UPbFractionI) myFraction).isAnOxide()) {
            fractionIsOxide_rb.setSelected(true);
        } else {
            fractionIsMetal_rb.setSelected(true);
        }

        // tracer tab
        tracerMass_text.setDocument(new BigDecimalDocument(tracerMass_text, !isCompiled()));//fractionHasTracer ) );
        tracerMassOneSigmaAbs_text.setDocument(new BigDecimalDocument(tracerMassOneSigmaAbs_text, !isCompiled()));

        // corrections tab *****************************************************
        // AlphaPb
        alphaPbFromModel_text.setDocument(new BigDecimalDocument(alphaPbFromModel_text, false));
        meanAlphaPbImported_text.setDocument(new BigDecimalDocument(meanAlphaPbImported_text, false));
        alphaPbCalculatedFromMeans_text.setDocument(new BigDecimalDocument(alphaPbCalculatedFromMeans_text, false));

        // AlphaU
        meanAlphaUImported_text.setDocument(new BigDecimalDocument(meanAlphaUImported_text, false));
        alphaUCalculatedFromMeans_text.setDocument(new BigDecimalDocument(alphaUCalculatedFromMeans_text, false));
        alphaUFromModel_text.setDocument(new BigDecimalDocument(alphaUFromModel_text, false));

        // Oxide Correction
        // reworked aug 2010 to fine-tune details
        if (fractionIsOxide) {
            oxidePanel.setVisible(true);
            r18O_16OUsed_text.setDocument(new BigDecimalDocument(r18O_16OUsed_text, false));
            fractionIsOxide_rb.doClick();//setSelected( true );
            r18O_16OUsedError_text.setDocument(new BigDecimalDocument(r18O_16OUsedError_text, !isCompiled()));
            r18O_16ORevised_text.setDocument(new BigDecimalDocument(r18O_16ORevised_text, !isCompiled()));
        } else {
            // metal
            oxidePanel.setVisible(false);
            fractionIsMetal_rb.doClick();//.setSelected( true );
        }

        // U Sample Components
        assumedUBlankMass_text.setDocument(new BigDecimalDocument(assumedUBlankMass_text, editableU));
        uBlankMassOneSigma_text.setDocument(new BigDecimalDocument(uBlankMassOneSigma_text, !isCompiled()));

        r238_235Blank_text.setDocument(new BigDecimalDocument(r238_235Blank_text, editableU));
        r238_235BlankError_text.setDocument(new BigDecimalDocument(r238_235BlankError_text, !isCompiled()));

        r238_235Sample_text.setDocument(new BigDecimalDocument(r238_235Sample_text, editableU));
        r238_235SampleError_text.setDocument(new BigDecimalDocument(r238_235SampleError_text, !isCompiled()));

        // Initial Pb Model tab
        PbBlank_Mass_text.setDocument(new BigDecimalDocument(PbBlank_Mass_text, !isCompiled()));
        pbBlankMassOneSigma_text.setDocument(new BigDecimalDocument(pbBlankMassOneSigma_text, !isCompiled()));

        // Archiving Tab (as of dec 2010) TODO: rename components publication details
        // set up Mineral chooser
        mineralNameChooser.removeAllItems();
        for (int i = 0; i < MineralTypes.values().length; i++) {
            mineralNameChooser.addItem(MineralTypes.values()[i].getName());
        }

        mineralNameChooser.setSelectedItem(((FractionI)myFraction).getMineralName());
        mineralNameChooser.setEnabled(!isCompiled());

        // setting type
        settingTypeChooser.removeAllItems();
        for (int i = 0; i < DataDictionary.SettingType.length; i++) {
            settingTypeChooser.addItem(DataDictionary.SettingType[i]);
        }

        settingTypeChooser.setSelectedItem(((FractionI) myFraction).getSettingType());
        settingTypeChooser.setEnabled(!isCompiled());

        countOfGrains_text.setDocument(new IntegerDocument(countOfGrains_text, !isCompiled()));
        physicallyAbraded_chkBox.setEnabled(!isCompiled());
        leachedInHFAcid_chkBox.setEnabled(!isCompiled());
        annealedChemicallyAbraded_chkBox.setEnabled(!isCompiled());
        chemicallyPurifiedUPb_chkBox.setEnabled(!isCompiled());

        fractionComment_text.setDocument(new UnDoAbleDocument(fractionComment_text, !isCompiled()));
        publicationTimeStamp_text.setDocument(new UnDoAbleDocument(publicationTimeStamp_text, false));

        selectImageFile_button.setEnabled(!isCompiled());

////////
////////        //*******************************************************************************************************************************************
////////        //New Inits for GUI 6/13/11 (Move over from older version to new 2.6)
////////        //ButtonGroup valueModelGroup = new ButtonGroup();
////////        JLabel choose = new JLabel( "Choose a set of ValueModels:" );
////////        choose.setBounds( 100, 50, 350, 50 );
////////
////////        //Added 7/6/11 DualLIstBox        
////////        dBox.setSize( 300, 200 );
////////        dBox.setLocation( 400, 65 );
////////
////////        dBox.addButton.addActionListener( new ActionListener() {
////////
////////            public void actionPerformed ( ActionEvent e ) {
////////
////////                Object selected[] = dBox.getSourceList().getSelectedValues();
////////                for (Object s : selected) {
////////                    selectedMList.add( s.toString() );
////////                    holdingMList.remove( s.toString() );
////////                }
////////                dBox.setDestinationElements( selectedMList.toArray() );
////////                dBox.setSourceElements( holdingMList.toArray() );
////////                dBox.clearDestinationSelected();
////////            }
////////        } );
////////
////////
////////        dBox.removeButton.addActionListener( new ActionListener() {
////////
////////            public void actionPerformed ( ActionEvent e ) {
////////                Object selected[] = dBox.getDestList().getSelectedValues();
////////                for (Object s : selected) {
////////                    nonSelectedMList.add( s.toString() );
////////                    selectedMList.remove( s.toString() );
////////                }
////////                dBox.addSourceElements( nonSelectedMList.toArray() );
////////                dBox.clearDestinationSelected();
////////            }
////////        } );
////////
////////        JButton runMathMachine = new JButton( "Run MathMachine on selected Value Model(s)." );
////////        runMathMachine.setBounds( 700, 60, 300, 30 );
////////        runMathMachine.addActionListener( new java.awt.event.ActionListener() {
////////
////////            public void actionPerformed ( java.awt.event.ActionEvent evt ) {
////////                runMathMachineActionPerformed( evt );
////////            }
////////        } );
////////
////////
////////        analysisMeasures = new JCheckBox( "Analysis Measures" );
////////        // valueModelGroup.add(analysisMeasures);
////////        analysisMeasures.setFont( new java.awt.Font( "SansSerif", 1, 11 ) );
////////        analysisMeasures.setBounds( 150, 125, 250, 20 );
////////        analysisMeasures.addChangeListener( new ChangeListener() {
////////
////////            SortedSet<String> aTemp = new TreeSet<String>( Arrays.asList( AnalysisMeasures.getNamesSorted() ) );
////////
////////            public void stateChanged ( ChangeEvent e ) {
////////                if ( analysisMeasures.isSelected() ) {
////////                    holdingMList.addAll( aTemp );
////////                    dBox.setSourceElements( holdingMList.toArray() );
////////                } else if (  ! analysisMeasures.isSelected() ) {
////////                    holdingMList.removeAll( aTemp );
////////                    dBox.setSourceElements( holdingMList.toArray() );
////////                }
////////                dBox.clearDestinationListModel();
////////                selectedMList.clear();
////////            }
////////        } );
////////
////////        compMeasures = new JCheckBox( "Compositional Measures" );
////////        // valueModelGroup.add(compMeasures);
////////        compMeasures.setFont( new java.awt.Font( "SansSerif", 1, 11 ) );
////////        compMeasures.setBounds( 150, 150, 250, 20 );
////////        compMeasures.addChangeListener( new ChangeListener() {
////////
////////            SortedSet<String> cTemp = new TreeSet<String>( Arrays.asList( CompiledValueModels.getCMeasureModel() ) );
////////
////////            public void stateChanged ( ChangeEvent e ) {
////////                if ( compMeasures.isSelected() ) {
////////                    holdingMList.addAll( cTemp );
////////                    dBox.setSourceElements( holdingMList.toArray() );
////////                } else if (  ! compMeasures.isSelected() ) {
////////                    holdingMList.removeAll( cTemp );
////////                    dBox.setSourceElements( holdingMList.toArray() );
////////                }
////////                dBox.clearDestinationListModel();
////////                selectedMList.clear();
////////            }
////////        } );
////////
////////        radRatios = new JCheckBox( "Radiogenic Isotope Ratios" );
////////        //  valueModelGroup.add(radRatios);
////////        radRatios.setFont( new java.awt.Font( "SansSerif", 1, 11 ) );
////////        radRatios.setBounds( 150, 100, 250, 20 );
////////        radRatios.addChangeListener( new ChangeListener() {
////////
////////            TreeSet<String> ratTemp = new TreeSet<String>( CompiledValueModels.toTreeSet( CompiledValueModels.getRRatioModel() ) );
////////
////////            public void stateChanged ( ChangeEvent e ) {
////////                if ( radRatios.isSelected() ) {
////////                    holdingMList.addAll( ratTemp );
////////                    dBox.setSourceElements( holdingMList.toArray() );
////////                } else if (  ! radRatios.isSelected() ) {
////////                    holdingMList.removeAll( ratTemp );
////////                    dBox.setSourceElements( holdingMList.toArray() );
////////                }
////////                dBox.clearDestinationListModel();
////////                selectedMList.clear();
////////            }
////////        } );
////////
////////        radDates = new JCheckBox( "Radiogenic Isotope Dates" );
////////        // valueModelGroup.add(radDates);
////////        radDates.setFont( new java.awt.Font( "SansSerif", 1, 11 ) );
////////        radDates.setBounds( 150, 175, 250, 20 );
////////        radDates.addChangeListener( new ChangeListener() {
////////
////////            TreeSet<String> radTemp = new TreeSet<String>( CompiledValueModels.toTreeSet( CompiledValueModels.getRDateModels() ) );
////////
////////            public void stateChanged ( ChangeEvent e ) {
////////                radTemp.remove( RadDates.bestAge.toString() );
////////                if ( radDates.isSelected() ) {
////////                    holdingMList.addAll( radTemp );
////////                    dBox.setSourceElements( holdingMList.toArray() );
////////                } else if (  ! radDates.isSelected() ) {
////////                    holdingMList.removeAll( radTemp );
////////                    dBox.setSourceElements( holdingMList.toArray() );
////////                }
////////                dBox.clearDestinationListModel();
////////                selectedMList.clear();
////////            }
////////        } );
////////
////////        reductionsTab.add( analysisMeasures );
////////        reductionsTab.add( compMeasures );
////////        reductionsTab.add( radDates );
////////        reductionsTab.add( radRatios );
////////        reductionsTab.add( choose );
////////        reductionsTab.add( runMathMachine );
////////        reductionsTab.add( dBox );
////////
    } // initializeTextBoxes
////////
//////////    /**
//////////     *
//////////     * @param evt
//////////     */
//////////    private void valueModelSetsActionPerformed ( java.awt.event.ActionEvent evt ) {
//////////        // TODO add your handling code here:
//////////
//////////        JComboBox cb = (JComboBox) evt.getSource();
//////////        String selectedVM = "";
//////////        selectedVM = cb.getSelectedItem().toString();
//////////        this.setCurrentVM( selectedVM );
//////////        System.out.print( getCurrentVM() );
//////////
//////////    }
////////    private void runMathMachineActionPerformed ( java.awt.event.ActionEvent evt ) {
////////        // TODO add your handling code here:
////////        //********************
////////        if (  ! selectedMList.isEmpty() ) { //If no  VMS are selected gives error message
////////
////////            MathMachineII.CreateHTMLIndexFile( dBox.getDestListModel(), "TestMathMachine", true, (UPbFraction) myFraction, aliquot );
////////            if ( System.getProperty( "os.name" ).toLowerCase().startsWith( "windows" ) ) {
////////                BrowserControl.displayURL( "C:/Users/JasonD/Documents/Cirdles/UPb_Redux_Dec2011/TestMathMachine/Index.html" );
////////            } else if ( System.getProperty( "os.name" ).toLowerCase().startsWith( "mac os x" ) ) {
////////                BrowserControl.displayURL( "./TestMathMachine/Index.html" );
////////
////////            }
////////        } else {
////////            JOptionPane.showMessageDialog( frame, "No value models have been chosen.", "No VM error", JOptionPane.WARNING_MESSAGE );
////////        }
////////    }
////////
////////    //*******************************************************************************************************************************************

    private void showSavedData(ETFractionInterface myFraction) {

        // header panel
        fractionID_text.setText(myFraction.getFractionID());

        fractionIsZircon_CheckBox.setSelected(((FractionI)myFraction).isZircon());

        fractionMass_text.setText(myFraction.getAnalysisMeasure(AnalysisMeasures.fractionMass.getName()).//
                getValue().setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE, RoundingMode.HALF_UP).toPlainString());

        // Pb tab **************************************************************
        r206_204_text.setText(myFraction.getMeasuredRatioByName(MeasuredRatios.r206_204m.getName()).//
                getValue().setScale(ReduxConstants.DEFAULT_RATIO_SCALE, RoundingMode.HALF_UP).toPlainString());
        e206_204_text.setText(myFraction.getMeasuredRatioByName(MeasuredRatios.r206_204m.getName()).//
                getOneSigma().setScale(ReduxConstants.DEFAULT_RATIO_SCALE, RoundingMode.HALF_UP).toPlainString());

        r207_204_text.setText(myFraction.getMeasuredRatioByName(MeasuredRatios.r207_204m.getName()).//
                getValue().setScale(ReduxConstants.DEFAULT_RATIO_SCALE, RoundingMode.HALF_UP).toPlainString());
        e207_204_text.setText(myFraction.getMeasuredRatioByName(MeasuredRatios.r207_204m.getName()).//
                getOneSigma().setScale(ReduxConstants.DEFAULT_RATIO_SCALE, RoundingMode.HALF_UP).toPlainString());

        r208_204_text.setText(myFraction.getMeasuredRatioByName(MeasuredRatios.r208_204m.getName()).//
                getValue().setScale(ReduxConstants.DEFAULT_RATIO_SCALE, RoundingMode.HALF_UP).toPlainString());
        e208_204_text.setText(myFraction.getMeasuredRatioByName(MeasuredRatios.r208_204m.getName()).//
                getOneSigma().setScale(ReduxConstants.DEFAULT_RATIO_SCALE, RoundingMode.HALF_UP).toPlainString());

        r206_207_text.setText(myFraction.getMeasuredRatioByName(MeasuredRatios.r206_207m.getName()).//
                getValue().setScale(ReduxConstants.DEFAULT_RATIO_SCALE, RoundingMode.HALF_UP).toPlainString());
        e206_207_text.setText(myFraction.getMeasuredRatioByName(MeasuredRatios.r206_207m.getName()).//
                getOneSigma().setScale(ReduxConstants.DEFAULT_RATIO_SCALE, RoundingMode.HALF_UP).toPlainString());

        r206_208_text.setText(myFraction.getMeasuredRatioByName(MeasuredRatios.r206_208m.getName()).//
                getValue().setScale(ReduxConstants.DEFAULT_RATIO_SCALE, RoundingMode.HALF_UP).toPlainString());
        e206_208_text.setText(myFraction.getMeasuredRatioByName(MeasuredRatios.r206_208m.getName()).//
                getOneSigma().setScale(ReduxConstants.DEFAULT_RATIO_SCALE, RoundingMode.HALF_UP).toPlainString());

        r204_205_text.setText(myFraction.getMeasuredRatioByName(MeasuredRatios.r204_205m.getName()).//
                getValue().setScale(ReduxConstants.DEFAULT_RATIO_SCALE, RoundingMode.HALF_UP).toPlainString());
        e204_205_text.setText(myFraction.getMeasuredRatioByName(MeasuredRatios.r204_205m.getName()).//
                getOneSigma().setScale(ReduxConstants.DEFAULT_RATIO_SCALE, RoundingMode.HALF_UP).toPlainString());

        r206_205_text.setText(myFraction.getMeasuredRatioByName(MeasuredRatios.r206_205m.getName()).//
                getValue().setScale(ReduxConstants.DEFAULT_RATIO_SCALE, RoundingMode.HALF_UP).toPlainString());
        e206_205_text.setText(myFraction.getMeasuredRatioByName(MeasuredRatios.r206_205m.getName()).//
                getOneSigma().setScale(ReduxConstants.DEFAULT_RATIO_SCALE, RoundingMode.HALF_UP).toPlainString());

        r207_205_text.setText(myFraction.getMeasuredRatioByName(MeasuredRatios.r207_205m.getName()).//
                getValue().setScale(ReduxConstants.DEFAULT_RATIO_SCALE, RoundingMode.HALF_UP).toPlainString());
        e207_205_text.setText(myFraction.getMeasuredRatioByName(MeasuredRatios.r207_205m.getName()).//
                getOneSigma().setScale(ReduxConstants.DEFAULT_RATIO_SCALE, RoundingMode.HALF_UP).toPlainString());

        r208_205_text.setText(myFraction.getMeasuredRatioByName(MeasuredRatios.r208_205m.getName()).//
                getValue().setScale(ReduxConstants.DEFAULT_RATIO_SCALE, RoundingMode.HALF_UP).toPlainString());
        e208_205_text.setText(myFraction.getMeasuredRatioByName(MeasuredRatios.r208_205m.getName()).//
                getOneSigma().setScale(ReduxConstants.DEFAULT_RATIO_SCALE, RoundingMode.HALF_UP).toPlainString());

        r202_205_text.setText(myFraction.getMeasuredRatioByName(MeasuredRatios.r202_205m.getName()).//
                getValue().setScale(ReduxConstants.DEFAULT_RATIO_SCALE, RoundingMode.HALF_UP).toPlainString());
        e202_205_text.setText(myFraction.getMeasuredRatioByName(MeasuredRatios.r202_205m.getName()).//
                getOneSigma().setScale(ReduxConstants.DEFAULT_RATIO_SCALE, RoundingMode.HALF_UP).toPlainString());

        notesPb.setText(((UPbFraction) myFraction).getNotesPb());

        sourcePbFileDisplay.setText(
                "<html><p>" + ((UPbFraction) myFraction).getSourceFilePb() + "</p></html");

        pedigreePbDisplay.setText(
                "<html><p>" + ((UPbFraction) myFraction).getPedigreePb() + "</p></html");

        // U tab
        r238_235_text.setText(myFraction.getMeasuredRatioByName(MeasuredRatios.r238_235m.getName()).//
                getValue().setScale(ReduxConstants.DEFAULT_RATIO_SCALE, RoundingMode.HALF_UP).toPlainString());
        e238_235_text.setText(myFraction.getMeasuredRatioByName(MeasuredRatios.r238_235m.getName()).//
                getOneSigma().setScale(ReduxConstants.DEFAULT_RATIO_SCALE, RoundingMode.HALF_UP).toPlainString());

        r233_235_text.setText(myFraction.getMeasuredRatioByName(MeasuredRatios.r233_235m.getName()).//
                getValue().setScale(ReduxConstants.DEFAULT_RATIO_SCALE, RoundingMode.HALF_UP).toPlainString());
        e233_235_text.setText(myFraction.getMeasuredRatioByName(MeasuredRatios.r233_235m.getName()).//
                getOneSigma().setScale(ReduxConstants.DEFAULT_RATIO_SCALE, RoundingMode.HALF_UP).toPlainString());

        r238_233_text.setText(myFraction.getMeasuredRatioByName(MeasuredRatios.r238_233m.getName()).//
                getValue().setScale(ReduxConstants.DEFAULT_RATIO_SCALE, RoundingMode.HALF_UP).toPlainString());
        e238_233_text.setText(myFraction.getMeasuredRatioByName(MeasuredRatios.r238_233m.getName()).//
                getOneSigma().setScale(ReduxConstants.DEFAULT_RATIO_SCALE, RoundingMode.HALF_UP).toPlainString());

        r233_236_text.setText(myFraction.getMeasuredRatioByName(MeasuredRatios.r233_236m.getName()).//
                getValue().setScale(ReduxConstants.DEFAULT_RATIO_SCALE, RoundingMode.HALF_UP).toPlainString());
        e233_236_text.setText(myFraction.getMeasuredRatioByName(MeasuredRatios.r233_236m.getName()).//
                getOneSigma().setScale(ReduxConstants.DEFAULT_RATIO_SCALE, RoundingMode.HALF_UP).toPlainString());

        // jan 2011
        r238_236_text.setText(myFraction.getMeasuredRatioByName(MeasuredRatios.r238_236m.getName()).//
                getValue().setScale(ReduxConstants.DEFAULT_RATIO_SCALE, RoundingMode.HALF_UP).toPlainString());
        e238_236_text.setText(myFraction.getMeasuredRatioByName(MeasuredRatios.r238_236m.getName()).//
                getOneSigma().setScale(ReduxConstants.DEFAULT_RATIO_SCALE, RoundingMode.HALF_UP).toPlainString());

        // utab oxide
        r18O_16OUsed_textOnUTab.setText(myFraction.getAnalysisMeasure(AnalysisMeasures.r18O_16O.getName()).
                getValue().setScale(ReduxConstants.DEFAULT_DEFAULTS_SCALE, RoundingMode.HALF_UP).toPlainString());

        notesU.setText(((UPbFraction) myFraction).getNotesU());

        sourceUFileDisplay.setText(
                "<html><p>" + ((UPbFraction) myFraction).getSourceFileU() + "</p></html");

        pedigreeUDisplay.setText(
                "<html><p>" + ((UPbFraction) myFraction).getPedigreeU() + "</p></html");

        inputPerCentUncertainty_text.setText(((UPbFraction) myFraction).getInputOneSigmaPct().//
                setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE, RoundingMode.HALF_UP).toPlainString());

        inputEst206_238Date_text.setText(((UPbFraction) myFraction).getInputDate206_238r().//
                movePointLeft(6).//
                setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE, RoundingMode.HALF_UP).toPlainString());

        inputAlphaU_text.setText(((UPbFraction) myFraction).getInputAlphaU().getValue().//
                movePointRight(2).//
                setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE, RoundingMode.HALF_UP).toPlainString());

        inputAlphaUOneSigmaAbs_text.setText(((UPbFraction) myFraction).getInputAlphaU().getOneSigmaAbs().//
                movePointRight(2).//
                setScale(ReduxConstants.DEFAULT_MASS_DISPLAY_SCALE, RoundingMode.HALF_UP).toPlainString());

        // tracer tab **********************************************************
        tracerMassOneSigmaAbs_label.setText("<html><u>1\u03C3 ABS:</u></html>");
        tracerMass_text.setText(myFraction.getAnalysisMeasure(AnalysisMeasures.tracerMassInGrams.getName()).//
                getValue().setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE, RoundingMode.HALF_UP).toPlainString());
        tracerMassOneSigmaAbs_text.setText(myFraction.getAnalysisMeasure(AnalysisMeasures.tracerMassInGrams.getName()).//
                getOneSigma().setScale(ReduxConstants.DEFAULT_MASS_DISPLAY_SCALE, RoundingMode.HALF_UP).toPlainString());

        // set up Tracer chooser modified June 2012
        ItemListener[] tracerActionListeners = tracerChooser.getItemListeners();
        for (int i = 0; i < tracerActionListeners.length; i++) {
            tracerChooser.removeItemListener(tracerActionListeners[i]);
        }
        TracerItemListener tracerActionListener = new TracerItemListener(myFraction);
        tracerChooser.addItemListener(tracerActionListener);
        try {
            initTracerModelChooser();
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }

        // Corrections tab ****************************************************
        PopulateCorrectionsTab(myFraction);

        // Oxide Correction - these fields are used as data holders even when not visible
        r18O_16OUsed_text.setText(myFraction.getAnalysisMeasure(AnalysisMeasures.r18O_16O.getName()).
                getValue().setScale(ReduxConstants.DEFAULT_DEFAULTS_SCALE, RoundingMode.HALF_UP).toPlainString());
        r18O_16OUsedError_text.setText(myFraction.getAnalysisMeasure(AnalysisMeasures.r18O_16O_revised.getName()).
                getOneSigma().setScale(ReduxConstants.DEFAULT_DEFAULTS_SCALE, RoundingMode.HALF_UP).toPlainString());
        r18O_16ORevised_text.setText(myFraction.getAnalysisMeasure(AnalysisMeasures.r18O_16O_revised.getName()).
                getValue().setScale(ReduxConstants.DEFAULT_DEFAULTS_SCALE, RoundingMode.HALF_UP).toPlainString());

        // blanks tab modified June 2012 *********************************
        ItemListener[] blankActionListeners = PbBlankChooser.getItemListeners();
        for (int i = 0; i < blankActionListeners.length; i++) {
            PbBlankChooser.removeItemListener(blankActionListeners[i]);
        }
        ItemListener blankActionListener = new BlankItemListener(myFraction);
        PbBlankChooser.addItemListener(blankActionListener);
        try {
            initPbBlankModelChooser();
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }

        // Initial Pb Model Tab modified June 2012 ********************************
        ItemListener[] initialPbModelActionListeners = InitialPbModelChooser.getItemListeners();
        for (int i = 0; i < initialPbModelActionListeners.length; i++) {
            InitialPbModelChooser.removeItemListener(initialPbModelActionListeners[i]);
        }
        InitialPbModelItemListener initialPbModelActionListener = new InitialPbModelItemListener(myFraction);
        InitialPbModelChooser.addItemListener(initialPbModelActionListener);
        try {
            initInitialPbModelChooser();
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }

        setVisibleInitialPbTabComponents(!((FractionI)myFraction).isZircon());
        //populateInitialPbModelFields( myFraction );

        // publication details
        mineralNameChooser.setSelectedItem(((FractionI)myFraction).getMineralName());
        settingTypeChooser.setSelectedItem(((FractionI)myFraction).getSettingType());
        countOfGrains_text.setText(String.valueOf(myFraction.getNumberOfGrains()));
        physicallyAbraded_chkBox.setSelected(((UPbFractionI) myFraction).isPhysicallyAbraded());
        leachedInHFAcid_chkBox.setSelected(((UPbFractionI) myFraction).isLeachedInHFAcid());
        annealedChemicallyAbraded_chkBox.setSelected(((UPbFractionI) myFraction).isAnnealedAndChemicallyAbraded());
        chemicallyPurifiedUPb_chkBox.setSelected(((UPbFractionI) myFraction).isChemicallyPurifiedUPb());
        fractionComment_text.setText(myFraction.getAnalysisFractionComment());

        publicationTimeStamp_text.setText(myFraction.getTimeStamp().toString());

        displayThumbnailForFraction(myFraction);

    } //showSavedData

    private void initTracerModelChooser() throws BadLabDataException {

        // set up Tracer chooser
        tracerChooser.removeAllItems();
        ArrayList<AbstractRatiosDataModel> tracers = ((UPbFraction) myFraction).getMyLabData().getTracers();
        for (int i = (tracers.size() > 1 ? 1 : 0); i < tracers.size(); i++) {
            tracerChooser.addItem(tracers.get(i).getReduxLabDataElementName());
        }

        tracerChooser.setSelectedIndex(0);
        tracerChooser.setSelectedItem(((UPbFraction) myFraction).getTracerID());

        tracerChooser.setEnabled(!isCompiled());

        // disable if Tracer was imported from fraction
        tracerChooser.setEnabled(!(((MeasuredRatioModel) myFraction.getMeasuredRatioByName(MeasuredRatios.r238_235m.getName())).isFracCorr()
                || ((MeasuredRatioModel) myFraction.getMeasuredRatioByName(MeasuredRatios.r233_235m.getName())).isFracCorr()));

    }

    private void initInitialPbModelChooser() throws BadLabDataException {
        // set up InitialPbModel chooser
        InitialPbModelChooser.removeAllItems();
        ArrayList<AbstractRatiosDataModel> initialPbModels = ((UPbFraction) myFraction).getMyLabData().getInitialPbModels();
        for (int i = (initialPbModels.size() > 1 ? 1 : 0); i < initialPbModels.size(); i++) {
            InitialPbModelChooser.addItem(initialPbModels.get(i).getReduxLabDataElementName());
        }

        InitialPbModelChooser.setSelectedIndex(0);
        InitialPbModelChooser.setSelectedItem(((UPbFraction) myFraction).getInitialPbModel().getReduxLabDataElementName());

        InitialPbModelChooser.setEnabled(!isCompiled());
    }

    private void initPbBlankModelChooser() throws BadLabDataException {
        // set up PbBlankModelChooser
        PbBlankChooser.removeAllItems();
        ArrayList<AbstractRatiosDataModel> pbBlankModels = ((UPbFraction) myFraction).getMyLabData().getPbBlanks();
        for (int i = (pbBlankModels.size() > 1 ? 1 : 0); i < pbBlankModels.size(); i++) {
            PbBlankChooser.addItem(pbBlankModels.get(i).getReduxLabDataElementName());
        }

        PbBlankChooser.setSelectedIndex(0);
        PbBlankChooser.setSelectedItem(((UPbFractionI) myFraction).getPbBlank().getReduxLabDataElementName());

        PbBlankChooser.setEnabled(!isCompiled());
    }

    private void PopulateCorrectionsTab(ETFractionInterface myFraction) {
        // corrections tab
        pbFracCorrTechnique1_label.setBorder(null);
        pbFracCorrTechnique2_label.setBorder(null);
        pbFracCorrTechnique3_label.setBorder(null);
        pbFracCorrTechnique4_label.setBorder(null);

        meanAlphaPbImported_text.setBorder(null);
        meanAlphaPbImported_text.setText("");
        alphaPbCalculatedFromMeans_text.setBorder(null);
        alphaPbCalculatedFromMeans_text.setText("");
        alphaPbFromModel_text.setBorder(null);
        alphaPbFromModel_text.setText("");

        uFracCorrTechnique1_label.setBorder(null);
        uFracCorrTechnique2_label.setBorder(null);
        uFracCorrTechnique3_label.setBorder(null);
        uFracCorrTechnique4_label.setBorder(null);

        meanAlphaUImported_text.setBorder(null);
        meanAlphaUImported_text.setText("");
        alphaUCalculatedFromMeans_text.setBorder(null);
        alphaUCalculatedFromMeans_text.setText("");
        alphaUFromModel_text.setBorder(null);
        alphaUFromModel_text.setText("");

        // handle alpha pb - see logic in UPbFractionReducer ******************
        if (((UPbFractionI) myFraction).hasMeasuredLead()) {
            // perform analysis to determine which of the four techniques applies
            if (!((UPbFractionI) myFraction).isFractionationCorrectedPb()) {
                if (getMyFraction().getMeasuredRatioByName(MeasuredRatios.r202_205m.getName()).hasPositiveValue()) {
                    // case 2 display calculated values
                    alphaPbCalculatedFromMeans_text.setText(myFraction.getAnalysisMeasure(AnalysisMeasures.alphaPb.getName()).
                            getValue().movePointRight(2).
                            setScale(ReduxConstants.DEFAULT_CONSTANTS_SCALE, RoundingMode.HALF_UP).toPlainString());

                    pbFracCorrTechnique2_label.setBorder(BorderFactory.createLineBorder(Color.RED));
                    alphaPbCalculatedFromMeans_text.setBorder(BorderFactory.createLineBorder(Color.RED));
                } else {
                    // case 3 display model value
                    alphaPbFromModel_text.setText(((UPbFraction) getMyFraction()).getAlphaPbModel().getValue().
                            movePointRight(2).
                            setScale(ReduxConstants.DEFAULT_CONSTANTS_SCALE, RoundingMode.HALF_UP).toPlainString());

                    pbFracCorrTechnique3_label.setBorder(BorderFactory.createLineBorder(Color.RED));
                    alphaPbFromModel_text.setBorder(BorderFactory.createLineBorder(Color.RED));
                }
            } else {
                // case 1 display fractionation corrected mean from Tripoli
                meanAlphaPbImported_text.setText(((UPbFraction) myFraction).getMeanAlphaPb().movePointRight(2).
                        setScale(ReduxConstants.DEFAULT_CONSTANTS_SCALE, RoundingMode.HALF_UP).toPlainString());

                pbFracCorrTechnique1_label.setBorder(BorderFactory.createLineBorder(Color.RED));
                meanAlphaPbImported_text.setBorder(BorderFactory.createLineBorder(Color.RED));
            }
        } else {
            // case 4 display no values
            pbFracCorrTechnique4_label.setBorder(BorderFactory.createLineBorder(Color.RED));
        }

        // AlphaU - this logic is repeated in UPbFractionReducer, which calculates actual values       
        String tracerType = ((UPbFraction) getMyFraction()).getTracerType().trim();

        if (((UPbFractionI) myFraction).isFractionationCorrectedU()) {
            // case 1 display fractionation corrected mean from Tripoli
            meanAlphaUImported_text.setText(((UPbFraction) myFraction).getMeanAlphaU().movePointRight(2).
                    setScale(ReduxConstants.DEFAULT_CONSTANTS_SCALE, RoundingMode.HALF_UP).toPlainString());

            uFracCorrTechnique1_label.setBorder(BorderFactory.createLineBorder(Color.RED));
            meanAlphaUImported_text.setBorder(BorderFactory.createLineBorder(Color.RED));

        } else if (tracerType.equalsIgnoreCase("mixed 205-233-235")
                || tracerType.equalsIgnoreCase("mixed 202-205-233-235")
                || tracerType.equalsIgnoreCase("mixed 205-233-235-230Th")
                || tracerType.equalsIgnoreCase("mixed 205-233-236")
                || tracerType.equalsIgnoreCase("mixed 202-205-233-236")) {

            // case 2 display calculated values
            alphaUCalculatedFromMeans_text.setText(myFraction.getAnalysisMeasure(AnalysisMeasures.alphaU.getName()).
                    getValue().movePointRight(2).
                    setScale(ReduxConstants.DEFAULT_CONSTANTS_SCALE, RoundingMode.HALF_UP).toPlainString());

            uFracCorrTechnique2_label.setBorder(BorderFactory.createLineBorder(Color.RED));
            alphaUCalculatedFromMeans_text.setBorder(BorderFactory.createLineBorder(Color.RED));

        } else if (tracerType.equalsIgnoreCase("mixed 205-235")
                || tracerType.equalsIgnoreCase("mixed 208-235")) {

            // case 3 display model value
            alphaUFromModel_text.setText(((UPbFractionI) getMyFraction()).getAlphaUModel().getValue().
                    movePointRight(2).
                    setScale(ReduxConstants.DEFAULT_CONSTANTS_SCALE, RoundingMode.HALF_UP).toPlainString());

            uFracCorrTechnique3_label.setBorder(BorderFactory.createLineBorder(Color.RED));
            alphaUFromModel_text.setBorder(BorderFactory.createLineBorder(Color.RED));
        } else {
            // Case 4 display nothing
            uFracCorrTechnique4_label.setBorder(BorderFactory.createLineBorder(Color.RED));

        }

        assumedUBlankMass_text.setText(myFraction.getAnalysisMeasure(AnalysisMeasures.uBlankMassInGrams.getName()).getValue().
                multiply(ReduxConstants.PicoGramsPerGram).//
                setScale(ReduxConstants.DEFAULT_DEFAULTS_SCALE, RoundingMode.HALF_UP).toPlainString());
        uBlankMassOneSigma_text.setText(myFraction.getAnalysisMeasure(AnalysisMeasures.uBlankMassInGrams.getName()).getOneSigma().//
                multiply(ReduxConstants.PicoGramsPerGram).//
                setScale(ReduxConstants.DEFAULT_DEFAULTS_SCALE, RoundingMode.HALF_UP).toPlainString());

        r238_235Blank_text.setText(myFraction.getAnalysisMeasure(AnalysisMeasures.r238_235b.getName()).
                getValue().setScale(ReduxConstants.DEFAULT_DEFAULTS_SCALE, RoundingMode.HALF_UP).toPlainString());
        r238_235BlankError_text.setText(myFraction.getAnalysisMeasure(AnalysisMeasures.r238_235b.getName()).
                getOneSigma().setScale(ReduxConstants.DEFAULT_DEFAULTS_SCALE, RoundingMode.HALF_UP).toPlainString());

        r238_235Sample_text.setText(myFraction.getAnalysisMeasure(AnalysisMeasures.r238_235s.getName()).
                getValue().setScale(ReduxConstants.DEFAULT_DEFAULTS_SCALE, RoundingMode.HALF_UP).toPlainString());
        r238_235SampleError_text.setText(myFraction.getAnalysisMeasure(AnalysisMeasures.r238_235s.getName()).
                getOneSigma().setScale(ReduxConstants.DEFAULT_DEFAULTS_SCALE, RoundingMode.HALF_UP).toPlainString());
    }

    private void populateInitialPbModelFields(ETFractionInterface myFraction) {

        PbBlank_Mass_text.setText(myFraction.getAnalysisMeasure(AnalysisMeasures.pbBlankMassInGrams.getName()).getValue().
                multiply(ReduxConstants.PicoGramsPerGram).//
                setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE, RoundingMode.HALF_UP).toPlainString());
        pbBlankMassOneSigma_text.setText(myFraction.getAnalysisMeasure(AnalysisMeasures.pbBlankMassInGrams.getName()).getOneSigma().//
                multiply(ReduxConstants.PicoGramsPerGram).//
                setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE, RoundingMode.HALF_UP).toPlainString());

        AbstractRatiosDataModel initialPbModel = null;
        try {
            initialPbModel =//
                    ((UPbFraction) myFraction).getMyLabData().//
                    getAnInitialPbModel((String) InitialPbModelChooser.getSelectedItem());
//        } catch (BadLabDataException badLabDataException) {
//        }

            // update staceykramers here
            if (initialPbModel instanceof StaceyKramersInitialPbModelET) {
                ((FractionI)myFraction).calculateStaceyKramersInitialPbModelValues();
            }

            initialPbModel.initializeModel();

            AbstractRatiosDataView initialPbModelView = //
                    new RatiosDataViewNotEditable(initialPbModel, initialPbModelsTab_panel.getSize(), false);

            initialPbModelView.setBackground(initialPbModelsTab_panel.getBackground());
            initialPbModelView.setLocation(0, 35);
            initialPbModelView.setSize(initialPbModelsTab_panel.getWidth(), 450);
            for (Component c : initialPbModelsTab_panel.getComponents()) {
                if (c instanceof AbstractRatiosDataView) {
                    initialPbModelsTab_panel.remove(c);
                }
            }
            initialPbModelsTab_panel.add(initialPbModelView);
            initialPbModelsTab_panel.validate();
            initialPbModelView.repaint();
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
    }

    private void populatePbBlankModelFields(AbstractRatiosDataModel pbBlankModel) {

        pbBlankModel.initializeModel();

        Dimension pbBlankModelViewDimension = new Dimension(//
                blankTab_panel.getWidth(), blankTab_panel.getHeight() - 35);

        AbstractRatiosDataView pbBlankModelView = //
                new RatiosDataViewNotEditable(pbBlankModel, pbBlankModelViewDimension, false);

        pbBlankModelView.setBackground(blankTab_panel.getBackground());
        pbBlankModelView.setBounds(blankTab_panel.getBounds());
        pbBlankModelView.setLocation(0, 35);
        for (Component c : blankTab_panel.getComponents()) {
            if (c instanceof AbstractRatiosDataView) {
                blankTab_panel.remove(c);
            }
        }
        blankTab_panel.add(pbBlankModelView);
        blankTab_panel.validate();
        pbBlankModelView.repaint();

    }

    private void populateTracerFields(AbstractRatiosDataModel tracerModel) {
        tracerModel.initializeModel();

        Dimension tracerViewDimension = new Dimension(//
                tracerTab.getWidth(), tracerTab.getHeight() - 35);

        AbstractRatiosDataView tracerModelView = //
                new TracerUPbRatiosDataViewNotEditable(tracerModel, tracerViewDimension, false);

        tracerModelView.setBackground(tracerTab.getBackground());
        tracerModelView.setBounds(tracerTab.getBounds());
        tracerModelView.setLocation(0, 35);
        for (Component c : tracerTab.getComponents()) {
            if (c instanceof AbstractRatiosDataView) {
                tracerTab.remove(c);
            }
        }
        tracerTab.add(tracerModelView);
        tracerTab.validate();
        tracerModelView.repaint();
    }

    class InitialPbModelItemListener implements ItemListener {
        // This method is called only if a new item has been selected.

        private ETFractionInterface myFraction;

        public InitialPbModelItemListener(ETFractionInterface fraction) {
            this.myFraction = fraction;
        }

        public void itemStateChanged(ItemEvent evt) {
            // Get the affected initialPbModel
            AbstractRatiosDataModel initialPbModel = null;
            try {
                initialPbModel = ((UPbFraction) myFraction).getMyLabData().getAnInitialPbModel((String) evt.getItem());
            } catch (BadLabDataException ex) {
                new ETWarningDialog(ex).setVisible(true);
            }

            if (evt.getStateChange() == ItemEvent.SELECTED) {
                // Item was just selected
                populateInitialPbModelFields(myFraction);
            }
        }
    }

    class BlankItemListener implements ItemListener {
        // This method is called only if a new item has been selected.

        private ETFractionInterface myFraction;

        public BlankItemListener(ETFractionInterface fraction) {
            this.myFraction = fraction;
        }

        @Override
        public void itemStateChanged(ItemEvent evt) {
            // Get the affected blank
            AbstractRatiosDataModel blank = null;
            try {
                blank = ((UPbFraction) myFraction).getMyLabData().getAPbBlankModel((String) evt.getItem());
            } catch (BadLabDataException ex) {
                new ETWarningDialog(ex).setVisible(true);
            }

            if (evt.getStateChange() == ItemEvent.SELECTED) {
                // Item was just selected
                populatePbBlankModelFields(blank);

            } else if (evt.getStateChange() == ItemEvent.DESELECTED) {
                // Item is no longer selected
            }
        }
    }

    class TracerItemListener implements ItemListener {
        // This method is called only if a new item has been selected.

        private ETFractionInterface myFraction;

        public TracerItemListener(ETFractionInterface fraction) {
            this.myFraction = fraction;
        }

        @Override
        public void itemStateChanged(ItemEvent evt) {

            // Get the affected tracer
            AbstractRatiosDataModel tracer = null;
            try {
                tracer = ((UPbFraction) myFraction).getMyLabData().getATracerModel((String) evt.getItem());
            } catch (BadLabDataException ex) {
                new ETWarningDialog(ex).setVisible(true);
            }

            if (evt.getStateChange() == ItemEvent.SELECTED) {

                // Item was just selected
                populateTracerFields(tracer);

            } else if (evt.getStateChange() == ItemEvent.DESELECTED) {
                // Item is no longer selected
            }
        }
    }

    private void save(ETFractionInterface myFraction)
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

        savedZirconStateOfFraction = ((FractionI)myFraction).isZircon();

        try {
            resetKwikiTabValues(myFraction);
        } catch (Exception e) {
        }

        myFraction.setFractionID(fractionID_text.getText());
        myFraction.setGrainID(fractionID_text.getText());

        // feb 2009
        // test for zircon state change for ReductionHandler
        if (((FractionI)myFraction).isZircon() != fractionIsZircon_CheckBox.isSelected()) {
            ((FractionI)myFraction).setZircon(fractionIsZircon_CheckBox.isSelected());
            ((UPbFraction) myFraction).initializeReductionHandler();
        }

        try {
            myFraction.getAnalysisMeasure(AnalysisMeasures.fractionMass.getName()).
                    setValue(new BigDecimal(fractionMass_text.getText()));

            // Pb tab
            myFraction.getMeasuredRatioByName(MeasuredRatios.r206_204m.getName()).setValue(new BigDecimal(r206_204_text.getText()));
            myFraction.getMeasuredRatioByName(MeasuredRatios.r206_204m.getName()).setOneSigma(new BigDecimal(e206_204_text.getText()));

            myFraction.getMeasuredRatioByName(MeasuredRatios.r207_204m.getName()).setValue(new BigDecimal(r207_204_text.getText()));
            myFraction.getMeasuredRatioByName(MeasuredRatios.r207_204m.getName()).setOneSigma(new BigDecimal(e207_204_text.getText()));

            myFraction.getMeasuredRatioByName(MeasuredRatios.r208_204m.getName()).setValue(new BigDecimal(r208_204_text.getText()));
            myFraction.getMeasuredRatioByName(MeasuredRatios.r208_204m.getName()).setOneSigma(new BigDecimal(e208_204_text.getText()));

            myFraction.getMeasuredRatioByName(MeasuredRatios.r206_207m.getName()).setValue(new BigDecimal(r206_207_text.getText()));
            myFraction.getMeasuredRatioByName(MeasuredRatios.r206_207m.getName()).setOneSigma(new BigDecimal(e206_207_text.getText()));

            myFraction.getMeasuredRatioByName(MeasuredRatios.r206_208m.getName()).setValue(new BigDecimal(r206_208_text.getText()));
            myFraction.getMeasuredRatioByName(MeasuredRatios.r206_208m.getName()).setOneSigma(new BigDecimal(e206_208_text.getText()));

            myFraction.getMeasuredRatioByName(MeasuredRatios.r204_205m.getName()).setValue(new BigDecimal(r204_205_text.getText()));
            myFraction.getMeasuredRatioByName(MeasuredRatios.r204_205m.getName()).setOneSigma(new BigDecimal(e204_205_text.getText()));

            myFraction.getMeasuredRatioByName(MeasuredRatios.r206_205m.getName()).setValue(new BigDecimal(r206_205_text.getText()));
            myFraction.getMeasuredRatioByName(MeasuredRatios.r206_205m.getName()).setOneSigma(new BigDecimal(e206_205_text.getText()));

            myFraction.getMeasuredRatioByName(MeasuredRatios.r207_205m.getName()).setValue(new BigDecimal(r207_205_text.getText()));
            myFraction.getMeasuredRatioByName(MeasuredRatios.r207_205m.getName()).setOneSigma(new BigDecimal(e207_205_text.getText()));

            myFraction.getMeasuredRatioByName(MeasuredRatios.r208_205m.getName()).setValue(new BigDecimal(r208_205_text.getText()));
            myFraction.getMeasuredRatioByName(MeasuredRatios.r208_205m.getName()).setOneSigma(new BigDecimal(e208_205_text.getText()));

            myFraction.getMeasuredRatioByName(MeasuredRatios.r202_205m.getName()).setValue(new BigDecimal(r202_205_text.getText()));
            myFraction.getMeasuredRatioByName(MeasuredRatios.r202_205m.getName()).setOneSigma(new BigDecimal(e202_205_text.getText()));

            ((UPbFraction) myFraction).setNotesPb(notesPb.getText());

            // U tab
            saveUraniumRatios(myFraction);

            ((UPbFraction) myFraction).setNotesU(notesU.getText());

            // tracer tab
            myFraction.getAnalysisMeasure(AnalysisMeasures.tracerMassInGrams.getName()).
                    setOneSigma(new BigDecimal(tracerMassOneSigmaAbs_text.getText()));

            try {
                ((UPbFraction) myFraction).setTracer(((UPbFraction) myFraction).//
                        getMyLabData().getATracerModel((String) tracerChooser.getSelectedItem()));
            } catch (BadLabDataException ex) {
                new ETWarningDialog(ex).setVisible(true);
            }

            // aug 2010, reset tracer mass to zero if no tracer chosen ... tracer mass is a test for legacy status
            if (((UPbFraction) myFraction).hasATracer()) {
                myFraction.getAnalysisMeasure(AnalysisMeasures.tracerMassInGrams.getName()).
                        setValue(new BigDecimal(tracerMass_text.getText()));
            } else {
                myFraction.getAnalysisMeasure(AnalysisMeasures.tracerMassInGrams.getName()).
                        setValue(BigDecimal.ZERO);
            }

            // Oxide Correction
            // first check if oxide radio button is on and 18o/16o is missing
            if (fractionIsOxide_rb.isSelected()) {
                if (r18O_16OUsed_textOnUTab.getText().isEmpty() || Double.valueOf(r18O_16OUsed_textOnUTab.getText()) <= 0.0) {
                    // force back to being a metal
                    fractionIsMetal_rb.doClick();//.setSelected( true );
                }
            }
            // these fields are used as data holders even when not visible
            myFraction.getAnalysisMeasure(AnalysisMeasures.r18O_16O.getName()).
                    setValue(new BigDecimal(r18O_16OUsed_text.getText()));

            myFraction.getAnalysisMeasure(AnalysisMeasures.r18O_16O.getName()).
                    setOneSigma(new BigDecimal(r18O_16OUsedError_text.getText()));

            myFraction.getAnalysisMeasure(AnalysisMeasures.r18O_16O_revised.getName()).
                    setValue(new BigDecimal(r18O_16ORevised_text.getText()));

            myFraction.getAnalysisMeasure(AnalysisMeasures.r18O_16O_revised.getName()).
                    setOneSigma(new BigDecimal(r18O_16OUsedError_text.getText()));// same error

            // U Sample Components
            myFraction.getAnalysisMeasure(AnalysisMeasures.uBlankMassInGrams.getName()).
                    setValue(new BigDecimal(assumedUBlankMass_text.getText()).movePointLeft(12));

            myFraction.getAnalysisMeasure(AnalysisMeasures.uBlankMassInGrams.getName()).
                    setOneSigma(new BigDecimal(uBlankMassOneSigma_text.getText()).movePointLeft(12));

            myFraction.getAnalysisMeasure(AnalysisMeasures.r238_235b.getName()).
                    setValue(new BigDecimal(r238_235Blank_text.getText()));

            myFraction.getAnalysisMeasure(AnalysisMeasures.r238_235b.getName()).
                    setOneSigma(new BigDecimal(r238_235BlankError_text.getText()));

            myFraction.getAnalysisMeasure(AnalysisMeasures.r238_235s.getName()).
                    setValue(new BigDecimal(r238_235Sample_text.getText()));

            myFraction.getAnalysisMeasure(AnalysisMeasures.r238_235s.getName()).
                    setOneSigma(new BigDecimal(r238_235SampleError_text.getText()));

            // PbBlank tab***********************************************
            try {
                ((UPbFraction) myFraction).setPbBlank(((UPbFraction) myFraction).//
                        getMyLabData().getAPbBlankModel((String) PbBlankChooser.getSelectedItem()));
            } catch (BadLabDataException ex) {
                new ETWarningDialog(ex).setVisible(true);
            }

            // Initial Pb Model Tab************************************
            try {
                ((UPbFraction) myFraction).setInitialPbModel(((UPbFraction) myFraction).//
                        getMyLabData().getAnInitialPbModel((String) InitialPbModelChooser.getSelectedItem()));
            } catch (BadLabDataException ex) {
                new ETWarningDialog(ex).setVisible(true);
            }

            // set fraction's pbBlankMass
            myFraction.getAnalysisMeasure(AnalysisMeasures.pbBlankMassInGrams.getName()).
                    setValue(new BigDecimal(PbBlank_Mass_text.getText()).movePointLeft(12));

            myFraction.getAnalysisMeasure(AnalysisMeasures.pbBlankMassInGrams.getName()).
                    setOneSigma(new BigDecimal(pbBlankMassOneSigma_text.getText()).movePointLeft(12));

            // publication details
            ((FractionI)myFraction).setMineralName((String) mineralNameChooser.getSelectedItem());
            ((FractionI) myFraction).setSettingType((String) settingTypeChooser.getSelectedItem());
            myFraction.setNumberOfGrains(Integer.parseInt(countOfGrains_text.getText()));

            ((FractionI) myFraction).setPhysicallyAbraded(physicallyAbraded_chkBox.isSelected());
            ((FractionI) myFraction).setLeachedInHFAcid(leachedInHFAcid_chkBox.isSelected());
            ((FractionI) myFraction).setAnnealedAndChemicallyAbraded(annealedChemicallyAbraded_chkBox.isSelected());
            ((FractionI) myFraction).setChemicallyPurifiedUPb(chemicallyPurifiedUPb_chkBox.isSelected());
            ((FractionI) myFraction).setAnalysisFractionComment(fractionComment_text.getText());

        } catch (NumberFormatException ex) {
            throw new ETException(
                    this,
                    "Blank numeric fields not allowed ... please enter a value.");

        }

        myFraction.setChanged(true);
        // the next line undoes the default deleted status of a new default fraction
        myFraction.setDeleted(false);

        // enable delete and restore button if off because of new default fraction
        delete_button.setEnabled(myFraction.isChanged());

        ((UPbFraction) myFraction).reduceData(isCompiled());

    }

    private void saveUraniumRatios(ETFractionInterface myFraction) {
        myFraction.getMeasuredRatioByName(MeasuredRatios.r238_235m.getName()).setValue(new BigDecimal(r238_235_text.getText()));
        myFraction.getMeasuredRatioByName(MeasuredRatios.r238_235m.getName()).setOneSigma(new BigDecimal(e238_235_text.getText()));

        myFraction.getMeasuredRatioByName(MeasuredRatios.r233_235m.getName()).setValue(new BigDecimal(r233_235_text.getText()));
        myFraction.getMeasuredRatioByName(MeasuredRatios.r233_235m.getName()).setOneSigma(new BigDecimal(e233_235_text.getText()));

        myFraction.getMeasuredRatioByName(MeasuredRatios.r238_233m.getName()).setValue(new BigDecimal(r238_233_text.getText()));
        myFraction.getMeasuredRatioByName(MeasuredRatios.r238_233m.getName()).setOneSigma(new BigDecimal(e238_233_text.getText()));

        myFraction.getMeasuredRatioByName(MeasuredRatios.r233_236m.getName()).setValue(new BigDecimal(r233_236_text.getText()));
        myFraction.getMeasuredRatioByName(MeasuredRatios.r233_236m.getName()).setOneSigma(new BigDecimal(e233_236_text.getText()));

        // jan 2011
        myFraction.getMeasuredRatioByName(MeasuredRatios.r238_236m.getName()).setValue(new BigDecimal(r238_236_text.getText()));
        myFraction.getMeasuredRatioByName(MeasuredRatios.r238_236m.getName()).setOneSigma(new BigDecimal(e238_236_text.getText()));

        ((UPbFraction) myFraction).setInputOneSigmaPct(new BigDecimal(inputPerCentUncertainty_text.getText()));
        ((UPbFraction) myFraction).setInputDate206_238r(new BigDecimal(inputEst206_238Date_text.getText()).movePointRight(6));
        ((UPbFraction) myFraction).getInputAlphaU().//
                setValue(new BigDecimal(inputAlphaU_text.getText()).movePointLeft(2));
        if (((UPbFraction) myFraction).tracerTypeIncludes233()) {
            BigDecimal oneSigmaAbs = ValueModel.//
                    convertOneSigmaPctToAbsIfRequired(//
                            ((UPbFraction) myFraction).getInputAlphaU(), //
                            ((UPbFraction) myFraction).getInputOneSigmaPct());
            ((UPbFraction) myFraction).getInputAlphaU().setOneSigma(oneSigmaAbs);
        } else {
            ((UPbFraction) myFraction).getInputAlphaU().//
                    setOneSigma(new BigDecimal(inputAlphaUOneSigmaAbs_text.getText()).movePointLeft(2));
        }
    }

    private boolean ExportUPbFractionPerInputSchema(ETFractionInterface myFraction) {

        resetKwikiTabValues(myFraction);

        boolean retval = true;
        File selectedFile = null;

        // first check on saving
        int response = JOptionPane.showConfirmDialog(this,
                new String[]{"Save data and continue with export ?"},
                "ET Redux Warning",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        if (response == JOptionPane.NO_OPTION) {
            retval = false;
        } else {
            try {
                save(myFraction);
            } catch (ETException ex) {
                ex.printStackTrace();
                new ETWarningDialog(ex).setVisible(true);
            }
        }

        if (retval) {
            // proceed with export
            // forces dialog to front
            setAlwaysOnTop(false);

            String dialogTitle = "Save this UPbFraction as xml: *.xml";
            final String fileExtension = ".xml";
            String fractionFileName
                    = ((UPbFraction) getMyFraction()).getSampleName()//
                    + "_" + getMyFraction().getFractionID()//
                    + "_" + ((UPbFraction) getMyFraction()).getRatioType()//
                    + fileExtension;

            FileFilter nonMacFileFilter = new XMLFileFilter();

            selectedFile = FileHelper.AllPlatformSaveAs(
                    new Frame(), dialogTitle, null, fileExtension, fractionFileName, nonMacFileFilter);

            setAlwaysOnTop(true);

        }

        if (selectedFile != null) {
            try {
                // export
                ((UPbFraction) getMyFraction()).serializeXMLObject(selectedFile.getCanonicalPath());
            } catch (IOException ex) {
//                ex.printStackTrace();
            }
        }

        //  setVisible(true);
        return (selectedFile != null);
    }

    /**
     *
     * @return
     */
    public ETFractionInterface getMyFraction() {
        return myFraction;
    }

    /**
     *
     * @param myFraction
     */
    public void setMyFraction(ETFractionInterface myFraction) {
        this.myFraction = myFraction;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        fractionMetalOrOxide_bg = new javax.swing.ButtonGroup();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        details_Pane = new javax.swing.JTabbedPane();
        PbDataTab = new javax.swing.JPanel();
        rawRatio_label = new javax.swing.JLabel();
        pctStdErr_label = new javax.swing.JLabel();
        r206_204_label = new javax.swing.JLabel();
        r206_204_text = new javax.swing.JTextField();
        e206_204_text = new javax.swing.JTextField();
        r206_207_label = new javax.swing.JLabel();
        r206_207_text = new javax.swing.JTextField();
        e206_207_text = new javax.swing.JTextField();
        r206_208_label = new javax.swing.JLabel();
        r206_208_text = new javax.swing.JTextField();
        e206_208_text = new javax.swing.JTextField();
        r206_205_label = new javax.swing.JLabel();
        r206_205_text = new javax.swing.JTextField();
        e206_205_text = new javax.swing.JTextField();
        r207_205_label = new javax.swing.JLabel();
        r207_205_text = new javax.swing.JTextField();
        e207_205_text = new javax.swing.JTextField();
        r208_205_label = new javax.swing.JLabel();
        r208_205_text = new javax.swing.JTextField();
        e208_205_text = new javax.swing.JTextField();
        r202_205_label = new javax.swing.JLabel();
        historyPbPane = new javax.swing.JTabbedPane();
        notesPbPane = new javax.swing.JScrollPane();
        notesPb = new javax.swing.JEditorPane();
        sourcePbPanel = new javax.swing.JPanel();
        sourcePbFile_label = new javax.swing.JLabel();
        sourcePbFileDisplay = new javax.swing.JLabel();
        sourcePbFileOpen = new javax.swing.JButton();
        pedigreePbPanel = new javax.swing.JPanel();
        pedigreePbLabel = new javax.swing.JLabel();
        pedigreePbDisplay = new javax.swing.JLabel();
        e202_205_text = new javax.swing.JTextField();
        r202_205_text = new javax.swing.JTextField();
        pbFracCorrFootnote_label = new javax.swing.JLabel();
        r207_204_label = new javax.swing.JLabel();
        r207_204_text = new javax.swing.JTextField();
        e207_204_text = new javax.swing.JTextField();
        e208_204_text = new javax.swing.JTextField();
        r208_204_text = new javax.swing.JTextField();
        r208_204_label = new javax.swing.JLabel();
        r204_205_label = new javax.swing.JLabel();
        r204_205_text = new javax.swing.JTextField();
        e204_205_text = new javax.swing.JTextField();
        r206_204_FracCorr_label = new javax.swing.JLabel();
        r206_207_FracCorr_label = new javax.swing.JLabel();
        r206_208_FracCorr_label = new javax.swing.JLabel();
        r206_205_FracCorr_label = new javax.swing.JLabel();
        r207_205_FracCorr_label = new javax.swing.JLabel();
        r208_205_FracCorr_label = new javax.swing.JLabel();
        r207_204_FracCorr_label = new javax.swing.JLabel();
        r208_204_FracCorr_label = new javax.swing.JLabel();
        r204_205_FracCorr_label = new javax.swing.JLabel();
        UDataTab = new javax.swing.JPanel();
        rawRatio_label2 = new javax.swing.JLabel();
        pctStdErr_label2 = new javax.swing.JLabel();
        r238_235_label = new javax.swing.JLabel();
        r238_235_text = new javax.swing.JTextField();
        e238_235_text = new javax.swing.JTextField();
        r233_235_label = new javax.swing.JLabel();
        r233_235_text = new javax.swing.JTextField();
        e233_235_text = new javax.swing.JTextField();
        historyUPane = new javax.swing.JTabbedPane();
        notesUPane = new javax.swing.JScrollPane();
        notesU = new javax.swing.JEditorPane();
        sourceUPanel = new javax.swing.JPanel();
        sourceUFile_label = new javax.swing.JLabel();
        sourceUFileDisplay = new javax.swing.JLabel();
        sourceUFileOpen = new javax.swing.JButton();
        pedigreeUPanel = new javax.swing.JPanel();
        pedigreeULabel = new javax.swing.JLabel();
        pedigreeUDisplay = new javax.swing.JLabel();
        r238_235_FracCorr_label = new javax.swing.JLabel();
        r233_235_FracCorr_label = new javax.swing.JLabel();
        uFracCorrFootnote_label = new javax.swing.JLabel();
        r233_236_label = new javax.swing.JLabel();
        e233_236_text = new javax.swing.JTextField();
        r233_236_text = new javax.swing.JTextField();
        r238_233_FracCorr_label = new javax.swing.JLabel();
        r238_233_label = new javax.swing.JLabel();
        r238_233_text = new javax.swing.JTextField();
        e238_233_text = new javax.swing.JTextField();
        autoUSettings_panel = new javax.swing.JPanel();
        inputPerCentUncertainty_text = new javax.swing.JTextField();
        r233_236_label1 = new javax.swing.JLabel();
        rawRatio_label3 = new javax.swing.JLabel();
        r233_236_label2 = new javax.swing.JLabel();
        inputEst206_238Date_text = new javax.swing.JTextField();
        restoreEstDate_button = new javax.swing.JButton();
        r233_236_label3 = new javax.swing.JLabel();
        r233_236_label4 = new javax.swing.JLabel();
        inputAlphaU_text = new javax.swing.JTextField();
        r233_236_label5 = new javax.swing.JLabel();
        inputAlphaUOneSigmaAbs_text = new javax.swing.JTextField();
        fractionIsMetal_rb = new javax.swing.JRadioButton();
        fractionIsOxide_rb = new javax.swing.JRadioButton();
        r18O_16OUsed_textOnUTab = new javax.swing.JTextField();
        r238_236_label = new javax.swing.JLabel();
        r238_236_text = new javax.swing.JTextField();
        e238_236_text = new javax.swing.JTextField();
        tracerTab = new javax.swing.JPanel();
        tracerChooser = new javax.swing.JComboBox<String>();
        chooseTracer_label = new javax.swing.JLabel();
        tracerMass_label = new javax.swing.JLabel();
        tracerMass_text = new javax.swing.JTextField();
        tracerMassOneSigmaAbs_label = new javax.swing.JLabel();
        tracerMassOneSigmaAbs_text = new javax.swing.JTextField();
        correctionsTab_panel = new javax.swing.JPanel();
        pbFractionationTitle_label = new javax.swing.JLabel();
        meanAlphaPbValueUsed_label = new javax.swing.JLabel();
        alphaPbFromModel_text = new javax.swing.JTextField();
        uFractionationTitle_label = new javax.swing.JLabel();
        meanAlphaUImported_label = new javax.swing.JLabel();
        uFracCorrTechnique1_label = new javax.swing.JLabel();
        meanAlphaUImported_text = new javax.swing.JTextField();
        uFracCorrTechnique2_label = new javax.swing.JLabel();
        alphaUCalculatedFromMeans_text = new javax.swing.JTextField();
        uFracCorrTechnique3_label = new javax.swing.JLabel();
        alphaUFromModel_text = new javax.swing.JTextField();
        uFracCorrTechnique4_label = new javax.swing.JLabel();
        refreshFractionation_Button = new javax.swing.JButton();
        pbFracCorrTechnique3_label = new javax.swing.JLabel();
        pbFracCorrTechnique1_label = new javax.swing.JLabel();
        pbFracCorrTechnique2_label = new javax.swing.JLabel();
        assumedUBlankMass_label = new javax.swing.JLabel();
        assumedUBlankMass_text = new javax.swing.JTextField();
        uBlankMassOneSigma_text = new javax.swing.JTextField();
        blankR238_235_label = new javax.swing.JLabel();
        r238_235Blank_text = new javax.swing.JTextField();
        r238_235BlankError_text = new javax.swing.JTextField();
        blankRatio_label1 = new javax.swing.JLabel();
        blankOneSigmaPctStdErr_label1 = new javax.swing.JLabel();
        oxideCorrectionTitle_label1 = new javax.swing.JLabel();
        sampleR238_235_label = new javax.swing.JLabel();
        r238_235Sample_text = new javax.swing.JTextField();
        r238_235SampleError_text = new javax.swing.JTextField();
        meanAlphaPbImported_text = new javax.swing.JTextField();
        alphaPbCalculatedFromMeans_text = new javax.swing.JTextField();
        pbFracCorrTechnique4_label = new javax.swing.JLabel();
        oxidePanel = new javax.swing.JPanel();
        oxideCorrectionTitle_label = new javax.swing.JLabel();
        blankOneSigmaPctStdErr_label2 = new javax.swing.JLabel();
        r18O_16OUsed_label = new javax.swing.JLabel();
        r18O_16OUsed_text = new javax.swing.JTextField();
        r18O_16OUsedError_text = new javax.swing.JTextField();
        r18O_16ORevised_label = new javax.swing.JLabel();
        r18O_16ORevised_text = new javax.swing.JTextField();
        blankTab_panel = new javax.swing.JPanel();
        choosePbBlank_label = new javax.swing.JLabel();
        PbBlankChooser = new javax.swing.JComboBox<String>();
        initialPbModelsTab_panel = new javax.swing.JPanel();
        InitialPbModelChooser = new javax.swing.JComboBox<String>();
        chooseInitialPbModel_label = new javax.swing.JLabel();
        pbBlankMass_label = new javax.swing.JLabel();
        PbBlank_Mass_text = new javax.swing.JTextField();
        pbBlankMassOneSigma_label = new javax.swing.JLabel();
        pbBlankMassOneSigma_text = new javax.swing.JTextField();
        staceyKramerCalculator_Panel = new javax.swing.JPanel();
        CalculatedModelInstructions_label = new javax.swing.JLabel();
        estimatedAgeInMA_text = new javax.swing.JTextField();
        refreshCalculations_JButton =  new ET_JButton("Update Model");
        relativeUncertainty_label = new javax.swing.JLabel();
        relativeUncertainty_text = new javax.swing.JTextField();
        correlationCoefficients_label = new javax.swing.JLabel();
        correlationCoefficients_text = new javax.swing.JTextField();
        reductionsTab = new javax.swing.JPanel();
        refreshOutputs_Button = new javax.swing.JButton();
        printToFileDataValues_Button = new javax.swing.JButton();
        printToFileMatrices_Button = new javax.swing.JButton();
        testMathMachineAlphaU = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        testSingleDisplay = new javax.swing.JButton();
        archivingDetails_panel = new javax.swing.JPanel();
        mineralNameChooser = new javax.swing.JComboBox<String>();
        chooseMineral_label = new javax.swing.JLabel();
        chooseSettingType_label = new javax.swing.JLabel();
        settingTypeChooser = new javax.swing.JComboBox<String>();
        countOfGrains_label = new javax.swing.JLabel();
        physicallyAbraded_chkBox = new javax.swing.JCheckBox();
        leachedInHFAcid_chkBox = new javax.swing.JCheckBox();
        annealedChemicallyAbraded_chkBox = new javax.swing.JCheckBox();
        chemicallyPurifiedUPb_chkBox = new javax.swing.JCheckBox();
        fractionComment_label = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        fractionComment_text = new javax.swing.JTextArea();
        countOfGrains_text = new javax.swing.JTextField();
        publicationTimeStamp_label = new javax.swing.JLabel();
        publicationTimeStamp_text = new javax.swing.JTextField();
        selectImageFile_button = new javax.swing.JButton();
        imageThumbnail_Panel = new javax.swing.JPanel();
        imageThumbnail_label = new javax.swing.JLabel();
        chooseSettingType_label1 = new javax.swing.JLabel();
        fractionID_label2 = new javax.swing.JLabel();
        useLabDefaults_button = new javax.swing.JButton();
        kwikiTab = new javax.swing.JLayeredPane();
        fractionPanel = new javax.swing.JPanel();
        fractionMass_text = new javax.swing.JTextField();
        fractionID_text = new javax.swing.JTextField();
        fractionMassInGrams_label = new javax.swing.JLabel();
        fractionIsZircon_CheckBox = new javax.swing.JCheckBox();
        fraction_Chooser = new javax.swing.JComboBox<ETFractionInterface>();
        fractionID_label1 = new javax.swing.JLabel();
        toggleStartStopLiveUpdate_button = new javax.swing.JButton();
        fractionID_label = new javax.swing.JLabel();
        buttonsPanel = new javax.swing.JPanel();
        saveAndClose_button = new javax.swing.JButton();
        delete_button = new javax.swing.JButton();
        close_button = new javax.swing.JButton();
        export_button = new javax.swing.JButton();
        save_button = new javax.swing.JButton();
        aliquotInfo_panel = new javax.swing.JPanel();
        aliquotName_label = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
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

        PbDataTab.setBackground(new java.awt.Color(246, 254, 235));
        PbDataTab.setBorder(new javax.swing.border.MatteBorder(null));
        PbDataTab.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        PbDataTab.setName(""); // NOI18N
        PbDataTab.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        rawRatio_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        rawRatio_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        rawRatio_label.setText("value");
        PbDataTab.add(rawRatio_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(185, 7, 70, -1));

        pctStdErr_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        pctStdErr_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pctStdErr_label.setText("1-sigma pct");
        PbDataTab.add(pctStdErr_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(420, 7, 70, -1));

        r206_204_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        r206_204_label.setText("206 / 204");
        PbDataTab.add(r206_204_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(29, 29, 70, -1));

        r206_204_text.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        r206_204_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        r206_204_text.setText("0.0");
        r206_204_text.setMinimumSize(new java.awt.Dimension(36, 19));
        r206_204_text.setPreferredSize(new java.awt.Dimension(36, 19));
        PbDataTab.add(r206_204_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(105, 27, 225, -1));

        e206_204_text.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        e206_204_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        e206_204_text.setText("0.0");
        e206_204_text.setMinimumSize(new java.awt.Dimension(36, 19));
        e206_204_text.setPreferredSize(new java.awt.Dimension(36, 19));
        PbDataTab.add(e206_204_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(336, 27, 225, -1));

        r206_207_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        r206_207_label.setText("206 / 207");
        PbDataTab.add(r206_207_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(29, 120, 70, -1));

        r206_207_text.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        r206_207_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        r206_207_text.setText("0.0");
        r206_207_text.setMinimumSize(new java.awt.Dimension(36, 19));
        r206_207_text.setPreferredSize(new java.awt.Dimension(36, 19));
        PbDataTab.add(r206_207_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(105, 118, 225, -1));

        e206_207_text.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        e206_207_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        e206_207_text.setText("0.0");
        e206_207_text.setMinimumSize(new java.awt.Dimension(36, 19));
        e206_207_text.setPreferredSize(new java.awt.Dimension(36, 19));
        PbDataTab.add(e206_207_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(336, 118, 225, -1));

        r206_208_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        r206_208_label.setText("206 / 208");
        PbDataTab.add(r206_208_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(29, 151, 70, -1));

        r206_208_text.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        r206_208_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        r206_208_text.setText("0.0");
        r206_208_text.setMinimumSize(new java.awt.Dimension(36, 19));
        r206_208_text.setPreferredSize(new java.awt.Dimension(36, 19));
        PbDataTab.add(r206_208_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(105, 149, 225, -1));

        e206_208_text.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        e206_208_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        e206_208_text.setText("0.0");
        e206_208_text.setMinimumSize(new java.awt.Dimension(36, 19));
        e206_208_text.setPreferredSize(new java.awt.Dimension(36, 19));
        PbDataTab.add(e206_208_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(336, 149, 225, -1));

        r206_205_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        r206_205_label.setText("206 / 205");
        PbDataTab.add(r206_205_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(29, 205, 70, -1));

        r206_205_text.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        r206_205_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        r206_205_text.setText("0.0");
        PbDataTab.add(r206_205_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(105, 200, 225, -1));

        e206_205_text.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        e206_205_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        e206_205_text.setText("0.0");
        e206_205_text.setMinimumSize(new java.awt.Dimension(40, 19));
        e206_205_text.setPreferredSize(new java.awt.Dimension(36, 19));
        PbDataTab.add(e206_205_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(336, 203, 225, -1));

        r207_205_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        r207_205_label.setText("207 / 205");
        PbDataTab.add(r207_205_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(29, 234, 70, -1));

        r207_205_text.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        r207_205_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        r207_205_text.setText("0.0");
        PbDataTab.add(r207_205_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(105, 229, 225, -1));

        e207_205_text.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        e207_205_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        e207_205_text.setText("0.0");
        e207_205_text.setMinimumSize(new java.awt.Dimension(40, 19));
        e207_205_text.setPreferredSize(new java.awt.Dimension(36, 19));
        PbDataTab.add(e207_205_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(336, 232, 225, -1));

        r208_205_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        r208_205_label.setText("208 / 205");
        PbDataTab.add(r208_205_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(29, 266, 70, -1));

        r208_205_text.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        r208_205_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        r208_205_text.setText("0.0");
        PbDataTab.add(r208_205_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(105, 261, 225, -1));

        e208_205_text.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        e208_205_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        e208_205_text.setText("0.0");
        e208_205_text.setMinimumSize(new java.awt.Dimension(40, 19));
        e208_205_text.setPreferredSize(new java.awt.Dimension(36, 19));
        PbDataTab.add(e208_205_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(336, 264, 225, -1));

        r202_205_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        r202_205_label.setText("202 / 205");
        PbDataTab.add(r202_205_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(29, 298, 70, -1));

        notesPb.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        notesPb.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                notesPbKeyReleased(evt);
            }
        });
        notesPbPane.setViewportView(notesPb);

        historyPbPane.addTab("Notes", notesPbPane);

        sourcePbFile_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        sourcePbFile_label.setText("Pb Source  File:");

        sourcePbFileDisplay.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        sourcePbFileDisplay.setText("No Source File for Pb Fraction Specified");

        sourcePbFileOpen.setFont(new java.awt.Font("Lucida Grande", 1, 10)); // NOI18N
        sourcePbFileOpen.setText("Open Pb Source File");
        sourcePbFileOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sourcePbFileOpenActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout sourcePbPanelLayout = new org.jdesktop.layout.GroupLayout(sourcePbPanel);
        sourcePbPanel.setLayout(sourcePbPanelLayout);
        sourcePbPanelLayout.setHorizontalGroup(
            sourcePbPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(sourcePbPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(sourcePbPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, sourcePbPanelLayout.createSequentialGroup()
                        .add(sourcePbPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, sourcePbFileDisplay, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1049, Short.MAX_VALUE)
                            .add(org.jdesktop.layout.GroupLayout.LEADING, sourcePbFile_label))
                        .addContainerGap())
                    .add(sourcePbPanelLayout.createSequentialGroup()
                        .add(sourcePbFileOpen, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1040, Short.MAX_VALUE)
                        .add(15, 15, 15))))
        );
        sourcePbPanelLayout.setVerticalGroup(
            sourcePbPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(sourcePbPanelLayout.createSequentialGroup()
                .add(sourcePbFile_label)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(sourcePbFileDisplay, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 24, Short.MAX_VALUE)
                .add(sourcePbFileOpen, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 29, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        historyPbPane.addTab("Source", sourcePbPanel);

        pedigreePbLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        pedigreePbLabel.setText("Pb source file generated from:");

        pedigreePbDisplay.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        pedigreePbDisplay.setText("No Pedigree for Pb fraction specified");

        org.jdesktop.layout.GroupLayout pedigreePbPanelLayout = new org.jdesktop.layout.GroupLayout(pedigreePbPanel);
        pedigreePbPanel.setLayout(pedigreePbPanelLayout);
        pedigreePbPanelLayout.setHorizontalGroup(
            pedigreePbPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pedigreePbPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(pedigreePbPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pedigreePbLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 264, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(pedigreePbDisplay, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1049, Short.MAX_VALUE))
                .addContainerGap())
        );
        pedigreePbPanelLayout.setVerticalGroup(
            pedigreePbPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pedigreePbPanelLayout.createSequentialGroup()
                .add(pedigreePbLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pedigreePbDisplay, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 45, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(38, Short.MAX_VALUE))
        );

        historyPbPane.addTab("Pedigree", pedigreePbPanel);

        PbDataTab.add(historyPbPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 433, -1, -1));

        e202_205_text.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        e202_205_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        e202_205_text.setText("0.0");
        e202_205_text.setMinimumSize(new java.awt.Dimension(40, 19));
        e202_205_text.setPreferredSize(new java.awt.Dimension(36, 19));
        PbDataTab.add(e202_205_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(336, 296, 225, -1));

        r202_205_text.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        r202_205_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        r202_205_text.setText("0.0");
        PbDataTab.add(r202_205_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(105, 293, 225, -1));

        pbFracCorrFootnote_label.setForeground(new java.awt.Color(0, 102, 0));
        pbFracCorrFootnote_label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/earthtime/UPb_Redux/images/icon_checkmark.png"))); // NOI18N
        pbFracCorrFootnote_label.setText("<html>Denotes that ratio was fractionation-corrected datum-by-datum.  See Corrections tab for alpha Pb.</html>");
        pbFracCorrFootnote_label.setToolTipText("Ratio was fractionation-corrected datum-by-datum.  See Corrections tab for alphaU.");
        PbDataTab.add(pbFracCorrFootnote_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(11, 383, 364, -1));

        r207_204_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        r207_204_label.setText("207 / 204");
        PbDataTab.add(r207_204_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(29, 59, 70, -1));

        r207_204_text.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        r207_204_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        r207_204_text.setText("0.0");
        PbDataTab.add(r207_204_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(105, 54, 225, -1));

        e207_204_text.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        e207_204_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        e207_204_text.setText("0.0");
        e207_204_text.setMinimumSize(new java.awt.Dimension(40, 19));
        e207_204_text.setPreferredSize(new java.awt.Dimension(36, 19));
        PbDataTab.add(e207_204_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(336, 57, 225, -1));

        e208_204_text.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        e208_204_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        e208_204_text.setText("0.0");
        e208_204_text.setMinimumSize(new java.awt.Dimension(40, 19));
        e208_204_text.setPreferredSize(new java.awt.Dimension(36, 19));
        PbDataTab.add(e208_204_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(336, 89, 225, -1));

        r208_204_text.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        r208_204_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        r208_204_text.setText("0.0");
        PbDataTab.add(r208_204_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(105, 86, 225, -1));

        r208_204_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        r208_204_label.setText("208 / 204");
        PbDataTab.add(r208_204_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(29, 91, 70, -1));

        r204_205_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        r204_205_label.setText("204 / 205");
        PbDataTab.add(r204_205_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(29, 179, 70, -1));

        r204_205_text.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        r204_205_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        r204_205_text.setText("0.0");
        PbDataTab.add(r204_205_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(105, 174, 225, -1));

        e204_205_text.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        e204_205_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        e204_205_text.setText("0.0");
        e204_205_text.setMinimumSize(new java.awt.Dimension(40, 19));
        e204_205_text.setPreferredSize(new java.awt.Dimension(36, 19));
        PbDataTab.add(e204_205_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(336, 177, 225, -1));

        r206_204_FracCorr_label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/earthtime/UPb_Redux/images/icon_checkmark.png"))); // NOI18N
        r206_204_FracCorr_label.setToolTipText("Ratio was fractionation-corrected datum-by-datum.  See Corrections tab for alphaU.");
        PbDataTab.add(r206_204_FracCorr_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 27, -1, -1));

        r206_207_FracCorr_label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/earthtime/UPb_Redux/images/icon_checkmark.png"))); // NOI18N
        r206_207_FracCorr_label.setToolTipText("Ratio was fractionation-corrected datum-by-datum.  See Corrections tab for alphaU.");
        PbDataTab.add(r206_207_FracCorr_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 118, -1, -1));

        r206_208_FracCorr_label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/earthtime/UPb_Redux/images/icon_checkmark.png"))); // NOI18N
        r206_208_FracCorr_label.setToolTipText("Ratio was fractionation-corrected datum-by-datum.  See Corrections tab for alphaU.");
        PbDataTab.add(r206_208_FracCorr_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 149, -1, -1));

        r206_205_FracCorr_label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/earthtime/UPb_Redux/images/icon_checkmark.png"))); // NOI18N
        r206_205_FracCorr_label.setToolTipText("Ratio was fractionation-corrected datum-by-datum.  See Corrections tab for alphaU.");
        PbDataTab.add(r206_205_FracCorr_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 200, -1, -1));

        r207_205_FracCorr_label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/earthtime/UPb_Redux/images/icon_checkmark.png"))); // NOI18N
        r207_205_FracCorr_label.setToolTipText("Ratio was fractionation-corrected datum-by-datum.  See Corrections tab for alphaU.");
        PbDataTab.add(r207_205_FracCorr_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 229, -1, -1));

        r208_205_FracCorr_label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/earthtime/UPb_Redux/images/icon_checkmark.png"))); // NOI18N
        r208_205_FracCorr_label.setToolTipText("Ratio was fractionation-corrected datum-by-datum.  See Corrections tab for alphaU.");
        PbDataTab.add(r208_205_FracCorr_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 261, -1, -1));

        r207_204_FracCorr_label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/earthtime/UPb_Redux/images/icon_checkmark.png"))); // NOI18N
        r207_204_FracCorr_label.setToolTipText("Ratio was fractionation-corrected datum-by-datum.  See Corrections tab for alphaU.");
        PbDataTab.add(r207_204_FracCorr_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 54, -1, -1));

        r208_204_FracCorr_label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/earthtime/UPb_Redux/images/icon_checkmark.png"))); // NOI18N
        r208_204_FracCorr_label.setToolTipText("Ratio was fractionation-corrected datum-by-datum.  See Corrections tab for alphaU.");
        PbDataTab.add(r208_204_FracCorr_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 86, -1, -1));

        r204_205_FracCorr_label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/earthtime/UPb_Redux/images/icon_checkmark.png"))); // NOI18N
        r204_205_FracCorr_label.setToolTipText("Ratio was fractionation-corrected datum-by-datum.  See Corrections tab for alphaU.");
        PbDataTab.add(r204_205_FracCorr_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 174, -1, -1));

        details_Pane.addTab("Pb Data", PbDataTab);

        UDataTab.setBackground(new java.awt.Color(250, 250, 220));
        UDataTab.setBorder(new javax.swing.border.MatteBorder(null));
        UDataTab.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        UDataTab.setName(""); // NOI18N
        UDataTab.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        rawRatio_label2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        rawRatio_label2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        rawRatio_label2.setText("value");
        UDataTab.add(rawRatio_label2, new org.netbeans.lib.awtextra.AbsoluteConstraints(174, 7, 70, -1));

        pctStdErr_label2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        pctStdErr_label2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        pctStdErr_label2.setText("1-sigma pct");
        UDataTab.add(pctStdErr_label2, new org.netbeans.lib.awtextra.AbsoluteConstraints(411, 7, 70, -1));

        r238_235_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        r238_235_label.setText("238 / 235");
        UDataTab.add(r238_235_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(29, 37, 70, -1));

        r238_235_text.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        r238_235_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        r238_235_text.setText("0.0");
        r238_235_text.setMinimumSize(new java.awt.Dimension(36, 19));
        r238_235_text.setPreferredSize(new java.awt.Dimension(36, 19));
        UDataTab.add(r238_235_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(105, 35, 225, -1));

        e238_235_text.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        e238_235_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        e238_235_text.setText("0.0");
        e238_235_text.setMinimumSize(new java.awt.Dimension(36, 19));
        e238_235_text.setPreferredSize(new java.awt.Dimension(36, 19));
        UDataTab.add(e238_235_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(342, 35, 220, -1));

        r233_235_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        r233_235_label.setText("233 / 235");
        UDataTab.add(r233_235_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(29, 71, 70, -1));

        r233_235_text.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        r233_235_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        r233_235_text.setText("0.0");
        r233_235_text.setMinimumSize(new java.awt.Dimension(36, 19));
        r233_235_text.setPreferredSize(new java.awt.Dimension(36, 19));
        UDataTab.add(r233_235_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(105, 69, 225, -1));

        e233_235_text.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        e233_235_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        e233_235_text.setText("0.0");
        e233_235_text.setMinimumSize(new java.awt.Dimension(36, 19));
        e233_235_text.setPreferredSize(new java.awt.Dimension(36, 19));
        UDataTab.add(e233_235_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(342, 69, 220, -1));

        notesU.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        notesU.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                notesUKeyReleased(evt);
            }
        });
        notesUPane.setViewportView(notesU);

        historyUPane.addTab("Notes", notesUPane);

        sourceUFile_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        sourceUFile_label.setText("U Source  File:");

        sourceUFileDisplay.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        sourceUFileDisplay.setText("No Source File for U Fraction Specified");

        sourceUFileOpen.setFont(new java.awt.Font("Lucida Grande", 1, 10)); // NOI18N
        sourceUFileOpen.setText("Open U Source File");
        sourceUFileOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sourceUFileOpenActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout sourceUPanelLayout = new org.jdesktop.layout.GroupLayout(sourceUPanel);
        sourceUPanel.setLayout(sourceUPanelLayout);
        sourceUPanelLayout.setHorizontalGroup(
            sourceUPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(sourceUPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(sourceUPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(sourceUFileOpen, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1021, Short.MAX_VALUE)
                    .add(sourceUFileDisplay, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1035, Short.MAX_VALUE)
                    .add(sourceUFile_label))
                .addContainerGap())
        );
        sourceUPanelLayout.setVerticalGroup(
            sourceUPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(sourceUPanelLayout.createSequentialGroup()
                .add(sourceUFile_label)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(sourceUFileDisplay, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 33, Short.MAX_VALUE)
                .add(sourceUFileOpen, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 29, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        historyUPane.addTab("Source", sourceUPanel);

        pedigreeULabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        pedigreeULabel.setText("U source file generated from:");

        pedigreeUDisplay.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        pedigreeUDisplay.setText("No Pedigree for U fraction specified");

        org.jdesktop.layout.GroupLayout pedigreeUPanelLayout = new org.jdesktop.layout.GroupLayout(pedigreeUPanel);
        pedigreeUPanel.setLayout(pedigreeUPanelLayout);
        pedigreeUPanelLayout.setHorizontalGroup(
            pedigreeUPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pedigreeUPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(pedigreeUPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(org.jdesktop.layout.GroupLayout.TRAILING, pedigreeUPanelLayout.createSequentialGroup()
                        .add(0, 0, Short.MAX_VALUE)
                        .add(pedigreeULabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 264, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(788, 788, 788))
                    .add(pedigreeUPanelLayout.createSequentialGroup()
                        .add(pedigreeUDisplay, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 376, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        pedigreeUPanelLayout.setVerticalGroup(
            pedigreeUPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(pedigreeUPanelLayout.createSequentialGroup()
                .add(pedigreeULabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pedigreeUDisplay, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 43, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(47, Short.MAX_VALUE))
        );

        historyUPane.addTab("Pedigree", pedigreeUPanel);

        UDataTab.add(historyUPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 429, 1068, -1));

        r238_235_FracCorr_label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/earthtime/UPb_Redux/images/icon_checkmark.png"))); // NOI18N
        r238_235_FracCorr_label.setToolTipText("Ratio was fractionation-corrected datum-by-datum.  See Corrections tab for alphaU.");
        UDataTab.add(r238_235_FracCorr_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 35, -1, -1));

        r233_235_FracCorr_label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/earthtime/UPb_Redux/images/icon_checkmark.png"))); // NOI18N
        r233_235_FracCorr_label.setToolTipText("Ratio was fractionation-corrected datum-by-datum.  See Corrections tab for alphaU.");
        UDataTab.add(r233_235_FracCorr_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 69, -1, -1));

        uFracCorrFootnote_label.setForeground(new java.awt.Color(0, 102, 0));
        uFracCorrFootnote_label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/earthtime/UPb_Redux/images/icon_checkmark.png"))); // NOI18N
        uFracCorrFootnote_label.setText("<html>Denotes that ratio was fractionation-corrected datum-by-datum.  See Corrections tab for alphaU.</html>");
        uFracCorrFootnote_label.setToolTipText("Ratio was fractionation-corrected datum-by-datum.  See Corrections tab for alphaU.");
        UDataTab.add(uFracCorrFootnote_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(29, 359, 339, -1));

        r233_236_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        r233_236_label.setText("233 / 236");
        UDataTab.add(r233_236_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(29, 145, 70, -1));

        e233_236_text.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        e233_236_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        e233_236_text.setText("0.0");
        e233_236_text.setMinimumSize(new java.awt.Dimension(36, 19));
        e233_236_text.setPreferredSize(new java.awt.Dimension(36, 19));
        UDataTab.add(e233_236_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(342, 143, 220, -1));

        r233_236_text.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        r233_236_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        r233_236_text.setText("0.0");
        r233_236_text.setMinimumSize(new java.awt.Dimension(36, 19));
        r233_236_text.setPreferredSize(new java.awt.Dimension(36, 19));
        UDataTab.add(r233_236_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(105, 143, 225, -1));

        r238_233_FracCorr_label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/earthtime/UPb_Redux/images/icon_checkmark.png"))); // NOI18N
        r238_233_FracCorr_label.setToolTipText("Ratio was fractionation-corrected datum-by-datum.  See Corrections tab for alphaU.");
        UDataTab.add(r238_233_FracCorr_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(7, 106, -1, -1));

        r238_233_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        r238_233_label.setText("238 / 233");
        UDataTab.add(r238_233_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(29, 108, 70, -1));

        r238_233_text.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        r238_233_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        r238_233_text.setText("0.0");
        r238_233_text.setMinimumSize(new java.awt.Dimension(36, 19));
        r238_233_text.setPreferredSize(new java.awt.Dimension(36, 19));
        UDataTab.add(r238_233_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(105, 106, 225, -1));

        e238_233_text.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        e238_233_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        e238_233_text.setText("0.0");
        e238_233_text.setMinimumSize(new java.awt.Dimension(36, 19));
        e238_233_text.setPreferredSize(new java.awt.Dimension(36, 19));
        UDataTab.add(e238_233_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(342, 106, 220, -1));

        autoUSettings_panel.setBackground(new java.awt.Color(255, 255, 204));
        autoUSettings_panel.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 255, 0)));

        inputPerCentUncertainty_text.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        inputPerCentUncertainty_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        inputPerCentUncertainty_text.setText("0.0");
        inputPerCentUncertainty_text.setMinimumSize(new java.awt.Dimension(36, 19));
        inputPerCentUncertainty_text.setPreferredSize(new java.awt.Dimension(36, 19));

        r233_236_label1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        r233_236_label1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        r233_236_label1.setText("measured ratio 1-sigma % uncertainty:");

        rawRatio_label3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        rawRatio_label3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        rawRatio_label3.setText("Settings for Auto-Uranium Measured Ratio Generation");

        r233_236_label2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        r233_236_label2.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        r233_236_label2.setText("estimated Date 206/238 Ma:");

        inputEst206_238Date_text.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        inputEst206_238Date_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        inputEst206_238Date_text.setText("0.0");
        inputEst206_238Date_text.setMinimumSize(new java.awt.Dimension(36, 19));
        inputEst206_238Date_text.setPreferredSize(new java.awt.Dimension(36, 19));

        restoreEstDate_button.setForeground(new java.awt.Color(255, 51, 0));
        restoreEstDate_button.setText("Restore estimated Date to 207/206 date");
        restoreEstDate_button.setPreferredSize(new java.awt.Dimension(140, 23));
        restoreEstDate_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                restoreEstDate_buttonActionPerformed(evt);
            }
        });

        r233_236_label3.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        r233_236_label3.setForeground(new java.awt.Color(255, 0, 0));
        r233_236_label3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        r233_236_label3.setText("Press Save button below to store changes; visit Kwiki tab");

        r233_236_label4.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        r233_236_label4.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        r233_236_label4.setText("alphaU %/amu:");

        inputAlphaU_text.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        inputAlphaU_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        inputAlphaU_text.setText("0.0");
        inputAlphaU_text.setMinimumSize(new java.awt.Dimension(36, 19));
        inputAlphaU_text.setPreferredSize(new java.awt.Dimension(36, 19));

        r233_236_label5.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        r233_236_label5.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        r233_236_label5.setText("1-sigma abs:");

        inputAlphaUOneSigmaAbs_text.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        inputAlphaUOneSigmaAbs_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        inputAlphaUOneSigmaAbs_text.setText("0.0");
        inputAlphaUOneSigmaAbs_text.setMinimumSize(new java.awt.Dimension(36, 19));
        inputAlphaUOneSigmaAbs_text.setPreferredSize(new java.awt.Dimension(36, 19));

        org.jdesktop.layout.GroupLayout autoUSettings_panelLayout = new org.jdesktop.layout.GroupLayout(autoUSettings_panel);
        autoUSettings_panel.setLayout(autoUSettings_panelLayout);
        autoUSettings_panelLayout.setHorizontalGroup(
            autoUSettings_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(autoUSettings_panelLayout.createSequentialGroup()
                .add(autoUSettings_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(autoUSettings_panelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(autoUSettings_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(r233_236_label3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 337, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(rawRatio_label3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 321, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(autoUSettings_panelLayout.createSequentialGroup()
                        .add(50, 50, 50)
                        .add(r233_236_label2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 167, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(inputEst206_238Date_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 84, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(autoUSettings_panelLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(autoUSettings_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(restoreEstDate_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 328, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(autoUSettings_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                .add(autoUSettings_panelLayout.createSequentialGroup()
                                    .add(r233_236_label1)
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                    .add(inputPerCentUncertainty_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 84, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                                .add(autoUSettings_panelLayout.createSequentialGroup()
                                    .add(r233_236_label4)
                                    .add(3, 3, 3)
                                    .add(inputAlphaU_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 59, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                    .add(r233_236_label5)
                                    .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                    .add(inputAlphaUOneSigmaAbs_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 84, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(18, Short.MAX_VALUE))
        );
        autoUSettings_panelLayout.setVerticalGroup(
            autoUSettings_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, autoUSettings_panelLayout.createSequentialGroup()
                .add(10, 10, 10)
                .add(rawRatio_label3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(autoUSettings_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(inputEst206_238Date_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(r233_236_label2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(restoreEstDate_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 16, Short.MAX_VALUE)
                .add(autoUSettings_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(r233_236_label4)
                    .add(inputAlphaU_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(r233_236_label5)
                    .add(inputAlphaUOneSigmaAbs_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(autoUSettings_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(r233_236_label1)
                    .add(inputPerCentUncertainty_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(29, 29, 29)
                .add(r233_236_label3)
                .addContainerGap())
        );

        UDataTab.add(autoUSettings_panel, new org.netbeans.lib.awtextra.AbsoluteConstraints(725, 172, -1, -1));

        fractionMetalOrOxide_bg.add(fractionIsMetal_rb);
        fractionIsMetal_rb.setSelected(true);
        fractionIsMetal_rb.setText("fraction is a metal");
        UDataTab.add(fractionIsMetal_rb, new org.netbeans.lib.awtextra.AbsoluteConstraints(105, 228, -1, -1));

        fractionMetalOrOxide_bg.add(fractionIsOxide_rb);
        fractionIsOxide_rb.setText("fraction is an oxide requires positive 18O/16O:");
        UDataTab.add(fractionIsOxide_rb, new org.netbeans.lib.awtextra.AbsoluteConstraints(105, 257, -1, -1));

        r18O_16OUsed_textOnUTab.setEditable(false);
        r18O_16OUsed_textOnUTab.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        r18O_16OUsed_textOnUTab.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        r18O_16OUsed_textOnUTab.setPreferredSize(new java.awt.Dimension(14, 19));
        r18O_16OUsed_textOnUTab.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                r18O_16OUsed_textOnUTabFocusLost(evt);
            }
        });
        r18O_16OUsed_textOnUTab.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                r18O_16OUsed_textOnUTabKeyReleased(evt);
            }
        });
        UDataTab.add(r18O_16OUsed_textOnUTab, new org.netbeans.lib.awtextra.AbsoluteConstraints(447, 261, 89, -1));

        r238_236_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        r238_236_label.setText("238 / 236");
        UDataTab.add(r238_236_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(29, 182, 70, -1));

        r238_236_text.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        r238_236_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        r238_236_text.setText("0.0");
        r238_236_text.setMinimumSize(new java.awt.Dimension(36, 19));
        r238_236_text.setPreferredSize(new java.awt.Dimension(36, 19));
        UDataTab.add(r238_236_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(105, 180, 225, -1));

        e238_236_text.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        e238_236_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        e238_236_text.setText("0.0");
        e238_236_text.setMinimumSize(new java.awt.Dimension(36, 19));
        e238_236_text.setPreferredSize(new java.awt.Dimension(36, 19));
        UDataTab.add(e238_236_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(342, 180, 220, -1));

        details_Pane.addTab("U Data", UDataTab);

        chooseTracer_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chooseTracer_label.setText("Choose Tracer:");

        tracerMass_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        tracerMass_label.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        tracerMass_label.setText("mass in g:");

        tracerMass_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        tracerMass_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        tracerMass_text.setText("0.0");

        tracerMassOneSigmaAbs_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        tracerMassOneSigmaAbs_label.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        tracerMassOneSigmaAbs_label.setText("1-sigma abs:");

        tracerMassOneSigmaAbs_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        tracerMassOneSigmaAbs_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        tracerMassOneSigmaAbs_text.setText("0.0");

        org.jdesktop.layout.GroupLayout tracerTabLayout = new org.jdesktop.layout.GroupLayout(tracerTab);
        tracerTab.setLayout(tracerTabLayout);
        tracerTabLayout.setHorizontalGroup(
            tracerTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tracerTabLayout.createSequentialGroup()
                .add(6, 6, 6)
                .add(chooseTracer_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 94, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(6, 6, 6)
                .add(tracerChooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 273, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(tracerMass_label)
                .add(6, 6, 6)
                .add(tracerMass_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 82, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(12, 12, 12)
                .add(tracerMassOneSigmaAbs_label)
                .add(6, 6, 6)
                .add(tracerMassOneSigmaAbs_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 83, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        tracerTabLayout.setVerticalGroup(
            tracerTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tracerTabLayout.createSequentialGroup()
                .add(6, 6, 6)
                .add(chooseTracer_label))
            .add(tracerChooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(tracerTabLayout.createSequentialGroup()
                .add(6, 6, 6)
                .add(tracerMass_label))
            .add(tracerTabLayout.createSequentialGroup()
                .add(1, 1, 1)
                .add(tracerMass_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(tracerTabLayout.createSequentialGroup()
                .add(6, 6, 6)
                .add(tracerMassOneSigmaAbs_label))
            .add(tracerTabLayout.createSequentialGroup()
                .add(1, 1, 1)
                .add(tracerMassOneSigmaAbs_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        details_Pane.addTab("Tracer", tracerTab);

        pbFractionationTitle_label.setFont(new java.awt.Font("Arial Black", 0, 12)); // NOI18N
        pbFractionationTitle_label.setForeground(new java.awt.Color(153, 0, 0));
        pbFractionationTitle_label.setText("Pb fractionation correction coefficient w/ technique used");

        meanAlphaPbValueUsed_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        meanAlphaPbValueUsed_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        meanAlphaPbValueUsed_label.setText("alphaPb in %/amu used");

        alphaPbFromModel_text.setEditable(false);
        alphaPbFromModel_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        alphaPbFromModel_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        uFractionationTitle_label.setFont(new java.awt.Font("Arial Black", 0, 12)); // NOI18N
        uFractionationTitle_label.setForeground(new java.awt.Color(153, 0, 0));
        uFractionationTitle_label.setText("U fractionation correction coefficient w/ technique used");

        meanAlphaUImported_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        meanAlphaUImported_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        meanAlphaUImported_label.setText("alphaU in %/amu used");

        uFracCorrTechnique1_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        uFracCorrTechnique1_label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        uFracCorrTechnique1_label.setText("1. Mean of point-wise alpha U corrections by Tripoli");
        uFracCorrTechnique1_label.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        meanAlphaUImported_text.setEditable(false);
        meanAlphaUImported_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        meanAlphaUImported_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        uFracCorrTechnique2_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        uFracCorrTechnique2_label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        uFracCorrTechnique2_label.setText("2. AlphaU calculated from ratio means");
        uFracCorrTechnique2_label.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        alphaUCalculatedFromMeans_text.setEditable(false);
        alphaUCalculatedFromMeans_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        alphaUCalculatedFromMeans_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        uFracCorrTechnique3_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        uFracCorrTechnique3_label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        uFracCorrTechnique3_label.setText("3. AlphaU mono-isotopic U Tracer (Aliquot alphaU model)");
        uFracCorrTechnique3_label.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        alphaUFromModel_text.setEditable(false);
        alphaUFromModel_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        alphaUFromModel_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        uFracCorrTechnique4_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        uFracCorrTechnique4_label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        uFracCorrTechnique4_label.setText("4. No correction");
        uFracCorrTechnique4_label.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        refreshFractionation_Button.setFont(new java.awt.Font("Tahoma", 3, 14)); // NOI18N
        refreshFractionation_Button.setForeground(new java.awt.Color(153, 0, 0));
        refreshFractionation_Button.setText("R E F R E S H");
        refreshFractionation_Button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                refreshFractionation_ButtonMouseClicked(evt);
            }
        });

        pbFracCorrTechnique3_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        pbFracCorrTechnique3_label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        pbFracCorrTechnique3_label.setText("3. AlphaPb mono-isotopic Pb Tracer (Aliquot alphaPb model)");
        pbFracCorrTechnique3_label.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        pbFracCorrTechnique1_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        pbFracCorrTechnique1_label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        pbFracCorrTechnique1_label.setText("1. Mean of point-wise alpha Pb corrections by Tripoli");
        pbFracCorrTechnique1_label.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        pbFracCorrTechnique2_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        pbFracCorrTechnique2_label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        pbFracCorrTechnique2_label.setText("2. AlphaPb calculated from ratio means");
        pbFracCorrTechnique2_label.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        assumedUBlankMass_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        assumedUBlankMass_label.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        assumedUBlankMass_label.setText("<html><p>U Blank mass in pg: </p></html>");

        assumedUBlankMass_text.setEditable(false);
        assumedUBlankMass_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        assumedUBlankMass_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        assumedUBlankMass_text.setText("0.0");

        uBlankMassOneSigma_text.setEditable(false);
        uBlankMassOneSigma_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        uBlankMassOneSigma_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        uBlankMassOneSigma_text.setText("0.0");

        blankR238_235_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        blankR238_235_label.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        blankR238_235_label.setText("238 / 235 blank:");

        r238_235Blank_text.setEditable(false);
        r238_235Blank_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        r238_235Blank_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        r238_235Blank_text.setText("0.0");
        r238_235Blank_text.setMinimumSize(new java.awt.Dimension(36, 19));
        r238_235Blank_text.setPreferredSize(new java.awt.Dimension(36, 19));

        r238_235BlankError_text.setEditable(false);
        r238_235BlankError_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        r238_235BlankError_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        r238_235BlankError_text.setText("0.0");
        r238_235BlankError_text.setMinimumSize(new java.awt.Dimension(36, 19));
        r238_235BlankError_text.setPreferredSize(new java.awt.Dimension(36, 19));

        blankRatio_label1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        blankRatio_label1.setText("value");

        blankOneSigmaPctStdErr_label1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        blankOneSigmaPctStdErr_label1.setText("1-sigma abs");

        oxideCorrectionTitle_label1.setFont(new java.awt.Font("Arial Black", 0, 12)); // NOI18N
        oxideCorrectionTitle_label1.setForeground(new java.awt.Color(153, 0, 0));
        oxideCorrectionTitle_label1.setText("U Sample Components");

        sampleR238_235_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        sampleR238_235_label.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        sampleR238_235_label.setText("238 / 235 sample:");

        r238_235Sample_text.setEditable(false);
        r238_235Sample_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        r238_235Sample_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        r238_235Sample_text.setText("0.0");
        r238_235Sample_text.setMinimumSize(new java.awt.Dimension(36, 19));
        r238_235Sample_text.setPreferredSize(new java.awt.Dimension(36, 19));

        r238_235SampleError_text.setEditable(false);
        r238_235SampleError_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        r238_235SampleError_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        r238_235SampleError_text.setText("0.0");
        r238_235SampleError_text.setMinimumSize(new java.awt.Dimension(36, 19));
        r238_235SampleError_text.setPreferredSize(new java.awt.Dimension(36, 19));

        meanAlphaPbImported_text.setEditable(false);
        meanAlphaPbImported_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        meanAlphaPbImported_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        alphaPbCalculatedFromMeans_text.setEditable(false);
        alphaPbCalculatedFromMeans_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        alphaPbCalculatedFromMeans_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        pbFracCorrTechnique4_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        pbFracCorrTechnique4_label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        pbFracCorrTechnique4_label.setText("4. No correction");
        pbFracCorrTechnique4_label.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        oxidePanel.setBackground(new java.awt.Color(232, 255, 255));

        oxideCorrectionTitle_label.setFont(new java.awt.Font("Arial Black", 0, 12)); // NOI18N
        oxideCorrectionTitle_label.setForeground(new java.awt.Color(153, 0, 0));
        oxideCorrectionTitle_label.setText("Oxide correction");

        blankOneSigmaPctStdErr_label2.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        blankOneSigmaPctStdErr_label2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        blankOneSigmaPctStdErr_label2.setText("<html>1-sigma abs<br> (initially default<br>from lab data)</html>");

        r18O_16OUsed_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        r18O_16OUsed_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        r18O_16OUsed_label.setText("<html>18O/16O applied<br> to imported data</html>");

        r18O_16OUsed_text.setEditable(false);
        r18O_16OUsed_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        r18O_16OUsed_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        r18O_16OUsedError_text.setEditable(false);
        r18O_16OUsedError_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        r18O_16OUsedError_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        r18O_16OUsedError_text.setText("0.0");
        r18O_16OUsedError_text.setMinimumSize(new java.awt.Dimension(36, 19));
        r18O_16OUsedError_text.setPreferredSize(new java.awt.Dimension(36, 19));

        r18O_16ORevised_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        r18O_16ORevised_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        r18O_16ORevised_label.setText("<html>18O/16O applied<br>by U-Pb_Redux <br>(same 1-sigma)</html>");

        r18O_16ORevised_text.setEditable(false);
        r18O_16ORevised_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        r18O_16ORevised_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        org.jdesktop.layout.GroupLayout oxidePanelLayout = new org.jdesktop.layout.GroupLayout(oxidePanel);
        oxidePanel.setLayout(oxidePanelLayout);
        oxidePanelLayout.setHorizontalGroup(
            oxidePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(oxidePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(oxideCorrectionTitle_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 164, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(oxidePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(r18O_16OUsed_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 116, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(r18O_16OUsed_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(oxidePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(blankOneSigmaPctStdErr_label2)
                    .add(r18O_16OUsedError_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 117, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(oxidePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(r18O_16ORevised_label)
                    .add(r18O_16ORevised_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 116, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        oxidePanelLayout.linkSize(new java.awt.Component[] {blankOneSigmaPctStdErr_label2, r18O_16OUsedError_text, r18O_16OUsed_label, r18O_16OUsed_text}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        oxidePanelLayout.setVerticalGroup(
            oxidePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(oxidePanelLayout.createSequentialGroup()
                .add(10, 10, 10)
                .add(oxidePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(oxidePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                        .add(oxidePanelLayout.createSequentialGroup()
                            .add(r18O_16ORevised_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(r18O_16ORevised_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(oxidePanelLayout.createSequentialGroup()
                            .add(oxidePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                                .add(r18O_16OUsed_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(blankOneSigmaPctStdErr_label2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                            .add(oxidePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                                .add(r18O_16OUsed_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                                .add(r18O_16OUsedError_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                    .add(oxideCorrectionTitle_label))
                .addContainerGap(10, Short.MAX_VALUE))
        );

        org.jdesktop.layout.GroupLayout correctionsTab_panelLayout = new org.jdesktop.layout.GroupLayout(correctionsTab_panel);
        correctionsTab_panel.setLayout(correctionsTab_panelLayout);
        correctionsTab_panelLayout.setHorizontalGroup(
            correctionsTab_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(correctionsTab_panelLayout.createSequentialGroup()
                .add(6, 6, 6)
                .add(pbFractionationTitle_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 376, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(45, 45, 45)
                .add(meanAlphaPbValueUsed_label))
            .add(correctionsTab_panelLayout.createSequentialGroup()
                .add(46, 46, 46)
                .add(pbFracCorrTechnique1_label)
                .add(80, 80, 80)
                .add(meanAlphaPbImported_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 150, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(correctionsTab_panelLayout.createSequentialGroup()
                .add(46, 46, 46)
                .add(pbFracCorrTechnique2_label)
                .add(154, 154, 154)
                .add(alphaPbCalculatedFromMeans_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 150, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(correctionsTab_panelLayout.createSequentialGroup()
                .add(46, 46, 46)
                .add(pbFracCorrTechnique3_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 348, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(25, 25, 25)
                .add(alphaPbFromModel_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 150, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(correctionsTab_panelLayout.createSequentialGroup()
                .add(46, 46, 46)
                .add(pbFracCorrTechnique4_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 213, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(correctionsTab_panelLayout.createSequentialGroup()
                .add(6, 6, 6)
                .add(uFractionationTitle_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 388, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(35, 35, 35)
                .add(meanAlphaUImported_label))
            .add(correctionsTab_panelLayout.createSequentialGroup()
                .add(46, 46, 46)
                .add(uFracCorrTechnique1_label)
                .add(86, 86, 86)
                .add(meanAlphaUImported_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 150, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(correctionsTab_panelLayout.createSequentialGroup()
                .add(46, 46, 46)
                .add(uFracCorrTechnique2_label)
                .add(160, 160, 160)
                .add(alphaUCalculatedFromMeans_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 150, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(correctionsTab_panelLayout.createSequentialGroup()
                .add(46, 46, 46)
                .add(uFracCorrTechnique3_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 355, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(alphaUFromModel_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 150, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(correctionsTab_panelLayout.createSequentialGroup()
                .add(46, 46, 46)
                .add(uFracCorrTechnique4_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 213, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(oxidePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(correctionsTab_panelLayout.createSequentialGroup()
                .add(6, 6, 6)
                .add(oxideCorrectionTitle_label1)
                .add(81, 81, 81)
                .add(blankRatio_label1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 41, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(89, 89, 89)
                .add(blankOneSigmaPctStdErr_label1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 81, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(correctionsTab_panelLayout.createSequentialGroup()
                .add(100, 100, 100)
                .add(sampleR238_235_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 103, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(6, 6, 6)
                .add(r238_235Sample_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 125, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(6, 6, 6)
                .add(r238_235SampleError_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 125, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(correctionsTab_panelLayout.createSequentialGroup()
                .add(24, 24, 24)
                .add(assumedUBlankMass_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 180, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(6, 6, 6)
                .add(assumedUBlankMass_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 125, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(6, 6, 6)
                .add(uBlankMassOneSigma_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 125, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(correctionsTab_panelLayout.createSequentialGroup()
                .add(101, 101, 101)
                .add(blankR238_235_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 103, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(6, 6, 6)
                .add(r238_235Blank_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 125, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(6, 6, 6)
                .add(r238_235BlankError_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 125, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(correctionsTab_panelLayout.createSequentialGroup()
                .add(181, 181, 181)
                .add(refreshFractionation_Button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 167, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        correctionsTab_panelLayout.setVerticalGroup(
            correctionsTab_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(correctionsTab_panelLayout.createSequentialGroup()
                .add(6, 6, 6)
                .add(correctionsTab_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pbFractionationTitle_label)
                    .add(correctionsTab_panelLayout.createSequentialGroup()
                        .add(3, 3, 3)
                        .add(meanAlphaPbValueUsed_label)))
                .add(6, 6, 6)
                .add(correctionsTab_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(correctionsTab_panelLayout.createSequentialGroup()
                        .add(5, 5, 5)
                        .add(pbFracCorrTechnique1_label))
                    .add(meanAlphaPbImported_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(6, 6, 6)
                .add(correctionsTab_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(correctionsTab_panelLayout.createSequentialGroup()
                        .add(5, 5, 5)
                        .add(pbFracCorrTechnique2_label))
                    .add(alphaPbCalculatedFromMeans_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(6, 6, 6)
                .add(correctionsTab_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(correctionsTab_panelLayout.createSequentialGroup()
                        .add(5, 5, 5)
                        .add(pbFracCorrTechnique3_label))
                    .add(alphaPbFromModel_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(6, 6, 6)
                .add(pbFracCorrTechnique4_label)
                .add(18, 18, 18)
                .add(correctionsTab_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(uFractionationTitle_label)
                    .add(correctionsTab_panelLayout.createSequentialGroup()
                        .add(3, 3, 3)
                        .add(meanAlphaUImported_label)))
                .add(6, 6, 6)
                .add(correctionsTab_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(correctionsTab_panelLayout.createSequentialGroup()
                        .add(5, 5, 5)
                        .add(uFracCorrTechnique1_label))
                    .add(meanAlphaUImported_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(6, 6, 6)
                .add(correctionsTab_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(correctionsTab_panelLayout.createSequentialGroup()
                        .add(5, 5, 5)
                        .add(uFracCorrTechnique2_label))
                    .add(alphaUCalculatedFromMeans_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(6, 6, 6)
                .add(correctionsTab_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(correctionsTab_panelLayout.createSequentialGroup()
                        .add(5, 5, 5)
                        .add(uFracCorrTechnique3_label))
                    .add(alphaUFromModel_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(6, 6, 6)
                .add(uFracCorrTechnique4_label)
                .add(18, 18, 18)
                .add(oxidePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(44, 44, 44)
                .add(correctionsTab_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(oxideCorrectionTitle_label1)
                    .add(correctionsTab_panelLayout.createSequentialGroup()
                        .add(3, 3, 3)
                        .add(correctionsTab_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(blankRatio_label1)
                            .add(blankOneSigmaPctStdErr_label1))))
                .add(12, 12, 12)
                .add(correctionsTab_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(correctionsTab_panelLayout.createSequentialGroup()
                        .add(2, 2, 2)
                        .add(sampleR238_235_label))
                    .add(r238_235Sample_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(r238_235SampleError_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(2, 2, 2)
                .add(correctionsTab_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(correctionsTab_panelLayout.createSequentialGroup()
                        .add(5, 5, 5)
                        .add(assumedUBlankMass_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(assumedUBlankMass_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(uBlankMassOneSigma_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(6, 6, 6)
                .add(correctionsTab_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(correctionsTab_panelLayout.createSequentialGroup()
                        .add(2, 2, 2)
                        .add(blankR238_235_label))
                    .add(r238_235Blank_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(r238_235BlankError_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(24, 24, 24)
                .add(refreshFractionation_Button))
        );

        details_Pane.addTab("Corrections", correctionsTab_panel);

        blankTab_panel.setBackground(new java.awt.Color(245, 245, 245));

        choosePbBlank_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        choosePbBlank_label.setText("  Choose Pb Blank:");

        org.jdesktop.layout.GroupLayout blankTab_panelLayout = new org.jdesktop.layout.GroupLayout(blankTab_panel);
        blankTab_panel.setLayout(blankTab_panelLayout);
        blankTab_panelLayout.setHorizontalGroup(
            blankTab_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(blankTab_panelLayout.createSequentialGroup()
                .add(6, 6, 6)
                .add(choosePbBlank_label)
                .add(14, 14, 14)
                .add(PbBlankChooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 341, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        blankTab_panelLayout.setVerticalGroup(
            blankTab_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(blankTab_panelLayout.createSequentialGroup()
                .add(12, 12, 12)
                .add(choosePbBlank_label))
            .add(blankTab_panelLayout.createSequentialGroup()
                .add(6, 6, 6)
                .add(PbBlankChooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        details_Pane.addTab("Pb Blank", blankTab_panel);

        initialPbModelsTab_panel.setBackground(new java.awt.Color(245, 245, 245));

        chooseInitialPbModel_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chooseInitialPbModel_label.setText("  Choose Initial Pb Model:");

        pbBlankMass_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        pbBlankMass_label.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        pbBlankMass_label.setText("<html><p>Pb Blank mass in pg: </p></html>");

        PbBlank_Mass_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        PbBlank_Mass_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        PbBlank_Mass_text.setText("0.0");

        pbBlankMassOneSigma_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        pbBlankMassOneSigma_label.setText("1-sigma abs:");

        pbBlankMassOneSigma_text.setEditable(false);
        pbBlankMassOneSigma_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        pbBlankMassOneSigma_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        pbBlankMassOneSigma_text.setText("0.0");

        staceyKramerCalculator_Panel.setBackground(new java.awt.Color(220, 255, 235));
        staceyKramerCalculator_Panel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        CalculatedModelInstructions_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        CalculatedModelInstructions_label.setText("Enter Stacey-Kramers age in Ma:");

        estimatedAgeInMA_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        estimatedAgeInMA_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        estimatedAgeInMA_text.setText("0");
        estimatedAgeInMA_text.setMinimumSize(new java.awt.Dimension(36, 19));
        estimatedAgeInMA_text.setPreferredSize(new java.awt.Dimension(36, 19));

        refreshCalculations_JButton.setFont(new java.awt.Font("Tahoma", 3, 14)); // NOI18N
        refreshCalculations_JButton.setForeground(new java.awt.Color(153, 0, 0));
        refreshCalculations_JButton.setText("Populate Model w/ SK values");
        refreshCalculations_JButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshCalculations_JButtonActionPerformed(evt);
            }
        });

        relativeUncertainty_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        relativeUncertainty_label.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        relativeUncertainty_label.setText("relative uncertainty for IC (1-sigma pct):");

        relativeUncertainty_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        relativeUncertainty_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        relativeUncertainty_text.setText("0");

        correlationCoefficients_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        correlationCoefficients_label.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        correlationCoefficients_label.setText("correlation coefficients for IC [-1.0,1.0]:");

        correlationCoefficients_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        correlationCoefficients_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        correlationCoefficients_text.setText("0");

        org.jdesktop.layout.GroupLayout staceyKramerCalculator_PanelLayout = new org.jdesktop.layout.GroupLayout(staceyKramerCalculator_Panel);
        staceyKramerCalculator_Panel.setLayout(staceyKramerCalculator_PanelLayout);
        staceyKramerCalculator_PanelLayout.setHorizontalGroup(
            staceyKramerCalculator_PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, staceyKramerCalculator_PanelLayout.createSequentialGroup()
                .addContainerGap(256, Short.MAX_VALUE)
                .add(staceyKramerCalculator_PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(CalculatedModelInstructions_label)
                    .add(relativeUncertainty_label)
                    .add(correlationCoefficients_label))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(staceyKramerCalculator_PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, relativeUncertainty_text)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, estimatedAgeInMA_text, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(correlationCoefficients_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 103, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(77, 77, 77)
                .add(refreshCalculations_JButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 298, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(125, 125, 125))
        );
        staceyKramerCalculator_PanelLayout.setVerticalGroup(
            staceyKramerCalculator_PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, staceyKramerCalculator_PanelLayout.createSequentialGroup()
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(staceyKramerCalculator_PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(CalculatedModelInstructions_label)
                    .add(estimatedAgeInMA_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(staceyKramerCalculator_PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(relativeUncertainty_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(relativeUncertainty_label)
                    .add(refreshCalculations_JButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(staceyKramerCalculator_PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(correlationCoefficients_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(correlationCoefficients_label))
                .addContainerGap())
        );

        org.jdesktop.layout.GroupLayout initialPbModelsTab_panelLayout = new org.jdesktop.layout.GroupLayout(initialPbModelsTab_panel);
        initialPbModelsTab_panel.setLayout(initialPbModelsTab_panelLayout);
        initialPbModelsTab_panelLayout.setHorizontalGroup(
            initialPbModelsTab_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(initialPbModelsTab_panelLayout.createSequentialGroup()
                .add(6, 6, 6)
                .add(chooseInitialPbModel_label)
                .add(6, 6, 6)
                .add(InitialPbModelChooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 293, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(4, 4, 4)
                .add(pbBlankMass_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, 0)
                .add(PbBlank_Mass_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 77, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(3, 3, 3)
                .add(pbBlankMassOneSigma_label)
                .add(8, 8, 8)
                .add(pbBlankMassOneSigma_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 80, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(staceyKramerCalculator_Panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        initialPbModelsTab_panelLayout.setVerticalGroup(
            initialPbModelsTab_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(initialPbModelsTab_panelLayout.createSequentialGroup()
                .add(5, 5, 5)
                .add(initialPbModelsTab_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(initialPbModelsTab_panelLayout.createSequentialGroup()
                        .add(7, 7, 7)
                        .add(chooseInitialPbModel_label))
                    .add(initialPbModelsTab_panelLayout.createSequentialGroup()
                        .add(1, 1, 1)
                        .add(InitialPbModelChooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(initialPbModelsTab_panelLayout.createSequentialGroup()
                        .add(5, 5, 5)
                        .add(pbBlankMass_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(PbBlank_Mass_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(initialPbModelsTab_panelLayout.createSequentialGroup()
                        .add(5, 5, 5)
                        .add(pbBlankMassOneSigma_label))
                    .add(pbBlankMassOneSigma_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(464, 464, 464)
                .add(staceyKramerCalculator_Panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        details_Pane.addTab("Initial Pb", initialPbModelsTab_panel);

        reductionsTab.setBackground(new java.awt.Color(255, 255, 255));

        refreshOutputs_Button.setFont(new java.awt.Font("Tahoma", 3, 14)); // NOI18N
        refreshOutputs_Button.setForeground(new java.awt.Color(153, 0, 0));
        refreshOutputs_Button.setText("R E - R E D U C E    F R A C T I O N");
        refreshOutputs_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshOutputs_ButtonActionPerformed(evt);
            }
        });

        printToFileDataValues_Button.setFont(new java.awt.Font("Tahoma", 3, 14)); // NOI18N
        printToFileDataValues_Button.setForeground(new java.awt.Color(153, 0, 0));
        printToFileDataValues_Button.setText("Print All Data Values and 1-sigmas to File");
        printToFileDataValues_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printToFileDataValues_ButtonActionPerformed(evt);
            }
        });

        printToFileMatrices_Button.setFont(new java.awt.Font("Tahoma", 3, 14)); // NOI18N
        printToFileMatrices_Button.setForeground(new java.awt.Color(153, 0, 0));
        printToFileMatrices_Button.setText("Print All Uncertainty Matrices to File");
        printToFileMatrices_Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printToFileMatrices_ButtonActionPerformed(evt);
            }
        });

        testMathMachineAlphaU.setFont(new java.awt.Font("Tahoma", 3, 14)); // NOI18N
        testMathMachineAlphaU.setForeground(new java.awt.Color(153, 0, 0));
        testMathMachineAlphaU.setText("Test MathMachine on AlphaU (make Firefox default)");
        testMathMachineAlphaU.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testMathMachineAlphaUActionPerformed(evt);
            }
        });

        jButton1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(153, 0, 0));
        jButton1.setText("Open Free Mind Map for Variables");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        testSingleDisplay.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        testSingleDisplay.setForeground(new java.awt.Color(153, 0, 0));
        testSingleDisplay.setText("Test Single Display ValueModel Viewer");
        testSingleDisplay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testSingleDisplayActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout reductionsTabLayout = new org.jdesktop.layout.GroupLayout(reductionsTab);
        reductionsTab.setLayout(reductionsTabLayout);
        reductionsTabLayout.setHorizontalGroup(
            reductionsTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(reductionsTabLayout.createSequentialGroup()
                .add(349, 349, 349)
                .add(jButton1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 304, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(reductionsTabLayout.createSequentialGroup()
                .add(349, 349, 349)
                .add(testSingleDisplay))
            .add(reductionsTabLayout.createSequentialGroup()
                .add(311, 311, 311)
                .add(refreshOutputs_Button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 406, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(reductionsTabLayout.createSequentialGroup()
                .add(311, 311, 311)
                .add(printToFileDataValues_Button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 406, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(reductionsTabLayout.createSequentialGroup()
                .add(311, 311, 311)
                .add(printToFileMatrices_Button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 406, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(reductionsTabLayout.createSequentialGroup()
                .add(311, 311, 311)
                .add(testMathMachineAlphaU))
        );
        reductionsTabLayout.setVerticalGroup(
            reductionsTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(reductionsTabLayout.createSequentialGroup()
                .add(56, 56, 56)
                .add(jButton1)
                .add(18, 18, 18)
                .add(testSingleDisplay)
                .add(191, 191, 191)
                .add(refreshOutputs_Button)
                .add(18, 18, 18)
                .add(printToFileDataValues_Button)
                .add(18, 18, 18)
                .add(printToFileMatrices_Button)
                .add(18, 18, 18)
                .add(testMathMachineAlphaU))
        );

        details_Pane.addTab("Reports", reductionsTab);

        chooseMineral_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chooseMineral_label.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        chooseMineral_label.setText("  Choose mineral for fraction:");

        chooseSettingType_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chooseSettingType_label.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        chooseSettingType_label.setText("  Choose setting type for fraction:");

        countOfGrains_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        countOfGrains_label.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        countOfGrains_label.setText("Count of grains:");

        physicallyAbraded_chkBox.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        physicallyAbraded_chkBox.setText("Physically abraded");
        physicallyAbraded_chkBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        leachedInHFAcid_chkBox.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        leachedInHFAcid_chkBox.setText("Leached in HF acid");
        leachedInHFAcid_chkBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        annealedChemicallyAbraded_chkBox.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        annealedChemicallyAbraded_chkBox.setText("Annealed & chemically abraded");
        annealedChemicallyAbraded_chkBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        chemicallyPurifiedUPb_chkBox.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chemicallyPurifiedUPb_chkBox.setText("Chemically purified UPb");
        chemicallyPurifiedUPb_chkBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        fractionComment_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        fractionComment_label.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        fractionComment_label.setText("Comment:");

        fractionComment_text.setColumns(20);
        fractionComment_text.setRows(5);
        jScrollPane2.setViewportView(fractionComment_text);

        countOfGrains_text.setEditable(false);
        countOfGrains_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        countOfGrains_text.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        countOfGrains_text.setText("0");

        publicationTimeStamp_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        publicationTimeStamp_label.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        publicationTimeStamp_label.setText("Analysis time stamp:");

        publicationTimeStamp_text.setEditable(false);
        publicationTimeStamp_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        publicationTimeStamp_text.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        publicationTimeStamp_text.setText("time-stamp");

        selectImageFile_button.setForeground(new java.awt.Color(255, 51, 0));
        selectImageFile_button.setText("Browse to select fraction image");
        selectImageFile_button.setPreferredSize(new java.awt.Dimension(140, 23));
        selectImageFile_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectImageFile_buttonActionPerformed(evt);
            }
        });

        imageThumbnail_Panel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 3));

        imageThumbnail_label.setBackground(new java.awt.Color(255, 255, 255));
        imageThumbnail_label.setOpaque(true);

        org.jdesktop.layout.GroupLayout imageThumbnail_PanelLayout = new org.jdesktop.layout.GroupLayout(imageThumbnail_Panel);
        imageThumbnail_Panel.setLayout(imageThumbnail_PanelLayout);
        imageThumbnail_PanelLayout.setHorizontalGroup(
            imageThumbnail_PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(imageThumbnail_label, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE)
        );
        imageThumbnail_PanelLayout.setVerticalGroup(
            imageThumbnail_PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(imageThumbnail_label, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE)
        );

        chooseSettingType_label1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chooseSettingType_label1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        chooseSettingType_label1.setText("Thumbnail 256 x 256 for publication to Geochron:");

        fractionID_label2.setBackground(new java.awt.Color(255, 255, 255));
        fractionID_label2.setFont(new java.awt.Font("Arial", 3, 14)); // NOI18N
        fractionID_label2.setForeground(new java.awt.Color(153, 0, 0));
        fractionID_label2.setText("Note: This tab is also available within the Aliquot Manager.");
        fractionID_label2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        fractionID_label2.setOpaque(true);

        useLabDefaults_button.setForeground(new java.awt.Color(255, 51, 0));
        useLabDefaults_button.setText("Use Lab Defaults for this fraction");
        useLabDefaults_button.setMargin(new java.awt.Insets(2, 1, 2, 1));
        useLabDefaults_button.setPreferredSize(new java.awt.Dimension(140, 23));
        useLabDefaults_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useLabDefaults_buttonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout archivingDetails_panelLayout = new org.jdesktop.layout.GroupLayout(archivingDetails_panel);
        archivingDetails_panel.setLayout(archivingDetails_panelLayout);
        archivingDetails_panelLayout.setHorizontalGroup(
            archivingDetails_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(archivingDetails_panelLayout.createSequentialGroup()
                .add(20, 20, 20)
                .add(fractionID_label2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 447, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(archivingDetails_panelLayout.createSequentialGroup()
                .add(10, 10, 10)
                .add(archivingDetails_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(archivingDetails_panelLayout.createSequentialGroup()
                        .add(140, 140, 140)
                        .add(useLabDefaults_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 320, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(archivingDetails_panelLayout.createSequentialGroup()
                        .add(130, 130, 130)
                        .add(archivingDetails_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(chooseMineral_label)
                            .add(archivingDetails_panelLayout.createSequentialGroup()
                                .add(160, 160, 160)
                                .add(mineralNameChooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
                    .add(archivingDetails_panelLayout.createSequentialGroup()
                        .add(chooseSettingType_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 289, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(1, 1, 1)
                        .add(settingTypeChooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 174, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(archivingDetails_panelLayout.createSequentialGroup()
                        .add(countOfGrains_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 289, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(11, 11, 11)
                        .add(countOfGrains_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 71, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(archivingDetails_panelLayout.createSequentialGroup()
                        .add(163, 163, 163)
                        .add(physicallyAbraded_chkBox)
                        .add(8, 8, 8)
                        .add(chooseSettingType_label1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 403, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(archivingDetails_panelLayout.createSequentialGroup()
                        .add(164, 164, 164)
                        .add(leachedInHFAcid_chkBox))
                    .add(archivingDetails_panelLayout.createSequentialGroup()
                        .add(91, 91, 91)
                        .add(annealedChemicallyAbraded_chkBox))
                    .add(archivingDetails_panelLayout.createSequentialGroup()
                        .add(137, 137, 137)
                        .add(chemicallyPurifiedUPb_chkBox)))
                .add(20, 20, 20)
                .add(archivingDetails_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(selectImageFile_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 262, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(imageThumbnail_Panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
            .add(archivingDetails_panelLayout.createSequentialGroup()
                .add(216, 216, 216)
                .add(fractionComment_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 93, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(8, 8, 8)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 689, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(archivingDetails_panelLayout.createSequentialGroup()
                .add(154, 154, 154)
                .add(publicationTimeStamp_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 155, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(8, 8, 8)
                .add(publicationTimeStamp_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 365, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        archivingDetails_panelLayout.setVerticalGroup(
            archivingDetails_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(archivingDetails_panelLayout.createSequentialGroup()
                .add(15, 15, 15)
                .add(fractionID_label2)
                .add(11, 11, 11)
                .add(archivingDetails_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(archivingDetails_panelLayout.createSequentialGroup()
                        .add(25, 25, 25)
                        .add(useLabDefaults_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(7, 7, 7)
                        .add(archivingDetails_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(archivingDetails_panelLayout.createSequentialGroup()
                                .add(5, 5, 5)
                                .add(chooseMineral_label))
                            .add(mineralNameChooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(6, 6, 6)
                        .add(archivingDetails_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(archivingDetails_panelLayout.createSequentialGroup()
                                .add(7, 7, 7)
                                .add(chooseSettingType_label))
                            .add(settingTypeChooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(5, 5, 5)
                        .add(archivingDetails_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(archivingDetails_panelLayout.createSequentialGroup()
                                .add(5, 5, 5)
                                .add(countOfGrains_label))
                            .add(countOfGrains_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(15, 15, 15)
                        .add(archivingDetails_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(physicallyAbraded_chkBox)
                            .add(archivingDetails_panelLayout.createSequentialGroup()
                                .add(5, 5, 5)
                                .add(chooseSettingType_label1)))
                        .add(7, 7, 7)
                        .add(leachedInHFAcid_chkBox)
                        .add(5, 5, 5)
                        .add(annealedChemicallyAbraded_chkBox)
                        .add(5, 5, 5)
                        .add(chemicallyPurifiedUPb_chkBox))
                    .add(archivingDetails_panelLayout.createSequentialGroup()
                        .add(selectImageFile_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(9, 9, 9)
                        .add(imageThumbnail_Panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(40, 40, 40)
                .add(archivingDetails_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(fractionComment_label)
                    .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 123, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(43, 43, 43)
                .add(archivingDetails_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(archivingDetails_panelLayout.createSequentialGroup()
                        .add(5, 5, 5)
                        .add(publicationTimeStamp_label))
                    .add(publicationTimeStamp_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
        );

        details_Pane.addTab("Archiving Details", archivingDetails_panel);

        kwikiTab.setBackground(new java.awt.Color(102, 102, 102));
        kwikiTab.setOpaque(true);

        org.jdesktop.layout.GroupLayout kwikiTabLayout = new org.jdesktop.layout.GroupLayout(kwikiTab);
        kwikiTab.setLayout(kwikiTabLayout);
        kwikiTabLayout.setHorizontalGroup(
            kwikiTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 1095, Short.MAX_VALUE)
        );
        kwikiTabLayout.setVerticalGroup(
            kwikiTabLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 603, Short.MAX_VALUE)
        );

        details_Pane.addTab("Kwiki", kwikiTab);

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

        fractionIsZircon_CheckBox.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        fractionIsZircon_CheckBox.setText("No InitialPb");

        fractionID_label1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        fractionID_label1.setText("Choose Fraction:");

        toggleStartStopLiveUpdate_button.setForeground(new java.awt.Color(255, 51, 51));
        toggleStartStopLiveUpdate_button.setText("Start Live Update");
        toggleStartStopLiveUpdate_button.setName(""); // NOI18N
        toggleStartStopLiveUpdate_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleStartStopLiveUpdate_buttonActionPerformed(evt);
            }
        });

        fractionID_label.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        fractionID_label.setText("Fraction ID:");

        org.jdesktop.layout.GroupLayout fractionPanelLayout = new org.jdesktop.layout.GroupLayout(fractionPanel);
        fractionPanel.setLayout(fractionPanelLayout);
        fractionPanelLayout.setHorizontalGroup(
            fractionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(fractionPanelLayout.createSequentialGroup()
                .add(6, 6, 6)
                .add(fractionID_label1)
                .add(6, 6, 6)
                .add(fraction_Chooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 176, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(toggleStartStopLiveUpdate_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 181, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(fractionID_label)
                .add(12, 12, 12)
                .add(fractionID_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 111, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(12, 12, 12)
                .add(fractionIsZircon_CheckBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 127, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(12, 12, 12)
                .add(fractionMassInGrams_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 133, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(6, 6, 6)
                .add(fractionMass_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 98, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        fractionPanelLayout.setVerticalGroup(
            fractionPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(fractionPanelLayout.createSequentialGroup()
                .add(6, 6, 6)
                .add(fractionID_label1))
            .add(fractionPanelLayout.createSequentialGroup()
                .add(1, 1, 1)
                .add(fraction_Chooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(toggleStartStopLiveUpdate_button)
            .add(fractionPanelLayout.createSequentialGroup()
                .add(6, 6, 6)
                .add(fractionID_label))
            .add(fractionID_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(fractionPanelLayout.createSequentialGroup()
                .add(1, 1, 1)
                .add(fractionIsZircon_CheckBox))
            .add(fractionPanelLayout.createSequentialGroup()
                .add(6, 6, 6)
                .add(fractionMassInGrams_label))
            .add(fractionMass_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
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

        export_button.setForeground(new java.awt.Color(255, 51, 0));
        export_button.setText("Export as XML");
        export_button.setToolTipText("This function exports the UPbFraction according to the schema for inputs to U-Pb_Redux.  Here, a user can save fractions for use by others with U-Pb_Redux.");
        export_button.setPreferredSize(new java.awt.Dimension(140, 23));
        export_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                export_buttonActionPerformed(evt);
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
                .add(6, 6, 6)
                .add(save_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 119, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(12, 12, 12)
                .add(close_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 115, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(127, 127, 127)
                .add(delete_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 115, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(6, 6, 6)
                .add(export_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        buttonsPanelLayout.setVerticalGroup(
            buttonsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(saveAndClose_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(save_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(close_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(delete_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(export_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        aliquotInfo_panel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        aliquotName_label.setText("jLabel1");

        org.jdesktop.layout.GroupLayout aliquotInfo_panelLayout = new org.jdesktop.layout.GroupLayout(aliquotInfo_panel);
        aliquotInfo_panel.setLayout(aliquotInfo_panelLayout);
        aliquotInfo_panelLayout.setHorizontalGroup(
            aliquotInfo_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(aliquotName_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 511, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        aliquotInfo_panelLayout.setVerticalGroup(
            aliquotInfo_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(aliquotName_label)
        );

        org.jdesktop.layout.GroupLayout jLayeredPane1Layout = new org.jdesktop.layout.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(aliquotInfo_panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(fractionPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(details_Pane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 1120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(buttonsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jLayeredPane1Layout.createSequentialGroup()
                .add(aliquotInfo_panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(jLayeredPane1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(fractionPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(jLayeredPane1Layout.createSequentialGroup()
                        .add(30, 30, 30)
                        .add(details_Pane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 650, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(buttonsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 30, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        jLayeredPane1.setLayer(details_Pane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(fractionPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(buttonsPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(aliquotInfo_panel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jLayeredPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jLayeredPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void refreshOutputs_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshOutputs_ButtonActionPerformed
        try {
            save(myFraction);
        } catch (ETException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
        ((UPbFraction) myFraction).reduceData(isCompiled());
}//GEN-LAST:event_refreshOutputs_ButtonActionPerformed

    private void refreshFractionation_ButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_refreshFractionation_ButtonMouseClicked
        try {
            save(myFraction);
        } catch (ETException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
        ((UPbFraction) myFraction).reduceData(isCompiled());
        PopulateCorrectionsTab(myFraction);
}//GEN-LAST:event_refreshFractionation_ButtonMouseClicked

    private void sourcePbFileOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sourcePbFileOpenActionPerformed
        try {
            Runtime.getRuntime().exec(
                    new String[]{"open", "-e", ((UPbFraction) myFraction).getSourceFilePb()});
        } catch (IOException ex) {
//            ex.printStackTrace();
        }
    }//GEN-LAST:event_sourcePbFileOpenActionPerformed

    private void sourceUFileOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sourceUFileOpenActionPerformed
        try {
            Runtime.getRuntime().exec(
                    new String[]{"open", "-e", ((UPbFraction) myFraction).getSourceFileU()});
        } catch (IOException ex) {
//            ex.printStackTrace();
        }
    }//GEN-LAST:event_sourceUFileOpenActionPerformed

    private void export_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_export_buttonActionPerformed
        ExportUPbFractionPerInputSchema(myFraction);
    }//GEN-LAST:event_export_buttonActionPerformed

    private void close_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_close_buttonActionPerformed
        close();
    }//GEN-LAST:event_close_buttonActionPerformed

    private void saveAndClose_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAndClose_buttonActionPerformed

        restoreFractionFromKwikiChanges(myFraction);

        try {
            save(myFraction); // includes a restore as well
        } catch (ETException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }

        if (((UPbFraction) myFraction).isInAutoUraniumMode()) {
            ((UPbFraction) myFraction).zeroUraniumRatios();
        }

        close();

}//GEN-LAST:event_saveAndClose_buttonActionPerformed

    /**
     *
     */
    @Override
    public void close() {

        super.close();
        restoreAllFractions(myFraction);
    }

    /**
     *
     * @param myFraction
     * @return
     */
    public boolean restoreAllFractions(ETFractionInterface myFraction) {
        boolean savedAutoUraniumStateOfMyFraction = false;

        restoreFractionFromKwikiChanges(myFraction);

        if (((UPbFraction) myFraction).isInAutoUraniumMode()) {
            ((UPbFraction) myFraction).zeroUraniumRatios();
            savedAutoUraniumStateOfMyFraction = true;
        }

        FireDataReducer(myFraction, true);

        // for every fraction
        for (ETFractionInterface f : ((UPbReduxAliquot) aliquot).getAliquotFractions()) {
            if (((UPbFraction) f).isInAutoUraniumMode()) {
                ((UPbFraction) f).zeroUraniumRatios();

                FireDataReducer(f, true);
            }
        }

        return savedAutoUraniumStateOfMyFraction;
    }

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
                myFraction.setChanged(true);
                myFraction.setDeleted(true);
            case JOptionPane.CANCEL_OPTION:
                close();
            case JOptionPane.NO_OPTION:
                break;
            case JOptionPane.CLOSED_OPTION:
                break;
        }

    }//GEN-LAST:event_delete_buttonActionPerformed

private void ChangedTabs(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ChangedTabs
}//GEN-LAST:event_ChangedTabs

private void UPbFractionEditorClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_UPbFractionEditorClosing
    close();
    // resetKwikiTabValues();
}//GEN-LAST:event_UPbFractionEditorClosing

private void printToFileDataValues_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printToFileDataValues_ButtonActionPerformed
    ((UPbFraction) myFraction).toFileAllDataValues();
}//GEN-LAST:event_printToFileDataValues_ButtonActionPerformed

private void printToFileMatrices_ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printToFileMatrices_ButtonActionPerformed
    ((UPbFraction) myFraction).getReductionHandler().toFileAllMatrices();

}//GEN-LAST:event_printToFileMatrices_ButtonActionPerformed

private void save_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_save_buttonActionPerformed
    try {
        save(myFraction);
        InitializeFractionData(myFraction);

        //  InitializeKwikiTab( myFraction );
        reInitializeKwikiTab(myFraction);

    } catch (ETException ex) {
        new ETWarningDialog(ex).setVisible(true);
    }
}//GEN-LAST:event_save_buttonActionPerformed

private void toggleStartStopLiveUpdate_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleStartStopLiveUpdate_buttonActionPerformed

    String buttonText = ((ETReduxFrame) parent).toggleLiveUpdate();
    toggleStartStopLiveUpdate_button.setText(buttonText);


}//GEN-LAST:event_toggleStartStopLiveUpdate_buttonActionPerformed

    private void selectImageFile(ETFractionInterface myFraction) throws BadLabDataException {
        String dialogTitle = "Select a fraction image: *.gif, *.jpeg";
        final String fileExtension = ".gif | .jpeg | .jpg";
        FileFilter nonMacFileFilter = new FileFilter() {

            @Override
            public boolean accept(File file) {
                String filename = file.getName();
                return ((filename.endsWith(".gif"))
                        || (filename.endsWith(".GIF"))
                        || (filename.endsWith(".jpeg"))
                        || (filename.endsWith(".JPEG"))
                        || (filename.endsWith(".jpg"))
                        || (filename.endsWith(".JPG"))
                        || (file.isDirectory()));
            }

            @Override
            public String getDescription() {
                return ".gif | .jpeg | .jpg";
            }
        };

        File returnFile
                = FileHelper.AllPlatformGetFile(dialogTitle, null, fileExtension, nonMacFileFilter, false, new JFrame())[0];

        if (returnFile != null) {
            try {
                myFraction.setImageURL(returnFile.getCanonicalPath());
            } catch (IOException iOException) {
            }
        } else {
            myFraction.setImageURL("");
        }

        displayThumbnailForFraction(myFraction);

    }

    /**
     *
     * @param myFraction
     */
    public void displayThumbnailForFraction(ETFractionInterface myFraction) {

        imageThumbnail_label.setText("No image selected");
        imageThumbnail_label.setIcon(null);

        // local files only for now
        File fractionImageFile = new File(myFraction.getImageURL());

        if (fractionImageFile.exists()) {
            BufferedImage scaledImage = Thumbnail.createThumbnailFromImage(myFraction.getImageURL(), 256);

            imageThumbnail_label.setText("");
            imageThumbnail_label.setIcon(new ImageIcon(scaledImage));
        }
    }

private void restoreEstDate_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_restoreEstDate_buttonActionPerformed
    inputEst206_238Date_text.setText(myFraction.getRadiogenicIsotopeDateByName(RadDates.age207_206r).//
            getValueInUnits("Ma").setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE, RoundingMode.HALF_UP).toPlainString());
}//GEN-LAST:event_restoreEstDate_buttonActionPerformed

private void selectImageFile_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectImageFile_buttonActionPerformed
    try {
        selectImageFile(myFraction);
    } catch (BadLabDataException badLabDataException) {
    }
}//GEN-LAST:event_selectImageFile_buttonActionPerformed

private void notesUKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_notesUKeyReleased
    notesPb.setText(notesU.getText());
}//GEN-LAST:event_notesUKeyReleased

private void notesPbKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_notesPbKeyReleased
    notesU.setText(notesPb.getText());
}//GEN-LAST:event_notesPbKeyReleased

private void r18O_16OUsed_textOnUTabKeyReleased (java.awt.event.KeyEvent evt) {//GEN-FIRST:event_r18O_16OUsed_textOnUTabKeyReleased
//
//    r18O_16OUsed_text.setText( r18O_16OUsed_textOnUTab.getText() );
//    r18O_16ORevised_text.setText( r18O_16OUsed_textOnUTab.getText() );
//
//    // in manual entry mode, the uncertainty is initialized to the lab default if 18O/16O is > 0
//    if ( r18O_16OUsed_textOnUTab.getText().isEmpty() || Double.valueOf( r18O_16OUsed_textOnUTab.getText() ) <= 0.0 ) {
//        // force back to being a metal
//        fractionIsMetal_rb.doClick();//.setSelected( true );
//    } else {
//        r18O_16OUsedError_text.setText( ((UPbFraction) myFraction).getMyLabData().getDefaultR18O_16O().getOneSigmaAbs()//
//                .setScale( ReduxConstants.DEFAULT_CONSTANTS_SCALE, RoundingMode.HALF_UP ).toPlainString() );
//    }
}//GEN-LAST:event_r18O_16OUsed_textOnUTabKeyReleased

private void testMathMachineAlphaUActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testMathMachineAlphaUActionPerformed
//    MathMachineII.outputExpressionFilesXML( ((UPbFraction) myFraction).getAnalysisMeasure( AnalysisMeasures.alphaU.getName() ), "MathMachine", true );
//    BrowserControl.displayURL( "./MathMachine/alphaU.html" );//xml" );
}//GEN-LAST:event_testMathMachineAlphaUActionPerformed

private void r18O_16OUsed_textOnUTabFocusLost (java.awt.event.FocusEvent evt) {//GEN-FIRST:event_r18O_16OUsed_textOnUTabFocusLost
    r18O_16OUsed_text.setText(r18O_16OUsed_textOnUTab.getText());
    r18O_16ORevised_text.setText(r18O_16OUsed_textOnUTab.getText());

    // in manual entry mode, the uncertainty is initialized to the lab default if 18O/16O is > 0
    if (r18O_16OUsed_textOnUTab.getText().isEmpty() || Double.valueOf(r18O_16OUsed_textOnUTab.getText()) <= 0.0) {
        // force back to being a metal
        fractionIsMetal_rb.doClick();//.setSelected( true );
    } else {
        r18O_16OUsedError_text.setText(((UPbFraction) myFraction).getMyLabData().getDefaultR18O_16O().getOneSigmaAbs()//
                .setScale(ReduxConstants.DEFAULT_CONSTANTS_SCALE, RoundingMode.HALF_UP).toPlainString());
    }
}//GEN-LAST:event_r18O_16OUsed_textOnUTabFocusLost

    private void showLabDefaultFractionArchivingData(ETFractionInterface myFraction) {

        ReduxLabData myLabData = ((UPbFraction) myFraction).getMyLabData();

        mineralNameChooser.setSelectedItem(myLabData.getDefaultMineralName());
        settingTypeChooser.setSelectedItem(myLabData.getDefaultSettingType());
        countOfGrains_text.setText(String.valueOf(myLabData.getDefaultNumberOfGrains()));
        physicallyAbraded_chkBox.setSelected(myLabData.isDefaultPhysicallyAbraded());
        leachedInHFAcid_chkBox.setSelected(myLabData.isDefaultLeachedInHFAcid());
        annealedChemicallyAbraded_chkBox.setSelected(myLabData.isDefaultAnnealedAndChemicallyAbraded());
        chemicallyPurifiedUPb_chkBox.setSelected(myLabData.isDefaultChemicallyPurifiedUPb());

    }

    private void refreshCalculations_JButtonActionPerformed ( java.awt.event.ActionEvent evt ) {//GEN-FIRST:event_refreshCalculations_JButtonActionPerformed
//////        try {
//////            populateInitialPbModelFields( currentEditableInitialPbModel, false );
//////        } catch (BadLabDataException ex) {
//////        }
    }//GEN-LAST:event_refreshCalculations_JButtonActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

    }//GEN-LAST:event_jButton1ActionPerformed

    private void testSingleDisplayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testSingleDisplayActionPerformed
        // TODO add your handling code here:
//        ValueModelWebViewer vmv = new ValueModelWebViewer(((UPbFraction) myFraction).getAnalysisMeasure(AnalysisMeasures.alphaU.getName()), this);
    }//GEN-LAST:event_testSingleDisplayActionPerformed

    private void useLabDefaults_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useLabDefaults_buttonActionPerformed
        showLabDefaultFractionArchivingData((FractionI) fraction_Chooser.getSelectedItem());
    }//GEN-LAST:event_useLabDefaults_buttonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel CalculatedModelInstructions_label;
    private javax.swing.JComboBox<String> InitialPbModelChooser;
    private javax.swing.JComboBox<String> PbBlankChooser;
    private javax.swing.JTextField PbBlank_Mass_text;
    private javax.swing.JPanel PbDataTab;
    private javax.swing.JPanel UDataTab;
    private javax.swing.JPanel aliquotInfo_panel;
    private javax.swing.JLabel aliquotName_label;
    private javax.swing.JTextField alphaPbCalculatedFromMeans_text;
    private javax.swing.JTextField alphaPbFromModel_text;
    private javax.swing.JTextField alphaUCalculatedFromMeans_text;
    private javax.swing.JTextField alphaUFromModel_text;
    private javax.swing.JCheckBox annealedChemicallyAbraded_chkBox;
    private javax.swing.JPanel archivingDetails_panel;
    private javax.swing.JLabel assumedUBlankMass_label;
    private javax.swing.JTextField assumedUBlankMass_text;
    private javax.swing.JPanel autoUSettings_panel;
    private javax.swing.JLabel blankOneSigmaPctStdErr_label1;
    private javax.swing.JLabel blankOneSigmaPctStdErr_label2;
    private javax.swing.JLabel blankR238_235_label;
    private javax.swing.JLabel blankRatio_label1;
    private javax.swing.JPanel blankTab_panel;
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JCheckBox chemicallyPurifiedUPb_chkBox;
    private javax.swing.JLabel chooseInitialPbModel_label;
    private javax.swing.JLabel chooseMineral_label;
    private javax.swing.JLabel choosePbBlank_label;
    private javax.swing.JLabel chooseSettingType_label;
    private javax.swing.JLabel chooseSettingType_label1;
    private javax.swing.JLabel chooseTracer_label;
    private javax.swing.JButton close_button;
    private javax.swing.JPanel correctionsTab_panel;
    private javax.swing.JLabel correlationCoefficients_label;
    private javax.swing.JTextField correlationCoefficients_text;
    private javax.swing.JLabel countOfGrains_label;
    private javax.swing.JTextField countOfGrains_text;
    private javax.swing.JButton delete_button;
    private javax.swing.JTabbedPane details_Pane;
    private javax.swing.JTextField e202_205_text;
    private javax.swing.JTextField e204_205_text;
    private javax.swing.JTextField e206_204_text;
    private javax.swing.JTextField e206_205_text;
    private javax.swing.JTextField e206_207_text;
    private javax.swing.JTextField e206_208_text;
    private javax.swing.JTextField e207_204_text;
    private javax.swing.JTextField e207_205_text;
    private javax.swing.JTextField e208_204_text;
    private javax.swing.JTextField e208_205_text;
    private javax.swing.JTextField e233_235_text;
    private javax.swing.JTextField e233_236_text;
    private javax.swing.JTextField e238_233_text;
    private javax.swing.JTextField e238_235_text;
    private javax.swing.JTextField e238_236_text;
    private javax.swing.JTextField estimatedAgeInMA_text;
    private javax.swing.JButton export_button;
    private javax.swing.JLabel fractionComment_label;
    private javax.swing.JTextArea fractionComment_text;
    private javax.swing.JLabel fractionID_label;
    private javax.swing.JLabel fractionID_label1;
    private javax.swing.JLabel fractionID_label2;
    private javax.swing.JTextField fractionID_text;
    private javax.swing.JRadioButton fractionIsMetal_rb;
    private javax.swing.JRadioButton fractionIsOxide_rb;
    private javax.swing.JCheckBox fractionIsZircon_CheckBox;
    private javax.swing.JLabel fractionMassInGrams_label;
    private javax.swing.JTextField fractionMass_text;
    private javax.swing.ButtonGroup fractionMetalOrOxide_bg;
    private javax.swing.JPanel fractionPanel;
    private javax.swing.JComboBox<ETFractionInterface> fraction_Chooser;
    private javax.swing.JTabbedPane historyPbPane;
    private javax.swing.JTabbedPane historyUPane;
    private javax.swing.JPanel imageThumbnail_Panel;
    private javax.swing.JLabel imageThumbnail_label;
    private javax.swing.JPanel initialPbModelsTab_panel;
    private javax.swing.JTextField inputAlphaUOneSigmaAbs_text;
    private javax.swing.JTextField inputAlphaU_text;
    private javax.swing.JTextField inputEst206_238Date_text;
    private javax.swing.JTextField inputPerCentUncertainty_text;
    private javax.swing.JButton jButton1;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLayeredPane kwikiTab;
    private javax.swing.JCheckBox leachedInHFAcid_chkBox;
    private javax.swing.JTextField meanAlphaPbImported_text;
    private javax.swing.JLabel meanAlphaPbValueUsed_label;
    private javax.swing.JLabel meanAlphaUImported_label;
    private javax.swing.JTextField meanAlphaUImported_text;
    private javax.swing.JComboBox<String> mineralNameChooser;
    private javax.swing.JEditorPane notesPb;
    private javax.swing.JScrollPane notesPbPane;
    private javax.swing.JEditorPane notesU;
    private javax.swing.JScrollPane notesUPane;
    private javax.swing.JLabel oxideCorrectionTitle_label;
    private javax.swing.JLabel oxideCorrectionTitle_label1;
    private javax.swing.JPanel oxidePanel;
    private javax.swing.JLabel pbBlankMassOneSigma_label;
    private javax.swing.JTextField pbBlankMassOneSigma_text;
    private javax.swing.JLabel pbBlankMass_label;
    private javax.swing.JLabel pbFracCorrFootnote_label;
    private javax.swing.JLabel pbFracCorrTechnique1_label;
    private javax.swing.JLabel pbFracCorrTechnique2_label;
    private javax.swing.JLabel pbFracCorrTechnique3_label;
    private javax.swing.JLabel pbFracCorrTechnique4_label;
    private javax.swing.JLabel pbFractionationTitle_label;
    private javax.swing.JLabel pctStdErr_label;
    private javax.swing.JLabel pctStdErr_label2;
    private javax.swing.JLabel pedigreePbDisplay;
    private javax.swing.JLabel pedigreePbLabel;
    private javax.swing.JPanel pedigreePbPanel;
    private javax.swing.JLabel pedigreeUDisplay;
    private javax.swing.JLabel pedigreeULabel;
    private javax.swing.JPanel pedigreeUPanel;
    private javax.swing.JCheckBox physicallyAbraded_chkBox;
    private javax.swing.JButton printToFileDataValues_Button;
    private javax.swing.JButton printToFileMatrices_Button;
    private javax.swing.JLabel publicationTimeStamp_label;
    private javax.swing.JTextField publicationTimeStamp_text;
    private javax.swing.JLabel r18O_16ORevised_label;
    private javax.swing.JTextField r18O_16ORevised_text;
    private javax.swing.JTextField r18O_16OUsedError_text;
    private javax.swing.JLabel r18O_16OUsed_label;
    private javax.swing.JTextField r18O_16OUsed_text;
    private javax.swing.JTextField r18O_16OUsed_textOnUTab;
    private javax.swing.JLabel r202_205_label;
    private javax.swing.JTextField r202_205_text;
    private javax.swing.JLabel r204_205_FracCorr_label;
    private javax.swing.JLabel r204_205_label;
    private javax.swing.JTextField r204_205_text;
    private javax.swing.JLabel r206_204_FracCorr_label;
    private javax.swing.JLabel r206_204_label;
    private javax.swing.JTextField r206_204_text;
    private javax.swing.JLabel r206_205_FracCorr_label;
    private javax.swing.JLabel r206_205_label;
    private javax.swing.JTextField r206_205_text;
    private javax.swing.JLabel r206_207_FracCorr_label;
    private javax.swing.JLabel r206_207_label;
    private javax.swing.JTextField r206_207_text;
    private javax.swing.JLabel r206_208_FracCorr_label;
    private javax.swing.JLabel r206_208_label;
    private javax.swing.JTextField r206_208_text;
    private javax.swing.JLabel r207_204_FracCorr_label;
    private javax.swing.JLabel r207_204_label;
    private javax.swing.JTextField r207_204_text;
    private javax.swing.JLabel r207_205_FracCorr_label;
    private javax.swing.JLabel r207_205_label;
    private javax.swing.JTextField r207_205_text;
    private javax.swing.JLabel r208_204_FracCorr_label;
    private javax.swing.JLabel r208_204_label;
    private javax.swing.JTextField r208_204_text;
    private javax.swing.JLabel r208_205_FracCorr_label;
    private javax.swing.JLabel r208_205_label;
    private javax.swing.JTextField r208_205_text;
    private javax.swing.JLabel r233_235_FracCorr_label;
    private javax.swing.JLabel r233_235_label;
    private javax.swing.JTextField r233_235_text;
    private javax.swing.JLabel r233_236_label;
    private javax.swing.JLabel r233_236_label1;
    private javax.swing.JLabel r233_236_label2;
    private javax.swing.JLabel r233_236_label3;
    private javax.swing.JLabel r233_236_label4;
    private javax.swing.JLabel r233_236_label5;
    private javax.swing.JTextField r233_236_text;
    private javax.swing.JLabel r238_233_FracCorr_label;
    private javax.swing.JLabel r238_233_label;
    private javax.swing.JTextField r238_233_text;
    private javax.swing.JTextField r238_235BlankError_text;
    private javax.swing.JTextField r238_235Blank_text;
    private javax.swing.JTextField r238_235SampleError_text;
    private javax.swing.JTextField r238_235Sample_text;
    private javax.swing.JLabel r238_235_FracCorr_label;
    private javax.swing.JLabel r238_235_label;
    private javax.swing.JTextField r238_235_text;
    private javax.swing.JLabel r238_236_label;
    private javax.swing.JTextField r238_236_text;
    private javax.swing.JLabel rawRatio_label;
    private javax.swing.JLabel rawRatio_label2;
    private javax.swing.JLabel rawRatio_label3;
    private javax.swing.JPanel reductionsTab;
    private javax.swing.JButton refreshCalculations_JButton;
    private javax.swing.JButton refreshFractionation_Button;
    private javax.swing.JButton refreshOutputs_Button;
    private javax.swing.JLabel relativeUncertainty_label;
    private javax.swing.JTextField relativeUncertainty_text;
    private javax.swing.JButton restoreEstDate_button;
    private javax.swing.JLabel sampleR238_235_label;
    private javax.swing.JButton saveAndClose_button;
    private javax.swing.JButton save_button;
    private javax.swing.JButton selectImageFile_button;
    private javax.swing.JComboBox<String> settingTypeChooser;
    private javax.swing.JLabel sourcePbFileDisplay;
    private javax.swing.JButton sourcePbFileOpen;
    private javax.swing.JLabel sourcePbFile_label;
    private javax.swing.JPanel sourcePbPanel;
    private javax.swing.JLabel sourceUFileDisplay;
    private javax.swing.JButton sourceUFileOpen;
    private javax.swing.JLabel sourceUFile_label;
    private javax.swing.JPanel sourceUPanel;
    private javax.swing.JPanel staceyKramerCalculator_Panel;
    private javax.swing.JButton testMathMachineAlphaU;
    private javax.swing.JButton testSingleDisplay;
    private javax.swing.JButton toggleStartStopLiveUpdate_button;
    private javax.swing.JComboBox<String> tracerChooser;
    private javax.swing.JLabel tracerMassOneSigmaAbs_label;
    private javax.swing.JTextField tracerMassOneSigmaAbs_text;
    private javax.swing.JLabel tracerMass_label;
    private javax.swing.JTextField tracerMass_text;
    private javax.swing.JPanel tracerTab;
    private javax.swing.JTextField uBlankMassOneSigma_text;
    private javax.swing.JLabel uFracCorrFootnote_label;
    private javax.swing.JLabel uFracCorrTechnique1_label;
    private javax.swing.JLabel uFracCorrTechnique2_label;
    private javax.swing.JLabel uFracCorrTechnique3_label;
    private javax.swing.JLabel uFracCorrTechnique4_label;
    private javax.swing.JLabel uFractionationTitle_label;
    protected javax.swing.JButton useLabDefaults_button;
    // End of variables declaration//GEN-END:variables
}
