/*
 * AbstractRawDataView.java
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
package org.earthtime.Tripoli.dataViews;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.math.BigDecimal;
import java.util.ArrayList;
import javax.swing.JLayeredPane;
import javax.swing.event.MouseInputListener;
import org.earthtime.ETReduxFrame;
import org.earthtime.Tripoli.dataModels.DataModelInterface;
import org.earthtime.Tripoli.dataModels.sessionModels.AbstractSessionForStandardDataModel;
import org.earthtime.Tripoli.dataViews.dataMonitorViews.AbstractDataMonitorView;
import org.earthtime.Tripoli.dataViews.fitFunctionPresentationViews.AbstractFitFunctionPresentationView;
import org.earthtime.Tripoli.dataViews.simpleViews.FitFunctionDataInterface;
import org.earthtime.Tripoli.dataViews.simpleViews.SessionOfStandardView;
import org.earthtime.Tripoli.fractions.TripoliFraction;
import org.earthtime.Tripoli.fractions.TripoliFractionIncludeChangeInterface;
import org.earthtime.Tripoli.sessions.TripoliSessionFractionationCalculatorInterface;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFractionI;
import org.earthtime.dataDictionaries.DataPresentationModeEnum;
import org.earthtime.dataDictionaries.IncludedTypeEnum;

/**
 *
 * @author James F. Bowring
 */
public abstract class AbstractRawDataView extends JLayeredPane implements MouseInputListener, TripoliFractionIncludeChangeInterface {

    /**
     *
     */
    protected ETReduxFrame uPbReduxFrame;
    /**
     *
     */
    protected TripoliFraction tripoliFraction;
    /**
     *
     */
    protected JLayeredPane sampleSessionDataView;
    /**
     *
     */
    protected JLayeredPane fractionDataViewsContainer;
    /**
     *
     */
    protected double[] myOnPeakData;
    /**
     *
     */
    protected double[] myOnPeakNormalizedAquireTimes;
    /**
     *
     */
    protected int graphWidth;
    /**
     *
     */
    protected int graphHeight;
    /**
     *
     */
    protected int topMargin = 0;
    /**
     *
     */
    protected int leftMargin = 0;
    /**
     *
     */
    protected double minX;
    /**
     *
     */
    protected double maxX;
    /**
     *
     */
    protected double minY;
    /**
     *
     */
    protected double maxY;
    /**
     *
     */
    protected double displayOffsetY = 0;
    /**
     *
     */
    protected double displayOffsetX = 0;
    /**
     *
     */
    protected Color EXCLUDED_COLOR = Color.RED;
    /**
     *
     */
    protected double shiftAquiredTimeIndex = 0;
    /**
     *
     */
    protected BigDecimal[] tics;
    /**
     *
     */
    protected IncludedTypeEnum showIncludedDataPoints;
    private Color paintColor;
    private transient boolean itIsI = false;

    /**
     *
     */
    protected boolean notShownDueToBelowDetectionFlag;

    /**
     *
     */
    protected DataPresentationModeEnum dataPresentationMode;

    /**
     *
     */
    protected double standardValue;

    /**
     *
     */
    protected boolean forStandards = true;

    /**
     *
     */
    protected DataModelInterface rawRatioDataModel;
    // added feb 2013 to allow quick vertical (fraction) refreshes

    /**
     *
     */
    protected AbstractRawDataView[] fractionRawDataViews;

    /**
     *
     */
    public AbstractRawDataView() {
        super();
    }

    /**
     *
     * @param bounds
     */
    protected AbstractRawDataView(Rectangle bounds) {
        super();

        this.myOnPeakData = null;

        setBounds(bounds);
        graphWidth = bounds.width - leftMargin;
        graphHeight = bounds.height - topMargin;

        setOpaque(true);
        setBackground(Color.white);

        this.tics = null;

        this.showIncludedDataPoints = IncludedTypeEnum.ALL;

        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

        if (tripoliFraction != null) {
            if (!tripoliFraction.isStandard()) {
                setBackground(ReduxConstants.LightBlueForUnknowns);
            }
        }

        this.paintColor = Color.black;
        this.notShownDueToBelowDetectionFlag = false;

        // set default presentation mode
        this.dataPresentationMode = DataPresentationModeEnum.RATIO;

        this.fractionRawDataViews = new AbstractRawDataView[0];

    }

