/*
 * AnalysisFractionXMLConverter.java
 *
 * Created on August 7, 2007, 7:51 AM
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
package org.earthtime.UPb_Redux.fractions;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.initialPbModels.InitialPbModel;
import org.earthtime.UPb_Redux.initialPbModels.InitialPbModelXMLConverter;
import org.earthtime.UPb_Redux.valueModels.MeasuredRatioModel;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.ratioDataModels.AbstractRatiosDataModel;
import org.earthtime.ratioDataModels.initialPbModelsET.InitialPbModelET;
import org.earthtime.ratioDataModels.initialPbModelsET.InitialPbModelETXMLConverter;

/**
 *
 * @author James F. Bowring
 */
public class AnalysisFractionXMLConverter implements Converter {

    /**
     *
     * @param clazz
     * @return
     */
    @Override
    public boolean canConvert ( Class clazz ) {
        return clazz.equals( AnalysisFraction.class );
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

        AnalysisFraction analysisFraction = (AnalysisFraction) value;

        // march 2012 to get fraction-specific values
        analysisFraction.calculateStaceyKramersInitialPbModelValues();

        // added oct 2010
        writer.startNode( "isLegacy" );
        writer.setValue( Boolean.toString( analysisFraction.isLegacy() ) );
        writer.endNode();

        writer.startNode( "sampleName" );
        writer.setValue( analysisFraction.getSampleName() );
        writer.endNode();

        writer.startNode( "fractionID" );
        writer.setValue( analysisFraction.getFractionID() );
        writer.endNode();

        writer.startNode( "grainID" );
        writer.setValue( analysisFraction.getGrainID() );
        writer.endNode();

        writer.startNode( "zircon" );
        writer.setValue( Boolean.toString( analysisFraction.isZircon() ) );
        writer.endNode();

        writer.startNode( "imageURL" );
        writer.addAttribute( "URL", analysisFraction.getImageURL() );
        writer.endNode();

        writer.startNode( "timeStamp" );
        writer.setValue( DateFormat.getDateTimeInstance( DateFormat.LONG, DateFormat.SHORT ).format(
                analysisFraction.getTimeStamp() ) );
        writer.endNode();

        writer.startNode( "mineralName" );
        writer.setValue( analysisFraction.getMineralName() );
        writer.endNode();

        writer.startNode( "settingType" );
        writer.setValue( analysisFraction.getSettingType() );
        writer.endNode();

        writer.startNode( "numberOfGrains" );
        writer.setValue( Integer.toString( analysisFraction.getNumberOfGrains() ) );
        writer.endNode();

        writer.startNode( "estimatedDate" );
        writer.setValue( analysisFraction.getEstimatedDate().toPlainString() );
        writer.endNode();

        // added in oct 2010
        writer.startNode( "staceyKramersOnePctUnct" );
        writer.setValue( analysisFraction.getStaceyKramersOnePctUnct().toPlainString() );
        writer.endNode();

        writer.startNode( "staceyKramersCorrelationCoeffs" );
        writer.setValue( analysisFraction.getStaceyKramersCorrelationCoeffs().toPlainString() );
        writer.endNode();


        writer.startNode( "physicallyAbraded" );
        writer.setValue( Boolean.toString( analysisFraction.isPhysicallyAbraded() ) );
        writer.endNode();

        writer.startNode( "leachedInHFAcid" );
        writer.setValue( Boolean.toString( analysisFraction.isLeachedInHFAcid() ) );
        writer.endNode();

        writer.startNode( "annealedAndChemicallyAbraded" );
        writer.setValue( Boolean.toString( analysisFraction.isAnnealedAndChemicallyAbraded() ) );
        writer.endNode();

        writer.startNode( "chemicallyPurifiedUPb" );
        writer.setValue( Boolean.toString( analysisFraction.isChemicallyPurifiedUPb() ) );
        writer.endNode();

        writer.startNode( "analysisFractionComment" );
        writer.setValue( analysisFraction.getAnalysisFractionComment() );
        writer.endNode();

        writer.startNode( "pbBlankID" );
        writer.setValue( analysisFraction.getPbBlankID() );
        writer.endNode();

        writer.startNode( "tracerID" );
        writer.setValue( analysisFraction.getTracerID() );
        writer.endNode();

        writer.startNode( "fractionationCorrectedPb" );
        writer.setValue( Boolean.toString( analysisFraction.isFractionationCorrectedPb() ) );
        writer.endNode();

        writer.startNode( "alphaPbModelID" );
        writer.setValue( analysisFraction.getAlphaPbModelID() );
        writer.endNode();

        writer.startNode( "fractionationCorrectedU" );
        writer.setValue( Boolean.toString( analysisFraction.isFractionationCorrectedU() ) );
        writer.endNode();

        writer.startNode( "alphaUModelID" );
        writer.setValue( analysisFraction.getAlphaUModelID() );
        writer.endNode();

        if ( analysisFraction.getInitialPbModelForXMLSerialization() != null ) {
            writer.startNode( "initialPbModelET" );
            context.convertAnother( analysisFraction.getInitialPbModelForXMLSerialization(),//
                    new InitialPbModelETXMLConverter() );
            writer.endNode();
        }

        writer.startNode( "pbCollectorType" );
        writer.setValue( analysisFraction.getPbCollectorType() );
        writer.endNode();

        writer.startNode( "uCollectorType" );
        writer.setValue( analysisFraction.getUCollectorType() );
        writer.endNode();

        writer.startNode( "analysisMeasures" );
        context.convertAnother( ValueModel.compressArrayOfValueModels( analysisFraction.getAnalysisMeasures() ) );
        // context.convertAnother(analysisFraction.getAnalysisMeasures());
        writer.endNode();

        writer.startNode( "measuredRatios" );
        context.convertAnother( ValueModel.compressArrayOfValueModels( analysisFraction.getMeasuredRatios() ) );
        //context.convertAnother(analysisFraction.getMeasuredRatios());
        writer.endNode();

        writer.startNode( "radiogenicIsotopeRatios" );
        context.convertAnother( ValueModel.compressArrayOfValueModels( analysisFraction.getRadiogenicIsotopeRatios() ) );
        //context.convertAnother(analysisFraction.getRadiogenicIsotopeRatios());
        writer.endNode();

        writer.startNode( "radiogenicIsotopeDates" );
        context.convertAnother( ValueModel.compressArrayOfValueModels( analysisFraction.getIsotopeDates() ) );
        // context.convertAnother(analysisFraction.getRadiogenicIsotopeDates());
        writer.endNode();

        writer.startNode( "compositionalMeasures" );
        context.convertAnother( ValueModel.compressArrayOfValueModels( analysisFraction.getCompositionalMeasures() ) );
        // context.convertAnother(analysisFraction.getCompositionalMeasures());
        writer.endNode();

        writer.startNode( "sampleIsochronRatios" );
        context.convertAnother( ValueModel.compressArrayOfValueModels( analysisFraction.getSampleIsochronRatios() ) );
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

        AnalysisFraction analysisFraction = new AnalysisFraction();

        // added oct 2010 for backward compat
        reader.moveDown();
        if ( "isLegacy".equals( reader.getNodeName() ) ) {
            analysisFraction.setLegacy( (reader.getValue().equalsIgnoreCase( "true" )) ? true : false );
            reader.moveUp();

            reader.moveDown();
            analysisFraction.setSampleName( reader.getValue() );
            reader.moveUp();

        } else {
            analysisFraction.setLegacy( false );
            analysisFraction.setSampleName( reader.getValue() );
            reader.moveUp();
        }


        reader.moveDown();
        analysisFraction.setFractionID( reader.getValue() );
        reader.moveUp();

        // april 2010 modified to handle grainID backward compatible
        // refactored aug 2010
        reader.moveDown();
        if ( "grainID".equals( reader.getNodeName() ) ) {
            analysisFraction.setGrainID( reader.getValue() );
            reader.moveUp();

            reader.moveDown();
            analysisFraction.setZircon( (reader.getValue().equalsIgnoreCase( "true" )) ? true : false );
            reader.moveUp();
        } else {
            analysisFraction.setGrainID( analysisFraction.getFractionID() );
            analysisFraction.setZircon( (reader.getValue().equalsIgnoreCase( "true" )) ? true : false );
            reader.moveUp();
        }


        reader.moveDown();
        analysisFraction.setImageURL( reader.getAttribute( "URL" ) );
        reader.moveUp();

        reader.moveDown();
        try {
            analysisFraction.setTimeStamp(
                    DateFormat.getDateTimeInstance( DateFormat.LONG, DateFormat.SHORT ).parse( reader.getValue() ) );
        } catch (ParseException ex) {
        }
        reader.moveUp();

        reader.moveDown();
        analysisFraction.setMineralName( reader.getValue() );
        reader.moveUp();

        reader.moveDown();
        analysisFraction.setSettingType( reader.getValue() );
        reader.moveUp();

        reader.moveDown();
        analysisFraction.setNumberOfGrains( Integer.parseInt( reader.getValue() ) );
        reader.moveUp();

        reader.moveDown();
        analysisFraction.setEstimatedDate(new BigDecimal( reader.getValue(), ReduxConstants.mathContext15 ) );
        reader.moveUp();


        // added in oct 2010 for backward compatible
        reader.moveDown();
        if ( "staceyKramersOnePctUnct".equals( reader.getNodeName() ) ) {
            analysisFraction.setStaceyKramersOnePctUnct(new BigDecimal( reader.getValue(), ReduxConstants.mathContext15 ) );
            reader.moveUp();

            reader.moveDown();
            analysisFraction.setStaceyKramersCorrelationCoeffs(new BigDecimal( reader.getValue(), ReduxConstants.mathContext15 ) );
            reader.moveUp();

            reader.moveDown();
            analysisFraction.setPhysicallyAbraded( (reader.getValue().equalsIgnoreCase( "true" )) ? true : false );
            reader.moveUp();

        } else {
            analysisFraction.setStaceyKramersOnePctUnct( BigDecimal.ZERO );
            analysisFraction.setStaceyKramersCorrelationCoeffs( BigDecimal.ZERO );
            analysisFraction.setPhysicallyAbraded( (reader.getValue().equalsIgnoreCase( "true" )) ? true : false );
            reader.moveUp();
        }



        reader.moveDown();
        analysisFraction.setLeachedInHFAcid( (reader.getValue().equalsIgnoreCase( "true" )) ? true : false );
        reader.moveUp();

        reader.moveDown();
        analysisFraction.setAnnealedAndChemicallyAbraded( (reader.getValue().equalsIgnoreCase( "true" )) ? true : false );
        reader.moveUp();

        reader.moveDown();
        analysisFraction.setChemicallyPurifiedUPb( (reader.getValue().equalsIgnoreCase( "true" )) ? true : false );
        reader.moveUp();

        reader.moveDown();
        analysisFraction.setAnalysisFractionComment( reader.getValue() );
        reader.moveUp();

        reader.moveDown();
        analysisFraction.setPbBlankID( reader.getValue() );
        reader.moveUp();

        reader.moveDown();
        analysisFraction.setTracerID( reader.getValue() );
        reader.moveUp();

        // aug 2010 for backward compatible
        reader.moveDown();
        if ( "fractionationCorrectedPb".equals( reader.getNodeName() ) ) {
            analysisFraction.setFractionationCorrectedPb( (reader.getValue().equalsIgnoreCase( "true" )) ? true : false );
            reader.moveUp();

            reader.moveDown();
            analysisFraction.setAlphaPbModelID( reader.getValue() );
            reader.moveUp();
        } else {
            analysisFraction.setFractionationCorrectedPb( false );
            analysisFraction.setAlphaPbModelID( reader.getValue() );
            reader.moveUp();
        }

        // aug 2010 for backward compatible
        reader.moveDown();
        if ( "fractionationCorrectedU".equals( reader.getNodeName() ) ) {
            analysisFraction.setFractionationCorrectedU( (reader.getValue().equalsIgnoreCase( "true" )) ? true : false );
            reader.moveUp();

            reader.moveDown();
            analysisFraction.setAlphaUModelID( reader.getValue() );
            reader.moveUp();
        } else {
            analysisFraction.setFractionationCorrectedU( false );
            analysisFraction.setAlphaUModelID( reader.getValue() );
            reader.moveUp();
        }

        reader.moveDown();
        if ( "initialPbModelET".equals( reader.getNodeName() ) ) {
            AbstractRatiosDataModel initialPbModelET = InitialPbModelET.createNewInstance();
            initialPbModelET = (InitialPbModelET) context.convertAnother(//
                    initialPbModelET, InitialPbModelET.class, new InitialPbModelETXMLConverter() );
            analysisFraction.setInitialPbModel( initialPbModelET );

            reader.moveUp();
            reader.moveDown();

        } else if ( "initialPbModel".equals( reader.getNodeName() ) ) {
            // march 2012
            InitialPbModel initialPbModel = new InitialPbModel();
            initialPbModel = (InitialPbModel) context.convertAnother( //
                    initialPbModel, InitialPbModel.class, new InitialPbModelXMLConverter() );

            AbstractRatiosDataModel initialPbModelET;
            if ( analysisFraction.isZircon() ) {
                //  has no initial Pb
                initialPbModelET = InitialPbModelET.getNoneInstance();
            } else {
//                // trap old stacey-kramers
//                if ( initialPbModel.isCalculated() ) {
//                    initialPbModel.setCalculated( false );
//                    initialPbModel.setName( "CalculatedFor_" + analysisFraction.getSampleName() + "_" + analysisFraction.getFractionID() );
//                }

                initialPbModelET = InitialPbModel.convertModel( initialPbModel );
            }
            
            analysisFraction.setInitialPbModel( initialPbModelET );

            reader.moveUp();
            reader.moveDown();
        } else {
            analysisFraction.setInitialPbModel( InitialPbModelET.getNoneInstance() );
//            reader.moveUp();
//            reader.moveDown();
        }

        //reader.moveDown();
        analysisFraction.setPbCollectorType( reader.getValue() );
        reader.moveUp();

        reader.moveDown();
        analysisFraction.setUCollectorType( reader.getValue() );
        reader.moveUp();

        reader.moveDown();
        if ( "analysisMeasures".equals( reader.getNodeName() ) ) {
            ArrayList<ValueModel> ratios = new ArrayList<ValueModel>();
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                ValueModel item = new ValueModel();
                item = (ValueModel) context.convertAnother( item, ValueModel.class );
                ratios.add( item );
                reader.moveUp();
            }
            // Convert to array
            ValueModel[] analysisMeasures = new ValueModel[ratios.size()];
            for (int i = 0; i < ratios.size(); i ++) {
                analysisMeasures[i] = ratios.get( i );
            }
            analysisFraction.setAnalysisMeasures( analysisMeasures );
        }
        reader.moveUp();


