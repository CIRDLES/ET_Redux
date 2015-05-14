/*
 * HeatMapMapper.java
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
package org.earthtime.colorViews;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Vector;
import org.earthtime.UPb_Redux.fractions.Fraction;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFractionI;
import org.earthtime.UPb_Redux.reports.ReportColumn;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.colorModels.HeatMap;
import org.earthtime.samples.SampleInterface;

/**
 *
 * @author James F. Bowring
 */
public class HeatMapMapper {

    private final SampleInterface sample;
    private ReportColumn selectedReportColumn;

    private double min;
    private double max;

    /**
     *
     * @param sample
     * @param selectedReportColumn
     */
    public HeatMapMapper(SampleInterface sample, ReportColumn selectedReportColumn) {
        this.sample = sample;
        this.selectedReportColumn = selectedReportColumn;
    }

    /**
     *
     * @param heatMapActive
     * @return
     */
    public ArrayList<Integer> determineFractionHeat(boolean heatMapActive) {
        min = Double.MAX_VALUE;
        max = -Double.MAX_VALUE;

        Method meth = null;
        // assign rgb color
        ArrayList<Integer> fractionRGBs = new ArrayList<>();

        try {
            Class<?> fractionClass =//
                    Class.forName(Fraction.class.getCanonicalName());
            meth = fractionClass.getMethod(//
                    selectedReportColumn.getRetrieveMethodName(),
                    new Class[]{String.class});
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException classNotFoundException) {
        }

        boolean amUnctCol = selectedReportColumn.isAmUncertaintyColumn();
        String unctType = selectedReportColumn.getUncertaintyType();

        if (meth != null) {
            Vector<Fraction> fractions = sample.getUPbFractions();
            for (Fraction fraction : fractions) {
                try {
                    if (!((UPbFractionI) fraction).isRejected()) {
                        ValueModel vm = (ValueModel) meth.invoke((Object) fraction, new Object[]{selectedReportColumn.getRetrieveVariableName()});
                        if (amUnctCol) {
                            double unct = 2.0 * (unctType.equalsIgnoreCase("ABS") ? vm.getOneSigmaAbs().doubleValue() : vm.getOneSigmaPct().doubleValue());
                            min = Double.min(min, unct);
                            max = Double.max(max, unct);
                        } else {
                            min = Double.min(min, vm.getValue().doubleValue());
                            max = Double.max(max, vm.getValue().doubleValue());
                        }
                    }
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException illegalAccessException) {
                }
            }

            for (Fraction fraction : fractions) {
                try {
                    if (!((UPbFractionI) fraction).isRejected()) {
                        ValueModel vm = (ValueModel) meth.invoke((Object) fraction, new Object[]{selectedReportColumn.getRetrieveVariableName()});

                        double valueToCompare;
                        if (amUnctCol) {
                            valueToCompare = 2.0 * (unctType.equalsIgnoreCase("ABS") ? vm.getOneSigmaAbs().doubleValue() : vm.getOneSigmaPct().doubleValue());
                        } else {
                            valueToCompare = vm.getValue().doubleValue();
                        }

                        int selectedIndex = HeatMap.selectColorInRange(0, 0, (valueToCompare - min) / (max - min));
                        fractionRGBs.add(selectedIndex);

                        int rgbColor = HeatMap.getRgb().get(selectedIndex);

                        if (heatMapActive) {
                            fraction.setRgbColor(rgbColor);
                        } else {
                            fraction.setRgbColor(0);
                        }
                    }
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException illegalAccessException) {
                }

            }
        }

        System.out.println("  min " + min + "   max " + max);

        return fractionRGBs;

    }

    /**
     * @return the min
     */
    public double getMin() {
        return min;
    }

    /**
     * @return the max
     */
    public double getMax() {
        return max;
    }

}
