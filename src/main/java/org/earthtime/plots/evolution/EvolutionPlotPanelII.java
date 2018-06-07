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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Path2D;
import java.util.Map;
import java.util.Vector;
import org.earthtime.Tripoli.dataModels.DataModelInterface;
import org.earthtime.Tripoli.dataViews.AbstractRawDataView;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.AliquotDetailsDisplayInterface;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.fractions.ETFractionInterface;
import org.earthtime.reportViews.ReportUpdaterInterface;
import org.earthtime.samples.SampleInterface;
import org.earthtime.utilities.TicGeneratorForAxes;

/**
 *
 * @author James F. Bowring
 */
public final class EvolutionPlotPanelII extends AbstractRawDataView implements AliquotDetailsDisplayInterface {

    protected transient ReportUpdaterInterface reportUpdater;

    private static final String X = "x";
    private static final String SIGMA_X = "sigma_x";
    private static final String Y = "y";
    private static final String SIGMA_Y = "sigma_y";
    private static final String RHO = "rho";
    private static final String SELECTED = "Selected";

    private static SampleInterface sample;
    private static boolean showMatrix = true;

    private ValueModel lambda234;
    private ValueModel lambda238;
    private ValueModel lambda230;
    private double lambda238D = 1.55125e-10;//lambda238.getValue().doubleValue();
    private double lambda234D = 2.82206e-6;//lambda234.getValue().doubleValue();
    private double lambda230D = 9.1705e-6;//lambda230.getValue().doubleValue();

    private static Vector<ETFractionInterface> selectedFractions;
    private Vector<ETFractionInterface> filteredFractions;
    private Vector<ETFractionInterface> excludedFractions;

    private double[][] xEndPointsD;
    private double[][] yEndPointsD;

    double[][] tv;
    private double[][][] xy;
    private double[][][] dardt;

    public EvolutionPlotPanelII(SampleInterface mySample, ReportUpdaterInterface reportUpdater) {
        super();

        this.sample = mySample;
        this.reportUpdater = reportUpdater;

        selectedFractions = new Vector<>();
        excludedFractions = new Vector<>();

        leftMargin = 100;
        topMargin = 100;

        this.setBounds(leftMargin, topMargin, 500, 500);
        graphWidth = getBounds().width - leftMargin;
        graphHeight = getBounds().height - topMargin;

        setOpaque(true);

        setBackground(Color.white);

//        lambda234 = selectedFractions.get(0)
//                .getPhysicalConstantsModel().getDatumByName(Lambdas.lambda234.getName());
//        lambda238 = selectedFractions.get(0)
//                .getPhysicalConstantsModel().getDatumByName(Lambdas.lambda238.getName());
//        lambda230 = selectedFractions.get(0)
//                .getPhysicalConstantsModel().getDatumByName(Lambdas.lambda230.getName());
        lambda238D = 1.55125e-10;//lambda238.getValue().doubleValue();
        lambda234D = 2.82206e-6;//lambda234.getValue().doubleValue();
        lambda230D = 9.1705e-6;//lambda230.getValue().doubleValue();
    }

    public static void main(String[] args) {
        EvolutionPlotPanelII evolutionPlotPanelII = new EvolutionPlotPanelII(null, null);
        evolutionPlotPanelII.preparePanel(false, false);

    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        paint((Graphics2D) g);
    }

    public void paint(Graphics2D g2d) {
        paintInit(g2d);

        g2d.setPaint(Color.black);

        // draw border
        g2d.drawRect(leftMargin, topMargin, (int) graphWidth, (int) graphHeight);

        drawTicsYAxisInBackground(g2d);

        for (int i = 0; i < xEndPointsD[0].length; i++) {
            Shape isochronLine = new Path2D.Double();
            g2d.setPaint(Color.black);
            g2d.setStroke(new BasicStroke(1.75f));
            ((Path2D) isochronLine).moveTo(//
                    mapX(xEndPointsD[0][i]), //
                    mapY(yEndPointsD[0][i]));
            ((Path2D) isochronLine).lineTo( //
                    mapX(xEndPointsD[1][i]), //
                    mapY(yEndPointsD[1][i]));
            g2d.draw(isochronLine);
        }

        for (int i = 0; i < xy.length; i++) {
            for (int j = 1; j < tv[i].length; j++) {
                double deltaTOver3 = (tv[i][j] - tv[i][j - 1]) / 3;
                Shape ar48iContour = new CubicCurve2D.Double(
                        mapX(xy[i][0][j-1]),
                        mapY(xy[i][1][j-1]),
                        mapX(xy[i][0][j - 1] + deltaTOver3 * dardt[i][0][j - 1]),
                        mapY(xy[i][1][j - 1] + deltaTOver3 * dardt[i][1][j - 1]),
                        mapX(xy[i][0][j] - deltaTOver3 * dardt[i][0][j]),
                        mapY(xy[i][1][j] - deltaTOver3 * dardt[i][1][j]),
                        mapX(xy[i][0][j]),
                        mapY(xy[i][1][j]));
                
                g2d.draw(ar48iContour);
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

    @Override
    public void preparePanel(boolean doReScale, boolean inLiveMode) {
        removeAll();

        if (true) {;//selectedFractions.size() > 0) {
            // adapted from Noah's matlab code 
            double[] tisochrons = new double[]{25.0e3, 50.0e3, 75.0e3, 100.0e3, 150.0e3, 200.0e3, 300.0e3, 10e13}; // plotted isochrons
            double[] ar48icntrs = new double[]{0.0, 0.25, 0.5, 0.75, 1, 1.25, 1.5, 1.75, 2.0, 2.25}; // contour lines of constant 234/238i
            Matrix ar48lim = new Matrix(new double[][]{{0.0, 2.0}});
            Matrix ar08lim = new Matrix(new double[][]{{0.0, 1.5}});

            // %% Calculations for isochron line parameters and endpoints
            int nisochrons = tisochrons.length;
            Matrix r48lim = ar48lim.times(lambda238D / lambda234D);
            Matrix r08lim = ar08lim.times(lambda238D / lambda230D);

            // Calculations for isochron slope/y-intercept, in isotope ratio coordinates (not activity ratios)
            Matrix abmat = new Matrix(2, nisochrons, 0.0);
            Matrix xminpoints = new Matrix(1, nisochrons, 0.0);
            Matrix yminpoints = new Matrix(1, nisochrons, 0.0);

            int it = -1;
            for (int i = 0; i < nisochrons; i++) {
                double t = tisochrons[i];
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
            int nts = 10; // number of segments
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

            repaint();
            validate();

            // X-axis i
            minX = 0.0;
            maxX = 1.5;

            // Y-axis 
            minY = 0.0;
            maxY = 2.0;

            tics = TicGeneratorForAxes.generateTics(minY, maxY, (int) (graphHeight / 20.0));
        }

        repaint();
    }

    @Override
    public Map<String, Map<String, String>> getAliquotOptions() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector<ETFractionInterface> getDeSelectedFractions() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setDeSelectedFractions(Vector<ETFractionInterface> deSelectedFractions) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<String, String> getSelectedAliquotOptions() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setSelectedFractions(Vector<ETFractionInterface> fractions) {
        selectedFractions = fractions;
    }

    @Override
    public void setFilteredFractions(Vector<ETFractionInterface> filteredFractions) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector<ETFractionInterface> getSelectedFractions() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isShowFilteredEllipses() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setShowFilteredEllipses(boolean showFilteredEllipses) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public DataModelInterface getDataModel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
