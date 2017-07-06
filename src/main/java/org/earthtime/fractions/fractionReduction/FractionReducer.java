/*
 * FractionReducer.java
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
package org.earthtime.fractions.fractionReduction;

import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.dataDictionaries.Lambdas;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataModels.physicalConstantsModels.PhysicalConstantsModel;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public abstract class FractionReducer {

    // 0. lab constants
    protected static AbstractRatiosDataModel currentPhysicalConstantsModel;
    protected static ValueModel lambda226;
    protected static ValueModel lambda230;
    protected static ValueModel lambda231;
    protected static ValueModel lambda232;
    protected static ValueModel lambda234;
    protected static ValueModel lambda235;
    protected static ValueModel lambda238;
    protected static double lambda226D;
    protected static double lambda230D;
    protected static double lambda231D;
    protected static double lambda232D;
    protected static double lambda234D;
    protected static double lambda235D;
    protected static double lambda238D;
    protected static ValueModel gmol204;
    protected static ValueModel gmol205;
    protected static ValueModel gmol206;
    protected static ValueModel gmol207;
    protected static ValueModel gmol208;
    protected static ValueModel gmol230;
    protected static ValueModel gmol232;
    protected static ValueModel gmol235;
    protected static ValueModel gmol238;

    /**
     *
     * @param physicalConstantsModel the value of physicalConstantsModel
     */
    protected static void initializeDecayConstants(
            AbstractRatiosDataModel physicalConstantsModel) {

        if (!physicalConstantsModel.equals(currentPhysicalConstantsModel)) {

            currentPhysicalConstantsModel = physicalConstantsModel;
            lambda226 = physicalConstantsModel.getDatumByName(Lambdas.lambda226.getName()).copy();
            lambda230 = physicalConstantsModel.getDatumByName(Lambdas.lambda230.getName()).copy();
            lambda231 = physicalConstantsModel.getDatumByName(Lambdas.lambda231.getName()).copy();
            lambda232 = physicalConstantsModel.getDatumByName(Lambdas.lambda232.getName()).copy();
            lambda234 = physicalConstantsModel.getDatumByName(Lambdas.lambda234.getName()).copy();
            lambda235 = physicalConstantsModel.getDatumByName(Lambdas.lambda235.getName()).copy();
            lambda238 = physicalConstantsModel.getDatumByName(Lambdas.lambda238.getName()).copy();

            lambda226D = lambda226.getValue().doubleValue();
            lambda230D = lambda230.getValue().doubleValue();
            lambda231D = lambda231.getValue().doubleValue();
            lambda232D = lambda232.getValue().doubleValue();
            lambda234D = lambda234.getValue().doubleValue();
            lambda235D = lambda235.getValue().doubleValue();
            lambda238D = lambda238.getValue().doubleValue();

        }
    }

    protected static void initializeAtomicMolarMasses(
            AbstractRatiosDataModel physicalConstants) {

        gmol204 = ((PhysicalConstantsModel) physicalConstants).getAtomicMolarMassByName("gmol204");
        gmol205 = ((PhysicalConstantsModel) physicalConstants).getAtomicMolarMassByName("gmol205");
        gmol206 = ((PhysicalConstantsModel) physicalConstants).getAtomicMolarMassByName("gmol206");
        gmol207 = ((PhysicalConstantsModel) physicalConstants).getAtomicMolarMassByName("gmol207");
        gmol208 = ((PhysicalConstantsModel) physicalConstants).getAtomicMolarMassByName("gmol208");
        gmol230 = ((PhysicalConstantsModel) physicalConstants).getAtomicMolarMassByName("gmol230");
        gmol232 = ((PhysicalConstantsModel) physicalConstants).getAtomicMolarMassByName("gmol232");
        gmol235 = ((PhysicalConstantsModel) physicalConstants).getAtomicMolarMassByName("gmol235");
        gmol238 = ((PhysicalConstantsModel) physicalConstants).getAtomicMolarMassByName("gmol238");
    }

}
