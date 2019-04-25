/*
 * IsochronsSelectorDialog.java
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
package org.earthtime.plots.evolution;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.earthtime.UPb_Redux.valueModels.SampleDateModel;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.beans.ET_JButton;
import org.earthtime.dialogs.DialogEditor;
import org.earthtime.plots.isochrons.IsochronModel;
import org.earthtime.utilities.CheckBoxList;

/**
 *
 * @author James F. Bowring
 */
public class InitialDelta234UEvolutionSelectorDialog extends DialogEditor {

    // Fields    
    private final ValueModel sampleDateModel;
    private double[] ar48icntrs;
    private boolean ar48icntrsDsplayAsDeltaUnits;

    private CheckBoxList initDelta234UCheckBoxes;
    private final boolean automaticInitDelta234USelection;

    /**
     * Creates new form IsochronsSelectorDialog
     *
     * @param parent
     * @param modal
     * @param sampleDateModel
     * @param selectedIsochrons
     */
    public InitialDelta234UEvolutionSelectorDialog(//
            java.awt.Frame parent,
            boolean modal,
            ValueModel sampleDateModel) {
        super(parent, modal);

        this.sampleDateModel = sampleDateModel;
        this.ar48icntrs = ((SampleDateModel) sampleDateModel).getAr48icntrs();
        if (this.ar48icntrs.length == 0) {
            ((SampleDateModel) sampleDateModel).setAr48icntrs(IsochronModel.generateDefaultEvolutionAr48icntrs());
            this.ar48icntrs = ((SampleDateModel) sampleDateModel).getAr48icntrs();
        }

        this.automaticInitDelta234USelection
                = ((SampleDateModel) sampleDateModel).isAutomaticInitDelta234USelection();

        this.ar48icntrsDsplayAsDeltaUnits
                = ((SampleDateModel) sampleDateModel).isAr48icntrsDsplayAsDeltaUnits();

        initComponents();

        delta234units.setText("\u03B4234U \u2030");
        if (ar48icntrsDsplayAsDeltaUnits) {
            delta234units.setSelected(true);
        } else {
            activityRatioUnits.setSelected(true);
        }

        automaticInitDelta234USelectionChoice.setSelected(automaticInitDelta234USelection);
        automaticInitDelta234USelectionChoice.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                ((SampleDateModel) sampleDateModel).setAutomaticInitDelta234USelection(
                        automaticInitDelta234USelectionChoice.isSelected());
            }
        });

        initLists();
    }

    private void initLists() {

        try {
            initDelta234U_LayeredPane.remove(initDelta234UCheckBoxes);
        } catch (Exception e) {
        }
        initDelta234UCheckBoxes = new CheckBoxList();
        initDelta234U_LayeredPane.add(initDelta234UCheckBoxes);

        JCheckBox[] isochronCheckBoxArray = new JCheckBox[ar48icntrs.length];

        for (int i = 0; i < ar48icntrs.length; i++) {
            String displayString = "";
            if (ar48icntrsDsplayAsDeltaUnits) {
                displayString = new BigDecimal((Math.abs(ar48icntrs[i]) - 1.0) * 1000.0).setScale(1, RoundingMode.HALF_UP).toPlainString();
            } else {
                displayString = new BigDecimal(Math.abs(ar48icntrs[i])).setScale(3, RoundingMode.HALF_UP).toPlainString();
            }

            JCheckBox checkBox = new JCheckBox(displayString);
            checkBox.setSelected(ar48icntrs[i] >= 0.0);
            // track index
            checkBox.setName(String.valueOf(i));
            checkBox.addChangeListener((ChangeEvent e) -> {
                int index = Integer.parseInt(((JCheckBox) e.getSource()).getName());
                ar48icntrs[index]
                        = ((JCheckBox) e.getSource()).isSelected() ? Math.abs(ar48icntrs[index]) : -1 * Math.abs(ar48icntrs[index]);
                initDelta234UAsRatioText.setText(Double.toString(ar48icntrs[index]));
            });
            isochronCheckBoxArray[i] = checkBox;
        }

        initDelta234UCheckBoxes.removeAll();
        initDelta234UCheckBoxes.setBounds(50, 75, 100, ar48icntrs.length * 18);
        initDelta234UCheckBoxes.setListData(isochronCheckBoxArray);
        initDelta234UCheckBoxes.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                initDelta234UAsRatioText.setText(
                        ((JCheckBox) ((CheckBoxList) e.getSource())
                                .getSelectedValue()).getText());
            }
        });

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        unitsChoiceButtonGroup = new javax.swing.ButtonGroup();
        buttonsPanel = new javax.swing.JPanel();
        close_button = new ET_JButton();
        initDelta234U_LayeredPane = new javax.swing.JLayeredPane();
        specifyInitDelta234U_label = new javax.swing.JLabel();
        availableListLabel = new javax.swing.JLabel();
        initDelta234UAsRatioText = new javax.swing.JTextField();
        specifyInitDelta234Ua_label = new javax.swing.JLabel();
        automaticInitDelta234USelectionChoice = new javax.swing.JCheckBox();
        addInitDelta234UButton = new ET_JButton();
        removeInitDelta234UButton = new ET_JButton();
        unitsChoiceLabel = new javax.swing.JLabel();
        delta234units = new javax.swing.JRadioButton();
        activityRatioUnits = new javax.swing.JRadioButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Isochron Chooser");
        setAlwaysOnTop(true);
        setBackground(new java.awt.Color(245, 236, 206));
        setForeground(java.awt.Color.white);
        setName(getTitle());
        setResizable(false);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        buttonsPanel.setBackground(new java.awt.Color(252, 236, 235));
        buttonsPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        buttonsPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        close_button.setForeground(new java.awt.Color(255, 51, 0));
        close_button.setText("Done");
        close_button.setMargin(new java.awt.Insets(0, 1, 0, 1));
        close_button.setPreferredSize(new java.awt.Dimension(140, 23));
        close_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                close_buttonActionPerformed(evt);
            }
        });
        buttonsPanel.add(close_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(35, 2, 270, 25));

        getContentPane().add(buttonsPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 575, 340, 27));

        initDelta234U_LayeredPane.setBackground(new java.awt.Color(204, 255, 255));
        initDelta234U_LayeredPane.setOpaque(true);

        specifyInitDelta234U_label.setText("Manage Init Delta 234U Evolution Curves:");
        initDelta234U_LayeredPane.add(specifyInitDelta234U_label);
        specifyInitDelta234U_label.setBounds(10, 10, 300, 16);

        availableListLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        availableListLabel.setText("Use CheckBoxes to toggle line visibility:");
        initDelta234U_LayeredPane.add(availableListLabel);
        availableListLabel.setBounds(20, 55, 260, 16);

        initDelta234UAsRatioText.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        initDelta234UAsRatioText.setText("0");
        initDelta234U_LayeredPane.add(initDelta234UAsRatioText);
        initDelta234UAsRatioText.setBounds(150, 450, 60, 26);

        specifyInitDelta234Ua_label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        specifyInitDelta234Ua_label.setText("Specify Init Delta 234U Curve in current units:");
        initDelta234U_LayeredPane.add(specifyInitDelta234Ua_label);
        specifyInitDelta234Ua_label.setBounds(20, 430, 310, 16);

        automaticInitDelta234USelectionChoice.setBackground(new java.awt.Color(204, 255, 255));
        automaticInitDelta234USelectionChoice.setSelected(true);
        automaticInitDelta234USelectionChoice.setText("Automatic Init Delta 234U Selection");
        automaticInitDelta234USelectionChoice.setOpaque(true);
        initDelta234U_LayeredPane.add(automaticInitDelta234USelectionChoice);
        automaticInitDelta234USelectionChoice.setBounds(30, 540, 260, 23);

        addInitDelta234UButton.setText("+");
        addInitDelta234UButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addInitDelta234UButtonActionPerformed(evt);
            }
        });
        initDelta234U_LayeredPane.add(addInitDelta234UButton);
        addInitDelta234UButton.setBounds(150, 480, 25, 25);

        removeInitDelta234UButton.setText("-");
        removeInitDelta234UButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeInitDelta234UButtonActionPerformed(evt);
            }
        });
        initDelta234U_LayeredPane.add(removeInitDelta234UButton);
        removeInitDelta234UButton.setBounds(180, 480, 25, 25);

        unitsChoiceLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        unitsChoiceLabel.setText("Choose display units:");
        initDelta234U_LayeredPane.add(unitsChoiceLabel);
        unitsChoiceLabel.setBounds(10, 33, 140, 16);

        unitsChoiceButtonGroup.add(delta234units);
        delta234units.setSelected(true);
        delta234units.setText("delta");
        delta234units.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delta234unitsActionPerformed(evt);
            }
        });
        initDelta234U_LayeredPane.add(delta234units);
        delta234units.setBounds(150, 30, 100, 23);

        unitsChoiceButtonGroup.add(activityRatioUnits);
        activityRatioUnits.setText("AR");
        activityRatioUnits.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                activityRatioUnitsActionPerformed(evt);
            }
        });
        initDelta234U_LayeredPane.add(activityRatioUnits);
        activityRatioUnits.setBounds(255, 30, 50, 23);

        getContentPane().add(initDelta234U_LayeredPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 370, 570));

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void close_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_close_buttonActionPerformed
        close();
    }//GEN-LAST:event_close_buttonActionPerformed

    private void addInitDelta234UButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addInitDelta234UButtonActionPerformed
        double initDelta234UAsRatio = 0;
        try {
            initDelta234UAsRatio = Double.parseDouble(initDelta234UAsRatioText.getText());
            if (ar48icntrsDsplayAsDeltaUnits) {
                // convert to activity ratio
                initDelta234UAsRatio = initDelta234UAsRatio / 1000. + 1.0;
            }
        } catch (NumberFormatException numberFormatException) {
        }

        if ((initDelta234UAsRatio > 0) && (initDelta234UAsRatio < 3)) {
            List<Double> arList = DoubleStream.of(ar48icntrs).boxed().collect(Collectors.toList());
            if (!arList.contains(initDelta234UAsRatio)) {
                arList.add(initDelta234UAsRatio);
            }
            Collections.sort(arList);
            ar48icntrs = new double[arList.size()];
            for (int i = 0; i < arList.size(); i++) {
                ar48icntrs[i] = arList.get(i);
            }
            ((SampleDateModel) sampleDateModel).setAr48icntrs(ar48icntrs);

            initLists();
        }

    }//GEN-LAST:event_addInitDelta234UButtonActionPerformed

    private void removeInitDelta234UButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeInitDelta234UButtonActionPerformed
        double initDelta234UAsRatio = 0;
        try {
            initDelta234UAsRatio = Double.parseDouble(initDelta234UAsRatioText.getText());
            if (ar48icntrsDsplayAsDeltaUnits) {
                // convert to activity ratio
                initDelta234UAsRatio = initDelta234UAsRatio / 1000. + 1.0;
            }
        } catch (NumberFormatException numberFormatException) {
        }

        if ((initDelta234UAsRatio > 0) && (initDelta234UAsRatio < 3)) {
            List<Double> arList = DoubleStream.of(ar48icntrs).boxed().collect(Collectors.toList());
            arList.remove(initDelta234UAsRatio);
            Collections.sort(arList);
            ar48icntrs = new double[arList.size()];
            for (int i = 0; i < arList.size(); i++) {
                ar48icntrs[i] = arList.get(i);
            }
            ((SampleDateModel) sampleDateModel).setAr48icntrs(ar48icntrs);

            initLists();
        }
    }//GEN-LAST:event_removeInitDelta234UButtonActionPerformed

    private void delta234unitsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delta234unitsActionPerformed
        ar48icntrsDsplayAsDeltaUnits = true;
        ((SampleDateModel) sampleDateModel).setAr48icntrsDsplayAsDeltaUnits(ar48icntrsDsplayAsDeltaUnits);
        initLists();
    }//GEN-LAST:event_delta234unitsActionPerformed

    private void activityRatioUnitsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_activityRatioUnitsActionPerformed
        ar48icntrsDsplayAsDeltaUnits = false;
        ((SampleDateModel) sampleDateModel).setAr48icntrsDsplayAsDeltaUnits(ar48icntrsDsplayAsDeltaUnits);
        initLists();
    }//GEN-LAST:event_activityRatioUnitsActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton activityRatioUnits;
    private javax.swing.JButton addInitDelta234UButton;
    private javax.swing.JCheckBox automaticInitDelta234USelectionChoice;
    private javax.swing.JLabel availableListLabel;
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JButton close_button;
    private javax.swing.JRadioButton delta234units;
    private javax.swing.JTextField initDelta234UAsRatioText;
    private javax.swing.JLayeredPane initDelta234U_LayeredPane;
    private javax.swing.JButton removeInitDelta234UButton;
    private javax.swing.JLabel specifyInitDelta234U_label;
    private javax.swing.JLabel specifyInitDelta234Ua_label;
    private javax.swing.ButtonGroup unitsChoiceButtonGroup;
    private javax.swing.JLabel unitsChoiceLabel;
    // End of variables declaration//GEN-END:variables

}
