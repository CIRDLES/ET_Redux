/*
 * SampleAnalysisWorkflowManagerDialog.java
 *
 *
 * Copyright 2006-2016 James F. Bowring and www.Earth-Time.org
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
package org.earthtime.UPb_Redux.dialogs.sessionManagers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JLayeredPane;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import org.earthtime.ETReduxFrame;
import org.earthtime.Tripoli.dataModels.DataModelInterface;
import org.earthtime.Tripoli.dataModels.DownholeFractionationDataModel;
import org.earthtime.Tripoli.dataModels.sessionModels.AbstractSessionForStandardDataModel;
import org.earthtime.Tripoli.dataViews.AbstractRawDataView;
import org.earthtime.Tripoli.dataViews.overlayViews.TripoliSessionRawDataView;
import org.earthtime.Tripoli.dataViews.simpleViews.SessionOfStandardView;
import org.earthtime.Tripoli.dataViews.simpleViews.usedByReflection.CorrectedIntensitiesDataView;
import org.earthtime.Tripoli.dataViews.simpleViews.usedByReflection.CorrectedRatioDataView;
import org.earthtime.Tripoli.dataViews.simpleViews.usedByReflection.FitFunctionsOnDownHoleRatioDataView;
import org.earthtime.Tripoli.dataViews.simpleViews.usedByReflection.FitFunctionsOnRatioDataView;
import org.earthtime.Tripoli.dataViews.simpleViews.usedByReflection.RawIntensitiesDataView;
import org.earthtime.Tripoli.dataViews.simpleViews.usedByReflection.RawRatioDataView;
import org.earthtime.Tripoli.fractions.TripoliFraction;
import org.earthtime.Tripoli.samples.AbstractTripoliSample;
import org.earthtime.Tripoli.sessions.TripoliSessionInterface;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.dialogs.projectManagers.ProjectManagerSubscribeInterface;
import org.earthtime.UPb_Redux.utilities.BrowserControl;
import org.earthtime.beans.ET_JButton;
import org.earthtime.dataDictionaries.DataPresentationModeEnum;
import org.earthtime.dataDictionaries.FractionLayoutViewStylesEnum;
import org.earthtime.dataDictionaries.FractionSelectionTypeEnum;
import org.earthtime.dataDictionaries.FractionationTechniquesEnum;
import org.earthtime.dataDictionaries.IncludedTypeEnum;
import org.earthtime.dataDictionaries.RawRatioNames;
import org.earthtime.dialogs.DialogEditor;

/**
 *
 * @author James F. Bowring
 */
