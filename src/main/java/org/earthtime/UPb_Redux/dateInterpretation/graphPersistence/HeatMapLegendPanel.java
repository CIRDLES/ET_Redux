/*
 * HeatMapLegendPanel.java
 *
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
package org.earthtime.UPb_Redux.dateInterpretation.graphPersistence;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JLayeredPane;
import org.earthtime.colorModels.HeatMap;

/**
 *
 * @author James F. Bowring
 */
public class HeatMapLegendPanel extends JLayeredPane {

    // Class Variables
    private int boxWidth = 400;

    private int boxHeight = 50;

    // Instance Variables
    private String title;

    private String titleFont;

    private String titleFontSize;

    private String subTitleText;

    private boolean titleBoxShow;

    /**
     * Creates a new instance of HeatMapLegendPanel
     *
     * @param title
     */
    public HeatMapLegendPanel(String title) {
        super();

        setOpaque(false);

        setBackground(Color.white);

        setBounds(100, 50, boxWidth, boxHeight);

        this.title = title;
        titleFont = "Serif";
        titleFontSize = "12";
        subTitleText = "variable";
        titleBoxShow = true;
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

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1.0f));

        g2d.setFont(new Font(
                getTitleFont(),
                Font.BOLD,
                Integer.parseInt(getTitleFontSize()) - 2));

        g2d.drawString(getSubtitle(), getX() + 10, getY() + 25 + 1.5f * Integer.valueOf(getTitleFontSize()));

        if (isTitleBoxShow()) {
            DrawBounds(g2d);
        }

        for (int i = 5; i < getBoxWidth() - 5; i++) {
            int selectedIndex = HeatMap.selectColorInRange(0, 0, ((double) i) / (getBoxWidth() - 5));
            int rgb = HeatMap.getRgb().get(selectedIndex);

            g2d.setColor(new Color(rgb));
            g2d.drawLine(i + getX(), 3 + getY(), i + getX(), 3 + getY() + 20);
        }

    }

    private void DrawBounds(Graphics2D g2d) {
        g2d.drawRect(
                getX(), getY(), getBoxWidth(), getBoxHeight());
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
    }

    /**
     *
     * @return
     */
    public int getBoxWidth() {
        return boxWidth;
    }

    /**
     *
     * @param boxWidth
     */
    public void setBoxWidth(int boxWidth) {
        this.boxWidth = boxWidth;
    }

    /**
     *
     * @return
     */
    public int getBoxHeight() {
        return boxHeight;
    }

    /**
     *
     * @param boxHeight
     */
    public void setBoxHeight(int boxHeight) {
        this.boxHeight = boxHeight;
    }

    /**
     *
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     *
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     *
     * @return
     */
    public String getTitleFont() {
        return titleFont;
    }

    /**
     *
     * @param titleFont
     */
    public void setTitleFont(String titleFont) {
        this.titleFont = titleFont;
    }

    /**
     *
     * @return
     */
    public String getTitleFontSize() {
        return titleFontSize;
    }

    /**
     *
     * @param titleFontSize
     */
    public void setTitleFontSize(String titleFontSize) {
        this.titleFontSize = titleFontSize;
    }

    /**
     *
     * @return
     */
    public String getSubtitle() {
        return subTitleText;
    }

    /**
     *
     * @param subTitleText
     */
    public void setSubtitle(String subTitleText) {
        this.subTitleText = subTitleText;
    }

    /**
     *
     * @return
     */
    public boolean isTitleBoxShow() {
        return titleBoxShow;
    }

    /**
     *
     * @param titleBoxShow
     */
    public void setTitleBoxShow(boolean titleBoxShow) {
        this.titleBoxShow = titleBoxShow;
    }
}
