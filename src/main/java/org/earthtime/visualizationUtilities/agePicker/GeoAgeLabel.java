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
package org.earthtime.visualizationUtilities.agePicker;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;

/**
 *
 * @author bowring
 */
public class GeoAgeLabel extends JPanel{

    private int locatorIndex;
    private String label;
    private double x;
    private double y;
    private double width;
    private double height;

    /**
     * 
     * @param locatorIndex
     * @param label
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public GeoAgeLabel(int locatorIndex, String label, double x, double y, double width, double height) {
        this.locatorIndex = locatorIndex;
        this.label = label;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        this.setBounds((int)x, (int)y, (int)width, (int)height);
        
        this.setBackground(Color.white);
        this.setOpaque(true);
        
   }

    /**
     * 
     * @param g
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        paint((Graphics2D) g);
    }

    /**
     * 
     * @param g2d
     */
    public void paint(Graphics2D g2d) {
        
        g2d.setFont(new Font(
                "SansSerif",
                Font.PLAIN,
                10));
        
        Rectangle2D level1GeoAge = new Rectangle2D.Double(0, 0, width - 0.1, height - 0.1);
        g2d.draw(level1GeoAge);
        g2d.drawString(label, (float)1, (float)(height - 2));

    }

    /**
     * @return the locatorIndex
     */
    public int getLocatorIndex() {
        return locatorIndex;
    }


}
