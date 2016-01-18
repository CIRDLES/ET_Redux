/*
 * Copyright 2015 CIRDLES.
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
package org.earthtime.UTh_Redux.dateInterpretation;

import java.awt.Container;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javafx.fxml.FXML;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javax.swing.JComponent;
import javax.swing.WindowConstants;
import org.cirdles.topsoil.dataset.Dataset;
import org.cirdles.topsoil.dataset.RawData;
import org.cirdles.topsoil.dataset.SimpleDataset;
import org.cirdles.topsoil.dataset.entry.Entry;
import org.cirdles.topsoil.dataset.entry.SimpleEntry;
import org.cirdles.topsoil.dataset.field.Field;
import org.cirdles.topsoil.dataset.field.NumberField;
import org.cirdles.topsoil.plot.Plot;
import org.cirdles.topsoil.plot.SimpleVariableContext;
import org.cirdles.topsoil.plot.VariableContext;
import org.cirdles.topsoil.plot.standard.EvolutionPlot;
import org.earthtime.UTh_Redux.fractions.UThLegacyFractionI;
import org.earthtime.dataDictionaries.UThAnalysisMeasures;
import org.earthtime.fractions.ETFractionInterface;

/**
 *
 * @author bowring
 */
public class TopsoilEvolutionPlot{// extends CustomVBox<TopsoilEvolutionChart> {

    private Vector<ETFractionInterface> selectedFractions;
    private Plot myChart;
    private List<Field<?>> myFields;
    private JComponent plotAsComponent;

    @FXML
    private HBox chartAndConfig;
    @FXML
    private ToolBar chartToolBar;

    public TopsoilEvolutionPlot() {
        //super(self -> self.myChart = new EvolutionChart());
        myChart = new EvolutionPlot();

        myFields = new ArrayList<>();
        myFields.add(new NumberField(UThAnalysisMeasures.ar230Th_238Ufc.getName()));
        myFields.add(new NumberField(UThAnalysisMeasures.ar230Th_238Ufc.getName() + "-2sigma"));
        myFields.add(new NumberField(UThAnalysisMeasures.ar234U_238Ufc.getName()));
        myFields.add(new NumberField(UThAnalysisMeasures.ar234U_238Ufc.getName() + "-2sigma"));
        myFields.add(new NumberField("rho"));

    }

    public void showPanel() {

//        Button fitData = new Button("Fit data");
//        fitData.setOnAction(mouseEvent -> {
//            ((JavaScriptChart) myChart).fitData();
//        });
//
//        chartToolBar.getItems().addAll(fitData);
//
//        try {
//            chartAndConfig.getChildren().setAll(
//                    myChart.displayAsNode(),
//                    myChart.getPropertiesPanel().displayAsNode());
//        } catch (UnsupportedOperationException ex) {
//            chartAndConfig.getChildren().setAll(
//                    myChart.displayAsNode());
//        }
//
//        Scene scene = new Scene(this, 1200, 800);
//
//        Stage chartStage = new Stage();
//        chartStage.setScene(scene);
//        chartStage.show();

        class EvolutionChartDialog extends javax.swing.JFrame {

            public EvolutionChartDialog(javax.swing.JFrame owner, boolean modal) {
                super();
            }
        }

        EvolutionChartDialog testTopsoilDialogDialog = new EvolutionChartDialog(null, true);
        testTopsoilDialogDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        testTopsoilDialogDialog.setBounds( //
                400,
                100,
                820,
                640);

        Container contentPane = testTopsoilDialogDialog.getContentPane();

        plotAsComponent = myChart.displayAsJComponent();
        plotAsComponent.createToolTip().setTipText("TESTING");
        contentPane.add(plotAsComponent);

//        ET_JButton fitDataButton = new ET_JButton("Fit Data");
//        fitDataButton.setBounds(10, 10, 50, 25);
//        fitDataButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                ((JavaScriptChart) myChart).fitData();
//            }
//        });
//
//        plotAsComponent.add(fitDataButton);

        testTopsoilDialogDialog.setVisible(true);

    }

    public void preparePanel() {

        List<Entry> myEntries = new ArrayList<>();

        for (int i = 0; i < selectedFractions.size(); i++) {
            UThLegacyFractionI fraction = (UThLegacyFractionI) selectedFractions.get(i);
            Entry dataEntry = new SimpleEntry();
            dataEntry.set((Field<? super Double>) myFields.get(0), //
                    fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.ar230Th_238Ufc.getName())//
                    .getValue().doubleValue());
            dataEntry.set((Field<? super Double>) myFields.get(1), //
                    fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.ar230Th_238Ufc.getName())//
                    .getOneSigmaAbs().doubleValue() * 2.0);
            dataEntry.set((Field<? super Double>) myFields.get(2), //
                    fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.ar234U_238Ufc.getName())//
                    .getValue().doubleValue());
            dataEntry.set((Field<? super Double>) myFields.get(3), //
                    fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.ar234U_238Ufc.getName())//
                    .getOneSigmaAbs().doubleValue() * 2.0);
            dataEntry.set((Field<? super Double>) myFields.get(4), 0.0);
            myEntries.add(dataEntry);
        }
        RawData rawData = new RawData(myFields, myEntries);

        Dataset dataset = new SimpleDataset(
                "U-series dataset",
                rawData);

        VariableContext vc = new SimpleVariableContext(dataset);
        for (int i = 0; i < myChart.getVariables().size(); i++) {
            vc.addBinding(myChart.getVariables().get(i), myFields.get(i));
        }

        
        
        
        myChart.setData(vc);

    }

    /**
     * @param selectedFractions the selectedFractions to set
     */
    public void setSelectedFractions(Vector<ETFractionInterface> selectedFractions) {
        this.selectedFractions = selectedFractions;
    }
}
