/*
 * FutureRatioCellRenderer.java
 *
 * Created on March 26, 2006, 6:40 PM
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
package org.earthtime.UPb_Redux.renderers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Formatter;

/**
 *
 * @author James F. Bowring
 */
public class FutureRatioCellRenderer extends TableCellRendererToLabel {

    //float[] myLightYellow = Color.RGBtoHSB(251, 251, 205, null);
    /**
     *
     * @param g
     */
    public void paint(Graphics g) {
        if (mePaint) {
            g.setColor(Color.white);
       // } else {
            //     g.setColor(selection);
        }
       // g.fillRect(0, 0, getWidth()-1, getHeight()-1);

        super.paint(g);
        g.setColor(Color.black);
        g.setFont(new Font("Monospaced", Font.PLAIN, 11));//was courier

        String data[] = this.getText().split(":");
        StringBuilder ratio = new StringBuilder();
        StringBuilder stdErr = new StringBuilder();

        Formatter ratioFormatter = new Formatter(ratio);
        Formatter stdErrFormatter = new Formatter(stdErr);

        try {
            ratioFormatter.format("%9.4f", Double.valueOf(data[0].trim()));
            stdErrFormatter.format("%9.4f", Double.valueOf(data[1].trim()));
        } catch (Exception e) {
            ratioFormatter.format("%9.4f", 0.0);
            stdErrFormatter.format("%9.4f", 0.0);

        }
    }
}
