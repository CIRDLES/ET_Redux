/*
 * KwikiDateDisplayPanel.java
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
package org.earthtime.UPb_Redux.dateInterpretation.kwiki;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.dataDictionaries.RadDates;

/**
 *
 * @author James F. Bowring
 */
public class KwikiDateDisplayPanel extends JPanel {

    private int topMargin = 1;
    private int leftMargin = 2;
    private double minY;
    private double maxY;
    private double minX;
    private double maxX;
    private ValueModel[] changedDates;
    private boolean autoUraniumGeneratedDate;

    /** Creates a new instance of KwikiDateDisplayPanel
     * @param x 
     * @param y 
     */
    public KwikiDateDisplayPanel ( int x, int y ) {
        super();

        setOpaque( true );

        setBackground(ReduxConstants.mySampleYellowColor );

        setBounds( x, y, 762, 20 );

        setChangedDates( null );

        setAutoUraniumGeneratedDate( false );

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

        double rangeX = (getMaxX() - getMinX());
        double rangeY = (getMaxY() - getMinY());

        DrawBounds( g2d );
        DrawDateNames( getChangedDates(), g2d, rangeX, rangeY );

    }

    private double MapX ( double x, double minX, double rangeX, int widthG ) {

        return (((x - minX) / rangeX) * (double) widthG) + (double) getLeftMargin();
    }

    private double MapY ( double y, double maxY, double rangeY, int heightG ) {

        return (((maxY - y) / rangeY) * (double) (heightG)) + (double) getTopMargin();
    }

    private void DrawBounds ( Graphics2D g2d ) {

        g2d.drawRect( 0, 0, getWidth() - 1, getHeight() - 1 );

    }

    private void DrawDateNames (
            ValueModel[] dates,
            Graphics2D g2d,
            double rangeX,
            double rangeY ) {

        g2d.setFont( new Font( "SansSerif", Font.BOLD, 12 ) );

        for (int i = 0; i < dates.length; i ++) {
            int leftOffset = i * 250 + 65;//70;

            // strip out "age" and split on underscore
            String[] dateName = dates[i].getName().substring( 3 ).split( "_" );

            g2d.setFont( new Font( "SansSerif", Font.BOLD, 12 ) );
            if ( isAutoUraniumGeneratedDate() && i < 2 ) {
                // non 206/207
                g2d.drawString(
                        "Auto-U:",
                        (float) MapX( getMinX(), getMinX(), rangeX, getWidth() ) + leftOffset - 50,
                        (float) MapY( getMinY(), getMaxY(), rangeY, getHeight() ) - 7 );
            }
            g2d.drawString(
                    dateName[0] + "/",
                    (float) MapX( getMinX(), getMinX(), rangeX, getWidth() ) + leftOffset,
                    (float) MapY( getMinY(), getMaxY(), rangeY, getHeight() ) - 9 );

            g2d.drawString(
                    dateName[1].substring( 0, 3 ),
                    (float) MapX( getMinX(), getMinX(), rangeX, getWidth() ) + leftOffset + 24,
                    (float) MapY( getMinY(), getMaxY(), rangeY, getHeight() ) - 5 );


            // sep 2010 add in Th and Pa correction notations
            g2d.setFont( new Font( "SansSerif", Font.BOLD, 10 ) );
            int correctionIndex = dates[i].getName().indexOf( "r_" );
            if ( correctionIndex > 0 ) {
                g2d.drawString(
                        "<" + dates[i].getName().substring( correctionIndex + 2, dates[i].getName().length() ) + ">",
                        (float) MapX( getMinX(), getMinX(), rangeX, getWidth() ) + leftOffset + 45,
                        (float) MapY( getMinY(), getMaxY(), rangeY, getHeight() ) - 5 );
            }

            g2d.setFont( new Font( "SansSerif", Font.BOLD, 16 ) );

            String dateValue = "";
            if ( dates[i].hasPositiveValue() || dates[i].getName().contains( RadDates.age207_206r.getName() ) ) {
                dateValue = dates[i].formatValueAndTwoSigmaForPublicationSigDigMode(//
                        dates[i].getUncertaintyType(), -6, 2 );
            } else {
                dateValue = "Not Calculated";
            }
            g2d.drawString(
                    dateValue,
                    (float) MapX( getMinX(), getMinX(), rangeX, getWidth() ) + leftOffset + 85,
                    (float) MapY( getMinY(), getMaxY(), rangeY, getHeight() ) - 5 );
        }

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

        this.removeAll();

        setMinX( 0 );
        setMinY( 0 );
        setMaxX( getWidth() );
        setMaxY( getHeight() );
    }

    /**
     * @return the topMargin
     */
    public int getTopMargin () {
        return topMargin;
    }

    /**
     * @param topMargin the topMargin to set
     */
    public void setTopMargin ( int topMargin ) {
        this.topMargin = topMargin;
    }

    /**
     * @return the leftMargin
     */
    public int getLeftMargin () {
        return leftMargin;
    }

    /**
     * @param leftMargin the leftMargin to set
     */
    public void setLeftMargin ( int leftMargin ) {
        this.leftMargin = leftMargin;
    }

    /**
     * @return the minY
     */
    public double getMinY () {
        return minY;
    }

    /**
     * @param minY the minY to set
     */
    public void setMinY ( double minY ) {
        this.minY = minY;
    }

    /**
     * @return the maxY
     */
    public double getMaxY () {
        return maxY;
    }

    /**
     * @param maxY the maxY to set
     */
    public void setMaxY ( double maxY ) {
        this.maxY = maxY;
    }

    /**
     * @return the minX
     */
    public double getMinX () {
        return minX;
    }

    /**
     * @param minX the minX to set
     */
    public void setMinX ( double minX ) {
        this.minX = minX;
    }

    /**
     * @return the maxX
     */
    public double getMaxX () {
        return maxX;
    }

    /**
     * @param maxX the maxX to set
     */
    public void setMaxX ( double maxX ) {
        this.maxX = maxX;
    }

    /**
     * @return the changedDates
     */
    public ValueModel[] getChangedDates () {
        return changedDates;
    }

    /**
     * @param changedDates the changedDates to set
     */
    public void setChangedDates ( ValueModel[] changedDates ) {
        this.changedDates = ValueModel.cullNullsFromArray( changedDates );
    }

    /**
     * @return the autoUraniumGeneratedDate
     */
    public boolean isAutoUraniumGeneratedDate () {
        return autoUraniumGeneratedDate;
    }

    /**
     * @param autoUraniumGeneratedDate the autoUraniumGeneratedDate to set
     */
    public void setAutoUraniumGeneratedDate ( boolean autoUraniumGeneratedDate ) {
        this.autoUraniumGeneratedDate = autoUraniumGeneratedDate;
    }
}
