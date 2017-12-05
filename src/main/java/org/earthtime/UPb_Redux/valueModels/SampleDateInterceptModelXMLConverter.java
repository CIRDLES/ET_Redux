/*
 * SampleDateInterceptModelXMLConverter.java
 *
 * Created April 2009
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
package org.earthtime.UPb_Redux.valueModels;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.math.BigDecimal;
import java.util.Vector;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.dataDictionaries.DataDictionary;
import org.earthtime.dataDictionaries.SampleDateTypes;

/**
 * A <code>SampleDateModelXMLConverter</code> is used to marshal and unmarshal data
 * between <code>SampleDateInterceptModels</code> and XML files.
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
public class SampleDateInterceptModelXMLConverter implements Converter {

    /**
     * checks the argument <code>clazz</code> against <code>SampleDateModel</code>'s
     * <code>Class</code>. Used to ensure that the object about to be
     * marshalled/unmarshalled is of the correct type.
     * 
     * @pre     argument <code>clazz</code> is a valid <code>Class</code>
     * @post    <code>boolean</code> is returned comparing <code>clazz</code>
     *          against <code>SampleDateModel.class</code>
     * @param   clazz   <code>Class</code> of the <code>Object</code> you wish
     *                  to convert to/from XML
     * @return  <code>boolean</code> - <code>true</code> if <code>clazz</code> matches
     *          <code>SampleDateModel</code>'s <code>Class</code>; else <code>false</code>.
     */
    public boolean canConvert(Class clazz) {
        return clazz.equals(SampleDateInterceptModel.class);
    }

    /**
     * writes the argument <code>value</code> to the XML file specified through <code>writer</code>
     * 
     * @pre     <code>value</code> is a valid <code>SampleDateModel</code>, <code>
     *          writer</code> is a valid <code>HierarchicalStreamWriter</code>,
     *          and <code>context</code> is a valid <code>MarshallingContext</code>
     * @post    <code>value</code> is written to the XML file specified via <code>writer</code>
     * @param   value   <code>SampleDateModel</code> that you wish to write to a file
     * @param   writer  stream to write through
     * @param   context <code>MarshallingContext</code> used to store generic data
     */
    public void marshal(Object value, HierarchicalStreamWriter writer,
            MarshallingContext context) {

        ValueModel dateModel = (SampleDateInterceptModel) value;

        writer.startNode("name");
        writer.setValue(dateModel.getName());
        writer.endNode();

        writer.startNode("value");
        writer.setValue(dateModel.getValue().toPlainString());
        writer.endNode();

        writer.startNode("uncertaintyType");
        writer.setValue(dateModel.getUncertaintyType());
        writer.endNode();

        writer.startNode("oneSigma");
        writer.setValue(dateModel.getOneSigma().toPlainString());
        writer.endNode();

        writer.startNode("meanSquaredWeightedDeviation");
        writer.setValue(((SampleDateInterceptModel) dateModel).getMeanSquaredWeightedDeviation().toPlainString());
        writer.endNode();

        writer.startNode("plusInternalTwoSigmaUnct");
        writer.setValue(((SampleDateInterceptModel) dateModel).getPlusInternalTwoSigmaUnct().toPlainString());
        writer.endNode();

        writer.startNode("minusInternalTwoSigmaUnct");
        writer.setValue(((SampleDateInterceptModel) dateModel).getMinusInternalTwoSigmaUnct().toPlainString());
        writer.endNode();

        writer.startNode("plusInternalTwoSigmaUnctWithTracerCalibrationUnct");
        writer.setValue(((SampleDateInterceptModel) dateModel).getPlusInternalTwoSigmaUnctWithTracerCalibrationUnct().toPlainString());
        writer.endNode();

        writer.startNode("minusInternalTwoSigmaUnctWithTracerCalibrationUnct");
        writer.setValue(((SampleDateInterceptModel) dateModel).getMinusInternalTwoSigmaUnctWithTracerCalibrationUnct().toPlainString());
        writer.endNode();

        writer.startNode("plusInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct");
        writer.setValue(((SampleDateInterceptModel) dateModel).getPlusInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct().toPlainString());
        writer.endNode();

        writer.startNode("minusInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct");
        writer.setValue(((SampleDateInterceptModel) dateModel).getMinusInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct().toPlainString());
        writer.endNode();

        writer.startNode("includedFractionsVector");
        Vector<String> includedFractions = ((SampleDateInterceptModel) dateModel).getIncludedFractionIDsVector();
        for (String id : includedFractions) {
            writer.startNode("fractionID");
            writer.setValue(id);
            writer.endNode();
        }
        writer.endNode();

        writer.startNode("explanation");
        writer.setValue(((SampleDateInterceptModel) dateModel).getExplanation());
        writer.endNode();

        writer.startNode("comment");
        writer.setValue(((SampleDateInterceptModel) dateModel).getComment());
        writer.endNode();

        writer.startNode("preferred");
        writer.setValue(Boolean.toString(((SampleDateInterceptModel) dateModel).isPreferred()));
        writer.endNode();

    }

    /**
     * reads a <code>SampleDateModel</code> from the XML file specified through <code>reader</code>
     * 
     * @pre     <code>reader</code> leads to a valid <code>SampleDateModel</code>
     * @post    the <code>SampleDateModel</code> is read from the XML file and returned
     * @param   reader  stream to read through
     * @param   context <code>UnmarshallingContext</code> used to store generic data
     * @return  <code>SampleDateModel</code> - <code>SampleDateModel</code> read from file
     *          specified by <code>reader</code>
     */
    public Object unmarshal(HierarchicalStreamReader reader,
            UnmarshallingContext context) {

        ValueModel dateModel = new SampleDateInterceptModel();

        reader.moveDown();
        dateModel.setName(reader.getValue());
        reader.moveUp();

        // use name to look up datename and methodname
        for (int i = 0; i < SampleDateTypes.getSampleDateModelTypes().length; i++) {
            if (dateModel.getName().equalsIgnoreCase(SampleDateTypes.getSampleDateModelTypes()[i][0])) {
                ((SampleDateInterceptModel) dateModel).setMethodName(SampleDateTypes.getSampleDateModelTypes()[i][1]);
                ((SampleDateInterceptModel) dateModel).setDateName(SampleDateTypes.getSampleDateModelTypes()[i][2]);
                break;
            }
        }

        reader.moveDown();
        dateModel.setValue(new BigDecimal(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        dateModel.setUncertaintyType(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        dateModel.setOneSigma(new BigDecimal(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        ((SampleDateInterceptModel) dateModel).setMeanSquaredWeightedDeviation(new BigDecimal(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        ((SampleDateInterceptModel) dateModel).setPlusInternalTwoSigmaUnct(new BigDecimal(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        ((SampleDateInterceptModel) dateModel).setMinusInternalTwoSigmaUnct(new BigDecimal(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        ((SampleDateInterceptModel) dateModel).setPlusInternalTwoSigmaUnctWithTracerCalibrationUnct(new BigDecimal(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        ((SampleDateInterceptModel) dateModel).setMinusInternalTwoSigmaUnctWithTracerCalibrationUnct(new BigDecimal(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        ((SampleDateInterceptModel) dateModel).setPlusInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct(new BigDecimal(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        ((SampleDateInterceptModel) dateModel).setMinusInternalTwoSigmaUnctWithTracerCalibrationAndDecayConstantUnct(new BigDecimal(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        if ("includedFractionsVector".equals(reader.getNodeName())) {
            Vector<String> includedFractions = new Vector<String>();
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                String item = new String();
                item = (String) context.convertAnother(item, String.class);
                includedFractions.add(item);
                reader.moveUp();
            }

            ((SampleDateInterceptModel) dateModel).setIncludedFractionIDsVector(includedFractions);
        }
        reader.moveUp();

        reader.moveDown();
        ((SampleDateInterceptModel) dateModel).setExplanation(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        ((SampleDateInterceptModel) dateModel).setComment(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        ((SampleDateInterceptModel) dateModel).setPreferred((reader.getValue().equalsIgnoreCase("true")) ? true : false);
        reader.moveUp();


        return dateModel;
    }
}
