/*
 * SampleAnalysisWorkflowManagerLAICPMS.java
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
package org.earthtime.UPb_Redux.dialogs.sampleManagers.analysisManagers;

import java.awt.Adjustable;
import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.earthtime.ETReduxFrame;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.ReduxConstants.ANALYSIS_PURPOSE;
import org.earthtime.UPb_Redux.aliquots.Aliquot;
import org.earthtime.UPb_Redux.aliquots.UPbReduxAliquot;
import org.earthtime.UPb_Redux.dialogs.DialogEditor;
import org.earthtime.UPb_Redux.dialogs.DialogEditor.BigDecimalDocument;
import org.earthtime.UPb_Redux.dialogs.DialogEditor.UnDoAbleDocument;
import org.earthtime.UPb_Redux.dialogs.fractionManagers.FractionNotesDialog;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.UPb_Redux.fractions.Fraction;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFraction;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFractionI;
import org.earthtime.UPb_Redux.reduxLabData.ReduxLabData;
import org.earthtime.UPb_Redux.renderers.EditFractionButton;
import org.earthtime.UPb_Redux.samples.UPbSampleInterface;
import org.earthtime.UPb_Redux.utilities.comparators.IntuitiveStringComparator;
import org.earthtime.dataDictionaries.SampleRegistries;
import org.earthtime.exceptions.ETException;
import org.earthtime.exceptions.ETWarningDialog;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.samples.SampleInterface;
import org.earthtime.utilities.FileHelper;
import org.jdesktop.layout.GroupLayout.ParallelGroup;
import org.jdesktop.layout.GroupLayout.SequentialGroup;

/**
 *
 * @author James F. Bowring
 */
public class SampleAnalysisWorkflowManagerLAICPMS extends DialogEditor implements SampleAnalysisWorkflowManagerInterface {

    /**
     *
     */
    protected static Font dropDownFont = new Font("SansSerif", Font.BOLD, 11);
    private SampleInterface mySample = null;
    private Aliquot myCurrentAliquot;
    private File importedXMLFractionsFolder;
    private File sampleFolder;
    private File sampleMetaDataFolder;
    private File tripoliRawDataFolder;
    private boolean initialized = false;
    private ArrayList<String> aliquotList;
    private boolean newSample = false;
    private JTextField masterNewFractionName = null;
    private JButton masterNewFractionNameAdder = null;
    private JCheckBox masterZirconCaseCheckBox = null;
    private JButton masterZironCaseFiller = null;
    private JComboBox masterTracerChooser = null;
    private JButton masterTracerFiller = null;
    private JTextField masterTracerMass = null;
    private JButton masterTracerMassFiller = null;
    private JButton masterTracerMassUndoFiller = null;
    private JTextField masterFractionMass = null;
    private JButton masterFractionMassFiller = null;
    private JButton masterFractionMassUndoFiller = null;
    private JComboBox masterPbBlankChooser = null;
    private JButton masterPbBlankFiller = null;
    private JComboBox masterInitialPbModelChooser = null;
    private JButton masterInitialPbModelFiller = null;
    private JTextField masterEstimatedDate = null;
    private JButton masterEstimatedDateUndoFiller = null;
    private JButton masterEstimatedDateFiller = null;
    private JTextField masterPbBlankMass = null;
    private JButton masterPbBlankMassFiller = null;
    private JButton masterPbBlankMassUndoFiller = null;
    // fraction row details
    private ArrayList<JComponent> fractionDeleteButtons;
    private ArrayList<JComponent> fractionNoteButtons;
    private ArrayList<JComponent> fractionID;
    private ArrayList<JComponent> fractionZirconCheckBox;
    private ArrayList<JComboBox> fractionTracerChoice;
    private ArrayList<JComponent> fractionTracerMassText;
    private ArrayList<JLabel> fractionTracerGRAMS;
    private ArrayList<JComponent> fractionMassText;
    private ArrayList<JLabel> fractionMassGRAMS;
    private ArrayList<JComboBox> fractionPbBlankChoice;
    private ArrayList<JComboBox> fractionInitialPbChoice;
    private ArrayList<JComponent> fractionEstDateText;
    private ArrayList<JLabel> fractionEstDateMEGAANNUM;
    private ArrayList<JComponent> fractionPbBlankMassText;
    private ArrayList<JLabel> fractionPbBlankPICOGRAMS;
    private java.awt.Frame parentFrame;

    /**
     * Creates new form SampleAnalysisWorkflowManagerDialog
     *
     * @param parent
     * @param modal
     * @param sampleMetaDataFolder
     * @param sample
     * @param sampleFolder
     * @param importedXMLFractionsFolder
     */
    public SampleAnalysisWorkflowManagerLAICPMS(
            java.awt.Frame parent,
            boolean modal,
            SampleInterface sample,
            File sampleFolder,
            File sampleMetaDataFolder,
            File importedXMLFractionsFolder) {
        super(parent, modal);

        this.parentFrame = parent;

        initComponents();

        fastEdits_panel.setBackground(ReduxConstants.myFractionGreenColor);

        setSampleFolder(sampleFolder);
        setSampleMetaDataFolder(sampleMetaDataFolder);
        setImportedXMLFractionsFolder(importedXMLFractionsFolder);

        setMySample(sample);
//
//        // set up arrow keys etc
//        Set<KeyStroke> forwardKeys = new HashSet<KeyStroke>();
//        forwardKeys.add( KeyStroke.getKeyStroke( KeyEvent.VK_TAB, 0 ) );
//        forwardKeys.add( KeyStroke.getKeyStroke( KeyEvent.VK_RIGHT, 0 ) );
//        fastEdits_panel.setFocusTraversalKeys( KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, forwardKeys );
//
//        Set<KeyStroke> backwardKeys = new HashSet<KeyStroke>();
//        backwardKeys.add( KeyStroke.getKeyStroke( "shift TAB" ) );//    (KeyEvent.VK_SHIFT & KeyEvent.VK_TAB), 0));
//
//        backwardKeys.add( KeyStroke.getKeyStroke( KeyEvent.VK_LEFT, 0 ) );
//        fastEdits_panel.setFocusTraversalKeys( KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, backwardKeys );
//
//        // Listen for value changes in the scroll pane's scrollbars
//        AdjustmentListener listener = new JscrollPaneScrollListener();
//        fastEdits_scrollPane.getHorizontalScrollBar().addAdjustmentListener( listener );
//        fastEdits_scrollPane.getVerticalScrollBar().addAdjustmentListener( listener );
//
//        // eliminate default "crawl"
//        fastEdits_scrollPane.getVerticalScrollBar().setUnitIncrement( 32 );
//        fastEdits_scrollPane.getHorizontalScrollBar().setUnitIncrement( 64 );
//        setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
//
//
//        aliquotName_text.addKeyListener( new KeyListener() {
//
//            @Override
//            public void keyTyped ( KeyEvent e ) {
//            }
//
//            @Override
//            public void keyPressed ( KeyEvent e ) {
//            }
//
//            @Override
//            public void keyReleased ( KeyEvent e ) {
//                if ( aliquotList.contains( aliquotName_text.getText().trim() ) ) {
//                    addAliquot_button.setEnabled( false );
//                    editAliquot_button.setEnabled( false );
//                } else {
//                    addAliquot_button.setEnabled( true );
//                    editAliquot_button.setEnabled( true );
//                }
//            }
//        } );

        try {
            initSampleFields();
            initSampleAliquots();
        } catch (ETException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }

        sampleToolBar_buttonGroup.clearSelection();
    }

    /**
     *
     */
    public void initSampleAliquots() {

        aliquotName_text.setDocument(
                new UnDoAbleDocument(aliquotName_text, true));
        aliquotName_text.setText("Aliquot Name");

        aliquotList = new ArrayList<>();
        if (mySample.getFractions().size() > 0) {
            for (int i = 0; i < mySample.getAliquots().size(); i++) {
                // only show aliquots with fractions because removed aliquots still exist with zero fraction
                if (((UPbReduxAliquot) mySample.getAliquots().get(i)).getAliquotFractions().size() > 0) {
                    aliquotList.add(mySample.getAliquots().get(i).getAliquotName());
                }
            }
        }

        String[] aliquotArray = new String[aliquotList.size()];
        aliquotArray = aliquotList.toArray(aliquotArray);
        aliquotsList_jList.setListData(aliquotArray);
//        aliquotsList_jList.setListData((String[]) aliquotList.toArray());
        aliquotsList_jList.addListSelectionListener(new aliquotListSelectionListener());
        if (aliquotList.size() > 1) {
            aliquotsList_jList.setSelectedIndex(1);
        } else {
            aliquotsList_jList.setSelectedIndex(0);
        }

        SpinnerModel valueDigits_spinnerModel
                = new SpinnerNumberModel(1, 1, 20, 1);
        insertFractionCount_spinner.setModel(valueDigits_spinnerModel);

    }

    /**
     *
     * @param aliquot
     */
    public void initAliquot(final Aliquot aliquot) {

        fastEdits_panel.removeAll();

        fastEdits_panel.setBackground(ReduxConstants.myFractionGreenColor);

//        fractionDeleteButtons =
//                new ArrayList<JComponent>();
        fractionNoteButtons
                = new ArrayList<JComponent>();

        fractionID
                = new ArrayList<JComponent>();

        fractionZirconCheckBox
                = new ArrayList<JComponent>();

        fractionTracerChoice
                = new ArrayList<JComboBox>();

        fractionTracerMassText
                = new ArrayList<JComponent>();

        fractionTracerGRAMS
                = new ArrayList<JLabel>();

        fractionMassText
                = new ArrayList<JComponent>();

        fractionMassGRAMS
                = new ArrayList<JLabel>();

        fractionPbBlankChoice
                = new ArrayList<JComboBox>();

        fractionInitialPbChoice
                = new ArrayList<JComboBox>();
        fractionEstDateText
                = new ArrayList<JComponent>();

        fractionEstDateMEGAANNUM
                = new ArrayList<JLabel>();

        fractionPbBlankMassText
                = new ArrayList<JComponent>();

        fractionPbBlankPICOGRAMS
                = new ArrayList<JLabel>();

        // create master row for filling others ********************************
        // new fraction namer
        masterNewFractionName
                = new JTextField();
        masterNewFractionName.setDocument(new UnDoAbleDocument(masterNewFractionName, true));
        masterNewFractionName.setText("New Fraction");

        // new fraction creator button
        masterNewFractionNameAdder
                = new EditFractionButton("ADD", -1, true);
        masterNewFractionNameAdder.setToolTipText("Click to ADD new Fraction");
        masterNewFractionNameAdder.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent arg0) {
                Fraction addedFraction = null;

                // check to see if fractionid is in use
                Vector<String> fractionIDs = new Vector<String>();

                for (int f = 0;
                        f < mySample.getFractions().size();
                        f++) {
                    fractionIDs.add(mySample.getFractions().get(f).getFractionID());
                }

                Collections.sort(fractionIDs, new IntuitiveStringComparator<String>());//String.CASE_INSENSITIVE_ORDER );

                int index = Collections.binarySearch(fractionIDs, masterNewFractionName.getText());
                if (index >= 0) {
                    new ETWarningDialog("Duplicate Fraction ID, please use another.").setVisible(true);
                } else {
                    // prepare fields
                    try {
                        addedFraction = new UPbFraction("NONE");
                        ((UPbFraction) addedFraction).setSampleName(mySample.getSampleName());
                        ((UPbFraction) addedFraction).setAliquotNumber(((UPbReduxAliquot) aliquot).getAliquotNumber());
                        ((UPbFraction) addedFraction).setFractionID(masterNewFractionName.getText().trim());

                        ReduxLabData labData = ((UPbReduxAliquot) aliquot).getMyReduxLabData();
                        ((UPbFraction) addedFraction).setMyLabData(labData);
//                        ((UPbFraction) addedFraction)//
//                                .setTracer( labData.getATracerModel( (String) masterTracerChooser.getSelectedItem() ) );
//                        ((UPbFraction) addedFraction)//
//                                .setPbBlank( labData.getAPbBlankModel( (String) masterPbBlankChooser.getSelectedItem() ) );
//                        ((UPbFraction) addedFraction)//
//                                .setInitialPbModel( labData.getAnInitialPbModel( (String) masterInitialPbModelChooser.getSelectedItem() ) );//march 2012 update.copy());
                        ((UPbFraction) addedFraction)//
                                .setPhysicalConstantsModel(mySample.getPhysicalConstantsModel());
                    } catch (BadLabDataException ex) {
                        new ETWarningDialog(ex).setVisible(true);
                    }
                    addedFraction.setEstimatedDate(new BigDecimal(masterEstimatedDate.getText()));

//                    addedFraction.getAnalysisMeasure( AnalysisMeasures.tracerMassInGrams.getName() ).copyValuesFrom( new ValueModel(
//                            AnalysisMeasures.tracerMassInGrams.getName(),
//                            new BigDecimal( masterTracerMass.getText() ),
//                            "ABS",
//                            BigDecimal.ZERO ) );
                    mySample.addFraction(addedFraction);

                    // be sure aliquot has upbfractions too
                    // usingthis miserablehack nov 2009
                    // TODO
                    mySample.getAliquotByName(aliquot.getAliquotName());

                    addNewFractionRow(aliquot, addedFraction);

                }
            }
        });

