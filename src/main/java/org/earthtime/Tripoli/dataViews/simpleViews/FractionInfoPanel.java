/*
 * FractionInfoPanel.java
 *
 * Copyright 2006-2017 James F. Bowring and www.Earth-Time.org
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
package org.earthtime.Tripoli.dataViews.simpleViews;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLayeredPane;
import org.earthtime.Tripoli.dataModels.DataModelInterface;
import org.earthtime.Tripoli.dataViews.AbstractRawDataView;
import org.earthtime.Tripoli.dataViews.simpleViews.usedByReflection.FitFunctionsOnDownHoleRatioDataView;
import org.earthtime.Tripoli.dataViews.simpleViews.usedByReflection.FitFunctionsOnRatioDataView;
import org.earthtime.Tripoli.fractions.TripoliFraction;
import org.earthtime.Tripoli.fractions.TripoliFractionIncludeChangeInterface;
import org.earthtime.beans.ET_JButton;
import org.earthtime.utilities.TimeToString;

/**
 *
 * @author James F. Bowring
 */
public class FractionInfoPanel extends AbstractRawDataView {

    /**
     *
     */
    public static int DEFAULT_WIDTH_OF_PANE = 128;
    //
    TripoliFractionIncludeChangeInterface rawDataModelView;
    private JCheckBox toggleFractionCheckBox;
    private JCheckBox showLocalYAxisCheckBox;
    private JCheckBox showLocalFitFunctionsCheckBox;

    /**
     *
     * @param sampleSessionDataView
     * @param tripoliFraction
     * @param rawDataModelView
     * @param bounds
     * @param invokeMouseListener
     */
    public FractionInfoPanel(//
            JLayeredPane sampleSessionDataView,//
            TripoliFraction tripoliFraction,//
            TripoliFractionIncludeChangeInterface rawDataModelView,//
            Rectangle bounds,//
            boolean invokeMouseListener) {
        super(sampleSessionDataView, tripoliFraction, bounds, invokeMouseListener, true);

        this.rawDataModelView = rawDataModelView;

        initView();

    }

    private void initView() {
        this.setCursor(Cursor.getDefaultCursor());

        Font checkBoxFont = new Font(
                "SansSerif",
                Font.BOLD,
                10);

        toggleFractionCheckBox = new JCheckBox("include fraction", true);
        toggleFractionCheckBox.setBounds(2, 22, DEFAULT_WIDTH_OF_PANE - 2, 15);
        toggleFractionCheckBox.setFont(checkBoxFont);
        toggleFractionCheckBox.setSelected(tripoliFraction.isIncluded());
        toggleFractionCheckBox.setBackground(this.getBackground());
        toggleFractionCheckBox.addActionListener(//
                new ToggleFractionCheckBoxActionListener(tripoliFraction, rawDataModelView, sampleSessionDataView));
        add(toggleFractionCheckBox, JLayeredPane.DEFAULT_LAYER);

        showLocalYAxisCheckBox = new JCheckBox("local Y axis", false);
        showLocalYAxisCheckBox.setBounds(2, 37, DEFAULT_WIDTH_OF_PANE - 2, 15);
        showLocalYAxisCheckBox.setFont(checkBoxFont);
        showLocalYAxisCheckBox.setSelected(tripoliFraction.isShowLocalYAxis());
        showLocalYAxisCheckBox.setBackground(this.getBackground());
        showLocalYAxisCheckBox.addActionListener(//
                new ShowLocalYAxisCheckBoxActionListener(tripoliFraction, sampleSessionDataView));
        add(showLocalYAxisCheckBox, JLayeredPane.DEFAULT_LAYER);

        if ((rawDataModelView instanceof FitFunctionsOnRatioDataView) || (rawDataModelView instanceof FitFunctionsOnDownHoleRatioDataView)) {
            showLocalFitFunctionsCheckBox = new JCheckBox("local Fit Functions", false);
            showLocalFitFunctionsCheckBox.setBounds(2, 52, DEFAULT_WIDTH_OF_PANE - 2, 15);
            showLocalFitFunctionsCheckBox.setFont(checkBoxFont);
            showLocalFitFunctionsCheckBox.setSelected(tripoliFraction.isShowLocalInterceptFitPanel());
            showLocalFitFunctionsCheckBox.setBackground(this.getBackground());
            showLocalFitFunctionsCheckBox.addActionListener(//
                    new ShowLocalInterceptFitFunctionsCheckBoxActionListener(tripoliFraction, sampleSessionDataView));
            add(showLocalFitFunctionsCheckBox, JLayeredPane.DEFAULT_LAYER);

            add(buttonForODChoiceFactory(68, "w/ OD", true), DEFAULT_LAYER);
            add(buttonForODChoiceFactory(88, "w/out OD", false), DEFAULT_LAYER);
            add(buttonForSelectAllFactory(68, "Select All"), DEFAULT_LAYER);
            add(buttonForReFitFactory(88, "ReFit Functions"), DEFAULT_LAYER);
        }

    }

    private JButton buttonForSelectAllFactory(int pixelsFromTop, final String caption) {
        JButton refitChoiceButton = new ET_JButton(caption);
        refitChoiceButton.setFont(new Font("SansSerif", Font.PLAIN, 11));
        refitChoiceButton.setBounds(70, pixelsFromTop, 88, 20);
        refitChoiceButton.addActionListener((ActionEvent ae) -> {
            tripoliFraction.toggleAllDataExceptShaded(true);
            rawDataModelView.repaintFraction();
            //((AbstractRawDataView) sampleSessionDataView).refreshPanel();
        });

        return refitChoiceButton;
    }

