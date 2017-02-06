/*
 * EditFractionButton.java
 *
 * Created on December 23, 2007, 7:04 AM
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

import java.awt.Font;
import java.awt.Insets;
import javax.swing.JButton;

/**
 *
 * @author James F. Bowring
 */
public class EditFractionButton extends JButton {

    private int myRow = 0;

    /**
     * Creates a button with the supplied title and row number.
     *
     * @param szText Button title.
     * @param row table row number.
     * @param isBold
     */
    public EditFractionButton(String szText, int row, boolean isBold) {
        super(szText);
        this.myRow = row;
        init(isBold);
    }

    private void init(boolean isBold) {
        setMargin(new Insets(0, 0, 0, 0));

        if (isBold) {
            setFont(new Font("Lucida Grande", Font.BOLD, 10));
        } else {
            setFont(new Font("Lucida Grande", Font.PLAIN, 10));
        }

        setToolTipText("Click to edit Fraction.");
    }

    /**
     *
     * @return
     */
    public int getMyRow() {
        return myRow;
    }

    /**
     *
     * @param myRow
     */
    public void setMyRow(int myRow) {
        this.myRow = myRow;
    }
}
