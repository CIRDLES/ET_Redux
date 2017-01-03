/*
 * GraphAxesSetup.java
 *
 *
 *
 * Copyright 2006-2017 James F. Bowring and www.Earth-Time.org
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
package org.earthtime.UPb_Redux.dateInterpretation.graphPersistence;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.TextLayout;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.ConcordiaLine;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.ConcordiaLineSegment;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.TeraWasserburgLineSegment;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.UPb_Redux.valueModels.definedValueModels.Age207_206r;
import org.earthtime.plots.PlotAxesSetupInterface;
import org.earthtime.utilities.TicGeneratorForAxes;

/**
 *
 * @author James F. Bowring
 */
public class GraphAxesSetup implements
        PlotAxesSetupInterface,
        Serializable {

    // class variables
    private static final long serialVersionUID = 7642041497376213621L;
    /**
     *
     */
    public static int DEFAULT_GRAPH_LEFT_MARGIN_VERTICAL_LABELS = 20;
    /**
     *
     */
    public static int DEFAULT_GRAPH_LEFT_MARGIN_HORIZONTAL_LABELS = 65;
    // instance variables
    private String graphType;
    private int axisCount;
    private AxisSetup[] axes;
    private boolean useAutomaticAxisTics;
    private double graphWidth;
    private double graphHeight;
    private int topMargin;
    private int leftMargin;
    private double lambda235;
    private double lambda238;
    private double defaultR238_235s;
    private boolean yAxisHorizontalTicLabels;
    // concordia tic specs
    private int cDecimalPlaceCount;
    private int cTicIncrement;
    private int cTicLabelFrequency;
    private double minCtic;
    private double maxCtic;
    private double stepCtic;
    private double min_T;
    private double max_T;
    // stop bad plots
    private boolean doPlotting;

    /**
     *
     */
    public GraphAxesSetup() {

        this.graphType = "C";
        this.axisCount = 2;
        axes = new AxisSetup[3];

        for (int i = 0; i < axisCount; i++) {
            axes[i] = new AxisSetup();
        }

        lambda235 = 0.0;
        lambda238 = 0.0;
        defaultR238_235s = 0.0;

        useAutomaticAxisTics = true;
        graphWidth = 775;
        graphHeight = 600;
        topMargin = 1;
        leftMargin = DEFAULT_GRAPH_LEFT_MARGIN_VERTICAL_LABELS;

        yAxisHorizontalTicLabels = false;

        cDecimalPlaceCount = -2;
        cTicIncrement = 1;
        cTicLabelFrequency = 1;
        min_T = 0.0;
        max_T = 0.0;

        doPlotting = true;
    }

    /**
     *
     * @param graphType
     * @param axisCount
     */
    public GraphAxesSetup( //
            String graphType, //
            int axisCount) {

        this();
        this.graphType = graphType;
        this.axisCount = axisCount;
        axes = new AxisSetup[3];

        for (int i = 0; i < axisCount; i++) {
            axes[i] = new AxisSetup();
        }
    }

    /**
     *
     * @param g2d
     * @param myConcordiaLine
     * @param concordiaLabelFont
     * @param concordiaLabelFontSize
     * @param concordiaTicShape
     * @param concordiaTicHalfWeight
     * @param concordiaLineWeight
     */
    public void plotConcordiaTicMarks(//
            Graphics2D g2d,//
            ConcordiaLine myConcordiaLine,
            String concordiaLabelFont,
            String concordiaLabelFontSize,
            String concordiaTicShape,
            float concordiaTicHalfWeight,
            float concordiaLineWeight) {

        // plot the Concordia tic marks on the concordia
        g2d.setFont(new Font(
                concordiaLabelFont,
                Font.BOLD,
                Integer.parseInt(concordiaLabelFontSize)));

        setMin_T(myConcordiaLine.getStartSeg().getMinT());
        setMax_T(myConcordiaLine.getEndSeg().getMaxT());

        // all rebuild with tic generator nov 2011
        BigDecimal[] tics = TicGeneratorForAxes.generateTics(min_T / 1e6, max_T / 1e6, 12);   //minCtic, maxCtic, 20 );

        // trap for bad plot
        if (tics.length <= 1) {
            tics = new BigDecimal[0];
        }

        int indexOfFirstMajorTic = TicGeneratorForAxes.indexOfFirstMajorTic;
        int concordiaTicLabelFrequency = getcTicLabelFrequency();

//        System.out.println(">> index  " + indexOfFirstMajorTic);
        for (int i = 0; i < tics.length; i++) {

            double t = tics[i].movePointRight(6).doubleValue();

            double xValue = 0.0;
            double yValue = 0.0;
            if (isTerraWasserbergGraph()) {
                xValue = TeraWasserburgLineSegment.theX(t);
                yValue = TeraWasserburgLineSegment.theY(t);
            } else {
                xValue = ConcordiaLineSegment.theX(t);
                yValue = ConcordiaLineSegment.theY(t);
            }

            // June 2010 test for out of bounds
            if (getXaxisSetup().valueInVisibleRange(xValue)//
                    &&//
                    getYaxisSetup().valueInVisibleRange(yValue)) {

                if ((i - indexOfFirstMajorTic) % concordiaTicLabelFrequency == 0) {
                    // major tic
                    drawConcordiaTic(g2d, t, concordiaTicShape, concordiaTicHalfWeight);

                    // build the box to fit the value string
                    TextLayout ticLabelLayout = //
                            new TextLayout(
                                    tics[i].toPlainString(), g2d.getFont(), g2d.getFontRenderContext());

                    Rectangle2D bounds = ticLabelLayout.getBounds();

                    float textX = (float) mapX(xValue) //
                            - (float) bounds.getWidth() - concordiaTicHalfWeight - 8f;
                    float textY = (float) mapY(yValue) //
                            + (float) bounds.getHeight() * 0.4f;

                    // test for label out of bounds to left
                    if (mappedPointInVisiblePlot(textX, textY)) {
                        g2d.setPaint(Color.black);
                        g2d.drawString(tics[i].toPlainString(), textX, textY);
                    }

                } else {
                    // minor tic
                    drawConcordiaTic(g2d, t, concordiaTicShape, concordiaTicHalfWeight / 2);
                }
            }

        }

    }

    private void drawConcordiaTic(//
            Graphics2D g2d,//
            double t,//
            String concordiaTicShape, //
            float concordiaTicHalfWeight) {

        double xValue = 0.0;
        double yValue = 0.0;
        double slope = 0.0;
        double orthogonalSlope = 0.0;
        if (isTerraWasserbergGraph()) {
            xValue = TeraWasserburgLineSegment.theX(t);
            yValue = TeraWasserburgLineSegment.theY(t);
            slope = (new TeraWasserburgLineSegment()).theSlope(t);
            orthogonalSlope = TeraWasserburgLineSegment.theOrthogonalSlope(t);
        } else {
            xValue = ConcordiaLineSegment.theX(t);
            yValue = ConcordiaLineSegment.theY(t);
            slope = (new ConcordiaLineSegment()).theSlope(t);
            orthogonalSlope = ConcordiaLineSegment.theOrthogonalSlope(t);
        }

        if (concordiaTicShape.equalsIgnoreCase("circle")) {
//                || (isTerraWasserbergGraph()) ) {
            Ellipse2D ticShape
                    = new Ellipse2D.Double(
                            mapX(xValue) - concordiaTicHalfWeight,
                            mapY(yValue) - concordiaTicHalfWeight,
                            2 * concordiaTicHalfWeight,
                            2 * concordiaTicHalfWeight);

            // draw by first whiting out concordia line
            g2d.setPaint(Color.white);
            g2d.fill(ticShape);
            g2d.setPaint(Color.black);
            g2d.setStroke(new BasicStroke(0.5f));
            g2d.draw(ticShape);

        } else if (concordiaTicShape.equalsIgnoreCase("line")) {
            // draw a line, regardless of aspectratio, then rotate it
            Path2D ticShape = new Path2D.Double(Path2D.WIND_NON_ZERO);
            // start at upper center
            ticShape.moveTo(
                    (float) mapX(xValue),
                    (float) mapY(yValue) - (float) concordiaTicHalfWeight);
            // bottom center
            ticShape.lineTo(
                    (float) mapX(xValue),
                    (float) mapY(yValue) + (float) concordiaTicHalfWeight);

            // rotate line to align with line right angles
            float aspectRatio = (float) ((getRangeY_Display() / getGraphHeight()) / (getRangeX_Display() / getGraphWidth()));
            float ticSlope = //
                    (float) Math.abs(slope / aspectRatio);//

            double rotateAngle = Math.atan(ticSlope);
            if (!Double.isFinite(rotateAngle)) {
                rotateAngle = 0.0;
            }

            g2d.rotate(//
                    -1 * Math.signum(slope) * rotateAngle,
                    mapX(xValue),
                    mapY(yValue));

            g2d.setPaint(Color.black);
            g2d.setStroke(new BasicStroke((float) concordiaTicHalfWeight / 2f));// 0.5f ) );
            g2d.draw(ticShape);

            // un-rotate line to align with line right angles
            g2d.rotate(//
                    Math.signum(slope) * rotateAngle,
                    mapX(xValue),
                    mapY(yValue));

        } else {
            // assumed to be square shape

            // draw a square, regardless of aspectratio, then rotate it
            Path2D ticShape = new Path2D.Double(Path2D.WIND_NON_ZERO);
            // start at upper left
            ticShape.moveTo(
                    (float) mapX(xValue) - (float) concordiaTicHalfWeight,
                    (float) mapY(yValue) - (float) concordiaTicHalfWeight);
            // top right
            ticShape.lineTo(
                    (float) mapX(xValue) + (float) concordiaTicHalfWeight,
                    (float) mapY(yValue) - (float) concordiaTicHalfWeight);
            // bottom right
            ticShape.lineTo(
                    (float) mapX(xValue) + (float) concordiaTicHalfWeight,
                    (float) mapY(yValue) + (float) concordiaTicHalfWeight);
            // bottom left
            ticShape.lineTo(
                    (float) mapX(xValue) - (float) concordiaTicHalfWeight,
                    (float) mapY(yValue) + (float) concordiaTicHalfWeight);
            ticShape.closePath();

            // rotate square to align with line
            float aspectRatio = (float) ((getRangeY_Display() / getGraphHeight()) / (getRangeX_Display() / getGraphWidth()));
            float ticSlope = //
                    (float) Math.abs(slope / aspectRatio);//

            double rotateAngle = Math.atan(ticSlope);
            if (!Double.isFinite(rotateAngle)) {
                rotateAngle = 0.0;
            }

            g2d.rotate(//
                    -1 * Math.signum(slope) * rotateAngle,
                    mapX(xValue),
                    mapY(yValue));

            g2d.setPaint(Color.white);
            g2d.fill(ticShape);

            g2d.setPaint(Color.black);
            g2d.setStroke(new BasicStroke(0.5f));
            g2d.draw(ticShape);

            // un-rotate square to align with line
            g2d.rotate(//
                    Math.signum(slope) * rotateAngle,
                    mapX(xValue),
                    mapY(yValue));
        }
    }

    private void calculateConcordiaTicLimits() {

        //-3        9000        100        9100
        //-2        9900        10        9910
        //-1        9990        1        9991
        //0        9999        0.1        9999.1
        //1        9999.9        0.01        9999.91
        int shiftCount = 6 - cDecimalPlaceCount;

        try {
            BigDecimal minTickBD = new BigDecimal(getMin_T(), ReduxConstants.mathContext15)//
                    .movePointLeft(shiftCount).setScale(0, RoundingMode.FLOOR).movePointRight(-cDecimalPlaceCount);
            BigDecimal maxTickBD = new BigDecimal(getMax_T(), ReduxConstants.mathContext15)//
                    .movePointLeft(shiftCount).setScale(0, RoundingMode.CEILING).movePointRight(-cDecimalPlaceCount);

            minCtic = minTickBD.doubleValue();
            maxCtic = maxTickBD.doubleValue();
        } catch (Exception e) {
        }

        BigDecimal stepTicBD = new BigDecimal(1111111111, ReduxConstants.mathContext15)//
                .movePointLeft(shiftCount - 1).setScale(0, RoundingMode.FLOOR).ulp().movePointRight(-cDecimalPlaceCount - 1)//
                .multiply(new BigDecimal(cTicIncrement, ReduxConstants.mathContext15));
        stepCtic = stepTicBD.doubleValue();

        // oct 2011
        if (stepCtic == 0) {
            stepCtic = 1;
        }

        System.out.print(getMin_T()//
                + "   concordia tics:  " //
                + minCtic + "   "//
                + maxCtic + "    "//
                + stepCtic + "   "//
                + ((maxCtic - minCtic) / stepCtic) + "  "//
                + cDecimalPlaceCount);
        System.out.println();

    }

    private void calculateConcordiaTicLayout(boolean useAutomaticAxisTics) {

        double minTicCount = 10.5;
        double maxTicCount = 27.5;

        if (isTerraWasserbergGraph()) {
            if (minCtic < 50) {
                minTicCount = 30;
                maxTicCount = 400;
                cDecimalPlaceCount = -3;
            }

        }

        int decPlaceMaxCount = 2;

        calculateConcordiaTicLimits();

        if (useAutomaticAxisTics) {
            // reset tic frequency
            cTicLabelFrequency = 1;

            boolean amNotOne = true;
            int safetyCount = 0;
            while ((safetyCount < 10) && amNotOne && ((maxCtic - minCtic) / stepCtic) < minTicCount) {
                safetyCount++;
                switch (cTicIncrement) {
                    case 1:
                        amNotOne = false;
                        break;
                    case 2:
                        cTicIncrement = 1;
                        break;
                    case 5:
                        cTicIncrement = 2;
                        break;
                }
                calculateConcordiaTicLimits();
            }

            boolean amStable = false;
            safetyCount = 0;
            int safetyCountMax = 2;
            if (isTerraWasserbergGraph()) {
                safetyCountMax = 6;
            }
            while ((safetyCount < safetyCountMax) && !amStable && ((maxCtic - minCtic) / stepCtic) < minTicCount) {
                safetyCount++;

                if (cDecimalPlaceCount < decPlaceMaxCount) {
                    cDecimalPlaceCount++;
                } else {
                    amStable = true;
                }
                calculateConcordiaTicLimits();

                if (((maxCtic - minCtic) / stepCtic) > maxTicCount) {
                    switch (cTicIncrement) {
                        case 1:
                            cTicIncrement = 2;
                            break;
                        case 2:
                            cTicIncrement = 5;
                            break;
                        case 5:
                            cTicIncrement = 1;
                            if (cDecimalPlaceCount > -3) {
                                cDecimalPlaceCount--;
                            }
                    }

                    calculateConcordiaTicLimits();
                }
            }

            safetyCount = 0;
            while ((safetyCount < 10) && ((maxCtic - minCtic) / stepCtic) > maxTicCount) {
                safetyCount++;
                switch (getcTicIncrement()) {
                    case 1:
                        cTicIncrement = 2;
                        break;
                    case 2:
                        cTicIncrement = 5;
                        break;
                    case 5:
                        cTicIncrement = 1;
                        if (cDecimalPlaceCount > -3) {
                            cDecimalPlaceCount--;
                        }
                }

                calculateConcordiaTicLimits();
            }
        }
    }

    /**
     *
     * @param g2d
     * @param axisLabelAtomicNum
     * @param axisLabelAtomicName
     * @param thoriumCorrected
     * @param protactiniumCorrected
     * @param commonLeadCorrected
     */
    @Override
    public void plotXYaxes( //
            Graphics2D g2d, Font axisLabelAtomicNum, Font axisLabelAtomicName, boolean thoriumCorrected, boolean protactiniumCorrected, boolean commonLeadCorrected) {

//        getXaxisSetup().calculateTicLayout( useAutomaticAxisTics );
        // rewritten Nov 2011
        BigDecimal[] tics = TicGeneratorForAxes.generateTics(getXaxisSetup().getMin_Display(), getXaxisSetup().getMax_Display(), 12);

        // trap for bad plot
        if (tics.length <= 1) {
            tics = new BigDecimal[0];
        }

        double minYDisplay = getYaxisSetup().getMin_Display();
        int xAxisTicWidth = 8;
        int xTicLabelFrequency = getXaxisSetup().getTicLabelFrequency();
        int labeledTicCountXAxis = 0;

        g2d.setPaint(Color.black);
        for (int i = 0; i < tics.length; i++) {

            double x = tics[i].doubleValue();

            if ((x > getXaxisSetup().getMin_Display()) // dont print across mappedX axis
                    && (x < getXaxisSetup().getMax_Display())) // dont print across top border
            {
                try {
                    Shape ticMark = new Line2D.Double( //
                            mapX(x), mapY(minYDisplay), mapX(x), mapY(minYDisplay) - xAxisTicWidth);
                    g2d.draw(ticMark);

                    String intString = "00000" + tics[i].toPlainString().replace(".", "");
                    int lastPlace = Integer.parseInt(intString.substring(intString.length() - 4));

                    if (lastPlace % xTicLabelFrequency == 0) {
                        if (labeledTicCountXAxis % xTicLabelFrequency == 0) {

                            TextLayout ticLabelLayout = //
                                    new TextLayout(
                                            tics[i].toPlainString(), g2d.getFont(), g2d.getFontRenderContext());

                            Rectangle2D bounds = ticLabelLayout.getBounds();

                            g2d.drawString(tics[i].toPlainString(),//
                                    (float) mapX(x) - (float) bounds.getWidth() / 2f,//
                                    (float) mapY(minYDisplay) + 13f);
                        }
                        labeledTicCountXAxis++;
                    } else {

                        if (labeledTicCountXAxis > 0) {
                            labeledTicCountXAxis++;
                        }
                    }
                } catch (Exception e) {
                }
            }

        }

//        // mappedX axis
//        double minXtic = getXaxisSetup().getMinTic();
//        double maxXtic = getXaxisSetup().getMaxTic();
//        double stepXtic = getXaxisSetup().getStepTic();
//        int xDecimalPlaceCount = getXaxisSetup().getDecimalPlaceCount();
//        int xTicIncrement = getXaxisSetup().getTicIncrement();
//        int xTicLabelFrequency = getXaxisSetup().getTicLabelFrequency();
//
//        NumberFormat xFormat = new DecimalFormat( "0.0000000000".substring( 0, xDecimalPlaceCount + 2 - (xTicIncrement / 10) ) );
//
//        g2d.setPaint( Color.black );
//
//        int count = 0;
//        for (double x = minXtic; x < maxXtic; x += stepXtic) {
//            int xTicWidth = 4;
//
//            if ( (x <= getXaxisSetup().getMax_Display())//
//                    && (x >= getXaxisSetup().getMin_Display()) // dont print across mappedY axis
//                    && (count % xTicLabelFrequency) == 0 ) {
//                xTicWidth = 8;
//
//                String xTicLabel = xFormat.format( x );
//
//                float xTicLabelOffsetY = 13f;
//
//                // draw number value, adjusting for font
//                // build the box to fit the value string
//                TextLayout ticLabelLayout = //
//                        new TextLayout(
//                        xTicLabel.trim(), g2d.getFont(), g2d.getFontRenderContext() );
//
//                Rectangle2D bounds = ticLabelLayout.getBounds();
//
//                float textX = (float) mapX( x ) //
//                        - (float) bounds.getWidth() / 2f;
//                float textY = (float) mapY( getYaxisSetup().getMin_Display() );
//
//                // test for label out of bounds
//                if ( mappedPointInVisiblePlot( textX, textY ) ) {//+ getLeftMargin() - (float) bounds.getWidth(), textY ) ) {
//                    g2d.drawString( xTicLabel, textX, textY + xTicLabelOffsetY );
//                }
//
//            }
//
//            if ( (x < getXaxisSetup().getMax_Display())//
//                    && (x > getXaxisSetup().getMin_Display()) ) {
//                Line2D line = new Line2D.Double(
//                        mapX( x ),
//                        mapY( getYaxisSetup().getMin_Display() ),
//                        mapX( x ),
//                        mapY( getYaxisSetup().getMin_Display() ) - xTicWidth );
//                g2d.draw( line );
//            }
//
//            count ++;
//        }
//
        // mappedY axis **************************************************************
//        getYaxisSetup().calculateTicLayout( useAutomaticAxisTics );
        // rewritten Nov 2011
        tics = TicGeneratorForAxes.generateTics(getYaxisSetup().getMin_Display(), getYaxisSetup().getMax_Display(), 10);

        // trap for bad plot
        if (tics.length <= 1) {
            tics = new BigDecimal[0];
        }

        double minXDisplay = getXaxisSetup().getMin_Display();
        int yAxisTicWidth = 8;
        int yTicLabelFrequency = getYaxisSetup().getTicLabelFrequency();
        int labeledTicCountYAxis = 0;

        g2d.setPaint(Color.black);
        for (int i = 0; i < tics.length; i++) {

            double y = tics[i].doubleValue();

            if ((y > getYaxisSetup().getMin_Display()) // dont print across mappedX axis
                    && (y < getYaxisSetup().getMax_Display())) // dont print across top border
            {
                try {
                    Shape ticMark = new Line2D.Double( //
                            mapX(minXDisplay), mapY(y), mapX(minXDisplay) + yAxisTicWidth, mapY(y));
                    g2d.draw(ticMark);

                    String intString = "00000" + tics[i].toPlainString().replace(".", "");
                    int lastPlace = Integer.parseInt(intString.substring(intString.length() - 4));

                    if (lastPlace % yTicLabelFrequency == 0) {
                        if (labeledTicCountYAxis % yTicLabelFrequency == 0) {

                            TextLayout mLayout = //
                                    new TextLayout(
                                            tics[i].toPlainString(), g2d.getFont(), g2d.getFontRenderContext());

                            Rectangle2D bounds = mLayout.getBounds();

                            if (isyAxisHorizontalTicLabels()) {

                                g2d.drawString(tics[i].toPlainString(),//
                                        (float) mapX(minXDisplay) - (float) bounds.getWidth() - 4f,//
                                        (float) mapY(y) + 2.9f);
                            } else {
                                float yLabelCenterOffset = (float) mLayout.getBounds().getWidth() / 2f;

                                g2d.rotate(
                                        -Math.PI / 2.0,
                                        (float) mapX(minXDisplay) - 4f,
                                        (float) mapY(y) + yLabelCenterOffset);
                                g2d.drawString(
                                        tics[i].toPlainString(),
                                        (float) mapX(minXDisplay) - 4f,
                                        (float) mapY(y) + yLabelCenterOffset);
                                g2d.rotate(
                                        Math.PI / 2.0,
                                        (float) mapX(minXDisplay) - 4f,
                                        (float) mapY(y) + yLabelCenterOffset);
                            }
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

//        double minYtic = getYaxisSetup().getMinTic();
//        double maxYtic = getYaxisSetup().getMaxTic();
//        double stepYtic = getYaxisSetup().getStepTic();
//        int yDecimalPlaceCount = getYaxisSetup().getDecimalPlaceCount();
//        int yTicIncrement = getYaxisSetup().getTicIncrement();
//        int concordiaTicLabelFrequency = getYaxisSetup().getTicLabelFrequency();
//
//        NumberFormat yFormat = new DecimalFormat( "0.0000000000".substring( 0, yDecimalPlaceCount + 2 - (yTicIncrement / 10) ) );
//
//        count = 0;
//        for (double y = minYtic; y < maxYtic; y += stepYtic) {
//            int yTicWidth = 4;
//
//            if ( (y > getYaxisSetup().getMin_Display()) // dont print across mappedX axis
//                    && (y < getYaxisSetup().getMax_Display()) // dont print across top border
//                    && (count % concordiaTicLabelFrequency) == 0 ) {
//
//                yTicWidth = 8;
//
//                String yTicLabel = yFormat.format( y ).trim();
//
//                g2d.setPaint( Color.black );
//
//                if ( isyAxisHorizontalTicLabels() ) {
//
//                    // draw number value, adjusting for font
//                    // build the box to fit the value string
//                    TextLayout ticLabelLayout = //
//                            new TextLayout(
//                            yTicLabel, g2d.getFont(), g2d.getFontRenderContext() );
//
//                    Rectangle2D bounds = ticLabelLayout.getBounds();
//
//                    float textX = //
//                            (float) mapX( getXaxisSetup().getMin_Display() )//
//                            - (float) bounds.getWidth() - 2f;
//                    float textY = (float) mapY( y );
//
//                    // test for label out of bounds
//                    if ( mappedPointInVisiblePlot( textX + getLeftMargin(), textY ) //
//                            && mappedPointInVisiblePlot( textX + getLeftMargin(), textY - (float) bounds.getHeight() / 2f ) ) {
//                        g2d.drawString( yTicLabel, textX, textY + (float) bounds.getHeight() / 2f );
//                    }
//
//                } else {
//
//                    float yLabelCenterOffset = g2d.getFont().getSize() * 12 / 16 * (yTicLabel.length() / 2);
//
//                    g2d.rotate(
//                             - Math.PI / 2.0,
//                            (float) mapX( getXaxisSetup().getMin_Display() ) - 4f,
//                            (float) mapY( y ) + yLabelCenterOffset );
//                    g2d.drawString(
//                            yTicLabel,
//                            (float) mapX( getXaxisSetup().getMin_Display() ) - 4f,
//                            (float) mapY( y ) + yLabelCenterOffset );
//                    g2d.rotate(
//                            Math.PI / 2.0,
//                            (float) mapX( getXaxisSetup().getMin_Display() ) - 4f,
//                            (float) mapY( y ) + yLabelCenterOffset );
//                }
//            }
//
//            if ( (y > getYaxisSetup().getMin_Display()) // dont print across mappedX axis
//                    && (y < getYaxisSetup().getMax_Display()) ) { // dont print across top border
//                Line2D line = new Line2D.Double(
//                        mapX( getXaxisSetup().getMin_Display() ),
//                        mapY( y ),
//                        mapX( getXaxisSetup().getMin_Display() ) + yTicWidth,
//                        mapY( y ) );
//                g2d.draw( line );
//            }
//
//            count ++;
//        }
        // draw and label axes
        g2d.drawRect(
                getLeftMargin(), getTopMargin(), (int) getGraphWidth() - 1, (int) getGraphHeight());

        String yAxisNumeratorAtomicNumber;
        String yAxisDenominatorAtomicNumber;

        String xAxisNumeratorAtomicNumber;
        String xAxisDenominatorAtomicNumber;

        String correctionPrefix = "";
        if (thoriumCorrected && protactiniumCorrected) {
            correctionPrefix = "ThPa-corrected";
        } else if (thoriumCorrected) {
            correctionPrefix = "Th-corrected";
        } else if (protactiniumCorrected) {
            correctionPrefix = "Pa-corrected";
        } else if (commonLeadCorrected){
            correctionPrefix = "Pbc-corrected";
        }

        if (isTerraWasserbergGraph()) {
            yAxisNumeratorAtomicNumber = "207";
            yAxisDenominatorAtomicNumber = "206";

            xAxisNumeratorAtomicNumber = "238";
            xAxisDenominatorAtomicNumber = "206";
        } else if (graphType.equalsIgnoreCase("Th")) {
            yAxisNumeratorAtomicNumber = "206";
            yAxisDenominatorAtomicNumber = "238";

            xAxisNumeratorAtomicNumber = "208";
            xAxisDenominatorAtomicNumber = "232";
        } else {
            yAxisNumeratorAtomicNumber = "206";
            yAxisDenominatorAtomicNumber = "238";

            xAxisNumeratorAtomicNumber = "207";
            xAxisDenominatorAtomicNumber = "235";
        }

        g2d.setFont(axisLabelAtomicNum);
        g2d.drawString(xAxisNumeratorAtomicNumber,
                getLeftMargin() + (int) getGraphWidth() - 7 * axisLabelAtomicNum.getSize2D() - 12,
                getTopMargin() + (int) getGraphHeight() - 18);
        g2d.setFont(axisLabelAtomicName);
        g2d.drawString((xAxisNumeratorAtomicNumber.indexOf("0") > 0) ? "Pb/" : "U/",
                getLeftMargin() + (int) getGraphWidth() - 5.3f * axisLabelAtomicNum.getSize2D() - 12,
                getTopMargin() + (int) getGraphHeight() - 10);
        g2d.setFont(axisLabelAtomicNum);
        g2d.drawString(xAxisDenominatorAtomicNumber,
                getLeftMargin() + (int) getGraphWidth() - 2.8f * axisLabelAtomicNum.getSize2D() - 12,
                getTopMargin() + (int) getGraphHeight() - 18);
        g2d.setFont(axisLabelAtomicName);
        g2d.drawString((xAxisDenominatorAtomicNumber.indexOf("0") > 0) ? "Pb" : ((xAxisDenominatorAtomicNumber.equalsIgnoreCase("232")) ? "Th" : "U"),
                getLeftMargin() + (int) getGraphWidth() - 1.0f * axisLabelAtomicNum.getSize2D() - 12,
                getTopMargin() + (int) getGraphHeight() - 10);

        g2d.rotate(-Math.PI / 2.0);
        g2d.setFont(axisLabelAtomicNum);
        g2d.drawString(yAxisNumeratorAtomicNumber,
                -(getTopMargin() + 7 * axisLabelAtomicNum.getSize2D()) - 10,
                getLeftMargin() + 24);
        g2d.setFont(axisLabelAtomicName);
        g2d.drawString((yAxisNumeratorAtomicNumber.indexOf("0") > 0) ? "Pb/" : "U /",
                -(getTopMargin() + 5.3f * axisLabelAtomicNum.getSize2D()) - 10,
                getLeftMargin() + 32);
        g2d.setFont(axisLabelAtomicNum);
        g2d.drawString(yAxisDenominatorAtomicNumber,
                -(getTopMargin() + 2.8f * axisLabelAtomicNum.getSize2D()) - 12,
                getLeftMargin() + 24);
        g2d.setFont(axisLabelAtomicName);
        g2d.drawString((yAxisDenominatorAtomicNumber.indexOf("0") > 0) ? "Pb" : "U",
                -(getTopMargin() + 1.0f * axisLabelAtomicNum.getSize2D()) - 12,
                getLeftMargin() + 32);

        g2d.rotate(Math.PI / 2.0);

        g2d.setFont(axisLabelAtomicNum);
        g2d.drawString(
                correctionPrefix,
                getLeftMargin() + 50,
                getTopMargin() + 18);

    }

    /**
     *
     * @param x
     * @return
     */
    public double mapX(double x) {

        return (((x - getXaxisSetup().getMin_Display()) / getRangeX_Display()) * getGraphWidth()) + (double) getLeftMargin();
    }

    /**
     *
     * @param y
     * @return
     */
    public double mapY(double y) {

        return (((getYaxisSetup().getMax_Display() - y) / getRangeY_Display()) * getGraphHeight()) + (double) getTopMargin();
    }

    /**
     *
     */
    public void calculateConcordiaDateBoundsFromRatiosBounds() {
        if (!isTerraWasserbergGraph()) {

            getXaxisSetup().setMin_T(Math.log1p(getXaxisSetup().getMin_Display()) / getLambda235());
            getYaxisSetup().setMin_T(Math.log1p(getYaxisSetup().getMin_Display()) / getLambda238());

            getXaxisSetup().setMax_T(Math.log1p(getXaxisSetup().getMax_Display()) / getLambda235());
            getYaxisSetup().setMax_T(Math.log1p(getYaxisSetup().getMax_Display()) / getLambda238());
        } else {
            // T-W
            ValueModel tempDate207_206r = createAge207_206r();

            getXaxisSetup().setMin_T( //
                    (Math.log1p(getXaxisSetup().getMax_Display()) //
                    - Math.log(getXaxisSetup().getMax_Display())) //
                    / getLambda238());

            getYaxisSetup().setMin_T(((Age207_206r) tempDate207_206r).calculateDate(//
                    137.88, getYaxisSetup().getMin_Display(), 0.0));

            getXaxisSetup().setMax_T(//
                    (Math.log1p(getXaxisSetup().getMin_Display()) //
                    - Math.log(getXaxisSetup().getMin_Display())) //
                    / getLambda238());
            getYaxisSetup().setMax_T(((Age207_206r) tempDate207_206r).calculateDate(//
                    137.88, getYaxisSetup().getMax_Display(), 0.0));

        }
    }

    private ValueModel createAge207_206r() {
        // may 2010 part of refactoring temporarily requires this workaround of lambdas
        ValueModel tempVMlambda235 = new ValueModel("", new BigDecimal(getLambda235(), ReduxConstants.mathContext15), "", BigDecimal.ZERO, BigDecimal.ZERO);
        ValueModel tempVMlambda238 = new ValueModel("", new BigDecimal(getLambda238(), ReduxConstants.mathContext15), "", BigDecimal.ZERO, BigDecimal.ZERO);

        return new Age207_206r(tempVMlambda235, tempVMlambda238);
    }

    /**
     *
     * @param xRatio
     * @param yRatio
     * @return
     */
    public boolean checkDateForInMaxBound(double xRatio, double yRatio) {
        boolean retVal = true;

        if (!isTerraWasserbergGraph()) {

            retVal = retVal && (Math.log1p(xRatio) / getLambda235() <= ReduxConstants.MAX_DATE_ANNUM);
//            retVal = retVal && (Math.log1p( yRatio ) / getLambda238() <= ReduxConstants.MAX_DATE_ANNUM);

        } else {
            // T-W
            ValueModel tempDate207_206r = createAge207_206r();

            retVal = retVal && ((Math.log1p(xRatio) //
                    - Math.log(xRatio)) //
                    / getLambda238() <= ReduxConstants.MAX_DATE_ANNUM)
                    && (((Age207_206r) tempDate207_206r).calculateDate(//
                            137.88, yRatio, 0.0) <= ReduxConstants.MAX_DATE_ANNUM);

        }

        return retVal;
    }

    /**
     *
     */
    public void calculateRatiosBoundsFromConcordiaDateBounds() {
        if (!isTerraWasserbergGraph()) {

            getXaxisSetup().setMin(Math.expm1(getXaxisSetup().getMin_T() * getLambda235()));
            getXaxisSetup().setMax(Math.expm1(getXaxisSetup().getMax_T() * getLambda235()));

            getYaxisSetup().setMin(Math.expm1(getYaxisSetup().getMin_T() * getLambda238()));
            getYaxisSetup().setMax(Math.expm1(getYaxisSetup().getMax_T() * getLambda238()));

        } else {
            // T-W
            getXaxisSetup().setMax(1.0 / (Math.expm1(getXaxisSetup().getMin_T() * getLambda238())));
            getXaxisSetup().setMin(1.0 / (Math.expm1(getXaxisSetup().getMax_T() * getLambda238())));

            getYaxisSetup().setMin((1.0 / getDefaultR238_235s()) //
                    * (Math.expm1(getYaxisSetup().getMin_T() * getLambda235()))//
                    / (Math.expm1(getYaxisSetup().getMin_T() * getLambda238())));

            getYaxisSetup().setMax((1.0 / getDefaultR238_235s()) //
                    * (Math.expm1(getYaxisSetup().getMax_T() * getLambda235()))//
                    / (Math.expm1(getYaxisSetup().getMax_T() * getLambda238())));

        }
    }

    /**
     *
     * @return
     */
    @Override
    public AxisSetup getXaxisSetup() {
        return axes[0];

    }

    /**
     *
     * @return
     */
    @Override
    public AxisSetup getYaxisSetup() {
        return axes[1];

    }

    /**
     *
     * @return
     */
    public AxisSetup getZaxisSetup() {
        return axes[2];
    }

//    /**
//     * @return the graphType
//     */
//    public String getGraphType () {
//        return graphType;
//
//
//
//
//    }
    /**
     * @param graphType the graphType to set
     */
    public void setGraphType(String graphType) {
        this.graphType = graphType;
    }

    /**
     * @return the axisCount
     */
    public int getAxisCount() {
        return axisCount;
    }

    /**
     * @param axisCount the axisCount to set
     */
    public void setAxisCount(int axisCount) {
        this.axisCount = axisCount;
    }

    /**
     * @return the axes
     */
    public AxisSetup[] getAxes() {
        return axes;
    }

    /**
     * @param axes the axes to set
     */
    public void setAxes(AxisSetup[] axes) {
        this.axes = axes;
    }

    /**
     * @return the useAutomaticAxisTics
     */
    public boolean isUseAutomaticAxisTics() {
        return useAutomaticAxisTics;
    }

    /**
     * @param useAutomaticAxisTics the useAutomaticAxisTics to set
     */
    @Override
    public void setUseAutomaticAxisTics(boolean useAutomaticAxisTics) {
        this.useAutomaticAxisTics = useAutomaticAxisTics;
    }

    /**
     * @return the graphWidth
     */
    @Override
    public double getGraphWidth() {
        return graphWidth - leftMargin + DEFAULT_GRAPH_LEFT_MARGIN_VERTICAL_LABELS;
    }

    /**
     * @param graphWidth the graphWidth to set
     */
    @Override
    public void setGraphWidth(double graphWidth) {
        this.graphWidth = graphWidth;
    }

    /**
     * @return the graphHeight
     */
    public double getGraphHeight() {
        return graphHeight;
    }

    /**
     * @param graphHeight the graphHeight to set
     */
    public void setGraphHeight(double graphHeight) {
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
     *
     * @return
     */
    public double getRangeX_Display() {
        return (getXaxisSetup().getMax_Display() - getXaxisSetup().getMin_Display());
    }

    /**
     *
     * @return
     */
    public double getRangeY_Display() {
        return (getYaxisSetup().getMax_Display() - getYaxisSetup().getMin_Display());
    }

    /**
     * @return the lambda235
     */
    public double getLambda235() {
        return lambda235;
    }

    /**
     * @return the lambda238
     */
    public double getLambda238() {
        return lambda238;
    }

    /**
     * @param lambda235 the lambda235 to set
     */
    public void setLambda235(double lambda235) {
        this.lambda235 = lambda235;
    }

    /**
     * @param lambda238 the lambda238 to set
     */
    public void setLambda238(double lambda238) {
        this.lambda238 = lambda238;
    }

    /**
     * @return the yAxisHorizontalTicLabels
     */
    public boolean isyAxisHorizontalTicLabels() {
        return yAxisHorizontalTicLabels;
    }

    /**
     * @param yAxisHorizontalTicLabels the yAxisHorizontalTicLabels to set
     */
    public void setyAxisHorizontalTicLabels(boolean yAxisHorizontalTicLabels) {
        if (yAxisHorizontalTicLabels) {
            setLeftMargin(DEFAULT_GRAPH_LEFT_MARGIN_HORIZONTAL_LABELS);
        } else {
            setLeftMargin(DEFAULT_GRAPH_LEFT_MARGIN_VERTICAL_LABELS);
        }
        this.yAxisHorizontalTicLabels = yAxisHorizontalTicLabels;
    }

    /**
     * @return the defaultR238_235s
     */
    public double getDefaultR238_235s() {
        return defaultR238_235s;
    }

    /**
     * @param defaultR238_235s the defaultR238_235s to set
     */
    public void setDefaultR238_235s(double defaultR238_235s) {
        this.defaultR238_235s = defaultR238_235s;
    }

    /**
     * @return the cDecimalPlaceCount
     */
    public int getcDecimalPlaceCount() {
        return cDecimalPlaceCount;
    }

    /**
     * @param cDecimalPlaceCount the cDecimalPlaceCount to set
     */
    public void setcDecimalPlaceCount(int cDecimalPlaceCount) {
        this.cDecimalPlaceCount = cDecimalPlaceCount;
    }

    /**
     * @return the cTicIncrement
     */
    public int getcTicIncrement() {
        // backwards compatibility
        if (cTicIncrement == 0) {
            cTicIncrement = 1;
        }
        return cTicIncrement;
    }

    /**
     * @param cTicIncrement the cTicIncrement to set
     */
    public void setcTicIncrement(int cTicIncrement) {
        this.cTicIncrement = cTicIncrement;
    }

    /**
     * @return the cTicLabelFrequency
     */
    public int getcTicLabelFrequency() {
        if (cTicLabelFrequency < 1) {
            cTicLabelFrequency = 1;
        }
        return cTicLabelFrequency;
    }

    /**
     * @param cTicLabelFrequency the cTicLabelFrequency to set
     */
    public void setcTicLabelFrequency(int cTicLabelFrequency) {
        this.cTicLabelFrequency = cTicLabelFrequency;
    }

    /**
     * @return the min_T
     */
    public double getMin_T() {
        return min_T;
    }

    /**
     * @param min_T the min_T to set
     */
    public void setMin_T(double min_T) {
        this.min_T = min_T;
    }

    /**
     * @return the max_T
     */
    public double getMax_T() {
        return max_T;
    }

    /**
     * @param max_T the max_T to set
     */
    public void setMax_T(double max_T) {
        this.max_T = max_T;
    }

    /**
     *
     * @return
     */
    public boolean isTerraWasserbergGraph() {
        return graphType.equalsIgnoreCase("T-W");
    }

    /**
     *
     * @param mappedX
     * @param mappedY
     * @return
     */
    public boolean mappedPointInVisiblePlot(double mappedX, double mappedY) {
        return //
                (mappedX >= mapX(getXaxisSetup().getMin_Display()))//
                && //
                (mappedX <= mapX(getXaxisSetup().getMax_Display()))//
                &&// remember y plots top down
                (mappedY <= mapY(getYaxisSetup().getMin_Display()))//
                && //
                (mappedY >= mapY(getYaxisSetup().getMax_Display()));
    }

    /**
     * @return the doPlotting
     */
    public boolean isDoPlotting() {
        return doPlotting;
    }

    /**
     * @param doPlotting the doPlotting to set
     */
    public void setDoPlotting(boolean doPlotting) {
        this.doPlotting = doPlotting;
    }
}
