/*
 * TitleBoxPanel.java
 *
 * Created on March 4, 2008, 3:08 PM
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
package org.earthtime.UPb_Redux.dateInterpretation.graphPersistence;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;

/**
 *
 * @author James F. Bowring
 */
public class TitleBoxPanel extends JPanel {

    // Class Variables
    private int boxWidth = 200;

    private int boxHeight = 100;

    // Instance Variables
    private String title;

    private String titleFont;

    private String titleFontSize;

    private String subTitleText;

    private boolean titleBoxShow;

    /**
     * Creates a new instance of TitleBoxPanel
     *
     * @param title
     */
    public TitleBoxPanel(String title) {
        super();

        setOpaque(false);

        setBackground(Color.white);

        setBounds(100, 50, boxWidth, boxHeight);

        this.title = title;
        titleFont = "Monospaced";
        titleFontSize = "18";
        subTitleText = "Subtitle";
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

        // www.hanb.co.kr/web/example/1058/examples/ShowOff.java
        // Find the bounds of the larger string.
        FontRenderContext frc = g2d.getFontRenderContext();
        String text = (getTitle().length() > getSubtitle().length() ? getTitle() : getSubtitle());
        TextLayout mLayout = //
                new TextLayout(
                        text.trim().toUpperCase(), new Font(
                                getTitleFont(),
                                Font.BOLD,
                                Integer.parseInt(getTitleFontSize()) + 1), frc);

        Rectangle2D bounds = mLayout.getBounds();
        setBoxWidth((int) bounds.getWidth() + 25);
        setBoxHeight((int) (bounds.getHeight() * 4.7f));

        if (isTitleBoxShow()) {
            DrawBounds(g2d);
        }
        g2d.setFont(new Font(
                getTitleFont(),
                Font.BOLD,
                Integer.parseInt(getTitleFontSize())));

        g2d.drawString(getTitle(), getX() + 10, getY() + 25);

        g2d.setFont(new Font(
                getTitleFont(),
                Font.BOLD,
                Integer.parseInt(getTitleFontSize()) - 2));

        g2d.drawString(getSubtitle(), getX() + 10, getY() + 25 + 1.5f * Integer.valueOf(getTitleFontSize()));

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
