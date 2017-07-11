/*
 * ReportSettingsXMLConverter.java
 *
 * Created 23 October 2009
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
package org.earthtime.UPb_Redux.reports;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * A <code>ReportSettingsXMLConverter</code> is used to marshal and unmarshal
 * data between <code>reportSettings</code> and XML files.
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
public class ReportSettingsXMLConverter implements Converter {

    /**
     * checks the argument <code>clazz</code> against
     * <code>reportSettings</code>'s <code>Class</code>. Used to ensure that the
     * object about to be marshalled/unmarshalled is of the correct type.
     *
     * @pre argument <code>clazz</code> is a valid <code>Class</code>
     * @post    <code>boolean</code> is returned comparing <code>clazz</code>
     * against <code>reportSettings.class</code>
     * @param clazz   <code>Class</code> of the <code>Object</code> you wish to
     * convert to/from XML
     * @return  <code>boolean</code> - <code>true</code> if <code>clazz</code>
     * matches <code>reportSettings</code>'s <code>Class</code>; else
     * <code>false</code>.
     */
    @Override
    public boolean canConvert(Class clazz) {
        return clazz.equals(ReportSettings.class);
    }

    /**
     * writes the argument <code>value</code> to the XML file specified through
     * <code>writer</code>
     *
     * @pre     <code>value</code> is a valid <code>reportSettings</code>, <code>
     *          writer</code> is a valid <code>HierarchicalStreamWriter</code>, and
     * <code>context</code> is a valid <code>MarshallingContext</code>
     * @post    <code>value</code> is written to the XML file specified via
     * <code>writer</code>
     * @param value   <code>reportSettings</code> that you wish to write to a file
     * @param writer stream to write through
     * @param context <code>MarshallingContext</code> used to store generic data
     */
    @Override
    public void marshal(Object value, HierarchicalStreamWriter writer,
            MarshallingContext context) {

        ReportSettings reportSettings = (ReportSettings) value;

        writer.startNode("name");
        writer.setValue(reportSettings.getName());
        writer.endNode();

        writer.startNode("version");
        writer.setValue(Integer.toString(reportSettings.getVersion()));
        writer.endNode();

        writer.startNode("isotopeStyle");
        writer.setValue(reportSettings.getDefaultReportSpecsType());
        writer.endNode();

        writer.startNode("fractionCategory");
        context.convertAnother(reportSettings.getFractionCategory());
        writer.endNode();

        if (reportSettings.isdefaultReportSpecsType_UPb()) {
            writer.startNode("compositionCategory");
            context.convertAnother(reportSettings.getCompositionCategory());
            writer.endNode();

            writer.startNode("isotopicRatiosCategory");
            context.convertAnother(reportSettings.getIsotopicRatiosCategory());
            writer.endNode();

            writer.startNode("isotopicRatiosPbcCorrCategory");
            context.convertAnother(reportSettings.getIsotopicRatiosPbcCorrCategory());
            writer.endNode();

            writer.startNode("datesCategory");
            context.convertAnother(reportSettings.getDatesCategory());
            writer.endNode();

            writer.startNode("datesPbcCorrCategory");
            context.convertAnother(reportSettings.getDatesPbcCorrCategory());
            writer.endNode();

            writer.startNode("rhosCategory");
            context.convertAnother(reportSettings.getRhosCategory());
            writer.endNode();

            writer.startNode("traceElementsCategory");
            context.convertAnother(reportSettings.getTraceElementsCategory());
            writer.endNode();
        } else {
            writer.startNode("datesCategory");
            context.convertAnother(reportSettings.getDatesCategory());
            writer.endNode();
        }

        writer.startNode("fractionCategory2");
        context.convertAnother(reportSettings.getFractionCategory2());
        writer.endNode();

        writer.startNode("reportSettingsComment");
        writer.setValue(reportSettings.getReportSettingsComment());
        writer.endNode();

        writer.startNode("legacyData");
        writer.setValue(Boolean.toString(reportSettings.isLegacyData()));
        writer.endNode();

    }

    /**
     * reads a <code>reportSettings</code> from the XML file specified through
     * <code>reader</code>
     *
     * @pre     <code>reader</code> leads to a valid <code>reportSettings</code>
     * @post the <code>reportSettings</code> is read from the XML file and
     * returned
     * @param reader stream to read through
     * @param context <code>UnmarshallingContext</code> used to store generic
     * data
     * @return  <code>Object</code> - <code>reportSettings</code> read from file
     * specified by <code>reader</code>
     */
    @Override
    public Object unmarshal(HierarchicalStreamReader reader,
            UnmarshallingContext context) {

        ReportSettings reportSettings = new ReportSettings();

        reader.moveDown();
        reportSettings.setName(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        reportSettings.setVersion(Integer.parseInt(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        if ("isotopeStyle".equals(reader.getNodeName())) {
            reportSettings.setDefaultReportSpecsType(reader.getValue());
            reader.moveUp();
            reader.moveDown();
        }

        ReportCategory reportCategory = new ReportCategory();
        reportCategory = (ReportCategory) context.convertAnother(reportCategory, ReportCategory.class);
        reportSettings.setFractionCategory(reportCategory);
        reader.moveUp();

        if (reportSettings.isdefaultReportSpecsType_UPb()) {

            reader.moveDown();
            reportCategory = new ReportCategory();
            reportCategory = (ReportCategory) context.convertAnother(reportCategory, ReportCategory.class);
            reportSettings.setCompositionCategory(reportCategory);
            reader.moveUp();

            reader.moveDown();
            reportCategory = new ReportCategory();
            reportCategory = (ReportCategory) context.convertAnother(reportCategory, ReportCategory.class);
            reportSettings.setIsotopicRatiosCategory(reportCategory);
            reader.moveUp();

            reader.moveDown();
            reportCategory = new ReportCategory();
            reportCategory = (ReportCategory) context.convertAnother(reportCategory, ReportCategory.class);
            reportSettings.setIsotopicRatiosPbcCorrCategory(reportCategory);
            reader.moveUp();

            reader.moveDown();
            reportCategory = new ReportCategory();
            reportCategory = (ReportCategory) context.convertAnother(reportCategory, ReportCategory.class);
            reportSettings.setDatesCategory(reportCategory);
            reader.moveUp();

            reader.moveDown();
            reportCategory = new ReportCategory();
            reportCategory = (ReportCategory) context.convertAnother(reportCategory, ReportCategory.class);
            reportSettings.setDatesPbcCorrCategory(reportCategory);
            reader.moveUp();

            reader.moveDown();
            reportCategory = new ReportCategory();
            reportCategory = (ReportCategory) context.convertAnother(reportCategory, ReportCategory.class);
            reportSettings.setRhosCategory(reportCategory);
            reader.moveUp();

            reader.moveDown();
            reportCategory = new ReportCategory();
            reportCategory = (ReportCategory) context.convertAnother(reportCategory, ReportCategory.class);
            reportSettings.setTraceElementsCategory(reportCategory);
            reader.moveUp();
        } else {
            reader.moveDown();
            reportCategory = new ReportCategory();
            reportCategory = (ReportCategory) context.convertAnother(reportCategory, ReportCategory.class);
            reportSettings.setDatesCategory(reportCategory);
            reader.moveUp();

        }
        reader.moveDown();
        reportCategory = new ReportCategory();
        reportCategory = (ReportCategory) context.convertAnother(reportCategory, ReportCategory.class);
        reportSettings.setFractionCategory2(reportCategory);
        reader.moveUp();

        reader.moveDown();
        reportSettings.setReportSettingsComment(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        reportSettings.setLegacyData((reader.getValue().equalsIgnoreCase("true")));
        reader.moveUp();

        reportSettings.assembleReportCategories();
        return reportSettings;
    }
}
