/*
 * IncludedTypeEnum.java
 *
 * Created Jul 24, 2011
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
public enum IncludedTypeEnum {

    /**
     * 
     */
    ALL(true),
    /**
     * 
     */
    INCLUDED(true),
    /**
     * 
     */
    EXCLUDED(false);
    boolean included;

    private IncludedTypeEnum ( boolean included ) {
        this.included = included;
    }

    /**
     * @param included 
     * @return the visibleFraction
     */
    public boolean isObjectIncluded (boolean included) {
        boolean result = true;
        
        if (this.equals( ALL)){
            result = true;
        } else {
            result = (!this.included) ^ included;
        }
        
        return result;
    }
      
}
