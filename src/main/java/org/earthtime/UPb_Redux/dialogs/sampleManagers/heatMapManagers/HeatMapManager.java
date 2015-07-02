/*
 * HeatMapManager.java
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
package org.earthtime.UPb_Redux.dialogs.sampleManagers.heatMapManagers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JRadioButton;
import static javax.swing.SwingConstants.CENTER;
import static javax.swing.SwingConstants.LEFT;
import static javax.swing.SwingConstants.RIGHT;
import static org.earthtime.UPb_Redux.ReduxConstants.sansSerif_11_Plain;
import static org.earthtime.UPb_Redux.ReduxConstants.sansSerif_12_Plain;
import org.earthtime.UPb_Redux.dateInterpretation.DateInterpretationUpdateViewI;
import org.earthtime.UPb_Redux.dialogs.DialogEditor;
import org.earthtime.UPb_Redux.reports.ReportCategory;
import org.earthtime.UPb_Redux.user.SampleDateInterpretationGUIOptions;
import org.earthtime.UPb_Redux.utilities.BrowserControl;
import org.earthtime.beans.ET_JButton;
import org.earthtime.colorViews.HeatMapMapper;
import org.earthtime.colorViews.HeatMapView;
import org.earthtime.reports.ReportCategoryInterface;
import org.earthtime.reports.ReportColumnInterface;
import org.earthtime.reports.ReportSettingsInterface;
import org.earthtime.samples.SampleInterface;

/**
 *
 * @author James F. Bowring
 */
public class HeatMapManager extends DialogEditor {

    private static final int WIDTH_OF_FORM = 765;
    private final HeatMapView heatMapView;
    private final SampleInterface sample;
    private final Map<String, ReportColumnInterface> mapOfCategoryColumns;
    private final Map<String, JRadioButton> mapOfCategoryButtons;
    private final ButtonGroup fieldsButtonGroup;
    private final DateInterpretationUpdateViewI dateInterpretationView;
    private JLabel minLabel;
    private JLabel maxLabel;
    private JLabel variableLabel;
    private JCheckBox toggleHeatActiveCheckBox;
    private boolean heatMapActive;
    private ReportColumnInterface selectedReportColumn;
    private final ReportSettingsInterface reportSettings;
    private Map<String, String> heatMapOptions;

    /**
     * Creates new form HeatMapDialog
     *
     * @param parent
     * @param modal
     * @param aSample
     * @param dateInterpretationView the value of dateInterpretationView
     */
    public HeatMapManager(java.awt.Frame parent, boolean modal, SampleInterface aSample, DateInterpretationUpdateViewI dateInterpretationView) {
        super(parent, modal);

        this.sample = aSample;

        this.reportSettings = sample.getReportSettingsModel();

        this.heatMapOptions =//
                sample.getSampleDateInterpretationGUISettings().getHeatMapOptions();

        this.heatMapActive = Boolean.valueOf(getStringEntryFromHeatMapOptions("activateHeatMap", "false"));

        this.dateInterpretationView = dateInterpretationView;

        initComponents();

        this.setBackground(Color.white);
        setSize(WIDTH_OF_FORM, 750);

        heatMapView = new HeatMapView();
        heatMapView.setBounds(40, 20, WIDTH_OF_FORM - 80, 30);
        heatMapDetailsPanel.add(heatMapView, JLayeredPane.DEFAULT_LAYER);

        validate();

        fieldsButtonGroup = new ButtonGroup();

        labelsFactory();
        buttonsFactory();

        mapOfCategoryColumns = new HashMap<>();
        mapOfCategoryButtons = new HashMap<>();
        compositionCategoryChoosersSetUp();
        ratiosCategoryChoosersSetUp();
        rhosCategoryChoosersSetUp();
        ratiosPbcCorrCategoryChoosersSetUp();
        datesCategoryChoosersSetUp();
        datesPbcCorrCategoryChoosersSetUp();
        tracerElementChoosersSetUp();

        String selectedColumnName = getStringEntryFromHeatMapOptions("reportColumnDisplayName", fieldsButtonGroup.getElements().nextElement().getActionCommand());
        if (selectedColumnName.length() == 0) {
            selectedColumnName = fieldsButtonGroup.getElements().nextElement().getActionCommand();
        }

        // check if exists else use first found
        if (mapOfCategoryButtons.containsKey(selectedColumnName)) {
            mapOfCategoryButtons.get(selectedColumnName).setSelected(true);
        } else {
            try {
                fieldsButtonGroup.getElements().nextElement().setSelected(true);
            } catch (Exception e) {
            }
        }

    }

