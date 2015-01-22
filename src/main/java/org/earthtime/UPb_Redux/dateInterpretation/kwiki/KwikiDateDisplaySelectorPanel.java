/*
 * KwikiDateDisplaySelectorPanel.java
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
package org.earthtime.UPb_Redux.dateInterpretation.kwiki;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JLayeredPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author James F. Bowring
 */
public class KwikiDateDisplaySelectorPanel extends JLayeredPane {

    private JCheckBox[] dateChkBoxes;
    /**
     * 
     */
    public static final String SHOWDATES_PROPERTY = "showdates";
    private boolean[] showDatesProperty;
    private PropertyChangeSupport propertySupport;

    /** Creates a new instance of KwikiDateDisplaySelectorPanel
     * @param x 
     * @param y
     * @param showDatesProperty  
     */
    public KwikiDateDisplaySelectorPanel(
            int x,
            int y,
            boolean[] showDatesProperty/*,
            PropertyChangeListener kwikiDateSelectorListener*/) {

        super();

        setOpaque(true);

        setBackground(Color.white);

        setBounds(x, y, 78, 75);

        this.showDatesProperty = showDatesProperty;
        propertySupport = new PropertyChangeSupport(this);
        //addPropertyChangeListener(kwikiDateSelectorListener);

        SetUpCheckBoxes();

    }

    private void SetUpCheckBoxes() {
        ActionListener chkBoxActionListener = new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                AbstractButton abstractButton =
                        (AbstractButton) e.getSource();
                boolean[] oldValue = showDatesProperty.clone();
                showDatesProperty[Integer.parseInt(abstractButton.getActionCommand())] =//
                        !showDatesProperty[Integer.parseInt(abstractButton.getActionCommand())];
                propertySupport.firePropertyChange(SHOWDATES_PROPERTY, oldValue, showDatesProperty);
            }
        };

        dateChkBoxes = new JCheckBox[3];
        for (int i = 0; i < 3; i++) {
            dateChkBoxes[i] = new JCheckBox();
            dateChkBoxes[i].setOpaque(false);
            dateChkBoxes[i].setForeground(new java.awt.Color(204, 0, 0));
            dateChkBoxes[i].setBounds(0, 5 + (i * 22), 78, 15);
            dateChkBoxes[i].setFont(new Font("SansSerif", Font.BOLD, 10));
            dateChkBoxes[i].setSelected(showDatesProperty[i]);
            dateChkBoxes[i].setEnabled(showDatesProperty[i]);
            dateChkBoxes[i].setActionCommand(String.valueOf(i));
            dateChkBoxes[i].addActionListener(chkBoxActionListener);
            add(dateChkBoxes[i],
                    javax.swing.JLayeredPane.DEFAULT_LAYER);
        }

        dateChkBoxes[0].setText("206 / 238");
        dateChkBoxes[1].setText("207 / 235");
        dateChkBoxes[2].setText("207 / 206");

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
        g2d.setColor(Color.red);

        DrawBounds(g2d);
    }

    private void DrawBounds(Graphics2D g2d) {

        g2d.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

    }

    /**
     * 
     * @param listener
     */
    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }

    /**
     * 
     * @param listener
     */
    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }


}
