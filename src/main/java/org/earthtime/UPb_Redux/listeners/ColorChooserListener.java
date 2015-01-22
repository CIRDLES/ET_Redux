/*
 * ColorChooserListener.java
 *
 * Created on August 21, 2008, 6:33 AM
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
package org.earthtime.UPb_Redux.listeners;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Map;
import javax.swing.JLabel;
import org.earthtime.UPb_Redux.dialogs.DialogEditor;
import org.earthtime.UPb_Redux.dialogs.UPbReduxColorChooser;

/**
 *
 * @author James F. Bowring
 */
public class ColorChooserListener implements MouseListener {

    private DialogEditor colorChooser;
    private Color currentColor;
    private String selectedColorString;
    private Map<String, String> optionsMap;
    private String key;

    /** Creates a new instance of ColorChooserListener
     * @param optionsMap 
     */
    public ColorChooserListener(
            Map<String, String> optionsMap) {
        
        setOptionsMap(optionsMap);
    }

    /**
     * 
     * @param arg0
     */
    public void mouseClicked(MouseEvent arg0) {
        // key is stored in name field of JLabel
        setKeyAndColor(((JLabel) arg0.getSource()).getName());
        colorChooser = new UPbReduxColorChooser(null, true, currentColor);
        colorChooser.setVisible(true);

        setSelectedColorString(((UPbReduxColorChooser) colorChooser).//
                getSelectedColorRGBString());

        String[] temp = getSelectedColorString().split(",");
        Color selectedColor = new Color(//
                (int) Integer.parseInt(temp[0].trim()),
                (int) Integer.parseInt(temp[1].trim()),
                (int) Integer.parseInt(temp[2].trim()));

        ((JLabel) arg0.getSource()).setBackground(selectedColor);
        ((JLabel) arg0.getSource()).setForeground(new Color(
                255 - selectedColor.getRed(),
                255 - selectedColor.getGreen(),
                255 - selectedColor.getBlue()));
    }

    /**
     * 
     * @param arg0
     */
    public void mousePressed(MouseEvent arg0) {
    //throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * 
     * @param arg0
     */
    public void mouseReleased(MouseEvent arg0) {
    //throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * 
     * @param arg0
     */
    public void mouseEntered(MouseEvent arg0) {
    // throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * 
     * @param arg0
     */
    public void mouseExited(MouseEvent arg0) {
    //throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * 
     * @return
     */
    public String getSelectedColorString() {
        return selectedColorString;
    }

    /**
     * 
     * @param selectedColorString
     */
    public void setSelectedColorString(String selectedColorString) {
        getOptionsMap().put(key, selectedColorString);
        
        this.selectedColorString = selectedColorString;
    }

    /**
     * 
     * @return
     */
    public Color getCurrentColor() {
        return currentColor;
    }

    /**
     * 
     * @param currentColor
     */
    public void setCurrentColor(Color currentColor) {
        this.currentColor = currentColor;
    }

    /**
     * 
     * @return
     */
    public Map<String, String> getOptionsMap() {
        return optionsMap;
    }

    /**
     * 
     * @param optionsMap
     */
    public void setOptionsMap(Map<String, String> optionsMap) {
        this.optionsMap = optionsMap;
    }

    /**
     * 
     * @return
     */
    public String getKey() {
        return key;
    }

    /**
     * 
     * @param key
     */
    public void setKeyAndColor(String key) {
        this.key = key;
                       
        String[] temp = optionsMap.get(key).split(",");
        setCurrentColor(new Color(//
                (int) Integer.parseInt(temp[0].trim()),
                (int) Integer.parseInt(temp[1].trim()),
                (int) Integer.parseInt(temp[2].trim())));
    }
}
