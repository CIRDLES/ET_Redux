/*
 * Copyright 2006-2016 CIRDLES.org.
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
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLayeredPane;
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
    private int peakLeftShade;
    private int peakWidth;
    private int timeZeroRelativeIndex;
    private int currentX;
    private boolean insidePeak;
    private boolean onLeftOfPeak;
    private boolean onRightOfPeak;
    private final JLayeredPane rawDataSerialSession;

    public RawDataSessionOverlayContainer(Rectangle bounds, JLayeredPane rawDataSerialSession) {
        super(bounds);
        this.rawDataSerialSession = rawDataSerialSession;
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

        g2d.setPaint(new Color(241, 255, 240)); //pale green
        g2d.setStroke(new BasicStroke(1.0f));

        g2d.fill(new Rectangle2D.Double(//
                mapX(myOnPeakNormalizedAquireTimes[timeZeroRelativeIndex + peakLeftShade]), //
                mapY(maxY),//
                mapX(peakWidth),//
                Math.abs(mapY(maxY) - mapY(minY))));
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
        timeZeroRelativeIndex = ((RawIntensityDataModel) firstRawIntensitiesDataOverlayView.getDataModel()).getTimeZeroRelativeIndex();

        minX = firstRawIntensitiesDataOverlayView.getMinX();
        maxX = firstRawIntensitiesDataOverlayView.getMaxX();
        minY = firstRawIntensitiesDataOverlayView.getMinY();
        maxY = firstRawIntensitiesDataOverlayView.getMaxY();

        //get a handle on serial view models
        rawIntensitiesDataSerialViews = new ArrayList<>();
        components = rawDataSerialSession.getComponents();
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
        if (confirmMouseInOnPeakZone(evt.getX())) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            insidePeak = true;
        } else if (confirmMouseLeftPeakZone(evt.getX())) {
            setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
            onLeftOfPeak = true;
        } else if (confirmMouseRightPeakZone(evt.getX())) {
            setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
            onRightOfPeak = true;
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
            }
            if ((timeZeroRelativeIndex + peakLeftShade + peakWidth + deltaX) > mapX(0.95 * maxX)) {
                deltaX = (int) mapX(0.95 * maxX) - (timeZeroRelativeIndex + peakLeftShade + peakWidth);
            }
            peakLeftShade += deltaX;
            updateModels();

            currentX += deltaX;
            repaint();
        }

        if (onLeftOfPeak) {
            int deltaX = convertMouseXToValue(evt.getX()) - currentX;
            if (deltaX < -1 * peakLeftShade) {
                deltaX = -1 * peakLeftShade;
            }
            if (deltaX > (peakWidth - 10)) {
                deltaX = peakWidth - 10;
            }
            peakLeftShade += deltaX;
            peakWidth -= deltaX;
            updateModels();

            currentX += deltaX;
            repaint();
        }

        if (onRightOfPeak) {
            int deltaX = convertMouseXToValue(evt.getX()) - currentX;
            if ((timeZeroRelativeIndex + peakLeftShade + peakWidth + deltaX) > mapX(0.95 * maxX)) {
                deltaX = (int) mapX(0.95 * maxX) - (timeZeroRelativeIndex + peakLeftShade + peakWidth);
            }
            if (Math.abs(deltaX) > (peakWidth - 10)) {
                deltaX = -(peakWidth - 10);
            }

            peakWidth += deltaX;
            updateModels();

            currentX += deltaX;
            repaint();
        }
    }

    private void updateModels() {
        for (int i = 0; i < rawIntensitiesDataOverlayViews.size(); i++) {
            ((RawIntensityDataModel) rawIntensitiesDataOverlayViews.get(i).getDataModel()).setPeakLeftShade(peakLeftShade);
            ((RawIntensityDataModel) rawIntensitiesDataOverlayViews.get(i).getDataModel()).setPeakWidth(peakWidth);
            rawIntensitiesDataOverlayViews.get(i).preparePanel(true, false);
        }

        for (int i = 0; i < rawIntensitiesDataSerialViews.size(); i++) {
            ((RawIntensityDataModel) rawIntensitiesDataSerialViews.get(i).getDataModel()).setPeakLeftShade(peakLeftShade);
            ((RawIntensityDataModel) rawIntensitiesDataSerialViews.get(i).getDataModel()).setPeakWidth(peakWidth);
            rawIntensitiesDataSerialViews.get(i).preparePanel(true, false);
            rawIntensitiesDataSerialViews.get(i).repaint();
        }

        rawDataSerialSession.revalidate();

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
        retVal = (currentX == (timeZeroRelativeIndex + peakLeftShade));

        return retVal;
    }

    private boolean confirmMouseRightPeakZone(int x) {
        boolean retVal = false;

        currentX = convertMouseXToValue(x);
        retVal = (currentX == (timeZeroRelativeIndex + peakLeftShade + peakWidth));

        return retVal;
    }

    @Override
    public DataModelInterface getDataModel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
