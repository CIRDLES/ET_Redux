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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import org.earthtime.Tripoli.dataModels.DataModelInterface;
import org.earthtime.Tripoli.dataModels.RawIntensityDataModel;
import org.earthtime.Tripoli.dataViews.AbstractRawDataView;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public class RawDataSessionSerialContainer extends AbstractRawDataView {

    private List<AbstractRawDataView> rawIntensitiesDataSerialViews;
    private AbstractRawDataView firstRawIntensitiesDataSerialView;
    List<Integer> sessionTimeZeroIndices;
    private int timeZeroRelativeIndex;
    private int peakLeftShade;
    private int peakWidth;
    private int backgroundRightShade;
    private int backgroundWidth;
    private transient boolean initialized;
    private JLabel[] fractionNameLabels;
    private String[] fractionNames;
    private JCheckBox[] fractionIgnores;

    /**
     *
     * @param bounds the value of bounds
     * @param fractionNames the value of fractionNames
     */
    public RawDataSessionSerialContainer(Rectangle bounds, String[] fractionNames) {
        super(bounds);
        initialized = false;
        this.fractionNames = fractionNames;
    }

    /**
     *
     * @param g2d
     */
    @Override
    public void paint(Graphics2D g2d) {
        super.paint(g2d);

//        g2d.setPaint(new Color(235, 255, 255));
//        g2d.setStroke(new BasicStroke(1.f));
//
//        for (int i = 0; i < sessionTimeZeroIndices.size(); i++) {
//            Rectangle2D fractionZOne = new Rectangle2D.Double(//
//                    mapX(myOnPeakNormalizedAquireTimes[sessionTimeZeroIndices.get(i)] - backgroundRightShade - backgroundWidth), //
//                    0,//
//                    mapX(backgroundRightShade + backgroundWidth + peakLeftShade + peakWidth),//
//                    25);
//
//            g2d.fill(fractionZOne);
//        }
    }

    public void initPanel() {
        setDisplayOffsetY(0.0);
        setDisplayOffsetX(0.0);

        //get a handle on serial view models
        rawIntensitiesDataSerialViews = new ArrayList<>();
        Component[] components = getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] instanceof RawDataSessionPlot) {
                rawIntensitiesDataSerialViews.add(((AbstractRawDataView) components[i]));
            }
        }

        firstRawIntensitiesDataSerialView = rawIntensitiesDataSerialViews.get(0);
        sessionTimeZeroIndices = ((RawIntensityDataModel) firstRawIntensitiesDataSerialView.getDataModel()).getSessionTimeZeroIndices();
        myOnPeakNormalizedAquireTimes = firstRawIntensitiesDataSerialView.getDataModel().getNormalizedOnPeakAquireTimes();
        peakLeftShade = ((RawIntensityDataModel) firstRawIntensitiesDataSerialView.getDataModel()).getPeakLeftShade();
        peakWidth = ((RawIntensityDataModel) firstRawIntensitiesDataSerialView.getDataModel()).getPeakWidth();
        backgroundRightShade = ((RawIntensityDataModel) firstRawIntensitiesDataSerialView.getDataModel()).getBackgroundRightShade();
        backgroundWidth = ((RawIntensityDataModel) firstRawIntensitiesDataSerialView.getDataModel()).getBackgroundWidth();
        timeZeroRelativeIndex = ((RawIntensityDataModel) firstRawIntensitiesDataSerialView.getDataModel()).getTimeZeroRelativeIndex();

        minX = firstRawIntensitiesDataSerialView.getMinX();
        maxX = firstRawIntensitiesDataSerialView.getMaxX();
        minY = firstRawIntensitiesDataSerialView.getMinY();
        maxY = firstRawIntensitiesDataSerialView.getMaxY();

        fractionNameLabels = new JLabel[sessionTimeZeroIndices.size()];
        for (int i = 0; i < sessionTimeZeroIndices.size(); i++) {
            fractionNameLabels[i] = new JLabel();
            fractionNameLabels[i].setBounds(//
                    (int) mapX(myOnPeakNormalizedAquireTimes[sessionTimeZeroIndices.get(i) - backgroundWidth - backgroundRightShade]),//
                    1, (int) mapX(backgroundWidth + backgroundRightShade + peakLeftShade + peakWidth), 20);
            fractionNameLabels[i].setHorizontalAlignment(JLabel.CENTER);
            fractionNameLabels[i].setOpaque(true);
            fractionNameLabels[i].setBackground(new Color(235, 255, 255));
            fractionNameLabels[i].setBorder(javax.swing.BorderFactory.createLineBorder(Color.black));
            add(fractionNameLabels[i], DEFAULT_LAYER);
        }

        renameFractions();

        fractionIgnores = new JCheckBox[sessionTimeZeroIndices.size()];
        for (int i = 0; i < sessionTimeZeroIndices.size(); i++) {
            fractionIgnores[i] = new JCheckBox("Ignore");
            fractionIgnores[i].setBounds(//
                    (int) mapX(myOnPeakNormalizedAquireTimes[sessionTimeZeroIndices.get(i) - backgroundWidth - backgroundRightShade]),//
                    (int) mapY(minY) - 25, (int) mapX(backgroundWidth + backgroundRightShade + peakLeftShade + peakWidth), 20);
            fractionIgnores[i].setHorizontalAlignment(JLabel.CENTER);
            fractionIgnores[i].setOpaque(true);
            fractionIgnores[i].setBackground(new Color(235, 255, 255));
            fractionIgnores[i].setBorder(javax.swing.BorderFactory.createLineBorder(Color.black));
            fractionIgnores[i].addItemListener(new fractionIgnoresListener(i));
            add(fractionIgnores[i], DEFAULT_LAYER);
        }

        initialized = true;
    }

    private void renameFractions() {
        int countIgnored = 0;
        for (int i = 0; i < sessionTimeZeroIndices.size(); i++) {
            if (sessionTimeZeroIndices.get(i) < 0) {
                countIgnored++;
                fractionNameLabels[i].setText("X " + i);
            } else {
                if ((i - countIgnored) < fractionNames.length) {
                    fractionNameLabels[i].setText(fractionNames[i - countIgnored]);
                } else {
                    fractionNameLabels[i].setText("none " + i);
                }
            }
        }
    }

    private class fractionIgnoresListener implements ItemListener {

        private int i;

        public fractionIgnoresListener(int i) {
            this.i = i;
        }

        @Override
        public void itemStateChanged(ItemEvent e) {
            sessionTimeZeroIndices.set(i, Math.abs(sessionTimeZeroIndices.get(i)) * (int) (((AbstractButton) e.getSource()).isSelected() ? -1 : 1));
            renameFractions();
            repaint();
        }

    }

    /**
     *
     * @param doReScale the value of doReScale
     * @param inLiveMode the value of inLiveMode
     */
    @Override
    public void preparePanel(boolean doReScale, boolean inLiveMode) {

        if (!initialized) {
            initPanel();
        }

        peakLeftShade = ((RawIntensityDataModel) firstRawIntensitiesDataSerialView.getDataModel()).getPeakLeftShade();
        peakWidth = ((RawIntensityDataModel) firstRawIntensitiesDataSerialView.getDataModel()).getPeakWidth();
        backgroundRightShade = ((RawIntensityDataModel) firstRawIntensitiesDataSerialView.getDataModel()).getBackgroundRightShade();
        backgroundWidth = ((RawIntensityDataModel) firstRawIntensitiesDataSerialView.getDataModel()).getBackgroundWidth();

        // relocate fractionLabels and fraction ignores
        for (int i = 0; i < fractionNameLabels.length; i++) {
            fractionNameLabels[i].setBounds(//
                    (int) mapX(myOnPeakNormalizedAquireTimes[sessionTimeZeroIndices.get(i) - backgroundWidth - backgroundRightShade]),//
                    1, (int) mapX(backgroundWidth + backgroundRightShade + peakLeftShade + peakWidth), 20);
            fractionNameLabels[i].revalidate();

            fractionIgnores[i].setBounds(//
                    (int) mapX(myOnPeakNormalizedAquireTimes[sessionTimeZeroIndices.get(i) - backgroundWidth - backgroundRightShade]),//
                    (int) mapY(minY) - 25, (int) mapX(backgroundWidth + backgroundRightShade + peakLeftShade + peakWidth), 20);
            fractionIgnores[i].revalidate();
        }

    }

    @Override
    public DataModelInterface getDataModel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
