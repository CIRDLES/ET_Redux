/*
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
package org.earthtime.UPb_Redux.customJTrees;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

/**
 *
 * @author James F. Bowring
 */
public class CheckBoxNodeRenderer implements TreeCellRenderer {

    private JCheckBox leafRenderer = new JCheckBox();

    private DefaultTreeCellRenderer nonLeafRenderer = new DefaultTreeCellRenderer();

    /**
     * 
     * @return
     */
    public JCheckBox getLeafRenderer() {
        return leafRenderer;
    }

    /**
     * 
     */
    public CheckBoxNodeRenderer() {
        Font fontValue;
        fontValue = UIManager.getFont("Tree.font");
        if (fontValue != null) {
            leafRenderer.setFont(fontValue);
        }

        Boolean booleanValue = (Boolean) UIManager.get("Tree.drawsFocusBorderAroundIcon");
        leafRenderer.setFocusPainted((booleanValue != null) && (booleanValue.booleanValue()));
    }

    /**
     * 
     * @param tree
     * @param value
     * @param selected
     * @param expanded
     * @param leaf
     * @param row
     * @param hasFocus
     * @return
     */
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected,
            boolean expanded, boolean leaf, int row, boolean hasFocus) {

        Component returnValue = null;
        Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
        if (leaf) {
            if ((value != null) && (value instanceof DefaultMutableTreeNode)) {
                if (userObject instanceof CheckBoxNode) {
                    if (((CheckBoxNode) userObject).isCheckBox()) {
                        CheckBoxNode node = (CheckBoxNode) userObject;
                        leafRenderer.setText(node.getText());
                        leafRenderer.setSelected(node.isSelected());
                        leafRenderer.setBackground(Color.white);
                        leafRenderer.setOpaque(true);

                        returnValue = leafRenderer;
                    }
                } else {
                    returnValue = nonLeafRenderer.getTreeCellRendererComponent(tree, value, selected, expanded,
                            leaf, row, hasFocus);
                }
            }
        } else {
            returnValue = nonLeafRenderer.getTreeCellRendererComponent(tree, value, selected, expanded,
                    leaf, row, hasFocus);
        }
        return returnValue;
    }
}
