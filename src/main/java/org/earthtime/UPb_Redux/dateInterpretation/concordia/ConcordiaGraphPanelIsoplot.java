/*
 * ConcordiaGraphPanelIsoplot.java
 *
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
package org.earthtime.UPb_Redux.dateInterpretation.concordia;

import java.util.Map;
import java.util.Vector;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import org.earthtime.UPb_Redux.samples.Sample;
import org.earthtime.fractions.ETFractionInterface;

/**
 *
 * @author James F. Bowring
 */
public class ConcordiaGraphPanelIsoplot extends JFXPanel
        implements AliquotDetailsDisplayInterface, PlottingDetailsDisplayInterface {

    // Class Variables
    // Instance Variables
    private final Sample sample;
    private ObservableList observableList;
    private ObservableList observableSequence;
    private Vector<ETFractionInterface> selectedFractions;
    private Vector<ETFractionInterface> excludedFractions;

    /**
     *
     * @param mySample
     */
    public ConcordiaGraphPanelIsoplot(Sample mySample) {
        super();

        this.sample = mySample;
////        
////        selectedFractions = sample.getUPbFractions();
////        
////        Platform.runLater(new Runnable() {
////
////            @Override
////            public void run() {
////                org.cirdles.isoplot.chart.NumberNumberChart chart = new ConcordiaChart();
////                VBox.setVgrow(chart, Priority.ALWAYS);
////
////                org.cirdles.isoplot.chart.XYChart.Series observableDataSeries = //
////                        new org.cirdles.isoplot.chart.XYChart.Series<>();
////
////                observableList = FXCollections.observableArrayList(observableDataSeries);
////                observableSequence = ((org.cirdles.isoplot.chart.XYChart.Series) observableList.get(0)).getData();
////                observableSequence.addListener(new ListChangeListener() {
////
////                    @Override
////                    public void onChanged(ListChangeListener.Change change) {
////                        //System.out.println("Detected a change! ");
////                    }
////                });
////
////                chart.setData(observableList);
////
////                preparePanel();
////
////                VBox root = new VBox(new AxisConfigurationToolBar((org.cirdles.isoplot.chart.NumberAxis) chart.getXAxis()),
////                        new AxisConfigurationToolBar((org.cirdles.isoplot.chart.NumberAxis) chart.getYAxis()),
////                        chart);
////
////                Scene scene = new Scene(root, 1200, 800, true, SceneAntialiasing.BALANCED);
////
////                setScene(scene);
////            }
////        });
    }

    @Override
    public Map<String, Map<String, String>> getAliquotOptions() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getConcordiaFlavor() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<String, String> getConcordiaOptions() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param concordiaOptions
     */
    @Override
    public void setConcordiaOptions(Map<String, String> concordiaOptions) {
        //this.concordiaOptions = concordiaOptions;
    }

    @Override
    public Vector<ETFractionInterface> getDeSelectedFractions() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<String, String> getSelectedAliquotOptions() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     *
     * @param fractions
     */
    @Override
    public void setSelectedFractions(Vector<ETFractionInterface> fractions) {
        this.selectedFractions = fractions;
    }

    @Override
    public Vector<ETFractionInterface> getSelectedFractions() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void preparePanel(boolean doReScale) {

////        String r206_238r_corrected = "r206_238r";
////        String r207_235r_corrected = "r207_235r";
////        String r207_206r_corrected = "r207_206r";
////
////        String rhoTW = "rhoR207_206r__r238_206r";
////        String rhoConcordia = "rhoR206_238r__r207_235r";
////
////        Vector<Fraction> fractions = selectedFractions;// sample.getUPbFractions();//  
////        for (Fraction fraction : fractions) {
////            if (!((UPbFractionI) fraction).isRejected()) {
////
////                double xAxisRatio = fraction.getRadiogenicIsotopeRatioByName(r207_235r_corrected).getValue().doubleValue();
////                double yAxisRatio = fraction.getRadiogenicIsotopeRatioByName(r206_238r_corrected).getValue().doubleValue();
////
////                double xAxisRatioUnct = 2.0 * fraction.getRadiogenicIsotopeRatioByName(r207_235r_corrected).getOneSigmaAbs().doubleValue();
////                double yAxisRatioUnct = 2.0 * fraction.getRadiogenicIsotopeRatioByName(r206_238r_corrected).getOneSigmaAbs().doubleValue();
////
////                double correlationCoefficient = fraction.getRadiogenicIsotopeRatioByName(rhoConcordia).getValue().doubleValue();
////
////                if ((xAxisRatio > 0.0) && (yAxisRatio > 0.0) && (correlationCoefficient >= -1.0) && (correlationCoefficient <= 1.0)) {
////
////                    org.cirdles.isoplot.chart.concordia.ErrorEllipse errorEllipseNode = //
////                            new org.cirdles.isoplot.chart.concordia.ErrorEllipse(xAxisRatio, yAxisRatio, xAxisRatioUnct, yAxisRatioUnct, correlationCoefficient);
////                    
////                    ((UPbFractionI)fraction).setErrorEllipseNode(errorEllipseNode);
////
////                    observableSequence.add(errorEllipseNode);
////
//////                        System.out.println("{" + xAxisRatio + ", " + yAxisRatio + ", " + xAxisRatioUnct + ", " + yAxisRatioUnct + ", " + correlationCoefficient + "},");
////                }
////            }
////        }
    }

    @Override
    public void refreshPanel(boolean doReScale) {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                observableSequence.clear();//.remove(0);
                preparePanel(doReScale);
            }
        });

    }

    @Override
    public void resetPanel(boolean doReScale) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setFilteredFractions(Vector<ETFractionInterface> filteredFractions) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isShowFilteredEllipses() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setShowFilteredEllipses(boolean showFilteredEllipses) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
