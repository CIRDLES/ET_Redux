/*
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

package org.earthtime.UPb_Redux.fractions.UPbReduxFractions.fractionReduction;

import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import javax.swing.BorderFactory;
import javax.swing.JTextPane;
import org.earthtime.UPb_Redux.ReduxConstants;

/**
 *
 * @author jackom
 */
public class UncertaintyGraphInfoPanel extends JTextPane{

    private double widthR;
    private double maxHeightR;
    private String info;
    private String names;

    /**
     * 
     * @param width
     * @param height
     */
    public UncertaintyGraphInfoPanel(int width, int height){

        setLayout(new GridLayout(2, 1));
        
        widthR = 0.5;
        maxHeightR = 0.55;
        info = "Placeholder";

        setFont(new Font("SansSerif", Font.BOLD, 10));
        setBounds(0, 0, (int)(widthR * width), (int)(maxHeightR * height));

        setVisible(true);
        setBackground(ReduxConstants.mySampleYellowColor);
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
    }

    // change in IUC
    /**
     * 
     * @param namesS
     * @param perCent
     */
    public void update(String namesS, double perCent){
        NumberFormat formatter = new DecimalFormat("##0.0000 %");
        names = namesS;
        info = formatter.format(perCent);
        repaint();
    }

    /**
     * 
     * @param g
     */
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        paint((Graphics2D) g);
    }

    /**
     * 
     * @param g2d
     */
    public void paint(Graphics2D g2d){
        g2d.setColor(Color.BLACK);
        removeAll();
        setText(names + "\n" + info);
    }

}
