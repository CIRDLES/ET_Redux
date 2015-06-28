/*
 * DataViewsOverlay.java
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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import javax.swing.JLayeredPane;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.earthtime.Tripoli.beans.MaskingShade;
import org.earthtime.Tripoli.dataModels.DataModelInterface;
import org.earthtime.Tripoli.dataModels.DownholeFractionationDataModel;
import org.earthtime.Tripoli.dataViews.AbstractRawDataView;
import org.earthtime.Tripoli.dataViews.simpleViews.FitFunctionDataInterface;
import org.earthtime.Tripoli.dataViews.simpleViews.MaskingShadeControl;
import org.earthtime.Tripoli.dataViews.simpleViews.usedByReflection.FitFunctionsOnRatioDataView;
import org.earthtime.Tripoli.dataViews.simpleViews.usedByReflection.RawIntensitiesDataView;
import org.earthtime.Tripoli.fractions.TripoliFraction;
import org.earthtime.dataDictionaries.DataPresentationModeEnum;
import org.earthtime.visualizationUtilities.ColorGradient;

/**
 *
 * @author James F. Bowring
 */
public class DataViewsOverlay extends AbstractRawDataView implements MaskingShadeTargetInterface, FitFunctionDataInterface {

    private final AbstractRawDataView[] tripoliFractionRawDataModelViews;
    private transient ArrayList<SortedSet<ValFrac>> sortedValFracs;
    private AbstractRawDataView rawDataView;

    /**
     *
     */
    protected DownholeFractionationDataModel downholeFractionationDataModel;
    private double[] myFittedAverages;
    private int countOfMaskedTimeSlotsOnLeft;
    private int countOfMaskedTimeSlotsOnRight;
    private double[] fitFunctionNormalizedTimes;

