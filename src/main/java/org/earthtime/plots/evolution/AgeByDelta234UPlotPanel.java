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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import org.apache.batik.apps.rasterizer.SVGConverter;
import org.apache.batik.apps.rasterizer.SVGConverterException;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.PlottingDetailsDisplayInterface;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.dataDictionaries.RadDates;
import org.earthtime.dataDictionaries.UThAnalysisMeasures;
import org.earthtime.fractions.ETFractionInterface;
import org.earthtime.plots.AbstractDataView;
import org.earthtime.plots.PlotAxesSetupInterface;
import org.earthtime.reportViews.ReportUpdaterInterface;
import org.earthtime.samples.SampleInterface;
import org.earthtime.utilities.TicGeneratorForAxes;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import org.earthtime.projects.ProjectSample;

/**
 *
 * @author James F. Bowring
 */
public final class AgeByDelta234UPlotPanel extends AbstractDataView implements PlottingDetailsDisplayInterface {

    protected transient ReportUpdaterInterface reportUpdater;

    SampleInterface mySample;

    // maps age to delta
    private Map<Double, Double> upperBoundary;
    private Map<Double, Double> lowerBoundary;

    public AgeByDelta234UPlotPanel(SampleInterface mySample, ReportUpdaterInterface reportUpdater) {
        super();

        this.mySample = mySample;

        this.leftMargin = 40;
        this.topMargin = 40;
        this.graphWidth = 500;
        this.graphHeight = 500;
        this.xLocation = 600;

        this.showMe = true;

        this.setBounds(xLocation, 0, graphWidth + leftMargin * 2, graphHeight + topMargin * 2);

        setOpaque(true);

        setBackground(Color.white);
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

        this.upperBoundary = new TreeMap<>(new UpperBoundaryComparator());
        this.lowerBoundary = new TreeMap<>(new LowerBoundaryComparator());

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
        if (showMe) {
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
                    boolean included
                            = ((ProjectSample) mySample).calculateIfEllipseIncluded(
                                    ageInKa.getValue().doubleValue(),
                                    f.getAnalysisMeasure(UThAnalysisMeasures.delta234Ui.getName()).getValue().doubleValue());
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
                                included ? includedCenterColor : new Color(222, 222, 222),
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

            // paint partitions
            if (upperBoundary.size() > 0) {
                Iterator<Double> upperKeys = upperBoundary.keySet().iterator();
                double saveAge = (double) upperBoundary.keySet().toArray()[0];
                double saveInitDelta = upperBoundary.get(saveAge);
                while (upperKeys.hasNext()) {
                    double age = upperKeys.next();
                    double initDelta = upperBoundary.get(age);
                    g2d.setStroke(new BasicStroke(1.5f));
                    g2d.setColor(Color.blue);
                    g2d.drawRect(//
                            (int) mapX(age) - 2,
                            (int) mapY(initDelta) - 2,
                            4,
                            4);

                    g2d.drawLine(
                            (int) mapX(saveAge),
                            (int) mapY(saveInitDelta),
                            (int) mapX(age),
                            (int) mapY(initDelta));
                    saveAge = age;
                    saveInitDelta = initDelta;
                }
            }

            if (lowerBoundary.size() > 0) {
                Iterator<Double> lowerKeys = lowerBoundary.keySet().iterator();
                double saveAge = (double) lowerBoundary.keySet().toArray()[0];
                double saveInitDelta = lowerBoundary.get(saveAge);
                while (lowerKeys.hasNext()) {
                    double age = lowerKeys.next();
                    double initDelta = lowerBoundary.get(age);
                    g2d.setStroke(new BasicStroke(1.5f));
                    g2d.setColor(Color.CYAN);
                    g2d.drawRect(//
                            (int) mapX(age) - 2,
                            (int) mapY(initDelta) - 2,
                            4,
                            4);

                    g2d.drawLine(
                            (int) mapX(saveAge),
                            (int) mapY(saveInitDelta),
                            (int) mapX(age),
                            (int) mapY(initDelta));
                    saveAge = age;
                    saveInitDelta = initDelta;
                }
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

        // this resets clip bounds
        try {
            drawAxesAndTics(g2d, false);
        } catch (Exception e) {
        }

        reportUpdater.updateEvolutionPlot();

    }

    @Override
    public void preparePanel(boolean doReset) {
        if (doReset) {

            removeAll();
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
                    // if (minX * minY > 0.0) {

                    maxX += getRangeX_Display() / ZOOM_FACTOR;

                    maxY += getRangeY_Display() / ZOOM_FACTOR;
                    //  }

                }

                zoomMinX = zoomMaxX;
                zoomMinY = zoomMaxY;

                ticsYaxis = TicGeneratorForAxes.generateTics(getMinY_Display(), getMaxY_Display(), 10);
                ticsXaxis = TicGeneratorForAxes.generateTics(getMinX_Display(), getMaxX_Display(), 10);

                repaint();
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent evt) {
        super.mousePressed(evt);
        if ((evt.getX() >= leftMargin)
                && (evt.getX() <= graphWidth + leftMargin)
                && (evt.getY() >= topMargin)
                && (evt.getY() <= graphHeight + topMargin)) {

            if (evt.isPopupTrigger() || evt.getButton() == MouseEvent.BUTTON3) {

                putInImageModePan();

                //Create the popup menu.
                JPopupMenu popup = new JPopupMenu();

                // Jan 2011 show coordinates fyi
                double x = convertMouseXToValue(evt.getX());
                double y = convertMouseYToValue(evt.getY());

                if (upperBoundary.containsKey(x)) {
                    JMenuItem menuItem = new JMenuItem("UPPER: Remove Boundary Point for initDelta234U at age = " + Math.round((float) y));
                    menuItem.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent arg0) {
                            upperBoundary.remove(x);
                            repaint();
                        }
                    });
                    popup.add(menuItem);

                    menuItem = new JMenuItem("UPPER: Replace Boundary Point for initDelta234U at age = " + Math.round((float) y));
                    menuItem.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent arg0) {
                            upperBoundary.remove(x);
                            upperBoundary.put(x, y);
                            repaint();
                        }
                    });
                    popup.add(menuItem);
                } else {

                    JMenuItem menuItem = new JMenuItem("UPPER: Create Boundary Point for initDelta234U at age = " + Math.round((float) y));
                    menuItem.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent arg0) {
                            upperBoundary.put(x, y);
                            repaint();
                        }
                    });
                    popup.add(menuItem);
                }
                if (upperBoundary.size() > 0) {
                    JMenuItem menuItem = new JMenuItem("UPPER: Remove Boundary");
                    menuItem.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent arg0) {
                            upperBoundary.clear();
                            repaint();
                        }
                    });
                    popup.add(menuItem);
                }

                if (lowerBoundary.containsKey(x)) {
                    JMenuItem menuItem = new JMenuItem("LOWER: Remove Boundary Point for initDelta234U at age = " + Math.round((float) y));
                    menuItem.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent arg0) {
                            lowerBoundary.remove(x);
                            repaint();
                        }
                    });
                    popup.add(menuItem);

                    menuItem = new JMenuItem("LOWER: Replace Boundary Point for initDelta234U at age = " + Math.round((float) y));
                    menuItem.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent arg0) {
                            lowerBoundary.remove(x);
                            lowerBoundary.put(x, y);
                            repaint();
                        }
                    });
                    popup.add(menuItem);
                } else {

                    JMenuItem menuItem = new JMenuItem("LOWER: Create Boundary Point for initDelta234U at age = " + Math.round((float) y));
                    menuItem.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent arg0) {
                            lowerBoundary.put(x, y);
                            repaint();
                        }
                    });
                    popup.add(menuItem);
                }

                if (lowerBoundary.size() > 0) {
                    JMenuItem menuItem = new JMenuItem("LOWER: Remove Boundary");
                    menuItem.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent arg0) {
                            lowerBoundary.clear();
                            repaint();
                        }
                    });
                    popup.add(menuItem);
                }

                popup.show(evt.getComponent(), evt.getX(), evt.getY());

            }
        }
    }

