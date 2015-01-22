/*
 * UPbFractionXMLConverter.java
 *
 * Created on August 9, 2007, 6:38 AM
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
package org.earthtime.UPb_Redux.fractions.UPbReduxFractions;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import org.earthtime.UPb_Redux.exceptions.BadLabDataException;
import org.earthtime.UPb_Redux.reduxLabData.ReduxLabData;
import org.earthtime.UPb_Redux.tracers.Tracer;
import org.earthtime.UPb_Redux.tracers.TracerXMLConverter;
import org.earthtime.UPb_Redux.valueModels.MeasuredRatioModel;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.dataDictionaries.AnalysisMeasures;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataModels.tracers.TracerUPbModel;
import org.earthtime.ratioDataModels.tracers.TracerUPbModelXMLConverter;

/**
 *
 * @author James F. Bowring
 */
public class UPbFractionXMLConverter implements Converter {

    private UPbFraction uPbFraction;

    /**
     *
     * @param uPbFraction
     */
    public UPbFractionXMLConverter ( UPbFraction uPbFraction ) {
        this.uPbFraction = uPbFraction;
    }

    /**
     *
     * @param clazz
     * @return
     */
    @Override
    public boolean canConvert ( Class clazz ) {
        return clazz.equals( UPbFraction.class );
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

        uPbFraction = (UPbFraction) value;

        writer.startNode( "sampleName" );
        writer.setValue( uPbFraction.getSampleName() );
        writer.endNode();

        writer.startNode( "fractionID" );
        writer.setValue( uPbFraction.getFractionID() );
        writer.endNode();

        writer.startNode( "grainID" );
        writer.setValue( uPbFraction.getGrainID() );
        writer.endNode();

        writer.startNode( "ratioType" );
        writer.setValue( uPbFraction.getRatioType() );
        writer.endNode();

        writer.startNode( "pedigree" );
        writer.setValue( uPbFraction.getPedigree() );
        writer.endNode();

        writer.startNode( "measuredRatios" );
        // modified april 2010 to split "U" fractions from "Pb" fractions parts for LiveUpdate
        String ratioType = uPbFraction.getRatioType();
        ArrayList<ValueModel> filteredMeasuredRatios = new ArrayList<ValueModel>();
        if ( ratioType.equalsIgnoreCase( "U" ) ) {
            for (ValueModel vm : uPbFraction.getMeasuredRatios()) {
                if ( vm.getName().contains( "3" ) ) {
                    filteredMeasuredRatios.add( vm );
                }
            }
        } else if ( ratioType.equalsIgnoreCase( "Pb" ) ) {
            for (ValueModel vm : uPbFraction.getMeasuredRatios()) {
                if ( vm.getName().contains( "0" ) ) {
                    filteredMeasuredRatios.add( vm );
                }
            }
        } else {
            filteredMeasuredRatios.addAll( Arrays.asList( uPbFraction.getMeasuredRatios() ) );
        }
        // now convert arrayList to array
        ValueModel[] tempArray = new ValueModel[filteredMeasuredRatios.size()];
        for (int i = 0; i < filteredMeasuredRatios.size(); i ++) {
            tempArray[i] = filteredMeasuredRatios.get( i );
        }
        context.convertAnother( tempArray );
        //  context.convertAnother( uPbFraction.getMeasuredRatios() );
        writer.endNode();

        writer.startNode( "meanAlphaU" );
        writer.setValue( uPbFraction.getMeanAlphaU().toPlainString() );
        writer.endNode();

        writer.startNode( "meanAlphaPb" );
        writer.setValue( uPbFraction.getMeanAlphaPb().toPlainString() );
        writer.endNode();

        writer.startNode( "r18O16O" );
        writer.setValue( uPbFraction.getAnalysisMeasure( AnalysisMeasures.r18O_16O.getName() ).getValue().toPlainString() );
        writer.endNode();

        writer.startNode( "labUBlankMass" );
        writer.setValue( uPbFraction.getAnalysisMeasure( AnalysisMeasures.uBlankMassInGrams.getName() ).getValue().toPlainString() );
        writer.endNode();

        writer.startNode( "r238235b" );
        writer.setValue( uPbFraction.getAnalysisMeasure( AnalysisMeasures.r238_235b.getName() ).getValue().toPlainString() );
        writer.endNode();

        writer.startNode( "r238235s" );
        writer.setValue( uPbFraction.getAnalysisMeasure( AnalysisMeasures.r238_235s.getName() ).getValue().toPlainString() );
        writer.endNode();

        writer.startNode( "tracerMass" );
        writer.setValue( uPbFraction.getAnalysisMeasure( AnalysisMeasures.tracerMassInGrams.getName() ).getValue().toPlainString() );
        writer.endNode();


        // Tracer is optional field for UPbRedux xml files
        if ( uPbFraction.getTracer() != null ) {
            writer.startNode( "tracerUPbModel" );
            context.convertAnother( uPbFraction.getTracer(), new TracerUPbModelXMLConverter() );
            writer.endNode();
        }


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

        reader.moveDown();
        uPbFraction.setSampleName( reader.getValue() );
        reader.moveUp();

        reader.moveDown();
        uPbFraction.setFractionID( reader.getValue() );
        reader.moveUp();

        // april 2010 add in field grainID and need to trap older xml files
        reader.moveDown();
        String temp = reader.getValue();
        reader.moveUp();

        if ( temp.startsWith( "U" ) || temp.startsWith( "Pb" ) ) {
            uPbFraction.setGrainID( uPbFraction.getFractionID() );
            uPbFraction.setRatioType( temp );
        } else {
            uPbFraction.setGrainID( temp );
            reader.moveDown();
            uPbFraction.setRatioType( reader.getValue() );
            reader.moveUp();
        }



        reader.moveDown();
        uPbFraction.setPedigree( reader.getValue() );
        reader.moveUp();

        reader.moveDown();
        if ( "measuredRatios".equals( reader.getNodeName() ) ) {
            ArrayList<ValueModel> ratios = new ArrayList<ValueModel>();
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                ValueModel item = new MeasuredRatioModel();
                item = (MeasuredRatioModel) context.convertAnother( item, MeasuredRatioModel.class );
                ratios.add( item );
                reader.moveUp();
            }
            // Convert to array
            ValueModel[] measuredRatios = new ValueModel[ratios.size()];
            for (int i = 0; i < ratios.size(); i ++) {
                measuredRatios[i] = ratios.get( i );
            }
            uPbFraction.setMeasuredRatios( measuredRatios );
        }
        reader.moveUp();

