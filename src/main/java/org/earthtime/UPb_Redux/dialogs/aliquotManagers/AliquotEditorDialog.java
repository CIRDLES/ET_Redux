/*
 * AliquotEditorDialog.java
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
package org.earthtime.UPb_Redux.dialogs.aliquotManagers;

import java.awt.Adjustable;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Vector;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.JTextComponent;
import javax.xml.transform.TransformerException;
import org.earthtime.ETReduxFrame;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.ReduxConstants.ANALYSIS_PURPOSE;
import org.earthtime.UPb_Redux.aliquots.UPbReduxAliquot;
import org.earthtime.UPb_Redux.dateInterpretation.DateProbabilityDensityPanel;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.ConcordiaGraphPanel;
import org.earthtime.UPb_Redux.dateInterpretation.graphPersistence.GraphAxesSetup;
import org.earthtime.UPb_Redux.dialogs.sampleManagers.sampleDateInterpretationManagers.SampleDateInterpretationSubscribeInterface;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.UPb_Redux.filters.XMLFileFilter;
import org.earthtime.UPb_Redux.fractions.FractionI;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFraction;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFractionI;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.fractionReduction.UPbFractionReducer;
import org.earthtime.UPb_Redux.renderers.EditFractionButton;
import org.earthtime.UPb_Redux.reports.excelReports.CsvResultsTable;
import org.earthtime.UPb_Redux.utilities.BrowserControl;
import org.earthtime.UPb_Redux.utilities.Thumbnail;
import org.earthtime.UPb_Redux.utilities.UPbReduxFocusTraversalPolicy;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.aliquots.AliquotInterface;
import org.earthtime.aliquots.ReduxAliquotInterface;
import org.earthtime.archivingTools.AnalysisImageInterface;
import org.earthtime.archivingTools.GeoPassIDValidator;
import static org.earthtime.archivingTools.GeoSamplesWebServices.isSampleRegisteredToParentAtGeoSamples;
import org.earthtime.archivingTools.GeochronUploadImagesHelper;
import org.earthtime.archivingTools.GeochronUploaderUtility;
import org.earthtime.archivingTools.URIHelper;
import org.earthtime.beans.ET_JButton;
import org.earthtime.dataDictionaries.AnalysisImageTypes;
import org.earthtime.dataDictionaries.AnalysisMeasures;
import org.earthtime.dataDictionaries.DataDictionary;
import org.earthtime.dataDictionaries.GeochronValidationResults;
import org.earthtime.dataDictionaries.MineralTypes;
import org.earthtime.dataDictionaries.RadDates;
import org.earthtime.dataDictionaries.SampleTypesEnum;
import org.earthtime.dialogs.DialogEditor;
import org.earthtime.exceptions.ETException;
import org.earthtime.exceptions.ETWarningDialog;
import org.earthtime.fractions.ETFractionInterface;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataModels.initialPbModelsET.InitialPbModelET;
import org.earthtime.ratioDataModels.initialPbModelsET.StaceyKramersInitialPbModelET;
import org.earthtime.ratioDataModels.mineralStandardModels.MineralStandardUPbModel;
import org.earthtime.ratioDataModels.tracers.TracerUPbModel;
import org.earthtime.reduxLabData.ReduxLabData;
import org.earthtime.reportViews.ReportAliquotFractionsView;
import org.earthtime.reportViews.TabbedReportViews;
import org.earthtime.samples.SampleInterface;
import org.earthtime.utilities.FileHelper;
import org.earthtime.xmlUtilities.SimpleTransform;
import org.jdesktop.layout.GroupLayout.ParallelGroup;
import org.jdesktop.layout.GroupLayout.SequentialGroup;

/**
 *
 * @author James F. Bowring
 */
public class AliquotEditorDialog extends DialogEditor {

    /**
     *
     */
    protected final static Font redHeadFont = new Font("Monospaced", Font.BOLD, 10);
    /**
     *
     */
    protected final static Font dropDownFont = new Font("SansSerif", Font.BOLD, 11);
    /**
     *
     */
    protected UPbReduxFocusTraversalPolicy publishPanelFocusTraversalPolicy;
    /**
     *
     */
    protected ClassLoader cldr = this.getClass().getClassLoader();
    /**
     *
     */
    protected java.net.URL imageGeochronURL = cldr.getResource("org/earthtime/images/geochron-logo-150.jpg");
    /**
     *
     */
    protected ImageIcon myGeochronIcon = new ImageIcon(imageGeochronURL);
    /**
     *
     */
    protected AliquotInterface myAliquot;
    /**
     *
     */
    protected SampleInterface sample;
    /**
     *
     */
    protected Vector<ETFractionInterface> deletedFractions;
    /**
     *
     */
    protected Vector<ETFractionInterface> addedFractions;
    // Instance variables
    /**
     * Default variables for new fractions
     */
//    protected JTextField masterNewFractionName = null;
//    protected JButton masterNewFractionNameAdder = null;
    protected JCheckBox masterZirconCaseCheckBox = null;
    /**
     *
     */
    protected JButton masterZironCaseFiller = null;
    /**
     *
     */
    protected JComboBox<String> masterTracerChooser = null;
    /**
     *
     */
    protected JButton masterTracerFiller = null;
    /**
     *
     */
    protected JTextField masterTracerMass = null;
    /**
     *
     */
    protected JButton masterTracerMassFiller = null;
    /**
     *
     */
    protected JTextField masterFractionMass = null;
    /**
     *
     */
    protected JButton masterFractionMassFiller = null;
    /**
     *
     */
    protected JComboBox<String> masterAlphaPbChooser = null;
    /**
     *
     */
    protected JButton masterAlphaPbFiller = null;
    /**
     *
     */
    protected JComboBox<String> masterAlphaUChooser = null;
    /**
     *
     */
    protected JButton masterAlphaUFiller = null;
    /**
     *
     */
    protected JComboBox<String> masterPbBlankChooser = null;
    /**
     *
     */
    protected JButton masterPbBlankFiller = null;
    /**
     *
     */
    protected JComboBox<String> masterInitialPbModelChooser = null;
    /**
     *
     */
    protected JButton masterInitialPbModelFiller = null;
    /**
     *
     */
    protected JTextField masterEstimatedDate = null;
    /**
     *
     */
    protected JButton masterEstimatedDateFiller = null;
    /**
     *
     */
    protected JTextField masterStaceyKramersPctUncertainty = null;
    /**
     *
     */
    protected JButton masterStaceyKramersPctUncertaintyFiller = null;
    /**
     *
     */
    protected JTextField masterStaceyKramersCorrelationCoeffs = null;
    /**
     *
     */
    protected JButton masterStaceyKramersCorrelationCoeffsFiller = null;
    /**
     *
     */
    protected JTextField masterPbBlankMass = null;
    /**
     *
     */
    protected JButton masterPbBlankMassFiller = null;
    /**
     *
     */
    protected JTextField masterUBlankMass = null;
    /**
     *
     */
    protected JButton masterBlankUMassFiller = null;
    /**
     *
     */
    protected JTextField masterR238_235s = null;
    /**
     *
     */
    protected JButton masterR238_235sFiller = null;
    /**
     *
     */
    protected JTextField masterR238_235b = null;
    /**
     *
     */
    protected JButton masterR238_235bFiller = null;
    /**
     *
     */
    protected JTextField masterR18O_16O = null;
    /**
     *
     */
    protected JButton master18O_16OFiller = null;
    /**
     *
     */
    protected JTextField masterRTh_Umagma = null;
    /**
     *
     */
    protected JButton masterRTh_UmagmaFiller = null;
    /**
     *
     */
    protected JTextField masterAr231_235sample = null;
    /**
     *
     */
    protected JButton masterAr231_235sampleFiller = null;
    /**
     *
     */
    protected JTextField masterTracerMassOneSigma = null;
    /**
     *
     */
    protected JButton masterTracerMassOneSigmaFiller = null;
    /**
     *
     */
    protected JTextField masterUBlankMassOneSigma = null;
    /**
     *
     */
    protected JButton masterUBlankMassOneSigmaFiller = null;
    /**
     *
     */
    protected JTextField masterR238_235sOneSigma = null;
    /**
     *
     */
    protected JButton masterR238_235sOneSigmaFiller = null;
    /**
     *
     */
    protected JTextField masterR238_235bOneSigma = null;
    /**
     *
     */
    protected JButton masterR238_235bOneSigmaFiller = null;
    /**
     *
     */
    protected JTextField masterR18O_16OOneSigma = null;
    /**
     *
     */
    protected JButton master18O_16OOneSigmaFiller = null;
    /**
     *
     */
    protected JTextField masterPbBlankMassOneSigma = null;
    /**
     *
     */
    protected JButton masterPbBlankMassOneSigmaFiller = null;
    /**
     *
     */
    protected JTextField masterRTh_UmagmaOneSigma = null;
    /**
     *
     */
    protected JButton masterRTh_UmagmaOneSigmaFiller = null;
    /**
     *
     */
    protected JTextField masterAr231_235sampleOneSigma = null;
    /**
     *
     */
    protected JButton masterAr231_235sampleOneSigmaFiller = null;
    /**
     * ArrayLists of textboxes etc for editing fractions
     */
    protected ArrayList<JComponent> fractionDeleteButtons;// still used in legacy managers for now june 2011
    /**
     *
     */
    protected ArrayList<JComponent> fractionEditButtons;
    /**
     *
     */
    protected ArrayList<JComponent> fractionZirconCheckBox;
    /**
     *
     */
    protected ArrayList<JComboBox> fractionTracerChoice;
    /**
     *
     */
    protected ArrayList<JComponent> fractionTracerMassText;
    /**
     *
     */
    protected ArrayList<JLabel> fractionTracerGRAMS;
    /**
     *
     */
    protected ArrayList<JComponent> fractionMassText;
    /**
     *
     */
    protected ArrayList<JLabel> fractionMassGRAMS;
    /**
     *
     */
    protected ArrayList<JComboBox<String>> fractionAlphaPbChoice;
    /**
     *
     */
    protected ArrayList<JComboBox<String>> fractionAlphaUChoice;
    /**
     *
     */
    protected ArrayList<JComboBox<String>> fractionPbBlankChoice;
    /**
     *
     */
    protected ArrayList<JComboBox<String>> fractionInitialPbChoice;
    /**
     *
     */
    protected ArrayList<JComponent> fractionEstDateText;
    /**
     *
     */
    protected ArrayList<JLabel> fractionEstDateMEGAANNUM;
    /**
     *
     */
    protected ArrayList<JComponent> fractionStaceyKramersPctUncertaintyText;
    /**
     *
     */
    protected ArrayList<JComponent> fractionStaceyKramersCorrelationCoeffsText;
    /**
     *
     */
    protected ArrayList<JComponent> fractionPbBlankMassText;
    /**
     *
     */
    protected ArrayList<JLabel> fractionPbBlankPICOGRAMS;
    /**
     *
     */
    protected ArrayList<JComponent> fractionUBlankMassText;
    /**
     *
     */
    protected ArrayList<JLabel> fractionUBlankPICOGRAMS;
    /**
     *
     */
    protected ArrayList<JComponent> fractionR238_235sText;
    /**
     *
     */
    protected ArrayList<JComponent> fractionR238_235bText;
    /**
     *
     */
    protected ArrayList<JComponent> fractionR18O_16OText;
    /**
     *
     */
    protected ArrayList<JComponent> fractionRTh_UmagmaText;
    /**
     *
     */
    protected ArrayList<JComponent> fractionAR231_235sampleText;
    /**
     *
     */
    protected ArrayList<JComponent> fractionTracerMassOneSigmaText;
    /**
     *
     */
    protected ArrayList<JComponent> fractionUBlankMassOneSigmaText;
    /**
     *
     */
    protected ArrayList<JComponent> fractionR238_235sOneSigmaText;
    /**
     *
     */
    protected ArrayList<JComponent> fractionR238_235bOneSigmaText;
    /**
     *
     */
    protected ArrayList<JComponent> fractionR18O_16OOneSigmaText;
    /**
     *
     */
    protected ArrayList<JComponent> fractionPbBlankMassOneSigmaText;
    /**
     *
     */
    protected ArrayList<JComponent> fractionRTh_UmagmaOneSigmaText;
    /**
     *
     */
    protected ArrayList<JComponent> fractionAr231_235sampleOneSigmaText;
    // mineral standards tab
    /**
     *
     */
    protected ArrayList<JComponent> mineralStandardsCheckBoxes;
    /**
     *
     */
    protected SampleDateInterpretationSubscribeInterface parent = null;
    /**
     *
     */
    protected ETFractionInterface myFraction;

    /**
     * Creates new form AliquotEditorDialog
     *
     * @param parent
     * @param modal
     * @param sample
     * @param aliquot
     */
    public AliquotEditorDialog(
            SampleDateInterpretationSubscribeInterface parent,
            boolean modal,
            SampleInterface sample,
            AliquotInterface aliquot) {
        super((Frame) parent, modal);
        this.parent = parent;

        this.sample = sample;

        this.myAliquot = aliquot;

        initComponents();
        setSize();

        geochronIcon_label.setIcon(myGeochronIcon);

        // set up arrow keys etc
        // nov 2010 modified to use left and right keys to edit values
        Set<KeyStroke> forwardKeys = new HashSet<>();
        forwardKeys.add(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0));
//        forwardKeys.add( KeyStroke.getKeyStroke( KeyEvent.VK_RIGHT, 0 ) );
        fastEdits_panel.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardKeys);

        Set<KeyStroke> backwardKeys = new HashSet<>();
        backwardKeys.add(KeyStroke.getKeyStroke("shift TAB"));//    (KeyEvent.VK_SHIFT & KeyEvent.VK_TAB), 0));

//        backwardKeys.add( KeyStroke.getKeyStroke( KeyEvent.VK_LEFT, 0 ) );
        fastEdits_panel.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backwardKeys);

        // Listen for value changes in the scroll pane's scrollbars
        AdjustmentListener listener = new JscrollPaneScrollListener();
        fastEdits_scrollPane.getHorizontalScrollBar().addAdjustmentListener(listener);
        fastEdits_scrollPane.getVerticalScrollBar().addAdjustmentListener(listener);

        // eliminate default "crawl"
        fastEdits_scrollPane.getVerticalScrollBar().setUnitIncrement(32);
        fastEdits_scrollPane.getHorizontalScrollBar().setUnitIncrement(64);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        this.setTitle("Aliquot Manager for:  " //
                + sample.getSampleName()//
                + "::"//
                + aliquot.getAliquotName()//
                + "  Purpose of Analysis is " + aliquot.getAnalysisPurpose());

        title_panel.setBackground(ReduxConstants.myAliquotGrayColor);
        details_tabbedPane.setBackground(ReduxConstants.myFractionGreenColor);
        buttonsPanel.setBackground(ReduxConstants.myAliquotGrayColor);

        boolean isCompiled = ((ReduxAliquotInterface) myAliquot).isCompiled();

        aliquotName_text.setDocument(new UnDoAbleDocument(aliquotName_text,//
                !((UPbReduxAliquot) aliquot).isAutomaticDataUpdateMode()
                && !isCompiled));

        analystName_text.setDocument(new UnDoAbleDocument(//
                analystName_text, !isCompiled));

        instMethodRef_text.setDocument(new UnDoAbleDocument(//
                instMethodRef_text, !isCompiled));

        reference_text.setDocument(new UnDoAbleDocument(//
                reference_text, !isCompiled));

        comment_textArea.setDocument(new UnDoAbleDocument(//
                comment_textArea, !isCompiled));

        // pre-publication tab
        calibrationUnct206_238_text.setDocument(new UnDoAbleDocument(//
                calibrationUnct206_238_text, !isCompiled));

        calibrationUnct208_232_text.setDocument(new UnDoAbleDocument(//
                calibrationUnct208_232_text, !isCompiled));

        calibrationUnct207_206_text.setDocument(new UnDoAbleDocument(//
                calibrationUnct207_206_text, !isCompiled));

        mineralStandardsCheckBoxes = new ArrayList<>();
        int count = 0;
        for (AbstractRatiosDataModel msm : ReduxLabData.getInstance().getMineralStandardModels()) {
            if (!(msm.equals(MineralStandardUPbModel.getNoneInstance()))) {
                count++;
                JCheckBox msmCheckBox = new JCheckBox(msm.getReduxLabDataElementName());
                msmCheckBox.setBounds(10, 20 * count + 45, mineralStandardsPanel.getWidth() - 25, 25);
                mineralStandardsCheckBoxes.add(msmCheckBox);
                mineralStandardsPanel.add(//
                        msmCheckBox,
                        javax.swing.JLayeredPane.DEFAULT_LAYER);
                msmCheckBox.setEnabled(!isCompiled);
            }
        }

        String validityReport
                = //
                ((UPbReduxAliquot) myAliquot).reportFractionMeasuredRatioUncertaintiesValidity();
        if (validityReport.length() > 0) {
            validityReport
                    = //
                    "<html> ET_Redux determines that some measured ratios in this aliquot "//
                    + "have correlations outside of [-1.0,,,1.0] and advises against archiving.<br><br>"//
                    + validityReport//
                    + "</html>";
        } else {
            validityReport
                    = //
                    "<html>ET_Redux determines that the correlations between your measured ratios"//
                    + " are within the correct range of [-1,,,1].</html>";
        }

        measuredRatioValidity_JEditorPanel.setEditable(false);
        measuredRatioValidity_JEditorPanel.setContentType("text/html");
        measuredRatioValidity_JEditorPanel.setText(validityReport + "</html>");

        keyWordsCSV_text.setDocument(new UnDoAbleDocument(//
                keyWordsCSV_text, !isCompiled));

        aliquotIGSN_text.setDocument(new UnDoAbleDocument(aliquotIGSN_text, true));// sept 2016 needs work//april 2011 ! isCompiled ) );
//        aliquotIGSN_text.setInputVerifier(new GeochronAliquotManager.AliquotIGSNVerifier(i));
//        aliquotIGSN_text.addKeyListener(new KeyAdapter() {
//            @Override
//            public void keyPressed(KeyEvent e) {
//                JTextField textField = (JTextField) e.getSource();
//                int key = e.getKeyCode();
//                if ((key == KeyEvent.VK_ENTER) || (key == KeyEvent.VK_TAB)) {
//                    textField.getInputVerifier().verify(textField);
//                }
//            }
//        }
//        );

        // oct 2011
        initializeFractionArchivingTab();

        // setup components
        setVisiblePublishTabComponents(!isCompiled);
        saveAndClose_button.setEnabled(!isCompiled);
        exportXMLAliquot_button.setEnabled(!isCompiled);
        save_button.setEnabled(!isCompiled);
        restore_button.setEnabled(!isCompiled);
        instrumentalMethod_jcombo.setEnabled(!isCompiled);
        analystName_text.setEnabled(!isCompiled);

        // april 2009 compatibililty
        if (myAliquot.getAliquotIGSN().equalsIgnoreCase("00000000")) {
            myAliquot.setAliquotIGSN(ReduxConstants.DEFAULT_ALIQUOT_IGSN);
        }

        initAliquot();

        // set focus traversal policy for publish tab
        ArrayList<Component> order = new ArrayList<>();
        order.add(geoPassUserName_text);
        order.add(geoPassPassword_passwordField);
        order.add(validateGeoPassID_button);
        order.add(saveAndPreviewXMLAliquotAsHTML_button);
        order.add(saveAndUploadAliquotToGeochron_button);

        publishPanelFocusTraversalPolicy = new UPbReduxFocusTraversalPolicy(order);
        publishAliquot_panel.setFocusCycleRoot(true);
        publishAliquot_panel.setFocusTraversalPolicy(publishPanelFocusTraversalPolicy);
        geoPassUserName_text.requestFocusInWindow();

        showFractionNotes();

        // Archive tab
        checkMarkForValidGeoPassID_label.setVisible(false);
        xMarkForValidGeoPassID_label.setVisible(false);

        geoPassUserName_text.setDocument(new UnDoAbleDocument(geoPassUserName_text, true));
        geoPassUserName_text.setText(((ETReduxFrame) parent).getMyState().getReduxPreferences().getGeochronUserName());
        geoPassUserName_text.setCaretPosition(0);

        geoPassPassword_passwordField.setDocument(new UnDoAbleDocument(geoPassPassword_passwordField, true));
        geoPassPassword_passwordField.setText(((ETReduxFrame) parent).getMyState().getReduxPreferences().getGeochronPassWord());
        geoPassPassword_passwordField.setCaretPosition(0);

    }

