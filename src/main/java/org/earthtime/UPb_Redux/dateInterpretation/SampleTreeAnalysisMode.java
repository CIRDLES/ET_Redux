/*
 * SampleTreeAnalysisMode.java
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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import org.earthtime.UPb_Redux.customJTrees.CheckBoxNode;
import org.earthtime.UPb_Redux.customJTrees.CheckBoxNodeEditor;
import org.earthtime.UPb_Redux.customJTrees.CheckBoxNodeRenderer;
import org.earthtime.UPb_Redux.dialogs.sampleManagers.sampleDateInterpretationManagers.SampleDateInterpretationChooserDialog;
import org.earthtime.UPb_Redux.valueModels.SampleDateModel;
import org.earthtime.UPb_Redux.valueModels.ValueModel;
import org.earthtime.UPb_Redux.valueModels.ValueModelI;
import org.earthtime.aliquots.AliquotInterface;
import org.earthtime.aliquots.ReduxAliquotInterface;
import org.earthtime.dataDictionaries.SampleAnalysisTypesEnum;
import org.earthtime.dialogs.DialogEditor;
import org.earthtime.fractions.ETFractionInterface;
import org.earthtime.samples.SampleInterface;

/**
 *
 * @author James F. Bowring
 */
public class SampleTreeAnalysisMode extends JTree implements SampleTreeI {

    // instance variables
    private SampleInterface sample;
    private SampleTreeChangeI sampleTreeChange;
    private Object lastNodeSelected;
    private static transient boolean suppressSelectionEvent = false;

    /**
     * Creates a new instance of SampleTreeAnalysisMode
     */
    public SampleTreeAnalysisMode() {
        super();
        sample = null;
    }

    /**
     *
     * @param mySample
     */
    public SampleTreeAnalysisMode(SampleInterface mySample) {
        super(new DefaultMutableTreeNode(mySample));
        sample = mySample;

        CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer();
        setCellRenderer(renderer);

        setCellEditor(new CheckBoxNodeEditor(this));
        setEditable(true);

        setLastNodeSelected(null);
    }

    /**
     *
     */
    @Override
    public void buildTree() {
        // oct 2014
        ((DefaultMutableTreeNode) getModel().getRoot()).removeAllChildren();

        DefaultMutableTreeNode aliquotNode;
        this.removeAll();

        // populate tree
        // todo: just walk aliquots now that ths mapping is fixed (may 2015)
        int saveAliquotNum = -1;
        for (int i = 0; i < sample.getFractions().size(); i++) {
            ETFractionInterface tempFraction = sample.getFractions().get(i);
            AliquotInterface tempAliquot;

            if (!tempFraction.isRejected()) {
                if (saveAliquotNum != tempFraction.getAliquotNumber()) {
                    saveAliquotNum = tempFraction.getAliquotNumber();

                    tempAliquot = sample.getAliquotByNumber(saveAliquotNum);
                    aliquotNode = new DefaultMutableTreeNode(tempAliquot);

                    ((DefaultMutableTreeNode) getModel().getRoot()).add(aliquotNode);

                    // get a master vector of active fraction names
                    Vector<String> activeFractionIDs
                            = ((ReduxAliquotInterface) tempAliquot).//
                            getAliquotFractionIDs();

                    // now load the sample date interpretations
                    for (int index = 0; index < tempAliquot.getSampleDateModels().size(); index++) {
                        DefaultMutableTreeNode sampleDateModelNode
                                = new DefaultMutableTreeNode(//
                                        tempAliquot.getSampleDateModels().get(index));

                        aliquotNode.add(sampleDateModelNode);

                        // remove from activefractionIDs any fraction with 0 date
                        Vector<String> zeroFractionDates = new Vector<>();
                        for (int f = 0; f < activeFractionIDs.size(); f++) {
                            try {
                                if (!((SampleDateModel) tempAliquot.getSampleDateModels().get(index)).//
                                        fractionDateIsPositive(((ReduxAliquotInterface) tempAliquot).getAliquotFractionByName(activeFractionIDs.get(f)))) {
                                    zeroFractionDates.add(activeFractionIDs.get(f));
                                }
                            } catch (Exception e) {
                            }
                        }
                        for (int f = 0; f < zeroFractionDates.size(); f++) {
                            activeFractionIDs.remove(zeroFractionDates.get(f));
                        }

                        // only show sample dates with non-zero data
                        if (activeFractionIDs.size() > 0) {
                            // give sample Date interpretation a value for aliquot
                            ((SampleDateModel) tempAliquot.getSampleDateModels().get(index)).//
                                    setAliquot(tempAliquot);
                            // calculate sample age
                            ((SampleDateModel) tempAliquot.getSampleDateModels().get(index)).//
                                    CalculateDateInterpretationForAliquot();

                            PopulateSampleDateModel(
                                    activeFractionIDs,
                                    tempAliquot,
                                    tempAliquot.getSampleDateModels().get(index),
                                    sampleDateModelNode);
                        }

                    }
                }
            }
        }

        int row = getRowCount() - 1;
        while (row >= 1) {
            collapseRow(row);
            row--;
        }

        DefaultMutableTreeNode rootNode = ((DefaultMutableTreeNode) getModel().getRoot());
        for (int i = 0; i < rootNode.getChildCount(); i++) {
            DefaultMutableTreeNode aliquotDateNode = (DefaultMutableTreeNode) rootNode.getChildAt(i);

            try {
                expandPath(new TreePath(((DefaultMutableTreeNode) aliquotDateNode.getChildAt(0)).getPath()));
            } catch (Exception e) {
            }
        }

        getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        //Listen for when the selection changes.
        addTreeSelectionListener(this);
        addMouseListener(this);

        // set sample as default selection
        setSelectionRow(0);

    }

