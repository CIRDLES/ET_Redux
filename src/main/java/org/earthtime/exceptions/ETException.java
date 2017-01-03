/*
 * ETException.java
 *
 * Created on September 20, 2006, 5:14 AM
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

package org.earthtime.exceptions;

import java.awt.Component;

/**
 *
 * @author James F. Bowring
 * @author John Zeringue
 */
public class ETException extends Exception {
    
    public ETException(String message) {
        super(message);
    }
    
    public ETException(Component parent, String message) {
        this(message);
    }
    
    public ETException(Component parent, String[] message) {
        this(String.join(" ", message));
    }
    
}
