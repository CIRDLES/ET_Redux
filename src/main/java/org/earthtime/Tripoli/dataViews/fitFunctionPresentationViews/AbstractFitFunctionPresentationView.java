/*
 * AbstractFitFunctionPresentationView.java
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
package org.earthtime.Tripoli.dataViews.fitFunctionPresentationViews;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import org.earthtime.Tripoli.dataModels.DataModelFitFunctionInterface;
import org.earthtime.Tripoli.dataModels.DataModelInterface;
import org.earthtime.Tripoli.dataModels.RawRatioDataModel;
import org.earthtime.Tripoli.dataViews.AbstractRawDataView;
import org.earthtime.Tripoli.dataViews.fitFunctionViews.AbstractFitFunctionView;
import org.earthtime.Tripoli.dataViews.fitFunctionViews.ExponentialFitFunctionView;
import org.earthtime.Tripoli.dataViews.fitFunctionViews.LineFitFunctionView;
import org.earthtime.Tripoli.dataViews.fitFunctionViews.MeanFitFunctionView;
import org.earthtime.Tripoli.dataViews.fitFunctionViews.SmoothingSplineFitFunctionView;
import org.earthtime.Tripoli.dataViews.overlayViews.DataViewsOverlay;
import org.earthtime.Tripoli.dataViews.simpleViews.FitFunctionDataInterface;
import org.earthtime.Tripoli.dataViews.simpleViews.SessionOfStandardView;
import org.earthtime.Tripoli.fitFunctions.AbstractFunctionOfX;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.beans.ValueModelValueSlider;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.dataDictionaries.FitFunctionTypeEnum;

/**
 *
 * @author James F. Bowring
 */
public abstract class AbstractFitFunctionPresentationView extends AbstractRawDataView {

    /**
     *
     */
    protected ButtonGroup fitFunctionButtonGroup;
    /**
     *
     */
    protected FitFunctionDataInterface targetDataModelView;
    private AbstractFitFunctionView meanFitFunctionView = null;
    private AbstractFitFunctionView lineFitFunctionView = null;
    private AbstractFitFunctionView exponentialFitFunctionView = null;
    private AbstractFitFunctionView splineFitFunctionView = null;
    private AbstractFitFunctionView meanRatioFitFunctionView = null;
    private boolean atleastOneFit = false;
    protected boolean meanOnly;

    /**
     *
     * @param targetDataModelView
     * @param bounds
     */
    public AbstractFitFunctionPresentationView(//
            FitFunctionDataInterface targetDataModelView,//
            Rectangle bounds) {

        super(bounds);
        this.targetDataModelView = targetDataModelView;

        setCursor(Cursor.getDefaultCursor());
    }

    /**
     *
     */
    public void resetValueModelSliders() {
        meanFitFunctionView.resetValueModelSliders();
        lineFitFunctionView.resetValueModelSliders();
        exponentialFitFunctionView.resetValueModelSliders();
        splineFitFunctionView.resetValueModelSliders();
        meanRatioFitFunctionView.resetValueModelSliders();
    }

