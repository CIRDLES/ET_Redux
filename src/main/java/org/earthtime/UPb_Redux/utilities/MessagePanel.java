/*
 * MessagePanel.java
 *
 * Created on 30 September 2009
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
package org.earthtime.UPb_Redux.utilities;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 *
 * @author James F. Bowring
 */
public class MessagePanel extends JPanel {

    private String message;

    /**
     *
     */
    public MessagePanel() {
        super();
        setOpaque(true);

        this.message = "";

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
     * @param g
     */
    public void paint(Graphics2D g) {

        RenderingHints rh = g.getRenderingHints();
        rh.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHints(rh);

        g.setColor(Color.white);
        Font bigTextFont = new Font("Helvetica", Font.BOLD, 18);
        Font littleTextFont = new Font("Helvetica", Font.BOLD, 12);

        g.setFont(bigTextFont);
        g.setColor(Color.BLACK);

        g.drawString(message, 150, 60);

//        g.setFont(littleTextFont);
//        g.drawString("version " + UPbRedux.VERSION, 5, 120);
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
