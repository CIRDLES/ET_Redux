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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.AliquotDetailsDisplayInterface;
import org.earthtime.UPb_Redux.dateInterpretation.concordia.PlottingDetailsDisplayInterface;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.fractions.ETFractionInterface;
import org.earthtime.plots.PlotAxesSetupInterface;

/**
 *
 * @author James F. Bowring
 */
public abstract class AbstractTopsoilDisplay extends JLayeredPane implements AliquotDetailsDisplayInterface, PlottingDetailsDisplayInterface {

    protected Dimension myDimension = new Dimension(900, 600);

    protected Vector<ETFractionInterface> selectedFractions;
    protected Vector<ETFractionInterface> filteredFractions;
    protected Vector<ETFractionInterface> excludedFractions;
    
    protected ValueModel lambda235;
    protected ValueModel lambda238;
    protected ValueModel lambda230;
    
    protected Map<String, Object> propertiesForTopsoilPlot;
    protected List<Map<String, Object>> dataForTopsoilPlot;

    public AbstractTopsoilDisplay() {
        propertiesForTopsoilPlot = new HashMap<>();
        dataForTopsoilPlot = new ArrayList<>();
    }

    public void runme(Dimension myDimension) {
        this.myDimension = myDimension;
        setSize(myDimension);
        preparePanel(true, false);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                initAndShowGUI();
            }
        });
    }

    protected void initAndShowGUI() {
        JFXPanel fxPanel = new JFXPanel();

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                initFX(fxPanel);
            }
        });
    }

    protected void initFX(JFXPanel fxPanel) {
        // This method is invoked on the JavaFX thread
        Scene scene = createScene();
        fxPanel.setScene(scene);
        fxPanel.setSize(myDimension);

        // this forces resizing
        setLayout(new BorderLayout());
        add(fxPanel, BorderLayout.CENTER);
    }

    protected abstract Scene createScene();

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
        
    }

    @Override
    public abstract void preparePanel(boolean doReScale, boolean inLiveMode);

    @Override
    public void refreshPanel(boolean doReScale, boolean inLiveMode) {
    }

    @Override
    public void resetPanel(boolean doReScale, boolean inLiveMode) {
    }

    @Override
    public void setShowTightToEdges(boolean showTightToEdges) {
    }

    @Override
    public void performZoom(double factor) {
    }

    @Override
    public PlotAxesSetupInterface getCurrentPlotAxesSetup() {
        return null;
    }
}
