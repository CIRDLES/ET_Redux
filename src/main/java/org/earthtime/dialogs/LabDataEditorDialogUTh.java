/*
 * LabDataEditorDialogUTh.java
 *
 *
 * Copyright 2006-2017 James F. Bowring and www.Earth-Time.org
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
package org.earthtime.dialogs;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.help.CSH;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.UPb_Redux.filters.XMLFileFilter;
import org.earthtime.UPb_Redux.utilities.BrowserControl;
import org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException;
import org.earthtime.beans.ET_JButton;
import org.earthtime.exceptions.ETException;
import org.earthtime.exceptions.ETWarningDialog;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataModels.detritalUraniumAndThoriumModels.DetritalUraniumAndThoriumModel;
import org.earthtime.ratioDataModels.mineralStandardModels.MineralStandardUPbModel;
import org.earthtime.ratioDataModels.physicalConstantsModels.PhysicalConstantsModel;
import org.earthtime.ratioDataViews.AbstractRatiosDataView;
import org.earthtime.ratioDataViews.PhysicalConstantsDataViewEditable;
import org.earthtime.ratioDataViews.PhysicalConstantsDataViewNotEditable;
import org.earthtime.ratioDataViews.RatiosDataViewEditable;
import org.earthtime.ratioDataViews.RatiosDataViewNotEditable;
import org.earthtime.ratioDataViews.ReferenceMaterialUPbRatiosDataViewEditable;
import org.earthtime.ratioDataViews.ReferenceMaterialUPbRatiosDataViewNotEditable;
import org.earthtime.reduxLabData.ReduxLabData;
import org.earthtime.utilities.FileHelper;

/**
 *
 * @author James F. Bowring
 */
public class LabDataEditorDialogUTh extends DialogEditor {

    // Fields
    //private boolean MAC_OS_X;
    private final int VERTICAL_OFFSET_MODEL_VIEW = 35;
    private ReduxLabData myLabData;
    private AbstractRatiosDataModel newEmptyDetritalUThModel;
    private AbstractRatiosDataModel newEmptyPhysicalConstantsModel;
    private AbstractRatiosDataModel newEmptyMineralStandardModel;
    private String savedDetritalUThModelName;
    private AbstractRatiosDataModel currentEditableMineralStandardModel;
    private AbstractRatiosDataModel currentEditableDetritalUThModel;
    private AbstractRatiosDataModel currentEditablePhysicalConstantsModel;
    private String savedPhysicalConstantsModelName;
    private String savedMineralStandardModelName;
    private int currentTabIndex;
    private AbstractRatiosDataView mineralStandardModelView;
    private AbstractRatiosDataView detritalUThModelView;
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
    public LabDataEditorDialogUTh(
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

        this.newEmptyDetritalUThModel = null;
        this.newEmptyPhysicalConstantsModel = null;
        this.newEmptyMineralStandardModel = null;

        this.savedDetritalUThModelName = myLabData.getDefaultDetritalUraniumAndThoriumModel().getReduxLabDataElementName();
        this.savedPhysicalConstantsModelName = myLabData.getDefaultPhysicalConstantsModel().getNameAndVersion();
        this.savedMineralStandardModelName = myLabData.getDefaultTIMSMineralStandardModel().getReduxLabDataElementName();

        // top panel
        labName_text.setDocument(new UnDoAbleDocument(labName_text, true));

        // Lab Default tab
        InitializeLabDefaultsTabTextBoxes(true);

        // set up tabs
        detritalUTh_Tab_panel.setBackground(ReduxConstants.dataModelGray);

        mineralStandard_panel.setBackground(ReduxConstants.dataModelGray);

        physicalConstantsModels_panel.setBackground(ReduxConstants.mySampleYellowColor);

        showSavedLabData(selectTab);

    }

    private void showSavedLabData(int selectTab)
            throws BadLabDataException {

        setDetailsPaneTab(selectTab);

        labName_text.setText(myLabData.getLabName());

        DetritalUThModelItemListener detritalUThModelActionListener = new DetritalUThModelItemListener();
        detritalUThChooser.addItemListener(detritalUThModelActionListener);
        initDetritalUThModelChooser();

        PhysicalConstantsItemListener physicalConstantsItemListener = new PhysicalConstantsItemListener();
        physicalConstantsModel_Chooser.addItemListener(physicalConstantsItemListener);
        initPhysicalConstantsModelChooser();

        MineralStandardModelItemListener MineralStandardModelItemListener
                = new MineralStandardModelItemListener();
        MineralStandardModelChooser.addItemListener(MineralStandardModelItemListener);
        initMineralStandardModelChooser();

        showSavedLabDefaults();

    }

