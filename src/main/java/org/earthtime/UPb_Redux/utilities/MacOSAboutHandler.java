/*
 * MacOSAboutHandler.java
 *
 * Created on April 28, 2007, 7:50 AM
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

package org.earthtime.UPb_Redux.utilities;

import com.apple.eawt.Application;
import com.apple.eawt.ApplicationAdapter;
import com.apple.eawt.ApplicationEvent;
import java.awt.Component;
import javax.swing.AbstractButton;
import javax.swing.JFrame;
import org.earthtime.ETReduxFrame;
import org.earthtime.dialogs.AboutBox;
import org.earthtime.dialogs.PreferencesEditorDialog;

//http://www.java2s.com/ExampleCode/Development-Class/MacOSApplicationAdapter.htm

// http://developer.apple.com/documentation/Java/Reference/1.4.2/appledoc/api/com/apple/eawt/Application.html

/**
 * 
 * @author samuelbowring
 */
public class MacOSAboutHandler extends Application {
    
    JFrame parent;
    //ShutdownHandler shutter;
    
    /**
     * 
     * @param theParent
     */
    public MacOSAboutHandler(JFrame theParent) {
        parent = theParent;
        
        addApplicationListener(new AboutBoxHandler());
        
        setEnabledPreferencesMenu(true);
        addPreferencesMenuItem();
    }
    
    class AboutBoxHandler extends ApplicationAdapter {
        
        @Override
        public void handleAbout(ApplicationEvent event) {
            AboutBox myBox = new AboutBox(parent, true);
            //myBox.setSize(290, 310);
            myBox.setVisible(true);
            
            event.setHandled(true);
        }
        
        @Override
        public void handlePreferences(ApplicationEvent event) {
            PreferencesEditorDialog myPrefs =
                    new PreferencesEditorDialog(
                    parent,
                    true,
                    ((ETReduxFrame)parent).getMyState().getReduxPreferences());
            myPrefs.setSize(375, 540);
            myPrefs.setVisible(true);
            
            ((ETReduxFrame)parent)
            .getTheSample()
            .setFractionDataOverriddenOnImport(
                    myPrefs.getReduxPreferences().isFractionDataOverriddenOnImport());
            
            event.setHandled(true);
        }
        
        /** This is called when the user does Application->Quit */
        @Override
        public void handleQuit(ApplicationEvent event) {
            // TODO this should not be hardcoded --> look up by menuitem name
            
            Component[] sampleFile = parent.getJMenuBar().getMenu(0).getMenuComponents();
            for (Component sampleFile1 : sampleFile) {
                if (sampleFile1.getClass().getName().equalsIgnoreCase("javax.swing.JMenuItem")) {
                    if (((AbstractButton) sampleFile1).getText().equalsIgnoreCase("Exit")) {
                        ((AbstractButton) sampleFile1).doClick();
                        event.setHandled(true);
                    }
                }
            }
            
        }
    }
}