//    private boolean calculateIfEllipseIncluded(double x, double y) {
//        boolean retVal = true;
//
//        if (upperBoundary.size() > 1 && lowerBoundary.size() > 1) {
//            // build polygon
//            List<Point2D> polygon = new ArrayList<>();
//
//            Iterator<Double> upperKeys = upperBoundary.keySet().iterator();
//            while (upperKeys.hasNext()) {
//                double age = upperKeys.next();
//                double initDelta = upperBoundary.get(age);
//                Point2D point = new Point2D(age, initDelta);
//                polygon.add(point);
//            }
//
//            Iterator<Double> lowerKeys = lowerBoundary.keySet().iterator();
//            while (lowerKeys.hasNext()) {
//                double age = lowerKeys.next();
//                double initDelta = lowerBoundary.get(age);
//                Point2D point = new Point2D(age, initDelta);
//                polygon.add(point);
//            }
//
//            retVal = (math.geom2d.polygon.Polygons2D.windingNumber(polygon, new Point2D(x, y)) != 0);
//        }
//        return retVal;
//    }
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

    @Override
    public void setxLocation(int xLocation) {
        super.setxLocation(xLocation);
        this.setBounds(xLocation, 0, graphWidth + leftMargin * 2, graphHeight + topMargin * 2);
    }

    /**
     * @param upperBoundary the upperBoundary to set
     */
    public void setUpperBoundary(Map<Double, Double> upperBoundary) {
        this.upperBoundary = upperBoundary;
    }

    /**
     * @param lowerBoundary the lowerBoundary to set
     */
    public void setLowerBoundary(Map<Double, Double> lowerBoundary) {
        this.lowerBoundary = lowerBoundary;
    }

}
