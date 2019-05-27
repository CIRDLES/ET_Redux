/*
 * IsochronsSelectorDialog.java
 *
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
package org.earthtime.plots.evolution.seaWater;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JLayeredPane;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import org.earthtime.plots.AbstractDataView;

/**
 *
 * @author James F. Bowring
 */
public class SeaWaterDataView extends JFrame {

    private JPopupMenu popup;
    private JTable table;
    private JScrollPane tablePane;
    private SeaWaterInitialDelta234UTableModel model;
    private AbstractDataView seaWaterDelta234UGraph;

    public SeaWaterDataView() {
        initComponent();
    }

    private void initComponent() {

        //create the model
        if (model == null) {
            model = new SeaWaterInitialDelta234UTableModel();
        }

        //create the table
        table = new JTable(model);
        table.setShowGrid(true);
        table.setGridColor(Color.blue);
        table.setFillsViewportHeight(true);

        popup = new JPopupMenu();
        JMenuItem menuItemAdd = new JMenuItem("Add Age row");
        menuItemAdd.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.addRow();
                model.fireTableDataChanged();
            }
        });
        popup.add(menuItemAdd);

        JMenuItem menuItemDelete = new JMenuItem("Delete Age row");
        menuItemDelete.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.removeRow(table.getSelectedRow());
                model.fireTableDataChanged();
                seaWaterDelta234UGraph.refreshPanel(true);
            }
        });
        popup.add(menuItemDelete);

        table.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) {
                    JTable source = (JTable) e.getSource();
                    int row = source.rowAtPoint(e.getPoint());
                    int column = source.columnAtPoint(e.getPoint());

                    if (!source.isRowSelected(row)) {
                        source.changeSelection(row, column, false, false);
                    }

                    popup.show(e.getComponent(), e.getX(), e.getY());
                }

            }
        });

        table.getDefaultEditor(Double.class).addCellEditorListener(new CellEditorListener() {
            @Override
            public void editingStopped(ChangeEvent e) {
                seaWaterDelta234UGraph.refreshPanel(true);
            }

            @Override
            public void editingCanceled(ChangeEvent e) {
                seaWaterDelta234UGraph.refreshPanel(true);
            }
        });

        table.setPreferredSize(new Dimension(500, 250));
        table.setPreferredScrollableViewportSize(table.getPreferredSize());
        tablePane = new JScrollPane(table);

        JLayeredPane displayPane = new JLayeredPane();
        getContentPane().add(displayPane);

        displayPane.setLayout(new BorderLayout(10, 10));

        displayPane.add(tablePane, BorderLayout.NORTH);

        seaWaterDelta234UGraph = new SeaWaterDelta234UGraph(model);
        seaWaterDelta234UGraph.setPreferredSize(new Dimension(500, 300));
        seaWaterDelta234UGraph.preparePanel(true);
        seaWaterDelta234UGraph.repaint();
        displayPane.add(seaWaterDelta234UGraph, BorderLayout.SOUTH);

    }

    public static void main(String[] args) {
        SeaWaterDataView frame = new SeaWaterDataView();
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * @param model the model to set
     */
    public void setModel(SeaWaterInitialDelta234UTableModel model) {
        this.model = model;
        if (table != null){
            table.setModel(model);
            SeaWaterDelta234UGraph.setModel(model);
        }
        seaWaterDelta234UGraph.preparePanel(true);
        seaWaterDelta234UGraph.repaint();
    }

}
