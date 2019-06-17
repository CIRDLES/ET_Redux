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
package org.earthtime.plots.evolution.openSystem;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.LineBorder;
import static org.earthtime.UPb_Redux.ReduxConstants.SEAWATER_GREEN;

/**
 *
 * @author James F. Bowring
 */
public class OpenSystemModelDataView extends JPanel {

    private final static int PANEL_HEIGHT = 250;
    private JPopupMenu popup;
    private JTable table;
    private JScrollPane tablePane;
    private OpenSystemIsochronTableModel model;

    private OpenSystemModelDataView() {
        this(new OpenSystemIsochronTableModel());
    }

    public OpenSystemModelDataView(OpenSystemIsochronTableModel model) {
        this.model = model;
        initComponent();
    }

    private void initComponent() {

        //create the model
        if (model == null) {
            model = new OpenSystemIsochronTableModel();
        }

        //create the table
        table = new JTable(model);
        table.getColumnModel().getColumn(0).setPreferredWidth(70);
        table.getColumnModel().getColumn(1).setPreferredWidth(60);
        table.getColumnModel().getColumn(2).setPreferredWidth(60);
        table.getColumnModel().getColumn(3).setPreferredWidth(60);
        table.setRowSelectionAllowed(false);
        table.setColumnSelectionAllowed(false);
        table.setShowGrid(true);
        table.setGridColor(Color.blue);
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

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

        table.setPreferredSize(new Dimension(250, 200));
        table.setPreferredScrollableViewportSize(new Dimension(250, 100));
        tablePane = new JScrollPane(table);
        tablePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        JLayeredPane displayPane = new JLayeredPane();
//        displayPane.setBorder(new LineBorder(Color.black, 1, true));
        add(displayPane);

        displayPane.setLayout(new BorderLayout(10, 10));

        String seawaterModelName = null;
        try {
            seawaterModelName = "Seawater model:  " + model.getSeaWaterInitialDelta234UTableModel().getNameAndVersion();
        } catch (Exception e) {
            seawaterModelName = "Seawater model:  NONE";
        }

        JPanel seawaterPanel = new JPanel(new BorderLayout(1, 1));
        JLabel seawaterModelNameLabel = new JLabel(seawaterModelName);
        seawaterModelNameLabel.setForeground(SEAWATER_GREEN);
        seawaterPanel.add(seawaterModelNameLabel, BorderLayout.NORTH);

        JLabel openSystemIsochronModelLabel = new JLabel("Open system isochron model:");
        seawaterPanel.add(openSystemIsochronModelLabel, BorderLayout.SOUTH);
        
        seawaterPanel.validate();
        displayPane.add(seawaterPanel, BorderLayout.NORTH);

        displayPane.add(tablePane, BorderLayout.CENTER);

        JPanel choicePanel = new JPanel(new BorderLayout(1, 1));
        JCheckBox showSeawaterCB = new JCheckBox("Plot Seawater model");
        showSeawaterCB.setSelected(model.isShowSeawaterModel());
        showSeawaterCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.setShowSeawaterModel(!model.isShowSeawaterModel());
            }
        });
        choicePanel.add(showSeawaterCB, BorderLayout.WEST);

        JCheckBox showIsochronsCB = new JCheckBox("Plot Isochrons");
        showIsochronsCB.setSelected(model.isShowOpenIsochrons());
        showIsochronsCB.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                model.setShowOpenIsochrons(!model.isShowOpenIsochrons());
            }
        });
        choicePanel.add(showIsochronsCB, BorderLayout.EAST);
        
        JButton colorChooserButton = new JButton("Choose Isochron Plot Color");
        colorChooserButton.setOpaque(true);
        colorChooserButton.setBackground(model.getDisplayColor());
        colorChooserButton.setForeground(Color.white);
        colorChooserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color newColor = JColorChooser.showDialog(
                     null,
                     "Choose Isochron Plot Color",
                     model.getDisplayColor());
                model.setDisplayColor(newColor);
                colorChooserButton.setBackground(newColor);
                colorChooserButton.repaint();
            }
        });
        
        choicePanel.add(colorChooserButton, BorderLayout.SOUTH);
        
        
        choicePanel.validate();
        displayPane.add(choicePanel, BorderLayout.SOUTH);
    }

    /**
     * @param model the model to set
     */
    public void setModel(OpenSystemIsochronTableModel model) {
        this.model = model;
        if (table != null) {
            table.setModel(model);
        }
    }

    public static void main(String[] args) {
        JFrame f = new JFrame("test");
        f.setSize(300, PANEL_HEIGHT);
        f.getContentPane().add(new OpenSystemModelDataView(), BorderLayout.CENTER);
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        f.setVisible(true);
    }
}
