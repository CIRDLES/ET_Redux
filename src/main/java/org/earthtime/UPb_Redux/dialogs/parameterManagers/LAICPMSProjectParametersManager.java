/*
 * LAICPMSProjectParametersManager.java
 *
 * Copyright 2006-2016 James F. Bowring and www.Earth-Time.org
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
package org.earthtime.UPb_Redux.dialogs.parameterManagers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.earthtime.ETReduxFrame;
import org.earthtime.Tripoli.dataModels.aquisitionTypeDataModels.IsotopeMappingModel;
import org.earthtime.Tripoli.dataModels.collectorModels.FaradayCollectorModel;
import org.earthtime.Tripoli.dataModels.collectorModels.FaradayCollectorModel.ResistorEnum;
import org.earthtime.Tripoli.dataModels.collectorModels.IonCounterCollectorModel;
import org.earthtime.Tripoli.dataModels.inputParametersModels.AbstractAcquisitionModel;
import org.earthtime.Tripoli.dataModels.inputParametersModels.SingleCollectorAcquisition;
import org.earthtime.Tripoli.dataModels.inputParametersModels.StaticAcquisition;
import org.earthtime.Tripoli.dataViews.dataMonitorViews.AbstractDataMonitorView;
import org.earthtime.Tripoli.massSpecSetups.AbstractMassSpecSetup;
import org.earthtime.Tripoli.rawDataFiles.handlers.AbstractRawDataFileHandler;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.dialogs.projectManagers.ProjectManagerSubscribeInterface;
import org.earthtime.beans.ET_JButton;
import org.earthtime.dataDictionaries.AcquisitionTypesEnum;
import org.earthtime.dialogs.DialogEditor;
import org.earthtime.isotopes.IsotopesEnum;
import org.earthtime.projects.ProjectInterface;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataViews.AbstractRatiosDataView;
import org.earthtime.ratioDataViews.ReferenceMaterialUPbRatiosDataViewNotEditable;
import org.earthtime.reduxLabData.ReduxLabData;

/**
 *
 * @author James F. Bowring
 */
public class LAICPMSProjectParametersManager extends JLayeredPane {

    /**
     *
     */
    protected ProjectInterface project;

    /**
     *
     */
    protected ETReduxFrame uPbReduxFrame;

    /**
     *
     */
    protected AbstractMassSpecSetup massSpecSetup;

    /**
     *
     */
    protected AbstractRawDataFileHandler rawDataFileHandler;

    /**
     *
     */
    protected Dimension parentDimension;

    /**
     *
     */
    protected JTextField cycleDurationText;

    /**
     *
     */
    protected ArrayList<JLabel> collectorNameLabels;

    /**
     *
     */
    protected ArrayList<JLabel> collectorNameFaradayLabels;

    /**
     *
     */
    protected ArrayList<JLabel> collectorNameIonCounterLabels;

    /**
     *
     */
    protected ArrayList<IsotopesEnum> isotopeNames;

    /**
     *
     */
    protected ArrayList<JTextField> relativeGainTexts;

    /**
     *
     */
    protected ArrayList<JTextField> relativeGainUnctTexts;

    /**
     *
     */
    protected ArrayList<JTextField> deadTimeTexts;

    /**
     *
     */
    protected ArrayList<JTextField> deadTimeUnctTexts;

    /**
     *
     */
    protected ArrayList<JTextField> integrationTimeTexts;

    /**
     *
     */
    protected ArrayList<JComboBox<ResistorEnum>> resistorCombos;

    /**
     *
     */
    protected ArrayList<JTextField> ampNoiseVarianceTexts;

    /**
     *
     */
    protected int leftMargin = 50;

    /**
     *
     */
    protected int topOfTable = 115;

    /**
     *
     */
    protected boolean readyToProcessData;

    /**
     *
     */
    protected ProjectParametersViewDialog parametersViewDialog;

    /**
     *
     */
    protected boolean rawDataProcessed;

    /**
     *
     */
    protected ProjectManagerSubscribeInterface projectManager;

    /**
     *
     */
    protected JSpinner leftMaskSpinner;

    /**
     *
     * @param project
     * @param projectManager
     * @param uPbReduxFrame
     */
    public LAICPMSProjectParametersManager(ProjectInterface project, ProjectManagerSubscribeInterface projectManager, ETReduxFrame uPbReduxFrame) {

        this.project = project;
        this.massSpecSetup = null;
        this.rawDataFileHandler = project.getRawDataFileHandler();
        this.projectManager = projectManager;
        this.uPbReduxFrame = uPbReduxFrame;

        parentDimension = new Dimension(900, 600);

        this.readyToProcessData = false;
        this.rawDataProcessed = rawDataFileHandler.getAcquisitionModel().isRawDataProcessed();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        paint((Graphics2D) g);
    }