    /**
     *
     * @param selectTab
     */
    public void setDetailsPaneTab(int selectTab) {
        details_pane.setSelectedIndex(selectTab);
        try {
            setCurrentTabIndex(selectTab);

        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
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

    // DetritalUThModel tab ****************************************************************************************
    /**
     *
     * @return
     */
    public String getSavedDetritalUThModelName() {
        return savedDetritalUThModelName;
    }

    /**
     *
     * @param savedDetritalUThModelName
     */
    public void setSavedDetritalUThModelName(String savedDetritalUThModelName) {
        this.savedDetritalUThModelName = savedDetritalUThModelName;
    }

    private void populateDetritalUThModelFields(AbstractRatiosDataModel detritalUThModel, boolean editable)
            throws BadLabDataException {
        detritalUThModel.initializeModel();

        Dimension detritalUThModelViewDimension = new Dimension(//
                detritalUTh_Tab_panel.getWidth(), detritalUTh_Tab_panel.getHeight() - VERTICAL_OFFSET_MODEL_VIEW);
        if (editable) {
            detritalUThModelView
                    = 
                    new RatiosDataViewEditable(detritalUThModel, detritalUThModelViewDimension, false);
        } else {
            detritalUThModelView
                    = 
                    new RatiosDataViewNotEditable(detritalUThModel, detritalUThModelViewDimension, false);
        }

        detritalUThModelView.setBackground(detritalUTh_Tab_panel.getBackground());
        detritalUThModelView.setBounds(detritalUTh_Tab_panel.getBounds());
        detritalUThModelView.setLocation(0, VERTICAL_OFFSET_MODEL_VIEW);
        for (Component c : detritalUTh_Tab_panel.getComponents()) {
            if (c instanceof AbstractRatiosDataView) {
                detritalUTh_Tab_panel.remove(c);
            }
        }
        detritalUTh_Tab_panel.add(detritalUThModelView);
        detritalUTh_Tab_panel.validate();
        detritalUThModelView.repaint();
    }

    private void initDetritalUThModelChooser()
            throws BadLabDataException {

        // set up DetritalUThModel chooser
        detritalUThChooser.removeAllItems();
        ArrayList<AbstractRatiosDataModel> detritalUThModels = myLabData.getDetritalUraniumAndThoriumModels();
        for (int i = (detritalUThModels.size() > 1 ? 1 : 0); i < detritalUThModels.size(); i++) {
            detritalUThChooser.addItem(detritalUThModels.get(i).getReduxLabDataElementName());
        }

        detritalUThChooser.setSelectedIndex(0);
        detritalUThChooser.setSelectedItem(savedDetritalUThModelName);

    }

    class DetritalUThModelItemListener implements ItemListener {
        // This method is called only if a new item has been selected.

        public void itemStateChanged(ItemEvent evt) {
            AbstractRatiosDataModel detritalUThModel = null;

            try {
                detritalUThModel = myLabData.getADetritalUraniumAndThoriumModel((String) evt.getItem());
            } catch (BadLabDataException ex) {
                new ETWarningDialog(ex).setVisible(true);
            }

            if (evt.getStateChange() == ItemEvent.SELECTED) {
                try {

                    // Item was just selected
                    populateDetritalUThModelFields(detritalUThModel, false);
                } catch (BadLabDataException ex) {
                    new ETWarningDialog(ex).setVisible(true);
                    Logger.getLogger(LabDataEditorDialogUTh.class.getName()).log(Level.SEVERE, null, ex);
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
    public AbstractRatiosDataModel getNewEmptyDetritalUThModel() {
        return newEmptyDetritalUThModel;
    }

    /**
     *
     * @param newEmptyDetritalUThModel
     */
    public void setNewEmptyDetritalUThModel(AbstractRatiosDataModel newEmptyDetritalUThModel) {
        this.newEmptyDetritalUThModel = newEmptyDetritalUThModel;
        if (detritalUThChooser.getSelectedIndex() >= 0) {
            savedDetritalUThModelName = (String) detritalUThChooser.getSelectedItem();
        } else {
            try {
                savedDetritalUThModelName = myLabData.getDefaultDetritalUraniumAndThoriumModel().getReduxLabDataElementName();
            } catch (BadLabDataException ex) {
                new ETWarningDialog(ex).setVisible(true);
            }
        }
    }

    private void editNewEmptyDetritalUThModel()
            throws BadLabDataException {
        newEmptyDetritalUThModel = DetritalUraniumAndThoriumModel.createNewInstance();
        detritalUThChooser.setEnabled(false);

        populateDetritalUThModelFields(newEmptyDetritalUThModel, true);
    }

    /**
     *
     * @param doAppendName the value of doAppendName
     * @throws BadLabDataException
     */
    private void editCopyOfCurrentDetritalUThModel(boolean doAppendName)
            throws BadLabDataException {

        try {
            currentEditableDetritalUThModel = myLabData.getADetritalUraniumAndThoriumModel(
                    (String) detritalUThChooser.getSelectedItem()).copyModel(doAppendName);
            currentEditableDetritalUThModel.setImmutable(false);

            detritalUThChooser.setEnabled(false);

            newEmptyDetritalUThModel = currentEditableDetritalUThModel;
            populateDetritalUThModelFields(currentEditableDetritalUThModel, true);

        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
    }

    private synchronized boolean checkIsSavedStatusOfDetritalUThModelEdit()
            throws BadLabDataException {
        boolean retval = true;

        if (newEmptyDetritalUThModel != null) {

            int response = JOptionPane.showConfirmDialog(this,
                    new String[]{"You have not saved the new Detrital UTh Model ... Proceed anyway?"},
                    "ET Redux Warning",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            retval = (response == JOptionPane.YES_OPTION);
        }
        if (retval) {
            cancelNewDetritalUThModelEdit();
        }

        return retval;
    }

    private void cancelNewDetritalUThModelEdit()
            throws BadLabDataException {
        newEmptyDetritalUThModel = null;
        populateDetritalUThModelFields(myLabData.getADetritalUraniumAndThoriumModel(
                (String) detritalUThChooser.getSelectedItem()), false);
        detritalUThChooser.setEnabled(true);
    }

    private synchronized void registerDetritalUThModel(final AbstractRatiosDataModel detritalUThModel)
            throws HeadlessException, BadLabDataException {
        myLabData.registerDetritalUraniumAndThoriumModel(detritalUThModel, true);
        initDetritalUThModelChooser();
        detritalUThChooser.setSelectedItem(detritalUThModel.getReduxLabDataElementName());
    }

    private void RemoveCurrentDetritalUThModel()
            throws BadLabDataException {
        // see also menu item is truned off
        if (!(((String) detritalUThChooser.getSelectedItem()).equalsIgnoreCase("<none>")
                || ((String) detritalUThChooser.getSelectedItem()).equalsIgnoreCase(ReduxConstants.NONE))) {
            String tempName = (String) detritalUThChooser.getSelectedItem();
            detritalUThChooser.removeAllItems();
            myLabData.removeADetritalUraniumAndThoriumModel(tempName);
        }

        initDetritalUThModelChooser();
    }

    private boolean exportDetritalUThModelAsXML()
            throws BadLabDataException, ETException {
        File selectedFile = null;

        // Nov 2015
        AbstractRatiosDataModel selectedModel
                =
                myLabData.getADetritalUraniumAndThoriumModel(
                        (String) detritalUThChooser.getSelectedItem());

        setAlwaysOnTop(false);

        String dialogTitle = "Save this Detrital UTh Model as xml: *.xml";
        final String fileExtension = ".xml";
        String detritalUThModelFileName
                = selectedModel.getReduxLabDataElementName() + fileExtension;
        FileFilter nonMacFileFilter = new XMLFileFilter();

        selectedFile = FileHelper.AllPlatformSaveAs(new Frame(), dialogTitle, null, fileExtension, detritalUThModelFileName, nonMacFileFilter);

        if (selectedFile != null) {
            try {
                // export
                selectedModel.serializeXMLObject(selectedFile.getCanonicalPath());
            } catch (IOException ex) {
            }
        }

        setAlwaysOnTop(true);
        return (selectedFile != null);
    }

    private synchronized void importLocalDetritalUThModel()
            throws BadLabDataException {

        String dialogTitle = "Select a ET_Redux Detrital UTh Model File to Open: *.xml";
        final String fileExtension = ".xml";
        FileFilter nonMacFileFilter = new XMLFileFilter();

        setAlwaysOnTop(false);

        File returnFile
                = FileHelper.AllPlatformGetFile(dialogTitle, null, fileExtension, nonMacFileFilter, false, new JFrame())[0];

        if (returnFile != null) {
            readAndRegisterDetritalUThModel(returnFile);
        }

        // wait to make visible so messages appear
        setAlwaysOnTop(true);
    }

    private synchronized void readAndRegisterDetritalUThModel(File returnFile) {
        AbstractRatiosDataModel tempDetritalUThModel = DetritalUraniumAndThoriumModel.getNoneInstance();

        try {
            tempDetritalUThModel = tempDetritalUThModel.readXMLObject(returnFile.getCanonicalPath(), true);
        } catch (IOException | ETException | BadOrMissingXMLSchemaException | com.thoughtworks.xstream.mapper.CannotResolveClassException ex) {
            if (ex instanceof ETException) {
                new ETWarningDialog((ETException) ex).setVisible(true);
            }

            tempDetritalUThModel = null;
        }

        // Nov 2015 type checking
        boolean proceed = (tempDetritalUThModel != null);
        if (proceed) {
            proceed = proceed && (tempDetritalUThModel instanceof DetritalUraniumAndThoriumModel);
        }

        if (proceed) {
            try {
                registerDetritalUThModel(tempDetritalUThModel);
            } catch (BadLabDataException ex) {
                new ETWarningDialog(ex).setVisible(true);
            }
        } else {
            JOptionPane.showConfirmDialog(
                    null,
                    new String[]{"This Detrital UT hModel could not be imported...please confirm it conforms to the schema."},
                    "ET Redux Warning",
                    JOptionPane.WARNING_MESSAGE);
        }

    }

    private void saveAndRegisterCurrentEditOfDetritalUThModel()
            throws BadLabDataException, ETException {
        setAlwaysOnTop(false);
        detritalUThModelView.saveAndUpdateModelView(true);
        myLabData.registerDetritalUraniumAndThoriumModel(newEmptyDetritalUThModel, true);
        savedDetritalUThModelName = newEmptyDetritalUThModel.getNameAndVersion();
        initDetritalUThModelChooser();
        detritalUThChooser.setEnabled(true);
        newEmptyDetritalUThModel = null;
        setAlwaysOnTop(true);
    }

    // PhysicalConstantsModel tab **********************************************
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
            physicalConstantsModelView
                    = new PhysicalConstantsDataViewEditable(physicalConstantsModel, physicalConstantsModelViewDimension, false);
        } else {
            physicalConstantsModelView
                    = new PhysicalConstantsDataViewNotEditable(physicalConstantsModel, physicalConstantsModelViewDimension, false);
        }

        physicalConstantsModelView.setBackground(physicalConstantsModels_panel.getBackground());
        physicalConstantsModelView.setBounds(physicalConstantsModels_panel.getBounds());
        physicalConstantsModelView.setLocation(0, VERTICAL_OFFSET_MODEL_VIEW);
        for (Component c : physicalConstantsModels_panel.getComponents()) {
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
                new ETWarningDialog(ex).setVisible(true);
            }

            if (evt.getStateChange() == ItemEvent.SELECTED) {
                try {
                    // Item was just selected
                    populatePhysicalConstantsFields(physicalConstantsModel, false);

                } catch (BadLabDataException ex) {
                    new ETWarningDialog(ex).setVisible(true);
                    Logger.getLogger(LabDataEditorDialogUTh.class.getName()).log(Level.SEVERE, null, ex);
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
            savedPhysicalConstantsModelName = myLabData.getDefaultPhysicalConstantsModel().getReduxLabDataElementName();
        }

    }

    private void editNewEmptyPhysicalConstantsModel()
            throws BadLabDataException {
        newEmptyPhysicalConstantsModel = PhysicalConstantsModel.createNewInstance();
        physicalConstantsModel_Chooser.setEnabled(false);
        populatePhysicalConstantsFields(newEmptyPhysicalConstantsModel, true);

    }

    /**
     *
     * @param doAppendName the value of doAppendName
     * @throws BadLabDataException
     */
    private void editCopyOfCurrentPhysicalConstantsModel(boolean doAppendName)
            throws BadLabDataException {
        try {
            currentEditablePhysicalConstantsModel = myLabData.getAPhysicalConstantsModel(
                    (String) physicalConstantsModel_Chooser.getSelectedItem()).copyModel(doAppendName);
            currentEditablePhysicalConstantsModel.setImmutable(false);

            physicalConstantsModel_Chooser.setEnabled(false);

            newEmptyPhysicalConstantsModel = currentEditablePhysicalConstantsModel;
            populatePhysicalConstantsFields(currentEditablePhysicalConstantsModel, true);

        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
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
        populatePhysicalConstantsFields(myLabData.getAPhysicalConstantsModel(
                (String) physicalConstantsModel_Chooser.getSelectedItem()), false);
        physicalConstantsModel_Chooser.setEnabled(true);
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

        // Nov 2015
        AbstractRatiosDataModel selectedModel
                =//
                myLabData.getAPhysicalConstantsModel(
                        (String) physicalConstantsModel_Chooser.getSelectedItem());

        setAlwaysOnTop(false);

        String dialogTitle = "Save this Physical Constants Model as xml: *.xml";
        final String fileExtension = ".xml";
        String fractionFileName
                = selectedModel.getReduxLabDataElementName() + fileExtension;
        FileFilter nonMacFileFilter = new XMLFileFilter();

        selectedFile = FileHelper.AllPlatformSaveAs(
                new Frame(), dialogTitle, null, fileExtension, fractionFileName, nonMacFileFilter);

        if (selectedFile != null) {
            try {
                // export
                selectedModel.serializeXMLObject(selectedFile.getCanonicalPath());
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
        AbstractRatiosDataModel physicalConstantsModel = PhysicalConstantsModel.getNoneInstance();

        try {
            physicalConstantsModel = physicalConstantsModel.readXMLObject(returnFile.getCanonicalPath(), true);
        } catch (IOException | ETException | BadOrMissingXMLSchemaException | com.thoughtworks.xstream.mapper.CannotResolveClassException ex) {
            if (ex instanceof ETException) {
                new ETWarningDialog((ETException) ex).setVisible(true);
            }

            physicalConstantsModel = null;
        }

        // Nov 2015 type checking
        boolean proceed = (physicalConstantsModel != null);
        if (proceed) {
            proceed = proceed && (physicalConstantsModel instanceof PhysicalConstantsModel);
        }

        if (proceed) {
            try {
                registerPhysicalConstantsModel(physicalConstantsModel);
            } catch (BadLabDataException ex) {
                new ETWarningDialog(ex).setVisible(true);
            }
        } else {
            JOptionPane.showConfirmDialog(
                    null,
                    new String[]{"This Physical Constants Model could not be imported...please confirm it conforms to the schema."},
                    "ET Redux Warning",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void saveAndRegisterCurrentEditOfPhysicalConstantsModel()
            throws BadLabDataException, ETException {
        setAlwaysOnTop(false);
        physicalConstantsModelView.saveAndUpdateModelView(true);
        myLabData.registerPhysicalConstantsModel(newEmptyPhysicalConstantsModel, true);
        savedPhysicalConstantsModelName = newEmptyPhysicalConstantsModel.getNameAndVersion();
        initPhysicalConstantsModelChooser();
        physicalConstantsModel_Chooser.setEnabled(true);
        newEmptyPhysicalConstantsModel = null;
        setAlwaysOnTop(true);
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
            mineralStandardModelView
                    = new ReferenceMaterialUPbRatiosDataViewEditable(mineralStandardModel, mineralStandard_panel.getSize(), false);
        } else {
            mineralStandardModelView
                    = new ReferenceMaterialUPbRatiosDataViewNotEditable(mineralStandardModel, mineralStandard_panel.getSize(), false);
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
                        new ETWarningDialog(ex).setVisible(true);
                        Logger.getLogger(LabDataEditorDialogUTh.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } else if (evt.getStateChange() == ItemEvent.DESELECTED) {
                    // Item is no longer selected
                }
            } catch (BadLabDataException ex) {
                new ETWarningDialog(ex).setVisible(true);
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
                new ETWarningDialog(ex).setVisible(true);
            }
        }
    }

    private void editNewEmptyMineralStandardModel()
            throws BadLabDataException {
        newEmptyMineralStandardModel = MineralStandardUPbModel.createNewInstance();
        MineralStandardModelChooser.setEnabled(false);
        populateMineralStandardModelFields(newEmptyMineralStandardModel, true);
    }

    /**
     *
     * @param doAppendName the value of doAppendName
     * @throws BadLabDataException
     */
    private void editCopyOfCurrentMineralStandardModel(boolean doAppendName)
            throws BadLabDataException {
        try {
            currentEditableMineralStandardModel = myLabData.getAMineralStandardModel(
                    (String) MineralStandardModelChooser.getSelectedItem()).copyModel(doAppendName);
            currentEditableMineralStandardModel.setImmutable(false);

            MineralStandardModelChooser.setEnabled(false);

            newEmptyMineralStandardModel = currentEditableMineralStandardModel;
            populateMineralStandardModelFields(currentEditableMineralStandardModel, true);

        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
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
        populateMineralStandardModelFields(myLabData.getAMineralStandardModel(
                (String) MineralStandardModelChooser.getSelectedItem()), false);
        MineralStandardModelChooser.setEnabled(true);
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

        // Nov 2015
        AbstractRatiosDataModel selectedModel
                =//
                myLabData.getAMineralStandardModel(
                        (String) MineralStandardModelChooser.getSelectedItem());

        setAlwaysOnTop(false);

        String dialogTitle = "Save this MineralStandardModel as xml: *.xml";
        final String fileExtension = ".xml";
        String fileName
                = selectedModel.getReduxLabDataElementName() + fileExtension;
        FileFilter nonMacFileFilter = new XMLFileFilter();

        selectedFile = FileHelper.AllPlatformSaveAs(
                new Frame(), dialogTitle, null, fileExtension, fileName, nonMacFileFilter);

        if (selectedFile != null) {
            try {
                // export
                selectedModel.serializeXMLObject(selectedFile.getCanonicalPath());
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
            mineralStandardModel = mineralStandardModel.readXMLObject(returnFile.getCanonicalPath(), true);
        } catch (IOException | ETException | BadOrMissingXMLSchemaException | com.thoughtworks.xstream.mapper.CannotResolveClassException ex) {
            if (ex instanceof ETException) {
                new ETWarningDialog((ETException) ex).setVisible(true);
            }

            mineralStandardModel = null;
        }

        // Nov 2015 type checking
        boolean proceed = (mineralStandardModel != null);
        if (proceed) {
            proceed = proceed && (mineralStandardModel instanceof MineralStandardUPbModel);
        }

        if (proceed) {
            try {
                registerMineralStandardModel(mineralStandardModel);

            } catch (BadLabDataException ex) {
                new ETWarningDialog(ex).setVisible(true);
            }
        } else {
            JOptionPane.showConfirmDialog(
                    null,
                    new String[]{"This Mineral Standard could not be imported...please confirm it conforms to the schema."},
                    "ET Redux Warning",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    private void saveAndRegisterCurrentEditOfMineralStandardModel()
            throws BadLabDataException, ETException {
        setAlwaysOnTop(false);
        mineralStandardModelView.saveAndUpdateModelView(true);
        myLabData.registerMineralStandardModel(newEmptyMineralStandardModel, true);
        savedMineralStandardModelName = newEmptyMineralStandardModel.getNameAndVersion();
        initMineralStandardModelChooser();
        MineralStandardModelChooser.setEnabled(true);
        newEmptyMineralStandardModel = null;
        setAlwaysOnTop(true);
    }

    // end Mineral Standards tab *********************************************
    // Lab Defaults ************************************************************
    private void InitializeLabDefaultsTabTextBoxes(boolean editable) {

    }

    private synchronized void showSavedLabDefaults()
            throws BadLabDataException {

        // set up defaultTIMSPMineralStandardModel_Chooser
        defaultTIMSMineralStandardModel_Chooser.removeAllItems();
        ArrayList<AbstractRatiosDataModel> mineralStandardModels = myLabData.getMineralStandardModels();
        for (int i = (mineralStandardModels.size() > 1 ? 1 : 0); i < mineralStandardModels.size(); i++) {
            defaultTIMSMineralStandardModel_Chooser.addItem(mineralStandardModels.get(i).getReduxLabDataElementName());
        }
        defaultTIMSMineralStandardModel_Chooser.setSelectedIndex(0);
        defaultTIMSMineralStandardModel_Chooser.setSelectedItem(myLabData.getDefaultTIMSMineralStandardModel().getReduxLabDataElementName());

        // set up DefaultDetritalUThModel chooser
        defaultDetritalUThModel_Chooser.removeAllItems();
        ArrayList<AbstractRatiosDataModel> detritalUThModels = myLabData.getDetritalUraniumAndThoriumModels();
        for (int i = (detritalUThModels.size() > 1 ? 1 : 0); i < detritalUThModels.size(); i++) {
            defaultDetritalUThModel_Chooser.addItem(detritalUThModels.get(i).getReduxLabDataElementName());
        }
        defaultDetritalUThModel_Chooser.setSelectedItem(myLabData.getDefaultDetritalUraniumAndThoriumModel().getReduxLabDataElementName());


        // set up DefaultPhysicalConstantsModel chooser
        defaultPhysicalConstantsModel_Chooser.removeAllItems();
        ArrayList<AbstractRatiosDataModel> pmodels = myLabData.getPhysicalConstantsModels();
        for (int i = (pmodels.size() > 1 ? 1 : 0); i < pmodels.size(); i++) {
            defaultPhysicalConstantsModel_Chooser.addItem(pmodels.get(i).getNameAndVersion());
        }
        defaultPhysicalConstantsModel_Chooser.setSelectedItem(myLabData.getDefaultPhysicalConstantsModel().getNameAndVersion());

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

        // default MineralStandardModel
        try {
            myLabData.setDefaultTIMSMineralStandardModel(
                    myLabData.getAMineralStandardModel(defaultTIMSMineralStandardModel_Chooser.getSelectedItem().toString()));
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }

        // default LabDetritalUThModel
        try {
            myLabData.setDefaultDetritalUraniumAndThoriumModel(
                    myLabData.getADetritalUraniumAndThoriumModel(defaultDetritalUThModel_Chooser.getSelectedItem().toString()));
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }


    }

    /**
     *
     */
    public void setSize() {
        setSize(1113, 770);
    }

    private void exitLabData()
            throws BadLabDataException {
        //SaveLabData();
        if (checkIsSavedStatusOfDetritalUThModelEdit()
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
        detritalUTh_menu.setVisible(false);
        physicalConstantsModels_menu.setVisible(false);
        MineralStdModels_menu.setVisible(false);

        // refresh tabs
        switch (currentTabIndex) {
            case 0:
                physicalConstantsModels_menu.setVisible(true);
                CSH.setHelpIDString(this, "LabData.Physical Constants models");
                initPhysicalConstantsModelChooser();
                break;
            case 1:
                detritalUTh_menu.setVisible(true);
                CSH.setHelpIDString(this, "LabData.Detrital UTh Models");
                initDetritalUThModelChooser();
                break;
            case 2:
                MineralStdModels_menu.setVisible(true);
                CSH.setHelpIDString(this, "LabData.Mineral Standard Models");
                initMineralStandardModelChooser();
                break;
            case 3:
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
        physicalConstantsModels_panel = new javax.swing.JPanel();
        choosePhysicalConstants_label = new javax.swing.JLabel();
        physicalConstantsModel_Chooser = new javax.swing.JComboBox<>();
        detritalUTh_Tab_panel = new javax.swing.JPanel();
        chooseDetritalUTh_label = new javax.swing.JLabel();
        detritalUThChooser = new javax.swing.JComboBox<>();
        mineralStandard_panel = new javax.swing.JPanel();
        chooseMineralStandardModel_label = new javax.swing.JLabel();
        MineralStandardModelChooser = new javax.swing.JComboBox<>();
        labDefaults_panel = new javax.swing.JPanel();
        defaultDetritalUThModel_label = new javax.swing.JLabel();
        defaultDetritalUThModel_Chooser = new javax.swing.JComboBox<>();
        jPanel2 = new javax.swing.JPanel();
        defaultPhsicalConstantsModel_label = new javax.swing.JLabel();
        defaultPhysicalConstantsModel_Chooser = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        defaultMineralStandardModel_label = new javax.swing.JLabel();
        defaultTIMSMineralStandardModel_Chooser = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        header_Panel = new javax.swing.JPanel();
        labName_label = new javax.swing.JLabel();
        labName_text = new javax.swing.JTextField();
        buttonsPanel = new javax.swing.JPanel();
        save_button =  new ET_JButton("OK");
        labData_menuBar = new javax.swing.JMenuBar();
        fileMenu_menu = new javax.swing.JMenu();
        closeDialog_menuItem = new javax.swing.JMenuItem();
        physicalConstantsModels_menu = new javax.swing.JMenu();
        ImportPhysicalConstantsModelXML_menuItem = new javax.swing.JMenuItem();
        savePhysicalConstantsModelAsXML_menuItem = new javax.swing.JMenuItem();
        jSeparator14 = new javax.swing.JSeparator();
        removePhysicalConstantsModel_menuItem = new javax.swing.JMenuItem();
        jSeparator15 = new javax.swing.JSeparator();
        editCurrentLocalPhysicalConstantsModel_menuItem = new javax.swing.JMenuItem();
        editCopyOfCurrentPhysicalConstantsModel_menuItem = new javax.swing.JMenuItem();
        newPhysicalConstantsModel_menuItem = new javax.swing.JMenuItem();
        cancelNewEditPhysicalConstantsModel_menuItem = new javax.swing.JMenuItem();
        jSeparator20 = new javax.swing.JPopupMenu.Separator();
        saveAndRegisterCurrentEditOfPhysicalConstantsModel_menuItem = new javax.swing.JMenuItem();
        detritalUTh_menu = new javax.swing.JMenu();
        importDetritalUThXML_menuItem = new javax.swing.JMenuItem();
        saveDetritalUThModelAsXML_menuItem = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        removeDetritalUThModel_menuItem = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JSeparator();
        editCurrentLocalDetritalUThModel_menuItem = new javax.swing.JMenuItem();
        editCopyOfCurrentDetritalUThModel_menuItem = new javax.swing.JMenuItem();
        newDetritalUThModelMode_menuItem = new javax.swing.JMenuItem();
        cancelNewEditDetritalUThModel_menuItem = new javax.swing.JMenuItem();
        jSeparator22 = new javax.swing.JPopupMenu.Separator();
        saveAndRegisterCurrentEditOfDetritalUThModel_menuItem = new javax.swing.JMenuItem();
        MineralStdModels_menu = new javax.swing.JMenu();
        importLocalMineralStdModelXML_menuItem = new javax.swing.JMenuItem();
        saveMineralStdModelAsXML_menuItem = new javax.swing.JMenuItem();
        jSeparator16 = new javax.swing.JSeparator();
        removeMineralStdModel_menuItem = new javax.swing.JMenuItem();
        jSeparator17 = new javax.swing.JSeparator();
        editCurrentLocalMineralStandardModel_menuItem = new javax.swing.JMenuItem();
        editCopyOfCurrentMineralStdModel_menuItem = new javax.swing.JMenuItem();
        newMineralStdModel_menuItem = new javax.swing.JMenuItem();
        cancelNewEditMineralStdModel_menuItem = new javax.swing.JMenuItem();
        jSeparator18 = new javax.swing.JPopupMenu.Separator();
        saveAndRegisterCurrentEditOfMineralStandardModel_menuItem = new javax.swing.JMenuItem();
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
                .add(physicalConstantsModel_Chooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 461, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );
        physicalConstantsModels_panelLayout.setVerticalGroup(
            physicalConstantsModels_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(physicalConstantsModels_panelLayout.createSequentialGroup()
                .add(6, 6, 6)
                .add(choosePhysicalConstants_label))
            .add(physicalConstantsModel_Chooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
        );

        details_pane.addTab("Physical Const. Models", physicalConstantsModels_panel);

        chooseDetritalUTh_label.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chooseDetritalUTh_label.setText("  Choose Detrital UTh Model:");

        org.jdesktop.layout.GroupLayout detritalUTh_Tab_panelLayout = new org.jdesktop.layout.GroupLayout(detritalUTh_Tab_panel);
        detritalUTh_Tab_panel.setLayout(detritalUTh_Tab_panelLayout);
        detritalUTh_Tab_panelLayout.setHorizontalGroup(
            detritalUTh_Tab_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(detritalUTh_Tab_panelLayout.createSequentialGroup()
                .addContainerGap()
                .add(chooseDetritalUTh_label)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                .add(detritalUThChooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 461, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        detritalUTh_Tab_panelLayout.setVerticalGroup(
            detritalUTh_Tab_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(detritalUTh_Tab_panelLayout.createSequentialGroup()
                .add(6, 6, 6)
                .add(detritalUTh_Tab_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(chooseDetritalUTh_label)
                    .add(detritalUThChooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(584, 584, 584))
        );

        details_pane.addTab("Detrital UTh Models", detritalUTh_Tab_panel);

        chooseMineralStandardModel_label.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        chooseMineralStandardModel_label.setText("  Choose Reference Material Model:");

        org.jdesktop.layout.GroupLayout mineralStandard_panelLayout = new org.jdesktop.layout.GroupLayout(mineralStandard_panel);
        mineralStandard_panel.setLayout(mineralStandard_panelLayout);
        mineralStandard_panelLayout.setHorizontalGroup(
            mineralStandard_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mineralStandard_panelLayout.createSequentialGroup()
                .add(18, 18, 18)
                .add(chooseMineralStandardModel_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 212, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(MineralStandardModelChooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 632, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(212, 212, 212))
        );
        mineralStandard_panelLayout.setVerticalGroup(
            mineralStandard_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(mineralStandard_panelLayout.createSequentialGroup()
                .add(6, 6, 6)
                .add(mineralStandard_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(chooseMineralStandardModel_label)
                    .add(MineralStandardModelChooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(584, 584, 584))
        );

        details_pane.addTab("Ref Material Models", mineralStandard_panel);

        labDefaults_panel.setBackground(new java.awt.Color(212, 231, 232));

        defaultDetritalUThModel_label.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        defaultDetritalUThModel_label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        defaultDetritalUThModel_label.setText("Set the default DetritalUThModel:");

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
                .add(defaultPhysicalConstantsModel_Chooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 424, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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
        defaultMineralStandardModel_label.setText("Set the default Reference Material Model for each new ALIQUOT:");

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(defaultMineralStandardModel_label)
                .add(18, 18, 18)
                .add(defaultTIMSMineralStandardModel_Chooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 422, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(244, Short.MAX_VALUE))
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

        org.jdesktop.layout.GroupLayout labDefaults_panelLayout = new org.jdesktop.layout.GroupLayout(labDefaults_panel);
        labDefaults_panel.setLayout(labDefaults_panelLayout);
        labDefaults_panelLayout.setHorizontalGroup(
            labDefaults_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jPanel3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(labDefaults_panelLayout.createSequentialGroup()
                .addContainerGap()
                .add(defaultDetritalUThModel_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 224, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(defaultDetritalUThModel_Chooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 250, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(379, 379, 379)
                .add(jLabel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 103, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(0, 0, Short.MAX_VALUE))
        );
        labDefaults_panelLayout.setVerticalGroup(
            labDefaults_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(labDefaults_panelLayout.createSequentialGroup()
                .add(jPanel2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(6, 6, 6)
                .add(jPanel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(labDefaults_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(labDefaults_panelLayout.createSequentialGroup()
                        .add(234, 234, 234)
                        .add(labDefaults_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(defaultDetritalUThModel_Chooser, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(labDefaults_panelLayout.createSequentialGroup()
                                .add(1, 1, 1)
                                .add(jLabel3))))
                    .add(labDefaults_panelLayout.createSequentialGroup()
                        .add(239, 239, 239)
                        .add(defaultDetritalUThModel_label)))
                .add(288, 288, 288))
        );

        details_pane.addTab("Lab Defaults UTh", labDefaults_panel);

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
        buttonsPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

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
        buttonsPanel.add(save_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(8, 6, 1097, 25));

        labPane_layered.setLayer(details_pane, javax.swing.JLayeredPane.DEFAULT_LAYER);
        labPane_layered.setLayer(header_Panel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        labPane_layered.setLayer(buttonsPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);

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
                .add(labPane_layeredLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(header_Panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(labPane_layeredLayout.createSequentialGroup()
                        .add(24, 24, 24)
                        .add(details_pane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 660, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(buttonsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 35, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        fileMenu_menu.setText("File");

        closeDialog_menuItem.setText("Close LabData");
        closeDialog_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeDialog_menuItemActionPerformed(evt);
            }
        });
        fileMenu_menu.add(closeDialog_menuItem);

        labData_menuBar.add(fileMenu_menu);

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

        ImportPhysicalConstantsModelXML_menuItem.setText("Import Physical Constants Model from XML file");
        ImportPhysicalConstantsModelXML_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ImportPhysicalConstantsModelXML_menuItemActionPerformed(evt);
            }
        });
        physicalConstantsModels_menu.add(ImportPhysicalConstantsModelXML_menuItem);

        savePhysicalConstantsModelAsXML_menuItem.setText("Export Physical Constants Model as XML file");
        savePhysicalConstantsModelAsXML_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                savePhysicalConstantsModelAsXML_menuItemActionPerformed(evt);
            }
        });
        physicalConstantsModels_menu.add(savePhysicalConstantsModelAsXML_menuItem);
        physicalConstantsModels_menu.add(jSeparator14);

        removePhysicalConstantsModel_menuItem.setText("Remove current Physical Constants Model from Lab Data (for editable models only)");
        removePhysicalConstantsModel_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removePhysicalConstantsModel_menuItemActionPerformed(evt);
            }
        });
        physicalConstantsModels_menu.add(removePhysicalConstantsModel_menuItem);
        physicalConstantsModels_menu.add(jSeparator15);

        editCurrentLocalPhysicalConstantsModel_menuItem.setText("Edit current Physical Constants Model (for editable models only)");
        editCurrentLocalPhysicalConstantsModel_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editCurrentLocalPhysicalConstantsModel_menuItemActionPerformed(evt);
            }
        });
        physicalConstantsModels_menu.add(editCurrentLocalPhysicalConstantsModel_menuItem);

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
        physicalConstantsModels_menu.add(jSeparator20);

        saveAndRegisterCurrentEditOfPhysicalConstantsModel_menuItem.setText("Save and Register Current Edit of Physical Constants Model");
        saveAndRegisterCurrentEditOfPhysicalConstantsModel_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAndRegisterCurrentEditOfPhysicalConstantsModel_menuItemActionPerformed(evt);
            }
        });
        physicalConstantsModels_menu.add(saveAndRegisterCurrentEditOfPhysicalConstantsModel_menuItem);

        labData_menuBar.add(physicalConstantsModels_menu);

        detritalUTh_menu.setText("Detrital UTh Models");
        detritalUTh_menu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                detritalUTh_menuMenuSelected(evt);
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
        });

        importDetritalUThXML_menuItem.setText("Import Detrital UTh Model from XML file");
        importDetritalUThXML_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importDetritalUThXML_menuItemActionPerformed(evt);
            }
        });
        detritalUTh_menu.add(importDetritalUThXML_menuItem);

        saveDetritalUThModelAsXML_menuItem.setText("Export current Detrital UTh Model as XML file");
        saveDetritalUThModelAsXML_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveDetritalUThModelAsXML_menuItemActionPerformed(evt);
            }
        });
        detritalUTh_menu.add(saveDetritalUThModelAsXML_menuItem);
        detritalUTh_menu.add(jSeparator3);

        removeDetritalUThModel_menuItem.setText("Remove current Detrital UTh Model from Lab Data (for editable models only)");
        removeDetritalUThModel_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeDetritalUThModel_menuItemActionPerformed(evt);
            }
        });
        detritalUTh_menu.add(removeDetritalUThModel_menuItem);
        detritalUTh_menu.add(jSeparator4);

        editCurrentLocalDetritalUThModel_menuItem.setText("Edit current Detrital UTh Model (for editable models only)");
        editCurrentLocalDetritalUThModel_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editCurrentLocalDetritalUThModel_menuItemActionPerformed(evt);
            }
        });
        detritalUTh_menu.add(editCurrentLocalDetritalUThModel_menuItem);

        editCopyOfCurrentDetritalUThModel_menuItem.setText("Edit copy of current Detrital UTh Model");
        editCopyOfCurrentDetritalUThModel_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editCopyOfCurrentDetritalUThModel_menuItemActionPerformed(evt);
            }
        });
        detritalUTh_menu.add(editCopyOfCurrentDetritalUThModel_menuItem);

        newDetritalUThModelMode_menuItem.setText("Edit new empty Detrital UTh Model");
        newDetritalUThModelMode_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newDetritalUThModelMode_menuItemActionPerformed(evt);
            }
        });
        detritalUTh_menu.add(newDetritalUThModelMode_menuItem);

        cancelNewEditDetritalUThModel_menuItem.setText("Cancel Edit of Detrital UTh Model");
        cancelNewEditDetritalUThModel_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelNewEditDetritalUThModel_menuItemActionPerformed(evt);
            }
        });
        detritalUTh_menu.add(cancelNewEditDetritalUThModel_menuItem);
        detritalUTh_menu.add(jSeparator22);

