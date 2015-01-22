/*
 * MineralStandardUPbModelXMLConverter.java
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
package org.earthtime.ratioDataModels.mineralStandardModels;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.earthtime.UPb_Redux.valueModels.MineralStandardUPbRatioModel;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataModels.initialPbModelsET.InitialPbModelET;
import org.earthtime.ratioDataModels.initialPbModelsET.InitialPbModelETXMLConverter;

/**
 *
 * @author James F. Bowring
 */
public class MineralStandardUPbModelXMLConverter implements Converter {

    /**
     *
     * @param clazz
     * @return
     */
    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(MineralStandardUPbModel.class);
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

        AbstractRatiosDataModel mineralStandardUPbModel = (MineralStandardUPbModel) value;

        writer.startNode("modelName");
        writer.setValue(mineralStandardUPbModel.getModelName());
        writer.endNode();

        writer.startNode("versionNumber");
        writer.setValue(Integer.toString(mineralStandardUPbModel.getVersionNumber()));
        writer.endNode();

        writer.startNode("minorVersionNumber");
        writer.setValue(Integer.toString(mineralStandardUPbModel.getMinorVersionNumber()));
        writer.endNode();

        writer.startNode("labName");
        writer.setValue(mineralStandardUPbModel.getLabName());
        writer.endNode();

        writer.startNode("dateCertified");
        writer.setValue(mineralStandardUPbModel.getDateCertified());
        writer.endNode();

        writer.startNode("reference");
        writer.setValue(mineralStandardUPbModel.getReference());
        writer.endNode();

        writer.startNode("comment");
        writer.setValue(mineralStandardUPbModel.getComment());
        writer.endNode();

        writer.startNode("mineralStandardName");
        writer.setValue(((MineralStandardUPbModel) mineralStandardUPbModel).getMineralStandardName());
        writer.endNode();

        writer.startNode("mineralName");
        writer.setValue(((MineralStandardUPbModel) mineralStandardUPbModel).getMineralName());
        writer.endNode();

        if (!((MineralStandardUPbModel) mineralStandardUPbModel).getInitialPbModelET().equals(MineralStandardUPbModel.getNoneInstance())) {
            writer.startNode("initialPbModelET");
            context.convertAnother(((MineralStandardUPbModel) mineralStandardUPbModel).getInitialPbModelET());
            writer.endNode();
        }

        writer.startNode("ratios");
        context.convertAnother(mineralStandardUPbModel.getData());
        writer.endNode();

        writer.startNode("rhos");
        context.convertAnother(mineralStandardUPbModel.getRhosVarUnctForXMLSerialization());
        writer.endNode();

        // dec 2014
        writer.startNode("concentrationsPPM");
        context.convertAnother(((MineralStandardUPbModel) mineralStandardUPbModel).getConcentrationsPPM());
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

        AbstractRatiosDataModel mineralStandardUPbModel = MineralStandardUPbModel.createNewInstance();

        reader.moveDown();
        mineralStandardUPbModel.setModelName(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        mineralStandardUPbModel.setVersionNumber(Integer.parseInt(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        if ("minorVersionNumber".equals(reader.getNodeName())) {
            mineralStandardUPbModel.setMinorVersionNumber(Integer.valueOf(reader.getValue()));
            reader.moveUp();
            reader.moveDown();
        } else {
            mineralStandardUPbModel.setMinorVersionNumber(0);
        }

        mineralStandardUPbModel.setLabName(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        mineralStandardUPbModel.setDateCertified(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        mineralStandardUPbModel.setReference(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        mineralStandardUPbModel.setComment(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        ((MineralStandardUPbModel) mineralStandardUPbModel).setMineralStandardName(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        ((MineralStandardUPbModel) mineralStandardUPbModel).setMineralName(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        if ("initialPbModelET".equals(reader.getNodeName())) {
            AbstractRatiosDataModel initialPbModelET = InitialPbModelET.createNewInstance();
            initialPbModelET = (InitialPbModelET) context.convertAnother(//
                    initialPbModelET, InitialPbModelET.class, new InitialPbModelETXMLConverter());
            ((MineralStandardUPbModel) mineralStandardUPbModel).setInitialPbModelET(initialPbModelET);

            reader.moveUp();
            reader.moveDown();
        }

        if ("ratios".equals(reader.getNodeName())) {
            ArrayList<ValueModel> ratios = new ArrayList<ValueModel>();
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                ValueModel item = new MineralStandardUPbRatioModel();
                item = (ValueModel) context.convertAnother(item, MineralStandardUPbRatioModel.class);
                ratios.add(item);
                reader.moveUp();
            }
            // Convert to array
            ValueModel[] arrayRatios = new MineralStandardUPbRatioModel[ratios.size()];
            for (int i = 0; i < ratios.size(); i++) {
                arrayRatios[i] = ratios.get(i);
            }

            mineralStandardUPbModel.setRatios(arrayRatios);
        }
        reader.moveUp();

        reader.moveDown();
        if ("rhos".equals(reader.getNodeName())) {
            Map<String, BigDecimal> rhos = new HashMap<>();

            rhos = (Map<String, BigDecimal>) context.convertAnother(rhos, Map.class);

            mineralStandardUPbModel.setRhosVarUnct(rhos);
        }
        reader.moveUp();

        // Dec 2014
        if (reader.hasMoreChildren()) {
            reader.moveDown();
            if ("concentrationsPPM".equals(reader.getNodeName())) {
                ArrayList<ValueModel> concentrationsPPM = new ArrayList<ValueModel>();
                while (reader.hasMoreChildren()) {
                    reader.moveDown();
                    ValueModel item = new ValueModel();
                    item = (ValueModel) context.convertAnother(item, ValueModel.class);
                    concentrationsPPM.add(item);
                    reader.moveUp();
                }
                // Convert to array
                ValueModel[] arrayConcentrationsPPM = new ValueModel[concentrationsPPM.size()];
                for (int i = 0; i < concentrationsPPM.size(); i++) {
                    arrayConcentrationsPPM[i] = concentrationsPPM.get(i);
                }

                ((MineralStandardUPbModel) mineralStandardUPbModel).setConcentrationsPPM(arrayConcentrationsPPM);
            }
            reader.moveUp();
        }

        return mineralStandardUPbModel;
    }
}
