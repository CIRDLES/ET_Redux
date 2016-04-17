/*
 * SampleTreeI.java
 *
 * Created on March 19, 2008, 7:32 PM
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
package org.earthtime.UPb_Redux.dateInterpretation;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

/**
 *
 * @author James F. Bowring
 */
public interface SampleTreeI extends MouseListener, TreeSelectionListener {

    /**
     *
     */
    void buildTree();

    /**
     *
     * @param value
     * @param selected
     * @param expanded
     * @param leaf
     * @param row
     * @param hasFocus
     * @return
     */
    String convertValueToText(Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus);

    /**
     *
     * @return
     */
    public SampleTreeChangeI getSampleTreeChange();

    /**
     *
     * @param sampleTreeChange
     */
    public void setSampleTreeChange(SampleTreeChangeI sampleTreeChange);

    /**
     *
     * @param arg0
     */
    @Override
    void mouseClicked(MouseEvent arg0);

    /**
     *
     * @param arg0
     */
    @Override
    void mouseEntered(MouseEvent arg0);

    /**
     *
     * @param arg0
     */
    @Override
    void mouseExited(MouseEvent arg0);

    /**
     *
     * @param e
     */
    @Override
    void mousePressed(MouseEvent e);

    /**
     *
     * @param arg0
     */
    @Override
    void mouseReleased(MouseEvent arg0);

    /**
     *
     * @param e
     */
    @Override
    void valueChanged(TreeSelectionEvent e);

    /**
     *
     */
    public void performLastUserSelection();

    /**
     *
     */
    public void performLastUserSelectionOfSampleDate();

    public String collectExpansionHistory();

    public void expandToHistory(String expansionHistory);
    
    public void expandAllNodes();
}
