package org.earthtime.plots.topsoil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import static org.cirdles.topsoil.app.dataset.field.Fields.SELECTED;
import static org.cirdles.topsoil.app.plot.variable.Variables.RHO;
import static org.cirdles.topsoil.app.plot.variable.Variables.SIGMA_X;
import static org.cirdles.topsoil.app.plot.variable.Variables.SIGMA_Y;
import static org.cirdles.topsoil.app.plot.variable.Variables.X;
import static org.cirdles.topsoil.app.plot.variable.Variables.Y;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.ELLIPSES;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.ELLIPSE_FILL_COLOR;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.EVOLUTION_MATRIX;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.ISOTOPE_TYPE;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.LAMBDA_Th230;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.LAMBDA_U235;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.LAMBDA_U238;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.REGRESSION_ENVELOPE;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.REGRESSION_LINE;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.TITLE;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.UNCERTAINTY;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.X_AXIS;
import static org.cirdles.topsoil.plot.base.BasePlotProperties.Y_AXIS;
import org.earthtime.UTh_Redux.fractions.UThLegacyFractionI;
import org.earthtime.dataDictionaries.Lambdas;
import org.earthtime.dataDictionaries.UThAnalysisMeasures;

public class TopsoilDisplayEvolutionPlot extends AbstractTopsoilDisplay {
    
    private String title;

    public TopsoilDisplayEvolutionPlot(String title) {
        super();
        this.title = title;
    }

    @Override
    protected Scene createScene() {
        AbstractTopsoilPlot topsoilPlot = new TopsoilPlotEvolution(propertiesForTopsoilPlot, dataForTopsoilPlot);

        Pane topsoilPlotUI = topsoilPlot.initializePlotPane();

        Scene topsoilPlotScene = new Scene(topsoilPlotUI, myDimension.width, myDimension.height);

        return (topsoilPlotScene);
    }

    @Override
    public void preparePanel(boolean doReScale, boolean inLiveMode) {

        if (selectedFractions.size() > 0) {

            propertiesForTopsoilPlot.put(TITLE, title);//"Evolution Plot");
            propertiesForTopsoilPlot.put(X_AXIS, "[230Th/238U]");
            propertiesForTopsoilPlot.put(Y_AXIS, "[234U/238U]");
            propertiesForTopsoilPlot.put(EVOLUTION_MATRIX, true);
            propertiesForTopsoilPlot.put(ELLIPSES, true);
            propertiesForTopsoilPlot.put(ISOTOPE_TYPE, "Uranium Thorium");
            propertiesForTopsoilPlot.put(ELLIPSE_FILL_COLOR, "red");
            propertiesForTopsoilPlot.put(REGRESSION_LINE, false);
            propertiesForTopsoilPlot.put(REGRESSION_ENVELOPE, false);
            propertiesForTopsoilPlot.put(UNCERTAINTY, 2.0);

            lambda235 = selectedFractions.get(0)
                    .getPhysicalConstantsModel().getDatumByName(Lambdas.lambda235.getName());
            lambda238 = selectedFractions.get(0)
                    .getPhysicalConstantsModel().getDatumByName(Lambdas.lambda238.getName());
            lambda230 = selectedFractions.get(0)
                    .getPhysicalConstantsModel().getDatumByName(Lambdas.lambda230.getName());

            propertiesForTopsoilPlot.put(LAMBDA_U235, lambda235.getValue().doubleValue());
            propertiesForTopsoilPlot.put(LAMBDA_U238, lambda238.getValue().doubleValue());
            propertiesForTopsoilPlot.put(LAMBDA_Th230, lambda230.getValue().doubleValue());

            dataForTopsoilPlot = new ArrayList<>();
            for (int i = 0; i < selectedFractions.size(); i++) {
                UThLegacyFractionI fraction = (UThLegacyFractionI) selectedFractions.get(i);
                Map<String, Object> datum = new HashMap<>();
                dataForTopsoilPlot.add(datum);
                datum.put(X.getName(), fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.ar230Th_238Ufc.getName())//
                        .getValue().doubleValue());
                datum.put(SIGMA_X.getName(), fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.ar230Th_238Ufc.getName())//
                        .getOneSigmaAbs().doubleValue() * 1.0);
                datum.put(Y.getName(), fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.ar234U_238Ufc.getName())//
                        .getValue().doubleValue());
                datum.put(SIGMA_Y.getName(), fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.ar234U_238Ufc.getName())//
                        .getOneSigmaAbs().doubleValue() * 1.0);
                datum.put(RHO.getName(), 0.0);
                datum.put(SELECTED.getName(), true);
            }

        }
    }
}
