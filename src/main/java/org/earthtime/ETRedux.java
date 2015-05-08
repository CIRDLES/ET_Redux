/*
 * ETRedux.java
 *
 * Created on March 14, 2006, 8:26 PM
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
package org.earthtime;

import java.awt.Font;
import java.io.File;
import javax.help.SwingHelpUtilities;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import org.earthtime.UPb_Redux.dateInterpretation.TestTopsoil;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.UPb_Redux.reduxLabData.ReduxLabData;
import org.earthtime.UPb_Redux.user.ReduxPersistentState;
import org.earthtime.UPb_Redux.utilities.JHelpAction;
import org.earthtime.exceptions.ETWarningDialog;

/**
 *
 * @author James F. Bowring
 */
public class ETRedux {

    // TODO: Proofread defined value models
    private static ReduxPersistentState myState = null;

    private static ReduxLabData myLabData = null;

    /**
     * Version 3.0.0 initiates switch to ET_Redux from U-Pb_Redux
     */
    public static String VERSION = "3.0.7";

    /**
     *
     */
    public static String RELEASE_DATE = "23 April 2015";

    /**
     * Creates a new instance of UPbRedux
     *
     * @param reduxFile
     */
    public ETRedux(File reduxFile) //throws3 IOException, InvalidPreferencesFormatException 
    {
        // get redux persistent state file
        myState = ReduxPersistentState.getExistingPersistentState();

        // get redux labdata file
        myLabData = ReduxLabData.getInstance();

        // set up apple menu bar
        String lcOSName = System.getProperty("os.name").toLowerCase();
        boolean MAC_OS_X = lcOSName.startsWith("mac os x");

        if (MAC_OS_X) {
            //http://www.developer.com/java/other/article.php/1577161
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        }

            // removed feb 2014 to support linux
        //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        UIManager.getLookAndFeelDefaults().put("defaultFont", new Font("SansSerif", Font.PLAIN, 12));

        SwingHelpUtilities.setContentViewerUI("org.earthtime.UPb_Redux.utilities.ExternalLinkContentViewerUI");
        JHelpAction.startHelpWorker("U-Pb_Help.hs");
        ETReduxFrame theUPbReduxFrame = null;

        try {
            theUPbReduxFrame = new ETReduxFrame(myState, myLabData, reduxFile);
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
        ETReduxFrame.setDefaultLookAndFeelDecorated(true);

        ToolTipManager.sharedInstance().setDismissDelay(10000);

        if (theUPbReduxFrame != null) {
            theUPbReduxFrame.setVisible(true);
        }

        TestTopsoil test = new TestTopsoil();
            // installer etc ref
        // http://www.centerkey.com/mac/java/
    }

    // installer etc ref
    // http://www.centerkey.com/mac/java/
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        // this code is experimental for auto-opening of redux files on any os
        // as of dec 31 2006, not operaional and low priority
//        no luck with this yet
//        see http://today.java.net/lpt/a/60
//        Application.getApplication().addApplicationListener(new ApplicationAdapter() {
//
//            public void handleOpenFile(ApplicationEvent evt) {
//                File reduxFile = new File(evt.getFilename());
//                JOptionPane.showMessageDialog(null,
//                        new String[]{evt.getFilename()},
//                        "ET Redux Warning",
//                        JOptionPane.WARNING_MESSAGE);
//                if (reduxFile.exists()) {
//                    new ETRedux(reduxFile);
//                } else {
//                    new ETRedux(new File(""));
//                }
//            }
//        });
        // one argument allowed and it must be a .redux file
        // TODO check if it is .redux
        if (args.length > 0) {
            File reduxFile = new File(args[0]);
            if (reduxFile.exists()) {
                //  try {
                new ETRedux(reduxFile);
                //   } catch (IOException ex) {
                //      ex.printStackTrace();
                //  } catch (InvalidPreferencesFormatException ex) {
                //      ex.printStackTrace();
                //  }
            }
        } else {
            //try {
            new ETRedux(new File(""));
            //} catch (IOException ex) {
            //    ex.printStackTrace();
            //} catch (InvalidPreferencesFormatException ex) {
            //    ex.printStackTrace();
            //}
        }

    }
}
