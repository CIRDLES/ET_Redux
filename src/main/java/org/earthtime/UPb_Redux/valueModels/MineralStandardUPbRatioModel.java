/*
 * MineralStandardUPbRatioModel.java
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
import java.io.Serializable;
import java.math.BigDecimal;
import org.earthtime.xmlUtilities.XMLSerializationI;

/**
 * <code>MeasuredRatioModels</code> are special
 * <code>ValueModels</code> that abstract isotope ratios and uncertainties with
 * <code>boolean</code> flags denoting whether or not the ratio has been
 * fractionation-corrected and whether or not it has been oxide-corrected. Each
 * flag is
 * <code>true</code> if the correction occurred on a datum-by-datum basis before
 * the means were calculated, else
 * <code>false</code>.
 *
 * @author James F. Bowring, javaDocs by Stan Gasque
 */
public class MineralStandardUPbRatioModel extends ValueModel implements
        Comparable<ValueModel>,
        Serializable,
        XMLSerializationI {

    // Class variables
    /**
     * identifies object in binary serialization
     */
    private static final long serialVersionUID = -8248719653895373170L;
    // Instance variables
    private boolean measured;

    /**
     * creates a new instance of
     * <code>MeasuredRatioModel</code> with values set according to the default
     * <code>ValueModel</code> constructor and
     * <code>fracCorr</code> and
     * <code>oxideCorr</code> both initialized to
     * <code>false</code>.
     */
    public MineralStandardUPbRatioModel () {
        super();
        this.measured = true;
    }
    
    /**
     * 
     * @param name
     */
    public MineralStandardUPbRatioModel (String name) {
        this();
        this.name = name;
    }

    /**
     * creates a new instance of
     * <code>MeasuredRatioModel</code> with its values initialized to the given
     * arguments.
     *
     * @param name the
     * <code>name</code> of the ratio
     * @param value the
     * <code>value</code> of the ratio
     * @param uncertaintyType the
     * <code>uncertaintyType</code> of the ratio
     * @param oneSigma the
     * <code>oneSigma</code> of the ratio
     * @param measured  
     */
    public MineralStandardUPbRatioModel (
            String name, //
            BigDecimal value, //
            String uncertaintyType,//
            BigDecimal oneSigma, //
            boolean measured) {

        super( name, value, uncertaintyType, oneSigma, BigDecimal.ZERO );
        this.measured = measured;
    }

    /**
     * returns a deep copy of this
     * <code>MeasuredRatioModel</code>.
     *
     * @pre this
     * <code>MeasuredRatioModel</code> exists @post a new
     * <code>MeasuedRatioModel</code> with identical data to this
     * <code>MeasuredRatioModel</code> is returned
     *
     * @return
     * <code>MeasuredRatioModel</code> - a new
     * <code>MeasuredRatioModel</code> whose fields match those of this
     * <code>MeasuredRatioModel</code>
     */
    @Override
    public MineralStandardUPbRatioModel copy () {
        return new MineralStandardUPbRatioModel(
                name,
                value,
                uncertaintyType,
                oneSigma,
                measured);
    }

    /**
     * copies the data from a given
     * <code>ValueModel</code> into this
     * <code>
     * MeasuredRatioModel</code>'s fields
     *
     * @pre argument
     * <code>valueModel</code> is a valid
     * <code>MeasuredRatioModel</code> @post this
     * <code>MeasuredRatioModel</code>'s fields are set to the values of
     * argument
     * <code>valueModel</code>'s respective fields
     *
     * @param valueModel
     * <code>ValueModel</code> whose fields will be copied
     */
    @Override
    public void copyValuesFrom ( ValueModel valueModel ) {
        this.value = valueModel.value;
        this.uncertaintyType = valueModel.uncertaintyType;
        this.oneSigma = valueModel.oneSigma;
        this.setValueTree( valueModel.getValueTree() );
        try {
            this.measured = ((MineralStandardUPbRatioModel) valueModel).measured;
        } catch (Exception e) {
        }
    }

    /**
     * formats
     * <code>value</code> and
     * <code>oneSigma</code> for display as a table; outputs in the format
     * "(value) : (oneSigma)".
     *
     * @pre this
     * <code>MeasuredRatioModel</code> exists @post returns
     * <code>value</code> and
     * <code>oneSigma</code> of this
     * <code>
     *          MeasuredRatioModel</code> formatted to be viewed as a table
     *
     * @return
     * <code>String</code> - the formatted
     * <code>value</code> and
     * <code>oneSigma</code> of this
     * <code>MeasuredRatioModel</code>
     */
    public String toTableFormat () {
        return getValue().toPlainString()//
                + " : "//
                + getOneSigma().toPlainString();
    }

    @Override
    public void customizeXstream ( XStream xstream ) {

        xstream.registerConverter( new MineralStandardUPbRatioModelXMLConverter() );

        xstream.alias( "MineralStandardUPbRatioModel", MineralStandardUPbRatioModel.class );

        setClassXMLSchemaURL();
    }

 
    /**
     *
     * @param args
     * @throws Exception
     */
    public static void main ( String[] args ) throws Exception {

//        ValueModel valueModel =
//                new MineralStandardUPbRatioModel(//
//                "r206_204b", new BigDecimal( "1234567890" ), "ABS", new BigDecimal( "123000" ), true, true );
//        System.out.println(
//                "Format Test: " + valueModel.formatValueAndTwoSigmaForPublicationSigDigMode( "ABS", 6, 2 ) );
//
//
//        String testFileName = "MeasuredRatioModelTEST.xml";
//
//        valueModel.serializeXMLObject( testFileName );
//        valueModel.readXMLObject( testFileName, true );

    }

    /**
     * @return the measured
     */
    public boolean isMeasured () {
        return measured;
    }

    /**
     * @param measured the measured to set
     */
    public void setMeasured ( boolean measured ) {
        this.measured = measured;
    }
//        private void readObject (
//            ObjectInputStream stream )
//            throws IOException, ClassNotFoundException {
//        stream.defaultReadObject();
//        ObjectStreamClass myObject = ObjectStreamClass.lookup(
//                Class.forName( MineralStandardUPbRatioModel.class.getCanonicalName() ) );
//        long theSUID = myObject.getSerialVersionUID();
//        System.out.println( "Customized De-serialization of MineralStandardUPbRatioModel " + theSUID );
//    }
}
