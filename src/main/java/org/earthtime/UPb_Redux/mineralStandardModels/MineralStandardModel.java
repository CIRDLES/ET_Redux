/*
 * MineralStandardModel.java
 *
 * Created on August 2, 2007, 9:28 AM
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
package org.earthtime.UPb_Redux.mineralStandardModels;

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
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.reduxLabData.ReduxLabDataListElementI;
import org.earthtime.UPb_Redux.user.UPbReduxConfigurator;
import org.earthtime.UPb_Redux.valueModels.MineralStandardUPbRatioModel;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.UPb_Redux.valueModels.ValueModelReferenced;
import org.earthtime.UPb_Redux.valueModels.ValueModelReferencedXMLConverter;
import org.earthtime.UPb_Redux.valueModels.ValueModelXMLConverter;
import org.earthtime.XMLExceptions.BadOrMissingXMLSchemaException;
import org.earthtime.archivingTools.URIHelper;
import org.earthtime.dataDictionaries.DataDictionary;
import org.earthtime.exceptions.ETException;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataModels.initialPbModelsET.InitialPbModelET;
import org.earthtime.ratioDataModels.mineralStandardModels.MineralStandardUPbModel;
import org.earthtime.utilities.DateHelpers;
import org.earthtime.xmlUtilities.XMLSerializationI;

/**
 * Deprecated June 2012.  Needed for compatibility with legacy serializations (archives).
 * @author James F. Bowring
 */
