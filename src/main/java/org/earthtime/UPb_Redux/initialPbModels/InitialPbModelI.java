/*
 * InitialPbModelI.java
 *
 * Created on August 5, 2007, 10:26 AM
 *
 *
 * Copyright 2006-2017 James F. Bowring and www.Earth-Time.org
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

package org.earthtime.UPb_Redux.initialPbModels;

import java.math.BigDecimal;
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public interface InitialPbModelI {
    
    
    // accessors
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
    abstract String getReference();
    /**
     * 
     * @param reference
     */
    abstract void setReference(String reference);

    /**
     * 
     * @return
     */
    abstract ValueModel[] getRatios();
    /**
     * 
     * @param ratios
     */
    abstract void setRatios(ValueModel[] ratios);
    
    /**
     * 
     * @param ratioName
     * @return
     */
    abstract ValueModel getRatioByName(String ratioName);
    
    // methods
    /**
     * 
     * @return
     */
    abstract InitialPbModel copy();
    
    
    /**
     * 
     * @param estimatedAgeInMA
     * @param lambda238
     * @param lambda235
     * @param lambda232
     */
    abstract void calculateRatios(
            BigDecimal estimatedAgeInMA,
            BigDecimal lambda238,
            BigDecimal lambda235,
            BigDecimal lambda232);
}
