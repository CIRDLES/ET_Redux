/*
 * TabbedReportViews.java
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
package org.earthtime.UPb_Redux.reports.reportViews;

import java.awt.Color;
import java.io.File;
import javax.swing.JLayeredPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.earthtime.ETReduxFrame;
import org.earthtime.UPb_Redux.reports.ReportSettings;
import org.earthtime.UPb_Redux.samples.Sample;
import org.earthtime.exceptions.ETException;
import org.earthtime.samples.SampleInterface;

/**
 *
 * @author James F. Bowring
 */
public class TabbedReportViews extends JTabbedPane {

    private SampleInterface sample;
    private JLayeredPane viewTabulatedAliquotActiveFractions;
    private JLayeredPane viewTabulatedAliquotRejectedFractions;
    private ETReduxFrame parentFrame;

    /**
     *
     * @param parentFrame
     */
    public TabbedReportViews(ETReduxFrame parentFrame) {
        super();
        this.parentFrame = parentFrame;
    }

    /**
     *
     * @param parentFrame
     * @param sample
     */
    public TabbedReportViews(ETReduxFrame parentFrame, Sample sample) {
        this(parentFrame);

        this.sample = sample;

    }

    /**
     *
     */
    public void initializeTabs() {

        setBackground(Color.white);

        viewTabulatedAliquotActiveFractions = new ReportAliquotFractionsView(parentFrame, true);
        add("Active Fractions", viewTabulatedAliquotActiveFractions);

        viewTabulatedAliquotRejectedFractions = new ReportAliquotFractionsView(parentFrame, false);
        add("Rejected Fractions", viewTabulatedAliquotRejectedFractions);

        prepareTabs();

        addChangeListener(new ChangeListener() {
            // This method is called whenever the selected tab changes

            public void stateChanged(ChangeEvent evt) {
//                int tab = ((JTabbedPane) evt.getSource()).getSelectedIndex();
//                if ( tab == 0 ) {
//                    ((ReportAliquotFractionsView) viewTabulatedAliquotActiveFractions).refreshPanel();
//                } else if ( tab == 1 ) {
//                    ((ReportAliquotFractionsView) viewTabulatedAliquotRejectedFractions).refreshPanel();
//                }

//////                jan 2015 not needed? revalidate();
            }
        });
    }

    /**
     *
     */
    public void prepareTabs() {

        // default is active fractions
////        setSelectedIndex(0);

        if (sample != null) {
            ((ReportAliquotFractionsView) viewTabulatedAliquotActiveFractions).setSample(sample);
//            ((ReportAliquotFractionsView) viewTabulatedAliquotActiveFractions).preparePanel();

            ((ReportAliquotFractionsView) viewTabulatedAliquotRejectedFractions).setSample(sample);
//            ((ReportAliquotFractionsView) viewTabulatedAliquotRejectedFractions).preparePanel();

            refreshTabs();

            validate();
        }
    }

    /**
     *
     */
    public void refreshTabs() {
        if (sample != null) {
            ((ReportAliquotFractionsView) viewTabulatedAliquotActiveFractions).refreshPanel();
            this.setTitleAt(0, "Active Fractions (" + Integer.toString(((ReportAliquotFractionsView) viewTabulatedAliquotActiveFractions).getReportFractions().length - ReportSettings.FRACTION_DATA_START_ROW) + ")");

            ((ReportAliquotFractionsView) viewTabulatedAliquotRejectedFractions).refreshPanel();
            this.setTitleAt(1, "Rejected Fractions (" + Integer.toString(((ReportAliquotFractionsView) viewTabulatedAliquotRejectedFractions).getReportFractions().length - ReportSettings.FRACTION_DATA_START_ROW) + ")");
        }
    }

    /**
     * @param sample the sample to set
     */
    public void setSample(SampleInterface sample) {
        this.sample = sample;
    }

    /**
     *
     * @return
     */
    public int getActiveFractionsSortedColumn() {
        return ((ReportAliquotFractionsView) getViewTabulatedAliquotActiveFractions()).sortedColumnNumber;
    }

    /**
     *
     * @return
     */
    public int getActiveFractionsSortedColumnDirection() {
        return ((ReportAliquotFractionsView) getViewTabulatedAliquotActiveFractions()).sortedColumnDirection;
    }

    /**
     *
     * @param fileNameForSVGPDF
     * @param outputPDF
     * @return
     */
    public File createReportSVGandPDF(String fileNameForSVGPDF, boolean outputPDF)
            throws ETException {
        return ((ReportAliquotFractionsView) getViewTabulatedAliquotActiveFractions()).createReportSVGandPDF(fileNameForSVGPDF, outputPDF);
    }

    /**
     * @return the viewTabulatedAliquotActiveFractions
     */
    public JLayeredPane getViewTabulatedAliquotActiveFractions() {
        return viewTabulatedAliquotActiveFractions;
    }
}
