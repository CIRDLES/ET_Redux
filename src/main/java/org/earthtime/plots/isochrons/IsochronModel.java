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
package org.earthtime.plots.isochrons;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import org.earthtime.UPb_Redux.ReduxConstants;

/**
 *
 * @author James F. Bowring
 */
public class IsochronModel implements Comparable<IsochronModel>, Serializable {

    // Class variables
    private static final long serialVersionUID = 6649500047671825154L;

    private double dateInAnnum;
    private double xCoord;
    private double yCoord;
    private String units;
    private boolean visible;
    private int densityLevel;

    public IsochronModel() {
        this(0.0, 0.0, 0.0, "ka", false, 0);
    }

    public IsochronModel(double dateInAnnum) {
        this(dateInAnnum, 0.0, 0.0, "ka", false, 0);
    }

    public IsochronModel(double dateInAnnum, double xCoord, double yCoord, String units, boolean visible, int densityLevel) {
        this.dateInAnnum = dateInAnnum;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.units = units;
        this.visible = visible;
        this.densityLevel = densityLevel;
    }

    @Override
    public int compareTo(IsochronModel isochronModel) throws ClassCastException {
        double isochronDate = isochronModel.getDateInAnnum();
        return Double.compare(this.getDateInAnnum(), isochronDate);
    }

    @Override
    public boolean equals(Object isochronModel) {
        //check for self-comparison
        if (this == isochronModel) {
            return true;
        }
        if (!(isochronModel instanceof IsochronModel)) {
            return false;
        }

        IsochronModel myIsochronModel = (IsochronModel) isochronModel;
        return this.getDateInAnnum() == myIsochronModel.getDateInAnnum();
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public String toString() {
        String retVal = new BigDecimal(dateInAnnum)
                .movePointRight(ReduxConstants.getUnitConversionMoveCount(units))
                .setScale(0).toPlainString() + " " + units
                + " (" + xCoord + ", " + yCoord + ")";
        return retVal;
    }

    public String prettyPrintI() {
        String retVal = new BigDecimal(dateInAnnum)
                .movePointRight(ReduxConstants.getUnitConversionMoveCount(units))
                .setScale(0).toPlainString() + " " + units;

        if (dateInAnnum >= 10e10) {
            retVal = " \u221E" + " " + units;
        }

        return retVal;
    }

    public String prettyPrintINoUnits() {
        String retVal = new BigDecimal(dateInAnnum)
                .movePointRight(ReduxConstants.getUnitConversionMoveCount(units))
                .setScale(0).toPlainString();

        if (dateInAnnum >= 10e10) {
            retVal = " \u221E" + " " + units;
        }

        return retVal;
    }

    public static SortedSet<IsochronModel> generateDefaultEvolutionIsochronModels() {
        SortedSet<IsochronModel> isochronModels = new TreeSet<>();
        // density level 0
        double[] annumIsochrons
                = new double[]{25.0e3, 50.0e3, 75.0e3, 100.0e3, 150.0e3, 200.0e3, 300.0e3, 500.0e3, 10e16};
        for (int i = 0; i < annumIsochrons.length; i++) {
            isochronModels.add(new IsochronModel(annumIsochrons[i], 0.0, 0.0, "ka", true, 0));
        }
        // density level 1
        annumIsochrons
                = new double[]{85.0e3, 115.0e3, 130.0e3, 140.0e3,
                    160.0e3, 180.0e3};
        for (int i = 0; i < annumIsochrons.length; i++) {
            isochronModels.add(new IsochronModel(annumIsochrons[i], 0.0, 0.0, "ka", true, 1));
        }
        // density level 2
        annumIsochrons
                = new double[]{225.0e3, 250.0e3, 275.0e3,
                    350.0e3, 400.0e3};
        for (int i = 0; i < annumIsochrons.length; i++) {
            isochronModels.add(new IsochronModel(annumIsochrons[i], 0.0, 0.0, "ka", true, 2));
        }
        return isochronModels;
    }

    public static double[] generateDefaultEvolutionAr48icntrs() {
        int init48Density = (int) Math.pow(2, (2 + 8 / 10));
        double[] ar48icntrs = new double[(int) 2 * init48Density + init48Density + 1];
        for (int i = 0; i < ar48icntrs.length; i++) {
            ar48icntrs[i] = i * (double) (1.0 / (double) init48Density);
        }
        return ar48icntrs;
    }

    public static double[] generateDefaultEvolutionAr48icntrs(double[] currentAr48icntrs) {
        double[] ar48icntrs = generateDefaultEvolutionAr48icntrs();
        List<Double> arList = DoubleStream.of(ar48icntrs).boxed().collect(Collectors.toList());
        for (int i = 0; i < currentAr48icntrs.length; i++) {
            if (!(arList.contains(currentAr48icntrs[i])
                    || arList.contains(Math.abs(currentAr48icntrs[i])))){
                arList.add(currentAr48icntrs[i]);
            }
        }
        Collections.sort(arList,new Comparator<Double>() {
            @Override
            public int compare(Double o1, Double o2) {
                return Double.compare(Math.abs(o1), Math.abs(o2));
            }
        });
        ar48icntrs = new double[arList.size()];
        for (int i = 0; i < arList.size(); i++) {
            ar48icntrs[i] = arList.get(i);
        }
        
        return ar48icntrs;
    }

    /**
     * @return the dateInAnnum
     */
    public double getDateInAnnum() {
        return dateInAnnum;
    }

    /**
     * @param dateInAnnum the dateInAnnum to set
     */
    public void setDateInAnnum(double dateInAnnum) {
        this.dateInAnnum = dateInAnnum;
    }

    /**
     * @return the xCoord
     */
    public double getxCoord() {
        return xCoord;
    }

    /**
     * @param xCoord the xCoord to set
     */
    public void setxCoord(double xCoord) {
        this.xCoord = xCoord;
    }

    /**
     * @return the yCoord
     */
    public double getyCoord() {
        return yCoord;
    }

    /**
     * @param yCoord the yCoord to set
     */
    public void setyCoord(double yCoord) {
        this.yCoord = yCoord;
    }

    /**
     * @return the units
     */
    public String getUnits() {
        return units;
    }

    /**
     * @param units the units to set
     */
    public void setUnits(String units) {
        this.units = units;
    }

    /**
     * @return the visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * @param visible the visible to set
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /**
     * @return the densityLevel
     */
    public int getDensityLevel() {
        return densityLevel;
    }

}
