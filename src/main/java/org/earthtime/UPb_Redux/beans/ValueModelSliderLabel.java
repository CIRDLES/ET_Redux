/*
 * ValueModelSliderLabel.java
 *
 * Created on February 2 2009
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
package org.earthtime.UPb_Redux.beans;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import javax.swing.JPanel;

/**
 *
 * @author James F. Bowring
 */
public class ValueModelSliderLabel extends JPanel implements Serializable {

    // Class Variables
    /**
     * 
     */
    public static final String PROP_SAMPLE_PROPERTY = "sampleProperty";

    // Instance Variables
    private String sampleProperty;
    private PropertyChangeSupport propertySupport;

    private String text;
    private int boxWidth = 107;
    private int boxHeight = 15;
    private String fontName;
    private String fontSize;
    private Font textFont;

    /**
     * 
     * @param leftX
     * @param topY
     * @param text
     */
    public ValueModelSliderLabel(
            int leftX,
            int topY,
            String text) {

        propertySupport = new PropertyChangeSupport(this);

        setOpaque(false);
        setBackground(Color.white);
        setBounds(leftX, topY, boxWidth, boxHeight);

        this.text = text;

        fontName = "SansSerif";
        fontSize = "10";
        textFont = new Font(
                fontName,
                Font.PLAIN,
                Integer.parseInt(fontSize));

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

        RenderingHints rh = g2d.getRenderingHints();
        rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHints(rh);

        g2d.setColor(Color.BLACK);

        g2d.setFont(textFont);

        g2d.drawString(text, 1, 11);//12);

        g2d.setColor(Color.gray);
      //  g2d.drawLine(0, 0, getWidth(), 0);

    }

    /**
     * 
     * @return
     */
    public String getSampleProperty() {
        return sampleProperty;
    }

    /**
     * 
     * @param value
     */
    public void setSampleProperty(String value) {
        String oldValue = sampleProperty;
        sampleProperty = value;
        propertySupport.firePropertyChange(PROP_SAMPLE_PROPERTY, oldValue, sampleProperty);
    }

    /**
     * 
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }

    /**
     * 
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }
}
