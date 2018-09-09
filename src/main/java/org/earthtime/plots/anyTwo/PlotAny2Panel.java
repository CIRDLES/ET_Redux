/*
 * PlotAny2Panel.java
 *
 * Created on March 4, 2008, 3:08 PM
 * Revised for this use Decemebr 2016
 *
 * Copyright 2006-2018 James F. Bowring, CIRDLES.org, and Earth-Time.org
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
package org.earthtime.plots.anyTwo;

import java.util.Vector;
import org.earthtime.dataDictionaries.RadRatios;
import org.earthtime.fractions.ETFractionInterface;
import org.earthtime.plots.AbstractPlot;
import org.earthtime.reportViews.ReportUpdaterInterface;
import org.earthtime.samples.SampleInterface;

/**
 *
 * @author James F. Bowring
 */
public class PlotAny2Panel extends AbstractPlot {

    /**
     * Creates a new instance of PlotAny2Panel
     *
     * @param mySample
     * @param reportUpdater the value of reportUpdater
     */
    public PlotAny2Panel(SampleInterface mySample, ReportUpdaterInterface reportUpdater) {
        super(mySample, reportUpdater);

        this.nameOfXaxisSourceValueModel = RadRatios.r207_235r.getName();
        this.nameOfYaxisSourceValueModel = RadRatios.r206_238r.getName();
    }

    @Override
    public void setSelectedFractions(Vector<ETFractionInterface> selectedFractions) {
        super.setSelectedFractions(selectedFractions);
        fitMcLeanRegression();
    }
}
