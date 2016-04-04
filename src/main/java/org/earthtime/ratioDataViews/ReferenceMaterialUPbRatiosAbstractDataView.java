/*
 * ReferenceMaterialUPbRatiosAbstractDataView.java
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
package org.earthtime.ratioDataViews;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.SwingConstants;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.valueModelPanelViews.AbstractValueModelsPanelView;
import org.earthtime.beans.ET_JButton;
import org.earthtime.dataDictionaries.DataDictionary;
import org.earthtime.dataDictionaries.MineralTypes;
import org.earthtime.exceptions.ETException;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataModels.initialPbModelsET.InitialPbModelET;
import org.earthtime.ratioDataModels.mineralStandardModels.MineralStandardUPbModel;
import org.earthtime.reduxLabData.ReduxLabData;

/**
 *
 * @author James F. Bowring
 */
public abstract class ReferenceMaterialUPbRatiosAbstractDataView extends AbstractRatiosDataView {

    private final JComboBox<AbstractRatiosDataModel> initialPbModelChooser;
    private JButton showInitialPbModelButton;
    /**
     *
     */
    protected JLabel apparentDatesLabel;
    private final JComboBox<String> ReferenceMaterialNameChooser;
    private final JComboBox<String> materialNameChooser;
    private final JCheckBox hasInitialPbCheckBox;
    protected AbstractValueModelsPanelView concentrationsPPMValueModelsPanelView;

    /**
     *
     *
     * @param ratiosDataModel
     * @param dataModel
     * @param parentDimension the value of parentDimension
     */
    public ReferenceMaterialUPbRatiosAbstractDataView(AbstractRatiosDataModel ratiosDataModel, Dimension parentDimension) {
        super(ratiosDataModel, parentDimension);

        initialPbModelChooser = new JComboBox<>();
        ReferenceMaterialNameChooser = new JComboBox<>();
        materialNameChooser = new JComboBox<>();
        hasInitialPbCheckBox = new JCheckBox("");
    }

