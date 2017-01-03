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
package org.earthtime.UPb_Redux.aliquots;

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
public class UPbReduxAliquotXMLConverter implements Converter {

    /**
     *
     * @param clazz
     * @return
     */
    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(UPbReduxAliquot.class);
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
        Aliquot uPbReduxAliquot = (UPbReduxAliquot) value;

        writer.startNode("keyWordsCSV");
        context.convertAnother(uPbReduxAliquot.getKeyWordsCSV());
        writer.endNode();

        writer.startNode("mySESARSampleMetadata");
        context.convertAnother(((UPbReduxAliquot) uPbReduxAliquot).getMySESARSampleMetadata());
        writer.endNode();

        writer.startNode("analysisPurpose");
        writer.setValue(uPbReduxAliquot.getAnalysisPurpose().toString());
        writer.endNode();

        writer.startNode("sampleIGSN");
        writer.setValue(uPbReduxAliquot.getSampleIGSN());
        writer.endNode();

        writer.startNode("aliquotIGSN");
        writer.setValue(uPbReduxAliquot.getAliquotIGSN());
        writer.endNode();

        writer.startNode("aliquotName");
        writer.setValue(uPbReduxAliquot.getAliquotName());
        writer.endNode();

        writer.startNode("laboratoryName");
        writer.setValue(uPbReduxAliquot.getLaboratoryName());
        writer.endNode();

        writer.startNode("analystName");
        writer.setValue(uPbReduxAliquot.getAnalystName());
        writer.endNode();

        writer.startNode("aliquotComment");
        writer.setValue(uPbReduxAliquot.getAliquotComment());
        writer.endNode();

        writer.startNode("aliquotReference");
        writer.setValue(uPbReduxAliquot.getAliquotReference());
        writer.endNode();

        writer.startNode("aliquotInstrumentalMethod");
        writer.setValue(uPbReduxAliquot.getAliquotInstrumentalMethod());
        writer.endNode();

        writer.startNode("aliquotInstrumentalMethodReference");
        writer.setValue(uPbReduxAliquot.getAliquotInstrumentalMethodReference());
        writer.endNode();

        writer.startNode("calibrationUnct206-238");
        writer.setValue(uPbReduxAliquot.getCalibrationUnct206_238().toPlainString());
        writer.endNode();

        writer.startNode("calibrationUnct208-232");
        writer.setValue(uPbReduxAliquot.getCalibrationUnct208_232().toPlainString());
        writer.endNode();

        writer.startNode("calibrationUnct207-206");
        writer.setValue(uPbReduxAliquot.getCalibrationUnct207_206().toPlainString());
        writer.endNode();

        // nov 2010
        writer.startNode("analysisImages");
        context.convertAnother(((UPbReduxAliquot) uPbReduxAliquot).getAnalysisImages());
        writer.endNode();

        writer.startNode("mineralStandardModels");
        context.convertAnother(uPbReduxAliquot.getMineralStandardModels());
        writer.endNode();

        writer.startNode("sampleDateModels");
        context.convertAnother(uPbReduxAliquot.legalizeSampleDateModels());
        writer.endNode();

        writer.startNode("pbBlanks");
        context.convertAnother(uPbReduxAliquot.getPbBlanksForXMLSerialization());
        writer.endNode();

        writer.startNode("tracers");
        context.convertAnother(uPbReduxAliquot.getTracersForXMLSerialization());
        writer.endNode();

        writer.startNode("alphaPbModels");
        context.convertAnother(uPbReduxAliquot.getAlphaPbModelsForXMLSerialization());
        writer.endNode();

        writer.startNode("alphaUModels");
        context.convertAnother(uPbReduxAliquot.getAlphaUModelsForXMLSerialization());
        writer.endNode();

        writer.startNode("analysisFractions");
        context.convertAnother(uPbReduxAliquot.getAnalysisFractions());
        writer.endNode();

        writer.startNode("physicalConstantsModelII");
        context.convertAnother(uPbReduxAliquot.getPhysicalConstantsModel());
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
        Aliquot uPbReduxAliquot = new UPbReduxAliquot();

        reader.moveDown();

        if ("keyWordsCSV".equalsIgnoreCase(reader.getNodeName())) {
            uPbReduxAliquot.setKeyWordsCSV(reader.getValue());
            reader.moveUp();

            reader.moveDown();
        } else {
            uPbReduxAliquot.setKeyWordsCSV("");
        }

        if ("mySESARSampleMetadata".equalsIgnoreCase(reader.getNodeName())) {
            // do nothing for now Oct 2010
            reader.moveUp();
            reader.moveDown();
        }

        if ("analysisPurpose".equalsIgnoreCase(reader.getNodeName())) {
            uPbReduxAliquot.setAnalysisPurpose(ANALYSIS_PURPOSE.valueOf(reader.getValue()));
            reader.moveUp();

            reader.moveDown();
        } else {
            uPbReduxAliquot.setAnalysisPurpose(ANALYSIS_PURPOSE.NONE);
        }

