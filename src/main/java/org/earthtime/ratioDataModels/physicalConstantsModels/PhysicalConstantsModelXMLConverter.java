/*
 * PhysicalConstantsModelXMLConverter.java
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
package org.earthtime.ratioDataModels.physicalConstantsModels;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.UPb_Redux.valueModels.ValueModelReferenced;
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
public class PhysicalConstantsModelXMLConverter implements Converter {

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
        return clazz.equals( PhysicalConstantsModel.class );
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

        AbstractRatiosDataModel physicalConstantsModel = (PhysicalConstantsModel) value;

        writer.startNode( "modelName" );
        writer.setValue( physicalConstantsModel.getModelName() );
        writer.endNode();

        writer.startNode( "versionNumber" );
        writer.setValue( Integer.toString( physicalConstantsModel.getVersionNumber() ) );
        writer.endNode();

        writer.startNode( "minorVersionNumber" );
        writer.setValue( Integer.toString( physicalConstantsModel.getMinorVersionNumber() ) );
        writer.endNode();

        writer.startNode( "labName" );
        writer.setValue( physicalConstantsModel.getLabName() );
        writer.endNode();

        writer.startNode( "dateCertified" );
        writer.setValue( physicalConstantsModel.getDateCertified() );
        writer.endNode();

        writer.startNode( "reference" );
        writer.setValue( physicalConstantsModel.getReference() );
        writer.endNode();

        writer.startNode( "comment" );
        writer.setValue( physicalConstantsModel.getComment() );
        writer.endNode();

        writer.startNode( "ratios" );
        context.convertAnother( physicalConstantsModel.getData() );
        writer.endNode();

        writer.startNode( "rhos" );
        context.convertAnother( physicalConstantsModel.getRhosVarUnctForXMLSerialization() );
        writer.endNode();

        writer.startNode( "atomicMolarMasses" );
        context.convertAnother( ((PhysicalConstantsModel) physicalConstantsModel).getAtomicMolarMassesForXMLSerialization() );
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
    public Object unmarshal ( HierarchicalStreamReader reader,
            UnmarshallingContext context ) {

        AbstractRatiosDataModel physicalConstantsModel = PhysicalConstantsModel.createNewInstance();

        reader.moveDown();
        physicalConstantsModel.setModelName( reader.getValue() );
        reader.moveUp();

        reader.moveDown();
        physicalConstantsModel.setVersionNumber( Integer.parseInt( reader.getValue() ) );
        reader.moveUp();

        reader.moveDown();
        if ( "minorVersionNumber".equals( reader.getNodeName() ) ) {
            physicalConstantsModel.setMinorVersionNumber( Integer.valueOf( reader.getValue() ) );
            reader.moveUp();
            reader.moveDown();
        } else {
            physicalConstantsModel.setMinorVersionNumber( 0 );
        }

        physicalConstantsModel.setLabName( reader.getValue() );
        reader.moveUp();

        reader.moveDown();
        physicalConstantsModel.setDateCertified( reader.getValue() );
        reader.moveUp();

        reader.moveDown();
        physicalConstantsModel.setReference( reader.getValue() );
        reader.moveUp();

        reader.moveDown();
        physicalConstantsModel.setComment( reader.getValue() );
        reader.moveUp();

        reader.moveDown();
        if ( "ratios".equals( reader.getNodeName() ) ) {
            ArrayList<ValueModel> ratios = new ArrayList<ValueModel>();
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                ValueModel item = new ValueModelReferenced();
                item = (ValueModel) context.convertAnother( item, ValueModelReferenced.class );
                ratios.add( item );
                reader.moveUp();
            }
            // Convert to array
            ValueModel[] arrayRatios = new ValueModel[ratios.size()];
            for (int i = 0; i < ratios.size(); i ++) {
                arrayRatios[i] = ratios.get( i );
            }

            physicalConstantsModel.setRatios( arrayRatios );
        }
        reader.moveUp();

        reader.moveDown();
        if ( "rhos".equals( reader.getNodeName() ) ) {
            Map<String, BigDecimal> rhos = new HashMap<>();

            rhos = (Map<String, BigDecimal>) context.convertAnother( rhos, Map.class );

            physicalConstantsModel.setRhosVarUnct( rhos );
        }
        reader.moveUp();

        reader.moveDown();
        if ( "atomicMolarMasses".equals( reader.getNodeName() ) ) {
            Map<String, BigDecimal> atomicMolarMasses = new TreeMap<>();

            atomicMolarMasses = (Map<String, BigDecimal>) context.convertAnother( atomicMolarMasses, Map.class );

            ((PhysicalConstantsModel) physicalConstantsModel).setAtomicMolarMasses( atomicMolarMasses );
        }
        reader.moveUp();

        return physicalConstantsModel;
    }
}
