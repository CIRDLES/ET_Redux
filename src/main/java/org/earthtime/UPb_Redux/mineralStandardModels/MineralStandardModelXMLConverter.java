/*
 * MineralStandardModelXMLConverter.java
 *
 * Created on August 7, 2007, 9:02 AM
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

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.util.ArrayList;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.UPb_Redux.valueModels.ValueModelReferenced;

/**
 *
 * @author James F. Bowring
 */
public class MineralStandardModelXMLConverter implements Converter {

    /**
     * 
     * @param clazz
     * @return
     */
    @Override
    public boolean canConvert ( Class clazz ) {
        return clazz.equals( MineralStandardModel.class );
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

        MineralStandardModel mineralStandard = (MineralStandardModel) value;

        writer.startNode( "name" );
        writer.setValue( mineralStandard.getName() );
        writer.endNode();

        writer.startNode( "mineralStandardName" );
        writer.setValue( mineralStandard.getMineralStandardName() );
        writer.endNode();

        writer.startNode( "standardMineralName" );
        writer.setValue( mineralStandard.getStandardMineralName() );
        writer.endNode();

        writer.startNode( "trueAge" );
        context.convertAnother( mineralStandard.getTrueAge() );
        writer.endNode();

        writer.startNode( "radiogenicIsotopeRatios" );
        context.convertAnother( ValueModel.compressArrayOfValueModels( mineralStandard.getRadiogenicIsotopeRatios() ) );
        writer.endNode();

        writer.startNode( "measuredAge" );
        context.convertAnother( mineralStandard.getMeasuredAge() );
        writer.endNode();

        writer.startNode( "comment" );
        writer.setValue( mineralStandard.getComment() );
        writer.endNode();

    }

    /**
     * 
     * @param reader
     * @param context
     * @return
     */
    public Object unmarshal ( HierarchicalStreamReader reader,
            UnmarshallingContext context ) {

        MineralStandardModel mineralStandard = new MineralStandardModel();

        reader.moveDown();
        mineralStandard.setName( reader.getValue() );
        reader.moveUp();

        reader.moveDown();
        mineralStandard.setMineralStandardName( reader.getValue() );
        reader.moveUp();

        reader.moveDown();
        mineralStandard.setStandardMineralName( reader.getValue() );
        reader.moveUp();

        reader.moveDown();
        mineralStandard.setTrueAge(
                (ValueModelReferenced) context.convertAnother(
                new ValueModelReferenced(), ValueModelReferenced.class ) );
        reader.moveUp();

        reader.moveDown();
        if ( "radiogenicIsotopeRatios".equals( reader.getNodeName() ) ) {
            ArrayList<ValueModel> ratios = new ArrayList<ValueModel>();
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                ValueModel item = new ValueModel();
                item = (ValueModel) context.convertAnother( item, ValueModel.class );
                ratios.add( item );
                reader.moveUp();
            }
            // Convert to array
            ValueModel[] radiogenicIsotopeRatios = new ValueModel[ratios.size()];
            for (int i = 0; i < ratios.size(); i ++) {
                radiogenicIsotopeRatios[i] = ratios.get( i );
            }
            mineralStandard.setRadiogenicIsotopeRatios( radiogenicIsotopeRatios );

            // only exercised if found radiogenicIsotopeRatios, added to schema 8 Oct 2011
            reader.moveUp();

            reader.moveDown();

        }
        
        mineralStandard.setMeasuredAge(
                (ValueModelReferenced) context.convertAnother(
                new ValueModelReferenced(), ValueModelReferenced.class ) );
        reader.moveUp();

        reader.moveDown();
        mineralStandard.setComment( reader.getValue() );
        reader.moveUp();

        return mineralStandard;
    }
}
