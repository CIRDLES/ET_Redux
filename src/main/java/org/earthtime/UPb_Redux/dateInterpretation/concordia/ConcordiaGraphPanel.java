/*
 * ConcordiaGraphPanel.java
 *
 * Created on March 4, 2008, 3:08 PM
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
package org.earthtime.UPb_Redux.dateInterpretation.concordia;

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
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import java.util.Map;
import java.util.Vector;
import javax.swing.JDialog;
import javax.swing.JLayeredPane;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import org.apache.batik.apps.rasterizer.SVGConverter;
import org.apache.batik.apps.rasterizer.SVGConverterException;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.aliquots.UPbReduxAliquot;
import org.earthtime.UPb_Redux.dateInterpretation.graphPersistence.DateInterpretationBoxPanel;
import org.earthtime.UPb_Redux.dateInterpretation.graphPersistence.GraphAxesSetup;
import org.earthtime.UPb_Redux.dateInterpretation.graphPersistence.HeatMapLegendPanel;
import org.earthtime.UPb_Redux.dateInterpretation.graphPersistence.TitleBoxPanel;
import org.earthtime.UPb_Redux.dialogs.graphManagers.GraphAxesDialog;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFractionI;
import org.earthtime.UPb_Redux.reduxLabData.ReduxLabData;
import org.earthtime.reportViews.ReportUpdaterInterface;
import org.earthtime.UPb_Redux.user.SampleDateInterpretationGUIOptions;
import org.earthtime.UPb_Redux.valueModels.SampleDateInterceptModel;
import org.earthtime.UPb_Redux.valueModels.SampleDateModel;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.Age207_206r;
import org.earthtime.aliquots.AliquotInterface;
import org.earthtime.dataDictionaries.Lambdas;
import org.earthtime.dataDictionaries.RadRatiosPbcCorrected;
import org.earthtime.exceptions.ETWarningDialog;
import org.earthtime.fractions.ETFractionInterface;
import org.earthtime.samples.SampleInterface;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

/**
 *
 * @author James F. Bowring
 */
