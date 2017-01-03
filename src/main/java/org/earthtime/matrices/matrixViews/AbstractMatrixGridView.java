/*
 * AbstractMatrixGridView.java
 *
 * Created Mar 4, 2012
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
package org.earthtime.matrices.matrixViews;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.DefaultCellEditor;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.table.JTableHeader;
import org.earthtime.UPb_Redux.ReduxConstants;
import org.earthtime.matrices.matrixModels.AbstractMatrixModel;

/**
 *
 * @author James F. Bowring
 */
public abstract class AbstractMatrixGridView extends JLayeredPane {

    /**
     *
     */
    public static int COL_WIDTH = 120;

    /**
     *
     */
    public int preferred_COL_WIDTH;

    /**
     *
     */
    public int x_Offset;
    /**
     *
     */
    public static int ROW_HEIGHT = 25;

    /**
     *
     */
    public int preferred_ROW_HEIGHT;

    /**
     *
     */
    public int y_Offset;
    /**
     *
     */
    private AbstractMatrixModel matrixModel;
    /**
     *
     */
    protected JTable table;
    private JTableHeader tableHeader;
    private JLabel descriptionLabel;

    /**
     *
     */
    protected boolean editable;

    /**
     *
     */
    protected boolean showTableOnly;

    /**
     *
     */
    protected  AbstractMatrixTableViewModel matrixTableViewModelEditable;

    /**
     *
     * @param matrixModel
     * @param editable
     * @param showTableOnly the value of showTableOnly
     */
    public AbstractMatrixGridView(AbstractMatrixModel matrixModel, boolean editable, boolean showTableOnly) {

        this.matrixModel = matrixModel;
        this.editable = editable;
        this.showTableOnly = showTableOnly;

        x_Offset = showTableOnly ? 0 : 25;
        y_Offset = showTableOnly ? 0 : 25;
        preferred_COL_WIDTH = showTableOnly ? 77 : COL_WIDTH;
        preferred_ROW_HEIGHT = showTableOnly ? 20 : ROW_HEIGHT;

    }

    /**
     *
     */
    public void initGridView() {

        descriptionLabel = //
                new JLabel("<html><u>" + getMatrixModel().getLevelName() + "</u>" //
                        + "&nbsp;(" + (editable ? "" : "not ") + "editable)"
                        + "&nbsp;&nbsp;&nbsp;&nbsp;Note: covariance matrix " //
                        + (String) (getMatrixModel().isCovMatrixSymmetricAndPositiveDefinite() ? "IS" : "IS NOT")
                        + " positive definite." + "</html>");

        descriptionLabel.setBounds(5, 0, 500, 25);
        descriptionLabel.setFont(ReduxConstants.sansSerif_12_Bold);

        if (!showTableOnly) {
            this.add(descriptionLabel);
        }

        table.createDefaultColumnsFromModel();
        tableHeader = table.getTableHeader();

        table.setDefaultEditor(Double.class,
                new DoubleEditor());

        refreshGridView();

        this.add(tableHeader);
        this.add(table);
    }

    /**
     * @return the matrixModel
     */
    public AbstractMatrixModel getMatrixModel() {
        return matrixModel;
    }

    /**
     *
     */
    public class DoubleEditor extends DefaultCellEditor {

        JTextField myTextField;

        /**
         *
         */
        public DoubleEditor() {
            super(new JTextField());
            myTextField = (JTextField) getComponent();
        }

        //Override to invoke setValue on the formatted text field.
        /**
         *
         * @param table
         * @param value
         * @param isSelected
         * @param row
         * @param column
         * @return
         */
        @Override
        public Component getTableCellEditorComponent(JTable table,
                Object value, boolean isSelected,
                int row, int column) {
            JTextField textField
                    = (JTextField) super.getTableCellEditorComponent(
                            table, value, isSelected, row, column);
            textField.setText(Double.toString((Double) value));
            return textField;
        }

        /**
         *
         * @return
         */
        @Override
        public Object getCellEditorValue() {
            JTextField textField = (JTextField) getComponent();
            return Double.valueOf(textField.getText());
        }
    }

    private void refreshGridView() {

        Font tableFont = showTableOnly ? ReduxConstants.sansSerif_10_Bold : ReduxConstants.sansSerif_11_Bold;

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(preferred_COL_WIDTH);
        }
        table.setBorder(new LineBorder(Color.black));
        table.setRowHeight(preferred_ROW_HEIGHT);

        table.setPreferredSize(new Dimension((getMatrixModel().getCols().size()+1) * (preferred_COL_WIDTH + 0),
                getMatrixModel().getRows().size() * preferred_ROW_HEIGHT));

        table.setGridColor(Color.gray);

        tableHeader.setBounds(x_Offset, y_Offset, (getMatrixModel().getCols().size()+1) * (preferred_COL_WIDTH + 0), preferred_ROW_HEIGHT);
        tableHeader.setFont(tableFont);//setFont( ReduxConstants.sansSerif_12_Bold );
        tableHeader.setBorder(new LineBorder(Color.black));
        tableHeader.setReorderingAllowed(false);

        table.setBounds(x_Offset,//
                y_Offset + preferred_ROW_HEIGHT, //
                (getMatrixModel().getCols().size()+1) * (preferred_COL_WIDTH + 0),
                getMatrixModel().getRows().size() * preferred_ROW_HEIGHT);
        table.setFont(tableFont);//ReduxConstants.sansSerif_12_Bold );

        this.setBorder(new LineBorder(Color.black));
        this.setBounds( //
                0, //
                0, //
                table.getWidth() + x_Offset + x_Offset,
                table.getHeight() + y_Offset + y_Offset + preferred_ROW_HEIGHT);

        tableHeader.validate();
        table.validate();
    }

    /**
     *
     */
    public void showTableHideFirstColLastRow() {

        // matrix may be empty if uncertainties are zero
        try {
            table.getColumnModel().getColumn(1).setMinWidth(0);
            table.getColumnModel().getColumn(1).setPreferredWidth(0);
        } catch (Exception e) {
        }
        // hide last row
        table.setSize(new Dimension((getMatrixModel().getCols().size() - 0) * preferred_COL_WIDTH, (getMatrixModel().getRows().size() - 1) * preferred_ROW_HEIGHT));
        tableHeader.setSize(new Dimension((getMatrixModel().getCols().size() - 0) * preferred_COL_WIDTH, preferred_ROW_HEIGHT));

        this.setSize(//
                table.getWidth() + x_Offset + x_Offset,
                table.getHeight() + y_Offset + y_Offset + preferred_ROW_HEIGHT);

        tableHeader.validate();
        table.getTableHeader().validate();
    }

    /**
     * @param matrixModel the matrixModel to set
     */
    public void setMatrixModel(AbstractMatrixModel matrixModel) {
        this.matrixModel = matrixModel;
        ((AbstractMatrixTableViewModel) table.getModel()).setMatrixModel(matrixModel);
    }

    /**
     * @return the table
     */
    public JTable getTable() {
        return table;
    }

    /**
     *
     */
    public void rebuildTableModel() {
        ((AbstractMatrixTableViewModel) table.getModel()).buildTableView();
        this.remove(table);
        this.remove(tableHeader);
        this.remove(descriptionLabel);
        this.validate();
        initGridView();
        table.repaint();
    }
}
