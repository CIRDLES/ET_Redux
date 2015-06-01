/*
 * CommonLeadLossCorrectionSchemeA2.java
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

import java.math.BigDecimal;
import java.util.SortedMap;
import org.earthtime.Tripoli.fitFunctions.algorithms.AbstractFunctionOfT;
import org.earthtime.Tripoli.fitFunctions.algorithms.NewtonRaphsonScalarSolver;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbFraction;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import static org.earthtime.ratioDataModels.initialPbModelsET.commonLeadLossCorrectionSchemes.CommonLeadLossCorrectionSchemeA1.calculateOneSigmaAbsUncertainty;

/**
 *
 * @author James F. Bowring
 */
public class CommonLeadLossCorrectionSchemeA2 extends AbstractCommonLeadLossCorrectionScheme {

    // Class variables
    private static final long serialVersionUID = -8578616106632214764L;
    private static CommonLeadLossCorrectionSchemeA2 instance = null;

    CommonLeadLossCorrectionSchemeA2() {
        super("A2", true);
    }

    /**
     *
     * @return
     */
    public static CommonLeadLossCorrectionSchemeA2 getInstance() {
        if (instance == null) {
            instance = new CommonLeadLossCorrectionSchemeA2();
        }
        return instance;
    }

    /**
     *
     * @param fraction
     * @param r238_235s
     * @param r238_206fc
     * @param r207_206fc
     * @return
     */
    public double ageForScheme(UPbFraction fraction, double r238_235s, double r238_206fc, double r207_206fc) {
        double age;

        double lambda235 = 0.00000000098485;
        double lambda238 = 0.000000000155125;

        double t0;

        AbstractFunctionOfT f0;
        AbstractFunctionOfT fp;

        // add in logic
        if (r207_206fc > (1.1651 - 0.63334 * r238_206fc)) {
            t0 = (7000.0 / (1.0592903 * (r238_206fc + 0.013921017)) + 4.0914451 + (-7000.0 / (0.88941914 * (r238_206fc + 0.12713499)) - 5.5997270) * r207_206fc);// * 1.0e6;
            System.out.println("\nt0  " + t0);
            f0 = new SchemeA2_f0_a(lambda235, lambda238, r238_235s, r238_206fc, r207_206fc);
            fp = new SchemeA2_fp_a(lambda235, lambda238, r238_235s, r238_206fc, r207_206fc);

        } else { // (r207_206fc <= (1.1651 - 0.63334 * r238_206fc))
            t0 = 4.3 * 1.0e3;
            System.out.println("\nt0  " + t0);
            f0 = new SchemeA2_f0_b(lambda235, lambda238, r238_235s, r238_206fc, r207_206fc);
            fp = new SchemeA2_fp_b(lambda235, lambda238, r238_235s, r238_206fc, r207_206fc);
        }

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
    public ValueModel calculatePbCorrectedAge(SortedMap<String, ValueModel> parameters, SortedMap<String, BigDecimal> staceyKramerCorrectionParameters, boolean useStaceyKramer, ValueModel r238_235sVM, ValueModel lambda235VM, ValueModel lambda238VM) {
        ValueModel r238_206fcVM = parameters.get("r238_206fc");
        ValueModel r207_206fcVM = parameters.get("r207_206fc");

        double age;

        double r238_206fc = r238_206fcVM.getValue().doubleValue();
        double r207_206fc = r207_206fcVM.getValue().doubleValue();
        double r238_235s = r238_235sVM.getValue().doubleValue();

//        double r238_206fcOneSigmaAbs = r238_206fcVM.getOneSigmaAbs().doubleValue();
//        double r207_206fcOneSigmaAbs = r207_206fcVM.getOneSigmaAbs().doubleValue();
//        double r238_235sOneSigmaAbs = r238_235sVM.getOneSigmaAbs().doubleValue();

        double lambda235 = lambda235VM.getValue().doubleValue();//  0.00000000098485;
        double lambda238 = lambda238VM.getValue().doubleValue();// 0.000000000155125;

        double t0;

        AbstractFunctionOfT f0;
        AbstractFunctionOfT fp;

        // add in logic
        if (r207_206fc > (1.1651 - 0.63334 * r238_206fc)) {
            t0 = (7000.0 / (1.0592903 * (r238_206fc + 0.013921017)) + 4.0914451 + (-7000.0 / (0.88941914 * (r238_206fc + 0.12713499)) - 5.5997270) * r207_206fc);// * 1.0e6;
//            System.out.println("\nt0  " + t0);
            f0 = new SchemeA2_f0_a(lambda235, lambda238, r238_235s, r238_206fc, r207_206fc);
            fp = new SchemeA2_fp_a(lambda235, lambda238, r238_235s, r238_206fc, r207_206fc);

        } else { // (r207_206fc <= (1.1651 - 0.63334 * r238_206fc))
            t0 = 4.3 * 1.0e3;
//            System.out.println("\nt0  " + t0);
            f0 = new SchemeA2_f0_b(lambda235, lambda238, r238_235s, r238_206fc, r207_206fc);
            fp = new SchemeA2_fp_b(lambda235, lambda238, r238_235s, r238_206fc, r207_206fc);
        }

        NewtonRaphsonScalarSolver solver = new NewtonRaphsonScalarSolver(t0, f0, fp);

        age = solver.solveForT();

//        // UNCERTAINTY
//        double df0_dR238_206fc = //
//                -(Math.exp(lambda235 * age) - 1) / r238_235s - (121750 * Math.exp(3700 * lambda235) - 121750 * Math.exp(lambda235 * age)//
//                - 22402053 * Math.exp(lambda238 * age) - 121750 * Math.exp(3700 * lambda235) * Math.exp(lambda238 * age)//
//                + 121750 * Math.exp(lambda235 * age) * Math.exp(lambda238 * age) + 22402053) / (16786890 * Math.exp(3700 * lambda238)//
//                - 16786890 * Math.exp(lambda238 * age) + 19220472);
//        double df0_dR207_206fc = 1.0;
//        double df0_dR238_235s = (r238_206fc * (Math.exp(lambda235 * age) - 1)) / (r238_235s * r238_235s);
//        double df0_dLambda238 = //
//                (((9009500 * Math.exp(3700 * lambda238) + 9009500 * r238_206fc * Math.exp(3700 * lambda238)) * (121750 * Math.exp(3700 * lambda235)//
//                - 121750 * Math.exp(lambda235 * age) + 22402053)) / 6894 - (Math.exp(lambda238 * age) * (121750 * Math.exp(3700 * lambda235)//
//                - 121750 * Math.exp(lambda235 * age) + 22402053) * (2435 * age + 9009500 * r238_206fc * Math.exp(3700 * lambda238) //
//                - 353 * r238_206fc * age - 2435 * r238_206fc * age * Math.exp(3700 * lambda238))) / 6894) / Math.pow(2435 * Math.exp(3700 * lambda238) - 2435 * Math.exp(lambda238 * age) + 2788, 2);
//        double df0_dLambda235 = //
//                -(450475000 * Math.exp(3700 * lambda235) + 450475000 * r238_206fc * Math.exp(3700 * lambda235) - 121750 * age * Math.exp(lambda235 * age)//
//                - 450475000 * r238_206fc * Math.exp(3700 * lambda235) * Math.exp(lambda238 * age) - 121750 * r238_206fc * age * Math.exp(lambda235 * age)//
//                + 121750 * r238_206fc * age * Math.exp(lambda235 * age) * Math.exp(lambda238 * age)) / (16786890 * Math.exp(3700 * lambda238)//
//                - 16786890 * Math.exp(lambda238 * age) + 19220472) - (r238_206fc * age * Math.exp(lambda235 * age)) / r238_235s;
//        double df0_dt =//
//                (121750 * lambda235 * Math.exp(lambda235 * age) + 121750 * lambda238 * r238_206fc * Math.exp(3700 * lambda235 + lambda238 * age) //
//                - 121750 * lambda235 * r238_206fc * Math.exp(age * (lambda235 + lambda238)) - 121750 * lambda238 * r238_206fc * Math.exp(age * (lambda235 + lambda238))//
//                - 16786890 * lambda238 * r207_206fc * Math.exp(lambda238 * age) + 121750 * lambda235 * r238_206fc * Math.exp(lambda235 * age) //
//                + 22402053 * lambda238 * r238_206fc * Math.exp(lambda238 * age)) / (6894 * (2435 * Math.exp(3700 * lambda238) - 2435 * Math.exp(lambda238 * age) + 2788))//
//                + (2435 * lambda238 * Math.exp(lambda238 * age) * (19220472 * r207_206fc + 16786890 * r207_206fc * Math.exp(3700 * lambda238)//
//                + Math.exp(lambda235 * age) * (121750 * r238_206fc + 121750) - 121750 * r238_206fc * Math.exp(age * (lambda235 + lambda238))//
//                - 16786890 * r207_206fc * Math.exp(lambda238 * age) + 121750 * r238_206fc * Math.exp(3700 * lambda235 + lambda238 * age) //
//                - Math.exp(3700 * lambda235) * (121750 * r238_206fc + 121750) + 44804106 * r238_206fc * Math.exp((lambda238 * age) / 2) * Math.sinh((lambda238 * age) / 2) - 22402053))//
//                / (6894 * Math.pow(2435 * Math.exp(3700 * lambda238) - 2435 * Math.exp(lambda238 * age) + 2788, 2)) - (lambda235 * r238_206fc * Math.exp(lambda235 * age)) / r238_235s;
//
//        double dt_dR238_206fc = -df0_dR238_206fc / df0_dt;
//        double dt_dR207_206fc = -df0_dR207_206fc / df0_dt;
//        double dt_dR238_235s = -df0_dR238_235s / df0_dt;
//
//        double dt_dLambda238 = -df0_dLambda238 / df0_dt;
//        double dt_dLambda235 = -df0_dLambda235 / df0_dt;
//
//        Matrix Srpbc = new Matrix(3, 3, 0.0);
//        Srpbc.set(0, 0, Math.pow(r238_206fcOneSigmaAbs, 2));
//        Srpbc.set(1, 1, Math.pow(r207_206fcOneSigmaAbs, 2));
//        Srpbc.set(2, 2, Math.pow(r238_235sOneSigmaAbs, 2));
//
//        Matrix Jtrpbc = new Matrix(3, 1, 0.0);
//        Jtrpbc.set(0, 0, dt_dR238_206fc);
//        Jtrpbc.set(1, 0, dt_dR207_206fc);
//        Jtrpbc.set(2, 0, dt_dR238_235s);
//
//        Matrix Su = Jtrpbc.transpose().times(Srpbc).times(Jtrpbc);
        
        
        BigDecimal correctedAge = new BigDecimal(age * 1.0e6);
        // sept 2014 - write over estimated date
        staceyKramerCorrectionParameters.put("skEstimatedDate", correctedAge.movePointLeft(6));
        ValueModel r207_206cVM = determineR207_206cUsingStaceyKramer(staceyKramerCorrectionParameters);
        BigDecimal oneSigmaAbsUnct = calculateOneSigmaAbsUncertainty(r238_206fcVM, r207_206fcVM, r207_206cVM, r238_235sVM, lambda235, lambda238, age * 1.0e6);

        return new ValueModel("PbcCorr_UPb_Date", correctedAge, "ABS", oneSigmaAbsUnct, BigDecimal.ZERO);
    }

    private static class SchemeA2_f0_a extends AbstractFunctionOfT {

        private final double lambda235;
        private final double lambda238;
        private final double r238_235s;
        private final double r238_206fc;
        private final double r207_206fc;

        public SchemeA2_f0_a(double lambda235, double lambda238, double r238_235s, double r238_206fc, double r207_206fc) {
            this.lambda235 = lambda235 * 1.0e6;
            this.lambda238 = lambda238 * 1.0e6;
            this.r238_235s = r238_235s;
            this.r238_206fc = r238_206fc;
            this.r207_206fc = r207_206fc;
        }

        @Override
        public double f(double t) {
            double f0 = //                    
                    (19220472 * r207_206fc //
                    - 22402053 * r238_206fc - //
                    121750 * Math.exp(3700 * lambda235)//
                    + 121750 * Math.exp(lambda235 * t) //
                    + 16786890 * r207_206fc * Math.exp(3700 * lambda238) //
                    - 121750 * r238_206fc * Math.exp(3700 * lambda235) //
                    - 16786890 * r207_206fc * Math.exp(lambda238 * t) //
                    + 121750 * r238_206fc * Math.exp(lambda235 * t) //
                    + 22402053 * r238_206fc * Math.exp(lambda238 * t) //
                    - 121750 * r238_206fc * Math.exp(lambda235 * t) * Math.exp(lambda238 * t) //
                    + 121750 * r238_206fc * Math.exp(3700 * lambda235) * Math.exp(lambda238 * t) //
                    - 22402053) / (6894 * (2435 * Math.exp(3700 * lambda238) //
                    - 2435 * Math.exp(lambda238 * t) + 2788))//
                    - (r238_206fc * (Math.exp(lambda235 * t) - 1)) / r238_235s;

            return f0;
        }
    }

    private static class SchemeA2_f0_b extends AbstractFunctionOfT {

        private final double lambda235;
        private final double lambda238;
        private final double r238_235s;
        private final double r238_206fc;
        private final double r207_206fc;

        public SchemeA2_f0_b(double lambda235, double lambda238, double r238_235s, double r238_206fc, double r207_206fc) {
            this.lambda235 = lambda235 * 1.0e6;
            this.lambda238 = lambda238 * 1.0e6;
            this.r238_235s = r238_235s;
            this.r238_206fc = r238_206fc;
            this.r207_206fc = r207_206fc;
        }

        @Override
        public double f(double t) {
            double f0 = //
                    (32081229.0 * r207_206fc //
                    - 35483418.0 * r238_206fc //
                    - 179750.0 * Math.exp(4570.0 * lambda235) //
                    + 179750.0 * Math.exp(lambda235 * t) //
                    + 24783930.0 * r207_206fc * Math.exp(4570.0 * lambda238) //
                    - 179750.0 * r238_206fc * Math.exp(4570.0 * lambda235) //
                    - 24783930.0 * r207_206fc * Math.exp(lambda238 * t) //
                    + 179750.0 * r238_206fc * Math.exp(lambda235 * t) //
                    + 35483418.0 * r238_206fc * Math.exp(lambda238 * t) //
                    - 179750.0 * r238_206fc * Math.exp(lambda235 * t) * Math.exp(lambda238 * t) //
                    + 179750.0 * r238_206fc * Math.exp(4570.0 * lambda235) * Math.exp(lambda238 * t) //
                    - 35483418.0)//
                    / (3447.0 //
                    * (7190.0 * Math.exp(4570.0 * lambda238) //
                    - 7190.0 * Math.exp(lambda238 * t) + 9307.0))//
                    - (r238_206fc * (Math.exp(lambda235 * t) - 1.0))//
                    / r238_235s;

            return f0;
        }

    }

    private class SchemeA2_fp_a extends AbstractFunctionOfT {

        private final double lambda235;
        private final double lambda238;
        private final double r238_235s;
        private final double r238_206fc;
        private final double r207_206fc;

        public SchemeA2_fp_a(double lambda235, double lambda238, double r238_235s, double r238_206fc, double r207_206fc) {
            this.lambda235 = lambda235 * 1.0e6;
            this.lambda238 = lambda238 * 1.0e6;
            this.r238_235s = r238_235s;
            this.r238_206fc = r238_206fc;
            this.r207_206fc = r207_206fc;
        }

        @Override
        public double f(double t) {
            double fp = //
                    (121750 * lambda235 * Math.exp(lambda235 * t)//
                    + 121750 * lambda238 * r238_206fc * Math.exp(3700 * lambda235 + lambda238 * t)//
                    - 121750 * lambda235 * r238_206fc * Math.exp(t * (lambda235 + lambda238))//
                    - 121750 * lambda238 * r238_206fc * Math.exp(t * (lambda235 + lambda238))//
                    - 16786890 * lambda238 * r207_206fc * Math.exp(lambda238 * t)//
                    + 121750 * lambda235 * r238_206fc * Math.exp(lambda235 * t) //
                    + 22402053 * lambda238 * r238_206fc * Math.exp(lambda238 * t)) / (6894 * (2435 * Math.exp(3700 * lambda238)//
                    - 2435 * Math.exp(lambda238 * t) + 2788)) //
                    + (2435 * lambda238 * Math.exp(lambda238 * t) * (19220472 * r207_206fc //
                    + 16786890 * r207_206fc * Math.exp(3700 * lambda238) //
                    + Math.exp(lambda235 * t) * (121750 * r238_206fc + 121750) //
                    - 121750 * r238_206fc * Math.exp(t * (lambda235 + lambda238))//
                    - 16786890 * r207_206fc * Math.exp(lambda238 * t)//
                    + 121750 * r238_206fc * Math.exp(3700 * lambda235 + lambda238 * t) //
                    - Math.exp(3700 * lambda235) * (121750 * r238_206fc + 121750)//
                    + 44804106 * r238_206fc * Math.exp((lambda238 * t) / 2) * Math.sinh((lambda238 * t) / 2) - 22402053))//
                    / (6894//
                    * (2435 * Math.exp(3700 * lambda238) - 2435 * Math.exp(lambda238 * t) + 2788) * (2435 * Math.exp(3700 * lambda238) - 2435 * Math.exp(lambda238 * t) + 2788))//
                    - (lambda235 * r238_206fc * Math.exp(lambda235 * t)) / r238_235s;

            return fp;
        }

    }

    private class SchemeA2_fp_b extends AbstractFunctionOfT {

        private final double lambda235;
        private final double lambda238;
        private final double r238_235s;
        private final double r238_206fc;
        private final double r207_206fc;

        public SchemeA2_fp_b(double lambda235, double lambda238, double r238_235s, double r238_206fc, double r207_206fc) {
            this.lambda235 = lambda235 * 1.0e6;
            this.lambda238 = lambda238 * 1.0e6;
            this.r238_235s = r238_235s;
            this.r238_206fc = r238_206fc;
            this.r207_206fc = r207_206fc;
        }

        @Override
        public double f(double t) {

            double fp = //
                    (179750 * lambda235 * Math.exp(lambda235 * t) //
                    + 179750 * lambda238 * r238_206fc * Math.exp(4570 * lambda235 + lambda238 * t) //
                    - 179750 * lambda235 * r238_206fc * Math.exp(t * (lambda235 + lambda238)) //
                    - 179750 * lambda238 * r238_206fc * Math.exp(t * (lambda235 + lambda238))//
                    - 24783930 * lambda238 * r207_206fc * Math.exp(lambda238 * t) //
                    + 179750 * lambda235 * r238_206fc * Math.exp(lambda235 * t)//
                    + 35483418 * lambda238 * r238_206fc * Math.exp(lambda238 * t))//
                    / (3447 * (7190 * Math.exp(4570 * lambda238) //
                    - 7190 * Math.exp(lambda238 * t) + 9307)) //
                    + (7190 * lambda238 * Math.exp(lambda238 * t) * (32081229 * r207_206fc //
                    + 24783930 * r207_206fc * Math.exp(4570 * lambda238) //
                    + Math.exp(lambda235 * t) * (179750 * r238_206fc + 179750) //
                    - 179750 * r238_206fc * Math.exp(t * (lambda235 + lambda238)) //
                    - 24783930 * r207_206fc * Math.exp(lambda238 * t) //
                    + 179750 * r238_206fc * Math.exp(4570 * lambda235 + lambda238 * t)//
                    - Math.exp(4570 * lambda235) * (179750 * r238_206fc + 179750)//
                    + 70966836 * r238_206fc * Math.exp((lambda238 * t) / 2) * Math.sinh((lambda238 * t) / 2)//
                    - 35483418)) / (3447  *//
                    (7190 * Math.exp(4570 * lambda238) - 7190 * Math.exp(lambda238 * t) + 9307) * (7190 * Math.exp(4570 * lambda238) - 7190 * Math.exp(lambda238 * t) + 9307)) //
                    - (lambda235 * r238_206fc * Math.exp(lambda235 * t)) / r238_235s;

            return fp;
        }

    }


}
