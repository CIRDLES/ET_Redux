/*
 * InitialPbTripoliDataView.java
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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.dialogs.DialogEditor;
import org.earthtime.UPb_Redux.dialogs.projectManagers.InitialPbModelSynchInterface;
import org.earthtime.beans.ET_JButton;
import org.earthtime.exceptions.ETException;
import org.earthtime.matrices.matrixModels.AbstractMatrixModel;
import org.earthtime.matrices.matrixModels.ValueModelsMatrix;
import org.earthtime.matrices.matrixViews.AbstractMatrixGridView;
import org.earthtime.matrices.matrixViews.MatrixGridViewEditable;
import org.earthtime.matrices.matrixViews.MatrixGridViewEditableRhoSK;
import org.earthtime.matrices.matrixViews.MatrixGridViewEditableValueModels;
import org.earthtime.matrices.matrixViews.MatrixGridViewEditableValueModelsSK;
import org.earthtime.matrices.matrixViews.MatrixGridViewNotEditable;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataModels.initialPbModelsET.PlaceholderInitialPb76Model;
import org.earthtime.ratioDataModels.initialPbModelsET.PlaceholderInitialPbModel;
import org.earthtime.ratioDataModels.initialPbModelsET.StaceyKramersInitialPbModelET;

/**
 *
 * @author James F. Bowring
 */
public class InitialPbTripoliDataView extends AbstractRatiosDataView {

    private AbstractMatrixGridView initialPbModelMatrixView;
    private AbstractMatrixModel initialPbModelMatrix;
    private AbstractMatrixModel initialPbModelMatrixRhoVar;
    private AbstractMatrixModel initialPbModelMatrixRhoSys;
    private final boolean editable;
    private final boolean showTableOnly;
    private boolean showAbsUnct;
    private static final int LEFT_MARGIN = 80;
    private JTextField skEstimatedDate_text;
    private final boolean usesEstDateForSK;
    private final InitialPbModelSynchInterface fractionGridRow;

    /**
     *
     *
     * @param fractionGridRow the value of fractionGridRow
     * @param ratiosDataModel
     * @param editable the value of editable
     * @param parentDimension the value of parentDimension
     * @param showTableOnly the value of showTableOnly
     * @param showAbsUnct the value of showAbsUnct
     * @param usesEstDateForSK the value of usesEstDateForSK
     */
    public InitialPbTripoliDataView(//
            InitialPbModelSynchInterface fractionGridRow, //
            AbstractRatiosDataModel ratiosDataModel, //
            boolean editable, Dimension parentDimension, //
            boolean showTableOnly, //
            boolean showAbsUnct,//
            boolean usesEstDateForSK) {
        super(ratiosDataModel, parentDimension);

        this.fractionGridRow = fractionGridRow;
        this.showTableOnly = showTableOnly;
        this.editable = editable;
        this.showAbsUnct = showAbsUnct;
        this.usesEstDateForSK = usesEstDateForSK;

        this.setBackground(Color.white);

        myInitView();
    }

    @Override
    public void paint(Graphics2D g2d) {
        super.paint(g2d);
        g2d.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
    }

    private void myInitView() {
        initView(editable);
    }