    public void initView() {

        boolean editable = !rawDataProcessed;

        removeAll();

        this.setBackground(new Color(255, 222, 173));

        this.setOpaque(true);

        massSpecSetup = rawDataFileHandler.getMassSpec();

        collectorNameLabels = new ArrayList<>();
        isotopeNames = new ArrayList<>();
        collectorNameFaradayLabels = new ArrayList<>();
        collectorNameIonCounterLabels = new ArrayList<>();
        relativeGainTexts = new ArrayList<>();
        relativeGainUnctTexts = new ArrayList<>();
        deadTimeTexts = new ArrayList<>();
        deadTimeUnctTexts = new ArrayList<>();
        integrationTimeTexts = new ArrayList<>();
        resistorCombos = new ArrayList<>();
        ampNoiseVarianceTexts = new ArrayList<>();

        JLabel titleLabel = new JLabel("Please confirm and save parameter values for this data reduction.");
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        titleLabel.setFont(ReduxConstants.sansSerif_12_Bold);
        titleLabel.setBounds(leftMargin, 5, parentDimension.width - 50, 25);
        this.add(titleLabel);

        JLabel massSpecNameLabel = new JLabel("Mass Spec Setup:   " + massSpecSetup.getName());
        massSpecNameLabel.setHorizontalAlignment(SwingConstants.LEFT);
        massSpecNameLabel.setFont(ReduxConstants.sansSerif_12_Bold);
        massSpecNameLabel.setBounds(leftMargin, 25, parentDimension.width - 50, 25);
        this.add(massSpecNameLabel);

        JLabel massSpecTypeLabel = new JLabel("Mass Spec Type:   " + massSpecSetup.getMassSpecType().getPrettyName());
        massSpecTypeLabel.setHorizontalAlignment(SwingConstants.LEFT);
        massSpecTypeLabel.setFont(ReduxConstants.sansSerif_12_Bold);
        massSpecTypeLabel.setBounds(leftMargin, 45, parentDimension.width - 50, 25);
        this.add(massSpecTypeLabel);

        AcquisitionTypesEnum acquisitionType = rawDataFileHandler.getAcquisitionType();

        JLabel acquistionTypeLabel = new JLabel("Acquisition Type:   " + acquisitionType.getName());
        acquistionTypeLabel.setHorizontalAlignment(SwingConstants.LEFT);
        acquistionTypeLabel.setFont(ReduxConstants.sansSerif_12_Bold);
        acquistionTypeLabel.setBounds(leftMargin, 65, 250, 25);
        this.add(acquistionTypeLabel);

        JLabel headerLabel = new JLabel(//
                "Isotope   Collector  Type             ");//
//                + "deadTime       1-sigma abs      " );

        IsotopeMappingModel isotopeMappingModel = massSpecSetup.getIsotopeMappingModel();

        if (acquisitionType.equals(AcquisitionTypesEnum.STATIC)) {
            JLabel cycleDurationLabel = new JLabel("Cycle Duration in seconds: ");
            cycleDurationLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            cycleDurationLabel.setFont(ReduxConstants.sansSerif_12_Bold);
            cycleDurationLabel.setBounds(leftMargin + 300, 65, 200, 25);
            this.add(cycleDurationLabel);

            cycleDurationText = new JTextField();
            cycleDurationText.setDocument(new DialogEditor.BigDecimalDocument(cycleDurationText, editable));
            cycleDurationText.setText(Double.toString(massSpecSetup.getCOLLECTOR_DATA_FREQUENCY_MILLISECS() / 1000.0));
            cycleDurationText.setBounds(leftMargin + 500, 65, 100, 25);
            this.add(cycleDurationText);

            headerLabel.setText(headerLabel.getText()//
                    + "Relative Gain     1-sigma relative      " //
                    + "deadTime   1-sigma abs    " //
                    + "Resistor         ampNoiseVar");

        } else if (acquisitionType.equals(AcquisitionTypesEnum.SINGLE_COLLECTOR)) {
            JLabel deadTimeSingleLabel = new JLabel("Dead Time in nanoseconds: ");
            deadTimeSingleLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            deadTimeSingleLabel.setFont(ReduxConstants.sansSerif_12_Bold);
            deadTimeSingleLabel.setBounds(leftMargin + 250, 65, 200, 25);
            this.add(deadTimeSingleLabel);

            JTextField deadTimeText = new JTextField();
            deadTimeText.setDocument(new DialogEditor.BigDecimalDocument(deadTimeText, editable));

            try {
                deadTimeText.setText(((IonCounterCollectorModel) isotopeMappingModel.getIsotopeToCollectorMap().get(IsotopesEnum.Pb206))//
                        .getDeadTime().getValue().movePointRight(ReduxConstants.getUnitConversionMoveCount("ns"))//
                        .stripTrailingZeros().toPlainString());
            } catch (Exception e) {
            }

            deadTimeText.setBounds(leftMargin + 450, 65, 50, 25);
            this.add(deadTimeText);
            deadTimeTexts.add(deadTimeText);

            JLabel deadTimeUnctSingleLabel = new JLabel("1 sigma abs unct in ns: ");
            deadTimeUnctSingleLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            deadTimeUnctSingleLabel.setFont(ReduxConstants.sansSerif_12_Bold);
            deadTimeUnctSingleLabel.setBounds(leftMargin + 500, 65, 150, 25);
            this.add(deadTimeUnctSingleLabel);

            JTextField deadTimeUnctText = new JTextField();
            deadTimeUnctText.setDocument(new DialogEditor.BigDecimalDocument(deadTimeUnctText, editable));

            try {
                deadTimeUnctText.setText(((IonCounterCollectorModel) isotopeMappingModel.getIsotopeToCollectorMap().get(IsotopesEnum.Pb206))//
                        .getDeadTime().getOneSigmaAbs().movePointRight(ReduxConstants.getUnitConversionMoveCount("ns"))//
                        .stripTrailingZeros().toPlainString());
            } catch (Exception e) {
            }

            deadTimeUnctText.setBounds(leftMargin + 500 + 150, 65, 50, 25);
            this.add(deadTimeUnctText);
            deadTimeUnctTexts.add(deadTimeUnctText);

            //**
            JLabel relativeGainLabel = new JLabel("Relative Gain: ");
            relativeGainLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            relativeGainLabel.setFont(ReduxConstants.sansSerif_12_Bold);
            relativeGainLabel.setBounds(leftMargin + 250, 90, 200, 25);
            this.add(relativeGainLabel);

            JTextField relativeGainText = new JTextField();
            relativeGainText.setDocument(new DialogEditor.BigDecimalDocument(relativeGainText, editable));
            try {
                relativeGainText.setText(isotopeMappingModel.getIsotopeToCollectorMap().get(IsotopesEnum.Pb206)//
                        .getRelativeGain().getValue().stripTrailingZeros().toPlainString());

            } catch (Exception e) {
            }

            relativeGainText.setBounds(leftMargin + 450, 90, 50, 25);
            this.add(relativeGainText);
            relativeGainTexts.add(relativeGainText);

            JLabel relativeGainUnctLabel = new JLabel("1 sigma %: ");
            relativeGainUnctLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            relativeGainUnctLabel.setFont(ReduxConstants.sansSerif_12_Bold);
            relativeGainUnctLabel.setBounds(leftMargin + 500, 90, 150, 25);
            this.add(relativeGainUnctLabel);

            JTextField relativeGainUnctText = new JTextField();
            relativeGainUnctText.setDocument(new DialogEditor.BigDecimalDocument(relativeGainUnctText, editable));

            try {
                relativeGainUnctText.setText(isotopeMappingModel.getIsotopeToCollectorMap().get(IsotopesEnum.Pb206)//
                        .getRelativeGain().getOneSigmaPct().stripTrailingZeros().toPlainString());
            } catch (Exception e) {
            }
            relativeGainUnctText.setBounds(leftMargin + 500 + 150, 90, 50, 25);
            this.add(relativeGainUnctText);
            relativeGainUnctTexts.add(relativeGainUnctText);

            //**
            collectorNameIonCounterLabels.add(new JLabel("Single"));

            headerLabel.setText(headerLabel.getText() + "     Integration Time");
        }

        headerLabel.setHorizontalAlignment(SwingConstants.LEFT);
        headerLabel.setFont(ReduxConstants.sansSerif_12_Bold);
        headerLabel.setBounds(leftMargin, topOfTable, parentDimension.width - 50, 25);
        this.add(headerLabel);

        Iterator<IsotopesEnum> isotopeIterator
                = isotopeMappingModel.getIsotopeToCollectorMap().keySet().iterator();

        int count = 1;
        while (isotopeIterator.hasNext()) {
            IsotopesEnum key = isotopeIterator.next();

            JLabel isotopeNameLabel = new JLabel(key.getPrettyName());
            isotopeNameLabel.setBounds(leftMargin, topOfTable + 25 * count, 65, 25);
            this.add(isotopeNameLabel);
            isotopeNames.add(key);

            JLabel collectorNameLabel = new JLabel(isotopeMappingModel.getIsotopeToCollectorMap().get(key).getCollectorName());
            collectorNameLabel.setBounds(leftMargin + 65, topOfTable + 25 * count, 60, 25);
            this.add(collectorNameLabel);
            collectorNameLabels.add(collectorNameLabel);

            JLabel collectorTypeLabel = new JLabel(isotopeMappingModel.getIsotopeToCollectorMap().get(key).getCollectorType());
            collectorTypeLabel.setBounds(leftMargin + 75 + 50, topOfTable + 25 * count, 75, 25);
            this.add(collectorTypeLabel);

            if (acquisitionType.equals(AcquisitionTypesEnum.STATIC)) {

                JTextField relativeGainText = new JTextField();
                relativeGainText.setDocument(new DialogEditor.BigDecimalDocument(relativeGainText, editable));
                try {
                    relativeGainText.setText(isotopeMappingModel.getIsotopeToCollectorMap().get(key)//
                            .getRelativeGain().getValue().stripTrailingZeros().toPlainString());
                } catch (Exception e) {
                }
                relativeGainText.setBounds(leftMargin + 75 + 50 + 75, topOfTable + 25 * count, 100, 25);
                this.add(relativeGainText);
                relativeGainTexts.add(relativeGainText);

                JTextField relativeGainUnctText = new JTextField();
                relativeGainUnctText.setDocument(new DialogEditor.BigDecimalDocument(relativeGainUnctText, editable));
                // faraday show ppm relative gain unct
                if (collectorTypeLabel.getText().startsWith("F")) {
                    relativeGainUnctText.setText(isotopeMappingModel.getIsotopeToCollectorMap().get(key)//
                            .getRelativeGain().getOneSigmaPct().movePointRight(4).stripTrailingZeros().toPlainString());
                } else {
                    relativeGainUnctText.setText(isotopeMappingModel.getIsotopeToCollectorMap().get(key)//
                            .getRelativeGain().getOneSigmaPct().stripTrailingZeros().toPlainString());
                }
                relativeGainUnctText.setBounds(leftMargin + 75 + 50 + 75 + 100, topOfTable + 25 * count, 100, 25);
                this.add(relativeGainUnctText);
                relativeGainUnctTexts.add(relativeGainUnctText);

                JLabel unctTypeLabel = new JLabel(//
                        collectorTypeLabel.getText().startsWith("F") ? "ppm" : " % ");
                unctTypeLabel.setBounds(leftMargin + 75 + 50 + 75 + 100 + 100, topOfTable + 25 * count, 30, 25);
                this.add(unctTypeLabel);
            }

            // dead time for ion counters
            NumberFormat formatter = new DecimalFormat("###0.00");
            if (collectorTypeLabel.getText().startsWith("I")) {
                // for case of all collectors are ion counters
                if (acquisitionType.equals(AcquisitionTypesEnum.SINGLE_COLLECTOR)) {
                    JTextField integrationTimeText = new JTextField();
                    integrationTimeText.setHorizontalAlignment(JTextField.RIGHT);
                    integrationTimeText.setDocument(new DialogEditor.BigDecimalDocument(integrationTimeText, editable));

                    integrationTimeText.setText( //
                            formatter.format(isotopeMappingModel.getIsotopeToIntegrationTimeMap().get(key) * 1000.0));

                    integrationTimeText.setBounds(leftMargin + 75 + 50 + 75 + 25, topOfTable + 25 * count, 75, 25);
                    this.add(integrationTimeText);
                    integrationTimeTexts.add(integrationTimeText);

                    JLabel millisecLabel = new JLabel("millisec");
                    millisecLabel.setBounds(leftMargin + 75 + 50 + 75 + 25 + 75, topOfTable + 25 * count, 50, 25);
                    this.add(millisecLabel);
                } else {

                    collectorNameIonCounterLabels.add(collectorNameLabel);

                    JTextField deadTimeText = new JTextField();
                    deadTimeText.setDocument(new DialogEditor.BigDecimalDocument(deadTimeText, editable));

                    deadTimeText.setText(((IonCounterCollectorModel) isotopeMappingModel.getIsotopeToCollectorMap().get(key))//
                            .getDeadTime().getValue().movePointRight(ReduxConstants.getUnitConversionMoveCount("ns"))//
                            .stripTrailingZeros().toPlainString());

                    deadTimeText.setBounds(leftMargin + 75 + 50 + 75 + 100 + 100 + 30, topOfTable + 25 * count, 50, 25);
                    this.add(deadTimeText);
                    deadTimeTexts.add(deadTimeText);

                    JLabel nanosecLabel = new JLabel("ns");
                    nanosecLabel.setBounds(leftMargin + 75 + 50 + 75 + 100 + 100 + 30 + 50, topOfTable + 25 * count, 25, 25);
                    this.add(nanosecLabel);

                    JTextField deadTimeUnctText = new JTextField();
                    deadTimeUnctText.setDocument(new DialogEditor.BigDecimalDocument(deadTimeUnctText, editable));

                    try {
                        deadTimeUnctText.setText(((IonCounterCollectorModel) isotopeMappingModel.getIsotopeToCollectorMap().get(key))//
                                .getDeadTime().getOneSigmaAbs().movePointRight(ReduxConstants.getUnitConversionMoveCount("ns"))//
                                .stripTrailingZeros().toPlainString());
                    } catch (Exception e) {
                    }

                    deadTimeUnctText.setBounds(leftMargin + 75 + 50 + 75 + 100 + 100 + 30 + 50 + 25, topOfTable + 25 * count, 50, 25);
                    this.add(deadTimeUnctText);
                    deadTimeUnctTexts.add(deadTimeUnctText);

                    JLabel nanosecLabel2 = new JLabel("ns");
                    nanosecLabel2.setBounds(leftMargin + 75 + 50 + 75 + 100 + 100 + 30 + 50 + 25 + 50, topOfTable + 25 * count, 25, 25);
                    this.add(nanosecLabel2);
                }

            } else {
                // setup faraday
                collectorNameFaradayLabels.add(collectorNameLabel);

                final JComboBox<ResistorEnum> resistorCombo = new JComboBox<>(FaradayCollectorModel.ResistorEnum.getArrayOfResistors());

                resistorCombo.setSelectedItem(((FaradayCollectorModel) isotopeMappingModel.getIsotopeToCollectorMap().get(key))//
                        .getResistor());

                resistorCombo.setFont(ReduxConstants.sansSerif_12_Plain);
                resistorCombo.setBounds(leftMargin + 75 + 50 + 75 + 100 + 100 + 30 + 50 + 25 + 50 + 25, topOfTable + 25 * count, 110, 25);
                resistorCombo.setEnabled(editable);
                resistorCombos.add(resistorCombo);

                final JTextField ampNoiseVarianceText = new JTextField();
                ampNoiseVarianceText.setDocument(new DialogEditor.DoubleDocument(ampNoiseVarianceText, editable));
                ampNoiseVarianceText.setText( //
                        Double.toString(((FaradayCollectorModel) isotopeMappingModel.getIsotopeToCollectorMap().get(key))//
                                .getAmplifierNoiseVariance()));

                ampNoiseVarianceText.setBounds(leftMargin + 75 + 50 + 75 + 100 + 100 + 30 + 50 + 25 + 50 + 25 + 110, topOfTable + 25 * count, 90, 25);
                this.add(ampNoiseVarianceText);
                ampNoiseVarianceTexts.add(ampNoiseVarianceText);

                resistorCombo.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent ie) {
                        ampNoiseVarianceText.setText( //
                                Double.toString(((FaradayCollectorModel.ResistorEnum) resistorCombo.getSelectedItem())//
                                        .getDefaultAmplifierNoiseVariance()));
                    }
                });
                this.add(resistorCombo);

            }

            count++;
        }

        // clean up 
        if (acquisitionType.equals(AcquisitionTypesEnum.SINGLE_COLLECTOR)) {
            collectorNameLabels.clear();
            collectorNameLabels.trimToSize();
            collectorNameLabels.add(new JLabel("Single"));

            collectorNameIonCounterLabels.clear();
            collectorNameIonCounterLabels.trimToSize();
            collectorNameIonCounterLabels.add(new JLabel("Single"));

        }
        if (rawDataFileHandler.getAcquisitionModel().getRawDataFile() != null) {
            try {
                // show rawdatafile path            
                JTextArea rawDataFilePathTextArea = new JTextArea(//
                        "Raw data location: "
                        + rawDataFileHandler.getAcquisitionModel().getRawDataFile().getCanonicalPath());

                rawDataFilePathTextArea.setBounds(leftMargin, 370, parentDimension.width - 100, 50);
                rawDataFilePathTextArea.setLineWrap(true);
                rawDataFilePathTextArea.setEditable(false);
                this.add(rawDataFilePathTextArea);

            } catch (IOException ex) {
                Logger.getLogger(LAICPMSProjectParametersManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        // Aug 2016
        if (acquisitionType.equals(AcquisitionTypesEnum.SINGLE_COLLECTOR)) {
            rawDataFileHandler.getAcquisitionModel().setLeftShadeCount(0);

            JLabel baselineStartIndexLabel = new JLabel("Baseline start index: ");
            baselineStartIndexLabel.setHorizontalAlignment(SwingConstants.LEFT);
            baselineStartIndexLabel.setFont(ReduxConstants.sansSerif_12_Bold);
            baselineStartIndexLabel.setBounds(leftMargin, 425, 125, 25);
            this.add(baselineStartIndexLabel);

            final JTextField baselineStartIndex = new JTextField();
            baselineStartIndex.setDocument(new DialogEditor.IntegerDocument(baselineStartIndex, editable));
            baselineStartIndex.setText(String.valueOf(rawDataFileHandler.getAcquisitionModel().getBaselineStartIndex()));
            baselineStartIndex.setFont(ReduxConstants.sansSerif_12_Bold);
            baselineStartIndex.setBounds(leftMargin + 125, 425, 25, 25);
            this.add(baselineStartIndex);

            JLabel baselineEndIndexLabel = new JLabel("end: ");
            baselineEndIndexLabel.setHorizontalAlignment(SwingConstants.LEFT);
            baselineEndIndexLabel.setFont(ReduxConstants.sansSerif_12_Bold);
            baselineEndIndexLabel.setBounds(leftMargin + 150, 425, 50, 25);
            this.add(baselineEndIndexLabel);

            final JTextField baselineEndIndex = new JTextField();
            baselineEndIndex.setDocument(new DialogEditor.IntegerDocument(baselineEndIndex, editable));
            baselineEndIndex.setText(String.valueOf(rawDataFileHandler.getAcquisitionModel().getBaselineEndIndex()));
            baselineEndIndex.setFont(ReduxConstants.sansSerif_12_Bold);
            baselineEndIndex.setBounds(leftMargin + 200, 425, 25, 25);
            this.add(baselineEndIndex);

        } else {
            JLabel leftShadeLabel = new JLabel("Count of starting datapoints to ignore (4 max): ");
            leftShadeLabel.setHorizontalAlignment(SwingConstants.LEFT);
            leftShadeLabel.setFont(ReduxConstants.sansSerif_12_Bold);
            leftShadeLabel.setBounds(leftMargin, 425, 300, 25);
            this.add(leftShadeLabel);

            SpinnerModel leftMaskModel
                    = new SpinnerNumberModel(0, //initial value
                            0, //min
                            4, //max
                            1); //step
            leftMaskSpinner = new JSpinner(leftMaskModel);
            leftMaskSpinner.setValue(rawDataFileHandler.getAcquisitionModel().getLeftShadeCount());
            leftMaskSpinner.setBounds(leftMargin + 305, 425, 40, 25);
            leftMaskSpinner.setValue(rawDataFileHandler.getAcquisitionModel().getLeftShadeCount());
            leftMaskSpinner.setEnabled(editable);
            leftMaskSpinner.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {
                    rawDataFileHandler.getAcquisitionModel().setLeftShadeCount((int) leftMaskSpinner.getValue());
                }
            });
            this.add(leftMaskSpinner);
        }

        ButtonGroup propagationSpeedGroup = new ButtonGroup();

        JRadioButton fastPropagationRB = new JRadioButton("Fast uncertainty propagation");
        propagationSpeedGroup.add(fastPropagationRB);
        fastPropagationRB.setBounds(leftMargin, 450, 300, 25);
        fastPropagationRB.setSelected(!rawDataFileHandler.getAcquisitionModel().isUsingFullPropagation());
        this.add(fastPropagationRB);

        final JRadioButton fullPropagationRB = new JRadioButton("Full uncertainty propagation");
        propagationSpeedGroup.add(fullPropagationRB);
        fullPropagationRB.setBounds(leftMargin, 475, 300, 25);
        fullPropagationRB.setSelected(rawDataFileHandler.getAcquisitionModel().isUsingFullPropagation());
        this.add(fullPropagationRB);

        // primary reference material chooser
        JLabel defaultPrimaryReferenceMaterialLabel = new JLabel("Select Primary Reference Material Model: ");
        defaultPrimaryReferenceMaterialLabel.setHorizontalAlignment(SwingConstants.LEFT);
        defaultPrimaryReferenceMaterialLabel.setFont(ReduxConstants.sansSerif_12_Bold);
        defaultPrimaryReferenceMaterialLabel.setBounds(500, 450, 400, 25);
        this.add(defaultPrimaryReferenceMaterialLabel);

        JComboBox referenceMagerialsComboBox = new JComboBox();
        ArrayList<AbstractRatiosDataModel> mineralStandardModels = ReduxLabData.getInstance().getMineralStandardModels();
        for (int i = (mineralStandardModels.size() > 1 ? 1 : 0); i < mineralStandardModels.size(); i++) {
            referenceMagerialsComboBox.addItem(mineralStandardModels.get(i));
        }

        referenceMagerialsComboBox.setSelectedItem(rawDataFileHandler.getAcquisitionModel().getPrimaryMineralStandardModel());
        referenceMagerialsComboBox.addActionListener((ActionEvent e) -> {
            rawDataFileHandler.getAcquisitionModel().setPrimaryMineralStandardModel((AbstractRatiosDataModel) ((JComboBox) e.getSource()).getSelectedItem());
        });

        referenceMagerialsComboBox.setBounds(500, 475, 275, 25);
        referenceMagerialsComboBox.setFont(ReduxConstants.sansSerif_10_Bold);
        this.add(referenceMagerialsComboBox);

        // view reference material model
        JButton viewReferenceMaterialModelButton = new ET_JButton("View");
        viewReferenceMaterialModelButton.addActionListener((ActionEvent e) -> {
            AbstractRatiosDataModel selectedModel
                    = ((AbstractRatiosDataModel) referenceMagerialsComboBox.getSelectedItem());
            AbstractRatiosDataView modelView
                    = new ReferenceMaterialUPbRatiosDataViewNotEditable(selectedModel, null, false);
            modelView.displayModelInFrame();
        });
        viewReferenceMaterialModelButton.setFont(ReduxConstants.sansSerif_10_Bold);
        viewReferenceMaterialModelButton.setBounds(775, 475, 30, 23);
        this.add(viewReferenceMaterialModelButton);

        // toolbar
        ET_JButton monitorButton = new ET_JButton("Save and Monitor/Process Raw Data");
        monitorButton.setBounds(leftMargin, 525, 225, 25);
        monitorButton.addActionListener((ActionEvent ae) -> {
            saveData_buttonActionPerformed(ae);
            rawDataFileHandler.getAcquisitionModel().setUsingFullPropagation(fullPropagationRB.isSelected());
            AbstractDataMonitorView dataMonitorView = new AbstractDataMonitorView(project, projectManager, uPbReduxFrame);
            projectManager.updateDataChangeStatus(true);
            dataMonitorView.displayModelInFrame();
        });
        monitorButton.setEnabled(editable);

        this.add(monitorButton);

        ET_JButton saveAndLoadButton = new ET_JButton("Save and Load/Process Raw Data");
        saveAndLoadButton.setBounds(leftMargin + 225, 525, 225, 25);
        saveAndLoadButton.addActionListener((ActionEvent ae) -> {
            saveAndLoadData_buttonActionPerformed(ae);
            rawDataFileHandler.getAcquisitionModel().setUsingFullPropagation(fullPropagationRB.isSelected());
            projectManager.updateDataChangeStatus(true);
        });
        saveAndLoadButton.setEnabled(editable);

        this.add(saveAndLoadButton);

        ET_JButton saveButton = new ET_JButton("Save and Close");
        saveButton.setBounds(leftMargin + 225 + 225, 525, 125, 25);
        saveButton.addActionListener((ActionEvent ae) -> {
            saveData_buttonActionPerformed(ae);
            projectManager.updateDataChangeStatus(true);
        });

        saveButton.setEnabled(editable);
        this.add(saveButton);

        ET_JButton repropagateButton = new ET_JButton("Re-propagate Unct");
        repropagateButton.setBounds(leftMargin + 125 + 225 + 225, 525, 150, 25);
        repropagateButton.addActionListener((ActionEvent ae) -> {
            // dec 2014
            rawDataFileHandler.getAcquisitionModel().setUsingFullPropagation(fullPropagationRB.isSelected());
            projectManager.reProcessFractionRawRatios(fullPropagationRB.isSelected());
            projectManager.updateDataChangeStatus(true);
            readyToProcessData = true;
            parametersViewDialog.setVisible(false);;
        });

        repropagateButton.setEnabled(!editable);
        this.add(repropagateButton);

        ET_JButton closeButton = new ET_JButton("Close");
        closeButton.setBounds(leftMargin + 125 + 225 + 200 + 175, 525, 75, 25);
        closeButton.addActionListener((ActionEvent ae) -> {
            projectManager.updateDataChangeStatus(true);
            parametersViewDialog.setVisible(false);
        });

        this.add(closeButton);

    }

    /**
     *
     * @param g2d
     */
    protected void paintInit(Graphics2D g2d) {
        RenderingHints rh = g2d.getRenderingHints();
        rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHints(rh);

        g2d.setPaint(Color.BLACK);
        g2d.setStroke(new BasicStroke(1.0f));
        g2d.setFont(ReduxConstants.sansSerif_12_Bold);
    }

    /**
     *
     * @param g2d
     */
    public void paint(Graphics2D g2d) {
        paintInit(g2d);

    }

    /**
     *
     */
    protected class ProjectParametersViewDialog extends javax.swing.JDialog {

        /**
         *
         * @param owner
         * @param modal
         */
        public ProjectParametersViewDialog(Dialog owner, boolean modal) {
            super(owner, modal);
        }
    }

    /**
     *
     * @return
     */
    public JDialog displayModelInFrame() {

        parametersViewDialog = new ProjectParametersViewDialog(null, true);
        parametersViewDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        parametersViewDialog.setBounds( //
                400, 400, //
                (int) parentDimension.getWidth(),
                (int) parentDimension.getHeight());

        //Get the screen size
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();

        //Calculate the frame location
        int x = (screenSize.width - (int) parentDimension.getWidth()) / 2;
        int y = (screenSize.height - (int) parentDimension.getHeight()) / 2;

        //Set the new frame location centered
        parametersViewDialog.setLocation(uPbReduxFrame.getX() - 100, y);

        parametersViewDialog.add(this);
        parametersViewDialog.setVisible(true);
        parametersViewDialog.toFront();

        return parametersViewDialog;
    }

    /**
     *
     */
    public void toFront() {
        parametersViewDialog.toFront();
    }

    private void saveAndLoadData(boolean loadData) {
        AbstractAcquisitionModel acquisitionModel = rawDataFileHandler.getAcquisitionModel();

        if (acquisitionModel.getAcquisitionType().equals(AcquisitionTypesEnum.STATIC)) {
            ((StaticAcquisition) acquisitionModel).setCycleDurationInSeconds(Double.valueOf(cycleDurationText.getText()));
        }

        // save table data
        Map<String, Double> collectorNameToRelativeGainsMap = new TreeMap<>();
        Map<String, Double> collectorNameToRelativeGainsUnctMap = new TreeMap<>();
        for (int i = 0; i < relativeGainTexts.size(); i++) {
            try {
                collectorNameToRelativeGainsMap.put( //
                        collectorNameLabels.get(i).getText().trim(), Double.valueOf(relativeGainTexts.get(i).getText()));
                collectorNameToRelativeGainsUnctMap.put( //
                        collectorNameLabels.get(i).getText().trim(), Double.valueOf(relativeGainUnctTexts.get(i).getText()));
            } catch (NumberFormatException numberFormatException) {
            }
        }
        acquisitionModel.setCollectorNameToRelativeGainsMap(collectorNameToRelativeGainsMap);
        acquisitionModel.setCollectorNameToRelativeGainsUnctMap(collectorNameToRelativeGainsUnctMap);

        // ioncounters
        Map<String, Double> collectorNameToDeadTimesMap = new TreeMap<>();
        Map<String, Double> collectorNameToDeadTimesUnctMap = new TreeMap<>();
        for (int i = 0; i < deadTimeTexts.size(); i++) {
            try {
                collectorNameToDeadTimesMap.put( //
                        collectorNameIonCounterLabels.get(i).getText().trim(), Double.valueOf(deadTimeTexts.get(i).getText()));
                collectorNameToDeadTimesUnctMap.put( //
                        collectorNameIonCounterLabels.get(i).getText().trim(), Double.valueOf(deadTimeUnctTexts.get(i).getText()));
            } catch (NumberFormatException numberFormatException) {
            }
        }
        acquisitionModel.setCollectorNameToDeadTimesMap(collectorNameToDeadTimesMap);
        acquisitionModel.setCollectorNameToDeadTimesUnctMap(collectorNameToDeadTimesUnctMap);

        if (acquisitionModel.getAcquisitionType().equals(AcquisitionTypesEnum.SINGLE_COLLECTOR)) {
            Map<IsotopesEnum, Double> isotopeNameToIntegrationTimesMap
                    = //
                    new EnumMap<>(IsotopesEnum.class);

            for (int i = 0; i < isotopeNames.size(); i++) {
                isotopeNameToIntegrationTimesMap.put( //
                        isotopeNames.get(i),//
                        Double.valueOf(integrationTimeTexts.get(i).getText()) / 1000.0);

            }
            ((SingleCollectorAcquisition) acquisitionModel).setIsotopeNameToIntegrationTimesMap(isotopeNameToIntegrationTimesMap);
        }

        // faradays for resistors and amp noise
        if (acquisitionModel.getAcquisitionType().equals(AcquisitionTypesEnum.STATIC)) {
            Map<String, FaradayCollectorModel.ResistorEnum> collectorNameToResistorMap
                    = //
                    new TreeMap<>();
            Map<String, Double> collectorNameToAmpNoiseMap = new TreeMap<>();

            for (int i = 0; i < resistorCombos.size(); i++) {
                collectorNameToResistorMap.put( //
                        collectorNameFaradayLabels.get(i).getText().trim(), (FaradayCollectorModel.ResistorEnum) resistorCombos.get(i).getSelectedItem());
                collectorNameToAmpNoiseMap.put( //
                        collectorNameFaradayLabels.get(i).getText().trim(), Double.valueOf(ampNoiseVarianceTexts.get(i).getText()));

            }

            acquisitionModel.setCollectorNameToResistorMap(collectorNameToResistorMap);
            acquisitionModel.setCollectorNameToAmpNoiseMap(collectorNameToAmpNoiseMap);
        }

        acquisitionModel.updateMassSpec(massSpecSetup);

        readyToProcessData = loadData;
    }

    private void saveAndLoadData_buttonActionPerformed(java.awt.event.ActionEvent evt) {

        saveAndLoadData(true);
        parametersViewDialog.setVisible(false);

    }

    private void saveData_buttonActionPerformed(java.awt.event.ActionEvent evt) {

        saveAndLoadData(false);
        parametersViewDialog.setVisible(false);

    }

    /**
     * @return the readyToProcessData
     */
    public boolean isReadyToProcessData() {
        return readyToProcessData;
    }
}
