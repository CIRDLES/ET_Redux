/*
 * DataPresentationModeChooserPanel.java
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
package org.earthtime.Tripoli.dataViews.simpleViews;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import javax.swing.JRadioButton;
import org.earthtime.Tripoli.dataModels.DataModelInterface;
import org.earthtime.Tripoli.dataViews.AbstractRawDataView;
import org.earthtime.Tripoli.dataViews.overlayViews.TripoliSessionRawDataView;
import org.earthtime.beans.ET_JButton;
import org.earthtime.dataDictionaries.DataPresentationModeEnum;

/**
 *
 * @author James F. Bowring
 */
public class DataPresentationModeChooserPanel extends AbstractRawDataView {

    /**
     *
     */
    public static int DEFAULT_WIDTH_OF_PANE = 64;

    /**
     *
     */
    protected ButtonGroup dataViewModeButtonGroup;
    private JRadioButton ratiosChooser;
    private JRadioButton logRatiosChooser;
    private JRadioButton alphasChooser;

    /**
     *
     * @param sampleSessionDataView
     * @param dataPresentationMode
     * @param timeArray
     * @param bounds
     */
    public DataPresentationModeChooserPanel(//
            JLayeredPane sampleSessionDataView, DataPresentationModeEnum dataPresentationMode, double[] timeArray, Rectangle bounds) {

        super(bounds);

        this.sampleSessionDataView = sampleSessionDataView;

        this.dataPresentationMode = dataPresentationMode;

        this.myOnPeakNormalizedAquireTimes = timeArray;

        setOpaque(true);
        setBackground(new Color(250, 240, 230));
        setCursor(Cursor.getDefaultCursor());

        dataViewModeButtonGroup = new ButtonGroup();
    }

    @Override
    public void paint(Graphics2D g2d) {
        paintInit(g2d);

        setBackground(new Color(250, 240, 230));

        String label = "for ALL Ratios";
        TextLayout mLayout = //
                new TextLayout(
                        label, g2d.getFont(), g2d.getFontRenderContext());

        Rectangle2D bounds = mLayout.getBounds();

        g2d.drawString(label,//
                10,// (getWidth() - (float) (bounds.getWidth())) / 2f,//
                10);

    }

    public void setShowLogRatioButtonOnly() {
        ratiosChooser.setVisible(false);
        alphasChooser.setVisible(false);
    }

    public void setHideAlphaButton() {
        alphasChooser.setVisible(false);
        if (alphasChooser.isSelected()) {
            ratiosChooser.doClick();
        }
    }

    @Override
    public void preparePanel() {
        this.removeAll();

        // arbitrary
        minY = 0.0;
        maxY = 80.0;

        ratiosChooser = buttonForFitFunctionFactory(12, DataPresentationModeEnum.RATIO);
        logRatiosChooser = buttonForFitFunctionFactory(30, DataPresentationModeEnum.LOGRATIO);
        alphasChooser = buttonForFitFunctionFactory(48, DataPresentationModeEnum.ALPHA);
        add(ratiosChooser, DEFAULT_LAYER);
        add(logRatiosChooser, DEFAULT_LAYER);
        add(alphasChooser, DEFAULT_LAYER);
        // this means that this view has been primed for masking array use, i.e. standards and unknowns in fractionation corr ratio view
        if (myOnPeakNormalizedAquireTimes.length > 0) {
            add(shadeFactory(sampleSessionDataView), DEFAULT_LAYER);
            add(applyShadeButtonFactory());

            add(buttonForODChoiceFactory(12, "w/ OD", true), DEFAULT_LAYER);
            add(buttonForODChoiceFactory(32, "w/out OD", false), DEFAULT_LAYER);
        }
    }

    private JRadioButton buttonForFitFunctionFactory(//
            int pixelsFromTop, //
            final DataPresentationModeEnum myDataPresentationMode) {

        JRadioButton dataViewModeButton = new JRadioButton(myDataPresentationMode.getName());
        dataViewModeButton.setName(myDataPresentationMode.getName());
        dataViewModeButton.setFont(new Font("SansSerif", Font.PLAIN, 10));
        dataViewModeButton.setBounds(5, pixelsFromTop, 90, 20);
        dataViewModeButton.setSelected(myDataPresentationMode.equals(dataPresentationMode));
        dataViewModeButton.setBackground(this.getBackground());
        dataViewModeButton.setOpaque(true);

        dataViewModeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                ((AbstractRawDataView) sampleSessionDataView).setDataPresentationMode(myDataPresentationMode);
                ((AbstractRawDataView) sampleSessionDataView).refreshPanel();

            }
        });

        dataViewModeButtonGroup.add(dataViewModeButton);
        return dataViewModeButton;
    }

    private JLayeredPane shadeFactory(JLayeredPane sampleSessionDataView) {
        AbstractRawDataView maskingShadeControl = //
                new MaskingShadeControl(new Rectangle(15, 88, 170, 20), myOnPeakNormalizedAquireTimes, sampleSessionDataView);

        maskingShadeControl.preparePanel();

        return maskingShadeControl;
    }

    private JButton applyShadeButtonFactory() {
        JButton applyShadeButton = new ET_JButton("Refit all data to shades.");
        applyShadeButton.setBounds(15, 68, 170, 20);

        applyShadeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                ((TripoliSessionRawDataView) sampleSessionDataView).getTripoliSession().applyMaskingArray();
                ((TripoliSessionRawDataView) sampleSessionDataView).getTripoliSession().reFitAllFractions();

                // jan 2015 force refit after applying shade
                ((TripoliSessionRawDataView) sampleSessionDataView).getTripoliSession().calculateSessionFitFunctionsForPrimaryStandard();

                ((AbstractRawDataView) sampleSessionDataView).refreshPanel();

            }
        });

        return applyShadeButton;
    }

    private JButton buttonForODChoiceFactory(int pixelsFromTop, final String caption, final boolean setOD) {

        JButton ODChoiceButton = new ET_JButton(caption);
        ODChoiceButton.setFont(new Font("SansSerif", Font.PLAIN, 11));
        ODChoiceButton.setBounds(125, pixelsFromTop, 60, 20);
        ODChoiceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                ((TripoliSessionRawDataView) sampleSessionDataView).getTripoliSession().setODforAllFractionsAllRatios(setOD);

                ((AbstractRawDataView) sampleSessionDataView).refreshPanel();
            }
        });

        return ODChoiceButton;
    }

    @Override
    public DataModelInterface getDataModel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void mouseClicked(MouseEvent e) {
//        super.mouseClicked( e );
    }

    @Override
    public void mouseDragged(MouseEvent evt) {
//        super.mouseDragged( evt );
    }

    @Override
    public void mouseEntered(MouseEvent e) {
//        super.mouseEntered( e );
    }

    @Override
    public void mouseExited(MouseEvent e) {
//        super.mouseExited( e );
    }

    @Override
    public void mouseMoved(MouseEvent e) {
//        super.mouseMoved( e );
    }

    @Override
    public void mousePressed(MouseEvent evt) {
//        super.mousePressed( evt );
    }

    @Override
    public void mouseReleased(MouseEvent e) {
//        super.mouseReleased( e );
    }

//    /**
//     * @param maskingMinX the maskingMinX to set
//     */
//    public void setMaskingMinX ( double maskingMinX ) {
//        this.maskingMinX = maskingMinX;
//    }
//
//    /**
//     * @param maskingMaxX the maskingMaxX to set
//     */
//    public void setMaskingMaxX ( double maskingMaxX ) {
//        this.maskingMaxX = maskingMaxX;
//    }
}
