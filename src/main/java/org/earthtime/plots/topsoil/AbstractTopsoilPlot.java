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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.cirdles.topsoil.plot.JavaScriptPlot;
import org.cirdles.topsoil.plot.Plot;
import org.cirdles.topsoil.plot.internal.SVGSaver;

/**
 *
 * @author James F. Bowring
 */
public abstract class AbstractTopsoilPlot {

    protected Plot plot;

    protected AbstractTopsoilPlot() {
    }

    public abstract Pane initializePlotPane();

    /**
     * @return the plot
     */
    public Plot getPlot() {
        return plot;
    }

    public void setSelectedAllData(boolean selected) {
        for (Map<String, Object> datum : plot.getData()) {
            datum.replace("Selected", selected);
        }

        plot.setData(plot.getData());
    }

    public List<Node> toolbarControlsFactory() {
        List<Node> controls = new ArrayList<>();

        JavaScriptPlot javaScriptPlot = (JavaScriptPlot) plot;

        Text loadingIndicator = new Text("Loading...");
        javaScriptPlot.getLoadFuture().thenRunAsync(() -> {
            loadingIndicator.visibleProperty().bind(
                    javaScriptPlot.getWebEngine().getLoadWorker()
                            .stateProperty().isEqualTo(Worker.State.RUNNING));
        },
                Platform::runLater
        );

        Button saveToSVGButton = new Button("Save as SVG");
        saveToSVGButton.setOnAction(mouseEvent -> {
            new SVGSaver().save(javaScriptPlot.displayAsSVGDocument());
        });

        Button recenterButton = new Button("Re-center");
        recenterButton.setOnAction(mouseEvent -> {
            javaScriptPlot.recenter();
        });

        controls.add(loadingIndicator);
        controls.add(saveToSVGButton);
        controls.add(recenterButton);

        return controls;
    }

    protected enum SigmaPresentationModes {
        ONE_SIGMA_ABSOLUTE("1σ (abs)", 1.0),
        TWO_SIGMA_ABSOLUTE("2σ (abs)", 2.0),
        NINETY_FIVE_PERCENT_CONFIDENCE("95% Conf.", 2.4477);
        
        private String displayName;
        private double sigmaMultiplier;

        private SigmaPresentationModes(String displayName, double sigmaMultiplier) {
            this.displayName = displayName;
            this.sigmaMultiplier = sigmaMultiplier;
        }

        /**
         * @return the displayName
         */
        public String getDisplayName() {
            return displayName;
        }

        /**
         * @return the sigmaMultiplier
         */
        public double getSigmaMultiplier() {
            return sigmaMultiplier;
        }
    }

}
