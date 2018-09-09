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

import java.util.EventObject;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeNode;

/**
 *
 * @author http://www.java2s.com/Tutorial/Java/0240__Swing/CreatinganEditorJustforLeafNodes.htm
 */
public class LeafCellEditor extends DefaultTreeCellEditor {

    /**
     * 
     * @param tree
     * @param renderer
     * @param editor
     */
    public LeafCellEditor(JTree tree, DefaultTreeCellRenderer renderer, TreeCellEditor editor) {
        super(tree, renderer, editor);
    }

    /**
     * 
     * @param event
     * @return
     */
    @Override
    public boolean isCellEditable(EventObject event) {
        boolean returnValue = super.isCellEditable(event);
        if (returnValue) {
            Object node = tree.getLastSelectedPathComponent();
            if ((node != null) && (node instanceof TreeNode)) {
                TreeNode treeNode = (TreeNode) node;
                returnValue = treeNode.isLeaf();
            }
        }
        return returnValue;
    }
}

