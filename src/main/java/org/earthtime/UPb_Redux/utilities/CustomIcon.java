/*
 * CustomIcon.java
 *
 * Created 24 Sep 2009
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
package org.earthtime.UPb_Redux.utilities;

import java.awt.Component;
import java.awt.Graphics;
import java.net.URL;
import javax.swing.ImageIcon;

/**
 *
 * @author James F. Bowring
 */
public class CustomIcon extends ImageIcon {

    int iconWidth;
    int iconHeight;

    /**
     * 
     * @param location
     */
    public CustomIcon(URL location) {
        super(location);
    }

    /**
     * 
     * @param c
     * @param g
     * @param x
     * @param y
     */
    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        if (getImage() != null) {
            g.drawImage(getImage(), x, y, getIconWidth(), getIconHeight(), c);
        }
    }

    /**
     * 
     * @param w
     * @param h
     */
    public void setSize(int w, int h) {
        iconWidth = w;
        iconHeight = h;
    }

    /**
     * 
     * @return
     */
    @Override
    public int getIconWidth() {
        return iconWidth;
    }

    /**
     * 
     * @return
     */
    @Override
    public int getIconHeight() {
        return iconHeight;
    }
}