    /**
     *
     *
     * @param rawRatioDataModel
     * @param includeSpline the value of includeSpline
     */
    protected void createFitFunctionPanes(final DataModelFitFunctionInterface rawRatioDataModel, boolean includeSpline) {
        fitFunctionButtonGroup = new ButtonGroup();

        ValueModelValueSlider[] valueModelSliders;

        if (meanOnly) {
            // set up MEAN function view
            AbstractFunctionOfX FofX = rawRatioDataModel.getFitFunctions().get(FitFunctionTypeEnum.MEAN_DH.getName());

            atleastOneFit = true;
            valueModelSliders = valueModelSliderFactory(rawRatioDataModel, FofX, FitFunctionTypeEnum.MEAN_DH);

            meanFitFunctionView = new MeanFitFunctionView(//
                    FofX,//
                    valueModelSliders[0],//
                    radioButtonForFitFunctionFactory(rawRatioDataModel, FitFunctionTypeEnum.MEAN_DH),//
                    new Rectangle(0, 0, getWidth() - 1, 60));
            meanFitFunctionView.preparePanel();
            add(meanFitFunctionView, javax.swing.JLayeredPane.DEFAULT_LAYER);

        } else {
            // set up MEAN function view
            AbstractFunctionOfX FofX = rawRatioDataModel.getFitFunctions().get(FitFunctionTypeEnum.MEAN.getName());

            if ((FofX != null) && FofX.verifyPositiveVariances()) {
                atleastOneFit = true;
                valueModelSliders = valueModelSliderFactory(rawRatioDataModel, FofX, FitFunctionTypeEnum.MEAN);

                meanFitFunctionView = new MeanFitFunctionView(//
                        FofX,//
                        valueModelSliders[0],//
                        radioButtonForFitFunctionFactory(rawRatioDataModel, FitFunctionTypeEnum.MEAN),//
                        new Rectangle(0, 0, getWidth() - 1, 60));

            } else {
                meanFitFunctionView = new MeanFitFunctionView(//
                        FofX,//
                        null,//
                        radioButtonForFitFunctionFactory(rawRatioDataModel, FitFunctionTypeEnum.MEAN),//
                        new Rectangle(0, 0, getWidth() - 1, 60));
            }

            meanFitFunctionView.preparePanel();
            add(meanFitFunctionView, javax.swing.JLayeredPane.DEFAULT_LAYER);

            // set up LINE function view
            FofX = rawRatioDataModel.getFitFunctions().get(FitFunctionTypeEnum.LINE.getName());

            if ((FofX != null) && FofX.verifyPositiveVariances()) {
                atleastOneFit = true;
                valueModelSliders = valueModelSliderFactory(rawRatioDataModel, FofX, FitFunctionTypeEnum.LINE);

                lineFitFunctionView = new LineFitFunctionView(//
                        FofX,//
                        valueModelSliders[0],//
                        valueModelSliders[1],//
                        radioButtonForFitFunctionFactory(rawRatioDataModel, FitFunctionTypeEnum.LINE),//
                        new Rectangle(0, 60, getWidth() - 1, 80));
            } else {
                lineFitFunctionView = new LineFitFunctionView(//
                        FofX,//
                        null,//
                        null,//
                        radioButtonForFitFunctionFactory(rawRatioDataModel, FitFunctionTypeEnum.LINE),//
                        new Rectangle(0, 60, getWidth() - 1, 80));
            }

            lineFitFunctionView.preparePanel();
            add(lineFitFunctionView, javax.swing.JLayeredPane.DEFAULT_LAYER);

            // set up EXPONENTIAL function view
            FofX = rawRatioDataModel.getFitFunctions().get(FitFunctionTypeEnum.EXPONENTIAL.getName());

            if ((FofX != null) && FofX.verifyPositiveVariances()) {
                atleastOneFit = true;
                valueModelSliders = valueModelSliderFactory(rawRatioDataModel, FofX, FitFunctionTypeEnum.EXPONENTIAL);

                exponentialFitFunctionView = new ExponentialFitFunctionView(//
                        FofX,//
                        valueModelSliders[0],//
                        valueModelSliders[1],//
                        valueModelSliders[2],//
                        radioButtonForFitFunctionFactory(rawRatioDataModel, FitFunctionTypeEnum.EXPONENTIAL),//
                        new Rectangle(0, 140, getWidth() - 1, 100));
            } else {
                exponentialFitFunctionView = new ExponentialFitFunctionView(//
                        null,//
                        null,//
                        null,//
                        null,//
                        radioButtonForFitFunctionFactory(rawRatioDataModel, FitFunctionTypeEnum.EXPONENTIAL),//
                        new Rectangle(0, 140, getWidth() - 1, 100));
            }

            exponentialFitFunctionView.preparePanel();
            add(exponentialFitFunctionView, javax.swing.JLayeredPane.DEFAULT_LAYER);

            // set up SMOOTHING_SPLINE function view
            if (includeSpline) {
                try {
                    FofX = rawRatioDataModel.getFitFunctions().get(FitFunctionTypeEnum.SMOOTHING_SPLINE.getName());

                    if ((FofX != null) && FofX.verifyPositiveVariances()) {
                        atleastOneFit = true;

//                    valueModelSliders = valueModelSliderFactory(rawRatioDataModel, FofX, FitFunctionTypeEnum.SMOOTHING_SPLINE);
                        splineFitFunctionView = new SmoothingSplineFitFunctionView(//
                                FofX,//
                                this,//
                                radioButtonForFitFunctionFactory(rawRatioDataModel, FitFunctionTypeEnum.SMOOTHING_SPLINE),//
                                new Rectangle(0, 140, getWidth() - 1, 100), //
                                targetDataModelView);
                    } else {
                        splineFitFunctionView = new SmoothingSplineFitFunctionView( //
                                FofX,//
                                null,//
                                radioButtonForFitFunctionFactory(rawRatioDataModel, FitFunctionTypeEnum.SMOOTHING_SPLINE),//
                                new Rectangle(0, 140, getWidth() - 1, 100), //
                                targetDataModelView);
                    }

                    splineFitFunctionView.preparePanel();
                    add(splineFitFunctionView, javax.swing.JLayeredPane.DEFAULT_LAYER);
                } catch (Exception e) {
                }
            } else {
                // nov 2014 show forced mean of ratios if present
                // set up MEAN function view
                FofX = rawRatioDataModel.getFitFunctions().get(FitFunctionTypeEnum.MEANRATIO.getName());

                if ((FofX != null) && FofX.verifyPositiveVariances()) {
                    atleastOneFit = true;
                    valueModelSliders = valueModelSliderFactory(rawRatioDataModel, FofX, FitFunctionTypeEnum.MEANRATIO);

                    meanRatioFitFunctionView = new MeanFitFunctionView(//
                            FofX,//
                            valueModelSliders[0],//
                            radioButtonForFitFunctionFactory(rawRatioDataModel, FitFunctionTypeEnum.MEANRATIO),//
                            new Rectangle(0, 0, getWidth() - 1, 60));
                    meanRatioFitFunctionView.preparePanel();
                    add(meanRatioFitFunctionView, javax.swing.JLayeredPane.DEFAULT_LAYER);

                }
            }
        }
        if (atleastOneFit) {
            JCheckBox overDispersionCheckBox = new JCheckBox("OverDispersion");
            if (meanOnly) {
                overDispersionCheckBox.setSelected(((RawRatioDataModel) rawRatioDataModel).isOverDispersionSelectedDownHole());
            } else {
                overDispersionCheckBox.setSelected(rawRatioDataModel.isOverDispersionSelected());
            }
            overDispersionCheckBox.setBounds(0, 0, 200, 20);
            overDispersionCheckBox.setFont(new Font("SansSerif", Font.PLAIN, 11));
            overDispersionCheckBox.setOpaque(true);
            overDispersionCheckBox.setBackground(Color.WHITE);
            overDispersionCheckBox.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent ae) {
                    if (meanOnly) {
                        ((RawRatioDataModel) rawRatioDataModel).setOverDispersionSelectedDownHole(((AbstractButton) ae.getSource()).isSelected());
                    } else {
                        rawRatioDataModel.setOverDispersionSelected(((AbstractButton) ae.getSource()).isSelected());
                    }

                    refreshPanel(true, false);

                    updatePlotsWithChanges(targetDataModelView);

                    if (targetDataModelView instanceof SessionOfStandardView) {
                        updateReportTable();
                    }
                }
            });
            add(overDispersionCheckBox);
        }

        layoutFitFunctionViews(atleastOneFit, this);
    }

    private ValueModelValueSlider[] valueModelSliderFactory(//
            final DataModelFitFunctionInterface rawRatioDataModel,//
            final AbstractFunctionOfX FofX, //
            final FitFunctionTypeEnum fitFunctionType) {

        ValueModelValueSlider[] valueModelSliders = new ValueModelValueSlider[3];

        int sliderLeft = 30;
        int sliderWidth = 85;
        int sliderHeight = 18;

        // new improved logic Sept 2012
        // TODO: refactor once tested
        if (fitFunctionType.equals(FitFunctionTypeEnum.MEAN)//
                || //
                fitFunctionType.equals(FitFunctionTypeEnum.MEAN_DH)//
                || //
                fitFunctionType.equals(FitFunctionTypeEnum.SMOOTHING_SPLINE)//
                || //
                fitFunctionType.equals(FitFunctionTypeEnum.MEANRATIO)) {
            double a = FofX.getA();
            double a1SigmaAbs = FofX.getStdErrOfA();

            try {
                ValueModel aVM = new ValueModel(//
                        "a", //
                        new BigDecimal(a),//
                        "ABS", //
                        new BigDecimal(a1SigmaAbs), BigDecimal.ZERO);

                valueModelSliders[0] = new ValueModelValueSlider(//
                        sliderLeft, //
                        35, //
                        sliderWidth, //
                        sliderHeight,//
                        aVM,//
                        "",//
                        (PropertyChangeEvent evt) -> {
                            FofX.setA((((Number) evt.getNewValue()).doubleValue()));                            
                            updatePlotsWithChanges(targetDataModelView);
                });
            } catch (Exception e) {
                System.out.println("Error creating parameter slider 0");
            }
        }

        if (fitFunctionType.equals(FitFunctionTypeEnum.LINE)) {

            double a = FofX.getA();
            double a1SigmaAbs = FofX.getStdErrOfA();

            try {
                ValueModel aVM = new ValueModel(//
                        "a", //
                        new BigDecimal(a),//
                        "ABS", //
                        new BigDecimal(a1SigmaAbs), BigDecimal.ZERO);

                valueModelSliders[0] = new ValueModelValueSlider(//
                        sliderLeft, //
                        35, //
                        sliderWidth, //
                        sliderHeight,//
                        aVM,
                        "",//
                        (PropertyChangeEvent evt) -> {
                            FofX.setA((((Number) evt.getNewValue()).doubleValue()));
                            updatePlotsWithChanges(targetDataModelView);
                        });
            } catch (Exception e) {
            }

            try {
                double b = FofX.getB();
                double b1SigmaAbs = FofX.getStdErrOfB();

                ValueModel bVM = new ValueModel(//
                        "b", //
                        new BigDecimal(b),//
                        "ABS", //
                        new BigDecimal(b1SigmaAbs), BigDecimal.ZERO);

                valueModelSliders[1] = new ValueModelValueSlider(//
                        sliderLeft, //
                        55, //
                        sliderWidth, //
                        sliderHeight,//
                        bVM,
                        "", (PropertyChangeEvent evt) -> {
                            FofX.setB((((Number) evt.getNewValue()).doubleValue()));                        
                            updatePlotsWithChanges(targetDataModelView);
                });
            } catch (Exception e) {
            }
        }

        if (fitFunctionType.equals(FitFunctionTypeEnum.EXPONENTIAL)) {

            double a = FofX.getA();
            double a1SigmaAbs = FofX.getStdErrOfA();

            try {
                ValueModel aVM = new ValueModel(//
                        "a", //
                        new BigDecimal(a),//
                        "ABS", //
                        new BigDecimal(a1SigmaAbs), BigDecimal.ZERO);

                valueModelSliders[0] = new ValueModelValueSlider(//
                        sliderLeft, //
                        35, //
                        sliderWidth, //
                        sliderHeight,//
                        aVM,//
                        "",//
                        (PropertyChangeEvent evt) -> {
                            FofX.setA((((Number) evt.getNewValue()).doubleValue()));                            
                            updatePlotsWithChanges(targetDataModelView);
                });
            } catch (Exception e) {
            }

            try {
                double b = FofX.getB();
                double b1SigmaAbs = FofX.getStdErrOfC();

                ValueModel bVM = new ValueModel(//
                        "b", //
                        new BigDecimal(b),//
                        "ABS", //
                        new BigDecimal(b1SigmaAbs), BigDecimal.ZERO);

                valueModelSliders[1] = new ValueModelValueSlider(//
                        sliderLeft, //
                        55, //
                        sliderWidth, //
                        sliderHeight,//
                        bVM,//
                        "",//
                        (PropertyChangeEvent evt) -> {
                            FofX.setB((((Number) evt.getNewValue()).doubleValue()));                           
                            updatePlotsWithChanges(targetDataModelView);
                });
            } catch (Exception e) {
            }

            try {
                double c = FofX.getC();
                double c1SigmaAbs = Math.sqrt(FofX.getFitParameterCovarianceMatrix().get(2, 2));

                ValueModel cVM = new ValueModel(//
                        "c", //
                        new BigDecimal(c),//
                        "ABS", //
                        new BigDecimal(c1SigmaAbs), BigDecimal.ZERO);

                valueModelSliders[2] = new ValueModelValueSlider(//
                        sliderLeft, //
                        75, //
                        sliderWidth, //
                        sliderHeight,//
                        cVM,//
                        "", //
                        (PropertyChangeEvent evt) -> {
                            FofX.setC((((Number) evt.getNewValue()).doubleValue()));                            
                            updatePlotsWithChanges(targetDataModelView);
                });
            } catch (Exception e) {
            }

        }

        if (valueModelSliders[0] == null) {
            System.out.println("Parameter slider did not make");
        }
        return valueModelSliders;
    }

    private JRadioButton radioButtonForFitFunctionFactory(//
            final DataModelFitFunctionInterface rawRatioDataModel, final FitFunctionTypeEnum fitFunctionType) {

        // feb 2013
        String overDispersion = "";
        DecimalFormat f = new DecimalFormat("0.000");
        if (fitFunctionType.compareTo(FitFunctionTypeEnum.MEAN_DH) == 0) {
            if (((RawRatioDataModel)rawRatioDataModel).isOverDispersionSelectedDownHole()&& rawRatioDataModel.doesFitFunctionTypeHaveOD(fitFunctionType)) {
                overDispersion = "-OD \u03BE = " + f.format(rawRatioDataModel.getXIforFitFunction(fitFunctionType));
            }
        } else {
            if (rawRatioDataModel.isOverDispersionSelected() && rawRatioDataModel.doesFitFunctionTypeHaveOD(fitFunctionType)) {
                if (fitFunctionType.compareTo(FitFunctionTypeEnum.SMOOTHING_SPLINE) == 0) {
                    overDispersion = "-OD";
                } else {
                    overDispersion = "-OD \u03BE = " + f.format(rawRatioDataModel.getXIforFitFunction(fitFunctionType));
                }
            }
        }

        JRadioButton functionChoiceRadioButton = new JRadioButton(fitFunctionType.getPrettyName() + overDispersion);
        functionChoiceRadioButton.setName(fitFunctionType.getName());
        functionChoiceRadioButton.setFont(ReduxConstants.sansSerif_10_Plain);//    new Font("SansSerif", Font.PLAIN, 10));
        functionChoiceRadioButton.setBounds(1, 1, 160, 17);
        functionChoiceRadioButton.setOpaque(false);

        if (fitFunctionType.compareTo(FitFunctionTypeEnum.MEAN_DH) == 0) {
            // only one available for downhole
            functionChoiceRadioButton.setSelected(true);
        } else {
            functionChoiceRadioButton.setSelected( //
                    rawRatioDataModel.getSelectedFitFunctionType().compareTo(fitFunctionType) == 0);
        }

        functionChoiceRadioButton.addActionListener((ActionEvent e) -> {
            // on click, take control
            // check if fit function exists (could be calculated)
            if (rawRatioDataModel.containsFitFunction(fitFunctionType)) {
                rawRatioDataModel.setSelectedFitFunctionType(fitFunctionType);
                
                if (targetDataModelView instanceof DataViewsOverlay) {
                    ((DataViewsOverlay) targetDataModelView).getDownholeFractionationDataModel()//
                            .calculateWeightedMeanForEachStandard(rawRatioDataModel.getRawRatioModelName(), rawRatioDataModel.getSelectedFitFunction());
                }
                
                layoutFitFunctionViews(atleastOneFit, ((AbstractRawDataView) ((Component) e.getSource()).getParent().getParent()));
                
                updatePlotsWithChanges(targetDataModelView);
                
                updateReportTable();
                
            }
//                do nothing updatePlotsWithChanges(targetDataModelView);
        });

        fitFunctionButtonGroup.add(functionChoiceRadioButton);
        return functionChoiceRadioButton;
    }

    private void layoutFitFunctionViews(boolean atleastOneFit, AbstractRawDataView parentFitFunctionsPresentationView) {

        int consumedHeight = 0;
        int currentPaneHeight;

        for (Component c : parentFitFunctionsPresentationView.getComponents()) {
            if (c instanceof AbstractFitFunctionView) {
                if (atleastOneFit && ((AbstractFitFunctionView) c).getFunctionChoiceRadioButton().isSelected()) {
                    if (((AbstractFitFunctionView) c).getFunctionChoiceRadioButton().getName().equalsIgnoreCase(FitFunctionTypeEnum.EXPONENTIAL.getName())) {

                        currentPaneHeight = 100;
                    } else if (((AbstractFitFunctionView) c).getFunctionChoiceRadioButton().getName().equalsIgnoreCase(FitFunctionTypeEnum.SMOOTHING_SPLINE.getName())) {

                        currentPaneHeight = 100;
                    } else {
                        currentPaneHeight = 75;
                    }
                } else {
                    currentPaneHeight = 20;
                }
                c.setBounds(0, consumedHeight, c.getWidth(), currentPaneHeight);
                consumedHeight += currentPaneHeight;
            } else if (c instanceof JCheckBox) {
                // overdispersion check box
                c.setBounds(0, consumedHeight, 200, 20);
            }
        }
    }

    /**
     *
     * @return
     */
    @Override
    public DataModelInterface getDataModel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
