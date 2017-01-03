/*
 * UPbFractionTableModel.java
 *
 * Created on March 29, 2006, 4:09 PM
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
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.table.AbstractTableModel;
import org.earthtime.ETReduxFrame;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.UPb_Redux.dialogs.fractionManagers.FractionNotesDialog;
import org.earthtime.UPb_Redux.renderers.EditFractionButton;
import org.earthtime.fractions.ETFractionInterface;
import org.earthtime.samples.SampleInterface;

/**
 *
 * @author James F. Bowring
 */
public class UPbFractionTableModel extends AbstractTableModel {

    private String[] columnNames = UPbFraction.getColumnNames();
    // has to be type Object to satisfy Table

    private ArrayList<Object[]> data = new ArrayList<Object[]>();

    private SampleInterface sample;

    private ETReduxFrame parentFrame;

    private final int aliquotButtonColumn = 0;

    private final int fractionSelectorCheckboxColumn = 1;

    private final int fractionNotesButtonColumn = 2;

    private final int FractionButtonColumn = 3;

    /**
     * Creates a new instance of UPbFractionTableModel
     * @param myFrame
     * @param mySample  
     */
    public UPbFractionTableModel(ETReduxFrame myFrame, SampleInterface mySample) {
        super();
        this.sample = mySample;
        this.parentFrame = myFrame;
        refreshTableData();
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
     * @return
     */
    @Override
    public int getRowCount() {
        return getData().size();
    }

    /**
     * 
     * @param col
     * @return
     */
    @Override
    public String getColumnName(int col) {
        return columnNames[col].trim();
    }

    /**
     * 
     * @param row
     * @param col
     * @return
     */
    @Override
    public Object getValueAt(int row, int col) {
        return getData().get(row)[col];
    }

    /**
     * 
     * @param c
     * @return
     */
    @Override
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    /*
     * Don't need to implement this method unless table is
     * editable.
     */
    /**
     * 
     * @param row
     * @param col
     * @return
     */
    @Override
    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        if ((col == 0) || (col == 1) || (col == 2) || (col == 3))//buttons and combobox are clickable
        {
            return true;
        } else {
            return false;
        }
    }

    /*
     * Don't need to implement this method unless table's
     * data can change.
     */
    /**
     * 
     * @param value
     * @param row
     * @param col
     */
    @Override
    public void setValueAt(Object value, int row, int col) {
        try {
            getData().get(row)[col] = value;
        } catch (Exception e) {
        }
        fireTableCellUpdated(row, col);
    }

    /**
     *
     *
     */
    public final void refreshTableData() {
        data =new ArrayList<>();

        // used to control display of aliquot buttons
        String saveAliquotNum = "0";

        Vector<ETFractionInterface> myFractions = getFractionsSorted();

        for (int row = 0; row < myFractions.size(); row++) {

            Object[] myFractionData = ((UPbFractionI) myFractions.get(row)).getFractionTableRowData();
            data.add(myFractionData);

            // place controls in data
            JButton tempJB =
                    new EditFractionButton((String) myFractionData[FractionButtonColumn], row, true);
            tempJB.addActionListener(new EditFractionListener(myFractions.get(row)));
            getData().get(row)[FractionButtonColumn] = tempJB;

            if (!((String) getData().get(row)[aliquotButtonColumn]).equalsIgnoreCase(saveAliquotNum)) {
                // display aliquot button
                // get name of aliquot also
                String aliquotName =
                        getSample().getAliquots().
                        get(Integer.parseInt(((String) getData().get(row)[aliquotButtonColumn])) - 1).getAliquotName();

                saveAliquotNum = (String) getData().get(row)[aliquotButtonColumn];
                getData().get(row)[aliquotButtonColumn] =
                        new EditAliquotButton(
                        aliquotName,
                        (String) myFractionData[aliquotButtonColumn],
                        row);
            } else {
                getData().get(row)[aliquotButtonColumn] =
                        new EditAliquotButton(
                        "",
                        (String) myFractionData[aliquotButtonColumn],
                        row);
            }

            getData().get(row)[fractionSelectorCheckboxColumn] =
                    new SelectFractionCheckBox(
                    (Boolean) myFractionData[fractionSelectorCheckboxColumn], row);


            tempJB =
                    new EditFractionButton("N", row, (Boolean) myFractionData[fractionNotesButtonColumn]);
            tempJB.addActionListener(new ShowFractionNotesListener(myFractions.get(row)));
            
            
            tuneNotesButton(tempJB, myFractions.get(row).getFractionNotes());
            
            getData().get(row)[fractionNotesButtonColumn] = tempJB;

        }
        super.fireTableDataChanged();

    }

    private Vector<ETFractionInterface> getFractionsSorted() {
        // here we sort the fractions so that they appear in alphabetical order by aliquot
        Vector<ETFractionInterface> temp = sample.getFractions();
        Collections.sort(temp, ETFractionInterface.FRACTION_ID_ORDER);
        return temp;
    }

