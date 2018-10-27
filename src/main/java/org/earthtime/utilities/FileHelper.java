/*
 * FileHelper.java
 *
 * Created on December 15, 2007, 10:23 AM
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
package org.earthtime.utilities;

import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import java.io.FilenameFilter;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author James F. Bowring
 */
public class FileHelper {

    /**
     * Creates a new instance of FileHelper
     */
    public FileHelper() {
    }

    /**
     *
     * @param parentFrame
     * @param dialogTitle
     * @param directory
     * @param fileExtension
     * @param fractionFileName
     * @param nonMacFileFilter
     * @return
     */
    public static File AllPlatformSaveAs(
            Frame parentFrame,
            String dialogTitle,
            String directory,
            final String fileExtension,
            String fractionFileName,
            FileFilter nonMacFileFilter) {

        File selectedFile;

        if (false) {//System.getProperty("os.name").toLowerCase().startsWith("mac os x")) {
            // Apple recommends awt
            FileDialog fd = new FileDialog(parentFrame, dialogTitle, FileDialog.SAVE);
            fd.setDirectory(directory);
            fd.setFilenameFilter(new FilenameFilter() {

                public boolean accept(File file, String string) {
                    return string.toLowerCase().endsWith(fileExtension);
                }
            });

            fd.setFile(fractionFileName);

            fd.setAlwaysOnTop(true);
            fd.setVisible(true);

            if (fd.getFile() != null) {
                selectedFile
                        = new File(fd.getDirectory() + File.separator + fd.getFile());
            } else {
                selectedFile = null;
            }
        } else {
            // not-MAC os use JfileChooser

            JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(new File(directory + File.separator + fractionFileName));
            fc.setFileFilter(nonMacFileFilter);
            fc.setDialogTitle(dialogTitle);
            fc.grabFocus();

            // Show save dialog; this method does not return until the dialog is closed
            int result = fc.showSaveDialog(new Frame());
            if (result == JFileChooser.APPROVE_OPTION) {
                selectedFile = fc.getSelectedFile();
                // check for already exists
                int response = 0;
                if (selectedFile.exists()) {
                    // Modal dialog with OK/cancel and a text field
                    response = JOptionPane.showConfirmDialog(parentFrame,
                            new String[]{"The file exists.",
                                "Do you want to replace it?"
                            },
                            "ET Redux Warning",
                            JOptionPane.YES_NO_OPTION,
                            JOptionPane.WARNING_MESSAGE);
                    if (response == JOptionPane.NO_OPTION) {
                        selectedFile = null;
                    }
                }
            } else {
                selectedFile = null;
            }
        }

        return selectedFile;
    }

    /**
     *
     * @param dialogTitle
     * @param locationIn
     * @param fileExtension
     * @param nonMacFileFilter
     * @param allowMultipleSelect
     * @param parentFrame
     * @return
     */
    public synchronized static File[] AllPlatformGetFile(
            String dialogTitle,
            File locationIn,
            final String fileExtension,
            FileFilter nonMacFileFilter,
            boolean allowMultipleSelect,
            JFrame parentFrame) {

        // Nov 2008 note: http://developer.apple.com/samplecode/FunWithFileDialogs/listing3.html
        File location = locationIn;

        if (location == null) {
            location = new File("default location");
        }

        File[] returnFile = new File[]{null};

        // nov 2008 went to swing only
        JFileChooser fc = new JFileChooser(location);
        fc.setDialogType(JFileChooser.OPEN_DIALOG);
        fc.addChoosableFileFilter(nonMacFileFilter);
        fc.setDialogTitle(dialogTitle);
        fc.setMultiSelectionEnabled(allowMultipleSelect);
        fc.setAcceptAllFileFilterUsed(true);
        fc.setFileFilter(nonMacFileFilter);

        // Show open dialog; this method does not return until the dialog is closed
//        System.out.println("made it here");
        int result;
        result = fc.showOpenDialog(parentFrame);
//        System.out.println("AND made it here");
        if (result == JFileChooser.APPROVE_OPTION) {
            if (allowMultipleSelect) {
                returnFile = fc.getSelectedFiles();
            } else {
                returnFile = new File[]{fc.getSelectedFile()};
            }
        }

        return returnFile;
    }

    /**
     *
     * @param dialogTitle
     * @param location
     * @return
     */
    public static File AllPlatformGetFolder(
            String dialogTitle,
            File location) {

        File fractionFolder = null;

        if (location == null) {
            location = new File("default location");
        }

        JFileChooser fc = new JFileChooser();
        // this moves up one level so we can choose folder
        fc.setCurrentDirectory(location.getParentFile());
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setDialogTitle(dialogTitle);

        // Show open dialog; this method does not return until the dialog is closed
        int result = fc.showOpenDialog(new JFrame());
        if (result == JFileChooser.APPROVE_OPTION) {
            fractionFolder = fc.getSelectedFile();
            if (!fractionFolder.exists()) {
                JOptionPane.showMessageDialog(null,
                        new String[]{"The folder does NOT exist."},
                        "ET Redux Warning",
                        JOptionPane.WARNING_MESSAGE);
            }
        }

        return fractionFolder;
    }
}
