/*
 * Copyright 2019 CIRDLES.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.earthtime.plots.evolution.seaWater;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import org.earthtime.ETReduxFrame;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.UTh_Redux.fractions.UThFractionI;
import org.earthtime.UTh_Redux.fractions.UThLegacyFraction;
import org.earthtime.UTh_Redux.fractions.UThLegacyFractionI;
import org.earthtime.UTh_Redux.fractions.fractionReduction.UThFractionReducer;
import org.earthtime.beans.ET_JButton;
import static org.earthtime.dataDictionaries.SampleAnalysisTypesEnum.USERIES_CARB;
import org.earthtime.dataDictionaries.SampleTypesEnum;
import org.earthtime.dialogs.DialogEditor;
import org.earthtime.fractions.ETFractionInterface;
import org.earthtime.projects.ProjectSample;
import org.earthtime.samples.SampleInterface;

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class SeawaterModelAssignmentManager extends DialogEditor {

    private SampleInterface projectSample;
    private ETReduxFrame parentFrame;

    /**
     * @return the projectSample
     */
    public SampleInterface getProjectSample() {
        return projectSample;
    }

    /**
     * @param projectSample the projectSample to set
     */
    public void setProjectSample(SampleInterface projectSample) {
        this.projectSample = projectSample;
    }

    private static final int FRACTION_COL_X = 15;
    private static final int SEAWATER_COL_X = 150;
    private static final int PCTLOSS_COL_X = 400;
    private static final int PCTLOSSUNCT_COL_X = 500;
    private static final int START_DATA_Y = 120;
    private static int DISPLAY_WIDTH = 600;

    private Vector<ETFractionInterface> fractions;
    private JLabel[] fractionNames;
    private JComboBox<SeaWaterInitialDelta234UTableModel>[] seawaterModelChoices;
    private JTextField[] pctLoss;
    private JTextField[] pctLossUnct;

    /**
     * Creates new form OpenSystemModelsManager
     */
    private SeawaterModelAssignmentManager() throws BadLabDataException {
        this(null, true, null);
    }

    public SeawaterModelAssignmentManager(java.awt.Frame parent,
            boolean modal, SampleInterface projectSample) throws HeadlessException {
        super(parent, modal);

        this.parentFrame = (ETReduxFrame) parent;
        this.projectSample = projectSample;

        initComponents();

        fractions = projectSample.getFractions();
        Collections.sort(fractions, new Comparator<ETFractionInterface>() {
            @Override
            public int compare(ETFractionInterface f1, ETFractionInterface f2) {
                return Integer.compare(f1.getAliquotNumber(), f2.getAliquotNumber());
            }
        });

        fractionNames = new JLabel[projectSample.getFractions().size()];
        seawaterModelChoices = new JComboBox[projectSample.getFractions().size()];
        pctLoss = new JTextField[projectSample.getFractions().size()];
        pctLossUnct = new JTextField[projectSample.getFractions().size()];

        initFractionsDisplay();
    }

    private void initFractionsDisplay() {
        setSize(DISPLAY_WIDTH + 25, 500);
        setTitle("Assign Seawater Models to fractions");
        setAlwaysOnTop(true);

        JPanel modelsPanel = new JPanel(null);
        modelsPanel.setSize(DISPLAY_WIDTH, 250);
        modelsPanel.setBackground(new Color(240, 255, 255));

        JLabel fractionHeaderLabel = new JLabel("Fraction");
        fractionHeaderLabel.setBounds(FRACTION_COL_X, 15, 100, 25);
        modelsPanel.add(fractionHeaderLabel);

        JLabel seawaterHeaderLabel = new JLabel("Seawater Model");
        seawaterHeaderLabel.setBounds(SEAWATER_COL_X, 15, 100, 25);
        modelsPanel.add(seawaterHeaderLabel);

        JLabel pctLossHeaderLabel = new JLabel("% Loss");
        pctLossHeaderLabel.setBounds(PCTLOSS_COL_X, 15, 100, 25);
        modelsPanel.add(pctLossHeaderLabel);

        JTextField pctLossFillValue = new JTextField();
        pctLossFillValue.setDocument(new DoubleDocument(pctLossFillValue, true));
        pctLossFillValue.setHorizontalAlignment(JTextField.RIGHT);
        pctLossFillValue.setText("2.5");
        pctLossFillValue.setBounds(PCTLOSS_COL_X, 40, 50, 25);
        modelsPanel.add(pctLossFillValue);

        JButton pctLossFillButton = new ET_JButton("Fill");
        pctLossFillButton.setBounds(PCTLOSS_COL_X, 65, 40, 25);
        pctLossFillButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                double pctLossValue = (double) Double.parseDouble(pctLossFillValue.getText());
                for (int i = 0; i < fractions.size(); i++) {
                    ((UThFractionI) fractions.get(i)).getPctLoss().setValue(pctLossValue);
                    pctLoss[i].setText(String.valueOf(pctLossValue));
                }
                parentFrame.updateReportTable();
            }
        });
        modelsPanel.add(pctLossFillButton);

        JLabel pctLossUnctHeaderLabel = new JLabel("1-sigma abs");
        pctLossUnctHeaderLabel.setBounds(PCTLOSSUNCT_COL_X, 15, 100, 25);
        modelsPanel.add(pctLossUnctHeaderLabel);

        JTextField pctLossUnctFillValue = new JTextField();
        pctLossUnctFillValue.setDocument(new DoubleDocument(pctLossUnctFillValue, true));
        pctLossUnctFillValue.setHorizontalAlignment(JTextField.RIGHT);
        pctLossUnctFillValue.setText("1.0");
        pctLossUnctFillValue.setBounds(PCTLOSSUNCT_COL_X, 40, 50, 25);
        modelsPanel.add(pctLossUnctFillValue);

        List<SeaWaterInitialDelta234UTableModel> seawaterModelsList = projectSample.getSeaWaterInitialDelta234UTableModels();

        int rowY = 0;
        for (int i = 0; i < projectSample.getFractions().size(); i++) {
            rowY = START_DATA_Y + i * 25;
            fractionNames[i] = new JLabel(projectSample.getFractions().get(i).getFractionID());
            fractionNames[i].setBounds(FRACTION_COL_X, rowY, 100, 25);
            modelsPanel.add(fractionNames[i]);

            seawaterModelChoices[i]
                    = new JComboBox<>(
                            seawaterModelsList.toArray(new SeaWaterInitialDelta234UTableModel[seawaterModelsList.size()]));
            seawaterModelChoices[i].setBounds(SEAWATER_COL_X, rowY, 200, 25);
            seawaterModelChoices[i].setSelectedItem(((UThFractionI) projectSample.getFractions().get(i)).getSeaWaterInitialDelta234UTableModel());
            seawaterModelChoices[i].addActionListener(
                    new SeawaterModelActionListener(((UThFractionI) projectSample.getFractions().get(i))));
            modelsPanel.add(seawaterModelChoices[i]);

            pctLoss[i] = new JTextField();
            pctLoss[i].setDocument(new DoubleDocument(pctLoss[i], true));
            pctLoss[i].setHorizontalAlignment(JTextField.RIGHT);
            pctLoss[i].setText(String.valueOf(((UThFractionI) projectSample.getFractions().get(i)).getPctLoss().getValue().doubleValue()));
            pctLoss[i].setBounds(PCTLOSS_COL_X, rowY, 50, 25);
            pctLoss[i].addFocusListener(
                    new PctLossFocusListener(((UThFractionI) projectSample.getFractions().get(i))));
            modelsPanel.add(pctLoss[i]);

            pctLossUnct[i] = new JTextField();
            pctLossUnct[i].setDocument(new DoubleDocument(pctLossUnct[i], true));
            pctLossUnct[i].setHorizontalAlignment(JTextField.RIGHT);
            pctLossUnct[i].setText(String.valueOf(((UThFractionI) projectSample.getFractions().get(i)).getPctLoss().getOneSigmaAbs().doubleValue()));
            pctLossUnct[i].setBounds(PCTLOSSUNCT_COL_X, rowY, 50, 25);
            pctLossUnct[i].addFocusListener(
                    new PctLossUnctFocusListener(((UThFractionI) projectSample.getFractions().get(i))));
            modelsPanel.add(pctLossUnct[i]);

        }

        JButton okButton = new ET_JButton("OK");
        okButton.setBounds(FRACTION_COL_X, rowY + 50, 500, 25);
        okButton.addActionListener((ActionEvent e) -> {
            parentFrame.updateReportTable();
            dispose();
        });
        modelsPanel.add(okButton);

        modelsPanel.setPreferredSize(new Dimension(DISPLAY_WIDTH, rowY + 100));
        JScrollPane modelsScroll = new JScrollPane(modelsPanel);
        modelsScroll.setSize(DISPLAY_WIDTH, rowY + 100);

        setContentPane(modelsScroll);
    }

    class SeawaterModelActionListener implements ActionListener {

        private UThFractionI fraction;

        public SeawaterModelActionListener(UThFractionI fraction) {
            this.fraction = fraction;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            SeaWaterInitialDelta234UTableModel model = (SeaWaterInitialDelta234UTableModel) ((JComboBox) e.getSource()).getSelectedItem();
            fraction.setSeaWaterInitialDelta234UTableModel(model);
            UThFractionReducer.reduceFraction((UThLegacyFractionI) fraction, false);
            parentFrame.updateReportTable();
        }
    }

    class PctLossFocusListener implements FocusListener {

        private UThFractionI fraction;

        public PctLossFocusListener(UThFractionI fraction) {
            this.fraction = fraction;
        }

        @Override
        public void focusGained(FocusEvent e) {
            // do nothing
        }

        @Override
        public void focusLost(FocusEvent e) {
            double pctLossValue = (double) Double.parseDouble(((JTextField) e.getSource()).getText());
            fraction.getPctLoss().setValue(pctLossValue);
            UThFractionReducer.reduceFraction((UThLegacyFractionI) fraction, false);
            parentFrame.updateReportTable();
        }
    }

    class PctLossUnctFocusListener implements FocusListener {

        private UThFractionI fraction;

        public PctLossUnctFocusListener(UThFractionI fraction) {
            this.fraction = fraction;
        }

        @Override
        public void focusGained(FocusEvent e) {
            // do nothing
        }

        @Override
        public void focusLost(FocusEvent e) {
            double pctLossUnct = (double) Double.parseDouble(((JTextField) e.getSource()).getText());
            fraction.getPctLoss().setOneSigma(pctLossUnct);
            UThFractionReducer.reduceFraction((UThLegacyFractionI) fraction, false);
            parentFrame.updateReportTable();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(204, 255, 255));
        getContentPane().setLayout(null);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SeawaterModelAssignmentManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SeawaterModelAssignmentManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SeawaterModelAssignmentManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SeawaterModelAssignmentManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            try {

                ProjectSample projectSample = new ProjectSample(
                        SampleTypesEnum.PROJECT.getName(),
                        SampleTypesEnum.COMPILATION.getName(),
                        USERIES_CARB.getName(),
                        ReduxConstants.ANALYSIS_PURPOSE.NONE,
                        true,
                        USERIES_CARB.getIsotypeSystem(),
                        USERIES_CARB.getDefaultReportSpecsType());

                UThLegacyFractionI myFraction = new UThLegacyFraction();
                myFraction.setFractionID("TEST Fraction");
                projectSample.addFraction(myFraction);

                SeawaterModelAssignmentManager test = new SeawaterModelAssignmentManager(null, true, projectSample);
                test.setVisible(true);
            } catch (BadLabDataException badLabDataException) {
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