    /**
     *
     *
     * @param sampleSessionDataView
     * @param tripoliFraction
     * @param bounds
     * @param invokeMouseListener
     * @param forStandards the value of forStandards
     */
    public AbstractRawDataView(//
            JLayeredPane sampleSessionDataView, //
            TripoliFraction tripoliFraction, //
            Rectangle bounds, //
            boolean invokeMouseListener,//
            boolean forStandards) {
        this(bounds);

        this.sampleSessionDataView = sampleSessionDataView;
        this.tripoliFraction = tripoliFraction;
        this.forStandards = forStandards;

        if (tripoliFraction != null) {
            if (!tripoliFraction.isStandard()) {
                setBackground(ReduxConstants.LightBlueForUnknowns);
            }
        }

        if (invokeMouseListener) {
            addMeAsMouseListener();
        }

    }

    /**
     *
     */
    protected final void addMeAsMouseListener() {
        addMouseListener(this);
        addMouseMotionListener(this);
    }

    /**
     *
     * @param g
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        paint((Graphics2D) g);
    }

    /**
     *
     * @param g2d
     */
    protected void paintInit(Graphics2D g2d) {
        RenderingHints rh = g2d.getRenderingHints();
        rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHints(rh);

        g2d.setPaint(Color.BLACK);
        g2d.setStroke(new BasicStroke(1.0f));
        g2d.setFont(new Font(
                "SansSerif",
                Font.BOLD,
                10));

    }

    /**
     *
     * @param g2d
     */
    public void paint(Graphics2D g2d) {
        paintInit(g2d);

        drawTicsYAxisInBackground(g2d);

        if ((tripoliFraction != null) && (myOnPeakNormalizedAquireTimes != null)) {
            if (tripoliFraction.isColorMeExcluded()) {
                paintFractionExcludedColor(g2d);
            }

            int chosenDatumIndex = tripoliFraction.getShowVerticalLineAtThisIndex();
            if (chosenDatumIndex > -1) {
                int secondChoiceIndex = tripoliFraction.getShowSecondVerticalLineAtThisIndex();
                highlightSelectedData(g2d, chosenDatumIndex, secondChoiceIndex);
            }
        }
    }

    /**
     *
     * @param logRatioDatum
     * @return the double
     */
    protected double convertLogDatumToPresentationMode(double logRatioDatum) {
        double retVal = logRatioDatum;

        if (dataPresentationMode.compareTo(DataPresentationModeEnum.ALPHA) == 0) {
            retVal = (standardValue / Math.exp(logRatioDatum)) - 1.0;
        }

        if (dataPresentationMode.compareTo(DataPresentationModeEnum.RATIO) == 0) {
            retVal = Math.exp(logRatioDatum);
        }

        // else we just need log
        return retVal;
    }

    /**
     * Special case for ratios with Pb204 in denominator
     *
     * @param logRatioDatum
     * @param ratioDatum
     * @return
     */
    protected double convertLogDatumToPresentationMode(double logRatioDatum, double ratioDatum) {
        double retVal = logRatioDatum;

        if (dataPresentationMode.compareTo(DataPresentationModeEnum.ALPHA) == 0) {
            retVal = (standardValue / ratioDatum) - 1.0;
        }

        if (dataPresentationMode.compareTo(DataPresentationModeEnum.RATIO) == 0) {
            retVal = ratioDatum;
        }

        // else we just need log
        return retVal;
    }

//    /**
//     *
//     * @param datum
//     * @return
//     */
//    protected double unConvertPresentationModeToLogDatum(double datum) {
//        double retVal = datum;
//
//        if (dataPresentationMode.compareTo(DataPresentationModeEnum.ALPHA) == 0) {
//            retVal = Math.log(standardValue / (1.0 + datum));
//        }
//
//        if (dataPresentationMode.compareTo(DataPresentationModeEnum.RATIO) == 0) {
//            retVal = Math.log(datum);
//        }
//
//        return retVal;
//    }
    /**
     *
     * @param x
     * @return
     */
    public double mapX(double x) {

        return (((x - getMinX_Display()) / getRangeX_Display()) * graphWidth) + leftMargin;
    }

