/* Copyright 2006-2015 James F. Bowring and www.Earth-Time.org
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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import java.net.URL;
import javax.help.JHelpContentViewer;
import javax.help.plaf.basic.BasicContentViewerUI;
import javax.swing.JComponent;
import javax.swing.event.HyperlinkEvent;

/**
 * taken from http://forum.java.sun.com/thread.jspa?threadID=728061&tstart=30
 * and modified to use our browser control, which is compatible with java 1.5
 * @author Jonathan
 */
/**
 *a UI subclass that will open external links (website or mail links) in an external browser
 */
public class ExternalLinkContentViewerUI extends BasicContentViewerUI {

    /**
     * 
     * @param x
     */
    public ExternalLinkContentViewerUI(JHelpContentViewer x) {
        super(x);
    }

    /**
     * 
     * @param x
     * @return
     */
    public static javax.swing.plaf.ComponentUI createUI(JComponent x) {
        return new ExternalLinkContentViewerUI((JHelpContentViewer) x);
    }

    /**
     * 
     * @param he
     */
    @Override
    public void hyperlinkUpdate(HyperlinkEvent he) {
        if (he.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
            try {
                URL u = he.getURL();
                if (u.getProtocol().equalsIgnoreCase("mailto") || 
                        u.getProtocol().equalsIgnoreCase("https") || 
                        u.getProtocol().equalsIgnoreCase("file") || 
                        u.getProtocol().equalsIgnoreCase("http") || 
                        u.getProtocol().equalsIgnoreCase("ftp")) {
                    BrowserControl.displayURL(u.toString());
                    return;
                }
            } catch (Throwable t) {
            }
        }
        super.hyperlinkUpdate(he);
    }
}