    private JButton buttonForReFitFactory(int pixelsFromTop, final String caption) {
        JButton refitChoiceButton = new ET_JButton(caption);
        refitChoiceButton.setFont(new Font("SansSerif", Font.PLAIN, 11));
        refitChoiceButton.setBounds(70, pixelsFromTop, 88, 20);
        refitChoiceButton.addActionListener((ActionEvent ae) -> {
            if (!tripoliFraction.isCurrentlyFitted()) {
                tripoliFraction.updateInterceptFitFunctionsIncludingCommonLead();
                tripoliFraction.updateDownholeFitFunctionsExcludingCommonLead();
                rawDataModelView.repaintFraction();//does not repaint funtion line anymore
                //((AbstractRawDataView) sampleSessionDataView).refreshPanel();
                updateReportTable();
            }
        });

        return refitChoiceButton;
    }

    private JButton buttonForODChoiceFactory(int pixelsFromTop, final String caption, final boolean setOD) {

        JButton ODChoiceButton = new ET_JButton(caption);
        ODChoiceButton.setFont(new Font("SansSerif", Font.PLAIN, 11));
        ODChoiceButton.setBounds(6, pixelsFromTop, 60, 20);
        ODChoiceButton.addActionListener((ActionEvent ae) -> {
            tripoliFraction.setODforAllRatios(setOD);
            ((AbstractRawDataView) sampleSessionDataView).refreshPanel(true, false);
        });

        return ODChoiceButton;
    }

    /**
     *
     * @param g2d
     */
    @Override
    public void paint(Graphics2D g2d) {
        super.paint(g2d);

        if (tripoliFraction != null) {
            g2d.drawString(tripoliFraction.getFractionID(), 10, 10);

//            g2d.drawString(TimeToString.secondsAsLongToTimeString(tripoliFraction.getZeroBasedTimeStamp() / 1000), 10, 20);
            g2d.drawString(TimeToString.secondsAsLongToTimeString(tripoliFraction.getZeroBasedTimeStamp()), 10, 20);

            toggleFractionCheckBox.setSelected(tripoliFraction.isIncluded());
        }
    }

    private class ToggleFractionCheckBoxActionListener implements ActionListener {

        private final TripoliFractionIncludeChangeInterface rawDataModelView;

        public ToggleFractionCheckBoxActionListener( //
                TripoliFraction tripoliFraction, //
                TripoliFractionIncludeChangeInterface rawDataModelView, //
                JLayeredPane sampleSessionDataView) {
            this.rawDataModelView = rawDataModelView;
            //this.sampleSessionDataView = sampleSessionDataView;
        }

        @Override
        public void actionPerformed(ActionEvent arg0) {
            boolean isSelected = ((AbstractButton) arg0.getSource()).isSelected();
            rawDataModelView.toggleFractionInclusion(isSelected);
            //if (  ! tripoliFraction.isStandard() ) {
            tripoliFraction.getuPbFraction().setRejected(!isSelected);

            // jan 2015
            if (tripoliFraction.isStandard()) {
                // removed to button may 2016 ((TripoliSessionFractionationCalculatorInterface) sampleSessionDataView).calculateSessionFitFunctionsForPrimaryStandard();
                updateReportTable();
            } else {
                updateReportTableView();
            }

            //}
        }
    }

    private class ShowLocalYAxisCheckBoxActionListener implements ActionListener {

        private TripoliFraction tripoliFraction = null;
        private JLayeredPane sampleSessionDataView = null;

        public ShowLocalYAxisCheckBoxActionListener(TripoliFraction tripoliFraction, JLayeredPane sampleSessionDataView) {
            this.tripoliFraction = tripoliFraction;
            this.sampleSessionDataView = sampleSessionDataView;
        }

        @Override
        public void actionPerformed(ActionEvent arg0) {
            tripoliFraction.toggleShowLocalYAxis();
            ((AbstractRawDataView) sampleSessionDataView).refreshPanel(true, false);
        }
    }

    private class ShowLocalInterceptFitFunctionsCheckBoxActionListener implements ActionListener {

        private TripoliFraction tripoliFraction = null;
        private JLayeredPane sampleSessionDataView = null;

        public ShowLocalInterceptFitFunctionsCheckBoxActionListener(TripoliFraction tripoliFraction, JLayeredPane sampleSessionDataView) {
            this.tripoliFraction = tripoliFraction;
            this.sampleSessionDataView = sampleSessionDataView;
        }

        @Override
        public void actionPerformed(ActionEvent arg0) {
            tripoliFraction.toggleShowLocalInterceptFitFunctionPanel();
            ((AbstractRawDataView) sampleSessionDataView).refreshPanel(true, false);
        }
    }

    /**
     *
     * @param doReScale the value of doReScale
     * @param inLiveMode the value of inLiveMode
     */
    @Override
    public void preparePanel(boolean doReScale, boolean inLiveMode) {

        this.removeAll();

    }

    /**
     *
     * @return
     */
    @Override
    public DataModelInterface getDataModel() {
        return null;//rawRatioDataModel;
    }

    /**
     *
     * @param g2d
     * @param chosenDatumIndex
     * @param secondChoiceIndex
     */
    @Override
    protected void highlightSelectedData(Graphics2D g2d, int chosenDatumIndex, int secondChoiceIndex) {
        // do nothing
    }
}
