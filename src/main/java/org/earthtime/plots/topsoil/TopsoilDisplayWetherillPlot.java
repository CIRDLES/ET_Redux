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

import java.util.Map;
import java.util.Vector;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import org.earthtime.fractions.ETFractionInterface;

/**
 *
 * @author James F. Bowring
 */
public class TopsoilDisplayWetherillPlot extends AbstractTopsoilDisplay {

    public TopsoilDisplayWetherillPlot() {
        super();
    }

    protected Scene createScene() {
        AbstractTopsoilPlot topsoilPlot = new TopsoilPlotWetherill("Wetherill Plot");
        Pane topsoilPlotUI = topsoilPlot.initializePlotPane();

        Scene topsoilPlotScene = new Scene(topsoilPlotUI, myDimension.width, myDimension.height);

        return (topsoilPlotScene);
    }

    @Override
    public void preparePanel(boolean doReScale, boolean inLiveMode) {

        if (selectedFractions.size() > 0) {
        }
    }

}
