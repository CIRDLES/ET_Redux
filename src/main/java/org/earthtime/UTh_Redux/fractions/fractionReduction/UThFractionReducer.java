/*
 * UThFractionReducer.java
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
package org.earthtime.UTh_Redux.fractions.fractionReduction;

import Jama.Matrix;
import java.math.BigDecimal;
import org.earthtime.UPb_Redux.ReduxConstants;
import static org.earthtime.UPb_Redux.ReduxConstants.timeInMillisecondsOfYear1950Since1970;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.UTh_Redux.fractions.UThFraction;
import org.earthtime.UTh_Redux.fractions.UThLegacyFractionI;
import org.earthtime.dataDictionaries.DetritalUThRatiosEnum;
import org.earthtime.dataDictionaries.RadDates;
import org.earthtime.dataDictionaries.UThAnalysisMeasures;
import org.earthtime.dataDictionaries.UThFractionationCorrectedIsotopicRatios;
import org.earthtime.fractions.fractionReduction.FractionReducer;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;

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

    private static UThFractionReducer instance = new UThFractionReducer();
    private static Matrix exponentialA;
    private static Matrix exponentialQUTh;
    private static Matrix exponentialQinvUTh;
    private static Matrix numberAtomsTimeT;
    private static double r230_238InitialT;

    private UThFractionReducer() {
    }

    /**
     *
     * @return
     */
    public static UThFractionReducer getInstance() {
        return instance;
    }

    public static void reduceFraction(UThLegacyFractionI fraction) {
        initializeDecayConstants(fraction.getPhysicalConstantsModel());

        calculateActivityRatios(fraction);

        calculateDatesFromLegacyData(fraction);
    }

    private static void calculateDatesFromLegacyData(UThLegacyFractionI fraction) {

        // matlab code meas
        ValueModel r234U_238Ufc = fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.r234U_238Ufc.getName());
        ValueModel r230Th_238Ufc = fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.r230Th_238Ufc.getName());
        ValueModel r232Th_238Ufc = fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.r232Th_238Ufc.getName());

        // test for missing data = need 2 of 3
        if ((r234U_238Ufc.getValue().signum() + r234U_238Ufc.getValue().signum() + r234U_238Ufc.getValue().signum()) >= 2) {

            // matlab code meas.C
            Matrix covariance_fc = new Matrix(3, 3);
            covariance_fc.set(0, 0, r234U_238Ufc.getOneSigmaAbs().pow(2).doubleValue());
            covariance_fc.set(1, 1, r230Th_238Ufc.getOneSigmaAbs().pow(2).doubleValue());
            covariance_fc.set(2, 2, r232Th_238Ufc.getOneSigmaAbs().pow(2).doubleValue());

            // todo: make a model for handling     
            // TOD Decide why not use one sigma in model as usual ???
            // The detritus variable is not at present included in Andrea's worksheet.  
            // We'll have to add it, though, when we make UTh_Redux capable of handling more data.  
            // Here are some typical values:
//            Matrix detritus = new Matrix(6, 2);
//            detritus.set(0, 0, 1.2); //detritus(1,1) = 1.2; 
//            detritus.set(0, 1, 0.6); //detritus(1,2) = 0.6;
//            detritus.set(1, 0, 1.0); //detritus(2,1) = 1;
//            detritus.set(1, 1, 0.5); //detritus(2,2) = 0.5;
//            detritus.set(2, 0, 1.0); //detritus(3,1) = 1;
//            detritus.set(2, 1, 0.5); //detritus(3,2) = 0.5;
//            detritus.set(3, 0, 0.0); //detritus(4,1) = 0;
//            detritus.set(4, 0, 0.0); //detritus(5,1) = 0;
//            detritus.set(5, 0, 0.5); //detritus(6,1) = 0.5;
//            detritus.set(5, 1, 65.0); //detritus(6,2) = 65;
            AbstractRatiosDataModel detritalUThModel = fraction.getDetritalUThModel();
            ValueModel ar232Th_238Ui = detritalUThModel.getDatumByName(DetritalUThRatiosEnum.ar232Th_238U.getName());
            ValueModel ar230Th_238Ui = detritalUThModel.getDatumByName(DetritalUThRatiosEnum.ar230Th_238U.getName());
            ValueModel ar234U_238Ui = detritalUThModel.getDatumByName(DetritalUThRatiosEnum.ar234U_238U.getName());

            Matrix detritus = new Matrix(6, 2);
            detritus.set(0, 0, ar232Th_238Ui.getValue().doubleValue());
            detritus.set(0, 1, ar232Th_238Ui.getOneSigmaAbs().doubleValue());
            detritus.set(1, 0, ar230Th_238Ui.getValue().doubleValue());
            detritus.set(1, 1, ar230Th_238Ui.getOneSigmaAbs().doubleValue());
            detritus.set(2, 0, ar234U_238Ui.getValue().doubleValue());
            detritus.set(2, 1, ar234U_238Ui.getOneSigmaAbs().doubleValue());

            ValueModel rhoAr232Th_238U__ar230Th_238Ui = detritalUThModel.getRhoVarUnctByName("rhoAr232Th_238U__ar230Th_238U");
            ValueModel rhoAr232Th_238U__ar234U_238Ui = detritalUThModel.getRhoVarUnctByName("rhoAr232Th_238U__ar234U_238U");
            ValueModel rhoAr230Th_238U__ar234U_238Ui = detritalUThModel.getRhoVarUnctByName("rhoAr230Th_238U__ar234U_238U");
            detritus.set(3, 0, rhoAr232Th_238U__ar230Th_238Ui.getValue().doubleValue());
            detritus.set(4, 0, rhoAr232Th_238U__ar234U_238Ui.getValue().doubleValue());
            detritus.set(5, 0, rhoAr230Th_238U__ar234U_238Ui.getValue().doubleValue());

            // di = detrital initial isotopic ratio
            double r232Th_238Udi = detritus.get(0, 0) * lambda238D / lambda232D;
            double r230Th_238Udi = detritus.get(1, 0) * lambda238D / lambda230D;
            double r234U_238Udi = detritus.get(2, 0) * lambda238D / lambda234D;

            // assuming detritus stores 1-sigma abs
            double r232Th_238Udi_sigma = detritus.get(0, 1) / detritus.get(0, 0) * r232Th_238Udi;
            double r230Th_238Udi_sigma = detritus.get(1, 1) / detritus.get(1, 0) * r230Th_238Udi;
            double r234U_238Udi_sigma = detritus.get(2, 1) / detritus.get(2, 0) * r234U_238Udi;
            Matrix covariance_di = new Matrix(3, 3);
            covariance_di.set(0, 0, r234U_238Udi_sigma * r234U_238Udi_sigma);
            covariance_di.set(1, 1, r230Th_238Udi_sigma * r230Th_238Udi_sigma);
            covariance_di.set(2, 2, r232Th_238Udi_sigma * r232Th_238Udi_sigma);

            covariance_di.set(0, 1, detritus.get(5, 0) * r234U_238Udi_sigma * r230Th_238Udi_sigma);
            covariance_di.set(0, 2, detritus.get(4, 0) * r234U_238Udi_sigma * r232Th_238Udi_sigma);
            covariance_di.set(1, 2, detritus.get(3, 0) * r230Th_238Udi_sigma * r232Th_238Udi_sigma);
            covariance_di.set(1, 0, covariance_di.get(0, 1));
            covariance_di.set(2, 0, covariance_di.get(0, 2));
            covariance_di.set(2, 1, covariance_di.get(1, 2));

            double yearsSince1950_di = (((UThFraction) fraction).getDateTimeMillisecondsOfAnalysis() - timeInMillisecondsOfYear1950Since1970) / 1000.0 / 60.0 / 60.0 / 24.0 / 365.242;

            Matrix covariance_in = new Matrix(6, 6);
            covariance_in.setMatrix(0, 2, 0, 2, covariance_fc);
            covariance_in.setMatrix(3, 5, 3, 5, covariance_di);

            exponentialA = new Matrix(new double[][]{//
                {-lambda238D, 0., 0.},//
                {lambda238D, -lambda234D, 0.},//
                {0., lambda234D, -lambda230D}});

            exponentialQUTh = new Matrix(new double[][]{//
                {((lambda230D - lambda238D) * (lambda234D - lambda238D)) / (lambda234D * lambda238D), 0., 0.},//
                {(lambda230D - lambda238D) / lambda234D, (lambda230D - lambda234D) / lambda234D, 0.},//
                {1., 1., 1.}});

            exponentialQinvUTh = new Matrix(new double[][]{//
                {(lambda234D * lambda238D) / ((lambda230D - lambda238D) * (lambda234D - lambda238D)), 0., 0.},//
                {-(lambda234D * lambda238D) / ((lambda230D - lambda234D) * (lambda234D - lambda238D)), lambda234D / (lambda230D - lambda234D), 0.},//
                {(lambda234D * lambda238D) / ((lambda230D - lambda234D) * (lambda230D - lambda238D)), -lambda234D / (lambda230D - lambda234D), 1.}});

            double tPrecision = 1e-14;
            int iterationMax = 50;

            r230_238InitialT = r232Th_238Ufc.getValue().doubleValue() * (r230Th_238Udi / r232Th_238Udi);

            numberAtomsTimeT = new Matrix(3, 1);
            numberAtomsTimeT.set(0, 0, 1.);
            numberAtomsTimeT.set(1, 0, r234U_238Ufc.getValue().doubleValue());
            numberAtomsTimeT.set(2, 0, r230Th_238Ufc.getValue().doubleValue());

            Matrix tv = new Matrix(iterationMax, 1, 0.0);

            double slope = 0.0;
            if (r234U_238Ufc.getValue().doubleValue() < (1.47091e-05 + 2.44231 * r230Th_238Ufc.getValue().doubleValue())) {
                // t > 400 ka
                //slope from IC of initial 234U=0 at 400 ka to measured point
                slope = (r234U_238Ufc.getValue().doubleValue() - 3.71920e-05) / (r230Th_238Ufc.getValue().doubleValue() - 9.20522e-06);
                // estimated age based on rotation of isochron
                tv.set(0, 0, (54.0138 - (43.3520 * slope) + (8.762243 * slope * slope)) * 1e6);
            } else if (r234U_238Ufc.getValue().doubleValue() < (0.0000105036 + 3.128406 * r230Th_238Ufc.getValue().doubleValue())) {
                // 200 ka < t < 400 ka
                // slope from IC of initial 234U=0 at 200 ka to measured point
                slope = (r234U_238Ufc.getValue().doubleValue() - 2.37086e-05) / (r230Th_238Ufc.getValue().doubleValue() - 4.2209e-06);
                // estimated age based on rotation of isochron
                tv.set(0, 0, (4.80518 - (2.89959 * slope) + (0.4567031 * slope * slope)) * 1e6);
            } else {
                //t < 200 ka
                tv.set(0, 0, -350194.43 * r230Th_238Ufc.getValue().doubleValue() / (1.559285 * r230Th_238Ufc.getValue().doubleValue() - r234U_238Ufc.getValue().doubleValue()));
            }

            // solve for 230 Th date
            int iNR = 1;
            tv.set(1, 0, tv.get(0, 0) - ft(tv.get(0, 0)) / fpt(tv.get(0, 0)));

            while ((Math.abs(tv.get(iNR, 0) - tv.get(iNR - 1, 0)) / tv.get(iNR, 0) > tPrecision) && (iNR < (iterationMax - 1))) {
                iNR++;
                tv.set(iNR, 0, tv.get(iNR - 1, 0) - ft(tv.get(iNR - 1, 0)) / fpt(tv.get(iNR - 1, 0))); // Newton - Raphson
            }
            double timeUncorrected = tv.get(iNR, 0);

            // Solve for detrital-corrected age
            iNR = 1;
            tv.set(0, 0, timeUncorrected);
            tv.set(1, 0, tv.get(0, 0) - dft(tv.get(0, 0)) / dfpt(tv.get(0, 0)));

            while ((Math.abs(tv.get(iNR, 0) - tv.get(iNR - 1, 0)) / tv.get(iNR, 0) > tPrecision) && (iNR < (iterationMax - 1))) {
                iNR++;
                tv.set(iNR, 0, tv.get(iNR - 1, 0) - dft(tv.get(iNR - 1, 0)) / dfpt(tv.get(iNR - 1, 0))); // Newton - Raphson
            }
            double timeCorrected = tv.get(iNR, 0);
            if (!Double.isFinite(timeCorrected)) {
                timeCorrected = 0.0;
            }

            // june 2016 sanity check
            if (timeCorrected < 0.0) {
                timeCorrected = 0.0;
            }

            // detrital - correction calculations and derivatives 
            Matrix numberOfIntialAtoms = exponentialUTh(-timeCorrected).times(numberAtomsTimeT); //ni
            Matrix dEatcorr = exponentialUTh(timeCorrected);
            Matrix dEanegtcorr = exponentialUTh(-timeCorrected);
            double diN238 = r232Th_238Ufc.getValue().doubleValue() / r232Th_238Udi * Math.exp(lambda232D * timeCorrected);
            Matrix diN = new Matrix(new double[][]{{1.0}, {r234U_238Udi}, {r230Th_238Udi}}).times(diN238);
            Matrix nat = numberAtomsTimeT.minus(dEatcorr.times(diN));
            Matrix nai = dEanegtcorr.times(numberAtomsTimeT).minus(diN);

            //J1
            double r08it_r28t_d = r230_238InitialT / r232Th_238Udi;
            double r08it_r08di_d = r232Th_238Ufc.getValue().doubleValue() / r232Th_238Udi;
            double r08it_r28di_d = -r232Th_238Ufc.getValue().doubleValue() * r230Th_238Udi / (r232Th_238Udi * r232Th_238Udi);

            //J2
            Matrix dT_nt
                    = dEanegtcorr.getMatrix(2, 2, 0, 2)//
                            .times(1.0 / (exponentialA.getMatrix(2, 2, 0, 2).times(numberOfIntialAtoms).get(0, 0) //
                                    + lambda232D * r230Th_238Udi / r232Th_238Udi * r232Th_238Ufc.getValue().doubleValue() * Math.exp(lambda232D * timeCorrected)));
            double dT_r08it
                    = -Math.exp(lambda232D * timeCorrected) / (exponentialA.getMatrix(2, 2, 0, 2).times(numberOfIntialAtoms).get(0, 0)//
                    + lambda232D * r230Th_238Udi / r232Th_238Udi * r232Th_238Ufc.getValue().doubleValue() * Math.exp(lambda232D * timeCorrected));

            //J3
            double dN238di_t = lambda232D * r232Th_238Ufc.getValue().doubleValue() / r232Th_238Udi * Math.exp(lambda232D * timeCorrected);
            double dN238di_r28t = Math.exp(lambda232D * timeCorrected) / r232Th_238Udi;
            double dN238di_r28di = -r232Th_238Ufc.getValue().doubleValue() * Math.exp(lambda232D * timeCorrected) / (r232Th_238Udi * r232Th_238Udi);

            //J5
            Matrix dNat_dt = exponentialA.times(dEatcorr).times(diN).times(-1.0);
            Matrix dNat_din = dEatcorr.times(-1.0);
            Matrix dNat_nt = new Matrix(new double[][]{{1., 0., 0.}, {0., 1., 0.}, {0., 0., 1.}});
            Matrix dNai_dt = exponentialA.times(numberOfIntialAtoms).times(-1.0);
            Matrix dNai_din = new Matrix(new double[][]{{-1., 0., 0.}, {0., -1., 0.}, {0., 0., -1.}});
            Matrix dNai_dnt = dEanegtcorr;

            //J6
            double dAr48tc_n238tauth = -nat.get(1, 0) * lambda234D / (nat.get(0, 0) * nat.get(0, 0) * lambda238D);
            double dAr48tc_n234tauth = lambda234D / (nat.get(0, 0) * lambda238D);
            double dAr08tc_n238tauth = -nat.get(2, 0) * lambda230D / (nat.get(0, 0) * nat.get(0, 0) * lambda238D);
            double dAr08tc_n230tauth = lambda230D / (nat.get(0, 0) * lambda238D);
            double dAr48ic_n238iauth = -nai.get(1, 0) * lambda234D / (nai.get(0, 0) * nai.get(0, 0) * lambda238D);
            double dAr48ic_n234iauth = lambda234D / (nai.get(0, 0) * lambda238D);

            // Propagate Uncertainties
            // 1 Uncorrected date
            Matrix dT_ntUncorr = exponentialUTh_0(-timeUncorrected).times(1.0 / exponentialA.getMatrix(2, 2, 0, 2).times(numberOfIntialAtoms).get(0, 0));

            Matrix J1 = new Matrix(7, 6, 0.0);
            // note dNat_nt is eye(3)
            J1.setMatrix(0, 2, 0, 2, dNat_nt);
            J1.setMatrix(4, 6, 3, 5, dNat_nt);
            J1.set(3, 2, r08it_r28t_d);
            J1.set(3, 4, r08it_r08di_d);
            J1.set(3, 5, r08it_r28di_d);

            Matrix J2 = new Matrix(7, 7, 0.0);
            // note dNat_nt is eye(3)
            J2.setMatrix(1, 3, 0, 2, dNat_nt);
            J2.setMatrix(4, 6, 4, 6, dNat_nt);
            J2.setMatrix(0, 0, 0, 1, dT_nt.getMatrix(0, 0, 1, 2));
            J2.set(0, 3, dT_r08it);

            Matrix J3 = new Matrix(6, 7, 0.0);
            J3.set(0, 0, 1.);
            J3.setMatrix(2, 3, 1, 2, new Matrix(new double[][]{{1., 0}, {0., 1.}}));
            J3.setMatrix(4, 5, 4, 5, new Matrix(new double[][]{{1., 0}, {0., 1.}}));
            J3.set(1, 0, dN238di_t);
            J3.set(1, 3, dN238di_r28t);
            J3.set(1, 6, dN238di_r28di);

            Matrix J4 = new Matrix(new double[][]{//
                {1., 0., 0., 0., 0., 0.},
                {0., 1., 0., 0., 0., 0.},
                {0., r234U_238Udi, 0., 0., diN238, 0.},
                {0., r230Th_238Udi, 0., 0., 0., diN238},
                {0., 0., 1., 0., 0., 0.},
                {0., 0., 0., 1., 0., 0.}});

            Matrix J5 = new Matrix(6, 6, 0.);
            J5.set(0, 0, 1.);
            J5.setMatrix(1, 3, 0, 0, dNat_dt);
            J5.setMatrix(1, 3, 1, 3, dNat_din);
            J5.setMatrix(1, 3, 4, 5, dNat_nt.getMatrix(0, 2, 1, 2));
            J5.setMatrix(4, 5, 0, 0, dNai_dt.getMatrix(0, 1, 0, 0));
            J5.setMatrix(4, 5, 1, 3, dNai_din.getMatrix(0, 1, 0, 2));
            J5.setMatrix(4, 5, 4, 5, dNai_dnt.getMatrix(0, 1, 1, 2));

            Matrix J6 = new Matrix(4, 6, 0.);
            J6.set(0, 0, 1.);
            J6.set(1, 1, dAr48tc_n238tauth);
            J6.set(1, 2, dAr48tc_n234tauth);
            J6.set(2, 1, dAr08tc_n238tauth);
            J6.set(2, 3, dAr08tc_n230tauth);
            J6.set(3, 4, dAr48ic_n238iauth);
            J6.set(3, 5, dAr48ic_n234iauth);

            Matrix Cout
                    = J6.times(J5).times(J4).times(J3).times(J2).times(J1).times(covariance_in)//
                            .times(J1.transpose()).times(J2.transpose()).times(J3.transpose()).times(J4.transpose()).times(J5.transpose()).times(J6.transpose());

//        %% Arrange outputs
//
//        outvec(1)  = nat(3)/nat(1) * lambda.Th230/lambda.U238; % detrital-corrected 230Th/238U AR
            // 230Th/238U, detrital Th-corrected ‡	[230Th/238U], 230Th/238U	activity, atom	--	nat(3)/nat(1)	2*sqrt(Cout(3,3))
            double ar230_238corrected = nat.get(2, 0) / nat.get(0, 0) * lambda230D / lambda238D;
            if (!Double.isFinite(ar230_238corrected)) {
                ar230_238corrected = 0.0;
            }

            // june 2016 test for danger zone
            if ((ar230_238corrected < -10.0) || (ar230_238corrected > 10.0)) {
                ar230_238corrected = 0.0;
            }

//        outvec(3)  = nat(2)/nat(1) * lambda.U234/lambda.U238;  % detrital-corrected 234U/238U AR
            double ar234_238corrected = nat.get(1, 0) / nat.get(0, 0) * lambda234D / lambda238D;
            if (!Double.isFinite(ar234_238corrected)) {
                ar234_238corrected = 0.0;
            }

            // june 2016 test for danger zone
            if ((ar234_238corrected < -10.0) || (ar234_238corrected > 10.0)) {
                ar234_238corrected = 0.0;
            }

//        outvec(6)  = tuncorr/1000; % uncorrected date, ka
//        outvec(7) = 2*sqrt( d.t_ntUncorr(2:3)*meas.C(1:2,1:2)*d.t_ntUncorr(2:3)' )/1000 ; % 2s abs, ka
            double uncorrectedDateOneSigmaABS = Math.sqrt(dT_ntUncorr.getMatrix(0, 0, 1, 2)
                    .times(covariance_fc.getMatrix(0, 1, 0, 1))
                    .times(dT_ntUncorr.getMatrix(0, 0, 1, 2).transpose()).get(0, 0));//    ' )/1000 ; % 2s abs, ka
            if (!Double.isFinite(uncorrectedDateOneSigmaABS)) {
                uncorrectedDateOneSigmaABS = 0.0;
            }

//        outvec(8)  = tcorr/1000; % detrital-corrected date, ka
//        outvec(9)  = (tcorr - di.yearsSince1950)/1000; % detrital-corrected date, ka BP (1950)
//        outvec(11) = nai(2)/nai(1) * lambda.U234/lambda.U238; % initial corrected 234/238 AR
            double initialCorrected234_238atomTatio = nai.get(1, 0) / nai.get(0, 0);//*lambda234D /lambda238D;
            if (!Double.isFinite(initialCorrected234_238atomTatio)) {
                initialCorrected234_238atomTatio = 0.0;
            }

//        outvec(2) = 2*sqrt(Cout(3,3))/outvec(1) * 100; % 2s% ar08t corrected
            double ar230_238correctedOneSigmaABS = Math.sqrt(Cout.get(2, 2) / ar230_238corrected);
            if (!Double.isFinite(ar230_238correctedOneSigmaABS)) {
                ar230_238correctedOneSigmaABS = 0.0;
            }

//        outvec(4) = 2*sqrt(Cout(2,2))/outvec(3) * 100; % 2s% ar48t corrected
            double ar234_238correctedOneSigmaABS = Math.sqrt(Cout.get(1, 1) / ar234_238corrected);
            if (!Double.isFinite(ar234_238correctedOneSigmaABS)) {
                ar234_238correctedOneSigmaABS = 0.0;
            }
//        outvec(5) = Cout(2,3)/sqrt(Cout(2,2)*Cout(3,3)); % corr coef ar08t-ar48t

//        outvec(10) = 2*sqrt(Cout(1,1))/1000; % 2s abs detrital-corrected date
            double correctedDateOneSigmaAbs = Math.sqrt(Cout.get(0, 0));
            if (!Double.isFinite(correctedDateOneSigmaAbs)) {
                correctedDateOneSigmaAbs = 0.0;
            }

//        outvec(12) = 2*sqrt(Cout(4,4)); % 2s abs ar48i
            double initialCorrected234_238atomRatioOneSigmaAbs = Math.sqrt(Cout.get(3, 3));
            if (!Double.isFinite(initialCorrected234_238atomRatioOneSigmaAbs)) {
                initialCorrected234_238atomRatioOneSigmaAbs = 0.0;
            }

//        outvec(13) = Cout(1,4)/sqrt(Cout(1,1)*Cout(4,4)); rho between d234uinit and corrected age
            ValueModel dateCorr = new ValueModel(//
                    RadDates.dateCorr.getName(), //
                    new BigDecimal(timeCorrected), ///
                    "ABS", //
                    new BigDecimal(correctedDateOneSigmaAbs), //
                    BigDecimal.ZERO);

            fraction.setRadiogenicIsotopeDateByName(RadDates.dateCorr, dateCorr);

            fraction.getAnalysisMeasure(UThAnalysisMeasures.ar230Th_238Ufc.getName())//
                    .setValue(ar230_238corrected);
            fraction.getAnalysisMeasure(UThAnalysisMeasures.ar230Th_238Ufc.getName())//
                    .setOneSigma(ar230_238correctedOneSigmaABS);

            fraction.getAnalysisMeasure(UThAnalysisMeasures.ar234U_238Ufc.getName())//
                    .setValue(ar234_238corrected);
            fraction.getAnalysisMeasure(UThAnalysisMeasures.ar234U_238Ufc.getName())//
                    .setOneSigma(ar234_238correctedOneSigmaABS);

            BigDecimal secularEquilibrium = lambda238.getValue().divide(lambda234.getValue(), ReduxConstants.mathContext15);
            BigDecimal delta234UInitialValue
                    = new BigDecimal(initialCorrected234_238atomTatio).subtract(secularEquilibrium)//
                            .divide(secularEquilibrium, ReduxConstants.mathContext15).movePointRight(3);
            BigDecimal delta234UInitialValueOneSigmaAbs
                    = new BigDecimal(initialCorrected234_238atomRatioOneSigmaAbs).movePointRight(3);

            ValueModel delta234U = new ValueModel(//
                    UThFractionationCorrectedIsotopicRatios.delta234U.getName(), //
                    delta234UInitialValue, ///
                    "ABS", //
                    delta234UInitialValueOneSigmaAbs, //
                    BigDecimal.ZERO);
            fraction.setRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.delta234U.getName(), delta234U);
        }
    }

    private static Matrix exponentialGUTh(Double t) {
        //mxp.GUTh = @(t) diag([exp(-lambda.U238*t) exp(-lambda.U234*t) exp(-lambda.Th230*t)]);
        Matrix exponentialGUTh = new Matrix(3, 3, 0.);
        exponentialGUTh.set(0, 0, Math.exp(-lambda238D * t));
        exponentialGUTh.set(1, 1, Math.exp(-lambda234D * t));
        exponentialGUTh.set(2, 2, Math.exp(-lambda230D * t));

        return exponentialGUTh;
    }

    private static Matrix exponentialUTh(double t) {
        // mxp.UTh = @(t) mxp.QUTh*mxp.GUTh(t)*mxp.QinvUTh;
        return exponentialQUTh.times(exponentialGUTh(t)).times(exponentialQinvUTh);
    }

    private static Matrix exponentialUTh_0(double t) {
        // mxp.UTh_0 = @(t) mxp.QUTh(3,:)*mxp.GUTh(t)*mxp.QinvUTh; % For the 230 concentration only (to solve for root)
        return exponentialQUTh.getMatrix(2, 2, 0, 2).times(exponentialGUTh(t)).times(exponentialQinvUTh);
    }

    private static double ft(double t) {
        // ft   = @(t) mxp.UTh_0(-t)*nt;
        return exponentialUTh_0(-t).times(numberAtomsTimeT).get(0, 0);
    }

    private static double fpt(double t) {
        // fpt  = @(t) -mxp.A(3,:)*mxp.UTh(-t)*nt;
        return -exponentialA.getMatrix(2, 2, 0, 2).times(exponentialUTh(-t)).times(numberAtomsTimeT).get(0, 0);
    }

    private static double dft(double t) {
        // dft  = @(t) mxp.UTh_0(-t)*nt - init.r08it*exp(lambda.Th232*t);
        return ft(t) - r230_238InitialT * Math.exp(lambda232D * t);
    }

    private static double dfpt(double t) {
        // dfpt = @(t) -mxp.A(3,:)*mxp.UTh(-t)*nt - lambda.Th232*init.r08it*exp(lambda.Th232*t);
        return fpt(t) - lambda232D * r230_238InitialT * Math.exp(lambda232D * t);
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

        // feb 2017 added temp unit Noah confirms
        fraction.getAnalysisMeasure(UThAnalysisMeasures.ar238U_232Thfc.getName())//
                .setValue(//
                        fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.r238U_232Thfc.getName()).getValue()//
                                .multiply(lambda238.getValue())//
                                .divide(lambda232.getValue(), ReduxConstants.mathContext15));
        fraction.getAnalysisMeasure(UThAnalysisMeasures.ar238U_232Thfc.getName())//
                .setOneSigma(//
                        fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.r238U_232Thfc.getName()).getOneSigmaAbs()//
                                .multiply(lambda238.getValue())//
                                .divide(lambda232.getValue(), ReduxConstants.mathContext15));

        // feb 2017 added temp unit Noah confirms
        fraction.getAnalysisMeasure(UThAnalysisMeasures.ar230Th_232Thfc.getName())//
                .setValue(//
                        fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.r230Th_232Thfc.getName()).getValue()//
                                .multiply(lambda230.getValue())//
                                .divide(lambda232.getValue(), ReduxConstants.mathContext15));
        fraction.getAnalysisMeasure(UThAnalysisMeasures.ar230Th_232Thfc.getName())//
                .setOneSigma(//
                        fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.r230Th_232Thfc.getName()).getOneSigmaAbs()//
                                .multiply(lambda230.getValue())//
                                .divide(lambda232.getValue(), ReduxConstants.mathContext15));

        // april 2017 added temp until Noah confirms
        fraction.getAnalysisMeasure(UThAnalysisMeasures.ar226Ra_230Thfc.getName())//
                .setValue(//
                        fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.r226Ba_230Thfc.getName()).getValue()//
                                .multiply(lambda226.getValue())//
                                .divide(lambda230.getValue(), ReduxConstants.mathContext15));
        fraction.getAnalysisMeasure(UThAnalysisMeasures.ar226Ra_230Thfc.getName())//
                .setOneSigma(//
                        fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.r226Ba_230Thfc.getName()).getOneSigmaAbs()//
                                .multiply(lambda226.getValue())//
                                .divide(lambda230.getValue(), ReduxConstants.mathContext15));

        fraction.getAnalysisMeasure(UThAnalysisMeasures.a226Rafc.getName())//
                .setValue(//
                        fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.a226Rafc.getName()).getValue()//
                                .multiply(lambda226.getValue(), ReduxConstants.mathContext15));
        fraction.getAnalysisMeasure(UThAnalysisMeasures.a226Rafc.getName())//
                .setOneSigma(//
                        fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.a226Rafc.getName()).getOneSigmaAbs()//
                                .multiply(lambda226.getValue(), ReduxConstants.mathContext15));

        fraction.getAnalysisMeasure(UThAnalysisMeasures.a230Thfc.getName())//
                .setValue(//
                        fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.a230Thfc.getName()).getValue()//
                                .multiply(lambda230.getValue(), ReduxConstants.mathContext15));
        fraction.getAnalysisMeasure(UThAnalysisMeasures.a230Thfc.getName())//
                .setOneSigma(//
                        fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.a230Thfc.getName()).getOneSigmaAbs()//
                                .multiply(lambda230.getValue(), ReduxConstants.mathContext15));
    }

    public static void calculateMeasuredAtomRatiosFromLegacyActivityRatios(UThLegacyFractionI fraction) {

        BigDecimal myLambda226Value = fraction.getLambda226Legacy().getValue();
        BigDecimal myLambda230Value = fraction.getLambda230Legacy().getValue();
        BigDecimal myLambda232Value = fraction.getLambda232Legacy().getValue();
        BigDecimal myLambda234Value = fraction.getLambda234Legacy().getValue();
        BigDecimal myLambda238Value = fraction.getLambda238Legacy().getValue();
        
        if (lambda238 == null){
            initializeDecayConstants(fraction.getPhysicalConstantsModel());
        }

        // check for gravimetric vs secular equilibrium for tracer
        // UThFractionationCorrectedIsotopicRatios.r232Th_238Ufc was read in directly from csv file
        if (((UThFraction) fraction).isSpikeCalibrationR230_238IsSecular()) {
            fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.r230Th_238Ufc.getName())//
                    .setValue(fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.ar230Th_238Ufc.getName()).getValue()//
                            .multiply(lambda238.getValue())//
                            .divide(lambda230.getValue(), ReduxConstants.mathContext15));
            fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.r230Th_238Ufc.getName())//
                    .setOneSigma(fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.ar230Th_238Ufc.getName()).getOneSigmaAbs()//
                            .multiply(lambda238.getValue())//
                            .divide(lambda230.getValue(), ReduxConstants.mathContext15));
        } else {
            fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.r230Th_238Ufc.getName())//
                    .setValue(fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.ar230Th_238Ufc.getName()).getValue()//
                            .multiply(myLambda238Value)//
                            .divide(myLambda230Value, ReduxConstants.mathContext15));
            fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.r230Th_238Ufc.getName())//
                    .setOneSigma(fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.ar230Th_238Ufc.getName()).getOneSigmaAbs()//
                            .multiply(myLambda238Value)//
                            .divide(myLambda230Value, ReduxConstants.mathContext15));
        }

        if (((UThFraction) fraction).isSpikeCalibrationR234_238IsSecular()) {
            fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.r234U_238Ufc.getName())//
                    .setValue(fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.ar234U_238Ufc.getName()).getValue()//
                            .multiply(lambda238.getValue())//
                            .divide(lambda234.getValue(), ReduxConstants.mathContext15));
            fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.r234U_238Ufc.getName())//
                    .setOneSigma(fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.ar234U_238Ufc.getName()).getOneSigmaAbs()//
                            .multiply(lambda238.getValue())//
                            .divide(lambda234.getValue(), ReduxConstants.mathContext15));
        } else {
            fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.r234U_238Ufc.getName())//
                    .setValue(fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.ar234U_238Ufc.getName()).getValue()//
                            .multiply(myLambda238Value)//
                            .divide(myLambda234Value, ReduxConstants.mathContext15));
            fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.r234U_238Ufc.getName())//
                    .setOneSigma(fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.ar234U_238Ufc.getName()).getOneSigmaAbs()//
                            .multiply(myLambda238Value)//
                            .divide(myLambda234Value, ReduxConstants.mathContext15));
        }

        // return to non-tracer sensitive math
        fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.r230Th_232Thfc.getName())//
                .setValue(fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.ar230Th_232Thfc.getName()).getValue()//
                        .multiply(myLambda232Value)//
                        .divide(myLambda230Value, ReduxConstants.mathContext15));
        fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.r230Th_232Thfc.getName())//
                .setOneSigma(fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.ar230Th_232Thfc.getName()).getOneSigmaAbs()//
                        .multiply(myLambda232Value)//
                        .divide(myLambda230Value, ReduxConstants.mathContext15));

        fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.r238U_232Thfc.getName())//
                .setValue(fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.ar238U_232Thfc.getName()).getValue()//
                        .multiply(myLambda232Value)//
                        .divide(myLambda238Value, ReduxConstants.mathContext15));
        fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.r238U_232Thfc.getName())//
                .setOneSigma(fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.ar238U_232Thfc.getName()).getOneSigmaAbs()//
                        .multiply(myLambda232Value)//
                        .divide(myLambda238Value, ReduxConstants.mathContext15));

        fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.r226Ba_230Thfc.getName())//
                .setValue(fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.ar226Ra_230Thfc.getName()).getValue()//
                        .multiply(myLambda230Value)//
                        .divide(myLambda226Value, ReduxConstants.mathContext15));
        fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.r226Ba_230Thfc.getName())//
                .setOneSigma(fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.ar226Ra_230Thfc.getName()).getOneSigmaAbs()//
                        .multiply(myLambda230Value)//
                        .divide(myLambda226Value, ReduxConstants.mathContext15));

        fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.a226Rafc.getName())//
                .setValue(fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.a226Rafc.getName()).getValue()//
                        .divide(myLambda226Value, ReduxConstants.mathContext15));
        fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.a226Rafc.getName())//
                .setOneSigma(fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.a226Rafc.getName()).getOneSigmaAbs()//
                        .divide(myLambda226Value, ReduxConstants.mathContext15));

        fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.a230Thfc.getName())//
                .setValue(fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.a230Thfc.getName()).getValue()//
                        .divide(myLambda230Value, ReduxConstants.mathContext15));
        fraction.getRadiogenicIsotopeRatioByName(UThFractionationCorrectedIsotopicRatios.a230Thfc.getName())//
                .setOneSigma(fraction.getLegacyActivityRatioByName(UThAnalysisMeasures.a230Thfc.getName()).getOneSigmaAbs()//
                        .divide(myLambda230Value, ReduxConstants.mathContext15));

    }

}