    private void PopulateSampleDateModel(
            Vector<String> activeFractionIDs,
            AliquotInterface aliquot,
            ValueModel SAM,
            DefaultMutableTreeNode SAMnode) {

        DefaultMutableTreeNode sampleDateValue
                = new DefaultMutableTreeNode(//
                        ((SampleDateModel) SAM).ShowCustomDateNode());
        SAMnode.add(sampleDateValue);

        DefaultMutableTreeNode sampleDateMSWD
                = new DefaultMutableTreeNode(//
                        ((SampleDateModel) SAM).ShowCustomMSWDwithN());
        SAMnode.add(sampleDateMSWD);

        if (((SampleDateModel) SAM).getMethodName().contains("LowerIntercept")) {
            SAMnode.add(new DefaultMutableTreeNode("See Upper Intercept Fractions"));
        } else {
            DefaultMutableTreeNode sampleDateFractions
                    = new DefaultMutableTreeNode("Fractions");
            SAMnode.add(sampleDateFractions);

            // create checkbox for each fraction set to whether it is in list     
            for (String fracID : activeFractionIDs) {
                DefaultMutableTreeNode fractionNode = new DefaultMutableTreeNode(fracID);

                fractionNode.setUserObject( //
                        new CheckBoxNode(
                                ((SampleDateModel) SAM).showFractionIdWithDateAndUnct(((ReduxAliquotInterface) aliquot).getAliquotFractionByName(fracID), "Ma"),
                                ((SampleDateModel) SAM).includesFractionByName(fracID),
                                true));
                sampleDateFractions.add(fractionNode);
            }
        }
    }

    /**
     *
     * @param e
     */
    @Override
    public void valueChanged(TreeSelectionEvent e) {
        //Returns the last path element of the selection.
        //This method is useful only when the selection model allows a single selection.
//        if (!suppressSelectionEvent) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) getLastSelectedPathComponent();
            // april 2016
            TreePath lastSelected = getSelectionPath();//e.getOldLeadSelectionPath();//   

            if (node == null) //Nothing is selected.
            {
                return;
            }

            setLastNodeSelected(node);

            //System.out.println(e.getSource());
            Object nodeInfo = node.getUserObject();
            //sampleTreeChange.sampleTreeChangeAnalysisMode(node);
            //  see below setSelectionRow(-1);

            if (nodeInfo instanceof SampleInterface) {
                // System.out.println(((SampleInterface) nodeInfo).getSampleName());
            } else if (nodeInfo instanceof AliquotInterface) {
                //  System.out.println(((AliquotInterface) nodeInfo).getAliquotName());
            } else if (nodeInfo instanceof ValueModel) {
                // System.out.println(((ValueModelI) nodeInfo).getName());
            } else if (nodeInfo instanceof CheckBoxNode) {
                // System.out.println(nodeInfo.toString());
                // required for toggling because it allows re-focus
                setSelectionRow(-1);

            } else {
                // System.out.println(nodeInfo.toString());
            }

            getSampleTreeChange().sampleTreeChangeAnalysisMode(node);

//            // april 2016
//            suppressSelectionEvent = true;
//            setSelectionPath((nodeInfo instanceof CheckBoxNode) ? lastSelected.getParentPath() : e.getNewLeadSelectionPath());
//            suppressSelectionEvent = false;
//        }

    }

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
    @Override
    public String convertValueToText(Object value,
            boolean selected,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus) {

        Object o = ((DefaultMutableTreeNode) value).getUserObject();

        if (o instanceof SampleInterface) {
            return ((SampleInterface) o).getSampleName();
        } else if (o instanceof AliquotInterface) {
            return ((AliquotInterface) o).getAliquotName();
        } else if (o instanceof ValueModel) {
            if (((SampleDateModel) o).isPreferred()) {
                return "PREFERRED: " + ((ValueModelI) o).getName();
            } else {
                // adding spaces provides for extra chars when needed
                return ((ValueModelI) o).getName() + "        ";
            }
        } else if ((o instanceof String) && (((String) o).startsWith("date"))) {
            return //                
                    ((SampleDateModel) ((DefaultMutableTreeNode) ((TreeNode) value).//
                    getParent()).getUserObject()).ShowCustomDateNode();

        } else if ((o instanceof String) && (((String) o).startsWith("MSWD"))) {
            return //                
                    ((SampleDateModel) ((DefaultMutableTreeNode) ((TreeNode) value).//
                    getParent()).getUserObject()).ShowCustomMSWDwithN() + "              ";

        } else {
            return super.convertValueToText(
                    value,
                    selected,
                    expanded,
                    leaf,
                    row,
                    hasFocus);
        }
    }