public class MineralStandardModel implements
        Comparable<MineralStandardModel>,
        Serializable,
        MineralStandardModelI,
        XMLSerializationI,
        ReduxLabDataListElementI {

    // Class variables
    private static final long serialVersionUID = -5428713038056324964L;
    private transient String mineralStandardModelXMLSchemaURL;
    // Instance variables
    private String name;
    private String mineralStandardName;
    private String standardMineralName;
    private ValueModelReferenced trueAge;
    private ValueModelReferenced measuredAge;
    private String comment;
    // modified oct 2011 to include radiogenic ratios for isotopic composition
    private ValueModel[] radiogenicIsotopeRatios;

    /**
     * Creates a new instance of MineralStandardModel
     */
    public MineralStandardModel () {
        this.name = ReduxConstants.DEFAULT_OBJECT_NAME;
        this.mineralStandardName = ReduxConstants.DEFAULT_OBJECT_NAME;
        this.standardMineralName = ReduxConstants.DEFAULT_OBJECT_NAME;
        this.trueAge = new ValueModelReferenced(
                "TrueAge",
                BigDecimal.ZERO,
                "ABS",
                BigDecimal.ZERO, BigDecimal.ZERO,
                "Reference" );
        this.measuredAge = new ValueModelReferenced(
                "MeasuredAge",
                BigDecimal.ZERO,
                "ABS",
                BigDecimal.ZERO, BigDecimal.ZERO,
                "Reference" );
        this.comment = "Comment";

        initializeRadiogenicRatios();
    }

    /**
     * 
     * @return
     */
    protected Object readResolve () {

        // march 2012 conversion to new MineralStandardUPbModel
        AbstractRatiosDataModel mineralStandardUPbModel;

        if ( (this.name.equalsIgnoreCase( "<none>" )) ) {

            mineralStandardUPbModel = MineralStandardUPbModel.getNoneInstance();//       convertModel( new MineralStandardModel( ReduxConstants.NONE ) );
        } else {
            mineralStandardUPbModel = convertModel( this );
        }

        return mineralStandardUPbModel;
    }

    /**
     * 
     * @param model
     * @return
     */
    public static AbstractRatiosDataModel convertModel ( MineralStandardModel model ) {

        ValueModel[] ratios = new MineralStandardUPbRatioModel[ model.radiogenicIsotopeRatios.length];
        for (int i = 0; i < ratios.length; i ++){
            ratios[i] = new MineralStandardUPbRatioModel(model.radiogenicIsotopeRatios[i].getName());
            ratios[i].copyValuesFrom(model.radiogenicIsotopeRatios[i]);
        }
        
        return//
                MineralStandardUPbModel.createInstance(//
                model.name,//
                1, 0, //
                "Unknown Lab",//
                DateHelpers.defaultEarthTimeDateString(),//
                "No reference", //
                "No comment", //
                ratios, //
                null, null,//
                model.mineralStandardName,//
                model.standardMineralName,//
                InitialPbModelET.getNoneInstance());

    }

    private void initializeRadiogenicRatios () {
        radiogenicIsotopeRatios = new ValueModel[DataDictionary.RadiogenicIsotopeRatioTypes.length];
        for (int i = 0; i < DataDictionary.RadiogenicIsotopeRatioTypes.length; i ++) {
            radiogenicIsotopeRatios[i] =
                    new ValueModel( DataDictionary.getRadiogenicIsotopeRatioTypes( i ),
                    BigDecimal.ZERO,
                    "PCT",
                    BigDecimal.ZERO, BigDecimal.ZERO );
        }
    }

    /**
     *
     * @param modelName
     */
    public MineralStandardModel ( String modelName ) {
        this();
        this.name = modelName.trim();

    }

    /**
     *
     * @return
     */
    public MineralStandardModel Copy () {
        MineralStandardModel tempModel = new MineralStandardModel( getName() );

        tempModel.setMineralStandardName( getMineralStandardName() );
        tempModel.setStandardMineralName( getStandardMineralName() );
        tempModel.setTrueAge( getTrueAge().copy() );
        tempModel.setMeasuredAge( getMeasuredAge().copy() );
        tempModel.setComment( getComment() );

        ValueModel[] myCopy = new ValueModel[radiogenicIsotopeRatios.length];
        for (int i = 0; i < myCopy.length; i ++) {
            myCopy[i] = radiogenicIsotopeRatios[i].copy();
        }

        tempModel.setRadiogenicIsotopeRatios( myCopy );

        return tempModel;
    }

    /**
     *
     * @param mineralStandardModel
     * @return
     * @throws ClassCastException
     */
    @Override
    public int compareTo ( MineralStandardModel mineralStandardModel ) throws ClassCastException {
        String mineralStandardModelName = ((MineralStandardModel) mineralStandardModel).getName();
        return this.getName().trim().compareToIgnoreCase( mineralStandardModelName.trim() );
    }

    /**
     *
     * @param mineralStandardModel
     * @return
     */
    @Override
    public boolean equals ( Object mineralStandardModel ) {
        //check for self-comparison
        if ( this == mineralStandardModel ) {
            return true;
        }
        if (  ! (mineralStandardModel instanceof MineralStandardModel) ) {
            return false;
        }

        MineralStandardModel myMineralStandardModel = (MineralStandardModel) mineralStandardModel;

        return (this.getName().trim().
                compareToIgnoreCase( myMineralStandardModel.getName().trim() ) == 0);

    }

    // http://www.javaworld.com/javaworld/jw-01-1999/jw-01-object.html?page=4
    /**
     *
     * @return
     */
    @Override
    public int hashCode () {

        return 0;
    }

    /**
     * gets a single ratio from this
     * <code>MineralStandardModel</code>'s
     * <code>radiogenicIsotopeRatios</code> specified by argument
     * <code>ratioName</code>. Returns a new, empty
     * <code>
     * ValueModel</code> if no matching radiogenicIsotopeRatios is found.
     *
     * @pre argument
     * <code>ratioName</code> is a valid
     * <code>String</code> @post returns the
     * <code>ValueModel</code> found in this
     * <code>MineralStandardModel</code>'s
     * <code>radiogenicIsotopeRatios</code> whose name matches argument
     * <code>ratioName</code>
     *
     * @param ratioName name of the radiogenicIsotopeRatios to search for
     * @return
     * <code>ValueModel</code> - ratio found in
     * <code>radiogenicIsotopeRatios</code> whose name matches argument
     * <code>ratioName</code> or a new
     * <code>
     *          ValueModel</code> if no match is found
     */
    public ValueModel getRadiogenicRatioByName ( String ratioName ) {

        if ( radiogenicIsotopeRatios == null ) {
            initializeRadiogenicRatios();
        }

        ValueModel retVal = null;
        for (int i = 0; i < radiogenicIsotopeRatios.length; i ++) {
            if ( radiogenicIsotopeRatios[i].getName().equals( ratioName ) ) {
                retVal = radiogenicIsotopeRatios[i];
            }
        }

        if ( retVal == null ) {
            // not found
            retVal = new ValueModel( ratioName, "PCT" );

            ValueModel[] temp = new ValueModel[radiogenicIsotopeRatios.length + 1];
            System.arraycopy( radiogenicIsotopeRatios, 0, temp, 0, radiogenicIsotopeRatios.length );

            temp[temp.length - 1] = retVal;
            radiogenicIsotopeRatios = temp;
        }

        return retVal;
    }

    // XML Serialization
    /**
     *
     * @return
     */
    private XStream getXStreamWriter () {
        XStream xstream = new XStream();

        customizeXstream( xstream );

        return xstream;
    }

    /**
     *
     * @return
     */
    private XStream getXStreamReader () {
        XStream xstream = new XStream( new DomDriver() );

        customizeXstream( xstream );

        return xstream;
    }

    /**
     *
     * @param xstream
     */
    private void customizeXstream ( XStream xstream ) {
        xstream.registerConverter( new MineralStandardModelXMLConverter() );
        xstream.registerConverter( new ValueModelReferencedXMLConverter() );
        xstream.registerConverter( new ValueModelXMLConverter() );

        xstream.alias( "MineralStandardModel", MineralStandardModel.class );
        xstream.alias( "ValueModelReferenced", ValueModelReferenced.class );
        xstream.alias( "ValueModel", ValueModel.class );

        setClassXMLSchemaURL();
    }

    /**
     *
     */
    private void setClassXMLSchemaURL () {
        UPbReduxConfigurator myConfigurator = new UPbReduxConfigurator();

        mineralStandardModelXMLSchemaURL =
                myConfigurator.getResourceURI( "URI_MineralStandardModelXMLSchemaURL" );
    }

    /**
     *
     * @param filename
     */
    @Override
    public void serializeXMLObject ( String filename ) {
        XStream xstream = getXStreamWriter();

        String xml = xstream.toXML( this );

        xml = ReduxConstants.XML_Header + xml;

        xml = xml.replaceFirst("MineralStandardModel",
                "MineralStandardModel "//
                + ReduxConstants.XML_ResourceHeader//
                + mineralStandardModelXMLSchemaURL +//
                "\"" );


        try {
            FileWriter outFile = new FileWriter( filename );
            PrintWriter out = new PrintWriter( outFile );

            // Write xml to file
            out.println( xml );
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
    @Override
    public Object readXMLObject ( String filename, boolean doValidate )
            throws FileNotFoundException,
            ETException,
            FileNotFoundException,
            BadOrMissingXMLSchemaException {

        MineralStandardModel myMineralStandardModel = null;

        BufferedReader reader = URIHelper.getBufferedReader( filename );

        if ( reader != null ) {
            boolean temp = false;
            XStream xstream = getXStreamReader();

            temp = URIHelper.validateXML( reader, filename, mineralStandardModelXMLSchemaURL );

            if ( temp ) {
                // re-create reader
                reader = URIHelper.getBufferedReader( filename );
                try {
                    myMineralStandardModel = (MineralStandardModel) xstream.fromXML( reader );
                } catch (ConversionException e) {
                    throw new ETException( null, e.getMessage() );
                }

            } else {
                throw new FileNotFoundException( "Badly formed or unvalidated XML data file." );
            }

        } else {
            throw new FileNotFoundException( "Missing XML data file." );
        }



        return myMineralStandardModel;
    }

    // Accessors
    /**
     *
     * @return
     */
    @Override
    public String getName () {
        return name;
    }

    /**
     *
     * @return
     */
    @Override
    public String getReduxLabDataElementName () {
        return getName();
    }

    /**
     *
     * @param modelName
     */
    @Override
    public void setName ( String modelName ) {
        this.name = modelName.trim();
    }

    /**
     *
     * @return
     */
    @Override
    public String getMineralStandardName () {
        return mineralStandardName;
    }

    /**
     *
     * @param name
     */
    @Override
    public void setMineralStandardName ( String name ) {
        this.mineralStandardName = name.trim();
    }

    /**
     *
     * @return
     */
    @Override
    public String getStandardMineralName () {
        return standardMineralName;
    }

    /**
     *
     * @param standardMineral
     */
    @Override
    public void setStandardMineralName ( String standardMineral ) {
        this.standardMineralName = standardMineral.trim();
    }

    /**
     *
     * @return
     */
    @Override
    public ValueModelReferenced getTrueAge () {
        return trueAge;
    }

    /**
     *
     * @param trueAge
     */
    @Override
    public void setTrueAge ( ValueModelReferenced trueAge ) {
        this.trueAge = trueAge;
    }

    /**
     *
     * @return
     */
    @Override
    public ValueModelReferenced getMeasuredAge () {
        return measuredAge;
    }

    /**
     *
     * @param measuredAge
     */
    @Override
    public void setMeasuredAge ( ValueModelReferenced measuredAge ) {
        this.measuredAge = measuredAge;
    }

    /**
     *
     * @return
     */
    @Override
    public String getComment () {
        return comment;
    }

    /**
     *
     * @param comment
     */
    @Override
    public void setComment ( String comment ) {
        this.comment = comment.trim();
    }

    /**
     *
     * @param args
     * @throws Exception
     */
    public static void main ( String[] args ) throws Exception {

        MineralStandardModel mineralStandardModel = new MineralStandardModel().Copy();
        mineralStandardModel.getTrueAge().setValue( new BigDecimal( "1.1112" ) );
        mineralStandardModel.getMeasuredAge().setValue( new BigDecimal( "1.444" ) );

        mineralStandardModel.setRadiogenicIsotopeRatios( new ValueModel[]{new ValueModel( "r207_235r", new BigDecimal( 1011 ), "PCT", new BigDecimal( 1 ), BigDecimal.ZERO )} );

        String testFileName = "MineralStandardModelTEST.xml";
        mineralStandardModel.serializeXMLObject( testFileName );
        mineralStandardModel.readXMLObject( testFileName, true );

    }

    /**
     * @return the radiogenicIsotopeRatios
     */
    public ValueModel[] getRadiogenicIsotopeRatios () {
        if ( radiogenicIsotopeRatios == null ) {
            initializeRadiogenicRatios();
        }
        return radiogenicIsotopeRatios;
    }

    /**
     * @param radiogenicIsotopeRatios the radiogenicIsotopeRatios to set
     */
    public void setRadiogenicIsotopeRatios ( ValueModel[] radiogenicIsotopeRatios ) {
        this.radiogenicIsotopeRatios = radiogenicIsotopeRatios;
    }

    /**
     * 
     */
    @Override
    public void removeSelf () {
//        throw new UnsupportedOperationException( "Not supported yet." );
    }
}
