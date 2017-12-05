/*
 * AllFunctionsChoicePanel.java
 *
 * Created Oct 14, 2012
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
package org.earthtime.Tripoli.dataViews.fitFunctionPresentationViews;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLayeredPane;
import org.earthtime.Tripoli.dataModels.DataModelFitFunctionInterface;
import org.earthtime.Tripoli.dataModels.DataModelInterface;
import org.earthtime.Tripoli.dataModels.RawRatioDataModel;
import org.earthtime.Tripoli.dataViews.AbstractRawDataView;
import org.earthtime.Tripoli.dataViews.overlayViews.TripoliSessionRawDataView;
import org.earthtime.Tripoli.dataViews.simpleViews.FitFunctionDataInterface;
import org.earthtime.beans.ET_JButton;
import org.earthtime.dataDictionaries.FitFunctionTypeEnum;

/**
 *
 * @author James F. Bowring
 */
public class AllFunctionsChoicePanel extends AbstractRawDataView {

    /**
     *
     */
    public static int DEFAULT_WIDTH_OF_PANE = 64;

    /**
     *
     */
    protected ButtonGroup fitFunctionButtonGroup;
    private final AbstractRawDataView[] rawDataModelViews;
    private final boolean meanOnly;

    /**
     *
     * @param sampleSessionDataView
     * @param rawDataModelViews
     * @param bounds
     * @param forStandards
     * @param meanOnly the value of meanOnly
     */
    public AllFunctionsChoicePanel(//
            JLayeredPane sampleSessionDataView,//
            AbstractRawDataView[] rawDataModelViews, //
            Rectangle bounds, //
            boolean forStandards, //
            boolean meanOnly) {
        super(bounds);

        this.sampleSessionDataView = sampleSessionDataView;
        this.rawDataModelViews = rawDataModelViews;
        this.forStandards = forStandards;
        this.meanOnly = meanOnly;

        setOpaque(true);
        setCursor(Cursor.getDefaultCursor());

        fitFunctionButtonGroup = new ButtonGroup();
    }

    @Override
    public void paint(Graphics2D g2d) {
        paintInit(g2d);

        String label = "Apply Fit Function:";
        TextLayout mLayout
                = //
                new TextLayout(
                        label, g2d.getFont(), g2d.getFontRenderContext());

        Rectangle2D bounds = mLayout.getBounds();

        g2d.drawString(label,//
                (getWidth() - (float) (bounds.getWidth())) / 2f,//
                10);

    }

    @Override
    public void preparePanel(boolean doReScale, boolean inLiveMode) {
        this.removeAll();

        // arbitrary
        minY = 0.0;
        maxY = 80.0;

        if (meanOnly) {
            add(buttonForFitFunctionFactory(15, FitFunctionTypeEnum.MEAN_DH), DEFAULT_LAYER);
        } else {
            add(buttonForFitFunctionFactory(15, FitFunctionTypeEnum.MEAN), DEFAULT_LAYER);
            add(buttonForFitFunctionFactory(35, FitFunctionTypeEnum.LINE), DEFAULT_LAYER);
            add(buttonForFitFunctionFactory(55, FitFunctionTypeEnum.EXPONENTIAL), DEFAULT_LAYER);
        }
        add(buttonForODChoiceFactory(75, "w/ OverDispersion", true), DEFAULT_LAYER);
        add(buttonForODChoiceFactory(95, "w/o OverDispersion", false), DEFAULT_LAYER);

    }

    private JButton buttonForFitFunctionFactory(int pixelsFromTop, final FitFunctionTypeEnum fitFunctionType) {

        JButton functionChoiceButton = new ET_JButton(fitFunctionType.getPrettyName());
        functionChoiceButton.setName(fitFunctionType.getName());
        functionChoiceButton.setFont(new Font("SansSerif", Font.PLAIN, 11));
        functionChoiceButton.setMargin(new Insets(0, 0, 0, 0));
        functionChoiceButton.setBounds(5, pixelsFromTop, 110, 20);

        functionChoiceButton.addActionListener((ActionEvent e) -> {
            for (AbstractRawDataView rawDataModelView : rawDataModelViews) {
                DataModelFitFunctionInterface rawRatioDataModel1 = (DataModelFitFunctionInterface) rawDataModelView.getDataModel();
                if (rawRatioDataModel1.containsFitFunction(fitFunctionType)) {
                    rawRatioDataModel1.setSelectedFitFunctionType(fitFunctionType);
                }
                try {
                    rawDataModelView.updatePlotsWithChanges((FitFunctionDataInterface) rawDataModelView);
                } catch (Exception e2) {
                }
            }
            ((TripoliSessionRawDataView) sampleSessionDataView).getTripoliSession().setFitFunctionsUpToDate(false);
            // ((AbstractRawDataView) sampleSessionDataView).refreshPanel(true);

            for (int i = 0; i < rawDataModelViews.length; i++) {
                rawDataModelViews[i].refreshPanel(false, false);
            }
//            // be sure changes to unknowns go to data table
//            if (rawDataModelViews[0] instanceof FitFunctionsOnRatioDataView) {
//                if (((FitFunctionDataInterface) rawDataModelViews[0]).amShowingUnknownFraction()) {
//                    updateReportTable();
//                }
//            }

            updateReportTable();
        });

        fitFunctionButtonGroup.add(functionChoiceButton);
        return functionChoiceButton;
    }

    private JButton buttonForODChoiceFactory(int pixelsFromTop, final String caption, final boolean setOD) {

        JButton ODChoiceButton = new ET_JButton(caption);
        ODChoiceButton.setFont(new Font("SansSerif", Font.PLAIN, 10));
        ODChoiceButton.setBounds(5, pixelsFromTop, 110, 20);
        ODChoiceButton.setMargin(new Insets(0, 0, 0, 0));
        ODChoiceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                for (AbstractRawDataView rawDataModelView : rawDataModelViews) {
                    DataModelFitFunctionInterface rawRatioDataModel = (DataModelFitFunctionInterface) rawDataModelView.getDataModel();
                    if (meanOnly) {// case of downhole
                        ((RawRatioDataModel) rawRatioDataModel).setOverDispersionSelectedDownHole(setOD);
                    } else {
                        rawRatioDataModel.setOverDispersionSelected(setOD);
                    }
//                    try {
//                        ((FitFunctionDataInterface) rawDataModelView).updateFittedData(true);
//                    } catch (Exception e2) {
//                    }
                }

//                ((AbstractRawDataView) sampleSessionDataView).refreshPanel(true);
                for (int i = 0; i < rawDataModelViews.length; i++) {
                    rawDataModelViews[i].refreshPanel(false, false);
                }
                updateReportTable();
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
}
