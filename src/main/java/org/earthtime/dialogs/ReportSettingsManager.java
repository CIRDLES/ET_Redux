/*
 * ReportSettingsManager.java
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
package org.earthtime.dialogs;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Map;
import java.util.Vector;
import javax.swing.AbstractButton;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.earthtime.UPb_Redux.reports.ReportCategory;
import org.earthtime.UPb_Redux.reports.ReportColumn;
import org.earthtime.beans.ET_JButton;
import org.earthtime.dataDictionaries.ReportSpecifications;
import org.earthtime.reportViews.ReportListItemI;
import org.earthtime.reportViews.ReportPainterI;
import org.earthtime.reports.ReportCategoryInterface;
import org.earthtime.reports.ReportColumnInterface;
import org.earthtime.reports.ReportSettingsInterface;

/**
 *
 * @author  James F. Bowring
 */
public class ReportSettingsManager extends DialogEditor {

    // Fields
    private ReportSettingsInterface reportSettings;
    private final Frame parent;
    @SuppressWarnings("UseOfObsoleteCollectionType")
    private Vector<ReportListItemI> catList;
    private Vector<ReportListItemI> colList;
    private UnitsActionListener unitsActionListener;

    /** Creates new form ReportSettingsManager
     * @param parent
     * @param modal 
     * @param reportSettings  
     */
    public ReportSettingsManager ( //
            Frame parent,
            boolean modal,
            ReportSettingsInterface reportSettings ) {
        super( parent, modal );

        // DialogEditor attribute
        amOpen = true;

        this.parent = parent;

        setLocationRelativeTo( parent );
        initComponents();

        this.reportSettings = reportSettings;
        populateCategoryList();
    }

    private void populateCategoryList () {
        // extract category names
        Map<Integer, ReportCategoryInterface> cats = reportSettings.getReportCategoriesInOrder();
        catList = new Vector<>();

        for (int c = 0; c < cats.size(); c ++) {
            catList.add(cats.get( c ));
        }

        categories_list.setCellRenderer( new ReportListRenderer() );
        categories_list.setListData( catList );
        categories_list.addListSelectionListener( new ReportListSelectionListener() );
        categories_list.setSelectedIndex( 0 );
    }

    private void populateColumnList ( ReportCategoryInterface cat ) {
        // extract column names
        Map<Integer, ReportColumnInterface> cols = cat.getCategoryColumnOrder();
        colList = new Vector<>();

        for (int c = 0; c < cols.size(); c ++) {
            colList.add( cols.get( c ) );
        }

        columns_list.setSelectedIndex( -1 );
        columns_list.setCellRenderer( new ReportListRenderer() );
        columns_list.setListData( colList );
        columns_list.addListSelectionListener( new ReportListSelectionListener() );
        columns_list.grabFocus();
        columns_list.setSelectedIndex( 0 );

    }

    class ReportListRenderer extends DefaultListCellRenderer {

        /** Creates a new instance of LocaleRenderer */
        public ReportListRenderer () {
        }

        @Override
        public Component getListCellRendererComponent (
                JList list,
                Object value,
                int index, boolean isSelected,
                boolean cellHasFocus ) {

            super.getListCellRendererComponent( list,
                    value,
                    index,
                    isSelected,
                    cellHasFocus );

            ReportListItemI c = (ReportListItemI) value;
            try {
                if (c.isVisible()) {
                    setText(c.getDisplayName());
                } else {
                    setText("<html><strike>" + prepareStringForHTML(c.getDisplayName()) + "</strike></html>");
                }
            } catch (Exception e) {
            }
            return this;
        }

        private String prepareStringForHTML ( String text ) {
            String retval = text;

            retval = retval.replace( "<", "&lt;" );
            retval = retval.replace( ">", "&gt;" );

            return retval;
        }
    }

    class ReportListSelectionListener implements ListSelectionListener {
        // This method is called each time the user changes the set of selected items

