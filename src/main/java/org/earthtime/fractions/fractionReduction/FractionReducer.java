/*
 * FractionReducer.java
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
package org.earthtime.fractions.fractionReduction;

import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.dataDictionaries.Lambdas;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public abstract class FractionReducer {

    // 0. lab constants
    protected static ValueModel lambda230;
    protected static ValueModel lambda231;
    protected static ValueModel lambda232;
    protected static ValueModel lambda234;
    protected static ValueModel lambda235;
    protected static ValueModel lambda238;
    protected static ValueModel gmol204;
    protected static ValueModel gmol206;
    protected static ValueModel gmol207;
    protected static ValueModel gmol208;
    protected static ValueModel gmol235;
    protected static ValueModel gmol238;

    /**
     *
     * @param physicalConstants the value of physicalConstants
     */
    protected static void initializeDecayConstants(
            AbstractRatiosDataModel physicalConstants) {

        lambda230 = physicalConstants.getDatumByName(Lambdas.lambda230.getName()).copy();
        lambda231 = physicalConstants.getDatumByName(Lambdas.lambda231.getName()).copy();
        lambda232 = physicalConstants.getDatumByName(Lambdas.lambda232.getName()).copy();
        lambda234 = physicalConstants.getDatumByName(Lambdas.lambda234.getName()).copy();
        lambda235 = physicalConstants.getDatumByName(Lambdas.lambda235.getName()).copy();
        lambda238 = physicalConstants.getDatumByName(Lambdas.lambda238.getName()).copy();

    }
}
