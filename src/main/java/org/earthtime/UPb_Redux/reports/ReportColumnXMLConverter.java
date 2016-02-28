/*
 * ReportColumnXMLConverter.java
 *
 *
 * Copyright 2006-2016 James F. Bowring and www.Earth-Time.org
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
import org.earthtime.reports.ReportColumnInterface;

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
public class ReportColumnXMLConverter implements Converter {

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
        return clazz.equals(ReportColumn.class);
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

        ReportColumnInterface reportColumn = (ReportColumnInterface) value;

        writer.startNode("displayName1");
        writer.setValue(reportColumn.getDisplayName1());
        writer.endNode();

        writer.startNode("displayName2");
        String displayName2 = reportColumn.getDisplayName2();
        // test for delta
        writer.setValue(displayName2.replace("\u03B4", "LOWERCASEDELTA"));
        writer.endNode();

        writer.startNode("displayName3");
        writer.setValue(reportColumn.getDisplayName3());
        writer.endNode();

        writer.startNode("positionIndex");
        writer.setValue(Integer.toString(reportColumn.getPositionIndex()));
        writer.endNode();

        writer.startNode("units");
        writer.setValue(reportColumn.getUnitsFoxXML());
        writer.endNode();

        writer.startNode("retrieveMethodName");
        writer.setValue(reportColumn.getRetrieveMethodName());
        writer.endNode();

        writer.startNode("retrieveVariableName");
        writer.setValue(reportColumn.getRetrieveVariableName());
        writer.endNode();

        writer.startNode("uncertaintyColumn");
        if (reportColumn.getUncertaintyColumn() != null) {
            ReportColumnInterface myReportColumn = reportColumn.getUncertaintyColumn();
            // repaired jan 2016 to restore report columns  after serialization
            displayName2 = myReportColumn.getDisplayName2();
            String displayName3 = myReportColumn.getDisplayName3();

            if (displayName3.contains("%")) {
                myReportColumn.setDisplayName3("PLUSMINUS2SIGMA%");
            } else {
                myReportColumn.setDisplayName2("PLUSMINUS2SIGMA");
            }
            context.convertAnother(reportColumn.getUncertaintyColumn());

            // restore
            myReportColumn.setDisplayName2(displayName2);
            myReportColumn.setDisplayName3(displayName3);
        }
        writer.endNode();

        writer.startNode("uncertaintyType");
        writer.setValue(reportColumn.getUncertaintyType());
        writer.endNode();

        writer.startNode("displayedWithArbitraryDigitCount");
        writer.setValue(Boolean.toString(reportColumn.isDisplayedWithArbitraryDigitCount()));
        writer.endNode();

        writer.startNode("countOfSignificantDigits");
        writer.setValue(Integer.toString(reportColumn.getCountOfSignificantDigits()));
        writer.endNode();

        writer.startNode("visible");
        writer.setValue(Boolean.toString(reportColumn.isVisible()));
        writer.endNode();

        writer.startNode("footnoteSpec");
        writer.setValue(reportColumn.getFootnoteSpec());
        writer.endNode();

        writer.startNode("alternateDisplayName");
        writer.setValue(reportColumn.getAlternateDisplayName());
        writer.endNode();

        writer.startNode("needsPb");
        writer.setValue(Boolean.toString(reportColumn.isNeedsPb()));
        writer.endNode();

        writer.startNode("needsU");
        writer.setValue(Boolean.toString(reportColumn.isNeedsU()));
        writer.endNode();

        writer.startNode("legacyData");
        writer.setValue(Boolean.toString(reportColumn.isLegacyData()));
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

        ReportColumnInterface reportColumn = new ReportColumn();

        reader.moveDown();
        reportColumn.setDisplayName1(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        String displayName2 = reader.getValue();
        // test for delta
        reportColumn.setDisplayName2(displayName2.replace("LOWERCASEDELTA", "\u03B4"));
        reader.moveUp();

        reader.moveDown();
        reportColumn.setDisplayName3(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        reportColumn.setPositionIndex(Integer.parseInt(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        reportColumn.setUnitsFromXML(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        reportColumn.setRetrieveMethodName(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        reportColumn.setRetrieveVariableName(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        if (reader.hasMoreChildren()) {
            ReportColumnInterface uncertaintyColumn = new ReportColumn();
            uncertaintyColumn = (ReportColumnInterface) context.convertAnother(uncertaintyColumn, ReportColumn.class);
            // correct for unicode missing in xml
            if (uncertaintyColumn.getUncertaintyType().equalsIgnoreCase("PCT")) {
                uncertaintyColumn.setDisplayName3("\u00B12\u03C3 %");
            } else {
                uncertaintyColumn.setDisplayName2("\u00B12\u03C3");
            }
            reportColumn.setUncertaintyColumn(uncertaintyColumn);
        } else {
            reportColumn.setUncertaintyColumn(null);
        }
        reader.moveUp();

        reader.moveDown();
        reportColumn.setUncertaintyType(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        reportColumn.setDisplayedWithArbitraryDigitCount((reader.getValue().equalsIgnoreCase("true")));
        reader.moveUp();

        reader.moveDown();
        reportColumn.setCountOfSignificantDigits(Integer.valueOf(reader.getValue()));
        reader.moveUp();

        reader.moveDown();
        reportColumn.setVisible((reader.getValue().equalsIgnoreCase("true")));
        reader.moveUp();

        reader.moveDown();
        reportColumn.setFootnoteSpec(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        reportColumn.setAlternateDisplayName(reader.getValue());
        reader.moveUp();

        reader.moveDown();
        reportColumn.setNeedsPb((reader.getValue().equalsIgnoreCase("true")));
        reader.moveUp();

        reader.moveDown();
        reportColumn.setNeedsU((reader.getValue().equalsIgnoreCase("true")));
        reader.moveUp();

        reader.moveDown();
        reportColumn.setLegacyData((reader.getValue().equalsIgnoreCase("true")));
        reader.moveUp();

        return reportColumn;
    }
}