        @Override
        public void valueChanged ( ListSelectionEvent evt ) {
            // When the user releases the mouse button and completes the selection,
            // getValueIsAdjusting() becomes false

            // save current
            try {
                for (ReportListItemI li : colList) {
                    li.setPositionIndex( colList.indexOf( li ) );
                }
            } catch (Exception e) {
            }

            if (  ! evt.getValueIsAdjusting() ) {
                JList list = (JList) evt.getSource();

                if ( list.getName().equalsIgnoreCase( "cat" ) ) {
                    // set buttons' enabled status
                    int selectedIndex = list.getSelectedIndex();
                    if ( selectedIndex > -1 ) {
                        for (Enumeration e = categoryChanges_buttonGroup.getElements(); e.hasMoreElements();) {
                            JButton jb = (JButton) e.nextElement();
                            jb.setEnabled( true );
                            if ( (jb.getName().equalsIgnoreCase( "up" )) && (selectedIndex <= 1) ) { //prevents fraction category from being displaced
                                jb.setEnabled( false );
                            }
                            if ( ((jb.getName().equalsIgnoreCase( "down" )) && (selectedIndex == (catList.size() - 1)))
                                    || (selectedIndex == 0) ) // this prevents movement of fraction category from first place
                            {
                                jb.setEnabled( false );
                            }
                        }
                    }
                    // populate columnList
                    populateColumnList( (ReportCategory) list.getSelectedValue() );



                } else { // columnsList
                    // set buttons' enabled status
                    int selectedIndex = list.getSelectedIndex();
                    if ( selectedIndex > -1 ) {
                        for (Enumeration e = columnChanges_buttonGroup.getElements(); e.hasMoreElements();) {
                            JButton jb = (JButton) e.nextElement();
                            jb.setEnabled( true );
                            if ( (jb.getName().equalsIgnoreCase( "up" )) && (selectedIndex == 0) ) {
                                jb.setEnabled( false );
                            }
                            if ( (jb.getName().equalsIgnoreCase( "down" )) && (selectedIndex == (colList.size() - 1)) ) {
                                jb.setEnabled( false );
                            }
                        }
                    }
                    // populate columnDetails
                    populateColumnsDetails( (ReportColumn) list.getSelectedValue() );

                }
            }
        }
    }

