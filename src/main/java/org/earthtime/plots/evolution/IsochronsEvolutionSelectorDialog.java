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

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
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
public class IsochronsEvolutionSelectorDialog extends DialogEditor {

    // Fields    
    private final ValueModel sampleDateModel;
    private SortedSet<IsochronModel> selectedIsochrons;

    private CheckBoxList isochronCheckBoxes;
    private final boolean automaticIsochronSelection;

    /**
     * Creates new form IsochronsSelectorDialog
     *
     * @param parent
     * @param modal
     * @param sampleDateModel
     * @param selectedIsochrons
     */
    public IsochronsEvolutionSelectorDialog(//
            java.awt.Frame parent,
            boolean modal,
            ValueModel sampleDateModel) {
        super(parent, modal);

        this.sampleDateModel = sampleDateModel;
        this.selectedIsochrons = ((SampleDateModel) sampleDateModel).getIsochronModels();
        if (this.selectedIsochrons == null) {
            this.selectedIsochrons = new TreeSet<>();
        }

        this.automaticIsochronSelection
                = ((SampleDateModel) sampleDateModel).isAutomaticIsochronSelection();

        initComponents();

        automaticIsochronSelectionChoice.setSelected(automaticIsochronSelection);
        automaticIsochronSelectionChoice.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                ((SampleDateModel) sampleDateModel).setAutomaticIsochronSelection(
                        automaticIsochronSelectionChoice.isSelected());
            }
        });

        initLists();
    }

    private void initLists() {
        try {
            isochronsLayeredPane.remove(isochronCheckBoxes);
        } catch (Exception e) {
        }
        isochronCheckBoxes = new CheckBoxList();
        isochronsLayeredPane.add(isochronCheckBoxes);

//        automaticIsochronSelectionChoice.setSelected(automaticIsochronSelection);
//        automaticIsochronSelectionChoice.addChangeListener(new ChangeListener() {
//            @Override
//            public void stateChanged(ChangeEvent e) {
//                ((SampleDateModel) sampleDateModel).setAutomaticIsochronSelection(
//                        automaticIsochronSelectionChoice.isSelected());
//            }
//        });
        JCheckBox[] isochronCheckBoxArray = new JCheckBox[selectedIsochrons.size()];

        // used to get quick access to isochrons
        IsochronModel[] isochrons = new IsochronModel[selectedIsochrons.size()];
        int count = 0;
        Iterator<IsochronModel> isochronIterator = selectedIsochrons.iterator();
        while (isochronIterator.hasNext()) {
            IsochronModel isochronModel = isochronIterator.next();
            isochrons[count] = isochronModel;

            JCheckBox checkBox = new JCheckBox(isochronModel.prettyPrintI());
            checkBox.setSelected(isochronModel.isVisible());
            // track index
            checkBox.setName(String.valueOf(count));
            checkBox.addChangeListener((ChangeEvent e) -> {
                isochrons[Integer.parseInt(((JCheckBox) e.getSource()).getName())]
                        .setVisible(((JCheckBox) e.getSource()).isSelected());
                isochronDateInKaText.setText(
                        isochrons[Integer.parseInt(((JCheckBox) e.getSource()).getName())]
                                .prettyPrintINoUnits());
            });
            isochronCheckBoxArray[count] = checkBox;
            count++;
        }

        isochronCheckBoxes.removeAll();
        isochronCheckBoxes.setBounds(50, 50, 100, count * 18);
        isochronCheckBoxes.setListData(isochronCheckBoxArray);
        isochronCheckBoxes.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                isochronDateInKaText.setText(
                        ((JCheckBox) ((CheckBoxList) e.getSource())
                                .getSelectedValue()).getText().split(" ka")[0]);
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
        isochronsLayeredPane = new javax.swing.JLayeredPane();
        specifyIsochron_label = new javax.swing.JLabel();
        availableListLabel = new javax.swing.JLabel();
        isochronDateInKaText = new javax.swing.JTextField();
        specifyDateka_label = new javax.swing.JLabel();
        automaticIsochronSelectionChoice = new javax.swing.JCheckBox();
        addIsochronButton = new ET_JButton();
        removeIsochronButton = new ET_JButton();

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

        isochronsLayeredPane.setBackground(new java.awt.Color(255, 237, 255));
        isochronsLayeredPane.setOpaque(true);

        specifyIsochron_label.setText("Manage Evolution Plot Isochrons:");
        isochronsLayeredPane.add(specifyIsochron_label);
        specifyIsochron_label.setBounds(10, 10, 240, 16);

        availableListLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        availableListLabel.setText("Use CheckBoxes to toggle Isochron visibility:");
        isochronsLayeredPane.add(availableListLabel);
        availableListLabel.setBounds(20, 30, 300, 16);

        isochronDateInKaText.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        isochronDateInKaText.setText("0");
        isochronsLayeredPane.add(isochronDateInKaText);
        isochronDateInKaText.setBounds(242, 310, 60, 26);

        specifyDateka_label.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        specifyDateka_label.setText("Specify Date in ka:");
        isochronsLayeredPane.add(specifyDateka_label);
        specifyDateka_label.setBounds(210, 290, 120, 16);

        automaticIsochronSelectionChoice.setBackground(new java.awt.Color(255, 237, 255));
        automaticIsochronSelectionChoice.setSelected(true);
        automaticIsochronSelectionChoice.setText("Automatic Isochron Selection");
        automaticIsochronSelectionChoice.setOpaque(true);
        isochronsLayeredPane.add(automaticIsochronSelectionChoice);
        automaticIsochronSelectionChoice.setBounds(30, 540, 230, 23);

        addIsochronButton.setText("+");
        addIsochronButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addIsochronButtonActionPerformed(evt);
            }
        });
        isochronsLayeredPane.add(addIsochronButton);
        addIsochronButton.setBounds(245, 340, 25, 25);

        removeIsochronButton.setText("-");
        removeIsochronButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeIsochronButtonActionPerformed(evt);
            }
        });
        isochronsLayeredPane.add(removeIsochronButton);
        removeIsochronButton.setBounds(275, 340, 25, 25);

        getContentPane().add(isochronsLayeredPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 340, 570));

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void close_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_close_buttonActionPerformed
        close();
    }//GEN-LAST:event_close_buttonActionPerformed

    private void addIsochronButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addIsochronButtonActionPerformed
        double isoDateKa = 0;
        try {
            isoDateKa = Double.parseDouble(isochronDateInKaText.getText());
        } catch (NumberFormatException numberFormatException) {
        }

        if ((isoDateKa > 0) && (isoDateKa < 10000)) {
            IsochronModel model = new IsochronModel(isoDateKa * 1000);
            model.setVisible(true);
            selectedIsochrons.add(model);
            initLists();
        }

    }//GEN-LAST:event_addIsochronButtonActionPerformed

    private void removeIsochronButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeIsochronButtonActionPerformed
        double isoDateKa = 0;
        try {
            isoDateKa = Double.parseDouble(isochronDateInKaText.getText());
        } catch (NumberFormatException numberFormatException) {
        }

        if ((isoDateKa > 0) && (isoDateKa < 10000)) {
            IsochronModel model = new IsochronModel(isoDateKa * 1000);
            selectedIsochrons.remove(model);
            initLists();
        }
    }//GEN-LAST:event_removeIsochronButtonActionPerformed

    /**
     * @return the selectedIsochrons
     */
    public SortedSet<IsochronModel> getSelectedIsochrons() {
        return selectedIsochrons;
    }

    /**
     * @param selectedIsochrons the selectedIsochrons to set
     */
    public void setSelectedIsochrons(SortedSet<IsochronModel> selectedIsochrons) {
        this.selectedIsochrons = selectedIsochrons;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addIsochronButton;
    private javax.swing.JCheckBox automaticIsochronSelectionChoice;
    private javax.swing.JLabel availableListLabel;
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JButton close_button;
    private javax.swing.JTextField isochronDateInKaText;
    private javax.swing.JLayeredPane isochronsLayeredPane;
    private javax.swing.JButton removeIsochronButton;
    private javax.swing.JLabel specifyDateka_label;
    private javax.swing.JLabel specifyIsochron_label;
    // End of variables declaration//GEN-END:variables

}
