/*
 * TableCellRendererToLabel.java
 *
 * Created on March 25, 2006, 8:29 PM
 *
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
package org.earthtime.UPb_Redux.renderers;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author James F. Bowring
 */
public class TableCellRendererToLabel extends JLabel implements TableCellRenderer {
    // This method is called each time a cell in a column
    // using this renderer needs to be rendered.

    /**
     * 
     */
    protected boolean mePaint = true;
    /**
     * 
     */
    protected Color selection = null;

    /**
     * 
     * @param table
     * @param value
     * @param isSelected
     * @param hasFocus
     * @param rowIndex
     * @param vColIndex
     * @return
     */
    @Override
    public Component getTableCellRendererComponent ( JTable table, Object value,
            boolean isSelected, boolean hasFocus, int rowIndex, int vColIndex ) {
        // 'value' is value contained in the cell located at
        // (rowIndex, vColIndex)

////////        if (vColIndex > 2){
//            table.setRowSelectionAllowed(true);
////////        } else {
////////            table.setRowSelectionAllowed(false);
////////            isSelected = false;
////////        }


        selection = table.getSelectionBackground();

        if ( isSelected && vColIndex >= 2 ) {
            // cell (and perhaps other cells) are selected
            mePaint = false;
        } else {
            mePaint = true;
        }

        // Configure the component with the specified value
        setText( value.toString() );

        // Set tool tip if desired
        //setToolTipText(value.toString());

        // Since the renderer is a component, return itself
        return this;
    }

    // Paint current color
    /**
     * 
     * @param g
     */
    @Override
    public void paint ( Graphics g ) {
        if (  ! mePaint ) {
            g.setColor( selection );
        }
        g.fillRect( 0, 0, getWidth() - 1, getHeight() - 1 );
    }

    // The following methods override the defaults for performance reasons
    /**
     * 
     */
    @Override
    public void validate () {
    }

    /**
     * 
     */
    @Override
    public void revalidate () {
    }

    /**
     * 
     * @param propertyName
     * @param oldValue
     * @param newValue
     */
    @Override
    protected void firePropertyChange ( String propertyName, Object oldValue, Object newValue ) {
    }

    /**
     * 
     * @param propertyName
     * @param oldValue
     * @param newValue
     */
    @Override
    public void firePropertyChange ( String propertyName, boolean oldValue, boolean newValue ) {
    }
}
