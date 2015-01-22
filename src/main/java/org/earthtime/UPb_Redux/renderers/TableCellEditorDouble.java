/*
 * TableCellEditorDouble.java
 *
 * Created on November 7, 2006, 7:46 AM
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

import java.awt.Component;
import java.util.EventObject;
import javax.swing.AbstractCellEditor;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

/**
 *
 * @author James F. Bowring
 */
public class TableCellEditorDouble extends AbstractCellEditor  implements TableCellEditor{
    
    JComponent component = new JTextField();
    
    
    // This method is called when a cell value is edited by the user.
    /**
     * 
     * @param table
     * @param value
     * @param isSelected
     * @param rowIndex
     * @param vColIndex
     * @return
     */
    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int rowIndex, int vColIndex) {
        
        
        // 'value' is value contained in the cell located at (rowIndex, vColIndex)
        
     /*   if (isSelected) {
            // cell (and perhaps other cells) are selected
            addCellEditorListener(new CellEditorListener() {
                public void editingCanceled(ChangeEvent changeEvent) {
                }
                public void editingStopped(ChangeEvent changeEvent) {
                    notifyAll();
                }
            });
        }
        
        // Configure the component with the specified value
        ((JTextField)component).setText(value.toString());
      */  
        // Return the configured component
        
        
        return component;
    }
    
    private boolean isValid(String s){
        return true;
    }
    
    // This method is called just before the cell value
    // is saved. If the value is not valid, false should be returned.
    /**
     * 
     * @return
     */
    @Override
    public boolean stopCellEditing() {
        String s = (String)getCellEditorValue();
        
        if (!isValid(s)) {
            // Should display an error message at this point
            return false;
        }
        return super.stopCellEditing();
    }
    
    
    // This method is called when editing is completed.
    // It must return the new value to be stored in the cell.
    /**
     * 
     * @return
     */
    public Object getCellEditorValue() {
        return ((JTextField)component).getText();
    }
    
    /**
     * 
     * @param eventObject
     * @return
     */
    @Override
    public boolean isCellEditable(EventObject eventObject) {
        return true;
    }
    
    /**
     * 
     * @param eventObject
     * @return
     */
    @Override
    public boolean shouldSelectCell(EventObject eventObject) {
        return true;
    }
    
    
}
