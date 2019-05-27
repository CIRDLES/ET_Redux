/*
 * Copyright 2019 CIRDLES.
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
package org.earthtime.plots.evolution;

import java.io.Serializable;
import java.util.Comparator;

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class UpperBoundaryComparator implements Comparator<Double>, Serializable {

    @Override
    public int compare(Double age1, Double age2) {
        int retVal = 0;

        // allow some slop
        if (Math.abs(age1 - age2) > 3.0) {
            retVal = Double.compare(age1, age2);
        }

        return retVal;
    }
}
