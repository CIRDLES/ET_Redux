
/*
 * GeochronSampleCustomMetadataDialog.java
 *
 * Created on Oct 30, 2010
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
package org.earthtime.UPb_Redux.dialogs.sampleManagers;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.DefaultComboBoxModel;
import org.earthtime.UPb_Redux.ReduxConstants.ANALYSIS_PURPOSE;
import org.earthtime.UPb_Redux.dialogs.DialogEditor;
import org.earthtime.UPb_Redux.samples.SESARSampleMetadata;
import org.earthtime.UPb_Redux.samples.Sample;
import org.earthtime.xmlUtilities.SchemaSimpleType;
import org.earthtime.xmlUtilities.XMLSchemaProcessor;

/**
 *
 * @author James F. Bowring
 */
public class GeochronSampleCustomMetadataDialog extends DialogEditor {

    private Sample sample;

    /** Creates new form GeochronSampleCustomMetadataDialog
     * @param parent
     * @param modal 
     * @param sample  
     */
    public GeochronSampleCustomMetadataDialog (//
            java.awt.Frame parent,//
            boolean modal,//
            Sample sample ) {
        super( parent, modal );

        this.sample = sample;

        initComponents();

        minAbsAgeMa_txtBox.setDocument( new BigDecimalDocument( minAbsAgeMa_txtBox, true ) );
        maxAbsAgeMa_txtBox.setDocument( new BigDecimalDocument( maxAbsAgeMa_txtBox, true ) );

        geologicAges_comboBox.setModel(//
                new DefaultComboBoxModel<String>(//
                (new XMLSchemaProcessor()).getSimpleTypeEnumeration( SchemaSimpleType.GeologicAge ) ) );
        geologicAges_comboBox.setSelectedIndex( 0 );


        detritalTypes_comboBox.setModel(//
                new DefaultComboBoxModel<String> (//
                (new XMLSchemaProcessor()).getSimpleTypeEnumeration( SchemaSimpleType.DetritalType ) ) );
        detritalTypes_comboBox.setSelectedIndex( 0 );

        initSampleData();
    }

    private void initSampleData () {

        SESARSampleMetadata sampleMetaData = sample.getMySESARSampleMetadata();

        sampleName_label.setText( "Sample: " + sample.getSampleName() );

        stratigraphicFormationName_text.setText( sampleMetaData.getStratigraphicFormationName() );

        minAbsAgeMa_txtBox.setText( Double.toString( sampleMetaData.getStratigraphicMinAbsoluteAgeMa() ) );
        maxAbsAgeMa_txtBox.setText( Double.toString( sampleMetaData.getStratigraphicMaxAbsoluteAgeMa() ) );

        geologicAges_comboBox.setSelectedItem( sampleMetaData.getStratigraphicGeologicAgeMa() );
        // add listener after showing saved data
        geologicAges_comboBox.addItemListener( new geologicAgesItemListener() );


        detritalTypes_comboBox.setSelectedItem( sampleMetaData.getDetritalType() );

        // set up analysisPurposeChooser
        analysisPurposeChooser.removeAllItems();
        for (ANALYSIS_PURPOSE ap : ANALYSIS_PURPOSE.values()) {
            analysisPurposeChooser.addItem( ap.toString() );
        }

        analysisPurposeChooser.setSelectedItem( sample.getAnalysisPurpose().toString() );
        analysisPurposeChooser.addItemListener( new analysisPurposeItemListener() );
        // show detrital choices
        detritalTypePanel.setVisible( sample.getAnalysisPurpose().compareTo( ANALYSIS_PURPOSE.DetritalSpectrum ) == 0 );


    }

    class analysisPurposeItemListener implements ItemListener {
        // This method is called only if a new item has been selected.

        public void itemStateChanged ( ItemEvent evt ) {

            if ( evt.getStateChange() == ItemEvent.SELECTED ) {
                // Item was just selected
                sample.setAnalysisPurpose( ANALYSIS_PURPOSE.valueOf( (String) evt.getItem() ) );

                // show detrital choices
                detritalTypePanel.setVisible( sample.getAnalysisPurpose().compareTo( ANALYSIS_PURPOSE.DetritalSpectrum ) == 0 );

            } else if ( evt.getStateChange() == ItemEvent.DESELECTED ) {
                // Item is no longer selected
            }
        }
    }