//    private class AliquotIGSNVerifier extends InputVerifier {
//
//        @Override
//        public boolean verify(JComponent input) {
//            
//        }
//
//    }
    // moved from fraction per Doug Walker october 2011
    private void initializeFractionArchivingTab() {

        initializeFractionArchivingTextBoxes();

        // populate combobox for fractions
        fraction_Chooser.removeAllItems();

        // add the not-rejected fractions
        for (ETFractionInterface f : getMyAliquot().getAliquotFractions()) {
            if (!f.isRejected()) {
                fraction_Chooser.addItem(f);
            }
        }

        fraction_Chooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox cb = (JComboBox) e.getSource();

                myFraction = (ETFractionInterface) cb.getSelectedItem();
                showSavedFractionArchivingData(myFraction);
            }
        });

        fraction_Chooser.setSelectedIndex(-1);
        fraction_Chooser.setSelectedIndex(0);

    }

    private void initializeFractionArchivingTextBoxes() {

        // copied from initializeFractionArchivingTextBoxes at UPbFractionEditor
        // Archiving (as of dec 2010) TODO: rename components publication details
        boolean isCompiled = sample.getSampleType().equalsIgnoreCase(SampleTypesEnum.COMPILATION.getName());

        // set up StandardMineral chooser
        mineralNameChooser.removeAllItems();
        for (int i = 0; i < MineralTypes.values().length; i++) {
            mineralNameChooser.addItem(MineralTypes.values()[i].getName());
        }

        mineralNameChooser.setEnabled(!isCompiled);

        // setting type
        settingTypeChooser.removeAllItems();
        for (int i = 0; i < DataDictionary.SettingType.length; i++) {
            settingTypeChooser.addItem(DataDictionary.SettingType[i]);
        }

        settingTypeChooser.setEnabled(!isCompiled);

        countOfGrains_text.setDocument(new IntegerDocument(countOfGrains_text, !isCompiled));
        physicallyAbraded_chkBox.setEnabled(!isCompiled);
        leachedInHFAcid_chkBox.setEnabled(!isCompiled);
        annealedChemicallyAbraded_chkBox.setEnabled(!isCompiled);
        chemicallyPurifiedUPb_chkBox.setEnabled(!isCompiled);

        fractionComment_text.setDocument(new UnDoAbleDocument(fractionComment_text, !isCompiled));
        publicationTimeStamp_text.setDocument(new UnDoAbleDocument(publicationTimeStamp_text, false));

        selectImageFile_button.setEnabled(!isCompiled);
    }

    private void showSavedFractionArchivingData(ETFractionInterface myFraction) {
        if (myFraction != null) {
            mineralNameChooser.setSelectedItem(((FractionI) myFraction).getMineralName());
            settingTypeChooser.setSelectedItem(((FractionI) myFraction).getSettingType());
            countOfGrains_text.setText(String.valueOf(myFraction.getNumberOfGrains()));
            physicallyAbraded_chkBox.setSelected(((FractionI) myFraction).isPhysicallyAbraded());
            leachedInHFAcid_chkBox.setSelected(((FractionI) myFraction).isLeachedInHFAcid());
            annealedChemicallyAbraded_chkBox.setSelected(((FractionI) myFraction).isAnnealedAndChemicallyAbraded());
            chemicallyPurifiedUPb_chkBox.setSelected(((FractionI) myFraction).isChemicallyPurifiedUPb());
            fractionComment_text.setText(myFraction.getAnalysisFractionComment());

            publicationTimeStamp_text.setText(myFraction.getTimeStamp().toString());

            displayThumbnailForFraction(myFraction);
            validate();
        }
    }

    private void showLabDefaultFractionArchivingData(ETFractionInterface myFraction) {

        if (myFraction != null) {
            ReduxLabData myLabData = ((UPbFraction) myFraction).getMyLabData();

            mineralNameChooser.setSelectedItem(myLabData.getDefaultMineralName());
            settingTypeChooser.setSelectedItem(myLabData.getDefaultSettingType());
            countOfGrains_text.setText(String.valueOf(myLabData.getDefaultNumberOfGrains()));
            physicallyAbraded_chkBox.setSelected(myLabData.isDefaultPhysicallyAbraded());
            leachedInHFAcid_chkBox.setSelected(myLabData.isDefaultLeachedInHFAcid());
            annealedChemicallyAbraded_chkBox.setSelected(myLabData.isDefaultAnnealedAndChemicallyAbraded());
            chemicallyPurifiedUPb_chkBox.setSelected(myLabData.isDefaultChemicallyPurifiedUPb());
        }
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

    /**
     *
     */
    protected final void setSize() {
        setSize(1125, 754);
    }

    private void showFractionNotes() {
        for (ETFractionInterface f : getMyAliquot().getAliquotFractions()) {
            String fractionNote = f.getFractionNotes();
            fractionNotes_textArea.setText(//
                    fractionNotes_textArea.getText()//
                    + "**********************\n"//
                    + "Fraction  " + f.getFractionID() //
                    + "\nNotes: " //
                    + fractionNote//
                    + "\n\n");
        }
    }

    // Section for exporting Aliquot as XML 
    /**
     *
     */
    protected void exportAliquotToXML() {

        // moved to caller: saveAliquot();
        setAlwaysOnTop(false);

        String dialogTitle = "Save this Aliquot as xml: *.xml";
        final String fileExtension = ".xml";
        String aliquotFileName
                = getSample().getSampleName() //
                + "_" //
                + getMyAliquot().getAliquotName()//
                + "_" //
                + getSample().getSampleIGSN()//
                + "_"//
                + getMyAliquot().getAliquotIGSN()//
                + fileExtension;

        File selectedFile = null;
        FileFilter nonMacFileFilter = new XMLFileFilter();

        selectedFile = FileHelper.AllPlatformSaveAs(
                new Frame(), dialogTitle, null, fileExtension, aliquotFileName, nonMacFileFilter);

        if (selectedFile != null) {
            try {
                // export
                getMyAliquot().serializeXMLObject(selectedFile.getCanonicalPath());
            } catch (IOException ex) {
            }

        }

        setAlwaysOnTop(true);

    }

    /**
     *
     * @param tempAliquotXML
     */
    public void viewXMLAliquotAsHTML(String tempAliquotXML) {

        // read in the remote transform and save locally
        String tempXSLTcontents = URIHelper.getTextFromURI(
                "https://www.geochron.org/upbgeochron.xslt");

        // write this to a file
        String tempXSLT = "TempTransformer.xslt";
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(tempXSLT);
        } catch (FileNotFoundException ex) {
        }
        OutputStreamWriter out = new OutputStreamWriter(fos);
        try {
            out.write(tempXSLTcontents);
            out.flush();
            out.close();
        } catch (IOException ex) {
        }

        // create the name of the result file
        String tempHTML = "TempAliquot.html";

        // transform the file to xml
        SimpleTransform transformer = new SimpleTransform();
        try {
            transformer.TransformXMLtoHTML(tempAliquotXML, tempXSLT, tempHTML);
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage() + "  >>>>> " + ex.getCause());
        } catch (IOException | TransformerException ex) {
            System.out.println(ex.getMessage() + "  >>>>> " + ex.getCause());
        }

        // modify the html to use css
        String content = "";
        try {
            Scanner scanner = new Scanner(new File(tempHTML));
            while (scanner.hasNextLine()) {
                content += scanner.nextLine() + "\n";
            }
            scanner.close();

        } catch (FileNotFoundException fileNotFoundException) {
        }

        content
                = //
                "<html><head>" + "<link rel=\"stylesheet\" type = \"text/css\" href = \"https://www.geochron.org/geo.css\"/>" + "</head>" //
                + content//
                + "</html>";

        // write this to a file
        try {
            fos = new FileOutputStream(tempHTML);
        } catch (FileNotFoundException ex) {
//            ex.printStackTrace();
        }
        out = new OutputStreamWriter(fos);
        try {
            out.write(content);
            out.flush();
            out.close();
            fos.close();
        } catch (IOException ex) {
//            ex.printStackTrace();
        }

        // show in a browser
        BrowserControl.displayURL(tempHTML);
    }

    private void saveAliquotFraction(ETFractionInterface fractionToSave)
            throws NumberFormatException {

        // TODO: feb 2010 refactor to remove repeats and switch to SAMPLEFOLDER
        if ((sample.getSampleType().equalsIgnoreCase(SampleTypesEnum.ANALYSIS.getName()))
                || (sample.getSampleType().equalsIgnoreCase(SampleTypesEnum.LIVEWORKFLOW.getName()))
                || (sample.getSampleType().equalsIgnoreCase(SampleTypesEnum.SAMPLEFOLDER.getName()))) {

            // set temp variable for fractionation correction both u and Pb to use in locking fields
            boolean fraCorrU = ((FractionI) fractionToSave).isFractionationCorrectedU();
            boolean fraCorrPb = ((FractionI) fractionToSave).isFractionationCorrectedPb();

            int row = getMyAliquot().getAliquotFractions().indexOf(fractionToSave);

            // feb 2009
            // test zircon state change for ReductionHandler
            try {
                if (((FractionI) fractionToSave).isZircon() != ((JCheckBox) fractionZirconCheckBox.get(row)).isSelected()) {
                    ((FractionI) fractionToSave).setZircon(((AbstractButton) fractionZirconCheckBox.get(row)).isSelected());
                    ((UPbFraction) fractionToSave).initializeReductionHandler();
                }
            } catch (Exception e) {
            }

            // check for locked down tracer first
            if (!(fraCorrU || fraCorrPb)) {
                try {
                    AbstractRatiosDataModel tracer = ((UPbFraction) fractionToSave).getMyLabData().//
                            getATracerModel((String) fractionTracerChoice.get(row).getSelectedItem());
                    ((UPbFractionI) fractionToSave).setTracer(tracer);
                } catch (BadLabDataException ex) {
                    new ETWarningDialog(ex).setVisible(true);
                }
            }

            fractionToSave.getAnalysisMeasure(AnalysisMeasures.tracerMassInGrams.getName())//
                    .setValue(new BigDecimal(((JTextComponent) fractionTracerMassText.get(row)).getText(), ReduxConstants.mathContext15));

            // feb 2022 issue #206 fraction mass units switch to micrograms from grams
//            fractionToSave.getAnalysisMeasure(AnalysisMeasures.fractionMass.getName())//
//                    .setValue(new BigDecimal(((JTextComponent) fractionMassText.get(row)).getText(), ReduxConstants.mathContext15));
            fractionToSave.getAnalysisMeasure(AnalysisMeasures.fractionMass.getName())//
                .setValue(new BigDecimal(((JTextComponent) fractionMassText.get(row)).getText()).movePointLeft(6));

            try {
                ValueModel alphaPb = ((UPbFraction) fractionToSave).getMyLabData().getNoneAlphaPbModel();
                if (fractionAlphaPbChoice.get(row).isEnabled()) {
                    alphaPb = ((UPbFraction) fractionToSave).getMyLabData().getAnAlphaPbModel((String) fractionAlphaPbChoice.get(row).getSelectedItem());
                }
                ((UPbFractionI) fractionToSave).setAlphaPbModel(alphaPb);
            } catch (BadLabDataException ex) {
                new ETWarningDialog(ex).setVisible(true);
            }

            try {
                ValueModel alphaU = ((UPbFraction) fractionToSave).getMyLabData().getNoneAlphaUModel();
                if (fractionAlphaUChoice.get(row).isEnabled()) {
                    alphaU = ((UPbFraction) fractionToSave).getMyLabData().getAnAlphaUModel((String) fractionAlphaUChoice.get(row).getSelectedItem());
                }
                ((UPbFractionI) fractionToSave).setAlphaUModel(alphaU);
                // handles auto-u generation case
                ((UPbFraction) fractionToSave).setInputAlphaU(alphaU);
            } catch (BadLabDataException ex) {
                new ETWarningDialog(ex).setVisible(true);
            }

            try {
                AbstractRatiosDataModel pbBlank = null;
                pbBlank = ((UPbFraction) fractionToSave).getMyLabData().getAPbBlankModel((String) fractionPbBlankChoice.get(row).getSelectedItem());
                ((UPbFractionI) fractionToSave).setPbBlank(pbBlank);
            } catch (BadLabDataException ex) {
                new ETWarningDialog(ex).setVisible(true);
            }

            try {
                AbstractRatiosDataModel initialPbModel = ((UPbFraction) fractionToSave).getMyLabData().//
                        getAnInitialPbModel((String) fractionInitialPbChoice.get(row).getSelectedItem());
                ((FractionI) fractionToSave).setInitialPbModel(initialPbModel);

                // march 2009 special condition for stacey kramers
                if (initialPbModel instanceof StaceyKramersInitialPbModelET) {

                    // set special fields for Stacey Kramers
                    fractionToSave.setEstimatedDate(new BigDecimal(((JTextComponent) fractionEstDateText.get(row)).getText(), ReduxConstants.mathContext15));

                    ((FractionI) fractionToSave).setStaceyKramersOnePctUnct(//
                            new BigDecimal(((JTextComponent) fractionStaceyKramersPctUncertaintyText.get(row)).getText(), ReduxConstants.mathContext15));

                    ((FractionI) fractionToSave).setStaceyKramersCorrelationCoeffs(//
                            new BigDecimal(((JTextComponent) fractionStaceyKramersCorrelationCoeffsText.get(row)).getText(), ReduxConstants.mathContext15));

                    ((FractionI) fractionToSave).calculateStaceyKramersInitialPbModelValues();
                }

            } catch (BadLabDataException ex) {
                new ETWarningDialog(ex).setVisible(true);
            }

            // set fraction's pbBlankMass
            fractionToSave.getAnalysisMeasure(AnalysisMeasures.pbBlankMassInGrams.getName()).//
                    setValue(new BigDecimal(((JTextComponent) fractionPbBlankMassText.get(row)).getText(), ReduxConstants.mathContext15).//
                            movePointLeft(12));

            // set fraction's uBlankMass
            fractionToSave.getAnalysisMeasure(AnalysisMeasures.uBlankMassInGrams.getName()).//
                    setValue(new BigDecimal(((JTextComponent) fractionUBlankMassText.get(row)).getText(), ReduxConstants.mathContext15).//
                            movePointLeft(12));

            fractionToSave.getAnalysisMeasure(AnalysisMeasures.r238_235s.getName())//
                    .setValue(new BigDecimal(((JTextComponent) fractionR238_235sText.get(row)).getText(), ReduxConstants.mathContext15));

            fractionToSave.getAnalysisMeasure(AnalysisMeasures.r238_235b.getName())//
                    .setValue(new BigDecimal(((JTextComponent) fractionR238_235bText.get(row)).getText(), ReduxConstants.mathContext15));

            fractionToSave.getAnalysisMeasure(AnalysisMeasures.r18O_16O_revised.getName())//
                    .setValue(new BigDecimal(((JTextComponent) fractionR18O_16OText.get(row)).getText(), ReduxConstants.mathContext15));

            fractionToSave.getAnalysisMeasure(AnalysisMeasures.rTh_Umagma.getName())//
                    .setValue(new BigDecimal(((JTextComponent) fractionRTh_UmagmaText.get(row)).getText(), ReduxConstants.mathContext15));
            fractionToSave.getAnalysisMeasure(AnalysisMeasures.ar231_235sample.getName())//
                    .setValue(new BigDecimal(((JTextComponent) fractionAR231_235sampleText.get(row)).getText(), ReduxConstants.mathContext15));

            // uncertainties
            fractionToSave.getAnalysisMeasure(AnalysisMeasures.tracerMassInGrams.getName())//
                    .setOneSigma(new BigDecimal(((JTextComponent) fractionTracerMassOneSigmaText.get(row)).getText(), ReduxConstants.mathContext15));

            fractionToSave.getAnalysisMeasure(AnalysisMeasures.uBlankMassInGrams.getName())//
                    .setOneSigma(new BigDecimal(((JTextComponent) fractionUBlankMassOneSigmaText.get(row)).getText(), ReduxConstants.mathContext15).//
                            movePointLeft(12));

            fractionToSave.getAnalysisMeasure(AnalysisMeasures.r238_235s.getName())//
                    .setOneSigma(new BigDecimal(((JTextComponent) fractionR238_235sOneSigmaText.get(row)).getText(), ReduxConstants.mathContext15));

            fractionToSave.getAnalysisMeasure(AnalysisMeasures.r238_235b.getName())//
                    .setOneSigma(new BigDecimal(((JTextComponent) fractionR238_235bOneSigmaText.get(row)).getText(), ReduxConstants.mathContext15));

            fractionToSave.getAnalysisMeasure(AnalysisMeasures.r18O_16O_revised.getName())//
                    .setOneSigma(new BigDecimal(((JTextComponent) fractionR18O_16OOneSigmaText.get(row)).getText(), ReduxConstants.mathContext15));

            fractionToSave.getAnalysisMeasure(AnalysisMeasures.pbBlankMassInGrams.getName())//
                    .setOneSigma(new BigDecimal(((JTextComponent) fractionPbBlankMassOneSigmaText.get(row)).getText(), ReduxConstants.mathContext15).//
                            movePointLeft(12));
            fractionToSave.getAnalysisMeasure(AnalysisMeasures.rTh_Umagma.getName())//
                    .setOneSigma(new BigDecimal(((JTextComponent) fractionRTh_UmagmaOneSigmaText.get(row)).getText(), ReduxConstants.mathContext15));
            fractionToSave.getAnalysisMeasure(AnalysisMeasures.ar231_235sample.getName())//
                    .setOneSigma(new BigDecimal(((JTextComponent) fractionAr231_235sampleOneSigmaText.get(row)).getText(), ReduxConstants.mathContext15));

            // better safe than sorry for now
            fractionToSave.setChanged(true);

            // reduce fraction
            UPbFractionReducer.getInstance().fullFractionReduce((FractionI) fractionToSave, true);

        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        registryButtonGroup = new javax.swing.ButtonGroup();
        title_panel = new javax.swing.JPanel();
        aliquotIName_label = new javax.swing.JLabel();
        aliquotName_text = new javax.swing.JTextField();
        SampleIGSN_label = new javax.swing.JLabel();
        analystName_label = new javax.swing.JLabel();
        analystName_text = new javax.swing.JTextField();
        instrumentalMethod_label = new javax.swing.JLabel();
        instrumentalMethod_jcombo = new javax.swing.JComboBox<>();
        instrumentalMethod_label1 = new javax.swing.JLabel();
        aliquotIGSN_text = new javax.swing.JTextField();
        details_tabbedPane = new javax.swing.JTabbedPane();
        fastEdits_scrollPane = new javax.swing.JScrollPane();
        fastEdits_panel = new javax.swing.JPanel();
        fractionNotes_scrollPane = new javax.swing.JScrollPane();
        fractionNotes_textArea = new javax.swing.JTextArea();
        archivingDetails_panel = new javax.swing.JPanel();
        mineralNameChooser = new javax.swing.JComboBox<>();
        chooseMineral_label = new javax.swing.JLabel();
        chooseSettingType_label = new javax.swing.JLabel();
        settingTypeChooser = new javax.swing.JComboBox<>();
        countOfGrains_label = new javax.swing.JLabel();
        physicallyAbraded_chkBox = new javax.swing.JCheckBox();
        leachedInHFAcid_chkBox = new javax.swing.JCheckBox();
        annealedChemicallyAbraded_chkBox = new javax.swing.JCheckBox();
        chemicallyPurifiedUPb_chkBox = new javax.swing.JCheckBox();
        fractionComment_label = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        fractionComment_text = new javax.swing.JTextArea();
        countOfGrains_text = new javax.swing.JTextField();
        publicationTimeStamp_label = new javax.swing.JLabel();
        publicationTimeStamp_text = new javax.swing.JTextField();
        selectImageFile_button = new javax.swing.JButton();
        imageThumbnail_Panel = new javax.swing.JPanel();
        imageThumbnail_label = new javax.swing.JLabel();
        chooseSettingType_label1 = new javax.swing.JLabel();
        fractionID_label1 = new javax.swing.JLabel();
        fraction_Chooser = new javax.swing.JComboBox<>();
        fractionID_label2 = new javax.swing.JLabel();
        useLabDefaults_button = new javax.swing.JButton();
        refsAndComment_panel = new javax.swing.JPanel();
        instMethodRef_label = new javax.swing.JLabel();
        instMethodRef_text = new javax.swing.JTextField();
        reference_label = new javax.swing.JLabel();
        reference_text = new javax.swing.JTextField();
        comment_label = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        comment_textArea = new javax.swing.JTextArea();
        calibrationUnct206_238_label = new javax.swing.JLabel();
        calibrationUnct208_232_label = new javax.swing.JLabel();
        calibrationUnct207_206_label = new javax.swing.JLabel();
        calibrationUnct207_206_text = new javax.swing.JTextField();
        calibrationUnct208_232_text = new javax.swing.JTextField();
        calibrationUnct206_238_text = new javax.swing.JTextField();
        mineralStandardsPanel = new javax.swing.JLayeredPane();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        comment_label1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        measuredRatioValidity_JEditorPanel = new javax.swing.JEditorPane();
        keyWords_label = new javax.swing.JLabel();
        keyWordsCSV_text = new javax.swing.JTextField();
        publishAliquot_panel = new javax.swing.JLayeredPane();
        geochronIcon_label = new javax.swing.JLabel();
        visitGeochron_button = new javax.swing.JButton();
        saveAndPreviewXMLAliquotAsHTML_button = new javax.swing.JButton();
        checkMarkForValidSampleID_label = new javax.swing.JLabel();
        xMarkForValidSampleID_label = new javax.swing.JLabel();
        sampleIsRegistered_label = new javax.swing.JLabel();
        geochronArchivePanel_panel = new javax.swing.JLayeredPane();
        jLabel13 = new javax.swing.JLabel();
        saveAndUploadAliquotToGeochron_button = new javax.swing.JButton();
        chooseUploadOptions_label = new javax.swing.JLabel();
        geochronPublicRecord_chkBox = new javax.swing.JCheckBox();
        geochronOverwrite_chkBox = new javax.swing.JCheckBox();
        geochronOverwrite_chkBox2 = new javax.swing.JCheckBox();
        concordiaUpload_chkBox = new javax.swing.JCheckBox();
        geochronOverwrite_chkBox5 = new javax.swing.JCheckBox();
        probabilityDensityUpload_chkBox = new javax.swing.JCheckBox();
        reportAsCSV_chkBox = new javax.swing.JCheckBox();
        reportSVGUpload_chkbox = new javax.swing.JCheckBox();
        geoPassPassword_passwordField = new javax.swing.JPasswordField();
        passwordGeochron_label1 = new javax.swing.JLabel();
        geoPassUserName_text = new javax.swing.JTextField();
        userNameGeochron_label = new javax.swing.JLabel();
        validateGeoPassID_button = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        checkMarkForValidGeoPassID_label = new javax.swing.JLabel();
        xMarkForValidGeoPassID_label = new javax.swing.JLabel();
        archiveNote_label = new javax.swing.JLabel();
        archivePanelTitle_label = new javax.swing.JLabel();
        archivePanelTitle_label1 = new javax.swing.JLabel();
        aliquotIsRegistered_label = new javax.swing.JLabel();
        checkMarkForValidAliquotIGSN_label = new javax.swing.JLabel();
        xMarkForValidAliquotIGSN_label = new javax.swing.JLabel();
        buttonsPanel = new javax.swing.JPanel();
        saveAndClose_button = new ET_JButton();
        save_button = new ET_JButton();
        restore_button = new ET_JButton();
        close_button = new ET_JButton();
        exportXMLAliquot_button = new ET_JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        title_panel.setBackground(new java.awt.Color(180, 180, 180));
        title_panel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        aliquotIName_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        aliquotIName_label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        aliquotIName_label.setText("<html>Aliquot Name:</html>");
        title_panel.add(aliquotIName_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 0, 43, 37));

        aliquotName_text.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        aliquotName_text.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        aliquotName_text.setText("NONE");
        title_panel.add(aliquotName_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(55, 5, 172, -1));

        SampleIGSN_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        SampleIGSN_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        SampleIGSN_label.setText("<html>Aliquot IGSN:</html>");
        title_panel.add(SampleIGSN_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(233, 0, 60, 38));

        analystName_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        analystName_label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        analystName_label.setText("Analyst:");
        title_panel.add(analystName_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(869, 4, -1, 29));

        analystName_text.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        analystName_text.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        analystName_text.setText("analyst name");
        title_panel.add(analystName_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(927, 5, 192, -1));

        instrumentalMethod_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        instrumentalMethod_label.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        instrumentalMethod_label.setText("<html>Inst Method:</html>");
        title_panel.add(instrumentalMethod_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(419, 6, 51, 25));

        instrumentalMethod_jcombo.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        title_panel.add(instrumentalMethod_jcombo, new org.netbeans.lib.awtextra.AbsoluteConstraints(476, 5, 157, -1));

        instrumentalMethod_label1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        instrumentalMethod_label1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        instrumentalMethod_label1.setText("<html>Equipment Settings:</html>");
        title_panel.add(instrumentalMethod_label1, new org.netbeans.lib.awtextra.AbsoluteConstraints(645, 6, 68, 25));

        aliquotIGSN_text.setForeground(new java.awt.Color(255, 0, 0));
        aliquotIGSN_text.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        aliquotIGSN_text.setText("IGSN");
        title_panel.add(aliquotIGSN_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(299, 5, 114, -1));

        getContentPane().add(title_panel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 1125, 42));

        details_tabbedPane.setBackground(new java.awt.Color(0, 0, 0));
        details_tabbedPane.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        details_tabbedPane.setOpaque(true);
        details_tabbedPane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                details_tabbedPaneMouseClicked(evt);
            }
        });

        fastEdits_panel.setBackground(new java.awt.Color(255, 255, 255));
        fastEdits_panel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        fastEdits_scrollPane.setViewportView(fastEdits_panel);

        details_tabbedPane.addTab("Fraction Fast Details", fastEdits_scrollPane);

        fractionNotes_textArea.setColumns(20);
        fractionNotes_textArea.setEditable(false);
        fractionNotes_textArea.setLineWrap(true);
        fractionNotes_textArea.setRows(5);
        fractionNotes_textArea.setWrapStyleWord(true);
        fractionNotes_textArea.setMargin(new java.awt.Insets(10, 20, 10, 20));
        fractionNotes_scrollPane.setViewportView(fractionNotes_textArea);

        details_tabbedPane.addTab("Fraction Notes", fractionNotes_scrollPane);

        archivingDetails_panel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        archivingDetails_panel.add(mineralNameChooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(249, 127, 174, -1));

        chooseMineral_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chooseMineral_label.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        chooseMineral_label.setText("  Choose material for fraction:");
        archivingDetails_panel.add(chooseMineral_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(78, 133, -1, -1));

        chooseSettingType_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chooseSettingType_label.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        chooseSettingType_label.setText("  Choose setting type for fraction:");
        archivingDetails_panel.add(chooseSettingType_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 166, 234, -1));
        archivingDetails_panel.add(settingTypeChooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(250, 160, 174, -1));

        countOfGrains_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        countOfGrains_label.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        countOfGrains_label.setText("Count of grains:");
        archivingDetails_panel.add(countOfGrains_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 198, 234, -1));

        physicallyAbraded_chkBox.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        physicallyAbraded_chkBox.setText("Physically abraded");
        physicallyAbraded_chkBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        archivingDetails_panel.add(physicallyAbraded_chkBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(104, 237, -1, -1));

        leachedInHFAcid_chkBox.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        leachedInHFAcid_chkBox.setText("Leached in HF acid");
        leachedInHFAcid_chkBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        archivingDetails_panel.add(leachedInHFAcid_chkBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(105, 272, -1, -1));

        annealedChemicallyAbraded_chkBox.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        annealedChemicallyAbraded_chkBox.setText("Annealed & chemically abraded");
        annealedChemicallyAbraded_chkBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        archivingDetails_panel.add(annealedChemicallyAbraded_chkBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(32, 307, -1, -1));

        chemicallyPurifiedUPb_chkBox.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chemicallyPurifiedUPb_chkBox.setText("Chemically purified UPb");
        chemicallyPurifiedUPb_chkBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        archivingDetails_panel.add(chemicallyPurifiedUPb_chkBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(78, 342, -1, -1));

        fractionComment_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        fractionComment_label.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        fractionComment_label.setText("Comment:");
        archivingDetails_panel.add(fractionComment_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(166, 406, 93, -1));

        fractionComment_text.setColumns(20);
        fractionComment_text.setRows(5);
        jScrollPane3.setViewportView(fractionComment_text);

        archivingDetails_panel.add(jScrollPane3, new org.netbeans.lib.awtextra.AbsoluteConstraints(265, 406, 591, 123));

        countOfGrains_text.setEditable(false);
        countOfGrains_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        countOfGrains_text.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        countOfGrains_text.setText("0");
        archivingDetails_panel.add(countOfGrains_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(259, 193, 71, -1));

        publicationTimeStamp_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        publicationTimeStamp_label.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        publicationTimeStamp_label.setText("Analysis time stamp:");
        archivingDetails_panel.add(publicationTimeStamp_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(269, 552, 155, -1));

        publicationTimeStamp_text.setEditable(false);
        publicationTimeStamp_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        publicationTimeStamp_text.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        publicationTimeStamp_text.setText("time-stamp");
        archivingDetails_panel.add(publicationTimeStamp_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 547, 365, -1));

        selectImageFile_button.setForeground(new java.awt.Color(255, 51, 0));
        selectImageFile_button.setText("Browse to select fraction image");
        selectImageFile_button.setPreferredSize(new java.awt.Dimension(140, 23));
        selectImageFile_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectImageFile_buttonActionPerformed(evt);
            }
        });
        archivingDetails_panel.add(selectImageFile_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(755, 40, 262, 28));

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

        archivingDetails_panel.add(imageThumbnail_Panel, new org.netbeans.lib.awtextra.AbsoluteConstraints(755, 74, 262, 262));

        chooseSettingType_label1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chooseSettingType_label1.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        chooseSettingType_label1.setText("Thumbnail 256 x 256 for publication to Geochron:");
        archivingDetails_panel.add(chooseSettingType_label1, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 159, 319, -1));

        fractionID_label1.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        fractionID_label1.setText("Choose Fraction:");
        archivingDetails_panel.add(fractionID_label1, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 45, -1, -1));
        archivingDetails_panel.add(fraction_Chooser, new org.netbeans.lib.awtextra.AbsoluteConstraints(112, 40, 176, -1));

        fractionID_label2.setBackground(new java.awt.Color(255, 255, 255));
        fractionID_label2.setFont(new java.awt.Font("Arial", 3, 14)); // NOI18N
        fractionID_label2.setForeground(new java.awt.Color(153, 0, 0));
        fractionID_label2.setText("Note: This tab is also available within the Fraction Manager.");
        fractionID_label2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        fractionID_label2.setOpaque(true);
        archivingDetails_panel.add(fractionID_label2, new org.netbeans.lib.awtextra.AbsoluteConstraints(6, 9, 447, -1));

        useLabDefaults_button.setForeground(new java.awt.Color(255, 51, 0));
        useLabDefaults_button.setText("Use Lab Defaults for this fraction");
        useLabDefaults_button.setMargin(new java.awt.Insets(2, 1, 2, 1));
        useLabDefaults_button.setPreferredSize(new java.awt.Dimension(140, 23));
        useLabDefaults_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useLabDefaults_buttonActionPerformed(evt);
            }
        });
        archivingDetails_panel.add(useLabDefaults_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(59, 87, 353, 28));

        details_tabbedPane.addTab("Fraction Archiving Details", archivingDetails_panel);

        refsAndComment_panel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        instMethodRef_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        instMethodRef_label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        instMethodRef_label.setText(" Method Ref:");
        refsAndComment_panel.add(instMethodRef_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(17, 11, -1, 17));

        instMethodRef_text.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        instMethodRef_text.setText("reference");
        refsAndComment_panel.add(instMethodRef_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(95, 6, 969, -1));

        reference_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        reference_label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        reference_label.setText("Reference:");
        refsAndComment_panel.add(reference_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(27, 44, -1, -1));

        reference_text.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        reference_text.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        reference_text.setText("reference");
        refsAndComment_panel.add(reference_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(95, 38, 969, -1));

        comment_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        comment_label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        comment_label.setText("Comment:");
        refsAndComment_panel.add(comment_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(32, 70, -1, -1));

        comment_textArea.setColumns(20);
        comment_textArea.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        comment_textArea.setRows(5);
        comment_textArea.setText("Comment");
        jScrollPane1.setViewportView(comment_textArea);

        refsAndComment_panel.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(95, 70, 969, 63));

        calibrationUnct206_238_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        calibrationUnct206_238_label.setText("Calibration uncertainty for 206Pb/238U:");
        refsAndComment_panel.add(calibrationUnct206_238_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(125, 154, -1, 16));

        calibrationUnct208_232_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        calibrationUnct208_232_label.setText("Calibration uncertainty for 208Pb/232U:");
        refsAndComment_panel.add(calibrationUnct208_232_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(125, 186, -1, 16));

        calibrationUnct207_206_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        calibrationUnct207_206_label.setText("Calibration uncertainty for 207Pb/206Pb:");
        refsAndComment_panel.add(calibrationUnct207_206_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(119, 218, -1, 16));

        calibrationUnct207_206_text.setEditable(false);
        calibrationUnct207_206_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        calibrationUnct207_206_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        calibrationUnct207_206_text.setText("0.0");
        calibrationUnct207_206_text.setMinimumSize(new java.awt.Dimension(36, 19));
        calibrationUnct207_206_text.setPreferredSize(new java.awt.Dimension(36, 19));
        refsAndComment_panel.add(calibrationUnct207_206_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(368, 217, 100, 20));

        calibrationUnct208_232_text.setEditable(false);
        calibrationUnct208_232_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        calibrationUnct208_232_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        calibrationUnct208_232_text.setText("0.0");
        calibrationUnct208_232_text.setMinimumSize(new java.awt.Dimension(36, 19));
        calibrationUnct208_232_text.setPreferredSize(new java.awt.Dimension(36, 19));
        refsAndComment_panel.add(calibrationUnct208_232_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(368, 185, 100, 20));

        calibrationUnct206_238_text.setEditable(false);
        calibrationUnct206_238_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        calibrationUnct206_238_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        calibrationUnct206_238_text.setText("0.0");
        calibrationUnct206_238_text.setMinimumSize(new java.awt.Dimension(36, 19));
        calibrationUnct206_238_text.setPreferredSize(new java.awt.Dimension(36, 19));
        refsAndComment_panel.add(calibrationUnct206_238_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(368, 153, 100, 20));

        mineralStandardsPanel.setBackground(new java.awt.Color(202, 251, 220));
        mineralStandardsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Reference Materials Chooser"));
        mineralStandardsPanel.setOpaque(true);

        jLabel3.setForeground(new java.awt.Color(204, 0, 0));
        jLabel3.setText("(Models are specified in LabData)");
        mineralStandardsPanel.add(jLabel3);
        jLabel3.setBounds(60, 40, 260, 16);

        jLabel4.setForeground(new java.awt.Color(204, 0, 0));
        jLabel4.setText("Select the Reference Materials to include:");
        mineralStandardsPanel.add(jLabel4);
        jLabel4.setBounds(20, 20, 310, 16);

        refsAndComment_panel.add(mineralStandardsPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 141, 424, 341));

        comment_label1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        comment_label1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        comment_label1.setText("Measured Data Consistency Check:");
        refsAndComment_panel.add(comment_label1, new org.netbeans.lib.awtextra.AbsoluteConstraints(95, 274, -1, -1));

        jScrollPane2.setViewportView(measuredRatioValidity_JEditorPanel);

        refsAndComment_panel.add(jScrollPane2, new org.netbeans.lib.awtextra.AbsoluteConstraints(95, 294, 527, 188));

        keyWords_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        keyWords_label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        keyWords_label.setText("Key words for Geochron searches, separated by commas:");
        refsAndComment_panel.add(keyWords_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(95, 542, 331, -1));

        keyWordsCSV_text.setText("jTextField1");
        refsAndComment_panel.add(keyWordsCSV_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(432, 534, 632, -1));

        details_tabbedPane.addTab("References, Meta-Data, and Consistency Check", refsAndComment_panel);

        publishAliquot_panel.setBackground(new java.awt.Color(237, 233, 205));
        publishAliquot_panel.setOpaque(true);
        publishAliquot_panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                publishAliquot_panelMouseClicked(evt);
            }
        });
        publishAliquot_panel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        geochronIcon_label.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        geochronIcon_label.setOpaque(true);
        publishAliquot_panel.add(geochronIcon_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 0, 155, 70));

        visitGeochron_button.setText("Visit Geochron");
        visitGeochron_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                visitGeochron_buttonActionPerformed(evt);
            }
        });
        publishAliquot_panel.add(visitGeochron_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(470, 70, 155, -1));

        saveAndPreviewXMLAliquotAsHTML_button.setForeground(new java.awt.Color(255, 51, 0));
        saveAndPreviewXMLAliquotAsHTML_button.setText("<html><b>Preview </b>Aliquot in browser</html>");
        saveAndPreviewXMLAliquotAsHTML_button.setName("false"); // NOI18N
        saveAndPreviewXMLAliquotAsHTML_button.setPreferredSize(new java.awt.Dimension(140, 23));
        saveAndPreviewXMLAliquotAsHTML_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAndPreviewXMLAliquotAsHTML_buttonActionPerformed(evt);
            }
        });
        publishAliquot_panel.add(saveAndPreviewXMLAliquotAsHTML_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 110, 250, -1));

        checkMarkForValidSampleID_label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/earthtime/UPb_Redux/images/check_icon.png"))); // NOI18N
        checkMarkForValidSampleID_label.setToolTipText("Geochron Credentials are VALID.");
        checkMarkForValidSampleID_label.setIconTextGap(0);
        publishAliquot_panel.add(checkMarkForValidSampleID_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 140, 40, 40));

        xMarkForValidSampleID_label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/earthtime/UPb_Redux/images/icon_red_x.png"))); // NOI18N
        xMarkForValidSampleID_label.setToolTipText("Geochron Credentials are NOT valid.");
        publishAliquot_panel.add(xMarkForValidSampleID_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 140, 40, 40));

        sampleIsRegistered_label.setFont(new java.awt.Font("Arial Black", 0, 14)); // NOI18N
        sampleIsRegistered_label.setText("Sample for this aliquot is registered.");
        sampleIsRegistered_label.setName("Sample for this aliquot is"); // NOI18N
        publishAliquot_panel.add(sampleIsRegistered_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 150, 980, 30));

        geochronArchivePanel_panel.setBackground(new java.awt.Color(208, 222, 232));
        geochronArchivePanel_panel.setBorder(new javax.swing.border.MatteBorder(null));
        geochronArchivePanel_panel.setOpaque(true);
        geochronArchivePanel_panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                geochronArchivePanel_panelMouseClicked(evt);
            }
        });
        geochronArchivePanel_panel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel13.setFont(new java.awt.Font("Arial Black", 0, 14)); // NOI18N
        jLabel13.setText("3.");
        geochronArchivePanel_panel.add(jLabel13, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 247, 20, 30));

        saveAndUploadAliquotToGeochron_button.setForeground(new java.awt.Color(255, 51, 51));
        saveAndUploadAliquotToGeochron_button.setText("<html><b>Upload</b> aliquot to Geochron</html>");
        saveAndUploadAliquotToGeochron_button.setName("false"); // NOI18N
        saveAndUploadAliquotToGeochron_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAndUploadAliquotToGeochron_buttonActionPerformed(evt);
            }
        });
        geochronArchivePanel_panel.add(saveAndUploadAliquotToGeochron_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 250, 250, -1));

        chooseUploadOptions_label.setFont(new java.awt.Font("Arial Black", 0, 14)); // NOI18N
        chooseUploadOptions_label.setText("2.    Choose Upload Options:");
        geochronArchivePanel_panel.add(chooseUploadOptions_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 140, 230, 30));

        geochronPublicRecord_chkBox.setText("Public record ?");
        geochronArchivePanel_panel.add(geochronPublicRecord_chkBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 140, -1, -1));

        geochronOverwrite_chkBox.setText("Overwrite ?");
        geochronArchivePanel_panel.add(geochronOverwrite_chkBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(300, 160, -1, -1));

        geochronOverwrite_chkBox2.setText("T-W image?");
        geochronOverwrite_chkBox2.setEnabled(false);
        geochronArchivePanel_panel.add(geochronOverwrite_chkBox2, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 160, 170, -1));

        concordiaUpload_chkBox.setText("concordia image?");
        concordiaUpload_chkBox.setName("CONCORDIA"); // NOI18N
        geochronArchivePanel_panel.add(concordiaUpload_chkBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 140, 190, -1));

        geochronOverwrite_chkBox5.setText("weighted means image?");
        geochronOverwrite_chkBox5.setEnabled(false);
        geochronArchivePanel_panel.add(geochronOverwrite_chkBox5, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 140, 240, -1));

        probabilityDensityUpload_chkBox.setText("probability density image?");
        geochronArchivePanel_panel.add(probabilityDensityUpload_chkBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(640, 160, 200, -1));

        reportAsCSV_chkBox.setText("report as '.csv'?");
        geochronArchivePanel_panel.add(reportAsCSV_chkBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 160, 130, -1));

        reportSVGUpload_chkbox.setText("report image?");
        geochronArchivePanel_panel.add(reportSVGUpload_chkbox, new org.netbeans.lib.awtextra.AbsoluteConstraints(880, 140, 130, -1));

        geoPassPassword_passwordField.setText("############");
        geochronArchivePanel_panel.add(geoPassPassword_passwordField, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 100, -1, -1));

        passwordGeochron_label1.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        passwordGeochron_label1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        passwordGeochron_label1.setText("password:");
        geochronArchivePanel_panel.add(passwordGeochron_label1, new org.netbeans.lib.awtextra.AbsoluteConstraints(600, 100, 70, 30));

        geoPassUserName_text.setText("username");
        geochronArchivePanel_panel.add(geoPassUserName_text, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 100, 150, -1));

        userNameGeochron_label.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        userNameGeochron_label.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        userNameGeochron_label.setText("user name:");
        geochronArchivePanel_panel.add(userNameGeochron_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(360, 100, 80, 30));

        validateGeoPassID_button.setForeground(new java.awt.Color(255, 51, 51));
        validateGeoPassID_button.setText("<html><b>Validate</b> GeoPass ID</html>");
        validateGeoPassID_button.setName("false"); // NOI18N
        validateGeoPassID_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                validateGeoPassID_buttonActionPerformed(evt);
            }
        });
        geochronArchivePanel_panel.add(validateGeoPassID_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 100, 250, -1));

        jLabel8.setFont(new java.awt.Font("Arial Black", 0, 14)); // NOI18N
        jLabel8.setText("1.");
        geochronArchivePanel_panel.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(70, 97, 20, 30));

        checkMarkForValidGeoPassID_label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/earthtime/UPb_Redux/images/check_icon.png"))); // NOI18N
        checkMarkForValidGeoPassID_label.setToolTipText("Geochron Credentials are VALID.");
        checkMarkForValidGeoPassID_label.setIconTextGap(0);
        geochronArchivePanel_panel.add(checkMarkForValidGeoPassID_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, 40, 40));

        xMarkForValidGeoPassID_label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/earthtime/UPb_Redux/images/icon_red_x.png"))); // NOI18N
        xMarkForValidGeoPassID_label.setToolTipText("Geochron Credentials are NOT valid.");
        geochronArchivePanel_panel.add(xMarkForValidGeoPassID_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 90, 40, 40));

        archiveNote_label.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        archiveNote_label.setText("Note:");
        geochronArchivePanel_panel.add(archiveNote_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(30, 50, 920, -1));

        archivePanelTitle_label.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        archivePanelTitle_label.setText("To Archive this aliquot to the Geochron database:");
        geochronArchivePanel_panel.add(archivePanelTitle_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, 470, -1));

        publishAliquot_panel.add(geochronArchivePanel_panel, new org.netbeans.lib.awtextra.AbsoluteConstraints(40, 230, 1040, 310));

        archivePanelTitle_label1.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        archivePanelTitle_label1.setText("Please enter the registered sample identifier by using the sample manager.");
        publishAliquot_panel.add(archivePanelTitle_label1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 240, 710, -1));

        aliquotIsRegistered_label.setFont(new java.awt.Font("Arial Black", 0, 14)); // NOI18N
        aliquotIsRegistered_label.setText("Aliquot is not registered as child of Sample IGSN.");
        aliquotIsRegistered_label.setName("Aliquot is"); // NOI18N
        publishAliquot_panel.add(aliquotIsRegistered_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 190, 980, 30));

        checkMarkForValidAliquotIGSN_label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/earthtime/UPb_Redux/images/check_icon.png"))); // NOI18N
        checkMarkForValidAliquotIGSN_label.setToolTipText("Geochron Credentials are VALID.");
        checkMarkForValidAliquotIGSN_label.setIconTextGap(0);
        publishAliquot_panel.add(checkMarkForValidAliquotIGSN_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 180, 40, 40));

        xMarkForValidAliquotIGSN_label.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/earthtime/UPb_Redux/images/icon_red_x.png"))); // NOI18N
        xMarkForValidAliquotIGSN_label.setToolTipText("Geochron Credentials are NOT valid.");
        publishAliquot_panel.add(xMarkForValidAliquotIGSN_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 180, 40, 40));

        details_tabbedPane.addTab("Archive Aliquot to Database", publishAliquot_panel);

        getContentPane().add(details_tabbedPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 48, 1125, 646));

        buttonsPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        buttonsPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        saveAndClose_button.setForeground(new java.awt.Color(255, 51, 0));
        saveAndClose_button.setText("Save & Close");
        saveAndClose_button.setMargin(new java.awt.Insets(2, 1, 2, 1));
        saveAndClose_button.setPreferredSize(new java.awt.Dimension(140, 23));
        saveAndClose_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAndClose_buttonActionPerformed(evt);
            }
        });
        buttonsPanel.add(saveAndClose_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 2, 129, 25));

        save_button.setForeground(new java.awt.Color(255, 51, 0));
        save_button.setText("Save");
        save_button.setMargin(new java.awt.Insets(2, 1, 2, 1));
        save_button.setPreferredSize(new java.awt.Dimension(140, 23));
        save_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                save_buttonActionPerformed(evt);
            }
        });
        buttonsPanel.add(save_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(140, 2, 90, 25));

        restore_button.setForeground(new java.awt.Color(255, 51, 0));
        restore_button.setText("Revert to Saved");
        restore_button.setMargin(new java.awt.Insets(2, 1, 2, 1));
        restore_button.setPreferredSize(new java.awt.Dimension(140, 23));
        restore_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                restore_buttonActionPerformed(evt);
            }
        });
        buttonsPanel.add(restore_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 2, 181, 25));

        close_button.setForeground(new java.awt.Color(255, 51, 0));
        close_button.setText("Close");
        close_button.setMargin(new java.awt.Insets(2, 1, 2, 1));
        close_button.setPreferredSize(new java.awt.Dimension(140, 23));
        close_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                close_buttonActionPerformed(evt);
            }
        });
        buttonsPanel.add(close_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 2, 90, 25));

        exportXMLAliquot_button.setForeground(new java.awt.Color(255, 51, 0));
        exportXMLAliquot_button.setText("Save and Export Aliquot as local XML file");
        exportXMLAliquot_button.setMargin(new java.awt.Insets(2, 1, 2, 1));
        exportXMLAliquot_button.setPreferredSize(new java.awt.Dimension(140, 23));
        exportXMLAliquot_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportXMLAliquot_buttonActionPerformed(evt);
            }
        });
        buttonsPanel.add(exportXMLAliquot_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(520, 2, 338, 25));

        getContentPane().add(buttonsPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 700, -1, -1));

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void saveAndPreviewXMLAliquotAsHTML_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAndPreviewXMLAliquotAsHTML_buttonActionPerformed
        saveAliquot();
        // save off the Aliquot as a temp file
        String tempAliquotXML = "TempAliquot.xml";
        ((UPbReduxAliquot) getMyAliquot()).serializeXMLObject(tempAliquotXML);
        viewXMLAliquotAsHTML(tempAliquotXML);
    }//GEN-LAST:event_saveAndPreviewXMLAliquotAsHTML_buttonActionPerformed

    private void exportXMLAliquot_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportXMLAliquot_buttonActionPerformed
        saveAliquot();
        exportAliquotToXML();
    }//GEN-LAST:event_exportXMLAliquot_buttonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        setVisible(false);
        //checkIsSavedStatusOfAliquotEdit();
        close();
    }//GEN-LAST:event_formWindowClosing

    private void close_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_close_buttonActionPerformed
        close();
    }//GEN-LAST:event_close_buttonActionPerformed

    private void restore_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_restore_buttonActionPerformed
        // TODO add your handling code here:
        initAliquot();//showSavedDataII();
    }//GEN-LAST:event_restore_buttonActionPerformed

    private void saveAndClose_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAndClose_buttonActionPerformed
        saveAliquot();
        close();
}//GEN-LAST:event_saveAndClose_buttonActionPerformed

    private void save_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_save_buttonActionPerformed
        saveAliquot();
}//GEN-LAST:event_save_buttonActionPerformed

    private void validateGeoPassID_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_validateGeoPassID_buttonActionPerformed
        validateGeoPassID(false);
}//GEN-LAST:event_validateGeoPassID_buttonActionPerformed

    private void visitGeochron_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_visitGeochron_buttonActionPerformed
        BrowserControl.displayURL("https://www.geochron.org/");
}//GEN-LAST:event_visitGeochron_buttonActionPerformed

    private void saveAndUploadAliquotToGeochron_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAndUploadAliquotToGeochron_buttonActionPerformed
        saveAliquot();
        try {
            uploadAliquotToGeochronZip();
        } catch (ETException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
        showArchiveNote(myAliquot, true);
}//GEN-LAST:event_saveAndUploadAliquotToGeochron_buttonActionPerformed