        reader.moveDown();
        if ( "measuredRatios".equals( reader.getNodeName() ) ) {
            ArrayList<MeasuredRatioModel> ratios = new ArrayList<MeasuredRatioModel>();
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                MeasuredRatioModel item = new MeasuredRatioModel();
                item = (MeasuredRatioModel) context.convertAnother( item, MeasuredRatioModel.class );
                ratios.add( item );
                reader.moveUp();
            }
            // Convert to array
            MeasuredRatioModel[] measuredRatios = new MeasuredRatioModel[ratios.size()];
            for (int i = 0; i < ratios.size(); i ++) {
                measuredRatios[i] = ratios.get( i );
            }
            analysisFraction.setMeasuredRatios( measuredRatios );
        }
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
            analysisFraction.setRadiogenicIsotopeRatios( radiogenicIsotopeRatios );
        }
        reader.moveUp();

        reader.moveDown();
        if ( "radiogenicIsotopeDates".equals( reader.getNodeName() ) ) {
            ArrayList<ValueModel> ages = new ArrayList<ValueModel>();
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                ValueModel item = new ValueModel();
                item = (ValueModel) context.convertAnother( item, ValueModel.class );
                ages.add( item );
                reader.moveUp();
            }
            // Convert to array
            ValueModel[] radiogenicIsotopeAges = new ValueModel[ages.size()];
            for (int i = 0; i < ages.size(); i ++) {
                radiogenicIsotopeAges[i] = ages.get( i );
            }
            analysisFraction.setIsotopeDates( radiogenicIsotopeAges );
        }
        reader.moveUp();


        reader.moveDown();
        if ( "compositionalMeasures".equals( reader.getNodeName() ) ) {
            ArrayList<ValueModel> values = new ArrayList<ValueModel>();
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                ValueModel item = new ValueModel();
                item = (ValueModel) context.convertAnother( item, ValueModel.class );
                values.add( item );
                reader.moveUp();
            }
            // Convert to array
            ValueModel[] compositionalMeasures = new ValueModel[values.size()];
            for (int i = 0; i < values.size(); i ++) {
                compositionalMeasures[i] = values.get( i );
            }
            analysisFraction.setCompositionalMeasures( compositionalMeasures );
        }
        reader.moveUp();

        reader.moveDown();
        if ( "sampleIsochronRatios".equals( reader.getNodeName() ) ) {
            ArrayList<ValueModel> values = new ArrayList<ValueModel>();
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                ValueModel item = new ValueModel();
                item = (ValueModel) context.convertAnother( item, ValueModel.class );
                values.add( item );
                reader.moveUp();
            }
            // Convert to array
            ValueModel[] sampleIsochronRatios = new ValueModel[values.size()];
            for (int i = 0; i < values.size(); i ++) {
                sampleIsochronRatios[i] = values.get( i );
            }
            analysisFraction.setSampleIsochronRatios( sampleIsochronRatios );
        }
        reader.moveUp();

        return analysisFraction;
    }
}
