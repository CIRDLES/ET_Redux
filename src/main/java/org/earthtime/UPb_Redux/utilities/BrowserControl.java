/*
 * BrowserControl.java
 *
 * Created on April 8, 2006, 4:48 PM
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
package org.earthtime.UPb_Redux.utilities;

import java.io.IOException;

/**
 *
 * http://www.javaworld.com/javaworld/javatips/jw-javatip66.html
 *
 *
 * A simple, static class to display a URL in the system browser.


 *
 * Under Unix, the system browser is hard-coded to be 'netscape'.
 * Netscape must be in your PATH for this to work.  This has been
 * tested with the following platforms: AIX, HP-UX and Solaris.


 *
 * Under Windows, this will bring up the default browser under windows,
 * usually either Netscape or Microsoft IE.  The default browser is
 * determined by the OS.  This has been tested under Windows 95/98/NT.


 *
 * Examples:


 *
BrowserControl.displayURL("http://www.javaworld.com")
 *
BrowserControl.displayURL("file://c:\\docs\\index.html")
 *
BrowserContorl.displayURL("file:///user/joe/index.html");
 *

 * Note - you must include the url type -- either "http://" or
 * "file://".
 */
public class BrowserControl {
    // Used to identify the windows platform.

    private static final String WIN_ID = "windows";
    // The default system browser under windows.

    private static final String WIN_PATH = "rundll32";
    // The flag to display a url.

    private static final String WIN_FLAG = "url.dll,FileProtocolHandler";
    // The default browser under unix.

    private static final String MAC_ID = "mac os x";

    /**
     * 
     * @param url
     */
    public static void displayURL(String url) {
        boolean windows = isWindowsPlatform();
        boolean macos = isMacOS();
        String cmd = null;
        String[] cmdArray = null;

        try {
            if (windows) {
                // Jim Bowring - remove "%20" if any
                String temp[] = url.split("%20");
                String urlNew = temp[0];
                for (int i = 1; i < temp.length; i++) {
                    urlNew += " " + temp[i];
                }
                cmd = WIN_PATH + " " + WIN_FLAG + " " + urlNew;
                System.out.println("JIM  " + cmd + "  *****");
                Process p = Runtime.getRuntime().exec(cmd);
            } else {

                if (macos) {
                    cmdArray = new String[]{"open", url};
                    Process p = Runtime.getRuntime().exec(cmdArray);
                }
            }
        } catch (IOException x) {
            // couldn't exec browser
            System.out.println("Could not invoke browser, command=" + cmd);
            System.out.println("Caught: " + x);
        }
    }

    /**
     * Try to determine whether this application is running under Windows
     * or some other platform by examing the "os.name" property.
     *
     * @return true if this application is running under a Windows OS
     */
    public static boolean isWindowsPlatform() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os != null && os.startsWith(WIN_ID)) {
            return true;
        } else {
            return false;
        }

    }

    /**
     * 
     * @return
     */
    public static boolean isMacOS() {

        String os = System.getProperty("os.name").toLowerCase();
        if (os != null && os.startsWith(MAC_ID)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Simple example.
     * @param args 
     */
    public static void main(String[] args) {
        displayURL("http://www.javaworld.com");
    }
}
