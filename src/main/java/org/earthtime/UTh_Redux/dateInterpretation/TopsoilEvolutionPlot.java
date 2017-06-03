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

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javafx.collections.FXCollections;
import javax.swing.*;
import org.cirdles.topsoil.app.plot.TopsoilPlotType;
import org.cirdles.topsoil.plot.Plot;
import org.cirdles.topsoil.plot.base.BasePlotDefaultProperties;
import org.cirdles.topsoil.plot.uth.evolution.EvolutionPlotDefaultProperties;
import org.earthtime.UTh_Redux.fractions.UThLegacyFractionI;
import org.earthtime.dataDictionaries.UThAnalysisMeasures;
import org.earthtime.fractions.ETFractionInterface;

/**
 *
 * @author bowring
 */
public final class TopsoilEvolutionPlot {

    private static final TopsoilEvolutionPlot instance = null;
    private Vector<ETFractionInterface> selectedFractions;
    private Plot myChart;
    private final JComponent plotAsComponent;
    private final EvolutionChartDialog topsoilEvolutionChartDialog;
    private final Container contentPane;

    private static final String X = "x";
    private static final String SIGMA_X = "sigma_x";
    private static final String Y = "y";
    private static final String SIGMA_Y = "sigma_y";
    private static final String RHO = "rho";
    private static final String SELECTED = "Selected";

    public TopsoilEvolutionPlot() {

        myChart = TopsoilPlotType.BASE_PLOT.getPlot();
        myChart.getProperties().put("Title", "Evolution Plot");
        myChart.getProperties().put("X Axis", "[230Th/238U]");
        myChart.getProperties().put("Y Axis", "[234U/238U]");
        myChart.getProperties().put("Evolution", true);
        myChart.getProperties().put("Ellipses", true);
        myChart.getProperties().put("Isotope", "Uranium Thorium");

        topsoilEvolutionChartDialog = new EvolutionChartDialog(null, true);
        topsoilEvolutionChartDialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        topsoilEvolutionChartDialog.setBounds( //
                400,
                100,
                820,
                640);

        contentPane = topsoilEvolutionChartDialog.getContentPane();
        plotAsComponent = myChart.displayAsJComponent();
        contentPane.add(plotAsComponent);
    }

    private class EvolutionChartDialog extends javax.swing.JFrame {

        public EvolutionChartDialog(javax.swing.JFrame owner, boolean modal) {
            super();
        }

    }

    public static TopsoilEvolutionPlot getInstance() {
        return new TopsoilEvolutionPlot();//instance;
    }

    public void showPanel() {
        plotAsComponent.repaint();
        topsoilEvolutionChartDialog.setVisible(true);
    }

    public void preparePanel() {

        List<Map<String, Object>> myData = new ArrayList<>();
        for (int i = 0; i < selectedFractions.size(); i++) {
            UThLegacyFractionI fraction = (UThLegacyFractionI) selectedFractions.get(i);
            Map<String, Object> datum = new HashMap<>();
            myData.add(datum);
            datum.put(X, fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.ar230Th_238Ufc.getName())//
                    .getValue().doubleValue());
            datum.put(SIGMA_X, fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.ar230Th_238Ufc.getName())//
                    .getOneSigmaAbs().doubleValue() * 2.0);
            datum.put(Y, fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.ar234U_238Ufc.getName())//
                    .getValue().doubleValue());
            datum.put(SIGMA_Y, fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.ar234U_238Ufc.getName())//
                    .getOneSigmaAbs().doubleValue() * 2.0);
            datum.put(RHO, 0.0);
            datum.put(SELECTED, "true");
        }

        myChart.setData(myData);
    }

    /**
     * @param selectedFractions the selectedFractions to set
     */
    public void setSelectedFractions(Vector<ETFractionInterface> selectedFractions) {
        this.selectedFractions = selectedFractions;
    }

    public void close() {
        topsoilEvolutionChartDialog.dispose();
        myChart = null;
    }
}
