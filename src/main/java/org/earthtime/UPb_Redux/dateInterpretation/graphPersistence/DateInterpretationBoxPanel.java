/*
 * DateInterpretationBoxPanel.java
 *
 * Created on November 28, 2008
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

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import javax.swing.JLayeredPane;
import org.earthtime.UPb_Redux.valueModels.SampleDateModel;
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public class DateInterpretationBoxPanel extends JLayeredPane{//JPanel {

    // Class Variables
    private int boxWidth = 200;
    private int boxHeight = 100;    // Instance Variables
    private ValueModel preferredDateModel;
    private String dateFontName;
    private String dateFontSize;
    private boolean visibleBoxOutline;
    private boolean dateShowDate;
    private boolean dateShowMSWD;
    private boolean dateShowN;

    /** Creates a new instance of DateInterpretationBoxPanel
     * @param preferredDateModel 
     */
    public DateInterpretationBoxPanel(ValueModel preferredDateModel) {
        super();

        setOpaque(false);

        setBackground(Color.white);

        setBounds(100, 150, boxWidth, boxHeight);
        setPreferredDateModel(preferredDateModel);
        setDateFontName("Monospaced");
        setDateFontSize("12");
        setVisibleBoxOutline(true);
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

        float verticalSpacer = 4.5f;
        
        g2d.setColor(Color.BLACK);

       // www.hanb.co.kr/web/example/1058/examples/ShowOff.java
        // Find the bounds of the larger string.
        FontRenderContext frc = g2d.getFontRenderContext();
        
        String SAMname = ((SampleDateModel) preferredDateModel).getName();
        String dateName = ((SampleDateModel) preferredDateModel).//
                    FormatValueAndTwoSigmaABSThreeWaysForPublication(6, 2);
        String MSWDname = ((SampleDateModel) preferredDateModel).ShowCustomMSWDwithN();
        
        String text = //
                (String)(SAMname.length() > dateName.length() ? SAMname : dateName);

        text = //
                (String)(text.length() > MSWDname.length() ? text : MSWDname);
        
        TextLayout mLayout = //
                new TextLayout(
                text.trim().toUpperCase(), new Font(
                getDateFontName(),
                Font.BOLD,
                Integer.parseInt(getDateFontSize())), frc);

        Rectangle2D bounds = mLayout.getBounds();
        setBoxWidth((int) bounds.getWidth() + 15);
        setBoxHeight((int) bounds.getHeight() * 6 //
                + (isDateShowDate() ? 2 : 0) * (int)Integer.valueOf(getDateFontSize()));
        
        if (isVisibleBoxOutline()) {
            DrawBounds(g2d);
        }
        g2d.setFont(new Font(
                getDateFontName(),
                Font.BOLD,
                Integer.parseInt(getDateFontSize())));

        g2d.drawString(((SampleDateModel) preferredDateModel).//
                getAliquot().getAliquotName(), getX() + 10, getY() + Integer.valueOf(getDateFontSize()));

        g2d.drawString(preferredDateModel.//
                getName(), getX() + 10, getY() + 3.0f * Integer.valueOf(getDateFontSize()));


        if (isDateShowDate()) {
            g2d.drawString(((SampleDateModel) preferredDateModel).//
                    FormatValueAndTwoSigmaABSThreeWaysForPublication(6, 2),
                    getX() + 10, getY() + verticalSpacer * Integer.valueOf(getDateFontSize()));
            verticalSpacer += 1.5f;
        }

        if (isDateShowMSWD() && isDateShowN()) {
            g2d.drawString(((SampleDateModel) preferredDateModel).//
                    ShowCustomMSWDwithN(),
                    getX() + 10, getY() + verticalSpacer * Integer.valueOf(getDateFontSize()));
        } else if (isDateShowMSWD()) {
            g2d.drawString(((SampleDateModel) preferredDateModel).//
                    ShowCustomMSWD(),
                    getX() + 10, getY() + verticalSpacer * Integer.valueOf(getDateFontSize()));
        } else if (isDateShowN()) {
            g2d.drawString(((SampleDateModel) preferredDateModel).//
                    ShowCustomN(),
                    getX() + 10, getY() + verticalSpacer * Integer.valueOf(getDateFontSize()));
        }
    }

    private void DrawBounds(
            Graphics2D g2d) {

        // draw and label axes
        g2d.setFont(new Font("Monospaced", Font.BOLD, 20));
        g2d.drawRect(
                getX(), getY(), getBoxWidth(), getBoxHeight());

    }

//    public void refreshPanel() {
//        preparePanel();
//        repaint();
//    }
//
//    public void preparePanel() {
//    }

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
    public ValueModel getPreferredDateModel() {
        return preferredDateModel;
    }

    /**
     * 
     * @param preferredDateModel
     */
    public void setPreferredDateModel(ValueModel preferredDateModel) {
        this.preferredDateModel = preferredDateModel;
    }

    /**
     * 
     * @return
     */
    public String getDateFontName() {
        return dateFontName;
    }

    /**
     * 
     * @param dateFontName
     */
    public void setDateFontName(String dateFontName) {
        this.dateFontName = dateFontName;
    }

    /**
     * 
     * @return
     */
    public String getDateFontSize() {
        return dateFontSize;
    }

    /**
     * 
     * @param dateFontSize
     */
    public void setDateFontSize(String dateFontSize) {
        this.dateFontSize = dateFontSize;
    }

    /**
     * 
     * @return
     */
    public boolean isVisibleBoxOutline() {
        return visibleBoxOutline;
    }

    /**
     * 
     * @param visibleBoxOutline
     */
    public void setVisibleBoxOutline(boolean visibleBoxOutline) {
        this.visibleBoxOutline = visibleBoxOutline;
    }

    /**
     * 
     * @return
     */
    public boolean isDateShowDate() {
        return dateShowDate;
    }

    /**
     * 
     * @param dateShowDate
     */
    public void setDateShowDate(boolean dateShowDate) {
        this.dateShowDate = dateShowDate;
    }

    /**
     * 
     * @return
     */
    public boolean isDateShowMSWD() {
        return dateShowMSWD;
    }

    /**
     * 
     * @param dateShowMSWD
     */
    public void setDateShowMSWD(boolean dateShowMSWD) {
        this.dateShowMSWD = dateShowMSWD;
    }

    /**
     * 
     * @return
     */
    public boolean isDateShowN() {
        return dateShowN;
    }

    /**
     * 
     * @param dateShowN
     */
    public void setDateShowN(boolean dateShowN) {
        this.dateShowN = dateShowN;
    }
}
