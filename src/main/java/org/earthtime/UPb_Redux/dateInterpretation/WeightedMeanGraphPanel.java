/*
 * WeightedMeanGraphPanel.java
 *
 * Created on June 9, 2008, 3:08 PM
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
package org.earthtime.UPb_Redux.dateInterpretation;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Random;
import java.util.Vector;
import javax.swing.JPanel;
import org.apache.batik.apps.rasterizer.SVGConverter;
import org.apache.batik.apps.rasterizer.SVGConverterException;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.apache.commons.math.special.Gamma;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.PlottingDetailsDisplayInterface;
import org.earthtime.UPb_Redux.samples.Sample;
import org.earthtime.UPb_Redux.user.SampleDateInterpretationGUIOptions;
import org.earthtime.UPb_Redux.utilities.comparators.IntuitiveStringComparator;
import org.earthtime.UPb_Redux.valueModels.SampleDateModel;
import org.earthtime.aliquots.ReduxAliquotInterface;
import org.earthtime.dataDictionaries.MSWDCoordinates;
import org.earthtime.fractions.ETFractionInterface;
import org.earthtime.samples.SampleInterface;
import org.earthtime.utilities.TicGeneratorForAxes;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

/**
 *
 * @author James F. Bowring
 */
public class WeightedMeanGraphPanel extends JPanel
        implements
        MouseListener,
        MouseMotionListener,
        PlottingDetailsDisplayInterface{

    // Class Variables
    private int graphWidth;
    private int graphHeight;
    private int topMargin = 1;//5;
    private int leftMargin = 15;
    // Instance Variables
    private SampleInterface sample;
    private Object[][] selectedSampleDateModels;
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;
    private double displayOffsetY;
    private double displayOffsetX;
    private double minT;
    private double maxT;
    private int zoomMinX;
    private int zoomMinY;
    private int zoomMaxX;
    private int zoomMaxY;
    private Cursor weightedMeanCursor;
    private Map<String, String> weightedMeanOptions;
//    private boolean inRandomMode;
    // PAN, ZOOM, EDIT
    private String imageMode;
    private final NumberFormat formatter2DecPlaces = new DecimalFormat("#####0.00");
    private final NumberFormat formatter1DecPlace = new DecimalFormat("#####0.0");

    /**
     * Creates a new instance of WeightedMeanGraphPanel
     *
     * @param sample
     */
    public WeightedMeanGraphPanel(SampleInterface sample) {
        super();

        this.sample = sample;

        setOpaque(true);

        setBackground(Color.white);

        graphWidth = 775;
        graphHeight = 600;

        selectedSampleDateModels = new Object[0][0];

        weightedMeanCursor = Cursor.getDefaultCursor();
        imageMode = "PAN";
//        setInRandomMode( false );
        weightedMeanOptions = new SampleDateInterpretationGUIOptions().getWeightedMeanOptions();

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

    private double mapX(double x, double minX, double rangeX, int widthG) {

        return (((x - minX) / rangeX) * (double) widthG) + (double) getLeftMargin();
    }

    private double mapY(double y, double maxY, double rangeY, int heightG) {

        return (((maxY - y) / rangeY) * (double) heightG) + (double) getTopMargin();
    }

    /**
     *
     * @param g2d
     */
    public void paint(Graphics2D g2d) {

        // setup painting parameters
        String fractionSortOrder = "name";  //random, weight, date
        if (getWeightedMeanOptions().containsKey("fractionSortOrder")) {
            fractionSortOrder = getWeightedMeanOptions().get("fractionSortOrder");
        }

        double rangeX = (getMaxX_Display() - getMinX_Display());
        double rangeY = (getMaxY_Display() - getMinY_Display());

        g2d.setClip(getLeftMargin(), getTopMargin(), getGraphWidth(), getGraphHeight());
        RenderingHints rh = g2d.getRenderingHints();
        rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHints(rh);

        // walk the sampleDateInterpretations and produce graphs
        g2d.setPaint(Color.BLACK);
        g2d.setStroke(new BasicStroke(2.0f));
        g2d.setFont(new Font(
                "SansSerif",
                Font.BOLD,
                10));

        double barWidth = 15.0;
        double barGap = 10.0;
        double startSamX = 10.0;
        double saveStartSamX = 0.0;
        double samSpace = 3.0;

        for (int i = 0; i < selectedSampleDateModels.length; i++) {
            for (int j = 1; j < 9; j++) {
                if (selectedSampleDateModels[i][j] instanceof SampleDateModel) {

                    final SampleDateModel SAM = ((SampleDateModel) selectedSampleDateModels[i][j]);
                    double wMean = SAM.getValue().movePointLeft(6).doubleValue();
                    double wMeanOneSigma = SAM.getOneSigmaAbs().movePointLeft(6).doubleValue();

                    Path2D mean = new Path2D.Double(Path2D.WIND_NON_ZERO);

                    // july 2008
                    // modified to show de-selected fractions as gray
                    // this means a new special list of fractionIDs is created fromall non-rejected fractions
                    // and each instance is tested for being included
                    // should eventually refactor
                    Vector<String> allFIDs = new Vector<>();
                    for (String f : ((ReduxAliquotInterface) SAM.getAliquot()).getAliquotFractionIDs()) {
                        // test added for Sample-based wm
                        if (SAM.fractionDateIsPositive(//
                                ((ReduxAliquotInterface) SAM.getAliquot()).getAliquotFractionByName(f))) {
                            allFIDs.add(f);
                        }
                    }

                    final int iFinal = i;
                    if (fractionSortOrder.equalsIgnoreCase("weight")) {
                        Collections.sort(allFIDs, new Comparator<String>() {

                            public int compare(String fID1, String fID2) {
                                double invertOneSigmaF1 = //
                                        1.0 //
                                        / ((ReduxAliquotInterface) selectedSampleDateModels[iFinal][0])//
                                        .getAliquotFractionByName(fID1)//
                                        .getRadiogenicIsotopeDateByName(SAM.getDateName())//
                                        .getOneSigmaAbs().movePointLeft(6).doubleValue();
                                double invertOneSigmaF2 = //
                                        1.0 //
                                        / ((ReduxAliquotInterface) selectedSampleDateModels[iFinal][0])//
                                        .getAliquotFractionByName(fID2)//
                                        .getRadiogenicIsotopeDateByName(SAM.getDateName())//
                                        .getOneSigmaAbs().movePointLeft(6).doubleValue();

                                return Double.compare(invertOneSigmaF2, invertOneSigmaF1);
                            }
                        });
                    } else if (fractionSortOrder.equalsIgnoreCase("date")) {
                        Collections.sort(allFIDs, new Comparator<String>() {

                            public int compare(String fID1, String fID2) {
                                double dateF1 = //
                                        ((ReduxAliquotInterface) selectedSampleDateModels[iFinal][0])//
                                        .getAliquotFractionByName(fID1)//
                                        .getRadiogenicIsotopeDateByName(SAM.getDateName())//
                                        .getValue().doubleValue();
                                double dateF2 = //
                                        ((ReduxAliquotInterface) selectedSampleDateModels[iFinal][0])//
                                        .getAliquotFractionByName(fID2)//
                                        .getRadiogenicIsotopeDateByName(SAM.getDateName())//
                                        .getValue().doubleValue();

                                return Double.compare(dateF1, dateF2);
                            }
                        });
                    } else if ( /* ! isInRandomMode() &&*/fractionSortOrder.equalsIgnoreCase("random")) {
                        Collections.shuffle(allFIDs, new Random());
                    } else if (fractionSortOrder.equalsIgnoreCase("name")) {
                        // default to alphabetic by name
                        //Collections.sort(allFIDs);
                        // april 2010 give same lexigraphic ordering that UPbFractions get
                        Collections.sort(allFIDs, new IntuitiveStringComparator<String>());
                    } else {
                        // do nothing
                    }

                    double actualWidthX = (allFIDs.size()) * (barWidth + barGap);//; + barGap;

                    // plot 2-sigma of mean
                    mean.moveTo(
                            (float) mapX(startSamX, getMinX_Display(), rangeX, graphWidth),
                            (float) mapY(wMean + 2.0 * wMeanOneSigma, getMaxY_Display(), rangeY, graphHeight));
                    mean.lineTo(
                            (float) mapX(startSamX + actualWidthX, getMinX_Display(), rangeX, graphWidth),
                            (float) mapY(wMean + 2.0 * wMeanOneSigma, getMaxY_Display(), rangeY, graphHeight));
                    mean.lineTo(
                            (float) mapX(startSamX + actualWidthX, getMinX_Display(), rangeX, graphWidth),
                            (float) mapY(wMean - 2.0 * wMeanOneSigma, getMaxY_Display(), rangeY, graphHeight));
                    mean.lineTo(
                            (float) mapX(startSamX, getMinX_Display(), rangeX, graphWidth),
                            (float) mapY(wMean - 2.0 * wMeanOneSigma, getMaxY_Display(), rangeY, graphHeight));
                    mean.closePath();

                    g2d.setColor(ReduxConstants.mySampleYellowColor);
                    g2d.fill(mean);
                    g2d.setPaint(Color.BLACK);

                    // plot 1-sigma of mean
                    mean.reset();
                    mean.moveTo(
                            (float) mapX(startSamX, getMinX_Display(), rangeX, graphWidth),
                            (float) mapY(wMean + wMeanOneSigma, getMaxY_Display(), rangeY, graphHeight));
                    mean.lineTo(
                            (float) mapX(startSamX + actualWidthX, getMinX_Display(), rangeX, graphWidth),
                            (float) mapY(wMean + wMeanOneSigma, getMaxY_Display(), rangeY, graphHeight));
                    mean.lineTo(
                            (float) mapX(startSamX + actualWidthX, getMinX_Display(), rangeX, graphWidth),
                            (float) mapY(wMean - wMeanOneSigma, getMaxY_Display(), rangeY, graphHeight));
                    mean.lineTo(
                            (float) mapX(startSamX, getMinX_Display(), rangeX, graphWidth),
                            (float) mapY(wMean - wMeanOneSigma, getMaxY_Display(), rangeY, graphHeight));
                    mean.closePath();

                    g2d.setColor(ReduxConstants.ColorOfRedux);
                    g2d.fill(mean);
                    g2d.setPaint(Color.BLACK);

                    // plot mean
                    mean.reset();
                    mean.moveTo(
                            (float) mapX(startSamX, getMinX_Display(), rangeX, graphWidth),
                            (float) mapY(wMean, getMaxY_Display(), rangeY, graphHeight));
                    mean.lineTo(
                            (float) mapX(startSamX + actualWidthX, getMinX_Display(), rangeX, graphWidth),
                            (float) mapY(wMean, getMaxY_Display(), rangeY, graphHeight));
                    g2d.setStroke(new BasicStroke(1.0f));
                    g2d.draw(mean);
                    g2d.setStroke(new BasicStroke(2.0f));

                    saveStartSamX = startSamX;

                    // plot fraction bars
                    double minPoint = 5000.0;
                    double maxWeight = 0.0;
                    double totalWeight = 0.0;

                    int barNum = 0;

                    for (String fID : allFIDs) {
                        // the dateModel has an associated aliquot, but in sample mode, it is a
                        // standin aliquot for the sample.  to get the aliquot number for
                        // use in coloring fractions, we need to query the fraction itself
                        String aliquotName = sample.getAliquotNameByFractionID(fID);

                        Color includedFillColor = new Color(0, 0, 0);
                        if (sample.getSampleDateInterpretationGUISettings().getAliquotOptions().get(aliquotName).containsKey("includedFillColor")) {
                            String[] temp = //
                                    sample.getSampleDateInterpretationGUISettings().getAliquotOptions().get(aliquotName).get("includedFillColor").split(",");
                            includedFillColor = buildRGBColor(temp);
                        }

                        ETFractionInterface f = ((ReduxAliquotInterface) selectedSampleDateModels[i][0]).getAliquotFractionByName(fID);

                        double date = f.
                                getRadiogenicIsotopeDateByName(SAM.getDateName()).getValue().movePointLeft(6).doubleValue();
                        double twoSigma = f.
                                getRadiogenicIsotopeDateByName(SAM.getDateName()).getTwoSigmaAbs().movePointLeft(6).doubleValue();

                        if ((date - twoSigma) < minPoint) {
                            minPoint = (date - twoSigma);
                        }

                        double invertedOneSigma = //
                                1.0 //
                                / f.
                                getRadiogenicIsotopeDateByName(SAM.getDateName()).getOneSigmaAbs().movePointLeft(6).doubleValue();

                        if (invertedOneSigma > maxWeight) {
                            maxWeight = invertedOneSigma;
                        }

                        Path2D bar = new Path2D.Double(Path2D.WIND_NON_ZERO);
                        bar.moveTo(
                                (float) mapX(saveStartSamX + ((barGap / 2.0) + barNum * (barWidth + barGap)), getMinX_Display(), rangeX, graphWidth),
                                (float) mapY(date + twoSigma, getMaxY_Display(), rangeY, graphHeight));
                        bar.lineTo(
                                (float) mapX(saveStartSamX + ((barGap / 2.0) + barNum * (barWidth + barGap)) + barWidth, getMinX_Display(), rangeX, graphWidth),
                                (float) mapY(date + twoSigma, getMaxY_Display(), rangeY, graphHeight));
                        bar.lineTo(
                                (float) mapX(saveStartSamX + ((barGap / 2.0) + barNum * (barWidth + barGap)) + barWidth, getMinX_Display(), rangeX, graphWidth),
                                (float) mapY(date - twoSigma, getMaxY_Display(), rangeY, graphHeight));
                        bar.lineTo(
                                (float) mapX(saveStartSamX + ((barGap / 2.0) + barNum * (barWidth + barGap)), getMinX_Display(), rangeX, graphWidth),
                                (float) mapY(date - twoSigma, getMaxY_Display(), rangeY, graphHeight));
                        bar.closePath();

                        Composite originalComposite = g2d.getComposite();

                        if (SAM.getIncludedFractionIDsVector().contains(fID)) {
                            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
                            totalWeight += Math.pow(invertedOneSigma, 2.0);

                            // april 2014 experiment
                            if (f.getRgbColor() != 0) {
                                includedFillColor = new Color(f.getRgbColor());
                            }

                        } else {
                            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
                        }

                        g2d.setPaint(includedFillColor);

                        g2d.draw(bar);
                        //restore composite
                        g2d.setComposite(originalComposite);

                        g2d.setColor(Color.black);

                        // label fraction at top
                        g2d.rotate(-Math.PI / 4.0,
                                (float) mapX(saveStartSamX + ((barGap / 2.0) + barNum * (barWidth + barGap)), getMinX_Display(), rangeX, graphWidth),
                                (float) mapY(date + twoSigma, getMaxY_Display(), rangeY, graphHeight));

                        g2d.drawString(
                                ((ReduxAliquotInterface) selectedSampleDateModels[i][0]).getAliquotFractionByName(fID).getFractionID(),
                                (float) mapX(saveStartSamX + ((barGap / 2.0) + barNum * (barWidth + barGap)), getMinX_Display(), rangeX, graphWidth) + 15,
                                (float) mapY(date + twoSigma, getMaxY_Display(), rangeY, graphHeight));

                        g2d.rotate(Math.PI / 4.0,
                                (float) mapX(saveStartSamX + ((barGap / 2.0) + barNum * (barWidth + barGap)), getMinX_Display(), rangeX, graphWidth),
                                (float) mapY(date + twoSigma, getMaxY_Display(), rangeY, graphHeight));

                        barNum++;
                        // startSamX += 2 * barWidth;
                        startSamX += barWidth + barGap;
                    }

                    // display three info boxes below weighted means
                    // each tic is the height of one calculated y-axis tic
                    // determine the y axis tic
                    double minYtic = Math.ceil(getMinY_Display() * 100) / 100;
                    double maxYtic = Math.floor(getMaxY_Display() * 100) / 100;
                    double deltay = Math.rint((maxYtic - minYtic) * 10 + 0.5);
                    double yTic = deltay / 100;

                    double yTopSummary = minPoint - yTic / 2.0;// wMeanOneSigma;
                    //double specialYTic = yTic;
                    double yTopWeights = yTopSummary - yTic * 1.1;
                    double yTopMSWD_PDF = yTopWeights - yTic * 1.1;

                    // summary box
                    Path2D box = new Path2D.Double(Path2D.WIND_NON_ZERO);
                    box.moveTo(
                            (float) mapX(saveStartSamX, getMinX_Display(), rangeX, graphWidth),
                            (float) mapY(yTopSummary, getMaxY_Display(), rangeY, graphHeight));
                    box.lineTo(
                            (float) mapX(saveStartSamX + actualWidthX, getMinX_Display(), rangeX, graphWidth),
                            (float) mapY(yTopSummary, getMaxY_Display(), rangeY, graphHeight));
                    box.lineTo(
                            (float) mapX(saveStartSamX + actualWidthX, getMinX_Display(), rangeX, graphWidth),
                            (float) mapY(yTopSummary - yTic, getMaxY_Display(), rangeY, graphHeight));
                    box.lineTo(
                            (float) mapX(saveStartSamX, getMinX_Display(), rangeX, graphWidth),
                            (float) mapY(yTopSummary - yTic, getMaxY_Display(), rangeY, graphHeight));
                    box.closePath();

                    g2d.setStroke(new BasicStroke(1.5f));
                    g2d.draw(box);

                    // Info Box
                    g2d.drawString(//
                            SAM.getAliquot().getAliquotName(),
                            (float) mapX(saveStartSamX, getMinX_Display(), rangeX, graphWidth) + 4f,
                            (float) mapY(yTopSummary, getMaxY_Display(), rangeY, graphHeight) + 13f);
                    g2d.drawString(//
                            SAM.getName(),
                            (float) mapX(saveStartSamX, getMinX_Display(), rangeX, graphWidth) + 4f,
                            (float) mapY(yTopSummary, getMaxY_Display(), rangeY, graphHeight) + 25f);
                    g2d.drawString(//
                            SAM.FormatValueAndTwoSigmaABSThreeWaysForPublication(6, 2),
                            (float) mapX(saveStartSamX, getMinX_Display(), rangeX, graphWidth) + 4f,
                            (float) mapY(yTopSummary, getMaxY_Display(), rangeY, graphHeight) + 36f);
                    g2d.drawString(//
                            SAM.ShowCustomMSWDwithN(),
                            (float) mapX(saveStartSamX, getMinX_Display(), rangeX, graphWidth) + 4f,
                            (float) mapY(yTopSummary, getMaxY_Display(), rangeY, graphHeight) + 48f);

                    // weights box
                    box.reset();
                    box.moveTo(
                            (float) mapX(saveStartSamX, getMinX_Display(), rangeX, graphWidth),
                            (float) mapY(yTopWeights, getMaxY_Display(), rangeY, graphHeight));
                    box.lineTo(
                            (float) mapX(saveStartSamX + actualWidthX, getMinX_Display(), rangeX, graphWidth),
                            (float) mapY(yTopWeights, getMaxY_Display(), rangeY, graphHeight));
                    box.lineTo(
                            (float) mapX(saveStartSamX + actualWidthX, getMinX_Display(), rangeX, graphWidth),
                            (float) mapY(yTopWeights - yTic, getMaxY_Display(), rangeY, graphHeight));
                    box.lineTo(
                            (float) mapX(saveStartSamX, getMinX_Display(), rangeX, graphWidth),
                            (float) mapY(yTopWeights - yTic, getMaxY_Display(), rangeY, graphHeight));
                    box.closePath();

                    g2d.setStroke(new BasicStroke(1.5f));
                    g2d.draw(box);

                    // plot fraction weights
                    double artificialXRange = allFIDs.size();
                    double count = 0;
                    //double weightWidth = Math.min(3.0 * barWidth, (yTic / rangeY * graphHeight)) - 15;//yTic;//barWidth * 2.0;
                    double weightWidth = (barWidth + barGap) * 0.9;

                    for (String fID : allFIDs) {
                        // the dateModel has an associated aliquot, but in sample mode, it is a
                        // standin aliquot for the sample.  to get the aliquot number for
                        // use in coloring fractions, we need to query the fraction itself
                        String aliquotName = sample.getAliquotNameByFractionID(fID);

                        ETFractionInterface f = ((ReduxAliquotInterface) selectedSampleDateModels[i][0]).getAliquotFractionByName(fID);

                        Color includedFillColor = new Color(0, 0, 0);
                        if (sample.getSampleDateInterpretationGUISettings().getAliquotOptions().get(aliquotName).containsKey("includedFillColor")) {
                            String[] temp = //
                                    sample.getSampleDateInterpretationGUISettings().getAliquotOptions().get(aliquotName).get("includedFillColor").split(",");
                            includedFillColor = buildRGBColor(temp);
                        }

                        double invertOneSigma = //
                                1.0 //
                                / ((ReduxAliquotInterface) selectedSampleDateModels[i][0]).getAliquotFractionByName(fID)//
                                .getRadiogenicIsotopeDateByName(SAM.getDateName()).getOneSigmaAbs().movePointLeft(6).doubleValue();

                        Path2D weight = new Path2D.Double(Path2D.WIND_NON_ZERO);
                        weight.moveTo(
                                (float) mapX(saveStartSamX + (count + 0.5) / artificialXRange * actualWidthX, getMinX_Display(), rangeX, graphWidth) //
                                - (float) (invertOneSigma / maxWeight / 2.0 * weightWidth),
                                (float) mapY(yTopWeights - (yTic / 2.0), getMaxY_Display(), rangeY, graphHeight) //
                                + (float) (invertOneSigma / maxWeight / 2.0 * weightWidth) - 5f);

                        weight.lineTo(
                                (float) mapX(saveStartSamX + (count + 0.5) / artificialXRange * actualWidthX, getMinX_Display(), rangeX, graphWidth) //
                                + (float) (invertOneSigma / maxWeight / 2.0 * weightWidth),
                                (float) mapY(yTopWeights - (yTic / 2.0), getMaxY_Display(), rangeY, graphHeight) //
                                + (float) (invertOneSigma / maxWeight / 2.0 * weightWidth) - 5f);

                        weight.lineTo(
                                (float) mapX(saveStartSamX + (count + 0.5) / artificialXRange * actualWidthX, getMinX_Display(), rangeX, graphWidth) //
                                + (float) (invertOneSigma / maxWeight / 2.0 * weightWidth),
                                (float) mapY(yTopWeights - (yTic / 2.0), getMaxY_Display(), rangeY, graphHeight) //
                                - (float) (invertOneSigma / maxWeight / 2.0 * weightWidth) - 5f);

                        weight.lineTo(
                                (float) mapX(saveStartSamX + (count + 0.5) / artificialXRange * actualWidthX, getMinX_Display(), rangeX, graphWidth) //
                                - (float) (invertOneSigma / maxWeight / 2.0 * weightWidth),
                                (float) mapY(yTopWeights - (yTic / 2.0), getMaxY_Display(), rangeY, graphHeight) //
                                - (float) (invertOneSigma / maxWeight / 2.0 * weightWidth) - 5f);

                        weight.closePath();

                        g2d.setStroke(new BasicStroke(2.5f));

                        // test for included or not == black or gray
                        String weightPerCent = "   0";//0.0%";

//                        g2d.setPaint(includedFillColor);
                        Composite originalComposite = g2d.getComposite();

                        if (SAM.getIncludedFractionIDsVector().contains(fID)) {
                            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
                            weightPerCent = formatter1DecPlace.format(Math.pow(invertOneSigma, 2.0) / totalWeight * 100.0);// + "%";

                            // april 2014 experiment
                            if (f.getRgbColor() != 0) {
                                includedFillColor = new Color(f.getRgbColor());
                            }
                        } else {
                            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
                        }

                        g2d.setPaint(includedFillColor);

                        g2d.fill(weight);
                        //restore composite
                        g2d.setComposite(originalComposite);

                        // write percent of total weight
                        g2d.drawString(
                                weightPerCent,
                                (float) mapX(saveStartSamX + (count + 0.5) / artificialXRange * actualWidthX, getMinX_Display(), rangeX, graphWidth) //
                                - (float) (invertOneSigma / maxWeight / 2.0 * weightWidth),
                                (float) mapY(yTopWeights - yTic, getMaxY_Display(), rangeY, graphHeight) - 5f);
                        g2d.setColor(Color.black);

                        count += 1.0;

                    }

                    // double box height for graph
                    yTic *= 2.0;

                    // plot MSWD_PDF
                    // store  function x,y values
                    Vector<Double> xVals = new Vector<Double>();
                    Vector<Double> yVals = new Vector<Double>();

                    double f = SAM.getIncludedFractionIDsVector().size() - 1;
                    if (f > 1.0) {
                        g2d.setStroke(new BasicStroke(1.0f));

                        double yRange = MSWDCoordinates.valuesByPointCount[(int) f][5] * 1.03; // alitle air at the top of curve
                        double xStart = MSWDCoordinates.valuesByPointCount[(int) f][1];
                        double xRange = MSWDCoordinates.valuesByPointCount[(int) f][4] - xStart;
                        double xStep = 0.005;

                        Path2D MSWD_PDF = new Path2D.Double(Path2D.WIND_NON_ZERO);
                        Path2D MSWD_right = new Path2D.Double(Path2D.WIND_NON_ZERO);

                        // start at lower left corner of box  (may or may not be 0,0 )
                        MSWD_PDF.moveTo(//
                                (float) mapX((double) saveStartSamX, getMinX_Display(), rangeX, graphWidth),
                                (float) mapY(yTopMSWD_PDF - yTic, getMaxY_Display(), rangeY, graphHeight));

                        // setup MSWD to paint last
                        Path2D MSWD = null;

                        // calculate function values
                        for (double x = xStart; x < xRange; x += xStep) {
                            xVals.add((((x - xStart) / xRange) * actualWidthX) + (double) saveStartSamX);

                            double y = //
                                    Math.pow(2, -1.0 * f / 2.0)//
                                    * Math.exp(-1.0 * f * x / 2.0)//
                                    * Math.pow(f, f / 2.0)//
                                    * Math.pow(x, (-1.0 + f / 2.0))//
                                    / Math.exp(Gamma.logGamma(f / 2.0));

                            yVals.add(((y / yRange) * yTic) + yTopMSWD_PDF - yTic);

                            MSWD_PDF.lineTo(//
                                    (float) mapX(xVals.lastElement(), getMinX_Display(), rangeX, graphWidth),
                                    (float) mapY(yVals.lastElement(), getMaxY_Display(), rangeY, graphHeight));

                            // test for location of left RED zone
                            if ((MSWDCoordinates.valuesByPointCount[(int) f][2] >= x)
                                    && (MSWDCoordinates.valuesByPointCount[(int) f][2] < (x + xStep))) {

                                double leftX = MSWDCoordinates.valuesByPointCount[(int) f][2];
                                xVals.add((((leftX - xStart) / xRange) * actualWidthX) + (double) saveStartSamX);

                                double leftY = //
                                        Math.pow(2, -1.0 * f / 2.0)//
                                        * Math.exp(-1.0 * f * leftX / 2.0)//
                                        * Math.pow(f, f / 2.0)//
                                        * Math.pow(leftX, (-1.0 + f / 2.0))//
                                        / Math.exp(Gamma.logGamma(f / 2.0));
                                yVals.add(((leftY / yRange) * yTic) + yTopMSWD_PDF - yTic);

                                MSWD_PDF.lineTo(//
                                        (float) mapX(xVals.lastElement(), getMinX_Display(), rangeX, graphWidth),
                                        (float) mapY(yVals.lastElement(), getMaxY_Display(), rangeY, graphHeight));

                                Path2D ciLower = new Path2D.Double(Path2D.WIND_NON_ZERO);
                                ciLower.append(MSWD_PDF.getPathIterator(new AffineTransform()), true);

                                ciLower.lineTo(//
                                        (float) mapX(xVals.lastElement(), getMinX_Display(), rangeX, graphWidth),
                                        (float) mapY(yTopMSWD_PDF - yTic, getMaxY_Display(), rangeY, graphHeight));
                                ciLower.closePath();
                                g2d.setColor(Color.RED);
                                g2d.fill(ciLower);

                                // draw right hand border line to compensate for a bug in the filler
                                Line2D right = new Line2D.Double(//
                                        mapX(xVals.lastElement(), getMinX_Display(), rangeX, graphWidth),
                                        mapY(yVals.lastElement(), getMaxY_Display(), rangeY, graphHeight),
                                        mapX(xVals.lastElement(), getMinX_Display(), rangeX, graphWidth),
                                        mapY(yTopMSWD_PDF - yTic, getMaxY_Display(), rangeY, graphHeight));
                                g2d.setStroke(new BasicStroke(0.5f));
                                g2d.draw(right);
                                g2d.setStroke(new BasicStroke(1.0f));

                                g2d.setColor(Color.BLACK);

                                System.out.println("Left Red = (" + leftX + ", " + leftY + ")");
                            }

                            // test for location of right RED zone
                            if ((MSWDCoordinates.valuesByPointCount[(int) f][3] >= x)
                                    && (MSWDCoordinates.valuesByPointCount[(int) f][3] < (x + xStep))) {

                                double rightX = MSWDCoordinates.valuesByPointCount[(int) f][3];
                                xVals.add((((rightX - xStart) / xRange) * actualWidthX) + (double) saveStartSamX);

                                double rightY = //
                                        Math.pow(2, -1.0 * f / 2.0)//
                                        * Math.exp(-1.0 * f * rightX / 2.0)//
                                        * Math.pow(f, f / 2.0)//
                                        * Math.pow(rightX, (-1.0 + f / 2.0))//
                                        / Math.exp(Gamma.logGamma(f / 2.0));
                                yVals.add(((rightY / yRange) * yTic) + yTopMSWD_PDF - yTic);

                                MSWD_PDF.lineTo(//
                                        (float) mapX(xVals.lastElement(), getMinX_Display(), rangeX, graphWidth),
                                        (float) mapY(yVals.lastElement(), getMaxY_Display(), rangeY, graphHeight));

                                // here the strategy is to draw the curve and then reset it to record the remainder
                                g2d.setStroke(new BasicStroke(1.0f));
                                g2d.draw(MSWD_PDF);
                                MSWD_PDF = new Path2D.Double(Path2D.WIND_NON_ZERO);
                                MSWD_PDF.moveTo(//
                                        (float) mapX(xVals.lastElement(), getMinX_Display(), rangeX, graphWidth),
                                        (float) mapY(yVals.lastElement(), getMaxY_Display(), rangeY, graphHeight));

                                MSWD_right.moveTo(//
                                        (float) mapX(xVals.lastElement(), getMinX_Display(), rangeX, graphWidth),
                                        (float) mapY(yTopMSWD_PDF - yTic, getMaxY_Display(), rangeY, graphHeight));
                                MSWD_right.lineTo(//
                                        (float) mapX(xVals.lastElement(), getMinX_Display(), rangeX, graphWidth),
                                        (float) mapY(yVals.lastElement(), getMaxY_Display(), rangeY, graphHeight));

                                System.out.println("Right Red = (" + rightX + ", " + rightY + ")");

                            }

                            // test for location of MSWD AND paint last
                            if ((SAM.getMeanSquaredWeightedDeviation().doubleValue() >= x)
                                    && (SAM.getMeanSquaredWeightedDeviation().doubleValue() < (x + xStep))) {
                                MSWD = new Path2D.Double(Path2D.WIND_NON_ZERO);
                                MSWD.moveTo(//
                                        (float) mapX(xVals.lastElement(), getMinX_Display(), rangeX, graphWidth),
                                        (float) mapY(yTopMSWD_PDF - yTic, getMaxY_Display(), rangeY, graphHeight));
                                MSWD.lineTo(//
                                        (float) mapX(xVals.lastElement(), getMinX_Display(), rangeX, graphWidth),
                                        (float) mapY(yVals.lastElement(), getMaxY_Display(), rangeY, graphHeight));

                            }
                        }
                        g2d.setStroke(new BasicStroke(1.0f));
                        // merge with border of right RED and fill
                        MSWD_right.append(MSWD_PDF.getPathIterator(new AffineTransform()), true);
                        g2d.setColor(Color.RED);
                        g2d.fill(MSWD_right);
                        g2d.setColor(Color.BLACK);
                        // draw the remaining curves
                        g2d.draw(MSWD_PDF);
                        // MSWD may be off the graph and hence not exist
                        try {
                            g2d.draw(MSWD);
                        } catch (Exception e) {
                        }

                        // label 95% conf interval and MSWD
                        g2d.drawString(//
                                "95% CI: (" + formatter2DecPlaces.format(MSWDCoordinates.valuesByPointCount[(int) f][2]) + ", " + formatter2DecPlaces.format(MSWDCoordinates.valuesByPointCount[(int) f][3]) + ")",
                                (float) mapX(saveStartSamX + (actualWidthX / 2.0), getMinX_Display(), rangeX, graphWidth) - 30f,
                                (float) mapY(yTopMSWD_PDF, getMaxY_Display(), rangeY, graphHeight) + 15f);

                        // determine if MSWD is out of range
                        String mswdAlert = "";
                        if (SAM.getMeanSquaredWeightedDeviation().doubleValue() > MSWDCoordinates.valuesByPointCount[(int) f][4]) {
                            mswdAlert = "\n !Out of Range!";
                        }
                        g2d.drawString(//
                                "MSWD = " + formatter2DecPlaces.format(SAM.getMeanSquaredWeightedDeviation().doubleValue()) + ", n = " + (int) (f + 1) + mswdAlert,
                                (float) mapX(saveStartSamX + (actualWidthX / 2.0), getMinX_Display(), rangeX, graphWidth) - 15f,
                                (float) mapY(yTopMSWD_PDF, getMaxY_Display(), rangeY, graphHeight) + 30f);

                    } else {
                        g2d.drawString(
                                "need more data...",
                                (float) mapX((double) saveStartSamX, getMinX_Display(), rangeX, graphWidth) + 4f,
                                (float) mapY(yTopMSWD_PDF - yTic, getMaxY_Display(), rangeY, graphHeight) - 10f);
                    }

                    // MSWD_PDF box
                    box.reset();
                    box.moveTo(
                            (float) mapX(saveStartSamX, getMinX_Display(), rangeX, graphWidth),
                            (float) mapY(yTopMSWD_PDF, getMaxY_Display(), rangeY, graphHeight));
                    box.lineTo(
                            (float) mapX(saveStartSamX + actualWidthX, getMinX_Display(), rangeX, graphWidth),
                            (float) mapY(yTopMSWD_PDF, getMaxY_Display(), rangeY, graphHeight));
                    box.lineTo(
                            (float) mapX(saveStartSamX + actualWidthX, getMinX_Display(), rangeX, graphWidth),
                            (float) mapY(yTopMSWD_PDF - yTic, getMaxY_Display(), rangeY, graphHeight));
                    box.lineTo(
                            (float) mapX(saveStartSamX, getMinX_Display(), rangeX, graphWidth),
                            (float) mapY(yTopMSWD_PDF - yTic, getMaxY_Display(), rangeY, graphHeight));
                    box.closePath();

                    g2d.setStroke(new BasicStroke(1.5f));
                    g2d.draw(box);

                    // MSWD_PDF x-axis tics
                    if (f > 1.0) {
                        g2d.setStroke(new BasicStroke(1.0f));
                        double xStart = (MSWDCoordinates.valuesByPointCount[(int) f][1] <= 0.5) ? 0.5 : 1.0;
                        double xRange = MSWDCoordinates.valuesByPointCount[(int) f][4] - MSWDCoordinates.valuesByPointCount[(int) f][1];
                        double xStep = 0.5;

                        for (double x = xStart; x < xRange; x += xStep) {
                            double xPlot = (((x - MSWDCoordinates.valuesByPointCount[(int) f][1]) / xRange) * actualWidthX) + (double) saveStartSamX;
                            Line2D line = new Line2D.Double(
                                    mapX(xPlot, getMinX_Display(), rangeX, graphWidth),
                                    mapY(yTopMSWD_PDF - yTic, getMaxY_Display(), rangeY, graphHeight),
                                    mapX(xPlot, getMinX_Display(), rangeX, graphWidth),
                                    mapY(yTopMSWD_PDF - yTic, getMaxY_Display(), rangeY, graphHeight) + 7);
                            g2d.draw(line);

                            g2d.rotate(-Math.PI / 2.0,
                                    (float) mapX(xPlot, getMinX_Display(), rangeX, graphWidth),
                                    (float) mapY(yTopMSWD_PDF - yTic, getMaxY_Display(), rangeY, graphHeight));
                            g2d.drawString(
                                    formatter1DecPlace.format(x),
                                    (float) mapX(xPlot, getMinX_Display(), rangeX, graphWidth) - 30f,
                                    (float) mapY(yTopMSWD_PDF - yTic, getMaxY_Display(), rangeY, graphHeight) + 5f);
                            g2d.rotate(Math.PI / 2.0,
                                    (float) mapX(xPlot, getMinX_Display(), rangeX, graphWidth),
                                    (float) mapY(yTopMSWD_PDF - yTic, getMaxY_Display(), rangeY, graphHeight));
                        }
                    }

                    // set counters
                    barNum += samSpace;
                    startSamX += 2 * samSpace * barWidth;
                }
            }
        }
//        // prevents re-randomization
//        setInRandomMode( true );

        drawAxesAndTicks(g2d, rangeX, rangeY);

        // draw zoom box if in use
        if ((Math.abs(zoomMaxX - zoomMinX) * Math.abs(zoomMinY - zoomMaxY)) > 0.0) {
            g2d.setStroke(new BasicStroke(2.0f));
            g2d.setColor(Color.red);
            g2d.drawRect(//
                    Math.min(zoomMinX, zoomMaxX),
                    Math.min(zoomMaxY, zoomMinY),
                    Math.abs(zoomMaxX - zoomMinX),
                    Math.abs(zoomMinY - zoomMaxY));
        }
    }

    private Color buildRGBColor(String[] rgbComponents) {
        return new Color(
                (int) Integer.parseInt(rgbComponents[0].trim()),
                (int) Integer.parseInt(rgbComponents[1].trim()),
                (int) Integer.parseInt(rgbComponents[2].trim()));
    }

    private void drawAxesAndTicks(Graphics2D g2d, double rangeX, double rangeY) {

        // oct 2014 new tic logic
        // reset the clip bounds to paint axis and numbers
        g2d.setClip(0, 0, getWidth(), getHeight());

        g2d.setFont(new Font("Monospaced", Font.BOLD, 14));
        g2d.setPaint(Color.BLACK);
        g2d.setStroke(new BasicStroke(2.0f));

        // determine the axis ticks
        BigDecimal[] tics = TicGeneratorForAxes.generateTics(getMinY_Display(), getMaxY_Display(), 12);
        // trap for bad plot
        if (tics.length <= 1) {
            tics = new BigDecimal[0];
        }
        double minXDisplay = 0.0;
        int yAxisTicWidth = 8;
        int yTicLabelFrequency = 2;
        int labeledTicCountYAxis = 0;

        g2d.setPaint(Color.black);
        for (int i = 0; i < tics.length; i++) {

            double y = tics[i].doubleValue();

            if ((y > getMinY_Display()) // dont print across mappedX axis
                    && (y < getMaxY_Display())) // dont print across top border
            {
                try {
                    Shape ticMark = new Line2D.Double( //
                            mapX(getMinX_Display(), getMinX_Display(), rangeX, graphWidth),
                            mapY(y, getMaxY_Display(), rangeY, graphHeight),
                            mapX(getMinX_Display(), getMinX_Display(), rangeX, graphWidth) + 7,
                            mapY(y, getMaxY_Display(), rangeY, graphHeight));
                    g2d.draw(ticMark);

                    String intString = "00000" + tics[i].toPlainString().replace(".", "");
                    int lastPlace = Integer.parseInt(intString.substring(intString.length() - 4));

                    if (lastPlace % yTicLabelFrequency == 0) {
                        if (labeledTicCountYAxis % yTicLabelFrequency == 0) {

                            TextLayout mLayout = //
                                    new TextLayout(
                                            tics[i].toPlainString(), g2d.getFont(), g2d.getFontRenderContext());

                            Rectangle2D bounds = mLayout.getBounds();

                            //if (isyAxisHorizontalTicLabels()) {
//                            g2d.drawString(tics[i].toPlainString(),//
//                                    (float) mapX(getMinX_Display(), getMinX_Display(), rangeX, graphWidth) - 4f,
//                                    (float) mapY(y, getMaxY_Display(), rangeY, graphHeight) + 30f);
//                            } else {
                            float yLabelCenterOffset = (float) mLayout.getBounds().getWidth() / 2f;

                            g2d.rotate(
                                    -Math.PI / 2.0,
                                    (float) mapX(getMinX_Display(), getMinX_Display(), rangeX, graphWidth) - 4f,
                                    (float) mapY(y, getMaxY_Display(), rangeY, graphHeight) + yLabelCenterOffset);
                            g2d.drawString(
                                    tics[i].toPlainString(),
                                    (float) mapX(getMinX_Display(), getMinX_Display(), rangeX, graphWidth) - 4f,
                                    (float) mapY(y, getMaxY_Display(), rangeY, graphHeight) + yLabelCenterOffset);
                            g2d.rotate(
                                    Math.PI / 2.0,
                                    (float) mapX(getMinX_Display(), getMinX_Display(), rangeX, graphWidth) - 4f,
                                    (float) mapY(y, getMaxY_Display(), rangeY, graphHeight) + yLabelCenterOffset);
                        }

                        labeledTicCountYAxis++;
                    } else {

                        if (labeledTicCountYAxis > 0) {
                            labeledTicCountYAxis++;
                        }
                    }
                } catch (Exception e) {
                }
            }
        }

////        // reset the clip bounds to paint axis and numbers
////        g2d.setClip(0, 0, getWidth(), getHeight());
////
////        g2d.setFont(new Font("Monospaced", Font.BOLD, 14));
////        g2d.setPaint(Color.BLACK);
////        g2d.setStroke(new BasicStroke(2.0f));
////
////        // determine the axis ticks
////        double minYtick = Math.ceil(getMinY_Display() * 100) / 100;
////        double maxYtick = Math.floor(getMaxY_Display() * 100) / 100;
////
////        int count = 0;
////        double deltay = Math.rint((maxYtick - minYtick) * 10 + 0.5);
////        double stepYtick = deltay / 100;
////
////        for (double y = minYtick; y
////                < maxYtick; y
////                += stepYtick) {
////            Line2D line = new Line2D.Double(
////                    mapX(getMinX_Display(), getMinX_Display(), rangeX, graphWidth),
////                    mapY(y, getMaxY_Display(), rangeY, graphHeight),
////                    mapX(getMinX_Display(), getMinX_Display(), rangeX, graphWidth) + 7,
////                    mapY(y, getMaxY_Display(), rangeY, graphHeight));
////            g2d.draw(line);
////
////            if ((count % 2) == 1) {
////                NumberFormat yFormat = null;
////                String temp = null;
////
////                yFormat
////                        = new DecimalFormat("0.00");
////                temp
////                        = yFormat.format(y);
////
////                g2d.setPaint(Color.black);
////                g2d.rotate(
////                        -Math.PI / 2.0,
////                        (float) mapX(getMinX_Display(), getMinX_Display(), rangeX, graphWidth) - 4f,
////                        (float) mapY(y, getMaxY_Display(), rangeY, graphHeight) + 30f);
////                g2d.drawString(
////                        temp,
////                        (float) mapX(getMinX_Display(), getMinX_Display(), rangeX, graphWidth) - 4f,
////                        (float) mapY(y, getMaxY_Display(), rangeY, graphHeight) + 30f);
////                g2d.rotate(
////                        Math.PI / 2.0,
////                        (float) mapX(getMinX_Display(), getMinX_Display(), rangeX, graphWidth) - 4f,
////                        (float) mapY(y, getMaxY_Display(), rangeY, graphHeight) + 30f);
////
////            }
////
////            count++;
////
////        }
        // draw and label axes
        g2d.setFont(
                new Font("Monospaced", Font.BOLD, 20));
        g2d.drawRect(
                getLeftMargin(), getTopMargin(), getGraphWidth() - 1, getGraphHeight() - 1);

    }

    /**
     *
     * @param doReScale the value of doReScale
     * @param inLiveMode the value of inLiveMode
     */
    public void refreshPanel(boolean doReScale, boolean inLiveMode) {
        preparePanel(doReScale, false);
        repaint();
    }

    /**
     *
     * @param doReScale the value of doReScale
     * @param inLiveMode the value of inLiveMode
     */
    public void preparePanel(boolean doReScale, boolean inLiveMode) {

        zoomMaxX = 0;
        zoomMaxY = 0;
        zoomMinX = 0;
        zoomMinY = 0;

        setDisplayOffsetY(0.0);
        setDisplayOffsetX(0.0);

        this.removeAll();

        // walk selectedDateModels and get min and max of both sample date weighted mean graphs
        // X-axis is merely for layout, so map it to the size of panel
        setMinX(0.0);
        setMaxX(getGraphWidth());

        // Y-axis is going to be the age associated with the weighted means
        setMinY(ReduxConstants.MAX_DATE_ANNUM);//4.5E9 );
        setMaxY(0.0);

        for (int i = 0; i
                < selectedSampleDateModels.length; i++) {
            for (int j = 1; j < 9; j++) {
                if (selectedSampleDateModels[i][j] instanceof SampleDateModel) {

                    double wmMax = ((SampleDateModel) selectedSampleDateModels[i][j]).//
                            DetermineMaxDatePlusTwoSigma();
                    if (wmMax > maxY) {
                        setMaxY(wmMax);
                    }

                    double wmMin = ((SampleDateModel) selectedSampleDateModels[i][j]).//
                            DetermineMinDateLessTwoSigma();
                    if (wmMin < minY) {
                        setMinY(wmMin);
                    }

                }
            }
        }

        // convert to Ma
        setMinY(minY / 1.0E6);
        setMaxY(maxY / 1.0E6);

        // force margin Y
        double rangeY = (getMaxY_Display() - getMinY_Display());
        setMaxY(maxY + 0.15 * rangeY);

        // determine the y axis tic
        double minYtic = Math.ceil(getMinY_Display() * 100) / 100;
        double maxYtic = Math.floor(getMaxY_Display() * 100) / 100;
        double deltay = Math.rint((maxYtic - minYtic) * 10 + 0.5);
        double yTic = deltay / 100;
        setMinY((minY - 0.25 * rangeY) - 9 * yTic);

    }

    /**
     *
     * @param file
     */
    public void outputToSVG(File file) {

        // Get a DOMImplementation.
        DOMImplementation domImpl
                = GenericDOMImplementation.getDOMImplementation();

        // Create an instance of org.w3c.dom.Document.
        String svgNS = "http://www.w3.org/2000/svg";
        Document document = domImpl.createDocument(svgNS, "svg", null);

        // Create an instance of the SVG Generator.
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

        // Ask the test to render into the SVG Graphics2D implementation.
        paint(svgGenerator);

        // Finally, stream out SVG to the standard output using
        // UTF-8 encoding.
        boolean useCSS = true; // we want to use CSS style attributes

        Writer out = null;
        try {
            out = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
        } catch (FileNotFoundException fileNotFoundException) {
        } catch (UnsupportedEncodingException unsupportedEncodingException) {
        }
        try {
            svgGenerator.stream(out, useCSS);
        } catch (SVGGraphics2DIOException sVGGraphics2DIOException) {
        }
    }

    /**
     *
     * @param file
     */
    public void outputToPDF(File file) {
        SVGConverter myConv = new SVGConverter();
        myConv.setDestinationType(org.apache.batik.apps.rasterizer.DestinationType.PDF);
        try {
            myConv.setSources(new String[]{file.getCanonicalPath()});

        } catch (IOException iOException) {
        }
        myConv.setWidth((float) getWidth() + 2);
        myConv.setHeight((float) getHeight() + 2);

        try {
            myConv.execute();

        } catch (SVGConverterException sVGConverterException) {
            System.out.println("Error in pdf conversion: " + sVGConverterException.getMessage());
        }

    }

    /**
     *
     * @return
     */
    public int getGraphWidth() {
        return graphWidth;
    }

    /**
     *
     * @param graphWidth
     */
    public void setGraphWidth(int graphWidth) {
        this.graphWidth = graphWidth;
    }

    /**
     *
     * @return
     */
    public int getGraphHeight() {
        return graphHeight;
    }

    /**
     *
     * @param graphHeight
     */
    public void setGraphHeight(int graphHeight) {
        this.graphHeight = graphHeight;
    }

    /**
     *
     * @return
     */
    public int getTopMargin() {
        return topMargin;
    }

    /**
     *
     * @param topMargin
     */
    public void setTopMargin(int topMargin) {
        this.topMargin = topMargin;
    }

    /**
     *
     * @return
     */
    public int getLeftMargin() {
        return leftMargin;
    }

    /**
     *
     * @param leftMargin
     */
    public void setLeftMargin(int leftMargin) {
        this.leftMargin = leftMargin;
    }

    /**
     *
     * @return
     */
    public double getMinX_Display() {
        return minX + displayOffsetX;
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
        return maxX + displayOffsetX;
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
        return minY + displayOffsetY;
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
        return maxY + displayOffsetY;
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
    public double getDisplayOffsetY() {
        return displayOffsetY;
    }

    /**
     *
     * @param displayOffsetY
     */
    public void setDisplayOffsetY(double displayOffsetY) {
        //System.out.println("displayOffsetY  " + displayOffsetY);
        this.displayOffsetY = displayOffsetY;
    }

    /**
     *
     * @return
     */
    public double getDisplayOffsetX() {
        return displayOffsetX;
    }

    /**
     *
     * @param displayOffsetX
     */
    public void setDisplayOffsetX(double displayOffsetX) {
        //System.out.println("displayOffsetX  " + displayOffsetX);
        this.displayOffsetX = displayOffsetX;
    }

    /**
     *
     * @return
     */
    public double getRangeX() {
        return maxX - minX;
    }

    /**
     *
     * @return
     */
    public double getRangeY() {
        return maxY - minY;
    }

    /**
     *
     * @return
     */
    public double getRangeX_Display() {
        return getMaxX_Display() - getMinX_Display();
    }

    /**
     *
     * @return
     */
    public double getRangeY_Display() {
        return getMaxY_Display() - getMinY_Display();
    }

    /**
     *
     * @return
     */
    public double getMinX() {
        return minX;
    }

    /**
     *
     * @return
     */
    public double getMinY() {
        return minY;
    }

    /**
     *
     * @return
     */
    public double getMaxX() {
        return maxX;
    }

    /**
     *
     * @return
     */
    public double getMaxY() {
        return maxY;
    }

    /**
     *
     * @return
     */
    public double getMinT() {
        return minT;
    }

    /**
     *
     * @param minT
     */
    public void setMinT(double minT) {
        this.minT = minT;
    }

    /**
     *
     * @return
     */
    public double getMaxT() {
        return maxT;
    }

    /**
     *
     * @param maxT
     */
    public void setMaxT(double maxT) {
        this.maxT = maxT;
    }

// Mouse events ************************************************************
    /**
     *
     * @param evt
     */
    public void mouseClicked(MouseEvent evt) {
    }

    /**
     *
     * @param evt
     */
    public void mousePressed(MouseEvent evt) {
        zoomMinX = evt.getX();
        zoomMinY
                = evt.getY();
    }

    /**
     *
     * @param evt
     */
    @Override
    public void mouseReleased(MouseEvent evt) {
        zoomMaxX = evt.getX();
        zoomMaxY = evt.getY();

        if (imageMode.equalsIgnoreCase("ZOOM") && (zoomMaxX != zoomMinX) && (zoomMaxY != zoomMinY)) {
            setMinX(convertMouseXToValue(Math.min(zoomMinX, zoomMaxX)));
            setMinY(convertMouseYToValue(Math.max(zoomMinY, zoomMaxY)));
            // hack to correct a bug in the reported x,y from mouse release vs mouse click
            setMaxX(convertMouseXToValue(Math.max(zoomMaxX, zoomMinX) - 65));
            setMaxY(convertMouseYToValue(Math.min(zoomMaxY, zoomMinY) + 60));

            setDisplayOffsetX(0.0);
            setDisplayOffsetY(0.0);

            zoomMaxX = zoomMinX;
            zoomMaxY = zoomMinY;

            repaint();
        }
    }

    /**
     *
     * @param evt
     */
    public void mouseEntered(MouseEvent evt) {
        // this is not very sensitive, so have forced cursor at mode selection below
        if ((evt.getX() >= getLeftMargin())
                && (evt.getY() >= getTopMargin())
                && (evt.getY() <= graphHeight + getTopMargin())) {
            setCursor(getWeightedMeanCursor());
        }

    }

    /**
     *
     * @param evt
     */
    @Override
    public void mouseExited(MouseEvent evt) {
        // setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     *
     * @param evt
     */
    @Override
    public void mouseDragged(MouseEvent evt) {
        zoomMaxX = evt.getX();
        zoomMaxY = evt.getY();

        if (imageMode.equalsIgnoreCase("PAN")) {
            setDisplayOffsetX(getDisplayOffsetX() //
                    + (convertMouseXToValue(zoomMinX) - convertMouseXToValue(zoomMaxX)));
            setDisplayOffsetY(getDisplayOffsetY() //
                    + (convertMouseYToValue(zoomMinY) - convertMouseYToValue(zoomMaxY)));
            // System.out.println("move from " + zoomMinX + ", " + zoomMinY + "  to  " + zoomMaxX + ", " + zoomMaxY);
            zoomMinX = zoomMaxX;
            zoomMinY = zoomMaxY;
        }

        repaint();
    }

    /**
     *
     * @param evt
     */
    @Override
    public void mouseMoved(MouseEvent evt) {
    }

    private double convertMouseXToValue(int x) {
        return //
                ((x - getLeftMargin()) / (double) getGraphWidth()) //
                * getRangeX_Display()//
                + getMinX_Display();
    }

    private double convertMouseYToValue(int y) {
        return //
                (1.0 - ((double) (y - getTopMargin()) / getGraphHeight())) //
                * getRangeY_Display()//
                + getMinY_Display();
    }

    /**
     *
     * @return
     */
    public String getImageMode() {
        return imageMode;
    }

    /**
     *
     * @param imageMode
     */
    public void setImageMode(String imageMode) {
        this.imageMode = imageMode;
        if (imageMode.equalsIgnoreCase("PAN")) {
            weightedMeanCursor = new Cursor((Cursor.HAND_CURSOR));
        } else if (imageMode.equalsIgnoreCase("ZOOM")) {
            weightedMeanCursor = new Cursor((Cursor.CROSSHAIR_CURSOR));
        }
        // force issue
        setCursor(weightedMeanCursor);
    }

    /**
     *
     * @return
     */
    public Cursor getWeightedMeanCursor() {
        return weightedMeanCursor;
    }

    /**
     *
     * @param weightedMeanCursor
     */
    public void setWeightedMeanCursor(Cursor weightedMeanCursor) {
        this.weightedMeanCursor = weightedMeanCursor;
    }

    /**
     *
     * @return
     */
    public Object[][] getSelectedSampleDateModels() {
        return selectedSampleDateModels;
    }

    /**
     *
     * @param selectedSampleDateModels
     */
    public void setSelectedSampleDateModels(Object[][] selectedSampleDateModels) {
        this.selectedSampleDateModels = selectedSampleDateModels;
    }

    /**
     *
     * @return
     */
    public Map<String, String> getWeightedMeanOptions() {
        if (weightedMeanOptions == null) {
            weightedMeanOptions = new SampleDateInterpretationGUIOptions().getWeightedMeanOptions();
        }
        return weightedMeanOptions;
    }

    /**
     *
     * @param weightedMeanOptions
     */
    public void setWeightedMeanOptions(Map<String, String> weightedMeanOptions) {
        this.weightedMeanOptions = weightedMeanOptions;
    }

    /**
     * @return the sample
     */
    public SampleInterface getSample() {
        return sample;
    }

    /**
     * @param sample the sample to set
     */
    public void setSample(Sample sample) {
        this.sample = sample;
    }

    @Override
    public void resetPanel(boolean doReScale, boolean inLiveMode) {
        refreshPanel(doReScale, inLiveMode);
    }
}
