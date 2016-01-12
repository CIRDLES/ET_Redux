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

import java.math.BigDecimal;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
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

    public void calculateDatesFromLegacyData(UThLegacyFractionI fraction) {

        initializeDecayConstants(fraction.getPhysicalConstantsModel());

        //Nov 2015
        // Note Noah's  matlab code starts with the already reworked activity ratios ... we will not do so.
//        meas.r28 = inputARs(1) * lambda.U238/lambda.Th232;
//        meas.r08 = inputARs(3) * lambda.U238/lambda.Th230;
//        meas.r48 = inputARs(5) * lambda.U238/lambda.U234;
//        meas.r28s = meas.r28 * inputARs(2)/100/2;
//        meas.r08s = meas.r08 * inputARs(4)/100/2;
//        meas.r48s = meas.r48 * inputARs(6)/100/2;
//        meas.C = diag([meas.r48s meas.r08s meas.r28s].^2);
        BigDecimal measuredValue
                = //
                fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.ar232Th_238Ufc.getName()).getValue()//
                .multiply(fraction.getLambda238Legacy().getValue())//
                .divide(fraction.getLambda232Legacy().getValue(), ReduxConstants.mathContext15);

        BigDecimal oneSigmaAbs
                = //
                measuredValue//
                .multiply(BigDecimal.ZERO);

        ValueModel r232Th_238Thm = new ValueModel(//
                "r232Th_238Thm",//
                measuredValue,//
                "ABS", //
                fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.ar232Th_238Ufc.getName()).getOneSigmaAbs(), //
                BigDecimal.ZERO);

        ValueModel r230Th_238Thm = new ValueModel(//
                "r230Th_238Thm",//
                fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.ar230Th_238Ufc.getName()).getValue(),//
                "ABS", //
                fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.ar230Th_238Ufc.getName()).getOneSigmaAbs(), //
                BigDecimal.ZERO);

        ValueModel r234U_238Thm = new ValueModel(//
                "r234U_238Thm",//
                fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.ar234U_238Ufc.getName()).getValue(),//
                "ABS", //
                fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.ar234U_238Ufc.getName()).getOneSigmaAbs(), //
                BigDecimal.ZERO);

    }

    public static void calculateActivityRatios(UThLegacyFractionI fraction) {

        BigDecimal myLambda238Value = fraction.getLambda238Legacy().getValue();
        BigDecimal myLambda234Value = fraction.getLambda234Legacy().getValue();
        BigDecimal myLambda230Value = fraction.getLambda230Legacy().getValue();

        fraction.getAnalysisMeasure(UThAnalysisMeasures.ar234U_238Ufc.getName())//
                .setValue(//
                        fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.r234U_238Ufc.getName()).getValue()//
                        .multiply(myLambda234Value)//
                        .divide(myLambda238Value, ReduxConstants.mathContext15));
        fraction.getAnalysisMeasure(UThAnalysisMeasures.ar234U_238Ufc.getName())//
                .setOneSigma(//
                        fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.r234U_238Ufc.getName()).getOneSigmaAbs()//
                        .multiply(myLambda234Value)//
                        .divide(myLambda238Value, ReduxConstants.mathContext15));

        fraction.getAnalysisMeasure(UThAnalysisMeasures.ar230Th_238Ufc.getName())//
                .setValue(//
                        fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.r230Th_238Ufc.getName()).getValue()//
                        .multiply(myLambda230Value)//
                        .divide(myLambda238Value, ReduxConstants.mathContext15));
        fraction.getAnalysisMeasure(UThAnalysisMeasures.ar230Th_238Ufc.getName())//
                .setOneSigma(//
                        fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.r230Th_238Ufc.getName()).getOneSigmaAbs()//
                        .multiply(myLambda230Value)//
                        .divide(myLambda238Value, ReduxConstants.mathContext15));

    }

    public static void calculateMeasuredAtomRatiosFromLegacyActivityRatios(UThLegacyFractionI fraction) {

        BigDecimal myLambda238Value = fraction.getLambda238Legacy().getValue();
        BigDecimal myLambda234Value = fraction.getLambda234Legacy().getValue();
        BigDecimal myLambda230Value = fraction.getLambda230Legacy().getValue();

        fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.r234U_238Ufc.getName())//
                .setValue(fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.ar234U_238Ufc.getName()).getValue()//
                        .multiply(myLambda238Value)//
                        .divide(myLambda234Value, ReduxConstants.mathContext15));

        fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.r234U_238Ufc.getName())//
                .setOneSigma(fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.ar234U_238Ufc.getName()).getOneSigmaAbs()//
                        .multiply(myLambda238Value)//
                        .divide(myLambda234Value, ReduxConstants.mathContext15));

        fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.r230Th_238Ufc.getName())//
                .setValue(fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.ar230Th_238Ufc.getName()).getValue()//
                        .multiply(myLambda238Value)//
                        .divide(myLambda230Value, ReduxConstants.mathContext15));

        fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.r230Th_238Ufc.getName())//
                .setOneSigma(fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.ar230Th_238Ufc.getName()).getOneSigmaAbs()//
                        .multiply(myLambda238Value)//
                        .divide(myLambda230Value, ReduxConstants.mathContext15));

    }

}