    /**
     * 
     * @param reportColumn
     */
    public void populateColumnsDetails ( ReportColumn reportColumn ) {
        if ( reportColumn != null ) {

            columnName_label.setText( reportColumn.getDisplayName() );

            // oct 2009 added in functionality of units chooser
            if (  ! reportColumn.getUnits().equalsIgnoreCase( "" ) ) {
                unitsChooser_ComboBox.setVisible( true );
                unitsChooser_ComboBox.removeActionListener( unitsActionListener );
                unitsChooser_ComboBox.removeAllItems();
                // decide flavor of units
                String[] unitsContents;
                try {
                    if ( ReportSpecifications.unitsType.get( reportColumn.getUnits() ).equalsIgnoreCase( "mass" ) ) {
                        unitsContents = ReportSpecifications.massUnits;
                    } else if ( ReportSpecifications.unitsType.get( reportColumn.getUnits() ).equalsIgnoreCase( "date" ) ) {
                        unitsContents = ReportSpecifications.dateUnits;
                    } else {
                        unitsContents = ReportSpecifications.concUnits;
                    }
                    for (int i = 0; i < unitsContents.length; i ++) {
                        unitsChooser_ComboBox.addItem( unitsContents[i] );
                    }
                    unitsChooser_ComboBox.setSelectedItem( reportColumn.getUnits() );
                    unitsActionListener = new UnitsActionListener();
                    unitsChooser_ComboBox.addActionListener( unitsActionListener );
                    unitsActionListener.setReportColumn( reportColumn );
                } catch (Exception e) {
                }

            } else {
                unitsChooser_ComboBox.setVisible( false );
            }
            // end added units functinality

            if ( reportColumn.isDisplayedWithArbitraryDigitCount() ) {
                valueModeArbitrary_rButton.setSelected( true );
            } else {
                valueModeSigFig_rButton.setSelected( true );
            }

            SpinnerModel valueDigits_spinnerModel =
                    new SpinnerNumberModel(//
                    (Number) Integer.valueOf( reportColumn.getCountOfSignificantDigits() ),//
                    0, 9, 1 );
            valueDigits_spinner.setModel( valueDigits_spinnerModel );

            valueDigitsCount_label.setText( "digit count = " );
            valueDigits_spinner.setEnabled( true );

            if ( reportColumn.getUncertaintyColumn() != null ) {
                uncertaintyColName_label.setVisible( true );
                uncertaintyDigitCount_label.setVisible( true );
                uncertaintyDigits_spinner.setVisible( true );

                if ( reportColumn.getUncertaintyColumn().isDisplayedWithArbitraryDigitCount() ) {
                    unctModeArbitrary_rButton.setSelected( true );
                } else {
                    unctModeSigFig_rButton.setSelected( true );
                    // check for both value and unct in sigfig mode
                    if (  ! reportColumn.isDisplayedWithArbitraryDigitCount() ) {
                        valueDigitsCount_label.setText( "digit count = n/a" );
                        valueDigits_spinner.setEnabled( false );
                    }
                }
                unctModeArbitrary_rButton.setVisible( true );
                unctModeSigFig_rButton.setVisible( true );

                uncertaintyVisible_chkBox.setVisible( true );

                uncertaintyColName_label.setText( reportColumn.getUncertaintyColumn().getDisplayName() );
                uncertaintyVisible_chkBox.setSelected( reportColumn.getUncertaintyColumn().isVisible() );

                SpinnerModel uncertaintyDigits_spinnerModel =
                        new SpinnerNumberModel(//
                        (Number) Integer.valueOf( reportColumn.getUncertaintyColumn().getCountOfSignificantDigits() ),//
                        1, 9, 1 );
                uncertaintyDigits_spinner.setModel( uncertaintyDigits_spinnerModel );

            } else { // no uncertainty column
                uncertaintyColName_label.setVisible( false );
                uncertaintyDigitCount_label.setVisible( false );
                uncertaintyDigits_spinner.setVisible( false );
                valueDigits_spinner.setVisible( true );
                valueDigits_spinner.setModel( valueDigits_spinnerModel );
                unctModeArbitrary_rButton.setVisible( false );
                unctModeSigFig_rButton.setVisible( false );
                uncertaintyVisible_chkBox.setVisible( false );
            }
        }
    }

    class UnitsActionListener implements ActionListener {

        private ReportColumn reportColumn;

        public void actionPerformed ( ActionEvent e ) {
            if ( (reportColumn != null)
                    && (((JComboBox) e.getSource()).getSelectedItem() != null) ) {
                reportColumn.setUnits( (String) ((JComboBox) e.getSource()).getSelectedItem() );
            }
        }

        public void setReportColumn ( ReportColumn reportColumn ) {
            this.reportColumn = reportColumn;
        }
    }

