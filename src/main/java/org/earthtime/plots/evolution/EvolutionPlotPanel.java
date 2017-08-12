/*
 * Copyright 2017 CIRDLES.
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
package org.earthtime.plots.evolution;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javafx.embed.swing.JFXPanel;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import org.cirdles.topsoil.app.plot.TopsoilPlotType;
import org.cirdles.topsoil.plot.Plot;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.ELLIPSES;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.EVOLUTION_MATRIX;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.ISOTOPE_TYPE;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.LAMBDA_Th230;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.LAMBDA_U235;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.LAMBDA_U238;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.TITLE;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.X_AXIS;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.Y_AXIS;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.AliquotDetailsDisplayInterface;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.ConcordiaPlotDisplayInterface;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.PlottingDetailsDisplayInterface;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.UTh_Redux.fractions.UThLegacyFractionI;
import org.earthtime.dataDictionaries.Lambdas;
import org.earthtime.dataDictionaries.UThAnalysisMeasures;
import org.earthtime.fractions.ETFractionInterface;
import org.earthtime.plots.PlotAxesSetupInterface;
import org.earthtime.reportViews.ReportUpdaterInterface;
import org.earthtime.samples.SampleInterface;

/**
 *
 * @author James F. Bowring
 */
public final class EvolutionPlotPanel extends JLayeredPane implements AliquotDetailsDisplayInterface, ConcordiaPlotDisplayInterface, PlottingDetailsDisplayInterface {

    protected transient ReportUpdaterInterface reportUpdater;

    private static final String X = "x";
    private static final String SIGMA_X = "sigma_x";
    private static final String Y = "y";
    private static final String SIGMA_Y = "sigma_y";
    private static final String RHO = "rho";
    private static final String SELECTED = "Selected";

    private static SampleInterface sample;
    private static boolean showMatrix = true;
    private ValueModel lambda235;
    private ValueModel lambda238;
    private ValueModel lambda230;
    private static Vector<ETFractionInterface> selectedFractions;
    private Vector<ETFractionInterface> filteredFractions;
    private Vector<ETFractionInterface> excludedFractions;

    private JComponent plotAsComponent;
    private Plot myEvolutionPlot;//= TopsoilPlotType.BASE_PLOT.getPlot();

    public EvolutionPlotPanel(SampleInterface mySample, ReportUpdaterInterface reportUpdater) {
        super();

        myEvolutionPlot = TopsoilPlotType.BASE_PLOT.getPlot();
        this.sample = mySample;
        this.reportUpdater = reportUpdater;

        selectedFractions = new Vector<>();
        excludedFractions = new Vector<>();

        setOpaque(true);

        setBackground(Color.white);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        paint((Graphics2D) g, false);
    }

    public void paint(Graphics2D g2d, boolean svgStyle) {

//            g2d.drawString("NO VALID DATA!", 200, 300);
//            g2d.drawString("Axis ratios must be > 0 and rho must be in [-1,...,1].", 200, 330);
    }

    @Override
    public void refreshPanel(boolean doReScale, boolean inLiveMode) {

        preparePanel(doReScale, false);
    }

    @Override
    public void preparePanel(boolean doReScale, boolean inLiveMode) {

//        removeAll();
        if (selectedFractions.size() > 0) {
            myEvolutionPlot = TopsoilPlotType.BASE_PLOT.getPlot();

            myEvolutionPlot.getProperties().put(TITLE, "Evolution Plot");
            myEvolutionPlot.getProperties().put(X_AXIS, "[230Th/238U]");
            myEvolutionPlot.getProperties().put(Y_AXIS, "[234U/238U]");
            myEvolutionPlot.getProperties().put(EVOLUTION_MATRIX, true);
            myEvolutionPlot.getProperties().put(ELLIPSES, true);
            myEvolutionPlot.getProperties().put(ISOTOPE_TYPE, "Uranium Thorium");

            lambda235 = selectedFractions.get(0)
                    .getPhysicalConstantsModel().getDatumByName(Lambdas.lambda235.getName());
            lambda238 = selectedFractions.get(0)
                    .getPhysicalConstantsModel().getDatumByName(Lambdas.lambda238.getName());
            lambda230 = selectedFractions.get(0)
                    .getPhysicalConstantsModel().getDatumByName(Lambdas.lambda230.getName());

            myEvolutionPlot.getProperties().put(LAMBDA_U235, lambda235.getValue().doubleValue());
            myEvolutionPlot.getProperties().put(LAMBDA_U238, lambda238.getValue().doubleValue());
            myEvolutionPlot.getProperties().put(LAMBDA_Th230, lambda230.getValue().doubleValue());

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

            try {
                myEvolutionPlot.setData(myData);
            } catch (Exception e) {
            }

            if (plotAsComponent == null) {

                plotAsComponent = myEvolutionPlot.displayAsJComponent();
                plotAsComponent.setBounds(getBounds());
                plotAsComponent.setBounds(0, 0, 820, 640);

                add(plotAsComponent, JLayeredPane.DEFAULT_LAYER);
            }
            remove(plotAsComponent);
            add(plotAsComponent, JLayeredPane.DEFAULT_LAYER);

            ((JFXPanel) plotAsComponent).revalidate();//   .repaint();
            ((JFXPanel) plotAsComponent).repaint();

            repaint();
            validate();

        }

        // repaint();
    }

    public void recenter() {
        try {
            myEvolutionPlot.recenter();
        } catch (Exception e) {
//            TODO solve threading issue
        }
    }

    public void toggleMatrix() {
        showMatrix = !showMatrix;
        try {
            myEvolutionPlot.setProperty(EVOLUTION_MATRIX, showMatrix);
        } catch (Exception e) {
//            TODO solve threading issue
        }
    }

    public void cancelFXThread() {
        if (myEvolutionPlot != null) {
            myEvolutionPlot.cancelFXApplicationThread();
        }
        plotAsComponent = null;
        myEvolutionPlot = null;
    }

    @Override
    public Map<String, Map<String, String>> getAliquotOptions() {
        return null;
    }

    @Override
    public Vector<ETFractionInterface> getDeSelectedFractions() {
        return null;
    }

    @Override
    public void setDeSelectedFractions(Vector<ETFractionInterface> deSelectedFractions) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<String, String> getSelectedAliquotOptions() {
        return null;
    }

    @Override
    public void setSelectedFractions(Vector<ETFractionInterface> fractions) {
        selectedFractions = fractions;
    }

    @Override
    public void setFilteredFractions(Vector<ETFractionInterface> filteredFractions) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Vector<ETFractionInterface> getSelectedFractions() {
        return selectedFractions;
    }

    @Override
    public boolean isShowFilteredEllipses() {
        return false;
    }

    @Override
    public void setShowFilteredEllipses(boolean showFilteredEllipses) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getConcordiaFlavor() {
        return null;
    }

    @Override
    public Map<String, String> getConcordiaOptions() {
        return null;
    }

    @Override
    public void setConcordiaOptions(Map<String, String> concordiaOptions) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

//    @Override
//    public void refreshPanel(boolean doReScale, boolean inLiveMode) {
//        preparePanel(doReScale, inLiveMode);
//    }
    @Override
    public void resetPanel(boolean doReScale, boolean inLiveMode) {
        refreshPanel(doReScale, inLiveMode);
    }

    @Override
    public void setShowTightToEdges(boolean showTightToEdges) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void performZoom(double factor) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PlotAxesSetupInterface getCurrentPlotAxesSetup() {
        return null;
    }

}
