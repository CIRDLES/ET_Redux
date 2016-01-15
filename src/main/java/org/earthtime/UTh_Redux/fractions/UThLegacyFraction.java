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

import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.UPb_Redux.valueModels.ValueModelReferenced;
import org.earthtime.dataDictionaries.Lambdas;
import org.earthtime.dataDictionaries.UThAnalysisMeasures;
import org.earthtime.dataDictionaries.UncertaintyTypesEnum;
import org.earthtime.reduxLabData.ReduxLabData;

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

    @Override
    public void useLegacyPhysicalConstantsD1() {
        try {
            setPhysicalConstantsModel(ReduxLabData.getInstance().getAPhysicalConstantsModel("EARTHTIME Physical Constants Model v.0.1"));
        } catch (BadLabDataException badLabDataException) {
        }

        lambda230Legacy = physicalConstantsModel.getDatumByName(Lambdas.lambda230.getName());
        lambda232Legacy = physicalConstantsModel.getDatumByName(Lambdas.lambda232.getName());
        lambda234Legacy = physicalConstantsModel.getDatumByName(Lambdas.lambda234.getName());
        lambda238Legacy = physicalConstantsModel.getDatumByName(Lambdas.lambda238.getName());
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
