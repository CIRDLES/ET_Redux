/*
 * UPbReduxConfigurator.java
 *
 * Created on August 8, 2007, 8:12 AM
 *
 *
 * Copyright 2006-2016 James F. Bowring and www.Earth-Time.org
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

import java.io.IOException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

/**
 *
 * @author James F. Bowring
 */
public final class UPbReduxConfigurator {

    // Fields
    private static Preferences myPreferences = null;
    private static Class myClass = null;
    /**
     *
     */
    public final static String DEFAULT_VALUE =
            "NONE";
    /**
     *
     */
    public final static String URL_EARTHTIMEORG = 
            "https://raw.githubusercontent.com/EARTHTIME/";
            //"http://earth-time.org/";
    /**
     *
     */
    public final static String URI_UPB_PUBLIC_DATA =
            "";
            //"projects/upb/public_data/";
    /**
     *
     */
    public final static String URI_AliquotXMLSchema =
            "Schema/master/AliquotXMLSchema.xsd";
    /**
     *
     */
    public final static String URI_UPbReduxFractionXMLSchemaURL =
            "Schema/master/UPbReduxInputXMLSchema.xsd";
    /**
     *
     */
    public final static String URI_AnalysisFractionXMLSchemaURL =
            "Schema/master/AnalysisFractionXMLSchema.xsd";
    /**
     *
     */
    public final static String URI_InitialPbModelXMLSchema =
            "Schema/master/InitialPbModelXMLSchema.xsd";
    /**
     *
     */
    public final static String URI_InitialPbModelETXMLSchema =
            "Schema/master/InitialPbModelETXMLSchema.xsd";
    /**
     *
     */
    public final static String URI_PbBlankXMLSchema =
            "Schema/master/PbBlankXMLSchema.xsd";

    /**
     *
     */
    public final static String URI_PbBlankICModelXMLSchema =
            "Schema/master/PbBlankICModelXMLSchema.xsd";
    /**
     *
     */
    public final static String URI_MineralStandardModelXMLSchemaURL =
            "Schema/master/MineralStandardModelXMLSchema.xsd";
    /**
     *
     */
    public final static String URI_MineralStandardUPbModelXMLSchemaURL =
            "Schema/master/MineralStandardUPbModelXMLSchema.xsd";
    /**
     *
     */
    public final static String URI_PhysicalConstantsXMLSchema =
            "Schema/master/PhysicalConstantsXMLSchema.xsd";

    /**
     *
     */
    public final static String URI_PhysicalConstantsModelXMLSchema =
            "Schema/master/PhysicalConstantsModelXMLSchema.xsd";
    /**
     *
     */
    public final static String URI_TracerXMLSchema =
            "Schema/master/TracerXMLSchema.xsd";

    /**
     *
     */
    public final static String URI_TracerUPbModelXMLSchema =
            "Schema/master/TracerUPbModelXMLSchema.xsd";
    /**
     *
     */
    public final static String URI_EARTHTIME_XMLTracers =
            "Tracers/master/XML/";
    /**
     *
     */
    public final static String URI_ValueModelXMLSchema =
            "Schema/master/ValueModelXMLSchema.xsd";
            //"XSD/ValueModelXMLSchema.xsd";
    /**
     *
     */
    public final static String URI_SampleMetaDataXMLSchema =
            "Schema/master/SampleMetaDataXMLSchema.xsd";
    /**
     *
     */
    public final static String URI_ReportSettingsXMLSchema =
            "Schema/master/upbReports/ReportSettingsXMLSchema.xsd";
    /**
     *
     */
    public final static String URI_ReduxMatrixXMLSchemaURL =
            "Schema/master/ReduxMatrixXMLSchema.xsd";

    /**
     * Creates a new instance of UPbReduxConfigurator
     */
    public UPbReduxConfigurator () {

        // http://www.particle.kth.se/~lindsey/JavaCourse/Book/Part1/Java/Chapter10/Preferences.html
        // initialize a preferences object
        myClass = this.getClass();
        myPreferences = Preferences.userNodeForPackage( myClass );
        try {
            InitializeConfiguration();
        } catch (InvalidPreferencesFormatException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     *
     * @throws IOException
     * @throws InvalidPreferencesFormatException
     */
    public static void InitializeConfiguration () throws IOException, InvalidPreferencesFormatException {

        myPreferences.put( "URL_EARTHTIMEORG", URL_EARTHTIMEORG );

        myPreferences.put( "URI_UPB_PUBLIC_DATA", URI_UPB_PUBLIC_DATA );

        myPreferences.put( "URI_AliquotXMLSchema", URI_AliquotXMLSchema );

        myPreferences.put( "URI_UPbReduxFractionXMLSchemaURL", URI_UPbReduxFractionXMLSchemaURL );

        myPreferences.put( "URI_AnalysisFractionXMLSchemaURL", URI_AnalysisFractionXMLSchemaURL );

        myPreferences.put( "URI_InitialPbModelXMLSchema", URI_InitialPbModelXMLSchema );
        myPreferences.put( "URI_InitialPbModelETXMLSchema", URI_InitialPbModelETXMLSchema );

        myPreferences.put( "URI_PbBlankXMLSchema", URI_PbBlankXMLSchema );
        myPreferences.put( "URI_PbBlankICModelXMLSchema", URI_PbBlankICModelXMLSchema );

        myPreferences.put( "URI_MineralStandardModelXMLSchemaURL", URI_MineralStandardModelXMLSchemaURL );
        myPreferences.put( "URI_MineralStandardUPbModelXMLSchemaURL", URI_MineralStandardUPbModelXMLSchemaURL );

        myPreferences.put( "URI_PhysicalConstantsXMLSchema", URI_PhysicalConstantsXMLSchema );
        myPreferences.put( "URI_PhysicalConstantsModelXMLSchema", URI_PhysicalConstantsModelXMLSchema );
        
        myPreferences.put( "URI_TracerXMLSchema", URI_TracerXMLSchema );
        myPreferences.put( "URI_TracerUPbModelXMLSchema", URI_TracerUPbModelXMLSchema );

        myPreferences.put( "URI_EARTHTIME_XMLTracers", URI_EARTHTIME_XMLTracers );

        myPreferences.put( "URI_ValueModelXMLSchema", URI_ValueModelXMLSchema );

        myPreferences.put( "URI_SampleMetaDataXMLSchema", URI_SampleMetaDataXMLSchema );

        myPreferences.put( "URI_ReportSettingsXMLSchema", URI_ReportSettingsXMLSchema );

        myPreferences.put( "URI_ReduxMatrixXMLSchemaURL", URI_ReduxMatrixXMLSchemaURL );
    }

    /**
     *
     * @param URIKey
     * @return
     */
    public String getResourceURI ( String URIKey ) {
        if ( myPreferences == null ) {
            myPreferences = Preferences.userNodeForPackage( myClass );
        }

        return getPreference( "URL_EARTHTIMEORG" )
                + getPreference( "URI_UPB_PUBLIC_DATA" )
                + getPreference( URIKey );
    }

    private static String getPreference ( String field ) {
        return myPreferences.get(
                field,
                UPbReduxConfigurator.DEFAULT_VALUE );
    }
}
