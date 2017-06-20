/*
 * PbBlankICModelXMLConverter.java
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
package org.earthtime.ratioDataModels.detritalUraniumAndThoriumModels;

import org.earthtime.ratioDataModels.pbBlankICModels.*;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;

/**
 * A
 * <code>PbBlankXMLConverter</code> is used to marshal and unmarshal data
 * between
 * <code>PbBlanks</code> and XML files.
 *
 * @imports <a
 * href=http://xstream.codehaus.org/javadoc/com/thoughtworks/xstream/converters/Converter.html>
 * com.thoughtworks.xstream.converters.Converter</a> @imports <a
 * href=http://xstream.codehaus.org/javadoc/com/thoughtworks/xstream/converters/MarshallingContext.html>
 * com.thoughtworks.xstream.converters.MarhsallingContext</a> @imports <a
 * href=http://xstream.codehaus.org/javadoc/com/thoughtworks/xstream/converters/UnmarshallingContext.html>
 * com.thoughtworks.xstream.converters.UnmarshallingContext</a> @imports <a
 * href=http://xstream.codehaus.org/javadoc/com/thoughtworks/xstream/io/HierarchicalStreamReader.html>
 * com.thoughtworks.xstream.io.HierachicalSreamReader</a> @imports <a
 * href=http://xstream.codehaus.org/javadoc/com/thoughtworks/xstream/io/HierarchicalStreamWriter.html>
 * com.thoughtworks.xstream.io.HierarchicalStreamWriter</a>
 *
 * @author James F. Bowring
 */
public class DetritalUThModelXMLConverter implements Converter {

    /**
     * checks the argument
     * <code>clazz</code> against
     * <code>PbBlank</code>'s
     * <code>Class</code>. Used to ensure that the object about to be
     * marshalled/unmarshalled is of the correct type.
     *
     * @pre argument
     * <code>clazz</code> is a valid
     * <code>Class</code> @post
     * <code>boolean</code> is returned comparing
     * <code>clazz</code> against
     * <code>PbBlank.class</code>
     *
     * @param clazz
     * <code>Class</code> of the
     * <code>Object</code> you wish to convert to/from XML
     * @return
     * <code>boolean</code> -
     * <code>true</code> if
     * <code>clazz</code> matches
     * <code>PbBlank</code>'s
     * <code>Class</code>; else
     * <code>false</code>.
     */
    @Override
    public boolean canConvert ( Class clazz ) {
        return clazz.equals( DetritalUraniumAndThoriumModel.class );
    }

    /**
     * writes the argument
     * <code>value</code> to the XML file specified through
     * <code>writer</code>
     *
     * @pre
     * <code>value</code> is a valid
     * <code>PbBlank</code>,
     * <code>
     *          writer</code> is a valid
     * <code>HierarchicalStreamWriter</code>, and
     * <code>context</code> is a valid
     * <code>MarshallingContext</code> @post
     * <code>value</code> is written to the XML file specified via
     * <code>writer</code>
     *
     * @param value
     * <code>PbBlank</code> that you wish to write to a file
     * @param writer stream to write through
     * @param context
     * <code>MarshallingContext</code> used to store generic data
     */
    @Override
    public void marshal ( Object value, HierarchicalStreamWriter writer,
            MarshallingContext context ) {

        AbstractRatiosDataModel detritalUraniumAndThoriumModel = (DetritalUraniumAndThoriumModel) value;

        writer.startNode( "modelName" );
        writer.setValue( detritalUraniumAndThoriumModel.getModelName() );
        writer.endNode();

        writer.startNode( "versionNumber" );
        writer.setValue( Integer.toString( detritalUraniumAndThoriumModel.getVersionNumber() ) );
        writer.endNode();

        writer.startNode( "minorVersionNumber" );
        writer.setValue( Integer.toString( detritalUraniumAndThoriumModel.getMinorVersionNumber() ) );
        writer.endNode();

        writer.startNode( "labName" );
        writer.setValue( detritalUraniumAndThoriumModel.getLabName() );
        writer.endNode();

        writer.startNode( "dateCertified" );
        writer.setValue( detritalUraniumAndThoriumModel.getDateCertified() );
        writer.endNode();

        writer.startNode( "reference" );
        writer.setValue( detritalUraniumAndThoriumModel.getReference() );
        writer.endNode();

        writer.startNode( "comment" );
        writer.setValue( detritalUraniumAndThoriumModel.getComment() );
        writer.endNode();

        writer.startNode( "ratios" );
        context.convertAnother( detritalUraniumAndThoriumModel.getData() );
        writer.endNode();

        writer.startNode( "rhos" );
        context.convertAnother( detritalUraniumAndThoriumModel.getRhosVarUnctForXMLSerialization() );
        writer.endNode();


    }

