/*
 * JTableForMatrices.java
 *
 * Created Aug 25, 2012
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
package org.earthtime.matrices.matrixViews;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import org.earthtime.UPb_Redux.ReduxConstants;

/**
 *
 * @author James F. Bowring
 */
public class JTableForMatrices extends JTable {

    /**
     *
     */
    protected TableCellRenderer matrixCellRenderer;

    /**
     *
     * @param tm
     */
    public JTableForMatrices ( TableModel tm ) {
        super( tm );

        matrixCellRenderer = new MatrixCellRenderer();

    }

    //Implement table cell tool tips to show double contents of each cell.
    @Override
    public String getToolTipText ( MouseEvent e ) {
        java.awt.Point p = e.getPoint();
        int rowIndex = rowAtPoint( p );
        int colIndex = columnAtPoint( p );

        return String.valueOf( getValueAt( rowIndex, colIndex ) );

    }

    @Override
    public TableCellRenderer getCellRenderer ( int row, int column ) {

        if ( column > 0 ) {
            return matrixCellRenderer;
        } else {
            return super.getCellRenderer( row, column );
        }
    }

    /**
     * @param matrixCellRenderer the matrixCellRenderer to set
     */
    public void setMatrixCellRenderer(TableCellRenderer matrixCellRenderer) {
        this.matrixCellRenderer = matrixCellRenderer;
    }

    /**
     *
     */
    public class MatrixCellRenderer extends JLabel implements TableCellRenderer {

        @Override
        public Component getTableCellRendererComponent ( JTable table, Object value,
                boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex ) {

            setFont(ReduxConstants.sansSerif_11_Bold);// "Calibri", Font.BOLD, 14 ) );
            setHorizontalAlignment( JLabel.LEFT );

            // format for easy reading, and supply all digits in the tooltip
            if ( value instanceof String ) {
                setText( (String) value );
            }
            if ( value instanceof Double ) {
                NumberFormat formatter;
                if ( Math.abs( (Double) value ) > 0.0001 ) {
                    formatter = new DecimalFormat( " 0.0########" );
                } else {
                    formatter = new DecimalFormat( " 0.######E0#" );
                }

                setText( formatter.format(value) );
            }

            return this;
        }

        // The following methods override the defaults for performance reasons
        @Override
        public void validate () {
        }

        @Override
        public void revalidate () {
        }

        @Override
        protected void firePropertyChange ( String propertyName, Object oldValue, Object newValue ) {
        }

        @Override
        public void firePropertyChange ( String propertyName, boolean oldValue, boolean newValue ) {
        }
        
    }
}
