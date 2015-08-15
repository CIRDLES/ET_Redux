/*
 * AgeExtents.java
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
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Collections;
import javax.swing.JLayeredPane;
import javax.swing.event.MouseInputListener;

/**
 *
 * @author James F. Bowring
 */
public class AgeExtents extends AbstractAgeWidget implements MouseInputListener {

    private int displayOffsetX = 0;
    private int xOnMousePress;
    private boolean zoom = false;
    private boolean panning = false;
    private JLayeredPane leftAgeSlider;

    /**
     * 
     * @param leftX
     * @param topY
     * @param boxWidth
     * @param boxHeight
     * @param minAgeMA
     * @param maxAgeMA
     */
    public AgeExtents(int leftX, int topY, int boxWidth, int boxHeight, double minAgeMA, double maxAgeMA) {
        super(leftX, topY, boxWidth, boxHeight);

        this.minAgeMA = minAgeMA;
        this.maxAgeMA = maxAgeMA;
        this.xOnMousePress = 0;

        this.setBounds(leftX, topY, boxWidth, boxHeight);
        this.setOpaque(true);
        this.setBackground(Color.WHITE);

        leftAgeSlider = new AgeValueSlider(125, 25, 50, 100, 875, 0, 4500);

        addMouseListener(this);
        addMouseMotionListener(this);

        preparePanel();
    }

    /**
     * @param displayOffsetX the displayOffsetX to set
     */
    public void setDisplayOffsetX(int displayOffsetX) {
        this.displayOffsetX = displayOffsetX;
    }

    /**
     * 
     */
    public class GeoAgeLabelMouseListener implements MouseListener {

        private AgeExtents ageExtents;
        private int locationIndex;

        GeoAgeLabelMouseListener(AgeExtents ageExtents, int locationIndex) {
            this.ageExtents = ageExtents;
            this.locationIndex = locationIndex;
        }

        /**
         * 
         * @param e
         */
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                double minAge = (locationIndex > 0) ? GeoAges.level1.get(locationIndex - 1) : 0.0;

                ((AgeValueSlider) ageExtents.getLeftAgeSlider()).setMinAgeMA((int) minAge);
                ((AgeValueSlider) ageExtents.getLeftAgeSlider()).setMaxAgeMA(GeoAges.level1.get(locationIndex));
                ((AgeValueSlider) ageExtents.getLeftAgeSlider()).repaint();