    /**
     *
     * @param y
     * @return
     */
    protected double mapY(double y) {

        return (((getMaxY_Display() - y) / getRangeY_Display()) * graphHeight) + topMargin;
    }

    /**
     *
     * @param targetDataModelView
     */
    public void updatePlotsWithChanges(FitFunctionDataInterface targetDataModelView) {
        if (targetDataModelView instanceof SessionOfStandardView) {
            targetDataModelView.updateFittedData();
            // repaint fittedfunction
            sampleSessionDataView.repaint();
            updateReportTable();
            //       } else if ( targetDataModelView instanceof FitFunctionsOnRatioDataView ) {
//            if ( targetDataModelView.amShowingUnknownFraction() ) {
            //               targetDataModelView.repaint();
//                updateReportTable();
            //           }
        } else {
            double saveMinY = ((AbstractRawDataView) targetDataModelView).getMinY();
            double saveMaxY = ((AbstractRawDataView) targetDataModelView).getMaxY();
            double saveMinX = ((AbstractRawDataView) targetDataModelView).getMinX();
            double saveMaxX = ((AbstractRawDataView) targetDataModelView).getMaxX();

            targetDataModelView.updateFittedData();

            ((AbstractRawDataView) targetDataModelView).setMinY(saveMinY);
            ((AbstractRawDataView) targetDataModelView).setMaxY(saveMaxY);
            ((AbstractRawDataView) targetDataModelView).setMinX(saveMinX);
            ((AbstractRawDataView) targetDataModelView).setMaxX(saveMaxX);

            // repaint fittedfunction
            sampleSessionDataView.repaint();

        }

    }

    /**
     *
     * @param index
     * @param defaultColor
     * @return
     */
    protected Color determineDataColor(int index, Color defaultColor) {
        //return (tripoliFraction.getDataActiveMap()[index] ? defaultColor : EXCLUDED_COLOR);
        // nov 2014 switched to ratio for different models
        return (rawRatioDataModel.getDataActiveMap()[index] ? defaultColor : EXCLUDED_COLOR);
    }

//    private void paintBullsEyeOnSelectedPoints () {
//    }
    /**
     *
     * @param g2d
     */
    protected void paintFractionExcludedColor(Graphics2D g2d) {
        Paint savePaint = g2d.getPaint();
        Path2D excludedFractionLine = new Path2D.Double();
        excludedFractionLine.moveTo(mapX(shiftAquiredTimeIndex + myOnPeakNormalizedAquireTimes[0]), mapY(myOnPeakData[0]));
        for (int i = 0; i < myOnPeakData.length; i++) {
            excludedFractionLine.lineTo(mapX(shiftAquiredTimeIndex + myOnPeakNormalizedAquireTimes[i]), mapY(myOnPeakData[i]));
        }
        g2d.setPaint(EXCLUDED_COLOR);
        g2d.draw(excludedFractionLine);
        g2d.setPaint(savePaint);
    }

    /**
     *
     * @param g2d
     * @param chosenDatumIndex
     */
    protected void paintLineOverSelectedDatum(Graphics2D g2d, int chosenDatumIndex) {
        Shape redDatumLine = new Line2D.Double(//
                mapX(shiftAquiredTimeIndex + myOnPeakNormalizedAquireTimes[chosenDatumIndex]), //
                mapY(minY),//
                mapX(shiftAquiredTimeIndex + myOnPeakNormalizedAquireTimes[chosenDatumIndex]),//
                mapY(maxY));

        Paint savedPaint = g2d.getPaint();
        Stroke savedStroke = g2d.getStroke();
        g2d.setPaint(EXCLUDED_COLOR);
        g2d.setStroke(new BasicStroke(0.5f));
        g2d.draw(redDatumLine);
        g2d.setPaint(savedPaint);
        g2d.setStroke(savedStroke);
    }

