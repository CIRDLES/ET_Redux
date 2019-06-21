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

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class OpenSystemDateCalculator {

    //         % physical constants 
    static private double lambda234 = 0.00000282206; //% physical constants v 1.1
    static private double lambda230 = 0.0000091705;// % physical constants v 1.1

//        % user / sample inputs 
    static private double pctLoss = 2.5;// % percent loss, percent 
    static private double ar234U238Umeas = 1.1154;// % measured activity ratio from Blanchon dataset 
    static private double ar230Th238Umeas = 0.7490; //% measured activity ratio from Blanchon dataset 
    static private double uncorrectedDate = 118000; //% uncorrected U - Th date, in years

    public static double calculateOpenSystemDate() {

//        % calculate constants f234 and f230 
        double f234 = 1 - pctLoss / 100;
        double f230 = ((f234 - 1) * (4.754 * 234 / 4.184 / 230) + 1 + f234) / 2;
        
        double tol = 1e-3; //% convergence criterion - tolerance for t
        int maxIterations = 100; //  % maximum iterations before hard loop exit

//        % initial values
        double t_n = 0;
        double t_previous = 1e12;
        double iterations = 0;
        double t_nMinus1 = uncorrectedDate - 1000;
        double t_nMinus2 = uncorrectedDate;
        
        while (Math.abs(t_n - t_previous) > tol) {
            
            iterations = iterations + 1;
            
            if (iterations > maxIterations) {
                System.out.println("error: maximum iterations reached");
                break;
            }
            
            t_previous = t_n;
            
            t_n = t_nMinus1 - f(t_nMinus1, f234, f230) * (t_nMinus1 - t_nMinus2)
                    / (f(t_nMinus1, f234, f230) - f(t_nMinus2, f234, f230));
            
            t_nMinus2 = t_nMinus1;
            t_nMinus1 = t_n;
        }

//        % output t_n as the open system date (in years)
        return t_n;
//        % for provided inputs, should get t_n = 1.12440999e+05

    }
    
    private static double ar234U238Ui(double t) {
        return 1.145 + 2e-8 * t;
    }
    
    private static double m(double t, double f234, double f230) {
//            % Equation 2 of Thompson et al. (2003)
        return (1 - f234) * (1 - Math.exp(-lambda234 * t))
                / ((1 - f234 * f230) * (1 - lambda230 / (lambda230 - lambda234) * Math.exp(-lambda234 * t) + lambda234 / (lambda230 - lambda234) * Math.exp(-lambda230 * t))
                + (1 - f230) * lambda230 / (lambda230 - lambda234) * ar234U238Ui(t) * (Math.exp(-lambda234 * t) - Math.exp(-lambda230 * t)));
    }
    
    private static double f(double t, double f234, double f230) {
//        % Equation 1 of Thompson et al. (2003)
        return 1 - Math.exp(-lambda230 * t) + lambda230 / (lambda230 - lambda234) * (ar234U238Ui(t) - 1) * (Math.exp(-lambda234 * t) - Math.exp(-lambda230 * t))
                + 1 / m(t, f234, f230) * (ar234U238Umeas - ((ar234U238Ui(t) - 1) * Math.exp(-lambda234 * t) + 1)) - ar230Th238Umeas;
    }
    public static void main(String[] args) {
        System.out.println(calculateOpenSystemDate());
    }
}
