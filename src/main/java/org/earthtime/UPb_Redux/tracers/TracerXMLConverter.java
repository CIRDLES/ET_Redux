/*
 * TracerXMLConverter.java
 *
 * Created on August 9, 2007, 8:57 AM
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
package org.earthtime.UPb_Redux.tracers;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.dataDictionaries.TracerIsotopes;
import org.earthtime.dataDictionaries.TracerRatiosEnum;

/**
 * A <code>TracerXMLConverter</code> is used to marshal and unmarshal data
 * between <code>Tracers</code> and XML files.
 * 
 * @imports <a href=http://xstream.codehaus.org/javadoc/com/thoughtworks/xstream/converters/Converter.html>
 *          com.thoughtworks.xstream.converters.Converter</a>
 * @imports <a href=http://xstream.codehaus.org/javadoc/com/thoughtworks/xstream/converters/MarshallingContext.html>
 *          com.thoughtworks.xstream.converters.MarhsallingContext</a>
 * @imports <a href=http://xstream.codehaus.org/javadoc/com/thoughtworks/xstream/converters/UnmarshallingContext.html>
 *          com.thoughtworks.xstream.converters.UnmarshallingContext</a>
 * @imports <a href=http://xstream.codehaus.org/javadoc/com/thoughtworks/xstream/io/HierarchicalStreamReader.html>
 *          com.thoughtworks.xstream.io.HierachicalSreamReader</a>
 * @imports <a href=http://xstream.codehaus.org/javadoc/com/thoughtworks/xstream/io/HierarchicalStreamWriter.html>
 *          com.thoughtworks.xstream.io.HierarchicalStreamWriter</a>
 * @author James F. Bowring, javaDocs by Stan Gasque
 */
public class TracerXMLConverter implements Converter {

    /**
     * checks the argument <code>clazz</code> against <code>Tracer</code>'s
     * <code>Class</code>. Used to ensure that the object about to be
     * marshalled/unmarshalled is of the correct type.
     * 
     * @pre     argument <code>clazz</code> is a valid <code>Class</code>
     * @post    <code>boolean</code> is returned comparing <code>clazz</code>
     *          against <code>Tracer.class</code>
     * @param   clazz   <code>Class</code> of the <code>Object</code> you wish
     *                  to convert to/from XML
     * @return  <code>boolean</code> - <code>true</code> if <code>clazz</code> matches
     *          <code>Tracer</code>'s <code>Class</code>; else <code>false</code>.
     */
    public boolean canConvert ( Class clazz ) {
        return clazz.equals( Tracer.class );
    }

    /**
     * writes the argument <code>value</code> to the XML file specified through <code>writer</code>
     * 
     * @pre     <code>value</code> is a valid <code>Tracer</code>, <code>
     *          writer</code> is a valid <code>HierarchicalStreamWriter</code>,
     *          and <code>context</code> is a valid <code>MarshallingContext</code>
     * @post    <code>value</code> is written to the XML file specified via <code>writer</code>
     * @param   value   <code>Tracer</code> that you wish to write to a file
     * @param   writer  stream to write through
     * @param   context <code>MarshallingContext</code> used to store generic data
     */
    public void marshal ( Object value, HierarchicalStreamWriter writer,
            MarshallingContext context ) {

        Tracer tracer = (Tracer) value;

        writer.startNode( "tracerName" );
        writer.setValue( tracer.getTracerName() );
        writer.endNode();

        writer.startNode( "versionNumber" );
        writer.setValue( Integer.toString( tracer.getVersionNumber() ) );
        writer.endNode();

        writer.startNode( "tracerType" );
        writer.setValue( tracer.getTracerType() );
        writer.endNode();

        writer.startNode( "labName" );
        writer.setValue( tracer.getLabName() );
        writer.endNode();

        writer.startNode( "dateCertified" );
        writer.setValue( tracer.getDateCertified() );
        writer.endNode();

        // prepare ratios to be custom-sorted
        ValueModel[] temp = new ValueModel[TracerRatiosEnum.getNames().length];
        for (int i = 0; i < TracerRatiosEnum.getNames().length; i ++) {
            temp[i] = tracer.getRatioByName( TracerRatiosEnum.getNames()[i] );
        }

        writer.startNode( "ratios" );
        context.convertAnother( temp );
        writer.endNode();

        temp = new ValueModel[TracerIsotopes.getNames().length];
        for (int i = 0; i < TracerIsotopes.getNames().length; i ++) {
            temp[i] = tracer.getIsotopeConcByName( TracerIsotopes.getNames()[i] );
        }

        writer.startNode( "isotopeConcentrations" );
        context.convertAnother( temp );
        writer.endNode();
    }

    /**
     * reads a <code>Tracer</code> from the XML file specified through <code>reader</code>
     * 
     * @pre     <code>reader</code> leads to a valid <code>Tracer</code>
     * @post    the <code>Tracer</code> is read from the XML file and returned
     * @param   reader  stream to read through
     * @param   context <code>UnmarshallingContext</code> used to store generic data
     * @return  <code>Object</code> - <code>Tracer</code> read from file
     *          specified by <code>reader</code>
     */
    public Object unmarshal ( HierarchicalStreamReader reader,
            UnmarshallingContext context ) {

        Tracer tracer = new Tracer();

        reader.moveDown();
        tracer.setTracerName( reader.getValue() );
        reader.moveUp();

        reader.moveDown();
        tracer.setVersionNumber( Integer.parseInt( reader.getValue() ) );
        reader.moveUp();

        reader.moveDown();
        tracer.setTracerType( reader.getValue() );
        reader.moveUp();

        reader.moveDown();
        tracer.setLabName( reader.getValue() );
        reader.moveUp();

        reader.moveDown();
        tracer.setDateCertified( reader.getValue() );
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
            ValueModel[] tracerRatios = new ValueModel[ratios.size()];
            for (int i = 0; i < ratios.size(); i ++) {
                tracerRatios[i] = ratios.get( i );
            }
            Arrays.sort( tracerRatios );
            tracer.setRatios( tracerRatios );
        }
        reader.moveUp();

        reader.moveDown();
        if ( "isotopeConcentrations".equals( reader.getNodeName() ) ) {
            ArrayList<ValueModel> concentrations = new ArrayList<ValueModel>();
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                ValueModel item = new ValueModel();
                item = (ValueModel) context.convertAnother( item, ValueModel.class );
                concentrations.add( item );
                reader.moveUp();
            }
            // Convert to array
            ValueModel[] isotopeConcentrations = new ValueModel[concentrations.size()];
            for (int i = 0; i < concentrations.size(); i ++) {
                isotopeConcentrations[i] = concentrations.get( i );
            }
            Arrays.sort( isotopeConcentrations );
            tracer.setIsotopeConcentrations( isotopeConcentrations );
        }
        reader.moveUp();


        return tracer;
    }
}