private void details_tabbedPaneMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_details_tabbedPaneMouseClicked
    if (details_tabbedPane.getSelectedIndex() == 3) {
        // we are at publish/archive
        System.out.println("ARCHIVE CHECK");
        validateGeoPassID(false);
    } else {
    }
}//GEN-LAST:event_details_tabbedPaneMouseClicked

private void geochronArchivePanel_panelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_geochronArchivePanel_panelMouseClicked
    // do nothing on purpose
}//GEN-LAST:event_geochronArchivePanel_panelMouseClicked

private void publishAliquot_panelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_publishAliquot_panelMouseClicked
    // do nothing on purpose
}//GEN-LAST:event_publishAliquot_panelMouseClicked

    private void selectImageFile_buttonActionPerformed ( java.awt.event.ActionEvent evt ) {//GEN-FIRST:event_selectImageFile_buttonActionPerformed
        try {
            selectImageFile(myFraction);
        } catch (BadLabDataException badLabDataException) {
        }
    }//GEN-LAST:event_selectImageFile_buttonActionPerformed

    private void useLabDefaults_buttonActionPerformed ( java.awt.event.ActionEvent evt ) {//GEN-FIRST:event_useLabDefaults_buttonActionPerformed
        showLabDefaultFractionArchivingData((ETFractionInterface) fraction_Chooser.getSelectedItem());
    }//GEN-LAST:event_useLabDefaults_buttonActionPerformed

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
     * @return
     */
    public SampleInterface getSample() {
        return sample;
    }

    /**
     *
     * @param sample
     */
    public void setSample(SampleInterface sample) {
        this.sample = sample;

    }

    private class EditFractionListener implements ActionListener {

        private int row;
        private ETFractionInterface fraction;

        public EditFractionListener(ETFractionInterface fraction, int row) {
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
                saveFractionArchivingData(fraction);
                parent.editFraction(getFraction(), 8);
                updateFractionRow(
                        fraction, getMyAliquot().getAliquotFractions().indexOf(fraction));
            }

        }

        /**
         * @return the fraction
         */
        public ETFractionInterface getFraction() {
            return fraction;
        }
    }

    private class DeleteFractionListener implements ActionListener {

        private int row;
        private FractionI fraction;

        public DeleteFractionListener(FractionI fraction, int row) {
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

    private class ChangeTracerListener implements ActionListener {

        private int row;
        private ETFractionInterface fraction;

        public ChangeTracerListener(ETFractionInterface fraction, int row) {
            this.row = row;
            this.fraction = fraction;
        }

        public ETFractionInterface getFraction() {
            return fraction;
        }

        public void actionPerformed(ActionEvent e) {

            try {
                String chosenTracerType
                        = ((TracerUPbModel) ((AbstractRatiosDataModel) ((UPbFraction) fraction).getMyLabData().//
                        getATracerModel((String) fractionTracerChoice.get(row).getSelectedItem()))).getTracerType();

                updateAlphaPbModelChooserForRow(fraction, ((UPbFraction) fraction).needsAlphaPbModel(chosenTracerType), row);
                updateAlphaUModelChooserForRow(fraction, ((UPbFraction) fraction).needsAlphaUModel(chosenTracerType), row);

                // aug 2010 zero tracer mass if tracer none is isZircon
                if (chosenTracerType.equalsIgnoreCase(DataDictionary.TracerType[0])) {
                    // none tracer chosen
                    ((JTextComponent) fractionTracerMassText.get(row)).setText("0.0");
                }
            } catch (BadLabDataException ex) {
                new ETWarningDialog(ex).setVisible(true);
            }

        }
    }

    private class ChangeIsZirconListener implements ChangeListener {

        private int row;
        private ETFractionInterface fraction;

        public ChangeIsZirconListener(ETFractionInterface fraction, int row) {
            this.row = row;
            this.fraction = fraction;
        }

        public ETFractionInterface getFraction() {
            return fraction;
        }

        public void stateChanged(ChangeEvent e) {
            AbstractButton abstractButton = (AbstractButton) e.getSource();
            boolean isZircon = abstractButton.getModel().isSelected();

            updateInitialPbModelChooserForRow(fraction, isZircon, row);
        }
    }

// aug 2010
    private class ChangeInitialPbModelItemListener implements ItemListener {

        private int row;
        private ETFractionInterface fraction;

        public ChangeInitialPbModelItemListener(ETFractionInterface fraction, int row) {
            this.row = row;
            this.fraction = fraction;
        }

        public ETFractionInterface getFraction() {
            return fraction;
        }

        public void itemStateChanged(ItemEvent e) {
            try {
                boolean isStaceyKramersModel = ((UPbFraction) fraction).getMyLabData().getAnInitialPbModel(((String) e.getItem())) instanceof StaceyKramersInitialPbModelET;
                fractionEstDateText.get(row).setEnabled(isStaceyKramersModel);
                fractionStaceyKramersPctUncertaintyText.get(row).setEnabled(isStaceyKramersModel);
                fractionStaceyKramersCorrelationCoeffsText.get(row).setEnabled(isStaceyKramersModel);
            } catch (BadLabDataException ex) {
                new ETWarningDialog(ex).setVisible(true);
            }
        }
    }

    /**
     *
     */
    protected void initAliquot() {

        fastEdits_panel.removeAll();

        deletedFractions = new Vector<>();
        addedFractions = new Vector<>();

        // in compilation, leave everything blank
        if (((ReduxAliquotInterface) myAliquot).isCompiled()) {
            return;
        }

//        fractionDeleteButtons =
//                new ArrayList<JComponent>();
        fractionEditButtons = new ArrayList<>();

        fractionZirconCheckBox = new ArrayList<>();

        fractionTracerChoice = new ArrayList<>();

        fractionTracerMassText = new ArrayList<>();

        fractionTracerGRAMS = new ArrayList<>();

        fractionMassText = new ArrayList<>();

        fractionMassGRAMS = new ArrayList<>();

        fractionAlphaPbChoice = new ArrayList<>();

        fractionAlphaUChoice = new ArrayList<>();

        fractionPbBlankChoice = new ArrayList<>();

        fractionInitialPbChoice = new ArrayList<>();

        fractionEstDateText = new ArrayList<>();

        fractionEstDateMEGAANNUM = new ArrayList<>();

        fractionStaceyKramersPctUncertaintyText = new ArrayList<>();

        fractionStaceyKramersCorrelationCoeffsText = new ArrayList<>();

        fractionPbBlankMassText
                = new ArrayList<>();
        fractionPbBlankPICOGRAMS = new ArrayList<>();

        fractionUBlankMassText = new ArrayList<>();

        fractionUBlankPICOGRAMS = new ArrayList<>();

        fractionR238_235sText = new ArrayList<>();

        fractionR238_235bText = new ArrayList<>();

        fractionR18O_16OText = new ArrayList<>();

        fractionRTh_UmagmaText = new ArrayList<>();

        fractionAR231_235sampleText = new ArrayList<>();

        fractionTracerMassOneSigmaText = new ArrayList<>();
        fractionUBlankMassOneSigmaText = new ArrayList<>();
        fractionR238_235sOneSigmaText = new ArrayList<>();
        fractionR238_235bOneSigmaText = new ArrayList<>();
        fractionR18O_16OOneSigmaText = new ArrayList<>();
        fractionPbBlankMassOneSigmaText = new ArrayList<>();
        fractionRTh_UmagmaOneSigmaText = new ArrayList<>();
        fractionAr231_235sampleOneSigmaText = new ArrayList<>();

        // create master row for filling others ********************************
        // new fraction namer
//        masterNewFractionName =
//                new JTextField();
//        masterNewFractionName.setDocument(new UnDoAbleDocument(masterNewFractionName, true));
//        masterNewFractionName.setText("New Fraction");
//
//        // new fraction creator button
//        masterNewFractionNameAdder =
//                new EditFractionButton("ADD", -1, true);
//        masterNewFractionNameAdder.setToolTipText("Click to ADD new Fraction");
//        masterNewFractionNameAdder.addActionListener(new ActionListener() {
//
//            public void actionPerformed(ActionEvent arg0) {
//                if (masterNewFractionName.getText().length() > 0) {
//
//                    Fraction addedFraction = null;
//
//                    // check to see if fractionid is in use
//                    // first create a list of used fractionids so that we
//                    // can tell user if new fraction name is already in use
//                    Vector<String> fractionIDs = new Vector<String>();
//
//                    for (int f = 0;
//                            f < getSample().getFractions().size();
//                            f++) {
//                        fractionIDs.add(getSample().getFractions().get(f).getFractionID());
//                    }
//
//                    // add pending new fractions
//                    for (int f = 0; f
//                            < addedFractions.size(); f++) {
//                        fractionIDs.add(addedFractions.get(f).getFractionID());
//                    }
//
//                    // remove pending deleted fractions
//                    for (int f = 0; f
//                            < deletedFractions.size(); f++) {
//                        fractionIDs.remove(deletedFractions.get(f).getFractionID());
//                    }
//
//                    Collections.sort(fractionIDs, new IntuitiveStringComparator<String>());//String.CASE_INSENSITIVE_ORDER );
//                    //Arrays.sort(fractionIDs, String.CASE_INSENSITIVE_ORDER);
//
//                    int index = Collections.binarySearch(fractionIDs, masterNewFractionName.getText());
//                    if (index >= 0) {
//                        JOptionPane.showMessageDialog(
//                                null,
//                                new String[]{"Duplicate Fraction ID, please use another."},
//                                "ET Redux Warning",
//                                JOptionPane.WARNING_MESSAGE);
//
//                    } else {
//                        // prepare fields
//                        try {
//                            addedFraction = new UPbFraction("NONE");
//                            ((UPbFraction) addedFraction).setSampleName(getSample().getSampleName());
//                            ((UPbFraction) addedFraction).setAliquotNumber(getMyAliquot().getAliquotNumber());
//                            ((UPbFraction) addedFraction).setFractionID(masterNewFractionName.getText().trim());
//                            ((UPbFraction) addedFraction).setGrainID(masterNewFractionName.getText().trim());
//
//                            ReduxLabData labData = getMyAliquot().getMyReduxLabData();
//                            ((UPbFraction) addedFraction).setMyLabData(labData);
//                            ((UPbFraction) addedFraction)//
//                                    .setTracer(labData.getATracerModel((String) masterTracerChooser.getSelectedItem()));
//                            ((UPbFraction) addedFraction)//
//                                    .setAlphaPbModel(labData.getAnAlphaPbModel((String) masterAlphaPbChooser.getSelectedItem()));//      labData.getDefaultLabAlphaPbModel() );
//                            ((UPbFraction) addedFraction)//
//                                    .setAlphaUModel(labData.getAnAlphaUModel((String) masterAlphaUChooser.getSelectedItem()));// getDefaultLabAlphaUModel() );
//                            ((UPbFraction) addedFraction)//
//                                    .setPbBlank(labData.getAPbBlankModel((String) masterPbBlankChooser.getSelectedItem()));
//                            ((UPbFraction) addedFraction)//
//                                    .setInitialPbModel(labData.getAnInitialPbModel((String) masterInitialPbModelChooser.getSelectedItem()).copy());
//                            ((UPbFraction) addedFraction)//
//                                    .setPhysicalConstantsModel(getSample().getPhysicalConstantsModel());
//
//                        } catch (BadLabDataException badLabDataException) {
//                        }
//
//                        addedFraction.setEstimatedDate(new BigDecimal(masterEstimatedDate.getText(), ReduxConstants.mathContext15));
//
//                        ((UPbFraction) addedFraction).//
//                                setStaceyKramersOnePctUnct(new BigDecimal(masterStaceyKramersPctUncertainty.getText(), ReduxConstants.mathContext15));
//
//                        ((UPbFraction) addedFraction).//
//                                setStaceyKramersCorrelationCoeffs(new BigDecimal(masterStaceyKramersCorrelationCoeffs.getText(), ReduxConstants.mathContext15));
//
//                        addedFraction.getAnalysisMeasure(AnalysisMeasures.tracerMassInGrams.getName()).copyValuesFrom(new ValueModel(
//                                AnalysisMeasures.tracerMassInGrams.getName(),
//                                new BigDecimal(masterTracerMass.getText(), ReduxConstants.mathContext15),
//                                "ABS",
//                                new BigDecimal(masterTracerMassOneSigma.getText(), ReduxConstants.mathContext15)));
//
//                        addedFraction.getAnalysisMeasure(AnalysisMeasures.fractionMass.getName()).copyValuesFrom(new ValueModel(
//                                AnalysisMeasures.fractionMass.getName(),
//                                new BigDecimal(masterFractionMass.getText(), ReduxConstants.mathContext15),
//                                "ABS",
//                                BigDecimal.ZERO));
//
//                        addedFraction.getAnalysisMeasure(AnalysisMeasures.uBlankMassInGrams.getName()).copyValuesFrom(new ValueModel(
//                                AnalysisMeasures.uBlankMassInGrams.getName(),
//                                new BigDecimal(masterUBlankMass.getText(), ReduxConstants.mathContext15).movePointLeft(12),
//                                "ABS",
//                                new BigDecimal(masterUBlankMassOneSigma.getText(), ReduxConstants.mathContext15).movePointLeft(12)));
//
//                        addedFraction.getAnalysisMeasure(AnalysisMeasures.pbBlankMassInGrams.getName()).copyValuesFrom(new ValueModel(
//                                AnalysisMeasures.pbBlankMassInGrams.getName(),
//                                new BigDecimal(masterPbBlankMass.getText(), ReduxConstants.mathContext15).movePointLeft(12),
//                                "ABS",
//                                new BigDecimal(masterPbBlankMassOneSigma.getText(), ReduxConstants.mathContext15).movePointLeft(12)));
//
//                        addedFraction.getAnalysisMeasure(AnalysisMeasures.r238_235b.getName()).copyValuesFrom(new ValueModel(
//                                AnalysisMeasures.r238_235b.getName(),
//                                new BigDecimal(masterR238_235b.getText(), ReduxConstants.mathContext15),
//                                "ABS",
//                                new BigDecimal(masterR238_235bOneSigma.getText(), ReduxConstants.mathContext15)));
//
//                        addedFraction.getAnalysisMeasure(AnalysisMeasures.r238_235s.getName()).copyValuesFrom(new ValueModel(
//                                AnalysisMeasures.r238_235s.getName(),
//                                new BigDecimal(masterR238_235s.getText(), ReduxConstants.mathContext15),
//                                "ABS",
//                                new BigDecimal(masterR238_235sOneSigma.getText(), ReduxConstants.mathContext15)));
//
//                        addedFraction.getAnalysisMeasure(AnalysisMeasures.r18O_16O_revised.getName()).copyValuesFrom(new ValueModel(
//                                AnalysisMeasures.r18O_16O_revised.getName(),
//                                new BigDecimal(masterR18O_16O.getText(), ReduxConstants.mathContext15),
//                                "ABS",
//                                new BigDecimal(masterR18O_16OOneSigma.getText(), ReduxConstants.mathContext15)));
//
//                        addedFraction.getAnalysisMeasure(AnalysisMeasures.rTh_Umagma.getName()).copyValuesFrom(new ValueModel(
//                                AnalysisMeasures.rTh_Umagma.getName(),
//                                new BigDecimal(masterRTh_Umagma.getText(), ReduxConstants.mathContext15),
//                                "ABS",
//                                new BigDecimal(masterRTh_UmagmaOneSigma.getText(), ReduxConstants.mathContext15)));
//
//                        addedFraction.getAnalysisMeasure(AnalysisMeasures.ar231_235sample.getName()).copyValuesFrom(new ValueModel(
//                                AnalysisMeasures.ar231_235sample.getName(),
//                                new BigDecimal(masterAr231_235sample.getText(), ReduxConstants.mathContext15),
//                                "ABS",
//                                new BigDecimal(masterAr231_235sampleOneSigma.getText(), ReduxConstants.mathContext15)));
//
//
//
//                        addedFractions.add(addedFraction);
//                        addNewFractionRow(addedFraction);
//                    }
//
//                }
//            }
//        });
        // Zircon checkbox
        masterZirconCaseCheckBox = new JCheckBox("NO I-Pb", false);
        masterZirconCaseCheckBox.setOpaque(false);
        masterZirconCaseCheckBox.setFont(new Font("Monospaced", Font.BOLD, 10));

        // Zircon checkbox filler button
        masterZironCaseFiller = new EditFractionButton("FILL", -1, true);
        masterZironCaseFiller.setMargin(new Insets(0, 0, 0, 0));

        masterZironCaseFiller.setToolTipText("Click to FILL Column");
        masterZironCaseFiller.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int f = 0; f
                        < getMyAliquot().getAliquotFractions().size(); f++) {
                    ((JCheckBox) fractionZirconCheckBox.get(f)).setSelected(masterZirconCaseCheckBox.isSelected());
                }

            }
        });

        //Tracer chooser
        masterTracerChooser = new JComboBox<>();
        masterTracerChooser.setFont(dropDownFont);
        masterTracerChooser.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                String tracerName = (String) masterTracerChooser.getSelectedItem();
                masterTracerChooser.setToolTipText(tracerName);
                try {
                    AbstractRatiosDataModel tracer = getMyAliquot().getMyReduxLabData().getATracerModel(tracerName);
                    masterTracerChooser.setToolTipText(ReduxLabData.toolTipForTracer(tracer));
                } catch (BadLabDataException ex) {
                    new ETWarningDialog(ex).setVisible(true);
                }
            }
        });

        ArrayList<AbstractRatiosDataModel> mtracers = getMyAliquot().getMyReduxLabData().getTracers();
        for (int i = (mtracers.size() > 1 ? 1 : 0); i < mtracers.size(); i++) {
            masterTracerChooser.addItem(mtracers.get(i).getReduxLabDataElementName());
        }

        // Tracer choosers filler button
        masterTracerFiller = new EditFractionButton("FILL", -1, true);
        masterTracerFiller.setToolTipText("Click to FILL Column");
        masterTracerFiller.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int f = 0; f
                        < getMyAliquot().getAliquotFractions().size(); f++) {
                    // check for enabled
                    if (fractionTracerChoice.get(f).isEnabled()) {
                        fractionTracerChoice.get(f)//
                                .setSelectedIndex(masterTracerChooser.getSelectedIndex() + 1);// dec 2011 shift of one since master does not show "none" tracer
                    }

                }
            }
        });

        // Tracer mass
        masterTracerMass
                = new JTextField();
        masterTracerMass.setDocument(new BigDecimalDocument(masterTracerMass, true));
        masterTracerMass.setText("0.0000");
        masterTracerMass.setHorizontalAlignment(JTextField.RIGHT);

        // Tracer mass filler button
        masterTracerMassFiller
                = new EditFractionButton("FILL", -1, true);
        masterTracerMassFiller.setToolTipText("Click to FILL Column");
        masterTracerMassFiller.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (int f = 0; f
                        < getMyAliquot().getAliquotFractions().size(); f++) {
                    if (fractionTracerMassText.get(f).isEnabled()) {
                        ((JTextComponent) fractionTracerMassText.get(f)).setText(masterTracerMass.getText());
                    }
                }

            }
        });

        // Fraction mass
        masterFractionMass
                = new JTextField();
        masterFractionMass.setDocument(new BigDecimalDocument(masterFractionMass, true));
        masterFractionMass.setText("0.0000");
        masterFractionMass.setHorizontalAlignment(JTextField.RIGHT);

        // Fraction mass filler button
        masterFractionMassFiller
                = new EditFractionButton("FILL", -1, true);
        masterFractionMassFiller.setToolTipText("Click to FILL Column");
        masterFractionMassFiller.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (int f = 0; f
                        < getMyAliquot().getAliquotFractions().size(); f++) {
                    if (fractionMassText.get(f).isEnabled()) {
                        ((JTextComponent) fractionMassText.get(f)).setText(masterFractionMass.getText());
                    }
                }

            }
        });

        // alphaPb chooser
        masterAlphaPbChooser = new JComboBox<>();
        masterAlphaPbChooser.setFont(dropDownFont);
        masterAlphaPbChooser.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                String alphaPbModelName = (String) masterAlphaPbChooser.getSelectedItem();
                masterAlphaPbChooser.setToolTipText(alphaPbModelName);
                try {
                    ValueModel alphaPb = getMyAliquot().getMyReduxLabData().getAnAlphaPbModel(alphaPbModelName);
                    masterAlphaPbChooser.setToolTipText(ReduxLabData.toolTipForAlphaPbModel(alphaPb));
                } catch (BadLabDataException ex) {
                    new ETWarningDialog(ex).setVisible(true);
                }
            }
        });

        ArrayList<ValueModel> myAlphaPbModels = getMyAliquot().getMyReduxLabData().getAlphaPbModels();
        for (int i = 1; i < myAlphaPbModels.size(); i++) {
            masterAlphaPbChooser.addItem(((ValueModel) myAlphaPbModels.get(i)).getName());
        }

        // alphaPb chooser filler button
        masterAlphaPbFiller = new EditFractionButton("FILL", -1, true);
        masterAlphaPbFiller.setToolTipText("Click to FILL Column");
        masterAlphaPbFiller.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (int f = 0; f
                        < getMyAliquot().getAliquotFractions().size(); f++) {
                    fractionAlphaPbChoice.get(f)//
                            .setSelectedIndex(masterAlphaPbChooser.getSelectedIndex());
                }
            }
        });

        // alphaU chooser
        masterAlphaUChooser = new JComboBox<>();
        masterAlphaUChooser.setFont(dropDownFont);
        masterAlphaUChooser.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                String alphaUModelName = (String) masterAlphaUChooser.getSelectedItem();
                masterAlphaUChooser.setToolTipText(alphaUModelName);
                try {
                    ValueModel alphaU = getMyAliquot().getMyReduxLabData().getAnAlphaUModel(alphaUModelName);
                    masterAlphaUChooser.setToolTipText(ReduxLabData.toolTipForAlphaUModel(alphaU));
                } catch (BadLabDataException ex) {
                    new ETWarningDialog(ex).setVisible(true);
                }
            }
        });

        ArrayList<ValueModel> myAlphaUModels = getMyAliquot().getMyReduxLabData().getAlphaUModels();
        for (int i = 1; i < myAlphaUModels.size(); i++) {
            masterAlphaUChooser.addItem(((ValueModel) myAlphaUModels.get(i)).getName());
        }

        // alphaU chooser filler button
        masterAlphaUFiller = new EditFractionButton("FILL", -1, true);
        masterAlphaUFiller.setToolTipText("Click to FILL Column");
        masterAlphaUFiller.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (int f = 0; f
                        < getMyAliquot().getAliquotFractions().size(); f++) {
                    if (fractionAlphaUChoice.get(f).isEnabled()) {
                        fractionAlphaUChoice.get(f)//
                                .setSelectedIndex(masterAlphaUChooser.getSelectedIndex());
                    }
                }
            }
        });

        // PbBlank chooser
        masterPbBlankChooser = new JComboBox<>();
        masterPbBlankChooser.setFont(dropDownFont);
        masterPbBlankChooser.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                JComboBox comboBox = (JComboBox) e.getSource();
                String blankName = (String) comboBox.getSelectedItem();
                comboBox.setToolTipText(blankName);
