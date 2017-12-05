/*
 * AliquotDetailsDisplayInterface.java
 *
 *
 * Copyright 2006-2018 James F. Bowring, CIRDLES.org, and Earth-Time.org
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
package org.earthtime.UPb_Redux.dateInterpretation.concordia;

import java.util.Map;
import java.util.Vector;
import org.earthtime.fractions.ETFractionInterface;

/**
 *
 * @author James F. Bowring
 */
public interface AliquotDetailsDisplayInterface {

    /**
     *
     * @return
     */
    Map<String, Map<String, String>> getAliquotOptions();

    /**
     *
     * @return
     */
    Vector<ETFractionInterface> getDeSelectedFractions();
    public void setDeSelectedFractions(Vector<ETFractionInterface> deSelectedFractions);

    /**
     *
     * @return
     */
    Map<String, String> getSelectedAliquotOptions();

    /**
     *
     * @param fractions
     */
    public void setSelectedFractions(Vector<ETFractionInterface> fractions);

    public void setFilteredFractions(Vector<ETFractionInterface> filteredFractions);

    /**
     *
     * @return
     */
    Vector<ETFractionInterface> getSelectedFractions();

    public boolean isShowFilteredEllipses();

    public void setShowFilteredEllipses(boolean showFilteredEllipses);
}
