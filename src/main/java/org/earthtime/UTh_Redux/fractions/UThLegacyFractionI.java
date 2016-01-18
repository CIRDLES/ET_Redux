/*
 * UThFractionI.java
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
package org.earthtime.UTh_Redux.fractions;

import java.math.BigDecimal;
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public interface UThLegacyFractionI extends UThFractionI {

    abstract ValueModel[] getLegacyActivityRatios();

    abstract void setLegacyActivityRatios(ValueModel[] compositionalMeasures);

    public default ValueModel getLegacyActivityRatioByName(String arName) {
        for (int i = 0; i < getLegacyActivityRatios().length; i++) {
            if (getLegacyActivityRatios()[i].getName().equalsIgnoreCase(arName.trim())) {
                return getLegacyActivityRatios()[i];
            }
        }
        // return a new model - handles backwards compatible
        // have to add element to array
        ValueModel[] temp = new ValueModel[getLegacyActivityRatios().length + 1];
        System.arraycopy(getLegacyActivityRatios(), 0, temp, 0, getLegacyActivityRatios().length);

        ValueModel arModel
                = new ValueModel(arName.trim(),
                        BigDecimal.ZERO,
                        "ABS",
                        BigDecimal.ZERO, BigDecimal.ZERO);

        temp[getLegacyActivityRatios().length] = arModel;

        setLegacyActivityRatios(temp);

        return arModel;
    }

    public default void setLegacyActivityRatioByName(String arName, ValueModel valueModel) {
        // make sure it exists
        getLegacyActivityRatioByName(arName.trim());
        //find it
        for (int i = 0; i < getLegacyActivityRatios().length; i++) {
            if (getLegacyActivityRatios()[i].getName().equalsIgnoreCase(arName.trim())) {
                getLegacyActivityRatios()[i] = valueModel;
            }
        }
    }

    public void useLegacyPhysicalConstantsD1();

    public void useLegacyPhysicalConstantsD2();

    public void useLegacyPhysicalConstantsD3();

    public ValueModel getLambda230Legacy();

    public ValueModel getLambda232Legacy();

    public ValueModel getLambda234Legacy();

    public ValueModel getLambda235Legacy();

    public ValueModel getLambda238Legacy();
}
