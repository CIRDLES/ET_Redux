/*
 * TracerI.java
 *
 * Created on August 6, 2007, 7:09 AM
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

package org.earthtime.UPb_Redux.tracers;

import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public interface TracerI {

    /**
     * 
     * @return
     */
    String getDateCertified();

    /**
     * 
     * @param isotopeConc
     * @return
     */
    ValueModel getIsotopeConcByName(String isotopeConc);

    /**
     * 
     * @return
     */
    ValueModel[] getIsotopeConcentrations();

    /**
     * 
     * @return
     */
    String getLabName();

    /**
     * 
     * @return
     */
    String getNameAndVersion();

    /**
     * 
     * @param ratioName
     * @return
     */
    ValueModel getRatioByName(String ratioName);

    /**
     * 
     * @return
     */
    ValueModel[] getRatios();

    /**
     * 
     * @return
     */
    String getReduxLabDataElementName();

    /**
     * 
     * @return
     */
    String getTracerName();

    /**
     * 
     * @return
     */
    String getTracerType();

    /**
     * 
     * @return
     */
    int getVersionNumber();

    /**
     * 
     * @param dateCertified
     */
    void setDateCertified(String dateCertified);

    /**
     * 
     * @param isotopeConcentrations
     */
    void setIsotopeConcentrations(ValueModel[] isotopeConcentrations);

    /**
     * 
     * @param labName
     */
    void setLabName(String labName);

    /**
     * 
     * @param ratios
     */
    void setRatios(ValueModel[] ratios);

    /**
     * 
     * @param tracerName
     */
    void setTracerName(String tracerName);

    /**
     * 
     * @param tracerType
     */
    void setTracerType(String tracerType);

    /**
     * 
     * @param versionNumber
     */
    void setVersionNumber(int versionNumber);

}
