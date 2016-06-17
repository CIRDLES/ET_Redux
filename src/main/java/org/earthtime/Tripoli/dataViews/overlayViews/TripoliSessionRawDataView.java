/*
 * TripoliSessionRawDataView.java
 *
 * Created Jul 6, 2011
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
package org.earthtime.Tripoli.dataViews.overlayViews;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.SortedSet;
import javax.swing.JLayeredPane;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.earthtime.ETReduxFrame;
import org.earthtime.Tripoli.dataModels.DataModelInterface;
import org.earthtime.Tripoli.dataModels.DownholeFractionationDataModel;
import org.earthtime.Tripoli.dataViews.AbstractRawDataView;
import org.earthtime.Tripoli.dataViews.fitFunctionPresentationViews.AbstractFitFunctionPresentationView;
import org.earthtime.Tripoli.dataViews.fitFunctionPresentationViews.AllFunctionsChoicePanel;
import org.earthtime.Tripoli.dataViews.fitFunctionPresentationViews.DownholeFitFunctionsPresentationView;
import org.earthtime.Tripoli.dataViews.fitFunctionPresentationViews.InterceptFitFunctionsPresentationView;
import org.earthtime.Tripoli.dataViews.fitFunctionPresentationViews.SessionFitFunctionsPresentationView;
import org.earthtime.Tripoli.dataViews.simpleViews.DataPresentationModeChooserPanel;
import org.earthtime.Tripoli.dataViews.simpleViews.FitFunctionDataInterface;
import org.earthtime.Tripoli.dataViews.simpleViews.FitFunctionResidualsView;
import org.earthtime.Tripoli.dataViews.simpleViews.FractionInfoPanel;
import org.earthtime.Tripoli.dataViews.simpleViews.ResidualsYAxisLabel;
import org.earthtime.Tripoli.dataViews.simpleViews.SessionOfStandardView;
import org.earthtime.Tripoli.dataViews.simpleViews.XAxisOverlayView;
import org.earthtime.Tripoli.dataViews.simpleViews.XAxisOverlayViewLabel;
import org.earthtime.Tripoli.dataViews.simpleViews.YAxisView;
import org.earthtime.Tripoli.dataViews.simpleViews.usedByReflection.FitFunctionsOnDownHoleRatioDataView;
import org.earthtime.Tripoli.dataViews.simpleViews.usedByReflection.FitFunctionsOnRatioDataView;
import org.earthtime.Tripoli.dataViews.simpleViews.usedByReflection.RawIntensitiesDataView;
import org.earthtime.Tripoli.dataViews.simpleViews.usedByReflection.RawRatioDataView;
import org.earthtime.Tripoli.fractions.TripoliFraction;
import org.earthtime.Tripoli.samples.AbstractTripoliSample;
import org.earthtime.Tripoli.sessions.TripoliSessionFractionationCalculatorInterface;
import org.earthtime.Tripoli.sessions.TripoliSessionInterface;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.dialogs.sessionManagers.SessionAnalysisWorkflowManagerInterface;
import org.earthtime.UPb_Redux.fractions.FractionsFilterInterface;
import org.earthtime.dataDictionaries.DataPresentationModeEnum;
import org.earthtime.dataDictionaries.FractionLayoutViewStylesEnum;
import org.earthtime.dataDictionaries.FractionSelectionTypeEnum;
import org.earthtime.dataDictionaries.FractionationTechniquesEnum;
import org.earthtime.dataDictionaries.IncludedTypeEnum;
import org.earthtime.dataDictionaries.RawRatioNames;
import org.earthtime.utilities.TicGeneratorForAxes;

/**
 *
 * @author James F. Bowring
 */
public class TripoliSessionRawDataView extends AbstractRawDataView implements TripoliSessionFractionationCalculatorInterface {

    /**
     *
     */
    public static int DEFAULT_HEIGHT_OF_FRACTION_PANES = 160;
    /**
     *
     */
    public static int DEFAULT_WIDTH_OF_FRACTION_PANES = 160;
    public static int DEFAULT_WIDTH_OF_SESSION_PANES = 490;
    private final static int WIDTH_OF_FIT_FUNCTION_PANES = 285;
    private final static int HEIGHT_OF_FIT_FUNCTION_RESIDUALS_PANES = 50;
    private final static int HEIGHT_OF_OVERLAY_XAXIS_PANES = 15;
    private final static int WIDTH_OF_LOCAL_YAXIS_PANES = 65;
    private final static int HEIGHT_OF_FRACTION_INFO_PANELS = 110;
    private static int SAVED_InterceptNormalModelWidth;
    private static int SAVED_InterceptOverlayModelWidth;
    private static int SAVED_DownholeNormalModelWidth;
    private static int SAVED_DownholeOverlayModelWidth;
    private static int SAVED_SessionModelWidth;
    private static boolean SAVED_YAXIS_IS_UNIFORM = true;
    private static IncludedTypeEnum SAVED_DATA_USED_FOR_SCALING = IncludedTypeEnum.ALL;

    private final TripoliSessionInterface tripoliSession;
    private SortedSet<TripoliFraction> tripoliFractions;
    private FractionSelectionTypeEnum fractionSelectionType;
    private IncludedTypeEnum fractionIncludedType;
    private final SortedMap<RawRatioNames, DownholeFractionationDataModel> downholeFractionationDataModels;
    private Constructor dataModelViewConstructor;
    private Method rawDataSourceMethod;
    private int countOfDataModels;
    public static FractionLayoutViewStylesEnum FRACTION_LAYOUT_VIEW_STYLE;
    private final JSlider yAxisZoomSlider;
    private final JSlider xAxisZoomSlider;
    private final JLayeredPane tripoliSessionRawDataViewYAxis;
    private int dataModelWidth;
    private int sessionModelWidth;
    private int dataModelHeight;
    private final int rightMargin = 1;
    private final int verticalGraphSeparation = 10;
    private final int horizontalGraphSeparation = 4;
    private boolean zoomSlidersIndependent;
    private TripoliSessionFractionationCalculatorInterface sessionFractionationCalculator;
    private int countOfLocalYAxisPanes = 0;
    private int countOfInterceptFitFunctionsPresentationPanes = 0;
    // control use of ChangeListeners
    private transient final ChangeListener yAxisZoomChangeListener;
    private transient final ChangeListener xAxisZoomChangeListener;
    // added jan 2012 for resetting sliders at view modes
    private AbstractRawDataView[][] rawDataModelViews;
    private AbstractRawDataView[][] interceptFitFunctionsPresentationViews;
    private final JLayeredPane tripoliSessionDataHeader_pane;
    private final JLayeredPane tripoliSessionDataControls_pane;
    private AbstractRawDataView dataPresentationModeChooserPanel;
    private SessionAnalysisWorkflowManagerInterface sessionAnalysisWorkflowManager;
    private transient AbstractTripoliSample selectedSample;

