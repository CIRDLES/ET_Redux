/*
 * TableCellButtonEditor.java
 *
 * Created on March 29, 2006, 6:00 AM
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
package org.earthtime.UPb_Redux.renderers;

import java.awt.Component;
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author James F. Bowring
 */
public class TableCellButtonEditor extends AbstractCellEditor implements TableCellEditor {

    /**
     * Stores the component currently being edited.
     */
    Component comp_ = null;

    /**
     * Returns the value contained in the editor.
     *
     * @return The component value currently being edited.
     */
    @Override
    public Object getCellEditorValue () {
        return comp_;
    }

    /**
     * Sets an initial value for the editor. This will cause the editor to
     * stopEditing and lose any partially edited value if the editor is editing
     * when this method is called.
     *
     * Returns the component that should be added to the client's Component
     * hierarchy. Once installed in the client's hierarchy this component will
     * then be able to draw and receive user input.
     *
     * @param table The invoking JTable; can be null.
     * @param value The value of the cell to be edited. Here, the component.
     * @param isSelected True if the cell should to be highlighted.
     * @param row The row of the cell being edited.
     * @param column The column of the cell being edited.
     *
     * @return The component itself, so that it can receive user events.
     */
    @Override
    public Component getTableCellEditorComponent ( JTable table, Object value, boolean isSelected,
            int row, int column ) {
        if ( value instanceof Component ) {
            comp_ = (Component) value;

            // stops button clicks in table from causing selection
//////               table.setRowSelectionAllowed(false);

            return (Component) value;
        } else {
            return null;
        }
    }
}
