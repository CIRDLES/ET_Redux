/*
 * Copyright 2019 CIRDLES.
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
package org.earthtime.plots.evolution.openSystem;

import Jama.Matrix;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.dataDictionaries.Lambdas;
import org.earthtime.dataDictionaries.RadDates;
import org.earthtime.plots.evolution.seaWater.SeaWaterInitialDelta234UTableModel;
import org.earthtime.reduxLabData.ReduxLabData;

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class OpenSystemDateCalculator {

    //  physical constants 
    static private double lambda234
            = ReduxLabData.getInstance().getDefaultPhysicalConstantsModel().getDatumByName(Lambdas.lambda234.getName()).getValue().doubleValue();     // 0.00000282206; //% physical constants v 1.1
    static private double lambda230
            = ReduxLabData.getInstance().getDefaultPhysicalConstantsModel().getDatumByName(Lambdas.lambda230.getName()).getValue().doubleValue();     //0.0000091705;// % physical constants v 1.1

    public static ValueModel calculateOpenSystemDate(
            ValueModel pctLossVM,
            ValueModel ar234U238UmeasVM,
            ValueModel ar230Th238UmeasVM,
            double uncorrectedDate,
            SeaWaterInitialDelta234UTableModel seaWaterInitialDelta234UTableModel) {

        double pctLoss = pctLossVM.getValue().doubleValue();
        double ar234U238Umeas = ar234U238UmeasVM.getValue().doubleValue();
        double ar230Th238Umeas = ar230Th238UmeasVM.getValue().doubleValue();

        // calculate constants f234 and f230 
        double f234 = 1.0 - pctLoss / 100.0;
        double f230 = ((f234 - 1.0) * (4.754 * 234 / 4.184 / 230.0) + 1.0 + f234) / 2.0;

        double tol = 1.0e-3; //% convergence criterion - tolerance for t
        int maxIterations = 100; //  % maximum iterations before hard loop exit

        // % initial values
        double t_n = 0.0;
        double t_previous = 1.0e12;
        double iterations = 0.0;
        double t_nMinus1 = uncorrectedDate - 1000.0;
        double t_nMinus2 = uncorrectedDate;

        while (Math.abs(t_n - t_previous) > tol) {

            iterations = iterations + 1;

            if (iterations > maxIterations) {
                System.out.println("error: maximum iterations reached");
                break;
            }

            t_previous = t_n;

            t_n = t_nMinus1 - f(t_nMinus1, f234, f230, ar234U238Umeas, ar230Th238Umeas, seaWaterInitialDelta234UTableModel) * (t_nMinus1 - t_nMinus2)
                    / (f(t_nMinus1, f234, f230, ar234U238Umeas, ar230Th238Umeas, seaWaterInitialDelta234UTableModel)
                    - f(t_nMinus2, f234, f230, ar234U238Umeas, ar230Th238Umeas, seaWaterInitialDelta234UTableModel));

            t_nMinus2 = t_nMinus1;
            t_nMinus1 = t_n;
        }

        // uncertainty calcs per email from Noah July 2019
        // output t_n as the open system date (in years)
        double t = t_n;
        double openSystemDate_oneSigmaAbs = 0.0;
        if (t > 0) {
            double ar234U238Ui = ar234U238Uisw(t, seaWaterInitialDelta234UTableModel);
            double dfdt = lambda230 * Math.exp(-lambda230 * t)
                    + (100
                    * (((lambda230 * lambda234 * Math.exp(-lambda230 * t))
                    / (lambda230 - lambda234) - (lambda230 * lambda234 * Math.exp(-lambda234 * t))
                    / (lambda230 - lambda234))
                    * (((388389323654697.0 * pctLoss) / 36028797018963968.0 - 1.0)
                    * (pctLoss / 100.0 - 1.0) - 1.0) + (388389323654697.0 * ar234U238Ui * lambda230 * pctLoss
                    * (lambda230 * Math.exp(-lambda230 * t) - lambda234 * Math.exp(-lambda234 * t)))
                    / (36028797018963968.0 * (lambda230 - lambda234))) * (Math.exp(-lambda234 * t) * (ar234U238Ui - 1) - ar234U238Umeas + 1.0))
                    / (pctLoss * (Math.exp(-lambda234 * t) - 1.0))
                    + (lambda230 * (lambda230 * Math.exp(-lambda230 * t) - lambda234 * Math.exp(-lambda234 * t))
                    * (ar234U238Ui - 1.0)) / (lambda230 - lambda234) - (100.0 * lambda234 * Math.exp(-lambda234 * t)
                    * ((((388389323654697.0 * pctLoss) / 36028797018963968.0 - 1.0) * (pctLoss / 100.0 - 1.0) - 1.0)
                    * ((lambda234 * Math.exp(-lambda230 * t)) / (lambda230 - lambda234)
                    - (lambda230 * Math.exp(-lambda234 * t)) / (lambda230 - lambda234) + 1.0)
                    + (388389323654697.0 * ar234U238Ui * lambda230 * pctLoss
                    * (Math.exp(-lambda230 * t) - Math.exp(-lambda234 * t)))
                    / (36028797018963968.0 * (lambda230 - lambda234)))
                    * (Math.exp(-lambda234 * t) * (ar234U238Ui - 1.0) - ar234U238Umeas + 1.0))
                    / (pctLoss * Math.pow(Math.exp(-lambda234 * t) - 1.0, 2.0)) + (100.0 * lambda234 * Math.exp(-lambda234 * t)
                    * ((((388389323654697.0 * pctLoss) / 36028797018963968.0 - 1.0) * (pctLoss / 100.0 - 1.0) - 1.0)
                    * ((lambda234 * Math.exp(-lambda230 * t)) / (lambda230 - lambda234) - (lambda230 * Math.exp(-lambda234 * t))
                    / (lambda230 - lambda234) + 1) + (388389323654697.0 * ar234U238Ui * lambda230 * pctLoss
                    * (Math.exp(-lambda230 * t) - Math.exp(-lambda234 * t)))
                    / (36028797018963968.0 * (lambda230 - lambda234))) * (ar234U238Ui - 1.0)) / (pctLoss * (Math.exp(-lambda234 * t) - 1.0));

            double dfdvar00 = (100.0 * ((((388389323654697.0 * pctLoss) / 36028797018963968.0 - 1.0)
                    * (pctLoss / 100.0 - 1.0) - 1.0) * ((lambda234 * Math.exp(-lambda230 * t)) / (lambda230 - lambda234)
                    - (lambda230 * Math.exp(-lambda234 * t)) / (lambda230 - lambda234) + 1) + (388389323654697.0 * ar234U238Ui
                    * lambda230 * pctLoss * (Math.exp(-lambda230 * t) - Math.exp(-lambda234 * t))) / (36028797018963968.0
                    * (lambda230 - lambda234))) * (Math.exp(-lambda234 * t) * (ar234U238Ui - 1.0) - ar234U238Umeas + 1))
                    / (pctLoss * pctLoss * (Math.exp(-lambda234 * t) - 1.0))
                    - (100.0 * (((388389323654697.0 * pctLoss) / 1801439850948198400.0 - 18716932346108417.0 / 900719925474099200.0)
                    * ((lambda234 * Math.exp(-lambda230 * t)) / (lambda230 - lambda234) - (lambda230 * Math.exp(-lambda234 * t))
                    / (lambda230 - lambda234) + 1.0) + (388389323654697.0 * ar234U238Ui * lambda230
                    * (Math.exp(-lambda230 * t) - Math.exp(-lambda234 * t))) / (36028797018963968.0 * (lambda230 - lambda234)))
                    * (Math.exp(-lambda234 * t) * (ar234U238Ui - 1) - ar234U238Umeas + 1)) / (pctLoss * (Math.exp(-lambda234 * t) - 1));

            double dfdvar01 = -(lambda230 * (Math.exp(-lambda230 * t) - Math.exp(-lambda234 * t))) / (lambda230 - lambda234)
                    - (100.0 * Math.exp(-lambda234 * t) * ((((388389323654697.0 * pctLoss) / 36028797018963968.0 - 1)
                    * (pctLoss / 100 - 1) - 1) * ((lambda234 * Math.exp(-lambda230 * t))
                    / (lambda230 - lambda234) - (lambda230 * Math.exp(-lambda234 * t))
                    / (lambda230 - lambda234) + 1) + (388389323654697.0 * ar234U238Ui * lambda230 * pctLoss
                    * (Math.exp(-lambda230 * t) - Math.exp(-lambda234 * t))) / (36028797018963968.0
                    * (lambda230 - lambda234)))) / (pctLoss * (Math.exp(-lambda234 * t) - 1.0))
                    - (9709733091367425.0 * lambda230 * (Math.exp(-lambda230 * t) - Math.exp(-lambda234 * t))
                    * (Math.exp(-lambda234 * t) * (ar234U238Ui - 1) - ar234U238Umeas + 1))
                    / (9007199254740992.0 * (Math.exp(-lambda234 * t) - 1) * (lambda230 - lambda234));

            double dfdvar02 = (100.0 * ((((388389323654697.0 * pctLoss) / 36028797018963968.0 - 1)
                    * (pctLoss / 100.0 - 1.0) - 1.0) * ((lambda234 * Math.exp(-lambda230 * t))
                    / (lambda230 - lambda234) - (lambda230 * Math.exp(-lambda234 * t))
                    / (lambda230 - lambda234) + 1.0) + (388389323654697.0 * ar234U238Ui
                    * lambda230 * pctLoss * (Math.exp(-lambda230 * t) - Math.exp(-lambda234 * t)))
                    / (36028797018963968.0 * (lambda230 - lambda234)))) / (pctLoss * (Math.exp(-lambda234 * t) - 1.0));

            double dfdvar03 = -1.0;

            Matrix dfdvar = new Matrix(new double[][]{//
                {dfdvar00, dfdvar01, dfdvar02, dfdvar03}});

            Matrix dtdvar = new Matrix(new double[][]{//
                {-dfdvar00 / dfdt, -dfdvar01 / dfdt, -dfdvar02 / dfdt, -dfdvar03 / dfdt}});

            //First, assemble a four row by four column covariance matrix with the oneSigmaAbs uncertainties SQUARED = VARIANCE
            // for pctLoss, ar234U238Ui, ar234U238Umeas, and ar230Th238Umeas, in that order, on the diagonal.
            Matrix covMat = new Matrix(new double[][]{//
                {pctLossVM.getOneSigmaAbs().pow(2).doubleValue(), 0., 0., 0.},
                {0., Math.pow(seaWaterInitialDelta234UTableModel.calculateAr234U_238UiswUnct(t), 2.0), 0., 0.},
                {0., 0., ar234U238UmeasVM.getOneSigmaAbs().pow(2).doubleValue(), 0.},
                {0., 0., 0., ar230Th238UmeasVM.getOneSigmaAbs().pow(2).doubleValue()}});

            openSystemDate_oneSigmaAbs = Math.sqrt(dtdvar.times(covMat).times(dtdvar.transpose()).get(0, 0));

//            System.out.println("dfdt = " + dfdt);
//            System.out.println("dfdvar = ");
//            dfdvar.print(new DecimalFormat("0.000000E00"), 14);
//            System.out.println("dtdvar = ");
//            dtdvar.print(new DecimalFormat("0.000000E00"), 14);
//            System.out.println("covMat = ");
//            covMat.print(new DecimalFormat("0.000000E00"), 14);
        }

        ValueModel dateOpenSys = new ValueModel(
                RadDates.dateOpenSys.getName(),
                new BigDecimal(t),
                "ABS",
                new BigDecimal(openSystemDate_oneSigmaAbs),
                BigDecimal.ZERO);

        return dateOpenSys;

    }

    // seawater model
    private static double ar234U238Uisw(double t, SeaWaterInitialDelta234UTableModel seaWaterInitialDelta234UTableModel) {
        return seaWaterInitialDelta234UTableModel.calculateAr234U_238Uisw(t);//               1.145 + 2e-8 * t;
    }

    private static double m(double t, double f234, double f230, SeaWaterInitialDelta234UTableModel seaWaterInitialDelta234UTableModel) {
        //   Equation 2 of Thompson et al. (2003)
        return (1 - f234) * (1 - Math.exp(-lambda234 * t))
                / ((1 - f234 * f230) * (1 - lambda230 / (lambda230 - lambda234) * Math.exp(-lambda234 * t) + lambda234 / (lambda230 - lambda234) * Math.exp(-lambda230 * t))
                + (1 - f230) * lambda230 / (lambda230 - lambda234)
                * ar234U238Uisw(t, seaWaterInitialDelta234UTableModel) * (Math.exp(-lambda234 * t) - Math.exp(-lambda230 * t)));
    }

    private static double f(double t, double f234, double f230, double ar234U238Umeas, double ar230Th238Umeas, SeaWaterInitialDelta234UTableModel seaWaterInitialDelta234UTableModel) {
        //  Equation 1 of Thompson et al. (2003)
        return 1 - Math.exp(-lambda230 * t) + lambda230 / (lambda230 - lambda234) * (ar234U238Uisw(t, seaWaterInitialDelta234UTableModel) - 1)
                * (Math.exp(-lambda234 * t) - Math.exp(-lambda230 * t))
                + 1 / m(t, f234, f230, seaWaterInitialDelta234UTableModel) * (ar234U238Umeas - ((ar234U238Uisw(t, seaWaterInitialDelta234UTableModel) - 1) * Math.exp(-lambda234 * t) + 1)) - ar230Th238Umeas;
    }

    public static void main(String[] args) {

        //    user sample inputs 
        //    pctLoss = 2.5;// % percent loss, percent 
        //    ar234U238Umeas = 1.1154;// % measured activity ratio from Blanchon dataset ;1 sig abs = 0.001
        //    ar230Th238Umeas = 0.7490; //% measured activity ratio from Blanchon dataset ;1 sig abs = 0.00395
        //    uncorrectedDate = 118000; //% uncorrected U - Th date, in years ;1 sig abs = 1.1
        // for provided inputs, should get t_n = 1.12440999e+05
        ValueModel dos = calculateOpenSystemDate(
                new ValueModel("pctLoss", new BigDecimal(2.5), "ABS", BigDecimal.ONE, BigDecimal.ZERO),
                new ValueModel("ar234U238Umeas", new BigDecimal(1.1154), "ABS", new BigDecimal(0.001), BigDecimal.ZERO),
                new ValueModel("ar230Th238Umeas", new BigDecimal(0.7490), "ABS", new BigDecimal(0.00395), BigDecimal.ZERO),
                118000,
                ReduxLabData.getInstance().getDefaultSeaWaterInitialDelta234UTableModel());

        System.out.println(dos.getValue().doubleValue() + "    " + dos.getOneSigmaAbs().doubleValue());
    }
}
