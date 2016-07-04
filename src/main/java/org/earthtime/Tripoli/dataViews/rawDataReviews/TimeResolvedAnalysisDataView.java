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
import org.earthtime.dialogs.DialogEditor;
import org.earthtime.isotopes.IsotopesEnum;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public class TimeResolvedAnalysisDataView extends DialogEditor {

    private static int SESSION_VIEW_WIDTH = 25000;
    private JLayeredPane rawDataSession;
    private AbstractRawDataView[] rawDataSessionPlots;
    private JLayeredPane rawDataSessionOverlay;
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
    }

    private void initSession() {
        setSize(1200, 750);
        SESSION_VIEW_WIDTH = massSpec.getCountOfAcquisitions();
        int fractionCount = massSpec.getCountOfFractions();

        rawDataSession = new JLayeredPane();
        rawDataSession.setBounds(0, 0, SESSION_VIEW_WIDTH, rawDataSessionPlotScrollPane.getHeight() - 25);
        // this forces scroll bar
        rawDataSession.setPreferredSize(rawDataSession.getSize());
        rawDataSession.setOpaque(true);
        rawDataSession.setBackground(Color.white);
        rawDataSessionPlotScrollPane.setViewportView(rawDataSession);
        rawDataSessionPlotScrollPane.getHorizontalScrollBar().setUnitIncrement(1000);
        rawDataSessionPlotScrollPane.revalidate();

        // overlay of plots
        rawDataSessionOverlay = new JLayeredPane();
        rawDataSessionOverlay.setBounds(0, 0, rawDataSessionOverlayPlotScrollPane.getWidth() - 25, rawDataSessionOverlayPlotScrollPane.getHeight() - 25);
        // this forces scroll bar
        rawDataSessionOverlay.setPreferredSize(rawDataSessionOverlay.getSize());
        rawDataSessionOverlay.setOpaque(true);
        rawDataSessionOverlay.setBackground(Color.white);
        rawDataSessionOverlayPlotScrollPane.setViewportView(rawDataSessionOverlay);
        rawDataSessionOverlayPlotScrollPane.getHorizontalScrollBar().setUnitIncrement(1000);
        rawDataSessionOverlayPlotScrollPane.revalidate();

        Map<IsotopesEnum, DataModelInterface> isotopeToRawIntensitiesMap = massSpec.getIsotopeMappingModel().getIsotopeToRawIntensitiesMap();
        rawDataSessionPlots = new AbstractRawDataView[isotopeToRawIntensitiesMap.size()];
        rawDataSessionOverlayPlots = new AbstractRawDataView[isotopeToRawIntensitiesMap.size()];
        isotopeToRawIntensitiesMap.forEach((isotope, dataModel) -> {
            int index = massSpec.getVirtualCollectorModelMapToFieldIndexes().get(dataModel);
            rawDataSessionPlots[index] = new RawDataSessionPlot(dataModel, new Rectangle(0, index * 110, SESSION_VIEW_WIDTH, 100), false);
            rawDataSession.add(rawDataSessionPlots[index], JLayeredPane.DEFAULT_LAYER);
            rawDataSessionPlots[index].preparePanel(true, false);
            rawDataSessionPlots[index].repaint();

            //overlays
            rawDataSessionOverlayPlots[index] = new RawDataSessionPlot(dataModel, new Rectangle(0, index * 110, rawDataSessionOverlayPlotScrollPane.getWidth() - 25, 100), true);
            rawDataSessionOverlay.add(rawDataSessionOverlayPlots[index], JLayeredPane.DEFAULT_LAYER);
            rawDataSessionOverlayPlots[index].preparePanel(true, false);
            rawDataSessionOverlayPlots[index].repaint();

        });

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        rawDataSessionOverlayPlotScrollPane = new javax.swing.JScrollPane();
        rawDataSessionPlotScrollPane = new javax.swing.JScrollPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setPreferredSize(new java.awt.Dimension(1200, 750));

        jSplitPane1.setDividerLocation(300);

        rawDataSessionOverlayPlotScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        rawDataSessionOverlayPlotScrollPane.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                rawDataSessionOverlayPlotScrollPaneComponentResized(evt);
            }
        });
        jSplitPane1.setLeftComponent(rawDataSessionOverlayPlotScrollPane);

        rawDataSessionPlotScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        jSplitPane1.setRightComponent(rawDataSessionPlotScrollPane);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1233, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 726, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void rawDataSessionOverlayPlotScrollPaneComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_rawDataSessionOverlayPlotScrollPaneComponentResized
        System.out.println("LEFT PANEL ON THE MOVE");
        if (rawDataSessionOverlayPlots.length > 0){
            int revisedWidth = rawDataSessionOverlayPlotScrollPane.getWidth() - 25;
            rawDataSessionOverlay.setSize(new Dimension(revisedWidth, rawDataSessionOverlay.getHeight()));
            rawDataSessionOverlay.setPreferredSize(new Dimension(revisedWidth, rawDataSessionOverlay.getHeight()));

            for (int i = 0; i < rawDataSessionOverlayPlots.length; i ++){
                rawDataSessionOverlayPlots[i].setSize(new Dimension(revisedWidth, rawDataSessionOverlayPlots[i].getHeight()));
                rawDataSessionOverlayPlots[i].setPreferredSize(new Dimension(revisedWidth, rawDataSessionOverlayPlots[i].getHeight()));
                rawDataSessionOverlayPlots[i].setGraphWidth(revisedWidth);
                rawDataSessionOverlayPlots[i].repaint();
            }
            
            rawDataSessionOverlay.revalidate();
            
        }
    }//GEN-LAST:event_rawDataSessionOverlayPlotScrollPaneComponentResized


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JScrollPane rawDataSessionOverlayPlotScrollPane;
    private javax.swing.JScrollPane rawDataSessionPlotScrollPane;
    // End of variables declaration//GEN-END:variables
}
