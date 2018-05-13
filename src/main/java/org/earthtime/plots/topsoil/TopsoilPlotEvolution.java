/*
 * Copyright 2017 James F. Bowring and CIRDLES.org.
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
package org.earthtime.plots.topsoil;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;
import org.cirdles.topsoil.app.isotope.IsotopeType;
import org.cirdles.topsoil.plot.base.BasePlotDefaultProperties;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.CONCORDIA_LINE;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.ELLIPSES;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.ELLIPSE_FILL_COLOR;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.EVOLUTION_MATRIX;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.ISOTOPE_TYPE;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.REGRESSION_ENVELOPE;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.REGRESSION_LINE;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.TITLE;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.UNCERTAINTY;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.X_AXIS;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.Y_AXIS;

/**
 *
 * @author James F. Bowring
 */
public class TopsoilPlotEvolution extends AbstractTopsoilPlot {

    public TopsoilPlotEvolution(Map<String, Object> propertiesForTopsoilPlot, List<Map<String, Object>> dataForTopsoilPlot) {

        plot = IsotopeType.UTh.getPlots()[0].getPlot();
        
        plot.setData(dataForTopsoilPlot);//TopsoilDataFactory.prepareEvolutionData(TopsoilDataFactory.EXAMPLE_EVOLUTION_DATASET));//

        Map<String, Object> basePropertiesForTopsoilPlot = new BasePlotDefaultProperties();
        basePropertiesForTopsoilPlot.putAll(propertiesForTopsoilPlot);
        plot.setProperties(basePropertiesForTopsoilPlot);

    }

    @Override
    public Pane initializePlotPane() {
        TopsoilPlotController.setTopsoilPlot(this);
        Pane topsoilPlotUI = null;
        try {
            topsoilPlotUI = FXMLLoader.load(getClass().getResource("TopsoilPlot.fxml"));
        } catch (IOException iOException) {
        }

        return topsoilPlotUI;
    }

    @Override
    public List<Node> toolbarControlsFactory() {
        List<Node> controls = super.toolbarControlsFactory();

        CheckBox ellipsesCheckBox = new CheckBox("Ellipses");
        ellipsesCheckBox.setSelected(true);
        ellipsesCheckBox.setOnAction(mouseEvent -> {
            plot.setProperty(ELLIPSES, ellipsesCheckBox.isSelected());
        });
        
        ChoiceBox<SigmaPresentationModes> uncertaintyChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(SigmaPresentationModes.values()));
        uncertaintyChoiceBox.setValue(SigmaPresentationModes.TWO_SIGMA_ABSOLUTE);
        uncertaintyChoiceBox.setConverter(new StringConverter<SigmaPresentationModes>() {
            @Override
            public String toString(SigmaPresentationModes object) {
                return object.getDisplayName();
            }

            @Override
            public SigmaPresentationModes fromString(String string) {
                return null;
            }
        });
        uncertaintyChoiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<SigmaPresentationModes>() {
            @Override
            public void changed(ObservableValue observable, SigmaPresentationModes oldValue, SigmaPresentationModes newValue) {
                plot.setProperty(UNCERTAINTY, newValue.getSigmaMultiplier());
            }
        });

        ColorPicker ellipsesColorPicker = new ColorPicker(Color.RED);
        ellipsesColorPicker.setStyle("-fx-font-size: 8px; -fx-font-family: 'Courier New';");
        ellipsesColorPicker.setPrefWidth(100);
        ellipsesColorPicker.setOnAction(mouseEvent -> {
            // to satisfy D3
            plot.setProperty(ELLIPSE_FILL_COLOR, ellipsesColorPicker.getValue().toString().substring(0, 8).replaceAll("0x", "#"));
        });

        CheckBox matrixCheckBox = new CheckBox("Matrix");
        matrixCheckBox.setSelected(true);
        matrixCheckBox.setOnAction(mouseEvent -> {
            plot.setProperty(EVOLUTION_MATRIX, matrixCheckBox.isSelected());
        });

        CheckBox allSelectedCheckBox = new CheckBox("Select All");
        allSelectedCheckBox.setSelected(true);
        allSelectedCheckBox.setOnAction(mouseEvent -> {
            setSelectedAllData(allSelectedCheckBox.isSelected());
        });

        CheckBox regressionUnctEnvelopeCheckBox = new CheckBox("2D Regression Unct");
        regressionUnctEnvelopeCheckBox.setSelected(false);
        regressionUnctEnvelopeCheckBox.setOnAction(mouseEvent -> {
            plot.setProperty(REGRESSION_ENVELOPE, regressionUnctEnvelopeCheckBox.isSelected());
        });

        CheckBox regressionCheckBox = new CheckBox("2D Regression");
        regressionCheckBox.setSelected(false);
        regressionUnctEnvelopeCheckBox.setDisable(true);
        regressionCheckBox.setOnAction(mouseEvent -> {
            boolean isRegression = regressionCheckBox.isSelected();
            plot.setProperty(REGRESSION_LINE, isRegression);
            regressionUnctEnvelopeCheckBox.setDisable(!isRegression);
        });

        controls.add(ellipsesCheckBox);
        controls.add(uncertaintyChoiceBox);
        controls.add(ellipsesColorPicker);
        controls.add(allSelectedCheckBox);
        controls.add(matrixCheckBox);
        controls.add(regressionCheckBox);
        controls.add(regressionUnctEnvelopeCheckBox);

        return controls;
    }
}
