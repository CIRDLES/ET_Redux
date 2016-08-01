/*
 * DateProbabilityDensityPanel.java
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
import java.awt.Robot;
import java.awt.Shape;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.font.TextLayout;
import java.awt.geom.Ellipse2D;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import javax.swing.JLayeredPane;
import org.apache.batik.apps.rasterizer.SVGConverter;
import org.apache.batik.apps.rasterizer.SVGConverterException;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.earthtime.UPb_Redux.beans.ReduxSuppressComponentEventsI;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.GraphPanelModeChangeI;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.PlottingDetailsDisplayInterface;
import org.earthtime.UPb_Redux.dateInterpretation.vermeeschKDE.KDE;
import org.earthtime.UPb_Redux.dateInterpretation.vermeeschKDE.OtherData;
import org.earthtime.UPb_Redux.dateInterpretation.vermeeschKDE.Preferences;
import org.earthtime.UPb_Redux.user.SampleDateInterpretationGUIOptions;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.aliquots.ReduxAliquotInterface;
import org.earthtime.dataDictionaries.RadDates;
import org.earthtime.fractions.ETFractionInterface;
import org.earthtime.samples.SampleInterface;
import org.earthtime.utilities.TicGeneratorForAxes;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

/**
 *
 * @author James F. Bowring
 */
