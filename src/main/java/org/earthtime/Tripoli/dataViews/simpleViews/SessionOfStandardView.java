/*
 * RawRatioAlphasDataView.java
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
package org.earthtime.Tripoli.dataViews.simpleViews;

import Jama.Matrix;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.SortedSet;
import javax.swing.JLayeredPane;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.earthtime.Tripoli.dataModels.DataModelFitFunctionInterface;
import org.earthtime.Tripoli.dataModels.DataModelInterface;
import org.earthtime.Tripoli.dataModels.sessionModels.AbstractSessionForStandardDataModel;
import org.earthtime.Tripoli.dataViews.AbstractRawDataView;
import org.earthtime.Tripoli.fitFunctions.AbstractFunctionOfX;
import org.earthtime.Tripoli.fractions.TripoliFraction;
import org.earthtime.Tripoli.sessions.TripoliSessionFractionationCalculatorInterface;
import org.earthtime.dataDictionaries.DataPresentationModeEnum;
import org.earthtime.utilities.TicGeneratorForAxes;

/**
 *
 * @author James F. Bowring
 */
public class SessionOfStandardView extends AbstractRawDataView implements FitFunctionDataInterface {

    /**
     *
     */
//    public static int DEFAULT_WIDTH_OF_PANE = 490;//600;
    //
    private DataModelFitFunctionInterface sessionForStandardDataModel;
    private SortedSet<TripoliFraction> tripoliFractions;
    private TripoliFraction[] tripoliFractionArray;
    private ArrayList<Double> zeroBasedFractionAquireTimes;
    private boolean[] fractionIncludedMap;
    private double[] stdErrorOfMean;
    // because of log space and the fact that upper and lower may not be equal in future
    // these are for plotting only
    private double[] myOnPeakDataPlusUnct;
    private double[] myOnPeakDataLessUnct;
    private double[] myOnPeakDataPlusUnctPlusOD;
    private double[] myOnPeakDataLessUnctPlusOD;
    private double[][] fitFunctionDataDisplay;
    private double[][] fitFunctionMinusUnctDataDisplay;
    private double[][] fitFunctionPlusUnctDataDisplay;
    private Color paintColor;

    /**
     *
     * @param sampleSessionDataView
     * @param rawRatioDataModel
     * @param sessionForStandardDataModel
     * @param tripoliFractions
     * @param dataPresentationMode
     * @param bounds
     */
    public SessionOfStandardView(//
            JLayeredPane sampleSessionDataView,//
            DataModelFitFunctionInterface sessionForStandardDataModel,//
            SortedSet<TripoliFraction> tripoliFractions,//
            DataModelInterface rawRatioDataModel,//
            DataPresentationModeEnum dataPresentationMode,//
            Rectangle bounds) {
        super(bounds);

        this.sampleSessionDataView = sampleSessionDataView;
        this.sessionForStandardDataModel = sessionForStandardDataModel;
        this.tripoliFractions = tripoliFractions;

        this.tripoliFractionArray = null;
        this.zeroBasedFractionAquireTimes = null;
        this.fractionIncludedMap = null;
        this.stdErrorOfMean = null;
        this.fitFunctionDataDisplay = null;

        this.paintColor = Color.black;

        this.dataPresentationMode = dataPresentationMode;

        this.standardValue = ((AbstractSessionForStandardDataModel) sessionForStandardDataModel).getStandardValue();

        addMeAsMouseListener();
    }

