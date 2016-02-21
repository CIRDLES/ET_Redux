/*
 * RawCountsDataViewForShrimp.java
 *
 * Copyright 2006-2016 James F. Bowring and www.Earth-Time.org
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
import org.earthtime.Tripoli.dataModels.RawIntensityDataModel;
import org.earthtime.Tripoli.dataViews.AbstractRawDataView;
import org.earthtime.Tripoli.fractions.TripoliFraction;
import org.earthtime.utilities.TicGeneratorForAxes;

/**
 *
 * @author James F. Bowring
 */
public class RawCountsDataViewForShrimp extends AbstractRawDataView {

    /**
     *
     */
    public static int DEFAULT_WIDTH_OF_PANE = 128;
    private double[] myOnPeakTwoSigmas;

    /**
     *
     * @param sampleSessionDataView
     * @param tripoliFraction
     * @param rawIsotopeDataModel
     * @param bounds
     * @param invokeMouseListener
     */
    public RawCountsDataViewForShrimp(//
            JLayeredPane sampleSessionDataView,//
            TripoliFraction tripoliFraction,//
            DataModelInterface rawIsotopeDataModel,//
            Rectangle bounds,//
            boolean invokeMouseListener) {
        super(sampleSessionDataView, tripoliFraction, bounds, invokeMouseListener, true);

        this.rawRatioDataModel = rawIsotopeDataModel;
    }

    /**
     *
     * @param g2d
     */
    @Override
    public void paint(Graphics2D g2d) {
        super.paint(g2d);

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
            Shape plusMinusTwoSigma = new Line2D.Double(//
                    mapX(myOnPeakNormalizedAquireTimes[i]),// 
                    mapY(myOnPeakData[i] - myOnPeakTwoSigmas[i]),//
                    mapX(myOnPeakNormalizedAquireTimes[i]),// 
                    mapY(myOnPeakData[i] + myOnPeakTwoSigmas[i]));
            g2d.setStroke(new BasicStroke(1.0f));
            g2d.draw(plusMinusTwoSigma);

            // tips of uncertainty
            Shape plusTwoSigmaTip = new Line2D.Double(//
                    mapX(myOnPeakNormalizedAquireTimes[i]) - 1,// 
                    mapY(myOnPeakData[i] + myOnPeakTwoSigmas[i]),//
                    mapX(myOnPeakNormalizedAquireTimes[i]) + 1,// 
                    mapY(myOnPeakData[i] + myOnPeakTwoSigmas[i]));

            Shape minusTwoSigmaTip = new Line2D.Double(//
                    mapX(myOnPeakNormalizedAquireTimes[i]) - 1,// 
                    mapY(myOnPeakData[i] - myOnPeakTwoSigmas[i]),//
                    mapX(myOnPeakNormalizedAquireTimes[i]) + 1,// 
                    mapY(myOnPeakData[i] - myOnPeakTwoSigmas[i]));

            g2d.setStroke(new BasicStroke(1.0f));
            g2d.draw(plusTwoSigmaTip);
            g2d.draw(minusTwoSigmaTip);

        }
    }

    /**
     *
     */
    @Override
    public void preparePanel() {

        this.removeAll();

        setDisplayOffsetY(0.0);
        setDisplayOffsetX(0.0);

        myOnPeakData = ((RawIntensityDataModel) rawRatioDataModel).getOnPeakCountsPerSecondAsRawIntensities();//   .getOnPeakVirtualCollector().getIntensities();
        double[] myOnPeakVariances = ((RawIntensityDataModel) rawRatioDataModel).getDiagonalOfMatrixSIntensities();
        myOnPeakTwoSigmas = new double[myOnPeakVariances.length];
        for (int i = 0; i < myOnPeakVariances.length; i++) {
            myOnPeakTwoSigmas[i] = 2.0 * Math.sqrt(myOnPeakVariances[i]);
        }
        myOnPeakNormalizedAquireTimes = rawRatioDataModel.getNormalizedOnPeakAquireTimes();

        // X-axis lays out time evenly spaced
        minX = myOnPeakNormalizedAquireTimes[0];
        maxX = myOnPeakNormalizedAquireTimes[myOnPeakNormalizedAquireTimes.length - 1] + 1;// say 0...14 and 15...29

        // Y-axis is intensities as voltages plus or minus
        minY = Double.MAX_VALUE;
        maxY = -Double.MAX_VALUE;

        // on peak
        for (int i = 0; i < myOnPeakData.length; i++) {
            minY = Math.min(minY, myOnPeakData[i] - myOnPeakTwoSigmas[i]);
            maxY = Math.max(maxY, myOnPeakData[i] + myOnPeakTwoSigmas[i]);
        }

        // adjust margins for unknowns
        if (!tripoliFraction.isStandard()) {
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

    @Override
    public void mouseDragged(MouseEvent evt) {
        // prevent point rejection
    }

    @Override
    public void mousePressed(MouseEvent evt) {
        // prevent point rejection
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // prevent point rejection
    }

}