public class SessionAnalysisWorkflowManagerLAICPMS extends DialogEditor //
        implements
        Serializable,
        SessionAnalysisWorkflowManagerInterface {

    private TripoliSessionInterface tripoliSession = null;
    private final boolean initialized = false;
    // view support
    private final ETReduxFrame uPbReduxFrame;
    private AbstractRawDataView tripoliSessionRawDataView;
    private final int HORIZ_SCROLL_BAR_HEIGHT = 14;
    private IncludedTypeEnum currentFractionView;
    private final ProjectManagerSubscribeInterface projectManager;
    // nov 2014
    private final static int OFFSET_TO_TOP_DATA_DISPLAY = 115;//0;
    private final static int WIDTH_OF_YAXIS_PANE = 200;
    private transient int lastUsedUnknownSampleIndexForSampleSelector = 1;

    /**
     * Creates new form SampleAnalysisWorkflowManagerDialog
     *
     * @param projectManager
     * @param uPbReduxFrame
     * @param modal
     * @param tripoliSession
     */
    public SessionAnalysisWorkflowManagerLAICPMS(
            ProjectManagerSubscribeInterface projectManager, //
            ETReduxFrame uPbReduxFrame, //
            boolean modal, //
            final TripoliSessionInterface tripoliSession) {
        // null parent allows focussed frame to be on top
        super(null, modal);

        this.projectManager = projectManager;

        this.uPbReduxFrame = uPbReduxFrame;
        this.tripoliSession = tripoliSession;

        initComponents();

        // color coding
        rawIsotopes_radioButton.setBackground(ReduxConstants.ColorOfRawIsotopeGreen);
        correctedIsotopes_radioButton.setBackground(ReduxConstants.ColorOfCorrIsotopeYellow);

        // april 2016
        fractionsShownLabel.setVisible(false);
        showAllFractions_radioButton.setVisible(false);
        showIncludedFractions_radioButton.setVisible(false);

        setSize();
    }

    /**
     *
     */
    @Override
    public void setupTripoliSessionRawDataView() {
        if (tripoliSession != null) {
            // default views
            // setup headerpane
            tripoliTab_layeredPane.remove(tripoliSessionDataHeader_pane);
            tripoliSessionDataHeader_pane = new javax.swing.JLayeredPane();
            tripoliTab_layeredPane.add(tripoliSessionDataHeader_pane, javax.swing.JLayeredPane.FRAME_CONTENT_LAYER);

            // setup axis pane 
            tripoliTab_layeredPane.remove(tripoliSessionRawDataViewYAxis);
            tripoliSessionRawDataViewYAxis = new javax.swing.JLayeredPane();
            tripoliTab_layeredPane.add(tripoliSessionRawDataViewYAxis, javax.swing.JLayeredPane.DEFAULT_LAYER);

            tripoliSessionRawDataViewYAxis.setBounds(//
                    controlPanel_panel.getWidth(),//
                    OFFSET_TO_TOP_DATA_DISPLAY,//
                    WIDTH_OF_YAXIS_PANE,//nov 2014 default width >> constant for better viewing per Noah
                    tripoliSessionDataView_scrollPane.getHeight() - HORIZ_SCROLL_BAR_HEIGHT);

            tripoliSessionDataHeader_pane.setBounds(//
                    controlPanel_panel.getWidth() + WIDTH_OF_YAXIS_PANE,//
                    0,//
                    tripoliSessionDataView_scrollPane.getWidth(), //
                    OFFSET_TO_TOP_DATA_DISPLAY);

            tripoliSessionDataControls_pane.setBounds(//
                    controlPanel_panel.getWidth(),//
                    0,//
                    WIDTH_OF_YAXIS_PANE, //
                    OFFSET_TO_TOP_DATA_DISPLAY);

            // create data viewing pane
            tripoliSessionRawDataView = new TripoliSessionRawDataView( //
                    uPbReduxFrame,//
                    tripoliSession,//
                    dataModelViewConstructorFactory(RawIntensitiesDataView.class.getName()),//
                    rawDataSourceMethodFactory("getIncludedIsotopes"), //
                    FractionLayoutViewStylesEnum.GRID,//
                    yAxisZoomSlider,//
                    xAxisZoomSlider, //
                    tripoliSessionRawDataViewYAxis, //
                    tripoliSessionDataHeader_pane,//
                    tripoliSessionDataControls_pane, //
                    tripoliSessionDataView_scrollPane.getBounds(), this);

            // set sampleFractionationCalculator
            ((TripoliSessionRawDataView) tripoliSessionRawDataView).setSessionFractionationCalculator(tripoliSession);

            // default
            uniformYaxis.doClick();
            allDataUsedForScaling.doClick();

            // feb 2016
            // set up sample combo box
            samplesCompboBox.removeAllItems();
            ArrayList<AbstractTripoliSample> samples = tripoliSession.getTripoliSamples();
            samples.stream().forEach((sample) -> {
                samplesCompboBox.addItem(sample);
            });
            samplesCompboBox.setSelectedIndex(-1);

            samplesCompboBox.addItemListener((ItemEvent e) -> {
                JComboBox comboBox = (JComboBox) e.getSource();
                AbstractTripoliSample sample = (AbstractTripoliSample) comboBox.getSelectedItem();
                if (sample != null) {
                    if (sample.getSampleFractions().first().isStandard()) {
                        ((TripoliSessionRawDataView) tripoliSessionRawDataView).setFractionSelectionType(FractionSelectionTypeEnum.STANDARD);
                        amPrimaryRefMaterial_label.setText("primary reference material");
                    } else {
                        ((TripoliSessionRawDataView) tripoliSessionRawDataView).setFractionSelectionType(FractionSelectionTypeEnum.UNKNOWN);
                        lastUsedUnknownSampleIndexForSampleSelector = comboBox.getSelectedIndex();
                        amPrimaryRefMaterial_label.setText("unknown");
                    }

                    ((TripoliSessionRawDataView) tripoliSessionRawDataView).setSelectedSample(sample);
                    tripoliSessionRawDataView.refreshPanel(true, false);
                    tripoliSessionDataView_scrollPane.revalidate();
                }
            });

            samplesCompboBox.setSelectedIndex(0);

            // set default zoom
            setDefaultZoom();

            // setup sliders
            tripoliSessionDataView_scrollPane.getHorizontalScrollBar().setUnitIncrement(xAxisZoomSlider.getValue());
            tripoliSessionDataView_scrollPane.getHorizontalScrollBar().setBlockIncrement(xAxisZoomSlider.getValue());

            // setup listener so header panels scroll outside of scrollpane
            tripoliSessionDataView_scrollPane.getHorizontalScrollBar().removeAll();
            tripoliSessionDataView_scrollPane.getHorizontalScrollBar().addAdjustmentListener(new AdjustmentListener() {
                @Override
                public void adjustmentValueChanged(AdjustmentEvent evt) {
                    // causes header panel to coordinate with data panel
                    tripoliSessionDataHeader_pane.setBounds(//
                            controlPanel_panel.getWidth() + WIDTH_OF_YAXIS_PANE - evt.getValue(), //
                            0, //
                            tripoliSessionDataHeader_pane.getWidth() + evt.getValue(), //
                            OFFSET_TO_TOP_DATA_DISPLAY);
                }
            });

            tripoliSessionDataView_scrollPane.getVerticalScrollBar().setUnitIncrement(yAxisZoomSlider.getValue());
            tripoliSessionDataView_scrollPane.getVerticalScrollBar().setBlockIncrement(yAxisZoomSlider.getValue());

            // setup listener so yaxis panels scroll outside of scrollpane
            tripoliSessionDataView_scrollPane.getVerticalScrollBar().removeAll();
            tripoliSessionDataView_scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {
                @Override
                public void adjustmentValueChanged(AdjustmentEvent evt) {
                    // causes yAxis panel to coordinate with data panel
                    //if (! evt.getValueIsAdjusting() ) {
                    tripoliSessionRawDataViewYAxis.setBounds(//
                            controlPanel_panel.getWidth(), //
                            OFFSET_TO_TOP_DATA_DISPLAY - evt.getValue(), //
                            tripoliSessionRawDataViewYAxis.getWidth(), //
                            tripoliSessionDataView_scrollPane.getHeight() + evt.getValue() - HORIZ_SCROLL_BAR_HEIGHT);
                }
                //}
            });

            // show data view
            tripoliSessionDataView_scrollPane.setViewportView(tripoliSessionRawDataView);
            adjustViewPanes();

            // default view for new data
            showRawIsotopeDataModels(false);
            rawIsotopes_radioButton.setSelected(true);
            gridPlot_radioButton.setSelected(true);

            tripoliSessionRawDataView.preparePanel(true, false);

            // now init listeners
            ((TripoliSessionRawDataView) tripoliSessionRawDataView).initializeListeners();

            // fractionation tab listener
            fractionationTechniqueTabbedPane.addChangeListener((ChangeEvent evt) -> {
                JTabbedPane pane = (JTabbedPane) evt.getSource();

                // Get current tab
                int sel = pane.getSelectedIndex();

                if (sel >= 0) {
                    if (pane.getTitleAt(sel).compareToIgnoreCase(FractionationTechniquesEnum.INTERCEPT.getName()) == 0) {
                        tripoliSession.setFractionationTechnique(FractionationTechniquesEnum.INTERCEPT);

                        interceptFitEachRefMaterial_radioButton.doClick();

                    } else if (pane.getTitleAt(sel).compareToIgnoreCase(FractionationTechniquesEnum.DOWNHOLE.getName()) == 0) {
                        tripoliSession.setFractionationTechnique(FractionationTechniquesEnum.DOWNHOLE);

                        downholeFitStandards_radioButton.doClick();
                    }

                    for (int i = 0; i < pane.getTabCount(); i++) {
                        pane.setForegroundAt(i, Color.black);
                    }
                    pane.setForegroundAt(sel, Color.red);

                    tripoliSession.applyCorrections(false);

                    // nov 2015 to update data
                    uPbReduxFrame.updateReportTable(true, false);
                }
            } // This method is called whenever the selected tab changes
            );
        }
    }

    public void invokeSavedFractionationTechnique(boolean doCorrections) {
        tripoliSessionRawDataView.refreshPanel(true, false);

        if (doCorrections) {
            tripoliSession.calculateSessionFitFunctionsForPrimaryStandard(false);
        }

        if (tripoliSession.getFractionationTechnique().compareTo(FractionationTechniquesEnum.DOWNHOLE) == 0) {
            fractionationTechniqueTabbedPane.setSelectedIndex(0);
        } else {
            fractionationTechniqueTabbedPane.setSelectedIndex(1);
        }

        tripoliSessionRawDataView.refreshPanel(true, false);
    }

    private void setDefaultZoom() {
        yAxisZoomSlider.setValue(TripoliSessionRawDataView.DEFAULT_HEIGHT_OF_FRACTION_PANES);
        xAxisZoomSlider.setValue((TripoliSessionRawDataView.FRACTION_LAYOUT_VIEW_STYLE.equals(FractionLayoutViewStylesEnum.SESSION)//
                ? TripoliSessionRawDataView.DEFAULT_WIDTH_OF_SESSION_PANES : TripoliSessionRawDataView.DEFAULT_WIDTH_OF_FRACTION_PANES));

    }

    private Constructor dataModelViewConstructorFactory(String dataModelViewClassName) {
        Constructor<?> dataModelViewConstructor = null;

        try {
            Class<?> dataModelViewClass = Class.forName(dataModelViewClassName);
            Class<?>[] partypes = new Class[5];
            partypes[0] = JLayeredPane.class;
            partypes[1] = TripoliFraction.class;
            partypes[2] = DataModelInterface.class;
            partypes[3] = Rectangle.class;
            partypes[4] = boolean.class;
            dataModelViewConstructor = dataModelViewClass.getConstructor(partypes);
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException classNotFoundException) {
        }

        return dataModelViewConstructor;
    }

    private Method rawDataSourceMethodFactory(String rawDataSourceMethodName) {
        Method rawDataSourceMethod = null;

        try {
            Class<?> tripoliFractionClass = Class.forName(TripoliFraction.class.getCanonicalName());
            rawDataSourceMethod = tripoliFractionClass.getMethod(rawDataSourceMethodName, new Class[]{});//(Class<?>[]) null );
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException classNotFoundException) {
        }
        return rawDataSourceMethod;
    }

    private void showRawIsotopeDataModels(boolean refreshPanel) {
        // assumption is that tripoliSessionRawDataView is initialized
        if (TripoliSessionRawDataView.FRACTION_LAYOUT_VIEW_STYLE.//
                equals(FractionLayoutViewStylesEnum.SESSION)//
                || //
                TripoliSessionRawDataView.FRACTION_LAYOUT_VIEW_STYLE.//
                equals(FractionLayoutViewStylesEnum.GRID_INTERCEPT)) {
            ((TripoliSessionRawDataView) tripoliSessionRawDataView).setFRACTION_LAYOUT_VIEW_STYLE(FractionLayoutViewStylesEnum.GRID);
        }
        tripoliSessionRawDataView.setBackground(rawIsotopes_radioButton.getBackground());
        ((TripoliSessionRawDataView) tripoliSessionRawDataView).setDataModelViewConstructor(//
                dataModelViewConstructorFactory(RawIntensitiesDataView.class.getName()));

        ((TripoliSessionRawDataView) tripoliSessionRawDataView).setRawDataSourceMethod(//
                rawDataSourceMethodFactory("getIncludedIsotopes"));

        if (refreshPanel) {
            updateSamplesSelection(false, false);
        }

        setEnableAllGridGraphOverlayButtons(true);
    }

    private void showCorrectedIsotopeDataModels() {
        // assumption is that tripoliSessionRawDataView is initialized
        if (TripoliSessionRawDataView.FRACTION_LAYOUT_VIEW_STYLE.//
                equals(FractionLayoutViewStylesEnum.SESSION)//
                || //
                TripoliSessionRawDataView.FRACTION_LAYOUT_VIEW_STYLE.//
                equals(FractionLayoutViewStylesEnum.GRID_INTERCEPT)) {
            ((TripoliSessionRawDataView) tripoliSessionRawDataView).setFRACTION_LAYOUT_VIEW_STYLE(FractionLayoutViewStylesEnum.GRID);
        }
        tripoliSessionRawDataView.setBackground(correctedIsotopes_radioButton.getBackground());
        ((TripoliSessionRawDataView) tripoliSessionRawDataView).setDataModelViewConstructor(//
                dataModelViewConstructorFactory(CorrectedIntensitiesDataView.class.getName()));

        ((TripoliSessionRawDataView) tripoliSessionRawDataView).setRawDataSourceMethod(//
                rawDataSourceMethodFactory("getIncludedIsotopes"));

        //cause slider to synch
        ((TripoliSessionRawDataView) tripoliSessionRawDataView).synchXAxisZoomSliderValue(((TripoliSessionRawDataView) tripoliSessionRawDataView).getDataModelWidth());

        updateSamplesSelection(false, false);

        setEnableAllGridGraphOverlayButtons(true);
    }

    private void showRawRatioDataModels() {
        // assumption is that tripoliSessionRawDataView is initialized

        if (TripoliSessionRawDataView.FRACTION_LAYOUT_VIEW_STYLE.//
                equals(FractionLayoutViewStylesEnum.SESSION)//
                || //
                TripoliSessionRawDataView.FRACTION_LAYOUT_VIEW_STYLE.//
                equals(FractionLayoutViewStylesEnum.GRID_INTERCEPT)) {
            ((TripoliSessionRawDataView) tripoliSessionRawDataView).setFRACTION_LAYOUT_VIEW_STYLE(FractionLayoutViewStylesEnum.GRID);
        }
        tripoliSessionRawDataView.setBackground(rawRatios_radioButton.getBackground());
        ((TripoliSessionRawDataView) tripoliSessionRawDataView).setDataModelViewConstructor(//
                dataModelViewConstructorFactory(RawRatioDataView.class.getName()));

        ((TripoliSessionRawDataView) tripoliSessionRawDataView).setRawDataSourceMethod(//
                rawDataSourceMethodFactory("getRatiosForFractionFitting"));

        updateSamplesSelection(false, false);

        setEnableAllGridGraphOverlayButtons(true);
    }

    private void showDownholeFractionationCorrectedUnknownRawRatioDataModels(FractionSelectionTypeEnum fractionSelectionType) {
        // assumption is that tripoliSessionRawDataView is initialized

        ((TripoliSessionRawDataView) tripoliSessionRawDataView).setFractionSelectionType(fractionSelectionType);
        if (TripoliSessionRawDataView.FRACTION_LAYOUT_VIEW_STYLE.//
                equals(FractionLayoutViewStylesEnum.SESSION)//
                || //
                TripoliSessionRawDataView.FRACTION_LAYOUT_VIEW_STYLE.//
                equals(FractionLayoutViewStylesEnum.GRID_INTERCEPT)//
                || //
                TripoliSessionRawDataView.FRACTION_LAYOUT_VIEW_STYLE.//
                equals(FractionLayoutViewStylesEnum.OVERLAY)) {
            ((TripoliSessionRawDataView) tripoliSessionRawDataView).setFRACTION_LAYOUT_VIEW_STYLE(FractionLayoutViewStylesEnum.GRID);
            gridPlot_radioButton.setSelected(true);
        }

        tripoliSessionRawDataView.setBackground(downholeCorrectedUnknownRatios_radioButton.getBackground());
        ((TripoliSessionRawDataView) tripoliSessionRawDataView).setDataModelViewConstructor(//
                dataModelViewConstructorFactory(CorrectedRatioDataView.class.getName()));

        ((TripoliSessionRawDataView) tripoliSessionRawDataView).setRawDataSourceMethod(//
                rawDataSourceMethodFactory("getRatiosForFractionFitting"));

        ((TripoliSessionRawDataView) tripoliSessionRawDataView).setDataModelWidth( //
                Math.max(CorrectedRatioDataView.DEFAULT_WIDTH_OF_PANE,//
                        ((TripoliSessionRawDataView) tripoliSessionRawDataView).getDataModelWidth()));

        updateSamplesSelection(false, true);

        setEnableAllGridGraphOverlayButtons(false);
    }

    private void showStandardDownholeFractionationRawRatioDataModels() {
        // assumption is that tripoliSessionRawDataView is initialized       
        overlayPlot_radioButton.setSelected(true);
        setEnableAllGridGraphOverlayButtons(false);

        ((TripoliSessionRawDataView) tripoliSessionRawDataView).setFRACTION_LAYOUT_VIEW_STYLE(FractionLayoutViewStylesEnum.OVERLAY);
        tripoliSessionRawDataView.setBackground(interceptFitEachRefMaterial_radioButton.getBackground());
        ((TripoliSessionRawDataView) tripoliSessionRawDataView).setDataModelViewConstructor(//
                dataModelViewConstructorFactory(FitFunctionsOnRatioDataView.class.getName()));

        ((TripoliSessionRawDataView) tripoliSessionRawDataView).setRawDataSourceMethod(//
                rawDataSourceMethodFactory("getNonPbRatiosForFractionFitting"));//getValidRawRatioAlphas"));

        updateSamplesSelection(true, false);

        repaint();

        int saveWidth = ((TripoliSessionRawDataView) tripoliSessionRawDataView).getDataModelWidth();
        //cause it to fire
        xAxisZoomSlider.setValue(((TripoliSessionRawDataView) tripoliSessionRawDataView).DEFAULT_WIDTH_OF_SESSION_PANES);

        ((TripoliSessionRawDataView) tripoliSessionRawDataView).setDataModelWidth(saveWidth);
    }

    private void showStandardInterceptFractionationDataModels() {
        // assumption is that tripoliSessionRawDataView is initialized

        gridPlot_radioButton.setSelected(true);

        ((TripoliSessionRawDataView) tripoliSessionRawDataView).setFRACTION_LAYOUT_VIEW_STYLE(FractionLayoutViewStylesEnum.GRID_INTERCEPT);
        tripoliSessionRawDataView.setBackground(interceptFitEachRefMaterial_radioButton.getBackground());
        ((TripoliSessionRawDataView) tripoliSessionRawDataView).setDataModelViewConstructor(//
                dataModelViewConstructorFactory(FitFunctionsOnRatioDataView.class.getName()));

        ((TripoliSessionRawDataView) tripoliSessionRawDataView).setRawDataSourceMethod(//
                rawDataSourceMethodFactory("getRatiosForFractionFitting"));//("getRatiosForUnknownFitting"));//  "getValidRawRatioAlphas" ) );

        //cause slider to synch
        ((TripoliSessionRawDataView) tripoliSessionRawDataView).synchXAxisZoomSliderValue(((TripoliSessionRawDataView) tripoliSessionRawDataView).getDataModelWidth());

        updateSamplesSelection(true, false);

        setEnableAllGridGraphOverlayButtons(false);

        repaint();
    }

    private void showStandardDownHoleFractionationDataModels() {
        // assumption is that tripoliSessionRawDataView is initialized

        gridPlot_radioButton.setSelected(true);

        ((TripoliSessionRawDataView) tripoliSessionRawDataView).setFRACTION_LAYOUT_VIEW_STYLE(FractionLayoutViewStylesEnum.GRID_INTERCEPT);
        tripoliSessionRawDataView.setBackground(interceptFitEachRefMaterial_radioButton.getBackground());
        ((TripoliSessionRawDataView) tripoliSessionRawDataView).setDataModelViewConstructor(//
                dataModelViewConstructorFactory(FitFunctionsOnDownHoleRatioDataView.class.getName()));

        ((TripoliSessionRawDataView) tripoliSessionRawDataView).setRawDataSourceMethod(//
                rawDataSourceMethodFactory("getNonPbRatiosForFractionFitting"));// sep 2015 keep common lead out of downhole standards ("getRatiosForFractionFitting"));

        //cause slider to synch
        ((TripoliSessionRawDataView) tripoliSessionRawDataView).synchXAxisZoomSliderValue(((TripoliSessionRawDataView) tripoliSessionRawDataView).getDataModelWidth());

        updateSamplesSelection(true, false);

        setEnableAllGridGraphOverlayButtons(false);

        repaint();
    }

    private void showUnknownInterceptFractionationDataModels() {
        // assumption is that tripoliSessionRawDataView is initialized

        gridPlot_radioButton.setSelected(true);

        ((TripoliSessionRawDataView) tripoliSessionRawDataView).setFRACTION_LAYOUT_VIEW_STYLE(FractionLayoutViewStylesEnum.GRID_INTERCEPT);
        tripoliSessionRawDataView.setBackground(interceptFitEachRefMaterial_radioButton.getBackground());
        ((TripoliSessionRawDataView) tripoliSessionRawDataView).setDataModelViewConstructor(//
                dataModelViewConstructorFactory(FitFunctionsOnRatioDataView.class.getName()));

        ((TripoliSessionRawDataView) tripoliSessionRawDataView).setRawDataSourceMethod(//
                rawDataSourceMethodFactory("getRatiosForFractionFitting"));

        //cause slider to synch
        ((TripoliSessionRawDataView) tripoliSessionRawDataView)//
                .synchXAxisZoomSliderValue(((TripoliSessionRawDataView) tripoliSessionRawDataView).getDataModelWidth());

        updateSamplesSelection(false, true);

        setEnableAllGridGraphOverlayButtons(false);

        repaint();
    }

    private void showSessionViewOfRawDataModels(FractionationTechniquesEnum fractionationTechnique) {
        // assumption is that tripoliSessionRawDataView is initialized

        tripoliSession.setFractionationTechnique(fractionationTechnique);

        // do the math
        if (!tripoliSession.isFitFunctionsUpToDate()) {
            tripoliSession.calculateSessionFitFunctionsForPrimaryStandard(false);
        }
        try {
            uPbReduxFrame.updateReportTable(true, false);
        } catch (Exception e) {
        }

        updateSamplesSelection(true, false);

        if (fractionationTechnique.compareTo(FractionationTechniquesEnum.DOWNHOLE) == 0) {
            tripoliSessionRawDataView.setDataPresentationMode(DataPresentationModeEnum.LOGRATIO);
            ((TripoliSessionRawDataView) tripoliSessionRawDataView).showSessionViewOfRawDataModels(//
                    interceptRefMaterialSession_radioButton.getBackground(),//
                    dataModelViewConstructorFactory(SessionOfStandardView.class.getName()),//
                    rawDataSourceMethodFactory("getNonPbRatiosForFractionFitting"));//getValidRawRatioAlphas"));"getValidRawRatioAlphas"));

        } else {
            tripoliSessionRawDataView.setDataPresentationMode(DataPresentationModeEnum.RATIO);
            ((TripoliSessionRawDataView) tripoliSessionRawDataView).showSessionViewOfRawDataModels(//
                    interceptRefMaterialSession_radioButton.getBackground(),//
                    dataModelViewConstructorFactory(SessionOfStandardView.class.getName()),//
                    rawDataSourceMethodFactory("getRatiosForFractionFitting"));//getValidRawRatioAlphas"));"getValidRawRatioAlphas"));

        }

        setEnableAllGridGraphOverlayButtons(false);

        repaint();
    }

    private void setEnableAllGridGraphOverlayButtons(boolean enableRawViewControls) {

        gridPlot_radioButton.setEnabled(enableRawViewControls);
        graphPlot_radioButton.setEnabled(enableRawViewControls);
        overlayPlot_radioButton.setEnabled(enableRawViewControls);
    }

    private void gridPlotFractions() {
        ((TripoliSessionRawDataView) tripoliSessionRawDataView).setFRACTION_LAYOUT_VIEW_STYLE(FractionLayoutViewStylesEnum.GRID);
        tripoliSessionRawDataView.preparePanel(true, false);
        tripoliSessionDataView_scrollPane.revalidate();
    }

    private void graphPlotFractions() {
        ((TripoliSessionRawDataView) tripoliSessionRawDataView).setFRACTION_LAYOUT_VIEW_STYLE(FractionLayoutViewStylesEnum.GRAPH);
        tripoliSessionRawDataView.preparePanel(true, false);
        tripoliSessionDataView_scrollPane.revalidate();
    }

    private void overlayPlotFractions() {
        ((TripoliSessionRawDataView) tripoliSessionRawDataView).setFRACTION_LAYOUT_VIEW_STYLE(FractionLayoutViewStylesEnum.OVERLAY);
        tripoliSessionRawDataView.refreshPanel(true, false);
    }

    /**
     *
     */
    public void includeAllFractions() {
        ((TripoliSessionRawDataView) tripoliSessionRawDataView).includeAllFractions();

//        currentFractionView = IncludedTypeEnum.ALL;
        showAllFractions_radioButton.setSelected(true);

        // jan 2015
       // moved to button may 2016 tripoliSession.calculateSessionFitFunctionsForPrimaryStandard();
        try {
            uPbReduxFrame.updateReportTable(true, false);
        } catch (Exception e) {
        }
    }

    /**
     *
     */
    public void includeAllAquisitions() {
        ((TripoliSessionRawDataView) tripoliSessionRawDataView).includeAllAquisitions();

        // jan 2015
        // moved to button may 2016 tripoliSession.calculateSessionFitFunctionsForPrimaryStandard();
        try {
            uPbReduxFrame.updateReportTable(true, false);
        } catch (Exception e) {
        }
    }

    private void adjustViewPanes() {

        Rectangle myBounds = tripoliTab_layeredPane.getBounds();

        // TODO: remove magic numbers
        controlPanel_panel.setSize(191, (int) myBounds.getHeight());

        yAxisZoomSlider.setLocation( //
                controlPanel_panel.getWidth() - yAxisZoomSlider.getWidth(),
                controlPanel_panel.getHeight() - yAxisZoomSlider.getHeight() - 20);

        xAxisZoomSlider.setLocation( //
                controlPanel_panel.getWidth(),//
                controlPanel_panel.getHeight() - 20);

        lockUnlockZoomSliders.setLocation( //
                controlPanel_panel.getWidth() - lockUnlockZoomSliders.getWidth(),//
                controlPanel_panel.getHeight() - lockUnlockZoomSliders.getHeight());

        defaultZoom_button.setLocation(0, //
                controlPanel_panel.getHeight() - defaultZoom_button.getHeight());

        refreshView_button.setLocation(0,//
                controlPanel_panel.getHeight() - 2 * defaultZoom_button.getHeight() - 2);

        tripoliSessionDataView_scrollPane.setBounds(//
                controlPanel_panel.getWidth() + tripoliSessionRawDataViewYAxis.getWidth(), //
                OFFSET_TO_TOP_DATA_DISPLAY, //
                (int) myBounds.getWidth() - controlPanel_panel.getWidth() - tripoliSessionRawDataViewYAxis.getWidth(), //
                controlPanel_panel.getHeight() - 20 - OFFSET_TO_TOP_DATA_DISPLAY);

        tripoliSessionDataHeader_pane.setBounds(//
                controlPanel_panel.getWidth() + WIDTH_OF_YAXIS_PANE, //
                0, //
                (int) myBounds.getWidth() - controlPanel_panel.getWidth() - tripoliSessionRawDataViewYAxis.getWidth(), //
                OFFSET_TO_TOP_DATA_DISPLAY);

        tripoliSessionRawDataViewYAxis.setSize( //
                tripoliSessionRawDataViewYAxis.getWidth(),//
                tripoliSessionDataView_scrollPane.getHeight() - HORIZ_SCROLL_BAR_HEIGHT);

        tripoliSessionRawDataViewYAxis.revalidate();
        tripoliSessionDataView_scrollPane.revalidate();

    }

    /**
     *
     */
    public void refreshView() {
        Enumeration buttons = includedFractions_buttonGroup.getElements();
        while (buttons.hasMoreElements()) {
            JRadioButton b = (JRadioButton) buttons.nextElement();
            if (b.isSelected()) {
                b.doClick();
                break;
            }
        }

        if (((TripoliSessionRawDataView) tripoliSessionRawDataView).isSAVED_YAXIS_IS_UNIFORM()) {
            uniformYaxis.doClick();
        } else {
            independentYaxis.doClick();
        }

        if (((TripoliSessionRawDataView) tripoliSessionRawDataView).getSAVED_DATA_USED_FOR_SCALING().equals(IncludedTypeEnum.ALL)) {
            allDataUsedForScaling.doClick();
        } else {
            includedDataUsedForScaling.doClick();
        }

    }

    /**
     *
     */
    @Override
    public void setSize() {
        setSize(1200, 750);
    }

    /**
     *
     * @return
     */
    @Override
    public boolean isInitialized() {
        return initialized;
    }

    /**
     *
     * @param referenceMaterialOnly the value of referenceMaterialOnly
     * @param selectLastUnknown the value of selectLastUnknown
     */
    private void updateSamplesSelection(boolean referenceMaterialOnly, boolean selectLastUnknown) {

        int index = samplesCompboBox.getSelectedIndex();
        samplesCompboBox.setEnabled(true);
        if (referenceMaterialOnly) {
            // only allow reference material into this view 
            samplesCompboBox.setSelectedIndex(-1);
            samplesCompboBox.setSelectedIndex(0);
            samplesCompboBox.setEnabled(false);
        } else if (selectLastUnknown) {
            // allow any sample discourage ref material at index 0 unless raw data
            samplesCompboBox.setSelectedIndex(-1);
            samplesCompboBox.setSelectedIndex(lastUsedUnknownSampleIndexForSampleSelector);
        } else {
            samplesCompboBox.setSelectedIndex(-1);
            samplesCompboBox.setSelectedIndex(index);
        }
    }

    private void saveProject() {
        projectManager.getProject().saveTheProjectAsSerializedReduxFile();
    }

    /**
     * This method is called from within the constructor to reInitialize the
     * form. WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        includedFractions_buttonGroup = new javax.swing.ButtonGroup();
        plotStyleFractions_buttonGroup = new javax.swing.ButtonGroup();
        viewChooser_buttonGroup = new javax.swing.ButtonGroup();
        yAxisScalingOptions = new javax.swing.ButtonGroup();
        dataScalingOptions = new javax.swing.ButtonGroup();
        sampleType_panel = new javax.swing.JPanel();
        sampleType_label = new javax.swing.JLabel();
        toolBar_panel = new javax.swing.JPanel();
        save_button =  new ET_JButton();
        outputSessionMeansAndStdErrsToFile_button = new javax.swing.JButton();
        switchToProjectManager_button =  new ET_JButton();
        switchToReductionManager_button =  new ET_JButton();
        tripoliDataProcessing_tabbedPane = new javax.swing.JTabbedPane();
        tripoliTab_layeredPane = new javax.swing.JLayeredPane();
        tripoliSessionDataView_scrollPane = new javax.swing.JScrollPane();
        controlPanel_panel = new javax.swing.JLayeredPane();
        fractionsShownLabel = new javax.swing.JLabel();
        rawIsotopes_radioButton = new javax.swing.JRadioButton();
        correctedIsotopes_radioButton = new javax.swing.JRadioButton();
        rawRatios_radioButton = new javax.swing.JRadioButton();
        lockUnlockZoomSliders = new javax.swing.JButton();
        showAllFractions_radioButton = new javax.swing.JRadioButton();
        showIncludedFractions_radioButton = new javax.swing.JRadioButton();
        includeAllFractions_button =  new ET_JButton();
        refreshView_button =  new ET_JButton();
        defaultZoom_button =  new ET_JButton();
        removeAllIndividualYAxisPanes_button =  new ET_JButton();
        jLabel4 = new javax.swing.JLabel();
        yAxisZoomSlider = new javax.swing.JSlider();
        fractionationTechniqueTabbedPane = new javax.swing.JTabbedPane();
        DownholePanel = new javax.swing.JPanel();
        downholeFitStandards_radioButton = new javax.swing.JRadioButton();
        downholeFitEachStandard_radioButton = new javax.swing.JRadioButton();
        downholeStandardSession_radioButton = new javax.swing.JRadioButton();
        downholeCorrectedUnknownRatios_radioButton = new javax.swing.JRadioButton();
        downholeCalculateRhos_button =  new ET_JButton();
        refitDownholeSession =  new ET_JButton();
        interceptPanel = new javax.swing.JPanel();
        interceptFitEachRefMaterial_radioButton = new javax.swing.JRadioButton();
        interceptRefMaterialSession_radioButton = new javax.swing.JRadioButton();
        interceptFitUnknownRatios_radioButton = new javax.swing.JRadioButton();
        interceptCalculatePbcCorrAndRhos_button1 =  new ET_JButton();
        refitInterceptSession =  new ET_JButton();
        restoreAllAquisitions =  new ET_JButton();
        jPanel1 = new javax.swing.JPanel();
        gridPlot_radioButton = new javax.swing.JRadioButton();
        graphPlot_radioButton = new javax.swing.JRadioButton();
        overlayPlot_radioButton = new javax.swing.JRadioButton();
        samplesCompboBox = new javax.swing.JComboBox<>();
        amPrimaryRefMaterial_label = new javax.swing.JLabel();
        yAxisOptionsLabel = new javax.swing.JLabel();
        uniformYaxis = new javax.swing.JRadioButton();
        independentYaxis = new javax.swing.JRadioButton();
        allDataUsedForScaling = new javax.swing.JRadioButton();
        dataScalingOptionsLabel = new javax.swing.JLabel();
        includedDataUsedForScaling = new javax.swing.JRadioButton();
        setAllIndividualYAxisPanes_button =  new ET_JButton();
        xAxisZoomSlider = new javax.swing.JSlider();
        tripoliSessionRawDataViewYAxis = new javax.swing.JLayeredPane();
        tripoliSessionDataHeader_pane = new javax.swing.JLayeredPane();
        tripoliSessionDataControls_pane = new javax.swing.JLayeredPane();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        sampleType_panel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        sampleType_label.setBackground(new java.awt.Color(255, 204, 102));
        sampleType_label.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        sampleType_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        sampleType_label.setText("LA ICP MS Workflow Manager for a Session of Raw Data Preparation");
        sampleType_label.setOpaque(true);

        org.jdesktop.layout.GroupLayout sampleType_panelLayout = new org.jdesktop.layout.GroupLayout(sampleType_panel);
        sampleType_panel.setLayout(sampleType_panelLayout);
        sampleType_panelLayout.setHorizontalGroup(
            sampleType_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(sampleType_label, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        sampleType_panelLayout.setVerticalGroup(
            sampleType_panelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(sampleType_label)
        );

        toolBar_panel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        toolBar_panel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        save_button.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        save_button.setText("Save");
        save_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                save_buttonActionPerformed(evt);
            }
        });
        toolBar_panel.add(save_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 3, 110, 20));

        outputSessionMeansAndStdErrsToFile_button.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        outputSessionMeansAndStdErrsToFile_button.setText("Output Session means and errors for ref mats");
        outputSessionMeansAndStdErrsToFile_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                outputSessionMeansAndStdErrsToFile_buttonActionPerformed(evt);
            }
        });
        toolBar_panel.add(outputSessionMeansAndStdErrsToFile_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(830, 3, 290, 20));

        switchToProjectManager_button.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        switchToProjectManager_button.setText("Switch to Project Manager");
        switchToProjectManager_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                switchToProjectManager_buttonActionPerformed(evt);
            }
        });
        toolBar_panel.add(switchToProjectManager_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(295, 3, 180, 20));

        switchToReductionManager_button.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        switchToReductionManager_button.setText("Switch to Reduction Manager");
        switchToReductionManager_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                switchToReductionManager_buttonActionPerformed(evt);
            }
        });
        toolBar_panel.add(switchToReductionManager_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(115, 3, 180, 20));

        tripoliDataProcessing_tabbedPane.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);
        tripoliDataProcessing_tabbedPane.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                tripoliDataProcessing_tabbedPaneComponentResized(evt);
            }
        });

        tripoliTab_layeredPane.setBackground(new java.awt.Color(250, 240, 230));
        tripoliTab_layeredPane.setOpaque(true);

        tripoliSessionDataView_scrollPane.setBorder(null);
        tripoliTab_layeredPane.add(tripoliSessionDataView_scrollPane);
        tripoliSessionDataView_scrollPane.setBounds(275, 70, 820, 530);

        fractionsShownLabel.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        fractionsShownLabel.setText("Fractions Shown:");
        controlPanel_panel.setLayer(fractionsShownLabel, javax.swing.JLayeredPane.PALETTE_LAYER);
        controlPanel_panel.add(fractionsShownLabel);
        fractionsShownLabel.setBounds(0, 435, 110, 16);

        rawIsotopes_radioButton.setBackground(new java.awt.Color(173, 204, 182));
        viewChooser_buttonGroup.add(rawIsotopes_radioButton);
        rawIsotopes_radioButton.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        rawIsotopes_radioButton.setText("Measured Intensities");
        rawIsotopes_radioButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        rawIsotopes_radioButton.setBorderPainted(true);
        rawIsotopes_radioButton.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        rawIsotopes_radioButton.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        rawIsotopes_radioButton.setOpaque(true);
        rawIsotopes_radioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rawIsotopes_radioButtonActionPerformed(evt);
            }
        });
        controlPanel_panel.add(rawIsotopes_radioButton);
        rawIsotopes_radioButton.setBounds(0, 70, 190, 20);

        correctedIsotopes_radioButton.setBackground(new java.awt.Color(204, 199, 173));
        viewChooser_buttonGroup.add(correctedIsotopes_radioButton);
        correctedIsotopes_radioButton.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        correctedIsotopes_radioButton.setText("Baseline-Corr Intensities");
        correctedIsotopes_radioButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        correctedIsotopes_radioButton.setBorderPainted(true);
        correctedIsotopes_radioButton.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        correctedIsotopes_radioButton.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        correctedIsotopes_radioButton.setOpaque(true);
        correctedIsotopes_radioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                correctedIsotopes_radioButtonActionPerformed(evt);
            }
        });
        controlPanel_panel.add(correctedIsotopes_radioButton);
        correctedIsotopes_radioButton.setBounds(0, 90, 190, 20);

        rawRatios_radioButton.setBackground(new java.awt.Color(204, 184, 173));
        viewChooser_buttonGroup.add(rawRatios_radioButton);
        rawRatios_radioButton.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        rawRatios_radioButton.setText("Raw Ratios");
        rawRatios_radioButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        rawRatios_radioButton.setBorderPainted(true);
        rawRatios_radioButton.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        rawRatios_radioButton.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        rawRatios_radioButton.setOpaque(true);
        rawRatios_radioButton.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                rawRatios_radioButtonStateChanged(evt);
            }
        });
        rawRatios_radioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rawRatios_radioButtonActionPerformed(evt);
            }
        });
        controlPanel_panel.add(rawRatios_radioButton);
        rawRatios_radioButton.setBounds(0, 110, 190, 20);

        lockUnlockZoomSliders.setBackground(new java.awt.Color(51, 51, 51));
        lockUnlockZoomSliders.setFont(new java.awt.Font("Braggadocio", 1, 24)); // NOI18N
        lockUnlockZoomSliders.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/earthtime/images/linked.png"))); // NOI18N
        lockUnlockZoomSliders.setToolTipText("Click to Freeze Histogram bin width.");
        lockUnlockZoomSliders.setAlignmentY(0.0F);
        lockUnlockZoomSliders.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        lockUnlockZoomSliders.setContentAreaFilled(false);
        lockUnlockZoomSliders.setDoubleBuffered(true);
        lockUnlockZoomSliders.setFocusable(false);
        lockUnlockZoomSliders.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lockUnlockZoomSliders.setMargin(new java.awt.Insets(0, 0, 0, 0));
        lockUnlockZoomSliders.setOpaque(true);
        lockUnlockZoomSliders.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/org/earthtime/images/unlinked.png"))); // NOI18N
        lockUnlockZoomSliders.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        lockUnlockZoomSliders.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lockUnlockZoomSlidersMouseEntered(evt);
            }
        });
        lockUnlockZoomSliders.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lockUnlockZoomSlidersActionPerformed(evt);
            }
        });
        controlPanel_panel.add(lockUnlockZoomSliders);
        lockUnlockZoomSliders.setBounds(160, 590, 25, 25);

        includedFractions_buttonGroup.add(showAllFractions_radioButton);
        showAllFractions_radioButton.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        showAllFractions_radioButton.setSelected(true);
        showAllFractions_radioButton.setText("All");
        showAllFractions_radioButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        showAllFractions_radioButton.setBorderPainted(true);
        showAllFractions_radioButton.setOpaque(true);
        showAllFractions_radioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showAllFractions_radioButtonActionPerformed(evt);
            }
        });
        controlPanel_panel.add(showAllFractions_radioButton);
        showAllFractions_radioButton.setBounds(0, 450, 70, 20);

        includedFractions_buttonGroup.add(showIncludedFractions_radioButton);
        showIncludedFractions_radioButton.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        showIncludedFractions_radioButton.setText("Included");
        showIncludedFractions_radioButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        showIncludedFractions_radioButton.setBorderPainted(true);
        showIncludedFractions_radioButton.setOpaque(true);
        showIncludedFractions_radioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showIncludedFractions_radioButtonActionPerformed(evt);
            }
        });
        controlPanel_panel.add(showIncludedFractions_radioButton);
        showIncludedFractions_radioButton.setBounds(70, 450, 80, 20);

        includeAllFractions_button.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        includeAllFractions_button.setText("Restore all Fractions");
        includeAllFractions_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                includeAllFractions_buttonActionPerformed(evt);
            }
        });
        controlPanel_panel.setLayer(includeAllFractions_button, javax.swing.JLayeredPane.PALETTE_LAYER);
        controlPanel_panel.add(includeAllFractions_button);
        includeAllFractions_button.setBounds(0, 480, 150, 20);

        refreshView_button.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        refreshView_button.setText("Refresh View");
        refreshView_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshView_buttonActionPerformed(evt);
            }
        });
        controlPanel_panel.add(refreshView_button);
        refreshView_button.setBounds(0, 575, 150, 20);

        defaultZoom_button.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        defaultZoom_button.setText("Default Zoom");
        defaultZoom_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                defaultZoom_buttonActionPerformed(evt);
            }
        });
        controlPanel_panel.add(defaultZoom_button);
        defaultZoom_button.setBounds(0, 595, 150, 20);

        removeAllIndividualYAxisPanes_button.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        removeAllIndividualYAxisPanes_button.setText("Remove all Local Y-Axis");
        removeAllIndividualYAxisPanes_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeAllIndividualYAxisPanes_buttonActionPerformed(evt);
            }
        });
        controlPanel_panel.setLayer(removeAllIndividualYAxisPanes_button, javax.swing.JLayeredPane.PALETTE_LAYER);
        controlPanel_panel.add(removeAllIndividualYAxisPanes_button);
        removeAllIndividualYAxisPanes_button.setBounds(0, 540, 150, 20);

        jLabel4.setFont(new java.awt.Font("SansSerif", 3, 10)); // NOI18N
        jLabel4.setText("   Select fractionation technique:");
        controlPanel_panel.add(jLabel4);
        jLabel4.setBounds(0, 145, 190, 13);

        yAxisZoomSlider.setMaximum(320);
        yAxisZoomSlider.setMinimum(64);
        yAxisZoomSlider.setOrientation(javax.swing.JSlider.VERTICAL);
        yAxisZoomSlider.setValue(128);
        controlPanel_panel.add(yAxisZoomSlider);
        yAxisZoomSlider.setBounds(0, 420, 20, 190);

        fractionationTechniqueTabbedPane.setBackground(new java.awt.Color(250, 240, 230));
        fractionationTechniqueTabbedPane.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        fractionationTechniqueTabbedPane.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        fractionationTechniqueTabbedPane.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        fractionationTechniqueTabbedPane.setOpaque(true);

        DownholePanel.setBackground(new java.awt.Color(250, 240, 230));
        DownholePanel.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        DownholePanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        downholeFitStandards_radioButton.setBackground(new java.awt.Color(173, 174, 204));
        viewChooser_buttonGroup.add(downholeFitStandards_radioButton);
        downholeFitStandards_radioButton.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        downholeFitStandards_radioButton.setText("Fit All Ref Materials");
        downholeFitStandards_radioButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        downholeFitStandards_radioButton.setBorderPainted(true);
        downholeFitStandards_radioButton.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        downholeFitStandards_radioButton.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        downholeFitStandards_radioButton.setOpaque(true);
        downholeFitStandards_radioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downholeFitStandards_radioButtonActionPerformed(evt);
            }
        });
        DownholePanel.add(downholeFitStandards_radioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 168, 20));

        downholeFitEachStandard_radioButton.setBackground(new java.awt.Color(173, 174, 204));
        viewChooser_buttonGroup.add(downholeFitEachStandard_radioButton);
        downholeFitEachStandard_radioButton.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        downholeFitEachStandard_radioButton.setText("Fit Each Ref Material");
        downholeFitEachStandard_radioButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        downholeFitEachStandard_radioButton.setBorderPainted(true);
        downholeFitEachStandard_radioButton.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        downholeFitEachStandard_radioButton.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        downholeFitEachStandard_radioButton.setOpaque(true);
        downholeFitEachStandard_radioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downholeFitEachStandard_radioButtonActionPerformed(evt);
            }
        });
        DownholePanel.add(downholeFitEachStandard_radioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 25, 168, 20));

        downholeStandardSession_radioButton.setBackground(new java.awt.Color(204, 204, 204));
        viewChooser_buttonGroup.add(downholeStandardSession_radioButton);
        downholeStandardSession_radioButton.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        downholeStandardSession_radioButton.setText("Show Session");
        downholeStandardSession_radioButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        downholeStandardSession_radioButton.setBorderPainted(true);
        downholeStandardSession_radioButton.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        downholeStandardSession_radioButton.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        downholeStandardSession_radioButton.setOpaque(true);
        downholeStandardSession_radioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downholeStandardSession_radioButtonActionPerformed(evt);
            }
        });
        DownholePanel.add(downholeStandardSession_radioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(58, 50, 110, 20));

        downholeCorrectedUnknownRatios_radioButton.setBackground(new java.awt.Color(173, 204, 204));
        viewChooser_buttonGroup.add(downholeCorrectedUnknownRatios_radioButton);
        downholeCorrectedUnknownRatios_radioButton.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        downholeCorrectedUnknownRatios_radioButton.setText("Correct Unknowns");
        downholeCorrectedUnknownRatios_radioButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        downholeCorrectedUnknownRatios_radioButton.setBorderPainted(true);
        downholeCorrectedUnknownRatios_radioButton.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        downholeCorrectedUnknownRatios_radioButton.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        downholeCorrectedUnknownRatios_radioButton.setOpaque(true);
        downholeCorrectedUnknownRatios_radioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downholeCorrectedUnknownRatios_radioButtonActionPerformed(evt);
            }
        });
        DownholePanel.add(downholeCorrectedUnknownRatios_radioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 75, 168, -1));

        downholeCalculateRhos_button.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        downholeCalculateRhos_button.setText("Update Report Table");
        downholeCalculateRhos_button.setActionCommand("");
        downholeCalculateRhos_button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        downholeCalculateRhos_button.setOpaque(true);
        downholeCalculateRhos_button.setPreferredSize(new java.awt.Dimension(314, 36));
        downholeCalculateRhos_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downholeCalculateRhos_buttonActionPerformed(evt);
            }
        });
        DownholePanel.add(downholeCalculateRhos_button, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 98, 165, -1));

        refitDownholeSession.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        refitDownholeSession.setText("Refit");
        refitDownholeSession.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refitDownholeSessionActionPerformed(evt);
            }
        });
        DownholePanel.add(refitDownholeSession, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 50, 55, 20));

        fractionationTechniqueTabbedPane.addTab("Downhole", DownholePanel);

        interceptPanel.setBackground(new java.awt.Color(250, 240, 230));
        interceptPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        interceptFitEachRefMaterial_radioButton.setBackground(new java.awt.Color(173, 174, 204));
        viewChooser_buttonGroup.add(interceptFitEachRefMaterial_radioButton);
        interceptFitEachRefMaterial_radioButton.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        interceptFitEachRefMaterial_radioButton.setText("Fit each Ref Material");
        interceptFitEachRefMaterial_radioButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        interceptFitEachRefMaterial_radioButton.setBorderPainted(true);
        interceptFitEachRefMaterial_radioButton.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        interceptFitEachRefMaterial_radioButton.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        interceptFitEachRefMaterial_radioButton.setOpaque(true);
        interceptFitEachRefMaterial_radioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                interceptFitEachRefMaterial_radioButtonActionPerformed(evt);
            }
        });
        interceptPanel.add(interceptFitEachRefMaterial_radioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 168, 20));

        interceptRefMaterialSession_radioButton.setBackground(new java.awt.Color(204, 204, 204));
        viewChooser_buttonGroup.add(interceptRefMaterialSession_radioButton);
        interceptRefMaterialSession_radioButton.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        interceptRefMaterialSession_radioButton.setText("Show Session");
        interceptRefMaterialSession_radioButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        interceptRefMaterialSession_radioButton.setBorderPainted(true);
        interceptRefMaterialSession_radioButton.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        interceptRefMaterialSession_radioButton.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        interceptRefMaterialSession_radioButton.setOpaque(true);
        interceptRefMaterialSession_radioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                interceptRefMaterialSession_radioButtonActionPerformed(evt);
            }
        });
        interceptPanel.add(interceptRefMaterialSession_radioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(58, 30, 110, 20));

        interceptFitUnknownRatios_radioButton.setBackground(new java.awt.Color(173, 204, 204));
        viewChooser_buttonGroup.add(interceptFitUnknownRatios_radioButton);
        interceptFitUnknownRatios_radioButton.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        interceptFitUnknownRatios_radioButton.setText("Fit each Unknown");
        interceptFitUnknownRatios_radioButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        interceptFitUnknownRatios_radioButton.setBorderPainted(true);
        interceptFitUnknownRatios_radioButton.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        interceptFitUnknownRatios_radioButton.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        interceptFitUnknownRatios_radioButton.setOpaque(true);
        interceptFitUnknownRatios_radioButton.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                interceptFitUnknownRatios_radioButtonStateChanged(evt);
            }
        });
        interceptFitUnknownRatios_radioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                interceptFitUnknownRatios_radioButtonActionPerformed(evt);
            }
        });
        interceptPanel.add(interceptFitUnknownRatios_radioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 60, 168, -1));

        interceptCalculatePbcCorrAndRhos_button1.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        interceptCalculatePbcCorrAndRhos_button1.setText("Update Report Table");
        interceptCalculatePbcCorrAndRhos_button1.setActionCommand("");
        interceptCalculatePbcCorrAndRhos_button1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        interceptCalculatePbcCorrAndRhos_button1.setOpaque(true);
        interceptCalculatePbcCorrAndRhos_button1.setPreferredSize(new java.awt.Dimension(314, 36));
        interceptCalculatePbcCorrAndRhos_button1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                interceptCalculatePbcCorrAndRhos_button1ActionPerformed(evt);
            }
        });
        interceptPanel.add(interceptCalculatePbcCorrAndRhos_button1, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 98, 165, -1));

        refitInterceptSession.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        refitInterceptSession.setText("Refit");
        refitInterceptSession.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refitInterceptSessionActionPerformed(evt);
            }
        });
        interceptPanel.add(refitInterceptSession, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 30, 55, 20));

        fractionationTechniqueTabbedPane.addTab("Intercept", interceptPanel);

        controlPanel_panel.add(fractionationTechniqueTabbedPane);
        fractionationTechniqueTabbedPane.setBounds(0, 165, 190, 180);

        restoreAllAquisitions.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        restoreAllAquisitions.setText("Restore all Aquisitions");
        restoreAllAquisitions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                restoreAllAquisitionsActionPerformed(evt);
            }
        });
        controlPanel_panel.setLayer(restoreAllAquisitions, javax.swing.JLayeredPane.PALETTE_LAYER);
        controlPanel_panel.add(restoreAllAquisitions);
        restoreAllAquisitions.setBounds(0, 500, 150, 20);

        jPanel1.setBackground(new java.awt.Color(250, 240, 230));
        jPanel1.setPreferredSize(new java.awt.Dimension(191, 70));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        gridPlot_radioButton.setBackground(new java.awt.Color(204, 255, 204));
        plotStyleFractions_buttonGroup.add(gridPlot_radioButton);
        gridPlot_radioButton.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        gridPlot_radioButton.setText("Grid");
        gridPlot_radioButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        gridPlot_radioButton.setBorderPainted(true);
        gridPlot_radioButton.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        gridPlot_radioButton.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        gridPlot_radioButton.setOpaque(true);
        gridPlot_radioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gridPlot_radioButtonActionPerformed(evt);
            }
        });
        jPanel1.add(gridPlot_radioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 8, 50, -1));

        graphPlot_radioButton.setBackground(new java.awt.Color(204, 255, 204));
        plotStyleFractions_buttonGroup.add(graphPlot_radioButton);
        graphPlot_radioButton.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        graphPlot_radioButton.setText("Graph");
        graphPlot_radioButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        graphPlot_radioButton.setBorderPainted(true);
        graphPlot_radioButton.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        graphPlot_radioButton.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        graphPlot_radioButton.setOpaque(true);
        graphPlot_radioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                graphPlot_radioButtonActionPerformed(evt);
            }
        });
        jPanel1.add(graphPlot_radioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(50, 8, 65, -1));

        overlayPlot_radioButton.setBackground(new java.awt.Color(204, 255, 204));
        plotStyleFractions_buttonGroup.add(overlayPlot_radioButton);
        overlayPlot_radioButton.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        overlayPlot_radioButton.setText("Overlay");
        overlayPlot_radioButton.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        overlayPlot_radioButton.setBorderPainted(true);
        overlayPlot_radioButton.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        overlayPlot_radioButton.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        overlayPlot_radioButton.setOpaque(true);
        overlayPlot_radioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                overlayPlot_radioButtonActionPerformed(evt);
            }
        });
        jPanel1.add(overlayPlot_radioButton, new org.netbeans.lib.awtextra.AbsoluteConstraints(115, 8, 75, -1));
        jPanel1.add(samplesCompboBox, new org.netbeans.lib.awtextra.AbsoluteConstraints(2, 27, 185, -1));

        amPrimaryRefMaterial_label.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        amPrimaryRefMaterial_label.setForeground(new java.awt.Color(255, 51, 0));
        amPrimaryRefMaterial_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        amPrimaryRefMaterial_label.setText("ref mat?");
        jPanel1.add(amPrimaryRefMaterial_label, new org.netbeans.lib.awtextra.AbsoluteConstraints(15, 50, 150, -1));

        controlPanel_panel.add(jPanel1);
        jPanel1.setBounds(0, 0, 191, 70);

        yAxisOptionsLabel.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        yAxisOptionsLabel.setText("Y-axis Scaling Options:");
        controlPanel_panel.add(yAxisOptionsLabel);
        yAxisOptionsLabel.setBounds(0, 350, 160, 16);

        yAxisScalingOptions.add(uniformYaxis);
        uniformYaxis.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        uniformYaxis.setSelected(true);
        uniformYaxis.setText("Uniform");
        uniformYaxis.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        uniformYaxis.setBorderPainted(true);
        uniformYaxis.setOpaque(true);
        uniformYaxis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uniformYaxisActionPerformed(evt);
            }
        });
        controlPanel_panel.add(uniformYaxis);
        uniformYaxis.setBounds(0, 365, 75, 20);

        yAxisScalingOptions.add(independentYaxis);
        independentYaxis.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        independentYaxis.setText("Independent");
        independentYaxis.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        independentYaxis.setBorderPainted(true);
        independentYaxis.setOpaque(true);
        independentYaxis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                independentYaxisActionPerformed(evt);
            }
        });
        controlPanel_panel.add(independentYaxis);
        independentYaxis.setBounds(75, 365, 115, 20);

        dataScalingOptions.add(allDataUsedForScaling);
        allDataUsedForScaling.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        allDataUsedForScaling.setSelected(true);
        allDataUsedForScaling.setText("All Data");
        allDataUsedForScaling.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        allDataUsedForScaling.setBorderPainted(true);
        allDataUsedForScaling.setOpaque(true);
        allDataUsedForScaling.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allDataUsedForScalingActionPerformed(evt);
            }
        });
        controlPanel_panel.add(allDataUsedForScaling);
        allDataUsedForScaling.setBounds(0, 405, 75, 20);

        dataScalingOptionsLabel.setFont(new java.awt.Font("SansSerif", 0, 13)); // NOI18N
        dataScalingOptionsLabel.setText("Data Scaling Options:");
        controlPanel_panel.add(dataScalingOptionsLabel);
        dataScalingOptionsLabel.setBounds(0, 390, 150, 16);

        dataScalingOptions.add(includedDataUsedForScaling);
        includedDataUsedForScaling.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        includedDataUsedForScaling.setText("Included Data");
        includedDataUsedForScaling.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        includedDataUsedForScaling.setBorderPainted(true);
        includedDataUsedForScaling.setOpaque(true);
        includedDataUsedForScaling.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                includedDataUsedForScalingActionPerformed(evt);
            }
        });
        controlPanel_panel.add(includedDataUsedForScaling);
        includedDataUsedForScaling.setBounds(75, 405, 115, 20);

        setAllIndividualYAxisPanes_button.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        setAllIndividualYAxisPanes_button.setText("Show all Local Y-Axis");
        setAllIndividualYAxisPanes_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setAllIndividualYAxisPanes_buttonActionPerformed(evt);
            }
        });
        controlPanel_panel.setLayer(setAllIndividualYAxisPanes_button, javax.swing.JLayeredPane.PALETTE_LAYER);
        controlPanel_panel.add(setAllIndividualYAxisPanes_button);
        setAllIndividualYAxisPanes_button.setBounds(0, 520, 150, 20);

        tripoliTab_layeredPane.add(controlPanel_panel);
        controlPanel_panel.setBounds(0, 0, 191, 620);

        xAxisZoomSlider.setBackground(new java.awt.Color(230, 228, 228));
        xAxisZoomSlider.setMaximum(640);
        xAxisZoomSlider.setMinimum(4);
        xAxisZoomSlider.setValue(128);
        tripoliTab_layeredPane.setLayer(xAxisZoomSlider, javax.swing.JLayeredPane.PALETTE_LAYER);
        tripoliTab_layeredPane.add(xAxisZoomSlider);
        xAxisZoomSlider.setBounds(110, 600, 400, 20);

        tripoliSessionRawDataViewYAxis.setBackground(new java.awt.Color(255, 255, 255));
        tripoliSessionRawDataViewYAxis.setOpaque(true);
        tripoliTab_layeredPane.add(tripoliSessionRawDataViewYAxis);
        tripoliSessionRawDataViewYAxis.setBounds(190, 70, 80, 530);

        tripoliSessionDataHeader_pane.setBackground(new java.awt.Color(255, 255, 204));
        tripoliSessionDataHeader_pane.setOpaque(true);

        org.jdesktop.layout.GroupLayout tripoliSessionDataHeader_paneLayout = new org.jdesktop.layout.GroupLayout(tripoliSessionDataHeader_pane);
        tripoliSessionDataHeader_pane.setLayout(tripoliSessionDataHeader_paneLayout);
        tripoliSessionDataHeader_paneLayout.setHorizontalGroup(
            tripoliSessionDataHeader_paneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 820, Short.MAX_VALUE)
        );
        tripoliSessionDataHeader_paneLayout.setVerticalGroup(
            tripoliSessionDataHeader_paneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 70, Short.MAX_VALUE)
        );

        tripoliTab_layeredPane.add(tripoliSessionDataHeader_pane);
        tripoliSessionDataHeader_pane.setBounds(275, 0, 820, 70);

        tripoliSessionDataControls_pane.setOpaque(true);

        org.jdesktop.layout.GroupLayout tripoliSessionDataControls_paneLayout = new org.jdesktop.layout.GroupLayout(tripoliSessionDataControls_pane);
        tripoliSessionDataControls_pane.setLayout(tripoliSessionDataControls_paneLayout);
        tripoliSessionDataControls_paneLayout.setHorizontalGroup(
            tripoliSessionDataControls_paneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 80, Short.MAX_VALUE)
        );
        tripoliSessionDataControls_paneLayout.setVerticalGroup(
            tripoliSessionDataControls_paneLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 70, Short.MAX_VALUE)
        );

        tripoliTab_layeredPane.add(tripoliSessionDataControls_pane);
        tripoliSessionDataControls_pane.setBounds(190, 0, 80, 70);

        tripoliDataProcessing_tabbedPane.addTab("Tripoli", tripoliTab_layeredPane);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(sampleType_panel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(tripoliDataProcessing_tabbedPane)
            .add(toolBar_panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 1119, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(sampleType_panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(tripoliDataProcessing_tabbedPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 676, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(toolBar_panel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        close();
    }//GEN-LAST:event_formWindowClosing

    private void rawIsotopes_radioButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rawIsotopes_radioButtonActionPerformed
        showRawIsotopeDataModels(true);
    }//GEN-LAST:event_rawIsotopes_radioButtonActionPerformed

    private void correctedIsotopes_radioButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_correctedIsotopes_radioButtonActionPerformed
        showCorrectedIsotopeDataModels();
    }//GEN-LAST:event_correctedIsotopes_radioButtonActionPerformed

    private void rawRatios_radioButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rawRatios_radioButtonActionPerformed
        showRawRatioDataModels();
    }//GEN-LAST:event_rawRatios_radioButtonActionPerformed

    private void tripoliDataProcessing_tabbedPaneComponentResized (java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_tripoliDataProcessing_tabbedPaneComponentResized
        adjustViewPanes();
    }//GEN-LAST:event_tripoliDataProcessing_tabbedPaneComponentResized

    private void lockUnlockZoomSlidersMouseEntered (java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lockUnlockZoomSlidersMouseEntered

        if (((TripoliSessionRawDataView) tripoliSessionRawDataView).isZoomSlidersIndependent()) {
            lockUnlockZoomSliders.setToolTipText("Click to lock zooms. ");
        } else {
            lockUnlockZoomSliders.setToolTipText("Click to unlock zooms. ");
        }
}//GEN-LAST:event_lockUnlockZoomSlidersMouseEntered

    private void lockUnlockZoomSlidersActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lockUnlockZoomSlidersActionPerformed
        Icon oldPressed = lockUnlockZoomSliders.getPressedIcon();
        lockUnlockZoomSliders.setPressedIcon(lockUnlockZoomSliders.getIcon());
        lockUnlockZoomSliders.setIcon(oldPressed);
        ((TripoliSessionRawDataView) tripoliSessionRawDataView).setZoomSlidersIndependent(//
                !((TripoliSessionRawDataView) tripoliSessionRawDataView).isZoomSlidersIndependent());

}//GEN-LAST:event_lockUnlockZoomSlidersActionPerformed

    private void includeAllFractions_buttonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_includeAllFractions_buttonActionPerformed
        includeAllFractions();
    }//GEN-LAST:event_includeAllFractions_buttonActionPerformed

    private void showAllFractions_radioButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showAllFractions_radioButtonActionPerformed
        ((TripoliSessionRawDataView) tripoliSessionRawDataView).setFractionIncludedType(IncludedTypeEnum.ALL);
//        currentFractionView = IncludedTypeEnum.ALL;
        tripoliSessionRawDataView.refreshPanel(true, false);
    }//GEN-LAST:event_showAllFractions_radioButtonActionPerformed

    private void showIncludedFractions_radioButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showIncludedFractions_radioButtonActionPerformed
        ((TripoliSessionRawDataView) tripoliSessionRawDataView).setFractionIncludedType(IncludedTypeEnum.INCLUDED);
//        currentFractionView = IncludedTypeEnum.INCLUDED;
        tripoliSessionRawDataView.refreshPanel(true, false);

    }//GEN-LAST:event_showIncludedFractions_radioButtonActionPerformed

    private void refreshView_buttonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshView_buttonActionPerformed
        refreshView();
    }//GEN-LAST:event_refreshView_buttonActionPerformed

    private void defaultZoom_buttonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_defaultZoom_buttonActionPerformed
        setDefaultZoom();
}//GEN-LAST:event_defaultZoom_buttonActionPerformed

    private void save_buttonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_save_buttonActionPerformed
        saveProject();
    }//GEN-LAST:event_save_buttonActionPerformed

    private void graphPlot_radioButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_graphPlot_radioButtonActionPerformed
        graphPlotFractions();
    }//GEN-LAST:event_graphPlot_radioButtonActionPerformed

    private void overlayPlot_radioButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_overlayPlot_radioButtonActionPerformed
        overlayPlotFractions();
    }//GEN-LAST:event_overlayPlot_radioButtonActionPerformed

    private void gridPlot_radioButtonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gridPlot_radioButtonActionPerformed
        gridPlotFractions();
    }//GEN-LAST:event_gridPlot_radioButtonActionPerformed

    private void outputSessionMeansAndStdErrsToFile_buttonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_outputSessionMeansAndStdErrsToFile_buttonActionPerformed
        outputSessionMeansAndStdErrsToFile();
    }//GEN-LAST:event_outputSessionMeansAndStdErrsToFile_buttonActionPerformed

    private void rawRatios_radioButtonStateChanged (javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_rawRatios_radioButtonStateChanged
//        System.out.println( "Radio Button State Change" );
    }//GEN-LAST:event_rawRatios_radioButtonStateChanged

private void removeAllIndividualYAxisPanes_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeAllIndividualYAxisPanes_buttonActionPerformed
    ((TripoliSessionRawDataView) tripoliSessionRawDataView).removeAllLocalYAxisPanes();
}//GEN-LAST:event_removeAllIndividualYAxisPanes_buttonActionPerformed

    private void interceptRefMaterialSession_radioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_interceptRefMaterialSession_radioButtonActionPerformed
        showSessionViewOfRawDataModels(FractionationTechniquesEnum.INTERCEPT);
    }//GEN-LAST:event_interceptRefMaterialSession_radioButtonActionPerformed

    private void interceptFitEachRefMaterial_radioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_interceptFitEachRefMaterial_radioButtonActionPerformed
        showStandardInterceptFractionationDataModels();
    }//GEN-LAST:event_interceptFitEachRefMaterial_radioButtonActionPerformed

    private void interceptFitUnknownRatios_radioButtonStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_interceptFitUnknownRatios_radioButtonStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_interceptFitUnknownRatios_radioButtonStateChanged

    private void interceptFitUnknownRatios_radioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_interceptFitUnknownRatios_radioButtonActionPerformed
        showUnknownInterceptFractionationDataModels();
    }//GEN-LAST:event_interceptFitUnknownRatios_radioButtonActionPerformed

    private void downholeStandardSession_radioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downholeStandardSession_radioButtonActionPerformed
        showSessionViewOfRawDataModels(FractionationTechniquesEnum.DOWNHOLE);
    }//GEN-LAST:event_downholeStandardSession_radioButtonActionPerformed

    private void downholeFitStandards_radioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downholeFitStandards_radioButtonActionPerformed
        showStandardDownholeFractionationRawRatioDataModels();
    }//GEN-LAST:event_downholeFitStandards_radioButtonActionPerformed

    private void downholeCorrectedUnknownRatios_radioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downholeCorrectedUnknownRatios_radioButtonActionPerformed
        showDownholeFractionationCorrectedUnknownRawRatioDataModels(FractionSelectionTypeEnum.UNKNOWN);
    }//GEN-LAST:event_downholeCorrectedUnknownRatios_radioButtonActionPerformed

    private void switchToProjectManager_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_switchToProjectManager_buttonActionPerformed
        this.close();
        ((Component) projectManager).setVisible(true);
    }//GEN-LAST:event_switchToProjectManager_buttonActionPerformed

    private void restoreAllAquisitionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_restoreAllAquisitionsActionPerformed
        includeAllAquisitions();
    }//GEN-LAST:event_restoreAllAquisitionsActionPerformed

    private void switchToReductionManager_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_switchToReductionManager_buttonActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_switchToReductionManager_buttonActionPerformed

    private void downholeCalculateRhos_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downholeCalculateRhos_buttonActionPerformed

        // for now we have to undo internal acquisition rejecttions allowed by intercept but not by downhole
        tripoliSession.refitAllFractionsForDownhole();

        uPbReduxFrame.updateReportTable(true, false);
    }//GEN-LAST:event_downholeCalculateRhos_buttonActionPerformed

    private void downholeFitEachStandard_radioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_downholeFitEachStandard_radioButtonActionPerformed
        showStandardDownHoleFractionationDataModels();
    }//GEN-LAST:event_downholeFitEachStandard_radioButtonActionPerformed

    private void interceptCalculatePbcCorrAndRhos_button1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_interceptCalculatePbcCorrAndRhos_button1ActionPerformed
        // refit any  fractions not currently fitted
        try {
            tripoliSession.interceptCalculatePbcCorrAndRhos(false);
        } catch (Exception e) {
        }

        uPbReduxFrame.updateReportTable(true, false);
    }//GEN-LAST:event_interceptCalculatePbcCorrAndRhos_button1ActionPerformed

    private void uniformYaxisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uniformYaxisActionPerformed
        ((TripoliSessionRawDataView) tripoliSessionRawDataView).setSAVED_YAXIS_IS_UNIFORM(true);
        tripoliSessionRawDataView.refreshPanel(true, false);
    }//GEN-LAST:event_uniformYaxisActionPerformed

    private void independentYaxisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_independentYaxisActionPerformed
        ((TripoliSessionRawDataView) tripoliSessionRawDataView).setSAVED_YAXIS_IS_UNIFORM(false);
        tripoliSessionRawDataView.refreshPanel(true, false);
    }//GEN-LAST:event_independentYaxisActionPerformed

    private void allDataUsedForScalingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allDataUsedForScalingActionPerformed
        ((TripoliSessionRawDataView) tripoliSessionRawDataView).setSAVED_DATA_USED_FOR_SCALING(IncludedTypeEnum.ALL);
        tripoliSessionRawDataView.refreshPanel(true, false);
    }//GEN-LAST:event_allDataUsedForScalingActionPerformed

    private void includedDataUsedForScalingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_includedDataUsedForScalingActionPerformed
        ((TripoliSessionRawDataView) tripoliSessionRawDataView).setSAVED_DATA_USED_FOR_SCALING(IncludedTypeEnum.INCLUDED);
        tripoliSessionRawDataView.refreshPanel(true, false);
    }//GEN-LAST:event_includedDataUsedForScalingActionPerformed

    private void setAllIndividualYAxisPanes_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setAllIndividualYAxisPanes_buttonActionPerformed
        ((TripoliSessionRawDataView) tripoliSessionRawDataView).setAllLocalYAxisPanes(); 
    }//GEN-LAST:event_setAllIndividualYAxisPanes_buttonActionPerformed

    private void refitInterceptSessionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refitInterceptSessionActionPerformed
        tripoliSession.calculateSessionFitFunctionsForPrimaryStandard(false);
        uPbReduxFrame.updateReportTable(true, false);
        tripoliSessionRawDataView.refreshPanel(true, false);
    }//GEN-LAST:event_refitInterceptSessionActionPerformed

    private void refitDownholeSessionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refitDownholeSessionActionPerformed
        tripoliSession.calculateSessionFitFunctionsForPrimaryStandard(false);
        uPbReduxFrame.updateReportTable(true, false);
        tripoliSessionRawDataView.refreshPanel(true, false);
    }//GEN-LAST:event_refitDownholeSessionActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel DownholePanel;
    private javax.swing.JRadioButton allDataUsedForScaling;
    private javax.swing.JLabel amPrimaryRefMaterial_label;
    private javax.swing.JLayeredPane controlPanel_panel;
    private javax.swing.JRadioButton correctedIsotopes_radioButton;
    private javax.swing.ButtonGroup dataScalingOptions;
    private javax.swing.JLabel dataScalingOptionsLabel;
    private javax.swing.JButton defaultZoom_button;
    private javax.swing.JButton downholeCalculateRhos_button;
    private javax.swing.JRadioButton downholeCorrectedUnknownRatios_radioButton;
    private javax.swing.JRadioButton downholeFitEachStandard_radioButton;
    private javax.swing.JRadioButton downholeFitStandards_radioButton;
    private javax.swing.JRadioButton downholeStandardSession_radioButton;
    private javax.swing.JTabbedPane fractionationTechniqueTabbedPane;
    private javax.swing.JLabel fractionsShownLabel;
    private javax.swing.JRadioButton graphPlot_radioButton;
    private javax.swing.JRadioButton gridPlot_radioButton;
    private javax.swing.JButton includeAllFractions_button;
    private javax.swing.JRadioButton includedDataUsedForScaling;
    private javax.swing.ButtonGroup includedFractions_buttonGroup;
    private javax.swing.JRadioButton independentYaxis;
    private javax.swing.JButton interceptCalculatePbcCorrAndRhos_button1;
    private javax.swing.JRadioButton interceptFitEachRefMaterial_radioButton;
    private javax.swing.JRadioButton interceptFitUnknownRatios_radioButton;
    private javax.swing.JPanel interceptPanel;
    private javax.swing.JRadioButton interceptRefMaterialSession_radioButton;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JButton lockUnlockZoomSliders;
    private javax.swing.JButton outputSessionMeansAndStdErrsToFile_button;
    private javax.swing.JRadioButton overlayPlot_radioButton;
    private javax.swing.ButtonGroup plotStyleFractions_buttonGroup;
    private javax.swing.JRadioButton rawIsotopes_radioButton;
    private javax.swing.JRadioButton rawRatios_radioButton;
    private javax.swing.JButton refitDownholeSession;
    private javax.swing.JButton refitInterceptSession;
    private javax.swing.JButton refreshView_button;
    private javax.swing.JButton removeAllIndividualYAxisPanes_button;
    private javax.swing.JButton restoreAllAquisitions;
    private javax.swing.JLabel sampleType_label;
    private javax.swing.JPanel sampleType_panel;
    private javax.swing.JComboBox<AbstractTripoliSample> samplesCompboBox;
    private javax.swing.JButton save_button;
    private javax.swing.JButton setAllIndividualYAxisPanes_button;
    private javax.swing.JRadioButton showAllFractions_radioButton;
    private javax.swing.JRadioButton showIncludedFractions_radioButton;
    private javax.swing.JButton switchToProjectManager_button;
    private javax.swing.JButton switchToReductionManager_button;
    private javax.swing.JPanel toolBar_panel;
    private javax.swing.JTabbedPane tripoliDataProcessing_tabbedPane;
    private javax.swing.JLayeredPane tripoliSessionDataControls_pane;
    private javax.swing.JLayeredPane tripoliSessionDataHeader_pane;
    private javax.swing.JScrollPane tripoliSessionDataView_scrollPane;
    private javax.swing.JLayeredPane tripoliSessionRawDataViewYAxis;
    private javax.swing.JLayeredPane tripoliTab_layeredPane;
    private javax.swing.JRadioButton uniformYaxis;
    private javax.swing.ButtonGroup viewChooser_buttonGroup;
    private javax.swing.JSlider xAxisZoomSlider;
    private javax.swing.JLabel yAxisOptionsLabel;
    private javax.swing.ButtonGroup yAxisScalingOptions;
    private javax.swing.JSlider yAxisZoomSlider;
    // End of variables declaration//GEN-END:variables

    /**
     *
     */
    public void outputSessionMeansAndStdErrsToFile() {
        File alphaValuesFile = new File("INCLUDED_STANDARDS_SESSION_MEANS_N_ERRS_" + tripoliSession.getRawDataFileHandler().getRawDataFile().getName() + ".txt");
        PrintWriter outputWriter = null;
        try {
            outputWriter = new PrintWriter(new FileWriter(alphaValuesFile));
            outputWriter.println("\n****** INCLUDED_STANDARDS  SESSION DATA  from " + tripoliSession.getRawDataFileHandler().getRawDataFile().getName() + "   ********************");

            Iterator<RawRatioNames> sessionRatioNamesIterator = tripoliSession.getCurrentSessionForStandardsFractionation().keySet().iterator();
            while (sessionRatioNamesIterator.hasNext()) {
                RawRatioNames rrName = sessionRatioNamesIterator.next();

                AbstractSessionForStandardDataModel sessionForStandard = tripoliSession.getCurrentSessionForStandardsFractionation().get(rrName);

                outputWriter.println("\n****** Techinique = " + sessionForStandard.getSessionTechnique() + "\n");

                outputWriter.println(sessionForStandard.getSelectedFitFunction().showParameters());

                outputWriter.println("\n\nRatio = " + rrName);
                outputWriter.println("\n fractionName, elapsed seconds timeStamp, mean, oneSigma  \n");

                Iterator<TripoliFraction> fractionIterator = sessionForStandard.getStandardFractions().iterator();
                int i = 0;
                while (fractionIterator.hasNext()) {
                    TripoliFraction tf = fractionIterator.next();
                    if (tf.isIncluded()) {
                        double timeStamp = sessionForStandard.getIncludedStandardsAquireTimes()[i];
                        double mean = sessionForStandard.getIncludedStandardMeanLogRatios()[i];
                        double stdErr = sessionForStandard.getIncludedStandardsMeanLogRatioStdErrs()[i];

                        outputWriter.println(tf.getFractionID() + ",  " + timeStamp + ",   " + mean + ",   " + stdErr);

                        i++;
                    }
                }
            }

            outputWriter.println();

            outputWriter.flush();
            outputWriter.close();

        } catch (IOException iOException) {
        }

        try {
            BrowserControl.displayURL(alphaValuesFile.getCanonicalPath());
        } catch (IOException ex) {
        }

    }

    /**
     *
     */
    public void outputAlphaAveragePerRatioToFile() {
        File alphaValuesFile = new File("ALPHA_AVERAGE_PER_RATIO_" + tripoliSession.getRawDataFileHandler().getRawDataFile().getName() + ".txt");
        PrintWriter outputWriter = null;
        try {
            outputWriter = new PrintWriter(new FileWriter(alphaValuesFile));
            outputWriter.println("\n\n******   ALPHA AVERAGES  from " + tripoliSession.getRawDataFileHandler().getRawDataFile().getName() + "   ********************");

            Iterator fractionationAlphaIterator = tripoliSession.getDownholeFractionationDataModels().keySet().iterator();
            while (fractionationAlphaIterator.hasNext()) {
                RawRatioNames rrName = (RawRatioNames) fractionationAlphaIterator.next();
                DownholeFractionationDataModel fractionationAlpha = tripoliSession.getDownholeFractionationDataModels().get(rrName);

                outputWriter.println("\n\nRatio = " + rrName + "  x-value, y-value (avg)  \n");

                for (int i = 0; i < fractionationAlpha.getWeightedMeanIntegrations().length; i++) {
                    outputWriter.println("       " + (int) fractionationAlpha.getNormalizedAquireTimes()[i] + ", " + fractionationAlpha.getWeightedMeanIntegrations()[i]);
                }

            }

            outputWriter.println();

            outputWriter.flush();
            outputWriter.close();

        } catch (IOException iOException) {
        }

        try {
            BrowserControl.displayURL(alphaValuesFile.getCanonicalPath());
        } catch (IOException ex) {
        }

    }

    /**
     * @return the tripoliSession
     */
    public TripoliSessionInterface getTripoliSession() {
        return tripoliSession;
    }

    /**
     * @param tripoliSession the tripoliSession to set
     */
    public void setTripoliSession(TripoliSessionInterface tripoliSession) {
        this.tripoliSession = tripoliSession;
    }

    @Override
    public void revalidateScrollPane() {
        tripoliSessionDataView_scrollPane.revalidate();
    }

}
