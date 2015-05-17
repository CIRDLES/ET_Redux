/*
 * IncludedFractionsDesktopPane.java
 *
 * Created on March 21, 2008, 3:32 PM
 *
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
package org.earthtime.UPb_Redux.dateInterpretation;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JCheckBox;
import javax.swing.JDesktopPane;
import javax.swing.JLabel;
import org.earthtime.UPb_Redux.aliquots.Aliquot;
import org.earthtime.UPb_Redux.aliquots.UPbReduxAliquot;
import org.earthtime.UPb_Redux.fractions.Fraction;
import org.earthtime.UPb_Redux.valueModels.SampleDateModel;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.samples.SampleInterface;

/**
 *
 * @author James F. Bowring
 */
public class IncludedFractionsDesktopPane extends JDesktopPane {

    // instance variables
    private SampleInterface sample;

    /** Creates a new instance of IncludedFractionsDesktopPane */
    public IncludedFractionsDesktopPane() {
        super();
        setBackground(Color.white);
        setOpaque(true);

        ShowTitle();
        repaint();

    }

    private void ShowTitle() {

        JLabel includedFractions_label = new javax.swing.JLabel();
        includedFractions_label.setBackground(new java.awt.Color(102, 102, 102));
        includedFractions_label.setForeground(new java.awt.Color(255, 255, 255));
        includedFractions_label.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        includedFractions_label.setText("Included Fractions");
        includedFractions_label.setBorder(
                javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        includedFractions_label.setOpaque(true);
        includedFractions_label.setBounds(0, 0, getWidth(), 18);
        add(includedFractions_label, javax.swing.JLayeredPane.DEFAULT_LAYER);

    }

    /**
     * 
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

    /**
     * 
     * @param aliquot
     * @param width
     */
    public void ClearAndShowAliquot(Aliquot aliquot, int width) {
        this.removeAll();

        ShowTitle();
        ShowAliquotDateModels(aliquot, width, 0);
        repaint();
    }

    /**
     * 
     * @param aliquot
     * @param sampleDateInterpretation
     * @param width
     */
    public void ClearAndShowSampleDate(
            Aliquot aliquot,
            ValueModel sampleDateInterpretation,
            int width) {

        this.removeAll();
        ShowTitle();
        ShowIncludedFractionCheckBox(
                aliquot,
                sampleDateInterpretation,
                width,
                0);
        repaint();
    }

    private void ShowSample(int width) {
        int offset = 0;
        for (Aliquot aliquot : sample.getAliquots()) {
            ShowAliquotDateModels(aliquot, width, offset);
            offset += 2 + ((UPbReduxAliquot) aliquot).getAliquotFractions().size();
        }
    }

    private void ShowAliquotDateModels(
            Aliquot aliquot,
            int width,
            int offset) {

        int count = 0;
        JLabel aliquotLabel = new JLabel(aliquot.getAliquotName());
        aliquotLabel.setBounds(0, (offset + (count ++)) * 20 + 25, width, 15);
        add(aliquotLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);
        
        for (Fraction f : ((UPbReduxAliquot) aliquot).getAliquotFractions()) {
            JCheckBox temp = new JCheckBox();
            temp.setText(f.getFractionID());
            temp.setBounds(0, (offset + (count ++)) * 20 + 25, width, 15);
            add(temp, javax.swing.JLayeredPane.DEFAULT_LAYER);
        }
        setBounds(
                getX(),
                getY(),
                width,
                (offset + ((UPbReduxAliquot) aliquot).getAliquotFractions().size() * 20 + 25));
    }

    private void ShowIncludedFractionCheckBox(
            Aliquot aliquot,
            ValueModel sampleDateModel,
            int width,
            int offset) {
        
        int count = 0;        
        for (Fraction f : ((UPbReduxAliquot) aliquot).getAliquotFractions()) {
            JCheckBox temp = new JCheckBox();
            temp.setText(f.getFractionID());
            temp.setBounds(0, (offset + (count ++)) * 20 + 25, width, 15);
            //set checked status
            temp.setSelected(((SampleDateModel)sampleDateModel).includesFractionByName(f.getFractionID()));
            add(temp, javax.swing.JLayeredPane.DEFAULT_LAYER);
        }
        setBounds(
                getX(),
                getY(),
                width,
                (offset + ((UPbReduxAliquot) aliquot).getAliquotFractions().size() * 20 + 25));
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
