/*
 * SchemaSimpleType.java
 *
 * Created Oct 30, 2010
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
package org.earthtime.xmlUtilities;

/**
 *
 * @author James F. Bowring
 */
public final class SchemaSimpleType {

    private String typeName;
    private String schemaFilePath;

    private SchemaSimpleType ( //
            String typeName,
            String schemaFilePath ) {
        this.typeName = typeName;
        this.schemaFilePath = schemaFilePath;
    }

    /**
     * 
     */
    public static SchemaSimpleType GeologicAge = //
            new SchemaSimpleType(//
            "GeologicAge",//
            "org/earthtime/UPb_Redux/resources/xmlSchema/SampleMetaDataSchemaForSESAR.xsd" );

    /**
     * 
     */
    public static SchemaSimpleType DetritalType = //
            new SchemaSimpleType(//
            "DetritalType",//
            "org/earthtime/UPb_Redux/resources/xmlSchema/SampleMetaDataSchemaForSESAR.xsd" );

    /**
     * 
     * @return
     */
    public String getSchemaFilePath () {
        return schemaFilePath;
    }

    /**
     * 
     * @return
     */
    public String getTypeName(){
        return typeName;
    }
}
