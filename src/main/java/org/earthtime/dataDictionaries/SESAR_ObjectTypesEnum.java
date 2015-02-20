/*
 * SESAR_ObjectTypesEnum.java
 *
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
package org.earthtime.dataDictionaries;

/**
 *
 * @author James F. Bowring
 */
public enum SESAR_ObjectTypesEnum {
    
    /**
     * 
     */
    IndividualSample("Individual Sample"),
    /**
     * 
     */
    Other("Other");
    
    private String name;
    
    private SESAR_ObjectTypesEnum(String name){
        this.name = name;        
    }

    /**
     * @return the name
     */
    public String getName () {
        return name;
    }

    @Override
    public String toString() {
        return name;
    }
    
    
}
