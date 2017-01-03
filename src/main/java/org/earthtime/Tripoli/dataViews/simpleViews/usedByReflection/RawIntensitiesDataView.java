/*
 * RawIntensitiesDataView.java
 *
 * Created Jul 6, 2011
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
package org.earthtime.Tripoli.dataViews.simpleViews.usedByReflection;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import javax.swing.JLayeredPane;
import org.earthtime.Tripoli.dataModels.DataModelFitFunctionInterface;
import org.earthtime.Tripoli.dataModels.DataModelInterface;
import org.earthtime.Tripoli.dataModels.RawIntensityDataModel;
import org.earthtime.Tripoli.dataViews.AbstractRawDataView;
import org.earthtime.Tripoli.fractions.TripoliFraction;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.dataDictionaries.FitFunctionTypeEnum;
import org.earthtime.dataDictionaries.IncludedTypeEnum;
import org.earthtime.utilities.TicGeneratorForAxes;

/**
 *
 * @author James F. Bowring
 */
public class RawIntensitiesDataView extends AbstractRawDataView {

    /**
     *
     */
    public static int DEFAULT_WIDTH_OF_PANE = 128;

    private double[] normalizedBackgroundAquireTimes;
    private double[] backgroundIntensities;
    private double[] backgroundFitIntensities;
    private double[] onPeakIntensityUncorrectedForMercury;
    private double[] backgroundIntensityUncorrectedForMercury;
    private double[] onPeakFitBackgroundIntensities;

    /**
     *
     * @param sampleSessionDataView
     * @param tripoliFraction
     * @param rawIsotopeDataModel
     * @param bounds
     * @param invokeMouseListener
     */
    public RawIntensitiesDataView(//
            JLayeredPane sampleSessionDataView,//
            TripoliFraction tripoliFraction,//
            DataModelInterface rawIsotopeDataModel,//
            Rectangle bounds,//
            boolean invokeMouseListener) {
        super(sampleSessionDataView, tripoliFraction, bounds, invokeMouseListener, true);

        this.rawRatioDataModel = rawIsotopeDataModel;
        this.normalizedBackgroundAquireTimes = null;
    }