    /**
     *
     * @param editable the value of editable
     */
    @Override
    protected void initView(boolean editable) {

        removeAll();

        initialPbModelMatrix = new ValueModelsMatrix(dataModel.getNameAndVersion());

        initialPbModelMatrixRhoVar = dataModel.getDataCorrelationsVarUnct();
        initialPbModelMatrixRhoSys = dataModel.getDataCorrelationsSysUnct();

        if (editable) {
            if (dataModel instanceof StaceyKramersInitialPbModelET) {
                initialPbModelMatrixView = new MatrixGridViewEditableValueModelsSK(initialPbModelMatrix, this, showTableOnly, false, showAbsUnct);
                correlationVarUnctMatrixView = new MatrixGridViewEditableRhoSK(initialPbModelMatrixRhoVar, this, showTableOnly, true);
                correlationSysUnctMatrixView = new MatrixGridViewEditableRhoSK(initialPbModelMatrixRhoSys, this, showTableOnly, true);
            } else {// placeholder model
                initialPbModelMatrixView = new MatrixGridViewEditableValueModels(initialPbModelMatrix, this, showTableOnly, false, showAbsUnct);
                correlationVarUnctMatrixView = new MatrixGridViewEditable(initialPbModelMatrixRhoVar, this, showTableOnly, true);
                correlationSysUnctMatrixView = new MatrixGridViewEditable(initialPbModelMatrixRhoSys, this, showTableOnly, true);
            }
        } else {
            initialPbModelMatrixView = new MatrixGridViewNotEditable(initialPbModelMatrix, showTableOnly);

            correlationVarUnctMatrixView = new MatrixGridViewNotEditable(initialPbModelMatrixRhoVar, showTableOnly);
            correlationSysUnctMatrixView = new MatrixGridViewNotEditable(initialPbModelMatrixRhoSys, showTableOnly);
        }

        JRadioButton absUnctRadioButton = new JRadioButton("<html><u>1\u03C3 ABS</u></html>");
        absUnctRadioButton.setName("true");
        absUnctRadioButton.setFont(ReduxConstants.sansSerif_10_Bold);
        absUnctRadioButton.setHorizontalAlignment(SwingConstants.CENTER);
        absUnctRadioButton.setBounds(5, 12, 75, 25);
        absUnctRadioButton.setSelected(showAbsUnct);
        add(absUnctRadioButton);

        JRadioButton pctUnctRadioButton = new JRadioButton("<html><u>1\u03C3 PCT</u></html>");
        pctUnctRadioButton.setName("false");
        pctUnctRadioButton.setFont(ReduxConstants.sansSerif_10_Bold);
        pctUnctRadioButton.setHorizontalAlignment(SwingConstants.CENTER);
        pctUnctRadioButton.setBounds(5, 12 + 25, 75, 25);
        pctUnctRadioButton.setSelected(!showAbsUnct);
        add(pctUnctRadioButton);

        ButtonGroup uncertaintyGroup = new ButtonGroup();
        uncertaintyGroup.add(absUnctRadioButton);
        uncertaintyGroup.add(pctUnctRadioButton);

        ActionListener uncertaintyActionListener = (ActionEvent actionEvent) -> {
            AbstractButton aButton = (AbstractButton) actionEvent.getSource();
            boolean showAbsUnctBut = Boolean.valueOf(aButton.getName());

            if ((showAbsUnct != showAbsUnctBut)) {
                showAbsUnct = showAbsUnctBut;
                //setShowAbsUnct(showAbsUnct);
                updateModelView();
            }
        };

        absUnctRadioButton.addActionListener(uncertaintyActionListener);
        pctUnctRadioButton.addActionListener(uncertaintyActionListener);

        this.add(initialPbModelMatrixView);
        this.add(correlationVarUnctMatrixView);
        this.add(correlationSysUnctMatrixView);

        JLabel varUnctRhoLabel = new JLabel("Var Uncertainty Correlations [-1,1]");
        varUnctRhoLabel.setFont(ReduxConstants.sansSerif_11_Bold);
        varUnctRhoLabel.setBounds(LEFT_MARGIN + 345, 57, 220, 25);
        this.add(varUnctRhoLabel);

        JLabel sysUnctRhoLabel = new JLabel("Sys Uncertainty Correlations [-1,1]");
        sysUnctRhoLabel.setFont(ReduxConstants.sansSerif_11_Bold);
        sysUnctRhoLabel.setBounds(LEFT_MARGIN + 345 + 230, 57, 220, 25);
        this.add(sysUnctRhoLabel);

        if (usesEstDateForSK && (dataModel instanceof StaceyKramersInitialPbModelET)) {
            JLabel skRhoSysUnct_label = new JLabel("EstDate Ma:");
            skRhoSysUnct_label.setBounds(LEFT_MARGIN + 345 + 230 + 230, 30, 60, 25);
            skRhoSysUnct_label.setFont(ReduxConstants.sansSerif_10_Plain);
            this.add(skRhoSysUnct_label);

            skEstimatedDate_text = new JTextField();
            skEstimatedDate_text.setFont(new java.awt.Font("Monospaced", 1, 10));
            skEstimatedDate_text.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
            skEstimatedDate_text.setDocument(new DialogEditor.DoubleDocument(skEstimatedDate_text, true));
            skEstimatedDate_text.setVisible(true);
            skEstimatedDate_text.setBounds(LEFT_MARGIN + 345 + 230 + 230, 55, 60, 25);
            skEstimatedDate_text.setText(((StaceyKramersInitialPbModelET) dataModel).getSKEstimatedDateFromFraction().toPlainString());
            this.add(skEstimatedDate_text);

            skEstimatedDate_text.addFocusListener(new FocusListener() {
                @Override
                public void focusGained(FocusEvent e) {
                }

                @Override
                public void focusLost(FocusEvent e) {
                    ((StaceyKramersInitialPbModelET) dataModel).saveSKEstimatedDateToFraction(Double.valueOf(skEstimatedDate_text.getText()));
                    ((StaceyKramersInitialPbModelET) dataModel).resetModelFromTripoliFraction();
                    updateModelView();
                }
            });

            skEstimatedDate_text.addKeyListener(new KeyListener() {
                @Override
                public void keyTyped(KeyEvent e) {
                }

                @Override
                public void keyPressed(KeyEvent e) {
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        ((Component) e.getSource()).dispatchEvent(new FocusEvent(((Component) e.getSource()), FocusEvent.FOCUS_LOST));
                    }
                }
            });
        }