//        // Zircon checkbox
//        masterZirconCaseCheckBox = new JCheckBox( "NO I-Pb", false );
//        masterZirconCaseCheckBox.setFont( new Font( "Monospaced", Font.BOLD, 10 ) );
//        masterZirconCaseCheckBox.setOpaque( false );
//
//        // Zircon checkbox filler button
//        masterZironCaseFiller = new EditFractionButton( "FILL", -1, true );
//        masterZironCaseFiller.setMargin( new Insets( 0, 0, 0, 0 ) );
//
//        masterZironCaseFiller.setToolTipText( "Click to FILL Column" );
//        masterZironCaseFiller.addActionListener( new ActionListener() {
//
//            public void actionPerformed ( ActionEvent e ) {
//                for (int f = 0; f
//                        < ((UPbReduxAliquot) aliquot).getAliquotFractions().size(); f ++) {
//                    ((JCheckBox) fractionZirconCheckBox.get( f )).setSelected( masterZirconCaseCheckBox.isSelected() );
//                }
//
//            }
//        } );
//        //Tracer chooser
//        masterTracerChooser = new JComboBox();
//        masterTracerChooser.setFont( dropDownFont );
//        masterTracerChooser.addItemListener( new ItemListener() {
//
//            public void itemStateChanged ( ItemEvent e ) {
//                String tracerName = (String) masterTracerChooser.getSelectedItem();
//                masterTracerChooser.setToolTipText( tracerName );
//                try {
//                    AbstractRatiosDataModel tracer = ((UPbReduxAliquot) aliquot).getMyReduxLabData().getATracerModel( tracerName );
//                    masterTracerChooser.setToolTipText( ReduxLabData.toolTipForTracer( tracer ) );
//                } catch (BadLabDataException badLabDataException) {
//                }
//            }
//        } );
//
//        ArrayList<AbstractRatiosDataModel> mtracers = ((UPbReduxAliquot) aliquot).getMyReduxLabData().getTracers();
//        for (int i = (mtracers.size() > 1 ? 1 : 0); i < mtracers.size(); i ++) {
//            masterTracerChooser.addItem( ((AbstractRatiosDataModel) mtracers.get( i )).getNameAndVersion() );
//        }
//        // Tracer choosers filler button
//        masterTracerFiller = new EditFractionButton( "FILL", -1, true );
//        masterTracerFiller.setToolTipText( "Click to FILL Column" );
//        masterTracerFiller.addActionListener( new ActionListener() {
//
//            public void actionPerformed ( ActionEvent e ) {
//                for (int f = 0; f
//                        < ((UPbReduxAliquot) aliquot).getAliquotFractions().size(); f ++) {
//                    // check for enabled
//                    if ( fractionTracerChoice.get( f ).isEnabled() ) {
//                        fractionTracerChoice.get( f )//
//                                .setSelectedIndex( masterTracerChooser.getSelectedIndex() + 1 );// dec 2011 shift of one since master does not show "none" tracer
//                    }
//
//                }
//            }
//        } );
//        // Tracer mass
//        masterTracerMass =
//                new JTextField();
//        masterTracerMass.setDocument( new BigDecimalDocument( masterTracerMass, true ) );
//        masterTracerMass.setText( "0.0000" );
//        masterTracerMass.setHorizontalAlignment( JTextField.RIGHT );
//
//        // Tracer mass filler button
//        masterTracerMassFiller =
//                new EditFractionButton( "FILL", -1, true );
//        masterTracerMassFiller.setToolTipText( "Click to FILL Column" );
//        masterTracerMassFiller.addActionListener( new ActionListener() {
//
//            public void actionPerformed ( ActionEvent e ) {
//                for (int f = 0; f
//                        < ((UPbReduxAliquot) aliquot).getAliquotFractions().size(); f ++) {
//                    if ( ((JTextField) fractionTracerMassText.get( f )).isEnabled() ) {
//                        ((JTextField) fractionTracerMassText.get( f )).setText( masterTracerMass.getText() );
//                    }
//                }
//
//            }
//        } );
//        // Tracer mass undo filler button
//        masterTracerMassUndoFiller =
//                new EditFractionButton( "UnFill", -1, true );
//        masterTracerMassUndoFiller.setToolTipText( "Click to UNDO FILL Column" );
//        masterTracerMassUndoFiller.addActionListener( new ActionListener() {
//
//            public void actionPerformed ( ActionEvent e ) {
//                for (int f = 0; f
//                        < ((UPbReduxAliquot) aliquot).getAliquotFractions().size(); f ++) {
//                    if ( ((JTextField) fractionTracerMassText.get( f )).isEnabled() ) {
//                        ((UnDoAbleDocument) ((JTextField) fractionTracerMassText.get( f )).getDocument()).unFill();
//                    }
//                }
//
//            }
//        } );
//        // Fraction mass
//        masterFractionMass =
//                new JTextField();
//        masterFractionMass.setDocument( new BigDecimalDocument( masterFractionMass, true ) );
//        masterFractionMass.setText( "0.0000" );
//        masterFractionMass.setHorizontalAlignment( JTextField.RIGHT );
//
//        // Fraction mass filler button
//        masterFractionMassFiller =
//                new EditFractionButton( "FILL", -1, true );
//        masterFractionMassFiller.setToolTipText( "Click to FILL Column" );
//        masterFractionMassFiller.addActionListener( new ActionListener() {
//
//            public void actionPerformed ( ActionEvent e ) {
//                for (int f = 0; f
//                        < ((UPbReduxAliquot) aliquot).getAliquotFractions().size(); f ++) {
//                    if ( ((JTextField) fractionMassText.get( f )).isEnabled() ) {
//                        ((JTextField) fractionMassText.get( f )).setText( masterFractionMass.getText() );
//                    }
//                }
//
//            }
//        } );
//        // Fraction mass undo filler button
//        masterFractionMassUndoFiller =
//                new EditFractionButton( "UnFill", -1, true );
//        masterFractionMassUndoFiller.setToolTipText( "Click to UNDO FILL Column" );
//        masterFractionMassUndoFiller.addActionListener( new ActionListener() {
//
//            public void actionPerformed ( ActionEvent e ) {
//                for (int f = 0; f
//                        < ((UPbReduxAliquot) aliquot).getAliquotFractions().size(); f ++) {
//                    if ( ((JTextField) fractionMassText.get( f )).isEnabled() ) {
//                        ((UnDoAbleDocument) ((JTextField) fractionMassText.get( f )).getDocument()).unFill();
//                    }
//                }
//
//            }
//        } );
//        // PbBlank chooser
//        masterPbBlankChooser = new JComboBox();
//        masterPbBlankChooser.setFont( dropDownFont );
//        masterPbBlankChooser.addItemListener( new ItemListener() {
//
//            public void itemStateChanged ( ItemEvent e ) {
//                JComboBox comboBox = (JComboBox) e.getSource();
//                String blankName = (String) comboBox.getSelectedItem();
//                comboBox.setToolTipText( blankName );
////                    try {
////                        Tracer tracer = getMyAliquot().getMyReduxLabData().getATracerModel( tracerName );
////                        comboBox.setToolTipText( ReduxLabData.toolTipForTracer( tracer ) );
////                    } catch (BadLabDataException badLabDataException) {
////                    }
//            }
//        } );
//        ArrayList<AbstractRatiosDataModel> myPbBlanks = ((UPbReduxAliquot) aliquot).getMyReduxLabData().getPbBlanks();
//        for (int i = (myPbBlanks.size() > 1 ? 1 : 0); i < myPbBlanks.size(); i ++) {
//            masterPbBlankChooser.addItem( ((AbstractRatiosDataModel) myPbBlanks.get( i )).getReduxLabDataElementName() );
//        }
//
//        // PbBlank choosers filler button
//        masterPbBlankFiller = new EditFractionButton( "FILL", -1, true );
//        masterPbBlankFiller.setToolTipText( "Click to FILL Column" );
//        masterPbBlankFiller.addActionListener( new ActionListener() {
//
//            public void actionPerformed ( ActionEvent e ) {
//                for (int f = 0; f
//                        < ((UPbReduxAliquot) aliquot).getAliquotFractions().size(); f ++) {
//                    fractionPbBlankChoice.get( f )//
//                            .setSelectedIndex( masterPbBlankChooser.getSelectedIndex() );
//                }
//
//            }
//        } );
//
//        // InitialPbModel chooser
//        masterInitialPbModelChooser = new JComboBox();
//        masterInitialPbModelChooser.setFont( dropDownFont );
//        masterInitialPbModelChooser.addItemListener( new ItemListener() {
//
//            public void itemStateChanged ( ItemEvent e ) {
//                JComboBox comboBox = (JComboBox) e.getSource();
//                String initialPBName = (String) comboBox.getSelectedItem();
//                comboBox.setToolTipText( initialPBName );
//            }
//        } );
//        ArrayList<AbstractRatiosDataModel> initialPbModels = ((UPbReduxAliquot) aliquot).getMyReduxLabData().getInitialPbModels();
//        for (int i = (initialPbModels.size() > 1 ? 1 : 0); i < initialPbModels.size(); i ++) {
////            if (  ! (initialPbModels.get( i ) instanceof StaceyKramersInitialPbModelET) ) {
//                masterInitialPbModelChooser.addItem( ((AbstractRatiosDataModel) initialPbModels.get( i )).getReduxLabDataElementName() );
////            }
//        }
//
//
//        // InitialPbModel choosers filler button
//        masterInitialPbModelFiller = new EditFractionButton( "FILL", -1, true );
//        masterInitialPbModelFiller.setToolTipText( "Click to FILL Column" );
//        masterInitialPbModelFiller.addActionListener( new ActionListener() {
//
//            public void actionPerformed ( ActionEvent e ) {
//                for (int f = 0; f
//                        < ((UPbReduxAliquot) aliquot).getAliquotFractions().size(); f ++) {
//                    if ( fractionInitialPbChoice.get( f ).isEnabled() ) {
//                        fractionInitialPbChoice.get( f )//
//                                .setSelectedIndex( masterInitialPbModelChooser.getSelectedIndex() );
//                    }
//                }
//
//            }
//        } );
//
//        // Estimated Date
//        masterEstimatedDate =
//                new JTextField();
//        masterEstimatedDate.setDocument( new BigDecimalDocument( masterEstimatedDate, true ) );
//        masterEstimatedDate.setText( "0000.0" );
//        masterEstimatedDate.setHorizontalAlignment( JTextField.RIGHT );
//
//        // Estimated Date filler button
//        masterEstimatedDateFiller =
//                new EditFractionButton( "FILL", -1, true );
//        masterEstimatedDateFiller.setToolTipText( "Click to FILL Column" );
//        masterEstimatedDateFiller.addActionListener( new ActionListener() {
//
//            public void actionPerformed ( ActionEvent e ) {
//                for (int f = 0; f
//                        < ((UPbReduxAliquot) aliquot).getAliquotFractions().size(); f ++) {
//                    if ( ((JTextField) fractionEstDateText.get( f )).isEnabled() ) {
//                        ((JTextField) fractionEstDateText.get( f )).setText( masterEstimatedDate.getText() );
//                    }
//                }
//
//            }
//        } );
//        // Estimated Date undo filler button
//        masterEstimatedDateUndoFiller =
//                new EditFractionButton( "UnFill", -1, true );
//        masterEstimatedDateUndoFiller.setToolTipText( "Click to UNDO FILL Column" );
//        masterEstimatedDateUndoFiller.addActionListener( new ActionListener() {
//
//            public void actionPerformed ( ActionEvent e ) {
//                for (int f = 0; f
//                        < ((UPbReduxAliquot) aliquot).getAliquotFractions().size(); f ++) {
//                    if ( ((JTextField) fractionEstDateText.get( f )).isEnabled() ) {
//                        ((UnDoAbleDocument) ((JTextField) fractionEstDateText.get( f )).getDocument()).unFill();
//                    }
//                }
//
//            }
//        } );
//        // Blank Pb mass
//        masterPbBlankMass =
//                new JTextField();
//        masterPbBlankMass.setDocument( new BigDecimalDocument( masterPbBlankMass, true ) );
//        masterPbBlankMass.setText( "0.0000" );
//        masterPbBlankMass.setHorizontalAlignment( JTextField.RIGHT );
//
//        // Blank Pb mass filler button
//        masterPbBlankMassFiller =
//                new EditFractionButton( "FILL", -1, true );
//        masterPbBlankMassFiller.setToolTipText( "Click to FILL Column" );
//        masterPbBlankMassFiller.addActionListener( new ActionListener() {
//
//            public void actionPerformed ( ActionEvent e ) {
//                for (int f = 0; f
//                        < ((UPbReduxAliquot) aliquot).getAliquotFractions().size(); f ++) {
//                    if ( ((JTextField) fractionPbBlankMassText.get( f )).isEnabled() ) {
//                        ((JTextField) fractionPbBlankMassText.get( f )).setText( masterPbBlankMass.getText() );
//                    }
//                }
//
//            }
//        } );
//        // Blank Pb mass undo filler button
//        masterPbBlankMassUndoFiller =
//                new EditFractionButton( "UnFill", -1, true );
//        masterPbBlankMassUndoFiller.setToolTipText( "Click to UNDO FILL Column" );
//        masterPbBlankMassUndoFiller.addActionListener( new ActionListener() {
//
//            public void actionPerformed ( ActionEvent e ) {
//                for (int f = 0; f
//                        < ((UPbReduxAliquot) aliquot).getAliquotFractions().size(); f ++) {
//                    if ( ((JTextField) fractionPbBlankMassText.get( f )).isEnabled() ) {
//                        ((UnDoAbleDocument) ((JTextField) fractionPbBlankMassText.get( f )).getDocument()).unFill();
//                    }
//                }
//
//            }
//        } );
        // populate rows
        for (int row = 0; row
                < ((UPbReduxAliquot) aliquot).getAliquotFractions().size(); row++) {

            Fraction tempFrac = ((UPbReduxAliquot) aliquot).getAliquotFractions().get(row);
            int max = ((UPbReduxAliquot) aliquot).getAliquotFractions().size();
            addFractionRow(aliquot, tempFrac, row, max);

            System.out.println("Aliquot num in fraction = " + ((UPbFractionI) tempFrac).getAliquotNumber());
        }
        System.out.println();

        // populate the components with fraction data
        showSavedDataII(aliquot);

        buildFastEditDisplayPanel();
    }

    private void showSavedDataII(Aliquot aliquot) {
        // default master fields
//        masterZirconCaseCheckBox.setSelected( ((UPbReduxAliquot) aliquot).getDefaultIsZircon() );
//
//        masterTracerChooser.setSelectedItem( ((UPbReduxAliquot) aliquot).getDefaultTracerID() );
//        if ( masterTracerChooser.getSelectedIndex() == -1 ) {
//            masterTracerChooser.setSelectedIndex( 0 );
//        }
//
//        masterTracerMass.setText( ((UPbReduxAliquot) aliquot).getDefaultTracerMassText() );
//        masterFractionMass.setText( ((UPbReduxAliquot) aliquot).getDefaultFractionMassText() );
//
//        masterPbBlankChooser.setSelectedItem( ((UPbReduxAliquot) aliquot).getDefaultPbBlankID() );
//        if ( masterPbBlankChooser.getSelectedIndex() == -1 ) {
//            masterPbBlankChooser.setSelectedIndex( -1 );
//        }
//
//        masterInitialPbModelChooser.setSelectedItem( ((UPbReduxAliquot) aliquot).getDefaultInitialPbModelID() );
//        if ( masterInitialPbModelChooser.getSelectedIndex() == -1 ) {
//            masterInitialPbModelChooser.setSelectedIndex( -1 );
//        }
//
//        masterPbBlankMass.setText( ((UPbReduxAliquot) aliquot).getDefaultPbBlankMassText() );
//
//        masterEstimatedDate.setText( ((UPbReduxAliquot) aliquot).getDefaultEstimatedDateText() );

        // fraction details
        for (int row = 0; row
                < ((UPbReduxAliquot) aliquot).getAliquotFractions().size(); row++) {
            updateFractionRow(((UPbReduxAliquot) aliquot).getAliquotFractions().get(row), row);
        }
    }

    private void addNewFractionRow(Aliquot aliquot, Fraction fraction) {
        int row = fractionID.size();

        addFractionRow(aliquot, fraction, row, row + 1);
        updateFractionRow(fraction, row);

        // update the keystroke actionlisteners for previous row in table
        modifyComponentKeyMapForTable(fractionID.get(row - 1), fractionID, row + 1);
//        modifyComponentKeyMapForTable( fractionZirconCheckBox.get( row - 1 ), fractionZirconCheckBox, row + 1 );
//        modifyComponentKeyMapForTable( fractionTracerMassText.get( row - 1 ), fractionTracerMassText, row + 1 );
//        modifyComponentKeyMapForTable( fractionMassText.get( row - 1 ), fractionMassText, row + 1 );
//
//        modifyComponentKeyMapForTable( fractionPbBlankMassText.get( row - 1 ), fractionPbBlankMassText, row + 1 );
//        modifyComponentKeyMapForTable( fractionEstDateText.get( row - 1 ), fractionEstDateText, row + 1 );

        buildFastEditDisplayPanel();
    }

    private void addFractionRow(Aliquot aliquot, Fraction tempFrac, int row, int max) {

//        // Buttons to allow deletion of fractions
//        JButton tempJB = new EditFractionButton( "X", row, true );
//        tempJB.setForeground( Color.red );
//        tempJB.setToolTipText( "Click to DELETE Fraction!" );
//        tempJB.setMargin( new Insets( 0, 0, 0, 0 ) );
//        tempJB.addActionListener( new deleteFractionListener( mySample, aliquot, tempFrac, row ) );
//        fractionDeleteButtons.add( tempJB );
//        modifyComponentKeyMapForTable( tempJB, fractionDeleteButtons, max );
        // nov 2009 Note button for fractions
        JButton tempJB = new EditFractionButton("Note", row, true);
        tuneNotesButton(tempJB, ((UPbFractionI) tempFrac).getFractionNotes());
        tempJB.setForeground(Color.red);
        //tempJB.setToolTipText("Click to Annotate Fraction.");
        tempJB.setMargin(new Insets(0, 0, 0, 0));
        tempJB.addActionListener(new showFractionNotesListener(tempFrac));
        fractionNoteButtons.add(tempJB);
        modifyComponentKeyMapForTable(tempJB, fractionNoteButtons, max);

        // fraction name
        insertTableTextField(fractionID, max);

//        // zircon check box
//        JCheckBox tempJChk = new JCheckBox( "NO I-Pb", false );
//        tempJChk.setFont( new Font( "Monospaced", Font.BOLD, 10 ) );
//        tempJChk.setOpaque( false );
//        tempJChk.addChangeListener( new changeIsZirconListener( tempFrac, row ) );
//        fractionZirconCheckBox.add( tempJChk );
//        modifyComponentKeyMapForTable( tempJChk, fractionZirconCheckBox, max );
//
//        //Tracer chooser
//        JComboBox tempJCB = new JComboBox();
//        tempJCB.setFont( dropDownFont );
//        tempJCB.addItemListener( new ItemListener() {
//
//            public void itemStateChanged ( ItemEvent e ) {
//                JComboBox comboBox = (JComboBox) e.getSource();
//                String tracerName = (String) comboBox.getSelectedItem();
//                comboBox.setToolTipText( tracerName );
//                try {
//                    AbstractRatiosDataModel tracer = mySample.getMyReduxLabData().getATracerModel( tracerName );
//                    comboBox.setToolTipText( ReduxLabData.toolTipForTracer( tracer ) );
//                } catch (BadLabDataException badLabDataException) {
//                }
//            }
//        } );
//        ArrayList<AbstractRatiosDataModel> tracers = mySample.getMyReduxLabData().getTracers();
//        // must keep index 0 for none tracer here for temporary state of Pb only before U imported
//        for (int i = 0; i < tracers.size(); i ++) {
//            tempJCB.addItem( ((AbstractRatiosDataModel) tracers.get( i )).getNameAndVersion() );
//        }
//
//        fractionTracerChoice.add( tempJCB );
//
//        // Tracer mass
//        insertTableTextFieldForDoubles( fractionTracerMassText, max );
//
//        // Tracer mass grams label
//        JLabel tempJL = new JLabel( " g" );
//        tempJL.setForeground( Color.RED );
//        tempJL.setFont( new Font( "Monospaced", Font.BOLD, 10 ) );
//        fractionTracerGRAMS.add( tempJL );
//
//        // Fraction mass
//        insertTableTextFieldForDoubles( fractionMassText, max );
//
//        // Fraction Mass grams label
//        tempJL = new JLabel( " g" );
//        tempJL.setForeground( Color.RED );
//        tempJL.setFont( new Font( "Monospaced", Font.BOLD, 10 ) );
//        fractionMassGRAMS.add( tempJL );
//        //  Pb Blank chooser
//        tempJCB = new JComboBox();
//        tempJCB.setFont( dropDownFont );
//        tempJCB.addItemListener( new ItemListener() {
//
//            public void itemStateChanged ( ItemEvent e ) {
//                JComboBox comboBox = (JComboBox) e.getSource();
//                String blankName = (String) comboBox.getSelectedItem();
//                comboBox.setToolTipText( blankName );
//            }
//        } );
//
//        ArrayList<AbstractRatiosDataModel> pbBlanks = mySample.getMyReduxLabData().getPbBlanks();
//        for (int i = (pbBlanks.size() > 1 ? 1 : 0); i < pbBlanks.size(); i ++) {
//            tempJCB.addItem( ((AbstractRatiosDataModel) pbBlanks.get( i )).getReduxLabDataElementName() );
//        }
//        fractionPbBlankChoice.add( tempJCB );
//
//
//        //  Initial Pb chooser
//        tempJCB = new JComboBox();
//        tempJCB.setFont( dropDownFont );
//        tempJCB.addItemListener( new ItemListener() {
//
//            public void itemStateChanged ( ItemEvent e ) {
//                JComboBox comboBox = (JComboBox) e.getSource();
//                String initialPbName = (String) comboBox.getSelectedItem();
//                comboBox.setToolTipText( initialPbName );
//            }
//        } );
//
//        ArrayList<AbstractRatiosDataModel> initialPbModels = mySample.getMyReduxLabData().getInitialPbModels();
//        for (int i = (initialPbModels.size() > 1 ? 1 : 0); i < initialPbModels.size(); i ++) {
////            if (  ! (initialPbModels.get( i ) instanceof StaceyKramersInitialPbModelET) ) {
//                tempJCB.addItem( ((AbstractRatiosDataModel) initialPbModels.get( i )).getReduxLabDataElementName() );
////            }
//        }
//        // aug 2010
//        tempJCB.addItemListener( new changeInitialPbModelItemListener( tempFrac, row ) );
//        fractionInitialPbChoice.add( tempJCB );
//
//        // Pb Blank mass
//        insertTableTextFieldForDoubles( fractionPbBlankMassText, max );
//
//        // Pb Blank mass picograms label
//        tempJL = new JLabel( " pg" );
//        tempJL.setForeground( Color.RED );
//        tempJL.setFont( new Font( "Monospaced", Font.BOLD, 10 ) );
//        fractionPbBlankPICOGRAMS.add( tempJL );
//        // Estimated Date
//        insertTableTextFieldForDoubles( fractionEstDateText, max );
//
//        // Estimated Date in MegaAnnum label
//        tempJL = new JLabel( " Ma" );
//        tempJL.setForeground( Color.RED );
//        tempJL.setFont( new Font( "Monospaced", Font.BOLD, 10 ) );
//        fractionEstDateMEGAANNUM.add( tempJL );
    }

    /**
     *
     * @param tableRows
     * @param max
     * @return
     */
    public JTextField insertTableTextFieldForDoubles(
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
     * @param tableRows
     * @param max
     * @return
     */
    public JTextField insertTableTextField(
            final ArrayList<JComponent> tableRows, final int max) {

        final JTextField tableTextField = new JTextField();
        tableTextField.setDocument(new UnDoAbleDocument(tableTextField, true));
        tableTextField.setHorizontalAlignment(JTextField.LEFT);
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
    public void modifyComponentKeyMapForTable(
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

    private void buildFastEditDisplayPanel() {

        fastEdits_panel.removeAll();

        // build display
        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(fastEdits_panel);
        fastEdits_panel.setLayout(jPanel2Layout);

        ParallelGroup myHorizFraction = jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false);
        SequentialGroup myVerticalFraction = jPanel2Layout.createSequentialGroup();

        // create title row elements
        JLabel headDelete = new JLabel("DEL");
        headDelete.setFont(new Font("Monospaced", Font.BOLD, 10));
        headDelete.setForeground(Color.RED);

        JLabel headNote = new JLabel("Note");
        headNote.setFont(new Font("Monospaced", Font.BOLD, 10));
        headNote.setForeground(Color.RED);

        JLabel headFraction = new JLabel("Fraction ID");
        headFraction.setFont(new Font("Monospaced", Font.BOLD, 10));
        headFraction.setForeground(Color.RED);

//        JLabel headZircon = new JLabel( "ZRC?" );
//        headZircon.setFont( new Font( "Monospaced", Font.BOLD, 10 ) );
//        headZircon.setForeground( Color.RED );
//
//        JLabel headTracer = new JLabel( "Tracer" );
//        headTracer.setFont( new Font( "Monospaced", Font.BOLD, 10 ) );
//        headTracer.setForeground( Color.RED );
//
//        JLabel headTracerMass = new JLabel( "Tracer Mass" );
//        headTracerMass.setFont( new Font( "Monospaced", Font.BOLD, 10 ) );
//        headTracerMass.setForeground( Color.RED );
//
//        JLabel headFractionMass = new JLabel( "Fraction mass" );
//        headFractionMass.setFont( new Font( "Monospaced", Font.BOLD, 10 ) );
//        headFractionMass.setForeground( Color.RED );
//
//        JLabel headPbBlank = new JLabel( "Pb Blank" );
//        headPbBlank.setFont( new Font( "Monospaced", Font.BOLD, 10 ) );
//        headPbBlank.setForeground( Color.RED );
//
//        JLabel headInitialPb = new JLabel( "Initial Pb Model" );
//        headInitialPb.setFont( new Font( "Monospaced", Font.BOLD, 10 ) );
//        headInitialPb.setForeground( Color.RED );
//
//        JLabel headEstDate = new JLabel( "Est Date" );
//        headEstDate.setFont( new Font( "Monospaced", Font.BOLD, 10 ) );
//        headEstDate.setForeground( Color.RED );
//
//        JLabel headPbBlankMass = new JLabel( "Pb Blank mass" );
//        headPbBlankMass.setFont( new Font( "Monospaced", Font.BOLD, 10 ) );
//        headPbBlankMass.setForeground( Color.RED );
        // build display *******************************************************
        // master fields
        myHorizFraction.add(jPanel2Layout.createSequentialGroup()//
                .add(55, 55, 55) // left margin
                .add(masterNewFractionName, 110, 110, 110)//
                .add(0, 0, 0)//
        //                .add( masterZirconCaseCheckBox, 75, 75, 75 )//
        //                .add( 5, 5, 5 )//
        //                .add( masterTracerChooser, 150, 150, 150 )//
        //                .add( 5, 5, 5 )//
        //                .add( masterTracerMass, 65, 65, 65 )//
        //                .add( 20, 20, 20 )//
        //                .add( masterFractionMass, 65, 65, 65 )//
        //                .add( 20, 20, 20 )//
        //                .add( masterPbBlankChooser, 150, 150, 150 )//
        //                .add( 5, 5, 5 )//
        //                .add( masterInitialPbModelChooser, 150, 150, 150 )//
        //                .add( 10, 10, 10 )//
        //                .add( masterEstimatedDate, 65, 65, 65 )//
        //                .add( 25, 25, 25 )//
        //                .add( masterPbBlankMass, 65, 65, 65 )//
        );

        myVerticalFraction.add(5, 5, 5) // top margin
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)//.BASELINE)//
                        .add(masterNewFractionName, 22, 22, 22)//
                //                .add( masterZirconCaseCheckBox, 22, 22, 22 )//
                //                .add( masterTracerChooser, 22, 22, 22 )//
                //                .add( masterTracerMass, 22, 22, 22 )//
                //                .add( masterFractionMass, 22, 22, 22 )//
                //                .add( masterPbBlankChooser, 22, 22, 22 )//
                //                .add( masterInitialPbModelChooser, 22, 22, 22 )//
                //                .add( masterEstimatedDate, 22, 22, 22 )//
                //                .add( masterPbBlankMass, 22, 22, 22 )//
                );

        // fill buttons
        myHorizFraction.add(jPanel2Layout.createSequentialGroup()//
                .add(55, 55, 55) // left margin
                .add(masterNewFractionNameAdder, 110, 110, 110)//
        //                .add( 5, 5, 5 )//
        //                .add( masterZironCaseFiller, 65, 65, 65 )//
        //                .add( 5, 5, 5 )//
        //                .add( masterTracerFiller, 150, 150, 150 )//
        //                .add( 5, 5, 5 )//
        //                .add( masterTracerMassFiller, 65, 65, 65 )//
        //                .add( 20, 20, 20 )//
        //                .add( masterFractionMassFiller, 65, 65, 65 )//
        //                .add( 20, 20, 20 )//
        //                .add( masterPbBlankFiller, 150, 150, 150 )//
        //                .add( 5, 5, 5 )//
        //                .add( masterInitialPbModelFiller, 150, 150, 150 )//
        //                .add( 10, 10, 10 )//
        //                .add( masterEstimatedDateFiller, 65, 65, 65 )//
        //                .add( 25, 25, 25 )//
        //                .add( masterPbBlankMassFiller, 65, 65, 65 )//
        );

        myVerticalFraction.add(1, 1, 1) // top margin
                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)//.BASELINE)//
                        .add(masterNewFractionNameAdder)//
                //                .add( masterZironCaseFiller )//, 22, 22, 22)//
                //                .add( masterTracerFiller )//, 22, 22, 22)//
                //                .add( masterTracerMassFiller )//, 22, 22, 22)//
                //                .add( masterFractionMassFiller )//, 22, 22, 22)//
                //                .add( masterPbBlankFiller )//, 22, 22, 22)//
                //                .add( masterInitialPbModelFiller )//, 22, 22, 22)//
                //                .add( masterEstimatedDateFiller )//, 22, 22, 22)//
                //                .add( masterPbBlankMassFiller )//, 22, 22, 22)//
                );