    private SampleInterface getSample() {
        return sample;
    }

    /**
     * 
     * @param sample
     */
    public void setSample(SampleInterface sample) {
        this.sample = sample;
    }

    class ShowFractionNotesListener implements ActionListener {

        private ETFractionInterface fraction;

        public ShowFractionNotesListener(ETFractionInterface fraction) {
            this.fraction = fraction;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JDialog notesDialog = new FractionNotesDialog(parentFrame, true, fraction);
            notesDialog.setLocation(parentFrame.getX() + 350, parentFrame.getY() + 350);
            notesDialog.setVisible(true);
            JButton tempJB = (JButton) e.getSource();
            tuneNotesButton(tempJB, fraction.getFractionNotes());
            tempJB.repaint();
        }
    }

    private void tuneNotesButton(JButton tempJB, String notes) {
        if (notes.length() == 0) {
            tempJB.setFont(new Font("SansSerif", Font.PLAIN, 11));

        } else {
            tempJB.setFont(new Font("SansSerif", Font.BOLD, 11));
        }
        tempJB.setToolTipText(notes);
    }

    class EditFractionListener implements ActionListener {

        private ETFractionInterface fraction;

        public EditFractionListener(ETFractionInterface fraction) {
            this.fraction = fraction;
        }

        public void actionPerformed(ActionEvent e) {
            if ((e != null)) {
                // here we edit the fraction and refresh the table
                parentFrame.editFraction(fraction, 8);// kwikitab
                parentFrame.updateReportTable(false, false, "");
            }
        }
    }

    class EditAliquotButton extends JButton implements ActionListener {

        private int myRow = 0;

        private int aliquotNum = -1;

        /**
         * Creates a button with the supplied title, and adds itself
         * as its own ActionListener.
         *
         * @param szText Button title.
         */
        public EditAliquotButton(String szText, String aliquotNum, int row) {
            super(szText);
            this.myRow = row;
            setAliquotNum(Integer.parseInt(aliquotNum));
            setMargin(new Insets(1, 1, 1, 1));
            setFont(new Font("Serif", Font.BOLD, 14));
            setToolTipText("Click to edit aliquot.");
            setBorder(BorderFactory.createRaisedBevelBorder());
            setOpaque(true);
            setBackground(ReduxConstants.myAliquotGrayColor);
            setForeground(Color.BLACK);
            setContentAreaFilled(true);

            addActionListener(this);
        }

        /**
         * Invoked when this button is pressed;
         *
         * @param e      ActionEvent that fired this method.
         */
        @Override
        public void actionPerformed(ActionEvent e) {
            if ((e != null) && (e.getSource() == this)) {
                // here we edit the aliquot and refresh the table
                parentFrame.editAliquotByNumber(getAliquotNum());
            }
            parentFrame.rebuildFractionDisplays(true, false);//changed to true may 2012false);
        }

        public int getMyRow() {
            return myRow;
        }

        public void setMyRow(int myRow) {
            this.myRow = myRow;
        }

        public int getAliquotNum() {
            return aliquotNum;
        }

        public void setAliquotNum(int aliquotNum) {
            this.aliquotNum = aliquotNum;
        }
    }

    class SelectFractionCheckBox extends JCheckBox implements ActionListener {

        private int myRow = 0;

        /**
         * Creates a JCheckBox with supplied state, and adds itself
         * as its own ActionListener.
         *
         * @param szText isSelected item.
         */
        public SelectFractionCheckBox(Boolean isSelected, int row) {
            super();
            this.myRow = row;
            setSelected(isSelected);
            setFont(new Font("Serif", Font.BOLD, 14));
            setToolTipText("Click to toggle Fraction selection.");
            setBackground(ReduxConstants.ColorOfRedux);
            setBorder(BorderFactory.createRaisedBevelBorder());
            setHorizontalAlignment(CENTER);
            addActionListener(this);
            setEnabled(true);
            setSize(30, getWidth());
        }

        /**
         * Invoked when this button is pressed;
         *
         * @param e   ActionEvent that fired this method.
         */
        public void actionPerformed(ActionEvent e) {
            if ((e != null) && (e.getSource() == this)) {
                // here we toggle fraction selection (via isRejected) and refresh table
                getSample().getFractions().get(myRow).//
                        setRejected(!this.isSelected());
                
                getParentFrame().updateReportTable(false, false, "");//.rebuildFractionDisplays(false);
            }
//            getParentFrame().updateReportTable( false);//.rebuildFractionDisplays(false);

        }

    }

    private ArrayList<Object[]> getData() {
        return data;
    }

    /**
     * 
     * @return
     */
    public ETReduxFrame getParentFrame() {
        return parentFrame;
    }

    /**
     * 
     * @param parentFrame
     */
    public void setParentFrame(ETReduxFrame parentFrame) {
        this.parentFrame = parentFrame;
    }
}



