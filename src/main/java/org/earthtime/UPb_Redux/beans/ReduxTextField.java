/*
 * ReduxTextField.java
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
package org.earthtime.UPb_Redux.beans;

import java.awt.event.ActionListener;
import javax.swing.JTextField;

/**
 *
 * @author James F. Bowring
 */
public class ReduxTextField extends JTextField implements ReduxSuppressComponentEventsI {

    /**
     * 
     * @param value
     */
    @Override
    public void setValueWithNoEvents ( int value ) {

        // assume only 0 or 1
        ActionListener[] myALs = this.getActionListeners();
        if ( myALs.length == 0 ) {
            this.setText( Integer.toString( value ) );
        } else {
            this.removeActionListener( myALs[0] );
            this.setText( Integer.toString( value ) );
            this.addActionListener( myALs[0] );
        }

    }
}
