/*
 * UPbReduxFocusTraversalPolicy.java
 *
 * Created April 2009
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

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.util.ArrayList;
import java.util.Vector;

/**
 *
 * @author James F. Bowring
 */
public class UPbReduxFocusTraversalPolicy extends FocusTraversalPolicy {
    
    ArrayList<Component> traversalOrder;

    /**
     * 
     * @param traversalOrder
     */
    public UPbReduxFocusTraversalPolicy(ArrayList<Component> traversalOrder) {

        this.traversalOrder = traversalOrder;
    }

    /**
     * 
     * @param focusCycleRoot
     * @param aComponent
     * @return
     */
    @Override
    public Component getComponentAfter(Container focusCycleRoot,
            Component aComponent) {
        int saveIdx = traversalOrder.indexOf(aComponent);
        int count = 0;
        int idx;

        idx = (traversalOrder.indexOf(aComponent) + count++) % traversalOrder.size();
        do {
            idx = (traversalOrder.indexOf(aComponent) + count++) % traversalOrder.size();
        } while (!traversalOrder.get(idx).isEnabled() && (saveIdx != idx));

        return traversalOrder.get(idx);
    }

    /**
     * 
     * @param focusCycleRoot
     * @param aComponent
     * @return
     */
    @Override
    public Component getComponentBefore(Container focusCycleRoot,
            Component aComponent) {
        int saveIdx = traversalOrder.indexOf(aComponent);
        int count = 0;
        int idx;

        idx = traversalOrder.indexOf(aComponent) - count++;
        do {
            idx = traversalOrder.indexOf(aComponent) - count++;
            if (idx < 0) {
                idx = traversalOrder.size() - 1;
                count = 0;
            }
        } while (!traversalOrder.get(idx).isEnabled() && (saveIdx != idx));


        return traversalOrder.get(idx);
    }

    /**
     * 
     * @param focusCycleRoot
     * @return
     */
    @Override
    public Component getDefaultComponent(Container focusCycleRoot) {
        return traversalOrder.get(0);
    }

    /**
     * 
     * @param focusCycleRoot
     * @return
     */
    @Override
    public Component getLastComponent(Container focusCycleRoot) {
        return traversalOrder.get( traversalOrder.size() -1);
    }

    /**
     * 
     * @param focusCycleRoot
     * @return
     */
    @Override
    public Component getFirstComponent(Container focusCycleRoot) {
        return traversalOrder.get(0);
    }
}
