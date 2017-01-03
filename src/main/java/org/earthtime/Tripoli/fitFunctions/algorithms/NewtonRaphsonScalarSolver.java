/*
 * NewtonRaphsonScalarSolver.java
 *
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
package org.earthtime.Tripoli.fitFunctions.algorithms;

/**
 *
 * @author James F. Bowring
 */
public class NewtonRaphsonScalarSolver {

    private static final double tRelativeTolerance = 1.0e-7;
    private final double t0;
    private final AbstractFunctionOfT f0;
    private final AbstractFunctionOfT fp;

    /**
     *
     * @param t0
     * @param f0
     * @param fp
     */
    public NewtonRaphsonScalarSolver(double t0, AbstractFunctionOfT f0, AbstractFunctionOfT fp) {
        this.t0 = t0;
        this.f0 = f0;
        this.fp = fp;
    }

    /**
     *
     * @return
     */
    public double solveForT() {
        double tNew = t0;
        double t;
        do {
            t = tNew;

            double f0_ofT = f0.f(t);
            double fp_ofT = fp.f(t);
            tNew = t - (f0_ofT / fp_ofT);
            
//            System.out.println("f0_ofT = " + f0_ofT);
//            System.out.println("fp_ofT = " + fp_ofT);
//            System.out.println("tNew = " + tNew + "\n");

        } while ((Math.abs(t - tNew) / tNew) > tRelativeTolerance);

        return tNew;
    }

}
