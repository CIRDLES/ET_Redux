/*
 * SmoothingSplineFitFunctionView.java
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
package org.earthtime.Tripoli.dataViews.fitFunctionViews;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.SortedMap;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import org.earthtime.Tripoli.dataModels.sessionModels.AbstractSessionForStandardDataModel;
import org.earthtime.Tripoli.dataViews.AbstractRawDataView;
import org.earthtime.Tripoli.dataViews.fitFunctionPresentationViews.AbstractFitFunctionPresentationView;
import org.earthtime.Tripoli.dataViews.fitFunctionPresentationViews.SplineOverDispersionChooserPanel;
import org.earthtime.Tripoli.dataViews.overlayViews.DataViewsOverlay;
import org.earthtime.Tripoli.dataViews.simpleViews.FitFunctionDataInterface;
import org.earthtime.Tripoli.dataViews.simpleViews.SessionOfStandardView;
import org.earthtime.Tripoli.fitFunctions.AbstractFunctionOfX;
import org.earthtime.UPb_Redux.dialogs.DialogEditor;
import org.earthtime.beans.ET_JButton;
import org.earthtime.dataDictionaries.FitFunctionTypeEnum;

/**
 *
 * @author James F. Bowring
 */
public class SmoothingSplineFitFunctionView extends AbstractFitFunctionView {

    private AbstractRawDataView splineOverDispersionChooserPanel;
    private final FitFunctionDataInterface targetDataModelView;
    private JLabel xiLabel;
    private final AbstractFitFunctionPresentationView presentationView;
    private JTextField startText;
    private JTextField stepText;
    private JTextField stopText;

