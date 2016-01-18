/*
 * UThFractionReducer.java
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
package org.earthtime.UTh_Redux.fractions.fractionReduction;

import Jama.Matrix;
import java.math.BigDecimal;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UTh_Redux.fractions.UThLegacyFractionI;
import org.earthtime.dataDictionaries.UThAnalysisMeasures;
import org.earthtime.dataDictionaries.UThFractionationCorrectedIsotopicRatios;
import org.earthtime.fractions.fractionReduction.FractionReducer;

/* NOTES from Noah 28 October 2015
    So instead, here's a MATLAB file that goes with an Excel worksheet and VBA Add-In I created to calculate U-Th dates.  Here's what's going on in the code.

   There are three inputs: inputARs, lambdas, detritus.

   inputARs is a six-element vector.  
   inputARs(1) is the input 232Th/238U activity ratio
   inputARs(2) is the 2-sigma uncertainty in this activity ratio, in percent
   inputARs(3) is the input 230Th/238U activity ratio
   inputARs(4) is the 2-sigma uncertainty in this activity ratio, in percent
   inputARs(5) is the input 234U/238U activity ratio
   inputARs(6) is the 2-sigma uncertainty in this activity ratio, in percent

   lambdas(1) = lambda230 (the 230Th decay constant)
   lambdas(2) = lambda232 (the 232Th decay constant)
   lambdas(3) = lambda234 (the 234U decay constant)
   lambdas(4) = lambda235 (the 235U decay constant)
   lambdas(5) = lambda238 (the 238U decay constant)

   The variable detritus is a matrix (2D array) with six rows and two columns, indexed starting at 1.
   detritus(1,1) is the detrital initial 232Th/238U activity ratio
   detritus(1,2) is its 2-sigma absolute uncertainty
   detritus(2,1) is the detrital initial 230Th/238U activity ratio
   detritus(2,2) is its 2-sigma absolute uncertainty
   detritus(3,1) is the detrital initial 234U/238U activity ratio
   detritus(3,2) is its 2-sigma absolute uncertainty
   detritus(4,1) is the correlation coefficient between the 232Th/238U - 230Th/238U activity ratio uncertainties
   detritus(5,1) is the correlation coefficient between the 232Th/238U - 234U/238U activity ratio uncertainties 
   detritus(6,1) is the correlation coefficient between the 230Th/238U - 234U/238U activity ratio uncertainties
   detritus(6,2) is the number of years between 1950 and the date of the analysis.

   So how do these inputs this map to Andrea's worksheet?  
   inputARs(1) can be calculated from Andrea's input column BF, as BF/(10^5)*lambda232/lambda238
   inputARs(2) is not given in Andrea's spreadsheet -- you can assume it's zero for now.
   inputARs(3) is column AX or BG
   inputARs(4) is column AY or BH, converted to percent
   inputARs(5) is column AZ or BI
   inputARs(6) is column BA or BJ, converted to percent

    Note: AX-BA = original and BG-BJ = Andrea's in house conversions that we will redo

   The lambdas come from our current physical constants model.

   The detritus variable is not at present included in Andrea's worksheet.  We'll have to add it, though, when we make UTh_Redux capable of handling more data.  Here are some typical values:
   detritus(1,1) = 1.2; detritus(1,2) = 0.6;
   detritus(2,1) = 1; detritus(2,2) = 0.5;
   detritus(3,1) = 1; detritus(3,2) = 0.5;
   detritus(4,1) = 0; detritus(5,1) = 0; detritus(6,1) = 0.5;
   detritus(6,2) = 65;

   The output from the MATLAB code is a variable called outvec, which has 13 elements.
   outvec(1) is the detrital-corrected 230Th/238U activity ratio
   outvec(2) is its 2-sigma relative uncertainty in percent
   outvec(3) is the detrital-corrected 234U/238U activity ratio
   outvec(4) is its 2-sigma relative uncertainty in percent
   outvec(5) is the correlation coefficient between the detrital-corrected 230Th/238U and 234U/238U activity ratio uncertainties

   outvec(6) is the (detrital-uncorrected) date, in ka (thousands of years ago)
   outvec(7) is its two-sigma absolute uncertainty
   outvec(8) is the detrital-corrected date, in ka (thousands of years ago)
   outvec(9) is the detrital-corrected date, in ka (thousands of years ago) since 1950
   outvec(10) is its two-sigma absolute uncertainty (this uncertainty is the same for 8 and 9)
   outvec(11) is the detrital-corrected initial 234U/238U activity ratio
   outvec(12) is its two-sigma absolute uncertainty
   outvec(13) is the correlation coefficient between the uncertainties in the detrital-corrected date and initial 234U/238U ratio.

   Some comments about the MATLAB code:

   MATLAB defines functions in-line with a syntax that begins with an @(x) after the equals sign.  So y = @(x) x^2 is a function 
   called y that takes one input (x) and squares it.  So y(2) = 4 and y(3) = 9.

   The function blkdiag() takes the inputs and places them along the diagonal of a matrix that is otherwise full of zeros. 
   If you've never seen a block diagonal matrix, then here's what it looks like (in a question 
   about Mathematica) http://mathematica.stackexchange.com/questions/19778/how-to-form-a-block-diagonal-matrix-from-a-list-of-matrices 

   Otherwise, there are no special MATLAB functions here, so you should be ok with just matrix multiplication in JAMA.  



 */