    /**
     *
     * @param editable the value of editable
     */
    @Override
    protected void initView(boolean editable) {
        // dec 2014 expand for additional tabs in ReferenceMaterial models
        super.initView(editable);

        JLayeredPane apparentDatesPane = new JLayeredPane();

        String dates = ((MineralStandardUPbModel) dataModel)//
                .listFormattedApparentDatesHTML();
        apparentDatesLabel = new JLabel(dates);

        apparentDatesLabel.setBounds(100, 10, 300, 100);
        apparentDatesPane.add(apparentDatesLabel, DEFAULT_LAYER);
        setOpaqueWithLightGray(apparentDatesLabel);
        setOpaqueWithLightGray(apparentDatesPane);
        insertModelTab("Apparent Dates", apparentDatesPane, 2);

        JLayeredPane specificationsPane = new JLayeredPane();

        // set up ReferenceMaterial chooser
        JLabel mineralStandardLabel = new JLabel("Reference Material:");
        mineralStandardLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        mineralStandardLabel.setBounds(10, 10, 150, 25);
        specificationsPane.add(mineralStandardLabel, DEFAULT_LAYER);

        ReferenceMaterialNameChooser.setBounds(200, 10, 275, 25);
        ReferenceMaterialNameChooser.setFont(ReduxConstants.sansSerif_12_Bold);
        ReferenceMaterialNameChooser.removeAllItems();
        for (int i = 0; i < DataDictionary.getMineralStandardNames().length; i++) {
            ReferenceMaterialNameChooser.addItem(DataDictionary.getMineralStandardNames()[i]);
        }
        ReferenceMaterialNameChooser.setSelectedItem(((MineralStandardUPbModel) dataModel).getMineralStandardName());
        ReferenceMaterialNameChooser.setEnabled(editable);
        setOpaqueWithLightGray(ReferenceMaterialNameChooser);
        specificationsPane.add(ReferenceMaterialNameChooser, DEFAULT_LAYER);

        // set up Material chooser
        JLabel materialLabel = new JLabel("Material:");
        materialLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        materialLabel.setBounds(10, 40, 150, 25);
        specificationsPane.add(materialLabel, DEFAULT_LAYER);

        materialNameChooser.setBounds(200, 40, 275, 25);
        materialNameChooser.setFont(ReduxConstants.sansSerif_12_Bold);
        materialNameChooser.removeAllItems();
        for (int i = 0; i < MineralTypes.getNames().length; i++) {
            materialNameChooser.addItem(MineralTypes.getNames()[i]);
        }
        materialNameChooser.setSelectedItem(((MineralStandardUPbModel) dataModel).getMineralName());
        materialNameChooser.setEnabled(editable);
        setOpaqueWithLightGray(materialNameChooser);
        specificationsPane.add(materialNameChooser, DEFAULT_LAYER);

        // initial Pb
        JLabel hasInitialPbLabel = new JLabel("Has Initial Pb:");
        hasInitialPbLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        hasInitialPbLabel.setBounds(10, 70, 150, 25);
        specificationsPane.add(hasInitialPbLabel, DEFAULT_LAYER);

        hasInitialPbCheckBox.setHorizontalTextPosition(SwingConstants.LEADING);
        hasInitialPbCheckBox.setBounds(170, 70, 25, 25);
        hasInitialPbCheckBox.setSelected(((MineralStandardUPbModel) dataModel).hasInitialPb());
        hasInitialPbCheckBox.setEnabled(editable);
        setOpaqueWithLightGray(hasInitialPbCheckBox);
        hasInitialPbCheckBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                displayInitialPbModelChoosers(hasInitialPbCheckBox.isSelected());
            }
        });
        specificationsPane.add(hasInitialPbCheckBox);

        initialPbModelChooser.setBounds(200, 70, 275, 25);
        initialPbModelChooser.setFont(ReduxConstants.sansSerif_12_Bold);
        ArrayList<AbstractRatiosDataModel> initialPbModels = ReduxLabData.getInstance().getInitialPbModels();
        for (int i = (initialPbModels.size() > 1 ? 1 : 0); i < initialPbModels.size(); i++) {
            if (!initialPbModels.get(i).equals(InitialPbModelET.getStaceyKramersInstance())) {
                initialPbModelChooser.addItem(initialPbModels.get(i));
            }
        }
        initialPbModelChooser.setEnabled(editable);
        initialPbModelChooser.setSelectedItem(((MineralStandardUPbModel) dataModel).getInitialPbModelET());

        specificationsPane.add(initialPbModelChooser, DEFAULT_LAYER);
        setOpaqueWithLightGray(initialPbModelChooser);

        showInitialPbModelButton = new ET_JButton("Show Initial Pb Model");
        showInitialPbModelButton.setBounds(200, 100, 275, 25);
        showInitialPbModelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                AbstractRatiosDataModel selectedModel = //
                        ((AbstractRatiosDataModel) initialPbModelChooser.getSelectedItem());
                AbstractRatiosDataView modelView = //
                        new RatiosDataViewNotEditable(selectedModel, null, false);
                modelView.displayModelInFrame();
            }
        });
        specificationsPane.add(showInitialPbModelButton, DEFAULT_LAYER);

        displayInitialPbModelChoosers(((MineralStandardUPbModel) dataModel).hasInitialPb());

        setOpaqueWithLightGray(specificationsPane);
        insertModelTab("Specs", specificationsPane, 1);

        // dec 2014
        JLayeredPane concentrationsPane = new JLayeredPane();

        setOpaqueWithLightGray(apparentDatesPane);
        insertModelTab("Concentrations", concentrationsPPMValueModelsPanelView, 1);

        initDataTabsValueModelPanel(0, 0);
    }

    private void displayInitialPbModelChoosers(Boolean doDisplay) {

        initialPbModelChooser.setVisible(doDisplay);

        showInitialPbModelButton.setVisible(doDisplay);
    }

    /**
     *
     *
     * @param checkCovarianceValidity the value of checkCovarianceValidity
     * @throws org.earthtime.exceptions.ETException
     */
    @Override
    protected void saveEdits(boolean checkCovarianceValidity)
            throws ETException {
        super.saveEdits(checkCovarianceValidity);

        ((MineralStandardUPbModel) dataModel)//
                .setMineralStandardName((String) ReferenceMaterialNameChooser.getSelectedItem());
        ((MineralStandardUPbModel) dataModel)//
                .setMineralName((String) materialNameChooser.getSelectedItem());

        if (hasInitialPbCheckBox.isSelected()) {
            ((MineralStandardUPbModel) dataModel)//
                    .setInitialPbModelET((AbstractRatiosDataModel) initialPbModelChooser.getSelectedItem());
        } else {
            ((MineralStandardUPbModel) dataModel)//
                    .setInitialPbModelET(InitialPbModelET.getNoneInstance());
        }
    }
}
