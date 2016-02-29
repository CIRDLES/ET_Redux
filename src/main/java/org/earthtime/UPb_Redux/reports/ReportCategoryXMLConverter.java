/*
 * ReportCategoryXMLConverter.java
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
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A <code>ReportSettingsXMLConverter</code> is used to marshal and unmarshal data
 * between <code>reportSettings</code> and XML files.
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
public class ReportCategoryXMLConverter implements Converter {

    /**
     * checks the argument <code>clazz</code> against <code>reportSettings</code>'s
     * <code>Class</code>. Used to ensure that the object about to be
     * marshalled/unmarshalled is of the correct type.
     * 
     * @pre     argument <code>clazz</code> is a valid <code>Class</code>
     * @post    <code>boolean</code> is returned comparing <code>clazz</code>
     *          against <code>reportSettings.class</code>
     * @param   clazz   <code>Class</code> of the <code>Object</code> you wish
     *                  to convert to/from XML
     * @return  <code>boolean</code> - <code>true</code> if <code>clazz</code> matches
     *          <code>reportSettings</code>'s <code>Class</code>; else <code>false</code>.
     */
    @Override
    public boolean canConvert ( Class clazz ) {
        return clazz.equals( ReportCategory.class );
    }

    /**
     * writes the argument <code>value</code> to the XML file specified through <code>writer</code>
     * 
     * @pre     <code>value</code> is a valid <code>reportSettings</code>, <code>
     *          writer</code> is a valid <code>HierarchicalStreamWriter</code>,
     *          and <code>context</code> is a valid <code>MarshallingContext</code>
     * @post    <code>value</code> is written to the XML file specified via <code>writer</code>
     * @param   value   <code>reportSettings</code> that you wish to write to a file
     * @param   writer  stream to write through
     * @param   context <code>MarshallingContext</code> used to store generic data
     */
    @Override
    public void marshal ( Object value, HierarchicalStreamWriter writer,
            MarshallingContext context ) {

        ReportCategory reportCategory = (ReportCategory) value;

        writer.startNode( "displayName" );
        writer.setValue( reportCategory.getDisplayName() );
        writer.endNode();

        writer.startNode( "positionIndex" );
        writer.setValue( Integer.toString( reportCategory.getPositionIndex() ) );
        writer.endNode();

        writer.startNode( "categoryColumns" );
        context.convertAnother( reportCategory.getCategoryColumns() );
        writer.endNode();

        writer.startNode( "categoryColor" );
        writer.setValue(  Integer.toString(reportCategory.getCategoryColor().getRGB() ));
        writer.endNode();

        writer.startNode( "visible" );
        writer.setValue( Boolean.toString( reportCategory.isVisible() ) );
        writer.endNode();

        writer.startNode( "legacyData" );
        writer.setValue( Boolean.toString( reportCategory.isLegacyData() ) );
        writer.endNode();


    }

    /**
     * reads a <code>reportSettings</code> from the XML file specified through <code>reader</code>
     * 
     * @pre     <code>reader</code> leads to a valid <code>reportSettings</code>
     * @post    the <code>reportSettings</code> is read from the XML file and returned
     * @param   reader  stream to read through
     * @param   context <code>UnmarshallingContext</code> used to store generic data
     * @return  <code>Object</code> - <code>reportSettings</code> read from file
     *          specified by <code>reader</code>
     */
    public Object unmarshal ( HierarchicalStreamReader reader,
            UnmarshallingContext context ) {

        ReportCategory reportCategory = new ReportCategory();

        reader.moveDown();
        reportCategory.setDisplayName( reader.getValue() );
        reader.moveUp();

        reader.moveDown();
        reportCategory.setPositionIndex( Integer.parseInt( reader.getValue() ) );
        reader.moveUp();

        reader.moveDown();
        if ( "categoryColumns".equals( reader.getNodeName() ) ) {
            ArrayList<ReportColumn> columns = new ArrayList<>();
            while (reader.hasMoreChildren()) {
                reader.moveDown();
                ReportColumn item = new ReportColumn();
                item = (ReportColumn) context.convertAnother( item, ReportColumn.class );
                columns.add( item );
                reader.moveUp();
            }
            // Convert to array
            ReportColumn[] ReportCategoryColumns = new ReportColumn[columns.size()];
            for (int i = 0; i < columns.size(); i ++) {
                ReportCategoryColumns[i] = columns.get( i );
            }
            Arrays.sort( ReportCategoryColumns );
            reportCategory.setCategoryColumns( ReportCategoryColumns );
        }
        reader.moveUp();

        reader.moveDown();
        reportCategory.setCategoryColor( new Color( Integer.valueOf( reader.getValue())));
        reader.moveUp();

        reader.moveDown();
        reportCategory.setVisible((reader.getValue().equalsIgnoreCase( "true" )));
        reader.moveUp();

        reader.moveDown();
        reportCategory.setLegacyData((reader.getValue().equalsIgnoreCase( "true" )));
        reader.moveUp();

        return reportCategory;
    }
}

