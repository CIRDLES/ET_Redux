/*
 * TableCellRendererToButton.java
 *
 * Created on March 29, 2006, 5:27 AM
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
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author James F. Bowring
 */
public class TableCellRendererToButton extends JButton implements TableCellRenderer {

    /**
     * 
     * @param table
     * @param value
     * @param isSelected
     * @param hasFocus
     * @param row
     * @param column
     * @return
     */
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {

        if (value instanceof Component) {
            return (Component) value;
        } else {
            return null;
        }

    }
    // The following methods override the defaults for performance reasons
    /**
     * 
     */
    @Override
    public void validate() {
    }

    /**
     * 
     */
    @Override
    public void revalidate() {
    }

    /**
     * 
     * @param propertyName
     * @param oldValue
     * @param newValue
     */
    @Override
    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
    }

    /**
     * 
     * @param propertyName
     * @param oldValue
     * @param newValue
     */
    @Override
    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
    }
}

