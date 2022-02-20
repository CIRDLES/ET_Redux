/*
 * ETRedux.java
 *
 * Created on March 14, 2006, 8:26 PM
 *
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
package org.earthtime;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.JOptionPane;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import org.cirdles.commons.util.ResourceExtractor;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.UPb_Redux.user.ReduxPersistentState;
import org.earthtime.exceptions.ETWarningDialog;
import org.earthtime.utilities.VersionChecker;

/**
 *
 * @author James F. Bowring
 */
public class ETRedux {

    // TODO: Proofread defined value models
    private static ReduxPersistentState myState = null;

    /**
     * Version 3.0.0 initiates switch to ET_Redux from U-Pb_Redux
     */
    public static String VERSION = "version";

    /**
     *
     */
    public static String RELEASE_DATE = "date";

    private static final ResourceExtractor RESOURCE_EXTRACTOR
            = new ResourceExtractor(ETRedux.class);

    /**
     * Creates a new instance of UPbRedux
     *
     * @param reduxFile
     */
    public ETRedux(File reduxFile) //throws3 IOException, InvalidPreferencesFormatException 
    {
        // get version number and release date written by pom.xml
        Path resourcePath = RESOURCE_EXTRACTOR.extractResourceAsPath("version.txt");
        Charset charset = Charset.forName("US-ASCII");
        try (BufferedReader reader = Files.newBufferedReader(resourcePath, charset)) {

            String[] versionText = reader.readLine().split("=");
            VERSION = versionText[1].trim();

            String[] versionDate = reader.readLine().split("=");
            RELEASE_DATE = versionDate[1];

            reader.close();
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }

        try {
            boolean upToDate = VersionChecker.checkIfCurrentVersion(VERSION);
            if (!upToDate){
                JOptionPane.showMessageDialog(
                    null,
                    new String[]{"There is a newer version of ET_Redux at " + "https://github.com/CIRDLES/ET_Redux/releases/ " + "."},
                    "ET Redux Announcement",
                    JOptionPane.INFORMATION_MESSAGE);
            }
            
        } catch (Exception exception) {
        }
 
        // get redux persistent state file
        myState = ReduxPersistentState.getExistingPersistentState();

        // set up apple menu bar
        String lcOSName = System.getProperty("os.name").toLowerCase();
        boolean MAC_OS_X = lcOSName.startsWith("mac os x");

        if (MAC_OS_X) {
            //http://www.developer.com/java/other/article.php/1577161
            System.setProperty("apple.laf.useScreenMenuBar", "false");
        }

        // removed feb 2014 to support linux
        //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        /* Set the Metal look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Metal is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Metal".equals(info.getName())) { //Nimbus (original), Motif, Metal
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
        }
        //</editor-fold>

        UIManager.getLookAndFeelDefaults().put("defaultFont", new Font("SansSerif", Font.PLAIN, 12));

        ETReduxFrame theUPbReduxFrame = null;

        try {
            theUPbReduxFrame = new ETReduxFrame(myState, reduxFile);
        } catch (BadLabDataException ex) {
            new ETWarningDialog(ex).setVisible(true);
        }
        ETReduxFrame.setDefaultLookAndFeelDecorated(true);

        ToolTipManager.sharedInstance().setDismissDelay(10000);

        if (theUPbReduxFrame != null) {
            theUPbReduxFrame.pack();
            theUPbReduxFrame.setVisible(true);
        }

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
//        if (args.length > 0) {
//            File reduxFile = new File(args[0]);
//            if (reduxFile.exists()) {
//                //  try {
//                new ETRedux(reduxFile);
//                //   } catch (IOException ex) {
//                //      ex.printStackTrace();
//                //  } catch (InvalidPreferencesFormatException ex) {
//                //      ex.printStackTrace();
//                //  }
//            }
//        } else {
//            //try {
//            new ETRedux(new File(""));
//            //} catch (IOException ex) {
//            //    ex.printStackTrace();
//            //} catch (InvalidPreferencesFormatException ex) {
//            //    ex.printStackTrace();
//            //}
//        }
        
        // dec 2017
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ETRedux(new File(""));//.setVisible(true);
            }
        });

    }
}
