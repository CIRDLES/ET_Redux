/*
 * Copyright 2006-2015 CIRDLES.org.
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
package org.earthtime.UTh_Redux.fractions;

import java.math.BigDecimal;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.UPb_Redux.valueModels.ValueModelReferenced;
import org.earthtime.dataDictionaries.UThAnalysisMeasures;
import org.earthtime.dataDictionaries.UncertaintyTypesEnum;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public class UThLegacyFraction extends UThFraction implements UThLegacyFractionI {

    protected ValueModel[] legacyActivityRatios;
    protected ValueModel lambda230Legacy;
    protected ValueModel lambda232Legacy;
    protected ValueModel lambda234Legacy;
    protected ValueModel lambda235Legacy;
    protected ValueModel lambda238Legacy;

    public void useLegacyPhysicalConstantsD1() {
        lambda230Legacy = new ValueModelReferenced(//
                "lambda230", new BigDecimal(0.00000919525053474941), //
                "PCT", new BigDecimal(0.3039), BigDecimal.ZERO,//
                "Meadows et al 1980");

        lambda232Legacy = new ValueModelReferenced(//
                "lambda230", new BigDecimal(4.93343E-11), //
                "PCT", new BigDecimal(0.042769), BigDecimal.ZERO,//
                "taken from D2");

        lambda234Legacy = new ValueModelReferenced(//
                "lambda230", new BigDecimal(0.000002835), //
                "PCT", new BigDecimal(0.1998), BigDecimal.ZERO,//
                "under discussion");
        
        lambda238Legacy = new ValueModelReferenced(//
                "lambda230", new BigDecimal(0.000000000155125), //
                "PCT", new BigDecimal(0.053505), BigDecimal.ZERO,//
                "from D2");
    }

    public UThLegacyFraction() {
        super();
        this.legacy = true;

        legacyActivityRatios = valueModelArrayFactory(UThAnalysisMeasures.getNames(), UncertaintyTypesEnum.ABS.getName());
        
        lambda230Legacy = new ValueModelReferenced();
        lambda232Legacy = new ValueModelReferenced();
        lambda234Legacy = new ValueModelReferenced();
        lambda235Legacy = new ValueModelReferenced();
        lambda238Legacy = new ValueModelReferenced();
    }

    /**
     * @return the legacyActivityRatios
     */
    @Override
    public ValueModel[] getLegacyActivityRatios() {
        return legacyActivityRatios;
    }

    /**
     * @param legacyActivityRatios the legacyActivityRatios to set
     */
    @Override
    public void setLegacyActivityRatios(ValueModel[] legacyActivityRatios) {
        this.legacyActivityRatios = legacyActivityRatios;
    }

    /**
     * @return the lambda230Legacy
     */
    public ValueModel getLambda230Legacy() {
        return lambda230Legacy;
    }

    /**
     * @return the lambda232Legacy
     */
    public ValueModel getLambda232Legacy() {
        return lambda232Legacy;
    }

    /**
     * @return the lambda234Legacy
     */
    public ValueModel getLambda234Legacy() {
        return lambda234Legacy;
    }

    /**
     * @return the lambda235Legacy
     */
    public ValueModel getLambda235Legacy() {
        return lambda235Legacy;
    }

    /**
     * @return the lambda238Legacy
     */
    public ValueModel getLambda238Legacy() {
        return lambda238Legacy;
    }

}
