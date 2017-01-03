/*
 * DateInterpretationsDesktopPane.java
 *
 * Created on March 21, 2008, 3:32 PM
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
package org.earthtime.UPb_Redux.dateInterpretation;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JDesktopPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import org.earthtime.UPb_Redux.valueModels.SampleDateModel;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.aliquots.AliquotInterface;
import org.earthtime.samples.SampleInterface;

/**
 *
 * @author James F. Bowring
 */
public class DateInterpretationsDesktopPane extends JDesktopPane {

    // instance variables
    private SampleInterface sample;

    /** Creates a new instance of DateInterpretationsDesktopPane */
    public DateInterpretationsDesktopPane() {
        super();
        setBackground(Color.white);
        setOpaque(true);

        ShowTitle();
        repaint();

    }

    private void ShowTitle() {
        
        JLabel sampleDateInterpretations_label = new javax.swing.JLabel();
        sampleDateInterpretations_label.setBackground(new java.awt.Color(102, 102, 102));
        sampleDateInterpretations_label.setForeground(new java.awt.Color(255, 255, 255));
        sampleDateInterpretations_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        sampleDateInterpretations_label.setText("Sample Date Interpretations");
        sampleDateInterpretations_label.setBorder(
                javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        sampleDateInterpretations_label.setOpaque(true);
        sampleDateInterpretations_label.setBounds(0, 0, getWidth(), 18);
        add(sampleDateInterpretations_label, javax.swing.JLayeredPane.DEFAULT_LAYER);

    }

    /** Creates a new instance of DateInterpretationsDesktopPane
     * @param sample
     * @param width  
     */
    public void ClearAndShowSample(SampleInterface sample, int width) {
        this.removeAll();
        
        this.sample = sample;
        ShowTitle();
        ShowSample(width);
        repaint();
    }

    /** Creates a new instance of DateInterpretationsDesktopPane
     * @param aliquot 
     * @param width 
     */
    public void ClearAndShowAliquot(AliquotInterface aliquot, int width) {
        this.removeAll();
        
        ShowTitle();
        ShowAliquotDateModels(aliquot, width, 0);
        repaint();
    }

    /** Creates a new instance of DateInterpretationsDesktopPane
     * @param aliquot 
     * @param sampleDateInterpretation 
     * @param width 
     */
    public void ClearAndShowSampleDate(
            AliquotInterface aliquot,
            ValueModel sampleDateInterpretation,
            int width) {
        
        this.removeAll();
        ShowTitle();
        ShowDateInterpretationPanel(
                aliquot,
                sampleDateInterpretation,
                width,
                0);
        repaint();
    }

    private void ShowSample(int width) {
        int offset = 0;
        for (AliquotInterface aliquot : sample.getAliquots()) {
            ShowAliquotDateModels(aliquot, width, offset);
            offset += aliquot.getSampleDateModels().size();
        }
    }

    private void ShowAliquotDateModels(
            AliquotInterface aliquot,
            int width,
            int offset) {
        for (int i = 0; i < aliquot.getSampleDateModels().size(); i++) {
            ShowDateInterpretationPanel(
                    aliquot,
                    aliquot.getSampleDateModels().get(i),
                    width,
                    offset + i);
        }
        setBounds(
                getX(),
                getY(),
                width, 
                (offset + aliquot.getSampleDateModels().size()) * 120 + 25);
    }

    private void ShowDateInterpretationPanel(
            AliquotInterface aliquot,
            ValueModel sampleDateModel,
            int width,
            int offset) {

        JPanel ageInterpPanel = new JPanel();
        if (((SampleDateModel)sampleDateModel).isPreferred()){
            ageInterpPanel.setBackground(new java.awt.Color(255, 204, 204));
        }else {
            ageInterpPanel.setBackground(new java.awt.Color(255, 255, 204));
        }

        org.jdesktop.layout.GroupLayout jPanel1Layout =
                new org.jdesktop.layout.GroupLayout(ageInterpPanel);
        ageInterpPanel.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(0, width, Short.MAX_VALUE));
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(
                org.jdesktop.layout.GroupLayout.LEADING).add(0, 100, Short.MAX_VALUE));

        ageInterpPanel.setBounds(0, offset * 120 + 25, width, 100);

        JLabel aliquotName_label = new JLabel();
        aliquotName_label.setHorizontalAlignment(SwingConstants.CENTER);
        aliquotName_label.setBorder(
                javax.swing.BorderFactory.createLineBorder(Color.black));
        aliquotName_label.setText(
                "Aliquot: " + aliquot.getAliquotName().trim());
        aliquotName_label.setSize(width, 10);


        try {
            JLabel ageInterpName_label = new JLabel(sampleDateModel.getName());
            org.jdesktop.layout.GroupLayout ageInterpPanelLayout =
                    new org.jdesktop.layout.GroupLayout(ageInterpPanel);
            ageInterpPanel.setLayout(ageInterpPanelLayout);
            ageInterpPanelLayout.setHorizontalGroup(
                    ageInterpPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING).add(ageInterpPanelLayout.createSequentialGroup().add(ageInterpName_label).addContainerGap(161, Short.MAX_VALUE)).add(aliquotName_label, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE));
            ageInterpPanelLayout.setVerticalGroup(
                    ageInterpPanelLayout.createParallelGroup(//
                    org.jdesktop.layout.GroupLayout.LEADING).//
                    add(ageInterpPanelLayout.createSequentialGroup().//
                    add(aliquotName_label).//
                    //addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED).//
                    add(ageInterpName_label).//
                    addContainerGap(60, Short.MAX_VALUE)));
        } catch (Exception e) {
        }


        add(ageInterpPanel);//, javax.swing.JLayeredPane.DEFAULT_LAYER);
    }

    /**
     * 
     * @param g
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);


    }
}