    /**
     *
     * @param myUPbReduxFrame
     * @param tripoliSession
     * @param dataModelViewConstructor
     * @param rawDataSourceMethod
     * @param fractionLayoutViewStyle
     * @param yAxisZoomSlider
     * @param xAxisZoomSlider
     * @param tripoliSessionRawDataViewYAxis
     * @param tripoliSessionDataHeader_pane
     * @param tripoliSessionDataControls_pane
     * @param scrollBounds
     * @param sessionAnalysisWorkflowManager the value of
     * sessionAnalysisWorkflowManager
     */
    public TripoliSessionRawDataView(//
            ETReduxFrame myUPbReduxFrame, //
            TripoliSessionInterface tripoliSession, //
            Constructor dataModelViewConstructor, Method rawDataSourceMethod, //
            FractionLayoutViewStylesEnum fractionLayoutViewStyle, //
            JSlider yAxisZoomSlider, JSlider xAxisZoomSlider, //
            JLayeredPane tripoliSessionRawDataViewYAxis, //
            JLayeredPane tripoliSessionDataHeader_pane, //
            JLayeredPane tripoliSessionDataControls_pane,//
            Rectangle scrollBounds, //
            SessionAnalysisWorkflowManagerInterface sessionAnalysisWorkflowManager) {
        super(scrollBounds);

        this.uPbReduxFrame = myUPbReduxFrame;
        this.tripoliSession = tripoliSession;
        this.fractionSelectionType = FractionSelectionTypeEnum.STANDARD;
        this.fractionIncludedType = IncludedTypeEnum.ALL;
        this.downholeFractionationDataModels = tripoliSession.getDownholeFractionationDataModels();
        this.dataModelViewConstructor = dataModelViewConstructor;
        this.rawDataSourceMethod = rawDataSourceMethod;
        FRACTION_LAYOUT_VIEW_STYLE = fractionLayoutViewStyle;
        this.yAxisZoomSlider = yAxisZoomSlider;
        this.xAxisZoomSlider = xAxisZoomSlider;
        this.tripoliSessionRawDataViewYAxis = tripoliSessionRawDataViewYAxis;
        this.tripoliSessionDataHeader_pane = tripoliSessionDataHeader_pane;
        this.tripoliSessionDataControls_pane = tripoliSessionDataControls_pane;

        // default values
        SAVED_InterceptNormalModelWidth = DEFAULT_WIDTH_OF_FRACTION_PANES;
        SAVED_InterceptOverlayModelWidth = DEFAULT_WIDTH_OF_FRACTION_PANES;
        SAVED_DownholeNormalModelWidth = DEFAULT_WIDTH_OF_FRACTION_PANES;
        SAVED_DownholeOverlayModelWidth = DEFAULT_WIDTH_OF_SESSION_PANES;
        SAVED_SessionModelWidth = DEFAULT_WIDTH_OF_SESSION_PANES;

        this.countOfDataModels = 0;
        this.dataModelWidth = DEFAULT_WIDTH_OF_FRACTION_PANES;
        this.sessionModelWidth = DEFAULT_WIDTH_OF_SESSION_PANES;
        this.dataModelHeight = DEFAULT_HEIGHT_OF_FRACTION_PANES;

        this.zoomSlidersIndependent = true;

        this.sessionFractionationCalculator = null;

        this.sessionAnalysisWorkflowManager = sessionAnalysisWorkflowManager;

        setBackground(new Color(204, 204, 204));

        // sliders
        yAxisZoomChangeListener = new YAxisZoomChangeListener(this, xAxisZoomSlider);
        xAxisZoomChangeListener = new XAxisZoomChangeListener(this, yAxisZoomSlider);
        // sep 2012 postpone adding them until everything prepared = initializeListeners()

        this.topMargin = 5;//HEIGHT_OF_FRACTION_INFO_PANELS + 5;

        // the first one should be reference material
        this.selectedSample = tripoliSession.getTripoliSamples().get(0);
    }

    /**
     *
     */
    public void initializeListeners() {
        yAxisZoomSlider.addChangeListener(yAxisZoomChangeListener);
        xAxisZoomSlider.addChangeListener(xAxisZoomChangeListener);
    }

    /**
     * @param zoomSlidersIndependent the zoomSlidersIndependent to set
     */
    public void setZoomSlidersIndependent(boolean zoomSlidersIndependent) {
        this.zoomSlidersIndependent = zoomSlidersIndependent;
    }

    /**
     * @return the zoomSlidersIndependent
     */
    public boolean isZoomSlidersIndependent() {
        return zoomSlidersIndependent;
    }

    /**
     * @param tripoliFractions the tripoliFractions to set
     */
    public void setTripoliFractions(SortedSet<TripoliFraction> tripoliFractions) {
        this.tripoliFractions = tripoliFractions;
    }

    /**
     *
     * @return
     */
    @Override
    public DataModelInterface getDataModel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * @param sessionFractionationCalculator the sessionFractionationCalculator
     * to set
     */
    public void setSessionFractionationCalculator(TripoliSessionFractionationCalculatorInterface sessionFractionationCalculator) {
        this.sessionFractionationCalculator = sessionFractionationCalculator;
    }

    /**
     * @return the sessionFractionationCalculator
     */
    public TripoliSessionFractionationCalculatorInterface getSessionFractionationCalculator() {
        return sessionFractionationCalculator;
    }

    /**
     * @param dataModelWidth the dataModelWidth to set
     */
    public void setDataModelWidth(int dataModelWidth) {
        this.dataModelWidth = dataModelWidth;
    }

    /**
     *
     * @return
     */
    public int getDataModelWidth() {
        return dataModelWidth;
    }

    /**
     * @return the FRACTION_LAYOUT_VIEW_STYLE
     */
    public FractionLayoutViewStylesEnum getFRACTION_LAYOUT_VIEW_STYLE() {
        return FRACTION_LAYOUT_VIEW_STYLE;
    }

    /**
     * @param fractionIncludedType the fractionIncludedType to set
     */
    public void setFractionIncludedType(IncludedTypeEnum fractionIncludedType) {
        this.fractionIncludedType = fractionIncludedType;
    }

    /**
     * @return the fractionIncludedType
     */
    public IncludedTypeEnum getFractionIncludedType() {
        return fractionIncludedType;
    }

    /**
     * @param fractionSelectionType the fractionSelectionType to set
     */
    public void setFractionSelectionType(FractionSelectionTypeEnum fractionSelectionType) {
        this.fractionSelectionType = fractionSelectionType;
    }

    /**
     * @return the tripoliSession
     */
    public TripoliSessionInterface getTripoliSession() {
        return tripoliSession;
    }

    /**
     * @return the dataPresentationMode
     */
    @Override
    public DataPresentationModeEnum getDataPresentationMode() {
        return dataPresentationMode;
    }

    /**
     * @param dataPresentationMode the dataPresentationMode to set
     */
    @Override
    public void setDataPresentationMode(DataPresentationModeEnum dataPresentationMode) {
        this.dataPresentationMode = dataPresentationMode;
    }

    @Override
    public void calculateDownholeFitSummariesForPrimaryStandard() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void calculateSessionFitFunctionsForPrimaryStandard(boolean inLiveMode) {
        tripoliSession.calculateSessionFitFunctionsForPrimaryStandard(inLiveMode);
    }

    @Override
    public void applyCorrections(boolean inLiveMode) {
        tripoliSession.applyCorrections(inLiveMode);
    }

    @Override
    public boolean isCalculatedInitialFitFunctions() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * @return the sessionModelWidth
     */
    public int getSessionModelWidth() {
        return sessionModelWidth;
    }

    /**
     * @param sessionModelWidth the sessionModelWidth to set
     */
    public void setSessionModelWidth(int sessionModelWidth) {
        this.sessionModelWidth = sessionModelWidth;
    }

    /**
     * @param selectedSample the selectedSample to set
     */
    public void setSelectedSample(AbstractTripoliSample selectedSample) {
        this.selectedSample = selectedSample;
    }

    class YAxisZoomChangeListener implements ChangeListener {

        JLayeredPane sampleSessionDataView;
        JSlider theOtherSlider;

        public YAxisZoomChangeListener(JLayeredPane sampleSessionDataView, JSlider theOtherSlider) {
            this.sampleSessionDataView = sampleSessionDataView;
            this.theOtherSlider = theOtherSlider;
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            if (!source.getValueIsAdjusting()) {
                int value = ((JSlider) source).getValue();
                dataModelHeight = value;
                if (!isZoomSlidersIndependent()) {
                    theOtherSlider.setValue(value);
                }
                ((AbstractRawDataView) sampleSessionDataView).refreshPanel(true);
                sessionAnalysisWorkflowManager.revalidateScrollPane();
            }
        }
    }

    class XAxisZoomChangeListener implements ChangeListener {

        JLayeredPane sampleSessionDataView;
        JSlider theOtherSlider;

