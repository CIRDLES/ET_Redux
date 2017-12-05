/*
 * FitFunctionTypeEnum.java
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
package org.earthtime.dataDictionaries;

/**
 *
 * @author James F. Bowring
 */
public enum FitFunctionTypeEnum {
    
    /**
     *
     */
    CONSTANT("CONSTANT", "Constant"),
    /**
     * 
     */
    MEAN("MEAN", "Mean"),
    /**
     * MEAN for downhole method
     */
    MEAN_DH("MEAN_DH", "Mean"),
    // added nov 2014 to handle Pbc */204 ratios with negative values for which no log exists
    MEANRATIO("MEANRATIO", "Mean of Ratios"),
    /**
     * 
     */
    EXPONENTIAL("EXPONENTIAL", "Exp"),

    /**
     *
     */
    EXPFAST("EXPFAST", "Exponential fast"),

    /**
     *
     */
    EXPMAT("EXPMAT", "Exponential Cov Mat"),

    /**
     * 
     */
    LINE("LINE", "Line"),
    /**
     * 
     */
    SMOOTHING_SPLINE("SMOOTHING_SPLINE", "Spline"),

    /**
     *
     */
    NONE("NONE", "NONE");
    
    private String name;
    private String prettyName;
    
    private FitFunctionTypeEnum(String name, String prettyName){
        this.name = name;   
        this.prettyName = prettyName;
    }

    /**
     * @return the name
     */
    public String getName () {
        return name;
    }

    /**
     * @return the prettyName
     */
    public String getPrettyName () {
        return prettyName;
    }
}