    private void setVisibleAllColumnsInCategory ( boolean visible ) {
        for (int i = 0; i < columns_list.getModel().getSize(); i ++) {
            columns_list.getModel().getElementAt( i ).setVisible( visible );
        }
        columns_list.repaint();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        categoryChanges_buttonGroup = new javax.swing.ButtonGroup();
        columnChanges_buttonGroup = new javax.swing.ButtonGroup();
        uncertaintyMode = new javax.swing.ButtonGroup();
        valueMode = new javax.swing.ButtonGroup();
        categories_scrollPane = new javax.swing.JScrollPane();
        categories_list = new javax.swing.JList<>();
        buttonsPanel = new javax.swing.JPanel();
        close_button = new ET_JButton();
        apply_button = new ET_JButton();
        jLabel1 = new javax.swing.JLabel();
        categoryMoveUp_button = new ET_JButton();
        categoryMoveDown_button = new ET_JButton();
        categoryHideShow_button = new ET_JButton();
        columns_scrollPane = new javax.swing.JScrollPane();
        columns_list = new javax.swing.JList<>();
        jLabel2 = new javax.swing.JLabel();
        columnHideShow_button = new ET_JButton();
        columnMoveDown_button = new ET_JButton();
        columnMoveUp_button = new ET_JButton();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        columnName_label = new javax.swing.JLabel();
        valueDigitsCount_label = new javax.swing.JLabel();
        uncertaintyColName_label = new javax.swing.JLabel();
        uncertaintyVisible_chkBox = new javax.swing.JCheckBox();
        uncertaintyDigitCount_label = new javax.swing.JLabel();
        uncertaintyDigits_spinner = new javax.swing.JSpinner();
        valueDigits_spinner = new javax.swing.JSpinner();
        unctModeSigFig_rButton = new javax.swing.JRadioButton();
        unctModeArbitrary_rButton = new javax.swing.JRadioButton();
        valueModeSigFig_rButton = new javax.swing.JRadioButton();
        valueModeArbitrary_rButton = new javax.swing.JRadioButton();
        unitsChooser_ComboBox = new javax.swing.JComboBox<>();
        categoryShowAllColumns_button = new ET_JButton();
        categoryHideAllColumns_button = new ET_JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Report Settings Manager");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                CloseDialog(evt);
            }
        });

        categories_list.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        categories_list.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        categories_list.setName("cat"); // NOI18N
        categories_list.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                categories_listMouseClicked(evt);
            }
        });
        categories_scrollPane.setViewportView(categories_list);

        buttonsPanel.setBackground(new java.awt.Color(252, 236, 235));
        buttonsPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        close_button.setForeground(new java.awt.Color(255, 51, 0));
        close_button.setText("OK");
        close_button.setMargin(new java.awt.Insets(0, 1, 0, 1));
        close_button.setPreferredSize(new java.awt.Dimension(140, 23));
        close_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                close_buttonActionPerformed(evt);
            }
        });

        apply_button.setForeground(new java.awt.Color(255, 51, 0));
        apply_button.setText("Apply");
        apply_button.setMargin(new java.awt.Insets(0, 1, 0, 1));
        apply_button.setPreferredSize(new java.awt.Dimension(140, 23));
        apply_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                apply_buttonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout buttonsPanelLayout = new org.jdesktop.layout.GroupLayout(buttonsPanel);
        buttonsPanel.setLayout(buttonsPanelLayout);
        buttonsPanelLayout.setHorizontalGroup(
            buttonsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, buttonsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(apply_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 196, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(close_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 196, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        buttonsPanelLayout.setVerticalGroup(
            buttonsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(buttonsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(apply_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(close_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        jLabel1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel1.setText("  Categories  ( double-click to hide / show )");

        categoryMoveUp_button.setText("move up");
        categoryChanges_buttonGroup.add(categoryMoveUp_button);
        categoryMoveUp_button.setEnabled(false);
        categoryMoveUp_button.setName("up"); // NOI18N
        categoryMoveUp_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                categoryMoveUp_buttonActionPerformed(evt);
            }
        });

        categoryMoveDown_button.setText("move down");
        categoryChanges_buttonGroup.add(categoryMoveDown_button);
        categoryMoveDown_button.setEnabled(false);
        categoryMoveDown_button.setName("down"); // NOI18N
        categoryMoveDown_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                categoryMoveDown_buttonActionPerformed(evt);
            }
        });

        categoryHideShow_button.setText("hide / show");
        categoryChanges_buttonGroup.add(categoryHideShow_button);
        categoryHideShow_button.setEnabled(false);
        categoryHideShow_button.setName("hideshow"); // NOI18N
        categoryHideShow_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                categoryHideShow_buttonActionPerformed(evt);
            }
        });

        columns_list.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        columns_list.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        columns_list.setName("col"); // NOI18N
        columns_list.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                columns_listMouseClicked(evt);
            }
        });
        columns_scrollPane.setViewportView(columns_list);

        jLabel2.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel2.setText("  Columns for Selected Category  ( double-click to hide / show )");

        columnHideShow_button.setText("hide / show");
        columnChanges_buttonGroup.add(columnHideShow_button);
        columnHideShow_button.setEnabled(false);
        columnHideShow_button.setName("hideshow"); // NOI18N
        columnHideShow_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                columnHideShow_buttonActionPerformed(evt);
            }
        });

        columnMoveDown_button.setText("move down");
        columnChanges_buttonGroup.add(columnMoveDown_button);
        columnMoveDown_button.setEnabled(false);
        columnMoveDown_button.setName("down"); // NOI18N
        columnMoveDown_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                columnMoveDown_buttonActionPerformed(evt);
            }
        });

        columnMoveUp_button.setText("move up");
        columnChanges_buttonGroup.add(columnMoveUp_button);
        columnMoveUp_button.setEnabled(false);
        columnMoveUp_button.setName("up"); // NOI18N
        columnMoveUp_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                columnMoveUp_buttonActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        jLabel3.setText("Column Details");

        jScrollPane2.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        jScrollPane2.setHorizontalScrollBar(null);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        columnName_label.setText("col name");

        valueDigitsCount_label.setText("digit count = ");

        uncertaintyColName_label.setText("unct col name");

        uncertaintyVisible_chkBox.setText("unct col visible for this value");
        uncertaintyVisible_chkBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uncertaintyVisible_chkBoxActionPerformed(evt);
            }
        });

        uncertaintyDigitCount_label.setText("digit count =");

        uncertaintyDigits_spinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                uncertaintyDigits_spinnerStateChanged(evt);
            }
        });

        valueDigits_spinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                valueDigits_spinnerStateChanged(evt);
            }
        });

        uncertaintyMode.add(unctModeSigFig_rButton);
        unctModeSigFig_rButton.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        unctModeSigFig_rButton.setSelected(true);
        unctModeSigFig_rButton.setText("sigfig");

        uncertaintyMode.add(unctModeArbitrary_rButton);
        unctModeArbitrary_rButton.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        unctModeArbitrary_rButton.setText("arbit.");
        unctModeArbitrary_rButton.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                unctModeArbitrary_rButtonStateChanged(evt);
            }
        });

        valueMode.add(valueModeSigFig_rButton);
        valueModeSigFig_rButton.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        valueModeSigFig_rButton.setSelected(true);
        valueModeSigFig_rButton.setText("sigfig");

        valueMode.add(valueModeArbitrary_rButton);
        valueModeArbitrary_rButton.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
        valueModeArbitrary_rButton.setText("arbit.");
        valueModeArbitrary_rButton.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                valueModeArbitrary_rButtonunctModeArbitraryRButton_State_Changed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jPanel1Layout.createSequentialGroup()
                        .add(columnName_label, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 293, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(unitsChooser_ComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 104, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(valueModeSigFig_rButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(valueModeArbitrary_rButton)
                                .add(14, 14, 14)
                                .add(valueDigitsCount_label)
                                .add(3, 3, 3)
                                .add(valueDigits_spinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(uncertaintyColName_label)
                                .add(18, 18, 18)
                                .add(uncertaintyVisible_chkBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 239, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(jPanel1Layout.createSequentialGroup()
                                .add(unctModeSigFig_rButton)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(unctModeArbitrary_rButton)
                                .add(14, 14, 14)
                                .add(uncertaintyDigitCount_label)
                                .add(7, 7, 7)
                                .add(uncertaintyDigits_spinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(293, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(columnName_label)
                    .add(unitsChooser_ComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(valueModeSigFig_rButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(valueModeArbitrary_rButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(valueDigitsCount_label)
                    .add(valueDigits_spinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .add(2, 2, 2)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(uncertaintyColName_label)
                    .add(uncertaintyVisible_chkBox))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(unctModeSigFig_rButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(unctModeArbitrary_rButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(uncertaintyDigits_spinner, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(uncertaintyDigitCount_label))
                .addContainerGap(101, Short.MAX_VALUE))
        );

        jScrollPane2.setViewportView(jPanel1);

        categoryShowAllColumns_button.setText("show ALL");
        columnChanges_buttonGroup.add(categoryShowAllColumns_button);
        categoryShowAllColumns_button.setEnabled(false);
        categoryShowAllColumns_button.setName("hideshow"); // NOI18N
        categoryShowAllColumns_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                categoryShowAllColumns_buttonActionPerformed(evt);
            }
        });

        categoryHideAllColumns_button.setText("hide ALL");
        columnChanges_buttonGroup.add(categoryHideAllColumns_button);
        categoryHideAllColumns_button.setEnabled(false);
        categoryHideAllColumns_button.setName("hideshow"); // NOI18N
        categoryHideAllColumns_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                categoryHideAllColumns_buttonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 456, Short.MAX_VALUE)
            .add(layout.createSequentialGroup()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(12, 12, 12)
                                .add(jLabel3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 210, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(layout.createSequentialGroup()
                                .addContainerGap()
                                .add(categories_scrollPane))
                            .add(layout.createSequentialGroup()
                                .addContainerGap()
                                .add(columns_scrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 261, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                        .add(43, 43, 43)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(categoryShowAllColumns_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 121, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(categoryHideAllColumns_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 121, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(categoryMoveUp_button)
                            .add(categoryMoveDown_button)
                            .add(categoryHideShow_button)
                            .add(columnMoveUp_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 111, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(columnMoveDown_button)
                            .add(columnHideShow_button, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .add(layout.createSequentialGroup()
                        .addContainerGap()
                        .add(jScrollPane2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 416, Short.MAX_VALUE))
                    .add(jLabel2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 436, Short.MAX_VALUE)
                    .add(buttonsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(new java.awt.Component[] {categoryHideShow_button, categoryMoveDown_button, categoryMoveUp_button, categoryShowAllColumns_button, columnHideShow_button, columnMoveDown_button, columnMoveUp_button}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.linkSize(new java.awt.Component[] {categories_scrollPane, columns_scrollPane}, org.jdesktop.layout.GroupLayout.HORIZONTAL);

        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .add(jLabel1)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(org.jdesktop.layout.GroupLayout.LEADING, layout.createSequentialGroup()
                        .add(categoryMoveUp_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(categoryMoveDown_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(categoryHideShow_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(layout.createSequentialGroup()
                        .add(categories_scrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 120, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(jLabel2)))
                .add(7, 7, 7)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(layout.createSequentialGroup()
                        .add(columns_scrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 277, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED))
                    .add(layout.createSequentialGroup()
                        .add(columnMoveUp_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(columnMoveDown_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .add(columnHideShow_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(63, 63, 63)
                        .add(categoryShowAllColumns_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.UNRELATED)
                        .add(categoryHideAllColumns_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 25, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .add(12, 12, 12)))
                .add(jLabel3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jScrollPane2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 130, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(buttonsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void close_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_close_buttonActionPerformed
        closeDialog();
        close();
}//GEN-LAST:event_close_buttonActionPerformed

    private void categoryHideShow_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_categoryHideShow_buttonActionPerformed
        categories_list.getSelectedValue().ToggleIsVisible();
        categories_list.repaint();
    }//GEN-LAST:event_categoryHideShow_buttonActionPerformed

    private void categoryMoveUp_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_categoryMoveUp_buttonActionPerformed
        catList.insertElementAt(//
                catList.remove( categories_list.getSelectedIndex() ), //
                categories_list.getSelectedIndex() - 1 );
        categories_list.repaint();
        categories_list.setSelectedIndex( categories_list.getSelectedIndex() - 1 );
    }//GEN-LAST:event_categoryMoveUp_buttonActionPerformed

    private void categoryMoveDown_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_categoryMoveDown_buttonActionPerformed
        catList.insertElementAt(//
                catList.remove( categories_list.getSelectedIndex() ), //
                categories_list.getSelectedIndex() + 1 );
        categories_list.repaint();
        categories_list.setSelectedIndex( categories_list.getSelectedIndex() + 1 );
    }//GEN-LAST:event_categoryMoveDown_buttonActionPerformed

    private void CloseDialog(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_CloseDialog
        closeDialog();
    }//GEN-LAST:event_CloseDialog

    private void columnHideShow_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_columnHideShow_buttonActionPerformed
        columns_list.getSelectedValue().ToggleIsVisible();
        columns_list.repaint();
}//GEN-LAST:event_columnHideShow_buttonActionPerformed

    private void columnMoveDown_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_columnMoveDown_buttonActionPerformed
        colList.insertElementAt(//
                colList.remove( columns_list.getSelectedIndex() ), //
                columns_list.getSelectedIndex() + 1 );
        columns_list.repaint();
        columns_list.setSelectedIndex( columns_list.getSelectedIndex() + 1 );
}//GEN-LAST:event_columnMoveDown_buttonActionPerformed

    private void columnMoveUp_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_columnMoveUp_buttonActionPerformed
        colList.insertElementAt(//
                colList.remove( columns_list.getSelectedIndex() ), //
                columns_list.getSelectedIndex() - 1 );
        columns_list.repaint();
        columns_list.setSelectedIndex( columns_list.getSelectedIndex() - 1 );
}//GEN-LAST:event_columnMoveUp_buttonActionPerformed

    private void uncertaintyVisible_chkBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_uncertaintyVisible_chkBoxActionPerformed
        ((ReportColumnInterface) columns_list.getSelectedValue())//
                .getUncertaintyColumn().setVisible( ((AbstractButton) evt.getSource()).isSelected() );
    }//GEN-LAST:event_uncertaintyVisible_chkBoxActionPerformed

    private void uncertaintyDigits_spinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_uncertaintyDigits_spinnerStateChanged
        ((ReportColumnInterface) columns_list.getSelectedValue())//
                .getUncertaintyColumn().setCountOfSignificantDigits( (Integer) uncertaintyDigits_spinner.getValue() );
    }//GEN-LAST:event_uncertaintyDigits_spinnerStateChanged

    private void valueDigits_spinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_valueDigits_spinnerStateChanged
        ((ReportColumnInterface) columns_list.getSelectedValue())//
                .setCountOfSignificantDigits( (Integer) valueDigits_spinner.getValue() );
}//GEN-LAST:event_valueDigits_spinnerStateChanged

private void valueModeArbitrary_rButtonunctModeArbitraryRButton_State_Changed(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_valueModeArbitrary_rButtonunctModeArbitraryRButton_State_Changed
    ((ReportColumnInterface) columns_list.getSelectedValue())//
            .setDisplayedWithArbitraryDigitCount( valueModeArbitrary_rButton.isSelected() );
    populateColumnsDetails( (ReportColumn) columns_list.getSelectedValue() );
}//GEN-LAST:event_valueModeArbitrary_rButtonunctModeArbitraryRButton_State_Changed

private void categoryShowAllColumns_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_categoryShowAllColumns_buttonActionPerformed
    setVisibleAllColumnsInCategory( true );
}//GEN-LAST:event_categoryShowAllColumns_buttonActionPerformed

private void categoryHideAllColumns_buttonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_categoryHideAllColumns_buttonActionPerformed
    setVisibleAllColumnsInCategory( false );
}//GEN-LAST:event_categoryHideAllColumns_buttonActionPerformed

private void unctModeArbitrary_rButtonStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_unctModeArbitrary_rButtonStateChanged
    ((ReportColumnInterface) columns_list.getSelectedValue())//
            .getUncertaintyColumn().setDisplayedWithArbitraryDigitCount( unctModeArbitrary_rButton.isSelected() );
    populateColumnsDetails( (ReportColumn) columns_list.getSelectedValue() );
}//GEN-LAST:event_unctModeArbitrary_rButtonStateChanged

private void categories_listMouseClicked (java.awt.event.MouseEvent evt) {//GEN-FIRST:event_categories_listMouseClicked
    if ( (evt.getClickCount() == 2) && (categories_list.getSelectedIndex() >= 1) ) {
        categories_list.getSelectedValue().ToggleIsVisible();
        categories_list.repaint();
    }
}//GEN-LAST:event_categories_listMouseClicked

private void columns_listMouseClicked (java.awt.event.MouseEvent evt) {//GEN-FIRST:event_columns_listMouseClicked
    if ( evt.getClickCount() == 2 ) {
        columns_list.getSelectedValue().ToggleIsVisible();
        columns_list.repaint();
    }
}//GEN-LAST:event_columns_listMouseClicked

private void apply_buttonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_apply_buttonActionPerformed

    // reorder categories
    for (ReportListItemI li : catList) {
        li.setPositionIndex( catList.indexOf( li ) );
    }
    for (ReportListItemI li : colList) {
        li.setPositionIndex( colList.indexOf( li ) );
    }

//    String[][] reportFractions = reportSettings.reportAllFractionsByNumberStyle( ((UPbReduxFrame) parent).getTheSample(), false );
    
////    String[][] reportFractions = ((UPbReduxFrame) parent).getTheSample().reportAllFractionsByNumberStyle( false );
    
    ((ReportPainterI) parent).loadAndShowReportTableData("");//// reportFractions );
    
    
}//GEN-LAST:event_apply_buttonActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton apply_button;
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JList<ReportListItemI> categories_list;
    private javax.swing.JScrollPane categories_scrollPane;
    private javax.swing.ButtonGroup categoryChanges_buttonGroup;
    private javax.swing.JButton categoryHideAllColumns_button;
    private javax.swing.JButton categoryHideShow_button;
    private javax.swing.JButton categoryMoveDown_button;
    private javax.swing.JButton categoryMoveUp_button;
    private javax.swing.JButton categoryShowAllColumns_button;
    private javax.swing.JButton close_button;
    private javax.swing.ButtonGroup columnChanges_buttonGroup;
    private javax.swing.JButton columnHideShow_button;
    private javax.swing.JButton columnMoveDown_button;
    private javax.swing.JButton columnMoveUp_button;
    private javax.swing.JLabel columnName_label;
    private javax.swing.JList<ReportListItemI> columns_list;
    private javax.swing.JScrollPane columns_scrollPane;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel uncertaintyColName_label;
    private javax.swing.JLabel uncertaintyDigitCount_label;
    private javax.swing.JSpinner uncertaintyDigits_spinner;
    private javax.swing.ButtonGroup uncertaintyMode;
    private javax.swing.JCheckBox uncertaintyVisible_chkBox;
    private javax.swing.JRadioButton unctModeArbitrary_rButton;
    private javax.swing.JRadioButton unctModeSigFig_rButton;
    private javax.swing.JComboBox<String> unitsChooser_ComboBox;
    private javax.swing.JLabel valueDigitsCount_label;
    private javax.swing.JSpinner valueDigits_spinner;
    private javax.swing.ButtonGroup valueMode;
    private javax.swing.JRadioButton valueModeArbitrary_rButton;
    private javax.swing.JRadioButton valueModeSigFig_rButton;
    // End of variables declaration//GEN-END:variables

    /**
     * 
     * @return
     */
    public // Fields
            ReportSettingsInterface getReportSettings () {
        return reportSettings;
    }

    /**
     * 
     * @param reportSettings
     */
    public void setReportSettings ( ReportSettingsInterface reportSettings ) {
        this.reportSettings = reportSettings;
    }

    private void closeDialog () {
        // reorder categories
        for (ReportListItemI li : catList) {
            li.setPositionIndex( catList.indexOf( li ) );
        }
        for (ReportListItemI li : colList) {
            li.setPositionIndex( colList.indexOf( li ) );
        }

        amOpen = false;
    }
}
