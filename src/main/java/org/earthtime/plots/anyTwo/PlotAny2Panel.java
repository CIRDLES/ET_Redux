/*
 * PlotAny2Panel.java
 *
 * Created on March 4, 2008, 3:08 PM
 * Revised for this use Decemebr 2016
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
package org.earthtime.plots.anyTwo;

import Jama.Matrix;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Vector;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import org.apache.batik.apps.rasterizer.SVGConverter;
import org.apache.batik.apps.rasterizer.SVGConverterException;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.cirdles.mcLeanRegression.core.McLeanRegressionLineInterface;
import org.earthtime.UPb_Redux.aliquots.UPbReduxAliquot;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.*;
import org.earthtime.UPb_Redux.dateInterpretation.graphPersistence.AxisSetup;
import org.earthtime.UPb_Redux.dateInterpretation.graphPersistence.DateInterpretationBoxPanel;
import org.earthtime.UPb_Redux.dateInterpretation.graphPersistence.HeatMapLegendPanel;
import org.earthtime.UPb_Redux.dateInterpretation.graphPersistence.TitleBoxPanel;
import org.earthtime.UPb_Redux.user.SampleDateInterpretationGUIOptions;
import org.earthtime.UPb_Redux.valueModels.SampleDateModel;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.aliquots.AliquotForUPbInterface;
import org.earthtime.aliquots.AliquotInterface;
import org.earthtime.aliquots.ReduxAliquotInterface;
import org.earthtime.dataDictionaries.RadRatios;
import org.earthtime.fractions.ETFractionInterface;
import org.earthtime.plots.McLeanRegressionLineFit;
import org.earthtime.plots.PlotAxesSetup;
import org.earthtime.plots.PlotAxesSetupInterface;
import org.earthtime.plots.PlotInterface;
import org.earthtime.reportViews.ReportUpdaterInterface;
import org.earthtime.samples.SampleInterface;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

/**
 *
 * @author James F. Bowring
 */