        if (dataModel instanceof StaceyKramersInitialPbModelET) {
            JButton synchButton = new ET_JButton("Synch");
            synchButton.setBounds(LEFT_MARGIN + 345 + 230 + 230, 0, 60, 25);
            synchButton.addActionListener((ActionEvent e) -> {
                fractionGridRow.synchToThisInitialPbModelSK(((StaceyKramersInitialPbModelET) dataModel).getTripoliFraction().assembleStaceyKramerCorrectionParameters());
                fractionGridRow.synchToThisSynchronizedSKDate(((StaceyKramersInitialPbModelET) dataModel).getTripoliFraction().getRadDateForSKSynch());
            });
            this.add(synchButton);
        }

        if (dataModel instanceof PlaceholderInitialPbModel) {
            JButton synchButton = new ET_JButton("Synch");
            synchButton.setBounds(LEFT_MARGIN + 345 + 230 + 230, 0, 60, 25);
            synchButton.addActionListener((ActionEvent e) -> {
                fractionGridRow.synchToThisInitialPbModelPlaceHolderFromFraction(((PlaceholderInitialPbModel) dataModel).getTripoliFraction());
            });
            this.add(synchButton);
        }

        if (dataModel instanceof PlaceholderInitialPb76Model) {
            JButton synchButton = new ET_JButton("Synch");
            synchButton.setBounds(LEFT_MARGIN + 345 + 230 + 230, 0, 60, 25);
            synchButton.addActionListener((ActionEvent e) -> {
                fractionGridRow.synchToThisInitialPbModelPlaceHolderFromFraction(((PlaceholderInitialPb76Model) dataModel).getTripoliFraction());
            });
            this.add(synchButton);
        }

        updateModelView();
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

        updateMatrixModelsAndSaveRhos(checkCovarianceValidity);

        // sept 2014 - only SK or Placeholder!
        if (dataModel instanceof StaceyKramersInitialPbModelET) {
            ((StaceyKramersInitialPbModelET) dataModel).saveSKParametersToFraction(initialPbModelMatrix, showAbsUnct);
            ((StaceyKramersInitialPbModelET) dataModel).saveSKRhoVarSysToFraction(//
                    initialPbModelMatrixRhoVar, initialPbModelMatrixRhoSys);
            ((StaceyKramersInitialPbModelET) dataModel).resetModelFromTripoliFraction();
        } else if (dataModel instanceof PlaceholderInitialPbModel) {
            ((PlaceholderInitialPbModel) dataModel).savePlaceHolderParametersToFraction(initialPbModelMatrix, showAbsUnct);
            ((PlaceholderInitialPbModel) dataModel).savePlaceHolderRhoVarSysToFraction(//
                    initialPbModelMatrixRhoVar, initialPbModelMatrixRhoSys);
        } else if (dataModel instanceof PlaceholderInitialPb76Model) {
            ((PlaceholderInitialPb76Model) dataModel).savePlaceHolderParametersToFraction(initialPbModelMatrix, showAbsUnct);
            ((PlaceholderInitialPb76Model) dataModel).savePlaceHolderRhoVarSysToFraction(//
                    initialPbModelMatrixRhoVar, initialPbModelMatrixRhoSys);
        }

        myInitView();
    }

    /**
     *
     */
    @Override
    public void updateModelView() {
        dataModel.refreshModel();

        ((ValueModelsMatrix) initialPbModelMatrix).initializeMatrixModelWithParameterModel(dataModel, showAbsUnct);
        initialPbModelMatrixView.rebuildTableModel();
        initialPbModelMatrixView.setLocation(LEFT_MARGIN + 15, 0);

        correlationVarUnctMatrixView.rebuildTableModel();
        correlationVarUnctMatrixView.setLocation(LEFT_MARGIN + initialPbModelMatrixView.getWidth() + 20, 0);
        correlationVarUnctMatrixView.showTableHideFirstColLastRow();

        correlationSysUnctMatrixView.rebuildTableModel();
        correlationSysUnctMatrixView.setLocation(LEFT_MARGIN + initialPbModelMatrixView.getWidth() + 255, 0);
        correlationSysUnctMatrixView.showTableHideFirstColLastRow();

        this.validate();
        initialPbModelMatrixView.repaint();

        fractionGridRow.updateCalculatedParameters(dataModel);
    }

    /**
     * @param showAbsUnct the showAbsUnct to set
     */
    public void setShowAbsUnct(boolean showAbsUnct) {
        this.showAbsUnct = showAbsUnct;
    }
}
