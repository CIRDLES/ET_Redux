/*
 * PhysicalConstants.java
 *
 * Created on August 1, 2007, 7:33 AM
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
package org.earthtime.physicalConstants;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.io.xml.DomDriver;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.user.UPbReduxConfigurator;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.UPb_Redux.valueModels.ValueModelReferenced;
import org.earthtime.UPb_Redux.valueModels.ValueModelReferencedXMLConverter;
import org.earthtime.UPb_Redux.valueModels.ValueModelXMLConverter;
import org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException;
import org.earthtime.archivingTools.URIHelper;
import org.earthtime.dataDictionaries.DataDictionary;
import org.earthtime.exceptions.ETException;
import org.earthtime.matrices.matrixModels.CovarianceMatrixModel;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataModels.physicalConstantsModels.PhysicalConstantsModel;
import org.earthtime.reduxLabData.ReduxLabData;
import org.earthtime.reduxLabData.ReduxLabDataListElementI;
import org.earthtime.utilities.DateHelpers;
import org.earthtime.xmlUtilities.XMLSerializationI;

/**
 *
 * @author James F. Bowring
 */
public class PhysicalConstants implements
        Comparable<PhysicalConstants>,
        PhysicalConstantsI,
        XMLSerializationI,
        Serializable,
        ReduxLabDataListElementI {

    private static final long serialVersionUID = -7947593462088652103L;
    private static PhysicalConstants instanceOfEARTHTIMEV1 = null;
    /**
     *
     */
    protected transient String physicalConstantsXMLSchemaURL;
//    protected AbstractMatrixModel lambdasCovarianceMatrix;
    // Fields
    /**
     *
     */
    protected String name;
    /**
     *
     */
    protected int version;
    /**
     *
     */
    protected ValueModel[] atomicMolarMasses;
    /**
     *
     */
    protected ValueModel[] measuredConstants;
    /**
     *
     */
    protected String physicalConstantsComment;

    /**
     * Creates a new instanceOfEARTHTIMEV1 of PhysicalConstants
     */
    public PhysicalConstants() {
        this(ReduxConstants.NONE, 0);
    }

    /**
     * Creates a new instanceOfEARTHTIMEV1 of PhysicalConstants
     *
     * @param name
     * @param version
     */
    public PhysicalConstants(String name, int version) {

        this.name = name;
        this.version = version;

        atomicMolarMasses
                = new ValueModel[DataDictionary.AtomicMolarMasses.length];
        for (int i = 0; i < DataDictionary.AtomicMolarMasses.length; i++) {
            atomicMolarMasses[i]
                    = new ValueModel(
                            DataDictionary.AtomicMolarMasses[i][0],
                            new BigDecimal(DataDictionary.AtomicMolarMasses[i][1]),
                            ValueModel.DEFAULT_UNCERTAINTY_TYPE,
                            BigDecimal.ZERO, BigDecimal.ZERO);
        }

        Arrays.sort(atomicMolarMasses);

        measuredConstants
                = new ValueModel[DataDictionary.MeasuredConstants.length];
        for (int i = 0; i < DataDictionary.MeasuredConstants.length; i++) {
            measuredConstants[i]
                    = new ValueModelReferenced(
                            DataDictionary.MeasuredConstants[i][0],
                            BigDecimal.ZERO,
                            "PCT",
                            BigDecimal.ZERO, BigDecimal.ZERO,
                            "NONE");
        }

        Arrays.sort(measuredConstants);

        physicalConstantsComment = "NONE";

    }

    /**
     *
     * @return
     */
    public static PhysicalConstants EARTHTIMEPhysicalConstants() {
        instanceOfEARTHTIMEV1 = new PhysicalConstants("EARTHTIME", 1);

        // note need to use dummy arrays as this.setter of ValueModels culls them
        ValueModel[] atomicMM
                = new ValueModel[DataDictionary.AtomicMolarMasses.length];
        for (int i = 0; i < DataDictionary.AtomicMolarMasses.length; i++) {
            atomicMM[i]
                    = new ValueModel(
                            DataDictionary.AtomicMolarMasses[i][0],
                            new BigDecimal(DataDictionary.AtomicMolarMasses[i][1]),
                            "NONE",
                            BigDecimal.ZERO, BigDecimal.ZERO);
        }
        instanceOfEARTHTIMEV1.setAtomicMolarMasses(atomicMM);

        Arrays.sort(instanceOfEARTHTIMEV1.getAtomicMolarMasses());

        ValueModel[] measuredC
                = new ValueModel[DataDictionary.MeasuredConstants.length];
        for (int i = 0; i < DataDictionary.MeasuredConstants.length; i++) {
            measuredC[i]
                    = new ValueModelReferenced(
                            DataDictionary.MeasuredConstants[i][0],
                            new BigDecimal(DataDictionary.MeasuredConstants[i][1]),
                            "PCT",
                            new BigDecimal(DataDictionary.MeasuredConstants[i][2]), BigDecimal.ZERO,
                            DataDictionary.MeasuredConstants[i][3]);
        }

        instanceOfEARTHTIMEV1.setMeasuredConstants(measuredC);

        Arrays.sort(instanceOfEARTHTIMEV1.getMeasuredConstants());

        instanceOfEARTHTIMEV1.setPhysicalConstantsComment(
                "This Physical Constants Model \n" + "is the accepted default as of 2008"
                + ".");

        return instanceOfEARTHTIMEV1;
    }

    // used for deserialization
    /**
     *
     * @return
     */
    protected Object readResolve() {

        AbstractRatiosDataModel physicalConstantsModel;

        if ((this.name.toUpperCase(Locale.US).startsWith("EARTHTIME"))) {
            // catch old Earthtime version 1
            physicalConstantsModel = ReduxLabData.getInstance().getDefaultPhysicalConstantsModel();
        } else {
            physicalConstantsModel = convertModel(this);
        }

        return physicalConstantsModel;
    }

    /**
     *
     * @param model
     * @return
     */
    public static AbstractRatiosDataModel convertModel(PhysicalConstants model) {

        // there were no correlations
        Map<String, BigDecimal> correlations = new HashMap<String, BigDecimal>();

        return//
                PhysicalConstantsModel.createInstance( //
                        model.name, //
                        1, 0, //
                        "Unknown Lab",//
                        DateHelpers.defaultEarthTimeDateString(),//
                        "No reference", //
                        model.physicalConstantsComment, //
                        model.measuredConstants, //
                        correlations,//
                        PhysicalConstantsModel.getEARTHTIMEatomicMolarMasses());

    }

    /**
     *
     * @return
     */
    @Override
    public PhysicalConstants Copy() {
        PhysicalConstants tempModel
                = new PhysicalConstants(getName(), getVersion());

        ValueModel[] tempAtomicMolarMasses
                = new ValueModel[getAtomicMolarMasses().length];
        for (int i = 0; i < getAtomicMolarMasses().length; i++) {
            tempAtomicMolarMasses[i] = getAtomicMolarMasses()[i].copy();
        }
        tempModel.setAtomicMolarMasses(tempAtomicMolarMasses);

        ValueModel[] tempMeasuredConstants
                = new ValueModel[getMeasuredConstants().length];
        for (int i = 0; i < getMeasuredConstants().length; i++) {
            tempMeasuredConstants[i] = getMeasuredConstants()[i].copy();
        }
        tempModel.setMeasuredConstants(tempMeasuredConstants);

        tempModel.setPhysicalConstantsComment(getPhysicalConstantsComment());

        return tempModel;
    }

    /**
     *
     * @param physicalConstantsModel
     * @return
     * @throws ClassCastException
     */
    @Override
    public int compareTo(PhysicalConstants physicalConstantsModel)
            throws ClassCastException {
        String physicalConstantsModelNameAndVersion
                = ((PhysicalConstants) physicalConstantsModel).getNameAndVersion();
        return this.getNameAndVersion().trim().//
                compareToIgnoreCase(physicalConstantsModelNameAndVersion.trim());
    }

    /**
     *
     * @param physicalConstantsModel
     * @return
     */
    @Override
    public boolean equals(Object physicalConstantsModel) {
        //check for self-comparison
        if (this == physicalConstantsModel) {
            return true;
        }
        if (!(physicalConstantsModel instanceof PhysicalConstants)) {
            return false;
        }

        PhysicalConstants myPhysicalConstants = (PhysicalConstants) physicalConstantsModel;

        return (this.getNameAndVersion().trim().
                compareToIgnoreCase(myPhysicalConstants.getNameAndVersion().trim()) == 0);

    }

    // http://www.javaworld.com/javaworld/jw-01-1999/jw-01-object.html?page=4
    /**
     *
     * @return
     */
    @Override
    public int hashCode() {

        return 0;
    }

    //  accessors
    /**
     *
     * @return
     */
    @Override
    public String getReduxLabDataElementName() {
        return getNameAndVersion();
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
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     */
    public int getVersion() {
        return version;
    }

    /**
     *
     * @param version
     */
    public void setVersion(int version) {
        this.version = version;
    }

    /**
     *
     * @return
     */
    public String getNameAndVersion() {
        return getName().trim() + " v." + getVersion();
    }

    /**
     *
     * @return
     */
    @Override
    public ValueModel[] getAtomicMolarMasses() {
        return atomicMolarMasses;
    }

    /**
     *
     * @param atomicMolarMasses
     */
    @Override
    public void setAtomicMolarMasses(ValueModel[] atomicMolarMasses) {
        this.atomicMolarMasses = ValueModel.cullNullsFromArray(atomicMolarMasses);
    }

    /**
     *
     * @return
     */
    @Override
    public ValueModel[] getMeasuredConstants() {
        return measuredConstants;
    }

    /**
     *
     * @param measuredConstants
     */
    @Override
    public void setMeasuredConstants(ValueModel[] measuredConstants) {
        this.measuredConstants = ValueModel.cullNullsFromArray(measuredConstants);
    }

    /**
     *
     * @return
     */
    @Override
    public String getPhysicalConstantsComment() {
        return physicalConstantsComment;
    }

    /**
     *
     * @param physicalConstantsComment
     */
    @Override
    public void setPhysicalConstantsComment(String physicalConstantsComment) {
        this.physicalConstantsComment = physicalConstantsComment;
    }


    // XML Serialization
    /**
     *
     * @return
     */
    private XStream getXStreamWriter() {
        XStream xstream = new XStream();

        customizeXstream(xstream);

        return xstream;
    }

    /**
     *
     * @return
     */
    private XStream getXStreamReader() {

        XStream xstream = new XStream(new DomDriver());

        customizeXstream(xstream);

        return xstream;
    }

    /**
     *
     * @param xstream
     */
    private void customizeXstream(XStream xstream) {

        xstream.registerConverter(new PhysicalConstantsXMLConverter());
        xstream.registerConverter(new ValueModelXMLConverter());
        xstream.registerConverter(new ValueModelReferencedXMLConverter());

        xstream.alias("PhysicalConstants", PhysicalConstants.class);
        xstream.alias("ValueModel", ValueModel.class);
        xstream.alias("ValueModelReferenced", ValueModelReferenced.class);
        xstream.alias("CovarianceMatrixModel", CovarianceMatrixModel.class);

        setClassXMLSchemaURL();

    }

    /**
     *
     */
    private void setClassXMLSchemaURL() {
        UPbReduxConfigurator myConfigurator = new UPbReduxConfigurator();

        physicalConstantsXMLSchemaURL
                = myConfigurator.getResourceURI("URI_PhysicalConstantsXMLSchema");
    }

    /**
     *
     * @param filename
     */
    @Override
    public void serializeXMLObject(String filename) {
        XStream xstream = getXStreamWriter();

        String xml = xstream.toXML(this);

        xml = ReduxConstants.XML_Header + xml;

        xml = xml.replaceFirst("PhysicalConstants",
                "PhysicalConstants "//
                + ReduxConstants.XML_ResourceHeader//
                + physicalConstantsXMLSchemaURL//
                + "\"");

        try {
            FileWriter outFile = new FileWriter(filename);
            PrintWriter out = new PrintWriter(outFile);

            // Write xml to file
            out.println(xml);
            out.flush();
            out.close();
            outFile.close();

        } catch (IOException e) {
//            e.printStackTrace();
        }
    }

    /**
     *
     *
     * @param filename
     * @param doValidate the value of doValidate
     * @return
     * @throws FileNotFoundException
     * @throws ETException
     * @throws BadOrMissingXMLSchemaException
     */
    public Object readXMLObject(String filename, boolean doValidate)
            throws FileNotFoundException, ETException, BadOrMissingXMLSchemaException {
        PhysicalConstants myPhysicalConstants = null;

        BufferedReader reader = URIHelper.getBufferedReader(filename);

        if (reader != null) {
            boolean temp = false;
            XStream xstream = getXStreamReader();

            temp = URIHelper.validateXML(reader, filename, physicalConstantsXMLSchemaURL);

            if (temp) {
                // re-create reader
                reader = URIHelper.getBufferedReader(filename);
                try {
                    myPhysicalConstants = (PhysicalConstants) xstream.fromXML(reader);
                } catch (ConversionException e) {
                    throw new ETException(null, e.getMessage());
                }

//                System.out.println("This is your PhysicalConstants that was just read successfully:\n");

//                String xml2 = getXStreamWriter().toXML(myPhysicalConstants);
//
//                System.out.println(xml2);
//                System.out.flush();
            } else {
                throw new ETException(null, "Badly formed PhysicalConstants XML data file.");
            }
        } else {
            throw new FileNotFoundException("Missing XML data file.");
        }

        return myPhysicalConstants;
    }

    /**
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        PhysicalConstants physicalConstants = EARTHTIMEPhysicalConstants();
//                new PhysicalConstants( "testset", 1 );
        String testFileName = "PhysicalConstantsTEST.xml";

        physicalConstants.serializeXMLObject(testFileName);

//        PhysicalConstants physicalConstants2 = (PhysicalConstants) physicalConstants.readXMLObject( testFileName, true );
//        ETSerializer.SerializeObjectToFile( physicalConstants, "test.ser" );
//        PhysicalConstants test = (PhysicalConstants) ETSerializer.GetSerializedObjectFromFile( "test.ser" );
//        System.out.println(physicalConstants2.getLambdasCovarianceMatrix().ToStringWithLabels());
    }

    /**
     *
     */
    @Override
    public void removeSelf() {
//        throw new UnsupportedOperationException( "Not supported yet." );
    }
}
