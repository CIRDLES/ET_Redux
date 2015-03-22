/*
 * LabDataEditorDialog.java
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
package org.earthtime.UPb_Redux.dialogs;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.help.CSH;
import javax.help.DefaultHelpBroker;
import javax.help.HelpBroker;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.UPb_Redux.filters.XMLFileFilter;
import org.earthtime.UPb_Redux.reduxLabData.ReduxLabData;
import org.earthtime.UPb_Redux.reduxLabData.ReduxLabDataListElementI;
import org.earthtime.UPb_Redux.utilities.JHelpAction;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException;
import org.earthtime.beans.ET_JButton;
import org.earthtime.dataDictionaries.DataDictionary;
import org.earthtime.dataDictionaries.Lambdas;
import org.earthtime.dataDictionaries.MineralTypes;
import org.earthtime.exceptions.ETException;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataModels.initialPbModelsET.InitialPbModelET;
import org.earthtime.ratioDataModels.initialPbModelsET.StaceyKramersInitialPbModelET;
import org.earthtime.ratioDataModels.mineralStandardModels.MineralStandardUPbModel;
import org.earthtime.ratioDataModels.pbBlankICModels.PbBlankICModel;
import org.earthtime.ratioDataModels.physicalConstantsModels.PhysicalConstantsModel;
import org.earthtime.ratioDataModels.tracers.TracerUPbModel;
import org.earthtime.ratioDataViews.AbstractRatiosDataView;
import org.earthtime.ratioDataViews.MineralStandardUPbRatiosDataViewEditable;
import org.earthtime.ratioDataViews.MineralStandardUPbRatiosDataViewNotEditable;
import org.earthtime.ratioDataViews.PhysicalConstantsDataViewEditable;
import org.earthtime.ratioDataViews.PhysicalConstantsDataViewNotEditable;
import org.earthtime.ratioDataViews.RatiosDataViewEditable;
import org.earthtime.ratioDataViews.RatiosDataViewNotEditable;
import org.earthtime.ratioDataViews.TracerUPbRatiosDataViewEditable;
import org.earthtime.ratioDataViews.TracerUPbRatiosDataViewNotEditable;
import org.earthtime.utilities.FileHelper;

/**
 *
 * @author James F. Bowring
 */
public class LabDataEditorDialog extends DialogEditor {

    // Fields
    //private boolean MAC_OS_X;
    private final int VERTICAL_OFFSET_MODEL_VIEW = 35;
    private ReduxLabData myLabData;
    private AbstractRatiosDataModel newEmptyTracerModel;
    private ValueModel newEmptyAlphaUModel;
    private ValueModel newEmptyAlphaPbModel;
    private AbstractRatiosDataModel newEmptyPbBlankModel;
    private AbstractRatiosDataModel newEmptyInitialPbModel;
    private AbstractRatiosDataModel newEmptyPhysicalConstantsModel;
    private AbstractRatiosDataModel newEmptyMineralStandardModel;
    private String savedTracerModelName;
    private String savedAlphaUModelName;
    private String savedAlphaPbModelName;
    private String savedPbBlankModelName;
    private String savedInitialPbModelName;
    private AbstractRatiosDataModel currentEditableInitialPbModel;
    private AbstractRatiosDataModel currentEditableMineralStandardModel;
    private AbstractRatiosDataModel currentEditableTracerModel;
    private AbstractRatiosDataModel currentEditablePbBlankModel;
    private AbstractRatiosDataModel currentEditablePhysicalConstantsModel;
    private String savedPhysicalConstantsModelName;
    private String savedMineralStandardModelName;
    private int currentTabIndex;
    private AbstractRatiosDataView mineralStandardModelView;
    private AbstractRatiosDataView tracerModelView;
    private AbstractRatiosDataView initialPbModelView;
    private AbstractRatiosDataView pbBlankModelView;
    private AbstractRatiosDataView physicalConstantsModelView;

    /**
     * Creates new form LabDataEditorDialog
     *
     * @param parent
     * @param modal
     * @param labData
     * @param selectTab
     * @throws BadLabDataException
     */
    public LabDataEditorDialog(
            java.awt.Frame parent,
            boolean modal,
            ReduxLabData labData,
            int selectTab) throws BadLabDataException {

        super(parent, modal);

        amOpen = true;

        this.myLabData = labData;

        initComponents();

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        setTitle("Redux Saved Lab Data");

        this.newEmptyTracerModel = null;
        this.newEmptyAlphaUModel = null;
        this.newEmptyAlphaPbModel = null;
        this.newEmptyPbBlankModel = null;
        this.newEmptyInitialPbModel = null;
        this.newEmptyPhysicalConstantsModel = null;
        this.newEmptyMineralStandardModel = null;

        this.savedTracerModelName = myLabData.getDefaultLabTracer().getReduxLabDataElementName();
        this.savedAlphaUModelName = myLabData.getDefaultLabAlphaUModel().getName();
        this.savedAlphaPbModelName = myLabData.getDefaultLabAlphaPbModel().getName();
        this.savedPbBlankModelName = myLabData.getDefaultLabPbBlank().getReduxLabDataElementName();
        this.savedInitialPbModelName = myLabData.getDefaultLabInitialPbModel().getReduxLabDataElementName();
        this.currentEditableInitialPbModel = null;
        this.savedPhysicalConstantsModelName = myLabData.getDefaultPhysicalConstantsModel().getNameAndVersion();
        this.savedMineralStandardModelName = myLabData.getDefaultTIMSMineralStandardModel().getReduxLabDataElementName();

        // top panel
        labName_text.setDocument(new UnDoAbleDocument(labName_text, true));

        // Stacey-Kramers Calculator for InitialPb
        estimatedAgeInMA_text.setDocument(//
                new BigDecimalDocument(estimatedAgeInMA_text, true));
        estimatedAgeInMA_text.setText("0");
        correlationCoefficients_text.setDocument( //
                new BigDecimalDocument(correlationCoefficients_text, true));
        correlationCoefficients_text.setText("0");
        relativeUncertainty_text.setDocument(//
                new BigDecimalDocument(relativeUncertainty_text, true));
        relativeUncertainty_text.setText("0");

        // Fractionation tab
        InitializeAlphaUTabTextBoxes(false);
        InitializeAlphaPbTabTextBoxes(false);

        // Lab Default tab
        InitializeLabDefaultsTabTextBoxes(true);

        // set up tabs
        tracersTab_panel.setBackground(ReduxConstants.myFractionGreenColor);
        PbBlankICsTab_panel.setBackground(ReduxConstants.myFractionGreenColor);
        initialPbModelsTab_panel.setBackground(ReduxConstants.myFractionGreenColor);

        fractionationModelsTab_panel.setBackground(ReduxConstants.myAliquotGrayColor);
        mineralStandard_panel.setBackground(ReduxConstants.dataModelGray);

        physicalConstantsModels_panel.setBackground(ReduxConstants.mySampleYellowColor);

        showSavedLabData(selectTab);

    }

    private void showSavedLabData(int selectTab)
            throws BadLabDataException {

        setDetailsPaneTab(selectTab);

        labName_text.setText(myLabData.getLabName());

        TracerItemListener tracerActionListener = new TracerItemListener();
        tracerModelChooser.addItemListener(tracerActionListener);
        initTracerModelChooser();

        InitAlphaUChooser();
        InitAlphaPbChooser();

        PbBlankItemListener pbBlankActionListener = new PbBlankItemListener();
        PbBlankChooser.addItemListener(pbBlankActionListener);
        initPbBlankModelChooser();

        InitialPbModelItemListener initialPbModelActionListener = new InitialPbModelItemListener();
        InitialPbModelChooser.addItemListener(initialPbModelActionListener);
        initInitialPbModelChooser();

        PhysicalConstantsItemListener physicalConstantsItemListener = new PhysicalConstantsItemListener();
        physicalConstantsModel_Chooser.addItemListener(physicalConstantsItemListener);
        initPhysicalConstantsModelChooser();

        MineralStandardModelItemListener MineralStandardModelItemListener
                = new MineralStandardModelItemListener();
        MineralStandardModelChooser.addItemListener(MineralStandardModelItemListener);
        initMineralStandardModelChooser();

        showSavedLabDefaults();

        showSavedLAICPMSLabDefaults();

    }

    /**
     *
     * @param selectTab
     */
    public void setDetailsPaneTab(int selectTab) {
        details_pane.setSelectedIndex(selectTab);
        try {
            setCurrentTabIndex(selectTab);

        } catch (BadLabDataException badLabDataException) {
        }
    }

    private void saveLabData() {
        // we save lab facts without asking
        myLabData.setLabName(labName_text.getText());

    }

    /**
     *
     * @return
     */
    public ReduxLabData getMyLabData() {
        return myLabData;
    }

    /**
     *
     * @param myLabData
     */
    public void setMyLabData(ReduxLabData myLabData) {
        this.myLabData = myLabData;
    }

    // Tracers
    /**
     *
     * @return
     */
    public String getSavedTracerModelName() {
        return savedTracerModelName;
    }

    /**
     *
     * @param savedTracerModelName
     */
    public void setSavedTracerModelName(String savedTracerModelName) {
        this.savedTracerModelName = savedTracerModelName;
    }

    private void populateTracerModelFields(AbstractRatiosDataModel tracerModel, boolean editable)
            throws BadLabDataException {

        tracerModel.initializeModel();

        Dimension tracerViewDimension = new Dimension(//
                tracersTab_panel.getWidth(), tracersTab_panel.getHeight() - VERTICAL_OFFSET_MODEL_VIEW);
        if (editable) {
            tracerModelView = //
                    new TracerUPbRatiosDataViewEditable(tracerModel, tracerViewDimension, false);
        } else {
            tracerModelView = //
                    new TracerUPbRatiosDataViewNotEditable(tracerModel, tracerViewDimension, false);
        }

        tracerModelView.setBackground(tracersTab_panel.getBackground());
        tracerModelView.setBounds(tracersTab_panel.getBounds());
        tracerModelView.setLocation(0, VERTICAL_OFFSET_MODEL_VIEW);
        for (Component c : tracersTab_panel.getComponents()) {
            if (c instanceof AbstractRatiosDataView) {
                tracersTab_panel.remove(c);
            }
        }
        tracersTab_panel.add(tracerModelView);
        tracersTab_panel.validate();
        tracerModelView.repaint();

    }

    private void initTracerModelChooser() throws BadLabDataException {

        // set up Tracer chooser
        tracerModelChooser.removeAllItems();
        ArrayList<AbstractRatiosDataModel> tracers = myLabData.getTracers();
        for (int i = (tracers.size() > 1 ? 1 : 0); i < tracers.size(); i++) {
            tracerModelChooser.addItem(((AbstractRatiosDataModel) tracers.get(i)).getReduxLabDataElementName());
        }

        tracerModelChooser.setSelectedIndex(0);
        tracerModelChooser.setSelectedItem(savedTracerModelName);

    }

    class TracerItemListener implements ItemListener {
        // This method is called only if a new item has been selected.

