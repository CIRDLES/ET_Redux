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
package org.earthtime.UPb_Redux.dialogs.sampleManagers.sampleDateInterpretationManagers;

import java.util.Vector;
import org.earthtime.dataDictionaries.RadDates;
import org.earthtime.fractions.ETFractionInterface;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public class SampleDateInterpretationsUtilities {

    public static Vector<ETFractionInterface> filterActiveUPbFractions(Vector<ETFractionInterface> fractions, String dateName, double positiveDiscordance, double negativeDiscordance, double percentUncertainty) {
        Vector<ETFractionInterface> filteredFractions = new Vector<>();

        for (ETFractionInterface f : fractions) {
            boolean doAddFraction = !f.isRejected();
            double pctDiscordance = f.getRadiogenicIsotopeDateByName(RadDates.percentDiscordance).getValue().doubleValue();

            if (pctDiscordance >= 0.0) {  //
                // positive percent discordance
                doAddFraction = doAddFraction && (pctDiscordance <= positiveDiscordance);
            } else {
                // negative percent discordance
                doAddFraction = doAddFraction && (pctDiscordance >= negativeDiscordance);
            }

            doAddFraction = doAddFraction //
                    && f.getRadiogenicIsotopeDateByName(dateName).getOneSigmaPct().doubleValue() //
                    <= percentUncertainty;

            //oct 2014
            doAddFraction = doAddFraction //
                    && f.getRadiogenicIsotopeDateByName(dateName).getOneSigmaPct().doubleValue() != 0.0;

            if (doAddFraction) {
                filteredFractions.add(f);
            }
        }
        return filteredFractions;
    }

}
