/*
 * AbstractMatrixTableViewModel.java
 *
 * Created Mar 5, 2012
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
package org.earthtime.matrices.matrixViews;

import java.awt.Color;
import java.awt.Component;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.dataDictionaries.RatioNamePrettyPrinter;
import org.earthtime.matrices.matrixModels.AbstractMatrixModel;

/**
 *
 * @author James F. Bowring
 */
public class AbstractMatrixTableViewModel extends AbstractTableModel {

    /**
     *
     */
    protected AbstractMatrixModel matrixModel;
    private String[] columnNames;
    private String[] rowNames;

    /**
     *
     */
    protected TableCellRenderer matrixCellRenderer = new MatrixCellRenderer();

    /**
     *
     * @param matrixModel
     */
    public AbstractMatrixTableViewModel(AbstractMatrixModel matrixModel) {
        this.matrixModel = matrixModel;

        buildTableView();
    }

    /**
     *
     */
    public final void buildTableView() {
            columnNames = new String[matrixModel.getCols().keySet().size() + 1];
            columnNames[0] = "<html><sub>\u21d3</sub>" + " Name " + "\u21d2</html>";
            Iterator<String> colKeys = matrixModel.getCols().keySet().iterator();
            while (colKeys.hasNext()) {
                String colKey = colKeys.next();
                columnNames[matrixModel.getCols().get(colKey) + 1] = RatioNamePrettyPrinter.makePrettyHTMLString(colKey);
            }

            rowNames = new String[matrixModel.getRows().keySet().size()];
            Iterator<Integer> rowKeys = matrixModel.getRows().keySet().iterator();
            while (rowKeys.hasNext()) {
                int rowKey = rowKeys.next();
                rowNames[rowKey] = RatioNamePrettyPrinter.makePrettyHTMLString(matrixModel.getRows().get(rowKey));
            }
        }

    /**
     *
     * @return
     */
    @Override
    public int getRowCount() {
        return rowNames.length;
    }

    /**
     *
     * @return
     */
    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    /**
     *
     * @param col
     * @return
     */
    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    /**
     *
     * @param rowIndex
     * @param columnIndex
     * @return
     */
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return rowNames[rowIndex];
        } else {
            try {
                double cellValue = matrixModel.getMatrix().get(rowIndex, columnIndex - 1);
                if (cellValue == 0.0) {
                    return "     0";
                } else if (cellValue == 1.0) {
                    return "     1";
                } else {
                    return cellValue;
                }
            } catch (Exception e) {
                return 0.0;
            }
        }
    }

    /**
     * @param matrixModel the matrixModel to set
     */
    public void setMatrixModel(AbstractMatrixModel matrixModel) {
        this.matrixModel = matrixModel;
    }

    /**
     *
     */
    public class MatrixCellRenderer extends JLabel implements TableCellRenderer {

        /**
         *
         */
        public MatrixCellRenderer() {
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex) {

            setFont(ReduxConstants.sansSerif_11_Bold);// "Calibri", Font.BOLD, 14 ) );
            setHorizontalAlignment(JLabel.LEFT);

            // format for easy reading, and supply all digits in the tooltip
            if (value instanceof String) {
                setText((String) value);
            }
            if (value instanceof Double) {
                NumberFormat formatter;
                if (Math.abs((Double) value) > 0.0001) {
                    formatter = new DecimalFormat(" 0.0########");
                } else {
                    formatter = new DecimalFormat(" 0.######E0#");
                }

                setText(formatter.format(value));
            }

            if (isCellEditable(rowIndex, vColIndex)) {
                setForeground(Color.red);
                setBackground(Color.white);
            } else {
                setForeground(Color.black);
                setBackground(Color.gray);
            }
            // Since the renderer is a component, return itself
            return this;
        }

        // The following methods override the defaults for performance reasons
        @Override
        public void validate() {
        }

        @Override
        public void revalidate() {
        }

        @Override
        protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        }

        @Override
        public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        }

    }
}
