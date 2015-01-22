/*
 * ValueModelReferenced.java
 *
 * Created on December 26, 2007, 1:03 PM
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
package org.earthtime.UPb_Redux.valueModels;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.math.BigDecimal;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException;
import org.earthtime.exceptions.ETException;
import org.earthtime.utilities.URIHelper;
import org.earthtime.xmlUtilities.XMLSerializationI;

/**
 * A
 * <code>ValueModelReferenced</code> object represents scientifically measured
 * quantities and their errors as well as citing any related academic reference.
 * It also provides additional methods for manipulating and publishing these
 * values.
 *
 * @author James F. Bowring, javaDocs by Stan Gasque
 */
public class ValueModelReferenced extends ValueModel implements
        Comparable<ValueModel>,
        Serializable,
        XMLSerializationI {

    // Class variables
    /**
     * identifies object in binary serialization
     */
    private static final long serialVersionUID = -436089576009120014L;
    // Instance variables
    /**
     * citation of any academic reference related to this
     * <code>ValueModel</code>
     */
    private String reference;

    /**
     * creates a new instance of
     * <code>ValueModelReferenced</code> with
     * <code>
     * name</code>,
     * <code>value</code>,
     * <code>uncertainty type</code>,
     * <code>
     * one sigma</code>, and
     * <code>reference</code> fields initialized to "NONE", 0, "NONE", 0, and
     * "NONE" respectively.
     */
    public ValueModelReferenced () {
        super();
        setReference( "NONE" );
    }

    /**
     * Creates a new instance of
     * <code>ValueModelReferenced</code> with a specified
     * <code>name</code>,
     * <code>value</code>,
     * <code>uncertainty type
     * </code>,
     * <code>one sigma</code>, and
     * <code>reference</code>.
     *
     * @param name name of the ratio that this
     * <code>ValueModel</code> represents
     * @param value numerical value of ratio
     * @param uncertaintyType type of uncertainty; ABS or PCT
     * @param oneSigma value of one standard deviation
     * @param oneSigmaSys the value of oneSigmaSys
     * @param reference related academic work
     */
    public ValueModelReferenced (
            String name, BigDecimal value, String uncertaintyType, BigDecimal oneSigma, BigDecimal oneSigmaSys, String reference) {

        super( name, value, uncertaintyType, oneSigma, oneSigmaSys );
        this.reference = reference;
    }

    /**
     * Returns a deep copy of a
     * <code>ValueModelReferenced</code>; a new
     * <code>
     * ValueModelReferenced</code> whose fields are equal to those of this
     * <code>ValueModelReferenced</code>
     *
     * @pre this
     * <code>ValueModelReferenced</code> exists @post a new
     * <code>ValueModelReferenced</code> with identical data to this
     * <code> ValueModelReferenced</code> is returned
     *
     * @return
     * <code>ValueModelReferenced</code> - a new
     * <code>ValueModelReferenced
     * </code> whose fields match those of this
     * <code>ValueModelReferenced</code>
     */
    @Override
    public ValueModelReferenced copy () {
        return new ValueModelReferenced(
                getName(),
                getValue(),
                getUncertaintyType(),
                getOneSigma(), getOneSigmaSys(),
                getReference() );
    }

    @Override
    public void copyValuesFrom ( ValueModel valueModel ) {
        this.setValue( valueModel.getValue() );
        this.setUncertaintyType( valueModel.getUncertaintyType() );
        this.setOneSigma( valueModel.getOneSigma() );
        this.setOneSigmaSys( valueModel.getOneSigmaSys() );
        this.setValueTree( valueModel.getValueTree() );
        this.setReference( ((ValueModelReferenced) valueModel).reference );
    }

    /**
     * gets the value of the
     * <code>reference</code> field
     *
     * @pre this
     * <code>ValueModelReferenced</code> exists @post
     * <code>reference</code> of this
     * <code>ValueModelReferenced</code> is returned
     *
     * @return
     * <code>String</code> -
     * <code>reference</code> of this
     * <code>
     *          ValueModelReferenced</code>
     */
    public String getReference () {
        return reference;
    }

    /**
     * sets the value of the
     * <code>reference</code> field
     *
     * @pre argument
     * <code>reference</code> is a valid
     * <code>String</code> @post
     * <code>reference</code> of this
     * <code>ValueModelReferenced</code> is set to argument
     * <code>reference</code>
     *
     * @param reference value to which this
     * <code>ValueModelReferenced</code>'s
     * <code>reference</code> is set
     */
    public void setReference ( String reference ) {
        this.reference = reference;
    }

    /**
     * registers converter for argument
     * <code>xstream</code> and sets aliases to make the XML file more
     * human-readable
     *
     * @pre argument
     * <code>xstream</code> is a valid
     * <code>XStream</code> @post argument
     * <code>xstream</code> is customized to produce a cleaner output
     * <code>file</code>
     *
     * @param xstream
     * <code>XStream</code> to be customized
     */
    @Override
    public void customizeXstream ( XStream xstream ) {

        //xstream.registerConverter(new ValueModelXMLConverter());
        xstream.registerConverter( new ValueModelReferencedXMLConverter() );

        //xstream.alias("ValueModel", ValueModel.class);
        xstream.alias( "ValueModelReferenced", ValueModelReferenced.class );

        setClassXMLSchemaURL();
    }

    /**
     * encodes this
     * <code>ValueModelReferenced</code> to the
     * <code>file</code> specified by the argument
     * <code>filename</code>
     *
     * @pre this
     * <code>ValueModelReferenced</code> exists @post this
     * <code>ValueModelReferenced</code> is stored in the specified XML
     * <code>file</code>
     *
     * @param filename location to store data to
     */
    @Override
    public void serializeXMLObject ( String filename ) {
        XStream xstream = getXStreamWriter();

        String xml = xstream.toXML( this );

        xml = ReduxConstants.XML_Header + xml;

        xml = xml.replaceFirst("ValueModelReferenced",
                "ValueModelReferenced " + ReduxConstants.XML_ResourceHeader + getValueModelXMLSchemaURL() + "\"" );


        try {
            FileWriter outFile = new FileWriter( filename );
            PrintWriter out = new PrintWriter( outFile );

            // Write xml to file
            out.println( xml );
            out.flush();
            out.close();
            outFile.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * decodes
     * <code>ValueModelReferenced</code> from
     * <code>file</code> specified by argument
     * <code>filename</code>
     *
     * @param filename location to read data from
     * @param doValidate the value of doValidate
     * @return
     * <code>Object</code> - the
     * <code>ValueModelReferenced</code> created from the specified XML
     * <code>file</code>
     * @throws java.io.FileNotFoundException
     * @throws org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException @pre
     * <code>filename</code> references an XML
     * <code>file</code> @post
     * <code>ValueModelReferenced</code> stored in
     * <code>filename</code> is returned
     */
    @Override
    public Object readXMLObject ( String filename, boolean doValidate )
            throws FileNotFoundException, ETException, FileNotFoundException, BadOrMissingXMLSchemaException {
        ValueModel myValueModel = null;

        BufferedReader reader = URIHelper.getBufferedReader( filename );

        if ( reader != null ) {
            boolean isValidOrAirplaneMode = !doValidate;
            
            XStream xstream = getXStreamReader();

            isValidOrAirplaneMode = URIHelper.validateXML( reader, filename, getValueModelXMLSchemaURL() );

            if ( isValidOrAirplaneMode ) {
                // re-create reader
                reader = URIHelper.getBufferedReader( filename );
                try {
                    myValueModel = (ValueModelReferenced) xstream.fromXML( reader );
                } catch (ConversionException e) {
                    throw new ETException( null, e.getMessage() );
                }

                System.out.println( "\nThis is your ValueModelReferenced that was just read successfully:\n" );

                String xml2 = getXStreamWriter().toXML( myValueModel );

                System.out.println( xml2 );
                System.out.flush();
            } else {
                throw new ETException( null, "XML data file does not conform to schema." );
            }
        } else {
            throw new FileNotFoundException( "Missing XML data file." );
        }
        return myValueModel;
    }

    /**
     *
     * @param args
     * @throws Exception
     */
    public static void main ( String[] args ) throws Exception {

        ValueModel valueModel =
                new ValueModelReferenced( "r206_204b", new BigDecimal( "1234567890" ), "ABS", new BigDecimal( "123000" ), BigDecimal.ZERO, "test reference" );
        System.out.println(
                "Format Test: " + valueModel.formatValueAndTwoSigmaForPublicationSigDigMode( "ABS", 6, 2 ) );


        String testFileName = "ValueModelReferencedTEST.xml";

        valueModel.serializeXMLObject( testFileName );
        valueModel.readXMLObject( testFileName, true );

    }
//        private void readObject(
//            ObjectInputStream stream)
//            throws IOException, ClassNotFoundException {
//        stream.defaultReadObject();
//        ObjectStreamClass myObject = ObjectStreamClass.lookup(
//                Class.forName(ValueModelReferenced.class.getCanonicalName()));
//        long theSUID = myObject.getSerialVersionUID();
//        System.out.println("Customized De-serialization of ValueModelReferenced " + theSUID);
//    }
}
