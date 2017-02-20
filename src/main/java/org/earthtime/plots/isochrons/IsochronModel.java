/*
 * Copyright 2006-2017 CIRDLES.org.
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

import java.math.BigDecimal;
import org.earthtime.UPb_Redux.ReduxConstants;

/**
 *
 * @author James F. Bowring
 */
public class IsochronModel implements Comparable<IsochronModel> {

    private double dateInAnnum;
    private double xCoord;
    private double yCoord;
    private String units;
    private boolean visible;

    public IsochronModel() {
        this(0.0, 0.0, 0.0, "ka", false);
    }

    public IsochronModel(double dateInAnnum) {
        this(dateInAnnum, 0.0, 0.0, "ka", false);
    }

    public IsochronModel(double dateInAnnum, double xCoord, double yCoord, String units, boolean visible) {
        this.dateInAnnum = dateInAnnum;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
        this.units = units;
        this.visible = visible;
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

    public String presentationView() {
        String retVal = new BigDecimal(dateInAnnum)
                .movePointRight(ReduxConstants.getUnitConversionMoveCount(units))
                .setScale(0).toPlainString() + " " + units;
        return retVal;
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
}
