/*
 * Copyright 2006-2016 CIRDLES.org.
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
package org.earthtime.Tripoli.dataViews.rawDataReviews;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Map;
import javax.swing.JLayeredPane;
import org.earthtime.Tripoli.dataModels.DataModelInterface;
import org.earthtime.Tripoli.dataViews.AbstractRawDataView;
import org.earthtime.Tripoli.massSpecSetups.AbstractMassSpecSetup;
import org.earthtime.beans.ET_JButton;
import org.earthtime.dialogs.DialogEditor;
import org.earthtime.isotopes.IsotopesEnum;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public class TimeResolvedAnalysisDataView extends DialogEditor {

    private static int SESSION_VIEW_WIDTH = 25000;
    private AbstractRawDataView rawDataSessionSerialContainer;
    private AbstractRawDataView[] rawDataSessionPlots;
    private AbstractRawDataView rawDataSessionOverlayContainer;
    private AbstractRawDataView[] rawDataSessionOverlayPlots;
    private AbstractMassSpecSetup massSpec;
    

    /**
     * Creates new form TimeResolvedAnalysisDataView
     *
     * @param parent
     * @param modal
     * @param massSpec
     */
    public TimeResolvedAnalysisDataView(java.awt.Frame parent, boolean modal, AbstractMassSpecSetup massSpec) {
        super(parent, modal);
        this.massSpec = massSpec;
        rawDataSessionPlots = new AbstractRawDataView[0];
        rawDataSessionOverlayPlots = new AbstractRawDataView[0];

        initComponents();

        rawDataSessionPlotScrollPane.getViewport().putClientProperty("EnableWindowBlit", Boolean.TRUE);

        rawDataSessionOverlayPlotScrollPane.getViewport().putClientProperty("EnableWindowBlit", Boolean.TRUE);

        initSession();
        
        // temp hack to remove focus
        loadData_button.setBackground(Color.white);
    }

    private void initSession() {
        setSize(1200, 800);
        SESSION_VIEW_WIDTH = massSpec.getCountOfAcquisitions();

        rawDataSessionSerialContainer = new RawDataSessionSerialContainer();
        rawDataSessionSerialContainer.setBounds(0, 0, SESSION_VIEW_WIDTH, rawDataSessionPlotScrollPane.getHeight() - 25);
        // this forces scroll bar
        rawDataSessionSerialContainer.setPreferredSize(rawDataSessionSerialContainer.getSize());
        rawDataSessionSerialContainer.setOpaque(true);
        rawDataSessionSerialContainer.setBackground(Color.white);

        rawDataSessionPlotScrollPane.setViewportView(rawDataSessionSerialContainer);
        rawDataSessionPlotScrollPane.getHorizontalScrollBar().setUnitIncrement(1000);
        rawDataSessionPlotScrollPane.revalidate();

        // overlay of plots
        rawDataSessionOverlayContainer = new RawDataSessionOverlayContainer(//
                new Rectangle(0, 0, rawDataSessionOverlayPlotScrollPane.getWidth(), rawDataSessionOverlayPlotScrollPane.getHeight() - 25),//
                rawDataSessionSerialContainer);

        rawDataSessionOverlayPlotScrollPane.setViewportView(rawDataSessionOverlayContainer);
        rawDataSessionOverlayPlotScrollPane.getHorizontalScrollBar().setUnitIncrement(1000);
        rawDataSessionOverlayPlotScrollPane.revalidate();

        Map<IsotopesEnum, DataModelInterface> isotopeToRawIntensitiesMap = massSpec.getIsotopeMappingModel().getIsotopeToRawIntensitiesMap();
        rawDataSessionPlots = new AbstractRawDataView[isotopeToRawIntensitiesMap.size()];
        rawDataSessionOverlayPlots = new AbstractRawDataView[isotopeToRawIntensitiesMap.size()];
        isotopeToRawIntensitiesMap.forEach((isotope, dataModel) -> {
            int index = massSpec.getVirtualCollectorModelMapToFieldIndexes().get(dataModel);
            rawDataSessionPlots[index] = new RawDataSessionPlot(dataModel, new Rectangle(0, index * 110+ 25, SESSION_VIEW_WIDTH, 100), false);
            rawDataSessionSerialContainer.add(rawDataSessionPlots[index], JLayeredPane.DEFAULT_LAYER);
            rawDataSessionPlots[index].preparePanel(true, false);
            rawDataSessionPlots[index].repaint();

            //overlays
            rawDataSessionOverlayPlots[index] = new RawDataSessionPlot(dataModel, new Rectangle(0, index * 110 + 25, rawDataSessionOverlayPlotScrollPane.getWidth(), 100), true);
            rawDataSessionOverlayContainer.add(rawDataSessionOverlayPlots[index], JLayeredPane.DEFAULT_LAYER);
            rawDataSessionOverlayPlots[index].preparePanel(true, false);
            rawDataSessionOverlayPlots[index].repaint();
        });

        rawDataSessionOverlayContainer.preparePanel(true, false);

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rawDataSplitPane = new javax.swing.JSplitPane();
        rawDataSessionOverlayPlotScrollPane = new javax.swing.JScrollPane();
        rawDataSessionPlotScrollPane = new javax.swing.JScrollPane();
        buttonPanel = new javax.swing.JLayeredPane();
        loadData_button = new ET_JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1200, 800));
        setSize(new java.awt.Dimension(1200, 800));

        rawDataSplitPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        rawDataSplitPane.setDividerLocation(300);

        rawDataSessionOverlayPlotScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        rawDataSessionOverlayPlotScrollPane.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                rawDataSessionOverlayPlotScrollPaneComponentResized(evt);
            }
        });
        rawDataSplitPane.setLeftComponent(rawDataSessionOverlayPlotScrollPane);

        rawDataSessionPlotScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        rawDataSplitPane.setRightComponent(rawDataSessionPlotScrollPane);

        buttonPanel.setBackground(new java.awt.Color(235, 255, 255));
        buttonPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        buttonPanel.setOpaque(true);
        buttonPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        loadData_button.setBackground(new java.awt.Color(204, 204, 204));
        loadData_button.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        loadData_button.setForeground(new java.awt.Color(255, 51, 0));
        loadData_button.setText("Proceed to Load Data");
        loadData_button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        loadData_button.setPreferredSize(new java.awt.Dimension(140, 23));
        loadData_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadData_buttonActionPerformed(evt);
            }
        });
        buttonPanel.add(loadData_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 5, 220, 25));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(rawDataSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 1200, Short.MAX_VALUE)
            .addComponent(buttonPanel)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(rawDataSplitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 759, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void rawDataSessionOverlayPlotScrollPaneComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_rawDataSessionOverlayPlotScrollPaneComponentResized
        if (rawDataSessionOverlayPlots.length > 0) {
            int revisedWidth = rawDataSessionOverlayPlotScrollPane.getWidth() - 25;
            rawDataSessionOverlayContainer.setSize(new Dimension(revisedWidth, rawDataSessionOverlayContainer.getHeight()));
            rawDataSessionOverlayContainer.setPreferredSize(new Dimension(revisedWidth, rawDataSessionOverlayContainer.getHeight()));
            rawDataSessionOverlayContainer.setGraphWidth(revisedWidth);
            rawDataSessionOverlayContainer.repaint();

            for (int i = 0; i < rawDataSessionOverlayPlots.length; i++) {
                rawDataSessionOverlayPlots[i].setSize(new Dimension(revisedWidth, rawDataSessionOverlayPlots[i].getHeight()));
                rawDataSessionOverlayPlots[i].setPreferredSize(new Dimension(revisedWidth, rawDataSessionOverlayPlots[i].getHeight()));
                rawDataSessionOverlayPlots[i].setGraphWidth(revisedWidth);
                rawDataSessionOverlayPlots[i].repaint();
            }

            rawDataSessionOverlayContainer.revalidate();

        }
    }//GEN-LAST:event_rawDataSessionOverlayPlotScrollPaneComponentResized

    private void loadData_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadData_buttonActionPerformed
       close();
    }//GEN-LAST:event_loadData_buttonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLayeredPane buttonPanel;
    private javax.swing.JButton loadData_button;
    private javax.swing.JScrollPane rawDataSessionOverlayPlotScrollPane;
    private javax.swing.JScrollPane rawDataSessionPlotScrollPane;
    private javax.swing.JSplitPane rawDataSplitPane;
    // End of variables declaration//GEN-END:variables
}