//        // UNDO fill buttons
//
//        myHorizFraction.add(jPanel2Layout.createSequentialGroup()//
//                .add(395, 395, 395) // left margin
//                .add(masterTracerMassUndoFiller, 65, 65, 65)//
//                .add(20, 20, 20)//
//                .add(masterFractionMassUndoFiller, 65, 65, 65)//
//                .add(335, 335, 335)//
//                .add(masterEstimatedDateUndoFiller, 65, 65, 65)//
//                .add(25, 25, 25)//
//                .add(masterPbBlankMassUndoFiller, 65, 65, 65)//
//                );
//
//        myVerticalFraction.add(1, 1, 1) // top margin
//                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)//.BASELINE)//
//                .add(masterTracerMassUndoFiller).add(masterFractionMassUndoFiller).add(masterEstimatedDateUndoFiller).add(masterPbBlankMassUndoFiller));
//        myHorizFraction.add(jPanel2Layout.createSequentialGroup()//
//                .add(4, 4, 4) // left margin
//                .add(headDelete, 21, 21, 21)//
//                .add(4, 4, 4)//
//                .add(headNote, 25, 25, 25)//
//                .add(10, 10, 10) //
//                .add(headFraction, 95, 95, 95)//
//                .add(20, 20, 20)//
//                .add(headZircon, 65, 65, 65)//
//                //.add(5, 5, 5)//
//                .add(headTracer, 140, 140, 140)//
//                .add(15, 15, 15)//
//                .add(headTracerMass, 75, 75, 75)//
//                .add(5, 5, 5)//
//                .add(headFractionMass, 85, 85, 85)//
//                .add(5, 5, 5)//
//                .add(headPbBlank, 140, 140, 140)//
//                .add(15, 15, 15)//
//                .add(headInitialPb, 150, 150, 150)//
//                .add(10, 10, 10)//
//                .add(headEstDate, 75, 75, 75)//
//                .add(12, 12, 12)//
//                .add(headPbBlankMass, 85, 85, 85)//
//                );
//
//        myVerticalFraction.add(10, 10, 10) // top margin
//                .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)//.BASELINE)//
//                .add(headDelete)//
//                .add(headNote) //
//                .add(headFraction)//
//                .add(headZircon)//
//                .add(headTracer)//
//                .add(headTracerMass)//
//                .add(headFractionMass)//
//                .add(headPbBlank)//
//                .add(headInitialPb)//
//                .add(headEstDate)//
//                .add(headPbBlankMass)//
//                )//
//                .add(2, 2, 2);
        // stop delete when only one fraction