        public XAxisZoomChangeListener(JLayeredPane sampleSessionDataView, JSlider theOtherSlider) {
            this.sampleSessionDataView = sampleSessionDataView;
            this.theOtherSlider = theOtherSlider;
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            JSlider source = (JSlider) e.getSource();
            if (!source.getValueIsAdjusting()) {
                int value = source.getValue();
                if (FRACTION_LAYOUT_VIEW_STYLE.equals(FractionLayoutViewStylesEnum.SESSION)) {
                    sessionModelWidth = value;
                    SAVED_SessionModelWidth = value;
                } else {
                    dataModelWidth = value;
                    if (tripoliSession.getFractionationTechnique().compareTo(FractionationTechniquesEnum.INTERCEPT) == 0) {
                        if (FRACTION_LAYOUT_VIEW_STYLE.equals(FractionLayoutViewStylesEnum.OVERLAY)) {
                            SAVED_InterceptOverlayModelWidth = value;
                        } else {
                            SAVED_InterceptNormalModelWidth = value;
                        }
                    } else if (FRACTION_LAYOUT_VIEW_STYLE.equals(FractionLayoutViewStylesEnum.OVERLAY)) {
                        SAVED_DownholeOverlayModelWidth = value;
                    } else {
                        SAVED_DownholeNormalModelWidth = value;
                    }
                }
                if (!isZoomSlidersIndependent()) {
                    theOtherSlider.setValue(value);
                }
                ((AbstractRawDataView) sampleSessionDataView).refreshPanel(true);
                sessionAnalysisWorkflowManager.revalidateScrollPane();
            }
        }
    }

    /**
     *
     * @param g2d
     */
    @Override
    public void paint(Graphics2D g2d) {

        super.paintInit(g2d);//sept 2012 was paint but not needed here
    }

    public void updateTripoliFractionSelection() {
        tripoliFractions = FractionsFilterInterface.getTripoliFractionsFiltered(//
                tripoliSession.getTripoliFractionsFromSample(selectedSample), //
                fractionSelectionType, IncludedTypeEnum.ALL);
    }

