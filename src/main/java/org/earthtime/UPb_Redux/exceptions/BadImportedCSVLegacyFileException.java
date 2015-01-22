/*
 * BadImportedCSVLegacyFileException.java
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
package org.earthtime.UPb_Redux.exceptions;

import java.awt.Component;
import org.earthtime.exceptions.ETException;

/**
 *
 * @author James F. Bowring
 */
public class BadImportedCSVLegacyFileException extends ETException{
    
    /**
     * Creates a new instance of BadLabDataException
     */
    public BadImportedCSVLegacyFileException() {
    }
    
    /**
     * 
     * @param parent
     * @param msg
     */
    public BadImportedCSVLegacyFileException(Component parent, String msg) {
        new ETException(parent, new String[] {msg});
        
    }
}
