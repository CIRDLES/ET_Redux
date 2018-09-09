/*
 * AxisSetup.java
 *
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
package org.earthtime.UPb_Redux.dateInterpretation.graphPersistence;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;

/**
 *
 * @author James F. Bowring
 */
public class AxisSetup implements
        Serializable {

    // class variables
    private static final long serialVersionUID = -4227776246274918874L;
    // instance variables
    private int decimalPlaceCount;
    private int ticIncrement;
    private int ticLabelFrequency;
    private double minTic;
    private double maxTic;
    private double stepTic;
    private double min;
    private double max;
    private double min_T;
    private double max_T;
    private double displayOffset;
    private int zoomMin;
    private int zoomMax;
    private String axisLabel;

    /**
     * 
     */
    public AxisSetup () {
        decimalPlaceCount = 1;
        ticIncrement = 1;
        ticLabelFrequency = 1;
        minTic = 0.0;
        maxTic = 0.0;
        stepTic = 1;
        min = 0.0;
        max = 0.0;
        min_T = 0.0;
        max_T = 0.0;
        displayOffset = 0.0;
        zoomMin = 0;
        zoomMax = 0;
        axisLabel = "NONE";
    }
    public AxisSetup (String axisLabel){
        this();
        this.axisLabel = axisLabel;
    }

    private void calculateAxisTicLimits () {
        double countOfDecimalPlaces = decimalPlaceCount - 1.0;
        int decFilter = (int) Math.pow( 10.0, countOfDecimalPlaces );

        BigDecimal minTickBD = //
                new BigDecimal( Double.toString( Math.floor( getMin_Display() * decFilter ) ), //
                new MathContext( 0 ) ).movePointLeft( (int) countOfDecimalPlaces );
        minTic = minTickBD.doubleValue();

        BigDecimal maxTickBD = //
                new BigDecimal( Double.toString( Math.ceil( getMax_Display() * decFilter ) ), //
                new MathContext( 0 ) ).movePointLeft( (int) countOfDecimalPlaces );
        maxTic = maxTickBD.doubleValue();


////        //-3        9000        100        9100
////        //-2        9900        10        9910
////        //-1        9990        1        9991
////        //0        9999        0.1        9999.1
////        //1        9999.9        0.01        9999.91
////
////        int shiftCount = 0 - decimalPlaceCount;
////
////        try {
////            BigDecimal minTickBD = new BigDecimal( getMin_Display() ).movePointLeft( shiftCount ).setScale( 0, RoundingMode.FLOOR ).movePointRight(  - decimalPlaceCount );
////            BigDecimal maxTickBD = new BigDecimal( getMax_T() ).movePointLeft( shiftCount ).setScale( 0, RoundingMode.CEILING ).movePointRight(  - decimalPlaceCount );
////
////            minTic = minTickBD.doubleValue();
////            maxTic = maxTickBD.doubleValue();
////        } catch (Exception e) {
////        }
////
////        BigDecimal stepTicBD = //
////                new BigDecimal( 1111111111 ).movePointLeft( shiftCount - 1 ).setScale( 0, RoundingMode.FLOOR ).ulp().movePointRight(  - decimalPlaceCount - 1 )//
////                .multiply( new BigDecimal( ticIncrement ) );
////        stepTic = stepTicBD.doubleValue();
////
////        System.out.print( getMin_T()//
////                + "   AXIS tics:  " //
////                + minTic + "   "//
////                + maxTic + "    "//
////                + stepTic + "   "//
////                + ((maxTic - minTic) / stepTic) + "  "//
////                + decimalPlaceCount );
////        System.out.println();

    }

    /**
     * 
     * @param useAutomaticAxisTics
     */
    protected void calculateTicLayout ( boolean useAutomaticAxisTics ) {
        double ticBreak = 10.5;
        int decPlaceMaxXCount = 10;

        calculateAxisTicLimits();

        // if yTicIncrement == 10, then we slide left one place in the substring selection
        BigDecimal stepTicBD = //
                new BigDecimal( "0.0000000000000".substring( 0, decimalPlaceCount + 1 - (ticIncrement / 10) ) + Integer.toString( ticIncrement ) );
        setStepTic( stepTicBD.doubleValue() );//   0.10;//= deltay / 100000;

        if ( useAutomaticAxisTics ) {
            // reset tic frequency
            ticLabelFrequency = 1;

            boolean amNotOne = true;
            int safetyCount = 0;
            while ((safetyCount < 10) && amNotOne && ((maxTic - minTic) / getStepTic()) < ticBreak) {
                safetyCount ++;
                switch (ticIncrement) {
                    case 1:
                        amNotOne = false;
                        break;
                    case 2:
                        ticIncrement = 1;
                        break;
                    case 5:
                        ticIncrement = 2;
                        break;
                    case 10:
                        ticIncrement = 5;
                        break;
                }

                stepTicBD = //
                        new BigDecimal( "0.0000000000000".substring( 0, decimalPlaceCount + 1 - (ticIncrement / 10) ) + Integer.toString( ticIncrement ) );
                setStepTic( stepTicBD.doubleValue() );//   0.10;//= deltay / 100000;
            }

            boolean amStable = false;
            safetyCount = 0;
            while ((safetyCount < 2) &&  ! amStable && ((maxTic - minTic) / getStepTic()) < ticBreak) {
                safetyCount ++;

                if ( decimalPlaceCount < decPlaceMaxXCount ) {
                    decimalPlaceCount ++;
                } else {
                    amStable = true;
                }
                calculateAxisTicLimits();

                // if yTicIncrement == 10, then we slide left one place in the substring selection
                stepTicBD = //
                        new BigDecimal( "0.0000000000000".substring( 0, decimalPlaceCount + 1 - (ticIncrement / 10) ) + Integer.toString( ticIncrement ) );
                setStepTic( stepTicBD.doubleValue() );//   0.10;//= deltay / 100000;


                if ( ((maxTic - minTic) / getStepTic()) >= ticBreak ) {
                    switch (ticIncrement) {
                        case 1:
                            ticIncrement = 2;
                            break;
                        case 2:
                            ticIncrement = 5;
                            break;
                        case 5:
                            ticIncrement = 10;
                            break;
                        case 10:
                            ticIncrement = 1;
                            if ( decimalPlaceCount > 1 ) {
                                decimalPlaceCount --;
                                calculateAxisTicLimits();

                            }
                    }

                    stepTicBD = //
                            new BigDecimal( "0.0000000000000".substring( 0, decimalPlaceCount + 1 - (ticIncrement / 10) ) + Integer.toString( ticIncrement ) );
                    setStepTic( stepTicBD.doubleValue() );//   0.10;//= deltay / 100000;


                }
            }

            safetyCount = 0;
            while ((safetyCount < 10) && ((maxTic - minTic) / getStepTic()) >= ticBreak) {
                safetyCount ++;
                switch (ticIncrement) {
                    case 1:
                        ticIncrement = 2;
                        break;
                    case 2:
                        ticIncrement = 5;
                        break;
                    case 5:
                        ticIncrement = 10;
                        break;
                    case 10:
                        ticIncrement = 1;
                        if ( decimalPlaceCount > 1 ) {
                            decimalPlaceCount --;
                            calculateAxisTicLimits();
                        }
                }

                stepTicBD = //
                        new BigDecimal( "0.0000000000000".substring( 0, decimalPlaceCount + 1 - (ticIncrement / 10) ) + Integer.toString( ticIncrement ) );
                setStepTic( stepTicBD.doubleValue() );//   0.10;//= deltay / 100000;

            }
        }


    }

//    public void drawAxisTicsAndLabels ( Graphics2D g2d ) {
//    }
    /**
     * 
     * @return
     */
    public double getMin_Display () {
        return min + displayOffset;
    }

    /**
     * 
     * @return
     */
    public double getMax_Display () {
        return max + displayOffset;
    }

    /**
     * 
     * @return
     */
    public double getRange_Display () {
        return max - min;
    }

    /**
     * @return the decimalPlaceCount
     */
    public int getDecimalPlaceCount () {
        return decimalPlaceCount;
    }

    /**
     * @param decimalPlaceCount the decimalPlaceCount to set
     */
    public void setDecimalPlaceCount ( int decimalPlaceCount ) {
        this.decimalPlaceCount = decimalPlaceCount;
    }

    /**
     * @return the ticIncrement
     */
    public int getTicIncrement () {
        return ticIncrement;
    }

    /**
     * @param ticIncrement the ticIncrement to set
     */
    public void setTicIncrement ( int ticIncrement ) {
        this.ticIncrement = ticIncrement;
    }

    /**
     * @return the ticLabelFrequency
     */
    public int getTicLabelFrequency () {
        return ticLabelFrequency;
    }

    /**
     * @param ticLabelFrequency the ticLabelFrequency to set
     */
    public void setTicLabelFrequency ( int ticLabelFrequency ) {
        this.ticLabelFrequency = ticLabelFrequency;
    }

    /**
     * @return the minTic
     */
    public double getMinTic () {
        return minTic;
    }

    /**
     * @param minTic the minTic to set
     */
    public void setMinTic ( double minTic ) {
        this.minTic = minTic;
    }

    /**
     * @return the maxTic
     */
    public double getMaxTic () {
        return maxTic;
    }

    /**
     * @param maxTic the maxTic to set
     */
    public void setMaxTic ( double maxTic ) {
        this.maxTic = maxTic;
    }

    /**
     * @return the min
     */
    public double getMin () {
        return min;
    }

    /**
     * @param min the min to set
     */
    public void setMin ( double min ) {
        if ( min < 0.0 ) {
            min = 0.0;
        }
        this.min = min;
    }

    /**
     * @return the max
     */
    public double getMax () {
        return max;
    }

    /**
     * @param max the max to set
     */
    public void setMax ( double max ) {
        this.max = max;
    }

    /**
     * @return the displayOffset
     */
    public double getDisplayOffset () {
        return displayOffset;
    }

    /**
     * @param displayOffset the displayOffset to set
     */
    public void setDisplayOffset ( double displayOffset ) {
        this.displayOffset = displayOffset;
    }

    /**
     * @return the stepTic
     */
    public double getStepTic () {
        return stepTic;
    }

    /**
     * @param stepTic the stepTic to set
     */
    public void setStepTic ( double stepTic ) {
        this.stepTic = stepTic;
    }

    /**
     * @return the zoomMin
     */
    public int getZoomMin () {
        return zoomMin;
    }

    /**
     * @param zoomMin the zoomMin to set
     */
    public void setZoomMin ( int zoomMin ) {
        this.zoomMin = zoomMin;
    }

    /**
     * @return the zoomMax
     */
    public int getZoomMax () {
        return zoomMax;
    }

    /**
     * @param zoomMax the zoomMax to set
     */
    public void setZoomMax ( int zoomMax ) {
        this.zoomMax = zoomMax;
    }

    /**
     * @return the min_T
     */
    public double getMin_T () {
        if ( min_T < 0.0 ) {
            min_T = 0.0;
        }
        return min_T;
    }

    /**
     * @param min_T the min_T to set
     */
    public void setMin_T ( double min_T ) {
        if ( min_T < 0.0 ) {
            min_T = 0.0;
        }
        this.min_T = min_T;
    }

    /**
     * @return the max_T
     */
    public double getMax_T () {
        if ( max_T < 0.0 ) {
            max_T = 0.0;
        }
        return max_T;
    }

    /**
     * @param max_T the max_T to set
     */
    public void setMax_T ( double max_T ) {
        if ( max_T < 0.0 ) {
            max_T = 0.0;
        }
        this.max_T = max_T;
    }

    /**
     * 
     * @param value
     * @return
     */
    public boolean valueInVisibleRange ( double value ) {
        return //
                (value >= getMin_Display())//
                && //
                (value <= getMax_Display());
    }

    /**
     * @return the axisLabel
     */
    public String getAxisLabel() {
        return axisLabel;
    }

    /**
     * @param axisLabel the axisLabel to set
     */
    public void setAxisLabel(String axisLabel) {
        this.axisLabel = axisLabel;
    }

   
}