    /**
     *
     * @param smoothingSplineFitFofX
     * @param presentationView
     * @param functionChoiceRadioButton
     * @param bounds
     * @param targetDataModelView
     */
    public SmoothingSplineFitFunctionView(//
            AbstractFunctionOfX smoothingSplineFitFofX, //
            AbstractFitFunctionPresentationView presentationView,//
            final JRadioButton functionChoiceRadioButton, //
            Rectangle bounds, //
            final FitFunctionDataInterface targetDataModelView) {

        super(smoothingSplineFitFofX, functionChoiceRadioButton, bounds);

        this.presentationView = presentationView;

        this.targetDataModelView = targetDataModelView;

        if (smoothingSplineFitFofX.isOverDispersionSelected()) {
            JButton ODChoiceButton = new ET_JButton("Generate OD");
            ODChoiceButton.setFont(new Font("SansSerif", Font.PLAIN, 10));
            ODChoiceButton.setBounds(5, 20, 81, 15);
            ODChoiceButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ae) {
                    // TODO: Broke Demeter

                    SortedMap<Double, AbstractFunctionOfX> sessionOfStandardsSplinesWithOD = null;
                    if (targetDataModelView instanceof SessionOfStandardView) {
                        sessionOfStandardsSplinesWithOD = //
                                ((AbstractSessionForStandardDataModel) ((SessionOfStandardView) targetDataModelView)//
                                .getSessionForStandardDataModel()).generateSetOfSplineWithODfitFunctions(//
                                        Double.valueOf(startText.getText()),//
                                        Double.valueOf(stepText.getText()),//
                                        Double.valueOf(stopText.getText()));
                    } else if (targetDataModelView instanceof DataViewsOverlay) {
                        sessionOfStandardsSplinesWithOD = //
                                ((DataViewsOverlay) targetDataModelView)//
                                .getDownholeFractionationDataModel().generateSetOfSplineWithODfitFunctions(//
                                        Double.valueOf(startText.getText()),//
                                        Double.valueOf(stepText.getText()),//
                                        Double.valueOf(stopText.getText()));
                    }

                    if (splineOverDispersionChooserPanel != null) {
                        remove(splineOverDispersionChooserPanel);
                    }

                    generateSplineOverDispersionChooserPanel(sessionOfStandardsSplinesWithOD);
                    splineOverDispersionChooserPanel.preparePanel();

                    add(splineOverDispersionChooserPanel);

                    if (targetDataModelView instanceof SessionOfStandardView) {
                        ((SessionOfStandardView) targetDataModelView).refreshPanel();
                    } else if (targetDataModelView instanceof SessionOfStandardView) {
                        ((DataViewsOverlay) targetDataModelView).refreshPanel();
                    }

                    functionChoiceRadioButton.doClick();
                }
            });

            add(ODChoiceButton);

            Font odFont = new Font(
                    "SansSerif",
                    Font.PLAIN,
                    9);

            // text boxes for od
            DecimalFormat sss = new DecimalFormat("0.00000");

            startText = new JTextField();
            startText.setDocument(new DialogEditor.DoubleDocument(startText, true));

            AbstractSessionForStandardDataModel sessionForStandardDataModel =//
                    ((AbstractSessionForStandardDataModel) ((SessionOfStandardView) targetDataModelView)//
                    .getSessionForStandardDataModel());
            
            double startVal = Math.sqrt(sessionForStandardDataModel.getSessionOfStandardsFitFunctionsWithOD().get(FitFunctionTypeEnum.LINE.getName()).getOverDispersion());
            startText.setText(sss.format(0.8 * startVal));//              ("0.001");
            startText.setBounds(new Rectangle(35, 38, 50, 15));
            startText.setFont(odFont);
            add(startText);

            JLabel startTextLabel = new JLabel("start:");
            startTextLabel.setBounds(new Rectangle(6, 38, 30, 15));
            startTextLabel.setFont(odFont);
            add(startTextLabel);

            stepText = new JTextField();
            stepText.setDocument(new DialogEditor.DoubleDocument(stepText, true));
            stepText.setText(sss.format(0.2 * startVal / 15.0));// "0.01");
            stepText.setBounds(new Rectangle(35, 54, 50, 15));
            stepText.setFont(odFont);
            add(stepText);

            JLabel stepTextLabel = new JLabel("step:");
            stepTextLabel.setBounds(new Rectangle(6, 54, 30, 15));
            stepTextLabel.setFont(odFont);
            add(stepTextLabel);

            stopText = new JTextField();
            stopText.setDocument(new DialogEditor.DoubleDocument(stopText, true));
            stopText.setText(sss.format(startVal));// "0.05");
            stopText.setBounds(new Rectangle(35, 70, 50, 15));
            stopText.setFont(odFont);
            add(stopText);

            JLabel stopTextLabel = new JLabel("stop:");
            stopTextLabel.setBounds(new Rectangle(6, 70, 30, 15));
            stopTextLabel.setFont(odFont);
            add(stopTextLabel);

            // show overdispersion
            DecimalFormat f = new DecimalFormat("\u03BE = 0.0000");

            xiLabel = new JLabel(f.format(Math.sqrt(fittedFofX.getOverDispersion())));
            xiLabel.setFont(new Font(
                    "SansSerif",
                    Font.BOLD,
                    10));
            xiLabel.setBounds(new Rectangle(6, 85, 80, 18));

            add(xiLabel);
        }

        /*
         NOTES FROM Noah 27 Oct 2014
         Default for Spline-OD =
         floor(0.95*0.15*1000)/1000
         floor(0.95*LinearOverdispersion*1000)/1000
         
         Start/Step/Stop:
         Start: floor(0.8*LinearOverdispersion*1000)/1000
         Stop: LinearOverdispersion
         Step = difference / 15
        
         Multiply all Overdispersions by 100 for display
         Where Overdispersion is the Greek ziggety-zag
         For display means in start, step, stop, and value display (ziggety equals)
         Show the graph always when Spline-OD is selected.
         */
    }

    /**
     *
     */
    public void refreshXiLabel() {
        DecimalFormat f = new DecimalFormat("\u03BE = 0.00000");
        xiLabel.setText(f.format(Math.sqrt(fittedFofX.getOverDispersion())));
    }

    private void generateSplineOverDispersionChooserPanel(SortedMap<Double, AbstractFunctionOfX> sessionOfStandardsSplinesWithOD) {

        Rectangle bounds = new Rectangle(90, 18, getWidth() - 90, getHeight() - 18);

        if (targetDataModelView instanceof SessionOfStandardView) {
            splineOverDispersionChooserPanel = //
                    new SplineOverDispersionChooserPanel(((SessionOfStandardView) targetDataModelView), this, sessionOfStandardsSplinesWithOD, bounds);

        } else if (targetDataModelView instanceof DataViewsOverlay) {
            splineOverDispersionChooserPanel = //
                    new SplineOverDispersionChooserPanel(((DataViewsOverlay) targetDataModelView), this, sessionOfStandardsSplinesWithOD, bounds);
        }
    }

    /**
     *
     */
    @Override
    public void resetValueModelSliders() {
        //parameterAValueSlider.resetSliderBox();
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
    @Override
    public void preparePanel() {
        super.preparePanel();

    }

    /**
     * @return the presentationView
     */
    public AbstractFitFunctionPresentationView getPresentationView() {
        return presentationView;
    }
}
