/*
 * AnalysisImageXMLConverter.java
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

package org.earthtime.archivingTools;

import org.earthtime.UPb_Redux.aliquots.AnalysisImage;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.earthtime.UPb_Redux.mineralStandardModels.MineralStandardModel;

/**
 *
 * @author James F. Bowring
 */
public class AnalysisImageXMLConverter implements Converter {
    
    /**
     * 
     * @param clazz
     * @return
     */
    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(AnalysisImage.class);
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
        
        AnalysisImage analysisImage = (AnalysisImage) value;
        
        writer.startNode("imageType");
        writer.setValue(analysisImage.getImageType().getName());
        writer.endNode();
        
        writer.startNode("imageURL");
        writer.setValue(analysisImage.getImageURL());
        writer.endNode();
    }
    
    /**
     * 
     * @param reader
     * @param context
     * @return
     */
    @Override
    public Object unmarshal(HierarchicalStreamReader reader,
            UnmarshallingContext context) {
        
        MineralStandardModel mineralStandard = new MineralStandardModel();
   
        return mineralStandard;
    }
    
}
