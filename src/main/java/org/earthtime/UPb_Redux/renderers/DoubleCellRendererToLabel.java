/*
 * DoubleCellRendererToLabel.java
 *
 * Created on March 25, 2006, 8:29 PM
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
import java.util.Formatter;

/**
 *
 * @author James F. Bowring
 */
public class DoubleCellRendererToLabel extends TableCellRendererToLabel {

    String myFormat = "%1.1f";

    /**
     *
     * @param myFormat
     */
    public DoubleCellRendererToLabel(String myFormat) {
        super();
        this.myFormat = myFormat;
    }

    // Paint current color
    /**
     *
     * @param g
     */
    @Override
    public void paint(Graphics g) {
        if (mePaint) {
            g.setColor(Color.WHITE);
      //  } else {
            //     g.setColor(selection);
        }
       // g.fillRect(0, 0, getWidth()-1, getHeight()-1);

        super.paint(g);
        g.setColor(Color.black);
        g.setFont(new Font("Monospaced", Font.BOLD, 11));

        StringBuilder dblVal = new StringBuilder();

        Formatter dblValFormatter = new Formatter(dblVal);

        // assume text is not null by construction
        double contentsValue;
        try {
            contentsValue = Double.valueOf(getText().trim());
            if (!Double.isFinite(contentsValue)){
                contentsValue = 0.0;
            }
        } catch (NumberFormatException numberFormatException) {
            contentsValue = 0.0;
        }
        
//        try {
            dblValFormatter.format(myFormat, contentsValue);
            g.drawString(dblVal.substring(0),
                    (int) g.getClipBounds().getWidth() - (3 + dblVal.substring(0).length()) * 5, 20); // hard-coded for now
//        } catch (Exception e) {
//        }
    }
}
