/*
 * ReduxPreferences.java
 *
 * Created on September 23, 2006, 7:29 AM
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

import java.io.Serializable;
import java.net.URI;
import org.earthtime.UPb_Redux.ReduxConstants.ANALYSIS_PURPOSE;

/**
 *
 * @author James F. Bowring
 */
public class ReduxPreferences implements Serializable {

    // Class variables
    private static final long serialVersionUID = -3387114034799849790L;
    // instance variables
    private boolean fractionDataOverriddenOnImport = true;
    private URI tripoliSubscriptionFile;
    private String geochronUserName;
    private String geochronPassWord;
    private ANALYSIS_PURPOSE defaultSampleAnalysisPurpose;

    /**
     * Creates a new instance of ReduxPreferences
     */
    public ReduxPreferences() {

        this.fractionDataOverriddenOnImport = true;
        this.tripoliSubscriptionFile = null;

        this.geochronUserName = "username";

        this.geochronPassWord = "longpassword";

        this.defaultSampleAnalysisPurpose = ANALYSIS_PURPOSE.NONE;
    }

    /**
     *
     * @return
     */
    public boolean isFractionDataOverriddenOnImport() {
        return fractionDataOverriddenOnImport;
    }

    /**
     *
     * @param fractionDataOverriddenOnImport
     */
    public void setFractionDataOverriddenOnImport(boolean fractionDataOverriddenOnImport) {
        this.fractionDataOverriddenOnImport = fractionDataOverriddenOnImport;
    }

    /**
     * @return the tripoliSubscriptionFile
     */
    public URI getTripoliSubscriptionFile() {
        return tripoliSubscriptionFile;
    }

    /**
     * @param tripoliSubscriptionFile the tripoliSubscriptionFile to set
     */
    public void setTripoliSubscriptionFile(URI tripoliSubscriptionFile) {
        this.tripoliSubscriptionFile = tripoliSubscriptionFile;
    }

    /**
     * @return the geochronUserName
     */
    public String getGeochronUserName() {
        if (geochronUserName == null) {
            geochronUserName = "username";
        }
        return geochronUserName;
    }

    /**
     * @param geochronUserName the geochronUserName to set
     */
    public void setGeochronUserName(String geochronUserName) {
        this.geochronUserName = geochronUserName;
    }

    /**
     * @return the geochronPassWord
     */
    public String getGeochronPassWord() {
        if (geochronPassWord == null) {
            geochronPassWord = "longpassword";
        }
        return geochronPassWord;
    }

    /**
     * @param geochronPassWord the geochronPassWord to set
     */
    public void setGeochronPassWord(String geochronPassWord) {
        this.geochronPassWord = geochronPassWord;
    }

    /**
     * @return the defaultSampleAnalysisPurpose
     */
    public ANALYSIS_PURPOSE getDefaultSampleAnalysisPurpose() {
        if (defaultSampleAnalysisPurpose == null) {
            defaultSampleAnalysisPurpose = ANALYSIS_PURPOSE.NONE;
        }
        return defaultSampleAnalysisPurpose;
    }

    /**
     * @param defaultSampleAnalysisPurpose the defaultSampleAnalysisPurpose to
     * set
     */
    public void setDefaultSampleAnalysisPurpose(ANALYSIS_PURPOSE defaultSampleAnalysisPurpose) {
        this.defaultSampleAnalysisPurpose = defaultSampleAnalysisPurpose;
    }
}
