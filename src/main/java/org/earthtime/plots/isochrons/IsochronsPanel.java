/*
 * IsochronsPanel.java
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
package org.earthtime.plots.isochrons;

import org.earthtime.dataDictionaries.UThAnalysisMeasures;
import org.earthtime.plots.AbstractPlot;
import org.earthtime.reportViews.ReportUpdaterInterface;
import org.earthtime.samples.SampleInterface;

/**
 *
 * @author James F. Bowring
 */
public class IsochronsPanel extends AbstractPlot {


    /**
     * Creates a new instance of IsochronsPanel
     *
     * @param mySample
     * @param reportUpdater the value of reportUpdater
     */
    public IsochronsPanel(SampleInterface mySample, ReportUpdaterInterface reportUpdater) {
        super(mySample, reportUpdater);

        this.nameOfXaxisSourceValueModel = UThAnalysisMeasures.ar238U_232Thfc.getName();
        this.nameOfYaxisSourceValueModel = UThAnalysisMeasures.ar230Th_232Thfc.getName();
    }
}