        saveAndRegisterCurrentEditOfDetritalUThModel_menuItem.setText("Save and Register Current Edit of Detrital UTh Model");
        saveAndRegisterCurrentEditOfDetritalUThModel_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAndRegisterCurrentEditOfDetritalUThModel_menuItemActionPerformed(evt);
            }
        });
        detritalUTh_menu.add(saveAndRegisterCurrentEditOfDetritalUThModel_menuItem);

        labData_menuBar.add(detritalUTh_menu);
        detritalUTh_menu.getAccessibleContext().setAccessibleName("");

        MineralStdModels_menu.setText("Reference Material Models");
        MineralStdModels_menu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                mineralStdModels_menuMenuSelected(evt);
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
        });

        importLocalMineralStdModelXML_menuItem.setText("Import Reference Material Model from XML file");
        importLocalMineralStdModelXML_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importLocalMineralStdModelXML_menuItemActionPerformed(evt);
            }
        });
        MineralStdModels_menu.add(importLocalMineralStdModelXML_menuItem);

        saveMineralStdModelAsXML_menuItem.setText("Export Reference Material Model as XML file");
        saveMineralStdModelAsXML_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveMineralStdModelAsXML_menuItemActionPerformed(evt);
            }
        });
        MineralStdModels_menu.add(saveMineralStdModelAsXML_menuItem);
        MineralStdModels_menu.add(jSeparator16);

        removeMineralStdModel_menuItem.setText("Remove current Reference Material Model (for editable models only)");
        removeMineralStdModel_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeMineralStdModel_menuItemActionPerformed(evt);
            }
        });
        MineralStdModels_menu.add(removeMineralStdModel_menuItem);
        MineralStdModels_menu.add(jSeparator17);

        editCurrentLocalMineralStandardModel_menuItem.setText("Edit current Reference Material Model (for editable models only)");
        editCurrentLocalMineralStandardModel_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editCurrentLocalMineralStandardModel_menuItemActionPerformed(evt);
            }
        });
        MineralStdModels_menu.add(editCurrentLocalMineralStandardModel_menuItem);

        editCopyOfCurrentMineralStdModel_menuItem.setText("Edit Copy of current Reference Material Model");
        editCopyOfCurrentMineralStdModel_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editCopyOfCurrentMineralStdModel_menuItemActionPerformed(evt);
            }
        });
        MineralStdModels_menu.add(editCopyOfCurrentMineralStdModel_menuItem);

        newMineralStdModel_menuItem.setText("Edit New empty Reference Material Model");
        newMineralStdModel_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newMineralStdModel_menuItemActionPerformed(evt);
            }
        });
        MineralStdModels_menu.add(newMineralStdModel_menuItem);

        cancelNewEditMineralStdModel_menuItem.setText("Cancel Edit of  Reference Material Model");
        cancelNewEditMineralStdModel_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelNewEditMineralStdModel_menuItemActionPerformed(evt);
            }
        });
        MineralStdModels_menu.add(cancelNewEditMineralStdModel_menuItem);
        MineralStdModels_menu.add(jSeparator18);

        saveAndRegisterCurrentEditOfMineralStandardModel_menuItem.setText("Save and Register Current Edit of Reference Material Model");
        saveAndRegisterCurrentEditOfMineralStandardModel_menuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAndRegisterCurrentEditOfMineralStandardModel_menuItemActionPerformed(evt);
            }
        });
        MineralStdModels_menu.add(saveAndRegisterCurrentEditOfMineralStandardModel_menuItem);

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

    private void LabDataTabChange(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_LabDataTabChange
        // save off work when changing tabs
        // determine if changing tab or just clicking panel
        if (getCurrentTabIndex() == details_pane.getSelectedIndex()) {
            // we have clicked the same tab so nothing happens
        } else {
            try {
                // we are changing tabs so we need to detect edit mode and warn
                if (checkIsSavedStatusOfDetritalUThModelEdit()
                        && checkIsSavedStatusOfPhysicalConstantsModelEdit()
                        && checkIsSavedStatusOfMineralStandardModelEdit()
                        && CheckIsSavedStatusOfDefaultsEdit()) {
                    saveLabDefaults();
                    cancelNewDetritalUThModelEdit();
                    cancelNewPhysicalConstantsModelEdit();
                    cancelNewMineralStandardModelEdit();

                    if (getCurrentTabIndex() == 6) {
                        // we are leaving labdefaults
                        setSavedDetritalUThModelName(myLabData.getDefaultDetritalUraniumAndThoriumModel().getReduxLabDataElementName());
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
                new ETWarningDialog(ex).setVisible(true);
            }
        }

    }//GEN-LAST:event_LabDataTabChange

    private void editCopyOfCurrentDetritalUThModel_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editCopyOfCurrentDetritalUThModel_menuItemActionPerformed
        try {
            editCopyOfCurrentDetritalUThModel(true);
        } catch (BadLabDataException ex) {
            try {
                cancelNewDetritalUThModelEdit();
            } catch (BadLabDataException ex2) {
            }
        }
    }//GEN-LAST:event_editCopyOfCurrentDetritalUThModel_menuItemActionPerformed

    private void removeDetritalUThModel_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeDetritalUThModel_menuItemActionPerformed
        try {
            RemoveCurrentDetritalUThModel();
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
    }//GEN-LAST:event_removeDetritalUThModel_menuItemActionPerformed

    private void importDetritalUThXML_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importDetritalUThXML_menuItemActionPerformed
        try {
            if (checkIsSavedStatusOfDetritalUThModelEdit()) {
                try {
                    importLocalDetritalUThModel();
                } catch (BadLabDataException ex) {
                    new ETWarningDialog(ex).setVisible(true);
                }
            }
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
    }//GEN-LAST:event_importDetritalUThXML_menuItemActionPerformed

    private void saveDetritalUThModelAsXML_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveDetritalUThModelAsXML_menuItemActionPerformed
        try {
            exportDetritalUThModelAsXML();
        } catch (ETException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
    }//GEN-LAST:event_saveDetritalUThModelAsXML_menuItemActionPerformed

    private void detritalUTh_menuMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_detritalUTh_menuMenuSelected

        details_pane.setSelectedComponent(detritalUTh_Tab_panel);

        boolean amInEditDetritalUThModelMode = (newEmptyDetritalUThModel == null);

        editCopyOfCurrentDetritalUThModel_menuItem.setEnabled(amInEditDetritalUThModelMode);
        importDetritalUThXML_menuItem.setEnabled(detritalUThChooser.isEnabled());

        newDetritalUThModelMode_menuItem.setEnabled(amInEditDetritalUThModelMode);
        cancelNewEditDetritalUThModel_menuItem.setEnabled(!amInEditDetritalUThModelMode);

        saveDetritalUThModelAsXML_menuItem.setEnabled(amInEditDetritalUThModelMode);
        saveAndRegisterCurrentEditOfDetritalUThModel_menuItem.setEnabled(!amInEditDetritalUThModelMode);

        // prevent removal of default DetritalUThModel and built-in models
        AbstractRatiosDataModel selectedDetritalUThModel = null;

        try {
            selectedDetritalUThModel = myLabData.getADetritalUraniumAndThoriumModel(
                    (String) detritalUThChooser.getSelectedItem());

            removeDetritalUThModel_menuItem.setEnabled(amInEditDetritalUThModelMode
                    && (!myLabData.getDefaultDetritalUraniumAndThoriumModel().equals(selectedDetritalUThModel))
                    && (!selectedDetritalUThModel.isImmutable()));

            editCurrentLocalDetritalUThModel_menuItem.setEnabled(amInEditDetritalUThModelMode
                    && (!selectedDetritalUThModel.isImmutable()));
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
    }//GEN-LAST:event_detritalUTh_menuMenuSelected

    private void cancelNewEditDetritalUThModel_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelNewEditDetritalUThModel_menuItemActionPerformed
        try {
            cancelNewDetritalUThModelEdit();
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
    }//GEN-LAST:event_cancelNewEditDetritalUThModel_menuItemActionPerformed

    private void newDetritalUThModelMode_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newDetritalUThModelMode_menuItemActionPerformed
        try {
            editNewEmptyDetritalUThModel();
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
    }//GEN-LAST:event_newDetritalUThModelMode_menuItemActionPerformed

    private void closeDialog_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeDialog_menuItemActionPerformed
        try {
            exitLabData();
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
    }//GEN-LAST:event_closeDialog_menuItemActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        try {
            exitLabData();
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
    }//GEN-LAST:event_formWindowClosing

    private void save_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_save_buttonActionPerformed
        try {
            exitLabData();
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
    }//GEN-LAST:event_save_buttonActionPerformed

    private void ImportPhysicalConstantsModelXML_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ImportPhysicalConstantsModelXML_menuItemActionPerformed
        try {
            if (checkIsSavedStatusOfPhysicalConstantsModelEdit()) {
                try {
                    importLocalPhysicalConstantsModel();
                } catch (BadLabDataException ex) {
                    new ETWarningDialog(ex).setVisible(true);
                }
            }
        } catch (ETException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
}//GEN-LAST:event_ImportPhysicalConstantsModelXML_menuItemActionPerformed

    private void savePhysicalConstantsModelAsXML_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_savePhysicalConstantsModelAsXML_menuItemActionPerformed
        try {
            exportPhysicalConstantsModelAsXML();
        } catch (ETException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
}//GEN-LAST:event_savePhysicalConstantsModelAsXML_menuItemActionPerformed

    private void removePhysicalConstantsModel_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removePhysicalConstantsModel_menuItemActionPerformed
        try {
            removeCurrentPhysicalConstantsModel();
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
}//GEN-LAST:event_removePhysicalConstantsModel_menuItemActionPerformed

    private void editCopyOfCurrentPhysicalConstantsModel_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editCopyOfCurrentPhysicalConstantsModel_menuItemActionPerformed
        try {
            editCopyOfCurrentPhysicalConstantsModel(true);
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
}//GEN-LAST:event_editCopyOfCurrentPhysicalConstantsModel_menuItemActionPerformed

    private void newPhysicalConstantsModel_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newPhysicalConstantsModel_menuItemActionPerformed
        try {
            editNewEmptyPhysicalConstantsModel();
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
}//GEN-LAST:event_newPhysicalConstantsModel_menuItemActionPerformed

    private void cancelNewEditPhysicalConstantsModel_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelNewEditPhysicalConstantsModel_menuItemActionPerformed
        try {
            cancelNewPhysicalConstantsModelEdit();
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
}//GEN-LAST:event_cancelNewEditPhysicalConstantsModel_menuItemActionPerformed

    private void physicalConstantsModels_menuMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_physicalConstantsModels_menuMenuSelected
        details_pane.setSelectedComponent(physicalConstantsModels_panel);

        boolean amInEditPhysicalConstantsModelMode = (newEmptyPhysicalConstantsModel == null);

        editCopyOfCurrentPhysicalConstantsModel_menuItem.setEnabled(amInEditPhysicalConstantsModelMode);
        ImportPhysicalConstantsModelXML_menuItem.setEnabled(physicalConstantsModel_Chooser.isEnabled());

        newPhysicalConstantsModel_menuItem.setEnabled(amInEditPhysicalConstantsModelMode);
        cancelNewEditPhysicalConstantsModel_menuItem.setEnabled(!amInEditPhysicalConstantsModelMode);

        savePhysicalConstantsModelAsXML_menuItem.setEnabled(amInEditPhysicalConstantsModelMode);
        saveAndRegisterCurrentEditOfPhysicalConstantsModel_menuItem.setEnabled(!amInEditPhysicalConstantsModelMode);

        // prevent removal of default PhysicalConstantsModel and built-in models
        AbstractRatiosDataModel selectedPhysicalConstantsModel = null;

        try {
            selectedPhysicalConstantsModel = myLabData.getAPhysicalConstantsModel(
                    (String) physicalConstantsModel_Chooser.getSelectedItem());

            removePhysicalConstantsModel_menuItem.setEnabled(amInEditPhysicalConstantsModelMode
                    && (!myLabData.getDefaultPhysicalConstantsModel().equals(
                            selectedPhysicalConstantsModel))
                    && (!selectedPhysicalConstantsModel.isImmutable()));

            editCurrentLocalPhysicalConstantsModel_menuItem.setEnabled(amInEditPhysicalConstantsModelMode
                    && (!selectedPhysicalConstantsModel.isImmutable()));

        } catch (BadLabDataException badLabDataException) {
        }
    }//GEN-LAST:event_physicalConstantsModels_menuMenuSelected

    private void importLocalMineralStdModelXML_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importLocalMineralStdModelXML_menuItemActionPerformed
        try {
            if (checkIsSavedStatusOfMineralStandardModelEdit()) {
                try {
                    importLocalMineralStandardModel();
                } catch (BadLabDataException ex) {
                }
            }
        } catch (BadLabDataException ex) {
        }
}//GEN-LAST:event_importLocalMineralStdModelXML_menuItemActionPerformed

    private void saveMineralStdModelAsXML_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMineralStdModelAsXML_menuItemActionPerformed
        try {
            exportMineralStandardModelAsXML();
        } catch (ETException ex) {
            new ETWarningDialog(ex).setVisible(true);
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
            editCopyOfCurrentMineralStandardModel(true);
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
            new ETWarningDialog(ex).setVisible(true);
        }
}//GEN-LAST:event_newMineralStdModel_menuItemActionPerformed

    private void cancelNewEditMineralStdModel_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelNewEditMineralStdModel_menuItemActionPerformed
        try {
            cancelNewMineralStandardModelEdit();
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
}//GEN-LAST:event_cancelNewEditMineralStdModel_menuItemActionPerformed

    private void mineralStdModels_menuMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_mineralStdModels_menuMenuSelected
        details_pane.setSelectedComponent(mineralStandard_panel);

        boolean amInEditMode = (newEmptyMineralStandardModel == null);

        editCopyOfCurrentMineralStdModel_menuItem.setEnabled(amInEditMode);

        importLocalMineralStdModelXML_menuItem.setEnabled(MineralStandardModelChooser.isEnabled());

        newMineralStdModel_menuItem.setEnabled(amInEditMode);
        cancelNewEditMineralStdModel_menuItem.setEnabled(!amInEditMode);

        saveMineralStdModelAsXML_menuItem.setEnabled(amInEditMode);
        saveAndRegisterCurrentEditOfMineralStandardModel_menuItem.setEnabled(!amInEditMode);

        // prevent removal of default mineralStandardModel and built-in models
        AbstractRatiosDataModel selectedMineralStandardModel = null;
        try {
            selectedMineralStandardModel = myLabData.getAMineralStandardModel(
                    (String) MineralStandardModelChooser.getSelectedItem());

            removeMineralStdModel_menuItem.setEnabled(amInEditMode
                    && (!myLabData.getDefaultTIMSMineralStandardModel().equals(
                            selectedMineralStandardModel))
                    && (!selectedMineralStandardModel.isImmutable()));

            editCurrentLocalMineralStandardModel_menuItem.setEnabled(amInEditMode
                    && (!selectedMineralStandardModel.isImmutable()));

        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
}//GEN-LAST:event_mineralStdModels_menuMenuSelected

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed


        BrowserControl.displayURL("http://cirdles.org/projects/et_redux/#welcome");
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void editCurrentLocalMineralStandardModel_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editCurrentLocalMineralStandardModel_menuItemActionPerformed
        try {
            editCopyOfCurrentMineralStandardModel(false);
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
    }//GEN-LAST:event_editCurrentLocalMineralStandardModel_menuItemActionPerformed

    private void saveAndRegisterCurrentEditOfMineralStandardModel_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAndRegisterCurrentEditOfMineralStandardModel_menuItemActionPerformed
        try {
            saveAndRegisterCurrentEditOfMineralStandardModel();
        } catch (ETException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
    }//GEN-LAST:event_saveAndRegisterCurrentEditOfMineralStandardModel_menuItemActionPerformed

    private void editCurrentLocalPhysicalConstantsModel_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editCurrentLocalPhysicalConstantsModel_menuItemActionPerformed
        try {
            editCopyOfCurrentPhysicalConstantsModel(false);
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
    }//GEN-LAST:event_editCurrentLocalPhysicalConstantsModel_menuItemActionPerformed

    private void saveAndRegisterCurrentEditOfPhysicalConstantsModel_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAndRegisterCurrentEditOfPhysicalConstantsModel_menuItemActionPerformed
        try {
            saveAndRegisterCurrentEditOfPhysicalConstantsModel();
        } catch (ETException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
    }//GEN-LAST:event_saveAndRegisterCurrentEditOfPhysicalConstantsModel_menuItemActionPerformed

    private void editCurrentLocalDetritalUThModel_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editCurrentLocalDetritalUThModel_menuItemActionPerformed
        try {
            editCopyOfCurrentDetritalUThModel(false);
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
    }//GEN-LAST:event_editCurrentLocalDetritalUThModel_menuItemActionPerformed

    private void saveAndRegisterCurrentEditOfDetritalUThModel_menuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAndRegisterCurrentEditOfDetritalUThModel_menuItemActionPerformed
        try {
            saveAndRegisterCurrentEditOfDetritalUThModel();
        } catch (ETException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
    }//GEN-LAST:event_saveAndRegisterCurrentEditOfDetritalUThModel_menuItemActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem ImportPhysicalConstantsModelXML_menuItem;
    private javax.swing.JComboBox<String> MineralStandardModelChooser;
    private javax.swing.JMenu MineralStdModels_menu;
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JMenuItem cancelNewEditDetritalUThModel_menuItem;
    private javax.swing.JMenuItem cancelNewEditMineralStdModel_menuItem;
    private javax.swing.JMenuItem cancelNewEditPhysicalConstantsModel_menuItem;
    private javax.swing.JLabel chooseDetritalUTh_label;
    private javax.swing.JLabel chooseMineralStandardModel_label;
    private javax.swing.JLabel choosePhysicalConstants_label;
    private javax.swing.JMenuItem closeDialog_menuItem;
    private javax.swing.JComboBox<String> defaultDetritalUThModel_Chooser;
    private javax.swing.JLabel defaultDetritalUThModel_label;
    private javax.swing.JLabel defaultMineralStandardModel_label;
    private javax.swing.JLabel defaultPhsicalConstantsModel_label;
    private javax.swing.JComboBox<String> defaultPhysicalConstantsModel_Chooser;
    private javax.swing.JComboBox<String> defaultTIMSMineralStandardModel_Chooser;
    private javax.swing.JTabbedPane details_pane;
    private javax.swing.JComboBox<String> detritalUThChooser;
    private javax.swing.JPanel detritalUTh_Tab_panel;
    private javax.swing.JMenu detritalUTh_menu;
    private javax.swing.JMenuItem editCopyOfCurrentDetritalUThModel_menuItem;
    private javax.swing.JMenuItem editCopyOfCurrentMineralStdModel_menuItem;
    private javax.swing.JMenuItem editCopyOfCurrentPhysicalConstantsModel_menuItem;
    private javax.swing.JMenuItem editCurrentLocalDetritalUThModel_menuItem;
    private javax.swing.JMenuItem editCurrentLocalMineralStandardModel_menuItem;
    private javax.swing.JMenuItem editCurrentLocalPhysicalConstantsModel_menuItem;
    private javax.swing.JMenu fileMenu_menu;
    private javax.swing.JPanel header_Panel;
    private javax.swing.JMenu helpMenu_menu;
    private javax.swing.JMenuItem importDetritalUThXML_menuItem;
    private javax.swing.JMenuItem importLocalMineralStdModelXML_menuItem;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSeparator jSeparator14;
    private javax.swing.JSeparator jSeparator15;
    private javax.swing.JSeparator jSeparator16;
    private javax.swing.JSeparator jSeparator17;
    private javax.swing.JPopupMenu.Separator jSeparator18;
    private javax.swing.JPopupMenu.Separator jSeparator20;
    private javax.swing.JPopupMenu.Separator jSeparator22;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JMenuBar labData_menuBar;
    private javax.swing.JPanel labDefaults_panel;
    private javax.swing.JLabel labName_label;
    private javax.swing.JTextField labName_text;
    private javax.swing.JLayeredPane labPane_layered;
    private javax.swing.JPanel mineralStandard_panel;
    private javax.swing.JMenuItem newDetritalUThModelMode_menuItem;
    private javax.swing.JMenuItem newMineralStdModel_menuItem;
    private javax.swing.JMenuItem newPhysicalConstantsModel_menuItem;
    private javax.swing.JComboBox<String> physicalConstantsModel_Chooser;
    private javax.swing.JMenu physicalConstantsModels_menu;
    private javax.swing.JPanel physicalConstantsModels_panel;
    private javax.swing.JMenuItem removeDetritalUThModel_menuItem;
    private javax.swing.JMenuItem removeMineralStdModel_menuItem;
    private javax.swing.JMenuItem removePhysicalConstantsModel_menuItem;
    private javax.swing.JMenuItem saveAndRegisterCurrentEditOfDetritalUThModel_menuItem;
    private javax.swing.JMenuItem saveAndRegisterCurrentEditOfMineralStandardModel_menuItem;
    private javax.swing.JMenuItem saveAndRegisterCurrentEditOfPhysicalConstantsModel_menuItem;
    private javax.swing.JMenuItem saveDetritalUThModelAsXML_menuItem;
    private javax.swing.JMenuItem saveMineralStdModelAsXML_menuItem;
    private javax.swing.JMenuItem savePhysicalConstantsModelAsXML_menuItem;
    private javax.swing.JButton save_button;
    // End of variables declaration//GEN-END:variables
}
