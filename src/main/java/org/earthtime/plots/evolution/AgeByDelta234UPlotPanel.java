/*
 * Copyright 2017 CIRDLES.
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
package org.earthtime.plots.evolution;

import Jama.Matrix;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.Path2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.Vector;
import org.apache.batik.apps.rasterizer.SVGConverter;
import org.apache.batik.apps.rasterizer.SVGConverterException;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.PlottingDetailsDisplayInterface;
import org.earthtime.UPb_Redux.valueModels.SampleDateModel;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.dataDictionaries.RadDates;
import org.earthtime.dataDictionaries.UThAnalysisMeasures;
import org.earthtime.dataDictionaries.UThFractionationCorrectedIsotopicRatios;
import org.earthtime.fractions.ETFractionInterface;
import org.earthtime.plots.AbstractDataView;
import org.earthtime.plots.PlotAxesSetupInterface;
import org.earthtime.plots.isochrons.IsochronModel;
import org.earthtime.reportViews.ReportUpdaterInterface;
import org.earthtime.samples.SampleInterface;
import org.earthtime.utilities.TicGeneratorForAxes;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

/**
 *
 * @author James F. Bowring
 */
public final class AgeByDelta234UPlotPanel extends AbstractDataView implements PlottingDetailsDisplayInterface {

    protected transient ReportUpdaterInterface reportUpdater;

    private static SampleInterface sample;
    private static boolean showMatrix = true;

    public AgeByDelta234UPlotPanel(SampleInterface mySample, ReportUpdaterInterface reportUpdater) {
        super();

        this.leftMargin = 40;
        this.topMargin = 40;
        this.graphWidth = 500;
        this.graphHeight = 500;
        xLocation = 600;

        this.setBounds(xLocation, 0, graphWidth + leftMargin * 2, graphHeight + topMargin * 2);

        setOpaque(true);

        setBackground(Color.white);
        this.sample = mySample;
        this.reportUpdater = reportUpdater;

        this.selectedFractions = new Vector<>();
        this.excludedFractions = new Vector<>();

        this.xAxisMax = 0;
        this.yAxisMax = 0;

        this.ticsYaxis = new BigDecimal[0];
        this.ticsXaxis = new BigDecimal[0];

        this.eastResizing = false;
        this.southResizing = false;

        this.zoomCount = 0;

        putInImageModePan();

        this.showCenters = true;
        this.showLabels = false;

        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        paint((Graphics2D) g, false);
    }

    public void paint(Graphics2D g2d, boolean svgStyle) {
        paintInit(g2d);

        //draw component border
        g2d.setPaint(Color.blue);
        g2d.drawRect(0, 0, (int) graphWidth + leftMargin * 2 - 1, (int) graphHeight + topMargin * 2 - 1);

        // draw graph border
        g2d.setPaint(Color.black);
        g2d.drawRect(leftMargin, topMargin, (int) graphWidth - 1, (int) graphHeight - 1);

        g2d.setPaint(Color.black);

        Color includedBorderColor = Color.BLACK;
        Color includedCenterColor = new Color(255, 0, 0);
        float includedCenterSize = 3.0f;
        String ellipseLabelFont = "Monospaced";
        String ellipseLabelFontSize = "12";

        for (ETFractionInterface f : selectedFractions) {
            ValueModel ageInKa = f.getRadiogenicIsotopeDateByName(RadDates.date.getName()).copy();
            ageInKa.setValue(ageInKa.getValue().movePointLeft(3));
            ageInKa.setOneSigma(ageInKa.getOneSigmaAbs().movePointLeft(3));
            if (!f.isRejected()) {
                generateEllipsePathIII(//
                        f,
                        ageInKa,
                        f.getAnalysisMeasure(UThAnalysisMeasures.delta234Ui.getName()),
                        2.0f);
                if (f.getErrorEllipsePath() != null) {
                    plotAFraction(g2d,
                            svgStyle,
                            f,
                            includedBorderColor,
                            0.5f,
                            includedCenterColor,
                            includedCenterSize,
                            ellipseLabelFont,
                            ellipseLabelFontSize,
                            showCenters,
                            showLabels);
                }
            }
        }

        double rangeX = (getMaxX_Display() - getMinX_Display());
        double rangeY = (getMaxY_Display() - getMinY_Display());

        try {
            drawAxesAndTicks(g2d, rangeX, rangeY);
        } catch (Exception e) {
        }

        // draw zoom box if in use
        if (isInImageModeZoom()
                && (Math.abs(zoomMaxX - zoomMinX) * Math.abs(zoomMinY - zoomMaxY)) > 0) {
            g2d.setStroke(new BasicStroke(2.0f));
            g2d.setColor(Color.red);
            g2d.drawRect(//
                    Math.min(zoomMinX, zoomMaxX),
                    Math.min(zoomMaxY, zoomMinY),
                    Math.abs(zoomMaxX - zoomMinX),
                    Math.abs(zoomMinY - zoomMaxY));
        }
    }

