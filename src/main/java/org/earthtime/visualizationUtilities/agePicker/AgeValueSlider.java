/*
 * AgeValueSlider.java
 *
 * Created Oct 26, 2011
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
package org.earthtime.visualizationUtilities.agePicker;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.Collections;
import javax.swing.event.MouseInputListener;

/**
 *
 * @author James F. Bowring
 */
public class AgeValueSlider extends AbstractAgeWidget implements MouseInputListener {

//    private int leftX;
    private int rightX;
//    private int minAgeMA;
//    private int maxAgeMA;
//    private int displayOffsetX;
    private int centerX;
    private int currentMouseX;
    private int pressedMouseX;
//    // to move
//    private ArrayList<Double> level1GeoAge;
//    private String[] level1Names;
    private Font textFont;

    /**
     * 
     * @param leftX
     * @param topY
     * @param boxWidth
     * @param boxHeight
     * @param rightX
     * @param minAgeMA
     * @param maxAgeMA
     */
    public AgeValueSlider ( int leftX, int topY, int boxWidth, int boxHeight, int rightX, int minAgeMA, int maxAgeMA ) {
        super( leftX, topY, boxWidth, boxHeight );

        this.leftX = leftX;
        this.rightX = rightX;
        this.minAgeMA = minAgeMA;
        this.maxAgeMA = maxAgeMA;
        this.displayOffsetX = 0;


        this.centerX = boxWidth / 2;
        this.currentMouseX = 0;


        this.setOpaque( false );

        addMouseListener( this );
        addMouseMotionListener( this );
        
       
        textFont = new Font(
                "SansSerif",
                Font.PLAIN,
                10);
    }

    /**
     * 
     * @param g2d
     */
    @Override
    protected void paint ( Graphics2D g2d ) {
        super.paintInit( g2d );
        drawBounds( g2d );
        
        g2d.setFont(textFont);
        
        double age = convertMouseXToValue( getX() - leftX );
        g2d.drawString( Integer.toString( (int)age), 10, boxHeight - 6);
        
        int index = Collections.binarySearch(GeoAges.level1, age);
        if (index == -1) {
                index = 0;
            }
            if (index < 0) {
                index = Math.abs(index) - 1;
            }
            if (index >= (GeoAges.level1.size() - 1)) {
                index = GeoAges.level1.size() - 1;
            }
        g2d.drawString( GeoAges.level1Names[index], 1,  12);
    }

    /**
     * 
     * @param g2d
     */
    @Override
    protected void drawBounds ( Graphics2D g2d ) {

        g2d.drawRect( 0, 0, boxWidth - 1, 19 );
        g2d.drawRect( 0, boxHeight - 20, boxWidth - 1, 19 );

        g2d.setColor( Color.red );
        g2d.drawLine( centerX, 20, centerX, boxHeight - 20 );
    }

    /**
     * 
     * @param x
     * @return
     */
    protected int convertMouseXToValue ( int x ) {
        return //
                (int) Math.round(
                (((double) (x) / (double) (rightX - leftX))) //
                * getRangeX_Display()//
                + getMinX_Display());
    }

    public double getRangeX_Display () {
        return (getMaxX_Display() - getMinX_Display());
    }

    /**
     * 
     * @return
     */
    public double getMinX_Display () {
        return minAgeMA + displayOffsetX;
    }

    /**
     * 
     * @return
     */
    public double getMaxX_Display () {
        return maxAgeMA + displayOffsetX;
    }

    private boolean mouseInPullTab () {

        boolean mouseInPullTab = false;


        mouseInPullTab = (currentMouseX > 0)//
                && //
                (currentMouseX < boxWidth);


        return mouseInPullTab;
    }

    /**
     * 
     * @param e
     */
    @Override
    public void mouseClicked ( MouseEvent e ) {
//        throw new UnsupportedOperationException( "Not supported yet." );
    }

    /**
     * 
     * @param e
     */
    @Override
    public void mousePressed ( MouseEvent e ) {
        currentMouseX = e.getX();
        pressedMouseX = e.getX();
    }

    /**
     * 
     * @param e
     */
    @Override
    public void mouseReleased ( MouseEvent e ) {
//        throw new UnsupportedOperationException( "Not supported yet." );
    }

    /**
     * 
     * @param e
     */
    @Override
    public void mouseEntered ( MouseEvent e ) {
//        throw new UnsupportedOperationException( "Not supported yet." );
    }

    /**
     * 
     * @param e
     */
    @Override
    public void mouseExited ( MouseEvent e ) {
//        throw new UnsupportedOperationException( "Not supported yet." );
    }

    /**
     * 
     * @param e
     */
    @Override
    public void mouseDragged ( MouseEvent e ) {
        if ( mouseInPullTab() ) {
            currentMouseX = e.getX();
            if ( ((getX() + (currentMouseX - pressedMouseX)) >= leftX) //
                    && //
                    ((getX() + (currentMouseX - pressedMouseX)) <= rightX) ) {
                this.setLocation( getX() + (currentMouseX - pressedMouseX), getY() );
            }
            currentMouseX = pressedMouseX;

        }
    }

    /**
     * 
     * @param e
     */
    @Override
    public void mouseMoved ( MouseEvent e ) {
//        throw new UnsupportedOperationException( "Not supported yet." );
    }

    /**
     * @param minAgeMA the minAgeMA to set
     */
    public void setMinAgeMA ( int minAgeMA ) {
        this.minAgeMA = minAgeMA;
    }

    /**
     * @param maxAgeMA the maxAgeMA to set
     */
    public void setMaxAgeMA ( int maxAgeMA ) {
        this.maxAgeMA = maxAgeMA;
    }
    
    
    
}