    /**
     *
     *
     * @param tripoliFractionRawDataModelViews
     * @param sampleSessionDataView
     * @param dataPresentationMode the value of dataPresentationMode
     * @param bounds
     */
    public DataViewsOverlay(//
            AbstractRawDataView[] tripoliFractionRawDataModelViews, //
            JLayeredPane sampleSessionDataView, //
            DataPresentationModeEnum dataPresentationMode,//
            Rectangle bounds) {
        super(bounds);

        this.tripoliFractionRawDataModelViews = tripoliFractionRawDataModelViews;
        this.sampleSessionDataView = sampleSessionDataView;
        this.downholeFractionationDataModel = null;
        this.myFittedAverages = null;
        this.dataPresentationMode = dataPresentationMode;

        this.standardValue = tripoliFractionRawDataModelViews[0].getStandardValue();

        addMeAsMouseListener();

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
    @Override
    public void paint(Graphics2D g2d) {

        super.paint(g2d);

        //TODO: refactor to remove this code repetition
        // moved here to avoid painting every FitFunctionsOnRatioDataView as an overlay
        if (tripoliFractionRawDataModelViews[0] instanceof FitFunctionsOnRatioDataView) {
            g2d.setStroke(new BasicStroke(0.5f));
            for (int i = 0; i < tripoliFractionRawDataModelViews.length; i++) {
                double[] tfOnPeakData = tripoliFractionRawDataModelViews[i].getMyOnPeakData();

                tripoliFraction = tripoliFractionRawDataModelViews[i].getTripoliFraction();
                // draw onPeak line
                Shape onPeakLine = new Path2D.Double();
                g2d.setPaint(tripoliFraction.isIncluded() ? tripoliFractionRawDataModelViews[i].getPaintColor() : EXCLUDED_COLOR);
                ((Path2D) onPeakLine).moveTo(//
                        mapX(myOnPeakNormalizedAquireTimes[0]), //
                        mapY(tfOnPeakData[0]));

                for (int j = 1; j < tfOnPeakData.length; j++) {
                    ((Path2D) onPeakLine).lineTo( //
                            mapX(myOnPeakNormalizedAquireTimes[j]), mapY(tfOnPeakData[j]));
                }
                g2d.draw(onPeakLine);

                // now check for excluded points and add redLine coming and going
                for (int j = 1; j < tfOnPeakData.length; j++) {
                    if (!tripoliFraction.getDataActiveMap()[j]) {
                        Path2D excludedLine = new Path2D.Double();
                        if (j == 0) {
                            excludedLine.moveTo(//
                                    mapX(myOnPeakNormalizedAquireTimes[0]), //
                                    mapY(tfOnPeakData[0]));
                        } else {
                            excludedLine.moveTo(//
                                    mapX(myOnPeakNormalizedAquireTimes[j - 1]), //
                                    mapY(tfOnPeakData[j - 1]));
                            excludedLine.lineTo(//
                                    mapX(myOnPeakNormalizedAquireTimes[j]), //
                                    mapY(tfOnPeakData[j]));
                        }

                        if (j < tfOnPeakData.length - 1) {
                            excludedLine.lineTo(//
                                    mapX(myOnPeakNormalizedAquireTimes[j + 1]), //
                                    mapY(tfOnPeakData[j + 1]));
                        }

                        g2d.setPaint(EXCLUDED_COLOR);
                        g2d.draw(excludedLine);

                    }

                    // check for temporary red vertical line from mouse click
                    int chosenDatumIndex = tripoliFraction.getShowVerticalLineAtThisIndex();
                    if (chosenDatumIndex > -1) {
                        paintLineOverSelectedDatum(g2d, chosenDatumIndex);
                    }
                }
            }
        } else if (tripoliFractionRawDataModelViews[0] instanceof RawIntensitiesDataView) {
            // do nothing, since overlay is handled by the old overlay all views = slow
        } else {
            g2d.setStroke(new BasicStroke(0.5f));
            for (int i = 0; i < tripoliFractionRawDataModelViews.length; i++) {
                double[] tfOnPeakData = tripoliFractionRawDataModelViews[i].getMyOnPeakData();
                tripoliFraction = tripoliFractionRawDataModelViews[i].getTripoliFraction();
                Shape dataLine = new Path2D.Double();
                g2d.setPaint(tripoliFraction.isIncluded() ? tripoliFractionRawDataModelViews[i].getPaintColor() : EXCLUDED_COLOR);
                try {
                    ((Path2D) dataLine).moveTo(//
                            mapX(myOnPeakNormalizedAquireTimes[0]), //
                            mapY(tfOnPeakData[0]));
                } catch (Exception e) {
                }

                for (int j = 1; j < tfOnPeakData.length; j++) {
                    try {
                        ((Path2D) dataLine).lineTo( //
                                mapX(myOnPeakNormalizedAquireTimes[j]), mapY(tfOnPeakData[j]));
                    } catch (Exception e) {
                    }
                }
                g2d.draw(dataLine);

                // check for temporary red vertical line from mouse click
                int chosenDatumIndex = tripoliFraction.getShowVerticalLineAtThisIndex();
                if (chosenDatumIndex > -1) {
                    paintLineOverSelectedDatum(g2d, chosenDatumIndex);
                }
            }
        }

        if (downholeFractionationDataModel != null) {

            // draw averageLine BLACK
            Shape averageLine = new Path2D.Double();
            g2d.setPaint(Color.BLACK);
            g2d.setStroke(new BasicStroke(2.5f));
            ((Path2D) averageLine).moveTo(//
                    mapX(fitFunctionNormalizedTimes[0]), //
                    mapY(myOnPeakData[0]));

            for (int i = 1; i < myOnPeakData.length; i++) {
                ((Path2D) averageLine).lineTo( //
                        mapX(fitFunctionNormalizedTimes[i]), mapY(myOnPeakData[i]));
            }
            g2d.draw(averageLine);

            // draw fittedAverageLine RED
            Shape fittedAverageLine = new Path2D.Double();
            g2d.setPaint(Color.RED);
            g2d.setStroke(new BasicStroke(3.0f));
            ((Path2D) fittedAverageLine).moveTo(//
                    mapX(fitFunctionNormalizedTimes[0]), //
                    mapY(myFittedAverages[0]));

            for (int i = 1; i < fitFunctionNormalizedTimes.length; i++) {
                ((Path2D) fittedAverageLine).lineTo(//
                        mapX(fitFunctionNormalizedTimes[i]), mapY(myFittedAverages[i]));
            }
            g2d.draw(fittedAverageLine);

        }
    }

    /**
     *
     */
    @Override
    public void updateFittedData() {

        myFittedAverages = downholeFractionationDataModel.getFitFunctionLogValues().clone();

        myOnPeakData = downholeFractionationDataModel.getWeightedMeanIntegrations().clone();

//        fitFunctionNormalizedTimes = new double[MaskingSingleton.getInstance().getCountOfActiveData()];
//        boolean[] maskingArray = MaskingSingleton.getInstance().getMaskingArray();
//
//        int index = 0;
//        for (int i = 0; i < maskingArray.length; i++) {
//            if (maskingArray[i]) {
//                fitFunctionNormalizedTimes[index] = myOnPeakNormalizedAquireTimes[i];
//                index++;
//            }
//        }
        fitFunctionNormalizedTimes = downholeFractionationDataModel.getActiveXvalues();

        // find min and max y
        for (int i = 0; i < myOnPeakData.length; i++) {
            myOnPeakData[i] = convertLogDatumToPresentationMode(myOnPeakData[i]);
            myFittedAverages[i] = convertLogDatumToPresentationMode(myFittedAverages[i]);
        }

    }

    /**
     *
     */
    @Override
    public void preparePanel() {
        this.removeAll();

        setDisplayOffsetY(0.0);
        setDisplayOffsetX(0.0);

        // set colors for overlay view
        // dec 2012 and determine if there is at least one valid == not belowdetection
        boolean allBelowDetection = false;
        Color[] standardsColorGradient = ColorGradient.generateGradient(Color.green, Color.blue, tripoliFractionRawDataModelViews.length);
        for (int i = 0; i < tripoliFractionRawDataModelViews.length; i++) {
            tripoliFractionRawDataModelViews[i].setPaintColor(standardsColorGradient[i]);
            allBelowDetection = allBelowDetection //
                    || tripoliFractionRawDataModelViews[i].getDataModel().isBelowDetection();
        }

        if (!allBelowDetection) {

            // sort all data points with associated tripoliFractionRawDataModelViews into timestamp slots
            sortedValFracs = new ArrayList<>();
            for (int i = 0; i < tripoliFractionRawDataModelViews[0].getMyOnPeakData().length; i++) {
                sortedValFracs.add(new TreeSet<>());
            }

            for (int i = 0; i < tripoliFractionRawDataModelViews.length; i++) {
                double[] onPeakData = tripoliFractionRawDataModelViews[i].getMyOnPeakData();
                if (onPeakData != null) {
                    for (int j = 0; j < onPeakData.length; j++) {
                        sortedValFracs.get(j).add(new ValFrac(onPeakData[j], tripoliFractionRawDataModelViews[i]));
                    }
                }
            }

            // set up aquire times
            myOnPeakNormalizedAquireTimes = tripoliFractionRawDataModelViews[0].getMyOnPeakNormalizedAquireTimes();

            // handle case of standard averages
            if (downholeFractionationDataModel != null) {
                // set up fitted data
                updateFittedData();

                // masking shade only for standard now *******************************************
                // first determine width of mask
                countOfMaskedTimeSlotsOnLeft = -1;
                for (int i = 0; i < MaskingShadeControl.MAX_SHADE_COUNT; i++) {
                    if (!downholeFractionationDataModel.getMaskingSingleton().getMaskingArray()[i]) {
                        countOfMaskedTimeSlotsOnLeft++;
                    }
                }

                JLayeredPane myMaskingShadeLeft = new MaskingShade( //
                        this, //
                        false,//
                        MaskingShade.PULL_FROM_LEFT,//
                        countOfMaskedTimeSlotsOnLeft);

                add(myMaskingShadeLeft, javax.swing.JLayeredPane.DEFAULT_LAYER);

                countOfMaskedTimeSlotsOnRight = -1;
                int lowestAquisitionIndex = //
                        downholeFractionationDataModel.getMaskingSingleton().getMaskingArray().length - MaskingShadeControl.MAX_SHADE_COUNT;
                for (int i = lowestAquisitionIndex; i < downholeFractionationDataModel.getMaskingSingleton().getMaskingArray().length; i++) {
                    if (!downholeFractionationDataModel.getMaskingSingleton().getMaskingArray()[i]) {
                        countOfMaskedTimeSlotsOnRight++;
                    }
                }

                JLayeredPane myMaskingShadeRight = new MaskingShade( //
                        this, //
                        false,//
                        MaskingShade.PULL_FROM_RIGHT,//
                        countOfMaskedTimeSlotsOnRight);

                add(myMaskingShadeRight, javax.swing.JLayeredPane.DEFAULT_LAYER);
            }
        }
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
     * @param fractionationDataModel
     */
    public void setFractionationDataModel(DownholeFractionationDataModel fractionationDataModel) {
        this.downholeFractionationDataModel = fractionationDataModel;
    }

    /**
     *
     * @param currentShadeX
     * @return
     */
    @Override
    public int provideShadeXFromLeft(int currentShadeX) {

//        // calculate nearest data point horizontally limited to MaskingShadeControl.MAX_SHADE_COUNT
//        int nearest = Math.min(MaskingShadeControl.MAX_SHADE_COUNT - 1, convertMouseXToValue(currentShadeX));
//
//        // mask up to nearest
//        for (int i = 0; i < nearest + 1; i++) {
//            MaskingSingleton.getInstance().getMaskingArray()[i] = false;
//        }
//        for (int i = nearest + 1; i < MaskingShadeControl.MAX_SHADE_COUNT; i++) {
//            if (i >= 0) {
//                MaskingSingleton.getInstance().getMaskingArray()[i] = true;
//            }
//        }
//
//        // april 2014
//        if (nearest < 0) {
//            MaskingSingleton.getInstance().setLeftShadeCount(-1);
//        } else {
//            MaskingSingleton.getInstance().setLeftShadeCount(nearest);
//        }
//
//        // recalculate averages and fits
//        if (downholeFractionationDataModel != null) {
//            ((TripoliSessionRawDataView) sampleSessionDataView).getTripoliSession().applyMaskingArray();
//
////            ((TripoliSessionRawDataView) sampleSessionDataView).//
////                    getSessionFractionationCalculator().calculateDownholeFitSummariesForPrimaryStandard();
//        }
//
//        // refresh all
//        ((AbstractRawDataView) sampleSessionDataView).refreshPanel();
//
//        //updateReportTable();
//        // send proposed width back to set shade halfway between points
//        return (int) mapX(nearest);
        return 0;
    }

    /**
     *
     * @param currentShadeX
     * @return
     */
    @Override
    public int provideShadeXFromRight(int currentShadeX) {
//
//        // calculate nearest data point horizontally limited to MaskingShadeControl.MAX_SHADE_COUNT
//        int lowestAquisitionIndex = //
//                downholeFractionationDataModel.getMaskingSingleton().getMaskingArray().length - MaskingShadeControl.MAX_SHADE_COUNT;
//        int nearest = Math.max( //
//                lowestAquisitionIndex,//
//                convertMouseXToValue(getWidth() + currentShadeX));
//
//        // mask up to nearest
//        for (int i = lowestAquisitionIndex; i < downholeFractionationDataModel.getMaskingSingleton().getMaskingArray().length; i++) {
//            downholeFractionationDataModel.getMaskingSingleton().getMaskingArray()[i] = true;
//        }
//        for (int i = nearest; i < downholeFractionationDataModel.getMaskingSingleton().getMaskingArray().length; i++) {
//            downholeFractionationDataModel.getMaskingSingleton().getMaskingArray()[i] = false;
//        }
//
//        // april 2014
//        MaskingSingleton.getInstance().setRightShadeCount(downholeFractionationDataModel.getMaskingSingleton().getMaskingArray().length - nearest - 1);
//
//        // recalculate averages and fits
//        if (downholeFractionationDataModel != null) {
//            ((TripoliSessionRawDataView) sampleSessionDataView).getTripoliSession().applyMaskingArray();
//            ((TripoliSessionRawDataView) sampleSessionDataView).//
//                    getSessionFractionationCalculator().calculateDownholeFitSummariesForPrimaryStandard();
//        }
//
//        // refresh all
//        ((AbstractRawDataView) sampleSessionDataView).refreshPanel();
//
//        //updateReportTable();
//        // send tab to right edge at last time slot
//        if (nearest == downholeFractionationDataModel.getMaskingSingleton().getMaskingArray().length - 1) {
//            nearest++;
//        }
//
//        return getWidth() - (int) mapX(nearest);

        return 0;
    }

    /**
     * @return the downholeFractionationDataModel
     */
    public DownholeFractionationDataModel getDownholeFractionationDataModel() {
        return downholeFractionationDataModel;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean amShowingUnknownFraction() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setShowFittedFunction(boolean showFittedFunction) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     */
    protected class ValFrac implements Comparable<ValFrac> {

        double val;
        AbstractRawDataView fracView;

        /**
         *
         * @param val
         * @param fracView
         */
        public ValFrac(double val, AbstractRawDataView fracView) {
            this.val = val;
            this.fracView = fracView;
        }

        /**
         *
         * @param o
         * @return
         */
        @Override
        public int compareTo(ValFrac o) {
            return Double.compare(val, o.val);
        }

        /**
         *
         */
        public void printMe() {
            Format formatter = new SimpleDateFormat("HH.mm.ss");
            System.out.println("Value = " + val + "FRACTION = "//
                    + fracView.getTripoliFraction().getFractionID() + "  " + formatter.format((new Date(fracView.getTripoliFraction().getPeakTimeStamp()))));
        }
    }

    /**
     *
     * @param index
     * @param yValue
     * @return
     */
    protected AbstractRawDataView findDataViewOfPoint(int index, double yValue) {
        AbstractRawDataView dataViewOfPoint = null;

        // 3 pixels tolerance
        double tolerance = Math.abs(convertMouseYToValue(4) - convertMouseYToValue(1));

        SortedSet<ValFrac> valuesAtIndex = sortedValFracs.get(index);

        // walk until find value >=
        Iterator it = valuesAtIndex.iterator();
        while (it.hasNext() && (dataViewOfPoint == null)) {
            ValFrac item = (ValFrac) it.next();

            if (((yValue - tolerance) <= item.val) //
                    && ((yValue + tolerance) >= item.val)) {
                dataViewOfPoint = item.fracView;
            }
        }

        return dataViewOfPoint;
    }

    /**
     *
     * @param evt
     */
    @Override
    public void mousePressed(MouseEvent evt) {

        int timeSlot = convertMouseXToValue(evt.getX());

        // test for special case of rawisotope data which is twice as wide
        if (tripoliFractionRawDataModelViews[0] instanceof RawIntensitiesDataView) {
            timeSlot = (int) Math.max(0.0, timeSlot - tripoliFractionRawDataModelViews[0].getMyOnPeakData().length);
        } else if (timeSlot >= myOnPeakNormalizedAquireTimes.length) {
            // catch out of bounds
            timeSlot = myOnPeakNormalizedAquireTimes.length - 1;
        }

        if (timeSlot > -1) {
            try {
                rawDataView = findDataViewOfPoint(timeSlot, convertMouseYToValue(evt.getY()));
                if (rawDataView != null) {
                    final TripoliFraction myTripoliFraction = rawDataView.getTripoliFraction();

                    if (timeSlot >= 0) {
                        final int finalTimeSlot = timeSlot;

                        myTripoliFraction.setColorMeExcluded(true);
                        myTripoliFraction.setShowVerticalLineAtThisIndex(finalTimeSlot);
                        myTripoliFraction.setShowSecondVerticalLineAtThisIndex(finalTimeSlot);
                        sampleSessionDataView.repaint();
                        updateReportTable();

                        // handle right button or control button for mac mouse
                        if (evt.isPopupTrigger() && ((evt.getButton() == MouseEvent.BUTTON3) || evt.isControlDown())) {

                            //Create the popup menu.
                            JPopupMenu popup = new JPopupMenu();

                            //  show coordinates fyi
                            double onPeakValue = rawDataView.getMyOnPeakData()[finalTimeSlot];
                            DecimalFormat f = new DecimalFormat("0.0000E00");

                            JMenuItem menuItem = //
                                    new JMenuItem("(" + timeSlot + ", " + f.format(onPeakValue) + ")");
                            popup.add(menuItem);

                            if (myTripoliFraction.isIncluded()) {
                                menuItem = new JMenuItem("Toggle aquisition (include / exclude).");
                                menuItem.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent arg0) {
                                        myTripoliFraction.flipIncludeExcludeOneDataAquisition(finalTimeSlot);
                                        myTripoliFraction.setColorMeExcluded(false);
                                        myTripoliFraction.setShowVerticalLineAtThisIndex(-1);
                                        // repaint fraction
                                        sampleSessionDataView.repaint();

                                        // recalculate averages and fits
                                        if (downholeFractionationDataModel != null) {
                                            ((TripoliSessionRawDataView) sampleSessionDataView).//
                                                    getSessionFractionationCalculator().calculateDownholeFitSummariesForPrimaryStandard();
                                        }

                                        // refresh all
                                        ((AbstractRawDataView) sampleSessionDataView).refreshPanel();

                                        updateReportTable();
                                    }
                                });
                                popup.add(menuItem);

                                menuItem = new JMenuItem("EXCLUDE this fraction (all aquisitions).");
                                menuItem.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent arg0) {

                                        myTripoliFraction.toggleAllDataExceptShaded(false);
                                        myTripoliFraction.setColorMeExcluded(false);
                                        myTripoliFraction.setShowVerticalLineAtThisIndex(-1);

                                        // repaint fraction
                                        sampleSessionDataView.repaint();

                                        // recalculate averages and fits
                                        if (downholeFractionationDataModel != null) {
                                            ((TripoliSessionRawDataView) sampleSessionDataView).//
                                                    getSessionFractionationCalculator().calculateDownholeFitSummariesForPrimaryStandard();
                                        }

                                        // refresh all
                                        ((AbstractRawDataView) sampleSessionDataView).refreshPanel();

                                        updateReportTable();
                                    }
                                });
                                popup.add(menuItem);
                            } else {
                                menuItem = new JMenuItem("INCLUDE this fraction (all aquisitions).");
                                menuItem.addActionListener(new ActionListener() {
                                    @Override
                                    public void actionPerformed(ActionEvent arg0) {

                                        myTripoliFraction.toggleAllDataExceptShaded(true);
                                        myTripoliFraction.setColorMeExcluded(false);
                                        myTripoliFraction.setShowVerticalLineAtThisIndex(-1);

                                        // repaint fraction
                                        sampleSessionDataView.repaint();

                                        // recalculate averages and fits
                                        if (downholeFractionationDataModel != null) {
                                            ((TripoliSessionRawDataView) sampleSessionDataView).//
                                                    getSessionFractionationCalculator().calculateDownholeFitSummariesForPrimaryStandard();
                                        }

                                        // refresh all
                                        ((AbstractRawDataView) sampleSessionDataView).refreshPanel();

                                        updateReportTable();

                                    }
                                });
                                popup.add(menuItem);

                            }

                            // show the menu
                            popup.show(evt.getComponent(), evt.getX() + 10, evt.getY() - 10);

                        }
                    }

                }
            } finally {
                // System.out.println( "MOUSE: " + convertMouseXToValue( evt.getX() ) + ", " + convertMouseYToValue( evt.getY() ) + " from " + evt.getY() );
            }
        }
    }

    /**
     *
     * @param e
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (rawDataView != null) {
            rawDataView.getTripoliFraction().setColorMeExcluded(false);
            rawDataView.getTripoliFraction().setShowVerticalLineAtThisIndex(-1);
            sampleSessionDataView.repaint();

            updateReportTable();
        }
    }
}