//                    try {
//                        Tracer tracer = getMyAliquot().getMyReduxLabData().getATracerModel( tracerName );
//                        comboBox.setToolTipText( ReduxLabData.toolTipForTracer( tracer ) );
//                    } catch (BadLabDataException badLabDataException) {
//                    }
            }
        });

        ArrayList<AbstractRatiosDataModel> myPbBlanks = getMyAliquot().getMyReduxLabData().getPbBlanks();
        for (int i = (myPbBlanks.size() > 1 ? 1 : 0); i < myPbBlanks.size(); i++) {
            masterPbBlankChooser.addItem(((AbstractRatiosDataModel) myPbBlanks.get(i)).getReduxLabDataElementName());
        }

        // PbBlank choosers filler button
        masterPbBlankFiller = new EditFractionButton("FILL", -1, true);
        masterPbBlankFiller.setToolTipText("Click to FILL Column");
        masterPbBlankFiller.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (int f = 0; f
                        < getMyAliquot().getAliquotFractions().size(); f++) {
                    fractionPbBlankChoice.get(f)//
                            .setSelectedIndex(masterPbBlankChooser.getSelectedIndex());
                }
            }
        });

        // InitialPbModel chooser
        masterInitialPbModelChooser = new JComboBox<>();
        masterInitialPbModelChooser.setFont(dropDownFont);
        masterInitialPbModelChooser.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                JComboBox comboBox = (JComboBox) e.getSource();
                String initialPBName = (String) comboBox.getSelectedItem();
                comboBox.setToolTipText(initialPBName);
            }
        });

        ArrayList<AbstractRatiosDataModel> initialPbModels = getMyAliquot().getMyReduxLabData().getInitialPbModels();
        for (int i = (initialPbModels.size() > 1 ? 1 : 0); i < initialPbModels.size(); i++) {
//            if (  ! (initialPbModels.get( i ) instanceof StaceyKramersInitialPbModelET) ) {
            masterInitialPbModelChooser.addItem(initialPbModels.get(i).getReduxLabDataElementName());
//            }
        }

        // InitialPbModel choosers filler button
        masterInitialPbModelFiller = new EditFractionButton("FILL", -1, true);
        masterInitialPbModelFiller.setToolTipText("Click to FILL Column");
        masterInitialPbModelFiller.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int f = 0; f
                        < getMyAliquot().getAliquotFractions().size(); f++) {
                    if (fractionInitialPbChoice.get(f).isEnabled()) {
                        fractionInitialPbChoice.get(f)//
                                .setSelectedIndex(masterInitialPbModelChooser.getSelectedIndex());
                    }
                }

            }
        });

        // Estimated Date
        masterEstimatedDate = new JTextField();
        masterEstimatedDate.setDocument(new BigDecimalDocument(masterEstimatedDate, true));
        masterEstimatedDate.setText("0000.0");
        masterEstimatedDate.setHorizontalAlignment(JTextField.RIGHT);

        // Estimated Date filler button
        masterEstimatedDateFiller
                = new EditFractionButton("FILL", -1, true);
        masterEstimatedDateFiller.setToolTipText("Click to FILL Column");
        masterEstimatedDateFiller.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (int f = 0; f
                        < getMyAliquot().getAliquotFractions().size(); f++) {
                    if (((JTextField) fractionEstDateText.get(f)).isEnabled()) {
                        ((JTextField) fractionEstDateText.get(f)).setText(masterEstimatedDate.getText());
                    }
                }

            }
        });

        // StaceyKramersPctUncertainty
        masterStaceyKramersPctUncertainty
                = new JTextField();
        masterStaceyKramersPctUncertainty.setDocument(//
                new BigDecimalDocument(masterStaceyKramersPctUncertainty, true));
        masterStaceyKramersPctUncertainty.setText("0.0000");
        masterStaceyKramersPctUncertainty.setHorizontalAlignment(JTextField.RIGHT);

        // StaceyKramersPctUncertainty filler button
        masterStaceyKramersPctUncertaintyFiller
                = new EditFractionButton("FILL", -1, true);
        masterStaceyKramersPctUncertaintyFiller.setToolTipText("Click to FILL Column");
        masterStaceyKramersPctUncertaintyFiller.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (int f = 0; f
                        < getMyAliquot().getAliquotFractions().size(); f++) {
                    if (((JTextField) fractionStaceyKramersPctUncertaintyText.get(f)).isEnabled()) {
                        ((JTextField) fractionStaceyKramersPctUncertaintyText.//
                                get(f)).setText(masterStaceyKramersPctUncertainty.getText());
                    }
                }

            }
        });

        // StaceyKramersCorrelationCoeffs
        masterStaceyKramersCorrelationCoeffs
                = new JTextField();
        masterStaceyKramersCorrelationCoeffs.setDocument(//
                new BigDecimalDocument(masterStaceyKramersCorrelationCoeffs, true));
        masterStaceyKramersCorrelationCoeffs.setText("0.0000");
        masterStaceyKramersCorrelationCoeffs.setHorizontalAlignment(JTextField.RIGHT);

        // StaceyKramersCorrelationCoeffs filler button
        masterStaceyKramersCorrelationCoeffsFiller
                = new EditFractionButton("FILL", -1, true);
        masterStaceyKramersCorrelationCoeffsFiller.setToolTipText("Click to FILL Column");
        masterStaceyKramersCorrelationCoeffsFiller.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int f = 0; f
                        < getMyAliquot().getAliquotFractions().size(); f++) {
                    if (((JTextField) fractionStaceyKramersCorrelationCoeffsText.get(f)).isEnabled()) {
                        ((JTextField) fractionStaceyKramersCorrelationCoeffsText.//
                                get(f)).setText(masterStaceyKramersCorrelationCoeffs.getText());
                    }
                }

            }
        });

        // Blank Pb mass
        masterPbBlankMass
                = new JTextField();
        masterPbBlankMass.setDocument(new BigDecimalDocument(masterPbBlankMass, true));
        masterPbBlankMass.setText("0.0000");
        masterPbBlankMass.setHorizontalAlignment(JTextField.RIGHT);

        // Blank Pb mass filler button
        masterPbBlankMassFiller
                = new EditFractionButton("FILL", -1, true);
        masterPbBlankMassFiller.setToolTipText("Click to FILL Column");
        masterPbBlankMassFiller.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int f = 0; f
                        < getMyAliquot().getAliquotFractions().size(); f++) {
                    if (((JTextField) fractionPbBlankMassText.get(f)).isEnabled()) {
                        ((JTextField) fractionPbBlankMassText.get(f)).setText(masterPbBlankMass.getText());
                    }
                }

            }
        });

        // Blank U mass
        masterUBlankMass
                = new JTextField();
        masterUBlankMass.setDocument(new BigDecimalDocument(masterUBlankMass, true));
        masterUBlankMass.setText("0.0000");
        masterUBlankMass.setHorizontalAlignment(JTextField.RIGHT);

        // Blank U mass filler button
        masterBlankUMassFiller
                = new EditFractionButton("FILL", -1, true);
        masterBlankUMassFiller.setToolTipText("Click to FILL Column");
        masterBlankUMassFiller.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int f = 0; f
                        < getMyAliquot().getAliquotFractions().size(); f++) {
                    if (((JTextField) fractionUBlankMassText.get(f)).isEnabled()) {
                        ((JTextField) fractionUBlankMassText.get(f)).setText(masterUBlankMass.getText());
                    }
                }

            }
        });

        // r238_235s
        masterR238_235s
                = new JTextField();
        masterR238_235s.setDocument(new BigDecimalDocument(masterR238_235s, true));
        masterR238_235s.setText("0.0000");
        masterR238_235s.setHorizontalAlignment(JTextField.RIGHT);

        // r238_235s filler button
        masterR238_235sFiller
                = new EditFractionButton("FILL", -1, true);
        masterR238_235sFiller.setToolTipText("Click to FILL Column");
        masterR238_235sFiller.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int f = 0; f
                        < getMyAliquot().getAliquotFractions().size(); f++) {
                    if (((JTextField) fractionR238_235sText.get(f)).isEnabled()) {
                        ((JTextField) fractionR238_235sText.get(f)).setText(masterR238_235s.getText());
                    }
                }

            }
        });

        // r238_235s
        masterR238_235b
                = new JTextField();
        masterR238_235b.setDocument(new BigDecimalDocument(masterR238_235b, true));
        masterR238_235b.setText("0.0000");
        masterR238_235b.setHorizontalAlignment(JTextField.RIGHT);

        // r238_235s filler button
        masterR238_235bFiller
                = new EditFractionButton("FILL", -1, true);
        masterR238_235bFiller.setToolTipText("Click to FILL Column");
        masterR238_235bFiller.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int f = 0; f
                        < getMyAliquot().getAliquotFractions().size(); f++) {
                    if (((JTextField) fractionR238_235bText.get(f)).isEnabled()) {
                        ((JTextField) fractionR238_235bText.get(f)).setText(masterR238_235b.getText());
                    }
                }

            }
        });

        // r238_235s
        masterR18O_16O
                = new JTextField();
        masterR18O_16O.setDocument(new BigDecimalDocument(masterR18O_16O, true));
        masterR18O_16O.setText("0.0000");
        masterR18O_16O.setHorizontalAlignment(JTextField.RIGHT);

        // r238_235s filler button
        master18O_16OFiller
                = new EditFractionButton("FILL", -1, true);
        master18O_16OFiller.setToolTipText("Click to FILL Column");
        master18O_16OFiller.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int f = 0; f
                        < getMyAliquot().getAliquotFractions().size(); f++) {
                    if (((JTextField) fractionR18O_16OText.get(f)).isEnabled()) {
                        ((JTextField) fractionR18O_16OText.get(f)).setText(masterR18O_16O.getText());
                    }
                }

            }
        });

        // rTh_Umagma
        masterRTh_Umagma
                = new JTextField();
        masterRTh_Umagma.setDocument(new BigDecimalDocument(masterRTh_Umagma, true));
        masterRTh_Umagma.setText("0.0000");
        masterRTh_Umagma.setHorizontalAlignment(JTextField.RIGHT);

        // rTh_Umagma filler button
        masterRTh_UmagmaFiller
                = new EditFractionButton("FILL", -1, true);
        masterRTh_UmagmaFiller.setToolTipText("Click to FILL Column");
        masterRTh_UmagmaFiller.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int f = 0; f
                        < getMyAliquot().getAliquotFractions().size(); f++) {
                    if (((JTextField) fractionRTh_UmagmaText.get(f)).isEnabled()) {
                        ((JTextField) fractionRTh_UmagmaText.get(f)).setText(masterRTh_Umagma.getText());
                    }
                }

            }
        });

        // ar231_235sample
        masterAr231_235sample
                = new JTextField();
        masterAr231_235sample.setDocument(new BigDecimalDocument(masterAr231_235sample, true));
        masterAr231_235sample.setText("0.0000");
        masterAr231_235sample.setHorizontalAlignment(JTextField.RIGHT);

        // ar231_235sample filler button
        masterAr231_235sampleFiller
                = new EditFractionButton("FILL", -1, true);
        masterAr231_235sampleFiller.setToolTipText("Click to FILL Column");
        masterAr231_235sampleFiller.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int f = 0; f
                        < getMyAliquot().getAliquotFractions().size(); f++) {
                    if (((JTextField) fractionAR231_235sampleText.get(f)).isEnabled()) {
                        ((JTextField) fractionAR231_235sampleText.get(f)).setText(masterAr231_235sample.getText());
                    }
                }

            }
        });

        // Tracer Mass Uncertainty
        masterTracerMassOneSigma
                = new JTextField();
        masterTracerMassOneSigma.setDocument(new BigDecimalDocument(masterTracerMassOneSigma, true));
        masterTracerMassOneSigma.setText("0.0000");
        masterTracerMassOneSigma.setHorizontalAlignment(JTextField.RIGHT);

        // Estimated Date filler button
        masterTracerMassOneSigmaFiller
                = new EditFractionButton("FILL", -1, true);
        masterTracerMassOneSigmaFiller.setToolTipText("Click to FILL Column");
        masterTracerMassOneSigmaFiller.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int f = 0; f
                        < getMyAliquot().getAliquotFractions().size(); f++) {
                    ((JTextField) fractionTracerMassOneSigmaText.get(f)).setText(masterTracerMassOneSigma.getText());
                }

            }
        });

        // U Blank Mass Uncertainty
        masterUBlankMassOneSigma
                = new JTextField();
        masterUBlankMassOneSigma.setDocument(new BigDecimalDocument(masterUBlankMassOneSigma, true));
        masterUBlankMassOneSigma.setText("0.0000");
        masterUBlankMassOneSigma.setHorizontalAlignment(JTextField.RIGHT);

        // Estimated Age filler button
        masterUBlankMassOneSigmaFiller
                = new EditFractionButton("FILL", -1, true);
        masterUBlankMassOneSigmaFiller.setToolTipText("Click to FILL Column");
        masterUBlankMassOneSigmaFiller.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (int f = 0; f
                        < getMyAliquot().getAliquotFractions().size(); f++) {
                    ((JTextField) fractionUBlankMassOneSigmaText.get(f)).setText(masterUBlankMassOneSigma.getText());
                }

            }
        });

        // r238_35s Uncertainty
        masterR238_235sOneSigma
                = new JTextField();
        masterR238_235sOneSigma.setDocument(new BigDecimalDocument(masterR238_235sOneSigma, true));
        masterR238_235sOneSigma.setText("0.0000");
        masterR238_235sOneSigma.setHorizontalAlignment(JTextField.RIGHT);

        // R238_235sOneSigma filler button
        masterR238_235sOneSigmaFiller
                = new EditFractionButton("FILL", -1, true);
        masterR238_235sOneSigmaFiller.setToolTipText("Click to FILL Column");
        masterR238_235sOneSigmaFiller.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (int f = 0; f
                        < getMyAliquot().getAliquotFractions().size(); f++) {
                    ((JTextField) fractionR238_235sOneSigmaText.get(f)).setText(masterR238_235sOneSigma.getText());
                }

            }
        });

        // r238_35b Uncertainty
        masterR238_235bOneSigma
                = new JTextField();
        masterR238_235bOneSigma.setDocument(new BigDecimalDocument(masterR238_235bOneSigma, true));
        masterR238_235bOneSigma.setText("0.0000");
        masterR238_235bOneSigma.setHorizontalAlignment(JTextField.RIGHT);

        // R238_235bOneSigma filler button
        masterR238_235bOneSigmaFiller
                = new EditFractionButton("FILL", -1, true);
        masterR238_235bOneSigmaFiller.setToolTipText("Click to FILL Column");
        masterR238_235bOneSigmaFiller.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (int f = 0; f
                        < getMyAliquot().getAliquotFractions().size(); f++) {
                    ((JTextField) fractionR238_235bOneSigmaText.get(f)).setText(masterR238_235bOneSigma.getText());
                }

            }
        });

        // 18O_16O Uncertainty
        masterR18O_16OOneSigma
                = new JTextField();
        masterR18O_16OOneSigma.setDocument(new BigDecimalDocument(masterR18O_16OOneSigma, true));
        masterR18O_16OOneSigma.setText("0.0000");
        masterR18O_16OOneSigma.setHorizontalAlignment(JTextField.RIGHT);

        // 18O_16OOneSigma filler button
        master18O_16OOneSigmaFiller
                = new EditFractionButton("FILL", -1, true);
        master18O_16OOneSigmaFiller.setToolTipText("Click to FILL Column");
        master18O_16OOneSigmaFiller.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int f = 0; f
                        < getMyAliquot().getAliquotFractions().size(); f++) {
                    ((JTextField) fractionR18O_16OOneSigmaText.get(f)).setText(masterR18O_16OOneSigma.getText());
                }

            }
        });

        // Pb Blank Mass Uncertainty
        masterPbBlankMassOneSigma
                = new JTextField();
        masterPbBlankMassOneSigma.setDocument(new BigDecimalDocument(masterPbBlankMassOneSigma, true));
        masterPbBlankMassOneSigma.setText("0.0000");
        masterPbBlankMassOneSigma.setHorizontalAlignment(JTextField.RIGHT);

        //  Pb Blank Mass Uncertainty filler button
        masterPbBlankMassOneSigmaFiller
                = new EditFractionButton("FILL", -1, true);
        masterPbBlankMassOneSigmaFiller.setToolTipText("Click to FILL Column");
        masterPbBlankMassOneSigmaFiller.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (int f = 0; f
                        < getMyAliquot().getAliquotFractions().size(); f++) {
                    if (((JTextField) fractionPbBlankMassOneSigmaText.get(f)).isEnabled()) {
                        ((JTextField) fractionPbBlankMassOneSigmaText.get(f)).setText(masterPbBlankMassOneSigma.getText());
                    }
                }

            }
        });

        // rTh_Umagma Uncertainty
        masterRTh_UmagmaOneSigma
                = new JTextField();
        masterRTh_UmagmaOneSigma.setDocument(new BigDecimalDocument(masterRTh_UmagmaOneSigma, true));
        masterRTh_UmagmaOneSigma.setText("0.0000");
        masterRTh_UmagmaOneSigma.setHorizontalAlignment(JTextField.RIGHT);

        //  rTh_Umagma Uncertainty filler button
        masterRTh_UmagmaOneSigmaFiller
                = new EditFractionButton("FILL", -1, true);
        masterRTh_UmagmaOneSigmaFiller.setToolTipText("Click to FILL Column");
        masterRTh_UmagmaOneSigmaFiller.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (int f = 0; f
                        < getMyAliquot().getAliquotFractions().size(); f++) {
                    if (((JTextField) fractionRTh_UmagmaOneSigmaText.get(f)).isEnabled()) {
                        ((JTextField) fractionRTh_UmagmaOneSigmaText.get(f)).setText(masterRTh_UmagmaOneSigma.getText());
                    }
                }

            }
        });

        // ar231_235sample Uncertainty
        masterAr231_235sampleOneSigma
                = new JTextField();
        masterAr231_235sampleOneSigma.setDocument(new BigDecimalDocument(masterAr231_235sampleOneSigma, true));
        masterAr231_235sampleOneSigma.setText("0.0000");
        masterAr231_235sampleOneSigma.setHorizontalAlignment(JTextField.RIGHT);

        //  ar231_235sample Uncertainty filler button
        masterAr231_235sampleOneSigmaFiller
                = new EditFractionButton("FILL", -1, true);
        masterAr231_235sampleOneSigmaFiller.setToolTipText("Click to FILL Column");
        masterAr231_235sampleOneSigmaFiller.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                for (int f = 0; f
                        < getMyAliquot().getAliquotFractions().size(); f++) {
                    if (((JTextField) fractionAr231_235sampleOneSigmaText.get(f)).isEnabled()) {
                        ((JTextField) fractionAr231_235sampleOneSigmaText.get(f)).setText(masterAr231_235sampleOneSigma.getText());
                    }
                }

            }
        });

        // populate rows
        for (int row = 0; row
                < getMyAliquot().getAliquotFractions().size(); row++) {

            ETFractionInterface tempFrac = getMyAliquot().getAliquotFractions().get(row);
            int max = getMyAliquot().getAliquotFractions().size();
            addFractionRow(tempFrac, row, max);

        }

        // populate the components with fraction data
        showSavedDataII();

        buildFastEditDisplayPanel();
    }

    private void addFractionRow(ETFractionInterface tempFrac, int row, int max) {

//        // Buttons to allow deletion of fractions
//        JButton tempJB = new EditFractionButton("X", row, true);
//        tempJB.setForeground(Color.red);
//        tempJB.setToolTipText("Click to DELETE Fraction!");
//        tempJB.setMargin(new Insets(0, 0, 0, 0));
//        tempJB.addActionListener(new DeleteFractionListener(tempFrac, row));
//        fractionDeleteButtons.add(tempJB);
//        modifyComponentKeyMapForTable(tempJB, fractionDeleteButtons, max);
        // Buttons to open fraction editor
        JButton tempJB = new EditFractionButton("Kwiki", row, true);
        tempJB.addActionListener(new EditFractionListener(tempFrac, row));
        fractionEditButtons.add(tempJB);
        modifyComponentKeyMapForTable(tempJB, fractionEditButtons, max);

        // zircon check box
        JCheckBox tempJChk = new JCheckBox("NO I-Pb", false);
        tempJChk.setFont(new Font("Monospaced", Font.BOLD, 10));
        tempJChk.setOpaque(false);
        tempJChk.addChangeListener(new ChangeIsZirconListener(tempFrac, row));
        fractionZirconCheckBox.add(tempJChk);
        modifyComponentKeyMapForTable(tempJChk, fractionZirconCheckBox, max);

        //Tracer chooser
        JComboBox<String> tempJCB = new JComboBox<>();
        tempJCB.setFont(dropDownFont);
        tempJCB.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                JComboBox comboBox = (JComboBox) e.getSource();
                String tracerName = (String) comboBox.getSelectedItem();
                comboBox.setToolTipText(tracerName);
                try {
                    AbstractRatiosDataModel tracer = getMyAliquot().getMyReduxLabData().getATracerModel(tracerName);
                    comboBox.setToolTipText(ReduxLabData.toolTipForTracer(tracer));
                } catch (BadLabDataException ex) {
                    new ETWarningDialog(ex).setVisible(true);
                }
            }
        });

        ArrayList<AbstractRatiosDataModel> tracers = getMyAliquot().getMyReduxLabData().getTracers();
        // must keep index 0 for none tracer here for temporary state of Pb only before U imported
        for (int i = 0; i < tracers.size(); i++) {
            tempJCB.addItem(tracers.get(i).getReduxLabDataElementName());
        }

        tempJCB.addActionListener(new ChangeTracerListener(tempFrac, row));
        fractionTracerChoice.add(tempJCB);

        // Tracer mass
        insertTableTextField(fractionTracerMassText, max);

        // Tracer mass grams label
        JLabel tempJL = new JLabel(" g");
        tempJL.setForeground(Color.RED);
        tempJL.setFont(new Font("Monospaced", Font.BOLD, 10));
        fractionTracerGRAMS.add(tempJL);

        // Fraction mass
        insertTableTextField(fractionMassText, max);

        // Fraction Mass micrograms label
        tempJL = new JLabel("\u03BCg");
        tempJL.setForeground(Color.RED);
        tempJL.setFont(new Font("Monospaced", Font.BOLD, 10));
        fractionMassGRAMS.add(tempJL);

        //  alphaPb chooser
        tempJCB = new JComboBox<>();
        tempJCB.setFont(dropDownFont);
        tempJCB.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                JComboBox comboBox = (JComboBox) e.getSource();
                String alphaPbModelName = (String) comboBox.getSelectedItem();
                comboBox.setToolTipText(alphaPbModelName);
                // if a real model is involved, box is enabled
                if (comboBox.isEnabled()) {
                    try {
                        ValueModel alphaPb = getMyAliquot().getMyReduxLabData().getAnAlphaPbModel(alphaPbModelName);
                        comboBox.setToolTipText(ReduxLabData.toolTipForAlphaPbModel(alphaPb));
                    } catch (BadLabDataException ex) {
                        new ETWarningDialog(ex).setVisible(true);
                    }
                }
            }
        });

        ArrayList<ValueModel> alphaPbModels = getMyAliquot().getMyReduxLabData().getAlphaPbModels();
        for (int i = 1; i < alphaPbModels.size(); i++) {
            tempJCB.addItem(((ValueModel) alphaPbModels.get(i)).getName());
        }

        fractionAlphaPbChoice.add(tempJCB);

        //  alphaU chooser
        tempJCB = new JComboBox<>();
        tempJCB.setFont(dropDownFont);
        tempJCB.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                JComboBox comboBox = (JComboBox) e.getSource();
                String alphaUModelName = (String) comboBox.getSelectedItem();
                comboBox.setToolTipText(alphaUModelName);
                // if a real model is involved, box is enabled
                if (comboBox.isEnabled()) {
                    try {
                        ValueModel alphaU = getMyAliquot().getMyReduxLabData().getAnAlphaUModel(alphaUModelName);
                        comboBox.setToolTipText(ReduxLabData.toolTipForAlphaUModel(alphaU));
                    } catch (BadLabDataException ex) {
                        new ETWarningDialog(ex).setVisible(true);
                    }
                }
            }
        });

        ArrayList<ValueModel> alphaUModels = getMyAliquot().getMyReduxLabData().getAlphaUModels();
        for (int i = 1; i < alphaUModels.size(); i++) {
            tempJCB.addItem(((ValueModel) alphaUModels.get(i)).getName());
        }
        fractionAlphaUChoice.add(tempJCB);

        //  Pb Blank chooser
        tempJCB = new JComboBox<>();
        tempJCB.setFont(dropDownFont);
        tempJCB.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                JComboBox comboBox = (JComboBox) e.getSource();
                String blankName = (String) comboBox.getSelectedItem();
                comboBox.setToolTipText(blankName);
            }
        });

        ArrayList<AbstractRatiosDataModel> pbBlanks = getMyAliquot().getMyReduxLabData().getPbBlanks();
        for (int i = (pbBlanks.size() > 1 ? 1 : 0); i < pbBlanks.size(); i++) {
            tempJCB.addItem(((AbstractRatiosDataModel) pbBlanks.get(i)).getReduxLabDataElementName());
        }
        fractionPbBlankChoice.add(tempJCB);

        //  Initial Pb chooser
        tempJCB = new JComboBox<>();
        tempJCB.setFont(dropDownFont);
        tempJCB.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                JComboBox comboBox = (JComboBox) e.getSource();
                String initialPbName = (String) comboBox.getSelectedItem();
                comboBox.setToolTipText(initialPbName);
            }
        });

        ArrayList<AbstractRatiosDataModel> initialPbModels = getMyAliquot().getMyReduxLabData().getInitialPbModels();
        for (int i = (initialPbModels.size() > 1 ? 1 : 0); i < initialPbModels.size(); i++) {
//            if (  ! (initialPbModels.get( i ) instanceof StaceyKramersInitialPbModelET) ) {
            tempJCB.addItem(initialPbModels.get(i).getReduxLabDataElementName());
//            }
        }
        // aug 2010
        tempJCB.addItemListener(new ChangeInitialPbModelItemListener(tempFrac, row));
        fractionInitialPbChoice.add(tempJCB);

        // Estimated Date
        insertTableTextField(fractionEstDateText, max);
        // Estimated Date in MegaAnnum label
        tempJL = new JLabel(" Ma");
        tempJL.setForeground(Color.RED);
        tempJL.setFont(new Font("Monospaced", Font.BOLD, 10));
        fractionEstDateMEGAANNUM.add(tempJL);

        // StaceyKramersPctUncertainty
        insertTableTextField(fractionStaceyKramersPctUncertaintyText, max);
        // StaceyKramersCorrelationCoeffs
        insertTableTextField(fractionStaceyKramersCorrelationCoeffsText, max);

        // Pb Blank mass
        insertTableTextField(fractionPbBlankMassText, max);

        // Pb Blank mass picograms label
        tempJL = new JLabel(" pg");
        tempJL.setForeground(Color.RED);
        tempJL.setFont(new Font("Monospaced", Font.BOLD, 10));
        fractionPbBlankPICOGRAMS.add(tempJL);

        // U Blank mass
        insertTableTextField(fractionUBlankMassText, max);

        // U Blank mass picograms label
        tempJL = new JLabel(" pg");
        tempJL.setForeground(Color.RED);
        tempJL.setFont(new Font("Monospaced", Font.BOLD, 10));
        fractionUBlankPICOGRAMS.add(tempJL);

        // R238_235s
        insertTableTextField(fractionR238_235sText, max);
        // R238_235b
        insertTableTextField(fractionR238_235bText, max);
        //18O_16O
        insertTableTextField(fractionR18O_16OText, max);

        insertTableTextField(fractionRTh_UmagmaText, max);
        insertTableTextField(fractionAR231_235sampleText, max);

        // Tracer Mass One Sigma
        insertTableTextField(fractionTracerMassOneSigmaText, max);
        // U Blank Mass One Sigma
        insertTableTextField(fractionUBlankMassOneSigmaText, max);
        // r238_235s One Sigma
        insertTableTextField(fractionR238_235sOneSigmaText, max);
        // r238_235b One Sigma
        insertTableTextField(fractionR238_235bOneSigmaText, max);
        // 18O_16O One Sigma
        insertTableTextField(fractionR18O_16OOneSigmaText, max);
        // Pb Blank Mass One Sigma
        insertTableTextField(fractionPbBlankMassOneSigmaText, max);
        insertTableTextField(fractionRTh_UmagmaOneSigmaText, max);
        insertTableTextField(fractionAr231_235sampleOneSigmaText, max);

    }

    private void buildFastEditDisplayPanel() {

        fastEdits_panel.removeAll();

        // build display
        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(fastEdits_panel);
        fastEdits_panel.setLayout(jPanel2Layout);

        ParallelGroup myHorizFraction = jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false);
        SequentialGroup myVerticalFraction = jPanel2Layout.createSequentialGroup();

        // create title row elements
