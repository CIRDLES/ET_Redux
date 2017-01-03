/*
 * Copyright 2006-2017 CIRDLES.org.
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
package org.earthtime.plots;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Vector;
import org.apache.commons.math3.stat.regression.SimpleRegression;
import org.cirdles.mcLeanRegression.McLeanRegression;
import org.cirdles.mcLeanRegression.McLeanRegressionInterface;
import org.cirdles.mcLeanRegression.core.McLeanRegressionLineInterface;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.fractions.ETFractionInterface;

/**
 *
 * @author CIRDLES.org
 */
public class McLeanRegressionLineFit {

    private McLeanRegressionLineInterface mcLeanRegressionLine;

    private McLeanRegressionLineFit() {
    }

    public McLeanRegressionLineFit(Vector<ETFractionInterface> selectedFractions, String nameOfXaxisSourceValueModel, String nameOfYaxisSourceValueModel) {

        mcLeanRegressionLine = null;

        if (selectedFractions.size() > 1) {
            // test if variables present
            boolean proceed = true;
            ValueModel xAxisRatio = selectedFractions.get(0).retrieveValueModelByName(nameOfXaxisSourceValueModel);
            if (xAxisRatio == null) {
                System.out.println("MISSING   " + nameOfXaxisSourceValueModel);
                proceed = false;
            }
            ValueModel yAxisRatio = selectedFractions.get(0).retrieveValueModelByName(nameOfYaxisSourceValueModel);
            if (yAxisRatio == null) {
                System.out.println("MISSING   " + nameOfYaxisSourceValueModel);
                proceed = false;
            }

            if (proceed) {
                // prepare arrays from selected fractions
                double x[] = new double[selectedFractions.size()];
                double y[] = new double[selectedFractions.size()];
                double x1SigmaAbs[] = new double[selectedFractions.size()];
                double y1SigmaAbs[] = new double[selectedFractions.size()];
                double rhos[] = new double[selectedFractions.size()];

                // in case of ordinary linear regression
                double[][] xy = new double[selectedFractions.size()][2];

                int counter = 0;
                for (ETFractionInterface fraction : selectedFractions) {
                    if (!fraction.isRejected()) {
                        ValueModel[] xyRho = fraction.retrieveXYRho(nameOfXaxisSourceValueModel, nameOfYaxisSourceValueModel);
                        xAxisRatio = xyRho[0];
                        yAxisRatio = xyRho[1];
                        ValueModel correlationCoefficient = xyRho[2];

                        x[counter] = xAxisRatio.getValue().doubleValue();
                        xy[counter][0] = x[counter];
                        x1SigmaAbs[counter] = xAxisRatio.getOneSigmaAbs().doubleValue();

                        y[counter] = yAxisRatio.getValue().doubleValue();
                        xy[counter][1] = y[counter];
                        y1SigmaAbs[counter] = yAxisRatio.getOneSigmaAbs().doubleValue();

                        double covXY = correlationCoefficient.getValue().doubleValue();
                        rhos[counter] = covXY;

                        counter++;
                    }
                }

                // trim arrays
                double xF[] = partArray(x, counter);
                double yF[] = partArray(y, counter);
                double x1SigmaAbsF[] = partArray(x1SigmaAbs, counter);
                double y1SigmaAbsF[] = partArray(y1SigmaAbs, counter);
                double rhosF[] = partArray(rhos, counter);

                // in case of ordinary linear regression
                double[][] xyF = new double[counter][2];
                for (int i = 0; i < (counter); i++) {
                    xyF[i][0] = xy[i][0];
                    xyF[i][1] = xy[i][1];
                }

                try {
                    McLeanRegressionInterface mcLeanRegression = new McLeanRegression();
                    mcLeanRegressionLine = mcLeanRegression.fitLineToDataFor2D(xF, yF, x1SigmaAbsF, y1SigmaAbsF, rhosF);

                    // output to csv for testing with matlab
                    Path dir = Paths.get(".");
                    Path path = dir.resolve(nameOfXaxisSourceValueModel + "_" + nameOfYaxisSourceValueModel + ".csv");
                    try (BufferedWriter writer = Files.newBufferedWriter(path, Charset.forName("UTF-8"))) {
                        for (int i = 0; i < xF.length; i++) {
                            writer.write(
                                    String.valueOf(xF[i]) + ", "
                                    + String.valueOf(x1SigmaAbsF[i]) + ", "
                                    + String.valueOf(yF[i]) + ", "
                                    + String.valueOf(y1SigmaAbsF[i]) + ", "
                                    + String.valueOf(rhosF[i])
                                    + "\n");
                        }
                    } catch (IOException ex) {
                    }
                } catch (Exception e) {
                    // we do an ordinary least squares (OLS)
                    SimpleRegression regression = new SimpleRegression();
                    regression.addData(xy);

                    mcLeanRegressionLine = new McLeanOrdinaryLeastSquaresRegressionLine(regression);
                }
            }
        }
    }

    private double[] partArray(double[] array, int size) {
        double[] part = new double[size];
        System.arraycopy(array, 0, part, 0, size);
        return part;
    }

    /**
     * @return the mcLeanRegressionLine
     */
    public McLeanRegressionLineInterface getMcLeanRegressionLine() {
        return mcLeanRegressionLine;
    }

    public class McLeanOrdinaryLeastSquaresRegressionLine
            implements McLeanRegressionLineInterface {

        private SimpleRegression regression;
        private double[][] a;
        private double[][] v;

        public McLeanOrdinaryLeastSquaresRegressionLine(SimpleRegression regression) {
            this.regression = regression;

            a = new double[2][1];
            a[1][0] = regression.getIntercept();

            v = new double[2][1];
            v[1][0] = regression.getSlope();
        }

        @Override
        public double[][] getA() {
            return a;
        }

        @Override
        public double[][] getV() {
            return v;
        }

        @Override
        public double[][] getSav() {
            return null;
        }

        @Override
        public double getMSWD() {
            return 0.0;
        }

        @Override
        public int getN() {
            return (int) regression.getN();
        }

    }
}