//        fractionDeleteButtons.get( 0 ).setEnabled( fractionDeleteButtons.size() != 1 );
        for (int f = 0; f
                < fractionID.size(); f++) {
            myHorizFraction.add(jPanel2Layout.createSequentialGroup()//
                    .add(3, 3, 3) // left-hand margin
                    //                    .add( fractionDeleteButtons.get( f ), 22, 22, 22 ) //
                    //                    .add( 4, 4, 4 )//
                    .add(fractionNoteButtons.get(f), 30, 30, 30) //
                    .add(4, 4, 4)//
                    .add(fractionID.get(f), 200, 200, 200) //
            //                    .add( 0, 0, 0 )//
            //                    .add( fractionZirconCheckBox.get( f ), 75, 75, 75 )//
            //                    .add( 5, 5, 5 )//
            //                    .add( fractionTracerChoice.get( f ), 150, 150, 150 )//
            //                    .add( 5, 5, 5 )//
            //                    .add( fractionTracerMassText.get( f ), 65, 65, 65 )//
            //                    .add( fractionTracerGRAMS.get( f ) )//
            //                    .add( 5, 5, 5 )//
            //                    .add( fractionMassText.get( f ), 65, 65, 65 )//
            //                    .add( fractionMassGRAMS.get( f ) )//
            //                    .add( 10, 10, 10 )//
            //                    .add( fractionPbBlankChoice.get( f ), 150, 150, 150 )//
            //                    .add( 5, 5, 5 )//
            //                    .add( fractionInitialPbChoice.get( f ), 150, 150, 150 )//
            //                    .add( 10, 10, 10 )//
            //                    .add( fractionEstDateText.get( f ), 60, 60, 60 )//
            //                    .add( fractionEstDateMEGAANNUM.get( f ) )//
            //                    .add( 7, 7, 7 )//
            //                    .add( fractionPbBlankMassText.get( f ), 65, 65, 65 )//
            //                    .add( fractionPbBlankPICOGRAMS.get( f ) )//
            );

            myVerticalFraction.add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)//
                    //                    .add( fractionDeleteButtons.get( f ), 22, 22, 22 )//)//, 28, 28, 28)//
                    .add(fractionNoteButtons.get(f), 22, 22, 22)//)//, 28, 28, 28)//
                    .add(fractionID.get(f), 22, 22, 22)//
            //                    .add( fractionZirconCheckBox.get( f ), 24, 24, 24 )//
            //                    .add( fractionTracerChoice.get( f ), 22, 22, 22 )//
            //                    .add( fractionTracerMassText.get( f ), 22, 22, 22 )//
            //                    .add( fractionTracerGRAMS.get( f ) )//
            //                    .add( fractionMassText.get( f ), 22, 22, 22 )//
            //                    .add( fractionMassGRAMS.get( f ) )//
            //                    .add( fractionPbBlankChoice.get( f ), 22, 22, 22 )//
            //                    .add( fractionInitialPbChoice.get( f ), 22, 22, 22 )//
            //                    .add( fractionEstDateText.get( f ), 22, 22, 22 )//
            //                    .add( fractionEstDateMEGAANNUM.get( f ) )//
            //                    .add( fractionPbBlankMassText.get( f ), 22, 22, 22 )//
            //                    .add( fractionPbBlankPICOGRAMS.get( f ) )//
            );
        }

        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(jPanel2Layout.createSequentialGroup().add(myHorizFraction)));

        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(myVerticalFraction));

    }

    /**
     * @return the mySampleMetaDataFolder
     */
    @Override
    public File getSampleMetaDataFolder() {
        return sampleMetaDataFolder;
    }

    /**
     * @param sampleMetaDataFolder
     */
    @Override
    public void setSampleMetaDataFolder(File sampleMetaDataFolder) {
        this.sampleMetaDataFolder = sampleMetaDataFolder;
    }

    /**
     * @return the parentFrame
     */
    public java.awt.Frame getParentFrame() {
        return parentFrame;
    }

    /**
     * @return the importedXMLFractionsFolder
     */
    @Override
    public File getImportedXMLFractionsFolder() {
        return importedXMLFractionsFolder;
    }

    /**
     * @param importedXMLFractionsFolder the importedXMLFractionsFolder to set
     */
    @Override
    public void setImportedXMLFractionsFolder(File importedXMLFractionsFolder) {
        this.importedXMLFractionsFolder = importedXMLFractionsFolder;
    }

    /**
     * @return the tripoliRawDataFolder
     */
    @Override
    public File getTripoliRawDataFolder() {
        return tripoliRawDataFolder;
    }

    /**
     * @param tripoliRawDataFolder the tripoliRawDataFolder to set
     */
    @Override
    public void setTripoliRawDataFolder(File tripoliRawDataFolder) {
        this.tripoliRawDataFolder = tripoliRawDataFolder;
    }

    class aliquotListSelectionListener implements ListSelectionListener {
        // This method is called each time the user changes the set of isZircon items

        public void valueChanged(ListSelectionEvent evt) {
            // When the user release the mouse button and completes the selection,
            // getValueIsAdjusting() becomes false

            if (!evt.getValueIsAdjusting()) {
                JList list = (JList) evt.getSource();
                aliquotName_text.setText((String) list.getSelectedValue());

                // decide if empty else process aliquot by name
                Aliquot aliquot = mySample.getAliquotByName((String) list.getSelectedValue());
                if (aliquot != null) {
                    if (myCurrentAliquot != null) {
                        saveAliquot(myCurrentAliquot);
                    }
                    myCurrentAliquot = aliquot;
                    initAliquot(aliquot);
                    addAliquot_button.setEnabled(false);
                    editAliquot_button.setEnabled(false);
                } else {
                    // we got no aliquots
                    fastEdits_panel.removeAll();
                    fastEdits_panel.repaint();
                    aliquotName_text.setText("Aliquot Name");
                    addAliquot_button.setEnabled(true);
                    editAliquot_button.setEnabled(false);
                }
            }
        }
    }

    class deleteFractionListener implements ActionListener {

        private SampleInterface sample;
        private Aliquot aliquot;
        private Fraction fraction;
        private int row;

        public deleteFractionListener(SampleInterface sample, Aliquot aliquot, Fraction fraction, int row) {
            this.sample = sample;
            this.aliquot = aliquot;
            this.fraction = fraction;
            this.row = row;
        }

        public Fraction getFraction() {
            return fraction;
        }

        public void actionPerformed(ActionEvent e) {
            sample.removeUPbReduxFraction((UPbFraction) fraction);
            removeFractionRow(aliquot, row);

            // be sure aliquot has upbfractions too
            // using this miserable hack nov 2009
            // TODO
            mySample.getAliquotByName(aliquot.getAliquotName());

            buildFastEditDisplayPanel();
        }
    }

    class showFractionNotesListener implements ActionListener {

        private Fraction fraction;

        public showFractionNotesListener(Fraction fraction) {
            this.fraction = fraction;
        }

        public void actionPerformed(ActionEvent e) {
            JDialog notesDialog = new FractionNotesDialog(parentFrame, true, fraction);
            notesDialog.setLocation(parentFrame.getX() + 500, parentFrame.getY() + 50);
            notesDialog.setVisible(true);
            JButton tempJB = (JButton) e.getSource();
            tuneNotesButton(tempJB, ((UPbFractionI) fraction).getFractionNotes());
            tempJB.repaint();
        }
    }

    private void tuneNotesButton(JButton tempJB, String notes) {
        if (notes.length() == 0) {
            tempJB.setFont(new Font("SansSerif", Font.PLAIN, 11));

        } else {
            tempJB.setFont(new Font("SansSerif", Font.BOLD, 11));
        }
        tempJB.setToolTipText(notes);
    }

    class changeIsZirconListener implements ChangeListener {

        private int row;
        private Fraction fraction;

        public changeIsZirconListener(Fraction fraction, int row) {
            this.row = row;
            this.fraction = fraction;
        }

        public Fraction getFraction() {
            return fraction;
        }

        public void stateChanged(ChangeEvent e) {
            AbstractButton abstractButton = (AbstractButton) e.getSource();
            boolean isZircon = abstractButton.getModel().isSelected();

            //fractionInitialPbChoice.get( row ).setEnabled(  ! isZircon );
            fractionPbBlankMassText.get(row).setEnabled(!isZircon);
            //fractionEstDateText.get( row ).setEnabled(  ! isZircon );

            // added aug 2010
            if (isZircon) {
                // fires itemlistener
                fractionInitialPbChoice.get(row).setSelectedIndex(0);
            }
            fractionInitialPbChoice.get(row).setEnabled(!isZircon);

            updateInitialPbModelChooserForRow(fraction, isZircon, row);
        }
    }

    // aug 2010
    private class changeInitialPbModelItemListener implements ItemListener {

        private int row;
        private Fraction fraction;

        public changeInitialPbModelItemListener(Fraction fraction, int row) {
            this.row = row;
            this.fraction = fraction;
        }

        public Fraction getFraction() {
            return fraction;
        }

        public void itemStateChanged(ItemEvent e) {
//            try {
//                boolean isStaceyKramersModel = ((UPbFraction) fraction).getMyLabData().getAnInitialPbModel(((String) e.getItem())) instanceof StaceyKramersInitialPbModel;
//                fractionEstDateText.get(row).setEnabled(isStaceyKramersModel);
//            } catch (BadLabDataException badLabDataException) {
//            }
        }
    }

    private void updateInitialPbModelChooserForRow(Fraction tempFrac, boolean isZircon, int row) {
        // modified aug 2010 to force backward compatibility, clear out model if not needed
//
//        AbstractRatiosDataModel initialPbNoneModel = InitialPbModelET.getNoneInstance();
//        AbstractRatiosDataModel initialPbDefaultModel = InitialPbModelET.getNoneInstance();
//        try {
//            initialPbNoneModel = mySample.getMyReduxLabData().getNoneInitialPbModel();
//            initialPbDefaultModel = mySample.getMyReduxLabData().getDefaultLabInitialPbModel();
//        } catch (BadLabDataException badLabDataException) {
//        }
//
//        // set labels for disabled chooser
//        String fractionIsZircon = "fraction is a Zircon";
//        fractionInitialPbChoice.get( row ).removeItem( fractionIsZircon );
//        if ( isZircon ) {
//            tempFrac.setInitialPbModel( initialPbNoneModel );
//            fractionInitialPbChoice.get( row ).setEnabled( false );
//            // display reason or warning
//            String reasonNoInitialPb = fractionIsZircon;
//            fractionInitialPbChoice.get( row ).addItem( reasonNoInitialPb );
//            fractionInitialPbChoice.get( row ).setSelectedItem( reasonNoInitialPb );
//        } else {
//            // if model is "none" use default otherwise proceed
//            if ( tempFrac.getInitialPbModel().equals( initialPbNoneModel ) ) {
//                tempFrac.setInitialPbModel( initialPbDefaultModel );
//            }
//            fractionInitialPbChoice.get( row ).setEnabled( true );
//            fractionInitialPbChoice.get( row ).setSelectedIndex( 0 );
//            fractionInitialPbChoice.get( row ).setSelectedItem( tempFrac.getInitialPbModel().getReduxLabDataElementName() );
//        }
//
//        boolean hasStaceyKramersModel = ((UPbFraction) tempFrac).getInitialPbModel() instanceof StaceyKramersInitialPbModelET;
//
//        ((JTextField) fractionEstDateText.get( row )).setText( tempFrac.getEstimatedDate().
//                setScale( ReduxConstants.DEFAULT_DATE_MA_SCALE, RoundingMode.HALF_UP ).//
//                toPlainString() );
//        fractionEstDateText.get( row ).setEnabled( hasStaceyKramersModel );
//
//        ((JTextField) fractionPbBlankMassText.get( row )).setText(
//                tempFrac.getAnalysisMeasure( AnalysisMeasures.pbBlankMassInGrams.getName() ).getValue().multiply(
//                ReduxConstants.PicoGramsPerGram ).setScale( ReduxConstants.DEFAULT_MASS_DISPLAY_SCALE,
//                RoundingMode.HALF_UP ).toPlainString() );
//        ((UnDoAbleDocument) ((JTextField) fractionPbBlankMassText.get( row )).getDocument()).undo.discardAllEdits();
//        fractionPbBlankMassText.get( row ).setEnabled(  ! isZircon );
//
//        ((JTextField) fractionEstDateText.get( row )).setText( tempFrac.getEstimatedDate().
//                setScale( ReduxConstants.DEFAULT_DATE_MA_SCALE, RoundingMode.HALF_UP ).//
//                toPlainString() );
//        ((UnDoAbleDocument) ((JTextField) fractionEstDateText.get( row )).getDocument()).undo.discardAllEdits();
//        fractionEstDateText.get( row ).setEnabled(  ! isZircon );

    }

    private void updateFractionRow(Fraction tempFrac, int row) {

//        // set temp variable for fractionation correction both u and Pb to use in locking fields
//        boolean fraCorrU = ((UPbFractionI) tempFrac).isFractionationCorrectedU();//.getMeanAlphaU().compareTo( BigDecimal.ZERO ) != 0);
//        boolean fraCorrPb = ((UPbFractionI) tempFrac).isFractionationCorrectedPb();//.getMeanAlphaPb().compareTo( BigDecimal.ZERO ) != 0);
//        boolean isZircon = ((UPbFractionI) tempFrac).isZircon();
//
        ((JTextField) fractionID.get(row)).setText(tempFrac.getFractionID());
//
//        ((JCheckBox) fractionZirconCheckBox.get( row )).setSelected( tempFrac.isZircon() );
//
//        fractionTracerChoice.get( row ).setSelectedItem( ((UPbFraction) tempFrac).getTracerID() );
//        // sept 2012 modified  to allow user to override this setting with checkbox
//        fractionTracerChoice.get( row ).setEnabled(  ! (fraCorrU || fraCorrPb) );
//
//        ((JTextField) fractionTracerMassText.get( row )).setText(
//                tempFrac.getAnalysisMeasure( AnalysisMeasures.tracerMassInGrams.getName() ).getValue().
//                setScale( ReduxConstants.DEFAULT_MASS_DISPLAY_SCALE,
//                RoundingMode.HALF_UP ).toPlainString() );
//        ((UnDoAbleDocument) ((JTextField) fractionTracerMassText.get( row )).getDocument()).undo.discardAllEdits();
//        fractionTracerMassText.get( row ).setEnabled(  ! (fraCorrU) );
//
//        ((JTextField) fractionMassText.get( row )).setText(
//                tempFrac.getAnalysisMeasure( AnalysisMeasures.fractionMass.getName() ).getValue().
//                setScale( ReduxConstants.DEFAULT_MASS_DISPLAY_SCALE, RoundingMode.HALF_UP ).//
//                toPlainString() );
//        ((UnDoAbleDocument) ((JTextField) fractionMassText.get( row )).getDocument()).undo.discardAllEdits();
//
//
//        // PbBlank choice
//        fractionPbBlankChoice.get( row ).setSelectedItem( ((UPbFraction) tempFrac).getPbBlank().getReduxLabDataElementName() );
//
//
//        updateInitialPbModelChooserForRow( tempFrac, isZircon, row );
//
//        // InitialPb model choice
//        fractionInitialPbChoice.get( row ).
//                setSelectedItem( ((UPbFraction) tempFrac).getInitialPbModel().getReduxLabDataElementName() );
//        fractionInitialPbChoice.get( row ).setEnabled(  ! isZircon );
//
//        ((JTextField) fractionPbBlankMassText.get( row )).setText(
//                tempFrac.getAnalysisMeasure( AnalysisMeasures.pbBlankMassInGrams.getName() ).getValue().multiply(
//                ReduxConstants.PicoGramsPerGram ).setScale( ReduxConstants.DEFAULT_MASS_DISPLAY_SCALE,
//                RoundingMode.HALF_UP ).toPlainString() );
//        ((UnDoAbleDocument) ((JTextField) fractionPbBlankMassText.get( row )).getDocument()).undo.discardAllEdits();
//        fractionPbBlankMassText.get( row ).setEnabled(  ! isZircon );
//
//        ((JTextField) fractionEstDateText.get( row )).setText( tempFrac.getEstimatedDate().
//                setScale( ReduxConstants.DEFAULT_DATE_MA_SCALE, RoundingMode.HALF_UP ).//
//                toPlainString() );
//        ((UnDoAbleDocument) ((JTextField) fractionEstDateText.get( row )).getDocument()).undo.discardAllEdits();
//        fractionEstDateText.get( row ).setEnabled(  ! isZircon );

    }

    private void removeFractionRow(Aliquot aliquot, int row) {
//        fractionDeleteButtons.remove( row );
        fractionNoteButtons.remove(row);
        fractionID.remove(row);
//        fractionZirconCheckBox.remove( row );
//        fractionTracerChoice.remove( row );
//        fractionTracerMassText.remove( row );
//        fractionMassText.remove( row );
//
//        fractionPbBlankChoice.remove( row );
//        fractionInitialPbChoice.remove( row );
//        fractionPbBlankMassText.remove( row );
//        fractionEstDateText.remove( row );
//
//        // fix row pointers in buttons
//        for (int f = 0; f < fractionDeleteButtons.size(); f ++) {
//            Fraction myFraction =
//                    ((deleteFractionListener) ((JButton) fractionDeleteButtons.get( f )).getActionListeners()[0]).getFraction();
//
//            ((JButton) fractionDeleteButtons.get( f )).removeActionListener( ((JButton) fractionDeleteButtons.get( f )).getActionListeners()[0] );
//            ((JButton) fractionDeleteButtons.get( f )).addActionListener( new deleteFractionListener( mySample, aliquot, myFraction, f ) );
//
//            ((JCheckBox) fractionZirconCheckBox.get( f )).removeChangeListener( ((JCheckBox) fractionZirconCheckBox.get( f )).getChangeListeners()[0] );
//            ((JCheckBox) fractionZirconCheckBox.get( f )).addChangeListener( new changeIsZirconListener( myFraction, f ) );
//
//            fractionInitialPbChoice.get( f ).removeItemListener( fractionInitialPbChoice.get( f ).getItemListeners()[0] );
//            fractionInitialPbChoice.get( f ).addItemListener( new changeInitialPbModelItemListener( myFraction, f ) );
//        }

    }

    /**
     *
     */
    public void setSize() {
        setSize(1120, 750);
    }

    /**
     *
     * @return
     */
    @Override
    public File getSampleFolder() {
        return sampleFolder;
    }

    /**
     *
     * @param sampleFolder
     */
    @Override
    public void setSampleFolder(File sampleFolder) {
        this.sampleFolder = sampleFolder;
    }

    private void validateSampleID(String sampleID) {
        if (!mySample.isArchivedInRegistry()) {
            boolean valid = SampleRegistries.isSampleIdentifierValidAtRegistry(//
                    sampleID);
            validSampleID_label.setText((String) (valid ? "Sample ID is Valid at registry." : "Sample ID is NOT valid at registry."));
            mySample.setValidatedSampleIGSN(valid);
        }
    }

    private void initSampleFields() throws ETException {
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
                try {
                    validateSampleID(mySample.getSampleIGSN());
                } catch (Exception e2) {
                }
            }
        });

        // april 2011
        try {
            validateSampleID(mySample.getSampleIGSN());
        } catch (Exception e) {
        }

        physicalConstantsModelChooser.removeAllItems();
        ArrayList<AbstractRatiosDataModel> physicalConstantsModels = getMySample().getMyReduxLabData().getPhysicalConstantsModels();
        for (int i = (physicalConstantsModels.size() > 1 ? 1 : 0); i < physicalConstantsModels.size(); i++) {
            physicalConstantsModelChooser.addItem(physicalConstantsModels.get(i).getNameAndVersion());
        }

        physicalConstantsModelChooser.setSelectedIndex(0);
        try {
            physicalConstantsModelChooser.setSelectedItem(getMySample().getPhysicalConstantsModel().getReduxLabDataElementName());
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }

        sampleNotes_textArea.setDocument(new UnDoAbleDocument(sampleNotes_textArea, true));
        sampleNotes_textArea.setText(getMySample().getSampleAnnotations());

        // init display fields - html allows multi-line
        sampleReduxFileName_label.setText(getMySample().getReduxSampleFilePath());
        sampleReduxFileName_label.setToolTipText("<html><p>" + getMySample().getReduxSampleFilePath() + "</p></html>");

        // set up analysisPurposeChooser
        analysisPurposeChooser.removeAllItems();
        for (ANALYSIS_PURPOSE ap : ANALYSIS_PURPOSE.values()) {
            analysisPurposeChooser.addItem(ap.toString());
        }

        analysisPurposeChooser.setSelectedItem(mySample.getAnalysisPurpose().toString());
        analysisPurposeChooser.addItemListener(new analysisPurposeItemListener());

        processSampleMetaDataFolder();
    }

    class analysisPurposeItemListener implements ItemListener {
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

    private void processSampleMetaDataFolder() {

        // check name
        if (sampleMetaDataFolder.getAbsolutePath().endsWith(ReduxConstants.NAME_OF_SAMPLEMETADATA_FOLDER) && sampleMetaDataFolder.exists()) {
            sampleMetaDataFolder_label.setText(sampleMetaDataFolder.getAbsolutePath());
            saveSampleWithDataStructure_button.setEnabled(true);
            saveAsSampleWithDataStructure_button.setEnabled(true);
        } else {
            sampleMetaDataFolder_label.setText("none selected");
            saveSampleWithDataStructure_button.setEnabled(false);
            saveAsSampleWithDataStructure_button.setEnabled(false);
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

// june 19 2012 not needed as validation already took place
//        if (  ! getMySample().getSampleIGSN().equalsIgnoreCase( sampleIGSN_text.getText() ) ) {
//            getMySample().setSampleIGSN( sampleIGSN_text.getText() );
//            getMySample().setChanged( true );
//        }
        String currentPhysicalConstantsModelName = "";
        try {
            currentPhysicalConstantsModelName = getMySample().getPhysicalConstantsModel().getNameAndVersion();

        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }

        if (!((String) physicalConstantsModelChooser.getSelectedItem()).equalsIgnoreCase(currentPhysicalConstantsModelName)) {
            try {
                getMySample().setPhysicalConstantsModel(
                        getMySample().getMyReduxLabData().
                        getAPhysicalConstantsModel(((String) physicalConstantsModelChooser.getSelectedItem())));
                //getMySample().setChanged(true);

            } catch (BadLabDataException ex) {
                new ETWarningDialog(ex).setVisible(true);
            }
        }

        getMySample().setSampleAnnotations(sampleNotes_textArea.getText());

        //setInitialized(true);
        getMySample().setChanged(true);//true

        if (myCurrentAliquot != null) {
            try {
                saveAliquot(myCurrentAliquot);// all others have been saved if changed
            } catch (Exception e) {
            }
        }

        Vector<Aliquot> aliquots = mySample.getActiveAliquots();
        for (Aliquot a : aliquots) {
            a.setAnalysisPurpose(mySample.getAnalysisPurpose());
        }

        SampleInterface.saveSampleAsSerializedReduxFile(mySample);

        try {
            setSampleFolder(new File(getMySample().getReduxSampleFilePath()).getParentFile());
        } catch (Exception e) {
            System.out.println("SampleAnalysisWorkflowOlderManager line 1685 " + e.getMessage());
        }
    }

    private void saveAliquot(Aliquot aliquot) {

        // master fields
        ((UPbReduxAliquot) aliquot).setDefaultIsZircon(masterZirconCaseCheckBox.isSelected());
        ((UPbReduxAliquot) aliquot).setDefaultTracerID((String) masterTracerChooser.getSelectedItem());
        ((UPbReduxAliquot) aliquot).setDefaultTracerMassText(masterTracerMass.getText());
        ((UPbReduxAliquot) aliquot).setDefaultFractionMassText(masterFractionMass.getText());

        ((UPbReduxAliquot) aliquot).setDefaultPbBlankID((String) masterPbBlankChooser.getSelectedItem());
        ((UPbReduxAliquot) aliquot).setDefaultInitialPbModelID((String) masterInitialPbModelChooser.getSelectedItem());

        ((UPbReduxAliquot) aliquot).setDefaultPbBlankMassText(masterPbBlankMass.getText());
        ((UPbReduxAliquot) aliquot).setDefaultEstimatedDateText(masterEstimatedDate.getText());

        // be sure aliquot has updated upbfractions too
        // using this miserablehack nov 2009
        // TODO
        mySample.getAliquotByName(aliquot.getAliquotName());

        for (Fraction f : ((UPbReduxAliquot) aliquot).getAliquotFractions()) {
            saveAliquotFraction(aliquot, f);
            f.setTimeStamp(new Date(System.currentTimeMillis()));
        }

    }

    private void saveAliquotFraction(Aliquot aliquot, Fraction tempFrac)
            throws NumberFormatException {

        // set temp variable for fractionation correction both u and Pb to use in locking fields
        boolean fraCorrU = ((UPbFraction) tempFrac).isFractionationCorrectedU();//.getMeanAlphaU().compareTo( BigDecimal.ZERO ) != 0);
        boolean fraCorrPb = ((UPbFraction) tempFrac).isFractionationCorrectedPb();//.getMeanAlphaPb().compareTo( BigDecimal.ZERO ) != 0);

        int row = ((UPbReduxAliquot) aliquot).getAliquotFractions().indexOf(tempFrac);

        // fractionID
        if (((JTextField) fractionID.get(row)).getText().trim().length() > 0) {
            tempFrac.setFractionID(((JTextField) fractionID.get(row)).getText().trim());
            tempFrac.setGrainID(tempFrac.getFractionID());
        }

//        // feb 2009
//        // test zircon state change for ReductionHandler
//        if ( tempFrac.isZircon() != ((JCheckBox) fractionZirconCheckBox.get( row )).isSelected() ) {
//            tempFrac.setZircon( ((JCheckBox) fractionZirconCheckBox.get( row )).isSelected() );
//            ((UPbFraction) tempFrac).initializeReductionHandler();
//        }
//
//        // check for locked down tracer first
//        if (allowTracerChange_checkbox.isSelected() || (  ! (fraCorrU || fraCorrPb) ) ){
//            try {
//                AbstractRatiosDataModel tracer = null;
//                tracer = ((UPbFraction) tempFrac).getMyLabData().//
//                        getATracerModel( (String) fractionTracerChoice.get( row ).getSelectedItem() );
//                ((UPbFraction) tempFrac).setTracer( tracer );
//            } catch (BadLabDataException ex) {
//                ex.printStackTrace();
//            }
//        }
//        // set fractions to Aliquot's default fractionation models
//        try {
//            ValueModel noneAlphaPb = ((UPbFraction) tempFrac).getMyLabData().getNoneAlphaPbModel();
//            if ( ((UPbFraction) tempFrac).needsAlphaPbModel() ) {
//                if ( ((UPbFraction) tempFrac).getAlphaPbModel().equals( noneAlphaPb ) ) {
//                    ValueModel alphaPb = //
//                            ((UPbFraction) tempFrac).getMyLabData().getAnAlphaPbModel( ((UPbReduxAliquot) aliquot).getDefaultAlphaPbModelID() );
//                    ((UPbFraction) tempFrac).setAlphaPbModel( alphaPb );
//                }
//            } else {
//                // does not need alphaPb model
//                ((UPbFraction) tempFrac).setAlphaPbModel( noneAlphaPb );
//            }
//        } catch (BadLabDataException badLabDataException) {
//        }
//
//        try {
//            ValueModel noneAlphaU = ((UPbFraction) tempFrac).getMyLabData().getNoneAlphaUModel();
//            if ( ((UPbFraction) tempFrac).needsAlphaUModel() ) {
//                if ( ((UPbFraction) tempFrac).getAlphaUModel().equals( noneAlphaU ) ) {
//                    ValueModel alphaU = //
//                            ((UPbFraction) tempFrac).getMyLabData().getAnAlphaUModel( ((UPbReduxAliquot) aliquot).getDefaultAlphaUModelID() );
//                    ((UPbFraction) tempFrac).setAlphaUModel( alphaU );
//                }
//            } else {
//                // does not need alphaU model
//                ((UPbFraction) tempFrac).setAlphaUModel( noneAlphaU );
//            }
//
//            // handles auto-u generation case
//            ((UPbFraction) tempFrac).setInputAlphaU( ((UPbFraction) tempFrac).getAlphaUModel() );
//
//        } catch (BadLabDataException badLabDataException) {
//        }
//        tempFrac.getAnalysisMeasure( AnalysisMeasures.tracerMassInGrams.getName() )//
//                .setValue( new BigDecimal( ((JTextField) fractionTracerMassText.get( row )).getText() ) );
//
//        tempFrac.getAnalysisMeasure( AnalysisMeasures.fractionMass.getName() )//
//                .setValue( new BigDecimal( ((JTextField) fractionMassText.get( row )).getText() ) );
//
//        try {
//            AbstractRatiosDataModel pbBlank = ((UPbFraction) tempFrac).getMyLabData().//
//                    getAPbBlankModel( (String) fractionPbBlankChoice.get( row ).getSelectedItem() );
//            ((UPbFraction) tempFrac).setPbBlank( pbBlank );
//        } catch (BadLabDataException ex) {
//        }
//
//        // set special fields for Stacey Kramers
//        tempFrac.setEstimatedDate( new BigDecimal( ((JTextField) fractionEstDateText.get( row )).getText() ) );
//
//
//        try {
//            AbstractRatiosDataModel initialPbModel = ((UPbFraction) tempFrac).getMyLabData().//
//                    getAnInitialPbModel( (String) fractionInitialPbChoice.get( row ).getSelectedItem() );
//            ((UPbFraction) tempFrac).setInitialPbModel( initialPbModel );
//
//            ((UPbFraction) tempFrac).calculateStaceyKramersInitialPbModelValues();
//
//        } catch (BadLabDataException ex) {
//        }
//        // set fraction's pbBlankMass
//        tempFrac.getAnalysisMeasure( AnalysisMeasures.pbBlankMassInGrams.getName() ).//
//                setValue( new BigDecimal( ((JTextField) fractionPbBlankMassText.get( row )).getText() ).//
//                movePointLeft( 12 ) );
        // better safe than sorry for now
        ((UPbFractionI) tempFrac).setChanged(true);

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
    public boolean canBeInitialized() {
        return //
                (sampleName_text.getText().trim().length() > 0) && //
                (mySample.getFractions().size() > 0);

    }

    /**
     *
     * @return
     */
    @Override
    public boolean isInitialized() {
        return initialized;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isNewSample() {
        return newSample;
    }

    /**
     *
     * @param newSample
     */
    @Override
    public void setNewSample(boolean newSample) {
        this.newSample = newSample;
    }

    private boolean addAliquotName() {
        String aliquotName = aliquotName_text.getText().trim();
        boolean retVal = false;

        // test for name length and existence
        if ((aliquotName.length() > 0) && (!aliquotList.contains(aliquotName))) {

            // we add aliquot name
            aliquotList.add(aliquotName);
            // add aliquot with one fraction
            try {
                myCurrentAliquot = mySample.addNewAliquot(aliquotName);

                addEmptyFractions();

                // update displayed list
                String[] aliquotArray = new String[aliquotList.size()];
                aliquotArray = aliquotList.toArray(aliquotArray);
                aliquotsList_jList.setListData(aliquotArray);
//                aliquotsList_jList.setListData((String[]) aliquotList.toArray());
                aliquotsList_jList.setSelectedIndex(aliquotList.indexOf(aliquotName));

                addAliquot_button.setEnabled(false);
                editAliquot_button.setEnabled(false);

                retVal = true;

            } catch (ETException ex) {
                new ETWarningDialog(ex).setVisible(true);
            }

        }
        return retVal;
    }

    private boolean editAliquotName() {
        int selectedIndex = aliquotsList_jList.getSelectedIndex();
        String selectedAliquotName = (String) aliquotsList_jList.getSelectedValue();
        String aliquotName = aliquotName_text.getText().trim();
        boolean retVal = false;

        // test for name length and existence
        if ((aliquotName.length() > 0) && (!aliquotList.contains(aliquotName))) {
            mySample.getAliquotByName(selectedAliquotName).setAliquotName(aliquotName);
            aliquotList.set(selectedIndex, aliquotName);
            retVal = true;

            // update displayed list
            String[] aliquotArray = new String[aliquotList.size()];
            aliquotArray = aliquotList.toArray(aliquotArray);
            aliquotsList_jList.setListData(aliquotArray);
//            aliquotsList_jList.setListData((String[]) aliquotList.toArray());
            aliquotsList_jList.setSelectedIndex(aliquotList.indexOf(aliquotName));

            addAliquot_button.setEnabled(false);
            editAliquot_button.setEnabled(false);
        }

        return retVal;
    }

    private void addEmptyFractions() {

        if (myCurrentAliquot != null) {
            // save any work
            try {
                saveSampleData();
            } catch (ETException ex) {
                new ETWarningDialog(ex).setVisible(true);
            }

            // add fractions per spinner
            for (int i = 0; i < (Integer) insertFractionCount_spinner.getValue(); i++) {
                try {
                    int aliquotNumber = ((UPbReduxAliquot) myCurrentAliquot).getAliquotNumber();
                    ((UPbSampleInterface)mySample).addDefaultUPbFractionToAliquot(aliquotNumber);
                } catch (BadLabDataException ex) {
                    new ETWarningDialog(ex).setVisible(true);
                }
            }

            completeAliquotFractionsUpdate(((UPbReduxAliquot) myCurrentAliquot).getAliquotNumber());
        }
    }

    private void importXMLFractionFiles() {

        if (myCurrentAliquot != null) {

            // save any work
            try {
                saveSampleData();
            } catch (ETException ex) {
                new ETWarningDialog(ex).setVisible(true);
            }

            int aliquotNumber = ((UPbReduxAliquot) myCurrentAliquot).getAliquotNumber();

            String importFolder = null;
            try {
                importFolder = SampleInterface.importFractionsFromXMLFilesIntoSample(mySample, getImportedXMLFractionsFolder(), aliquotNumber, true);
            } catch (FileNotFoundException fileNotFoundException) {
            } catch (BadLabDataException ex) {
                new ETWarningDialog(ex).setVisible(true);
            }

            if (importFolder != null) {
                setImportedXMLFractionsFolder(new File(importFolder));
            }

            // fix sample name due to auto-detect
            sampleName_text.setText(mySample.getSampleName());

            completeAliquotFractionsUpdate(aliquotNumber);
        }
    }

////    private Aliquot prepareAliquotForFractions() {
////        Aliquot tempA = null;
////        // update status by saving changes to aliquot name if user forgot to
////        if (addOrEditAliquotName()) {
////
////            // detect if aliquot has already been added
////            tempA = mySample.getAliquotByName(aliquotName_text.getText().trim());
////            if (tempA == null) {
////                try {
////                    tempA = mySample.addNewAliquot(aliquotName_text.getText().trim());
////                } catch (ETException eTException) {
////                }
////            }
////        }
////
////        return tempA;
////    }
    private void completeAliquotFractionsUpdate(int aliquotNumber) {
        // this call updates aliquot with labdata and fractions
        // TODO: refactor this call and similar ones for law of demeter
        myCurrentAliquot = mySample.getAliquotByNumber(aliquotNumber);

        // refresh view to show added fractions
        initAliquot(myCurrentAliquot);
    }

    private void removeAliquotAndFractions() {

        if (myCurrentAliquot != null) {
            String aliquotName = (String) aliquotsList_jList.getSelectedValue();
            int selectedIndex = aliquotsList_jList.getSelectedIndex();

            aliquotList.remove(aliquotName);

            mySample.removeAliquot(myCurrentAliquot);
            // force aliquot name to be its number ... this is a hangover from the fact that aliquots
            // are virtual and tracked in fractions by their number ... they never go away just
            // don't show if empty
            myCurrentAliquot.setAliquotName(Integer.toString(((UPbReduxAliquot) myCurrentAliquot).getAliquotNumber()));

            String[] aliquotArray = new String[aliquotList.size()];
            aliquotArray = aliquotList.toArray(aliquotArray);
            aliquotsList_jList.setListData(aliquotArray);
//            aliquotsList_jList.setListData((String[]) aliquotList.toArray());
            fastEdits_panel.removeAll();
            fastEdits_panel.repaint();

            // prevent drop off the top of list
            if ((aliquotList.size() > 0) && (selectedIndex == 0)) {
                selectedIndex = 1;
            }

            if (aliquotList.size() == 0) {
                myCurrentAliquot = null;
            }

            aliquotsList_jList.setSelectedIndex(selectedIndex - 1);
        }
    }

    private void moveAliquotUpOne() {
        int saveSelectedIndex = aliquotsList_jList.getSelectedIndex();

        if (saveSelectedIndex > 0) {
            // get myself
            String myAliquotName = (String) aliquotsList_jList.getSelectedValue();

            // get my predecessor
            String prevAliquotName = (String) aliquotsList_jList.getModel().getElementAt(saveSelectedIndex - 1);

            if (mySample.swapOrderOfTwoAliquots(myAliquotName, prevAliquotName)) {

                // switch them in the model
                aliquotList.set(saveSelectedIndex, prevAliquotName);//.setElementAt(prevAliquotName, saveSelectedIndex);
                aliquotList.set(saveSelectedIndex - 1, myAliquotName);//.setElementAt(myAliquotName, saveSelectedIndex - 1);
                String[] aliquotArray = new String[aliquotList.size()];
                aliquotArray = aliquotList.toArray(aliquotArray);
                aliquotsList_jList.setListData(aliquotArray);
//                aliquotsList_jList.setListData((String[]) aliquotList.toArray());

                aliquotsList_jList.setSelectedIndex(saveSelectedIndex - 1);
            }
        }

    }

    private void moveAliquotDownOne() {
        int saveSelectedIndex = aliquotsList_jList.getSelectedIndex();

        if (saveSelectedIndex < (aliquotsList_jList.getModel().getSize() - 1)) {
            // get myself
            String myAliquotName = (String) aliquotsList_jList.getSelectedValue();

            // get my successor
            String succAliquotName = (String) aliquotsList_jList.getModel().getElementAt(saveSelectedIndex + 1);

            if (mySample.swapOrderOfTwoAliquots(myAliquotName, succAliquotName)) {

                // switch them in the model
                aliquotList.set(saveSelectedIndex, succAliquotName);//.setElementAt(succAliquotName, saveSelectedIndex);
                aliquotList.set(saveSelectedIndex + 1, myAliquotName);//.setElementAt(myAliquotName, saveSelectedIndex + 1);
                String[] aliquotArray = new String[aliquotList.size()];
                aliquotArray = aliquotList.toArray(aliquotArray);
                aliquotsList_jList.setListData(aliquotArray);
//                aliquotsList_jList.setListData((String[]) aliquotList.toArray());

                aliquotsList_jList.setSelectedIndex(saveSelectedIndex + 1);
            }
        }
    }

    private void saveSaveAsOK(boolean doSaveAs) {
        if (canBeInitialized()) {
            mySample.setSampleName(sampleName_text.getText().trim());

            File sampleFile = null;

            try {
                saveSampleData();
            } catch (ETException ex) {
                new ETWarningDialog(ex).setVisible(true);
            }

            if (!doSaveAs && (new File(mySample.getReduxSampleFilePath()).exists())) {
                SampleInterface.saveSampleAsSerializedReduxFile(mySample);
                // capture sample name in file for MRUlist below
                sampleFile = new File(mySample.getReduxSampleFilePath());
            } else {
                try {
                    sampleFile = SampleInterface.saveSampleFileAs(mySample, ((ETReduxFrame)parentFrame).getMyState().getMRUSampleFolderPath());
                    setSampleFolder(new File(sampleFile.getParent()));
                } catch (BadLabDataException ex) {
                    new ETWarningDialog(ex).setVisible(true);
                } catch (NullPointerException nullPointerException) {
                }
            }

            if (sampleFile != null) {
                ((ETReduxFrame) getParentFrame()).getMyState().updateMRUSampleList(sampleFile);
                initialized = true;
            } else {
                initialized = false;
            }
        }

        // dec 2011 update sample date models
        SampleInterface.updateAndSaveSampleDateModelsByAliquot(mySample);

        close();
    }

    private void saveSaveAsWithDataStructure(boolean doSaveAs) {
        if (canBeInitialized()) {
            mySample.setSampleName(sampleName_text.getText().trim());

            File sampleFile = null;

            try {
                saveSampleData();
            } catch (ETException ex) {
                new ETWarningDialog(ex).setVisible(true);
            }

            if (!doSaveAs && (new File(mySample.getReduxSampleFilePath()).exists())) {
                SampleInterface.saveSampleAsSerializedReduxFile(mySample);
                sampleFile = new File(mySample.getReduxSampleFilePath());
            } else {
                try {
                    sampleFile = SampleInterface.saveSampleFileAs(mySample, ((ETReduxFrame)parentFrame).getMyState().getMRUSampleFolderPath());
                    try {
                        setSampleFolder(new File(sampleFile.getParent()));
                    } catch (Exception e) {
                    }
                } catch (BadLabDataException ex) {
                    new ETWarningDialog(ex).setVisible(true);
                }
            }

            try {
                if (sampleFile != null) {
                    ((ETReduxFrame) getParentFrame()).getMyState().updateMRUSampleList(sampleFile);
                    saveSampleDataStructure();
                    initialized = true;
                } else {
                    initialized = false;
                }
            } catch (ETException ex) {
                new ETWarningDialog(ex).setVisible(true);
            }

        }
        close();

    }

    /**
     *
     */
    @Override
    public void close() {
        super.close();
        initialized = //
                initialized
                && (sampleName_text.getText().trim().length() > 0) && //
                (mySample.getFractions().size() > 0);

        setVisible(false);
    }

    /**
     * Provides for repainting pane when scrollbar is used
     */
    class JscrollPaneScrollListener implements AdjustmentListener {
        // This method is called whenever the value of a scrollbar is changed,
        // either by the user or programmatically.

        public void adjustmentValueChanged(AdjustmentEvent evt) {
            Adjustable source = evt.getAdjustable();

            // getValueIsAdjusting() returns true if the user is currently
            // dragging the scrollbar's knob and has not picked a final value
            if (evt.getValueIsAdjusting()) {
                // The user is dragging the knob
                return;
            } else {
                ((javax.swing.JScrollBar) source).getParent().repaint();
            }

            // Determine which scrollbar fired the event
            int orient = source.getOrientation();
            if (orient == Adjustable.HORIZONTAL) {
                // Event from horizontal scrollbar
            } else {
                // Event from vertical scrollbar
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
            //int value = evt.getValue();
        }
    }

    private void setSampleMetaDataFolder() throws FileNotFoundException {

        String dialogTitle = "Select the SampleMetaData Folder: ";

        sampleMetaDataFolder
                = FileHelper.AllPlatformGetFolder(dialogTitle, getSampleMetaDataFolder());

        processSampleMetaDataFolder();
    }

    private void saveSampleDataStructure() throws ETException {

//        // PRE: folders have been specified for Sample and SampleMetaData
//
//        mySample.setAutomaticDataUpdateMode( true );
//
//        // process aliquot folders: if they exist, empty them of xml files and delete if possible
//        // June 2010 realized this was dangerous as it deletes all xml files in all folders
//        // so modified filter to get just aliquot folders
//
//        File[] aliquotFolders = getSampleFolder().listFiles( new FileFilter() {
//
//            public boolean accept ( File pathname ) {
//                return (pathname.isDirectory()) //
//                        && (getMySample().getAliquotByName( pathname.getName() ) != null);
//            }
//        } );
//
//        for (int i = 0; i < aliquotFolders.length; i ++) {
//            // remove any toplevel files
//            File[] filesToDelete = aliquotFolders[i].listFiles( new FilenameFilter() {
//
//                public boolean accept ( File dir, String name ) {
//                    return name.toLowerCase().endsWith( ".xml" );
//                }
//            } );
//            for (int f = 0; f < filesToDelete.length; f ++) {
//                filesToDelete[f].delete();
//            }
//
//            // delete aliquot folder if it contains nothing else
//            aliquotFolders[i].delete();
//        }
//
//        // then create current aliquot folders and populate with xml fraction files
//        FractionMetaData[] fractionsMetaData = new FractionMetaData[mySample.getFractions().size()];
//        int fractionMetaDataCount = 0;
//        String sampleMetaDataFileName = mySample.getSampleName();
//
//        for (int i = 0; i < mySample.getAliquots().size(); i ++) {
//            if ( ((UPbReduxAliquot) mySample.getAliquots().get( i )).getAliquotFractions().size() > 0 ) {
//
//                String aliquotName = mySample.getAliquots().get( i ).getAliquotName();
//                //sampleMetaDataFileName += "_" + aliquotName;
//
//                File aliquotFolder = null;
//                try {
//                    //System.out.println(getSampleFolder().getCanonicalPath() + File.separator + aliquotName);
//                    aliquotFolder = new File( getSampleFolder().getCanonicalPath() + File.separator + aliquotName );
//                } catch (IOException iOException) {
//                }
//                if (  ! aliquotFolder.exists() ) {
//                    aliquotFolder.mkdir();
//                }
//
//                Vector<Fraction> fractions = ((UPbReduxAliquot) mySample.getAliquots().get( i )).getAliquotFractions();
//
//                for (int f = 0; f < fractions.size(); f ++) {
//                    // for each fraction, write out the U part and the Pb part
//                    String U_fractionFileName =
//                            mySample.getSampleName()//
//                            + "_" + fractions.get( f ).getFractionID()//
//                            + "_U"//
//                            + ".xml";
//
//                    String Pb_fractionFileName =
//                            mySample.getSampleName()//
//                            + "_" + fractions.get( f ).getFractionID()//
//                            + "_Pb"//
//                            + ".xml";
//
//                    File fractionFile = null;
//                    try {
//                        // export U
//                        fractionFile = new File( aliquotFolder.getCanonicalPath() + File.separator + U_fractionFileName );
//                        ((UPbFraction) fractions.get( f )).setRatioType( "U" );
//                        ((UPbFraction) fractions.get( f )).serializeXMLObject( fractionFile.getCanonicalPath() );
//                    } catch (IOException iOException) {
//                    }
//                    try {
//                        // export Pb
//                        fractionFile = new File( aliquotFolder.getCanonicalPath() + File.separator + Pb_fractionFileName );
//                        ((UPbFraction) fractions.get( f )).setRatioType( "Pb" );
//                        ((UPbFraction) fractions.get( f )).serializeXMLObject( fractionFile.getCanonicalPath() );
//                    } catch (IOException iOException) {
//                    }
//
//                    // set type back
//                    ((UPbFraction) fractions.get( f )).setRatioType( "UPb" );
//
//                    // update fractionMetaData
//                    fractionsMetaData[fractionMetaDataCount ++] = new FractionMetaData(//
//                            fractions.get( f ).getFractionID(), aliquotName, U_fractionFileName, Pb_fractionFileName );
//                }
//            }
//        }
//
//        // write sample meta data file for this sample to SampleMetaDataFolder
//        // first confirm / create SampleMetaData folder
//
//        if ( sampleMetaDataFolder.exists() ) {
////////            File sampleMetaDataFolder = null;
////////            try {
////////                File containingFolder = new File( this.sampleMetaDataFolder.getCanonicalPath() );
////////                File[] testForContainingFolder = containingFolder.listFiles( new FilenameFilter() {
////////
////////                    public boolean accept ( File dir, String name ) {
////////                        return name.equalsIgnoreCase( ReduxConstants.NAME_OF_SAMPLEMETADATA_FOLDER );
////////                    }
////////                } );
////////
////////                sampleMetaDataFolder = new File(//
////////                        this.sampleMetaDataFolder.getCanonicalPath()//
////////                        + File.separator //
////////                        + ReduxConstants.NAME_OF_SAMPLEMETADATA_FOLDER );
////////
////////                if ( testForContainingFolder.length == 0 ) {
////////                    sampleMetaDataFolder.mkdir();
////////                }
////////            } catch (IOException iOException) {
////////                throw new ETException( parentFrame, "SampleMetaDataFolder could not be found ... please set and confirm connectivity." );
////////            }
////////
//            try {
//                SampleMetaData sampleMetaData = new SampleMetaData(//
//                        mySample.getSampleName(), sampleFolder.getCanonicalPath() );
//
//                sampleMetaData.setFractionsMetaData( fractionsMetaData );
//
//                sampleMetaData.serializeXMLObject(//
//                        sampleMetaDataFolder.getCanonicalPath()//
//                        + File.separator//
//                        + sampleMetaDataFileName + ".xml" );
//            } catch (IOException iOException) {
//                throw new ETException( parentFrame, "SampleMetaDataFolder could not be found ... please set and confirm connectivity." );
//            }
//        } else {
//            throw new ETException( parentFrame, "SampleMetaDataFolder could not be found ... please set and confirm connectivity." );
//        }
//
//        // 4. alert user to start Tripoli
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
        sampleToolBar_buttonGroup = new javax.swing.ButtonGroup();
        sampleType_panel = new javax.swing.JPanel();
        sampleType_label = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        saveSampleWithDataStructure_button = new javax.swing.JButton();
        setSampleMetaDataFolder_button = new javax.swing.JButton();
        saveAsSampleWithDataStructure_button = new javax.swing.JButton();
        saveAsSample_button = new javax.swing.JButton();
        revertToSaved_button = new javax.swing.JButton();
        closeAndSave_button = new javax.swing.JButton();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        sampleIGSN_text = new javax.swing.JTextField();
        sampleID_label = new javax.swing.JLabel();
        sampleName_text = new javax.swing.JTextField();
        sampleName_label = new javax.swing.JLabel();
        physicalConstantsModel_label = new javax.swing.JLabel();
        aliquotName_text = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        aliquotsList_jList = new javax.swing.JList<String>();
        aliquotName_label = new javax.swing.JLabel();
        insertFractionCount_spinner = new javax.swing.JSpinner();
        insertFraction_button = new javax.swing.JButton();
        removeAliquot_button = new javax.swing.JButton();
        aliquotFractionsArea_label = new javax.swing.JLabel();
        fastEdits_scrollPane = new javax.swing.JScrollPane();
        fastEdits_panel = new javax.swing.JPanel();
        sampleNotes_label = new javax.swing.JLabel();
        sampleNotes_scrollPane = new javax.swing.JScrollPane();
        sampleNotes_textArea = new javax.swing.JTextArea();
        sampleReduxFile_label = new javax.swing.JLabel();
        sampleReduxFileName_label = new javax.swing.JLabel();
        sampleMetaData_label = new javax.swing.JLabel();
        sampleMetaDataFolder_label = new javax.swing.JLabel();
        aliquotsArea_label = new javax.swing.JLabel();
        addAliquot_button = new javax.swing.JButton();
        importFractionFiles_button = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        welcomeStatement_jTextArea = new javax.swing.JTextArea();
        samples_scrollPane = new javax.swing.JScrollPane();
        samples_textArea = new javax.swing.JTextArea();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTextArea2 = new javax.swing.JTextArea();
        jScrollPane6 = new javax.swing.JScrollPane();
        jTextArea4 = new javax.swing.JTextArea();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTextArea5 = new javax.swing.JTextArea();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTextArea3 = new javax.swing.JTextArea();
        moveAliquotDown_button = new javax.swing.JButton();
        moveAliquotUp_button = new javax.swing.JButton();
        editAliquot_button = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        chooseAnalysisPurpose_label = new javax.swing.JLabel();
        analysisPurposeChooser = new javax.swing.JComboBox<String>();
        physicalConstantsModelChooser = new javax.swing.JComboBox<String>();
        sampleRegistry_label = new javax.swing.JLabel();
        sampleRegistryChooser = new javax.swing.JComboBox<SampleRegistries>();
        validSampleID_label = new javax.swing.JLabel();
        allowTracerChange_checkbox = new javax.swing.JCheckBox();
        validateSampleID_button = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        sampleType_panel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        sampleType_label.setBackground(new java.awt.Color(255, 204, 102));
        sampleType_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        sampleType_label.setText("LA-ICP-MS Workflow Manager for   A N A L Y S I S     M O D E    of a Sample");
        sampleType_label.setOpaque(true);

        org.jdesktop.layout.GroupLayout sampleType_panelLayout = new org.jdesktop.layout.GroupLayout(sampleType_panel);
        sampleType_panel.setLayout(sampleType_panelLayout);
        sampleType_panelLayout.setHorizontalGroup(
            sampleType_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(sampleType_label, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1154, Short.MAX_VALUE)
        );
        sampleType_panelLayout.setVerticalGroup(
            sampleType_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(sampleType_panelLayout.createSequentialGroup()
                .add(sampleType_label)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        jPanel2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        saveSampleWithDataStructure_button.setBackground(new java.awt.Color(255, 255, 255));
        saveSampleWithDataStructure_button.setForeground(new java.awt.Color(255, 51, 0));
        saveSampleWithDataStructure_button.setText("Save Sample for Live Workflow");
        saveSampleWithDataStructure_button.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        sampleToolBar_buttonGroup.add(saveSampleWithDataStructure_button);
        saveSampleWithDataStructure_button.setContentAreaFilled(false);
        saveSampleWithDataStructure_button.setFocusPainted(false);
        saveSampleWithDataStructure_button.setOpaque(true);
        saveSampleWithDataStructure_button.setPreferredSize(new java.awt.Dimension(110, 23));
        saveSampleWithDataStructure_button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                sampleToolBox_buttonMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                sampleToolBox_buttonMouseEntered(evt);
            }
        });
        saveSampleWithDataStructure_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveSampleWithDataStructure_buttonActionPerformed(evt);
            }
        });
        jPanel2.add(saveSampleWithDataStructure_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 2, 207, 30));

        setSampleMetaDataFolder_button.setBackground(new java.awt.Color(255, 255, 255));
        setSampleMetaDataFolder_button.setForeground(new java.awt.Color(255, 51, 0));
        setSampleMetaDataFolder_button.setText("Set SampleMetaData Folder");
        setSampleMetaDataFolder_button.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        sampleToolBar_buttonGroup.add(setSampleMetaDataFolder_button);
        setSampleMetaDataFolder_button.setContentAreaFilled(false);
        setSampleMetaDataFolder_button.setFocusPainted(false);
        setSampleMetaDataFolder_button.setOpaque(true);
        setSampleMetaDataFolder_button.setPreferredSize(new java.awt.Dimension(110, 23));
        setSampleMetaDataFolder_button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                sampleToolBox_buttonMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                sampleToolBox_buttonMouseEntered(evt);
            }
        });
        setSampleMetaDataFolder_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setSampleMetaDataFolder_buttonActionPerformed(evt);
            }
        });
        jPanel2.add(setSampleMetaDataFolder_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(823, 2, 205, 30));

        saveAsSampleWithDataStructure_button.setBackground(new java.awt.Color(255, 255, 255));
        saveAsSampleWithDataStructure_button.setForeground(new java.awt.Color(255, 51, 0));
        saveAsSampleWithDataStructure_button.setText("SaveAs Sample for Live Workflow");
        saveAsSampleWithDataStructure_button.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        sampleToolBar_buttonGroup.add(saveAsSampleWithDataStructure_button);
        saveAsSampleWithDataStructure_button.setContentAreaFilled(false);
        saveAsSampleWithDataStructure_button.setFocusPainted(false);
        saveAsSampleWithDataStructure_button.setOpaque(true);
        saveAsSampleWithDataStructure_button.setPreferredSize(new java.awt.Dimension(110, 23));
        saveAsSampleWithDataStructure_button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                sampleToolBox_buttonMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                sampleToolBox_buttonMouseEntered(evt);
            }
        });
        saveAsSampleWithDataStructure_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsSampleWithDataStructure_buttonActionPerformed(evt);
            }
        });
        jPanel2.add(saveAsSampleWithDataStructure_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(577, 2, 246, 30));

        saveAsSample_button.setBackground(new java.awt.Color(255, 255, 255));
        saveAsSample_button.setForeground(new java.awt.Color(255, 51, 0));
        saveAsSample_button.setText("SaveAs Sample");
        saveAsSample_button.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        sampleToolBar_buttonGroup.add(saveAsSample_button);
        saveAsSample_button.setContentAreaFilled(false);
        saveAsSample_button.setFocusPainted(false);
        saveAsSample_button.setOpaque(true);
        saveAsSample_button.setPreferredSize(new java.awt.Dimension(110, 23));
        saveAsSample_button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                sampleToolBox_buttonMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                sampleToolBox_buttonMouseEntered(evt);
            }
        });
        saveAsSample_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsSample_buttonActionPerformed(evt);
            }
        });
        jPanel2.add(saveAsSample_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(467, 2, -1, 30));

        revertToSaved_button.setBackground(new java.awt.Color(255, 255, 255));
        revertToSaved_button.setForeground(new java.awt.Color(255, 51, 0));
        revertToSaved_button.setText("Revert to Saved");
        revertToSaved_button.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        sampleToolBar_buttonGroup.add(revertToSaved_button);
        revertToSaved_button.setContentAreaFilled(false);
        revertToSaved_button.setFocusPainted(false);
        revertToSaved_button.setOpaque(true);
        revertToSaved_button.setPreferredSize(new java.awt.Dimension(110, 23));
        revertToSaved_button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                sampleToolBox_buttonMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                sampleToolBox_buttonMouseEntered(evt);
            }
        });
        revertToSaved_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                revertToSaved_buttonActionPerformed(evt);
            }
        });
        jPanel2.add(revertToSaved_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(150, 2, -1, 30));

        closeAndSave_button.setBackground(new java.awt.Color(255, 255, 255));
        closeAndSave_button.setForeground(new java.awt.Color(255, 51, 0));
        closeAndSave_button.setText("Close and Save");
        closeAndSave_button.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        sampleToolBar_buttonGroup.add(closeAndSave_button);
        closeAndSave_button.setContentAreaFilled(false);
        closeAndSave_button.setFocusPainted(false);
        closeAndSave_button.setOpaque(true);
        closeAndSave_button.setPreferredSize(new java.awt.Dimension(110, 23));
        closeAndSave_button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                sampleToolBox_buttonMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                sampleToolBox_buttonMouseEntered(evt);
            }
        });
        closeAndSave_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeAndSave_buttonActionPerformed(evt);
            }
        });
        jPanel2.add(closeAndSave_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 2, 150, 30));

        jLayeredPane1.setBackground(new java.awt.Color(245, 236, 206));
        jLayeredPane1.setOpaque(true);

        sampleIGSN_text.setEditable(false);
        sampleIGSN_text.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        sampleIGSN_text.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        sampleIGSN_text.setText("<none>");
        sampleIGSN_text.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                sampleIGSN_textFocusLost(evt);
            }
        });
        jLayeredPane1.add(sampleIGSN_text);
        sampleIGSN_text.setBounds(270, 30, 100, 27);

        sampleID_label.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        sampleID_label.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        sampleID_label.setText("Sample ID:");
        jLayeredPane1.add(sampleID_label);
        sampleID_label.setBounds(200, 30, 70, 30);

        sampleName_text.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        sampleName_text.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        sampleName_text.setText("Sample Name");
        jLayeredPane1.add(sampleName_text);
        sampleName_text.setBounds(130, 0, 190, 27);

        sampleName_label.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        sampleName_label.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        sampleName_label.setText("Local Sample Name:");
        jLayeredPane1.add(sampleName_label);
        sampleName_label.setBounds(10, 0, 120, 30);

        physicalConstantsModel_label.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        physicalConstantsModel_label.setForeground(new java.awt.Color(204, 51, 0));
        physicalConstantsModel_label.setText("Set Physical Constants Model :");
        jLayeredPane1.add(physicalConstantsModel_label);
        physicalConstantsModel_label.setBounds(720, 30, 177, 30);

        aliquotName_text.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        aliquotName_text.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        aliquotName_text.setText("Aliquot Name");
        jLayeredPane1.add(aliquotName_text);
        aliquotName_text.setBounds(300, 100, 220, 27);

        aliquotsList_jList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(aliquotsList_jList);

        jLayeredPane1.add(jScrollPane2);
        jScrollPane2.setBounds(50, 100, 240, 146);

        aliquotName_label.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        aliquotName_label.setText("Aliquot name: choose Add or Change");
        jLayeredPane1.add(aliquotName_label);
        aliquotName_label.setBounds(290, 80, 230, 14);

        insertFractionCount_spinner.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLayeredPane1.add(insertFractionCount_spinner);
        insertFractionCount_spinner.setBounds(460, 160, 52, 26);

        insertFraction_button.setForeground(new java.awt.Color(255, 51, 0));
        insertFraction_button.setText("Insert Fractions:");
        insertFraction_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertFraction_buttonActionPerformed(evt);
            }
        });
        jLayeredPane1.add(insertFraction_button);
        insertFraction_button.setBounds(330, 160, 130, 29);

        removeAliquot_button.setText("Remove Selected Aliquot");
        removeAliquot_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeAliquot_buttonActionPerformed(evt);
            }
        });
        jLayeredPane1.add(removeAliquot_button);
        removeAliquot_button.setBounds(300, 220, 220, 29);

        aliquotFractionsArea_label.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        aliquotFractionsArea_label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        aliquotFractionsArea_label.setText("Aliquot Fractions Fast Entry (click Aliquot in aliquot list above):");
        jLayeredPane1.add(aliquotFractionsArea_label);
        aliquotFractionsArea_label.setBounds(20, 260, 360, 17);

        fastEdits_panel.setBackground(new java.awt.Color(255, 255, 255));

        org.jdesktop.layout.GroupLayout fastEdits_panelLayout = new org.jdesktop.layout.GroupLayout(fastEdits_panel);
        fastEdits_panel.setLayout(fastEdits_panelLayout);
        fastEdits_panelLayout.setHorizontalGroup(
            fastEdits_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 32692, Short.MAX_VALUE)
        );
        fastEdits_panelLayout.setVerticalGroup(
            fastEdits_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 729, Short.MAX_VALUE)
        );

        fastEdits_scrollPane.setViewportView(fastEdits_panel);

        jLayeredPane1.add(fastEdits_scrollPane);
        fastEdits_scrollPane.setBounds(20, 300, 1080, 280);

        sampleNotes_label.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        sampleNotes_label.setText("Notes about this Sample:");
        jLayeredPane1.add(sampleNotes_label);
        sampleNotes_label.setBounds(10, 600, 150, 14);

        sampleNotes_textArea.setColumns(20);
        sampleNotes_textArea.setRows(5);
        sampleNotes_textArea.setWrapStyleWord(true);
        sampleNotes_textArea.setMaximumSize(new java.awt.Dimension(250, 80));
        sampleNotes_textArea.setPreferredSize(new java.awt.Dimension(250, 80));
        sampleNotes_scrollPane.setViewportView(sampleNotes_textArea);

        jLayeredPane1.add(sampleNotes_scrollPane);
        sampleNotes_scrollPane.setBounds(160, 590, 940, 35);

        sampleReduxFile_label.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        sampleReduxFile_label.setText("Path to this Sample:");
        jLayeredPane1.add(sampleReduxFile_label);
        sampleReduxFile_label.setBounds(407, 0, 120, 20);

        sampleReduxFileName_label.setBackground(new java.awt.Color(240, 240, 240));
        sampleReduxFileName_label.setText("<Not Saved>");
        sampleReduxFileName_label.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        sampleReduxFileName_label.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        sampleReduxFileName_label.setOpaque(true);
        jLayeredPane1.add(sampleReduxFileName_label);
        sampleReduxFileName_label.setBounds(540, 0, 570, 20);

        sampleMetaData_label.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        sampleMetaData_label.setText("SampleMetaData  Folder:");
        jLayeredPane1.add(sampleMetaData_label);
        sampleMetaData_label.setBounds(10, 639, 150, 14);

        sampleMetaDataFolder_label.setBackground(new java.awt.Color(240, 240, 240));
        sampleMetaDataFolder_label.setText("<Not Saved>");
        sampleMetaDataFolder_label.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        sampleMetaDataFolder_label.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        sampleMetaDataFolder_label.setOpaque(true);
        jLayeredPane1.add(sampleMetaDataFolder_label);
        sampleMetaDataFolder_label.setBounds(160, 635, 940, 20);

        aliquotsArea_label.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        aliquotsArea_label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        aliquotsArea_label.setText("Aliquots:  click Aliquot name to change it:");
        jLayeredPane1.add(aliquotsArea_label);
        aliquotsArea_label.setBounds(10, 80, 250, 14);

        addAliquot_button.setForeground(new java.awt.Color(255, 51, 0));
        addAliquot_button.setText("Add Name");
        addAliquot_button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        addAliquot_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addAliquot_buttonActionPerformed(evt);
            }
        });
        jLayeredPane1.add(addAliquot_button);
        addAliquot_button.setBounds(300, 130, 100, 29);

        importFractionFiles_button.setForeground(new java.awt.Color(255, 51, 0));
        importFractionFiles_button.setText("Import Fraction Files");
        importFractionFiles_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importFractionFiles_buttonActionPerformed(evt);
            }
        });
        jLayeredPane1.add(importFractionFiles_button);
        importFractionFiles_button.setBounds(330, 190, 190, 29);

        welcomeStatement_jTextArea.setBackground(new java.awt.Color(238, 255, 255));
        welcomeStatement_jTextArea.setColumns(20);
        welcomeStatement_jTextArea.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        welcomeStatement_jTextArea.setLineWrap(true);
        welcomeStatement_jTextArea.setRows(5);
        welcomeStatement_jTextArea.setText("A sample in U-Pb_Redux is composed of aliquots, or physical pieces of the sample (e.g. a zircon separate), which in turn are composed of fractions, or individual U/Pb analyses.  For more information about each, see their tabs here, or U-Pb_Redux Help.\n\nTo create a new aliquot, type its name in the Aliquot Name dialog box, increment the counter below to the number of fractions it contains (you can add more at any time), and then select ''Add Name''.  Add any number of aliquots in this way, and order them in the data table with the arrows to the left. After entering the requisite information for each fraction, press ''SaveAs Sample'' or \"SaveAs Sample for Live Workflow\".  Navigate to the desired location, then create a new folder for your sample before saving the .redux file inside.  U-Pb_Redux will create a sub-folder for each aliquot, populated with empty data files for each Pb and U measurement.");
        welcomeStatement_jTextArea.setWrapStyleWord(true);
        welcomeStatement_jTextArea.setMargin(new java.awt.Insets(0, 5, 0, 5));
        jScrollPane1.setViewportView(welcomeStatement_jTextArea);

        jTabbedPane1.addTab("Introduction", jScrollPane1);

        samples_textArea.setBackground(new java.awt.Color(238, 255, 255));
        samples_textArea.setColumns(20);
        samples_textArea.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        samples_textArea.setLineWrap(true);
        samples_textArea.setRows(5);
        samples_textArea.setText("A sample is a single collection of a geologic material from one location.  This name is shared by each of the sample's fractions, or U/Pb analyses.  You don't have to give the .redux file or the folder that contains it the exact same name as the sample.  However, when you export an analysis from Tripoli for Redux, you must provide the sample name correctly, or Redux won't recognize it.  A sample name may not contain space--use hyphens or underscores instead.\n\nTo upload your sample to the GeoChron database, you may request an IGSN, or International Geologic Sample Number, through SESAR ( www.geosamples.org ).  U-Pb_Redux will guide you through this process when you publish from the Aliquot page.\n");
        samples_textArea.setWrapStyleWord(true);
        samples_textArea.setMargin(new java.awt.Insets(0, 5, 0, 5));
        samples_scrollPane.setViewportView(samples_textArea);

        jTabbedPane1.addTab("Samples", samples_scrollPane);

        jTextArea2.setBackground(new java.awt.Color(238, 255, 255));
        jTextArea2.setColumns(20);
        jTextArea2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jTextArea2.setLineWrap(true);
        jTextArea2.setRows(5);
        jTextArea2.setText("Aliquots are physical pieces of a sample whose dates you wish to interpret separately.  For instance, zircon and titanite mineral separates could be two aliquots.  Pieces of a sample sent to two different labs, or whose mineral separation was performed with two different techniques, would also be two aliquots.\n\nNew aliquots may be added at any time to an existing sample.  In this window, type a new name (over an existing name) in the Aliquot Name dialog box, increment the counter below to number of fractions it contains (you can add more fractions at any time), and then select ''Add Name''.  \n");
        jTextArea2.setWrapStyleWord(true);
        jTextArea2.setMargin(new java.awt.Insets(0, 5, 0, 5));
        jScrollPane4.setViewportView(jTextArea2);

        jTabbedPane1.addTab("Aliquots", jScrollPane4);

        jTextArea4.setBackground(new java.awt.Color(238, 255, 255));
        jTextArea4.setColumns(20);
        jTextArea4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jTextArea4.setLineWrap(true);
        jTextArea4.setRows(5);
        jTextArea4.setText("A fraction is a paired U+Pb analysis.  For instance, a zircon analyzed by TIMS or a spot zapped by LA-ICPMS would be separate fractions, even if both analyzed the same grain.  \n\nTo add fractions to this sample, choose an aliquot at left, increment the counter found to the right of the ``Insert Fractions'' button to the desired number of fractions, then click ''Add Fractions''.  Fractions may be removed by clicking the red X on the left side of its row in the table.  If you have already made U/Pb measurements and exported them from Tripoli (as .xml files), you can import them from this window.  Click the ''Import Fraction Files'' button, then navigate to and select the analyses you want to import.  You can hold control or shift to select multiple .xml files.  If these are the first fractions created for this sample, your sample will automatically be given the name from the imported file.  ");
        jTextArea4.setWrapStyleWord(true);
        jTextArea4.setMargin(new java.awt.Insets(0, 5, 0, 5));
        jScrollPane6.setViewportView(jTextArea4);

        jTabbedPane1.addTab("Fractions", jScrollPane6);

        jTextArea5.setBackground(new java.awt.Color(238, 255, 255));
        jTextArea5.setColumns(20);
        jTextArea5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jTextArea5.setLineWrap(true);
        jTextArea5.setRows(5);
        jTextArea5.setText("The Live Workflow mode establishes a direct link between Tripoli and U-Pb_Redux. Clicking the ''START Live Workflow'' button at the bottom of U-Pb_Redux’s main screen immediately imports data from Tripoli and prompts U-Pb_Redux to update its calculations. \n\nTo enable Live Workflow, you must create a folder called ''SampleMetaData'' and then click the ''Set SampleMetaData Folder'' button. \n\nTripoli must also be told the location of this SampleMetaData folder.  In Tripoli, point to this SampleMetaData folder with the ''Set Live Workflow Data Folder'' item from the Control Panel menu.\n");
        jTextArea5.setWrapStyleWord(true);
        jTextArea5.setMargin(new java.awt.Insets(0, 5, 0, 5));
        jScrollPane7.setViewportView(jTextArea5);

        jTabbedPane1.addTab("Live Workflow", jScrollPane7);

        jTextArea3.setBackground(new java.awt.Color(238, 255, 255));
        jTextArea3.setColumns(20);
        jTextArea3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jTextArea3.setLineWrap(true);
        jTextArea3.setRows(5);
        jTextArea3.setText("Redux can also read in new data files or refresh data from overwritten data files, as long as they are organized into aliquot sub-folders in your sample’s folder.  After saving or changing xml files, press the ''Update Data'' button at the bottom of the Redux main window.  \n\nWhile you can import new fractions this way, you must create new aliquots from this window, using the interface at left.");
        jTextArea3.setWrapStyleWord(true);
        jTextArea3.setMargin(new java.awt.Insets(0, 5, 0, 5));
        jScrollPane5.setViewportView(jTextArea3);

        jTabbedPane1.addTab("Manual Update", jScrollPane5);

        jLayeredPane1.add(jTabbedPane1);
        jTabbedPane1.setBounds(520, 60, 590, 210);

        moveAliquotDown_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/earthtime/images/Arrow-down-32.png"))); // NOI18N
        moveAliquotDown_button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        moveAliquotDown_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveAliquotDown_buttonActionPerformed(evt);
            }
        });
        jLayeredPane1.add(moveAliquotDown_button);
        moveAliquotDown_button.setBounds(20, 170, 33, 40);

        moveAliquotUp_button.setBackground(new java.awt.Color(255, 255, 255));
        moveAliquotUp_button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/earthtime/images/Arrow-up-32.png"))); // NOI18N
        moveAliquotUp_button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        moveAliquotUp_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveAliquotUp_buttonActionPerformed(evt);
            }
        });
        jLayeredPane1.add(moveAliquotUp_button);
        moveAliquotUp_button.setBounds(20, 120, 33, 40);

        editAliquot_button.setForeground(new java.awt.Color(255, 51, 0));
        editAliquot_button.setText("Save edited");
        editAliquot_button.setEnabled(false);
        editAliquot_button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        editAliquot_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editAliquot_buttonActionPerformed(evt);
            }
        });
        jLayeredPane1.add(editAliquot_button);
        editAliquot_button.setBounds(400, 130, 120, 29);

        jLabel1.setBackground(new java.awt.Color(238, 255, 255));
        jLabel1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(204, 51, 0));
        jLabel1.setText("                            Fraction ID                                                Tracer                     Tracer Mass     Fraction Mass                    Pb Blank                         Initial Pb Model                    Est Date         Pb Blank Mass");
        jLabel1.setOpaque(true);
        jLayeredPane1.add(jLabel1);
        jLabel1.setBounds(20, 280, 1080, 20);

        chooseAnalysisPurpose_label.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        chooseAnalysisPurpose_label.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        chooseAnalysisPurpose_label.setText("Analysis purpose:");
        jLayeredPane1.add(chooseAnalysisPurpose_label);
        chooseAnalysisPurpose_label.setBounds(410, 30, 110, 30);
        jLayeredPane1.add(analysisPurposeChooser);
        analysisPurposeChooser.setBounds(540, 30, 180, 27);

        physicalConstantsModelChooser.setBackground(new java.awt.Color(245, 236, 206));
        jLayeredPane1.add(physicalConstantsModelChooser);
        physicalConstantsModelChooser.setBounds(898, 30, 200, 27);

        sampleRegistry_label.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        sampleRegistry_label.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        sampleRegistry_label.setText("Registry:");
        jLayeredPane1.add(sampleRegistry_label);
        sampleRegistry_label.setBounds(10, 30, 53, 30);

        sampleRegistryChooser.setBackground(new java.awt.Color(245, 236, 206));
        jLayeredPane1.add(sampleRegistryChooser);
        sampleRegistryChooser.setBounds(60, 32, 140, 27);

        validSampleID_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        validSampleID_label.setForeground(new java.awt.Color(204, 51, 0));
        validSampleID_label.setText("<html>Validity unknown.</html>");
        jLayeredPane1.add(validSampleID_label);
        validSampleID_label.setBounds(170, 60, 200, 20);

        allowTracerChange_checkbox.setFont(new java.awt.Font("Arial", 1, 10)); // NOI18N
        allowTracerChange_checkbox.setForeground(new java.awt.Color(204, 0, 0));
        allowTracerChange_checkbox.setText("Allow tracer change");
        allowTracerChange_checkbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allowTracerChange_checkboxActionPerformed(evt);
            }
        });
        jLayeredPane1.add(allowTracerChange_checkbox);
        allowTracerChange_checkbox.setBounds(380, 257, 140, 20);

        validateSampleID_button.setText("?");
        validateSampleID_button.setToolTipText("click to validate");
        validateSampleID_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                validateSampleID_buttonActionPerformed(evt);
            }
        });
        jLayeredPane1.add(validateSampleID_button);
        validateSampleID_button.setBounds(370, 33, 20, 20);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jLayeredPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1156, Short.MAX_VALUE)
            .add(sampleType_panel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 1156, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(sampleType_panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLayeredPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 674, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void saveSampleWithDataStructure_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveSampleWithDataStructure_buttonActionPerformed
        // april 2010 test whether sample folder has been set
        boolean saveAs = (getMySample().getReduxSampleFilePath() == null);
        saveSaveAsWithDataStructure(saveAs);
    }//GEN-LAST:event_saveSampleWithDataStructure_buttonActionPerformed

    private void insertFraction_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertFraction_buttonActionPerformed
        addEmptyFractions();
    }//GEN-LAST:event_insertFraction_buttonActionPerformed

    private void removeAliquot_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeAliquot_buttonActionPerformed
        removeAliquotAndFractions();
    }//GEN-LAST:event_removeAliquot_buttonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        close();
    }//GEN-LAST:event_formWindowClosing

    private void setSampleMetaDataFolder_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setSampleMetaDataFolder_buttonActionPerformed
        try {
            setSampleMetaDataFolder();
        } catch (FileNotFoundException fileNotFoundException) {
        }
    }//GEN-LAST:event_setSampleMetaDataFolder_buttonActionPerformed

    private void saveAsSampleWithDataStructure_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsSampleWithDataStructure_buttonActionPerformed
        saveSaveAsWithDataStructure(true);
    }//GEN-LAST:event_saveAsSampleWithDataStructure_buttonActionPerformed

    private void addAliquot_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addAliquot_buttonActionPerformed
        addAliquotName();
    }//GEN-LAST:event_addAliquot_buttonActionPerformed

    private void importFractionFiles_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importFractionFiles_buttonActionPerformed
        importXMLFractionFiles();
    }//GEN-LAST:event_importFractionFiles_buttonActionPerformed

    private void saveAsSample_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsSample_buttonActionPerformed
        saveSaveAsOK(true);
    }//GEN-LAST:event_saveAsSample_buttonActionPerformed

    private void moveAliquotUp_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveAliquotUp_buttonActionPerformed
        moveAliquotUpOne();
    }//GEN-LAST:event_moveAliquotUp_buttonActionPerformed

    private void editAliquot_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editAliquot_buttonActionPerformed
        editAliquotName();
    }//GEN-LAST:event_editAliquot_buttonActionPerformed

    private void moveAliquotDown_buttonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveAliquotDown_buttonActionPerformed
        moveAliquotDownOne();
}//GEN-LAST:event_moveAliquotDown_buttonActionPerformed

    private void sampleIGSN_textFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_sampleIGSN_textFocusLost
        try {
            validateSampleID(((SampleRegistries) sampleRegistryChooser.getSelectedItem()).getCode() + "." + sampleIGSN_text.getText().trim());
        } catch (Exception e) {
        }
    }//GEN-LAST:event_sampleIGSN_textFocusLost

    private void revertToSaved_buttonActionPerformed ( java.awt.event.ActionEvent evt ) {//GEN-FIRST:event_revertToSaved_buttonActionPerformed
        initAliquot(myCurrentAliquot);
    }//GEN-LAST:event_revertToSaved_buttonActionPerformed

    private void sampleToolBox_buttonMouseEntered ( java.awt.event.MouseEvent evt ) {//GEN-FIRST:event_sampleToolBox_buttonMouseEntered
        ((JButton) evt.getSource()).setBackground(new Color(200, 200, 200));
    }//GEN-LAST:event_sampleToolBox_buttonMouseEntered

    private void sampleToolBox_buttonMouseExited ( java.awt.event.MouseEvent evt ) {//GEN-FIRST:event_sampleToolBox_buttonMouseExited
        ((JButton) evt.getSource()).setBackground(Color.white);
    }//GEN-LAST:event_sampleToolBox_buttonMouseExited

    private void closeAndSave_buttonActionPerformed ( java.awt.event.ActionEvent evt ) {//GEN-FIRST:event_closeAndSave_buttonActionPerformed
        saveSaveAsOK(false);
    }//GEN-LAST:event_closeAndSave_buttonActionPerformed

    private void allowTracerChange_checkboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allowTracerChange_checkboxActionPerformed
        boolean enabled = false;
        if (((JCheckBox) evt.getSource()).isSelected()) {
            enabled = true;
        }

        for (int i = 0; i < fractionTracerChoice.size(); i++) {
            fractionTracerChoice.get(i).setEnabled(enabled);
        }
    }//GEN-LAST:event_allowTracerChange_checkboxActionPerformed

    private void validateSampleID_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_validateSampleID_buttonActionPerformed
        try {
            validateSampleID(((SampleRegistries) sampleRegistryChooser.getSelectedItem()).getCode() + "." + sampleIGSN_text.getText().trim());
        } catch (Exception e) {
        }
    }//GEN-LAST:event_validateSampleID_buttonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addAliquot_button;
    private javax.swing.JLabel aliquotFractionsArea_label;
    private javax.swing.JLabel aliquotName_label;
    private javax.swing.JTextField aliquotName_text;
    private javax.swing.JLabel aliquotsArea_label;
    private javax.swing.JList<String> aliquotsList_jList;
    private javax.swing.JCheckBox allowTracerChange_checkbox;
    private javax.swing.JComboBox<String> analysisPurposeChooser;
    private javax.swing.JLabel chooseAnalysisPurpose_label;
    private javax.swing.JButton closeAndSave_button;
    private javax.swing.ButtonGroup destinationOfFractionsOptions_buttonGroup;
    private javax.swing.JButton editAliquot_button;
    private javax.swing.JPanel fastEdits_panel;
    private javax.swing.JScrollPane fastEdits_scrollPane;
    private javax.swing.JButton importFractionFiles_button;
    private javax.swing.JSpinner insertFractionCount_spinner;
    private javax.swing.JButton insertFraction_button;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea jTextArea2;
    private javax.swing.JTextArea jTextArea3;
    private javax.swing.JTextArea jTextArea4;
    private javax.swing.JTextArea jTextArea5;
    private javax.swing.JButton moveAliquotDown_button;
    private javax.swing.JButton moveAliquotUp_button;
    private javax.swing.JComboBox<String> physicalConstantsModelChooser;
    private javax.swing.JLabel physicalConstantsModel_label;
    private javax.swing.JButton removeAliquot_button;
    private javax.swing.JButton revertToSaved_button;
    private javax.swing.JLabel sampleID_label;
    private javax.swing.JTextField sampleIGSN_text;
    private javax.swing.JLabel sampleMetaDataFolder_label;
    private javax.swing.JLabel sampleMetaData_label;
    private javax.swing.JLabel sampleName_label;
    private javax.swing.JTextField sampleName_text;
    private javax.swing.JLabel sampleNotes_label;
    private javax.swing.JScrollPane sampleNotes_scrollPane;
    private javax.swing.JTextArea sampleNotes_textArea;
    private javax.swing.JLabel sampleReduxFileName_label;
    private javax.swing.JLabel sampleReduxFile_label;
    private javax.swing.JComboBox<SampleRegistries> sampleRegistryChooser;
    private javax.swing.JLabel sampleRegistry_label;
    private javax.swing.ButtonGroup sampleToolBar_buttonGroup;
    private javax.swing.JLabel sampleType_label;
    private javax.swing.JPanel sampleType_panel;
    private javax.swing.JScrollPane samples_scrollPane;
    private javax.swing.JTextArea samples_textArea;
    private javax.swing.JButton saveAsSampleWithDataStructure_button;
    private javax.swing.JButton saveAsSample_button;
    private javax.swing.JButton saveSampleWithDataStructure_button;
    private javax.swing.JButton setSampleMetaDataFolder_button;
    private javax.swing.ButtonGroup sourceOfFractionsOptions_buttonGroup;
    private javax.swing.ButtonGroup updateMode_buttonGroup;
    private javax.swing.JLabel validSampleID_label;
    private javax.swing.JButton validateSampleID_button;
    private javax.swing.JTextArea welcomeStatement_jTextArea;
    // End of variables declaration//GEN-END:variables
}