//        JLabel headDelete = new JLabel("DELETE");
//        headDelete.setFont(new Font("Monospaced", Font.BOLD, 10));
//        headDelete.setForeground(Color.RED);
        JLabel headFraction = new JLabel("EDIT Fraction");
        headFraction.setFont(new Font("Monospaced", Font.BOLD, 10));
        headFraction.setForeground(Color.RED);

        JLabel headZircon = new JLabel("NO Init Pb");//Zircon?" );
        headZircon.setFont(new Font("Monospaced", Font.BOLD, 10));
        headZircon.setForeground(Color.RED);

        JLabel headTracer = new JLabel("Tracer Name");
        headTracer.setFont(new Font("Monospaced", Font.BOLD, 10));
        headTracer.setForeground(Color.RED);

        JLabel headTracerMass = new JLabel("Tracer Mass");
        headTracerMass.setFont(new Font("Monospaced", Font.BOLD, 10));
        headTracerMass.setForeground(Color.RED);

        JLabel headFractionMass = new JLabel("Fraction mass");
        headFractionMass.setFont(new Font("Monospaced", Font.BOLD, 10));
        headFractionMass.setForeground(Color.RED);

        JLabel headAlphaPb = new JLabel("<html>Pb Fractionation Model</html>");
        headAlphaPb.setFont(new Font("Monospaced", Font.BOLD, 10));
        headAlphaPb.setForeground(Color.RED);

        JLabel headAlphaU = new JLabel("<html>U Fractionation Model</html>");
        headAlphaU.setFont(new Font("Monospaced", Font.BOLD, 10));
        headAlphaU.setForeground(Color.RED);

        JLabel headPbBlank = new JLabel("Pb Blank");
        headPbBlank.setFont(new Font("Monospaced", Font.BOLD, 10));
        headPbBlank.setForeground(Color.RED);

        JLabel headInitialPb = new JLabel("Initial Pb Model");
        headInitialPb.setFont(new Font("Monospaced", Font.BOLD, 10));
        headInitialPb.setForeground(Color.RED);

        JLabel headEstDate = new JLabel("Est Date");
        headEstDate.setFont(new Font("Monospaced", Font.BOLD, 10));
        headEstDate.setForeground(Color.RED);

        JLabel headStaceyKramerPctUnct = new JLabel("SK % Unct");
        headStaceyKramerPctUnct.setFont(new Font("Monospaced", Font.BOLD, 10));
        headStaceyKramerPctUnct.setForeground(Color.RED);

        JLabel headStaceyKramerRhos = new JLabel("SK Rhos");
        headStaceyKramerRhos.setFont(new Font("Monospaced", Font.BOLD, 10));
        headStaceyKramerRhos.setForeground(Color.RED);

        JLabel headPbBlankMass = new JLabel("Pb Blank mass");
        headPbBlankMass.setFont(new Font("Monospaced", Font.BOLD, 10));
        headPbBlankMass.setForeground(Color.RED);

        JLabel headUBlankMass = new JLabel("U Blank mass");
        headUBlankMass.setFont(new Font("Monospaced", Font.BOLD, 10));
        headUBlankMass.setForeground(Color.RED);

        JLabel headr238_235s = new JLabel("238/235smpl");
        headr238_235s.setFont(new Font("Monospaced", Font.BOLD, 10));
        headr238_235s.setForeground(Color.RED);

        JLabel headr238_235b = new JLabel("238/235blnk");
        headr238_235b.setFont(new Font("Monospaced", Font.BOLD, 10));
        headr238_235b.setForeground(Color.RED);

        JLabel head18O_16O = new JLabel("18O/16OUox");
        head18O_16O.setFont(new Font("Monospaced", Font.BOLD, 10));
        head18O_16O.setForeground(Color.RED);

        JLabel headRTh_Umagma = new JLabel(AnalysisMeasures.rTh_Umagma.getName());
        headRTh_Umagma.setFont(new Font("Monospaced", Font.BOLD, 10));
        headRTh_Umagma.setForeground(Color.RED);

        JLabel headAr231_235sample = new JLabel("ar231_235s");
        headAr231_235sample.setFont(new Font("Monospaced", Font.BOLD, 10));
        headAr231_235sample.setForeground(Color.RED);

        // uncertainties
        JLabel headTracerMassOneSigma
                = new JLabel("<html>\u00B11" + '\u03c3' + " (abs)</html>");
        headTracerMassOneSigma.setFont(new Font("Monospaced", Font.BOLD, 10));
        headTracerMassOneSigma.setForeground(Color.RED);
        headTracerMassOneSigma.setHorizontalTextPosition(SwingConstants.CENTER);

        JLabel headUBlankMassOneSigma
                = new JLabel("<html>1" + '\u03c3' + "-ABS U blank mass </html>");
        headUBlankMassOneSigma.setFont(new Font("Monospaced", Font.BOLD, 9));
        headUBlankMassOneSigma.setForeground(Color.RED);
        headUBlankMassOneSigma.setHorizontalTextPosition(SwingConstants.CENTER);

        JLabel headR238_235sOneSigma
                = new JLabel("<html>1" + '\u03c3' + "-ABS 238/235 smpl </html>");
        headR238_235sOneSigma.setFont(new Font("Monospaced", Font.BOLD, 9));
        headR238_235sOneSigma.setForeground(Color.RED);
        headR238_235sOneSigma.setHorizontalTextPosition(SwingConstants.CENTER);

        JLabel headR238_235bOneSigma
                = new JLabel("<html>1" + '\u03c3' + "-ABS 238/235 blnk </html>");
        headR238_235bOneSigma.setFont(new Font("Monospaced", Font.BOLD, 9));
        headR238_235bOneSigma.setForeground(Color.RED);
        headR238_235bOneSigma.setHorizontalTextPosition(SwingConstants.CENTER);

        JLabel head18O_16OOneSigma
                = new JLabel("<html>1" + '\u03c3' + "-ABS 18O/16O uox</html>");
        head18O_16OOneSigma.setFont(new Font("Monospaced", Font.BOLD, 9));
        head18O_16OOneSigma.setForeground(Color.RED);
        head18O_16OOneSigma.setHorizontalTextPosition(SwingConstants.CENTER);

        JLabel headPbBlankMassOneSigma
                = new JLabel("<html>1" + '\u03c3' + "-ABS Pb Blank mass</html>");
        headPbBlankMassOneSigma.setFont(new Font("Monospaced", Font.BOLD, 9));
        headPbBlankMassOneSigma.setForeground(Color.RED);
        headPbBlankMassOneSigma.setHorizontalTextPosition(SwingConstants.CENTER);

        JLabel headRTh_UmagmaOneSigma
                = new JLabel("<html>1" + '\u03c3' + "-ABS rTh_Umagma</html>");
        headRTh_UmagmaOneSigma.setFont(new Font("Monospaced", Font.BOLD, 9));
        headRTh_UmagmaOneSigma.setForeground(Color.RED);
        headRTh_UmagmaOneSigma.setHorizontalTextPosition(SwingConstants.CENTER);

        JLabel headAr231_235sampleOneSigma
                = new JLabel("<html>1" + '\u03c3' + "-ABS ar231_235s</html>");
        headAr231_235sampleOneSigma.setFont(new Font("Monospaced", Font.BOLD, 9));
        headAr231_235sampleOneSigma.setForeground(Color.RED);
        headAr231_235sampleOneSigma.setHorizontalTextPosition(SwingConstants.CENTER);

        // build display *******************************************************
        // master fields
        myHorizFraction.add(jPanel2Layout.createSequentialGroup()//
                .add(50, 50, 50) // left margin
                //               .add(masterNewFractionName, 110, 110, 110)//
                .add(100, 100, 100)//
                .add(masterZirconCaseCheckBox, 75, 75, 75)//
                .add(5, 5, 5)//
                .add(masterTracerChooser, 150, 150, 150)//
                .add(5, 5, 5)//
                .add(masterTracerMass, 65, 65, 65)//
                .add(20, 20, 20)//
                .add(masterTracerMassOneSigma, 70, 70, 70)//
                .add(15, 15, 15)//
                .add(masterFractionMass, 65, 65, 65)//
                .add(20, 20, 20)//
                .add(masterAlphaPbChooser, 150, 150, 150)//
                .add(5, 5, 5)//
                .add(masterAlphaUChooser, 150, 150, 150)//
                .add(5, 5, 5)//
                .add(masterPbBlankChooser, 150, 150, 150)//
                .add(5, 5, 5)//
                .add(masterInitialPbModelChooser, 150, 150, 150)//
                .add(10, 10, 10)//
                .add(masterEstimatedDate, 65, 65, 65)//
                .add(25, 25, 25)//
                .add(masterStaceyKramersPctUncertainty, 60, 60, 60)//
                .add(10, 10, 10)//
                .add(masterStaceyKramersCorrelationCoeffs, 60, 60, 60)//
                .add(10, 10, 10)//
                .add(masterPbBlankMass, 65, 65, 65)//
                .add(15, 15, 15)//
                .add(masterUBlankMass, 65, 65, 65)//
                .add(25, 25, 25)//
                .add(masterR238_235s, 70, 70, 70)//
                .add(10, 10, 10)//
                .add(masterR238_235b, 70, 70, 70)//
                .add(10, 10, 10)//
                .add(masterR18O_16O, 70, 70, 70)//
                .add(10, 10, 10)//
                .add(masterRTh_Umagma, 65, 65, 65)//
                .add(15, 15, 15)//
                .add(masterAr231_235sample, 65, 65, 65)//
                .add(15, 15, 15)//
                //                .add( masterTracerMassOneSigma, 70, 70, 70 )//
                //                .add( 15, 15, 15 )//
                .add(masterUBlankMassOneSigma, 70, 70, 70)//
                .add(15, 15, 15)//
                .add(masterR238_235sOneSigma, 70, 70, 70)//
                .add(15, 15, 15)//
                .add(masterR238_235bOneSigma, 70, 70, 70)//
                .add(15, 15, 15)//
                .add(masterR18O_16OOneSigma, 70, 70, 70)//
                .add(15, 15, 15)//
                .add(masterPbBlankMassOneSigma, 70, 70, 70)//
                .add(15, 15, 15)//
                .add(masterRTh_UmagmaOneSigma, 70, 70, 70)//
                .add(15, 15, 15)//
                .add(masterAr231_235sampleOneSigma, 70, 70, 70)//
        );

        myVerticalFraction.add(5, 5, 5) // top margin
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)//.BASELINE)//
                        // .add(masterNewFractionName, 22, 22, 22)//
                        .add(masterZirconCaseCheckBox, 22, 22, 22)//
                        .add(masterTracerChooser, 22, 22, 22)//
                        .add(masterTracerMass, 22, 22, 22)//
                        .add(masterTracerMassOneSigma, 22, 22, 22)//
                        .add(masterFractionMass, 22, 22, 22)//
                        .add(masterAlphaPbChooser, 22, 22, 22)//
                        .add(masterAlphaUChooser, 22, 22, 22)//
                        .add(masterPbBlankChooser, 22, 22, 22)//
                        .add(masterInitialPbModelChooser, 22, 22, 22)//
                        .add(masterEstimatedDate, 22, 22, 22)//
                        .add(masterStaceyKramersPctUncertainty, 22, 22, 22)//
                        .add(masterStaceyKramersCorrelationCoeffs, 22, 22, 22)//
                        .add(masterPbBlankMass, 22, 22, 22)//
                        .add(masterUBlankMass, 22, 22, 22)//
                        .add(masterR238_235s, 22, 22, 22)//
                        .add(masterR238_235b, 22, 22, 22)//
                        .add(masterR18O_16O, 22, 22, 22)//
                        .add(masterRTh_Umagma, 22, 22, 22)//
                        .add(masterAr231_235sample, 22, 22, 22)//
                        //                .add( masterTracerMassOneSigma, 22, 22, 22 )//
                        .add(masterUBlankMassOneSigma, 22, 22, 22)//
                        .add(masterR238_235sOneSigma, 22, 22, 22)//
                        .add(masterR238_235bOneSigma, 22, 22, 22)//
                        .add(masterR18O_16OOneSigma, 22, 22, 22)//
                        .add(masterPbBlankMassOneSigma, 22, 22, 22)//
                        .add(masterRTh_UmagmaOneSigma, 22, 22, 22)//
                        .add(masterAr231_235sampleOneSigma, 22, 22, 22)//
                );

        // fill buttons
        myHorizFraction.add(jPanel2Layout.createSequentialGroup()//
                .add(50, 50, 50) // left margin
                // .add(masterNewFractionNameAdder, 110, 110, 110)//
                .add(115, 115, 115)//
                .add(masterZironCaseFiller, 65, 65, 65)//
                .add(5, 5, 5)//
                .add(masterTracerFiller, 150, 150, 150)//
                .add(5, 5, 5)//
                .add(masterTracerMassFiller, 65, 65, 65)//
                .add(20, 20, 20)//
                .add(masterTracerMassOneSigmaFiller, 70, 70, 70)//
                .add(15, 15, 15)//
                .add(masterFractionMassFiller, 65, 65, 65)//
                .add(20, 20, 20)//
                .add(masterAlphaPbFiller, 150, 150, 150)//
                .add(5, 5, 5)//
                .add(masterAlphaUFiller, 150, 150, 150)//
                .add(5, 5, 5)//
                .add(masterPbBlankFiller, 150, 150, 150)//
                .add(5, 5, 5)//
                .add(masterInitialPbModelFiller, 150, 150, 150)//
                .add(10, 10, 10)//
                .add(masterEstimatedDateFiller, 65, 65, 65)//
                .add(22, 22, 22)//
                .add(masterStaceyKramersPctUncertaintyFiller, 65, 65, 65)//
                .add(5, 5, 5)//
                .add(masterStaceyKramersCorrelationCoeffsFiller, 65, 65, 65)//
                .add(5, 5, 5)//
                .add(masterPbBlankMassFiller, 65, 65, 65)//
                .add(15, 15, 15)//
                .add(masterBlankUMassFiller, 65, 65, 65)//
                .add(25, 25, 25)//
                .add(masterR238_235sFiller, 70, 70, 70)//
                .add(10, 10, 10)//
                .add(masterR238_235bFiller, 70, 70, 70)//
                .add(10, 10, 10)//
                .add(master18O_16OFiller, 70, 70, 70)//
                .add(10, 10, 10)//
                .add(masterRTh_UmagmaFiller, 65, 65, 65)//
                .add(15, 15, 15)//
                .add(masterAr231_235sampleFiller, 65, 65, 65)//
                .add(15, 15, 15)//
                //                .add( masterTracerMassOneSigmaFiller, 70, 70, 70 )//
                //                .add( 15, 15, 15 )//
                .add(masterUBlankMassOneSigmaFiller, 70, 70, 70)//
                .add(15, 15, 15)//
                .add(masterR238_235sOneSigmaFiller, 70, 70, 70)//
                .add(15, 15, 15)//
                .add(masterR238_235bOneSigmaFiller, 70, 70, 70)//
                .add(15, 15, 15)//
                .add(master18O_16OOneSigmaFiller, 70, 70, 70)//
                .add(15, 15, 15)//
                .add(masterPbBlankMassOneSigmaFiller, 70, 70, 70)//
                .add(15, 15, 15)//
                .add(masterRTh_UmagmaOneSigmaFiller, 70, 70, 70)//
                .add(15, 15, 15)//
                .add(masterAr231_235sampleOneSigmaFiller, 70, 70, 70)//
        );

        myVerticalFraction.add(1, 1, 1) // top margin
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)//.BASELINE)//
                        //  .add(masterNewFractionNameAdder)//
                        .add(masterZironCaseFiller)//, 22, 22, 22)//
                        .add(masterTracerFiller)//, 22, 22, 22)//
                        .add(masterTracerMassFiller)//, 22, 22, 22)//
                        .add(masterTracerMassOneSigmaFiller)//, 22, 22, 22)//
                        .add(masterFractionMassFiller)//, 22, 22, 22)//
                        .add(masterAlphaPbFiller)//, 22, 22, 22)//
                        .add(masterAlphaUFiller)//, 22, 22, 22)//
                        .add(masterPbBlankFiller)//, 22, 22, 22)//
                        .add(masterInitialPbModelFiller)//, 22, 22, 22)//
                        .add(masterEstimatedDateFiller)//, 22, 22, 22)//
                        .add(masterStaceyKramersPctUncertaintyFiller)//, 22, 22, 22)//
                        .add(masterStaceyKramersCorrelationCoeffsFiller)//, 22, 22, 22)//
                        .add(masterPbBlankMassFiller)//, 22, 22, 22)//
                        .add(masterBlankUMassFiller)//, 22, 22, 22)//
                        .add(masterR238_235sFiller)//, 22, 22, 22)//
                        .add(masterR238_235bFiller)//, 22, 22, 22)//
                        .add(master18O_16OFiller)//, 22, 22, 22)//
                        .add(masterRTh_UmagmaFiller)//, 22, 22, 22)//
                        .add(masterAr231_235sampleFiller)//, 22, 22, 22)//
                        .add(masterRTh_UmagmaFiller)//, 22, 22, 22)//
                        .add(masterAr231_235sampleFiller)//, 22, 22, 22)//
                        //                .add( masterTracerMassOneSigmaFiller )//, 22, 22, 22)//
                        .add(masterUBlankMassOneSigmaFiller)//, 22, 22, 22)//
                        .add(masterR238_235sOneSigmaFiller)//, 22, 22, 22)//
                        .add(masterR238_235bOneSigmaFiller)//, 22, 22, 22)//
                        .add(master18O_16OOneSigmaFiller)//, 22, 22, 22)//
                        .add(masterPbBlankMassOneSigmaFiller)//, 22, 22, 22)//
                        .add(masterRTh_UmagmaOneSigmaFiller)//, 22, 22, 22)//
                        .add(masterAr231_235sampleOneSigmaFiller)//, 22, 22, 22)//
                );

        myHorizFraction.add(jPanel2Layout.createSequentialGroup()//
                .add(5, 5, 5) // left margin
                // .add(headDelete, 50, 50, 50)//
                .add(5, 5, 5)//
                .add(headFraction, 145, 145, 145)//
                .add(15, 15, 15)//
                .add(headZircon, 65, 65, 65)//
                .add(5, 5, 5)//
                .add(headTracer, 140, 140, 140)//
                .add(15, 15, 15)//
                .add(headTracerMass, 75, 75, 75)//
                .add(5, 5, 5)//
                .add(headTracerMassOneSigma, 75, 75, 75)//
                .add(10, 10, 10)//
                .add(headFractionMass, 85, 85, 85)//
                .add(7, 7, 7)//
                .add(headAlphaPb, 140, 140, 140)//
                .add(15, 15, 15)//
                .add(headAlphaU, 140, 140, 140)//
                .add(15, 15, 15)//
                .add(headPbBlank, 140, 140, 140)//
                .add(15, 15, 15)//
                .add(headInitialPb, 150, 150, 150)//
                .add(10, 10, 10)//
                .add(headEstDate, 75, 75, 75)//
                .add(12, 12, 12)//
                .add(headStaceyKramerPctUnct, 60, 60, 60)//
                .add(10, 10, 10)//
                .add(headStaceyKramerRhos, 60, 60, 60)//
                .add(10, 10, 10)//
                .add(headPbBlankMass, 85, 85, 85)//
                .add(7, 7, 7)//
                .add(headUBlankMass, 80, 80, 80)//
                .add(7, 7, 7)//
                .add(headr238_235s, 75, 75, 75)//
                .add(5, 5, 5)//
                .add(headr238_235b, 75, 75, 75)//
                .add(5, 5, 5)//
                .add(head18O_16O, 75, 75, 75)//
                .add(5, 5, 5)//
                .add(headRTh_Umagma, 75, 75, 75)//
                .add(5, 5, 5)//
                .add(headAr231_235sample, 75, 75, 75)//
                .add(5, 5, 5)//
                //                .add( headTracerMassOneSigma, 75, 75, 75 )//
                //                .add( 10, 10, 10 )//
                .add(headUBlankMassOneSigma, 75, 75, 75)//
                .add(10, 10, 10)//
                .add(headR238_235sOneSigma, 75, 75, 75)//
                .add(10, 10, 10)//
                .add(headR238_235bOneSigma, 75, 75, 75)//
                .add(10, 10, 10)//
                .add(head18O_16OOneSigma, 75, 75, 75)//
                .add(10, 10, 10)//
                .add(headPbBlankMassOneSigma, 75, 75, 75)//
                .add(12, 12, 12)//
                .add(headRTh_UmagmaOneSigma, 75, 75, 75)//
                .add(10, 10, 10)//
                .add(headAr231_235sampleOneSigma, 75, 75, 75)//
        );

        myVerticalFraction.add(10, 10, 10) // top margin
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)//.BASELINE)//
                        //.add(headDelete)//
                        .add(headFraction)//
                        .add(headZircon)//
                        .add(headTracer)//
                        .add(headTracerMass)//
                        .add(headTracerMassOneSigma)//
                        .add(headFractionMass)//
                        .add(headAlphaPb)//
                        .add(headAlphaU)//
                        .add(headPbBlank)//
                        .add(headInitialPb)//
                        .add(headEstDate)//
                        .add(headStaceyKramerPctUnct)//
                        .add(headStaceyKramerRhos)//
                        .add(headPbBlankMass)//
                        .add(headUBlankMass)//
                        .add(headr238_235s)//
                        .add(headr238_235b)//
                        .add(head18O_16O)//
                        .add(headRTh_Umagma)//
                        .add(headAr231_235sample)//
                        //                .add( headTracerMassOneSigma )//
                        .add(headUBlankMassOneSigma)//
                        .add(headR238_235sOneSigma)//
                        .add(headR238_235bOneSigma)//
                        .add(head18O_16OOneSigma)//
                        .add(headPbBlankMassOneSigma)//
                        .add(headRTh_UmagmaOneSigma)//
                        .add(headAr231_235sampleOneSigma)//
                )//
                .add(2, 2, 2);

        // stop delete when only one fraction
