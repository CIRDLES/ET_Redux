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

import java.util.Vector;
import org.earthtime.fractions.ETFractionInterface;

/**
 *
 * @author bowring
 */
public final class TopsoilEvolutionPlot {

    private static final TopsoilEvolutionPlot instance = new TopsoilEvolutionPlot();
    private Vector<ETFractionInterface> selectedFractions;
//    private final Plot myChart;
//    private final List<Field<?>> myFields;
//    private JComponent plotAsComponent;
//    private final EvolutionChartDialog topsoilEvolutionChartDialog;
//    private Container contentPane;

    private TopsoilEvolutionPlot() {
//        myChart = new EvolutionPlot();
//
//        myFields = new ArrayList<>();
//        myFields.add(new NumberField(UThAnalysisMeasures.ar230Th_238Ufc.getName()));
//        myFields.add(new NumberField(UThAnalysisMeasures.ar230Th_238Ufc.getName() + "-2sigma"));
//        myFields.add(new NumberField(UThAnalysisMeasures.ar234U_238Ufc.getName()));
//        myFields.add(new NumberField(UThAnalysisMeasures.ar234U_238Ufc.getName() + "-2sigma"));
//        myFields.add(new NumberField("rho"));
//
//        topsoilEvolutionChartDialog = new EvolutionChartDialog(null, true);
//        topsoilEvolutionChartDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
//        topsoilEvolutionChartDialog.setBounds( //
//                400,
//                100,
//                820,
//                640);
//
//        contentPane = topsoilEvolutionChartDialog.getContentPane();
//        plotAsComponent = myChart.displayAsJComponent();
//        contentPane.add(plotAsComponent);
    }

    private class EvolutionChartDialog extends javax.swing.JFrame {

        public EvolutionChartDialog(javax.swing.JFrame owner, boolean modal) {
            super();
        }
    }

    public static TopsoilEvolutionPlot getInstance() {
        return instance;
    }

//    public void showPanel() {
//        plotAsComponent.repaint();
//        topsoilEvolutionChartDialog.setVisible(true);
//    }

    public void preparePanel() {
//
//        List<Entry> myEntries = new ArrayList<>();
//
//        for (int i = 0; i < selectedFractions.size(); i++) {
//            if (!selectedFractions.get(i).isRejected()) {
//                UThLegacyFractionI fraction = (UThLegacyFractionI) selectedFractions.get(i);
//                Entry dataEntry = new SimpleEntry();
//                dataEntry.set((Field<? super Double>) myFields.get(0), //
//                        fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.ar230Th_238Ufc.getName())//
//                        .getValue().doubleValue());
//                dataEntry.set((Field<? super Double>) myFields.get(1), //
//                        fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.ar230Th_238Ufc.getName())//
//                        .getOneSigmaAbs().doubleValue() * 2.0);
//                dataEntry.set((Field<? super Double>) myFields.get(2), //
//                        fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.ar234U_238Ufc.getName())//
//                        .getValue().doubleValue());
//                dataEntry.set((Field<? super Double>) myFields.get(3), //
//                        fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.ar234U_238Ufc.getName())//
//                        .getOneSigmaAbs().doubleValue() * 2.0);
//                dataEntry.set((Field<? super Double>) myFields.get(4), 0.0);
//                myEntries.add(dataEntry);
//            }
//        }
//        RawData rawData = new RawData(myFields, myEntries);
//
//        Dataset dataset = new SimpleDataset(
//                "U-series dataset",
//                rawData);
//
//        PlotContext vc = new SimplePlotContext(dataset);
//        for (int i = 0; i < myChart.getVariables().size(); i++) {
//            vc.addBinding(myChart.getVariables().get(i), myFields.get(i));
//        }
//
//        myChart.setContext(vc);

    }

    /**
     * @param selectedFractions the selectedFractions to set
     */
    public void setSelectedFractions(Vector<ETFractionInterface> selectedFractions) {
        this.selectedFractions = selectedFractions;
    }
}