    /**
     *
     * @param g2d
     * @param chosenDatumIndex
     * @param secondChoiceIndex
     */
    protected void highlightSelectedData(Graphics2D g2d, int chosenDatumIndex, int secondChoiceIndex) {

        if (itIsI) {
            // paint transparent rectangle over selected data points
            int leftSide = Math.min(chosenDatumIndex, secondChoiceIndex);
            double halfDistance = //
                    (Math.abs(mapX(myOnPeakNormalizedAquireTimes[2])//
                            - mapX(myOnPeakNormalizedAquireTimes[1])) / 2.0);

            double topSide = calcTopSelectionBox();
            double botSide = calcBottomSelectionBox();

            Shape redDatumBox = new Rectangle2D.Double(//
                    mapX(shiftAquiredTimeIndex + myOnPeakNormalizedAquireTimes[leftSide]) - halfDistance, //
                    mapY(topSide),//maxY ),//
                    Math.abs(chosenDatumIndex - secondChoiceIndex) * halfDistance * 2.0,//
                    mapY(botSide) - mapY(topSide) - 1);

            Paint savedPaint = g2d.getPaint();
            Stroke savedStroke = g2d.getStroke();
            g2d.setPaint(new Color(255, 99, 71));//   );
            g2d.setStroke(new BasicStroke(0.5f));
            g2d.draw(redDatumBox);

            Composite originalComposite = g2d.getComposite();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.05f));
            g2d.fill(redDatumBox);
            //restore composite
            g2d.setComposite(originalComposite);