public class PlotAny2Panel extends JLayeredPane
        implements
        MouseListener,
        MouseMotionListener,
        PlotInterface,
        AliquotDetailsDisplayInterface,
        ConcordiaPlotDisplayInterface,
        PlottingDetailsDisplayInterface {

    // Class Variables
    // Instance Variables
    private SampleInterface sample;
    private Vector<ETFractionInterface> selectedFractions;
    // april 2016
    private Vector<ETFractionInterface> filteredFractions;
    private Vector<ETFractionInterface> excludedFractions;
    private double minT;
    private double maxT;
    private boolean showEllipseCenters;
    private boolean showEllipseLabels;
    private boolean showExcludedEllipses;
    private boolean showFilteredEllipses;
    private Map<String, String> concordiaOptions;
    private Map<String, Map<String, String>> aliquotOptions;
    private Cursor concordiaCursor;    // PAN, ZOOM, EDIT
    private String imageMode;
    private final JPanel concordiaTitlePanel;
    private int savedConcordiaTitlePanelX;
    private int savedConcordiaTitlePanelY;
    private JLayeredPane preferredDatePanel;
    private int savedPreferredDatePanelX;
    private int savedPreferredDatePanelY;
    // special case for intercepts
    private YorkLineFit yorkLineFit;
    private boolean showTitleBox;
    private boolean fadedDeselectedFractions;
    private String concordiaFlavor;
    // May 2010 fields to refine saving and manipulation of views
    private PlotAxesSetupInterface currentPlotAxesSetup;
    private GraphPanelModeChangeI graphPanelModeChanger;

    private boolean showTightToEdges = false;
    // may 2014
    private boolean showBestDateDivider206_238 = false;
    private boolean showingSingleAliquot = false;
    // june 2014
    private boolean useUncertaintyCrosses = false; // ellipses are default
    private JLayeredPane heatMapLegendPanel;
    //oct 2014
    private transient double currentBestDate;
    protected transient AliquotInterface curAliquot;
    private transient boolean changingBestDateDivider;
    private transient ReportUpdaterInterface reportUpdater;

    private String nameOfXaxisSourceValueModel;
    private String nameOfYaxisSourceValueModel;
    private McLeanRegressionLineFit mcLeanRegressionLineFit;

    /**
     * Creates a new instance of ConcordiaGraphPanel
     *
     * @param mySample
     * @param reportUpdater the value of reportUpdater
     */
    public PlotAny2Panel(SampleInterface mySample, ReportUpdaterInterface reportUpdater) {
        super();

        this.sample = mySample;
        this.reportUpdater = reportUpdater;

        setOpaque(true);

        setBackground(Color.white);

        selectedFractions = new Vector<>();
        excludedFractions = new Vector<>();

        this.showEllipseCenters = true;
        this.showEllipseLabels = true;
        this.showExcludedEllipses = true;
        this.showFilteredEllipses = false;

        this.yorkLineFit = null;

        concordiaCursor = Cursor.getDefaultCursor();
        this.imageMode = "PAN";

        this.showTitleBox = true;
        // set up title box
        concordiaTitlePanel = new TitleBoxPanel(sample.getSampleName());
        this.savedConcordiaTitlePanelX = concordiaTitlePanel.getX();
        this.savedConcordiaTitlePanelY = concordiaTitlePanel.getY();

        this.heatMapLegendPanel = new HeatMapLegendPanel("legend");

        this.useUncertaintyCrosses = false;

        changingBestDateDivider = false;

        setViewOptions();

        this.fadedDeselectedFractions = false;

        // default is standard Concordia
        this.concordiaFlavor = "C";

        this.nameOfXaxisSourceValueModel = RadRatios.r206_238r.getName();
        this.nameOfYaxisSourceValueModel = RadRatios.r207_235r.getName();

        addMouseListener(this);
        addMouseMotionListener(this);

    }

    /**
     *
     */
    public void setViewOptions() {
        this.concordiaOptions
                = sample.getSampleDateInterpretationGUISettings().getConcordiaOptions();
        setAliquotOptions(//
                sample.getSampleDateInterpretationGUISettings().getAliquotOptions());

    }

    /**
     *
     * @param g
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        paint((Graphics2D) g, false);
    }

    private double mapX(double x) {

        return getCurrentPlotAxesSetup().mapX(x);
    }

    private double mapY(double y) {

        return getCurrentPlotAxesSetup().mapY(y);
    }

    /**
     *
     * @param factor
     */
    public void performZoom(double factor) {

        // minus factor = zoom out
        double rangeX = getRangeX();
        double rangeY = getRangeY();

        double x = getMaxX() - (rangeX / factor);
        setMinX(getMinX() + rangeX / factor);
        setMinY(getMinY() + rangeY / factor);
        setMaxX(getMaxX() - rangeX / factor);
        setMaxY(getMaxY() - rangeY / factor);
    }

    private String getStringEntryFromConcordiaOptions(String key, String value) {
        if (getConcordiaOptions().containsKey(key)) {
            return getConcordiaOptions().get(key);
        } else {
            return value;
        }
    }

    /**
     *
     * @param myAliquotOptions
     * @param key
     * @param value
     * @return
     */
    public String getStringEntryFromAliquotOptions(
            Map<String, String> myAliquotOptions,
            String key,
            String value) {
        if (myAliquotOptions.containsKey(key)) {
            return myAliquotOptions.get(key);
        } else {
            return value;
        }

    }

    /**
     *
     * @param g2d
     * @param svgStyle
     */
    public void paint(Graphics2D g2d, boolean svgStyle) {

        // http://www.3rd-evolution.de/tkrammer/docs/java_font_size.html
        // http://www.developer.com/net/vb/article.php/626051
        //int screenRes = Toolkit.getDefaultToolkit().getScreenResolution();
        // g2d.scale((double) screenRes / 72, (double) screenRes / 72);
        if (!getCurrentPlotAxesSetup().isDoPlotting()) {
            g2d.drawString("NO VALID DATA!", 200, 300);
            g2d.drawString("Axis ratios must be > 0 and rho must be in [-1,...,1].", 200, 330);
            return; // need to paint no data warning
        }
        // setup painting parameters
        double ellipseSize = 2.0f;
        String ellipseLabelFont = "Monospaced";
        String ellipseLabelFontSize = "12";

        String axesTicLabelFont = "Monospaced";
        String axesTicLabelFontSize = "12";
        String axesLabelFont = "Monospaced";
        String axesLabelFontSize = "20";

        if (getConcordiaOptions().containsKey("useUncertaintyCrosses")) {
            setUseUncertaintyCrosses(Boolean.valueOf(getConcordiaOptions().get("useUncertaintyCrosses")));
        }

        try {
            if (getConcordiaOptions().containsKey("ellipseSize")) {
                ellipseSize
                        = (float) Float.valueOf(getConcordiaOptions().get("ellipseSize"));
            }
        } catch (NumberFormatException numberFormatException) {
        }

        float interceptLineWeight = 1.5f;
        try {
            if (getConcordiaOptions().containsKey("interceptLineWeight")) {
                interceptLineWeight = Float.valueOf(getConcordiaOptions().get("interceptLineWeight"));
            }
        } catch (NumberFormatException numberFormatException) {
        }

        Color interceptLineColor = new Color(0, 0, 0);
        try {
            if (getConcordiaOptions().containsKey("interceptLineColor")) {
                String[] temp
                        = getConcordiaOptions().get("interceptLineColor").split(",");
                interceptLineColor = buildRGBColor(temp);
            }
        } catch (Exception e) {
        }

        String interceptErrorLineStyle
                = getStringEntryFromAliquotOptions(getConcordiaOptions(), "interceptErrorLineStyle", "solid");

        Boolean truncateRegressionCurves
                = Boolean.valueOf(getStringEntryFromAliquotOptions(getConcordiaOptions(), //
                        "truncateRegressionCurves", "false"));

        axesTicLabelFont = getStringEntryFromConcordiaOptions("axesTicLabelFont", axesTicLabelFont);
        axesTicLabelFontSize = getStringEntryFromConcordiaOptions("axesTicLabelFontSize", axesTicLabelFontSize);
        axesLabelFont = getStringEntryFromConcordiaOptions("axesLabelFont", axesLabelFont);
        axesLabelFontSize = getStringEntryFromConcordiaOptions("axesLabelFontSize", axesLabelFontSize);

        if (isShowTitleBox()) {
            ((TitleBoxPanel) concordiaTitlePanel)//
                    .setTitleFont(getStringEntryFromConcordiaOptions(//
                            "titleFont", ((TitleBoxPanel) concordiaTitlePanel).getTitleFont()));
            ((TitleBoxPanel) concordiaTitlePanel)//
                    .setTitleFontSize(getStringEntryFromConcordiaOptions(//
                            "titleFontSize", ((TitleBoxPanel) concordiaTitlePanel).getTitleFontSize()));
            ((TitleBoxPanel) concordiaTitlePanel)//
                    .setSubtitle(getStringEntryFromConcordiaOptions(//
                            "subTitleText", ((TitleBoxPanel) concordiaTitlePanel).getSubtitle()));
            ((TitleBoxPanel) concordiaTitlePanel)//
                    .setTitleBoxShow(Boolean.valueOf(getStringEntryFromConcordiaOptions(//
                            "titleBoxShow", Boolean.toString(((TitleBoxPanel) concordiaTitlePanel).isTitleBoxShow()))));
        }

        ellipseLabelFont = getStringEntryFromConcordiaOptions("ellipseLabelFont", ellipseLabelFont);
        ellipseLabelFontSize = getStringEntryFromConcordiaOptions("ellipseLabelFontSize", ellipseLabelFontSize);

        g2d.setClip(getLeftMargin(), getTopMargin(), (int) getGraphWidth(), (int) getGraphHeight());
        RenderingHints rh = g2d.getRenderingHints();
        rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        rh.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        rh.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHints(rh);

        g2d.setFont(new Font("Monospaced", Font.BOLD, 14));

        // paint de-selected fractions first 
        // paint fills first
        if (showExcludedEllipses) {
            for (ETFractionInterface f : excludedFractions) {
                if (!f.isRejected()) {
                    // determine aliquot for colors etc.
                    String aliquotName = sample.getNameOfAliquotFromSample(f.getAliquotNumber());
                    Map<String, String> myAliquotOptions = getAliquotOptions().get(aliquotName);

                    Color excludedFillColor = new Color(255, 255, 255);
                    if (myAliquotOptions.containsKey("excludedFillColor")) {
                        String[] temp
                                = myAliquotOptions.get("excludedFillColor").split(",");
                        excludedFillColor = buildRGBColor(temp);
                    }

                    float excludedFillTransparencyPCT = 20.0f;
                    if (myAliquotOptions.containsKey("excludedFillTransparencyPCT")) {
                        excludedFillTransparencyPCT = Float.valueOf(myAliquotOptions.get("excludedFillTransparencyPCT"));
                    }

                    // first pass, generate ellipsePath
                    generateEllipsePathIII(//
                            f,
                            ellipseSize);

                    if ((f.getErrorEllipsePath() != null) && !useUncertaintyCrosses) {
                        plotAFractionFillOnly(
                                g2d,
                                svgStyle,
                                f,
                                excludedFillTransparencyPCT / 100.0f,
                                excludedFillColor);
                    }
                }

            }
        }

        // repeat for excluded fractions the borders etc
        if (showExcludedEllipses) {
            for (ETFractionInterface f : excludedFractions) {
                if (!f.isRejected()) {
                    // determine aliquot for colors etc.
                    String aliquotName = sample.getNameOfAliquotFromSample(f.getAliquotNumber());
                    Map<String, String> myAliquotOptions = getAliquotOptions().get(aliquotName);

                    float excludedBorderWeight = 1.5f;
                    if (myAliquotOptions.containsKey("excludedBorderWeight")) {
                        excludedBorderWeight = Float.valueOf(myAliquotOptions.get("excludedBorderWeight"));
                    }

                    Color excludedBorderColor = new Color(0, 0, 0);
                    if (myAliquotOptions.containsKey("excludedBorderColor")) {
                        String[] temp
                                = myAliquotOptions.get("excludedBorderColor").split(",");
                        excludedBorderColor = buildRGBColor(temp);
                    }

                    Color excludedCenterColor = new Color(0, 0, 0);
                    if (myAliquotOptions.containsKey("excludedCenterColor")) {
                        String[] temp
                                = myAliquotOptions.get("excludedCenterColor").split(",");
                        excludedCenterColor = buildRGBColor(temp);
                    }

                    float excludedCenterSize = 3.0f;
                    if (myAliquotOptions.containsKey("excludedCenterSize")) {
                        excludedCenterSize = Float.valueOf(myAliquotOptions.get("excludedCenterSize"));
                    }

                    if (f.getErrorEllipsePath() != null) {
                        plotAFraction(
                                g2d,
                                svgStyle,
                                f,
                                excludedBorderColor,
                                isFadedDeselectedFractions() ? 0.5f : excludedBorderWeight,
                                excludedCenterColor,
                                excludedCenterSize,
                                ellipseLabelFont,
                                ellipseLabelFontSize);

                    }
                }
            }
        }// END excluded FRACTIONS *************************************

        // selected fractions fill only 
        for (ETFractionInterface f : showFilteredEllipses ? getFilteredFractions() : selectedFractions) {
            if (!f.isRejected()) {
                // determine aliquot for colors etc.
                String aliquotName = sample.getNameOfAliquotFromSample(f.getAliquotNumber());
                Map<String, String> myAliquotOptions = getAliquotOptions().get(aliquotName);

                Color includedFillColor = new Color(255, 255, 255);
                if (myAliquotOptions.containsKey("includedFillColor")) {
                    String[] temp
                            = myAliquotOptions.get("includedFillColor").split(",");
                    includedFillColor = buildRGBColor(temp);

                    // april 2014 experiment
                    if (f.getRgbColor() != 0) {
                        includedFillColor = new Color(f.getRgbColor());
                    }
                }

                float includedFillTransparencyPCT = 20.0f;
                if (myAliquotOptions.containsKey("includedFillTransparencyPCT")) {
                    includedFillTransparencyPCT = Float.valueOf(myAliquotOptions.get("includedFillTransparencyPCT"));
                }

                // first pass, generate ellipsePath
                generateEllipsePathIII(//
                        f,
                        ellipseSize);

                if ((f.getErrorEllipsePath() != null) && !useUncertaintyCrosses) {
                    plotAFractionFillOnly(
                            g2d,
                            svgStyle,
                            f,
                            includedFillTransparencyPCT / 100.0f,
                            includedFillColor);

                }
            }
        }

        for (ETFractionInterface f : showFilteredEllipses ? getFilteredFractions() : selectedFractions) {
            if (!f.isRejected()) {
                // determine aliquot for colors etc.
                String aliquotName = sample.getNameOfAliquotFromSample(f.getAliquotNumber());
                Map<String, String> myAliquotOptions = getAliquotOptions().get(aliquotName);

                float includedBorderWeight = 1.5f;
                if (myAliquotOptions.containsKey("includedBorderWeight")) {
                    includedBorderWeight = Float.valueOf(myAliquotOptions.get("includedBorderWeight"));
                }

                Color includedBorderColor = new Color(0, 0, 0);
                if (myAliquotOptions.containsKey("includedBorderColor")) {
                    String[] temp
                            = myAliquotOptions.get("includedBorderColor").split(",");
                    includedBorderColor = buildRGBColor(temp);
                }

                Color includedCenterColor = new Color(0, 0, 0);
                if (myAliquotOptions.containsKey("includedCenterColor")) {
                    String[] temp
                            = myAliquotOptions.get("includedCenterColor").split(",");
                    includedCenterColor = buildRGBColor(temp);
                }

                float includedCenterSize = 3.0f;
                if (myAliquotOptions.containsKey("includedCenterSize")) {
                    includedCenterSize = Float.valueOf(myAliquotOptions.get("includedCenterSize"));
                }

                // april 2014 experiment
                if (f.getRgbColor() != 0) {
                    includedBorderColor = new Color(f.getRgbColor());
                }

                if (f.getErrorEllipsePath() != null) {
                    plotAFraction(
                            g2d,
                            svgStyle,
                            f,
                            includedBorderColor,
                            includedBorderWeight,
                            includedCenterColor,
                            includedCenterSize,
                            ellipseLabelFont,
                            ellipseLabelFontSize);
                }
            }
        }// END SELECTED FRACTIONS *******************************************

        if (selectedFractions.size() > 0) {
            plotMcLeanRegressionLine(g2d, interceptLineWeight, interceptLineColor);
        }

        g2d.setStroke(new BasicStroke(2.0f));

        if (isShowTitleBox()) {
            // paint title box
//            ((TitleBoxPanel) concordiaTitlePanel).paint(g2d);

//             ((HeatMapLegendPanel)heatMapLegendPanel).paint(g2d);
        }

        drawAxesAndTicks(
                g2d,
                axesTicLabelFont,
                axesTicLabelFontSize,
                axesLabelFont,
                axesLabelFontSize);

        // draw zoom box if in use
        if (getImageMode().equalsIgnoreCase("ZOOM")
                && (Math.abs(getZoomMaxX() - getZoomMinX()) * Math.abs(getZoomMinY() - getZoomMaxY())) > 0) {
            g2d.setStroke(new BasicStroke(2.0f));
            g2d.setColor(Color.red);
            g2d.drawRect(//
                    Math.min(getZoomMinX(), getZoomMaxX()),
                    Math.min(getZoomMaxY(), getZoomMinY()),
                    Math.abs(getZoomMaxX() - getZoomMinX()),
                    Math.abs(getZoomMinY() - getZoomMaxY()));
        }
    }

    private Color buildRGBColor(String[] rgbComponents) {
        return new Color(
                (int) Integer.parseInt(rgbComponents[0].trim()),
                (int) Integer.parseInt(rgbComponents[1].trim()),
                (int) Integer.parseInt(rgbComponents[2].trim()));
    }

    /**
     *
     * @param g2d the value of g2d
     */
    private void plotMcLeanRegressionLine(
            Graphics2D g2d,
            float interceptLineWeight,
            Color interceptLineColor) {

        Stroke stroke = new BasicStroke(interceptLineWeight, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
        g2d.setStroke(stroke);
        g2d.setPaint(interceptLineColor);

        McLeanRegressionLineInterface mcLeanRegressionLine = mcLeanRegressionLineFit.getMcLeanRegressionLine();
        if (mcLeanRegressionLine != null) {
            double aXvar = mcLeanRegressionLine.getA()[0][0];
            double aYvar = mcLeanRegressionLine.getA()[1][0];
            double vXvar = mcLeanRegressionLine.getV()[0][0];
            double vYvar = mcLeanRegressionLine.getV()[1][0];
            // plot the line
            Line2D line = new Line2D.Double(
                    mapX(getMinX_Display()),
                    mapY(aYvar + vYvar * getMinX_Display()),
                    mapX(getMaxX_Display()),
                    mapY(aYvar + vYvar * getMaxX_Display()));
            g2d.draw(line);

            if (mcLeanRegressionLine.getSav() != null) {
                Matrix subCov = new Matrix(mcLeanRegressionLine.getSav());
                
                // uncertainty envelope
                Path2D uncertaintyAboveBounds = new Path2D.Double(Path2D.WIND_EVEN_ODD);
                Path2D uncertaintyBelowBounds = new Path2D.Double(Path2D.WIND_EVEN_ODD);

                // line is of form a + vt and for now a0=0, a1=y-intercept, v0=1, v1=slope, so T=x
                // solve for minT and maxT
                minT = getMinX_Display();
                maxT = getMaxX_Display();
                double tIncrement = (getRangeX_Display()) / 250.0;
                StringBuilder csvOutput = new StringBuilder("tstep, xplus, yplus, xminus, yminus\n");
                if (tIncrement > 0.0) {
                    for (double tStep = (0.9 * minT); tStep <= (1.1 * maxT); tStep += tIncrement) {                        
                        double dxda = 0;
                        double dxdb = 0;
                        double dyda = 1;
                        double dydb = tStep;
                        double dxdt = vXvar;
                        double dydt = vYvar;
                        Matrix vperp = new Matrix(new double[][]{{-dydt, dxdt}});
                        Matrix Jxyab = new Matrix(new double[][]{{dxda, dxdb},
                        {dyda, dydb}});
                        double s2perp = vperp.times(Jxyab).times(subCov).times(Jxyab.transpose()).times(vperp.transpose()).get(0, 0) / vperp.times(vperp.transpose()).get(0, 0);
                        double xv = 2.0 * StrictMath.cos(StrictMath.atan(-dxdt / dydt)) * StrictMath.sqrt(s2perp);
                        double yv = 2 * StrictMath.sin(StrictMath.atan(-dxdt / dydt)) * StrictMath.sqrt(s2perp);

                        double xplus = aXvar + vXvar * tStep + xv;
                        double yplus = aYvar + vYvar * tStep + yv;
                        double xminus = aXvar + vXvar * tStep - xv;
                        double yminus = aYvar + vYvar * tStep - yv;

                        // test bounds
                        if (isInVisibleBounds(xplus, yplus) && isInVisibleBounds(xminus, yminus)) {
                            csvOutput.append(tStep).append(", ");
                            csvOutput.append(xplus).append(", ");
                            csvOutput.append(yplus).append(", ");
                            csvOutput.append(xminus).append(", ");
                            csvOutput.append(yminus).append(", ");
                            csvOutput.append("\n");

                            if (uncertaintyAboveBounds.getCurrentPoint() == null) {
                                uncertaintyAboveBounds.moveTo(
                                        (float) mapX(xplus),
                                        (float) mapY(yplus));
                                uncertaintyBelowBounds.moveTo(
                                        (float) mapX(xminus),
                                        (float) mapY(yminus));
                            } else {
                                uncertaintyAboveBounds.lineTo(
                                        (float) mapX(xplus),
                                        (float) mapY(yplus));
                                uncertaintyBelowBounds.lineTo(
                                        (float) mapX(xminus),
                                        (float) mapY(yminus));
                            }
                        }
                    }//tStep iteration

//                float dash1[] = {10.0f}; 
                    float[] dash1 = {6.0f, 4.0f, 2.0f, 4.0f, 2.0f, 4.0f};
                    stroke = new BasicStroke(interceptLineWeight, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, dash1, 0.0f);
                    g2d.setStroke(stroke);
                    g2d.setPaint(Color.red);

                    g2d.draw(uncertaintyAboveBounds);
                    g2d.draw(uncertaintyBelowBounds);

//                    // output to csv for testing with matlab
//                    Path path = Paths.get(nameOfXaxisSourceValueModel + "_" + nameOfYaxisSourceValueModel + "UNCT.csv");
//                    try (BufferedWriter writer = Files.newBufferedWriter(path, Charset.forName("UTF-8"))) {
//                        writer.write(csvOutput.toString());
//                    } catch (IOException ex) {
//                    }
                }
            }
        }
    }

    private boolean isInVisibleBounds(double x, double y) {
        return (x >= 0.9 * getMinX()) && (x <= 1.1 * getMaxX())
                && (y >= 0.9 * getMinY()) && (y <= 1.1 * getMaxY());
    }

    private void plotAFraction(
            Graphics2D g2d,
            boolean svgStyle,
            ETFractionInterface fraction,
            Color borderColor,
            float borderWeight,
            Color centerColor,
            float centerSize,
            String ellipseLabelFont,
            String ellipseLabelFontSize) {

        Path2D ellipse = fraction.getErrorEllipsePath();
        if (svgStyle) {
            // generate file if necessary to handle weakness in Batik
        } else {
            // draw ellipse
            g2d.setStroke(new BasicStroke(borderWeight));
            g2d.setPaint(borderColor);
            g2d.draw(ellipse);
        }

        // draw ellipse centers
        if (isShowEllipseCenters() && !useUncertaintyCrosses) {

            float centerXbox = (float) (ellipse.getBounds().x + ellipse.getBounds().width / 2.0 - centerSize / 2.0);
            float centerYbox = (float) (ellipse.getBounds().y + ellipse.getBounds().height / 2.0 - centerSize / 2.0);

            Ellipse2D fractionbox = new Ellipse2D.Double(
                    centerXbox,
                    centerYbox,
                    centerSize,
                    centerSize);
            g2d.setPaint(centerColor);
            g2d.setStroke(new BasicStroke(1.0f));
            g2d.fill(fractionbox);
        } else if (isShowEllipseCenters()){
            ValueModel[] xyRho = fraction.retrieveXYRho(nameOfXaxisSourceValueModel, nameOfYaxisSourceValueModel);
            ValueModel xAxisRatio = xyRho[0];
            ValueModel yAxisRatio = xyRho[1];

            Ellipse2D fractionbox = new Ellipse2D.Double(
                    mapX(xAxisRatio.getValue().doubleValue()) - centerSize / 2.0,
                    mapY(yAxisRatio.getValue().doubleValue()) - centerSize / 2.0,
                    centerSize,
                    centerSize);
            g2d.setPaint(centerColor);
            g2d.setStroke(new BasicStroke(1.0f));
            g2d.fill(fractionbox);
        }

        if (isShowEllipseLabels()) {
            g2d.setPaint(borderColor);
            g2d.setFont(new Font(
                    ellipseLabelFont,
                    Font.BOLD,
                    Integer.parseInt(ellipseLabelFontSize)));

            // locate label based on tilt of ellipse as represented by rho
            float labelY;
            float labelX;
            if (fraction.getEllipseRho() < 0) {
                labelY = (float) (ellipse.getBounds().getY() //
                        + ellipse.getBounds().getHeight() - (1.0 + fraction.getEllipseRho()) * ellipse.getBounds().getHeight() / 4.0 + 15f);
                labelX = (float) (ellipse.getBounds().getX() + ellipse.getBounds().getWidth() - 6f);
            } else {
                labelY = (float) (ellipse.getBounds().getY() //
                        + (1.0 - fraction.getEllipseRho()) * ellipse.getBounds().getHeight() / 2.0);
                labelX = (float) (ellipse.getBounds().getX() + ellipse.getBounds().getWidth() + 2f);
            }

            g2d.drawString(fraction.getFractionID(), labelX, labelY);
        }

    }

    private void generateEllipsePathIII(
            ETFractionInterface fraction,
            double ellipseSize) {

        ValueModel[] xyRho = fraction.retrieveXYRho(nameOfXaxisSourceValueModel, nameOfYaxisSourceValueModel);
        ValueModel xAxisRatio = xyRho[0];
        ValueModel yAxisRatio = xyRho[1];
        ValueModel correlationCoefficient = xyRho[2];

        if (xAxisRatio != null && yAxisRatio != null) {
            Path2D ellipse = new Path2D.Double(Path2D.WIND_NON_ZERO);

            double aspectRatio = ((getRangeY_Display() / (double) getGraphHeight()) / (getRangeX_Display() / (double) getGraphWidth()));

            if ((correlationCoefficient.getValue().doubleValue() >= -1.0)
                    && (correlationCoefficient.getValue().doubleValue() <= 1.0)) {
                if (!useUncertaintyCrosses
                        && xAxisRatio.hasPositiveVarUnct()
                        && yAxisRatio.hasPositiveVarUnct()) {
                    ErrorEllipse ee = new ErrorEllipse(
                            xAxisRatio,
                            yAxisRatio,
                            correlationCoefficient,
                            aspectRatio,
                            ellipseSize);

                    int pointCount = 13;

                    Matrix ellipseXY = ee.getEllipseControlPoints();

                    //ellipse = new Path2D.Double ( Path2D.WIND_NON_ZERO );
                    ellipse.moveTo(
                            mapX(ellipseXY.get(0, 0)),
                            mapY(ellipseXY.get(0, 1)));

                    for (int i = 1; i < pointCount; i += 3) {
                        ellipse.curveTo(
                                mapX(ellipseXY.get(i, 0)),
                                mapY(ellipseXY.get(i, 1)),
                                mapX(ellipseXY.get(i + 1, 0)),
                                mapY(ellipseXY.get(i + 1, 1)),
                                mapX(ellipseXY.get(i + 2, 0)),
                                mapY(ellipseXY.get(i + 2, 1)));
                    }
                    ellipse.closePath();

                } else {

                    useUncertaintyCrosses = true;
                    // june 2014 play with crosses
                    double xValue = xAxisRatio.getValue().doubleValue();
                    double yValue = yAxisRatio.getValue().doubleValue();
                    double xOneSigmaAbs = xAxisRatio.getOneSigmaAbs().doubleValue();
                    double yOneSigmaAbs = yAxisRatio.getOneSigmaAbs().doubleValue();

                    ellipse = new Path2D.Double(Path2D.WIND_NON_ZERO);

                    // y-tip
                    int halfTip = (yOneSigmaAbs > 0) ? 5 : 0;

                    ellipse.moveTo(
                            mapX(xValue) - halfTip,
                            mapY(yValue - 2 * yOneSigmaAbs));
                    ellipse.lineTo(
                            mapX(xValue) + halfTip,
                            mapY(yValue - 2 * yOneSigmaAbs));
                    // y-bar
                    ellipse.moveTo(
                            mapX(xValue),
                            mapY(yValue - 2 * yOneSigmaAbs));
                    ellipse.lineTo(
                            mapX(xValue),
                            mapY(yValue + 2 * yOneSigmaAbs));
                    //y-tip
                    ellipse.moveTo(
                            mapX(xValue) - halfTip,
                            mapY(yValue + 2 * yOneSigmaAbs));
                    ellipse.lineTo(
                            mapX(xValue) + halfTip,
                            mapY(yValue + 2 * yOneSigmaAbs));

                    // x-tip
                    halfTip = (xOneSigmaAbs > 0) ? 5 : 0;
                    ellipse.moveTo(
                            mapX(xValue - 2 * xOneSigmaAbs),
                            mapY(yValue) - halfTip);
                    ellipse.lineTo(
                            mapX(xValue - 2 * xOneSigmaAbs),
                            mapY(yValue) + halfTip);

                    // x-bar
                    ellipse.moveTo(
                            mapX(xValue - 2 * xOneSigmaAbs),
                            mapY(yValue));
                    ellipse.lineTo(
                            mapX(xValue + 2 * xOneSigmaAbs),
                            mapY(yValue));

                    // x-tip
                    ellipse.moveTo(
                            mapX(xValue + 2 * xOneSigmaAbs),
                            mapY(yValue) - halfTip);
                    ellipse.lineTo(
                            mapX(xValue + 2 * xOneSigmaAbs),
                            mapY(yValue) + halfTip);

                }

                // june 2010 if any part of bounds in view, then display
                if (ellipse.getBounds().intersects(//
                        getLeftMargin() - 1, getTopMargin() - 1, (int) getGraphWidth() + 2, (int) getGraphHeight() + 2)) {

                    fraction.setErrorEllipsePath(ellipse);
                    // used for placing ellipse label
                    fraction.setEllipseRho(correlationCoefficient.getValue().doubleValue());

                } else {
                    fraction.setErrorEllipsePath(null);
                }
            } else {

                // dec 2015 due to downhole and intercept flavors
                fraction.setErrorEllipsePath(null);
            }
        }

    }

    /**
     * This method allows the painting of fills first and borders later for a
     * better look
     */
    private void plotAFractionFillOnly(
            Graphics2D g2d, boolean svgStyle,
            ETFractionInterface f,
            float transparency,
            Color fillColor) {

        Path2D ellipse = f.getErrorEllipsePath();
        if (svgStyle) {
            // generate file if necessary to handle weakness in Batik
        } else {
            // draw ellipse
            // http://www.informit.com/articles/article.aspx?errorEnvelope=26349&seqNum=5
            Composite originalComposite = g2d.getComposite();
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, transparency));
            g2d.setPaint(fillColor);
            g2d.fill(ellipse);
            //restore composite
            g2d.setComposite(originalComposite);
        }

    }

    private void drawAxesAndTicks(
            Graphics2D g2d,
            String axesTicLabelFont,
            String axesTicLabelFontSize,
            String axesLabelFont,
            String axesLabelFontSize) {

        // reset the clip bounds to paint axis and numbers
        g2d.setClip(0, 0, getWidth(), getHeight());

        g2d.setFont(new Font(
                axesTicLabelFont,
                Font.BOLD,
                Integer.parseInt(axesTicLabelFontSize)));

        g2d.setPaint(Color.BLACK);
        g2d.setStroke(new BasicStroke(2.0f));

        Font axisLabelAtomicNum = new Font(axesLabelFont, Font.BOLD, Integer.valueOf(axesLabelFontSize) - 6);
        Font axisLabelAtomicName = new Font(axesLabelFont, Font.BOLD, Integer.valueOf(axesLabelFontSize));

        getCurrentPlotAxesSetup().plotXYaxes(//
                g2d, axisLabelAtomicNum, axisLabelAtomicName, false, false, false);
    }

    /**
     *
     * @param doReScale the value of doReScale
     * @param inLiveMode the value of inLiveMode
     */
    @Override
    public void resetPanel(boolean doReScale, boolean inLiveMode) {

        if (doReScale) {
            setDisplayOffsetX(0.0);
            setMinX(0);
            setMaxX(0);
            setZoomMaxX(0);
            setZoomMinX(0);

            setDisplayOffsetY(0.0);
            setMinY(0);
            setMaxY(0);
            setZoomMaxY(0);
            setZoomMinY(0);
        }

        currentPlotAxesSetup.setUseAutomaticAxisTics(true);
        try {
            graphPanelModeChanger.switchToPanMode();
        } catch (Exception e) {
        } finally {
            refreshPanel(doReScale, inLiveMode);
        }
    }

    /**
     *
     * @param doReScale the value of doReScale
     * @param inLiveMode the value of inLiveMode
     */
    @Override
    public void refreshPanel(boolean doReScale, boolean inLiveMode) {
        preparePanel(doReScale, inLiveMode);
        repaint();
    }

    /**
     *
     */
    @Override
    public void determineCurrentAliquot() {
        try {
            curAliquot = sample.getAliquotByNumber(selectedFractions.get(0)//
                    .getAliquotNumber());

            currentBestDate = ((AliquotForUPbInterface) curAliquot).getBestAgeDivider206_238().doubleValue();

        } catch (Exception e) {
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

        useUncertaintyCrosses = false; // gets set true by ellipses with bad uncert

        if (!getCurrentPlotAxesSetup().isDoPlotting()
                ||//
                (getMinX() + getMaxX() + getMinY() + getMaxY()) == 0) {
            if ((selectedFractions.size() + excludedFractions.size()) > 0) {
                getCurrentPlotAxesSetup().setDoPlotting(false);
            }

            if (doReScale) {
                setMinX(Double.MAX_VALUE);
                setMaxX(-Double.MAX_VALUE);

                setMinY(Double.MAX_VALUE);
                setMaxY(-Double.MAX_VALUE);
            }

            ValueModel xAxisRatio;
            ValueModel yAxisRatio;
            ValueModel correlationCoefficient;

            for (ETFractionInterface fraction : showFilteredEllipses ? getFilteredFractions() : selectedFractions) {

                if (!fraction.isRejected()) {
                    ValueModel[] xyRho = fraction.retrieveXYRho(nameOfXaxisSourceValueModel, nameOfYaxisSourceValueModel);
                    xAxisRatio = xyRho[0];
                    yAxisRatio = xyRho[1];
                    correlationCoefficient = xyRho[2];

                    if (xAxisRatio != null && yAxisRatio != null) {

                        if ((xAxisRatio.hasPositiveValue())
                                && (yAxisRatio.hasPositiveValue())
                                && (correlationCoefficient.getValue().doubleValue() >= -1.0)
                                && (correlationCoefficient.getValue().doubleValue() <= 1.0)) {
                            // June 2010 added check for correlation coeff

                            getCurrentPlotAxesSetup().setDoPlotting(true);

                            // dec 2016 check for bad uncertainties
                            if (xAxisRatio.hasPositiveVarUnct()
                                    && yAxisRatio.hasPositiveVarUnct()) {
                                ErrorEllipse ee = new ErrorEllipse(
                                        xAxisRatio,
                                        yAxisRatio,
                                        correlationCoefficient,
                                        getGraphHeight() / getGraphWidth(),
                                        2.5);

                                if (doReScale) {
                                    if (ee.getbezierMinX() < getMinX()) {
                                        setMinX(ee.getbezierMinX());
                                    }

                                    if (ee.getbezierMaxX() > getMaxX()) {
                                        setMaxX(ee.getbezierMaxX());
                                    }
                                    if (ee.getbezierMinY() < getMinY()) {
                                        setMinY(ee.getbezierMinY());
                                    }

                                    if (ee.getbezierMaxY() > getMaxY()) {
                                        setMaxY(ee.getbezierMaxY());
                                    }
                                }
                            } else {
                                if (doReScale) {
                                    if (xAxisRatio.getValue().doubleValue() < getMinX()) {
                                        setMinX(xAxisRatio.getValue().doubleValue());
                                    }

                                    if (xAxisRatio.getValue().doubleValue() > getMaxX()) {
                                        setMaxX(xAxisRatio.getValue().doubleValue());
                                    }
                                    if (yAxisRatio.getValue().doubleValue() < getMinY()) {
                                        setMinY(yAxisRatio.getValue().doubleValue());
                                    }

                                    if (yAxisRatio.getValue().doubleValue() > getMaxY()) {
                                        setMaxY(yAxisRatio.getValue().doubleValue());
                                    }
                                }
                            }
                        } else {
                            // inconsistent data
                        }
                    }
                }
            }

            if (showExcludedEllipses) {
                for (ETFractionInterface fraction : excludedFractions) {
                    if (!fraction.isRejected()) {

                        ValueModel[] xyRho = fraction.retrieveXYRho(nameOfXaxisSourceValueModel, nameOfYaxisSourceValueModel);
                        xAxisRatio = xyRho[0];
                        yAxisRatio = xyRho[1];
                        correlationCoefficient = xyRho[2];

                        if (xAxisRatio != null && yAxisRatio != null) {

                            if ((xAxisRatio.hasPositiveValue())
                                    && (yAxisRatio.hasPositiveValue())
                                    && (correlationCoefficient.getValue().doubleValue() >= -1.0)
                                    && (correlationCoefficient.getValue().doubleValue() <= 1.0)) {
                                // June 2010 added check for correlation coeff

                                getCurrentPlotAxesSetup().setDoPlotting(true);

                                // dec 2016 check for bad uncertainties
                                if (xAxisRatio.hasPositiveVarUnct()
                                        && yAxisRatio.hasPositiveVarUnct()) {
                                    ErrorEllipse ee = new ErrorEllipse(
                                            xAxisRatio,
                                            yAxisRatio,
                                            correlationCoefficient,
                                            getGraphHeight() / getGraphWidth(),
                                            2.5);

                                    if (doReScale) {
                                        if (ee.getbezierMinX() < getMinX()) {
                                            setMinX(ee.getbezierMinX());
                                        }

                                        if (ee.getbezierMaxX() > getMaxX()) {
                                            setMaxX(ee.getbezierMaxX());
                                        }
                                        if (ee.getbezierMinY() < getMinY()) {
                                            setMinY(ee.getbezierMinY());
                                        }

                                        if (ee.getbezierMaxY() > getMaxY()) {
                                            setMaxY(ee.getbezierMaxY());
                                        }
                                    }
                                } else {
                                    if (doReScale) {
                                        if (xAxisRatio.getValue().doubleValue() < getMinX()) {
                                            setMinX(xAxisRatio.getValue().doubleValue());
                                        }

                                        if (xAxisRatio.getValue().doubleValue() > getMaxX()) {
                                            setMaxX(xAxisRatio.getValue().doubleValue());
                                        }
                                        if (yAxisRatio.getValue().doubleValue() < getMinY()) {
                                            setMinY(yAxisRatio.getValue().doubleValue());
                                        }

                                        if (yAxisRatio.getValue().doubleValue() > getMaxY()) {
                                            setMaxY(yAxisRatio.getValue().doubleValue());
                                        }
                                    }
                                }
                            } else {
                                // inconsistent data
                            }

                        }
                    }
                }

            }

            // test for constant
            if (getMinX() == getMaxX()) {
                double constant = getMinX();
                setMinX(0.9 * constant);
                setMaxX(1.1 * constant);
            }

            if (getMinY() == getMaxY()) {
                double constant = getMinY();
                setMinY(0.9 * constant);
                setMaxY(1.1 * constant);
            }
            
            if (!showTightToEdges) {
                performZoom(-2.0);
                // performZoom(-2.0);
            }
        }

        // oct 2014
        setImageMode("PAN");

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
            System.out.println(sVGGraphics2DIOException.getMessage());
        }

        // aug 2013
        // read svg file back in to add clip size to comments
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
    @Override
    public Vector<ETFractionInterface> getSelectedFractions() {
        return selectedFractions;
    }

    /**
     *
     * @param fractions
     */
    @Override
    public void setSelectedFractions(Vector<ETFractionInterface> fractions) {
        this.selectedFractions = fractions;
        if (filteredFractions == null) {
            filteredFractions = fractions;
        }
        fitMcLeanRegression();
    }

    /**
     *
     */
    public void fitMcLeanRegression() {
        mcLeanRegressionLineFit = new McLeanRegressionLineFit(selectedFractions, nameOfXaxisSourceValueModel, nameOfYaxisSourceValueModel);
    }

    /**
     *
     * @return
     */
    public int getTopMargin() {
        return getCurrentPlotAxesSetup().getTopMargin();
    }

    /**
     *
     * @param topMargin
     */
    public void setTopMargin(int topMargin) {
        getCurrentPlotAxesSetup().setTopMargin(topMargin);
    }

    /**
     *
     * @return
     */
    public int getLeftMargin() {
        return getCurrentPlotAxesSetup().getLeftMargin();
    }

    /**
     *
     * @param leftMargin
     */
    public void setLeftMargin(int leftMargin) {
        getCurrentPlotAxesSetup().setLeftMargin(leftMargin);
    }

    /**
     *
     * @return
     */
    public SampleInterface getSample() {
        return sample;
    }

    /**
     *
     * @param sample
     */
    public void setSample(SampleInterface sample) {
        this.sample = sample;
    }

    /**
     *
     * @return
     */
    public double getMinX_Display() {
        return getCurrentPlotAxesSetup().getXaxisSetup().getMin_Display();//           minX + displayOffsetX;
    }

    /**
     *
     * @param minX
     */
    public void setMinX(double minX) {
        if (minX < 0.0) {
            minX = 0.0;
        }
        //this.minX = minX;
        getCurrentPlotAxesSetup().getXaxisSetup().setMin(minX);
    }

    /**
     *
     * @return
     */
    public double getMaxX_Display() {
        return getCurrentPlotAxesSetup().getXaxisSetup().getMax_Display();//    maxX + displayOffsetX;
    }

    /**
     *
     * @param maxX
     */
    public void setMaxX(double maxX) {
        //this.maxX = maxX;
        getCurrentPlotAxesSetup().getXaxisSetup().setMax(maxX);
    }

    /**
     *
     * @return
     */
    public double getMinY_Display() {
        return getCurrentPlotAxesSetup().getYaxisSetup().getMin_Display();//    minY + displayOffsetY;
    }

    /**
     *
     * @param minY
     */
    public void setMinY(double minY) {
        if (minY < 0.0) {
            minY = 0.0;
        }
        //this.minY = minY;
        getCurrentPlotAxesSetup().getYaxisSetup().setMin(minY);
    }

    /**
     *
     * @return
     */
    public double getMaxY_Display() {
        return getCurrentPlotAxesSetup().getYaxisSetup().getMax_Display();//   maxY + displayOffsetY;
    }

    /**
     *
     * @param maxY
     */
    public void setMaxY(double maxY) {
        //this.maxY = maxY;
        getCurrentPlotAxesSetup().getYaxisSetup().setMax(maxY);
    }

    /**
     *
     * @return
     */
    public double getDisplayOffsetY() {
        return getCurrentPlotAxesSetup().getYaxisSetup().getDisplayOffset();//  displayOffsetY;
    }

    /**
     *
     * @param displayOffsetY
     */
    public void setDisplayOffsetY(double displayOffsetY) {
        // System.out.println("displayOffsetY  " + displayOffsetY);
        //this.displayOffsetY = displayOffsetY;
        getCurrentPlotAxesSetup().getYaxisSetup().setDisplayOffset(displayOffsetY);
    }

    /**
     *
     * @return
     */
    public double getDisplayOffsetX() {
        return getCurrentPlotAxesSetup().getXaxisSetup().getDisplayOffset();//  displayOffsetX;
    }

    /**
     *
     * @param displayOffsetX
     */
    public void setDisplayOffsetX(double displayOffsetX) {
        // System.out.println("displayOffsetX  " + displayOffsetX);
        //this.displayOffsetX = displayOffsetX;
        getCurrentPlotAxesSetup().getXaxisSetup().setDisplayOffset(displayOffsetX);
    }

    /**
     *
     * @return
     */
    public double getRangeX() {
        return getCurrentPlotAxesSetup().getXaxisSetup().getRange_Display();//       maxX - minX;
    }

    /**
     *
     * @return
     */
    public double getRangeY() {
        return getCurrentPlotAxesSetup().getYaxisSetup().getRange_Display();//   maxY - minY;
    }

    /**
     *
     * @return
     */
    public double getRangeX_Display() {
        return getCurrentPlotAxesSetup().getXaxisSetup().getRange_Display();//     getMaxX_Display() - getMinX_Display();
    }

    /**
     *
     * @return
     */
    public double getRangeY_Display() {
        return getCurrentPlotAxesSetup().getYaxisSetup().getRange_Display();//   getMaxY_Display() - getMinY_Display();
    }

    /**
     *
     * @return
     */
    public double getMinX() {
        return getCurrentPlotAxesSetup().getXaxisSetup().getMin(); // minX;
    }

    /**
     *
     * @return
     */
    public double getMinY() {
        return getCurrentPlotAxesSetup().getYaxisSetup().getMin();//   minY;
    }

    /**
     *
     * @return
     */
    public double getMaxX() {
        return getCurrentPlotAxesSetup().getXaxisSetup().getMax();//    maxX;
    }

    /**
     *
     * @return
     */
    public double getMaxY() {
        return getCurrentPlotAxesSetup().getYaxisSetup().getMax();// maxY;
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
        if (minT < 0.0) {
            minT = 0.0;
        }
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

    /**
     *
     * @return
     */
    public boolean isShowEllipseCenters() {
        return showEllipseCenters;
    }

    /**
     *
     * @param showEllipseCenters
     */
    public void setShowEllipseCenters(boolean showEllipseCenters) {
        this.showEllipseCenters = showEllipseCenters;
    }

    /**
     *
     * @return
     */
    public boolean isShowEllipseLabels() {
        return showEllipseLabels;
    }

    /**
     *
     * @param showEllipseLabels
     */
    public void setShowEllipseLabels(boolean showEllipseLabels) {
        this.showEllipseLabels = showEllipseLabels;
    }

    /**
     *
     * @return
     */
    @Override
    public Vector<ETFractionInterface> getDeSelectedFractions() {
        return excludedFractions;
    }

    /**
     *
     * @param deSelectedFractions
     */
    @Override
    public void setDeSelectedFractions(Vector<ETFractionInterface> deSelectedFractions) {
        this.excludedFractions = deSelectedFractions;
    }

    /**
     *
     * @return
     */
    @Override
    public Map<String, String> getConcordiaOptions() {
        if (concordiaOptions == null) {
            concordiaOptions = new SampleDateInterpretationGUIOptions().getConcordiaOptions();
        }
        return concordiaOptions;
    }

    /**
     *
     * @param concordiaOptions
     */
    @Override
    public void setConcordiaOptions(Map<String, String> concordiaOptions) {
        this.concordiaOptions = concordiaOptions;
    }

    // Mouse events ************************************************************
    /**
     *
     * @param evt
     */
    @Override
    public void mouseClicked(MouseEvent evt) {
        // use mousepressed so can trap events with one-button mac mouse much more easily
    }

    /**
     *
     * @param evt
     */
    @Override
    public void mousePressed(MouseEvent evt) {
        if ((evt.getX() >= getLeftMargin())
                && (evt.getY() >= getTopMargin())
                && (evt.getY() <= getGraphHeight() + getTopMargin())) {

            if (evt.isPopupTrigger() || evt.getButton() == MouseEvent.BUTTON3) {

                // reset to pan mode
                graphPanelModeChanger.switchToPanMode();

                // may 2014
                // check if aliquot vs sample
            } else {

                setZoomMinX(evt.getX());
                setZoomMinY(evt.getY());
            }
        } else {

        }
    }

    /**
     *
     * @param evt
     */
    @Override
    public void mouseReleased(MouseEvent evt) {

        if ((evt.getX() >= getLeftMargin())
                && (evt.getY() >= getTopMargin())
                && (evt.getY() <= getGraphHeight() + getTopMargin()) && !changingBestDateDivider) {

            setZoomMaxX(evt.getX());
            setZoomMaxY(evt.getY());

            if (getImageMode().equalsIgnoreCase("ZOOM") //
                    && (getZoomMaxX() != getZoomMinX()) //using != provides for legal inverting of zoom box
                    && (getZoomMaxY() != getZoomMinY())) {

                // may 2010 check for bad zooms
                double zMinX = convertMouseXToValue(Math.min(getZoomMinX(), getZoomMaxX()));
                double zMinY = convertMouseYToValue(Math.max(getZoomMinY(), getZoomMaxY()));
                // hack to correct a bug in the reported x,y from mouse release vs mouse click
                // june 2010 removed this hack
                double zMaxX = convertMouseXToValue(Math.max(getZoomMaxX(), getZoomMinX()) - 0 * 65);
                double zMaxY = convertMouseYToValue(Math.min(getZoomMaxY(), getZoomMinY()) + 0 * 60);

                if ((zMaxX > zMinX) && (zMaxY > zMinY)) {
                    setMinX(zMinX);
                    setMinY(zMinY);
                    setMaxX(zMaxX);
                    setMaxY(zMaxY);

                    setDisplayOffsetX(0.0);
                    setDisplayOffsetY(0.0);

                    setZoomMaxX(getZoomMinX());
                    setZoomMaxY(getZoomMinY());

                    // may 2010
                    currentPlotAxesSetup.setUseAutomaticAxisTics(true);
                    graphPanelModeChanger.switchToPanMode();

                    repaint();
                }
            }
        } else // set best age divider
        {
            if (changingBestDateDivider) {
                ((AliquotForUPbInterface) curAliquot).setBestAgeDivider206_238(new BigDecimal(currentBestDate));
                ((UPbReduxAliquot) curAliquot).updateBestAge();
                reportUpdater.updateReportTable(false, false, "");
            }
        }

        changingBestDateDivider = false;

    }

    /**
     *
     * @param evt
     */
    @Override
    public void mouseEntered(MouseEvent evt) {
//        // this is not very sensitive, so have forced cursor at mode selection below
//        if ((evt.getX() >= getLeftMargin())
//                && (evt.getY() >= getTopMargin())
//                && (evt.getY() <= getGraphHeight() + getTopMargin())) {
//            setCursor(getConcordiaCursor());
//        }
    }

    /**
     *
     * @param evt
     * @return
     */
    public boolean mouseEnteredTitleBox(MouseEvent evt) {
        boolean retval = false;
        if (isShowTitleBox()) {
            // this is not very sensitive, so have forced cursor at mode selection below
            if ((evt.getX() >= concordiaTitlePanel.getX())
                    && (evt.getX() <= (concordiaTitlePanel.getX() + concordiaTitlePanel.getWidth()))
                    && (evt.getY() >= concordiaTitlePanel.getY())
                    && (evt.getY() <= (concordiaTitlePanel.getY() + concordiaTitlePanel.getHeight()))) {
                retval = true;
            } else {
                retval = false;
            }
        }
        return retval;
    }

    /**
     *
     * @param evt
     * @return
     */
    public boolean mouseEnteredDateBox(MouseEvent evt) {
        // this is not very sensitive, so have forced cursor at mode selection below
        if (preferredDatePanel == null) {
            return false;
        } else if ((evt.getX() >= preferredDatePanel.getX())
                && (evt.getX() <= (preferredDatePanel.getX() + preferredDatePanel.getWidth()))
                && (evt.getY() >= preferredDatePanel.getY())
                && (evt.getY() <= (preferredDatePanel.getY() + preferredDatePanel.getHeight()))) {
            return true;
        } else {
            return false;
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
        if ((evt.getX() >= getLeftMargin())
                && (evt.getY() >= getTopMargin())
                && (evt.getY() <= getGraphHeight() + getTopMargin()) && !changingBestDateDivider) {

            setZoomMaxX(evt.getX());
            setZoomMaxY(evt.getY());

            changingBestDateDivider = false;
            setCursor(concordiaCursor);

            if (getImageMode().equalsIgnoreCase("PAN")) {

                double xOffsetValue
                        = getDisplayOffsetX() //
                        + (convertMouseXToValue(getZoomMinX()) - convertMouseXToValue(getZoomMaxX()));
                double yOffsetValue
                        = getDisplayOffsetY() //
                        + (convertMouseYToValue(getZoomMinY()) - convertMouseYToValue(getZoomMaxY()));

                // test for out of bounds
                double x = getMaxX() - xOffsetValue;

                if (((getMinX() + xOffsetValue) >= 0.0)// may 2010 zeroed - 1.0)

                        && ((getMinY() + yOffsetValue) >= 0.0)) {

                    setDisplayOffsetX(xOffsetValue);
                    setDisplayOffsetY(yOffsetValue);

                    // System.out.println( "move from " + getZoomMinX() + ", " + getZoomMinY() + "  to  " + getZoomMaxX() + ", " + getZoomMaxY() );
                    setZoomMinX(getZoomMaxX());
                    setZoomMinY(getZoomMaxY());
                }

            } else if (getImageMode().equalsIgnoreCase("TITLE")
                    && isShowTitleBox()) {
                int titleLocationX = concordiaTitlePanel.getX();
                int titleLocationY = concordiaTitlePanel.getY();

                int nextTitleLocationX = titleLocationX + (getZoomMaxX() - getZoomMinX());
                int nextTitleLocationY = titleLocationY + (getZoomMaxY() - getZoomMinY());

                setZoomMinX(getZoomMaxX());
                setZoomMinY(getZoomMaxY());

                if (this.contains(
                        nextTitleLocationX,
                        nextTitleLocationY)
                        && this.contains(
                                (nextTitleLocationX + concordiaTitlePanel.getWidth()),
                                (nextTitleLocationY + concordiaTitlePanel.getHeight()))) {
                    concordiaTitlePanel.setLocation(
                            nextTitleLocationX,
                            nextTitleLocationY);
                    setSavedConcordiaTitlePanelX(nextTitleLocationX);
                    setSavedConcordiaTitlePanelY(nextTitleLocationY);
                }

            } else if (getImageMode().equalsIgnoreCase("DATE")) {
                int dateLocationX = preferredDatePanel.getX();
                int dateLocationY = preferredDatePanel.getY();

                int nextDateLocationX = dateLocationX + (getZoomMaxX() - getZoomMinX());
                int nextDateLocationY = dateLocationY + (getZoomMaxY() - getZoomMinY());

                setZoomMinX(getZoomMaxX());
                setZoomMinY(getZoomMaxY());

                if (this.contains(
                        nextDateLocationX,
                        nextDateLocationY)
                        && this.contains(
                                (nextDateLocationX + preferredDatePanel.getWidth()),
                                (nextDateLocationY + preferredDatePanel.getHeight()))) {
                    preferredDatePanel.setLocation(
                            nextDateLocationX,
                            nextDateLocationY);
                    setSavedPreferredDatePanelX(nextDateLocationX);
                    setSavedPreferredDatePanelY(nextDateLocationY);
                }
            }

//            repaint();
        }

        repaint();
    }

    /**
     *
     * @param evt
     */
    @Override
    public void mouseMoved(MouseEvent evt) {
        if ((evt.getX() >= getLeftMargin())
                && (evt.getY() >= getTopMargin())
                && (evt.getY() <= getGraphHeight() + getTopMargin())) {

            if (!getImageMode().equalsIgnoreCase("ZOOM")
                    && mouseEnteredTitleBox(evt)) {
                setImageMode("TITLE");
                //  System.out.println("TITLE");
            } else if (!getImageMode().equalsIgnoreCase("ZOOM")
                    && mouseEnteredDateBox(evt)) {
                setImageMode("DATE");
                //  System.out.println("DATE");

            } else if (getImageMode().equalsIgnoreCase("TITLE")
                    || getImageMode().equalsIgnoreCase("DATE")) {
                setImageMode("PAN");
            } else {
                setCursor(concordiaCursor);
            }
        } else {
            setCursor(Cursor.getDefaultCursor());
        }

        //System.out.println("mouse " + evt.getX());
    }

    private double convertMouseXToValue(int x) {
        return //
                ((double) (x - getLeftMargin()) / (double) getGraphWidth()) //
                * getRangeX_Display()//
                + getMinX_Display();
    }

    private double convertMouseYToValue(int y) {
        return //
                (1.0 - ((double) (y - getTopMargin()) / (double) getGraphHeight())) //
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
            concordiaCursor = new Cursor((Cursor.HAND_CURSOR));
        } else if (imageMode.equalsIgnoreCase("ZOOM")) {
            concordiaCursor = new Cursor((Cursor.CROSSHAIR_CURSOR));
        } else if (imageMode.equalsIgnoreCase("TITLE")
                || imageMode.equalsIgnoreCase("DATE")) {
            concordiaCursor = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
        }

        // force issue
        setCursor(concordiaCursor);
    }

    /**
     *
     * @return
     */
    public Cursor getConcordiaCursor() {
        return concordiaCursor;
    }

    /**
     *
     * @param concordiaCursor
     */
    public void setConcordiaCursor(Cursor concordiaCursor) {
        this.concordiaCursor = concordiaCursor;
    }

    /**
     *
     * @return
     */
    public int getSavedConcordiaTitlePanelX() {
        return savedConcordiaTitlePanelX;
    }

    /**
     *
     * @param savedConcordiaTitlePanelX
     */
    public void setSavedConcordiaTitlePanelX(int savedConcordiaTitlePanelX) {
        this.savedConcordiaTitlePanelX = savedConcordiaTitlePanelX;
    }

    /**
     *
     * @return
     */
    public int getSavedConcordiaTitlePanelY() {
        return savedConcordiaTitlePanelY;
    }

    /**
     *
     * @param savedConcordiaTitlePanelY
     */
    public void setSavedConcordiaTitlePanelY(int savedConcordiaTitlePanelY) {
        this.savedConcordiaTitlePanelY = savedConcordiaTitlePanelY;
    }

    /**
     *
     * @return
     */
    @Override
    public Map<String, Map<String, String>> getAliquotOptions() {
        return aliquotOptions;
    }

    private void setAliquotOptions(Map<String, Map<String, String>> aliquotOptions) {
        // here we scan the sample and make sure there are aliquot options for each aliquot
        SampleDateInterpretationGUIOptions myOptions = sample.getSampleDateInterpretationGUISettings();
        for (AliquotInterface a : sample.getActiveAliquots()) {
            // this finds or creates an aliquotOptions map
            myOptions.getAliquotOptionsMapByName(a.getAliquotName(), ((ReduxAliquotInterface) a).getAliquotNumber());
        }
        this.aliquotOptions = aliquotOptions;
    }

    /**
     *
     * @return
     */
    public double getGraphWidth() {
        return getCurrentPlotAxesSetup().getGraphWidth();
    }

    /**
     *
     * @param graphWidth
     */
    public void setGraphWidth(double graphWidth) {
        getCurrentPlotAxesSetup().setGraphWidth(graphWidth);
    }

    /**
     *
     * @return
     */
    public double getGraphHeight() {
        return getCurrentPlotAxesSetup().getGraphHeight();
    }

    /**
     *
     * @param graphHeight
     */
    public void setGraphHeight(double graphHeight) {
        getCurrentPlotAxesSetup().setGraphHeight(graphHeight);
    }

    /**
     *
     * @return
     */
    public YorkLineFit getYorkLineFit() {
        return yorkLineFit;
    }

    /**
     *
     * @param yorkLineFit
     */
    public void setYorkFitLine(YorkLineFit yorkLineFit) {
        this.yorkLineFit = yorkLineFit;
    }

    /**
     *
     * @return
     */
    public JLayeredPane getPreferredDatePanel() {
        return preferredDatePanel;
    }

    /**
     *
     * @param preferredDatePanel
     */
    public void setPreferredDatePanel(JLayeredPane preferredDatePanel) {
        this.preferredDatePanel = preferredDatePanel;
        if (preferredDatePanel != null) {
            initPreferredDatePanel();
        }
    }

    /**
     *
     * @return
     */
    public int getSavedPreferredDatePanelX() {
        return savedPreferredDatePanelX;
    }

    /**
     *
     * @param savedPreferredDatePanelX
     */
    public void setSavedPreferredDatePanelX(int savedPreferredDatePanelX) {
        this.savedPreferredDatePanelX = savedPreferredDatePanelX;
    }

    /**
     *
     * @return
     */
    public int getSavedPreferredDatePanelY() {
        return savedPreferredDatePanelY;
    }

    /**
     *
     * @param savedPreferredDatePanelY
     */
    public void setSavedPreferredDatePanelY(int savedPreferredDatePanelY) {
        this.savedPreferredDatePanelY = savedPreferredDatePanelY;

    }

    /**
     *
     */
    public void saveSettings() {
    }

    /**
     *
     */
    public void initPreferredDatePanel() {
        Map<String, String> myAliquotOptions = getSelectedAliquotOptions();

        preferredDatePanel//
                .setLocation(//
                        (int) Integer.parseInt(getStringEntryFromAliquotOptions(myAliquotOptions, //
                                "dateBoxX", String.valueOf(preferredDatePanel.getX()))),
                        (int) Integer.parseInt(getStringEntryFromAliquotOptions(myAliquotOptions, //
                                "dateBoxY", String.valueOf(preferredDatePanel.getY()))));
    }

    /**
     *
     * @return
     */
    @Override
    public Map<String, String> getSelectedAliquotOptions() {
        String aliquotName
                = ((SampleDateModel) ((DateInterpretationBoxPanel) preferredDatePanel).//
                        getPreferredDateModel()).getAliquot().getAliquotName();
        return getAliquotOptions().get(aliquotName);
    }

    /**
     * @return the showTitleBox
     */
    public boolean isShowTitleBox() {
        return showTitleBox;
    }

    /**
     * @param showTitleBox the showTitleBox to set
     */
    public void setShowTitleBox(boolean showTitleBox) {
        this.showTitleBox = showTitleBox;
    }

    /**
     * @return the fadedDeselectedFractions
     */
    public boolean isFadedDeselectedFractions() {
        return fadedDeselectedFractions;
    }

    /**
     * @param fadedDeselectedFractions the fadedDeselectedFractions to set
     */
    public void setFadedDeselectedFractions(boolean fadedDeselectedFractions) {
        this.fadedDeselectedFractions = fadedDeselectedFractions;
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

    /**
     * @return the zoomMinX
     */
    public int getZoomMinX() {
        return getCurrentPlotAxesSetup().getXaxisSetup().getZoomMin();//   zoomMinX;
    }

    /**
     * @param zoomMinX the zoomMinX to set
     */
    public void setZoomMinX(int zoomMinX) {
        // this.zoomMinX = zoomMinX;
        getCurrentPlotAxesSetup().getXaxisSetup().setZoomMin(zoomMinX);
    }

    /**
     * @return the zoomMinY
     */
    public int getZoomMinY() {
        return getCurrentPlotAxesSetup().getYaxisSetup().getZoomMin();//   zoomMinY;
    }

    /**
     * @param zoomMinY the zoomMinY to set
     */
    public void setZoomMinY(int zoomMinY) {
        //this.zoomMinY = zoomMinY;
        getCurrentPlotAxesSetup().getYaxisSetup().setZoomMin(zoomMinY);
    }

    /**
     * @return the zoomMaxX
     */
    public int getZoomMaxX() {
        return getCurrentPlotAxesSetup().getXaxisSetup().getZoomMax();//  zoomMaxX;
    }

    /**
     * @param zoomMaxX the zoomMaxX to set
     */
    public void setZoomMaxX(int zoomMaxX) {
        //this.zoomMaxX = zoomMaxX;
        getCurrentPlotAxesSetup().getXaxisSetup().setZoomMax(zoomMaxX);
    }

    /**
     * @return the zoomMaxY
     */
    public int getZoomMaxY() {
        return getCurrentPlotAxesSetup().getYaxisSetup().getZoomMax();//   zoomMaxY;
    }

    /**
     * @param zoomMaxY the zoomMaxY to set
     */
    public void setZoomMaxY(int zoomMaxY) {
        //this.zoomMaxY = zoomMaxY;
        getCurrentPlotAxesSetup().getYaxisSetup().setZoomMax(zoomMaxY);
    }

    /**
     * @return the currentPlotAxesSetup
     */
    @Override
    public PlotAxesSetupInterface getCurrentPlotAxesSetup() {
        if (currentPlotAxesSetup == null) {
            AxisSetup xAxis = new AxisSetup(nameOfXaxisSourceValueModel);
            AxisSetup yAxis = new AxisSetup(nameOfYaxisSourceValueModel);
            currentPlotAxesSetup = new PlotAxesSetup(new AxisSetup[]{xAxis, yAxis});
        }
        return currentPlotAxesSetup;
    }

    /**
     * @param currentPlotAxesSetup
     * @param currentGraphAxesSetup the currentPlotAxesSetup to set
     */
    public void setCurrentGraphAxesSetup(PlotAxesSetupInterface currentPlotAxesSetup) {
        try {
            graphPanelModeChanger.synchronizePanelSizes(currentPlotAxesSetup);
        } catch (Exception e) {
        }

        this.currentPlotAxesSetup = currentPlotAxesSetup;
    }

    /**
     * @param showTightToEdges the showTightToEdges to set
     */
    public void setShowTightToEdges(boolean showTightToEdges) {
        this.showTightToEdges = showTightToEdges;
    }

    /**
     * @return the showExcludedEllipses
     */
    public boolean isShowExcludedEllipses() {
        return showExcludedEllipses;
    }

    /**
     * @param showExcludedEllipses the showExcludedEllipses to set
     */
    public void setShowExcludedEllipses(boolean showExcludedEllipses) {
        this.showExcludedEllipses = showExcludedEllipses;
    }

    /**
     * @return the showingSingleAliquot
     */
    public boolean isShowingSingleAliquot() {
        return showingSingleAliquot;
    }

    /**
     * @param showingSingleAliquot the showingSingleAliquot to set
     */
    public void setShowingSingleAliquot(boolean showingSingleAliquot) {
        this.showingSingleAliquot = showingSingleAliquot;
    }

    /**
     * @return the useUncertaintyCrosses
     */
    public boolean isUseUncertaintyCrosses() {
        return useUncertaintyCrosses;
    }

    /**
     * @param useUncertaintyCrosses the useUncertaintyCrosses to set
     */
    public void setUseUncertaintyCrosses(boolean useUncertaintyCrosses) {
        this.useUncertaintyCrosses = useUncertaintyCrosses;
    }

    /**
     * @return the showFilteredEllipses
     */
    @Override
    public boolean isShowFilteredEllipses() {
        return showFilteredEllipses;
    }

    /**
     * @param showFilteredEllipses the showFilteredEllipses to set
     */
    @Override
    public void setShowFilteredEllipses(boolean showFilteredEllipses) {
        this.showFilteredEllipses = showFilteredEllipses;
    }

    /**
     * @return the filteredFractions
     */
    public Vector<ETFractionInterface> getFilteredFractions() {
        Vector<ETFractionInterface> filtered = new Vector<>();
        for (ETFractionInterface filteredF : filteredFractions) {
            if (selectedFractions.contains(filteredF)) {
                filtered.add(filteredF);
            }
        }
        return filtered;
    }

    /**
     * @param proposedFilteredFractions the proposedFilteredFractions to set
     * from
     */
    @Override
    public void setFilteredFractions(Vector<ETFractionInterface> proposedFilteredFractions) {
        this.filteredFractions = proposedFilteredFractions;
    }

    /**
     * @param curAliquot the curAliquot to set
     */
    public void setCurAliquot(AliquotInterface curAliquot) {
        this.curAliquot = curAliquot;
    }

    /**
     * @param nameOfXaxisSourceValueModel the nameOfXaxisSourceValueModel to set
     */
    public void setNameOfXaxisSourceValueModel(String nameOfXaxisSourceValueModel) {
        try {
            currentPlotAxesSetup.getXaxisSetup().setAxisLabel(nameOfXaxisSourceValueModel);
        } catch (Exception e) {
        }
        this.nameOfXaxisSourceValueModel = nameOfXaxisSourceValueModel;
    }

    /**
     * @param nameOfYaxisSourceValueModel the nameOfYaxisSourceValueModel to set
     */
    public void setNameOfYaxisSourceValueModel(String nameOfYaxisSourceValueModel) {
        try {
            currentPlotAxesSetup.getYaxisSetup().setAxisLabel(nameOfYaxisSourceValueModel);
        } catch (Exception e) {
        }
        this.nameOfYaxisSourceValueModel = nameOfYaxisSourceValueModel;
    }

    /**
     * @return the nameOfXaxisSourceValueModel
     */
    public String getNameOfXaxisSourceValueModel() {
        return nameOfXaxisSourceValueModel;
    }

    /**
     * @return the nameOfYaxisSourceValueModel
     */
    public String getNameOfYaxisSourceValueModel() {
        return nameOfYaxisSourceValueModel;
    }

    @Override
    public String getConcordiaFlavor() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
