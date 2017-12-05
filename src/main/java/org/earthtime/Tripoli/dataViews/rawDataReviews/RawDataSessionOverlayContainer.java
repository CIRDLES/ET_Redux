/*
 * Copyright 2006-2018 James F. Bowring, CIRDLES.org, and Earth-Time.org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.earthtime.Tripoli.dataViews.rawDataReviews;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import org.earthtime.Tripoli.dataModels.DataModelInterface;
import org.earthtime.Tripoli.dataModels.RawIntensityDataModel;
import org.earthtime.Tripoli.dataViews.AbstractRawDataView;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public class RawDataSessionOverlayContainer extends AbstractRawDataView {

    private List<AbstractRawDataView> rawIntensitiesDataOverlayViews;
    private AbstractRawDataView firstRawIntensitiesDataOverlayView;
    private List<AbstractRawDataView> rawIntensitiesDataSerialViews;
    private int timeZeroRelativeIndex;
    private int currentX;
    private int peakLeftShade;
    private int peakWidth;
    private boolean insidePeak;
    private boolean onLeftOfPeak;
    private boolean onRightOfPeak;
    private int backgroundRightShade;
    private int backgroundWidth;
    private boolean insideBackground;
    private boolean onLeftOfBackground;
    private boolean onRightOfBackground;
    private final AbstractRawDataView rawDataSessionSerialContainer;

    public RawDataSessionOverlayContainer(Rectangle bounds, AbstractRawDataView rawDataSessionSerialContainer) {
        super(bounds);
        this.rawDataSessionSerialContainer = rawDataSessionSerialContainer;
        initSession();
    }

    private void initSession() {
        setBackground(new Color(255, 255, 255));
        setOpaque(true);
        setPreferredSize(getSize());
        addMeAsMouseListener();
    }

    /**
     *
     * @param g2d
     */
    @Override
    public void paint(Graphics2D g2d) {
        super.paint(g2d);

        BufferedImage bufferedImage
                = new BufferedImage(5, 5, BufferedImage.TYPE_INT_RGB);
        Graphics2D big = bufferedImage.createGraphics();
        Rectangle r = new Rectangle(0, 0, 5, 5);

        // mark peak
        Rectangle2D peakZone = new Rectangle2D.Double(//
                mapX(myOnPeakNormalizedAquireTimes[timeZeroRelativeIndex + peakLeftShade]), //
                mapY(maxY),//
                mapX(peakWidth),//
                Math.abs(mapY(maxY) - mapY(minY)));
        big.setColor(new Color(241, 255, 240));//pale green
        big.fillRect(0, 0, 5, 5);
        big.setColor(Color.lightGray);
        big.fillOval(0, 0, 5, 5);
        g2d.setPaint(new TexturePaint(bufferedImage, r));
        g2d.fill(peakZone);

        g2d.setPaint(Color.black);

        g2d.setStroke(new BasicStroke(0.75f));

        g2d.draw(new Line2D.Double(//
                mapX(myOnPeakNormalizedAquireTimes[timeZeroRelativeIndex + peakLeftShade]), //
                mapY(minY),//
                mapX(myOnPeakNormalizedAquireTimes[timeZeroRelativeIndex + peakLeftShade]),//
                mapY(maxY)));

        g2d.draw(new Line2D.Double(//
                mapX(myOnPeakNormalizedAquireTimes[timeZeroRelativeIndex + peakLeftShade + peakWidth]), //
                mapY(minY),//
                mapX(myOnPeakNormalizedAquireTimes[timeZeroRelativeIndex + peakLeftShade + peakWidth]),//
                mapY(maxY)));

        // mark background
        Rectangle2D backgroundZone = new Rectangle2D.Double(//
                mapX(myOnPeakNormalizedAquireTimes[timeZeroRelativeIndex - backgroundRightShade - backgroundWidth]), //
                mapY(maxY),//
                mapX(backgroundWidth),//
                Math.abs(mapY(maxY) - mapY(minY)));
        big.setColor(new Color(253, 253, 233));//pale yellow
        big.fillRect(0, 0, 5, 5);
        big.setColor(Color.lightGray);
        big.fillOval(0, 0, 5, 5);
        g2d.setPaint(new TexturePaint(bufferedImage, r));
        g2d.fill(backgroundZone);

        g2d.setPaint(Color.black);

        g2d.setStroke(new BasicStroke(0.75f));

        g2d.draw(new Line2D.Double(//
                mapX(myOnPeakNormalizedAquireTimes[timeZeroRelativeIndex - backgroundRightShade - backgroundWidth]), //
                mapY(minY),//
                mapX(myOnPeakNormalizedAquireTimes[timeZeroRelativeIndex - backgroundRightShade - backgroundWidth]),//
                mapY(maxY)));

        g2d.draw(new Line2D.Double(//
                mapX(myOnPeakNormalizedAquireTimes[timeZeroRelativeIndex - backgroundRightShade]), //
                mapY(minY),//
                mapX(myOnPeakNormalizedAquireTimes[timeZeroRelativeIndex - backgroundRightShade]),//
                mapY(maxY)));

        // mark time zero
        g2d.setPaint(Color.red);
        g2d.draw(new Line2D.Double(//
                mapX(myOnPeakNormalizedAquireTimes[timeZeroRelativeIndex]), //
                mapY(minY),//
                mapX(myOnPeakNormalizedAquireTimes[timeZeroRelativeIndex]),//
                mapY(maxY)));
        g2d.setPaint(Color.black);
    }

    /**
     *
     * @param doReScale the value of doReScale
     * @param inLiveMode the value of inLiveMode
     */
    @Override
    public void preparePanel(boolean doReScale, boolean inLiveMode) {
        //this.removeAll();
        setDisplayOffsetY(0.0);
        setDisplayOffsetX(0.0);

        rawIntensitiesDataOverlayViews = new ArrayList<>();
        Component[] components = getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] instanceof RawDataSessionPlot) {
                rawIntensitiesDataOverlayViews.add(((AbstractRawDataView) components[i]));
            }
        }

        firstRawIntensitiesDataOverlayView = rawIntensitiesDataOverlayViews.get(0);
        myOnPeakNormalizedAquireTimes = firstRawIntensitiesDataOverlayView.getDataModel().getNormalizedOnPeakAquireTimes();
        peakLeftShade = ((RawIntensityDataModel) firstRawIntensitiesDataOverlayView.getDataModel()).getPeakLeftShade();
        peakWidth = ((RawIntensityDataModel) firstRawIntensitiesDataOverlayView.getDataModel()).getPeakWidth();
        backgroundRightShade = ((RawIntensityDataModel) firstRawIntensitiesDataOverlayView.getDataModel()).getBackgroundRightShade();
        backgroundWidth = ((RawIntensityDataModel) firstRawIntensitiesDataOverlayView.getDataModel()).getBackgroundWidth();
        timeZeroRelativeIndex = ((RawIntensityDataModel) firstRawIntensitiesDataOverlayView.getDataModel()).getTimeZeroRelativeIndex();

        minX = firstRawIntensitiesDataOverlayView.getMinX();
        maxX = firstRawIntensitiesDataOverlayView.getMaxX();
        minY = firstRawIntensitiesDataOverlayView.getMinY();
        maxY = firstRawIntensitiesDataOverlayView.getMaxY();

        //get a handle on serial view models
        rawIntensitiesDataSerialViews = new ArrayList<>();
        components = rawDataSessionSerialContainer.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] instanceof RawDataSessionPlot) {
                rawIntensitiesDataSerialViews.add(((AbstractRawDataView) components[i]));
            }
        }

    }

    /**
     *
     * @param evt
     */
    @Override
    public void mousePressed(MouseEvent evt) {
    }

    @Override
    public void mouseReleased(MouseEvent evt) {
    }

    @Override
    public void mouseMoved(MouseEvent evt) {
        insidePeak = false;
        onLeftOfPeak = false;
        onRightOfPeak = false;
        insideBackground = false;
        onLeftOfBackground = false;
        onRightOfBackground = false;

        if (confirmMouseInOnPeakZone(evt.getX())) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            insidePeak = true;
        } else if (confirmMouseLeftPeakZone(evt.getX())) {
            setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
            onLeftOfPeak = true;
        } else if (confirmMouseRightPeakZone(evt.getX())) {
            setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
            onRightOfPeak = true;

        } else if (confirmMouseInBackgroundZone(evt.getX())) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            insideBackground = true;
        } else if (confirmMouseLeftBackgroundZone(evt.getX())) {
            setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
            onLeftOfBackground = true;
        } else if (confirmMouseRightBackgroundZone(evt.getX())) {
            setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
            onRightOfBackground = true;
        } else {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    @Override
    public void mouseDragged(MouseEvent evt) {
        if (insidePeak) {
            int deltaX = convertMouseXToValue(evt.getX()) - currentX;
            if (deltaX < -1 * peakLeftShade) {
                deltaX = -1 * peakLeftShade;
            } else if ((timeZeroRelativeIndex + peakLeftShade + peakWidth + deltaX) > (0.95 * maxX)) {
                deltaX = (int) (0.95 * maxX) - (timeZeroRelativeIndex + peakLeftShade + peakWidth);
            }
            currentX += deltaX;
            peakLeftShade += deltaX;
            updateModels();

            repaint();
        }

        if (onLeftOfPeak) {
            int deltaX = convertMouseXToValue(evt.getX()) - currentX;
            if (deltaX < -1 * peakLeftShade) {
                deltaX = -1 * peakLeftShade;
            } else {
                if (deltaX > (peakWidth - 10)) {
                    deltaX = peakWidth - 10;
                }
            }
            currentX += deltaX;
            peakLeftShade += deltaX;
            peakWidth -= deltaX;
            updateModels();

            repaint();
        }

        if (onRightOfPeak) {
            int deltaX = convertMouseXToValue(evt.getX()) - currentX;
            if ((timeZeroRelativeIndex + peakLeftShade + peakWidth + deltaX) > (0.95 * maxX)) {
                deltaX = (int) ((0.95 * maxX)) - (timeZeroRelativeIndex + peakLeftShade + peakWidth);
            } else {
                if ((deltaX < 0) && (Math.abs(deltaX) > (peakWidth - 10))) {
                    deltaX = -(peakWidth - 10);
                }
            }

            currentX += deltaX;
            peakWidth += deltaX;
            updateModels();

            repaint();
        }

        if (insideBackground) {
            int deltaX = convertMouseXToValue(evt.getX()) - currentX;
            if (deltaX > backgroundRightShade) {
                deltaX = backgroundRightShade;
            } else {
                if ((timeZeroRelativeIndex - backgroundRightShade - backgroundWidth + deltaX) < (1.05 * minX)) {
                    deltaX = (int) (1.05 * minX) - (timeZeroRelativeIndex - backgroundRightShade - backgroundWidth);
                }
            }
            currentX += deltaX;
            backgroundRightShade -= deltaX;
            updateModels();

            repaint();
        }

        if (onLeftOfBackground) {
            int deltaX = convertMouseXToValue(evt.getX()) - currentX;
            if ((timeZeroRelativeIndex - backgroundRightShade - backgroundWidth + deltaX) < (1.05 * minX)) {
                deltaX = (int) (1.05 * minX) - (timeZeroRelativeIndex - backgroundRightShade - backgroundWidth);
            } else {
                if (deltaX > (backgroundWidth - 10)) {
                    deltaX = backgroundWidth - 10;
                }
            }
            currentX += deltaX;
            backgroundWidth -= deltaX;
            updateModels();

            repaint();
        }

        if (onRightOfBackground) {
            int deltaX = convertMouseXToValue(evt.getX()) - currentX;
            if (deltaX > backgroundRightShade) {
                deltaX = backgroundRightShade;
            } else {
                if (deltaX > (backgroundWidth - 10)) {
                    deltaX = backgroundWidth - 10;
                }
            }
            currentX += deltaX;
            backgroundRightShade -= deltaX;
            backgroundWidth += deltaX;
            updateModels();

            repaint();
        }
    }

    private void updateModels() {
        for (int i = 0; i < rawIntensitiesDataOverlayViews.size(); i++) {
            ((RawIntensityDataModel) rawIntensitiesDataOverlayViews.get(i).getDataModel()).setPeakLeftShade(peakLeftShade);
            ((RawIntensityDataModel) rawIntensitiesDataOverlayViews.get(i).getDataModel()).setPeakWidth(peakWidth);
            ((RawIntensityDataModel) rawIntensitiesDataOverlayViews.get(i).getDataModel()).setBackgroundRightShade(backgroundRightShade);
            ((RawIntensityDataModel) rawIntensitiesDataOverlayViews.get(i).getDataModel()).setBackgroundWidth(backgroundWidth);

            rawIntensitiesDataOverlayViews.get(i).preparePanel(true, false);
            rawIntensitiesDataSerialViews.get(i).preparePanel(true, false);
            rawIntensitiesDataSerialViews.get(i).repaint();
        }

//        rawDataSessionSerialContainer.revalidate();
        rawDataSessionSerialContainer.refreshPanel(true, false);

    }

    private boolean confirmMouseInOnPeakZone(int x) {
        boolean retVal = false;

        currentX = convertMouseXToValue(x);
        retVal = ((currentX > (timeZeroRelativeIndex + peakLeftShade))//
                && (currentX < (timeZeroRelativeIndex + peakLeftShade + peakWidth)));

        return retVal;
    }

    private boolean confirmMouseLeftPeakZone(int x) {
        boolean retVal = false;

        currentX = convertMouseXToValue(x);
        retVal = (currentX >= (timeZeroRelativeIndex + peakLeftShade - 2))//
                && (currentX <= (timeZeroRelativeIndex + peakLeftShade + 2));

        return retVal;
    }

    private boolean confirmMouseRightPeakZone(int x) {
        boolean retVal = false;

        currentX = convertMouseXToValue(x);
        retVal = (currentX <= (timeZeroRelativeIndex + peakLeftShade + peakWidth + 2))//
                && (currentX >= (timeZeroRelativeIndex + peakLeftShade + peakWidth - 2));

        return retVal;
    }

    private boolean confirmMouseInBackgroundZone(int x) {
        boolean retVal = false;

        currentX = convertMouseXToValue(x);
        retVal = ((currentX > (timeZeroRelativeIndex - backgroundRightShade - backgroundWidth))//
                && (currentX < (timeZeroRelativeIndex - backgroundRightShade)));

        return retVal;
    }

    private boolean confirmMouseLeftBackgroundZone(int x) {
        boolean retVal = false;

        currentX = convertMouseXToValue(x);
        retVal = (currentX >= (timeZeroRelativeIndex - backgroundRightShade - backgroundWidth - 2))//
                && (currentX <= (timeZeroRelativeIndex - backgroundRightShade - backgroundWidth + 2));

        return retVal;
    }

    private boolean confirmMouseRightBackgroundZone(int x) {
        boolean retVal = false;

        currentX = convertMouseXToValue(x);
        retVal = (currentX >= (timeZeroRelativeIndex - backgroundRightShade - 2))//
                && (currentX <= (timeZeroRelativeIndex - backgroundRightShade + 2));

        return retVal;
    }

    @Override
    public DataModelInterface getDataModel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
