/*
 * UPbReduxAliquotXMLConverter.java
 *
 * Created on January 13, 2008, 9:26 AM
 *
 *
 * Copyright 2006-2017 James F. Bowring and www.Earth-Time.org
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
package org.earthtime.UTh_Redux.aliquots;

import org.earthtime.UPb_Redux.aliquots.*;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.math.BigDecimal;
import java.util.Vector;
import org.earthtime.UPb_Redux.ReduxConstants.ANALYSIS_PURPOSE;
import org.earthtime.UPb_Redux.fractions.AnalysisFraction;
import org.earthtime.UPb_Redux.fractions.FractionI;
import org.earthtime.UPb_Redux.mineralStandardModels.MineralStandardModel;
import org.earthtime.UPb_Redux.mineralStandardModels.MineralStandardModelXMLConverter;
import org.earthtime.UPb_Redux.pbBlanks.PbBlank;
import org.earthtime.UPb_Redux.pbBlanks.PbBlankXMLConverter;
import org.earthtime.UPb_Redux.tracers.Tracer;
import org.earthtime.UPb_Redux.tracers.TracerXMLConverter;
import org.earthtime.UPb_Redux.valueModels.SampleDateInterceptModel;
import org.earthtime.UPb_Redux.valueModels.SampleDateModel;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.physicalConstants.PhysicalConstants;
import org.earthtime.physicalConstants.PhysicalConstantsXMLConverter;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataModels.mineralStandardModels.MineralStandardUPbModel;
import org.earthtime.ratioDataModels.mineralStandardModels.MineralStandardUPbModelXMLConverter;
import org.earthtime.ratioDataModels.pbBlankICModels.PbBlankICModel;
import org.earthtime.ratioDataModels.physicalConstantsModels.PhysicalConstantsModel;
import org.earthtime.ratioDataModels.tracers.TracerUPbModel;

/**
 *
 * @author James F. Bowring
 */
public class UThReduxAliquotXMLConverter implements Converter {

    /**
     *
     * @param clazz
     * @return
     */
    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(UThReduxAliquot.class);
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
        UThReduxAliquot uThReduxAliquot = (UThReduxAliquot) value;

        writer.startNode("keyWordsCSV");
        context.convertAnother(uThReduxAliquot.getKeyWordsCSV());
        writer.endNode();

        writer.startNode("mySESARSampleMetadata");
////        context.convertAnother(((UThReduxAliquot) uThReduxAliquot).getMySESARSampleMetadata());
        writer.endNode();

        writer.startNode("analysisPurpose");
        writer.setValue(uThReduxAliquot.getAnalysisPurpose().toString());
        writer.endNode();

        writer.startNode("sampleIGSN");
        writer.setValue(uThReduxAliquot.getSampleIGSN());
        writer.endNode();

        writer.startNode("aliquotIGSN");
        writer.setValue(uThReduxAliquot.getAliquotIGSN());
        writer.endNode();

        writer.startNode("aliquotName");
        writer.setValue(uThReduxAliquot.getAliquotName());
        writer.endNode();

        writer.startNode("laboratoryName");
        writer.setValue(uThReduxAliquot.getLaboratoryName());
        writer.endNode();

        writer.startNode("analystName");
        writer.setValue(uThReduxAliquot.getAnalystName());
        writer.endNode();

        writer.startNode("aliquotComment");
        writer.setValue(uThReduxAliquot.getAliquotComment());
        writer.endNode();

        writer.startNode("aliquotReference");
        writer.setValue(uThReduxAliquot.getAliquotReference());
        writer.endNode();

        writer.startNode("aliquotInstrumentalMethod");
        writer.setValue(uThReduxAliquot.getAliquotInstrumentalMethod());
        writer.endNode();

        writer.startNode("aliquotInstrumentalMethodReference");
        writer.setValue(uThReduxAliquot.getAliquotInstrumentalMethodReference());
        writer.endNode();

        writer.startNode("mineralStandardModels");
        context.convertAnother(uThReduxAliquot.getMineralStandardModels());
        writer.endNode();

        writer.startNode("sampleDateModels");
        context.convertAnother(uThReduxAliquot.legalizeSampleDateModels());
        writer.endNode();

        writer.startNode("analysisFractions");
        context.convertAnother(uThReduxAliquot.getAnalysisFractions());
        writer.endNode();

        writer.startNode("physicalConstantsModel");
        context.convertAnother(uThReduxAliquot.getPhysicalConstantsModel());
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
        UThReduxAliquot uThReduxAliquot = new UThReduxAliquot();

        reader.moveDown();