    /**
     *
     * @param g2d
     */
    @Override
    public void paint(Graphics2D g2d) {
        //super.paint( g2d );

        paintInit(g2d);

        if (!(myOnPeakData == null)) {
            // box and whiskers
            g2d.setStroke(new BasicStroke(1.0f));
            for (int i = 0; i < myOnPeakData.length; i++) {
                try {
                    Shape meanSquare = new Rectangle2D.Double( //
                            mapX(zeroBasedFractionAquireTimes.get(i)) - 1.0,// 
                            mapY(myOnPeakData[i]) - 1.0,//
                            2,
                            2);
                    g2d.setPaint(determineDataColor(i, paintColor));

                    g2d.fill(meanSquare);
                    g2d.draw(meanSquare);

                    if (fitFunctionDataDisplay != null) {
                        g2d.setColor(Color.red);

                        Shape stdErrLinePlusOD = new Line2D.Double(//
                                mapX(zeroBasedFractionAquireTimes.get(i)) + 0.0,// 
                                mapY(myOnPeakDataPlusUnctPlusOD[i]),//
                                mapX(zeroBasedFractionAquireTimes.get(i)) + 0.0,// 
                                mapY(myOnPeakDataLessUnctPlusOD[i]));

                        g2d.draw(stdErrLinePlusOD);
                    }

                    g2d.setColor(Color.black);
                    Shape stdErrLine = new Line2D.Double(//
                            mapX(zeroBasedFractionAquireTimes.get(i)) + 0.0,// 
                            mapY(myOnPeakDataPlusUnct[i]),//
                            mapX(zeroBasedFractionAquireTimes.get(i)) + 0.0,// 
                            mapY(myOnPeakDataLessUnct[i]));

                    g2d.draw(stdErrLine);

                    // paint red vertical line where mouse clicks
                    if (tripoliFractionArray[i].getShowVerticalLineAtThisIndex() > -1) {
                        paintFractionVerticalTicRed(g2d, i);
                    }
                } catch (Exception e) {
                }
            }
        }

        if (fitFunctionDataDisplay != null) {

            // plot smoothing fittedCurve or fitted line as required
            Path2D fittedCurve = new Path2D.Double();
            fittedCurve.moveTo(//
                    mapX(fitFunctionDataDisplay[0][0]),//
                    mapY(fitFunctionDataDisplay[1][0]));

            for (int i = 1; i < fitFunctionDataDisplay[1].length; i++) {
                fittedCurve.lineTo(//
                        mapX(fitFunctionDataDisplay[0][i]),//
                        mapY(fitFunctionDataDisplay[1][i]));
            }

            for (int i = fitFunctionDataDisplay[1].length - 1; i >= 0; i--) {
                fittedCurve.lineTo(//
                        mapX(fitFunctionDataDisplay[0][i]),//
                        mapY(fitFunctionDataDisplay[1][i]) + 1.0f);
            }

            fittedCurve.lineTo(//
                    mapX(fitFunctionDataDisplay[0][0]),//
                    mapY(fitFunctionDataDisplay[1][0]));

            g2d.setPaint(Color.blue);
            g2d.fill(fittedCurve);

            // plot smoothing PLUS UNCERTAINTY ENVELOPE fittedCurve or fitted line as required
            fittedCurve = new Path2D.Double();
            fittedCurve.moveTo(//
                    mapX(fitFunctionPlusUnctDataDisplay[0][0]),//
                    mapY(fitFunctionPlusUnctDataDisplay[1][0]));

            for (int i = 1; i < fitFunctionPlusUnctDataDisplay[1].length; i++) {
                fittedCurve.lineTo(//
                        mapX(fitFunctionPlusUnctDataDisplay[0][i]),//
                        mapY(fitFunctionPlusUnctDataDisplay[1][i]));
            }

            for (int i = fitFunctionMinusUnctDataDisplay[1].length - 1; i >= 0; i--) {
                fittedCurve.lineTo(//
                        mapX(fitFunctionMinusUnctDataDisplay[0][i]),//
                        mapY(fitFunctionMinusUnctDataDisplay[1][i]));
            }

            fittedCurve.lineTo(//
                    mapX(fitFunctionPlusUnctDataDisplay[0][0]),//
                    mapY(fitFunctionPlusUnctDataDisplay[1][0]));

            Composite originalComposite = g2d.getComposite();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.10f));
            g2d.setPaint(Color.red);
            g2d.fill(fittedCurve);
            //restore composite
            g2d.setComposite(originalComposite);
        }

    }

    @Override
    protected Color determineDataColor(int index, Color defaultColor) {
        return ((Color) (fractionIncludedMap[index] ? defaultColor : EXCLUDED_COLOR));
    }

    private void paintFractionVerticalTicRed(Graphics2D g2d, int chosenDatumIndex) {
        Shape redDatumLine = new Line2D.Double(//
                mapX(zeroBasedFractionAquireTimes.get(chosenDatumIndex)),// 
                mapY(minY),//
                mapX(zeroBasedFractionAquireTimes.get(chosenDatumIndex)),// 
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
     */
    @Override
    public void updateFittedData() {

        // Y-axis is ratios
        minY = Double.MAX_VALUE;
        maxY = -Double.MAX_VALUE;

        // X-axis lays out time 
        minX = 0;
        maxX = zeroBasedFractionAquireTimes.get(zeroBasedFractionAquireTimes.size() - 1);

        AbstractFunctionOfX fitFunc = sessionForStandardDataModel.getSelectedFitFunction();

        if (fitFunc != null) {

            double[] timesOfMatrixJfPlotting = ((AbstractSessionForStandardDataModel) sessionForStandardDataModel).getTripoliSession().getTimesForPlotting();

            int countOfPlottedPoints = timesOfMatrixJfPlotting.length;

            fitFunctionDataDisplay = new double[2][countOfPlottedPoints];
            fitFunctionMinusUnctDataDisplay = new double[2][countOfPlottedPoints];
            fitFunctionPlusUnctDataDisplay = new double[2][countOfPlottedPoints];

            fitFunctionDataDisplay[0] = timesOfMatrixJfPlotting;
            fitFunctionMinusUnctDataDisplay[0] = timesOfMatrixJfPlotting;
            fitFunctionPlusUnctDataDisplay[0] = timesOfMatrixJfPlotting;

            Matrix matrixJfPlotting = //
                    ((AbstractSessionForStandardDataModel) sessionForStandardDataModel).getTripoliSession().getMatrixJfPlottingActiveStandards(fitFunc.getShortName());

            double[] variances = fitFunc.calculateInterpolatedVariances(matrixJfPlotting, timesOfMatrixJfPlotting);

            for (int i = 0; i < countOfPlottedPoints; i++) {

                try {
                    double fitFuncValue = fitFunc.f(fitFunctionDataDisplay[0][i]);
                    double fitFuncUnct = Math.sqrt(variances[i]);

                    fitFunctionDataDisplay[1][i] = convertLogDatumToPresentationMode(fitFuncValue);
                    fitFunctionMinusUnctDataDisplay[1][i] = convertLogDatumToPresentationMode(fitFuncValue - 2 * fitFuncUnct);
                    fitFunctionPlusUnctDataDisplay[1][i] = convertLogDatumToPresentationMode(fitFuncValue + 2 * fitFuncUnct);

                } catch (Exception e) {
                }
            }

            // prepare fitted data for display as alpha or ratio or log
            for (int i = 0; i < fitFunctionDataDisplay[1].length; i++) {
                minY = Math.min(minY, fitFunctionDataDisplay[1][i]);
                maxY = Math.max(maxY, fitFunctionDataDisplay[1][i]);

                // note uncertainty envelopes not to be scaled per Noah Feb 2013
            }

        } else {
            fitFunctionDataDisplay = null;

        }

        // show data anyway
        if (!(((AbstractSessionForStandardDataModel) sessionForStandardDataModel).getIncludedStandardMeanLogRatios() == null)) {

            // nov 2014
            myOnPeakData = ((AbstractSessionForStandardDataModel) sessionForStandardDataModel).getAllStandardsMeanLogRatios().clone();
            stdErrorOfMean = ((AbstractSessionForStandardDataModel) sessionForStandardDataModel).getAllStandardsMeanLogRatioStdErrs().clone();

            // prepare means data for display as alpha or ratio or log
            myOnPeakDataPlusUnct = new double[myOnPeakData.length];
            myOnPeakDataLessUnct = new double[myOnPeakData.length];
            myOnPeakDataPlusUnctPlusOD = new double[myOnPeakData.length];
            myOnPeakDataLessUnctPlusOD = new double[myOnPeakData.length];

            for (int i = 0; i < myOnPeakData.length; i++) {
                myOnPeakDataPlusUnct[i] = convertLogDatumToPresentationMode(myOnPeakData[i] + 2.0 * stdErrorOfMean[i]);
                myOnPeakDataLessUnct[i] = convertLogDatumToPresentationMode(myOnPeakData[i] - 2.0 * stdErrorOfMean[i]);

                if (fitFunc != null) {
                    double oneSigmaPlusOD = Math.sqrt(stdErrorOfMean[i] * stdErrorOfMean[i] + fitFunc.getOverDispersion());
                    myOnPeakDataPlusUnctPlusOD[i] = convertLogDatumToPresentationMode(myOnPeakData[i] + 2.0 * oneSigmaPlusOD);
                    myOnPeakDataLessUnctPlusOD[i] = convertLogDatumToPresentationMode(myOnPeakData[i] - 2.0 * oneSigmaPlusOD);
                }

                // do last as data is needed in above calcs            
                myOnPeakData[i] = convertLogDatumToPresentationMode(myOnPeakData[i]);

                // added for no fit func
                if (fitFunc == null) {
                    //handling alpha flip too
                    minY = Math.min(minY, myOnPeakDataPlusUnct[i]);
                    minY = Math.min(minY, myOnPeakDataLessUnct[i]);

                    maxY = Math.max(maxY, myOnPeakDataLessUnct[i]);
                    maxY = Math.max(maxY, myOnPeakDataPlusUnct[i]);
                } else {
                    // do both min and max to be sure especially as alphas may flip
                    // just use the one including plus OD  which will be bigger or equal to err
                    minY = Math.min(minY, myOnPeakDataPlusUnctPlusOD[i]);
                    minY = Math.min(minY, myOnPeakDataLessUnctPlusOD[i]);

                    maxY = Math.max(maxY, myOnPeakDataPlusUnctPlusOD[i]);
                    maxY = Math.max(maxY, myOnPeakDataLessUnctPlusOD[i]);
                }
            }
        }

        double xMarginStretch = TicGeneratorForAxes.generateMarginAdjustment(minX, maxX, 0.05);
        minX -= xMarginStretch;
        maxX += xMarginStretch;

        double yMarginStretch = TicGeneratorForAxes.generateMarginAdjustment(minY, maxY, 12.0 / this.getHeight());
        minY -= yMarginStretch;
        maxY += yMarginStretch;
    }

    /**
     *
     */
    @Override
    public void preparePanel() {

        this.removeAll();

        setDisplayOffsetY(0.0);
        setDisplayOffsetX(0.0);

        // map fraction means into myOnPeakData and fraction times into myOnPeakNormalizedAquireTimes  
        tripoliFractionArray = new TripoliFraction[tripoliFractions.size()];
        zeroBasedFractionAquireTimes = new ArrayList<Double>();
        fractionIncludedMap = new boolean[tripoliFractions.size()];

        int index = 0;
        Iterator<TripoliFraction> fractionIterator = tripoliFractions.iterator();
        while (fractionIterator.hasNext()) {
            TripoliFraction tf = fractionIterator.next();
            tripoliFractionArray[index] = tf;
            zeroBasedFractionAquireTimes.add((double) tf.getZeroBasedNormalizedTimeStamp() + 2.0);
            fractionIncludedMap[index] = tf.isIncluded();

            index++;
        }

        updateFittedData();

    }

    @Override
    public void mousePressed(MouseEvent evt) {
        int timeSlot = convertMouseXToValue(evt.getX()) - (int) shiftAquiredTimeIndex;

        if (timeSlot < 0) {
            timeSlot = 0;
        }

        // find fraction to left of click or under it
        int index = Collections.binarySearch(zeroBasedFractionAquireTimes, (double) timeSlot);

        if (index < 0) {
            index = Math.abs(index) - 2;
        }
        if (index >= (zeroBasedFractionAquireTimes.size() - 1)) {
            index = zeroBasedFractionAquireTimes.size() - 2;
        }

        // adjust index to nearest fraction
        if ((timeSlot - zeroBasedFractionAquireTimes.get(index)) //
                > (zeroBasedFractionAquireTimes.get(index + 1) - timeSlot)) {
            index++;
        }

        final int finalTimeSlot = index;

        // tripolifraction used as placeholder for mouse click only
        tripoliFraction = tripoliFractionArray[finalTimeSlot];
        tripoliFraction.setShowVerticalLineAtThisIndex(finalTimeSlot);
        try {
            sampleSessionDataView.repaint();
        } catch (Exception e) {
        }

        // handle right button or control button for mac mouse
        // feb 2013 this solves the mac/windows/one button/ two button problem
        if (evt.isPopupTrigger() || (evt.getButton() != MouseEvent.BUTTON1)) {
            //Create the popup menu.
            JPopupMenu popup = new JPopupMenu();

            //  show coordinates fyi
            double onPeakValue = zeroBasedFractionAquireTimes.get(finalTimeSlot);
            DecimalFormat f = new DecimalFormat("#######0 seconds");

            JMenuItem menuItem = //
                    new JMenuItem("(" + f.format(onPeakValue) + ")");
            popup.add(menuItem);

            if (tripoliFraction.isIncluded()) {

                menuItem = new JMenuItem("EXCLUDE this fraction (all aquisitions).");
                menuItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent arg0) {

                        tripoliFraction.toggleAllDataExceptShaded(false);
                        fractionIncludedMap[finalTimeSlot] = false;
                        tripoliFraction.setShowVerticalLineAtThisIndex(-1);
                        try {
                            ((TripoliSessionFractionationCalculatorInterface) sampleSessionDataView).calculateSessionFitFunctionsForPrimaryStandard();
                            //((AbstractRawDataView) sampleSessionDataView).refreshPanel();
                        } catch (Exception e) {
                            System.out.println(">>>>>>>>>>>>trouble at standard exclude");
                        }
                        updateReportTable();
                        ((AbstractRawDataView) sampleSessionDataView).refreshPanel();
                    }
                });
                popup.add(menuItem);
            } else {
                menuItem = new JMenuItem("INCLUDE this fraction (all aquisitions).");
                menuItem.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent arg0) {

                        tripoliFraction.toggleAllDataExceptShaded(true);
                        fractionIncludedMap[finalTimeSlot] = true;
                        tripoliFraction.setShowVerticalLineAtThisIndex(-1);
                        try {
                            ((TripoliSessionFractionationCalculatorInterface) sampleSessionDataView).calculateSessionFitFunctionsForPrimaryStandard();
                            //((AbstractRawDataView) sampleSessionDataView).refreshPanel();
                        } catch (Exception e) {
                            System.out.println(">>>>>>>>>>>>trouble at standard include");
                        }
                        updateReportTable();
                        ((AbstractRawDataView) sampleSessionDataView).refreshPanel();
                    }
                });
                popup.add(menuItem);

            }

            // show the menu
            popup.show(evt.getComponent(), evt.getX() + 10, evt.getY() - 10);

        }
//        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        tripoliFraction.setShowVerticalLineAtThisIndex(-1);
        if (!(sampleSessionDataView == null)) {
            sampleSessionDataView.repaint();
        }
    }

    /**
     * @param paintColor the paintColor to set
     */
    @Override
    public void setPaintColor(Color paintColor) {
        this.paintColor = paintColor;
    }

    /**
     * @return the sessionForStandardDataModel
     */
    public DataModelFitFunctionInterface getFractionationAlphaDataModel() {
        return sessionForStandardDataModel;
    }

    @Override
    public DataModelInterface getDataModel() {
        return tripoliFractions.first().getRawRatioDataModelByName(//
                sessionForStandardDataModel.getRawRatioModelName());
    }

    /**
     * @return the sessionForStandardDataModel
     */
    public DataModelFitFunctionInterface getSessionForStandardDataModel() {
        return sessionForStandardDataModel;
    }

    /**
     *
     * @return
     */
    @Override
    public boolean amShowingUnknownFraction() {
        return false;
    }
}
