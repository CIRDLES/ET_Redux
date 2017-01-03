/*
 * UPbFractionTable.java
 *
 * Created on March 24, 2006, 8:26 AM
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
package org.earthtime.UPb_Redux.fractions.UPbReduxFractions;

import java.awt.Color;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import org.earthtime.ETReduxFrame;
import org.earthtime.UPb_Redux.renderers.DoubleCellRendererToLabel;
import org.earthtime.UPb_Redux.renderers.HeaderCellRendererToLabel;
import org.earthtime.UPb_Redux.renderers.PbRatioCellRenderer;
import org.earthtime.UPb_Redux.renderers.TableCellButtonEditor;
import org.earthtime.UPb_Redux.renderers.TableCellRendererToButton;
import org.earthtime.UPb_Redux.renderers.TracerNameCellRenderer;
import org.earthtime.UPb_Redux.renderers.URatioCellRenderer;
import org.earthtime.samples.SampleInterface;

/**
 *
 * @author James F. Bowring
 */
public class UPbFractionTable extends JTable {

    private SampleInterface mySample;

    /**
     * Creates a new instance of UPbFractionTable
     */
    public UPbFractionTable() {
        super();
        setFont(new java.awt.Font("SansSerif", 1, 11));
        setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        setAutoscrolls(false);
        setRowHeight(32);
        setGridColor(Color.gray);

        // added march 2008
        //   setColumnSelectionAllowed(false);
        setRowSelectionAllowed(false);
        //   setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }

    /**
     * 
     */
    public void formatTable() {

        TableColumn column = null;

        for (int col = 0; col < getColumnCount(); col++) {
            column = getColumnModel().getColumn(col);

            switch (col) {
                case 0:  //Aliquot button
                    column.setPreferredWidth(160);
                    column.setCellRenderer(new TableCellRendererToButton());
                    column.setCellEditor(new TableCellButtonEditor());
                    break;
                case 1:  //Fraction selector checkbox
                    column.setPreferredWidth(25);//53);
                    column.setCellRenderer(new TableCellRendererToButton());
                    column.setCellEditor(new TableCellButtonEditor());
                    break;
                case 2:  //Fraction notes viewer
                    column.setPreferredWidth(29);//53);
                    column.setCellRenderer(new TableCellRendererToButton());
                    column.setCellEditor(new TableCellButtonEditor());
                    break;
                case 3: // fraction ID
                    column.setPreferredWidth(100);
                    column.setCellRenderer(new TableCellRendererToButton());
                    column.setCellEditor(new TableCellButtonEditor());
                    break;
                case 4:// 206/204 lead ratio a little wider
                    column.setPreferredWidth(PbRatioCellRenderer.PbRatioWidthInTable + 7);
                    column.setCellRenderer(new PbRatioCellRenderer());
                    break;
                case 5:
                case 6:
                case 7:
                case 8:
                case 9:
                case 10:// lead ratios
                    column.setPreferredWidth(PbRatioCellRenderer.PbRatioWidthInTable);
                    column.setCellRenderer(new PbRatioCellRenderer());
                    break;
                case 11:
                case 12:
                case 13: // uranium ratios
                    column.setPreferredWidth(URatioCellRenderer.URatioWidthInTable);
                    column.setCellRenderer(new URatioCellRenderer());
                    break;
                case 14:  //tracer id
                    column.setPreferredWidth(120);
                    column.setCellRenderer(new TracerNameCellRenderer());
                    break;
                default:
                    column.setPreferredWidth(60);
                    column.setCellRenderer(new DoubleCellRendererToLabel("%4.3f"));

            }
        }




    }

    /**
     * 
     * @return
     */
    public SampleInterface getMySample() {
        return mySample;
    }

    /**
     * 
     * @param myFrame
     * @param mySample
     */
    public void setMySample(ETReduxFrame myFrame, SampleInterface mySample) {
        this.mySample = mySample;

        setModel(new UPbFractionTableModel(myFrame, mySample));

        JTableHeader header = getTableHeader();
        header.setReorderingAllowed(false);
        float[] myLightBlue = Color.RGBtoHSB(241, 246, 254, null);
        header.setBackground(Color.getHSBColor(myLightBlue[0], myLightBlue[1], myLightBlue[2]));
        header.setDefaultRenderer(new HeaderCellRendererToLabel());


    }
}


