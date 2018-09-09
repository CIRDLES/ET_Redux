/*
 * CorrectedRatioDataView.java
 *
 * Copyright 2006-2018 James F. Bowring, CIRDLES.org, and Earth-Time.org
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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Path2D;
import javax.swing.JLayeredPane;
import org.earthtime.Tripoli.dataModels.DataModelInterface;
import org.earthtime.Tripoli.dataModels.RawRatioDataModel;
import org.earthtime.Tripoli.dataViews.AbstractRawDataView;
import org.earthtime.Tripoli.fractions.TripoliFraction;
import org.earthtime.dataDictionaries.IncludedTypeEnum;
import org.earthtime.utilities.TicGeneratorForAxes;

/**
 *
 * @author James F. Bowring
 */
public class CorrectedRatioDataView extends AbstractRawDataView {

    /**
     *
     */
    public static int DEFAULT_WIDTH_OF_PANE = 128;

    /**
     *
     * @param sampleSessionDataView
     * @param tripoliFraction
     * @param rawRatioDataModel
     * @param bounds
     * @param invokeMouseListener
     */
    public CorrectedRatioDataView(//
            JLayeredPane sampleSessionDataView,
            TripoliFraction tripoliFraction,//
            DataModelInterface rawRatioDataModel,//
            Rectangle bounds,//
            boolean invokeMouseListener) {
        super(sampleSessionDataView, tripoliFraction, bounds, invokeMouseListener, true);

        this.rawRatioDataModel = rawRatioDataModel;
    }

    /**
     *
     * @param g2d
     */
    @Override
    public void paint(Graphics2D g2d) {
        //super.paint( g2d );

        paintInit(g2d);

        double mean = ((RawRatioDataModel) rawRatioDataModel).getMeanOfCorrectedRatios();
        double sigma = ((RawRatioDataModel) rawRatioDataModel).getStdDevOfCorrectedRatios();
        double stdErr = ((RawRatioDataModel) rawRatioDataModel).getStdErrOfMeanCorrectedRatios();

        //draw two-sigma
        Path2D twoSigmaArea = new Path2D.Double();
        twoSigmaArea.moveTo(mapX(minX), mapY(mean - 2.0 * sigma));
        twoSigmaArea.lineTo(mapX(minX), mapY(mean + 2.0 * sigma));
        twoSigmaArea.lineTo(mapX(maxX), mapY(mean + 2.0 * sigma));
        twoSigmaArea.lineTo(mapX(maxX), mapY(mean - 2.0 * sigma));
        twoSigmaArea.closePath();
        // pale red
        g2d.setColor(new Color(255, 233, 235));
        g2d.fill(twoSigmaArea);

        //draw one-sigma
        Path2D oneSigmaArea = new Path2D.Double();
        oneSigmaArea.moveTo(mapX(minX), mapY(mean - sigma));
        oneSigmaArea.lineTo(mapX(minX), mapY(mean + sigma));
        oneSigmaArea.lineTo(mapX(maxX), mapY(mean + sigma));
        oneSigmaArea.lineTo(mapX(maxX), mapY(mean - sigma));
        oneSigmaArea.closePath();
        // pale yellow
        g2d.setColor(new Color(254, 255, 233));
        g2d.fill(oneSigmaArea);

        //draw std err
        Path2D stdErrArea = new Path2D.Double();
        stdErrArea.moveTo(mapX(minX), mapY(mean - stdErr));
        stdErrArea.lineTo(mapX(minX), mapY(mean + stdErr));
        stdErrArea.lineTo(mapX(maxX), mapY(mean + stdErr));
        stdErrArea.lineTo(mapX(maxX), mapY(mean - stdErr));
        stdErrArea.closePath();
        // pale blue
        g2d.setColor(new Color(208, 222, 254));
        g2d.fill(stdErrArea);

        // draw mean
        Path2D meanLine = new Path2D.Double();
        meanLine.moveTo(mapX(minX), mapY(mean));
        meanLine.lineTo(mapX(maxX), mapY(mean));
        g2d.setPaint(Color.black);
        g2d.setStroke(new BasicStroke(1.0f));
        g2d.draw(meanLine);

        drawTicsYAxisInBackground(g2d);

        if (tripoliFraction != null) {
            if (tripoliFraction.isColorMeExcluded()) {
                paintFractionExcludedColor(g2d);
            }

            int chosenDatumIndex = tripoliFraction.getShowVerticalLineAtThisIndex();
            if (chosenDatumIndex > -1) {
                int secondChoiceIndex = tripoliFraction.getShowSecondVerticalLineAtThisIndex();
                highlightSelectedData(g2d, chosenDatumIndex, secondChoiceIndex);
            }

            if (!tripoliFraction.isStandard()) {
                setBackground(new Color(245, 251, 252));
            }
        }

        // draw data points
        for (int i = 0; i < myOnPeakData.length; i++) {
            Shape rawRatioPoint = new java.awt.geom.Ellipse2D.Double( //
                    mapX(myOnPeakNormalizedAquireTimes[i]), mapY(myOnPeakData[i]), 1.0, 1.0);
            g2d.setPaint(determineDataColor(i, Color.black));
            g2d.draw(rawRatioPoint);
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

        if (doReScale) {
            setDisplayOffsetY(0.0);
            setDisplayOffsetX(0.0);
        }

        // walk ratios and get min and max for axes
        myOnPeakNormalizedAquireTimes = rawRatioDataModel.getNormalizedOnPeakAquireTimes();
        myOnPeakData = ((RawRatioDataModel) rawRatioDataModel).getCorrectedRatios();
        if (myOnPeakData == null) {
            myOnPeakData = new double[myOnPeakNormalizedAquireTimes.length];
        }

        if (doReScale) {
            // X-axis lays out time evenly spaced
            minX = myOnPeakNormalizedAquireTimes[0];
            maxX = myOnPeakNormalizedAquireTimes[myOnPeakNormalizedAquireTimes.length - 1];
            // adjust margins for unknowns
            double xMarginStretch = TicGeneratorForAxes.generateMarginAdjustment(minX, maxX, 0.05);
            minX -= xMarginStretch;
            maxX += xMarginStretch;
            // Y-axis is ratios
            minY = Double.MAX_VALUE;
            maxY = -Double.MAX_VALUE;

            // find min and max y
            boolean[] myDataActiveMap = rawRatioDataModel.getDataActiveMap();
            boolean showAll = showIncludedDataPoints.equals(IncludedTypeEnum.ALL);
            // rework logic April 2016 
            for (int i = 0; i < myOnPeakData.length; i++) {
                if (showAll || myDataActiveMap[i]) {
                    minY = Math.min(minY, myOnPeakData[i]);
                    maxY = Math.max(maxY, myOnPeakData[i]);
                }
            }

            double yMarginStretch = TicGeneratorForAxes.generateMarginAdjustment(minY, maxY, 0.05);
            minY -= yMarginStretch;
            maxY += yMarginStretch;
        }
    }

    /**
     *
     * @return
     */
    @Override
    public DataModelInterface getDataModel() {
        return rawRatioDataModel;
    }
}
