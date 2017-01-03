/*
 * DataPresentationModeChooserPanel.java
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
package org.earthtime.Tripoli.dataViews.simpleViews;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Line2D;
import javax.swing.JLayeredPane;
import org.earthtime.Tripoli.beans.MaskingShade;
import org.earthtime.Tripoli.dataModels.DataModelInterface;
import org.earthtime.Tripoli.dataModels.MaskingSingleton;
import org.earthtime.Tripoli.dataViews.AbstractRawDataView;
import org.earthtime.Tripoli.dataViews.overlayViews.MaskingShadeTargetInterface;
import org.earthtime.utilities.TicGeneratorForAxes;

/**
 *
 * @author samuelbowring
 */
public class MaskingShadeControl extends AbstractRawDataView implements MaskingShadeTargetInterface {

    private int countOfMaskedTimeSlotsOnLeft;
    private int countOfMaskedTimeSlotsOnRight;
    private final MaskingSingleton maskingArray;
    public static final int MAX_SHADE_COUNT = 4;

    /**
     *
     * @param bounds
     * @param myOnPeakNormalizedAquireTimes
     * @param sampleSessionDataView
     */
    public MaskingShadeControl(Rectangle bounds, double[] myOnPeakNormalizedAquireTimes, JLayeredPane sampleSessionDataView) {
        super(bounds);

        this.myOnPeakNormalizedAquireTimes = myOnPeakNormalizedAquireTimes;
        this.sampleSessionDataView = sampleSessionDataView;

        maskingArray = MaskingSingleton.getInstance();

        setCursor(Cursor.getDefaultCursor());

        setOpaque(true);
    }

    @Override
    public void paint(Graphics2D g2d) {
        paintInit(g2d);

        g2d.setStroke(new BasicStroke(0.75f));
        g2d.drawLine(0, 0, getWidth() - 1, 0);

        g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

        g2d.setColor(Color.red);
        g2d.drawString("<-Pull Shades to hide data->", 16, 12);
        //draw zero
        g2d.setColor(Color.black);
        g2d.setStroke(new BasicStroke(0.5f));
        Shape zeroLine = new Line2D.Double(//
                mapX(myOnPeakNormalizedAquireTimes[0]) - 4.0f,//
                mapY(0), //
                mapX(myOnPeakNormalizedAquireTimes[myOnPeakNormalizedAquireTimes.length - 1]) + 4.0f,//
                mapY(0));
        g2d.draw(zeroLine);

        for (int i = 0; i < MAX_SHADE_COUNT; i++) {
            Shape ticLine = new Line2D.Double(//
                    mapX(myOnPeakNormalizedAquireTimes[i]),//
                    mapY(minY), //
                    mapX(myOnPeakNormalizedAquireTimes[i]),//
                    mapY(maxY / 3));
            g2d.draw(ticLine);
        }

        for (int i = myOnPeakNormalizedAquireTimes.length - MAX_SHADE_COUNT; i < myOnPeakNormalizedAquireTimes.length; i++) {
            Shape ticLine = new Line2D.Double(//
                    mapX(myOnPeakNormalizedAquireTimes[i]),//
                    mapY(minY), //
                    mapX(myOnPeakNormalizedAquireTimes[i]),//
                    mapY(maxY / 3));
            g2d.draw(ticLine);
        }
    }

    @Override
    public void preparePanel(boolean doReScale, boolean inLiveMode) {

        this.removeAll();

        setDisplayOffsetY(0.0);
        setDisplayOffsetX(0.0);

        // Y-axis is ratios
        minY = 0.0;
        maxY = this.getHeight();

        minX = myOnPeakNormalizedAquireTimes[0];
        maxX = myOnPeakNormalizedAquireTimes[myOnPeakNormalizedAquireTimes.length - 1];

        double xMarginStretch = TicGeneratorForAxes.generateMarginAdjustment(minX, maxX, 0.05);
        minX -= xMarginStretch;
        maxX += xMarginStretch;

        countOfMaskedTimeSlotsOnLeft = maskingArray.getLeftShadeCount();
//        countOfMaskedTimeSlotsOnLeft =    -1;
//        for (int i = 0; i < MAX_SHADE_COUNT; i ++) {
//            if (  ! maskingArray.getMaskingArray()[i] ) {
//                countOfMaskedTimeSlotsOnLeft ++;
//            }
//        }

        JLayeredPane myMaskingShadeLeft = new MaskingShade( //
                this, //
                true,//
                MaskingShade.PULL_FROM_LEFT,//
                countOfMaskedTimeSlotsOnLeft - 1);

        ((MaskingShade) myMaskingShadeLeft).setHEIGHT_OF_PULLTAB(getHeight());
        add(myMaskingShadeLeft, javax.swing.JLayeredPane.DEFAULT_LAYER);

        countOfMaskedTimeSlotsOnRight = -1;
        int lowestAquisitionIndex = //
                maskingArray.getMaskingArray().length - MAX_SHADE_COUNT;
        for (int i = lowestAquisitionIndex; i < maskingArray.getMaskingArray().length; i++) {
            if (!maskingArray.getMaskingArray()[i]) {
                countOfMaskedTimeSlotsOnRight++;
            }
        }
        JLayeredPane myMaskingShadeRight = new MaskingShade( //
                this, //
                true,//
                MaskingShade.PULL_FROM_RIGHT,//
                countOfMaskedTimeSlotsOnRight);

        ((MaskingShade) myMaskingShadeRight).setHEIGHT_OF_PULLTAB(getHeight());
        add(myMaskingShadeRight, javax.swing.JLayeredPane.DEFAULT_LAYER);

    }

    @Override
    public DataModelInterface getDataModel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int provideShadeXFromLeft(int currentShadeX) {

        int timeIndex = Math.min(MAX_SHADE_COUNT - 1, convertMouseXToValue(currentShadeX));

        System.out.println("timeIndex " + timeIndex);
        for (int i = 0; i < maskingArray.getMaskingArray().length - MAX_SHADE_COUNT; i++) {
            maskingArray.getMaskingArray()[i] = true;
        }
        for (int i = 0; i < timeIndex + 1; i++) {
            maskingArray.getMaskingArray()[i] = false;
        }

        // april 2014
        if (timeIndex < 0) {
            maskingArray.setLeftShadeCount(-1);
        } else {
            maskingArray.setLeftShadeCount(timeIndex + 1);
        }

        // refresh all
        ((AbstractRawDataView) sampleSessionDataView).refreshPanel(true, false);

        return (int) mapX(timeIndex);//currentShadeX;//timeIndex;
    }

    @Override
    public int provideShadeXFromRight(int currentShadeX) {

        int timeIndex = Math.max(maskingArray.getMaskingArray().length - MAX_SHADE_COUNT, convertMouseXToValue(getWidth() - currentShadeX) + 1);

        // this will repair any damage
        for (int i = MAX_SHADE_COUNT; i < maskingArray.getMaskingArray().length; i++) {
            maskingArray.getMaskingArray()[i] = true;
        }
        for (int i = timeIndex; i < maskingArray.getMaskingArray().length; i++) {
            maskingArray.getMaskingArray()[i] = false;
        }

        // april 2014
        maskingArray.setRightShadeCount(maskingArray.getMaskingArray().length - timeIndex - 1);

        // refresh all
        ((AbstractRawDataView) sampleSessionDataView).refreshPanel(true, false);

        return (int) mapX(timeIndex - 1);//currentShadeX;//timeIndex;
    }
}
