/*
 * SesarSampleXMLConverter.java
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
package org.earthtime.archivingTools.forSESAR;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import java.math.BigDecimal;
import org.earthtime.dataDictionaries.SESAR_MaterialTypesEnum;
import org.earthtime.dataDictionaries.SESAR_ObjectTypesEnum;

/**
 * A <code>SesarSampleXMLConverter</code> is used to marshal and unmarshal data
 * between <code>SesarSample</code> and XML files.
 *
 * @imports
 * <a href=http://xstream.codehaus.org/javadoc/com/thoughtworks/xstream/converters/Converter.html>
 * com.thoughtworks.xstream.converters.Converter</a>
 * @imports
 * <a href=http://xstream.codehaus.org/javadoc/com/thoughtworks/xstream/converters/MarshallingContext.html>
 * com.thoughtworks.xstream.converters.MarhsallingContext</a>
 * @imports
 * <a href=http://xstream.codehaus.org/javadoc/com/thoughtworks/xstream/converters/UnmarshallingContext.html>
 * com.thoughtworks.xstream.converters.UnmarshallingContext</a>
 * @imports
 * <a href=http://xstream.codehaus.org/javadoc/com/thoughtworks/xstream/io/HierarchicalStreamReader.html>
 * com.thoughtworks.xstream.io.HierachicalSreamReader</a>
 * @imports
 * <a href=http://xstream.codehaus.org/javadoc/com/thoughtworks/xstream/io/HierarchicalStreamWriter.html>
 * com.thoughtworks.xstream.io.HierarchicalStreamWriter</a>
 * @author James F. Bowring, javaDocs by Stan Gasque
 */
public class SesarSampleXMLConverter implements Converter {

