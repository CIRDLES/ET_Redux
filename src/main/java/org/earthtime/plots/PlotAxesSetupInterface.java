/*
 * Copyright 2006-2018 James F. Bowring, CIRDLES.org, and Earth-Time.org
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
package org.earthtime.plots;

import java.awt.Font;
import java.awt.Graphics2D;
import org.earthtime.UPb_Redux.dateInterpretation.graphPersistence.AxisSetup;

/**
 *
 * @author CIRDLES.org
 */
public interface PlotAxesSetupInterface {

    public AxisSetup getXaxisSetup();

    public AxisSetup getYaxisSetup();

    public int getcDecimalPlaceCount();

    public int getcTicIncrement();

    public int getcTicLabelFrequency();

    public void setUseAutomaticAxisTics(boolean useAutomaticAxisTics);

    public boolean isyAxisHorizontalTicLabels();

    public void setyAxisHorizontalTicLabels(boolean yAxisHorizontalTicLabels);

    public void setcDecimalPlaceCount(int cDecimalPlaceCount);

    public void setcTicIncrement(int cTicIncrement);

    public void setcTicLabelFrequency(int cTicLabelFrequency);

    public double mapX(double x);

    public double mapY(double y);

    public boolean isDoPlotting();

    public void setDoPlotting(boolean doPlotting);

    public int getTopMargin();

    public void setTopMargin(int topMargin);

    public int getLeftMargin();

    public void setLeftMargin(int leftMargin);

    public double getGraphWidth();

    public void setGraphWidth(double graphWidth);

    public double getGraphHeight();

    public void setGraphHeight(double graphHeight);

    //todo move this
    public void plotXYaxes( //
            Graphics2D g2d, Font axisLabelAtomicNum, Font axisLabelAtomicName, boolean thoriumCorrected, boolean protactiniumCorrected, boolean commonLeadCorrected);

}
