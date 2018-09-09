/*
 * PhysicalConstantsI.java
 *
 * Created on August 1, 2007, 7:33 AM
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
package org.earthtime.physicalConstants;

import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public interface PhysicalConstantsI {

    // Field Accessors
    /**
     * 
     * @return
     */
    abstract String getName();

    /**
     * 
     * @param name
     */
    abstract void setName(String name);

    /**
     * 
     * @return
     */
    abstract int getVersion();

    /**
     * 
     * @param version
     */
    abstract void setVersion(int version);

    /**
     * 
     * @return
     */
    abstract ValueModel[] getAtomicMolarMasses();

    /**
     * 
     * @param atomicMolarMasses
     */
    abstract void setAtomicMolarMasses(ValueModel[] atomicMolarMasses);

    /**
     * 
     * @return
     */
    abstract ValueModel[] getMeasuredConstants();

    /**
     * 
     * @param measuredConstants
     */
    abstract void setMeasuredConstants(ValueModel[] measuredConstants);

        /**
     *
     * @param mcName
     * @return
     */
    public default ValueModel getMeasuredConstantByName(String mcName) {
        ValueModel retVal = null;
        for (int i = 0; i < getMeasuredConstants().length; i++) {
            if (getMeasuredConstants()[i].getName().equals(mcName)) {
                retVal = getMeasuredConstants()[i];
                break;
            }
        }
        return retVal;
    }

    /**
     *
     * @param ammName
     * @return
     */
    public default ValueModel getAtomicMolarMassByName(String ammName) {
        ValueModel retVal = null;
        for (int i = 0; i < getAtomicMolarMasses().length; i++) {
            if (getAtomicMolarMasses()[i].getName().equals(ammName)) {
                retVal = getAtomicMolarMasses()[i];
                break;
            }
        }
        return retVal;
    }

    /**
     * 
     * @return
     */
    abstract String getPhysicalConstantsComment();

    /**
     * 
     * @param physicalConstantsComment
     */
    abstract void setPhysicalConstantsComment(String physicalConstantsComment);

    // methods
    /**
     * 
     * @return
     */
    abstract PhysicalConstants Copy();
}
