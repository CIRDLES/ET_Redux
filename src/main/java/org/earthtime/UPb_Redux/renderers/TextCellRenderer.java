/*
 * TextCellRenderer.java
 *
 * Created on October 7, 2006, 5:05 PM
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

/**
 *
 * @author James F. Bowring
 */
public class TextCellRenderer extends TableCellRendererToLabel{
    

    // Paint current color
    /**
     * 
     * @param g
     */
    @Override
    public void paint(Graphics g) {
        if (mePaint){
            g.setColor(Color.WHITE);
        }
        
        super.paint(g);
        
        g.setColor(Color.black);
        g.setFont(new Font("Monospaced", Font.BOLD, 12));
        g.drawString(this.getText(),
                (int)g.getClipBounds().getWidth() - 55, 20); // hard-coded for now
    }
    
}