/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public class UThFractionReducer extends FractionReducer {

    private static UThFractionReducer instance;

    private UThFractionReducer() {
    }

    /**
     *
     * @return
     */
    public static UThFractionReducer getInstance() {
        if (instance == null) {
            instance = new UThFractionReducer();
        }
        return instance;
    }

    public static void reduceFraction(UThLegacyFractionI fraction) {
        initializeDecayConstants(fraction.getPhysicalConstantsModel());

        calculateActivityRatios(fraction);

        calculateDatesFromLegacyData(fraction);
    }

    private static void calculateDatesFromLegacyData(UThLegacyFractionI fraction) {

        // todo: make a model for handling     
        //           The variable detritus is a matrix (2D array) with six rows and two columns, indexed starting at 1.
        //   detritus(1,1) is the detrital initial 232Th/238U activity ratio
        //   detritus(1,2) is its 2-sigma absolute uncertainty
        //   detritus(2,1) is the detrital initial 230Th/238U activity ratio
        //   detritus(2,2) is its 2-sigma absolute uncertainty
        //   detritus(3,1) is the detrital initial 234U/238U activity ratio
        //   detritus(3,2) is its 2-sigma absolute uncertainty
        //   detritus(4,1) is the correlation coefficient between the 232Th/238U - 230Th/238U activity ratio uncertainties
        //   detritus(5,1) is the correlation coefficient between the 232Th/238U - 234U/238U activity ratio uncertainties 
        //   detritus(6,1) is the correlation coefficient between the 230Th/238U - 234U/238U activity ratio uncertainties
        //   detritus(6,2) is the number of years between 1950 and the date of the analysis.
        // The detritus variable is not at present included in Andrea's worksheet.  
        // We'll have to add it, though, when we make UTh_Redux capable of handling more data.  
        // Here are some typical values:
        Matrix detritus = new Matrix(6, 2);
        detritus.set(0, 0, 1.2); //detritus(1,1) = 1.2; 
        detritus.set(0, 1, 0.6); //detritus(1,2) = 0.6;
        detritus.set(1, 0, 1.0); //detritus(2,1) = 1;
        detritus.set(1, 1, 0.5); //detritus(2,2) = 0.5;
        detritus.set(2, 0, 1.0); //detritus(3,1) = 1;
        detritus.set(2, 1, 0.5); //detritus(3,2) = 0.5;
        detritus.set(3, 0, 0.0); //detritus(4,1) = 0;
        detritus.set(4, 0, 0.0); //detritus(5,1) = 0;
        detritus.set(5, 0, 0.5); //detritus(6,1) = 0.5;
        detritus.set(5, 1, 65.0); //detritus(6,2) = 65;

        // di = detrital initial isotoipic ratio
        double r232Th_238Udi = detritus.get(0, 0) * lambda238.getValue().doubleValue() / lambda232.getValue().doubleValue();
        double r230Th_238Udi = detritus.get(1, 0) * lambda238.getValue().doubleValue() / lambda230.getValue().doubleValue();
        double r234U_238Udi = detritus.get(2, 0) * lambda238.getValue().doubleValue() / lambda234.getValue().doubleValue();

        double r232Th_238Udi_sigma = detritus.get(0, 1) / detritus.get(0, 0) * r232Th_238Udi;
        double r230Th_238Udi_sigma = detritus.get(1, 1) / detritus.get(1, 0) * r230Th_238Udi;
        double r234U_238Udi_sigma = detritus.get(2, 1) / detritus.get(2, 0) * r234U_238Udi;
        Matrix Cov_di = new Matrix(3, 3);
        Cov_di.set(0, 0, r234U_238Udi_sigma * r234U_238Udi_sigma);
        Cov_di.set(1, 1, r230Th_238Udi_sigma * r230Th_238Udi_sigma);
        Cov_di.set(2, 2, r232Th_238Udi_sigma * r232Th_238Udi_sigma);

        Cov_di.set(0, 1, detritus.get(5, 0) * r234U_238Udi_sigma * r230Th_238Udi_sigma);
        Cov_di.set(0, 2, detritus.get(4, 0) * r234U_238Udi_sigma * r232Th_238Udi_sigma);
        Cov_di.set(1, 2, detritus.get(3, 0) * r230Th_238Udi_sigma);
        Cov_di.set(1, 0, Cov_di.get(0, 1));
        Cov_di.set(2, 0, Cov_di.get(0, 2));
        Cov_di.set(2, 1, Cov_di.get(1, 2));

        double yearsSince1950_di = detritus.get(5, 1);

        Matrix measuredCovariance = new Matrix(3, 3);
        measuredCovariance.set(0, 0, //
                fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.r234U_238Ufc.getName()).getOneSigmaAbs().pow(2).doubleValue());
        measuredCovariance.set(1, 1, //
                fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.r230Th_238Ufc.getName()).getOneSigmaAbs().pow(2).doubleValue());
        measuredCovariance.set(2, 2, //
                fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.r232Th_238Ufc.getName()).getOneSigmaAbs().pow(2).doubleValue());

    }

    private static void calculateActivityRatios(UThLegacyFractionI fraction) {

        fraction.getAnalysisMeasure(UThAnalysisMeasures.ar232Th_238Ufc.getName())//
                .setValue(//
                        fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.r232Th_238Ufc.getName()).getValue()//
                        .multiply(lambda232.getValue())//
                        .divide(lambda238.getValue(), ReduxConstants.mathContext15));
        fraction.getAnalysisMeasure(UThAnalysisMeasures.ar232Th_238Ufc.getName())//
                .setOneSigma(//
                        fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.r232Th_238Ufc.getName()).getOneSigmaAbs()//
                        .multiply(lambda232.getValue())//
                        .divide(lambda238.getValue(), ReduxConstants.mathContext15));

        fraction.getAnalysisMeasure(UThAnalysisMeasures.ar230Th_238Ufc.getName())//
                .setValue(//
                        fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.r230Th_238Ufc.getName()).getValue()//
                        .multiply(lambda230.getValue())//
                        .divide(lambda238.getValue(), ReduxConstants.mathContext15));
        fraction.getAnalysisMeasure(UThAnalysisMeasures.ar230Th_238Ufc.getName())//
                .setOneSigma(//
                        fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.r230Th_238Ufc.getName()).getOneSigmaAbs()//
                        .multiply(lambda230.getValue())//
                        .divide(lambda238.getValue(), ReduxConstants.mathContext15));

        fraction.getAnalysisMeasure(UThAnalysisMeasures.ar234U_238Ufc.getName())//
                .setValue(//
                        fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.r234U_238Ufc.getName()).getValue()//
                        .multiply(lambda234.getValue())//
                        .divide(lambda238.getValue(), ReduxConstants.mathContext15));
        fraction.getAnalysisMeasure(UThAnalysisMeasures.ar234U_238Ufc.getName())//
                .setOneSigma(//
                        fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.r234U_238Ufc.getName()).getOneSigmaAbs()//
                        .multiply(lambda234.getValue())//
                        .divide(lambda238.getValue(), ReduxConstants.mathContext15));

    }

    public static void calculateMeasuredAtomRatiosFromLegacyActivityRatios(UThLegacyFractionI fraction) {

        BigDecimal myLambda230Value = fraction.getLambda230Legacy().getValue();
        BigDecimal myLambda234Value = fraction.getLambda234Legacy().getValue();
        BigDecimal myLambda238Value = fraction.getLambda238Legacy().getValue();

        // UThFractionationCorrectedIsotopicRatios.r232Th_238Ufc was read in directly from csv file
        fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.r230Th_238Ufc.getName())//
                .setValue(fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.ar230Th_238Ufc.getName()).getValue()//
                        .multiply(myLambda238Value)//
                        .divide(myLambda230Value, ReduxConstants.mathContext15));
        fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.r230Th_238Ufc.getName())//
                .setOneSigma(fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.ar230Th_238Ufc.getName()).getOneSigmaAbs()//
                        .multiply(myLambda238Value)//
                        .divide(myLambda230Value, ReduxConstants.mathContext15));

        fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.r234U_238Ufc.getName())//
                .setValue(fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.ar234U_238Ufc.getName()).getValue()//
                        .multiply(myLambda238Value)//
                        .divide(myLambda234Value, ReduxConstants.mathContext15));
        fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.r234U_238Ufc.getName())//
                .setOneSigma(fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.ar234U_238Ufc.getName()).getOneSigmaAbs()//
                        .multiply(myLambda238Value)//
                        .divide(myLambda234Value, ReduxConstants.mathContext15));

    }

}
