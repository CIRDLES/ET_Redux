/*
 * Copyright 2019 CIRDLES.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.earthtime.plots.evolution.openSystem;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import org.earthtime.plots.evolution.seaWater.SeaWaterInitialDelta234UTableModel;
import org.earthtime.reduxLabData.ReduxLabData;

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class OpenSystemIsochronTableModel extends AbstractTableModel implements Serializable, Comparable<OpenSystemIsochronTableModel> {

    // class attributes
    private static final long serialVersionUID = -2719449659758349646L;

    // attributes
    private List<OpenSystemIsochronModelEntry> entryList;
    private double defaultAgeInKa = 100.0;
    private double defaultPctLoss = 2.5;
    private double defaultRStart = -0.05;
    private double defaultREnd = 0.15;
    private Color displayColor = Color.blue;
    private boolean showSeawaterModel = false;
    private boolean showOpenIsochrons = false;

    private transient SeaWaterInitialDelta234UTableModel seaWaterInitialDelta234UTableModel;

    private final String[] columnNames = new String[]{
        " Age in ka", " Pct Loss", " start R", " end R"
    };
    private final Class[] columnClass = new Class[]{
        Double.class, Double.class, Double.class, Double.class
    };

    public OpenSystemIsochronTableModel() {
        this(new ArrayList<OpenSystemIsochronModelEntry>(),
                2.5,
                -0.05,
                0.15,
                Color.blue);
    }

    public OpenSystemIsochronTableModel(
            List<OpenSystemIsochronModelEntry> entryList, double defaultPctLoss, double defaultRStart, double defaultREnd, Color displayColor) {
        this.entryList = entryList;
        if (this.entryList.isEmpty()) {
            this.entryList.add(new OpenSystemIsochronModelEntry(defaultAgeInKa, defaultPctLoss, defaultRStart, defaultREnd));
        }
        this.defaultPctLoss = defaultPctLoss;
        this.defaultRStart = defaultRStart;
        this.defaultREnd = defaultREnd;
        this.displayColor = displayColor;
        showSeawaterModel = false;
        showOpenIsochrons = false;
        
        this.seaWaterInitialDelta234UTableModel = ReduxLabData.getInstance().getDefaultSeaWaterInitialDelta234UTableModel();
    }

//    @Override
//    public int compareTo(SeaWaterInitialDelta234UTableModel model) throws ClassCastException {
//        String modelID
//                = model.getNameAndVersion().trim();
//        return (this.getNameAndVersion().trim() //
//                .compareToIgnoreCase(modelID));
//    }
    @Override
    public String getColumnName(int column) {
        return columnNames[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columnClass[columnIndex];
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        return entryList.size();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        OpenSystemIsochronModelEntry row = entryList.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return row.getAgeInKa();
            case 1:
                return row.getPctLoss();
            case 2:
                return row.getrStart();
            case 3:
                return row.getrEnd();
            default:
                break;
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        OpenSystemIsochronModelEntry row = entryList.get(rowIndex);
        switch (columnIndex) {
            case 0:
                row.setAgeInKa((Double) aValue);
                sortEntries();
                break;
            case 1:
                row.setPctLoss((Double) aValue);
                break;
            case 2:
                row.setrStart((Double) aValue);
                break;
            case 3:
                row.setrEnd((Double) aValue);
                break;
            default:
                break;
        }
        fireTableDataChanged();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    public void removeRow(int row) {
        entryList.remove(row);
    }

    public void addRow() {
        entryList.add(new OpenSystemIsochronModelEntry(0, defaultPctLoss, defaultRStart, defaultREnd));
    }

    private void sortEntries() {
        Collections.sort(entryList, (OpenSystemIsochronModelEntry o1, OpenSystemIsochronModelEntry o2) -> {
            int retVal;
            if (o1.getAgeInKa() < 0) {
                retVal = 1;
            } else if (o2.getAgeInKa() < 0) {
                retVal = -1;
            } else {
                retVal = Double.compare(o1.getAgeInKa(), o2.getAgeInKa());
            }

            return retVal;
        });
    }

    @Override
    public int compareTo(OpenSystemIsochronTableModel o) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * @param seaWaterInitialDelta234UTableModel the
     * seaWaterInitialDelta234UTableModel to set
     */
    public void setSeaWaterInitialDelta234UTableModel(SeaWaterInitialDelta234UTableModel seaWaterInitialDelta234UTableModel) {
        this.seaWaterInitialDelta234UTableModel = seaWaterInitialDelta234UTableModel;
    }

    public SeaWaterInitialDelta234UTableModel getSeaWaterInitialDelta234UTableModel() {
        return seaWaterInitialDelta234UTableModel;
    }

    /**
     * @return the entryList
     */
    public List<OpenSystemIsochronModelEntry> getEntryList() {
        return entryList;
    }

    /**
     * @return the displayColor
     */
    public Color getDisplayColor() {
        return displayColor;
    }

    /**
     * @param displayColor the displayColor to set
     */
    public void setDisplayColor(Color displayColor) {
        this.displayColor = displayColor;
    }

    /**
     * @return the showSeawaterModel
     */
    public boolean isShowSeawaterModel() {
        return showSeawaterModel;
    }

    /**
     * @param showSeawaterModel the showSeawaterModel to set
     */
    public void setShowSeawaterModel(boolean showSeawaterModel) {
        this.showSeawaterModel = showSeawaterModel;
    }

    /**
     * @return the showOpenIsochrons
     */
    public boolean isShowOpenIsochrons() {
        return showOpenIsochrons;
    }

    /**
     * @param showOpenIsochrons the showOpenIsochrons to set
     */
    public void setShowOpenIsochrons(boolean showOpenIsochrons) {
        this.showOpenIsochrons = showOpenIsochrons;
    }

}