                ageExtents.setMinAgeMA(minAge);
                ageExtents.setMaxAgeMA(GeoAges.level1.get(locationIndex));
                ageExtents.preparePanel();
                ageExtents.repaint();
            }
            if (e.getButton() != MouseEvent.BUTTON1) {

                int currentMaxIndex = Collections.binarySearch(GeoAges.level1, ageExtents.getMaxAgeMA());
                if (currentMaxIndex == -1) {
                    currentMaxIndex = 0;
                }
                if (currentMaxIndex < 0) {
                    currentMaxIndex = Math.abs(currentMaxIndex) - 1;
                }
                if (currentMaxIndex >= (GeoAges.level1.size() - 1)) {
                    currentMaxIndex = GeoAges.level1.size() - 1;
                }

                double maxAge = (currentMaxIndex < GeoAges.level1.size() - 1) ? GeoAges.level1.get(currentMaxIndex + 1) : GeoAges.level1.get(currentMaxIndex);

                int currentMinIndex = Collections.binarySearch(GeoAges.level1, ageExtents.getMinAgeMA());
                if (currentMinIndex == -1) {
                    currentMinIndex = 0;
                }
                if (currentMinIndex < 0) {
                    currentMinIndex = Math.abs(currentMinIndex) - 1;
                }
                if (currentMinIndex >= (GeoAges.level1.size() - 1)) {
                    currentMinIndex = GeoAges.level1.size() - 1;
                }

                double minAge = (currentMinIndex > 0) ? GeoAges.level1.get(currentMinIndex - 1) : 0.0;

                ((AgeValueSlider) ageExtents.getLeftAgeSlider()).setMinAgeMA(minAge);
                ((AgeValueSlider) ageExtents.getLeftAgeSlider()).setMaxAgeMA(maxAge);
                ((AgeValueSlider) ageExtents.getLeftAgeSlider()).repaint();

                ageExtents.setMinAgeMA(minAge);
                ageExtents.setMaxAgeMA(maxAge);
                ageExtents.preparePanel();
                ageExtents.repaint();
            }
        }

        /**
         * 
         * @param e
         */
        @Override
        public void mousePressed(MouseEvent e) {
//                    throw new UnsupportedOperationException("Not supported yet.");
        }

        /**
         * 
         * @param e
         */
        @Override
        public void mouseReleased(MouseEvent e) {
//                    throw new UnsupportedOperationException("Not supported yet.");
        }

        /**
         * 
         * @param e
         */
        @Override
        public void mouseEntered(MouseEvent e) {
//                    throw new UnsupportedOperationException("Not supported yet.");
        }

        /**
         * 
         * @param e
         */
        @Override
        public void mouseExited(MouseEvent e) {
//                    throw new UnsupportedOperationException("Not supported yet.");
        }
    }

    /**
     * 
     * @param g2d
     */
    @Override
    public void paint(Graphics2D g2d) {

        super.paint(g2d);


    }

    /**
     * 
     */
    public void preparePanel() {

        this.removeAll();

        double previousX = 0;
        for (int i = 0; i < GeoAges.level1Names.length; i++) {
            GeoAgeLabel label = new GeoAgeLabel(//
                    i,//
                    GeoAges.level1Names[i], //
                    mapX(previousX), //
                    0, mapX(GeoAges.level1.get(i)) - mapX(previousX),//
                    15);
            add(label, javax.swing.JLayeredPane.DEFAULT_LAYER);

            label.addMouseListener(new GeoAgeLabelMouseListener(this, i));

            previousX = GeoAges.level1.get(i);

        }
    }

    /**
     * 
     * @param x
     * @return
     */
    protected int convertMouseXToValue(int x) {
        return //
                (int) Math.round(
                (((double) (x) / (double) (boxWidth))) //
                * getRangeX_Display()//
                + getMinX_Display());
    }

    public double getRangeX_Display() {
        return (getMaxX_Display() - getMinX_Display());
    }

    /**
     * 
     * @return
     */
    public double getMinX_Display() {
        return getMinAgeMA() + displayOffsetX;
    }

    /**
     * 
     * @return
     */
    public double getMaxX_Display() {
        return getMaxAgeMA() + displayOffsetX;
    }

    /**
     * 
     * @param e
     */
    @Override
    public void mouseClicked(MouseEvent e) {
//        throw new UnsupportedOperationException( "Not supported yet." );
    }

    /**
     * 
     * @param evt
     */
    @Override
    public void mousePressed(MouseEvent evt) {
        xOnMousePress = evt.getX();
    }

    /**
     * 
     * @param e
     */
    @Override
    public void mouseReleased(MouseEvent e) {
//        throw new UnsupportedOperationException( "Not supported yet." );
    }

    /**
     * 
     * @param e
     */
    @Override
    public void mouseEntered(MouseEvent e) {
//        throw new UnsupportedOperationException( "Not supported yet." );
    }

    /**
     * 
     * @param e
     */
    @Override
    public void mouseExited(MouseEvent e) {
//        throw new UnsupportedOperationException( "Not supported yet." );
    }

    /**
     * 
     * @param evt
     */
    @Override
    public void mouseDragged(MouseEvent evt) {
        int xOnMouseDragged = evt.getX();

        int xDelta = Math.abs((convertMouseXToValue(xOnMouseDragged) - convertMouseXToValue(xOnMousePress)));

        int currentX = convertMouseXToValue(xOnMouseDragged);
        zoom = (currentX > 80) & (currentX < getMaxAgeMA());
        panning = (currentX < 80) & (currentX > getMinAgeMA());

        // if ( zoom ) {
        if (xOnMouseDragged < xOnMousePress) {
            //System.out.println( "Mouse NORTH" ); zoomin
            setMaxAgeMA(Math.min(getMaxAgeMA() + xDelta, 4500));
            setMinAgeMA(getMinAgeMA() + xDelta);
        }

        if (xOnMouseDragged > xOnMousePress) {
//            System.out.println( "Mouse SOUTH" );zoomout
            setMaxAgeMA(getMaxAgeMA() - xDelta);
            setMinAgeMA(Math.max(getMinAgeMA() - xDelta, 0));
        }

//            tics = TicGeneratorForAxes.generateTics( minY, maxY, (int) (rawDataModelViews[0].getHeight() / 20.0) );

        ((AgeValueSlider) leftAgeSlider).setMinAgeMA(getMinAgeMA());
        ((AgeValueSlider) leftAgeSlider).setMaxAgeMA(getMaxAgeMA());
        //   }

    }

    /**
     * 
     * @param e
     */
    @Override
    public void mouseMoved(MouseEvent e) {
//        throw new UnsupportedOperationException( "Not supported yet." );
    }

    /**
     * @return the leftAgeSlider
     */
    public JLayeredPane getLeftAgeSlider() {
        return leftAgeSlider;
    }
}