    @Override
    public void preparePanel(boolean doReset) {
        if (doReset) {

            removeAll();
//
//            maxX =  xAxisMax;
//            xAxisMax = maxX;
//            maxY =  yAxisMax;
//            yAxisMax = maxY;

            showTight();
        }

        putInImageModePan();
        repaint();
    }

    public void showTight() {
        // determine default zoom  
        zoomMaxX = 0;
        zoomMaxY = 0;
        zoomMinX = 0;
        zoomMinY = 0;

        displayOffsetX = 0.0;
        displayOffsetY = 0.0;
        minX = Double.MAX_VALUE;
        maxX = -Double.MAX_VALUE;
        minY = Double.MAX_VALUE;
        maxY = -Double.MAX_VALUE;
        for (ETFractionInterface f : selectedFractions) {
            if (!f.isRejected()) {
                double xAxisRatio = f.getRadiogenicIsotopeDateByName(RadDates.date.getName()).getValue().movePointLeft(3).doubleValue();
                if (xAxisRatio > 0) {
                    double xAxis2Sigma = f.getRadiogenicIsotopeDateByName(RadDates.date.getName()).getTwoSigmaAbs().movePointLeft(3).doubleValue();
                    minX = Math.min(minX, xAxisRatio - 2.0 * xAxis2Sigma);
                    maxX = Math.max(maxX, xAxisRatio + 2.0 * xAxis2Sigma);

                    double yAxisRatio = f.getAnalysisMeasure(UThAnalysisMeasures.delta234Ui.getName()).getValue().doubleValue();
                    double yAxis2Sigma = f.getAnalysisMeasure(UThAnalysisMeasures.delta234Ui.getName()).getTwoSigmaAbs().doubleValue();
                    minY = Math.min(minY, yAxisRatio - 2.0 * yAxis2Sigma);
                    maxY = Math.max(maxY, yAxisRatio + 2.0 * yAxis2Sigma);
                }
            }
        }

        ticsYaxis = TicGeneratorForAxes.generateTics(getMinY_Display(), getMaxY_Display(), 10);
        ticsXaxis = TicGeneratorForAxes.generateTics(getMinX_Display(), getMaxX_Display(), 10);

        repaint();
        validate();

    }

    private double[][] diag(double zeroZero, double oneOne, double twoTwo) {
        double[][] diag = new double[3][3];
        diag[0][0] = zeroZero;
        diag[1][1] = oneOne;
        diag[2][2] = twoTwo;

        return diag;
    }

    private Matrix matrixA() {
        double[][] matrixAvals = new double[][]//
        {{-lambda238D, 0.0, 0.0},
        {lambda238D, -lambda234D, 0.0},
        {0.0, lambda234D, -lambda230D}};
        return new Matrix(matrixAvals);
    }

    private Matrix matrixQUTh() {
        double[][] matrixQUThvals = new double[][]//
        {{((lambda230D - lambda238D) * (lambda234D - lambda238D)) / (lambda234D * lambda238D), 0.0, 0.0},
        {(lambda230D - lambda238D) / lambda234D, (lambda230D - lambda234D) / lambda234D, 0.0},
        {1.0, 1.0, 1.0}};
        return new Matrix(matrixQUThvals);
    }

    private Matrix matrixGUTh(double t) {
        return new Matrix(diag(Math.exp(-lambda238D * t), Math.exp(-lambda234D * t), Math.exp(-lambda230D * t)));
    }

    private Matrix matrixQinvUTh() {
        double[][] matrixQinvUThvals = new double[][]//
        {{(lambda234D * lambda238D) / ((lambda230D - lambda238D) * (lambda234D - lambda238D)), 0.0, 0.0},
        {-(lambda234D * lambda238D) / ((lambda230D - lambda234D) * (lambda234D - lambda238D)), lambda234D / (lambda230D - lambda234D), 0.0},
        {(lambda234D * lambda238D) / ((lambda230D - lambda234D) * (lambda230D - lambda238D)), -lambda234D / (lambda230D - lambda234D), 1.0}};
        return new Matrix(matrixQinvUThvals);
    }

