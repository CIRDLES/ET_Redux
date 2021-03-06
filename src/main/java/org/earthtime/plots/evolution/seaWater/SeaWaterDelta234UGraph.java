/*
 * Copyright 2019 CIRDLES.
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
package org.earthtime.plots.evolution.seaWater;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.TextLayout;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.earthtime.plots.AbstractDataView;
import org.earthtime.utilities.TicGeneratorForAxes;

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class SeaWaterDelta234UGraph extends AbstractDataView {

    private static SeaWaterInitialDelta234UTableModel model;

    public SeaWaterDelta234UGraph(SeaWaterInitialDelta234UTableModel model) {

        super();

        this.model = model;

        this.leftMargin = 50;
        this.topMargin = 30;
        this.graphWidth = 900;
        this.graphHeight = 230;
        this.xLocation = 0;

        this.showMe = true;

        initGraph();

    }

    private void initGraph() {
        setBounds(xLocation, 0, graphWidth + leftMargin * 2, graphHeight + topMargin * 2);

        setOpaque(true);

        setBackground(Color.white);
    }

    @Override
    public void refreshPanel(boolean doReset) {
        preparePanel(doReset);
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        paint((Graphics2D) g, false);
    }

    public void paint(Graphics2D g2d, boolean svgStyle) {

        RenderingHints rh = g2d.getRenderingHints();
        rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHints(rh);

        g2d.setPaint(Color.BLACK);
        g2d.setStroke(new BasicStroke(1.0f));
        g2d.setFont(new Font(
                "SansSerif",
                Font.BOLD,
                10));

        // draw graph border
        g2d.setPaint(Color.black);
        g2d.drawRect(leftMargin, topMargin, (int) graphWidth - 1, (int) graphHeight - 1);

        // spring green
        g2d.setPaint(new Color(0, 255, 127));
        for (int i = 0; i < myOnPeakData.length; i++) {
            g2d.setPaint(new Color(0, 255, 127));
            Shape rawRatioPoint = new java.awt.geom.Ellipse2D.Double( //
                    mapX(myOnPeakNormalizedAquireTimes[i]) - 3, mapY(myOnPeakData[i]) - 3, 6, 6);

            g2d.draw(rawRatioPoint);
            g2d.fill(rawRatioPoint);

            if (i > 0) {
                // draw line
                g2d.setPaint(new Color(0, 255, 127));
                g2d.setStroke(new BasicStroke(1.0f));
                Line2D line = new Line2D.Double(
                        mapX(myOnPeakNormalizedAquireTimes[i - 1]),
                        mapY(myOnPeakData[i - 1]),
                        mapX(myOnPeakNormalizedAquireTimes[i]),
                        mapY(myOnPeakData[i]));
                g2d.draw(line);

                g2d.setPaint(Color.black);
                g2d.setStroke(new BasicStroke(0.5f));
                Line2D upperEnv = new Line2D.Double(
                        mapX(myOnPeakNormalizedAquireTimes[i - 1]),
                        mapY(myOnPeakDataUpperUnct[i - 1]),
                        mapX(myOnPeakNormalizedAquireTimes[i]),
                        mapY(myOnPeakDataUpperUnct[i]));
                g2d.draw(upperEnv);

                Line2D lowerEnv = new Line2D.Double(
                        mapX(myOnPeakNormalizedAquireTimes[i - 1]),
                        mapY(myOnPeakDataLowerUnct[i - 1]),
                        mapX(myOnPeakNormalizedAquireTimes[i]),
                        mapY(myOnPeakDataLowerUnct[i]));
                g2d.draw(lowerEnv);
            }
        }

        g2d.setPaint(Color.black);
        for (int i = 0; i < myOnPeakNormalizedAquireTimes.length; i++) {
            try {
                Shape ticMark = new Line2D.Double(
                        mapX(myOnPeakNormalizedAquireTimes[i]),
                        mapY(minY) - 2,
                        mapX(myOnPeakNormalizedAquireTimes[i]),
                        mapY(minY) + 4);
                g2d.draw(ticMark);

                TextLayout mLayout
                        = new TextLayout(
                                String.valueOf(myOnPeakNormalizedAquireTimes[i]), g2d.getFont(), g2d.getFontRenderContext());

                Rectangle2D bounds = mLayout.getBounds();

                g2d.drawString(String.valueOf(myOnPeakNormalizedAquireTimes[i]),
                        (float) mapX(myOnPeakNormalizedAquireTimes[i]) - (float) (bounds.getWidth() / 2.0f),
                        (float) mapY(minY) + 15);
            } catch (Exception e) {
            }
        }

        Set<Double> yTics = new HashSet<>();
        yTics.add(minY);
        yTics.add(maxY);
        for (int i = 0; i < myOnPeakData.length; i++) {
            yTics.add(myOnPeakData[i]);
//            yTics.add(myOnPeakDataUpperUnct[i]);
//            yTics.add(myOnPeakDataLowerUnct[i]);
        }

        for (Double tic : yTics) {
            try {
                Shape ticMark = new Line2D.Double(
                        mapX(minX) - 4,
                        mapY(tic),
                        mapX(minX) + 2,
                        mapY(tic));
                g2d.draw(ticMark);

                TextLayout mLayout
                        = new TextLayout(
                                String.valueOf(tic), g2d.getFont(), g2d.getFontRenderContext());

                Rectangle2D bounds = mLayout.getBounds();

                g2d.drawString(String.valueOf(tic),
                        (float) mapX(minX) - (float) bounds.getWidth() - 5,
                        (float) mapY(tic) + (float) (bounds.getHeight() / 2.0f));
            } catch (Exception e) {
            }
        }

    }

    @Override
    public void preparePanel(boolean doReset) {
        List<SeaWaterDelta234UModelEntry> entryListOrig = model.getEntryList();

        // remove -1 entries
        List<SeaWaterDelta234UModelEntry> entryList = new ArrayList<>();
        for (SeaWaterDelta234UModelEntry swe : entryListOrig) {
            if (swe.getAgeInKa() >= 0) {
                entryList.add(swe);
            }
        }

        myOnPeakNormalizedAquireTimes = new double[entryList.size()];
        myOnPeakData = new double[entryList.size()];
        myOnPeakDataUpperUnct = new double[entryList.size()];
        myOnPeakDataLowerUnct = new double[entryList.size()];

        for (int i = 0; i < myOnPeakNormalizedAquireTimes.length; i++) {
            myOnPeakNormalizedAquireTimes[i] = entryList.get(i).ageInKa;
            myOnPeakData[i] = entryList.get(i).delta234UPerMil;
            myOnPeakDataUpperUnct[i] = entryList.get(i).delta234UPerMil + entryList.get(i).oneSigmaAbsUnct;
            myOnPeakDataLowerUnct[i] = entryList.get(i).delta234UPerMil - entryList.get(i).oneSigmaAbsUnct;
        }

        setDisplayOffsetY(0.0);

        setDisplayOffsetX(0.0);

        // X-axis lays out time evenly spaced
        minX = myOnPeakNormalizedAquireTimes[0];
        maxX = myOnPeakNormalizedAquireTimes[myOnPeakNormalizedAquireTimes.length - 1];
        double xMarginStretch = TicGeneratorForAxes.generateMarginAdjustment(minX, maxX, 0.05);
        minX -= xMarginStretch;
        maxX += xMarginStretch;

        // Y-axis is ratios
        minY = Double.MAX_VALUE;
        maxY = -Double.MAX_VALUE;

        // find min and max y
        for (int i = 0; i < myOnPeakData.length; i++) {
            if ((Double.isFinite(myOnPeakData[i]))) {
                minY = Math.min(minY, myOnPeakDataLowerUnct[i]);
                maxY = Math.max(maxY, myOnPeakDataUpperUnct[i]);
            }
        }

        // adjust margins for unknowns
        minY -= 2;
        maxY += 2;
//        double yMarginStretch = TicGeneratorForAxes.generateMarginAdjustment(minY, maxY, 0.05);
//        minY -= yMarginStretch;
//        maxY += yMarginStretch;
    }

    /**
     * @param aModel the model to set
     */
    public static void setModel(SeaWaterInitialDelta234UTableModel aModel) {
        model = aModel;
    }

}