    /**
     *
     * @param doReScale the value of doReScale
     */
    @Override
    public void preparePanel(boolean doReScale) {

        // jan 2013 clean up before switching modes log / ratio / alpha
        if (rawDataModelViews != null) {
            for (AbstractRawDataView[] rawDataModelView : rawDataModelViews) {
                // contains session view fit function presentations with sliders
                try {
                    ((AbstractFitFunctionPresentationView) rawDataModelView[1]).resetValueModelSliders();
                } catch (Exception e) {
                }
            }
        }

        if (interceptFitFunctionsPresentationViews != null) {
            if (interceptFitFunctionsPresentationViews[0][0] != null) {
                for (AbstractRawDataView[] interceptFitFunctionsPresentationView : interceptFitFunctionsPresentationViews) {
                    for (AbstractRawDataView interceptFitFunctionsPresentationView1 : interceptFitFunctionsPresentationView) {
                        // contains individual fraction view fit function presentations with sliders
                        try {
                            ((AbstractFitFunctionPresentationView) interceptFitFunctionsPresentationView1).resetValueModelSliders();
                        } catch (Exception e) {
                        }
                    }
                }
            }
        }

        removeAll();
        validate();

        tripoliSessionRawDataViewYAxis.removeAll();
        tripoliSessionRawDataViewYAxis.validate();
        tripoliSessionRawDataViewYAxis.repaint();

        tripoliSessionDataHeader_pane.removeAll();
        tripoliSessionDataHeader_pane.validate();
        tripoliSessionDataHeader_pane.repaint();

        tripoliSessionDataControls_pane.removeAll();
        tripoliSessionDataControls_pane.validate();
        tripoliSessionDataControls_pane.repaint();

        dataPresentationModeChooserPanel = null;

        updateTripoliFractionSelection();

        boolean isFitFunctionsOnRatioDataView = false;
        boolean isFitFunctionsOnDownHoleRatioDataView = false;
        boolean isRawIntensitiesDataView = false;
        boolean isRawRatioDataView = false;
        if (!FRACTION_LAYOUT_VIEW_STYLE.equals(FractionLayoutViewStylesEnum.SESSION)) {
            try {
                isFitFunctionsOnRatioDataView = (dataModelViewConstructor.getDeclaringClass() == Class.forName(FitFunctionsOnRatioDataView.class.getCanonicalName()));
//                isFitFunctionsOnRatioDataView = isFitFunctionsOnRatioDataView || (dataModelViewConstructor.getDeclaringClass() == Class.forName(RawRatioDataViewForShrimp.class.getCanonicalName()));
                
                isFitFunctionsOnDownHoleRatioDataView = (dataModelViewConstructor.getDeclaringClass() == Class.forName(FitFunctionsOnDownHoleRatioDataView.class.getCanonicalName()));
                
                isRawIntensitiesDataView = (dataModelViewConstructor.getDeclaringClass() == Class.forName(RawIntensitiesDataView.class.getCanonicalName()));
//                isRawIntensitiesDataView = isRawIntensitiesDataView || (dataModelViewConstructor.getDeclaringClass() == Class.forName(RawCountsDataViewForShrimp.class.getCanonicalName()));
                
                isRawRatioDataView = (dataModelViewConstructor.getDeclaringClass() == Class.forName(RawRatioDataView.class.getCanonicalName()));
//                isRawRatioDataView = isRawRatioDataView || (dataModelViewConstructor.getDeclaringClass() == Class.forName(RawRatioDataViewForShrimp.class.getCanonicalName()));
            } catch (ClassNotFoundException classNotFoundException) {
            }
        }

        //prepare array to store and manage isotope views (fractions vertically, dataModel horizontally)
        TripoliFraction[] fractionMap = new TripoliFraction[tripoliFractions.size()];
        countOfDataModels = rawDataSourceFactory(tripoliFractions.first()).size();

        rawDataModelViews = new AbstractRawDataView[countOfDataModels][tripoliFractions.size()];
        AbstractRawDataView[][] localYAxisViews = new AbstractRawDataView[countOfDataModels][tripoliFractions.size()];
        interceptFitFunctionsPresentationViews = new AbstractRawDataView[countOfDataModels][tripoliFractions.size()];

        int fractionCountForHorizontalLayout = 0;
        int dataModelCountForVerticalLayout;

        // user friendly memory of x zooms
        if (FRACTION_LAYOUT_VIEW_STYLE.equals(FractionLayoutViewStylesEnum.SESSION)) {
            sessionModelWidth = SAVED_SessionModelWidth;
        } else if (tripoliSession.getFractionationTechnique().compareTo(FractionationTechniquesEnum.INTERCEPT) == 0) {
            if (FRACTION_LAYOUT_VIEW_STYLE.equals(FractionLayoutViewStylesEnum.OVERLAY)) {
                dataModelWidth = SAVED_InterceptOverlayModelWidth;
            } else {
                dataModelWidth = SAVED_InterceptNormalModelWidth;
            }
        } else if (FRACTION_LAYOUT_VIEW_STYLE.equals(FractionLayoutViewStylesEnum.OVERLAY)) {
            dataModelWidth = SAVED_DownholeOverlayModelWidth;
        } else {
            dataModelWidth = SAVED_DownholeNormalModelWidth;
        }

        // detect and create FRACTION_LAYOUT_VIEW_STYLE.SESSION view, which differs from all 
        // the others in that it only has one instance
        // that displays the session and spline curves through the fractons
        if (FRACTION_LAYOUT_VIEW_STYLE.equals(FractionLayoutViewStylesEnum.SESSION)) {

            // placeholder
            fractionMap[0] = tripoliFractions.first();
            // only one panel per dataModel
            rawDataModelViews = new AbstractRawDataView[countOfDataModels][2];

            dataModelCountForVerticalLayout = 0;

            SortedSet<DataModelInterface> dataModel = rawDataSourceFactory(tripoliFractions.first());

            Iterator<DataModelInterface> dataModelIterator = dataModel.iterator();
            while (dataModelIterator.hasNext()) {
                DataModelInterface dm = dataModelIterator.next();
                AbstractRawDataView rawDataModelView
                        = new SessionOfStandardView( //
                                this,//
                                tripoliSession.getCurrentSessionForStandardsFractionation().get(dm.getRawRatioModelName()), //
                                tripoliFractions,//
                                dm, //
                                dataPresentationMode,//
                                new Rectangle(//
                                        0, //
                                        dataModelCountForVerticalLayout * (dataModelHeight + verticalGraphSeparation) + topMargin, //
                                        sessionModelWidth,//
                                        dataModelHeight));

                // sept 2015
                rawDataModelView.setShowIncludedDataPoints(SAVED_DATA_USED_FOR_SCALING);

                rawDataModelView.preparePanel(doReScale);

                rawDataModelViews[dataModelCountForVerticalLayout][0] = rawDataModelView;

                // feb 2013 only for standards
                if ((fractionSelectionType.compareTo(FractionSelectionTypeEnum.STANDARD)) == 0) {
                    AbstractRawDataView sessionFitFunctionsPresentationView
                            = new SessionFitFunctionsPresentationView( //
                                    this,//
                                    tripoliSession.getCurrentSessionForStandardsFractionation().get(dm.getRawRatioModelName()), //
                                    (FitFunctionDataInterface) rawDataModelView,//
                                    dataPresentationMode, //
                                    new Rectangle( //
                                            0, //
                                            dataModelCountForVerticalLayout * (dataModelHeight + verticalGraphSeparation) + topMargin, //
                                            WIDTH_OF_FIT_FUNCTION_PANES, //
                                            dataModelHeight));

                    sessionFitFunctionsPresentationView.preparePanel(doReScale);

                    rawDataModelViews[dataModelCountForVerticalLayout][1] = sessionFitFunctionsPresentationView;

                }
                // jan 2013 add in data view chooser panel upper left corner
                makeDataPresentationModeChooserPanel(new double[0], isFitFunctionsOnDownHoleRatioDataView);
                if (tripoliSession.getFractionationTechnique().compareTo(FractionationTechniquesEnum.DOWNHOLE) == 0) {
                    ((DataPresentationModeChooserPanel) dataPresentationModeChooserPanel).setShowLogRatioButtonOnly();
                }
                dataModelCountForVerticalLayout++;
            }

            fractionCountForHorizontalLayout = 1; // we use 2, but hide the fitfractionpane until ready to show

            // end of session view setup **************************************** end of session view setup *************
        } else {

            Iterator<TripoliFraction> fractionIterator = tripoliFractions.iterator();
            while (fractionIterator.hasNext()) {
                TripoliFraction tf = fractionIterator.next();
                fractionMap[fractionCountForHorizontalLayout] = tf;

                dataModelCountForVerticalLayout = 0;

                SortedSet<DataModelInterface> dataModel = rawDataSourceFactory(tf);

                Iterator<DataModelInterface> dataModelIterator = dataModel.iterator();
                while (dataModelIterator.hasNext()) {
                    DataModelInterface dm = dataModelIterator.next();

                    AbstractRawDataView rawDataModelView
                            = rawDataViewFactory(//
                                    this,//
                                    tf, //
                                    dm, //
                                    dataModelCountForVerticalLayout, //
                                    !FRACTION_LAYOUT_VIEW_STYLE.equals(FractionLayoutViewStylesEnum.OVERLAY));

                    rawDataModelView.setShowIncludedDataPoints(SAVED_DATA_USED_FOR_SCALING);

                    rawDataModelView.setDataPresentationMode(dataPresentationMode);

                    rawDataModelView.preparePanel(doReScale);

                    rawDataModelViews[dataModelCountForVerticalLayout][fractionCountForHorizontalLayout] = rawDataModelView;

                    dataModelCountForVerticalLayout++;
                }

                // feb 2013 provide a way to relate all vertical fraction views for a single fraction
                // allow for matching fitfraction panes if made later
                AbstractRawDataView[] fractionSisterRawDataViews = new AbstractRawDataView[(dataModelCountForVerticalLayout * 2)];
                for (int i = 0; i < dataModelCountForVerticalLayout; i++) {
                    fractionSisterRawDataViews[i] = rawDataModelViews[i][fractionCountForHorizontalLayout];
                }
                for (int i = 0; i < dataModelCountForVerticalLayout; i++) {
                    rawDataModelViews[i][fractionCountForHorizontalLayout].setFractionRawDataViews(fractionSisterRawDataViews);
                }

                fractionCountForHorizontalLayout++;
            }
        }

        // make special rawDataModelViewsOverlays for OVERLAY case
        // adapt to overlay mode which needs residuals pane as well
        int residualsHeight = 0;
        AbstractRawDataView[] rawDataModelViewsOverlays = null;
        JLayeredPane[] underlays = null;

        if (FRACTION_LAYOUT_VIEW_STYLE.equals(FractionLayoutViewStylesEnum.OVERLAY)) {
            rawDataModelViewsOverlays = new AbstractRawDataView[countOfDataModels];

            underlays = new JLayeredPane[countOfDataModels];

            if (rawDataModelViews[0][0] instanceof FitFunctionsOnRatioDataView) {
                residualsHeight = HEIGHT_OF_FIT_FUNCTION_RESIDUALS_PANES;
            }
        }

        // normalize y axis for each kind of data model across fractions
        // init tripoliSessionRawDataViewYAxis
        tripoliSessionRawDataViewYAxis.setBackground(getBackground());
        tripoliSessionRawDataViewYAxis.setOpaque(true);

        tripoliSessionDataHeader_pane.setBackground(getBackground());
        tripoliSessionDataHeader_pane.setOpaque(true);

        tripoliSessionDataControls_pane.setBackground(getBackground());
        tripoliSessionDataControls_pane.setOpaque(true);

        // handle x-axis which is uniform across time
        double overallMinX = rawDataModelViews[0][0].getMinX();
        double overallMaxX = rawDataModelViews[0][0].getMaxX();
        double xMarginStretch = TicGeneratorForAxes.generateMarginAdjustment(overallMinX, overallMaxX, 0.05);
        overallMinX -= xMarginStretch;
        overallMaxX += xMarginStretch;

        for (int i = 0; i < countOfDataModels; i++) {
            double overallMinY = Double.MAX_VALUE;
            double overallMaxY = -Double.MAX_VALUE;
            boolean atLeastOneStandard = true;

            for (int f = 0; f < (fractionCountForHorizontalLayout); f++) {
                if (Double.isFinite(rawDataModelViews[i][f].getMinY()) && Double.isFinite(rawDataModelViews[i][f].getMaxY())) {
                    overallMinY = Math.min(overallMinY, rawDataModelViews[i][f].getMinY());
                    overallMaxY = Math.max(overallMaxY, rawDataModelViews[i][f].getMaxY());
                }
            }

            // generate tics array for all
            BigDecimal[] yAxisTics = null;
            yAxisTics = TicGeneratorForAxes.generateTics(overallMinY, overallMaxY, (int) (dataModelHeight / 20.0));
            if (yAxisTics.length > 15) {
                yAxisTics = TicGeneratorForAxes.generateTics(overallMinY, overallMaxY, (int) (dataModelHeight / 32.0));
            }

            // create margins for y-values after axis tics calculated
            double yMarginStretch = TicGeneratorForAxes.generateMarginAdjustment(overallMinY, overallMaxY, 12.0 / dataModelHeight);
            overallMinY -= yMarginStretch;
            overallMaxY += yMarginStretch;

            if (!(rawDataModelViews[0][0] instanceof SessionOfStandardView)) {

                // now apply to all fraction data views by walking across fractions within a dataModel type
                for (int f = 0; f < (fractionCountForHorizontalLayout); f++) {
                    rawDataModelViews[i][f].setMinX(overallMinX);
                    rawDataModelViews[i][f].setMaxX(overallMaxX);
                    if (SAVED_YAXIS_IS_UNIFORM) {
                        // the yaxis represents the normalized view across all sample fractions
                        rawDataModelViews[i][f].setMinY(overallMinY);
                        rawDataModelViews[i][f].setMaxY(overallMaxY);
                        rawDataModelViews[i][f].setTics(yAxisTics);
                    } else {
                        // each graph gets its own tic layout
                        BigDecimal[] yAxisTicsUnknown = TicGeneratorForAxes.generateTics(//
                                rawDataModelViews[i][f].getMinY(), rawDataModelViews[i][f].getMaxY(), (int) (dataModelHeight / 20.0));
                        if (yAxisTicsUnknown.length > 15) {
                            yAxisTicsUnknown = TicGeneratorForAxes.generateTics(//
                                    rawDataModelViews[i][f].getMinY(), rawDataModelViews[i][f].getMaxY(), (int) (dataModelHeight / 32.0));
                        }
                        rawDataModelViews[i][f].setTics(yAxisTicsUnknown);
                    }

                    // if fraction gets a local y-axis, then create it here********************************** local y axis
                    // these local axis boxes will be placed when their fraction is placed
                    if (fractionMap[f].isShowLocalYAxis()) {
                        AbstractRawDataView localYAxisPane = new YAxisView( //
                                new AbstractRawDataView[]{rawDataModelViews[i][f]},//
                                this,//
                                fractionMap[f].isStandard() ? Color.white : ReduxConstants.LightBlueForUnknowns, //
                                rawDataModelViews[i][f].getMinY(),//
                                rawDataModelViews[i][f].getMaxY(),//
                                new Rectangle( //
                                        1, i * (dataModelHeight + residualsHeight + verticalGraphSeparation) + topMargin, //
                                        WIDTH_OF_LOCAL_YAXIS_PANES, //
                                        dataModelHeight),//
                                true, atLeastOneStandard);
                        localYAxisPane.setTics(rawDataModelViews[i][f].getTics());

                        localYAxisViews[i][f] = localYAxisPane;

                        countOfLocalYAxisPanes++;
                    }

                    // if fraction gets a local fit function panel, then create it here ******************local fit function panel
                    // these local panels will be placed when their fraction is placed
                    if (fractionMap[f].isShowLocalInterceptFitPanel() //
                            && //
                            FRACTION_LAYOUT_VIEW_STYLE.equals(FractionLayoutViewStylesEnum.GRID_INTERCEPT)) {
                        AbstractRawDataView interceptFitFunctionsPresentationView
                                = //
                                new InterceptFitFunctionsPresentationView( //
                                        this, //
                                        rawDataModelViews[i][f].getDataModel(), //
                                        (FitFunctionDataInterface) rawDataModelViews[i][f],//
                                        new Rectangle( //
                                                1, i * (dataModelHeight + residualsHeight + verticalGraphSeparation) + topMargin, //
                                                WIDTH_OF_FIT_FUNCTION_PANES, //
                                                dataModelHeight),
                                        atLeastOneStandard, //
                                        isFitFunctionsOnDownHoleRatioDataView);

                        interceptFitFunctionsPresentationViews[i][f] = interceptFitFunctionsPresentationView;

                        rawDataModelViews[i][f].getFractionRawDataViews()[i + countOfDataModels] = interceptFitFunctionsPresentationView;

                        countOfInterceptFitFunctionsPresentationPanes++;
                    }
                }
            } // end check for session of standard view

            AbstractRawDataView[] standardsForYAxisArray;
            if (!FRACTION_LAYOUT_VIEW_STYLE.equals(FractionLayoutViewStylesEnum.SESSION)) {
                // create y-axis pane for standards that will stay to left of screen
                ArrayList<AbstractRawDataView> standardsForYAxis = new ArrayList<>();
                standardsForYAxis.addAll(Arrays.asList(rawDataModelViews[i]));

                standardsForYAxisArray = new AbstractRawDataView[standardsForYAxis.size()];
                for (int j = 0; j < standardsForYAxisArray.length; j++) {
                    standardsForYAxisArray[j] = standardsForYAxis.get(j);
                }
            } else {
                standardsForYAxisArray = rawDataModelViews[i];
            }

            AbstractRawDataView yAxisPane = null;

            //yaxis for outside scroll area
            yAxisPane = new YAxisView( //
                    standardsForYAxisArray,
                    this,//
                    Color.white, //
                    overallMinY,//
                    overallMaxY,//
                    new Rectangle( //
                            2, i * (dataModelHeight + residualsHeight + verticalGraphSeparation) + topMargin, //
                            tripoliSessionRawDataViewYAxis.getWidth() - 1, //
                            dataModelHeight),//
                    false, atLeastOneStandard);
            yAxisPane.setTics(yAxisTics);

            // this fires up equation chooser pane on the left of the display for either intercept frac or background fitting
            if ((!FRACTION_LAYOUT_VIEW_STYLE.equals(FractionLayoutViewStylesEnum.OVERLAY)) && (isFitFunctionsOnRatioDataView || isFitFunctionsOnDownHoleRatioDataView || isRawIntensitiesDataView)) {
                // add universal FitFunction chooser
                AbstractRawDataView universalFitFunctionChooser
                        = new AllFunctionsChoicePanel(//
                                this,
                                standardsForYAxisArray,//
                                new Rectangle( //
                                        15,//
                                        20, //
                                        115, //
                                        120),
                                atLeastOneStandard,
                                isFitFunctionsOnDownHoleRatioDataView);

                universalFitFunctionChooser.preparePanel(doReScale);
                yAxisPane.add(universalFitFunctionChooser, javax.swing.JLayeredPane.DRAG_LAYER);
            }
            tripoliSessionRawDataViewYAxis.add(yAxisPane, javax.swing.JLayeredPane.DEFAULT_LAYER);

            if (isFitFunctionsOnRatioDataView) {
                // data view chooser panel upper left corner
                makeDataPresentationModeChooserPanel(standardsForYAxisArray[0].getMyOnPeakNormalizedAquireTimes(), isFitFunctionsOnDownHoleRatioDataView);
                ((DataPresentationModeChooserPanel) dataPresentationModeChooserPanel).setHideODButtons();
            }
            if (isFitFunctionsOnDownHoleRatioDataView) {
                // data view chooser panel upper left corner log only
                makeDataPresentationModeChooserPanel(standardsForYAxisArray[0].getMyOnPeakNormalizedAquireTimes(), isFitFunctionsOnDownHoleRatioDataView);
                ((DataPresentationModeChooserPanel) dataPresentationModeChooserPanel).setShowLogRatioButtonOnly();
            }

            if (isRawRatioDataView) {
                // data view chooser panel upper left corner with ratio / log / alpha only
                makeDataPresentationModeChooserPanel(new double[0], isFitFunctionsOnDownHoleRatioDataView);
                ((DataPresentationModeChooserPanel) dataPresentationModeChooserPanel).setHideAlphaButton();
            }

            // for OVERLAY, create overlays
            if (FRACTION_LAYOUT_VIEW_STYLE.equals(FractionLayoutViewStylesEnum.OVERLAY)) {
                // adjust yaxis pane
                // dec 2012 there might be no standards
                try {
                    yAxisPane.setLocation(yAxisPane.getX(), yAxisPane.getY() + i * HEIGHT_OF_OVERLAY_XAXIS_PANES);
                } catch (Exception e) {
                }
                AbstractRawDataView rawDataModelViewsOverlay = new DataViewsOverlay(//
                        rawDataModelViews[i],
                        this, //
                        dataPresentationMode,//
                        new Rectangle(//
                                leftMargin, //
                                0,//i * (dataModelHeight + HEIGHT_OF_OVERLAY_XAXIS_PANES + residualsHeight + verticalGraphSeparation) + topMargin, //
                                dataModelWidth, //
                                dataModelHeight));
                rawDataModelViewsOverlay.setOpaque(false);
                rawDataModelViewsOverlay.setBackground(Color.WHITE);
                rawDataModelViewsOverlays[i] = rawDataModelViewsOverlay;
                try {
                    ((YAxisView) yAxisPane).setDataViewsOverlay(rawDataModelViewsOverlay);
                } catch (Exception e) {
                }

                JLayeredPane underlay = new JLayeredPane();
                underlay.setBounds( //
                        new Rectangle(//
                                leftMargin, //
                                i * (dataModelHeight + HEIGHT_OF_OVERLAY_XAXIS_PANES + residualsHeight + verticalGraphSeparation) + topMargin, //
                                dataModelWidth, //
                                dataModelHeight));
                underlay.setOpaque(true);
                underlay.setBackground(Color.WHITE);
                underlays[i] = underlay;

                // build x-axis pane for overviews
                AbstractRawDataView OverlayViewXAxis = new XAxisOverlayView(//
                        rawDataModelViews[0][0], //
                        new Rectangle(//
                                leftMargin,//
                                i * (dataModelHeight + HEIGHT_OF_OVERLAY_XAXIS_PANES + residualsHeight + verticalGraphSeparation) + topMargin + dataModelHeight, //
                                dataModelWidth, //
                                HEIGHT_OF_OVERLAY_XAXIS_PANES));

                OverlayViewXAxis.setMinX(overallMinX);
                OverlayViewXAxis.setMaxX(overallMaxX);
                OverlayViewXAxis.preparePanel(doReScale);

                add(OverlayViewXAxis, javax.swing.JLayeredPane.DEFAULT_LAYER);

                // build x-axis pane LABEL for overviews to be placed in yaxis column
                AbstractRawDataView overlayViewXAxisLabel = new XAxisOverlayViewLabel(//
                        rawDataModelViews[0][0], //
                        new Rectangle(//
                                1,//
                                i * (dataModelHeight + HEIGHT_OF_OVERLAY_XAXIS_PANES + residualsHeight + verticalGraphSeparation) + topMargin + dataModelHeight, //
                                tripoliSessionRawDataViewYAxis.getWidth() - 1, //
                                HEIGHT_OF_OVERLAY_XAXIS_PANES));

                overlayViewXAxisLabel.preparePanel(doReScale);

                // place overlayViewXAxisLabel in yaxis column
                tripoliSessionRawDataViewYAxis.add(overlayViewXAxisLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

                // detect if overlay for downhole fractionation *****************************************  OVERLAY for downhole fractionation
                // TODO: dec 2012 check for missing ratio due to all belowDetection
                if (rawDataModelViews[i][0] instanceof FitFunctionsOnRatioDataView) {
                    RawRatioNames rrName
                            = //
                            rawDataModelViews[i][0].getDataModel().getRawRatioModelName();

                    ((DataViewsOverlay) rawDataModelViewsOverlay).setFractionationDataModel(downholeFractionationDataModels.get(rrName));

                    // build DownholeFitFunctionsPresentationView panes
                    AbstractRawDataView fitFunctionView = new DownholeFitFunctionsPresentationView(//
                            this, //
                            downholeFractionationDataModels.get(rrName),//
                            (FitFunctionDataInterface) rawDataModelViewsOverlay, //
                            new Rectangle(//
                                    leftMargin + dataModelWidth + horizontalGraphSeparation, //
                                    i * (dataModelHeight + HEIGHT_OF_OVERLAY_XAXIS_PANES + residualsHeight + verticalGraphSeparation) + topMargin, //
                                    WIDTH_OF_FIT_FUNCTION_PANES, //
                                    dataModelHeight + HEIGHT_OF_OVERLAY_XAXIS_PANES + residualsHeight - 1));

                    fitFunctionView.preparePanel(doReScale);
                    add(fitFunctionView, javax.swing.JLayeredPane.DEFAULT_LAYER);

                    // build fit function residuals panes
                    AbstractRawDataView fitFunctionResidualsView = new FitFunctionResidualsView(//
                            downholeFractionationDataModels.get(rrName),//
                            dataPresentationMode, //
                            new Rectangle(//
                                    leftMargin,//
                                    i * (dataModelHeight + HEIGHT_OF_OVERLAY_XAXIS_PANES + residualsHeight + verticalGraphSeparation) + topMargin + dataModelHeight + HEIGHT_OF_OVERLAY_XAXIS_PANES, //
                                    dataModelWidth, //
                                    HEIGHT_OF_FIT_FUNCTION_RESIDUALS_PANES));

                    fitFunctionResidualsView.setMinX(overallMinX);
                    fitFunctionResidualsView.setMaxX(overallMaxX);
                    fitFunctionResidualsView.preparePanel(doReScale);

                    add(fitFunctionResidualsView, javax.swing.JLayeredPane.DEFAULT_LAYER);

                    // build residuals pane LABEL for overviews to be placed in yaxis column
                    AbstractRawDataView residualsYAxisLabel = new ResidualsYAxisLabel(//
                            rawDataModelViews[0][0], //
                            new Rectangle(//
                                    //
                                    1,//
                                    i * (dataModelHeight + HEIGHT_OF_OVERLAY_XAXIS_PANES + residualsHeight + verticalGraphSeparation) + topMargin + dataModelHeight + HEIGHT_OF_OVERLAY_XAXIS_PANES, //
                                    tripoliSessionRawDataViewYAxis.getWidth() - 1, //
                                    HEIGHT_OF_FIT_FUNCTION_RESIDUALS_PANES));

                    residualsYAxisLabel.preparePanel(doReScale);

                    // place in yaxis column
                    tripoliSessionRawDataViewYAxis.add(residualsYAxisLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

                    // march 2013 add in data view chooser panel upper left corner
                    makeDataPresentationModeChooserPanel(new double[0], isFitFunctionsOnDownHoleRatioDataView);
                }

                rawDataModelViewsOverlay.setMinY(overallMinY);
                rawDataModelViewsOverlay.setMaxY(overallMaxY);
                rawDataModelViewsOverlay.setMinX(overallMinX);
                rawDataModelViewsOverlay.setMaxX(overallMaxX);

                rawDataModelViewsOverlay.setTics(yAxisTics);
                rawDataModelViewsOverlay.preparePanel(doReScale);

                add(underlay, javax.swing.JLayeredPane.DEFAULT_LAYER);
                underlay.add(rawDataModelViewsOverlay, javax.swing.JLayeredPane.PALETTE_LAYER);

            } else // detect if grid for intercept fractionation of standards *********************************** Intercept Fractionation of standards
             if (FRACTION_LAYOUT_VIEW_STYLE.equals(FractionLayoutViewStylesEnum.GRID_INTERCEPT)) {
                    for (int f = 0; f < (fractionCountForHorizontalLayout); f++) {
                        ((FitFunctionDataInterface) rawDataModelViews[i][f]).setShowFittedFunction(true);
                    }
                }
        }

        // establish graphWidth 
        if (FRACTION_LAYOUT_VIEW_STYLE.equals(FractionLayoutViewStylesEnum.GRAPH)) {
            minX = tripoliFractions.first().getPeakTimeStamp();
            maxX = tripoliFractions.last().getPeakTimeStamp() //
                    + (rawDataModelViews[0][0].getRangeX_Display() * rawDataModelViews[0][0].getDataModel().getCollectorDataFrequencyMillisecs());
            graphWidth = (int) ((maxX - minX) * dataModelWidth //
                    / rawDataModelViews[0][0].getRangeX_Display() / rawDataModelViews[0][0].getDataModel().getCollectorDataFrequencyMillisecs());

        } else if (FRACTION_LAYOUT_VIEW_STYLE.equals(FractionLayoutViewStylesEnum.OVERLAY)) {
            minX = 0;
            maxX = dataModelWidth + horizontalGraphSeparation + WIDTH_OF_FIT_FUNCTION_PANES;
            graphWidth = (int) (maxX - minX);

        } else if (FRACTION_LAYOUT_VIEW_STYLE.equals(FractionLayoutViewStylesEnum.SESSION)) {
            minX = 0;
            maxX = fractionCountForHorizontalLayout * (sessionModelWidth + horizontalGraphSeparation);
            graphWidth = (int) (maxX - minX);
            graphWidth += countOfLocalYAxisPanes * WIDTH_OF_LOCAL_YAXIS_PANES + countOfInterceptFitFunctionsPresentationPanes * WIDTH_OF_FIT_FUNCTION_PANES;

        } else {
            minX = 0;
            maxX = fractionCountForHorizontalLayout * (dataModelWidth + horizontalGraphSeparation);
            graphWidth = (int) (maxX - minX);
            graphWidth += countOfLocalYAxisPanes * WIDTH_OF_LOCAL_YAXIS_PANES + countOfInterceptFitFunctionsPresentationPanes * WIDTH_OF_FIT_FUNCTION_PANES;
        }

        // create vertical fraction panes for layout on x = time axis ********************************************************
        countOfLocalYAxisPanes = 0;
        countOfInterceptFitFunctionsPresentationPanes = 0;
        // this provides for placing intercept panel after fraction
        boolean doIncreaseCountOfInterceptPanels = false;

        // this will allow loading of stored session fitfunctionpanes
        if (FRACTION_LAYOUT_VIEW_STYLE.equals(FractionLayoutViewStylesEnum.SESSION)) {
            fractionCountForHorizontalLayout = 2;
        }

        for (int f = 0; f < (fractionCountForHorizontalLayout); f++) {
            if (doIncreaseCountOfInterceptPanels) {
                countOfInterceptFitFunctionsPresentationPanes++;
                doIncreaseCountOfInterceptPanels = false;
            }

            // handle local y axis container panel for this fraction
            AbstractRawDataView[] localYAxisViewsArray = null;
            // handle local InterceptFitFunctionsPresentationPanes for this fraction
            AbstractRawDataView[] interceptFitFunctionsPresentationPanesArray = null;
            if (FRACTION_LAYOUT_VIEW_STYLE.equals(FractionLayoutViewStylesEnum.GRAPH)//
                    ||//
                    FRACTION_LAYOUT_VIEW_STYLE.equals(FractionLayoutViewStylesEnum.GRID)//
                    ||//
                    FRACTION_LAYOUT_VIEW_STYLE.equals(FractionLayoutViewStylesEnum.GRID_INTERCEPT)//
                    ) {
                // detect if localYAxis created
                if (fractionMap[f].isShowLocalYAxis()) {
                    localYAxisViewsArray = new AbstractRawDataView[countOfDataModels];
                    for (int i = 0; i < countOfDataModels; i++) {
                        localYAxisViewsArray[i] = localYAxisViews[i][f];
                    }
                    countOfLocalYAxisPanes++;
                }

                // detect if local InterceptFitFunctionsPresentationPane created 
                // fraction fits view is current
                if (fractionMap[f].isShowLocalInterceptFitPanel() //
                        && //
                        ((rawDataModelViews[0][0] instanceof FitFunctionsOnRatioDataView)//
                        ||//
                        (rawDataModelViews[0][0] instanceof FitFunctionsOnDownHoleRatioDataView))) {
                    interceptFitFunctionsPresentationPanesArray = new AbstractRawDataView[countOfDataModels];
                    for (int i = 0; i < countOfDataModels; i++) {
                        interceptFitFunctionsPresentationPanesArray[i] = interceptFitFunctionsPresentationViews[i][f];
                    }
                    doIncreaseCountOfInterceptPanels = true;
                }
            }

            // gather views within this fraction
            AbstractRawDataView[] fractionDataModelViewsArray = new AbstractRawDataView[countOfDataModels];
            for (int i = 0; i < countOfDataModels; i++) {
                fractionDataModelViewsArray[i] = rawDataModelViews[i][f];
            }

            // viewStyle calculations
            double mapTimeStamp = 0.0;
            if (FRACTION_LAYOUT_VIEW_STYLE.equals(FractionLayoutViewStylesEnum.GRAPH)) {
                mapTimeStamp = mapX(fractionMap[f].getPeakTimeStamp());
            } else {
                // yaxis local is inserted just before fraction
                // local intercept panel is to right of fraction
                mapTimeStamp = leftMargin//
                        + f * ((FRACTION_LAYOUT_VIEW_STYLE.equals(FractionLayoutViewStylesEnum.SESSION) ? sessionModelWidth : dataModelWidth) + horizontalGraphSeparation) //
                        + countOfLocalYAxisPanes * WIDTH_OF_LOCAL_YAXIS_PANES//
                        + countOfInterceptFitFunctionsPresentationPanes * WIDTH_OF_FIT_FUNCTION_PANES;
            }

            //  create vertical fraction panels for each type of vertical use
            JLayeredPane tripoliFractionViewsContainer = null;
            JLayeredPane tripoliFractionInfoViewsContainer = null;
            JLayeredPane layeredYAxisDataViewsContainer = null;
            JLayeredPane layeredLocalInterceptFitFunctionsContainer = null;

            if (!FRACTION_LAYOUT_VIEW_STYLE.equals(FractionLayoutViewStylesEnum.OVERLAY)) {
                tripoliFractionViewsContainer = new JLayeredPane(); //
                tripoliFractionViewsContainer.setBounds(new Rectangle( //
                        (int) mapTimeStamp,//
                        0, //
                        FRACTION_LAYOUT_VIEW_STYLE.equals(FractionLayoutViewStylesEnum.SESSION) ? sessionModelWidth : dataModelWidth, //
                        countOfDataModels * (dataModelHeight + HEIGHT_OF_OVERLAY_XAXIS_PANES + residualsHeight + verticalGraphSeparation) + topMargin));

                tripoliFractionInfoViewsContainer = new JLayeredPane(); //
                tripoliFractionInfoViewsContainer.setBounds(new Rectangle( //
                        //
                        (int) mapTimeStamp,//
                        0, //
                        FRACTION_LAYOUT_VIEW_STYLE.equals(FractionLayoutViewStylesEnum.SESSION) ? sessionModelWidth : dataModelWidth, //
                        tripoliSessionDataHeader_pane.getHeight()));

                if (!FRACTION_LAYOUT_VIEW_STYLE.equals(FractionLayoutViewStylesEnum.SESSION)) {
                    // fraction info panel at top of fraction panel
                    AbstractRawDataView fractionInfoPanel = new FractionInfoPanel( //
                            this, //
                            fractionMap[f], rawDataModelViews[0][f], //[0,f] is top view and it sets view details for column
                            new Rectangle(0, 0, dataModelWidth, HEIGHT_OF_FRACTION_INFO_PANELS), //
                            zoomSlidersIndependent);

                    tripoliFractionInfoViewsContainer.add(fractionInfoPanel, javax.swing.JLayeredPane.DEFAULT_LAYER);
                }

                // local y axis panes
                if (localYAxisViewsArray != null) {
                    layeredYAxisDataViewsContainer = new JLayeredPane(); //
                    layeredYAxisDataViewsContainer.setBounds(
                            new Rectangle( //
                                    (int) (mapTimeStamp - WIDTH_OF_LOCAL_YAXIS_PANES),//
                                    0, //
                                    WIDTH_OF_LOCAL_YAXIS_PANES, //
                                    countOfDataModels * (dataModelHeight + HEIGHT_OF_OVERLAY_XAXIS_PANES + residualsHeight + verticalGraphSeparation) + topMargin));
                    //set color key from this
                    layeredYAxisDataViewsContainer.setBackground(getBackground());
                }

                // local intercept fitfunction panels
                if (interceptFitFunctionsPresentationPanesArray != null) {
                    layeredLocalInterceptFitFunctionsContainer = new JLayeredPane(); //
                    layeredLocalInterceptFitFunctionsContainer.setBounds(new Rectangle( //
                            (int) (mapTimeStamp + (int) (FRACTION_LAYOUT_VIEW_STYLE.equals(FractionLayoutViewStylesEnum.SESSION) ? sessionModelWidth : dataModelWidth)),//
                            0, //
                            WIDTH_OF_FIT_FUNCTION_PANES, //
                            countOfDataModels * (dataModelHeight + HEIGHT_OF_OVERLAY_XAXIS_PANES + residualsHeight + verticalGraphSeparation) + topMargin));
                    //set color key from this
                    layeredLocalInterceptFitFunctionsContainer.setBackground(getBackground());
                }
            }

            // sept 2011 no longer add individuals to underlay because rendering is too expensive 
            // except for the case of raw intensities for now
            if (FRACTION_LAYOUT_VIEW_STYLE.equals(FractionLayoutViewStylesEnum.OVERLAY)) {
                if (rawDataModelViews[0][0] instanceof RawIntensitiesDataView) {
                    for (int i = 0; i < countOfDataModels; i++) {
                        fractionDataModelViewsArray[i].setOpaque(false);
                        fractionDataModelViewsArray[i].setLocation(0, 0);

                        underlays[i].add(fractionDataModelViewsArray[i], javax.swing.JLayeredPane.DEFAULT_LAYER);
                    }
                }
            } else {
                for (int i = 0; i < countOfDataModels; i++) {
                    fractionDataModelViewsArray[i].setOpaque(true);
                    tripoliFractionViewsContainer.add(fractionDataModelViewsArray[i], javax.swing.JLayeredPane.DEFAULT_LAYER);
                    fractionDataModelViewsArray[i].setFractionDataViewsContainer(tripoliFractionViewsContainer);

                    if (layeredYAxisDataViewsContainer != null) {
                        layeredYAxisDataViewsContainer.add(localYAxisViewsArray[i], javax.swing.JLayeredPane.DEFAULT_LAYER);
                        localYAxisViewsArray[i].setFractionDataViewsContainer(layeredYAxisDataViewsContainer);
                    }

                    if ((layeredLocalInterceptFitFunctionsContainer != null)//
                            &&//
                            FRACTION_LAYOUT_VIEW_STYLE.equals(FractionLayoutViewStylesEnum.GRID_INTERCEPT)) {
                        interceptFitFunctionsPresentationPanesArray[i].preparePanel(doReScale);
                        layeredLocalInterceptFitFunctionsContainer.add(interceptFitFunctionsPresentationPanesArray[i], javax.swing.JLayeredPane.DEFAULT_LAYER);
                        interceptFitFunctionsPresentationPanesArray[i].setFractionDataViewsContainer(layeredLocalInterceptFitFunctionsContainer);
                    }

                }

                // add vertical views containers
                add(tripoliFractionViewsContainer);

                tripoliSessionDataHeader_pane.add(tripoliFractionInfoViewsContainer, javax.swing.JLayeredPane.DEFAULT_LAYER);

                if (layeredYAxisDataViewsContainer != null) {
                    add(layeredYAxisDataViewsContainer);
                }
                if (layeredLocalInterceptFitFunctionsContainer != null) {
                    add(layeredLocalInterceptFitFunctionsContainer);
                }
            }

        }

        // prepare for use in scrollpane
        setPreferredSize( //
                new Dimension(leftMargin + rightMargin + (int) graphWidth, 30 + topMargin + countOfDataModels * (dataModelHeight + HEIGHT_OF_OVERLAY_XAXIS_PANES + residualsHeight)));

        validate();

    }

    private void makeDataPresentationModeChooserPanel(double[] timeArray, boolean meanOnly) {
        dataPresentationModeChooserPanel
                = //
                new DataPresentationModeChooserPanel(//
                        this,//
                        dataPresentationMode,//
                        timeArray,//
                        new Rectangle( //
                                0, //
                                0, //
                                tripoliSessionRawDataViewYAxis.getWidth(), //
                                HEIGHT_OF_FRACTION_INFO_PANELS), //
                        meanOnly);

        dataPresentationModeChooserPanel.preparePanel(true);

        tripoliSessionDataControls_pane.add(dataPresentationModeChooserPanel, javax.swing.JLayeredPane.DRAG_LAYER);

    }

    private AbstractRawDataView rawDataViewFactory(//
            JLayeredPane sampleSessionDataView, //
            TripoliFraction tf, //
            DataModelInterface dm, //
            int dataModelCount, //
            boolean invokeMouseListener) {

        AbstractRawDataView dataView = null;

        try {
            Object arglist[] = new Object[5];
            arglist[0] = sampleSessionDataView;
            arglist[1] = tf;
            arglist[2] = dm;
            arglist[3] = new Rectangle(//
                    0, dataModelCount * (dataModelHeight + verticalGraphSeparation) + topMargin, dataModelWidth, dataModelHeight);
            arglist[4] = invokeMouseListener;
            dataView = (AbstractRawDataView) dataModelViewConstructor.newInstance(arglist);
        } catch (SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException securityException) {
        }

        return dataView;
    }

    private SortedSet<DataModelInterface> rawDataSourceFactory(TripoliFraction tf) {

        SortedSet<DataModelInterface> rawDataSource = null;

        try {
            rawDataSource = (SortedSet<DataModelInterface>) rawDataSourceMethod.invoke(tf, new Object[]{});//   (Object[]) null );
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException illegalAccessException) {
        }

        return rawDataSource;

    }

    /**
     *
     */
    public void includeAllFractions() {
        tripoliSession.includeAllFractions(fractionSelectionType);
//////        if (FRACTION_LAYOUT_VIEW_STYLE.equals(FractionLayoutViewStylesEnum.SESSION)) {
//////            sessionFractionationCalculator.calculateSessionFitFunctionsForPrimaryStandard();
//////            updateReportTable();
//////        }
//////
//////        refreshPanel();
    }

    /**
     *
     */
    public void includeAllAquisitions() {
        tripoliSession.includeAllAquisitions();

        refreshPanel(true);
    }

    /**
     *
     */
    public void removeAllLocalYAxisPanes() {
        getTripoliSession().clearAllFractionsOfLocalYAxis();

        refreshPanel(true);
    }

    public void setAllLocalYAxisPanes() {
        getTripoliSession().setAllFractionsOfLocalYAxis();

        refreshPanel(true);
    }

    /**
     *
     * @param backgroundColor
     * @param dataModelViewConstructor
     * @param rawDataSourceMethod
     */
    public void showSessionViewOfRawDataModels(//
            Color backgroundColor, //
            Constructor dataModelViewConstructor,//
            Method rawDataSourceMethod) {

        setFRACTION_LAYOUT_VIEW_STYLE(FractionLayoutViewStylesEnum.SESSION);

        setBackground(backgroundColor);
        setDataModelViewConstructor(dataModelViewConstructor);

        setRawDataSourceMethod(rawDataSourceMethod);

        dataModelHeight = 180;

        refreshPanel(true);

        //cause slider to synch
        synchXAxisZoomSliderValue(sessionModelWidth);
//        xAxisZoomSlider.setValue( sessionModelWidth);//          SessionOfStandardView.DEFAULT_WIDTH_OF_PANE );
    }

    /**
     * @param FRACTION_LAYOUT_VIEW_STYLE the FRACTION_LAYOUT_VIEW_STYLE to set
     */
    public void setFRACTION_LAYOUT_VIEW_STYLE(FractionLayoutViewStylesEnum FRACTION_LAYOUT_VIEW_STYLE) {
        this.FRACTION_LAYOUT_VIEW_STYLE = FRACTION_LAYOUT_VIEW_STYLE;
    }

    /**
     *
     */
    public void zoomOut() {
        if (dataModelWidth > 8) {
            dataModelWidth /= 2;
        }
        if (dataModelHeight > 8) {
            dataModelHeight /= 2;
        }
    }

    /**
     *
     */
    public void zoomIn() {
        if (dataModelWidth < 512) {
            dataModelWidth *= 2;
        }
        if (dataModelHeight < 512) {
            dataModelHeight *= 2;
        }
    }

    /**
     * @param dataModelViewConstructor the dataModelViewConstructor to set
     */
    public void setDataModelViewConstructor(Constructor dataModelViewConstructor) {
        this.dataModelViewConstructor = dataModelViewConstructor;
    }

    /**
     * @param rawDataSourceMethod the rawDataSourceMethod to set
     */
    public void setRawDataSourceMethod(Method rawDataSourceMethod) {
        this.rawDataSourceMethod = rawDataSourceMethod;
    }

    /**
     * @param countOfDataModels the countOfDataModels to set
     */
    public void setCountOfDataModels(int countOfDataModels) {
        this.countOfDataModels = countOfDataModels;
    }

    public void synchXAxisZoomSliderValue(int value) {
        // synchs slider to selected views value (session vs data)
        if (value != xAxisZoomSlider.getValue()) {
            ChangeListener[] changeListeners = xAxisZoomSlider.getChangeListeners();
            xAxisZoomSlider.removeChangeListener(changeListeners[0]);
            xAxisZoomSlider.setValue(value);
            xAxisZoomSlider.addChangeListener(changeListeners[0]);
        }
    }

    /**
     * @return the SAVED_YAXIS_IS_UNIFORM
     */
    public boolean isSAVED_YAXIS_IS_UNIFORM() {
        return SAVED_YAXIS_IS_UNIFORM;
    }

    /**
     * @param aSAVED_YAXIS_IS_UNIFORM the SAVED_YAXIS_IS_UNIFORM to set
     */
    public void setSAVED_YAXIS_IS_UNIFORM(boolean aSAVED_YAXIS_IS_UNIFORM) {
        SAVED_YAXIS_IS_UNIFORM = aSAVED_YAXIS_IS_UNIFORM;
    }

    /**
     * @return the SAVED_DATA_USED_FOR_SCALING
     */
    public IncludedTypeEnum getSAVED_DATA_USED_FOR_SCALING() {
        return SAVED_DATA_USED_FOR_SCALING;
    }

    /**
     * @param aSAVED_DATA_USED_FOR_SCALING the SAVED_DATA_USED_FOR_SCALING to
     * set
     */
    public void setSAVED_DATA_USED_FOR_SCALING(IncludedTypeEnum aSAVED_DATA_USED_FOR_SCALING) {
        SAVED_DATA_USED_FOR_SCALING = aSAVED_DATA_USED_FOR_SCALING;
    }

}