    /**
     *
     * @param g2d
     */
    @Override
    public void paint(Graphics2D g2d) {
        super.paint(g2d);

        if (rawRatioDataModel.isBelowDetection()) {
            setBackground(ReduxConstants.palePinkBelowDetection);
            g2d.drawString("BELOW DETECTION", 25, 25);
        }

        // draw background intensities
        for (int i = 0; i < backgroundIntensities.length; i++) {
            Shape intensity = new java.awt.geom.Ellipse2D.Double(//
                    mapX(normalizedBackgroundAquireTimes[i]), mapY(backgroundIntensities[i]), 1, 1);
            g2d.setPaint(Color.BLACK);
            g2d.draw(intensity);

            // test for presence of fitfunction (ie mercury has none)
            if (!((DataModelFitFunctionInterface) rawRatioDataModel).getSelectedFitFunctionType().equals(FitFunctionTypeEnum.NONE)) {
                Shape background = new java.awt.geom.Ellipse2D.Double( //
                        mapX(normalizedBackgroundAquireTimes[i]), mapY(backgroundFitIntensities[i]), 1, 1);
                g2d.setPaint(Color.RED);
                g2d.draw(background);
            }

            // check for presence of mercury corrections
            if (onPeakIntensityUncorrectedForMercury[0] != 0.0) {
                Shape correction = new java.awt.geom.Ellipse2D.Double( //
                        mapX(normalizedBackgroundAquireTimes[i]), mapY(backgroundIntensityUncorrectedForMercury[i]), 1, 1);
                g2d.setPaint(Color.GREEN);
                g2d.draw(correction);
            }
        }

        shiftAquiredTimeIndex = normalizedBackgroundAquireTimes[normalizedBackgroundAquireTimes.length - 1] + 1;
        for (int i = 0; i < myOnPeakData.length; i++) {
            Shape intensity = new java.awt.geom.Ellipse2D.Double( //
                    mapX(shiftAquiredTimeIndex + myOnPeakNormalizedAquireTimes[i]), mapY(myOnPeakData[i]), 1, 1);
            g2d.setPaint(determineDataColor(i, Color.black));

            g2d.draw(intensity);

            // test for presence of fitfunction (ie mercury has none)
            if (!((DataModelFitFunctionInterface) rawRatioDataModel).getSelectedFitFunctionType().equals(FitFunctionTypeEnum.NONE)) {
                Shape background = new java.awt.geom.Ellipse2D.Double( //
                        mapX(shiftAquiredTimeIndex + myOnPeakNormalizedAquireTimes[i]), mapY(onPeakFitBackgroundIntensities[i]), 1, 1);
                g2d.setPaint(Color.BLUE);
                g2d.draw(background);
            }

            // check for presence of mercury corrections
            if (onPeakIntensityUncorrectedForMercury[0] != 0.0) {
                Shape correction = new java.awt.geom.Ellipse2D.Double( //
                        mapX(shiftAquiredTimeIndex + myOnPeakNormalizedAquireTimes[i]), mapY(onPeakIntensityUncorrectedForMercury[i]), 1, 1);
                g2d.setPaint(determineDataColor(i, Color.GREEN));
                g2d.draw(correction);
            }
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

        // walk intensities and get min and max for axes
        backgroundIntensities = ((RawIntensityDataModel) rawRatioDataModel).getBackgroundCountsPerSecondAsRawIntensities();//     .getBackgroundVirtualCollector().getIntensities();
        backgroundFitIntensities = ((RawIntensityDataModel) rawRatioDataModel).getBackgroundFitCountsPerSecondAsRawIntensities();//  .getBackgroundVirtualCollector().getFitBackgroundIntensities();

        myOnPeakData = ((RawIntensityDataModel) rawRatioDataModel).getOnPeakCountsPerSecondAsRawIntensities();//   .getOnPeakVirtualCollector().getIntensities();

        // recalculate original un-mercury corrected on peak data (only pertains to Pb204)
        double[] onPeakMercuryCorrections
                = ((RawIntensityDataModel) rawRatioDataModel).getOnPeakCountsPerSecondCorrectionsAsRawIntensities();//   .getOnPeakVirtualCollector().getIntensityCorrections();
        // only if corrected do we uncorrect
        onPeakIntensityUncorrectedForMercury = new double[onPeakMercuryCorrections.length];
        if (onPeakMercuryCorrections[0] != 0.0) {
            for (int i = 0; i < myOnPeakData.length; i++) {
                onPeakIntensityUncorrectedForMercury[i] = myOnPeakData[i] + onPeakMercuryCorrections[i];
            }

            // recalculate original un-mercury corrected background data (only pertains to Pb204)
            double[] backgroundMercuryCorrections
                    = ((RawIntensityDataModel) rawRatioDataModel).getBackgroundCountsPerSecondCorrectionsAsRawIntensities();//   .getBackgroundVirtualCollector().getIntensityCorrections();
            backgroundIntensityUncorrectedForMercury = new double[backgroundMercuryCorrections.length];
            for (int i = 0; i < backgroundIntensities.length; i++) {
                backgroundIntensityUncorrectedForMercury[i] = backgroundIntensities[i] + backgroundMercuryCorrections[i];
            }
        }

        onPeakFitBackgroundIntensities = ((RawIntensityDataModel) rawRatioDataModel).getOnPeakFitCountsPerSecondAsRawIntensities();// .getOnPeakVirtualCollector().getFitBackgroundIntensities();

        // normalize aquireTimes
        normalizedBackgroundAquireTimes = ((RawIntensityDataModel) rawRatioDataModel).getNormalizedBackgroundAquireTimes();

        myOnPeakNormalizedAquireTimes = rawRatioDataModel.getNormalizedOnPeakAquireTimes();
        boolean[] myDataActiveMap = rawRatioDataModel.getDataActiveMap();
        boolean showAll = showIncludedDataPoints.equals(IncludedTypeEnum.ALL);
        // rework logic April 2016 

        if (doReScale) {
            setDisplayOffsetY(0.0);

            setDisplayOffsetX(0.0);

            // X-axis lays out time evenly spaced
            minX = normalizedBackgroundAquireTimes[0];
            maxX = normalizedBackgroundAquireTimes[normalizedBackgroundAquireTimes.length - 1]//
                    + myOnPeakNormalizedAquireTimes[myOnPeakNormalizedAquireTimes.length - 1] + 1;// say 0...14 and 15...29
            double xMarginStretch = TicGeneratorForAxes.generateMarginAdjustment(minX, maxX, 0.05);
            minX -= xMarginStretch;
            maxX += xMarginStretch;

            // Y-axis is intensities as voltages plus or minus
            // find min and max y
            minY = Double.MAX_VALUE;
            maxY = -Double.MAX_VALUE;

            // background
            for (int i = 0; i < backgroundIntensities.length; i++) {
                minY = Math.min(minY, backgroundIntensities[i]);
                maxY = Math.max(maxY, backgroundIntensities[i]);

                if (!((DataModelFitFunctionInterface) rawRatioDataModel).getSelectedFitFunctionType().equals(FitFunctionTypeEnum.NONE)) {
                    minY = Math.min(minY, backgroundFitIntensities[i]);
                    maxY = Math.max(maxY, backgroundFitIntensities[i]);
                }

                if (onPeakMercuryCorrections[0] != 0.0) {
                    minY = Math.min(minY, backgroundIntensityUncorrectedForMercury[i]);
                    maxY = Math.max(maxY, backgroundIntensityUncorrectedForMercury[i]);
                }
            }

            // on peak
            for (int i = 0; i < myOnPeakData.length; i++) {
                if (showAll || myDataActiveMap[i]) {
                    if (Double.isFinite(myOnPeakData[i])) {
                        minY = Math.min(minY, myOnPeakData[i]);
                        maxY = Math.max(maxY, myOnPeakData[i]);
                    }

                    if (!((DataModelFitFunctionInterface) rawRatioDataModel).getSelectedFitFunctionType().equals(FitFunctionTypeEnum.NONE)) {
                        minY = Math.min(minY, onPeakFitBackgroundIntensities[i]);
                        maxY = Math.max(maxY, onPeakFitBackgroundIntensities[i]);
                    }

                    if (onPeakMercuryCorrections[0] != 0.0) {
                        minY = Math.min(minY, onPeakIntensityUncorrectedForMercury[i]);
                        maxY = Math.max(maxY, onPeakIntensityUncorrectedForMercury[i]);
                    }
                }

            }
            // adjust margins for unknowns
            double yMarginStretch = TicGeneratorForAxes.generateMarginAdjustment(minY, maxY, 0.05);
            minY -= yMarginStretch;
            maxY += yMarginStretch;
        }
    }

    /**
     * @return the rawRatioDataModel
     */
    @Override
    public DataModelInterface getDataModel() {
        return rawRatioDataModel;
    }
}
