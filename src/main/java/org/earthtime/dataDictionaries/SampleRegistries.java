/*
 * SampleRegistries.java
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
package org.earthtime.dataDictionaries;

import org.earthtime.archivingTools.URIHelper;

/**
 *
 * @author James F. Bowring
 */
public enum SampleRegistries {

    // Sample Registries
    /**
     * 
     */
    /**
     * 
     */
    SESAR( "SESAR", "SSR", "http://app.geosamples.org/webservices/display.php?igsn=" ),

    /**
     *
     */
    GeochronID( "GeochronID", "GCH", "http://www.geochron.org/igsnexists.php?igsn=");//    "http://www.geochronid.org/display.php?geochronid=" ),
    
    
    //EARTHTIME_ID( "EARTHTIME-ID", "ERT", "" );
    private String name;
    private String code;
    private String connectionString;

    private SampleRegistries ( String name, String code, String connectionString ) {
        this.name = name;
        this.code = code;
        this.connectionString = connectionString;
    }

    /**
     * 
     * @return
     */
    public String getName () {
        return name;
    }

    /**
     * 
     * @return
     */
    public String getCode () {
        return code;
    }

    /**
     * 
     * @return
     */
    public String getConnectionString () {
        return connectionString;
    }

    /**
     * 
     * @return
     */
    public static String[] getNames () {
        String[] retVal = new String[SampleRegistries.values().length];
        for (int i = 0; i < SampleRegistries.values().length; i ++) {
            retVal[i] = SampleRegistries.values()[i].getName();
        }
        return retVal;
    }

    /**
     * 
     * @return
     */
    public static String[] getCodes () {
        String[] retVal = new String[SampleRegistries.values().length];
        for (int i = 0; i < SampleRegistries.values().length; i ++) {
            retVal[i] = SampleRegistries.values()[i].getCode();
        }
        return retVal;
    }

    /**
     * 
     * @param checkCode
     * @return
     */
    public static SampleRegistries getRegistryIfLegalCode ( String checkCode ) {
        SampleRegistries retVal = null;

        for (int i = 0; i < SampleRegistries.values().length; i ++) {
            if ( SampleRegistries.values()[i].getCode().equalsIgnoreCase( checkCode ) ) {
                retVal = SampleRegistries.values()[i];
                break;
            }
        }

        return retVal;
    }

    /**
     * 
     * @param sampleID
     * @return
     */
    public static String updateSampleID ( String sampleID ) {
        if ( !sampleID.contains(".") ) {
            sampleID = GeochronID.code + "." + sampleID;
        }

        return sampleID;
    }

    /**
     * 
     * @param sampleID
     * @return
     */
    public static boolean isSampleIdentifierValidAtRegistry ( String sampleID ) {
        // if missing or bad registry code, default to SampleRegistries.GeochronID
        boolean retVal = false;
        String[] parsed = new String[2];
        SampleRegistries registry = null;

        if (  ! sampleID.toUpperCase().contains( "NONE" ) ) {
            if ( sampleID.contains(".") ) {
                parsed = sampleID.split( "\\." );
                registry = getRegistryIfLegalCode( parsed[0] );
            }

            // quick and dirty for now
            org.w3c.dom.Document doc;
            if ((parsed.length > 1) && ( registry != null ) ){
                String connectionString = registry.getConnectionString();
                try {
                    doc = URIHelper.RetrieveXMLfromServerAsDOMdocument( connectionString + parsed[1].trim() );
                } catch (Exception e) {
                    doc = null;
                }
                if ( doc != null ) {
                    if ( doc.hasChildNodes() ) {
                        // sept 2012, now geochron uses <results>yes</results>
                        boolean resultsElementPresent = doc.getFirstChild().getNodeName().equalsIgnoreCase( "results" );
                        if (resultsElementPresent){
                            // geochron
                            if (registry.equals( (SampleRegistries.GeochronID)) ){
                                retVal = doc.getFirstChild().getTextContent().equalsIgnoreCase( "yes");
                            } else if (registry.equals( (SampleRegistries.SESAR)) ){
                                retVal = doc.getElementsByTagName( "error" ).getLength() == 0;
                            }
                        }
                        
//                        
//                        // As of Aug 15 2011 SESAR uses <results>, GeochronID uses <sample> for positive result
//                        // however, both use <results><error> for negative result
//                        boolean resultsElementPresent = doc.getFirstChild().getNodeName().equalsIgnoreCase( "results" );
//                        // for now, take negative approach
//                        if ( resultsElementPresent ) {
//                            retVal = doc.getElementsByTagName( "error" ).getLength() == 0;
//                        } else {
//                            // got GeochronID <sample> 
//                            retVal = true;
//                        }
                    }
                }
            }
        }

        return retVal;
    }
}
