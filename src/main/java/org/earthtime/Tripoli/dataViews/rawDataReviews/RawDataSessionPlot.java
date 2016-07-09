/*
 * Copyright 2006-2016 CIRDLES.org.
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
package org.earthtime.Tripoli.dataViews.rawDataReviews;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.earthtime.Tripoli.dataModels.DataModelInterface;
import org.earthtime.Tripoli.dataModels.RawIntensityDataModel;
import org.earthtime.Tripoli.dataViews.AbstractRawDataView;
import org.earthtime.utilities.TicGeneratorForAxes;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public class RawDataSessionPlot extends AbstractRawDataView {

    private List<Integer> sessionTimeZeroIndices;
    private boolean overlayMode;
    private int peakLeftShade;
    private int peakWidth;
    private int backgroundRightShade;
    private int backgroundWidth;
    private int timeZeroRelativeIndex;

    /**
     *
     * @param rawIsotopeDataModel the value of rawIsotopeDataModel
     * @param bounds the value of bounds
     * @param overlayMode the value of overlayMode
     */
    public RawDataSessionPlot(DataModelInterface rawIsotopeDataModel, Rectangle bounds, boolean overlayMode) {
        super(bounds);
        this.rawRatioDataModel = rawIsotopeDataModel;
        this.overlayMode = overlayMode;

        initSession();
    }

    private void initSession() {
        setBackground(new Color(255, 255, 255));
        setOpaque(true);
        setPreferredSize(getSize());
    }

    /**
     *
     * @param g2d
     */
    @Override
    public void paint(Graphics2D g2d) {
        super.paint(g2d);

        if (overlayMode) {
            // on peak 
            g2d.setPaint(new Color(241, 255, 240)); //pale green

            g2d.fill(new Rectangle2D.Double(//
                    mapX(myOnPeakNormalizedAquireTimes[timeZeroRelativeIndex + peakLeftShade]), //
                    mapY(maxY),//
                    mapX(peakWidth),//
                    Math.abs(mapY(maxY) - mapY(minY))));
            g2d.setPaint(Color.black);

            g2d.setStroke(new BasicStroke(0.75f));

            g2d.draw(new Line2D.Double(//
                    mapX(myOnPeakNormalizedAquireTimes[timeZeroRelativeIndex + peakLeftShade]), //
                    mapY(minY),//
                    mapX(myOnPeakNormalizedAquireTimes[timeZeroRelativeIndex + peakLeftShade]),//
                    mapY(maxY)));

            g2d.draw(new Line2D.Double(//
                    mapX(myOnPeakNormalizedAquireTimes[timeZeroRelativeIndex + peakLeftShade + peakWidth]), //
                    mapY(minY),//
                    mapX(myOnPeakNormalizedAquireTimes[timeZeroRelativeIndex + peakLeftShade + peakWidth]),//
                    mapY(maxY)));

            // on background 
            g2d.setPaint(new Color(253, 253, 233)); //pale yellow

            g2d.fill(new Rectangle2D.Double(//
                    mapX(myOnPeakNormalizedAquireTimes[timeZeroRelativeIndex - backgroundRightShade - backgroundWidth]), //
                    mapY(maxY),//
                    mapX(backgroundWidth),//
                    Math.abs(mapY(maxY) - mapY(minY))));
            g2d.setPaint(Color.black);

            g2d.setStroke(new BasicStroke(0.75f));

            g2d.draw(new Line2D.Double(//
                    mapX(myOnPeakNormalizedAquireTimes[timeZeroRelativeIndex - backgroundRightShade - backgroundWidth]), //
                    mapY(minY),//
                    mapX(myOnPeakNormalizedAquireTimes[timeZeroRelativeIndex - backgroundRightShade - backgroundWidth]),//
                    mapY(maxY)));

            g2d.draw(new Line2D.Double(//
                    mapX(myOnPeakNormalizedAquireTimes[timeZeroRelativeIndex - backgroundRightShade]), //
                    mapY(minY),//
                    mapX(myOnPeakNormalizedAquireTimes[timeZeroRelativeIndex - backgroundRightShade]),//
                    mapY(maxY)));

            // calculate good right to graph
            DescriptiveStatistics timeZeroDeltas = new DescriptiveStatistics();
            for (int i = 0; i < sessionTimeZeroIndices.size() - 1; i++) {
                timeZeroDeltas.addValue(Math.abs(sessionTimeZeroIndices.get(i + 1) - sessionTimeZeroIndices.get(i)));
            }
            int timeToNextTimeZero = (int) timeZeroDeltas.getMax();

            for (int i = 0; i < sessionTimeZeroIndices.size(); i++) {
                int timeZeroStartIndex = sessionTimeZeroIndices.get(i);

//                int timeToNextTimeZero = 300;
//                if (i < sessionTimeZeroIndices.size() - 1) {
//                    timeToNextTimeZero = sessionTimeZeroIndices.get(i + 1) - timeZeroStartIndex;
//                } else {
//                    timeToNextTimeZero = timeZeroStartIndex - sessionTimeZeroIndices.get(i - 1);
//                }
                // mark time zero
                g2d.setPaint(Color.red);
                g2d.setStroke(new BasicStroke(1.0f));
                g2d.draw(new Line2D.Double(//
                        mapX(timeZeroRelativeIndex), //
                        mapY(minY),//
                        mapX(timeZeroRelativeIndex),//
                        mapY(maxY)));
                g2d.setPaint(Color.black);

                Shape fractionPlot = new Path2D.Double();
                ((Path2D) fractionPlot).moveTo(//
                        mapX(0), mapY(myOnPeakData[timeZeroStartIndex - timeZeroRelativeIndex]));
                int t = 1;
                for (int j = timeZeroStartIndex - timeZeroRelativeIndex - 1; j < timeZeroStartIndex + timeToNextTimeZero - timeZeroRelativeIndex; j++) {
                    ((Path2D) fractionPlot).lineTo(//
                            mapX(t), mapY(myOnPeakData[j]));
                    t++;

                }
                g2d.setPaint(Color.black);
                g2d.setStroke(new BasicStroke(0.75f));
                g2d.draw(fractionPlot);

            }

        } else { //serial mode            
            for (int i = 0; i < sessionTimeZeroIndices.size(); i++) {
                // mark onpeak
                g2d.setPaint(new Color(241, 255, 240)); //pale green
                int peakStartIndex = sessionTimeZeroIndices.get(i) + peakLeftShade;
                g2d.fill(new Rectangle2D.Double(//
                        mapX(myOnPeakNormalizedAquireTimes[peakStartIndex]), //
                        mapY(maxY),//
                        mapX(peakWidth),//
                        Math.abs(mapY(maxY) - mapY(minY))));

                g2d.setPaint(Color.black);
                g2d.setStroke(new BasicStroke(0.75f));

                g2d.draw(new Line2D.Double(//
                        mapX(myOnPeakNormalizedAquireTimes[peakStartIndex]), //
                        mapY(minY),//
                        mapX(myOnPeakNormalizedAquireTimes[peakStartIndex]),//
                        mapY(maxY)));

                g2d.draw(new Line2D.Double(//
                        mapX(myOnPeakNormalizedAquireTimes[peakStartIndex + peakWidth]), //
                        mapY(minY),//
                        mapX(myOnPeakNormalizedAquireTimes[peakStartIndex + peakWidth]),//
                        mapY(maxY)));

                // mark background
                g2d.setPaint(new Color(253, 253, 233)); //pale yellow
                int backgroundStartIndex = sessionTimeZeroIndices.get(i) - backgroundRightShade - backgroundWidth;
                g2d.fill(new Rectangle2D.Double(//
                        mapX(myOnPeakNormalizedAquireTimes[backgroundStartIndex]), //
                        mapY(maxY),//
                        mapX(backgroundWidth),//
                        Math.abs(mapY(maxY) - mapY(minY))));

                g2d.setPaint(Color.black);
                g2d.setStroke(new BasicStroke(0.75f));

                g2d.draw(new Line2D.Double(//
                        mapX(myOnPeakNormalizedAquireTimes[backgroundStartIndex]), //
                        mapY(minY),//
                        mapX(myOnPeakNormalizedAquireTimes[backgroundStartIndex]),//
                        mapY(maxY)));

                g2d.draw(new Line2D.Double(//
                        mapX(myOnPeakNormalizedAquireTimes[backgroundStartIndex + backgroundWidth]), //
                        mapY(minY),//
                        mapX(myOnPeakNormalizedAquireTimes[backgroundStartIndex + backgroundWidth]),//
                        mapY(maxY)));

                // mark time zero
                g2d.setPaint(Color.red);
                int timeZeroIndex = sessionTimeZeroIndices.get(i);
                g2d.draw(new Line2D.Double(//
                        mapX(myOnPeakNormalizedAquireTimes[timeZeroIndex]), //
                        mapY(minY),//
                        mapX(myOnPeakNormalizedAquireTimes[timeZeroIndex]),//
                        mapY(maxY)));
                g2d.setPaint(Color.black);
            }

            // draw data over selection zone
            Shape connectingLine = new Path2D.Double();
            ((Path2D) connectingLine).moveTo(//
                    mapX(myOnPeakNormalizedAquireTimes[0]), mapY(myOnPeakData[0]));
            for (int i = 1; i < myOnPeakData.length; i++) {
                ((Path2D) connectingLine).lineTo(//
                        mapX(myOnPeakNormalizedAquireTimes[i]), mapY(myOnPeakData[i]));
            }

            g2d.draw(connectingLine);

        }

        g2d.drawString(rawRatioDataModel.getDataModelName(), 25, 15);

    }

    /**
     *
     * @param doReScale the value of doReScale
     * @param inLiveMode the value of inLiveMode
     */
    @Override
    public void preparePanel(boolean doReScale, boolean inLiveMode
    ) {

        this.removeAll();

        // walk intensities and get min and max for axes
        myOnPeakData = ((RawIntensityDataModel) rawRatioDataModel).getOnPeakVirtualCollector().getIntensities();//        getOnPeakCountsPerSecondAsRawIntensities();

        // normalize aquireTimes
        myOnPeakNormalizedAquireTimes = rawRatioDataModel.getNormalizedOnPeakAquireTimes();
        boolean[] myDataActiveMap = rawRatioDataModel.getDataActiveMap();

        // get TRA estimate start of time zero
        sessionTimeZeroIndices = ((RawIntensityDataModel) rawRatioDataModel).getSessionTimeZeroIndices();
        peakLeftShade = ((RawIntensityDataModel) rawRatioDataModel).getPeakLeftShade();
        peakWidth = ((RawIntensityDataModel) rawRatioDataModel).getPeakWidth();
        backgroundRightShade = ((RawIntensityDataModel) rawRatioDataModel).getBackgroundRightShade();
        backgroundWidth = ((RawIntensityDataModel) rawRatioDataModel).getBackgroundWidth();
        timeZeroRelativeIndex = ((RawIntensityDataModel) rawRatioDataModel).getTimeZeroRelativeIndex();

        if (doReScale) {
            setDisplayOffsetY(0.0);

            setDisplayOffsetX(0.0);

            // X-axis lays out time evenly spaced
            minX = myOnPeakNormalizedAquireTimes[0];
            maxX = myOnPeakNormalizedAquireTimes[myOnPeakNormalizedAquireTimes.length - 1];
            if (overlayMode) {
                maxX /= sessionTimeZeroIndices.size();
            }

//            double xMarginStretch = TicGeneratorForAxes.generateMarginAdjustment(minX, maxX, 0.05);
//            minX -= xMarginStretch;
//            maxX += xMarginStretch;
            // Y-axis is intensities as voltages plus or minus
            // find min and max y
            minY = Double.MAX_VALUE;
            maxY = -Double.MAX_VALUE;

            // on peak
            for (int i = 0; i < myOnPeakData.length; i++) {
                if (myDataActiveMap[i]) {
                    if (Double.isFinite(myOnPeakData[i])) {
                        minY = Math.min(minY, myOnPeakData[i]);
                        maxY = Math.max(maxY, myOnPeakData[i]);
                    }
                }
            }

            // adjust margins for unknowns
            double yMarginStretch = TicGeneratorForAxes.generateMarginAdjustment(minY, maxY, 0.05);
            minY -= yMarginStretch;
            maxY += yMarginStretch;
        }
    }

    @Override
    public DataModelInterface getDataModel() {
        return rawRatioDataModel;
    }
}
