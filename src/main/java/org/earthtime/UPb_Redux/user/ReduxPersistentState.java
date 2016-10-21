/*
 * ReduxPersistentState.java
 *
 * Created on March 24, 2006, 8:21 PM
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
package org.earthtime.UPb_Redux.user;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.utilities.ETSerializer;
import org.earthtime.exceptions.ETException;

/**
 *
 * @author James F. Bowring
 */
// NOTE: java.utils.Properties should be considered for use here JFB April 2007
public class ReduxPersistentState implements Serializable {

    // class variables
    private static final long serialVersionUID = -2957701651505126654L;
    private static ReduxPersistentState instance = (ReduxPersistentState) ETSerializer.GetSerializedObjectFromFile(getMySerializedName());
    private static final String persistentStateFileName = "ReduxPersistentState.ser";
    private static int MRU_COUNT = 10;

    /**
     * @return the persistentStateFileName
     */
    public static String getPersistentStateFileName() {
        return persistentStateFileName;
    }
    // instance variables
    private ArrayList<String> MRUSampleList;
    private String MRUSampleFolderPath;
    private String MRUImportedXMLFractionsFolder;
    private String MRUImportFolderCompilationMode;
    private String MRUImportFolderLegacyMode;
    private String MRUSampleFolder;
    private String MRUSampleMetaDataFolder;
    private String MRUReportSettingsModelFolder;
    private String MRUTripoliRawDataFolder;
    private String excelFolderMRU;
    private SampleDateInterpretationGUIOptions sampleDateInterpretationGUISettings;
    // persistant state for user
    private ReduxPreferences reduxPreferences;
    // 2012 introduce projects
    private File MRUProjectFile;
    private ArrayList<String> MRUProjectList;
    private String MRUProjectFolderPath;
    // oct 2016 manage mru for LAICPMS file handling protocols - save name of last used
    private String mruFileHandlingProtocolForLAICPMS;

    /**
     *
     */
    public ReduxPersistentState() {

        initMRUSampleLists();
        initMRUProjectLists();
        MRUSampleFolderPath = "";

        MRUImportedXMLFractionsFolder = "";
        MRUImportFolderCompilationMode = "";
        MRUImportFolderLegacyMode = "";
        MRUSampleFolder = "";
        MRUSampleMetaDataFolder = "";
        MRUReportSettingsModelFolder = "";
        MRUTripoliRawDataFolder = "";

        excelFolderMRU = ".";
        sampleDateInterpretationGUISettings = new SampleDateInterpretationGUIOptions();

        reduxPreferences = new ReduxPreferences();

        // check if user data folder exists and create if it does not
        File dataFolder = new File(
                File.separator + System.getProperty("user.home") + File.separator + ReduxConstants.myUsersETReduxDataFolderName);
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        // set default folder for sampleMRU1
        File sampleFolder = new File(
                dataFolder.getAbsolutePath() + File.separator + "samples");
        try {
            sampleFolder.mkdir();
        } catch (SecurityException ex) {
        }

        MRUProjectFile = null;
        
        mruFileHandlingProtocolForLAICPMS = "";

        serializeSelf();
    }

    public void serializeSelf() {
        // save off initial persistent state serialized file
        try {
            ETSerializer.SerializeObjectToFile(this, getMySerializedName());
        } catch (ETException eTException) {
        }
    }

    private void initMRUSampleLists() {
        MRUSampleList = new ArrayList<>(MRU_COUNT);
    }

    private void initMRUProjectLists() {
        MRUProjectList = new ArrayList<>(MRU_COUNT);
    }

    /**
     *
     * @param MRUSampleFile
     */
    public void updateMRUSampleList(File MRUSampleFile) {
        try {
            // remove if exists in MRU list
            String MRUSamplefileName = MRUSampleFile.getCanonicalPath();
            MRUSampleList.remove(MRUSamplefileName);
            MRUSampleList.add(0, MRUSamplefileName);

            // trim list
            if (MRUSampleList.size() > MRU_COUNT) {
                MRUSampleList.remove(MRU_COUNT);
            }

            // update MRU folder
            MRUSampleFolderPath = MRUSampleFile.getParent();
        } catch (IOException iOException) {
        }

        // save
        try {
            ETSerializer.SerializeObjectToFile(this, getMySerializedName());
        } catch (ETException eTException) {
        }
    }