public class ConcordiaGraphPanel extends JLayeredPane
        implements
        MouseListener,
        MouseMotionListener,
        AliquotDetailsDisplayInterface,
        PlottingDetailsDisplayInterface {

    // Class Variables
    // Instance Variables
    private SampleInterface sample;
    private ValueModel lambda235;
    private ValueModel lambda238;
    private ValueModel lambda232;
    private Vector<ETFractionInterface> selectedFractions;
    private Vector<ETFractionInterface> excludedFractions;
    private double minT;
    private double maxT;
    private boolean showConcordiaErrorBars;
    private boolean showEllipseCenters;
    private boolean showEllipseLabels;
    private boolean showExcludedEllipses;
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
    private GraphAxesSetup currentGraphAxesSetup;
    private GraphPanelModeChangeI graphPanelModeChanger;
    private ConcordiaGraphPanel mySelf;
    //Sept 2010 reduxfest requirement
    private boolean display_r206_238r_Th;
    private boolean display_r206_238r_Pa;
    // oct 2014 common lead
    private boolean display_PbcCorr;

    private boolean uploadToGeochronMode = false;
    private boolean showTightToEdges = false;
    // may 2014
    private boolean showBestDateDivider206_238 = false;
    private boolean showingSingleAliquot = false;
    // june 2014
    private boolean useUncertaintyCrosses = false; // ellipses are default
    private JLayeredPane heatMapLegendPanel;
    //oct 2014
    private transient double currentBestDate;
    private transient AliquotInterface curAliquot;
    private transient boolean changingBestDateDivider;
    private transient ReportUpdaterInterface reportUpdater;

    /**
     * Creates a new instance of ConcordiaGraphPanel
     *
     * @param mySample
     * @param reportUpdater the value of reportUpdater
     */
    public ConcordiaGraphPanel(SampleInterface mySample, ReportUpdaterInterface reportUpdater) {
        super();

        this.sample = mySample;
        this.reportUpdater = reportUpdater;

        setOpaque(true);

        setBackground(Color.white);

        selectedFractions = new Vector<>();
        excludedFractions = new Vector<>();

        this.showConcordiaErrorBars = true;
        this.showEllipseCenters = true;
        this.showEllipseLabels = true;
        this.showExcludedEllipses = true;
        this.display_r206_238r_Th = false;
        this.display_r206_238r_Pa = false;
        this.display_PbcCorr = false;

        this.yorkLineFit = null;

        concordiaCursor = Cursor.getDefaultCursor();
        this.imageMode = "PAN";

//        addMouseListener( this );
//        addMouseMotionListener( this );
        this.showTitleBox = true;
        // set up title box
        concordiaTitlePanel = new TitleBoxPanel(sample.getSampleName());
        this.savedConcordiaTitlePanelX = concordiaTitlePanel.getX();
        this.savedConcordiaTitlePanelY = concordiaTitlePanel.getY();

        this.heatMapLegendPanel = new HeatMapLegendPanel("legend");

        this.useUncertaintyCrosses = false;

        changingBestDateDivider = false;

        try {
            lambda235 = sample.getPhysicalConstantsModel().getDatumByName(Lambdas.lambda235.getName());
            lambda238 = sample.getPhysicalConstantsModel().getDatumByName(Lambdas.lambda238.getName());
            lambda232 = sample.getPhysicalConstantsModel().getDatumByName(Lambdas.lambda232.getName());
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }

        setViewOptions();

        this.fadedDeselectedFractions = false;

        // default is standard Concordia
        this.concordiaFlavor = "C";

        addMouseListener(this);
        addMouseMotionListener(this);

    }

    /**
     *
     */
    public void setViewOptions() {
        this.concordiaOptions =//
                sample.getSampleDateInterpretationGUISettings().getConcordiaOptions();
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

        return getCurrentGraphAxesSetup().mapX(x);
    }

    private double mapY(double y) {

        return getCurrentGraphAxesSetup().mapY(y);
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
        if (concordiaFlavor.equalsIgnoreCase("T-W")) {
            x = getMinX() + (rangeX / factor / 2);
        }

        if (currentGraphAxesSetup.checkDateForInMaxBound(x, getMaxY() - rangeY / factor)) {
            setMinX(getMinX() + rangeX / factor);
            setMinY(getMinY() + rangeY / factor);
            setMaxX(getMaxX() - rangeX / factor);
            setMaxY(getMaxY() - rangeY / factor);
        }
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
        if (!getCurrentGraphAxesSetup().isDoPlotting()) {
            g2d.drawString("NO VALID DATA!", 200, 300);
            g2d.drawString("Axis ratios must be > 0 and rho must be in [-1,...,1].", 200, 330);
            return; // need to paint no data warning
        }
        // setup painting parameters
        double ellipseSize = 2.0f;
        String ellipseLabelFont = "Monospaced";
        String ellipseLabelFontSize = "12";

        String concordiaErrorStyle = "shaded";
        float concordiaLineWeight = 1.5f;
        Color concordiaLineColor = new Color(0, 0, 255);
        String concordiaTicShape = "circle";
        double concordiaTicHalfWeight = 3.0;
        String concordiaLabelFont = "Monospaced";
        String concordiaLabelFontSize = "12";

        String axesTicLabelFont = "Monospaced";
        String axesTicLabelFontSize = "12";
        String axesLabelFont = "Monospaced";
        String axesLabelFontSize = "20";

        try {
            if (getConcordiaOptions().containsKey("ellipseSize")) {
                ellipseSize
                        = (float) Float.valueOf(getConcordiaOptions().get("ellipseSize"));
            }
        } catch (NumberFormatException numberFormatException) {
        }

        ellipseLabelFont = getStringEntryFromConcordiaOptions("ellipseLabelFont", ellipseLabelFont);
        ellipseLabelFontSize = getStringEntryFromConcordiaOptions("ellipseLabelFontSize", ellipseLabelFontSize);

        concordiaErrorStyle = getStringEntryFromConcordiaOptions("concordiaErrorStyle", concordiaErrorStyle);

        try {
            if (getConcordiaOptions().containsKey("concordiaLineWeight")) {
                concordiaLineWeight
                        = (float) Float.valueOf(getConcordiaOptions().get("concordiaLineWeight"));
            }
        } catch (NumberFormatException numberFormatException) {
        }

        try {
            if (getConcordiaOptions().containsKey("concordiaLineColor")) {
                String[] temp = //
                        getConcordiaOptions().get("concordiaLineColor").split(",");
                concordiaLineColor = buildRGBColor(temp);
            }
        } catch (Exception e) {
        }

        concordiaTicShape = getStringEntryFromConcordiaOptions("concordiaTicShape", concordiaTicShape);

        try {
            if (getConcordiaOptions().containsKey("concordiaTicWeight")) {
                concordiaTicHalfWeight
                        = (float) Float.valueOf(getConcordiaOptions().get("concordiaTicWeight"));
            }
        } catch (NumberFormatException numberFormatException) {
        }

        concordiaLabelFont = getStringEntryFromConcordiaOptions("concordiaLabelFont", concordiaLabelFont);
        concordiaLabelFontSize = getStringEntryFromConcordiaOptions("concordiaLabelFontSize", concordiaLabelFontSize);

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
                String[] temp = //
                        getConcordiaOptions().get("interceptLineColor").split(",");
                interceptLineColor = buildRGBColor(temp);
            }
        } catch (Exception e) {
        }

        String interceptErrorLineStyle = //
                getStringEntryFromAliquotOptions(getConcordiaOptions(), "interceptErrorLineStyle", "solid");

        Boolean truncateRegressionCurves = //
                Boolean.valueOf(getStringEntryFromAliquotOptions(getConcordiaOptions(), //
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

        g2d.setClip(getLeftMargin(), getTopMargin(), (int) getGraphWidth(), (int) getGraphHeight());
        RenderingHints rh = g2d.getRenderingHints();
        rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHints(rh);

        g2d.setFont(new Font("Monospaced", Font.BOLD, 14));

        ConcordiaLine myConcordiaLine = null;

        if (getConcordiaFlavor().equalsIgnoreCase("T-W")) {
            myConcordiaLine = buildTerraWasserburgLine();

            drawConcordiaLineSegments(//
                    g2d,
                    myConcordiaLine,
                    concordiaErrorStyle,
                    concordiaLineColor,
                    concordiaLineWeight);

            currentGraphAxesSetup.setLambda235(lambda235.getValue().doubleValue());
            currentGraphAxesSetup.plotConcordiaTicMarks(//
                    g2d,
                    myConcordiaLine,
                    concordiaLabelFont,
                    concordiaLabelFontSize,
                    concordiaTicShape,
                    (float) concordiaTicHalfWeight,
                    concordiaLineWeight);

        } else if (getConcordiaFlavor().equalsIgnoreCase("Th")) {
            // concordia line building ********************************************
            myConcordiaLine = buildConcordiaLineThorium();
            //   }
            drawConcordiaLineSegments(//
                    g2d,
                    myConcordiaLine,
                    concordiaErrorStyle,
                    concordiaLineColor,
                    concordiaLineWeight);

            currentGraphAxesSetup.setLambda235(lambda232.getValue().doubleValue());
            currentGraphAxesSetup.plotConcordiaTicMarks(//
                    g2d,
                    myConcordiaLine,
                    concordiaLabelFont,
                    concordiaLabelFontSize,
                    concordiaTicShape,
                    (float) concordiaTicHalfWeight,
                    concordiaLineWeight);
        } else {
            // concordia line building ********************************************
            myConcordiaLine = buildConcordiaLine();
            //   }
            drawConcordiaLineSegments(//
                    g2d,
                    myConcordiaLine,
                    concordiaErrorStyle,
                    concordiaLineColor,
                    concordiaLineWeight);

            currentGraphAxesSetup.setLambda235(lambda235.getValue().doubleValue());
            currentGraphAxesSetup.plotConcordiaTicMarks(//
                    g2d,
                    myConcordiaLine,
                    concordiaLabelFont,
                    concordiaLabelFontSize,
                    concordiaTicShape,
                    (float) concordiaTicHalfWeight,
                    concordiaLineWeight);
        }

        // paint de-selected fractions first 
        // paint fills first
        if (showExcludedEllipses) {
            for (ETFractionInterface f : excludedFractions) {
                if (!f.isRejected() && !(isDisplay_PbcCorr() && !((UPbFractionI) f).isCommonLeadLossCorrected())) {
                    // determine aliquot for colors etc.
                    String aliquotName = sample.getNameOfAliquotFromSample(f.getAliquotNumber());
                    Map<String, String> myAliquotOptions = getAliquotOptions().get(aliquotName);

                    Color excludedFillColor = new Color(255, 255, 255);
                    if (myAliquotOptions.containsKey("excludedFillColor")) {
                        String[] temp = //
                                myAliquotOptions.get("excludedFillColor").split(",");
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

                    if (f.getErrorEllipsePath() != null) {
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
                if (!f.isRejected() && !(isDisplay_PbcCorr() && !((UPbFractionI) f).isCommonLeadLossCorrected())) {
                    // determine aliquot for colors etc.
                    String aliquotName = sample.getNameOfAliquotFromSample(f.getAliquotNumber());
                    Map<String, String> myAliquotOptions = getAliquotOptions().get(aliquotName);

                    float excludedBorderWeight = 1.5f;
                    if (myAliquotOptions.containsKey("excludedBorderWeight")) {
                        excludedBorderWeight = Float.valueOf(myAliquotOptions.get("excludedBorderWeight"));
                    }

                    Color excludedBorderColor = new Color(0, 0, 0);
                    if (myAliquotOptions.containsKey("excludedBorderColor")) {
                        String[] temp = //
                                myAliquotOptions.get("excludedBorderColor").split(",");
                        excludedBorderColor = buildRGBColor(temp);
                    }

                    Color excludedCenterColor = new Color(0, 0, 0);
                    if (myAliquotOptions.containsKey("excludedCenterColor")) {
                        String[] temp = //
                                myAliquotOptions.get("excludedCenterColor").split(",");
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
        for (ETFractionInterface f : selectedFractions) {
            if (!f.isRejected() && !(isDisplay_PbcCorr() && !((UPbFractionI) f).isCommonLeadLossCorrected())) {
                // determine aliquot for colors etc.
                String aliquotName = sample.getNameOfAliquotFromSample(f.getAliquotNumber());
                Map<String, String> myAliquotOptions = (Map<String, String>) getAliquotOptions().get(aliquotName);

                Color includedFillColor = new Color(255, 255, 255);
                if (myAliquotOptions.containsKey("includedFillColor")) {
                    String[] temp = //
                            myAliquotOptions.get("includedFillColor").split(",");
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

                if (f.getErrorEllipsePath() != null) {
                    plotAFractionFillOnly(
                            g2d,
                            svgStyle,
                            f,
                            includedFillTransparencyPCT / 100.0f,
                            includedFillColor);

                }
            }
        }

        for (ETFractionInterface f : selectedFractions) {
            if (!f.isRejected() && !(isDisplay_PbcCorr() && !((UPbFractionI) f).isCommonLeadLossCorrected())) {
                // determine aliquot for colors etc.
                String aliquotName = sample.getNameOfAliquotFromSample(f.getAliquotNumber());
                Map<String, String> myAliquotOptions = getAliquotOptions().get(aliquotName);

                float includedBorderWeight = 1.5f;
                if (myAliquotOptions.containsKey("includedBorderWeight")) {
                    includedBorderWeight = Float.valueOf(myAliquotOptions.get("includedBorderWeight"));
                }

                Color includedBorderColor = new Color(0, 0, 0);
                if (myAliquotOptions.containsKey("includedBorderColor")) {
                    String[] temp = //
                            myAliquotOptions.get("includedBorderColor").split(",");
                    includedBorderColor = buildRGBColor(temp);
                }

                Color includedCenterColor = new Color(0, 0, 0);
                if (myAliquotOptions.containsKey("includedCenterColor")) {
                    String[] temp = //
                            myAliquotOptions.get("includedCenterColor").split(",");
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

        if ((getYorkLineFit() != null)
                && (selectedFractions.size() > 0)
                && getConcordiaFlavor().equalsIgnoreCase("C")) {
//            curAliquot = //
//                    sample.getAliquotByNumber(((UPbFractionI) selectedFractions.get(0))//
//                            .getAliquotNumber());

            plotYorkBestFitLineAndUncertainty(
                    curAliquot,
                    g2d,
                    interceptLineWeight,
                    interceptLineColor,
                    interceptErrorLineStyle,
                    truncateRegressionCurves);
        }

        g2d.setStroke(new BasicStroke(2.0f));

        if (isShowTitleBox()) {
            // paint title box
            ((TitleBoxPanel) concordiaTitlePanel).paint(g2d);

            // ((HeatMapLegendPanel)heatMapLegendPanel).paint(g2d);
        }

        // paint preferred date box
        if (isShowTitleBox() && (preferredDatePanel != null)) {
            Map<String, String> myAliquotOptions = getSelectedAliquotOptions();

            setSavedPreferredDatePanelX(preferredDatePanel.getX());
            setSavedPreferredDatePanelY(preferredDatePanel.getY());

            ((DateInterpretationBoxPanel) preferredDatePanel)//
                    .setDateFontName(getStringEntryFromAliquotOptions(myAliquotOptions, //
                                    "dateFont", ((DateInterpretationBoxPanel) preferredDatePanel).getDateFontName()));
            ((DateInterpretationBoxPanel) preferredDatePanel)//
                    .setDateFontSize(getStringEntryFromAliquotOptions(myAliquotOptions, //
                                    "dateFontSize", ((DateInterpretationBoxPanel) preferredDatePanel).getDateFontSize()));
            ((DateInterpretationBoxPanel) preferredDatePanel)//
                    .setVisibleBoxOutline(Boolean.valueOf(getStringEntryFromAliquotOptions(myAliquotOptions, //
                                            "visibleDateBoxOutline", Boolean.toString(((DateInterpretationBoxPanel) preferredDatePanel).//
                                                    isVisibleBoxOutline()))));
            ((DateInterpretationBoxPanel) preferredDatePanel)//
                    .setDateShowDate(Boolean.valueOf(getStringEntryFromAliquotOptions(myAliquotOptions, //
                                            "dateShowDate", Boolean.toString(((DateInterpretationBoxPanel) preferredDatePanel).//
                                                    isDateShowDate()))));
            ((DateInterpretationBoxPanel) preferredDatePanel)//
                    .setDateShowMSWD(Boolean.valueOf(getStringEntryFromAliquotOptions(myAliquotOptions, //
                                            "dateShowMSWD", Boolean.toString(((DateInterpretationBoxPanel) preferredDatePanel).//
                                                    isDateShowMSWD()))));
            ((DateInterpretationBoxPanel) preferredDatePanel)//
                    .setDateShowN(Boolean.valueOf(getStringEntryFromAliquotOptions(myAliquotOptions, //
                                            "dateShowN", Boolean.toString(((DateInterpretationBoxPanel) preferredDatePanel).//
                                                    isDateShowN()))));

            myAliquotOptions//
                    .put("dateBoxX", String.valueOf(getSavedPreferredDatePanelX()));
            myAliquotOptions//
                    .put("dateBoxY", String.valueOf(getSavedPreferredDatePanelY()));

            ((DateInterpretationBoxPanel) preferredDatePanel).paint(g2d);
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

        // may 2014
        if (showingSingleAliquot && showBestDateDivider206_238) {

            if (currentBestDate > 0.0) {
                // convert to 206_238 ratio
                double bestRatioDivider = Math.expm1(lambda238.getValue().doubleValue() * currentBestDate);

                g2d.setColor(Color.BLUE);

                Shape bestRatio = new Line2D.Double(//
                        mapX(getMinX_Display()),//
                        mapY(bestRatioDivider),//
                        mapX(getMaxX_Display()),//
                        mapY(bestRatioDivider));
                g2d.setStroke(new BasicStroke(0.5f));
                g2d.draw(bestRatio);

                Shape bestRatioHandle = new Ellipse2D.Double(//
                        mapX(getMinX_Display()) - 20,//
                        mapY(bestRatioDivider) - 5,//
                        20,//
                        10);
                g2d.fill(bestRatioHandle);

                DecimalFormat f = new DecimalFormat("###0");
                Font savedFont = g2d.getFont();
                g2d.setFont(new Font(
                        "SansSerif",
                        Font.PLAIN,
                        9));
                g2d.drawString(//
                        "Best Date Divider = " + f.format(currentBestDate / 1e6) + " ma",//
                        (float) mapX(getMaxX_Display()) - 150f, //
                        (float) mapY(bestRatioDivider) - 1f);
                g2d.setFont(savedFont);

                g2d.setColor(Color.black);
            }
        }

        // nov 2011
        if (uploadToGeochronMode) {
            Font savedFont = g2d.getFont();
            // draw sample name for geochron-uploaded graphs
            g2d.setPaint(Color.black);
            g2d.setFont(new Font(
                    "SansSerif",
                    Font.PLAIN,
                    14));

            String myText = "SAMPLE = " + sample.getSampleName().trim();
            TextLayout mLayout = //
                    new TextLayout(
                            myText, g2d.getFont(), g2d.getFontRenderContext());

            Rectangle2D bounds = mLayout.getBounds();
            g2d.drawString(myText, (int) ((currentGraphAxesSetup.getGraphWidth() - bounds.getBounds().getWidth())) / 2, (int) bounds.getBounds().getHeight() + 5);
            g2d.setFont(savedFont);
        }

    }

    private ConcordiaLine buildTerraWasserburgLine() {
        // note here that min and max refer to the x and y axis values
        // as the corresponding dates for x-axis are high for minx

        double minX_t = //
                (Math.log1p(getMaxX_Display()) //
                - Math.log(getMaxX_Display())) //
                / lambda238.getValue().doubleValue();

        ValueModel tempDate207_206r = new Age207_206r(lambda235, lambda238);
        double minY_t = ((Age207_206r) tempDate207_206r).calculateDate(//
                137.88, getMinY_Display(), 0.0);// may 2010 was plain min

        setMinT(Math.max(minX_t, minY_t));// switched min max june 2010

        double maxX_t = //
                (Math.log1p(getMinX_Display()) //
                - Math.log(getMinX_Display())) //
                / lambda238.getValue().doubleValue();
        double maxY_t = ((Age207_206r) tempDate207_206r).calculateDate(//
                137.88, getMaxY_Display(), 0.0);// may 2010 was plain max

        setMaxT(Math.min(maxX_t, maxY_t));

        ConcordiaLine myConcordiaLine
                = new ConcordiaLine(new TeraWasserburgLineSegment(//
                                lambda235, lambda238, null, minT, maxT), currentGraphAxesSetup);

        // curve the line
        myConcordiaLine.RefineLineByRecursiveHalving(5);//7 ); //10);

        // Jan 2011 looks like aspect ratio already handled with transform from Concordia
        double aspectRatio = 1.0;//(double) ((getRangeY_Display() / getGraphHeight()) / (getRangeX_Display() / getGraphWidth()));
        //System.out.println("A S P E C T =  " + aspectRatio);

        myConcordiaLine.calcUpperUnctEnvelopeTW(aspectRatio, getMinX_Display(), getMinY_Display(), getMaxX_Display(), getMaxY_Display());
        myConcordiaLine.calcLowerUnctEnvelopeTW(aspectRatio, getMinX_Display(), getMinY_Display(), getMaxX_Display(), getMaxY_Display());

        return myConcordiaLine;
    }

    private ConcordiaLine buildConcordiaLine() {
        // start concordia experiment http://java.sun.com/developer/technicalArticles/GUI/java2d/java2dpart1.html
        // determine the starting and ending coordinates of the concordia curve
        // and create a ConcordiaLine
        double minX_t = Math.log1p(getMinX_Display()) / lambda235.getValue().doubleValue();
        double minY_t = Math.log1p(getMinY_Display()) / lambda238.getValue().doubleValue();
        // calculate the min x and y for concordia by finding max T
        setMinT(Math.max(minX_t, minY_t));

        double maxX_t = Math.log1p(getMaxX_Display()) / lambda235.getValue().doubleValue();
        double maxY_t = Math.log1p(getMaxY_Display()) / lambda238.getValue().doubleValue();
        // calculate the max x and y for concordia by finding min T
        setMaxT(Math.min(maxX_t, maxY_t));

        ConcordiaLine myConcordiaLine
                = new ConcordiaLine(new ConcordiaLineSegment( //
                                lambda235, lambda238, minT, maxT), currentGraphAxesSetup);

        // curve the line
        // may 2010 bezier curve approach added
        myConcordiaLine.RefineLineByRecursiveHalving(5);

        // Jan 2011 looks like aspect ratio already handled with transform from Concordia
        double aspectRatio = 1.0;//(double) ((getRangeY_Display() / getGraphHeight()) / (getRangeX_Display() / getGraphWidth()));
        //System.out.println("A S P E C T =  " + aspectRatio);

        myConcordiaLine.calcUpperUnctEnvelopeC(aspectRatio, getMinX_Display(), getMinY_Display(), getMaxX_Display(), getMaxY_Display());
        myConcordiaLine.calcLowerUnctEnvelopeC(aspectRatio, getMinX_Display(), getMinY_Display(), getMaxX_Display(), getMaxY_Display());

        return myConcordiaLine;
    }

    private ConcordiaLine buildConcordiaLineThorium() {
        // start concordia experiment http://java.sun.com/developer/technicalArticles/GUI/java2d/java2dpart1.html
        // determine the starting and ending coordinates of the concordia curve
        // and create a ConcordiaLine
        double minX_t = Math.log1p(getMinX_Display()) / lambda232.getValue().doubleValue();
        double minY_t = Math.log1p(getMinY_Display()) / lambda238.getValue().doubleValue();
        // calculate the min x and y for concordia by finding max T
        setMinT(Math.max(minX_t, minY_t));

        double maxX_t = Math.log1p(getMaxX_Display()) / lambda232.getValue().doubleValue();
        double maxY_t = Math.log1p(getMaxY_Display()) / lambda238.getValue().doubleValue();
        // calculate the max x and y for concordia by finding min T
        setMaxT(Math.min(maxX_t, maxY_t));

        ConcordiaLine myConcordiaLine
                = new ConcordiaLine(new ConcordiaLineSegment( //
                                lambda232, lambda238, minT, maxT), currentGraphAxesSetup);

        // curve the line
        // may 2010 bezier curve approach added
        myConcordiaLine.RefineLineByRecursiveHalving(5);

        // Jan 2011 looks like aspect ratio already handled with transform from Concordia
        double aspectRatio = 1.0;//(double) ((getRangeY_Display() / getGraphHeight()) / (getRangeX_Display() / getGraphWidth()));
        //System.out.println("A S P E C T =  " + aspectRatio);

        myConcordiaLine.calcUpperUnctEnvelopeC(aspectRatio, getMinX_Display(), getMinY_Display(), getMaxX_Display(), getMaxY_Display());
        myConcordiaLine.calcLowerUnctEnvelopeC(aspectRatio, getMinX_Display(), getMinY_Display(), getMaxX_Display(), getMaxY_Display());

        return myConcordiaLine;
    }

    private void drawConcordiaLineSegments(
            Graphics2D g2d,//
            ConcordiaLine myConcordiaLine,
            String concordiaErrorStyle,
            Color concordiaLineColor,
            float concordiaLineWeight) {

        // draw the concordia segments
        ParametricCurveSegmentI myWorkingSeg = myConcordiaLine.getStartSeg();
        ParametricCurveSegmentI myWorkingSegSaved = myWorkingSeg;

        Path2D curvedP = new Path2D.Double(Path2D.WIND_NON_ZERO);
        // start at bottom left of concordia
        curvedP.moveTo(
                (float) mapX(myWorkingSeg.minX()),
                (float) mapY(myWorkingSeg.minY()));

        curvedP.curveTo(//
                (float) mapX(myWorkingSeg.minX()),
                (float) mapY(myWorkingSeg.minY()),
                (float) mapX(myWorkingSeg.controlX()),
                (float) mapY(myWorkingSeg.controlY()),
                (float) mapX(myWorkingSeg.maxX()),
                (float) mapY(myWorkingSeg.maxY()));

        myWorkingSegSaved = myWorkingSeg;
        myWorkingSeg = myWorkingSeg.getRightSeg();

        while ((myWorkingSeg != null) && (myWorkingSeg.getLeftSeg() != null)) { // stops traversal to fake upper envelope
            curvedP.curveTo(//
                    mapX(myWorkingSeg.minX()), //
                    mapY(myWorkingSeg.minY()), //
                    mapX(myWorkingSeg.controlX()), //
                    mapY(myWorkingSeg.controlY()), //
                    mapX(myWorkingSeg.maxX()), //
                    mapY(myWorkingSeg.maxY()));

            myWorkingSegSaved = myWorkingSeg;
            myWorkingSeg = myWorkingSeg.getRightSeg();
        }

        if (isShowConcordiaErrorBars()) {// && getConcordiaFlavor().equalsIgnoreCase( "C" ) ) {
            Path2D errorEnvelope = myConcordiaLine.getUpperUnctEnvelope();
            errorEnvelope.append(myConcordiaLine.getLowerUnctEnvelope(), true);
            errorEnvelope.closePath();

            if (concordiaErrorStyle.equalsIgnoreCase("shaded")) {
                g2d.setColor(ReduxConstants.myNotEditingGreyColor);
                g2d.fill(errorEnvelope);

            } else {
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(
                        1f,
                        BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND,
                        1f,
                        new float[]{2f},
                        0f));

                g2d.draw(errorEnvelope);
            }
        }

        // may 2010 new curved line
        g2d.setColor(concordiaLineColor);
        g2d.setStroke(new BasicStroke(concordiaLineWeight));
        g2d.draw(curvedP);
    }

    private Color buildRGBColor(String[] rgbComponents) {
        return new Color(
                (int) Integer.parseInt(rgbComponents[0].trim()),
                (int) Integer.parseInt(rgbComponents[1].trim()),
                (int) Integer.parseInt(rgbComponents[2].trim()));
    }

    private void plotYorkBestFitLineAndUncertainty(
            AliquotInterface curAliquot,
            Graphics2D g2d,
            float interceptLineWeight,
            Color interceptLineColor,
            String interceptErrorLineStyle,
            boolean truncateRegressionCurves) {

        double minX_Display = getMinX_Display();
        double maxX_Display = getMaxX_Display();

        // XIncrement is used to calculate the y values of the upper and lower uncertainty
        // it also serves to test for bad or missing data
        double xIncrement = (getRangeX_Display()) / 20.0;
        if (xIncrement > 0.0) {

            try {
                g2d.setStroke(new BasicStroke(interceptLineWeight));
                g2d.setPaint(interceptLineColor);

                // draw york line after first deciding whether to truncate it to just under the concordia
                // set lower and upper x to default values
                double lowerYorkX = minX_Display;
                double lowerYorkDate = 0.0;
                double upperYorkX = maxX_Display;
                double upperYorkDate = 0.0;

                if (truncateRegressionCurves) {
                    // determine x value for lower and upper dates
                    lowerYorkDate = //
                            curAliquot.getASampleDateModelByName("lower intercept").getValue().doubleValue();
                    lowerYorkX = Math.expm1(lambda235.getValue().doubleValue() * lowerYorkDate);
                    upperYorkDate = //
                            curAliquot.getASampleDateModelByName("upper intercept").getValue().doubleValue();
                    upperYorkX = Math.expm1(lambda235.getValue().doubleValue() * upperYorkDate);
                }

                // plot the line
                Line2D line = new Line2D.Double(
                        mapX(lowerYorkX),
                        mapY(getYorkLineFit().getYIntercept() + getYorkLineFit().getSlope() * lowerYorkX),
                        mapX(upperYorkX),
                        mapY(getYorkLineFit().getYIntercept() + getYorkLineFit().getSlope() * upperYorkX));
                g2d.draw(line);

                // uncertainty lines
                // in order to provide shading, the two lines need to be drawn in reverse order to ease closing
                // start at lower left with bottom of upper error bar
                double startLowerPlusX = minX_Display - xIncrement;
                double endUpperMinusX = maxX_Display + xIncrement;
                if (truncateRegressionCurves) {
                    double lowerPlus = //
                            ((SampleDateInterceptModel) curAliquot.getASampleDateModelByName("lower intercept")).getPlusInternalTwoSigmaUnct().doubleValue();
                    startLowerPlusX = Math.expm1(lambda235.getValue().doubleValue() * (lowerYorkDate + lowerPlus));
                    double upperMinus = //
                            ((SampleDateInterceptModel) curAliquot.getASampleDateModelByName("upper intercept")).getMinusInternalTwoSigmaUnct().doubleValue();
                    endUpperMinusX = Math.expm1(lambda235.getValue().doubleValue() * (upperYorkDate + upperMinus));
                }

                double sigmaYbarStart = //
                        Math.sqrt(getYorkLineFit().getYInterceptVariance() //
                                + 2.0 * startLowerPlusX * getYorkLineFit().getCovYIntercept__slope() //
                                + getYorkLineFit().getSlopeVariance() * startLowerPlusX * startLowerPlusX);
                double yUpStart = getYorkLineFit().getYIntercept()//
                        + getYorkLineFit().getSlope() * startLowerPlusX + 2.0 * sigmaYbarStart;

                Path2D uncertaintyBounds = new Path2D.Double(Path2D.WIND_EVEN_ODD);
                // at least x
                uncertaintyBounds.moveTo(
                        (float) mapX(startLowerPlusX),
                        (float) mapY(yUpStart));

                for (double xStep = startLowerPlusX; xStep <= endUpperMinusX; xStep += xIncrement) {
                    double sigmaYbar = //
                            Math.sqrt(getYorkLineFit().getYInterceptVariance() //
                                    + 2.0 * xStep * getYorkLineFit().getCovYIntercept__slope() //
                                    + getYorkLineFit().getSlopeVariance() * xStep * xStep);

                    double yUp = getYorkLineFit().getYIntercept()//
                            + getYorkLineFit().getSlope() * xStep + 2.0 * sigmaYbar;

                    // draw segment
                    uncertaintyBounds.lineTo(
                            (float) mapX(xStep),
                            (float) mapY(yUp));
                }

                if (truncateRegressionCurves) {
                    uncertaintyBounds.lineTo(
                            (float) mapX(upperYorkX),
                            (float) mapY(getYorkLineFit().getYIntercept() + getYorkLineFit().getSlope() * upperYorkX));
                }

                // next is upper right with top of lower error bar
                double startUpperPlusX = maxX_Display + xIncrement;
                double endLowerMinusX = minX_Display - xIncrement;

                if (truncateRegressionCurves) {
                    double upperPlus = //
                            ((SampleDateInterceptModel) curAliquot.getASampleDateModelByName("upper intercept")).getPlusInternalTwoSigmaUnct().doubleValue();
                    startUpperPlusX = Math.expm1(lambda235.getValue().doubleValue() * (upperYorkDate + upperPlus));
                    double lowerMinus = //
                            ((SampleDateInterceptModel) curAliquot.getASampleDateModelByName("lower intercept")).getMinusInternalTwoSigmaUnct().doubleValue();
                    endLowerMinusX = Math.expm1(lambda235.getValue().doubleValue() * (lowerYorkDate + lowerMinus));
                }

                double sigmaYbarEnd = //
                        Math.sqrt(getYorkLineFit().getYInterceptVariance() //
                                + 2.0 * startUpperPlusX * getYorkLineFit().getCovYIntercept__slope() //
                                + getYorkLineFit().getSlopeVariance() * startUpperPlusX * startUpperPlusX);
                double yDownEnd = getYorkLineFit().getYIntercept() //
                        + getYorkLineFit().getSlope() * startUpperPlusX - 2.0 * sigmaYbarEnd;

                uncertaintyBounds.lineTo(
                        (float) mapX(startUpperPlusX),
                        (float) mapY(yDownEnd));

                for (double xStep = startUpperPlusX; xStep >= endLowerMinusX; xStep -= xIncrement) {
                    double sigmaYbar = //
                            Math.sqrt(getYorkLineFit().getYInterceptVariance() //
                                    + 2.0 * xStep * getYorkLineFit().getCovYIntercept__slope() //
                                    + getYorkLineFit().getSlopeVariance() * xStep * xStep);

                    double yDown = getYorkLineFit().getYIntercept() //
                            + getYorkLineFit().getSlope() * xStep - 2.0 * sigmaYbar;

                    // draw segment
                    uncertaintyBounds.lineTo(
                            (float) mapX(xStep),
                            (float) mapY(yDown));
                }

                if (truncateRegressionCurves) {
                    uncertaintyBounds.lineTo(
                            (float) mapX(lowerYorkX),
                            (float) mapY(getYorkLineFit().getYIntercept() + getYorkLineFit().getSlope() * lowerYorkX));
                }

                uncertaintyBounds.closePath();

                if (interceptErrorLineStyle.equalsIgnoreCase("dashed")) {
                    float dash1[] = {10.0f};
                    BasicStroke dashed = //
                            new BasicStroke(interceptLineWeight,
                                    BasicStroke.CAP_BUTT,
                                    BasicStroke.JOIN_MITER,
                                    10.0f, dash1, 0.0f);
                    g2d.setStroke(dashed);
                    g2d.draw(uncertaintyBounds);

                } else {

                    // nov 2008 pdf converter does not like alphacomposite here
                    //Composite originalComposite = g2d.getComposite();
                    //g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.05f));
                    g2d.setPaint(new Color(0, 0, 0, 0.05f));

                    g2d.fill(uncertaintyBounds);
                    //restore composite
                    //g2d.setComposite(originalComposite);
                    g2d.setPaint(Color.black);
                    g2d.setStroke(new BasicStroke(0.5f));
                    g2d.draw(uncertaintyBounds);
                }
            } catch (Exception e) {
            }
            //   System.out.println("SLOPE = " + getYorkLineFit().getSlope());
        } else {
            System.out.println("Bad or missing data");
        }

    }

    private void plotAFraction(
            Graphics2D g2d,
            boolean svgStyle,
            ETFractionInterface f,
            Color borderColor,
            float borderWeight,
            Color centerColor,
            float centerSize,
            String ellipseLabelFont,
            String ellipseLabelFontSize) {

        Path2D ellipse = f.getErrorEllipsePath();
        if (svgStyle) {
            // generate file if necessary to handle weakness in Batik
        } else {
            // draw ellipse
            g2d.setStroke(new BasicStroke(borderWeight));
            g2d.setPaint(borderColor);
            g2d.draw(ellipse);
        }

        // draw ellipse centers
        if (isShowEllipseCenters()) {

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

//            // jan 2011 experiment re: centers: we need to find a more precise way to draw centers
//            float centerX = (float)mapX(((UPbFractionI) f).getCenterX());
//            float centerY = (float)mapY(((UPbFractionI) f).getCenterY());
//
//            Ellipse2D fraction = new Ellipse2D.Double(
//                    centerX,
//                    centerY,
//                    centerSize,
//                    centerSize );
//            g2d.setPaint( centerColor );
//            g2d.setStroke( new BasicStroke( 1.0f ) );
//            g2d.fill( fraction );
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
            if (f.getEllipseRho() < 0) {
                labelY = (float) (ellipse.getBounds().getY() //
                        + ellipse.getBounds().getHeight() - (1.0 + f.getEllipseRho()) * ellipse.getBounds().getHeight() / 4.0 + 15f);
                labelX = (float) (ellipse.getBounds().getX() + ellipse.getBounds().getWidth() - 6f);
            } else {
                labelY = (float) (ellipse.getBounds().getY() //
                        + (1.0 - f.getEllipseRho()) * ellipse.getBounds().getHeight() / 2.0);
                labelX = (float) (ellipse.getBounds().getX() + ellipse.getBounds().getWidth() + 2f);
            }

            g2d.drawString(f.getFractionID(), labelX, labelY);
        }

    }

    private void generateEllipsePathIII(
            ETFractionInterface f,
            double ellipseSize) {

        ValueModel xAxisRatio = null;
        ValueModel yAxisRatio = null;
        ValueModel correlationCoefficient = null;

        String r206_238r_corrected = "r206_238r";
        String r207_235r_corrected = "r207_235r";
        String r207_206r_corrected = "r207_206r";
        String r208_232r_corrected = "r208_232r";

        String rhoTW = "rhoR207_206r__r238_206r";
        String rhoConcordia = "rhoR206_238r__r207_235r";
        String rhoConcordiaThorium = "rhoR206_238r__r208_232r";

        if (isDisplay_r206_238r_Th() && isDisplay_r206_238r_Pa()) {
            r206_238r_corrected = "r206_238r_Th";
            r207_235r_corrected = "r207_235r_Pa";
            r207_206r_corrected = "r207_206r_ThPa";
            rhoConcordia = "rhoR206_238r_Th__r207_235r_Pa";
            rhoTW = "rhoR207_206r_ThPa__r238_206r_Th";
        } else if (isDisplay_r206_238r_Th()) {
            r206_238r_corrected = "r206_238r_Th";
            r207_206r_corrected = "r207_206r_Th";
            rhoConcordia = "rhoR206_238r_Th__r207_235r";
            rhoTW = "rhoR207_206r_Th__r238_206r_Th";
        } else if (isDisplay_r206_238r_Pa()) {
            r207_235r_corrected = "r207_235r_Pa";
            r207_206r_corrected = "r207_206r_Pa";
            rhoConcordia = "rhoR206_238r__r207_235r_Pa";
            rhoTW = "rhoR207_206r_Pa__r238_206r";
        } else if (isDisplay_PbcCorr()) {
            r206_238r_corrected = RadRatiosPbcCorrected.r206_238_PbcCorr.getName();
            r207_235r_corrected = RadRatiosPbcCorrected.r207_235_PbcCorr.getName();
            r207_206r_corrected = RadRatiosPbcCorrected.r207_206_PbcCorr.getName();
            rhoConcordia = "rhoR206_238PbcCorr__r207_235PbcCorr";
            rhoTW = "rhoR207_206PbcCorr__r238_206PbcCorr";
        }

        if (getConcordiaFlavor().equalsIgnoreCase("T-W")) {
            xAxisRatio = new ValueModel(r206_238r_corrected);
            try {
                xAxisRatio.setValue(BigDecimal.ONE.//
                        divide(f.getRadiogenicIsotopeRatioByName(r206_238r_corrected).getValue(),//
                                ReduxConstants.mathContext15));
                xAxisRatio.setUncertaintyType("ABS");
                xAxisRatio.setOneSigma(f.getRadiogenicIsotopeRatioByName(r206_238r_corrected).getOneSigmaAbs().//
                        divide(f.getRadiogenicIsotopeRatioByName(r206_238r_corrected).getValue().pow(2),//
                                ReduxConstants.mathContext15));
            } catch (Exception e) {
                xAxisRatio = new ValueModel(r206_238r_corrected);
            }

            yAxisRatio = f.getRadiogenicIsotopeRatioByName(r207_206r_corrected);
            correlationCoefficient = f.getRadiogenicIsotopeRatioByName(rhoTW);

        } else if (getConcordiaFlavor().equalsIgnoreCase("Th")) {
            xAxisRatio = f.getRadiogenicIsotopeRatioByName(r208_232r_corrected);
            yAxisRatio = f.getRadiogenicIsotopeRatioByName(r206_238r_corrected);

            correlationCoefficient = f.getRadiogenicIsotopeRatioByName(rhoConcordiaThorium);
            correlationCoefficient.setValue(BigDecimal.ZERO);
        } else {
            xAxisRatio = f.getRadiogenicIsotopeRatioByName(r207_235r_corrected);
            yAxisRatio = f.getRadiogenicIsotopeRatioByName(r206_238r_corrected);

            correlationCoefficient = f.getRadiogenicIsotopeRatioByName(rhoConcordia);
        }

        Path2D ellipse = new Path2D.Double(Path2D.WIND_NON_ZERO);// null;

        double aspectRatio = ((getRangeY_Display() / (double) getGraphHeight()) / (getRangeX_Display() / (double) getGraphWidth()));

        if ((correlationCoefficient.getValue().doubleValue() >= -1.0)
                && (correlationCoefficient.getValue().doubleValue() <= 1.0)) {
            if (!useUncertaintyCrosses) {
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

                // june 2014 play with crosses
                double xValue = xAxisRatio.getValue().doubleValue();
                double yValue = yAxisRatio.getValue().doubleValue();
                double xOneSigmaAbs = xAxisRatio.getOneSigmaAbs().doubleValue();
                double yOneSigmaAbs = yAxisRatio.getOneSigmaAbs().doubleValue();

                ellipse = new Path2D.Double(Path2D.WIND_NON_ZERO);

                int halfTip = 5;
                // y-tip
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

                f.setErrorEllipsePath(ellipse);
                // used for placing ellipse label
                f.setEllipseRho(correlationCoefficient.getValue().doubleValue());

            } else {
                f.setErrorEllipsePath(null);
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

        getCurrentGraphAxesSetup().plotXYaxes(//
                g2d, axisLabelAtomicNum, axisLabelAtomicName, isDisplay_r206_238r_Th(), isDisplay_r206_238r_Pa(), isDisplay_PbcCorr());
    }

    /**
     *
     */
    @Override
    public void resetPanel() {
        setDisplayOffsetY(0.0);
        setDisplayOffsetX(0.0);

        setMinX(0);
        setMaxX(0);
        setMinY(0);
        setMaxY(0);

        setZoomMaxX(0);
        setZoomMaxY(0);
        setZoomMinX(0);
        setZoomMinY(0);

        currentGraphAxesSetup.setUseAutomaticAxisTics(true);
        try {
            graphPanelModeChanger.switchToPanMode();
        } catch (Exception e) {
        } finally {
            refreshPanel();
        }
    }

    /**
     *
     */
    @Override
    public void refreshPanel() {
        preparePanel();
        repaint();
    }

    /**
     *
     */
    public void determineCurrentAliquot() {
        try {
            curAliquot = sample.getAliquotByNumber(selectedFractions.get(0)//
                    .getAliquotNumber());

            currentBestDate = curAliquot.getBestAgeDivider206_238().doubleValue();

        } catch (Exception e) {
        }
    }

    /**
     *
     */
    @Override
    public void preparePanel() {

        try {
            if (getConcordiaFlavor().equalsIgnoreCase("Th")) {
                setLambda235(sample.getPhysicalConstantsModel().getDatumByName(Lambdas.lambda232.getName()));
                getCurrentGraphAxesSetup().setLambda235(lambda232.getValue().doubleValue());
            } else {
                setLambda235(sample.getPhysicalConstantsModel().getDatumByName(Lambdas.lambda235.getName()));
                getCurrentGraphAxesSetup().setLambda235(lambda235.getValue().doubleValue());
            }
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
        setLambda238(lambda238);

        this.removeAll();

        // allows for escaspe from a bad plot that is saved
        if (getMaxX() >= 6500) {
            getCurrentGraphAxesSetup().setDoPlotting(false);
        }

        if (!getCurrentGraphAxesSetup().isDoPlotting()// revisit if turned off = also handles legacy files where deseralize boolean = false
                ||//
                (getMinX() + getMaxX() + getMinY() + getMaxY()) == 0) {
            if ((selectedFractions.size() + excludedFractions.size()) > 0) {
                getCurrentGraphAxesSetup().setDoPlotting(false);
            }

            // walk selectedFractions and get min and max of both ratios
            // set min and max at opposing values while searching for min and max
            // May 2010 don't reset if already saved
            if (getConcordiaFlavor().equalsIgnoreCase("T-W")) {
                // X-axis is xAxisRatio with value from 1 to 1E11
                setMinX(1E11);
                setMaxX(1.0);

                // Y-axis is yAxisRatio with value from 0.0460474 to 0.62525
                setMinY(0.7);
                setMaxY(0.04);
            } else if (getConcordiaFlavor().equalsIgnoreCase("Th")) {
                // X-axis is xAxisRatio with value from 0 to 93
                setMinX(100.0);
                setMaxX(0.0);

                // Y-axis is yAxisRatio with value from 0 to 2.05
                setMinY(3.0);
                setMaxY(0.0);
            } else {
                // X-axis is xAxisRatio with value from 0 to 93
                setMinX(100.0);
                setMaxX(0.0);

                // Y-axis is yAxisRatio with value from 0 to 2.05
                setMinY(3.0);
                setMaxY(0.0);
            }

            ValueModel xAxisRatio = null;
            ValueModel yAxisRatio = null;
            ValueModel correlationCoefficient = null;

            String r206_238r_corrected = "r206_238r";
            String r207_235r_corrected = "r207_235r";
            String r207_206r_corrected = "r207_206r";
            String r208_232r_corrected = "r208_232r";

            String rhoTW = "rhoR207_206r__r238_206r";
            String rhoConcordia = "rhoR206_238r__r207_235r";
            String rhoConcordiaThorium = "rhoR206_238r__r208_232r";

            if (isDisplay_r206_238r_Th() && isDisplay_r206_238r_Pa()) {
                r206_238r_corrected = "r206_238r_Th";
                r207_235r_corrected = "r207_235r_Pa";
                r207_206r_corrected = "r207_206r_ThPa";
                rhoConcordia = "rhoR206_238r_Th__r207_235r_Pa";
                rhoTW = "rhoR207_206r_ThPa__r238_206r_Th";
            } else if (isDisplay_r206_238r_Th()) {
                r206_238r_corrected = "r206_238r_Th";
                r207_206r_corrected = "r207_206r_Th";
                rhoConcordia = "rhoR206_238r_Th__r207_235r";
                rhoTW = "rhoR207_206r_Th__r238_206r_Th";
            } else if (isDisplay_r206_238r_Pa()) {
                r207_235r_corrected = "r207_235r_Pa";
                r207_206r_corrected = "r207_206r_Pa";
                rhoConcordia = "rhoR206_238r__r207_235r_Pa";
                rhoTW = "rhoR207_206r_Pa__r238_206r";
            } else if (isDisplay_PbcCorr()) {
                r206_238r_corrected = RadRatiosPbcCorrected.r206_238_PbcCorr.getName();
                r207_235r_corrected = RadRatiosPbcCorrected.r207_235_PbcCorr.getName();
                r207_206r_corrected = RadRatiosPbcCorrected.r207_206_PbcCorr.getName();
                rhoConcordia = "rhoR206_238PbcCorr__r207_235PbcCorr";
                rhoTW = "rhoR207_206PbcCorr__r238_206PbcCorr";
            }

            for (ETFractionInterface f : selectedFractions) {

                if (!f.isRejected() && !(isDisplay_PbcCorr() && !((UPbFractionI) f).isCommonLeadLossCorrected())) {
                    if (getConcordiaFlavor().equalsIgnoreCase("T-W")) {
                        xAxisRatio = new ValueModel("r238_206r");
                        try {
                            xAxisRatio.setValue(BigDecimal.ONE.//
                                    divide(f.getRadiogenicIsotopeRatioByName(r206_238r_corrected).getValue(),//
                                            ReduxConstants.mathContext15));
                            xAxisRatio.setUncertaintyType("ABS");
                            xAxisRatio.setOneSigma(f.getRadiogenicIsotopeRatioByName(r206_238r_corrected).getOneSigmaAbs().//
                                    divide(f.getRadiogenicIsotopeRatioByName(r206_238r_corrected).getValue().pow(2),//
                                            ReduxConstants.mathContext15));
                        } catch (Exception e) {
                        }

                        yAxisRatio = f.getRadiogenicIsotopeRatioByName(r207_206r_corrected);
                        correlationCoefficient = f.getRadiogenicIsotopeRatioByName(rhoTW);

                    } else if (getConcordiaFlavor().equalsIgnoreCase("Th")) {
                        xAxisRatio = f.getRadiogenicIsotopeRatioByName(r208_232r_corrected);
                        yAxisRatio = f.getRadiogenicIsotopeRatioByName(r206_238r_corrected);

                        correlationCoefficient = f.getRadiogenicIsotopeRatioByName(rhoConcordiaThorium);
                        correlationCoefficient.setValue(BigDecimal.ZERO);
                    } else {
                        xAxisRatio = f.getRadiogenicIsotopeRatioByName(r207_235r_corrected);
                        yAxisRatio = f.getRadiogenicIsotopeRatioByName(r206_238r_corrected);
                        correlationCoefficient = f.getRadiogenicIsotopeRatioByName(rhoConcordia);
                    }

                    if ((xAxisRatio.hasPositiveValue())
                            && (yAxisRatio.hasPositiveValue())
                            && (correlationCoefficient.getValue().doubleValue() >= -1.0)
                            && (correlationCoefficient.getValue().doubleValue() <= 1.0)) {
                        // June 2010 added check for correlation coeff

                        getCurrentGraphAxesSetup().setDoPlotting(true);

                        ErrorEllipse ee = new ErrorEllipse(
                                xAxisRatio,
                                yAxisRatio,
                                correlationCoefficient,
                                getGraphHeight() / getGraphWidth(),
                                2.5);

                        if (ee.getbezierMinX() < getMinX()) {
                            setMinX(ee.getbezierMinX());
                        }

                        if (ee.getbezierMinY() < getMinY()) {
                            setMinY(ee.getbezierMinY());
                        }

                        if (ee.getbezierMaxX() > getMaxX()) {
                            setMaxX(ee.getbezierMaxX());
                        }

                        if (ee.getbezierMaxY() > getMaxY()) {
                            setMaxY(ee.getbezierMaxY());
                        }

                    } else {
                        // inconsistent data
                    }

                }
            }

            if (showExcludedEllipses) {
                for (ETFractionInterface f : excludedFractions) {
                    if (!f.isRejected() && !(isDisplay_PbcCorr() && !((UPbFractionI) f).isCommonLeadLossCorrected())) {
                        if (getConcordiaFlavor().equalsIgnoreCase("T-W")) {
                            xAxisRatio = new ValueModel("r238_206r");
                            try {
                                xAxisRatio.setValue(BigDecimal.ONE.//
                                        divide(f.getRadiogenicIsotopeRatioByName(r206_238r_corrected).getValue(),//
                                                ReduxConstants.mathContext15));
                                xAxisRatio.setUncertaintyType("ABS");
                                xAxisRatio.setOneSigma(f.getRadiogenicIsotopeRatioByName(r206_238r_corrected).getOneSigmaAbs().//
                                        divide(f.getRadiogenicIsotopeRatioByName(r206_238r_corrected).getValue().pow(2),//
                                                ReduxConstants.mathContext15));
                            } catch (Exception e) {
                            }
                            yAxisRatio = f.getRadiogenicIsotopeRatioByName(r207_206r_corrected);

                            correlationCoefficient = f.getRadiogenicIsotopeRatioByName(rhoTW);
                        } else if (getConcordiaFlavor().equalsIgnoreCase("Th")) {
                            xAxisRatio = f.getRadiogenicIsotopeRatioByName(r208_232r_corrected);
                            yAxisRatio = f.getRadiogenicIsotopeRatioByName(r206_238r_corrected);
                            correlationCoefficient = f.getRadiogenicIsotopeRatioByName(rhoConcordia);
                            correlationCoefficient.setValue(BigDecimal.ZERO);
                        } else {
                            xAxisRatio = f.getRadiogenicIsotopeRatioByName(r207_235r_corrected);
                            yAxisRatio = f.getRadiogenicIsotopeRatioByName(r206_238r_corrected);
                            correlationCoefficient = f.getRadiogenicIsotopeRatioByName(rhoConcordia);
                        }

                        if ((xAxisRatio.hasPositiveValue())
                                && (yAxisRatio.hasPositiveValue())
                                && (correlationCoefficient.getValue().doubleValue() >= -1.0)
                                && (correlationCoefficient.getValue().doubleValue() <= 1.0)) {
                            // June 2010 added check for correlation coeff

                            getCurrentGraphAxesSetup().setDoPlotting(true);

                            ErrorEllipse ee = new ErrorEllipse(
                                    xAxisRatio,
                                    yAxisRatio,
                                    correlationCoefficient,
                                    getGraphHeight() / getGraphWidth(),
                                    2.5);

                            if (ee.getbezierMinX() < getMinX()) {
                                setMinX(ee.getbezierMinX());
                            }

                            if (ee.getbezierMinY() < getMinY()) {
                                setMinY(ee.getbezierMinY());
                            }

                            if (ee.getbezierMaxX() > getMaxX()) {
                                setMaxX(ee.getbezierMaxX());
                            }

                            if (ee.getbezierMaxY() > getMaxY()) {
                                setMaxY(ee.getbezierMaxY());
                            }

                        } else {
                            // inconsistent data
                        }

                    }
                }

            }
            // march 2009
            // for kwiki mode, if no dates worked, then lets show the full concordia
            // June 2010 let's show nothing
            if (getMinX() >= getMaxX()) {
                if (getConcordiaFlavor().equalsIgnoreCase("T-W")) {
                    // X-axis is xAxisRatio with value from 1 to 1E11
                    setMinX(1.0);
                    setMaxX(6500);// what is source of this ?

                    // Y-axis is yAxisRatio with value from 0.0460474 to 0.62525
                    setMinY(0.046);
                    setMaxY(0.625);

//                } else if (getConcordiaFlavor().equalsIgnoreCase("Th")) {
//                    // X-axis is xAxisRatio with value from 0 to 93
//                    setMinX(0.0);
//                    setMaxX(93.0);
//
//                    // Y-axis is yAxisRatio with value from 0 to 2.05
//                    setMinY(0.0);
//                    setMaxY(2.05);
                } else {
                    // X-axis is xAxisRatio with value from 0 to 93
                    setMinX(0.0);
                    setMaxX(93.0);

                    // Y-axis is yAxisRatio with value from 0 to 2.05
                    setMinY(0.0);
                    setMaxY(2.05);
                }
            }

//            System.out.println("Initial bounds " + getMinX() + "  " + getMinY() + "  " + getMaxX() + "   " + getMaxY());
            // this line pre-zooms out unless for upload to geochron, etc.
            if (!showTightToEdges) {
                performZoom(-2.0);
                performZoom(-2.0);
            }
        }

        if (isShowTitleBox()) {
            concordiaTitlePanel//
                    .setLocation(//
                            Integer.parseInt(getStringEntryFromConcordiaOptions(//
                                            "titleBoxX", String.valueOf(concordiaTitlePanel.getX()))), Integer.parseInt(getStringEntryFromConcordiaOptions(//
                                            "titleBoxY", String.valueOf(concordiaTitlePanel.getY()))));

            setSavedConcordiaTitlePanelX(concordiaTitlePanel.getX());
            setSavedConcordiaTitlePanelY(concordiaTitlePanel.getY());

            heatMapLegendPanel//
                    .setLocation(//
                            Integer.parseInt(getStringEntryFromConcordiaOptions(//
                                            "titleBoxX", String.valueOf(concordiaTitlePanel.getX()))), Integer.parseInt(getStringEntryFromConcordiaOptions(//
                                            "titleBoxY", String.valueOf(concordiaTitlePanel.getY()))));
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

    }

    /**
     *
     * @return
     */
    public int getTopMargin() {
        return getCurrentGraphAxesSetup().getTopMargin();
    }

    /**
     *
     * @param topMargin
     */
    public void setTopMargin(int topMargin) {
        getCurrentGraphAxesSetup().setTopMargin(topMargin);
    }

    /**
     *
     * @return
     */
    public int getLeftMargin() {
        return getCurrentGraphAxesSetup().getLeftMargin();
    }

    /**
     *
     * @param leftMargin
     */
    public void setLeftMargin(int leftMargin) {
        getCurrentGraphAxesSetup().setLeftMargin(leftMargin);
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
    public ValueModel getLambda235() {
        return lambda235;
    }

    /**
     *
     * @param lambda235
     */
    public void setLambda235(ValueModel lambda235) {
        this.lambda235 = lambda235;
    }

    /**
     *
     * @return
     */
    public ValueModel getLambda238() {
        return lambda238;
    }

    /**
     *
     * @param lambda238
     */
    public void setLambda238(ValueModel lambda238) {
        this.lambda238 = lambda238;
    }

    /**
     *
     * @return
     */
    public double getMinX_Display() {
        return getCurrentGraphAxesSetup().getXaxisSetup().getMin_Display();//           minX + displayOffsetX;
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
        getCurrentGraphAxesSetup().getXaxisSetup().setMin(minX);
    }

    /**
     *
     * @return
     */
    public double getMaxX_Display() {
        return getCurrentGraphAxesSetup().getXaxisSetup().getMax_Display();//    maxX + displayOffsetX;
    }

    /**
     *
     * @param maxX
     */
    public void setMaxX(double maxX) {
        //this.maxX = maxX;
        getCurrentGraphAxesSetup().getXaxisSetup().setMax(maxX);
    }

    /**
     *
     * @return
     */
    public double getMinY_Display() {
        return getCurrentGraphAxesSetup().getYaxisSetup().getMin_Display();//    minY + displayOffsetY;
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
        getCurrentGraphAxesSetup().getYaxisSetup().setMin(minY);
    }

    /**
     *
     * @return
     */
    public double getMaxY_Display() {
        return getCurrentGraphAxesSetup().getYaxisSetup().getMax_Display();//   maxY + displayOffsetY;
    }

    /**
     *
     * @param maxY
     */
    public void setMaxY(double maxY) {
        //this.maxY = maxY;
        getCurrentGraphAxesSetup().getYaxisSetup().setMax(maxY);
    }

    /**
     *
     * @return
     */
    public double getDisplayOffsetY() {
        return getCurrentGraphAxesSetup().getYaxisSetup().getDisplayOffset();//  displayOffsetY;
    }

    /**
     *
     * @param displayOffsetY
     */
    public void setDisplayOffsetY(double displayOffsetY) {
        // System.out.println("displayOffsetY  " + displayOffsetY);
        //this.displayOffsetY = displayOffsetY;
        getCurrentGraphAxesSetup().getYaxisSetup().setDisplayOffset(displayOffsetY);
    }

    /**
     *
     * @return
     */
    public double getDisplayOffsetX() {
        return getCurrentGraphAxesSetup().getXaxisSetup().getDisplayOffset();//  displayOffsetX;
    }

    /**
     *
     * @param displayOffsetX
     */
    public void setDisplayOffsetX(double displayOffsetX) {
        // System.out.println("displayOffsetX  " + displayOffsetX);
        //this.displayOffsetX = displayOffsetX;
        getCurrentGraphAxesSetup().getXaxisSetup().setDisplayOffset(displayOffsetX);
    }

    /**
     *
     * @return
     */
    public double getRangeX() {
        return getCurrentGraphAxesSetup().getXaxisSetup().getRange_Display();//       maxX - minX;
    }

    /**
     *
     * @return
     */
    public double getRangeY() {
        return getCurrentGraphAxesSetup().getYaxisSetup().getRange_Display();//   maxY - minY;
    }

    /**
     *
     * @return
     */
    public double getRangeX_Display() {
        return getCurrentGraphAxesSetup().getXaxisSetup().getRange_Display();//     getMaxX_Display() - getMinX_Display();
    }

    /**
     *
     * @return
     */
    public double getRangeY_Display() {
        return getCurrentGraphAxesSetup().getYaxisSetup().getRange_Display();//   getMaxY_Display() - getMinY_Display();
    }

    /**
     *
     * @return
     */
    public double getMinX() {
        return getCurrentGraphAxesSetup().getXaxisSetup().getMin(); // minX;
    }

    /**
     *
     * @return
     */
    public double getMinY() {
        return getCurrentGraphAxesSetup().getYaxisSetup().getMin();//   minY;
    }

    /**
     *
     * @return
     */
    public double getMaxX() {
        return getCurrentGraphAxesSetup().getXaxisSetup().getMax();//    maxX;
    }

    /**
     *
     * @return
     */
    public double getMaxY() {
        return getCurrentGraphAxesSetup().getYaxisSetup().getMax();// maxY;
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

//    public boolean isSnapConcordiaToDiagonal () {
//        return snapConcordiaToDiagonal;
//    }
//
//    public void setSnapConcordiaToDiagonal ( boolean snapConcordiaToDiagonal ) {
//        this.snapConcordiaToDiagonal = snapConcordiaToDiagonal;
//    }
    /**
     *
     * @return
     */
    public boolean isShowConcordiaErrorBars() {
        return showConcordiaErrorBars;
    }

    /**
     *
     * @param showConcordiaErrorBars
     */
    public void setShowConcordiaErrorBars(boolean showConcordiaErrorBars) {
        this.showConcordiaErrorBars = showConcordiaErrorBars;
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

                //Create the popup menu.
                JPopupMenu popup = new JPopupMenu();

                // Jan 2011 show coordinates fyi
                double x = convertMouseXToValue(evt.getX());
                double y = convertMouseYToValue(evt.getY());
                DecimalFormat f = new DecimalFormat("0.0000E00");

//                JMenuItem menuItemXY = //
//                        new JMenuItem("(" + f.format(x) + ", " + f.format(y) + ")");
                //popup.add( menuItemXY );

                JMenuItem menuItem = new JMenuItem("Automatically configure axes");
                menuItem.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent arg0) {
                        getCurrentGraphAxesSetup().setUseAutomaticAxisTics(true);
                        repaint();
                    }
                });
                popup.add(menuItem);

                mySelf = this;
                menuItem = new JMenuItem("Manually configure axes");
                menuItem.addActionListener(new ActionListener() {

                    public void actionPerformed(ActionEvent arg0) {

                        getCurrentGraphAxesSetup().setUseAutomaticAxisTics(false);

                        JDialog myGraphAxisDialog = new GraphAxesDialog( //
                                null, //
                                true,
                                getCurrentGraphAxesSetup(),
                                mySelf);
                        myGraphAxisDialog.setVisible(true);

                        // dialog closed
                        repaint();
                    }
                });
                popup.add(menuItem);

                // may 2014
                // check if aliquot vs sample
                if (showingSingleAliquot) {
                    menuItem = new JMenuItem("Toggle Best Date Divider");
                    menuItem.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent arg0) {
                            showBestDateDivider206_238 = !showBestDateDivider206_238;

                            repaint();
                        }
                    });
                    popup.add(menuItem);
                }

                popup.show(evt.getComponent(), evt.getX(), evt.getY());
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
    public void mouseReleased(MouseEvent evt) {

        if ((evt.getX() >= getLeftMargin())
                && (evt.getY() >= getTopMargin())
                && (evt.getY() <= getGraphHeight() + getTopMargin())) {

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
                    currentGraphAxesSetup.setUseAutomaticAxisTics(true);
                    graphPanelModeChanger.switchToPanMode();

                    repaint();
                }
            }
        } else {
            // set best age divider
            if (changingBestDateDivider) {
                curAliquot.setBestAgeDivider206_238(new BigDecimal(currentBestDate));
                ((UPbReduxAliquot) curAliquot).updateBestAge();
                reportUpdater.updateReportTable(false);
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
                && (evt.getY() <= getGraphHeight() + getTopMargin())) {

            setZoomMaxX(evt.getX());
            setZoomMaxY(evt.getY());

            changingBestDateDivider = false;
            setCursor(concordiaCursor);

            if (getImageMode().equalsIgnoreCase("PAN")) {

                double xOffsetValue = //
                        getDisplayOffsetX() //
                        + (convertMouseXToValue(getZoomMinX()) - convertMouseXToValue(getZoomMaxX()));
                double yOffsetValue = //
                        getDisplayOffsetY() //
                        + (convertMouseYToValue(getZoomMinY()) - convertMouseYToValue(getZoomMaxY()));

                // test for out of bounds
                double x = getMaxX() - xOffsetValue;
                if (concordiaFlavor.equalsIgnoreCase("T-W")) {
                    x = getMinX() + xOffsetValue;
                }

                if (((getMinX() + xOffsetValue) >= 0.0)// may 2010 zeroed - 1.0)

                        && ((getMinY() + yOffsetValue) >= 0.0)
                        && currentGraphAxesSetup.checkDateForInMaxBound((x), (getMaxY() + yOffsetValue))) {

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
        } else if (mouseInBestAgeHandleArea(evt.getX(), evt.getY())) {
            if (showBestDateDivider206_238) {
                changingBestDateDivider = true;
                double nextBestRatio = convertMouseYToValue(evt.getY());
                currentBestDate = Math.log1p(nextBestRatio) / lambda238.getValue().doubleValue();
                // System.out.println("Best Date = " + nextBestRatio + "  " + currentBestDate);
                setCursor(new Cursor(Cursor.N_RESIZE_CURSOR));//.WAIT_CURSOR));//.MOVE_CURSOR));
            }
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

    private boolean mouseInBestAgeHandleArea(int x, int y) {
        boolean retval = false;
        if (currentBestDate > 0.0) {
            // convert to 206_238 ratio
            double bestRatioDivider = Math.expm1(lambda238.getValue().doubleValue() * currentBestDate);
            if ((x >= mapX(getMinX_Display()) - 20)//
                    &&//
                    (x <= mapX(getMinX_Display()))//
                    && //
                    (y >= mapY(bestRatioDivider) - 5)//
                    && //
                    (y <= mapY(bestRatioDivider) + 6)//
                    ) {
                // System.out.println("mouse in best age handle");

                retval = true;
            }
        }

        return retval;
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
            myOptions.getAliquotOptionsMapByName(a.getAliquotName(), ((UPbReduxAliquot) a).getAliquotNumber());
        }
        this.aliquotOptions = aliquotOptions;
    }

    /**
     *
     * @return
     */
    public double getGraphWidth() {
        return getCurrentGraphAxesSetup().getGraphWidth();
    }

    /**
     *
     * @param graphWidth
     */
    public void setGraphWidth(double graphWidth) {
        getCurrentGraphAxesSetup().setGraphWidth(graphWidth);
    }

    /**
     *
     * @return
     */
    public double getGraphHeight() {
        return getCurrentGraphAxesSetup().getGraphHeight();
    }

    /**
     *
     * @param graphHeight
     */
    public void setGraphHeight(double graphHeight) {
        getCurrentGraphAxesSetup().setGraphHeight(graphHeight);
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
        getConcordiaOptions()//
                .put("titleBoxX", String.valueOf(getSavedConcordiaTitlePanelX()));
        getConcordiaOptions()//
                .put("titleBoxY", String.valueOf(getSavedConcordiaTitlePanelY()));

        if (preferredDatePanel != null) {
            String aliquotName = //
                    ((SampleDateModel) ((DateInterpretationBoxPanel) preferredDatePanel).//
                    getPreferredDateModel()).getAliquot().getAliquotName();
            Map<String, String> myAliquotOptions = getAliquotOptions().get(aliquotName);

            myAliquotOptions//
                    .put("dateBoxX", String.valueOf(getSavedPreferredDatePanelX()));
            myAliquotOptions//
                    .put("dateBoxY", String.valueOf(getSavedPreferredDatePanelY()));
        }
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
        String aliquotName = //
                ((SampleDateModel) ((DateInterpretationBoxPanel) preferredDatePanel).//
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
     * @return the concordiaFlavor
     */
    @Override
    public String getConcordiaFlavor() {
        return concordiaFlavor;
    }

    /**
     * @param concordiaFlavor the concordiaFlavor to set
     */
    public void setConcordiaFlavor(String concordiaFlavor) {
        this.concordiaFlavor = concordiaFlavor;
        if (concordiaFlavor.equalsIgnoreCase("T-W")) {
            setCurrentGraphAxesSetup(sample.getTerraWasserburgGraphAxesSetup());
        } else {
            setCurrentGraphAxesSetup(sample.getConcordiaGraphAxesSetup());

        }
//
//        if (concordiaFlavor.equalsIgnoreCase("C")) {
//            setCurrentGraphAxesSetup(sample.getConcordiaGraphAxesSetup());
//        } else {
//            setCurrentGraphAxesSetup(sample.getTerraWasserburgGraphAxesSetup());
//        }
//
//        getCurrentGraphAxesSetup().setLambda235( lambda235.getValue().doubleValue() );
//        getCurrentGraphAxesSetup().setLambda238( lambda238.getValue().doubleValue() );
//        getCurrentGraphAxesSetup().setDefaultR238_235s( //
//                getSample().getMyReduxLabData().getDefaultR238_235s().getValue().doubleValue() );
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
        return getCurrentGraphAxesSetup().getXaxisSetup().getZoomMin();//   zoomMinX;
    }

    /**
     * @param zoomMinX the zoomMinX to set
     */
    public void setZoomMinX(int zoomMinX) {
        // this.zoomMinX = zoomMinX;
        getCurrentGraphAxesSetup().getXaxisSetup().setZoomMin(zoomMinX);
    }

    /**
     * @return the zoomMinY
     */
    public int getZoomMinY() {
        return getCurrentGraphAxesSetup().getYaxisSetup().getZoomMin();//   zoomMinY;
    }

    /**
     * @param zoomMinY the zoomMinY to set
     */
    public void setZoomMinY(int zoomMinY) {
        //this.zoomMinY = zoomMinY;
        getCurrentGraphAxesSetup().getYaxisSetup().setZoomMin(zoomMinY);
    }

    /**
     * @return the zoomMaxX
     */
    public int getZoomMaxX() {
        return getCurrentGraphAxesSetup().getXaxisSetup().getZoomMax();//  zoomMaxX;
    }

    /**
     * @param zoomMaxX the zoomMaxX to set
     */
    public void setZoomMaxX(int zoomMaxX) {
        //this.zoomMaxX = zoomMaxX;
        getCurrentGraphAxesSetup().getXaxisSetup().setZoomMax(zoomMaxX);
    }

    /**
     * @return the zoomMaxY
     */
    public int getZoomMaxY() {
        return getCurrentGraphAxesSetup().getYaxisSetup().getZoomMax();//   zoomMaxY;
    }

    /**
     * @param zoomMaxY the zoomMaxY to set
     */
    public void setZoomMaxY(int zoomMaxY) {
        //this.zoomMaxY = zoomMaxY;
        getCurrentGraphAxesSetup().getYaxisSetup().setZoomMax(zoomMaxY);
    }

    /**
     * @return the currentGraphAxesSetup
     */
    public GraphAxesSetup getCurrentGraphAxesSetup() {
        if (currentGraphAxesSetup == null) {
            currentGraphAxesSetup = new GraphAxesSetup();
        }
        return currentGraphAxesSetup;
    }

    /**
     * @param currentGraphAxesSetup the currentGraphAxesSetup to set
     */
    public void setCurrentGraphAxesSetup(GraphAxesSetup currentGraphAxesSetup) {
        try {
            graphPanelModeChanger.synchronizePanelSizes(currentGraphAxesSetup);
        } catch (Exception e) {
        }

        this.currentGraphAxesSetup = currentGraphAxesSetup;

        if (concordiaFlavor.equalsIgnoreCase("C")) {
            currentGraphAxesSetup.setLambda235(lambda235.getValue().doubleValue());
        } else if (concordiaFlavor.equalsIgnoreCase("Th")) {
            currentGraphAxesSetup.setLambda235(lambda232.getValue().doubleValue());
        }

        currentGraphAxesSetup.setGraphType(concordiaFlavor);

        currentGraphAxesSetup.setLambda238(lambda238.getValue().doubleValue());
        currentGraphAxesSetup.setDefaultR238_235s( //
                ReduxLabData.getInstance().getDefaultR238_235s().getValue().doubleValue());
    }

    /**
     * @return the r206_238r_CorrectionSuffix
     */
    public boolean isDisplay_r206_238r_Th() {
        return display_r206_238r_Th;
    }

    /**
     * @param display_r206_238r_Th
     */
    public void setDisplay_r206_238r_Th(boolean display_r206_238r_Th) {
        this.display_r206_238r_Th = display_r206_238r_Th;
        getConcordiaOptions().put("display_r206_238r_Th", Boolean.toString(display_r206_238r_Th));
    }

    /**
     *
     */
    public void toggleDisplay_r206_238r_Th() {
        setDisplay_r206_238r_Th(!display_r206_238r_Th);
    }

    /**
     * @return the display_r206_238r_Pa
     */
    public boolean isDisplay_r206_238r_Pa() {
        return display_r206_238r_Pa;
    }

    /**
     * @param display_r206_238r_Pa the display_r206_238r_Pa to set
     */
    public void setDisplay_r206_238r_Pa(boolean display_r206_238r_Pa) {
        this.display_r206_238r_Pa = display_r206_238r_Pa;
        getConcordiaOptions().put("display_r206_238r_Pa", Boolean.toString(display_r206_238r_Pa));
    }

    /**
     *
     */
    public void toggleDisplay_r206_238r_Pa() {
        setDisplay_r206_238r_Pa(!display_r206_238r_Pa);
    }

    /**
     * @return the display_PbcCorr
     */
    public boolean isDisplay_PbcCorr() {
        return display_PbcCorr;
    }

    /**
     * @param display_PbcCorr the display_PbcCorr to set
     */
    public void setDisplay_PbcCorr(boolean display_PbcCorr) {
        this.display_PbcCorr = display_PbcCorr;
        getConcordiaOptions().put("display_PbcCorr", Boolean.toString(display_PbcCorr));
    }

    /**
     *
     */
    public void toggleDisplay_PbcCorr() {
        setDisplay_PbcCorr(!display_PbcCorr);
    }

    /**
     * @param uploadToGeochronMode the uploadToGeochronMode to set
     */
    public void setUploadToGeochronMode(boolean uploadToGeochronMode) {
        this.uploadToGeochronMode = uploadToGeochronMode;
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
     * @return the showBestDateDivider206_238
     */
    public boolean isShowBestDateDivider206_238() {
        return showBestDateDivider206_238;
    }

    /**
     * @param showBestDateDivider206_238 the showBestDateDivider206_238 to set
     */
    public void setShowBestDateDivider206_238(boolean showBestDateDivider206_238) {
        this.showBestDateDivider206_238 = showBestDateDivider206_238;
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

}