    /**
     *
     */
    @Override
    public void performLastUserSelection() {
        getSampleTreeChange().sampleTreeChangeAnalysisMode(getLastNodeSelected());
    }

    /**
     *
     */
    @Override
    public void performLastUserSelectionOfSampleDate() {
        if ((((DefaultMutableTreeNode) getLastNodeSelected()).getUserObject() instanceof ValueModel)//
                || (((DefaultMutableTreeNode) getLastNodeSelected()).getUserObject() instanceof String)) {// i.e. "Fraction
            getSampleTreeChange().sampleTreeChangeAnalysisMode(getLastNodeSelected());
        }

        if (((DefaultMutableTreeNode) getLastNodeSelected()).getUserObject() instanceof CheckBoxNode) {
            DefaultMutableTreeNode parentNode
                    = (DefaultMutableTreeNode) ((TreeNode) getLastNodeSelected()).getParent();
            DefaultMutableTreeNode sampleAgeNode
                    = (DefaultMutableTreeNode) parentNode.getParent();
            getSampleTreeChange().sampleTreeChangeAnalysisMode(sampleAgeNode);
        }
    }

    // Provide for adding sample date interpretations
    /**
     *
     * @param e
     */
    @Override
    public void mousePressed(MouseEvent e) {
        int selRow = getRowForLocation(e.getX(), e.getY());
        TreePath selPath = getPathForLocation(e.getX(), e.getY());

        if (selRow != -1) {
            final DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
            final Object nodeInfo = node.getUserObject();
            if (!e.isPopupTrigger() && (e.getButton() == MouseEvent.BUTTON1)) {
            } else if ((e.isPopupTrigger()) || (e.getButton() == MouseEvent.BUTTON3)) {
                setSelectionPath(selPath);
                if (nodeInfo instanceof AliquotInterface) {

                    DialogEditor myEditor
                            = new SampleDateInterpretationChooserDialog(
                                    null,
                                    true,
                                    ((AliquotInterface) nodeInfo).determineUnusedSampleDateModels());

                    myEditor.setSize(340, 395);
                    JDialog.setDefaultLookAndFeelDecorated(true);

                    myEditor.setVisible(true);

                    // get a master vector of active fraction names
                    Vector<String> activeFractionIDs = ((ReduxAliquotInterface) nodeInfo).//
                            getAliquotFractionIDs();

                    if (((SampleDateInterpretationChooserDialog) myEditor).getSelectedModels().size() > 0) {
                        DefaultMutableTreeNode sampleDateModelNode = null;

                        ArrayList<Integer> tempNewNodes = new ArrayList<>();
                        for (ValueModel selectedSAM : ((SampleDateInterpretationChooserDialog) myEditor).getSelectedModels()) {

                            // remove from activefractionIDs any fraction with 0 date
                            Vector<String> zeroFractionDates = new Vector<>();
                            for (String activeFractionID : activeFractionIDs) {
                                if (!((SampleDateModel) selectedSAM).fractionDateIsPositive(sample.getSampleFractionByName(activeFractionID))) {
                                    zeroFractionDates.add(activeFractionID);
                                }
                            }
                            zeroFractionDates.stream().forEach((zeroFractionDate) -> {
                                activeFractionIDs.remove(zeroFractionDate);
                            });

                            // use next two lines to pre-select all fractions
                            ((SampleDateModel) selectedSAM).setIncludedFractionIDsVector(activeFractionIDs);
                            // feb 2013 add in SampleAnalysisTypesEnum  for transition to logbased calcs starting with LAICPMS from raw
                            //nov 2013 added try 
                            try {
                                ((SampleDateModel) selectedSAM).setSampleAnalysisType(SampleAnalysisTypesEnum.valueOf(sample.getSampleAnalysisType().trim()));
                            } catch (Exception eSampleType) {
                            }
                            ((SampleDateModel) selectedSAM).CalculateDateInterpretationForAliquot();

                            if (activeFractionIDs.size() > 0) {
                                ((AliquotInterface) nodeInfo).getSampleDateModels().add(selectedSAM);

                                // fix up tree
                                sampleDateModelNode = new DefaultMutableTreeNode(selectedSAM);

                                PopulateSampleDateModel(activeFractionIDs,
                                        ((AliquotInterface) nodeInfo),
                                        selectedSAM,
                                        sampleDateModelNode);

                                node.add(sampleDateModelNode);

                                // save node indexes of inserted nodes
                                tempNewNodes.add(node.getChildCount() - 1);

                                // oct 2010 added to make new weighted means automatically selected vs not selected
                                if (((SampleDateModel) selectedSAM).getMethodName().startsWith("WM")) {
                                    String aliquotFlags = sample.getSampleDateInterpretationGUISettings().getWeightedMeanOptions().//
                                            get(selectedSAM.getName());
                                    try {
                                        aliquotFlags = setAliquotFlag(aliquotFlags, ((ReduxAliquotInterface) nodeInfo).getAliquotNumber() - 1, "1");
                                    } catch (Exception e_aliquotFlags) {
                                    }
                                    sample.getSampleDateInterpretationGUISettings().getWeightedMeanOptions().//
                                            put(selectedSAM.getName(), aliquotFlags);

                                    // now need to refresh panel
                                    getSampleTreeChange().sampleTreeChangeAnalysisMode(sampleDateModelNode);
                                }
                            }
                        }

                        SampleInterface.updateAndSaveSampleDateModelsByAliquot(sample);

                        int[] newNodes = new int[tempNewNodes.size()];
                        for (int i = 0; i < tempNewNodes.size(); i++) {
                            newNodes[i] = tempNewNodes.get(i);
                        }

                        ((DefaultTreeModel) getModel()).nodesWereInserted(
                                node, newNodes);//new int[]{node.getChildCount() - 1});

                        // collapse all and expand new date
                        int row = getRowCount() - 1;
                        while (row >= 1) {
                            collapseRow(row);
                            row--;
                        }
                        try {
                            expandPath(new TreePath(sampleDateModelNode.getPath()));
                        } catch (Exception eNoWM) {
                            System.out.println("SampleTreeAnalysisMode line 462 = no WeightedMean available");
                        }

                        getSampleTreeChange().sampleTreeChangeAnalysisMode(node);
                    }

                } else if (nodeInfo instanceof ValueModel) {
                    //Create the popup menu.
                    JPopupMenu popup = new JPopupMenu();

                    JMenuItem menuItem = new JMenuItem("Set as Preferred Sample Date Interpretation");
                    menuItem.addActionListener((ActionEvent arg0) -> {
                        DefaultMutableTreeNode parentNode
                                = (DefaultMutableTreeNode) node.getParent();
                        Object parentNodeInfo = parentNode.getUserObject();
                        ((AliquotInterface) parentNodeInfo).setPreferredSampleDateModel((ValueModel) nodeInfo);
                        SampleInterface.updateAndSaveSampleDateModelsByAliquot(sample);

                        // fix tree
                        ((DefaultTreeModel) getModel()).nodeChanged(node);
                        getSampleTreeChange().sampleTreeChangeAnalysisMode(node);
                    });
                    popup.add(menuItem);
                    menuItem = new JMenuItem("Delete Sample Date Interpretation");
                    menuItem.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent arg0) {
                            // delete sample age from aliquot
                            DefaultMutableTreeNode aliquotNode
                                    = (DefaultMutableTreeNode) ((DefaultMutableTreeNode) node).getParent();
                            Object aliquotNodeInfo = aliquotNode.getUserObject();

                            // remove from aliquot and save sample
                            // first check for special case of lower-upper intercept
                            DefaultMutableTreeNode otherInterceptNode = null;
                            Object otherInterceptNodeInfo = null;
                            int[] indicesOfIntercepts = new int[2];
                            DefaultMutableTreeNode[] nodesOfIntercepts = new DefaultMutableTreeNode[2];

                            // May 2010 rework logic for when upper is preferred (i.e. intercepts in either order)
                            if (((SampleDateModel) nodeInfo).getMethodName().equalsIgnoreCase("LowerIntercept")//
                                    || ((SampleDateModel) nodeInfo).getMethodName().equalsIgnoreCase("UpperIntercept")) {
                                DefaultMutableTreeNode previousInterceptNode = node.getPreviousSibling();
                                DefaultMutableTreeNode nextInterceptNode = node.getNextSibling();

                                if (previousInterceptNode != null) {
                                    otherInterceptNode = node.getPreviousSibling();
                                    otherInterceptNodeInfo = otherInterceptNode.getUserObject();
                                    indicesOfIntercepts[1] = aliquotNode.getIndex(node);
                                    nodesOfIntercepts[1] = node;
                                    indicesOfIntercepts[0] = indicesOfIntercepts[1] - 1;
                                    nodesOfIntercepts[0] = node.getPreviousSibling();

                                } else {
                                    try {
                                        otherInterceptNode = nextInterceptNode;
                                        otherInterceptNodeInfo = otherInterceptNode.getUserObject();
                                        indicesOfIntercepts[0] = aliquotNode.getIndex(node);
                                        nodesOfIntercepts[0] = node;
                                        indicesOfIntercepts[1] = indicesOfIntercepts[0] + 1;
                                        nodesOfIntercepts[1] = node.getNextSibling();
                                    } catch (Exception e) {
                                    }
                                }
                            }

                            if (otherInterceptNodeInfo != null) {
                                // this is the special case where the two intercept nodes were removed
                                ((AliquotInterface) aliquotNodeInfo).getSampleDateModels().remove(nodeInfo);
                                ((AliquotInterface) aliquotNodeInfo).getSampleDateModels().remove((ValueModel) otherInterceptNodeInfo);
                                SampleInterface.updateAndSaveSampleDateModelsByAliquot(sample);

                                // fix up tree
                                aliquotNode.remove(nodesOfIntercepts[0]);
                                aliquotNode.remove(nodesOfIntercepts[1]);
                                ((DefaultTreeModel) getModel()).nodesWereRemoved(
                                        aliquotNode,
                                        indicesOfIntercepts,
                                        nodesOfIntercepts);
                            } else {

                                ((AliquotInterface) aliquotNodeInfo).getSampleDateModels().remove((ValueModel) nodeInfo);
                                SampleInterface.updateAndSaveSampleDateModelsByAliquot(sample);

                                // fix up tree
                                int indexOfNode = aliquotNode.getIndex(node);
                                aliquotNode.remove(node);
                                ((DefaultTreeModel) getModel()).nodesWereRemoved(
                                        aliquotNode,
                                        new int[]{indexOfNode},
                                        new Object[]{node});
                            }

                            if (((SampleDateModel) nodeInfo).getMethodName().startsWith("WM")) {
                                String aliquotFlags = sample.getSampleDateInterpretationGUISettings().getWeightedMeanOptions().//
                                        get(((ValueModelI) nodeInfo).getName());
                                aliquotFlags = setAliquotFlag(aliquotFlags, ((ReduxAliquotInterface) aliquotNodeInfo).getAliquotNumber() - 1, "0");
                                sample.getSampleDateInterpretationGUISettings().getWeightedMeanOptions().//
                                        put(((ValueModelI) nodeInfo).getName(), aliquotFlags);
                            }

                            getSampleTreeChange().sampleTreeChangeAnalysisMode(aliquotNode);
                        }
                    });
                    popup.add(menuItem);

                    popup.show(e.getComponent(),
                            e.getX(), e.getY());

                } else if (nodeInfo instanceof String) {
                    System.out.println("STRING HIT");
                    if (((String) nodeInfo).equalsIgnoreCase("Fractions")) {

                        //Create the popup menu.
                        JPopupMenu popup = new JPopupMenu();
                        JMenuItem menuItem = new JMenuItem("Select None");
                        menuItem.addActionListener(new ActionListener() {

                            @Override
                            public void actionPerformed(ActionEvent arg0) {
                                DefaultMutableTreeNode sampleDateNode
                                        = (DefaultMutableTreeNode) ((DefaultMutableTreeNode) node).getParent();
                                Object SampleDateNodeInfo = sampleDateNode.getUserObject();
                                ((SampleDateModel) SampleDateNodeInfo).//
                                        setIncludedFractionIDsVector(new Vector<String>());

                                SampleInterface.updateAndSaveSampleDateModelsByAliquot(sample);

                                // fix tree
                                for (int c = 0; c < node.getChildCount(); c++) {
                                    ((CheckBoxNode) ((DefaultMutableTreeNode) node.//
                                            getChildAt(c)).getUserObject()).setSelected(false);
                                    ((DefaultTreeModel) getModel()).nodeChanged(node.getChildAt(c));
                                }
                                ((DefaultTreeModel) getModel()).nodeChanged(node);

                                ((SampleDateModel) SampleDateNodeInfo).//
                                        CalculateDateInterpretationForAliquot();

                                getSampleTreeChange().sampleTreeChangeAnalysisMode(sampleDateNode);
                            }
                        });

                        popup.add(menuItem);

                        menuItem = new JMenuItem("Select All");
                        menuItem.addActionListener(new ActionListener() {

                            @Override
                            public void actionPerformed(ActionEvent arg0) {
                                DefaultMutableTreeNode sampleDateNode
                                        = //
                                        (DefaultMutableTreeNode) node.getParent();
                                DefaultMutableTreeNode aliquotNode
                                        = //
                                        (DefaultMutableTreeNode) sampleDateNode.getParent();

                                Object SampleDateNodeInfo = sampleDateNode.getUserObject();
                                Object AliquotNodeInfo = aliquotNode.getUserObject();

                                ((SampleDateModel) SampleDateNodeInfo).//
                                        setIncludedFractionIDsVector(//
                                                ((ReduxAliquotInterface) AliquotNodeInfo).getAliquotFractionIDs());

                                SampleInterface.updateAndSaveSampleDateModelsByAliquot(sample);

                                // fix tree
                                for (int c = 0; c < node.getChildCount(); c++) {
                                    ((CheckBoxNode) ((DefaultMutableTreeNode) node.//
                                            getChildAt(c)).getUserObject()).setSelected(true);
                                    ((DefaultTreeModel) getModel()).nodeChanged(node.getChildAt(c));
                                }
                                ((DefaultTreeModel) getModel()).nodeChanged(node);

                                ((SampleDateModel) SampleDateNodeInfo).//
                                        CalculateDateInterpretationForAliquot();

                                getSampleTreeChange().sampleTreeChangeAnalysisMode(sampleDateNode);

                            }
                        });

                        popup.add(menuItem);

                        popup.show(e.getComponent(),
                                e.getX(), e.getY());
                    }
                }

            }
        } else {
            // do nothing
        }
    }

    /**
     * Used to set weighted means chooser array
     *
     * @param flags
     * @param position
     * @param value
     * @return
     */
    private String setAliquotFlag(String flags, int position, String value) {
        // set position to value or add to end
        // april 2016 pad flags if necessary
        if (position > flags.length() - 1) {
            for (int i = flags.length() - 1; i < position; i++) {
                flags += "0";
            }
        }

        return flags.substring(0, position) + value + flags.substring(position + 1);

    }

    /**
     *
     * @param arg0
     */
    @Override
    public void mouseReleased(MouseEvent arg0) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     *
     * @param arg0
     */
    @Override
    public void mouseEntered(MouseEvent arg0) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     *
     * @param arg0
     */
    @Override
    public void mouseExited(MouseEvent arg0) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     *
     * @param arg0
     */
    @Override
    public void mouseClicked(MouseEvent arg0) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * @return the sampleTreeChange
     */
    @Override
    public SampleTreeChangeI getSampleTreeChange() {
        return sampleTreeChange;
    }

    /**
     * @param sampleTreeChange the sampleTreeChange to set
     */
    @Override
    public void setSampleTreeChange(SampleTreeChangeI sampleTreeChange) {
        this.sampleTreeChange = sampleTreeChange;
    }

    /**
     * @return the lastNodeSelected
     */
    public Object getLastNodeSelected() {
        return lastNodeSelected;
    }

    /**
     * @param lastNodeSelected the lastNodeSelected to set
     */
    public void setLastNodeSelected(Object lastNodeSelected) {
        this.lastNodeSelected = lastNodeSelected;
    }
}