    private Matrix matrixUTh(double t) {
        return matrixQUTh().times(matrixGUTh(t)).times(matrixQinvUTh());
    }

    private Matrix matrixUTh0(double t) {
        return matrixQUTh().getMatrix(2, 2, 0, 2).times(matrixGUTh(t)).times(matrixQinvUTh()); //For the 230 concentration only (to solve for root)
    }

    private Matrix matrixUTh4(double t) {
        return matrixQUTh().getMatrix(1, 1, 0, 2).times(matrixGUTh(t)).times(matrixQinvUTh()); //For the 234 concentration only (to solve for root)
    }

    /**
     * @param xAxisMax the xAxisMax to set
     */
    public void setxAxisMax(double xAxisMax) {
        this.xAxisMax = xAxisMax;
    }

    /**
     * @param yAxisMax the yAxisMax to set
     */
    public void setyAxisMax(double yAxisMax) {
        this.yAxisMax = yAxisMax;
    }

    /**
     *
     * @param evt
     */
    @Override
    public void mouseMoved(MouseEvent evt) {
        int myX = evt.getX();
        int myY = evt.getY();

        eastResizing = isEastResize(myX);
        southResizing = isSouthResize(myY);

        setCursor(Cursor.getDefaultCursor());
        if (eastResizing ^ southResizing) {
            if (eastResizing) {
                setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
            } else {
                setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
            }
        }

        if (eastResizing && southResizing) {
            setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
        }
    }

    private boolean isEastResize(int myX) {
        return ((myX >= (graphWidth + leftMargin - 2)) && (myX <= (graphWidth + leftMargin + 2)));
    }

    private boolean isSouthResize(int myY) {
        return ((myY >= (graphHeight + leftMargin - 2)) && (myY <= (graphHeight + leftMargin + 2)));
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (mouseInHouse(e)) {

            zoomMaxX = e.getX();
            zoomMaxY = e.getY();

            // https://java.com/en/download/faq/release_changes.xml
            double notches = e.getPreciseWheelRotation();
            if (true) {//(notches == Math.rint(notches)) {
                if (notches < 0) {// zoom in
                    minX += getRangeX_Display() / ZOOM_FACTOR;
                    maxX -= getRangeX_Display() / ZOOM_FACTOR;
                    minY += getRangeY_Display() / ZOOM_FACTOR;
                    maxY -= getRangeY_Display() / ZOOM_FACTOR;

                    zoomCount++;

                } else {// zoom out
                    minX -= getRangeX_Display() / ZOOM_FACTOR;
                    minX = Math.max(minX, 0.0);

                    minY -= getRangeY_Display() / ZOOM_FACTOR;
                    minY = Math.max(minY, 0.0);

                    zoomCount--;
                    // stop zoom out
                    if (minX * minY > 0.0) {

                        maxX += getRangeX_Display() / ZOOM_FACTOR;

                        maxY += getRangeY_Display() / ZOOM_FACTOR;
                    }

                }

                zoomMinX = zoomMaxX;
                zoomMinY = zoomMaxY;

                ticsYaxis = TicGeneratorForAxes.generateTics(getMinY_Display(), getMaxY_Display(), 10);
                ticsXaxis = TicGeneratorForAxes.generateTics(getMinX_Display(), getMaxX_Display(), 10);

                repaint();
            }
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
        paint(svgGenerator, false);

        // Finally, stream out SVG to the standard output using
        // UTF-8 encoding.
        boolean useCSS = true; // we want to use CSS style attributes

        Writer out = null;
        try {
            out = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
        } catch (FileNotFoundException | UnsupportedEncodingException fileNotFoundException) {
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

    @Override
    public void preparePanel(boolean doReScale, boolean inLiveMode) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void refreshPanel(boolean doReScale, boolean inLiveMode) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void resetPanel(boolean doReScale, boolean inLiveMode) {
        refreshPanel(doReScale);
    }

    @Override
    public void setShowTightToEdges(boolean showTightToEdges) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void performZoom(double factor) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PlotAxesSetupInterface getCurrentPlotAxesSetup() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