    /**
     *
     * @param MRUProjectFile
     */
    public void updateMRUProjectList(File MRUProjectFile) {
        // init for backward compatibility
        getMRUProjectList();

        try {
            // remove if exists in MRU list
            String MRUProjectFileName = MRUProjectFile.getCanonicalPath();
            MRUProjectList.remove(MRUProjectFileName);
            MRUProjectList.add(0, MRUProjectFileName);

            // trim list
            if (MRUProjectList.size() > MRU_COUNT) {
                MRUProjectList.remove(MRU_COUNT);
            }

            // update MRU folder
            MRUProjectFolderPath = MRUProjectFile.getParent();
        } catch (IOException iOException) {
        }

        // save
        try {
            ETSerializer.SerializeObjectToFile(this, getMySerializedName());
        } catch (ETException eTException) {
        }

    }

    /**
     *
     * @return
     */
    public static ReduxPersistentState getExistingPersistentState() {
        // check if user data folder exists and create if it does not
        File dataFolder = new File(
                File.separator + System.getProperty("user.home") + File.separator + ReduxConstants.myUsersETReduxDataFolderName);
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }
        if (instance == null) {
            instance = new ReduxPersistentState();
        }

        return instance;
    }

    //properties
    /**
     *
     * @return
     */
    public static String getMySerializedName() {
        String mySerializedName
                = File.separator//
                + System.getProperty("user.home")//
                + File.separator//
                + ReduxConstants.myUsersETReduxDataFolderName //
                + File.separator + persistentStateFileName;
        return mySerializedName;
    }

    /**
     *
     * @return
     */
    public File getMRUImportedXMLFractionsFolder() {
        if (MRUImportedXMLFractionsFolder == null) {
            MRUImportedXMLFractionsFolder = "";
        }
        return new File(MRUImportedXMLFractionsFolder);
    }

    /**
     *
     * @param MRUImportedXMLFractionsFolder
     */
    public void setMRUImportedXMLFractionsFolder(String MRUImportedXMLFractionsFolder) {
        this.MRUImportedXMLFractionsFolder = MRUImportedXMLFractionsFolder;
    }

    /**
     *
     * @return
     */
    public File getExcelFolderMRU() {
        return new File(excelFolderMRU);
    }

    /**
     *
     * @param ExcelFolder
     */
    public void setExcelFolderMRU(String ExcelFolder) {
        this.excelFolderMRU = ExcelFolder;
    }

    /**
     *
     * @return
     */
    public ReduxPreferences getReduxPreferences() {
        return reduxPreferences;
    }

    /**
     *
     * @param reduxPreferences
     */
    public void setReduxPreferences(ReduxPreferences reduxPreferences) {
        this.reduxPreferences = reduxPreferences;
    }

    /**
     *
     * @return
     */
    public SampleDateInterpretationGUIOptions getSampleAgeInterpretationGUISettings() {
        return sampleDateInterpretationGUISettings;
    }

    /**
     *
     * @param sampleAgeInterpretationGUISettings
     */
    public void setSampleDateInterpretationGUISettings(SampleDateInterpretationGUIOptions sampleAgeInterpretationGUISettings) {
        this.sampleDateInterpretationGUISettings = sampleAgeInterpretationGUISettings;
    }

    /**
     * @return the MRUSampleList
     */
    public ArrayList<String> getMRUSampleList() {
        if (MRUSampleList == null) {
            initMRUSampleLists();
        }
        return MRUSampleList;
    }

    /**
     * @param MRUsampleList
     */
    public void setMRUSampleList(ArrayList<String> MRUsampleList) {
        this.MRUSampleList = MRUsampleList;
    }

    /**
     * @return the MRUSampleFolderPath
     */
    public String getMRUSampleFolderPath() {
        if (MRUSampleFolderPath == null) {
            MRUSampleFolderPath = "";
        }
        return MRUSampleFolderPath;
    }

    /**
     * @return the MRUImportFolderCompilationMode
     */
    public File getMRUImportFolderCompilationMode() {
        if (MRUImportFolderCompilationMode == null) {
            MRUImportFolderCompilationMode = "";
        }
        return new File(MRUImportFolderCompilationMode);
    }

    /**
     * @param MRUImportFolderCompilationMode the MRUImportFolderCompilationMode
     * to set
     */
    public void setMRUImportFolderCompilationMode(String MRUImportFolderCompilationMode) {
        this.MRUImportFolderCompilationMode = MRUImportFolderCompilationMode;
    }

    /**
     * @return the MRUImportFolderLegacyMode
     */
    public File getMRUImportFolderLegacyMode() {
        if (MRUImportFolderLegacyMode == null) {
            MRUImportFolderLegacyMode = "";
        }
        return new File(MRUImportFolderLegacyMode);
    }

    /**
     * @param MRUImportFolderLegacyMode the MRUImportFolderLegacyMode to set
     */
    public void setMRUImportFolderLegacyMode(String MRUImportFolderLegacyMode) {
        this.MRUImportFolderLegacyMode = MRUImportFolderLegacyMode;
    }

    /**
     * @return the MRUSampleFolder
     */
    public File getMRUSampleFolder() {
        if (MRUSampleFolder == null) {
            MRUSampleFolder = "";
        }
        return new File(MRUSampleFolder);
    }

    /**
     * @param MRUSampleFolder the MRUSampleFolder to set
     */
    public void setMRUSampleFolder(String MRUSampleFolder) {
        this.MRUSampleFolder = MRUSampleFolder;
    }

    /**
     * @return the MRUSampleMetaDataFolder
     */
    public File getMRUSampleMetaDataFolder() {
        if (MRUSampleMetaDataFolder == null) {
            MRUSampleMetaDataFolder = "";
        }
        return new File(MRUSampleMetaDataFolder);
    }

    /**
     * @param MRUSampleMetaDataFolder the MRUSampleMetaDataFolder to set
     */
    public void setMRUSampleMetaDataFolder(String MRUSampleMetaDataFolder) {
        this.MRUSampleMetaDataFolder = MRUSampleMetaDataFolder;
    }

    /**
     * @return the MRUReportSettingsModelFolder
     */
    public String getMRUReportSettingsModelFolder() {
        if (MRUReportSettingsModelFolder == null) {
            MRUReportSettingsModelFolder = "";
        }
        return MRUReportSettingsModelFolder;
    }

    /**
     * @param MRUReportSettingsModelFolder the MRUReportSettingsModelFolder to
     * set
     */
    public void setMRUReportSettingsModelFolder(String MRUReportSettingsModelFolder) {
        this.MRUReportSettingsModelFolder = MRUReportSettingsModelFolder;
    }

    /**
     * @return the MRUTripoliRawDataFolder
     */
    public File getMRUTripoliRawDataFolder() {
        if (MRUTripoliRawDataFolder == null) {
            MRUTripoliRawDataFolder = "";
        }
        return new File(MRUTripoliRawDataFolder);

    }

    /**
     * @param MRUTripoliRawDataFolder the MRUTripoliRawDataFolder to set
     */
    public void setMRUTripoliRawDataFolder(String MRUTripoliRawDataFolder) {
        this.MRUTripoliRawDataFolder = MRUTripoliRawDataFolder;
    }

    /**
     * @return the MRUProjectFile
     */
    public File getMRUProjectFile() {
        return MRUProjectFile;
    }

    /**
     * @param MRUProjectFile the MRUProjectFile to set
     */
    public void setMRUProjectFile(File MRUProjectFile) {
        this.MRUProjectFile = MRUProjectFile;
    }

    /**
     *
     * @return
     */
    public ArrayList<String> getMRUProjectList() {
        if (MRUProjectList == null) {
            initMRUProjectLists();
        }
        return MRUProjectList;
    }

    /**
     * @param MRUProjectList
     * @param MRUsampleList
     */
    public void setMRUProjectList(ArrayList<String> MRUProjectList) {
        this.MRUProjectList = MRUProjectList;
    }

    /**
     * @return the MRUProjectFolderPath
     */
    public String getMRUProjectFolderPath() {
        return MRUProjectFolderPath;
    }

    /**
     * @param MRUProjectFolderPath the MRUProjectFolderPath to set
     */
    public void setMRUProjectFolderPath(String MRUProjectFolderPath) {
        this.MRUProjectFolderPath = MRUProjectFolderPath;
    }

    /**
     * @return the mruFileHandlingProtocolForLAICPMS
     */
    public String getMruFileHandlingProtocolForLAICPMS() {
        if (mruFileHandlingProtocolForLAICPMS == null){
            mruFileHandlingProtocolForLAICPMS = "";
        }
        return mruFileHandlingProtocolForLAICPMS;
    }

    /**
     * @param mruFileHandlingProtocolForLAICPMS the mruFileHandlingProtocolForLAICPMS to set
     */
    public void setMruFileHandlingProtocolForLAICPMS(String mruFileHandlingProtocolForLAICPMS) {
        this.mruFileHandlingProtocolForLAICPMS = mruFileHandlingProtocolForLAICPMS;
    }
}
