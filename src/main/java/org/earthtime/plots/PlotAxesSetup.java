/*
 * PlotAxesSetup.java
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
package org.earthtime.plots;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.TextLayout;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.math.BigDecimal;
import org.earthtime.UPb_Redux.dateInterpretation.graphPersistence.*;
import org.earthtime.utilities.TicGeneratorForAxes;

/**
 *
 * @author James F. Bowring
 */
public class PlotAxesSetup implements
        PlotAxesSetupInterface,
        Serializable {

    // class variables
    //private static final long serialVersionUID = 7642041497376213621L;
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
    private boolean yAxisHorizontalTicLabels;
    // concordia tic specs
    private int cDecimalPlaceCount;
    private int cTicIncrement;
    private int cTicLabelFrequency;
    // stop bad plots
    private boolean doPlotting;

    /**
     *
     */
    public PlotAxesSetup() {

        this.graphType = "C";
        this.axisCount = 2;
        axes = new AxisSetup[3];

        for (int i = 0; i < axisCount; i++) {
            axes[i] = new AxisSetup();
        }

        useAutomaticAxisTics = true;
        graphWidth = 775;
        graphHeight = 600;
        topMargin = 1;
        leftMargin = DEFAULT_GRAPH_LEFT_MARGIN_VERTICAL_LABELS;

        yAxisHorizontalTicLabels = false;

        cDecimalPlaceCount = -2;
        cTicIncrement = 1;
        cTicLabelFrequency = 1;

        doPlotting = true;
    }

    /**
     *
     * @param axes
     * @param graphType
     * @param axisCount
     */
    public PlotAxesSetup( //
            AxisSetup[] axes) {

        this();
        this.axes = axes;
        this.axisCount = axes.length;
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

                            TextLayout ticLabelLayout
                                    = //
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

                            TextLayout mLayout
                                    = new TextLayout(
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

        // draw and label axes
        g2d.drawRect(
                getLeftMargin(), getTopMargin(), (int) getGraphWidth() - 1, (int) getGraphHeight());

        String xAxisLabel = axes[0].getAxisLabel();
        String yAxisLabel = axes[1].getAxisLabel();

        g2d.setFont(axisLabelAtomicName);
        g2d.drawString(xAxisLabel,
                getLeftMargin() + (int) getGraphWidth() - 8 * axisLabelAtomicNum.getSize2D() - 12,
                getTopMargin() + (int) getGraphHeight() - 18);


        g2d.rotate(-Math.PI / 2.0);
        g2d.drawString(yAxisLabel,
                -(getTopMargin() + 8 * axisLabelAtomicNum.getSize2D()) - 10,
                getLeftMargin() + 24);

        g2d.rotate(Math.PI / 2.0);

    }

    /**
     *
     * @param x
     * @return
     */
    @Override
    public double mapX(double x) {
        return (((x - getXaxisSetup().getMin_Display()) / getRangeX_Display()) * getGraphWidth()) + getLeftMargin();
    }

    /**
     *
     * @param y
     * @return
     */
    @Override
    public double mapY(double y) {
        return (((getYaxisSetup().getMax_Display() - y) / getRangeY_Display()) * getGraphHeight()) + getTopMargin();
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
    @Override
    public double getGraphHeight() {
        return graphHeight;
    }

    /**
     * @param graphHeight the graphHeight to set
     */
    @Override
    public void setGraphHeight(double graphHeight) {
        this.graphHeight = graphHeight;
    }

    /**
     * @return the topMargin
     */
    @Override
    public int getTopMargin() {
        return topMargin;
    }

    /**
     * @param topMargin the topMargin to set
     */
    @Override
    public void setTopMargin(int topMargin) {
        this.topMargin = topMargin;
    }

    /**
     * @return the leftMargin
     */
    @Override
    public int getLeftMargin() {
        return leftMargin;
    }

    /**
     * @param leftMargin the leftMargin to set
     */
    @Override
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
     * @return the cDecimalPlaceCount
     */
    @Override
    public int getcDecimalPlaceCount() {
        return cDecimalPlaceCount;
    }

    /**
     * @param cDecimalPlaceCount the cDecimalPlaceCount to set
     */
    @Override
    public void setcDecimalPlaceCount(int cDecimalPlaceCount) {
        this.cDecimalPlaceCount = cDecimalPlaceCount;
    }

    /**
     * @return the cTicIncrement
     */
    @Override
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
    @Override
    public void setcTicIncrement(int cTicIncrement) {
        this.cTicIncrement = cTicIncrement;
    }

    /**
     * @return the cTicLabelFrequency
     */
    @Override
    public int getcTicLabelFrequency() {
        if (cTicLabelFrequency < 1) {
            cTicLabelFrequency = 1;
        }
        return cTicLabelFrequency;
    }

    /**
     * @param cTicLabelFrequency the cTicLabelFrequency to set
     */
    @Override
    public void setcTicLabelFrequency(int cTicLabelFrequency) {
        this.cTicLabelFrequency = cTicLabelFrequency;
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
    @Override
    public boolean isDoPlotting() {
        return doPlotting;
    }

    /**
     * @param doPlotting the doPlotting to set
     */
    @Override
    public void setDoPlotting(boolean doPlotting) {
        this.doPlotting = doPlotting;
    }
}
