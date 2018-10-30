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
import java.awt.geom.CubicCurve2D;
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
import org.earthtime.dataDictionaries.UThAnalysisMeasures;
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
public final class EvolutionPlotPanelII extends AbstractDataView implements PlottingDetailsDisplayInterface {

    protected transient ReportUpdaterInterface reportUpdater;

    private static SampleInterface sample;
    private static boolean showMatrix = true;

    private double[] annumIsochrons;
    private double[] ar48icntrs;

    private double[][] xEndPointsD;
    private double[][] yEndPointsD;

    double[][] tv;
    private double[][][] xy;
    private double[][][] dardt;

    public EvolutionPlotPanelII(SampleInterface mySample, ReportUpdaterInterface reportUpdater) {
        super();

        this.leftMargin = 50;
        this.topMargin = 50;
        this.graphWidth = 500;
        this.graphHeight = 500;

        this.setBounds(0, 0, graphWidth + leftMargin * 2, graphHeight + topMargin * 2);

        setOpaque(true);

        setBackground(Color.white);
        this.sample = mySample;
        this.reportUpdater = reportUpdater;

        this.selectedFractions = new Vector<>();
        this.excludedFractions = new Vector<>();

        this.annumIsochrons = new double[]{};
        this.ar48icntrs = new double[]{};
        this.xAxisMax = 0;
        this.yAxisMax = 0;
        this.xEndPointsD = new double[][]{{}};
        this.yEndPointsD = new double[][]{{}};
        this.tv = new double[][]{{}};
        this.xy = new double[0][0][0];
        this.dardt = new double[0][0][0];

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
        g2d.drawRect(leftMargin, topMargin, (int) graphWidth, (int) graphHeight);

        // draw isochrons
        double[] yItercepts = new double[xEndPointsD[0].length];
        double[] slopes = new double[xEndPointsD[0].length];
        g2d.setPaint(Color.red);
        for (int i = 0; i < annumIsochrons.length; i++) {
            slopes[i] = (yEndPointsD[1][i] - yEndPointsD[0][i]) / (xEndPointsD[1][i] - xEndPointsD[0][i]);
            yItercepts[i] = yEndPointsD[0][i] - slopes[i] * xEndPointsD[0][i];

            Shape isochronLine = new Path2D.Double();
            g2d.setStroke(new BasicStroke(1.75f));
            ((Path2D) isochronLine).moveTo(//
                    mapX(xEndPointsD[0][i]), //
                    mapY(yItercepts[i] + slopes[i] * xEndPointsD[0][i]));
            ((Path2D) isochronLine).lineTo( //
                    mapX(getMaxX_Display()), //
                    mapY(yItercepts[i] + slopes[i] * getMaxX_Display()));

            if ((i == (annumIsochrons.length - 1))
                    && annumIsochrons[i] >= 10e10) {
                g2d.setPaint(Color.black);
            }
            g2d.draw(isochronLine);

        }

        // draw contour lines
        g2d.setPaint(Color.blue);
        // determine what curves should plot
        int lowIndex = 0;
        int highIndex = ar48icntrs.length;
        for (int index = 0; index < ar48icntrs.length; index++) {
            if (ar48icntrs[index] < getMinY_Display()) {
                lowIndex = index;
            }
            if (ar48icntrs[index] < getMaxY_Display()) {
                highIndex = Math.min(ar48icntrs.length - 1, index + 6 + (zoomCount / 10) * 1);
            }
        }
        for (int i = lowIndex; i < highIndex; i++) {
            boolean labelPrinted = false;
            for (int j = 1; j < tv[i].length; j++) {
                double deltaTOver3 = (tv[i][j] - tv[i][j - 1]) / 3;

                Shape ar48iContour = new CubicCurve2D.Double(
                        mapX(xy[i][0][j - 1]),
                        mapY(xy[i][1][j - 1]),
                        mapX(xy[i][0][j - 1] + deltaTOver3 * dardt[i][0][j - 1]),
                        mapY(xy[i][1][j - 1] + deltaTOver3 * dardt[i][1][j - 1]),
                        mapX(xy[i][0][j] - deltaTOver3 * dardt[i][0][j]),
                        mapY(xy[i][1][j] - deltaTOver3 * dardt[i][1][j]),
                        mapX(xy[i][0][j]),
                        mapY(xy[i][1][j]));

                g2d.draw(ar48iContour);

                DecimalFormat myFormatter = new DecimalFormat("#.##");
                if (!labelPrinted && (xy[i][0][j - 1] > getMinX_Display()) && (ar48icntrs[i] > 0.0)) {
                    double angleOfText = (Math.atan(xy[i][1][j - 1] - xy[i][1][j]) / (xy[i][0][j - 1] - xy[i][0][j]));
//                    System.out.println(ar48icntrs[i] + "  >  " + angleOfText);
                    g2d.rotate(-angleOfText, leftMargin + 20, mapY(xy[i][1][j - 1]));
                    g2d.drawString(myFormatter.format(ar48icntrs[i]), leftMargin + 10, (float) mapY(xy[i][1][j - 1]) - 5);
                    g2d.rotate(angleOfText, leftMargin + 20, mapY(xy[i][1][j - 1]));
                    labelPrinted = true;
                }

            }
        }

        g2d.setPaint(Color.black);

        Color includedBorderColor = Color.BLACK;
        Color includedCenterColor = new Color(255, 0, 0);
        float includedCenterSize = 3.0f;
        String ellipseLabelFont = "Monospaced";
        String ellipseLabelFontSize = "12";

        for (ETFractionInterface f : selectedFractions) {
            if (!f.isRejected()) {
                generateEllipsePathIII(//
                        f,
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
        // draw and label isochron axes - top and right
        g2d.setFont(new Font("Monospaced", Font.BOLD, 12));

        for (int i = 0; i < annumIsochrons.length; i++) {

            String label = " " + new BigDecimal(annumIsochrons[i]).movePointLeft(3).setScale(0, RoundingMode.HALF_UP).toPlainString() + " ka";
            // set infinity label
            if (annumIsochrons[i] >= 10e10) {
                label = " \u221E";
            }

            double rotateAngle = StrictMath.atan(slopes[i]);

            double labelX = ((yItercepts[i] + slopes[i] * getMaxX_Display()) < (getMaxY_Display() - 0.000)) ? getMaxX_Display() : (getMaxY_Display() - yItercepts[i]) / slopes[i];
            double labelY = slopes[i] * labelX + yItercepts[i];
            float displacementFactorX = 0f;
            float displacementFactorY = -6f;

            if ((labelX >= getMinX_Display()) && (labelX <= getMaxX_Display()) && (labelY > getMinY_Display())) {
                g2d.rotate(-rotateAngle,
                        (float) mapX(labelX),
                        (float) mapY(yItercepts[i] + slopes[i] * (labelX)));

                g2d.drawString(label,
                        (float) mapX(labelX) + displacementFactorX,
                        (float) mapY(yItercepts[i] + slopes[i] * (labelX)) - displacementFactorY);

                g2d.rotate(rotateAngle,
                        (float) mapX(labelX),
                        (float) mapY(yItercepts[i] + slopes[i] * (labelX)));
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

    @Override
    public void preparePanel(boolean doReset) {
        if (doReset) {

            removeAll();

            maxX = (xAxisMax == 0) ? 1.5 : xAxisMax;
            xAxisMax = maxX;
            maxY = (yAxisMax == 0) ? 2.0 : yAxisMax;
            yAxisMax = maxY;

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

                double xAxisRatio = f.getLegacyActivityRatioByName(UThAnalysisMeasures.ar230Th_238Ufc.getName()).getValue().doubleValue();
                double xAxis2Sigma = f.getLegacyActivityRatioByName(UThAnalysisMeasures.ar230Th_238Ufc.getName()).getTwoSigmaAbs().doubleValue();
                minX = Math.min(minX, xAxisRatio - 2.0 * xAxis2Sigma);
                maxX = Math.max(maxX, xAxisRatio + 2.0 * xAxis2Sigma);

                double yAxisRatio = f.getLegacyActivityRatioByName(UThAnalysisMeasures.ar234U_238Ufc.getName()).getValue().doubleValue();
                double yAxis2Sigma = f.getLegacyActivityRatioByName(UThAnalysisMeasures.ar234U_238Ufc.getName()).getTwoSigmaAbs().doubleValue();
                minY = Math.min(minY, yAxisRatio - 2.0 * yAxis2Sigma);
                maxY = Math.max(maxY, yAxisRatio + 2.0 * yAxis2Sigma);
            }
        }

        // maintain 2/1.5 aspect ratio
        if (getRangeY_Display() / getRangeX_Display() < 2.0 / 1.5) {
            // increase Y
            double yDelta = (2.0 / 1.5) * getRangeX_Display() - getRangeY_Display();
            minY -= yDelta / 2.0;
            maxY += yDelta / 2.0;

            zoomCount = (int) Math.min(10.0, 10.0 / getRangeY_Display());
        } else {
            // increase x
            double xDelta = (1.5 / 2.0) * getRangeY_Display() - getRangeX_Display();
            minX -= xDelta / 2.0;
            maxX += xDelta / 2.0;

            zoomCount = (int) Math.min(10.0, 10.0 / getRangeX_Display());
        }

        buildIsochronsAndContours();

        ticsYaxis = TicGeneratorForAxes.generateTics(getMinY_Display(), getMaxY_Display(), 10);
        ticsXaxis = TicGeneratorForAxes.generateTics(getMinX_Display(), getMaxX_Display(), 10);

        repaint();
        validate();

    }

    public void showLoose() {
        // determine default zoom  
        zoomMaxX = 0;
        zoomMaxY = 0;
        zoomMinX = 0;
        zoomMinY = 0;

        displayOffsetX = 0.0;
        displayOffsetY = 0.0;

        zoomCount = 0;

        minX = 0.0;
        maxX = 1.5;
        xAxisMax = maxX;

        minY = 0.0;
        maxY = 2.0;
        yAxisMax = maxY;

        buildIsochronsAndContours();

        ticsYaxis = TicGeneratorForAxes.generateTics(getMinY_Display(), getMaxY_Display(), 10);
        ticsXaxis = TicGeneratorForAxes.generateTics(getMinX_Display(), getMaxX_Display(), 10);

        repaint();
        validate();
    }

    public void buildIsochronsAndContours() {
        // math adapted from Noah's matlab code 
        ValueModel sampleDateModel = ((SampleDateModel) sample.getSampleDateModelByName("DEFAULT"));
        SortedSet<IsochronModel> selectedIsochrons = ((SampleDateModel) sampleDateModel).getIsochronModels();
        List<Double> annumList = new ArrayList<>();
        Iterator<IsochronModel> isochronIterator = selectedIsochrons.iterator();
        while (isochronIterator.hasNext()) {
            IsochronModel isochronModel = isochronIterator.next();
            if (((SampleDateModel) sampleDateModel).isAutomaticIsochronSelection()) {
                isochronModel.setVisible(
                        (zoomCount >= 0) ? (isochronModel.getDensityLevel() <= (zoomCount / 4))
                                : (isochronModel.getDensityLevel() == 0));
            }
            if (isochronModel.isVisible()) {
                annumList.add(isochronModel.getDateInAnnum());
            }
        }

        annumIsochrons = annumList.stream().mapToDouble(Double::doubleValue).toArray();

//        annumIsochrons = new double[]{25.0e3, 50.0e3, 75.0e3, 100.0e3, 150.0e3, 200.0e3, 300.0e3, 500.0e3, 10e16}; // plotted isochrons
//
//        if (zoomCount >= 25) {
//            annumIsochrons = new double[]{25.0e3, 50.0e3, 75.0e3, 85.0e3, 100.0e3, 115.0e3, 140.0e3, 150.0e3, 175.0e3, 200.0e3, 300.0e3, 500.0e3, 10e16}; // plotted isochrons
//        }
//
//        if (zoomCount >= 50) {
//            annumIsochrons = new double[]{25.0e3, 50.0e3, 75.0e3, 85.0e3, 100.0e3, 115.0e3, 130.0e3, 140.0e3, 150.0e3, 160.0e3, 180.0e3, 200.0e3, 225.0e3, 250.0e3, 275.0e3, 300.0e3, 350.0e3, 400.0e3, 500.0e3, 10e16}; // plotted isochrons
//        }
        int init48Density = (int) Math.pow(2, (2 + zoomCount / 10));
        ar48icntrs = new double[(int) maxY * init48Density + init48Density + 1];
        for (int i = 0; i < ar48icntrs.length; i++) {
            ar48icntrs[i] = i * (double) (1.0 / (double) init48Density);
        }

        Matrix ar48limYaxis = new Matrix(new double[][]{{0.0, maxY * 1}});
        Matrix ar08limXaxis = new Matrix(new double[][]{{0.0, maxX * 1}});

        // % Calculations for isochron line parameters and endpoints
        int nisochrons = annumIsochrons.length;
        Matrix r48lim = ar48limYaxis.times(lambda238D / lambda234D);
        Matrix r08lim = ar08limXaxis.times(lambda238D / lambda230D);

        // Calculations for isochron slope/y-intercept, in isotope ratio coordinates (not activity ratios)
        Matrix abmat = new Matrix(2, nisochrons, 0.0);
        Matrix xminpoints = new Matrix(1, nisochrons, 0.0);
        Matrix yminpoints = new Matrix(1, nisochrons, 0.0);

        int it = -1;
        for (int i = 0; i < nisochrons; i++) {
            double t = annumIsochrons[i];
            it++;

            if (t >= 10e13) {
                abmat.set(1, it, lambda230D / lambda234D - 1.0); //% note: works, but not sure how to evaluate this limit
                abmat.set(0, it, lambda238D / (lambda230D - lambda238D)); // y-int with above slope through transient eqbm

                xminpoints.set(0, it, matrixQUTh().get(2, 0) / matrixQUTh().get(0, 0)); // limit is transient eqbm.  Lower starts all end up here after ~5 Myr
                yminpoints.set(0, it, matrixQUTh().get(1, 0) / matrixQUTh().get(0, 0));

            } else {// finite t
                Matrix mxpNegAt = matrixUTh(-t);
                abmat.set(1, it, -mxpNegAt.get(2, 2) / mxpNegAt.get(2, 1));// slope

                Matrix mxpAt = matrixUTh(t);
                double XX = -mxpAt.get(2, 0) / mxpAt.get(2, 1);
                abmat.set(0, it, matrixUTh4(t).times((new Matrix(new double[][]{{1.0, XX, 0.0}})).transpose()).get(0, 0));   // y-int
                Matrix mxpAtmin = mxpAt.times((new Matrix(new double[][]{{1.0, 0.0, 0.0}})).transpose());

                xminpoints.set(0, it, mxpAtmin.get(2, 0) / mxpAtmin.get(0, 0));
                yminpoints.set(0, it, mxpAtmin.get(1, 0) / mxpAtmin.get(0, 0));
            }
        }

        // y-coord of intersections with left boundary of box
        Matrix leftBorder = abmat.getMatrix(0, 0, 0, nisochrons - 1).plus(abmat.getMatrix(1, 1, 0, nisochrons - 1).times(r08lim.get(0, 0)));
        // y-coord of intersections with right boundary of box
        Matrix rightBorder = abmat.getMatrix(0, 0, 0, nisochrons - 1).plus(abmat.getMatrix(1, 1, 0, nisochrons - 1).times(r08lim.get(0, 1)));
        // x-coord of intersections with bottom boundary of box
        Matrix bottomBorder = (new Matrix(1, nisochrons, r48lim.get(0, 0))).minus(abmat.getMatrix(0, 0, 0, nisochrons - 1)).arrayRightDivide(abmat.getMatrix(1, 1, 0, nisochrons - 1));
        // x-coord of intersections with top boundary of box
        Matrix topBorder = (new Matrix(1, nisochrons, r48lim.get(0, 1))).minus(abmat.getMatrix(0, 0, 0, nisochrons - 1)).arrayRightDivide(abmat.getMatrix(1, 1, 0, nisochrons - 1));

        xEndPointsD = new double[2][nisochrons];
        yEndPointsD = new double[2][nisochrons];
        for (int col = 0; col < nisochrons; col++) {
            xEndPointsD[0][col] = leftBorder.get(0, col) > r48lim.get(0, 0) ? r08lim.get(0, 0) : 0.0;
            xEndPointsD[0][col] += (leftBorder.get(0, col) <= r48lim.get(0, 0) ? bottomBorder.get(0, col) : 0.0);
            xEndPointsD[0][col] = Math.max(xEndPointsD[0][col], xminpoints.get(0, col));
            xEndPointsD[0][col] *= lambda230D / lambda238D;

            xEndPointsD[1][col] = rightBorder.get(0, col) < r48lim.get(0, 1) ? r08lim.get(0, 1) : 0.0;
            xEndPointsD[1][col] += (rightBorder.get(0, col) >= r48lim.get(0, 1) ? topBorder.get(0, col) : 0.0);
            xEndPointsD[1][col] *= lambda230D / lambda238D;

            yEndPointsD[0][col] = leftBorder.get(0, col) > r48lim.get(0, 0) ? leftBorder.get(0, col) : 0.0;
            yEndPointsD[0][col] += (leftBorder.get(0, col) <= r48lim.get(0, 0) ? r48lim.get(0, 0) : 0.0);
            yEndPointsD[0][col] = Math.max(yEndPointsD[0][col], yminpoints.get(0, col));
            yEndPointsD[0][col] *= lambda234D / lambda238D;

            yEndPointsD[1][col] = rightBorder.get(0, col) < r48lim.get(0, 1) ? rightBorder.get(0, col) : 0.0;
            yEndPointsD[1][col] += (rightBorder.get(0, col) >= r48lim.get(0, 1) ? r48lim.get(0, 1) : 0.0);
            yEndPointsD[1][col] *= lambda234D / lambda238D;
        }

        // calculate ar48i contours
        int nts = 200;//10; // number of segments - need 200 to position label well
        // build array of vectors of evenly spaced values with last value = 2e6
        tv = new double[ar48icntrs.length][nts];
        for (int i = 0; i < (nts - 1); i++) {
            double colVal = (double) (i * 1.0e6 / (double) (nts - 2));
            for (double[] tv1 : tv) {
                tv1[i] = colVal;
            }
        }
        for (double[] tv1 : tv) {
            tv1[nts - 1] = 2e6;
        }

        xy = new double[ar48icntrs.length][2][nts];
        dardt = new double[ar48icntrs.length][2][nts];

        int iar48i = -1;

        for (double ar48i : ar48icntrs) {
            iar48i++;
            it = -1;

            for (double t : tv[iar48i]) {
                it++;
                Matrix n0 = new Matrix(new double[][]{{1, ar48i * lambda238D / lambda234D, 0}}).transpose();

                Matrix nt = matrixUTh(t).times(n0);

                xy[iar48i][0][it] = nt.get(2, 0) / nt.get(0, 0) * lambda230D / lambda238D;
                xy[iar48i][1][it] = nt.get(1, 0) / nt.get(0, 0) * lambda234D / lambda238D;

                double dar48dnt1 = -nt.get(1, 0) / nt.get(0, 0) / nt.get(0, 0) * lambda234D / lambda238D;
                double dar48dnt2 = 1.0 / nt.get(0, 0) * lambda234D / lambda238D;
                double dar48dnt3 = 0;
                double dar08dnt1 = -nt.get(2, 0) / nt.get(0, 0) / nt.get(0, 0) * lambda230D / lambda238D;
                double dar08dnt2 = 0;
                double dar08dnt3 = 1.0 / nt.get(0, 0) * lambda230D / lambda238D;

                Matrix dardnt = new Matrix(new double[][]{{dar08dnt1, dar08dnt2, dar08dnt3}, {dar48dnt1, dar48dnt2, dar48dnt3}});
                Matrix dntdt = matrixA().times(matrixUTh(t)).times(n0);

                dardt[iar48i][0][it] = dardnt.times(dntdt).get(0, 0);
                dardt[iar48i][1][it] = dardnt.times(dntdt).get(1, 0);
            }
        }
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
     * @param annumIsochrons the annumIsochrons to set
     */
    public void setAnnumIsochrons(double[] annumIsochrons) {
        this.annumIsochrons = annumIsochrons;
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
            if (true){//(notches == Math.rint(notches)) {
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

                    } else {
                        minX = 0.0;
                        maxX = xAxisMax;
                        minY = 0.0;
                        maxY = yAxisMax;
                        zoomCount = 0;
                    }
                }

                if (minX <= 0.0) {
                    minX = 0.0;
                    displayOffsetX = 0.0;
                }
                if (minY <= 0.0) {
                    minY = 0.0;
                    displayOffsetY = 0.0;
                }

                zoomMinX = zoomMaxX;
                zoomMinY = zoomMaxY;

                buildIsochronsAndContours();
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
