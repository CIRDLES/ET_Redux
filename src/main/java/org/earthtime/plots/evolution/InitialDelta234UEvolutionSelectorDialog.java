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
            ((SampleDateModel)sampleDateModel).setAr48icntrs(IsochronModel.generateDefaultEvolutionAr48icntrs());
            this.ar48icntrs = ((SampleDateModel) sampleDateModel).getAr48icntrs();
        }
        

        this.automaticInitDelta234USelection
                = ((SampleDateModel) sampleDateModel).isAutomaticInitDelta234USelection();

        initComponents();
        
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

//        automaticInitDelta234USelectionChoice.setSelected(automaticInitDelta234USelection);
//        automaticInitDelta234USelectionChoice.addChangeListener(new ChangeListener() {
//            @Override
//            public void stateChanged(ChangeEvent e) {
//                ((SampleDateModel) sampleDateModel).setAutomaticInitDelta234USelection(
//                        automaticInitDelta234USelectionChoice.isSelected());
//            }
//        });

        JCheckBox[] isochronCheckBoxArray = new JCheckBox[ar48icntrs.length];

        for (int i = 0; i < ar48icntrs.length; i++) {
            JCheckBox checkBox = new JCheckBox(Double.toString(Math.abs(ar48icntrs[i])));
            checkBox.setSelected(ar48icntrs[i] >= 0.0);
            // track index
            checkBox.setName(String.valueOf(i));
            checkBox.addChangeListener((ChangeEvent e) -> {
                int index = Integer.parseInt(((JCheckBox) e.getSource()).getName());
                ar48icntrs[index]
                        = ((JCheckBox) e.getSource()).isSelected() ? Math.abs(ar48icntrs[index]) : -1 * Math.abs(ar48icntrs[index]);
                initDelta234UText.setText(Double.toString(ar48icntrs[index]));
            });
            isochronCheckBoxArray[i] = checkBox;
        }

        initDelta234UCheckBoxes.removeAll();
        initDelta234UCheckBoxes.setBounds(50, 50, 100, ar48icntrs.length * 18);
        initDelta234UCheckBoxes.setListData(isochronCheckBoxArray);
        initDelta234UCheckBoxes.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                initDelta234UText.setText(
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

        buttonsPanel = new javax.swing.JPanel();
        close_button = new ET_JButton();
        initDelta234U_LayeredPane = new javax.swing.JLayeredPane();
        specifyInitDelta234U_label = new javax.swing.JLabel();
        availableListLabel = new javax.swing.JLabel();
        initDelta234UText = new javax.swing.JTextField();
        specifyInitDelta234Ua_label = new javax.swing.JLabel();
        automaticInitDelta234USelectionChoice = new javax.swing.JCheckBox();
        addInitDelta234UButton = new ET_JButton();
        removeInitDelta234UButton = new ET_JButton();

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

        initDelta234U_LayeredPane.setBackground(new java.awt.Color(204, 204, 255));
        initDelta234U_LayeredPane.setOpaque(true);

        specifyInitDelta234U_label.setText("Manage Evolution Plot Init Delta 234U:");
        initDelta234U_LayeredPane.add(specifyInitDelta234U_label);
        specifyInitDelta234U_label.setBounds(10, 10, 300, 16);

        availableListLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        availableListLabel.setText("Use CheckBoxes to toggle line visibility:");
        initDelta234U_LayeredPane.add(availableListLabel);
        availableListLabel.setBounds(20, 30, 300, 16);

        initDelta234UText.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        initDelta234UText.setText("0");
        initDelta234U_LayeredPane.add(initDelta234UText);
        initDelta234UText.setBounds(242, 310, 60, 26);

        specifyInitDelta234Ua_label.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        specifyInitDelta234Ua_label.setText("Specify Init Delta 234U:");
        initDelta234U_LayeredPane.add(specifyInitDelta234Ua_label);
        specifyInitDelta234Ua_label.setBounds(190, 290, 148, 16);

        automaticInitDelta234USelectionChoice.setBackground(new java.awt.Color(204, 204, 255));
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
        addInitDelta234UButton.setBounds(245, 340, 25, 25);

        removeInitDelta234UButton.setText("-");
        removeInitDelta234UButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeInitDelta234UButtonActionPerformed(evt);
            }
        });
        initDelta234U_LayeredPane.add(removeInitDelta234UButton);
        removeInitDelta234UButton.setBounds(275, 340, 25, 25);

        getContentPane().add(initDelta234U_LayeredPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 340, 570));

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void close_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_close_buttonActionPerformed
        close();
    }//GEN-LAST:event_close_buttonActionPerformed

    private void addInitDelta234UButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addInitDelta234UButtonActionPerformed
        double initDelta234U = 0;
        try {
            initDelta234U = Double.parseDouble(initDelta234UText.getText());
        } catch (NumberFormatException numberFormatException) {
        }

        if ((initDelta234U > 0) && (initDelta234U < 3)) {
            List<Double> arList = DoubleStream.of(ar48icntrs).boxed().collect(Collectors.toList());
            arList.add(initDelta234U);
            Collections.sort(arList);
            ar48icntrs = new double[arList.size()];
            for (int i = 0; i < arList.size(); i ++){
                ar48icntrs[i] = arList.get(i);
            }
            ((SampleDateModel)sampleDateModel).setAr48icntrs(ar48icntrs);
            
            initLists();
        }

    }//GEN-LAST:event_addInitDelta234UButtonActionPerformed

    private void removeInitDelta234UButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeInitDelta234UButtonActionPerformed
       double initDelta234U = 0;
        try {
            initDelta234U = Double.parseDouble(initDelta234UText.getText());
        } catch (NumberFormatException numberFormatException) {
        }

        if ((initDelta234U > 0) && (initDelta234U < 3)) {
            List<Double> arList = DoubleStream.of(ar48icntrs).boxed().collect(Collectors.toList());
            arList.remove(initDelta234U);
            Collections.sort(arList);
            ar48icntrs = new double[arList.size()];
            for (int i = 0; i < arList.size(); i ++){
                ar48icntrs[i] = arList.get(i);
            }
            ((SampleDateModel)sampleDateModel).setAr48icntrs(ar48icntrs);
            
            initLists();
        }
    }//GEN-LAST:event_removeInitDelta234UButtonActionPerformed



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addInitDelta234UButton;
    private javax.swing.JCheckBox automaticInitDelta234USelectionChoice;
    private javax.swing.JLabel availableListLabel;
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JButton close_button;
    private javax.swing.JTextField initDelta234UText;
    private javax.swing.JLayeredPane initDelta234U_LayeredPane;
    private javax.swing.JButton removeInitDelta234UButton;
    private javax.swing.JLabel specifyInitDelta234U_label;
    private javax.swing.JLabel specifyInitDelta234Ua_label;
    // End of variables declaration//GEN-END:variables

}
