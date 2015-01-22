/*
 * ETException.java
 *
 * Created on September 20, 2006, 5:14 AM
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

package org.earthtime.exceptions;

import java.awt.Component;
import javax.swing.JOptionPane;

/**
 *
 * @author James F. Bowring
 */
public class ETException extends Exception{
    
    /**
     * Creates a new instance of ETException
     */
    public ETException() {
    }
    
    
    /**
     * 
     * @param parent
     * @param msg
     */
    public ETException(Component parent, String[] msg) {
        JOptionPane.showMessageDialog(parent,
                msg,
                "EARTHTIME Warning",
                JOptionPane.WARNING_MESSAGE);
    }
    
    /**
     * 
     * @param parent
     * @param msg
     */
    public ETException(Component parent, String msg) {
       // this(parent, new String[] {msg});
        JOptionPane.showMessageDialog(parent,
                msg,
                "EARTHTIME Warning",
                JOptionPane.WARNING_MESSAGE);
    }
    
    
    
}
