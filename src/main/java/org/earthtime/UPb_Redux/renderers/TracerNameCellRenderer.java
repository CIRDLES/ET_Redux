/*
 * TracerNameCellRenderer.java
 *
 * Created on May 17, 2007, 4:37 PM
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
package org.earthtime.UPb_Redux.renderers;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import org.earthtime.UPb_Redux.ReduxConstants;

/**
 *
 * @author James F. Bowring
 */
public class TracerNameCellRenderer extends TableCellRendererToLabel {

    private static int myFontSize = 11;

    // Paint current color
    /**
     *
     * @param g
     */
    @Override
    public void paint(Graphics g) {
        if (mePaint) {
            g.setColor(ReduxConstants.myFractionGreenColor);
        }

        super.paint(g);

        g.setColor(Color.black);
        g.setFont(new Font("Monospaced", Font.BOLD, myFontSize));

        String data[] = this.getText().split("v");

        try {
            g.drawString(data[0],
                    (int) (g.getClipBounds().getWidth() - data[0].length() * myFontSize / 2) / 2, 12); // hard-coded for now 2,12);
            g.drawString("v" + data[1],
                    (int) (g.getClipBounds().getWidth() - data[1].length() * myFontSize / 2) / 2, 27);//, 2,27);
        } catch (Exception e) {
            g.drawString("",
                    (int) (g.getClipBounds().getWidth()) / 2, 12); 
            g.drawString("",
                    (int) (g.getClipBounds().getWidth()) / 2, 27);

        }
    }
}