        reader.moveDown();
        uPbFraction.setMeanAlphaU( new BigDecimal( reader.getValue() ) );
        reader.moveUp();

        reader.moveDown();
        uPbFraction.setMeanAlphaPb( new BigDecimal( reader.getValue() ) );
        reader.moveUp();

        reader.moveDown();
        uPbFraction.setR18O16O( new BigDecimal( reader.getValue() ) );
        reader.moveUp();

        reader.moveDown();
        uPbFraction.setLabUBlankMass( new BigDecimal( reader.getValue() ) );
        reader.moveUp();

        reader.moveDown();
        uPbFraction.setR238_235b( new BigDecimal( reader.getValue() ) );
        reader.moveUp();

        reader.moveDown();
        uPbFraction.setR238_235s( new BigDecimal( reader.getValue() ) );
        reader.moveUp();

        reader.moveDown();
        uPbFraction.setTracerMass( new BigDecimal( reader.getValue() ) );
        reader.moveUp();

        if ( reader.hasMoreChildren() ) {
            reader.moveDown();

            // may 2012 detect new or old style tracer
            if ( "tracerUPbModel".equals( reader.getNodeName() ) ) {
                AbstractRatiosDataModel tracerUPbModel = TracerUPbModel.createNewInstance();
                tracerUPbModel = (TracerUPbModel) context.convertAnother(//
                        tracerUPbModel, TracerUPbModel.class, new TracerUPbModelXMLConverter() );
                uPbFraction.setTracer( tracerUPbModel );

            } else {
                // old style where "tracer" is node name
                Tracer tracer = new Tracer();
                tracer = (Tracer) context.convertAnother( //
                        tracer, Tracer.class, new TracerXMLConverter() );

                AbstractRatiosDataModel tracerUPbModel = Tracer.convertModel( tracer );
                uPbFraction.setTracer( tracerUPbModel );
            }


            reader.moveUp();
        } else {
//            try {
//                uPbFraction.setTracer( ReduxLabData.getInstance().getDefaultLabTracer() );
//            } catch (BadLabDataException badLabDataException) {
                uPbFraction.setTracer( (TracerUPbModel.getNoneInstance()) );
//            }
        }

        return uPbFraction;
    }
}
