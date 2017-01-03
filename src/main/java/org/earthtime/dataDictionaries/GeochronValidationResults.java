/*
 * GeochronValidationResults.java
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
package org.earthtime.dataDictionaries;

import org.earthtime.archivingTools.URIHelper;
import org.w3c.dom.Node;

/**
 *
 * @author James F. Bowring
 */
/*
 *
 *
 * Created Feb 2011 as part of major refactoring to control magic strings
 */
public enum GeochronValidationResults {

    // tracer ratios
    /**
     * 
     */
    success("success", "This aliquot exists in Geochron.  You may update it by choosing 'overwrite'."),
    /**
     * 
     */
    errorIsPrivate("errorIsPrivate", "This aliquot exists in Geochron but is private with the current credentials."),
    /**
     * 
     */
    errorNotFound("errorNotFound", "This aliquot does not exist in Geochron."),
    /**
     * 
     */
    invalidUser("invalidUser", "This aliquot exists in Geochron but your credentials are invalid.");
    private String name;
    private String message;

    private GeochronValidationResults(String name, String message) {
        this.name = name;
        this.message = message;
    }

    /**
     * 
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @return
     */
    public String getMessage() {
        return message;
    }

    /**
     * 
     * @param connectionString
     * @return
     */
    public static GeochronValidationResults validateAliquot(String connectionString) {
        GeochronValidationResults retVal = errorNotFound;

        org.w3c.dom.Document doc = //
                URIHelper.RetrieveXMLfromServerAsDOMdocument(connectionString);

        if (doc != null) {
            if (doc.hasChildNodes()) {
                Node successNode = doc.getElementsByTagName("success").item(0);
                if (successNode != null) {
                    retVal = GeochronValidationResults.success;
                } else {
                    // assume = errorNotFound
                    Node errorNode = doc.getElementsByTagName("error").item(0);
                    if (errorNode.getTextContent().toUpperCase().contains("PRIVATE")) {
                        retVal = GeochronValidationResults.errorIsPrivate;
                    }
                }
            }
        }
        return retVal;
    }
}