        public void itemStateChanged(ItemEvent evt) {
            // Get the affected tracerModel
            AbstractRatiosDataModel tracerModel = null;
            try {
                tracerModel = myLabData.getATracerModel((String) evt.getItem());
            } catch (BadLabDataException ex) {
            }

            if (evt.getStateChange() == ItemEvent.SELECTED) {
                try {

                    // Item was just selected
                    populateTracerModelFields(tracerModel, false);

                    // tracerNotes_text.setText(tracerModel.getTracerNotes());
                } catch (BadLabDataException ex) {
                    Logger.getLogger(LabDataEditorDialog.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else if (evt.getStateChange() == ItemEvent.DESELECTED) {
                // Item is no longer selected
            }
        }
    }

    /**
     *
     * @return
     */
    public AbstractRatiosDataModel getNewEmptyTracer() {
        return newEmptyTracerModel;
    }

    /**
     *
     * @param newEmptyTracer
     */
    public void setNewEmptyTracer(AbstractRatiosDataModel newEmptyTracer) {
        this.newEmptyTracerModel = newEmptyTracer;
        if (tracerModelChooser.getSelectedIndex() >= 0) {
            savedTracerModelName = (String) tracerModelChooser.getSelectedItem();
        } else {
            try {
                savedTracerModelName = myLabData.getDefaultLabTracer().getReduxLabDataElementName();
            } catch (BadLabDataException ex) {
            }
        }
    }

    private void editNewEmptyTracerModel()
            throws BadLabDataException {
        newEmptyTracerModel = TracerUPbModel.createNewInstance();
        tracerModelChooser.setSelectedIndex(-1);
        tracerModelChooser.setEnabled(false);
        populateTracerModelFields(newEmptyTracerModel, true);

    }

    private void editCopyOfCurrentTracerModel()
            throws BadLabDataException {

        try {
            currentEditableTracerModel = myLabData.getATracerModel(
                    (String) tracerModelChooser.getSelectedItem()).copyModel();

            tracerModelChooser.setSelectedIndex(-1);
            tracerModelChooser.setEnabled(false);

            newEmptyTracerModel = currentEditableTracerModel;
            populateTracerModelFields(currentEditableTracerModel, true);

        } catch (BadLabDataException ex) {
        }

    }

    private synchronized boolean checkIsSavedStatusOfTracerModelEdit()
            throws BadLabDataException {
        boolean retval = true;

        if (newEmptyTracerModel != null) {

            int response = JOptionPane.showConfirmDialog(this,
                    new String[]{"You have not saved the edited Tracer UPb Model ... Proceed anyway ?"},
                    "ET Redux Warning",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            retval = (response == JOptionPane.YES_OPTION);
        }
        if (retval) {
            cancelNewTracerModelEdit();
        }

        return retval;
    }

    private synchronized void cancelNewTracerModelEdit()
            throws BadLabDataException {
        newEmptyTracerModel = null;
        tracerModelChooser.setEnabled(true);

        try {
            initTracerModelChooser();
        } catch (BadLabDataException ex) {
        }
    }

    private synchronized void registerTracerModel(final AbstractRatiosDataModel tempModel)
            throws HeadlessException, BadLabDataException {

        myLabData.registerTracer(tempModel, true);
        initTracerModelChooser();
        tracerModelChooser.setSelectedItem(tempModel.getReduxLabDataElementName());
    }

    private void removeCurrentTracerModel() throws BadLabDataException {
        // see also menu item is turned off

        String tracerModelName = (String) tracerModelChooser.getSelectedItem();
        tracerModelChooser.removeAllItems();
        myLabData.removeATracer(tracerModelName);

        initTracerModelChooser();
    }

    private boolean exportTracerAsXML()
            throws BadLabDataException, ETException {

        File selectedFile;

        // identify TracerModel for export
        if (newEmptyTracerModel == null) {
            // we are exporting an existing TracerModel
            newEmptyTracerModel
                    = myLabData.getATracerModel(
                            (String) tracerModelChooser.getSelectedItem());
        } else {
            tracerModelView.saveAndUpdateModelView(true);
        }

        setAlwaysOnTop(false);

        String dialogTitle = "Save this Tracer as xml: *.xml";
        final String fileExtension = ".xml";
        String tracerFileName
                = newEmptyTracerModel.getReduxLabDataElementName() + fileExtension;
        FileFilter nonMacFileFilter = new XMLFileFilter();

        selectedFile = FileHelper.AllPlatformSaveAs(
                new Frame(), dialogTitle, null, fileExtension, tracerFileName, nonMacFileFilter);

        if (selectedFile != null) {
            try {
                // export
                newEmptyTracerModel.serializeXMLObject(selectedFile.getCanonicalPath());

                if (newEmptyTracerModel != null) {
                    // Feb 2008 now per Noah automatically import it as well
                    cancelNewTracerModelEdit();
                    readAndRegisterTracerModel(selectedFile);
                }
            } catch (IOException ex) {
            }
        }

        setAlwaysOnTop(true);
        return (selectedFile != null);
    }

    private synchronized void importLocalTracerModel() throws BadLabDataException {
        String dialogTitle = "Select a U-Pb Redux Tracer File to Open: *.xml";
        final String fileExtension = ".xml";
        FileFilter nonMacFileFilter = new XMLFileFilter();

        setAlwaysOnTop(false);

        File returnFile
                = FileHelper.AllPlatformGetFile(dialogTitle, null, fileExtension, nonMacFileFilter, false, new JFrame())[0];

        if (returnFile != null) {
            readAndRegisterTracerModel(returnFile);
        }

        // wait to make visible so messages appear
        setAlwaysOnTop(true);

    }

    private synchronized void readAndRegisterTracerModel(File returnFile) {

        AbstractRatiosDataModel tempTracer = TracerUPbModel.createNewInstance();

        try {
            tempTracer = tempTracer.readXMLObject(returnFile.getCanonicalPath(), true);//true );
        } catch (FileNotFoundException ex) {
            tempTracer = null;
        } catch (IOException ex) {
            tempTracer = null;
        } catch (ETException | BadOrMissingXMLSchemaException ex) {
            tempTracer = null;
        }

        if (tempTracer != null) {
            try {
                registerTracerModel(tempTracer);
            } catch (BadLabDataException badLabDataException) {
            }
        } else {
            JOptionPane.showConfirmDialog(
                    null,
                    new String[]{"This Tracer could not be imported...please confirm it conforms to the schema."},
                    "ET Redux Warning",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private synchronized void importEarthTimeTracer() throws BadLabDataException {
//        JDialog myEditor =
//                new EarthTimeTracerDialog( null, true, Tracer.getListOfEarthTimeTracers() );
//        myEditor.setSize( 350, 300 );
//        JDialog.setDefaultLookAndFeelDecorated( true );
//
//        setAlwaysOnTop( false );
//        myEditor.setVisible( true );
//
//        UPbReduxConfigurator myConfigurator = new UPbReduxConfigurator();
//
//        AbstractRatiosDataModel myTracer = TracerUPbModel.createNewInstance();
//        if (  ! ((EarthTimeTracerDialog) myEditor).getSelectedTracerName().equals( "" ) ) {
//            try {
//                myTracer =
//                        (AbstractRatiosDataModel) myTracer.readXMLObject(
//                        myConfigurator.getResourceURI( "URI_EARTHTIME_XMLTracers" )//
//                        + ((EarthTimeTracerDialog) myEditor).getSelectedTracerName(), true );
//            } catch (FileNotFoundException ex) {
//                ex.printStackTrace();
//                myTracer = null;
//            } catch (ETException ex) {
//                ex.printStackTrace();
//                myTracer = null;
//            } catch (BadOrMissingXMLSchemaException ex) {
//                ex.printStackTrace();
//                myTracer = null;
//            }
//
//
//        } else {
//            myTracer = null;
//        }
//        if ( myTracer != null ) {
//            registerTracerModel( myTracer );
//        }
//        setAlwaysOnTop( true );
    }

    // Fractionation Tab *******************************************************
    /**
     *
     * @return
     */
    public String getSavedAlphaUModelName() {
        return savedAlphaUModelName;
    }

    /**
     *
     * @param savedAlphaUModelName
     */
    public void setSavedAlphaUModelName(String savedAlphaUModelName) {
        this.savedAlphaUModelName = savedAlphaUModelName;
    }

    private void InitializeAlphaUTabTextBoxes(boolean editable) {

        // set properties of text boxes AlphaU tab
        alphaUName_text.setDocument(new UnDoAbleDocument(alphaUName_text, editable));

        alphaU_text.setDocument(new BigDecimalDocument(alphaU_text, editable));
        alphaUoneSigma_text.setDocument(new BigDecimalDocument(alphaUoneSigma_text, editable));

    }

    private void saveAlphaUFields(ValueModel alphaU) {

        alphaU.setName(alphaUName_text.getText());

        double tempA = Double.valueOf(alphaU_text.getText());
        double tempQ = tempA / 100.00;
        alphaU.setValue(new BigDecimal(Double.toString(tempQ)));

        tempA = Double.valueOf(alphaUoneSigma_text.getText());
        tempQ = tempA / 100.00;
        alphaU.setOneSigma(new BigDecimal(Double.toString(tempQ)));
    }

    private void PopulateAlphaUFields(ValueModel alphaU)
            throws BadLabDataException {

        // set visible flag if default tracerModel showing
        alphaUName_label.setForeground(
                (alphaU.getName().equalsIgnoreCase(
                        myLabData.getDefaultLabAlphaUModel().getName()))
                        ? Color.RED : Color.BLACK);

        alphaUName_text.setText(alphaU.getName());

        alphaU_text.setText(alphaU.getValue().multiply(
                new BigDecimal("100.00")).
                setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE, RoundingMode.HALF_UP).toPlainString());

        alphaUoneSigma_text.setText(alphaU.getOneSigma().multiply(
                new BigDecimal("100.00")).
                setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE, RoundingMode.HALF_UP).toPlainString());

    }

    private void InitAlphaUChooser()
            throws BadLabDataException {

        // set up AlphaU chooser
        alphaUChooser.removeAllItems();
        ArrayList<ValueModel> alphaUModels = myLabData.getAlphaUModels();
        for (int i = 1; i < alphaUModels.size(); i++) {
            alphaUChooser.addItem(((ValueModel) alphaUModels.get(i)).getName());
        }

        // handle AlphaU chooser
        AlphaUItemListener alphaUActionListener = new AlphaUItemListener();
        alphaUChooser.addItemListener(alphaUActionListener);

        // labData always has at least the none tracerModel
        alphaUChooser.setSelectedIndex(-1);
        alphaUChooser.setSelectedItem(getSavedAlphaUModelName());

    }

    class AlphaUItemListener implements ItemListener {
        // This method is called only if a new item has been selected.

        public void itemStateChanged(ItemEvent evt) {
            JComboBox alphaUChooser = (JComboBox) evt.getSource();

            // Get the affected AlphaUModel
            ValueModel alphaUModel = null;
            try {
                alphaUModel = myLabData.getAnAlphaUModel((String) evt.getItem());
            } catch (BadLabDataException ex) {
                ex.printStackTrace();
            }

            if (evt.getStateChange() == ItemEvent.SELECTED) {
                try {

                    // Item was just selected
                    PopulateAlphaUFields(alphaUModel);
                } catch (BadLabDataException ex) {
                    Logger.getLogger(LabDataEditorDialog.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else if (evt.getStateChange() == ItemEvent.DESELECTED) {
                // Item is no longer selected
            }
        }
    }

    /**
     *
     * @return
     */
    public ValueModel getNewEmptyAlphaUModel() {
        return newEmptyAlphaUModel;
    }

    /**
     *
     * @param newEmptyAlphaUModel
     */
    public void setNewEmptyAlphaUModel(ValueModel newEmptyAlphaUModel) {
        this.newEmptyAlphaUModel = newEmptyAlphaUModel;
    }

    private void EditNewEmptyAlphaUModel()
            throws BadLabDataException {

        setNewEmptyAlphaUModel(new ValueModel("New Alpha U Model", "ABS"));
        alphaUChooser.setSelectedIndex(-1);
        alphaUChooser.setEnabled(false);
        InitializeAlphaUTabTextBoxes(true);
        PopulateAlphaUFields(getNewEmptyAlphaUModel());
    }

    private synchronized boolean CheckIsSavedStatusOfAlphaUModelEdit()
            throws BadLabDataException {
        boolean retval = true;

        if (getNewEmptyAlphaUModel() != null) {
            //setVisible(false);
            int response = JOptionPane.showConfirmDialog(this,
                    new String[]{"You have not saved the edited Alpha U Model ... Proceed anyway ?"},
                    "ET Redux Warning",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            retval = (response == JOptionPane.YES_OPTION);
        }
        if (retval) {
            CancelNewAlphaUModelEdit();
        } else {
            //setVisible(true);
        }

        return retval;
    }

    private synchronized void CancelNewAlphaUModelEdit()
            throws BadLabDataException {
        setNewEmptyAlphaUModel(null);
        alphaUChooser.setEnabled(true);
        InitializeAlphaUTabTextBoxes(false);
        try {
            InitAlphaUChooser();
        } catch (BadLabDataException ex) {
            ex.printStackTrace();
        }

    }

    private synchronized void RegisterAlphaUModel(final ValueModel tempAlphaUModel)
            throws HeadlessException, BadLabDataException {
        myLabData.registerAlphaUModel(tempAlphaUModel, true);
        InitAlphaUChooser();
        alphaUChooser.setSelectedItem(tempAlphaUModel.getName());
    }

    private void RemoveCurrentAlphaUModel()
            throws BadLabDataException {
        // see also menu item is turned off
        if (!(((String) alphaUChooser.getSelectedItem()).equalsIgnoreCase("<none>") || ((String) alphaUChooser.getSelectedItem()).equalsIgnoreCase(ReduxConstants.NONE))) {
            String tempName = (String) alphaUChooser.getSelectedItem();

            alphaUChooser.removeAllItems();
            myLabData.removeAnAlphaUModel(tempName);
        }

        InitAlphaUChooser();
    }

    private boolean ExportAlphaUModelAsXML()
            throws BadLabDataException {
        File selectedFile = null;

        // identify AlphaUModel for export
        ValueModel exportAlphaUModel = null;
        if (getNewEmptyAlphaUModel() == null) {
            // we are exporting an existing tracerModel
            exportAlphaUModel = myLabData.getAnAlphaUModel((String) alphaUChooser.getSelectedItem());
        } else {
            // we are exporting the new AlphaUModel under edit
            // first save AlphaUModel data from screen
            exportAlphaUModel = getNewEmptyAlphaUModel();
            saveAlphaUFields(exportAlphaUModel);
        }

        setAlwaysOnTop(false);

        String dialogTitle = "Save this Alpha U Model as xml: *.xml";
        final String fileExtension = ".xml";
        String alphaUModelFileName
                = exportAlphaUModel.getName() + fileExtension;
        FileFilter nonMacFileFilter = new XMLFileFilter();

        selectedFile = FileHelper.AllPlatformSaveAs(
                new Frame(), dialogTitle, null, fileExtension, alphaUModelFileName, nonMacFileFilter);

        if (selectedFile != null) {
            try {
                // export
                exportAlphaUModel.serializeXMLObject(selectedFile.getCanonicalPath());

                if (newEmptyAlphaUModel != null) {
                    // Feb 2008 now per Noah automatically import it as well
                    CancelNewAlphaUModelEdit();
                    ReadAndRegisterAlphaUModel(selectedFile);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        setAlwaysOnTop(true);
        return (selectedFile != null);
    }

    private synchronized void ImportLocalAlphaUModel()
            throws BadLabDataException {
        String dialogTitle = "Select a U-Pb Redux Alpha U Model File to Open: *.xml";
        final String fileExtension = ".xml";
        FileFilter nonMacFileFilter = new XMLFileFilter();

        setAlwaysOnTop(false);

        File returnFile
                = FileHelper.AllPlatformGetFile(dialogTitle, null, fileExtension, nonMacFileFilter, false, new JFrame())[0];

        if (returnFile != null) {
            ReadAndRegisterAlphaUModel(returnFile);
        }

        // wait to make visible so messages appear
        setAlwaysOnTop(true);

    }

    private synchronized void ReadAndRegisterAlphaUModel(File returnFile) {
        ValueModel tempAlphaUModel = new ValueModel();

        try {
            tempAlphaUModel = (ValueModel) tempAlphaUModel.readXMLObject(returnFile.getCanonicalPath(), true);
        } catch (FileNotFoundException ex) {
            tempAlphaUModel = null;
        } catch (IOException ex) {
            tempAlphaUModel = null;
        } catch (ETException ex) {
            tempAlphaUModel = null;
        } catch (BadOrMissingXMLSchemaException ex) {
            tempAlphaUModel = null;
        }

        if (tempAlphaUModel != null) {
            try {
                RegisterAlphaUModel(tempAlphaUModel);
            } catch (BadLabDataException badLabDataException) {
            }
        } else {
            int result
                    = JOptionPane.showConfirmDialog(
                            null,
                            new String[]{"This AlphaUModel could not be imported...please confirm it conforms to the schema."},
                            "ET Redux Warning",
                            JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     *
     * @return
     */
    public String getSavedAlphaPbModelName() {
        return savedAlphaPbModelName;
    }

    /**
     *
     * @param savedAlphaPbModelName
     */
    public void setSavedAlphaPbModelName(String savedAlphaPbModelName) {
        this.savedAlphaPbModelName = savedAlphaPbModelName;
    }

    private void InitializeAlphaPbTabTextBoxes(boolean editable) {

        // set properties of text boxes AlphaPb tab
        alphaPbName_text.setDocument(new UnDoAbleDocument(alphaPbName_text, editable));

        alphaPb_text.setDocument(new BigDecimalDocument(alphaPb_text, editable));
        alphaPboneSigma_text.setDocument(new BigDecimalDocument(alphaPboneSigma_text, editable));

    }

    private void saveAlphaPbFields(ValueModel alphaPb) {

        alphaPb.setName(alphaPbName_text.getText());

        double tempA = Double.valueOf(alphaPb_text.getText());
        double tempQ = tempA / 100.00;
        alphaPb.setValue(new BigDecimal(Double.toString(tempQ)));

        tempA = Double.valueOf(alphaPboneSigma_text.getText());
        tempQ = tempA / 100.00;
        alphaPb.setOneSigma(new BigDecimal(Double.toString(tempQ)));
    }

    private void populateAlphaPbFields(ValueModel alphaPb)
            throws BadLabDataException {

        // set visible flag if default tracerModel showing
        alphaPbName_label.setForeground(
                (alphaPb.getName().equalsIgnoreCase(
                        myLabData.getDefaultLabAlphaPbModel().getName()))
                        ? Color.RED : Color.BLACK);

        alphaPbName_text.setText(alphaPb.getName());

        alphaPb_text.setText(alphaPb.getValue().movePointRight(2).//.multiply(
                //new BigDecimal( "100.00" ) ).
                setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE, RoundingMode.HALF_UP).toPlainString());
        alphaPboneSigma_text.setText(alphaPb.getOneSigma().movePointRight(2).//.multiply(
                //new BigDecimal( "100.00" ) ).
                setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE, RoundingMode.HALF_UP).toPlainString());

    }

    private void InitAlphaPbChooser()
            throws BadLabDataException {

        // set up AlphaPb chooser
        alphaPbChooser.removeAllItems();
        ArrayList<ValueModel> alphaPbModels = myLabData.getAlphaPbModels();
        for (int i = 1; i < alphaPbModels.size(); i++) {
            alphaPbChooser.addItem(((ValueModel) alphaPbModels.get(i)).getName());
        }

        // handle AlphaPb chooser
        AlphaPbItemListener alphaPbActionListener = new AlphaPbItemListener();
        alphaPbChooser.addItemListener(alphaPbActionListener);

        // labData always has at least the first AlphaPb chooser
        alphaPbChooser.setSelectedIndex(-1);
        alphaPbChooser.setSelectedItem(getSavedAlphaPbModelName());

    }

    class AlphaPbItemListener implements ItemListener {
        // This method is called only if a new item has been selected.

        public void itemStateChanged(ItemEvent evt) {
            JComboBox alphaPbChooser = (JComboBox) evt.getSource();

            // Get the affected AlphaPbModel
            ValueModel alphaPbModel = null;
            try {
                alphaPbModel = myLabData.getAnAlphaPbModel((String) evt.getItem());
            } catch (BadLabDataException ex) {
                ex.printStackTrace();
            }

            if (evt.getStateChange() == ItemEvent.SELECTED) {
                try {

                    // Item was just selected
                    populateAlphaPbFields(alphaPbModel);
                } catch (BadLabDataException ex) {
                    Logger.getLogger(LabDataEditorDialog.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else if (evt.getStateChange() == ItemEvent.DESELECTED) {
                // Item is no longer selected
            }
        }
    }

    /**
     *
     * @return
     */
    public ValueModel getNewEmptyAlphaPbModel() {
        return newEmptyAlphaPbModel;
    }

    /**
     *
     * @param newEmptyAlphaPbModel
     */
    public void setNewEmptyAlphaPbModel(ValueModel newEmptyAlphaPbModel) {
        this.newEmptyAlphaPbModel = newEmptyAlphaPbModel;
    }

    private void EditNewEmptyAlphaPbModel()
            throws BadLabDataException {

        setNewEmptyAlphaPbModel(new ValueModel("New Alpha Pb Model", "ABS"));
        alphaPbChooser.setSelectedIndex(-1);
        alphaPbChooser.setEnabled(false);
        InitializeAlphaPbTabTextBoxes(true);
        populateAlphaPbFields(getNewEmptyAlphaPbModel());
    }

    private synchronized boolean CheckIsSavedStatusOfAlphaPbModelEdit()
            throws BadLabDataException {
        boolean retval = true;

        if (getNewEmptyAlphaPbModel() != null) {
            //setVisible(false);
            int response = JOptionPane.showConfirmDialog(this,
                    new String[]{"You have not saved the edited Alpha U Model ... Proceed anyway ?"},
                    "ET Redux Warning",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            retval = (response == JOptionPane.YES_OPTION);
        }
        if (retval) {
            CancelNewAlphaPbModelEdit();
        } else {
            //setVisible(true);
        }

        return retval;
    }

    private synchronized void CancelNewAlphaPbModelEdit()
            throws BadLabDataException {
        setNewEmptyAlphaPbModel(null);
        alphaPbChooser.setEnabled(true);
        InitializeAlphaPbTabTextBoxes(false);
        try {
            InitAlphaPbChooser();
        } catch (BadLabDataException ex) {
            ex.printStackTrace();
        }

    }

    private synchronized void RegisterAlphaPbModel(final ValueModel tempAlphaPbModel)
            throws HeadlessException, BadLabDataException {
        myLabData.registerAlphaPbModel(tempAlphaPbModel, true);
        InitAlphaPbChooser();
        alphaPbChooser.setSelectedItem(tempAlphaPbModel.getName());
    }

    private void RemoveCurrentAlphaPbModel()
            throws BadLabDataException {
        // see also menu item is turned off
        if (!(((String) alphaPbChooser.getSelectedItem()).equalsIgnoreCase("<none>") || ((String) alphaPbChooser.getSelectedItem()).equalsIgnoreCase(ReduxConstants.NONE))) {
            String tempName = (String) alphaPbChooser.getSelectedItem();

            alphaPbChooser.removeAllItems();
            myLabData.removeAnAlphaPbModel(tempName);
        }

        InitAlphaPbChooser();
    }

    private boolean ExportAlphaPbModelAsXML()
            throws BadLabDataException {
        File selectedFile = null;

        // identify AlphaPbModel for export
        ValueModel exportAlphaPbModel = null;
        if (getNewEmptyAlphaPbModel() == null) {
            // we are exporting an existing tracerModel
            exportAlphaPbModel = myLabData.getAnAlphaPbModel((String) alphaPbChooser.getSelectedItem());
        } else {
            // we are exporting the new AlphaPbModel under edit
            // first save AlphaPbModel data from screen
            exportAlphaPbModel = getNewEmptyAlphaPbModel();
            saveAlphaPbFields(exportAlphaPbModel);
        }

        setAlwaysOnTop(false);

        String dialogTitle = "Save this Alpha U Model as xml: *.xml";
        final String fileExtension = ".xml";
        String alphaPbModelFileName
                = exportAlphaPbModel.getName() + fileExtension;
        FileFilter nonMacFileFilter = new XMLFileFilter();

        selectedFile = FileHelper.AllPlatformSaveAs(
                new Frame(), dialogTitle, null, fileExtension, alphaPbModelFileName, nonMacFileFilter);

        if (selectedFile != null) {
            try {
                // export
                exportAlphaPbModel.serializeXMLObject(selectedFile.getCanonicalPath());

                if (newEmptyAlphaPbModel != null) {
                    // Feb 2008 now per Noah automatically import it as well
                    CancelNewAlphaPbModelEdit();
                    ReadAndRegisterAlphaPbModel(selectedFile);
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        setAlwaysOnTop(true);
        return (selectedFile != null);
    }

    private synchronized void ImportLocalAlphaPbModel()
            throws BadLabDataException {

        String dialogTitle = "Select a U-Pb Redux Alpha U Model File to Open: *.xml";
        final String fileExtension = ".xml";
        FileFilter nonMacFileFilter = new XMLFileFilter();

        setAlwaysOnTop(false);

        File returnFile
                = FileHelper.AllPlatformGetFile(dialogTitle, null, fileExtension, nonMacFileFilter, false, new JFrame())[0];

        if (returnFile != null) {
            ReadAndRegisterAlphaPbModel(returnFile);
        }

        // wait to make visible so messages appear
        setAlwaysOnTop(true);

    }

    private synchronized void ReadAndRegisterAlphaPbModel(File returnFile) {
        ValueModel tempAlphaPbModel = new ValueModel();

        try {
            tempAlphaPbModel = (ValueModel) tempAlphaPbModel.readXMLObject(returnFile.getCanonicalPath(), true);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            returnFile = null;
        } catch (IOException ex) {
            ex.printStackTrace();
            returnFile = null;
        } catch (ETException | BadOrMissingXMLSchemaException ex) {
            ex.printStackTrace();
            returnFile = null;
        }

        if (returnFile != null) {
            try {
                RegisterAlphaPbModel(tempAlphaPbModel);
            } catch (BadLabDataException badLabDataException) {
            }
        }
    }

    // PbBlank tab
    /**
     *
     * @return
     */
    public String getSavedPbBlankName() {
        return savedPbBlankModelName;
    }

    /**
     *
     * @param savedPbBlankName
     */
    public void setSavedPbBlankName(String savedPbBlankName) {
        this.savedPbBlankModelName = savedPbBlankName;
    }

    private void populatePbBlankModelFields(AbstractRatiosDataModel pbBlankModel, boolean editable)
            throws BadLabDataException {
        pbBlankModel.initializeModel();

        Dimension pbBlankModelViewDimension = new Dimension(//
                PbBlankICsTab_panel.getWidth(), PbBlankICsTab_panel.getHeight() - VERTICAL_OFFSET_MODEL_VIEW);
        if (editable) {
            pbBlankModelView = //
                    new RatiosDataViewEditable(pbBlankModel, pbBlankModelViewDimension, false);
        } else {
            pbBlankModelView = //
                    new RatiosDataViewNotEditable(pbBlankModel, pbBlankModelViewDimension, false);
        }

        pbBlankModelView.setBackground(PbBlankICsTab_panel.getBackground());
        pbBlankModelView.setBounds(PbBlankICsTab_panel.getBounds());
        pbBlankModelView.setLocation(0, VERTICAL_OFFSET_MODEL_VIEW);
        for (Component c : PbBlankICsTab_panel.getComponents()) {
            if (c instanceof AbstractRatiosDataView) {
                PbBlankICsTab_panel.remove(c);
            }
        }
        PbBlankICsTab_panel.add(pbBlankModelView);
        PbBlankICsTab_panel.validate();
        pbBlankModelView.repaint();
    }

    private void initPbBlankModelChooser()
            throws BadLabDataException {

        // set up pbBlankModel chooser
        PbBlankChooser.removeAllItems();
        ArrayList<AbstractRatiosDataModel> blanks = myLabData.getPbBlanks();
        for (int i = (blanks.size() > 1 ? 1 : 0); i < blanks.size(); i++) {
            PbBlankChooser.addItem(blanks.get(i).getReduxLabDataElementName());
        }

        PbBlankChooser.setSelectedIndex(0);
        PbBlankChooser.setSelectedItem(savedPbBlankModelName);

        // set up Tracer chooser
        tracerModelChooser.removeAllItems();
        ArrayList<AbstractRatiosDataModel> tracers = myLabData.getTracers();
        for (int i = (tracers.size() > 1 ? 1 : 0); i < tracers.size(); i++) {
            tracerModelChooser.addItem(tracers.get(i).getReduxLabDataElementName());
        }

        tracerModelChooser.setSelectedIndex(0);
        tracerModelChooser.setSelectedItem(savedTracerModelName);

    }

    class PbBlankItemListener implements ItemListener {
        // This method is called only if a new item has been selected.

        public void itemStateChanged(ItemEvent evt) {
            AbstractRatiosDataModel pbBlankModel = null;

            try {
                pbBlankModel = myLabData.getAPbBlankModel((String) evt.getItem());
            } catch (BadLabDataException ex) {
            }

            if (evt.getStateChange() == ItemEvent.SELECTED) {
                try {

                    // Item was just selected
                    populatePbBlankModelFields(pbBlankModel, false);
                } catch (BadLabDataException ex) {
                    Logger.getLogger(LabDataEditorDialog.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else if (evt.getStateChange() == ItemEvent.DESELECTED) {
                // Item is no longer selected
            }
        }
    }

    /**
     *
     * @return
     */
    public AbstractRatiosDataModel getNewEmptyPbBlank() {
        return newEmptyPbBlankModel;
    }

    /**
     *
     * @param newEmptyBlank
     */
    public void setNewEmptyPbBlank(AbstractRatiosDataModel newEmptyBlank) {
        this.newEmptyPbBlankModel = newEmptyBlank;
        if (PbBlankChooser.getSelectedIndex() >= 0) {
            savedPbBlankModelName = (String) PbBlankChooser.getSelectedItem();
        } else {
            try {
                savedPbBlankModelName = myLabData.getDefaultLabPbBlank().getReduxLabDataElementName();
            } catch (BadLabDataException ex) {
            }
        }
    }

    private void editNewEmptyPbBlankModel()
            throws BadLabDataException {
        newEmptyPbBlankModel = PbBlankICModel.createNewInstance();
        PbBlankChooser.setSelectedIndex(-1);
        PbBlankChooser.setEnabled(false);

        populatePbBlankModelFields(newEmptyPbBlankModel, true);
    }

    private void editCopyOfCurrentPbBlankModel()
            throws BadLabDataException {

        try {
            currentEditablePbBlankModel = myLabData.getAPbBlankModel(
                    (String) PbBlankChooser.getSelectedItem()).copyModel();

            PbBlankChooser.setSelectedIndex(-1);
            PbBlankChooser.setEnabled(false);

            newEmptyPbBlankModel = currentEditablePbBlankModel;
            populatePbBlankModelFields(currentEditablePbBlankModel, true);

        } catch (BadLabDataException ex) {
        }

    }

    private synchronized boolean checkIsSavedStatusOfPbBlankModelEdit()
            throws BadLabDataException {
        boolean retval = true;

        if (newEmptyPbBlankModel != null) {

            int response = JOptionPane.showConfirmDialog(this,
                    new String[]{"You have not saved the new Pb Blank Model ... Proceed anyway?"},
                    "ET Redux Warning",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            retval = (response == JOptionPane.YES_OPTION);
        }
        if (retval) {
            cancelNewPbBlankEdit();
        }

        return retval;
    }

    private void cancelNewPbBlankEdit()
            throws BadLabDataException {
        newEmptyPbBlankModel = null;
        PbBlankChooser.setEnabled(true);

        try {
            initPbBlankModelChooser();
        } catch (BadLabDataException ex) {
        }
    }

    private synchronized void registerPbBlankModel(final AbstractRatiosDataModel pbBlankModel)
            throws HeadlessException, BadLabDataException {
        myLabData.registerPbBlank(pbBlankModel, true);
        initPbBlankModelChooser();
        PbBlankChooser.setSelectedItem(pbBlankModel.getReduxLabDataElementName());
    }

    private void RemoveCurrentPbBlank()
            throws BadLabDataException {
        // see also menu item is truned off
        if (!(((String) PbBlankChooser.getSelectedItem()).equalsIgnoreCase("<none>") || ((String) PbBlankChooser.getSelectedItem()).equalsIgnoreCase(ReduxConstants.NONE))) {// <none> is not nexessarily first!!getSelectedIndex() > 0){
            String tempName = (String) PbBlankChooser.getSelectedItem();
            PbBlankChooser.removeAllItems();
            myLabData.removeABlank(tempName);
        }

        initPbBlankModelChooser();
    }

    private boolean exportPbBlankModelAsXML()
            throws BadLabDataException, ETException {
        File selectedFile = null;

        // identify PbBlankModel for export
        if (newEmptyPbBlankModel == null) {
            // we are exporting an existing PbBlankModel
            newEmptyPbBlankModel = myLabData.getAPbBlankModel((String) PbBlankChooser.getSelectedItem());
        } else {
            pbBlankModelView.saveAndUpdateModelView(true);
        }

        setAlwaysOnTop(false);

        String dialogTitle = "Save this Pb Blank as xml: *.xml";
        final String fileExtension = ".xml";
        String pbBlankFileName
                = newEmptyPbBlankModel.getReduxLabDataElementName() + fileExtension;
        FileFilter nonMacFileFilter = new XMLFileFilter();

        selectedFile = FileHelper.AllPlatformSaveAs(
                new Frame(), dialogTitle, null, fileExtension, pbBlankFileName, nonMacFileFilter);

        if (selectedFile != null) {
            try {
                // export
                newEmptyPbBlankModel.serializeXMLObject(selectedFile.getCanonicalPath());

                if (newEmptyPbBlankModel != null) {
                    // Feb 2008 now per Noah automatically import it as well
                    cancelNewPbBlankEdit();
                    readAndRegisterPbBlankModel(selectedFile);
                }

            } catch (IOException ex) {
            }
        }

        setAlwaysOnTop(true);
        return (selectedFile != null);

    }

    private synchronized void importLocalPbBlankModel()
            throws BadLabDataException {

        String dialogTitle = "Select a U-Pb Redux Pb Blank Model File to Open: *.xml";
        final String fileExtension = ".xml";
        FileFilter nonMacFileFilter = new XMLFileFilter();

        setAlwaysOnTop(false);

        File returnFile
                = FileHelper.AllPlatformGetFile(dialogTitle, null, fileExtension, nonMacFileFilter, false, new JFrame())[0];

        if (returnFile != null) {
            readAndRegisterPbBlankModel(returnFile);
        }

        // wait to make visible so messages appear
        setAlwaysOnTop(true);
    }

    private synchronized void readAndRegisterPbBlankModel(File returnFile) {
        AbstractRatiosDataModel tempBlank = PbBlankICModel.createNewInstance();

        try {
            tempBlank = (AbstractRatiosDataModel) tempBlank.readXMLObject(returnFile.getCanonicalPath(), true);
        } catch (FileNotFoundException ex) {
            returnFile = null;
        } catch (IOException ex) {
            returnFile = null;
        } catch (ETException ex) {
            returnFile = null;
        } catch (BadOrMissingXMLSchemaException ex) {
            returnFile = null;
        }

        if (tempBlank != null) {
            try {
                registerPbBlankModel(tempBlank);
            } catch (BadLabDataException badLabDataException) {
            }
        } else {
            JOptionPane.showConfirmDialog(
                    null,
                    new String[]{"This PbBlank Model could not be imported...please confirm it conforms to the schema."},
                    "ET Redux Warning",
                    JOptionPane.WARNING_MESSAGE);
        }

    }

    // Initial Pb Models tab ***************************************************
    /**
     *
     * @return
     */
    public String getSavedInitialPbModelName() {
        return savedInitialPbModelName;
    }

    /**
     *
     * @param savedInitialPbModelName
     */
    public void setSavedInitialPbModelName(String savedInitialPbModelName) {
        this.savedInitialPbModelName = savedInitialPbModelName;
    }

    private void populateInitialPbModelFields(AbstractRatiosDataModel initialPbModel, boolean editable)
            throws BadLabDataException {

        // setup and perform calculations for models that calculate
        staceyKramerCalculator_Panel.setVisible(initialPbModel instanceof StaceyKramersInitialPbModelET);

        BigDecimal estimatedAge
                = new BigDecimal(estimatedAgeInMA_text.getText(), ReduxConstants.mathContext15);
//        AbstractRatiosDataModel physicalConstantsModel =
//                myLabData.getAPhysicalConstantsModel( (String) physicalConstantsModelForInitialPbModel_Chooser.getSelectedItem() );
        // april 2014 changed to force to ET model that contains original SK values
        AbstractRatiosDataModel physicalConstantsModel
                = PhysicalConstantsModel.getEARTHTIMEPhysicalConstantsModel();

        if (initialPbModel instanceof StaceyKramersInitialPbModelET) {
            BigDecimal lambda238 = physicalConstantsModel.getDatumByName(Lambdas.lambda238.getName()).getValue();
            BigDecimal lambda235 = physicalConstantsModel.getDatumByName(Lambdas.lambda235.getName()).getValue();
            BigDecimal lambda232 = physicalConstantsModel.getDatumByName(Lambdas.lambda232.getName()).getValue();
            ((StaceyKramersInitialPbModelET) initialPbModel).calculateRatios(
                    estimatedAge,
                    lambda238, lambda235, lambda232);

            if (Math.abs(Double.valueOf(correlationCoefficients_text.getText())) > 1.0) {
                correlationCoefficients_text.setText(//
                        String.valueOf(Math.signum(Double.valueOf(correlationCoefficients_text.getText()))));
            }

            ((StaceyKramersInitialPbModelET) initialPbModel).calculateUncertaintiesAndRhos(new BigDecimal(relativeUncertainty_text.getText(), ReduxConstants.mathContext15),
                    new BigDecimal(correlationCoefficients_text.getText(), ReduxConstants.mathContext15), BigDecimal.ZERO, BigDecimal.ZERO);

            currentEditableInitialPbModel = initialPbModel;
        }

        initialPbModel.initializeModel();

        if (editable) {
            initialPbModelView = //
                    new RatiosDataViewEditable(initialPbModel, initialPbModelsTab_panel.getSize(), false);
        } else {
            initialPbModelView = //
                    new RatiosDataViewNotEditable(initialPbModel, initialPbModelsTab_panel.getSize(), false);
        }

        initialPbModelView.setBackground(initialPbModelsTab_panel.getBackground());
        initialPbModelView.setLocation(0, VERTICAL_OFFSET_MODEL_VIEW);
        initialPbModelView.setSize(initialPbModelsTab_panel.getWidth(), 450);
        for (Component c : initialPbModelsTab_panel.getComponents()) {
            if (c instanceof AbstractRatiosDataView) {
                initialPbModelsTab_panel.remove(c);
            }
        }
        initialPbModelsTab_panel.add(initialPbModelView);
        initialPbModelsTab_panel.validate();
        initialPbModelView.repaint();
    }

    private void initInitialPbModelChooser() throws BadLabDataException {

        // FIRST populate PhysicalConstantsModel chooser for testing initial pb physicalConstantsModel calculations
        physicalConstantsModelForInitialPbModel_Chooser.removeAllItems();
        ArrayList<AbstractRatiosDataModel> models = myLabData.getPhysicalConstantsModels();
        for (Iterator it = models.iterator(); it.hasNext();) {
            physicalConstantsModelForInitialPbModel_Chooser.addItem(((ReduxLabDataListElementI) it.next()).getReduxLabDataElementName());
        }
        physicalConstantsModelForInitialPbModel_Chooser.setSelectedItem(myLabData.getDefaultPhysicalConstantsModel().getNameAndVersion());

        // set up InitialPbModel chooser
        InitialPbModelChooser.removeAllItems();
        ArrayList<AbstractRatiosDataModel> initialPbModels = myLabData.getInitialPbModels();
        for (int i = (initialPbModels.size() > 1 ? 1 : 0); i < initialPbModels.size(); i++) {
            // hide specialty models
            if (!(initialPbModels.get(i).getReduxLabDataElementName().startsWith("Placeholder"))//
                    && //
                    !(initialPbModels.get(i).getReduxLabDataElementName().startsWith("Custom"))) {
                InitialPbModelChooser.addItem(initialPbModels.get(i).getReduxLabDataElementName());
            }
        }

        InitialPbModelChooser.setSelectedIndex(0);
        InitialPbModelChooser.setSelectedItem(savedInitialPbModelName);

    }

    class InitialPbModelItemListener implements ItemListener {
        // This method is called only if a new item has been selected.

        public void itemStateChanged(ItemEvent evt) {

            // Get the chosen Initial Pb Model
            AbstractRatiosDataModel initialPbModel = null;
            try {
                initialPbModel = myLabData.getAnInitialPbModel((String) evt.getItem());
            } catch (BadLabDataException ex) {
            }

            if (evt.getStateChange() == ItemEvent.SELECTED) {
                try {
                    // Item was just selected
                    populateInitialPbModelFields(initialPbModel, false);
                } catch (BadLabDataException ex) {
                    Logger.getLogger(LabDataEditorDialog.class.getName()).log(Level.SEVERE, null, ex);
                }

            } else if (evt.getStateChange() == ItemEvent.DESELECTED) {
                // Item is no longer selected
            }
        }
    }

    /**
     *
     * @return
     */
    public AbstractRatiosDataModel getNewEmptyInitialPbModel() {
        return newEmptyInitialPbModel;
    }

    /**
     *
     * @param newEmptyInitialPbModel
     */
    public void setNewEmptyInitialPbModel(AbstractRatiosDataModel newEmptyInitialPbModel) {
        this.newEmptyInitialPbModel = newEmptyInitialPbModel;
        if (InitialPbModelChooser.getSelectedIndex() >= 0) {
            savedInitialPbModelName = (String) InitialPbModelChooser.getSelectedItem();
        } else {
            try {
                savedInitialPbModelName = myLabData.getDefaultLabInitialPbModel().getReduxLabDataElementName();
            } catch (BadLabDataException ex) {
            }
        }
    }

    private void editNewEmptyInitialPbModel()
            throws BadLabDataException {
        newEmptyInitialPbModel = InitialPbModelET.createNewInstance();
        InitialPbModelChooser.setSelectedIndex(-1);
        InitialPbModelChooser.setEnabled(false);
        populateInitialPbModelFields(newEmptyInitialPbModel, true);
    }

    private void editCopyOfCurrentInitialPbModel()
            throws BadLabDataException {

        try {
            currentEditableInitialPbModel = myLabData.getAnInitialPbModel((String) InitialPbModelChooser.getSelectedItem()).copyModel();

            InitialPbModelChooser.setSelectedIndex(-1);
            InitialPbModelChooser.setEnabled(false);

            newEmptyInitialPbModel = currentEditableInitialPbModel;
            populateInitialPbModelFields(currentEditableInitialPbModel, true);

        } catch (BadLabDataException ex) {
        }
    }

    private synchronized boolean checkIsSavedStatusOfInitialPbModelEdit()
            throws BadLabDataException {
        boolean retval = true;

        if (newEmptyInitialPbModel != null) {
            int response = JOptionPane.showConfirmDialog(this,
                    new String[]{"You have not saved the new Initial Pb Model ... Proceed anyway?"},
                    "ET Redux Warning",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            retval = (response == JOptionPane.YES_OPTION);
        }
        if (retval) {
            cancelNewInitialPbModelEdit();
        }

        return retval;
    }

    private void cancelNewInitialPbModelEdit()
            throws BadLabDataException {
        newEmptyInitialPbModel = null;
        InitialPbModelChooser.setEnabled(true);
        try {
            initInitialPbModelChooser();
        } catch (BadLabDataException ex) {
        }
    }

    private synchronized void registerInitialPbModel(final AbstractRatiosDataModel tempInitialPbModel)
            throws HeadlessException, BadLabDataException {
        myLabData.registerInitialPbModel(tempInitialPbModel, true);
        initInitialPbModelChooser();
        InitialPbModelChooser.setSelectedItem(tempInitialPbModel.getReduxLabDataElementName());
    }

    private void removeCurrentInitialPbModel() throws BadLabDataException {
        // see also menu item is turned off

        String intitialPbModelName = (String) InitialPbModelChooser.getSelectedItem();
        myLabData.removeAnInitialPbModel(intitialPbModelName);
        initInitialPbModelChooser();
    }

    private boolean exportInitialPbModelAsXML()
            throws BadLabDataException, ETException {
        File selectedFile;

        // identify InitialPbModel for export
        if (newEmptyInitialPbModel == null) {
            // we are exporting an existing InitialPbModel
            newEmptyInitialPbModel
                    = myLabData.getAnInitialPbModel((String) InitialPbModelChooser.getSelectedItem());
        } else {
            // we are exporting the new InitialPbModel under edit
            // first save InitialPbModel data from screen
            initialPbModelView.saveAndUpdateModelView(true);

            // convert StaceyKramers to regular
            if (newEmptyInitialPbModel instanceof StaceyKramersInitialPbModelET) {
                AbstractRatiosDataModel convertedInitialPbModel = //
                        InitialPbModelET.createInstance(//
                                newEmptyInitialPbModel.getModelName(),//
                                newEmptyInitialPbModel.getVersionNumber(),//
                                newEmptyInitialPbModel.getMinorVersionNumber(),//
                                newEmptyInitialPbModel.getLabName(),//
                                newEmptyInitialPbModel.getDateCertified(),//
                                newEmptyInitialPbModel.getReference(),//
                                newEmptyInitialPbModel.getComment(),//
                                newEmptyInitialPbModel.getData(), newEmptyInitialPbModel.getRhosVarUnct());

                newEmptyInitialPbModel = convertedInitialPbModel;
            }
        }

        setAlwaysOnTop(false);

        String dialogTitle = "Save this InitialPbModel as xml: *.xml";
        final String fileExtension = ".xml";
        String initialPbModelFileName
                = newEmptyInitialPbModel.getReduxLabDataElementName() + fileExtension;
        FileFilter nonMacFileFilter = new XMLFileFilter();

        selectedFile = FileHelper.AllPlatformSaveAs(
                new Frame(), dialogTitle, null, fileExtension, initialPbModelFileName, nonMacFileFilter);

        if (selectedFile != null) {
            try {
                // export
                newEmptyInitialPbModel.serializeXMLObject(selectedFile.getCanonicalPath());

                if (newEmptyInitialPbModel != null) {
                    // Feb 2008 now per Noah automatically import it as well
                    cancelNewInitialPbModelEdit();
                    readAndRegisterInitialPbModel(selectedFile);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        setAlwaysOnTop(true);
        return (selectedFile != null);
    }

    private synchronized void importLocalInitialPbModel()
            throws BadLabDataException {

        String dialogTitle = "Select a U-Pb Redux InitialPb Model File to Open: *.xml";
        final String fileExtension = ".xml";
        FileFilter nonMacFileFilter = new XMLFileFilter();

        setAlwaysOnTop(false);

        File returnFile
                = FileHelper.AllPlatformGetFile(dialogTitle, null, fileExtension, nonMacFileFilter, false, new JFrame())[0];

        if (returnFile != null) {
            readAndRegisterInitialPbModel(returnFile);
        }

        // wait to make visible so messages appear
        setAlwaysOnTop(true);
    }

    private synchronized void readAndRegisterInitialPbModel(File returnFile) {
        AbstractRatiosDataModel initialPbModel = InitialPbModelET.getNoneInstance();

        try {
            initialPbModel = (AbstractRatiosDataModel) initialPbModel.readXMLObject(returnFile.getCanonicalPath(), true);
        } catch (FileNotFoundException ex) {
            initialPbModel = null;
        } catch (IOException ex) {
            initialPbModel = null;
        } catch (ETException ex) {
            initialPbModel = null;
        } catch (BadOrMissingXMLSchemaException ex) {
            initialPbModel = null;
        }

        if (initialPbModel != null) {
            try {
                registerInitialPbModel(initialPbModel);
            } catch (BadLabDataException badLabDataException) {
            }
        } else {
            JOptionPane.showConfirmDialog(
                    null,
                    new String[]{"This Initial Pb Model could not be imported...please confirm it conforms to the schema."},
                    "ET Redux Warning",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    // PhysicalConstantsModel tab
    /**
     *
     * @return
     */
    public String getSavedPhysicalConstantsModelName() {
        return savedPhysicalConstantsModelName;
    }

    /**
     *
     * @param savedPhysicalConstantsModelName
     */
    public void setSavedPhysicalConstantsModelName(String savedPhysicalConstantsModelName) {
        this.savedPhysicalConstantsModelName = savedPhysicalConstantsModelName;
    }

    private void populatePhysicalConstantsFields(AbstractRatiosDataModel physicalConstantsModel, boolean editable)
            throws BadLabDataException {
        physicalConstantsModel.initializeModel();

        Dimension physicalConstantsModelViewDimension = new Dimension(//
                physicalConstantsModels_panel.getWidth(), physicalConstantsModels_panel.getHeight() - VERTICAL_OFFSET_MODEL_VIEW);
        if (editable) {
            physicalConstantsModelView = //
                    new PhysicalConstantsDataViewEditable(physicalConstantsModel, physicalConstantsModelViewDimension, false);
        } else {
            physicalConstantsModelView = //
                    new PhysicalConstantsDataViewNotEditable(physicalConstantsModel, physicalConstantsModelViewDimension, false);
        }

        physicalConstantsModelView.setBackground(physicalConstantsModels_panel.getBackground());
        physicalConstantsModelView.setBounds(physicalConstantsModels_panel.getBounds());
        physicalConstantsModelView.setLocation(0, VERTICAL_OFFSET_MODEL_VIEW);
        for (Component c : tracersTab_panel.getComponents()) {
            if (c instanceof AbstractRatiosDataView) {
                physicalConstantsModels_panel.remove(c);
            }
        }
        physicalConstantsModels_panel.add(physicalConstantsModelView);
        physicalConstantsModels_panel.validate();
        physicalConstantsModelView.repaint();
    }

    private void initPhysicalConstantsModelChooser()
            throws BadLabDataException {

        // set up PhysicalConstantsModel chooser
        physicalConstantsModel_Chooser.removeAllItems();
        ArrayList<AbstractRatiosDataModel> physicalConstants = myLabData.getPhysicalConstantsModels();
        for (int i = (physicalConstants.size() > 1 ? 1 : 0); i < physicalConstants.size(); i++) {
            physicalConstantsModel_Chooser.addItem(((AbstractRatiosDataModel) physicalConstants.get(i)).getReduxLabDataElementName());
        }

        physicalConstantsModel_Chooser.setSelectedIndex(0);
        physicalConstantsModel_Chooser.setSelectedItem(savedPhysicalConstantsModelName);

    }

    class PhysicalConstantsItemListener implements ItemListener {
        // This method is called only if a new item has been selected.

        public void itemStateChanged(ItemEvent evt) {
            // Get the affected physicalConstantsModel
            AbstractRatiosDataModel physicalConstantsModel = null;
            try {
                physicalConstantsModel = myLabData.getAPhysicalConstantsModel((String) evt.getItem());
            } catch (BadLabDataException ex) {
            }

            if (evt.getStateChange() == ItemEvent.SELECTED) {
                try {
                    // Item was just selected
                    populatePhysicalConstantsFields(physicalConstantsModel, false);

                } catch (BadLabDataException ex) {
                    Logger.getLogger(LabDataEditorDialog.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (evt.getStateChange() == ItemEvent.DESELECTED) {
                // Item is no longer selected
            }
        }
    }

    /**
     *
     * @return
     */
    public AbstractRatiosDataModel getNewEmptyPhysicalConstantsModel() {
        return newEmptyPhysicalConstantsModel;
    }

    /**
     *
     * @param newEmptyPhysicalConstantsModel
     */
    public void setNewEmptyPhysicalConstantsModel(AbstractRatiosDataModel newEmptyPhysicalConstantsModel) {
        this.newEmptyPhysicalConstantsModel = newEmptyPhysicalConstantsModel;
        if (physicalConstantsModel_Chooser.getSelectedIndex() >= 0) {
            savedPhysicalConstantsModelName = (String) physicalConstantsModel_Chooser.getSelectedItem();
        } else {
            try {
                savedPhysicalConstantsModelName = myLabData.getDefaultPhysicalConstantsModel().getReduxLabDataElementName();
            } catch (BadLabDataException ex) {
            }
        }

    }

    private void editNewEmptyPhysicalConstantsModel()
            throws BadLabDataException {
        newEmptyPhysicalConstantsModel = PhysicalConstantsModel.createNewInstance();
        physicalConstantsModel_Chooser.setSelectedIndex(-1);
        physicalConstantsModel_Chooser.setEnabled(false);
        populatePhysicalConstantsFields(newEmptyPhysicalConstantsModel, true);

    }

    private void editCopyOfCurrentPhysicalConstantsModel()
            throws BadLabDataException {

        try {
            currentEditablePhysicalConstantsModel = myLabData.getAPhysicalConstantsModel(
                    (String) physicalConstantsModel_Chooser.getSelectedItem()).copyModel();

            physicalConstantsModel_Chooser.setSelectedIndex(-1);
            physicalConstantsModel_Chooser.setEnabled(false);

            newEmptyPhysicalConstantsModel = currentEditablePhysicalConstantsModel;
            populatePhysicalConstantsFields(currentEditablePhysicalConstantsModel, true);

        } catch (BadLabDataException ex) {
        }

    }

    private synchronized boolean checkIsSavedStatusOfPhysicalConstantsModelEdit()
            throws BadLabDataException {
        boolean retval = true;

        if (newEmptyPhysicalConstantsModel != null) {

            int response = JOptionPane.showConfirmDialog(this,
                    new String[]{"You have not saved the edited Physical Constants Model ... Proceed anyway ?"},
                    "ET Redux Warning",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            retval = (response == JOptionPane.YES_OPTION);
        }
        if (retval) {
            cancelNewPhysicalConstantsModelEdit();
        } else {
        }

        return retval;
    }

    private synchronized void cancelNewPhysicalConstantsModelEdit()
            throws BadLabDataException {
        newEmptyPhysicalConstantsModel = null;
        physicalConstantsModel_Chooser.setEnabled(true);
        try {
            initPhysicalConstantsModelChooser();
        } catch (BadLabDataException ex) {
        }

    }

    private synchronized void registerPhysicalConstantsModel(final AbstractRatiosDataModel tempModel)
            throws HeadlessException, BadLabDataException {

        myLabData.registerPhysicalConstantsModel(tempModel, true);
        initPhysicalConstantsModelChooser();
        physicalConstantsModel_Chooser.setSelectedItem(tempModel.getReduxLabDataElementName());
    }

    private void removeCurrentPhysicalConstantsModel()
            throws BadLabDataException {
        // see also menu item is turned off

        String PhysicalConstantsModelName = (String) physicalConstantsModel_Chooser.getSelectedItem();
        physicalConstantsModel_Chooser.removeAllItems();
        myLabData.removeAPhysicalConstantsModel(PhysicalConstantsModelName);

        initPhysicalConstantsModelChooser();
    }

    private boolean exportPhysicalConstantsModelAsXML()
            throws BadLabDataException, ETException {
        File selectedFile;

        if (newEmptyPhysicalConstantsModel == null) {
            // we are exporting an existing PhysicalConstantsModel
            newEmptyPhysicalConstantsModel
                    = myLabData.getAPhysicalConstantsModel((String) physicalConstantsModel_Chooser.getSelectedItem());
        } else {
            physicalConstantsModelView.saveAndUpdateModelView(true);
        }

        setAlwaysOnTop(false);

        String dialogTitle = "Save this Physical Constants Model as xml: *.xml";
        final String fileExtension = ".xml";
        String fractionFileName
                = newEmptyPhysicalConstantsModel.getReduxLabDataElementName() + fileExtension;
        FileFilter nonMacFileFilter = new XMLFileFilter();

        selectedFile = FileHelper.AllPlatformSaveAs(
                new Frame(), dialogTitle, null, fileExtension, fractionFileName, nonMacFileFilter);

        if (selectedFile != null) {
            try {
                // export
                newEmptyPhysicalConstantsModel.serializeXMLObject(selectedFile.getCanonicalPath());

                if (newEmptyPhysicalConstantsModel != null) {
                    // Feb 2008 now per Noah automatically import it as well
                    cancelNewPhysicalConstantsModelEdit();
                    readAndRegisterPhysicalConstantsModel(selectedFile);
                }
            } catch (IOException ex) {
            }
        }

        setAlwaysOnTop(true);
        return (selectedFile != null);
    }

    private synchronized void importLocalPhysicalConstantsModel()
            throws BadLabDataException {

        String dialogTitle = "Select a Physical Constants Model File to Open: *.xml";
        final String fileExtension = ".xml";
        FileFilter nonMacFileFilter = new XMLFileFilter();

        setAlwaysOnTop(false);

        File returnFile
                = FileHelper.AllPlatformGetFile(dialogTitle, null, fileExtension, nonMacFileFilter, false, new JFrame())[0];

        if (returnFile != null) {
            readAndRegisterPhysicalConstantsModel(returnFile);
        }

        // wait to make visible so messages appear
        setAlwaysOnTop(true);

    }

    private synchronized void readAndRegisterPhysicalConstantsModel(File returnFile) {
        AbstractRatiosDataModel tempModel = PhysicalConstantsModel.createNewInstance();

        try {
            tempModel = (AbstractRatiosDataModel) tempModel.readXMLObject(returnFile.getCanonicalPath(), true);
        } catch (FileNotFoundException ex) {
            returnFile = null;
        } catch (IOException ex) {
            returnFile = null;
        } catch (ETException ex) {
            returnFile = null;
        } catch (BadOrMissingXMLSchemaException ex) {
            returnFile = null;
        }

        if (returnFile != null) {
            try {
                registerPhysicalConstantsModel(tempModel);
            } catch (BadLabDataException badLabDataException) {
            }
        }
    }

    // Mineral Standard Models tab *********************************************
    /**
     *
     * @return
     */
    public String getSavedMineralStandardModelName() {
        return savedMineralStandardModelName;
    }

    /**
     *
     * @param savedMineralStandardModelName
     */
    public void setSavedMineralStandardModelName(String savedMineralStandardModelName) {
        this.savedMineralStandardModelName = savedMineralStandardModelName;
    }

    private void populateMineralStandardModelFields(AbstractRatiosDataModel mineralStandardModel, boolean editable)
            throws BadLabDataException {

        mineralStandardModel.initializeModel();

        if (editable) {
            mineralStandardModelView = //
                    new MineralStandardUPbRatiosDataViewEditable(mineralStandardModel, mineralStandard_panel.getSize(), false);
        } else {
            mineralStandardModelView = //
                    new MineralStandardUPbRatiosDataViewNotEditable(mineralStandardModel, mineralStandard_panel.getSize(), false);
        }

        mineralStandardModelView.setBounds(mineralStandard_panel.getBounds());
        mineralStandardModelView.setLocation(0, VERTICAL_OFFSET_MODEL_VIEW);
        for (Component c : mineralStandard_panel.getComponents()) {
            if (c instanceof AbstractRatiosDataView) {
                mineralStandard_panel.remove(c);
            }
        }
        mineralStandard_panel.add(mineralStandardModelView);
        mineralStandard_panel.validate();
        mineralStandardModelView.repaint();

    }

    private void initMineralStandardModelChooser() throws BadLabDataException {

        // set up MineralStandardModelChooser
        MineralStandardModelChooser.removeAllItems();
        ArrayList<AbstractRatiosDataModel> mineralStandardModels = myLabData.getMineralStandardModels();
        for (int i = (mineralStandardModels.size() > 1 ? 1 : 0); i < mineralStandardModels.size(); i++) {
            MineralStandardModelChooser.addItem(mineralStandardModels.get(i).getReduxLabDataElementName());
        }

        MineralStandardModelChooser.setSelectedIndex(0);
        MineralStandardModelChooser.setSelectedItem(savedMineralStandardModelName);
    }

    class MineralStandardModelItemListener implements ItemListener {

        @Override
        public void itemStateChanged(ItemEvent evt) {
            // Get the affected MineralStandardModel
            try {
                AbstractRatiosDataModel model = myLabData.getAMineralStandardModel((String) evt.getItem());

                if (evt.getStateChange() == ItemEvent.SELECTED) {
                    try {
                        // Item was just selected
                        populateMineralStandardModelFields(model, false);
                    } catch (BadLabDataException ex) {
                        Logger.getLogger(LabDataEditorDialog.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } else if (evt.getStateChange() == ItemEvent.DESELECTED) {
                    // Item is no longer selected
                }
            } catch (BadLabDataException ex) {
            }

        }
    }

    /**
     *
     * @return
     */
    public AbstractRatiosDataModel getNewEmptyMineralStandardModel() {
        return newEmptyMineralStandardModel;
    }

    /**
     *
     * @param newEmptyMineralStandardModel
     */
    public void setNewEmptyMineralStandardModel(AbstractRatiosDataModel newEmptyMineralStandardModel) {
        this.newEmptyMineralStandardModel = newEmptyMineralStandardModel;
        if (MineralStandardModelChooser.getSelectedIndex() >= 0) {
            savedMineralStandardModelName = (String) MineralStandardModelChooser.getSelectedItem();
        } else {
            try {
                savedMineralStandardModelName = myLabData.getDefaultTIMSMineralStandardModel().getReduxLabDataElementName();
            } catch (BadLabDataException ex) {
            }
        }
    }

    private void editNewEmptyMineralStandardModel()
            throws BadLabDataException {
        newEmptyMineralStandardModel = MineralStandardUPbModel.createNewInstance();
        MineralStandardModelChooser.setSelectedIndex(-1);
        MineralStandardModelChooser.setEnabled(false);
        populateMineralStandardModelFields(newEmptyMineralStandardModel, true);
    }

    private void editCopyOfCurrentMineralStandardModel()
            throws BadLabDataException {

        try {
            currentEditableMineralStandardModel = myLabData.getAMineralStandardModel(
                    (String) MineralStandardModelChooser.getSelectedItem()).copyModel();

            MineralStandardModelChooser.setSelectedIndex(-1);
            MineralStandardModelChooser.setEnabled(false);

            newEmptyMineralStandardModel = currentEditableMineralStandardModel;
            populateMineralStandardModelFields(currentEditableMineralStandardModel, true);

        } catch (BadLabDataException ex) {
        }

    }

    private synchronized boolean checkIsSavedStatusOfMineralStandardModelEdit()
            throws BadLabDataException {
        boolean retval = true;

        if (newEmptyMineralStandardModel != null) {

            int response = JOptionPane.showConfirmDialog(this,
                    new String[]{"You have not saved the new Mineral Standard Model ... Proceed anyway?"},
                    "ET Redux Warning",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            retval = (response == JOptionPane.YES_OPTION);
        }
        if (retval) {
            cancelNewMineralStandardModelEdit();
        }

        return retval;
    }

    private void cancelNewMineralStandardModelEdit()
            throws BadLabDataException {
        newEmptyMineralStandardModel = null;
        MineralStandardModelChooser.setEnabled(true);
        try {
            initMineralStandardModelChooser();
        } catch (BadLabDataException ex) {
        }
    }

    private synchronized void registerMineralStandardModel(final AbstractRatiosDataModel tempModel)
            throws HeadlessException, BadLabDataException {
        myLabData.registerMineralStandardModel(tempModel, true);
        initMineralStandardModelChooser();
        MineralStandardModelChooser.setSelectedItem(tempModel.getReduxLabDataElementName());
    }

    private void removeCurrentMineralStandardModel() throws BadLabDataException {
        // see also menu item is turned off

        String mineralStandardModelName = (String) MineralStandardModelChooser.getSelectedItem();
        MineralStandardModelChooser.removeAllItems();
        myLabData.removeAMineralStandardModel(mineralStandardModelName);

        initMineralStandardModelChooser();

    }

    private boolean exportMineralStandardModelAsXML()
            throws BadLabDataException, ETException {
        File selectedFile;

        // identify MineralStandardModel for export
        if (newEmptyMineralStandardModel == null) {
            // we are exporting an existing physicalConstantsModel
            newEmptyMineralStandardModel
                    = myLabData.getAMineralStandardModel(
                            (String) MineralStandardModelChooser.getSelectedItem());
        } else {
            mineralStandardModelView.saveAndUpdateModelView(true);
        }

        setAlwaysOnTop(false);

        String dialogTitle = "Save this MineralStandardModel as xml: *.xml";
        final String fileExtension = ".xml";
        String fileName
                = newEmptyMineralStandardModel.getReduxLabDataElementName() + fileExtension;
        FileFilter nonMacFileFilter = new XMLFileFilter();

        selectedFile = FileHelper.AllPlatformSaveAs(
                new Frame(), dialogTitle, null, fileExtension, fileName, nonMacFileFilter);

        if (selectedFile != null) {
            try {
                // export
                newEmptyMineralStandardModel.serializeXMLObject(selectedFile.getCanonicalPath());

                if (newEmptyMineralStandardModel != null) {
                    // Feb 2008 now per Noah automatically import it as well
                    cancelNewMineralStandardModelEdit();
                    readAndRegisterMineralStandardModel(selectedFile);
                }
            } catch (IOException ex) {
            }
        }

        setAlwaysOnTop(true);
        return (selectedFile != null);
    }

    private synchronized void importLocalMineralStandardModel()
            throws BadLabDataException {

        String dialogTitle = "Select a U-Pb Redux Mineral Standard Model File to Open: *.xml";
        final String fileExtension = ".xml";
        FileFilter nonMacFileFilter = new XMLFileFilter();

        setAlwaysOnTop(false);

        File returnFile
                = FileHelper.AllPlatformGetFile(dialogTitle, null, fileExtension, nonMacFileFilter, false, new JFrame())[0];

        if (returnFile != null) {
            readAndRegisterMineralStandardModel(returnFile);
        }

        // wait to make visible so messages appear
        setAlwaysOnTop(true);
    }

    private synchronized void readAndRegisterMineralStandardModel(File returnFile) {
        AbstractRatiosDataModel mineralStandardModel = MineralStandardUPbModel.getNoneInstance();

        try {
            mineralStandardModel = (AbstractRatiosDataModel) mineralStandardModel.readXMLObject(returnFile.getCanonicalPath(), true);/////true );
        } catch (FileNotFoundException ex) {
            mineralStandardModel = null;
        } catch (IOException ex) {
            mineralStandardModel = null;
        } catch (ETException ex) {
            mineralStandardModel = null;
        } catch (BadOrMissingXMLSchemaException ex) {
            mineralStandardModel = null;
        }

        if (mineralStandardModel != null) {
            try {
                registerMineralStandardModel(mineralStandardModel);

                if (((MineralStandardUPbModel) mineralStandardModel).hasInitialPb()) {
                    registerInitialPbModel(((MineralStandardUPbModel) mineralStandardModel).getInitialPbModelET());
                }

            } catch (BadLabDataException badLabDataException) {
            }
        } else {
            JOptionPane.showConfirmDialog(
                    null,
                    new String[]{"This Mineral Standard could not be imported...please confirm it conforms to the schema."},
                    "ET Redux Warning",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
    // end Mineral Standards tab *********************************************

    // Lab Defaults ************************************************************
    private void InitializeLabDefaultsTabTextBoxes(boolean editable) {

        defaultStaceyKramersRelativeUnct_text.setDocument(
                new BigDecimalDocument(defaultStaceyKramersRelativeUnct_text, editable));

        defaultStaceyKramersCorrelationCoeff_text.setDocument(
                new BigDecimalDocument(defaultStaceyKramersCorrelationCoeff_text, editable));

        defaultPbBlankMass_text.setDocument(new BigDecimalDocument(defaultPbBlankMass_text, editable));
        defaultPbBlankMassOneSigma_text.setDocument(new BigDecimalDocument(defaultPbBlankMassOneSigma_text, editable));

        defaultUBlankMass_text.setDocument(new BigDecimalDocument(defaultUBlankMass_text, editable));
        defaultUBlankMassOneSigma_text.setDocument(new BigDecimalDocument(defaultUBlankMassOneSigma_text, editable));

        default18O_16O_text.setDocument(new BigDecimalDocument(default18O_16O_text, editable));
        default18O_16OOneSigma_text.setDocument(new BigDecimalDocument(default18O_16OOneSigma_text, editable));

        defaultR238_235b_text.setDocument(new BigDecimalDocument(defaultR238_235b_text, editable));
        defaultR238_235bOneSigma_text.setDocument(new BigDecimalDocument(defaultR238_235bOneSigma_text, editable));

        defaultR238_235s_text.setDocument(new BigDecimalDocument(defaultR238_235s_text, editable));
        defaultR238_235sOneSigma_text.setDocument(new BigDecimalDocument(defaultR238_235sOneSigma_text, editable));

        defaultTracerMassOneSigma_text.setDocument(new BigDecimalDocument(defaultTracerMassOneSigma_text, editable));

        defaultRTh_Umagma_text.setDocument(new BigDecimalDocument(defaultRTh_Umagma_text, editable));
        defaultRTh_UmagmaOneSigma_text.setDocument(new BigDecimalDocument(defaultRTh_UmagmaOneSigma_text, editable));

        defaultAr231_235sample_text.setDocument(new BigDecimalDocument(defaultAr231_235sample_text, editable));
        defaultAr231_235sampleOneSigma_text.setDocument(new BigDecimalDocument(defaultAr231_235sampleOneSigma_text, editable));

        countOfGrains_text.setDocument(new IntegerDocument(countOfGrains_text, editable));

    }

    private synchronized void showSavedLabDefaults()
            throws BadLabDataException {

        // set up DefaultTracer chooser
        defaultTracer_Chooser.removeAllItems();
        ArrayList<AbstractRatiosDataModel> tracers = myLabData.getTracers();
        for (int i = (tracers.size() > 1 ? 1 : 0); i < tracers.size(); i++) {
            defaultTracer_Chooser.addItem(((AbstractRatiosDataModel) tracers.get(i)).getReduxLabDataElementName());
        }
        defaultTracer_Chooser.setSelectedItem(myLabData.getDefaultLabTracer().getReduxLabDataElementName());

        // set up DefaultAlphaUModel chooser
        defaultAlphaUModel_Chooser.removeAllItems();
        ArrayList<ValueModel> alphaUModels = myLabData.getAlphaUModels();
        for (int i = (alphaUModels.size() > 1 ? 1 : 0); i < alphaUModels.size(); i++) {
            defaultAlphaUModel_Chooser.addItem(((ValueModel) alphaUModels.get(i)).getName());
        }
        defaultAlphaUModel_Chooser.setSelectedItem(myLabData.getDefaultLabAlphaUModel().getName());

        // set up DefaultAlphaPbModel chooser leaving out the none choice
        defaultAlphaPbModel_Chooser.removeAllItems();
        ArrayList<ValueModel> alphaPbModels = myLabData.getAlphaPbModels();
        for (int i = (alphaPbModels.size() > 1 ? 1 : 0); i < alphaPbModels.size(); i++) {
            defaultAlphaPbModel_Chooser.addItem(((ValueModel) alphaPbModels.get(i)).getName());
        }
        defaultAlphaPbModel_Chooser.setSelectedItem(myLabData.getDefaultLabAlphaPbModel().getName());

        // set up defaultTIMSPMineralStandardModel_Chooser
        defaultTIMSMineralStandardModel_Chooser.removeAllItems();
        ArrayList<AbstractRatiosDataModel> mineralStandardModels = myLabData.getMineralStandardModels();
        for (int i = (mineralStandardModels.size() > 1 ? 1 : 0); i < mineralStandardModels.size(); i++) {
            defaultTIMSMineralStandardModel_Chooser.addItem(mineralStandardModels.get(i).getReduxLabDataElementName());
        }
        defaultTIMSMineralStandardModel_Chooser.setSelectedIndex(0);
        defaultTIMSMineralStandardModel_Chooser.setSelectedItem(myLabData.getDefaultTIMSMineralStandardModel().getReduxLabDataElementName());

        // set up DefaultPbBlank chooser
        defaultPbBlankIC_Chooser.removeAllItems();
        ArrayList<AbstractRatiosDataModel> blanks = myLabData.getPbBlanks();
        for (int i = (blanks.size() > 1 ? 1 : 0); i < blanks.size(); i++) {
            defaultPbBlankIC_Chooser.addItem(blanks.get(i).getReduxLabDataElementName());
        }
        defaultPbBlankIC_Chooser.setSelectedItem(myLabData.getDefaultLabPbBlank().getReduxLabDataElementName());

        // set up DefaultInitialPbModel chooser
        defaultInitialPbModel_Chooser.removeAllItems();
        ArrayList<AbstractRatiosDataModel> initialPbModels = myLabData.getInitialPbModels();
        for (int i = (initialPbModels.size() > 1 ? 1 : 0); i < initialPbModels.size(); i++) {
            defaultInitialPbModel_Chooser.addItem(initialPbModels.get(i).getReduxLabDataElementName());
        }
        defaultInitialPbModel_Chooser.setSelectedItem(myLabData.getDefaultLabInitialPbModel().getReduxLabDataElementName());

        // set up DefaultPhysicalConstantsModel chooser
        defaultPhysicalConstantsModel_Chooser.removeAllItems();
        ArrayList<AbstractRatiosDataModel> pmodels = myLabData.getPhysicalConstantsModels();
        for (int i = (pmodels.size() > 1 ? 1 : 0); i < pmodels.size(); i++) {
            defaultPhysicalConstantsModel_Chooser.addItem(pmodels.get(i).getNameAndVersion());
        }
        defaultPhysicalConstantsModel_Chooser.setSelectedItem(myLabData.getDefaultPhysicalConstantsModel().getNameAndVersion());

        // default Pb Blank mass
        defaultPbBlankMass_text.setText(
                myLabData.getDefaultPbBlankMassInGrams().getValue().//
                multiply(ReduxConstants.PicoGramsPerGram).//
                setScale(ReduxConstants.DEFAULT_MASS_DISPLAY_SCALE, RoundingMode.HALF_UP).toPlainString());
        defaultPbBlankMassOneSigma_text.setText(
                myLabData.getDefaultPbBlankMassInGrams().getOneSigma().//
                multiply(ReduxConstants.PicoGramsPerGram).//
                setScale(ReduxConstants.DEFAULT_MASS_DISPLAY_SCALE, RoundingMode.HALF_UP).toPlainString());

        // assumed U Blank mass
        defaultUBlankMass_text.setText(
                myLabData.getDefaultAssumedUBlankMassInGrams().getValue().//
                multiply(ReduxConstants.PicoGramsPerGram).//
                setScale(ReduxConstants.DEFAULT_MASS_DISPLAY_SCALE, RoundingMode.HALF_UP).toPlainString());
        defaultUBlankMassOneSigma_text.setText(
                myLabData.getDefaultAssumedUBlankMassInGrams().getOneSigma().
                multiply(ReduxConstants.PicoGramsPerGram).//
                setScale(ReduxConstants.DEFAULT_MASS_DISPLAY_SCALE, RoundingMode.HALF_UP).toPlainString());

        // default r18O_16O
        default18O_16O_text.setText(
                myLabData.getDefaultR18O_16O().getValue()//
                .setScale(ReduxConstants.DEFAULT_DEFAULTS_SCALE, RoundingMode.HALF_UP).toPlainString());
        default18O_16OOneSigma_text.setText(
                myLabData.getDefaultR18O_16O().getOneSigma()//
                .setScale(ReduxConstants.DEFAULT_DEFAULTS_SCALE, RoundingMode.HALF_UP).toPlainString());

        // default r238_235b
        defaultR238_235b_text.setText(
                myLabData.getDefaultR238_235b().getValue()//
                .setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE, RoundingMode.HALF_UP).toPlainString());
        defaultR238_235bOneSigma_text.setText(
                myLabData.getDefaultR238_235b().getOneSigma()//
                .setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE, RoundingMode.HALF_UP).toPlainString());

        // default r238_235s
        defaultR238_235s_text.setText(
                myLabData.getDefaultR238_235s().getValue()//
                .setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE, RoundingMode.HALF_UP).toPlainString());
        defaultR238_235sOneSigma_text.setText(
                myLabData.getDefaultR238_235s().getOneSigma()//
                .setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE, RoundingMode.HALF_UP).toPlainString());

        // default tracerModel mass uncertainty
        defaultTracerMassOneSigma_text.setText(
                myLabData.getDefaultTracerMass().getOneSigma()//
                .setScale(ReduxConstants.DEFAULT_MASS_DISPLAY_SCALE, RoundingMode.HALF_UP).toPlainString());

        // default rTh_Umagma
        defaultRTh_Umagma_text.setText(
                myLabData.getDefaultRTh_Umagma().getValue()//
                .setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE, RoundingMode.HALF_UP).toPlainString());
        defaultRTh_UmagmaOneSigma_text.setText(
                myLabData.getDefaultRTh_Umagma().getOneSigma()//
                .setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE, RoundingMode.HALF_UP).toPlainString());

        // default ar231_235sample
        defaultAr231_235sample_text.setText(
                myLabData.getDefaultAr231_235sample().getValue()//
                .setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE, RoundingMode.HALF_UP).toPlainString());
        defaultAr231_235sampleOneSigma_text.setText(
                myLabData.getDefaultAr231_235sample().getOneSigma()//
                .setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE, RoundingMode.HALF_UP).toPlainString());

        // default stacey kramers settings
        defaultStaceyKramersRelativeUnct_text.setText(
                myLabData.getDefaultStaceyKramersOnePctUnct().//
                setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE, RoundingMode.HALF_UP).toPlainString());
        defaultStaceyKramersCorrelationCoeff_text.setText(
                myLabData.getDefaultStaceyKramersCorrelationCoeffs().//
                setScale(ReduxConstants.DEFAULT_PARAMETERS_SCALE, RoundingMode.HALF_UP).toPlainString());

        // set up Mineral chooser
        mineralNameChooser.removeAllItems();
        for (int i = 0; i < MineralTypes.values().length; i++) {
            mineralNameChooser.addItem(MineralTypes.values()[i].getName());
        }

        mineralNameChooser.setSelectedItem(myLabData.getDefaultMineralName());

        // setting type
        settingTypeChooser.removeAllItems();
        for (int i = 0; i < DataDictionary.SettingType.length; i++) {
            settingTypeChooser.addItem(DataDictionary.SettingType[i]);
        }

        settingTypeChooser.setSelectedItem(myLabData.getDefaultSettingType());

        countOfGrains_text.setText(Integer.toString(myLabData.getDefaultNumberOfGrains()));

        physicallyAbraded_chkBox.setSelected(myLabData.isDefaultPhysicallyAbraded());

        leachedInHFAcid_chkBox.setSelected(myLabData.isDefaultLeachedInHFAcid());

        annealedChemicallyAbraded_chkBox.setSelected(myLabData.isDefaultAnnealedAndChemicallyAbraded());

        chemicallyPurifiedUPb_chkBox.setSelected(myLabData.isDefaultChemicallyPurifiedUPb());

    }

    private synchronized void showSavedLAICPMSLabDefaults()
            throws BadLabDataException {

        // hide developer note
        developerNote.setVisible(false);

        // default left shade
        JLabel leftShadeLabel = new JLabel("Number of datapoints to ignore at start of each fraction (4 max): ");
        leftShadeLabel.setHorizontalAlignment(SwingConstants.LEFT);
        leftShadeLabel.setFont(ReduxConstants.sansSerif_12_Bold);
        leftShadeLabel.setBounds(25, 25, 450, 25);
        LAICPMSLabDefaultsPane.add(leftShadeLabel);

        SpinnerModel leftMaskModel
                = new SpinnerNumberModel(2, //initial value
                        0, //min
                        4, //max
                        1); //step
        JSpinner leftMaskSpinner = new JSpinner(leftMaskModel);
        leftMaskSpinner.setBounds(475, 25, 40, 25);
        leftMaskSpinner.setValue(myLabData.getDefaultLeftShadeCountForLAICPMSAquisitions());
        leftMaskSpinner.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                myLabData.setDefaultLeftShadeCountForLAICPMSAquisitions((int) leftMaskSpinner.getValue());
            }
        });
        LAICPMSLabDefaultsPane.add(leftMaskSpinner);

        // set up defaultLAICPMSPrimaryMineralStandardModel_Chooser
        JLabel defaultPrimaryStandardLabel = new JLabel("Select default Primary Mineral Standard Model: ");
        defaultPrimaryStandardLabel.setHorizontalAlignment(SwingConstants.LEFT);
        defaultPrimaryStandardLabel.setFont(ReduxConstants.sansSerif_12_Bold);
        defaultPrimaryStandardLabel.setBounds(25, 60, 450, 25);
        LAICPMSLabDefaultsPane.add(defaultPrimaryStandardLabel);

        defaultLAICPMSPrimaryMineralStandardModel_Chooser.removeAllItems();
        ArrayList<AbstractRatiosDataModel> mineralStandardModels = myLabData.getMineralStandardModels();
        for (int i = (mineralStandardModels.size() > 1 ? 1 : 0); i < mineralStandardModels.size(); i++) {
            defaultLAICPMSPrimaryMineralStandardModel_Chooser.addItem(mineralStandardModels.get(i).getReduxLabDataElementName());
        }
        defaultLAICPMSPrimaryMineralStandardModel_Chooser.setSelectedIndex(0);
        defaultLAICPMSPrimaryMineralStandardModel_Chooser.setSelectedItem(myLabData.getDefaultLAICPMSPrimaryMineralStandardModel().getReduxLabDataElementName());

        // view standard model
        JButton viewStandardModelButton = new ET_JButton("View");
        viewStandardModelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AbstractRatiosDataModel selectedModel = null;
                try {
                    selectedModel = //
                            myLabData.getAMineralStandardModel((String) defaultLAICPMSPrimaryMineralStandardModel_Chooser.getSelectedItem());
                } catch (BadLabDataException badLabDataException) {
                }
                AbstractRatiosDataView modelView = //
                        new MineralStandardUPbRatiosDataViewNotEditable(selectedModel, null, false);
                modelView.displayModelInFrame();
            }
        });
        viewStandardModelButton.setFont(ReduxConstants.sansSerif_10_Bold);
        viewStandardModelButton.setBounds(850, 60, 30, 23);
        LAICPMSLabDefaultsPane.add(viewStandardModelButton);

    }

    private synchronized boolean CheckIsSavedStatusOfDefaultsEdit()
            throws BadLabDataException {
        boolean retval = true;

        saveLabDefaults();

        return retval;
    }

    private synchronized void saveLabDefaults()
            throws BadLabDataException {

        myLabData.setLabName(labName_text.getText().trim());

        // default LabTracer
        try {
            myLabData.setDefaultLabTracer(
                    myLabData.getATracerModel(defaultTracer_Chooser.getSelectedItem().toString()));
        } catch (BadLabDataException ex) {
//            ex.printStackTrace();
        }

        // default LabAlphaUModel
        try {
            myLabData.setDefaultLabAlphaUModel(
                    myLabData.getAnAlphaUModel(defaultAlphaUModel_Chooser.getSelectedItem().toString()));
        } catch (BadLabDataException ex) {
        }

        // default LabAlphaPbModel
        try {
            myLabData.setDefaultLabAlphaPbModel(
                    myLabData.getAnAlphaPbModel(defaultAlphaPbModel_Chooser.getSelectedItem().toString()));
        } catch (BadLabDataException ex) {
        }

        // default MineralStandardModel
        try {
            myLabData.setDefaultTIMSMineralStandardModel(
                    myLabData.getAMineralStandardModel(defaultTIMSMineralStandardModel_Chooser.getSelectedItem().toString()));
        } catch (BadLabDataException ex) {
        }
        try {
            myLabData.setDefaultLAICPMSPrimaryMineralStandardModel(
                    myLabData.getAMineralStandardModel(defaultLAICPMSPrimaryMineralStandardModel_Chooser.getSelectedItem().toString()));
        } catch (BadLabDataException ex) {
        }

        // default LabPbBlank
        try {
            myLabData.setDefaultLabPbBlank(
                    myLabData.getAPbBlankModel(defaultPbBlankIC_Chooser.getSelectedItem().toString()));
        } catch (BadLabDataException ex) {
        }

        // default LabInitialPbModel
        try {
            myLabData.setDefaultLabInitialPbModel(
                    myLabData.getAnInitialPbModel(defaultInitialPbModel_Chooser.getSelectedItem().toString()));
        } catch (BadLabDataException ex) {
        }

        // default Pb Blank mass
        double tempA = Double.valueOf(defaultPbBlankMass_text.getText());
        double tempQ = tempA / ReduxConstants.PicoGramsPerGram.doubleValue();
        myLabData.getDefaultPbBlankMassInGrams().//
                setValue(new BigDecimal(Double.toString(tempQ)));

        tempA = Double.valueOf(defaultPbBlankMassOneSigma_text.getText());
        tempQ = tempA / ReduxConstants.PicoGramsPerGram.doubleValue();
        myLabData.getDefaultPbBlankMassInGrams().
                setOneSigma(new BigDecimal(Double.toString(tempQ)));

        // assumed Blank U mass
        tempA = Double.valueOf(defaultUBlankMass_text.getText());
        tempQ = tempA / ReduxConstants.PicoGramsPerGram.doubleValue();
        myLabData.getDefaultAssumedUBlankMassInGrams().//
                setValue(new BigDecimal(Double.toString(tempQ)));

        tempA = Double.valueOf(defaultUBlankMassOneSigma_text.getText());
        tempQ = tempA / ReduxConstants.PicoGramsPerGram.doubleValue();
        myLabData.getDefaultAssumedUBlankMassInGrams().
                setOneSigma(new BigDecimal(Double.toString(tempQ)));

        // default r18O_16O
        myLabData.getDefaultR18O_16O().//
                setValue(new BigDecimal(default18O_16O_text.getText()));

        myLabData.getDefaultR18O_16O().
                setOneSigma(new BigDecimal(default18O_16OOneSigma_text.getText()));

        // default r238_235b
        myLabData.getDefaultR238_235b().//
                setValue(new BigDecimal(defaultR238_235b_text.getText()));

        myLabData.getDefaultR238_235b().
                setOneSigma(new BigDecimal(defaultR238_235bOneSigma_text.getText()));

        // default r238_235s
        myLabData.getDefaultR238_235s().//
                setValue(new BigDecimal(defaultR238_235s_text.getText()));

        myLabData.getDefaultR238_235s().
                setOneSigma(new BigDecimal(defaultR238_235sOneSigma_text.getText()));

        // default tracerModel mass uncertainty
        myLabData.getDefaultTracerMass().
                setOneSigma(new BigDecimal(defaultTracerMassOneSigma_text.getText()));

        // default rTh_Umagma
        myLabData.getDefaultRTh_Umagma().//
                setValue(new BigDecimal(defaultRTh_Umagma_text.getText()));

        myLabData.getDefaultRTh_Umagma().
                setOneSigma(new BigDecimal(defaultRTh_UmagmaOneSigma_text.getText()));

        // default ar231_235sample
        myLabData.getDefaultAr231_235sample().//
                setValue(new BigDecimal(defaultAr231_235sample_text.getText()));

        myLabData.getDefaultAr231_235sample().
                setOneSigma(new BigDecimal(defaultAr231_235sampleOneSigma_text.getText()));

        // default stacey kramers settings
        myLabData.setDefaultStaceyKramersOnePctUnct(//
                new BigDecimal(defaultStaceyKramersRelativeUnct_text.getText()));

        myLabData.setDefaultStaceyKramersCorrelationCoeffs(//
                new BigDecimal(defaultStaceyKramersCorrelationCoeff_text.getText()));

        // also set the stacey kramers physicalConstantsModel to have these values
        // default PhysicalConstantsModel
        try {
            myLabData.setDefaultPhysicalConstantsModel(
                    myLabData.getAPhysicalConstantsModel(defaultPhysicalConstantsModel_Chooser.getSelectedItem().toString()));
        } catch (BadLabDataException ex) {
            ex.printStackTrace();
        }

        myLabData.setDefaultMineralName(mineralNameChooser.getSelectedItem().toString());
        myLabData.setDefaultSettingType(settingTypeChooser.getSelectedItem().toString());
        myLabData.setDefaultNumberOfGrains(Integer.parseInt(countOfGrains_text.getText()));
        myLabData.setDefaultPhysicallyAbraded(physicallyAbraded_chkBox.isSelected());
        myLabData.setDefaultLeachedInHFAcid(leachedInHFAcid_chkBox.isSelected());
        myLabData.setDefaultAnnealedAndChemicallyAbraded(annealedChemicallyAbraded_chkBox.isSelected());
        myLabData.setDefaultChemicallyPurifiedUPb(chemicallyPurifiedUPb_chkBox.isSelected());

    }

    /**
     *
     */
    public void setSize() {
        setSize(1113, 765);
    }

    private void exitLabData()
            throws BadLabDataException {
        //SaveLabData();
        if (checkIsSavedStatusOfTracerModelEdit()
                && CheckIsSavedStatusOfAlphaPbModelEdit()
                && CheckIsSavedStatusOfAlphaUModelEdit()
                && checkIsSavedStatusOfPbBlankModelEdit()
                && checkIsSavedStatusOfInitialPbModelEdit()
                && checkIsSavedStatusOfPhysicalConstantsModelEdit()
                && checkIsSavedStatusOfMineralStandardModelEdit()
                && CheckIsSavedStatusOfDefaultsEdit()) {
            amOpen = false;
            close();
        }
    }

    /**
     *
     * @return
     */
    public int getCurrentTabIndex() {
        return currentTabIndex;

    }

    /**
     *
     * @param currentTabIndex
     * @throws BadLabDataException
     */
    public void setCurrentTabIndex(int currentTabIndex)
            throws BadLabDataException {
        this.currentTabIndex = currentTabIndex;

        // hide all menu items
        tracers_menu.setVisible(false);
        fractionation_menu.setVisible(false);
        PbBlanks_menu.setVisible(false);
        initialPbModels_menu.setVisible(false);
        physicalConstantsModels_menu.setVisible(false);
        MineralStdModels_menu.setVisible(false);

        // refresh tabs
        switch (currentTabIndex) {
            case 0:
                tracers_menu.setVisible(true);
                CSH.setHelpIDString(this, "LabData.Tracers");
                initTracerModelChooser();
                break;
            case 1:
                fractionation_menu.setVisible(true);
                CSH.setHelpIDString(this, "LabData.Fractionation Models");
                InitAlphaPbChooser();
                InitAlphaPbChooser();
                break;
            case 2:
                PbBlanks_menu.setVisible(true);
                CSH.setHelpIDString(this, "LabData.U-Pb Blank Models");
                initPbBlankModelChooser();
                break;
            case 3:
                initialPbModels_menu.setVisible(true);
                CSH.setHelpIDString(this, "LabData.Initial Pb Model");
                initInitialPbModelChooser();
                break;
            case 4:
                physicalConstantsModels_menu.setVisible(true);
                CSH.setHelpIDString(this, "LabData.Physical constants model");
                initPhysicalConstantsModelChooser();
                break;
            case 5:
                MineralStdModels_menu.setVisible(true);
                CSH.setHelpIDString(this, "LabData.U-Pb Blank Models");
                initMineralStandardModelChooser();
                break;
            case 6:
                showSavedLabDefaults();
                CSH.setHelpIDString(this, "LabData.Managing LabData Default");

        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        labPane_layered = new javax.swing.JLayeredPane();
        details_pane = new javax.swing.JTabbedPane();
        tracersTab_panel = new javax.swing.JPanel();
        tracerModelChooser = new javax.swing.JComboBox<String>();
        chooseTracer_label = new javax.swing.JLabel();
        fractionationModelsTab_panel = new javax.swing.JPanel();
        uFractionation_label = new javax.swing.JLabel();
        monoIsotopicU_label = new javax.swing.JLabel();
        chooseAlphaU_label = new javax.swing.JLabel();
        alphaUChooser = new javax.swing.JComboBox<String>();
        alphaUName_label = new javax.swing.JLabel();
        alphaUName_text = new javax.swing.JTextField();
        coefficientU_label = new javax.swing.JLabel();
        alphaU_label = new javax.swing.JLabel();
        alphaU_text = new javax.swing.JTextField();
        alphaUoneSigma_label = new javax.swing.JLabel();
        alphaUoneSigma_text = new javax.swing.JTextField();
        jSeparator7 = new javax.swing.JSeparator();
        pbFractionation_label = new javax.swing.JLabel();
        monoIsotopicPb_label = new javax.swing.JLabel();
        chooseAlphaPb_label = new javax.swing.JLabel();
        alphaPbChooser = new javax.swing.JComboBox<String>();
        alphaPbName_label = new javax.swing.JLabel();
        alphaPbName_text = new javax.swing.JTextField();
        coefficientPb_label = new javax.swing.JLabel();
        alphaPb_label = new javax.swing.JLabel();
        alphaPb_text = new javax.swing.JTextField();
        alphaPboneSigma_label = new javax.swing.JLabel();
        alphaPboneSigma_text = new javax.swing.JTextField();
        PbBlankICsTab_panel = new javax.swing.JPanel();
        choosePbBlank_label = new javax.swing.JLabel();
        PbBlankChooser = new javax.swing.JComboBox<String>();
        initialPbModelsTab_panel = new javax.swing.JPanel();
        InitialPbModelChooser = new javax.swing.JComboBox<String>();
        chooseInitialPbModel_label = new javax.swing.JLabel();
        staceyKramerCalculator_Panel = new javax.swing.JPanel();
        CalculatedModelInstructions_label = new javax.swing.JLabel();
        estimatedAgeInMA_text = new javax.swing.JTextField();
        refreshCalculations_JButton =  new ET_JButton("Update Model");
        CalculatedModelInstructions_label1 = new javax.swing.JLabel();
        physicalConstantsModelForInitialPbModel_Chooser = new javax.swing.JComboBox<String>();
        relativeUncertainty_label = new javax.swing.JLabel();
        relativeUncertainty_text = new javax.swing.JTextField();
        correlationCoefficients_label = new javax.swing.JLabel();
        correlationCoefficients_text = new javax.swing.JTextField();
        physicalConstantsModels_panel = new javax.swing.JPanel();
        choosePhysicalConstants_label = new javax.swing.JLabel();
        physicalConstantsModel_Chooser = new javax.swing.JComboBox<String>();
        mineralStandard_panel = new javax.swing.JPanel();
        chooseMineralStandardModel_label = new javax.swing.JLabel();
        MineralStandardModelChooser = new javax.swing.JComboBox<String>();
        rareEarthElementsModels_panel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        labDefaults_panel = new javax.swing.JPanel();
        defaultTracer_label = new javax.swing.JLabel();
        defaultTracer_Chooser = new javax.swing.JComboBox<String>();
        defaultPbBlankIC_label = new javax.swing.JLabel();
        defaultPbBlankIC_Chooser = new javax.swing.JComboBox<String>();
        defaultInitialPbModel_label = new javax.swing.JLabel();
        defaultInitialPbModel_Chooser = new javax.swing.JComboBox<String>();
        defaultPbBlankMass_label = new javax.swing.JLabel();
        defaultPbBlankMass_text = new javax.swing.JTextField();
        pbBlankMassOneSigma_label = new javax.swing.JLabel();
        defaultPbBlankMassOneSigma_text = new javax.swing.JTextField();
        defaultUBlankMass_label = new javax.swing.JLabel();
        defaultUBlankMass_text = new javax.swing.JTextField();
        uBlankMassOneSigma_label = new javax.swing.JLabel();
        defaultUBlankMassOneSigma_text = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        defaultPhsicalConstantsModel_label = new javax.swing.JLabel();
        defaultPhysicalConstantsModel_Chooser = new javax.swing.JComboBox<String>();
        jPanel3 = new javax.swing.JPanel();
        defaultMineralStandardModel_label = new javax.swing.JLabel();
        defaultTIMSMineralStandardModel_Chooser = new javax.swing.JComboBox<String>();
        jLabel3 = new javax.swing.JLabel();
        default18O_16O_label = new javax.swing.JLabel();
        defaultR238_235s_label = new javax.swing.JLabel();
        defaultR238_235b_label = new javax.swing.JLabel();
        defaultTracerMass_label = new javax.swing.JLabel();
        default18O_16O_text = new javax.swing.JTextField();
        default18O_16OOneSigma_label = new javax.swing.JLabel();
        default18O_16OOneSigma_text = new javax.swing.JTextField();
        defaultR238_235sOneSigma_text = new javax.swing.JTextField();
        defaultR238_235s_text = new javax.swing.JTextField();
        defaultR238_235sOneSigma_label = new javax.swing.JLabel();
        defaultR238_235b_text = new javax.swing.JTextField();
        defaultR238_235bOneSigma_text = new javax.swing.JTextField();
        defaultR238_235bOneSigma_label = new javax.swing.JLabel();
        defaultTracerMassOneSigma_label = new javax.swing.JLabel();
        defaultTracerMassOneSigma_text = new javax.swing.JTextField();
        defaultrTh_Umagma_label = new javax.swing.JLabel();
        defaultRTh_Umagma_text = new javax.swing.JTextField();
        defaultrTh_UmagmaOneSigma_label = new javax.swing.JLabel();
        defaultRTh_UmagmaOneSigma_text = new javax.swing.JTextField();
        defaultar231_235sample_label = new javax.swing.JLabel();
        defaultAr231_235sample_text = new javax.swing.JTextField();
        defaultar231_235sampleOneSigma_label = new javax.swing.JLabel();
        defaultAr231_235sampleOneSigma_text = new javax.swing.JTextField();
        defaultStaceyKramersRelativeUnct_label = new javax.swing.JLabel();
        defaultStaceyKramersRelativeUnct_text = new javax.swing.JTextField();
        defaultStaceyKramersCorrelationCoeff_label = new javax.swing.JLabel();
        defaultStaceyKramersCorrelationCoeff_text = new javax.swing.JTextField();
        defaultAlphaUModel_label = new javax.swing.JLabel();
        defaultAlphaUModel_Chooser = new javax.swing.JComboBox<String>();
        defaultAlphaPbModel_label = new javax.swing.JLabel();
        defaultAlphaPbModel_Chooser = new javax.swing.JComboBox<String>();
        jLabel5 = new javax.swing.JLabel();
        restoreOriginalDefaultValues_button = new javax.swing.JButton();
        labDefaultsCont_panel = new javax.swing.JLayeredPane();
        chooseMineral_label = new javax.swing.JLabel();
        mineralNameChooser = new javax.swing.JComboBox<String>();
        chooseSettingType_label = new javax.swing.JLabel();
        settingTypeChooser = new javax.swing.JComboBox<String>();
        countOfGrains_label = new javax.swing.JLabel();
        countOfGrains_text = new javax.swing.JTextField();
        physicallyAbraded_chkBox = new javax.swing.JCheckBox();
        leachedInHFAcid_chkBox = new javax.swing.JCheckBox();
        annealedChemicallyAbraded_chkBox = new javax.swing.JCheckBox();
        chemicallyPurifiedUPb_chkBox = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
        LAICPMSLabDefaultsPane = new javax.swing.JPanel();
        defaultLAICPMSPrimaryMineralStandardModel_Chooser = new javax.swing.JComboBox<String>();
        developerNote = new javax.swing.JLabel();
        header_Panel = new javax.swing.JPanel();
        labName_label = new javax.swing.JLabel();
        labName_text = new javax.swing.JTextField();
        buttonsPanel = new javax.swing.JPanel();
        save_button =  new ET_JButton("OK");
        labData_menuBar = new javax.swing.JMenuBar();
        fileMenu_menu = new javax.swing.JMenu();
        closeDialog_menuItem = new javax.swing.JMenuItem();
        tracers_menu = new javax.swing.JMenu();
        earthTimeTracerImport_menuItem = new javax.swing.JMenuItem();
        localTracerImport_menuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        saveTracerAsXML_menuItem = new javax.swing.JMenuItem();
        removeTracer_menuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        editCopyOfCurrentTracer_menuItem = new javax.swing.JMenuItem();
        newTracerMode_menuItem = new javax.swing.JMenuItem();
        cancelNewTracerEdit_menuItem = new javax.swing.JMenuItem();
        fractionation_menu = new javax.swing.JMenu();
        localAlphaUImportXML_menuItem = new javax.swing.JMenuItem();
        localAlphaUImportXML_menuItem.setBackground(ReduxConstants.ColorOfUranium);
        jSeparator8 = new javax.swing.JSeparator();
        saveAlphaUasLocalXML_menuItem = new javax.swing.JMenuItem();
        saveAlphaUasLocalXML_menuItem.setBackground(ReduxConstants.ColorOfUranium);
        removeAlphaU_menuItem = new javax.swing.JMenuItem();
        removeAlphaU_menuItem.setBackground(ReduxConstants.ColorOfUranium);
        jSeparator9 = new javax.swing.JSeparator();
        editNewAlphaU_menuItem = new javax.swing.JMenuItem();
        editNewAlphaU_menuItem.setBackground(ReduxConstants.ColorOfUranium);
        cancelEditAlphaU_menuItem = new javax.swing.JMenuItem();
        cancelEditAlphaU_menuItem.setBackground(ReduxConstants.ColorOfUranium);
        jSeparator10 = new javax.swing.JSeparator();
        jSeparator13 = new javax.swing.JSeparator();
        localAlphaPbImportXML_menuItem = new javax.swing.JMenuItem();
        localAlphaPbImportXML_menuItem.setBackground(ReduxConstants.ColorOfLead);
        jSeparator11 = new javax.swing.JSeparator();
        saveAlphaPbasLocalXML_menuItem = new javax.swing.JMenuItem();
        saveAlphaPbasLocalXML_menuItem.setBackground(ReduxConstants.ColorOfLead);
        removeAlphaPb_menuItem = new javax.swing.JMenuItem();
        removeAlphaPb_menuItem.setBackground(ReduxConstants.ColorOfLead);
        jSeparator12 = new javax.swing.JSeparator();
        editNewAlphaPb_menuItem = new javax.swing.JMenuItem();
        editNewAlphaPb_menuItem.setBackground(ReduxConstants.ColorOfLead);
        cancelEditAlphaPb_menuItem = new javax.swing.JMenuItem();
        cancelEditAlphaPb_menuItem.setBackground(ReduxConstants.ColorOfLead);
        PbBlanks_menu = new javax.swing.JMenu();
        localPbBlankImport_menuItem = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        savePbBlankAsXML_menuItem = new javax.swing.JMenuItem();
        removePbBlank_menuItem = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        editCopyOfCurrentPbBlank_menuItem = new javax.swing.JMenuItem();
        newPbBlankMode_menuItem = new javax.swing.JMenuItem();
        cancelNewEditBlank_menuItem = new javax.swing.JMenuItem();
        initialPbModels_menu = new javax.swing.JMenu();
        localInitialPbModelImport_menuItem = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JSeparator();
        saveInitialPbModelAsXML_menuItem = new javax.swing.JMenuItem();
        removeInitialPbModel_menuItem = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JSeparator();
        editCopyOfCurrentInitialPbModel_menuItem = new javax.swing.JMenuItem();
        newInitialPbModelMode_menuItem = new javax.swing.JMenuItem();
        cancelNewEditInitialPbModel_menuItem = new javax.swing.JMenuItem();
        physicalConstantsModels_menu = new javax.swing.JMenu();
        localPhysicalConstantsModelImport_menuItem = new javax.swing.JMenuItem();
        jSeparator14 = new javax.swing.JSeparator();
        savePhysicalConstantsModelAsXML_menuItem = new javax.swing.JMenuItem();
        removePhysicalConstantsModel_menuItem = new javax.swing.JMenuItem();
        jSeparator15 = new javax.swing.JSeparator();
        editCopyOfCurrentPhysicalConstantsModel_menuItem = new javax.swing.JMenuItem();
        newPhysicalConstantsModel_menuItem = new javax.swing.JMenuItem();
        cancelNewEditPhysicalConstantsModel_menuItem = new javax.swing.JMenuItem();
        MineralStdModels_menu = new javax.swing.JMenu();
        localMineralStdModelImport_menuItem = new javax.swing.JMenuItem();
        jSeparator16 = new javax.swing.JSeparator();
        saveMineralStdModelAsXML_menuItem = new javax.swing.JMenuItem();
        removeMineralStdModel_menuItem = new javax.swing.JMenuItem();
        jSeparator17 = new javax.swing.JSeparator();
        editCopyOfCurrentMineralStdModel_menuItem = new javax.swing.JMenuItem();
        newMineralStdModel_menuItem = new javax.swing.JMenuItem();
        cancelNewEditMineralStdModel_menuItem = new javax.swing.JMenuItem();
        helpMenu_menu = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setAlwaysOnTop(true);
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        details_pane.setBackground(new java.awt.Color(204, 204, 204));
        details_pane.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        details_pane.setFont(new java.awt.Font("Arial", 1, 10)); // NOI18N
        details_pane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                LabDataTabChange(evt);
            }
        });

        chooseTracer_label.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        chooseTracer_label.setText("  Choose Tracer Model:");

        org.jdesktop.layout.GroupLayout tracersTab_panelLayout = new org.jdesktop.layout.GroupLayout(tracersTab_panel);
        tracersTab_panel.setLayout(tracersTab_panelLayout);
        tracersTab_panelLayout.setHorizontalGroup(
            tracersTab_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tracersTab_panelLayout.createSequentialGroup()
                .add(12, 12, 12)
                .add(chooseTracer_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 147, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(6, 6, 6)
                .add(tracerModelChooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 400, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        tracersTab_panelLayout.setVerticalGroup(
            tracersTab_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(tracersTab_panelLayout.createSequentialGroup()
                .add(12, 12, 12)
                .add(chooseTracer_label))
            .add(tracersTab_panelLayout.createSequentialGroup()
                .add(6, 6, 6)
                .add(tracerModelChooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        details_pane.addTab("Tracer Models", tracersTab_panel);

        uFractionation_label.setBackground(new java.awt.Color(0, 0, 0));
        uFractionation_label.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        uFractionation_label.setForeground(new java.awt.Color(255, 255, 153));
        uFractionation_label.setText("U Fractionation");
        uFractionation_label.setOpaque(true);

        monoIsotopicU_label.setText("for mono-isotopic U tracer");

        chooseAlphaU_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chooseAlphaU_label.setText("Choose Model:");

        alphaUName_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        alphaUName_label.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        alphaUName_label.setText("Model Name:");

        alphaUName_text.setEditable(false);
        alphaUName_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        alphaUName_text.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        alphaUName_text.setMinimumSize(new java.awt.Dimension(36, 19));
        alphaUName_text.setPreferredSize(new java.awt.Dimension(36, 19));

        coefficientU_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        coefficientU_label.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        coefficientU_label.setText("coefficient for U fractionation in %/amu:");

        alphaU_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        alphaU_label.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        alphaU_label.setText("alpha U:");
        alphaU_label.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        alphaU_text.setEditable(false);
        alphaU_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        alphaU_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        alphaU_text.setText("0.0");
        alphaU_text.setMinimumSize(new java.awt.Dimension(36, 19));
        alphaU_text.setPreferredSize(new java.awt.Dimension(36, 19));

        alphaUoneSigma_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        alphaUoneSigma_label.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        alphaUoneSigma_label.setText("one Sigma abs:");
        alphaUoneSigma_label.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        alphaUoneSigma_text.setEditable(false);
        alphaUoneSigma_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        alphaUoneSigma_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        alphaUoneSigma_text.setText("0.0");
        alphaUoneSigma_text.setMinimumSize(new java.awt.Dimension(36, 19));
        alphaUoneSigma_text.setPreferredSize(new java.awt.Dimension(36, 19));

        jSeparator7.setBackground(new java.awt.Color(0, 0, 0));
        jSeparator7.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jSeparator7.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        jSeparator7.setOpaque(true);

        pbFractionation_label.setBackground(new java.awt.Color(0, 0, 0));
        pbFractionation_label.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        pbFractionation_label.setForeground(new java.awt.Color(204, 255, 204));
        pbFractionation_label.setText("Pb Fractionation");
        pbFractionation_label.setOpaque(true);

        monoIsotopicPb_label.setText("for mono-isotopic Pb tracer");

        chooseAlphaPb_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chooseAlphaPb_label.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        chooseAlphaPb_label.setText("Choose Model:");

        alphaPbName_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        alphaPbName_label.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        alphaPbName_label.setText("Model Name:");

        alphaPbName_text.setEditable(false);
        alphaPbName_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        alphaPbName_text.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        alphaPbName_text.setMinimumSize(new java.awt.Dimension(36, 19));
        alphaPbName_text.setPreferredSize(new java.awt.Dimension(36, 19));

        coefficientPb_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        coefficientPb_label.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        coefficientPb_label.setText("coefficient for Pb fractionation in %/amu:");

        alphaPb_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        alphaPb_label.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        alphaPb_label.setText("alpha Pb:");
        alphaPb_label.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        alphaPb_text.setEditable(false);
        alphaPb_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        alphaPb_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        alphaPb_text.setText("0.0");
        alphaPb_text.setMinimumSize(new java.awt.Dimension(36, 19));
        alphaPb_text.setPreferredSize(new java.awt.Dimension(36, 19));

        alphaPboneSigma_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        alphaPboneSigma_label.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        alphaPboneSigma_label.setText("one Sigma abs:");
        alphaPboneSigma_label.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        alphaPboneSigma_text.setEditable(false);
        alphaPboneSigma_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        alphaPboneSigma_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        alphaPboneSigma_text.setText("0.0");
        alphaPboneSigma_text.setMinimumSize(new java.awt.Dimension(36, 19));
        alphaPboneSigma_text.setPreferredSize(new java.awt.Dimension(36, 19));

        org.jdesktop.layout.GroupLayout fractionationModelsTab_panelLayout = new org.jdesktop.layout.GroupLayout(fractionationModelsTab_panel);
        fractionationModelsTab_panel.setLayout(fractionationModelsTab_panelLayout);
        fractionationModelsTab_panelLayout.setHorizontalGroup(
            fractionationModelsTab_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(fractionationModelsTab_panelLayout.createSequentialGroup()
                .add(6, 6, 6)
                .add(fractionationModelsTab_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(uFractionation_label)
                    .add(fractionationModelsTab_panelLayout.createSequentialGroup()
                        .add(77, 77, 77)
                        .add(monoIsotopicU_label))
                    .add(fractionationModelsTab_panelLayout.createSequentialGroup()
                        .add(chooseAlphaU_label)
                        .add(6, 6, 6)
                        .add(alphaUChooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 214, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(fractionationModelsTab_panelLayout.createSequentialGroup()
                        .add(alphaUName_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 82, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(6, 6, 6)
                        .add(alphaUName_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 214, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(fractionationModelsTab_panelLayout.createSequentialGroup()
                        .add(64, 64, 64)
                        .add(coefficientU_label))
                    .add(fractionationModelsTab_panelLayout.createSequentialGroup()
                        .add(88, 88, 88)
                        .add(alphaU_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(35, 35, 35)
                        .add(alphaU_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(fractionationModelsTab_panelLayout.createSequentialGroup()
                        .add(88, 88, 88)
                        .add(alphaUoneSigma_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 89, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(6, 6, 6)
                        .add(alphaUoneSigma_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .add(26, 26, 26)
                .add(jSeparator7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(fractionationModelsTab_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(pbFractionation_label)
                    .add(fractionationModelsTab_panelLayout.createSequentialGroup()
                        .add(79, 79, 79)
                        .add(monoIsotopicPb_label))
                    .add(fractionationModelsTab_panelLayout.createSequentialGroup()
                        .add(chooseAlphaPb_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(6, 6, 6)
                        .add(alphaPbChooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 205, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(fractionationModelsTab_panelLayout.createSequentialGroup()
                        .add(27, 27, 27)
                        .add(alphaPbName_label)
                        .add(6, 6, 6)
                        .add(alphaPbName_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 205, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(fractionationModelsTab_panelLayout.createSequentialGroup()
                        .add(70, 70, 70)
                        .add(coefficientPb_label))
                    .add(fractionationModelsTab_panelLayout.createSequentialGroup()
                        .add(106, 106, 106)
                        .add(alphaPb_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(38, 38, 38)
                        .add(alphaPb_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(fractionationModelsTab_panelLayout.createSequentialGroup()
                        .add(106, 106, 106)
                        .add(alphaPboneSigma_label)
                        .add(12, 12, 12)
                        .add(alphaPboneSigma_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 100, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))))
        );
        fractionationModelsTab_panelLayout.setVerticalGroup(
            fractionationModelsTab_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jSeparator7, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 617, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
            .add(fractionationModelsTab_panelLayout.createSequentialGroup()
                .add(20, 20, 20)
                .add(fractionationModelsTab_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(fractionationModelsTab_panelLayout.createSequentialGroup()
                        .add(uFractionation_label)
                        .add(6, 6, 6)
                        .add(monoIsotopicU_label)
                        .add(28, 28, 28)
                        .add(fractionationModelsTab_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(fractionationModelsTab_panelLayout.createSequentialGroup()
                                .add(6, 6, 6)
                                .add(chooseAlphaU_label))
                            .add(alphaUChooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(15, 15, 15)
                        .add(fractionationModelsTab_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(fractionationModelsTab_panelLayout.createSequentialGroup()
                                .add(2, 2, 2)
                                .add(alphaUName_label))
                            .add(alphaUName_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(37, 37, 37)
                        .add(coefficientU_label)
                        .add(18, 18, 18)
                        .add(fractionationModelsTab_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(fractionationModelsTab_panelLayout.createSequentialGroup()
                                .add(2, 2, 2)
                                .add(alphaU_label))
                            .add(alphaU_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(15, 15, 15)
                        .add(fractionationModelsTab_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(fractionationModelsTab_panelLayout.createSequentialGroup()
                                .add(2, 2, 2)
                                .add(alphaUoneSigma_label))
                            .add(alphaUoneSigma_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(fractionationModelsTab_panelLayout.createSequentialGroup()
                        .add(pbFractionation_label)
                        .add(6, 6, 6)
                        .add(monoIsotopicPb_label)
                        .add(27, 27, 27)
                        .add(fractionationModelsTab_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(fractionationModelsTab_panelLayout.createSequentialGroup()
                                .add(6, 6, 6)
                                .add(chooseAlphaPb_label))
                            .add(alphaPbChooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(17, 17, 17)
                        .add(fractionationModelsTab_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(fractionationModelsTab_panelLayout.createSequentialGroup()
                                .add(2, 2, 2)
                                .add(alphaPbName_label))
                            .add(alphaPbName_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(38, 38, 38)
                        .add(coefficientPb_label)
                        .add(18, 18, 18)
                        .add(fractionationModelsTab_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(fractionationModelsTab_panelLayout.createSequentialGroup()
                                .add(2, 2, 2)
                                .add(alphaPb_label))
                            .add(alphaPb_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(16, 16, 16)
                        .add(fractionationModelsTab_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(fractionationModelsTab_panelLayout.createSequentialGroup()
                                .add(2, 2, 2)
                                .add(alphaPboneSigma_label))
                            .add(alphaPboneSigma_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))))
        );

        details_pane.addTab("Fractionation Models", fractionationModelsTab_panel);

        choosePbBlank_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        choosePbBlank_label.setText("  Choose Pb Blank:");

        org.jdesktop.layout.GroupLayout PbBlankICsTab_panelLayout = new org.jdesktop.layout.GroupLayout(PbBlankICsTab_panel);
        PbBlankICsTab_panel.setLayout(PbBlankICsTab_panelLayout);
        PbBlankICsTab_panelLayout.setHorizontalGroup(
            PbBlankICsTab_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(PbBlankICsTab_panelLayout.createSequentialGroup()
                .add(6, 6, 6)
                .add(choosePbBlank_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 195, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(6, 6, 6)
                .add(PbBlankChooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 408, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        PbBlankICsTab_panelLayout.setVerticalGroup(
            PbBlankICsTab_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(PbBlankICsTab_panelLayout.createSequentialGroup()
                .add(12, 12, 12)
                .add(choosePbBlank_label))
            .add(PbBlankICsTab_panelLayout.createSequentialGroup()
                .add(6, 6, 6)
                .add(PbBlankChooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        details_pane.addTab("Pb Blank IC Models", PbBlankICsTab_panel);

        chooseInitialPbModel_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chooseInitialPbModel_label.setText("  Choose Initial Pb Model:");
        chooseInitialPbModel_label.setVerifyInputWhenFocusTarget(false);

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

        CalculatedModelInstructions_label1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        CalculatedModelInstructions_label1.setText("Select Physical Constants Model (default is shown):");

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
            .add(staceyKramerCalculator_PanelLayout.createSequentialGroup()
                .add(41, 41, 41)
                .add(CalculatedModelInstructions_label1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(physicalConstantsModelForInitialPbModel_Chooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 242, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, staceyKramerCalculator_PanelLayout.createSequentialGroup()
                .addContainerGap(235, Short.MAX_VALUE)
                .add(staceyKramerCalculator_PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(CalculatedModelInstructions_label)
                    .add(relativeUncertainty_label)
                    .add(correlationCoefficients_label))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(staceyKramerCalculator_PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING, false)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, relativeUncertainty_text)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, estimatedAgeInMA_text, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .add(correlationCoefficients_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 103, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(refreshCalculations_JButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 298, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(151, 151, 151))
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
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(staceyKramerCalculator_PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(CalculatedModelInstructions_label1)
                    .add(physicalConstantsModelForInitialPbModel_Chooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 27, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(20, 20, 20))
        );

        org.jdesktop.layout.GroupLayout initialPbModelsTab_panelLayout = new org.jdesktop.layout.GroupLayout(initialPbModelsTab_panel);
        initialPbModelsTab_panel.setLayout(initialPbModelsTab_panelLayout);
        initialPbModelsTab_panelLayout.setHorizontalGroup(
            initialPbModelsTab_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(initialPbModelsTab_panelLayout.createSequentialGroup()
                .add(6, 6, 6)
                .add(chooseInitialPbModel_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 159, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(6, 6, 6)
                .add(InitialPbModelChooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 275, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(staceyKramerCalculator_Panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );
        initialPbModelsTab_panelLayout.setVerticalGroup(
            initialPbModelsTab_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(initialPbModelsTab_panelLayout.createSequentialGroup()
                .add(6, 6, 6)
                .add(initialPbModelsTab_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(initialPbModelsTab_panelLayout.createSequentialGroup()
                        .add(6, 6, 6)
                        .add(chooseInitialPbModel_label))
                    .add(InitialPbModelChooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(454, 454, 454)
                .add(staceyKramerCalculator_Panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 130, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        details_pane.addTab("Initial Pb Models", initialPbModelsTab_panel);

        choosePhysicalConstants_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        choosePhysicalConstants_label.setText("  Choose Physical Constants Model:");

        org.jdesktop.layout.GroupLayout physicalConstantsModels_panelLayout = new org.jdesktop.layout.GroupLayout(physicalConstantsModels_panel);
        physicalConstantsModels_panel.setLayout(physicalConstantsModels_panelLayout);
        physicalConstantsModels_panelLayout.setHorizontalGroup(
            physicalConstantsModels_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(physicalConstantsModels_panelLayout.createSequentialGroup()
                .add(6, 6, 6)
                .add(choosePhysicalConstants_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 203, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(6, 6, 6)
                .add(physicalConstantsModel_Chooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 425, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        physicalConstantsModels_panelLayout.setVerticalGroup(
            physicalConstantsModels_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(physicalConstantsModels_panelLayout.createSequentialGroup()
                .add(6, 6, 6)
                .add(choosePhysicalConstants_label))
            .add(physicalConstantsModel_Chooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        details_pane.addTab("Physical Constants Models", physicalConstantsModels_panel);

        chooseMineralStandardModel_label.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        chooseMineralStandardModel_label.setText("  Choose Mineral Standard Model:");

        MineralStandardModelChooser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MineralStandardModelChooserActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout mineralStandard_panelLayout = new org.jdesktop.layout.GroupLayout(mineralStandard_panel);
        mineralStandard_panel.setLayout(mineralStandard_panelLayout);
        mineralStandard_panelLayout.setHorizontalGroup(
            mineralStandard_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mineralStandard_panelLayout.createSequentialGroup()
                .add(18, 18, 18)
                .add(chooseMineralStandardModel_label)
                .add(5, 5, 5)
                .add(MineralStandardModelChooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 461, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        mineralStandard_panelLayout.setVerticalGroup(
            mineralStandard_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mineralStandard_panelLayout.createSequentialGroup()
                .add(12, 12, 12)
                .add(chooseMineralStandardModel_label))
            .add(mineralStandard_panelLayout.createSequentialGroup()
                .add(6, 6, 6)
                .add(MineralStandardModelChooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        details_pane.addTab("Mineral Std Models", mineralStandard_panel);

        jLabel1.setText("Coming soon ...");

        org.jdesktop.layout.GroupLayout rareEarthElementsModels_panelLayout = new org.jdesktop.layout.GroupLayout(rareEarthElementsModels_panel);
        rareEarthElementsModels_panel.setLayout(rareEarthElementsModels_panelLayout);
        rareEarthElementsModels_panelLayout.setHorizontalGroup(
            rareEarthElementsModels_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(rareEarthElementsModels_panelLayout.createSequentialGroup()
                .add(431, 431, 431)
                .add(jLabel1)
                .addContainerGap(561, Short.MAX_VALUE))
        );
        rareEarthElementsModels_panelLayout.setVerticalGroup(
            rareEarthElementsModels_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(rareEarthElementsModels_panelLayout.createSequentialGroup()
                .add(290, 290, 290)
                .add(jLabel1)
                .addContainerGap(311, Short.MAX_VALUE))
        );

        details_pane.addTab("REE Models", rareEarthElementsModels_panel);

        labDefaults_panel.setBackground(new java.awt.Color(212, 231, 232));

        defaultTracer_label.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        defaultTracer_label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        defaultTracer_label.setText("Set the default Tracer:");

        defaultPbBlankIC_label.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        defaultPbBlankIC_label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        defaultPbBlankIC_label.setText("Set the default Pb Blank IC:");

        defaultInitialPbModel_label.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        defaultInitialPbModel_label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        defaultInitialPbModel_label.setText("Set the default Initial Pb Model:");

        defaultPbBlankMass_label.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        defaultPbBlankMass_label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        defaultPbBlankMass_label.setText("Set the default mass of blank Pb:");

        defaultPbBlankMass_text.setEditable(false);
        defaultPbBlankMass_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        defaultPbBlankMass_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        defaultPbBlankMass_text.setText("0.0");

        pbBlankMassOneSigma_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        pbBlankMassOneSigma_label.setText("pg      1-sigma ABS:");

        defaultPbBlankMassOneSigma_text.setEditable(false);
        defaultPbBlankMassOneSigma_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        defaultPbBlankMassOneSigma_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        defaultPbBlankMassOneSigma_text.setText("0.0");

        defaultUBlankMass_label.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        defaultUBlankMass_label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        defaultUBlankMass_label.setText("Set the assumed mass of blank U:");

        defaultUBlankMass_text.setEditable(false);
        defaultUBlankMass_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        defaultUBlankMass_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        defaultUBlankMass_text.setText("0.0");

        uBlankMassOneSigma_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        uBlankMassOneSigma_label.setText("pg      1-sigma ABS:");

        defaultUBlankMassOneSigma_text.setEditable(false);
        defaultUBlankMassOneSigma_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        defaultUBlankMassOneSigma_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        defaultUBlankMassOneSigma_text.setText("0.0");

        jPanel2.setBackground(new java.awt.Color(245, 236, 206));
        jPanel2.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        defaultPhsicalConstantsModel_label.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        defaultPhsicalConstantsModel_label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        defaultPhsicalConstantsModel_label.setText("Set the default Physical Constants Model for each new Sample:");

        org.jdesktop.layout.GroupLayout jPanel2Layout = new org.jdesktop.layout.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .add(defaultPhsicalConstantsModel_label)
                .add(18, 18, 18)
                .add(defaultPhysicalConstantsModel_Chooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 311, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(56, 56, 56))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(defaultPhsicalConstantsModel_label)
                .add(defaultPhysicalConstantsModel_Chooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        jPanel3.setBackground(new java.awt.Color(180, 180, 180));
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        defaultMineralStandardModel_label.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        defaultMineralStandardModel_label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        defaultMineralStandardModel_label.setText("Set the default Mineral Standard Model for each new ALIQUOT:");

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(defaultMineralStandardModel_label)
                .add(18, 18, 18)
                .add(defaultTIMSMineralStandardModel_Chooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 308, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(367, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(defaultMineralStandardModel_label)
                .add(defaultTIMSMineralStandardModel_Chooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        jLabel3.setFont(new java.awt.Font("Lucida Grande", 1, 16)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("FRACTION");

        default18O_16O_label.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        default18O_16O_label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        default18O_16O_label.setText("Set the default value of 18O/16O U oxide:");

        defaultR238_235s_label.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        defaultR238_235s_label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        defaultR238_235s_label.setText("Set the default value of 238U/235U sample:");

        defaultR238_235b_label.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        defaultR238_235b_label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        defaultR238_235b_label.setText("Set the default value of 238U/235U blank:");

        defaultTracerMass_label.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        defaultTracerMass_label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        defaultTracerMass_label.setText("Set the default Uncertainty for tracer mass:");

        default18O_16O_text.setEditable(false);
        default18O_16O_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        default18O_16O_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        default18O_16O_text.setText("0.0");

        default18O_16OOneSigma_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        default18O_16OOneSigma_label.setText(" 1-sigma ABS:");

        default18O_16OOneSigma_text.setEditable(false);
        default18O_16OOneSigma_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        default18O_16OOneSigma_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        default18O_16OOneSigma_text.setText("0.0");

        defaultR238_235sOneSigma_text.setEditable(false);
        defaultR238_235sOneSigma_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        defaultR238_235sOneSigma_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        defaultR238_235sOneSigma_text.setText("0.0");

        defaultR238_235s_text.setEditable(false);
        defaultR238_235s_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        defaultR238_235s_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        defaultR238_235s_text.setText("0.0");

        defaultR238_235sOneSigma_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        defaultR238_235sOneSigma_label.setText("1-sigma ABS:");

        defaultR238_235b_text.setEditable(false);
        defaultR238_235b_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        defaultR238_235b_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        defaultR238_235b_text.setText("0.0");

        defaultR238_235bOneSigma_text.setEditable(false);
        defaultR238_235bOneSigma_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        defaultR238_235bOneSigma_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        defaultR238_235bOneSigma_text.setText("0.0");

        defaultR238_235bOneSigma_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        defaultR238_235bOneSigma_label.setText("1-sigma ABS:");

        defaultTracerMassOneSigma_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        defaultTracerMassOneSigma_label.setText("g      1-sigma ABS:");

        defaultTracerMassOneSigma_text.setEditable(false);
        defaultTracerMassOneSigma_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        defaultTracerMassOneSigma_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        defaultTracerMassOneSigma_text.setText("0.0");

        defaultrTh_Umagma_label.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        defaultrTh_Umagma_label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        defaultrTh_Umagma_label.setText("Set the default value of rTh_Umagma:");

        defaultRTh_Umagma_text.setEditable(false);
        defaultRTh_Umagma_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        defaultRTh_Umagma_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        defaultRTh_Umagma_text.setText("0.0");

        defaultrTh_UmagmaOneSigma_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        defaultrTh_UmagmaOneSigma_label.setText("1-sigma ABS:");

        defaultRTh_UmagmaOneSigma_text.setEditable(false);
        defaultRTh_UmagmaOneSigma_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        defaultRTh_UmagmaOneSigma_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        defaultRTh_UmagmaOneSigma_text.setText("0.0");

        defaultar231_235sample_label.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        defaultar231_235sample_label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        defaultar231_235sample_label.setText("Set the default value of ar231_235sample:");

        defaultAr231_235sample_text.setEditable(false);
        defaultAr231_235sample_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        defaultAr231_235sample_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        defaultAr231_235sample_text.setText("0.0");

        defaultar231_235sampleOneSigma_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        defaultar231_235sampleOneSigma_label.setText("1-sigma ABS:");

        defaultAr231_235sampleOneSigma_text.setEditable(false);
        defaultAr231_235sampleOneSigma_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        defaultAr231_235sampleOneSigma_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        defaultAr231_235sampleOneSigma_text.setText("0.0");

        defaultStaceyKramersRelativeUnct_label.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        defaultStaceyKramersRelativeUnct_label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        defaultStaceyKramersRelativeUnct_label.setText("For Stacey-Kramers, set default relative uncertainty for IC as 1-sigma PCT:");

        defaultStaceyKramersRelativeUnct_text.setEditable(false);
        defaultStaceyKramersRelativeUnct_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        defaultStaceyKramersRelativeUnct_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        defaultStaceyKramersRelativeUnct_text.setText("0.0");

        defaultStaceyKramersCorrelationCoeff_label.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        defaultStaceyKramersCorrelationCoeff_label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        defaultStaceyKramersCorrelationCoeff_label.setText("For Stacey-Kramers, set default correlation coefficients for IC [-1.000,1.000]:");

        defaultStaceyKramersCorrelationCoeff_text.setEditable(false);
        defaultStaceyKramersCorrelationCoeff_text.setFont(new java.awt.Font("Courier New", 1, 12)); // NOI18N
        defaultStaceyKramersCorrelationCoeff_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        defaultStaceyKramersCorrelationCoeff_text.setText("0.0");

        defaultAlphaUModel_label.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        defaultAlphaUModel_label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        defaultAlphaUModel_label.setText("Set default Alpha U Model for a mono-isotopic U Tracer for each new FRACTION:");

        defaultAlphaPbModel_label.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        defaultAlphaPbModel_label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        defaultAlphaPbModel_label.setText("Set default Alpha Pb Model for a mono-isotopic Pb Tracer for each new FRACTION:");

        jLabel5.setFont(new java.awt.Font("Lucida Grande", 1, 16)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel5.setText("(cont.)");

        restoreOriginalDefaultValues_button.setText("Restore original default values");
        restoreOriginalDefaultValues_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                restoreOriginalDefaultValues_buttonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout labDefaults_panelLayout = new org.jdesktop.layout.GroupLayout(labDefaults_panel);
        labDefaults_panel.setLayout(labDefaults_panelLayout);
        labDefaults_panelLayout.setHorizontalGroup(
            labDefaults_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(labDefaults_panelLayout.createSequentialGroup()
                .add(6, 6, 6)
                .add(defaultAlphaPbModel_label)
                .add(18, 18, 18)
                .add(defaultAlphaPbModel_Chooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 205, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(labDefaults_panelLayout.createSequentialGroup()
                .add(6, 6, 6)
                .add(defaultAlphaUModel_label)
                .add(32, 32, 32)
                .add(defaultAlphaUModel_Chooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 205, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(labDefaults_panelLayout.createSequentialGroup()
                .add(6, 6, 6)
                .add(defaultTracer_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 148, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(94, 94, 94)
                .add(defaultTracer_Chooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 250, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(labDefaults_panelLayout.createSequentialGroup()
                .add(6, 6, 6)
                .add(defaultInitialPbModel_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 213, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(29, 29, 29)
                .add(defaultInitialPbModel_Chooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 250, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(labDefaults_panelLayout.createSequentialGroup()
                .add(46, 46, 46)
                .add(defaultStaceyKramersRelativeUnct_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 462, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(6, 6, 6)
                .add(defaultStaceyKramersRelativeUnct_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(labDefaults_panelLayout.createSequentialGroup()
                .add(36, 36, 36)
                .add(defaultStaceyKramersCorrelationCoeff_label)
                .add(6, 6, 6)
                .add(defaultStaceyKramersCorrelationCoeff_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(labDefaults_panelLayout.createSequentialGroup()
                .add(6, 6, 6)
                .add(defaultPbBlankIC_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 173, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(69, 69, 69)
                .add(defaultPbBlankIC_Chooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 250, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(379, 379, 379)
                .add(jLabel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 103, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(labDefaults_panelLayout.createSequentialGroup()
                .add(6, 6, 6)
                .add(defaultPbBlankMass_label)
                .add(75, 75, 75)
                .add(defaultPbBlankMass_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(6, 6, 6)
                .add(pbBlankMassOneSigma_label)
                .add(6, 6, 6)
                .add(defaultPbBlankMassOneSigma_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(labDefaults_panelLayout.createSequentialGroup()
                .add(6, 6, 6)
                .add(defaultUBlankMass_label)
                .add(73, 73, 73)
                .add(defaultUBlankMass_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(6, 6, 6)
                .add(uBlankMassOneSigma_label)
                .add(6, 6, 6)
                .add(defaultUBlankMassOneSigma_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(labDefaults_panelLayout.createSequentialGroup()
                .add(6, 6, 6)
                .add(default18O_16O_label)
                .add(19, 19, 19)
                .add(default18O_16O_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(35, 35, 35)
                .add(default18O_16OOneSigma_label)
                .add(6, 6, 6)
                .add(default18O_16OOneSigma_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(labDefaults_panelLayout.createSequentialGroup()
                .add(6, 6, 6)
                .add(defaultR238_235s_label)
                .add(6, 6, 6)
                .add(defaultR238_235s_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(38, 38, 38)
                .add(defaultR238_235sOneSigma_label)
                .add(6, 6, 6)
                .add(defaultR238_235sOneSigma_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(91, 91, 91)
                .add(restoreOriginalDefaultValues_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 250, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(labDefaults_panelLayout.createSequentialGroup()
                .add(6, 6, 6)
                .add(defaultR238_235b_label)
                .add(15, 15, 15)
                .add(defaultR238_235b_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(38, 38, 38)
                .add(defaultR238_235bOneSigma_label)
                .add(6, 6, 6)
                .add(defaultR238_235bOneSigma_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(labDefaults_panelLayout.createSequentialGroup()
                .add(6, 6, 6)
                .add(defaultTracerMass_label)
                .add(137, 137, 137)
                .add(defaultTracerMassOneSigma_label)
                .add(6, 6, 6)
                .add(defaultTracerMassOneSigma_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(labDefaults_panelLayout.createSequentialGroup()
                .add(6, 6, 6)
                .add(defaultrTh_Umagma_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 260, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(defaultRTh_Umagma_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(39, 39, 39)
                .add(defaultrTh_UmagmaOneSigma_label)
                .add(6, 6, 6)
                .add(defaultRTh_UmagmaOneSigma_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(labDefaults_panelLayout.createSequentialGroup()
                .add(6, 6, 6)
                .add(defaultar231_235sample_label)
                .add(12, 12, 12)
                .add(defaultAr231_235sample_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(41, 41, 41)
                .add(defaultar231_235sampleOneSigma_label)
                .add(6, 6, 6)
                .add(defaultAr231_235sampleOneSigma_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 110, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(304, 304, 304)
                .add(jLabel5))
        );
        labDefaults_panelLayout.setVerticalGroup(
            labDefaults_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(labDefaults_panelLayout.createSequentialGroup()
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(6, 6, 6)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(6, 6, 6)
                .add(labDefaults_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(labDefaults_panelLayout.createSequentialGroup()
                        .add(5, 5, 5)
                        .add(defaultAlphaPbModel_label))
                    .add(defaultAlphaPbModel_Chooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(12, 12, 12)
                .add(labDefaults_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(labDefaults_panelLayout.createSequentialGroup()
                        .add(5, 5, 5)
                        .add(defaultAlphaUModel_label))
                    .add(defaultAlphaUModel_Chooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(labDefaults_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(labDefaults_panelLayout.createSequentialGroup()
                        .add(5, 5, 5)
                        .add(defaultTracer_label))
                    .add(defaultTracer_Chooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(6, 6, 6)
                .add(labDefaults_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(labDefaults_panelLayout.createSequentialGroup()
                        .add(5, 5, 5)
                        .add(defaultInitialPbModel_label))
                    .add(defaultInitialPbModel_Chooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(12, 12, 12)
                .add(labDefaults_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(labDefaults_panelLayout.createSequentialGroup()
                        .add(4, 4, 4)
                        .add(defaultStaceyKramersRelativeUnct_label))
                    .add(defaultStaceyKramersRelativeUnct_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(6, 6, 6)
                .add(labDefaults_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(labDefaults_panelLayout.createSequentialGroup()
                        .add(4, 4, 4)
                        .add(defaultStaceyKramersCorrelationCoeff_label))
                    .add(defaultStaceyKramersCorrelationCoeff_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(18, 18, 18)
                .add(labDefaults_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(labDefaults_panelLayout.createSequentialGroup()
                        .add(5, 5, 5)
                        .add(defaultPbBlankIC_label))
                    .add(defaultPbBlankIC_Chooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(labDefaults_panelLayout.createSequentialGroup()
                        .add(1, 1, 1)
                        .add(jLabel3)))
                .add(6, 6, 6)
                .add(labDefaults_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(labDefaults_panelLayout.createSequentialGroup()
                        .add(5, 5, 5)
                        .add(defaultPbBlankMass_label))
                    .add(defaultPbBlankMass_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(labDefaults_panelLayout.createSequentialGroup()
                        .add(6, 6, 6)
                        .add(pbBlankMassOneSigma_label))
                    .add(defaultPbBlankMassOneSigma_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(6, 6, 6)
                .add(labDefaults_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(labDefaults_panelLayout.createSequentialGroup()
                        .add(5, 5, 5)
                        .add(defaultUBlankMass_label))
                    .add(defaultUBlankMass_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(labDefaults_panelLayout.createSequentialGroup()
                        .add(6, 6, 6)
                        .add(uBlankMassOneSigma_label))
                    .add(defaultUBlankMassOneSigma_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(6, 6, 6)
                .add(labDefaults_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(labDefaults_panelLayout.createSequentialGroup()
                        .add(5, 5, 5)
                        .add(default18O_16O_label))
                    .add(default18O_16O_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(labDefaults_panelLayout.createSequentialGroup()
                        .add(6, 6, 6)
                        .add(default18O_16OOneSigma_label))
                    .add(default18O_16OOneSigma_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(6, 6, 6)
                .add(labDefaults_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(labDefaults_panelLayout.createSequentialGroup()
                        .add(7, 7, 7)
                        .add(defaultR238_235s_label))
                    .add(labDefaults_panelLayout.createSequentialGroup()
                        .add(1, 1, 1)
                        .add(defaultR238_235s_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(labDefaults_panelLayout.createSequentialGroup()
                        .add(7, 7, 7)
                        .add(defaultR238_235sOneSigma_label))
                    .add(labDefaults_panelLayout.createSequentialGroup()
                        .add(1, 1, 1)
                        .add(defaultR238_235sOneSigma_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(restoreOriginalDefaultValues_button))
                .add(6, 6, 6)
                .add(labDefaults_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(labDefaults_panelLayout.createSequentialGroup()
                        .add(5, 5, 5)
                        .add(defaultR238_235b_label))
                    .add(defaultR238_235b_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(labDefaults_panelLayout.createSequentialGroup()
                        .add(6, 6, 6)
                        .add(defaultR238_235bOneSigma_label))
                    .add(defaultR238_235bOneSigma_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(6, 6, 6)
                .add(labDefaults_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(labDefaults_panelLayout.createSequentialGroup()
                        .add(5, 5, 5)
                        .add(defaultTracerMass_label))
                    .add(labDefaults_panelLayout.createSequentialGroup()
                        .add(6, 6, 6)
                        .add(defaultTracerMassOneSigma_label))
                    .add(defaultTracerMassOneSigma_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(6, 6, 6)
                .add(labDefaults_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(labDefaults_panelLayout.createSequentialGroup()
                        .add(5, 5, 5)
                        .add(defaultrTh_Umagma_label))
                    .add(defaultRTh_Umagma_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(labDefaults_panelLayout.createSequentialGroup()
                        .add(6, 6, 6)
                        .add(defaultrTh_UmagmaOneSigma_label))
                    .add(defaultRTh_UmagmaOneSigma_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(6, 6, 6)
                .add(labDefaults_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(labDefaults_panelLayout.createSequentialGroup()
                        .add(5, 5, 5)
                        .add(defaultar231_235sample_label))
                    .add(defaultAr231_235sample_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(labDefaults_panelLayout.createSequentialGroup()
                        .add(6, 6, 6)
                        .add(defaultar231_235sampleOneSigma_label))
                    .add(defaultAr231_235sampleOneSigma_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(labDefaults_panelLayout.createSequentialGroup()
                        .add(3, 3, 3)
                        .add(jLabel5)))
                .addContainerGap())
        );

        details_pane.addTab("Lab Defaults I", labDefaults_panel);

        labDefaultsCont_panel.setBackground(new java.awt.Color(212, 231, 232));
        labDefaultsCont_panel.setOpaque(true);

        chooseMineral_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chooseMineral_label.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        chooseMineral_label.setText("  Set default mineral for fractions:");

        chooseSettingType_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chooseSettingType_label.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        chooseSettingType_label.setText("  Set default setting type for fractions:");

        countOfGrains_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        countOfGrains_label.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        countOfGrains_label.setText("Set default count of grains in a fraction:");

        countOfGrains_text.setEditable(false);
        countOfGrains_text.setFont(new java.awt.Font("Courier New", 1, 18)); // NOI18N
        countOfGrains_text.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        countOfGrains_text.setText("1");

        physicallyAbraded_chkBox.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        physicallyAbraded_chkBox.setText("Set default choice for physically abraded fractions:");
        physicallyAbraded_chkBox.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        physicallyAbraded_chkBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        leachedInHFAcid_chkBox.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        leachedInHFAcid_chkBox.setText("Set default choice for leached in HF acid fractions:");
        leachedInHFAcid_chkBox.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        leachedInHFAcid_chkBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        annealedChemicallyAbraded_chkBox.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        annealedChemicallyAbraded_chkBox.setText("Set default choice for annealed & chemically abraded fractions:");
        annealedChemicallyAbraded_chkBox.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        annealedChemicallyAbraded_chkBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        chemicallyPurifiedUPb_chkBox.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chemicallyPurifiedUPb_chkBox.setText("Set default choice for chemically purified UPb fractions:");
        chemicallyPurifiedUPb_chkBox.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        chemicallyPurifiedUPb_chkBox.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);

        jLabel4.setFont(new java.awt.Font("Lucida Grande", 1, 16)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel4.setText("Additional defaults for FRACTION");

        org.jdesktop.layout.GroupLayout labDefaultsCont_panelLayout = new org.jdesktop.layout.GroupLayout(labDefaultsCont_panel);
        labDefaultsCont_panel.setLayout(labDefaultsCont_panelLayout);
        labDefaultsCont_panelLayout.setHorizontalGroup(
            labDefaultsCont_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(labDefaultsCont_panelLayout.createSequentialGroup()
                .add(430, 430, 430)
                .add(jLabel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 310, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(labDefaultsCont_panelLayout.createSequentialGroup()
                .add(160, 160, 160)
                .add(chooseMineral_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 240, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(10, 10, 10)
                .add(mineralNameChooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 200, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(labDefaultsCont_panelLayout.createSequentialGroup()
                .add(160, 160, 160)
                .add(chooseSettingType_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 240, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(10, 10, 10)
                .add(settingTypeChooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 200, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(labDefaultsCont_panelLayout.createSequentialGroup()
                .add(160, 160, 160)
                .add(countOfGrains_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 240, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(20, 20, 20)
                .add(countOfGrains_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(labDefaultsCont_panelLayout.createSequentialGroup()
                .add(30, 30, 30)
                .add(physicallyAbraded_chkBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 400, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(labDefaultsCont_panelLayout.createSequentialGroup()
                .add(30, 30, 30)
                .add(leachedInHFAcid_chkBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 400, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(labDefaultsCont_panelLayout.createSequentialGroup()
                .add(30, 30, 30)
                .add(annealedChemicallyAbraded_chkBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 400, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(labDefaultsCont_panelLayout.createSequentialGroup()
                .add(30, 30, 30)
                .add(chemicallyPurifiedUPb_chkBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 400, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        labDefaultsCont_panelLayout.setVerticalGroup(
            labDefaultsCont_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(labDefaultsCont_panelLayout.createSequentialGroup()
                .add(30, 30, 30)
                .add(jLabel4)
                .add(40, 40, 40)
                .add(labDefaultsCont_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(labDefaultsCont_panelLayout.createSequentialGroup()
                        .add(7, 7, 7)
                        .add(chooseMineral_label))
                    .add(mineralNameChooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(13, 13, 13)
                .add(labDefaultsCont_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(labDefaultsCont_panelLayout.createSequentialGroup()
                        .add(5, 5, 5)
                        .add(chooseSettingType_label))
                    .add(settingTypeChooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(3, 3, 3)
                .add(labDefaultsCont_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(labDefaultsCont_panelLayout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(countOfGrains_label))
                    .add(countOfGrains_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(17, 17, 17)
                .add(physicallyAbraded_chkBox)
                .add(7, 7, 7)
                .add(leachedInHFAcid_chkBox)
                .add(7, 7, 7)
                .add(annealedChemicallyAbraded_chkBox)
                .add(7, 7, 7)
                .add(chemicallyPurifiedUPb_chkBox))
        );
        labDefaultsCont_panel.setLayer(chooseMineral_label, javax.swing.JLayeredPane.DEFAULT_LAYER);
        labDefaultsCont_panel.setLayer(mineralNameChooser, javax.swing.JLayeredPane.DEFAULT_LAYER);
        labDefaultsCont_panel.setLayer(chooseSettingType_label, javax.swing.JLayeredPane.DEFAULT_LAYER);
        labDefaultsCont_panel.setLayer(settingTypeChooser, javax.swing.JLayeredPane.DEFAULT_LAYER);
        labDefaultsCont_panel.setLayer(countOfGrains_label, javax.swing.JLayeredPane.DEFAULT_LAYER);
        labDefaultsCont_panel.setLayer(countOfGrains_text, javax.swing.JLayeredPane.DEFAULT_LAYER);
        labDefaultsCont_panel.setLayer(physicallyAbraded_chkBox, javax.swing.JLayeredPane.DEFAULT_LAYER);
        labDefaultsCont_panel.setLayer(leachedInHFAcid_chkBox, javax.swing.JLayeredPane.DEFAULT_LAYER);
        labDefaultsCont_panel.setLayer(annealedChemicallyAbraded_chkBox, javax.swing.JLayeredPane.DEFAULT_LAYER);
        labDefaultsCont_panel.setLayer(chemicallyPurifiedUPb_chkBox, javax.swing.JLayeredPane.DEFAULT_LAYER);
        labDefaultsCont_panel.setLayer(jLabel4, javax.swing.JLayeredPane.DEFAULT_LAYER);

        details_pane.addTab("Lab Defaults II", labDefaultsCont_panel);

        LAICPMSLabDefaultsPane.setLayout(null);
        LAICPMSLabDefaultsPane.add(defaultLAICPMSPrimaryMineralStandardModel_Chooser);
        defaultLAICPMSPrimaryMineralStandardModel_Chooser.setBounds(475, 60, 350, 27);

        developerNote.setText("Note: elements are programmatically generated here.");
        LAICPMSLabDefaultsPane.add(developerNote);
        developerNote.setBounds(10, 0, 630, 16);

        details_pane.addTab("LAICPMS Defaults", LAICPMSLabDefaultsPane);

        header_Panel.setBackground(new java.awt.Color(252, 236, 235));
        header_Panel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        labName_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        labName_label.setText("Laboratory Name:");

        labName_text.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        labName_text.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        labName_text.setText("laboratory name");

        org.jdesktop.layout.GroupLayout header_PanelLayout = new org.jdesktop.layout.GroupLayout(header_Panel);
        header_Panel.setLayout(header_PanelLayout);
        header_PanelLayout.setHorizontalGroup(
            header_PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(header_PanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(labName_label)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(labName_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 421, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        header_PanelLayout.setVerticalGroup(
            header_PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(header_PanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(labName_label)
                .add(labName_text, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        buttonsPanel.setBackground(new java.awt.Color(252, 236, 235));
        buttonsPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        save_button.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        save_button.setForeground(new java.awt.Color(255, 51, 0));
        save_button.setText("OK");
        save_button.setMargin(new java.awt.Insets(0, 1, 0, 1));
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
                .addContainerGap()
                .add(save_button, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        buttonsPanelLayout.setVerticalGroup(
            buttonsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(save_button, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout labPane_layeredLayout = new org.jdesktop.layout.GroupLayout(labPane_layered);
        labPane_layered.setLayout(labPane_layeredLayout);
        labPane_layeredLayout.setHorizontalGroup(
            labPane_layeredLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(header_Panel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(buttonsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(details_pane)
        );
        labPane_layeredLayout.setVerticalGroup(
            labPane_layeredLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(labPane_layeredLayout.createSequentialGroup()
                .add(header_Panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(649, 649, 649)
                .add(buttonsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
            .add(labPane_layeredLayout.createSequentialGroup()
                .add(24, 24, 24)
                .add(details_pane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 660, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        labPane_layered.setLayer(details_pane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        labPane_layered.setLayer(header_Panel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        labPane_layered.setLayer(buttonsPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        fileMenu_menu.setText("File");

        closeDialog_menuItem.setText("Close LabData");
        closeDialog_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeDialog_menuItemActionPerformed(evt);
            }
        });
        fileMenu_menu.add(closeDialog_menuItem);

        labData_menuBar.add(fileMenu_menu);

        tracers_menu.setText("Tracer Models");
        tracers_menu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                tracers_menuMenuSelected(evt);
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
        });

        earthTimeTracerImport_menuItem.setText("Import Tracer from Earth-Time.org (tracers are currently built-in)");
        earthTimeTracerImport_menuItem.setEnabled(false);
        earthTimeTracerImport_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                earthTimeTracerImport_menuItemActionPerformed(evt);
            }
        });
        tracers_menu.add(earthTimeTracerImport_menuItem);

        localTracerImport_menuItem.setText("Import Tracer from local file");
        localTracerImport_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                localTracerImport_menuItemActionPerformed(evt);
            }
        });
        tracers_menu.add(localTracerImport_menuItem);
        tracers_menu.add(jSeparator1);

        saveTracerAsXML_menuItem.setText("Save current Tracer as XML file");
        saveTracerAsXML_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveTracerAsXML_menuItemActionPerformed(evt);
            }
        });
        tracers_menu.add(saveTracerAsXML_menuItem);

        removeTracer_menuItem.setText("Remove current Tracer from Lab Data");
        removeTracer_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeTracer_menuItemActionPerformed(evt);
            }
        });
        tracers_menu.add(removeTracer_menuItem);
        tracers_menu.add(jSeparator2);

        editCopyOfCurrentTracer_menuItem.setText("Edit copy of current Tracer");
        editCopyOfCurrentTracer_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editCopyOfCurrentTracer_menuItemActionPerformed(evt);
            }
        });
        tracers_menu.add(editCopyOfCurrentTracer_menuItem);

        newTracerMode_menuItem.setText("Edit new empty Tracer");
        newTracerMode_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newTracerMode_menuItemActionPerformed(evt);
            }
        });
        tracers_menu.add(newTracerMode_menuItem);

        cancelNewTracerEdit_menuItem.setText("Cancel Edit of Tracer");
        cancelNewTracerEdit_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelNewTracerEdit_menuItemActionPerformed(evt);
            }
        });
        tracers_menu.add(cancelNewTracerEdit_menuItem);

        labData_menuBar.add(tracers_menu);

        fractionation_menu.setText("Fractionation Models");
        fractionation_menu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fractionation_menuActionPerformed(evt);
            }
        });
        fractionation_menu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                fractionation_menuMenuSelected(evt);
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
        });

        localAlphaUImportXML_menuItem.setText("Import AlphaU Model from local XML file");
        localAlphaUImportXML_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                localAlphaUImportXML_menuItemActionPerformed(evt);
            }
        });
        fractionation_menu.add(localAlphaUImportXML_menuItem);
        fractionation_menu.add(jSeparator8);

        saveAlphaUasLocalXML_menuItem.setText("Save AlphaU Model as local XML file");
        saveAlphaUasLocalXML_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAlphaUasLocalXML_menuItemActionPerformed(evt);
            }
        });
        fractionation_menu.add(saveAlphaUasLocalXML_menuItem);

        removeAlphaU_menuItem.setText("Remove Alpha U from Lab Data");
        removeAlphaU_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeAlphaU_menuItemActionPerformed(evt);
            }
        });
        fractionation_menu.add(removeAlphaU_menuItem);
        fractionation_menu.add(jSeparator9);

        editNewAlphaU_menuItem.setText("Edit new AlphaU Model");
        editNewAlphaU_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editNewAlphaU_menuItemActionPerformed(evt);
            }
        });
        fractionation_menu.add(editNewAlphaU_menuItem);

        cancelEditAlphaU_menuItem.setText("Cancel edit of new AlphaU Model");
        cancelEditAlphaU_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelEditAlphaU_menuItemActionPerformed(evt);
            }
        });
        fractionation_menu.add(cancelEditAlphaU_menuItem);

        jSeparator10.setBorder(javax.swing.BorderFactory.createCompoundBorder());
        fractionation_menu.add(jSeparator10);
        fractionation_menu.add(jSeparator13);

        localAlphaPbImportXML_menuItem.setText("Import AlphaPb Model from local XML file");
        localAlphaPbImportXML_menuItem.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        localAlphaPbImportXML_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                localAlphaPbImportXML_menuItemActionPerformed(evt);
            }
        });
        fractionation_menu.add(localAlphaPbImportXML_menuItem);
        fractionation_menu.add(jSeparator11);

        saveAlphaPbasLocalXML_menuItem.setText("Save AlphaPb Model as local XML file");
        saveAlphaPbasLocalXML_menuItem.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        saveAlphaPbasLocalXML_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAlphaPbasLocalXML_menuItemActionPerformed(evt);
            }
        });
        fractionation_menu.add(saveAlphaPbasLocalXML_menuItem);

        removeAlphaPb_menuItem.setText("Remove AlphaPb Model from Lab Data");
        removeAlphaPb_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeAlphaPb_menuItemActionPerformed(evt);
            }
        });
        fractionation_menu.add(removeAlphaPb_menuItem);
        fractionation_menu.add(jSeparator12);

        editNewAlphaPb_menuItem.setText("Edit new AlphaPb Model");
        editNewAlphaPb_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editNewAlphaPb_menuItemActionPerformed(evt);
            }
        });
        fractionation_menu.add(editNewAlphaPb_menuItem);

        cancelEditAlphaPb_menuItem.setText("Cancel edit of new AlphaPb Model");
        cancelEditAlphaPb_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelEditAlphaPb_menuItemActionPerformed(evt);
            }
        });
        fractionation_menu.add(cancelEditAlphaPb_menuItem);

        labData_menuBar.add(fractionation_menu);

        PbBlanks_menu.setText("Pb Blank IC Models");
        PbBlanks_menu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                PbBlanks_menuMenuSelected(evt);
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
        });

        localPbBlankImport_menuItem.setText("Import Pb Blank from local XML file");
        localPbBlankImport_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                localPbBlankImport_menuItemActionPerformed(evt);
            }
        });
        PbBlanks_menu.add(localPbBlankImport_menuItem);
        PbBlanks_menu.add(jSeparator3);

        savePbBlankAsXML_menuItem.setText("Save current Pb Blank as XML file");
        savePbBlankAsXML_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                savePbBlankAsXML_menuItemActionPerformed(evt);
            }
        });
        PbBlanks_menu.add(savePbBlankAsXML_menuItem);

        removePbBlank_menuItem.setText("Remove current Pb Blank from Lab Data");
        removePbBlank_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removePbBlank_menuItemActionPerformed(evt);
            }
        });
        PbBlanks_menu.add(removePbBlank_menuItem);
        PbBlanks_menu.add(jSeparator4);

        editCopyOfCurrentPbBlank_menuItem.setText("Edit copy of current Pb Blank");
        editCopyOfCurrentPbBlank_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editCopyOfCurrentPbBlank_menuItemActionPerformed(evt);
            }
        });
        PbBlanks_menu.add(editCopyOfCurrentPbBlank_menuItem);

        newPbBlankMode_menuItem.setText("Edit new empty Pb Blank");
        newPbBlankMode_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newPbBlankMode_menuItemActionPerformed(evt);
            }
        });
        PbBlanks_menu.add(newPbBlankMode_menuItem);

        cancelNewEditBlank_menuItem.setText("Cancel Edit of Pb Blank");
        cancelNewEditBlank_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelNewEditBlank_menuItemActionPerformed(evt);
            }
        });
        PbBlanks_menu.add(cancelNewEditBlank_menuItem);

        labData_menuBar.add(PbBlanks_menu);
        PbBlanks_menu.getAccessibleContext().setAccessibleName("");

        initialPbModels_menu.setText("Initial Pb Models");
        initialPbModels_menu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                initialPbModels_menuMenuSelected(evt);
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
        });

        localInitialPbModelImport_menuItem.setText("Import Initial Pb Model from local XML file");
        localInitialPbModelImport_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                localInitialPbModelImport_menuItemActionPerformed(evt);
            }
        });
        initialPbModels_menu.add(localInitialPbModelImport_menuItem);
        initialPbModels_menu.add(jSeparator5);

        saveInitialPbModelAsXML_menuItem.setText("Save Initial Pb Model as local XML file");
        saveInitialPbModelAsXML_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveInitialPbModelAsXML_menuItemActionPerformed(evt);
            }
        });
        initialPbModels_menu.add(saveInitialPbModelAsXML_menuItem);

        removeInitialPbModel_menuItem.setText("Remove current Initial Pb Model from Lab Data");
        removeInitialPbModel_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeInitialPbModel_menuItemActionPerformed(evt);
            }
        });
        initialPbModels_menu.add(removeInitialPbModel_menuItem);
        initialPbModels_menu.add(jSeparator6);

        editCopyOfCurrentInitialPbModel_menuItem.setText("Edit Copy of current Initial Pb Model");
        editCopyOfCurrentInitialPbModel_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editCopyOfCurrentInitialPbModel_menuItemActionPerformed(evt);
            }
        });
        initialPbModels_menu.add(editCopyOfCurrentInitialPbModel_menuItem);

        newInitialPbModelMode_menuItem.setText("Edit New empty Initial Pb Model");
        newInitialPbModelMode_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newInitialPbModelMode_menuItemActionPerformed(evt);
            }
        });
        initialPbModels_menu.add(newInitialPbModelMode_menuItem);

        cancelNewEditInitialPbModel_menuItem.setText("Cancel edit of Initial Pb Model");
        cancelNewEditInitialPbModel_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelNewEditInitialPbModel_menuItemActionPerformed(evt);
            }
        });
        initialPbModels_menu.add(cancelNewEditInitialPbModel_menuItem);

        labData_menuBar.add(initialPbModels_menu);

        physicalConstantsModels_menu.setText("Physical Constants Models");
        physicalConstantsModels_menu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                physicalConstantsModels_menuMenuSelected(evt);
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
        });

        localPhysicalConstantsModelImport_menuItem.setText("Import Physical Constants Model from local XML file");
        localPhysicalConstantsModelImport_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                localPhysicalConstantsModelImport_menuItemActionPerformed(evt);
            }
        });
        physicalConstantsModels_menu.add(localPhysicalConstantsModelImport_menuItem);
        physicalConstantsModels_menu.add(jSeparator14);

        savePhysicalConstantsModelAsXML_menuItem.setText("Save Physical Constants Model as local XML file");
        savePhysicalConstantsModelAsXML_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                savePhysicalConstantsModelAsXML_menuItemActionPerformed(evt);
            }
        });
        physicalConstantsModels_menu.add(savePhysicalConstantsModelAsXML_menuItem);

        removePhysicalConstantsModel_menuItem.setText("Remove current Physical Constants Model from Lab Data");
        removePhysicalConstantsModel_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removePhysicalConstantsModel_menuItemActionPerformed(evt);
            }
        });
        physicalConstantsModels_menu.add(removePhysicalConstantsModel_menuItem);
        physicalConstantsModels_menu.add(jSeparator15);

        editCopyOfCurrentPhysicalConstantsModel_menuItem.setText("Edit Copy of current Physical Constants Model");
        editCopyOfCurrentPhysicalConstantsModel_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editCopyOfCurrentPhysicalConstantsModel_menuItemActionPerformed(evt);
            }
        });
        physicalConstantsModels_menu.add(editCopyOfCurrentPhysicalConstantsModel_menuItem);

        newPhysicalConstantsModel_menuItem.setText("Edit New empty Physical Constants Model");
        newPhysicalConstantsModel_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newPhysicalConstantsModel_menuItemActionPerformed(evt);
            }
        });
        physicalConstantsModels_menu.add(newPhysicalConstantsModel_menuItem);

        cancelNewEditPhysicalConstantsModel_menuItem.setText("Cancel edit of Physical Constants Model");
        cancelNewEditPhysicalConstantsModel_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelNewEditPhysicalConstantsModel_menuItemActionPerformed(evt);
            }
        });
        physicalConstantsModels_menu.add(cancelNewEditPhysicalConstantsModel_menuItem);

        labData_menuBar.add(physicalConstantsModels_menu);

        MineralStdModels_menu.setText("Mineral Std Models");
        MineralStdModels_menu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                mineralStdModels_menuMenuSelected(evt);
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
        });

        localMineralStdModelImport_menuItem.setText("Import Mineral Standard Model from local XML file");
        localMineralStdModelImport_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                localMineralStdModelImport_menuItemActionPerformed(evt);
            }
        });
        MineralStdModels_menu.add(localMineralStdModelImport_menuItem);
        MineralStdModels_menu.add(jSeparator16);

        saveMineralStdModelAsXML_menuItem.setText("Save Mineral Standard Model as local XML file");
        saveMineralStdModelAsXML_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveMineralStdModelAsXML_menuItemActionPerformed(evt);
            }
        });
        MineralStdModels_menu.add(saveMineralStdModelAsXML_menuItem);

        removeMineralStdModel_menuItem.setText("Remove current Mineral Standard Model from Lab Data");
        removeMineralStdModel_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeMineralStdModel_menuItemActionPerformed(evt);
            }
        });
        MineralStdModels_menu.add(removeMineralStdModel_menuItem);
        MineralStdModels_menu.add(jSeparator17);

        editCopyOfCurrentMineralStdModel_menuItem.setText("Edit Copy of current Mineral Standard Model");
        editCopyOfCurrentMineralStdModel_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editCopyOfCurrentMineralStdModel_menuItemActionPerformed(evt);
            }
        });
        MineralStdModels_menu.add(editCopyOfCurrentMineralStdModel_menuItem);

        newMineralStdModel_menuItem.setText("Edit New empty Mineral Standard Model");
        newMineralStdModel_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newMineralStdModel_menuItemActionPerformed(evt);
            }
        });
        MineralStdModels_menu.add(newMineralStdModel_menuItem);

        cancelNewEditMineralStdModel_menuItem.setText("Cancel edit of Mineral Standard Model");
        cancelNewEditMineralStdModel_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelNewEditMineralStdModel_menuItemActionPerformed(evt);
            }
        });
        MineralStdModels_menu.add(cancelNewEditMineralStdModel_menuItem);

        labData_menuBar.add(MineralStdModels_menu);

        helpMenu_menu.setText("Help");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        jMenuItem1.setText("Help");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        helpMenu_menu.add(jMenuItem1);

        labData_menuBar.add(helpMenu_menu);

        setJMenuBar(labData_menuBar);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, labPane_layered)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(labPane_layered)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void fractionation_menuMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_fractionation_menuMenuSelected

        details_pane.setSelectedComponent(fractionationModelsTab_panel);

        boolean amInEditAlphaUMode
                = (getNewEmptyAlphaUModel() == null) && (alphaUChooser.getSelectedIndex() > 0);

        editNewAlphaU_menuItem.setEnabled(amInEditAlphaUMode);

        localAlphaUImportXML_menuItem.setEnabled(alphaUChooser.isEnabled());

        editNewAlphaU_menuItem.setEnabled(getNewEmptyAlphaUModel() == null);
        cancelEditAlphaU_menuItem.setEnabled(getNewEmptyAlphaUModel() != null);

        saveAlphaUasLocalXML_menuItem.setEnabled((getNewEmptyAlphaUModel() != null)
                || (!(((String) alphaUChooser.getSelectedItem()).equalsIgnoreCase("<none>") || ((String) alphaUChooser.getSelectedItem()).equalsIgnoreCase(ReduxConstants.NONE))));
        try {

            removeAlphaU_menuItem.setEnabled((getNewEmptyAlphaUModel() == null)
                    && (!(((String) alphaUChooser.getSelectedItem()).equalsIgnoreCase("<none>") || ((String) alphaUChooser.getSelectedItem()).equalsIgnoreCase(ReduxConstants.NONE))) && (myLabData.getDefaultLabAlphaUModel().getName().trim().compareTo(
                            myLabData.getAnAlphaUModel((String) alphaUChooser.getSelectedItem()).getName().trim()) != 0));
        } catch (BadLabDataException ex) {
//            ex.printStackTrace();
        }

        boolean amInEditAlphaPbMode
                = (getNewEmptyAlphaPbModel() == null) && (alphaPbChooser.getSelectedIndex() > 0);

        editNewAlphaPb_menuItem.setEnabled(amInEditAlphaPbMode);

        localAlphaPbImportXML_menuItem.setEnabled(alphaPbChooser.isEnabled());

        editNewAlphaPb_menuItem.setEnabled(getNewEmptyAlphaPbModel() == null);
        cancelEditAlphaPb_menuItem.setEnabled(getNewEmptyAlphaPbModel() != null);

        saveAlphaPbasLocalXML_menuItem.setEnabled((getNewEmptyAlphaPbModel() != null)
                || (!(((String) alphaPbChooser.getSelectedItem()).equalsIgnoreCase("<none>") || ((String) alphaPbChooser.getSelectedItem()).equalsIgnoreCase(ReduxConstants.NONE))));
        try {

            removeAlphaPb_menuItem.setEnabled((getNewEmptyAlphaPbModel() == null)
                    && (!(((String) alphaPbChooser.getSelectedItem()).equalsIgnoreCase("<none>") || ((String) alphaPbChooser.getSelectedItem()).equalsIgnoreCase(ReduxConstants.NONE))) && (myLabData.getDefaultLabAlphaPbModel().getName().trim().compareTo(
                            myLabData.getAnAlphaPbModel((String) alphaPbChooser.getSelectedItem()).getName().trim()) != 0));
        } catch (BadLabDataException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_fractionation_menuMenuSelected

    private void fractionation_menuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fractionation_menuActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_fractionation_menuActionPerformed

    private void cancelEditAlphaU_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelEditAlphaU_menuItemActionPerformed
        try {
            CancelNewAlphaUModelEdit();
        } catch (BadLabDataException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_cancelEditAlphaU_menuItemActionPerformed

    private void editNewAlphaU_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editNewAlphaU_menuItemActionPerformed

        try {
            EditNewEmptyAlphaUModel();
        } catch (BadLabDataException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_editNewAlphaU_menuItemActionPerformed

    private void removeAlphaU_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeAlphaU_menuItemActionPerformed
        try {
            RemoveCurrentAlphaUModel();
        } catch (BadLabDataException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_removeAlphaU_menuItemActionPerformed

    private void saveAlphaUasLocalXML_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAlphaUasLocalXML_menuItemActionPerformed
        try {
            ExportAlphaUModelAsXML();
        } catch (BadLabDataException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_saveAlphaUasLocalXML_menuItemActionPerformed

    private void localAlphaPbImportXML_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_localAlphaPbImportXML_menuItemActionPerformed
        try {
            if (CheckIsSavedStatusOfAlphaPbModelEdit()) {
                try {
                    ImportLocalAlphaPbModel();
                } catch (BadLabDataException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (BadLabDataException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_localAlphaPbImportXML_menuItemActionPerformed

    private void localAlphaUImportXML_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_localAlphaUImportXML_menuItemActionPerformed
        try {
            if (CheckIsSavedStatusOfAlphaUModelEdit()) {
                try {
                    ImportLocalAlphaUModel();
                } catch (BadLabDataException ex) {
                    ex.printStackTrace();
                }
            }
        } catch (BadLabDataException ex) {
        }
    }//GEN-LAST:event_localAlphaUImportXML_menuItemActionPerformed

    private void newInitialPbModelMode_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newInitialPbModelMode_menuItemActionPerformed
        try {
            editNewEmptyInitialPbModel();
        } catch (BadLabDataException ex) {
        }
    }//GEN-LAST:event_newInitialPbModelMode_menuItemActionPerformed

    private void cancelNewEditInitialPbModel_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelNewEditInitialPbModel_menuItemActionPerformed
        try {
            cancelNewInitialPbModelEdit();
        } catch (BadLabDataException ex) {
        }
    }//GEN-LAST:event_cancelNewEditInitialPbModel_menuItemActionPerformed

    private void saveInitialPbModelAsXML_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveInitialPbModelAsXML_menuItemActionPerformed
        try {
            exportInitialPbModelAsXML();
        } catch (BadLabDataException ex) {
        } catch (ETException etex) {
        }
    }//GEN-LAST:event_saveInitialPbModelAsXML_menuItemActionPerformed

    private void localInitialPbModelImport_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_localInitialPbModelImport_menuItemActionPerformed
        try {
            if (checkIsSavedStatusOfInitialPbModelEdit()) {
                try {
                    importLocalInitialPbModel();
                } catch (BadLabDataException ex) {
                }
            }
        } catch (BadLabDataException ex) {
        }
    }//GEN-LAST:event_localInitialPbModelImport_menuItemActionPerformed

    private void removeInitialPbModel_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeInitialPbModel_menuItemActionPerformed
        try {
            removeCurrentInitialPbModel();
        } catch (BadLabDataException ex) {
            try {
                cancelNewInitialPbModelEdit();
            } catch (BadLabDataException ex2) {
            }
        }
    }//GEN-LAST:event_removeInitialPbModel_menuItemActionPerformed

    private void editCopyOfCurrentInitialPbModel_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editCopyOfCurrentInitialPbModel_menuItemActionPerformed
        try {
            editCopyOfCurrentInitialPbModel();
        } catch (BadLabDataException ex) {
        }
    }//GEN-LAST:event_editCopyOfCurrentInitialPbModel_menuItemActionPerformed

    private void refreshCalculations_JButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshCalculations_JButtonActionPerformed
        try {
            populateInitialPbModelFields(currentEditableInitialPbModel, false);
        } catch (BadLabDataException ex) {
        }
    }//GEN-LAST:event_refreshCalculations_JButtonActionPerformed

    private void LabDataTabChange(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_LabDataTabChange
        // save off work when changing tabs
        // determine if changing tab or just clicking panel
        if (getCurrentTabIndex() == details_pane.getSelectedIndex()) {
            // we have clicked the same tab so nothing happens
        } else {
            try {
                // we are changing tabs so we need to detect edit mode and warn
                if (checkIsSavedStatusOfTracerModelEdit()
                        && CheckIsSavedStatusOfAlphaPbModelEdit()
                        && CheckIsSavedStatusOfAlphaUModelEdit()
                        && checkIsSavedStatusOfPbBlankModelEdit()
                        && checkIsSavedStatusOfInitialPbModelEdit()
                        && checkIsSavedStatusOfPhysicalConstantsModelEdit()
                        && checkIsSavedStatusOfMineralStandardModelEdit()
                        && CheckIsSavedStatusOfDefaultsEdit()) {
                    saveLabDefaults();
                    cancelNewTracerModelEdit();
                    CancelNewAlphaPbModelEdit();
                    CancelNewAlphaUModelEdit();
                    cancelNewPbBlankEdit();
                    cancelNewInitialPbModelEdit();
                    cancelNewPhysicalConstantsModelEdit();
                    cancelNewMineralStandardModelEdit();

                    if (getCurrentTabIndex() == 6) {
                        // we are leaving labdefaults
                        setSavedTracerModelName(myLabData.getDefaultLabTracer().getReduxLabDataElementName());
                        setSavedAlphaPbModelName(myLabData.getDefaultLabAlphaPbModel().getName());
                        setSavedAlphaUModelName(myLabData.getDefaultLabAlphaUModel().getName());
                        setSavedPbBlankName(myLabData.getDefaultLabPbBlank().getReduxLabDataElementName());
                        setSavedInitialPbModelName(myLabData.getDefaultLabInitialPbModel().getReduxLabDataElementName());
                        setSavedPhysicalConstantsModelName(
                                myLabData.getDefaultPhysicalConstantsModel().getNameAndVersion());
                        setSavedMineralStandardModelName(
                                myLabData.getDefaultTIMSMineralStandardModel().getReduxLabDataElementName());
                    }
                    setCurrentTabIndex(details_pane.getSelectedIndex());
                } else {
                    // stay put
                    details_pane.setSelectedIndex(getCurrentTabIndex());
                }
            } catch (BadLabDataException ex) {
            }
        }

    }//GEN-LAST:event_LabDataTabChange

    private void initialPbModels_menuMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_initialPbModels_menuMenuSelected
        details_pane.setSelectedComponent(initialPbModelsTab_panel);

        boolean amInEditMode = (getNewEmptyInitialPbModel() == null);

        editCopyOfCurrentInitialPbModel_menuItem.setEnabled(amInEditMode);

        localInitialPbModelImport_menuItem.setEnabled(InitialPbModelChooser.isEnabled());

        newInitialPbModelMode_menuItem.setEnabled(amInEditMode);
        cancelNewEditInitialPbModel_menuItem.setEnabled(!amInEditMode);

        saveInitialPbModelAsXML_menuItem.setEnabled(true);//

        AbstractRatiosDataModel selectedInitialPbModel = null;
        try {
            selectedInitialPbModel = myLabData.getAnInitialPbModel(
                    (String) InitialPbModelChooser.getSelectedItem());

            removeInitialPbModel_menuItem.setEnabled(amInEditMode
                    && (!myLabData.getDefaultLabInitialPbModel().equals(
                            selectedInitialPbModel))
                    && (!selectedInitialPbModel.isImmutable()));
        } catch (BadLabDataException badLabDataException) {
        }

    }//GEN-LAST:event_initialPbModels_menuMenuSelected

    private void editCopyOfCurrentPbBlank_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editCopyOfCurrentPbBlank_menuItemActionPerformed
        try {
            editCopyOfCurrentPbBlankModel();
        } catch (BadLabDataException ex) {
            try {
                cancelNewPbBlankEdit();
            } catch (BadLabDataException ex2) {
            }
        }
    }//GEN-LAST:event_editCopyOfCurrentPbBlank_menuItemActionPerformed

    private void editCopyOfCurrentTracer_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editCopyOfCurrentTracer_menuItemActionPerformed
        try {
            editCopyOfCurrentTracerModel();
        } catch (BadLabDataException ex) {
            try {
                cancelNewTracerModelEdit();
            } catch (BadLabDataException ex2) {
            }
        }
    }//GEN-LAST:event_editCopyOfCurrentTracer_menuItemActionPerformed

    private void removePbBlank_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removePbBlank_menuItemActionPerformed
        try {
            RemoveCurrentPbBlank();
        } catch (BadLabDataException ex) {
        }
    }//GEN-LAST:event_removePbBlank_menuItemActionPerformed

    private void localPbBlankImport_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_localPbBlankImport_menuItemActionPerformed
        try {
            if (checkIsSavedStatusOfPbBlankModelEdit()) {
                try {
                    importLocalPbBlankModel();
                } catch (BadLabDataException ex) {
                }
            }
        } catch (BadLabDataException ex) {
        }
    }//GEN-LAST:event_localPbBlankImport_menuItemActionPerformed

    private void savePbBlankAsXML_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_savePbBlankAsXML_menuItemActionPerformed
        try {
            exportPbBlankModelAsXML();
        } catch (BadLabDataException ex) {
        } catch (ETException etex) {
        }
    }//GEN-LAST:event_savePbBlankAsXML_menuItemActionPerformed

    private void PbBlanks_menuMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_PbBlanks_menuMenuSelected

        details_pane.setSelectedComponent(PbBlankICsTab_panel);

        boolean amInEditPbBlankMode = (newEmptyPbBlankModel == null);

        editCopyOfCurrentPbBlank_menuItem.setEnabled(amInEditPbBlankMode);
        localPbBlankImport_menuItem.setEnabled(PbBlankChooser.isEnabled());

        newPbBlankMode_menuItem.setEnabled(amInEditPbBlankMode);
        cancelNewEditBlank_menuItem.setEnabled(!amInEditPbBlankMode);

        savePbBlankAsXML_menuItem.setEnabled(true);

        // prevent removal of default PbBlankModel and built-in models
        AbstractRatiosDataModel selectedPbBlankModel = null;

        try {
            selectedPbBlankModel = myLabData.getAPbBlankModel(
                    (String) PbBlankChooser.getSelectedItem());

            removePbBlank_menuItem.setEnabled(amInEditPbBlankMode
                    && (!myLabData.getDefaultLabPbBlank().equals(
                            selectedPbBlankModel))
                    && (!selectedPbBlankModel.isImmutable()));
        } catch (BadLabDataException badLabDataException) {
        }

    }//GEN-LAST:event_PbBlanks_menuMenuSelected

    private void cancelNewEditBlank_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelNewEditBlank_menuItemActionPerformed
        try {
            cancelNewPbBlankEdit();
        } catch (BadLabDataException ex) {
        }
    }//GEN-LAST:event_cancelNewEditBlank_menuItemActionPerformed

    private void newPbBlankMode_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newPbBlankMode_menuItemActionPerformed
        try {
            editNewEmptyPbBlankModel();
        } catch (BadLabDataException ex) {
        }
    }//GEN-LAST:event_newPbBlankMode_menuItemActionPerformed

    private void closeDialog_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeDialog_menuItemActionPerformed
        try {
            exitLabData();
        } catch (BadLabDataException ex) {
        }
    }//GEN-LAST:event_closeDialog_menuItemActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        try {
            exitLabData();
        } catch (BadLabDataException ex) {
        }
    }//GEN-LAST:event_formWindowClosing

    private void saveTracerAsXML_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveTracerAsXML_menuItemActionPerformed
        try {
            exportTracerAsXML();
        } catch (BadLabDataException ex) {
        } catch (ETException etex) {
        }
    }//GEN-LAST:event_saveTracerAsXML_menuItemActionPerformed

    private void removeTracer_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeTracer_menuItemActionPerformed
        try {
            removeCurrentTracerModel();
        } catch (BadLabDataException ex) {
            ex.printStackTrace();
        }
    }//GEN-LAST:event_removeTracer_menuItemActionPerformed

    private void cancelNewTracerEdit_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelNewTracerEdit_menuItemActionPerformed
        try {

            cancelNewTracerModelEdit();
        } catch (BadLabDataException ex) {
            ex.printStackTrace();
        }

    }//GEN-LAST:event_cancelNewTracerEdit_menuItemActionPerformed

    private void tracers_menuMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_tracers_menuMenuSelected

        boolean amInEditTracerMode = (newEmptyTracerModel == null);
        editCopyOfCurrentTracer_menuItem.setEnabled(amInEditTracerMode);

        earthTimeTracerImport_menuItem.setEnabled(false);// oct 2014 since built-in .. this change predates oct 2014  tracerModelChooser.isEnabled());
        localTracerImport_menuItem.setEnabled(tracerModelChooser.isEnabled());

        newTracerMode_menuItem.setEnabled(amInEditTracerMode);
        cancelNewTracerEdit_menuItem.setEnabled(!amInEditTracerMode);

        saveTracerAsXML_menuItem.setEnabled(true);

        // prevent removal of default tracermodel and built-in models
        AbstractRatiosDataModel selectedTracerModel = null;
        try {
            selectedTracerModel = myLabData.getATracerModel(
                    (String) tracerModelChooser.getSelectedItem());

            removeTracer_menuItem.setEnabled(amInEditTracerMode
                    && (!myLabData.getDefaultLabTracer().equals(
                            selectedTracerModel))
                    && (!selectedTracerModel.isImmutable()));
        } catch (BadLabDataException badLabDataException) {
        }

    }//GEN-LAST:event_tracers_menuMenuSelected

    private void newTracerMode_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newTracerMode_menuItemActionPerformed
        try {
            editNewEmptyTracerModel();
        } catch (BadLabDataException ex) {
        }
    }//GEN-LAST:event_newTracerMode_menuItemActionPerformed

    private void earthTimeTracerImport_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_earthTimeTracerImport_menuItemActionPerformed
        try {
            if (checkIsSavedStatusOfTracerModelEdit()) {
                try {
                    importEarthTimeTracer();
                } catch (BadLabDataException ex) {
                }
            }
        } catch (BadLabDataException ex) {
        }
    }//GEN-LAST:event_earthTimeTracerImport_menuItemActionPerformed

    private void localTracerImport_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_localTracerImport_menuItemActionPerformed
        try {
            if (checkIsSavedStatusOfTracerModelEdit()) {
                try {
                    importLocalTracerModel();
                } catch (BadLabDataException ex) {
                }
            }
        } catch (BadLabDataException ex) {
        }
    }//GEN-LAST:event_localTracerImport_menuItemActionPerformed

    private void save_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_save_buttonActionPerformed
        try {
            exitLabData();
        } catch (BadLabDataException ex) {
        }
    }//GEN-LAST:event_save_buttonActionPerformed

    private void saveAlphaPbasLocalXML_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAlphaPbasLocalXML_menuItemActionPerformed
        try {
            ExportAlphaPbModelAsXML();
        } catch (BadLabDataException ex) {
        }
    }//GEN-LAST:event_saveAlphaPbasLocalXML_menuItemActionPerformed

    private void removeAlphaPb_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeAlphaPb_menuItemActionPerformed
        try {
            RemoveCurrentAlphaPbModel();
        } catch (BadLabDataException ex) {
        }
    }//GEN-LAST:event_removeAlphaPb_menuItemActionPerformed

    private void editNewAlphaPb_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editNewAlphaPb_menuItemActionPerformed
        try {
            EditNewEmptyAlphaPbModel();
        } catch (BadLabDataException ex) {
        }
    }//GEN-LAST:event_editNewAlphaPb_menuItemActionPerformed

    private void cancelEditAlphaPb_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelEditAlphaPb_menuItemActionPerformed
        try {
            CancelNewAlphaPbModelEdit();
        } catch (BadLabDataException ex) {
        }
    }//GEN-LAST:event_cancelEditAlphaPb_menuItemActionPerformed

    private void localPhysicalConstantsModelImport_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_localPhysicalConstantsModelImport_menuItemActionPerformed
        try {
            if (checkIsSavedStatusOfPhysicalConstantsModelEdit()) {
                try {
                    importLocalPhysicalConstantsModel();
                } catch (BadLabDataException ex) {
                }
            }
        } catch (BadLabDataException ex) {
            ex.printStackTrace();
        }
}//GEN-LAST:event_localPhysicalConstantsModelImport_menuItemActionPerformed

    private void savePhysicalConstantsModelAsXML_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_savePhysicalConstantsModelAsXML_menuItemActionPerformed
        try {
            exportPhysicalConstantsModelAsXML();
        } catch (BadLabDataException ex) {
        } catch (ETException etex) {
        }
}//GEN-LAST:event_savePhysicalConstantsModelAsXML_menuItemActionPerformed

    private void removePhysicalConstantsModel_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removePhysicalConstantsModel_menuItemActionPerformed
        try {
            removeCurrentPhysicalConstantsModel();
        } catch (BadLabDataException ex) {
            ex.printStackTrace();
        }
}//GEN-LAST:event_removePhysicalConstantsModel_menuItemActionPerformed

    private void editCopyOfCurrentPhysicalConstantsModel_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editCopyOfCurrentPhysicalConstantsModel_menuItemActionPerformed
        try {
            editCopyOfCurrentPhysicalConstantsModel();
        } catch (BadLabDataException ex) {
            try {
                cancelNewPhysicalConstantsModelEdit();
            } catch (BadLabDataException ex2) {
            }
        }
}//GEN-LAST:event_editCopyOfCurrentPhysicalConstantsModel_menuItemActionPerformed

    private void newPhysicalConstantsModel_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newPhysicalConstantsModel_menuItemActionPerformed
        try {
            editNewEmptyPhysicalConstantsModel();
        } catch (BadLabDataException ex) {
        }
}//GEN-LAST:event_newPhysicalConstantsModel_menuItemActionPerformed

    private void cancelNewEditPhysicalConstantsModel_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelNewEditPhysicalConstantsModel_menuItemActionPerformed
        try {
            cancelNewPhysicalConstantsModelEdit();
        } catch (BadLabDataException ex) {
        }
}//GEN-LAST:event_cancelNewEditPhysicalConstantsModel_menuItemActionPerformed

    private void physicalConstantsModels_menuMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_physicalConstantsModels_menuMenuSelected
        details_pane.setSelectedComponent(physicalConstantsModels_panel);

        boolean amInEditPhysicalConstantsModelMode = (newEmptyPhysicalConstantsModel == null);

        editCopyOfCurrentPhysicalConstantsModel_menuItem.setEnabled(amInEditPhysicalConstantsModelMode);
        localPhysicalConstantsModelImport_menuItem.setEnabled(physicalConstantsModel_Chooser.isEnabled());

        newPhysicalConstantsModel_menuItem.setEnabled(amInEditPhysicalConstantsModelMode);
        cancelNewEditPhysicalConstantsModel_menuItem.setEnabled(!amInEditPhysicalConstantsModelMode);

        savePhysicalConstantsModelAsXML_menuItem.setEnabled(true);

        // prevent removal of default PbBlankModel and built-in models
        AbstractRatiosDataModel selectedPhysicalConstantsModel = null;

        try {
            selectedPhysicalConstantsModel = myLabData.getAPhysicalConstantsModel(
                    (String) physicalConstantsModel_Chooser.getSelectedItem());

            removePhysicalConstantsModel_menuItem.setEnabled(amInEditPhysicalConstantsModelMode
                    && (!myLabData.getDefaultPhysicalConstantsModel().equals(
                            selectedPhysicalConstantsModel))
                    && (!selectedPhysicalConstantsModel.isImmutable()));
        } catch (BadLabDataException badLabDataException) {
        }
    }//GEN-LAST:event_physicalConstantsModels_menuMenuSelected

    private void localMineralStdModelImport_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_localMineralStdModelImport_menuItemActionPerformed
        try {
            if (checkIsSavedStatusOfMineralStandardModelEdit()) {
                try {
                    importLocalMineralStandardModel();
                } catch (BadLabDataException ex) {
                }
            }
        } catch (BadLabDataException ex) {
        }
}//GEN-LAST:event_localMineralStdModelImport_menuItemActionPerformed

    private void saveMineralStdModelAsXML_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMineralStdModelAsXML_menuItemActionPerformed
        try {
            exportMineralStandardModelAsXML();
        } catch (BadLabDataException ex) {
        } catch (ETException etex) {
        }
}//GEN-LAST:event_saveMineralStdModelAsXML_menuItemActionPerformed

    private void removeMineralStdModel_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeMineralStdModel_menuItemActionPerformed
        try {
            removeCurrentMineralStandardModel();
        } catch (BadLabDataException ex) {
            try {
                cancelNewMineralStandardModelEdit();
            } catch (BadLabDataException ex2) {
            }
        }
}//GEN-LAST:event_removeMineralStdModel_menuItemActionPerformed

    private void editCopyOfCurrentMineralStdModel_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editCopyOfCurrentMineralStdModel_menuItemActionPerformed
        try {
            editCopyOfCurrentMineralStandardModel();
        } catch (BadLabDataException ex) {
            try {
                cancelNewMineralStandardModelEdit();
            } catch (BadLabDataException ex2) {
            }
        }
}//GEN-LAST:event_editCopyOfCurrentMineralStdModel_menuItemActionPerformed

    private void newMineralStdModel_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newMineralStdModel_menuItemActionPerformed
        try {
            editNewEmptyMineralStandardModel();
        } catch (BadLabDataException ex) {
            ex.printStackTrace();
        }
}//GEN-LAST:event_newMineralStdModel_menuItemActionPerformed

    private void cancelNewEditMineralStdModel_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelNewEditMineralStdModel_menuItemActionPerformed
        try {
            cancelNewMineralStandardModelEdit();
        } catch (BadLabDataException ex) {
            ex.printStackTrace();
        }
}//GEN-LAST:event_cancelNewEditMineralStdModel_menuItemActionPerformed

    private void mineralStdModels_menuMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_mineralStdModels_menuMenuSelected
        details_pane.setSelectedComponent(mineralStandard_panel);

        boolean amInEditMode = (newEmptyMineralStandardModel == null);

        editCopyOfCurrentMineralStdModel_menuItem.setEnabled(amInEditMode);

        localMineralStdModelImport_menuItem.setEnabled(MineralStandardModelChooser.isEnabled());

        newMineralStdModel_menuItem.setEnabled(amInEditMode);
        cancelNewEditMineralStdModel_menuItem.setEnabled(!amInEditMode);

        saveMineralStdModelAsXML_menuItem.setEnabled(true);

        // prevent removal of default mineralStandardModel and built-in models
        AbstractRatiosDataModel selectedMineralStandardModel = null;
        try {
            selectedMineralStandardModel = myLabData.getAMineralStandardModel(
                    (String) MineralStandardModelChooser.getSelectedItem());

            removeMineralStdModel_menuItem.setEnabled(amInEditMode
                    && (!myLabData.getDefaultTIMSMineralStandardModel().equals(
                            selectedMineralStandardModel))
                    && (!selectedMineralStandardModel.isImmutable()));
        } catch (BadLabDataException badLabDataException) {
        }
}//GEN-LAST:event_mineralStdModels_menuMenuSelected

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        this.setAlwaysOnTop(false);
        String id = null;
        LabDataEditorDialog.setDefaultLookAndFeelDecorated(false);
        switch (currentTabIndex) {
            case 0:
                id = "LabData.Tracers";
                break;
            case 1:
                id = "LabData.Fractionation_Models";
                break;
            case 2:
                id = "LabData.U-Pb_Blank_Models";
                break;
            case 3:
                id = "LabData.Initial_Pb_Model";
                break;
            case 4:
                id = "LabData.Physical_constants_model";
                break;
            case 5:
                id = "LabData.Mineral_Std_Models";
                break;
            case 6:
                id = "LabData.Managing_LabData_Default";

        }
        //System.out.println(currentTabIndex);
        HelpBroker hb = JHelpAction.getHelpBroker();
        ((DefaultHelpBroker) hb).setActivationWindow(this);
        JHelpAction.setHelpBroker(hb);
        CSH.setHelpIDString(this, "Tracers");
        JHelpAction.showHelp(id);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void restoreOriginalDefaultValues_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_restoreOriginalDefaultValues_buttonActionPerformed
        myLabData.setDefaultMITValues();
        try {
            showSavedLabDefaults();
        } catch (BadLabDataException badLabDataException) {
        }
    }//GEN-LAST:event_restoreOriginalDefaultValues_buttonActionPerformed

    private void MineralStandardModelChooserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MineralStandardModelChooserActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_MineralStandardModelChooserActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel CalculatedModelInstructions_label;
    private javax.swing.JLabel CalculatedModelInstructions_label1;
    private javax.swing.JComboBox<String> InitialPbModelChooser;
    private javax.swing.JPanel LAICPMSLabDefaultsPane;
    private javax.swing.JComboBox<String> MineralStandardModelChooser;
    private javax.swing.JMenu MineralStdModels_menu;
    private javax.swing.JComboBox<String> PbBlankChooser;
    private javax.swing.JPanel PbBlankICsTab_panel;
    private javax.swing.JMenu PbBlanks_menu;
    private javax.swing.JComboBox<String> alphaPbChooser;
    private javax.swing.JLabel alphaPbName_label;
    private javax.swing.JTextField alphaPbName_text;
    private javax.swing.JLabel alphaPb_label;
    private javax.swing.JTextField alphaPb_text;
    private javax.swing.JLabel alphaPboneSigma_label;
    private javax.swing.JTextField alphaPboneSigma_text;
    private javax.swing.JComboBox<String> alphaUChooser;
    private javax.swing.JLabel alphaUName_label;
    private javax.swing.JTextField alphaUName_text;
    private javax.swing.JLabel alphaU_label;
    private javax.swing.JTextField alphaU_text;
    private javax.swing.JLabel alphaUoneSigma_label;
    private javax.swing.JTextField alphaUoneSigma_text;
    private javax.swing.JCheckBox annealedChemicallyAbraded_chkBox;
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JMenuItem cancelEditAlphaPb_menuItem;
    private javax.swing.JMenuItem cancelEditAlphaU_menuItem;
    private javax.swing.JMenuItem cancelNewEditBlank_menuItem;
    private javax.swing.JMenuItem cancelNewEditInitialPbModel_menuItem;
    private javax.swing.JMenuItem cancelNewEditMineralStdModel_menuItem;
    private javax.swing.JMenuItem cancelNewEditPhysicalConstantsModel_menuItem;
    private javax.swing.JMenuItem cancelNewTracerEdit_menuItem;
    private javax.swing.JCheckBox chemicallyPurifiedUPb_chkBox;
    private javax.swing.JLabel chooseAlphaPb_label;
    private javax.swing.JLabel chooseAlphaU_label;
    private javax.swing.JLabel chooseInitialPbModel_label;
    private javax.swing.JLabel chooseMineralStandardModel_label;
    private javax.swing.JLabel chooseMineral_label;
    private javax.swing.JLabel choosePbBlank_label;
    private javax.swing.JLabel choosePhysicalConstants_label;
    private javax.swing.JLabel chooseSettingType_label;
    private javax.swing.JLabel chooseTracer_label;
    private javax.swing.JMenuItem closeDialog_menuItem;
    private javax.swing.JLabel coefficientPb_label;
    private javax.swing.JLabel coefficientU_label;
    private javax.swing.JLabel correlationCoefficients_label;
    private javax.swing.JTextField correlationCoefficients_text;
    private javax.swing.JLabel countOfGrains_label;
    private javax.swing.JTextField countOfGrains_text;
    private javax.swing.JLabel default18O_16OOneSigma_label;
    private javax.swing.JTextField default18O_16OOneSigma_text;
    private javax.swing.JLabel default18O_16O_label;
    private javax.swing.JTextField default18O_16O_text;
    private javax.swing.JComboBox<String> defaultAlphaPbModel_Chooser;
    private javax.swing.JLabel defaultAlphaPbModel_label;
    private javax.swing.JComboBox<String> defaultAlphaUModel_Chooser;
    private javax.swing.JLabel defaultAlphaUModel_label;
    private javax.swing.JTextField defaultAr231_235sampleOneSigma_text;
    private javax.swing.JTextField defaultAr231_235sample_text;
    private javax.swing.JComboBox<String> defaultInitialPbModel_Chooser;
    private javax.swing.JLabel defaultInitialPbModel_label;
    private javax.swing.JComboBox<String> defaultLAICPMSPrimaryMineralStandardModel_Chooser;
    private javax.swing.JLabel defaultMineralStandardModel_label;
    private javax.swing.JComboBox<String> defaultPbBlankIC_Chooser;
    private javax.swing.JLabel defaultPbBlankIC_label;
    private javax.swing.JTextField defaultPbBlankMassOneSigma_text;
    private javax.swing.JLabel defaultPbBlankMass_label;
    private javax.swing.JTextField defaultPbBlankMass_text;
    private javax.swing.JLabel defaultPhsicalConstantsModel_label;
    private javax.swing.JComboBox<String> defaultPhysicalConstantsModel_Chooser;
    private javax.swing.JLabel defaultR238_235bOneSigma_label;
    private javax.swing.JTextField defaultR238_235bOneSigma_text;
    private javax.swing.JLabel defaultR238_235b_label;
    private javax.swing.JTextField defaultR238_235b_text;
    private javax.swing.JLabel defaultR238_235sOneSigma_label;
    private javax.swing.JTextField defaultR238_235sOneSigma_text;
    private javax.swing.JLabel defaultR238_235s_label;
    private javax.swing.JTextField defaultR238_235s_text;
    private javax.swing.JTextField defaultRTh_UmagmaOneSigma_text;
    private javax.swing.JTextField defaultRTh_Umagma_text;
    private javax.swing.JLabel defaultStaceyKramersCorrelationCoeff_label;
    private javax.swing.JTextField defaultStaceyKramersCorrelationCoeff_text;
    private javax.swing.JLabel defaultStaceyKramersRelativeUnct_label;
    private javax.swing.JTextField defaultStaceyKramersRelativeUnct_text;
    private javax.swing.JComboBox<String> defaultTIMSMineralStandardModel_Chooser;
    private javax.swing.JLabel defaultTracerMassOneSigma_label;
    private javax.swing.JTextField defaultTracerMassOneSigma_text;
    private javax.swing.JLabel defaultTracerMass_label;
    private javax.swing.JComboBox<String> defaultTracer_Chooser;
    private javax.swing.JLabel defaultTracer_label;
    private javax.swing.JTextField defaultUBlankMassOneSigma_text;
    private javax.swing.JLabel defaultUBlankMass_label;
    private javax.swing.JTextField defaultUBlankMass_text;
    private javax.swing.JLabel defaultar231_235sampleOneSigma_label;
    private javax.swing.JLabel defaultar231_235sample_label;
    private javax.swing.JLabel defaultrTh_UmagmaOneSigma_label;
    private javax.swing.JLabel defaultrTh_Umagma_label;
    private javax.swing.JTabbedPane details_pane;
    private javax.swing.JLabel developerNote;
    private javax.swing.JMenuItem earthTimeTracerImport_menuItem;
    private javax.swing.JMenuItem editCopyOfCurrentInitialPbModel_menuItem;
    private javax.swing.JMenuItem editCopyOfCurrentMineralStdModel_menuItem;
    private javax.swing.JMenuItem editCopyOfCurrentPbBlank_menuItem;
    private javax.swing.JMenuItem editCopyOfCurrentPhysicalConstantsModel_menuItem;
    private javax.swing.JMenuItem editCopyOfCurrentTracer_menuItem;
    private javax.swing.JMenuItem editNewAlphaPb_menuItem;
    private javax.swing.JMenuItem editNewAlphaU_menuItem;
    private javax.swing.JTextField estimatedAgeInMA_text;
    private javax.swing.JMenu fileMenu_menu;
    private javax.swing.JPanel fractionationModelsTab_panel;
    private javax.swing.JMenu fractionation_menu;
    private javax.swing.JPanel header_Panel;
    private javax.swing.JMenu helpMenu_menu;
    private javax.swing.JPanel initialPbModelsTab_panel;
    private javax.swing.JMenu initialPbModels_menu;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator10;
    private javax.swing.JSeparator jSeparator11;
    private javax.swing.JSeparator jSeparator12;
    private javax.swing.JSeparator jSeparator13;
    private javax.swing.JSeparator jSeparator14;
    private javax.swing.JSeparator jSeparator15;
    private javax.swing.JSeparator jSeparator16;
    private javax.swing.JSeparator jSeparator17;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JMenuBar labData_menuBar;
    private javax.swing.JLayeredPane labDefaultsCont_panel;
    private javax.swing.JPanel labDefaults_panel;
    private javax.swing.JLabel labName_label;
    private javax.swing.JTextField labName_text;
    private javax.swing.JLayeredPane labPane_layered;
    private javax.swing.JCheckBox leachedInHFAcid_chkBox;
    private javax.swing.JMenuItem localAlphaPbImportXML_menuItem;
    private javax.swing.JMenuItem localAlphaUImportXML_menuItem;
    private javax.swing.JMenuItem localInitialPbModelImport_menuItem;
    private javax.swing.JMenuItem localMineralStdModelImport_menuItem;
    private javax.swing.JMenuItem localPbBlankImport_menuItem;
    private javax.swing.JMenuItem localPhysicalConstantsModelImport_menuItem;
    private javax.swing.JMenuItem localTracerImport_menuItem;
    private javax.swing.JComboBox<String> mineralNameChooser;
    private javax.swing.JPanel mineralStandard_panel;
    private javax.swing.JLabel monoIsotopicPb_label;
    private javax.swing.JLabel monoIsotopicU_label;
    private javax.swing.JMenuItem newInitialPbModelMode_menuItem;
    private javax.swing.JMenuItem newMineralStdModel_menuItem;
    private javax.swing.JMenuItem newPbBlankMode_menuItem;
    private javax.swing.JMenuItem newPhysicalConstantsModel_menuItem;
    private javax.swing.JMenuItem newTracerMode_menuItem;
    private javax.swing.JLabel pbBlankMassOneSigma_label;
    private javax.swing.JLabel pbFractionation_label;
    private javax.swing.JComboBox<String> physicalConstantsModelForInitialPbModel_Chooser;
    private javax.swing.JComboBox<String> physicalConstantsModel_Chooser;
    private javax.swing.JMenu physicalConstantsModels_menu;
    private javax.swing.JPanel physicalConstantsModels_panel;
    private javax.swing.JCheckBox physicallyAbraded_chkBox;
    private javax.swing.JPanel rareEarthElementsModels_panel;
    private javax.swing.JButton refreshCalculations_JButton;
    private javax.swing.JLabel relativeUncertainty_label;
    private javax.swing.JTextField relativeUncertainty_text;
    private javax.swing.JMenuItem removeAlphaPb_menuItem;
    private javax.swing.JMenuItem removeAlphaU_menuItem;
    private javax.swing.JMenuItem removeInitialPbModel_menuItem;
    private javax.swing.JMenuItem removeMineralStdModel_menuItem;
    private javax.swing.JMenuItem removePbBlank_menuItem;
    private javax.swing.JMenuItem removePhysicalConstantsModel_menuItem;
    private javax.swing.JMenuItem removeTracer_menuItem;
    private javax.swing.JButton restoreOriginalDefaultValues_button;
    private javax.swing.JMenuItem saveAlphaPbasLocalXML_menuItem;
    private javax.swing.JMenuItem saveAlphaUasLocalXML_menuItem;
    private javax.swing.JMenuItem saveInitialPbModelAsXML_menuItem;
    private javax.swing.JMenuItem saveMineralStdModelAsXML_menuItem;
    private javax.swing.JMenuItem savePbBlankAsXML_menuItem;
    private javax.swing.JMenuItem savePhysicalConstantsModelAsXML_menuItem;
    private javax.swing.JMenuItem saveTracerAsXML_menuItem;
    private javax.swing.JButton save_button;
    private javax.swing.JComboBox<String> settingTypeChooser;
    private javax.swing.JPanel staceyKramerCalculator_Panel;
    private javax.swing.JComboBox<String> tracerModelChooser;
    private javax.swing.JPanel tracersTab_panel;
    private javax.swing.JMenu tracers_menu;
    private javax.swing.JLabel uBlankMassOneSigma_label;
    private javax.swing.JLabel uFractionation_label;
    // End of variables declaration//GEN-END:variables
}