    private void saveMetaData () {
        SESARSampleMetadata sampleMetaData = sample.getMySESARSampleMetadata();

        sampleMetaData.setStratigraphicFormationName( stratigraphicFormationName_text.getText() );
        sampleMetaData.setStratigraphicMinAbsoluteAgeMa( Double.valueOf( minAbsAgeMa_txtBox.getText() ) );
        sampleMetaData.setStratigraphicMaxAbsoluteAgeMa( Double.valueOf( maxAbsAgeMa_txtBox.getText() ) );
        sampleMetaData.setStratigraphicGeologicAgeMa( (String) geologicAges_comboBox.getSelectedItem() );
        sampleMetaData.setDetritalType( (String) detritalTypes_comboBox.getSelectedItem() );

    }

    class geologicAgesItemListener implements ItemListener {
        // This method is called only if a new item has been selected.

        public void itemStateChanged ( ItemEvent evt ) {
            String geologicAge = (String) evt.getItem();

            if ( evt.getStateChange() == ItemEvent.SELECTED ) {
                // parse the string to get ages
                String[] pass1 = geologicAge.split( "\\x28" );//?"("
                String[] pass2 = pass1[1].split( "-" );

                String ageMin = pass2[0].split( "Ma" )[0].trim();
                String ageMax = pass2[1].split( "Ma" )[0].trim();

                System.out.println( "AGE = " + ageMin + "   " + ageMax );
                minAbsAgeMa_txtBox.setText( ageMin );
                maxAbsAgeMa_txtBox.setText( ageMax );

            } else if ( evt.getStateChange() == ItemEvent.DESELECTED ) {
                // Item is no longer selected
            }
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings ("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLayeredPane1 = new javax.swing.JLayeredPane();
        explanation_label = new javax.swing.JLabel();
        geologicAges_comboBox = new javax.swing.JComboBox<String>();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        maxAbsAgeMa_txtBox = new javax.swing.JTextField();
        stratigraphicFormationName_text = new javax.swing.JTextField();
        minAbsAgeMa_txtBox = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        sampleName_label = new javax.swing.JLabel();
        chooseAnalysisPurpose_label = new javax.swing.JLabel();
        analysisPurposeChooser = new javax.swing.JComboBox<String>();
        detritalTypePanel = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        detritalTypes_comboBox = new javax.swing.JComboBox<String>();
        buttonsPanel = new javax.swing.JPanel();
        save_button = new javax.swing.JButton();
        close_button = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Customize Sample Metadata");

        jLayeredPane1.setBackground(new java.awt.Color(255, 255, 255));
        jLayeredPane1.setOpaque(true);

        explanation_label.setText("<html> This dialog is in development.  It provides a way for users to add sample metadata that is not currently supported by the sample registries, but is supported by Geochron.</html>");
        jLayeredPane1.add(explanation_label);
        explanation_label.setBounds(10, 0, 380, 60);

        geologicAges_comboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jLayeredPane1.add(geologicAges_comboBox);
        geologicAges_comboBox.setBounds(50, 210, 330, 27);

        jLabel1.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel1.setText("Modify min and max absolute ages (Ma) of geologic age:");
        jLayeredPane1.add(jLabel1);
        jLabel1.setBounds(30, 250, 370, 14);

        jLabel3.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("max:");
        jLabel3.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jLayeredPane1.add(jLabel3);
        jLabel3.setBounds(190, 280, 40, 30);

        jLabel4.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel4.setText("Select Stratigraphic geologic age:");
        jLayeredPane1.add(jLabel4);
        jLabel4.setBounds(30, 190, 220, 14);

        maxAbsAgeMa_txtBox.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        maxAbsAgeMa_txtBox.setText("0");
        jLayeredPane1.add(maxAbsAgeMa_txtBox);
        maxAbsAgeMa_txtBox.setBounds(230, 280, 80, 28);
        jLayeredPane1.add(stratigraphicFormationName_text);
        stratigraphicFormationName_text.setBounds(50, 150, 320, 28);

        minAbsAgeMa_txtBox.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        minAbsAgeMa_txtBox.setText("0");
        jLayeredPane1.add(minAbsAgeMa_txtBox);
        minAbsAgeMa_txtBox.setBounds(100, 280, 80, 28);

        jLabel5.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel5.setText("Enter the Stratigraphic Formation name:");
        jLayeredPane1.add(jLabel5);
        jLabel5.setBounds(30, 130, 250, 14);

        jLabel7.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel7.setText("min:");
        jLabel7.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jLayeredPane1.add(jLabel7);
        jLabel7.setBounds(60, 280, 40, 30);

        sampleName_label.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        sampleName_label.setText("Sample Name");
        jLayeredPane1.add(sampleName_label);
        sampleName_label.setBounds(10, 80, 380, 14);

        chooseAnalysisPurpose_label.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        chooseAnalysisPurpose_label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        chooseAnalysisPurpose_label.setText("Analysis purpose:");
        jLayeredPane1.add(chooseAnalysisPurpose_label);
        chooseAnalysisPurpose_label.setBounds(30, 360, 110, 30);

        analysisPurposeChooser.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jLayeredPane1.add(analysisPurposeChooser);
        analysisPurposeChooser.setBounds(140, 360, 230, 27);

        detritalTypePanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        detritalTypePanel.setOpaque(false);

        jLabel6.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        jLabel6.setText("Select Detrital type:");

        detritalTypes_comboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        org.jdesktop.layout.GroupLayout detritalTypePanelLayout = new org.jdesktop.layout.GroupLayout(detritalTypePanel);
        detritalTypePanel.setLayout(detritalTypePanelLayout);
        detritalTypePanelLayout.setHorizontalGroup(
            detritalTypePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(detritalTypePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 160, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(218, Short.MAX_VALUE))
            .add(org.jdesktop.layout.GroupLayout.TRAILING, detritalTypePanelLayout.createSequentialGroup()
                .addContainerGap(61, Short.MAX_VALUE)
                .add(detritalTypes_comboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 270, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(59, 59, 59))
        );
        detritalTypePanelLayout.setVerticalGroup(
            detritalTypePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(detritalTypePanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(jLabel6, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(detritalTypes_comboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        jLayeredPane1.add(detritalTypePanel);
        detritalTypePanel.setBounds(20, 400, 390, 60);

        buttonsPanel.setBackground(new java.awt.Color(204, 204, 204));
        buttonsPanel.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));

        save_button.setForeground(new java.awt.Color(255, 51, 0));
        save_button.setText("OK");
        save_button.setMargin(new java.awt.Insets(0, 1, 0, 1));
        save_button.setPreferredSize(new java.awt.Dimension(140, 23));
        save_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                save_buttonActionPerformed(evt);
            }
        });

        close_button.setForeground(new java.awt.Color(255, 51, 0));
        close_button.setText("Cancel");
        close_button.setMargin(new java.awt.Insets(0, 1, 0, 1));
        close_button.setPreferredSize(new java.awt.Dimension(140, 23));
        close_button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                close_buttonActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout buttonsPanelLayout = new org.jdesktop.layout.GroupLayout(buttonsPanel);
        buttonsPanel.setLayout(buttonsPanelLayout);
        buttonsPanelLayout.setHorizontalGroup(
            buttonsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, buttonsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(save_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 200, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(close_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 200, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        buttonsPanelLayout.setVerticalGroup(
            buttonsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(buttonsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                .add(close_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(save_button, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 28, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(buttonsPanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(jLayeredPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 444, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .add(jLayeredPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 552, Short.MAX_VALUE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(buttonsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void save_buttonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_save_buttonActionPerformed
        saveMetaData();
        close();
}//GEN-LAST:event_save_buttonActionPerformed

    private void close_buttonActionPerformed (java.awt.event.ActionEvent evt) {//GEN-FIRST:event_close_buttonActionPerformed
        close();
}//GEN-LAST:event_close_buttonActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main ( String args[] ) {
        java.awt.EventQueue.invokeLater( new Runnable() {

            public void run () {
                GeochronSampleCustomMetadataDialog dialog = new GeochronSampleCustomMetadataDialog( new javax.swing.JFrame(), true, new Sample() );
                dialog.addWindowListener( new java.awt.event.WindowAdapter() {

                    public void windowClosing ( java.awt.event.WindowEvent e ) {
                        System.exit( 0 );
                    }
                } );
                dialog.setVisible( true );
            }
        } );
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> analysisPurposeChooser;
    private javax.swing.JPanel buttonsPanel;
    private javax.swing.JLabel chooseAnalysisPurpose_label;
    private javax.swing.JButton close_button;
    private javax.swing.JPanel detritalTypePanel;
    private javax.swing.JComboBox<String> detritalTypes_comboBox;
    private javax.swing.JLabel explanation_label;
    private javax.swing.JComboBox<String> geologicAges_comboBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JTextField maxAbsAgeMa_txtBox;
    private javax.swing.JTextField minAbsAgeMa_txtBox;
    private javax.swing.JLabel sampleName_label;
    private javax.swing.JButton save_button;
    private javax.swing.JTextField stratigraphicFormationName_text;
    // End of variables declaration//GEN-END:variables
}
