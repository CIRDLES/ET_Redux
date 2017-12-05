/*
 * KwikiDateDisplay.java
 *
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
package org.earthtime.UPb_Redux.dateInterpretation.kwiki;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import javax.swing.JPanel;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public class KwikiDateSensitivityPanel extends JPanel {

    private int topMargin;
    private int leftMargin;
    private double minY;
    private double maxY;
    private double minX;
    private double maxX;
    private ValueModel[] changedDates;
    private ValueModel[] originalDates;
    private boolean[] showDates;

    /** Creates a new instance of KwikiDateDisplay
     * @param x 
     * @param y 
     */
    public KwikiDateSensitivityPanel(int x, int y) {
        super();

        setOpaque(true);

        setBackground(Color.white);

        setBounds(x, y, 900, 80);

        topMargin = 1;
        leftMargin = 0;
        minY = 0.0;
        maxY = 0.0;
        minX = 0.0;
        maxX = 0.0;
        changedDates = new ValueModel[0];
        originalDates = new ValueModel[0];
        showDates = new boolean[0];

    }

    /**
     * 
     * @param g
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        paint((Graphics2D) g);
    }

    /**
     * 
     * @param g2d
     */
    public void paint(Graphics2D g2d) {

        RenderingHints rh = g2d.getRenderingHints();
        rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHints(rh);
        g2d.setColor(Color.BLACK);

        double rangeX = (getMaxX() - getMinX());
        double rangeY = (getMaxY() - getMinY());

        DrawBounds(g2d);
        DrawAxesAndTicks(g2d, rangeX, rangeY);
        DrawDates(g2d, rangeX, rangeY);

    }

    private double MapX(double x, double minX, double rangeX, int widthG) {

        return (((x - minX) / rangeX) * (double) widthG - leftMargin) + (double) getLeftMargin();
    }

    private double MapY(double y, double maxY, double rangeY, int heightG) {

        return (((maxY - y) / rangeY) * (double) (heightG)) + (double) getTopMargin();
    }

    private void DrawBounds(Graphics2D g2d) {

        g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

    }

    private void DrawAxesAndTicks(Graphics2D g2d, double rangeX, double rangeY) {

        // reset the clip bounds to paint axis and numbers
        g2d.setClip(0, 0, getWidth(), getHeight());

        g2d.setFont(new Font("Monospaced", Font.BOLD, 10));
        g2d.setPaint(Color.BLACK);
        g2d.setStroke(new BasicStroke(1.0f));

        // determine the axis ticks
        double minXtick = Math.ceil(getMinX() * 100) / 100;
        double maxXtick = Math.floor(getMaxX() * 100) / 100;

        int count = 0;
        double deltax = Math.rint((maxXtick - minXtick) * 10 + 0.5);
        double stepXtick = deltax / 100;

        for (double x = minXtick; x <
                maxXtick; x +=
                        stepXtick) {
            Line2D line = new Line2D.Double(
                    MapX(x, getMinX(), rangeX, getWidth()),
                    MapY(getMinY(), getMaxY(), rangeY, getHeight()),
                    MapX(x, getMinX(), rangeX, getWidth()),
                    MapY(getMaxY(), getMaxY(), rangeY, getHeight()));
            g2d.setPaint(Color.GRAY);
            g2d.draw(line);

            NumberFormat xFormat = null;
            String temp = null;

            xFormat = new DecimalFormat("0.00");
            temp = xFormat.format(x);

            g2d.setPaint(Color.BLACK);
            g2d.drawString(
                    temp,
                    (float) MapX(x, getMinX(), rangeX, getWidth()) - 21f,
                    (float) MapY(getMinY(), getMaxY(), rangeY, getHeight()) - 4f);
            count++;
        }
    }

    /**
     * 
     * @param g2d
     * @param rangeX
     * @param rangeY
     */
    public void DrawDates(Graphics2D g2d, double rangeX, double rangeY) {
        Color lightRed = new Color(255, 225, 225);
        Color lightGreen = new Color(225, 255, 225);
        
        DrawDateBars(getOriginalDates(), g2d, rangeX, rangeY, 13, Color.red, lightRed);
        DrawDateBars(getChangedDates(), g2d, rangeX, rangeY, 23, Color.black, lightGreen);
    }

    private void DrawDateBars(
            ValueModel[] dates,
            Graphics2D g2d,
            double rangeX,
            double rangeY,
            int offset,
            Color borderColor,
            Color fillColor) {

        int barWidth = 10;


        for (int i = 0; i < dates.length; i++) {
            if (getShowDates()[i]) {
                int bottomBar = offset + 44 - i * 22;

                double lowerDate = //
                        dates[i].getValue().//
                        subtract(new BigDecimal(2.0).//
                        multiply(dates[i].getOneSigmaAbs())).//
                        movePointLeft(6).doubleValue();

                double upperDate = //
                        dates[i].getValue().//
                        add(new BigDecimal(2.0).//
                        multiply(dates[i].getOneSigmaAbs())).//
                        movePointLeft(6).doubleValue();

                double twoSigma = //
                        new BigDecimal(2.0).//
                        multiply(dates[i].getOneSigmaAbs()).//
                        movePointLeft(6).doubleValue();

                g2d.setStroke(new BasicStroke(1f));

                Path2D origDateLow = new Path2D.Double (Path2D.WIND_NON_ZERO);
                Path2D origDateHigh = new Path2D.Double (Path2D.WIND_NON_ZERO);

                // lower left
                origDateLow.moveTo(//
                        (float) MapX(lowerDate, getMinX(), rangeX, getWidth()),
                        (float) MapY(bottomBar, getMaxY(), rangeY, getHeight()));
                origDateLow.lineTo(//
                        (float) MapX(lowerDate + twoSigma, getMinX(), rangeX, getWidth()),
                        (float) MapY(bottomBar, getMaxY(), rangeY, getHeight()));
                origDateLow.lineTo(//
                        (float) MapX(lowerDate + twoSigma, getMinX(), rangeX, getWidth()),
                        (float) MapY(bottomBar + barWidth, getMaxY(), rangeY, getHeight()));
                origDateLow.lineTo(//
                        (float) MapX(lowerDate, getMinX(), rangeX, getWidth()),
                        (float) MapY(bottomBar + barWidth, getMaxY(), rangeY, getHeight()));
                origDateLow.closePath();
                g2d.setColor(fillColor);//lightRed);
                g2d.fill(origDateLow);

                // lower left
                origDateHigh.moveTo(//
                        (float) MapX(lowerDate + twoSigma, getMinX(), rangeX, getWidth()),
                        (float) MapY(bottomBar, getMaxY(), rangeY, getHeight()));
                origDateHigh.lineTo(//
                        (float) MapX(upperDate, getMinX(), rangeX, getWidth()),
                        (float) MapY(bottomBar, getMaxY(), rangeY, getHeight()));
                origDateHigh.lineTo(//
                        (float) MapX(upperDate, getMinX(), rangeX, getWidth()),
                        (float) MapY(bottomBar + barWidth, getMaxY(), rangeY, getHeight()));
                origDateHigh.lineTo(//
                        (float) MapX(lowerDate + twoSigma, getMinX(), rangeX, getWidth()),
                        (float) MapY(bottomBar + barWidth, getMaxY(), rangeY, getHeight()));
                origDateHigh.closePath();
                g2d.setColor(fillColor);//lightGreen);
                g2d.fill(origDateHigh);

                g2d.setColor(borderColor);
                g2d.draw(origDateLow);
                g2d.draw(origDateHigh);

                // draw date string
//                String dateString =//
//                        dates[i].formatValueAndTwoSigmaForPublicationSigDigMode(//
//                        dates[i].getUncertaintyType(),
//                        -6, 2);
//
//                g2d.setFont(new Font("Monospaced", Font.BOLD, 10));
//
//                g2d.drawString(
//                        dateString,
//                        (float) MapX(lowerDate + twoSigma, getMinX(), rangeX, getWidth()) - 20f,
//                        (float) MapY(bottomBar, getMaxY(), rangeY, getHeight()) - 1f);

            }
        }
    }

    /**
     * 
     */
    public void refreshPanel() {
        preparePanel();
        repaint();
    }

    /**
     * 
     */
    public void preparePanel() {

        this.removeAll();

        // walk ages for min and max using +/- 4 sigma

        // X-axis is ages
        setMinX(ReduxConstants.MAX_DATE_ANNUM);//4.5E9);
        setMaxX(0.0);

        for (int i = 0; i < getOriginalDates().length; i++) {
            if (getShowDates()[i]) {
                double dateMax = //
                        getOriginalDates()[i].getValue().//
                        add(new BigDecimal(4.0).//
                        multiply(getOriginalDates()[i].getOneSigmaAbs())).doubleValue();
                if (dateMax > getMaxX()) {
                    setMaxX(dateMax);
                }

                double dateMin = //
                        getOriginalDates()[i].getValue().//
                        subtract(new BigDecimal(4.0).//
                        multiply(getOriginalDates()[i].getOneSigmaAbs())).doubleValue();
                if (dateMin < getMinX()) {
                    setMinX(dateMin);

                }
            }
        }

        // convert to Ma
        setMinX(getMinX() / 1.0E6);
        setMaxX(getMaxX() / 1.0E6);
        // Y-axis is merely for layout, so map it to the size of panel
        setMinY(0.0);
        setMaxY(getHeight());

    }

    /**
     * @return the minY
     */
    public double getMinY() {
        return minY;
    }

    /**
     * @param minY the minY to set
     */
    public void setMinY(double minY) {
        this.minY = minY;
    }

    /**
     * @return the maxY
     */
    public double getMaxY() {
        return maxY;
    }

    /**
     * @param maxY the maxY to set
     */
    public void setMaxY(double maxY) {
        this.maxY = maxY;
    }

    /**
     * @return the topMargin
     */
    public int getTopMargin() {
        return topMargin;
    }

    /**
     * @param topMargin the topMargin to set
     */
    public void setTopMargin(int topMargin) {
        this.topMargin = topMargin;
    }

    /**
     * @return the leftMargin
     */
    public int getLeftMargin() {
        return leftMargin;
    }

    /**
     * @param leftMargin the leftMargin to set
     */
    public void setLeftMargin(int leftMargin) {
        this.leftMargin = leftMargin;
    }

    /**
     * @return the minX
     */
    public double getMinX() {
        return minX;
    }

    /**
     * @param minX the minX to set
     */
    public void setMinX(double minX) {
        this.minX = minX;
    }

    /**
     * @return the maxX
     */
    public double getMaxX() {
        return maxX;
    }

    /**
     * @param maxX the maxX to set
     */
    public void setMaxX(double maxX) {
        this.maxX = maxX;
    }

    /**
     * @return the changedDates
     */
    public ValueModel[] getChangedDates() {
        return changedDates;
    }

    /**
     * @param changedDates the changedDates to set
     */
    public void setChangedDates(ValueModel[] changedDates) {
        this.changedDates = ValueModel.cullNullsFromArray( changedDates );
    }

    /**
     * @return the originalDates
     */
    public ValueModel[] getOriginalDates() {
        return originalDates;
    }

    /**
     * @param originalDates the originalDates to set
     */
    public void setOriginalDates(ValueModel[] originalDates) {
        this.originalDates = ValueModel.cullNullsFromArray(originalDates);
    }

    /**
     * @return the showDates
     */
    public boolean[] getShowDates() {
        return showDates;
    }

    /**
     * @param showDates the showDates to set
     */
    public void setShowDates(boolean[] showDates) {
        this.showDates = showDates;
    }
}