    private String getStringEntryFromHeatMapOptions(String key, String value) {
        if (heatMapOptions.containsKey(key)) {
            return heatMapOptions.get(key);
        } else {
            return value;
        }
    }

    private void labelsFactory() {
        minLabel = new JLabel("min");
        minLabel.setBounds(15, 50, 150, 25);
        minLabel.setFont(sansSerif_12_Plain);
        minLabel.setHorizontalAlignment(LEFT);
        heatMapDetailsPanel.add(minLabel);

        maxLabel = new JLabel("max");
        maxLabel.setBounds(getWidth() - 170, 50, 150, 25);
        maxLabel.setFont(sansSerif_12_Plain);
        maxLabel.setHorizontalAlignment(RIGHT);
        heatMapDetailsPanel.add(maxLabel);

        variableLabel = new JLabel("var");
        variableLabel.setBounds(getWidth() / 2 - 110, 50, 220, 25);
        variableLabel.setFont(sansSerif_12_Plain);
        variableLabel.setHorizontalAlignment(CENTER);
        heatMapDetailsPanel.add(variableLabel);

    }

    private void buttonsFactory() {
        toggleHeatActiveCheckBox = new JCheckBox("Activate Heat Map");
        toggleHeatActiveCheckBox.setBounds(50, 100, WIDTH_OF_FORM - 100, 25);
        toggleHeatActiveCheckBox.setFont(sansSerif_12_Plain);
        toggleHeatActiveCheckBox.setSelected(heatMapActive);
        toggleHeatActiveCheckBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    heatMapActive = true;
                } else {
                    heatMapActive = false;
                }
                determineFractionHeat();

