/*
 * CommonLeadLossCorrectionSchemeA1.java
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
package org.earthtime.ratioDataModels.initialPbModelsET.commonLeadLossCorrectionSchemes;

import Jama.Matrix;
import java.math.BigDecimal;
import java.util.SortedMap;
import org.earthtime.Tripoli.fitFunctions.algorithms.AbstractFunctionOfT;
import org.earthtime.Tripoli.fitFunctions.algorithms.NewtonRaphsonScalarSolver;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFraction;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.fractionReduction.PbcCorrectionDetails;
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public class CommonLeadLossCorrectionSchemeA1 extends AbstractCommonLeadLossCorrectionScheme {

    // Class variables
    private static final long serialVersionUID = 4164254815966053479L;
    private static CommonLeadLossCorrectionSchemeA1 instance = null;

    private CommonLeadLossCorrectionSchemeA1() {
        super("A1", false);
    }

    /**
     *
     * @return
     */
    public static CommonLeadLossCorrectionSchemeA1 getInstance() {
        if (instance == null) {
            instance = new CommonLeadLossCorrectionSchemeA1();
        }
        return instance;
    }

    private double ageForScheme(UPbFraction fraction, double r238_235s, double r238_206fc, double r207_206fc, double r207_206c) {
        double age;

        double lambda235 = 0.00000000098485;
        double lambda238 = 0.000000000155125;

        double t0;

        if (r207_206c < 1.11) {
            t0 = (r207_206fc - r207_206c) / r238_206fc * (6384.9923 * Math.sqrt(r207_206c - 0.80183883) - 9063.8137) * 1.0e6;
        } else if ((1.11 <= r207_206c) && (r207_206c < 11.2)) {
            t0 = (r207_206fc - r207_206c) / r238_206fc * (1349.6847 * Math.log(r207_206c - 0.78795528) - 2.5240584) * 1.0e6;
        } else {
            t0 = (r207_206fc - r207_206c) / r238_206fc * (109.56243 * Math.sqrt(r207_206c - 10.725608) - 653.360321) * 1.0e6;
        }

        AbstractFunctionOfT f0 = new SchemeA1_f0(lambda235, lambda238, r238_235s, r207_206c, r238_206fc, r207_206fc);
        AbstractFunctionOfT fp = new SchemeA1_fp(lambda235, lambda238, r238_235s, r207_206c, r238_206fc);

        NewtonRaphsonScalarSolver solver = new NewtonRaphsonScalarSolver(t0, f0, fp);

        age = solver.solveForT();

        return age;
    }

    /**
     *
     * @param parameters the value of parameterz
     * @param staceyKramerCorrectionParameters the value of
     * staceyKramerCorrectionParameters
     * @param useStaceyKramer the value of useStaceyKramer
     * @param r238_235sVM the value of parameters
     * @param lambda235VM the value of r238_235s
     * @param lambda238VM the value of lambda235
     * @return
     */
    @Override
    public ValueModel calculatePbCorrectedAge(//
            SortedMap<String, ValueModel> parameters, SortedMap<String, BigDecimal> staceyKramerCorrectionParameters, boolean useStaceyKramer, ValueModel r238_235sVM, ValueModel lambda235VM, ValueModel lambda238VM) {
        ValueModel r238_206fcVM = parameters.get("r238_206fc");
        ValueModel r207_206fcVM = parameters.get("r207_206fc");

        // r207_206c depends on StaceyKramer chosen or not
        ValueModel r207_206cVM;
        if (useStaceyKramer) {
            r207_206cVM = determineR207_206cUsingStaceyKramer(staceyKramerCorrectionParameters);
        } else {
            r207_206cVM = parameters.get("r207_206c");
        }

        double age;

        double r238_206fc = r238_206fcVM.getValue().doubleValue();
        double r207_206fc = r207_206fcVM.getValue().doubleValue();
        double r207_206c = r207_206cVM.getValue().doubleValue();
        double r238_235s = r238_235sVM.getValue().doubleValue();

        double lambda235 = lambda235VM.getValue().doubleValue();//  0.00000000098485;
        double lambda238 = lambda238VM.getValue().doubleValue();// 0.000000000155125;

        double t0;

        if (r207_206c < 1.11) {
            t0 = (r207_206fc - r207_206c) / r238_206fc * (6384.9923 * Math.sqrt(r207_206c - 0.80183883) - 9063.8137) * 1.0e6;
        } else if ((1.11 <= r207_206c) && (r207_206c < 11.2)) {
            t0 = (r207_206fc - r207_206c) / r238_206fc * (1349.6847 * Math.log(r207_206c - 0.78795528) - 2.5240584) * 1.0e6;
        } else {
            t0 = (r207_206fc - r207_206c) / r238_206fc * (109.56243 * Math.sqrt(r207_206c - 10.725608) - 653.360321) * 1.0e6;
        }

        AbstractFunctionOfT f0 = new SchemeA1_f0(lambda235, lambda238, r238_235s, r207_206c, r238_206fc, r207_206fc);
        AbstractFunctionOfT fp = new SchemeA1_fp(lambda235, lambda238, r238_235s, r207_206c, r238_206fc);

        NewtonRaphsonScalarSolver solver = new NewtonRaphsonScalarSolver(t0, f0, fp);

        age = solver.solveForT();

        BigDecimal oneSigmaAbsUnct = calculateOneSigmaAbsUncertainty(r238_206fcVM, r207_206fcVM, r207_206cVM, r238_235sVM, lambda235, lambda238, age);

        return new ValueModel("PbcCorr_UPb_Date", new BigDecimal(age), "ABS", oneSigmaAbsUnct, BigDecimal.ZERO);
    }

    /**
     *
     * @param r238_206fcVM
     * @param r207_206fcVM
     * @param r207_206cVM
     * @param r238_235sVM
     * @param lambda235
     * @param lambda238
     * @param age
     * @return
     */
    protected static BigDecimal calculateOneSigmaAbsUncertainty(//
            ValueModel r238_206fcVM, ValueModel r207_206fcVM, ValueModel r207_206cVM, ValueModel r238_235sVM, double lambda235, double lambda238, double age) {

        double r238_206fc = r238_206fcVM.getValue().doubleValue();
        double r207_206c = r207_206cVM.getValue().doubleValue();
        double r238_235s = r238_235sVM.getValue().doubleValue();

        double r238_206fcOneSigmaAbs = r238_206fcVM.getOneSigmaAbs().doubleValue();
        double r207_206fcOneSigmaAbs = r207_206fcVM.getOneSigmaAbs().doubleValue();
        double r207_206cOneSigmaAbs = r207_206cVM.getOneSigmaAbs().doubleValue();
        double r238_235sOneSigmaAbs = r238_235sVM.getOneSigmaAbs().doubleValue();
//System.out.println("A1 207/6 abs " + r207_206cOneSigmaAbs);
        // UNCERTAINTY
        double df0_dR238_206fc = -(Math.exp(lambda235 * age) + r207_206c * r238_235s - r207_206c * r238_235s * Math.exp(lambda238 * age) - 1) / r238_235s;
        double df0_dR207_206fc = 1.0;
        double df0_dR207_206c = r238_206fc * Math.exp(lambda238 * age) - r238_206fc - 1.0;
        double df0_dR238_235s = (r238_206fc * (Math.exp(lambda235 * age) - 1)) / (r238_235s * r238_235s);
        double df0_dLambda238 = r207_206c * r238_206fc * age * Math.exp(lambda238 * age);
        double df0_dLambda235 = -(r238_206fc * age * Math.exp(lambda235 * age)) / r238_235s;
        double df0_dt = -(r238_206fc * (lambda235 * Math.exp(lambda235 * age) - lambda238 * r207_206c * r238_235s * Math.exp(lambda238 * age))) / r238_235s;

        double dt_dR238_206fc = -df0_dR238_206fc / df0_dt;
        double dt_dR207_206fc = -df0_dR207_206fc / df0_dt;
        double dt_dR207_206c = -df0_dR207_206c / df0_dt;
        double dt_dR238_235s = -df0_dR238_235s / df0_dt;

        double dt_dLambda238 = -df0_dLambda238 / df0_dt;
        double dt_dLambda235 = -df0_dLambda235 / df0_dt;
        Matrix Srpbc = new Matrix(4, 4, 0.0);
        Srpbc.set(0, 0, Math.pow(r238_206fcOneSigmaAbs, 2));
        Srpbc.set(1, 1, Math.pow(r207_206fcOneSigmaAbs, 2));
        Srpbc.set(2, 2, Math.pow(r207_206cOneSigmaAbs, 2));
        Srpbc.set(3, 3, Math.pow(r238_235sOneSigmaAbs, 2));

        Matrix Jtrpbc = new Matrix(4, 1, 0.0);
        Jtrpbc.set(0, 0, dt_dR238_206fc);
        Jtrpbc.set(1, 0, dt_dR207_206fc);
        Jtrpbc.set(2, 0, dt_dR207_206c);
        Jtrpbc.set(3, 0, dt_dR238_235s);

        Matrix Su = Jtrpbc.transpose().times(Srpbc).times(Jtrpbc);
        
        PbcCorrectionDetails.df0_dR238_206fc = df0_dR238_206fc;
        PbcCorrectionDetails.df0_dR207_206fc = df0_dR207_206fc;
        PbcCorrectionDetails.df0_dR207_206c = df0_dR207_206c;
        PbcCorrectionDetails.df0_dR238_235s = df0_dR238_235s;
        PbcCorrectionDetails.df0_dLambda238 = df0_dLambda238;
        PbcCorrectionDetails.df0_dLambda235 = df0_dLambda235;
        PbcCorrectionDetails.df0_dt = df0_dt;

        PbcCorrectionDetails.dt_dR238_206fc = dt_dR238_206fc;
        PbcCorrectionDetails.dt_dR207_206fc = dt_dR207_206fc;
        PbcCorrectionDetails.dt_dR207_206c = dt_dR207_206c;
        PbcCorrectionDetails.dt_dR238_235s = dt_dR238_235s;
        PbcCorrectionDetails.dt_dLambda238 = dt_dLambda238;
        PbcCorrectionDetails.dt_dLambda235 = dt_dLambda235;

        return new BigDecimal(Math.sqrt(Su.get(0, 0)));
    }

    private static class SchemeA1_f0 extends AbstractFunctionOfT {

        private final double lambda235;
        private final double lambda238;
        private final double r238_235s;
        private final double r207_206c;
        private final double r238_206fc;
        private final double r207_206fc;

        public SchemeA1_f0(double lambda235, double lambda238, double r238_235s, double r207_206c, double r238_206fc, double r207_206fc) {
            this.lambda235 = lambda235;
            this.lambda238 = lambda238;
            this.r238_235s = r238_235s;
            this.r207_206c = r207_206c;
            this.r238_206fc = r238_206fc;
            this.r207_206fc = r207_206fc;
        }

        @Override
        public double f(double t) {
            double f0 = //
                    (r238_206fc //
                    - r207_206c * r238_235s //
                    + r207_206fc * r238_235s //
                    - r238_206fc * Math.exp(lambda235 * t) //
                    - r207_206c * r238_206fc * r238_235s //
                    + r207_206c * r238_206fc * r238_235s * Math.exp(lambda238 * t))//
                    / r238_235s;

            return f0;
        }

    }

    private static class SchemeA1_fp extends AbstractFunctionOfT {

        private final double lambda235;
        private final double lambda238;
        private final double r238_235s;
        private final double r207_206c;
        private final double r238_206fc;

        public SchemeA1_fp(double lambda235, double lambda238, double r238_235s, double r207_206c, double r238_206fc) {
            this.lambda235 = lambda235;
            this.lambda238 = lambda238;
            this.r238_235s = r238_235s;
            this.r207_206c = r207_206c;
            this.r238_206fc = r238_206fc;
        }

        @Override
        public double f(double t) {
            double fp = //
                    -(lambda235 * r238_206fc * Math.exp(lambda235 * t) //
                    - lambda238 * r207_206c * r238_206fc * r238_235s * Math.exp(lambda238 * t))//
                    / r238_235s;

            return fp;
        }

    }

    /**
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        System.out.println("CommonLeadLossCorrectionSchemeA1");

        CommonLeadLossCorrectionSchemeA1 cllcsA = new CommonLeadLossCorrectionSchemeA1();

        //double ageA1 = cllcsA.ageForScheme(null, 137.818, 10.0, 0.109, 0.860057344508161);
        double ageA1 = cllcsA.ageForScheme(null, 137.8185, 14.766, 0.0567, 0.87143);
        System.out.println("Scheme A1:   " + ageA1);

    }

}
