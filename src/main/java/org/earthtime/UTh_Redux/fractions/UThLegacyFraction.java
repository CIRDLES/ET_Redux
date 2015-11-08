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

import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.dataDictionaries.UThActivityRatios;
import org.earthtime.dataDictionaries.UncertaintyTypesEnum;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public class UThLegacyFraction extends UThFraction implements UThLegacyFractionI{

    protected ValueModel[] legacyActivityRatios;

    public UThLegacyFraction() {
        super();
        this.legacy = true;
        
        legacyActivityRatios = valueModelArrayFactory(UThActivityRatios.getNames(), UncertaintyTypesEnum.ABS.getName());
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


}