                heatMapOptions.put("activateHeatMap", Boolean.toString(heatMapActive));
            }
        });

        heatMapDetailsPanel.add(toggleHeatActiveCheckBox);

    }

    private void compositionCategoryChoosersSetUp() {
        ReportCategoryInterface reportCategory = reportSettings.getCompositionCategory();

        fieldChooserFactory(reportCategory, compositionPanel);
    }

    private void datesCategoryChoosersSetUp() {
        ReportCategoryInterface reportCategory = reportSettings.getDatesCategory();

        fieldChooserFactory(reportCategory, datesPanel);
    }

    private void datesPbcCorrCategoryChoosersSetUp() {
        ReportCategoryInterface reportCategory = reportSettings.getDatesPbcCorrCategory();

        fieldChooserFactory(reportCategory, datesPbcCorrPanel);
    }

    private void ratiosCategoryChoosersSetUp() {
        ReportCategoryInterface reportCategory = reportSettings.getIsotopicRatiosCategory();

        fieldChooserFactory(reportCategory, ratiosPanel);
    }

    private void ratiosPbcCorrCategoryChoosersSetUp() {
        ReportCategoryInterface reportCategory = reportSettings.getIsotopicRatiosPbcCorrCategory();

        fieldChooserFactory(reportCategory, ratiosPbcCorrPanel);
    }

    private void rhosCategoryChoosersSetUp() {
        ReportCategoryInterface reportCategory = reportSettings.getRhosCategory();

        fieldChooserFactory(reportCategory, rhosPanel);
    }

    private void tracerElementChoosersSetUp() {
        ReportCategoryInterface reportCategory = reportSettings.getTraceElementsCategory();

        fieldChooserFactory(reportCategory, traceElementsPanel);
    }

    private void fieldChooserFactory(ReportCategoryInterface reportCategory, JLayeredPane categoryPanel) {

        ReportColumnInterface[] reportColumns = reportCategory.getCategoryColumns();

        JLabel headerLabel = new JLabel(reportCategory.getDisplayName() + " Variables Showing in Report Table");
        headerLabel.setBounds(5, 0, WIDTH_OF_FORM - 10, 25);
        categoryPanel.add(headerLabel);

        int layoutRow = 1;
        for (ReportColumnInterface reportColumn : reportColumns) {
            if (reportColumn.isVisible()) {
                final String varDisplayName = reportColumn.getDisplayName();
                final String varName = reportColumn.getRetrieveVariableName();

                mapOfCategoryColumns.put(varName, reportColumn);
                JRadioButton btn = new JRadioButton(varDisplayName);
                btn.setActionCommand(varName);
                btn.setBounds(10, layoutRow * 20, 300, 25);
                btn.setFont(sansSerif_11_Plain);
                btn.addItemListener(new ItemListener() {

                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        if (e.getStateChange() == ItemEvent.SELECTED) {
                            JRadioButton btn = ((JRadioButton) e.getSource());
                            selectedReportColumn = mapOfCategoryColumns.get(btn.getActionCommand());
                            determineFractionHeat();
                            heatMapOptions.put("reportColumnDisplayName", varName);
                        }
                    }
                });
                fieldsButtonGroup.add(btn);
                mapOfCategoryButtons.put(varName, btn);

                categoryPanel.add(btn, JLayeredPane.DEFAULT_LAYER);

                // oct add in flag for showing uncertainty
                try {
                    if (reportColumn.getUncertaintyColumn().isVisible()) {
                        String varNameUnct = varName + "." + reportColumn.getUncertaintyColumn().getUncertaintyType();
                        mapOfCategoryColumns.put(varNameUnct, reportColumn.getUncertaintyColumn());
                        btn = new JRadioButton(reportColumn.getUncertaintyColumn().getDisplayName() + " for " + reportColumn.getDisplayName());
                        btn.setActionCommand(varNameUnct);
                        btn.setBounds(325, layoutRow * 20, 300, 25);
                        btn.setFont(sansSerif_11_Plain);
                        btn.addItemListener(new ItemListener() {

                            @Override
                            public void itemStateChanged(ItemEvent e) {
                                if (e.getStateChange() == ItemEvent.SELECTED) {
                                    JRadioButton btn = ((JRadioButton) e.getSource());
                                    selectedReportColumn = mapOfCategoryColumns.get(btn.getActionCommand());
                                    determineFractionHeat();
                                    heatMapOptions.put("reportColumnDisplayName", varNameUnct);
                                }
                            }
                        });
                        fieldsButtonGroup.add(btn);
                        mapOfCategoryButtons.put(varNameUnct, btn);

                        categoryPanel.add(btn, JLayeredPane.DEFAULT_LAYER);

                    }
                } catch (Exception e) {
                }

                layoutRow++;
            }
        }
    }

    private void determineFractionHeat() {
        System.out.println("button getter " + selectedReportColumn.getRetrieveVariableName());

        HeatMapMapper heatMapMapper = new HeatMapMapper(sample, selectedReportColumn);
        ArrayList<Integer> fractionRGBs = heatMapMapper.determineFractionHeat(heatMapActive);

        double min = heatMapMapper.getMin();
        double max = heatMapMapper.getMax();

        // send indexes to stripe the heatmap
        heatMapView.setFractionRGBs(fractionRGBs);
        heatMapView.repaint();

        System.out.println("  min " + min + "   max " + max);

        NumberFormat formatter = new DecimalFormat("0.0000E0");
        minLabel.setText(formatter.format(min));
        maxLabel.setText(formatter.format(max));
        
        variableLabel.setText(selectedReportColumn.getDisplayName());

        if (selectedReportColumn.getRetrieveMethodName().contains("Date")) {
            variableLabel.setText("date: " + selectedReportColumn.getDisplayName());
        }

        if (selectedReportColumn.getRetrieveMethodName().contains("Ratio")) {
            variableLabel.setText("ratio: " + selectedReportColumn.getDisplayName());
        }

        if (selectedReportColumn.getRetrieveMethodName().contains("Element")) {
            variableLabel.setText("conc of: " + selectedReportColumn.getDisplayName());
        }

        dateInterpretationView.repaintActiveTab();

    }

    @Override
    public void setSize(int width, int height) {
        if (BrowserControl.isMacOS()) {
            super.setSize(width, height);
        }
        if (BrowserControl.isWindowsPlatform()) {
            super.setSize(width, height + 25);
        }

        //Get the screen size
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();

        //Calculate the frame location
        int x = (screenSize.width - getWidth()) / 2;
        int y = (screenSize.height - getHeight()) / 2;

        //Set the new frame location
        setLocation(x, y);

        heatMapPanel.setSize(width, height);
        heatMapDetailsPanel.setBounds(0, 0, width, 150);
        variablesChart_tabbedPane.setBounds(2, 150, width - 4, 350);//height - 175);

        doneButton.setBounds(10, 500, width - 20, 25);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        heatMapPanel = new javax.swing.JLayeredPane();
        heatMapDetailsPanel = new javax.swing.JLayeredPane();
        variablesChart_tabbedPane = new javax.swing.JTabbedPane();
        compositionPanel = new javax.swing.JLayeredPane();
        ratiosPanel = new javax.swing.JLayeredPane();
        rhosPanel = new javax.swing.JLayeredPane();
        ratiosPbcCorrPanel = new javax.swing.JLayeredPane();
        datesPanel = new javax.swing.JLayeredPane();
        datesPbcCorrPanel = new javax.swing.JLayeredPane();
        traceElementsPanel = new javax.swing.JLayeredPane();
        customPanel = new javax.swing.JLayeredPane();
        doneButton =  new ET_JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(java.awt.Color.white);
        getContentPane().setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        heatMapPanel.setBackground(new java.awt.Color(255, 255, 255));
        heatMapPanel.setOpaque(true);
        heatMapPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        heatMapDetailsPanel.setBackground(new java.awt.Color(255, 255, 255));
        heatMapDetailsPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        heatMapDetailsPanel.setOpaque(true);

        javax.swing.GroupLayout heatMapDetailsPanelLayout = new javax.swing.GroupLayout(heatMapDetailsPanel);
        heatMapDetailsPanel.setLayout(heatMapDetailsPanelLayout);
        heatMapDetailsPanelLayout.setHorizontalGroup(
            heatMapDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 758, Short.MAX_VALUE)
        );
        heatMapDetailsPanelLayout.setVerticalGroup(
            heatMapDetailsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 148, Short.MAX_VALUE)
        );

        heatMapPanel.add(heatMapDetailsPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 760, -1));

        variablesChart_tabbedPane.setBackground(new java.awt.Color(255, 255, 255));
        variablesChart_tabbedPane.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        variablesChart_tabbedPane.setOpaque(true);

        javax.swing.GroupLayout compositionPanelLayout = new javax.swing.GroupLayout(compositionPanel);
        compositionPanel.setLayout(compositionPanelLayout);
        compositionPanelLayout.setHorizontalGroup(
            compositionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 739, Short.MAX_VALUE)
        );
        compositionPanelLayout.setVerticalGroup(
            compositionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 487, Short.MAX_VALUE)
        );

        variablesChart_tabbedPane.addTab("Compostion", compositionPanel);

        javax.swing.GroupLayout ratiosPanelLayout = new javax.swing.GroupLayout(ratiosPanel);
        ratiosPanel.setLayout(ratiosPanelLayout);
        ratiosPanelLayout.setHorizontalGroup(
            ratiosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 739, Short.MAX_VALUE)
        );
        ratiosPanelLayout.setVerticalGroup(
            ratiosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 487, Short.MAX_VALUE)
        );

        variablesChart_tabbedPane.addTab("Ratios", ratiosPanel);

        javax.swing.GroupLayout rhosPanelLayout = new javax.swing.GroupLayout(rhosPanel);
        rhosPanel.setLayout(rhosPanelLayout);
        rhosPanelLayout.setHorizontalGroup(
            rhosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 739, Short.MAX_VALUE)
        );
        rhosPanelLayout.setVerticalGroup(
            rhosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 487, Short.MAX_VALUE)
        );

        variablesChart_tabbedPane.addTab("rhos", rhosPanel);

        javax.swing.GroupLayout ratiosPbcCorrPanelLayout = new javax.swing.GroupLayout(ratiosPbcCorrPanel);
        ratiosPbcCorrPanel.setLayout(ratiosPbcCorrPanelLayout);
        ratiosPbcCorrPanelLayout.setHorizontalGroup(
            ratiosPbcCorrPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 739, Short.MAX_VALUE)
        );
        ratiosPbcCorrPanelLayout.setVerticalGroup(
            ratiosPbcCorrPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 487, Short.MAX_VALUE)
        );

        variablesChart_tabbedPane.addTab("Ratios PbcCorr", ratiosPbcCorrPanel);

        javax.swing.GroupLayout datesPanelLayout = new javax.swing.GroupLayout(datesPanel);
        datesPanel.setLayout(datesPanelLayout);
        datesPanelLayout.setHorizontalGroup(
            datesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 739, Short.MAX_VALUE)
        );
        datesPanelLayout.setVerticalGroup(
            datesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 487, Short.MAX_VALUE)
        );

        variablesChart_tabbedPane.addTab("Dates", datesPanel);

        javax.swing.GroupLayout datesPbcCorrPanelLayout = new javax.swing.GroupLayout(datesPbcCorrPanel);
        datesPbcCorrPanel.setLayout(datesPbcCorrPanelLayout);
        datesPbcCorrPanelLayout.setHorizontalGroup(
            datesPbcCorrPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 739, Short.MAX_VALUE)
        );
        datesPbcCorrPanelLayout.setVerticalGroup(
            datesPbcCorrPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 487, Short.MAX_VALUE)
        );

        variablesChart_tabbedPane.addTab("Dates PbcCorr", datesPbcCorrPanel);

        javax.swing.GroupLayout traceElementsPanelLayout = new javax.swing.GroupLayout(traceElementsPanel);
        traceElementsPanel.setLayout(traceElementsPanelLayout);
        traceElementsPanelLayout.setHorizontalGroup(
            traceElementsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 739, Short.MAX_VALUE)
        );
        traceElementsPanelLayout.setVerticalGroup(
            traceElementsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 487, Short.MAX_VALUE)
        );

        variablesChart_tabbedPane.addTab("Trace Elements", traceElementsPanel);

        javax.swing.GroupLayout customPanelLayout = new javax.swing.GroupLayout(customPanel);
        customPanel.setLayout(customPanelLayout);
        customPanelLayout.setHorizontalGroup(
            customPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 739, Short.MAX_VALUE)
        );
        customPanelLayout.setVerticalGroup(
            customPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 487, Short.MAX_VALUE)
        );

        variablesChart_tabbedPane.addTab("Custom Ratios", customPanel);

        heatMapPanel.add(variablesChart_tabbedPane, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 150, 760, 530));

        doneButton.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        doneButton.setText("Done");
        doneButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                doneButtonActionPerformed(evt);
            }
        });
        heatMapPanel.add(doneButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 690, 760, 25));

        getContentPane().add(heatMapPanel, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, -1, 720));

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void doneButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_doneButtonActionPerformed
        close();
    }//GEN-LAST:event_doneButtonActionPerformed

