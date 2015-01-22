/*
 * InitialPbModelXMLConverter.java
 *
 * Created on October 14, 2007, 6:37 AM
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


package org.earthtime.UPb_Redux.initialPbModels;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import org.earthtime.UPb_Redux.valueModels.ValueModel;

/**
 *
 * @author James F. Bowring
 */
public class InitialPbModelXMLConverter implements Converter {
    
    /**
     * 
     * @param clazz
     * @return
     */
    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(InitialPbModel.class);
    }
    
    /**
     * 
     * @param value
     * @param writer
     * @param context
     */
    @Override
    public void marshal(Object value, HierarchicalStreamWriter writer,
            MarshallingContext context) {
        
        InitialPbModel initialPbModel = (InitialPbModel) value;
        
        writer.startNode("name");
        writer.setValue(initialPbModel.getName());
        writer.endNode();
        
        writer.startNode("reference");
        writer.setValue(initialPbModel.getReference());
        writer.endNode();
                
        writer.startNode("calculated");
        writer.setValue(Boolean.toString(initialPbModel.isCalculated()));
        writer.endNode();

        writer.startNode("ratios");
        context.convertAnother(initialPbModel.getRatios());
        writer.endNode();
        
        writer.startNode("correlationCoefficients");
        context.convertAnother(initialPbModel.getCorrelationCoefficients());
        writer.endNode();

    }
    
    /**
     * 
     * @param reader
     * @param context
     * @return
     */
    public Object unmarshal(HierarchicalStreamReader reader,
            UnmarshallingContext context) {
        
        InitialPbModel initialPbModel = new InitialPbModel();
        
        reader.moveDown();
        initialPbModel.setName(reader.getValue());
        reader.moveUp();
        
        reader.moveDown();
        initialPbModel.setReference(reader.getValue());
        reader.moveUp();
        
        reader.moveDown();
        initialPbModel.setCalculated( (reader.getValue().equalsIgnoreCase("true")) ? true : false);
        reader.moveUp();
        
        reader.moveDown();
        if ("ratios".equals(reader.getNodeName())){
            ArrayList<ValueModel> ratios = new ArrayList<ValueModel>();
            while(reader.hasMoreChildren()){
                reader.moveDown();
                ValueModel item = new ValueModel();
                item = (ValueModel)context.convertAnother(item, ValueModel.class);
                ratios.add(item);
                reader.moveUp();
            }
            // Convert to array
            ValueModel[] arrayRatios = new ValueModel[ratios.size()];
            for (int i = 0; i < ratios.size(); i ++){
                arrayRatios[i] = ratios.get(i);
            }
            Arrays.sort(arrayRatios);
            initialPbModel.setRatios(arrayRatios);
        }
        reader.moveUp();
        
        reader.moveDown();
        if ("correlationCoefficients".equals(reader.getNodeName())){
            ArrayList<ValueModel> rhos = new ArrayList<ValueModel>();
            while(reader.hasMoreChildren()){
                reader.moveDown();
                ValueModel item = new ValueModel();
                item = (ValueModel)context.convertAnother(item, ValueModel.class);
                rhos.add(item);
                reader.moveUp();
            }
            // Convert to array
            ValueModel[] arrayRatios = new ValueModel[rhos.size()];
            for (int i = 0; i < rhos.size(); i ++){
                arrayRatios[i] = rhos.get(i);
            }
            Arrays.sort(arrayRatios);
            initialPbModel.setCorrelationCoefficients(arrayRatios);
        }
        reader.moveUp();

        // march 2012 update
////        return InitialPbModel.convertModel( initialPbModel );
        return  initialPbModel ;
    }
    
}