        if ("keyWordsCSV".equalsIgnoreCase(reader.getNodeName())) {
            uThReduxAliquot.setKeyWordsCSV(reader.getValue());
            reader.moveUp();

            reader.moveDown();
        } else {
            uThReduxAliquot.setKeyWordsCSV("");
        }

        if ("mySESARSampleMetadata".equalsIgnoreCase(reader.getNodeName())) {
            // do nothing for now Oct 2010
            reader.moveUp();
            reader.moveDown();
        }

        if ("analysisPurpose".equalsIgnoreCase(reader.getNodeName())) {
            uThReduxAliquot.setAnalysisPurpose(ANALYSIS_PURPOSE.valueOf(reader.getValue()));
            reader.moveUp();

            reader.moveDown();
        } else {
            uThReduxAliquot.setAnalysisPurpose(ANALYSIS_PURPOSE.NONE);
        }

        uThReduxAliquot.setSampleIGSN(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        uThReduxAliquot.setAliquotIGSN(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        uThReduxAliquot.setAliquotName(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        uThReduxAliquot.setLaboratoryName(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        uThReduxAliquot.setAnalystName(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        uThReduxAliquot.setAliquotComment(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        uThReduxAliquot.setAliquotReference(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        uThReduxAliquot.setAliquotInstrumentalMethod(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        uThReduxAliquot.setAliquotInstrumentalMethodReference(reader.getValue());
        reader.moveUp();

        if ("mineralStandardModels".equals(reader.getNodeName())) {
            Vector<AbstractRatiosDataModel> mineralStandardModels = new Vector<>();
            while (reader.hasMoreChildren()) {
                reader.moveDown();

                if ("MineralStandardUPbModel".equals(reader.getNodeName())) {
                    AbstractRatiosDataModel mineralStandardUPbModel = MineralStandardUPbModel.createNewInstance();
                    mineralStandardUPbModel = (AbstractRatiosDataModel) context.convertAnother(//
                            mineralStandardUPbModel, MineralStandardUPbModel.class, new MineralStandardUPbModelXMLConverter());
                    mineralStandardModels.add(mineralStandardUPbModel);
                    reader.moveUp();

                    // pre may 2012
                } else if ("MineralStandardModel".equals(reader.getNodeName())) {
                    MineralStandardModel mineralStandardModel = new MineralStandardModel();
                    mineralStandardModel = (MineralStandardModel) context.convertAnother( //
                            mineralStandardModel, MineralStandardModel.class, new MineralStandardModelXMLConverter());

                    AbstractRatiosDataModel mineralStandardUPbModel = MineralStandardModel.convertModel(mineralStandardModel);
                    mineralStandardModels.add(mineralStandardUPbModel);
                    reader.moveUp();
                }

            }
            uThReduxAliquot.setMineralStandardModels(mineralStandardModels);

        }
        reader.moveUp();

        reader.moveDown();
        if ("sampleDateModels".equals(reader.getNodeName())) {
            Vector<ValueModel> sampleDates = new Vector<>();
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                SampleDateModel item = null;
                if ("SampleDateModel".equals(reader.getNodeName())) {
                    item = new SampleDateModel();
                    item = (SampleDateModel) context.convertAnother(item, SampleDateModel.class);
                } else {
                    // assume SampleDateInterceptModel
                    item = new SampleDateInterceptModel();
                    item = (SampleDateModel) context.convertAnother(item, SampleDateInterceptModel.class);
                }
                sampleDates.add(item);
                reader.moveUp();
            }

            uThReduxAliquot.setSampleDateModels(sampleDates);
        }
        reader.moveUp();

        reader.moveDown();
        if ("analysisFractions".equals(reader.getNodeName())) {
            Vector<FractionI> analysisFractions = new Vector<>();
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                FractionI item = new AnalysisFraction();
                item = (AnalysisFraction) context.convertAnother(item, AnalysisFraction.class);
                analysisFractions.add(item);
                reader.moveUp();
            }

//////            uThReduxAliquot.setAnalysisFractions(analysisFractions);
        }
        reader.moveUp();

        reader.moveDown();
        if ("physicalConstantsModel".equals(reader.getNodeName())) {
            AbstractRatiosDataModel physicalConstantsModel = PhysicalConstantsModel.createNewInstance();
            physicalConstantsModel = (AbstractRatiosDataModel) context.convertAnother(physicalConstantsModel, PhysicalConstantsModel.class);
            uThReduxAliquot.setPhysicalConstantsModel(physicalConstantsModel);
        } 
        reader.moveUp();

        return uThReduxAliquot;
    }
}
