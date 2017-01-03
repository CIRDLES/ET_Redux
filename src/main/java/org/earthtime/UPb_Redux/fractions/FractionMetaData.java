/*
 * FractionMetaData.java
 *
 * Created Nov 10, 2009
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
package org.earthtime.UPb_Redux.fractions;

/**
 *
 * @author James F. Bowring
 */
public class FractionMetaData {

    private String fractionID;

    private String aliquotName;

    private String fractionXMLUPbReduxFileName_U;

    private String fractionXMLUPbReduxFileName_Pb;

    /**
     * 
     * @param fractionID
     * @param aliquotName
     * @param fractionXMLUPbReduxFileName_U
     * @param fractionXMLUPbReduxFileName_Pb
     */
    public FractionMetaData(//
            String fractionID, //
            String aliquotName, //
            String fractionXMLUPbReduxFileName_U,
            String fractionXMLUPbReduxFileName_Pb) {
        this.fractionID = fractionID;
        this.aliquotName = aliquotName;
        this.fractionXMLUPbReduxFileName_U = fractionXMLUPbReduxFileName_U;
        this.fractionXMLUPbReduxFileName_Pb = fractionXMLUPbReduxFileName_Pb;
    }
}