//    /**
//     * @param args the command line arguments
//     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(HeatMapManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(HeatMapManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(HeatMapManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(HeatMapManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the dialog */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                HeatMapManager dialog = new HeatMapManager(new javax.swing.JFrame(), true, null, null);
//                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
//                    @Override
//                    public void windowClosing(java.awt.event.WindowEvent e) {
//                        System.exit(0);
//                    }
//                });
//                dialog.setVisible(true);
//            }
//        });
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLayeredPane compositionPanel;
    private javax.swing.JLayeredPane customPanel;
    private javax.swing.JLayeredPane datesPanel;
    private javax.swing.JLayeredPane datesPbcCorrPanel;
    private javax.swing.JButton doneButton;
    private javax.swing.JLayeredPane heatMapDetailsPanel;
    private javax.swing.JLayeredPane heatMapPanel;
    private javax.swing.JLayeredPane ratiosPanel;
    private javax.swing.JLayeredPane ratiosPbcCorrPanel;
    private javax.swing.JLayeredPane rhosPanel;
    private javax.swing.JLayeredPane traceElementsPanel;
    private javax.swing.JTabbedPane variablesChart_tabbedPane;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the heatMapOptions
     */
    public Map<String, String> getHeatMapOptions() {
        if (heatMapOptions == null) {
            heatMapOptions = new SampleDateInterpretationGUIOptions().getHeatMapOptions();
        }
        return heatMapOptions;
    }

    /**
     * @param heatMapOptions the heatMapOptions to set
     */
    public void setHeatMapOptions(Map<String, String> heatMapOptions) {
        this.heatMapOptions = heatMapOptions;
    }
}