            g2d.setPaint(savedPaint);
            g2d.setStroke(savedStroke);
        } else {
            // need bullseyes on individual points in other views
            ArrayList<Integer> includedIndexes = //
                    tripoliFraction.getSelectedForToggleIndexes();
            try {
                for (int i = 0; i < includedIndexes.size(); i++) {
                    Shape rawRatioPoint = new java.awt.geom.Ellipse2D.Double( //
                            mapX(shiftAquiredTimeIndex + myOnPeakNormalizedAquireTimes[includedIndexes.get(i)]) - 2.0, //
                            mapY(myOnPeakData[includedIndexes.get(i)]) - 2.0,//
                            4.0, 4.0);
                    g2d.setPaint(EXCLUDED_COLOR);
                    g2d.draw(rawRatioPoint);
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     *
     * @param g2d
     */
    protected void drawTicsYAxisInBackground(Graphics2D g2d) {
        // y -axis tics
        Stroke savedStroke = g2d.getStroke();

        // tics
        if (tics != null) {
            for (int i = 0; i < tics.length; i++) {
                try {
                    Shape ticMark = new Line2D.Double( //
                            mapX(minX), mapY(tics[i].doubleValue()), mapX(maxX), mapY(tics[i].doubleValue()));

                    g2d.setPaint(new Color(202, 202, 202));//pale gray
                    g2d.setStroke(new BasicStroke(0.5f));
                    g2d.draw(ticMark);
                } catch (Exception e) {
                }
            }
        } else {
            double ticWidth = (maxY - minY) / 10;
            if (ticWidth > 0.0) {
                for (double tic = minY + ticWidth; tic < (maxY * 0.999); tic += ticWidth) {
                    Shape ticMark = new Line2D.Double( //
                            mapX(minX), mapY(tic), mapX(maxX), mapY(tic));
                    g2d.setPaint(new Color(202, 202, 202));//pale gray
                    g2d.setStroke(new BasicStroke(0.5f));
                    g2d.draw(ticMark);
                }
            }
        }

        g2d.setStroke(savedStroke);
    }

    /**
     *
     */
    public void refreshPanel() {
        preparePanel();
        validate();
        repaint();
    }

    /**
     *
     */
    public abstract void preparePanel();

    /**
     * @return the graphWidth
     */
    public int getGraphWidth() {
        return graphWidth;
    }

    /**
     * @param graphWidth the graphWidth to set
     */
    public void setGraphWidth(int graphWidth) {
        this.graphWidth = graphWidth;
    }

    /**
     * @return the graphHeight
     */
    public int getGraphHeight() {
        return graphHeight;
    }

    /**
     * @param graphHeight the graphHeight to set
     */
    public void setGraphHeight(int graphHeight) {
        this.graphHeight = graphHeight;
    }

    /**
     * @return the topMargin
     */
    public int getTopMargin() {
        return topMargin;
    }

    /**
     * @param topMargin the topMargin to set
     */
    public void setTopMargin(int topMargin) {
        this.topMargin = topMargin;
    }

    /**
     * @return the leftMargin
     */
    public int getLeftMargin() {
        return leftMargin;
    }

    /**
     * @param leftMargin the leftMargin to set
     */
    public void setLeftMargin(int leftMargin) {
        this.leftMargin = leftMargin;
    }

    /**
     * @return the displayOffsetY
     */
    public double getDisplayOffsetY() {
        return displayOffsetY;
    }

    /**
     * @param displayOffsetY the displayOffsetY to set
     */
    public void setDisplayOffsetY(double displayOffsetY) {
        this.displayOffsetY = displayOffsetY;
    }

    /**
     * @return the displayOffsetX
     */
    public double getDisplayOffsetX() {
        return displayOffsetX;
    }

    /**
     * @param displayOffsetX the displayOffsetX to set
     */
    public void setDisplayOffsetX(double displayOffsetX) {
        this.displayOffsetX = displayOffsetX;
    }

    /**
     *
     * @return
     */
    public double getMinX_Display() {
        return getMinX() + getDisplayOffsetX();
    }

    /**
     *
     * @param minX
     */
    public void setMinX(double minX) {
        this.minX = minX;
    }

    /**
     *
     * @return
     */
    public double getMaxX_Display() {
        return getMaxX() + getDisplayOffsetX();
    }

    /**
     *
     * @param maxX
     */
    public void setMaxX(double maxX) {
        this.maxX = maxX;
    }

    /**
     *
     * @return
     */
    public double getMinY_Display() {
        return getMinY() + getDisplayOffsetY();
    }

    /**
     *
     * @param minY
     */
    public void setMinY(double minY) {
        this.minY = minY;
    }

    /**
     *
     * @return
     */
    public double getMaxY_Display() {
        return getMaxY() + getDisplayOffsetY();
    }

    /**
     *
     * @param maxY
     */
    public void setMaxY(double maxY) {
        this.maxY = maxY;
    }

    /**
     *
     * @return
     */
    public double getRangeX_Display() {
        return (getMaxX_Display() - getMinX_Display());
    }

    /**
     *
     * @return
     */
    public double getRangeY_Display() {
        return (getMaxY_Display() - getMinY_Display());
    }

    /**
     * @return the minX
     */
    public double getMinX() {
        return minX;
    }

    /**
     * @return the maxX
     */
    public double getMaxX() {
        return maxX;
    }

    /**
     * @return the minY
     */
    public double getMinY() {
        return minY;
    }

    /**
     * @return the maxY
     */
    public double getMaxY() {
        return maxY;
    }

    /**
     *
     * @return
     */
    public abstract DataModelInterface getDataModel();

    /**
     * @return the tripoliFraction
     */
    public TripoliFraction getTripoliFraction() {
        return tripoliFraction;
    }

    /**
     * @return the myOnPeakData
     */
    public double[] getMyOnPeakData() {
        return myOnPeakData;
    }

    /**
     * @return the myOnPeakNormalizedAquireTimes
     */
    public double[] getMyOnPeakNormalizedAquireTimes() {
        return myOnPeakNormalizedAquireTimes;
    }

    /**
     *
     * @param x
     * @return
     */
    protected int convertMouseXToValue(int x) {
        return //
                (int) Math.round(
                        (((double) (x - getLeftMargin())) / (double) getGraphWidth()) //
                        * getRangeX_Display()//
                        + getMinX_Display());
    }

    /**
     *
     * @param y
     * @return
     */
    protected double convertMouseYToValue(double y) {
        return //
                -1 * (((y - topMargin - 1) * getRangeY_Display() / graphHeight) //
                - getMaxY_Display());
    }

    /**
     *
     */
    protected void repaintFraction() {
        if (fractionDataViewsContainer != null) {
            fractionDataViewsContainer.repaint();
        }
    }

    /**
     *
     * @param e
     */
    @Override
    public void mouseClicked(MouseEvent e) {
//        throw new UnsupportedOperationException( "Not supported yet." );
    }

    /**
     *
     * @param evt
     */
    @Override
    public void mousePressed(MouseEvent evt) {

        if (myOnPeakData != null) {
            itIsI = true;

            int timeSlot = convertMouseXToValue(evt.getX()) - (int) shiftAquiredTimeIndex;

            if (timeSlot >= 0) {
                // special case of summary data
                if (myOnPeakData.length == 1) {
                    timeSlot = 0;
                }
                // over the right end
                if (timeSlot >= myOnPeakNormalizedAquireTimes.length) {
                    timeSlot = myOnPeakNormalizedAquireTimes.length - 1;
                }

                final int finalTimeSlot = timeSlot;

                tripoliFraction.setShowVerticalLineAtThisIndex(finalTimeSlot);
                tripoliFraction.setShowSecondVerticalLineAtThisIndex(finalTimeSlot);
                // capture the relative position of y as deltaY over range
                tripoliFraction.//
                        setSelBoxFirstY( //
                                (convertMouseYToValue(evt.getY()) - getMinY_Display()) / getRangeY_Display());
                tripoliFraction.//
                        setSelBoxSecondY( //
                                (convertMouseYToValue(evt.getY()) - getMinY_Display()) / getRangeY_Display());

            }
        }
    }

    /**
     *
     * @param e
     */
    @Override
    public void mouseReleased(MouseEvent e) {

        itIsI = false;

        int selectedIndex = tripoliFraction.getShowVerticalLineAtThisIndex();
        if (selectedIndex >= 0) {

            // determine which points are included in drawn box
            ArrayList<Integer> includedIndexes = tripoliFraction.getSelectedForToggleIndexes();

            if ((includedIndexes != null) && (includedIndexes.size() > 0)) {
                // first get majority count to rule the reversal
                int included = 0;
                for (int i = 0; i < includedIndexes.size(); i++) {
                    included += tripoliFraction.getDataActiveMap()[includedIndexes.get(i)] ? 1 : 0;
                }
                boolean moreAreIncluded = (included >= includedIndexes.size() / 2.0);

                for (int i = 0; i < includedIndexes.size(); i++) {
                    // jan 2015 want to make Pbc ratios able to exclude additional points
                    if (rawRatioDataModel.isUsedForCommonLeadCorrections()) {
                        tripoliFraction.toggleOneDataAquisition(includedIndexes.get(i), !moreAreIncluded);
////                        tripoliFraction.toggleOneDataAquisitionForPbcOnly(includedIndexes.get(i), !moreAreIncluded);
                    } else {
                        tripoliFraction.toggleOneDataAquisition(includedIndexes.get(i), !moreAreIncluded);
                    }
//                    tripoliFraction.updateIncludedStatus();
                    tripoliFraction.setShowVerticalLineAtThisIndex(-1);
                }

                tripoliFraction.updateIncludedStatus();

                //???? Check for is active?
//                // recalculate based on context     
//                if (tripoliFraction.isStandard()) {
//                    tripoliFraction.updateInterceptFitFunctionsIncludingCommonLead();//updateInterceptFitFunctions();
//                } else {
                tripoliFraction.updateInterceptFitFunctionsIncludingCommonLead();
                updateReportTable();
//                }

                // feb 2013 here we differentiate between session and ratios
                // for ratios,we want data point toggle to only affect fraction and not disturb layout
                if (rawRatioDataModel instanceof AbstractSessionForStandardDataModel) {
                    ((AbstractRawDataView) sampleSessionDataView).refreshPanel();
                } else {
                    for (AbstractRawDataView fractionRawDataView : fractionRawDataViews) {
                        if (fractionRawDataView instanceof AbstractFitFunctionPresentationView) {
                            fractionRawDataView.refreshPanel();
                        } else if (fractionRawDataView != null) {
//                            try {
                            // a bad fraction might not plot in ratio space, but plot in log space, so we have to handle missing views
                            fractionRawDataView.updatePlotsWithChanges((FitFunctionDataInterface) fractionRawDataView);
//                            } catch (Exception emissingview) {
//                            }
                        }
                    }
                }

            }

        }
        tripoliFraction.setShowVerticalLineAtThisIndex(-1);
        tripoliFraction.setShowSecondVerticalLineAtThisIndex(-1);

        tripoliFraction.setSelectedForToggleIndexes(new ArrayList<>());

        repaintFraction();
    }

    private double calcTopSelectionBox() {
        return Math.max( //
                tripoliFraction.getSelBoxFirstY(),//
                tripoliFraction.getSelBoxSecondY())//
                * getRangeY_Display() + getMinY_Display();
    }

    private double calcBottomSelectionBox() {
        return Math.min( //
                tripoliFraction.getSelBoxFirstY(),//
                tripoliFraction.getSelBoxSecondY())//
                * getRangeY_Display() + getMinY_Display();
    }

    private ArrayList<Integer> calcListOfSelectedToToggleIndexes(int selectedIndex, int secondIndex) {
        // determine which points are included in drawn box
        ArrayList<Integer> includedIndexes = new ArrayList<>();
        double top = calcTopSelectionBox();
        double bot = calcBottomSelectionBox();

        for (int i = Math.min(selectedIndex, secondIndex); i < Math.max(selectedIndex, secondIndex); i++) {
            try {
                if ((myOnPeakData[i] < top) && (myOnPeakData[i] > bot)) {
                    includedIndexes.add(i);
                }
            } catch (Exception e) {
            }
        }

        return includedIndexes;
    }

    /**
     *
     * @param e
     */
    @Override
    public void mouseEntered(MouseEvent e) {
//        throw new UnsupportedOperationException( "Not supported yet." );
    }

    /**
     *
     * @param e
     */
    @Override
    public void mouseExited(MouseEvent e) {
//        throw new UnsupportedOperationException( "Not supported yet." );
    }

    /**
     *
     * @param evt
     */
    @Override
    public void mouseDragged(MouseEvent evt) {

        if (tripoliFraction.getShowVerticalLineAtThisIndex() >= 0) {
            int timeSlot = convertMouseXToValue(evt.getX()) - (int) shiftAquiredTimeIndex;

            if (timeSlot >= 0) {
                // over the right end by one
                try {
                    if (timeSlot > myOnPeakNormalizedAquireTimes.length) {
                        timeSlot = myOnPeakNormalizedAquireTimes.length;
                    }
                } catch (Exception e) {
                }
                final int finalTimeSlot = timeSlot;

                tripoliFraction.setShowSecondVerticalLineAtThisIndex(finalTimeSlot);
                // capture the relative position of y as deltaY over range
                tripoliFraction.//
                        setSelBoxSecondY( //
                                (convertMouseYToValue(evt.getY()) - getMinY_Display()) / getRangeY_Display());
                tripoliFraction.setSelectedForToggleIndexes( //
                        calcListOfSelectedToToggleIndexes( //
                                tripoliFraction.getShowVerticalLineAtThisIndex(), //
                                tripoliFraction.getShowSecondVerticalLineAtThisIndex()));
                // repaint fraction

                repaintFraction();
            }
        }
    }

    /**
     *
     * @param e
     */
    @Override
    public void mouseMoved(MouseEvent e) {
    }

    /**
     * @param fractionDataViewsContainer the fractionDataViewsContainer to set
     */
    public void setFractionDataViewsContainer(JLayeredPane fractionDataViewsContainer) {
        this.fractionDataViewsContainer = fractionDataViewsContainer;
    }

    /**
     * @param showIncludedDataPoints the showIncludedDataPoints to set
     */
    public void setShowIncludedDataPoints(IncludedTypeEnum showIncludedDataPoints) {
        this.showIncludedDataPoints = showIncludedDataPoints;
    }

    /**
     * @param tics the tics to set
     */
    public void setTics(BigDecimal[] tics) {
        this.tics = tics;
    }

    /**
     * @return the tics
     */
    public BigDecimal[] getTics() {
        return tics;
    }

    /**
     * @return the paintColor
     */
    public Color getPaintColor() {
        return paintColor;
    }

    /**
     * @param paintColor the paintColor to set
     */
    public void setPaintColor(Color paintColor) {
        this.paintColor = paintColor;
    }

    @Override
    public void toggleFractionInclusion(boolean included) {
        tripoliFraction.toggleAllData(included);

        // force masking array
        if (included) {
            tripoliFraction.applyMaskingArray();
        }

        tripoliFraction.setShowVerticalLineAtThisIndex(-1);

        ((AbstractRawDataView) sampleSessionDataView).refreshPanel();

        // feb 2013 standards not put to redux anymore
        try {
            ((UPbFractionI) tripoliFraction.getuPbFraction()).setRejected(!included);
        } catch (Exception e) {
            System.out.println("line 1112 abstractrawdataview problem");
        }

    }

    /**
     *
     */
    public void updateReportTable() {

        // detecting correct object
        if (sampleSessionDataView == null) {
            ((TripoliSessionFractionationCalculatorInterface) this).applyCorrections();
            // march 2014 changed
            // ((AbstractSessionForStandardDataModel)((SessionOfStandardView) this).getSessionForStandardDataModel()).getTripoliSession().applyCorrections();

            //this.preparePanel();//.getTripoliSession().applyCorrections();
            System.out.println("UPDATETABLE ABSTRACTRAWDATA   SELF");
        } else {
            ((TripoliSessionFractionationCalculatorInterface) sampleSessionDataView).applyCorrections();
            System.out.println("UPDATETABLE ABSTRACTRAWDATA  SESSION");
        }

        updateReportTableView();

        if (sampleSessionDataView instanceof AbstractDataMonitorView) {
            ((AbstractDataMonitorView) sampleSessionDataView).prepareConcordia();
        }

    }

    public void updateReportTableView() {
        if (uPbReduxFrame == null) {
            uPbReduxFrame = ((AbstractRawDataView) sampleSessionDataView).getuPbReduxFrame();
            uPbReduxFrame.updateReportTable(true);
        }
    }

    /**
     * @return the notShownDueToBelowDetectionFlag
     */
    public boolean isNotShownDueToBelowDetectionFlag() {
        return notShownDueToBelowDetectionFlag;
    }

    /**
     * @return the dataPresentationMode
     */
    public DataPresentationModeEnum getDataPresentationMode() {
        return dataPresentationMode;
    }

    /**
     * @param dataPresentationMode the dataPresentationMode to set
     */
    public void setDataPresentationMode(DataPresentationModeEnum dataPresentationMode) {
        this.dataPresentationMode = dataPresentationMode;
    }

    /**
     * @return the fractionRawDataViews
     */
    public AbstractRawDataView[] getFractionRawDataViews() {
        return fractionRawDataViews;
    }

    /**
     * @param fractionRawDataViews the fractionRawDataViews to set
     */
    public void setFractionRawDataViews(AbstractRawDataView[] fractionRawDataViews) {
        this.fractionRawDataViews = fractionRawDataViews;
    }

    /**
     * @return the standardValue
     */
    public double getStandardValue() {
        return standardValue;
    }

    /**
     * @return the uPbReduxFrame
     */
    public ETReduxFrame getuPbReduxFrame() {
        return uPbReduxFrame;
    }
}
