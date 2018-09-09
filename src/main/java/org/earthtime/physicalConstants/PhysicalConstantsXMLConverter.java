/*
 * PhysicalConstantsXMLConverter.java
 *
 * Created on November 3, 2007, 2:10 PM
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

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.UPb_Redux.valueModels.ValueModelReferenced;

/**
 *
 * @author James F. Bowring
 */
public class PhysicalConstantsXMLConverter implements Converter {
    
    /**
     * 
     * @param clazz
     * @return
     */
    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(PhysicalConstants.class);
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
        
        PhysicalConstants physicalConstants = (PhysicalConstants) value;
        
        writer.startNode("name");
        writer.setValue(physicalConstants.getName());
        writer.endNode();
        
        writer.startNode("version");
        writer.setValue(Integer.toString(physicalConstants.getVersion()));
        writer.endNode();
        
        writer.startNode("atomicMolarMasses");
        context.convertAnother(physicalConstants.getAtomicMolarMasses());
        writer.endNode();
        
        writer.startNode("measuredConstants");
        context.convertAnother(physicalConstants.getMeasuredConstants());
        writer.endNode();
        
        writer.startNode("physicalConstantsComment");
        writer.setValue(physicalConstants.getPhysicalConstantsComment());
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
        
        PhysicalConstants physicalConstants = new PhysicalConstants();
        
        reader.moveDown();
        physicalConstants.setName(reader.getValue());
        reader.moveUp();
        
        reader.moveDown();
        physicalConstants.setVersion(Integer.parseInt(reader.getValue()));
        reader.moveUp();
        
        reader.moveDown();
        if ("atomicMolarMasses".equals(reader.getNodeName())){
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
            physicalConstants.setAtomicMolarMasses(arrayRatios);
        }
        reader.moveUp();
        
        reader.moveDown();
        if ("measuredConstants".equals(reader.getNodeName())){
            ArrayList<ValueModel> ratios = new ArrayList<ValueModel>();
            while(reader.hasMoreChildren()){
                reader.moveDown();
                ValueModel item = new ValueModelReferenced();
                item = (ValueModelReferenced)context.convertAnother(item, ValueModelReferenced.class);
                ratios.add(item);
                reader.moveUp();
            }
            // Convert to array
            ValueModel[] arrayRatios = new ValueModel[ratios.size()];
            for (int i = 0; i < ratios.size(); i ++){
                arrayRatios[i] = ratios.get(i);
            }
            Arrays.sort(arrayRatios);
            physicalConstants.setMeasuredConstants(arrayRatios);
        }
        reader.moveUp();
        
        reader.moveDown();
        physicalConstants.setPhysicalConstantsComment(reader.getValue());
        reader.moveUp();
        
//        reader.moveDown();
//        AbstractMatrixModel item = new CovarianceMatrixModel();
//        physicalConstants.setLambdasCovarianceMatrix(//
//                (AbstractMatrixModel) context.convertAnother(item, CovarianceMatrixModel.class));
//        reader.moveUp();
        
        return physicalConstants;
    }
    
}
