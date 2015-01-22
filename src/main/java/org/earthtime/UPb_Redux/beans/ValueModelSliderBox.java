/*
 * ValueModelSliderBox.java
 *
 * Created on March 4, 2008, 3:08 PM
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
package org.earthtime.UPb_Redux.beans;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;

/**
 *
 * @author James F. Bowring
 */
public class ValueModelSliderBox extends JPanel {

    // Instance Variables
    private int boxWidth;// = 70;
    private int boxHeight;// = 17;//12;
    private int leftX;
    private String valueString;
    private String fontName;
    private String fontSize;
    private Font valueFont;

    /** Creates a new instance of ValueModelSliderBox
     * @param widthParent 
     * @param boxHeight 
     * @param value 
     */
    public ValueModelSliderBox ( int widthParent, int boxHeight, String value ) {
        super();

        setOpaque( false );

        this.fontName =  "SansSerif" ;
        this.fontSize = "10" ;
        valueFont = new Font(
                fontName,
                Font.BOLD,
                Integer.parseInt( fontSize ) );

        boxWidth = value.length() * 6;//8;
        leftX = (widthParent / 2) - (boxWidth / 2);

        this.boxHeight = boxHeight;

        centerSliderBox( value );
    }

    /**
     * 
     * @param g
     */
    @Override
    protected void paintComponent ( Graphics g ) {
        super.paintComponent( g );

        paint( (Graphics2D) g );
    }

    /**
     * 
     * @param g2d
     */
    public void paint ( Graphics2D g2d ) {

        RenderingHints rh = g2d.getRenderingHints();
        rh.put( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        rh.put( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
        g2d.setRenderingHints( rh );

        g2d.setColor( Color.BLACK );

        // build the box to fit the value string
        TextLayout mLayout = //
                new TextLayout(
                getValueString(), valueFont, g2d.getFontRenderContext() );

        Rectangle2D bounds = mLayout.getBounds();
        setBoxWidth( (int) bounds.getWidth() + 4 );

        drawBounds( g2d );

        g2d.setFont( valueFont );

        g2d.drawString( getValueString(), getX() + 3, getY() + boxHeight - 3 );


    }

    /**
     * 
     * @param value
     */
    protected void centerSliderBox ( String value ) {
        setValueString( value );
        setBounds( leftX, 1, boxWidth, boxHeight );
    }

    /**
     * 
     * @param x
     */
    public void moveSliderBox ( int x ) {
        setBounds( x, 1, boxWidth, boxHeight );
    }

    private void drawBounds ( Graphics2D g2d ) {

        g2d.drawRect(
                getX(), getY(), getBoxWidth(), getBoxHeight() );

    }

    /**
     * 
     */
    public void refreshPanel () {
        preparePanel();
        repaint();
    }

    /**
     * 
     */
    public void preparePanel () {
    }

    /**
     * 
     * @return
     */
    public int getBoxWidth () {
        return boxWidth;
    }

    /**
     * 
     * @param boxWidth
     */
    public void setBoxWidth ( int boxWidth ) {
        this.boxWidth = boxWidth;
    }

    /**
     * 
     * @return
     */
    public int getBoxHeight () {
        return boxHeight;
    }

    /**
     * 
     * @param boxHeight
     */
    public void setBoxHeight ( int boxHeight ) {
        this.boxHeight = boxHeight;
    }

    /**
     * 
     * @return
     */
    public String getValueString () {
        return valueString;
    }

    /**
     * 
     * @param valueString
     */
    public void setValueString ( String valueString ) {
        this.valueString = valueString;
    }

    /**
     * 
     * @return
     */
    public String getFontName () {
        return fontName;
    }

    /**
     * 
     * @param fontName
     */
    public void setFontName ( String fontName ) {
        this.fontName = fontName;
    }

    /**
     * 
     * @return
     */
    public String getTitleFontSize () {
        return fontSize;
    }

    /**
     * 
     * @param titleFontSize
     */
    public void setFontSize ( String titleFontSize ) {
        this.fontSize = titleFontSize;
    }
}