public class DateProbabilityDensityPanel extends JLayeredPane
        implements
        PlottingDetailsDisplayInterface,
        MouseListener,
        MouseMotionListener {

    private int graphWidth;
    private int graphHeight;
    private int topMargin = 1;
    private int leftMargin = 20;
    // Instance Variables
    /**
     *
     */
    protected SampleInterface sample;
    private int selectedAliquotNumber;
    private Vector<ETFractionInterface> selectedFractions;
    private Vector<ETFractionInterface> deSelectedFractions;
    /**
     *
     */
    protected double[] stackedKernels;
    /**
     *
     */
    protected double[] activeStackedKernels;
    /**
     *
     */
    protected double[][] stackedAliquotKernels;
    /**
     *
     */
    protected double[] activeStackedAliquotKernels;
    /**
     *
     */
    protected ArrayList<Double> pdfPoints;
    private double minX;
    private double maxX;
    private double minY;
    private double maxY;
    private double displayOffsetY;
    private double displayOffsetX;
    private String chosenDateName;
    private int zoomMin;
    private int zoomMax;
    private Map<String, Map<String, String>> aliquotOptions;
    /**
     *
     */
    public final static double DEFAULT_DISPLAY_MINX = -50;//- 100.0;
    /**
     *
     */
    public final static double DEFAULT_DISPLAY_MAXX = 4050;//4100.0;
    private boolean mouseInside;
    private Line2D cursorBar;
    private Rectangle2D cursorLabel;
    private boolean showHistogram;
    private int selectedHistogramBinCount;
    private ReduxSuppressComponentEventsI externalBinCountIntegerSpinner;
    private ReduxSuppressComponentEventsI externalBinWidthTextField;
    private boolean freezeHistogramBinWidth;
    private double adjustedScottsBinWidth;
    private boolean uploadToGeochronMode = false;
    private Map<Integer, Double> maxima;
    private Map<Integer, Boolean> maximaShown;
    // june 2013 Vermeesch
    private double[] pdfOfKDE;
    private double[] timescale;
    private double maxKDE;
    private GraphPanelModeChangeI graphPanelModeChanger;

    /**
     *
     * @param sample
     */
    public DateProbabilityDensityPanel(SampleInterface sample) {
        super();

        this.sample = sample;

        setOpaque(true);

        setBackground(Color.white);

        selectedAliquotNumber = 0;

        this.graphWidth = 775;
        this.graphHeight = 585;

        selectedFractions = new Vector<>();
        deSelectedFractions = new Vector<>();

        stackedKernels = new double[0];
        activeStackedKernels = new double[0];
        stackedAliquotKernels = new double[0][0];
        activeStackedAliquotKernels = new double[0];

        minX = DEFAULT_DISPLAY_MINX;
        maxX = DEFAULT_DISPLAY_MAXX;
        minY = 0.0;
        maxY = 1.0;

        zoomMax = 0;
        zoomMin = 0;

        chosenDateName = RadDates.age207_206r.getName();

        this.aliquotOptions
                =//
                sample.getSampleDateInterpretationGUISettings().getAliquotOptions();

        mouseInside = false;

        cursorBar = new Line2D.Double();
        cursorLabel = new Rectangle2D.Double();

        showHistogram = true;
        selectedHistogramBinCount = 0;
        externalBinCountIntegerSpinner = null;

        // per Gehrels Nov 2011
        freezeHistogramBinWidth = true;
        adjustedScottsBinWidth = 20.0;

        maxima = new HashMap<>();
        maximaShown = new TreeMap<>();

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
    public void paint(Graphics2D g2d) {

        g2d.setClip(0/*
                 * getLeftMargin()
                 */, getTopMargin(), getGraphWidth(), getGraphHeight() + 15);
        RenderingHints rh = g2d.getRenderingHints();
        rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHints(rh);

        if (stackedAliquotKernels[0][pdfPoints.size()] > 0) {
            g2d.setFont(new Font(
                    "SansSerif",
                    Font.PLAIN,
                    10));

            // draw sample curve as backdrop
            Path2D sampleProbabilities = new Path2D.Double(Path2D.WIND_NON_ZERO);
            Path2D sampleKDE = new Path2D.Double(Path2D.WIND_NON_ZERO);

            // handle zoom
            int startX = 0;
            for (int i = 0; i < pdfPoints.size(); i++) {
                if (mapX(pdfPoints.get(i)) <= 0.0) {
                    startX++;
                } else {
                    break;
                }
            }

            int endX = startX;
            for (int i = startX; i < pdfPoints.size(); i++) {
                if (mapX(pdfPoints.get(i)) <= (getGraphWidth()) + (double) getLeftMargin()) {
                    endX++;
                } else {
                    break;
                }
            }
            endX--;

//            sampleProbabilities.moveTo( mapX( getMinX_Display() ), mapY( 0.01 ) );
            sampleProbabilities.moveTo(Math.max(leftMargin, mapX(pdfPoints.get(startX))), mapY(0.01));
            sampleKDE.moveTo(Math.max(leftMargin, mapX(timescale[startX])), mapY(0.01));

            double scale = 0.96/*
                     * 8
                     */ * maxY / stackedAliquotKernels[0][pdfPoints.size()];

            double scaleKDE = 0.96 / maxKDE;

            // startx and endx bound the viewport and hence the data points for which we want histograms
            for (int i = startX; i <= endX; i++) {
                sampleProbabilities.lineTo(//
                        Math.max(leftMargin, mapX(pdfPoints.get(i))),//
                        mapY(stackedAliquotKernels[0][i] * scale + .01));

                try {
                    sampleKDE.lineTo(//
                            Math.max(leftMargin, mapX(timescale[i])),//
                            mapY(pdfOfKDE[i] * scaleKDE + .01));
                } catch (Exception e) {
                }
            }

            sampleProbabilities.lineTo(mapX(pdfPoints.get(endX)), mapY(0.01));
            // sampleProbabilities.moveTo( mapX( 4000.0 ), mapY( 0.01 ) );

            if (selectedAliquotNumber > 0) {
                // paint sample semi-transparent
                Composite originalComposite = g2d.getComposite();
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
                g2d.setPaint(Color.gray);
                g2d.fill(sampleProbabilities);
                //restore composite
                g2d.setComposite(originalComposite);

                // paint aliquot
                Path2D selectedAliquotProbabilities = new Path2D.Double(Path2D.WIND_NON_ZERO);

                selectedAliquotProbabilities.moveTo(mapX(getMinX_Display()), mapY(0.01));
                // selectedAliquotProbabilities.moveTo( mapX( 0.0 ), mapY( 0.01 ) );

                for (int i = startX; i <= endX; i++) {//int i = 0; i < pdfPoints.size(); i ++) {
                    selectedAliquotProbabilities.lineTo(//
                            mapX(pdfPoints.get(i)), mapY(stackedAliquotKernels[selectedAliquotNumber][i] * scale + .01));
                }

                selectedAliquotProbabilities.lineTo(mapX(pdfPoints.get(endX)), mapY(0.01));
                // selectedAliquotProbabilities.moveTo( mapX( 4000.0 ), mapY( 0.01 ) );

                // determine aliquot for colors etc.
                String aliquotName = sample.getAliquotByNumber(selectedAliquotNumber).getAliquotName();
                Map<String, String> myAliquotOptions = (Map<String, String>) getAliquotOptions().get(aliquotName);

                Color includedFillColor = new Color(255, 255, 255);
                if (myAliquotOptions.containsKey("includedFillColor")) {
                    String[] temp
                            = myAliquotOptions.get("includedFillColor").split(",");
                    includedFillColor = buildRGBColor(temp);
                }

                float includedFillTransparencyPCT = 20.0f;
//                if ( myAliquotOptions.containsKey( "includedFillTransparencyPCT" ) ) {
//                    includedFillTransparencyPCT = Float.valueOf( myAliquotOptions.get( "includedFillTransparencyPCT" ) );
//                }

                // check for deselected fractions
                if (deSelectedFractions.size() > 0) {
                    // remove deslected fractions, paint the aliquot with its transparency and the remaining fractions in full color
                    originalComposite = g2d.getComposite();
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, includedFillTransparencyPCT / 100.0f));
                    g2d.setPaint(includedFillColor);

                    g2d.fill(selectedAliquotProbabilities);
                    //restore composite
                    g2d.setComposite(originalComposite);

                    // now remove the deselected fractions
                    activeStackedAliquotKernels = stackedAliquotKernels[selectedAliquotNumber].clone();
                    for (ETFractionInterface f : deSelectedFractions) {
                        // April 2016 remove primary and secondary reference materials
                        if (!f.isStandard() && !f.isSecondaryStandard()) {
                            ValueModel date = f.getRadiogenicIsotopeDateByName(getChosenDateName());
                            KernelF myKernel = new KernelF(date);
                            for (int i = 0; i < pdfPoints.size(); i++) {
                                double eval = evalKernelAt(myKernel, pdfPoints.get(i));
                                activeStackedAliquotKernels[i] -= eval;
                            }
                        }
                    }

                    Path2D selectedFractionProbabilities = new Path2D.Double(Path2D.WIND_NON_ZERO);

                    selectedFractionProbabilities.moveTo(mapX(getMinX_Display()), mapY(0.01));
                    // selectedFractionProbabilities.moveTo( mapX( 0.0 ), mapY( 0.01 ) );

                    for (int i = startX; i <= endX; i++) {//for (int i = 0; i < pdfPoints.size(); i ++) {
                        selectedFractionProbabilities.lineTo(//
                                mapX(pdfPoints.get(i)), mapY(activeStackedAliquotKernels[i] * scale + .01));
                    }

                    selectedFractionProbabilities.lineTo(mapX(pdfPoints.get(endX)), mapY(0.01));
                    // selectedFractionProbabilities.moveTo( mapX( 4000.0 ), mapY( 0.01 ) );

                    // paint the active fractions full color
                    g2d.setPaint(includedFillColor);
                    g2d.fill(selectedFractionProbabilities);

                } else {
                    // just paint the aliquot full color
                    g2d.setPaint(includedFillColor);
                    g2d.fill(selectedAliquotProbabilities);
                }

            } else {
                // paint sample gray
//                g2d.setPaint( Color.LIGHT_GRAY );
//                g2d.fill( sampleProbabilities );
//                
                g2d.setPaint(Color.GRAY);
                g2d.draw(sampleProbabilities);

                g2d.setPaint(Color.red);
// May 2014 turn off                g2d.draw(sampleKDE);
            }
            // mark data points and collect them for histogram in visibleSample
            List visibleSample = new ArrayList<>();

            g2d.setPaint(Color.red);
            for (int i = startX; i <= endX; i++) {
                // test for actual data point
                if (((pdfPoints.get(i) - Math.floor(pdfPoints.get(i))) > 0.0)//
                        &&//
                        (mapX(pdfPoints.get(i)) >= leftMargin)) {
                    Ellipse2D selectedPoint = new Ellipse2D.Double( //
                            mapX(pdfPoints.get(i)), mapY(stackedAliquotKernels[0][i] * scale + .01), 2, 2);

                    visibleSample.add(pdfPoints.get(i));
                    g2d.fill(selectedPoint);
                }
            }

//            }
            // draw tics
            drawTics(g2d);
            if (showHistogram && visibleSample.size() > 0) {
                drawHistograms(g2d, visibleSample);
            }

            double cursorAge = ((cursorBar.getX1() - leftMargin) * getRangeX_Display() / getGraphWidth()) + getMinX_Display();
            if (cursorAge > getMinX_Display() && cursorAge < 4000) {
                // draw cursor
                g2d.setPaint(Color.red);
                g2d.draw(cursorBar);
                g2d.setPaint(Color.white);
                g2d.fill(cursorLabel);
                g2d.setPaint(Color.red);
                g2d.draw(cursorLabel);

                g2d.drawString(Integer.toString((int) Math.round(cursorAge)),//
                        (float) cursorLabel.getX() + 2, (float) cursorLabel.getY() + 10);

            }

            // april 2012 print local maxima labels after Victor Valencia request
            Iterator<Integer> maximaIterator = maxima.keySet().iterator();
            while (maximaIterator.hasNext()) {

                int dateOfMax = maximaIterator.next();

                if (maximaShown.get(dateOfMax)) {
                    double maxProbability = maxima.get(dateOfMax);

                    TextLayout tempLayout
                            = //
                            new TextLayout(
                                    Integer.toString(dateOfMax), g2d.getFont(), g2d.getFontRenderContext());

                    Rectangle2D bounds = tempLayout.getBounds();

                    float textX = (float) mapX(dateOfMax) //
                            - (float) bounds.getWidth() / 2;
                    float textY = (float) mapY(maxProbability * scale + .01) - 8 //
                            + (float) bounds.getHeight() * 0.4f;

                    g2d.setPaint(Color.black);
                    g2d.drawString( //
                            Integer.toString(dateOfMax), //
                            textX, //
                            textY);
                }
            }
        }

        // nov 2011
        if (uploadToGeochronMode) {
            // draw sample name for geochron-uploaded graphs
            Font savedFont = g2d.getFont();
            g2d.setPaint(Color.black);
            g2d.setFont(new Font(
                    "SansSerif",
                    Font.PLAIN,
                    14));

            String myText = "SAMPLE = " + sample.getSampleName().trim();
            TextLayout mLayout
                    = //
                    new TextLayout(
                            myText, g2d.getFont(), g2d.getFontRenderContext());

            Rectangle2D bounds = mLayout.getBounds();
            g2d.drawString(myText, (int) (graphWidth - bounds.getBounds().getWidth()), (int) bounds.getBounds().getHeight() + 5);
            g2d.drawString( //
                    "Bin Width = " + new DecimalFormat("###0").format(adjustedScottsBinWidth)//
                    + " Ma", (int) (graphWidth - bounds.getBounds().getWidth()), (int) bounds.getBounds().getHeight() + 20);

            g2d.setFont(savedFont);
        }

    }

    private void drawHistograms(Graphics2D g2d, List<Double> visibleSample) {

        int countOfBins = selectedHistogramBinCount;  // incoming from spinner

        double dataRange = 0;
        try {
            dataRange = ((visibleSample.get(visibleSample.size() - 1)) - visibleSample.get(0));
            dataRange *= 1.05;
        } catch (Exception e) {
        }

        if (!freezeHistogramBinWidth) {
            // to use Scotts rule of 3.49 * s / n^3
            // we need the mean to calculate the sample standard deviation = s
            double sum = 0.0;
            for (int i = 0; i < visibleSample.size(); i++) {
                sum += visibleSample.get(i);
            }
            double meanOfVisibleSample = sum / visibleSample.size();
            // calculate sample standard deviation
            sum = 0.0;
            for (int i = 0; i < visibleSample.size(); i++) {
                sum += Math.pow((visibleSample.get(i) - meanOfVisibleSample), 2);
            }
            double sampleStandardDeviation = Math.sqrt(sum / (visibleSample.size() - 1));
            double scottsBinWidth = 3.49 * sampleStandardDeviation / Math.pow(visibleSample.size(), 1.0 / 3.0);

            if (countOfBins == 0) {
                countOfBins = /*
                         * was 1 until april 2012
                         */ 0 + (int) Math.round(dataRange / scottsBinWidth);
                externalBinCountIntegerSpinner.setValueWithNoEvents(countOfBins);
            }

            setAdjustedScottsBinWidth(dataRange / countOfBins);

        } else {
            // use the last width, so count of bins changes
            countOfBins = (int) Math.round(dataRange / getAdjustedScottsBinWidth());
            try {
                externalBinCountIntegerSpinner.setValueWithNoEvents(countOfBins);
            } catch (Exception e) {
            }
        }

        try {
            this.externalBinWidthTextField.setValueWithNoEvents((int) adjustedScottsBinWidth);
        } catch (Exception e) {
        }

////        System.out.println( "ADJSCOTT BIN WIDTH = " + adjustedScottsBinWidth//
////                + "  left = " + visibleSample.get( 0 ) //
////                + "   right = " + (visibleSample.get( visibleSample.size() - 1 ))//
////                + "   diff =  " + dataRange//
////                + "    count =  " + countOfBins );
        // histogram calcs
        if (countOfBins < 0) {
            countOfBins = 0;
        }
        int[] histogram = new int[countOfBins];
        int maxBinPopulation = 0;

        for (int i = 0; i < visibleSample.size(); i += 1) {
            int binNumber
                    = //
                    (int) Math.floor( //
                            (visibleSample.get(i) - visibleSample.get(0)) / getAdjustedScottsBinWidth());

//            System.out.println( "BIN = " + binNumber + "  " + (visibleSample.get( i ) - visibleSample.get( 0 )) / adjustedScottsBinWidth );
            // catch last entry
//            if ((binNumber == countOfBins) && (binNumber > 0)) {
////                binNumber --;
//            }
            try {
                histogram[binNumber]++;
                if (histogram[binNumber] > maxBinPopulation) {
                    maxBinPopulation = histogram[binNumber];
                }
            } catch (Exception e) {
            }
        }

        selectedHistogramBinCount = countOfBins;

        // histogram plot
        Composite originalComposite = g2d.getComposite();
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.10f));
        g2d.setPaint(Color.blue);

        double histoScale = 0.96 * maxY / maxBinPopulation;
        for (int i = 0; i < histogram.length; i += 1) {

            if (mapX(visibleSample.get(0) + (i * getAdjustedScottsBinWidth())) >= leftMargin) {
                try {
                    Rectangle2D histoBin = new Rectangle2D.Double(//
                            mapX(visibleSample.get(0) + (i * getAdjustedScottsBinWidth())),
                            mapY(histogram[i] * histoScale + 0.01),
                            // include extra pixel for looks
                            1.0f + mapX(visibleSample.get(0) + ((i + 1) * getAdjustedScottsBinWidth())) - mapX(visibleSample.get(0) + (i * getAdjustedScottsBinWidth())),
                            mapY(maxY - histogram[i] * histoScale));

                    g2d.fill(histoBin);
                } catch (Exception e) {
                }
            }

        }
        //restore composite
        g2d.setComposite(originalComposite);

        // april 2012
        // left hand scale showing count of ages in bins
        int maxHistogramCount = 0;
        for (int i = 0; i < histogram.length; i++) {
//            System.out.println( "bin = " + i + "   count = " + histogram[i] );
            maxHistogramCount = Math.max(maxHistogramCount, histogram[i]);
        }

        // create a tic for each count number
        BigDecimal[] yAxisHistogramTics
                =//
                TicGeneratorForAxes.generateTics(0, maxHistogramCount, 12);//maxHistogramCount + (maxHistogramCount % 2) );
        for (int i = 0; i < yAxisHistogramTics.length; i++) {
//            System.out.println( "TIC = " + i );

            try {
                if (yAxisHistogramTics[i].doubleValue() == Math.floor(yAxisHistogramTics[i].doubleValue())) {
                    Shape ticMark = new Line2D.Double( //
                            leftMargin - 5,//mapX( minX ),//
                            mapY(yAxisHistogramTics[i].doubleValue() * histoScale + 0.01),//
                            leftMargin,//mapX( maxX ), //
                            mapY(yAxisHistogramTics[i].doubleValue() * histoScale + 0.01));

                    g2d.setPaint(Color.black);
                    g2d.setStroke(new BasicStroke(1.0f));
                    g2d.draw(ticMark);

                    g2d.drawString( //
                            yAxisHistogramTics[i].setScale(0).toPlainString(),//
                            1f, //
                            (float) mapY(yAxisHistogramTics[i].doubleValue() * histoScale + 0.01) + 3);

                }
            } catch (Exception e) {
            }
        }
    }

    private void drawTics(Graphics2D g2d) {
        // plot grid
        g2d.setColor(Color.darkGray);
        g2d.setStroke(new BasicStroke(1.0f));
        g2d.drawRect(
                leftMargin, getTopMargin(), (int) getGraphWidth() - 1, (int) getGraphHeight() - 1);

//        g2d.setClip( 0/*getLeftMargin()*/, getTopMargin(), getGraphWidth(), getGraphHeight() + 15 );
        DecimalFormat dateFormat = new DecimalFormat("###0");
        g2d.setStroke(new BasicStroke(0.5f));

        // round to the highest N00000...
        double ticFreq = ((maxX - minX) / 10);
        double digits = Math.ceil(Math.log10(ticFreq)) - 1;
        double tenPow = Math.pow(10, digits);
        ticFreq = Math.floor(ticFreq / tenPow) * tenPow;

        for (int i = 0; i < 4001; i += ticFreq) {

            if (mapX(i) > leftMargin) {
                Line2D tic = new Line2D.Double(
                        mapX(i),
                        mapY(getMinY_Display()) + 5,
                        mapX(i),
                        mapY(getMinY_Display() + maxY));
                g2d.draw(tic);

                String temp = dateFormat.format(i);

                // draw number value, adjusting for font
                // build the box to fit the value string
                TextLayout tempLayout
                        = //
                        new TextLayout(
                                temp.trim(), g2d.getFont(), g2d.getFontRenderContext());

                Rectangle2D bounds = tempLayout.getBounds();

                float textX = (float) mapX(i) //
                        - (float) bounds.getWidth() / 2;
                float textY = (float) mapY(getMinY_Display()) + 8 //
                        + (float) bounds.getHeight() * 0.4f;

                g2d.setPaint(Color.black);
                g2d.drawString(temp, textX, textY);
            }
        }
    }

    private double mapX(double x) {

        return (((x - getMinX_Display()) / getRangeX_Display()) * getGraphWidth()) + (double) getLeftMargin();
    }

    private double mapY(double y) {

        return (((getMaxY_Display() - y) / getRangeY_Display()) * getGraphHeight()) + (double) getTopMargin();
    }

    private Color buildRGBColor(String[] rgbComponents) {
        return new Color(
                (int) Integer.parseInt(rgbComponents[0].trim()),
                (int) Integer.parseInt(rgbComponents[1].trim()),
                (int) Integer.parseInt(rgbComponents[2].trim()));
    }

    @Override
    public void resetPanel(boolean doReScale, boolean inLiveMode) {
        refreshPanel(doReScale, inLiveMode);
    }

    @Override
    public void refreshPanel(boolean doReScale, boolean inLiveMode) {
        if (doReScale) {
            // nov 2011
            setMinX(DateProbabilityDensityPanel.DEFAULT_DISPLAY_MINX);
            setMaxX(DateProbabilityDensityPanel.DEFAULT_DISPLAY_MAXX);
            setDisplayOffsetX(0);
        }

        preparePanel(doReScale, inLiveMode);
        repaint();
    }

    /**
     *
     */
    public void prepareAndPaintPanel() {
        preparePanel(true, false);
        repaint();
    }

    /**
     *
     * @param doReScale the value of doReScale
     * @param inLiveMode the value of inLiveMode
     */
    @Override
    public void preparePanel(boolean doReScale, boolean inLiveMode) {

//        System.out.println("========Probability Prep=======");
        this.removeAll();

        //nov 2011
        try {
            externalBinCountIntegerSpinner.setValueWithNoEvents(selectedHistogramBinCount);
        } catch (Exception e) {
        }

        // modified to include actual measured data
        // there is one point for each of 0 to 4000 inclusive plus a point for each of the actual data
        pdfPoints = new ArrayList<>();
        for (double i = 0; i < 4001; i++) {
            pdfPoints.add(i);
        }
        for (ETFractionInterface f : selectedFractions) {
            // nov 2011 add in tiny amount so that grapher can distinguish between annum and dates based on aaaa.0  vs aaaa.0000001
            // April 2016 remove primary and secondary reference materials
            if (!f.isStandard() && !f.isSecondaryStandard()) {
                pdfPoints.add(f.getRadiogenicIsotopeDateByName(getChosenDateName()).getValue().movePointLeft(6).doubleValue() + 0.0000001);
            }
        }
        Collections.sort(pdfPoints);

        // one row for each aliquot even though some may be dummy aliquots, based on count
        // row zero for sample totals and [0][size -+ 1] stores max for plotting scale
        stackedAliquotKernels = new double[sample.getAliquots().size() + 1][pdfPoints.size() + 1];

        // June 2013 experiment with Vermeesch KDE
        ArrayList<Double> X = new ArrayList<>();
        ArrayList<Double> Y = new ArrayList<>();
        ArrayList<Double> Z = new ArrayList<>();

        // 4096 = 2^12
        timescale = new double[4096];
        for (int i = 0; i < timescale.length; i++) {
            timescale[i] = i;
        }
        // end June 2013 experiment with Vermeesch KDE

        for (ETFractionInterface f : selectedFractions) {
            // April 2016 remove primary standard
            if (!f.isStandard() && !f.isSecondaryStandard()) {
                ValueModel date = f.getRadiogenicIsotopeDateByName(chosenDateName);

                // June 2013 experiment with Vermeesch KDE
                X.add(date.getValue().movePointLeft(6).doubleValue());
                Y.add(Math.pow(date.getOneSigmaAbs().movePointLeft(6).doubleValue(), 2));
                Z.add(Double.NaN);
                // end June 2013 experiment with Vermeesch KDE

                int aliquotNumber = f.getAliquotNumber();
                KernelF myKernel = new KernelF(date);
                for (int i = 0; i < pdfPoints.size(); i++) {
                    double eval = evalKernelAt(myKernel, pdfPoints.get(i));
                    stackedAliquotKernels[0][i] += eval;
                    stackedAliquotKernels[aliquotNumber][i] += eval;
                }
            }
        }

        // more vermeesch ************************
        try {
            OtherData otherData = new OtherData(X, Y, Z, new Preferences(true));
            double[][] ae = otherData.getDataErrArray(otherData.preferences.logarithmic());

            KDE kde = new KDE();
            pdfOfKDE = kde.pdf(ae[0], timescale, true);

            maxKDE = 0.0;
            for (int i = 0; i < pdfOfKDE.length; i++) {
                maxKDE = Math.max(maxKDE, pdfOfKDE[i]);
            }

        } catch (Exception ex) {
        }
        // end vermeesch ************************

        double maxDateProb = 0.0;
        for (int i = 0; i < pdfPoints.size(); i++) {
            if (stackedAliquotKernels[0][i] > maxDateProb) {
                maxDateProb = stackedAliquotKernels[0][i];
            }
        }
        stackedAliquotKernels[0][pdfPoints.size()] = maxDateProb;

        // april 2012 experiment to detect maxima on curve
        // this simplistic technique leverages what we know about this pdf
        maxima = new HashMap<>();
        maximaShown = new TreeMap<>();

        int currentMaxIndex = 0;
        int currentMinIndex = 0;
        boolean uphill = true;
        for (int i = 1; i < pdfPoints.size(); i++) {
            if (uphill) {
                if (stackedAliquotKernels[0][i] < stackedAliquotKernels[0][currentMaxIndex]) {
//                    System.out.println( //
//                            "PEAK = " //
//                            + (int) Math.round( pdfPoints.get( currentMaxIndex ) ) );

                    maxima.put( //
                            (int) Math.round(pdfPoints.get(currentMaxIndex)),//
                            stackedAliquotKernels[0][currentMaxIndex]);
                    maximaShown.put(//
                            (int) Math.round(pdfPoints.get(currentMaxIndex)),//
                            true);

                    uphill = false;
                    currentMinIndex = i;
                } else {
                    currentMaxIndex++;
                }

            } else // downhill
            {
                if (stackedAliquotKernels[0][i] > stackedAliquotKernels[0][currentMinIndex]) {
                    uphill = true;
                    currentMaxIndex = i;
                } else {
                    currentMinIndex++;
                }
            }
        }

    }

    /**
     *
     */
    public void showTight() {
        refreshPanel(true, false);

        for (int i = 0; i < pdfPoints.size(); i++) {
            if (stackedAliquotKernels[0][i] > 0.01) {
                minX = Math.max(i - 100, DEFAULT_DISPLAY_MINX);
                break;
            }
        }

        for (int i = pdfPoints.size() - 1; i > -1; i--) {
            if (stackedAliquotKernels[0][i] > 0.01) {
                maxX = Math.min(i + 100, DEFAULT_DISPLAY_MAXX);
                break;
            }
        }

        repaint();
    }

    /**
     * @return the showHistogram
     */
    public boolean isShowHistogram() {
        return showHistogram;
    }

    /**
     * @param showHistogram the showHistogram to set
     */
    public void setShowHistogram(boolean showHistogram) {
        this.showHistogram = showHistogram;
    }

    /**
     * @return the selectedHistogramBinCount
     */
    public int getSelectedHistogramBinCount() {
        return selectedHistogramBinCount;
    }

    /**
     * @param selectedHistogramBinCount the selectedHistogramBinCount to set
     */
    public void setSelectedHistogramBinCount(int selectedHistogramBinCount) {
        this.selectedHistogramBinCount = selectedHistogramBinCount;
    }

    /**
     * @return the externalBinCountIntegerSpinner
     */
    public ReduxSuppressComponentEventsI getExternalBinCountIntegerSpinner() {
        return externalBinCountIntegerSpinner;
    }

    /**
     * @param externalBinCountIntegerSpinner the externalBinCountIntegerSpinner
     * to set
     */
    public void setExternalBinCountIntegerSpinner(ReduxSuppressComponentEventsI externalBinCountIntegerSpinner) {
        this.externalBinCountIntegerSpinner = externalBinCountIntegerSpinner;
    }

    /**
     * @return the freezeHistogramBinWidth
     */
    public boolean isFreezeHistogramBinWidth() {
        return freezeHistogramBinWidth;
    }

    /**
     * @param freezeHistogramBinWidth the freezeHistogramBinWidth to set
     */
    public void setFreezeHistogramBinWidth(boolean freezeHistogramBinWidth) {
        this.freezeHistogramBinWidth = freezeHistogramBinWidth;
    }

    /**
     * @return the adjustedScottsBinWidth
     */
    public double getAdjustedScottsBinWidth() {
        return adjustedScottsBinWidth;
    }

    /**
     * @param adjustedScottsBinWidth the adjustedScottsBinWidth to set
     */
    public void setAdjustedScottsBinWidth(double adjustedScottsBinWidth) {
        this.adjustedScottsBinWidth = adjustedScottsBinWidth;
//        try {
//            this.externalBinWidthTextField.setValueWithNoEvents( (int) adjustedScottsBinWidth );
//        } catch (Exception e) {
//        }
    }

    /**
     * @param uploadToGeochronMode the uploadToGeochronMode to set
     */
    public void setUploadToGeochronMode(boolean uploadToGeochronMode) {
        this.uploadToGeochronMode = uploadToGeochronMode;
    }

    /**
     * @return the externalBinWidthTextField
     */
    public ReduxSuppressComponentEventsI getExternalBinWidthTextField() {
        return externalBinWidthTextField;
    }

    /**
     * @param externalBinWidthTextField the externalBinWidthTextField to set
     */
    public void setExternalBinWidthTextField(ReduxSuppressComponentEventsI externalBinWidthTextField) {
        this.externalBinWidthTextField = externalBinWidthTextField;
    }

    /**
     * @return the maximaShown
     */
    public Map<Integer, Boolean> getMaximaShown() {
        return maximaShown;
    }

    /**
     * @param maximaShown the maximaShown to set
     */
    public void setMaximaShown(Map<Integer, Boolean> maximaShown) {
        this.maximaShown = maximaShown;
    }

    /**
     * @return the graphPanelModeChanger
     */
    public GraphPanelModeChangeI getGraphPanelModeChanger() {
        return graphPanelModeChanger;
    }

    /**
     * @param graphPanelModeChanger the graphPanelModeChanger to set
     */
    public void setGraphPanelModeChanger(GraphPanelModeChangeI graphPanelModeChanger) {
        this.graphPanelModeChanger = graphPanelModeChanger;
    }

    class KernelF implements Comparable<KernelF> {

        protected double meanDate;
        protected double variance;

        KernelF(ValueModel date) {
            this.meanDate = date.getValue().movePointLeft(6).doubleValue();
            this.variance = Math.pow(date.getOneSigmaAbs().movePointLeft(6).doubleValue(), 2);

            // temp hack for missing laicpms uncertainty    
            if (this.variance == 0.0) {
                this.variance = 0.01 * this.meanDate;
            }
        }

        public int compareTo(KernelF o) {
            return Double.compare(meanDate, ((KernelF) o).meanDate);
        }
    }

    private double evalKernelAt(KernelF k, double x) {
        double retval = (1.0 / Math.sqrt(2.0 * Math.PI * k.variance)) //
                * Math.exp(-1.0 * Math.pow((x - k.meanDate), 2) / (2 * k.variance));

        return retval;
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
     * @return the deSelectedFractions
     */
    public Vector<ETFractionInterface> getDeSelectedFractions() {
        return deSelectedFractions;
    }

    /**
     * @param selectedAliquot
     */
    public void setSelectedAliquot(int selectedAliquot) {
        this.selectedAliquotNumber = selectedAliquot;
    }

    /**
     * @return the aliquotOptions
     */
    public Map<String, Map<String, String>> getAliquotOptions() {
        return aliquotOptions;
    }

    /**
     * @param aliquotOptions the aliquotOptions to set
     */
    public void setAliquotOptions(Map<String, Map<String, String>> aliquotOptions) {
        // here we scan the sample and make sure there are aliquot options for each aliquot
        SampleDateInterpretationGUIOptions myOptions = sample.getSampleDateInterpretationGUISettings();
        sample.getActiveAliquots().stream().forEach((a) -> {
            // this finds or creates an aliquotOptions map
            myOptions.getAliquotOptionsMapByName(a.getAliquotName(), ((ReduxAliquotInterface) a).getAliquotNumber());
        });
        this.aliquotOptions = aliquotOptions;
    }

    /**
     * @return the selectedFractions
     */
    public Vector<ETFractionInterface> getSelectedFractions() {
        return selectedFractions;
    }

    /**
     * @return the chosenDateName
     */
    public String getChosenDateName() {
        return chosenDateName;
    }

    /**
     * @param chosenDateName the chosenDateName to set
     */
    public void setChosenDateName(String chosenDateName) {
        this.chosenDateName = chosenDateName;
    }

    /**
     *
     * @param e
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        //  throw new UnsupportedOperationException( "Not supported yet." );
    }

    /**
     *
     * @param evt
     */
    @Override
    public void mousePressed(MouseEvent evt) {
        if (evt.getModifiers() == InputEvent.BUTTON1_MASK) {
            zoomMin = evt.getX();
            this.setCursor(new Cursor((Cursor.HAND_CURSOR)));
            cursorBar = new Line2D.Double();
            cursorLabel = new Rectangle2D.Double();
            mouseInside = true;
        } else {
            // right mouse for zoom
            mouseInside = true;
            // center the date under the mouse
            double trialDisplayOffsetX
                    = //
                    getDisplayOffsetX() //
                    + (convertMouseXToValue(evt.getX()) - convertMouseXToValue((getGraphWidth()) / 2 + getLeftMargin()));
//
//            System.out.println( "ZOOMING:  x val = " + convertMouseXToValue( evt.getX() ) //
//                    + "  min =  " + getMinX_Display()
//                    + "  offset =  " + getDisplayOffsetX() //
//                    + "  max =  " + getMaxX_Display() //
//                    + "  new offset = " + trialDisplayOffsetX );

            if (Math.abs(trialDisplayOffsetX) < DEFAULT_DISPLAY_MAXX) {
                setDisplayOffsetX(trialDisplayOffsetX);

                try {
                    Robot robot = new Robot();
                    int y = (int) evt.getComponent().getLocationOnScreen().getY() + evt.getY();
                    int x = (int) evt.getComponent().getLocationOnScreen().getX() + (getGraphWidth() / 2 + getLeftMargin());
                    mouseInside = false;
                    robot.mouseMove(x, y);
                    robot.mousePress(InputEvent.BUTTON1_MASK);
                    robot.mouseRelease(InputEvent.BUTTON1_MASK);
                } catch (Exception e) {
                }

                zoomIn();
            }

        }
        repaint();

    }

    /**
     *
     * @param evt
     */
    public void mouseReleased(MouseEvent evt) {
        zoomMax = evt.getX();
        this.setCursor(new Cursor((Cursor.DEFAULT_CURSOR)));
    }

    /**
     *
     * @param e
     */
    public void mouseEntered(MouseEvent e) {
        mouseInside = true;
        this.setToolTipText("Right mouse click to center and zoom.");

    }

    /**
     *
     * @param e
     */
    public void mouseExited(MouseEvent e) {
        this.setCursor(new Cursor((Cursor.DEFAULT_CURSOR)));
        mouseInside = false;
        cursorBar = new Line2D.Double();
        cursorLabel = new Rectangle2D.Double();
        this.setToolTipText(null);
        repaint();

    }

    /**
     *
     * @param evt
     */
    public void mouseDragged(MouseEvent evt) {

        if (mouseInside) {
            zoomMax = evt.getX();

            double trialDisplayOffsetX
                    = //
                    getDisplayOffsetX() //
                    + (convertMouseXToValue(zoomMin) - convertMouseXToValue(zoomMax));

//            if ( ((getMinX() + xOffsetValue) >= DEFAULT_DISPLAY_MINX)//
//                    && //
//                    ((getMaxX() + xOffsetValue) <= DEFAULT_DISPLAY_MAXX) ) {
            if (Math.abs(trialDisplayOffsetX) < DEFAULT_DISPLAY_MAXX) {
                setDisplayOffsetX(trialDisplayOffsetX);

                // setDisplayOffsetX( trialDisplayOffsetX );
                zoomMin = zoomMax;
            }

            repaint();
        }
    }

    private double convertMouseXToValue(int x) {
        return //
                ((double) (x - getLeftMargin()) / (double) getGraphWidth()) //
                * getRangeX_Display()//
                + getMinX_Display();
    }

    /**
     *
     * @param e
     */
    public void mouseMoved(MouseEvent e) {
        if (mouseInside) {

            cursorBar = new Line2D.Double(e.getX(), mapY(0.01), e.getX(), mapY(maxY));

            cursorLabel = new Rectangle2D.Double(e.getX() - 13, e.getY() - 12, 26, 12);

            repaint();
        }
    }

    /**
     *
     */
    public void zoomIn() {
        double rangeX = getRangeX_Display();

        if (rangeX >= DEFAULT_DISPLAY_MAXX / 64.0) {//16.0) ) {

            setMinX(getMinX() + rangeX / 8.0);

            setMaxX(getMaxX() - rangeX / 8.0);

            // setting to 0 sends signal to calculate bin count
            setSelectedHistogramBinCount(0);

            repaint();
        }
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
     * @param selectedFractions the selectedFractions to set
     */
    public void setSelectedFractions(Vector<ETFractionInterface> selectedFractions) {
        this.selectedFractions = selectedFractions;
    }

    /**
     * @param deSelectedFractions the deSelectedFractions to set
     */
    public void setDeSelectedFractions(Vector<ETFractionInterface> deSelectedFractions) {
        this.deSelectedFractions = deSelectedFractions;
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
}
