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
package org.earthtime.aliquots;

import java.util.Collections;
import java.util.Vector;
import org.earthtime.UPb_Redux.utilities.comparators.IntuitiveStringComparator;
import org.earthtime.fractions.ETFractionInterface;

/**
 *
 * @author James F. Bowring <bowring at gmail.com>
 */
public interface ReduxAliquotInterface {

    public int getAliquotNumber();

    public void setAliquotNumber(int aliquotNumber);

    public Vector<ETFractionInterface> getAliquotFractions();

    public default Vector<ETFractionInterface> getAliquotFractionsSorted() {
        if (getAliquotFractions() != null) {
            Collections.sort(getAliquotFractions(), ETFractionInterface.FRACTION_ID_ORDER);
        }
        return getAliquotFractions();
    }

    public void setAliquotFractions(Vector<ETFractionInterface> aliquotFractions);

    public boolean isCompiled();

    /**
     *
     * @param compiled
     */
    public void setCompiled(boolean compiled);

    /**
     *
     * @return
     */
    public default boolean containsActiveFractions() {
        return (!getActiveAliquotFractions().isEmpty());
    }

    /**
     *
     * @return
     */
    public default Vector<ETFractionInterface> getActiveAliquotFractions() {
        Vector<ETFractionInterface> retVal = new Vector<>();
        getAliquotFractions().stream().filter((f) -> (!f.isRejected())).forEach((f) -> {
            retVal.add(f);
        });
        return retVal;
    }

    /**
     *
     * @return
     */
    public default Vector<String> getAliquotFractionIDs() {
        Vector<String> retVal = new Vector<>();
        getAliquotFractions().stream().filter((f) -> (!f.isRejected())).forEach((f) -> {
            retVal.add(f.getFractionID());
        });
        
        Collections.sort(retVal, new IntuitiveStringComparator<>());
        return retVal;
    }

    /**
     *
     * @param name
     * @return
     */
    public default ETFractionInterface getAliquotFractionByName(String name) {
        ETFractionInterface retVal = null;
        for (ETFractionInterface f : getAliquotFractions()) {
            if (f.getFractionID().equalsIgnoreCase(name)) {
                retVal = f;
            }
        }
        return retVal;
    }

    /**
     *
     * @param selectedFractionIDs
     * @return
     */
    public default Vector<ETFractionInterface> getAliquotSampleDateModelSelectedFractions(Vector<String> selectedFractionIDs) {
        Vector<ETFractionInterface> retVal = new Vector<>();
        selectedFractionIDs.stream().forEach((fID) -> {
            retVal.add(getAliquotFractionByName(fID));
        });

        return retVal;
    }

    /**
     *
     * @param selectedFractionIDs
     * @return
     */
    public default Vector<ETFractionInterface> getAliquotSampleDateModelDeSelectedFractions(Vector<String> selectedFractionIDs) {
        Vector<ETFractionInterface> retVal = new Vector<>();
        getAliquotFractionIDs().stream().filter((fID) -> (!selectedFractionIDs.contains(fID))).forEach((fID) -> {
            retVal.add(getAliquotFractionByName(fID));
        });

        return retVal;
    }

    public void reduceData();
}
