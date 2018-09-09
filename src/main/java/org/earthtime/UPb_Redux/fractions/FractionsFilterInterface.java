/*
 * Copyright 2006-2018 James F. Bowring, CIRDLES.org, and Earth-Time.org
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
package org.earthtime.UPb_Redux.fractions;

import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;
import org.earthtime.Tripoli.fractions.TripoliFraction;
import org.earthtime.UPb_Redux.fractions.UPbReduxFractions.UPbLAICPMSFraction;
import org.earthtime.dataDictionaries.FractionSelectionTypeEnum;
import org.earthtime.dataDictionaries.IncludedTypeEnum;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public interface FractionsFilterInterface {

    /**
     *
     * @param tripoliFractions
     * @param selection
     * @param visibility
     * @return
     */
    public static SortedSet<TripoliFraction> getTripoliFractionsFiltered(//
            SortedSet<TripoliFraction> tripoliFractions,
            FractionSelectionTypeEnum selection,//
            IncludedTypeEnum visibility) {

        SortedSet<TripoliFraction> filteredFractions = new TreeSet<>();

        Iterator<TripoliFraction> fractionIterator = tripoliFractions.iterator();
        while (fractionIterator.hasNext()) {
            TripoliFraction tf = fractionIterator.next();
            if ((selection.equals(FractionSelectionTypeEnum.ALL) //
                    || //
                    (selection.equals(FractionSelectionTypeEnum.STANDARD) && tf.isStandard())//
                    || //
                    (selection.equals(FractionSelectionTypeEnum.UNKNOWN) && !tf.isStandard()))//
                    && //
                    visibility.isObjectIncluded(tf.isIncluded())) {

                filteredFractions.add(tf);

                if (tf.getuPbFraction() instanceof UPbLAICPMSFraction) {
                    // turn off bad fractions
                    if (!tf.confirmHealthyFraction()) {
                        tf.toggleAllDataExceptShaded(false);
                    }
                }
            }
        }
        return filteredFractions;
    }
}