        uPbReduxAliquot.setSampleIGSN(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        uPbReduxAliquot.setAliquotIGSN(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        uPbReduxAliquot.setAliquotName(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        uPbReduxAliquot.setLaboratoryName(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        uPbReduxAliquot.setAnalystName(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        uPbReduxAliquot.setAliquotComment(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        uPbReduxAliquot.setAliquotReference(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        uPbReduxAliquot.setAliquotInstrumentalMethod(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        uPbReduxAliquot.setAliquotInstrumentalMethodReference(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        uPbReduxAliquot.setCalibrationUnct206_238(new BigDecimal(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        uPbReduxAliquot.setCalibrationUnct208_232(new BigDecimal(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        uPbReduxAliquot.setCalibrationUnct207_206(new BigDecimal(reader.getValue()));
        reader.moveUp();

        // nov 2010
        reader.moveDown();
        if ("analysisImages".equalsIgnoreCase(reader.getNodeName())) {
            reader.moveUp(); /// ignore for now
            reader.moveDown();
        }

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
            uPbReduxAliquot.setMineralStandardModels(mineralStandardModels);

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

            uPbReduxAliquot.setSampleDateModels(sampleDates);
        }
        reader.moveUp();

        reader.moveDown();
        if ("pbBlanks".equals(reader.getNodeName())) {
            Vector<AbstractRatiosDataModel> pbBlankICModels = new Vector<AbstractRatiosDataModel>();
            while (reader.hasMoreChildren()) {
                reader.moveDown();

                if ("PbBlankICModel".equals(reader.getNodeName())) {
                    AbstractRatiosDataModel pbBlankICModel = PbBlankICModel.createNewInstance();
                    pbBlankICModel = (AbstractRatiosDataModel) context.convertAnother(pbBlankICModel, PbBlankICModel.class);
                    pbBlankICModels.add(pbBlankICModel);
                    reader.moveUp();

                    // pre may 2012
                } else if ("PbBlank".equals(reader.getNodeName())) {
                    PbBlank pbBlank = new PbBlank();
                    pbBlank = (PbBlank) context.convertAnother( //
                            pbBlank, PbBlank.class, new PbBlankXMLConverter());

                    AbstractRatiosDataModel pbBlankModel = PbBlank.convertModel(pbBlank);
                    pbBlankICModels.add(pbBlankModel);
                    reader.moveUp();
                }
            }

            uPbReduxAliquot.setPbBlanks(pbBlankICModels);
        }
        reader.moveUp();

        reader.moveDown();
        if ("tracers".equals(reader.getNodeName())) {
            Vector<AbstractRatiosDataModel> tracers = new Vector<AbstractRatiosDataModel>();
            while (reader.hasMoreChildren()) {
                reader.moveDown();

                if ("TracerUPbModel".equals(reader.getNodeName())) {
                    AbstractRatiosDataModel tracerUPbModel = TracerUPbModel.createNewInstance();
                    tracerUPbModel = (AbstractRatiosDataModel) context.convertAnother(tracerUPbModel, TracerUPbModel.class);
                    tracers.add(tracerUPbModel);
                    reader.moveUp();

                    // pre may 2012
                } else if ("Tracer".equals(reader.getNodeName())) {
                    Tracer tracer = new Tracer();
                    tracer = (Tracer) context.convertAnother( //
                            tracer, Tracer.class, new TracerXMLConverter());

                    AbstractRatiosDataModel tracerUPbModel = Tracer.convertModel(tracer);
                    tracers.add(tracerUPbModel);
                    reader.moveUp();
                }
            }

            uPbReduxAliquot.setTracers(tracers);
        }
        reader.moveUp();

        reader.moveDown();
        if ("alphaPbModels".equals(reader.getNodeName())) {
            Vector<ValueModel> alphaPbModels = new Vector<ValueModel>();
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                ValueModel item = new ValueModel();
                item = (ValueModel) context.convertAnother(item, ValueModel.class);
                alphaPbModels.add(item);
                reader.moveUp();
            }

            uPbReduxAliquot.setAlphaPbModels(alphaPbModels);
        }
        reader.moveUp();

        reader.moveDown();
        if ("alphaUModels".equals(reader.getNodeName())) {
            Vector<ValueModel> alphaUModels = new Vector<ValueModel>();
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                ValueModel item = new ValueModel();
                item = (ValueModel) context.convertAnother(item, ValueModel.class);
                alphaUModels.add(item);
                reader.moveUp();
            }

            uPbReduxAliquot.setAlphaUModels(alphaUModels);
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

            uPbReduxAliquot.setAnalysisFractions(analysisFractions);
        }
        reader.moveUp();

        reader.moveDown();
        if ("physicalConstantsModelII".equals(reader.getNodeName())) {
            AbstractRatiosDataModel physicalConstantsModel = PhysicalConstantsModel.createNewInstance();
            physicalConstantsModel = (AbstractRatiosDataModel) context.convertAnother(physicalConstantsModel, PhysicalConstantsModel.class);
            uPbReduxAliquot.setPhysicalConstantsModel(physicalConstantsModel);

            // pre may 2012
        } else if ("physicalConstantsModel".equals(reader.getNodeName())) {
            PhysicalConstants physicalConstants = new PhysicalConstants();
            physicalConstants = (PhysicalConstants) context.convertAnother(physicalConstants, PhysicalConstants.class, new PhysicalConstantsXMLConverter());

            AbstractRatiosDataModel physicalConstantsModel = physicalConstants.convertModel(physicalConstants);
            uPbReduxAliquot.setPhysicalConstantsModel(physicalConstantsModel);
        }
        reader.moveUp();

        return uPbReduxAliquot;
    }
}
