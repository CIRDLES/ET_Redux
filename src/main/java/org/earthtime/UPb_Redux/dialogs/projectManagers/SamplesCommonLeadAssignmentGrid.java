/*
 * SamplesCommonLeadAssignmentGrid.java
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
package org.earthtime.UPb_Redux.dialogs.projectManagers;

import java.awt.Color;
import java.awt.Dimension;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.SortedMap;
import javax.swing.JLayeredPane;
import static javax.swing.JLayeredPane.DEFAULT_LAYER;
import javax.swing.JPanel;
import org.earthtime.Tripoli.fractions.TripoliFraction;
import org.earthtime.Tripoli.samples.AbstractTripoliSample;
import org.earthtime.dataDictionaries.RadDatesForPbCorrSynchEnum;

/**
 *
 * @author James F. Bowring
 */
public class SamplesCommonLeadAssignmentGrid extends JLayeredPane implements SamplesCommonLeadAssignmentGridLayoutInterface {
    
    private final AbstractTripoliSample tripoliSample;
    private JPanel[] fractionCommonLeadManagers;
    private final int width;
    private final int GRID_ELEMENT_HEIGHT = 110;

    /**
     *
     * @param tripoliSample
     * @param width
     */
    public SamplesCommonLeadAssignmentGrid(//
            AbstractTripoliSample tripoliSample, int width) {
        
        setOpaque(true);
        setBackground(Color.white);
        setBorder(null);
        
        this.tripoliSample = tripoliSample;
        this.fractionCommonLeadManagers = null;
        this.width = width;
        
        populateGrid();
    }
    
    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, this.width, height);
    }
    
    private void populateGrid() {
        this.removeAll();
        
        fractionCommonLeadManagers = new TripoliFractionCommonLeadManager[tripoliSample.getSampleFractions().size()];
        int index = 0;
        Iterator<TripoliFraction> tripoliFractionsIterator = tripoliSample.getSampleFractions().iterator();
        while (tripoliFractionsIterator.hasNext()) {
            TripoliFraction tf = tripoliFractionsIterator.next();
            fractionCommonLeadManagers[index] = new TripoliFractionCommonLeadManager(this, tf, 2, (index + 0) * GRID_ELEMENT_HEIGHT, width - 25, GRID_ELEMENT_HEIGHT);
            this.add(fractionCommonLeadManagers[index], DEFAULT_LAYER);
            index++;
        }
        
        refreshLayoutOfGrid();
    }

    /**
     *
     * @param expandedView
     */
    @Override
    public void toggleAllFractionViewsExpanded(boolean expandedView) {
        for (JPanel fractionCommonLeadManager : fractionCommonLeadManagers) {
            ((TripoliFractionCommonLeadManager) fractionCommonLeadManager).setExpandedView(expandedView);
        }
    }

    /**
     *
     */
    @Override
    public void refreshLayoutOfGrid() {
        int runningLayoutHeight = 0;
        for (JPanel fractionCommonLeadManager : fractionCommonLeadManagers) {
            int fractionPanelHeight = ((TripoliFractionCommonLeadManager) fractionCommonLeadManager).isExpandedView() ? GRID_ELEMENT_HEIGHT : 25;//.getHeightIntialPbModelDataView();//        .isExpandedView() ? GRID_ELEMENT_HEIGHT : 25;
            fractionCommonLeadManager.setBounds(2, runningLayoutHeight, width - 25, fractionPanelHeight);
            ((TripoliFractionCommonLeadManager) fractionCommonLeadManager).fillParameterFields();
            runningLayoutHeight += fractionPanelHeight;
        }
        this.validate();
        setPreferredSize(new Dimension(width - 25, runningLayoutHeight + 50));
    }

    /**
     *
     * @param parameters
     */
    @Override
    public void synchAllSKModelsToThis(SortedMap<String, BigDecimal> parameters) {
        Iterator<TripoliFraction> tripoliFractionsIterator = tripoliSample.getSampleFractions().iterator();
        while (tripoliFractionsIterator.hasNext()) {
            TripoliFraction tf = tripoliFractionsIterator.next();
            //if (tf.hasStaceyKramersInitialPbModelET()) {
            // all fractions will have same SK even if they don't use it
            tf.updateSKParametersFromDataModel(parameters);
//            if (tf.hasStaceyKramersInitialPbModelET()) {
//                ((StaceyKramersInitialPbModelET)tf.getInitialPbModelET()).resetModelFromTripoliFraction();
//            }
        }
        refreshGrid();
    }
    
    @Override
    public void synchAllSynchronizedSKDateToThis(RadDatesForPbCorrSynchEnum radDateForSKSynch) {
        Iterator<TripoliFraction> tripoliFractionsIterator = tripoliSample.getSampleFractions().iterator();
        while (tripoliFractionsIterator.hasNext()) {
            TripoliFraction tf = tripoliFractionsIterator.next();
            tf.setRadDateForSKSynch(radDateForSKSynch);
        }
        refreshGrid();
    }

    /**
     *
     * @param tripoliFraction
     */
    @Override
    public void synchAllPlaceHolderModelsToThisFraction(TripoliFraction tripoliFraction) {
        Iterator<TripoliFraction> tripoliFractionsIterator = tripoliSample.getSampleFractions().iterator();
        while (tripoliFractionsIterator.hasNext()) {
            TripoliFraction tf = tripoliFractionsIterator.next();
            // all fractions will have same PlaceHolder even if they don't use it
            tf.copyPlaceHolderParameters(tripoliFraction);
        }
        refreshGrid();
    }

    /**
     *
     */
    @Override
    public void refreshGrid() {
        for (JPanel fractionCommonLeadManager : fractionCommonLeadManagers) {
            ((TripoliFractionCommonLeadManager) fractionCommonLeadManager).initialPbModelPanelLayoutFactory();
        }
    }

    /**
     *
     * @return
     */
    @Override
    public boolean determineExpandedViewState() {
        boolean overallExpandedView = false;
        for (JPanel fractionCommonLeadManager : fractionCommonLeadManagers) {
            overallExpandedView = overallExpandedView || ((TripoliFractionCommonLeadManager) fractionCommonLeadManager).isExpandedView();
        }
        return overallExpandedView;
    }

    /**
     * @return the tripoliSample
     */
    public AbstractTripoliSample getTripoliSample() {
        return tripoliSample;
    }
}
