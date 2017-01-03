/*
 * YAxisView.java
 *
 * Created Jul 6, 2011
 *
 * Copyright 2006-2017 James F. Bowring and www.Earth-Time.org
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
package org.earthtime.Tripoli.dataViews.simpleViews;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.font.TextLayout;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JLayeredPane;
import org.earthtime.Tripoli.dataModels.DataModelInterface;
import org.earthtime.Tripoli.dataViews.AbstractRawDataView;
import org.earthtime.utilities.TicGeneratorForAxes;

/**
 *
 * @author James F. Bowring
 */
public class YAxisView extends AbstractRawDataView {

    private final AbstractRawDataView[] rawDataModelViews;
    private AbstractRawDataView dataViewsOverlay;
    private final Color backgroundColor;
    private int yOnMousePress;
    private boolean zoom = false;
    private boolean panning = false;
    private boolean floating = false;

    /**
     *
     *
     * @param rawDataModelViews
     * @param sampleSessionDataView
     * @param backgroundColor
     * @param minY
     * @param maxY
     * @param bounds
     * @param floating
     * @param forStandards the value of forStandards
     */
    public YAxisView(//
            AbstractRawDataView[] rawDataModelViews, //
            JLayeredPane sampleSessionDataView, //
            Color backgroundColor, //
            double minY, //
            double maxY, //
            Rectangle bounds, //
            boolean floating, //
            boolean forStandards) {
        super(bounds);

        this.rawDataModelViews = rawDataModelViews;
        this.dataViewsOverlay = null;
        this.sampleSessionDataView = sampleSessionDataView;
        this.backgroundColor = backgroundColor;
        this.yOnMousePress = 0;
        this.minY = minY;
        this.maxY = maxY;
        this.floating = floating;
        this.forStandards = forStandards;

        // horizontal layout arbitrarily fixed
        this.minX = 0;
        this.maxX = bounds.getWidth();//100;

        setOpaque(true);
        setBackground(Color.white);

        setBounds(bounds);
        graphWidth = bounds.width - leftMargin;
        graphHeight = bounds.height - topMargin;

        addMeAsMouseListener();
        addMeAsMouseMotionListener();

        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    private void addMeAsMouseMotionListener() {

        addMouseMotionListener(this);
    }

    /**
     *
     * @param g2d
     */
    @Override
    public void paint(Graphics2D g2d) {

        paintInit(g2d);

        setBackground(backgroundColor);
        // paint axis and tics
        Shape yAxis = new Line2D.Double( //
                mapX(maxX - 1), mapY(minY), mapX(maxX - 1), mapY(maxY));
        g2d.setPaint(Color.black);
        g2d.draw(yAxis);

        Shape zoomingArea = new Rectangle2D.Double(//
                mapX(maxX - 19),//
                -1,//
                mapX(maxX - 1) - mapX(maxX - 19),//
                getHeight() + 1);
        g2d.draw(zoomingArea);

        // tics
        // tics only appear for standards as unknowns do not have a common axis
        if (tics != null) {
            g2d.setPaint(Color.BLACK);
            for (int i = 0; i < tics.length; i++) {
                try {
                    Shape ticMark = new Line2D.Double( //
                            mapX(maxX - 19), mapY(getTics()[i].doubleValue()), mapX(maxX - 1), mapY(getTics()[i].doubleValue()));
                    g2d.draw(ticMark);

                    TextLayout mLayout
                            = //
                            new TextLayout(
                                    getTics()[i].toPlainString(), g2d.getFont(), g2d.getFontRenderContext());

                    Rectangle2D bounds = mLayout.getBounds();

                    g2d.drawString(getTics()[i].toPlainString(),//
                            (float) mapX(maxX - 21) - (float) bounds.getWidth(),//
                            (float) mapY(getTics()[i].doubleValue()) + 2.9f);
                } catch (Exception e) {
                }
            }
        }

        if (!floating) {
            // paint display name
            g2d.setPaint(Color.black);
            g2d.setFont(new Font("SansSerif", Font.BOLD, 12));

            TextLayout mLayout
                    = //
                    new TextLayout(
                            rawDataModelViews[0].getDataModel().getDataModelName(), g2d.getFont(), g2d.getFontRenderContext());

            Rectangle2D bounds = mLayout.getBounds();

            int drawX = 15;
            int drawY = (int) (getHeight() / 2 + bounds.getWidth() / 2);

            g2d.rotate(-Math.PI / 2.0, drawX, drawY);
            g2d.drawString(rawDataModelViews[0].getDataModel().getDataModelName(), drawX, drawY);
            g2d.rotate(Math.PI / 2.0, drawX, drawY);
        }

    }

    /**
     *
     * @param doReScale the value of doReScale
     * @param inLiveMode the value of inLiveMode
     */
    @Override
    public void preparePanel(boolean doReScale, boolean inLiveMode) {

        this.removeAll();

        setDisplayOffsetY(0.0);
        setDisplayOffsetX(0.0);

        //prepareAxes();
    }

    /**
     *
     * @param evt
     */
    @Override
    public void mousePressed(MouseEvent evt) {
        yOnMousePress = evt.getY();
    }

    /**
     *
     * @param evt
     */
    @Override
    public void mouseReleased(MouseEvent evt) {
        zoom = false;
        panning = false;
    }

    /**
     *
     * @param e
     */
    @Override
    public void mouseExited(MouseEvent e) {
        zoom = false;
        panning = false;
    }

    /**
     *
     * @param evt
     */
    @Override
    public void mouseDragged(MouseEvent evt) {
        //  System.out.println( "MOUSE Dragged: " + convertMouseYToValue( evt.getY() ) + " from " + evt.getY() + " X " + evt.getX());
        int yOnMouseDragged = evt.getY();
        int xOnMouseDragged = evt.getX();

        double yDelta = Math.abs((convertMouseYToValue(yOnMouseDragged) - convertMouseYToValue(yOnMousePress)));

        int currentX = convertMouseXToValue(xOnMouseDragged);
        zoom = (currentX > maxX - 19) & (currentX < maxX);
        panning = (currentX < maxX - 19) & (currentX > maxX / 3);//oct 2012 minX);

        if (zoom) {
            if (yOnMouseDragged < yOnMousePress) {
                //System.out.println( "Mouse NORTH" ); zoomin
                maxY -= yDelta;
                minY += yDelta;
            }

            if (yOnMouseDragged > yOnMousePress) {
//            System.out.println( "Mouse SOUTH" );zoomout
                maxY += yDelta;
                minY -= yDelta;
            }

//            tics = TicGeneratorForAxes.generateTics( minY, maxY, (int) (rawDataModelViews[0].getHeight() / 20.0) );
        }

        if (panning) {
            // panning
            if (yOnMouseDragged < yOnMousePress) {
                //System.out.println( "Mouse NORTH" );
                maxY -= yDelta;
                minY -= yDelta;
            }

            if (yOnMouseDragged > yOnMousePress) {
//            System.out.println( "Mouse SOUTH" );
                maxY += yDelta;
                minY += yDelta;
            }
        }

        tics = TicGeneratorForAxes.generateTics(minY, maxY, (int) (rawDataModelViews[0].getHeight() / 20.0));
        for (int i = 0; i < rawDataModelViews.length; i++) {
            rawDataModelViews[i].setTics(tics);
        }

        yOnMousePress = yOnMouseDragged;
        repaint();

        // reset all fractions if not in dataViewsOverlay
        if (dataViewsOverlay == null) {
            for (int i = 0; i < rawDataModelViews.length; i++) {
                if (!floating) {
                    if (true || rawDataModelViews[i].getTripoliFraction().isStandard()) {
                        rawDataModelViews[i].setMaxY(maxY);
                        rawDataModelViews[i].setMinY(minY);
                    }
                } else {
                    rawDataModelViews[i].setMaxY(maxY);
                    rawDataModelViews[i].setMinY(minY);
                }
            }
        } else //reset DataViewsOverlay 
        {
            dataViewsOverlay.setMaxY(maxY);
            dataViewsOverlay.setMinY(minY);
        }
        sampleSessionDataView.repaint();
    }

    /**
     *
     * @return
     */
    @Override
    public DataModelInterface getDataModel() {
        throw new UnsupportedOperationException("Not a legitimate. call!");
    }

    /**
     * @param dataViewsOverlay the dataViewsOverlay to set
     */
    public void setDataViewsOverlay(AbstractRawDataView dataViewsOverlay) {
        this.dataViewsOverlay = dataViewsOverlay;
    }

    /**
     * @return the floating
     */
    public boolean isFloating() {
        return floating;
    }

    /**
     * @param floating the floating to set
     */
    public void setFloating(boolean floating) {
        this.floating = floating;
    }
}
