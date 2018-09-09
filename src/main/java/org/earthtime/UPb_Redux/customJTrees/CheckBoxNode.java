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
package org.earthtime.UPb_Redux.customJTrees;

/**
 *
 * @author http://www.java2s.com/Tutorial/Java/0240__Swing/CreatinganEditorJustforLeafNodes.htm
 */
public class CheckBoxNode {

    String text;

    boolean selected;
    
    private boolean checkBox;

    /**
     * 
     * @param text
     * @param selected
     * @param checkBox
     */
    public CheckBoxNode(String text, boolean selected, boolean checkBox) {
        this.text = text;
        this.selected = selected;
        this.checkBox = checkBox;
    }

    /**
     * 
     * @return
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * 
     * @param newValue
     */
    public void setSelected(boolean newValue) {
        selected = newValue;
    }

    /**
     * 
     * @return
     */
    public String getText() {
        return text;
    }

    /**
     * 
     * @param newValue
     */
    public void setText(String newValue) {
        text = newValue;
    }

    /**
     * 
     * @return
     */
    @Override
    public String toString() {
        return getClass().getName() + "[" + text + "/" + selected + "]";
    }

    /**
     * 
     * @return
     */
    public boolean isCheckBox() {
        return checkBox;
    }

    /**
     * 
     * @param checkBox
     */
    public void setCheckBox(boolean checkBox) {
        this.checkBox = checkBox;
    }
}
