/*
 * SampleDateInterpretationGUIOptions.java
 *
 * Created on May 28, 2008, 6:00 AM
 *
 *
 * Copyright 2006-2017 James F. Bowring and www.Earth-Time.org
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
package org.earthtime.UPb_Redux.user;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.earthtime.dataDictionaries.RadDates;

/**
 *
 * @author James F. Bowring
 */
public class SampleDateInterpretationGUIOptions
        implements Serializable {

    // Class variables
    private static final long serialVersionUID = 337325885289060859L;
    // instance variables
    private Map<String, String> concordiaOptions;
    private Map<String, String> weightedMeanOptions;
    private Map<String, Map<String, String>> aliquotOptions;
    private Map<String, String> heatMapOptions;
    private Map<String, String> probabilityChartOptions;
    private Map<String, String> uSeriesIsochronOptions;

    /**
     *
     */
    public SampleDateInterpretationGUIOptions() {
        // view settings
        concordiaOptions = new HashMap<>();

        // Concordia Flavor = C (Concordia) or T-W (Tera-Wasserberg) or Th thorium (June 2014)
        concordiaOptions.put("concordiaFlavor", "C");
        concordiaOptions.put("showEllipseCenters", "true");
        concordiaOptions.put("showEllipseLabels", "false");
        concordiaOptions.put("showConcordiaErrors", "true");
        concordiaOptions.put("showExcludedEllipses", "true");
        concordiaOptions.put("showFilteredEllipses", "false");
        // suffix can be _Th, _Pa, _Th_Pa, or ""
        concordiaOptions.put("display_r206_238r_Th", "false");
        concordiaOptions.put("display_r206_238r_Pa", "false");

        // oct 2014 common lead correction
        concordiaOptions.put("display_PbcCorr", "false");
        // Ellipses
        // sets error 1=1sigma, 2=2sigma, 2.4477=95%
        concordiaOptions.put("ellipseSize", "2");
        // ellipse labels font
        concordiaOptions.put("ellipseLabelFont", "Monospaced");
        concordiaOptions.put("ellipseLabelFontSize", "12");
        // Concordia
        //Concordia errorStyle "dotted" or "shaded"
        concordiaOptions.put("concordiaErrorStyle", "shaded");
        // Concordia line weight  
        concordiaOptions.put("concordiaLineWeight", "1.5");
        // Concordia line color r,g,b
        concordiaOptions.put("concordiaLineColor", "0, 0, 255");
        // Concordia tick shape: line, square, circle
        concordiaOptions.put("concordiaTicShape", "circle");
        // Concordia tic weight
        concordiaOptions.put("concordiaTicWeight", "6");
        // Concordia labels font
        concordiaOptions.put("concordiaLabelFont", "Monospaced");
        concordiaOptions.put("concordiaLabelFontSize", "12");
        //june 2014
        concordiaOptions.put("useUncertaintyCrosses", "false");

        // intercept line
        concordiaOptions.put("interceptLineWeight", "1.5");
        concordiaOptions.put("interceptLineColor", "0, 0, 0");
        concordiaOptions.put("interceptErrorLineStyle", "dashed");
        concordiaOptions.put("truncateRegressionCurves", "false");
        // Axes
        // Axes tic labels font
        concordiaOptions.put("axesTicLabelFont", "Monospaced");
        concordiaOptions.put("axesTicLabelFontSize", "12");
        // Axes labels font
        concordiaOptions.put("axesLabelFont", "Monospaced");
        concordiaOptions.put("axesLabelFontSize", "20");

        // Title
        // Title font
        concordiaOptions.put("titleFont", "Monospaced");
        concordiaOptions.put("titleFontSize", "18");
        // Subtitle text
        concordiaOptions.put("subTitleText", "");
        // Title box location and visibility
        concordiaOptions.put("titleBoxX", "100");
        concordiaOptions.put("titleBoxY", "50");
        concordiaOptions.put("titleBoxShow", "true");

        // Weighted means options
        weightedMeanOptions = new HashMap<>();
        // set fraction sort order = weight, name, random, date
        weightedMeanOptions.put("fractionSortOrder", "weight");
        // store a string of binary digits representing each aliquot by number for turned on or off
        weightedMeanOptions.put("weighted mean 207Pb/235U", "0");
        weightedMeanOptions.put("weighted mean 206Pb/238U", "0");
        weightedMeanOptions.put("weighted mean 207Pb/206Pb", "0");
        weightedMeanOptions.put("weighted mean 208Pb/232Th", "0");
        weightedMeanOptions.put("weighted mean 206Pb/238U (Th-corrected)", "0");
        weightedMeanOptions.put("weighted mean 207Pb/235U (Pa-corrected)", "0");
        weightedMeanOptions.put("weighted mean 207Pb/206Pb (Th-corrected)", "0");
        weightedMeanOptions.put("weighted mean 207Pb/206Pb (Pa-corrected)", "0");
        weightedMeanOptions.put("weighted mean 207Pb/206Pb (Th- and Pa-corrected)", "0");

        // Aliquot options
        aliquotOptions = new HashMap<>();

        initializeHeatMapOptions();
        initializeProbabilityChartOptions();
    }

    private void initializeUSeriesIsochronOptions(){
        uSeriesIsochronOptions = new HashMap<>();

        uSeriesIsochronOptions.put("showEquiline", "true");
        uSeriesIsochronOptions.put("showEllipseLabels", "false");
        uSeriesIsochronOptions.put("showEllipseCenters", "true");
        uSeriesIsochronOptions.put("showExcludedEllipses", "true");
        uSeriesIsochronOptions.put("showRegressionLine", "true");
        uSeriesIsochronOptions.put("showRegressionUnct", "false");
    }
    
    private void initializeHeatMapOptions() {
        //heatMapOptions; see HeatMap class
        heatMapOptions = new HashMap<>();
        heatMapOptions.put("leftShift", "0");
        heatMapOptions.put("rightShift", "0");
        heatMapOptions.put("reportColumnDisplayName", "");
        heatMapOptions.put("activateHeatMap", "false");

    }

    private void initializeProbabilityChartOptions() {
        // oct 2014
        probabilityChartOptions = new HashMap<>();
        probabilityChartOptions.put("uncertaintyPerCentSliderValue", "100");
        probabilityChartOptions.put("positivePerCentDiscordanceSliderValue", "100");
        probabilityChartOptions.put("negativePerCentDiscordanceSliderValue", "-100");
        probabilityChartOptions.put("chosenDateName", RadDates.age207_206r.getName());
        probabilityChartOptions.put("correctedForPbc", "false");
        probabilityChartOptions.put("showHistogram", "true");
        
    }

    private Map<String, String> CreateAliquotOptionsMap(String aliquotName, int aliquotNumber) {
        Map<String, String> retVal = new HashMap<String, String>();

        getAliquotOptions().put(aliquotName, retVal);

        String includedBorderRGB = "0,0,0";

        String includedFillRGB = "0,0,0";

        int primary = (aliquotNumber - 1) % 6;// altered oct 2010 to make predicatable (int)Math.floor( Math.random() * 5.999);

        // red, green, blue, magenta, cyan, yellow
        switch (primary) {
            case 0:
                includedFillRGB = "255" + ", " + "0" + ", " + "0";
                break;
            case 1:
                includedFillRGB = "0" + ", " + "255" + ", " + "0";
                break;
            case 2:
                includedFillRGB = "0" + ", " + "0" + ", " + "255";
                break;
            case 3:
                includedFillRGB = "255" + ", " + "0" + ", " + "255";
                break;
            case 4:
                includedFillRGB = "0" + ", " + "255" + ", " + "0";
                break;
            case 5:
                includedFillRGB = "255" + ", " + "255" + ", " + "0";
                break;
        }

        // included ellipses
        // line weight
        retVal.put("includedBorderWeight", "2.5");
        retVal.put("includedBorderColor", includedBorderRGB);
        retVal.put("includedFillColor", includedFillRGB);
        retVal.put("includedCenterColor", includedBorderRGB);
        retVal.put("includedFillTransparencyPCT", "35");
        retVal.put("includedCenterSize", "4");

        // excluded ellipses
        // line weight
        retVal.put("excludedBorderWeight", "1.5");
        retVal.put("excludedBorderColor", "0, 0, 0");
        retVal.put("excludedFillColor", "50, 50, 50");
        retVal.put("excludedCenterColor", "0, 0, 0");
        retVal.put("excludedFillTransparencyPCT", "15");
        retVal.put("excludedCenterSize", "3");

        // Preferred Date Box
        // font
        retVal.put("dateFont", "Monospaced");
        retVal.put("dateFontSize", "12");
        retVal.put("dateShowDate", "true");
        retVal.put("dateShowMSWD", "true");
        retVal.put("dateShowN", "true");

        // box location and visibility
        retVal.put("dateBoxX", "100");
        retVal.put("dateBoxY", "150");
        retVal.put("visibleDateBoxOutline", "true");

        return retVal;
    }

    /**
     *
     * @param aliquotName
     * @param aliquotNumber
     * @return
     */
    public Map<String, String> getAliquotOptionsMapByName(String aliquotName, int aliquotNumber) {

        Map<String, String> retVal;

        try {
            retVal = ((Map<String, Map<String, String>>) getAliquotOptions()).get(aliquotName);
        } catch (Exception ex) {
            retVal = null;
        }

        if (retVal == null) {
            retVal = CreateAliquotOptionsMap(aliquotName, aliquotNumber);
        }
        return retVal;
    }

    /**
     *
     * @param aliquotName
     * @param aliquotOptions
     */
    public void setAliquotOptionsByName(String aliquotName, Map<String, String> aliquotOptions) {
        getAliquotOptions().remove(aliquotName);
        getAliquotOptions().put(aliquotName, aliquotOptions);
    }

    /**
     *
     * @return
     */
    public Map<String, String> getConcordiaOptions() {
        if (concordiaOptions == null) {
            concordiaOptions = new SampleDateInterpretationGUIOptions().getConcordiaOptions();
        }
        return concordiaOptions;
    }

    /**
     *
     * @param concordiaOptions
     */
    public void setConcordiaOptions(Map<String, String> concordiaOptions) {
        this.concordiaOptions = concordiaOptions;
    }

    /**
     *
     * @return
     */
    public Map<String, String> getWeightedMeanOptions() {
        if (weightedMeanOptions == null) {
            weightedMeanOptions = new SampleDateInterpretationGUIOptions().getWeightedMeanOptions();
        }
        return weightedMeanOptions;
    }

    /**
     *
     * @param weightedMeanOptions
     */
    public void setWeightedMeanOptions(Map<String, String> weightedMeanOptions) {
        this.weightedMeanOptions = weightedMeanOptions;
    }

    /**
     *
     * @return
     */
    public Map<String, Map<String, String>> getAliquotOptions() {
        if (aliquotOptions == null) {
            setAliquotOptions(new HashMap<String, Map<String, String>>());
        }

        return aliquotOptions;
    }

    /**
     *
     * @param aliquotOptions
     */
    public void setAliquotOptions(Map<String, Map<String, String>> aliquotOptions) {
        this.aliquotOptions = aliquotOptions;
    }

    /**
     * @return the heatMapOptions
     */
    public Map<String, String> getHeatMapOptions() {
        if (heatMapOptions == null) {
            initializeHeatMapOptions();
        }
        return heatMapOptions;
    }

    /**
     * @param rainbowOptions the heatMapOptions to set
     */
    public void setHeatMapOptions(Map<String, String> rainbowOptions) {
        this.heatMapOptions = rainbowOptions;
    }

    /**
     * @return the probabilityChartOptions
     */
    public Map<String, String> getProbabilityChartOptions() {
        if (probabilityChartOptions == null) {
            initializeProbabilityChartOptions();
        }
        return probabilityChartOptions;
    }

    /**
     * @param probabilityChartOptions the probabilityChartOptions to set
     */
    public void setProbabilityChartOptions(Map<String, String> probabilityChartOptions) {
        this.probabilityChartOptions = probabilityChartOptions;
    }

    /**
     * @return the uSeriesIsochronOptions
     */
    public Map<String, String> getuSeriesIsochronOptions() {
        return uSeriesIsochronOptions;
    }

    /**
     * @param uSeriesIsochronOptions the uSeriesIsochronOptions to set
     */
    public void setuSeriesIsochronOptions(Map<String, String> uSeriesIsochronOptions) {
        this.uSeriesIsochronOptions = uSeriesIsochronOptions;
    }

}
