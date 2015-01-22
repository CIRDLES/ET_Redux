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

import java.awt.BorderLayout;
import java.util.Vector;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author http://www.java2s.com/Tutorial/Java/0240__Swing/CreatinganEditorJustforLeafNodes.htm
 */
public class CheckBoxNodeTreeSample {

    /**
     * 
     * @param args
     */
    public static void main(String args[]) {
        JFrame frame = new JFrame("CheckBox Tree");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        DefaultMutableTreeNode accessibilityOptions[] = {new DefaultMutableTreeNode("A"),
            new DefaultMutableTreeNode("B")
        };
        CheckBoxNode browsingOptions[] = {new CheckBoxNode("C", true, true), 
        new CheckBoxNode("D", true, true),
            new CheckBoxNode("E", true, true), new CheckBoxNode("F", false, true)
        };
        Vector<DefaultMutableTreeNode> accessVector = 
                new TreeNodeVector<DefaultMutableTreeNode>("G", accessibilityOptions);
        Vector<CheckBoxNode> browseVector = new TreeNodeVector<CheckBoxNode>("H", browsingOptions);
        Object rootNodes[] = {accessVector, browseVector};
        Vector<Object> rootVector = new TreeNodeVector<Object>("Root", rootNodes);
        JTree tree = new JTree(rootVector);

        CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer();
        tree.setCellRenderer(renderer);

        tree.setCellEditor(new CheckBoxNodeEditor(tree));
        tree.setEditable(true);
        JScrollPane scrollPane = new JScrollPane(tree);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setSize(300, 150);
        frame.setVisible(true);
    }
}
