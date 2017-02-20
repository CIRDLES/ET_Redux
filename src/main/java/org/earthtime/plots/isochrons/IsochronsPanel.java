/*
 * IsochronsPanel.java
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
package org.earthtime.plots.isochrons;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.dataDictionaries.Lambdas;
import org.earthtime.dataDictionaries.UThAnalysisMeasures;
import org.earthtime.plots.AbstractPlot;
import org.earthtime.reportViews.ReportUpdaterInterface;
import org.earthtime.samples.SampleInterface;

/**
 *
 * @author James F. Bowring
 */
public class IsochronsPanel extends AbstractPlot {

    private double lambda230;

    /**
     * Creates a new instance of IsochronsPanel
     *
     * @param mySample
     * @param reportUpdater the value of reportUpdater
     */
    public IsochronsPanel(SampleInterface mySample, ReportUpdaterInterface reportUpdater) {
        super(mySample, reportUpdater);

        try {
            lambda230 = sample.getPhysicalConstantsModel().getDatumByName(Lambdas.lambda230.getName()).getValue().doubleValue();
        } catch (BadLabDataException badLabDataException) {
        }

        this.nameOfXaxisSourceValueModel = UThAnalysisMeasures.ar238U_232Thfc.getName();
        this.nameOfYaxisSourceValueModel = UThAnalysisMeasures.ar230Th_232Thfc.getName();
    }

    @Override
    public void paint(Graphics2D g2d, boolean svgStyle) {
        super.paint(g2d, svgStyle);

        g2d.setClip(getLeftMargin(), getTopMargin(), (int) getGraphWidth(), (int) getGraphHeight());

        plot1to1Line(g2d);
        plotIsochronDate(0.9, 1., 0, g2d);
        plotIsochronDate(0.9, 1., 50000, g2d);
    }

    private void plotIsochronDate(double x, double y, double date, Graphics2D g2d) {
        double slope = 1.0 - Math.exp(-1.0 * date * lambda230);
        String label = new BigDecimal(date).movePointLeft(3).setScale(0, RoundingMode.HALF_UP).toPlainString() + " ka";
        plotIsochronLine(x, y, slope, label, g2d);
    }

    private void plotIsochronLine(double x, double y, double slope, String label, Graphics2D g2d) {

        g2d.setPaint(Color.BLACK);

        String axesTicLabelFont = "";
        String axesTicLabelFontSize = "";
        axesTicLabelFont = getStringEntryFromConcordiaOptions("axesTicLabelFont", axesTicLabelFont);
        axesTicLabelFontSize = getStringEntryFromConcordiaOptions("axesTicLabelFontSize", axesTicLabelFontSize);
        g2d.setFont(new Font(
                axesTicLabelFont,
                Font.BOLD,
                Integer.parseInt(axesTicLabelFontSize)));

        double displacementFactor;
        Stroke stroke;
        if (x == 0) {
            // equiline
            displacementFactor = 1.0;
            float[] dash1 = {6.0f, 4.0f, 2.0f, 4.0f, 2.0f, 4.0f};
            stroke = new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10.0f, dash1, 0.0f);
            g2d.setStroke(stroke);
        } else {
            displacementFactor = x * 0.05;
            stroke = new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
            g2d.setStroke(stroke);
        }

        double yIntercept = y - slope * x;

        Line2D line = new Line2D.Double(
                mapX(x),
                mapY(yIntercept + slope * x),
                mapX(getMaxX_Display()),
                mapY(yIntercept + slope * getMaxX_Display()));
        g2d.draw(line);

        double printSlope
                = (mapY(yIntercept + slope * x) - mapY(yIntercept + slope * getMaxX_Display())) / (mapX(getMaxX_Display()) - mapX(x));

        double rotateAngle = StrictMath.atan(printSlope);
        g2d.rotate(-rotateAngle,
                (float) mapX(x + displacementFactor),
                (float) mapY(yIntercept + slope * (x + displacementFactor)));

        g2d.drawString(label,
                (float) mapX((x + displacementFactor)),
                (float) mapY(yIntercept + slope * (x + displacementFactor)) - 2f);

        g2d.rotate(rotateAngle,
                (float) mapX((x + displacementFactor)),
                (float) mapY(yIntercept + slope * (x + displacementFactor)));

    }

    private void plot1to1Line(Graphics2D g2d) {
        plotIsochronLine(0., 0., 1.0, "Equiline", g2d);
    }
}
