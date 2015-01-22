/*
 * InitialPbModelETXMLConverter.java
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
package org.earthtime.ratioDataModels.initialPbModelsET;

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
 *
 * @author James F. Bowring
 */
public class InitialPbModelETXMLConverter implements Converter {

    /**
     *
     * @param clazz
     * @return
     */
    @Override
    public boolean canConvert ( Class clazz ) {
        return clazz.equals( InitialPbModelET.class );
    }

    /**
     *
     * @param value
     * @param writer
     * @param context
     */
    @Override
    public void marshal ( Object value, HierarchicalStreamWriter writer,
            MarshallingContext context ) {

        AbstractRatiosDataModel initialPbModelET = (InitialPbModelET) value;

        writer.startNode( "modelName" );
        writer.setValue( initialPbModelET.getModelName() );
        writer.endNode();

        writer.startNode( "versionNumber" );
        writer.setValue( Integer.toString( initialPbModelET.getVersionNumber() ) );
        writer.endNode();

        writer.startNode( "minorVersionNumber" );
        writer.setValue( Integer.toString( initialPbModelET.getMinorVersionNumber() ) );
        writer.endNode();

        writer.startNode( "labName" );
        writer.setValue( initialPbModelET.getLabName() );
        writer.endNode();

        writer.startNode( "dateCertified" );
        writer.setValue( initialPbModelET.getDateCertified() );
        writer.endNode();

        writer.startNode( "reference" );
        writer.setValue( initialPbModelET.getReference() );
        writer.endNode();

        writer.startNode( "comment" );
        writer.setValue( initialPbModelET.getComment() );
        writer.endNode();

        writer.startNode( "ratios" );
        context.convertAnother( initialPbModelET.getData() );
        writer.endNode();

        writer.startNode( "rhos" );
        context.convertAnother( initialPbModelET.getRhosVarUnctForXMLSerialization() );
        writer.endNode();

    }

    /**
     *
     * @param reader
     * @param context
     * @return
     */
    @Override
    public Object unmarshal ( HierarchicalStreamReader reader,
            UnmarshallingContext context ) {

        AbstractRatiosDataModel initialPbModelET = InitialPbModelET.createNewInstance();

        reader.moveDown();
        initialPbModelET.setModelName( reader.getValue() );
        reader.moveUp();

        reader.moveDown();
        initialPbModelET.setVersionNumber( Integer.parseInt( reader.getValue() ) );
        reader.moveUp();

        reader.moveDown();
        if ( "minorVersionNumber".equals( reader.getNodeName() ) ) {
            initialPbModelET.setMinorVersionNumber( Integer.valueOf( reader.getValue() ) );
            reader.moveUp();
            reader.moveDown();
        } else {
            initialPbModelET.setMinorVersionNumber( 0 );
        }

        initialPbModelET.setLabName( reader.getValue() );
        reader.moveUp();

        reader.moveDown();
        initialPbModelET.setDateCertified( reader.getValue() );
        reader.moveUp();

        reader.moveDown();
        initialPbModelET.setReference( reader.getValue() );
        reader.moveUp();

        reader.moveDown();
        initialPbModelET.setComment( reader.getValue() );
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

            initialPbModelET.setRatios( arrayRatios );
        }
        reader.moveUp();

        reader.moveDown();
        if ( "rhos".equals( reader.getNodeName() ) ) {
            Map<String, BigDecimal> rhos = new HashMap<String, BigDecimal>();

            rhos = (Map<String, BigDecimal>) context.convertAnother( rhos, Map.class );

            initialPbModelET.setRhosVarUnct( rhos );
        }
        reader.moveUp();

        return initialPbModelET;
    }
}