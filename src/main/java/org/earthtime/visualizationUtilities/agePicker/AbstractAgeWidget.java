/*
 * AbstractAgeWidget.java
 *
 * Created Oct 26, 2011
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
package org.earthtime.visualizationUtilities.agePicker;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JLayeredPane;

/**
 *
 * @author James F. Bowring
 */
public class AbstractAgeWidget extends JLayeredPane {

    /**
     * 
     */
    protected int leftX;
    /**
     * 
     */
    protected int topY;
    /**
     * 
     */
    protected int boxWidth;
    /**
     * 
     */
    protected int boxHeight;
    /**
     * 
     */
    protected double minAgeMA = 0;
    /**
     * 
     */
    protected double maxAgeMA = 4500;
    /**
     * 
     */
    protected double displayOffsetX = 0;

    /**
     * 
     * @param leftX
     * @param topY
     * @param boxWidth
     * @param boxHeight
     */
    public AbstractAgeWidget(int leftX, int topY, int boxWidth, int boxHeight) {
        this.leftX = leftX;
        this.topY = topY;
        this.boxWidth = boxWidth;
        this.boxHeight = boxHeight;

        this.setBounds(leftX, topY, boxWidth, boxHeight);
        this.setOpaque(true);
        this.setBackground(Color.WHITE);
    }

    /**
     * 
     */
    public AbstractAgeWidget() {
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
    protected void paintInit(Graphics2D g2d) {
        RenderingHints rh = g2d.getRenderingHints();
        rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHints(rh);

        g2d.setColor(Color.BLACK);


    }

    /**
     * 
     * @param g2d
     */
    protected void paint(Graphics2D g2d) {
        paintInit(g2d);
        drawBounds(g2d);
    }

    /**
     * 
     * @param g2d
     */
    protected void drawBounds(Graphics2D g2d) {

        g2d.drawRect(
                0, 0, boxWidth - 1, boxHeight - 1);

    }

    /**
     * 
     * @param x
     * @return
     */
    public double mapX(double x) {

        return (((x - getMinAgeMA_Display()) / getRangeX_Display()) * boxWidth);// + (double) leftMargin;
    }

    /**
     * 
     * @return
     */
    public double getMinAgeMA_Display() {
        return getMinAgeMA() + displayOffsetX;
    }

    /**
     * 
     * @return
     */
    public double getMaxAgeMA_Display() {
        return getMaxAgeMA() + displayOffsetX;
    }

    /**
     * @return the minAgeMA
     */
    public double getMinAgeMA() {
        return minAgeMA;
    }

    /**
     * @param minAgeMA the minAgeMA to set
     */
    public void setMinAgeMA(double minAgeMA) {
        this.minAgeMA = minAgeMA;
    }

    /**
     * @return the maxAgeMA
     */
    public double getMaxAgeMA() {
        return maxAgeMA;
    }

    /**
     * @param maxAgeMA the maxAgeMA to set
     */
    public void setMaxAgeMA(double maxAgeMA) {
        this.maxAgeMA = maxAgeMA;
    }
    
       /**
     * 
     * @return
     */
    public double getRangeX_Display () {
        return (getMaxAgeMA_Display() - getMinAgeMA_Display());
    }
}
