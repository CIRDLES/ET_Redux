/*
 * RawRatioDataView.java
 *
 * Created Jul 6, 2011
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
package org.earthtime.Tripoli.dataViews.simpleViews.usedByReflection;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
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
public class RawRatioDataViewForShrimp extends AbstractRawDataView {

    /**
     *
     */
    public static int DEFAULT_WIDTH_OF_PANE = 128;
    private double[] myOnPeakOneSigmas;

    /**
     *
     * @param sampleSessionDataView
     * @param tripoliFraction
     * @param rawRatioDataModel
     * @param bounds
     * @param invokeMouseListener
     */
    public RawRatioDataViewForShrimp(//
            JLayeredPane sampleSessionDataView,//
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
        super.paint(g2d);

//        if (isNotShownDueToBelowDetectionFlag()) {
//            setBackground(ReduxConstants.palePinkBelowDetection);
//            g2d.drawString("BELOW DETECTION", 25, 25);
//        }
//
//        if (!isNotShownDueToBelowDetectionFlag()) {
//            for (int i = 0; i < myOnPeakData.length; i++) {
//                Shape rawRatioPoint = new java.awt.geom.Ellipse2D.Double( //
//                        mapX(myOnPeakNormalizedAquireTimes[i]), mapY(myOnPeakData[i]), 1, 1);
//                g2d.setPaint(determineDataColor(i, Color.black));
//
//                g2d.draw(rawRatioPoint);
//            }
//        }
        Path2D pointTrace = new Path2D.Double(Path2D.WIND_NON_ZERO);
        pointTrace.moveTo(mapX(myOnPeakNormalizedAquireTimes[0]), mapY(myOnPeakData[0]));
        for (int i = 0; i < myOnPeakData.length; i++) {
            // line tracing through points
            pointTrace.lineTo(mapX(myOnPeakNormalizedAquireTimes[i]), mapY(myOnPeakData[i]));
            g2d.setStroke(new BasicStroke(0.5f));
            g2d.setPaint(determineDataColor(i, Color.GRAY));
            g2d.draw(pointTrace);

            Shape intensity = new java.awt.geom.Ellipse2D.Double( //
                    mapX(myOnPeakNormalizedAquireTimes[i]) - 1, mapY(myOnPeakData[i]) - 1, 2, 2);
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.setPaint(determineDataColor(i, Color.black));
            g2d.draw(intensity);

            // uncertainty
            Shape plusMinusOneSigma = new Line2D.Double(//
                    mapX(myOnPeakNormalizedAquireTimes[i]),// 
                    mapY(myOnPeakData[i] - myOnPeakOneSigmas[i]),//
                    mapX(myOnPeakNormalizedAquireTimes[i]),// 
                    mapY(myOnPeakData[i] + myOnPeakOneSigmas[i]));
            g2d.setStroke(new BasicStroke(1.0f));
            g2d.draw(plusMinusOneSigma);

            // tips of uncertainty
            Shape plusOneSigmaTip = new Line2D.Double(//
                    mapX(myOnPeakNormalizedAquireTimes[i]) - 1,// 
                    mapY(myOnPeakData[i] + myOnPeakOneSigmas[i]),//
                    mapX(myOnPeakNormalizedAquireTimes[i]) + 1,// 
                    mapY(myOnPeakData[i] + myOnPeakOneSigmas[i]));

            Shape minusOneSigmaTip = new Line2D.Double(//
                    mapX(myOnPeakNormalizedAquireTimes[i]) - 1,// 
                    mapY(myOnPeakData[i] - myOnPeakOneSigmas[i]),//
                    mapX(myOnPeakNormalizedAquireTimes[i]) + 1,// 
                    mapY(myOnPeakData[i] - myOnPeakOneSigmas[i]));

            g2d.setStroke(new BasicStroke(1.0f));
            g2d.draw(plusOneSigmaTip);
            g2d.draw(minusOneSigmaTip);

        }
    }

    /**
     *
     * @param doReScale the value of doReScale
     */
    @Override
    public void preparePanel(boolean doReScale) {

        this.removeAll();

        // normalize aquireTimes
        myOnPeakNormalizedAquireTimes = rawRatioDataModel.getNormalizedOnPeakAquireTimes();

        if (doReScale) {
            setDisplayOffsetY(0.0);

            setDisplayOffsetX(0.0);

            // X-axis lays out time evenly spaced
            minX = myOnPeakNormalizedAquireTimes[0];
            maxX = myOnPeakNormalizedAquireTimes[myOnPeakNormalizedAquireTimes.length - 1];
            double xMarginStretch = TicGeneratorForAxes.generateMarginAdjustment(minX, maxX, 0.05);
            minX -= xMarginStretch;
            maxX += xMarginStretch;
        }

        notShownDueToBelowDetectionFlag = rawRatioDataModel.isBelowDetection();

        if (!notShownDueToBelowDetectionFlag) {
            // walk ratios and get min and max for axes
            myOnPeakData = ((RawRatioDataModel) rawRatioDataModel).getRatios().clone(); //.getLogRatios().clone();//            
//            for (int i = 0; i < myOnPeakData.length; i++) {
//                double convertedOnPeak = convertLogDatumToPresentationMode(myOnPeakData[i]);
//                myOnPeakData[i] = convertedOnPeak;
//            }
            myOnPeakOneSigmas = ((RawRatioDataModel) rawRatioDataModel).getUncertaintyOneSigmaAbsRatios().clone();

            if (doReScale) {
                // Y-axis is ratios
                minY = Double.MAX_VALUE;
                maxY = -Double.MAX_VALUE;

                // find min and max y
                boolean[] myDataActiveMap = rawRatioDataModel.getDataActiveMap();

                boolean showAll = showIncludedDataPoints.equals(IncludedTypeEnum.ALL);
                // rework logic April 2016 
                for (int i = 0; i < myOnPeakData.length; i++) {
                    if ((Double.isFinite(myOnPeakData[i])) && (showAll || myDataActiveMap[i])) {
                        minY = Math.min(minY, myOnPeakData[i]);
                        maxY = Math.max(maxY, myOnPeakData[i]);
                    }
                }

                // adjust margins for unknowns
                double yMarginStretch = TicGeneratorForAxes.generateMarginAdjustment(minY, maxY, 0.05);
                minY -= yMarginStretch;
                maxY += yMarginStretch;
            }

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

    @Override
    public void mouseDragged(MouseEvent evt) {
        //super.mouseDragged(evt); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        //super.mouseReleased(e); //To change body of generated methods, choose Tools | Templates.
    }

}