    /**
     * checks the argument <code>clazz</code> against <code>SesarSample</code>'s
     * <code>Class</code>. Used to ensure that the object about to be
     * marshalled/unmarshalled is of the correct type.
     *
     * @pre argument <code>clazz</code> is a valid <code>Class</code>
     * @post    <code>boolean</code> is returned comparing <code>clazz</code>
     * against <code>ValueModel.class</code>
     * @param clazz   <code>Class</code> of the <code>Object</code> you wish to
     * convert to/from XML
     * @return  <code>boolean</code> - <code>true</code> if <code>clazz</code>
     * matches <code>SesarSample</code>'s <code>Class</code>; else
     * <code>false</code>.
     */
    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(SesarSample.class);
    }

    /**
     * writes the argument <code>value</code> to the XML file specified through
     * <code>writer</code>
     *
     * @pre     <code>value</code> is a valid <code>SesarSample</code>, <code>
     *          writer</code> is a valid <code>HierarchicalStreamWriter</code>, and
     * <code>context</code> is a valid <code>MarshallingContext</code>
     * @post    <code>value</code> is written to the XML file specified via
     * <code>writer</code>
     * @param value   <code>SesarSample</code> that you wish to write to a file
     * @param writer stream to write through
     * @param context <code>MarshallingContext</code> used to store generic data
     */
    @Override
    public void marshal(Object value, HierarchicalStreamWriter writer,
            MarshallingContext context) {

        SesarSample sesarSample = (SesarSample) value;

        // this order is for registering at SESAR
        writer.startNode("user_code");
        writer.setValue(sesarSample.getUser_code());
        writer.endNode();

        writer.startNode("sample_type");
        writer.setValue(sesarSample.getSampleType());
        writer.endNode();

        writer.startNode("name");
        writer.setValue(sesarSample.getName());
        writer.endNode();

        writer.startNode("material");
        writer.setValue(sesarSample.getMaterial());
        writer.endNode();

        writer.startNode("igsn");
        writer.setValue(sesarSample.getIGSN());
        writer.endNode();

        if (sesarSample.getParentIGSN().length() > 0) {
            writer.startNode("parent_igsn");
            writer.setValue(sesarSample.getParentIGSN());
            writer.endNode();
        }

        writer.startNode("latitude");
        writer.setValue(CoordinateSystemConversions.getDecimalCoordinateAsString(sesarSample.getLatitude()));
        writer.endNode();

        writer.startNode("longitude");
        writer.setValue(CoordinateSystemConversions.getDecimalCoordinateAsString(sesarSample.getLongitude()));
        writer.endNode();

    }

    /**
     * reads a <code>SesarSample</code> from the XML file specified through
     * <code>reader</code>
     *
     * @pre     <code>reader</code> leads to a valid <code>SesarSample</code>
     * @post the <code>SesarSample</code> is read from the XML file and returned
     * @param reader stream to read through
     * @param context <code>UnmarshallingContext</code> used to store generic
     * data
     * @return  <code>SesarSample</code> - <code>ValueModel</code> read from file
     * specified by <code>reader</code>
     */
    @Override
    public Object unmarshal(HierarchicalStreamReader reader,
            UnmarshallingContext context) {

        SesarSample sesarSample = new SesarSample();

        reader.moveDown();
        // bar code
        reader.moveUp();

        reader.moveDown();
        sesarSample.setUser_code(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        sesarSample.setIGSN(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        sesarSample.setName(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        // sample_other_names
        reader.moveUp();

        reader.moveDown();
        String sampleType = reader.getValue();
        try {
            sampleType = SESAR_ObjectTypesEnum.valueOf(sampleType.replace(" ", "")).getName();
        } catch (Exception e) {
            sampleType = "Other";
        }
        sesarSample.setSampleType(sampleType);
        reader.moveUp();

        reader.moveDown();
        sesarSample.setParentIGSN(cleanStringElement(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        String material = reader.getValue();
        try {
            material = SESAR_MaterialTypesEnum.valueOf(material.replace(" ", "")).getName();
        } catch (Exception e) {
            material = "Other";
        }
        sesarSample.setMaterial(material);
        reader.moveUp();

        skipElements(reader, 12);

        reader.moveDown();
        sesarSample.setLatitude(new BigDecimal(cleanNumberElement(reader.getValue())));
        reader.moveUp();

        reader.moveDown();
        sesarSample.setLongitude(new BigDecimal(cleanNumberElement(reader.getValue())));
        reader.moveUp();

        return sesarSample;
    }

    private void skipElements(HierarchicalStreamReader reader, int count) {
        for (int i = 0; i < count; i++) {
            reader.moveDown();
            reader.moveUp();
        }
    }

    private String cleanStringElement(String element) {
        String retval;
        if (element.trim().startsWith("Not Provided")) {
            retval = "";
        } else {
            retval = element.trim();
        }

        return retval;
    }

    private String cleanNumberElement(String element) {
        String retval;
        if (element.trim().startsWith("Not Provided")) {
            retval = "0";
        } else {
            retval = element.trim();
        }

        return retval;
    }

}
// Jan 2016 from SESAR service
//
//    <barcode_img_src>app.geosamples.org/barcode/image.php?igsn=JFB111111&amp;sample_id=AQUIPE01-STD</barcode_img_src>
//    <user_code>JFB</user_code>
//    <igsn>JFB111111</igsn>
//    <name>AQUIPE01-STD</name>
//    <sample_other_names></sample_other_names>
//    <sample_type>Individual Sample</sample_type>
//    <parent_igsn>Not Provided</parent_igsn>
//    <material>Mineral</material>
//    <classification>Not Provided</classification>
//    <field_name>Not Provide</field_name>
//    <description>Not Provided</description>
//    <age_min>Not Provided</age_min>
//    <age_max>Not Provided</age_max>
//    <collection_method>Not Provided</collection_method>
//    <collection_method_description>Not Provided</collection_method_description>
//    <size>Not Provided</size>
//    <geological_age>Not Provided</geological_age>
//    <geological_unit>Not Provided</geological_unit>
//    <sample_comment>Not Provide</sample_comment>
//    <purpose>Not Provided</purpose>
//    <latitude>Not Provided</latitude>
//    <longitude>Not Provided</longitude>
//    <easting>Not Provided</easting>
//    <northing>Not Provided</northing>
//    <zone>Not Provided</zone>
//    <vertical_datum>Not Provided</vertical_datum>
//    <elevation>Not Provided</elevation>
//    <navigation_type>Not Provided</navigation_type>
//    <primary_location_type>Not Provided</primary_location_type>
//    <primary_location_name>Not Provided</primary_location_name>
//    <location_description>Not Provided</location_description>
//    <locality>Not Provided</locality>
//    <locality_description>Not Provided</locality_description>
//    <country>Not Provided</country>
//    <province>Not Provided</province>
//    <county>Not Provided</county>
//    <city>Not Provided</city>
//    <cruise_field_prgrm>Not Provided</cruise_field_prgrm>
//    <platform_type>Not Provided</platform_type>
//    <platform_name>Not Provided</platform_name>
//    <platform_descr>Not Provided</platform_descr>
//    <launch_platform_name>Not Provided</launch_platform_name>
//    <launch_id>Not Provided</launch_id>
//    <launch_type_name>Not Provided</launch_type_name>
//    <collector>Not Provided</collector>
//    <collector_detail>Not Provided</collector_detail>
//    <collection_start_date>Not Provided</collection_start_date>
//    <collection_end_date>Not Provided</collection_end_date>
//    <collection_date_precision>Not Provided</collection_date_precision>
//    <current_archive>Not Provide</current_archive>
//    <current_archive_contact>Not Provide</current_archive_contact>
//    <original_archive>Not Provide</original_archive>
//    <original_archive_contact>Not Provide</original_archive_contact>
//    <depth_min>Not Provided</depth_min>
//    <depth_max>Not Provided</depth_max>
//    <parents/>
//    <siblings/>
//    <children/>
