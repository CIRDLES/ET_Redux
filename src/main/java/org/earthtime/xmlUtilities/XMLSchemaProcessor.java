/*
 * XMLSchemaProcessor.java
 *
 * Created Oct 30, 2010
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
package org.earthtime.xmlUtilities;



import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSRestrictionSimpleType;
import com.sun.xml.xsom.XSSchema;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.XSSimpleType;
import com.sun.xml.xsom.parser.XSOMParser;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Vector;

/**
 *
 * @author James F. Bowring
 */
public final class XMLSchemaProcessor {

    // https://xsom.dev.java.net/
    /**
     * 
     */
    public XMLSchemaProcessor () {
    }

    // experimental oct 2010
    // http://it.toolbox.com/blogs/enterprise-web-solutions/parsing-an-xsd-schema-in-java-32565
    private XSSimpleType parse ( SchemaSimpleType schemaSimpleType ) {

        ClassLoader cldr = this.getClass().getClassLoader();

        InputStream resourceXMLSchema = cldr.getResourceAsStream( schemaSimpleType.getSchemaFilePath() );

        File localXMLSchema = new File( schemaSimpleType.getTypeName() + ".xsd" );
        try {
            InputStream in = resourceXMLSchema;
            // Overwrite the file.
            OutputStream out = new FileOutputStream( localXMLSchema );

            while (in.available() > 0) {
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read( buf )) > 0) {
                    out.write( buf, 0, len );
                }
            }
            in.close();
            out.close();

        } catch (IOException iOException) {
        }


        XSSimpleType st = null;
        try {
            XSOMParser parser = new XSOMParser();
            parser.parse( localXMLSchema );
            XSSchemaSet schemaSet = parser.getResult();
            XSSchema xsSchema = schemaSet.getSchema( 1 );

            st = xsSchema.getSimpleType( schemaSimpleType.getTypeName() );

        } catch (Exception exp) {
            exp.printStackTrace( System.out );
        }

        localXMLSchema.delete();

        return st;
    }

    /**
     * 
     * @param schemaSimpleType
     * @return
     */
    public Vector<String> getSimpleTypeEnumeration ( SchemaSimpleType schemaSimpleType ) {

        Vector<String> enumeration = new Vector<String>();
        XSSimpleType st = parse( schemaSimpleType );

        XSRestrictionSimpleType restriction = st.asRestriction();

        if ( restriction != null ) {

            Iterator<? extends XSFacet> i = restriction.getDeclaredFacets().iterator();
            while (i.hasNext()) {
                XSFacet facet = i.next();

                if ( facet.getName().equals( XSFacet.FACET_ENUMERATION ) ) {
                    enumeration.add( facet.getValue().value );
                }
            }
        }
        return enumeration;

    }
}