//        fractionDeleteButtons.get(0).setEnabled(fractionDeleteButtons.size() != 1);
        for (int f = 0; f
                < fractionEditButtons.size(); f++) {
            myHorizFraction.add(jPanel2Layout.createSequentialGroup()//
                    .add(4, 4, 4) // left-hand margin
                    //.add(fractionDeleteButtons.get(f), 50, 50, 50) //
                    .add(3, 3, 3)//
                    .add(fractionEditButtons.get(f), 150, 150, 150) //
                    .add(0, 0, 0)//
                    .add(fractionZirconCheckBox.get(f), 75, 75, 75)//
                    .add(5, 5, 5)//
                    .add(fractionTracerChoice.get(f), 150, 150, 150)//
                    .add(5, 5, 5)//
                    .add(fractionTracerMassText.get(f), 65, 65, 65)//
                    .add(fractionTracerGRAMS.get(f))//
                    .add(5, 5, 5)//
                    .add(fractionTracerMassOneSigmaText.get(f), 75, 75, 75)//
                    .add(10, 10, 10)//
                    .add(fractionMassText.get(f), 65, 65, 65)//
                    .add(fractionMassGRAMS.get(f))//
                    .add(10, 10, 10)//
                    .add(fractionAlphaPbChoice.get(f), 150, 150, 150)//
                    .add(5, 5, 5)//
                    .add(fractionAlphaUChoice.get(f), 150, 150, 150)//
                    .add(5, 5, 5)//
                    .add(fractionPbBlankChoice.get(f), 150, 150, 150)//
                    .add(5, 5, 5)//
                    .add(fractionInitialPbChoice.get(f), 150, 150, 150)//
                    .add(10, 10, 10)//
                    .add(fractionEstDateText.get(f), 60, 60, 60)//
                    .add(fractionEstDateMEGAANNUM.get(f))//
                    .add(7, 7, 7)//
                    .add(fractionStaceyKramersPctUncertaintyText.get(f), 60, 60, 60)//
                    .add(10, 10, 10)//
                    .add(fractionStaceyKramersCorrelationCoeffsText.get(f), 60, 60, 60)//
                    .add(10, 10, 10)//
                    .add(fractionPbBlankMassText.get(f), 65, 65, 65)//
                    .add(fractionPbBlankPICOGRAMS.get(f))//
                    .add(5, 5, 5)//
                    .add(fractionUBlankMassText.get(f), 65, 65, 65)//
                    .add(fractionUBlankPICOGRAMS.get(f))//
                    .add(5, 5, 5)//
                    .add(fractionR238_235sText.get(f), 70, 70, 70)//
                    .add(10, 10, 10)//
                    .add(fractionR238_235bText.get(f), 70, 70, 70)//
                    .add(10, 10, 10)//
                    .add(fractionR18O_16OText.get(f), 70, 70, 70)//
                    .add(10, 10, 10)//
                    .add(fractionRTh_UmagmaText.get(f), 70, 70, 70)//
                    .add(10, 10, 10)//
                    .add(fractionAR231_235sampleText.get(f), 70, 70, 70)//
                    .add(10, 10, 10)//
                    //                    .add( fractionTracerMassOneSigmaText.get( f ), 75, 75, 75 )//
                    //                    .add( 10, 10, 10 )//
                    .add(fractionUBlankMassOneSigmaText.get(f), 75, 75, 75)//
                    .add(10, 10, 10)//
                    .add(fractionR238_235sOneSigmaText.get(f), 75, 75, 75)//
                    .add(10, 10, 10)//
                    .add(fractionR238_235bOneSigmaText.get(f), 75, 75, 75)//
                    .add(10, 10, 10)//
                    .add(fractionR18O_16OOneSigmaText.get(f), 75, 75, 75)//
                    .add(10, 10, 10)//
                    .add(fractionPbBlankMassOneSigmaText.get(f), 75, 75, 75)//
                    .add(10, 10, 10)//
                    .add(fractionRTh_UmagmaOneSigmaText.get(f), 75, 75, 75)//
                    .add(10, 10, 10)//
                    .add(fractionAr231_235sampleOneSigmaText.get(f), 75, 75, 75)//
            );

            myVerticalFraction.add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)//
                    //.add(fractionDeleteButtons.get(f), 22, 22, 22)//)//, 28, 28, 28)//
                    .add(fractionEditButtons.get(f), 22, 22, 22)//)//, 28, 28, 28)//
                    .add(fractionZirconCheckBox.get(f), 24, 24, 24)//
                    .add(fractionTracerChoice.get(f), 22, 22, 22)//
                    .add(fractionTracerMassText.get(f), 22, 22, 22)//
                    .add(fractionTracerGRAMS.get(f))//
                    .add(fractionTracerMassOneSigmaText.get(f), 22, 22, 22)//
                    .add(fractionMassText.get(f), 22, 22, 22)//
                    .add(fractionMassGRAMS.get(f))//
                    .add(fractionAlphaPbChoice.get(f), 22, 22, 22)//
                    .add(fractionAlphaUChoice.get(f), 22, 22, 22)//
                    .add(fractionPbBlankChoice.get(f), 22, 22, 22)//
                    .add(fractionInitialPbChoice.get(f), 22, 22, 22)//
                    .add(fractionEstDateText.get(f), 22, 22, 22)//
                    .add(fractionEstDateMEGAANNUM.get(f))//
                    .add(fractionStaceyKramersPctUncertaintyText.get(f), 22, 22, 22)//
                    .add(fractionStaceyKramersCorrelationCoeffsText.get(f), 22, 22, 22)//
                    .add(fractionPbBlankMassText.get(f), 22, 22, 22)//
                    .add(fractionPbBlankPICOGRAMS.get(f))//
                    .add(fractionUBlankMassText.get(f), 22, 22, 22)//
                    .add(fractionUBlankPICOGRAMS.get(f))//
                    .add(fractionR238_235sText.get(f), 22, 22, 22)//
                    .add(fractionR238_235bText.get(f), 22, 22, 22)//
                    .add(fractionR18O_16OText.get(f), 22, 22, 22)//
                    .add(fractionRTh_UmagmaText.get(f), 22, 22, 22)//
                    .add(fractionAR231_235sampleText.get(f), 22, 22, 22)//
                    //                    .add( fractionTracerMassOneSigmaText.get( f ), 22, 22, 22 )//
                    .add(fractionUBlankMassOneSigmaText.get(f), 22, 22, 22)//
                    .add(fractionR238_235sOneSigmaText.get(f), 22, 22, 22)//
                    .add(fractionR238_235bOneSigmaText.get(f), 22, 22, 22)//
                    .add(fractionR18O_16OOneSigmaText.get(f), 22, 22, 22)//
                    .add(fractionPbBlankMassOneSigmaText.get(f), 22, 22, 22)//
                    .add(fractionRTh_UmagmaOneSigmaText.get(f), 22, 22, 22)//
                    .add(fractionAr231_235sampleOneSigmaText.get(f), 22, 22, 22)//
            );
        }

        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jPanel2Layout.createSequentialGroup().add(myHorizFraction)));

        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(myVerticalFraction));

    }

    /**
     *
     * @param aliquot
     * @param validUser
     */
    protected void showArchiveNote(AliquotInterface aliquot, boolean validUser) {
        // determine if aliquot in GeochronID
        GeochronValidationResults validateAliquot = GeochronValidationResults.invalidUser;

        try {
            if (validUser) {
                validateAliquot = confirmAliquotArchivedInGeochron(getMyAliquot());
            }
        } catch (Exception e) {
        }
        archiveNote_label.setText("Note: " + validateAliquot.getMessage());

        geochronOverwrite_chkBox.setSelected(true);//validateAliquot.compareTo(GeochronValidationResults.success) == 0);

        archiveNote_label.setForeground((Color) ((validateAliquot.compareTo(GeochronValidationResults.success) == 0) ? Color.red : Color.black));

        aliquotName_text.setEnabled(validateAliquot.compareTo(GeochronValidationResults.success) == 0);

        saveAndUploadAliquotToGeochron_button.setEnabled(true);/////// ! (validateAliquot.compareTo( GeochronValidationResults.errorIsPrivate ) == 0) );
    }

    /**
     *
     */
    protected void showSavedDataI() {
        // general info

        validateIGSNs();

        aliquotName_text.setText(getMyAliquot().getAliquotName());
        analystName_text.setText(getMyAliquot().getAnalystName());

        instrumentalMethod_jcombo.removeAllItems();
        for (int i = 0; i
                < DataDictionary.AliquotInstrumentalMethod.length; i++) {
            instrumentalMethod_jcombo.addItem(DataDictionary.AliquotInstrumentalMethod[i]);
        }

        instrumentalMethod_jcombo.setSelectedItem(getMyAliquot().
                getAliquotInstrumentalMethod().toString());

        instMethodRef_text.setText(getMyAliquot().getAliquotInstrumentalMethodReference());

        reference_text.setText(getMyAliquot().getAliquotReference());

        comment_textArea.setText(getMyAliquot().getAliquotComment());

        calibrationUnct206_238_text.setText(//
                getMyAliquot().getCalibrationUnct206_238()//
                .setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE, RoundingMode.HALF_UP).toPlainString());

        calibrationUnct208_232_text.setText(getMyAliquot().getCalibrationUnct208_232()//
                .setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE, RoundingMode.HALF_UP).toPlainString());

        calibrationUnct207_206_text.setText(getMyAliquot().getCalibrationUnct207_206()//
                .setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE, RoundingMode.HALF_UP).toPlainString());

        keyWordsCSV_text.setText( //
                getMyAliquot().getKeyWordsCSV());

        for (JComponent cb : mineralStandardsCheckBoxes) {
            if (getMyAliquot().getAMineralStandardModelByName(((AbstractButton) cb).getText()) != null) {
                ((AbstractButton) cb).setSelected(true);
            } else {
                ((AbstractButton) cb).setSelected(false);
            }
        }

    }

    protected void validateIGSNs() {
        
        // April 2011 now using xxx.sampleID for sampleIGSN and need to split off
        aliquotIGSN_text.setText(getMyAliquot().getAlliquotIGSNnoRegistry());

        if (sample.isValidatedSampleIGSN()) {
            checkMarkForValidSampleID_label.setVisible(true);
            xMarkForValidSampleID_label.setVisible(false);
            sampleIsRegistered_label.setText(//
                    sampleIsRegistered_label.getName()//
                    + " REGISTERED"//
                    + " as " + sample.getSampleIGSNnoRegistry() //
                    + " in " + sample.getSampleRegistry().getName()//
                    + "  (to change, use Manage Sample menu item)");
            geochronArchivePanel_panel.setVisible(true);
            showArchiveNote(myAliquot, true);
            concordiaUpload_chkBox.setSelected(getMyAliquot()//
                    .getAnalysisImageByType(AnalysisImageTypes.CONCORDIA).getImageURL().equalsIgnoreCase(""));

        } else {
            checkMarkForValidSampleID_label.setVisible(false);
            xMarkForValidSampleID_label.setVisible(true);
            sampleIsRegistered_label.setText(sampleIsRegistered_label.getName() + " NOT REGISTERED");
            geochronArchivePanel_panel.setVisible(false);
        }

        if (isSampleRegisteredToParentAtGeoSamples(getMyAliquot().getAliquotIGSN(), getMyAliquot().getSampleIGSNnoRegistry())) {
            checkMarkForValidAliquotIGSN_label.setVisible(true);
            xMarkForValidAliquotIGSN_label.setVisible(false);
            aliquotIsRegistered_label.setText(//
                    "Aliquot IGSN " + getMyAliquot().getAliquotIGSN()//
                    + " is REGISTERED"//
                    + " as child of sample IGSN " + sample.getSampleIGSNnoRegistry() //
                    + " in " + sample.getSampleRegistry().getName());
            geochronArchivePanel_panel.setVisible(true);
            showArchiveNote(myAliquot, true);
            concordiaUpload_chkBox.setSelected(getMyAliquot()//
                    .getAnalysisImageByType(AnalysisImageTypes.CONCORDIA).getImageURL().equalsIgnoreCase(""));

        } else {
            checkMarkForValidAliquotIGSN_label.setVisible(false);
            xMarkForValidAliquotIGSN_label.setVisible(true);
            aliquotIsRegistered_label.setText(//
                    "Aliquot IGSN " + getMyAliquot().getAliquotIGSN() + " is NOT REGISTERED as child of Sample IGSN " + sample.getSampleIGSNnoRegistry());
            geochronArchivePanel_panel.setVisible(false);
        }
    }

    /**
     *
     */
    protected void showSavedDataII() {

        showSavedDataI();

        // default master fields
        masterZirconCaseCheckBox.setSelected(getMyAliquot().getDefaultIsZircon());

        masterTracerChooser.setSelectedItem(getMyAliquot().getDefaultTracerID());
        if (masterTracerChooser.getSelectedIndex() == -1) {
            masterTracerChooser.setSelectedIndex(0);
        }

        masterTracerMass.setText(getMyAliquot().getDefaultTracerMassText());
        masterFractionMass.setText(getMyAliquot().getDefaultFractionMassText());

        masterAlphaPbChooser.setSelectedItem(getMyAliquot().getDefaultAlphaPbModelID());
        if (masterAlphaPbChooser.getSelectedIndex() == -1) {
            masterAlphaPbChooser.setSelectedIndex(-1);
        }

        masterAlphaUChooser.setSelectedItem(getMyAliquot().getDefaultAlphaUModelID());
        if (masterAlphaUChooser.getSelectedIndex() == -1) {
            masterAlphaUChooser.setSelectedIndex(-1);
        }

        masterPbBlankChooser.setSelectedItem(getMyAliquot().getDefaultPbBlankID());
        if (masterPbBlankChooser.getSelectedIndex() == -1) {
            masterPbBlankChooser.setSelectedIndex(-1);
        }

        masterInitialPbModelChooser.setSelectedItem(getMyAliquot().getDefaultInitialPbModelID());
        if (masterInitialPbModelChooser.getSelectedIndex() == -1) {
            masterInitialPbModelChooser.setSelectedIndex(-1);
        }

        masterEstimatedDate.setText(getMyAliquot().getDefaultEstimatedDateText());

        masterStaceyKramersPctUncertainty.setText(//
                getMyAliquot().getDefaultStaceyKramersOnePctUnctText());

        masterStaceyKramersCorrelationCoeffs.setText(//
                getMyAliquot().getDefaultStaceyKramersCorrelationCoeffsText());

        masterPbBlankMass.setText(getMyAliquot().getDefaultPbBlankMassText());

        masterUBlankMass.setText(getMyAliquot().getDefaultUBlankMassText());
        masterR238_235s.setText(getMyAliquot().getDefaultR238_235sText());
        masterR238_235b.setText(getMyAliquot().getDefaultR238_235bText());
        masterR18O_16O.setText(getMyAliquot().getDefaultR18O_16OText());

        masterRTh_Umagma.setText(getMyAliquot().getDefaultRTh_UmagmaText());

        masterAr231_235sample.setText(getMyAliquot().getDefaultAr231_235sampleText());

        // one-sigmas
        masterTracerMassOneSigma.setText(getMyAliquot().getDefaultTracerMassOneSigmaText());
        masterUBlankMassOneSigma.setText(getMyAliquot().getDefaultUBlankMassOneSigmaText());
        masterR238_235sOneSigma.setText(getMyAliquot().getDefaultR238_235sOneSigmaText());
        masterR238_235bOneSigma.setText(getMyAliquot().getDefaultR238_235bOneSigmaText());
        masterR18O_16OOneSigma.setText(getMyAliquot().getDefaultR18O_16OOneSigmaText());
        masterPbBlankMassOneSigma.setText(getMyAliquot().getDefaultPbBlankMassOneSigmaText());
        masterRTh_UmagmaOneSigma.setText(getMyAliquot().getDefaultRTh_UmagmaOneSigmaText());
        masterAr231_235sampleOneSigma.setText(getMyAliquot().getDefaultAr231_235sampleOneSigmaText());

        // fraction details
        for (int row = 0; row
                < getMyAliquot().getAliquotFractions().size(); row++) {
            updateFractionRow(getMyAliquot().getAliquotFractions().get(row), row);

        }

        // show first fraction info
        fraction_Chooser.setSelectedIndex(0);

    }

    private void setVisiblePublishTabComponents(boolean visible) {
        // turn off Publish Tab for Compilation Mode
        Component[] myComponents = publishAliquot_panel.getComponents();
        for (int i = 0; i < myComponents.length; i++) {
            myComponents[i].setVisible(visible);
        }
    }

    private void updateAlphaPbModelChooserForRow(ETFractionInterface tempFrac, boolean needsAlphaPb, int row) {
        // aug 2010 control alphaPb Chooser
        // if we don't meet the conditions for using alphaPb model (see also UPbFractionEditor)
        // then we reset model to none model (the first model)
        // this happens here to enforce backward compatibility since no setting
        // on this fast fraction tab will change whether this is true
        ValueModel alphaPbNoneModel = new ValueModel();
        ValueModel alphaPbDefaultModel = new ValueModel();
        try {
            alphaPbNoneModel = getMyAliquot().getMyReduxLabData().getNoneAlphaPbModel();
            alphaPbDefaultModel = getMyAliquot().getDefaultAlphaPbModel();
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }

        // set labels for disabled chooser
        String usesMean202_205 = "uses mean 202/205";
        String missing202_205 = "WARNING: missing 202/205; requires single-spiked tracer";
        String ptWiseTripoliCorrected = "pt-wise corr Tripoli";
        fractionAlphaPbChoice.get(row).removeItem(ptWiseTripoliCorrected);
        fractionAlphaPbChoice.get(row).removeItem(usesMean202_205);
        fractionAlphaPbChoice.get(row).removeItem(missing202_205);
        if (!needsAlphaPb) {
            ((UPbFraction) tempFrac).setAlphaPbModel(alphaPbNoneModel);
            fractionAlphaPbChoice.get(row).setEnabled(false);
            // display reason
            String reasonNoAlphaPbModel = usesMean202_205;
            if (((UPbFraction) tempFrac).isFractionationCorrectedPb()) {
                reasonNoAlphaPbModel = ptWiseTripoliCorrected;
            } else if (!((UPbFraction) tempFrac).hasMeasured202_205()) {
                reasonNoAlphaPbModel = missing202_205;
            }
            fractionAlphaPbChoice.get(row).addItem(reasonNoAlphaPbModel);
            fractionAlphaPbChoice.get(row).setSelectedItem(reasonNoAlphaPbModel);
        } else {
            // if model is "none" use default otherwise proceed
            if (((UPbFractionI) tempFrac).getAlphaPbModel().equals(alphaPbNoneModel)) {
                ((UPbFractionI) tempFrac).setAlphaPbModel(alphaPbDefaultModel);
            }
            fractionAlphaPbChoice.get(row).setEnabled(true);
            fractionAlphaPbChoice.get(row).setSelectedIndex(0);
            fractionAlphaPbChoice.get(row).setSelectedItem(((UPbFractionI) tempFrac).getAlphaPbModel().getName());

        }
    }

    private void updateAlphaUModelChooserForRow(ETFractionInterface tempFrac, boolean needsAlphaU, int row) {
        // sept 2010 altered to parallel alphaPb case
        ValueModel alphaUNoneModel = new ValueModel();
        ValueModel alphaUDefaultModel = new ValueModel();
        try {
            alphaUNoneModel = getMyAliquot().getMyReduxLabData().getNoneAlphaUModel();
            alphaUDefaultModel = getMyAliquot().getDefaultAlphaUModel();
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }

        // set labels for disabled chooser
        String usesRatioMeans = "uses ratio means";
        String missingRatioMeans = "WARNING: no measured ratios; requires single-spiked tracer";
        String ptWiseTripoliCorrected = "pt-wise corr Tripoli";
        fractionAlphaUChoice.get(row).removeItem(ptWiseTripoliCorrected);
        fractionAlphaUChoice.get(row).removeItem(usesRatioMeans);
        fractionAlphaUChoice.get(row).removeItem(missingRatioMeans);
        if (!needsAlphaU) {
            ((UPbFractionI) tempFrac).setAlphaUModel(alphaUNoneModel);
            fractionAlphaUChoice.get(row).setEnabled(false);
            // display reason or warning
            String reasonNoAlphaUModel = usesRatioMeans;
            if (((FractionI) tempFrac).isFractionationCorrectedU()) {
                reasonNoAlphaUModel = ptWiseTripoliCorrected;
            } else if (!((UPbFractionI) tempFrac).hasMeasuredUranium()) {
                reasonNoAlphaUModel = missingRatioMeans;
            }
            fractionAlphaUChoice.get(row).addItem(reasonNoAlphaUModel);
            fractionAlphaUChoice.get(row).setSelectedItem(reasonNoAlphaUModel);
        } else {
            // if model is "none" use default otherwise proceed
            if (((UPbFractionI) tempFrac).getAlphaUModel().equals(alphaUNoneModel)) {
                ((UPbFractionI) tempFrac).setAlphaUModel(alphaUDefaultModel);
            }

            fractionAlphaUChoice.get(row).setEnabled(true);
            fractionAlphaUChoice.get(row).setSelectedIndex(0);
            fractionAlphaUChoice.get(row).setSelectedItem(((UPbFractionI) tempFrac).getAlphaUModel().getName());

        }
    }

    private void updateInitialPbModelChooserForRow(ETFractionInterface tempFrac, boolean isZircon, int row) {

        AbstractRatiosDataModel initialPbNoneModel = InitialPbModelET.getNoneInstance();
        AbstractRatiosDataModel initialPbDefaultModel = getMyAliquot().getDefaultInitialPbModel();

        // set labels for disabled chooser
        String fractionIsZircon = "fraction is a Zircon";
        fractionInitialPbChoice.get(row).removeItem(fractionIsZircon);
        if (isZircon) {
            ((FractionI) tempFrac).setInitialPbModel(initialPbNoneModel);
            fractionInitialPbChoice.get(row).setEnabled(false);
            // display reason or warning
            String reasonNoInitialPb = fractionIsZircon;
            fractionInitialPbChoice.get(row).addItem(reasonNoInitialPb);
            fractionInitialPbChoice.get(row).setSelectedItem(reasonNoInitialPb);
        } else {
            // if model is "none" use default otherwise proceed
            if (((FractionI) tempFrac).getInitialPbModel().equals(initialPbNoneModel)) {
                ((FractionI) tempFrac).setInitialPbModel(initialPbDefaultModel);
            }
            fractionInitialPbChoice.get(row).setEnabled(true);
            fractionInitialPbChoice.get(row).setSelectedIndex(0);
            fractionInitialPbChoice.get(row).setSelectedItem(((FractionI) tempFrac).getInitialPbModel().getReduxLabDataElementName());
        }

        boolean hasStaceyKramersModel = ((FractionI) tempFrac).getInitialPbModel() instanceof StaceyKramersInitialPbModelET;

        ((JTextComponent) fractionEstDateText.get(row)).setText(tempFrac.getEstimatedDate().
                setScale(ReduxConstants.DEFAULT_DATE_MA_SCALE, RoundingMode.HALF_UP).//
                toPlainString());
        fractionEstDateText.get(row).setEnabled(hasStaceyKramersModel);

        ((JTextComponent) fractionStaceyKramersPctUncertaintyText.get(row)).//
                setText(((FractionI) tempFrac).getStaceyKramersOnePctUnct().
                        setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE, RoundingMode.HALF_UP).//
                        toPlainString());
        fractionStaceyKramersPctUncertaintyText.get(row).setEnabled(hasStaceyKramersModel);

        ((JTextComponent) fractionStaceyKramersCorrelationCoeffsText.get(row)).//
                setText(((FractionI) tempFrac).getStaceyKramersCorrelationCoeffs().
                        setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE, RoundingMode.HALF_UP).//
                        toPlainString());
        fractionStaceyKramersCorrelationCoeffsText.get(row).setEnabled(hasStaceyKramersModel);

        ((JTextComponent) fractionPbBlankMassText.get(row)).setText(tempFrac.getAnalysisMeasure(AnalysisMeasures.pbBlankMassInGrams.getName()).getValue().multiply(ReduxConstants.PicoGramsPerGram).setScale(ReduxConstants.DEFAULT_MASS_IN_GRAMS_DISPLAY_SCALE,
                RoundingMode.HALF_UP).toPlainString());
        ((UnDoAbleDocument) ((JTextComponent) fractionPbBlankMassText.get(row)).getDocument()).undo.discardAllEdits();
        fractionPbBlankMassText.get(row).setEnabled(!isZircon);

        ((JTextComponent) fractionEstDateText.get(row)).setText(tempFrac.getEstimatedDate().
                setScale(ReduxConstants.DEFAULT_DATE_MA_SCALE, RoundingMode.HALF_UP).//
                toPlainString());
        ((UnDoAbleDocument) ((JTextComponent) fractionEstDateText.get(row)).getDocument()).undo.discardAllEdits();
        fractionEstDateText.get(row).setEnabled(!isZircon);

    }

    private void updateFractionRow(ETFractionInterface tempFrac, int row) {

        // set temp variable for fractionation correction both u and Pb to use in locking fields
        boolean fraCorrU = ((FractionI) tempFrac).isFractionationCorrectedU();//.getMeanAlphaU().compareTo( BigDecimal.ZERO ) != 0);
        boolean fraCorrPb = ((FractionI) tempFrac).isFractionationCorrectedPb();//.getMeanAlphaPb().compareTo( BigDecimal.ZERO ) != 0);
        boolean isZircon = ((FractionI) tempFrac).isZircon();

        ((AbstractButton) fractionEditButtons.get(row)).setText(tempFrac.getFractionID());

        ((AbstractButton) fractionZirconCheckBox.get(row)).setSelected(isZircon);

        fractionTracerChoice.get(row).setSelectedItem(((FractionI) tempFrac).getTracerID());
        fractionTracerChoice.get(row).setEnabled(!(fraCorrU || fraCorrPb));

        ((JTextComponent) fractionTracerMassText.get(row)).setText(tempFrac.getAnalysisMeasure(AnalysisMeasures.tracerMassInGrams.getName()).getValue().
                setScale(ReduxConstants.DEFAULT_MASS_IN_GRAMS_DISPLAY_SCALE,
                        RoundingMode.HALF_UP).toPlainString());
        fractionTracerMassText.get(row).setEnabled(!(fraCorrU));

        // feb 2022 issue #206 fraction mass units switch to micrograms from grams
//        ((JTextComponent) fractionMassText.get(row)).setText(tempFrac.getAnalysisMeasure(AnalysisMeasures.fractionMass.getName()).getValue().
//                setScale(ReduxConstants.DEFAULT_MASS_IN_GRAMS_DISPLAY_SCALE, RoundingMode.HALF_UP).//
//                toPlainString());
        ((JTextComponent) fractionMassText.get(row)).setText(tempFrac.getAnalysisMeasure(AnalysisMeasures.fractionMass.getName())
                .getValue().movePointRight(6).
                setScale(ReduxConstants.DEFAULT_MASS_IN_MICRO_GRAMS_DISPLAY_SCALE, RoundingMode.HALF_UP).//
                toPlainString());

        updateAlphaPbModelChooserForRow(tempFrac, ((UPbFraction) tempFrac).needsAlphaPbModel(), row);
        updateAlphaUModelChooserForRow(tempFrac, ((UPbFraction) tempFrac).needsAlphaUModel(), row);

        // PbBlank choice
        fractionPbBlankChoice.get(row).setSelectedItem(((UPbFraction) tempFrac).getPbBlank().getReduxLabDataElementName());

        updateInitialPbModelChooserForRow(tempFrac, isZircon, row);

        ((JTextField) fractionPbBlankMassText.get(row)).setText(tempFrac.getAnalysisMeasure(AnalysisMeasures.pbBlankMassInGrams.getName()).getValue().multiply(ReduxConstants.PicoGramsPerGram).setScale(ReduxConstants.DEFAULT_MASS_IN_GRAMS_DISPLAY_SCALE,
                RoundingMode.HALF_UP).toPlainString());
        fractionPbBlankMassText.get(row).setEnabled(!isZircon);

        ((JTextField) fractionUBlankMassText.get(row)).setText(tempFrac.getAnalysisMeasure(AnalysisMeasures.uBlankMassInGrams.getName()).getValue().multiply(ReduxConstants.PicoGramsPerGram).setScale(ReduxConstants.DEFAULT_MASS_IN_GRAMS_DISPLAY_SCALE,
                RoundingMode.HALF_UP).toPlainString());
        fractionUBlankMassText.get(row).setEnabled(!(fraCorrU));

        ((JTextField) fractionR238_235sText.get(row)).setText(tempFrac.getAnalysisMeasure(AnalysisMeasures.r238_235s.getName()).getValue().
                setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE, RoundingMode.HALF_UP).//
                toPlainString());
        fractionR238_235sText.get(row).setEnabled(!(fraCorrU));

        ((JTextField) fractionR238_235bText.get(row)).setText(tempFrac.getAnalysisMeasure(AnalysisMeasures.r238_235b.getName()).getValue().
                setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE, RoundingMode.HALF_UP).//
                toPlainString());
        fractionR238_235bText.get(row).setEnabled(!(fraCorrU));

        ((JTextField) fractionR18O_16OText.get(row)).setText(tempFrac.getAnalysisMeasure(AnalysisMeasures.r18O_16O_revised.getName()).getValue().
                setScale(ReduxConstants.DEFAULT_DEFAULTS_SCALE, RoundingMode.HALF_UP).//
                toPlainString());
        fractionR18O_16OText.get(row).setEnabled(((UPbFraction) tempFrac).isAnOxide());

        ((JTextField) fractionRTh_UmagmaText.get(row)).setText(tempFrac.getAnalysisMeasure(AnalysisMeasures.rTh_Umagma.getName()).getValue().
                setScale(ReduxConstants.DEFAULT_MASS_IN_GRAMS_DISPLAY_SCALE, RoundingMode.HALF_UP).//
                toPlainString());
        fractionRTh_UmagmaText.get(row).setEnabled(true);

        ((JTextField) fractionAR231_235sampleText.get(row)).setText(tempFrac.getAnalysisMeasure(AnalysisMeasures.ar231_235sample.getName()).getValue().
                setScale(ReduxConstants.DEFAULT_MASS_IN_GRAMS_DISPLAY_SCALE, RoundingMode.HALF_UP).//
                toPlainString());
        fractionAR231_235sampleText.get(row).setEnabled(true);

        //  uncertainties
        ((JTextField) fractionTracerMassOneSigmaText.get(row)).setText(tempFrac.getAnalysisMeasure(AnalysisMeasures.tracerMassInGrams.getName()).getOneSigmaAbs().
                setScale(ReduxConstants.DEFAULT_MASS_IN_GRAMS_DISPLAY_SCALE,
                        RoundingMode.HALF_UP).toPlainString());

        ((JTextField) fractionUBlankMassOneSigmaText.get(row)).setText(tempFrac.getAnalysisMeasure(AnalysisMeasures.uBlankMassInGrams.getName()).getOneSigmaAbs().//
                multiply(ReduxConstants.PicoGramsPerGram).
                setScale(ReduxConstants.DEFAULT_MASS_IN_GRAMS_DISPLAY_SCALE, RoundingMode.HALF_UP).//
                toPlainString());

        ((JTextField) fractionR238_235sOneSigmaText.get(row)).setText(tempFrac.getAnalysisMeasure(AnalysisMeasures.r238_235s.getName()).getOneSigmaAbs().
                setScale(ReduxConstants.DEFAULT_MASS_IN_GRAMS_DISPLAY_SCALE,
                        RoundingMode.HALF_UP).toPlainString());

        ((JTextField) fractionR238_235bOneSigmaText.get(row)).setText(tempFrac.getAnalysisMeasure(AnalysisMeasures.r238_235b.getName()).getOneSigmaAbs().
                setScale(ReduxConstants.DEFAULT_MASS_IN_GRAMS_DISPLAY_SCALE,
                        RoundingMode.HALF_UP).toPlainString());

        ((JTextField) fractionPbBlankMassOneSigmaText.get(row)).setText(tempFrac.getAnalysisMeasure(AnalysisMeasures.pbBlankMassInGrams.getName()).getOneSigmaAbs().
                multiply(ReduxConstants.PicoGramsPerGram).
                setScale(ReduxConstants.DEFAULT_MASS_IN_GRAMS_DISPLAY_SCALE,
                        RoundingMode.HALF_UP).toPlainString());
        fractionPbBlankMassOneSigmaText.get(row).setEnabled(!isZircon);

        ((JTextField) fractionR18O_16OOneSigmaText.get(row)).setText(tempFrac.getAnalysisMeasure(AnalysisMeasures.r18O_16O_revised.getName()).getOneSigmaAbs().
                setScale(ReduxConstants.DEFAULT_DEFAULTS_SCALE,
                        RoundingMode.HALF_UP).toPlainString());
        fractionR18O_16OOneSigmaText.get(row).setEnabled(((UPbFraction) tempFrac).isAnOxide());

        ((JTextField) fractionRTh_UmagmaOneSigmaText.get(row)).setText(tempFrac.getAnalysisMeasure(AnalysisMeasures.rTh_Umagma.getName()).getOneSigmaAbs().
                setScale(ReduxConstants.DEFAULT_MASS_IN_GRAMS_DISPLAY_SCALE,
                        RoundingMode.HALF_UP).toPlainString());

        ((JTextField) fractionAr231_235sampleOneSigmaText.get(row)).setText(tempFrac.getAnalysisMeasure(AnalysisMeasures.ar231_235sample.getName()).getOneSigmaAbs().
                setScale(ReduxConstants.DEFAULT_MASS_IN_GRAMS_DISPLAY_SCALE,
                        RoundingMode.HALF_UP).toPlainString());

    }

    private void removeFractionRow(int row) {
//        fractionDeleteButtons.remove(row);
        fractionEditButtons.remove(row);
        fractionZirconCheckBox.remove(row);
        fractionTracerChoice.remove(row);
        fractionTracerMassText.remove(row);
        fractionMassText.remove(row);
        fractionAlphaPbChoice.remove(row);
        fractionAlphaUChoice.remove(row);
        fractionPbBlankChoice.remove(row);
        fractionInitialPbChoice.remove(row);
        fractionEstDateText.remove(row);
        fractionStaceyKramersPctUncertaintyText.remove(row);
        fractionStaceyKramersCorrelationCoeffsText.remove(row);

        fractionPbBlankMassText.remove(row);
        fractionUBlankMassText.remove(row);
        fractionR238_235sText.remove(row);
        fractionR238_235bText.remove(row);
        fractionR18O_16OText.remove(row);
        fractionRTh_UmagmaText.remove(row);
        fractionAR231_235sampleText.remove(row);

        // uncertainties
        fractionTracerMassOneSigmaText.remove(row);
        fractionUBlankMassOneSigmaText.remove(row);
        fractionR238_235sOneSigmaText.remove(row);
        fractionR238_235bOneSigmaText.remove(row);
        fractionPbBlankMassOneSigmaText.remove(row);
        fractionRTh_UmagmaOneSigmaText.remove(row);
        fractionAr231_235sampleOneSigmaText.remove(row);
        fractionR18O_16OOneSigmaText.remove(row);

        // fix row pointers in buttonfractionDeleteButtonss
        for (int f = 0; f < fractionEditButtons.size(); f++) {
//            Fraction myFraction =
//                    ((DeleteFractionListener) ((JButton) fractionDeleteButtons.get(f)).getActionListeners()[0]).getFraction();
//
//            ((JButton) fractionDeleteButtons.get(f)).removeActionListener(((JButton) fractionDeleteButtons.get(f)).getActionListeners()[0]);
//            ((JButton) fractionDeleteButtons.get(f)).addActionListener(new DeleteFractionListener(myFraction, f));

            ETFractionInterface fraction
                    = ((EditFractionListener) ((JButton) fractionEditButtons.get(f)).getActionListeners()[0]).getFraction();

            ((AbstractButton) fractionEditButtons.get(f)).removeActionListener(((JButton) fractionEditButtons.get(f)).getActionListeners()[0]);
            ((AbstractButton) fractionEditButtons.get(f)).addActionListener(new EditFractionListener(fraction, f));

            ((AbstractButton) fractionZirconCheckBox.get(f)).removeChangeListener(((JCheckBox) fractionZirconCheckBox.get(f)).getChangeListeners()[0]);
            ((AbstractButton) fractionZirconCheckBox.get(f)).addChangeListener(new ChangeIsZirconListener(fraction, f));

            fractionTracerChoice.get(f).removeActionListener(fractionTracerChoice.get(f).getActionListeners()[0]);
            fractionTracerChoice.get(f).addActionListener(new ChangeTracerListener(fraction, f));

            fractionInitialPbChoice.get(f).removeItemListener(fractionInitialPbChoice.get(f).getItemListeners()[0]);
            fractionInitialPbChoice.get(f).addItemListener(new ChangeInitialPbModelItemListener(fraction, f));
        }

    }

    private void saveFractionArchivingData(ETFractionInterface myFraction) {

        // publication details
        ((FractionI) myFraction).setMineralName((String) mineralNameChooser.getSelectedItem());
        ((FractionI) myFraction).setSettingType((String) settingTypeChooser.getSelectedItem());
        myFraction.setNumberOfGrains(Integer.parseInt(countOfGrains_text.getText()));

        ((FractionI) myFraction).setPhysicallyAbraded(physicallyAbraded_chkBox.isSelected());
        ((FractionI) myFraction).setLeachedInHFAcid(leachedInHFAcid_chkBox.isSelected());
        ((FractionI) myFraction).setAnnealedAndChemicallyAbraded(annealedChemicallyAbraded_chkBox.isSelected());
        ((FractionI) myFraction).setChemicallyPurifiedUPb(chemicallyPurifiedUPb_chkBox.isSelected());
        myFraction.setAnalysisFractionComment(fractionComment_text.getText());

    }

    private void saveAliquot() {

        // in compilation, nothing to save
        if (((ReduxAliquotInterface) myAliquot).isCompiled()) {
            return;
        }

        // general info
        myAliquot.setAliquotName(aliquotName_text.getText().trim());
        this.setTitle("Aliquot # " + getMyAliquot().getAliquotNumber() + " <> " + getMyAliquot().getAliquotName());

        getMyAliquot().setAnalystName(analystName_text.getText());
        getMyAliquot().getMyReduxLabData().setAnalystName(analystName_text.getText());
        getMyAliquot().setAliquotInstrumentalMethod(
                instrumentalMethod_jcombo.getSelectedItem().toString());
        getMyAliquot().setAliquotInstrumentalMethodReference(instMethodRef_text.getText());
        getMyAliquot().setAliquotReference(reference_text.getText());
        getMyAliquot().setAliquotComment(comment_textArea.getText());

        // calibration and mineral standards
        getMyAliquot().setCalibrationUnct206_238(new BigDecimal(calibrationUnct206_238_text.getText(), ReduxConstants.mathContext15));
        getMyAliquot().setCalibrationUnct208_232(new BigDecimal(calibrationUnct208_232_text.getText(), ReduxConstants.mathContext15));
        getMyAliquot().setCalibrationUnct207_206(new BigDecimal(calibrationUnct207_206_text.getText(), ReduxConstants.mathContext15));

        getMyAliquot().getMineralStandardModels().clear();
        for (JComponent cb : mineralStandardsCheckBoxes) {
            if (((AbstractButton) cb).isSelected()) {
                try {
                    getMyAliquot().getMineralStandardModels().add(//
                            ReduxLabData.getInstance().getAMineralStandardModel(((AbstractButton) cb).getText()));
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

        if (!sample.isSampleTypeLegacy() && !sample.isAnalysisTypeLAICPMS()) {
            // master fields
            getMyAliquot().setDefaultIsZircon(masterZirconCaseCheckBox.isSelected());
            getMyAliquot().setDefaultTracerID((String) masterTracerChooser.getSelectedItem());
            getMyAliquot().setDefaultTracerMassText(masterTracerMass.getText());
            getMyAliquot().setDefaultFractionMassText(masterFractionMass.getText());
            getMyAliquot().setDefaultAlphaPbModelID((String) masterAlphaPbChooser.getSelectedItem());
            getMyAliquot().setDefaultAlphaUModelID((String) masterAlphaUChooser.getSelectedItem());

            getMyAliquot().setDefaultPbBlankID((String) masterPbBlankChooser.getSelectedItem());
            getMyAliquot().setDefaultInitialPbModelID((String) masterInitialPbModelChooser.getSelectedItem());
            getMyAliquot().setDefaultEstimatedDateText(masterEstimatedDate.getText());
            getMyAliquot().setDefaultStaceyKramersOnePctUnctText(//
                    masterStaceyKramersPctUncertainty.getText());
            getMyAliquot().setDefaultStaceyKramersCorrelationCoeffsText(//
                    masterStaceyKramersCorrelationCoeffs.getText());

            getMyAliquot().setDefaultPbBlankMassText(masterPbBlankMass.getText());
            getMyAliquot().setDefaultUBlankMassText(masterUBlankMass.getText());

            getMyAliquot().setDefaultR238_235sText(masterR238_235s.getText());
            getMyAliquot().setDefaultR238_235bText(masterR238_235b.getText());
            getMyAliquot().setDefaultR18O_16OText(masterR18O_16O.getText());

            getMyAliquot().setDefaultRTh_UmagmaText(masterRTh_Umagma.getText());
            getMyAliquot().setDefaultAr231_235sampleText(masterAr231_235sample.getText());

            // uncertainties
            getMyAliquot().setDefaultTracerMassOneSigmaText(masterTracerMassOneSigma.getText());
            getMyAliquot().setDefaultUBlankMassOneSigmaText(masterUBlankMassOneSigma.getText());
            getMyAliquot().setDefaultR238_235sOneSigmaText(masterR238_235sOneSigma.getText());
            getMyAliquot().setDefaultR238_235bOneSigmaText(masterR238_235bOneSigma.getText());
            getMyAliquot().setDefaultPbBlankMassOneSigmaText(masterPbBlankMassOneSigma.getText());
            getMyAliquot().setDefaultR18O_16OOneSigmaText(masterR18O_16OOneSigma.getText());
            getMyAliquot().setDefaultRTh_UmagmaOneSigmaText(masterRTh_UmagmaOneSigma.getText());
            getMyAliquot().setDefaultAr231_235sampleOneSigmaText(masterAr231_235sampleOneSigma.getText());

            //  }
            for (ETFractionInterface f : getMyAliquot().getAliquotFractions()) {
                saveAliquotFraction((FractionI) f);
            }
        }//march 2014 temp for raw laicpms

        for (ETFractionInterface f : getMyAliquot().getAliquotFractions()) {
            f.setTimeStamp(new Date(System.currentTimeMillis()));
        }

        saveFractionArchivingData(myFraction);

        // save the sample
        // april 2011 no longer provide edit of sample igsn in this window
//        getMyAliquot().setSampleIGSN( aliquotIGSN_text.getText().trim() );
//        sample.setSampleIGSN( aliquotIGSN_text.getText().trim() );
//
        // nov 2011 per Doug Walker, need to be sure lab name is correct
        myAliquot.setLaboratoryName(ReduxLabData.getInstance().getLabName());

        // sept 2016
        myAliquot.setAliquotIGSN(aliquotIGSN_text.getText().trim());
               
        validateIGSNs();
        
        SampleInterface.saveSampleAsSerializedReduxFile(sample);

        System.out.println("**************** PRE-PUBLISH CHECKLIST FOR ALIQUOT");

    } // saveAliquot

    /**
     *
     * @param tableRows
     * @param max
     * @return
     */
    protected JTextField insertTableTextField(
            final ArrayList<JComponent> tableRows, final int max) {

        final JTextField tableTextField = new JTextField();
        tableTextField.setDocument(new BigDecimalDocument(tableTextField, true));
        tableTextField.setHorizontalAlignment(JTextField.RIGHT);
        tableRows.add(tableTextField);

        modifyComponentKeyMapForTable(tableTextField, tableRows, max);

        return tableTextField;
    }

    /**
     *
     * @param tableComponent
     * @param tableRows
     * @param max
     */
    protected void modifyComponentKeyMapForTable(
            final JComponent tableComponent,
            final ArrayList<JComponent> tableRows,
            final int max) {

        tableComponent.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "upArrow");
        tableComponent.getActionMap().put("upArrow",
                new AbstractAction("upArrow") {
            public void actionPerformed(ActionEvent evt) {
                int nextUp = tableRows.indexOf(tableComponent);
                if (nextUp > 0) {
                    nextUp--;
                }

                tableRows.get(nextUp).grabFocus();
            }
        });

        tableComponent.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "downArrow");
        tableComponent.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "downArrow");
        tableComponent.getActionMap().put("downArrow",
                new AbstractAction("downArrow") {
            public void actionPerformed(ActionEvent evt) {
                int nextDown = tableRows.indexOf(tableComponent);
                if ((nextDown + 1) < max) {
                    nextDown++;
                }

                tableRows.get(nextDown).grabFocus();

            }
        });

    }

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
    public UPbReduxAliquot getMyAliquot() {
        return (UPbReduxAliquot) myAliquot;
    }

    /**
     *
     * @param myAliquot
     */
    public void setMyAliquot(AliquotInterface myAliquot) {
        this.myAliquot = myAliquot;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel SampleIGSN_label;
    protected javax.swing.JTextField aliquotIGSN_text;
    private javax.swing.JLabel aliquotIName_label;
    private javax.swing.JLabel aliquotIsRegistered_label;
    protected javax.swing.JTextField aliquotName_text;
    private javax.swing.JLabel analystName_label;
    protected javax.swing.JTextField analystName_text;
    private javax.swing.JCheckBox annealedChemicallyAbraded_chkBox;
    private javax.swing.JLabel archiveNote_label;
    private javax.swing.JLabel archivePanelTitle_label;
    private javax.swing.JLabel archivePanelTitle_label1;
    private javax.swing.JPanel archivingDetails_panel;
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JLabel calibrationUnct206_238_label;
    private javax.swing.JTextField calibrationUnct206_238_text;
    private javax.swing.JLabel calibrationUnct207_206_label;
    private javax.swing.JTextField calibrationUnct207_206_text;
    private javax.swing.JLabel calibrationUnct208_232_label;
    private javax.swing.JTextField calibrationUnct208_232_text;
    private javax.swing.JLabel checkMarkForValidAliquotIGSN_label;
    private javax.swing.JLabel checkMarkForValidGeoPassID_label;
    private javax.swing.JLabel checkMarkForValidSampleID_label;
    private javax.swing.JCheckBox chemicallyPurifiedUPb_chkBox;
    private javax.swing.JLabel chooseMineral_label;
    private javax.swing.JLabel chooseSettingType_label;
    private javax.swing.JLabel chooseSettingType_label1;
    private javax.swing.JLabel chooseUploadOptions_label;
    private javax.swing.JButton close_button;
    private javax.swing.JLabel comment_label;
    private javax.swing.JLabel comment_label1;
    protected javax.swing.JTextArea comment_textArea;
    private javax.swing.JCheckBox concordiaUpload_chkBox;
    private javax.swing.JLabel countOfGrains_label;
    private javax.swing.JTextField countOfGrains_text;
    private javax.swing.JTabbedPane details_tabbedPane;
    protected javax.swing.JButton exportXMLAliquot_button;
    protected javax.swing.JPanel fastEdits_panel;
    private javax.swing.JScrollPane fastEdits_scrollPane;
    private javax.swing.JLabel fractionComment_label;
    private javax.swing.JTextArea fractionComment_text;
    private javax.swing.JLabel fractionID_label1;
    private javax.swing.JLabel fractionID_label2;
    private javax.swing.JScrollPane fractionNotes_scrollPane;
    private javax.swing.JTextArea fractionNotes_textArea;
    private javax.swing.JComboBox<ETFractionInterface> fraction_Chooser;
    private javax.swing.JPasswordField geoPassPassword_passwordField;
    private javax.swing.JTextField geoPassUserName_text;
    private javax.swing.JLayeredPane geochronArchivePanel_panel;
    private javax.swing.JLabel geochronIcon_label;
    private javax.swing.JCheckBox geochronOverwrite_chkBox;
    private javax.swing.JCheckBox geochronOverwrite_chkBox2;
    private javax.swing.JCheckBox geochronOverwrite_chkBox5;
    private javax.swing.JCheckBox geochronPublicRecord_chkBox;
    private javax.swing.JPanel imageThumbnail_Panel;
    private javax.swing.JLabel imageThumbnail_label;
    private javax.swing.JLabel instMethodRef_label;
    protected javax.swing.JTextField instMethodRef_text;
    protected javax.swing.JComboBox<String> instrumentalMethod_jcombo;
    private javax.swing.JLabel instrumentalMethod_label;
    private javax.swing.JLabel instrumentalMethod_label1;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    protected javax.swing.JTextField keyWordsCSV_text;
    private javax.swing.JLabel keyWords_label;
    private javax.swing.JCheckBox leachedInHFAcid_chkBox;
    private javax.swing.JEditorPane measuredRatioValidity_JEditorPanel;
    private javax.swing.JComboBox<String> mineralNameChooser;
    private javax.swing.JLayeredPane mineralStandardsPanel;
    private javax.swing.JLabel passwordGeochron_label1;
    private javax.swing.JCheckBox physicallyAbraded_chkBox;
    private javax.swing.JCheckBox probabilityDensityUpload_chkBox;
    private javax.swing.JLabel publicationTimeStamp_label;
    private javax.swing.JTextField publicationTimeStamp_text;
    private javax.swing.JLayeredPane publishAliquot_panel;
    private javax.swing.JLabel reference_label;
    protected javax.swing.JTextField reference_text;
    private javax.swing.JPanel refsAndComment_panel;
    private javax.swing.ButtonGroup registryButtonGroup;
    private javax.swing.JCheckBox reportAsCSV_chkBox;
    private javax.swing.JCheckBox reportSVGUpload_chkbox;
    protected javax.swing.JButton restore_button;
    private javax.swing.JLabel sampleIsRegistered_label;
    protected javax.swing.JButton saveAndClose_button;
    protected javax.swing.JButton saveAndPreviewXMLAliquotAsHTML_button;
    protected javax.swing.JButton saveAndUploadAliquotToGeochron_button;
    protected javax.swing.JButton save_button;
    private javax.swing.JButton selectImageFile_button;
    private javax.swing.JComboBox<String> settingTypeChooser;
    protected javax.swing.JPanel title_panel;
    protected javax.swing.JButton useLabDefaults_button;
    private javax.swing.JLabel userNameGeochron_label;
    private javax.swing.JButton validateGeoPassID_button;
    private javax.swing.JButton visitGeochron_button;
    private javax.swing.JLabel xMarkForValidAliquotIGSN_label;
    private javax.swing.JLabel xMarkForValidGeoPassID_label;
    private javax.swing.JLabel xMarkForValidSampleID_label;
    // End of variables declaration//GEN-END:variables

    /**
     * Provides for repainting pane when scrollbar is used
     */
    class JscrollPaneScrollListener implements AdjustmentListener {
        // This method is called whenever the value of a scrollbar is changed,
        // either by the user or programmatically.

        @Override
        public void adjustmentValueChanged(AdjustmentEvent evt) {
            Adjustable source = evt.getAdjustable();

            // getValueIsAdjusting() returns true if the user is currently
            // dragging the scrollbar's knob and has not picked a final value
            if (evt.getValueIsAdjusting()) {
                // The user is dragging the knob
                return;
            } else {
                ((Component) source).getParent().repaint();
            }

            // Determine the type of event
            int type = evt.getAdjustmentType();
            switch (type) {
                case AdjustmentEvent.UNIT_INCREMENT:
                    // Scrollbar was increased by one unit
                    break;
                case AdjustmentEvent.UNIT_DECREMENT:
                    // Scrollbar was decreased by one unit
                    break;
                case AdjustmentEvent.BLOCK_INCREMENT:
                    // Scrollbar was increased by one block
                    break;
                case AdjustmentEvent.BLOCK_DECREMENT:
                    // Scrollbar was decreased by one block
                    break;
                case AdjustmentEvent.TRACK:
                    // The knob on the scrollbar was dragged
                    break;
            }

            // Get current value
            int value = evt.getValue();
        }
    }

    private void validateGeoPassID(boolean isVerbose) {

        String userCode = GeoPassIDValidator.validateGeoPassID(//
                geoPassUserName_text.getText().trim(),//
                new String(geoPassPassword_passwordField.getPassword()),
                isVerbose);

        boolean valid = (userCode.trim().length() > 0) && (!userCode.equalsIgnoreCase("NONEXXXXX"));

        showArchiveNote(getMyAliquot(), valid);

        if (valid) {
            checkMarkForValidGeoPassID_label.setVisible(true);
            xMarkForValidGeoPassID_label.setVisible(false);
            validateGeoPassID_button.setName("true");
        } else {
            checkMarkForValidGeoPassID_label.setVisible(false);
            xMarkForValidGeoPassID_label.setVisible(true);
            validateGeoPassID_button.setName("false");
            saveAndUploadAliquotToGeochron_button.setEnabled(false);
        }
    }

    /**
     *
     * https://www.geochron.org/post_to_redux_service.html
     */
    public void uploadAliquotToGeochronZip() throws ETException {

        String userName = ((ETReduxFrame) parent).getMyState().getReduxPreferences().getGeochronUserName();
        String password = ((ETReduxFrame) parent).getMyState().getReduxPreferences().getGeochronPassWord();

        String data = "";

        for (ETFractionInterface f : ((ReduxAliquotInterface) myAliquot).getAliquotFractions()) {
            // first we upload the thumbnail of the image if it exists and get back a url
            if (f.getImageURL().startsWith("http://thevaccinator.com/earth-time.org/public-data/images/ZirconCrystal.jpg")) {
                f.setImageURL("");
            }

            if (f.getImageURL().length() > 10) {

                //generate the thumbnail file
                BufferedImage scaledImage = Thumbnail.createThumbnailFromImage(f.getImageURL(), 256);
                if (scaledImage != null) {
                    try {
                        ImageIO.write(scaledImage, "jpg", new File("TempThumbnail.jpg"));
                    } catch (IOException iOException) {
                    }
                    File uploadFile = new File("TempThumbnail.jpg");

                    f.setImageURL(GeochronUploadImagesHelper.uploadImage(//
                            uploadFile, //
                            userName, //
                            password,
                            AnalysisImageTypes.PHOTO));
                } else {
                    // march 2013 Doug Walker had a fraction with a imageuri at geochron?
                    f.setImageURL("");

                }
            }

        }

        // nov 2010
        // decide which images to create and upload
        if (concordiaUpload_chkBox.isSelected()) {
            File tempConcordiaSVGforUploading = new File("tempConcordia.svg");

            ConcordiaGraphPanel concordiaGraphPanel = ((ConcordiaGraphPanel) ((ETReduxFrame) parent).getMyConcordiaGraphPanel());

            // use default if user has not initialized
            if (concordiaGraphPanel.getSelectedFractions().isEmpty()) {

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

                concordiaGraphPanel.refreshPanel(true, false);

                concordiaGraphPanel.setShowTightToEdges(false);

            } else {
                concordiaGraphPanel.setGraphWidth(580);
                concordiaGraphPanel.setGraphHeight(405);
            }

            boolean saveShowTitleBox = concordiaGraphPanel.isShowTitleBox();
            concordiaGraphPanel.setShowTitleBox(false);
            concordiaGraphPanel.setUploadToGeochronMode(true);

            concordiaGraphPanel.outputToSVG(tempConcordiaSVGforUploading);

            concordiaGraphPanel.setShowTitleBox(saveShowTitleBox);
            concordiaGraphPanel.setUploadToGeochronMode(false);

            GeochronUploaderUtility.uploadConcordiaImage(tempConcordiaSVGforUploading, myAliquot, userName, password);
//            // april 2011 revise to check if concordia slot is already taken
//            AnalysisImageInterface concordiaImage = getMyAliquot().getAnalysisImageByType(AnalysisImageTypes.CONCORDIA);
//
//            concordiaImage.setImageURL(//
//                    GeochronUploadImagesHelper.uploadImage(//
//                            tempConcordiaSVGforUploading, //
//                            userName, //
//                            password,
//                            AnalysisImageTypes.CONCORDIA));
        }

        // nov 2011
        if (probabilityDensityUpload_chkBox.isSelected()) {
            File tempProbabilitySVGforUploading = new File("tempProbabilityDensity.svg");

            DateProbabilityDensityPanel probabilityPanel = ((DateProbabilityDensityPanel) ((ETReduxFrame) parent).getMyNormedProbabilityPanel());

            // use default if user has not initialized
            if (probabilityPanel.getSelectedFractions().isEmpty()) {
                probabilityPanel.//
                        setSelectedFractions(getSample().getUpbFractionsUnknown());
                probabilityPanel.//
                        getDeSelectedFractions().clear();

                probabilityPanel.setGraphWidth(565);
                probabilityPanel.setGraphHeight(385);

                probabilityPanel.setSelectedHistogramBinCount(5);

                if (sample.isSampleTypeLegacy() & sample.getAnalysisPurpose().equals(ANALYSIS_PURPOSE.DetritalSpectrum)) {
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

            probabilityPanel.outputToSVG(tempProbabilitySVGforUploading);

            probabilityPanel.setUploadToGeochronMode(false);

            GeochronUploaderUtility.uploadPDFImage(tempProbabilitySVGforUploading, myAliquot, userName, password);
//            AnalysisImageInterface probabilityImage = getMyAliquot().getAnalysisImageByType(AnalysisImageTypes.PROBABILITY_DENSITY);
//
//            probabilityImage.setImageURL(//
//                    GeochronUploadImagesHelper.uploadImage(//
//                            tempProbabilitySVGforUploading, //
//                            userName, //
//                            password,
//                            AnalysisImageTypes.PROBABILITY_DENSITY));

        }

        // nov 2011 updated July 2012
        if (reportSVGUpload_chkbox.isSelected()) {
            JTabbedPane tabbedReportViews = ((ETReduxFrame) parent).getReportTableTabbedPane();

            String[][] reportFractions = SampleInterface.reportActiveAliquotFractionsByNumberStyle(sample, myAliquot, false);
            ReportAliquotFractionsView.sortReportColumn(//
                    reportFractions, ((TabbedReportViews) ((ETReduxFrame) parent).getReportTableTabbedPane()).getActiveFractionsSortedColumn(), ((TabbedReportViews) ((ETReduxFrame) parent).getReportTableTabbedPane()).getActiveFractionsSortedColumnDirection());

            ((ReportAliquotFractionsView) ((TabbedReportViews) tabbedReportViews)//
                    .getViewTabulatedAliquotActiveFractions()).setReportFractions(reportFractions);

            File tempReportSVG = ((TabbedReportViews) tabbedReportViews).createReportSVGandPDF("UPLOADED_DATA_TABLE", false);

            AnalysisImageInterface reportImage = getMyAliquot().getAnalysisImageByType(AnalysisImageTypes.REPORT);

            reportImage.setImageURL(//
                    GeochronUploadImagesHelper.uploadImage(//
                            tempReportSVG, //
                            userName, //
                            password,
                            AnalysisImageTypes.REPORT));

        }

        // nov 2011 updated July 2012
        if (reportAsCSV_chkBox.isSelected()) {

            File tempReportCSV = new File("UPLOADED_DATA_TABLE.csv");
            CsvResultsTable.setSampleName(sample.getSampleName());

            String[][] reportFractions = SampleInterface.reportActiveAliquotFractionsByNumberStyle(sample, myAliquot, true);
            ReportAliquotFractionsView.sortReportColumn(//
                    reportFractions, ((TabbedReportViews) ((ETReduxFrame) parent).getReportTableTabbedPane()).getActiveFractionsSortedColumn(), ((TabbedReportViews) ((ETReduxFrame) parent).getReportTableTabbedPane()).getActiveFractionsSortedColumnDirection());

            // generate csv report
            CsvResultsTable.setReportFractions(reportFractions);
            CsvResultsTable.writeCSVReport(tempReportCSV);

            AnalysisImageInterface reportImage = getMyAliquot().getAnalysisImageByType(AnalysisImageTypes.REPORT_CSV);

            reportImage.setImageURL(//
                    GeochronUploadImagesHelper.uploadImage(//
                            tempReportCSV, //
                            userName, //
                            password,
                            AnalysisImageTypes.REPORT_CSV));
        }

        // april 2011 save image urls
        saveAliquot();

        // proceed with upload of Aliquot
        // feb 2015 refactored to another class
        GeochronUploaderUtility.uploadAliquotToGeochron(//
                sample, myAliquot, //
                userName, //
                password, //
                geochronPublicRecord_chkBox.isSelected(), //
                geochronOverwrite_chkBox.isSelected());

////        String content = ((UPbReduxAliquot)myAliquot).serializeXMLObject();
////        // Construct data
////        // June 2010 added overwrite and public choices
////        String isPublic = geochronPublicRecord_chkBox.isSelected() ? "yes" : "no";
////        String overWrite = geochronOverwrite_chkBox.isSelected() ? "yes" : "no";
////
////        try {
////            data
////                    = URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(userName, "UTF-8");
////            data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8");
////            data += "&" + URLEncoder.encode("public", "UTF-8") + "=" + URLEncoder.encode(isPublic, "UTF-8");
////            data += "&" + URLEncoder.encode("content", "UTF-8") + "=" + URLEncoder.encode(content, "UTF-8");
////            data += "&" + URLEncoder.encode("overwrite", "UTF-8") + "=" + URLEncoder.encode(overWrite, "UTF-8");
////        } catch (UnsupportedEncodingException unsupportedEncodingException) {
////        }
////
////        // april 2009 move to zipping for improved upload performance
////        // http://www.exampledepot.com/egs/java.util.zip/CreateZip.html
////        // These are the files to include in the ZIP file
////        // geochron expects this file name exactly
////        String fileName = "tempDataForAliquotUpload";
////
////        // Create a buffer for reading the files
////        byte[] buf = new byte[2048];
////
////        try {
////            // Create the ZIP file
////            String outFilename = "tempDataForAliquotUploadzip";
////            ZipOutputStream out = new ZipOutputStream(new FileOutputStream(outFilename));
////
////            // Compress the file
////            //FileInputStream in = new FileInputStream(fileName);
////            InputStream in = new ByteArrayInputStream(data.getBytes());
////
////            // Add ZIP entry to output stream.
////            out.putNextEntry(new ZipEntry(fileName));
////
////            // Transfer bytes from the file to the ZIP file
////            int len;
////            while ((len = in.read(buf)) > 0) {
////                out.write(buf, 0, len);
////            }
////
////            // Complete the entry
////            out.closeEntry();
////            in.close();
////
////            // Complete the ZIP file
////            out.close();
////        } catch (IOException e) {
////        }
////
////        File uploadFile = new File("tempDataForAliquotUploadzip");
////
////        InputStream response = null;
////        try {
////            response = ClientHttpRequest.post(//
////                    new URL("http://www.geochron.org/redux_service.php"),//
////                    "filetoupload",
////                    uploadFile);
////        } catch (IOException iOException) {
////        }
////
////        org.w3c.dom.Document doc = null;
////        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
////        factory.setValidating(false);
////        try {
////            doc = factory.newDocumentBuilder().parse(response);
////        } catch (ParserConfigurationException | SAXException | IOException parserConfigurationException) {
////        }
////
////        String error = "no";
////        String message = "";
////        if (doc != null) {
////            if (doc.getElementsByTagName("error").getLength() > 0) {
////                error = doc.getElementsByTagName("error").item(0).getTextContent();
////                message = doc.getElementsByTagName("message").item(0).getTextContent();
////            }
////        }
////
////        sample.setArchivedInRegistry(error.equalsIgnoreCase("no"));
////
////        JOptionPane.showMessageDialog(this,
////                new String[]{
////                    !error.equalsIgnoreCase("no") ? "Failure!\n" : "Success!\n",
////                    message + "   " + myAliquot.getSampleIGSN() + "::" + myAliquot.getAliquotName()
////                });
    }

    private class ValidationFromRegistry {

        String selectedSampleID;
        boolean valid;

        public ValidationFromRegistry(//
                String selectedSampleID,
                boolean valid) {
            this.selectedSampleID = selectedSampleID;
            this.valid = valid;
        }
    }

//    private ValidationFromRegistry queryRegistryWithSampleID(String SampleID) {
//        // april 2011 simple validation: is sampleID an ID in the specified registry
//        // reg.SampleID
//        return new ValidationFromRegistry( //
//                SampleID, //
//                SampleRegistries.isSampleIdentifierValidAtRegistry(SampleID));
//    }
//
//    private ValidationFromRegistry querySESAR(String SampleID) {
//        String connectionStringSESAR = "http://www.geosamples.org/display.php?igsn=";
//        org.w3c.dom.Document doc = URIHelper.RetrieveXMLfromServerAsDOMdocument(connectionStringSESAR + SampleID.trim());
//
//        ValidationFromRegistry retVal = new ValidationFromRegistry(SampleID, false);
//        String selectedSampleIGSN;
//
//        if (doc != null) {
//            if (doc.hasChildNodes()) {
//                Node SESARsample = doc.getFirstChild();
//
//                ArrayList<String> messageTop = new ArrayList<String>();
//                if (SESARsample.getNodeName().equalsIgnoreCase("sample")) {
//                    NodeList sampleDetails = SESARsample.getChildNodes();
//                    for (int i = 0; i < sampleDetails.getLength(); i++) {
//                        System.out.println(sampleDetails.item(i).getNodeName());
//                    }
//
//                    // check for child or parent type SampleID
//                    NodeList temp = doc.getElementsByTagName("ParentIGSN");
//                    if (temp.getLength() > 0) {
//                        doc = URIHelper.RetrieveXMLfromServerAsDOMdocument(connectionStringSESAR//
//                                + temp.item(0).getTextContent());
//
//                        selectedSampleIGSN = temp.item(0).getTextContent();
//
//                        messageTop.add(
//                                "SESAR reports that IGSN " + SampleID + " is a child of a sample.\n\n");
//                        messageTop.add(
//                                "You must choose a Sample IGSN, and SESAR reports that for this child, \n");
//                        messageTop.add(
//                                "the parent Sample has IGSN " //
//                                + selectedSampleIGSN //
//                                + ", identified as:\n\n");
//                    } else {
//                        // we have a sample SampleID
//                        selectedSampleIGSN = SampleID;
//                        messageTop.add(
//                                "SESAR reports that IGSN " + selectedSampleIGSN + " is a Sample identified as:\n\n");
//                    }
//
//                    // present the user with the sample SampleID and data and a confirm
//                    try {
//                        messageTop.add("IGSN = " + doc.getElementsByTagName("IGSN").item(0).getTextContent() + "\n");
//                    } catch (NullPointerException dOMException) {
//                    }
//                    try {
//                        messageTop.add("SampleID = " + doc.getElementsByTagName("SampleID").item(0).getTextContent() + "\n");
//                    } catch (NullPointerException dOMException) {
//                    }
//                    try {
//                        messageTop.add("SampleComment = " + doc.getElementsByTagName("SampleComment").item(0).getTextContent() + "\n");
//                    } catch (NullPointerException dOMException) {
//                    }
//                    try {
//                        messageTop.add("GeoObjectType = " + doc.getElementsByTagName("GeoObjectType").item(0).getTextContent() + "\n");
//                    } catch (NullPointerException dOMException) {
//                    }
//                    try {
//                        messageTop.add("Material = " + doc.getElementsByTagName("Material").item(0).getTextContent() + "\n");
//                    } catch (NullPointerException dOMException) {
//                    }
////                    try {
////                        messageTop.add("PrimaryLocationName = " + doc.getElementsByTagName("PrimaryLocationName").item(0).getTextContent() + "\n");
////                    } catch (NullPointerException dOMException) {
////                    }
////                    try {
////                        messageTop.add("MostRecentArchivalInstitution = " + doc.getElementsByTagName("MostRecentArchivalInstitution").item(0).getTextContent() + "\n");
////                    } catch (NullPointerException dOMException) {
////                    }
//                    messageTop.add("\n     Do you accept this Sample as the parent of this Aliquot?");
//
//                    String[] message = new String[messageTop.size()];
//                    message = messageTop.toArray(message);
//
//                    int response = JOptionPane.showConfirmDialog(this,
//                            message,
//                            "U-Pb Redux Information",
//                            JOptionPane.YES_NO_OPTION,
//                            JOptionPane.INFORMATION_MESSAGE);
//                    if (response == JOptionPane.NO_OPTION) {
//                        retVal.selectedSampleID = SampleID;
//                        retVal.valid = false;
//                    } else {
//                        retVal.selectedSampleID = selectedSampleIGSN;
//                        retVal.valid = true;
//                    }
//
//                } else {
//                    // assume error received
//
//                    String SESAR_message = "SESAR failed to process this request - please contact SESAR.";
//
//                    try {
//                        SESAR_message = doc.getElementsByTagName("Error").item(0).getTextContent();
//                    } catch (DOMException dOMException) {
//                    }
//
//                    JOptionPane.showMessageDialog(this,
//                            new String[]{"SESAR responded: " + SESAR_message});
//
//                    retVal.selectedSampleID = SampleID;
//                    retVal.valid = false;
//
//                }
//
//            }
//        }
//
//        return retVal;
//    }
    // refactor this stuff to reduce coupling
    private GeochronValidationResults confirmAliquotArchivedInGeochron(AliquotInterface aliquot) {

        String userName = ((ETReduxFrame) parent).getMyState().getReduxPreferences().getGeochronUserName();
        String password = ((ETReduxFrame) parent).getMyState().getReduxPreferences().getGeochronPassWord();

        String connectionString
                = "https://www.geochron.org/getxml.php?sampleigsn="//
                + aliquot.getSampleIGSN() //
                + "&aliquotname=" //
                + aliquot.getAliquotName()//
                + "&username="//
                + userName//
                + "&password="//
                + password//
                + "&validateonly=yes";
        
        GeochronValidationResults checkValid = GeochronValidationResults.errorNotFound;
        
        try {
            checkValid = GeochronValidationResults.validateAliquot(connectionString);
        } catch (Exception e) {
        }
        
        return checkValid;
    }

    /**
     *
     */
    public void testUploadedAliquotRetrieval() {

        String userName = ((ETReduxFrame) parent).getMyState().getReduxPreferences().getGeochronUserName();
        String password = ((ETReduxFrame) parent).getMyState().getReduxPreferences().getGeochronPassWord();

        String downloadURL
                = //
                "https://www.geochron.org/getxml.php?sampleigsn="//
                + getMyAliquot().getSampleIGSN() //
                + "&aliquotname=" //
                + getMyAliquot().getAliquotName()//
                + "&username="//
                + userName//
                + "&password="//
                + password;

        viewXMLAliquotAsHTML(downloadURL);
    }
}