    /**
     * reads a
     * <code>PbBlank</code> from the XML file specified through
     * <code>reader</code>
     *
     * @pre
     * <code>reader</code> leads to a valid
     * <code>PbBlank</code> @post the
     * <code>PbBlank</code> is read from the XML file and returned
     *
     * @param reader stream to read through
     * @param context
     * <code>UnmarshallingContext</code> used to store generic data
     * @return
     * <code>PbBlank</code> -
     * <code>PbBlank</code> read from file specified by
     * <code>reader</code>
     */
    @Override
    public Object unmarshal ( HierarchicalStreamReader reader,
            UnmarshallingContext context ) {

        AbstractRatiosDataModel detritalUraniumAndThoriumModel = DetritalUraniumAndThoriumModel.createNewInstance();

        reader.moveDown();
        detritalUraniumAndThoriumModel.setModelName( reader.getValue() );
        reader.moveUp();

        reader.moveDown();
        detritalUraniumAndThoriumModel.setVersionNumber( Integer.parseInt( reader.getValue() ) );
        reader.moveUp();

        reader.moveDown();
        if ( "minorVersionNumber".equals( reader.getNodeName() ) ) {
            detritalUraniumAndThoriumModel.setMinorVersionNumber( Integer.valueOf( reader.getValue() ) );
            reader.moveUp();
            reader.moveDown();
        } else {
            detritalUraniumAndThoriumModel.setMinorVersionNumber( 0 );
        }

        detritalUraniumAndThoriumModel.setLabName( reader.getValue() );
        reader.moveUp();

        reader.moveDown();
        detritalUraniumAndThoriumModel.setDateCertified( reader.getValue() );
        reader.moveUp();

        reader.moveDown();
        detritalUraniumAndThoriumModel.setReference( reader.getValue() );
        reader.moveUp();

        reader.moveDown();
        detritalUraniumAndThoriumModel.setComment( reader.getValue() );
        reader.moveUp();

        reader.moveDown();
        if ( "ratios".equals( reader.getNodeName() ) ) {
            ArrayList<ValueModel> ratios = new ArrayList<ValueModel>();
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                ValueModel item = new ValueModel();
                item = (ValueModel) context.convertAnother( item, ValueModel.class );
                ratios.add( item );
                reader.moveUp();
            }
            // Convert to array
            ValueModel[] arrayRatios = new ValueModel[ratios.size()];
            for (int i = 0; i < ratios.size(); i ++) {
                arrayRatios[i] = ratios.get( i );
            }

            detritalUraniumAndThoriumModel.setRatios( arrayRatios );
        }
        reader.moveUp();

        reader.moveDown();
        if ( "rhos".equals( reader.getNodeName() ) ) {
            Map<String, BigDecimal> rhos = new HashMap<String, BigDecimal>();

            rhos = (Map<String, BigDecimal>) context.convertAnother( rhos, Map.class );

            detritalUraniumAndThoriumModel.setRhosVarUnct( rhos );
        }
        reader.moveUp();

        return detritalUraniumAndThoriumModel;
    }
}
