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
package org.earthtime.plots.evolution.seaWater;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author James F. Bowring, CIRDLES.org, and Earth-Time.org
 */
public class SeaWaterInitialDelta234UTableModel extends AbstractTableModel implements Serializable {

    // Class variables
    private static final long serialVersionUID = 7009628923494163993L;
    private static int versionNumber = 0;

    private final List<SeaWaterDelta234UModelEntry> entryList;

    private final String[] columnNames = new String[]{
        "   Age in ka", "   \u03B4234U \u2030", "   1\u03C3 Abs Unct"
    };
    private final Class[] columnClass = new Class[]{
        Double.class, Double.class, Double.class
    };

    public SeaWaterInitialDelta234UTableModel() {
        SeaWaterDelta234UModelEntry row1 = new SeaWaterDelta234UModelEntry(0, 145, 1);
        SeaWaterDelta234UModelEntry row2 = new SeaWaterDelta234UModelEntry(500, 145, 1);
        SeaWaterDelta234UModelEntry row3 = new SeaWaterDelta234UModelEntry(1000, 145, 1);

        //build the list
        entryList = new ArrayList<>();
        entryList.add(row1);
        entryList.add(row2);
        entryList.add(row3);
    }

    public SeaWaterInitialDelta234UTableModel(List<SeaWaterDelta234UModelEntry> entryList) {
        this.entryList = entryList;
    }

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
        SeaWaterDelta234UModelEntry row = entryList.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return row.getAgeInKa();
            case 1:
                return row.getDelta234UPerMil();
            case 2:
                return row.getOneSigmaAbsUnct();
            default:
                break;
        }
        return null;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        SeaWaterDelta234UModelEntry row = entryList.get(rowIndex);
        switch (columnIndex) {
            case 0:
                row.setAgeInKa((Double) aValue);
                sortEntries();
                break;
            case 1:
                row.setDelta234UPerMil((Double) aValue);
                break;
            case 2:
                row.setOneSigmaAbsUnct((Double) aValue);
                break;
            default:
                break;
        }

    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return true;
    }

    public void removeRow(int row) {
        entryList.remove(row);
    }

    public void addRow() {
        entryList.add(new SeaWaterDelta234UModelEntry());
    }

    private void sortEntries() {
        Collections.sort(entryList, (SeaWaterDelta234UModelEntry o1, SeaWaterDelta234UModelEntry o2) -> {
            int retVal;
            if (o1.getAgeInKa() < 0) {
                retVal = 1;
            } else if (o2.ageInKa < 0) {
                retVal = -1;
            } else {
                retVal = Double.compare(o1.ageInKa, o2.ageInKa);
            }

            return retVal;
        });
    }

    /**
     * @return the entryList
     */
    public List<SeaWaterDelta234UModelEntry> getEntryList() {
        return entryList;
    }

    public double[] getArrayOfDates() {
        double[] datesArray = new double[entryList.size()];
        for (int i = 0; i < entryList.size(); i++) {
            datesArray[i] = entryList.get(i).ageInKa;
        }
        
        return datesArray;
    }
    
    public double[] getArrayOfDeltasAsRatios() {
        double[] deltasArray = new double[entryList.size()];
        for (int i = 0; i < entryList.size(); i++) {
            deltasArray[i] = entryList.get(i).delta234UPerMil / 1000.0 + 1.0;
        }
        
        return deltasArray;
    }
}
